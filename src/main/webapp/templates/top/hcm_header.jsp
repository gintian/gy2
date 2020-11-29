<%@page import="com.hrms.hjsj.sys.VersionControl"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hrms.frame.utility.AdminCode"%>
<%@ page import="com.hjsj.hrms.utils.ResourceFactory"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hrms.hjsj.sys.Des,com.hrms.frame.utility.AdminDb"%>
<%@ page import="java.util.Date,com.hrms.hjsj.sys.DataDictionary,com.hrms.hjsj.sys.FieldItem,com.hjsj.hrms.businessobject.sys.SysParamBo" %>
<%@ page import="com.hrms.frame.dao.utility.DateUtils,java.sql.*,com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter" %>

<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    if (userView!=null){
        String path = request.getSession().getServletContext().getRealPath("/js");
        if (SystemConfig.getPropertyValue("webserver").equals("weblogic")) {
            path = session.getServletContext().getResource("/js").getPath();//.substring(0);
            if (path.indexOf(':') != -1) {
                path = path.substring(1);
            } else {
                path = path.substring(0);
            }
            int nlen = path.length();
            StringBuffer buf = new StringBuffer();
            buf.append(path);
            buf.setLength(nlen - 1);
            path = buf.toString();
        }
        userView.getHm().put("js_path", path);
    }
    int flag=1;
    String webserver=SystemConfig.getPropertyValue("webserver");
    if(webserver.equalsIgnoreCase("websphere"))
        flag=2;

%>

<%
    // 在标题栏显示当前用户和日期 2004-5-10
    String aurl = (String)request.getServerName();
    String port=request.getServerPort()+"";
    String prl=request.getProtocol();
    int idx=prl.indexOf("/");
    prl=prl.substring(0,idx);
    String url_p=SystemConfig.getCsClientServerURL(request);
    String userName = null;
    String pwd=null;
    boolean bexchange=false;
    int status=0;
    EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
    boolean bflag=lockclient.isBtest();
    String verdesc="";
    if(bflag)
    {
        verdesc=ResourceFactory.getProperty("label.sys.about.test");
    }
    String css_url="/css/css1.css";

    bexchange=userView.isBexchange();
    status=userView.getStatus();
    String view_time=SystemConfig.getPropertyValue("banner_viewTime");
    if(status==0)
    {
        userName=userView.getS_userName();
        if(userView.isBEncryPwd())
        {
            Des des=new Des();
            pwd=des.DecryPwdStr(userView.getS_pwd());
        }
        else
            pwd=userView.getS_pwd();
    }
    else
    {
        userName=userView.getUserName();
        if(userView.isBEncryPwd())
        {
            Des des=new Des();
            pwd=des.DecryPwdStr(userView.getPassWord());
        }
        else
            pwd=userView.getPassWord();
    }
    boolean bself=true;
    if(status!=4)
    {
        String a0100=userView.getA0100();
        if(a0100==null||a0100.length()==0)
        {
            bself=false;
        }
    }
    //主题皮肤
    String themes = "default";
    if(userView != null){
        css_url=userView.getCssurl();
        if(css_url==null||css_url.equals(""))
            css_url="/css/css1.css";
        //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");
        themes = SysParamBo.getSysParamValue("THEMES",userView.getUserName());
    }


    String time = String.valueOf(System.currentTimeMillis());

    String logoPath = "/images/hcm/themes/"+themes+"/login/";
    //71以后版本专业版为HCM，标准版为eHR。版本大于等于71 并且 是标准版，显示eHR guodd 2017-10-13
    int nver_s=lockclient.getVersion_flag();
    int nver=lockclient.getVersion();
    VersionControl ver=new VersionControl();
    if(ver.getVer_no()>=71 && nver>=70 && nver_s==0){
        logoPath+="logo_ehr.png";
    }else{
        //logoPath+="logo.png";
        logoPath+="logo_small.gif";
    }

    /*添加系统提示信息 guodd 2020-03-25*/
    String loginWarn = (String)request.getSession().getAttribute("loginWarn");
    if(loginWarn != null){
        request.getSession().removeAttribute(loginWarn);
    }else{
        loginWarn = "";
    }


