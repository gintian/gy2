<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>


<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>



<script language="javascript">
<% 
	String callbackFunc = request.getParameter("callbackFunc");
%>

	function myClose()
	{
		if("${param.type}"=="save_continue")			
		{
			var thevo=new Object();
			thevo.flag="true";
            parent.window.returnValue=thevo;
			<% 
				if(callbackFunc != null && callbackFunc.length() > 0){
			%>
					callback_(thevo);
			<% }else { %>
		            if(window.showModalDialog) {
                        parent.window.close();;
		            }else{
		                parent.parent.window.opener.window.edit_window_ok(thevo);
		                window.open("about:blank","_top").close();
		            }
            <% } %>
		}			
	}
	
	function callback_(thevo){
		if(window.showModalDialog){
			parent.window.returnValue = thevo;
		}else {
			<%
			if(callbackFunc != null && callbackFunc.length() > 0) {
			%>
				parent.parent.<%=callbackFunc%>(thevo);
			<%}%>
		}
	}
	
    function closewindow()
    {
    <%
		if(callbackFunc != null && callbackFunc.length() > 0) {
	%>
			if(!window.showModalDialog){
			  	var win = parent.parent.Ext.getCmp('checkBody_win');
		 	  	if(win) {
		  			win.close();
		 	  	}
		  	}
		 	parent.window.close();
	<%  }else {%>
	        if(window.showModalDialog) {
                parent.window.close();
	        }else{
	            window.open("about:blank","_top").close();
	        }
    <%  }%>
    }
var isSave = false;
function qxFunc()
{
	if(isSave)
	{
		var thevo=new Object();
		thevo.flag="true";
	<%
		if(callbackFunc != null && callbackFunc.length() > 0) {
	%>
			callback_(thevo);
	<%  }else { %>
			if(window.showModalDialog) {
                parent.window.returnValue=thevo;
			}else {
				parent.parent.window.opener.window.edit_window_ok(thevo);
			}
	<%  } %>
	}
    closewindow();
}
	function save(type)
	{
		var name = document.getElementById("name").value;
		if(ltrim(rtrim(name)) == "")
		{
		 	alert("<bean:message key='jx.paramset.info1'/>");
		 	return;
		}
		/*
		checkBodyObjectForm.action="/performance/options/checkBodyObjectAdd.do?b_save=link&type="+type;
		checkBodyObjectForm.target="_self";
	    checkBodyObjectForm.submit();
	   if(type=='save')
	   {				 
			var thevo=new Object();
			thevo.flag="true";
			window.returnValue=thevo;
			window.close();
	   }*/
 		var hashvo=new ParameterSet();
		hashvo.setValue("body_id",document.getElementById('bodyId').value);
		hashvo.setValue("status",document.getElementById('status').value);
		hashvo.setValue("name",getEncodeStr(document.getElementById('name').value));
		hashvo.setValue("bodyType",${checkBodyObjectForm.bodyType});
		hashvo.setValue("level",document.getElementById('level').value);
		//<logic:equal name="checkBodyObjectForm" property="bodyType" value="0">	
		//hashvo.setValue("scope",document.getElementById('scope').value);
		//</logic:equal>
		hashvo.setValue("noself",${checkBodyObjectForm.noself});
		if('${checkBodyObjectForm.bodyType}'=='1')		
			hashvo.setValue("object_type",document.getElementById('object_type').value);	
		hashvo.setValue("type",type);
		var request=new Request({method:'post',asynchronous:false,onSuccess:afterSave,functionId:'9026001003'},hashvo);	   
	}
	function afterSave(outparamters)
	{
		var type=outparamters.getValue("type");
		isSave=true;
		if(type=='save')
			qxFunc();
		else
		{
			document.getElementById('bodyId').value='';
			document.getElementById('name').value='';
			document.getElementById('level').value='6';		
			document.getElementById('status').value='1';		
		}
	}
	function selectrange(){
	var selectid = document.checkBodyObjectForm.level.value;
	if(selectid=="5"){//选择本人
	var info=$('info');
	info.style.display="none";
	var info2=$('info2');
	info2.style.display="none";
	}else{
	var info=$('info');
	info.style.display="block";
	var info2=$('info2');
	info2.style.display="block";
	}
	}
