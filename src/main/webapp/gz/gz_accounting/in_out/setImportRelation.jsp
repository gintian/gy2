<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
   
  </head>
  <script language='javascript'>
  
  function isMultipleSelect(obj_name)
  {
  		var obj=eval("document.accountingForm."+obj_name);
  		var num=0;
  		for(var i=0;i<obj.options.length;i++)
  		{
  			if(obj.options[i].selected==true)
  				num++;
  		}
  		return num;
  }
  
  
  
  function getSelectText(obj_name)
  {
 	 var obj=eval("document.accountingForm."+obj_name);
 	 for(var i=0;i<obj.options.length;i++)
  	 {
  			if(obj.options[i].selected==true)
  				return obj.options[i].text;
  	 }
 	 
  }
  
  
  function validateRelationItem()
  {
  		var o_obj=eval("document.accountingForm.a1");
  		var to_obj=eval("document.accountingForm.a2");
        var  o_obj2=eval("document.accountingForm.oppositeItem");
  		for(var i=0;i<o_obj2.options.length;i++)
  		{
  			var a_value=o_obj2.options[i].value.split("=")[0];
  			var a_value1=o_obj2.options[i].value.split("=")[1];
  			var is=0;
  			for(var j=0;j<o_obj.options.length;j++)
  			{
  				if(a_value==o_obj.options[j].value)
  				{
  					is=1;
  					break;
  				}
  			}
  			if(is==0)
			{
					alert("数据对应指标有错！");
  					return false;
  			}
  			
  			is=0;
  			for(var j=0;j<to_obj.options.length;j++)
  			{
  				if(a_value1==to_obj.options[j].value)
  				{
  					is=1;
  					break;
  				}
  			}
  			if(is==0)
			{
					alert("数据对应指标有错！");
  					return false;
  			}
  		}
  		
  		
        o_obj2=eval("document.accountingForm.relationItem");
  		for(var i=0;i<o_obj2.options.length;i++)
  		{
  			var a_value=o_obj2.options[i].value.split("=")[0];
  			var a_value1=o_obj2.options[i].value.split("=")[1];
  			var is=0;
  			for(var j=0;j<o_obj.options.length;j++)
  			{
  				
  				if(a_value==o_obj.options[j].value)
  				{
  					is=1;
  					break;
  				}
  			}
  			if(is==0)
			{
					alert("关联关系指标有错！");
  					return false;
  			}
  			
  			is=0;
  			for(var j=0;j<to_obj.options.length;j++)
  			{
  				if(a_value1==to_obj.options[j].value)
  				{
  					is=1;
  					break;
  				}
  			}
  			if(is==0)
			{
					alert("数据对应指标有错！");
  					return false;
  			}  			
  		}
  		
    	return true;
  }
  
  
  
  /** 过滤日期 和 大字段 类型的指标  */
  function setRelationItem(obj_name)
  {
  		if(isMultipleSelect("a1")==0)
  			return;
		if(isMultipleSelect("a1")>1)
		{
			alert("只能单选");
			return;
		}
		if(isMultipleSelect("a2")==0)
  			return;
		if(isMultipleSelect("a2")>1)
		{
			alert("只能单选");
			return;
		}
		var a1_value=document.accountingForm.a1.value;
		var a2_value=document.accountingForm.a2.value;
		if(obj_name=='oppositeItem')
		{
			if(a2_value.toUpperCase()=='A00Z2'||a2_value.toUpperCase()=='A00Z3'||a2_value.toUpperCase()=='NBASE'||a2_value.toUpperCase()=='A0100'||a2_value.toUpperCase()=='B0110'||a2_value.toUpperCase()=='E0122'||a2_value.toUpperCase()=='A0101')
			{
				alert("此目标数据为不可修改系统项!");
				return;
			}
		}
		
		var a1_text=getSelectText("a1");
		var a2_text=getSelectText("a2");
		var newValue=a1_value+"="+a2_value;
		var newText=trim(a1_text+"="+a2_text.substring(a2_text.indexOf(" ")+1,a2_text.indexOf("(")));
	 	var newOption = new Option(newText,newValue);
	 	var obj=eval("document.accountingForm."+obj_name);
	 	var num=0;
	 	for(var i=0;i<obj.options.length;i++)
	 	{
	 		if(obj.options[i].value.indexOf(a2_value)!="-1"&&obj_name=='oppositeItem'){
	 			alert("目标数据不能重复！");
	 			return;
	 		}
	 		if(obj.options[i].value==newValue)
	 			num++;
	 	}
	 	if(num==0)
			obj.options[obj.options.length]=newOption;
  }
  
  /** 展现原始数据 **/
  function outOriginationData()
  {		
	  var fieldName = getDecodeStr(${accountingForm.originalDataFile});
 	  var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","xls");
  }
  
  
  
  /** 显示没有对应数据 **/
  function showNoRelationData()
  {
  		setselectitem("relationItem");
  		setselectitem("oppositeItem");
  		if(isMultipleSelect("relationItem")==0)
  		{
  			alert("请指定关联指标!")
  			return;
  		}
  		document.accountingForm.action="/gz/gz_accounting/in_out.do?b_setRelation=no";
  		document.accountingForm.submit();
  }
  
   /** 显示有同号数据 **/
   function showRelationData()
  {
  		setselectitem("relationItem");
  		setselectitem("oppositeItem");
 	    if(isMultipleSelect("relationItem")==0)
  		{
  			alert("请指定关联指标!")
  			return;
  		}
  		document.accountingForm.action="/gz/gz_accounting/in_out.do?b_setRelation=yes";
  		document.accountingForm.submit();
  }
  
  
  <%
  if(request.getParameter("b_setRelation")!=null)
  {
  %>
  window.open("/gz/gz_accounting/in_out.do?b_showRelation=<%=(request.getParameter("b_setRelation"))%>");
  <%
  }
  %>
  
  /* 保存关联 方案  */
  function saveRelation()
  {
  	
  	var name=window.prompt("请输入方案名称：","新方案");
	if(name&&trim(name).length>0)
	{
		setselectitem("relationItem");
  		setselectitem("oppositeItem");
  		document.accountingForm.schemeName.value=name;
		document.accountingForm.action="/gz/gz_accounting/in_out.do?b_add=add";
		document.accountingForm.submit();
	}
  
  }
  
  /* 读取关联指标方案 */
  function readRelation()
  {
  	//br_getSchemeList
  		var arguments=new Array();     
	    var strurl="/gz/gz_accounting/in_out.do?br_getSchemeList=link";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
	    var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=325px;dialogHeight=420px;resizable=yes;scroll=no;status=no;");  
	    if(ss)
	    {
	    	
	    	var oppositeItemList=ss[0];
	    	var relationItemList=ss[1];
	    	delOptions("oppositeItem")
	    	delOptions("relationItem")
	    	
	    	if(oppositeItemList&&oppositeItemList.length>0)
	    	{
	    		setOptions(oppositeItemList,"oppositeItem")
	    	}
	    	if(relationItemList&&relationItemList.length>0)
	    	{
	    		setOptions(relationItemList,"relationItem")
	    	}
	     	
	    }
  }
  
  function setOptions(options,obj_name)
  {
  	var obj=eval("document.accountingForm."+obj_name);
  	for(var i=0;i<options.length;i++)
  	{
  		var temps=options[i].split("#");
  		var newOption = new Option(temps[1],temps[0]);
  		obj.options.add(newOption);
  	
  	}
  }
  
  //删除 select控件里所有的选项
  function delOptions(obj_name)
  {
  		var obj=eval("document.accountingForm."+obj_name);
	  	for(var i=obj.options.length-1;i>=0;i--)
	  	{	
	  		obj.options.remove(i);
	  	}
  
  }
  
  
  function enter()
  {
  		if(document.accountingForm.relationItem.options.length==0)
  		{
  			alert("请指定关联指标!")
  			return;
  		}
  		if(document.accountingForm.oppositeItem.options.length==0)
  		{
  			alert("请指定对应指标!")
  			return;
  		}
  		 		

  		if(!validateRelationItem())
  			return;
  		setselectitem("relationItem");
  		setselectitem("oppositeItem");
  		document.getElementById("enterbutton").disabled=true;
  		document.accountingForm.action="/gz/gz_accounting/in_out.do?b_import=import";
		document.accountingForm.submit();
  }
  
  
  function validate()
  {
  		if(document.accountingForm.relationItem.options.length==0)
  		{
  			alert("请指定关联指标!")
  			return;
  		}
  		if(document.accountingForm.oppositeItem.options.length==0)
  		{
  			alert("请指定对应指标!")
  			return;
  		}
  		if(!validateRelationItem())
  			return;  					
  		setselectitem("relationItem");
  		setselectitem("oppositeItem");
  		document.getElementById("enterbutton").disabled=true;
  		document.accountingForm.action="/gz/gz_accounting/in_out.do?b_validate=import";
		document.accountingForm.submit();
  }
  
  
  
  
  
  </script>
  <body>
     <html:form action="/gz/gz_accounting/in_out">
   <table align="center"> 
		<tr> <td valign="top"><Br>
		
			<fieldset align="center" >
    							 <legend >选择数据对应指标</legend>
   			<table width="90%" height='100%' border="0" cellspacing="1"  align="center" cellpadding="10"  >
   			<tr>
   				<td align="center" >
   						<fieldset align="center" >
    							 <legend >源数据</legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"   cellpadding="0">                   		                     			
			                      				<tr>
			                					<td width="100%" >
			                					<select name='a1'  multiple="multiple"  ondblclick='setRelationItem("oppositeItem")'    style="height:150px;width:220px;font-size:9pt"  >
                  									<logic:iterate id="element"  name="accountingForm" property="originalDataList"  >
                  										<option value='<bean:write name="element" property="dataValue" />'><bean:write name="element" property="dataName" /></option>
                  									</logic:iterate>
                  								</select>							                  								
			                					</td>
			                					</tr>
		                					
		                      			</table>
		                 </fieldset>
   						 <br>
   						 <fieldset align="center"  >
    							 <legend >选择数据对应指标</legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"   cellpadding="0">                   		                     			
			                      				<tr>
			                					<td width="100%" >
			                					<select name='oppositeItem' ondblclick='removeitem("oppositeItem")'   multiple="multiple" style="height:150px;width:220px;font-size:9pt"  >
                  										<logic:iterate id="element"  name="accountingForm" property="oppositeItemList"  >
                  										<option value='<bean:write name="element" property="dataValue" />'><bean:write name="element" property="dataName" /></option>
                  										</logic:iterate>
                  								</select>
			                					</td>
			                					</tr>
		                					
		                      			</table>
		                 </fieldset>
		                 <Br>
		                 <Input type='button' value='选择对应指标' onclick='setRelationItem("oppositeItem")' class="mybutton"   />
		                 <Input type='button' value='撤销对应'  class="mybutton" onclick='removeitem("oppositeItem")'  />
   				
   				
   				</td>
   				<td align="center" >
   						<fieldset align="center" >
    							 <legend >目标数据</legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"   cellpadding="0">                   		                     			
			                      				<tr>
			                					<td width="100%" >
			                					<select name='a2'  multiple="multiple"  ondblclick='setRelationItem("relationItem")'    style="height:150px;width:220px;font-size:9pt"  >
                  									<logic:iterate id="element"  name="accountingForm" property="aimDataList"  >
                  										<option value='<bean:write name="element" property="dataValue" />'><bean:write name="element" property="dataName" /></option>
                  									</logic:iterate>
                  								</select>
			                					</td>
			                					</tr>
		                					
		                      			</table>
		                 </fieldset>
   						 <br>
   						 <fieldset align="center"  >
    							 <legend >选择关联关系</legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"   cellpadding="0">                   		                     			
			                      				<tr>
			                					<td width="100%" >
			                					<select name='relationItem' ondblclick='removeitem("relationItem")'  multiple="multiple" style="height:150px;width:220px;font-size:9pt"  >
                  									<logic:iterate id="element"  name="accountingForm" property="relationItemList"  >
                  										<option value='<bean:write name="element" property="dataValue" />'><bean:write name="element" property="dataName" /></option>
                  									</logic:iterate>
                  								</select>
			                					</td>
			                					</tr>
		                					
		                      			</table>
		                 </fieldset>
   					<Br>
		                 <Input type='button' value='选择关联指标'  class="mybutton"  onclick='setRelationItem("relationItem")' />
		                 <Input type='button' value='撤销关联'  class="mybutton" onclick='removeitem("relationItem")'   />
   				
   				
   				</td>
   				<td>
   				
   						<table width='100px' border="0" cellspacing="1" heigth='240px' align="center" cellpadding="1" >
   						<tr><td width='100%' align="center" style="height:35px">
   						<Input type='button' style="width: 70px;" value=' 原始数据 '  class="mybutton" onclick='outOriginationData()'  />
   						</td></tr>
   						<tr><td width='100%' align="center" style="height:35px">
   						<Input type='button' style="width: 70px;" value='没对应数据'  class="mybutton" onclick="showNoRelationData()"  />
   						</td></tr>
   						<tr><td width='100%' align="center" style="height:35px">
   						<Input type='button' style="width: 70px;" value='有同号数据'  class="mybutton" onclick="showRelationData()"  />
   						</td></tr>
   						<tr><td width='100%' align="center" style="height:35px">
   						<Input type='button' style="width: 70px;" value=' 保存对应 '  class="mybutton" onclick="saveRelation()"  />
   						</td></tr>
   						<tr><td width='100%' align="center" style="height:35px">
   						<Input type='button' style="width: 70px;" value=' 读取对应 '  class="mybutton"  onclick='readRelation()'  />
   						</td></tr>
   						<tr><td width='100%' align="center" style="height:35px">
   						<Input type='button' id="enterbutton" style="width: 70px;" value=' 确    定 '  class="mybutton"  onclick="validate()"  />
   						</td></tr>
   						<tr><td width='100%' align="center" style="height:35px">
   						<Input type='button' style="width: 70px;" value=' 取    消 '  class="mybutton" onclick='goback()'  />
   						</td></tr>
   						
   						
   						</table>
   						
   				</td>
   			</tr>
   			</table>
   	  		</fieldset>
		</td></tr>
	</table>
	
	<input type='hidden' name='schemeName' value="" />
   </html:form>
  <script language='javascript'>
  function goback()
  {
  	document.accountingForm.target="il_body";
	    document.accountingForm.action="/gz/gz_accounting/gz_org_tree.do?b_query=link&salaryid=${accountingForm.salaryid}";
		document.accountingForm.submit();
  
  }
  
   <%
  if(request.getParameter("b_import")!=null)
  {
  %>
  
  		alert("有${accountingForm.rowNums}条记录导入成功!");
  		document.accountingForm.target="il_body";
	    document.accountingForm.action="/gz/gz_accounting/gz_org_tree.do?b_query=link&salaryid=${accountingForm.salaryid}";
		document.accountingForm.submit();
  <%
  }
  %>
  
  <%
  if(request.getParameter("b_validate")!=null)
  {
  %>
  var msg='${accountingForm.msg}';
  if(trim(msg).length>0)
  {
  	alert(msg);
  }
  else
  {
  	enter();
  }
  
  <%
  }
  %>
  
  
  
  </script>
  </body>
</html>
