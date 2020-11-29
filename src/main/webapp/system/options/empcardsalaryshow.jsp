<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.actionform.sys.options.EmpCardSalaryShowForm"%>
<%
	EmpCardSalaryShowForm cssf = (EmpCardSalaryShowForm)session.getAttribute("empCardSalaryShowForm");
	String a0100 = cssf.getA0100();
	String flag = cssf.getFlag();
	String pre = cssf.getPre();
        String b0110=cssf.getB0110();  
        String recardconstant=cssf.getRecardconstant();     
	String recordUrl ="/module/card/cardCommonSearch.jsp?inforkind=7"; //"/ykcard/employeeselfcard.do?b_card=infoself&userbase="+pre+"&flag="+flag+"&b0110="+b0110+"&pre="+pre;
	String tableUrl = "/system/options/salaryinfo.do?b_search=link&a0100="+a0100+"&pre="+pre+"&isMobile=0&prv_flag="+flag;
	String musterUrl="/general/muster/hmuster/executeStipendHmuster.do?b_query=link&groupCount=0&a0100="+a0100+"&musterFlag=allInfo&isInit=init&dbpre="+pre+"&flag="+flag;;
        String rd_url="";       
        if(recardconstant.equals("0"))
        {
           rd_url=tableUrl;
        }else if(recardconstant.equals("1"))
        {
           rd_url=musterUrl;
        }
     UserView userView=(UserView)session.getAttribute(WebConstant.userView);
     boolean cardFlag=userView.hasTheFunction("01020103");//表格方式
     boolean musterFlag=userView.hasTheFunction("01020102");//列表方式
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
		<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
	</head>
	<script>
		 var cardFlag=<%=cardFlag%>;
		 var musterFlag=<%=musterFlag%>;
		
		//浏览器兼容，使用Ext guodd 2017-11-07
	     Ext.onReady(function(){
	    	 	Ext.widget('viewport',{
	    	 		layout:'fit',
	    	 		items:{
	    	 			xtype:'tabpanel',
	    	 			id:'cardTabPanel',
	    	 			margin:'10 0 0 0'
	    	 		}
	    	 	});
	    	if(cardFlag){
	    	 Ext.getCmp("cardTabPanel").add({
	 				title:'表格方式',
	 				bodyStyle:'border-top:0',
	 				html:'<iframe src="<%=recordUrl%>" width="100%" height="100%" frameborder=0 />'
	 			});	
	    	 Ext.getCmp("cardTabPanel").setActiveTab(0);//默认选择状态  		
	    	} 	
	    	if(musterFlag)
	    	 Ext.getCmp("cardTabPanel").add({
	 				title:'列表方式',
	 				bodyStyle:'border-top:0',
	 				html:'<iframe src="<%=rd_url%>" width="100%" height="100%" frameborder=0 />'
	 			});	
	    	 
	     });
	</script>
</html>
