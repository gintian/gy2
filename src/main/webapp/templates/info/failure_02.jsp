<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import='com.hrms.struts.utility.JSMessage' %>
<%
	JSMessage test = new JSMessage(pageContext);
	String msg=test.getMessage();
	//控制按钮  0不显示按钮 |1关闭|默认为返回  xuj add 2013-7-18
	String targetWindow = (String)request.getAttribute("targetWindow");
	//szk 不用back而返回指定的路径
	String formpath = (String)request.getAttribute("formpath");
	
	if(msg != null && msg.length()>0 &&(msg.indexOf("MRMapping.xml") > -1 || msg.indexOf("WFMapping.xml") > -1))
	    msg = "页面不存在";
	
	if(msg.contains("description:"))
		msg = msg.substring(msg.lastIndexOf("description:")+12);
%> 
<script language="javascript">
function back()
{
	var path = "<%= formpath%>";
	if(path == "null" || path == null||path=="")
	{
		window.history.back();
	}
	else{
		window.location.href = path;
	}
}

</script>

<hrms:themes></hrms:themes>

  <table width="310" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable"  style="margin-top:10px;">
          <tr>
       		<td align="left" class="TableRow">&nbsp;<bean:message key="label.information"/>&nbsp;</td>
          </tr> 
                    <tr >
              	      <td align="left" valign="middle" nowrap style="height:120"><%=msg%></td>
                    </tr> 

                    <tr >
                      <td align="center" style="height:35">
                      <%if("0".equals(targetWindow)){ %>
                      
                      <%}else if("1".equals(targetWindow)){ %>
                   <!--     <input type="button" name="btnreturn" value="关闭" onclick="top.close();" class="mybutton"> -->
                      <%}else{ %>
              				<input type="button" name="btnreturn" value="返回" onclick="back()" class="mybutton">
                      <%} %>
                      </td>
                    </tr>   
          
  </table> 

