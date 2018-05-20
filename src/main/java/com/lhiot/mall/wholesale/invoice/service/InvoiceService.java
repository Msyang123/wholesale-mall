package com.lhiot.mall.wholesale.invoice.service;

import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.invoice.domain.Invoice;
import com.lhiot.mall.wholesale.invoice.domain.InvoiceTitle;
import com.lhiot.mall.wholesale.invoice.domain.gridparam.InvoiceGridParam;
import com.lhiot.mall.wholesale.invoice.mapper.InvoiceMapper;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.mapper.OrderMapper;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.service.PaymentLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class InvoiceService {

    private final InvoiceMapper invoiceMapper;

    private final OrderMapper orderMapper;

    private final PaymentLogService paymentLogService;


    @Autowired
    public InvoiceService(InvoiceMapper invoiceMapper, OrderMapper orderMapper, PaymentLogService paymentLogService) {
        this.invoiceMapper = invoiceMapper;
        this.orderMapper = orderMapper;
        this.paymentLogService = paymentLogService;
    }


    public InvoiceTitle selectInvoiceTitle(long id){
        InvoiceTitle invoiceTitle = invoiceMapper.selectInvoiceTitle(id);
        if (invoiceTitle==null){
            return new InvoiceTitle();
        }
        return invoiceTitle;
    }

    public InvoiceTitle selectInvoiceTitleById(long id){
        InvoiceTitle invoiceTitle = invoiceMapper.selectInvoiceTitleById(id);
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
     * @param paymentType 支付方式
     * @return
     */
    public int applyInvoice(Invoice invoice,String paymentType,String bankType,String transactionId){
        //依据附加参数查询发票抬头信息
        InvoiceTitle invoiceTitle= selectInvoiceTitleById(invoice.getInvoiceTitleId());
        //构建发票信息

        invoice.setInvoiceTitleId(invoiceTitle.getId());
        invoice.setTaxpayerNumber(invoiceTitle.getTaxpayerNumber());
        invoice.setCompanyName(invoiceTitle.getCompanyName());
        invoice.setContactName(invoiceTitle.getContactName());
        invoice.setContactPhone(invoiceTitle.getContactPhone());
        invoice.setAddressArea(invoiceTitle.getAddressArea());
        invoice.setAddressDetail(invoiceTitle.getAddressDetail());
        invoice.setBankName(invoiceTitle.getBankName());
        invoice.setBankCardCode(invoiceTitle.getBankCardCode());
        invoice.setCreateTime(new Timestamp(System.currentTimeMillis()));

        invoice.setInvoiceStatus("no");//yes-已开票no未开票
        invoice.setCreateTime(new Timestamp(System.currentTimeMillis()));
        invoice.setOrderNumber(invoice.getInvoiceOrderIds().split(",").length);//所含订单数量
        int result=invoiceMapper.applyInvoice(invoice);
        if (result>0) {
            //将订单设置成已开票
            for(String id:invoice.getInvoiceOrderIds().split(",")){
                OrderDetail orderDetail=new OrderDetail();
                orderDetail.setInvoiceStatus("yes");
                orderDetail.setId(Long.valueOf(id));
                orderMapper.updateOrderById(orderDetail);
            }

            PaymentLog paymentLog = new PaymentLog();
            //写入日志
            paymentLog.setPaymentStep("paid");//支付步骤：sign-签名成功 paid-支付成功
            paymentLog.setOrderCode(invoice.getInvoiceCode());
            paymentLog.setOrderId(invoice.getId());
            paymentLog.setUserId(invoice.getUserId());
            paymentLog.setPaymentFrom("invoice");//支付来源：order-订单 debt-账款 invoice-发票 recharge-充值
            paymentLog.setPaymentType(paymentType);//支付类型：balance-余额支付 wechat-微信 offline-线下支付
            paymentLog.setTotalFee(invoice.getTaxFee());

            paymentLog.setBankType(bankType);//银行类型
            paymentLog.setTransactionId(transactionId);//微信流水
            paymentLogService.insertPaymentLog(paymentLog);
            return 1;
        }else{
            return -1;
        }
    }

    /**
     * 计算发票税费信息
     * @param invoice
     * @return
     */
    public Invoice calculateTaxFee(Invoice invoice){
        //查询订单信息
        List<OrderDetail> orderDetailList= orderMapper.searchOrdersByOrderIds(Arrays.asList(invoice.getInvoiceOrderIds().split(",")));
        //计算订单的开票金额
        int invoiceFee=0;
        for (OrderDetail item:orderDetailList) {
            //invoiceFee+=item.getPayableFee()+item.getDeliveryFee();
            invoiceFee+=item.getPayableFee();
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
    public Invoice findInvoiceByCode(String invoiceCode){
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
     * 修改开票状态或驳回原因
     * @return
     */
    public int updateInvoiceStatus(Invoice invoice) {
        if (Objects.nonNull(invoice.getId()) && invoice.getId()>0 ){
            return invoiceMapper.updateInvoiceStatus(invoice);
        }else {
            return 0;
        }
    }

    /**
     * 导出开票信息
     * @return
     */
    public List<Map<String, Object>> exportData(InvoiceGridParam param){
        return invoiceMapper.exportData(param);
    }
}
