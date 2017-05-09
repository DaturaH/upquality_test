package com.netease.idc.strategy

import com.netease.idc.feature.data.UPConstant.{UPKey, UPSource}

import scala.collection.mutable

/**
  * Created by hutianqi on 17-5-4.
  *
  * bsYear进行比较，与标准集相差3岁均为正确
  *
  */
class BsYearEqualStrategy extends IStrategy{

  def handle(resultMap : mutable.Map[String, BigInt] , key: UPKey, source: UPSource, stdValue: Any, upValue: Any): Map[String, BigInt] = {
    count(resultMap, generateKey(key, source))

    if ((stdValue.asInstanceOf[Int]/10000 - upValue.asInstanceOf[Int]/10000).abs <= 3) {
      count(resultMap, generateKeyTrue(key, source))
    }
    resultMap.toMap
  }


  def count(map: mutable.Map[String, BigInt], targetValue: String): Unit = {
    val count: BigInt = map.getOrElse(targetValue, 0)
    map(targetValue) = count + 1l
  }

  //字符串拼接生成Key
  def generateKey(key: UPKey, source: UPSource): String = {
    key + ":" + source
  }

  def generateKeyTrue(key: UPKey, source: UPSource): String = {
    key + "_True" + ":" + source
  }
}
