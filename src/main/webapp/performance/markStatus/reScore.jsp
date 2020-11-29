<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function selectOK()
{
  var obj=document.getElementById("sid");
  var num=0;
  var ids;
  if(obj)
  {
     for(var i=0;i<obj.options.length;i++)
     {
        if(obj.options[i].selected)
        {
          num++;
          ids+="`"+obj.options[i].value;
        }
     }
  }
  if(num==0)
  {
     alert("请选择考核对象！");
     return;
  }
  var hashVo=new ParameterSet();
  hashVo.setValue("id",ids.substring(1));
  var request=new Request({method:'post',asynchronous:false,onSuccess:setOK,functionId:'90100170007'},hashVo);			 
}
function closeWin(){
	if(window.showModalDialog)
		parent.window.close();
	  else
	  	parent.parent.Ext.getCmp("reScoreWin").close();
}
function setOK(outparameters)
{
  alert("操作成功！");
  if(window.showModalDialog)
 	window.parent.returnValue="1"; //此处非兼容模式要求严格，因为有两层iframe 所以 window 应该改为window.parent haosl
  else
  	parent.parent.openFlag('1');
    closeWin();
}
//-->
</script>
<html:form action="/performance/markStatus/reScore">
<table width='381' border="0" cellspacing="0" style="margin-top:5px;margin-left:4px;" align="center" cellpadding="0" class="ListTable">
<thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
		<bean:message key="jx.datacol.khobj"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr>
            <td width="100%" style='padding-bottom:4px;' align="center" class="RecordRow" nowrap>
            
            <hrms:optioncollection name="markStatusForm" property="submitList" collection="list" />
		             <html:select styleId="sid" name="markStatusForm" property="submitid" multiple="multiple" size="10"  style="width:100%;margin-top:2px">
		             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>		
            </td>
            </tr>
            <tr style="height:30px;">
            <td align="center" style="padding-top:6px;">
            <input type="button" name="okk" value="<bean:message key="button.ok"/>" class="mybutton" onclick="selectOK();"/>
            <input type="button" name="calcell" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="closeWin();"/>
            </td>
            </tr>
            
</table>
</html:form>
