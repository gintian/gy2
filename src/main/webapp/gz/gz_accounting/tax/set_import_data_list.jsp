<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <script language='javascript'>
  
function checkExcelSelected()
{
	var num=0;
	var a_value = document.taxTableForm.a1.value;
	if(a_value.indexOf("-")!=-1)
	{
		a_value=a_value.substring(1);
	}
	var obj=eval("document.taxTableForm.oppositeItem");	
	for(var i=0;i<obj.options.length;i++)
	{
		var arr = obj.options[i].value.split("=");
		if(arr[0]==a_value)
		{
			num++;
			alert('Excel列<'+a_value+'>已做过对应');
			break;
		}	
	}
	return num;
}
  
function checkTaxMxSelected()
{
	var num=0;
	var a_value = document.taxTableForm.a2.value;
	var a_text = getSelectText("a2");
	a_text = a_text.substring(a_text.indexOf(" ")+1,a_text.lastIndexOf("( ")-1);
	var obj=eval("document.taxTableForm.oppositeItem");	
	for(var i=0;i<obj.options.length;i++)
	{
		var arr = obj.options[i].value.split("=");
		if(arr[1]==a_value)
		{
			num++;
			alert('个税申抱表字段<'+a_text+'>已做过对应');
			break;
		}		
	}
	return num;
}
  
function isRepeatSelect()
{
	var obj=eval("document.taxTableForm.nbaseItem");			
	var a_value = document.taxTableForm.a1.value;
	if(a_value.indexOf("-")!=-1)
	{
		a_value=a_value.substring(1);
	}
	var num=0;
	for(var i=0;i<obj.options.length;i++)
	{
		if(obj.options[i].value==a_value)
			num++;
	}
	return num;
}

function isMultipleSelect(obj_name)
{
	var obj=eval("document.taxTableForm."+obj_name);
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
	 var obj=eval("document.taxTableForm."+obj_name);
	 for(var i=0;i<obj.options.length;i++)
	 {
			if(obj.options[i].selected==true)
				return obj.options[i].text;
	 }
	 
}

/** 过滤日期 和 大字段 类型的指标  */
function setNbaseItem(obj_name)
{
	if(isMultipleSelect("a1")==0)
			return;
	if(isMultipleSelect("a1")>1)
	{
		alert("只能单选");
		return;
	}	
	if(isRepeatSelect()>0)
	{
		return;
	}
	var a1_value=document.taxTableForm.a1.value;
	var a1_text=getSelectText("a1");
	if(a1_value.indexOf("-")==-1)
	{
		alert('人员主集中没名称为<'+a1_text+'>的指标!');
		return ;
	}
	var newValue=a1_value.substring(1);
	var newText=a1_text;
 	var newOption = new Option(newText,newValue);
 	var obj=eval("document.taxTableForm."+obj_name)
	obj.options[obj.options.length]=newOption;
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
	if(checkExcelSelected()>0)
	{
		return;
	}
	if(checkTaxMxSelected()>0)
	{
		return;
	}
	var a1_value=document.taxTableForm.a1.value;
	if(a1_value.indexOf("-")!=-1)
	{
		a1_value = a1_value.substring(1); 
	}
	var a2_value=document.taxTableForm.a2.value;
	var a1_text=getSelectText("a1");
	var a2_text=getSelectText("a2");
	
	var newValue=a1_value+"="+a2_value;
	var newText=trim(a1_text+"="+a2_text.substring(a2_text.indexOf(" "),a2_text.indexOf("(")));
 	var newOption = new Option(newText,newValue);
 	var obj=eval("document.taxTableForm."+obj_name)
	obj.options[obj.options.length]=newOption;
}
//删除 select控件里所有的选项
function delOptions(obj_name)
{
		var obj=eval("document.taxTableForm."+obj_name);
  	for(var i=obj.options.length-1;i>=0;i--)
  	{	
  		obj.options.remove(i);
  	}

}

