package com.hjsj.hrms.module.system.fingerprint.businessobject.impl;

import com.hjsj.hrms.bankgz.utils.GzBankHttpUtil;
import com.hjsj.hrms.bankgz.utils.SendKafkaGlblSrvNo;
import com.hjsj.hrms.module.system.fingerprint.businessobject.FingerPrintInfoService;
import com.hjsj.hrms.module.system.fingerprint.dao.FingerPrintInfoDao;
import com.hjsj.hrms.module.system.fingerprint.dao.impl.FingerPrintInfoDaoImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.bankgz.utils.MacAddressUtil;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import com.union.utils.Hex;
import com.union.utils.SM3;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @author wangchunyu
 */
public class FingerPrintInfoServiceImpl implements FingerPrintInfoService {

    private Logger log = LoggerFactory.getLogger(FingerPrintInfoServiceImpl.class);
    private Connection connection;
    private UserView userView;
    private int tabId = 0;

    public FingerPrintInfoServiceImpl(Connection connection, UserView userview, int tabid) {
        this.connection = connection;
        this.userView = userview;
        this.tabId = tabid;
    }

    @Override
    public Map initData() throws SQLException, GeneralException {
        Map dataMap = new HashMap();
        FingerPrintInfoDao saveFaceDao = new FingerPrintInfoDaoImpl(this.connection);
        saveFaceDao.searchInitData(this.userView.getUserName() + "templet_" + this.tabId, dataMap);
        return dataMap;
    }

    @Override
    public boolean saveFaceData(String faceData, String jobNumber) throws Exception {

        FingerPrintInfoDao saveFaceDao = new FingerPrintInfoDaoImpl(this.connection);
//        String result = PubFunc.convertTo64Base(PubFunc.fileToByte(facePath));
        boolean flag = saveFaceDao.saveFaceData(faceData, this.userView.getUserName() + "templet_" + this.tabId);
        if (flag) {
            String hrpserver = SystemConfig.getPropertyValue("hrpserver");//服务器ip
            String uap_addFace_url = SystemConfig.getPropertyValue("uap_addFace_url");
            Date date = new Date();
            String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
            String timestamp = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS");
            Map<String, String> systemHeaderMap = new HashMap();
            systemHeaderMap.put("sourceSystemCode", "HRS");//HRS系统码
            systemHeaderMap.put("sinkSystemCode", "UAP");//统一认证系统码
            systemHeaderMap.put("actionId", "uap6026103");//交易码
            systemHeaderMap.put("actionVersion", "v1");
            String sNo = MacAddressUtil.getGlbSrvNo();
            SendKafkaGlblSrvNo.registerGlbSrlNo(sNo, "uap6026103");//登记流水号
            String sourceJnlNo = "HRS0" + datetime.substring(4, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17) + Math.round(Math.ceil(Math.random() * 1000));
            systemHeaderMap.put("sourceJnlNo", sourceJnlNo);//请求方流水号
            systemHeaderMap.put("glblSrvNo", sNo);//全局流水号 雪花算法
            systemHeaderMap.put("timestamp", timestamp);
            systemHeaderMap.put("ip", hrpserver);
            systemHeaderMap.put("mac", "");
            Map<String, Object> bodyMap = new HashMap();
            bodyMap.put("appId", "HRS");
            bodyMap.put("account", jobNumber);
            bodyMap.put("transType", "login");
            String cur_date = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss");
            bodyMap.put("date", cur_date);
            String inData = "HRS" + jobNumber + cur_date + "/+xo5t6/kfv/i12+9d8CaA=";
            SM3 sm3 = new SM3();
            byte[] digest = sm3.Digest(inData.getBytes());
            String signature = Hex.encode(digest);
            bodyMap.put("signature", signature);
            Map<String, Map> requestDataMap = new HashMap();
            requestDataMap.put("systemHeader", systemHeaderMap);
            requestDataMap.put("body", bodyMap);
            Map<String, String> filesMap = new HashMap();
            filesMap.put("fileName", "credential");
            filesMap.put("content", faceData);
            Map<String, Object> dataMap = new HashMap();
            dataMap.put("requestData", requestDataMap);
            List fileList = new ArrayList<>();
            fileList.add(filesMap);
            dataMap.put("files", fileList);
            String urlParams = JSONObject.fromObject(dataMap).toString();
            String macVal = MacAddressUtil.getMacAddress(urlParams, "GZYH.UAPA_node.zak");
            log.info("调用统一认证新增指纹请求报文：" + urlParams + " 请求地址:" + uap_addFace_url + "  macVal:" + macVal);
            String responseString = null;
            try {
                responseString = GzBankHttpUtil.sendParamToUrl(uap_addFace_url, urlParams, macVal, "");
            } catch (Exception e) {
                log.error("贵州银行--->调用统一认证人脸新增接口失败,错误信息为ErrorMessage:{}，请求地址url:{},请求报文param:{}", e, uap_addFace_url, urlParams);
                throw e;
            }
            log.info("调用统一认证新增指纹响应报文：" + responseString);
            JSONObject responseObject = JSONObject.fromObject(responseString);
            String responseMessage = responseObject.getString("responseMessage");
            String responseDetail = responseObject.getString("responseDetail");
            String responseCode = responseObject.getString("responseCode");
            if ("success".equals(responseMessage)) {
                flag = true;
            } else {
                SendKafkaGlblSrvNo.updateGlbSrlNo(sNo, "1", responseCode, responseString);
                flag = false;
            }
        }
        return flag;
    }

