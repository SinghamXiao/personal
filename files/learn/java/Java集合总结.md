####1、Java集合总结

    集合
    
        集合类存放于Java.util包中。
        
        集合类型主要有3种：Set(集）、List(列表包含Queue）和Map(映射)。
        
        Collection：Collection是集合的基本接口，List、Set、Queue的最基本的接口。
        
        Iterator：迭代器，可以通过迭代器遍历集合中的数据。
        
        Map：是映射表的基础接口。
    
    1. List 有序集合
    
        Java的List是非常常用的数据类型。List是有序的Collection。Java List一共三个实现类：分别是ArrayList、Vector和LinkedList。
        
        ArrayList：
        
            ArrayList是最常用的List实现类，内部是通过数组实现的，它允许对元素进行快速随机访问。数组的缺点是每个元素之间不能有间隔，当数组大小不满足时需要增加存储能力，就要将已经有数组的数据复制到新的存储空间中。
        
            当从ArrayList的中间位置插入或者删除元素时，需要对数组进行复制、移动、代价比较高。因此，它适合随机查找和遍历，不适合插入和删除。
        
        Vector：
        
            Vector与ArrayList一样，也是通过数组实现的，不同的是它支持线程的同步，即某一时刻只有一个线程能够写Vector，避免多线程同时写而引起的不一致性，但实现同步需要很高的花费，因此，访问它比访问ArrayList慢。
        
        LinkedList：
        
            LinkedList是用链表结构存储数据的，很适合数据的动态插入和删除，随机访问和遍历速度比较慢，但是遍历就很慢，并且不存在get()的操作，不能单个定位。
        
            说白了，ArrayList是顺序存储结构，LinkedList是链表存储结构。另外，它还提供了List接口中没有定义的方法，专门用于操作表头和表尾元素，可以当作堆栈、队列和双向队列使用。
        
        Queue：
        
            常用的Queue有PriorityQueue、ConcurrentLinkedQueue、ArrayBlockingQueue、LinkedBlockingQueue、PriorityBlockingQueue。
        
        说明：
        
            ArrayList在内存不够时默认是扩展50% + 1个，Vector是默认扩展1倍。
        
            Vector属于线程安全级别的，但是大多数情况下不使用Vector，因为线程安全需要更大的系统开销。
        
            一般使用ArrayList和LinkedList比较多。
        
            对于随机访问get和set，ArrayList觉得优于LinkedList，因为LinkedList要移动指针。
        
            对于新增和删除操作add和remove，LinedList比较占优势，因为ArrayList要移动数据。
    
    2. Set 集合
    
        Set集合的几个特点：
        
            Set集合不允许出现重复数据；
            
            允许包含值为null的元素，但最多只能有一个null元素。
           
        HashSet：
        
            HashSet中不能有重复的元素；
            
            HashSet是无序的；
            
            HashSet是基于HashMap实现的。
            
        TreeSet：
                
            TreeSet中不能有重复的元素；
            
            TreeSet具有排序功能，缺省是按照自然排序进行排列；
            
            TreeSet中的元素必须实现Comparable接口并重写compareTo()方法，TreeSet判断元素是否重复 、以及确定元素的顺序靠的都是这个方法；
            
            基于TreeMap实现。
        
        LinkedHashSet：
        
            LinkedHashSet中不能有重复的元素；
            
            按照元素插入的顺序进行迭代，即迭代输出的顺序与插入的顺序保持一致；
            
            LinkedHashSet是基于LinkedHashMap实现的。
    
    3、 Map
    
        Map集合主要有：HashMap，LinkedHashMap，TreeMap
    
        HashMap：
        
            HashMap是无序的散列映射表；
        
            HashMap通过Hash算法来决定存储位置；
        
            底层实现是哈希表。
            
        LinkedHashMap：
        
            LinkedHashMap可以认为是HashMap + LinkedList，即它既使用HashMap操作数据结构，又使用LinkedList维护插入元素的先后顺序。
            
        TreeMap：
        
            适用于按自然顺序或自定义顺序遍历键(key)。
            
            底层是红黑树；
            
            提供compareTo，可以定义排序方法。
    
    总结：
    
    1、Set 存放的是对象的引用，没有重复的对象。
    
        ①：HashSet：按照哈希算法来存取集合中的对象，存取速度比较快。
        
        ②：TreeSet：TreeSet类实现了SortedSet接口，能够对集合中的对象进行排序。
        
        ③：LinkedHashSet：具有HashMap的查询速度，且内部使用链表维护元素顺序，因此遍历时返回的是插入次序。
        
    2、List 以线性方式储存，可以存放重复的数据。
    
        ①：ArrayList 代表长度可以改变的数组，可以对元素进行随机的访问，但插入与删除元素的速度比较慢。
        
        ②：LinkedList 采用链表数据结构，插入和删除速度快，访问速度慢。
        
    3、Map 存放的是键-值对。
    
        ①：HashMap 基于散列表的实现，插入和查询键值对的开销是固定的。
        
        ②：LinkedHashMap 使用链表维护内部次序，且遍历时取得的键值对是其插入次序。
        
        ③：TreeMap 基于红黑树数据结构，遍历取得的数据是经过内部排序的，同时也是唯一一个带有subMap方法的Map。
        
        ④：WeakHashMap：弱键Map，使用的对象可以被释放。
        
        ⑤：IdentifyHashMap：使用==代替equals对键作比较的HashMap。 
        
    参考资料：
        
        1. http://www.runoob.com/java/java-collections.html
        
        2. https://www.cnblogs.com/xiaoxi/p/6089984.html
        
        3. https://segmentfault.com/a/1190000008934023
        
        4. http://tutorials.jenkov.com/java-collections/index.html
        
        5. http://ifeve.com/java-collections/