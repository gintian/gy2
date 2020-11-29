<%@ page contentType="text/html; charset=UTF-8"%>
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
<% int i=0;%>
<script language="javascript">
  function changes()
  {
      baseNetSignInForm.action="/kq/kqself/net_signin/allnet_signin_data.do?b_search=link&select_flag=1";
      baseNetSignInForm.submit();
  }
  function approve()
  {
      var len=document.baseNetSignInForm.elements.length;
      var isCorrect=false;
      for (var i=0;i<len;i++)
      {
         if (document.baseNetSignInForm.elements[i].type=="checkbox"&&document.baseNetSignInForm.elements[i].checked==true)
            isCorrect=true;
      }
      if(!isCorrect)
      {
         alert("请选择记录!");
         return false;
      }else
      {
        if(confirm("是要批准选择的补签数据吗?"))
        {
           baseNetSignInForm.action="/kq/kqself/net_signin/allnet_signin_data.do?b_approve=link";
           baseNetSignInForm.submit();
        }
      }      
  }
  function clearselect()
  {
      baseNetSignInForm.action="/kq/kqself/net_signin/allnet_signin_data.do?b_search=link&select_flag=0";
      baseNetSignInForm.submit();
  }

</script>
<html:form action="/kq/kqself/net_signin/allnet_signin_data">

<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
  <thead>
  	<tr class="RecordRow" style="background-color:white;">
      <td align="left"  nowrap colspan="12">  
      <html:select name="baseNetSignInForm" property="location" size="1" onchange="changes();">
                <html:optionsCollection property="locatlist" value="dataValue" label="dataName"/>	        
            </html:select>
			<bean:message key="label.from"/>
   	  	 	<input type="text" name="start_date" value="${baseNetSignInForm.start_date}" extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor1"  dropDown="dropDownDate">
   	  	 	<bean:message key="label.to"/>
   	  	 	<input type="text" name="end_date"  value="${baseNetSignInForm.end_date}" extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor2"  dropDown="dropDownDate">
                
             姓名
         <input type="text" name="select_name" value="${baseNetSignInForm.select_name}" class="inputtext" style="width:100px;font-size:10pt;text-align:left">			
            &nbsp;<input type="button" name="br_return" value='<bean:message key="button.query"/>' class="mybutton" onclick="changes();"> 
           <!-- <logic:equal name="baseNetSignInForm" property="select_flag" value="1">
              <input type="button" name="br_return" value='全显' class="mybutton" onclick="clearselect();">          
         </logic:equal>-->
      </td>      	 
  	</tr>
    <tr class="TableRow">
     <td align="center" class="TableRow" nowrap>
		<input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
     </td> 
         <logic:iterate id="element"    name="baseNetSignInForm"  property="fieldlist" indexId="index"> 
               <logic:equal name="element" property="visible" value="true">
                 <td align="center" class="TableRow" nowrap>
                  <bean:write  name="element" property="itemdesc"/>&nbsp; 
                 </td>
               </logic:equal>
           </logic:iterate> 
      
    </tr>  
  </thead>
  <hrms:paginationdb id="element" name="baseNetSignInForm" sql_str="baseNetSignInForm.sql_str" table="" where_str="baseNetSignInForm.where_str" columns="${baseNetSignInForm.column_str}" order_by="${baseNetSignInForm.order_str}" page_id="pagination"  indexes="indexes">
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
                <hrms:checkmultibox name="baseNetSignInForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
           </td>  
           <logic:iterate id="info" name="baseNetSignInForm"  property="fieldlist" indexId="index">  
             <logic:equal name="info" property="visible" value="true">
              <td align="left" class="RecordRow" nowrap>
               <logic:notEqual name="info" property="codesetid" value="0">
                     <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                     &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                              
                          
                </logic:notEqual>
                <logic:equal name="info" property="codesetid" value="0">                
                   &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
                 </logic:equal>
                 </td>  
             </logic:equal>             
            </logic:iterate>         
       </tr>
      </hrms:paginationdb>
      <tr>
      		<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="pagination" property="current" filter="true" />
					页
					共
					<bean:write name="pagination" property="count" filter="true" />
					条
					共
					<bean:write name="pagination" property="pages" filter="true" />
					页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="baseNetSignInForm" property="pagination" nameId="baseNetSignInForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
    
</table>
      </tr>
</table>
   <table  width="80%" align="left">
		<tr>
		    <td align="center">	
		        <hrms:priv func_id="0B403"> 	        
                        <input type="button" name="br_return" value='批准' class="mybutton" onclick="approve();"> 
		        </hrms:priv>
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
</script>


