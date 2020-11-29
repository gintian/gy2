<html>
    <%@ page contentType="text/html; charset=UTF-8"%>
    <%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>   
    <%@ taglib uri="/tags/struts-html" prefix="html" %>
    <%@ taglib uri="/tags/struts-bean" prefix="bean"%>
    <%@ taglib uri="/tags/struts-logic" prefix="logic" %>
    <%@ page import="com.hjsj.hrms.actionform.train.ilearning.IlearningStudentForm" %>
<style>
  li{
     backgound-image: url(/images/book1.gif);
  }
  
  span{
      color: green;
      font-size: 18px;
  }
</style>
<head> 
 
</head>

<body style="height:100%;width:100%;overflow:hidden;margin:0;padding:0;TEXT-ALIGN:center;">
    <br>
    <div id="studentstatu">
        <table style="margin:8% 0 0 10%">
    <!--
          <tr>
            <td><img src="/images/book1.gif"/></td>
            <td>&nbsp;<bean:message key="Ilearning.learning"/>：</td>
            <td align="center"><span>${ilearningStudentForm.learningCourseCount}</span></td>
            <td><bean:message key="Ilearning.subject"/></td>
          </tr>
          <tr>
            <td><img src="/images/book1.gif"/></td>
            <td>&nbsp;<bean:message key="Ilearning.learned"/>：</td>
            <td align="center"><span>${ilearningStudentForm.learnedCourseCount}</span></td>
            <td><bean:message key="Ilearning.subject"/></td>
          </tr>
      	  <tr>
            <td><img src="/images/book1.gif"/></td>
            <td>&nbsp;<bean:message key="Ilearning.learningPoint"/>：</td>
            <td align="right"><span>${ilearningStudentForm.learningPoint}</span></td>
            <td><bean:message key="Ilearning.score"/></td>
          </tr>
	-->
          <tr>
            <td><img src="/images/book1.gif"/></td>
            <td>&nbsp;<bean:message key="Ilearning.learning.req"/>：</td>
            <logic:equal value="0" name="ilearningStudentForm" property="learningReqCourseCount">
            	<td align="center"><span>${ilearningStudentForm.learningReqCourseCount}</span></td>
            </logic:equal>
            <logic:notEqual value="0" name="ilearningStudentForm" property="learningReqCourseCount">
            	<td align="center"><a href="javascript:" onclick='window.top.tabs(1)'><span>${ilearningStudentForm.learningReqCourseCount}</span></a></td>
            </logic:notEqual>
            <td><bean:message key="Ilearning.subject"/></td>
          </tr>
           <tr>
            <td><img src="/images/book1.gif"/></td>
            <td>&nbsp;<bean:message key="Ilearning.learning.opt"/>：</td>
            <logic:equal value="0" name="ilearningStudentForm" property="learningOptCourseCount">
            	<td align="center"><span>${ilearningStudentForm.learningOptCourseCount}</span></td>
            </logic:equal>
            <logic:notEqual value="0" name="ilearningStudentForm" property="learningOptCourseCount">
            	<td align="center"><a href="javascript:" onclick="window.top.tabs(2)"><span>${ilearningStudentForm.learningOptCourseCount}</span></a></td>
            </logic:notEqual>
            <td><bean:message key="Ilearning.subject"/></td>
          </tr>
          <tr>
            <td><img src="/images/book1.gif"/></td>
            <td>&nbsp;<bean:message key="Ilearning.learned.req"/>：</td>
            <logic:equal value="0" name="ilearningStudentForm" property="learnedReqCourseCount">
            	<td align="center"><span>${ilearningStudentForm.learnedReqCourseCount}</span></td>
            </logic:equal>
            <logic:notEqual value="0" name="ilearningStudentForm" property="learnedReqCourseCount">
            	<td align="center"><a href="javascript:" onclick="window.top.tabs(3)"><span>${ilearningStudentForm.learnedReqCourseCount}</span></a></td>
            </logic:notEqual>
            <td><bean:message key="Ilearning.subject"/></td>
          </tr>
           <tr>
            <td><img src="/images/book1.gif"/></td>
            <td>&nbsp;<bean:message key="Ilearning.learned.opt"/>：</td>
            <logic:equal value="0" name="ilearningStudentForm" property="learnedOptCourseCount">
            	<td align="center"><span>${ilearningStudentForm.learnedOptCourseCount}</span></td>
            </logic:equal>
            <logic:notEqual value="0" name="ilearningStudentForm" property="learnedOptCourseCount">
            	<td align="center"><a href="javascript:" onclick="window.top.tabs(4)"><span>${ilearningStudentForm.learnedOptCourseCount}</span></a></td>
            </logic:notEqual>
            <td><bean:message key="Ilearning.subject"/></td>
          </tr>
          <tr>
            <td><img src="/images/book1.gif"/></td>
            <td>&nbsp;&nbsp;<bean:message key="Ilearning.learningScore"/>：</td>
            <logic:equal value="0" name="ilearningStudentForm" property="learningPoint">
            	<td align="right"><span>${ilearningStudentForm.learningPoint}</span></td>
            </logic:equal>
            <logic:notEqual value="0" name="ilearningStudentForm" property="learningPoint">
           		<td align="right"><a href="javascript:" onclick="window.top.tabs(5)"><span>${ilearningStudentForm.learningPoint}</span></a></td>
            </logic:notEqual>
            <td><bean:message key="Ilearning.score"/></td>
          </tr>
        </table>
    </div> 

</body>
</html>