<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo,java.sql.Connection,com.hrms.frame.utility.AdminDb,com.hrms.struts.valueobject.UserView,com.hjsj.hrms.actionform.sys.options.CardSalaryShowForm,com.hjsj.hrms.utils.PubFunc"%>
<%
    Connection conn =null;
conn = AdminDb.getConnection();
UserView userView = (UserView)session.getAttribute("userView");
CardSalaryShowForm cssf = (CardSalaryShowForm)session.getAttribute("cardSalaryShowForm");
String a0100 = cssf.getA0100();
String flag = cssf.getFlag();
String pre = cssf.getPre();
String view_photo = cssf.getView_photo();//liuy 2015-3-18 7976：自助服务/员工信息/员工薪酬，显示照片，怎么不是照片墙呢？另外每行的照片个数也不对。
String showFlag = cssf.getShowFlag();
String b0110=cssf.getB0110();
String payment=cssf.getPayment();
String recordUrl ="";
String rd_url="";
    try{
    
	
	if("infoself".equalsIgnoreCase(flag))
    {
		a0100 = userView.getA0100();
    }
    
    if(!"infoself".equalsIgnoreCase(flag)&&null != a0100 && !"".equals(a0100)&&!"A0100".equalsIgnoreCase(a0100)){
        CheckPrivSafeBo cps = new CheckPrivSafeBo(conn, userView);
        a0100 = cps.checkA0100("", pre, a0100, "");
    }
	
	recordUrl = "/module/card/cardCommonSearch.jsp?inforkind=7&a0100="+PubFunc.encrypt(pre+"`"+a0100)+"&fieldpriv=1";//"/workbench/ykcard/showykcardinfo.do?b_setpage=link&a0100="+a0100+"&flag="+flag+"&pre="+pre+"&b0110="+b0110;
	String tableUrl = "/system/options/salaryinfo.do?b_search=link&a0100="+a0100+"&pre="+pre+"&prv_flag="+flag;
	String musterUrl="/general/muster/hmuster/executeStipendHmuster.do?b_query=link&groupCount=0&a0100="+a0100+"&dbpre="+pre+"&musterFlag=allInfo&flag="+flag+"&payment="+payment;
	String recardconstant=cssf.getRecardconstant();  
       
        if(recardconstant.equals("0"))
        {
           rd_url=tableUrl;
        }else if(recardconstant.equals("1"))
        {
           rd_url=musterUrl;
        }
      //将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
        int index = recordUrl.indexOf("&");
        if(index>-1){
            String allurl = recordUrl.substring(0,index);
            String allparam = recordUrl.substring(index);
            recordUrl=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
        }
        
        index = rd_url.indexOf("&");
        if(index>-1){
            String allurl = rd_url.substring(0,index);
            String allparam = rd_url.substring(index);
            rd_url=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
        }
        //将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end    
    }catch(Exception e){
    	e.printStackTrace();
    }finally{
    	if(conn!=null)
    		try{
    		   conn.close();
    		}catch(Exception e){
    			
    		}
    }
    
   boolean cardFlag=false;
   if(userView.hasTheFunction("01020103")||userView.hasTheFunction("03020103")){
	   cardFlag=true;
   }
   boolean musterFlag=false;
   if(userView.hasTheFunction("01020102")||userView.hasTheFunction("03020102")){
	   musterFlag=true;
   }
%>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
		<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
	</head>
<script language="javaScript">
function back(re)
{
	if(re==2)
	{
	    document.cardSalaryShowForm.target="mil_body";
		document.cardSalaryShowForm.action="/workbench/ykcard/showykcardinfo.do?br_returnphoto=link";
		document.cardSalaryShowForm.submit();
	}else if(re==3){
		//【7731】自助服务-员工信息-员工薪酬页面的返回按钮，返回的位置不对  jingq add 2015.02.28
		top.open("/workbench/ykcard/showinfodata.do?b_search=link","mil_body");
	}else {
	    document.cardSalaryShowForm.target="mil_body";
		document.cardSalaryShowForm.action="/workbench/ykcard/showinfodata.jsp";
		document.cardSalaryShowForm.submit();
	}	
}

var cardFlag=<%=cardFlag%>;
var musterFlag=<%=musterFlag%>;
var view_photo="<%=view_photo%>";
var flag="<%=flag%>";
//浏览器兼容，使用Ext guodd 2017-11-07
Ext.onReady(function(){
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
		        	if(view_photo!=null&&view_photo==="photo"){
		 	        	back(2);
		 	        }else if(flag!=null&&flag==="noself"){
		 	        	back(3);
		 	        }else{
		 	        	back(1);
		 	        }
		        }
		    }
		}
		
	});
		var panel=Ext.widget('panel',{
			layout:'fit',
			title:'员工薪酬',
			border:0,
			tools:[img],
			items:{
				xtype:'tabpanel',
	 			id:'cardTabPanel',
	 			margin:'5 0 10 5'
			}
		})
	
	 	Ext.widget('viewport',{
	 		layout:'fit',
	 		style:'margin-top:-1px',
	 		items:[panel]
	 	});
	if(cardFlag){
	 Ext.getCmp("cardTabPanel").add({
			title:'表格方式',
			bodyStyle:'border-top:0',
			html:'<iframe src="<%=recordUrl%>"  width="100%" height="100%" frameborder=0 />'
		});	
	 Ext.getCmp("cardTabPanel").setActiveTab(0);//默认选择状态  		
	} 	
	if(musterFlag)
	 Ext.getCmp("cardTabPanel").add({
			title:'列表方式',
			style:'border-top:0',
			html:'<iframe src="<%=rd_url%>" width="100%" height="100%" frameborder=0 />'
		});	
	 
});
</script>
