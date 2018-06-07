package com.lhiot.mall.wholesale.base.duplicateaop;

/**
 * 重复提交异常
 * @author lynn
 *
 */
public class DuplicateSubmitException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DuplicateSubmitException(String msg) {
        super(msg);
    }

    public DuplicateSubmitException(String msg, Throwable cause){
        super(msg,cause);
    }
}
