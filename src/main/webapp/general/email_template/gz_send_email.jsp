<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="Javascript" src="/gz/salary.js"></script>
<script type="text/javascript">
<!--
var prv_filterCondId="${gzEmailForm.filterId}";
var buttons=new Array();
buttons[0]="scyj";
buttons[1]="xzfsyj";
buttons[2]="qbfsyj";
buttons[3]="xzfsdx";
buttons[4]="qbfsdx";
buttons[5]="sclsjl";
function email_send_email(type,templateId,code,salaryid,e_m_type,id)
{
if(templateId == "")
{
       alert("请选择邮件模板！");
       return;
}
var ids="";
var num=0;
var queryvalue=gzEmailForm.queryvalue.value;
var queryYearValue=gzEmailForm.queryYearValue.value;
//type=1按选择的人员发送,type=2群发.
if(parseInt(type)==1)
{
  var obj=document.getElementsByName("selectpersonid");
  for(var i=0;i<obj.length;i++)
  {
    if(obj[i].checked)
    {
       ids+="`"+obj[i].value;
       if(obj[i].value!='')
       	num++;
    }
  }
  if(num==0)
  {
     alert("请选择人员");
     return;
  }
}
jinduo(id);
    var hashvo=new ParameterSet(); 
    hashvo.setValue("type",type);
    hashvo.setValue("ids",ids.substring(1));	
    hashvo.setValue("templateid",templateId);
    hashvo.setValue("queryvalue",queryvalue);
    hashvo.setValue("queryYearValue",queryYearValue);
    hashvo.setValue("salaryid",salaryid);
    hashvo.setValue("code",code);
    hashvo.setValue("e_m_type",e_m_type);
    hashvo.setValue("beforeSql","${gzEmailForm.beforeSql}");
   	var In_paramters="flag=1"; 	
    var request=new Request({method:'post',asynchronous:true,
		     parameters:In_paramters,onSuccess:send_email_ok,functionId:'0202030020'},hashvo);
}
function send_email_ok(outparameters)
{
   closeJindu();
   var n=outparameters.getValue("n");
   var templateid = outparameters.getValue("templateid");
   var code=outparameters.getValue("code");
   var outName=outparameters.getValue("filename");
   var e_m_type =outparameters.getValue("e_m_type");
    if(parseInt(n)==2&&parseInt(e_m_type)==0)
   {
       alert("邮件发送失败,请检查邮箱设置和网络连接");
       return;
   }else if(parseInt(n)==2&&parseInt(e_m_type)==1)
   {
      alert("短信发送失败,请检查电话指标设置和网络连接");
       return;
   }
   else
   {
       if(outName!=null&&outName!='Z~30DuTtqmt~33k~40~33HJD~40')
       {
    	 //20/3/17 xus vfs改造
         var win=open("/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true","txt");
       }
   }
   if(code==undefined||templateid==undefined){
		return;
   }
   gzEmailForm.action="/general/email_template/gz_send_email.do?b_init=init2&a_code="+code+"&id="+templateid;
   gzEmailForm.submit();
}
function gzemail_changtemplate(obj,salaryid,code)
{
    var id="";
    var num=0;
    for(var i=0;i<obj.options.length;i++)
    {
       if(obj.options[i].selected)
       {
          id=obj.options[i].value;
       }
    }
    if(trim(id).length==0)
    {
       return;
    }
    var obje = $("selectpersonid");
      var selectid="";
      for(var i=0;i<obje.length;i++)
      {
           if(obje[i].checked)
           {
              num++;
              selectid+="`"+obje[i].value;
           }
      }
      if(num==0)
      {
      }
      else
      {
           gzEmailForm.selectid.value=selectid.substring(1); 
      }
      gzEmailForm.action="/general/email_template/gz_send_email.do?b_init=init2&a_code="+code+"&salaryid="+salaryid+"&id="+id;
      gzEmailForm.submit();
}
function gzemail_exportcontent(salaryid,id,code)
{
var obj=document.getElementsByName("selectpersonid");
var nbase=document.getElementsByName("nbase");
var a0100=document.getElementsByName("usra0100");
if(id=="")
{
      alert("请选择邮件模板！");
      return;
}
var ids="";
var num=0;
var type="0";
jinduo('scyj');
if(obj)
{
   for(var i=0;i<obj.length;i++)
   {
      if(obj[i].checked)
      {
        num++;
        ids+=","+nbase[i].value+a0100[i].value+"~1";
      }
   }
   if(num>0)
   {
     type="1";
     document.gzEmailForm.selectedid.value=ids.substring(1);
   }
   gzEmailForm.action="/general/email_template/select_template.do?b_select=select&code="+code+"&salaryid="+salaryid+"&templateId="+id+"&type="+type;
   gzEmailForm.submit();
}
}
function gzemail_is_reexport(outparameters)
{
 var code=outparameters.getValue("code");
 var nflag=outparameters.getValue("nflag");
 var id=outparameters.getValue("id");
 var salaryid=outparameters.getValue("salaryid");
 var num=outparameters.getValue("num");
  // gzEmailForm.action="/general/email_template/select_template.do?b_select=select&code="
                      // +code+"&salaryid="+salaryid+"&templateId="+id+"&num="+num;
   gzEmailForm.action="/general/email_template/gz_send_email.do?b_init=init2$a_code="+code+"&salaryid="+salaryid+"&id="+id;
   gzEmailForm.submit();
}
function queryByTime(obj)
{
  var id="";
  for(var i=0;i<obj.options.length;i++)
  {
      if(obj.options[i].selected)
      {
        id=obj.options[i].value;
        break;
      }
  }
  gzEmailForm.action="/general/email_template/gz_send_email.do?b_query=query&timeid="+id;
  gzEmailForm.submit();
}
function queryByNama()
{
 var id="";
 var obj=document.getElementById("t");
  for(var i=0;i<obj.options.length;i++)
  {
      if(obj.options[i].selected)
      {
        id=obj.options[i].value;
        break;
      }
  }
  gzEmailForm.action="/general/email_template/gz_send_email.do?b_query=query&timeid="+id;
  gzEmailForm.submit();
}
function allselect()
{
  var obj=document.getElementsByName("selectpersonid");
  for(var i=0;i<obj.length;i++)
  {
     if(obj[i].checked)
     {
        obj[i].checked=false;
     }
     else
     {
        obj[i].checked=true;
     }
  }
}