    /**
     * 保存指纹数据，并调用统一认证新增指纹接口
     *
     * @param fingerDataMap
     * @return
     * @throws SQLException
     * @throws GeneralException
     */
    @Override
    public boolean saveFingerData(Map<String, String> fingerDataMap, String jobNumber) throws Exception {

        FingerPrintInfoDao saveFaceDao = new FingerPrintInfoDaoImpl(this.connection);
        boolean flag = saveFaceDao.saveFingerData(fingerDataMap, this.userView.getUserName() + "templet_" + this.tabId);
        if (flag) {
            String hrpserver = SystemConfig.getPropertyValue("hrpserver");//服务器ip
            String uap_addFinger_url = SystemConfig.getPropertyValue("uap_addFinger_url");
            Date date = new Date();
            String timestamp = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS");
            String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
            Map<String, String> systemHeaderMap = new HashMap<String, String>();
            systemHeaderMap.put("sourceSystemCode", "HRS");//HRS系统码
            systemHeaderMap.put("sinkSystemCode", "UAP");//统一认证系统码
            systemHeaderMap.put("actionId", "uap6025101");//交易码
            systemHeaderMap.put("actionVersion", "v1");
            String sNo = MacAddressUtil.getGlbSrvNo();
            SendKafkaGlblSrvNo.registerGlbSrlNo(sNo, "uap6025101");//登记流水号
            String sourceJnlNo = "HRS0" + datetime.substring(4, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17) + Math.round(Math.ceil(Math.random() * 1000));
            systemHeaderMap.put("sourceJnlNo", sourceJnlNo);//请求方流水号
            systemHeaderMap.put("glblSrvNo", sNo);//全局流水号 雪花算法
            systemHeaderMap.put("timestamp", timestamp);
            systemHeaderMap.put("ip", hrpserver);
            Map<String, Object> bodyMap = new HashMap();
            Map<String, String> paramMap = new HashMap();
            paramMap.put("targetAccount", jobNumber);
            paramMap.put("finger0", fingerDataMap.get("finger0") == null ? "" : fingerDataMap.get("finger0"));
            paramMap.put("finger1", fingerDataMap.get("finger1") == null ? "" : fingerDataMap.get("finger1"));
            paramMap.put("finger2", fingerDataMap.get("finger2") == null ? "" : fingerDataMap.get("finger2"));
            paramMap.put("finger3", fingerDataMap.get("finger3") == null ? "" : fingerDataMap.get("finger3"));
            paramMap.put("finger4", fingerDataMap.get("finger4") == null ? "" : fingerDataMap.get("finger4"));
            paramMap.put("finger5", fingerDataMap.get("finger5") == null ? "" : fingerDataMap.get("finger5"));
            paramMap.put("finger6", fingerDataMap.get("finger6") == null ? "" : fingerDataMap.get("finger6"));
            paramMap.put("finger7", fingerDataMap.get("finger7") == null ? "" : fingerDataMap.get("finger7"));
            paramMap.put("finger8", fingerDataMap.get("finger8") == null ? "" : fingerDataMap.get("finger8"));
            paramMap.put("finger9", fingerDataMap.get("finger9") == null ? "" : fingerDataMap.get("finger9"));
            bodyMap.put("param", paramMap);
            bodyMap.put("appId", "HRS");
            bodyMap.put("account", jobNumber);
            bodyMap.put("token", "");
            String cur_date = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss");
            bodyMap.put("date", cur_date);
            String inData = "HRS" + jobNumber + cur_date + JSONObject.fromObject(paramMap).toString() + "/+xo5t6/kfv/i12+9d8CaA=";
            SM3 sm3 = new SM3();
            byte[] digest = sm3.Digest(inData.getBytes());
            String signature = Hex.encode(digest);
            bodyMap.put("signature", signature);
            Map<String, Map> requestDataMap = new HashMap();
            requestDataMap.put("systemHeader", systemHeaderMap);
            requestDataMap.put("body", bodyMap);
            Map<String, Map> dataMap = new HashMap<String, Map>();
            dataMap.put("requestData", requestDataMap);
            String urlParams = JSONObject.fromObject(dataMap).toString();
            String macVal = MacAddressUtil.getMacAddress(urlParams, "GZYH.UAPA_node.zak");
            log.info("调用统一认证新增指纹请求报文：" + urlParams + " 请求地址:" + uap_addFinger_url + "  macVal:" + macVal);
            String responseString = null;
            try {
                responseString = GzBankHttpUtil.sendParamToUrl(uap_addFinger_url, urlParams, macVal, "");
            } catch (Exception e) {
                log.error("贵州银行--->调用统一认证指纹新增接口失败,错误信息为ErrorMessage:{}，请求地址url:{},请求报文param:{}", e, uap_addFinger_url, urlParams);
                throw e;
            }
            log.info("调用统一认证新增指纹响应报文：" + responseString);
            JSONObject responseObject = JSONObject.fromObject(responseString);
            String responseMessage = responseObject.getString("responseMessage");
            String responseDetail = responseObject.getString("responseDetail");
            String responseCode = responseObject.getString("responseCode");
            if ("success".equals(responseMessage)) {
                flag = true;
            } else {
                SendKafkaGlblSrvNo.updateGlbSrlNo(sNo, "1", responseCode, responseString);
                flag = false;
            }
        }
        return flag;
    }

