package com.handbook.structural.proxy;

import com.handbook.structural.proxy.banking.CoreAccountService;
import com.handbook.structural.proxy.banking.SecuredAccountService;
import com.handbook.structural.proxy.dynamic.LoggingProxy;
import com.handbook.structural.proxy.remote.InventoryClientProxy;
import com.handbook.structural.proxy.remote.InventoryServer;
import com.handbook.structural.proxy.streaming.LazyPoster;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProxyTest {

    @Test
    void lazyPosterDoesNotLoadForTitleOnly() {
        LazyPoster p = new LazyPoster("X");
        assertThat(p.title()).isEqualTo("X");
        assertThat(p.render()).contains("X");
    }

    @Test
    void protectionProxyEnforcesLimits() {
        var teller = new SecuredAccountService(new CoreAccountService(), new SecuredAccountService.User("t", Set.of("TELLER")));
        assertThat(teller.withdraw("SB-1001", 50_000)).contains("withdrew");
        assertThatThrownBy(() -> teller.withdraw("SB-1001", 50_001)).isInstanceOf(SecurityException.class);
    }

    @Test
    void remoteProxyMarshalsCalls() {
        InventoryServer server = new InventoryServer();
        var client = new InventoryClientProxy(server::handle);
        assertThat(client.availableUnits("BLR-1", "RICE-5KG")).isEqualTo(120);
        assertThat(server.calls()).isEqualTo(1);
        var broken = new InventoryClientProxy(r -> "ERR|db down");
        assertThatThrownBy(() -> broken.availableUnits("a", "b")).hasMessageContaining("db down");
    }

    @Test
    void dynamicProxyLogsAndRethrowsOriginalException() {
        List<String> log = new ArrayList<>();
        var secured = new SecuredAccountService(new CoreAccountService(), new SecuredAccountService.User("x", Set.of()));
        var proxied = LoggingProxy.wrap((com.handbook.structural.proxy.banking.AccountService) secured,
                com.handbook.structural.proxy.banking.AccountService.class, log::add);
        assertThatThrownBy(() -> proxied.balance("SB-1001")).isInstanceOf(SecurityException.class);
        assertThat(log).singleElement().asString().contains("threw java.lang.SecurityException");
    }
}
