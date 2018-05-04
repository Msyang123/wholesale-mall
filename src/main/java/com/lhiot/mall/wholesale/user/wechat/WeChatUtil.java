/**
* Copyright © 2016 SGSL
* 湖南绿航恰果果农产品有限公司
* http://www.sgsl.com 
* All rights reserved. 
*/
package com.lhiot.mall.wholesale.user.wechat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhiot.mall.wholesale.base.DateFormatUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;


/**
 * 微信工具类
 * 
 * @author leon
 * @version 1.0 2016年11月18日下午3:31:26
 */
@Slf4j
public class WeChatUtil {
	public final  String encoding = "UTF-8";

	/** 微信支付 - 退款接口 (POST) */
	public final  String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
	//public final  String REFUND_URL = "https://api.mch.weixin.qq.com/sandboxnew/pay/refund";//沙箱环境测试
	
	/** 微信支付 - 获取沙箱密钥 (POST) */
	//public final  String GET_SING_KEY = "https://api.mch.weixin.qq.com/sandboxnew/pay/getsignkey";//沙箱环境测试

	/** 微信支付统一接口(POST) */
	public final  String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	//public final  String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/sandboxnew/pay/unifiedorder";//沙箱环境测试
	
	/** 下载对账单(POST) */
	//public final  String 	DOWN_LOAD_BILL_URL = "https://api.mch.weixin.qq.com/sandboxnew/pay/downloadbill";//沙箱环境测试
	
	/** 查询支付订单状态(POST) */
	//public final  String 	ORDER_QUERY_URL = "https://api.mch.weixin.qq.com/sandboxnew/pay/orderquery";//沙箱环境测试

	/** 查询退款订单状态(POST) */
	//public final  String 	REFUND_QUERY_URL = "https://api.mch.weixin.qq.com/sandboxnew/pay/refundquery";//沙箱环境测试

