## Linux-Ubuntu系统使用总结  ##

**1、系统版本ubuntu16.04-x86_64**
	
	http://releases.ubuntu.com/16.04/

	sudo adduser 用户名 sudo

	ubuntu 终端显示路径太长
	
	sudo vim ~/.bashrc

	这个文件记录了用户终端配置

	找到

	if [ "$color_prompt " = yes ]; then
		PS1 ='${debian_chroot:+($debian_chroot)}\[\033[01;32m\]\u@\h\[\033[00m\]:\[\033[01;34m\]\W \[\033[00m\]\$ '
	else
		PS1 ='${debian_chroot:+($debian_chroot)}\u@\h:\W \$ '

	将蓝色的w由小写改成大写，可以表示只显示当前目录名称
	
**2、配置IP、DNS及网络代理**

![](ip.png)
![](proxy.png)

	sudo apt-get update
	
	sudo apt-get upgrade

**3、安装openssh-server**

	ps -ef | grep ssh

	sudo apt-get install openssh-server
	
	sudo /etc/init.d/ssh start
	
	sudo /etc/init.d/ssh restart

	# 配置免密码ssh登陆

	cd ~/.ssh/ # 若没有该目录，请先执行一次ssh localhost
 
	ssh-keygen -t rsa # 会有提示，都按回车就可以
	
	cat id_rsa.pub >> authorized_keys # 加入授权
	
	使用ssh localhost试试能否直接登录
	
	同样的方式将生成的公钥id_rsa.pub都追加到authorized_keys文件中，将authorized_keys文件放到各个主机的.ssh目录下即可

	# 安装SFTP服务
    
    第01步，如果还没有安装OpenSSH服务器，先安装它。

    sudo apt-get install openssh-server

    第02步，为SFTP访问创建用户组，便于管理权限。

    sudo addgroup sftp-users

    第03步，创建SFTP用户，并配置相应权限。这里第二行的意思是将alice从所有其他用户组中移除并加入到sftp-users组，并且关闭其Shell访问。如果想深入了解usermod命令，可以使用以下"man usermod"命令查看帮助文档。

    sudo adduser alice

    sudo usermod -G sftp-users -s /bin/false alice

    第04步，创建SSH用户组，并把管理员加入到该组（注意usermod中的-a参数的意思是不从其他用户组用移除）。

    sudo addgroup ssh-users

    sudo usermod -a -G ssh-users admin

    第05步，准备“监狱”的根目录及共享目录。这里解释一下，“监狱”的根目录必须满足以下要求：所有者为root，其他任何用户都不能拥有写入权限。因此，为了让SFTP用户能够上传文件，还必须在“监狱”根目录下再创建一个普通用户能够写入的共享文件目录。为了便于管理员通过SFTP管理上传的文件，我把这个共享文件目录配置为：由admin所有，允许sftp-users读写。这样，管理员和SFTP用户组成员就都能读写这个目录了。

    sudo mkdir /home/sftp_root

    sudo mkdir /home/sftp_root/shared

    sudo chown admin:sftp-users /home/sftp_root/shared

    sudo chmod 770 /home/sftp_root/shared

    第06步，修改SSH配置文件。

    sudo nano /etc/ssh/sshd_config

    在sshd_config文件的最后，添加以下内容：
    AllowGroups ssh-users sftp-users
    Match Group sftp-users
    ChrootDirectory /home/sftp_root
    AllowTcpForwarding no
    X11Forwarding no
    ForceCommand internal-sftp
    这些内容的意思是：
    只允许ssh-uers及sftp-users通过SSH访问系统；
    针对sftp-users用户，额外增加一些设置：将“/home/sftp_root”设置为该组用户的系统根目录（因此它们将不能访问该目录之外的其他系统文件）；禁止TCP Forwarding和X11 Forwarding；强制该组用户仅仅使用SFTP。

    如果需要进一步了解细节，可以使用“man sshd_config”命令。这样设置之后，SSH用户组可以访问SSH，并且不受其他限制；而SFTP用户组仅能使用SFTP进行访问，而且被关进监狱目录。

    第07步，重启系统以便使新配置生效。

    sudo reboot now

**4、Windows远程Ubuntu桌面**

	sudo apt-get install xrdp
	
	sudo apt-get install xubuntu-desktop
	
	echo "xfce4-session" >~/.xsession

	sudo vim /etc/xrdp/startwm.sh

	在. /etc/X11/Xsession 前一行插入
	xfce4-session
	
	sudo service xrdp restart

	xfce图形界面远程连接有个优点，就是每次重新建立远程链接的话就会新建一个xfce的图形
	桌面，可供多用户同时对一台电脑进行远程控制，而上一次远程链接打开的图形桌面关掉后
	就没了，只剩下应用程序还在后台运行所以，如何将xfce的这个优点消除掉啊？能让它像
	windows一样老老实实多次远程控制只作用于一个桌面。步骤如下：

	sudo vim /etc/xrdp/xrdp.ini

	[xrdp1]
	name=Reconnect
	lib=libvnc.so
	username=ask
	password=ask
	ip=127.0.0.1
	port=-1
	port 是-1,每次都是一个新的会话。可以用netstat -l |grep 591 查看
	
	建议改成
	
	[xrdp1]
	name=sesman-Xvnc
	lib=libvnc.so
	username=ask
	password=ask
	ip=127.0.0.1
	#port=-1
	port=ask-1或者port=5910
	
	这样就可以是同一个会话了，而且多用户同时登陆的时候可以看得到每一个用户的操作
	
	Tab补全：
	办法一：编辑~/.config/xfce4/xfconf/xfce-perchannel-xml/xfce4-keyboard-
	shortcuts.xml文件，在里面内容里找到<property name="&lt;Super&gt;Tab" 
	type="string" value="switch_window_key"/>，把它用<property 
	name="&lt;Super&gt;Tab" type="empty"/>这句替换，重新启动系统后即可解决问题。
	
	办法二：该方法不需要你去重新启动系统，在远程桌面中设置，路径是：打开菜单——设置——
	窗口管理器，或在终端中输入xfwm4-settings打开也行（xfwm4就是xfce4 window 
	manger的缩写），选择键盘，可以看到窗口快捷键中动作一列有“切换同一应用程序的窗
	口”选项，将该选项的快捷键清除后关闭窗口即可解决问题。

**5、安装subversion/git**

	sudo apt-get remove --purge subversion libsvn1 libapr1 libaprutil1 libserf-1-1

	sudo apt-get install subversion 

	subversion libsvn1 libapr1 libaprutil1 libserf-1-1

	sudo apt-cache show subversion | grep '^Version:'

	sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys ***********

	echo subversion hold | sudo dpkg --set-selections
	
	echo libsvn1 hold | sudo dpkg --set-selections
	
	echo libapr1 hold | sudo dpkg --set-selections
	
	echo libaprutil1 hold | sudo dpkg --set-selections
	
	echo libserf-1-1 hold | sudo dpkg --set-selections
	
	sudo dpkg --get-selections | grep hold

	sudo apt-get install git

**6、安装vim**

	sudo apt-get install vim

**7、安装samba**

	sudo apt-get install samba
	
	sudo touch /etc/libuser.conf
	
	sudo apt-get install gksu
	
	sudo apt-get install system-config-samba
	
	sudo system-config-samba
	
	sudo vim /etc/samba/smb.conf
	
	[share]
        path = /home/zte/share
	    available = yes
        browsealbe = yes
        writeable = yes
	;   browseable = yes
        valid users = zte
	
	sudo touch /etc/samba/smbpasswd
	
	sudo smbpasswd -a zte
	
	sudo /etc/init.d/samba restart