function enter()
{
	if(document.taxTableForm.oppositeItem.options.length==0)
	{
		alert("请指定关联指标!")
		return;
	}
	if(document.taxTableForm.nbaseItem.options.length==0)
	{
		alert("请指定对应指标!")
		return;
	}
	
	setselectitem("oppositeItem");
	setselectitem("nbaseItem");
	document.taxTableForm.action="/gz/gz_accounting/tax/import_tax_mx_excel.do?b_import=link";
	document.taxTableForm.submit();
}


  </script>
  <body>
     <html:form action="/gz/gz_accounting/tax/import_tax_mx_excel">
   <table align="center"> 
		<tr> <td valign="top"><Br>
			<fieldset align="center" >
    				<legend >选择数据对应指标</legend>
   			<table width="90%" height='100%' border="0" cellspacing="1"  align="center" cellpadding="10"  >
   			<tr>
   				<td align="center" >
   						<fieldset align="center" style="position: relative;top: -18px;"><!-- modify by xiaoyun 2014-10-11 -->
    							<legend >Excel文件列</legend>
                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"   cellpadding="0">                   		                     			
	                      				<tr>
	                					<td width="100%" >
	                					<select name='a1'  multiple="multiple"  ondblclick='setNbaseItem("nbaseItem")' style="height:150px;width:220px;font-size:9pt"  >
          									<logic:iterate id="element"  name="taxTableForm" property="excelDataFiledList"  >
          										<option value='<bean:write name="element" property="dataValue" />'><bean:write name="element" property="dataName" /></option>
          									</logic:iterate>
          								</select>							                  								
	                					</td>
	                					</tr>		                					
                      			</table>
		                 </fieldset>
   						 
   						 <Input type='button' value='选择标识指标' onclick='setNbaseItem("nbaseItem")' class="mybutton"   />
		                 <Input type='button' value='撤销标识指标'  class="mybutton" onclick='removeitem("nbaseItem")'  />
		                  <br>
		                  <br>
   						 <fieldset align="center" >
    							<legend >人员标识</legend>
                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"   cellpadding="0">                   		                     			
	                      				<tr>
	                					<td width="100%" >
	                					<select name='nbaseItem'  multiple="multiple" style="height:150px;width:220px;font-size:9pt"  >
          										<logic:iterate id="element"  name="taxTableForm" property="nbaseList" >
          										<option value='<bean:write name="element" property="dataValue" />'><bean:write name="element" property="dataName" /></option>
          										</logic:iterate>
          								</select>	
	                					</td>
	                					</tr>
                					
                      			</table>
		                 </fieldset>
		                 <Br>
		                 
   				</td>
   				<td align="center" style="padding-right: 0px;">
   						<fieldset align="center" >
    							<legend >个人申报表字段</legend>
                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"   cellpadding="0">                   		                     			
	                      				<tr>
	                					<td width="100%" >
	                					<select name='a2'  multiple="multiple" style="height:150px;width:220px;font-size:9pt"  >
          									<logic:iterate id="element"  name="taxTableForm" property="taxMxField"  >
          										<option value='<bean:write name="element" property="dataValue" />'><bean:write name="element" property="dataName" /></option>
          									</logic:iterate>
          								</select>
	                					</td>
	                					</tr>
                					
                      			</table>
		                 </fieldset>
   						 <br>
   						 <Input type='button' value='指标对应'  class="mybutton"  onclick='setRelationItem("oppositeItem")' />
   						 
		                 <Input type='button' value='撤销对应'  class="mybutton" onclick='removeitem("oppositeItem")'   />
		                 <br>
		       			 <br>
		       			 <br>
   						 <fieldset align="center" style="position: relative;top:-20px;" ><!-- modify by xiaoyun 2014-10-11 -->
    							<legend >指标对应</legend>
                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"   cellpadding="0">                   		                     			
	                      				<tr>
	                					<td width="100%" >
	                					<select name='oppositeItem'  multiple="multiple" style="height:150px;width:220px;font-size:9pt"  >
          									<logic:iterate id="element"  name="taxTableForm" property="oppositeItemList"  >
          										<option value='<bean:write name="element" property="dataValue" />'><bean:write name="element" property="dataName" /></option>
          									</logic:iterate>
          								</select>
	                					</td>
	                					</tr>			                					
                      			</table>
		                 </fieldset>
   					<Br>
		                 
   				</td>
   				<td align="left" valign="top" style="padding-top: 19px;padding-left: 5px;"><!-- modify by xiaoyun 2014-10-11 -->   				
   						<Input type='button' value=' 确   定 '  class="mybutton"  onclick="enter()" style="margin-bottom: 5px;" /><br>
   						<Input type='button' value=' 返   回 '  class="mybutton" onclick="goback()"  />
   						
   				</td>
   			</tr>
   			</table>
   	  		</fieldset>
		</td></tr>
	</table>
	
   </html:form>
  <script language='javascript'>
  function goback()
  {
		var is_back = '${taxTableForm.is_back}';
		if(is_back=="yes")
		{
			document.taxTableForm.target="il_body";
	    	document.taxTableForm.action="/gz/gz_accounting/gz_org_tree.do?br_query=link&salaryid=${taxTableForm.salaryid}";
			document.taxTableForm.submit();
		}else
		{
			document.taxTableForm.target="il_body";
	    	document.taxTableForm.action="/gz/gz_accounting/tax/gz_tax_org_tree.do?b_query=link&is_back=not";
			document.taxTableForm.submit();
		}
  		
  }
  
   <%
  if(request.getParameter("b_import")!=null)
  {
  %>
        alert("${taxTableForm.importInfo}");
  		document.taxTableForm.target="il_body";
	    document.taxTableForm.action="/gz/gz_accounting/tax/gz_tax_org_tree.do?br_link=link";
		document.taxTableForm.submit();
  <%
  }
  %>
  
  </script>
  </body>
</html>
