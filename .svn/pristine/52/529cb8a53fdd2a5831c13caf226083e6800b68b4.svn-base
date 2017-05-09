package com.netease.idc.strategy

import com.alibaba.fastjson.JSON
import com.netease.idc.feature.data.PropertyValue
import com.netease.idc.feature.data.UPConstant.{UPKey, UPSource}
import org.json4s.jackson.JsonMethods._
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
  * Created by hutianqi on 17/4/6.
  */
class TestEqualEnumStrategy  extends FunSpec with Matchers{
  describe("test EqualStrategy"){

    val equalEnumStrategy = new EqualEnumStrategy
    val source = UPSource.hyIp

    val resultMap : mutable.Map[String , BigInt] = mutable.Map()

    it("test Int"){
      //true
      val key = UPKey.bsYear
      var stdValue : Any = 1
      val upValue : Any = 1.asInstanceOf[java.lang.Object]
      equalEnumStrategy.handle(resultMap , key , source , stdValue, upValue)
      var targetMap = Map(
      "bsYear_1:hyIp" -> 1,
      "bsYear:hyIp" -> 1,
      "bsYear_True:hyIp" -> 1,
      "bsYear_1_True:hyIp" -> 1
      )
      assert(resultMap == targetMap)
      resultMap.clear()

      //false
      stdValue = 2
      equalEnumStrategy.handle(resultMap , key , source , stdValue, upValue)
      targetMap = Map(
        "bsYear:hyIp" -> 1 ,
        "bsYear_2:hyIp" -> 1
      )
      assert(resultMap == targetMap)
      resultMap.clear()
    }


    it("test String"){
      val resultMap : mutable.Map[String , BigInt] = mutable.Map()

      val key = UPKey.gender
      val stdValue = "F"
      val upValue = "M"
      equalEnumStrategy.handle(resultMap , key , source , stdValue, upValue)
      val targetMap = Map(
        "gender_F:hyIp" -> 1 ,
        "gender:hyIp" -> 1
      )
      assert(resultMap == targetMap)
      resultMap.clear()
    }

    it("test array"){
      val stdValue : Any = List(2,3)
      val upValue : List[Any] = jsonArray2List(JSON.parseArray("[2,3]"))
      assert(stdValue == upValue)
    }


    it("test map"){
      val key = UPKey.gender
      val stdPV = new PropertyValue()
      stdPV.setKeyId(key)
      stdPV.setSourceId(source)
      val stdMap : java.util.Map[String , Int] = scala.collection.mutable.Map().asInstanceOf[mutable.Map[String , Int]]
      stdMap.put("1" ,1)
      stdPV.setValue(stdMap)

      val pv = new PropertyValue()
      val map : java.util.Map[String , Int] = scala.collection.mutable.Map().asInstanceOf[mutable.Map[String , Int]]
      map.put("1" ,1)
      pv.setValue(map)
      equalEnumStrategy.handle(resultMap , key , source , stdPV.getValue.asInstanceOf[java.util.Map[String , Object]].toMap , pv.getValue.asInstanceOf[java.util.Map[String , Object]].toMap).foreach(println)
      resultMap.clear()
    }


    it("test GenderStrategy.handle()") {
      val key = UPKey.gender
      val stdValue = "F"
      var upValue = "M"
      var map = equalEnumStrategy.handle(resultMap , key , source , stdValue, upValue)
      val valueDiffMap = Map("gender_F:hyIp" -> BigInt(1), "gender:hyIp" -> BigInt(1))
      assert(map == valueDiffMap)

      upValue = "F"
      map = equalEnumStrategy.handle(resultMap , key , source , stdValue, upValue)
      val valueSameMap : Map[String , BigInt]= Map("gender_F_True:hyIp" -> 1, "gender_F:hyIp" -> 2, "gender_True:hyIp" -> 1, "gender:hyIp" -> 2)
      assert(map == valueSameMap)

    }

    it("test GenderStrategy.count()") {
      val map = mutable.Map("1" -> BigInt(1), "2" -> BigInt(2))
      equalEnumStrategy.count(map, "1")
      val targetMap = mutable.Map("1" -> BigInt(2), "2" -> BigInt(2))
      assert(map == targetMap)
    }

  }


  def jsonArray2List(str : Any) : List[Any] = {
     parse(str.toString).values.asInstanceOf[List[Any]]
  }
}
