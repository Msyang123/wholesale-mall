package com.lhiot.mall.wholesale.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * Created by HuFan on 2018/4/21.
 */
@Data
@ToString
@ApiModel(description = "用户订单参数")
@NoArgsConstructor
public class Order{

    @ApiModelProperty(notes="订单ID",dataType="Long")
    private long id;

    @ApiModelProperty(notes="订单编码",dataType="String")
    private String orderCode;

    @ApiModelProperty(notes="用户编号",dataType="Long")
    private String userId;

    @ApiModelProperty(notes="业务员编号",dataType="Long")
    private String salesmanId;

    @ApiModelProperty(notes="订单总金额",dataType="Int")
    private int orderTotal;

    //TODO dataType is datetime
    @ApiModelProperty(notes="订单创建时间",dataType="Timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp orderCreateTime;

    @ApiModelProperty(notes="订单状态",dataType="Integer")
    private Integer orderStatus; //订单状态 0已失效 1待付款 2支付中 3待收货 4已收货 5退货中 6退货完成 7微信退款失败 8配送中

    @ApiModelProperty(notes="订单优惠券",dataType="Integer")
    private Integer orderCoupon;

    @ApiModelProperty(notes="优惠金额",dataType="Integer")
    private Integer orderDiscountFee;

    @ApiModelProperty(notes="应付金额",dataType="Integer")
    private Integer orderNeedFee;

    @ApiModelProperty(notes="海鼎状态",dataType="Integer")
    private Integer hdStatus; //0成功 1失败

    @ApiModelProperty(notes="配送费用",dataType="Integer")
    private Integer deliveryFee;

    @ApiModelProperty(notes="订单备注",dataType="String")
    private String remarks;

    @ApiModelProperty(notes="订单类型",dataType="Integer")
    private Integer orderType; //0线上支付 1货到付款

    @ApiModelProperty(notes="支付状态",dataType="Integer")
    private Integer payStatus;  //0已支付 1未支付

    @ApiModelProperty(notes="配送地址",dataType="String")
    private String deliveryAddress;

    //TODO dataType is datetime
    @ApiModelProperty(notes="确认收货时间",dataType="Timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp receiveTime;

    //----------t_whs_user 用户表-----------
    @ApiModelProperty(notes="门店名称",dataType="String")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String  shopName;

    @ApiModelProperty(notes="用户昵称",dataType="String")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String  nickname;

    @ApiModelProperty(notes="用户姓名",dataType="String")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String  userName;

    @ApiModelProperty(notes="手机号码",dataType="String")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String phone;

    @ApiModelProperty(notes="地址详情",dataType="String")
    private String  addressDetail;

    //----------t_whs_order_goods 订单商品表-----------
    @ApiModelProperty(notes="商品数量",dataType="Float")
    private Float quanity;

    //----------t_whs_goods 商品表-----------
    @ApiModelProperty(notes="商品名称",dataType="String")
    private String  goodsName;

    @ApiModelProperty(notes="商品图片",dataType="String")
    private String  goodsImages;



}
