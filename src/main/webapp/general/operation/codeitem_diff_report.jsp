<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.general.operation.OperationForm
				,java.util.ArrayList" %>
<%
OperationForm operationForm = (OperationForm)session.getAttribute("operationForm");
ArrayList fieldList=(ArrayList)operationForm.getFieldList();
int size=fieldList.size();
String codeitemid_buffers=request.getParameter("codeitemid_buffers");
String layer_buffers=request.getParameter("layer_buffers");
boolean flag_buffer=false;
String[] codeitemids=new String[30];
String[] layers=new String[30];
if(codeitemid_buffers!=null && !codeitemid_buffers.equals(""))
{
	flag_buffer=true;
	codeitemid_buffers=codeitemid_buffers.substring(0,codeitemid_buffers.length()-1);
	codeitemids=codeitemid_buffers.split(",");
	layer_buffers=layer_buffers.substring(0,layer_buffers.length()-1);
	layers=layer_buffers.split(",");
}

%>
<style>
#tbl-container 
{
	width:100%;
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}

</style>
<html>
<head>
<script language="javascript" src="/js/dict.js"></script>
<script type="text/javascript"><!--
	function init(){
		var index=0 //这样写的意义 
		var size="<%=size%>";
		for(var i=0;i<size;i++){
			index=i;
			if(document.getElementById("checked"+index).value=="1"){
				   document.getElementById("codeitemid"+index).checked=true;
				   if(document.getElementById("layer"+index).childNodes.length>1){
				       document.getElementById("layer"+index).disabled=false;		
				   }else{
				       document.getElementById("layer"+index).disabled=true;
				   }
			}else if(document.getElementById("checked"+index).value=="0"){
				   document.getElementById("codeitemid"+index).checked=false;
				   document.getElementById("layer"+index).disabled=true;	
			}
		}
	}

	function save(){
		var index=0;
		var layer=document.getElementsByName("layer");
		var codeitemname=document.getElementsByName("codeitemname");
		var codeitemid=document.getElementsByName("codeitemid");
		var chgstate=document.getElementsByName("chgstate");
		var codeitemid_buffer="";
		var codeitemname_buffer="";
		var layer_buffer="";
		var chgstate_buffer="";
		var a="";
		
		for(var i=0;i<document.operationForm.elements.length;i++)
		{			
	   		if(document.operationForm.elements[i].type=='checkbox')       
	   			{	
			  		if(document.operationForm.elements[i].checked)
			  			{
			  				codeitemid_buffer=codeitemid_buffer+codeitemid[index].value+",";
			  				codeitemname_buffer=codeitemname_buffer+codeitemname[index].value+",";
			  				a=layer[index].value;
			  				if(layer[index].value==""){
			  				   a=layer[index].value="kong";
			  				}
			  			    layer_buffer=layer_buffer+a+",";
			  			    chgstate_buffer=chgstate_buffer+chgstate[index].value+",";
						}
					index++;
				}
			
		}
		var vo=new Object();
		vo.codeitemid_buffer=codeitemid_buffer;
		vo.codeitemname_buffer=codeitemname_buffer;
		vo.layer_buffer=layer_buffer;
		vo.chgstate_buffer=chgstate_buffer;
		window.returnValue=vo;
		window.close();
	}
	function changeSelect(index)
	{
	  var obj = document.getElementById("codeitemid"+index);
	  var obj2 = document.getElementById("layer"+index);
	  if(obj.checked){
		if(obj2.childNodes.length>1)
		  obj2.disabled=false;
	  }else{
	     obj2.disabled=true;
	  }
		
	}

--></script>

