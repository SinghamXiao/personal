
####HashMap的实现原理


    具体的原理分析可以参考一下两篇文章，有透彻的分析！
    
    参考资料：
    
        1. https://www.jianshu.com/p/17177c12f849 [JDK8中的HashMap实现原理及源码分析]
        
        2. https://tech.meituan.com/java-hashmap.html [Java 8系列之重新认识HashMap]
        
    先介绍几个重要的概念：
    
        //一个桶的树化阈值
        //当桶中元素个数超过这个值时，需要使用红黑树节点替换链表节点
        //这个值必须为 8，要不然频繁转换效率也不高
        static final int TREEIFY_THRESHOLD = 8;
        
        //一个树的链表还原阈值
        //当扩容时，桶中元素个数小于这个值，就会把树形的桶元素 还原（切分）为链表结构
        //这个值应该比上面那个小，至少为 6，避免频繁转换
        static final int UNTREEIFY_THRESHOLD = 6;
        
        //哈希表的最小树形化容量
        //当哈希表中的容量大于这个值时，表中的桶才能进行树形化
        //否则桶内元素太多时会扩容，而不是树形化
        //为了避免进行扩容、树形化选择的冲突，这个值不能小于 4 * TREEIFY_THRESHOLD
        static final int MIN_TREEIFY_CAPACITY = 64;
    
    1、首先针对很多文章中的纰漏语句：如果一个桶中的元素个数超过 TREEIFY_THRESHOLD(默认是 8 )，就使用红黑树来替换链表。作进一步解释！
    
    纰漏见如下图：
    
        插入图片！！！
    
    图片中红色标记的地方个人理解是不够严谨的！！！数据插入HashMap的时候，如果当前桶中的元素个数 > 8(TREEIFY_THRESHOLD)时，则会进行桶的树形化处理（见代码片段1：treeifyBin()）。
    
    注意这里只是进行桶的树形化处理，并不是把桶（如果是链表结构）直接转换为红黑树，这里面是有条件的！！！具体规则如下：
    
        条件1. 如果当前桶数组为null或者桶数组的长度 < 64(MIN_TREEIFY_CAPACITY)，则进行扩容处理（见代码片段2：resize()）；
        
        条件2. 当不满足条件1的时候则将桶中的元素转换成红黑树！！！稍后再详细讨论转换过程。
    
    2、再来分析下HashMap扩容机制的实现：
    
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
    
        final void treeifyBin(Node<K,V>[] tab, int hash) {
            int n, index; Node<K,V> e;
            if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
                resize();
            else if ((e = tab[index = (n - 1) & hash]) != null) {
                TreeNode<K,V> hd = null, tl = null;
                do {
                    TreeNode<K,V> p = replacementTreeNode(e, null);
                    if (tl == null)
                        hd = p;
                    else {
                        p.prev = tl;
                        tl.next = p;
                    }
                    tl = p;
                } while ((e = e.next) != null);
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
                                if ((e.hash & oldCap) == 0) {
                                    if (loTail == null)
                                        loHead = e;
                                    else
                                        loTail.next = e;
                                    loTail = e;
                                }
                                else {
                                    if (hiTail == null)
                                        hiHead = e;
                                    else
                                        hiTail.next = e;
                                    hiTail = e;
                                }
                            } while ((e = next) != null);
                            if (loTail != null) {
                                loTail.next = null;
                                newTab[j] = loHead;
                            }
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

    
    
    谢谢！欢迎批评指正！！！