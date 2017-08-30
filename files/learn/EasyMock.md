#### EasyMock
	通过 EasyMock，我们可以为指定的接口动态的创建 Mock 对象，并利用 Mock 对象来模拟协同模块或是领域对象，从而使单元测试顺利进行。这个过程大致可以划分为以下几个步骤：

	1. 使用 EasyMock 生成 Mock 对象；
	使用 EasyMock 动态构建 ResultSet 接口的 Mock 对象来解决这个问题。一些简单的测试用例只需要一个 Mock 对象，这时，我们可以用以下的方法来创建 Mock 对象：
	
		ResultSet mockResultSet = createMock(ResultSet.class);
		
	其中 createMock 是 org.easymock.EasyMock 类所提供的静态方法。
	
	如果需要在相对复杂的测试用例中使用多个 Mock 对象，EasyMock 提供了另外一种生成和管理 Mock 对象的机制：

		IMocksControl control = EasyMock.createControl();
		java.sql.Connection mockConnection = control.createMock(Connection.class);
		java.sql.Statement mockStatement = control.createMock(Statement.class);
		java.sql.ResultSet mockResultSet = control.createMock(ResultSet.class);

EasyMock 类的 createControl 方法能创建一个接口 IMocksControl 的对象，该对象能创建并管理多个 Mock 对象。如果需要在测试中使用多个 Mock 对象，我们推荐您使用这一机制，因为它在多个 Mock 对象的管理上提供了相对便捷的方法。

	2. 设定 Mock 对象的预期行为和输出；
	
	3. 将 Mock 对象切换到 Replay 状态；
	
	4. 调用 Mock 对象方法进行单元测试；
	
	5. 对 Mock 对象的行为进行验证。
	
	1). expect() 是录制 Mock 对象方法的调用，其参数就是 Mock 对象的方法，其中如果调用的方法有返回值，要通过 andReturn() 方法设置预期的返回值。
	
	2). replay() 是结束录制过程。 在调用 replay() 方法之前的状态， EashMock 称之为 “record 状态 ” 。该状态下， Mock 对象不具备行为（即模拟接口的实现），它仅仅记录方法的调用。在调用 replay() 后，它才以 Mock 对象预期的行为进行工作，检查预期的方法调用是否真的完成。
	
	3). verify() 是用于在录制和回放两个步骤完成之后进行预期和实际结果的检查。这里就是检查 accountDAOMock 是否如预期一样调用了 getByNameAndPwd 方法。