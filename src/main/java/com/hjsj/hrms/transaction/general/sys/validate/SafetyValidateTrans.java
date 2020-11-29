package com.hjsj.hrms.transaction.general.sys.validate;

import com.hjsj.hrms.bankgz.utils.GzBankHttpUtil;
import com.hjsj.hrms.transaction.mobileapp.utils.Tools;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.bankgz.utils.MacAddressUtil;
import com.hjsj.hrms.bankgz.utils.SendKafkaGlblSrvNo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页
 *
 * @author guodd
 * Date: 2018年12月28日
 */
public class SafetyValidateTrans extends IBusiness {
    private Logger log = Logger.getLogger(this.getClass().getName());

    public void execute() throws GeneralException {

        String transType = (String) this.formHM.get("transType");
        if ("cfcaPwdcheck".equals(transType)) {
            String encryptedPwd = (String) this.formHM.get("encryptedPwd");
            String encryptedClientRandom = (String) this.formHM.get("encryptedClientRandom");
            String serverRandom = (String) this.formHM.get("serverRandom");
            String account = (String) this.formHM.get("account");
            String hrpserver = SystemConfig.getPropertyValue("hrpserver");//服务器ip
            Date date = new Date();
            String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
            String timestamp = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS");
            Map<String, String> systemHeaderMap = new HashMap<String, String>();
            systemHeaderMap.put("sourceSystemCode", "HRS");//HRS系统码
            systemHeaderMap.put("sinkSystemCode", "UAP");//统一认证系统码
            systemHeaderMap.put("actionId", "UAP6021101");//交易码
            systemHeaderMap.put("actionVersion", "v1");
            String sourceJnlNo = "HRS0" + datetime.substring(4, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17) + Math.round(Math.ceil(Math.random() * 1000));
            systemHeaderMap.put("sourceJnlNo", sourceJnlNo);//请求方流水号
            String sNo = MacAddressUtil.getGlbSrvNo();
            SendKafkaGlblSrvNo.registerGlbSrlNo(sNo, "uap6021101");//登记流水号
            systemHeaderMap.put("glblSrvNo", sNo);//全局流水号 雪花算法
            systemHeaderMap.put("timestamp", timestamp);
            systemHeaderMap.put("ip", hrpserver);
            Map<String, String> bodyMap = new HashMap<String, String>();
            bodyMap.put("appId", "HRS");
            bodyMap.put("authType", "pwd");
            bodyMap.put("transType", "viewPay");
            bodyMap.put("account", account);
            bodyMap.put("credential", encryptedPwd);
            bodyMap.put("randomSign", serverRandom);
            bodyMap.put("encryptedRc", encryptedClientRandom);
            Map<String, Map> requestDataMap = new HashMap<String, Map>();
            requestDataMap.put("systemHeader", systemHeaderMap);
            requestDataMap.put("body", bodyMap);
            Map<String, Map> dataMap = new HashMap<String, Map>();
            dataMap.put("requestData", requestDataMap);
            String uap_checkpwd_url = SystemConfig.getPropertyValue("uap_checkpwd_url");//请求接口地址
            boolean result = false;
            String detail = "认证失败！";
            try {
                String mac = Tools.getMACAddress();
                systemHeaderMap.put("mac", mac);
                String urlParams = JSONObject.fromObject(dataMap).toString();
                String macVal = MacAddressUtil.getMacAddress(urlParams, "GZYH.UAPA_node.zak");
                log.info("统一认证校验pwd请求报文：" + urlParams + " 请求地址:" + uap_checkpwd_url + "  macVal:" + macVal);
                String responseString = GzBankHttpUtil.sendParamToUrl(uap_checkpwd_url, urlParams, macVal, "uap6021101");
                log.info("统一认证校验pwd响应报文：" + responseString);
                JSONObject responseObject = JSONObject.fromObject(responseString);
                String responseMessage = responseObject.getString("responseMessage");
                String responseDetail = responseObject.getString("responseDetail");
                String responseCode = responseObject.getString("responseCode");
                if ("success".equals(responseMessage)) {
                    result = true;
                    detail = responseDetail;
                } else {
                    result = false;
                    detail = responseDetail;
                    if ("UAPAMR0006".equals(responseCode)) {
                        detail = detail + "，请重新输入密码！";
                    } else if ("UAPAMR0032".equals(responseCode)) {
                        detail = "密码错误！";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.formHM.put("result", result);
                this.formHM.put("detail", detail);
            }
        } else if ("pwdcheck".equals(transType)) {
            String password = (String) this.formHM.get("password");
            String realPwd = this.userView.getPassWord();
            //如果密码加密，解密
            if (ConstantParamter.isEncPwd()) {
                Des des = new Des();
                realPwd = des.DecryPwdStr(realPwd);
            }
            if (realPwd.equals(password)) {
                this.formHM.put("result", true);
            } else {
                this.formHM.put("result", false);
            }
        } else if ("getPhone".equals(transType)) {
            String a0100 = this.userView.getA0100();
            String pre = this.userView.getDbname();
            String msg = "";
            if (pre == null || a0100 == null || "".equalsIgnoreCase(pre) || "".equals(a0100)) {
                msg = ResourceFactory.getProperty("selfservice.module.pri");
                this.formHM.put("result", false);
                this.formHM.put("msg", msg);
                return;
            }
            String phone = this.userView.getUserTelephone();
            if (phone == null || phone.length() < 1) {
                this.formHM.put("result", false);
                this.formHM.put("msg", ResourceFactory.getProperty("label.gz.noPhoneAddress"));
            }
            String delaytime = SystemConfig.getPropertyValue("validatecode_time");
            delaytime = delaytime.length() < 1 ? "180" : delaytime;

            this.formHM.put("phone", phone);
            this.formHM.put("delaytime", delaytime);
            this.formHM.put("result", true);

        } else if ("sendSMS".equals(transType)) {
            this.formHM.put("result", true);
        }

    }

}
