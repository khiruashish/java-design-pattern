package com.handbook.microservices.acl.erp;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * The anti-corruption layer between our catalog and a decades-old ERP. The ERP's cryptic fields,
 * zero-padded codes, comma decimals and status flags stop here; the rest of our code only sees
 * our own Product model.
 */
public final class ErpProductTranslator {

    public enum Unit { KILOGRAM, PIECE, LITRE }

    /** Our domain model: clean names, types and invariants. */
    public record Product(String sku, String name, Unit unit, long pricePaise, boolean active) {
    }

    /** The ERP's interface, as it is. We don't control it. */
    public interface LegacyErp {
        Map<String, String> readMaterial(String materialNumber);
    }

    private final LegacyErp erp;

    public ErpProductTranslator(LegacyErp erp) {
        this.erp = erp;
    }

    public Optional<Product> product(String sku) {
        Map<String, String> raw = erp.readMaterial(String.format("%018d", Long.parseLong(sku.replace("SKU-", "")))); // (1)!
        if (raw.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new Product(
                "SKU-" + Long.parseLong(raw.get("MAT_NO")),       // strip the zero padding
                titleCase(raw.get("MAT_DESC").trim()),
                unit(raw.get("BASE_UOM")),                       // (2)!
                new BigDecimal(raw.get("PRICE").replace(".", "").replace(',', '.'))
                        .movePointRight(2).longValueExact(),     // (3)!
                !"X".equals(raw.get("DEL_FLAG"))));              // (4)!
    }

    private static Unit unit(String erpCode) {
        return switch (erpCode) {
            case "KGM" -> Unit.KILOGRAM;
            case "ST", "PCE" -> Unit.PIECE;
            case "LTR" -> Unit.LITRE;
            default -> throw new IllegalArgumentException("unmapped ERP unit " + erpCode);
        };
    }

    private static String titleCase(String upper) {
        StringBuilder sb = new StringBuilder();
        for (String w : upper.toLowerCase().split(" ")) {
            sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(' ');
        }
        return sb.toString().trim();
    }
}
