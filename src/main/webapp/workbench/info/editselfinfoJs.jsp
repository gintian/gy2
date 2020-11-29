<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.actionform.selfinfomation.SelfInfoForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
String userName = null;
String css_url = "/css/css1.css";
UserView userView = (UserView) session.getAttribute(WebConstant.userView);
String codevalue="";String code="";String isAll="";
if (userView != null) {
css_url = userView.getCssurl();
if (css_url == null || css_url.equals(""))
    css_url = "/css/css1.css";
if(!userView.isSuper_admin()){ 
    code=userView.getManagePrivCode();
    codevalue=userView.getManagePrivCodeValue();
    if("UN".equalsIgnoreCase(code)&&(codevalue==null||codevalue.length()==0)){
        isAll="all";
    }
  }else{
    isAll="all";
  } 
}
String bosflag="";
if(userView!=null){
    bosflag = userView.getBosflag();
}
%>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
<script type="text/javascript" src="/js/dict.js"></script>
<script type="text/javascript" src="/js/popcalendar.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
</head>
<%String orgtemp = "";String orgtempview = "";String postemp = "";String postempview = "";String kktemp = "";
String kktempview = "";String birthdayfield = "";String agefield = "";
String workagefield = "";String postagefield = "";String axfield = "";String axviewfield = "";int rowss;
%>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
var date_desc;
var code_value="<%=codevalue%>";  
var isAall = "<%=isAll %>"; 
  function validate(){
     var item = document.getElementById("a0101");
      if(item!=null){
          check();
      }
    var tag=true;    
     <logic:iterate  id="element"    name="selfInfoForm"  property="infoFieldList" indexId="index"> 
      <logic:equal name="element" property="visible" value="true">   
            <bean:define id="fl" name="element" property="fillable"/>
            <bean:define id="desc" name="element" property="itemdesc"/>
            <bean:define id="itemid" name="element" property="itemid"/>
             var valueInputs=document.getElementsByName("<%="infoFieldList["+index+"].value"%>");
              var dobj=valueInputs[0];
            <%
                if("2".equalsIgnoreCase(userView.analyseFieldPriv(itemid.toString()))) {
            %>
              if("${fl}"=="true"&&dobj.value.length<1){
                alert("${desc}"+"必须填写！");
                return ;
              }
              <%}%>
          <logic:equal name="element" property="itemid" value="b0110">
              <%orgtemp="infoFieldList["+index+"].value";%>  
              <%orgtempview="infoFieldList["+index+"].viewvalue";%>         
          </logic:equal>
          <logic:equal name="element" property="itemid" value="e0122">
                 <%postemp="infoFieldList["+index+"].value";%>
                  <%postempview="infoFieldList["+index+"].viewvalue";%>
          </logic:equal>
          <logic:equal name="element" property="itemid" value="e01a1">
                 <%kktemp="infoFieldList["+index+"].value";%>
                  <%kktempview="infoFieldList["+index+"].viewvalue";%>
          </logic:equal>
          <logic:equal name="element" property="itemid" value="${selfInfoForm.birthdayfield}">
                  <%birthdayfield="infoFieldList["+index+"].value";%>
             </logic:equal>
           <logic:equal name="element" property="itemid" value="${selfInfoForm.agefield}">
                  <%agefield="infoFieldList["+index+"].value";%>
             </logic:equal>
               <logic:equal name="element" property="itemid" value="${selfInfoForm.workagefield}">
                  <%workagefield="infoFieldList["+index+"].value";%>
             </logic:equal>
               <logic:equal name="element" property="itemid" value="${selfInfoForm.postagefield}">
                  <%postagefield="infoFieldList["+index+"].value";%>
             </logic:equal>
             <logic:equal name="element" property="itemid" value="${selfInfoForm.axfield}">
                  <%axfield="infoFieldList["+index+"].value";%>
                 <%axviewfield="infoFieldList["+index+"].viewvalue";%>
             </logic:equal>
        <logic:equal name="element" property="itemtype" value="D">   
         var d_value=dobj.value;
         d_value=replaceAll(d_value,"-",".");
          tag= validateDate(dobj,d_value) && tag;      
      if(tag==false)
      {
        dobj.focus();
        return false;
      }
        </logic:equal> 
        <logic:equal name="element" property="itemtype" value="N"> 
           <logic:lessThan name="element" property="decimalwidth" value="1"> 
             var valueInputs=document.getElementsByName("<%="infoFieldList["+index+"].value"%>");
             var dobj=valueInputs[0];
              tag=checkNUM1(dobj) &&  tag ;  
          if(tag==false)
          {
            dobj.focus();
            return false;
          }
        </logic:lessThan>
        <logic:greaterThan name="element" property="decimalwidth" value="0"> 
         var valueInputs=document.getElementsByName('<%="infoFieldList["+index+"].value"%>');
             var dobj=valueInputs[0];
             tag=checkNUM2(dobj,${element.itemlength},${element.decimalwidth}) &&  tag ;  
              if(tag==false)
          {
            dobj.focus();
            return false;
          }
        </logic:greaterThan>
       </logic:equal>  
       <logic:equal name="element" property="itemtype" value="A"> 
       	<logic:equal name="element" property="codesetid" value="0"> 
	       var itemValue=document.getElementsByName("<%="infoFieldList["+index+"].value"%>")[0].value;
	       var itemlength = ${element.itemlength}
	       if(IsOverStrLength(itemValue, itemlength)){
	    	   var msg = ITEMVALUE_MORE_LENGTH;
	    	   msg = msg.replace('{0}', itemlength).replace('{1}', itemlength/2);
	    	   alert("${desc}"+msg);
		       return false;
	       }
       	</logic:equal>  
       </logic:equal>  
      </logic:equal>  
      check(document.getElementsByName('<%="infoFieldList["+index+"].value"%>')[0]);
     </logic:iterate>    
     return tag;
  }
var orgtemp="<%=orgtemp%>";
var orgtempview="<%=orgtempview%>";
var postemp="<%=postemp%>";
var postempview="<%=postempview%>";
var kktemp="<%=kktemp%>";
var kktempview="<%=kktempview%>";
var returnValue = "${selfInfoForm.returnvalue}";
var uplevel = "${selfInfoForm.uplevel}";
var nbase = "${selfInfoForm.userbase}";
var birthdayfield = "<%=birthdayfield%>";
var agefield = "<%=agefield%>";
var axfield = "<%=axfield%>";
var axviewfield = "<%=axviewfield%>";
var workagefield = "<%=workagefield%>";
var postagefield = "<%=postagefield%>";
var startpostfield = "${selfInfoForm.startpostfield}";
var workdatefield = "${selfInfoForm.workdatefield}";
</script>
<body>
</body>

