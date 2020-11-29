<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateListForm" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script LANGUAGE=javascript src="/js/function.js"></script>
<script language="javascript" src="/general/template/templatelist/templatelist.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<%
	TemplateListForm templateListForm = (TemplateListForm)session.getAttribute("templateListForm");
	ArrayList list = (ArrayList)templateListForm.getTargitemlist();
	int number = list.size();//取得记录总数，为了循环
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String manager="";
    String limit_manage_priv="";
    if(templateListForm!=null&&templateListForm.getLimit_manage_priv()!=null){
        //获得变化后设置的管理范围
       limit_manage_priv = templateListForm.getLimit_manage_priv();
    }
    if(limit_manage_priv!=null&&!limit_manage_priv.equals("")){
        manager=userView.getManagePrivCodeValue();
    }
    String bosflag= userView.getBosflag();
%>
<script language="javascript">
///点击确定
function save()
{
	var isSelected=0;///判断是否有记录选中
	var fielditem_array=new Array();//记录所有的字段
	var fieldvalue_array=new Array();//记录字段的值
	var fieldtype_array=new Array();//记录字段的类型
	var count=<%=number%>;
	var index=0;
	for(i=0;i<count;i++)
	{
		var obj1=document.getElementById("edit"+i);
		if(obj1!=null)
		{
			if(obj1.checked)
			{
				if(isSelected==0)
					isSelected="1";
				var value1=document.getElementById("field_item"+i).value;
				var value2=document.getElementById("field_value"+i).value;
				var value3=document.getElementById("field_type"+i).value;
				if(value3=="M"){
                    value2=getEncodeStr(value2);
                }
				fielditem_array[index]=value1;
				fieldvalue_array[index]=value2;
				fieldtype_array[index]=value3;
				index++;
			}
		}
	}
	if(isSelected==0)
	{
		alert("请选择要修改的指标!");
		return;
	}
	var isOnlySelected=0;///只修改已勾选记录
	var obj2=document.getElementsByName("onlySelected");
    if(obj2!=null)
    {
        for(var i=0;i<obj2.length;i++){
          if(obj2[i].checked){
             isOnlySelected=obj2[i].value; 
          }
        }
    }
	var param=new Object();
	param.fielditem_array=fielditem_array;
	param.fieldvalue_array=fieldvalue_array;
	param.fieldtype_array=fieldtype_array;
	param.isOnlySelected=isOnlySelected;
	window.returnValue=param;
	window.close();
}
///1：全选 2：全撤
function batchCheckbox(flag)
{
	var count=<%=number%>;
	if(flag==1)
	{
		for(i=0;i<count;i++)
		{
			var obj1=document.getElementById("edit"+i);
			if(obj1!=null)
			{
				obj1.checked=true;
			}
		}
	}
	else if(flag==2)
	{
		for(i=0;i<count;i++)
		{
			var obj1=document.getElementById("edit"+i);
			if(obj1!=null)
			{
				obj1.checked=false;
			}
		}
	}
}
///验证是否为数字
function IsDigit() 
	{
		return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
	}
//检验数字类型
function checkValue(obj,ind)
	{
	  	if(obj.value.length>0)
	  	{
	  		if(!checkIsNum2(obj.value))
	  		{
	  			alert('请输入数值！');
	  			obj.value='';
	  			obj.focus();
	  		}
	  		if(checkIsNum2(obj.value)){
              var intext = obj.value;
              var ItemLength=document.getElementById("field_length"+ind).value;
              var DescLength=document.getElementById("field_declength"+ind).value; 
              if((intext.length-1)>ItemLength){
               alert("请将位数限制在"+ItemLength+"位以及以下!\r\n注：包括小数位数");
               obj.value='';
               obj.focus();
               return false;
              }
             var inside = intext.indexOf(".");
             if(inside==-1){
                 return true;
             }else{
                 if(DescLength==0){
                    alert("当前指标不允许含有小数位！");
                    obj.value='';
                    obj.focus();
                    return false;
                 }
                 var ss = intext.substring(inside+1,intext.length-1);
                 if(ss.length>DescLength){
                     alert("当前指标的小数点后的位数不能超过"+DescLength+"位!");
                     obj.value='';
                     obj.focus();
                     return false;
                 }
             }
            }
	  	} 
	}
