<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript" src="/js/constant.js"></script>
<script type="text/javascript">
<!--
function savePara()
{
  var tfield="";
  var tf=document.getElementById("tf");
  var cfield="";
  var cf=document.getElementById("cf");
  var lfield="";
  var lf=document.getElementById("lf");
  var cdfield="";
  var cdf=document.getElementById("cdf");
  var cufield="";
  var cuf=document.getElementById("cuf");
  if(tf)
  {
     for(var i=0;i<tf.options.length;i++)
     {
        if(tf.options[i].selected)
        {
          tfield=tf.options[i].value;
          break;
        }
     }
  }
  if(cf)
  {
     for(var i=0;i<cf.options.length;i++)
     {
        if(cf.options[i].selected)
        {
          cfield=cf.options[i].value;
          break;
        }
     }
  }
  if(lf)
  {
     for(var i=0;i<lf.options.length;i++)
     {
        if(lf.options[i].selected)
        {
          lfield=lf.options[i].value;
          break;
        }
     }
  }
  if(cdf)
  {
     for(var i=0;i<cdf.options.length;i++)
     {
        if(cdf.options[i].selected)
        {
          cdfield=cdf.options[i].value;
          break;
        }
     }
  }
  if(cuf)
  {
     for(var i=0;i<cuf.options.length;i++)
     {
        if(cuf.options[i].selected)
        {
          cufield=cuf.options[i].value;
          break;
        }
     }
  }
  if(tfield=='')
  {
     alert("请选择标题指标！");
     return;
  }
   if(cfield=='')
  {
     alert("请选择内容指标！");
     return;
  }
   if(lfield=='')
  {
     alert("请选择等级指标！");
     return;
  }
   if(cdfield=='')
  {
     alert("请选择评审日期指标");
     return;
  }
   if(cufield=='')
  {
     alert("请选择评审人指标");
     return;
  }
  if(cfield==cufield)
  {
     alert("内容 和 评审人 不能选择同一个指标！");
     return;
  }
  if(tfield==cufield)
  {
     alert("标题 和 评审人 不能选择同一个指标！");
     return;
  }
  if(tfield==cfield)
  {
    alert("标题 和 内容 不能选择同一个指标！");
    return;
  }
  var obj = new Object();
  obj.tf=tfield;
  obj.cf=cfield;
  obj.lf=lfield;
  obj.cdf=cdfield;
  obj.cuf=cufield;
  returnValue=obj;
  window.close();
}
function closeWin()
{
  returnValue=null;
  window.close();
}
//-->
</script>
<html:form action="/hire/parameterSet/configureParameter/examine_info_config">
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
    
%>  
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<Br>
<%
}
%>
<table width='390px' border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="2">
		面试过程记录设置
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
   	   <td align="right" class="RecordRow" height="400">
   	   标&nbsp;&nbsp;&nbsp;&nbsp;题:
   	   </td>
   	   <td align="left" class="RecordRow">
   	   &nbsp;&nbsp;
   	   	<hrms:optioncollection name="parameterForm2" property="titleFieldList" collection="list" />
						 <html:select styleId="tf" name="parameterForm2" property="titleField" size="1" style="width:150px;">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
   	   </td>
   	   </tr>
   	   <tr>
   	   <td align="right" class="RecordRow" height="40">
   	   内&nbsp;&nbsp;&nbsp;&nbsp;容:
   	   </td>
   	   <td align="left" class="RecordRow">
   	   &nbsp;&nbsp;
   	   	<hrms:optioncollection name="parameterForm2" property="contentFieldList" collection="list" />
						 <html:select styleId="cf" name="parameterForm2" property="contentField" size="1" style="width:150px;">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
   	   </td>
   	   </tr>
   	   <tr>
   	   <td align="right" class="RecordRow">
   	   等&nbsp;&nbsp;&nbsp;&nbsp;级:
   	   </td>
   	   <td align="left" class="RecordRow">
   	   &nbsp;&nbsp;
   	   	<hrms:optioncollection name="parameterForm2" property="levelFieldList" collection="list" />
						 <html:select styleId="lf" name="parameterForm2" property="levelField" size="1" style="width:150px;">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>&nbsp;(关联代码类01)
   	   </td>
   	   </tr>
   	   <tr>
   	   <td align="right" class="RecordRow">
   	   评审日期:
   	   </td>
   	   <td align="left" class="RecordRow">
   	   &nbsp;&nbsp;
   	   	<hrms:optioncollection name="parameterForm2" property="commentDateFieldList" collection="list" />
						 <html:select styleId="cdf" name="parameterForm2" property="commentDateField" size="1" style="width:150px;">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
   	   </td>
   	   </tr>
   	   <tr>
   	   <td align="right" class="RecordRow">
   	   评&nbsp;审&nbsp;人:
   	   </td>
   	   <td align="left" class="RecordRow">
   	   &nbsp;&nbsp;
   	   	<hrms:optioncollection name="parameterForm2" property="commentUserFieldList" collection="list" />
						 <html:select styleId="cuf" name="parameterForm2" property="commentUserField" size="1" style="width:150px;">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
   	   </td>
   	   </tr>
   	   <tr>
   	   <td align="center" colspan="2" style="padding-top:5px;padding-bottom:3px;">
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<Br>
<%
}
%>
   	   <input type="button" class="mybutton" name="save" value="<bean:message key="button.ok"/>" onclick="savePara();"/>
   	    <input type="button" class="mybutton" name="clo" value="<bean:message key="button.close"/>" onclick="closeWin();"/>
   	   </td>
   	   </tr>
   	   </table>
</html:form>
