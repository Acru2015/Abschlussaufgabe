/**
 * Created by Anton on 19.06.2015.
 */
public class StringValue implements Value {

    private String value;

    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public String toS() {
        return value;
    }

    @Override
    public int toI() {
        return Integer.parseInt(value);
    }
}
