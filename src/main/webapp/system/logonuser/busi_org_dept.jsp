<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">	
   function save(){
   	var str='';
   	str='1,'+$F('busi_org_dept1')+'|2,'+$F('busi_org_dept2')+'|3,'+$F('busi_org_dept3')+'|4,'+$F('busi_org_dept4');
   	//alert(str);
   	window.returnValue=str;
    window.close();
   }
function getorg(param)
	{
		var ret_vo=select_org_emp_dialog(0,1,0,1,0,1);
		if(ret_vo)
		{
			var	re=/,/g;
			var tmp=ret_vo.content;
			//alert(tmp+"  "+ret_vo.title);
			var str=tmp.replace(re,"`");
			$('busi_org_dept'+param+'v').innerHTML=ret_vo.title;
			$('busi_org_dept'+param).value=str;
		}
	}
</script>
<html:form action="/system/logonuser/add_edit_user">

<br>
<table width="90%" border="0" cellpadding="0" cellspacing="0" align="center">
<tr><td align="center">
<div  style="height: expression(document.body.clientHeight-80);width:expression(document.body.clientWidth);overflow-x: hidden;overflow-y:auto;border: #0099ff 1pt solid;">
<table width="100%" border="1" cellpadding="0" cellspacing="0" align="center" class=ListTableF1>
     <tr align="center"  class="fixedHeaderTr">
		<td valign="center" class="TableRow" width="20%" >
			业务名称
		</td>
		<td valign="center" class="TableRow" width="70%">
			操作单位
		</td>
		<td valign="center" class="TableRow" width="10%">
			设置
		</td>
	 </tr> 
	 <tr class="trShallow" height="30"> 
		      <td align="center" valign="middle" class="RecordRowHx" nowrap>工资总额</td>
			  <td align='left' valign='middle' class="RecordRowHx" id='busi_org_dept2v'>
					<bean:write name="logonUserForm" property="busi_org_dept2view"/>
		      </td>
		      <html:hidden name="logonUserForm" property="busi_org_dept2"/>
		      <td  valign='middle' class="RecordRowHx" align="center">  
		           <img src="/images/code.gif" onclick="getorg(2);"/> 
		      </td>
	</tr> 
	<tr class="trShallow" height="30"> 
		      <td align="center" valign="middle" class="RecordRowHx" nowrap>所得税管理</td>
			  <td    align='left' valign='middle' class="RecordRowHx" id='busi_org_dept3v'>
				<bean:write name="logonUserForm" property="busi_org_dept3view"/>
		      </td>
		      <html:hidden name="logonUserForm" property="busi_org_dept3"/>
		      <td  valign='middle' class="RecordRowHx" align="center">  
		          <img src="/images/code.gif" onclick="getorg(3);"/>   	
		      </td>
	</tr> 
	<tr class="trShallow" height="30"> 
		      <td align="center" valign="middle" class="RecordRowHx" nowrap>工资发放</td>
			  <td    align='left' valign='middle' class="RecordRowHx" id='busi_org_dept1v'>
					<bean:write name="logonUserForm" property="busi_org_dept1view"/>
		      </td>
		      <html:hidden name="logonUserForm" property="busi_org_dept1"/>
		      <td  valign='middle' class="RecordRowHx" align="center">  
		          <img src="/images/code.gif" onclick="getorg(1);"/>   	
		      </td>
	</tr> 
	<tr class="trShallow" height="30"> 
		      <td align="center" valign="middle" class="RecordRowHx" nowrap>组织机构</td>
			  <td    align='left' valign='middle' class="RecordRowHx" id='busi_org_dept4v'>
					<bean:write name="logonUserForm" property="busi_org_dept4view"/>
		      </td>
		      <html:hidden name="logonUserForm" property="busi_org_dept4"/>
		      <td  valign='middle' class="RecordRowHx" align="center">  
		          <img src="/images/code.gif" onclick="getorg(4);"/>   	
		      </td>
	</tr>
</table>
  </div> 
  </td>
  </tr>
</table>   
     <table  width="100%" align="center">
          <tr>
            <td align="center">
               <br>
	     <input type="button"  value=" <bean:message key="button.ok"/>" class="mybutton" onclick="save();">
	     <input type="button"  value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close();">
            </td>
          </tr>          
    </table>
</html:form>
