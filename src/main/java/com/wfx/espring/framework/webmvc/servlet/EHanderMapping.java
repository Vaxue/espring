package com.wfx.espring.framework.webmvc.servlet;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.webmvc.servlet 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-18-14:29:44
 * @Description: 处理Controller里面方法和RequestMapping的url的关系
 **/

@Data
@AllArgsConstructor
public class EHanderMapping {
    private Object controller;//保存方法对应的实例
    private Method method; //保存映射的方法
    private Pattern pattern; //保存URL的正则匹配
}
