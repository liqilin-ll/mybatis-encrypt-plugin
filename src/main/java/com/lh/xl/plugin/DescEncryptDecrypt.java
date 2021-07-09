package com.lh.xl.plugin;

import com.lh.xl.annotation.EncryptDecryptField;

import java.lang.reflect.Field;

/**
 * @author lql
 * @date 2021/7/7 19:35
 */
public class DescEncryptDecrypt implements IEncryptDecrypt {
    private String password;

    public DescEncryptDecrypt(String password) {
        this.password = password;
    }

    @Override
    public <T> T encrypt(Field[] declaredFields, T parameterObject) throws IllegalAccessException {
        for (int i = 0; i < declaredFields.length; i++) {
            //取出所有被EncryptDecryptField注解的字段
            EncryptDecryptField annotation = declaredFields[i].getAnnotation(EncryptDecryptField.class);
            if (annotation != null) {
                String name = declaredFields[i].getName();
                try {
                    Field field = parameterObject.getClass().getDeclaredField(name);
                    field.setAccessible(true);
                    Object oldValue = field.get(parameterObject);
                    if (oldValue instanceof String) {
                        String newValue = DesUtils.encrypt((String) oldValue,password);
                        field.set(parameterObject, newValue);
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
        return parameterObject;
    }

    @Override
    public <T> T decrypt(T result) throws IllegalAccessException {
        Class<?> resultClass = result.getClass();
        Field[] fields = resultClass.getDeclaredFields();
        for (Field field : fields) {
            EncryptDecryptField annotation = field.getAnnotation(EncryptDecryptField.class);
            if (annotation != null) {
                field.setAccessible(true);
                Object s = field.get(result);
                if (s instanceof String) {
                    String value = (String) s;
                    field.set(result, DesUtils.decrypt(value,password));
                }
            }
        }
        return result;
    }
}