</script>
<body onbeforeunload="myClose();">
<html:form action="/performance/options/checkBodyObjectList">
	<table border="0" cellspacing="0" align="center" cellpadding="2">

			<tr>
						<td align="center" nowrap>
							<fieldset align="center" style="width:355px;">
							<legend align="center">
							<logic:equal name="checkBodyObjectForm" property="bodyType" value="0">
									主体类别维护
							</logic:equal>	
							<logic:equal name="checkBodyObjectForm" property="bodyType" value="1">
									对象类别维护
							</logic:equal>		
							</legend>
		<table border="0" cellspacing="2" align="left" cellpadding="5">			   
			<input type="hidden" id="bodyType" value="${checkBodyObjectForm.bodyType}" />
			<html:hidden name="checkBodyObjectForm" styleId="bodyId" property="checkbodyobjectvo.string(body_id)"/>
			<tr>
				<td align="right" nowrap valign="left">
					&nbsp;<bean:message key='column.name' />
				</td>
				<td align="left" nowrap valign="left">
						<logic:notEqual name="checkBodyObjectForm" property="checkbodyobjectvo.string(body_id)"
							value="-1">
						<logic:notEqual name="checkBodyObjectForm" property="checkbodyobjectvo.string(body_id)"
							value="5">
					<html:text name="checkBodyObjectForm" styleId="name" property="checkbodyobjectvo.string(name)" styleClass="inputtext"/>					
						</logic:notEqual>
						</logic:notEqual>
						<logic:equal name="checkBodyObjectForm" property="checkbodyobjectvo.string(body_id)"
							value="-1">
						
							<html:text name="checkBodyObjectForm" styleId="name" property="checkbodyobjectvo.string(name)" disabled="true" styleClass="inputtext"/>	
						</logic:equal>		
						<logic:equal name="checkBodyObjectForm" property="checkbodyobjectvo.string(body_id)"
							value="5">
									<html:text name="checkBodyObjectForm" styleId="name" property="checkbodyobjectvo.string(name)" disabled="true" styleClass="inputtext"/>
						</logic:equal>	
				</td>  
			</tr> 
		
			<tr id="levelRow">
				<td align="right" nowrap valign="left">
					&nbsp; <bean:message key="jx.param.dengji"/>
				</td>
				<td align="left" nowrap valign="left">
				<logic:notEqual name="checkBodyObjectForm" property="checkbodyobjectvo.string(body_id)"
							value="-1">
				<logic:notEqual name="checkBodyObjectForm" property="checkbodyobjectvo.string(body_id)"
							value="5">
				  <logic:equal name="checkBodyObjectForm" property="dbType" value="oracle">				   				
					<html:select name="checkBodyObjectForm" property="checkbodyobjectvo.string(level_o)" styleId="level" size="1">			
						<html:option value="-2">
							<bean:message key='jx.param.degree8'/><!-- 第四级领导 -->
						</html:option>	
						<html:option value="-1">
							<bean:message key='jx.param.degree7'/><!-- 第三级领导 -->
						</html:option>	
						<html:option value="0">
							<bean:message key='jx.param.degree0'/><!-- 第二级领导 -->
						</html:option>
						<html:option value="1">
							<bean:message key='jx.param.degree1'/><!-- 直接领导 -->
						</html:option>
						<html:option value="2">
							<bean:message key='jx.param.degree2'/>
						</html:option>
						<html:option value="3">
							<bean:message key='jx.param.degree3'/>
						</html:option>
						<html:option value="4">
							<bean:message key='jx.param.degree4'/>
						</html:option>
						<html:option value="5">
							<bean:message key='jx.param.degree5'/>
						</html:option>
						<html:option value="6">
							<bean:message key='jx.param.degree6'/>
						</html:option>
					</html:select>
			   </logic:equal>
			   <logic:notEqual name="checkBodyObjectForm" property="dbType" value="oracle">				   				
					<html:select name="checkBodyObjectForm" property="checkbodyobjectvo.string(level)"  styleId="level" size="1">
						<html:option value="-2">
							<bean:message key='jx.param.degree8'/>
						</html:option>	
						<html:option value="-1">
							<bean:message key='jx.param.degree7'/>
						</html:option>		
						<html:option value="0">
							<bean:message key='jx.param.degree0'/>
						</html:option>
						<html:option value="1">
							<bean:message key='jx.param.degree1'/>
						</html:option>
						<html:option value="2">
							<bean:message key='jx.param.degree2'/>
						</html:option>
						<html:option value="3">
							<bean:message key='jx.param.degree3'/>
						</html:option>
						<html:option value="4">
							<bean:message key='jx.param.degree4'/>
						</html:option>
						<html:option value="5">
							<bean:message key='jx.param.degree5'/>
						</html:option>
						<html:option value="6">
							<bean:message key='jx.param.degree6'/>
						</html:option>
					</html:select>
			   </logic:notEqual>
			   	</logic:notEqual>
						</logic:notEqual>
						<logic:equal name="checkBodyObjectForm" property="checkbodyobjectvo.string(body_id)"
							value="5">
							 <logic:notEqual name="checkBodyObjectForm" property="dbType" value="oracle">		
								<html:select name="checkBodyObjectForm" property="checkbodyobjectvo.string(level)" styleId="level" size="1" disabled="true">								
									<html:option value="5">					
										<bean:message key='jx.param.degree5'/>
									</html:option>
								</html:select>
							</logic:notEqual>
							 <logic:equal name="checkBodyObjectForm" property="dbType" value="oracle">		
								<html:select name="checkBodyObjectForm" property="checkbodyobjectvo.string(level_o)" styleId="level" size="1" disabled="true">								
									<html:option value="5">					
										<bean:message key='jx.param.degree5'/>
									</html:option>
								</html:select>
							</logic:equal>
						</logic:equal>	
							<logic:equal name="checkBodyObjectForm" property="checkbodyobjectvo.string(body_id)"
							value="-1">
										 <logic:notEqual name="checkBodyObjectForm" property="dbType" value="oracle">		
								<html:select name="checkBodyObjectForm" property="checkbodyobjectvo.string(level)" styleId="level" size="1" disabled="true">								
									<html:option value="5">					
										<bean:message key='jx.param.degree5'/>
									</html:option>
								</html:select>
							</logic:notEqual>
							 <logic:equal name="checkBodyObjectForm" property="dbType" value="oracle">		
								<html:select name="checkBodyObjectForm" property="checkbodyobjectvo.string(level_o)" styleId="level" size="1" disabled="true">								
									<html:option value="5">					
										<bean:message key='jx.param.degree5'/>
									</html:option>
								</html:select>
							</logic:equal>
						</logic:equal>		
				</td>  
			</tr> 
			<logic:equal name="checkBodyObjectForm" property="bodyType" value="1">
				<tr>
					<td align="right" nowrap valign="left">
						&nbsp;<bean:message key='jx.param.objectype' />
					</td>
					<td align="left" nowrap valign="left">
						<html:select name="checkBodyObjectForm" property="checkbodyobjectvo.string(object_type)" styleId="object_type" size="1">
							<html:option value="">
								&nbsp;
							</html:option>
							<html:option value="1">
								<bean:message key='jx.khplan.team' />
							</html:option>
							<html:option value="2">
								<bean:message key='kjg.title.personnel' />
							</html:option>
						</html:select>
					</td>
				</tr>
			</logic:equal>	
			<tr>
				<td align="right" nowrap valign="left">
					&nbsp;<bean:message key='kh.field.flag' />
				</td>
				<td align="left" nowrap valign="left">
					<html:select name="checkBodyObjectForm" property="checkbodyobjectvo.string(status)" styleId="status" size="1">
						<html:option value="1">
							<bean:message key='kh.field.yx' />
						</html:option>
						<html:option value="0">
							<bean:message key='kh.field.wx' />
						</html:option>
					</html:select>
				</td>
			</tr>
			<!-- <logic:equal name="checkBodyObjectForm" property="bodyType" value="0">
			<tr>
			
				<td align="right" nowrap valign="left">
				<div id="info2" style="display:block">
					&nbsp;<bean:message key="menu.performance.mainBodyrange" />
					</div>
				</td>
				<td align="left" nowrap valign="left">
				<div id="info" style="display:block">
				<logic:equal name="checkBodyObjectForm" property="checkbodyobjectvo.string(body_id)"
							value="5">
							<html:select name="checkBodyObjectForm" property="checkbodyobjectvo.string(scope)" styleId="scope" size="1" disabled="true">
						<html:option value="">
							
						</html:option>
						<html:option value="1">
							<bean:message key='label.title.org' />
						</html:option>
						<html:option value="2">
							<bean:message key='label.title.topdept' />
						</html:option>
						<html:option value="3">
							<bean:message key='label.title.deptself' />
						</html:option>
					</html:select>
							</logic:equal>
							<logic:notEqual name="checkBodyObjectForm" property="checkbodyobjectvo.string(body_id)"
							value="5">
							
							<html:select name="checkBodyObjectForm" property="checkbodyobjectvo.string(scope)" styleId="scope" size="1" >
							<html:option value="">
							
						</html:option>
							<html:option value="1">
								<bean:message key='label.title.org' />
							</html:option>
							<html:option value="2">
								<bean:message key='label.title.topdept' />
							</html:option>
							<html:option value="3">
								<bean:message key='label.title.deptself' />
							</html:option>
							</html:select>
					
							</logic:notEqual>
							</div>
				</td>
				
			</tr>
			</logic:equal>-->
		</table>
	</fieldset>
				</td>
			</tr>
			</table>
	<table border="0" cellspacing="0" align="center" cellpadding="2">		
			<tr>
				<td align="center">
					<input type="button" class="mybutton" value="<bean:message key='button.save' />" onClick="save('save');" />
					<logic:equal name="checkBodyObjectForm" property="show" value="save">
						<input type="button" value="<bean:message key='button.save'/>&<bean:message key='edit_report.continue'/>" onclick="save('save_continue');" Class="mybutton">
						<input type="button" class="mybutton" value="<bean:message key='button.cancel'/>" onClick="qxFunc();">  
		  			</logic:equal>
		  			<logic:equal name="checkBodyObjectForm" property="show" value="edit">
						<input type="button" class="mybutton" value="<bean:message key='button.cancel'/>" onClick="closewindow();">
		  			</logic:equal>
					
				</td>
			</tr>
		</table>
</html:form>
<script>
	<logic:equal name="checkBodyObjectForm" property="bodyType" value="1">
		document.getElementById('levelRow').style.display='none';
	</logic:equal>
    if(!getBrowseVersion()){
        var legends = document.getElementsByTagName("legend");
        if(legends && legends.length>0){
            legends[0].style.display='none';
        }
    }
</script>
</body>