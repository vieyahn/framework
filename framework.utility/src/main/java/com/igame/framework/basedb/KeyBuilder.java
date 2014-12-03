package com.igame.framework.basedb;

import org.springframework.util.StringUtils;

/**   
 * @Title: KeyBuilder.java
 * @Package com.igame.framework.basedb
 * @Author Allen allen.ime@gmail.com  
 * @Date 2014年9月29日 下午6:02:42
 * @Description:键名生成器
 * @Version V1.0   
 */
public class KeyBuilder {
    /**
     * 生成索引键名
     * @param clazz
     * @param indexName
     * @param indexValues
     * @return
     */
    public static String buildIndexKey(Class<?> clazz, String indexName, Object... indexValues) {
        StringBuilder builder = new StringBuilder();
        if (clazz != null) {
            builder.append(clazz.getName()).append("&");
        }
        if (indexName != null) {
            builder.append(indexName).append("#");
        }
        if ((indexValues != null) && (indexValues.length > 0)) {
            return StringUtils.arrayToDelimitedString(indexValues, "^");
        }
        return builder.toString();
    }
}