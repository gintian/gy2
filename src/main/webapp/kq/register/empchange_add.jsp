<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<html:form action="/kq/register/empchange_add">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript">
	var weeks="";
	var feasts ="";
	var turn_dates="";
	var week_dates="";

	function getKqCalendarVar(){
		var hashvo=new ParameterSet();
		var request=new Request({method:'post',onSuccess:setKqCalendarVar,functionId:'15388800008'},hashvo);
	}
	function setKqCalendarVar(outparamters){
		weeks=outparamters.getValue("weeks");
		feasts=outparamters.getValue("feasts");  
		turn_dates=outparamters.getValue("turn_dates");  
		week_dates=outparamters.getValue("week_dates");
	}
function MusterInitData()
   {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
   }
   function change_over()
 {
    empChangeForm.action="/kq/register/empchange.do?br_over=link";
    empChangeForm.target="mil_body";
    empChangeForm.submit()
 }
   var checkflag = "false";

 function selAll()
  {
      var len=document.empChangeForm.elements.length;
       var i;
       
     if(checkflag=="false")
     {
        for (i=0;i<len;i++)
        {
           if (document.empChangeForm.elements[i].type=="checkbox")
            {
              document.empChangeForm.elements[i].checked=true;
            }
         }
        checkflag="true";
     }else
     {
        for (i=0;i<len;i++)
        {
          if (document.empChangeForm.elements[i].type=="checkbox")
          {
            document.empChangeForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    } 
  }
 
	function addchange(){
		var len=document.empChangeForm.elements.length;
		var i;
		var isCorrect=false;
		for (i=0;i<len;i++){
			if(document.empChangeForm.elements[i].type=="checkbox"){
				if(document.empChangeForm.elements[i].checked==true&&document.empChangeForm.elements[i].name!="selbox"&&!document.empChangeForm.elements[i].name!="re_static"){
					isCorrect=true;
					break;
				}
			}
		}      
		if(isCorrect){
            var a=0; 
            var b=0;       
            var selectid=new Array();
            for (i=0;i<len;i++)
            {
	        	     var aCheckBox = document.empChangeForm.elements[i];
	             if(aCheckBox.type=="checkbox" && aCheckBox.checked==true && aCheckBox.name!="selbox" && aCheckBox.name!="re_static")
	             {
	                 var endIndex = aCheckBox.name.indexOf("]");
	                 var checkIndex = parseInt(aCheckBox.name.substring(22,endIndex));
	                 var pagerows = "${empChangeForm.pagerows}";
	                 checkIndex = checkIndex%parseInt(pagerows);
	                 selectid[a++]=document.getElementById("IDs_"+checkIndex).value;
	             }
	         }
	         var hashvo=new ParameterSet();
			 hashvo.setValue("emplist",selectid);	
			 hashvo.setValue("end_date","${empChangeForm.end_date}");
			 var request=new Request({method:'post',asynchronous:true,onSuccess:returnInfo,functionId:'15301110669'},hashvo);
		}else{
        	     alert("请先选择人员！");
		}  
	}
	
	function returnInfo(outparamters){
		var info=outparamters.getValue("info");
		info = getDecodeStr(info);
		var flag=outparamters.getValue("flag");
		if(flag=="1"){
			alert(info);
			return false;
		} else {
			var waitInfo=eval("wait");
			waitInfo.style.display="block";
			empChangeForm.action="/kq/register/empchange_add.do?b_addchange=link";
			empChangeForm.submit();
		}
	}
</script>
<%
int i=0;
%>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
    <tr>
       <td align="center" class="TableRow" nowrap>
          <input type="checkbox" name="selbox" onclick="batch_select(this,'recordListForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
       </td>
       <td align="center" class="TableRow" nowrap="nowrap"><bean:message key="hrms.nbase"/></td>
       <td align="center" class="TableRow" nowrap="nowrap"><bean:message key="b0110.label"/></td>
       <td align="center" class="TableRow" nowrap="nowrap"><bean:message key="e0122.label"/></td>
       <td align="center" class="TableRow" nowrap="nowrap"><bean:message key="label.title.name"/></td>
       <td align="center" class="TableRow" nowrap="nowrap"> 
             <bean:message key="kq.emp.change.add.date"/>
        <html:hidden name="empChangeForm" property="changestatus" styleClass="text"/>
       </td> 
  </tr>  
 
  <hrms:extenditerate id="element" name="empChangeForm" property="recordListForm.list" indexes="indexes"  pagination="recordListForm.pagination" pageCount="${empChangeForm.pagerows}" scope="session">
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
          %> 
            <td align="center" class="RecordRow" nowrap>
               <hrms:checkmultibox name="empChangeForm" property="recordListForm.select" value="true" indexes="indexes"/>&nbsp;
               <input type="hidden" name="IDs" id="IDs_<%=i%>" value='<bean:write name="element" property="string(nbase)" filter="false"/>`<bean:write  name="element" property="string(a0100)" filter="true"/>' />
            </td>  
            <td align="left" class="RecordRow" nowrap>    
                  <hrms:codetoname codeid="@@" name="element" codevalue="string(nbase)" codeitem="codeitem" scope="page"/> 
                   &nbsp;<bean:write name="codeitem" property="codename" />      
            </td>  
            <td align="left" class="RecordRow" nowrap>    
                  <hrms:codetoname codeid="UN" name="element" codevalue="string(b0110)" codeitem="codeitem" scope="page"/> 
                   &nbsp;<bean:write name="codeitem" property="codename" />      
            </td>                    
            <td align="left" class="RecordRow" nowrap> 
                   <hrms:codetoname codeid="UM" name="element" codevalue="string(e0122)" codeitem="codeitem" scope="page" uplevel="${empChangeForm.uplevel}"/>  	      
          	    &nbsp;<bean:write name="codeitem" property="codename" />             
             </td>   
            <td align="left" class="RecordRow" nowrap>              
                   &nbsp;<bean:write  name="element" property="string(a0101)" filter="true"/>&nbsp;
            </td> 
             <td align="left" class="RecordRow" nowrap>              
                  <bean:write name="element" property="string(change_date)" />
             </td>
         </tr>
         <%
         i++; 
         %>
  </hrms:extenditerate>    
  </table>
  <table align="center" width="100%"  class="RecordRowP"> 
    <tr>
    <td valign="bottom">
      新增人数:<font color="red">
    <bean:write name="empChangeForm" property="add_count" /> </font>人 
    
        <td width="40%" valign="bottom" align="left" class="tdFontcolor" nowrap>
		           
		            <hrms:paginationtag name="empChangeForm"
							pagerows="${empChangeForm.pagerows}" property="recordListForm.pagination"
							 refresh="true"></hrms:paginationtag>
			    </td>
       <td  align="right" nowrap class="tdFontcolor">
          <p align="right">
           <hrms:paginationlink name="empChangeForm" property="recordListForm.pagination"
                   nameId="recordListForm">
           </hrms:paginationlink>
       </td>
    </tr>  
 </table>

 <table width="50%" align="center">
    <tr>
       <td width="60%" align="center"  nowrap>
       
        <input type="button" id="add" name="btnreturn" value='<bean:message key="button.ok"/>' onclick="addchange();" class="mybutton">	     	                 	     
	   
	    <input type="button" name="btnreturn" value='<bean:message key="button.leave"/>' onclick="change_over();" class="mybutton">
	 </td>
    </tr>
</table>  			      
</html:form>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style common_background_color" height="24"><bean:message key="classdata.isnow.wiat"/></td>
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
