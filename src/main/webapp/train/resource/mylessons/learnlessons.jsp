<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.businessobject.train.resource.MyLessonBo"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.train.resource.TrainProjectForm" %>
<%
TrainProjectForm daily=(TrainProjectForm)session.getAttribute("trainProjectForm");
String lessonState = (String)request.getParameter("opt");
lessonState = lessonState == null || lessonState.length()<1 ? "" : "`lessonState="+lessonState;
UserView userView = (UserView)session.getAttribute(WebConstant.userView);

%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>

<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
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
<script language="JavaScript" src="/js/meizzDate_saveop.js"></script>
<script language="javascript" src="/ext/adapter/ext/ext-base.js"></script>
<script language="javascript" src="/ext/ext-all.js"></script>
<script language="javascript" src="/ext/rpc_command.js"></script>
<style>
body{padding-top: 5px;text-align: center;padding-left: 5px;}
.myfixedDiv
{  
	overflow:auto; 
	height:expression(document.body.clientHeight-75);
	width:expression(document.body.clientWidth-10); 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
    border-collapse:collapse
}

</style>
<hrms:themes></hrms:themes>
<html:form action="/train/resource/mylessons" styleId="form1">

<% int s=0;
	int i=0;
   int n=0;
   String name=null;
   int num_s=0;
   int lock=0;
%>


