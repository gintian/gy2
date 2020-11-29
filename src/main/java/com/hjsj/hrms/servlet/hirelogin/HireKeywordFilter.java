package com.hjsj.hrms.servlet.hirelogin;

import com.hjsj.hrms.servlet.ParameterRequestWrapper;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HireKeywordFilter implements Filter {

    static Logger log = LoggerFactory.getLogger(HireKeywordFilter.class);
    final static String html = "<table height=100% width=100%><tr><td align=center valign=middle style='color:red;font-size:20px;'>您的请求来源不合法！</td></tr><table>";

    public void destroy() {

    }

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        if (request.getContentType() != null) //20170526 dengcan Apache Struts2 远程代码执行漏洞（S2-045）
        {
            String contentType = request.getContentType().toLowerCase(Locale.ENGLISH);
            if (contentType != null && contentType.contains("multipart/form-data") && !contentType.startsWith("multipart/form-data")) {
                response.setCharacterEncoding("GBK");
                response.getWriter().write("拒绝执行远程代码攻击命令!");
                return;
            }
        }

        HttpServletRequest _request = (HttpServletRequest) request;
        HttpServletResponse _response = (HttpServletResponse) response;
        String url = _request.getRequestURI();

        // zxj 20200403 是否允许访问老招聘外网，默认不允许
        String canUseOldZp = SystemConfig.getPropertyValue("use_old_zp");
        if (!"true".equalsIgnoreCase(canUseOldZp) && url.indexOf("/hire/hireNetPortal") != -1 && url.indexOf("search_notice_card.do") == -1) {
            _response.sendRedirect("/zp.html");
            return;
        }
            
			/*
				ckfinder 角色控制。防止直接访问 /ckfinder/ckfinder.html进行上传文件。
				系统中没有单独使用ckfinder的地方，都是和ckeditor联合使用。
				此处必须加载ckeditor.js后，才给ckfinder使用权限，可以一定程序上控制越权访问 guodd 2019-10-24
			 */
        if (url.indexOf("/ckeditor/ckeditor.js") != -1) {
            _request.getSession().setAttribute("CKFinder_UserRole", "admin");
        }
        /*判断是否访问频率超限，防止暴力攻击 guodd 2018-11-26*/
        if (isOverFrequency(_request, url)) {
            _response.setCharacterEncoding("UTF-8");
            _response.getWriter().write("您访问的频率超出限制，请稍后访问。");
            return;
        }
        boolean isAndroidClient = false;
        if ("/servlet/DisplayOleContent".equalsIgnoreCase(url))
            isAndroidClient = isAndroid(_request);//加载图片 安卓端无法获取到userview 单独做处理 不校验userview changxy 20170329
        if ("/ajax/ajaxService".equalsIgnoreCase(_request.getRequestURI()))
            _request.setCharacterEncoding("UTF-8");
        else
            _request.setCharacterEncoding("UTF-8");

        //目标网站存在长密码拒绝服务攻击
        if (_request.getParameter("username") != null && _request.getParameter("username").trim().length() > 50) {
            // zhangh 2019-12-13 前台用户名传入时进行了编码，当用户名比较长时，编码超过50位，需要解码后再判断
            String username = SafeCode.decode(_request.getParameter("username").trim());
            if (username != null && username.length() > 50) {
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("拒绝用户名过长!");
                return;
            }
        }
        if (_request.getParameter("password") != null && _request.getParameter("password").trim().length() > 100) {
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("拒绝密码过长!");
            return;
        }

        //检查请求来源是否为头攻击
        if (checkRequestHost(_request, _response))
            return;


        //检查请求来源是否合法，防止CSRF跨站攻击 guodd 2016-11-26
        if (!checkRequestSource(url, _request, _response))
            return;

        // iframe跨域参数
        String xFrameOption = SystemConfig.getPropertyValue("x-frame-options");
        // 未配置信息，默认同域
        if (StringUtils.isBlank(xFrameOption)) {
            _response.addHeader("x-frame-options", "SAMEORIGIN");
        } else {
            // 正确配置了参数值，则按正确值设置，否则，不加设置，保持不限制的状态
            if ("SAMEORIGIN".equalsIgnoreCase(xFrameOption) || "DENY".equalsIgnoreCase(xFrameOption)
                    || xFrameOption.startsWith("ALLOW-FROM"))
                _response.addHeader("x-frame-options", xFrameOption);
        }

        /**
         * 执行sql记录日志功能，需要记录用户名 guodd 2016-09-08
         * 获取userview，将账号和用户作为当前线程的name，log4j 记录日志时会记录线程name
         */
        HttpSession session = _request.getSession();
        UserView userView = (UserView) session.getAttribute(WebConstant.userView);
        if (userView != null)
            Thread.currentThread().setName(userView.getUserName() + ":" + userView.getUserFullName());
        //培训课件未登录的情况下不允许访问 chenxg
        if (url.startsWith("/coureware") && userView == null)
            return;
			/*zxj 20141220 此方式在tomcat下没问题，websphere下不兼容，暂注释掉，待寻找解决方案。
			//zxj 20141209 设置jessionid的cookie为httponly,由于会影响到applet,所以暂时仅针对外网招聘
			if (null != url && url.startsWith("/hire/hireNetPortal")) {
    			String sessionid = (String)_request.getSession().getId();
    			if (null != sessionid)
    			    _response.setHeader("SET-COOKIE", "JSESSIONID=" + sessionid + ";Path=/;HttpOnly");
			}*/

        /**
         * 请求地址开始为多斜杠时，能正常访问，但过滤不起作用。正常系统内链接地址不会出现多斜杠，此处遇到多斜杠直接拒绝请求
         * guodd 2017-12-14
         */
        if (url.indexOf("//") != -1)
            return;

        // 登录限制页面，未登录时不允许访问
        if (limitedPage(url, userView)) {
            //System.out.println("未登录访问拦截：" + url);
            return;
        }

        // 不需要进行参数字符安全过滤的请求链接，直接进入下一环节
        if (!needCharSecurityFilter(url)) {
            chain.doFilter(request, response);
            return;
        }

        // 各类上传下载链接登录权限控制
        if (url.startsWith("/ckfinder/core/connector/java/connector.java")//ckfinder 文件上传管理组件 guodd 2016-09-08
                //zxj 20161129 上传文件servlet
                || "/train/media/upload".equalsIgnoreCase(url)
                //培训课件下载 chenxg 2017-12-13
                || "/DownLoadCourseware".equalsIgnoreCase(url)
                //工作计划上传
                || "/servlet/workplan/UpLoadFileServlet".equalsIgnoreCase(url)
                //CKEditor上传路径
                || "/ckeditor/uploader".equalsIgnoreCase(url)) {
            //如果登陆了，并且不是外网招聘登陆的，可以执行上传
            if (userView != null && !userView.getHm().containsKey("isEmployee"))
                chain.doFilter(request, response);
            return;
        } else if ("/servlet/DisplayOleContent".equalsIgnoreCase(url)) {
            if (userView != null || isAndroidClient)
                chain.doFilter(request, response);
            return;
        } else if (url.startsWith("/components/fileupload/upload")) {
            boolean sessionFlag = false;
            //火狐和safari 取不到session 上传控件初始化时 走rpc 判断当前操作人是否为空
            String safariORFoxType = _request.getParameter("safariORFoxType");
            if (safariORFoxType != null && safariORFoxType.length() > 8 && _request.getParameter("datems") != null) {
                Long times = Long.parseLong(_request.getParameter("datems"));
                Long nowTime = new Date().getTime();
                if ((nowTime - times) <= 120000 && "true".equals(PubFunc.decrypt(safariORFoxType))) {//判断火狐或者safari 是否点击文件上传是否超时 2分钟之内不超时
                    sessionFlag = true;
                }
            }

            if ((userView != null && !userView.getHm().containsKey("isEmployee")) || sessionFlag)
                chain.doFilter(request, response);
            return;
        } else if (url.startsWith("/fckeditor/editor")) {
            if (userView == null || !userView.getHm().containsKey("fckeditorAccessTime"))
                return;

            if (url.startsWith("/fckeditor/editor/fckeditor.html")) {
                Long time = (Long) userView.getHm().get("fckeditorAccessTime");
                if (new Date().getTime() - time > 10000) {
                    userView.getHm().remove("fckeditorAccessTime");
                    return;
                }
            }

        } else if (url.startsWith("/w_selfservice/oauthservlet")) {//
            chain.doFilter(request, response);
            return;
        }

        // 进行安全字符过滤
        ServletRequest filterRequest = charSecurityFilter(request);

        // 本过滤器任务完成，转下一过滤链
        chain.doFilter(filterRequest, response);
    }

    /**
     * 对请求链接参数进行字符安全过滤，以防止跨站脚本攻击、SQL注入
     *
     * @param request
     * @return 过滤后的请求
     */
    private ServletRequest charSecurityFilter(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        HashMap map = new HashMap(httpServletRequest.getParameterMap());

        // 无参数链接，不用过滤
        if (map.size() == 0 && !ServletFileUpload.isMultipartContent(httpServletRequest)) {
            return request;
        }

        // 安全过滤
        String param = "";
        String paramValue = "";

        //通过继承HttpServletRequestWrapper类转化
        ParameterRequestWrapper wrapRequest = new ParameterRequestWrapper(httpServletRequest, map);
        try {
            if ("/ajax/ajaxService".equalsIgnoreCase(httpServletRequest.getRequestURI()))
                wrapRequest.setCharacterEncoding("UTF-8");
            else
                wrapRequest.setCharacterEncoding("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //解决Apusic中间件中，Form上传附件时，一些数据格式字符被误转义。例如：公告维护的内容格式
        boolean isMultiForm = ServletFileUpload.isMultipartContent(httpServletRequest);
        if (isMultiForm && "apusic".equalsIgnoreCase(SystemConfig.getPropertyValue("webserver"))) {
            return wrapRequest;
        }

        java.util.Enumeration params = wrapRequest.getParameterNames();
        Map requestParams = wrapRequest.getParameterMap();
        while (params.hasMoreElements()) {
            param = (String) params.nextElement();

            if ("__xml".equalsIgnoreCase(param)
                    && ("extTrans".equals(httpServletRequest.getParameter("__type"))
                    || "byserviceclient".equals(httpServletRequest.getParameter("__type")))) {

                String[] values = (String[]) requestParams.get(param);
                for (int i = 0; i < values.length; i++) {
                    if (values[i] == null)
                        continue;

                    //sm4解密ajax参数
                    values[i] = SafeCode.decrypt("hjsoftjsencryptk", values[i]);
                    //AesEncryptUtil.aesDecrypt(values[i]);

                    //解决rpc跨站脚本攻击和sql注入问题
                    if ("__xml".equalsIgnoreCase(param)) {
                        values[i] = PubFunc.stripScriptXss(values[i]);
                        values[i] = PubFunc.replaceSQLkey(values[i]);
                    }
                }
                requestParams.put(param, values);
                continue;
            }

            if ("__xml".equalsIgnoreCase(param) || "__type".equalsIgnoreCase(param)) {
                continue;
            }
            if ("encryptParam".equalsIgnoreCase(param) || "etoken".equalsIgnoreCase(param)) {
                continue;
            }

            if (requestParams.get(param) != null) {
                String[] values = (String[]) requestParams.get(param);// 获得每个参数的value
                for (int i = 0; i < values.length; i++) {
                    if (values[i] == null)
                        continue;

                    paramValue = values[i];

                    //20140901  dengcan
                    if (paramValue.indexOf(".do?") != -1 && "center_url".equalsIgnoreCase(param))
                        continue;

                    boolean flag = false;
                    if (paramValue.indexOf("<@>") != -1) {
                        paramValue = paramValue.replaceAll("<@>", "~~");
                        flag = true;
                    }

                    //应用程序应该屏蔽任何肯定要出现在HTTP响应头中、含有特殊字符的输入，特别是CR（回车符，也可由%0d或\r提供）和LF（换行符，也可由%0a或\n提供）字符，将它们当作非法字符。 
                    /**
                     if (paramValue.indexOf("CR")!=-1){
                     paramValue = paramValue.replaceAll("CR", "");
                     }
                     */
                    if (paramValue.indexOf("%0d") != -1) {
                        paramValue = paramValue.replaceAll("%0d", "");
                    }

                    //zxj 20141018 注释原因：前台传入的带换行(\r\n)的文本（如textarea中）过滤掉是不对的 
                    //if (paramValue.indexOf("\r")!=-1){  
                    //  paramValue = paramValue.replaceAll("\r", "");  
                    //}  

                    // 代码类为LF时会出问题，不得已注释掉
                    //if (paramValue.indexOf("LF")!=-1){  
                    //  paramValue = paramValue.replaceAll("LF", "");  
                    //} 

                    if (paramValue.indexOf("%0a") != -1) {
                        paramValue = paramValue.replaceAll("%0a", "");
                    }

                    //zxj 20141018 注释原因：前台传入的带换行的文本（如textarea中）过滤掉是不对的 
                    //if (paramValue.indexOf("\n")!=-1){  
                    //   paramValue = paramValue.replaceAll("\n", "");  
                    //} 

                    if (paramValue.indexOf("Chr(34)") != -1) {
                        paramValue = paramValue.replaceAll("Chr(34)", "＂");
                    }

                    paramValue = PubFunc.hireKeyWord_filter(paramValue);
                    if (httpServletRequest.getRequestURI().indexOf("/fckeditor/editor") != -1) //20140922 dengcan 特殊处理网页在线编辑器
                        paramValue = paramValue.replaceAll("／", "/");
                    if (flag)
                        paramValue = paramValue.replaceAll("~~", "<@>");

                    values[i] = paramValue;
                }
                // 把转义后的参数重新放回request中

                requestParams.put(param, values);
            }
        }

        return wrapRequest;
    }

    /**
     * 是否为限制访问的URL
     * v1.0: 白名单、黑名单混合形式限制部分jsp,html的访问
     *
     * @param url
     * @return true:受限，false: 不受限
     */
    private boolean limitedPage(String url, UserView userView) {
        boolean limited = false;

        // 异常链接
        if (StringUtils.isBlank(url))
            return true;

        // 不是页面文件不做处理
        if (!url.toLowerCase().contains(".html") && !url.toLowerCase().contains(".jsp") && !url.toLowerCase().contains(".htm"))
            return false;

        // 是否已登录
        boolean logged = null != userView;

        // 受限的链接（没登录不能访问）
        if (!logged) {
            // 绝大部分html页面都可未登录访问
            // 富文本编辑组件有上传功能，必须限制
            if (url.startsWith("/ckfinder/ckfinder.html"))
                return true;

            // 绝大多少jsp页面都必须登录后访问，只排除不需要登录的即可
            if (url.contains(".jsp")) {
                limited = true;

                // 招聘外网页面不限制
                if (url.startsWith("/hire/hireNetPortal") || url.startsWith("/hire/employNetPortal")) {
                    return false;
                }

                // 登录等相关页面不需要限制
                if (url.startsWith("/templates/index")) {
                    return false;
                }

                // 二次开发单点登录等功能目录
                if (url.startsWith("/templates/attestation/")) {
                    return false;
                }

                // 二维码业务办理扫码页面不需要限制
                if (url.startsWith("/module/system/qrcard/mobilewrite/qrcardmain.jsp")) {
                    return false;
                }

                // 自助终端登录页面
                if (url.startsWith("/module/serviceclient/index.jsp")) {
                    return false;
                }
                if (url.startsWith("/module/serviceclient/serviceclientmain.jsp")) {
                    return false;
                }

                // 问卷调查
                if (url.startsWith("/module/system/questionnaire/template/AnswerQn.jsp")) {
                    return false;
                }
                if (url.startsWith("/module/system/questionnaire/mobile/index.jsp")) {
                    return false;
                }
                //人事异动外部链接进入表单
                if (url.startsWith("/module/template/templatemain/templatemain.jsp"))
                    return false;
                // 一个用于单点登录的特殊链接
                if (url.startsWith("/module.utils.jsp.do"))
                    return false;

                //------- w_selfservice 工程 路径  start
                if (url.startsWith("/w_selfservice/menu.html"))
                    return false;
                if (url.startsWith("/w_selfservice/version.html"))
                    return false;
                if (url.startsWith("/w_selfservice/ddtalkAuth.jsp"))
                    return false;
                if (url.startsWith("/w_selfservice/error.jsp"))
                    return false;
                if (url.startsWith("/w_selfservice/index.jsp"))
                    return false;
                if (url.startsWith("/w_selfservice/module/selfservice/index.jsp"))
                    return false;
                if (url.startsWith("/w_selfservice/module/selfservice/home.jsp"))
                    return false;
                if (url.startsWith("/w_selfservice/module/zxdeclare/ZXDeclareMain.jsp"))
                    return false;
                if (url.startsWith("/w_selfservice/oauthservlet"))
                    return false;
                if (url.startsWith("/w_selfservice/userinfo"))
                    return false;
                if (url.startsWith("/w_selfservice/module/talentmarkets/TalentMarketsMain.jsp"))
                    return false;
                if (url.startsWith("/w_selfservice/module/selfservice/fileinfo.jsp"))
                    return false;
                if (url.startsWith("/elearning/mylession/mobile/list.jsp"))//放开培训小助手地址
                    return false;
                //------- w_selfservice 工程 路径方案  end

                // 根目录下的页面不限制
                int matchCount = 0;
                Pattern pattern = Pattern.compile("/");
                Matcher matcher = pattern.matcher(url);
                while (matcher.find()) {
                    matchCount++;
                }
                // "/"只匹配到一个，认为是根目录
                if (matchCount <= 1)
                    return false;

                // system.properties中增加unlimite_jsp参数，此参数中的路径将不限制访问
                // 格式形如：/test/test.jsp,/test/test2.jsp
                String unlimitedJsp = SystemConfig.getPropertyValue("unlimited_jsp");
                if (StringUtils.isNotBlank(unlimitedJsp)) {
                    if (("," + unlimitedJsp + ",").contains("," + url + ","))
                        return false;
                }
            }
        }

        return limited;
    }

    /**
     * 是否是需要进行参数安全过滤的链接
     *
     * @param url
     * @return true: 需要过滤 false: 不需要过滤
     */
    private boolean needCharSecurityFilter(String url) {
        boolean needFilter = true;

        //单点登陆页面不过滤 guodd 2017-03-20
        if (url.startsWith("/templates/attestation/")) {
            return false;
        }

        /**
         * wangzhongjun  2014-09-16
         * 过滤器拦截了单点登录和 WebService 的连接url，需要放开
         */
        if (url.startsWith("/services/")) {
            return false;
        }

        //静态文件无需经过过滤器  dengcan 20141021
        if (url.startsWith("/ext/resources/") || url.startsWith("/images/") || url.startsWith("/js/")) {
            return false;
        }

        String ext = url.substring(url.lastIndexOf(".") + 1).toLowerCase();
        if ("gif".equals(ext) || "bmp".equals(ext) || "jpg".equals(ext) || "js".equals(ext) || "htc".equals(ext) || "ico".equals(ext) || "css".equals(ext) || "cab".equals(ext)) {
            return false;
        }

        // 访问登录页面不需要过滤
        if (isLogin(url)) {
            return false;
        }

        //人事异动-电子签章 wangard 20160323
        if (url.startsWith("/iSignatureHTML/")) {
            return false;
        }

        return needFilter;
    }

    /***
     * 加载图片 安卓端无法获取到userview 单独做处理 不校验userview
     *
     * */
    private boolean isAndroid(HttpServletRequest _request) throws IOException, ServletException {

        String ismobile = _request.getParameter("mobile");
        if ("1".equals(ismobile) || "zp_noticetemplate_flag".equals(ismobile))//1 移动端不判断userview zp_noticetemplate_flag 判断招聘公告登记表图片查看时标记，1为displayOleCOntent 移动端文件不加密设置
            return true;
        return false;
    }

    /**
     * 判断是否来自登陆链接
     *
     * @param url
     * @return
     */
    private boolean isLogin(String url) {
        boolean flag = false;
        if (url.startsWith("/templates/index/")) {
            if (url.startsWith("/templates/index/UserLogon.do"))
                flag = true;
            else if (url.startsWith("/templates/index/employLogon.do"))
                flag = true;
            else if (url.startsWith("/templates/index/hrlogon.do"))
                flag = true;
            else if (url.startsWith("/templates/index/hrlogon4.do"))
                flag = true;
            else if (url.startsWith("/templates/index/emlogon4.do"))
                flag = true;
            else if (url.startsWith("/templates/index/perlogon.do"))
                flag = true;
            else if (url.startsWith("/templates/index/bilogon.do"))
                flag = true;
            else if (url.startsWith("/templates/index/ilearning.do"))
                flag = true;
            else if (url.startsWith("/templates/index/epmlogon.do"))
                flag = true;
            else if (url.startsWith("/templates/index/hcmlogon.do"))
                flag = true;
        } else if (url.startsWith("/phone-app/index.do"))
            flag = true;
        return flag;
    }

    /**
     * 解决头攻击
     *
     * @param req
     * @return true:有头攻击请求拒绝访问    false:无
     */
    private boolean checkRequestHost(HttpServletRequest req, HttpServletResponse res) {
        AntPathMatcher matcher = new AntPathMatcher();

        try {
            //如果没有配置验证的主机ip和端口 或 域名和端口 ，跳出检查      例： www.hjsoft.com.cn,127.0.0.1:8081
            String whiteHostList = SystemConfig.getPropertyValue("white_host_list");
            if (StringUtils.isBlank(whiteHostList)) {
                log.error("没有配置白名单，请配置~");
                throw new RuntimeException("没有配置白名单，请配置~");
            }

            //主机ip和端口  或 域名和端口
            String myhosts = req.getHeader("host");
            log.debug("tip===> request host is {}", myhosts);
            if (StringUtils.isBlank(myhosts)) {
                log.error("获取请求地址失败");
                return true;
            }

            String[] split = myhosts.split(":");
            if (split == null || split.length == 0) {
                log.error("获取请求地址失败,host:{}", myhosts);
                return true;
            }

            boolean flag = true;
            String host = split[0];//# 获取ip
            if (StringUtils.isNotBlank(whiteHostList)) {
                String[] hosts = whiteHostList.split(",");
                for (String h : hosts) {
                    if (matcher.match(h, host)) {
                        flag = false;
                        break;
                    }
                }
            }

            if (!flag) {
                //# 是授权的（是白名单）
                log.debug("white host is {}", myhosts);
            } else {
                log.error("host={},it is not in white list!, please look white_host_list config ~", host);
                res.setCharacterEncoding("UTF-8");
                res.setHeader("content-type", "text/html; charset=utf-8");
                res.setContentType("text/html;charset=utf-8");

                res.getWriter().write(html);
                return true;
            }

        } catch (Exception e) {
            log.error("处理host攻击时发生错误，desc: {}", e);
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkRequestSource(String url, HttpServletRequest req, HttpServletResponse res) {

        boolean result = true;

        try {
            //如果没有配置服务地址，跳出检查 例： www.hjsoft.com.cn,http://127.0.0.1,https://192.192.102.112
            String CSRF_webset = SystemConfig.getPropertyValue("CSRF_webset");

            if (CSRF_webset == null || CSRF_webset.length() < 1)
                return result;

            //如果地址登录或者接口等不需要检查的，跳出检查
            if (!needCheckSource(url, req))
                return result;

            //如果检查通过，跳出检查
            String requestReferer = req.getHeader("referer");
            String[] websets = CSRF_webset.split(",");
            for (int i = 0; i < websets.length; i++) {
                if (websets[i] == null || websets[i].length() < 1)
                    continue;
                if (requestReferer == null || requestReferer.startsWith(websets[i]))
                    return result;
            }

            //不通过的，返回提示信息
            res.setCharacterEncoding("UTF-8");
            res.setHeader("content-type", "text/html; charset=utf-8");
            res.setContentType("text/html;charset=utf-8");
            res.getWriter().write(html);
            result = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 是否需要检查链接来源
     *
     * @param url     请求地址
     * @param request HttpServletRequest
     * @return 是否需要检查
     */
    private boolean needCheckSource(String url, HttpServletRequest request) {
        boolean flag = true;
        //ehr系统登陆相关连接
        if (url.startsWith("/index.jsp"))
            flag = false;
        else if (url.startsWith("/templates/index/"))
            flag = false;
            //招聘外网通过邮箱激活账户，需放开
        else if (url.startsWith("/hire/hireNetPortal/search_zp_position") && request.getParameter("b_activecount") != null && "active".equalsIgnoreCase(request.getParameter("b_activecount")))
            flag = false;
            //招聘外网通过邮箱找回密码，需放开
        else if (url.startsWith("/module/hire/resetPassword.html") && request.getParameter("action") != null && "resetpassword".equalsIgnoreCase(request.getParameter("action")))
            flag = false;
            //职称评审登陆
        else if (url.startsWith("/module/jobtitle/hcmlogon.html"))
            flag = false;
            //外部系统集成，这两个为固定参数
        else if (request.getParameterMap().containsKey("etoken") || request.getParameterMap().containsKey("appfwd") || request.getParameterMap().containsKey("encryptParam"))
            flag = false;
            //问卷调查
        else if (url.startsWith("/module/system/questionnaire/"))
            flag = false;
            //接收短信
        else if (url.startsWith("/servlet/sms/SmsAcceptGSTXServlet"))
            flag = false;
            //单点登录
        else if (url.startsWith("/logon/logonService"))
            flag = false;
            //获取令牌
        else if (url.startsWith("/system/createtoken"))
            flag = false;
            //webservice接口
        else if (url.startsWith("/services/"))
            flag = false;

        //除以上链接全部需要检查
        return flag;
    }

    /**
     * 检查请求是否超频
     *
     * @param request
     * @param url
     * @return
     */
    private boolean isOverFrequency(HttpServletRequest request, String url) {
        String requestFrequency = SystemConfig.getPropertyValue("requestFrequency");
        if (requestFrequency == null || requestFrequency.length() < 1)
            return false;

        //如果不是.do请求和ajax交易类请求，认为是加载静态资源，不拦截
        if (isStaticResourcesRequest(url))
            return false;

        HttpSession session = request.getSession();
        //获取计数器对象
        HashMap counter = (HashMap) session.getAttribute("requestCounter");
        //如果计数器不存在，session中添加计数器
        if (counter == null) {
            counter = new HashMap();
            counter.put("time", new Date());
            counter.put("count", 1);
            counter.put("legal", true);
            session.setAttribute("requestCounter", counter);
            return false;
        }

        //如果计数器中合法状态 为false，说明此session可能存在暴力攻击
        boolean legal = (Boolean) counter.get("legal");
        if (!legal) {
            return true;
        }

        Date lastTime = (Date) counter.get("time");
        Date nowTime = new Date();
        //如果上次访问时间跟此次访问时间相差超过1秒，重置计数器
        if (nowTime.getTime() - lastTime.getTime() >= 1000) {
            counter.put("time", nowTime);
            counter.put("count", 1);
            session.setAttribute("requestCounter", counter);
            return false;
        }

        //如果两次相差不到一秒，获取当前秒访问次数，超过10次，认为超频，不响应
        int count = (Integer) counter.get("count");
        count++;
        //int frequency = 50;
        //if(requestFrequency!=null && requestFrequency.length()>0)
        int frequency = Integer.parseInt(requestFrequency);
        if (count > frequency) {
            counter.put("legal", false);
            session.setAttribute("requestCounter", counter);
            return true;
        }

        //1秒不超过10此，不超频，正常访问，并更新计数
        counter.put("count", count);
        session.setAttribute("requestCounter", counter);
        return false;
    }

    private boolean isStaticResourcesRequest(String url) {
        if (url.endsWith(".do"))
            return false;
        //if(url.equals("/ajax/ajaxService"))
        //	return false;
        if (url.indexOf(".") == -1)
            return false;

        return true;
    }
}
