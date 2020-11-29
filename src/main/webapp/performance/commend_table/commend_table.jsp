<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%> 
<%@taglib uri="/tags/struts-html" prefix="html"%> 
<%@taglib uri="/tags/struts-logic" prefix="logic"%> 
<%@taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%> 
<script type="text/javascript">
<!--
var checkedValue=false;
function sub(opttype)
{
   <% int n=0;%>
   var num=0;
   <logic:iterate id="element" name="commendTableForm" property="commendList" indexId="index"> 
      var obj = document.getElementsByName("<%=n%>a");
      if(obj)
      {
         var h=0;
         for(var j=0;j<obj.length;j++)
         {
              if(!obj[j].checked)
              {
                h++;
              }
         }
         if(h==obj.length)
         {
           num++;
         }
      }
   <%n++;%>
   </logic:iterate>
   if(num>0)
   {
       var msg="保存";
       if(opttype=='1')
       {
          msg="完成";
       }
       if(confirm("尚有空项，是否确认"+msg+"。"))
       {
            for(var i=0;i<document.forms[0].elements.length;i++)
            {
           if(document.forms[0].elements[i].type=='checkbox')
           {
            if(document.forms[0].elements[i].checked)
            {
              document.forms[0].elements[i].value='1';
            }
           else
          {
           document.forms[0].elements[i].value='0';
           document.forms[0].elements[i].checked=true;
        }
     }
   }
          if(opttype=='1')
            commendTableForm.action="/performance/commend_table/commend_table.do?b_save=save&isClose=0&opttype="+opttype;
         else
             commendTableForm.action="/performance/commend_table/commend_table.do?b_save1=save&isClose=1&opttype="+opttype;
         commendTableForm.submit();

       }
   }
   else
   {
        for(var i=0;i<document.forms[0].elements.length;i++)
        {
       if(document.forms[0].elements[i].type=='checkbox')
        {
           if(document.forms[0].elements[i].checked)
           {
              document.forms[0].elements[i].value='1';
           }
           else
           {
             document.forms[0].elements[i].value='0';
             document.forms[0].elements[i].checked=true;
            }
       }
     }
      if(opttype=='1')
           commendTableForm.action="/performance/commend_table/commend_table.do?b_save=save&isClose=0&opttype="+opttype;
      else
           commendTableForm.action="/performance/commend_table/commend_table.do?b_save1=save&isClose=1&opttype="+opttype;
       commendTableForm.submit();
    }
}
function returnBack()
{
    window.opener.location="/templates/attestation/unicom/performance.do?b_query=link";
    window.close();
}
function setValueO(obj,name,index)
{
    if(checkedValue)
    {
       obj.checked=false;
       var nn=document.getElementsByName(name);
       nn[0].value="0";
       nn[0].checked=false;
       checkedValue=obj.checked
       return;
    }
    var arr=document.getElementsByName(name);
    for(var i=0;i<6;i++)
    {
      var objname="commendList["+index+"].C"+(i+1);
      if(name == objname)
      {
         if(obj.checked)
       {
          arr[0].value='1';
          arr[0].checked=true;
       }
       else
       {
          arr[0].value='0';
          arr[0].checked=false;
       }
       }
       else
       {
           var anotherObj=document.getElementsByName(objname);
           anotherObj[0].value='0';
           anotherObj[0].checked=false;
       }
     }
}
function clearValueE(name,index)
{
   var radioObj=document.getElementsByName(name);
   for(var i=0;i<radioObj.length;i++)
   {
      radioObj[i].checked=false;
   }
   for(var i=0;i<8;i++)
    {
      var objname="commendList["+index+"].C"+(i+3);
       var anotherObj=document.getElementsByName(objname);
       anotherObj[0].value='0';
       anotherObj[0].checked=false;
    }
   
}
function clearValueO(name,index)
{
   var radioObj=document.getElementsByName(name);
   for(var i=0;i<radioObj.length;i++)
   {
      radioObj[i].checked=false;
   }
   for(var i=0;i<6;i++)
    {
      var objname="commendList["+index+"].C"+(i+1);
       var anotherObj=document.getElementsByName(objname);
       anotherObj[0].value='0';
       anotherObj[0].checked=false;
    }
   
}
function setValueE(obj,name,index)
{
    if(checkedValue)
    {
       obj.checked=false;
       var nn=document.getElementsByName(name);
       nn[0].value="0";
       nn[0].checked=false;
       checkedValue=obj.checked
       return;
    }
    var arr=document.getElementsByName(name);
    for(var i=0;i<8;i++)
    {
      var objname="commendList["+index+"].C"+(i+3);
      if(name == objname)
      {
         if(obj.checked)
       {
          arr[0].value='1';
          arr[0].checked=true;
       }
       else
       {
          arr[0].value='0';
          arr[0].checked=false;
       }
       }
       else
       {
           var anotherObj=document.getElementsByName(objname);
           anotherObj[0].value='0';
           anotherObj[0].checked=false;
       }
     }
}
function setValue(obj)
{
   checkedValue=obj.checked;
}
<%
if(request.getParameter("isClose")!=null&&request.getParameter("isClose").equals("0"))
{
%>
   window.opener.location="/templates/attestation/unicom/performance.do?b_query=link";
   window.close();
<%
 }
