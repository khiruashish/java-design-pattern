package com.handbook.microservices.sharding.banking;

import com.handbook.microservices.support.Db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Hash sharding: each account lives in exactly one of N databases, chosen from its id. */
public final class ShardedLedger {

    private final List<DataSource> shards = new ArrayList<>();

    public ShardedLedger(int shardCount) {
        for (int i = 0; i < shardCount; i++) {
            shards.add(Db.inMemory("ledger_shard" + i,
                    "CREATE TABLE account (id VARCHAR PRIMARY KEY, balance_paise BIGINT CHECK (balance_paise >= 0))"));
        }
    }

    public int shardOf(String accountId) { // (1)!
        return Math.floorMod(accountId.hashCode(), shards.size());
    }

    public void open(String accountId, long paise) {
        exec(shards.get(shardOf(accountId)), c -> update(c, "INSERT INTO account VALUES (?, ?)", accountId, paise));
    }

    public long balance(String accountId) {
        try (Connection c = shards.get(shardOf(accountId)).getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT balance_paise FROM account WHERE id = ?")) {
            ps.setString(1, accountId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Same shard: one local ACID transaction. Different shards: refuse, a saga is needed. */
    public String transfer(String from, String to, long paise) {
        if (shardOf(from) != shardOf(to)) { // (2)!
            return "cross-shard (" + shardOf(from) + "->" + shardOf(to) + "): start a transfer saga instead";
        }
        exec(shards.get(shardOf(from)), c -> {
            update(c, "UPDATE account SET balance_paise = balance_paise - ? WHERE id = ?", paise, from);
            update(c, "UPDATE account SET balance_paise = balance_paise + ? WHERE id = ?", paise, to);
        });
        return "local transaction on shard " + shardOf(from);
    }

    private interface Work { void run(Connection c) throws SQLException; }

    private static void exec(DataSource ds, Work work) {
        try (Connection c = ds.getConnection()) {
            c.setAutoCommit(false);
            try {
                work.run(c);
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw new IllegalStateException(e);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void update(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            ps.executeUpdate();
        }
    }
}
