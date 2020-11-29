<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
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

<hrms:themes /> <!-- 7.0css -->
<script language="javascript">

   function change()
   {
      kqShiftForm.action="/kq/kqself/class/kq_class.do?b_query=link";
      kqShiftForm.submit();
   }   
   function editClass(nbase,a_code,days)
   {
     
   } 
</script>
<html:form action="/kq/kqself/class/kq_class">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
 <tr>
  <td>
   <table width="100%" border="0" cellspacing="1"  align="left" cellpadding="1">
    
    <tr> 
    <td align= "left" nowrap>&nbsp;&nbsp;
        <bean:message key="kq.register.daily.menu"/>&nbsp;&nbsp;
        <html:select name="kqShiftForm" property="session_data" size="0" onchange="javascript:change();">
        <html:optionsCollection property="sessionlist" value="dataValue" label="dataName"/>
        </html:select> 
        <html:hidden name="kqShiftForm" property="a_code" styleClass="text"/> 
         <html:hidden name="kqShiftForm" property="nbase" styleClass="text"/>
      </td> 
    </tr>
    </table>
  </td>
 </tr>
 <tr>
  <td width="100%">
      ${kqShiftForm.table_html}
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
