<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.hrms.frame.utility.IDGenerator" %>
<%@ page import="java.lang.reflect.Field" %>
<%@ page import="java.lang.Exception" %>

<%
    Connection con=null;
    Random random = new Random();
    int s = random.nextInt(1000);
    Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR); 
    int month = c.get(Calendar.MONTH)+1; 
    int date = c.get(Calendar.DATE); 
    int hour = c.get(Calendar.HOUR_OF_DAY); 
    String username = "二维码进入"; 
    //临时人员_20161230_00~24+三位随机数
    UserView userView = new UserView(username,con);
    //A0100:idg.getId("rsbd.a0100")、nbase:表单提交人员库
    IDGenerator idg = new IDGenerator(2, con);
    String a0100 = idg.getId("rsbd.a0100")+"-0";//外部访问 人员编号 加 -0 区分
    userView.setA0100(a0100);
    userView.setDbname("Usr");
    userView.setVersion(70);
    userView.reSetResourceMx(request.getParameter("tab_id"),7);
    StringBuffer  sb = new StringBuffer();
    sb.append(",010703,010705,010704,010709,010710,");
    userView.setFuncpriv(sb);
    String serverurl = request.getRequestURL().substring(0,request.getRequestURL().indexOf("/system"));
    userView.setServerurl(serverurl);
    HashMap hm = new HashMap();
    hm.put("fillInfo", "1");//标志是外部链接进入
    userView.setHm(hm);
    session.setAttribute("islogon",true);
    /*
    try {
    	Field field = userView.getClass().getDeclaredField("status");
		field.setAccessible(true);
		field.set(userView,4);
	}catch (Exception e) {
		e.printStackTrace();
	}*/	
    session.setAttribute("userView",userView);
    
    String qrid = request.getParameter("qrid");
    qrid = qrid==null? "":qrid;
%> 
<!DOCTYPE html>
<html>
<head>
<title>入职登记</title>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
 <meta name="viewport" content="initial-scale=1, maximum-scale=1, minimum-scale=1">
<script type="text/javascript" src="../../../../../jquery/jquery-3.3.1.min.js"></script>
<!-- 加载 touch js文件 -->
<script type="text/javascript" src="../../../../../ext/touch/sencha-touch-all.js"></script>
<!-- 加载 touch css文件 -->
<link rel="stylesheet" href="../../../../../ext/touch/resources/css/cupertino.css" type="text/css">
<link rel="stylesheet" href="../css/style.css" type="text/css">
<!-- 加载 ajax js文件 -->
<script type="text/javascript" src="../../../../../ext/rpc_command.js"></script>
<link rel="stylesheet" href="../../../../components/mobleTemplate/css/codeselector.css" type="text/css">
<script>
	var a0100 = '<%=a0100%>';
	
    appMap=new HashMap();//全局对象，用于存储应用生命周期内的参数，例如panel间跳转参数传输
    Ext.Loader.setPath('EHR','../../../../components'); 
    Ext.Loader.setConfig({
        scriptCharset:'UTF-8'
    });
    //需要在application没装载之前修改
    Ext.MessageBox.OK.text = '确定';
    Ext.MessageBox.YES.text = '确定';
    Ext.MessageBox.NO.text = '取消';
    Ext.MessageBox.CANCEL.text = '取消';
    Ext.apply(Ext.MessageBox, {
        YESNO: [Ext.MessageBox.YES, Ext.MessageBox.NO]
    });
   /*  Ext.each(Ext.MessageBox.OKCANCEL, function(item, index, allItems) {
        if ('cancel' == item.itemId) {
            item.text = '取消';
        } else if ('ok' == item.itemId) {
            item.text = '是';
        }
    });
    Ext.each(Ext.MessageBox.YESNOCANCEL, function(item, index, allItems) {
        if ('cancel' == item.itemId) {
            item.text = '取消';
        } else if ('ok' == item.itemId) {
            item.text = '是';
        } else {
            item.text = '否';
        }
    });
    Ext.each(Ext.MessageBox.YESNO, function(item, index, allItems) {
        if ('ok' == item.itemId) {
            item.text = '是';
        } else {
            item.text = '否';
        }
    }); */
    Ext.application({
        name:'QRCard',
        appFloder:'/module/system/qrcard/mobilewrite',
        requires:['EHR.mobleTemplate.Template'],
        views:['qrcardmain','qrcardinfo','SuperDateTimePicker','SuperDateTimePickerField','EHR.mobleTemplate.TemplateForm'],
        launch:function(){
            var mainview = Ext.create('QRCard.view.qrcardmain',{
            	qrid:'<%=qrid%>'
            	,a0100:a0100
            	}
            );
            Ext.Viewport.add(mainview);
        }
    });
</script>
<style type="text/css">
.x-form .x-scroll-container{
    background:none;
}
/*  .template_label{
padding:14px 6px 14px 6px;
}  */
.template_label_area{
padding:5px 6px 15px 6px;
}
</style>
</head>
<body>

</body>
<!-- <script type="text/javascript">
document.body.addEventListener('touchmove', function (e) {
	e.preventDefault(); //阻止默认的处理方式(阻止下拉滑动的效果)
	}, {passive: false}
); //passive 参数不能省略，用来兼容ios和android
</script> -->
<script type="text/javascript">
/*微信浏览器特殊处理*/
	//if(window.navigator.userAgent.toLowerCase().match(/MicroMessenger/i) == 'micromessenger'){
    	document.body.style='overflow-y: hidden;height: 100%;position: fixed; ';
	//}
</script>
</html>