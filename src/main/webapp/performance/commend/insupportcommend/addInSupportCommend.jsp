<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.performance.commend.insupportcommend.InSupportCommendForm"%>
<html>

<head>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<!-- 【5794】干部考察：后备推荐，候选人提名中，点击新建，在选择时间框的时候时间控件不对，
且选择时间之后无法保存，提示时间格式无效    jingq add 2014.12.16-->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script language="javascript">
function add(){
 <%int n=0;%> 
<logic:iterate id="element" name="inSupportCommendForm" property="sysList" indexId="index">
<logic:equal name="element" property="itemtype" value="D">
var a<%=n%>=document.getElementsByName("sysList[<%=n%>].itemid");
if(a<%=n%>[0].value!=null&&trim(a<%=n%>[0].value)!=''){
if(!checkDateTime(a<%=n%>[0].value)){
alert("<bean:write  name="element" property="desc"/>的值["+a<%=n%>[0].value+"]不是有效的时间格式\r\n正确的格式为[YYYY-MM-DD]")
return;
}
}
 </logic:equal>
<logic:equal name="element" property="itemtype" value="N">
 var a<%=n%>=document.getElementsByName("sysList[<%=n%>].itemid");
  if(a<%=n%>[0].value !=''){
                  var myReg =/^(-?\d+)(\.\d+)?$/
		  if(!myReg.test(a<%=n%>[0].value)) 
		   {
		    alert("<bean:write  name="element" property="desc"/>请输入数字！");
		    return;
		   }
		   else{
		
		if(a<%=n%>[0].value.indexOf(".")==-1)
		{
		     if(trim(a<%=n%>[0].value).length>parseInt("<bean:write name="element" property="intLength"/>"))
		     {
		     alert("<bean:write  name="element" property="desc"/>值超出指定范围 整数位最大应为<bean:write name="element" property="intLength"/>");
		     return;
		     }
		}else
		{
		      var iteml=a<%=n%>[0].value.substring(0,a<%=n%>[0].value.indexOf("."));
		      if(trim(iteml).length>parseInt("<bean:write name="element" property="intLength"/>"))
		      {
		           alert("<bean:write  name="element" property="desc"/>值超出指定范围 整数位最大应为<bean:write name="element" property="intLength"/>");
		           return;
		      }
		}
		}
		}
</logic:equal>
<logic:equal name="element" property="itemtype" value="M">

</logic:equal>
<logic:equal name="element" property="itemtype" value="A">
<logic:equal name="element" property="codesetid" value="0">
<logic:equal name="element" property="itemid" value="p0203">
var a<%=n%>=document.getElementsByName("sysList[<%=n%>].itemid");
if(a<%=n%>[0].value == ''){
alert("<bean:write  name="element" property="desc"/>不允许为空");
return;
}
</logic:equal>
<logic:notEqual name="element" property="itemid" value="p0203">
</logic:notEqual>
</logic:equal>
<logic:notEqual name="element" property="codesetid" value="0">
<logic:equal name="element" property="itemid" value="p0209">

</logic:equal>
<logic:notEqual name="element" property="itemid" value="p0209">
</logic:notEqual>
</logic:notEqual>

</logic:equal>
<%n++;%>
</logic:iterate>
var param=inSupportCommendForm.ctrl_param.value;
if(param==null||param==''){
alert("最多推荐人数不能为空");
return;
}else{
 var myReg =/^(-?\d+)(\.\d+)?$/
 if(!myReg.test(param)) 
		   {
		    alert("最多推荐人数请输入数字！");
		    return;
		   }
		}

inSupportCommendForm.action="/performance/commend/insupportcommend/addInSupportCommend.do?b_add=add&oper=2";
inSupportCommendForm.submit();
}
</script>
</head>
<body>
 <base id="mybase" target="_self">
<html:form action="/performance/commend/insupportcommend/addInSupportCommend">
<br>
<br>
<table width="90%" border="0"  cellspacing="1"  align="center" cellpadding="1" class="ListTable complex_border_color">
<thead>
<tr>
<td align="left" class="TableRow" colspan="2" nowrap><bean:message key="label.commend.new"/>
</td>
</tr>
</thead>
<tr><td align="right" width="30%">推荐职务指标</td>
<td>
<hrms:optioncollection name="inSupportCommendForm" property="commendFieldList" collection="list" />
			<html:select name="inSupportCommendForm" property="commendField" size="1" style="width:200px;">
				             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
			</html:select><font color="red">(后备干部人员名单表中不关联单位和部门的代码型指标)</font>