**8、安装JDK**

	sudo tar -zxvf jdk-8u25-linux-x64.tar.gz -C /usr/lib/jvm/
	
	# 加入环境变量，见环境变量设置
	# 替换系统默认JDK

	sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/jdk1.8.0_25/bin/java 300  
	
	sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/jdk1.8.0_25/bin/javac 300  
	
	sudo update-alternatives --install /usr/bin/jar jar /usr/lib/jvm/jdk1.8.0_25/bin/jar 300   
	
	sudo update-alternatives --install /usr/bin/javah javah /usr/lib/jvm/jdk1.8.0_25/bin/javah 300   
	
	sudo update-alternatives --install /usr/bin/javap javap /usr/lib/jvm/jdk1.8.0_25/bin/javap 300 
	
	sudo update-alternatives --config java

	http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

**9、安装Docker**

	sudo apt-get install -y docker.io 

	sudo ln -sf /usr/bin/docker.io /usr/local/bin/docker
	sudo sed -i `$acomplete -F _docker docker` /etc/bash_completion.d/docker.io

	sudo apt-get install apt-transport-https  
	# Add the Docker repository key to your local keychain  
	sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 36A1D7869245C8950F966E92D8576A8BA88D21E9  
	# Add the Docker repository to your apt sources list.  
	sudo sh -c "echo deb https://get.docker.io/ubuntu docker main > /etc/apt/sources.list.d/docker.list"  
	# update your sources list  
	sudo apt-get update  
	   
	# 之后通过下面命令来安装最新版本的docker：  
	sudo apt-get install -y lxc-docker  
	# 以后更新则：  
	sudo apt-get install -y lxc-docker  
	sudo usermod -a -G docker $USER
	
	ln -sf /usr/bin/docker /usr/local/bin/docker
	
	# 查看docker服务状态
	service docker status
	
	# 启动Docker
	sudo su
	docker -d &
	
	# docker开机启动
	sudo update-rc.d docker defaults
	sudo update-rc.d docker enable
	
	# 添加一个新的docker用户组
	sudo groupadd docker
	
	# 添加当前用户到docker用户组里，注意这里的zte为ubuntu登录用户名
	sudo gpasswd -a zte docker
	# 重启Docker后台监护进程
	sudo service docker restart
	# 重启之后，尝试一下，是否生效
	docker version
	#若还未生效，则系统重启，则生效
	sudo reboot
	
	# 使用openVZ的模板来创建docker镜像
	http://openvz.org/Download/templates/precreated
	cat ubuntu-15.04-x86-minimal.tar.gz | docker import - ubuntu:15.04

	docker常用命令：
	删除所有已经停止的容器
	docker rm $(docker ps -a -q)

	删除所有镜像
	docker rmi $(docker images -q)

	删除所有未打 dangling 标签的镜像
	docker rmi $(docker images -q -f dangling=true)
	
	docker images [-a]

	docker ps -a

	docker rm containerID

	docker rmi imageID

    docker run -e MYSQL_ROOT_PASSWORD=root -v /opt/docker/mysql/data:/var/lib/mysql -v /opt/docker/mysql/mysqld.cnf:/etc/mysql/mysql.conf.d/mysqld.cnf -p 127.0.0.1:3306:3306 --restart=always --name mysql -d mysql:latest
	docker exec -it  mysql1 bash
	
    docker run -e MYSQL_ROOT_PASSWORD=root -v /opt/docker/mariadb/data:/var/lib/mysql -v /opt/docker/mariadb/my.cnf:/etc/mysql/my.cnf -p 127.0.0.1:3307:3306  --restart=always --name mariadb -d mariadb:5.5.57
    
	docker export <CONTAINER ID> > /home/export.tar

	如何给docker设置http代理:

	$ sudo mkdir /etc/systemd/system/docker.service.d

	$ sudo vi /etc/systemd/system/docker.service.d/http-proxy.conf

	[Service]
	Environment="HTTP_PROXY=http://10.67.14.8:80/"
	Environment="NO_PROXY=localhost,127.0.0.0/8,10.62.49.160:5000"
	
	If you have internal Docker registries that you need to contact without proxying you can specify them via the NO_PROXY environment variable:
	
	Environment="HTTP_PROXY=http://10.67.14.8:80/"
	Environment="NO_PROXY=localhost,127.0.0.0/8,docker-registry.somecorporation.com"
	
	Flush changes:
	
	$ sudo systemctl daemon-reload
	
	Verify that the configuration has been loaded:
	
	$ sudo systemctl show docker --property Environment
	Environment=HTTP_PROXY=http://10.67.14.8:80/ NO_PROXY=localhost,127.0.0.0/8,10.62.49.160:5000
	
	Restart Docker:
	
	$ sudo systemctl restart docker

	$ sudo su
	$ sudo echo "DOCKER_OPTS=\"$DOCKER_OPTS --registry mirror=http://786573aa.m.daocloud.io\"" >> /etc/default/docker

	Docker私有仓库Registry的搭建:

	$ docker pull registry:latest

	$ sudo docker run -d -v /opt/registry:/var/lib/registry -p [本机IP]:5000:5000 --privileged=true --restart=always --name registry docker.io/registry:latest

	Centos 7:
	yum -y install docker-1.10.3
	
	docker run -d -v /opt/registry:/var/lib/registry -p 10.62.49.167:5000:5000 --privileged=true --restart=always --name registry docker.io/registry:latest

	sed -i "s/OPTIONS.*/OPTIONS='--selinux-enabled --insecure-registry 172.30.0.0\/16 --insecure-registry 10.62.49.167:5000'/" /etc/sysconfig/docker

	sed -i "s/#*\s*ADD_REGISTRY=.*/ADD_REGISTRY='--add-registry 10.62.49.167:5000'/" /etc/sysconfig/docker

	sed -i "s/#*\s*BLOCK_REGISTRY=.*/BLOCK_REGISTRY='--block-registry docker.io'/" /etc/sysconfig/docker

	systemctl restart docker

	chkconfig docker on
	
	部署私有Docker Registry：
	
	1、确保本机可以从官网下载registry:latest 。

	docker search registry // 搜索镜像

	docker pull registry // 拉取镜像

	docker images // 查看本地拉取的镜像

	docker代理设置方法参考：http://dev.zte.com.cn/topic/#/25899

	2、设置docker取消docker registry服务器代理

	vi /etc/systemd/system/docker.service.d/http-proxy.conf

	[Service]
	Environment="HTTP_PROXY=http://10.67.14.8:80/" // 10.67.14.8是上海的上网代理proxysh.zte.com.cn，若是其他地区则需要对应修改
	Environment="NO_PROXY=localhost,127.0.0.0/8,本机IP:5000"

	保存，退出。

	systemctl daemon-reload

	systemctl restart docker

	3、在本机设置docker参数

	说明：Ubuntu系统用户修改文件：/etc/default/docker，CentOS系统用户修改文件：/etc/sysconfig/docker

	vi /etc/sysconfig/docker 或者 vi /etc/default/docker

	DOCKER_OPTS="$DOCKER_OPTS  --registry-mirror=http://786573aa.m.daocloud.io --insecure-registry=本机IP:5000"

	保存，退出。

	systemctl restart docker

	4、在所有需要用到该docker registry的机器上设置docker参数

	按照2的方式首先设置docker取消docker registry服务器代理，接着修改docker相关参数。

	vi /etc/sysconfig/docker 或者 vi /etc/default/docker

	ADD_REGISTRY='--add-registry docker registry服务器IP:5000'

	INSECURE_REGISTRY='--insecure-registry docker registry服务器IP:5000'

	保存，退出。

	systemctl restart docker

	5、由本地registry:latest镜像启动运行docker registry容器

	mkdir /opt/registry

	说明：docker registry服务默认会将上传的镜像保存在容器的/var/lib/registry目录下，这里我们将其映射到本机的/opt/registry目录

  
	 docker run -d -v /opt/docker/registry/:/var/lib/registry/ -p 10.200.176.60:5000:5000 -v /opt/docker/registry/auth/:/auth/  -e "REGISTRY_AUTH=htpasswd" -e "REGISTRY_AUTH_HTPASSWD_REALM=Registry Realm" -e REGISTRY_AUTH_HTPASSWD_PATH=/auth/htpasswd --privileged=true --restart=always --name registry registry:latest

	6、测试私有docker registry镜像库在本机和其他机器是否可用
	
	7、报错解决办法：

	Error response from daemon: Get https://10.10.239.222:5000/v1/_ping: http: server gave HTTP response to HTTPS client
	
	只需要两步即可：

	1. Create or modify /etc/docker/daemon.json

	echo '{ "insecure-registries":["10.10.239.222:5000"] }' > /etc/docker/daemon.json
	
	cat /etc/docker/daemon.json
	
	{ "insecure-registries":["10.10.239.222:5000"] }
	
	2. 重载docker
	
	root@localhost ~]# systemctl restart docker

	本机测试：

	docker tag docker.io/registry:latest docker registry服务器IP:5000/docker.io/registry:latest

	docker push docker registry服务器IP:5000/docker.io/registry:latest

	docker rmi   docker registry服务器IP:5000/docker.io/registry:latest

	docker pull  docker registry服务器IP:5000/docker.io/registry:latest

	其他机器测试：

	docker pull   docker registry服务器IP:5000/docker.io/registry:latest

	docker tag   docker registry服务器IP:5000/docker.io/registry:latest docker registry服务器IP:5000/test/registry:latest

	docker push docker registry服务器IP:5000/test​/registry:latestdocker rmi   docker registry服务器IP:5000/test/registry:latest

	docker pull  docker registry服务器IP:5000/test/registry:latest
	
	参考：
	https://docs.docker.com/
	http://www.server110.com/docker/201411/11122.html
	http://cloud.51cto.com/art/201501/463536.htm
	http://www.cnblogs.com/lienhua34/p/4922130.html
	http://tonybai.com/2016/02/26/deploy-a-private-docker-registry/
	
