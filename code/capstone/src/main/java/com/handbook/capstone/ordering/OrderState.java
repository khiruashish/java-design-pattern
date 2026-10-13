package com.handbook.capstone.ordering;

/**
 * [State] Each state knows which actions are legal and what comes next. Illegal transitions
 * (delivering a cancelled order) are impossible by construction.
 */
public enum OrderState {

    CONFIRMED {
        @Override
        public OrderState on(String action) {
            return switch (action) {
                case "pack" -> PACKED;
                case "cancel" -> CANCELLED;
                default -> reject(action);
            };
        }
    },
    PACKED {
        @Override
        public OrderState on(String action) {
            return switch (action) {
                case "dispatch" -> OUT_FOR_DELIVERY;
                case "cancel" -> CANCELLED;
                default -> reject(action);
            };
        }
    },
    OUT_FOR_DELIVERY {
        @Override
        public OrderState on(String action) {
            return switch (action) {
                case "deliver" -> DELIVERED;
                default -> reject(action); // too late to cancel: the rider is on the way
            };
        }
    },
    DELIVERED {
        @Override
        public OrderState on(String action) {
            return reject(action);
        }
    },
    CANCELLED {
        @Override
        public OrderState on(String action) {
            return reject(action);
        }
    };

    public abstract OrderState on(String action);

    OrderState reject(String action) {
        throw new IllegalStateException("cannot '" + action + "' an order that is " + this);
    }
}