//检验输入文本的长度
function checkAlength(obj,ind){
    var intext = obj.value;
    var ItemLength=document.getElementById("field_length"+ind).value;
    var l=0;
    if(intext.length>0){
	    var a=intext.split(""); 
	    for (var i=0;i<a.length;i++){ 
	        if (a[i].charCodeAt(0)<299) {
	            l++; 
	        } else { 
	           l+=2; 
	        } 
	    }
	    if(l>ItemLength){
	        alert("当前指标的长度不能超过"+ItemLength+"!\r\n注：一个汉字的长度是2");
	       // obj.value='';
            obj.focus();
	    } 
    }
}
//检验输入备注型文本的长度
function checkMlength(obj,ind){
    var ItemLength=document.getElementById("field_length"+ind).value;
    if(ItemLength==0||ItemLength==10){//由于bs在建立备注型指标时默认给的备注型指标的长度是10所以特殊加了处理 xcs 2014-6-17
        return true;
    }else{
       var intext = obj.value;
       var a=intext.split(""); 
       var l=0;
        for (var i=0;i<a.length;i++){ 
            if (a[i].charCodeAt(0)<299) {
                l++; 
            } else { 
               l+=2; 
            } 
        }
        if(l>ItemLength){
            alert("当前指标的长度不能超过"+ItemLength+"!\r\n注：一个汉字的长度是2");
            //obj.value='';
            obj.focus();
        }  
    }
}
function setTPinput(){
    var InputObject=document.getElementsByTagName("input");
    for(var i=0;i<InputObject.length;i++){
        var InputType=InputObject[i].getAttribute("type");
        if(InputType!=null&&(InputType=="text"||InputType=="password")){
            InputObject[i].className=" "+"TEXT4";
        }
    }
}
</script>
<style>
.whole{
height:400px;
overflow-y:auto;
overflow-x:hidden;
}
.t_cell_head {
BACKGROUND-COLOR: #f4f7f7;
BORDER-TOP: #94B6E6 1pt solid;
BORDER-BOTTOM: #94B6E6 1pt solid;
BORDER-LEFT: #94B6E6 0pt solid; 
BORDER-RIGHT: #94B6E6 1pt solid; 
font-size: 12px;
height:22;
}
.t_cell_head_last {
BACKGROUND-COLOR: #f4f7f7;
BORDER-TOP: #94B6E6 1pt solid;
BORDER-BOTTOM: #94B6E6 1pt solid;
BORDER-LEFT: #94B6E6 0pt solid; 
BORDER-RIGHT: #94B6E6 0pt solid; 
font-size: 12px;
height:22;
}
.t_cell_data {
BORDER-TOP: #94B6E6 0pt solid;
BORDER-BOTTOM: #94B6E6 1pt solid; 
BORDER-LEFT: #94B6E6 0pt solid; 
BORDER-RIGHT: #94B6E6 1pt solid; 
font-size: 12px;
height:22;
}
.t_cell_data_last {
BORDER-TOP: #94B6E6 0pt solid;
BORDER-BOTTOM: #94B6E6 1pt solid; 
BORDER-LEFT: #94B6E6 0pt solid; 
BORDER-RIGHT: #94B6E6 0pt solid; 
font-size: 12px;
height:22;
}
</style>
<hrms:themes></hrms:themes>
<%	   
if ("hcm".equals(bosflag)){	   
%>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<%} %>
<body onload="setTPinput()">
<html:form action="/general/template/batchupdatefields">
 <%
     if (!"hcm".equals(bosflag)) {
 %>
<br>
<%
    }
