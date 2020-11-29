package com.hjsj.hrms.utils.sendmessage.weixin;

import com.hrms.struts.constant.SystemConfig;
import net.sf.json.JSONArray;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.mortbay.util.ajax.JSON;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 发送微信消息，远程调用 w_selfservice 工程的 接口发送
 * @author Calvin
 * @date 2019-12-25
 */
public class WeiXinBo {

    private static Logger log = Logger.getLogger(WeiXinBo.class);
    private static String sys_username = null;
    public static String MESSAGE_PICTURE_NOTICE = "UserFiles/Image/tongzhi.png";
    public static String MESSAGE_PICTURE_WARN = "UserFiles/Image/warn.png";

    public static boolean sendMsgToPerson(String nbase, String a0100, String title, String description, String picUrl, String url) {
        ArrayList values = new ArrayList();
        values.add(nbase);
        values.add(a0100);
        values.add(title);
        values.add(description);
        values.add(picUrl);
        values.add(url);

        String valueStr = JSON.toString(values);
        String callInfoStr = getCallInfo("sendMsgToPerson",String.class,String.class,String.class,String.class,String.class,String.class);
        return sendRequest(valueStr,callInfoStr);
    }

    public static boolean sendMsgToPerson(List usernames, String title, String description, String picUrl, String url) {
        ArrayList values = new ArrayList();
        values.add(usernames);
        values.add(title);
        values.add(description);
        values.add(picUrl);
        values.add(url);

        String valueStr = JSON.toString(values);
        String callInfoStr = getCallInfo("sendMsgToPerson",List.class,String.class,String.class,String.class,String.class);
        return sendRequest(valueStr,callInfoStr);
    }

    public static boolean sendMsgToPerson(String nbase, List a0100s, String title, String description, String picUrl, String url) {
        ArrayList values = new ArrayList();
        values.add(nbase);
        values.add(a0100s);
        values.add(title);
        values.add(description);
        values.add(picUrl);
        values.add(url);

        String valueStr = JSON.toString(values);
        String callInfoStr = getCallInfo("sendMsgToPerson",String.class,List.class,String.class,String.class,String.class,String.class);
        return sendRequest(valueStr,callInfoStr);
    }

    public static boolean sendMsgToPerson(String username, String title, String description, String picUrl, String url) {
        ArrayList values = new ArrayList();
        values.add(username);
        values.add(title);
        values.add(description);
        values.add(picUrl);
        values.add(url);

        String valueStr = JSON.toString(values);
        String callInfoStr = getCallInfo("sendMsgToPerson",String.class,String.class,String.class,String.class,String.class);
        return sendRequest(valueStr,callInfoStr);
    }

    public static boolean sendMsgToPerson(List usernames, List articles) {
        ArrayList values = new ArrayList();
        values.add(usernames);
        values.add(articles);

        JSONArray arr = JSONArray.fromObject(values);
        String valueStr = arr.toString();
        String callInfoStr = getCallInfo("sendMsgToPerson",List.class,List.class);
        return sendRequest(valueStr,callInfoStr);
    }

    public static boolean sendMsgToDept(List orgids, List articles) {
        ArrayList values = new ArrayList();
        values.add(orgids);
        values.add(articles);

        String valueStr = JSON.toString(values);
        String callInfoStr = getCallInfo("sendMsgToDept",List.class,List.class);
        return sendRequest(valueStr,callInfoStr);
    }

    public static boolean sendMsgToDept(List orgids, String title, String description, String picUrl, String url) {
        ArrayList values = new ArrayList();
        values.add(orgids);
        values.add(title);
        values.add(description);
        values.add(picUrl);
        values.add(url);

        String valueStr = JSON.toString(values);
        String callInfoStr = getCallInfo("sendMsgToDept",List.class,String.class,String.class,String.class,String.class);
        return sendRequest(valueStr,callInfoStr);
    }

    private static String getCallInfo(String methodName,Class... classTypes){
        HashMap callInfo = new HashMap();
        callInfo.put("className","com.hjsj.weixin.message.WeiXinBo");
        callInfo.put("methodName","sendMsgToPerson");
        ArrayList paramObj = new ArrayList();

        for(int i=0;i<classTypes.length;i++){
            paramObj.add(classTypes[i].getName());
        }
        callInfo.put("paramType",paramObj);

        String callInfoStr = JSON.toString(callInfo);
        return callInfoStr;
    }

    private static boolean sendRequest(String values,String callInfo) {
        boolean result = false;
        InputStream is = null;
        BufferedReader br = null;
        try {
            String weixinServer = SystemConfig.getPropertyValue("w_selfservice_url");
            if (!weixinServer.endsWith("/")) {
                weixinServer += "/";
            }
            weixinServer += "w_selfservice/wxmessage/sendmessage";

            String time = new Date().getTime()+"";

            String signData = values+"."+callInfo+"."+time+"./w_selfservice/wxmessage/sendmessage";
            String sign = getMac(signData);

            HttpClient hc = new HttpClient();
            PostMethod pm = new PostMethod(weixinServer);
            pm.setParameter("values", URLEncoder.encode(values,"UTF-8"));
            pm.setParameter("callInfo",callInfo);
            pm.setParameter("time",time);
            pm.setParameter("sign",sign);
            int statusCode = hc.executeMethod(pm);

            if (statusCode != HttpStatus.SC_OK) {
                return false;
            }

            is = pm.getResponseBodyAsStream();
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            StringBuffer sbf = new StringBuffer();
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sbf.append(temp);
            }

            String returnMsg  = sbf.toString();

            result = Boolean.parseBoolean(returnMsg);

        }catch(Exception e){
            result = false;
        }
        return result;
    }

    private static String getMac(String data) throws Exception {
        if(data == null) {
            throw new Exception("no data");
        }

        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(data.getBytes("UTF8"));
        byte s[] = m.digest();
        String result = "";
        for (int i = 0; i < s.length; i++) {
            result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6);
        }
        return result;
    }

}
