<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<html:form action="/workflow/rensi/rensi_view">
      <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" >

          <tr height="20">
       		<!-- <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;人事变动&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="800"></td> --> 
       		<td align=center class="TableRow">&nbsp;人事变动&nbsp;</td>            	      
          </tr>
          <tr>
            <td  class="framestyle9">
              <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                 
                    <tr >
              	      <td align="right" nowrap>变动类型：</td>
              	      <td align="left" nowrap >
          				<hrms:codetoname codeid="AA" name="rensiForm" codevalue="bian_vo.string(change_status)" codeitem="codeitem"/>&nbsp;  	      
          				<bean:write name="codeitem" property="codename" />&nbsp;				
                        </td>
                    </tr>
                    <tr >
              	      <td align="right" nowrap>部门：</td>
              	      <td align="left" nowrap >
          				   <hrms:codetoname codeid="UN" name="rensiForm" codevalue="bian_vo.string(unit)" codeitem="codeitem"/>&nbsp;  	      
          				   <bean:write name="codeitem" property="codename" />&nbsp;
          				   <hrms:codetoname codeid="UM" name="rensiForm" codevalue="bian_vo.string(department_id)" codeitem="codeitem"/>&nbsp;  	      
          				   <bean:write name="codeitem" property="codename" />&nbsp;					   	    	      
                        </td>
                    </tr>
                    <tr >
              	      <td align="right" nowrap>职务：</td>
              	      <td align="left" nowrap >
          				   <hrms:codetoname codeid="@K" name="rensiForm" codevalue="bian_vo.string(job_id)" codeitem="codeitem"/>&nbsp;  	      
          				   <bean:write name="codeitem" property="codename" />&nbsp;					   	    	      
                        </td>
                    </tr>          
                    <tr >
              	      <td align="right" nowrap>姓名：</td>
              	      <td align="left" nowrap >
                             <bean:write name="rensiForm" property="bian_vo.string(staff_name)" filter="true"/>&nbsp;                   
                        </td>
                    </tr>
                    <tr >
              	      <td align="right" nowrap>性别：</td>
              	      <td align="left" nowrap >
          				   <hrms:codetoname codeid="AX" name="rensiForm" codevalue="bian_vo.string(sex)" codeitem="codeitem"/>&nbsp;  	      
          				   <bean:write name="codeitem" property="codename" />&nbsp;					   	    	      
                          
                        </td>
                    </tr>          
                              
                    <tr >
              	      <td align="right" nowrap>是否转正：</td>
              	      <td align="left"  nowrap >
          				   <hrms:codetoname codeid="ZS" name="rensiForm" codevalue="bian_vo.string(duty_status)" codeitem="codeitem"/>&nbsp;  	      
          				   <bean:write name="codeitem" property="codename" />&nbsp;					   	    	      
          
                        </td>
                    </tr>
                    <tr >
              	      <td align="right" nowrap>联系电话：</td>
              	      <td align="left"  nowrap >
                             <bean:write name="rensiForm" property="bian_vo.string(telephone)" filter="true"/>&nbsp;                   
                        </td>
                    </tr>
                    <tr >
              	      <td align="right" nowrap>入司时间：</td>
              	      <td align="left"  nowrap >
                          <bean:write name="rensiForm" property="bian_vo.string(come_date)" filter="true"/>&nbsp;                   
                        </td>
                    </tr>          
                    <tr >
              	      <td align="right" nowrap>上岗时间：</td>
              	      <td align="left"  nowrap >
                          <bean:write name="rensiForm" property="bian_vo.string(post_date)" filter="true"/>&nbsp;                   
                        </td>
                    </tr>
                    <tr >
              	      <td align="right" nowrap>变动情况：</td>
              	      <td align="left"  wrap>
                          <bean:write name="rensiForm" property="bian_vo.string(change_circs)" filter="true"/>&nbsp;                   
                        </td>
                    </tr> 
                    <tr >
              	      <td align="right" nowrap>变动原因：</td>
              	      <td align="left"  wrap >
                          <bean:write name="rensiForm" property="bian_vo.string(change_whys)" filter="true"/>&nbsp;                   
                        </td>
                    </tr>
                </table>    
             </td>                                                 
         </tr>
      </table>
</html:form>
