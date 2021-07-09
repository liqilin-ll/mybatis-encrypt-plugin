package com.lh.xl.plugin;

import com.lh.xl.annotation.EncryptDecryptClass;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

/**
 * @author lql
 * @date 2021/7/8 15:26
 */
@Intercepts({@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})})
@Component
@ConditionalOnProperty(value = "domain.decrypt", havingValue = "true")
public class ResultSetInterceptor implements Interceptor, ApplicationListener<ContextRefreshedEvent> {
    @Value("${domain.password}")
    private String password;
    private IEncryptDecrypt encryptDecrypt = null;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object resultObject = invocation.proceed();
        if (resultObject == null) {
            return null;
        }
        // selectList类型的结果集
        if (resultObject instanceof ArrayList) {
            ArrayList resultList = (ArrayList) resultObject;
            if (!CollectionUtils.isEmpty(resultList) && needToDecrypt(resultList.get(0))) {
                for (Object result : resultList) {
                    //逐一解密
                    encryptDecrypt.decrypt(result);
                }
            }
        } else {
            // 单个对象
            if (needToDecrypt(resultObject)) {
                encryptDecrypt.decrypt(resultObject);
            }
        }
        return resultObject;
    }

    // 判断是否要解密
    private boolean needToDecrypt(Object object) {
        EncryptDecryptClass encryptDecryptClass = object.getClass().getAnnotation(EncryptDecryptClass.class);
        return Objects.nonNull(encryptDecryptClass);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    // 实现接口ApplicationListener，在spring初始化之后执行
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        encryptDecrypt = new DescEncryptDecrypt(password);
    }
}
