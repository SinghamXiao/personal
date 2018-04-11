/*
package main.java.test;

import com.derbysoft.common.util.collect.Lists;
import org.junit.Test;

import java.util.List;

*/
/*
 * JTB 最近新功能开发的时候无意当中写了一个栈溢出的问题！！！
 *
 * 详见下面的两个测试
 *
 * 主要原因是com.alibaba.fastjson这个包里面的方法在生成json串的时候优先根据类的getXXX()方法解析相应的字段的值
 *
 *     public Object get(Object javaObject) throws IllegalAccessException, InvocationTargetException {
        return method != null
                ? method.invoke(javaObject)
                : field.get(javaObject);
       }
 *
 * *//*


public class 问题汇总 {

    @Test
    public void test1() {
        List<TestModel1> list = Lists.newArrayList();
        list.add(new TestModel1("test1"));
        list.add(new TestModel1("test2"));
        com.alibaba.fastjson.JSON.toJSON(list); // 注意这个方法的使用！！！
    }

    @Test
    public void test2() {
        List<TestModel2> list = Lists.newArrayList();
        list.add(new TestModel2("test1"));
        list.add(new TestModel2("test2"));
        com.alibaba.fastjson.JSON.toJSON(list); // 注意这个方法的使用！！！
    }

*/
/*  结果：
        java.lang.StackOverflowError
        at java.util.HashMap.<init>(HashMap.java:457)
        at java.util.HashMap.<init>(HashMap.java:468)
        at java.util.LinkedHashMap.<init>(LinkedHashMap.java:359)
        at com.alibaba.fastjson.serializer.JavaBeanSerializer.getFieldValuesMap(JavaBeanSerializer.java:588)
        at com.alibaba.fastjson.JSON.toJSON(JSON.java:930)
        at com.alibaba.fastjson.JSON.toJSON(JSON.java:852)
        at com.alibaba.fastjson.JSON.toJSON(JSON.java:932)
        at com.alibaba.fastjson.JSON.toJSON(JSON.java:852)
        at com.alibaba.fastjson.JSON.toJSON(JSON.java:932)
        at com.alibaba.fastjson.JSON.toJSON(JSON.java:852)
        at com.alibaba.fastjson.JSON.toJSON(JSON.java:932)
        at com.alibaba.fastjson.JSON.toJSON(JSON.java:852)
        at com.alibaba.fastjson.JSON.toJSON(JSON.java:932)
        at com.alibaba.fastjson.JSON.toJSON(JSON.java:852)
        at com.alibaba.fastjson.JSON.toJSON(JSON.java:932)
        ..................................................
        ..................................................
        ..................................................
*//*


    private class TestModel1 {

        private String name;

        public TestModel1(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        // 注意这个地方
        public TestModel1 getReturnThis() {
            // 生成json串的时候这个方法会被调用，尽管没有returnThis字段
            return this;
        }

        // 注意这个地方
        public TestModel1 returnThis() {
            return this;
        }

    }

    private class TestModel2 {

        private String name;

        public TestModel2(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        // 注意这个地方
        public String key() {
            return name + "key";
        }

        // 注意这个地方
        public String getKey() {
            // 生成json串的时候这个方法会被调用，尽管没有key字段
            return name + "key";
        }
    }

}*/