</head>
<body>
<html:form action="/general/operation/updateapproveway" >

	<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0" align="center">
		<tr>
			<td width="100%" valign="top" align="center">
	
 			<script language='javascript' >
				document.write("<div id=\"tbl-container\"  style='position:absolute;left:2;height:"+(document.body.clientHeight-50)+";width:100%'  >");
 			</script> 
				<table width="100%" cellspacing="0" align="center" cellpadding="0"  style="border:0px;">
				<thead>
				   <tr class="fixedHeaderTr" >
				     <td align="center" class="TableRow" nowrap style="border-top:0px;border-left:0px;">
				    	指标
				     </td>
				      
				     <td align="center" class="TableRow" nowrap style="border-top:0px;border-left:0px;">
				         &nbsp;&nbsp;&nbsp;&nbsp;
				 	</td>
				 	<td align="center" class="TableRow" nowrap style="border-top:0px;border-left:0px;border-right:0px">
				        代码控制层级
				 	</td>
				  </tr>	
				 </thead> 
				  
				  	<logic:iterate id="element" name="operationForm" property="fieldList" indexId="index">     
					  	<tr >
						      	<td class="RecordRow" style="border-top:0px;border-left:0px;">
						      		<SPAN><bean:write name="element" property="codeitemname"/></SPAN>
						      		<input type="hidden" name="codeitemname"   value=<bean:write name="element" property="codeitemname"/> />
						      		<input type="hidden" name="chgstate"   value=<bean:write name="element" property="chgstate"/> />
						      	</td>
						      	
						      	<%
						      	   String aa="checked"+index;
						      	   String cc="layer"+index;
						      	   String value="0";
						      	   String layer="";
						            if(flag_buffer){
						               for(int i=0;i<codeitemids.length;i++){
						         %>
						            	  <logic:equal value="<%=codeitemids[i] %>" name="element" property="codeitemid">
						            	  <%
						            	      layer=layers[i];
						            	      value="1";
						            	      
						            	  %>
						            	  </logic:equal>
						            	  <%
						               }
						                  %>
						            	  <td class="RecordRow" style="border-top:0px;border-left:0px;"> 
						            	       <input type="hidden"  name="checked" id="<%=aa%>"  value=<%=value%> />
						            	      <input type="checkbox" id="codeitemid<%=index%>" name="codeitemid"  onclick='changeSelect("<%=index%>");' value="<bean:write name="element" property="codeitemid"/>" />
						            	   </td>
						      	          <td class="RecordRow" style="border-top:0px;border-left:0px;border-right:0px">
						         	  <hrms:optioncollection name="element" property="layerList" collection="list" />
										<html:select styleId="<%=cc%>" name="element" property="layer" value="<%=layer %>" style="width:100px;">
											<html:options collection="list"  property="dataValue" labelProperty="dataName"/>
								   		</html:select>   
						      	      </td>
						             <% 
					           }else{%>
						        <td class="RecordRow" style="border-top:0px;border-left:0px;">
						         <input type="hidden"  name="checked" id="<%=aa%>"  value="<bean:write name="element" property="checked"/>" />
						      	 <input type="checkbox" id="codeitemid<%=index%>" name="codeitemid"  onclick='changeSelect("<%=index%>");' value="<bean:write name="element" property="codeitemid"/>" />
						      	</td>
						      	<td class="RecordRow" style="border-top:0px;border-left:0px;border-right:0px">
					
						         	<hrms:optioncollection name="element" property="layerList" collection="list" />
										<html:select disabled="true" styleId="<%=cc%>" name="element" property="layer"  style="width:100px;">
											<html:options collection="list"  property="dataValue" labelProperty="dataName"/>
								   		</html:select>   
						      	</td>        
						       <% } %>
					  </tr>
				    </logic:iterate>
				</table> 
			</div>
			
		</td>
	</tr>
</table>
<div id="container" style='position:relative; left:5px; top:-5px'>
<table border="0" cellspacing="0" cellpadding="0" align="center" >
	<tr>
			<td style="padding:0px;">
				<input type='button' class="mybutton" onclick='save();' name="ok" value='<bean:message key="button.ok"/>' id="ok" />
				<input type='button' class="mybutton" onclick='window.close();' id="cancel" name='cancel' value='<bean:message key="button.cancel"/>' style="margin-left:0px;"/>
			</td>
	</tr>
</table>	
</div>
	

</html:form>
<script type="text/javascript">
		init();
</script>
</body>
</html>