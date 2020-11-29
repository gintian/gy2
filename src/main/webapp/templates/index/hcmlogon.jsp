<%@page import="java.io.File"%>
<%@page pageEncoding="utf-8" %>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.hrms.hjsj.sys.VersionControl"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig,java.util.*,com.hrms.hjsj.sys.ResourceFactory,com.hjsj.hrms.utils.PubFunc,com.hrms.hjsj.sys.ConstantParamter"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient,com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>
<%@ page import="java.nio.charset.Charset" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv=X-UA-Compatible content=IE=EDGE />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
    <%
        //如果使用的是标准版的锁直接跳到hrlogon.jsp  xuj add 2015-1-19
        EncryptLockClient lock = (EncryptLockClient)request.getSession().getServletContext().getAttribute("lock");
//logo 路径前缀 默认为空，即HCM
        String logoPrefix = "";
        if(lock!=null){
            if(lock.getVersion()<70)
                response.sendRedirect("/templates/index/hrlogon.jsp");
            //71以后版本专业版为HCM，标准版为eHR。锁版本为70及以上，程序版本大于等于71 并且 是标准版，显示eHR guodd 2017-10-13
            VersionControl ver=new VersionControl();
            if(ver.getVer_no()>=71 && lock.getVersion()>=70 && lock.getVersion_flag()==0)
                logoPrefix = "ehr-";
        }

        /*是否启用U盾验证*/
        String useUSBKey = ConstantParamter.getAttribute("enbleUsbControl");

        String admin_contact_phone=SystemConfig.getPropertyValue("admin_contact_phone");
        String admin_contact_email=SystemConfig.getPropertyValue("admin_contact_email");
        String logon_welcome_content=SystemConfig.getPropertyValue("logon_welcome_content");
        logon_welcome_content=(logon_welcome_content!=null&&logon_welcome_content.length()!=0)?new String(logon_welcome_content.getBytes("ISO8859_1"),"GB2312"):"";
        logon_welcome_content=PubFunc.splitString(logon_welcome_content,248);
        String title = SystemConfig.getPropertyValue("frame_logon_title");
        //system.properties已经修改为utf-8编码格式，获取参数后不用再转码
        title=(title!=null&&title.length()!=0)?title:ResourceFactory.getProperty("frame.logon.title");
        //主题皮肤
        String themes="default";

        String aurl = (String)request.getServerName();
        String port=request.getServerPort()+"";
        String prl=request.getProtocol();
        int idx=prl.indexOf("/");
        prl=prl.substring(0,idx);
        String url_p=SystemConfig.getCsClientServerURL(request);

        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);

        String realurl = request.getSession().getServletContext().getRealPath(
                "/");
        if (SystemConfig.getPropertyValue("webserver").equals("weblogic")) {
            realurl = request.getSession().getServletContext().getResource(
                    "/").getPath();
        }
        //获取后台有多少背景图片
        File file = new File(realurl + "/images/hcm/themes/default/login");
        int bgCount = 0;
        if(file.exists()) {
            String[] bgs = file.list();
            for(int i = 0; i < bgs.length; i++){
                if(bgs[i].startsWith("bg") && bgs[i].toLowerCase().endsWith(".jpg")) {
                    bgCount++;
                }
            }
        }
        //随机生成背景图片的名称中的数字，背景图片的名称格式：bg数字.jpg
        String[] numbers = new String[bgCount];
        for(int i = 0; i < bgCount; i++){
            numbers[i] = (i+1) + "";
        }

        String[] bgIds = new String[3];
        if(bgCount > 0) {
            Random random = new Random();
            int index=0;
            for(int i=0;i<3;i++) {
                index=random.nextInt(bgCount);
                if(i > 0 && Arrays.asList(bgIds).contains(numbers[index])) {
                    i--;
                } else {
                    bgIds[i] = numbers[index];
                }
            }
        }
    %>
    <title><%=title%></title>
    <link rel="icon" href="favicon.ico" type="image/x-icon" />
    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
    <LINK rel="bookmark" href="favicon.ico"  type="image/x-icon">

    <link id="css" href="/css/hcm/login.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="/jquery/jquery-3.5.1.min.js"></script>
    <script language="JavaScript" src="/jquery/JQuery.md5.js"></script>
    <script language="JavaScript" src="/ajax/basic.js"></script>
    <script language="JavaScript" src="/js/validate.js"></script>
    <script language="JavaScript" src="/js/function.js"></script>
    <script language="JavaScript" src="/js/constant.js"></script>
    <script language="JavaScript" src="/js/rec.js"></script>
    <script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
    <script language="JavaScript" src="/js/hjsjUrlEncode.js"></script>
    <script language="JavaScript" src="/ext/ext6/ext-all.gzjs"></script>
    <script language="JavaScript" src="/ext/ext6/locale-zh_CN.js"></script>
    <link id="css" href="/ext/ext6/resources/ext-theme.css" rel="stylesheet" type="text/css" />

    <!-- U盾安全认证 -->
    <script type="text/javascript" src="../../module/template/signature/encryptionlock/websocket.js"></script>
    <script type="text/javascript" src="../../module/template/signature/encryptionlock/RockeyArmCtrl.js"></script>
    <script type="text/javascript" src="../../module/template/signature/encryptionlock/base64.js"></script>

    <style>
        .x-window-body-default{
            border:none;
        }
    </style>

