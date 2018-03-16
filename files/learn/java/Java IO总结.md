####1、Java IO总结

    在整个Java.io包中最重要的就是5个类和一个接口。5个类指的是File、OutputStream、InputStream、Writer、Reader；一个接口指的是Serializable。
    
    Java I/O主要包括如下几个层次，包含三个部分：
    
        1.流式部分――IO的主体部分；
        
        2.非流式部分――主要包含一些辅助流式部分的类，如：File类、RandomAccessFile类和FileDescriptor等类；
        
        3.其他类--文件读取部分的与安全相关的类，如：SerializablePermission类，以及与本地操作系统相关的文件系统的类，如：FileSystem类和Win32FileSystem类和WinNTFileSystem类。
        
        主要的类如下：
        
            1. File（文件特征与管理）：用于文件或者目录的描述信息，例如生成新目录，修改文件名，删除文件，判断文件所在路径等。
            
            2. InputStream（二进制格式操作）：抽象类，基于字节的输入操作，是所有输入流的父类。定义了所有输入流都具有的共同特征。
            
            3. OutputStream（二进制格式操作）：抽象类。基于字节的输出操作。是所有输出流的父类。定义了所有输出流都具有的共同特征。
            
            Java中字符是采用Unicode标准，一个字符是16位，即一个字符使用两个字节来表示。为此，JAVA中引入了处理字符的流。
            
            4. Reader（文件格式操作）：抽象类，基于字符的输入操作。
            
            5. Writer（文件格式操作）：抽象类，基于字符的输出操作。
            
            6. RandomAccessFile（随机文件操作）：它的功能丰富，可以从文件的任意位置进行存取（输入输出）操作。

    一、按I/O类型来总体分类：
    
        1. Memory:
         
            1）从/向内存数组读写数据: CharArrayReader、 CharArrayWriter、ByteArrayInputStream、ByteArrayOutputStream
            
            2）从/向内存字符串读写数据 StringReader、StringWriter、StringBufferInputStream
        
        2. Pipe管道  实现管道的输入和输出（进程间通信）: PipedReader、PipedWriter、PipedInputStream、PipedOutputStream
        
        3. File 文件流。对文件进行读、写操作 ：FileReader、FileWriter、FileInputStream、FileOutputStream
        
        4. ObjectSerialization 对象输入、输出 ：ObjectInputStream、ObjectOutputStream
        
        5. DataConversion数据流 按基本数据类型读、写（处理的数据是Java的基本类型（如布尔型，字节，整数和浮点数））：DataInputStream、DataOutputStream
        
        6. Printing 包含方便的打印方法 ：PrintWriter、PrintStream
        
        7. Buffering缓冲  在读入或写出时，对数据进行缓存，以减少I/O的次数：BufferedReader、BufferedWriter、BufferedInputStream、BufferedOutputStream
        
        8. Filtering 滤流，在数据进行读或写时进行过滤：FilterReader、FilterWriter、FilterInputStream、FilterOutputStream过
        
        9. Concatenation合并输入 把多个输入流连接成一个输入流 ：SequenceInputStream 
        
        10. Counting计数  在读入数据时对行记数 ：LineNumberReader、LineNumberInputStream
        
        11. Peeking Ahead 通过缓存机制，进行预读 ：PushbackReader、PushbackInputStream
        
        12. Converting between Bytes and Characters 按照一定的编码/解码标准将字节流转换为字符流，或进行反向转换（Stream到Reader,Writer的转换类）：InputStreamReader、OutputStreamWriter

    二、按数据来源（去向）分类： 
    
        1、File（文件）： FileInputStream, FileOutputStream, FileReader, FileWriter 
        
        2、byte[]：ByteArrayInputStream, ByteArrayOutputStream 
        
        3、Char[]: CharArrayReader, CharArrayWriter 
        
        4、String: StringBufferInputStream, StringReader, StringWriter 
        
        5、网络数据流：InputStream, OutputStream, Reader, Writer 
        
        7. 字节流: InputStream/OutputStream

    参考文献：
    
        1. https://www.cnblogs.com/yyy-blog/p/7003693.html
        
        2. http://blog.csdn.net/baobeisimple/article/details/1713797
        
        3. https://www.ibm.com/developerworks/cn/java/j-lo-javaio/index.html
        
        4. http://ifeve.com/java-io/
