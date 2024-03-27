package fr.maxlego08.stats.api;

public class GlobalValue {
    private Object value;

    public GlobalValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String asString() {
        return value.toString();
    }

    public double asDouble() {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        throw new IllegalArgumentException("Value cannot be converted to double");
    }

    public int asInt() {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        throw new IllegalArgumentException("Value cannot be converted to int");
    }

    public long asLong() {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        throw new IllegalArgumentException("Value cannot be converted to long");
    }

    public void add(long value) {
        setValue(this.asLong() + value);
    }

    public void add(int value) {
        setValue(this.asInt() + value);
    }
}
