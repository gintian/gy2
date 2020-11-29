<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.org.autostatic.mainp.ProjectForm"%>
<%@page import="com.hrms.struts.taglib.CommonData"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
  </head>
  <script language="javascript">
  	var ids ;
  	function ischange(c){
  		 ids = c;
  		 var hashvo=new ParameterSet(); 
   		 hashvo.setValue("flag","1"); 
   		 var a = document.getElementById(ids).value;
   		 hashvo.setValue("peoples",a);
   		 hashvo.setValue("itemid",c);
   		 hashvo.setValue("ajax", "1");
 		 var pars="people="+document.getElementById('people').value;
   		 var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showSelectList,functionId:'1602010240'},hashvo);
  	}
  	
  	function showSelectList(outparamters){
	 	var list=outparamters.getValue("codes");
		AjaxBind.bind(document.getElementById("a_"+ids),list);
	}
	 
	function savefieldOk(c){
		var hashvo=new ParameterSet(); 
		var srcs = document.getElementsByName("src");
		var peoples = document.getElementsByName("people");
		var ids = document.getElementsByName("ids");
		var src = "";
		var people = "";
		var app = "";
		for(var i = 0 ; i < srcs.length ; i ++){
			if (srcs[i].value == null || srcs[i] == "#")
				srcs[i].value = "";
			
			src += srcs[i].value + ",";
		
			if (peoples[i].value == null || peoples[i] == "#")
				peoples[i].value = "";			
		
			people += peoples[i].value + ",";			
			app += ids[i].value+",";
			
		}	
			hashvo.setValue("src",src);
			hashvo.setValue("subset",people);
			hashvo.setValue("app",app);
   			var request=new Request({method:'post',onSuccess:saveOk,functionId:'1602010241'},hashvo);
	}
	
	function saveOk(outparamters){
		var mess=outparamters.getValue("mess");
		if(mess=="ok"){
			alert("设置成功!");
		}else{
			alert("设置失败!");
		}
	}
	
	var ids1 = "";
	function load(){
		var s ="${projectForm.src}";
		var s2 = s.split(",");
		var c = document.getElementById("people").value;
		var p ="${projectForm.people}";
		var s1 = p.split(",");
		var d = "${projectForm.dest}";
		var d1 = d.split(",");
		for(var i = 0 ; i < s1.length ; i ++){
			if(s1[i] != null && s1[i] != ""
			 && d1[i] != null && d1[i] != ""){
				document.getElementById(d1[i]).value = s1[i];
				var hashvo=new ParameterSet();
				hashvo.setValue("flag","1"); 
				hashvo.setValue("peoples",s1[i]);
				if(d1[i] != "undefined" && d1[i] != null){					
					ids1 += d1[i] +",";
					hashvo.setValue("itemid",d1[i]);
				}
				var pars = s1[i];
				hashvo.setValue("ajax", "1");
				var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showSelectList1,functionId:'1602010240'},hashvo);		
				for(var j = 0 ; j < s2.length ; j++){
					if(s2[j] != null && s2[j] != ""){
					 document.getElementById("a_"+d1[j]).value = s2[j];			
					}
				}
			}
		}
		
	}
	
	function showSelectList1(outparamters){
	 	var list=outparamters.getValue("codes");
	 	var d = outparamters.getValue("d1");
	 	//alert(list.length);
	 	//var arrs = outparamters.getValue("arrs");
	 	//	alert(arrs.length);
		//if(ids1 != null){			
		//	var ds = ids1.split(",");
		//	var bd;
		//	for(var i = 0 ; i < ds.length ; i++){
		//		bd = ds[i];
		//		if(bd != null && bd != "undefined" && bd != ""){				
					AjaxBind.bind(document.getElementById("a_"+d),list);
		//		}
		//	}
		//}
	}
  </script>
  <body onload="load();">
<html:form action="/train/setparam/teacherfield">
	 <table width="500" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap > 
            	培训教师指标设置 
            <br></td>            	        	        	        
           </tr>
   	  </thead>
   		   <tr align="center">   
   		   		<td  class="RecordRow">
	   		   		<table width="90%" border="0" class="ListTable" style="position:relative;top:1px;" cellpadding="0" cellspacing="0" >
	   		   			<tr>
	   		   				<td class="RecordRow">
	   		   					&nbsp;培训教师指标
	   		   				</td>
	   		   				<td class="RecordRow" align="center">
	   		   					信息集
	   		   				</td>
	   		   				<td class="RecordRow" align="center">
	   		   					指标
	   		   				</td>
	   		   			</tr>
	   		   			<logic:iterate id="element" name="projectForm" property="desc">
	   		   				<tr>
	   		   					<td class="RecordRow">
	   		   						&nbsp;<bean:write name="element" property="itemdesc"/>
	   		   						<bean:define id="aa" name="element" property="itemid"/>
	   		   						<html:hidden name="element" property="ids" value="${aa}"/>
	   		   					</td>
	   		   					
	   		   					<td class="RecordRow">
	   		   						<hrms:optioncollection name="projectForm" property="list" collection="lists" />&nbsp;  
								     <html:select name="projectForm" property="people" style="width:150px;" onchange="ischange('${aa}'); " 
								     styleId='${aa}'>
										<html:options collection="lists" property="dataValue" labelProperty="dataName" />
									 </html:select>
	   		   					</td>
	   		   					
	   		   					<td class="RecordRow">
	   		   					<%
	   		   						ProjectForm pro = (ProjectForm)session.getAttribute("projectForm");
	   		   						HashMap hs =pro.getHs();
	   		   						String src = pro.getDest();
	   		   						String [] s = src.split(",");
	   		   						int count = 0 ;
	   		   						
	   		   						for(int i = 0 ; i < s.length; i++){
	   		   							ArrayList as = (ArrayList)hs.get(s[i]);
	   		   						%>		
	   		   								
	   		   						<logic:equal name="element" property="itemid" value="<%=s[i]%>">
		   		   						<%
		   		   							count = 1;
		   		   						 %>
		   		   						<hrms:optioncollection name="projectForm" property="codes" collection="code"/>
						    	        <html:select name="projectForm" property="src" style="width:150px;" 
						    	        styleId='a_${aa}'>
											<html:options collection="code" property="dataValue" labelProperty="dataName" />
										</html:select>	
									</logic:equal>
									<%
										if(count != 0){
											break;
										}
										}
										if(count == 0){
									 %>
									 
	   		   						<hrms:optioncollection name="projectForm" property="ass" collection="code"/> 
					    	        <html:select name="projectForm" property="src" style="width:150px;" 
					    	        styleId='a_${aa}'>
										<html:options collection="code" property="dataValue" labelProperty="dataName" />
									</html:select>	
									<%
									}
									 %>
	   		   					 	</td>
	   		   					</tr>
	   		   			</logic:iterate>
	   		   		</table>
   		   		</td>
   		   </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap   style="height:35px;">
               <input type="button" name="btsave" value='<bean:message key="button.ok"/>' class="mybutton" onclick="savefieldOk('${aa}');">
              <!-- <input type="button" name="breturn" value='<bean:message key="button.return"/>' class="mybutton" onclick="retrunSans();"> -->
          </td>
          </tr>
</table>
</html:form>
  </body>
</html>
