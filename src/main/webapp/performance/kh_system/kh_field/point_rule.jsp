<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function changeDisplay(obj)
{
var value="";
     for(var i=0;i<obj.options.length;i++)
     {
         if(obj.options[i].selected)
         {
            value=obj.options[i].value;
            break;
         }
     }
     if(value=='0')
     {
        document.getElementById("addvalid").style.display="none";
        document.getElementById("all").style.display="none";
        document.getElementById("abut").style.display="none";
        document.getElementById("lt").style.display="none";
        if(document.getElementById("lt")!=null)
        {
          document.getElementById("lt").style.display="none";
        }
        if(document.getElementById("lt2")!=null)
        {
          document.getElementById("lt2").style.display="none";
        }
     }
     if(value=='1')
     {
        document.getElementById("addvalid").style.display="block";
        document.getElementById("all").style.display="block";
        document.getElementById("abut").style.display="none";
        document.getElementById("lt").style.display="block";
        var obj= document.getElementById("sid");
        baifen(obj);
     }
     if(value=='3')
     {
          document.getElementById("addvalid").style.display="none";
          document.getElementById("all").style.display="none";
          document.getElementById("abut").style.display="block";
          document.getElementById("lt").style.display="none";
          if(document.getElementById("lt")!=null)
         {
          document.getElementById("lt").style.display="none";
         }
         if(document.getElementById("lt2")!=null)
         {
          document.getElementById("lt2").style.display="none";
         }
     }
     if(value=='2')
     {
        document.getElementById("addvalid").style.display="none";
        document.getElementById("all").style.display="none";
        document.getElementById("abut").style.display="block";
        document.getElementById("lt").style.display="block";
       
     }
}
function baifen(obj)
{
    var ruleObj=document.getElementsByName("rule")[0];
    var ruleValue="";
    for(var i=0;i<ruleObj.options.length;i++)
    {
        if(ruleObj.options[i].selected)
           ruleValue=ruleObj.options[i].value;
    }
    if(ruleValue=="1")
    {
        var value="";
        for(var i=0;i<obj.options.length;i++)
        {
           if(obj.options[i].selected)
           {
              value=obj.options[i].value;
              break;
           }
        }
        if(value=='1')
        {
           document.getElementById("pp").style.display="block";
           document.getElementById("pp2").style.display="block";
        }
        else
        {
           document.getElementById("pp").style.display="none";
           document.getElementById("pp2").style.display="none";
        }
     }
}
function addBaseRule(point_id,type)
{
   var theurl="/performance/achivement/standarditem/search_standarditem_list.do?b_baserule=base`point_id="+point_id+"`type="+type;
         var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    var config = {
        width:570,
        height:420,
        type:'2'
    }
    modalDialog.showModalDialogs(iframe_url,"addBaseRule_win",config);
}
function addrul(point_id,type)
{         
         var theurl="/performance/achivement/standarditem/search_standarditem_list.do?b_init=init`point_id="+point_id+"`type="+type;
         var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
     var config = {
        width:620,
        height:430,
        type:'2'
    }
    modalDialog.showModalDialogs(iframe_url,"addrul_win",config);
}
function save()
{
    var checkFloat = /^(-?\d+)(\.\d+)?$/;
    var add_value=document.getElementsByName("add_value")[0].value;
    var add_score=document.getElementsByName("add_score")[0].value;
    var minus_value=document.getElementsByName("minus_value")[0].value;
    var minus_score=document.getElementsByName("minus_score")[0].value;
    if(add_value!=null&&trim(add_value)!='')
    { 
       if(!checkFloat.test(add_value))
       {
           alert("请输入数值!");
           document.getElementsByName("add_value")[0].value="";
           return;
       }
    }
    if(add_score!=null&&trim(add_score)!='')
    { 
       if(!checkFloat.test(add_score))
       {
            alert("请输入数值!");
            document.getElementsByName("add_score")[0].value="";
           return;
       }
    }
    if(minus_value!=null&&trim(minus_value)!='')
    { 
       if(!checkFloat.test(minus_value))
       {
           alert("请输入数值!");
           document.getElementsByName("minus_value")[0].value="";
           return;
       }
    }
    if(minus_score!=null&&trim(minus_score)!='')
    { 
       if(!checkFloat.test(minus_score))
       {
           alert("请输入数值!");
           document.getElementsByName("minus_score")[0].value="";
           return;
       }
    }
     var av=document.getElementById("av");
       var mv=document.getElementById("mv");
       var cv=document.getElementById("cv");
       if(!cv.checked)
       {
           cv.value="0";
           cv.checked=true;
       }
       if(!av.checked)
       {
           av.value="0";
           av.checked=true;
       }
       if(!mv.checked)
       {
          mv.value="0";
          mv.checked=true;
       }
khFieldForm.action="/performance/kh_system/kh_field/add_edit_field.do?b_saverule=init&closeVar=1";
khFieldForm.submit();
}
<%if(request.getParameter("closeVar")!=null&&request.getParameter("closeVar").equals("1")){%>
parent.window.close();
<%}%>
//-->
</script>
<html:form action="/performance/kh_system/kh_field/add_edit_field">
  <table width="98%" align="center" class="ListTable"> 
  <tr> <td class="TableRow" valign="middle" align='left'>
  定量指标计分规则
  </td>
  </tr>
			<tr> <td class="RecordRow" valign="middle" align='center'>
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
				 <tr> <td align=left colspan=5> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:checkbox styleId="cv" property="convert" value="1" name="khFieldForm">
			<bean:message key="kh.field.convert"/>
			 </html:checkbox></td></tr>
				<tr height="110px">
				<td valign="middle" align="center" width="15%">
				<html:radio property="pointtype" value="0" name="khFieldForm"><bean:message key="kh.field.base"/></html:radio>
				</td>
				<td valign="middle" align="left" width="85%">
				<fieldset style="height:90px">
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
				
				<tr>
				<td align="left" rowspan="2" width="120" style="height:60px" nowrap>
				<bean:message key="kh.field.an"/><html:select name="khFieldForm" property="rule" onchange="changeDisplay(this);">