function gzEmail_changeCondList(salaryid)
{
 var hashVo=new ParameterSet();
 hashVo.setValue("isclose","2");
 hashVo.setValue("salaryid",salaryid);
 var In_parameters="opt=1";
 var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:gzEmail_condlist_ok,functionId:'3020100017'},hashVo);			
    
}
function gzEmail_condlist_ok(outparameters)
{
  var filterList = outparameters.getValue("filterCondList");
  //var ob=document.getElementsByName("filterCondId");
  AjaxBind.bind(gzEmailForm.filterId,filterList); 
  var obj=$("filterId"); 
  var tabb=document.getElementById("tabb");
  if(obj.options.length==2)
  {
      var id="";
      var obj=document.getElementById("t");
      for(var i=0;i<obj.options.length;i++)
      {
         if(obj.options[i].selected)
         {
           id=obj.options[i].value;
           break;
         }
      }
     gzEmailForm.action="/general/email_template/gz_send_email.do?b_query=query&timeid="+id;
     gzEmailForm.submit();
  }
  else
  {
     for(var i=0;i<obj.options.length;i++)
     {
        if(obj.options[i].value==prv_filterCondId)
        {
            obj.options[i].selected=true;
            tabb.focus();
            return;
        }
     }
  }  
}
function jinduo(type){
	var x=document.body.clientWidth/2-300;
    var y=document.body.clientHeight/2-125+document.body.scrollTop;
    for(var i=0;i<buttons.length;i++)
    {
      document.getElementById(buttons[i]).disabled=true;
    }
    if(type=='scyj')
    {
       document.getElementById("hlw").innerHTML="正在生成邮件内容,请稍候...";
    }else if(type=='xzfsyj')
    {
       document.getElementById("hlw").innerHTML="正在发送邮件,请稍候...";
    }else if(type=='qbfsyj')
    {
       document.getElementById("hlw").innerHTML="正在发送邮件,请稍候...";
    }else if(type=='xzfsdx')
    {
       document.getElementById("hlw").innerHTML="正在发送短信,请稍候...";
    }else if(type=='qbfsdx')
    {
       document.getElementById("hlw").innerHTML="正在发送短信,请稍候...";
    }else if(type=='sclsjl')
    {
       document.getElementById("hlw").innerHTML="正在删除历史记录,请稍候...";
    }else if(type=='sclsjl')
    {
       document.getElementById("hlw").innerHTML="正在删除历史记录,请稍候...";
    }else if(type=='xzfswx')
    {
       document.getElementById("hlw").innerHTML="正在发送微信,请稍候...";
    }else if(type=='qbfswx')
    {
       document.getElementById("hlw").innerHTML="正在发送微信,请稍候...";
    }
	var waitInfo;
	waitInfo=eval("wait");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="block";
} 
function closeJindu()
{

     for(var i=0;i<buttons.length;i++)
    {
      document.getElementById(buttons[i]).disabled=false;
    }
    var waitInfo;
	waitInfo=eval("wait");
	waitInfo.style.display="none";
}
//-->
</script>
<html:form action="/general/email_template/gz_send_email">
<div   id="wait" style='position:absolute;top:285;left:120;display:none;width:500px;heigth:250px'>
 
		<table border="1" width="50%" cellspacing="0" cellpadding="4" class="table_style" height="100" align="center">
			<tr>
			
				<td class="td_style" height=24 id="hlw">
					<bean:message key="hmuster.label.wait"/>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="400" scrollamount="5" scrolldelay="10">
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
		    <iframe src="javascript:false" style="position:absolute; visibility:inherit; top:0px; left:0px;width:285px;height:120px;z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';">
		    </iframe>	
	</div>
