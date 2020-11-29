<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.transaction.train.trainexam.paper.PubExamPaperTrans"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hjsj.hrms.actionform.train.trainexam.paper.RelExamCourseForm" %>
<%@page import="java.util.List"%>

<%// 在标题栏显示当前用户和日期 2004-5-10 
			String userName = null;
			String css_url = "/css/css1.css";
			UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			int status=userView.getStatus();
			String manager=userView.getManagePrivCodeValue();
			int fflag=1;
			String webserver=SystemConfig.getPropertyValue("webserver");
			if(webserver.equalsIgnoreCase("websphere"))
				fflag=2;
%>
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

<script language="javascript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
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
	var webserver=<%=fflag%>;
	  

function html_decode(str)   
{   
  var s = "";   
  if (str.length == 0) return "";   
  s = str.replace(/&amp;/g, "&");   
  s = s.replace(/&lt;/g, "<");   
  s = s.replace(/&gt;/g, ">");   
  s = s.replace(/&nbsp;/g, " ");   
  s = s.replace(/&#39;/g, "\'");   
  s = s.replace(/&quot;/g, "\"");   
  s = s.replace(/<br>/g, "\n");   
  return s;   
}
</script>
<%int i=0;%>
<html:form action="/train/trainexam/paper/relcourse">
<html:hidden name="relExamCourseForm" property="r5300" />
 <table border="0" cellpadding="0" cellspacing="0" style="margin-top: 5px;">
 	<tr>
 	<td>
 		　　课程分类&nbsp;
 		<html:text name="relExamCourseForm" property="itemizevalue"  styleClass="text4" styleId="itemizevalue" readonly="true"/>
 		<html:hidden name="relExamCourseForm" property="itemize"  styleClass="textbox" styleId="itemize"/>
 		<img src="/images/code.gif" onclick='javascript:openInputCodePos("55_1","itemize","05","2");'  style="vertical-align: middle;"/>
 		&nbsp;&nbsp;课程名称&nbsp;
 		<html:text name="relExamCourseForm" property="coursename"  styleClass="text4" styleId="coursename"/>
 		&nbsp;&nbsp;课程简介&nbsp;
 		<html:text name="relExamCourseForm" property="courseintro"  styleClass="text4" styleId="courseintro"/>
 		&nbsp;<span style="vertical-align: middle;"><input type="button" value="查询" class="mybutton" onclick="search();"/></span>
 	</td>
 	</tr>
   <tr>
    <td>
     <div class="fixedDiv2" style="margin-top: 5px;"> 
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id="tableid" >
           <tr class="fixedHeaderTr">
             <td align="center" class="TableRow" nowrap width="20" style="border-left: none;border-top:none;">
              <input type="checkbox" name="selbox" onclick="batch_select(this,'sel');" title='<bean:message key="label.query.selectall"/>'>
             </td>
             <logic:iterate id="info"    name="relExamCourseForm"  property="itemlist">   
              <logic:equal name="info" property="visible" value="true">
              <td align="center" class="TableRow" style="border-left: none;border-top:none;" nowrap>
                   <bean:write  name="info" property="itemdesc" filter="true"/>&nbsp;              
              </td>
              </logic:equal>
             </logic:iterate>
             <td align="center" class="TableRow" nowrap  width="40" style="border-right:none;border-left: none;border-top:none;">
                              操作
             </td>	    	    	    		        	        	        
           </tr>

          <hrms:paginationdb id="element" name="relExamCourseForm" sql_str="relExamCourseForm.strsql" table="" where_str="relExamCourseForm.strwhere" columns="relExamCourseForm.columns" order_by="relExamCourseForm.order_by" page_id="pagination" pagerows="${relExamCourseForm.pagerows}" indexes="indexes">
          <%
          
          if(i%2==0)
          {
          %>
          <tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'')">
          <%}
          else
          {%>
          <tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'E4F2FC')">
          <%
          }
          i++;          
          %>  
          <bean:define id="lessid" name="element" property="r5000"/>
          <%String r5000 = SafeCode.encode(PubFunc.encrypt(lessid.toString())); %>
            <td align="center" class="RecordRow" nowrap  style="border-left: none;border-top:none;">
            	<logic:equal value="" name="element" property="r5300">
              		<input type="checkbox" name="sel" value='<%=r5000 %>'/>
                </logic:equal>
                <logic:notEqual value="" name="element" property="r5300">
              		<input type="checkbox" name="sel" checked="checked" value='<%=r5000 %>'/>
                </logic:notEqual>
            </td>  
           
	         <logic:iterate id="info"    name="relExamCourseForm"  property="itemlist">  	
	         <logic:equal name="info" property="visible" value="true">
                  <logic:notEqual  name="info" property="itemtype" value="N">               
                    <logic:notEqual  name="info" property="itemtype" value="M">               
                    	<td align="left" class="RecordRow" style="border-left: none;border-top:none;" nowrap>
                    </logic:notEqual>   
	                <logic:equal  name="info" property="itemtype" value="M">               
	                    <td align="left" class="RecordRow" style="border-left: none;border-top:none;" onmouseout="UnTip();" onmouseover='outContent("${info.itemid}","<%=r5000 %>");' style="width: 8%;" nowrap>        
	                </logic:equal>      
                  </logic:notEqual>
                  <logic:equal  name="info" property="itemtype" value="N">               
                    <td align="right" class="RecordRow" style="border-left: none;border-top:none;" nowrap>        
                  </logic:equal>    
                  <logic:equal  name="info" property="codesetid" value="0">  
                  	<logic:equal value="r5004" property="itemid" name="info">
                  		<hrms:codetoname codeid="55" name="element" codevalue="r5004" codeitem="codeitem" scope="page"/>  	      
          	    	    &nbsp; <bean:write name="codeitem" property="codename" />&nbsp;  
                  	</logic:equal> 
                  	<logic:notEqual value="r5004" property="itemid" name="info">
	                  	<logic:notEqual  name="info" property="itemtype" value="M"> 
	                    	&nbsp; <bean:write  name="element" property="${info.itemid}" filter="true"/>&nbsp;
	                   	</logic:notEqual>
	                   	<logic:equal name="info" property="itemtype" value="M">
	                   		&nbsp;<script>
	                   			document.write(html_decode("<bean:write  name="element" property="${info.itemid}" filter="true"/>"));
	                   		</script>&nbsp;</td>
	                   	</logic:equal>
                    </logic:notEqual>
                  </logic:equal>
                 <logic:notEqual  name="info" property="codesetid" value="0">  
                 <logic:equal name="info" property="codesetid" value="UM">
                     <hrms:codetoname codeid="UM" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel=""/>  	      
          	           &nbsp;  <bean:write name="codeitem" property="codename" />&nbsp; 
                   </logic:equal>
                   <logic:notEqual name="info" property="codesetid" value="UM">
                        <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
          	    	    &nbsp; <bean:write name="codeitem" property="codename" />&nbsp;  
                   </logic:notEqual>                 
          	     </logic:notEqual>  
              
              </logic:equal>
             </logic:iterate>  
             <td align="center" class="RecordRow" nowrap  width="40" style="border-right: none;border-left: none;border-top:none;">
                 <a href="###" onclick="learn('<%=r5000 %>')"> 
          	   		<img src="/images/view.gif" alt="浏览" border="0">
            	 </a> 
             </td>    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
        
