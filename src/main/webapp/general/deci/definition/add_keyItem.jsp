<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT> 
<hrms:themes></hrms:themes>
<script language="javascript">
 
 //根据指标集取得相应的指标
 function searchItem()
 {
 	var pars="fieldSetId="+statCutlineForm.fieldSetID.value;
   	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showSelectList,functionId:'05601000018'});
 }
 
 
 function showSelectList(outparamters)
 {
 	var fielditemlist=outparamters.getValue("fielditemlist");
	AjaxBind.bind(statCutlineForm.fieldItemID,fielditemlist);
	var fielditem_vo=eval("document.statCutlineForm.fieldItemID");
	fielditem_vo.fireEvent("onchange");
 }
 
 
 
 function setCodeSetID()
 {	
 	var a=statCutlineForm.fieldItemID.value;	
 	var pars="fielditemid="+a;
   	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:setCodeSet,functionId:'05601000019'});
 }
 
 function setCodeSet(outparamters)
 {
 	var codesetid=outparamters.getValue("codeSetid");
 	statCutlineForm.defaultCodeSetID.value=codesetid;
 	statCutlineForm.codeValues.value="";
 }
 
 
 function selectCodes(codeSetID)
 {
 	 var re_vo=select_codeTree_dialog(codeSetID);
 	 if(re_vo)
	 {
	    	
	    	var tmp=re_vo.content;
	    	var len=tmp.length;
	    	if(tmp.substring(0,len-1).indexOf("root")!=-1)
	    	{
	    		alert("根目录不予选择！");
	    		return;
	    	}
	    	
	    	if(codeSetID=='UN'||codeSetID=='UM'||codeSetID=='@K')
	    	{
		    	var hashvo=new ParameterSet();
		    	hashvo.setValue("codeItemValue",tmp.substring(0,len-1)); 
				hashvo.setValue("codeSetID",codeSetID);
				hashvo.setValue("codeValue",re_vo.title);
		    	var In_paramters="flag=1"; 	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:selectCodeResult,functionId:'05601000022'},hashvo);			
	  		}
	  		else
	  		{
	  			var a_codeValues = eval("document.statCutlineForm.codeValues");//机构组织的中文显示框
				var a_codeItemValue = eval("document.statCutlineForm.codeItemValue");
	  			a_codeItemValue.value=tmp.substring(0,len-1);
	 			a_codeValues.value=re_vo.title;	
	  		}
	 }
 }
 
 
 function selectCodeResult(outparamters)
 {
 	var a_codeValues = eval("document.statCutlineForm.codeValues");//机构组织的中文显示框
	var a_codeItemValue = eval("document.statCutlineForm.codeItemValue");
 	var isTrue=outparamters.getValue("isTrue");
 	var codeValue=outparamters.getValue("codeValue");
 	var codeItemValue=outparamters.getValue("codeItemValue");
 	var codeSetID=outparamters.getValue("codeSetID");
 	if(isTrue==0)
 	{
 		if(codeSetID=='UN')
 			alert("统计指标代码值有的不是单位！");
 		if(codeSetID=='UM')
 			alert("统计指标代码值有的不是部门！");
 		if(codeSetID=='@K')
 			alert("统计指标代码值有的不是职位！");
 	}
 	else
 	{
     	a_codeItemValue.value=codeItemValue;
	 	a_codeValues.value=codeValue;	
 	}
 }
 
 
 
 
 
 
 
 function adds()
 {
 	var temp_keyFactors=returnSelectKeyFactors();
 	
 	if(temp_keyFactors.value==""||temp_keyFactors.length==0)
 	{
 		alert("请选择关键指标项！");
 		return;
 	}
 	if(statCutlineForm.a_itemname.value==""||statCutlineForm.a_itemname.value==" ")
 	{
 		alert("请输入图例名称!");
 		return;
 	}

 	if(statCutlineForm.codeValues.value==""||statCutlineForm.codeValues.value==" ")
 	{
 		alert("请选择统计指标代码值!");
 		return;
 	}
	statCutlineForm.aa_keyFactors.value=temp_keyFactors;
 	statCutlineForm.action="/general/deci/definition/statCutline/searchStatCutline.do?b_save=save";
 	statCutlineForm.submit();

 }
 
 
 
 function returnSelectKeyFactors()
 {
 	var keyFactors="";
 	for(var i=0;i<statCutlineForm.a_keyFactors.options.length;i++)
 	{
 		if(statCutlineForm.a_keyFactors.options[i].selected==true)
 			keyFactors+=","+statCutlineForm.a_keyFactors.options[i].value;
 	}
 	if(keyFactors.length==0)
 		return keyFactors;
 	else
 		return keyFactors.substring(1);
 }
 
 
 