    /**
     * 调用单个指纹比对接口
     *
     * @param jobNumber
     * @param featureFinger
     * @param finger
     * @return
     * @throws Exception
     */
    @Override
    public boolean checkFingerData(String jobNumber, String featureFinger, String finger) throws Exception {
        boolean flag = false;
        String hrpserver = SystemConfig.getPropertyValue("hrpserver");//服务器ip
        String uap_singleFinger_auth_url = SystemConfig.getPropertyValue("uap_singleFinger_auth_url");
        Date date = new Date();
        String timestamp = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS");
        String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
        Map<String, String> systemHeaderMap = new HashMap<String, String>();
        systemHeaderMap.put("sourceSystemCode", "HRS");//HRS系统码
        systemHeaderMap.put("sinkSystemCode", "UAP");//统一认证系统码
        systemHeaderMap.put("actionId", "uap6025105");//交易码
        systemHeaderMap.put("actionVersion", "v1");
        String sNo = MacAddressUtil.getGlbSrvNo();
        SendKafkaGlblSrvNo.registerGlbSrlNo(sNo, "uap6025105");//登记流水号
        String sourceJnlNo = "HRS0" + datetime.substring(4, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17) + Math.round(Math.ceil(Math.random() * 1000));
        systemHeaderMap.put("sourceJnlNo", sourceJnlNo);//请求方流水号
        systemHeaderMap.put("glblSrvNo", sNo);//全局流水号 雪花算法
        systemHeaderMap.put("timestamp", timestamp);
        systemHeaderMap.put("ip", hrpserver);
        Map<String, Object> bodyMap = new HashMap();
        Map<String, String> paramMap = new HashMap();
        paramMap.put("userId", jobNumber);
        paramMap.put("finger", finger);
        paramMap.put("feature", featureFinger);
        bodyMap.put("param", paramMap);
        bodyMap.put("appId", "HRS");
        bodyMap.put("account", jobNumber);
        bodyMap.put("token", "");
        String cur_date = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss");
        bodyMap.put("date", cur_date);
        String inData = "HRS" + jobNumber + cur_date + JSONObject.fromObject(paramMap).toString() + "/+xo5t6/kfv/i12+9d8CaA=";
        SM3 sm3 = new SM3();
        byte[] digest = sm3.Digest(inData.getBytes());
        String signature = Hex.encode(digest);
        bodyMap.put("signature", signature);
        Map<String, Map> requestDataMap = new HashMap();
        requestDataMap.put("systemHeader", systemHeaderMap);
        requestDataMap.put("body", bodyMap);
        Map<String, Map> dataMap = new HashMap<String, Map>();
        dataMap.put("requestData", requestDataMap);
        String urlParams = JSONObject.fromObject(dataMap).toString();
        String macVal = MacAddressUtil.getMacAddress(urlParams, "GZYH.UAPA_node.zak");
        log.info("调用统一认证单个指纹比对请求报文：" + urlParams + " 请求地址:" + uap_singleFinger_auth_url + "  macVal:" + macVal);
        String responseString = null;
        try {
            responseString = GzBankHttpUtil.sendParamToUrl(uap_singleFinger_auth_url, urlParams, macVal, "");
        } catch (Exception e) {
            log.error("贵州银行--->调用统一认证单个指纹比对接口失败,错误信息为ErrorMessage:{},请求地址url:{},请求报文param:{}", e, uap_singleFinger_auth_url, urlParams);
            throw e;
        }
        log.info("调用统一认证单个指纹比对响应报文：" + responseString);
        JSONObject responseObject = JSONObject.fromObject(responseString);
        String responseMessage = responseObject.getString("responseMessage");
        String responseDetail = responseObject.getString("responseDetail");
        String responseCode = responseObject.getString("responseCode");
        if ("success".equals(responseMessage)) {
            flag = true;
        } else {
            SendKafkaGlblSrvNo.updateGlbSrlNo(sNo, "1", responseCode, responseString);
            flag = false;
        }
        return flag;
    }

