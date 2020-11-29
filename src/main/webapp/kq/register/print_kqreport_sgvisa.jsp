<%@ page contentType="text/html; charset=UTF-8"%>
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
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
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
<style>
.RecordRow_self {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	
}

.RecordRow_self_l{
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #CCCCCC 0pt dotted; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	
}
.RecordRow_self_r {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #CCCCCC 0pt dotted; 
	BORDER-TOP: #94B6E6 1pt solid;
	
}
.RecordRow_self_t {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #CCCCCC 0pt dotted;
	
}
.RecordRow_self_b {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #CCCCCC 0pt dotted; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	
}
.RecordRow_self_two {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #CCCCCC 0pt dotted; 
	BORDER-RIGHT: #CCCCCC 0pt dotted; 
	BORDER-TOP: #94B6E6 1pt solid;
	
}

.ListTable_self {

    BACKGROUND-COLOR: #F7FAFF;
    BORDER-BOTTOM: medium none; 
    BORDER-COLLAPSE: collapse; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none; 
    
}
.RecordRow_self_t_l {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #CCCCCC 0pt dotted; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	
}
.RecordRow_self_t_r {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	
}

.TEXT {
	BACKGROUND-COLOR:transparent;
	
	BORDER-BOTTOM: medium none; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
}
.BackText {
	background-color: #FFFFFF;
}
</style>
<hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<html:form action="/kq/register/print_kqreport_unittable">
<html:hidden name="printKqInfoForm" property="codeValue" styleClass="text" styleId="codeValue"/>
<script language=JavaScript>
	function pagepar()
	{
     printKqInfoForm.action="/kq/register/print_kqreport_unittable.do?b_par=link&userbaseunit=${printKqInfoForm.userbaseunit}&unita0100=${printKqInfoForm.unita0100}&username=" + $URL.encode("${printKqInfoForm.username}")+ "&report_unitid=${printKqInfoForm.report_unitid}&coursedate=${printKqInfoForm.coursedate}";
     printKqInfoForm.submit();
	}
	function go_back()
	{
		var codeValue = document.getElementById("codeValue").value;
	    printKqInfoForm.action="/kq/register/print_kqreport.do?b_search=link&b_search=link&code="+codeValue+"&kind=1";
        printKqInfoForm.submit();
	}
	function createPDF() {
		var hashvo=new ParameterSet();
	    hashvo.setValue("a0100","${printKqInfoForm.unita0100}");	
	    hashvo.setValue("nbase","${printKqInfoForm.userbaseunit}");
	    hashvo.setValue("username","${printKqInfoForm.username}");
	    hashvo.setValue("reportid","${printKqInfoForm.report_unitid}");
	    hashvo.setValue("coursedate","${printKqInfoForm.coursedate}");
	    hashvo.setValue("fileunit","2");
	    
	    var request=new Request({method:'post',asynchronous:false,onSuccess:showdownloadfile,functionId:'15301110080'},hashvo);
	}
	
	function createEXCEL() {
		var hashvo=new ParameterSet();
	    hashvo.setValue("a0100","${printKqInfoForm.unita0100}");	
	    hashvo.setValue("nbase","${printKqInfoForm.userbaseunit}");
	    hashvo.setValue("username","${printKqInfoForm.username}");
	    hashvo.setValue("reportid","${printKqInfoForm.report_unitid}");
	    hashvo.setValue("coursedate","${printKqInfoForm.coursedate}");
	    hashvo.setValue("fileunit","2");
	    var request=new Request({method:'post',asynchronous:false,onSuccess:showdownloadfile,functionId:'15301110081'},hashvo);
	} 
	
	function showdownloadfile(outparamters) {
		var filename=outparamters.getValue("filename");
		window.open("/servlet/vfsservlet?fileid=" + filename +"&fromjavafolder=true");
	}
</script>
      ${printKqInfoForm.tableUnitHtml}&nbsp;&nbsp;
      ${printKqInfoForm.turnUnitTableHtml}
      
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