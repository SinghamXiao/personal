
####1、一些基本概念

    1. 不可变对象是该对象在创建后它的哈希值不会被改变，如String。
    
    2. 由于位运算直接对内存数据进行操作，不需要转成十进制，因此处理速度非常快。
    
####2、HashMap的实现原理


    具体的原理分析可以参考一下两篇文章，有透彻的分析！本文主要是针对文中的纰漏作进一步解释！
    
    参考资料：
    
        1. https://www.jianshu.com/p/17177c12f849 [JDK8中的HashMap实现原理及源码分析]
        
        2. https://tech.meituan.com/java-hashmap.html [Java 8系列之重新认识HashMap]
        
    纰漏见如下图：
    
    