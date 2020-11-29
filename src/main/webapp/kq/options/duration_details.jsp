<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>

<logic:equal name="kqDurationForm" property="flag" value="3">
<script language="JavaScript">
	  parent.mil_menu.location.reload();
        
</script>
</logic:equal>

<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->

<%
int i=0;
%>
<script language="JavaScript">
	 
      function dels()
      {
       var len=document.kqDurationForm.elements.length;
       var uu;
       for (var i=0;i<len;i++)
       {
           if (document.kqDurationForm.elements[i].type=="checkbox")
           {
              if(document.kqDurationForm.elements[i].checked==true)
              {
                uu="dd";
                break;
               }
           }
       }
       if(uu!="dd")
       {
          alert("请选择要删除的记录！");
          return false;
       }
        if(confirm("确定删除所选考勤期间吗?"))
        {
          kqDurationForm.action="/kq/options/duration_details.do?b_delete=link";
           kqDurationForm.target="il_body";
          kqDurationForm.submit();
        }        
      } 
var checkflag = "false";

 function selAll()
  {
      var len=document.kqDurationForm.elements.length;
       var i;

    
  
     if(checkflag=="false")
     {
        for (i=0;i<len;i++)
        {
           if (document.kqDurationForm.elements[i].type=="checkbox")
            {
              document.kqDurationForm.elements[i].checked=true;
            }
         }
        checkflag="true";
     }else
     {
        for (i=0;i<len;i++)
        {
          if (document.kqDurationForm.elements[i].type=="checkbox")
          {
            document.kqDurationForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    } 
        
  } 
   function noUpdate()
   {
      
       alert("该期间已经封存，不可以修改！");
     
   } 
   function durationTrans(flag)
   {
      var len=document.kqDurationForm.elements.length;
      var i;
      var j=0;   
      var name="";  
      var kq_duration="";
      for (i=0;i<len;i++)
      {
         if(document.kqDurationForm.elements[i].type=="checkbox" && "aa" != document.kqDurationForm.elements[i].name)
         {
           if(document.kqDurationForm.elements[i].checked==true)
           {
              j++;
              name=document.kqDurationForm.elements[i].name;
              name=name.substring(name.indexOf("[")+1,name.indexOf("]"));
              var obj =document.getElementsByName("hidden_"+name);
              kq_duration= obj[0].value;
           }
         }
      }      
      if(j==1)
      {
         if(flag=="b_usave")
         {
            getAppSeal(kq_duration);
         }else if(flag=="b_dsave" && confirm("您确定要解封所选考勤期间及其之后所有已封存期间？"))
         {
            kqDurationForm.action="/kq/options/duration_details.do?b_dsave=link";
            kqDurationForm.submit();
         }
        
      }else if(j==0){
    	  if(flag=="b_usave")
          {
            alert("请选择需要封存的考勤期间！");
          }else if(flag=="b_dsave")
          {
            alert("请选择需要解封的考勤期间！");
          }  
          return false
      }else
      {
          var ok = false;
    	  if(flag=="b_usave")
          {
    		  ok = confirm("您确定要封存所选考勤期间及其之前所有未封存期间？");
          }else if(flag=="b_dsave")
          {
        	  ok = confirm("您确定要解封所选考勤期间及其之后所有已封存期间？");
          } 
          if(ok)
          {
    	      kqDurationForm.action="/kq/options/duration_details.do?" + flag + "=link";
              kqDurationForm.submit();
          }
      }
   }
   function getAppSeal(kq_duration) 
   {  	if(!confirm("您确定要封存所选考勤期间及其之前所有未封存期间？"))
       {  
          return false;
       }
       startSeal();
   }
   function sealTerm(outparamters)
   {
      MusterInitData();
	  var tes=outparamters.getValue("notapptag");
      var pigeonhole_type=outparamters.getValue("pigeonhole_type");
      if(tes=="seal")
      {
          startSeal();
      }else if(tes=="noseal")      
      {
        var q03=outparamters.getValue("notapp_list");            
        if(q03=="have")
        {
           alert('请先将所有用户的考勤信息进行月汇总审批！');
           return false;
        }
        var notpige=outparamters.getValue("notpige_list");            
        if(notpige=="have")
        {
           alert('请先将所有用户的考勤信息进行归档！');
           return false;
        }
        var isseal=false;
        var q07=outparamters.getValue("notQ07_list");
        var q09=outparamters.getValue("notQ09_list");  
        if(q07=="have")
        {
           if(confirm("部门考勤信息是否需要进行日汇总！"))
           {
              return false;
           }else
           {
              isseal=true;
           }
        }
        if(q09=="have")
        {
           if(confirm("部门考勤信息是否需要进行月汇总！"))
           {
              return false;
           }else
           {
              isseal=true;              
           }
        }
        if(isseal)
        {
          startSeal();
         }
     }
   }
   function startSeal()
   {
      kqDurationForm.action="/kq/options/duration_details.do?b_usave=link";
      kqDurationForm.submit();
   }
   function MusterInitData()
   {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
   }
</script>
<html:form action="/kq/options/duration_details">
 <table width="75%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:10px;">
    	
    <tr>
      <td align="center" class="TableRow" nowrap><input type="checkbox" name="aa" value="true" onclick="selAll()">&nbsp;</td>
      <td align="center" class="TableRow" nowrap><bean:message key="kq.deration_details.kqqj"/></td>
      <td align="center" class="TableRow" nowrap><bean:message key="kq.deration_details.start"/></td>
      <td align="center" class="TableRow" nowrap><bean:message key="kq.deration_details.end"/></td>	 
      <td align="center" class="TableRow" nowrap><bean:message key="kq.deration_details.dyyf"/></td>	 
      <td align="center" class="TableRow" nowrap><bean:message key="kq.deration_details.save"/></td>	 
      <td align="center" class="TableRow" nowrap><bean:message key="kq.deration_details.edit"/></td>	  	  
    </tr>
    <hrms:extenditerate id="element" name="kqDurationForm" property="kqDurationForm.list" indexes="indexes"  pagination="kqDurationForm.pagination" pageCount="30" scope="session">
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
            <td align="center" class="RecordRow" nowrap>
            <hrms:checkmultibox name="kqDurationForm" property="kqDurationForm.select" value="true" indexes="indexes"/>&nbsp;
            <input type="hidden" name="hidden_${indexes}" value='<bean:write name="element" property="string(kq_duration)"/>'/>
            </td> 
            <hrms:priv func_id = "27031"> 
              <td align="left" class="RecordRow" nowrap>              
                   &nbsp;<bean:write  name="element" property="string(kq_duration)" filter="true"/>&nbsp;
              </td>
            </hrms:priv> 
            <hrms:priv func_id = "27031"> 
               <td align="left" class="RecordRow" nowrap>              
                   &nbsp;<bean:write  name="element" property="string(kq_start)" filter="true"/>&nbsp;
              </td> 
            </hrms:priv>
            <hrms:priv func_id = "27031">                   
              <td align="left" class="RecordRow" nowrap>              
                   &nbsp;<bean:write  name="element" property="string(kq_end)" filter="true"/>&nbsp;
              </td> 
            </hrms:priv>  
            <hrms:priv func_id = "27031">
              <td align="left" class="RecordRow" nowrap>              
                   &nbsp;<bean:write  name="element" property="string(gz_duration)" filter="true"/>&nbsp;
              </td>
            </hrms:priv>   
              <hrms:priv func_id = "27031">
               <td align="left" class="RecordRow" nowrap>              
               	<logic:equal name="element" property="string(finished)" value="1">
                   &nbsp;<bean:message key="kq.duration.usave"/>
                 </logic:equal>
                 <logic:equal name="element" property="string(finished)" value="0">
                   &nbsp;<bean:message key="kq.duration.dsave"/>
                 </logic:equal>
              </td> 
              </hrms:priv>
              <hrms:priv func_id = "27031">
              <td align="center" class="RecordRow" nowrap>
            	<logic:equal name="element" property="string(finished)" value="1">
                   &nbsp;
                 </logic:equal>
                 <logic:equal name="element" property="string(finished)" value="0">
                  	 <bean:define id="kq_duration1" name="element" property="string(kq_duration)"/>
			         <bean:define id="kq_start1" name="element" property="string(kq_start)"/>
			         <%
			         		//参数加密
			    		     String str1 = "akq_durations="+kq_duration1+"&start="+kq_start1;
			         %>
                   <a href="/kq/options/add_duration.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str1) %>"><img src="/images/edit.gif" border=0></a>
                 </logic:equal>
            	
            	
	        </td>   
	        </hrms:priv>                                	    
      	    
          </tr>
        </hrms:extenditerate> 
  </table>
    <table width="75%" class="RecordRowP" align="center"> 
    <tr>
       <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
          <bean:write name="kqDurationForm" property="kqDurationForm.pagination.current" filter="true" />
          <bean:message key="label.page.sum"/>
          <bean:write name="kqDurationForm" property="kqDurationForm.pagination.count" filter="true" />
          <bean:message key="label.page.row"/>
          <bean:write name="kqDurationForm" property="kqDurationForm.pagination.pages" filter="true" />
          <bean:message key="label.page.page"/>
       </td>
       <td  align="right" nowrap class="tdFontcolor">
          <p align="right">
           <hrms:paginationlink name="kqDurationForm" property="kqDurationForm.pagination"
                   nameId="kqDurationForm">
           </hrms:paginationlink>
       </td>
    </tr>  
 </table>
<table  width="80%" align="center">
          <tr>
           <hrms:priv func_id = "27031">   
           <td align="center">
            <hrms:submit styleClass="mybutton" property="b_add">	<bean:message key="button.insert"/> </hrms:submit>
        
            <input type="button" name="b_delete" value='<bean:message key="button.delete"/>' class="mybutton" onclick="dels()">
            <!--<hrms:submit styleClass="mybutton" property="b_delete">	<bean:message key="button.delete"/> </hrms:submit>-->
            <input type="button" name="b_usave" value='<bean:message key="kq.deration_details.usave"/>' class="mybutton" onclick="durationTrans('b_usave');">
            <!--<hrms:submit styleClass="mybutton" property="b_usave"><bean:message key="kq.deration_details.usave"/></hrms:submit>-->
            <input type="button" name="b_dsave" value='<bean:message key="kq.deration_details.dsave"/>' class="mybutton" onclick="durationTrans('b_dsave');">
            <hrms:tipwizardbutton flag="workrest" target="il_body" formname="kqDurationForm"/>
           </td>
           </hrms:priv>
          </tr>          
</table>
</html:form>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>

             <td class="td_style common_background_color" height=24><bean:message key="classdata.isnow.wiat"/></td>

           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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
</div>
<script language="javascript">
 MusterInitData();
 </script>