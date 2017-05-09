package com.netease.idc.strategy

import com.netease.idc.feature.data.UPConstant.{UPKey, UPSource}

import scala.collection.mutable

/**
  * Created by hutianqi on 17/4/6.
  *
  * 用于对值为枚举类型进行比较的策略
  * 计算结果中包含了具体枚举结果的计算精确度
  *
  *
  */
class EqualEnumStrategy  extends IStrategy{

  def handle(resultMap : mutable.Map[String, BigInt] , key: UPKey, source: UPSource, stdValue: Any, upValue: Any): Map[String, BigInt] = {

    count(resultMap , generateKey(key , source , stdValue))
    count(resultMap , generateKey(key , source))

    if (stdValue == upValue) {
      count(resultMap , generateKeyTrue(key , source , stdValue))
      count(resultMap , generateKeyTrue(key , source))
    }
    resultMap.toMap
  }


  def count(map: mutable.Map[String, BigInt], targetValue: String): Unit = {
    val count: BigInt = map.getOrElse(targetValue, 0)
    map(targetValue) = count + 1l
  }

  //字符串拼接生成Key
  def generateKey(key: UPKey, source: UPSource, stdValue: Any): String = {
    key + "_" + stdValue + ":" + source
  }

  def generateKey(key: UPKey, source: UPSource): String = {
    key + ":" + source
  }

  def generateKeyTrue(key: UPKey, source: UPSource): String = {
    key + "_True" + ":" + source
  }

  def generateKeyTrue(key: UPKey, source: UPSource, stdValue: Any): String = {
    key + "_" + stdValue + "_True" + ":" + source
  }
}