%>
//-->
</script>
<style>
<!--
.TableRowCommend {
	BACKGROUND-COLOR: #D7E9FF; 
	font-family:楷体_GB2312;
	font-size: 21px;  
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	height:50;
	font-weight: bold;	
	valign:middle;
}
.TableRowBLOD {
	BACKGROUND-COLOR: #D7E9FF; 
	font-family:楷体_GB2312;
	font-size: 21px; 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 2pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	height:50;
	font-weight: bold;	
	valign:middle;
}
.RecordRowBLOD {
	border: inset 1px #94B6E6;
	font-family:楷体_GB2312;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 2pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 16px;
	border-collapse:collapse; 
	height:30;
}
.RecordRowFONT {
	border: inset 1px #94B6E6;
	font-family:楷体_GB2312;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 16px;
	border-collapse:collapse; 
	height:30;
    width:100;
}
.tt5{
  font-family:楷体_GB2312;
  font-size: 23px;
}
.mybuttonBig{
	border:1px solid #84ADC9;
	background-image:url(/images/button.jpg);
	background-repeat:repeat-x;
	background-position:right;
	font-size:16px;
	line-height:18px;
	padding-left:1px;
	padding-right:1px;
	/*margin-left:1px;*/
	color:#36507E;
	background-color: transparent;	
	cursor: hand ; 	
 }