**10、安装ZeroMQ/jzeromq**

	sudo apt-get install libtool autoconf automake cmake
	
	sudo autoreconf -ivf
	
	(1) ZeroMQ
	sudo ./configure
	sudo make
	sudo make install
	sudo ldconfig
	
	(2) jzmq
	sudo ./autogen.sh
	sudo ./configure
	sudo make
	sudo make install
	# 加入环境变量，见环境变量设置

	参考：
	http://download.zeromq.org/
	https://github.com/zeromq/jzmq
	http://zeromq.org/bindings:go

**12、golang/scala安装**

	sudo apt install golang

	sudo apt install scala

	# 加入环境变量，见环境变量设置

**13、gradle安装**

	sudo cp -r gradle-2.2.1 /opt
	# 加入环境变量，见环境变量设置

	参考：
	http://services.gradle.org/distributions
	http://gradle.org/gradle-download/
	https://docs.gradle.org/2.12/userguide/gradle_daemon.html

**14、安装Intellij IDEA**

	sudo tar -zxvf ideaIU-14.1.3.tar.gz -C /opt
	# 加入环境变量，见环境变量设置
	# 启动
	1) 命令行 idea.sh
	2) 点击桌面左上角Application --> Development --> Intellij IDEA
	# 安装go-lang插件
	intellij-go.jar
	参考：
	http://studygolang.com/articles/2545
	http://www.jetbrains.com/idea/
	http://plugins.jetbrains.com/
	http://www.phperz.com/article/15/0923/159043.html
	http://idea.qinxi1992.cn/

**15、Ubuntu用户环境变量设置**

	sudo vim /home/user/.bashrc
	
	# set environment
	export NEXUS_HOME=/opt/nexus-2.14.0-01
	export GRADLE_HOME=/opt/gradle-2.2.1
	export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_77
	export JRE_HOME=${JAVA_HOME}/jre
	export HADOOP_HOME=/opt/hadoop-2.7.3
	export HADOOP_MAPRED_HOME=${HADOOP_HOME}
	export HADOOP_COMMON_HOME=${HADOOP_HOME}
	export HADOOP_HDFS_HOME=${HADOOP_HOME}
	export HADOOP_CONF_DIR=${HADOOP_HOME}/etc/hadoop
	export HADOOP_COMMON_LIB_NATIVE_DIR=${HADOOP_HOME}/lib/native
	export HADOOP_OPTS="-Djava.library.path=${HADOOP_HOME}/lib:${HADOOP_COMMON_LIB_NATIVE_DIR}"
	export YARN_HOME=${HADOOP_HOME}
	export SPARK_HOME=/opt/spark-2.0.0-bin-hadoop2.7
	export SPARK_EXAMPLES_JAR=${SPARK_HOME}/examples/jars/spark-examples_2.11-2.0.0.jar
	export GOBIN=/home/zte/projects/go/bin
	export GOPATH=/home/zte/projects/go
	export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib:/usr/local/share/java/zmq.jar:.
	export PATH=${JAVA_HOME}/bin:${NEXUS_HOME}/bin:${GRADLE_HOME}/bin:${GOBIN}:${HADOOP_HOME}/bin:${HADOOP_HOME}/sbin:${SPARK_HOME}/bin:$PATH
	export LD_LIBRARY_PATH=/usr/local/lib:${HADOOP_HOME}/lib/native:$LD_LIBRARY_PATH
	unset http_proxy

**16、VMware esx server 4.1序列号**

	HF2H2-8ZHD4-48E38-0R2Z2-0V052
	
	HA0NK-0A10J-M8EN1-FJ2X0-9CR4F
	
	NA2H8-08HE0-M8968-UTC74-3CHN4
	
	MC058-4P20K-48E99-0T0E6-AQ0P8
	
	0Z6JH-8QJEJ-M8911-Y2ANK-1LR00
	
	1V4NK-AQL9M-M8999-4U3NK-36A3F
	
	4G6N8-ALJ8Q-48049-9KAEP-AYRQ2
	
	0A4WA-20L82-H8EV0-EJ0NK-CG276
	
	HY2J2-83J17-H88M0-KTCQ2-8L02D
	
	5V252-4G2DJ-M81V8-81950-0LKL4
	
	MV4EU-2HKD5-M8860-WHCG6-8L06F
	
	5U4NA-4CJ9H-48E40-110ZP-9VKM2
	
	http://blog.sina.com.cn/s/blog_7e64a87b0101bqm1.html