</head>
<script type="text/javascript">
    //判断是否宽屏
    var winWide = window.screen.width;
    if (winWide <1024) {//1024及以下分辨率
        $("#css").attr("href", "/css/hcm/login2.css");
    } else if (winWide <1440){
        $("#css").attr("href", "/css/hcm/login1.css");
    }else{
        $("#css").attr("href", "/css/hcm/login.css");
    }

    function winopen()
    {
        var userID=document.logonForm.username.value;
        var password = document.getElementById('passwordInput').value;
        var appdate=document.logonForm.appdate.value;
        appdate=appdate.replace(/\-/g,".");
        var url = "/templates/index/hrlogon.do?logon.x=link&username="+userID+"&password="+password+"&appdate="+appdate;
        newwin=window.open(url,"_blank","toolbar=no,location=no,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=no");
        if (document.all){
            newwin.moveTo(-4,-4);
            newwin.resizeTo(screen.width+6,screen.height-20);
        }
        newwin.location=url;
        window.opener=null;//不会出现提示信息
        self.close();
    }

    function pf_ChangeFocus(e)
    {
        e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
        var key = window.event?e.keyCode:e.which;
        var t=e.target?e.target:e.srcElement;
        if ( key==0xD && t.tagName!='TEXTAREA') /*0xD*/
        {
            if(t.name!="logon")
                if(window.event) {
                    e.keyCode=9;
                }else{
                    e.which=9;
                }
        }
        key = window.event?e.keyCode:e.which;
        if(key==13){
            var obj=document.getElementsByName("logon")[0];
            obj.click();
        }
    }
    function up_ChangeFocus(e)
    {
        e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
        var t=e.target?e.target:e.srcElement;
        if(t.name=="logon")
        {
            //document.logonForm.submit();
            var obj=document.getElementsByName("logon")[0];
            obj.click();
        }

    }
    /*设置计算截止日期*/
    function getAppdate()
    {
        /*业务日期不走cookie 直接获取当前日期 guodd 2020-04-22*/
        var now = new Date();
        var strvalue=getDateString(now,".");
        <%  if(StringUtils.isEmpty(SystemConfig.getPropertyValue("dusi_date_display")) || SystemConfig.getPropertyValue("dusi_date_display").equalsIgnoreCase("true")){ %>
        document.logonForm.appdate.value=strvalue.replace(/\./g,"-");
        <%}%>
    }

    function setAppdate()
    {
        var strvalue;
        strvalue=document.logonForm.appdate.value;
        strvalue=strvalue.replace(/\-/g,".");
        setCookie("appdate",strvalue);
    }


    function logondisabled(obj){
        /*非IE浏览器 U盾读取是异步的方式，需要以回调的方式执行登陆*/
        var doLogon = function(){
            var passwordTransEncrypt="<%=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, "password_trans_encrypt")%>";
            if(passwordTransEncrypt=="true"){
                var password = document.getElementById('passwordInput').value+<%=hour%>;
                document.logonForm.password.value="MD5`"+$.md5(password);
            }else{
                document.logonForm.password.value = document.getElementById('passwordInput').value;
            }
            logonForm.action="/templates/index/hcmlogon.do";
            logonForm.submit();
            obj.disabled=true;
        }

        readUSBKey(doLogon);

    }




    function enterToTab(event, input) {
        return;
        var e = event?event:window.event;
        var form = document.getElementById('form1');
        if(e.keyCode == 13) {
            var tabindex = input.getAttribute('tabindex');
            tabindex++;
            var inputs = form.getElementsByTagName('input');
            for(var i=0,j=inputs.length;  i<j; i++) {
                if (inputs[i].getAttribute('tabindex') == tabindex) {
                    inputs[i].focus();
                    break;
                }
            }
        }
    }

    function checkBrowserSettings()
    {
        AxManager.checkBrowserSettings('<%=url_p%>', 'ax');
    }


    function reloadCss () {
        var obj = document.getElementById('codeRefresh');
        var imgObj = document.getElementById('vaildataCode');
        if(!obj || !imgObj)
            return;

        var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
        var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1; //判断是否IE浏览器
        if (isIE) {
            var reIE = new RegExp("MSIE (\\d+\\.\\d+);");
            reIE.test(userAgent);
            var fIEVersion = parseFloat(RegExp["$1"]);
            if(fIEVersion < 8) {
                obj.style.marginTop="-25px";
                imgObj.style.marginTop="7px";
            }
        }
    }
    /*显示日期控件*/
    function showCalendar(){
        if(window.datePicker){
            return;
        }
        var appDateEl =  document.getElementsByName("appdate")[0];
        window.datePicker = Ext.widget('datepicker',{
            value:Ext.Date.parse(appDateEl.value, "Y-m-d"),
            format:'Y-m-d',
            alignTarget:appDateEl,
            renderTo:document.body,
            style:'z-index:200',
            floating:true,
            defaultAlign:'tc-bc?',
            shadow:false,
            alwaysOnTop:true,
            listeners:{
                select:function(picker,date){
                    var dateValue = Ext.Date.format(date,'Y-m-d');
                    document.getElementsByName("appdate")[0].value = dateValue;
                    picker.destroy();
                    window.datePicker = undefined;
                }

            }
        });

    }
    /*判断是否隐藏日期控件*/
    function hideCalendarIf(evt) {
        if(!window.datePicker)
            return;
        if(!window.datePicker.owns(evt.srcElement) && evt.srcElement.name!='appdate'){
            window.datePicker.destroy();
            window.datePicker = undefined;
        }
    }
