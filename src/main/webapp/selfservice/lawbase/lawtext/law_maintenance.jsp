<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.lawbase.LawBaseForm, com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.HashMap" %>
<script language="javascript" src="/js/validateDate.js"></script>
<%int i = 0;
	boolean canquery=false;
%>
<script language="javascript">
	var date_desc;
  
   function setSelectValue()
   {
     if(date_desc)
     {
       date_desc.value=$F("date_box");
       Element.hide("date_panel"); 

     }

   }
   function exeAdd(addStr)
   {
       target_url=addStr;
       window.open(target_url, "_self"); 
   }
   function characterCheck(a_base_id){
	   window.location.href="/selfservice/lawbase/lawtext/law_maintenance.do?b_check=link&a_base_id=" + a_base_id;
   }
   function ajaxcheck(file_id,query)
   {
      var hashvo=new ParameterSet();
      hashvo.setValue("file_id",file_id);    
      hashvo.setValue("query",query); 
      var request=new Request({method:"post",asynchronous:false,onSuccess:showCheckFlag,functionId:"10400201035"},hashvo);
   }
   function showCheckFlag(outparamters)
   {
      var sturt=outparamters.getValue("sturt");
      var file_id=outparamters.getValue("file_id");
      var query=outparamters.getValue("query");
      if(sturt=="false")
      {
        alert("您没有浏览权限，请与管理员联系！");
        return false;
      }
      if(query=="download")
      {
    	  if(!file_id) {
      		alert("文件不存在！");  
      		return;
      	  }
         window.open("/servlet/vfsservlet?fileid="+file_id,"_blank");
      }else if(query=="original")
      {
    	  if(!file_id) {
      		alert("文件不存在！");  
      		return;
      	  }
      	window.open("/servlet/vfsservlet?fileid="+file_id+"&type=original","_blank");
      }
      else if(query=="view")
      {
         location.href="/selfservice/lawbase/lawtext/law_view_base.do?b_query=link&flag=2&a_id="+file_id;
      }else if(query=="affix")
      {
         location.href="/selfservice/lawbase/lawtext/affix_digest.do?b_affix=link&file_id="+file_id;
      }
   }
   function checkdate()
   {
   		var str = document.getElementById("editor1");
   		var str2 = document.getElementById("editor2");
   		if(str&&str.value!="")
  		{
  			if(!(validate(str,"日期","YYYY-MM-DD或YYYY.MM.DD"))){
	  			//alert("输入的日期不正确,日期格式应为:YYYY-MM-DD,请重新输入!");
	  			return false;
	  		}
	  	}
	  	if(str2&&str2.value!="")
  		{
  			if(validate(str2,"日期","YYYY-MM-DD或YYYY.MM.DD")){
			  		if(trim(str.value)>trim(str2.value)){
			  			alert("起始日期不能大于终止日期!");
			  			return false;
			  		}
	  		}
	  		else{
	  			//alert("输入的日期不正确,日期格式应为:YYYY-MM-DD,请重新输入!");
	  			return false;
	  		}
	  	}
		
	  		lawbaseForm.action = "/selfservice/lawbase/lawtext/law_maintenance.do?b_select=link";
	  		lawbaseForm.submit();
	  	
   }
   function IsDigit(e) 
    { 
    	e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
      var key = window.event?e.keyCode:e.which;
    return ((key != 96)); 
    } 
   function showDateSelectBox(srcobj)
   {
      date_desc=document.getElementsByName(srcobj)[0];
      Element.show("date_panel");   
      $("date_box").focus();
      var pos=getAbsPosition(date_desc);
	  with($("date_panel"))
	  {
        style.position="absolute";
        if(navigator.appName.indexOf("Microsoft")!= -1){
			style.posLeft=pos[0]-1;
			style.posTop=pos[1]-1+date_desc.offsetHeight;
			style.width=(date_desc.offsetWidth<150)?150:date_desc.offsetWidth+1;
		}else{
			style.left=pos[0]-1;
			style.top=pos[1]-1+date_desc.offsetHeight;
			style.width=(date_desc.offsetWidth<150)?150:date_desc.offsetWidth+1;
		}
      }                 
   }
  	function _onkeydown(e){
  		e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
      var key = window.event?e.keyCode:e.which;
      if(key==13) remove();
  	}
   function remove()
    {
    	//var aa = eval("date_panel");
    	//aa.style.display = 'none';
    	Element.hide("date_panel");
    }
   
   window.onresize = function(){
		setDivStyle();
	}
   
   window.onload = function() {
	   setDivStyle();
   }

	function setDivStyle(){
		document.getElementById("fixedDiv").style.height = document.body.clientHeight-135;
	    document.getElementById("fixedDiv").style.width = document.body.clientWidth-15; 
	}
