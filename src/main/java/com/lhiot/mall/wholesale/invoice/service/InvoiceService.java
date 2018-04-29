package com.lhiot.mall.wholesale.invoice.service;

import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.invoice.domain.Invoice;
import com.lhiot.mall.wholesale.invoice.domain.InvoiceTitle;
import com.lhiot.mall.wholesale.invoice.mapper.InvoiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class InvoiceService {

    private final InvoiceMapper invoiceMapper;

    private final SnowflakeId snowflakeId;


    @Autowired
    public InvoiceService(InvoiceMapper invoiceMapper,SnowflakeId snowflakeId) {
        this.invoiceMapper = invoiceMapper;
        this.snowflakeId=snowflakeId;
    }


    public InvoiceTitle selectInvoiceTitle(long id){
        return invoiceMapper.selectInvoiceTitle(id);
    }

    public int saveOrUpdateInvoiceTitle(InvoiceTitle invoiceTitle){
        if (invoiceTitle.getId()>0){
            return invoiceMapper.updateInvoiceTitle(invoiceTitle);
        }else {
            return invoiceMapper.insertInvoiceTitle(invoiceTitle);
        }
    }

    public int applyInvoice(Invoice invoice){
        invoice.setCreateTime(new Timestamp(System.currentTimeMillis()));
        invoice.setInvoiceCode(snowflakeId.stringId());//发票业务编码
        return invoiceMapper.applyInvoice(invoice);
    }

    /**
     * 依据发票code查询发票信息
     * @param invoiceCode
     * @return
     */
    public Invoice findInvoiceByCode(String invoiceCode){
        return invoiceMapper.findInvoiceByCode(invoiceCode);
    }

    /**
     * 修改发票信息
     * @param invoice
     * @return
     */
    public int updateInvoiceByCode(Invoice invoice){
        return invoiceMapper.updateInvoiceByCode(invoice);
    }


    public List<Invoice> list(Invoice invoice){
        return this.invoiceMapper.list(invoice);
    }
}
