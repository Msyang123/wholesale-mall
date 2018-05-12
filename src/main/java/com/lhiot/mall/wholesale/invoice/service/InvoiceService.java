package com.lhiot.mall.wholesale.invoice.service;

import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.invoice.domain.Invoice;
import com.lhiot.mall.wholesale.invoice.domain.InvoiceTitle;
import com.lhiot.mall.wholesale.invoice.domain.gridparam.InvoiceGridParam;
import com.lhiot.mall.wholesale.invoice.mapper.InvoiceMapper;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class InvoiceService {

    private final InvoiceMapper invoiceMapper;

    private final OrderMapper orderMapper;

    private final SnowflakeId snowflakeId;


    @Autowired
    public InvoiceService(InvoiceMapper invoiceMapper, OrderMapper orderMapper, SnowflakeId snowflakeId) {
        this.invoiceMapper = invoiceMapper;
        this.orderMapper = orderMapper;
        this.snowflakeId=snowflakeId;
    }


    public InvoiceTitle selectInvoiceTitle(long id){
        InvoiceTitle invoiceTitle = invoiceMapper.selectInvoiceTitle(id);
        if (invoiceTitle==null){
            return new InvoiceTitle();
        }
        return invoiceTitle;
    }

    public int saveOrUpdateInvoiceTitle(InvoiceTitle invoiceTitle){
        if (invoiceTitle.getId()>0){
            return invoiceMapper.updateInvoiceTitle(invoiceTitle);
        }else {
            return invoiceMapper.insertInvoiceTitle(invoiceTitle);
        }
    }

    /**
     * 发票开票申请
     * @param invoice
     * @return
     */
    public int applyInvoice(Invoice invoice){
        invoice.setInvoiceStatus("no");//yes-已开票no未开票
        invoice.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return invoiceMapper.applyInvoice(invoice);
    }

    /**
     * 计算发票税费信息
     * @param invoice
     * @return
     */
    public Invoice calculateTexFee(Invoice invoice){
        //查询订单信息
        List<OrderDetail> orderDetailList= orderMapper.searchOrdersByOrderCodes(Arrays.asList(invoice.getInvoiceOrderIds().split(",")));
        //计算订单的开票金额
        int invoiceFee=0;
        for (OrderDetail item:orderDetailList) {
            invoiceFee+=item.getPayableFee()+item.getDeliveryFee();
        }
        //税点
        BigDecimal invoiceTaxPre=new BigDecimal(0.0336f);
        int taxFee=new BigDecimal(invoiceFee).multiply(new BigDecimal(0.0336)).setScale(0, RoundingMode.DOWN).intValue();
        invoice.setInvoiceFee(invoiceFee);//开票金额
        invoice.setInvoiceTax(invoiceTaxPre);//发票税点
        invoice.setTaxFee(taxFee);//需付税费
        return invoice;
    }
    /**
     * 依据发票code查询发票信息
     * @param invoiceCode
     * @return
     */
    public Invoice findInvoiceByCode(long invoiceCode){
        return invoiceMapper.findInvoiceByCode(invoiceCode);
    }

    /**
     * 依据订单code模糊查询发票信息
     * @param orderCode
     * @return
     */
    public Invoice listByorderCodesLike(String orderCode){
        return invoiceMapper.listByorderCodesLike(orderCode);
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


    /**
     * 分页查询 开票信息
     * @return
     */
    public PageQueryObject pageQuery(InvoiceGridParam param){
        PageQueryObject result = new PageQueryObject();
        int count = invoiceMapper.pageQueryCount(param);
        int page = param.getPage();
        int rows = param.getRows();
        //起始行
        param.setStart((page-1)*rows);
        //总记录数
        int totalPages = (count%rows==0?count/rows:count/rows+1);
        if(totalPages < page){
            page = 1;
            param.setPage(page);
            param.setStart(0);
        }
        List<Invoice> invoiceList = invoiceMapper.pageQuery(param);
        result.setRows(invoiceList);
        result.setPage(page);
        result.setRecords(rows);
        result.setTotal(totalPages);
        return result;
    }

    /**
     * 查询开票信息详情
     * @return
     */
    public Invoice detail(Long id) {
        return invoiceMapper.select(id);
    }

    /**
     * 修改开票状态和驳回原因
     * @return
     */
    public int updateInvoiceStatus(Invoice invoice) {
        return invoiceMapper.updateInvoiceStatus(invoice);
    }
}