%>
<link href="../../css/hcm/themes/<%=themes %>/layout.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="../../phone-app/jquery/jquery-3.5.1.min.js"></script>
<!-- <script type="text/javascript" src="../../ext/adapter/ext/ext-base.js"></script> -->
<script type="text/javascript" src="../../ext/ext-all.gzjs"></script>
<script type="text/javascript" src="../../ext/rpc_command.js"></script>
<script type="text/javascript" src="../../js/validate.js"></script>
<script type="text/javascript" src="../../ajax/basic.js"></script>
<script type="text/javascript" src="../../general/sys/hjaxmanage.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<!--[if lte IE 6]>
<script type="text/javascript" src="js/PNG.js"></script>
<script>PNG.fix('.png');</script>
<![endif]-->
<hrms:priv func_id="000107,3017,0B4,0B401,0B405" module_id="">
    <script type="text/javascript">
        var needSetIEAx = true;
    </script>
</hrms:priv>

<script language="JavaScript">
    if("<%=loginWarn%>".length>0){
        alert("<%=loginWarn%>");
    }
    //防止打开多个页面造成数据混乱 guodd 2015-12-18
    window.document.oncontextmenu = function(){return false;};
    function logout()
    {
        if(confirm("确定要退出吗？"))
        {
            var url = "/templates/index/hcm.jsp";
            url="/servler/sys/logout?flag=30";
            newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
            //window.opener=null;//不会出现提示信息
            //parent.window.close();
        }
    }
    function isclose()
    {
        if(confirm("确定要退出吗？"))
        {
            var url = "/templates/close.jsp";
            newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
            //parent.window.close();
        }
    }
    //进入或退出全屏
    function fullScreen(ele){
        window.parent.fullScreen(ele,"<%=themes%>");
    }
    function mustclose()
    {
        var url = "/templates/close.jsp";
        newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
        //parent.window.close();
    }
    function SetIEOpt()
    {
        if(needSetIEAx)
            if(!AxManager.setup("axc", "SetIE", 0, 0, null, AxManager.setIEName))
                return;

        var obj=document.getElementById('SetIE');
        if (obj != null)
        {
            obj.SetIEOptions('<%=url_p%>');
        }
    }
    //签到
    var netsiginFlag = false;
    function netsingin(singin_flag)
    {
        if(netsiginFlag) {
            alert('正在签到，请不要重复操作。');
            return;
        }

        var ip_addr="";
        ip_addr=getLocalIPAddressf();

        netsiginFlag = true;

        var map = new HashMap();
        map.put("singin_flag",singin_flag);
        map.put("ip_addr",ip_addr);
        Rpc({functionId:'15502110200',success:showReturn},map);
    }
    function showReturn(response)
    {
        netsiginFlag = false;
        /*var mess=outparamters.getValue("mess");
        alert(mess);
        if(mess.indexOf("成功")!=-1){
            sysForm.action="/kq/kqself/card/carddata.do?b_query=link";
            sysForm.target="il_body";
            sysForm.submit();
        }*/
        var value=response.responseText;
        //alert(value);
        var map=Ext.decode(value);
        if(map.succeed.toString()=='false'){
            alert(map.message);
        }else{
            var mess=map.mess;
            alert(mess);
            if(mess.indexOf("成功")!=-1){
                sysForm.action="/kq/kqself/card/carddata.do?b_query=link";
                sysForm.target="il_body";
                sysForm.submit();
            }
        }
    }
    /**取得本地机器ip地址*/
    function getLocalIPAddressf()
    {
        var obj = null;
        var rslt = "";
        try
        {
            if(needSetIEAx)
                if(!AxManager.setup("axc", "SetIE", 0, 0, null, AxManager.setIEName))
                    return;

            obj=document.getElementById('SetIE');
            if(obj)
                rslt = obj.GetIP();
            obj = null;
        }
        catch(e)
        {
            //异常发生
        }
        return rslt;
    }
    function change_agent(obj)
    {
        document.getElementById("agentId").value=obj.value;
        sysForm.action="/selfservice/selfinfo/agent/agent.do?b_agent_hcm=link";
        sysForm.target="_parent";
        sysForm.submit();
    }

    function changeThemes(themes){
        var map = new HashMap();
        map.put("username","${userView.userName}");
        map.put("themes",themes);
        Rpc({functionId:'15502110207',success:reThemes},map);

        function reThemes(response)
        {
            var value=response.responseText;
            var map=Ext.decode(value);
            if(map.succeed.toString()=='true'){
                window.location.reload();
            }
        }
    }
