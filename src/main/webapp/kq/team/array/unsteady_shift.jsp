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
<script language="JavaScript" src="/js/popcalendar.js"></script>
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
<script language="javascript">
  function goback()
  {
     unsteadyKqShtifForm.action="/kq/team/array/unsteady_shift_data.do?br_return=link";
     unsteadyKqShtifForm.target="il_body";
     unsteadyKqShtifForm.submit();
  }
  function addShiftClass(c_str,k_str)
  {
      if(c_str.length<=0||c_str=="")
      {
        alert("请选择单位、部门或岗位！");
      }else
      {
         var theurl="/kq/team/array/normal_array_data_add.do?b_query=link&flag=1";
         var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
         var return_vo= window.showModalDialog(iframe_url,0, 
          "dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
         if(return_vo!=null&&return_vo.length>0)
         {
            var hashvo=new ParameterSet();            
	    hashvo.setValue("addclass",return_vo);	
	    hashvo.setValue("code",c_str);	
	    hashvo.setValue("kind",k_str);		 
            var request=new Request({method:'post',asynchronous:true,onSuccess:addSetLeftList,functionId:'15221200203'},hashvo);
         }
      }
 }
    function addSetLeftList(outparamters)
    {
       var save_flag=outparamters.getValue("flag"); 
       if(save_flag=="true")
       {
          unsteadyKqShtifForm.action="/kq/team/array/unsteady_shift_data.do?b_search=link&code=${unsteadyKqShtifForm.code}&kind=${unsteadyKqShtifForm.kind}";
          unsteadyKqShtifForm.submit();
       }else
       {
         alert("添加人员失败！");
       }   
    }
    function submitDEL()
    {
        var sb = false;
        var len = document.unsteadyKqShtifForm.elements.length;
        for(var i=0;i<len;i++){
        	if (document.unsteadyKqShtifForm.elements[i].type=="checkbox"){
				if (document.unsteadyKqShtifForm.elements[i].checked==true && document.unsteadyKqShtifForm.elements[i].name != "aa")
					sb = true;
    		}
        }
        if(sb){
	        if(confirm("是否删除选择的记录？")){
	      	    unsteadyKqShtifForm.action="/kq/team/array/unsteady_shift_data.do?b_delete=link&code=${unsteadyKqShtifForm.code}&kind=${unsteadyKqShtifForm.kind}";
	      	    unsteadyKqShtifForm.submit();
	        }
        }else{
			alert("请选择需要删除的记录！");
        }
    }
   var checkflag = "false";
   function selAll()
   {
      var len=document.unsteadyKqShtifForm.elements.length;
       var i;
     if(checkflag=="false")
     {
        for (i=0;i<len;i++)
        {
           if (document.unsteadyKqShtifForm.elements[i].type=="checkbox")
           {
              document.unsteadyKqShtifForm.elements[i].checked=true;
           }
        }
        checkflag="true";
     }else
     {
        for (i=0;i<len;i++)
        {
          if (document.unsteadyKqShtifForm.elements[i].type=="checkbox")
          {
            document.unsteadyKqShtifForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    }   
  } 
</script>
<%
	int i=0;
%>
<html:form action="/kq/team/array/unsteady_shift_data">
<table border="0" cellspacing="0"  align="left" cellpadding="0" width="100%" >
 <tr>
   <td align="left" valign="top" height="24">   
      &nbsp;<bean:message key="kq.shift.unsteady.explain"/>
      <html:hidden name="unsteadyKqShtifForm" property="code"/>     
       <html:hidden name="unsteadyKqShtifForm" property="kind"/>     
    <td>
 </td>
 <tr>
   <td width="100%">
      <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	     <thead>
              <tr>      
               <td align="center" class="TableRow" nowrap>
		<input type="checkbox" name="aa" value="true" onclick="selAll()">&nbsp;
               </td>         	    
               <td align="center" class="TableRow" nowrap>
                    <bean:message key="kq.shift.unsteady.classname"/>&nbsp;
               </td>
               <td align="center" class="TableRow" nowrap>
                    <bean:message key="kq.shift.unsteady.one.up"/>&nbsp;
               </td>
               <td align="center" class="TableRow" nowrap>
		<bean:message key="kq.shift.unsteady.one.down"/>&nbsp;
               </td> 
                <td align="center" class="TableRow" nowrap>
                    <bean:message key="kq.shift.unsteady.tow.up"/>&nbsp;
               </td>
               <td align="center" class="TableRow" nowrap>
		<bean:message key="kq.shift.unsteady.tow.down"/>&nbsp;
               </td> 
               <td align="center" class="TableRow" nowrap>
                    <bean:message key="kq.shift.unsteady.three.up"/>&nbsp;
               </td>
               <td align="center" class="TableRow" nowrap>
		<bean:message key="kq.shift.unsteady.three.down"/>&nbsp;
               </td> 
               <td align="center" class="TableRow" nowrap>
                    <bean:message key="kq.shift.unsteady.four.up"/>&nbsp;
               </td>
               <td align="center" class="TableRow" nowrap>
		<bean:message key="kq.shift.unsteady.four.down"/>&nbsp;
               </td>               
               </tr>
   	     </thead>
   	   <hrms:extenditerate id="element" name="unsteadyKqShtifForm" property="recordListForm.list" indexes="indexes"  pagination="recordListForm.pagination" pageCount="20" scope="session">
	     <%
               if(i%2==0){ 
             %>
             <tr class="trShallow">
             <%
               }else{
             %>
             <tr class="trDeep">
             <%}
             %>
              <td align="center" class="RecordRow" nowrap>
               <hrms:checkmultibox name="unsteadyKqShtifForm" property="recordListForm.select" value="true" indexes="indexes"/>&nbsp;
              </td>  
              <td align="left" class="RecordRow" nowrap>              
                   <bean:write  name="element" property="string(name)" filter="true"/>&nbsp;
              </td> 
              <td align="left" class="RecordRow" nowrap>              
                   <bean:write  name="element" property="string(onduty_1)" filter="true"/>&nbsp;
              </td> 
               <td align="left" class="RecordRow" nowrap>              
                   <bean:write  name="element" property="string(offduty_1)" filter="true"/>&nbsp;
              </td>
              <td align="left" class="RecordRow" nowrap>              
                   <bean:write  name="element" property="string(onduty_2)" filter="true"/>&nbsp;
              </td> 
               <td align="left" class="RecordRow" nowrap>              
                   <bean:write  name="element" property="string(offduty_2)" filter="true"/>&nbsp;
              </td> 
              <td align="left" class="RecordRow" nowrap>              
                   <bean:write  name="element" property="string(onduty_3)" filter="true"/>&nbsp;
              </td> 
               <td align="left" class="RecordRow" nowrap>              
                   <bean:write  name="element" property="string(offduty_3)" filter="true"/>&nbsp;
              </td> 
              <td align="left" class="RecordRow" nowrap>              
                   <bean:write  name="element" property="string(onduty_4)" filter="true"/>&nbsp;
              </td> 
               <td align="left" class="RecordRow" nowrap>              
                   <bean:write  name="element" property="string(offduty_4)" filter="true"/>&nbsp;
              </td>  
             <%i++;%>  
	    </tr>	     
         </hrms:extenditerate>
      </table>
   </td>
  </tr>
  <tr>
    <td>
      <table width="100%" class="RecordRowP" align="center">
      <tr>
       <td valign="bottom" class="tdFontcolor">第
          <bean:write name="unsteadyKqShtifForm" property="recordListForm.pagination.current" filter="true" />
          页
          共
          <bean:write name="unsteadyKqShtifForm" property="recordListForm.pagination.count" filter="true" />
          条
          共
          <bean:write name="unsteadyKqShtifForm" property="recordListForm.pagination.pages" filter="true" />
          页
       </td>
       <td  align="right" nowrap class="tdFontcolor">
          <p align="right">
           <hrms:paginationlink name="unsteadyKqShtifForm" property="recordListForm.pagination"
                   nameId="recordListForm">
           </hrms:paginationlink>
       </td>
      </tr>
    </table>  
    </td>
  </tr>
  <tr>
 <td align="left" style="height:35px;">
     <input type="button" name="tt" value="<bean:message key="button.insert"/>"  class="mybutton" onclick="addShiftClass('${unsteadyKqShtifForm.code}','${unsteadyKqShtifForm.kind}');">
       <input type="button" name="tdf" value="<bean:message key="button.delete"/>"  class="mybutton" onclick="submitDEL();">
     <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="goback();" class="mybutton">
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