</td>
</tr>
<logic:iterate id="element" name="inSupportCommendForm" property="sysList" indexId="index">
 <tr>
 <td align="right" width="30%"><bean:write name="element" property="desc"/>
 <input type='hidden' name="<%="sysList["+index+"].columns"%>" value="<bean:write name="element" property="itemid"/>"/>
 <input type='hidden' name="<%="sysList["+index+"].itemtype"%>" value="<bean:write name="element" property="itemtype"/>"/>
 <input type='hidden' name="<%="sysList["+index+"].codesetid"%>" value="<bean:write name="element" property="codesetid"/>"/>
 
 </td>
 <td align="left">
 <logic:equal name="element" property="itemtype" value="D">
 <input type="text" name="<%="sysList["+index+"].itemid"%>" size='30' maxlength='<bean:write name="element" property="itemlength"/>' value="<bean:write name="element" property="initvalue"/>"  extra="editor" id="editor2"  dropDown="dropDownDate"/>
</logic:equal>
<logic:equal name="element" property="itemtype" value="N">
<input type="text" name="<%="sysList["+index+"].itemid"%>" size='30' maxlength='<bean:write name="element" property="itemlength"/>' value="<bean:write name="element" property="initvalue"/>"  extra="editor"/>
</logic:equal>
<logic:equal name="element" property="itemtype" value="M">
<input type="text" name="<%="sysList["+index+"].itemid"%>" size='30' maxlength='<bean:write name="element" property="itemlength"/>' value="<bean:write name="element" property="initvalue"/>"  extra="editor"/>
</logic:equal>
<logic:equal name="element" property="itemtype" value="A">
<logic:equal name="element" property="codesetid" value="0">
<logic:equal name="element" property="itemid" value="p0203">
<input type="text" name="<%="sysList["+index+"].itemid"%>" size='30' maxlength='<bean:write name="element" property="itemlength"/>' value="<bean:write name="element" property="initvalue"/>"  extra="editor"/>
</logic:equal>
<logic:notEqual name="element" property="itemid" value="p0203">
<input type="text" name="<%="sysList["+index+"].itemid"%>" size='30' maxlength='<bean:write name="element" property="itemlength"/>' value="<bean:write name="element" property="initvalue"/>"  extra="editor"/>
</logic:notEqual>
</logic:equal>
<logic:notEqual name="element" property="codesetid" value="0">
<logic:equal name="element" property="itemid" value="p0209">
<input type="hidden" name="<%="sysList["+index+"].itemid"%>" value="01"  extra="editor"/>
<input type="text" name="item" value="起草" size='30' extra="editor" readonly/>
</logic:equal>
<logic:notEqual name="element" property="itemid" value="p0209">
<input type="hidden" name="<%="sysList["+index+"].value"%>" value="<bean:write name="element" property="value"/>" />
<input type="text" size='30' name="<%="sysList["+index+"].viewvalue"%>" value="<bean:write name="element" property="initvalue"/>"  extra="editor" readonly/>

 <img src="/images/code.gif" onclick='javascript:openInputCodeDialog("<bean:write  name="element" property="codesetid"/>","<%="sysList["+index+"].viewvalue"%>");'/>
</logic:notEqual>
</logic:notEqual>

</logic:equal>
</td>
</tr>
</logic:iterate>
<tr><td align="right" width="30%">最多推荐人数</td>
<td align='left'><input type='text' name='ctrl_param' value='' maxlength='3' size='30' extra='editor'/></td>
</tr>

<tr>
<td colspan="2" align="center">
</td>
</tr>
</table>
<table  width="90%" border="0" align="center">
          <tr>
            <td align="center"> 
             <button class="mybutton" name="" onclick="add();"><bean:message key="button.save"/></button>&nbsp;&nbsp;&nbsp;&nbsp;
             <button class="mybutton" name="" onclick="history.back();"><bean:message key="button.return"/></button>
         	</td>
         </tr>
</table>
</html:form>
</body>
</html>