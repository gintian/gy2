<%@page import="com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo"%>
<%@page import="java.text.SimpleDateFormat"%>

<%@page import="java.util.Date"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.train.resource.TrainProjectForm,com.hrms.frame.dao.utility.DateUtils" %>
<%
TrainProjectForm daily=(TrainProjectForm)session.getAttribute("trainProjectForm");
%>
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
<style>
body{text-align: center;visibility: hidden;}
.myfixedDiv
{  
	overflow:auto; 
	height:100%;
	width:100%; 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
    border-collapse:collapse
}

.commentdiv{
			position: relative;
			width:100%;
			border-top:solid 1px #C4D8EE;
			padding:0px 0px 10px 0px;
			margin-left:auto;
			margin-right:auto;
			text-align:left;
			border-collapse:collapse
		}
.commentdiv1{
			position: relative;
			width:100%;
			padding:0px 0px 10px 0px;
			margin-left:auto;
			margin-right:auto;
			text-align:left;
			border-collapse:collapse
		}
</style>
<html:form action="/train/resource/mylessonscomment" styleId="form1">
<%int i=0; %>
<div>
    <table width="100%" border="0" id="GV" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" >
      
         <tr>
            <td align="left" class="TableRow" width="50%" nowrap style="border-left-width: 0px;">
            	<logic:equal name="trainProjectForm" property="isNote"  value="1">
				&nbsp;<bean:write name="trainProjectForm" property="lessonName"/>的笔记&nbsp;
				</logic:equal>
				<logic:notEqual name="trainProjectForm" property="isNote"  value="1">
				&nbsp;相关评论&nbsp;
				</logic:notEqual>
            </td>
         </tr>
         <tr>
         <td align="left" class="RecordRow" style="border-left-width: 0px;border-right-width: 0px;border-bottom-width: 0px;" valign="top"> 
         <div id="commentDivId" style="position: relative;width:100%; height: expression(document.body.clientHeight-140);float:none;overflow-y:auto;overflow-x:hidden;text-align:center;">
      		<hrms:paginationdb id="element" name="trainProjectForm" sql_str="trainProjectForm.commentSql" table="" where_str="trainProjectForm.commentWhere" columns="trainProjectForm.commentColumns" order_by="trainProjectForm.commentOrder" allmemo="1" page_id="pagination" pagerows="${trainProjectForm.pagerows}"  indexes="indexes">
        						<bean:define id="cre" name="element" property="createtime"></bean:define>
        						<bean:define id="noteid" name="element" property="id"></bean:define>
        						<%String id = SafeCode.encode(PubFunc.encrypt(noteid.toString())); 
        						String d = "";
        						if(cre.toString()!=null&&cre.toString().length()>10){
        							Date date = new SimpleDateFormat("yyyy-MM-dd").parse(cre.toString().replace(".","-")); 
									d = new SimpleDateFormat("yyyy年MM月dd日").format(date);
        							//d = DateUtils.format(DateUtils.getDate(cre.toString(),"yyyy-MM-dd HH:mm:ss"),"yyyy年MM月dd日 HH:mm");
        						}else if(cre.toString()!=null&&cre.toString().length()==10){
        							Date date = new SimpleDateFormat("yyyy-MM-dd").parse(cre.toString().replace(".","-")); 
									d = new SimpleDateFormat("yyyy年MM月dd日").format(date);
        							
        							//d = DateUtils.format(DateUtils.getDate(cre.toString(),"yyyy-MM-dd"),"yyyy年MM月dd日");
        						} %>
        						<%if (i == 0) {%>
									<div class="commentdiv1" id='<%=id %>'>
									<%} else { %>
										<div class="commentdiv common_border_color" id='<%=id %>'>
									<%} %>
								
									<%if (i == 0) {%>
									<p style="margin:3px 10px 3px 10px;padding:0px;">
									<%} else { %>
										<p style="margin:3px 10px 3px 10px;padding:0px;">
									<%} 
										i++;
									%>
									<bean:write name="element" property="a0101" filter="true"/>&nbsp;<%=d %> &nbsp;
									<logic:notEqual name="trainProjectForm" property="isNote"  value="1">
									发表的评论
									<logic:equal value="1" name="trainProjectForm" property="moduleFlag">
									  <hrms:priv func_id="32306C201">
									   <input type="checkbox" name="commentid" style="float:right;margin-top:-17px;" value="<%=id %>">
									  </hrms:priv>
									</logic:equal>
									</logic:notEqual>
									<logic:equal name="trainProjectForm" property="isNote"  value="1">
									记录笔记<img border="0" src="/images/del.gif" style="float:right;margin-top:-20px;cursor: pointer;" alt="删除" onclick="delnote('<%=id %>');"/>
									</logic:equal>
									</p>
									<hr class="common_border_color" width="100%" style="border:dashed;"/>
									<p style="margin:3px 10px 3px 10px;padding:0px;">
									<bean:write name="element" property="comments" filter="false" />
									
									</p>
								</div>
						
          	&nbsp;&nbsp;
          
        </hrms:paginationdb> 
        </div> 
       </td>  
        </tr>                         	    		        	        	        
    </table>
  </div>
  <table border="0" width="100%" cellspacing="0"  align="center" cellpadding="0">
  <tr>
       <td width="60%" class="RecordRow noright" style="border-top: none;" valign="middle" align="left" height="30" nowrap>
           <hrms:paginationtag name="trainProjectForm"
								pagerows="${trainProjectForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
	  </td>
	  <td  width="40%" class="RecordRow noleft" style="border-top: none;" valign="middle" align="right" nowrap>
	     <hrms:paginationdblink name="trainProjectForm" property="pagination" nameId="trainProjectForm" scope="page">
             </hrms:paginationdblink>
	  </td>
 </tr> 
 </table>
	<table width="100%" cellspacing="0" align="center" cellpadding="0" border="0">
		<tr>
			<td align="center" style="height: 35px; border: none;">
				<logic:equal value="0" name="trainProjectForm" property="moduleFlag">
					<input type="button" name="bt" value="返回" class="mybutton" onclick="returnlearn()" />
				</logic:equal>
				<logic:notEqual value="0" name="trainProjectForm" property="moduleFlag">
					<hrms:priv func_id="32306C201">
						<button name="delbutton" class="mybutton" onclick="delComment()">
							<bean:message key="lable.tz_template.delete" />
						</button>
      				</hrms:priv>
					<button name="closeWin" class="mybutton" onclick="top.close()">
						<bean:message key="button.close" />
					</button>
				</logic:notEqual>
			</td>
		</tr>
	</table>
