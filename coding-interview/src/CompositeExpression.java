/** Problem: model arithmetic expressions as a tree; evaluate and pretty-print them; add operations without editing nodes. */
public class CompositeExpression {

    /** Composite: leaves (Num, Var) and composites (Add, Mul, Neg) share one type. */
    sealed interface Expr permits Num, Var, Add, Mul, Neg { }
    record Num(double value) implements Expr { }
    record Var(String name) implements Expr { }
    record Add(Expr left, Expr right) implements Expr { }
    record Mul(Expr left, Expr right) implements Expr { }
    record Neg(Expr operand) implements Expr { }

    // Java 21 "visitor": an exhaustive pattern-matching switch. New operation = new method; the compiler checks every node type.
    static double eval(Expr e, java.util.Map<String, Double> env) {
        return switch (e) {
            case Num n -> n.value();
            case Var v -> {
                Double value = env.get(v.name());
                if (value == null) throw new IllegalArgumentException("unbound variable " + v.name());
                yield value;
            }
            case Add a -> eval(a.left(), env) + eval(a.right(), env);
            case Mul m -> eval(m.left(), env) * eval(m.right(), env);
            case Neg n -> -eval(n.operand(), env);
        };
    }

    static String show(Expr e) {
        return switch (e) {
            case Num n -> n.value() == Math.rint(n.value()) ? String.valueOf((long) n.value()) : String.valueOf(n.value());
            case Var v -> v.name();
            case Add a -> "(" + show(a.left()) + " + " + show(a.right()) + ")";
            case Mul m -> show(m.left()) + " * " + show(m.right());
            case Neg n -> "-" + show(n.operand());
        };
    }

    /** A third operation: algebraic simplification (x*1 = x, x+0 = x, 0*x = 0, constant folding). */
    static Expr simplify(Expr e) {
        return switch (e) {
            case Add(Expr l, Expr r) -> {
                Expr a = simplify(l), b = simplify(r);
                if (a instanceof Num(double x) && x == 0) yield b;
                if (b instanceof Num(double y) && y == 0) yield a;
                if (a instanceof Num(double x) && b instanceof Num(double y)) yield new Num(x + y);
                yield new Add(a, b);
            }
            case Mul(Expr l, Expr r) -> {
                Expr a = simplify(l), b = simplify(r);
                if ((a instanceof Num(double x) && x == 0) || (b instanceof Num(double y) && y == 0)) yield new Num(0);
                if (a instanceof Num(double x) && x == 1) yield b;
                if (b instanceof Num(double y) && y == 1) yield a;
                if (a instanceof Num(double x) && b instanceof Num(double y)) yield new Num(x * y);
                yield new Mul(a, b);
            }
            case Neg(Neg(Expr inner)) -> simplify(inner);
            case Neg(Expr inner) -> new Neg(simplify(inner));
            default -> e;
        };
    }

    public static void main(String[] args) {
        // (x + 2) * (3 + -1)  and  (y * 1 + 0) * (2 + 3)
        Expr e1 = new Mul(new Add(new Var("x"), new Num(2)), new Add(new Num(3), new Neg(new Num(1))));
        Expr e2 = new Mul(new Add(new Mul(new Var("y"), new Num(1)), new Num(0)), new Add(new Num(2), new Num(3)));
        var env = java.util.Map.of("x", 4.0, "y", 10.0);

        System.out.println(show(e1) + " = " + eval(e1, env) + "   with x=4");
        System.out.println(show(e2) + " = " + eval(e2, env) + "   with y=10");
        System.out.println("simplify: " + show(e2) + "  ->  " + show(simplify(e2)));
        check(eval(e1, env) == 12.0, "eval e1");
        check(show(simplify(e2)).equals("y * 5"), "simplify");
        check(eval(simplify(e2), env) == eval(e2, env), "simplification preserves meaning");
        try { eval(new Var("z"), env); check(false, "unbound"); }
        catch (IllegalArgumentException ex) { System.out.println(ex.getMessage()); }
        System.out.println("PASS");
    }

    static void check(boolean ok, String what) { if (!ok) { System.out.println("FAILED: " + what); System.exit(1); } }
}
