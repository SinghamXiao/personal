/**
  * 使用Scala开发集群运行的Spark WordCount程序
  * Created by Yuanxiao on 9/28/16.
  */

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

object WordCount {
  def wordCount(filePath: String): Unit = {
    /**
      * 第一步：创建Spark的配置对象SparkConf，设置Spark程序的运行时的配置信息
      * 例如说通过setMaster来设置程序要链接的Spark集群的Master的URL，如果设置为local，
      * 则代表Spark程序在本地运行，特别适合机器配置条件差的初学者。
      */

    val conf = new SparkConf()//创建SparkConf对象
    conf.setAppName("My First Spark App!")//设置应用程序的名称，在程序运行的监控界面可以看到名称
    //conf.setMaster("spark://spark-master:7077")//程序此时运行在Spark集群

    /**
      * 第二步：创建SparkContext对象，
      * SparkContext是Spark程序所有功能的唯一入口，无论是采用Scala、Java、Python、R等都必须有一个SparkContext
      * SparkContext的核心作用：初始化Spark应用程序运行所需要的核心组件，包括DAGScheduler、TaskScheduler、SchedulerBacken
      * 同时还会负责Spark程序往Master注册程序等
      * SparkContext是整个Spark应用程序中至关重要的一个对象
      */

    val sc = new SparkContext(conf)//通过创建SparkContext对象，通过传入SparkConf实例来定制Spark运行的具体参数和配置信息

    /**
      * 第三步：根据具体的数据来源(HDFS、HBase、Local FS、S3)通过SparkContext来创建RDD
      * RDD的创建基本有三种方式：根据外部的数据来源(例如HDFS)、根据Scala集合、由其他的RDD操作
      * 数据会被RDD划分称为一些列的Partitions，分配到每个Partition的数据属于一个Task的处理范畴
      */

    val lines: RDD[String] = sc.textFile(filePath, 1)

    /**
      * 第四步：对初始的RDD进行Transformation级别的处理，例如map、filter等高阶函数的编程，来进行具体的数据计算
      * 第4.1步：将每一行的字符串拆分成单个的单词
      */

    val words = lines.flatMap { line => line.split(" ")}//对每一行的字符串进行单词切分，并把所有行的切分结果通过flat合并成一个大的单词集合

    /**
      * 第四步：对初始的RDD进行Transformation级别的处理，例如map、filter等高阶函数的编程，来进行具体的数据计算
      * 第4.2步：在单词切分的基础上，对每个单词实例的计数为1，也就是word=>(word,1)
      */

    val pairs = words.map { word => (word, 1) }

    /**
      * 第四步：对初始的RDD进行Transformation级别的处理，例如map、filter等高阶函数的编程，来进行具体的数据计算
      * 第4.3步：在每个单词实例计数为1的基础之上统计每个单词在文件中出现的总次数
      */

    val wordCounts = pairs.reduceByKey(_+_)//对相同的Key，进行Value的累计(包括Local和Reducer级别同时Reduce)
    wordCounts.collect.foreach(wordNumberPair => println(wordNumberPair._1 + " : "+wordNumberPair._2))

    /**
      * 第五步：关闭SparkContext对象
      */

    sc.stop()
  }
}