</table>
</div>
</td></tr>
<tr><td style="padding-right: 5px;">
<table width="100%"  align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            	<hrms:paginationtag name="relExamCourseForm"
								pagerows="${relExamCourseForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <hrms:paginationdblink name="relExamCourseForm" property="pagination" nameId="relExamCourseForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</td>
</tr>          
</table>
<table style="margin-top: 5px;" cellpadding="0" cellspacing="0">
		<tr>
			<td>
				<input type="button" name="b_retrun" value="确定" class="mybutton" onclick="save();" />
				<input type="button" name="b_retrun" value="返回" class="mybutton" onclick="relreturn();" />
			</td>
		</tr>
	</table>
</html:form>
<script>
function search(){
	relExamCourseForm.action = "/train/trainexam/paper/relcourse.do?b_relcourse=link";
	relExamCourseForm.submit();
}
function save(){
	var sel="";
	var allsels="";
	var sels = document.getElementsByTagName("input");	
	for(var i=0;i<sels.length;i++)
	{			
	   if(sels[i].type=='checkbox'&&sels[i].name!="selbox")
	   {		   			
			if(sels[i].checked==true)
			{
				sel+=sels[i].value+",";
			}
			allsels+=sels[i].value+",";
	   }
    }
    //alert("allsels:"+allsels+"\r\nsel:"+sel);
	var hashvo=new ParameterSet();
    hashvo.setValue("exams", "${relExamCourseForm.r5300}");
    hashvo.setValue("sels", sel);
    hashvo.setValue("allsels", allsels);
    var request=new Request({method:'post',onSuccess:examajaxok,functionId:'2020070016'},hashvo);
}
function examajaxok(outparamters){
	var flag=outparamters.getValue("flag"); 
	if("ok"==flag){
		//alert("关联成功！");
		relreturn();
	}else{
		alert("关联失败！");
	}
}
function relreturn(){
	location.href = "/train/trainexam/paper.do?b_query=link";	
}

function openInputCodePos(codeid,mytarget,managerstr,flag) 
{
    var thecodeurl,valueobj,hiddenobj;
    hiddenobj = document.getElementById(mytarget);
    valueobj = document.getElementById(mytarget+"value");
    var theArr=new Array(codeid,managerstr,valueobj,hiddenobj,flag); 
    thecodeurl="/system/codeselectposinputpos1.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=" + flag; 
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}
//学习
function learn(courseid) {
var url = "/train/resource/mylessons/learncoursebyextjs.jsp?opt=sss`lesson=" + courseid;
var fram = "/train/resource/mylessons/learniframe.jsp?src="+$URL.encode(url);
window.open(fram,'','fullscreen=yes,left=0,top=0,width='+ (screen.availWidth - 10) +',height='+ (screen.availHeight-50) +',scrollbars,resizable=no,toolbar=no,location=no,status=no,menubar=no');
}
function outContent(column,rid){
	var hashvo=new ParameterSet();
	hashvo.setValue("table","r50");	
	hashvo.setValue("column",column);
	hashvo.setValue("keys","r5000");
	hashvo.setValue("values",rid);
   	var request=new Request({method:'post',asynchronous:true,onSuccess:viewContent,functionId:'2020061000'},hashvo);
}
function viewContent(outparamters){
	var content=outparamters.getValue("content");
	config.FontSize='10pt';//hint提示信息中的字体大小
	Tip(getDecodeStr(content),STICKY,true);
}
</script>