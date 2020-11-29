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
		var r=document.getElementsByName("radio1");
		var backdate="";
		var v="";
		for(i=0;i<r.length;i++){
			if(r[i].checked){
				v=r[i].value;
			}
		}
		if(v==1){//归档时间点
			backdate=document.forms[0].backdate.value;
		}else if(v==0){//任何时间点
			backdate=document.forms[0].anydate.value;
		}
		window.opener.openHistoryReturn(backdate);
		window.close();
	}
	function changdate(v){
		var archivedate=document.getElementById('archivedate');
		var anydate=document.getElementById('anydate');
		if(v==1){//归档时间点
			archivedate.style.display="block";
			anydate.style.display="none";
		}else if(v==0){//任何时间点
			anydate.style.display="block";
			archivedate.style.display="none";
		}
		
	}
//-->
</script>
<html:form action="/org/orginfo/searchorgtree">
<div class="fixedDiv3">
  <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0"> 
     <tr>  <td colspan="2" class="framestyle1"><table  width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
	 <tr align="center">
		<td valign="top" class="TableRow" style="border-right:0px;border-left:0px;">
		 <span style="text-align: center;line-height: 30px;"> &nbsp;历史时点查询&nbsp;</span>
		</td>
	 </tr>         
           	  
         <%
	             	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		            String date = sdf.format(new Date());
              %>
              <tr height="10">
            	<td>&nbsp;</td>
            </tr>
             <tr  align="center" class="list3">
                <td >
                   <input type="radio" name="radio1" id="radio1" checked="checked" value="0" onclick="changdate(this.value);" />任何时间
               &nbsp;
               	<input type="radio" name="radio1" id="radio2" onclick="changdate(this.value);" value="1" />归档时间
               </td>
             </tr>
             <tr height="20">
            	<td>&nbsp;</td>
            </tr>
             <tr  align="center" class="list3">
               <td> 
               <span id="anydate" name="anydate" style="display:block">
                  <input type="text" name="anydate" value="<%=date %>" maxlength="50" 
                  style="width:150px" extra="editor" dropDown="dropDownDate" 
                  onchange="if(!validate(this,'时间点')) {this.focus(); this.value='<%=date %>'; }"/>
               </span>
               <span  id="archivedate" name="archivedate" style="display:none">
                  <hrms:optioncollection name="orgInformationForm" property="archivedatelist" collection="list1" />
					<html:select name="orgInformationForm" property="backdate" style="BACKGROUND-COLOR:#F8F8F8;border: 1pt solid #A8C4EC;width:150px">
						<html:options collection="list1" property="dataValue" labelProperty="dataName"/>
					</html:select>
               </span>
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
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top: 5px;"> 
    <tr >
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
</div>
</html:form>
