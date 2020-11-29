package com.hjsj.hrms.servlet.sys;

import com.hjsj.hrms.businessobject.sys.SysParamConstant;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.servlet.hirelogin.HireCfcaRandom;
import com.hrms.struts.constant.SystemConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
/**
 * 用于生成验证码图片并把验证码文字保存到session中
 * @Title:        VaildateCodeServlet.java
 * @Description:  生成验证码图片并把验证码文字保存到session中
 * @Company:      hjsj     
 * @Create time:  2016-6-1 下午01:54:07
 * @author        chenxg
 * @version       1.0
 */
public class VaildateCodeServlet extends HttpServlet {
    private Logger log = LoggerFactory.getLogger(VaildateCodeServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            log.info("招聘外网进入生成验证码类！requset:{}",request.getParameterMap());
            HttpSession session = request.getSession();
            //区分是内网还是外网；=0：外网；=1：内网；默认为1；
            int channel;
            int codelen;
            String bosflag = null;
			try {
				channel = Integer.valueOf(StringUtils.isEmpty((String)request.getParameter("channel"))
				        ? "1" : (String)request.getParameter("channel"));
			} catch (Exception e1) {
				 channel = 1;
			}
			//外部传入参数异常处理，验证码最小长度为4，防止传入长度为0
			try {
				codelen = Integer.valueOf(StringUtils.isEmpty((String)request.getParameter("codelen"))
				        ? "6" : (String)request.getParameter("codelen"));
			} catch (Exception e) {
				codelen = 4;
			}
			try {
				bosflag = StringUtils.isEmpty((String)request.getParameter("bosflag"))
				        ? "hcm" : (String)request.getParameter("bosflag");
			} catch (Exception e) {
				bosflag = "hcm";
			}
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);
            // 表明生成的响应的是图片
            response.setContentType("image/jpeg");
            
            String validatecodelen = SystemConfig.getPropertyValue(SysParamConstant.VALIDATECODELEN);
            String validatecodeinfo = SystemConfig.getPropertyValue(SysParamConstant.VALIDATECODEINFO);
            if (channel == 1 && validatecodelen != null && validatecodelen.length() > 0) // zzk 加以内外网区分
                codelen = Integer.parseInt(validatecodelen);
            //验证码最小长度为4
            codelen = codelen<4?4:codelen;
            StringBuffer strSrc = new StringBuffer();
            if (validatecodeinfo != null && validatecodeinfo.length() > 0)
                strSrc.append(validatecodeinfo);
            else
                strSrc.append("QAZWSXEDCRFVTGBYHNUJMIKLP123456789");
            
            BufferedImage image = null;
            image = ServletUtilities.validateCodeImage(codelen, strSrc, session, channel, bosflag);
            ImageIO.write(image, "JPEG", response.getOutputStream());
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
