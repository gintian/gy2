<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript">
var desc="薪资类别";
var gz_module=${historyDataForm.gz_module};
if(gz_module=='1')
	desc="保险类别";

<!--
function allSelectSalary(obj)
{
  var selectObj=document.getElementsByName("salaryid");
  if(selectObj)
  {
      for(var i=0;i<selectObj.length;i++)
      {
         if(obj.checked)
            selectObj[i].checked=true;
         else
            selectObj[i].checked=false;
      }
  }
}
function config_time(opt)
{
    var id="";
    var num=0;
    var sc=document.getElementsByName("salaryid");
    if(sc)
    {
       for(var i=0;i<sc.length;i++)
       {
         if(sc[i].checked)
         {
           id+="`"+sc[i].value;
           num++;
         }
       }
    }
    if(num==0)
    {
       if(opt=='0')
       {
          alert("请选择要进行归档的"+desc+"!");
          return;
       }
       if(opt=='1')
       {
          alert("请选择要还原的"+desc+"!");
          return;
       }
       if(opt=='2')
       {
          alert("请选择要删除归档数据的"+desc+"!");
          return;
       }
    }
    if(trim(id).length>0)
       id=id.substring(1);

     var strurl="/gz/gz_analyse/historydata/salary_set_list.do?br_config=link&gz_module=${historyDataForm.gz_module}&id="+$URL.encode(id)+"&opttype="+opt;
     historyDataForm.action=strurl;
     historyDataForm.submit();
}
function browseHisData()
{
	var salaryids = '';
	var selectObj=document.getElementsByName("salaryid");
  	if(selectObj)
  	{
      for(var i=0;i<selectObj.length;i++)
      {
         if(selectObj[i].checked)
            salaryids+='@'+selectObj[i].value; 
      }
 	}
  if(salaryids=='')
 	alert('请先选择'+desc+'！');
  else
  {
  	historyDataForm.action='/gz/gz_analyse/historydata/browse.do?b_orgtree=link&salaryids='+salaryids.substring(1);
  	historyDataForm.submit();
  }  
}
function browseHisData2(salaryids)
{
  	historyDataForm.action='/gz/gz_analyse/historydata/browse.do?b_orgtree=link&salaryids='+salaryids;
  	historyDataForm.submit(); 
}

//-->
</script>
<html:form action="/gz/gz_analyse/historydata/salary_set_list">

<table width="100%" align="center" style="margin-left:-3px;">
<tr>
<td align="center">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<thead>
<tr>
<td align="center" class="TableRow">
 <input type="checkbox" name="allSelect" value="0" onclick="allSelectSalary(this);"/>
</td>
<td align="center" class="TableRow">
 编号
</td>
<td align="center" class="TableRow">
 <logic:equal value="0" name="historyDataForm" property="gz_module">
 薪资类别
 </logic:equal>
 <logic:equal value="1" name="historyDataForm" property="gz_module">
 保险类别
 </logic:equal>
</td>
<hrms:priv func_id="325040202,32407402">
<td align="center" class="TableRow">
浏览
</td>
</hrms:priv>
</tr>
</thead>
<% int i=0; %>
  <hrms:extenditerate id="element" name="historyDataForm" property="salarySetListform.list" indexes="indexes"  pagination="salarySetListform.pagination" pageCount="30" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>  
          <td class="RecordRow" align="center">
          <input type="checkbox" name="salaryid" value="<bean:write name="element" property="salaryid"/>"/>
          </td>
          <td class="RecordRow" align="right">
          <bean:write name="element" property="salaryid"/>
          </td>
           <td class="RecordRow" align="left">
          &nbsp;<bean:write name="element" property="cname"/>
          </td>
          <hrms:priv func_id="325040202,32407402">
            <td class="RecordRow" align="center">
          <img src="/images/view.gif" border="0" style="cursor:hand;" onclick="browseHisData2('<bean:write name="element" property="salaryid"/>')">
          </td>
          </hrms:priv>
          </tr>
          </hrms:extenditerate>
</table>
<table  width="100%"  class='RecordRowP' align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="historyDataForm" property="salarySetListform.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="historyDataForm" property="salarySetListform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="historyDataForm" property="salarySetListform.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="historyDataForm" property="salarySetListform.pagination"
				nameId="salarySetListform" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
</td>
</tr>
<tr>
<td align="center">
<hrms:priv func_id="325040201,32407401">
<input type="button" class="mybutton" name="his" value="归档" onclick="config_time('0');"/>
</hrms:priv>
<hrms:priv func_id="325040203,32407403">
   <input type="button" class="mybutton" name="his" value="还原" onclick="config_time('1');"/>
</hrms:priv>

<hrms:priv func_id="32407404,325040204">
   <input type="button" class="mybutton" name="del" value="删除" onclick="config_time('2');"/>
</hrms:priv>

<hrms:priv func_id="325040202,32407402">
<input type="button" class="mybutton" name="browse" value="浏览" onclick="browseHisData()"/>
</hrms:priv>
<hrms:tipwizardbutton flag="compensation" target="il_body" formname="historyDataForm"/>
</td>
</tr>
</table>
</html:form>