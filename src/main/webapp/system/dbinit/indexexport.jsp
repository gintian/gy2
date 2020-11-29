<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<hrms:themes></hrms:themes>
<script language='javascript' >
function goback(){
	document.dbinitForm.action="/system/dbinit/inforlist.do?b_query=link";
  	document.dbinitForm.submit();
}
function saveSort(obj){
	var left_vo,vos;
	vos= document.getElementsByName(obj);
	  if(vos==null)
	  		return false;
	  left_vo=vos[0];
	  var set="";
	  var num=0;
	  	for(i=0;i<left_vo.options.length;i++)
	  		{
	  	 		if(left_vo.options[i].selected)
		    		{
		    			set+="/"+left_vo.options[i].value;
		    			num++;
		    			
		    		}
	  		}
	  		if(num==0)
	  		{
	  		 alert(KJG_ZBTX_INFO4); 
	  		 return;     
	  		}
	  		var hashvo=new ParameterSet();
	  		hashvo.setValue("set",set.substring(1));
	  		
	  		var request=new Request({method:'post',asynchronous:false,onSuccess:showOut,functionId:'1020010128'},hashvo);
}
function showOut(outparamters)
{
     var url=outparamters.getValue("file");	     
	 if(url!=""){
		 //xus 20/4/29 vfs 改造
		 var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url,"excel"); 
//	 var win=open("/servlet/DisplayOleContent?filename="+url,"excel");
	 }
	
}

</script>


<html:form action="/system/dbinit/inforlist">
<table width="60%" height="600" border="0" align="center">
  <tr> 
    <td>
    	<fieldset style="width:100%;height:600">
    	<legend><bean:message key='system.param.sysinfosort.selsubset'/></legend>
    	<table width="100%"  border="0" align="center">
    	<tr>
    		<td width="100%" valign="middle" align="right">
    			<html:select name="dbinitForm" property="indexexport" multiple="multiple"  style="height:560px;width:100%;font-size:10pt">
                         <html:optionsCollection property="exportlist" value="dataValue" label="dataName"/>   		      
 		     	</html:select>
    		</td>
    		<td>
    			<table width="100%"  border="0" align="center" height="560">
    				<tr height="400">
    					<td>&nbsp;</td>
    				</tr>
    				<tr>
    					<td>&nbsp;</td>
    				</tr>
    				<tr>
    					<td align="center"><input type="button" value="<bean:message key='menu.out.label'/>" onclick=saveSort('indexexport') Class="mybutton"></td>
    				</tr>
    				<tr>
    					<td align="center"><input type="button" value="<bean:message key='button.return'/>" onclick="goback()" Class="mybutton"></td>
    				</tr>
    			</table>
    		</td>
    	</tr>
    	</table>
    	 </fieldset>
    </td>
  </tr>
</table>
</center>
</html:form>
