package com.lhiot.mall.wholesale.base;

import java.math.BigDecimal;

public class CalculateUtil {

	//比较两个数的大小
	public static <T> int compare(T number1,T number2){
		String n1 = String.valueOf(number1);
		String n2 = String.valueOf(number2);
		
		BigDecimal b1 = new BigDecimal(n1);
		BigDecimal b2 = new BigDecimal(n2);
		
		return b1.compareTo(b2);
	}

	/**
	 * 做除法
	 * @param dividend 被除数
	 * @param divisor 除数
	 * @param scale 保留位数
	 * @return
	 */
	public static <T> String division(T dividend,T divisor,Integer scale){
		String n1 = String.valueOf(dividend);
		String n2 = String.valueOf(divisor);
		
		BigDecimal b1 = new BigDecimal(n1);
		BigDecimal b2 = new BigDecimal(n2);
		
		return b1.divide(b2, scale, BigDecimal.ROUND_DOWN).toString();
	}
}
