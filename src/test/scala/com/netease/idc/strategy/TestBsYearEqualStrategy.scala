package com.netease.idc.strategy

import com.netease.idc.feature.data.UPConstant.{UPKey, UPSource}
import org.scalatest.{FunSpec, Matchers}

import scala.collection.mutable

/**
  * Created by hutianqi on 17-5-4.
  */
class TestBsYearEqualStrategy  extends FunSpec with Matchers {
  describe("test EqualStrategy"){

    val strategy = new BsYearEqualStrategy
    val key = UPKey.bsYear
    val source = UPSource.kaola

    it("test handle() true") {
      val resultMap : mutable.Map[String , BigInt] = mutable.Map()

        val stdValue : Any = 20160000
        val upValue : Any = 20131221
        strategy.handle(resultMap , key , source , stdValue , upValue)
//        resultMap.foreach(println)
        assert(resultMap.contains("bsYear_True:kaola"))
    }

    it("test handle() false") {
      val resultMap : mutable.Map[String , BigInt] = mutable.Map()

      val stdValue : Any = 19910201
      val upValue : Any = 19951212
      strategy.handle(resultMap , key , source , stdValue , upValue)
      assert(!resultMap.contains("bsYear_True:kaola"))
    }
  }
}