<div class="myfixedDiv">
	<table width="100%" border="0" cellspacing="0" cellpadding="0" class="ListTableF" >
		<tr>
			<td height="35" style="padding-left: 5px;" valign="middle">
			课程状态 <html:select name="trainProjectForm" property="state" onchange="searchlesson();" styleId="state"> 
					<html:option value="all">全部</html:option> 
					<html:option value="1">正学</html:option> 
					<html:option value="2">已学</html:option> 
				 </html:select>
					
				</select>
			课程名称   <html:text name="trainProjectForm" property="searchLesson" styleId="NameId"> </html:text>
			<input type="button" class="mybutton" value="查询" onclick="searchlesson()"/>
			</td>
		</tr>
	</table>
    <table width="100%" border="0" id="GV" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" >
      
         <tr>
         	<td align="center" class="TableRow" width="80" nowrap style="border-left-width: 0px;">
				&nbsp;选修/必修&nbsp;
            </td>
            <td align="center" class="TableRow" width="50" nowrap style="border-left-width: 0px;">
				&nbsp;状态&nbsp;
            </td>
            <td align="center" class="TableRow" width="30%" nowrap style="border-left-width: 0px;">
				&nbsp;<bean:message key="train.resource.mylessons.coursename"/>&nbsp;
            </td>
            <td align="center" class="TableRow" width="11%" nowrap style="border-left-width: 0px;">
				&nbsp;<bean:message key="train.examplan.begindate"/>&nbsp;
            </td>
            <td align="center" class="TableRow" width="11%" nowrap style="border-left-width: 0px;">
				&nbsp;<bean:message key="train.examplan.enddate"/>&nbsp;
            </td>
            <td align="center" class="TableRow" width="5%" nowrap style="border-left-width: 0px;">
				&nbsp;<bean:message key="hmuster.label.counts"/>&nbsp;
            </td>
            <logic:equal name="trainProjectForm" property="isLearned" value="0">
	            <td align="center" class="TableRow" width="14%" nowrap>
					&nbsp;<bean:message key="train.resource.mylessons.courseprogress"/>&nbsp;
	            </td>
            </logic:equal>
            
            <logic:iterate id="element" name="trainProjectForm"  property="viewItemList" indexId="index">
				<td align="center" class="TableRow" width="80" nowrap>
	                 <bean:write  name="element" property="itemdesc" filter="true"/>
		        </td> 
			</logic:iterate>
            
            <logic:equal name="trainProjectForm" property="isLearned" value="0">
	            <td align="center" class="TableRow" width="6%" nowrap>
					&nbsp;<bean:message key="train.resource.mylessons.coursetest"/>&nbsp;
	            </td>
            </logic:equal>
            <td align="center" class="TableRow" width="6%" nowrap>
				&nbsp;<bean:message key="train.resource.mylessons.coursenotes"/>&nbsp;
            </td>
            <td align="center" class="TableRow" width="6%" nowrap style="border-right-width: 0px;">
				&nbsp;<bean:message key="train.resource.mylessons.coursecomment"/>&nbsp;
            </td>	        
         </tr>
     	<div>
      <%i=0; %>
      <hrms:paginationdb id="element" name="trainProjectForm" sql_str="trainProjectForm.myLessonSql" table="" where_str="trainProjectForm.myLessonWhere" columns="trainProjectForm.myLessonColumns" order_by="trainProjectForm.myLessonOrder" page_id="pagination" pagerows="${trainProjectForm.pagerows}"  indexes="indexes">
        <bean:define id="lessonid" name="element" property="r5000"/>
        <bean:define id="lessoncode" name="element" property="r5004"/>
          <% String r5000 = SafeCode.encode(PubFunc.encrypt(lessonid.toString()));
          String r5004 = SafeCode.encode(PubFunc.encrypt(lessoncode.toString())); 
          if(i%2==0){ 
          %>
          <tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'')">
          <%
          }else{
          %>
          <tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'')">
          <%}          
          %>     
          <% int  inNum=0;lock=0;%>  
          <td align="left" class="RecordRow" nowrap style="border-left-width: 0px;"> 
          	&nbsp;
          	<logic:equal value="1" name="element" property="lesson_from">选修</logic:equal>
          	<logic:notEqual value="1" name="element" property="lesson_from">必修</logic:notEqual>
          	&nbsp;
          </td>
          <td align="left" class="RecordRow" nowrap style="border-left-width: 0px;"> 
          	&nbsp;
          	<logic:equal value="0" name="element" property="state">未学</logic:equal>
          	<logic:equal value="1" name="element" property="state">正学</logic:equal>
          	<logic:equal value="2" name="element" property="state">已学</logic:equal>
          	&nbsp;
          	
          </td>
          <td align="left" class="RecordRow" nowrap style="border-left-width: 0px;"> 
          	&nbsp;
          <a href="javascript:;" onclick="learn('<%=r5000 %>','<%=r5004 %>','<bean:write name="element" property="state" filter="true"/>')">
          	<bean:write name="element" property="r5003" filter="true"/>
          </a>
          	&nbsp;
          </td>
          <td align="center" class="RecordRow" nowrap style="border-left-width: 0px;"> 
          	&nbsp;<bean:write name="element" property="start_date" filter="true"/>&nbsp;
          </td>
          <td align="center" class="RecordRow" nowrap style="border-left-width: 0px;"> 
          	&nbsp;<bean:write name="element" property="end_date" filter="true"/>&nbsp;
          </td>
          <td align="right" class="RecordRow" nowrap style="border-left-width: 0px;"> 
          	&nbsp;<bean:write name="element" property="learnednum" filter="true"/>&nbsp;
          </td>
          <logic:equal name="trainProjectForm" property="isLearned" value="0">
          <td align="left" class="RecordRow" nowrap> 
          	<bean:define id="lprogress" name="element" property="lprogress"></bean:define>
          	<%if(StringUtils.isEmpty(lprogress.toString()))
          	  	  lprogress = "0";
          	    %>
          		<table border='0'>
					<tr>
						<td>
							<!--  <div style="position: relative;width:100px;height:15px;border:1px solid #84ADC9;display: inline-block;">
							<div style='position: relative;width:<bean:write name="element" property="lprogress" filter="true"/>%;height:15px;background-image:url(/images/shu_bg_bg.gif);background-repeat:repeat-x;background-position:right;'>&nbsp;</div>
							</div>-->
							<img border="0" width='"+value+"px' height=10 src='/images/board_bottom_1.gif' style="width:<%=lprogress %>px;height:10px;"/>
						</td>
						<td><span style='border:0px;'><%=lprogress %>%</span></td>
					</tr>
				</table>
			
          </td>
          </logic:equal>
          <bean:define id="courseID" name="element" property="r5000"></bean:define>
          <logic:iterate id="fielditem" name="trainProjectForm"  property="viewItemList" indexId="index">
          	  <logic:equal value="SCORE" name="fielditem" property="itemid">	
          		  <td align="right" class="RecordRow" nowrap>
		               &nbsp;<%=MyLessonBo.getScore(courseID.toString(), userView)%>&nbsp;
			      </td>
          	  </logic:equal>
          	  
          	  <logic:notEqual value="SCORE" name="fielditem" property="itemid">
	          	  <logic:equal value="N" name="fielditem" property="itemtype">	
					<td align="right" class="RecordRow" nowrap>
		                 &nbsp;<bean:write  name="element" property="${fielditem.itemid}" filter="true"/>&nbsp;
			       </td>
			      </logic:equal> 
			      <logic:notEqual value="N" name="fielditem" property="itemtype">	
					<td align="center" class="RecordRow" nowrap>
						<logic:equal name="fielditem" property="codesetid" value="UM">
                       <hrms:codetoname codeid="UM" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="0"/>  	      
          	          		<!-- 
          	            	//tianye update start
							//关联部门的指标支持指定单位（部门中查不出信息就去单位中查找）
          	            	-->
          	          		<logic:notEqual  name="codeitem" property="codename" value="">
          	           			&nbsp;  <bean:write name="codeitem" property="codename" />&nbsp; 
          	           		</logic:notEqual>
          	          		<logic:equal  name="codeitem" property="codename" value="">
          	          		 	<hrms:codetoname codeid="UN" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page" uplevel="0"/>  
          	           			&nbsp;  <bean:write name="codeitem" property="codename" />&nbsp; 
          	           		</logic:equal>
          	           		<!-- end -->
                      </logic:equal>
                      <logic:notEqual name="fielditem" property="codesetid" value="UM">                      
                        <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	    	    &nbsp; <bean:write name="codeitem" property="codename" />&nbsp;  
                        
                      </logic:notEqual>   
					  <logic:equal name="fielditem" property="codesetid" value="0">
		                 &nbsp;<bean:write  name="element" property="${fielditem.itemid}" filter="true"/>&nbsp;
		              </logic:equal>
			       </td>
			      </logic:notEqual> 
          	  </logic:notEqual>	
		  </logic:iterate>
          
          
          <logic:equal name="trainProjectForm" property="isLearned" value="0">
          
          <td align="center" class="RecordRow" nowrap> 
          	&nbsp;
          	<logic:notEqual value="2" name="element" property="state">
          	<logic:equal name="element" property="r5024" value="1">
          	<%if (MyLessonBo.isShow(lessonid.toString())) { %>
          	<a href="javascript:;" onclick="test('<%=r5000 %>')"><img border="0" src="/images/exam.png" alt='<bean:message key="train.resource.mylessons.coursetest"/>'/></a>&nbsp;
          	<%} %>
          	</logic:equal>
          	</logic:notEqual>
          </td>
          
          </logic:equal>
          <td align="center" class="RecordRow" nowrap> 
          	&nbsp;<a href="javascript:;" onclick="note('<%=r5000 %>')"><img border="0" src="/images/note.png" alt='<bean:message key="train.resource.mylessons.coursenotes"/>'/></a>&nbsp;
          </td>
          <td align="center" class="RecordRow" nowrap style="border-right-width: 0px;"> 
          	&nbsp;<a href="javascript:;" onclick="comment('<%=r5000 %>')"><img border="0" src="/images/discuss.png" alt='<bean:message key="train.resource.mylessons.coursecomment"/>'/></a>&nbsp;
          </td>
           
            <%
            i++;  
            %>  
          </tr>
          <%
          s++;
          %>
        </hrms:paginationdb>                                 	    		        	        	        
    </table>
  </div>
  <div class="myfixedDiv" style="height:40px; border-top-width:0px;">		    
     <table  width="100%" >
       <tr>          
       <td width="60%" valign="bottom" align="left" height="30" nowrap>
           <hrms:paginationtag name="trainProjectForm"
								pagerows="${trainProjectForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
	  </td>
	  <td  width="40%" valign="bottom" align="right" nowrap>
	     <hrms:paginationdblink name="trainProjectForm" property="pagination" nameId="trainProjectForm" scope="page">
             </hrms:paginationdblink>
	  </td>
	  <td></td>
	</tr>
     </table>
     
