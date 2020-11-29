<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.hire.innerEmployNetPortal.InnerEmployPortalForm,
			     org.apache.commons.beanutils.LazyDynaBean,com.hrms.struts.taglib.CommonData,
			     java.util.*,com.hrms.hjsj.sys.ResourceFactory"%>
<%
  InnerEmployPortalForm innerEmployPortalForm=(InnerEmployPortalForm)session.getAttribute("innerEmployPortalForm");
  ArrayList mediaList=innerEmployPortalForm.getMediaList();
  String flag = innerEmployPortalForm.getFlag();
  String alertMessage = innerEmployPortalForm.getAlertMessage();
 %>
<script type="text/javascript">
<!--

<%if(flag.equals("7")){
  out.print("var alt='"+innerEmployPortalForm.getAlertMessage()+"';");
  out.print("alert(getDecodeStr(alt));");
  out.print("window.history.back();");
}%>
function deleteattach()
{
    var obj = document.getElementsByName("ids");
    var obj_a = document.getElementsByName("aid");
    var obj_i = document.getElementsByName("iid");
    var obj_n = document.getElementsByName("nid");
    var  num=0;
    var ids = "";
    for(var i=0;i<obj.length;i++)
    {
       if(obj[i].checked)
       {
          num++;
          ids+="/"+obj_a[i].value+"`"+obj_i[i].value+"`"+obj_n[i].value;
       }
    }
    if(num==0)
    {
       alert("请选择要删除的项目!");
       return;
    }
     if(confirm("确认删除？"))
     {
         var hashvo=new ParameterSet();
         hashvo.setValue("ids",getEncodeStr(ids));
		 var request=new Request({method:'post',asynchronous:false,onSuccess:delete_ok,functionId:'90100170015'},hashvo);
         
     }
}
function delete_ok(outparameters)
{
   alert("删除成功！");
    document.innerEmployPortalForm.action="/hire/innerEmployNetPortal/initInnerEmployPos.do?b_attach=link";
	document.innerEmployPortalForm.submit();
}

function go_bck()
{
    document.innerEmployPortalForm.action="/hire/innerEmployNetPortal/initInnerEmployPos.do?b_query=link";
	document.innerEmployPortalForm.submit();
}
function allChange(obj)
{
   var a_obj=document.getElementsByName("ids");
   for(var i=0;i<a_obj.length;i++)
   {
      if(obj.checked)
      {
          a_obj[i].checked=true;
      }else
      {
         a_obj[i].checked=false;
      }
   }
}
function NewUp(i9999,type)
{
   var a0100 = document.getElementById("zpkA0100").value;  
   var dbname = document.getElementById("zpDbName").value;  
   var thecodeurl="/hire/innerEmployNetPortal/initInnerEmployPos.do?b_add=link`type="+type+"`i9999="+i9999+"`a0100="+a0100+"`dbname="+dbname; 
   var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
   var values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:540px; dialogHeight:210px;resizable:no;center:yes;scroll:yes;status:no");			
   if(values!=null&&values=='1')
   {
      document.innerEmployPortalForm.action="/hire/innerEmployNetPortal/initInnerEmployPos.do?b_attach=link";
      document.innerEmployPortalForm.submit();
   }
}
//-->
</script>

 <html:form action="/hire/innerEmployNetPortal/initInnerEmployPos"> <br><br><br>
 <logic:equal value="1" name="innerEmployPortalForm" property="isSelfUser">
 <table width="55%" border="0" cellpadding="0" cellspacing="0" align="center" class="ListTable">
 <html:hidden name="innerEmployPortalForm" property="zpkA0100"/>
 <html:hidden name="innerEmployPortalForm" property="zpDbName"/>
<tr>
<td align="left" width="2%">&nbsp;</td><td align="left">
<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" class="ListTable">
  <tr  align='center' > 
                        <td class='TableRow' width='10%' nowrap>
                      		<input type="checkbox" name="allids" value="" onclick="allChange(this);"/>     		        			
                      	</td>
                     	<td class='TableRow' width="70%" nowrap>
                      		<bean:message key="column.law_base.filename"/>         			
                      	</td>
                          <td class='TableRow' width="20%" nowrap>
                      		<bean:message key="label.edit"/>         			
                      	</td>
                      	</tr>
<logic:iterate id="element" name="innerEmployPortalForm" property="attachList">
  <tr  align='center' > 
                        <td class='RecordRow' nowrap>
                      		<input type="checkbox" name="ids" value=""/>
                      		<input type="hidden" name="aid" value="<bean:write name="element" property="a0100"/>"/>  
                      		<input type="hidden" name="iid" value="<bean:write name="element" property="i9999"/>"/>  	
                      		<input type="hidden" name="nid" value="<bean:write name="element" property="nbase"/>"/>  				
                      	</td>
                     	<td class='RecordRow' align="left" nowrap>
                      		&nbsp;&nbsp;<bean:write name="element" property="title"/>     		
                      	</td>
                          <td class='RecordRow' nowrap>
                      		<img src='/images/edit.gif' title='修改' border='0' style='cursor:hand' onclick="NewUp('<bean:write name="element" property="i9999"/>','1')"/>     			
                      	</td>
                      	</tr>
</logic:iterate>
</table>
</td>
</tr>
<tr><td align="left" width="2%">&nbsp;</td><td align="left" style="padding-top:3px">
<input  type="button" name="n" value="<bean:message key="button.insert"/>" class="mybutton" onclick="NewUp('0','0');"/>
<input  type="button" name="d" value="<bean:message key="button.delete"/>" class="mybutton" onclick="deleteattach();"/>
<input  type="button" name="bck" value="<bean:message key="button.return"/>" class="mybutton" onclick="go_bck();"/>
</td></tr>
 </table>
 </logic:equal>
 <logic:equal value="0" name="innerEmployPortalForm" property="isSelfUser">
 <p align="center">业务用户且没关联自助用户，不能上传简历附件.</p>
 <p align="center"><input  type="button" name="bck" value="<bean:message key="button.return"/>" class="mybutton" onclick="window.history.back();"/>
 </logic:equal>
 </html:form>
