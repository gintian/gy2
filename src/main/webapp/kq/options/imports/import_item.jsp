<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript">
    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>

<script language="javascript">
function changeFieldSet(sub)
{
	var subset=sub.value;
	var temidtype="${kqItemForm.temidtype}";
    var hashvo=new ParameterSet();
    hashvo.setValue("subset",subset);
    hashvo.setValue("temidtype",temidtype);
	var request=new Request({method:'post',asynchronous:false,onSuccess:resultChangeFieldSet,functionId:'15204110025'},hashvo);
}
function resultChangeFieldSet(outparamters)
{
	var fielditemlist=outparamters.getValue("itemidlist");
	AjaxBind.bind(kqItemForm.field,fielditemlist);
	var listdate=outparamters.getValue("listdate");
	AjaxBind.bind(kqItemForm.begindate,listdate);
	AjaxBind.bind(kqItemForm.enddate,listdate);
}
function search()
{
	var subset=document.getElementById("subset1").value;
	if(subset=="#")
	{
		alert("请选择导入指标!");
		return;
	}
	var field=document.getElementById("field1").value;
	var begindate=document.getElementById("begindate1").value;
	var enddate=document.getElementById("enddate1").value;
	if(begindate==enddate)
	{
		alert("起始时间与结束时间不能一样！");
		return;
	}
	var akq_item1="${kqItemForm.akq_item}";
	kqItemForm.action="/kq/options/add_item.do?b_saveimport=link&subset="+subset+"&field="+field+"&begindate="+begindate+"&enddate="+enddate+"&akq_item1="+akq_item1;
    kqItemForm.submit();
}
function dele()
{
    if(confirm("是否清除导入指标?"))
    {
    var akq_item1="${kqItemForm.akq_item}";
	kqItemForm.action="/kq/options/add_item.do?b_deleimport=link&akq_item1="+akq_item1;
    kqItemForm.submit();
    }
}
</script>
<html:form action="/kq/options/add_item">
	<br>
	<br>
	<html:hidden name="kqItemForm" property="temidtype"/>
	<html:hidden name="kqItemForm" property="akq_item"/>
	<table border="0" cellspacing="0"  align="center" width="55%">
		<tr><td>
		<fieldset align="center" >
			<legend >导入指标</legend>
			<table border="0" cellspacing="0"  align="center" cellpadding="2">
				<tr>
					<td align="right" nowrap valign="left">        
            			来源子集       
           			</td>
           			<td align="left"  nowrap valign="center"> 
	          			<hrms:optioncollection name="kqItemForm" property="mainlist" collection="list" />
						<html:select name="kqItemForm" property="subset" size="1" styleId="subset1" onchange="changeFieldSet(this);">
						<html:options collection="list" property="dataValue" labelProperty="dataName" />
						</html:select>
           			</td>
				</tr>
				<tr>
					<td align="right" nowrap valign="left">        
                 	    指    标
          			</td>
          			<td align="left"  nowrap valign="center"> 
	          			<hrms:optioncollection name="kqItemForm" property="itemidlist" collection="list" />
						<html:select name="kqItemForm" property="field" size="1" styleId="field1">
						<html:options collection="list" property="dataValue" labelProperty="dataName" />
						</html:select>
           			</td>
				</tr>
				<tr>
					<td align="right" nowrap valign="left">        
                 	    起始日期
          			</td>
          			<td align="left"  nowrap valign="center"> 
	          			<hrms:optioncollection name="kqItemForm" property="listdate" collection="list" />
						<html:select name="kqItemForm" property="begindate" size="1" styleId="begindate1">
						<html:options collection="list" property="dataValue" labelProperty="dataName" />
						</html:select>
           			</td>
				</tr>
				<tr>
					<td align="right" nowrap valign="left">        
                 	    结束日期
          			</td>
          			<td align="left"  nowrap valign="center"> 
	          			<hrms:optioncollection name="kqItemForm" property="listdate" collection="list" />
						<html:select name="kqItemForm" property="enddate" size="1" styleId="enddate1">
						<html:options collection="list" property="dataValue" labelProperty="dataName" />
						</html:select>
           			</td>
				</tr>
			</table>
		</fieldset>
		</td></tr>
	</table>
	<table align="center">
		<tr>
        	<td align="center" colspan="2">
	       		<input type="button" name="Submit" value="<bean:message key="button.save"/>" class="mybutton" onClick="search();">
	       	  	<input type="button" name="Submit" value="<bean:message key="button.clearup"/>" class="mybutton" onClick="dele();">
	       	  	<input type="button" value="<bean:message key="button.return"/>" class="mybutton" onclick="history.back();"/>
          	</td>
        </tr>
    </table>
</html:form>

<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>