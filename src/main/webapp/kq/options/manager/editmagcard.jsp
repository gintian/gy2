<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/general/inform/inform.js"></script>
<script language="JavaScript">
function IsDigit(obj) {
	if((event.keyCode >= 46) && (event.keyCode <= 57)){
		var values=obj.value;
		if((event.keyCode == 46) && (values.indexOf(".")!=-1))
			return false;
		if((event.keyCode == 46) && (values.length==0))
			return false;	
		
		if((values.lastIndexOf(".")<values.length)&&(values.indexOf(".")!=1))
			return true;
		else
			return false;
	}else{
		return false;
	}
}
function saveSet()
{
   if(confirm("确定保存编辑内容?"))
   {
     var tablevos=document.getElementsByTagName("input");
	 var itemid_arr=new Array();
	 var itemvalue_arr=new Array();
	 var j=0;
	 for(var i=0;i<tablevos.length;i++){
		var id = tablevos[i].name;
		var arr = id.split(".");
		if(arr.length==2){
			if(arr[1]=='value'){
	     		itemid_arr[j]=arr[0];
	     		itemvalue_arr[j]=tablevos[i].value;
	     		j++;
			}
		}
     }     
     var hashvo=new ParameterSet();
	 hashvo.setValue("flag","add");	
	 hashvo.setValue("itemid_arr",itemid_arr);
	 hashvo.setValue("itemvalue_arr",itemvalue_arr);	
	 hashvo.setValue("a0100","${magCardManagerForm.a0100}");
	 hashvo.setValue("i9999","${magCardManagerForm.i9999}");
	 hashvo.setValue("nbase","${magCardManagerForm.nbase}");	 
	 hashvo.setValue("magcard_setid","${magCardManagerForm.magcard_setid}");	 
	 var request=new Request({method:'post',asynchronous:false,onSuccess:saveCard,functionId:'15207000079'},hashvo);
	}
}
function saveCard(outparamters)
{
   var flag=outparamters.getValue("flag");
   if(flag=="1")
   {
     　alert("操作失败！");
   }else
   {
   　　magCardManagerForm.action = "/kq/options/manager/magcarddata.do?b_search=link";
   　　magCardManagerForm.target="mil_body";
　　　 magCardManagerForm.submit(); 
      window.close();  
   }
}

</script>
<style type="text/css">
#scroll_box {
           border: 1px solid #eee;
           height: 320px;    
           width: 500px;            
           overflow: auto;            
           margin: 1em 0;
       }
