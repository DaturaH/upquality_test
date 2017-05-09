package com.netease.idc.strategy

import com.netease.idc.feature.data.UPConstant.UPKey
import org.scalatest.{FunSpec, Matchers}

/**
  * Created by hutianqi on 17/4/1.
  */
class TestStrategyContainer extends FunSpec with Matchers {
  describe("test StrategyContainer.......") {
    it("test stragetyContainer") {
      assert( StrategyContainer.strategyContainer.get(UPKey.gender).get.isInstanceOf[com.netease.idc.strategy.EqualEnumStrategy])
    }
  }
}
