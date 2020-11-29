<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.performance.implement.ImplementForm" %>

<%
	ImplementForm implementForm=(ImplementForm)session.getAttribute("implementForm");	
	String  noApproveTargetCanScore=implementForm.getNoApproveTargetCanScore();
	String callBackFunc = "";
	if(request.getParameter("callBackFunc")!=null){
        callBackFunc = request.getParameter("callBackFunc");
    }
%>
<html>
  <head>
  <title><bean:message key="jx.eval.notCalculate"/></title>
   
  </head>
  <script language='javascript'>

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
              returnValue=a_value;
              window.close();
          }else{
              <%if(callBackFunc.length()>0){%>
                  eval(parent.window.opener.<%=callBackFunc%>)(a_value);
              <%}%>
              parent.window.opener.close();
          }
      }
  	var info=window.dialogArguments||window.parent.parent.dialogArguments||parent.window.opener.dialogArguments;
  	function enterButton()
  	{
  		if(info[2]!=null && info[2]=='true' && document.getElementById('noApproveTargetCanScore').checked==false)  		
  			document.getElementById("buttonOk").disabled=true;  		
  		else
  			document.getElementById("buttonOk").disabled=false;
  	}  	
  	function enter(theflag)
  	{
  		var thevo=new Object();
		thevo.ok=theflag;
		thevo.noApproveTargetCanScore=document.getElementById('noApproveTargetCanScore').checked;
		if (window.showModalDialog){
            parent.window.returnValue=thevo;
            parent.window.close();
        }else{
            <%if(callBackFunc.length()>0){%>
                if (theflag!=0)
                 eval(parent.window.opener.<%=callBackFunc%>)(thevo);
            <%}%>
            window.parent.parent.close();
        }
  	}

  </script>
   
<hrms:themes />
  <body>
  <form name='form1'>
    <table>
    <tr><td colspan='3'>
    <script language='javascript' >
    	document.write(info[1]);
    </script>
    </td></tr>
    <tr>
    <td> 
    	<TEXTAREA name='cause' style="width:470px;height:300px;"> </TEXTAREA>
    </td>
    </tr>
    <tr>
    <tr>
    <td> 
    	<input type="checkbox" id='noApproveTargetCanScore' onclick='enterButton(this)' <%if(noApproveTargetCanScore.equalsIgnoreCase("true")) {%>checked<%} %>/><bean:message key="jx.param.noApproveTargetCanScore"/>
    </td>
    </tr>
    <td align='center' >
    
     <input type='button' id='buttonOk' class="mybutton"   onclick='enter(1)' value='<bean:message key="lable.tz_template.enter"/>'  />
 	 <input type='button' class="mybutton"   onclick='enter(0)' value='<bean:message key="lable.tz_template.cancel"/>'  />
    </td>
    </tr>
    
    </table>
  </form>
  
  
  <script language='javascript' >
  		document.form1.cause.value=info[0];
  		enterButton();
  </script>
  
  </body>
</html>
