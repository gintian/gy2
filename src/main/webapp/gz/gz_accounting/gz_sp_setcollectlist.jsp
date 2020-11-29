<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.hjsj.sys.VersionControl"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%!
VersionControl ver = new VersionControl();
 %>
<%
	int i=0;
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="/js/function.js"></script>
<script type="text/javascript" src="sp_salary.js"></script>
<script language='javascript' >
var userid='${accountingForm.userid}'
var verify_ctrl="";
var isSendMessage="";
var a00z2="";
var a00z3="";
var salaryid="";
var isTotalControl="";
var gz_module='${accountingForm.gz_module}';

</script>
<html:form action="/gz/gz_accounting/gz_sp_setcollectlist">

<div id='wait' style='position:absolute;top:60;left:400;display:none;'>
		<table border="1" width="100" cellspacing="0" cellpadding="4"  class="table_style"  height="87" align="center">
			<tr>
				<td  class="td_style"  id='wait_desc'   height=24>
					<bean:message key="label.gz.submitData"/>......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee  class="marquee_style"  direction="right" width="300" scrollamount="5" scrolldelay="10" >
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
		<iframe src="javascript:false" style="position:absolute; visibility:inherit; top:0px; left:0px;width:315; height:87; 					    	
			   			 				z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';"></iframe>	
</div>




<br>
 
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
        <tr>
               
         <td align="center" width='40%' class="TableRow" nowrap >
           <logic:equal name="accountingForm" property="gz_module" value="0">
		     <bean:message key="label.gz.salarytype"/>
		   </logic:equal>		    
           <logic:equal name="accountingForm" property="gz_module" value="1">
		     <bean:message key="sys.res.ins_set"/>
		   </logic:equal>	
	     </td>         

         <td align="center"  width='13%'  class="TableRow" nowrap >
			业务日期
         </td>
        <td align="center" width='12%'  class="TableRow" nowrap >
			发薪人数
         </td>
         <td align="center" width='12%'  class="TableRow" nowrap >
			发放金额
         </td>
         <td align="center" width='23%'  class="TableRow" nowrap >
			业务处理
         </td>		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="accountingForm" property="setlistform.list" indexes="indexes"  pagination="setlistform.pagination" pageCount="15" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow"   onmouseout="changTRColor(this,'');" onmouseover="changTRColor(this,'#FFF8D2');" >
          <%}
          else
          {%>
          <tr class="trDeep"  onmouseout="changTRColor(this,'');" onmouseover="changTRColor(this,'#FFF8D2');">
          <%
          }
          i++;
          
          LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
          String _count=(String)abean.get("_count");
         
          %>  
              
           <td align="left" class="RecordRow" nowrap   >
              <a href="/gz/gz_accounting/gz_sp_orgtree.do?b_query=link&returnflag=collect&ori=0&salaryid=<bean:write name="element" property="salaryid" filter="true"/>">
				&nbsp;<bean:write name="element" property="cname" filter="true"/>
			  </a>
	       </td>
 		   <td align="left" class="RecordRow" nowrap>
 		  	 &nbsp;<bean:write name="element" property="a00z2" filter="true"/>
 		   </td>
           <td align="right" class="RecordRow" nowrap>
 		  	 <bean:write name="element" property="_count" filter="true"/>&nbsp;
 		   </td>
           <td align="right" class="RecordRow" nowrap>
 		  	 <bean:write name="element" property="_sum" filter="true"/>&nbsp;
 		   </td>    
            <td align="center" class="RecordRow" nowrap> 
           <% if(_count.length()>0){ %>
           <hrms:priv func_id="3241601,3251601">
           <input type='button' class="mybutton" property="reject"  onclick="reject('<bean:write name="element" property="a00z2" filter="true"/>','<bean:write name="element" property="a00z3" filter="true"/>','<bean:write name="element" property="salaryid" filter="true"/>')" value='驳 回'  />
           </hrms:priv>
           &nbsp;
           <hrms:priv func_id="3241602,3251602">
           <input type='button' class="mybutton" property="appeal"  onclick="appeal('<bean:write name="element" property="a00z2" filter="true"/>','<bean:write name="element" property="a00z3" filter="true"/>','<bean:write name="element" property="salaryid" filter="true"/>','<bean:write name="element" property="verify_ctrl" filter="true"/>','<bean:write name="element" property="isSendMessage" filter="true"/>','<bean:write name="element" property="isControl" filter="true"/>')" value='报 批'  />
           </hrms:priv>
           &nbsp;
           <hrms:priv func_id="3241603,3251603">
           <input type='button' class="mybutton" property="confirm"  onclick="confirmRecord('<bean:write name="element" property="a00z2" filter="true"/>','<bean:write name="element" property="a00z3" filter="true"/>','<bean:write name="element" property="salaryid" filter="true"/>','<bean:write name="element" property="verify_ctrl" filter="true"/>','<bean:write name="element" property="isSendMessage" filter="true"/>','<bean:write name="element" property="isControl" filter="true"/>')" value='批 准'  />
           </hrms:priv>
           <% } %>
            </td>
            
               
          </tr>
        </hrms:extenditerate>
        
</table>
<table  width="80%"  class='RecordRowP' align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="accountingForm" property="setlistform.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="accountingForm" property="setlistform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="accountingForm" property="setlistform.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="accountingForm" property="setlistform.pagination"
				nameId="setlistform" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
 
 
 <input type='hidden' name='rejectCause' value='' />
 <input type='hidden' name='reportSql' value='' />
 <input type='hidden' name='bosdate' value='' />
 <input type='hidden' name='count' value='' />
 <input type='hidden' name='salaryid' value='' />
  <input type='hidden' name='sendMen' value='' />
  <input type='hidden' name='approveObject' value='' />
 
</html:form>
