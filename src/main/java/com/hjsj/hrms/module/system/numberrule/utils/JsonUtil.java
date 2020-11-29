
/*
 *
 *  *   @copyright      Copyright ©  2020 贵州银行 All rights reserved.
 *  *   @project        hrs-backend
 *  *   @author         warne
 *  *   @date           2020/6/1 上午10:52
 *  *
 *
 */

/*
 * www.bgzchina.com
 * ©2019 BANK OF GUIZHOU All rights reserved.
 * stock-mgr
 * 2019/11/5 下午4:59 by warne
 */

package com.hjsj.hrms.module.system.numberrule.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;

/*
 * 针对json 进行解析的工具类
 * @author Alan
 *
 */
public abstract class JsonUtil {

    public static <T> T parseObject(String text, TypeReference<T> type, Feature... features) {
        return JSONObject.parseObject(text, type, features);
    }

    public static String toJSONString(Object object) {
        return JSONObject.toJSONString(object,
                SerializerFeature.WriteEnumUsingToString,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullBooleanAsFalse);
    }

    public static String toJSONString(Object object, SerializerFeature... features) {
        return JSONObject.toJSONString(object, features);
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        return JSONObject.parseObject(text, clazz);
    }

    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        return JSONObject.parseArray(text, clazz);
    }

    public static Object getValueByKey(String text, Object key) {
        JSONObject jsonObject = JSON.parseObject(text);
        return jsonObject.get(key);
    }

    public static JSONObject parseObject(String text) {
        return JSONObject.parseObject(text);
    }
}