</script>
<SCRIPT language="javascript" type="text/javascript" >
    function StringToDate(DateStr,TimeStr)
    {

        var arys= DateStr.split('.');
        var arts= TimeStr.split(':');
        var myDate = new Date(arys[0],--arys[1],arys[2],arts[0],arts[1],arts[2]);
        return myDate;
    }
    function reloop(){
        if(!document.getElementById("t"))
            return;
        var timevalue = document.getElementById('timevalueid');
        var tmpvalue = parseInt(timevalue.value);
        var time=new Date(tmpvalue);
        timevalue.value=tmpvalue+1000;
        var datetime=time;
        var month = time.getMonth()+1;
        var date = time.getDate();
        var year = time.getFullYear();
        var hour = time.getHours();
        var minute = time.getMinutes();
        var second = time.getSeconds();

        if(month<10){
            month="0"+month;
        }
        if(date<10){
            date="0"+date;
        }
        var day = time.getDay();
        if (minute < 10)
            minute="0"+minute;
        if (second < 10)
            second="0"+second;
        var apm="AM";
        if (hour>12)
        {
            hour=hour-12;
            apm="PM" ;
        }
        if(hour<10){
            hour="0"+hour;
        }
        var weekday = 0;
        switch(time.getDay())
        {
            case 0:
                weekday = "星期日";
                break;
            case 1:
                weekday = "星期一";
                break;
            case 2:
                weekday = "星期二";
                break;
            case 3:
                weekday = "星期三";
                break;
            case 4:
                weekday = "星期四";
                break;
            case 5:
                weekday = "星期五";
                break;
            case 6:
                weekday = "星期六";
                break;
        }
        var datestr=datetime.getFullYear()+"."+(datetime.getMonth()+1)+"."+datetime.getDate();
        timestr=datetime.getHours()+":"+datetime.getMinutes()+":"+datetime.getSeconds();
        document.getElementById("t").value=year+"年"+month+"月"+date+"日"+" "+hour+":"+minute+":"+second+apm;
        //setTimeout("reloop(\""+datetime+"\")",1000);
        setTimeout("reloop()",1000);
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
    function showDateSelectBox(srcobj)
    {
        if(document.getElementById('selectname').value=="")
        {
            document.getElementById('date_panel').style.display="none";
            return false ;
        }

        date_desc=document.getElementById(srcobj);
        document.getElementById('date_panel').style.display="";
        var pos=getAbsPosition(date_desc);
        with(document.getElementById('date_panel'))
        {
            style.position="absolute";
            style.posLeft=pos[0];
            style.posTop=pos[1]-date_desc.offsetHeight+42;
            style.width=(date_desc.offsetWidth<20)?150:date_desc.offsetWidth+1;
        }
        //var hashVo = new ParameterSet();
        var map = new HashMap();
        map.put("name",getEncodeStr(document.sysForm.selectname.value));
        map.put("dbpre","usr");
        map.put("showDb","1");
        map.put("priv","1");
        map.put("dbtype","0");
        map.put("viewunit","0");
        map.put("isfilter","0");
        map.put("SYS_FILTER_FACTOR","");
        map.put("flag","4");
        Rpc({functionId:'3020071012',success:shownamelist},map);
        //var request=new Request({method:'post',onSuccess:shownamelist,functionId:'3020071012'},hashVo);
    }
    function shownamelist(outparamters)
    {
        /*var namelist=outparamters.getValue("namelist");
        if(namelist.length==0){
            Element.hide('date_panel');
        }
        else{
            AjaxBind.bind(sysForm.contenttype,namelist);
        }*/
        var value=outparamters.responseText;
        //alert(value);
        var map=Ext.decode(value);
        if(map.succeed.toString()!='false'){
            var namelist=map.namelist;
            if(namelist.length==0){
                document.getElementById('date_panel').style.display="none"
            }
            else{
                bind(sysForm.contenttype,namelist);
            }
        }else{
            document.getElementById('date_panel').style.display="none"
            alert(map.message);
        }
    }

    function bindSelect(elem,value)
    {
        if (typeof(value) != "object" || value.constructor != Array) {
            this.reportError(elem,value,"Array Type Needed for binding select!");
        }
        // delete all the nodes.
        while (elem.childNodes.length > 0) {
            elem.removeChild(elem.childNodes[0]);
        }
        // bind data
        for (var i = 0; i < value.length; i++)
        {
            var option = document.createElement("OPTION");
            var data = value[i];
            if (data == null || typeof(data) == "undefined") {
                option.value = "";
                option.text = "";
            }
            if (typeof(data) != 'object') {
                option.value = data;
                option.text = data;
            } else {
                option.value = data.dataValue;
                option.text = data.dataName;
            }
            elem.options.add(option);
        }
    }

    function bind(elem,value)
    {
        bindSelect(elem, value);
    }

    function showhidden(id,ctr){
        var elem = document.getElementById(id);
        if(elem)
            elem.style.display=ctr;
    }

    function menuTree(menu_id,menu_target,center_url,center_target,menu_name,mod_id,allname,be_link,span){
        //zhangh 2020-2-19 领导桌面走新页面
        if(menu_id == '21'){
            window.open('/module/bi_toolbox/index.html','_blank');
            return;
        }
        /*36546 防止频繁点击菜单，导致浏览器报错 guodd 2018-07-12*/
        if(span.getAttribute("disable")=="true")
            return;
        span.setAttribute("disable","true");
        setTimeout(function(){
            span.setAttribute("disable","false");
        },300);
        menu_name = $URL.encode(menu_name);
        allname = $URL.encode(allname);
        //判断一级菜单是直接打开连接还是加载下级菜单 guodd 2016-05-25
        if(be_link==true)
            document.getElementById("center_iframe").src="/templates/index/hcm_mainpanel.do?b_open=link&module="+mod_id+"&center_url="+center_url;
        else
            document.getElementById("center_iframe").src="/templates/index/hcm_mainpanel.do?b_query=link&module="+mod_id+"&menu_id="+menu_id+"&menu_target="+menu_target+"&center_url="+center_url+"&center_target="+center_target+"&menu_name="+menu_name+"&allname="+allname;

        //云平台单点控制
        if(menu_id == '04')
            window.open('/servlet/hrcloud/login','_blank');

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

    function initPosition(aTag){
        var pos=getAbsPosition(aTag);
        var themesbar=document.getElementById('themesid');
        themesbar.style.position="absolute";
        themesbar.style.left=pos[0]-7;
        if(Ext.isIE) {
            themesbar.style.top=pos[1]+aTag.offsetHeight+5;
        } else {
            themesbar.style.top=pos[1]+aTag.offsetHeight+25;
        }
    }

    function showthemes(obj){
        if(document.getElementById('themesid').style.display!='block'){
            showhidden('themesid','block');
            initPosition(obj);
        }else{
            showhidden('themesid','none');
        }
    }

    var selectmessage;
</SCRIPT>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <style type="text/css">
        INPUT {
            font-size: 12px;
            border-style:none ;
            background-color:transparent;
        }
    </style>
</head>
<body onbeforeunload="">
<form name="sysForm" action="" method=post>
    <input type="hidden" name="agentId">
    <input type="hidden" id="timevalueid" value="<%=time %>"/>
    <div class="headbg">
        <div class="header">
            <%--<img src="/images/hcm/themes/default/login/logo_small-01.svg" width="175" style="float: left;margin: 9px 115px 0 0;" />--%>
            <img src="/images/hcm/themes/default/login/logo_small.png" class="Idlogo png" width="175" height="44"/>
            <div class="timeview" style="display:none;">
                <h2>
                    <% if(bself){%>
                    <hrms:agent></hrms:agent>
                    <%} else{%>
                    <%if(view_time!=null&&view_time.equals("true")){ %>
                    <INPUT id="t" name="time" type="text" size="27" readonly="readonly">
                    <%}else{ %>
                    <INPUT id="t2" name="time" type="text" size="20" readonly="readonly">
                    <%} %>
                    <%} %>
                </h2>
            </div>
            <!-- 工具条按钮，其中包含便捷搜索或滚动欢迎词 -->
            <hrms:hcmmenu menu_id="90" menutype="toolbar"  themes="<%=themes %>"></hrms:hcmmenu>
            <!-- 导航菜单
        <div class="navArea">
            <div class="midnav">
            <ul class="nav">
                <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav1.png"/>组织结构</a></li>
                <li class="line"></li>
                <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav2.png"/>能力素质</a></li>
                <li class="line"></li>
                <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav3.png"/>员工管理</a></li>
                <li class="line"></li>
                <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav4.png"/>招聘管理</a></li>
                <li class="line"></li>
                <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav5.png"/>人事异动</a></li>
                <li class="line"></li>
                <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav1.png"/>组织结构</a></li>
                <li class="line"></li>
                <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav2.png"/>能力素质</a></li>
                <li class="line"></li>
                <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav3.png"/>员工管理</a></li>
                <li class="line"></li>
                <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav4.png"/>招聘管理</a></li>
                <li class="line"></li>
                <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav5.png"/>人事异动</a></li>
                <li class="line"></li>
                <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav3.png"/>员工管理</a></li>
                <li class="line"></li>
                <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav4.png"/>招聘管理</a></li>
                <li class="line"></li>
                <li><a href="javascript:void(0);"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav5.png"/>人事异动</a></li>
                <li class="line"></li>
            </ul>
            <a id="showmoreid" href="javascript:void(0);"  onclick="showhidden('menuid','block');" class="open" style="display:none"><img src="/images/hcm/themes/<%=themes %>/icon/icon7.png" /></a>
            </div>
        </div>
   </div>
</div>
 -->
            <!-- 导航菜单更多弹出层
 <div class="navAreaopen" id="menuid">
 <div class="opnav">
            <a href="javascript:void(0);"  onclick="showhidden('menuid','none');" class="close"><img src="/images/hcm/themes/<%=themes %>/icon/icon7_hover.png" /></a>
            <ul>
                <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav1.png"/>组织结构</a></li>
                <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav2.png"/>能力素质</a></li>
                <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav3.png"/>员工管理</a></li>
                <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav4.png"/>招聘管理</a></li>
                <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav5.png"/>人事异动</a></li>
                <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav1.png"/>组织结构</a></li>
                <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav2.png"/>能力素质</a></li>
                <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav3.png"/>员工管理</a></li>
                <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav4.png"/>招聘管理</a></li>
                <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav5.png"/>人事异动</a></li>
                <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav3.png"/>员工管理</a></li>
                <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav4.png"/>招聘管理</a></li>
                <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav5.png"/>人事异动</a></li>
                <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav4.png"/>招聘管理</a></li>
                <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav5.png"/>人事异动</a></li>
                <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav3.png"/>员工管理</a></li>
                <li><a href="javascript:void(0);" onclick="showhidden('menuid','none');"><img class="png" src="/images/hcm/themes/<%=themes %>/nav/nav4.png"/>招聘管理</a></li>
            </ul>
  </div>
  </div>
 -->
            <hrms:hcmmenu menu_id="" target="il_menu" max_menu="5" themes="<%=themes %>"></hrms:hcmmenu>


            <!-- 样式皮肤按钮 -->
            <hrms:themesbar />

            <div class="main_content_border" style="z-index: 999;">
                <span class="shadow"></span>
                <iframe src="" name="i_body"  id="center_iframe" scrolling="auto" width="100%" frameborder="0" style="z-index:999; "></iframe>
            </div>
</form>

<script language="JavaScript">
    Ext.BLANK_IMAGE_URL="../../ext/resources/images/default/s.gif";
    Ext.useShims = true;
    Ext.onReady(function()
    {
        function clickHandler(item, e) {
            window.open(item.href,item.hrefTarget);
        }

        menu_name = $URL.encode(menu_name);
        allname = $URL.encode(allname);
        /*如果showRoot=true，默认打开没有左菜单栏的template guodd 2016-07-14*/
        if(showRoot)
            document.getElementById("center_iframe").src="/templates/index/hcm_mainpanel.do?b_open=link&module="+mod_id+"&center_url="+centerurl+"&autoLoad=true";
        else
            document.getElementById("center_iframe").src="/templates/index/hcm_mainpanel.do?b_query=link&module="+mod_id+"&menu_id="+menu_id+"&menu_name="+menu_name+"&allname="+allname+"&first_center_url="+centerurl+"&first_center_target="+centertarget+"&autoLoad=true";
        <%if("1".equals(session.getAttribute("isSSO"))){// 单点登录进来才检查 %>
        AxManager.checkBrowserSettings('<%=url_p%>');
        <%}%>
    });

    <%if(view_time!=null&&view_time.equals("true")){ %>
    reloop();
    <%}%>

    function ccc(obj){
        obj.style.width="19px";
        obj.style.height="19px";
    }
    function cccc(obj){
        obj.style.width="18px";
        obj.style.height="18px";
    }
    function queryperson(e){
        e=e?e:(window.event?window.event:null);
        if (e.keyCode==13||e==1){
            var selectname=document.getElementById("selectname").value;
            document.getElementById("selectname").value="";
            selectname=$URL.encode(getEncodeStr(selectname));
            if(selectname == null || selectname == ""){
                alert(selectmessage);
                return false;
            }
            sysForm.target="il_body";
            sysForm.action="/workbench/query/query_interface1.do?b_queryperson=link&selectname="+selectname+"&path=";
            sysForm.submit();
        }
    }

    $(window).resize(function (){
        setHight();
        setNavWidth();
        showhidden('navid','none');
        showhidden('menuid','none');
    });
    function setHight() {
        var obj = document.getElementById("center_iframe");
        var bodyHeight = $(window).height();
        var bodyWidth = document.body.offsetWidth;
        obj.height = bodyHeight - 69;
        obj.width = bodyWidth;
    }
    setHight();
    function setNavWidth(){
        var totalW = $(window).width();
        var tWidth = 550;
        //$(".opnav").width(totalW-tWidth);
        //$(".opnav").width(560);
        var remainW = totalW-tWidth;
        var openremainW = totalW-tWidth;
        //document.getElementById('searchid').value=totalW;
        showhidden('showmoreid','block');
        if(remainW>652&&menuNum>=6){
            remainW=622;
            openremainW=622;
            if(menuNum==6){
                showhidden('showmoreid','none');
                showhidden('menuli'+menuNum,'none');
            }
        }else if(/*remainW>443&&*/menuNum>=5){
            remainW = 503;
            openremainW = 503;
            if(menuNum==5){
                showhidden('showmoreid','none');
                showhidden('menuli'+menuNum,'none');
            }
        }/*else{
        remainW = 360;
        openremainW = 360;
        if(menuNum==4){
            showhidden('showmoreid','none');
            showhidden('menuli'+menuNum,'none');
        }
    }*/
        if(menuNum==4){
            remainW = 400;
            openremainW = 400;
            if(menuNum==4){
                showhidden('showmoreid','none');
                showhidden('menuli'+menuNum,'none');
            }
        }
        if(/*remainW>280&&*/menuNum==3){
            remainW = 300;
            showhidden('showmoreid','none');
            showhidden('menuli'+menuNum,'none');
        }else if(menuNum==2){
            remainW = 200;
            showhidden('showmoreid','none');
            showhidden('menuli'+menuNum,'none');
        }else if(menuNum==1){
            showhidden('showmoreid','none');
            //$(".midnav").css("display","none");
            showhidden('menuli'+menuNum,'none');
            remainW = 115;
        }else if(menuNum==0){
            showhidden('showmoreid','none');
        }

        if(menuNum!=1 || showRoot){
            $(".midnav").width(remainW);

            if($.browser.msie) {
                $(".midnav").css("marginLeft",-(remainW/2));
            }
        }else {
            /*if($.browser.msie) {
             $(".midnav").css({position: "absolute",'top':0,'left':450,'z-index':2});
            }else{
                $(".midnav").css({position: "absolute",'top':0,'left':230,'z-index':2});
            }*/
            $(".midnav").css({display: "none"});
        }
        $(".opnav").width(openremainW);
        $(".opIframe").width(openremainW);
        //alert(totalW);
        if(totalW<995){
            if($.browser.msie) {
                $(".opnav").css("marginLeft",0);
                $(".opnav").css("left",280);//327
            }else{
                $(".opnav").css("marginLeft",0);
                $(".opnav").css("left",275);
            }
            if($.browser.msie) {
                $(".opIframe").css("marginLeft",0);
                $(".opIframe").css("left",280);//327
            }else{
                $(".opIframe").css("marginLeft",0);
                $(".opIframe").css("left",275);//317
            }
        }else{
            $(".opnav").css("left",'50%');
            if($.browser.msie) {
                $(".opnav").css("marginLeft",-(openremainW/2+15));
            }else{
                $(".opnav").css("marginLeft",-(openremainW/2));
            }
            $(".opIframe").css("left",'50%');
            if($.browser.msie) {
                $(".opIframe").css("marginLeft",-(openremainW/2-10));
            }else{
                $(".opIframe").css("marginLeft",-(openremainW/2));
            }
        }

        var nums = 5;//每行显示菜单按钮个数
        if(remainW==532)
            nums=6;
        /*if(totalW<1026)
            nums = 4;*/
        var hmum = menuNum/nums;
        hmum =  Math.floor(hmum);
        if(menuNum%nums!=0)
            hmum++;
        if(hmum==5)
            hmum=hmum*71-2;
        if(hmum==4)
            hmum=hmum*71-1;
        if(hmum==3)
            hmum=hmum*71;
        if(hmum==2)
            hmum=hmum*71+1;
        $(".opIframe").height(hmum);


    }

    setNavWidth();

    function mouse_out(id){
        var _this = this, obj = document.getElementById(id);
        this.in_dom = function(mObj,nObj){
            if(!mObj)return false;

            if (mObj==nObj) return true;
            else if (!mObj.parentNode) return false;
            else if (mObj.parentNode==nObj) return true;
            else return _this.in_dom(mObj.parentNode,nObj);
        };
        if(obj){

            obj.onmouseout = function(event){
                var e = arguments[0]||window.event;
                var x = e.relatedTarget||e.toElement; // 鼠标滑出的目标元素
                if (!_this.in_dom(x,obj)){
                    //alert("移出来了");
                    showhidden(id,'none');
                }
            }
        }
    }

    new mouse_out("menuid");
    new mouse_out("navid");
    new mouse_out("themesid");
</script>
<div id='axc' style='display:none'/>
</body>
</html>