package com.lhiot.mall.wholesale.invoice.mapper;

import com.lhiot.mall.wholesale.invoice.domain.Invoice;
import com.lhiot.mall.wholesale.invoice.domain.InvoiceTitle;
import com.lhiot.mall.wholesale.invoice.domain.gridparam.InvoiceGridParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface InvoiceMapper {

    InvoiceTitle selectInvoiceTitle(long id);

    InvoiceTitle selectInvoiceTitleById(long userId);

    int insertInvoiceTitle(InvoiceTitle invoiceTitle);

    int updateInvoiceTitle(InvoiceTitle invoiceTitle);

    int applyInvoice(Invoice invoice);

    Invoice findInvoiceByCode(String invoiceCode);

    Invoice listByorderCodesLike(String orderCode);

    int updateInvoiceByCode(Invoice invoice);

    List<Invoice> list(Invoice invoice);

    //根据id查询开票信息详情
    Invoice select(long id);

    //分页查询开票信息
    List<Invoice> pageQuery(InvoiceGridParam param);

    //查询分类的开票信息总记录数
    int pageQueryCount(InvoiceGridParam param);

    //修改开票状态或驳回原因
    int updateInvoiceStatus(Invoice invoice);

    //后台管理系统 导出开票信息
    List<Map<String, Object>> exportData(InvoiceGridParam param);
}
