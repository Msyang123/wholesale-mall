package com.lhiot.mall.wholesale.invoice.service;

import com.lhiot.mall.wholesale.invoice.domain.InvoiceTitle;
import com.lhiot.mall.wholesale.invoice.mapper.InvoiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InvoiceService {

    private final InvoiceMapper invoiceMapper;


    @Autowired
    public InvoiceService(InvoiceMapper invoiceMapper) {
        this.invoiceMapper = invoiceMapper;
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

}
