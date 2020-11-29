package com.hjsj.hrms.bankgz.utils;

import com.hrms.frame.dao.utility.DateUtils;
import com.union.api.UnionCSSP;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


public class MacAddressUtil {
    private static Logger log = LoggerFactory.getLogger(MacAddressUtil.class);

    /**
     * 获取全密码平台mac地址
     *
     * @param content //加密内容
     * @param keyName //数据接收方的mac密钥名称
     * @return
     */
    public static String getMacAddress(String content, String keyName) {
        String mac = "";
        try {
            String classPath = MacAddressUtil.class.getResource("/").getPath().substring(1);
            System.setProperty("cn.keyou.platform.api3.config.file", classPath + "serverList.conf");
            UnionCSSP cssp = new UnionCSSP();
            byte[] byt = content.getBytes("utf-8");
            UnionCSSP.Recv recv = cssp.unionGenMac("1", "1", keyName, "", "", "", "1", "16", byt);
            if (recv != null) {
                if (recv.getResponseCode() == 0) {
                    mac = recv.getMac();
                } else {
                    log.error("贵州银行-->调用全密码平台返回错误信息ErrorMessage:{}", recv.getResponseRemark());
                }
            }
        } catch (Exception e) {
            log.error("贵州银行-->调用全密码平台失败！异常信息ErrorMessage:{}", e);
        }
        return mac;
    }

    /**
     * 验证全密码平台mac地址
     *
     * @param content //加密内容
     * @param keyName // 数据发送方的mac密钥名称
     * @return
     */
    public static boolean verifyMacAddress(String content, String keyName, String macVal) {
        boolean flag = false;
        try {
            String classPath = MacAddressUtil.class.getResource("/").getPath().substring(1);
            System.setProperty("cn.keyou.platform.api3.config.file", classPath + "serverList.conf");
            UnionCSSP cssp = new UnionCSSP();
            byte[] byt = content.getBytes("utf-8");
            UnionCSSP.Recv recv = cssp.unionVerifyMac("1", "1", keyName, "", "", "", "1", byt, macVal);
            if (recv != null) {
                if (recv.getResponseCode() == 0) {
                    flag = true;
                } else {
                    log.error("贵州银行-->调用全密码平台验证mac返回错误信息ErrorMessage:{},验证报文:{},mac:{}", recv.getResponseRemark(), content, macVal);
                }
            }
        } catch (Exception e) {
            log.error("贵州银行-->调用全密码平台验证mac失败！异常信息ErrorMessage:{}", e);
        }
        return flag;
    }

    /**
     * 生成全局流水号：系统简称+雪花算法id
     * <p>
     * eg: HRS184137205940227
     *
     * @return
     * @throws Exception
     */
    public static String getGlbSrvNo() {
        String glbSrvNo = snowflake.nextIdStr();

        //# 此为补救措施，正常情况下不会使用
        if (StringUtils.isBlank(glbSrvNo)) {
            Date date = new Date();
            String datetime = DateUtils.format(date, "yyyyMMddHHmmssS");
            glbSrvNo = datetime.substring(4, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17) + Math.round(Math.ceil(Math.random() * 1000));
        }
        return SYSTEM_NAME.concat(glbSrvNo);
    }

    public final static String SYSTEM_NAME = "HRS";
    public static Snowflake snowflake = Snowflake.Singleton.SNOWFLAKE.get();
}