<table width='90%' border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td colspan="2">
<table width='100%' border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td>
<table width='100%' border="0" id="tabb" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td>


按状态查询:
		       <html:radio name="gzEmailForm" property="sendok" value="3" onclick="email_search_cond('${gzEmailForm.templateId}');">全部</html:radio>
		       <html:radio name="gzEmailForm" property="sendok" value="1" onclick="email_search_cond('${gzEmailForm.templateId}');">成功 </html:radio>
		       <html:radio name="gzEmailForm" property="sendok" value="2" onclick="email_search_cond('${gzEmailForm.templateId}');">未成功</html:radio>
		       <html:radio name="gzEmailForm" property="sendok" value="0" onclick="email_search_cond('${gzEmailForm.templateId}');">未发</html:radio>
&nbsp;<bean:message key="gz.bankdisk.personfilter"/>&nbsp;
			<html:select name="gzEmailForm" property="filterId" size="1" onchange="gzEmail_filterPersonMethod('${gzEmailForm.salaryid}',this,'${gzEmailForm.tableName}');">
			<html:optionsCollection property="filterList" value="dataValue" label="dataName"/>
		    </html:select>

&nbsp;按时间查询&nbsp;
<html:select name="gzEmailForm" styleId="t" size="1" property="queryYearValue" onchange="queryByTime(this);" >
    <html:optionsCollection property="queryListYear" value="dataValue" label="dataName"/>
</html:select>&nbsp年
<html:select name="gzEmailForm" styleId="t" size="1" property="queryvalue" onchange="queryByTime(this);">
    <html:optionsCollection property="querylist" value="dataValue" label="dataName"/>
