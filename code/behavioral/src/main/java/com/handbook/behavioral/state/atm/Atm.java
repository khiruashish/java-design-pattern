package com.handbook.behavioral.state.atm;

/** Context + states for an ATM session. States are inner classes so they can reach the context. */
public class Atm {

    private abstract class State { // inner (non-static) so states can use the ATM's fields
        void insertCard(String card) { say("card already inserted"); }
        void enterPin(String pin) { say("insert a card first"); }
        void withdraw(int amount) { say("enter your PIN first"); }
        void eject() { say("no card to eject"); }
    }

    private final String correctPin = "4321";
    private long balance = 12_000;
    private State state = new NoCard();

    public void insertCard(String card) { state.insertCard(card); }

    public void enterPin(String pin) { state.enterPin(pin); }

    public void withdraw(int amount) { state.withdraw(amount); }

    public void eject() { state.eject(); }

    private void say(String message) {
        System.out.println("  ATM: " + message);
    }

    private final class NoCard extends State {
        @Override
        public void withdraw(int amount) { say("insert a card first"); }

        @Override
        public void insertCard(String card) {
            say("card " + card + " read, enter PIN");
            state = new CardInserted();
        }
    }

    private final class CardInserted extends State {
        private int attemptsLeft = 3; // (1)!

        @Override
        public void enterPin(String pin) {
            if (pin.equals(correctPin)) {
                say("PIN ok");
                state = new Authenticated();
            } else if (--attemptsLeft > 0) {
                say("wrong PIN, " + attemptsLeft + " attempt(s) left");
            } else {
                say("card retained after 3 wrong PINs, please contact your branch");
                state = new NoCard();
            }
        }

        @Override
        public void eject() { say("card returned"); state = new NoCard(); }
    }

    private final class Authenticated extends State {
        @Override
        public void withdraw(int amount) {
            if (amount % 100 != 0) {
                say("amount must be a multiple of 100");
            } else if (amount > balance) {
                say("insufficient balance");
            } else {
                balance -= amount;
                say("dispensed INR " + amount + ", balance INR " + balance);
            }
        }

        @Override
        public void enterPin(String pin) { say("already authenticated"); }

        @Override
        public void eject() { say("card returned, thank you"); state = new NoCard(); }
    }
}
