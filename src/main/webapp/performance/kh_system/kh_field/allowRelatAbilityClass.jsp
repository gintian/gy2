<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
	    // 在标题栏显示当前用户和日期 2004-5-10 
	    String userName = null;
	    String css_url = "/css/css1.css";
	    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	    if (userView != null)
	    {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
		    css_url = "/css/css1.css";
	    }	    	    
%>

<style>

.keyMatterDiv 
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-150);
	width:expression(document.body.clientWidth-10);
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 0pt solid ; 
}

</style>

<script LANGUAGE=javascript src="/js/xtree.js"></script>
<hrms:themes />
<script type="text/javascript" src="/js/wz_tooltip.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script language="javascript">

// 查询
function searchCourseClass()
{
	khFieldForm.action="/performance/kh_system/kh_field/select_ability_class.do?b_query=search";   
    khFieldForm.submit();		
}

// 课程等级选择
function openInputCodePos(codeid,mytarget,managerstr,flag) 
{
    var thecodeurl,valueobj,hiddenobj;
    hiddenobj = document.getElementById(mytarget);
    valueobj = document.getElementById(mytarget+"value");
    var theArr=new Array(codeid,managerstr,valueobj,hiddenobj,flag); 
    thecodeurl="/system/codeselectposinputpos1.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=" + flag; 
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:305px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
}

// 全选
function allSelectOptions(obj)
{
	var arr=document.getElementsByName("choose");
    if(arr)
    {
    	for(var i=0;i<arr.length;i++)
        {
        	if(obj.checked)           
            	arr[i].checked=true;           
            else           
            	arr[i].checked=false;           
        }
    }
}

// 确定
function courseOk()
{	
	var obj=document.getElementsByName("choose");
    var num=0;
    var ids = "";
    for(var i=0;i<obj.length;i++)
    {
		if(obj[i].checked)
        {
        	ids+="/"+obj[i].value;
            num++;
        }
    }
    if(num<=0)  
    {   
        alert("请选择要关联的课程！");  
        return;
    }   
    
	khFieldForm.action="/performance/kh_system/kh_field/select_ability_class.do?b_decide=link&classIds="+ids.substring(1);
	khFieldForm.submit();				
}

// 学习、浏览培训课程
function learn(courseid) 
{
	var url = "/train/resource/mylessons/learncoursebyextjs.jsp?opt=sss`lesson=" + courseid;
	var fram = "/train/resource/mylessons/learniframe.jsp?src="+$URL.encode(url);
	//window.showModalDialog(fram, "", "dialogWidth:880px; dialogHeight:700px;resizable:no;center:yes;scroll:yes;status:yes");
	window.open(fram,'','fullscreen=yes,left=0,top=0,width='+ (screen.availWidth - 10) +',height='+ (screen.availHeight-50) +',scrollbars,resizable=no,toolbar=no,location=no,status=no,menubar=no');
}

// 返回
function goBack()
{
	khFieldForm.action="/performance/kh_system/kh_field/init_kh_field.do?b_search=search";
    khFieldForm.target="mil_body";   
    khFieldForm.submit();		
}       
        
</script>

