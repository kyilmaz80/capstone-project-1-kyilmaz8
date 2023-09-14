package func;

public interface MathFunction {
    public String getName();
    public int getArgCount();
    public default MathFunction[] getFunctions() {
        //
        return null;
    }
}
