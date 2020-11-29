<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.constant.SystemConfig"%>
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
<script language="javascript" src="/js/wz_tooltip.js"></script>
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
	
	
	function exampaperdel(){		
		var a=0;
		var sels="";
		var a_IDs=document.getElementsByName("selectid");	
		for(var i=0;i<document.forms[0].elements.length;i++)
		{			
		   if(document.forms[0].elements[i].type=='checkbox'&&document.examPaperForm.elements[i].name!="selbox")
		   {		   			
				if(document.forms[0].elements[i].checked==true)
				{
					sels+=a_IDs[a].value+",";
				}
				a++;
		   }
	    }
		if(!sels)
    		alert("请选择要删除的试卷！");
    	else if(confirm("确认要删除吗？")){
	    	if(checkIsParent(sels)){
	    		alert("只能删除管理范围内的试卷！");
	    		return;
	    	}
			var hashvo=new ParameterSet();
	        hashvo.setValue("sels", sels);
	        var request=new Request({method:'post',onSuccess:examajaxok,functionId:'2020070012'},hashvo);
        }
	}
	
	function exampaperpub(state){
		var a=0;
		var sels="";
		var a_IDs=document.getElementsByName("selectid");		
		for(var i=0;i<document.forms[0].elements.length;i++)
		{			
		   if(document.forms[0].elements[i].type=='checkbox'&&document.examPaperForm.elements[i].name!="selbox")
		   {		   			
				if(document.forms[0].elements[i].checked==true)
				{
					sels+=a_IDs[a].value+",";
				}
				a++;
		   }
	    }
		var tmp="";
		if(state=="04")
			tmp="发布";
		else
			tmp="暂停";
		if(!sels){
    		alert("请选择要"+tmp+"的试卷！");
    		return;
    	}
    	if(checkIsParent(sels)){
    		alert("只能操作管理范围内的试卷！");
    		return;
    	}
		var hashvo=new ParameterSet();
        hashvo.setValue("sels", sels);
        hashvo.setValue("state", state);
        var request=new Request({method:'post',onSuccess:examajaxok,functionId:'2020070013'},hashvo);
	}
	
	function examajaxok(outparamters){
		var flag=outparamters.getValue("flag"); 
		if("ok"==flag){
			examPaperForm.action="/train/trainexam/paper.do?b_query=link";
			examPaperForm.submit();
		}else{
			alert(getDecodeStr(outparamters.getValue("mess")));
		}
	}
	
	function exampaperadd(){
		examPaperForm.action="/train/trainexam/paper.do?b_add=link&r5300=";
		examPaperForm.submit();
	}
	function exampaperaedit(r5300){
		examPaperForm.action="/train/trainexam/paper.do?b_add=link&r5300="+r5300;
		examPaperForm.submit();
	}
	function relexamcourse(r5300){
		if(checkIsParent(r5300)){
    		alert("只能操作管理范围内的试卷！");
    		return;
    	}
		location.href="/train/trainexam/paper/relcourse.do?b_relcourse=link&r5300="+r5300;
	}
	function relexamscourses(){
		var a=0;
		var sels="";
		var a_IDs=document.getElementsByName("selectid");		
		for(var i=0;i<document.forms[0].elements.length;i++)
		{			
		   if(document.forms[0].elements[i].type=='checkbox'&&document.examPaperForm.elements[i].name!="selbox")
		   {		   			
				if(document.forms[0].elements[i].checked==true)
				{
					sels+=a_IDs[a].value+",";
				}
				a++;
		   }
	    }
		if(!sels){
    		alert("请选择试卷进行关联操作！");
    		return;
    	}
    	if(checkIsParent(sels)){
    		alert("只能操作管理范围内的试卷！");
    		return;
    	}
		examPaperForm.action="/train/trainexam/paper.do?b_relcourse=link&r5300="+sels;
		examPaperForm.submit();
	}
	function seanch(){
		examPaperForm.action="/train/trainexam/paper.do?b_query=link";
		examPaperForm.submit();
	}
	
	function addquestiontypes(r5300,r5308){
		for(var i=0;i<document.forms[0].elements.length;i++)
		{			
		   if(document.forms[0].elements[i].type=='checkbox'&&document.examPaperForm.elements[i].name!="selbox")
		   {		   			
				document.forms[0].elements[i].checked=false;
		   }
	    }
		if(r5308=="1"){
			examPaperForm.action="/train/trainexam/paper/questiontype.do?b_add=link&r5300="+r5300;
		}else{
			examPaperForm.action="/train/trainexam/paper/autoquestiontype.do?b_add=link&r5300="+r5300;
		}
		examPaperForm.submit();
	}
	function sortItem(type,r5300){
		var hashvo=new ParameterSet();
		hashvo.setValue("flag","1");
		hashvo.setValue("r5300", r5300);
        hashvo.setValue("type", type);
        hashvo.setValue("strwhere", "${examPaperForm.strwhere}");
        var request=new Request({method:'post',onSuccess:examajaxok,functionId:'2020070014'},hashvo);
	}
	function checkIsParent(id){
		var isp = false;
		var hashvo = new ParameterSet();
	    hashvo.setValue("id",id);
	    var request=new Request({method:'post',asynchronous:false,onSuccess:isParent,functionId:'2020070017'},hashvo);
		function isParent(outparamters){
			if(outparamters){
			   var temp1=outparamters.getValue("isParent");
			   if("yes" == temp1)
					isp = true;
		   	}
		} 
	   	return isp;
	}
	function paperspreview(r5300){
		var w=window.screen.width-20;
		var h=window.screen.height-80;
		var url="/train/trainexam/paper/preview/paperspreview.do?b_query=link&r5300="+r5300+"&exam_type=2&flag=1&returnId=0";
		window.open (url, 'newwindow', 'top=0,left=0,toolbar=no,menubar=no,resizable=yes,location=no,status=no,scrollbars=yes,width='+w+',height='+h);
		//window.open("/train/trainexam/paper/preview/paperspreview.do?b_query=link&r5300=${paperQuestionTypeForm.r5300}&exam_type=2&flag=1");
	}
	
	function outContent(content){
		config.FontSize='10pt';//hint提示信息中的字体大小
		Tip(getDecodeStr(content),STICKY,true);
	}
