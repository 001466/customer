package com.ec.common;

/**
 * Created by Karl He on 2017-11-14.
 * 鍦ㄩ渶瑕佺櫥褰曢獙璇佺殑Controller鐨勬柟娉曚笂浣跨敤姝ゆ敞瑙�
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorized {}
