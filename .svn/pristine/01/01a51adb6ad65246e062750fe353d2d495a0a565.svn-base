package com.netease.idc.strategy

import com.netease.idc.feature.data.UPConstant.{UPKey, UPSource}
import com.netease.idc.feature.metadata.PropertyMetadata.PropertyType
import org.json4s.jackson.JsonMethods._

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
  * Created by hutianqi on 17/3/31.
  *
  * 策略类
  *
  *
  */
abstract class IStrategy {
  def use(resultMap : mutable.Map[String, BigInt] , key : UPKey , source : UPSource , valueType :PropertyType , stdValue: Any, upValue: Any): Map[String, BigInt] ={
    valueType match{
      case PropertyType.PT_STRING
           | PropertyType.PT_BOOLEAN
           | PropertyType.PT_INT
           | PropertyType.PT_DOUBLE =>
        handle(resultMap , key , source , stdValue , upValue)
      case PropertyType.PT_ARRAY_INT
           | PropertyType.PT_ARRAY_STRING =>
        handle(resultMap , key , source , jsonArray2List(stdValue) , jsonArray2List(upValue))
      case PropertyType.PT_MAP_INT
           | PropertyType.PT_MAP_STRING =>
        handle(resultMap ,key , source , stdValue.asInstanceOf[java.util.Map[String , Object]].toMap , upValue.asInstanceOf[java.util.Map[String , Object]].toMap)
      case _ => null
    }
  }

  def handle(resultMap : mutable.Map[String, BigInt] , key : UPKey , source : UPSource , stdValue: Any, upValue: Any): Map[String, BigInt]

  def jsonArray2List(str : Any) : List[Any] = {
    parse(str.toString).values.asInstanceOf[List[Any]]
  }
}
