<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.performance.objectiveManage.setUnderlingObjective.SetUnderlingObjectiveForm"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
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
String levelstr=setUnderlingObjectiveForm.getLevel();
String maxLevelstr=setUnderlingObjectiveForm.getMaxLevel();
int level=1;
int maxLevel=1;
if(levelstr!=null&&!levelstr.equals(""))
{
   level=Integer.parseInt(levelstr);
}
if(maxLevelstr!=null&&!maxLevelstr.equals(""))
{
   maxLevel=Integer.parseInt(maxLevelstr);
}
   
    String url_p=SystemConfig.getServerURL(request);
 %>  
 <link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
<!--
function sub(outparameters)
{
   var send=outparameters.getValue("send");
   var str="";
   var lev="${setUnderlingObjectiveForm.level}";
   var intlevel=parseInt(lev);
   var firstRadio=0;
   var num=0;
   var actionCount=0;
   for(var i=0;i<intlevel;i++)
   {
     var isActionObj=document.getElementById(i+"isAction");
     if(isActionObj.value=='1')
     {
      actionCount++;
      continue;
     }
     var selectObj=document.getElementById(i+"se");
     var tempstr="";
     for(var h=0;h<selectObj.options.length;h++)
     {
       if(selectObj.options[h].selected)
       {
         tempstr+=selectObj.options[h].value;
         tempstr+="`"+selectObj.options[h].text;
         break;
       }
     }
     if(tempstr=='')
       continue;
     var radioObj=document.getElementsByName(i+"sp_flag");
     var radiovalue="";
     for(var h=0;h<radioObj.length;h++)
     {
       if(radioObj[h].checked)
       {
         if(num==0)
         {
            firstRadio=i;
         }
         radiovalue=radioObj[h].value;
         break;
       }  
     }
     if(radiovalue=="")
     {
       continue;
     }
     var bh="";
     if(radiovalue=='07')
     {
       var hoObj=document.getElementById(i+"t");
       bh=hoObj.value;
       if(trim(bh)=='')
       {
          if(!confirm(KH_PLAN_BACK+"原因未填，是否继续？"))
             return;
       }
     }
     var BPselectObj=document.getElementById((i+1)+"se");
     var BPtempstr="";
     if(BPselectObj)
     {
        for(var h=0;h<BPselectObj.options.length;h++)
        {
          if(BPselectObj.options[h].selected)
          {
            BPtempstr+=BPselectObj.options[h].value;
            BPtempstr+="`"+BPselectObj.options[h].text;
            break;
         }
       }
     }
     if(BPtempstr=='')
     {
        BPselectObj=document.getElementById((i+2)+"se");
        if(BPselectObj)
        {
        for(var h=0;h<BPselectObj.options.length;h++)
        {
          if(BPselectObj.options[h].selected)
          {
             BPtempstr=BPselectObj.options[h].value;
             BPtempstr+="`"+BPselectObj.options[h].text;
            break;
         }
       }
       }
     }
     if(BPtempstr=='')
     {
        BPselectObj=document.getElementById((i+3)+"se");
        if(BPselectObj)
        {
        for(var h=0;h<BPselectObj.options.length;h++)
        {
          if(BPselectObj.options[h].selected)
          {
             BPtempstr=BPselectObj.options[h].value;
             BPtempstr+="`"+BPselectObj.options[h].text;
            break;
         }
       }
       }
     }
     str+="/"+tempstr+"`"+radiovalue+"`"+i+"`"+bh+"`"+BPtempstr;
     num++;
   }
   if(firstRadio!=0)
   {
      var defaultstr="";
      for(var i=0;i<firstRadio;i++)
      {
           var isActionObj=document.getElementById(i+"isAction");
           if(isActionObj.value=='1')
           {
               continue;
           }
          var selectObj=document.getElementById(i+"se");
          var tempstr="";
          for(var h=0;h<selectObj.options.length;h++)
          {
            if(selectObj.options[h].selected)
            {
               tempstr+=selectObj.options[h].value;
               tempstr+="`"+selectObj.options[h].text;
               break;
             }
          }
          if(tempstr=='')
            continue;
         var BPselectObj=document.getElementById((i+1)+"se");
         var BPtempstr="";
         if(BPselectObj)
         {
            for(var h=0;h<BPselectObj.options.length;h++)
            {
               if(BPselectObj.options[h].selected)
               {
                  BPtempstr+=BPselectObj.options[h].value;
                  BPtempstr+="`"+BPselectObj.options[h].text;
                  break;
               }
            }
         }
         if(BPtempstr=='')
         {
             BPselectObj=document.getElementById((i+2)+"se");
             if(BPselectObj)
             {
               for(var h=0;h<BPselectObj.options.length;h++)
              {
                if(BPselectObj.options[h].selected)
                {
                   BPtempstr=BPselectObj.options[h].value;
                   BPtempstr+="`"+BPselectObj.options[h].text;
                   break;
                }
              }
            }
         } 
         if(BPtempstr=='')
         {
            BPselectObj=document.getElementById((i+3)+"se");
           if(BPselectObj)
           {
             for(var h=0;h<BPselectObj.options.length;h++)
             {
                if(BPselectObj.options[h].selected)
                 {
                    BPtempstr=BPselectObj.options[h].value;
                    BPtempstr+="`"+BPselectObj.options[h].text;
                    break;
                 }
             }
           }
         }
          defaultstr+="/"+tempstr+"`02`"+i+"` `"+BPtempstr;
      }
      str=defaultstr+str;
   }
   if(trim(str).length>0)
      str=str.substring(1);
   var isSend="0";
   if(trim(str).length>0)
   {
      if(!confirm("确定执行代批操作吗?"))
      {
         return;
      }
      if(send=='send')
      {
         if(confirm("是否发送邮件?"))
         {
            isSend="1";
         }
      }
   }
   else
   {
     return;
   }
   var hashVo=new ParameterSet();
   hashVo.setValue("str",getEncodeStr(str));
   hashVo.setValue("object_id","${setUnderlingObjectiveForm.object_id}");
   hashVo.setValue("plan_id","${setUnderlingObjectiveForm.p_id}");
   hashVo.setValue("khType","${setUnderlingObjectiveForm.khType}");
   hashVo.setValue("market","${setUnderlingObjectiveForm.market}");
   hashVo.setValue("level","${setUnderlingObjectiveForm.level}");
   hashVo.setValue("url_p",document.getElementById("hostname").href);
   hashVo.setValue("isSend",isSend);
   var request=new Request({method:'post',asynchronous:false,onSuccess:save_ok,functionId:'9028000306'},hashVo);			
  
}
function save_ok(outparameters)
{
  window.returnValue="1";
  window.close();
}
function isSendEmail()
{
   var hashVo=new ParameterSet();
   hashVo.setValue("str","11");
   var request=new Request({method:'post',asynchronous:false,onSuccess:sub,functionId:'9028000308'},hashVo);			
  
}
function closeWindow()
{
  window.returnValue="0";
  window.close();
}
function visibleBH(id,spvalue)
{
 var obj = document.getElementById(id+'tr');
 if(spvalue=='07')
   obj.style.display="block";
  else
   obj.style.display="none";
}
function clearUp(name)
{
  for(var i=0;i<name;i++)
  {
     var isActionObj=document.getElementById(i+"isAction");
     if(isActionObj.value=='1')
     {
      continue;
     }
    var obj = document.getElementsByName(i+"sp_flag");
    for(var j=0;j<obj.length;j++)
    {
      obj[j].checked=false;
    }
    document.getElementById(i+"tr").style.display="none"; 
    document.getElementById(i+"t").value=""; 
  }
}
//-->
</script>
<html:form action="/performance/objectiveManage/objectiveCardMonitor/objective_state_list">
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
<td align="center">
<fieldset align="center">
<legend><font class='<%=tt4CssName%>'>代批</font></legend>
<div style="overflow:auto;width:590px;height:300px;" >
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
<td align="center" colspan="3"><strong><bean:write name="setUnderlingObjectiveForm" property="a0101"/></strong></td>
</tr>
<%
 ArrayList mainbodyList = setUnderlingObjectiveForm.getMainbodyList();
  for(int i=0;i<mainbodyList.size();i++)
  {
    out.println("<tr>");
    LazyDynaBean bean = (LazyDynaBean)mainbodyList.get(i);
    String leaderLevel=(String)bean.get("level");
    String isAction=(String)bean.get("isAction");
    String spa0100=(String)bean.get("spa0100");
    String sp_flag=(String)bean.get("sp_flag");
    ArrayList sublist=(ArrayList)bean.get("sublist");
    out.println("<td align=\"center\" class=\"RecordRow\"><font class='"+tt3CssName+"'>");
    if(leaderLevel.equals("1"))
    {
      out.println("直接上级审批");
    }
    else if(leaderLevel.equals("2"))
    {
       out.println("间接上级审批");
    }
    else if(leaderLevel.equals("3"))
    {
       out.println("第三上级审批");
    }
    else if(leaderLevel.equals("4"))
    {
       out.println("第四上级审批");
    }
    out.println("</font></td>");
    out.println("<td align=\"center\" class=\"RecordRow\">");
    out.print("<select width=\"10px\" id=\""+i+"se\"");
    if(isAction.equals("1"))
       out.print(" disabled");
    out.println(">");
   for(int n=0;n<sublist.size();n++)
    {
       LazyDynaBean abean = (LazyDynaBean)sublist.get(n);
       String a0100=(String)abean.get("a0100");
       String body_id=(String)abean.get("body_id");
       out.println("<option value=\""+a0100+"`"+body_id+"\"");
       if(spa0100.equalsIgnoreCase(a0100))
         out.print(" selected");
       out.println(">");
       out.println((String)abean.get("a0101")+"</option>");
    }
    out.println("</select></td>");
    out.println("<td align=\"center\" class=\"RecordRow\">");
    out.print("<input type=\"hidden\" name=\""+i+"is\" value=\"");
    if(isAction.equals("1"))
      out.print("1\"");
    else
      out.print("0\"");
    out.println(" id=\""+i+"isAction\"/>");
    if((i+1)==level||(i+1)==maxLevel)
    {
        out.print("<input type=\"radio\" name=\""+i+"sp_flag\" value=\"03\" onclick=\"visibleBH('"+i+"','03');\"");
        if(sp_flag.equalsIgnoreCase("03"))
           out.print(" checked ");
        if(isAction.equals("1"))
           out.print(" disabled=\true\" ");
        out.println("/>  <font class='"+tt3CssName+"'>"+ResourceFactory.getProperty("info.appleal.state8")+"</font>");   
        out.print("<input type=\"radio\" name=\""+i+"sp_flag\" value=\"07\" onclick=\"visibleBH('"+i+"','07');\"");
        if(sp_flag.equalsIgnoreCase("07"))
           out.print(" checked ");
        if(isAction.equals("1"))
           out.print(" disabled=\true\"");
         out.println("/>  <font class='"+tt3CssName+"'>"+ResourceFactory.getProperty("info.appleal.state10")+"</font>");              
    }
    else
    {
        out.print("<input type=\"radio\" name=\""+i+"sp_flag\" value=\"02\" onclick=\"visibleBH('"+i+"','02');\"");
        if(sp_flag.equalsIgnoreCase("02"))
           out.print(" checked ");
        if(isAction.equals("1"))
           out.print(" disabled=\true\"");
        out.println("/>   <font class='"+tt3CssName+"'>"+ResourceFactory.getProperty("info.appleal.state7")+"</font>");   
        out.print("<input type=\"radio\" name=\""+i+"sp_flag\" value=\"07\" onclick=\"visibleBH('"+i+"','07');\"");
        if(sp_flag.equalsIgnoreCase("07"))
           out.print(" checked");
        if(isAction.equals("1"))
           out.print(" disabled=\true\" ");
         out.println("/>  <font class='"+tt3CssName+"'> "+ResourceFactory.getProperty("info.appleal.state10")+"</font>");         
    }
    out.println("</td>");
    out.println("</tr>");
    out.println("<tr style=\"display=none\" id=\""+i+"tr\">");
    out.println("<td class=\"RecordRow\" align=\"center\" valign=\"top\">  <font class='"+tt3CssName+"'>"+ResourceFactory.getProperty("info.appleal.state10")+"原因</font>:</td>");
    out.println("<td class=\"RecordRow\" colspan=\"2\"><textarea id=\""+i+"t\" name=\"bohui\" rows=\"8\" cols=\"50\"></textarea></td>");
    out.println("</tr>");
  }


 %>
<tr>
<td colspan="3">
&nbsp;&nbsp;
</td>
</tr>
</table>
</div>
</fieldset>
</td>
</tr>
<tr>
<td colspan="3">
<input type="button" name="ok" value="<bean:message key="button.ok"/>" onclick="isSendEmail();" class="mybutton"/>
<input type="button" name="clear" value="<bean:message key="button.clearup"/>" onclick="clearUp('<%=level%>');" class="mybutton"/>
<input type="button" name="clo" value="<bean:message key="button.close"/>" onclick="closeWindow();" class="mybutton"/>
<html:hidden name="setUnderlingObjectiveForm" property="object_id"/>
<html:hidden name="setUnderlingObjectiveForm" property="p_id"/>
<html:hidden name="setUnderlingObjectiveForm" property="khType"/>
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
</td>
</tr>
</table>
</html:form>