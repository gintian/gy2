<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
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
   function change_print()
   {
   	document.mysearchform.submit();
      var returnURL = getEncodeStr("${dailyRegisterForm.returnURL}");
      //var condition = getEncodeStr("${dailyRegisterForm.condition}");
      document.location.href="/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=81&relatTableid=${dailyRegisterForm.relatTableid}&returnURL="+returnURL;//+"&condition="+condition;
      //dailyRegisterForm.submit();      
   }  
</SCRIPT>
<html:form action="/kq/register/daily_registerdata"> 
                <html:hidden name="dailyRegisterForm" property="returnURL" styleClass="text"/>
                <html:hidden name="dailyRegisterForm" property="condition" styleClass="text"/>         
</html:form>
<form name="mysearchform" action="/general/muster/hmuster/searchHroster.do?b_search=link" method="post" target="mysearchframe">
	<input type="hidden" name="condition" value="${dailyRegisterForm.condition}">
</form>
<iframe name="mysearchframe" style="display: none;"></iframe>
<script language="javascript">
  change_print();  
</script>
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