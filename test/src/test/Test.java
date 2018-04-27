package test;

import main.java.H;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {

    String f;

    private AtomicInteger num = new AtomicInteger(0);

    public Test() {
        super();
    }

    public int testAdd() {
        return num.incrementAndGet();
    }

    public int testSub() {
        return num.decrementAndGet();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.gc();
    }

    public static void main(String[] args) {
        final Test test = new Test();
        for (int i = 0; i < args.length + 10; i++) {
            new Thread() {
                public void run() {
                    System.out.println(new Date().getTime() + Thread.currentThread().getName() + ": " + test.testAdd());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(new Date().getTime() + Thread.currentThread().getName() + ": " + test.testSub());
                }
            }.start();

        }


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        System.out.println(4 & (16 - 1));
        System.out.println(4 & (32 - 1));
        System.out.println(4 & (64 - 1));
        System.out.println(4 & (128 - 1));


        HashMap<String, String> map = new HashMap<String, String>();
        System.out.println(map.put("a", "A"));
        System.out.println(map.put("a", "AA"));
        System.out.println(map.put("a", "AB"));
        map.clear();

        String str1 = new StringBuffer("go").append("to").toString();
        System.out.println(str1.intern() == str1);

        String str2 = new StringBuffer("go").append("to").toString();
        System.out.println(str2.intern() == str2);

        StringBuilder builder = new StringBuilder();

        System.out.println("Test " + a.toString());

        try {
            Class.forName("main.java.H");
        } catch (ClassNotFoundException e1) {
            System.out.println(e1.getCause());
        }
        Class<H> hClass = H.class;

        try {
            H instance = hClass.newInstance();
            instance.getClass();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
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