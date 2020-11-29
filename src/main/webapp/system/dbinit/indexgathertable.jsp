<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script type="text/javascript" language="javascript">
//全部选中

function sub(chk){
	n=document.all.itemtype.options.length;
	if (n>0){
		for (i=0;i<n;i++){
			document.all.itemtype.options[i].selected=chk;
		} 
	}
	return;
}
//提交

function subs(sourcebox_id){
	var left_vo,vos;
	vos= document.getElementsByName(sourcebox_id);
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

	  	hashvo.setValue("num",num);
	  	var request=new Request({method:'post',asynchronous:false,onSuccess:returnExportOk,functionId:'1020010126'},hashvo);	
}
function returnExportOk(outparameters)
	{
		var outName=outparameters.getValue("outName");
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"excel");
	}
function back(){
	window.location="/system/dbinit/fieldset_tree.jsp";
}
</script>
<html:form action="/system/dbinit/indexgather">
<table width="100%" height='100%' align="center">
	<table>
	<tr><td width='100%' >
		<tr> <td class="framestyle" valign="top">
		<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0"> 
  								<tr><td style="padding-top:5px;">
  								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="kjg.gather.xuanzeziji"/>
  								</td>
  								</tr><tr>
  								<td align="center">
  										<html:select styleId="itemtype" name="dbinitForm" property="indexid" multiple="multiple" style="height:489px;width:90%;font-size:10pt">
  											<html:optionsCollection property="indexlist"
													 label="dataName" value="dataValue" />
  								
  										</html:select>
  								</td>
  								</tr>
  								
	</table>
	</td>
	<td valign='bottom' height="35px;" align="right" style="padding-left:10px;">
  				<input type='button' value='<bean:message key="kjg.title.selectall"/>' onclick='sub(true)'  class="smallbutton" style="margin-bottom:30px;">
   			
   				<input type='button' value='<bean:message key="kjg.title.run"/>' onclick=subs('indexid')  class="smallbutton" style="margin-bottom:30px;">
   			
  				<input type='button' value='返 回' onclick='back()'  class="smallbutton">
   			</td>
	</tr>
	</table>
</html:form>