	/** 获取token接口(GET) */
	public final  String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}";

	/** 获取ticket接口(GET) */
	public final  String TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token={0}&type=jsapi";

	/** 获取OPEN ID (GET) */
	public final  String OPEN_ID_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid={0}&secret={1}&code={2}&grant_type=authorization_code";

	/** 获取微信用户信息 (GET) */
	public final  String USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token={0}&openid={1}&lang=zh_CN";

	/** oauth2网页授权接口(GET) */
	public final  String OAUTH2_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid={0}&redirect_uri={1}&response_type=code&scope={2}&state={3}#wechat_redirect";

	/** 获取网友授权微信用户信息 (GET) */
	public final  String OAUTH2_USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token={0}&openid={1}&lang=zh_CN";

	/**通过refresh_token刷新ACCESS_TOKEN*/
	public final String OAUTH2_REFRESH_ACCESS_TOKEN="https://api.weixin.qq.com/sns/oauth2/refresh_token?appid={0}&grant_type=refresh_token&refresh_token={1}";

	/************微信认证登录与支付配置*******************************************/
	@Getter
	private PaymentProperties properties;

	private ObjectMapper om = new ObjectMapper();

	public WeChatUtil(PaymentProperties properties) {
		this.properties = properties;
	}
	/**
	 * 申请退款
	 * 
	 * @param tradeNo
	 *            订单号
	 * @param totalFee
	 *            退款金额
	 * @return 微信返回的XML
	 * @throws Exception 
	 */
	public  String refund(final String tradeNo, final int totalFee) throws Exception {
		return refund(tradeNo, tradeNo, totalFee, totalFee);
	}


	/**
	 * 申请退款（分多次退款）
	 * 
	 * @param tradeNo
	 *            订单号
	 * @param refundNo
	 *            退款单号
	 * @param totalFee
	 *            订单总额
	 * @param refundFee
	 *            退款金额
	 * @return 微信返回的XML
	 * @throws Exception 
	 */
	public String refund(final String tradeNo, final String refundNo, final int totalFee, final int refundFee) throws Exception {
		String currTime = DateFormatUtil.format3(new Date());
		String strTime = currTime.substring(8, currTime.length());
		String nonce = strTime + this.buildRandom(4);

		SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
		packageParams.put("appid", properties.getWeChatOauth().getAppId());
		packageParams.put("mch_id", properties.getWeChatPay().getPartnerId());
		packageParams.put("nonce_str", nonce);
		packageParams.put("out_trade_no", tradeNo);
		packageParams.put("out_refund_no", refundNo);
		packageParams.put("total_fee", totalFee);
		packageParams.put("refund_fee", refundFee);
		//packageParams.put("op_user_id", AppProps.get("partner_id"));
		String sign = this.createSign(properties.getWeChatPay().getPartnerKey(), packageParams); // 获取签名
		packageParams.put("sign", sign);
		String xml = this.getRequestXml(packageParams); // 获取请求微信的XML
		HttpPost httpPost = new HttpPost(REFUND_URL);
		try {
			KeyStore ks = KeyStore.getInstance("pkcs12");
			ks.load(new FileInputStream(properties.getWeChatPay().getPkcs12().getFile()), properties.getWeChatPay().getPartnerId().toCharArray());
			SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(ks, properties.getWeChatPay().getPartnerId().toCharArray())
					.build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" },
					null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			HttpClient client = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			httpPost.setEntity(new StringEntity(xml, "UTF-8"));
			HttpResponse resp = client.execute(httpPost);
			HttpEntity entity = resp.getEntity();
			if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				return resp.getStatusLine().getStatusCode() + "";
			}
			String resource = EntityUtils.toString(entity, encoding);
			return resource.replace("<![CDATA[", "").replace("]]>", "");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return "";
	}

	/**
	 * 微信回调时，获取参数
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public  XPathParser getParametersByWeChatCallback(final HttpServletRequest request) throws IOException {
		BufferedReader reader = request.getReader();
		StringBuffer inputString = new StringBuffer();
		String line = "";
		while ((line = reader.readLine()) != null) {
			inputString.append(line);
		}
		request.getReader().close();
		log.info("微信回调时，获取参数getParametersByWeChatCallback:"+inputString.toString());
		InputStream in = new ByteArrayInputStream(inputString.toString().getBytes());
		XPathParser xpath = new XPathParser(in);
		in.close();
		return xpath;
	}


	/**
	 * 获取预支付ID
	 * 
	 * @param packageParams
	 * @return
	 */
	public  String sendWeChatGetPrepayId(final SortedMap<Object, Object> packageParams) {
		String xml = this.getRequestXml(packageParams);
		return this.sendWeChatGetPrepayId(xml);
	}

	/**
	 * 获取预支付ID
	 * 
	 * @param xml
	 * @return
	 */
	public  String sendWeChatGetPrepayId(final String xml) {
		String resultXml = null;
		String prepay_id = null;
		try {
			HttpClient client = HttpClients.custom().build();
			HttpPost httpost = new HttpPost(UNIFIED_ORDER_URL);
			httpost.setEntity(new StringEntity(xml, encoding));
			HttpResponse HttpClientResponse = client.execute(httpost);
			resultXml = EntityUtils.toString(HttpClientResponse.getEntity(), encoding);
			log.info("\n" + resultXml);
			InputStream in = new ByteArrayInputStream(resultXml.getBytes(encoding));
			XPathParser xpath = new XPathParser(in);
			XNode xNode = xpath.evalNode("//prepay_id");
			if (xNode == null) {
				return null;
			}
			prepay_id = xNode.body();
			in.close();
		} catch (Exception e) {
			log.error(e.getMessage() + "\n" + resultXml, e);
		}
		return prepay_id;
	}
	

	/**
	 * 【微信支付】返回给微信的参数
	 * 
	 * @param return_code
	 *            返回编码
	 * @param return_msg
	 *            返回信息
	 * @return
	 */
	public  String setXML(final String return_code, final String return_msg) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml><return_code><![CDATA[").append(return_code).append("]]></return_code><return_msg><![CDATA[")
				.append(return_msg).append("]]></return_msg></xml>");
		return sb.toString();
	}

	/**
	 * 【微信支付】 将请求参数转换为xml格式的string
	 * 
	 * @param parameters
	 *            请求参数
	 * @return
	 */
	public  String getRequestXml(final SortedMap<Object, Object> parameters) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		Set<Entry<Object, Object>> es = parameters.entrySet();
		Iterator<Entry<Object, Object>> it = es.iterator();
		while (it.hasNext()) {
			Entry<Object, Object> entry = it.next();
			String k = (String) entry.getKey();
			Object v = entry.getValue();
			if ("sign".equalsIgnoreCase(k)) {
				continue;
			}
			if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k)) {
				sb.append("<").append(k).append("><![CDATA[").append(v).append("]]></").append(k).append(">");
			} else {
				sb.append("<").append(k).append(">").append(v).append("</").append(k).append(">");
			}
		}
		sb.append("<sign>").append(parameters.get("sign")).append("</sign>").append("</xml>");
		return sb.toString();
	}

	/**
	 * sign签名
	 * 
	 * @param partner_key
	 *            商户支付标识
	 * 
	 * @param parameters
	 *            请求参数
	 * @return
	 */
	public  String createSign(final String partner_key, final SortedMap<Object, Object> parameters) {
		StringBuffer sb = new StringBuffer();
		Set<Entry<Object, Object>> es = parameters.entrySet();
		Iterator<Entry<Object, Object>> it = es.iterator();
		while (it.hasNext()) {
			Entry<Object, Object> entry = it.next();
			String k = (String) entry.getKey();
			Object v = entry.getValue();
			if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
				sb.append(k).append("=").append(v).append("&");
			}
		}
		sb.append("key=").append(partner_key);
		//FIXME 可能有问题
		String sign = DigestUtils.md5Hex(sb.toString().getBytes());
		return sign.toUpperCase();
	}

	/**
	 * 获取网页授权微信用户信息
	 * 
	 * @param openId
	 * @param access_token
	 */
	public  String getOauth2UserInfo(final String openId, final String access_token) {
		String requestUrl = MessageFormat.format(OAUTH2_USER_INFO_URL, access_token, openId);
		return httpsRequest(requestUrl, "GET", null);
	}

	/**
	 * 获取微信用户信息
	 * 
	 * @param openId
	 * @param access_token
	 * @return
	 */
	public  String getUserInfo(final String openId, final String access_token) {
		String requestUrl = MessageFormat.format(USER_INFO_URL, access_token, openId);
		return httpsRequest(requestUrl, "GET", null);
	}

	/**
	 * 获取open_id 和 网页授权access_token
	 * 
	 * @param appid
	 * @param appsecrect
	 * @param code
	 * @return
	 */
	public  AccessToken getAccessTokenByCode(final String appid, final String appsecrect, final String code) throws IOException{
		String requestUrl = MessageFormat.format(OPEN_ID_URL, appid, appsecrect, code);
		String result = httpsRequest(requestUrl, "GET", null);
		Map<String, Object> amapMap = om.readValue(result, Map.class);
		log.info("WeChatUtil getToken"+result);
		AccessToken accessToken = new AccessToken();
		accessToken.setAccessToken(String.valueOf(amapMap.get("access_token")));
		Integer expiresIn=Integer.valueOf(String.valueOf(amapMap.get("expires_in")));
		if(Objects.isNull(expiresIn)){
			expiresIn=7100;
		}
		accessToken.setExpiresIn(expiresIn);
		accessToken.setRefreshToken(String.valueOf(amapMap.get("refresh_token")));
		accessToken.setOpenId(String.valueOf(amapMap.get("openid")));
		accessToken.setScope(String.valueOf(amapMap.get("scope")));
		return accessToken;
	}

	/**
	 * 获得js signature
	 * 
	 * @param jsapi_ticket
	 * @param timestamp
	 * @param nonce
	 * @param jsurl
	 * @return signature
	 */
	public  String getSignature(final String jsapi_ticket, final String timestamp, final String nonce,
			final String jsurl) {
		String[] paramArr = new String[] { "jsapi_ticket=" + jsapi_ticket, "timestamp=" + timestamp,
				"noncestr=" + nonce, "url=" + jsurl };
		Arrays.sort(paramArr);
		String content = paramArr[0].concat("&" + paramArr[1]).concat("&" + paramArr[2]).concat("&" + paramArr[3]);
		String gensignature = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] digest = md.digest(content.toString().getBytes());
			gensignature = byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
		}
		if (gensignature != null) {
			return gensignature;
		} else {
			return "false";
		}
	}

	/**
	 * 获取接口JsapiTicket访问凭证
	 * 
	 * @param accessToken
	 * @return JsapiTicket
	 */
	public  JsapiTicket getJsapiTicket(final String accessToken) throws IOException {
		String requestUrl = MessageFormat.format(TICKET_URL, accessToken);
		String result = httpsRequest(requestUrl, "GET", null);
		if (result == null) {
			return null;
		}
		Map<String, Object> amapMap = om.readValue(result, Map.class);
		JsapiTicket ticket = new JsapiTicket();
		ticket.setTicket(String.valueOf(amapMap.get("ticket")));
		Integer expiresIn = Integer.valueOf(String.valueOf(amapMap.get("expires_in")));
		if(Objects.isNull(expiresIn)){
			expiresIn = 7100;
		}
		ticket.setExpiresIn(expiresIn);
		return ticket;
	}

	/**
	 * 获取接口访问凭证
	 * @return Token
	 */
	public  Token getToken() throws IOException {
		String requestUrl = MessageFormat.format(TOKEN_URL, this.getProperties().getWeChatOauth().getAppId(), this.getProperties().getWeChatOauth().getAppSecret());
		String result = httpsRequest(requestUrl, "GET", null);
		Map<String, Object> amapMap = om.readValue(result, Map.class);
		log.info("WeChatUtil getToken"+result);
		Token token = new Token();
		token.setAccessToken(String.valueOf(amapMap.get("access_token")));
		Integer expiresIn=Integer.valueOf(String.valueOf(amapMap.get("expires_in")));
		if(Objects.isNull(expiresIn)){
			expiresIn=7100;
		}
		token.setExpiresIn(expiresIn);
		token.setRefreshToken(String.valueOf(amapMap.get("refresh_token")));
		return token;
	}

	/**
	 * 通过refreshAccessToken获取AccessToken
	 * @param refreshAccessToken
	 * @return AccessToken
	 */
	public  AccessToken refreshAccessToken(String refreshAccessToken) throws IOException {
		//如果refreshAccessToken是空，就需要重新授权
		if(StringUtils.isEmpty(refreshAccessToken))
			return null;
		String requestUrl = MessageFormat.format(OAUTH2_REFRESH_ACCESS_TOKEN, this.getProperties().getWeChatOauth().getAppId(), refreshAccessToken);
		String result = httpsRequest(requestUrl, "GET", null);
		Map<String, Object> amapMap = om.readValue(result, Map.class);
		log.info("WeChatUtil getToken"+result);
		AccessToken accessToken = new AccessToken();
		accessToken.setAccessToken(String.valueOf(amapMap.get("access_token")));
		Integer expiresIn=Integer.valueOf(String.valueOf(amapMap.get("expires_in")));
		if(Objects.isNull(expiresIn)){
			expiresIn=7100;
		}
		accessToken.setExpiresIn(expiresIn);
		accessToken.setRefreshToken(String.valueOf(amapMap.get("refresh_token")));
		accessToken.setOpenId(String.valueOf(amapMap.get("openid")));
		accessToken.setScope(String.valueOf(amapMap.get("scope")));
		return accessToken;

	}

	/**
	 * 发送https请求
	 * 
	 * @param requestUrl
	 *            请求地址
	 * @param requestMethod
	 *            请求方式（GET、POST）
	 * @param outputStr
	 *            提交的数据
	 * @return 返回微信服务器响应的JSON信息
	 */
	public  String httpsRequest(final String requestUrl, final String requestMethod, final String outputStr) {
		try {
			TrustManager[] tm = { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}
			} };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			conn.setRequestMethod(requestMethod);
			conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
			// 当outputStr不为null时向输出流写数据
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				// 注意编码格式
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			// 从输入流读取返回内容
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			// 释放资源
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			conn.disconnect();
			return buffer.toString();
		} catch (ConnectException ce) {
			log.error("连接超时：{}", ce);
		} catch (Exception e) {
			log.error("https请求异常：{}", e);
		}
		return null;
	}

	public  String urlEncodeUTF8(final String source) {
		String result = source;
		try {
			result = java.net.URLEncoder.encode(source, "utf-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 将字节数组转换为十六进制字符串
	 *
	 * @param byteArray
	 * @return
	 */
	private  String byteToStr(final byte[] byteArray) {
		String strDigest = "";
		for (int i = 0; i < byteArray.length; i++) {
			strDigest += byteToHexStr(byteArray[i]);
		}
		return strDigest;
	}

	/**
	 * 将字节转换为十六进制字符串
	 *
	 * @param mByte
	 * @return
	 */
	private  String byteToHexStr(final byte mByte) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] tempArr = new char[2];
		tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
		tempArr[1] = Digit[mByte & 0X0F];
		String s = new String(tempArr);
		return s;
	}


	/**
	 * 取出一个指定长度大小的随机正整数.
	 * 
	 * @param length
	 *            int 设定所取出随机数的长度。length小于11
	 * @return int 返回生成的随机数。
	 */
	public  int buildRandom(final int length) {
		int num = 1;
		double random = Math.random();
		if (random < 0.1) {
			random = random + 0.1;
		}
		for (int i = 0; i < length; i++) {
			num = num * 10;
		}
		return (int) ((random * num));
	}
}
