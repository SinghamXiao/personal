package main.java;

public class H {

    private String data;

    public H(String data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        return 10;
    }

    @Override
    public String toString() {
        return "" + this;
    }
}