**17、error while loading shared libraries的解决方法**

	库文件在连接（静态库和共享库）和运行（仅限于使用共享库的程序）时被使用，其搜索路径是在系统中进行设置的。一般 Linux 系统把 /lib 和 /usr/lib 两个目录作为默认的库搜索路径，所以使用这两个目录中的库时不需要进行设置搜索路径即可直接使用。对于处于默认库搜索路径之外的库，需要将库的位置添加到 库的搜索路径之中。设置库文件的搜索路径有下列两种方式，可任选其一使用：
	
	1.修改环境变量LD_LIBRARY_PATH
	在环境变量 LD_LIBRARY_PATH 中指明库的搜索路径。
	
	2.修改配置文件ld.so.conf
	
	在 /etc/ld.so.conf 文件中添加库的搜索路径。
	将自己可能存放库文件的路径都加入到/etc/ld.so.conf 中是明智的选择
	添加方法也极其简单，将库文件的绝对路径直接写进去就OK了，一行一个。例如：
	/usr/X11R6/lib
	
	/usr/local/lib
	
	/opt/lib
	
	需要注意的是：第二种搜索路径的设置方式对于程序连接时的库（包括共享库和静态库）的定位已经足够了，但是对于使用了共享库的程序的执行还是不够的。
	
	这是因为为了加快程序执行时对共享库的定位速度，避免使用搜索路径查找共享库的低效率，所以是直接读取库列表文件 /etc/ld.so.cache 从中进行搜索的。/etc/ld.so.cache 是一个非文本的数据文件，不能直接编辑，它是根据 /etc/ld.so.conf 中设置的搜索路径由 /sbin/ldconfig 命令将这些搜索路径下的共享库文件集中在一起而生成的（ldconfig 命令要以 root 权限执行）。
	
	因此，为了保证程序执行时对库的定位，在 /etc/ld.so.conf 中进行了库搜索路径的设置之后，还必须要运行 /sbin/ldconfig 命令更新 /etc/ld.so.cache 文件之后才可以。ldconfig ,简单的说，它的作用就是将/etc/ld.so.conf列出的路径下的库文件缓存到/etc/ld.so.cache 以供使用。因此当安装完一些库文件，(例如刚安装好glib)，或者修改ld.so.conf增加新的库路径后，需要运行一下 /sbin/ldconfig使所有的库文件都被缓存到ld.so.cache中，如果没做，即使库文件明明就在/usr/lib下的，也是不会被使用的，结果编译过程中报错，缺少xxx库，去查看发现明明就在那放着，搞的想大骂computer蠢猪一个。
	
	在程序连接时，对于库文件（静态库和共享库）的搜索路径，除了上面的设置方式之外，还可以通过 -L 参数显式指定。因为用 -L 设置的路径将被优先搜索，所以在连接的时候通常都会以这种方式直接指定要连接的库的路径。
	
	前面已经说明过了，库搜索路径的设置有两种方式：
	
	在环境变量 LD_LIBRARY_PATH 中设置
	
	在 /etc/ld.so.conf 文件中设置。
	
	其中，第二种设置方式需要 root 权限，以改变 /etc/ld.so.conf 文件并执行 /sbin/ldconfig 命令。而且，当系统重新启动后，所有的基于 GTK2 的程序在运行时都将使用新安装的 GTK+ 库。不幸的是，由于 GTK+ 版本的改变，这有时会给应用程序带来兼容性的问题，造成某些程序运行不正常。为了避免出现上面的这些情况，在 GTK+ 及其依赖库的安装过程中对于库的搜索路径的设置将采用第一种方式进行。这种设置方式不需要 root 权限，设置也简单：
	
	$ export LD_LIBRARY_PATH=/opt/gtk/lib:$LD_LIBRARY_PATH
	
	可以用下面的命令查看 LD_LIBRAY_PATH 的设置内容：
	
	$ echo $LD_LIBRARY_PATH
	
	至此，库的两种设置就完成了。
	
	dpkg -l | grep '^rc' | awk '{print $2}' | sudo xargs dpkg --purge 2>/dev/null

**18、ubuntu设置服务自启动**

	update-rc.d XXX enable/disable
	
	如果提示：System start/stop links for /etc/init.d/mysql do not exist.
	
	则先执行 update-rc.d XXX defaults, 这个命令会将服务注册到自启动中。

**19、ubuntu清除多余内核**

	# 查看系统内存在的内核版本列表
	sudo dpkg --get-selections |grep linux

	# 查看当前Ubuntu系统使用的内核版本
	uname -a

	# 删除多余内核：
	sudo apt-get purge linux-headers-3.0.0-12 linux-image-3.0.0-12-generic

	# 更新grub
	sudo update-grub

**20、强大的 apt-get 命令**

	一、ubuntu下管理软件最方便的非 apt-get 工具莫属了，它的常见用法稍微整理一下供以后参考（详细见 man apt-get ）:
	
	1.更新源，升级软件和系统之前要先干这个～ （源目录：/etc/apt/sources.list）
	sudo apt-get update
	
	2.更新已安装的包到最新版本（若已经最新则忽略）
	sudo apt-get upgrade
	
	3.升级系统，有一定风险！此时 apt 将首先升级重要的包，如果有冲突的话，可能会删除一些次要的包（详见 man apt_preferences）
	sudo apt-get dist-upgrade
	
	4.安装（升级）一个或多个软件，也可指定版本号，安装某软件特定版本。pac 也可以使用正则匹配哦
	sudo apt-get install pac1 pac2  pac3 ...
	
	5.删除软件，用法同 install，会保留配置文件
	sudo apt-get remove pac1 pac2  pac3 ...
	
	6.用法同 remove ，也将删除配置文件，（这个更彻底）
	sudo apt-get purge pac1 pac2  pac3 ...
	
	7.获取源码，到当前文件夹下
	sudo apt-get source pac
	
	8.安装相关的编译环境
	sudo apt-get build-dep pac
	
	9.更新缓存，检查是否有损坏的依赖
	sudo apt-get check
	
	10.下载二进制包到当前目录
	sudo apt-get download pac
	
	11.clears out the local repository of retrieved package files，可以释放磁盘空间
	sudo apt-get clean
	
	12.clears out the local repository of retrieved package files，清除不能下载和无用的包，可以释放磁盘空间
	sudo apt-get autoclean
	
	13. remove packages that were automatically installed to satisfy dependencies for some package and that are no more needed.
	sudo apt-get autoremove
	
	14.其他一些参数：如 -y -m -f ，组合起来实现更强大的功能
	
	二、源及升级
	
	1.更新源 /etc/apt/sources.list
	
	2.升级版本
	sudo apt-get install update-manager-core
	sudo do-release-upgrade
	
	3.查看当前版本及内核版本：
	lsb_release -a
	uname -a

**21、UFW防火墙 -- ubuntu**

	sudo apt-get install ufw
	
	1 启用
	
	sudo ufw enable
	
	sudo ufw default deny 
	
	作用：开启了防火墙并随系统启动同时关闭所有外部对本机的访问（本机访问外部正常）。
	
	2 关闭
	
	sudo ufw disable 
	
	2 查看防火墙状态
	
	sudo ufw status 
	
	3 开启/禁用相应端口或服务举例
	
	sudo ufw allow 80 允许外部访问80端口
	
	sudo ufw delete allow 80 禁止外部访问80 端口
	
	sudo ufw allow from 192.168.1.1 允许此IP访问所有的本机端口
	
	sudo ufw deny smtp 禁止外部访问smtp服务
	
	sudo ufw delete allow smtp 删除上面建立的某条规则
	
	sudo ufw deny proto tcp from 10.0.0.0/8 to 192.168.0.1 port 22 要拒绝所有的TCP流量从10.0.0.0/8 到192.168.0.1地址的22端口
	
	可以允许所有RFC1918网络（局域网/无线局域网的）访问这个主机（/8,/16,/12是一种网络分级）：
	sudo ufw allow from 10.0.0.0/8
	sudo ufw allow from 172.16.0.0/12
	sudo ufw allow from 192.168.0.0/16
	
	# 推荐设置
	
	sudo apt-get install ufw
	
	sudo ufw enable
	
	sudo ufw default deny 
	
	这样设置已经很安全，如果有特殊需要，可以使用sudo ufw allow开启相应服务。 

**22、Ubuntu修改系统时间**

	# 修改时间
	sudo date -s MM/DD/YY //修改日期
	sudo date -s hh:mm:ss //修改时间
	
	# 在修改时间以后，修改硬件CMOS的时间
	sudo hwclock --systohc //非常重要，如果没有这一步的话，后面时间还是不准

**23、find命令**

	find / -name "filename" 2>/dev/null

