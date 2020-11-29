<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
    String callBackFunc = "";
    if(request.getParameter("callBackFunc")!=null){
        callBackFunc = request.getParameter("callBackFunc");
    }
%>
<html>
  <head>
    
  </head>
  <script language="JavaScript" src="implement.js"></script>
  <script type="text/javascript">
      //选择考核等级
      function selectDegree()
      {
          var num=0;
          var a_value="";
          for(var i=0;i<document.implementForm.perDegree.options.length;i++)
          {
              if(document.implementForm.perDegree.options[i].selected)
              {
                  num++;
                  a_value=document.implementForm.perDegree.options[i].value;
              }
          }
          if(num==0||num>1)
          {
              alert(P_I_INF10+"!");
              return;
          }
          if (window.showModalDialog){
              parent.window.returnValue=a_value;
              parent.window.close();
          }else{
              <%if(callBackFunc.length()>0){%>
                if (window.top.opener && window.top.opener.<%=callBackFunc%>)
                    eval(window.top.opener.<%=callBackFunc%>)(a_value);
                 else if (parent.parent.<%=callBackFunc%>)
                    parent.parent.<%=callBackFunc%>(a_value);
              <%}%>
              window.parent.parent.close();
          }
      }
    function closeWin(){
        if(window.showModalDialog){
            parent.window.close();
        }else{
            if(parent.parent.Ext && parent.parent.Ext.getCmp("showGradeWin")){
                parent.parent.Ext.getCmp("showGradeWin").close()
            }
            window.parent.parent.close();

        }
    }
  </script>
  <body>
   <html:form action="/performance/implement/performanceImplement">
   <bean:message key="performance.implement.info5"/>
   <table >
   <tr><td>
   
   		<html:select name="implementForm" property="perDegree"   multiple="multiple"  size="1" style="height:180px;width:300px;font-size:9pt"   >
  	 		<html:optionsCollection property="perGradeSetList" value="dataValue" label="dataName"/>
		</html:select>
   
   </td><td valign='top' >
   		<table height='170px'   >
   		<tr height='50%' ><td valign='top' >
   		&nbsp;&nbsp;<input type="button" name="button1"  value="  <bean:message key="button.ok"/>  " class="mybutton" style="width:60px;" onclick="selectDegree()" >
   		<br><br>    
        &nbsp;&nbsp;<input type="button" name="button2"  value="  <bean:message key="button.cancel"/>  " class="mybutton" style="width:60px;" onclick="closeWin()" >
		<br><br>  
   		&nbsp; <input type="button" name="button2"  value="<bean:message key="performance.implement.degreeDefine"/>" class="mybutton" style="width:60px;padding:0px;" onclick="defineDegree('${implementForm.planid}','${implementForm.busitype}')" >    
   		</td></tr>
   		</table>
   
   </td></tr>
   </table>
   </html:form>
  </body>
</html>