<html:form action="/performance/kh_system/kh_field/select_ability_class">
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td align="left" style="height:30px;vertical-align: top;">
						
				&nbsp;&nbsp;
				<span style='position:relative;bottom:3px;'><bean:message key="train.resource.mylessons.courseClassType" /></span>&nbsp;&nbsp;
				<html:text name="khFieldForm" property="itemizevalue"  styleClass="textbox inputtext" styleId="itemizevalue" readonly="true"/>
 				<html:hidden name="khFieldForm" property="itemize"  styleClass="textbox" styleId="itemize"/>
 				<img src="/images/code.gif" onclick='javascript:openInputCodePos("55","itemize","05","2");'  style="vertical-align: middle;margin-top:-3px;"/>
				
				&nbsp;&nbsp;
				<span style='position:relative;bottom:3px;'><bean:message key="train.resource.mylessons.coursename" /></span>&nbsp;&nbsp;
				<html:text name="khFieldForm" property="coursename"  styleClass="textbox inputtext" styleId="coursename"/>
				
				&nbsp;&nbsp;								
				<span style='position:relative;bottom:3px;'><bean:message key="train.resource.mylessons.courseClassdesc" /></span>&nbsp;&nbsp;
				<html:text name="khFieldForm" property="courseintro"  styleClass="textbox inputtext" styleId="courseintro"/>
				
				&nbsp;
				<input type='button' class="mybutton" property="checked" onclick='searchCourseClass()'
						value='<bean:message key="infor.menu.query"/>' />
			</td>
		<%
			int i = 0;
		%>
		</tr>
		<tr>
			<td>
			   <div class="keyMatterDiv common_border_color">
				<table width="100%" border="0" align="center" class="ListTable">
				
					<tr class="fixedHeaderTr">
						<td align="center" class="TableRow_right common_background_color common_border_color" width="3%" nowrap>
							<input type="checkbox" name="chk" value="1" onclick="allSelectOptions(this);"/>
						</td>
						<td align="center" class="TableRow" nowrap>
							<bean:message key="train.resource.mylessons.courseClassType" />
						</td>
						<td align="center" class="TableRow" nowrap>														
							<bean:message key="train.resource.mylessons.coursename"/>														
						</td>						
						<td align="center" class="TableRow" nowrap>
							<bean:message key="train.resource.mylessons.courseClassdesc" />
						</td>
						<td align="center" class="TableRow" nowrap>
							<bean:message key="train.resource.mylessons.courseClasshour" />
						</td>
						<td align="center" class="TableRow_left  common_background_color common_border_color" nowrap>
							<bean:message key="train.resource.mylessons.courseClassscore" />
						</td>												
							
						<hrms:priv func_id="">	
							<td align="center" class="TableRow_left  common_background_color common_border_color" nowrap>
								<bean:message key="kh.field.opt" />
							</td>
						</hrms:priv>	
						
					</tr>

					<hrms:extenditerate id="element" name="khFieldForm" property="fieldinfolistForm.list" indexes="indexes"
						pagination="fieldinfolistForm.pagination" pageCount="20" scope="session">
												
						<%
							if (i % 2 == 0)
							{
						%>
						<tr class="trShallow">
						<%
							}else{
						%>						
						<tr class="trDeep">
						<%
							}
							i++;
						%>
							<td align="center" class="RecordRow_right" width="3%" nowrap>
								<input type="checkbox" name="choose" value="<bean:write name="element" property="classId"/>"/>
							</td>
							<td align="left" class="RecordRow" nowrap>	&nbsp;						
								<bean:write name="element" property="classType" filter="true" />
							</td>
							<td align="left" class="RecordRow" nowrap>	&nbsp;						
								<bean:write name="element" property="className" filter="true" />
							</td>														

							<bean:define id="event" name="element" property="classDesc" />
							<hrms:showitemmemo showtext="showtext" itemtype="M" setname=""
								tiptext="tiptext" text="${event}"></hrms:showitemmemo>
							<td align="left" class="RecordRow" ${tiptext}  nowrap>&nbsp;
								${showtext}&nbsp;
							</td>

							<td align="right" class="RecordRow" nowrap>&nbsp;
								<bean:write name="element" property="classHour" filter="true" />&nbsp;
							</td>							
							<td align="right" class="RecordRow_left" nowrap>
								<bean:write name="element" property="classScore" filter="true" />&nbsp;
							</td>														
							
							<hrms:priv func_id="">
								<td align="center" class="RecordRow_left" nowrap>	
									<bean:define id="lessonid" name="element" property="classId"/>
									<%String id = SafeCode.encode(PubFunc.encrypt(lessonid.toString()));  %>
									<a href="javascript:learn('<%=id %>')" onclick=""> 
					          	   		<img src="/images/view.gif" alt="浏览" border="0">
					            	</a>																																		
								</td>
							</hrms:priv>
							
						</tr>
					</hrms:extenditerate>
				</table>
				</div>
			</td>
		</tr>
	</table>
	<table width="100%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				第
				<bean:write name="khFieldForm"
					property="fieldinfolistForm.pagination.current" filter="true" />
				页 共
				<bean:write name="khFieldForm"
					property="fieldinfolistForm.pagination.count" filter="true" />
				条 共
				<bean:write name="khFieldForm"
					property="fieldinfolistForm.pagination.pages" filter="true" />
				页
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="khFieldForm"
						property="fieldinfolistForm.pagination" nameId="fieldinfolistForm"
						propertyId="fieldinfolistProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>	
	<table width="100%" align="center">
		<tr>
			<td align="center" style="height:35px"> 									
				<hrms:priv func_id="">		
					<input type='button' class="mybutton" property="b_ok"
						onclick='courseOk();'
						value='确定' />
				</hrms:priv>			
				<hrms:priv func_id="">	
					<input type='button' class="mybutton" property="b_back"
						onclick='goBack()'
						value='返回' />
				</hrms:priv>	
			</td>
		</tr>
	</table>
</html:form>
