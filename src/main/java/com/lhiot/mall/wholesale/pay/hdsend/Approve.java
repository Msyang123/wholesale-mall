package com.lhiot.mall.wholesale.pay.hdsend;

import lombok.Data;

/**
 * 批发退服务--审核   不需要
 */
@Data
public class Approve {
    private String id;//批发退货单标识或者单号		是			98001606220004
    private String srcCls;//来源单号类型	否	String（20）		cls
    private String oper;//操作人	是	String		曹操
    private String toStat;//目前状态	是		400：总部批准1000：已收货300：已完成	400
}
