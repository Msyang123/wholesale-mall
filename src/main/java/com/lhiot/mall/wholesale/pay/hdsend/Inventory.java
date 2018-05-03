package com.lhiot.mall.wholesale.pay.hdsend;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 进货清单
 */
@Data
public class Inventory {
    private String uuid;    //是		唯一标识，全局唯一	209AC86FDA5446C0BAE1FFBC0EDC481E
    private String senderCode;    //	是		送货单位 必须是合法的门店代码。	0088
    private String senderWrh;    //	否		出库仓位代码	-
    private String receiverCode;    //	否		收货人代码 传入null，则任务是采用默认客户	100061
    private String contactor;    //	否			收货人名称 关羽
    private String phoneNumber;    //	否			收货人联系电话 02154325000
    private String deliverAddress;    //	否	 	收货人地址	上海市闵行区联航路1588号海鼎公司
    private String remark;    //	否	备注		(门店)随机产生的接口测试单据。
    private Date ocrDate;    //是	发生日期 Date		new Date（）//DateFormatUtil.format1(new Date())
    private String filler;    //是	填单人 string		guanyu
    private String seller;//	否	销售员		刘备
    private String souceOrderCls;//	否	来源单号类型 String（20）		cls
    private String	negInvFlag;//是否允许负库存	否	String(10)	取值：0：否1：是 空：不控制	0
    private String	memberCode;//会员编号	否	String(255)		000001
    private BigDecimal freight;// 订单运费		否		单头运费非空时会覆盖明细行累加得到的运费	1.02

    private List<WholeSaleDtl> products;//商品明细信息

    private List<Pay> pays;//支付信息
    @Data
    public class WholeSaleDtl {
        private String skuId;
        private BigDecimal qty;
        private BigDecimal price;
        private BigDecimal	total;//	商品总金额 否		Total为空时，则商品总金额由price*qty得到，total不为空时，则商品价格有total/qty得到	20
        private BigDecimal	freight;//运费	否			1
        private	BigDecimal payAmount;//顾客实付金额	否			20
        private	BigDecimal unitPrice;//标准价	否			23
        private BigDecimal	priceAmount;//售价金额	否			28
        private BigDecimal	buyAmount;//购买金额	否			24
        private BigDecimal	businessDiscount;//商家优惠金额	否			3
        private String	businessDiscountInfo;//商家优惠信息	否	（255）		商家优惠
        private BigDecimal	platformDiscount;	//平台优惠金额 否			4
        private String	platformDiscountInfo;//平台优惠信息	否	（255）		平台优惠
        private BigDecimal	qpc;//规格数量	否		比如：1*20，则等于20。	20
        private String	qpcStr;//规格 否	（255）		1*20
    }
    @Data
    public class Pay{
        private BigDecimal total;//:100.00,
        private String payName;//现金
    }
}