</script>

<body onKeyDown="" onKeyUp="" onload="getAppdate();checkBrowserSettings();" onclick="hideCalendarIf(event)">
<%
    String ShowHomePagePluginSetup=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.SHOWHOMEPAGEPLUGINSETUP);
    if(ShowHomePagePluginSetup.equalsIgnoreCase("true")&&false)
    {
%>
<%}%>
<div id="name_hint" style="display:none;z-index:111;position:absolute;overflow:hidden;color:#a1acb8;"
     onclick="this.style.display='none';document.getElementsByName('username')[0].focus();">请输入用户名</div>
<div id="pwd_hint" style="display:none;z-index:111;position:absolute;overflow:hidden;color:#a1acb8;"
     onclick="this.style.display='none';document.getElementById('passwordInput').focus();">请输入密码</div>

<html:form focus="username" action="/templates/index/hcmlogon"  styleId="form1">
    <input type="hidden" name="password" value=""/>
    <input type="hidden" name="USBKeyInfo" value=""/>
    <div class="hj-hy-all">
        <div class="hj-hy-all-one">
                <%-- <%if(bgCount > 0) {%>
                 <img id="bgimg1" width="100%" height="100%" src="/images/hcm/themes/default/login/bg<%=bgIds[0] %>.jpg" style="z-index: 1;position: fixed;"/>
                 <img id="bgimg2" width="100%" height="100%" src="/images/hcm/themes/default/login/bg<%=bgIds[1] %>.jpg" style="z-index: 1;position: fixed; display: none;"/>
                 <img id="bgimg3" width="100%" height="100%" src="/images/hcm/themes/default/login/bg<%=bgIds[2] %>.jpg" style="z-index: 1;position: fixed; display: none;"/>
                 <%} %>--%>
            <img id="mybg" width="100%" height="100%" src="/images/hcm/themes/default/login/gzb-bg01.png" style="z-index: 1;position: fixed; "/>
            <div class="hj-hy-all-one-logo" style="z-index: 99;position: fixed;">
                <!-- 	<img src="/images/hcm/themes/default/login/logo.png" /> -->
            </div>
            <div class="bh-clear" style="z-index: 99;position: fixed;"></div>
            <div id='formDiv' class="hj-hy-form" style="z-index: 99;position: fixed;">
                <table border="352" cellpadding="0" cellspacing="0">
                    <tr>
                        <td colspan="2" style="font-size:14px;"><img src="/images/hcm/themes/default/login/logo_small-01.svg" class='hj-hy-title' /></td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <input name="username"  TABINDEX="1" type="text" class="hj-hy-yonhum" value="" maxlength="25" title="用户名"
                                   onkeydown="document.getElementById('name_hint').style.display='none';enterToTab(event,this);"
                                   onfocus="document.getElementById('name_hint').style.display='none';" autocomplete='off'
                                   onclick="document.getElementById('name_hint').style.display='none';"
                                   onblur="if (this.value=='') document.getElementById('name_hint').style.display='block';"
                                   oninput="if(this.value!='')document.getElementById('name_hint').style.display='none'"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <input type="hidden" name="logon.x" value="link" />
                            <input type="password" TABINDEX="2" autocomplete='off' id="passwordInput" class="hj-hy-mima"
                                   maxlength="25" size="20" class="textCss1" value=""  title="密　码"
                                   onfocus="document.getElementById('pwd_hint').style.display='none';"
                                   onblur="if (this.value=='') document.getElementById('pwd_hint').style.display='block';"
                                   onclick="document.getElementById('pwd_hint').style.display='none';"
                                   onkeydown="document.getElementById('pwd_hint').style.display='none';enterToTab(event,this);" autocomplete='off'
                                   onpropertychange="if(this.value!='')document.getElementById('pwd_hint').style.display='none'"
                                   oninput="if(this.value!='')document.getElementById('pwd_hint').style.display='none'"/>
                        </td>
                    </tr>
                    <%
                        String retrieving_password=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.RETRIEVING_PASSWORD);
                        if(SystemConfig.getPropertyValue("dusi_date_display").equalsIgnoreCase("false")
                                &&(!SystemConfig.isValidateCode())&&!(retrieving_password.equalsIgnoreCase("true"))) {%>
                    <tr>
                        <td colspan="2">
                            <input  name="logon" type="button" class="hj-hy-login" value="登录"  TABINDEX="3"
                                    onclick="logondisabled(this);"/>
                        </td>
                    </tr>
                    <%}
                        if(SystemConfig.getPropertyValue("dusi_date_display").equalsIgnoreCase("false")
                                &&(!SystemConfig.isValidateCode())&&(retrieving_password.equalsIgnoreCase("true"))) {%>
                    <tr>
                        <td colspan="2">
                            <input  name="logon" type="button" class="hj-hy-login" value="登录"  TABINDEX="3"
                                    onclick="logondisabled(this);"/>
                        </td>
                    </tr>
                    <%}
                        if(SystemConfig.getPropertyValue("dusi_date_display")==null || SystemConfig.getPropertyValue("dusi_date_display").equals("")
                                ||SystemConfig.getPropertyValue("dusi_date_display").equalsIgnoreCase("true")){ %>
                    <tr style="">
                        <td colspan="2">
                            <input name="appdate" maxlength="10" size="20" onclick="showCalendar()" TABINDEX="3"
                                   type="text" class="hj-hy-riqi" title="" autocomplete='off'
                                   onkeydown="enterToTab(event,this);"/>
                            <input type='hidden' id='cal_width' value='255'/>
                        </td>
                    </tr>
                    <% if(!SystemConfig.isValidateCode()) {%>
                    <tr>
                        <td colspan="2">
                            <%if(SystemConfig.isPwdCookie()) {%>
                            <input  name="logon" type="button" class="hj-hy-login" value="登录"
                                    TABINDEX="4" onclick="saveUser();logondisabled(this);"/>
                            <%}else{ %>
                            <input  name="logon" type="button" class="hj-hy-login" value="登录"
                                    TABINDEX="4" onclick="logondisabled(this);"/>
                            <%} %>
                        </td>
                    </tr>
                    <%}
                    }

                        if(SystemConfig.isValidateCode()) {
                            //xus 17/5/22 validatecode_type 初始是否有值
                            String validatecode_type=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, "validatecode_type");
                            String corpid=(String) ConstantParamter.getAttribute("wx", "corpid");
                            //发送短信和微信
                            if("1".equals(validatecode_type)) { %>
                    <tr>
                        <td colspan="2">
                            <input type="text" placeholder="请输入验证码" name="validatecode" maxlength="20" size="20" TABINDEX="3" class="hj-hy-yzm" onfocus="if(this.value==''){this.value='';this.style.color='#000';}"
                                   onblur="if (this.value=='') {this.value='';this.style.color='#a1acb8';}" autocomplete='off'
                                   onkeydown="enterToTab(event,this);">
                            <br>
                            <table id="codesender"  style="float:right;margin-top:10px;" width=150 cellpadding=0 cellspacing=0>
                                <tr>
                                    <td id="sendType" style="height: 15px;">
                                        <a id="send1" href="#" onclick="sendMessage('1')" style="float: none;">短信获取</a>
                                        <%
                                            //没有配置微信企业号则不显示发送微信按钮
                                            if(corpid!=null&&corpid.length()>0){
                                        %>
                                        <a id="send2" href="#" onclick="sendMessage('2')" style="float: none;">微信获取</a>
                                        <%} %>
                                    </td>
                                    <td id="resend" style='display:none;height: 15px;' valign="middle" >
                                        <img style="float:left;margin-top:1px;" align="absMiddle" src='/images/hcm/themes/default/login/send_ok.gif' >
                                        <A style="float:left;margin-top:0px;"  onclick="resendFunc()" href="#"  >重新发送</A>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <% }else {
                        //原始验证码
                    %>
                    <tr>
                        <td colspan="2">
                            <input type="text" name="validatecode" maxlength="20" size="20" TABINDEX="3" class="hj-hy-yzm"
                                   placeholder="请输入验证码"  onfocus="if(this.value==''){this.value='';this.style.color='#000';}"
                                   onblur="if (this.value=='') {this.value='';this.style.color='#a1acb8';}" autocomplete='off'
                                   onkeydown="enterToTab(event,this);">&nbsp;
                            <img align="absMiddle" src="/servlet/vaildataCode?channel=1&codelen=4"
                                 id="vaildataCode" class="hj-hy-yzm-img" title="换一张" onclick="validataCodeReload()"/>
                        </td>
                    </tr>
                    <%}
                        if(SystemConfig.isPwdCookie()) {%>
                    <tr>
                        <td colspan="2">
                            <input  name="logon" type="button" class="hj-hy-login" value="登录"  TABINDEX="4"
                                    onclick="saveUser();logondisabled(this);"/>
                        </td>
                    </tr>
                    <%}else{ %>
                    <tr>
                        <td colspan="2">
                            <input  name="logon" type="button" class="hj-hy-login" value="登录"  TABINDEX="4"
                                    onclick="logondisabled(this);"/>
                        </td>
                    </tr>
                    <%}
                    } %>
                    <tr style="margin-top:20px;height:40px;border:none;">
                        <td style="vertical-align:top;width:50%;line-height:50px;">
                            <% if(SystemConfig.isPwdCookie()) {%>
                            <input name="chk" type="checkbox" value="1" class="hj-hy-jzzh" />
                            <a href="#" style="color:#fff; font-size:14px;margin-left:10px;">免登录</a>
                            <%} %> &nbsp;
                        </td>
                        <td align="right" class="wjmm" style="vertical-align:top;">
                            <% if(retrieving_password.equalsIgnoreCase("true")){ %>
                            <a href="###" onclick="getPassword('1','hcm');"
                               style="color:#fff; font-size:14px;margin-left:10px;border: none;">忘记密码？</a>
                            <% } %>  &nbsp;
                        </td>
                    </tr>
                </table>
            </div>
            <div class="hj-wzm-copyright" id="copyright" style="z-index: 99;display: none;">Copyright© BANK OF GUIZHOU(BOGZ) All Rights Reserved. </div>
                <%--<div class="hj-wzm-li" id="hj-wzm-li" style="display: none; z-index: 99;position:absolute;">
                    <ul>
                        <li style="height: 5xp;">
                            <a href="###" onclick="changeBanerImg(1);" class="png current">
                                <img id="unSelect1" src="/images/hcm/themes/default/login/li.png" style="display: none;"></img>
                                <img id="select1" src="/images/hcm/themes/default/login/li_s.png"></img>
                            </a>
                        </li>
                        <li style="height: 5xp;">
                            <a href="###" onclick="changeBanerImg(2);" class="png">
                                <img id="unSelect2" src="/images/hcm/themes/default/login/li.png"></img>
                                <img id="select2" src="/images/hcm/themes/default/login/li_s.png" style="display: none;"></img>
                            </a>
                        </li>
                        <li style="height: 5xp;">
                            <a href="###" onclick="changeBanerImg(3);" class="png">
                                <img id="unSelect3" src="/images/hcm/themes/default/login/li.png"></img>
                                <img id="select3" src="/images/hcm/themes/default/login/li_s.png" style="display: none;"></img>
                            </a>
                        </li>
                    </ul>
                </div>--%>
        </div>
        <div class="bh-clear"></div>
    </div>
