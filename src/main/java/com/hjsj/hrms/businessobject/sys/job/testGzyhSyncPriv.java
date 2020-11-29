package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.bankgz.utils.MacAddressUtil;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.constant.SystemConfig;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;

public class testGzyhSyncPriv {
    private static Logger log = LoggerFactory.getLogger(testGzyhSyncPriv.class);

    public static void main(String[] args) {
        String menuPrivPushUrl = "http://172.31.116.10:8080/v1/acp/";//联调
//        String menuPrivPushUrl = "http://172.31.126.89:8080/v1/acp/";//sit
//        String menuPrivPushUrl = "http://172.31.128.38:8080/v1/acp/";//uat
        //获取 角色基础、角色菜单对应
        String param0 = packSyncJson0("00002075");
        String str0 = sendByPost(param0, "ACP2004002", menuPrivPushUrl);
        System.out.println("领导角色对应的菜单数据:" + str0);
        String param00 = packSyncJson0("00002074");
        String str00 = sendByPost(param00, "ACP2004002", menuPrivPushUrl);
        System.out.println("员工角色对应的菜单数据:" + str00);
        //获取 菜单层级表
        String param1 = packSyncJson1();
        String str1 = sendByPost(param1, "ACP2005008", menuPrivPushUrl);
        System.out.println("菜单层级数据:" + str1);
        //获取 用户角色关系表
        String param2 = packSyncJson2("23232");
        String str2 = sendByPost(param2, "ACP2004008", menuPrivPushUrl);
        System.out.println("用户角色关系表:" + str2);
    }

    /**
     * 角色菜单对应接口报文组装
     *
     * @return
     */
    private static String packSyncJson0(String role_id) {
        JSONObject escMessage = new JSONObject();
        JSONObject requestData = new JSONObject();

        JSONObject systemHeader = new JSONObject();//业务数据系统头
        systemHeader.put("sourceSystemCode", "HRS");//请求方系统码
        systemHeader.put("sinkSystemCode", "ACP");//目标方系统码
        systemHeader.put("actionId", "ACP2004002");//服务码
        systemHeader.put("actionVersion", "v1");//交易码版本 默认v1
        Date date = new Date();
        String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
        String timestamp = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS");
        String sourceJnlNo = datetime.substring(0, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17);
        systemHeader.put("sourceJnlNo", MacAddressUtil.getGlbSrvNo());//请求方流水号 交易日期(8位：YYYYMMDD) + 请求方流水序号（12 位）
        systemHeader.put("timestamp", timestamp);//请求发送时间戳 yyyy-MM-dd HH:mm:ss.SSS
        systemHeader.put("ip", SystemConfig.getPropertyValue("hrpserver"));//发送请求机器ip
        JSONObject appHeader = new JSONObject();
        appHeader.put("BankNum", "001");//todo ???
        appHeader.put("Brno", "01998");
        appHeader.put("Userno", "02928");
        systemHeader.put("appHeader", appHeader);
        requestData.put("systemHeader", systemHeader);
        JSONObject body = new JSONObject();
        body.put("DutyNo", role_id);
        requestData.put("body", body);
        escMessage.put("requestData", requestData);
        return escMessage.toString();
    }

    /**
     * 菜单浏览接口报文组装
     *
     * @return
     */
    private static String packSyncJson1() {

        JSONObject escMessage = new JSONObject();
        JSONObject requestData = new JSONObject();
        JSONObject systemHeader = new JSONObject();//业务数据系统头
        systemHeader.put("sourceSystemCode", "HRS");//请求方系统码
        systemHeader.put("sinkSystemCode", "ACP");//目标方系统码
        systemHeader.put("actionId", "ACP2005008");//服务码
        systemHeader.put("actionVersion", "v1");//交易码版本 默认v1
        Date date = new Date();
        String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
        String timestamp = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS");
        String sourceJnlNo = datetime.substring(0, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17);
        systemHeader.put("sourceJnlNo", MacAddressUtil.getGlbSrvNo());//请求方流水号 交易日期(8位：YYYYMMDD) + 请求方流水序号（12 位）
        systemHeader.put("timestamp", timestamp);//请求发送时间戳 yyyy-MM-dd HH:mm:ss.SSS
        systemHeader.put("ip", SystemConfig.getPropertyValue("hrpserver"));//发送请求机器ip
        JSONObject appHeader = new JSONObject();
        appHeader.put("BankNum", "001");//todo ???
        appHeader.put("Brno", "01998");
        appHeader.put("Userno", "02928");
        systemHeader.put("appHeader", appHeader);
        requestData.put("systemHeader", systemHeader);
        JSONObject body = new JSONObject();
        requestData.put("body", body);
        escMessage.put("requestData", requestData);
        return escMessage.toString();
    }

