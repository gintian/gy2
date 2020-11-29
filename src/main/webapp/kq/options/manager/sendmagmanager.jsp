<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>				
<%
	String url_p=SystemConfig.getCsClientServerURL(request); 
%>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/general/inform/inform.js"></script>
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
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
   if(confirm("确定保存发卡内容?"))
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
     var gObj=document.getElementById("cardno_value"); 
     var cardno_value=gObj.value; 
     if(cardno_value=="")
     {
       alert("卡号不能为空！");
       return false;
     }
     var hashvo=new ParameterSet();
	 hashvo.setValue("flag","add");	
	 hashvo.setValue("itemid_arr",itemid_arr);
	 hashvo.setValue("itemvalue_arr",itemvalue_arr);	
	 hashvo.setValue("a0100","${magCardManagerForm.a0100}");
	 hashvo.setValue("select_pre","${magCardManagerForm.select_pre}");
	 hashvo.setValue("cardno_value",cardno_value);
	 hashvo.setValue("magcard_setid","${magCardManagerForm.magcard_setid}");
	 hashvo.setValue("kq_cardno","${magCardManagerForm.kq_cardno}"); 
	 var request=new Request({method:'post',asynchronous:false,onSuccess:saveCard,functionId:'15207000074'},hashvo);
	}
}
function saveCard(outparamters)
{
   var flag=outparamters.getValue("flag");
   if(flag=="1")
   {
     alert("发卡失败！");
   }else
   {
     //magCardManagerForm.action = "/kq/options/manager/magcarddata.do?b_search=link";   
     // magCardManagerForm.target="mil_body";   
     //  magCardManagerForm.submit(); 
     var thevo=new Object();
     thevo.flag="true";
     window.returnValue=thevo;     
     window.close();  
   }
   
}
function cheackCard()
{
    var cardno_value=document.getElementById("cardno_value"); 
    var cardno_value=gObj.value; 
    if(cardno_value=="")
    {
       alert("卡号不能为空,请读卡！");
       return false;
    }
    var hashvo=new ParameterSet();
    hashvo.setValue("card_no",cardno_value); 
    hashvo.setValue("kq_cardno","${magCardManagerForm.kq_cardno}");         
    var request=new Request({method:'post',asynchronous:true,onSuccess:addSetCard,functionId:'15207000077'},hashvo);
}
function addSetCard(outparamters)
{
     var flag=outparamters.getValue("flag");
     if(flag=="1")
     {
        var message=outparamters.getValue("message");
        alert(message);
        return message;
     }else
     {
       saveSet();
     }
}
var cardvo;
function readCard()
{
	if(!AxManager.setup(null, "KqMachine", 0, 0, null, AxManager.kqmachPkgName, null, false, "<%=url_p%>"))
  		return;  	
   var obj=document.getElementById('KqMachine'); 
   if(cardvo==null)
   {
      alert("请选定读卡项文本框!");
      return false;
   }    
   if(obj!=null)
   {
      var com="${magCardManagerForm.magcard_com}";      
      var carid=obj.ReadCardID(com);          
      if(carid!="")
      {
        vos= document.getElementById("cardno_value");  
        vos.value=carid;
        cardvo.value=carid;
      }else if((carid==''))
      {
        	var hashvo=new ParameterSet();
    		hashvo.setValue("a0100s","${magCardManagerForm.a0100}");
    		hashvo.setValue("select_pres","${magCardManagerForm.select_pre}");
    		var request=new Request({method:'post',asynchronous:true,onSuccess:addcode,functionId:'15207000080'},hashvo);
        	
      }else
      {
      	alert("读卡失败");
      }
  }else
  {
     alert("读卡失败");
  }
}
function  addcode(outparamters)
{
	 var flag=outparamters.getValue("flag");
	 if(flag==''||flag==null)
	 {
	 	alert("读卡失败");
	 }else{
	 	savekahao(flag);
	 }
}
function savekahao(flag)
{
	vos= document.getElementById("cardno_value");  
    vos.value=flag;
    cardvo.value=flag;
}
function setObject(obj)
{
  cardvo=obj;
}
function   document_onactivate(){   
	setObject(document.activeElement);   
}
</script>
<style type="text/css">
#scroll_box {
           border: 1px solid #eee;
           height: 320px;    
           width: 400px;            
           overflow: auto;            
           margin: 1em 0;
       }
