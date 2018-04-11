package com.singham.yuan.ws.test.test;

import java.util.HashMap;

public class Test {

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        System.out.println(loader);
        System.out.println(loader.getParent());
        System.out.println(loader.getParent().getParent());

        StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
        System.out.println();

        System.out.println(tableSizeFor(0));
        System.out.println(tableSizeFor(1));
        System.out.println(tableSizeFor(2));
        System.out.println(tableSizeFor(3));
        System.out.println(tableSizeFor(17));
        System.out.println(tableSizeFor(32));
        System.out.println(tableSizeFor(31));
        System.out.println(tableSizeFor(64));
        System.out.println(tableSizeFor(63));

        HashMap<H, String> hashMap = new HashMap<H, String>(100);
        H a = new H("a");
        hashMap.put(a, "A");
        H b = new H("b");
        hashMap.put(b, "A");
        H c = new H("c");
        hashMap.put(c, "A");
        H d = new H("d");
        hashMap.put(d, "A");
        H e = new H("e");
        hashMap.put(e, "A");
        H f = new H("f");
        hashMap.put(f, "A");
        H g = new H("g");
        hashMap.put(g, "A");
        H h = new H("h");
        hashMap.put(h, "A");
        H i = new H("i");
        hashMap.put(i, "A");
        H j = new H("j");
        hashMap.put(j, "A");

        System.out.println(hashMap.get(a));
        System.out.println(hashMap.get(b));
        System.out.println(hashMap.get(c));
        System.out.println(hashMap.get(d));

        System.out.println("----------");
        System.out.println(4&(16-1));
        System.out.println(4&(32-1));
        System.out.println(4&(64-1));
        System.out.println(4&(128-1));


        HashMap<String, String> map = new HashMap<String, String>();
        System.out.println(map.put("a", "A"));
        System.out.println(map.put("a", "AA"));
        System.out.println(map.put("a", "AB"));
        map.clear();
    }


    static final int MAXIMUM_CAPACITY = 1 << 30;

    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }


}