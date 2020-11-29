<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
  <%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	  int i=0;
	    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<STYLE type=text/css>
.div2
{
 /*overflow:auto;*/ 
	width:100%;
 height: 300px;
 overflow:auto; 
 line-height:15px; 
 border-width:1px; 
 border-style: groove;
 border-width :thin ;
}
</STYLE>
<script language="javascript">
 function getOrg()
{
	 var orgID=root.getSelected();
	 if(orgID.indexOf("@K")!=-1)
	 {
	     alert("不能从职位开始输出!");
	     return false;
	 }
	 var hashvo=new ParameterSet();			
	 hashvo.setValue("orgid",orgID);
	 hashvo.setValue("time",'${orgInformationForm.backdate }');//backdate 没有设置历史时日就是今日日期  wangb 30188  20170814
	 var request=new Request({method:'post',asynchronous:false,onSuccess:showOut,functionId:'16010000025'},hashvo);
}
function showOut(outparamters)
{
     var url=outparamters.getValue("file");	     
	 if(url!="")
	  var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url,"excel");
	
}
//关闭ext弹窗方法   wangb 20190306
function winclose(){
   	var win = parent.Ext.getCmp('exportorgtree');
   	if(win)
   		win.close();
}
</script>
<hrms:themes></hrms:themes>
<html:form action="/org/orginfo/searchorglist">     
<div class="fixedDiv3">
<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0"  class="framestyle0">   
<!-- 	 <tr align="left">
		<td valign="middle" class="TableRow" style="border:0px;border-bottom: 1px solid;" nowrap >
		  &nbsp;<bean:message key="button.output.data"/>
		</td>
	 </tr>  -->        
         <tr height="25">        
           <td style="padding:5px;">
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="org.orginfo.info03"/>
           </td>
         </tr>   	  
         <tr>        
           <td align="left" class="RecordRow" style="border-left:0px;border-right:0px;border-bottom:0px;"> 
           <div id="tbl_container" class="div2" style="border:0px;" >
                 <hrms:orgtree flag="0" loadtype="0" showroot="false" selecttype="2" nmodule="4" dbtype="0" priv="1" isfilter="0"/>
           </div>
           </td>
         </tr>   
             
   </table>
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">  
   		<tr style="height: 35">
            <td align="center" >
         	 <html:button styleClass="mybutton" property="b_save" onclick="getOrg();">
            		<bean:message key="button.ok"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="br_return" onclick="winclose();">
            		<bean:message key="button.close"/>
	 	    </html:button>  
            </td>         
         </tr>    
   </table>
</div>
</html:form>
