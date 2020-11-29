<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.ykcard.CardTagParamForm" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String userFullName=null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);	
	String fields="";
	String tables="";	
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");
     fields=userView.getFieldpriv().toString();
	 tables=userView.getTablepriv().toString();
	 userName=userView.getUserName(); 
	 userFullName=userView.getUserFullName();	 
	}
	String superUser="0";
	if(userView.isSuper_admin())
	  superUser="1";
	else
    {
       if(fields==null||fields.length()<=0)
	       fields=",";
	   if(tables==null||tables.length()<=0)
	       tables=","; 
	}  
	EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
    String license=lockclient.getLicenseCount();
    int version=userView.getVersion();
    if(license.equals("0"))
        version=100+version;
    int usedday=lockclient.getUseddays();
%>
<%
 String url_p=SystemConfig.getCsClientServerURL(request);
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes></hrms:themes>
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
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
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


function getPersonlist(outparamters)
{
  var personlist=outparamters.getValue("personlist");
  var obj = document.getElementById('CardPreview1');  
  if(obj==null)
  {
      alert("没有下载打印控件，请设置IE重新下载！");
      return false;
  }
  obj.SetCardID("${cardTagParamForm.tabid}");
  obj.SetDataFlag("${cardTagParamForm.inforkind}");
  obj.SetNBASE("${cardTagParamForm.userbase}");
  obj.ClearObjs();
  for(var i=0;i<personlist.length;i++)
  {
    obj.AddObjId(personlist[i].dataName);
  }
  obj.ShowCard();
}
function showCard()
{
   var hashvo=new ParameterSet();
   hashvo.setValue("dbname","${cardTagParamForm.userbase}");
   hashvo.setValue("inforkind","${cardTagParamForm.inforkind}");
   var request=new Request({method:'post',onSuccess:getPersonlist,functionId:'07020100077'},hashvo);
}
  function initCard()
  {
      var aurl="<%=url_p%>";
      var DBType="${cardTagParamForm.dbType}";
      var UserName="<%=userName%>";
      var obj = document.getElementById('CardPreview1');   
      if(obj==null)
      {
         return false;
      }
      var superUser="<%=superUser%>";
      var menuPriv="<%=fields%>";
      var tablePriv="<%=tables%>";
      obj.SetSuperUser(superUser);
      obj.SetUserMenuPriv(menuPriv);
      obj.SetUserTablePriv(tablePriv); 
      obj.SetURL(aurl);
      obj.SetDBType(DBType);
      obj.SetUserName(UserName);
      obj.SetUserFullName("<%=userFullName%>");
      obj.SetHrpVersion("<%=version%>");
      obj.SetTrialDays("<%=usedday%>","30");
  }
  var scrheigh=window.screen.availHeight;  //屏幕可用工作区高度 
  var scewidth=window.screen.availWidth; //屏幕可用工作区宽度   
  scewidth=scewidth-20;
</script>
<html:form action="/general/card/searchcard">
<table>
  <tr>
   <td align="left">       
<script language="javascript">AxManager.writeCard();</script>  
   </td>  
   </tr>  
</table>
    <script language="javascript">
      initCard();
      showCard();
    </script>
</html:form>
