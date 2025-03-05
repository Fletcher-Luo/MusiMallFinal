package com.ByteAndHeartDance.order.entity.VO;

import io.swagger.v3.oas.annotations.media.Schema;

        import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "订单交易和订单项返回实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TradeEntityAndItemVO extends TradeEntityVO {
  List<TradeItemVO> tradeItemList;

}