    /**
     * 用户角色对应接口报文组装
     *
     * @return
     */
    private static String packSyncJson2(String counterEmployee) {

        JSONObject escMessage = new JSONObject();
        JSONObject requestData = new JSONObject();
        JSONObject systemHeader = new JSONObject();//业务数据系统头
        systemHeader.put("sourceSystemCode", "HRS");//请求方系统码
        systemHeader.put("sinkSystemCode", "ACP");//目标方系统码
        systemHeader.put("actionId", "ACP2004008");//服务码
        systemHeader.put("actionVersion", "v1");//交易码版本 默认v1
        Date date = new Date();
        String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
        String timestamp = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS");
        String sourceJnlNo = datetime.substring(0, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17);
        systemHeader.put("sourceJnlNo", MacAddressUtil.getGlbSrvNo());//请求方流水号 交易日期(8位：YYYYMMDD) + 请求方流水序号（12 位）
        systemHeader.put("timestamp", timestamp);//请求发送时间戳 yyyy-MM-dd HH:mm:ss.SSS
        systemHeader.put("ip", SystemConfig.getPropertyValue("hrpserver"));//发送请求机器ip
        JSONObject appHeader = new JSONObject();
        appHeader.put("BankNum", "001");//todo ???
        appHeader.put("Brno", "01998");
        appHeader.put("Userno", "02928");
        systemHeader.put("appHeader", appHeader);
        requestData.put("systemHeader", systemHeader);
        JSONObject body = new JSONObject();
        body.put("TlrNo", counterEmployee);
        requestData.put("body", body);
        escMessage.put("requestData", requestData);
        return escMessage.toString();
    }


    /**
     * 调用全渠道平台接口
     *
     * @return
     */
    public static String sendByPost(String param, String actionId, String url) {
        String retStr = "";

        url = url + actionId;
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setContentCharset("GBK");
        PostMethod postMethod = new PostMethod(url);
        String responseMsg = "";
        try {
            postMethod.addRequestHeader("X-GZB-actionId", actionId);
            postMethod.addRequestHeader("x-gzb-sourcesystemcode", "1000");
            String macVal = MacAddressUtil.getMacAddress(param, "GZYH.ACPA_node.zak");
            postMethod.addRequestHeader("X-GZB-mac", macVal);
            RequestEntity se = new StringRequestEntity(param, "application/json", "UTF-8");
            postMethod.setRequestEntity(se);
            int status = httpClient.executeMethod(postMethod);
            if (status == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                InputStream in = postMethod.getResponseBodyAsStream();
                int len = 0;
                byte[] buf = new byte[1024];
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                responseMsg = out.toString("UTF-8");
                JSONObject obj = JSONObject.fromObject(responseMsg);
                String responseCode = (String) obj.get("responseCode");
                if ("ACPAAAAAAA".equals(responseCode)) {
                    retStr = responseMsg;
                } else {
                    log.error("权限推送-->调用全渠道接口失败，返回结果为 responseMsg:{},请求报文param:{}", responseMsg, param);
                }
            } else {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                InputStream in = postMethod.getResponseBodyAsStream();
                int len = 0;
                byte[] buf = new byte[1024];
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                responseMsg = out.toString("UTF-8");
                System.out.println(responseMsg);
            }
        } catch (Exception e) {
            log.error("sendByPost:调用全渠道推送权限接口出错!,desc:{},param:{}", e, param);
            e.printStackTrace();
        } finally {
            postMethod.releaseConnection();
        }
        return retStr;
    }
}
