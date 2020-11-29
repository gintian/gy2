<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.hire.employActualize.EmployResumeForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.struts.valueobject.UserView,com.hrms.hjsj.utils.Sql_switcher,
				com.hrms.struts.constant.WebConstant,
				com.hrms.frame.codec.SafeCode" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">AxManager.writeCard();</script>
<script language="javascript" src="/hire/employActualize/employResume/employResume.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<%
	int dbtype=Sql_switcher.searchDbServer();
	String url_p=SystemConfig.getServerURL(request);
	int n=0;
	int y =0;
	String userName = null;
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	userName=userView.getUserName();
	String dataflag=SafeCode.encode("<CARDSTYLE>A</CARDSTYLE><SUPER_USER>1</SUPER_USER>"); // 转码加密; SUPER_USER: 不限制用户管理范
%>
<hrms:themes></hrms:themes>
<html:form action="/hire/employActualize/employResume">
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
<table width="590px" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style=position:absolute;top:5px;">
 <hrms:extenditerate id="element" name="employResumeForm"  property="cardlistform.list" pagination="cardlistform.pagination" indexes="indexes"   pageCount="15" scope="session">
 
          <tr class="RecordRow">
          <td align="left" class="RecordRow"  width="250">
          <logic:equal value="-1" name="element" property="tableId">
   		  <span style="color:red">*请在配置参数中设置预览简历登记表！</span>
   		   <logic:equal value="0" name="indexes" >
   		  <% y++;   %>
   		   </logic:equal>
   		 </logic:equal>
   		  <% if(y==1){ %>
   		  
   		  <logic:notEqual value="-1" name="element" property="tableId">
   		 <logic:equal value="1" name="indexes" >
   		 <input type="radio" checked name="tabid" value="<bean:write name="element" property="tableId"/>" />
   		 </logic:equal>
   		 <logic:notEqual value="1" name="indexes" >
   		 <input type="radio"  name="tabid" value="<bean:write name="element" property="tableId"/>" />
   		  </logic:notEqual>
   		<input type="hidden" name="tagStr" value="<bean:write name="element" property="tagStr"/>"/>
   		<bean:write name="element" property="rr" filter="true"/>
   		 </logic:notEqual>  
   		   <% }else{ %>
   		 
   		 <logic:notEqual value="-1" name="element" property="tableId">
   		 <logic:equal value="0" name="indexes" >
   		 <input type="radio" checked name="tabid" value="<bean:write name="element" property="tableId"/>" />
   		 </logic:equal>
   		 <logic:notEqual value="0" name="indexes" >
   		 <input type="radio"  name="tabid" value="<bean:write name="element" property="tableId"/>" />
   		  </logic:notEqual>
   		<input type="hidden" name="tagStr" value="<bean:write name="element" property="tagStr"/>"/>
   		<bean:write name="element" property="rr" filter="true"/>
   		 </logic:notEqual>
   		   <% }%>
	     </td> 
	      </tr> 
	        <tr class="RecordRow">      
            <td align="left" class="RecordRow">
            <bean:write  name="element" property="a0101s" filter="true"/>
            </td>  	 	    	            
          </tr>
        </hrms:extenditerate>
        <tr>
        <td align="center" style="padding-top: 5px;">
            <input type='button' value='打印' class='mybutton' onclick='printCard();'> 
        </td>
        </tr>
        </table>
         
<script language="javascript">
function printCard()
{
  var obj = document.getElementById('CardPreview1');  
 obj.SetDataFlag("<%=dataflag%>");
 obj.ClearObjs();  
  if(obj==null)
  {
      alert("没有下载打印控件，请设置IE重新下载！");
      return false;
  }
    var arr=document.getElementsByName("tabid");
    var tagStr=document.getElementsByName("tagStr");
    for(var i=0;i<arr.length;i++){  
      if(arr[i].checked)
      { 
           obj.SetCardID(arr[i].value);
           var arr2= tagStr[i].value.split("`");
            for(var j=0;j<arr2.length;j++)
            {
              if(arr2[j].value!='')
              {
                obj.AddObjId(arr2[j]);
              } 
            }
      }
    }
  try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
  obj.ShowCardModal();
  
}
function initCard()
{     
      var arr=document.getElementsByName("tabid");
      if(arr=="#")
   		{
   			alert(PLASE_SELECT_RECORD+"！");
   			return;
   		} 
      var rl = document.getElementById("hostname").href; 
      var aurl=rl;//tomcat路径
      var DBType="<%=dbtype%>";
      var obj = document.getElementById('CardPreview1');
      var UserName="<%=userName%>";
      obj.SetUserName(UserName);
      obj.SetURL(aurl);
      obj.SetDBType(DBType);
}
</script>
 <script language="javascript">
 initCard();
 
</script>
</html:form>