-->
</style>
<% int i=0; %>
<html:form action="/performance/commend_table/commend_table">
<br>
 <table width="95%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
 <tr>
 <td>
 <html:hidden name="commendTableForm" property="tableType" styleId="tlt"/>
 <table  width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">

 <logic:equal name="commendTableForm" property="tableType" value="0">
  <thead>
   <tr>
  <td  class="TableRowCommend"  valign="middle" align="center" colspan="10">
 <font class="tt5">  民&nbsp;&nbsp;主&nbsp;&nbsp;推&nbsp;&nbsp;荐&nbsp;&nbsp;表（一）</font>
  
  </td>
  </tr> 
 <tr>
  <td class="TableRowCommend" rowspan="2" height="40"  valign="middle" align="center">
                姓名
          </td>
          <td class="TableRowBLOD" colspan="5" height="20" valign="middle" align="center">
          建议本单位任职（可推荐提拔任用）
          </td>
           <td class="TableRowBLOD" colspan="3" height="20" valign="middle" align="center">
          建议异地交流任职
          </td>  
          <!--  
           <logic:notEqual value="2" name="commendTableForm" property="flag">
          <td class="TableRowCommend" rowspan="2" height="40"  valign="middle" align="center">
                清空
          </td>
          </logic:notEqual>
          -->
 </tr>
 <tr>
 <td  class="TableRowBLOD" height="20" valign="middle" align="center">
 总经理
 </td>
 <td  class="TableRowCommend"height="20" valign="middle" align="center" >
 书记
 </td>
 <td class="TableRowCommend" height="20" valign="middle" align="center">
 副总经理副书记<br>
 (正职待遇)
 </td>
 <td class="TableRowCommend" height="20" valign="middle" align="center">
 副总经理
 </td>
 <td class="TableRowCommend" height="20" valign="middle" align="center">
 降职使用
 </td>
 <td class="TableRowBLOD" height="20" valign="middle" align="center">
 提拔任用
 </td>
 <td class="TableRowCommend" height="20" valign="middle" align="center" >
 平级交流
 </td>
 <td class="TableRowCommend" height="20" valign="middle" align="center" >
 降职使用
 </td>
 
 </tr>
 </thead>
  <logic:iterate id="element" name="commendTableForm" property="commendList" indexId="index"> 
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
   <td align="center" valign="middle" class="RecordRowFONT">
      <font size="5"><bean:write name="element" property="a0101_1"/></font>
   </td>
    <td align="center" valign="middle" class="RecordRowBLOD">
    <logic:equal name="element" property="self" value="1">
      <input type="radio" name="<%=index+"a"%>" value="0" disabled/>
     <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C7"%>" value="1" disabled/>	
    </logic:equal>
     <logic:equal name="element" property="self" value="0">
         <logic:equal name="element" property="C7" value="1">
            <input type="radio" onmouseover="setValue(this);" onclick='setValueE(this,"<%="commendList["+index+"].C7"%>","<%=index%>")' name="<%=index+"a"%>" value="0" checked="checked" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
             <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C7"%>" checked="checked" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
        </logic:equal>
        <logic:equal name="element" property="C7" value="0">
         <input type="radio"  onmouseover="setValue(this);" onclick='setValueE(this,"<%="commendList["+index+"].C7"%>","<%=index%>")' name="<%=index+"a"%>" value="0" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
           <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C7"%>" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
         </logic:equal>
     </logic:equal>
   </td>
    <td align="center" valign="middle" class="RecordRow">
    <logic:equal name="element" property="self" value="1">
    <input type="radio" name="<%=index+"a"%>" value="0" disabled/>
     <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C8"%>" value="1" disabled/>	
    </logic:equal>
     <logic:equal name="element" property="self" value="0">
     <logic:equal name="element" property="C8" value="1">
               <input type="radio" onmouseover="setValue(this);" onclick='setValueE(this,"<%="commendList["+index+"].C8"%>","<%=index%>")' name="<%=index+"a"%>" value="0" checked="checked" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
         <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C8"%>" checked="checked" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     <logic:equal name="element" property="C8" value="0">
       <input type="radio" onmouseover="setValue(this);" onclick='setValueE(this,"<%="commendList["+index+"].C8"%>","<%=index%>")' name="<%=index+"a"%>" value="0" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
        <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C8"%>" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     </logic:equal>
   </td>
    <td align="center" valign="middle" class="RecordRow">
     <logic:equal name="element" property="self" value="1">
     <input type="radio" name="<%=index+"a"%>" value="0" disabled/>
     <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C9"%>" value="1" disabled/>	
    </logic:equal>
     <logic:equal name="element" property="self" value="0">
     <logic:equal name="element" property="C9" value="1">
        <input type="radio" onmouseover="setValue(this);" onclick='setValueE(this,"<%="commendList["+index+"].C9"%>","<%=index%>")' name="<%=index+"a"%>" value="0" checked="checked" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
         <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C9"%>" checked="checked" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     <logic:equal name="element" property="C9" value="0">
           <input type="radio" onmouseover="setValue(this);" onclick='setValueE(this,"<%="commendList["+index+"].C9"%>","<%=index%>")' name="<%=index+"a"%>" value="0" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
        <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C9"%>" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     </logic:equal>
   </td>
    <td align="center" valign="middle" class="RecordRow">
     <logic:equal name="element" property="self" value="1">
     <input type="radio" name="<%=index+"a"%>" value="0" disabled/>
     <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C10"%>" value="1" disabled/>	
    </logic:equal>
     <logic:equal name="element" property="self" value="0">
     <logic:equal name="element" property="C10" value="1">
      <input type="radio" onmouseover="setValue(this);" onclick='setValueE(this,"<%="commendList["+index+"].C10"%>","<%=index%>")' name="<%=index+"a"%>" value="0" checked="checked" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
         <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C10"%>" checked="checked" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     <logic:equal name="element" property="C10" value="0">
         <input type="radio" onmouseover="setValue(this);" onclick='setValueE(this,"<%="commendList["+index+"].C10"%>","<%=index%>")' name="<%=index+"a"%>" value="0" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
        <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C10"%>" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     </logic:equal>
   </td>
    <td align="center" valign="middle" class="RecordRow">
     <logic:equal name="element" property="self" value="1">
     <input type="radio" name="<%=index+"a"%>" value="0"  disabled/>
     <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C3"%>" value="1" disabled/>	
    </logic:equal>
     <logic:equal name="element" property="self" value="0">
       <logic:equal name="element" property="C3" value="1">
           <input type="radio" onmouseover="setValue(this);" onclick='setValueE(this,"<%="commendList["+index+"].C3"%>","<%=index%>")' name="<%=index+"a"%>" value="0" checked="checked" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
       
         <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C3"%>" checked="checked" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     <logic:equal name="element" property="C3" value="0">
           <input type="radio" onmouseover="setValue(this);" onclick='setValueE(this,"<%="commendList["+index+"].C3"%>","<%=index%>")' name="<%=index+"a"%>" value="0" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
 
        <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C3"%>" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     </logic:equal>
   </td>
    <td align="center" valign="middle" class="RecordRowBLOD">
      <logic:equal name="element" property="self" value="1">
      <input type="radio" name="<%=index+"a"%>" value="0" disabled/>
     <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C4"%>" value="1" disabled/>	
    </logic:equal>
     <logic:equal name="element" property="self" value="0">
       <logic:equal name="element" property="C4" value="1">
                <input type="radio" onmouseover="setValue(this);" onclick='setValueE(this,"<%="commendList["+index+"].C4"%>","<%=index%>")' name="<%=index+"a"%>" value="0" checked="checked" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
 
         <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C4"%>" checked="checked" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     <logic:equal name="element" property="C4" value="0">
              <input type="radio" onmouseover="setValue(this);" onclick='setValueE(this,"<%="commendList["+index+"].C4"%>","<%=index%>")' name="<%=index+"a"%>" value="0" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
 
        <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C4"%>" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     </logic:equal>
   </td>
     <td align="center" valign="middle" class="RecordRow">
      <logic:equal name="element" property="self" value="1">
          <input type="radio" name="<%=index+"a"%>" value="0" disabled/>
     <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C5"%>" value="1" disabled/>	
    </logic:equal>
     <logic:equal name="element" property="self" value="0">
       <logic:equal name="element" property="C5" value="1">
       <input type="radio" onmouseover="setValue(this);" onclick='setValueE(this,"<%="commendList["+index+"].C5"%>","<%=index%>")' name="<%=index+"a"%>" value="0" checked="checked" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
  
         <input style="display:none"type="checkbox" name="<%="commendList["+index+"].C5"%>" checked="checked" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     <logic:equal name="element" property="C5" value="0">
       <input type="radio" onmouseover="setValue(this);" onclick='setValueE(this,"<%="commendList["+index+"].C5"%>","<%=index%>")' name="<%=index+"a"%>" value="0" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
        <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C5"%>" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     </logic:equal>
   </td>
    <td align="center" valign="middle" class="RecordRow">
     <logic:equal name="element" property="self" value="1">
     <input type="radio" name="<%=index+"a"%>" value="0"  disabled/>
     <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C6"%>" value="1" disabled/>	
    </logic:equal>
     <logic:equal name="element" property="self" value="0">
       <logic:equal name="element" property="C6" value="1">
           <input type="radio" onmouseover="setValue(this);" onclick='setValueE(this,"<%="commendList["+index+"].C6"%>","<%=index%>")' name="<%=index+"a"%>" value="0" checked="checked" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
         <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C6"%>" checked="checked" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     <logic:equal name="element" property="C6" value="0">
           <input type="radio" onmouseover="setValue(this);" onclick='setValueE(this,"<%="commendList["+index+"].C6"%>","<%=index%>")' name="<%=index+"a"%>" value="0" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
     
        <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C6"%>" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     </logic:equal>
   </td>
   <!--  
    <logic:notEqual value="2" name="commendTableForm" property="flag">
   <td align="center" valign="middle" class="RecordRow">
   <input type="button" value="清空" name="<%=index+"button"%>" onclick='clearValueE("<%=index+"a"%>","<%=index%>")' class="mybuttonBig"/>
   </td>
   </logic:notEqual>
   -->
 </tr>
 </logic:iterate>

 </logic:equal>
  <logic:equal name="commendTableForm" property="tableType" value="1">
  <thead>
 <tr>
  <td class="TableRowCommend"  valign="middle" align="center" colspan="10">
 <font class="tt5">  民&nbsp;&nbsp;主&nbsp;&nbsp;推&nbsp;&nbsp;荐&nbsp;&nbsp;表（一）</font>
 
  </td>
  </tr>
  <tr>
  <td class="TableRowCommend" rowspan="2" height="40"  valign="middle" align="center" >
                姓名
          </td>
          <td class="TableRowBLOD" colspan="3" height="20" valign="middle" align="center" >
          建议本部门任职（可推荐提拔任用）
          </td>
           <td class="TableRowBLOD" colspan="3" height="20" valign="middle" align="center" >
          建议到其他单位交流任职
          </td> 
          <!-- 
           <logic:notEqual value="2" name="commendTableForm" property="flag">
          <td class="TableRowCommend" rowspan="2" height="40"  valign="middle" align="center" >
               清空
          </td>
          </logic:notEqual>
           --> 
 </tr>
 <tr>
 <td  class="TableRowBLOD" height="20" valign="middle" align="center" >