</script>
<html:form action="/general/deci/definition/statCutline/searchStatCutline">
	<fieldset align="center" style="width:45%;">
  	  <table border="0" cellspacing="0"  align="left" cellpadding="3">
		<tr >
          <td align="right" nowrap valign="middle">                
            <bean:message key="general.defini.statCutlineName"/>     
           </td>
           <td align="left"  nowrap valign="middle">
             <html:text name="statCutlineForm" property="a_itemname" styleClass="text4" style="width:400px;"/>  
         </td>	
         </tr>
         
        <tr >
	         <td align="right" nowrap valign="middle">        
	            <bean:message key="general.defini.xname"/>   
	         </td>
	         <td align="left"  nowrap valign="middle"> 
			     <hrms:optioncollection name="statCutlineForm" property="fieldSetList" collection="list"/>
	             <html:select name="statCutlineForm" property="fieldSetID" size="1"  onchange='searchItem()' style="width:400px;">
	            	 <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	        	</html:select>
		     </td>		
        </tr>  
        <tr >
         <td align="right" nowrap valign="middle">  
                 
         </td>
         <td align="left"  nowrap valign="middle"> 
		          <hrms:optioncollection name="statCutlineForm" property="fieldItemList" collection="list" />
		             <html:select name="statCutlineForm" property="fieldItemID" size="1" onchange='setCodeSetID()' style="width:400px;">
		            	 <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        	</html:select>
		        	<html:hidden name="statCutlineForm" property="defaultCodeSetID" />
		        	
           </td>	
       </tr>  
         <tr >
        <td align="right" nowrap valign="middle">        
            <bean:message key="general.defini.code"/>      
         </td>
         <td align="left"  nowrap valign="middle">  
                 <html:text name="statCutlineForm" property="codeValues" size='21' disabled="true" styleClass="text4" style="width:400px;"/>  
                <img src="/hrms/images/code.gif" onclick="selectCodes(statCutlineForm.defaultCodeSetID.value)" align="absmiddle"/>
                <html:hidden name="statCutlineForm" property="codeItemValue"/>
                
          </td>	
       </tr>
        <tr>
        <td align="right" nowrap valign="middle">        
          <bean:message key="general.defini.keyItem"/>
         </td>
        <td align="left"  nowrap valign="middle">
                <hrms:optioncollection name="statCutlineForm" property="keyFactorList" collection="list" />
		             <html:select name="statCutlineForm" property="a_keyFactors"  multiple="true"  style="height:120px;width:400;font-size:10ptmargin-bottom:-5px;"  >
		            	 <html:options collection="list" property="dataValue" labelProperty="dataName" />
		        	</html:select>	
		    <html:hidden name="statCutlineForm" property="a_itemid" />
         	<html:hidden name="statCutlineForm" property="a_flag" />
         	<html:hidden name="statCutlineForm" property="a_typeid" />
         	<html:hidden name="statCutlineForm" property="aa_keyFactors" />	
         </td>		
        </tr>   
        <tr>
        <td align="center" colspan="2">
           <input type="button" name="b_saveb" value="<bean:message key="button.save"/>" class="mybutton" onclick="adds()"> 
           
         	 <hrms:submit styleClass="mybutton" property="b_query">
              <bean:message key="button.return"/>
	       	  </hrms:submit> 
          </td>
        </tr>
     </table>
	</fieldset>
</html:form>

<script language="javascript">

var aa_keyFactors='${statCutlineForm.aa_keyFactors}';
//var fieldsetid = "${statCutlineForm.fieldSetID}";
//var fielditemid = "${statCutlineForm.fieldItemID}";

//alert(fieldsetid);
//alert(fielditemid);

initKeyFactors();

function initKeyFactors(){
	if(aa_keyFactors !=""){
		for(var i=0; i<statCutlineForm.a_keyFactors.options.length;i++){
			if(aa_keyFactors.indexOf(statCutlineForm.a_keyFactors.options[i].value)!=-1){
				statCutlineForm.a_keyFactors.options[i].selected=true;
			}
		}
	}
	
	
}
   
</script>