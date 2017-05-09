package com.netease.idc.jobs

import java.text.SimpleDateFormat
import java.util.Date

import com.netease.idc.jobs.UPQuality.Result
import org.scalatest.{FunSpec, Matchers}

/**
  * Created by hutianqi on 17/3/30.
  */
class TestUPQuality extends FunSpec with Matchers {

  val dateFormat:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
  val now = dateFormat.format(new Date())

  describe("test UPQuality.......") {
    it("test main") {
      val standardInput = s"/home/hutianqi/test/map1"
      val UPInput = s"/home/hutianqi/test/map2"
      val output = s"/home/hutianqi/test/map1_reduce"
      val args: Array[String] = Array(standardInput, UPInput, output)
//      UPQuality.main(args)
    }

    it("test UPQuality.mapValuesCount()") {
      val mapLeft: Map[String, BigInt] = Map("1" -> 1, "2" -> 2, "3" -> 3)
      val mapRight: Map[String, BigInt] = Map("1" -> 1, "2" -> 2, "4" -> 4)
      var result: Map[String, BigInt] = Map()
      result = UPQuality.mapValuesCount(result, mapRight)
      UPQuality.mapValuesCount(result, mapLeft)

      val target : Map[String, BigInt] = Map("2" -> 2 , "1" -> 1 , "4" ->4)
      assert(result == target)
    }

    it("test UPQuality.getAccuracy()") {
      val map: Map[String, BigInt] = Map("Gender_F:love" -> 3,
        "Gender_True:love" -> 5,
        "Gender_M:love" -> 2,
        "Gender_M_True:love" -> 2,
        "Gender:love" -> 5,
        "Gender_F_True:love" -> 2)

      val result: Set[Result] = UPQuality.getAccuracy(map)
      val target: Set[Result] = Set(
          Result("Gender_True","love","100.00%","5" , now),
          Result("Gender_M_True","love","100.00%","2" , now),
          Result("Gender_F_True","love","66.67%","2" , now)
      )
      assert(result == target)
    }

    it("test bigint division") {
      val a: BigInt = 1000
      val b: BigInt = 994
      assert("%1.3f".format(b.toDouble / a.toDouble) == "0.994")
    }
  }
}