</script>

<style>
img {vertical-align: middle;}
</style>
<%
	HashMap itemMap = new HashMap();
	LawBaseForm lawbaseForm = (LawBaseForm)session.getAttribute("lawbaseForm");
	itemMap = lawbaseForm.getItemMap();
%>

<hrms:themes cssName="content.css"></hrms:themes>
<html:form action="/selfservice/lawbase/lawtext/law_maintenance">
<script language="javascript">
	if(!getBrowseVersion()){//兼容非IE浏览器  wangb 20171127
		var form = document.getElementsByName('lawbaseForm')[0]; // 获取form 表单   lawbaseForm变量名冲突   改为 form   wangb 20180130 bug 34324
		var table = form.parentNode.parentNode.parentNode.parentNode;//获取模板 table
		table.style.tableLayout = 'fixed'; // 模板table  添加table-layout 样式  固定大小
	}
</script>
<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
	<thead>
		<tr>
			<td style="padding-bottom: 5px;padding-top: 5px;">
						<span style="vertical-align: middle;">
							<logic:notEmpty name="lawbaseForm" property="colums">
							<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",note_num,">
								<logic:match name="lawbaseForm" property="colums" value=" note_num,">
								<%=itemMap.get("note_num")%>&nbsp;<html:text name="lawbaseForm" property="note_nums"  styleClass="text4" size="10" maxlength="30" onkeypress="return IsDigit(event);"/>
								<%
									canquery=true;
								%>
								</logic:match>
							</logic:notMatch>
								<!-- bean:message key="lable.lawfile.keyword" />&nbsp;
								< html:text name="lawbaseForm" property="keyword" size="10" maxlength="30" onkeypress="event.returnValue=IsDigit();"/>
								-->
							<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",content_type,">
								<logic:match name="lawbaseForm" property="colums" value=" content_type,">
								<%=itemMap.get("content_type")%>&nbsp;<html:text name="lawbaseForm" styleClass="text4" styleId="contenttype" property="contenttype" size="10" maxlength="10" onkeypress="return IsDigit(event);" ondblclick="showDateSelectBox('contenttype');" onkeydown="_onkeydown(event)"/>
								<img  src="/images/code.gif" align="absmiddle" onclick="showDateSelectBox('contenttype');" >
								<%
									canquery=true;
								%>
								</logic:match>
							</logic:notMatch>
							<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_date,">
								<logic:match name="lawbaseForm" property="colums" value=" issue_date,">
								<%=itemMap.get("issue_date")%>&nbsp;<input type="text" name="issuedate" class="text4"  extra="editor" value="${lawbaseForm.issuedate}"style="width:100px;font-size:10pt;text-align:left;"  id="editor1"  dropDown="dropDownDate">至<input type="text" name="enddate" extra="editor" value="${lawbaseForm.enddate}" style="width:100px;font-size:10pt;text-align:left;"  id="editor2"  dropDown="dropDownDate">
								<%
									canquery=true;
								%>
								</logic:match>
							</logic:notMatch>
								<logic:equal name="lawbaseForm" property="basetype" value="5">
									<logic:equal name="lawbaseForm" property="sign" value="1">
										姓名&nbsp;<html:text name="lawbaseForm" styleClass="text4" property="username" size="9" maxlength="30" onkeypress="return IsDigit(event);"/>
										<%
											canquery=true;
										%>
									</logic:equal>
								</logic:equal>
							</logic:notEmpty>
						</span>
						<span style="vertical-align: middle;">						
							<logic:empty name="lawbaseForm" property="colums">
							<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",note_num,">
								<bean:message key="lable.lawfile.notenum" />&nbsp;<html:text name="lawbaseForm" styleClass="text4"  property="note_nums" size="10" maxlength="30" onkeypress="return IsDigit(event);"/>
								<%
									canquery=true;
								%>
							</logic:notMatch>
							<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",content_type,">
								<bean:message key="lable.lawfile.contenttype" />&nbsp;<html:text name="lawbaseForm" styleId="contenttype" styleClass="text4"  property="contenttype" size="10" maxlength="10" onkeypress="return IsDigit(event);" ondblclick="showDateSelectBox('contenttype');" onkeydown="_onkeydown(event)"/>
								<img  src="/images/code.gif" align="absmiddle" onclick="showDateSelectBox('contenttype');" >
								<%
									canquery=true;
								%>
							</logic:notMatch>
							<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_date,">
								<bean:message key="column.law_base.issuedate" />&nbsp;
								<input type="text" name="issuedate" extra="editor"  class="text4" value="${lawbaseForm.issuedate}" style="width:100px;font-size:10pt;text-align:left;"  id="editor1"  dropDown="dropDownDate">至<input type="text" name="enddate" extra="editor" value="${lawbaseForm.enddate}"  class="text4"   style="width:100px;font-size:10pt;text-align:left;" id="editor2"  dropDown="dropDownDate">
								<%
									canquery=true;
								%>
							</logic:notMatch>
								<logic:equal name="lawbaseForm" property="basetype" value="5">
									<logic:equal name="lawbaseForm" property="sign" value="1">
										姓名&nbsp;<html:text name="lawbaseForm" property="username" size="9"  styleClass="text4"  maxlength="30" onkeypress="return IsDigit(event);"/>
										<%
											canquery=true;
										%>
									</logic:equal>
								</logic:equal>
							</logic:empty>
							<bean:message key="lable.lawfile.keyword" />&nbsp;<html:text name="lawbaseForm" property="keyword" size="10" styleClass="text4"  maxlength="30" onkeypress="return IsDigit();"/>
						</span>
						<span id="btnsSpanId" style="vertical-align: bottom;">
							<input type="button" name="b_select" class="mybutton" value="<bean:message key="button.query" />" onclick="checkdate();">
							<input type="button" class="mybutton" value="<bean:message key="law_maintenance.term"/>" onclick="characterCheck('<%=PubFunc.encrypt(request.getParameter("a_base_id"))%>')">
							<input type="button" Class="mybutton" value="<bean:message key="law_maintenance.globalsearch"/>" onclick="exeAdd('./globalsearch.jsp?encryptParam=<%=PubFunc.encrypt("a_base_id=" + request.getParameter("a_base_id"))%>')">
						</span>
			</td>
		</tr>
	</thead>
