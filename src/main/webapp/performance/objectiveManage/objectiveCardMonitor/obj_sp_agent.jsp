<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.performance.objectiveManage.setUnderlingObjective.SetUnderlingObjectiveForm"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean,com.hrms.struts.taglib.CommonData"%>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hrms.hjsj.sys.ResourceFactory" %>
<%
   String tt4CssName="ttNomal4";
   String tt3CssName="ttNomal3";
   if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
   {
      tt4CssName="tt4";
      tt3CssName="tt3";
   }
 %>
<%  
SetUnderlingObjectiveForm setUnderlingObjectiveForm=(SetUnderlingObjectiveForm)session.getAttribute("setUnderlingObjectiveForm"); 
    ArrayList personList=setUnderlingObjectiveForm.getPersonListForm().getList();
    String plan_id=request.getParameter("plan_id");
    String object_id=request.getParameter("object_id");
    LazyDynaBean abean=null;
    for(int i=0;i<personList.size();i++)
    {
    	abean=(LazyDynaBean)personList.get(i);
    	String _object_id=(String)abean.get("object_id");
    	if(_object_id.equals(object_id))
    		break;
    }
    
    String title=(String)abean.get("_b0110")+"&nbsp;"+(String)abean.get("_e0122")+"&nbsp;"+(String)abean.get("a0101");
    LazyDynaBean currentOptObj=(LazyDynaBean)abean.get("currentOptObj");
    String current_a0100=(String)currentOptObj.get("a0100");
    String current_level=(String)currentOptObj.get("level");
    ArrayList appealList=(ArrayList)abean.get("appealList");
    ArrayList rejectlist=(ArrayList)abean.get("rejectlist");
    
 %>  
 <link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<script type="text/javascript">

function func_opt(flag)
{
	if(flag=='07')
	{
	   document.getElementById("rej").style.display='block';
	}
	if(flag=='02'||flag=='03')
	{
	   document.getElementById("rej").style.display='none';
	} 
}

function closeWindow()
{
	parent.window.close();
}


function isSendEmail()
{
 	var hashVo=new ParameterSet();
   hashVo.setValue("str","11");
   var request=new Request({method:'post',asynchronous:false,onSuccess:sub,functionId:'9028000308'},hashVo);			 
}

function sub(outparameters)
{
   var send=outparameters.getValue("send");
   if(!confirm("确定执行代批操作吗?"))
   {
         return;
   }
  
   var sp_flag="02";
   var obj="";
   var objs=document.getElementsByName("opt");
   for(var n=0;n<objs.length;n++)
   {
   		if(objs[n].checked)
   			sp_flag=objs[n].value;
   }
  
   var reject_cause="";
   if(sp_flag=='07')
   { 
   		if(trim(document.getElementsByName("reason")[0].value).length==0)
   		{ 
   				alert(GZ_ACCOUNTING_PLEASEFILL+KH_PLAN_BACK+"原因!");
  				return;
   		}
   		reject_cause=trim(document.getElementsByName("reason")[0].value);
   		obj=document.getElementsByName("to_obj_2")[0].value;
   }
   else if(sp_flag=='02')
  	 	obj=document.getElementsByName("to_obj")[0].value;
    var isSend="0";
   if(send=='send')
   {
         if(confirm("是否发送邮件?"))
         {
            isSend="1";
         }
   }
   
   var hashVo=new ParameterSet();
   hashVo.setValue("reject_cause",getEncodeStr(reject_cause));
   hashVo.setValue("obj",obj); 
   hashVo.setValue("sp_flag",sp_flag); 
   hashVo.setValue("isSend",isSend);
   hashVo.setValue("object_id",'<%=object_id%>');
   hashVo.setValue("plan_id",'<%=plan_id%>');
   hashVo.setValue("current_a0100",'<%=current_a0100%>');
   hashVo.setValue("current_level",'<%=current_level%>');
   var request=new Request({method:'post',asynchronous:false,onSuccess:save_ok,functionId:'9028000306'},hashVo);			
   
}

function save_ok(outparameters)
{
  //window.returnValue="1";
  parent.window.opener.openValue("1");//改用调用父页面 openValue 方法 
  parent.window.close();
}

</script>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<html:form action="/performance/objectiveManage/objectiveCardMonitor/objective_state_list">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
<td align="center">
<fieldset align="center">
<legend><font class='<%=tt4CssName%>'>代批</font></legend>
<div style="overflow:auto;margin-bottom:4px;height:275px;" >
<table width="99%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
<td align="center" colspan="2"><strong> <%=title%>  </strong><Br>&nbsp;</td>
</tr>
<tr>

<tr>
<td align="center"  class="TableRow"  >主体类别</td>
<td align="left" class="RecordRow" >
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<%=((String)currentOptObj.get("lay_desc"))%></td>
</tr>

<td align="center" width='30%'  class="TableRow"  >考核主体</td>
<td align="left" width='70%'  class="RecordRow"   >
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<%=((String)currentOptObj.get("a0101"))%></td>
</tr>
<tr>
<td align="center"  class="TableRow"  >审批方式</td>
<td align="left" class="RecordRow" > 
<table>
<tr><td height='30' nowrap >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<% if(appealList.size()==0){ %>
<input type="radio" name="opt" value="03"  onclick="func_opt('03')"  checked /><bean:message key="info.appleal.state8"/>
<% }else { %>
<input type="radio" name="opt" value="02"  onclick="func_opt('02')"    checked /><bean:message key="info.appleal.state7"/>&nbsp;&nbsp;
</td>
<td id='app' >
<%
if(appealList.size()>0){
		out.print("<select name='to_obj' >");
		for(int i=0;i<appealList.size();i++)
		{
			CommonData _data=(CommonData)appealList.get(i);
			out.print("<option value='"+_data.getDataValue()+"'>"+_data.getDataName()+"&nbsp;&nbsp;</option>");
		}
		out.print("</select>");
	}
	
 } %>
</td></tr>
<tr><td  height='30'  nowrap valign='top'  >

 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="radio" name="opt"   onclick="func_opt('07')"   value="07"  /><bean:message key="info.appleal.state10"/>
</td>
<td id='rej' style='display:none;margin-top:2px;'> 
<%
if(rejectlist.size()>0){
		out.print("<select name='to_obj_2' >");
		for(int i=0;i<rejectlist.size();i++)
		{
			LazyDynaBean _data=(LazyDynaBean)rejectlist.get(i);
			String level=(String)_data.get("level");
			String a0101=(String)_data.get("a0101");
			String level_name=(String)_data.get("level_name");
			a0101+="("+level_name+")";
		 
			String _str="";
			if(level.equals("5"))
				_str="checked";
			out.print("<option  "+_str+"  value='"+(String)_data.get("a0100")+"'>"+a0101+"&nbsp;&nbsp;</option>");
		}
		out.print("</select>");
	}
	 %>
	 
	 <textarea style='margin-top:2px;' name="reason" rows="8" cols="40"></textarea>
	 
	 
	 
</td></tr></table>



</td>
</tr>

 
 
 
</table>
</div>
</fieldset>
</td>
</tr>
<tr>
<td colspan="3"  style="padding-top:3px;padding-bottom:3px;" align="center" > 
<input type="button" name="ok" value="<bean:message key="button.ok"/>" onclick="isSendEmail();" class="mybutton"/> 
<input type="button" name="clo" value="<bean:message key="button.close"/>" onclick="closeWindow();" class="mybutton"/>
 
</td>
</tr>
</table>
</html:form>


</body>
</html>