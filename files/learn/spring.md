####Spring
	七大模块：
	
	核心容器：核心容器提供 Spring 框架的基本功能。核心容器的主要组件是 BeanFactory，它是工厂模式的实现。BeanFactory 使用控制反转 （IOC） 模式将应用程序的配置和依赖性规范与实际的应用程序代码分开。
	
	Spring 上下文：Spring 上下文是一个配置文件，向 Spring 框架提供上下文信息。Spring 上下文包括企业服务，例如 JNDI、EJB、电子邮件、国际化、校验和调度功能。
	
	Spring AOP：通过配置管理特性，Spring AOP 模块直接将面向方面的编程功能集成到了 Spring 框架中。所以，可以很容易地使 Spring 框架管理的任何对象支持 AOP。Spring AOP 模块为基于 Spring 的应用程序中的对象提供了事务管理服务。通过使用 Spring AOP，不用依赖 EJB 组件，就可以将声明性事务管理集成到应用程序中。
	
	Spring DAO：JDBC DAO 抽象层提供了有意义的异常层次结构，可用该结构来管理异常处理和不同数据库供应商抛出的错误消息。异常层次结构简化了错误处理，并且极大地降低了需要编写的异常代码数量（例如打开和关闭连接）。Spring DAO 的面向 JDBC 的异常遵从通用的 DAO 异常层次结构。
	
	Spring ORM：Spring 框架插入了若干个 ORM 框架，从而提供了 ORM 的对象关系工具，其中包括 JDO、Hibernate 和 iBatis SQL Map。所有这些都遵从 Spring 的通用事务和 DAO 异常层次结构。
	
	Spring Web 模块：Web 上下文模块建立在应用程序上下文模块之上，为基于 Web 的应用程序提供了上下文。所以，Spring 框架支持与 Jakarta Struts 的集成。Web 模块还简化了处理多部分请求以及将请求参数绑定到域对象的工作。
	
	Spring MVC 框架：MVC 框架是一个全功能的构建 Web 应用程序的 MVC 实现。通过策略接口，MVC 框架变成为高度可配置的，MVC 容纳了大量视图技术，其中包括 JSP、Velocity、Tiles、iText 和 POI。
	
	IOC 和 AOP

	控制反转模式（也称作依赖性介入）的基本概念是：不创建对象，但是描述创建它们的方式。在代码中不直接与对象和服务连接，但在配置文件中描述哪一个组件需要哪一项服务。容器 （在 Spring 框架中是 IOC 容器） 负责将这些联系在一起。
	
	面向方面的编程，即 AOP，是一种编程技术，它允许程序员对横切关注点或横切典型的职责分界线的行为（例如日志和事务管理）进行模块化。AOP 的核心构造是方面，它将那些影响多个类的行为封装到可重用的模块中。

	AOP 和 IOC 是补充性的技术，它们都运用模块化方式解决企业应用程序开发中的复杂问题。在典型的面向对象开发方式中，可能要将日志记录语句放在所有方法和 Java 类中才能实现日志功能。在 AOP 方式中，可以反过来将日志服务模块化，并以声明的方式将它们应用到需要日志的组件上。当然，优势就是 Java 类不需要知道日志服务的存在，也不需要考虑相关的代码。所以，用 Spring AOP 编写的应用程序代码是松散耦合的。

	AOP 的功能完全集成到了 Spring 事务管理、日志和其他各种特性的上下文中。
	
	控制反转IoC(Inversion of Control)，是一种设计思想，DI(依赖注入)是实现IoC的一种方法，也有人认为DI只是IoC的另一种说法。控制的什么被反转了？就是：获得依赖对象的方式反转了。
	
	Spring实现IoC的多种方式：
	
	装配注解主要有：@Autowired、@Qualifier、@Resource，它们的特点是：

	1、@Resource默认是按照名称来装配注入的，只有当找不到与名称匹配的bean才会按照类型来装配注入；

	2、@Autowired默认是按照类型装配注入的，如果想按照名称来转配注入，则需要结合@Qualifier一起使用；

	3、@Resource注解是又J2EE提供，而@Autowired是由spring提供，故减少系统对spring的依赖建议使用@Resource的方式；如果Maven项目是1.5的JRE则需换成更高版本的。

	4、@Resource和@Autowired都可以书写注解在字段或者该字段的setter方法之上，@Autowired是Spring的注解，@Resource是J2EE的注解。

	5、@Autowired 可以对成员变量、方法以及构造函数进行注释，而 @Qualifier 的注解对象是成员变量、方法入参、构造函数入参。

	6、@Qualifier("XXX") 中的 XX是 Bean 的名称，所以 @Autowired 和 @Qualifier 结合使用时，自动注入的策略就从 byType 转变成 byName 了。

	7、@Autowired 注释进行自动注入时，Spring 容器中匹配的候选 Bean 数目必须有且仅有一个，通过属性required可以设置非必要。

	8、@Resource装配顺序
	
	　　8.1. 如果同时指定了name和type，则从Spring上下文中找到唯一匹配的bean进行装配，找不到则抛出异常；
	　　
	　　8.2. 如果指定了name，则从上下文中查找名称（id）匹配的bean进行装配，找不到则抛出异常；
	　　
	　　8.3. 如果指定了type，则从上下文中找到类型匹配的唯一bean进行装配，找不到或者找到多个，都会抛出异常；
	　　
	　　8.4. 如果既没有指定name，又没有指定type，则自动按照byName方式进行装配；如果没有匹配，则回退为一个原始类型进行匹配，如果匹配则自动装配。
	
	9、Spring常用注解汇总：

		使用注解之前要开启自动扫描功能，其中base-package为需要扫描的包(含子包)。

		<context:component-scan base-package="cn.test"/> 

		@Configuration把一个类作为一个IoC容器，它的某个方法头上如果注册了@Bean，就会作为这个Spring容器中的Bean。
		@Scope注解 作用域
		@Lazy(true) 表示延迟初始化
		@Service用于标注业务层组件
		@Controller用于标注控制层组件（如struts中的action）
		@Repository用于标注数据访问组件，即DAO组件。
		@Component泛指组件，当组件不好归类的时候，我们可以使用这个注解进行标注。
		@Scope用于指定scope作用域的（用在类上）
		@PostConstruct用于指定初始化方法（用在方法上）
		@PreDestory用于指定销毁方法（用在方法上）
		@DependsOn：定义Bean初始化及销毁时的顺序
		@Primary：自动装配时当出现多个Bean候选者时，被注解为@Primary的Bean将作为首选者，否则将抛出异常
		@Autowired 默认按类型装配，如果我们想使用按名称装配，可以结合@Qualifier注解一起使用。如下：
		@Autowired @Qualifier("personDaoBean") 存在多个实例配合使用
		@Resource默认按名称装配，当找不到与名称匹配的bean才会按类型装配。
		@PostConstruct 初始化注解
		@PreDestroy 摧毁注解 默认 单例  启动就加载
		@Async异步方法调用
	
	Spring的4种关键策略：
		
		1. 基于POJO（Plain Old Java Object）的轻量级和最小侵入性编程。
		
		2. 通过依赖注入（Dependency Injection， DI）和面向接口实现松耦合。
		
		3. 基于切面和惯例进行声明式编程。
		
		4. 通过切面和模板减少样板式代码。
		
	Spring webservice
	
		Endpoint:	Indicates that an annotated class is an "Endpoint" (e.g. a web service endpoint)
	
		PayloadRoot:    Marks an endpoint method as the handler for an incoming request
	
		XPathParam:	Indicates that a method parameter should be bound to an XPath expression
	
	Spring框架四大原则：
	
	    1、使用POJO进行轻量级和最小侵入式开发。
	    
	    2、通过依赖注入和基于接口编程实现松耦合。
	    
	    3、通过AOP和默认习惯进行声明式编程。
	    
	    4、使用AOP和模板减少模式化代码。
	
	参考：
	http://blog.csdn.net/qq_22654611/article/details/52606960
	http://www.cnblogs.com/best/p/5727935.html
	http://blog.csdn.net/xyh820/article/details/7303330/
	http://www.cnblogs.com/qq78292959/p/3716827.html
	http://www.cnblogs.com/xiaoxi/p/5935009.html
	