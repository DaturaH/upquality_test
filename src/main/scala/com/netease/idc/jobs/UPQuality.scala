package com.netease.idc.jobs

import java.text.SimpleDateFormat
import java.util.{Date, Properties}

import com.hadoop.mapreduce.LzoTextInputFormat
import com.netease.idc.feature.data.UPConstant.{UPKey, UPSource}
import com.netease.idc.feature.data.{PropertyValue, UserProperty}
import com.netease.idc.feature.metadata.PropertyMetadata.PropertyType
import com.netease.idc.strategy.StrategyContainer
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SQLContext, SaveMode}
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
  * Created by hutianqi on 17/3/30.
  *
  * UPQuality的目标是在UserProperty数据的基础之上，
  * 对UP库中各个字段属性与基准库中的数据进行对比，检验UP库中数据的正确性
  *
  *
  */
object UPQuality {

  val log = LoggerFactory.getLogger(UPQuality.getClass)
  val  dateFormat:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
  val now = dateFormat.format(new Date())

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("UPQuality")
    val sc = new SparkContext(sparkConf)

    val stdInput = args(0)
    var UPInput = args(1)
    val output = args(2)

    var partitionNum: Int = 1   //默认partition个数设为8011
    if (args.length == 4) {
      partitionNum = args(3).toInt
    }
    if (args.length == 5) {
      val partition = args(4)
      UPInput = UPInput + "/" + partition //如果知道具体分区,可以对UP路径进行优化,减少数据输入量
    }

    log.info(s"using standardInput : $stdInput")
    log.info(s"using UPInput : $UPInput")
    log.info(s"using output : $output")
    log.info(s"using partitionNum : $partitionNum")

    val UPRDD: RDD[(String, String)] = sc.newAPIHadoopFile[LongWritable, Text, LzoTextInputFormat](UPInput).map(_._2.toString).map(x => (parseUP(x).getId, x)).filter(_._1 != "error")
    val stdRDD: RDD[(String, String)] = sc.newAPIHadoopFile[LongWritable, Text, LzoTextInputFormat](stdInput).map(_._2.toString).map(x => (parseUP(x).getId, x)).filter(_._1 != "error")

    val result: Map[String, BigInt] = stdRDD.join(UPRDD, partitionNum).mapPartitions(
      partition => Iterator(compareOfEveryPartition(partition))).reduce((x, y) => mapValuesCount(x, y))

    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._

    //创建Properties存储数据库相关属性
    val prop = new Properties()
    prop.put("user", "AAA")
    prop.put("password", "BBB")
    sc.parallelize(getAccuracy(result).toList).sortBy(_.UPKey).coalesce(1, shuffle = true).toDF.write.mode(SaveMode.Append).jdbc("jdbc:mysql://127.0.0.1:3306/CCC", "CCC.DDD", prop)

    sc.stop()
  }


  def parseUP(str: String): UserProperty = {
    val up = new UserProperty
    try {
      up.parseFromJSONStr(str)
    } catch {
      case ex: Exception => log.error("up parse error : " + str)
        up.setId("error")
        return up
    }
    up
  }


  def compare(resultMap : mutable.Map[String , BigInt] , stdStr: String, upStr: String): Map[String, BigInt] = {
    val std = parseUP(stdStr)
    val up = parseUP(upStr)
    for (pv: PropertyValue <- std.getPropsRefList) {
      val key = pv.getKeyId
      val stdValue = pv.getValue
      val upPropLists = up.getAllValuesOfKey(key)
      for(upPropertyValue <- upPropLists){
        val source = upPropertyValue.getSourceId
        val valueType = upPropertyValue.getValueType
        val upValue = upPropertyValue.getValue
        comparePV(resultMap , key, source, valueType, stdValue, upValue)
      }
    }
    resultMap.toMap
  }

  def comparePV(resultMap : mutable.Map[String, BigInt] , key: UPKey, source: UPSource, valueType: PropertyType, stdValue: AnyRef, upValue: AnyRef): Map[String, BigInt] = {
    val map = StrategyContainer.strategyContainer
    if (StrategyContainer.strategyContainer.contains(key)) {
      return StrategyContainer.strategyContainer(key).use(resultMap , key, source, valueType, stdValue, upValue)
    }
    null
  }

  def compareOfEveryPartition(input: Iterator[(String, (String, String))]): Map[String, BigInt] = {
    val resultMap: mutable.Map[String, BigInt] = mutable.Map()
    input.foreach {
      elem =>
        compare(resultMap , elem._2._1, elem._2._2)
    }
    resultMap.toMap
  }

  //两个map相同key累加,生成一个map
  def mapValuesCount(mapLeft: Map[String, BigInt], mapRight: Map[String, BigInt]): Map[String, BigInt] = {
    val resultMap: mutable.Map[String, BigInt] = collection.mutable.Map(mapLeft.toSeq: _*)
    if (mapRight != null) {
      for ((k, v) <- mapRight) {
        resultMap(k) = mapLeft.getOrElse(k, BigInt(0)) + mapRight.getOrElse(k, BigInt(0))
      }
    }
    resultMap.toMap
  }

  //最终数据输出字段
  case class Result(UPKey: String, UPSource: String, accuracy: String, accnum: String , day : String)

  //计算各个字段各个结果的正确率,保留2位小数
  def getAccuracy(map: Map[String, BigInt]): Set[Result] = {
    val resultSet: mutable.Set[Result] = mutable.Set()
    for ((k, v) <- map) {
      if (k.contains("True")) {
        val key = k.split(":")(0)
        val source = k.split(":")(1)
        val index = k.indexOf("_True")
        val num: String = map(k).toString()
        val accuracy = "%1.2f".format(map(k).toDouble / map(k.substring(0, index) + ":" + source).toDouble * 100) + "%"
        val result: Result = Result(key, source, accuracy, num , now)
        resultSet.add(result)
      }
    }
    resultSet.toSet
  }
}