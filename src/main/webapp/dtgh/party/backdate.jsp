<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.WebConstant,java.text.SimpleDateFormat,java.util.Date"%>

<hrms:themes/>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/js/validateDate.js"></script>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript">
<!--
	function clickok(){
        // var backdate="";
        // backdate=document.getElementsByName("anydate")[0].value;
        // returnValue=backdate;
        // window.close();

	    //去父级页面获取弹窗对象，并返回数据  wangbs 2019年3月6日14:26:30
	    window.opener.openHistoryReturn(document.getElementsByName("anydate")[0].value);
	    window.close();
    }
    //点击取消按钮事件 wangbs 2019年3月6日14:29:50
    function historyWinClose(){
        window.close();
	}

//-->
</script>
<div class="fixedDiv3">
  <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0"  class="ListTable"> 
  <tr >  <td colspan="2" class="framestyle1"><table  width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
	 <tr align="center">
		<td valign="middle" class="TableRow" style="border-left: 0px; border-right: 0px;">
		  &nbsp;历史时点查询&nbsp;
		</td>
	 </tr>         
           	  
         <%
	             	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		            String date = sdf.format(new Date());
              %>
              <tr height="10">
            	<td>&nbsp;</td>
            </tr>
             
             <tr height="20">
            	<td>&nbsp;</td>
            </tr>
             <tr  align="center" class="list3">
               <td> 
                  历史时点&nbsp;<input type="text" name="anydate" value="<%=date %>" maxlength="50" class="text4" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'时间点')) {this.focus(); this.value='<%=date %>'; }"/>
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
    <tr>
            <td align="center">
         	 <html:button styleClass="mybutton" property="" onclick="clickok();">
            		<bean:message key="button.ok"/>
	 	    </html:button>
            <%--window.close()关闭不掉Ext.Window  wangbs 2019年3月6日15:30:15--%>
            <%--<html:button styleClass="mybutton" property="" onclick="window.close();">--%>
         	<html:button styleClass="mybutton" property="" onclick="historyWinClose();">
            		<bean:message key="button.cancel"/>
	 	    </html:button>  
            </td>         
         </tr>        
   </table>
</div>