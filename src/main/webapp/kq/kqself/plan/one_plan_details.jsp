<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
<script language="javascript">
    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>
<style type="text/css">
<!--
.RecordRow3 {
	border: inset 1px #B9D2F5;
	BORDER-BOTTOM: #B9D2F5 1pt solid; 
	BORDER-LEFT: #B9D2F5 1pt solid; 
	BORDER-RIGHT: #B9D2F5 1pt solid; 
	BORDER-TOP: #B9D2F5 1pt solid;
	font-size: 12px;
	height:70;
}
-->
</style>
<SCRIPT LANGUAGE="JavaScript">
  var checkflag = "false";
  function selAll()
   {
      var len=document.kqPlanInfoForm.elements.length;
       var i;
    if(checkflag == "false")
    {
        for (i=0;i<len;i++)
        {
         if (document.kqPlanInfoForm.elements[i].type=="checkbox")
          {
             
            document.kqPlanInfoForm.elements[i].checked=true;
          }
        }
        checkflag = "true";
    }else
    {
        for (i=0;i<len;i++)
        {
          if (document.kqPlanInfoForm.elements[i].type=="checkbox")
          {
             
            document.kqPlanInfoForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    }      
  } 
  function noput()
  {
     kqPlanInfoForm.action="/kq/kqself/plan/searchone_noput.do?b_query=link&plan_id=${kqPlanInfoForm.plan_id}";
     kqPlanInfoForm.target="il_body";
     kqPlanInfoForm.submit();
  }
  function goback()
  {
     kqPlanInfoForm.action="/kq/kqself/plan/annual_plan_institute.do?b_query=link&table=q29";
     kqPlanInfoForm.target="il_body";
     kqPlanInfoForm.submit();
  }
</script>

<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="JavaScript">
function update()
   {
      
       alert("该计划已发布执行不可以修改！");
     
   }     
   function approve()
   {
       var len=document.kqPlanInfoForm.elements.length;
       var isCorrect=false;
       for (i=0;i<len;i++)
       {
           if (document.kqPlanInfoForm.elements[i].type=="checkbox")
            {
              if( document.kqPlanInfoForm.elements[i].checked==true)
                isCorrect=true;
            }
       }
       if(!isCorrect)
       {
          alert("请选择人员");
          return false;
       }

       var target_url;
       var winFeatures = ""; 
       target_url="/kq/kqself/plan/searchoneplan.do?b_approve=link";

       var returnValue = window.showModalDialog(target_url, '', "dialogWidth:450px; dialogHeight:300px;resizable:no;center:yes;scroll:no;status:no;scrollbars:no");
       if (returnValue) {
           var approveDate = getEncodeStr(returnValue[0]);
           var approveResult = getEncodeStr(returnValue[1]);
           var approveMemo = encodeURI(returnValue[2]);
           kqPlanInfoForm.action="/kq/kqself/plan/searchoneplan.do?b_transact=link&status=05&date="+approveDate+"&result="+approveResult+"&memo="+approveMemo;
           kqPlanInfoForm.submit();
       }
   }
   
   function change(){
      kqPlanInfoForm.action="/kq/kqself/plan/searchoneplan.do?b_search=link&select_flag=1";
      kqPlanInfoForm.submit();
   }
   
   function viewAll()
   {
       kqPlanInfoForm.action="/kq/kqself/plan/searchoneplan.do?b_search=link&select_flag=0";
       kqPlanInfoForm.submit();
   }   
   function excecuteExcel()
   {
	var hashvo=new ParameterSet();			
	hashvo.setValue("plan_id","${kqPlanInfoForm.plan_id}");	
	hashvo.setValue("select_name","${kqPlanInfoForm.select_name}");
	hashvo.setValue("select_pre","${kqPlanInfoForm.select_pre}");
	hashvo.setValue("a_code","${kqPlanInfoForm.a_code}");
	var In_paramters="exce=excel";	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showExcel,functionId:'15502110015'},hashvo);
   }	
   function showExcel(outparamters)
   {
    var url=outparamters.getValue("excelfile");	
	var win=open("/servlet/vfsservlet?fileid="+url+"&fromjavafolder=true","excel");
   }
 </script>   
