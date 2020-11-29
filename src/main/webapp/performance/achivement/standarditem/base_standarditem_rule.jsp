<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<script type="text/javascript">
<!--
var beforeitemid="-1";
//opt=0增加同级=1增加下级=2插入=3编辑
var opt_ = '';
function addStandardItem(opt)
{
    opt_ = opt;
	if(opt=='3')
	{
    	var num=0;
    	var obj=document.getElementsByName("itemid");
    	for(var i=0;i<obj.length;i++)
    	{
       		if(obj[i].checked)
       		{
          		num++;
          		beforeitemid=obj[i].value;
       		}
    	}
      	if(num>1)
      	{
          	alert("一次只能编辑一个项目！");
          	return;
      	}
      	if(num==0)
      	{
         	alert("请选择项目！");
         	return;
      	}      
    }
    var theurl="/performance/achivement/standarditem/search_standarditem_list.do?b_add=addbase`ruletype=0`opt="+opt+"`itemid="+beforeitemid;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    var config = {
        width:480,
        height:220,
        type:'2'
    }
    modalDialog.showModalDialogs(iframe_url,'addStandardItemWin',config,addStandardItem_ok)

     
}
function addStandardItem_ok(return_vo){
    if(return_vo)
    {
        var obj=new Object();
        obj.desc=return_vo.desc;
        obj.score=return_vo.score;
        obj.topv=return_vo.topv;
        obj.bottomv=return_vo.bottomv;
        obj.ruletype=return_vo.ruletype;
        var point_id=standardItemForm.point_id.value;
        var hashvo=new ParameterSet();
        hashvo.setValue("opt",opt_);
        hashvo.setValue("desc",getEncodeStr(obj.desc));
        hashvo.setValue("score",obj.score);
        hashvo.setValue("beforeitemid",beforeitemid);
        hashvo.setValue("model","base");
        hashvo.setValue("top",obj.topv);
        hashvo.setValue("bottom",obj.bottomv);
        hashvo.setValue("point_id",point_id);
        hashvo.setValue("ruletype",obj.ruletype);
        var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'9020020203'},hashvo);
    }
}
function deleteItem()
{
    var num=0;
    var vals="";
    var obj=document.getElementsByName("itemid");
    for(var i=0;i<obj.length;i++)
    {
       	if(obj[i].checked)
       	{
          	num++;
          	vals+="`"+obj[i].value;
       	}
    }
   
    if(num==0)
    {
    	alert("请选择项目！");
        return;
    }
    if(num>=1)
    {
        if(confirm("确认删除？"))
      	{
        	var hashvo=new ParameterSet();
            hashvo.setValue("itemid",vals.substring(1));
            var request=new Request({asynchronous:false,onSuccess:delete_ok,functionId:'9020020208'},hashvo);
        }
    }    
}
function delete_ok(outparameters)
{
   	standardItemForm.action="/performance/achivement/standarditem/search_standarditem_list.do?b_baserule=init&type=2";
   	standardItemForm.submit();
}
function save_ok(outparameters)
{
  	standardItemForm.action="/performance/achivement/standarditem/search_standarditem_list.do?b_baserule=init&type=2";
  	standardItemForm.submit();
}
//-->
</script>
<html:form action="/performance/achivement/standarditem/search_standarditem_list">

<table width="90%" border="0" cellspacing="1" align="center" cellpadding="1">
<tr>
<td align="left" style="height:35px"> 
<html:hidden name="standardItemForm" property="point_id"/>
<input type="button" name="a" value="<bean:message key="lable.tz_template.new"/>" class="mybutton" onclick="addStandardItem('0');"/>
<input type="button" name="e" value="<bean:message key="button.edit"/>" class="mybutton" onclick="addStandardItem('3');"/>
<input type="button" name="d" value="<bean:message key="button.delete"/>" class="mybutton" onclick="deleteItem();"/>
<input type="button" name="c" value="<bean:message key="button.close"/>" class="mybutton" onclick="parent.window.close();"/>
</td>
</tr>
</table>
<div style="width:540px;height:350px;overflow:auto;" >
<table width="90%" border="0" cellspacing="0" id="tb" align="center" cellpadding="0" class="ListTable">
<thead>
<tr>
<td align="center" class="TableRow">
<bean:message key="column.select"/>
</td>
<td align="center" class="TableRow">
项目名称
</td>
<td align="center" class="TableRow">
上限值
</td>
<td align="center" class="TableRow">
下限值
</td>
<td align="center" class="TableRow">
标准分值
</td>
</tr>
</thead>
   <logic:iterate id="element" name="standardItemForm" property="baseRuleList">
<tr>
<td align="center" class="RecordRow">
<input type="checkbox" name="itemid" value="<bean:write name="element" property="item_id"/>"/>
</td>
<td align="left" class="RecordRow">&nbsp;
<bean:write name="element" property="itemdesc"/>
</td>
<td align="right" class="RecordRow">
<bean:write name="element" property="top_value"/>&nbsp;
</td>
<td align="right" class="RecordRow">
<bean:write name="element" property="bottom_value"/>&nbsp;
</td>
<td align="right" class="RecordRow">
<bean:write name="element" property="score"/>&nbsp;
</td>
</tr>
   </logic:iterate>
</table>

</div>
</html:form>