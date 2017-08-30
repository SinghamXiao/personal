#### Junit4

	Test Suite的用法

	1、Suit ----- 它可以一次性执行多个类中的测试用例。例如，
	
	import org.junit.Test;
	import org.junit.runner.RunWith;
	import org.junit.runners.Suite;

	@RunWith(Suite.class)
	@Suite.SuiteClasses({
		RedisRateTest.class,
		RedisInventoryTest.class,
		RedisAvailStatusTest.class,
		RedisHotelTimeZoneTest.class})
	public class RedisInputDataTest {
	    @Test
	    public void test() {
	    }
	}