%>
	<table border="0" cellpadding="0" cellspacing="0"  width="490">
		<tr>
		 	<td width="95%" style="padding:0px;">
		  		<fieldset>
		  		<legend>批量修改</legend>
		  		<div class="whole" id='Wdiv'>
		   			
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							
							<tr class="fixedHeaderTr">
							    <td align="center" class="t_cell_head common_background_color common_border_color" width="10%">&nbsp;</td>
						    	<td align="center" class="t_cell_head common_background_color common_border_color" width="30%">指标名称</td>
						    	<td align="center" class="t_cell_head common_background_color common_border_color" width="50%">修改值</td>
						    	<td align="center" class="t_cell_head_last common_background_color common_border_color" width="10%">修改</td>
						 	</tr>
						 	<logic:iterate id="element"    name="templateListForm"  property="targitemlist" indexId="index">
						 	<input type="hidden" id="field_item<%=index %>" value="<bean:write name="element" property="field_name"/>_2" />
						 	<input type="hidden" id="field_type<%=index %>" value="<bean:write name="element" property="field_type"/>" />
							 	<tr style="height: 28px;">
								 	<td align="center" class="t_cell_data common_border_color" style="height: 30px;">
								 		${index+1}
								 	</td>
								 	<td align="left" class="t_cell_data common_border_color" style="height: 30px;">
								 		&nbsp;拟<bean:write name="element" property="field_hz" filter="true"/>
								 	</td>
								 	
								 		<logic:equal name="element" property="field_type"  value="A" >
								 			<logic:equal name="element" property="codeid"  value="0" >
								 			    <%
                               
                                                        int indexof=index.intValue();
                                                        LazyDynaBean bean = (LazyDynaBean)list.get(indexof);
                                                        String item_id = (String)bean.get("field_name");
                                                        String length = (String)bean.get("itemlength");
                                                        int itemlength = Integer.parseInt(length);

                                                %>
								 				<td align="left" class="t_cell_data common_border_color" style="height: 30px;">
								 					&nbsp;
								 					<input type="text" id="field_value<%=index %>" onblur="checkAlength(this,<%=index %>)">
								 					<input type="hidden" id="field_length<%=index %>" value="<%=itemlength %>">
								 				</td>
								 			</logic:equal>
								 			<logic:notEqual name="element" property="codeid" value="0" >
								 				<input type="hidden" name="<%="targitemlist["+index+"].value"%>" id="field_value<%=index %>">
								 				<td align="left" class="t_cell_data common_border_color" style="height: 30px;">
								 					&nbsp;
									 				<input type="text" name="<%="targitemlist["+index+"].viewvalue"%>" readonly vertical-align="middle">
							    					<%
							    						//如果单位、部门、岗位的弹出对话框不符合规范，则用这种方法，把组织机构和普通代码类分开处理。
							    						int indexof=index.intValue();
							    						LazyDynaBean bean = (LazyDynaBean)list.get(indexof);
							    					    String item_id = (String)bean.get("field_name");
							    					    if(item_id.equalsIgnoreCase("b0110") || item_id.equalsIgnoreCase("e0122") || item_id.equalsIgnoreCase("e01a1")){
							    					%>
							    						
							    						<img src="/images/code.gif" style="vertical-align:bottom" onclick='openInputCodeDialogOrgInputPosForBatchUpdate("<bean:write  name="element" property="codeid"/>","<%="targitemlist["+index+"].viewvalue"%>","<%=manager %>","1","1");'/>
							    						
							    					<%
							    					    } else if(item_id.equalsIgnoreCase("parentid")){
							    					%>
							    					
							    					    <img src="/images/code.gif" style="vertical-align:bottom"  onclick='openInputCodeDialogOrgInputPosForBatchUpdate("<bean:write  name="element" property="codeid"/>","<%="targitemlist["+index+"].viewvalue"%>","<%=manager %>","1","0");'/>
							    					
							    					<%
							    					    }else{
							    					 %>
							    					
							    					 	<img src="/images/code.gif" style="vertical-align:bottom"  onclick='openInputCodeDialog("<bean:write  name="element" property="codeid"/>","<%="targitemlist["+index+"].viewvalue"%>");'/>
							    					 </span>
							    					 <%   	
							    					    }
							    					%>
							    					
							    				</td>
								 			</logic:notEqual>
								 		</logic:equal>
								 		<logic:equal name="element" property="field_type"  value="D" >
								 			<td align="left" class="t_cell_data common_border_color" style="height: 30px;">
								 				&nbsp;
								 				<input type="text" id="field_value<%=index %>" onclick="popUpCalendar(this,this, dateFormat,'','',true,false);" readonly>
						    				</td>
								 		</logic:equal>
								 		<logic:equal name="element" property="field_type"  value="M" >
								 		<%
                        
                                           int indexof=index.intValue();
                                           LazyDynaBean bean = (LazyDynaBean)list.get(indexof);
                                           String item_id = (String)bean.get("field_name");
                                           String length = (String)bean.get("itemlength");
                                           int itemlength = Integer.parseInt(length);
                                     
                                         %>
								 			<td align="left" class="t_cell_data common_border_color" style="height: 30px;">
								 				&nbsp;
								 				<textarea id="field_value<%=index %>" cols="19" rows="2" onblur="checkMlength(this,<%=index %>)" ></textarea>
								 				<input type="hidden" id="field_length<%=index %>" value="<%=itemlength %>">
						    				</td>
								 		</logic:equal>
								 		<logic:equal name="element" property="field_type"  value="N" >
								 		<%
                                           int indexof=index.intValue();
                                           LazyDynaBean bean = (LazyDynaBean)list.get(indexof);
                                           String item_id = (String)bean.get("field_name");
                                           String decimalwidth = (String)bean.get("decimalwidth");
                                           String length = (String)bean.get("itemlength");
                                           int itemlength = Integer.parseInt(length);
                                           int declength=Integer.parseInt(decimalwidth);
                                         %>
								 			<td align="left" class="t_cell_data common_border_color" style="height: 30px;">
								 				&nbsp;
								 				<input type="text" id="field_value<%=index %>" onkeypress="event.returnValue=IsDigit();" onblur="checkValue(this,<%=index %>)">
								 				<input type="hidden" id="field_length<%=index %>"  value="<%=itemlength %>">
                                                <input type="hidden" id="field_declength<%=index %>" value="<%=declength %>">
								 			</td>
								 		</logic:equal>
								 	
								 	<td align="center" class="t_cell_data_last common_border_color" style="height: 30px;">
						      		 	<input type="checkbox" id="edit<%=index%>" />
								 	</td>
							 	</tr>
						 	</logic:iterate>
						</table>
						</div>
		   		</fieldset>
			</td>
			<td width="5%" valign="top" style="padding-left:5px;padding-top:8px;">
				<table border="0" cellpadding="0" cellspacing="0">
					<tr>	
					  <td >					
							<input type="button" class="mybutton" value="全选" onclick="batchCheckbox('1');"  style="margin-bottom:12px"/>
						<br>
							<input type="button" class="mybutton" value="全撤" onclick="batchCheckbox('2');"/>
						</td>
					</tr>

				</table>
			</td>
		</tr>
	</table>
	
	<table border="0" cellpadding="0" cellspacing="0" style="margin-top:5px" width="100%" align="center" >
		<tr>
			<td>
				<!--  <input type="checkbox" id="onlySelected" />只修改勾选数据 -->
				<input type="radio" name="onlySelected" id="all"  value="0" />全部记录
                <input type="radio" name="onlySelected" id="onlysel" value="1" checked/>选中记录
			</td>
		</tr>
		<tr height="35px;" > 
			<td align="center" valgin="middle">
				<input type="button" class="mybutton" value="确定" onclick="save();"/>
				<input type="button" class="mybutton" value="关闭" onclick="window.close();"/>
			</td>
		</tr>
	
	</table>
	
</html:form>
</body>