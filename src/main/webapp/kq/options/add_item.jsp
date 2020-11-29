<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.options.KqItemForm" %>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<SCRIPT LANGUAGE=javascript src="/js/color.js"></SCRIPT>
<script type="text/javascript" src="/js/validate.js"></script>
<%
	String item_id = request.getParameter("akq_item");
	String returnFlag = request.getParameter("returnFlag");
%>
<script type="text/javascript">
	function change(va)
	{
		var item_sy2 = document.getElementById("item_sy2");
	    item_sy2.value = va;
	}
	function save(){
		kqItemForm.action="/kq/options/add_item.do?b_save=link&item_id=<%=item_id%>&returnFlag=<%=returnFlag%>";
		kqItemForm.submit();
	}
</script> 
<html:form action="/kq/options/add_item">
	<br>
	<br>	
	<html:hidden name="kqItemForm" property="display"/>
	<input type="hidden" name="item_id" value="<%=item_id %>"/>
	<table border="0" cellspacing="0"  align="center" width="55%">
	<tr><td>
  <fieldset align="center" >
     <legend ><bean:message key="kq.addItem.item"/></legend>
      <table border="0" cellspacing="0"  align="center" cellpadding="2" >
        <%--<tr >
          <td align="right" nowrap valign="left">        
            <bean:message key="kq.addItem.sort"/>        
           </td>
          <td align="left"  nowrap valign="center"> 
	          <html:select name="kqItemForm" property="item.string(item_id)" size="1" >
              <html:optionsCollection  property="klist" value="dataValue" label="dataName"/>
             </html:select> 
           </td>
         </tr>--%>
         <div id="wmj" style="display:none">
         	<html:select name="kqItemForm" property="item.string(item_id)" size="1" >
              <html:optionsCollection  property="klist" value="dataValue" label="dataName"/>
             </html:select> 
         </div>
        <tr >
         <td align="right" nowrap valign="middle">        
           <bean:message key="kq.item.name"/>        
          </td>
         <td align="left"  nowrap valign="middle">
             <html:text name="kqItemForm" property="item.string(item_name)" readonly="true" style="width:135px;background-color:#aaa;"/>  
         </td>		
        </tr>  
       <logic:empty name="kqItemForm" property="gw_flag">
	       <tr >
        <td align="right" nowrap valign="middle">        
	           <bean:message key="kq.item.stat"/>         
	         </td>
        <td align="left"  nowrap valign="middle">
	           <html:select name="kqItemForm" property="item.string(fielditemid)" size="1"  style="width:135px">
	              <html:optionsCollection  property="fieldlist" value="dataValue" label="dataName"/>
	           </html:select> 
	              
	        </td>		
	       </tr>    
	       <tr >
        <td align="right" nowrap valign="middle">        
	           <bean:message key="kq.item.measure"/>       
	         </td>
        <td align="left"  nowrap valign="middle">
	           <hrms:importgeneraldata showColumn="codeitemdesc" valueColumn="codeitemid" flag="false" paraValue="" 
	                sql="SELECT  codeitemid,codeitemdesc FROM codeitem where  codesetid ='28'" collection="list" scope="page"/>
	             <html:select name="kqItemForm" property="item.string(item_unit)" size="1"  style="width:135px">
	                <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	             </html:select> 
	          </td>		
	        </tr>  
       </logic:empty>
       <tr >
        <td align="right" nowrap valign="middle">        
           <bean:message key="kq.item.sign"/>         
         </td>
         <%
         	KqItemForm kqItemForm = (KqItemForm)request.getSession().getAttribute("kqItemForm");
         	RecordVo item_vo = kqItemForm.getItem();
         	String item_sy=item_vo.getString("item_symbol");
          %>
        <td align="left"  nowrap valign="middle">
        	<div style="position:relative;"> 
	        	<span style="margin-left:115px;	height:22px; width:135px; overflow:hidden;">
		        	<html:select styleId="sel_item_symbol" name="kqItemForm" property="item.string(item_symbol)" 
		        	    style="margin-left:-115px; width:134px;" 
		        	    onchange="change(this.value);" size="1" >
		        		<html:option value=""></html:option>
		        		<html:option value="√">√</html:option>
		        		<html:option value="×">×</html:option>
		        		<html:option value="★">★</html:option>
		        		<html:option value="☆">☆</html:option>
		        		<html:option value="◆">◆</html:option>
		        		<html:option value="◇">◇</html:option>
		        		<html:option value="■">■</html:option>
		        		<html:option value="□">□</html:option>
		        		<html:option value="▲">▲</html:option>
		        		<html:option value="△">△</html:option>
		        		<html:option value="●">●</html:option>
		        		<html:option value="○">○</html:option>
		        		<html:option value="◎">◎</html:option>
		        		<html:option value="※">※</html:option>
		        		<html:option value="§">§</html:option>
		        		<html:option value="＃">＃</html:option>
		        		<html:option value="＆">＆</html:option>
		        		<html:option value="♁">♁</html:option>
		        		<html:option value="⊙">⊙</html:option>
		        	</html:select> 
        		</span>
        		<% 
        			if(item_sy.length()>0)
        			{
        			
        		%>
        			<input id="item_sy2" type="text" name="item_sy" value=<%=item_sy%> 
        			    style="left:0px; height:22px; width:117px; position:absolute;">
        		<% 
        			}else
        			{
        		%>
        			<input id="item_sy2" type="text" name="item_sy" value="" 
        			    style="left:0px; height:22px; width:117px; position:absolute;" >
        		<%
        			} 
        		%>
        		
        	</div>
         </td>		
       </tr> 
       <logic:empty name="kqItemForm" property="gw_flag">
	      <tr>
        <td align="right" nowrap valign="middle">        
	          <bean:message key="kq.item.color"/>         
	         </td>
        <td align="left"  nowrap valign="middle">
	           	   <html:text  name="kqItemForm" property="item.string(item_color)" alt="clrDlg" size="6" 
	           	   style="BACKGROUND-COLOR:${kqItemForm.colo}; width:135px"  
	           	   styleClass="textColorWrite" readonly="true"/>
	         </td>		
	        </tr>  
	       <tr >
        <td align="left" nowrap vlign="middle" colspan="2">        
	          <html:checkbox name="kqItemForm" property="item.string(has_rest)" value="1" /><bean:message key="kq.item.rest"/>
		        <html:checkbox name="kqItemForm" property="item.string(has_feast)" value="1" /><bean:message key="kq.item.feria"/>
		        <html:checkbox name="kqItemForm" property="item.string(want_sum)" value="1" /><bean:message key="kq.item.cellect"/>        
	         </td>
	       </tr>   
       </logic:empty>
     </table>
     <br>
	</fieldset>
	</td></tr>
	</table>
	<table align="center">
		<tr>
        	<td align="center" colspan="2">
	       		<input type="button" value="<bean:message key="button.save"/>" class="mybutton" onclick="save();"/>
         	 	<input type="button" value="<bean:message key="button.return"/>" class="mybutton" onclick="history.back();"/>
          	</td>
        </tr>
    </table>
</html:form>
<div id="colorpanel" style="position:absolute;display:none;width:253px;height:177px;z-index:3"></div>
<script>
var inputItemSy = document.getElementById("item_sy2");
if(inputItemSy) {
	// 为保持symbol输入框与下拉框组件高度一致，没办法，硬编码
    if(getBrowseVersion() > 0)
        inputItemSy.style.height = 22;
    else
        inputItemSy.style.height = 19.6;
}
</script>