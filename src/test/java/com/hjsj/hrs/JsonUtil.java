
/*
 *
 *  *   @project        warne-boot-common
 *  *   @file           JsonUtil
 *  *   @author         warne
 *  *   @date           19-4-19 下午6:22
 *
 */

package com.hjsj.hrs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @desc: 进行解析的工具类
 * @date: 2018/10/10 17:10
 * @author: warne
 */
@Slf4j
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

    public static Object getValueBykey(String text, Object key) {
        JSONObject jsonObject = JSON.parseObject(text);
        return jsonObject.get(key);
    }

    public static JSONObject parseObject(String text) {
        return JSONObject.parseObject(text);
    }



    /**
     * JSONObject to bean
     *
     * @param obj
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T toBean(JSONObject obj, Class<T> clazz) {
        T t = obj.toJavaObject(clazz);
        return t;
    }


    /**
     * 格式化json
     *
     * @param obj
     * @return
     */
    public static String formatJson(Object obj) {
        String result = null;
        if (obj != null) {
            result = toJSONString(obj);
        }
        return formatJson(result);
    }

    public static String formatJson(Map map) {
        String result = null;
        if (map != null) {
            result = toJSONString(map);
        }

        return formatJson(result);
    }

    public static String formatJson(String json) {

        int level = 0;
        //存放格式化的json字符串
        StringBuffer jsonForMatStr = new StringBuffer();
        for (int index = 0; index < json.length(); index++) {
            //获取s中的每个字符
            char c = json.charAt(index);

            //level大于0并且jsonForMatStr中的最后一个字符为\n,jsonForMatStr加入\t
            if (level > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                jsonForMatStr.append(getLevelStr(level));
            }
            //遇到"{"和"["要增加空格和换行，遇到"}"和"]"要减少空格，以对应，遇到","要换行
            switch (c) {
                case '{':
                case '[':
                    jsonForMatStr.append(c + "\n");
                    level++;
                    break;
                case ',':
                    jsonForMatStr.append(c + "\n");
                    break;
                case '}':
                case ']':
                    jsonForMatStr.append("\n");
                    level--;
                    jsonForMatStr.append(getLevelStr(level));
                    jsonForMatStr.append(c);
                    break;
                default:
                    jsonForMatStr.append(c);
                    break;
            }
        }

        return jsonForMatStr.toString();
    }


    private static String getLevelStr(int level) {
        StringBuffer levelStr = new StringBuffer();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }
}
