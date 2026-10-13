package com.handbook.capstone.catalog;

import com.handbook.capstone.shared.Money;

/** [Builder] Products have many optional attributes; the builder validates them together. */
public record Product(String sku, String name, Category category, Money price, int maxPerOrder, boolean organic) {

    public static Builder builder(String sku) {
        return new Builder(sku);
    }

    public static final class Builder {
        private final String sku;
        private String name;
        private Category category;
        private Money price;
        private int maxPerOrder = 10;
        private boolean organic;

        private Builder(String sku) {
            this.sku = sku;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Builder price(Money price) {
            this.price = price;
            return this;
        }

        public Builder maxPerOrder(int max) {
            this.maxPerOrder = max;
            return this;
        }

        public Builder organic() {
            this.organic = true;
            return this;
        }

        public Product build() {
            if (name == null || category == null || price == null || price.paise() <= 0) {
                throw new IllegalStateException(sku + ": name, category and a positive price are required");
            }
            return new Product(sku, name, category, price, maxPerOrder, organic);
        }
    }
}
