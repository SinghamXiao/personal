import org.junit.runner.RunWith
import org.scalatest.{FunSpec, Matchers}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class WordCountTest extends FunSpec with Matchers {
  describe("Spark Word Count Test") {
    it("Word Count") {
      println("Hello World!")
      WordCount.wordCount("test.txt")
    }
  }
}
