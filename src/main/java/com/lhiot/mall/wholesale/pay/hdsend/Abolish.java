package com.lhiot.mall.wholesale.pay.hdsend;

import lombok.Data;

/**
 * 批发单服务--作废
 */
@Data
public class Abolish {
    private String id;//批发单标识或者单号		是	String		98001606220004
    private String srcCls;//来源单号类型		否	String（20）		cls
    private String oper;//操作人		是			曹操

}