</html:form>
<script type="text/javascript">
<!--
	function delnote(id) {
		if(!confirm("确认要删除吗？"))
			return;
		var from1 = document.getElementById("form1");
		from1.action = "/train/resource/mylessonscomment.do?b_del=link&id="+id;
		from1.submit();
	}
	
	// function succdelNote(response) {
	//	var value=response.responseText;
	//	var map=Ext.util.JSON.decode(value);
	//	if(map.succeed){
	//		document.getElementById(map.id).style.display="none";
	//	}
	//} 
	
	function returnlearn() {
		var from1 = document.getElementById("form1");
		<logic:equal name="trainProjectForm" property="isLearned" value="0">
			from1.action = "/train/resource/mylessons.do?b_query=link&opt=ing";
		</logic:equal>
		<logic:notEqual name="trainProjectForm" property="isLearned" value="0">
			from1.action = "/train/resource/mylessons.do?b_query=link&opt=ed";
		</logic:notEqual>
		from1.submit();
	}
	
	function delComment(){
		var objs = document.getElementsByName("commentid");
		var commentids='';
		for(var i=0;i<objs.length;i++){
			if(objs[i].checked)
				commentids += objs[i].value+",";
		}

		if (''==commentids) {
			alert(TRAIN_COURSE_COMMENT_SEL);
			return;		
		}

		if(!confirm(TRAIN_COURSE_COMMENT_DEL))
            return;
        
		var from1 = document.getElementById("form1");
		form1.action="/train/resource/mylessonscomment.do?b_delcomment=link&comids="+commentids;
		form1.submit();
	}
//-->
</script>