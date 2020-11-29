<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.hrms.struts.constant.WebConstant,java.text.SimpleDateFormat,java.util.Date"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script type="text/javascript" src="/js/validateDate.js"></script>
<%
int i=0;
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
String date = sdf.format(new Date());

%>
<script language="javascript">
   function comma_msg()
   {
   	  var waitInfo=eval("wait");			
	  waitInfo.style.display="block";
   }
   
   function opennewwin(a0100,nbase,tabid) {
   	var url = "/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase="+nbase+"&a0100="+a0100+"&inforkind=1&tabid="+tabid+"&multi_cards=-1"
   	window.open(url);
   }
</script>

<html:form action="/system/sms/mail_list_comma">
<div id='wait' style='position:absolute;top:150;left:150;display:none;'   >
  <table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style" height=24>正在接收短信，请稍等...</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div>

<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable"  style="margin-top:-2px;">
	<tr height="30px">
	  <td align="left" class="" nowrap colspan="5">
	  	<html:hidden name="mailListForm" property="state" value="2"/>
	   &nbsp;<bean:message key="label.from1"/>
				<input type="text" name="startime" value='<bean:write name="mailListForm" property="startime"/>'  extra="editor" style="width:100px;font-size:10pt;text-align:left;"  class="complex_border_color inputtext"  id="editor1"  dropDown="dropDownDate" onchange="if(!validate(this,'开始时间')) {this.focus(); this.value='<%=date %>'; }">
				<bean:message key="kq.init.tand"/>
				<input type="text" name="endtime" value='<bean:write name="mailListForm" property="endtime"/>'  extra="editor" style="width:100px;font-size:10pt;text-align:left;" class="complex_border_color inputtext"  id="editor1"  dropDown="dropDownDate" onchange="if(!validate(this,'结束时间')) {this.focus(); this.value='<%=date %>'; }">	
				&nbsp;<hrms:submit styleClass="mybutton" property="b_query"><bean:message key="button.query"/></hrms:submit>
	   </td> 	
	</tr>
    <tr> 
      <td align="center" class="TableRow" width="25" nowrap><input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'></td>
      <td align="center" class="TableRow" nowrap><bean:message key="system.sms.sman"/></td>
      <td align="center" class="TableRow" nowrap><bean:message key="system.sms.mobimun"/></td>
      <td align="center" class="TableRow" nowrap><bean:message key="conlumn.board.content"/></td>	 
      <td align="center" class="TableRow" nowrap><bean:message key="conlumn.board.commatime"/></td>	  
    </tr>

   <hrms:paginationdb id="element" name="mailListForm" sql_str="mailListForm.str"  table=""  where_str="mailListForm.where" columns="${mailListForm.conum}" page_id="pagination" order_by="${mailListForm.order}"  pagerows="15" indexes="indexes">
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
             	   LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
             	   String a0100_encrypt=(String)abean.get("a0100");              	            	   
                   request.setAttribute("a0100_encrypt",PubFunc.encrypt(a0100_encrypt));    	                           
                   %>    
          
             <td align="center" class="RecordRow" nowrap>
              <hrms:checkmultibox name="mailListForm" property="pagination.select" value="true"    indexes="indexes"/>
             </td>            
             <td align="left" class="RecordRow" nowrap>
         
             		<logic:notEqual name="mailListForm" property="cardid" value="-1">
             			<logic:notEmpty name="element" property="a0100">
             				<logic:notEmpty name="element" property="nbase">
             					&nbsp;<a href="###" onclick="opennewwin('${a0100_encrypt}','<bean:write  name="element" property="nbase" filter="true"/>','<bean:write  name="mailListForm" property="cardid" filter="true"/>')"><bean:write  name="element" property="sender" filter="true"/></a>&nbsp;
             				</logic:notEmpty>
             				<logic:empty name="element" property="nbase">
             					&nbsp;<bean:write  name="element" property="sender" filter="true"/>&nbsp;
             				</logic:empty>
             			</logic:notEmpty>
             			<logic:empty name="element" property="a0100">
             				&nbsp;<bean:write  name="element" property="sender" filter="true"/>&nbsp;
             			</logic:empty>
             		</logic:notEqual>
             	    <logic:equal name="mailListForm" property="cardid" value="-1">     
                   	&nbsp;<bean:write  name="element" property="sender" filter="true"/>&nbsp;
                   </logic:equal>    
             </td>
              <td align="left" class="RecordRow" nowrap>              
                   &nbsp;<bean:write  name="element" property="mobile_no" filter="true"/>&nbsp;
             </td> 
              <td align="left" class="RecordRow">              
                  &nbsp; <bean:write  name="element" property="msg" filter="true" />&nbsp;
             </td> 
             <td align="left" class="RecordRow" nowrap>              
                   &nbsp;<bean:write  name="element" property="send_time" filter="true"/>&nbsp;
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
				</hrms:paginationdblink>
			</td>
		</tr>
</table> 

<table cellspacing="0" cellpadding="0"   width="90%" align="center">
       <tr height="35px">
          <td align="center">
         	 <hrms:submit styleClass="mybutton" property="b_all_delete" onclick="document.mailListForm.target='_self';return ifdelall()&&document.returnValue;"><bean:message key="system.sms.alle"/></hrms:submit>
         	 <hrms:submit styleClass="mybutton" property="b_delete" onclick="document.mailListForm.target='_self';return ifmsdel()&&document.returnValue;"><bean:message key="button.delete"/></hrms:submit>
         	 <hrms:submit styleClass="mybutton" property="b_comma" onclick="comma_msg()">收件</hrms:submit>
          </td>
        </tr>          
</table>

</html:form>