</table><div class="fixedDiv2" id='fixedDiv'>
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
		<thead>
			<tr class="fixedHeaderTr">
			<logic:empty name="lawbaseForm" property="colums">
				<td align="center" class="TableRow" style="border-left: none;border-top: none;" nowrap>
				&nbsp;
					<bean:message key="column.law_base.title" />
					&nbsp;
				</td>
				<logic:equal name="lawbaseForm" property="basetype" value="5">
					<logic:equal name="lawbaseForm" property="sign" value="1">
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",originalfile,">
					<td align="center" class="TableRow" style="border-left: none;border-top: none;"  nowrap>
					&nbsp;
						<bean:message key="column.law_base.original" />
						&nbsp;
					</td>
					</logic:notMatch>
					</logic:equal>
				</logic:equal>
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",note_num,">
				<td align="center" class="TableRow" style="border-left: none;border-top: none;"  nowrap>
				&nbsp;
					<bean:message key="column.law_base.notenum" />
					&nbsp;
				</td>
				</logic:notMatch>
				
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_date,">
				<td align="center" class="TableRow" style="border-left: none;border-top: none;"  nowrap>
				&nbsp;
					<bean:message key="column.law_base.issuedate" />
					&nbsp;
				</td>
				</logic:notMatch>
				
					<logic:notEqual name="lawbaseForm" property="basetype" value="5">
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",type,">           						
				<td align="center" class="TableRow" style="border-left: none;border-top: none;"  nowrap>
					<bean:message key="lable.lawfile.typenum" />
				</td>
				</logic:notMatch>
				
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",valid,">           						
				<td align="center" class="TableRow" style="border-left: none;border-top: none;"  nowrap>
					<bean:message key="lable.lawfile.valid" />
				</td>
				</logic:notMatch>
				</logic:notEqual>
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",content_type,">           						
				<td align="center" class="TableRow" style="border-left: none;border-top: none;"  nowrap>
					<bean:message key="lable.lawfile.contenttype" />
				</td>
				</logic:notMatch>
				
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_org,">           						
				<td align="center" class="TableRow" style="border-left: none;border-top: none;"  nowrap>
					<bean:message key="lable.lawfile.issue_org" />
				</td>
				</logic:notMatch>
				
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",name,">           						
				<td align="center" class="TableRow" style="border-left: none;border-top: none;"  nowrap>
					<bean:message key="column.law_base.filename" />
				</td>
				</logic:notMatch>
				<logic:notEqual name="lawbaseForm" property="basetype" value="5">
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",valid_date,">           						
				<td align="center" class="TableRow" style="border-left: none;border-top: none;"  nowrap>
					<bean:message key="lable.lawfile.invalidationdate" />
				</td>
				</logic:notMatch>
				
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",notes,">           						
				<td align="center" class="TableRow" width="30" style="border-left: none;border-top: none;"  nowrap>
					<bean:message key="lable.lawfile.note" />
				</td>
				</logic:notMatch>
				
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",digest,">           						
				<td align="center" class="TableRow" style="border-left: none;border-top: none;"  nowrap>
					<bean:message key="label.law_base.affixdigest" />
				</td>
				</logic:notMatch>								
				</logic:notEqual>
				
			
				<logic:notEqual name="lawbaseForm" property="basetype" value="5">
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",implement_date,">
					<td align="center" class="TableRow" style="border-left: none;border-top: none;"  nowrap>
					&nbsp;
						<bean:message key="column.law_base.impdate" />
						&nbsp;
					</td>
					</logic:notMatch>
				</logic:notEqual>
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",viewcount,">
				<td align="center" class="TableRow" style="border-left: none;border-top: none;"  nowrap>
				&nbsp;
					浏览次数
					&nbsp;
				</td>
				</logic:notMatch>
				
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",extfile,">
	                <td align="center" class="TableRow" style="border-left: none;border-top: none;border-right: none;"  nowrap>
						&nbsp;
						附件
						&nbsp;
					</td>
				</logic:notMatch>
				</logic:empty> 
				<logic:notEmpty name="lawbaseForm" property="colums">
				 <logic:iterate id="item" name="lawbaseForm" property="fieldlist">
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",${item.itemid },">           						
					<td align="center" class="TableRow" style="border-left: none;border-top: none;"  nowrap>
					&nbsp;
						<bean:write name="item" property="itemdesc"/>
						&nbsp;
					</td>
					</logic:notMatch>
				</logic:iterate>
				</logic:notEmpty> 
			</tr>
		</thead>
		<hrms:extenditerate id="element" name="lawbaseForm" property="lawbaseForm.list" indexes="indexes" pagination="lawbaseForm.pagination" pageCount="${lawbaseForm.pagerows}" scope="session">
			
				<%if (i % 2 == 0) {

			%>
				<tr class="trShallow">
					<%} else {%>
				<tr class="trDeep">
					<%}
			i++;

			%>
			<logic:empty name="lawbaseForm" property="colums">
                   <td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>                        
                        &nbsp;
                        <logic:notEqual name="element" property="ext" value="">
                   		    <a href="###" onclick="ajaxcheck('<bean:write name="element" property="file_id" filter="true"/>','view');">
							    <bean:write name="element" property="title" filter="false" />
						    </a>
						</logic:notEqual>
						<logic:equal name="element" property="ext" value="">
					        <bean:write name="element" property="title" filter="false" />
				        </logic:equal>
				        &nbsp;
					</td>
					<logic:equal name="lawbaseForm" property="basetype" value="5">
						<logic:equal name="lawbaseForm" property="sign" value="1">
						<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",originalfile,">
							<td align="center" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
							<logic:notEmpty name="element" property="originalext">
								<a href="###" onclick="ajaxcheck('<bean:write name="element" property="file_id" filter="true"/>','original');"><img src="/images/view.gif" border=0></a>&nbsp;
							</logic:notEmpty>&nbsp;
							</td>
						</logic:notMatch>
						</logic:equal>
					</logic:equal>
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",note_num,">
					<td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
						<bean:write name="element" property="note_num" filter="true" />
						&nbsp;
					</td>
					</logic:notMatch>
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_date,">
					<td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
						<bean:write name="element" property="issue_date" filter="true" />
						&nbsp;
					</td>
					</logic:notMatch>
					<logic:notEqual name="lawbaseForm" property="basetype" value="5">
					 <logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",type,">
                    <td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
						<bean:write name="element" property="type" filter="true" />
						&nbsp;
					</td>
					</logic:notMatch>					    
				    
				     <logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",valid,">
                    <td align="right" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
						<logic:equal name="element" property="valid" value="1">
					 	   	 <bean:message key="lable.lawfile.availability"/>
					 	 </logic:equal>
					 	 <logic:equal name="element" property="valid" value="0">
					 	    <bean:message key="lable.lawfile.invalidation"/>
					 	 </logic:equal>
					 	 <logic:equal name="element" property="valid" value="2">
					 	    <bean:message key="lable.lawfile.nowmodify"/>
					 	 </logic:equal>
					 	 <logic:equal name="element" property="valid" value="3">
					 	    <bean:message key="lable.lawfile.other"/>
					 	 </logic:equal>	  	&nbsp;
					</td>
					</logic:notMatch>					  
					</logic:notEqual>
					 <logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",content_type,">
                    <td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
						<bean:write name="element" property="content_type" filter="true" />
						&nbsp;
					</td>
					</logic:notMatch>						 									
				
				    <logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_org,">
                    <td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
						<bean:write name="element" property="issue_org" filter="true" />
						&nbsp;
					</td>
					</logic:notMatch>				
					
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",name,">
                    <td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
						<bean:write name="element" property="name" filter="true" />
						&nbsp;
					</td>
					</logic:notMatch>
					<logic:notEqual name="lawbaseForm" property="basetype" value="5">										
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",valid_date,">
                    <td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
						<bean:write name="element" property="valid_date" filter="true" />
						&nbsp;
					</td>
					</logic:notMatch>				
					
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",notes,">
                    <td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
						<bean:write name="element" property="notes" filter="true" />
						&nbsp;
					</td>
					</logic:notMatch>						
										
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",digest,">
                    <td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
						<bean:write name="element" property="digest" filter="true" />
						&nbsp;
					</td>
					</logic:notMatch>										
					
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",implement_date,">
                    <td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
						<bean:write name="element" property="implement_date" filter="true" />
						&nbsp;
					</td>
					</logic:notMatch>								  					
											
				</logic:notEqual>
				
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",viewcount,">
                    <td align="right" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
						<bean:write name="element" property="viewcount" filter="true" />
						&nbsp;
					</td>
					</logic:notMatch>
						<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",extfile,">
							<logic:equal name="element" property="choice" value="1"> 
							<td align="left" class="RecordRow" style="border-left: none;border-top: none"  nowrap>&nbsp;
							 <a href="###" onclick="ajaxcheck('<bean:write name="element" property="file_id" filter="true"/>','affix');">附件</a>&nbsp;
						        </td>
						   </logic:equal>
						   <logic:notEqual name="element" property="choice" value="1"> 
							<td align="left" class="RecordRow" style="border-left: none;border-top: none;border-right: none;"  nowrap>
							 &nbsp;
						        </td>
						   </logic:notEqual>  
					   </logic:notMatch>
				 </logic:empty>
				 <logic:notEmpty name="lawbaseForm" property="colums">
				 <logic:iterate id="item" name="lawbaseForm" property="fieldlist">
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",${item.itemid },">           						
					<logic:equal value="originalext" name="item" property="itemid">
						<td align="center" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
							<logic:notEqual name="element" property="originalext" value=" ">
							<logic:notEmpty name="element" property="originalext">
								<a href="###" onclick="ajaxcheck('<bean:write name="element" property="file_id" filter="true"/>','original');"><img src="/images/view.gif" border=0></a>&nbsp;
							</logic:notEmpty>
							</logic:notEqual>
							&nbsp;
						</td>
					</logic:equal>
					<logic:equal value="ext" name="item" property="itemid">
					<td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
						<logic:empty name="element" property="ext">
							
							&nbsp;
	                   </logic:empty>
	                   <logic:notEqual name="element" property="ext" value=" ">
	                   <logic:notEmpty  name="element" property="ext">
	                   		<a href="###" onclick="ajaxcheck('<bean:write name="element" property="file_id" filter="true"/>','download');">
								文件
							</a>&nbsp;
	                   </logic:notEmpty>
	                   </logic:notEqual>
					</td>
					</logic:equal>
					<logic:equal value="name" name="item" property="itemid">
					<td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
						<logic:empty name="element" property="ext">
							<FONT color="black"><bean:write name="element" property="name" filter="false" /></FONT>
							&nbsp;
	                   </logic:empty>
	                   <logic:notEqual name="element" property="ext" value=" ">
	                   <logic:notEmpty  name="element" property="ext">
	                   		<a href="###" onclick="ajaxcheck('<bean:write name="element" property="file_id" filter="true"/>','view');">
								<bean:write name="element" property="name" filter="false" />
							</a>&nbsp;
	                   </logic:notEmpty>
	                   </logic:notEqual>
					</td>
					</logic:equal>
					<logic:equal value="title" name="item" property="itemid">
					<td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
	                   		<a href="###" onclick="ajaxcheck('<bean:write name="element" property="file_id" filter="true"/>','view');">
								<bean:write name="element" property="title" filter="false" />
							</a>&nbsp;
					</td>
					</logic:equal>
					
					<logic:equal value="extfile" name="item" property="itemid">
							<logic:equal name="element" property="choice" value="1"> 
							<td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
							 <a href="###" onclick="ajaxcheck('<bean:write name="element" property="file_id" filter="true"/>','affix');">附件</a>&nbsp;
						        </td>
						   </logic:equal>
						   <logic:notEqual name="element" property="choice" value="1"> 
							<td align="left" class="RecordRow"style=" border-left: none;border-top: none;" nowrap>
							 &nbsp;
						        </td>
						   </logic:notEqual> 
					</logic:equal>
					
					<logic:notEqual value="originalext" name="item" property="itemid">
					<logic:notEqual value="ext" name="item" property="itemid">
					<logic:notEqual value="name" name="item" property="itemid">
					<logic:notEqual value="title" name="item" property="itemid">
					<logic:notEqual value="extfile" name="item" property="itemid">
					
					<logic:equal value="0"  name="item" property="codesetid">
					<td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
						<bean:write name="element" property="${item.itemid }" filter="false" />
						&nbsp;
					</td>
					</logic:equal>
					<logic:notEqual value="0" name="item" property="codesetid">
					<td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;
						<hrms:codetoname codeitem="codeitem" codeid="${item.codesetid }" name="element" codevalue="${item.itemid}"  scope="page"/>
						<bean:write name="codeitem" property="codename" />&nbsp; 
					</td>
					</logic:notEqual>
					</logic:notEqual>
					</logic:notEqual>
					</logic:notEqual>
					</logic:notEqual>
					</logic:notEqual>
					</logic:notMatch>
				</logic:iterate>
				</logic:notEmpty> 
				</tr>			
		</hrms:extenditerate>

	</table>
