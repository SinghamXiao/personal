
####深度剖析HashMap的数据存储实现原理（看完必懂篇）

    具体的原理分析可以参考以下两篇文章，有透彻的分析！本文在此基础上加入个人理解和完善部分纰漏！！！
    
    参考资料：
    
        1. https://www.jianshu.com/p/17177c12f849 [JDK8中的HashMap实现原理及源码分析]
        
        2. https://tech.meituan.com/java-hashmap.html [Java 8系列之重新认识HashMap]
        
    1、关键字段：
    
        /**
         * The default initial capacity - MUST be a power of two.
         */
        static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // 2^4
    
        /**
         * The maximum capacity, used if a higher value is implicitly specified
         * by either of the constructors with arguments.
         * MUST be a power of two <= 1<<30.
         */
        static final int MAXIMUM_CAPACITY = 1 << 30; // 2^30
    
        /**
         * The load factor used when none specified in constructor.
         */
        static final float DEFAULT_LOAD_FACTOR = 0.75f;
    
        /**
         * The bin count threshold for using a tree rather than list for a
         * bin.  Bins are converted to trees when adding an element to a
         * bin with at least this many nodes. The value must be greater
         * than 2 and should be at least 8 to mesh with assumptions in
         * tree removal about conversion back to plain bins upon
         * shrinkage.
         *
         * 一个桶的树化阈值
         * 当桶中元素个数超过这个值时，需要使用红黑树节点替换链表节点
         * 这个值必须为 8，要不然频繁转换效率也不高
         */
        static final int TREEIFY_THRESHOLD = 8;
        
        /**
         * The bin count threshold for untreeifying a (split) bin during a
         * resize operation. Should be less than TREEIFY_THRESHOLD, and at
         * most 6 to mesh with shrinkage detection under removal.
         *
         * 一个树的链表还原阈值
         * 当扩容时，桶中元素个数小于这个值，就会把树形的桶元素 还原（切分）为链表结构
         * 这个值应该比上面那个小，至少为 6，避免频繁转换
         */
        static final int UNTREEIFY_THRESHOLD = 6;
        
        /**
         * The smallest table capacity for which bins may be treeified.
         * (Otherwise the table is resized if too many nodes in a bin.)
         * Should be at least 4 * TREEIFY_THRESHOLD to avoid conflicts
         * between resizing and treeification thresholds.
         *
         * 哈希表的最小树形化容量
         * 当哈希表中的容量大于这个值时，表中的桶才能进行树形化
         * 否则桶内元素太多时会扩容，而不是树形化
         * 为了避免进行扩容、树形化选择的冲突，这个值不能小于 4 * TREEIFY_THRESHOLD
         */
        static final int MIN_TREEIFY_CAPACITY = 64;
        
        /* ---------------- Fields -------------- */
    
        /**
         * The table, initialized on first use, and resized as
         * necessary. When allocated, length is always a power of two.
         * (We also tolerate length zero in some operations to allow
         * bootstrapping mechanics that are currently not needed.)
         * 
         * 为了更好表示本文称之为桶数组
         */
        transient Node<K,V>[] table;
    
        /**
         * Holds cached entrySet(). Note that AbstractMap fields are used
         * for keySet() and values().
         */
        transient Set<Map.Entry<K,V>> entrySet;
    
        /**
         * The number of key-value mappings contained in this map.
         */
        transient int size;
    
        /**
         * The number of times this HashMap has been structurally modified
         * Structural modifications are those that change the number of mappings in
         * the HashMap or otherwise modify its internal structure (e.g.,
         * rehash).  This field is used to make iterators on Collection-views of
         * the HashMap fail-fast.  (See ConcurrentModificationException).
         */
        transient int modCount;
    
        /**
         * The next size value at which to resize (capacity * load factor).
         *
         * @serial
         */
        // (The javadoc description is true upon serialization.
        // Additionally, if the table array has not been allocated, this
        // field holds the initial array capacity, or zero signifying
        // DEFAULT_INITIAL_CAPACITY.)
        int threshold;
    
        /**
         * The load factor for the hash table.
         *
         * @serial
         */
        final float loadFactor;
        
        /**
         * Constructs an empty <tt>HashMap</tt> with the specified initial
         * capacity and load factor.
         *
         * @param  initialCapacity the initial capacity
         * @param  loadFactor      the load factor
         * @throws IllegalArgumentException if the initial capacity is negative
         *         or the load factor is nonpositive
         */
        public HashMap(int initialCapacity, float loadFactor) {
            if (initialCapacity < 0)
                throw new IllegalArgumentException("Illegal initial capacity: " +
                                                   initialCapacity);
            if (initialCapacity > MAXIMUM_CAPACITY)
                initialCapacity = MAXIMUM_CAPACITY;
            if (loadFactor <= 0 || Float.isNaN(loadFactor))
                throw new IllegalArgumentException("Illegal load factor: " +
                                                   loadFactor);
            this.loadFactor = loadFactor;
            this.threshold = tableSizeFor(initialCapacity);
        }
    
        /**
         * Constructs an empty <tt>HashMap</tt> with the specified initial
         * capacity and the default load factor (0.75).
         *
         * @param  initialCapacity the initial capacity.
         * @throws IllegalArgumentException if the initial capacity is negative.
         */
        public HashMap(int initialCapacity) {
            this(initialCapacity, DEFAULT_LOAD_FACTOR);
        }
    
        /**
         * Constructs an empty <tt>HashMap</tt> with the default initial capacity
         * (16) and the default load factor (0.75).
         */
        public HashMap() {
            this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
        }
    
        /**
         * Constructs a new <tt>HashMap</tt> with the same mappings as the
         * specified <tt>Map</tt>.  The <tt>HashMap</tt> is created with
         * default load factor (0.75) and an initial capacity sufficient to
         * hold the mappings in the specified <tt>Map</tt>.
         *
         * @param   m the map whose mappings are to be placed in this map
         * @throws  NullPointerException if the specified map is null
         */
        public HashMap(Map<? extends K, ? extends V> m) {
            this.loadFactor = DEFAULT_LOAD_FACTOR;
            putMapEntries(m, false);
        }
    
    2、首先针对很多文章中的纰漏语句：如果一个桶中链表内的元素个数超过 TREEIFY_THRESHOLD(默认是8)，就使用红黑树来替换链表。
     
        // 插入图片1张
        
        图片中红色标记的地方个人理解是不够严谨的！！！数据插入HashMap的时候，如果当前桶中的元素个数 > TREEIFY_THRESHOLD时，则会进行桶的树形化处理（见代码片段1：treeifyBin()）。
        
        注意这里只是进行桶的树形化处理，并不是把桶（如果是链表结构）直接转换为红黑树，这里面是有条件的！！！具体规则如下：
        
            条件1. 如果当前桶数组为null或者桶数组的长度 < MIN_TREEIFY_CAPACITY，则进行扩容处理（见代码片段2：resize()）；
            
            条件2. 当不满足条件1的时候则将桶中链表内的元素转换成红黑树！！！稍后再详细讨论红黑树。
    
    3、再来分析下HashMap扩容机制的实现：
    
        概念：
        
            1. 扩容(resize)就是重新计算容量。当向HashMap对象里不停的添加元素，而HashMap对象内部的桶数组无法装载更多的元素时，HashMap对象就需要扩大桶数组的长度，以便能装入更多的元素。
            
            2. capacity 就是数组的长度/大小，loadFactor 是这个数组填满程度的最大比比例。
            
            3. size表示当前HashMap中已经储存的Node<key,value>的数量，包括桶数组和链表 / 红黑树中的的Node<key,value>。
            
            4. threshold表示扩容的临界值，如果size大于这个值，则必需调用resize()方法进行扩容。
            
            5. 在jdk1.7及以前，threshold = capacity * loadFactor，其中 capacity 为桶数组的长度。
            
                这里需要说明一点，默认负载因子0.75是是对空间和时间(纵向横向)效率的一个平衡选择，建议大家不要修改。
                
                jdk1.8对threshold值进行了改进，通过一系列位移操作算法最后得到一个power of two size的值，见代码片段4。
        
        扩容过程：
        
            1. 使用new Hashap<>()时，新桶数组初始容量设置为默认值DEFAULT_INITIAL_CAPACITY，默认容量下的阈值为DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY。
            
            2. 使用new Hashap<>(int initialCapacity)或new HashMap(int initialCapacity, float loadFactor)时，newCap, newThr均重新计算。
            
            3. 如果使用过程中HashMap中的数据过多，原始容量不够使用，那么需要扩容。扩容是以2^1为单位扩容的， newCap = oldCap << 1和newThr = oldThr << 1。
            
            4. 如果原来的桶数组长度大于最大值MAXIMUM_CAPACITY时，扩容临界值提高到正无穷（Integer.MAX_VALUE），返回原来的数组，也就是系统已经管不了了，随便你怎么玩吧。
            
        正常扩容之后需要将老的桶数组数据重新放到新的桶数组中，同时对每个桶上的链表进行了重排，再介绍重排之前先来看看代码片段5里面的hash()计算：
        
            首先将得到key对应的哈希值：h = key.hashCode()，然后通过hashCode()的高16位异或低16位计算得到最终的key.hash值（(h = key.hashCode()) ^ (h >>> 16)）。
            
                1. 取key的hashcode值：
            
                    ① Object类的hashCode
                      
                        返回对象的经过处理后的内存地址，由于每个对象的内存地址都不一样，所以哈希码也不一样。这个是native方法，取决于JVM的内部设计，一般是某种C地址的偏移。
                    
                    ② String类的hashCode
                    
                        根据String类包含的字符串的内容，根据一种特殊算法返回哈希码，只要字符串的内容相同，返回的哈希码也相同。
                    
                    ③ Integer等包装类
                    
                        返回的哈希码就是Integer对象里所包含的那个整数的数值，例如Integer i1=new Integer(100)，i1.hashCode的值就是100。
                        
                        由此可见，2个一样大小的Integer对象，返回的哈希码也一样。
                    
                    ④ int，char这样的基础类
                    
                        它们不需要hashCode，如果需要存储时，将进行自动装箱操作，计算方法包装类。
                
                2. hashCode()的高16位异或低16位
            
                    在JDK1.8的实现中，优化了高位运算的算法，通过hashCode()的高16位异或低16位实现的：key.hash = (h = k.hashCode()) ^ (h >>> 16)，
                    
                    主要是从速度、功效、质量来考虑的，这么做可以在数组table的length比较小的时候，也能保证考虑到高低Bit都参与到Hash的计算中，同时不会有太大的开销。
            
                3. key.hash & (n - 1) 取模运算
            
                    这个n我们说过是table的长度，那么n-1就是table数组元素应有的下表。这个方法非常巧妙，它通过 key.hash & (table.length - 1) 来得到该对象的保存位，
            
                    而HashMap底层数组的长度总是2的n次方，这是HashMap在速度上的优化。当length总是2的n次方时，key.hash & (table.length - 1) 运算等价于对length取模，也就是key.hash % length，但是&比%具有更高的效率。
                
        链表重排：
        
            1. 如果原桶上只有一个节点，并且该节点不是红黑树节点，那么直接放到新桶原索引key.hash & (table.length - 1)下；
            
            2. 如果原桶上的节点是红黑树节点，那么则对该树进行分割split()；
            
            3. 如果原桶上的节点是一个链表，则进行链表重排算法:
            
                由于桶数组的容量是按2次幂的扩展(指容量扩为原来2倍)，所以，元素的位置要么是在“原索引”，要么是在“原索引 + oldCap”的位置。
                
                所以，只需要看看原来key.hash值新增的那个bit是1还是0就好了，是0的话索引没变，是1的话索引变成“原索引 + oldCap”。
                
                // 插入图片2张
    
    4、HashMap的数据存储实现原理
        
        流程：
        
            1. 根据key计算得到key.hash = (h = k.hashCode()) ^ (h >>> 16)；
            
            2. 根据key.hash计算得到桶数组的索引index = key.hash & (table.length - 1)，这样就找到该key的存放位置了：
            
                ① 如果该位置没有数据，用该数据新生成一个节点保存新数据，返回null；
                
                ② 如果该位置有数据是一个红黑树，那么执行相应的插入 / 更新操作，稍后再详细讨论红黑树；
                
                ③ 如果该位置有数据是一个链表，分两种情况一是该链表没有这个节点，另一个是该链表上有这个节点，注意这里判断的依据是key.hash是否一样：
                
                    如果该链表没有这个节点，那么采用尾插法新增节点保存新数据，返回null；
                    
                    如果该链表已经有这个节点了，那么找到該节点并更新新数据，返回老数据。
        注意：
    
            HashMap的put会返回key的上一次保存的数据，比如：
            
                HashMap<String, String> map = new HashMap<String, String>();
                System.out.println(map.put("a", "A"));  // 打印null
                System.out.println(map.put("a", "AA")); // 打印A
                System.out.println(map.put("a", "AB")); // 打印AA

    5、红黑树
    
        上面的讨论中对于红黑树并没有深入分析，HashMap的数据存储中主要有两种场景用到红黑树的操作：
        
            场景1. 当满足一定条件（条件2，见上文）时，单链表内的数据会转换为红黑树存储（见代码片段2：treeifyBin()）。
            
            场景2. 当HashMap桶结构由链表转换为红黑树后，再往里put数据将变成往红黑树插入 / 更新数据，这和链表又不太一样了。
        
        下面进行逐一详细分析：
        
            场景1:
             
                代码片段2：treeifyBin()树形化函数完成的第一件事是将单链表的Node节点转换为红黑树TreeNode节点，然后将其首位相连，并将桶指向红黑树的头结点。
                
                其实此时所谓的红黑树只是一个双向链表，并不严格意义上的红黑树结构！！！ 因为此时每个节点只有prev和next有值，那么接着就需要对这个双向链表树形化将其塑造成一个标准的红黑树。
                
                这时候用到了函数treeify()，转换过程未完待续。。。。。。
                
            场景2：
            
                这时候用到了函数putTreeVal()，处理过程未完待续。。。。。。
            
    源码片段1：
        
        /**
         * Associates the specified value with the specified key in this map.
         * If the map previously contained a mapping for the key, the old
         * value is replaced.
         *
         * @param key key with which the specified value is to be associated
         * @param value value to be associated with the specified key
         * @return the previous value associated with <tt>key</tt>, or
         *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
         *         (A <tt>null</tt> return can also indicate that the map
         *         previously associated <tt>null</tt> with <tt>key</tt>.)
         */
        public V put(K key, V value) {
            return putVal(hash(key), key, value, false, true);
        }
    
        /**
         * Implements Map.put and related methods
         *
         * @param hash hash for key
         * @param key the key
         * @param value the value to put
         * @param onlyIfAbsent if true, don't change existing value
         * @param evict if false, the table is in creation mode.
         * @return previous value, or null if none
         */
        final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                       boolean evict) {
            Node<K,V>[] tab; Node<K,V> p; int n, i;
            if ((tab = table) == null || (n = tab.length) == 0)
                n = (tab = resize()).length;
            if ((p = tab[i = (n - 1) & hash]) == null)
                tab[i] = newNode(hash, key, value, null);
            else {
                Node<K,V> e; K k;
                if (p.hash == hash &&
                    ((k = p.key) == key || (key != null && key.equals(k))))
                    e = p;
                else if (p instanceof TreeNode)
                    e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
                else {
                    for (int binCount = 0; ; ++binCount) {
                        if ((e = p.next) == null) {
                            p.next = newNode(hash, key, value, null);
                            if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                                treeifyBin(tab, hash);
                            break;
                        }
                        if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k))))
                            break;
                        p = e;
                    }
                }
                if (e != null) { // existing mapping for key
                    V oldValue = e.value;
                    if (!onlyIfAbsent || oldValue == null)
                        e.value = value;
                    afterNodeAccess(e);
                    return oldValue;
                }
            }
            ++modCount;
            if (++size > threshold)
                resize();
            afterNodeInsertion(evict);
            return null;
        }
        
    源码片段2：
        
        //将桶内所有的 链表节点 替换成 红黑树节点
        final void treeifyBin(Node<K,V>[] tab, int hash) {
            int n, index; Node<K,V> e;
            //如果当前哈希表为空，或者哈希表中元素的个数小于进行树形化的阈值(默认为64)，就去新建/扩容
            if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
                resize();
            else if ((e = tab[index = (n - 1) & hash]) != null) {
                // 如果哈希表中的元素个数超过了 树形化阈值，进行树形化
                // e 是哈希表中指定位置桶里的链表节点，从头结点开始
                TreeNode<K,V> hd = null, tl = null; // 红黑树的头、尾节点
                do {
                    // 新建一个红黑树节点，内容和当前链表节点e一致
                    TreeNode<K,V> p = replacementTreeNode(e, null);
                    if (tl == null)// 确定红黑树头节点
                        hd = p;
                    else {
                        p.prev = tl;
                        tl.next = p;
                    }
                    tl = p;
                } while ((e = e.next) != null);
                // 让桶的第一个元素指向新建的红黑树头结点，以后这个桶里的元素就是红黑树而不是链表了
                if ((tab[index] = hd) != null)
                    hd.treeify(tab);
            }
        }
    
    源码片段3：
        
        /**
         * Initializes or doubles table size.  If null, allocates in
         * accord with initial capacity target held in field threshold.
         * Otherwise, because we are using power-of-two expansion, the
         * elements from each bin must either stay at same index, or move
         * with a power of two offset in the new table.
         *
         * @return the table
         */
        final Node<K,V>[] resize() {
            Node<K,V>[] oldTab = table;
            int oldCap = (oldTab == null) ? 0 : oldTab.length;
            int oldThr = threshold;
            int newCap, newThr = 0;
            if (oldCap > 0) {
                if (oldCap >= MAXIMUM_CAPACITY) {
                    threshold = Integer.MAX_VALUE;
                    return oldTab;
                }
                else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                         oldCap >= DEFAULT_INITIAL_CAPACITY)
                    newThr = oldThr << 1; // double threshold
            }
            else if (oldThr > 0) // initial capacity was placed in threshold
                newCap = oldThr;
            else {               // zero initial threshold signifies using defaults
                newCap = DEFAULT_INITIAL_CAPACITY;
                newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
            }
            if (newThr == 0) {
                float ft = (float)newCap * loadFactor;
                newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                          (int)ft : Integer.MAX_VALUE);
            }
            threshold = newThr;
            @SuppressWarnings({"rawtypes","unchecked"})
                Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
            table = newTab;
            if (oldTab != null) {
                for (int j = 0; j < oldCap; ++j) {
                    Node<K,V> e;
                    if ((e = oldTab[j]) != null) {
                        oldTab[j] = null;
                        if (e.next == null)
                            newTab[e.hash & (newCap - 1)] = e;
                        else if (e instanceof TreeNode)
                            ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                        else { // preserve order
                            Node<K,V> loHead = null, loTail = null;
                            Node<K,V> hiHead = null, hiTail = null;
                            Node<K,V> next;
                            do {
                                next = e.next;
                                // 原索引
                                if ((e.hash & oldCap) == 0) {
                                    if (loTail == null)
                                        loHead = e;
                                    else
                                        loTail.next = e;
                                    loTail = e;
                                }
                                // 原索引 + oldCap
                                else {
                                    if (hiTail == null)
                                        hiHead = e;
                                    else
                                        hiTail.next = e;
                                    hiTail = e;
                                }
                            } while ((e = next) != null);
                            // 原索引放到桶数组里
                            if (loTail != null) {
                                loTail.next = null;
                                newTab[j] = loHead;
                            }
                            // 原索引 + oldCap放到桶数组里
                            if (hiTail != null) {
                                hiTail.next = null;
                                newTab[j + oldCap] = hiHead;
                            }
                        }
                    }
                }
            }
            return newTab;
        }

    源码片段4：
    
        /**
         * Returns a power of two size for the given target capacity.
         */
        static final int tableSizeFor(int cap) {
            int n = cap - 1;
            n |= n >>> 1;
            n |= n >>> 2;
            n |= n >>> 4;
            n |= n >>> 8;
            n |= n >>> 16;
            return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
        }
    
    源码片段5：
    
        /**
         * Computes key.hashCode() and spreads (XORs) higher bits of hash
         * to lower.  Because the table uses power-of-two masking, sets of
         * hashes that vary only in bits above the current mask will
         * always collide. (Among known examples are sets of Float keys
         * holding consecutive whole numbers in small tables.)  So we
         * apply a transform that spreads the impact of higher bits
         * downward. There is a tradeoff between speed, utility, and
         * quality of bit-spreading. Because many common sets of hashes
         * are already reasonably distributed (so don't benefit from
         * spreading), and because we use trees to handle large sets of
         * collisions in bins, we just XOR some shifted bits in the
         * cheapest possible way to reduce systematic lossage, as well as
         * to incorporate impact of the highest bits that would otherwise
         * never be used in index calculations because of table bounds.
         */
        static final int hash(Object key) {
            int h;
            return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
        }
    
    谢谢！欢迎批评指正！！！