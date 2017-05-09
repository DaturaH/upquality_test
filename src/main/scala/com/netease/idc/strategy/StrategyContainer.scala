package com.netease.idc.strategy

import com.netease.idc.feature.data.UPConstant.UPKey
import org.springframework.context.support.ClassPathXmlApplicationContext

import scala.collection.JavaConversions._

/**
  * Created by hutianqi on 17/3/31.
  *
  * 对策略类进行管理
  *
  */

object StrategyContainer {

  val sourceContext = new ClassPathXmlApplicationContext("strategies.xml")
  val strategyContainer : Map[UPKey , IStrategy] = sourceContext.getBeansOfType(classOf[IStrategy]).map(x=>(UPKey.valueOf(x._1) -> x._2)).toMap

}
