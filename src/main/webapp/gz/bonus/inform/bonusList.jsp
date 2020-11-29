 
 <%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/js/dict.js"></script> 
<script language="javascript" src="/gz/bonus/inform/bonus.js"></script>
<style type="text/css"> 
.selectPre{
	position:absolute;
    left:450px;
    top:35px;
    z-index: 10;
}
</style>
<html:form action="/gz/bonus/inform">
	<table>
		<tr>
			<td>
				<table>
					<tr>
						<td>
							<hrms:menubar menu="menu1" id="menubar1">
								<hrms:menuitem name="gz0" label="menu.file.label" function_id="32402140101">
									<hrms:menuitem name="m1" label="menu.gz.import"
										icon="/images/import.gif" url="importTable()" function_id="3240214010101" />
									<hrms:menuitem name="m2" label="menu.gz.export"
										icon="/images/export.gif" url="exportExcel('${bonusForm.bonusSet}','${bonusForm.jobnumFld}');" function_id="3240214010102" />
									<hrms:menuitem name="m3" label="export.bonus.sum"
										icon="/images/export.gif" url="exportSumData('${bonusForm.bonusSet}')" function_id="3240214010103" />
									<hrms:menuitem name="m4" label="button.download.template"
										icon="/images/export.gif" url="downLoadTemp('${bonusForm.bonusSet}','${bonusForm.jobnumFld}');"
										function_id="3240214010104" />
								</hrms:menuitem>
								<hrms:menuitem name="gz1" label="menu.gz.edit" function_id="32402140102">
									<hrms:menuitem name="m1" label="button.insert"
										icon="/images/prop_ps.gif" url="add();" function_id="3240214010201" />
									<hrms:menuitem name="m2" label="menu.gz.delete"
										icon="/images/del.gif" url="del('${bonusForm.bonusSet}','${bonusForm.doStatusFld}')" 
										 function_id="3240214010202" />
									<hrms:menuitem name="m3" label="button.query"
										icon="/images/quick_query.gif" url="querycondition('${bonusForm.bonusSet}','${bonusForm.doStatusFld}')"
										function_id="3240214010203" />
									<hrms:menuitem name="m4" label="menu.batch.add"
										icon="/images/goto_input.gif" url="" command="" visible="true"
										function_id="" />
									<hrms:menuitem name="m5" label="menu.gz.batch.update"
										icon="/images/edit.gif" url="" command="" visible="true"
										function_id="3240214010204" url="bathUpdate()"/>
								</hrms:menuitem>
							</hrms:menubar>
						</td>
					</tr>
				</table>
			</td>
			<td>
				&nbsp;
			</td>
		</tr>
	</table>
<hrms:dataset name="bonusForm" property="fieldlist" scope="session" setname="Usr${bonusForm.bonusSet}"  
setalias="data_table" readonly="false" rowlock="true" editable="true" select="true" 
sql="${bonusForm.sql}" pagerows="${bonusForm.pagerows}" rowlock="true"  rowlockfield="${bonusForm.doStatusFld}" rowlockvalues=",0," buttons="movefirst,prevpage,nextpage,movelast">
   <hrms:commandbutton name="add" function_id="3240214010201" hint="" functionId="" visible="true" refresh="true" type="selected" setname="${bonusForm.bonusSet}" onclick="add();" >
     <bean:message key="button.insert"/> 
   </hrms:commandbutton>
   <hrms:commandbutton name="save" functionId="32402140103"  hint="hire.confirm.save2"   function_id="3241403"   refresh="true" type="all-change" setname="${bonusForm.bonusSet}">
	   <bean:message key="button.save"/>
	</hrms:commandbutton> 
    <hrms:commandbutton name="delselected" function_id="3240214010202" hint="" visible="true" functionId="" refresh="true" type="selected" setname="${bonusForm.bonusSet}" onclick="del('${bonusForm.bonusSet}','${bonusForm.doStatusFld}')">
     <bean:message key="button.delete"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="import" function_id="3240214010101" hint="" functionId="" visible="true" refresh="true" type="selected" setname="${bonusForm.bonusSet}" onclick="importTable();">
     <bean:message key="button.import"/> 
   </hrms:commandbutton>
   <hrms:commandbutton name="export" function_id="3240214010102" hint="" functionId="" visible="true" refresh="true" type="selected" setname="${bonusForm.bonusSet}" onclick="exportExcel('${bonusForm.bonusSet}','${bonusForm.jobnumFld}');">
     <bean:message key="button.export"/> 
   </hrms:commandbutton>
   <hrms:commandbutton name="mc" function_id="32402140104" hint="" functionId="" visible="true" refresh="true" type="selected" setname="${bonusForm.bonusSet}" onclick="outhmuster();" >
     <bean:message key="button.bonus.mc"/> 
   </hrms:commandbutton>
    <hrms:commandbutton name="submit" function_id="32402140105" hint="" visible="true" functionId="" refresh="true" type="selected" setname="${bonusForm.bonusSet}" onclick="updateStatus('1','${bonusForm.doStatusFld}','${bonusForm.bonusSet}')">
     <bean:message key="button.submit"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="reject" function_id="32402140106" hint="" visible="true" functionId="" refresh="true" type="selected" setname="${bonusForm.bonusSet}" onclick="updateStatus('0','${bonusForm.doStatusFld}','${bonusForm.bonusSet}')">
     <bean:message key="button.reject"/>
   </hrms:commandbutton>
</hrms:dataset>
<table id="selectprename" class="selectPre">
					<tr>
						<td align="right">
							<bean:message key="label.login.appdate" />
							<html:select name="bonusForm" property="businessDate" size="1" 
								onchange="query();" style="width:100px">
								<html:optionsCollection property="dateList" value="dataValue"
									label="dataName" />
							</html:select>
						</td>
					</tr>
				</table>
	<html:hidden name="bonusForm" property="paramStr"/>
	<html:hidden name="bonusForm" property="a_code"/>
	<html:hidden name="bonusForm" property="expr"/>
	<html:hidden name="bonusForm" property="factor"/>
	<html:hidden name="bonusForm" property="sql"/>
</html:form>