**24、deb文件安装**

	deb是debian linus的安装格式，跟red hat的rpm非常相似，最基本的安装命令是：dpkg -i file.deb
	
	dpkg 是Debian Package的简写，是为Debian 专门开发的套件管理系统，方便软件的安装、更新及移除。所有源自Debian的Linux发行版都使用dpkg，例如Ubuntu、Knoppix 等。
	以下是一些 Dpkg 的普通用法：
	
	1、dpkg -i <package.deb>
	安装一个 Debian 软件包，如你手动下载的文件。
	
	2、dpkg -c <package.deb>
	列出 <package.deb> 的内容。
	
	3、dpkg -I <package.deb>
	从 <package.deb> 中提取包裹信息。
	
	4、dpkg -r <package>
	移除一个已安装的包裹。
	
	5、dpkg -P <package>
	完全清除一个已安装的包裹。和 remove 不同的是，remove 只是删掉数据和可执行文件，purge 另外还删除所有的配制文件。
	
	6、dpkg -L <package>
	列出 <package> 安装的所有文件清单。同时请看 dpkg -c 来检查一个 .deb 文件的内容。
	
	7、dpkg -s <package>
	显示已安装包裹的信息。同时请看 apt-cache 显示 Debian 存档中的包裹信息，以及 dpkg -I 来显示从一个 .deb 文件中提取的包裹信息。
	
	8、dpkg-reconfigure <package>
	重新配制一个已经安装的包裹，如果它使用的是 debconf (debconf 为包裹安装提供了一个统一的配制界面)。

**25、lksctp-tools安装**

	# lksctp-tools-1.0.16
	 ./bootstrap
	 ./configure
	 sudo make
	 sudo make install

**26、添加网桥**

	sudo ifconfig
	sudo brctl show
	sudo brctl addbr br0
	sudo ip addr add 172.17.42.1/24 dev br0
	sudo ip link set dev br0 up
	sudo ifconfig