</html:select>&nbsp月
&nbsp;按姓名查询&nbsp;<html:text property="queryName" name="gzEmailForm" size="10" styleClass="inputtext"/>&nbsp;&nbsp;<input type="button" name="query" class="mybutton" value="<bean:message key="button.query"/>" onclick="queryByNama();"/>
</td>
</tr>
</table>
</td>
</tr>
</table>
</td>
</tr>
<tr>
<td>
<table width='100%' border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<THEAD>
<tr>
<td align="center" class="TableRow" nowrap><input type="checkbox" name="allsel" onclick="allselect();"/></td>
<td align="center" class="TableRow" nowrap>状态</td>
<td align="center" class="TableRow" nowrap>单位</td>
<td align="center" class="TableRow" nowrap>部门</td>
<td align="center" class="TableRow" nowrap>姓名</td>
<td align="center" class="TableRow" nowrap>发送到</td>
<td align="center" class="TableRow" nowrap>主题</td>
</tr>
</THEAD>
<% int i=0; String className="trShallow"; %>
  	<hrms:paginationdb id="element" name="gzEmailForm" sql_str="${gzEmailForm.select_sql}" fromdict="1" where_str="${gzEmailForm.where_sql}" columns="${gzEmailForm.columns}" order_by="${gzEmailForm.order_sql}" page_id="pagination" pagerows="${gzEmailForm.pagerows}" indexes="indexes">
      
       
         <%
			  if(i%2==0)
			  	className="trShallow";
			  else
			  	className="trDeep";
			  i++;
			  
			  %>
	   <tr class='<%=className%>' >
	   <td align="center" class="RecordRow" nowrap>
	   <input type="checkbox" name="selectpersonid" value="<bean:write name="element" property="personid"/>"/>
	  <input type="hidden" name="nbase" value="<bean:write name="element" property="nbase"/>"/>
	  <input type="hidden" name="usra0100" value="<bean:write name="element" property="a0100"/>"/>
	   </td>
	   <td align="center" class="RecordRow" nowrap>
	   <logic:equal name="element" property="send_ok" value="0">
	   <img src="/images/icon_fbyjs.gif" border="0"/>
	   </logic:equal>
	   <logic:equal name="element" property="send_ok" value="1">
	   <img src="/images/icon_tgsx.gif" border="0"/>
	   </logic:equal>
	   <logic:equal name="element" property="send_ok" value="2">
	   <img src="/images/icon_wtgsx.gif" border="0"/>
	   </logic:equal>
	   </td>
	   <td align="left" class="RecordRow" nowrap>
	    <hrms:codetoname codeid="UN" name="element" codeitem="codeitem" codevalue="b0110" scope="page"/>
         &nbsp;<bean:write name="codeitem" property="codename"/>&nbsp;
	   </td>
	   <td align="left" class="RecordRow" nowrap>
	    <hrms:codetoname codeid="UM" name="element" codeitem="codeitem" codevalue="e0122" scope="page"/>
         &nbsp;<bean:write name="codeitem" property="codename"/>&nbsp;
	   </td>
	   <td align="left" class="RecordRow" nowrap>
	   &nbsp;<bean:write name="element" property="a0101"/>&nbsp;
	   </td>
	   <td align="left" class="RecordRow" nowrap>
	   &nbsp;<bean:write name="element" property="address"/>&nbsp;
	   </td>
	   <td align="left" class="RecordRow" nowrap>
<a href="/general/email_template/gz_browse_email.do?b_query=link&a0100=<bean:write name="element" property="personid"/>&id=<bean:write name="element" property="id"/>"><bean:write name="element" property="subject" filter="false"/></a>
	   </td>
	   </tr>
	   </hrms:paginationdb>
	   <tr>
<td class="RecordRow" colspan="7">
 <table  width="100%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="hmuster.label.d"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="hmuster.label.paper"/>
					<bean:message key="hmuster.label.total"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.every.row"/>
					<bean:message key="hmuster.label.total"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="hmuster.label.paper"/>
			&nbsp;&nbsp;<bean:message key="label.every.page"/><html:text property="pagerows" name="gzEmailForm" size="5"></html:text><bean:message key="label.every.row"/><a href="javascript:queryByNama();"><bean:message key="label.page.refresh"/></a>
			</td>
			
			
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="gzEmailForm" property="pagination" nameId="gzEmailForm" scope="page">
				</hrms:paginationdblink></p>
			</td>
		</tr>  	  
