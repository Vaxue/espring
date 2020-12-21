package com.wfx.espring.demo.aspect;


import com.wfx.espring.framework.aop.aspect.EJoinPoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * Created by Tom.
 */

public class LogAspect {

    //在调用一个方法之前，执行before方法
    public void before(EJoinPoint joinPoint){
        System.out.println("Invoker Before Method!!!" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(),System.currentTimeMillis());
    }

    //在调用一个方法之后，执行after方法
    public void after(EJoinPoint joinPoint){
        System.out.println("Invoker After Method!!!" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
        long startTime = (Long) joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        System.out.println("use time :" + (endTime - startTime));
    }

    public void afterThrowing(EJoinPoint joinPoint, Throwable ex){
        System.out.println("出现异常" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()) +
                "\nThrows:" + ex.getMessage());
    }

}
