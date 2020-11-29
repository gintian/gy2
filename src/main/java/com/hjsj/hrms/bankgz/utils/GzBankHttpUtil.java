package com.hjsj.hrms.bankgz.utils;

import com.hrms.frame.dao.utility.DateUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 〈贵州银行调用http接口类〉
 *
 * @Author duxl
 * @Date 2020/6/23
 * @since 1.0.0
 */
public class GzBankHttpUtil {
    private static Logger log = LoggerFactory.getLogger(GzBankHttpUtil.class);

    /**
     * 通过http请求获取需要的数据Post请求
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String sendParamToUrl(String url, String params, String macVal, String actionId) throws Exception {
        Date date = new Date();
        String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
        String sourceJnlNo = "HRS0" + datetime.substring(4, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17) + Math.round(Math.ceil(Math.random() * 1000));
        // 创建post方法
        PostMethod post = new PostMethod(url);
        if (StringUtils.isNotEmpty(params)) {
            // 添参数
            post.setRequestEntity(new StringRequestEntity(params, "application/json", "UTF-8"));
        }
        post.addRequestHeader("Content-Type", "application/json");
        post.addRequestHeader("Accept-Language", "zh-CN");
        if (StringUtils.isNotBlank(macVal)) {
            post.addRequestHeader("X-GZB-mac", macVal);//请求数据 mac 值(从全密码平台获取)
            post.addRequestHeader("X-GZB-actionId", actionId);//请求服务码
            post.addRequestHeader("x-gzb-sourcesystemcode", "HRS");//请求系统码
            post.addRequestHeader("X-GZB-jnlNo", sourceJnlNo);//业务流水号
        }
        return sendParamsToUrl(url, post, params);
    }

    /**
     * 发送请求获得数据
     *
     * @param sUrl
     * @param method
     * @return
     * @throws Exception
     */
    protected static String sendParamsToUrl(String sUrl, HttpMethod method, String params) throws Exception {
        HttpClient client = new HttpClient();
        // 设置参数
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        try {
            // 发送请求
            int status = client.executeMethod(method);
            if (status != HttpStatus.SC_OK) {
                throw new Exception("贵州银行-->发送参数到url时，服务端异常  url：" + sUrl + " 异常信息:\r\n\r\n" + method.getResponseBodyAsString());
            }

            return method.getResponseBodyAsString();
        } catch (Exception e) {
            log.error("贵州银行-->发送参数到url时，服务端异常,错误信息为ErrorMessage:{}，请求地址url:{},返回报文response:{},请求报文param:{}", e, sUrl, e, params);
            throw e;
        } finally {
            method.releaseConnection();
        }
    }
}
