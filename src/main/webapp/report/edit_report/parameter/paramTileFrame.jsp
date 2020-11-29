<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%  
	String url_1="/report/edit_report/parameter.do?b_query=query&tabid="+request.getParameter("tabid")+"&status="+request.getParameter("status")+"&operateObject="+request.getParameter("operateObject")+"&code="+request.getParameter("code")+"&unitcode="+request.getParameter("unitcode")+"&paramscope=0";
	String url_2="/report/edit_report/parameter.do?b_query=query&tabid="+request.getParameter("tabid")+"&status="+request.getParameter("status")+"&operateObject="+request.getParameter("operateObject")+"&code="+request.getParameter("code")+"&unitcode="+request.getParameter("unitcode")+"&paramscope=1";
 %>

<html>
  <script language="JavaScript"src="../../../module/utils/js/template.js"></script>
  <script language='javascript'>
  	var message = "";
	var b=false;
    var url = "";
    var operateObject ='<%=(request.getParameter("operateObject"))%>';
    var obj1 ='<%=(request.getParameter("obj1"))%>';
    var username ='<%=(request.getParameter("username"))%>';
    if (operateObject == 1) {
    	if(obj1== 1){
    		url = '/report/edit_report/reportSettree.do?b_query=link&code=${parameterForm.tabid}&status=${parameterForm.status}&operateObject=1&obj1=<%=(request.getParameter("obj1"))%>';
    	}else if(obj1== 2){
    		url = '/report/edit_report/reportSettree.do?b_query=link&code=${parameterForm.tabid}&status=${parameterForm.status}&operateObject=1';
    	}else{
    		url = '/report/edit_report/reportSettree.do?b_query=link&code=${parameterForm.tabid}&status=${parameterForm.status}&operateObject=1';
    	}
        
    } else {
        url = '/report/report_collect/reportOrgCollecttree.do?b_query=link&a_code=<%=request.getParameter("unitcode")%>&operateObject=2'; 
    }
    var img=Ext.widget('image',{
		xtype:'image',
		title:'<bean:message key="button.return" />',
		height:17,
		width:17,
		border:0,
		style:'cursor:pointer;margin-right:10px',
		src:'/module/serviceclient/images/close_mouseover.png',
		listeners:{
		    click:{
		        element: 'el',
		        fn:function(){
		        	window.location.href=url;
		        }
		    }
		}
		
	});
    var panel=Ext.widget('panel',{
    		  layout:'fit',
    		  border:0,
    		  title:'编辑报表参数',
    		  tools:[img],
    		  items:{
    			xtype:'tabpanel',
  	 			id:'cardTabPanel',
  	 			height:'98%',
  	 			activeTab:0,
  	 			margin:'4 0 0 0',
  	 			items:[{title:'<bean:message key="edit_report.param.allParam" />',
  	 					//编辑报表参数宽度修改
  						html:'<iframe src="<%=url_1%>" width="98%" height="100%" frameborder=0 />'   
  						},{
  							title:'<bean:message key="edit_report.param.tsortParam" />',
  							html:'<iframe src="<%=url_2%>" width="98%" height="100%" frameborder=0 />'
  						}]
    		  }
    })
    
  	Ext.onReady(function(){
  		Ext.widget('viewport',{
	 		layout:'fit',
	 		style:'margin-top:-1px',
	 		items:[panel],
	 		renderTo : Ext.getBody()
  		});
	});
  </script>
  <body>
  </body>
</html>