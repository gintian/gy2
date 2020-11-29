<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,java.text.SimpleDateFormat,java.util.Date"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/js/validateDate.js"></script>
<script type="text/javascript">
<!--
	function clickok(){
        if(confirm("确定初始化历史时点信息？")) {
            var in_paramters = "start_date=" + document.forms[0].start_date.value + "&end_date=" + document.forms[0].end_date.value;
            var request = new Request({
                method: 'post',
                asynchronous: false,
                parameters: in_paramters,
                onSuccess: showlist,
                functionId: '16010000032'
            });
        }
	}
	function showlist(outparamters){
	   var msg=outparamters.getValue("msg");
	   if(msg==1){
           //用window.open后的回调的方法修改  wangbs 2019年3月12日17:04:30
           window.opener.initHistoryReturn(msg);
           window.close();
		}else{
			alert("历史时点初始化失败！");
		}
	}
//-->
</script>
<html:form action="/org/orginfo/searchorgtree"> 
<div class="fixedDiv3">
  <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0"  class="framestyle2">   
	 <tr align="center">
		<td valign="top" class="TableRow" colspan="2" style="line-height: 30px;border-left:0px;border-top:0px;border-right:0px;">
		  &nbsp;历史时点初始化&nbsp;
		</td>
	 </tr>         
           	  
         <%
	             	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		            String date = sdf.format(new Date());
              %>
              <tr height="10">
            	<td colspan="2">&nbsp;</td>
            </tr>
             <tr  align="center" class="list3">
                <td align="right">
                   &nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="conlumn.codeitemid.start_date"/>:&nbsp;
                </td>
               <td align="left"> 
               <input type="text" name="start_date" value="<%=date %>" class="text4" maxlength="50" style="BACKGROUND-COLOR:#F8F8F8;width:150px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'有效日期起')) {this.focus(); this.value='<%=date %>'; }"/>
               </td>
             </tr>
            
             <tr  align="center" class="list3" style="padding-top:5px;">
                <td align="right">
                   &nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="conlumn.codeitemid.end_date"/>:&nbsp;
                </td>
               <td align="left"> 
                  <input type="text" name="end_date" value="9999-12-31" class="text4" maxlength="50" style="BACKGROUND-COLOR:#F8F8F8;width:150px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'有效日期止')) {this.focus(); this.value='9999-12-31'; }"/>
               </td>
             </tr>  
            <tr height="10">
            	<td colspan="2">&nbsp;</td>
            </tr>
            <tr height="100">
            	<td colspan="2">&nbsp;</td>
            </tr>
      </table>  
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="padding-top:5px;"> 
    <tr>
            <td align="center" colspan="2">
         	 <html:button styleClass="mybutton" property="" onclick="clickok();">
            		<bean:message key="button.ok"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="" onclick="window.close();">
            		<bean:message key="button.cancel"/>
	 	    </html:button>  
            </td>         
         </tr>        
   </table>
</div>
</html:form>
