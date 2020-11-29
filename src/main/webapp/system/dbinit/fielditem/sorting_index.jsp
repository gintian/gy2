<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.sys.dbinit.DbinitForm"%>

<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
function saveSort(){
	var hashvo=new ParameterSet();
	var setid = document.getElementsByName('setid')[0].value;
	// var setid = document.getElementById("setid").value;
	hashvo.setValue("setid",setid);
	hashvo.setValue("displayid",selectTostr('sort_fields'));
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:showFieldList,functionId:'1020010119'},hashvo);
}
function showFieldList(outparamters){
		var base=outparamters.getValue("info");
		if(base=='ok'){
			// window.returnValue="ok";
			// window.close();
			parent.return_vo = "ok";
			winClose();
		}else{
			alert(KJG_ZBTX_INF26);
		}
}
function selectTostr(listbox){
  var vos,right_vo,i,str='';
  vos= document.getElementsByName(listbox);
  if(vos==null || vos[0].length==0){
  	return;  	
 	vos[0].options[0].selected=false;

  }
  //设为要可选状态
  right_vo=vos[0];  
  for(i=0;i<right_vo.options.length;i++){
	str += right_vo.options[i].value+",";
  }
  return str;  	
}
function winClose(){
    if(parent.Ext.getCmp('indexSorting')){
        parent.Ext.getCmp('indexSorting').close();
	}

}
</script>
<base target="_self">
<html:form action="/system/dbinit/fielditemlist">
<center>
    	<table width="390"  border="0" align="center">
    	
    	<tr>
    		<td width="100%" align="center">
    		<html:hidden name="dbinitForm" property="setid"/>
    		<fieldset style="width:100%;height:350;">
    	<%
    		DbinitForm dbinitForm = (DbinitForm)session.getAttribute("dbinitForm");
    		String setid = dbinitForm.getSetid();
    		setid=setid!=null&&setid.trim().length()>0?setid:"";
    		if(setid.length()==1){
    	 %>
    	<legend>调整子集顺序</legend>
    	<%}else{ %>
    	<legend><bean:message key="menu.gz.sortitem"/></legend>
    	<%} %>
    			<html:select name="dbinitForm" property="sort_fields" multiple="multiple" ondblclick="removeitem('sort_fields');" style="height:90%;width:90%;font-size:10pt">
                         <html:optionsCollection property="sortlist" value="dataValue" label="dataName"/>   		      
 		     	</html:select>
 		   </fieldset>
    		</td>
    		<td>
    			<table width="100%"  border="0" align="center" height="240">
    				<tr height="100">
    					<td>&nbsp;</td>
    				</tr>
    				<tr>
    					<td>&nbsp;</td>
    				</tr>
    				<tr>
    					<td align="right">
							<html:button  styleClass="smallbutton" property="b_up" onclick="upItem($('sort_fields'));">
            		     		<bean:message key="button.previous"/> 
	           				</html:button >
						</td>
    				</tr>
    				<tr>
    					<td align="right" height="35px;" style="padding-top:30px;">
							<html:button  styleClass="smallbutton" property="b_down" onclick="downItem($('sort_fields'));">
            		     		<bean:message key="button.next"/>    
	           				</html:button >	 
						</td>
    				</tr>
    				<tr>
    					<td>&nbsp;</td>
    				</tr>
    				<tr>
    					<td>&nbsp;</td>
    				</tr>
    			</table>
    		</td>
    	</tr>
    	<tr>
    		<td align="center" colspan="2" height="35px;">
    		<input type="button" value="<bean:message key='button.ok'/>" onclick="saveSort();" Class="mybutton">
    		<%--<input type="button" value="<bean:message key='button.close'/>" onclick="window.close();" Class="mybutton"></td>--%>
    		<input type="button" value="<bean:message key='button.close'/>" onclick="winClose();" Class="mybutton"></td>
    	</tr>
    	</table>
</center>
</html:form>
<script language="JavaScript">
	if(!getBrowseVersion()||getBrowseVersion()=='10'){
		document.getElementsByTagName('fieldset')[0].style.width = '94%';
	}
</script>