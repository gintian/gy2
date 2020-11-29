<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">

	function to_save()
	{
		var name = mInformForm.filetitle.value;
		if(ltrim(rtrim(name)) == "")
		{
			alert('名称不能为空！');
			return;
		}

		var hashvo=new ParameterSet();
		hashvo.setValue("a0100",mInformForm.a0100.value);
		hashvo.setValue("i9999",mInformForm.i9999.value);
		hashvo.setValue("kind",mInformForm.kind.value);
		hashvo.setValue("dbname",mInformForm.dbname.value);
		hashvo.setValue("filetitle",mInformForm.filetitle.value);
		var request=new Request({method:'post',asynchronous:false,onSuccess:afterSave,functionId:'1010094021'},hashvo);
	}
	function afterSave(outparamters)
	{
		//var flag=outparamters.getValue("flag");
		//if(flag=='1')
		//{
			alert('保存成功');
			var thevo=new Object();
			thevo.flag="true";
			window.returnValue=thevo;
			window.close();
		//}
	}
	function   NoExec()   
  	{   
          if(event.keyCode==13||event.keyCode==222)   event.returnValue=false; 
          document.onkeypress=NoExec;     
  	} 
</script>
<html:form action="/general/inform/emp/view/opermultimedia">
<br>
<br>
<html:hidden name="mInformForm" property="a0100"/>
<html:hidden name="mInformForm" property="i9999"/>
<html:hidden name="mInformForm" property="kind"/>
<html:hidden name="mInformForm" property="dbname"/>
<table width="350" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
   <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="general.mediainfo.mediaedit"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="300"></td>--> 
       		<td width=130 align="left" colspan="2" class="TableRow">&nbsp;<bean:message key="general.mediainfo.mediaedit"/>&nbsp;</td>             	      
  </tr>  
   <tr>
                <td align="right">
                  <bean:message key="general.mediainfo.title"/>&nbsp;
                </td>
               <td align="left">
                  <html:text   name="mInformForm" property="filetitle"  styleClass="textborder" maxlength="20" size="25" onkeydown="NoExec()"/>
               </td>
             </tr> 
             <tr>
                <td colspan="2" align="center" style="height:35px;">
                <html:button styleClass="mybutton" property="b_save" onclick="to_save();">
		      		<bean:message key="button.save"/>
			   </html:button>
               <html:button styleClass="mybutton" property="b_return" onclick="window.close();">
		      		<bean:message key="button.return"/>
			   </html:button>	
                </td>
            </tr>
           
  </table>
</html:form>

