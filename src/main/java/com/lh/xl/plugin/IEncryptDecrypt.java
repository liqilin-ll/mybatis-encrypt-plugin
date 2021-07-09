package com.lh.xl.plugin;

import java.lang.reflect.Field;

/**
 * @author lql
 * @date 2021/7/7 19:32
 */
public interface IEncryptDecrypt {
    /**
     * 加密方法
     *
     * @param declaredFields  反射bean成员变量
     * @param parameterObject Mybatis入参
     * @param <T>
     * @return
     */
    public <T> T encrypt(Field[] declaredFields, T parameterObject) throws IllegalAccessException;


    /**
     * 解密方法
     *
     * @param result Mybatis 返回值，需要判断是否是ArrayList类型
     * @param <T>
     * @return
     */
    public <T> T decrypt(T result) throws IllegalAccessException;
}
