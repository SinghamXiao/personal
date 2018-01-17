**golang channel的使用以及调度原理**
    
    为了并发的goroutines之间的通讯，golang使用了管道channel。
    
        可以通过一个goroutines向channel发送数据，然后从另一个goroutine接收它。
    
        通常我们会使用make来创建channel   -----   make(chan valType, [size])。
    
            写入 c <- data
        
            读取 data ：= <-c
    
    Golang的channel分为缓冲和非缓冲的两种。主要区别：缓冲chanel是同步的，非缓冲channel是非同步的。
    
        举个例子：
             
            c1 := make(chan int)        // 无缓冲： c1 <- 1，当前协程阻塞。
    
            c2 := make(chan int, 1)     // 有缓冲： c2 <- 1，当前协程不会阻塞，c2 <- 2，此时1没有被取走，当前协程才阻塞。
    
    注意：
    
        1、给一个nil channel发送数据，造成永远阻塞。
        
        2、从一个nil channel接收数据，造成永远阻塞。
        
        3、给一个已经关闭的channel发送数据，引起panic。
        
        4、从一个已经关闭的channel接收数据，立即返回一个零值（false : bool, 0 : int, 0.0 : float, "" : string,
         
            nil : pointer, function, interface, slice, channel, map）。
            
        5、channel关闭多次会引起panic，channel不能close大于1次。
        
        6、可以用两个返回值(valeu, b := <-c)来捕获channel是否关闭，b取值false或true。
        
    缓冲channel的调度：
        
        一、写数据：
        
            1、协程1往channel写数据，buffer为空，读队列为空，直接写入buffer并返回。
            
            2、协程1往channel写数据，buffer为空，读队列非空（协程2阻塞在读队列），协程1将数据交给协程2并唤醒协程2等待调度，
            
                协程1返回，调度协程2取得数据并返回。
               
            3、协程1往channel写数据，buffer非空未满，直接写入buffer并返回。
            
            4、协程1往channel写数据，buffer满，加入写队列，阻塞当前协程1，等待有其他协程n从channel读数据。
        
        二、读数据：
            
            1、协程2从channel读数据，buffer非空，写队列非空（协程1阻塞在写队列），唤醒协程1等待调度，协程2从buffer读取数据并返回，
            
                调度协程1将数据写入buffer并返回。
            
            2、协程2从channel读数据，buffer非空，写队列空，从buffer读取数据并返回。
            
            3、重复步骤2直至buffer空。
            
                3.1、若写队列为空，加入读队列，阻塞当前协程2，等待有其他协程n往channel写数据。
                
                3.2、若写队列非空（协程1阻塞在写队列），协程2从写队列取出数据并唤醒协程1等待调度，协程2携带数据返回，调度协程1返回。
                    
    非缓冲channel的调度：
        
        一、写数据：
        
            1、协程1往channel写数据，读队列为空，加入写队列，阻塞当前协程1，等待有其他协程n从channel读数据。
            
            2、协程1往channel写数据，读队列非空（协程2阻塞在读队列），协程1将数据交给协程2并唤醒协程2等待调度，协程1返回，调度协程2取
            
                得数据并返回。
               
        二、读数据：
            
            1、协程2从channel读数据，写队列非空（协程1阻塞在写队列），协程2从写队列取出数据并唤醒协程1等待调度，协程2携带数据返回，调
            
                度协程1返回。
            
            2、协程2从channel读数据，写队列为空，加入读队列，阻塞当前协程2，等待有其他协程n往channel写数据。
       	
    参考资料：blog.csdn.net/nobugtodebug/article/details/45396507