<html:option value="0"><bean:message key="kh.field.lf"/></html:option>
<html:option value="1"><bean:message key="kh.field.jd"/></html:option>
<html:option value="2"><bean:message key="kh.field.fd"/></html:option>
<html:option value="3"><bean:message key="kh.field.pm"/></html:option>
</html:select></td><td>



<logic:equal value="0" property="rule" name="khFieldForm">
<table id="addvalid" style="display:none">
<tr><td>
<html:checkbox styleId="av" property="add_valid" value="1" name="khFieldForm"><bean:message key="kh.field.addscore"/></html:checkbox>
	<html:select name="khFieldForm" property="add_type">
<html:option value="1"><bean:message key="kh.field.mg"/></html:option>
<html:option value="0"><bean:message key="kh.field.md"/></html:option>
</html:select>&nbsp;&nbsp;<html:text property="add_value" name="khFieldForm" size="5" styleClass="inputtext"></html:text></td>
<logic:equal value="1" property="rule" name="khFieldForm">
    <logic:equal value="1" name="khFieldForm" property="ltype">
    <td id="pp" style="display:block">%</td>
    </logic:equal>
    <logic:notEqual value="1" name="khFieldForm" property="ltype">
    <td id="pp" style="display:none">%</td>
    </logic:notEqual>
</logic:equal>
<logic:notEqual value="1" name="khFieldForm" property="rule">
    <td id="pp" style="display:none">%</td>
    </logic:notEqual>
<td>
<bean:message key="kh.field.add"/><html:text property="add_score" name="khFieldForm"  size="5" styleClass="inputtext"></html:text>&nbsp;&nbsp;

