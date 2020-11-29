<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>
<html>
  <head>
    
  </head>
  <style type="text/css">
	
#scroll_box {
	           border: 0px solid #ccc;
	           height: 460px;    
	           width: 100%;            
	           overflow: auto;            
	           margin: 0em 0;
	       }
.TableRow_2lock {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: 0pt; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:30px;
	font-weight: bold;
	background-color:#f4f7f7;	
	valign:middle;
	position: relative;
	top: expression(document.getElementById("scroll_box").scrollTop); /*IE5+ only*/
}
.TableRow_2lock_blt {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: 0pt; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: 0pt; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	valign:middle;
	position: relative;
	top: expression(document.getElementById("scroll_box").scrollTop); /*IE5+ only*/
}
.notops{
	border-top: none;
}
.noleft{
	border-left: none;
}
.noright{
	border-right: none;
}
.notopleft{
	border-top: none;
	border-left: none;
}
	</style>
  <hrms:themes />
  <script language='javascript'>
  var desc=GZ_ACCOUNTING_GZ;
<logic:equal name="gztemplateSetForm" property="gz_module"   value="1">
	desc=GZ_ACCOUNTING_POLICY;
</logic:equal>
  function goback()
  {
  	document.gztemplateSetForm.action="/gz/templateset/gz_templatelist.do?b_query=link2";
  	document.gztemplateSetForm.submit();
  }
  
  function del()
  {
  	   var num=0;
  	   for(var i=0;i<document.gztemplateSetForm.elements.length;i++)
  	   {
  			if(document.gztemplateSetForm.elements[i].type=='checkbox')
  			{
  				if(document.gztemplateSetForm.elements[i].checked==true)
  				{
  					num++;
  				}
  			}
  		}
  		if(num>0)
  		{
  			if(confirm(GZ_ACCOUNTING_INFO2+desc+GZ_ACCOUNTING_ITEM+"?"))
  			{
  				document.gztemplateSetForm.action="/gz/templateset/salaryItem.do?b_del=link";
  				document.gztemplateSetForm.submit();
  			}
  		}
  		else
  		{
  			alert(GZ_ACCOUNTING_IFNO3+desc+GZ_ACCOUNTING_ITEM+"！");
  		}
  }
  
  
  function add()
  {
  		var arguments=new Array("");     
	    var strurl="/gz/templateset/salaryItem.do?br_add=link";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
	    var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=540px;dialogHeight=380px;resizable=yes;scroll=no;status=no;");
  		if(ss&&ss=='1')
  		{
  			document.gztemplateSetForm.action="/gz/templateset/salaryItem.do?b_query=query&salaryid=${gztemplateSetForm.salaryid}";
  			document.gztemplateSetForm.submit();
  		}
  }
  
  
  
  function changeValue(obj)
  {
  //	alert(obj.name+"  "+obj.value);
  	if(obj.value==0)   //输入项
  	{
  		var hashvo=new ParameterSet();	
	  	var In_paramters="flag="+obj.value;  
	  	hashvo.setValue("fieldid",obj.name.split("/")[1]);
	  	hashvo.setValue("salaryid",'${gztemplateSetForm.salaryid}');	
	    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfoAdd,functionId:'3020030012'},hashvo);
  	}
  	else if(obj.value==1) //累计项
  	{
  		var hashvo=new ParameterSet();	
	  	var In_paramters="salaryid=${gztemplateSetForm.salaryid}";  
	  	hashvo.setValue("fieldid",obj.name.split("/")[1]);
	    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'3020030013'},hashvo);
  		//var ss=generalAddUpDialog("","A01","3","定义累计项");
  	}
  	else if(obj.value==2) //导入项
  	{
  		saveInputItem(obj.name.split("/")[1])
  	} 
  	
  }
  
  
  function saveInputItem(fieldid)
  {
  		var hashvo=new ParameterSet();	
	  	var In_paramters="salaryid=${gztemplateSetForm.salaryid}";  
	  	hashvo.setValue("fieldid",fieldid);
	  	 
	    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3020030013'},hashvo);
  }
  
  function returnInfo(outparamters)
  {
  		 
  		var fieldid=outparamters.getValue("fieldid");
  		var formula=getDecodeStr(outparamters.getValue("formula"));
  		var itemtype=outparamters.getValue("itemtype");
  		var heapFlag=outparamters.getValue("heapFlag");
  		if(heapFlag.length==0)
  			heapFlag="0";
  		var strExpression=generalComplexConditionDialog2(formula,"1",GZ_ACCOUNTING_IMPORTFORMULA,itemtype,heapFlag);
  		if(strExpression!=null)
  		{
  			
  			var hashvo=new ParameterSet();	
		  	var In_paramters="flag=2";  
		  	hashvo.setValue("fieldid",fieldid);
		  	hashvo.setValue("salaryid",'${gztemplateSetForm.salaryid}');
		  	hashvo.setValue("formula",getEncodeStr(strExpression[0]));	
		  	hashvo.setValue("heapFlag",strExpression[1]);	
		    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfoAdd,functionId:'3020030012'},hashvo);
  		}
  		else
  		{
  			returnInfoAdd2(fieldid,"2");
  		//	document.gztemplateSetForm.action="/gz/templateset/salaryItem.do?b_query=query&salaryid=${gztemplateSetForm.salaryid}";
  		//	document.gztemplateSetForm.submit();
  		
  		}
  }
  
  
  function returnInfo2(outparamters)
  {
  		var fieldid=outparamters.getValue("fieldid");
  		var formula=getDecodeStr(outparamters.getValue("formula"));
  		var heapFlag=outparamters.getValue("heapFlag");
  		var fieldsetid=outparamters.getValue("fieldsetid");
  		
  		var strExpression=generalAddUpDialog(formula,fieldsetid,heapFlag,GZ_ACCOUNTING_DEFINEACCUMULATE);
  		if(strExpression&&strExpression.length>0)
  		{
  			var hashvo=new ParameterSet();	
		  	var In_paramters="flag=1";  
		  	hashvo.setValue("fieldid",fieldid);
		  	hashvo.setValue("salaryid",'${gztemplateSetForm.salaryid}');
		  	hashvo.setValue("formula",getEncodeStr(strExpression[1]));	
		  	hashvo.setValue("heapFlag",strExpression[0]);
		    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfoAdd,functionId:'3020030012'},hashvo);
  		}
  		else
  		{
  			returnInfoAdd2(fieldid,"1");
  		//	document.gztemplateSetForm.action="/gz/templateset/salaryItem.do?b_query=query&salaryid=${gztemplateSetForm.salaryid}";
  		//	document.gztemplateSetForm.submit();
  		
  		}
  }
  
  
  function returnInfoAdd2(fieldid,flag)
  {
  		var obj=document.getElementById("a"+fieldid);
  		var str="";
  		if(flag=='1')
	  		str="<a href=\"javascript:editAddUp('"+fieldid+"')\" ><img src='/images/edit.gif' border=0> </a>";
    	if(flag=='2')
	  		str="<a href=\"javascript:editImport('"+fieldid+"')\" ><img src='/images/edit.gif' border=0> </a>";
      	obj.innerHTML=str;
  	
  }
  
  
  function returnInfoAdd(outparamters)
  {
  		
  		var fieldid=outparamters.getValue("fieldid");
  		var flag=outparamters.getValue("flag");
  		var obj=document.getElementById("a"+fieldid);
  		var str="&nbsp;";
  		if(flag=='1')
	  		str="<a href=\"javascript:editAddUp('"+fieldid+"')\" ><img src='/images/edit.gif' border=0> </a>";
    	if(flag=='2')
	  		str="<a href=\"javascript:editImport('"+fieldid+"')\" ><img src='/images/edit.gif' border=0> </a>";
      	obj.innerHTML=str;
  	
  }
  
  
  //编辑导入项
  function editImport(fieldid)
  {
  		saveInputItem(fieldid)
  }
  
  function editAddUp(fieldid)
  {
  	var hashvo=new ParameterSet();	
	  	var In_paramters="salaryid=${gztemplateSetForm.salaryid}";  
	  	hashvo.setValue("fieldid",fieldid);
	    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'3020030013'},hashvo);
  		
  }
 function queryItem()
 {
        var queryvalue=getEncodeStr(document.getElementById("qv").value);
        
        document.gztemplateSetForm.action="/gz/templateset/salaryItem.do?b_query=query&salaryid=${gztemplateSetForm.salaryid}&queryvalue="+queryvalue;
  		document.gztemplateSetForm.submit();
 }
 var value="${gztemplateSetForm.queryvalue}";
  function queryItemByKey()
  {
    var queryvalue=getEncodeStr(document.getElementById("qv").value);
    if(getDecodeStr(trim(queryvalue))!=getDecodeStr(trim(value)))
    {
       value=queryvalue;
       queryItem();
    }
  }
  function MouseBind()
  {
     document.getElementById('qv').focus();
     var element = document.selection;
     var rge = element.createRange();
		if (rge!=null)
		{	
		    document.getElementById('qv').value="";
	        rge.text=getDecodeStr(value);
	    }
  }
  function replaceAll(str)
  {
        var dd=str;
        dd=str.replace(/\'/g,"’");//替换半角单引号为全角单引号
        dd=str.replace(/\"/g,"”");//替换半角双引号为全角双引号
        dd=str.replace(/</g,"《").replace(/>/g,"》");//
        return dd;
  }
  
  </script>
  
  <body onload="MouseBind();">
   <html:form action="/gz/templateset/salaryItem">
   
   <table width="80%" height='470' align="center" style="margin-top:-3px;"> 
		<tr> <td valign="middle" height='430' align="center" >
		
			<fieldset align="center" style="width:70%;height: 100%;overflow: auto;">
    							 <legend >${gztemplateSetForm.salarySetName}</legend>
    							 
    						<div id="scroll_box" align="center" style="height: 94%;overflow: auto;border: 1px solid;width: 98%;">	<!-- modify by xiaoyun 2014-9-10 --> 
		                      	 <table border="0" cellspacing="0" style="margin-top:0;width: 100%;"  align="center" cellpadding="0">
							   	  <thead>
							   	  <tr class="fixedHeaderTr">
							   	  <td class='TableRow' style="border: none;" colspan='5'>
							   	   &nbsp;&nbsp;<bean:message key="conlumn.investigate_item.name"/>：
							   	   <input type="text" class="text4" style="background-color: #FFF;vertical-align: middle;" id="qv" name="queryvalue" value="${gztemplateSetForm.queryvalue}" onkeyup="queryItemByKey();"/>
							   	   &nbsp;
							   	   <input type="button" class="mybutton" style="vertical-align: middle;" name=qq value="<bean:message key="button.find"/>" onclick="queryItem();"/>
							   	  </td>
							   	  </tr>
							        <tr  class="fixedHeaderTr">
							         <td align="center"  width='10%' class="TableRow noleft" nowrap>
									    <bean:message key="column.select"/>
							         </td>         
							         <td align="center"  width='40%'  class="TableRow noleft" nowrap >
									    <bean:message key="conlumn.investigate_item.name"/>
								     </td>          
							         <td align="center"   width='15%'   class="TableRow noleft" nowrap >
									   <bean:message key="gz.templateset.dealwithFashion"/>
								     </td>         
							         <td align="center"    width='20%' class="TableRow noleft" nowrap >
									   <bean:message key="gz.templateset.itemcode"/>
								     </td>
							         <td align="center"   width='15%' class="TableRow noleft noright" nowrap >
										<bean:message key="column.operation"/>
								     </td>
							         </tr>
							   	  </thead>
					<logic:iterate   id="element" name="gztemplateSetForm" property="salaryItemList"  >
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
							  <td align="center" class="RecordRow noleft" style="border-top: none;" nowrap>&nbsp;
							  	<logic:notEqual  name="element" property="initflag" value="3" >
								  <input type='checkbox' name='salarySetIDs' value='<bean:write name="element" property="fieldid" filter="true"/>' />
								</logic:notEqual>&nbsp;
							  </td>  	  
							  <td align="left"  class="RecordRow noleft" style="border-top: none;" nowrap>
							 &nbsp; <bean:write name="element" property="itemdesc" filter="true"/>
							  </td>
							  <td align="left" class="RecordRow noleft" style="border-top: none;" nowrap> 	 
							 	 &nbsp;&nbsp;<bean:write name="element" property="manageFashion" filter="false"/>
							  </td>
							  <td align="left" class="RecordRow noleft" style="border-top: none;" nowrap> 	 
								 &nbsp;<bean:write name="element" property="itemid" filter="true"/>
							  </td>
							  <td align="center"  id='a<bean:write name="element" property="fieldid" filter="true"/>' style="border-top: none;" class="RecordRow noleft noright" nowrap>
							  &nbsp;
							  	<logic:notEqual  name="element" property="initflag" value="3" >
							     <logic:equal  name="element" property="initflag" value="1">
							     <a href="javascript:editAddUp('<bean:write name="element" property="fieldid" filter="true"/>')" >
							    	<img src="/images/edit.gif" border=0> 
							     </a> 
							     </logic:equal>
							     <logic:equal  name="element" property="initflag" value="2">
							    <a href="javascript:editImport('<bean:write name="element" property="fieldid" filter="true"/>')" >
							    	<img src="/images/edit.gif" border=0>  
							    </a>
							     </logic:equal>
							    </logic:notEqual>
							    &nbsp;
							  </td> 
							 </tr>  	  
		            </logic:iterate>
		            </table>
		            </div>      		
		   </fieldset>
		</td></tr>
		<tr><td align='center' height='20' >
		 <input type='button' class="mybutton" value="<bean:message key="button.insert"/>"  onclick='add()'  />&nbsp;
			<input type='button' class="mybutton" value="<bean:message key="button.delete"/>"  onclick='del()'  />&nbsp;
			<input type='button' class="mybutton" value="<bean:message key="button.return"/>"  onclick='goback()'  />
		</td></tr>
		
	</table>
  			
   
   
   
   
  </html:form> 
  </body>
</html>
