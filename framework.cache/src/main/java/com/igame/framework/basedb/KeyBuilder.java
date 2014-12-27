package com.igame.framework.basedb;

import org.springframework.util.StringUtils;

/**  
 * @ClassName: KeyBuilder   
 * @Author: Allen allen.ime@gmail.com  
 * @Date: 2014年12月5日 上午11:57:52  
 * @Description: 键名生成器   
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