**27、Mysql/MariaDB相关操作**
	
	sudo apt-get install mysql-server
 
	sudo apt-get isntall mysql-client
 
	sudo apt-get install libmysqlclient-dev
	
	Master和Slave上执行：
	mysql -uroot -proot123
	
	mysql> CREATE USER zte;
	
	mysql> GRANT ALL ON *.* TO 'zte'@'%';
	
	mysql> SELECT DISTINCT CONCAT('User: ''',user,'''@''',host,''';') AS query FROM mysql.user;
	
	mysql> flush privileges;
	
	Master上执行：
	sudo vim /etc/mysql/mysql.conf.d/mysqld.cnf
	# 增加
	...............................................
	...............................................
	...............................................
	[mysqld]
	#
	# * Basic Settings
	#
	user            = mysql
	pid-file        = /var/run/mysqld/mysqld.pid
	socket          = /var/run/mysqld/mysqld.sock
	port            = 3306
	basedir         = /usr
	datadir         = /var/lib/mysql
	tmpdir          = /tmp
	lc-messages-dir = /usr/share/mysql
	server-id       = 10
	log-bin-index   = master-bin.index
	log-bin         = master-bin
	log-slave-updates
	auto-increment-increment= 2
	auto-increment-offset = 1
	skip-external-locking
	#
	# Instead of skip-networking the default is now to listen only on
	# localhost which is more compatible and is not less secure.
	bind-address    = 10.62.49.157
	...............................................
	...............................................
	...............................................
	
	mysql -u root -p
	Enter password:root23
	mysql> GRANT REPLICATION SLAVE ON *.* To root IDENTIFIED BY '';
	
	mysql> quit
	
	mysql
	mysql> show master status;
	mysql> show master status\G;
	mysql> show full processlist;
	
	Slave上执行：
	sudo vim /etc/mysql/mysql.conf.d/mysqld.cnf
	# 增加
	...............................................
	...............................................
	...............................................
	[mysqld]
	#
	# * Basic Settings
	#
	user            = mysql
	pid-file        = /var/run/mysqld/mysqld.pid
	socket          = /var/run/mysqld/mysqld.sock
	port            = 3306
	basedir         = /usr
	datadir         = /var/lib/mysql
	tmpdir          = /tmp
	lc-messages-dir = /usr/share/mysql
	server-id       = 2
	relay-log-index = slave-relay-bin.index
	relay-log       = slave-relay-bin
	skip-external-locking
	#
	# Instead of skip-networking the default is now to listen only on
	# localhost which is more compatible and is not less secure.
	bind-address            = 127.0.0.1
	...............................................
	...............................................
	...............................................
	
	mysql
	mysql> change master to master_host='10.62.49.157', master_user='root', master_port=3306, master_password='root123', master_log_file='master-bin.000001', master_log_pos=120;
	
	mysql> start slave;
	
	mysql> show slave status;
	
	mysql> show slave status\G;
	
	mysql> show full processlist;

	# Mysql数据库备份
	mysqldump --host="10.62.49.160" -uroot -proot123 -P3306 --opt --databases mysqldump_test | mysql --host="10.62.49.157" -uroot -proot123 -P3306 -C
	
	# 忘记root密码
	配置文件/etc/mysql/my.cnf  [mysqld]中加入skip-grant-tables一行
	sudo service mysql restart
	mysql -uroot
	mysql> use mysql;
	mysql> UPDATE user SET Password = password ( 'root' ) WHERE User = 'root' ; 
	mysql> flush privileges;
	mysql> exit;
	
    
    mysql> grant all privileges on *.* to root@'%' identified by 'root';
    mysql> flush privileges;
    
    查看 MySQL 数据库服务器和数据库字符集:
	mysql> show variables like '%char%';
    
    设置字符集：
    [mysqld]
    character-set-server=utf8
    collation-server=utf8_general_ci

    [mysql]
    default-character-set = utf8

    [mysql.server]
    default-character-set = utf8

    [mysqld_safe]
    default-character-set = utf8

    [client]
    default-character-set = utf8
	参考：
	http://www.cnblogs.com/feichexia/p/MysqlDataBackup.html
	http://www.cnblogs.com/Richard-xie/p/4205630.html
	
**28、SecureCRT 常用技巧**
	
	快捷键：

	1、 ctrl + a : 移动光标到行首

	2、 ctrl + e ：移动光标到行尾

	3、 ctrl + d ：删除光标之后的一个字符

	4、 ctrl + w ：删除行首到当前光标所在位置的所有字符

	5、 crtl + k ：删除当前光标到行尾的所有字符

	6、 alt + b ： 打开快速启动栏

	7、 alt + 1/2/3... ： 在多个不同的session标签之间切换

**29、安装Tomcat**
  	
	1）下载tomcat

	   http://tomcat.apache.org/

	   下载core下边的，tar.gz文件

    2）解压

       在终端上切换到tomcat 的下载目录下，再输入

       tar -vzxf XXX-tomcat-XXX.tar.gz -C /opt
	
	   cd /opt/XXX-tomcat-XXX
	   
	   sudo chmod -R 777 *

	4）启动、关闭tomcat

       在tomcat的bin目录下右击 在终端中打开 ，打开终端，输入：
	
	   启动服务器：

       sudo ./startup.sh

       关闭服务器：

       sudo ./shutdown.sh
	
	5）Tomact开机启动

	   执行命令sudo vim /etc/init.d/tomcat，输入：

		#!/bin/sh 
		### BEGIN INIT INFO
		# Provides:          tomcat
		# Required-Start:    $remote_fs $network
		# Required-Stop:     $remote_fs $network
		# Default-Start:     2 3 4 5
		# Default-Stop:      0 1 6
		# Short-Description: The tomcat Java Application Server
		### END INIT INFO
		# description: Auto-starts tomcat
		# processname: tomcat
		
		export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_77
		
		case $1 in
		start)
		sh /opt/apache-tomcat-9.0.0.M6/bin/startup.sh
		;;
		stop) 
		sh /opt/apache-tomcat-9.0.0.M6/bin/shutdown.sh
		;;
		restart)
		sh /opt/apache-tomcat-9.0.0.M6/bin/shutdown.sh
		sh /opt/apache-tomcat-9.0.0.M6/bin/startup.sh
		;;
		esac 
		exit 0

	   sudo chmod 755 /etc/init.d/tomcat
	   
	   sudo ln -s /etc/init.d/tomcat /etc/rc1.d/K99tomcat
	   
	   sudo ln -s /etc/init.d/tomcat /etc/rc2.d/S99tomcat

	   在终端中执行sudo /etc/init.d/tomcat start/stop/restart(三个选一个就能实现启动，停止，重启功能了）
	
	   访问http://localhost:8080

	6）文献
	   http://wiki.ubuntu.org.cn/Tomcat6
	   http://tomcat.apache.org/index.html

**30、安装Nexus**

	http://www.sonatype.com/download-oss-sonatype
	http://blog.csdn.net/lansine2005/article/details/19397685
	http://blog.csdn.net/dengsilinming/article/details/17635191
	https://support.sonatype.com/hc/en-us/articles/213465668-Where-is-the-Nexus-OSS-war-file-

	1、使用war包安装

	下载nexus.xxx.war放到/opt/apache-tomcat-9.0.0.M6/webapps下面

	访问http://your-server:8080/nexus，用户名/密码：admin/admin123

	2、使用bundle包安装

	下载nexus-latest-bundle.tar.gz

	sudo tar -zxvf nexus-latest-bundle.tar.gz -C /opt

	sudo chown -R 用户名：用户名 nexus-latest

	sudo chown -R 用户名：用户名 sonatype-work

	默认的监听地址为：http://your-server:8081/nexus

	Nexus的默认登录帐号为：admin:admin123

	设置nexus监听端口和地址：修改$NEXUS_HOME/conf/nexus.properties
	
	设置开机启动
	
	sudo ln -s $NEXUS_HOME/bin/nexus /etc/init.d/nexus
	
	sudo chmod 755 /etc/init.d/nexus

	update-rc.d nexus defaults

**31、安装Maven**
	
	1）文献：
	   http://search.maven.org/
	   http://mvnrepository.com/
	   http://xiaoboss.iteye.com/blog/1560542
	   http://www.yiibai.com/maven/maven_environment_setup.html
	   http://www.cnblogs.com/friends-wf/p/3804377.html
	   http://blog.csdn.net/wanghantong/article/details/36427433
	   http://maven.apache.org/download.cgi
	   http://www.2cto.com/kf/201303/198403.html
	   http://san-yun.iteye.com/blog/1609410
	   http://blog.csdn.net/dhmpgt/article/details/9998321
	   http://www.iteye.com/topic/1126678

	2）解压文件：
	   tar -zxvf apache-maven-3.3.9-bin.tar.gz -C /opt
	   # 加入环境变量，见环境变量设置

	3）setting.xml配置


**31、Gradle进阶**

	1）安装详见标号13

	2）学习资料
	   http://wiki.jikexueyuan.com/project/gradle/

**32、linux下tar.gz、tar、bz2、zip等解压缩、压缩命令小结**
	
	1)、*.tar 用 tar –xvf 解压
	2)、*.gz 用 gzip -d或者gunzip 解压
	3)、*.tar.gz和*.tgz 用 tar –xzf 解压
	4)、*.bz2 用 bzip2 -d或者用bunzip2 解压
	5)、*.tar.bz2用tar –xjf 解压
	6)、*.Z 用 uncompress 解压
	7)、*.tar.Z 用tar –xZf 解压
	8)、*.rar 用 unrar e解压
	9)、*.zip 用 unzip 解压 
	
	例如：
	1)、tar –xvf file.tar //解压 tar包
	2)、tar -xzvf file.tar.gz //解压tar.gz
	3)、tar -xjvf file.tar.bz2 //解压 tar.bz2
	4)、tar –xZvf file.tar.Z //解压tar.Z
	5)、unrar e file.rar //解压rar
	6)、unzip file.zip //解压zip

	参考：
	http://www.jb51.net/LINUXjishu/43356.html

**33、Firefox安装**

	1)、命令安装：
	sudo apt-get install firefox
	或者
	sudo apt install firefox
	
	2)、手动安装：
	sudo tar -xjf Firefox-latest-x86_64.tar.bz2 -C /usr/lib/
	sudo ln -s /usr/lib/firefox/firefox /usr/bin/firefox

	3)、配置代理
![](proxy.png)

**34、Phabricator安装使用**
	
	安装步骤：
	1)、PHP软件安装
	sudo apt-get install php5 php5-curl

	2)、arc客户端
	获取并解压arc包：
	进入存放目录如 cd /usr/local/arc
	执行下载命令：wget ftp://pha.zte.com.cn/client/arc.zip
	执行解压命令：unzip arc.zip

	3)、配置环境变量
	vim ~/.bashrc
	export PATH="/usr/local/arc/arcanist/bin:$PATH"
	
	4)、配置默认编辑器
	arc set-config editor "vim"
	
	5)、配置默认服务器
	arc set-config default "http://pha.zte.com.cn"
	
	6)、安装证书
	arc install-certificate
	注意：如果出现无法连接网络的情况，请设置一下网络代理
	登录网站：http://pha.zte.com.cn/conduit/login/
	复制API Token: cli-4rzgf2vue3yzxt4u2lqqiqlq4266

	7)、Phabricator 使用(SVN)
	http://pha.zte.com.cn/w/guide/instruction/svn-based/
	
	1）检出svn副本并修改

	注意：如果希望增加或删除文件进行评审，先进行svn add 或 svn delete 再arc diff。
	
	2）创建评审
	
	场景一：此工作副本无其他待处理评审
	
	方法一、命令行方式提交：
	
	在副本目录cmd执行命令  arc diff --encoding GBK  file1 file2 ...
	
	小技巧：输入arc diff –encoding GBK后，直接将需要评审的文件拖入命令提示符并改成相对路径 (注意文件之间添加空格)。
	
	方法二、右键方式提交：
	
	在副本目录，右键选择arc diff，弹出svn变化文件选择框(2.5版本arcmenu开始支持)，勾选希望提交的文件并点击确定！

	场景二：此工作副本有待处理评审，想重新创建新的评审
	
	注意：同一文件不应出现在两个评审中
	
	方法：在副本目录cmd执行命令
	arc diff --encoding GBK  --create  file1 file2 ...

	3）更新评审
	
	场景一：此工作副本无其他待处理评审
	
	方法：同创建评审场景一
	
	注意：需要填写修改摘要，不填写会当成放弃提交处理。

	场景二：此工作副本有待处理评审，想更新指定评审

	注意：

    同一文件不应出现在两个评审中
    如增加文件B，当前评审文件A也应在更新文件列表中

	方法：在副本目录cmd执行命令
	arc diff --update D***  file1 file2 ...

	4）提交代码到SVN库
	
	场景一：此工作副本只有一个已完成评审
	
	方法一：
	
	在副本目录，右键菜单执行"arc commit"执行提交
	
	方法二：
	
	在副本根目录cmd执行命令  arc commit
	
	场景二：此工作副本至少有一个已完成评审
	
	方法：在副本根目录cmd执行命令  arc commit  --revision  D***
	
	注意：

    只有当评审单的状态为Accepted时，才可以提交代码。
    弹出SVN提交页面(新版本arc不会弹出)，注意将不需要上传的文件去掉勾选，点击OK。
    svn代码目录命令行使用arc list 可以看到当前所有待处理评审

	5）填写评审单信息
	<<Replace With Title>>

	Summary:
	
	Test Plan: NO
	
	Reviewers: zhu.zhengnan
	
	Subscribers:

	6）填写代码行内评论
	
	鼠标点击左侧行号或长按并拖动选中连续的多个行号。
	
	7）评审人选项
	
	Comment：增加讨论（不改变评审状态）
	
	Accept Revision：接受评审(评审状态：Accepted)
	
	Request Changes：不接受评审(评审状态：Needs Review )
	
	Resign as Reviewer：拒绝评审，可以另外指定评审人(评审状态：Needs Review)
	
	Commandeer Revision：评审人获取此评审单的处理权(评审状态：Needs Review)
	
	Add Reviewers：增加其他评审人参与评审(评审状态：Needs Review)
	
	Add Subscribers：增加其他人作为此次评审的提交人(评审状态：Needs Review)
	
	8）提交人选项
	
	Comment：增加讨论（不改变评审状态）
	
	Abandon Revision：放弃并关闭此次评审(评审状态：Abandoned)
	
	Reclaim Revision：重新打开已经Abandoned的评审(评审状态：Needs Review)
	
	Plan Changes：评审待进一步更新，暂不需要评审人处理(评审状态：Changes Planned)
	
	Request Review：更新Changes Planed状态，请求开始评审(评审状态：Needs Review)
	
	Add Reviewers：增加其他评审人参与评审(评审状态：Needs Review)
	
	Add Subscribers：增加其他人作为此次评审的提交人(评审状态：Needs Review)

	8)、Phabricator 使用(GIT)
	http://pha.zte.com.cn/w/guide/instruction/git-based/

**35、Gerrit安装使用**
	// TODO

**36、ESX4.1开启SSH的root登陆**
	
	ESX4.1在默认情况下SSH是开启的，但是不允许以root权限登录。
	修改/etc/ssh/sshd_config，并重启sshd服务。
	
	将　　PermitRootLogin no
	改成　PermitRootLogin yes
	
	/etc/init.d/sshd restart

	参考文献：
	http://pubs.vmware.com/vsphere-51/index.jsp?topic=%2Fcom.vmware.vsphere.vm_admin.doc%2FGUID-AFEDC48B-C96F-4088-9C1F-4F0A30E965DE.html

**37、Ubuntu下搭建Robotframework+Selenium环境**

	1）安装pip
	
	# sudo apt-get install python-pip 用该命令安装会出现错误，是一个bug
	
	sudo apt-get purge python-pip

	sudo apt-get remove python-pip
	
	sudo apt-get autoremove
	
	wget https://bootstrap.pypa.io/get-pip.py --no-check-certificate
    wget https://bootstrap.pypa.io/get-pip.py
    参考：http://www.cnblogs.com/ityoung/p/6256169.html
	
	sudo python get-pip.py
	
	sudo easy_install -U pip
	
	参考：http://blog.csdn.net/tao_627/article/details/44274445
	
	2）安装robotframework
	
	sudo pip install robotframework
	
	3）安装selenium2library
	
	sudo pip install robotframework-selenium2library
	
	4）安装Wxpython
    
    echo "deb http://archive.ubuntu.com/ubuntu wily main universe" | sudo tee /etc/apt/sources.list.d/wily-copies.list

    sudo apt update

    sudo apt install python-wxgtk2.8 python-wxtools wx2.8-i18n

    sudo rm /etc/apt/sources.list.d/wily-copies.list
    sudo apt update
	
	5）安装ride
	
	sudo pip install robotframework-ride
	
	6）打开ride
	
	在终端里面输入：ride.py 
    
	7) 安装必备库
    
    sudo pip install requests
    sudo pip install -U robotframework-requests
    sudo pip install robotframework-databaselibrary
    sudo pip install PyMySQL
    sudo pip install –upgrade robotframework-httplibrary
    sudo pip install  robotframework-httplibrary
    sudo pip install robotframework-redislibrary
    sudo pip install xmltodict
    sudo pip install pygments
    sudo pip install jsonpath_rw
    sudo pip install mysql.connector
    sudo pip install mysql-connector
    sudo pip install httplib2
    sudo pip install pyftpdlib
    sudo pip install paramiko
    sudo apt-get install python-dev
    sudo easy_install pycryp
    sudo pip install tenjin
    sudo pip install jinja2
    sudo pip install jmespath

	8）关于RFS的使用，请参考
	
	http://blog.csdn.net/xc5683/article/details/10017915
    
    http://www.cnblogs.com/saryli/p/7261295.html

**38、Ubuntu下安装部署Hadoop环境**

	sudo addgroup hadoop

	sudo usermod -a -G hadoop 用户名

	下载文件，http://hadoop.apache.org/releases.html，选择最新的版本，文件名为：hadoop-x.x.x.tar.gz。

	sudo tar -zxvf hadoop-x.x.x.tar.gz -C /opt

	配置环境变量，参见#15环境变量设置

	参考文献：
	http://blog.csdn.net/tian_li/article/details/49103603
	http://www.linuxidc.com/Linux/2015-09/122873.htm
	http://blog.csdn.net/young_kim1/article/details/50324345

**39、Ubuntu下安装部署Spark环境**

	安装JDK，参考#8；安装scala，参考#15；安装Hadoop，参考#38；配置免密码ssh登陆，参考#3

	下载文件，http://spark.apache.org/downloads.html，选择最新版本，文件名为：spark-x.x.x-bin-hadoopx.x.tgz。

	wget http://d3kbcqa49mib13.cloudfront.net/spark-x.x.x-bin-hadoopx.x.tgz

	sudo tar -zxvf spark-x.x.x-bin-hadoopx.x.tgz -C /opt

	配置环境变量，参见#15环境变量设置

	Spark配置：

	cd ${SPARK_HOME}/conf

	sudo cp spark-env.sh.template spark-env.sh

	sudo vim spark-env.sh 文件结尾添加以下内容：

	export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_77
	export SCALA_HOME=/usr/share/scala-2.11
	export SPARK_HOME=/opt/spark-2.0.0-bin-hadoop2.7
	
	SPARK_LOCAL_IP="10.62.49.228"
	
    配置Slave：

	sudo cp slaves.template slaves 

	sudo vim slaves 添加以下内容： 

	localhost

	配置log：

	sudo cp log4j.properties.template log4j.properties

	参考文献：
	http://www.cnblogs.com/ghouleztt/p/5791536.html
	http://blog.csdn.net/tian_li/article/details/49328517
	http://blog.csdn.net/qinzhaokun/article/details/49101377
	http://www.cnblogs.com/spark-china/p/3906751.html
	http://blog.csdn.net/silentwolfyh/article/details/51559062

**40、CentOS7下使用Bind9配置DNS服务**
	1、安装bind软件
	
	yum -y install bind
	
	yum list installed | grep bind
	2、配置bind参数
	
	vi /etc/named.conf
	options {
	
	    listen-on port 53 { DNS服务器地址;127.0.0.1; };
	
	    directory     "/var/named";
	
	    allow-query     { any; };
	    allow-query-cache { any; };
	    forwarders { 10.67.1.9;10.67.1.10; }; // 这个地方保证能连接互联网，用的是公司上海研发中心DNS代理地址，其他地方需要相应修改
	
	    dnssec-enable yes;
	    dnssec-validation yes;
	    dnssec-lookaside auto;
	
	};

	zone "xxx.yyy.com" IN {
	  type master;
	  file "zones/xxx.yyy.com.db";
	  allow-update { key xxx.yyy.com; };
	  allow-query{any;};
	};
	
	zone "yyy.com" IN {
	  type master;
	  file "zones/yyy.com.db";
	  allow-update { };
	  allow-query{ any; };
	};
	
	保存，退出。
	
	说明：xxx.yyy.com和yyy.com是需要解析的域名
	
	mkdir /var/named/zones
	
	cd /var/named/zones
	
	添加两个文件xxx.yyy.com.db和yyy.com.db
	
	vi xxx.yyy.com.db
	
	$ORIGIN  .
	$TTL 1  ;  1 seconds (for testing only)
	xxx.yyy.com IN SOA master.xxx.yyy.com.  root.xxx.yyy.com.  (
	  2011112904  ;  serial
	  60  ;  refresh (1 minute)
	  15  ;  retry (15 seconds)
	  1800  ;  expire (30 minutes)
	  10  ; minimum (10 seconds)
	)
	 NS master.xxx.yyy.com.
	$ORIGIN xxx.yyy.com.
	test A 10.x1.y1,z1
	* A 10.x1.y1,z1
	
	保存，退出。
	
	vi yyy.com.db
	
	$ORIGIN  .
	$TTL 1  ;  1 seconds (for testing only)
	yyy.com IN SOA master.yyy.com.  root.yyy.com.  (
	  2011112904  ;  serial
	  60  ;  refresh (1 minute)
	  15  ;  retry (15 seconds)
	  1800  ;  expire (30 minutes)
	  10  ; minimum (10 seconds)
	)
	 NS master.yyy.com.
	$ORIGIN yyy.com.
	master A 10.x2.y2.z2
	root A 10.x2.y2.z2
	test A 10.x2.y2.z2
	infranode A 10.x1.y1.z1
	node A 10.x3.y3.z3
	
	保存，退出。
	
	chgrp named -R /var/named
	chown named -R /var/named/zones
	restorecon -R /var/named
	chown root:named /etc/named.conf
	restorecon /etc/named.conf
	
	3、重启named服务
	
	systemctl enable named
	systemctl start named
	
	systemctl restart network
	
	4、用nslookup命令验证
	
	nslookup master.openshift.com
	
	说明：本文只提供了正向解析，即域名 -> IP地址的解析，反向的IP地址 -> 域名的解析可以参考文献资料
	
	5、附上本机部分配置
	
	[root@master ~]# cat /etc/hosts
	127.0.0.1    localhost localhost.localdomain localhost4 localhost4.localdomain4
	::1          localhost localhost.localdomain localhost6 localhost6.localdomain6
	10.x1.y1,z1 master.openshift.com
	10.x2.y2.z2 infranode.openshift.com
	10.x3.y3.z3 node.openshift.com
	[root@master ~]# cat /etc/hostname
	master.openshift.com
	[root@master ~]# cat /etc/resolv.conf
	# Generated by NetworkManager
	search openshift.com/opt/docker/registry
	nameserver 10.67.1.9
	nameserver 10.67.1.10
	
	6、参考文献：
	http://heylinux.com/archives/3308.html
	http://server.zzidc.com/fwqpz/546.html
	
**41、Terminator使用**
	
	1、在Ubuntu命令行安装Terminator的命令：
	
		sudo apt-get install terminator
		
	2、安装好之后，系统默认是Ctrl + Alt + T 快速打开Terminator界面，在界面右键打开首选项，把字体配置成 Ubuntu Mono 12。
	
	3、Terminator 最大的用途就是多屏组合在一起
	
		Ctrl + Shift + E 垂直分割窗口
		
		Ctrl + Shift + O水平分割窗口
		
		Ctrl + Shift + N在分割的各个窗口自由切换
		关闭多个终端窗口

	4、在Terminator窗口中添加的若干终端窗口该如何关闭呢?我们可以在每个终端窗口的命令行提示符后输入命令“exit”来关闭该窗口，也可以通过点击每个终端窗口的右键快捷菜单中的“close”项来关闭该终端窗口。
** 42、惠普打印机驱动 **

	http://www.hplipopensource.com/hplip-web/install_wizard/index.html
    
    http://scateu.me/2014/01/07/ubuntu-debian-install-hp-printer.html
	
** 43、rancher/server安装配置 **

	docker pull rancher/server:latest
	
	sudo mkdir -p /opt/docker/rancher-server

	sudo mkdir -p /opt/docker/rancher-server/mysql

	sudo mkdir -p /opt/docker/rancher-server/rancher
	
    docker run -d -v /opt/docker/rancher-server/mysql/:/var/lib/mysql/ --restart=always -p 10.200.176.60:8080:8080 --name rancher-server rancher/server:latest
	
	sudo docker run --rm --privileged -v /var/run/docker.sock:/var/run/docker.sock -v /opt/docker/rancher-server/rancher:/var/lib/rancher rancher/agent:v1.2.5 http://10.200.176.60:8080/v1/scripts/C1DE79BD8C2F512DE3D2:1483142400000:25OOJNE4kMFidHOQXEe8lgnefQU
	
	参考：
	http://www.linuxidc.com/Linux/2016-04/130603.htm
    http://mt.sohu.com/20160926/n469179917.shtml

** 44、Ubuntu下安装和卸载Nodepadqq**

    对于Ubuntu发行版本可以通过PPA安装，命令如下：

    sudo add-apt-repository ppa:notepadqq-team/notepadqq
    sudo apt-get update
    sudo apt-get install notepadqq

    类似的，卸载命令如下：

    sudo apt-get purge notepadqq
    sudo add-apt-repository --remove ppa:notepadqq-team/notepadqq

** 45、Linux下硬链接和软链接的区别和用法**

	实例：ln -s /home/gamestat    /gamestat

    linux下的软链接类似于windows下的快捷方式

    ln -s a b 中的 a 就是源文件[目录]，b是链接文件[目录]，其作用是当进入b目录，实际上是链接进入了a目录

    如上面的示例，当我们执行命令   cd /gamestat/的时候  实际上是进入了 /home/gamestat/

    值得注意的是执行命令的时候,应该是a目录已经建立，目录b没有建立。我最开始操作的是也把b目录给建立了，结果就不对了

    删除软链接：

       rm -rf  b  注意不是rm -rf  b/

    ln  a b 是建立硬链接

    区别：
    1.硬链接原文件/链接文件公用一个inode号，说明他们是同一个文件，而软链接原文件/链接文件拥有不同的inode号，表明他们是两个不同的文件；
    2.在文件属性上软链接明确写出了是链接文件，而硬链接没有写出来，因为在本质上硬链接文件和原文件是完全平等关系；
    3.链接数目是不一样的，软链接的链接数目不会增加；
    4.文件大小是不一样的，硬链接文件显示的大小是跟原文件是一样的。而这里软链接显示的大小与原文件就不同了，BBB大小是95B，而BBBsoft是3B。因为BBB共有3个字符
    5.软链接没有任何文件系统的限制，任何用户可以创建指向目录的符号链接
    6.不允许给目录创建硬链接。
	7.不可以在不同文件系统的文件间建立链接。因为 inode 是这个文件在当前分区中的索引值，是相对于这个分区的，当然不能跨越文件系统了。
    8.软链接克服了硬链接的不足，没有任何文件系统的限制，任何用户可以创建指向目录的符号链接。因而现在更为广泛使用，它具有更大的灵活性，甚至可以跨越不同机器、不同网络对文件进行链接。
    总之，建立软链接就是建立了一个新文件。当访问链接文件时，系统就会发现他是个链接文件，它读取链接文件找到真正要访问的文件。

    当然软链接也有硬链接没有的缺点：因为链接文件包含有原文件的路径信息，所以当原文件从一个目录下移到其他目录中，再访问链接文件，系统就找不到了，而硬链接就没有这个缺陷，你想怎么移就怎么移；还有它要系统分配额外的空间用于建立新的索引节点和保存原文件的路径。

    参考：
    www.cnblogs.com/itech/archive/2009/04/10/1433052.html
	
** 46、Linux下硬链接和软链接的区别和用法**
    APT
    # make sure you have 'apt-transport-https' installed
    dpkg -s apt-transport-https > /dev/null || bash -c "sudo apt-get update; sudo apt-get install apt-transport-https -y"
    curl https://repo.skype.com/data/SKYPE-GPG-KEY | sudo apt-key add -
    echo "deb [arch=amd64] https://repo.skype.com/deb stable main" | sudo tee /etc/apt/sources.list.d/skype-stable.list
    sudo apt-get update
    sudo apt-get install skypeforlinux -y
    Yum
    sudo yum-config-manager --add-repo=https://repo.skype.com/data/skype-stable.repo
    sudo yum install skypeforlinux -y
    DNF
    sudo dnf config-manager --add-repo https://repo.skype.com/data/skype-stable.repo
    sudo dnf install skypeforlinux -y
    Zypper
    sudo zypper ar -f https://repo.skype.com/rpm/stable skypeforlinux
    sudo zypper update
    sudo zypper install skypeforlinux
    
    参考：
    https://repo.skype.com
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	