</td></tr></table>
</logic:equal>
<logic:equal value="1" property="rule" name="khFieldForm">
<table id="addvalid" style="display:block">
<tr><td>
<html:checkbox styleId="av" property="add_valid" value="1" name="khFieldForm"><bean:message key="kh.field.addscore"/></html:checkbox>
	<html:select name="khFieldForm" property="add_type">
<html:option value="1"><bean:message key="kh.field.mg"/></html:option>
<html:option value="0"><bean:message key="kh.field.md"/></html:option>
</html:select>&nbsp;&nbsp;<html:text property="add_value" name="khFieldForm" size="5" styleClass="inputtext"></html:text></td>
<logic:equal value="1" property="rule" name="khFieldForm">
    <logic:equal value="1" name="khFieldForm" property="ltype">
    <td id="pp" style="display:block">%</td>
    </logic:equal>
    <logic:notEqual value="1" name="khFieldForm" property="ltype">
    <td id="pp" style="display:none">%</td>
    </logic:notEqual>
</logic:equal>
<logic:notEqual value="1" name="khFieldForm" property="rule">
    <td id="pp" style="display:none">%</td>
    </logic:notEqual>
<td>
<bean:message key="kh.field.add"/><html:text property="add_score" name="khFieldForm"  size="5" styleClass="inputtext"></html:text>&nbsp;&nbsp;

</td></tr></table>
</logic:equal>
<logic:equal value="2" property="rule" name="khFieldForm">
<table id="addvalid" style="display:none">
<tr><td>
<html:checkbox styleId="av" property="add_valid" value="1" name="khFieldForm"><bean:message key="kh.field.addscore"/></html:checkbox>
	<html:select name="khFieldForm" property="add_type">
<html:option value="1"><bean:message key="kh.field.mg"/></html:option>
<html:option value="0"><bean:message key="kh.field.md"/></html:option>
</html:select>&nbsp;&nbsp;<html:text property="add_value" name="khFieldForm"  size="5" styleClass="inputtext"></html:text></td>
<logic:equal value="1" property="rule" name="khFieldForm">
    <logic:equal value="1" name="khFieldForm" property="ltype">
    <td id="pp" style="display:block">%</td>
    </logic:equal>
    <logic:notEqual value="1" name="khFieldForm" property="ltype">
    <td id="pp" style="display:none">%</td>
    </logic:notEqual>
</logic:equal>
<logic:notEqual value="1" name="khFieldForm" property="rule">
    <td id="pp" style="display:none">%</td>
    </logic:notEqual>
<td>
<bean:message key="kh.field.add"/><html:text property="add_score" name="khFieldForm"  size="5" styleClass="inputtext"></html:text>&nbsp;&nbsp;

</td></tr></table>
</logic:equal>
<logic:equal value="3" property="rule" name="khFieldForm">
<table id="addvalid" style="display:none">
<tr><td>
<html:checkbox styleId="av" property="add_valid" value="1" name="khFieldForm"><bean:message key="kh.field.addscore"/></html:checkbox>
	<html:select name="khFieldForm" property="add_type">
<html:option value="1"><bean:message key="kh.field.mg"/></html:option>
<html:option value="0"><bean:message key="kh.field.md"/></html:option>
</html:select>&nbsp;&nbsp;<html:text property="add_value" name="khFieldForm"  size="5" styleClass="inputtext"></html:text></td>
<logic:equal value="1" property="rule" name="khFieldForm">
    <logic:equal value="1" name="khFieldForm" property="ltype">
    <td id="pp" style="display:block">%</td>
    </logic:equal>
    <logic:notEqual value="1" name="khFieldForm" property="ltype">
    <td id="pp" style="display:none">%</td>
    </logic:notEqual>
</logic:equal>
<logic:notEqual value="1" name="khFieldForm" property="rule">
    <td id="pp" style="display:none">%</td>
    </logic:notEqual>