</style>
<html:form action="/kq/options/manager/sendmagcard">
<%int i=1;%>
<table width="100%" border="0" align="center">
  <tr> 
    <td width="90%" height="310" align="center"> 
      <table width="100%" border="0">
        <tr>
          <td height="310" valign="top"> 
          <fieldset style="width:100%;height:310">
     		 <legend>发卡</legend>
     		  &nbsp; &nbsp; <bean:write name="magCardManagerForm" property="singmess" filter="false"/>
     		  &nbsp; &nbsp;
     		 <div id="scroll_box">     		  
              <table width="100%" border="0" class="ListTable">
              <tr> 
                <td width="15%" class="TableRow" align="center">&nbsp;</td>
                <td width="30%" class="TableRow"  align="center"><bean:message key='field.label'/></td>
                <td width="55%" class="TableRow" align="center"><bean:message key='infor.menu.alert.value'/></td>
                <html:hidden name="magCardManagerForm" property="cardno_value" styleId='cardno_value'/>
              </tr>
              <logic:iterate id="element" name="magCardManagerForm" property="newfieldlist">
              <tr> 
                <td class="RecordRow" align="center" nowrap><%=i%></td>
                <td class="RecordRow"  nowrap>&nbsp;<bean:write name="element" property="itemdesc"/>&nbsp;</td>
                <logic:equal name="element" property="codesetid" value="0">
                	<logic:equal name="element" property="itemtype" value="D">
                		<td class="RecordRow"  nowrap>
                			<input type="text" name="${element.itemid}.value"  extra="editor"  onblur="timeCheck(this);" style="width:150px;font-size:10pt;text-align:left" dropDown="dropDownDate">
                		 </td>                		 
                	</logic:equal>
                	<logic:equal name="element" property="itemtype" value="N">
                		<td class="RecordRow"  nowrap>
                			<input type="text" name="${element.itemid}.value" maxlength="8" onclick="setObject(this);" onkeypress="event.returnValue=IsDigit(this);" style="width:150px;ime-mode:disabled">
                		</td>                		 
                	</logic:equal>
                	<logic:notEqual name="element" property="itemtype" value="D">
                		<logic:notEqual name="element" property="itemtype" value="N">
                			<td class="RecordRow"  nowrap>
                				<input type="text" name="${element.itemid}.value" onclick="setObject(this);" style="width:150px;">
                			</td>                			
                		 </logic:notEqual>
                	</logic:notEqual>	
                </logic:equal>
                <logic:notEqual name="element" property="codesetid" value="0">
                	<logic:equal name="element" property="codesetid" value="UN">
                		<td class="RecordRow"  nowrap>
                			<input type="hidden" name="companyid" value="${element.itemid}">
                			<input type="hidden" name="${element.itemid}.value" onchange="changepos('UN');orgchngeinfo(this,1);">
                			<input type="text" name="${element.itemid}.hzvalue" style="width:150px;" onchange="fieldcode(this,1);" readOnly>
                			<img  src="/images/code.gif" onclick='openOrgInfo("${element.codesetid}","${element.itemid}.hzvalue",1,"1");'/>&nbsp;
                		</td>                		
                	</logic:equal>
                	<logic:equal name="element" property="codesetid" value="UM">
                		<td class="RecordRow"  nowrap>
                			<input type="hidden" name="depid" value="${element.itemid}">
                			<input type="hidden" name="${element.itemid}.value" onchange="changepos('UM');orgchngeinfo(this,2);">
                			<input type="text" name="${element.itemid}.hzvalue" style="width:150px;" onchange="fieldcode(this,1);" readOnly>
                			<img  src="/images/code.gif" onclick='openOrgInfo("${element.codesetid}","${element.itemid}.hzvalue",2,"2");'/>&nbsp;
                		</td>                		
                	</logic:equal>
                	<logic:equal name="element" property="codesetid" value="@K">
                		<td class="RecordRow"  nowrap>
                			<input type="hidden" name="jobid" value="${element.itemid}">
                			<input type="hidden" name="${element.itemid}.value" onchange="changepos('@K');orgchngeinfo(this,3);">
                			<input type="text" name="${element.itemid}.hzvalue" style="width:150px;" onchange="fieldcode(this,1);" readOnly>
                			<img  src="/images/code.gif" onclick='openOrgInfo("${element.codesetid}","${element.itemid}.hzvalue",3,"2");'/>&nbsp;
                		</td>
                		
                	</logic:equal>
                	<logic:notEqual name="element" property="codesetid" value="UN">
                		<logic:notEqual name="element" property="codesetid" value="UM">
                			<logic:notEqual name="element" property="codesetid" value="@K">
                				<td class="RecordRow"  nowrap>
                					<input type="hidden" name="${element.itemid}.value">
                					<input type="text"  name="${element.itemid}.hzvalue" style="width:150px;" readOnly>
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
			<input type="button" name="Submit" value="读卡" onclick="readCard();" Class="mybutton">
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
</html:form>
<script language="JavaScript">
<!--
document_onactivate();
//-->
</script>