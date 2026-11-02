package com.handbook.principles.other.dry;

/** VIOLATES DRY: the same email rule is copy-pasted, and the copies have already drifted apart. */
public class SignupBefore {
    public boolean canRegister(String email) {
        return email != null && email.contains("@") && email.indexOf('@') > 0;
    }

    public boolean canInvite(String email) {
        return email != null && email.contains("@");          // drifted: accepts "@nobody"
    }
}
