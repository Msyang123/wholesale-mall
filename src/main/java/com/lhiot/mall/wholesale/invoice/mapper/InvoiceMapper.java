package com.lhiot.mall.wholesale.invoice.mapper;

import com.lhiot.mall.wholesale.invoice.domain.Invoice;
import com.lhiot.mall.wholesale.invoice.domain.InvoiceTitle;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InvoiceMapper {

    InvoiceTitle selectInvoiceTitle(long id);

    int insertInvoiceTitle(InvoiceTitle invoiceTitle);

    int updateInvoiceTitle(InvoiceTitle invoiceTitle);

    int applyInvoice(Invoice invoice);

    Invoice findInvoiceByCode(String invoiceCode);
}
