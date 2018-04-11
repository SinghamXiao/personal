package main.java.test;

import java.util.HashMap;
import java.util.List;

public class H {

    private String data;

    public H(String data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        return 10;
    }

    public static void main(String[] args) {
        HashMap<String, List<String>> map = new HashMap<String, List<String>>();
        System.out.println(map.values().size());
    }

}