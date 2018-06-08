package com.lhiot.mall.wholesale.pay.hdsend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.microx.common.exception.ServiceException;
import com.sgsl.hd.autoconfigure.HaiDingProperties;
import com.sgsl.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.*;

@Service
@Slf4j
public class Warehouse {

    @Autowired
    private HaiDingProperties properties;
    //String h4rest="http://172.16.10.109:7280/h4rest-server/rest/h5rest-server/core";

    private static final Charset charset = Charset.forName("UTF-8");
    private String apiUser="test01",apiPass="AePq88kJbleNGUDT";
    private ObjectMapper om=new ObjectMapper();

    class MyAuthenticator extends Authenticator {
        protected PasswordAuthentication getPasswordAuthentication() {
            String username = "guest",password ="guest";
            return new PasswordAuthentication(username, password.toCharArray());
        }
    }

    /**
     * 总仓订单创建
     * @param inventory
     * @return
     */
    public String savenew2state(Inventory inventory){
        try {
            //{"echoCode":"0","uuid":"96461805060001"}
            //String result=this.h4Request("post","/wholesaleservice/wholesale/savenew2state/1700",om.writeValueAsString(inventory));
        	String result=this.h4Request("post","/wholesaleservice/wholesale/savenew2state/0",om.writeValueAsString(inventory));
        	Map resultMap=om.readValue(result, Map.class);
            if(!Objects.equals(resultMap.get("echoCode"),"0")){
                throw new ServiceException("发送海鼎总仓批发单错误"+result);
            }
            return String.valueOf(resultMap.get("uuid"));
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    /**
     * 总仓退货
     * @param abolish
     * @return
     */
    public String abolish(Abolish abolish){
        try {
            //{"echoCode":"0"}
           String uri= MessageFormat.format("/wholesaleservice/wholesale/abolish/{0}?srcCls={1}&oper={2}",abolish.getId(),abolish.getSrcCls(),abolish.getOper());
           String result= this.h4Request("post",uri,om.writeValueAsString(abolish));
            Map resultMap=om.readValue(result, Map.class);
            return String.valueOf(resultMap.get("echoCode"));
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    private String h4Request(String method,String api,String data) throws Exception {
        Authenticator.setDefault(
                new MyAuthenticator());
        URL url = new URL(properties.getH4rest()+api);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method.toUpperCase());
        connection.setDoOutput(true);
        String tok = apiUser + ':' + apiPass;
        String hash = Base64.encodeBase64String(tok.getBytes(charset));
        connection.addRequestProperty("Authorization", "Basic " + hash);
        connection.addRequestProperty("Content-Type", "application/json;charset=UTF-8");
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("charset", "UTF-8");
        connection.connect();

        if (data != null) {
            IOUtils.write(data.toString().getBytes(charset), connection.getOutputStream());
        }

        InputStream result = connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream();
        List<String> lst = null;
        if (method.equalsIgnoreCase("get")) {
            lst = IOUtils.readLines(result, charset);
        } else {
            lst = IOUtils.readLines(new BufferedReader(new InputStreamReader(result, charset)));
        }
        IOUtils.closeQuietly(result);
        IOUtils.close(connection);

        return String.join("", lst);
    }

    public static void main(String[] args) {
        try {
            /*Inventory inventory=new Inventory();
            inventory.setUuid(UUID.randomUUID().toString());
            inventory.setSenderCode("9646");
            inventory.setSenderWrh("07310101");
            inventory.setReceiverCode(null);
            inventory.setContactor("老曹");
            inventory.setPhoneNumber("18888888888");
            inventory.setDeliverAddress("五一大道98号");
            inventory.setRemark("快点送");
            inventory.setOcrDate(new Date());
            inventory.setFiller("填单人");
            inventory.setSeller("销售员");
            inventory.setSouceOrderCls("批发商城");
            inventory.setNegInvFlag("1");
            inventory.setMemberCode(null);
            inventory.setFreight(new BigDecimal(21.3));

            //清单
            List<Inventory.WholeSaleDtl> wholeSaleDtlList=new ArrayList<>();
            Inventory.WholeSaleDtl wholeSaleDtl1=inventory.new WholeSaleDtl();
            wholeSaleDtl1.setSkuId("010100100011");
            wholeSaleDtl1.setQty(new BigDecimal(3));
            wholeSaleDtl1.setPrice(new BigDecimal(100.1));
            wholeSaleDtl1.setTotal(null);
            wholeSaleDtl1.setFreight(null);
            wholeSaleDtl1.setPayAmount(new BigDecimal((99.1)));
            wholeSaleDtl1.setUnitPrice(null);
            wholeSaleDtl1.setPriceAmount(new BigDecimal(200.1));
            wholeSaleDtl1.setBuyAmount(new BigDecimal(99.2));
            wholeSaleDtl1.setBusinessDiscount(new BigDecimal(0.1));
            wholeSaleDtl1.setPlatformDiscount(new BigDecimal(0));
            wholeSaleDtl1.setQpc(new BigDecimal(5));
            wholeSaleDtl1.setQpcStr("1*5");

            wholeSaleDtlList.add(wholeSaleDtl1);
            inventory.setProducts(wholeSaleDtlList);

            List<Inventory.Pay> pays=new ArrayList<>();
            Inventory.Pay pay=inventory.new Pay();

            pay.setTotal(new BigDecimal(234.56));
            pay.setPayName("现金支付");
            pays.add(pay);
            inventory.setPays(pays);

            ObjectMapper om=new ObjectMapper();
            //api=/savenew2state/{to_state}
            System.out.println( new Warehouse().savenew2state(inventory));*/
           /**********批发单服务-作废**********************************/
            Abolish abolish=new Abolish();
            abolish.setId("96461805060001");
            abolish.setSrcCls("批发商城");
            abolish.setOper("退货管理员");
            System.out.println(new Warehouse().abolish(abolish));

            /*********批发退服务**********************/
            /*Wholesalebck wholesalebck=new Wholesalebck();
            wholesalebck.setUuid(UUID.randomUUID().toString());
            wholesalebck.setReceiverCode("9646");
            wholesalebck.setReceiverWrh("07310101");
            wholesalebck.setClientCode(null);
            wholesalebck.setContactor("老曹");
            wholesalebck.setPhoneNumber("18888888888");
            wholesalebck.setRemark("快点退");

            wholesalebck.setOcrDate(new Date());
            wholesalebck.setFiller("填单人");
            wholesalebck.setSeller("销售员");
            wholesalebck.setSouceOrderCls("批发商城");
            wholesalebck.setOutBillNumber(null);
            wholesalebck.setFreight(new BigDecimal(21.3));
            List<Wholesalebck.Product> products=new ArrayList<>();

            Wholesalebck.Product product=wholesalebck.new Product();
            product.setSkuId("010100100011");
            product.setQty(new BigDecimal(3));
            product.setPrice(new BigDecimal(100.1));
            product.setBckReason("果子坏了");
            product.setTotal(null);
            product.setFreight(null);
            product.setPayAmount(new BigDecimal((99.1)));
            product.setUnitPrice(null);
            product.setPriceAmount(new BigDecimal((99.1)));
            product.setBuyAmount(new BigDecimal(99.2));
            product.setBusinessDiscount(new BigDecimal(0.1));
            product.setPlatformDiscount(new BigDecimal(0));

            products.add(product);
            wholesalebck.setProducts(products);

            System.out.println(new Warehouse().h4Request("post","/wholesalebckservice/wholesalebck",om.writeValueAsString(wholesalebck)));*/


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
