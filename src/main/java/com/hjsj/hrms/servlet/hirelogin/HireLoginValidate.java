package com.hjsj.hrms.servlet.hirelogin;

import com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm;
import com.hjsj.hrms.businessobject.hire.AutoSendEMailBo;
import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.module.hire.businessobject.ResumeBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CreateSequence;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.struts.admin.OnlineListener;
import com.hrms.struts.admin.OnlineUserView;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import org.apache.commons.collections.FastHashMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.mortbay.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 登录验证
 *
 * @author akuan
 */
public class HireLoginValidate extends HttpServlet {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.info("进入招聘外网request:{}", request.getParameterMap());

        HttpSession session = request.getSession();
        //将特殊字符还原
        String new_hire = request.getParameter("new_hire");
        new_hire = SafeCode.decode(new_hire);
        new_hire = PubFunc.hireKeyWord_filter(new_hire);
        Connection connection = null;
        ResultSet resultset = null;
        EmployPortalForm employPortalForm = (EmployPortalForm) session.getAttribute("employPortalForm");
        String info = "";
        PrintWriter out = null;
        try {
            String sessionValidateCode = (String) session.getAttribute("validatecode");
            // session中的验证码取后即销毁，避免安全漏洞
            session.removeAttribute("validatecode");

            connection = (Connection) AdminDb.getConnection();

            //生成新招聘外网激活邮件链接地址
            StringBuffer url = new StringBuffer("https://" + request.getServerName() + ":" + request.getServerPort());
            //系统外部地址
            String zp_url = SystemConfig.getPropertyValue("zp_url");
            if (StringUtils.isNotBlank(zp_url)){
                url = new StringBuffer(SystemConfig.getPropertyValue("zp_url"));
            }
            if ("true".equals(new_hire)) {
                response.setCharacterEncoding("utf-8");
                response.setContentType("text/html; charset=utf-8");
                out = response.getWriter();

                if (!url.toString().contains("/zp.html#/signin"))
                    url.append("/zp.html#/signin");

                String[] reParams = request.getParameterValues("__xml");
                if (reParams.length == 0 || StringUtils.isBlank(reParams[0])) {
                    return;
                }

                Map values = (Map<String, String>) JSON.parse(reParams[0]);

                String loginName = (String) values.get("loginName");
                ;//登录邮箱
                loginName = SafeCode.decode(loginName);

                String password = (String) values.get("password");// 密码
                password = SafeCode.decode(password);
                password = PubFunc.hireKeyWord_filter_reback(password);

                String encryptedClientRandom = (String) values.get("encryptedClientRandom");// CFCA加密后的随机数
                encryptedClientRandom = SafeCode.decode(encryptedClientRandom);
                encryptedClientRandom = PubFunc.hireKeyWord_filter_reback(encryptedClientRandom);

                String serverRandom = (String) values.get("serverRandom");// CFCA后端传的随机数
                serverRandom = SafeCode.decode(serverRandom);
                serverRandom = PubFunc.hireKeyWord_filter_reback(serverRandom);

                String blackValue = (String) values.get("blackValue");// 黑名单文本框值
                blackValue = SafeCode.decode(blackValue);

                String equalFlag = (String) values.get("equalFlag");// 唯一性指标是否与黑名单指标相同3：相同
                equalFlag = SafeCode.decode(equalFlag);

                HashMap<String, String> map = new HashMap<String, String>();

                String vaildcode = (String) values.get("vaildcode");
                vaildcode = SafeCode.decode(vaildcode);
                vaildcode = PubFunc.hireKeyWord_filter(vaildcode);

                String operate = request.getParameter("operate");
				log.info("招聘外网界面输入的验证码vaildcode:{}", vaildcode);
				log.info("招聘外网缓存获取的验证码sessionValidateCode:{}", sessionValidateCode);

                log.info("招聘外网方法operate:{}", operate);

                ResumeBo bo = new ResumeBo(connection);

                //判断验证码,发送激活邮件不需要验证码
                if ((StringUtils.isBlank(vaildcode) || !vaildcode.equalsIgnoreCase(sessionValidateCode)) && !"sendActiveEmail".equals(operate)) {
                    String return_code = "error_vaildata";//登录时 用于前台提示
                    if ("register".equals(operate)) {
                        return_code = "验证码错误！";
                    }
                    map.put("return_code", return_code);
                    out.write(JSON.toString(map));
                    return;
                }

                if ("login".equals(operate)) {
                    String return_code = "error_pw";
                    ServletContext application = this.getServletContext();
                    password = bo.decodeCFCA(encryptedClientRandom, password, serverRandom);
                    ArrayList<String> list = bo.loginValidate(loginName, password, application);
                    return_code = list.get(0);
                    if ("success".equals(return_code)) {
                        password = list.get(2);
                    }
                    login(session, loginName, password, connection, map, bo, return_code, list);
                } else if ("register".equals(operate)) {
                    String return_code = "注册失败！";
                    String acountBeActived = StringUtils.EMPTY;//是否配置了帐号需要激活
                    //注册截止时间
                    String regEndTime = RecruitUtilsBo.getRegisterEndTime();
                    //判断注册是否已截止
                    if (StringUtils.isNotEmpty(regEndTime)) {
                        String format = "yyyy-MM-dd HH:mm";
                        Date endtime = DateUtils.getDate(regEndTime, format);
                        Date now = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat(format);
                        now = DateUtils.getDate(sdf.format(now), format);
                        if (now.after(endtime)) {
                            map.put("return_code", "抱歉，本次招聘注册已截止！");
                            out.write(JSON.toString(map));
                            return;
                        }
                    }
                    //改为cfca控件解密
                    password = bo.decodeCFCA(encryptedClientRandom, password, serverRandom);
                    if (StringUtils.isBlank(password)) {
                        map.put("return_code", "没有配置sm2证书文件！");
                        out.write(JSON.toString(map));
                        return;
                    }
                    String onlyValue = (String) values.get("onlyValue");
                    onlyValue = SafeCode.decode(onlyValue);
                    onlyValue = SafeCode.decrypt("hjsoftjsencryptk", onlyValue);
                    String idTypeValue = (String) values.get("idTypeValue");
                    idTypeValue = SafeCode.decode(idTypeValue);
                    idTypeValue = SafeCode.decrypt("hjsoftjsencryptk", idTypeValue);
                    //证据类型前台加密了，安全过滤必须是解密后进行
                    idTypeValue = PubFunc.hireKeyWord_filter(idTypeValue);

                    String applycode = (String) values.get("applycode");
                    applycode = SafeCode.decode(applycode);
                    applycode = PubFunc.hireKeyWord_filter(applycode);

                    String realname = (String) values.get("realname");
                    realname = SafeCode.decode(realname);
                    realname = PubFunc.hireKeyWord_filter(realname);

                    //是否启用账号密码加密规则
                    RecordVo recordVo = ConstantParamter.getRealConstantVo("EncryPwd");
                    if (recordVo != null) {
                        String isEncryPwd = recordVo.getString("str_value");
                        if ("1".equals(isEncryPwd)) {
                            password = new Des().EncryPwdStr(password);
                        }
                    }
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("email", loginName);
                    params.put("password", password);
                    params.put("only_name", onlyValue);
                    params.put("idTypeValue", idTypeValue);
                    params.put("applycode", applycode);
                    params.put("realname", realname);
                    params.put("localUrl", url.toString());
                    params.put("blackValue", blackValue);
                    params.put("equalFlag", equalFlag);
                    ArrayList<String> list = new ArrayList<String>();
                    return_code = bo.registerCheck(params);
                    if ("success".equals(return_code)) {
                        return_code = bo.register(params, list);//信息入库
                        if ("success".equals(return_code)) {
                            ParameterXMLBo xmlBo = new ParameterXMLBo(connection, "1");
                            HashMap paramsMap = xmlBo.getAttributeValues();
                            if (paramsMap != null && paramsMap.get("acountBeActived") != null && ((String) paramsMap.get("acountBeActived")).length() > 0) {
                                acountBeActived = (String) paramsMap.get("acountBeActived");
                            }
                            if (!"1".equals(acountBeActived)) {//不用激活的话 直接设置session
                                login(session, loginName, password, connection, map, bo, return_code, list);
                            }
                        }
                    }
                    map.put("acountBeActived", acountBeActived);
                    map.put("return_code", return_code);
                } else if ("sendActiveEmail".equals(operate)) {
                    password = bo.decodeCFCA(encryptedClientRandom, password, serverRandom);
                    ArrayList<String> list = bo.loginValidate(loginName, password, this.getServletContext());
                    String return_code = list.get(0);
                    if ("not_active".equals(return_code)) {
                        String a0100 = list.get(1);
                        bo.sendActiveEmail(a0100, loginName, bo.getRealName(a0100), url.toString());
                        map.put("send_flag", "true");
                    }
                }
                //用来登录邮箱，激活账号
                map.put("address", bo.getMailBoxLoginAddress(loginName));
                out.write(JSON.toString(map));
            } else {
                String loginName = request.getParameter("loginName");//登录邮箱
                loginName = SafeCode.decode(loginName);

                String password = request.getParameter("password");// 密码
                password = SafeCode.decode(password);
                password = PubFunc.hireKeyWord_filter_reback(password);
                boolean bool = false;
                ServletContext application = this.getServletContext();
                String hireChannel = employPortalForm.getHireChannel();//获得招聘渠道
                String acountBeActived = employPortalForm.getAcountBeActived();//是否需要邮箱激活=0不需要=1需要
                String failedTime = employPortalForm.getFailedTime();//最大失败次数
                String unlockTime = employPortalForm.getUnlockTime();//解锁时间间隔

                String validateCode = request.getParameter("validatecode");//验证码
                validateCode = SafeCode.decode(validateCode);
                String activeNum = request.getParameter("activeNum");//0登录 1激活
                activeNum = SafeCode.decode(activeNum);
                Calendar calendar = Calendar.getInstance();
                //String validateCode=(String) session.getAttribute("validatecode");
                String a0100 = "";
                String name = "";
                EmployNetPortalBo bo = new EmployNetPortalBo(connection);
                String dbName = bo.getZpkdbName();//获得人才库前缀
                java.sql.PreparedStatement statement = null;
                //判断验证码
                if (StringUtils.isBlank(validateCode) || !validateCode.equalsIgnoreCase(sessionValidateCode)) {
                    info = "验证码错误!";
                    bool = true;
                }
                if ("headHire".equals(hireChannel)) {//如果是猎头招聘不用验证是否符合格式
                } else {
                    /** 验证邮箱格式 **/
                    String checkEmail = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$";
                    Pattern p = Pattern.compile(checkEmail);
                    Matcher m = p.matcher(loginName.trim());
                    if ("".equals(info) && !m.matches()) {
                        info = "邮箱格式不正确!";
                        bool = true;
                    }
                }
                String sql = "";
                String tablePwd = "";
                if ("headHire".equals(hireChannel))
                    sql = "select isused,password  from zp_headhunter_login where lower(userName)=lower(?)";
                else
                    sql = "select userpassword as password from " + dbName + "A01 where lower(username)=lower(?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, loginName);
                resultset = statement.executeQuery();
                boolean haveData = resultset.next();
                if ("headHire".equals(hireChannel) && info == "" && haveData) {
                    String isused = resultset.getString("isused");
                    if (!"1".equalsIgnoreCase(isused)) {
                        info = "用户名或密码错误!";
                        bool = true;
                    }
                } else if (!"headHire".equals(hireChannel) && info == "" && !haveData) {//如果是猎头招聘,不要提示这个信息
                    info = "用户名或密码错误!";
                    bool = true;
                } else if ("headHire".equals(hireChannel) && info == "" && !haveData) {
                    info = "用户名或密码错误!";
                    bool = true;
                } else if (haveData) {
                    tablePwd = resultset.getString("password");
                }

                //口令加密处理
                RecordVo encryVo = ConstantParamter.getRealConstantVo("EncryPwd", connection);
                String encryPwd = "0";
                if (encryVo != null) {
                    encryPwd = encryVo.getString("str_value");
                    if ("1".equals(encryPwd)) {//启用了密码加密登录时要先解密，然后在下面做md5加密做登录验证
                        Des des = new Des();
                        tablePwd = des.DecryPwdStr(tablePwd);
                    }
                }
                //验证MD5加密密码是否一致，如果一直，返回tablePwd，数据库密码，如果不一致返回加密的密码
                password = IsMatchPassword(PubFunc.hireKeyWord_filter_reback(tablePwd), password);
                if ("1".equals(encryPwd)) {//启用了密码加密登录时要先解密，然后在下面做md5加密做登录验证
                    Des des = new Des();
                    password = des.EncryPwdStr(password);
                }

                sql = "select a0100,a0101,state from " + dbName + "A01 where lower(username)=lower(?) and userpassword=?";
                if ("headHire".equals(hireChannel)) {
                    sql = "select userName,password from zp_headhunter_login where lower(userName)=lower(?) and password=? and isused=1";
                }
                statement = connection.prepareStatement(sql);
                statement.setString(1, loginName);
                statement.setString(2, password);
                if (resultset != null)
                    PubFunc.closeResource(resultset);

                resultset = statement.executeQuery();
                haveData = resultset.next();
                String value = (String) application.getAttribute(loginName);//获得用户的登入失败信息
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                int i = 0;
                if (Integer.parseInt(failedTime) > 0) {
                    //超过解锁时间间隔 清除信息
                    if (value != null && !"".equals(value)) {
                        String[] str = value.split("`");
                        Date date1 = sdf.parse(str[1]);//账号锁定时间或最后一次输错的时间
                        Date date2 = sdf.parse(sdf.format(calendar.getTime()));
                        long intervalMinute = date2.getTime() - date1.getTime();
                        int j = (int) (intervalMinute / (60 * 1000));
                        if (j >= Integer.parseInt(unlockTime)) {
                            application.removeAttribute(loginName);
                        }
                    }
                    //密码输入错误
                    if (info == "" && !haveData) {
                        value = (String) application.getAttribute(loginName);//在此重新赋值 是因为超过时间间隔会清空信息
                        if (value == null || value == "") {
                            application.setAttribute(loginName, 1 + "`" + sdf.format(calendar.getTime()));
                            info = "用户名或密码输入错误,您已经输错" + (i + 1) + "次,连续输错" + failedTime + "次后账号将被锁定!";
                        } else {
                            String[] str = value.split("`");
                            if (Integer.parseInt(str[0]) < Integer.parseInt(failedTime)) {
                                i = Integer.parseInt(str[0]) + 1;
                                str[1] = sdf.format(calendar.getTime());
                                application.setAttribute(loginName, i + "`" + str[1]);
                                if (i == Integer.parseInt(failedTime)) {
                                    Date date1 = sdf.parse(str[1]);//账号锁定时间或最后一次输错的时间
                                    Date date2 = sdf.parse(sdf.format(calendar.getTime()));
                                    long intervalMinute = date2.getTime() - date1.getTime();
                                    int j = (int) (intervalMinute / (60 * 1000));//现在距离锁定过了多少分钟
                                    int k = Integer.parseInt(unlockTime) - j;//还剩多少分钟解锁
                                    if (k > 0) {
                                        info = "用户名或密码已连续输错" + failedTime + "次,账号已被锁定,请" + k + "分钟后再试!";
                                    }

                                } else {
                                    info = "用户名或密码输入错误,您已经输错" + i + "次，连续输错" + failedTime + "次后账号将被锁定!";
                                }

                            } else {

                                Date date1 = sdf.parse(str[1]);//账号锁定时间或最后一次输错的时间
                                Date date2 = sdf.parse(sdf.format(calendar.getTime()));
                                long intervalMinute = date2.getTime() - date1.getTime();
                                int j = (int) (intervalMinute / (60 * 1000));//现在距离锁定过了多少分钟
                                int k = Integer.parseInt(unlockTime) - j;//还剩多少分钟解锁
                                if (k > 0) {
                                    info = "用户名或密码已连续输错" + failedTime + "次,账号已被锁定,请" + k + "分钟后再试!";
                                }

                            }

                        }
                        bool = true;
                    }
                    //密码输入正确
                    else if (info == "" && haveData) {
                        if ("headHire".equals(hireChannel)) {
                            a0100 = "headHire";//resultset.getString("a0100");猎头招聘没有a0100因此随意写了一个
                            name = resultset.getString("userName");
                        } else {
                            a0100 = resultset.getString("a0100");
                            name = resultset.getString("a0101");
                        }
                        value = (String) application.getAttribute(loginName);
                        if (value == null || value == "") {

                        } else {
                            String[] str = value.split("`");
                            if (Integer.parseInt(str[0]) < Integer.parseInt(failedTime)) {
                                application.removeAttribute(loginName);
                            } else {
                                Date date1 = sdf.parse(str[1]);//账号锁定时间或最后一次输错的时间
                                Date date2 = sdf.parse(sdf.format(calendar.getTime()));
                                long intervalMinute = date2.getTime() - date1.getTime();
                                int j = (int) (intervalMinute / (60 * 1000));//现在距离锁定过了多少分钟
                                int k = Integer.parseInt(unlockTime) - j;//还剩多少分钟解锁
                                if (k > 0) {
                                    info = "用户名或密码已连续输错" + failedTime + "次,账号已被锁定,请" + k + "分钟后再试!";
                                }
                                bool = true;
                            }
                        }
                    }
                } else {
                    //密码输入错误
                    if (info == "" && !haveData) {
                        info = "用户名或密码输入错误";
                        bool = true;
                    }
                }

                if ("0".equals(activeNum) && !"headHire".equals(hireChannel)) {//登录操作 判断是否激活,猎头招聘不激活
                    if (info == "" && "1".equals(acountBeActived)) {
                        String state = resultset.getString("state");
                        if (state == null || "1".equals(state)) {

                        } else {
                            info = "账号还未被激活，请到注册邮箱中激活帐号!";
                            bool = true;
                        }
                    }
                }
                if ("1".equals(activeNum) && !"headHire".equals(hireChannel)) {//激活操作 判断是否激活,猎头招聘不激活
                    if (info == "" && "1".equals(acountBeActived)) {
                        String state = resultset.getString("state");
                        if (state != null && "1".equals(state)) {
                            info = "账号已被激活，无需重复激活!";
                            bool = true;
                        } else {

                        }
                    }
                }

                employPortalForm.setA0100(a0100);
                String message = bo.getApplyMessage(a0100);
                employPortalForm.setApplyMessage(message);
                employPortalForm.setLoginName(loginName);
                employPortalForm.setPassword(password);
                employPortalForm.setValidateInfo(info);
                String txtEmail = loginName;
                String a0100Code = a0100;
/*                loginName = PubFunc.encryption(loginName);
                password = PubFunc.encryption(password);
                a0100 = PubFunc.encryption(a0100);*/
                //清除session 和cookie
                Cookie[] ck = request.getCookies();
                if (ck != null) {
                    for (int k = 0; k < ck.length; k++) {
                        ck[k].setMaxAge(0);
                        response.addCookie(ck[k]);
                    }

                }
                if ("weblogic".equalsIgnoreCase(SystemConfig.getPropertyValue("webserver").trim())) {
                    session.invalidate();
                    session = request.getSession();
                    Enumeration e = session.getAttributeNames();
                    while (e.hasMoreElements()) {
                        name = (String) e.nextElement();
                        session.removeAttribute(name);
                    }
                } else {
                    session.invalidate();
                    session = request.getSession();
                }
                if (bool) {
                    if ("0".equals(hireChannel))//为了在首页登陆的时候登陆成功取了其他页面
                        response.sendRedirect("/hire/hireNetPortal/zp_homepage.do?b_hquery=link&hireChannel=0&validate=true");
                    else
                        response.sendRedirect("/hire/hireNetPortal/search_zp_position.do?b_query=link&operate=init&hireChannel=" + hireChannel + "&validate=true");
                } else {
                    //单独处理猎头招聘的情况
                    if ("headHire".equals(hireChannel)) {
                        response.sendRedirect("/hire/hireNetPortal/search_zp_position.do?b_headLogin=login&hireChannel=" + hireChannel + "&validate=true");
                    } else {
                        if ("0".equals(activeNum)) {
                            if ("0".equals(hireChannel))
                                response.sendRedirect("/hire/hireNetPortal/search_zp_position.do?b_homeLogin=login&hireChannel=" + hireChannel + "&validate=true");
                            else
                                response.sendRedirect("/hire/hireNetPortal/search_zp_position.do?b_login=login&hireChannel=" + hireChannel + "&validate=true");
                        } else if ("1".equals(activeNum)) {//激活操作
                            String why = SystemConfig.getPropertyValue("masterName");
                            if (why == null || "".equals(why))
                                why = "";
                            String str = why;
                            EMailBo emb = null;
                            try {
                                emb = new EMailBo(connection, true, "");
                            } catch (Exception e) {
                                e.printStackTrace();
                                info = "系统邮件服务器配置或网络连接不正确,请联系系统管理员!";
                                response.sendRedirect("/hire/hireNetPortal/search_zp_position.do?b_query=link&operate=init&hireChannel=" + hireChannel + "&validate=true");
                                throw GeneralExceptionHandler.Handle(new Exception("系统邮件服务器配置或网络连接不正确,请联系系统管理员!"));
                            }
                            AutoSendEMailBo autoSendEMailBo = new AutoSendEMailBo(connection);
                            String from_addr = autoSendEMailBo.getFromAddr();
                            String title = str + "招聘网帐号激活邮件";
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            StringBuffer context = new StringBuffer();
                            calendar = Calendar.getInstance(); //发送激活邮件的时间
                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String activeDate = format1.format(calendar.getTime());
                            activeDate = PubFunc.encryption(activeDate);
                            if (!"".equals(url)) {
                                context.append(name + "&nbsp;&nbsp;您好:\r\n");
                                context.append("您在" + str + "招聘网的帐号已经注册成功，请点击下面链接激活该帐号。<br><br>");
                                context.append("<a href=\"" + url + "/hire/hireNetPortal/search_zp_position.do?b_activecount=active&activeid=" + PubFunc.convertTo64Base(a0100Code) + "&activeDate=" + activeDate + "\"");
                                context.append(" target=\"_blank\">激活帐号</a><br><br>");
                                context.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + format.format(new Date()));
                                try {
                                    emb.sendEmail(title, context.toString(), "", from_addr, txtEmail);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    info = "系统邮件服务器配置或网络连接不正确,请联系系统管理员!";
                                    response.sendRedirect("/hire/hireNetPortal/search_zp_position.do?b_query=link&operate=init&hireChannel=" + hireChannel + "&validate=true");
                                    throw GeneralExceptionHandler.Handle(new Exception("系统邮件服务器配置或网络连接不正确,请联系系统管理员!"));
                                }
                            }
                            info = "已将激活帐号邮件发送到注册邮箱，请到注册邮箱中激活帐号!";
                            response.sendRedirect("/hire/hireNetPortal/search_zp_position.do?b_query=link&operate=init&hireChannel=" + hireChannel + "&validate=true");
                        }
                    }
                }

            }
        } catch (Exception ex) {
            log.error("贵州银行-->招聘外网登录失败！异常信息ErrorMessage:{}", ex);
            try {
                throw GeneralExceptionHandler.Handle(ex);
            } catch (GeneralException e) {
                e.printStackTrace();
            }
        } finally {
            PubFunc.closeResource(connection);
            PubFunc.closeResource(resultset);
            PubFunc.closeResource(out);
            if (employPortalForm != null) {
                employPortalForm.setValidateInfo(info);
                session.setAttribute("employPortalForm", employPortalForm);
            }
        }
    }

    /**
     * 保存登录信息到session
     *
     * @param session
     * @param loginName
     * @param password
     * @param connection
     * @param map
     * @param bo
     * @param return_code
     * @param list
     * @throws Exception
     */
    private void login(HttpSession session, String loginName, String password, Connection connection,
                       HashMap<String, String> map, ResumeBo bo, String return_code, ArrayList<String> list) throws Exception {
        if ("success".equals(return_code) && session != null) {
            String a0100 = list.get(1);
            String applyCode = bo.getApplyCode(a0100);
            UserView userview = new UserView(loginName, password, connection);
            userview.setA0100(a0100);
            userview.setUserId(a0100);
            userview.setUserEmail(loginName);
            userview.setDbname(bo.getDbName());
//			userview.getHm().put("isEmployee","1");
            userview.getHm().put("isHeadhunter", "0");//是否是猎头登录用户  0：不是     1：是
            userview.getHm().put("applyCode", applyCode);//登陆时设置应聘渠道
            //userview.canLogin(true); 去掉这个方法，否则影响招聘外网的人员信息
            session.setAttribute("islogon", true);
            session.setAttribute(WebConstant.userView, userview);
            deleteOnlineUser(session, userview);
            /** 是否可以打印准考证、查看成绩 **/
            ParameterXMLBo xmlBo = new ParameterXMLBo(connection, "1");
            HashMap xmlMap = xmlBo.getAttributeValues();
            String isAttach = "0";
            if (xmlMap.get("attach") != null && ((String) xmlMap.get("attach")).length() > 0) {
                isAttach = (String) xmlMap.get("attach");
            }
            EmployNetPortalBo employNetPortalBo = new EmployNetPortalBo(connection, isAttach);
            String admissionCard = "#";
            if (xmlMap.get("admissionCard") != null && !"".equals((String) xmlMap.get("admissionCard"))) {
                admissionCard = (String) xmlMap.get("admissionCard");
            }
            boolean canPrintExamno = employNetPortalBo.canPrintExamNo(a0100, admissionCard);
            boolean canQueryScore = employNetPortalBo.canQueryScore(bo.getDbName(), a0100); //能否查看成绩
            if (canQueryScore)
                map.put("canQueryScore", "1");
            else
                map.put("canQueryScore", "0");
            String scoreTableId = employNetPortalBo.getScoreTabId();
            String resumeTemplateId = employNetPortalBo.getResumeTemplateId(a0100);
            map.put("scoreTableId", scoreTableId); //成绩登记表ID
            map.put("resumeTemplateId", resumeTemplateId); //简历登记表ID
            map.put("admissionCard", admissionCard); //准考证登记表ID
            map.put("canPrintExamno", String.valueOf(canPrintExamno)); //能否打印准考证
            map.put("nbase", PubFunc.encrypt(bo.getDbName()));
            map.put("a0100", PubFunc.encrypt(a0100));
            map.put("return_code", return_code);
        } else {
            map.put("return_code", return_code);
        }
    }

    /**
     * 每个用户只允许登录一次，再次登录删除上一个登录session
     *
     * @param session
     * @param userView
     */
    private void deleteOnlineUser(HttpSession session, UserView userView) {
        FastHashMap onlineUserMap = (FastHashMap) session.getServletContext().getAttribute("userNames");
        if (onlineUserMap == null) {
            onlineUserMap = new FastHashMap();
            session.getServletContext().setAttribute("userNames", onlineUserMap);
        }
        Iterator keys = onlineUserMap.keySet().iterator();
        while (keys.hasNext()) {
            String sessionId = (String) keys.next();
            OnlineUserView user = (OnlineUserView) onlineUserMap.get(sessionId);
            if (userView.getUserName().equalsIgnoreCase(user.getUserId())) {
                // 通过得到session注销用户
                onlineUserMap.remove(sessionId);
                if (sessionId != null && !sessionId.equalsIgnoreCase(session.getId())) {
                    user.getSession().invalidate();
                }
            }
        }
        ResumeOnlineListener onLineListener = (ResumeOnlineListener)session.getAttribute("online_listener");
        if (onLineListener == null) {
            onLineListener = new ResumeOnlineListener();
            session.setAttribute("online_listener", onLineListener);
        }
        OnlineUserView onLineUser = new OnlineUserView();
        onLineUser.setDept(userView.getUserDeptId());
        onLineUser.setOrgname(userView.getUserOrgId());
        onLineUser.setPos(userView.getUserPosId());
        onLineUser.setUsername(userView.getUserFullName());
        onLineUser.setIp_addr(userView.getRemote_ip());
        onLineUser.setLogin_date(DateStyle.dateformat(new Date(), "yyyy-MM-dd HH:mm:ss"));
        onLineUser.setUserId(userView.getUserName());
        onLineUser.setSession(session);
        onLineUser.setThreerole(userView.getThreeUserRole());
        onLineUser.setLoginSeqno(CreateSequence.getUUID());
        onlineUserMap.put(session.getId(), onLineUser);
    }

    /**
     * md5加密
     *
     * @param str
     * @return
     */
    private String getMD5(String str) {

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }

    /**
     * 查找出对应的密码是否匹配，比较和前台传过来的加密串是否一致
     * 如果是匹配的返回tablePwd，不匹配返回originPwd，原逻辑不变
     *
     * @param tablePwd：  表中根据username查出来的密码
     * @param originPwd： 页面传过来的密码
     * @return
     */
    private String IsMatchPassword(String tablePwd, String originPwd) {
        String newPwd = "";
        try {
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);

            cal.add(Calendar.HOUR_OF_DAY, -1);//1小时前，可能在11:59分这种情况下，到后台变成了12:00，所以得加上判断一小时前
            int pre_hour = cal.get(Calendar.HOUR_OF_DAY);

            String _passWordEncript = "MD5`" + getMD5(tablePwd + hour);
            String _passWordEncript2 = "MD5`" + getMD5(tablePwd + pre_hour);
            if (_passWordEncript.equals(originPwd) || _passWordEncript2.equals(originPwd))
                newPwd = tablePwd;
            else
                newPwd = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newPwd;
    }

}
