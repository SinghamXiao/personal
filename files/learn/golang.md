golang向channel发送数据处理，分三种情况：

	1. 有 goroutine 阻塞在 channel 上，此时 hchan.buf 为空：直接将数据发送给该 goroutine。

	2. 当前 hchan.buf 还有可用空间：将数据放到 buffer 里面。

	3. 当前 hchan.buf 已满：阻塞当前 goroutine。	

缓冲channel的调度：
    
    写数据：
    
        1、协程goroutine-1往channel写数据，buffer为空，读队列为空，直接写入buffer并返回。
        
        2、协程goroutine-1往channel写数据，buffer为空，读队列非空（协程goroutine-2阻塞在读队列），协程goroutine-1将数据交给协程goroutine-2
        
            并唤醒协程goroutine-2等待调度，协程goroutine-1返回，调度协程goroutine-2取得数据并返回。
           
        3、协程goroutine-1往channel写数据，buffer非空未满，直接写入buffer并返回。
        
        4、协程goroutine-1往channel写数据，buffer满，加入写队列，阻塞当前协程goroutine-1，等待有其他协程goroutine-n从channel读数据。
    
    读数据：
        
        1、协程goroutine-2从channel读数据，buffer非空，写队列非空（协程goroutine-1阻塞在写队列），唤醒协程goroutine-1等待调度，
        
            协程goroutine-2从buffer读取数据并返回，调度协程goroutine-1将数据写入buffer并返回。
        
        2、协程goroutine-2从channel读数据，buffer非空，写队列空，从buffer读取数据并返回。
        
        3、重复步骤2直至buffer空。
        
            3.1、若写队列为空，协程goroutine-2加入读队列，阻塞当前协程goroutine-2，等待有其他协程goroutine-n往channel写数据。
            
            3.2、若写队列非空（协程goroutine-1阻塞在写队列），协程goroutine-2唤醒协程goroutine-1等待调度，调度协程goroutine-1直接将数据
                
                发送给协程goroutine-2并返回，协程goroutine-2收到数据并返回。