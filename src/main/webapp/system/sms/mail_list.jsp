<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.hrms.struts.constant.WebConstant,java.text.SimpleDateFormat,java.util.Date"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes></hrms:themes>
<script type="text/javascript" src="/js/validateDate.js"></script>
<%
int i=0;
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
String date = sdf.format(new Date());
%>
<script language="javascript">
   function change()
   {
      mailListForm.action="/system/sms/mail_list.do?b_query=link";
      mailListForm.submit();
   }
   function openwin(nbase,a0100)
   {
   	  var url = "/general/inform/synthesisbrowse/mycard.do?b_mysearch=link&userbase="+nbase+"&a0100="+a0100+"&inforkind=1&tabid=${mailListForm.tabid}&npage=1&userpriv=selfinfo&flick=1&multi_cards=-1&flick=1";//&multi_cards=-1"; 不显示显示登记列表
   	  window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+(screen.availHeight-100)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
   }
</script>

<html:form action="/system/sms/mail_list">
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
	<tr height="30px">
	  <td align="left" class="" nowrap colspan="7">
	  	<html:select name="mailListForm" property="state" size="1" onchange="change();" >
	       <html:option  value="-1"><bean:message key="system.sms.nsend"/></html:option>
	       <html:option  value="0"><bean:message key="lable.performance.lost"/></html:option>
	       <html:option  value="1"><bean:message key="system.sms.ysend"/></html:option>
	   </html:select> 
	   &nbsp;<bean:message key="label.from1"/>
				<input type="text" name="startime" value='<bean:write name="mailListForm" property="startime"/>'  extra="editor" style="width:100px;font-size:10pt;text-align:left;" class="complex_border_color inputtext" id="editor1"  dropDown="dropDownDate" onchange="if(!validate(this,'开始时间')) {this.focus(); this.value='<%=date %>'; }">
				<bean:message key="kq.init.tand"/>
				<input type="text" name="endtime" value='<bean:write name="mailListForm" property="endtime"/>'  extra="editor" style="width:100px;font-size:10pt;text-align:left;" class="complex_border_color inputtext" id="editor1"  dropDown="dropDownDate" onchange="if(!validate(this,'结束时间')) {this.focus(); this.value='<%=date %>'; }">	
				&nbsp;<hrms:submit styleClass="mybutton" property="b_query"><bean:message key="button.query"/></hrms:submit>
	   </td> 	
	</tr>
    <tr> 
      <td align="center" width="25" class="TableRow" nowrap><input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'></td>
      <td align="center" class="TableRow" nowrap><bean:message key="system.sms.sman"/></td>	  
      <td align="center" class="TableRow" nowrap><bean:message key="system.sms.aman"/></td>
      <td align="center" class="TableRow" nowrap><bean:message key="system.sms.mobimun"/></td>
      <td align="center" class="TableRow" nowrap><bean:message key="conlumn.board.content"/></td>	 
      <td align="center" class="TableRow" nowrap><bean:message key="system.sms.stime"/></td>	   
      <td align="center" class="TableRow" nowrap><bean:message key="system.sms.scount"/></td>	 	  
    </tr>

   <hrms:paginationdb id="element" name="mailListForm" sql_str="mailListForm.str"  table=""  where_str="mailListForm.where" columns="${mailListForm.conum}"  page_id="pagination" order_by="${mailListForm.order}"  pagerows="15" indexes="indexes">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>
          <%
          LazyDynaBean bean=(LazyDynaBean)pageContext.getAttribute("element");
          String a0100_encrypt=(String)bean.get("a0100");
          a0100_encrypt=PubFunc.encrypt(a0100_encrypt);
          pageContext.setAttribute("a0100_encrypt", a0100_encrypt);
          %>  
             <td align="center" class="RecordRow" nowrap>
              <hrms:checkmultibox name="mailListForm" property="pagination.select" value="true"    indexes="indexes"/>
             </td>            
             <td align="left" class="RecordRow" nowrap>
				   <logic:equal value="" name="element" property="a0100" >
                   	&nbsp;<bean:write  name="element" property="sender" filter="true"/>&nbsp;
                   </logic:equal>
                   <logic:notEqual value="" name="element" property="a0100">
                   	&nbsp;<a href="###" onclick="openwin('<bean:write  name="element" property="nbase" filter="true"/>','${a0100_encrypt}')">
                   		<bean:write  name="element" property="sender" filter="true"/>
                   	</a>&nbsp;
                   </logic:notEqual>
             </td>                          	    
      	     <td align="left" class="RecordRow" nowrap>              
                   &nbsp;<bean:write  name="element" property="receiver" filter="true"/>&nbsp;
             </td> 
              <td align="left" class="RecordRow" nowrap>              
                   &nbsp;<bean:write  name="element" property="mobile_no" filter="true"/>&nbsp;
             </td> 
              <td align="left" class="RecordRow">              
                   &nbsp;<bean:write  name="element" property="msg" filter="true" />&nbsp;
             </td> 
             <td align="left" class="RecordRow" nowrap>              
                  &nbsp; <bean:write  name="element" property="send_time" filter="true"/>&nbsp;
             </td>
             <td align="left" class="RecordRow" nowrap>              
                   &nbsp;<bean:write  name="element" property="sended_count" filter="true"/>&nbsp;
             </td>                        	    
      	  
          </tr>
        </hrms:paginationdb> 

  </table>

<table  width="90%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	        <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="mailListForm" property="pagination" nameId="mailListForm" scope="page">
				</hrms:paginationdblink></p>
			</td>
		</tr>
</table> 

<table cellpadding="0" cellspacing="0" width="90%" align="center">
       <tr height="35px;">
          <td align="center">
         	 <hrms:submit styleClass="mybutton" property="b_all_delete" onclick="document.mailListForm.target='_self';return ifdelall()&&document.returnValue;"><bean:message key="system.sms.alle"/></hrms:submit>
         	 <hrms:submit styleClass="mybutton" property="b_delete" onclick="document.mailListForm.target='_self';return ifmsdel()&&document.returnValue;"><bean:message key="button.delete"/></hrms:submit>
         	 <hrms:submit styleClass="mybutton" property="b_reset" onclick="return if_resend();"><bean:message key="system.sms.reset"/></hrms:submit>
          </td>
        </tr>          
</table>

</html:form>

