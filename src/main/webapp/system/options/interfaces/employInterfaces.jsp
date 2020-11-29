<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    

  </head>
<script type="text/javascript" language="javascript">
function getbasefield(mytarget){
	var return_vo= window.showModalDialog("/system/options/otherparam/global_employeeitemtree.do?b_query=link&param=root&froms=db&name=usr&input=2", false, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");  
   if(return_vo==null){
     return;
   }else
   {
     var codeitemid,codetext;
     codeitemid=return_vo.codeitemid;
     codetext=return_vo.codetext;
     var oldInputs=document.getElementsByName(mytarget);
     var oldobj=oldInputs[0];  
     target_name=oldobj.name;
     var hidden_name=target_name.replace(".viewvalue",".value");
     var hiddenInputs=document.getElementsByName(hidden_name);
     if(hiddenInputs!=null)
     {
    	hiddenobj=hiddenInputs[0];
    	hiddenobj.value=codeitemid;    	
     }
     oldobj.value=codetext;
   }
}
function save()
{
//setInterfacesForm.action="/system/options/interfaces/employInterfaces.do?b_save=link";
//setInterfacesForm.submit();
setInterfacesForm.action="/system/options/interfaces/setInterfaces.do?b_save=link";
setInterfacesForm.submit();
}
function checkBoximpmode(obj) { 
    if(obj.checked){
    	document.setInterfacesForm.impmode.value="1";
    }else{
    	document.setInterfacesForm.impmode.value="0";
    }
}
function checkBoxexpmode(obj) { 
    if(obj.checked){
    	document.setInterfacesForm.expmode.value="1";
    }else{
    	document.setInterfacesForm.expmode.value="0";
    }
}
function clement(value)
{
	if(value=="1")
	{
		if(value1.style.display=="none")
		{
			value1.style.display="block";
			return;
		}else if(value1.style.display=="block")
		{
			value1.style.display="none";
			return;
		}
	}
	if(value=="2")
	{
		if(value2.style.display=="none")
		{
			value2.style.display="block";
			return;
		}else if(value2.style.display=="block")
		{
			value2.style.display="none";
			return;
		}
	}
}
function getorgbasefield(mytarget)
{
	var return_vo= window.showModalDialog("/system/options/interfaces/orgInterfaces.do?b_tree=link&param=root&froms=db&name=org&input=2", false, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo==null){
     return;
   }else
   {
     var codeitemid,codetext;
     codeitemid=return_vo.codeitemid;
     codetext=return_vo.codetext;
     var oldInputs=document.getElementsByName(mytarget);
     var oldobj=oldInputs[0];  
     target_name=oldobj.name;
     var hidden_name=target_name.replace(".viewvalue",".value");
     var hiddenInputs=document.getElementsByName(hidden_name);
     if(hiddenInputs!=null)
     {
    	hiddenobj=hiddenInputs[0];
    	hiddenobj.value=codeitemid;    	
     }
     oldobj.value=codetext;
   }
} 
</script>
  <body>
 <html:form action="/system/options/interfaces/setInterfaces">
    <table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
    <tr>
    	<td align="left"  nowrap>
    		<input type="button" name="returnbutton"  value="人员指标" class="mybutton" onclick="clement('1');"> 
    	</td>
    </tr>
    <tr>
    	<td align="left"  nowrap>
    		<div id="value1" style="display:block;">
    		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
    	<thead>     
           <tr>
                      
            <td align="center" class="TableRow" colspan="2" nowrap>
		        &nbsp;人员指标&nbsp;
	         </td>    	    		        	        	        
           </tr>
   	    </thead>
   	       <tr>
                      
            <td align="center" class="RecordRow"  nowrap>
		        &nbsp;人员关联指标&nbsp;
	         </td> 
	         <td align="left" class="RecordRow"  nowrap>
		        &nbsp;&nbsp;<html:select name="setInterfacesForm" property="chitemid" size="0">
								<html:optionsCollection property="chklist" value="dataValue" label="dataName"/>
							</html:select>
	         </td>   	    		        	        	        
           </tr>
           <logic:iterate id="element" name="setInterfacesForm"  property="fielditemlist" indexId="index"> 
               <tr>    
                 <td align="center" class="RecordRow" nowrap>                 
                  &nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;                   
                 </td>
                 <td align="left" class="RecordRow" nowrap>         
                 &nbsp;
                 <html:text name="setInterfacesForm" property='<%="fielditemlist["+index+"].viewvalue"%>'  styleClass="textColorWrite"/>   
                 <html:hidden name="setInterfacesForm" property='<%="fielditemlist["+index+"].value"%>' />     
                  <img src="/images/code.gif" onclick='javascript:getbasefield("<%="fielditemlist["+index+"].viewvalue"%>");' />
                  
                  &nbsp;                   
                 </td>
               </tr>
           </logic:iterate> 
           </table>
    </div>
    	</td>
    </tr>
    <tr>
    	<td align="left"  nowrap>
    		<input type="button" name="returnbutton"  value="组织机构指标" class="mybutton" onclick="clement('2');"> 
    	</td>
    </tr>
    <tr>
    	<td align="left"  nowrap>
    		<div id="value2" style="display:block;">
    			<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
    				<thead>     
           				<tr>
            				<td align="center" class="TableRow" colspan="2" nowrap>
		        				&nbsp;组织机构&nbsp;
	         				</td>    	    		        	        	        
          				</tr>
   	    			</thead>
           			<logic:iterate id="element" name="setInterfacesForm"  property="orgitemlist" indexId="index"> 
               <tr>    
                 <td align="center" class="RecordRow" nowrap>                 
                  &nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;                   
                 </td>
                 <td align="left" class="RecordRow" nowrap>         
                 &nbsp;
                 <html:text name="setInterfacesForm" property='<%="orgitemlist["+index+"].viewvalue"%>'  styleClass="TEXT6"/>   
                 <html:hidden name="setInterfacesForm" property='<%="orgitemlist["+index+"].value"%>' />     
                 <img src="/images/code.gif" onclick='javascript:getorgbasefield("<%="orgitemlist["+index+"].viewvalue"%>");' /> 
                  &nbsp;                   
                 </td>
               </tr>
           </logic:iterate>
    			</table>
    		</div>
    	</td>
    </tr>
            <tr>
            <td  align="left" class="RecordRow" colspan="2" nowrap>
		       &nbsp;&nbsp;其他参数
	         </td>    	    		        	        	        
           </tr>
           <tr>                      
            <td  align="left"  class="RecordRow" colspan="2" nowrap>
		        &nbsp;&nbsp;
		       <logic:equal name="setInterfacesForm" property="impmode" value="1">
                   			<input type="checkbox" name="boximpmode" onclick="checkBoximpmode(this);" checked/> 
                   		</logic:equal>
                   		<logic:notEqual name="setInterfacesForm" property="impmode" value="1">
                   			<input type="checkbox" name="boximpmode" onclick="checkBoximpmode(this);"/> 
                   		</logic:notEqual> 导入时是否进行代码转换
                   		<html:hidden name="setInterfacesForm" property="impmode"/>
                   		&nbsp;&nbsp;<logic:equal name="setInterfacesForm" property="expmode" value="1">
                   			<input type="checkbox" name="boxexpmode" onclick="checkBoxexpmode(this);" checked/> 
                   		</logic:equal>
                   		<logic:notEqual name="setInterfacesForm" property="expmode" value="1">
                   			<input type="checkbox" name="boxexpmode" onclick="checkBoxexpmode(this);"/> 
                   		</logic:notEqual>
                   		<html:hidden name="setInterfacesForm" property="expmode"/>
	                      导出时是否进行代码转换      		        	        	        
           </tr>
      </table>
       <table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0">
         <tr>
           <td>
              <input type="button" name="returnbutton"  value="<bean:message key="button.save"/>" class="mybutton" onclick="save();">                 
                       
           </td>
         </tr>
       </table>
  </html:form>
  </body>
</html>
