<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
	
	String browser = "MSIE";
	String agent = request.getHeader("user-agent").toLowerCase(); 
	if(agent.indexOf("firefox")!=-1)
		browser="Firefox";
	else if(agent.indexOf("chrome")!=-1)
		browser="Chrome";
	else if(agent.indexOf("safari")!=-1)
		browser="Safari";
%>
<script LANGUAGE=javascript>
  function changTabid()
  {
     this.document.cardTagParamForm.pageid.value=0;
     cardTagParamForm.action="/hire/jp_contest/personinfo/jpcard.do";
     cardTagParamForm.submit();
  }
  function excecutePDF()
  {
        var tab_id="${cardTagParamForm.tabid}";        
        if(tab_id==null||tab_id.length<=0)
        {
           alert(CHOICE_ROLL_CALL+"！");
           return false;
        }
        var hashvo=new ParameterSet();
        hashvo.setValue("nid","${cardTagParamForm.a0100}");
        hashvo.setValue("cardid","${cardTagParamForm.tabid}");
        if(${cardTagParamForm.cardparam.queryflagtype}==1)
        {
           hashvo.setValue("cyear","${cardTagParamForm.cardparam.cyear}");
        }else if(${cardTagParamForm.cardparam.queryflagtype}==3)
        {
           hashvo.setValue("cyear","${cardTagParamForm.cardparam.csyear}");
        }else if(${cardTagParamForm.cardparam.queryflagtype}==4)
        {
           hashvo.setValue("cyear","${cardTagParamForm.cardparam.csyear}");
        }        
        hashvo.setValue("userpriv","${cardTagParamForm.userpriv}");
        hashvo.setValue("istype","1");        
        hashvo.setValue("cmonth","${cardTagParamForm.cardparam.cmonth}");
        hashvo.setValue("season","${cardTagParamForm.cardparam.season}");
        hashvo.setValue("ctimes","${cardTagParamForm.cardparam.ctimes}");
        hashvo.setValue("cdatestart","${cardTagParamForm.cardparam.cdatestart}");
	hashvo.setValue("cdateend","${cardTagParamForm.cardparam.cdateend}");
	hashvo.setValue("infokind","${cardTagParamForm.inforkind}");
	hashvo.setValue("querytype","${cardTagParamForm.cardparam.queryflagtype}");
	<logic:equal name="cardTagParamForm" property="inforkind" value="1">
	   hashvo.setValue("userbase","${cardTagParamForm.userbase}");
        </logic:equal>
        <logic:notEqual name="cardTagParamForm" property="inforkind" value="1">
	   hashvo.setValue("userbase","BK");
        </logic:notEqual>
    var In_paramters="exce=PDF";  
    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showPDF,functionId:'07020100005'},hashvo);
  }
function showPDF(outparamters)
{
 
    var url=outparamters.getValue("url");
    url = decode(url);
    var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url);
}
</script> 
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<hrms:themes></hrms:themes>
<html:form action="/hire/jp_contest/personinfo/jpcard">
   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top">
		</td>
	 </tr>          
         <tr>        
           <td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
             <html:hidden property="multi_cards"/>       
           </td>
         </tr>            
   </table> 
  <div id="card">
    <table>
       <tr>
         <td>
            <hrms:ykcard name="cardTagParamForm" property="cardparam"  nid="${cardTagParamForm.a0100}" tabid="${cardTagParamForm.tabid}" infokind="${cardTagParamForm.inforkind}" cardtype="jpcard" disting_pt="javascript:screen.width" userpriv="${cardTagParamForm.userpriv}"  havepriv="1" queryflag="0" istype="3"  browser="<%=browser %>"/>
         </td>
       </tr>
    </table>
   </div> 

</html:form>
