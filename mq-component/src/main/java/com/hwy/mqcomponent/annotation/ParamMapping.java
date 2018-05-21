package com.hwy.mqcomponent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义解析器
 *   默认的消息解析器，仅支持对象完全相同的情况
 *   但是，仅仅支持一级层级的数据转换，比如List<Email>,Map<String,Email>
 *   而嵌套的层级如：List<Set<Email>>,Map<String,Map<String, Email>> 这样是不支持
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ParamMapping {
}
