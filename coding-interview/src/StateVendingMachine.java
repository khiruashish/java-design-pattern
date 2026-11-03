import java.util.ArrayList;
import java.util.List;

/** Problem: a vending machine whose behaviour depends on its state: Idle -> HasMoney -> (Dispense) -> Idle, or SoldOut. */
public class StateVendingMachine {

    /** Each state decides what an action means and returns the NEXT state (immutable states, no if/else ladders). */
    sealed interface State permits Idle, HasMoney, SoldOut {
        default State insert(Machine m, int cents) { m.say("cannot insert in " + this); return this; }
        default State select(Machine m) { m.say("cannot select in " + this); return this; }
        default State refund(Machine m) { m.say("nothing to refund"); return this; }
    }

    record Idle() implements State {
        public State insert(Machine m, int cents) { m.say("credit " + cents); return new HasMoney(cents); }
    }

    record HasMoney(int credit) implements State {
        public State insert(Machine m, int cents) { m.say("credit " + (credit + cents)); return new HasMoney(credit + cents); }
        public State select(Machine m) {
            if (credit < m.price) { m.say("need " + (m.price - credit) + " more"); return this; }
            m.stock--;
            m.say("dispensed, change " + (credit - m.price));
            return m.stock == 0 ? new SoldOut() : new Idle();
        }
        public State refund(Machine m) { m.say("refunded " + credit); return new Idle(); }
    }

    record SoldOut() implements State {
        public State insert(Machine m, int cents) { m.say("sold out, returned " + cents); return this; }
    }

    static final class Machine {
        final int price;
        int stock;
        private State state = new Idle();
        final List<String> log = new ArrayList<>();

        Machine(int price, int stock) { this.price = price; this.stock = stock; }

        void insert(int cents) { state = state.insert(this, cents); }
        void select() { state = state.select(this); }
        void refund() { state = state.refund(this); }
        void say(String s) { log.add(String.format("%-22s %s", state, s)); }
        State state() { return state; }
    }

    public static void main(String[] args) {
        Machine m = new Machine(150, 2);
        m.select();            // nothing inserted
        m.insert(100);
        m.select();            // not enough
        m.insert(100);
        m.select();            // dispense, change 50
        m.insert(50);
        m.refund();
        m.insert(200);
        m.select();            // last item -> SoldOut
        m.insert(100);
        m.log.forEach(System.out::println);
        System.out.println("final state: " + m.state());
        check(m.state() instanceof SoldOut && m.stock == 0, "sold out after 2 sales");
        check(m.log.contains(String.format("%-22s %s", new HasMoney(200), "dispensed, change 50")), "change computed");
        System.out.println("PASS");
    }

    static void check(boolean ok, String what) { if (!ok) { System.out.println("FAILED: " + what); System.exit(1); } }
}
