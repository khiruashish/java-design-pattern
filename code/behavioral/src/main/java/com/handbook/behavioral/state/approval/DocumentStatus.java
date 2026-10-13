package com.handbook.behavioral.state.approval;

/** Challenge solution: an enum where each constant is a state with its own transitions. */
public enum DocumentStatus {

    DRAFT {
        public DocumentStatus submit() { return IN_REVIEW; }
    },
    IN_REVIEW {
        public DocumentStatus approve() { return APPROVED; }
        public DocumentStatus reject() { return DRAFT; } // back to the author
    },
    APPROVED {
        public DocumentStatus publish() { return PUBLISHED; }
    },
    PUBLISHED;

    public DocumentStatus submit() { return illegal("submit"); }

    public DocumentStatus approve() { return illegal("approve"); }

    public DocumentStatus reject() { return illegal("reject"); }

    public DocumentStatus publish() { return illegal("publish"); }

    private DocumentStatus illegal(String action) {
        throw new IllegalStateException("Cannot " + action + ": document is " + this);
    }
}