</script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes/>
<%int i=0;%>
<html:form action="/train/trainexam/paper">
<table border="0" cellpadding="0" cellspacing="0">
   <tr style="height: 30px">
   	<td>
   		试卷类型：<span style="vertical-align: middle;">
   		<html:select name="examPaperForm" property="r5307" onchange="seanch();">
   			<html:option value="">全部</html:option>
   			<html:option value="1">考试</html:option>
   			<html:option value="2">作业</html:option>
   		</html:select></span>
   		&nbsp;&nbsp;组卷方式：<span style="vertical-align: middle;">
   		<html:select name="examPaperForm" property="r5308" onchange="seanch();">
   			<html:option value="">全部</html:option>
   			<html:option value="1">手工组卷</html:option>
   			<html:option value="2">自由组卷</html:option>
   		</html:select></span>
   		&nbsp;&nbsp;名称：
   		<html:text name="examPaperForm" styleClass="text4" property="r5301" maxlength="50" />
   		&nbsp;<span style="vertical-align: middle;"><input type="button" class="mybutton" value="查询" onclick="seanch();" /></span>
   	</td>
   </tr>
   <tr>
    <td>
     <div class="fixedDiv2"> 
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id="tableid">
           <tr class="fixedHeaderTr">
             <td align="center" class="TableRow" nowrap width="40" style="border-left: 0px;border-top: none;border-right: none;">&nbsp;
              <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>
             &nbsp;</td>
             <logic:iterate id="info" indexId="index"   name="examPaperForm"  property="itemlist">   
              <logic:equal name="info" property="visible" value="true">
	              <td align="center" class="TableRow" style="border-top:none;border-right: none;" nowrap <logic:equal name="index" value="1">width="80"</logic:equal><logic:equal name="index" value="2">width="80"</logic:equal><logic:equal name="index" value="3">width="80"</logic:equal><logic:equal name="index" value="4">width="80"</logic:equal><logic:equal name="index" value="5">width="80"</logic:equal> >
	                   <bean:write  name="info" property="itemdesc" filter="true"/>&nbsp;              
	              </td>
              </logic:equal>
             </logic:iterate>
             <td align="center" class="TableRow" style="border-top:none;border-right: none;"  nowrap  width="100">
             	操作
             </td>	
             <hrms:priv func_id="3238208">
             <td align="center" class="TableRow" nowrap  width="50" style="border-right: 0px;border-top:none;" >
                  排序
             </td>    	    	    		    
             </hrms:priv>    	        	        
           </tr>

          <hrms:paginationdb id="element" name="examPaperForm" sql_str="examPaperForm.strsql" table="" where_str="examPaperForm.strwhere" columns="examPaperForm.columns" order_by="examPaperForm.order_by" page_id="pagination" pagerows="${examPaperForm.pagerows}" indexes="indexes">
          <bean:define id="tmpr5300"  name="element" property="r5300" />
          <bean:define id="b0110" name="element" property="b0110" />
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
          <%String r5300 = SafeCode.encode(PubFunc.encrypt(tmpr5300.toString())); %>
            <td align="center" class="RecordRow" nowrap style="border-left: 0px;border-top:none;border-right: none;" >
            <%if( "0".equals(QuestionesBo.isQuote(tmpr5300.toString())) ){ %>
            <hrms:coursesortisparent isParent="0" codeid="${b0110}">&nbsp;
               <hrms:checkmultibox name="examPaperForm" property="pagination.select" value="true" indexes="indexes"/>
            	<input type="hidden" name="selectid" value='<%=r5300 %>' />
            </hrms:coursesortisparent>&nbsp;
            <%}else{ %>
            &nbsp;
            <%} %>
            </td>  
           
	         <logic:iterate id="info" indexId="index"   name="examPaperForm"  property="itemlist">  
	         <logic:equal name="info" property="visible" value="true">	
                  <logic:notEqual  name="info" property="itemtype" value="N">               
                    <td align="left" class="RecordRow" style="border-top:none;border-right: none;"  nowrap>        
                  </logic:notEqual>
                  <logic:equal  name="info" property="itemtype" value="N">               
                    <td align="right" class="RecordRow" style="border-top:none;border-right: none;"  nowrap>        
                  </logic:equal>    
                  <logic:equal  name="info" property="codesetid" value="0">   
                    &nbsp; 
                    <logic:equal value="r5307" name="info" property="itemid"><!-- 试卷类型 -->
                    	<logic:equal value="1" name="element" property="${info.itemid}">
                    		考试
                    	</logic:equal>
                    	<logic:equal value="2" name="element" property="${info.itemid}">
                    		作业
                    	</logic:equal>
                    </logic:equal>
                    <logic:equal value="r5308" name="info" property="itemid"><!-- 组卷方法 -->
                    	<logic:equal value="1" name="element" property="${info.itemid}">
                    		手工组卷
                    	</logic:equal>
                    	<logic:equal value="2" name="element" property="${info.itemid}">
                    		自由组卷
                    	</logic:equal>
                    </logic:equal>
                    <logic:equal value="r5000" name="info" property="itemid"><!-- 所属课程 -->
                    	<bean:define id="lessonpaper" name="element" property="r5000"></bean:define>
                    	<logic:equal value="2" name="element" property="r5308"><!-- 自由组卷才可以关联课程 -->
                    	<hrms:coursesortisparent isParent="0" codeid="${b0110}">
                    	  <hrms:priv func_id="3238206">
	                    	<a href='javascript:relexamcourse("<%=r5300 %>");'> 
			          	   		&nbsp;<img src="/images/add.gif" alt="关联课程" border="0">&nbsp;
			            	    </a>
			            	    </hrms:priv>
			                </hrms:coursesortisparent>
                    	&nbsp;<hrms:lessonpaper r5300="${lessonpaper}"></hrms:lessonpaper>&nbsp;
			            </logic:equal>
                    </logic:equal>
                    <logic:notEqual value="r5307" name="info" property="itemid">
                    	<logic:notEqual value="r5308" name="info" property="itemid">
                    		<logic:notEqual value="r5000" name="info" property="itemid">
                    			<bean:write  name="element" property="${info.itemid}" filter="true"/>
                    		</logic:notEqual>
                    	</logic:notEqual>
                    </logic:notEqual>
                    &nbsp;
                  </logic:equal>
                 <logic:notEqual  name="info" property="codesetid" value="0">  
                 <logic:equal name="info" property="codesetid" value="UN">
                     <hrms:codetoname codeid="UN" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel=""/>  	      
          	           &nbsp;  <bean:write name="codeitem" property="codename" />&nbsp; 
                   </logic:equal>
                   <logic:notEqual name="info" property="codesetid" value="UN">
                        <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
          	    	    &nbsp; <bean:write name="codeitem" property="codename" />&nbsp;  
                   </logic:notEqual>                 
                     
          	     </logic:notEqual>  
              
              </logic:equal>
             </logic:iterate>  
             <td align="left" class="RecordRow" style="padding-left:20px;border-top:none;border-right: none;"  nowrap  width="70">
               <hrms:priv func_id="3238202">
                 <a href="###" onclick='exampaperaedit("<%=r5300 %>");' > 
          	       <img src="/images/edit.gif" alt="编辑" border="0">
            	   </a>&nbsp; 
            	 </hrms:priv>            	 
            	               	   
            	 <a href="###" onclick='paperspreview("<%=r5300 %>");'> 
          	   		<img src="/images/view.gif" alt="浏览" border="0">
            	 </a> &nbsp;
            	 
            	  <%if( "0".equals(QuestionesBo.isQuote(tmpr5300.toString())) ){ %>
            	 <hrms:coursesortisparent isParent="0" codeid="${b0110}">
            	   <hrms:priv func_id="3238207">
            	   <logic:notEqual name="element" property="r5311" value="04">
            	     <a href="###" onclick="addquestiontypes('<%=r5300 %>','<bean:write  name="element" property="r5308" filter="true"/>')"> 
          	   		   <img src="/images/add.gif" alt="增加题型" border="0">
            	     </a>
            	    </logic:notEqual>
            	   </hrms:priv>
            	 </hrms:coursesortisparent>
            	 
            	 <%} %>
             </td>
             <hrms:priv func_id="3238208">
	             <td align="center" class="RecordRow" nowrap  width="50" style="border-right: 0px;border-top:none;" >
	             	<bean:define id="start" name="examPaperForm" property="start"/>
	             	<bean:define id="end" name="examPaperForm" property="end"/>
	             	<%if(tmpr5300.toString().equals(start)){ %>
	             		&nbsp;&nbsp;&nbsp;
					      <%} else{%>
					        <a href="javaScript:sortItem('up','<%=r5300 %>');">
						         <img src="/images/up01.gif" width="12" height="17" border=0></a>
					      <%} 
	             	  if(tmpr5300.toString().equals(end)){ 
								%>
									&nbsp;&nbsp;&nbsp;
								<%} else{%>
								<a href="javaScript:sortItem('down','<%=r5300 %>');">
									<img src="/images/down01.gif" width="12" height="17" border=0></a>
								<%} %>
	             </td>
	           </hrms:priv>     	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
        
