package com.hjsj.hrms.servlet.hirelogin;

import cfca.sadk.control.sip.api.SIPDecryptionBuilder;
import cfca.sadk.control.sip.api.SIPDecryptor;
import com.hjsj.hrms.transaction.sys.sms.GZBankSmsSend;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.constant.SystemConfig;
import org.mortbay.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class HireCfcaRandom extends HttpServlet {
    private Logger log = LoggerFactory.getLogger(HireCfcaRandom.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = null;
        HashMap<String, String> map = new HashMap<String, String>();//map回写数据
        try {
            log.info("招聘外网CFCA加密控件进入！");
            out = response.getWriter();
            String sm2PfxFile = SystemConfig.getPropertyValue("sm2PfxFile") + File.separator + "sm2Encrypt.sm2";
            SIPDecryptor decryptor = SIPDecryptionBuilder.sm2().config(sm2PfxFile, "111111");
            String serverRandom = decryptor.generateServerRandom();
            map.put("serverRandom", serverRandom);
            out.write(JSON.toString(map));
            return;
        }catch (Exception ex) {
            log.error("贵州银行-->招聘外网加密控件加密失败！异常信息ErrorMessage:{}", ex);
        }finally{
            PubFunc.closeResource(out);
        }
    }
}