</html:form>

<div style="display:none;">
    <script type="text/javascript">
        AxManager.write("ax", 0, 0);
    </script>
</div>
<!-- U盾控件 -->
<OBJECT id="ctrl" classid="clsid:33020048-3E6B-40BE-A1D4-35577F57BF14" VIEWASTEXT width="0" height="0"></OBJECT>
</body>
<script type="text/javascript">
    function closePassWin(){
        window.passWindow.destroy();
    }
    function getAbsPosition(obj, offsetObj){
        var _offsetObj=(offsetObj)?offsetObj:document.body;
        var x=obj.offsetLeft;
        var y=obj.offsetTop;
        var tmpObj=obj.offsetParent;

        while ((tmpObj!=_offsetObj) && tmpObj){
            x += tmpObj.offsetLeft - tmpObj.scrollLeft + tmpObj.clientLeft;
            y += tmpObj.offsetTop - tmpObj.scrollTop + tmpObj.clientTop;
            tmpObj=tmpObj.offsetParent;
        }
        return ([x, y]);
    }

    //xus 17/5/23 发送验证码跳转
    function sendMessage(type){
        var username=document.getElementsByName("username")[0].value;
        var pwd=document.getElementById("passwordInput").value;
        var send=document.getElementById("send"+type).disabled;
        if(send==true){
            return;
        }
        document.getElementById("send1").disabled=true;
        if(document.getElementById("send2"))
            document.getElementById("send2").disabled=true;
        $.ajax({
            url:'/templates/index/send_validcode.jsp',
            type:'post',
            data:{'username':username,'pwd':pwd,'type':type},
            dataType:'json',
            success:function(result){
                if(result.send=='true'){
                    if(result.code){
                        document.getElementsByName("validatecode")[0].value=result.code;
                    }

                    document.getElementById("send1").disabled=false;
                    if(document.getElementById("send2"))
                        document.getElementById("send2").disabled=false;

                    document.getElementById("sendType").style.display='none';
                    document.getElementById("resend").style.display='block';
                    settime();
                    return;
                }


                if(result.msg=='no_user')
                    alert("用户名或密码错误！");
                else if(result.msg=='no_phone')
                    alert("手机号码不存在，请选择其他验证方式！");
                else if(result.msg=='fail')
                    alert("验证码发送失败，请重试！");
                document.getElementById("send1").disabled=false;
                if(document.getElementById("send2"))
                    document.getElementById("send2").disabled=false;
                //document.getElementById("sendType").style.display='block';
                //document.getElementById("resend").style.display='none';
                clearTimeout(timeOut);
            },
            error:function(result){
                alert("发送失败，请选择其他方式！");
                document.getElementById("send1").disabled=false;
                if(document.getElementById("send2"))
                    document.getElementById("send2").disabled=false;
                //document.getElementById("sendType").style.display='block';
                //document.getElementById("resend").style.display='none';
                clearTimeout(timeOut);
            }
        });
    }
    //xus 17-6-24 短信验证码60秒后重新发送
    <%
        String eff_Sec=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, "validatecode_effect_second");
        int second =(eff_Sec==null||"".equals(eff_Sec))?60:Integer.parseInt(eff_Sec);
    %>
    var	countdownfinal=<%=second %>;
    if(countdownfinal==0)
        countdownfinal=60;
    var countdown= countdownfinal;
    var timeOut;
    //读秒倒计时
    function settime() {
        document.getElementById("resend")
        if (countdown == 0) {
            document.getElementById("resend").removeAttribute("disabled");
            document.getElementById("resend").getElementsByTagName("a")[0].innerHTML="重新发送";
            document.getElementById("resend").getElementsByTagName("a")[0].onclick=resendFunc;
            countdown =countdownfinal;
            return;
        } else {
            document.getElementById("resend").setAttribute("disabled", true);
            document.getElementById("resend").getElementsByTagName("a")[0].innerHTML="重新发送(" + countdown + ")";
            document.getElementById("resend").getElementsByTagName("a")[0].onclick=null;
            countdown--;
        }
        timeOut=setTimeout(settime,1000);
    }
    //xus 17-6-26 短信验证码重新发送方法
    function resendFunc() {
        javascript:document.getElementById('sendType').style.display='block';
        document.getElementById('resend').style.display='none';
        document.getElementsByName("validatecode")[0].value="验证码";
    }


    function initPosition(){
        var _username = document.getElementsByName('username')[0];
        var _password = document.getElementById('passwordInput');
        var _name_hint = document.getElementById('name_hint');
        var _pwd_hint = document.getElementById('pwd_hint');
        var namepos=getAbsPosition(_username);
        var pwdpos=getAbsPosition(_password);
        //alert(namepos[0]);
        var left = 50, top = 7;
        if (winWide <1024) {//1024及以下分辨率
            left = 40, top = 3;
        } else if (winWide <1440){
            left = 40, top = 6;
        }

        if(_username.value.length==0){
            _name_hint.style.left=(namepos[0]+left)+'px';
            _name_hint.style.top=(namepos[1]+top)+'px';
            _name_hint.style.display = "block";
        }

        if(_password.value.length==0){
            _pwd_hint.style.left=(pwdpos[0]+left)+'px';
            _pwd_hint.style.top=(pwdpos[1]+top)+'px';
            _pwd_hint.style.display = "block";
        }
    }


    $(document).ready(function(){
        setTimeout("initPosition()",100);
        initData();
    });

    var frmname=window.name;
    if(frmname!=""&&(frmname!="0"&&frmname!="1"&&frmname!="iphone"&&frmname!="if_frame")&&frmname.indexOf("refer_pv_id=")==-1)//refer_pv_id=VpUZSK&wm_old_value=是从taoba过来的连接，暂时先这么改，这么控制不太合适，应统计一下系统内会给window.name哪些值
    {
        alert(SYS_LBL_SESSION);
        var newwin=window.open(window.location,"_top","toolbar=no,location=no,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=no");
        window.opener=null;
        self.close();
    }

    var inum=1;
    // function changeBanerImg(index){
    //     inum=index;
    //     //解决图片切换重新下载图片问题，通过显示和隐藏实现图片切换，节省流量 guodd 18/05/28
    //     for(var bIndex = 1;bIndex<=3;bIndex++ ){
    //         if(bIndex==index) {
    //             document.getElementById("bgimg"+bIndex).style.display="block";
    //             document.getElementById("select"+bIndex).style.display="block";
    //             document.getElementById("unSelect"+bIndex).style.display="none";
    //         } else {
    //             document.getElementById("bgimg"+bIndex).style.display="none";
    //             document.getElementById("select"+bIndex).style.display="none";
    //             document.getElementById("unSelect"+bIndex).style.display="block";
    //         }
    //     }
    // }

    var froms = document.getElementsByTagName("form");
    for ( var i = 0; i < froms.length; i++) {
        froms[i].setAttribute("autocomplete","off");
    }
