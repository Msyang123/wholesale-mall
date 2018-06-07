package com.lhiot.mall.wholesale.base.duplicateaop;

import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * 防止重复提交拦截器
 * @author lynn
 *
 */
@Aspect
@Component
@Slf4j
public class DuplicateSubmitAspect {
    public static final String  DUPLICATE_TOKEN_KEY="duplicate_token_key";
    
    @Pointcut("execution(* com.lhiot.mall.wholesale.pay..*.*(..))")
    public void execute() {
    }

    @Before("execute() && @annotation(token)")
    public void before(final JoinPoint joinPoint, DuplicateSubmitToken token){
        if (Objects.nonNull(token)){
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = 
            		(HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
            boolean isSaveSession=token.save();
            if (isSaveSession){
                String key = getDuplicateTokenKey(joinPoint);
                Object t = request.getSession().getAttribute(key);
                if (Objects.isNull(t)){
                    String uuid= UUID.randomUUID().toString();
                    request.getSession().setAttribute(key.toString(),uuid);
                    log.info("token-key="+key);
                    log.info("token-value="+uuid.toString());
                }else {
                    throw new DuplicateSubmitException("支付中...");
                }
            }else{
            	throw new DuplicateSubmitException("支付中...");
            }

        }
    }

    /**
     * 获取重复提交key-->duplicate_token_key+','+请求方法名
     * @param joinPoint
     * @return
     */
    public String getDuplicateTokenKey(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        Object flagValue = args[0];
        StringBuilder key=new StringBuilder(DUPLICATE_TOKEN_KEY);
        key.append("_"+methodName).append("_"+flagValue);
        return key.toString();
    }
    
    @AfterReturning("execute() && @annotation(token)")
    public void doAfterReturning(JoinPoint joinPoint,DuplicateSubmitToken token) throws InterruptedException {
        // 处理完请求，返回内容
        if (Objects.nonNull(token)){
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = 
            		(HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
            boolean isSaveSession=token.save();
            if (isSaveSession){
                String key = getDuplicateTokenKey(joinPoint);
                Object t = request.getSession().getAttribute(key);
                if (Objects.nonNull(t)){
                    //方法执行完毕移除请求重复标记
                	Thread.sleep(5000);
                    request.getSession(false).removeAttribute(key);
                    log.info("方法执行完毕移除请求重复标记！");
                }
            }
        }
    }

    /**
     * 异常
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "execute()&& @annotation(token)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e, DuplicateSubmitToken token) {
        if (Objects.nonNull(token)
                && e instanceof DuplicateSubmitException==false){
            //处理处理重复提交本身之外的异常
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = 
            		(HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
            boolean isSaveSession=token.save();
            //获得方法名称
            if (isSaveSession){
                String key=getDuplicateTokenKey(joinPoint);
                Object t = request.getSession().getAttribute(key);
                if (Objects.nonNull(t)){
                    //方法执行完毕移除请求重复标记
                    request.getSession(false).removeAttribute(key);
                    log.info("异常情况--移除请求重复标记！");
                }
            }
        }
    }
}
