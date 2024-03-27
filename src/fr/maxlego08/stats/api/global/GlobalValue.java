package fr.maxlego08.stats.api.global;

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
        if (value instanceof Double) return (double) value;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(this.asString());
        } catch (Exception ignored) {
        }
        throw new IllegalArgumentException("Value cannot be converted to double");
    }

    public int asInt() {
        if (value instanceof Integer) return (int) value;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(this.asString());
        } catch (Exception ignored) {
        }
        throw new IllegalArgumentException("Value cannot be converted to int");
    }

    public long asLong() {
        if (value instanceof Long) return (long) value;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(this.asString());
        } catch (Exception ignored) {
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
