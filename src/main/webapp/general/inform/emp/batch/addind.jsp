<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="../../inform.js"></script>
<script language="JavaScript">

function IsDigit(obj) {
	if((event.keyCode >= 46) && (event.keyCode <= 57)){
		var values=obj.value;
		if((event.keyCode == 46) && (values.indexOf(".")!=-1))
			return false;
		if((event.keyCode == 46) && (values.length==0))
			return false;	
	}else{
		return false;
	}
}

//如果是ie6
function ie6Style(){
	if(isIE6()){
		document.getElementById('tableId').style.cssText="margin-top:-3px;margin-left=-3px;";
	}
}
</script>
<style type="text/css">
#scroll_box {
           border: 1px solid #eee;
           height: 270px;    
           width: 450px;            
           overflow: auto;            
           margin: 1em 0;
       }
</style>
<html:form action="/general/inform/emp/batch/addind">
<%int i=1;%>
<input type="hidden" name="companyid">
<input type="hidden" name="depid">
<input type="hidden" name="jobid">
<table id="tableId" width="498px" border="0" align="center" style="margin-top:-8px;margin-left=-5px;">
  <tr> 
    <td width="100%" height="300" align="center"> 
      <table width="100%" border="0">
        <tr>
          <td  valign="top" align="center"> 
           <fieldset style="width:100%;height:320">
     		 <legend><bean:message key='infor.menu.batupdate_a'/></legend>
     		 <div id="scroll_box"  style="border-left: none;border-right: none;border-bottom: none;">
             <table width="100%" border="0" class="ListTable1">
              <tr class="fixedHeaderTr1"> 
                <td width="10%" class="TableRow" align="center">&nbsp;</td>
                <td width="30%" class="TableRow" align="center"><bean:message key='field.label'/></td>
                <td class="TableRow" align="center"><bean:message key='infor.menu.additional.value'/></td>
              </tr>
              <logic:iterate id="element" name="indBatchHandForm" property="fieldlist">
              <tr> 
                <td class="RecordRow" align="center" nowrap><%=i%></td>
                <td class="RecordRow"  nowrap><bean:write name="element" property="itemdesc"/></td>
                <logic:equal name="element" property="codesetid" value="0">
                	<logic:equal name="element" property="itemtype" value="D">
                		<td class="RecordRow"  nowrap>
                			<input type="text" class="text4" name="${element.itemid}.value" extra="editor" onblur="timeCheck(this);" style="width:150px;font-size:10pt;text-align:left" dropDown="dropDownDate">
                		 </td>
                	</logic:equal>
                	<logic:equal name="element" property="itemtype" value="N">
                		<td class="RecordRow"  nowrap>
                			<input type="text" class="text4" name="${element.itemid}.value" onkeypress="event.returnValue=IsDigit(this);" style="width:150px;ime-mode:disabled">
                		</td>
                	</logic:equal>
                	<logic:notEqual name="element" property="itemtype" value="D">
                		<logic:notEqual name="element" property="itemtype" value="N">
                			<td class="RecordRow"  nowrap>
                				<input type="text" class="text4" name="${element.itemid}.value" style="width:150px;">
                			</td>
                		 </logic:notEqual>
                	</logic:notEqual>	
                </logic:equal>
                <logic:notEqual name="element" property="codesetid" value="0">
                	<logic:equal name="element" property="codesetid" value="UN">
                		<td class="RecordRow"  nowrap>
                			<input type="hidden" name="comp" value="${element.itemid}">
                			<input type="hidden" name="${element.itemid}.value" onchange="changepos('UN');orgchngeinfo(this,1);">
                			<input type="text" class="textColorRead" name="${element.itemid}.hzvalue" style="width:150px;" onchange="fieldcode(this,1);" readOnly>
                			<img align="absMiddle" src="/images/code.gif" onclick='openOrgInfo("${element.codesetid}","${element.itemid}.hzvalue",1,"1");'/>&nbsp;
                		</td>
                	</logic:equal>
                	<logic:equal name="element" property="codesetid" value="UM">
                		<td class="RecordRow"  nowrap>
                			<input type="hidden" name="dep" value="${element.itemid}">
                			<input type="hidden" name="${element.itemid}.value" onchange="changepos('UM');orgchngeinfo(this,2);">
                			<input type="text" class="textColorRead" name="${element.itemid}.hzvalue" style="width:150px;" onchange="fieldcode(this,1);" readOnly>
                			<img align="absMiddle" src="/images/code.gif" onclick='openOrgInfo("${element.codesetid}","${element.itemid}.hzvalue",2,"2");'/>&nbsp;
                		</td>
                	</logic:equal>
                	<logic:equal name="element" property="codesetid" value="@K">
                		<td class="RecordRow"  nowrap>
                			<input type="hidden" name="job" value="${element.itemid}">
                			<input type="hidden" name="${element.itemid}.value" onchange="changepos('@K');orgchngeinfo(this,3);">
                			<input type="text" class="textColorRead" name="${element.itemid}.hzvalue" style="width:150px;" onchange="fieldcode(this,1);" readOnly>
                			<img align="absMiddle" src="/images/code.gif" onclick='openOrgInfo("${element.codesetid}","${element.itemid}.hzvalue",3,"2");'/>&nbsp;
                		</td>
                	</logic:equal>
                	<logic:notEqual name="element" property="codesetid" value="UN">
                		<logic:notEqual name="element" property="codesetid" value="UM">
                			<logic:notEqual name="element" property="codesetid" value="@K">
                				<td class="RecordRow"  nowrap>
                					<input type="hidden" name="${element.itemid}.value">
                					<input type="text" class="textColorRead" name="${element.itemid}.hzvalue" style="width:150px;" readOnly>
                					<img align="absMiddle" src="/images/code.gif" onclick='javascript:openCondCodeDialog("${element.codesetid}","${element.itemid}.hzvalue");'/>&nbsp;
                				</td>
                			</logic:notEqual>
                		</logic:notEqual>
                	</logic:notEqual>
                </logic:notEqual>
              </tr>
              <%i++;%>
              </logic:iterate>
            </table>
            </div>
            </fieldset>
          </td>
        </tr>
      </table>
    </td>

  </tr>
  <tr>  
          <td height="20" align="center">
			<input type="button" name="Submit" value="<bean:message key='button.ok'/>" onclick='saveAddind("${indBatchHandForm.setname}","${indBatchHandForm.a_code}","${indBatchHandForm.viewsearch}","${indBatchHandForm.dbname}","${indBatchHandForm.infor}","${indBatchHandForm.count}","${indBatchHandForm.inforflag}");' Class="mybutton">
			<input type="button" name="Submit2" value="<bean:message key='button.close'/>" onclick="window.close();" Class="mybutton">
		  </td>
    </tr>
</table>
</html:form>
<script language="JavaScript">
ie6Style();
</script>