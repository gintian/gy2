<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<html:form action="/kq/register/empchangebase">

<script language="javascript">
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
function basechange(){
	var len=document.empChangeForm.elements.length;
	var i;
	var isCorrect=false;
	for (i=0;i<len;i++){
		if(document.empChangeForm.elements[i].type=="checkbox"){
			if(document.empChangeForm.elements[i].checked==true&&document.empChangeForm.elements[i].name!="selbox"){
				isCorrect=true;
				break;
			}
		}
	}      
	if(isCorrect){
		displayWaiting();
		empChangeForm.action="/kq/register/empchangebase.do?b_bidui=link";
	    empChangeForm.submit();
	}else{
		alert("请先选择人员！");
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
       <td align="center" class="TableRow" nowrap><bean:message key="kq.emp.change.nbase"/></td> 
       <td align="center" class="TableRow" nowrap><bean:message key="b0110.label"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="kq.emp.change.ob0110"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="e0122.label"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="kq.emp.change.oe0122"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="e01a1.label"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="kq.emp.change.oe01a1"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="label.title.name"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="gz.info.sa0101"/></td>
       <logic:equal value="1" name="empChangeForm" property="deptChange">
       <td align="center" class="TableRow" nowrap><bean:message key="kq.emp.change.changedate"/></td>
       </logic:equal>
       
    
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
          i++;          
          %> 
            <td align="center" class="RecordRow" nowrap>
               <hrms:checkmultibox name="empChangeForm" property="recordListForm.select" value="true" indexes="indexes"/>&nbsp;
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
                  <hrms:codetoname codeid="UN" name="element" codevalue="string(ob0110)" codeitem="codeitem" scope="page"/> 
                   &nbsp;<bean:write name="codeitem" property="codename" />      
            </td>                   
            <td align="left" class="RecordRow" nowrap> 
                   <hrms:codetoname codeid="UM" name="element" codevalue="string(e0122)" codeitem="codeitem" scope="page" uplevel="${empChangeForm.uplevel}"/>  	      
          	    &nbsp;<bean:write name="codeitem" property="codename" />             
             </td>  

              <td align="left" class="RecordRow" nowrap> 
                   <hrms:codetoname codeid="UM" name="element" codevalue="string(oe0122)" codeitem="codeitem" scope="page" uplevel="${empChangeForm.uplevel}"/>  	      
          	    &nbsp;<bean:write name="codeitem" property="codename" />             
             </td>  
             <td align="left" class="RecordRow" nowrap> 
                   <hrms:codetoname codeid="@K" name="element" codevalue="string(e01a1)" codeitem="codeitem" scope="page"/>  	      
          	    &nbsp;<bean:write name="codeitem" property="codename" />             
             </td>   
              <td align="left" class="RecordRow" nowrap> 
                   <hrms:codetoname codeid="@K" name="element" codevalue="string(oe01a1)" codeitem="codeitem" scope="page"/>  	      
          	    &nbsp;<bean:write name="codeitem" property="codename" />             
             </td>  
             <td align="left" class="RecordRow" nowrap>              
                  &nbsp; <bean:write  name="element" property="string(a0101)" filter="true"/>&nbsp;
             </td> 
             <td align="left" class="RecordRow" nowrap>              
                  &nbsp; <bean:write  name="element" property="string(oa0101)" filter="true"/>&nbsp;
             </td>             
             <logic:equal value="1" name="empChangeForm" property="deptChange">
	             <td align="left" class="RecordRow" nowrap>                  	      
	          	    <bean:write  name="element" property="string(change_date)" filter="true"/>&nbsp;             
	             </td>
             </logic:equal>   
         </tr>
  </hrms:extenditerate>    
  </table>
  <table align="center" width="100%"  class="RecordRowP">
 
    <tr>
       <td valign="bottom" class="tdFontcolor">    <hrms:paginationtag name="empChangeForm"
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

 <table width="100%" align="center">
    <tr>
    	<td valign="bottom" align="left">  
         	其它变动<font color="red">
         <bean:write name="empChangeForm" property="base_count" /> </font>人 
    	</td>
       <td align="left"  nowrap>   
       <!-- 
       <input type="button" name="b_delete" value='<bean:message key="label.query.selectall"/>' class="mybutton" onclick="selAll()"> 
       -->
        <input type="button" value='<bean:message key="button.ok"/>' onclick="basechange();" class="mybutton">   
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

<script>
    function displayWaiting() {
        document.getElementById("wait").style.display="block";
    }
</script>