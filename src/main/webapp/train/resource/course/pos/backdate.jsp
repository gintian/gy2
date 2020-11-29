<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hjsj.hrms.actionform.train.resource.course.CourseForm"%>
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
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/js/validateDate.js"></script>
<script type="text/javascript">
<!--
	function clickok(){
		backdate=document.forms[0].anydate.value;
		returnValue=backdate;
		window.close();
	}
	function changvalue(obj){
		if(obj.checked){
			document.forms[0].anydate.value=1;
		}else{
			document.forms[0].anydate.value=0;
		}
	}
//-->
</script>
<hrms:themes></hrms:themes>
<html:form action="/train/resource/course/pos"> 
  <table width="340px" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-right: 5px;margin-top: 5px;" > 
  <tr >  <td colspan="2" class="framestyle1"><table  width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
	 <tr align="center">
		<td valign="middle" class="TableRow noleft noright">
		  <logic:equal value="1" name="courseForm" property="validateflag">
		  		&nbsp;历史时点查询&nbsp;
		  </logic:equal>
		  <logic:notEqual value="1" name="courseForm" property="validateflag">
				&nbsp;查询设置&nbsp;
		  </logic:notEqual>
		</td>
	 </tr>         
           	  
         <%
         			CourseForm courseForm = (CourseForm)session.getAttribute("courseForm");
         			String backdate = courseForm.getBackdate();
	             	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		            String date = sdf.format(new Date());
		            date=backdate==null||backdate.length()==0?date:backdate;
              %>
              <tr height="10">
            	<td>&nbsp;</td>
            </tr>
             <tr height="20">
            	<td>&nbsp;</td>
            </tr>
             <tr  align="center" class="list3">
               <td> 
               <logic:equal value="1" name="courseForm" property="validateflag">
                  <input type="text" name="anydate" value="<%=date %>" maxlength="50" class="text4" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'时间点')) {this.focus(); this.value='<%=date %>'; }"/>
               </logic:equal>
               <logic:notEqual value="1" name="courseForm" property="validateflag">	
               		<input type="checkbox" onclick="changvalue(this);" <logic:equal value="1" name="courseForm" property="checked">checked="checked"</logic:equal> />
               		<input type=hidden name=anydate value="${courseForm.checked }" />
               		包含无效代码
               </logic:notEqual>	
               </td>
             </tr>  
            <tr height="10">
            	<td>&nbsp;</td>
            </tr>
            <tr height="100">
            	<td>&nbsp;</td>
            </tr>
            </table>
            </td>
            </tr>
      </table>  
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0"> 
   <tr height="20">
            	<td>&nbsp;</td>
    </tr>
    <tr>
            <td align="center">
         	 <html:button styleClass="mybutton" property="" onclick="clickok();">
            		<bean:message key="button.ok"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="" onclick="window.close();">
            		<bean:message key="button.cancel"/>
	 	    </html:button>  
            </td>         
         </tr>        
   </table>
</html:form>