    /**
     * 调用指纹审核接口
     *
     * @param fingerDataMap 相关参数
     * @param jobNumber
     * @return
     * @throws Exception
     */
    @Override
    public boolean revieFingerData(Map<String, String> fingerDataMap, String jobNumber) throws Exception {
        boolean flag = false;
        String hrpserver = SystemConfig.getPropertyValue("hrpserver");//服务器ip
        String uap_auditFinger_url = SystemConfig.getPropertyValue("uap_auditFinger_url");
        Date date = new Date();
        String timestamp = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS");
        String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
        Map<String, String> systemHeaderMap = new HashMap<String, String>();
        systemHeaderMap.put("sourceSystemCode", "HRS");//HRS系统码
        systemHeaderMap.put("sinkSystemCode", "UAP");//统一认证系统码
        systemHeaderMap.put("actionId", "uap6025106");//交易码
        systemHeaderMap.put("actionVersion", "v1");
        String sNo = MacAddressUtil.getGlbSrvNo();
        SendKafkaGlblSrvNo.registerGlbSrlNo(sNo, "uap6025106");//登记流水号
        String sourceJnlNo = "HRS0" + datetime.substring(4, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17) + Math.round(Math.ceil(Math.random() * 1000));
        systemHeaderMap.put("sourceJnlNo", sourceJnlNo);//请求方流水号
        systemHeaderMap.put("glblSrvNo", sNo);//全局流水号 雪花算法
        systemHeaderMap.put("timestamp", timestamp);
        systemHeaderMap.put("ip", hrpserver);
        Map<String, Object> bodyMap = new HashMap();
        Map<String, String> paramMap = new HashMap();
        paramMap.put("userId", jobNumber);
        paramMap.put("checkFinger0", fingerDataMap.get("finger0") == null ? "" : fingerDataMap.get("finger0"));
        paramMap.put("checkFinger1", fingerDataMap.get("finger1") == null ? "" : fingerDataMap.get("finger1"));
        paramMap.put("checkFinger2", fingerDataMap.get("finger2") == null ? "" : fingerDataMap.get("finger2"));
        paramMap.put("checkFinger3", fingerDataMap.get("finger3") == null ? "" : fingerDataMap.get("finger3"));
        paramMap.put("checkFinger4", fingerDataMap.get("finger4") == null ? "" : fingerDataMap.get("finger4"));
        paramMap.put("checkFinger5", fingerDataMap.get("finger5") == null ? "" : fingerDataMap.get("finger5"));
        paramMap.put("checkFinger6", fingerDataMap.get("finger6") == null ? "" : fingerDataMap.get("finger6"));
        paramMap.put("checkFinger7", fingerDataMap.get("finger7") == null ? "" : fingerDataMap.get("finger7"));
        paramMap.put("checkFinger8", fingerDataMap.get("finger8") == null ? "" : fingerDataMap.get("finger8"));
        paramMap.put("checkFinger9", fingerDataMap.get("finger9") == null ? "" : fingerDataMap.get("finger9"));
        bodyMap.put("param", paramMap);
        bodyMap.put("appId", "HRS");
        bodyMap.put("account", jobNumber);
        bodyMap.put("token", "");
        String cur_date = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss");
        bodyMap.put("date", cur_date);
        String inData = "HRS" + jobNumber + cur_date + JSONObject.fromObject(paramMap).toString() + "/+xo5t6/kfv/i12+9d8CaA=";
        SM3 sm3 = new SM3();
        byte[] digest = sm3.Digest(inData.getBytes());
        String signature = Hex.encode(digest);
        bodyMap.put("signature", signature);
        Map<String, Map> requestDataMap = new HashMap();
        requestDataMap.put("systemHeader", systemHeaderMap);
        requestDataMap.put("body", bodyMap);
        Map<String, Map> dataMap = new HashMap<String, Map>();
        dataMap.put("requestData", requestDataMap);
        String urlParams = JSONObject.fromObject(dataMap).toString();
        String macVal = MacAddressUtil.getMacAddress(urlParams, "GZYH.UAPA_node.zak");
        log.info("调用统一认证指纹审核请求报文：" + urlParams + " 请求地址:" + uap_auditFinger_url + "  macVal:" + macVal);
        String responseString = null;
        try {
            responseString = GzBankHttpUtil.sendParamToUrl(uap_auditFinger_url, urlParams, macVal, "");
        } catch (Exception e) {
            log.error("贵州银行--->调用统一认证指纹复核接口失败,错误信息为ErrorMessage:{}，请求地址url:{},请求报文param:{}", e, uap_auditFinger_url, urlParams);
            throw e;
        }
        log.info("调用统一认证审核指纹响应报文：" + responseString);
        JSONObject responseObject = JSONObject.fromObject(responseString);
        String responseMessage = responseObject.getString("responseMessage");
        String responseDetail = responseObject.getString("responseDetail");
        String responseCode = responseObject.getString("responseCode");
        if ("success".equals(responseMessage)) {
            flag = true;
        } else {
            SendKafkaGlblSrvNo.updateGlbSrlNo(sNo, "1", responseCode, responseString);
            flag = false;
        }
        return flag;
    }