</table> 
</td>
</tr>
</table>
</td>
</tr>
<tr>
<td>
<table width="80%" border="0">
<tr>
<td colspan="2" nowrap>
选择模板
<html:select name="gzEmailForm" size="1" property="templateId" onchange="gzemail_changtemplate(this,'${gzEmailForm.salaryid}','${gzEmailForm.code}');">
    <html:optionsCollection property="templateList" value="dataValue" label="dataName"/>
</html:select>
<html:hidden name="gzEmailForm" property="code"/>
<html:hidden name="gzEmailForm" property="id"/>
<html:hidden name="gzEmailForm" property="salaryid"/>
<html:hidden name="gzEmailForm" property="beforeSql"/>
<input type="hidden" name="selectedid" value="-1"/>
<input type="hidden" name="selectid" value=""/>
<input type="hidden" name="model" id="gm" value="0"/>
<input type="button" name="send" id="scyj" value="生成邮件" class="mybutton" onclick="gzemail_exportcontent('${gzEmailForm.salaryid}','${gzEmailForm.id}','${gzEmailForm.code}');"/>
<input type="button" name="sel_send"  id="xzfsyj" value="选中发送邮件" class="mybutton" onclick="email_send_email('1','${gzEmailForm.id}','${gzEmailForm.code}','${gzEmailForm.salaryid}','0','xzfsyj');"/>
<input type="button" name="all_send" id="qbfsyj" value="全部发送邮件" class="mybutton" onclick="email_send_email('2','${gzEmailForm.id}','${gzEmailForm.code}','${gzEmailForm.salaryid}','0','qbfsyj');"/>
<logic:equal name="gzEmailForm" property="dxFlag" value="1">
	<input type="button" name="sel_send" id="xzfsdx" value="选中发送短信" class="mybutton" onclick="email_send_email('1','${gzEmailForm.id}','${gzEmailForm.code}','${gzEmailForm.salaryid}','1','xzfsdx');"/>
	<input type="button" name="all_send" id="qbfsdx" value="全部发送短信" class="mybutton" onclick="email_send_email('2','${gzEmailForm.id}','${gzEmailForm.code}','${gzEmailForm.salaryid}','1','qbfsdx');"/>
</logic:equal>
<logic:equal name="gzEmailForm" property="wxFlag" value="1">
	<input type="button" name="sel_send" id="xzfswx" value="选中发送微信" class="mybutton" onclick="email_send_email('1','${gzEmailForm.id}','${gzEmailForm.code}','${gzEmailForm.salaryid}','2','xzfswx');"/>
	<input type="button" name="all_send" id="qbfswx" value="全部发送微信" class="mybutton" onclick="email_send_email('2','${gzEmailForm.id}','${gzEmailForm.code}','${gzEmailForm.salaryid}','2','qbfswx');"/>
</logic:equal>

<input type="button" name="del" id="sclsjl" value="删除历史记录" class="mybutton" onclick="email_delete_person('${gzEmailForm.id}');"/>
<input type="button" name="clo" value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close();"/>
</td>
</tr>
</table>
</td>
</tr>
<tr>
<td>
<table width="80%" border="0">
<tr>
<td colspan="2">

状态说明:<img src="/images/icon_tgsx.gif" border="0"/>成功&nbsp;&nbsp;<img src="/images/icon_fbyjs.gif" border="0"/>未发&nbsp;&nbsp;<img src="/images/icon_wtgsx.gif" border="0"/>未成功
</td>
</tr>
</table>
</td>
</tr>
</table>
</html:form>
<script type="text/javascript">
<!--
window.focus();
//-->
</script>