<td>
<bean:message key="kh.field.add"/><html:text property="add_score" name="khFieldForm"  size="5" styleClass="inputtext"></html:text>&nbsp;&nbsp;

</td></tr></table>
</logic:equal>
				</td>
				</tr>
				<tr>
				<td>		
				<logic:equal value="0" property="rule" name="khFieldForm">
				<table id="all" style="display:none"><tr><td>
				<html:checkbox styleId="mv" property="minus_valid" value="1" name="khFieldForm"><bean:message key="kh.field.minusscore"/></html:checkbox>
	<html:select name="khFieldForm" property="minus_type">
<html:option value="1"><bean:message key="kh.field.mg"/></html:option>
<html:option value="0"><bean:message key="kh.field.md"/></html:option>
</html:select>&nbsp;&nbsp;<html:text property="minus_value" name="khFieldForm"  size="5" styleClass="inputtext"></html:text></td>
<logic:equal value="1" property="rule" name="khFieldForm">
    <logic:equal value="1" name="khFieldForm" property="ltype">
    <td id="pp2" style="display:block">%</td>
    </logic:equal>
    <logic:notEqual value="1" name="khFieldForm" property="ltype">
    <td id="pp2" style="display:none">%</td>
    </logic:notEqual>
</logic:equal>
<logic:notEqual value="1" name="khFieldForm" property="rule">
    <td id="pp2" style="display:none">%</td>
    </logic:notEqual>
<td>
<bean:message key="kh.field.minus"/><html:text property="minus_score" name="khFieldForm"  size="5" styleClass="inputtext"></html:text>

				</td>
				</tr>
				</table>
				</logic:equal>
		<logic:equal value="1" property="rule" name="khFieldForm">
				<table id="all" style="display:block"><tr><td>
				<html:checkbox styleId="mv" property="minus_valid" value="1" name="khFieldForm"><bean:message key="kh.field.minusscore"/></html:checkbox>
	<html:select name="khFieldForm" property="minus_type">
<html:option value="1"><bean:message key="kh.field.mg"/></html:option>
<html:option value="0"><bean:message key="kh.field.md"/></html:option>
</html:select>&nbsp;&nbsp;<html:text property="minus_value" name="khFieldForm"  size="5" styleClass="inputtext"></html:text></td>
<logic:equal value="1" property="rule" name="khFieldForm">
    <logic:equal value="1" name="khFieldForm" property="ltype">
    <td id="pp2" style="display:block">%</td>
    </logic:equal>
    <logic:notEqual value="1" name="khFieldForm" property="ltype">
    <td id="pp2" style="display:none">%</td>
    </logic:notEqual>
</logic:equal>
<logic:notEqual value="1" name="khFieldForm" property="rule">
    <td id="pp2" style="display:none">%</td>
    </logic:notEqual>
<td>
<bean:message key="kh.field.minus"/><html:text property="minus_score" name="khFieldForm"  size="5" styleClass="inputtext"></html:text>

				</td>
				</tr>
				</table>
				</logic:equal>		
				<logic:equal value="2" property="rule" name="khFieldForm">
				<table id="all" style="display:none"><tr><td>
				<html:checkbox styleId="mv" property="minus_valid" value="1" name="khFieldForm"><bean:message key="kh.field.minusscore"/></html:checkbox>
	<html:select name="khFieldForm" property="minus_type">
<html:option value="1"><bean:message key="kh.field.mg"/></html:option>
<html:option value="0"><bean:message key="kh.field.md"/></html:option>
</html:select>&nbsp;&nbsp;<html:text property="minus_value" name="khFieldForm"  size="5" styleClass="inputtext"></html:text></td>
<logic:equal value="1" property="rule" name="khFieldForm">
    <logic:equal value="1" name="khFieldForm" property="ltype">
    <td id="pp2" style="display:block">%</td>
    </logic:equal>
    <logic:notEqual value="1" name="khFieldForm" property="ltype">
    <td id="pp2" style="display:none">%</td>
    </logic:notEqual>