    /**
     * 调用重置指纹接口
     *
     * @param jobNumber
     * @return
     * @throws Exception
     */
    @Override
    public boolean resetFingerData(String jobNumber) throws Exception {
        boolean flag = false;
        String hrpserver = SystemConfig.getPropertyValue("hrpserver");//服务器ip
        String uap_resetFinger_url = SystemConfig.getPropertyValue("uap_resetFinger_url");
        Date date = new Date();
        String timestamp = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS");
        String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
        Map<String, String> systemHeaderMap = new HashMap<String, String>();
        systemHeaderMap.put("sourceSystemCode", "HRS");//HRS系统码
        systemHeaderMap.put("sinkSystemCode", "UAP");//统一认证系统码
        systemHeaderMap.put("actionId", "uap6025104");//交易码
        systemHeaderMap.put("actionVersion", "v1");
        String sNo = MacAddressUtil.getGlbSrvNo();
        SendKafkaGlblSrvNo.registerGlbSrlNo(sNo, "uap6025104");//登记流水号
        String sourceJnlNo = "HRS0" + datetime.substring(4, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17) + Math.round(Math.ceil(Math.random() * 1000));
        systemHeaderMap.put("sourceJnlNo", sourceJnlNo);//请求方流水号
        systemHeaderMap.put("glblSrvNo", sNo);//全局流水号 雪花算法
        systemHeaderMap.put("timestamp", timestamp);
        systemHeaderMap.put("ip", hrpserver);
        Map<String, Object> bodyMap = new HashMap();
        Map<String, String> paramMap = new HashMap();
        paramMap.put("userId", jobNumber);
        bodyMap.put("param", paramMap);
        bodyMap.put("appId", "HRS");
        bodyMap.put("account", jobNumber);
        bodyMap.put("token", "");
        String cur_date = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss");
        bodyMap.put("date", cur_date);
        String inData = "HRS" + jobNumber + cur_date + JSONObject.fromObject(paramMap).toString() + "/+xo5t6/kfv/i12+9d8CaA=";
        SM3 sm3 = new SM3();
        byte[] digest = sm3.Digest(inData.getBytes());
        String signature = Hex.encode(digest);
        bodyMap.put("signature", signature);
        Map<String, Map> requestDataMap = new HashMap();
        requestDataMap.put("systemHeader", systemHeaderMap);
        requestDataMap.put("body", bodyMap);
        Map<String, Map> dataMap = new HashMap<String, Map>();
        dataMap.put("requestData", requestDataMap);
        String urlParams = JSONObject.fromObject(dataMap).toString();
        String macVal = MacAddressUtil.getMacAddress(urlParams, "GZYH.UAPA_node.zak");
        log.info("调用统一认证重置指纹请求报文：" + urlParams + " 请求地址:" + uap_resetFinger_url + "  macVal:" + macVal);
        String responseString = null;
        try {
            responseString = GzBankHttpUtil.sendParamToUrl(uap_resetFinger_url, urlParams, macVal, "");
        } catch (Exception e) {
            log.error("贵州银行--->调用统一认证重置指纹接口失败,错误信息为ErrorMessage:{}，请求地址url:{},请求报文param:{}", e, uap_resetFinger_url, urlParams);
            throw e;
        }
        log.info("调用统一认证重置指纹响应报文：" + responseString);
        JSONObject responseObject = JSONObject.fromObject(responseString);
        String responseMessage = responseObject.getString("responseMessage");
        String responseDetail = responseObject.getString("responseDetail");
        String responseCode = responseObject.getString("responseCode");
        if ("success".equals(responseMessage)) {
            flag = true;
        } else {
            SendKafkaGlblSrvNo.updateGlbSrlNo(sNo, "1", responseCode, responseString);
            flag = false;
        }
        return flag;
    }


}