<%
int i=0;
%>
<html:form action="/kq/kqself/plan/searchoneplan">
<table width="100%" border="0" cellspacing="0" cellpadding="0" >
<tr> 
  <td>
     <table width="30%" border="0" cellspacing="0"  align="left" cellpadding="0">
    <tr>
       <td>
         <html:select name="kqPlanInfoForm" property="select_pre" size="1" onchange="change();">
                <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
            </html:select>
       </td>
       <td align= "left" nowrap>
          &nbsp;姓名&nbsp;
           <input type="text" name="select_name" value="${kqPlanInfoForm.select_name}" class="inputtext" style="width:100px;font-size:10pt;text-align:left">
           <input type="button" name="btnreturn" value="查询" onclick="change();" class="mybutton">	
           <input type="button" name="btnreturn" value="输出Excel" onclick="excecuteExcel();" class="mybutton">	
       </td>       
    </tr>
</table>
  </td>
</tr>
<tr>
   <td>
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
  <thead>
    <tr>
      <td align="center" class="TableRow" nowrap>
		<input type="checkbox" name="aa" value="true" onclick="selAll()">&nbsp;
      </td> 
      <logic:iterate id="element" name="kqPlanInfoForm"  property="tlist" indexId="index">
         <logic:equal name="element" property="visible" value="true">
            <td align="center" class="TableRow" nowrap>
                <bean:write name="element" property="itemdesc" />&nbsp;
            </td>
         </logic:equal>    
      </logic:iterate>
      <td align="center" class="TableRow" nowrap>
        <bean:message key="label.edit"/>   
      </td>       
    </tr>  
  </thead>
<hrms:paginationdb id="element" name="kqPlanInfoForm" sql_str="kqPlanInfoForm.sql" table="" where_str="" columns="${kqPlanInfoForm.com}" page_id="pagination"  indexes="indexes">
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
               <hrms:checkmultibox name="kqPlanInfoForm" property="pagination.select"  value="true" indexes="indexes"/>&nbsp;
          </td>  
            <logic:iterate id="tlist" name="kqPlanInfoForm"  property="tlist" indexId="index">
             <logic:equal name="tlist" property="visible" value="true">
                  <logic:notEqual name="tlist" property="itemtype" value="D">
                     <td align="left" class="RecordRow" nowrap>
                        <logic:notEqual name="tlist" property="codesetid" value="0">
                           <hrms:codetoname codeid="${tlist.codesetid}" name="element" codevalue="${tlist.itemid}" codeitem="codeitem" scope="page"/>  	      
                           <bean:write name="codeitem" property="codename" />&nbsp;                    
                        </logic:notEqual>
                        <logic:equal name="tlist" property="codesetid" value="0">
                            <bean:write name="element" property="${tlist.itemid}" filter="false"/>&nbsp;                 
                        </logic:equal>                   
                     </td>
                    </logic:notEqual>
                    <logic:equal name="tlist" property="itemtype" value="D">
                       <td align="center" class="RecordRow" nowrap>
                           <bean:write name="element" property="${tlist.itemid}" filter="false"/>&nbsp;   
                       </td>
                    </logic:equal>    
            </logic:equal>    
          </logic:iterate>          
          <td align="center" class="RecordRow" nowrap>
             <hrms:priv func_id="0C3607">
               <logic:equal name="element" property="q31z5" value="01">
            	  <a href="/kq/kqself/plan/searchoneplan.do?b_view=link&apply_id=<bean:write name="element" property="q3101" filter="true"/>&dtable=q31"><img src="/images/edit.gif" border=0></a>
	       </logic:equal>
	       <logic:notEqual name="element" property="q31z5" value="01">
            	  <a href="###" onclick="update();"><img src="/images/edit.gif" border=0></a>
	        </logic:notEqual>
	     </hrms:priv>         
	  </td>  
         </tr>
    </hrms:paginationdb>
   </table>    
  </td>
</tr>
<tr>
  <td>
   <table  width="100%" align="center"  class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="pagination" property="current" filter="true" />
					页
					共
					<bean:write name="pagination" property="count" filter="true" />
					条
					共
			页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="kqPlanInfoForm" property="pagination" nameId="kqPlanInfoForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
    
  </table>
   </td>
</tr>
<tr>
  <td align="left"> 
   <table  width="100%" align="left">
          <tr>  
          
           <td align="left">
           <hrms:priv func_id="0C3605,2704405"> 
	               <input type="button" name="br_approve" value='<bean:message key="button.approve"/>' class="mybutton" onclick="approve();"> 
	   </hrms:priv> 
	    <input type="button" name="btnreturn" value="未提交计划人员" onclick="noput();" class="mybutton">	
        <input type="button" name="btnreturn" value="<bean:message key="button.return"/>" onclick="goback();" class="mybutton" >			
            
           </td>
          </tr>          
    </table>
  </td>
</tr>
</table>
</html:form>
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
  hide_nbase_select('select_pre');
</script>
				