</div>

</html:form>

<logic:equal name="trainProjectForm" property="home" value="5">
<center><input type="button" value="返回"  class="mybutton" onclick="goPortal();"/></center>
</logic:equal>

<script type="text/javascript">

  function goPortal()
  {
    parent.location.href = "/templates/index/portal.do?b_query=link";
  }
  
//<!--
	//学习
	function learn(courseid,classes,state) {
	var map = new HashMap();
	map.put("r5000",courseid);
	Rpc({functionId:'2020030198'},map);
		
	var url = "/train/resource/mylessons/learncoursebyextjs.jsp?opt=me`classes="+classes+"`lesson=" + courseid+"`state="+state;
	//var url = "/train/resource/mylessons/show.do?b_query=link";
	var fram = "/train/resource/mylessons/learniframe.jsp?src="+$URL.encode(url);
	//window.showModalDialog(fram, "", "dialogWidth:880px; dialogHeight:700px;resizable:no;center:yes;scroll:yes;status:yes");
	window.open(fram,"learnwindow");
		
	}
	
	//考试
	function test(courseid) {
		var url = "/train/trainexam/exam/mytest/mytest.do?b_querry=link&lessonId=" + courseid;
		var form1 = document.getElementById("form1")
		form1.action=url;
		//window.showModalDialog(fram, "", "dialogWidth:780px; dialogHeight:580px;resizable:no;center:yes;scroll:yes;status:yes");
		form1.submit();
	}
	
	//笔记
	function note(courseid) {
		var url = "/train/resource/mylessonscomment.do?b_comment=link&opt=note&lesson=" + courseid;
		//var fram = "/train/resource/mylessons/learniframe.jsp?src="+url;
		var form1 = document.getElementById("form1")
		form1.action=url;
		//window.showModalDialog(fram, "", "dialogWidth:780px; dialogHeight:580px;resizable:no;center:yes;scroll:yes;status:yes");
		form1.submit();
	}
	
	//评论
	function comment(courseid) {
		var url = "/train/resource/mylessonscomment.do?b_comment=link&opt=comment&lesson=" + courseid;
		//var fram = "/train/resource/mylessons/learniframe.jsp?src="+url;
		var form1 = document.getElementById("form1")
		form1.action=url;
		//window.showModalDialog(fram, "", "dialogWidth:780px; dialogHeight:580px;resizable:no;center:yes;scroll:yes;status:yes");
		form1.submit();
	}

	function searchlesson() {
		var state = document.getElementById("state").value;
		var searchLesson = document.getElementById("nameId").value;
		var form1 = document.getElementById("form1");
		form1.action="/train/resource/mylessons.do?b_query=link&opt=all&state=" + state + "&searchLesson=" + searchLesson;
		form1.submit();
	}
//-->
</script>