部门正职
 </td>
 <td class="TableRowCommend" height="20" valign="middle" align="center" >
 部门副职
 </td>
 <td class="TableRowCommend" height="20" valign="middle" align="center" >
 降职使用
 </td>
 <td class="TableRowBLOD" height="20" valign="middle" align="center" >
 提拔任用
 </td>
 <td class="TableRowCommend" height="20" valign="middle" align="center" >
 平级交流
 </td>
 <td class="TableRowCommend" height="20" valign="middle" align="center" >
 降职使用
 </td>
 
 </tr> 
  </thead>
  <logic:iterate id="element" name="commendTableForm" property="commendList" indexId="index"> 
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
   <td align="center" valign="middle" class="RecordRowFONT">
      <font size="5"><bean:write name="element" property="a0101_1"/></font>
   </td>
    <td align="center" valign="middle" class="RecordRowBLOD">
     <logic:equal name="element" property="self" value="1">
     <input type="radio" name="<%=index+"a"%>" value="0" disabled/>
     <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C1"%>" value="1" disabled/>	
    </logic:equal>
     <logic:equal name="element" property="self" value="0">
     <logic:equal name="element" property="C1" value="1">
        <input type="radio" onmouseover="setValue(this);" onclick='setValueO(this,"<%="commendList["+index+"].C1"%>","<%=index%>")' name="<%=index+"a"%>" value="0" checked="checked" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
         <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C1"%>" checked="checked" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     <logic:equal name="element" property="C1" value="0">
      <input type="radio" onmouseover="setValue(this);" onclick='setValueO(this,"<%="commendList["+index+"].C1"%>","<%=index%>")' name="<%=index+"a"%>" value="0" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
        <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C1"%>" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     </logic:equal>
   </td>
    <td align="center" valign="middle" class="RecordRow">
     <logic:equal name="element" property="self" value="1">
      <input type="radio" name="<%=index+"a"%>" value="0" disabled/>
     <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C2"%>" value="1" disabled/>	
    </logic:equal>
     <logic:equal name="element" property="self" value="0">
     <logic:equal name="element" property="C2" value="1">
      <input type="radio" onmouseover="setValue(this);" onclick='setValueO(this,"<%="commendList["+index+"].C2"%>","<%=index%>")' name="<%=index+"a"%>" value="0" checked="checked" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
         <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C2"%>" checked="checked" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     <logic:equal name="element" property="C2" value="0">
      <input type="radio" onmouseover="setValue(this);" onclick='setValueO(this,"<%="commendList["+index+"].C2"%>","<%=index%>")' name="<%=index+"a"%>" value="0"  <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
        <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C2"%>" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     </logic:equal>
   </td>
    <td align="center" valign="middle" class="RecordRow">
     <logic:equal name="element" property="self" value="1">
      <input type="radio" name="<%=index+"a"%>" value="0" disabled/>
     <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C3"%>" value="1" disabled/>	
    </logic:equal>
     <logic:equal name="element" property="self" value="0">
     <logic:equal name="element" property="C3" value="1">
      <input type="radio" onmouseover="setValue(this);" onclick='setValueO(this,"<%="commendList["+index+"].C3"%>","<%=index%>")' name="<%=index+"a"%>" value="0"checked="checked" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
         <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C3"%>" checked="checked" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     <logic:equal name="element" property="C3" value="0">
      <input type="radio" onmouseover="setValue(this);" onclick='setValueO(this,"<%="commendList["+index+"].C3"%>","<%=index%>")' name="<%=index+"a"%>" value="0" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
        <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C3"%>" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     </logic:equal>
   </td>
    <td align="center" valign="middle" class="RecordRowBLOD">
     <logic:equal name="element" property="self" value="1">
      <input type="radio" name="<%=index+"a"%>" value="0" disabled/>
     <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C4"%>" value="1" disabled/>	
    </logic:equal>
     <logic:equal name="element" property="self" value="0">
     <logic:equal name="element" property="C4" value="1">
      <input type="radio" onmouseover="setValue(this);" onclick='setValueO(this,"<%="commendList["+index+"].C4"%>","<%=index%>")' name="<%=index+"a"%>" value="0" checked="checked" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
         <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C4"%>" checked="checked" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     <logic:equal name="element" property="C4" value="0">
      <input type="radio" onmouseover="setValue(this);" onclick='setValueO(this,"<%="commendList["+index+"].C4"%>","<%=index%>")' name="<%=index+"a"%>" value="0" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
        <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C4"%>" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal> />	 
     </logic:equal>
     </logic:equal>
   </td>
    <td align="center" valign="middle" class="RecordRow">
     <logic:equal name="element" property="self" value="1">
      <input type="radio" name="<%=index+"a"%>" value="0" disabled/>
     <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C5"%>" value="1" disabled/>	
    </logic:equal>
     <logic:equal name="element" property="self" value="0">
       <logic:equal name="element" property="C5" value="1">
        <input type="radio" onmouseover="setValue(this);" onclick='setValueO(this,"<%="commendList["+index+"].C5"%>","<%=index%>")' name="<%=index+"a"%>" value="0" checked="checked" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
         <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C5"%>" checked="checked" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     <logic:equal name="element" property="C5" value="0">
      <input type="radio" onmouseover="setValue(this);" onclick='setValueO(this,"<%="commendList["+index+"].C5"%>","<%=index%>")' name="<%=index+"a"%>" value="0"  <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
        <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C5"%>" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     </logic:equal>
   </td>
    <td align="center" valign="middle" class="RecordRow">
     <logic:equal name="element" property="self" value="1">
      <input type="radio" name="<%=index+"a"%>" value="0" disabled/>
     <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C6"%>" value="1" disabled/>	
    </logic:equal>
     <logic:equal name="element" property="self" value="0">
       <logic:equal name="element" property="C6" value="1">
        <input type="radio" onmouseover="setValue(this);" onclick='setValueO(this,"<%="commendList["+index+"].C6"%>","<%=index%>")' name="<%=index+"a"%>" value="0" checked="checked" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
         <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C6"%>" checked="checked" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal> />	 
     </logic:equal>
     <logic:equal name="element" property="C6" value="0">
      <input type="radio" onmouseover="setValue(this);" onclick='setValueO(this,"<%="commendList["+index+"].C6"%>","<%=index%>")' name="<%=index+"a"%>" value="0" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>
        <input style="display:none" type="checkbox" name="<%="commendList["+index+"].C6"%>" value="1" <logic:equal name="commendTableForm" property="flag" value="2">disabled</logic:equal>/>	 
     </logic:equal>
     </logic:equal>
   </td>
   <!--  
    <logic:notEqual value="2" name="commendTableForm" property="flag">
    <td align="center" valign="middle" class="RecordRow">
 <input type="button" value="清空" name="<%=index+"button"%>" onclick='clearValueE("<%=index+"a"%>","<%=index%>")' class="mybuttonBig"/>
   </td>
   </logic:notEqual>
   -->
 </tr>
 </logic:iterate>
  </logic:equal>
 </table>
 </td>
 </tr>
  <tr>
 <logic:equal name="commendTableForm" property="tableType" value="1">
  <td colspan="7" align="left">
 </logic:equal>
  <logic:equal name="commendTableForm" property="tableType" value="0">
  <td colspan="9" align="left">
  </logic:equal>
  &nbsp;&nbsp;
  </td>
  </tr>
 <tr>
 <logic:equal name="commendTableForm" property="tableType" value="1">
  <td colspan="7" align="left">
 </logic:equal>
  <logic:equal name="commendTableForm" property="tableType" value="0">
  <td colspan="9" align="left">
  </logic:equal>
  <logic:notEqual value="2" name="commendTableForm" property="flag">
  <input type="button" name="bc" class="mybuttonBig" value="<bean:message key="button.save"/>" onclick="sub('0');"/>&nbsp;
  <input type="button" name="wc" class="mybuttonBig" value="完 成" onclick="sub('1');"/>
  </logic:notEqual>
  &nbsp;<input type="button" name="rb" class="mybuttonBig" value="<bean:message key="button.return"/>" onclick="returnBack();"/> </td>
 </tr>
 </table>
</html:form>
