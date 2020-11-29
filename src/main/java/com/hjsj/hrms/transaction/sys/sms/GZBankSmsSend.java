package com.hjsj.hrms.transaction.sys.sms;

import com.hjsj.hrms.bankgz.utils.SendKafkaGlblSrvNo;
import com.hjsj.hrms.interfaces.sys.SmsProxy;
import com.hjsj.hrms.bankgz.utils.MacAddressUtil;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.constant.SystemConfig;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @Titile: GZBankSmsSend
 * @Description:
 * @Company:hjsj
 * @Create time: 2020年1月15日16:31:56
 * @author: Duxl
 */
public class GZBankSmsSend implements SmsProxy {
    private Logger log = LoggerFactory.getLogger(GZBankSmsSend.class);

    @Override
    public boolean sendMessage(String phone, String msg) {
        boolean flag = false;
        String imn_url = SystemConfig.getPropertyValue("imn_url");//全媒体请求接口地址
        Date date = new Date();
        String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
        String timestamp = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss");
        Map<String, Object> systemHeaderMap = new HashMap();
        systemHeaderMap.put("sinkSystemCode", "IMN");//短信平台系统码
        systemHeaderMap.put("actionId", "IMN0112003");//交易码
        systemHeaderMap.put("actionVersion", "v1");
        systemHeaderMap.put("timestamp", timestamp);
        String sNo = MacAddressUtil.getGlbSrvNo();
        SendKafkaGlblSrvNo.registerGlbSrlNo(sNo, "IMN0112003");//登记流水号
        String sourceJnlNo = "HRS0" + datetime.substring(4, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17) + Math.round(Math.ceil(Math.random() * 1000));
        systemHeaderMap.put("sourceJnlNo", sourceJnlNo);//请求方流水号
        systemHeaderMap.put("glblSrvNo", sNo);//全局流水号 雪花算法
        Map<String, String> reserveMap = new HashMap();
        reserveMap.put("bank_num", "001");
        reserveMap.put("intor_syst_cod", "HRS");
        Map<String, Map> serv_call_areaMap = new HashMap();
        serv_call_areaMap.put("serv_call_area", reserveMap);
        systemHeaderMap.put("reserve", serv_call_areaMap);
        Map<String, String> coreMap = new HashMap();
        coreMap.put("push_cre_time", timestamp);//事件创建时间
        coreMap.put("push_task_id", datetime);//消息ID
        coreMap.put("push_event_cod", "HRS00001");
        coreMap.put("push_encry_flag", "0");//加密标识
        JSONObject content = new JSONObject();
        content.put("content", msg);
        coreMap.put("push_event_content", content.toString());
        coreMap.put("chl_type", "1");//通道类型  1-短信，2-微信，3-邮件
        coreMap.put("dst_addr", phone);//目标地址
        Map<String, Map> bodyMap = new HashMap();
        bodyMap.put("core", coreMap);
        Map<String, Map> requestDataMap = new HashMap();
        requestDataMap.put("systemHeader", systemHeaderMap);
        requestDataMap.put("body", bodyMap);
        Map<String, Map> dataMap = new HashMap();
        dataMap.put("requestData", requestDataMap);
        String urlParams = JSONObject.fromObject(dataMap).toString();
        String macVal = MacAddressUtil.getMacAddress(urlParams, "GZYH.IMNA_node.zak");
        log.info("贵州银行--->调用全媒体请求报文,请求地址url:{},请求报文param:{},mac值:{}", imn_url, urlParams, macVal);
        flag = sendParamToUrl(imn_url, urlParams, macVal, sourceJnlNo, sNo);

        return flag;
    }

    /**
     * 通过http请求获取需要的数据Post请求
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public boolean sendParamToUrl(String url, String params, String macVal, String sourceJnlNo, String sNo) {
        boolean callFlag = false;
        HttpClient httpClient = new HttpClient();
        PostMethod httpPost = new PostMethod(url);
        httpPost.addRequestHeader("Content-type", "application/json");
        httpPost.addRequestHeader("Charset", "UTF-8");
        httpPost.addRequestHeader("Accept", "application/json");
        httpPost.addRequestHeader("Accept-Charset", "UTF-8");
        httpPost.addRequestHeader("X-GZB-mac", macVal);//请求数据 mac 值(从全密码平台获取)
        httpPost.addRequestHeader("X-GZB-sourceSystemCode", "HRS");//请求方系统代码
        httpPost.addRequestHeader("X-GZB-jnlNo", sourceJnlNo);//业务流水号
        httpPost.addRequestHeader("X-GZB-actionId", "IMN0112003");//交易码
        String string = "";//返回报文
        try {
            httpPost.setRequestEntity(new StringRequestEntity(params, "application/json", "UTF-8"));
            int status = httpClient.executeMethod(httpPost);//发送请求
            if (status == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                InputStream in = httpPost.getResponseBodyAsStream();
                int len = 0;
                byte[] buf = new byte[1024];
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                string = out.toString("UTF-8");

                JSONObject responseObject = JSONObject.fromObject(string);

                String responseCode = responseObject.getString("responseCode");
                if ("IMNAAAAAAA".equals(responseCode)) {
                    return true;
                } else {
                    SendKafkaGlblSrvNo.updateGlbSrlNo(sNo, "1", responseCode, responseObject.toString());
                    log.error("贵州银行--->调用全媒体失败,错误信息为ErrorMessage:{}，请求地址url:{},请求报文param:{},mac值:{}", string, url, params, macVal);
                }

            } else {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                InputStream in = httpPost.getResponseBodyAsStream();
                int len = 0;
                byte[] buf = new byte[1024];
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                string = out.toString("UTF-8");
                log.error("贵州银行--->调用全媒体失败,错误信息ErrorMessage:{}，请求地址url:{},请求报文param:{},mac值:{}", string, url, params, macVal);
                return callFlag;
            }
        } catch (Exception e) {
            log.error("贵州银行-->调用全媒体平台失败！异常信息ErrorMessage:{}", e);
            log.error("贵州银行--->调用全媒体失败,错误信息为{}，请求地址url:{},请求报文param:{},mac值:{}", string, url, params, macVal);
        }
        return callFlag;
    }

    /**
     * 发送请求获得数据
     *
     * @param sUrl
     * @param method
     * @return
     * @throws Exception
     */
    protected String sendParamsToUrl(String sUrl, HttpMethod method) throws Exception {
        HttpClient client = new HttpClient();
        // 设置参数
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        try {
            // 发送请求
            int status = client.executeMethod(method);
            if (status != HttpStatus.SC_OK) {
                throw new Exception("发送参数到url时，服务端异常  url：" + sUrl + " 异常信息:\r\n\r\n" + method.getResponseBodyAsString());
            }
        } catch (Exception e) {
            log.error("贵州银行-->调用短信平台失败！异常信息ErrorMessage:{}", e);
        } finally {
            method.releaseConnection();
        }
        return method.getResponseBodyAsString();
    }
}
