package com.lhiot.mall.wholesale.pay.hdsend;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 批发退服务—新增
 */
@Data
public class Wholesalebck {
    private String uuid;//唯一标识 是		唯一标识，全局唯一	209AC86FDA5446C0BAE1FFBC0EDC481E
    private String receiverCode;//收货单位	是		必须是合法的门店（物流中心）代码。	0088
    private String receiverWrh;//收货单位的仓位代码	否		如果传入null，则认为是默认仓“-”	-
    private String clientCode;    //退货客户代码 否		传入null，则任务是采用默认客户	100061
    private String contactor;//退货人名称	否			关羽
    private String phoneNumber;//退货人联系电话	否			02154325000
    private String remark;//备注 否	string		(门店)随机产生的接口测试单据。
    private Date ocrDate;//发生日期是	2016-07-27T10:36:18.344+0800
    private String filler;//填单人	是			guanyu
    private String seller;//销售员	否			刘备
    private String souceOrderCls;//来源单号类型	否			来源单号类型
    private String outBillNumber;//批发单单号	否			对应批发出货单单号。
    private BigDecimal freight;//订单运费	否		单头运费非空时会覆盖明细行累加得到的运费	1.02
    private List<Product> products;


    @Data
    public class Product {

        private String skuId;//商品输入码	是	string		6947503750237
        private BigDecimal qty;//数量	是	BigDecimal		100
        private BigDecimal price;//价格（单品价）	否	BigDecimal		10
        private String bckReason;//退货原因	否	string		东西坏了
        private BigDecimal total;//商品总金额	否		Total为空时，则商品总金额由price*qty得到，total不为空时，则商品价格有total/qty得到	10
        private BigDecimal freight;//运费 否	BigDecimal		1
        private BigDecimal payAmount;//顾客实付金额否	BigDecimal		20
        private BigDecimal unitPrice;//标准价否	BigDecimal		23
        private BigDecimal priceAmount;//售价金额否	BigDecimal		28
        private BigDecimal buyAmount;// 购买金额否	BigDecimal		24
        private BigDecimal businessDiscount;//商家优惠金额否	BigDecimal		3
        private BigDecimal platformDiscount;//平台优惠金额否	BigDecimal		4
    }
}