</logic:equal>
<logic:notEqual value="1" name="khFieldForm" property="rule">
    <td id="pp2" style="display:none">%</td>
    </logic:notEqual>
<td>
<bean:message key="kh.field.minus"/><html:text property="minus_score" name="khFieldForm"  size="5" styleClass="inputtext"></html:text>

				</td>
				</tr>
				</table>
				</logic:equal>
				<logic:equal value="3" property="rule" name="khFieldForm">
				<table id="all" style="display:none"><tr><td>
				<html:checkbox styleId="mv" property="minus_valid" value="1" name="khFieldForm"><bean:message key="kh.field.minusscore"/></html:checkbox>
	<html:select name="khFieldForm" property="minus_type">
<html:option value="1"><bean:message key="kh.field.mg"/></html:option>
<html:option value="0"><bean:message key="kh.field.md"/></html:option>
</html:select>&nbsp;&nbsp;<html:text property="minus_value" name="khFieldForm"  size="5" styleClass="inputtext"></html:text></td>
<logic:equal value="1" property="rule" name="khFieldForm">
    <logic:equal value="1" name="khFieldForm" property="ltype">
    <td id="pp2" style="display:block">%</td>
    </logic:equal>
    <logic:notEqual value="1" name="khFieldForm" property="ltype">
    <td id="pp2" style="display:none">%</td>
    </logic:notEqual>
</logic:equal>
<logic:notEqual value="1" name="khFieldForm" property="rule">
    <td id="pp2" style="display:none">%</td>
    </logic:notEqual>
<td>
<bean:message key="kh.field.minus"/><html:text property="minus_score" name="khFieldForm"  size="5" styleClass="inputtext"></html:text>

				</td>
				</tr>
				</table>
				</logic:equal>			
				</td>
				<td align="left">			
				
				<logic:equal value="0" property="rule" name="khFieldForm">
				    <table id="abut" style="display:none"><tr><td>
				        <input type="button" name="addrule" class="mybutton" value="<bean:message key="kh.field.rule"/>" onclick="addBaseRule('${khFieldForm.rulePointid}','${khFieldForm.type}');"/>
				     </td></tr></table>
				     </logic:equal>
				    <logic:equal value="1" property="rule" name="khFieldForm">
				    <table id="abut" style="display:none"><tr><td>
				        <input type="button" name="addrule" class="mybutton" value="<bean:message key="kh.field.rule"/>" onclick="addBaseRule('${khFieldForm.rulePointid}','${khFieldForm.type}');"/>
				     </td></tr></table>
				     </logic:equal> 
				     <logic:equal value="2" property="rule" name="khFieldForm">
				    <table id="abut" style="display:block"><tr><td>
				        <input type="button" name="addrule" class="mybutton" value="<bean:message key="kh.field.rule"/>" onclick="addBaseRule('${khFieldForm.rulePointid}','${khFieldForm.type}');"/>
				     </td></tr></table>
				     </logic:equal> 
				     <logic:equal value="3" property="rule" name="khFieldForm">
				    <table id="abut" style="display:block"><tr><td>
				        <input type="button" name="addrule" class="mybutton" value="<bean:message key="kh.field.rule"/>" onclick="addBaseRule('${khFieldForm.rulePointid}','${khFieldForm.type}');"/>
				     </td></tr></table>
				     </logic:equal> 		     
				</td>
				</tr>
				
				<logic:equal value="1" property="rule" name="khFieldForm">
				<tr id="lt">
				<td align="left" colspan="2">
				<bean:message key="kh.field.an"/><html:select styleId="sid" name="khFieldForm" property="ltype" onchange="baifen(this);">