</script>
<script language="JavaScript" src="/js/newcalendar.js"></script>

<script type="text/javascript">
    $(function(){
        $("input[TABINDEX=1]").focus();

    });

    document.onkeydown = function enterHandler(event){
        var inputs = $("input[TABINDEX]"); //可自行添加其它过滤条件
        var browser = navigator.appName ; //浏览器名称
        var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串

        var Code = '' ;
        if(browser.indexOf('Internet')>-1) // IE
            Code = window.event.keyCode ;
        else if(userAgent.indexOf("Firefox")>-1) // 火狐
            Code = event.which;
        else // 其它
            Code = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;

        if (Code == 13) //可以自行加其它过滤条件
        {
            for(var i=0;i<inputs.length;i++){
                if(inputs[i].name == document.activeElement.name){
                    //console.log(inputs[i]);
                    var index = $(inputs[i]).attr("TABINDEX");

                    $("input[TABINDEX="+(parseInt(index) + 1)+"]").focus();

                    break;
                }
            }


            if(document.activeElement.name=="logon"){
                //document.logonForm.submit();
                var obj=document.getElementsByName("logon")[0];
                // obj.click();
                <%
                    if(SystemConfig.isPwdCookie())
               {%>
                saveUser();
                <%}%>
                logondisabled(obj);
            }
        }


    };

    reloadCss();

    /*读取U盾需要的全局参数*/
    var ctrl = null;
    var websock = true;
    var b = new Base64();
    var Handle,	//加密锁句柄
        Index = 0,  //默认打开识别出的第一把锁
        Offset = 2048,		//起始偏移
        ReadLength = 128;	//读取长度
    /*页面初始化成功后初始化U盾对象*/
    $(function(){
        /*如果开启U盾验证，初始化U盾插件*/
        if('<%=useUSBKey%>' != 'true'){
            return;
        }

        if(window.WebSocket){
            ctrl = new AtlCtrlForRockeyArm("{33020048-3E6B-40BE-A1D4-35577F57BF14}");
        }else{
            ctrl = document.getElementById("ctrl");
            websock = false;
        }

    });

    function readUSBKey(callBack){
        /*如果未开启U盾验证，直接执行登陆*/
        if('<%=useUSBKey%>' != 'true'){
            callBack();
            return;
        }


        /*如果是websocket模式且连接失败，直接登录*/
        if(websock && ctrl.isConnect==false){
            callBack();
            return;
        }

        /*websocket模式读取U盾*/
        if(websock){
            readDataByWebSocket(callBack);
        }else{/*控件读取*/
            readDataByActiveX(callBack);
        }

    }

    /*websocket方式读取U盾数据*/
    function readDataByWebSocket(callBack){
        /*读取U盾信息，并将信息填入表单中*/
        var readLockData = function(){
            ctrl.Arm_ReadData(function(result,lockdata){
                document.getElementsByName('USBKeyInfo')[0].value=lockdata;
                callBack();
            });

        };
        /*打开U盾*/
        var openLock = function(){
            ctrl.Arm_Open(function(result, handle){
                if (!result){
                    document.getElementsByName('USBKeyInfo')[0].value="";
                    callBack();
                    return;
                }
                Handle = handle;
                readLockData();
            });
        };
        try{
            /*检索U盾*/
            ctrl.Arm_Enum(function(result, locknum){
                //小于1说明没有检测到U盾
                if(!result || locknum<1 || locknum>10){
                    document.getElementsByName('USBKeyInfo')[0].value="";
                    callBack();
                    return;
                }
                /*打开U盾*/
                openLock();
            });
        }catch(e){
            document.getElementsByName('USBKeyInfo')[0].value="";
            callBack();
        }


    }

    // function reloop(){
    //     inum++;
    //     if(inum > 3) {
    //         inum = 1;
    //     }
    //
    //     changeBanerImg(inum);
    // }

    /*activeX插件方式读取U盾数据*/
    function readDataByActiveX(callBack){
        /*检测U盾个数*/
        try{
            var locknum = ctrl.Arm_Enum();
            //有时没插锁会返回很大的数字，此处判断一下，正常情况不会出现插10个锁，所以检测的锁数量如果超过10个认为异常
            if(locknum>0 && locknum<10){
                /*打开U盾*/
                var armHandle = ctrl.Arm_Open(0);
                /*读取U盾数据*/
                var lockdata = ctrl.Arm_ReadData(parseInt(armHandle), Offset,ReadLength);
                document.getElementsByName('USBKeyInfo')[0].value=lockdata;
            }else{
                document.getElementsByName('USBKeyInfo')[0].value="";
            }
        }catch(e){
            document.getElementsByName('USBKeyInfo')[0].value="";
        }
        callBack();
    }

    setInterval("{}",10000);
    window.onresize = function () {
        resizeChange();
    }

    function resizeChange() {
        var width = document.body.clientWidth;
        if (winWide <1024) {//1024及以下分辨率
            //document.getElementById('hj-wzm-li').style.marginLeft = ((width - 150) / 2) + "px";
            document.getElementById('copyright').style.marginLeft = ((width - 245) / 2) + "px";
            document.getElementById('formDiv').style.marginLeft = ((width - 200) / 2) + "px";
        } else if (winWide <1440){
            document.getElementById('hj-wzm-li').style.marginLeft = ((width - 150) / 2) + "px";
            document.getElementById('copyright').style.marginLeft = ((width - 245) / 2) + "px";
            document.getElementById('formDiv').style.marginLeft = ((width - 290) / 2) + "px";
        }else{
            //document.getElementById('hj-wzm-li').style.marginLeft = ((width - 150) / 2) + "px";
            document.getElementById('copyright').style.marginLeft = ((width - 245) / 2) + "px";
            document.getElementById('formDiv').style.marginLeft = ((width - 360) / 2) + "px";
        }

        initPosition();
    }

    resizeChange();
    window.load = function(){
        document.getElementById('passwordInput').value='';
    };
</script>

</html>
