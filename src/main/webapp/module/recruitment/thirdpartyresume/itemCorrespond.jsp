<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%> 
<%
	int i=0;
%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<style type="text/css">

</style>
<script language='javascript'>
function change(num,obj){

	document.getElementById("itemlist["+num+"].iselected").value=obj.value;
	if(obj.value==""){
	var checkbox = document.getElementsByName("checkbox")[num].checked;
	if(checkbox){
		document.getElementsByName("checkbox")[num].checked = false;
	}
	}
}

	//设置人员库指标对应
	function setEhrfld(obj)
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("name",obj.name);
		hashvo.setValue("item",obj.value); 
		hashvo.setValue("resumeset","${importResumeForm.resumeset}")
	   	var request=new Request({method:'post',asynchronous:false,onSuccess:returnInfoRefresh,functionId:'3000000266'},hashvo);
	}
	function returnInfoRefresh(outparameters)
	{

alert("保存成功");
	}
	
	function initOnly()
	{
	var checkbox = document.getElementsByName("checkbox");
	if(checkbox)
	{
	       for(var i=0;i<checkbox.length;i++)
           {
              if(checkbox[i].checked)
                 checkbox[i].checked=true;
              else
                 checkbox[i].checked=false;
           }
	}
	}
	
	function Savevalid() {
		var baseitems = new Array();
		var itemvalues = new Array();
		var resumefldids = new Array();
		var n=0
		<logic:iterate id="element" name="importResumeForm" property="itemlist" indexId="index" >
			n++;
			var baseitem = '<bean:write name="element" property="resumefld" filter="true"/>';

			var itemvalue = document.getElementById('<%="itemlist["+index+"].iselected"%>').value;
			var resumefldid = document.getElementById('<%="itemlist["+index+"].resumefldid"%>').value;
			baseitems[n-1]=baseitem;
			itemvalues[n-1]=itemvalue;
			resumefldids[n-1]=resumefldid;
		</logic:iterate>
		var s = itemvalues.join(",")+",";
	
		for(var j=0;j<itemvalues.length;j++){
			if(s.replace(itemvalues[j]+",","").indexOf(itemvalues[j]+",")>-1&&itemvalues[j]!=""){
				alert("对应人员库指标项不能重复!");
				return;
			}
		}
		
		var hashvo=new ParameterSet();
		hashvo.setValue("baseitems",baseitems);
		hashvo.setValue("itemvalues",itemvalues);
		hashvo.setValue("resumefldids",resumefldids);
		hashvo.setValue("resumeset","${importResumeForm.resumeset}");
		var request=new Request({method:'post',asynchronous:false,onSuccess:returnInfoRefresh,functionId:'ZP0000002607'},hashvo);
	
	}
	
	function selectAll(){
	var checkbox = document.getElementsByName("checkbox");
		for(var i=0;i<checkbox.length;i++)
        {
           checkbox[i].checked=true;
        }
	
	}
	
	function resetAll(){
	var checkbox = document.getElementsByName("checkbox");
		for(var i=0;i<checkbox.length;i++)
        {
           checkbox[i].checked=false;
        }
	
	
	}
		function codeCorrespond(){
		importResumeForm.action="/module/recruitment/thirdpartyresume.do?b_codeCorrespond=link&from_flag=2&fieldSet="+'${importResumeForm.fieldSet}'
	///var url="/hire/employActualize/employResumeImport.do?b_codeCorrespond=link&from_flag=2&fieldSet="+'${importResumeForm.fieldSet}'
			///window.showModalDialog(url,"","dialogWidth:800px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");
		importResumeForm.submit(); 	
	
	
	}
	function All(obj){
	   if(obj.checked==false){//这个状态在执行onclick的时候把checkbox的状态已经更改了
	       resetAll();
	       obj.setAttribute("title","全选");
	   }else{
	       selectAll();
	       obj.setAttribute("title","全撤");
	   }
	}
	function back(){
	var  url='/module/recruitment/thirdpartyresume.do?b_defineScheme=link';
	importResumeForm.action=url;
	importResumeForm.submit(); 
	}
</script>

<base id="mybase" target="_self">

<html:form action="/module/recruitment/thirdpartyresume">
<div style="margin-top:50px;">
	<fieldset align="center" style="width:45%;">
    <legend ><bean:message key="zp.resumeImport.itemCorrespond"/></legend>
	<table align='center' width="85%">
		<tr>
			<td colspan="4" >
				<table width="98%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
					<thead>
						<tr>
							<td align="center" class="TableRow" nowrap>
							<bean:message key="zp.resumeImport.baseItem"/>
							</td>
							<td align="center" class="TableRow" nowrap>
							<bean:message key="zp.resumeImport.fielditem"/>
							</td>
						<!--  	<td align="center" class="TableRow" nowrap>
							<bean:message key="zp.resumeImport.sureImport"/>
							<logic:equal name="importResumeForm" property="validAll" value="true">
							     <input id="allmodify" type="checkbox" checked onclick="All(this);" title="全撤" >
							</logic:equal> 
							<logic:notEqual name="importResumeForm" property="validAll" value="true">
							       <input id="allmodify" type="checkbox" onclick="All(this);" title="全选">
							</logic:notEqual>
							</td>-->
						</tr>
					</thead>
					<logic:iterate id="element" name="importResumeForm" property="itemlist" indexId="index" >
          			<%
          			if(i%2==0)
          			{
          			%>
          			<tr class="trShallow">
          			<%}
          			else
          			{%>
          			<tr class="trDeep">
          			<%
          			}
          			i++;          
          			%>
          			<td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="resumefld" filter="true"/>
	    			</td>
	    			<td align="left" class="RecordRow" nowrap>
				     <html:select name="element" property="iselected" size="1"  style="width:100%"  onchange='<%="change("+index+",this)"%>'>
			  	  		<html:optionsCollection  name="importResumeForm" property="ilist" value="dataValue" label="dataName" />
					 </html:select>
					<input type='hidden' name='<%="itemlist["+index+"].iselected"%>'    value="<bean:write name="element" property="iselected" filter="true"/>"   />  
					<input type='hidden' name='<%="itemlist["+index+"].resumefldid"%>'    value="<bean:write name="element" property="resumefldid" filter="true"/>"   />

	    			</td>
            		<!--  <td align="center" class="RecordRow" nowrap>
					<bean:write name="element" property="valid" filter="false"/>
	    			</td> -->
          			</tr>
          			</logic:iterate>
				</table>
			</td>
		</tr>
	</table>
	<br/>
</fieldset>
<table  width="50%" align="center" style="margin-top:0px;">
          <tr >
            <td nowrap align="center">   
            <input type="button" value="<bean:message key="jx.eval.codeaccord"/>" class="mybutton" onclick="codeCorrespond()" />
        <input type="button" value="<bean:message key="button.save"/>" class="mybutton" onclick="Savevalid()" />
        <input type="button" class="mybutton" value="<bean:message key="button.return"/>" onclick="back()" />
        <!--      
        <input type="button" value="<bean:message key="button.all.select"/>" class="mybutton" onclick="selectAll();" />
        <input type="button" value="<bean:message key="button.all.reset"/>" class="mybutton" onclick="resetAll();" />
        -->
      <!--    <hrms:submit styleClass="mybutton" property="br_return" >
                    <bean:message key="button.return"/>
        </hrms:submit>   -->  
        
            </td>
          </tr>       
</table>
</div>
<script language="JavaScript">
initOnly();
</script>
</html:form>