<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">
	function ifAssign()
	{
		return ( confirm('确认指派任务吗？') );
	}
	
	function guiChange(thetype)
	{
		var typename,codeid,dept,job,label0;
		typename=MM_findObj_(thetype);
		codeid=typename.value;
		if(codeid=="#")
			return;
		//alert(typename.value);
		dept=document.getElementById("department");
		job=document.getElementById("job");
		if(codeid=="01"||codeid=="03"||codeid=="05"||codeid=="07")
		{
			dept.style.display="none";
			job.style.display="none";			
		}
		if(codeid=="02"||codeid=="04"||codeid=="6")
		{
			dept.style.display="block";
			job.style.display="block";			
		}
		label0=document.getElementById("label");		
		if(codeid=="01")
		{
			label0.innerHTML="上岗时间";
		}
		else
		{
			label0.innerHTML="离岗时间";		
		}
	}
</script>

<html:form action="/workflow/rensi/rensi_biandong">
      <table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;人事变动&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="800"></td>-->
		<td align=center class="TableRow">&nbsp;人事变动&nbsp;</td>             	      
          </tr> 
          <tr>
            <td class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
                      <tr >
                	      <td align="right" nowrap width="20%" class="tdFontcolor">变动类型：</td>
                	      <td align="left" nowrap class="tdFontcolor">
                              <hrms:importgeneraldata showColumn="codeitemdesc" valueColumn="codeitemid" flag="true" paraValue="AA" 
                               sql="select codeitemid,codeitemdesc from codeitem where codesetid=?" collection="list" scope="page"/>
                               <html:select name="rensiForm" property="bian_vo.string(change_status)" size="1" onchange="guiChange('bian_vo.string(change_status)');">
                                  <html:option value="#">请选择...</html:option>
                                  <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                              </html:select>&nbsp;
                          </td>
                      </tr>
            
                      <tr class="list3">
                	      <td align="right" nowrap class="tdFontcolor">部门：</td>
                	      <td align="left" nowrap class="tdFontcolor">
                              <hrms:importgeneraldata showColumn="codeitemdesc" valueColumn="codeitemid" flag="true" paraValue="" 
                               sql="select codeitemid,codeitemdesc from organization where codesetid='UN'" collection="listun" scope="page"/>
                              <html:select name="rensiForm" property="bian_vo.string(unit)" size="1" onchange="document.rensiForm.submit();">
                                  <html:option value="#">请选择...</html:option>
                                  <html:options collection="listun" property="dataValue" labelProperty="dataName"/>
                              </html:select>&nbsp;
                              <hrms:importgeneraldata showColumn="codeitemdesc" valueColumn="codeitemid" flag="true" paraValue="rensiForm.bian_vo.string(unit)" 
                               sql="select codeitemid,codeitemdesc from organization where codesetid='UM' and parentid = ?" collection="listum" scope="page"/>
                              <html:select name="rensiForm" property="bian_vo.string(department_id)" size="1" onchange="document.rensiForm.submit();">
                                  <html:option value="#">请选择...</html:option>
                                  <html:options collection="listum" property="dataValue" labelProperty="dataName"/>
                              </html:select>&nbsp;                  
                          </td>
                      </tr>
                      <tr class="list3">
                	      <td align="right" nowrap class="tdFontcolor">职务：</td>
                	      <td align="left" nowrap class="tdFontcolor">
                              <hrms:importgeneraldata showColumn="codeitemdesc" valueColumn="codeitemid" flag="true" paraValue="rensiForm.bian_vo.string(department_id)" 
                               sql="select codeitemid,codeitemdesc from organization where codesetid='@K' and parentid = ?" collection="listpos" scope="page"/>
                              <html:select name="rensiForm" property="bian_vo.string(job_id)" size="1" >
                                  <html:option value="#">请选择...</html:option>
                                  <html:options collection="listpos" property="dataValue" labelProperty="dataName"/>
                              </html:select>&nbsp;
                          </td>
                      </tr>
                      <logic:equal name="rensiForm" property="bian_vo.string(change_status)" value="01">
                      	<tr id="department" class="tdFontcolor" style="display:none">
            	      </logic:equal>
                      <logic:equal name="rensiForm" property="bian_vo.string(change_status)" value="03">
                      	<tr id="department" class="tdFontcolor" style="display:none">
            	      </logic:equal>
                       <logic:equal name="rensiForm" property="bian_vo.string(change_status)" value="05">
                      	<tr id="department" class="tdFontcolor" style="display:none">
            	      </logic:equal>
                       <logic:equal name="rensiForm" property="bian_vo.string(change_status)" value="07">
                      	<tr id="department" class="tdFontcolor" style="display:none">
            	      </logic:equal>
                       <logic:equal name="rensiForm" property="bian_vo.string(change_status)" value="02">
                      	<tr id="department" class="tdFontcolor" style="display:block">
            	      </logic:equal>
                       <logic:equal name="rensiForm" property="bian_vo.string(change_status)" value="04">
                      	<tr id="department" class="tdFontcolor" style="display:block">
            	      </logic:equal>
                       <logic:equal name="rensiForm" property="bian_vo.string(change_status)" value="06">
                      	<tr id="department" class="tdFontcolor" style="display:block">
            	      </logic:equal>	       	      
                  	      <td align="right" nowrap>原部门：</td>
                  	      <td align="left" nowrap >
                                <html:select name="rensiForm" property="bian_vo.string(former_unit)" size="1" onchange="document.rensiForm.submit();">
                                    <html:option value="#">请选择...</html:option>
                                    <html:options collection="listun" property="dataValue" labelProperty="dataName"/>
                                </html:select>&nbsp;
                                <hrms:importgeneraldata showColumn="codeitemdesc" valueColumn="codeitemid" flag="true" paraValue="rensiForm.bian_vo.string(former_unit)" 
                                 sql="select codeitemid,codeitemdesc from organization where codesetid='UM' and parentid = ?" collection="listum" scope="page"/>
                                
                                <html:select name="rensiForm" property="bian_vo.string(former_department)" size="1" onchange="document.rensiForm.submit();">
                                    <html:option value="#">请选择...</html:option>
                                    <html:options collection="listum" property="dataValue" labelProperty="dataName"/>
                                </html:select>&nbsp;                  
                            </td>
                        </tr>
                      <logic:equal name="rensiForm" property="bian_vo.string(change_status)" value="01">
                        <tr id="job" class="list3" style="display:none">
            	      </logic:equal>
                      <logic:equal name="rensiForm" property="bian_vo.string(change_status)" value="03">
                        <tr id="job" class="list3" style="display:none">
            	      </logic:equal>
                       <logic:equal name="rensiForm" property="bian_vo.string(change_status)" value="05">
                        <tr id="job" class="list3" style="display:none">
            	      </logic:equal>
                       <logic:equal name="rensiForm" property="bian_vo.string(change_status)" value="07">
                        <tr id="job" class="list3" style="display:none">
            	      </logic:equal>
                       <logic:equal name="rensiForm" property="bian_vo.string(change_status)" value="02">
                        <tr id="job" class="list3" style="display:block">
            	      </logic:equal>
                       <logic:equal name="rensiForm" property="bian_vo.string(change_status)" value="04">
                        <tr id="job" class="list3" style="display:block">
            	      </logic:equal>
                       <logic:equal name="rensiForm" property="bian_vo.string(change_status)" value="06">
                        <tr id="job" class="list3" style="display:block">
            	      </logic:equal>            
            
                  	      <td align="right" nowrap>原职务：</td>
                  	      <td align="left" nowrap >
                              <hrms:importgeneraldata showColumn="codeitemdesc" valueColumn="codeitemid" flag="true" paraValue="rensiForm.bian_vo.string(former_department)" 
                               sql="select codeitemid,codeitemdesc from organization where codesetid='@K' and parentid = ?" collection="listpos" scope="page"/>
                  	      
                                <html:select name="rensiForm" property="bian_vo.string(former_job_id)" size="1" >
                                    <html:option value="#">请选择...</html:option>
                                    <html:options collection="listpos" property="dataValue" labelProperty="dataName"/>
                                </html:select>&nbsp;
                            </td>
                      </tr>          
                             
                      <tr class="list3">
                	      <td align="right" nowrap>姓名：</td>
                	      <td align="left" nowrap >
                	      	<html:text name="rensiForm" property="bian_vo.string(staff_name)" size="20" maxlength="20" styleClass="text"/>    	      
                          </td>
                      </tr>
                      <tr class="list3">
                	      <td align="right" nowrap>性别：</td>
                	      <td align="left" nowrap >
                              <hrms:importgeneraldata showColumn="codeitemdesc" valueColumn="codeitemid" flag="true" paraValue="" 
                               sql="select codeitemid,codeitemdesc from codeitem where codesetid='AX'" collection="list" scope="page"/>
                              <html:select name="rensiForm" property="bian_vo.string(sex)" size="1">
                                  <html:option value="#">请选择...</html:option>
                                  <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                              </html:select>&nbsp;
                          </td>
                      </tr>          
                                
                      <tr class="list3">
                	      <td align="right" nowrap>是否转正：</td>
                	      <td align="left"  nowrap >
                              <hrms:importgeneraldata showColumn="codeitemdesc" valueColumn="codeitemid" flag="true" paraValue="" 
                               sql="select codeitemid,codeitemdesc from codeitem where codesetid='ZS'" collection="list0" scope="page"/>
                              <html:select name="rensiForm" property="bian_vo.string(duty_status)" size="1">
                                  <html:option value="#">请选择...</html:option>
                                  <html:options collection="list0" property="dataValue" labelProperty="dataName"/>
                              </html:select>&nbsp;
                          </td>
                      </tr>
                      <tr class="list3">
                	      <td align="right" nowrap>联系电话：</td>
                	      <td align="left"  nowrap >
                	      	<html:text name="rensiForm" property="bian_vo.string(telephone)" size="20" maxlength="20" styleClass="text"/>
                          </td>
                      </tr>
                      <tr id="first" class="list3">
                	      <td align="right" nowrap>入司时间：</td>
                	      <td align="left"  nowrap >
            				<html:text  property="first_date.year" size="4" maxlength="4"  styleClass="text" />年&nbsp;
            				<html:text  property="first_date.month" size="2" maxlength="2" styleClass="text" />月&nbsp;
            				<html:text  property="first_date.date" size="2" maxlength="2" styleClass="text" />日
                          </td>
                      </tr>          
                      <tr id="second" class="list3">
                	      <td align="right" nowrap id="label">上岗时间：</td>
                	      <td align="left"  nowrap >
            				<html:text  property="second_date.year" size="4" maxlength="4"  styleClass="text" />年&nbsp;
            				<html:text  property="second_date.month" size="2" maxlength="2" styleClass="text" />月&nbsp;
            				<html:text  property="second_date.date" size="2" maxlength="2" styleClass="text" />日
                          </td>
                      </tr>
                    
                      <tr class="list3">
                	      <td align="right" nowrap>变动情况：</td>
                	      <td align="left"  nowrap>
                	      	<html:textarea name="rensiForm" property="bian_vo.string(change_circs)" cols="80" rows="6"/>
                          </td>
                      </tr> 
                      <tr class="list3">
                	      <td align="right" nowrap>变动原因：</td>
                	      <td align="left"  nowrap >
                	      	<html:textarea name="rensiForm" property="bian_vo.string(change_whys)" cols="80" rows="6" />
                          </td>
                      </tr>
                 </table>     
              </td>
          </tr>                                          
          <tr class="list3">
            <td align="center" style="height:35px;">
    			<input type="submit" name="b_start" value="提交" class="mybutton" onclick="document.rensiForm.target='_self';validate('RS','bian_vo.string(change_status)','变动类型','RS','bian_vo.string(department_id)','部门','RS','bian_vo.string(job_id)','职务',
    			  'RS','bian_vo.string(duty_status)','是否转正','R','bian_vo.string(staff_name)','姓名','RD','first_date.','入司时间','RD','second_date.','上岗时间');return (document.returnValue && iftqsp());">
    			<input type="submit" name="br_return" class="mybutton" value="返回">
            </td>
          </tr>          
      </table>
</html:form>
