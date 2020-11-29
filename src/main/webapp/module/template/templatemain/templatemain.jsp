<%@page import="com.hjsj.hrms.interfaces.webservice.SysoutSyncInterf"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.hrms.frame.utility.IDGenerator" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title></title>
    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="this is my page">
	<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  	<!-- 人事异动必引入文件-->
	<script language="JavaScript" src="../../../module/utils/js/template.js"></script>	
    <script language='JavaScript' src='../../../components/tableFactory/tableFactory.js'></script>
	<script language="JavaScript" src="../../../module/template/utils/template_util.js"></script>
	<script type="text/javascript">
        //模板所需参数
 		var templateBean={};
		var url = window.location.search;
		//解析url的参数，将参数赋给到templateBean
		parseUrl(url);		

		 /**
		  * 解析url传的参数 
		  * */
	    function parseUrl(url) {  
			if (url.indexOf('encryptParam')>0){//加密参数,需要后台解析				
				var map = new HashMap();
				map.put("url",url);
			    Rpc({functionId:'MB00001002',async:false,success: parseUrlOK},map);			
			}
			else {
				var params= getRequest(url);
				Ext.apply(templateBean,params);
				if (templateBean.other_param && templateBean.other_param!=""){
		    		var other_value= getDecodeStr(templateBean.other_param);
		    		templateBean.other_param=other_value;
	    		}
			}
	    }
		 /**
		  * 赋值后台传回的参数
		  * */
	    function parseUrlOK(form) {  
	    	var result = Ext.decode(form.responseText);
	    	Ext.apply(templateBean,result);
			if (templateBean.other_param && templateBean.other_param!=""){
	    		var other_value= getDecodeStr(templateBean.other_param);
	    		templateBean.other_param=other_value;
    		}
	    }

        if(templateBean.fillInfo=='1'){
        	<% 
        		Connection con=null;
		        Random random = new Random();
		        int s = random.nextInt(1000);
		        Calendar c = Calendar.getInstance();
				int year = c.get(Calendar.YEAR); 
				int month = c.get(Calendar.MONTH)+1; 
				int date = c.get(Calendar.DATE); 
				int hour = c.get(Calendar.HOUR_OF_DAY); 
        		String username = "临时人员_"+year +month +date+"_"+hour+s+""; 
        		//临时人员_20161230_00~24+三位随机数
        		UserView userView = new UserView(username,con);
        		//A0100:idg.getId("rsbd.a0100")、nbase:表单提交人员库
        		IDGenerator idg = new IDGenerator(2, con);
                String a0100 = idg.getId("rsbd.a0100");
                StringBuilder a0100Builder = new StringBuilder(a0100);
                int length = a0100.length();
                if(length>=8){
                	a0100 = a0100Builder.replace(0, 1, "L").toString();
                }else{
                	a0100 = "L"+a0100;
                }
        		userView.setA0100(a0100);
        		userView.setDbname("Usr");
        		userView.setVersion(70);
        		userView.reSetResourceMx(request.getParameter("tab_id"),7);
        		StringBuffer  sb = new StringBuffer();
        		sb.append(",010703,010705,010704,010709,010710,");
        		userView.setFuncpriv(sb);
        		String serverurl = request.getRequestURL().substring(0,request.getRequestURL().indexOf("/module"));
        		userView.setServerurl(serverurl);
        		HashMap hm = new HashMap();
        		hm.put("fillInfo", "1");//标志是外部链接进入
        		userView.setHm(hm);
        		session.setAttribute("islogon",true);
        		session.setAttribute("userView",userView);
        	 %>
        }
        templateBean.callBack_init="renderForm";
        Ext.onReady(function(){
            //调用template_util.js的方法
            createTemplateForm(templateBean);
        });
        
        
         /**
          * 渲染主页面
          * */
	    function renderForm() {  
            Ext.create('Ext.container.Viewport',{
                autoScroll:false,
                style:'backgroundColor:white',
                layout:'fit',
                items:templateMain_me.mainPanel
            });
	    }
	    
		
 	</script>
  <body>
   
  </body>
</html>