</table>
</div>
</td></tr>
<tr><td style="padding-right: 5px;">
<table width="100%"  align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            	<hrms:paginationtag name="examPaperForm"
								pagerows="${examPaperForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="examPaperForm" property="pagination" nameId="examPaperForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</td></tr>
<tr style="height: 35px"><td align="left">
	  <hrms:priv func_id="3238201">
			<input type="button" name="b_retrun" value="新增" class="mybutton" onclick="exampaperadd();" />
   	</hrms:priv>
   	<hrms:priv func_id="3238203">
   			<input type="button" name="b_retrun" value="删除" class="mybutton" onclick="exampaperdel();" />
   	</hrms:priv>
   	<hrms:priv func_id="3238204">
   			<input type="button" name="b_retrun" value="发布" class="mybutton" onclick="exampaperpub('04');" />
	</hrms:priv>
	<hrms:priv func_id="3238205">
   			<input type="button" name="b_retrun" value="暂停" class="mybutton" onclick="exampaperpub('09');" />
	</hrms:priv>
	<!-- 
	<hrms:priv func_id="" module_id="">
   			&nbsp;&nbsp;<input type="button" name="b_retrun" value="关联" class="mybutton" onclick="relexamscourses();" />
	</hrms:priv>
	 -->
       </td>
     </tr>     
</table>
</html:form>