<html:option value="0"><bean:message key="kh.field.ce"/></html:option>
<html:option value="1"><bean:message key="kh.field.bl"/></html:option>
</html:select><bean:message key="kh.field.js"/>
				</td>
				</tr>
				</logic:equal>
			<logic:equal value="2" property="rule" name="khFieldForm">
				
				<tr id="lt">
				<td align="left" colspan="2">
				<bean:message key="kh.field.an"/><html:select styleId="sid" name="khFieldForm" property="ltype" onchange="baifen(this);">
<html:option value="0"><bean:message key="kh.field.ce"/></html:option>
<html:option value="1"><bean:message key="kh.field.bl"/></html:option>
</html:select><bean:message key="kh.field.js"/>
				</td>
				</tr>
				</logic:equal>
				<logic:notEqual value="1" property="rule" name="khFieldForm">
				<logic:notEqual value="2" property="rule" name="khFieldForm">
				<logic:equal value="0" property="rule" name="khFieldForm">
				<tr id="lt" style="display:none">
				<td align="left" colspan="3">
				<bean:message key="kh.field.an"/><html:select styleId="sid" name="khFieldForm" property="ltype" onchange="baifen(this);">
<html:option value="0"><bean:message key="kh.field.ce"/></html:option>
<html:option value="1"><bean:message key="kh.field.bl"/></html:option>
</html:select><bean:message key="kh.field.js"/>
				</td>
				</tr>
				</logic:equal>
				<logic:equal value="3" property="rule" name="khFieldForm">
				<tr id="lt" style="display:none">
				<td align="left" colspan="2">
				<bean:message key="kh.field.an"/><html:select styleId="sid" name="khFieldForm" property="ltype" onchange="baifen(this);">
<html:option value="0"><bean:message key="kh.field.ce"/></html:option>
<html:option value="1"><bean:message key="kh.field.bl"/></html:option>
</html:select><bean:message key="kh.field.js"/>
				</td>
				</tr>
				
				</logic:equal>
				</logic:notEqual>
				</logic:notEqual>
				
				
				
				</table>
				</fieldset>
				</td>
				</tr>
				
				
				
				<tr height="60px">
				<td valign="middle" align="center" width="15%">
				<html:radio property="pointtype" value="1" name="khFieldForm"><bean:message key="kh.field.addscore"/></html:radio>
				</td>
				<td valign="middle" align="left" width="85%">
				<fieldset align="center" style="height:40px">
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
				<tr>
				<td>&nbsp;</td>
				</tr>
				<tr>
				<td valign="middle" style="height:30px">&nbsp;&nbsp;
				<input style="margin-bottom:5px;" type="button" class="mybutton" name="add" value="<bean:message key="kh.field.rule"/>"  onclick="addrul('${khFieldForm.rulePointid}','${khFieldForm.type}');"/>
				</td>
				</tr>
				</table>
				</fieldset>
				</td>
				</tr>
				<tr height="60px">
				<td valign="middle" align="center" width="15%">
				<html:radio property="pointtype" value="2" name="khFieldForm"><bean:message key="kh.field.minusscore"/></html:radio>
				</td>
				<td valign="middle" align="left" width="85%">
				<fieldset align="center" style="height:40px">
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
				<tr>
				<td>&nbsp;</td>
				</tr>
				<tr>
				<td valign="middle" style="height:30px">&nbsp;&nbsp;
				<input type="button" style="margin-bottom: 5px;" class="mybutton" name="minus" value="<bean:message key="kh.field.rule"/>" onclick="addrul('${khFieldForm.rulePointid}','${khFieldForm.type}');"/>
				</td>
				</tr>
				</table>
				</fieldset>
				</td>
				</tr>
	</table>
	</td>
	</tr>
	<tr><td align="center" style="height:35px"> 
	<input type="button" value="<bean:message key="button.ok"/>" class="mybutton" onclick="save();"/>
	<input type="button" value="<bean:message key="button.close"/>" class="mybutton" onclick="parent.window.close();"/>
	</td>
	</tr>
	</table>
	</html:form>