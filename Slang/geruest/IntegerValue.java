/**
 * Created by Anton on 19.06.2015.
 */
public class IntegerValue implements Value {

    private int value;

    public IntegerValue(int value) {
        this.value = value;
    }

    @Override
    public String toS() {
        return "" + value;
    }

    @Override
    public int toI() {
        return value;
    }
}