</div>
	<table width="100%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
		    	<hrms:paginationtag name="lawbaseForm" pagerows="${lawbaseForm.pagerows}" property="lawbaseForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="lawbaseForm" property="lawbaseForm.pagination" nameId="lawbaseForm" propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>

	<table width="98%" align="left" style="margin-top: 5px;">
		<tr >
			<td align="left">
				<input type="button" class="mybutton" value="<bean:message key="law_maintenance.term"/>" onclick="characterCheck('<%=PubFunc.encrypt(request.getParameter("a_base_id"))%>')">
				<input type="button" Class="mybutton" value="<bean:message key="law_maintenance.globalsearch"/>" onclick="exeAdd('./globalsearch.jsp?encryptParam=<%=PubFunc.encrypt("a_base_id=" + request.getParameter("a_base_id"))%>')">
				<logic:equal value="dxt" name="lawbaseForm" property="returnvalue">
				<!--        
                   <hrms:tipwizardbutton flag="law" target="il_body" formname="lawbaseForm"></hrms:tipwizardbutton>
                 -->
                   <input type="button" name="b_retrun" value="返回" class="mybutton" onclick="hrbreturn('law','il_body','lawbaseForm');" />
                </logic:equal>
                <logic:equal name="lawbaseForm" property="returnvalue" value="zdxt">
					<input type="button" name="b_retrun" value="返回" class="mybutton" onclick="hrbreturn('selfinfo','il_body','lawbaseForm');" />
				</logic:equal>
			</td>
		</tr>
	</table>
	<logic:notEmpty name="lawbaseForm" property="colums">
	<logic:match name="lawbaseForm" property="colums" value="content_type,">
	<div id="date_panel" style="display:none;" onblur="$('date_panel').style.display='none';">
	 	<html:select styleId ="date_box" name="lawbaseForm" multiple="multiple" style="width:100" size="6" property="contenttype" onchange="setSelectValue();" onblur="$('date_panel').style.display='none';" onclick="setSelectValue();">    
			<html:optionsCollection property="contentlist" value="dataValue" label="dataName"/>	
		</html:select>
	 </div>
	 </logic:match>
	 </logic:notEmpty>
	 <logic:empty  name="lawbaseForm" property="colums">
	 <div id="date_panel" style="display:none;">
	 	<html:select styleId ="date_box" name="lawbaseForm" multiple="multiple" style="width:100" size="6" property="contenttype" onchange="setSelectValue();"  onblur="$('date_panel').style.display='none';" onclick="setSelectValue();">    
			<html:optionsCollection property="contentlist" value="dataValue" label="dataName"/>	
		</html:select>
	 </div>
	 </logic:empty>
</html:form>

<%if (request.getAttribute("fileexterror") == null
					|| request.getAttribute("fileexterror").toString().equals(
							"")) {
			} else {

				%>
<script>
   alert('操作不成功,必须选doc,xls,txt类型的文件');
   </script>
<%request.setAttribute("fileexterror", "");
			}

		%>

<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script>
var htnObjs = document.getElementById("btnsSpanId");//add by xiegh bug:35537 date20180304
if(!getBrowseVersion()){//兼容非IE浏览器  wangb 20180130
	var fixedDiv2 = document.getElementsByClassName('fixedDiv2')[0];//table高度 与IE下 不一致 调整table 高度  wangb 20180130 34319
	fixedDiv2.style.height = '560px';
	htnObjs.style.verticalAlign="-webkit-baseline-middle";
	var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
	var isSafari = userAgent.indexOf("Safari") > -1; //判断是否Safari浏览器
	if(isSafari){
		var btnsSpanId = document.getElementById('btnsSpanId');
		var btns = btnsSpanId.getElementsByTagName('input');
		for(var i = 0 ; i < btns.length ; i++){
			btns[i].style.position = "relative";
			btns[i].style.bottom = "3px";
		}
	}
} else
	htnObjs.style.verticalAlign="bottom"; 
</script>