</style>
<html:form action="/kq/options/manager/editmagcard">
<%int i=1;%>
<table width="100%" border="0" align="center">
  <tr> 
    <td width="90%" height="310" align="center"> 
      <table width="100%" border="0">
        <tr>
          <td height="310" valign="top"> 
          <fieldset style="width:100%;height:310">
     		 <legend>操作</legend>
     		  &nbsp; &nbsp; <bean:write name="magCardManagerForm" property="singmess" filter="false"/>
     		 <div id="scroll_box">     		  
              <table width="100%" border="0" class="ListTable">
              <tr> 
                <td width="10%" class="TableRow" align="center">&nbsp;</td>
                <td width="20%" class="TableRow"  align="center"><bean:message key='field.label'/></td>                
                <td width="35%" class="TableRow" align="center"><bean:message key='infor.menu.alert.value'/>                
                </td>
                <html:hidden name="magCardManagerForm" property="cardno_value" styleId='cardno_value'/>
              </tr>
              <logic:iterate id="element" name="magCardManagerForm" property="changefieldlist">
              <tr> 
                <td class="RecordRow" align="center" nowrap><%=i%></td>
                <td class="RecordRow"  nowrap><bean:write name="element" property="itemdesc"/></td>              
                <!-- 修改值 -->
                <logic:equal name="element" property="codesetid" value="0">
                	<logic:equal name="element" property="itemtype" value="D">
                		<td class="RecordRow"  nowrap>
                			<input type="text" name="${element.itemid}.value"  extra="editor" onblur="timeCheck(this);" value="<bean:write name="element" property="value" filter="true"/>" style="width:150px;font-size:10pt;text-align:left" dropDown="dropDownDate">
                		 </td>                		 
                	</logic:equal>
                	<logic:equal name="element" property="itemtype" value="N">
                		<td class="RecordRow"  nowrap>
                			<input type="text" name="${element.itemid}.value" maxlength="8" value="<bean:write name="element" property="value" filter="true"/>" onkeypress="event.returnValue=IsDigit(this);" style="width:150px;ime-mode:disabled">
                		</td>                		 
                	</logic:equal>
                	<logic:notEqual name="element" property="itemtype" value="D">
                		<logic:notEqual name="element" property="itemtype" value="N">
                			<td class="RecordRow"  nowrap>
                				<input type="text" name="${element.itemid}.value" value="<bean:write name="element" property="value" filter="true"/>" style="width:150px;">
                			</td>                			
                		 </logic:notEqual>
                	</logic:notEqual>	
                </logic:equal>
                <logic:notEqual name="element" property="codesetid" value="0">
                	<logic:equal name="element" property="codesetid" value="UN">
                		<td class="RecordRow"  nowrap>
                			<input type="hidden" name="comp" value="${element.itemid}">
                			<input type="hidden" name="${element.itemid}.value" value="<bean:write name="element" property="value" filter="true"/>"  onchange="changepos('UN');orgchngeinfo(this,1);">
                			<input type="text" name="${element.itemid}.hzvalue" style="width:150px;" value="<bean:write name="element" property="viewvalue" filter="true"/>"  onchange="fieldcode(this,1);" readOnly>
                			<img  src="/images/code.gif" onclick='openOrgInfo("${element.codesetid}","${element.itemid}.hzvalue",1,"1");'/>&nbsp;
                		</td>                		
                	</logic:equal>
                	<logic:equal name="element" property="codesetid" value="UM">
                		<td class="RecordRow"  nowrap>
                			<input type="hidden" name="dep" value="${element.itemid}">
                			<input type="hidden" name="${element.itemid}.value" value="<bean:write name="element" property="value" filter="true"/>" onchange="changepos('UM');orgchngeinfo(this,2);">
                			<input type="text" name="${element.itemid}.hzvalue" style="width:150px;" value="<bean:write name="element" property="viewvalue" filter="true"/>"  onchange="fieldcode(this,1);" readOnly>
                			<img  src="/images/code.gif" onclick='openOrgInfo("${element.codesetid}","${element.itemid}.hzvalue",2,"2");'/>&nbsp;
                		</td>                		
                	</logic:equal>
                	<logic:equal name="element" property="codesetid" value="@K">
                		<td class="RecordRow"  nowrap>
                			<input type="hidden" name="job" value="${element.itemid}">
                			<input type="hidden" name="${element.itemid}.value" value="<bean:write name="element" property="value" filter="true"/>" onchange="changepos('@K');orgchngeinfo(this,3);">
                			<input type="text" name="${element.itemid}.hzvalue" value="<bean:write name="element" property="viewvalue" filter="true"/>"  style="width:150px;" onchange="fieldcode(this,1);" readOnly>
                			<img  src="/images/code.gif" onclick='openOrgInfo("${element.codesetid}","${element.itemid}.hzvalue",3,"2");'/>&nbsp;
                		</td>
                		
                	</logic:equal>
                	<logic:notEqual name="element" property="codesetid" value="UN">
                		<logic:notEqual name="element" property="codesetid" value="UM">
                			<logic:notEqual name="element" property="codesetid" value="@K">
                				<td class="RecordRow"  nowrap>
                					<input type="hidden" name="${element.itemid}.value" value="<bean:write name="element" property="value" filter="true"/>">
                					<input type="text"  name="${element.itemid}.hzvalue" value="<bean:write name="element" property="viewvalue" filter="true"/>"  style="width:150px;" readOnly>
                					<img  src="/images/code.gif" onclick='javascript:openCondCodeDialog("${element.codesetid}","${element.itemid}.hzvalue");'/>&nbsp;
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
    <td width="10%" valign="bottom"> 
      <table width="100%" border="0">
        <tr> 
          <td height="33" align="center">
			
          </td>
        </tr>
        <tr> 
          <td height="33" align="center">
			<input type="button" name="Submit" value="<bean:message key='button.ok'/>" onclick="saveSet();" Class="mybutton">
          </td>
        </tr>
        <tr>
          <td height="34" align="center">
			<input type="button" name="Submit2" value="<bean:message key='button.close'/>" onclick="window.close();" Class="mybutton">
          </td>
        </tr>
        <tr>
          <td height="46">&nbsp;</td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<script language="JavaScript">
<!--

//-->
</script>

</html:form>

