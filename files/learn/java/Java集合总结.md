####1、集合

    1、Set 存放的是对象的引用，没有重复的对象。
    
        ①：HashSet：按照哈希算法来存取集合中的对象，存取速度比较快。
        
        ②：TreeSet：TreeSet类实现了SortedSet接口，能够对集合中的对象进行排序。
        
        ③：LinkedHashSet：具有HashMap的查询速度，且内部使用链表维护元素顺序，因此遍历时返回的是插入次序。
        
    2、List 以线性方式储存，可以存放重复的数据。
    
        ①：ArrayList 代表长度可以改变的数组，可以对元素进行随机的访问，但插入与删除元素的速度比较慢。
        
        ②：LinkedList 采用链表数据结构，插入和删除速度快，访问速度慢。
        
    3、Map 存放的是键值对
    
        ①：HashMap 基于散列表的实现，插入和查询键值对的开销是固定的。
        
        ②：LinkedHashMap 使用链表维护内部次序，且遍历时取得的键值对是其插入次序。
        
        ③：TreeMap 基于红黑树数据结构，遍历取得的数据是经过内部排序的，同时也是唯一一个带有subMap方法的Map。
        
        ④：WeakHashMap：弱键Map，map使用的对象可以被释放。
        
        ⑤：IdentifyHashMap：使用==代替equals对键作比较的HashMap。 