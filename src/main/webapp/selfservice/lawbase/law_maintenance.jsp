<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<jsp:useBean id="lawBaseForm" class="com.hjsj.hrms.actionform.lawbase.LawBaseForm" scope="session" />
<%
 int i=0;
 String law_file_priv=SystemConfig.getPropertyValue("law_file_priv");
 law_file_priv=law_file_priv.trim();
%>
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
<script language="javascript">
   function checkValid(base_id) {
      if (base_id == "" || base_id == "null" || base_id == "ALL") {
      	if(${lawbaseForm.basetype}=="4")
      		alert("不能在根节点添加知识制度!");
      	else if(${lawbaseForm.basetype}=="1")
        	alert("<bean:message key="errors.law_maintenance.add"/>");
        else if(${lawbaseForm.basetype}=="5")
        	alert("不能在根节点添加文件文档!");	
      } else {
        lawbaseForm.action = "/selfservice/lawbase/law_maintenance.do?b_add=link&status=${lawbaseForm.status}";
        lawbaseForm.submit();
      }
   }
   function insertValue(file_id,base_id)
   {
   	if(base_id == "" || base_id == "null" || base_id == "ALL"){
   		alert("不能在根节点插入知识制度!");
   	}else{
   		lawbaseForm.action = "/selfservice/lawbase/law_maintenance.do?b_add=link&status=${lawbaseForm.status}&isInsert=1&file_id="+file_id;
        lawbaseForm.submit();
    }
   }
   
  // parent.mil_menu.flg.value = "<bean:write name="lawbaseForm" property="flg"/>";
  function assign_role()
  {
     var len=document.lawbaseForm.elements.length;
     var isCorrect=false;
     for (i=0;i<len;i++)
     {
           if (document.lawbaseForm.elements[i].type=="checkbox"&&document.lawbaseForm.elements[i].name!='box')
            {
              if( document.lawbaseForm.elements[i].checked==true)
                isCorrect=true;
            }
     }
    if(!isCorrect)
    {
    	if(${lawbaseForm.basetype}=="4")
    		alert("请选择知识制度!");
    	else if(${lawbaseForm.basetype}=="1") 
        	alert("请选择制度政策！");
        else if(${lawbaseForm.basetype}=="5")
        	alert("请选择文件档案!");
        return false;
     }
     var target_url;
     var winFeatures = "dialogHeight:400px; dialogLeft:320px;"; 
     target_url="/selfservice/lawbase/add_law_text_role.do?b_add=link";
     var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:600px; dialogHeight:600px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
     if(!return_vo)
		return false;	
	 if(return_vo.role_id==""||return_vo.role_id=="undefined")
	    return false;
     lawbaseForm.action = "/selfservice/lawbase/add_law_text_role.do?b_save=link&a_base_ids="+$URL.encode(return_vo.role_id);
     lawbaseForm.submit();
  }
  function query_role_assign(a_id)
  {
	   var target_url="/selfservice/lawbase/lawtext/law_onetext_role.do?b_query=link`a_id="+a_id;
       //newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=500,height=404'); 
  		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
		  		var return_vo= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:600px; dialogHeight:600px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
		        if(return_vo!=null){
		        }
  }
  function relating_person(a_id)
  {
  	//var target_url="/selfservice/lawbase/add_law_text_role.do?b_relating=link&a_id="+a_id;
  	//newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=560,height=370'); 
  	var target_url="/selfservice/lawbase/add_law_text_role.do?b_relating=link`a_id="+a_id;
  	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);

  	var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:600px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
  }
  function delete_role_assign(file_id)
  {
      var hashvo=new ParameterSet();
      hashvo.setValue("file_id",file_id); 
      var request=new Request({method:'post',asynchronous:false,onSuccess:delete_role_Show,functionId:'10400201035'},hashvo);
  }
  function delete_role_Show(outparamters)
  {
      var sturt=outparamters.getValue("sturt");
      var a_id=outparamters.getValue("file_id");     
      if(sturt=="false")
      {
        alert("您没有浏览权限，请与管理员联系！");
        return false;
      }else
      { 
         var target_url="/selfservice/lawbase/update_law_onetext_role.do?b_query=link`a_id="+a_id;
         //newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=500,height=404'); 
      	 var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
		  		var return_vo= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:600px; dialogHeight:600px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
		        if(return_vo!=null){
		        }
      }
  }
  function deletes()
  {
     var len=document.lawbaseForm.elements.length;
     var isCorrect=false;
     for (i=0;i<len;i++)
     {
           if (document.lawbaseForm.elements[i].type=="checkbox"&&document.lawbaseForm.elements[i].name!='box')
            {
              if( document.lawbaseForm.elements[i].checked==true)
                isCorrect=true;
            }
     }
    if(!isCorrect)
    {
    	if(${lawbaseForm.basetype}=="1")
          	alert("请选择制度政策！");
        else if(${lawbaseForm.basetype}=="4")  	
        	alert("请选择知识制度!");
        else if(${lawbaseForm.basetype}=="5")
        	alert("请选择文件档案!");
          return false;
     }
     if(confirm("确认要删除所选中的文件吗？"))
     {
          lawbaseForm.action = "/selfservice/lawbase/law_maintenance.do?b_delete=link";
          lawbaseForm.submit();
     }
  }
  function ajaxcheck(file_id,query)
   {
      var hashvo=new ParameterSet();
      hashvo.setValue("file_id",file_id);    
      hashvo.setValue("query",query); 
      var request=new Request({method:'post',asynchronous:false,onSuccess:showCheckFlag,functionId:'10400201035'},hashvo);
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
      if(query=="download") {
    	  if(!file_id) {
    		alert("文件不存在！");  
    		return;
    	  }
    	  
         window.open("/servlet/vfsservlet?fileid="+file_id,"_blank");
      }else if(query=="affix")
      {
         location.href="/selfservice/lawbase/affix.do?b_query=link&file_id="+file_id+"&basetype=${lawbaseForm.basetype}";
      }else if(query=="update")
      {
         location.href="/selfservice/lawbase/law_into_base.do?b_query=link&a_id="+file_id;
      }else if(query=="original") {
    	  if(!file_id) {
      		alert("文件不存在！");  
      		return;
      	  }
      	window.open("/servlet/vfsservlet?fileid="+file_id,"_blank");
      }else if(query=="view")
      {
         location.href="/selfservice/lawbase/lawtext/law_view_base.do?b_query=link&flag=2&type=view&a_id="+file_id;
      }
   }
   
  function adjust_order(base_id){
  	if (base_id == "" || base_id == "null" || base_id == "ALL") {
      	alert("不能在根节点排序!");
    } else {
  		target_url="/selfservice/lawbase/adjust_order.do?b_order=link`base_id=${lawbaseForm.base_id}`basetype=${lawbaseForm.basetype}";
  		//newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=300,width=596,height=354'); 
  		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
  		var return_vo= window.showModalDialog(iframe_url,null, 
        					"dialogWidth:520px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no;scrollbars:no");
       	var currnode=parent.frames['mil_menu'].Global.selectedItem;
       	window.location.href=currnode.action;
  	}
  }
  
  function clear_allrole(){
  	var mess = "您确定要初始化所有文档吗？";
  	if(confirm(mess)){
  		var waitInfo=eval("wait");	   
		waitInfo.style.display="block";	
  		lawbaseForm.action =  "/selfservice/lawbase/law_maintenance.do?b_delallrole=link";
  		lawbaseForm.submit();
  	}
  }
  function MusterInitData()
   {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
   }
   
  function selectAll()
   {
   		var len = document.lawbaseForm.elements.length;
   		for(i=0;i<len;i++)
   		{
   			if(document.lawbaseForm.elements[i].type=="checkbox")
   				document.lawbaseForm.elements[i].checked=true;
   		}
   }
   function deleteAll()
   {
   		var len = document.lawbaseForm.elements.length;
   		for(i=0;i<len;i++)
   		{
   			if(document.lawbaseForm.elements[i].type=="checkbox")
   				document.lawbaseForm.elements[i].checked=false;
   		}
   }
   function IsDigit() 
   { 
   	 return ((event.keyCode != 96)); 
   }
   function check(base_id)
   {
	   	if (base_id == "" || base_id == "null" || base_id == "ALL") {
    	  	alert("不能在根节点排序!");
    	}
    	else
    	{
    		lawbaseForm.action =  "/selfservice/lawbase/law_maintenance.do?b_quickorder=link";
  			lawbaseForm.submit();
    	}
   }
   function afresh_range()
   {
   		var len=document.lawbaseForm.elements.length;
	    var isCorrect=false;
	    for (i=0;i<len;i++)
	    {
	          if (document.lawbaseForm.elements[i].type=="checkbox"&&document.lawbaseForm.elements[i].name!='box')
	           {
	             if( document.lawbaseForm.elements[i].checked==true)
	               isCorrect=true;
	           }
	    }
	    if(!isCorrect)
	    {
	    	if(${lawbaseForm.basetype}=="1")
	          	alert("请选择制度政策！");
	        else if(${lawbaseForm.basetype}=="4")  	
	        	alert("请选择知识制度!");
	        else if(${lawbaseForm.basetype}=="5")
	        	alert("请选择文件档案!");
	          return false;
	    }
	    var aa = ${lawbaseForm.basetype};
     	var return_vo= window.showModalDialog("Rangetree.jsp",aa, 
     	   "dialogWidth:500px; dialogHeight:350px;resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes");
     	if(return_vo==null)
     		return;
     	lawbaseForm.action = "/selfservice/lawbase/law_maintenance.do?b_rangeafresh=link&a_base_ids="+return_vo;
     	lawbaseForm.submit();
   }
   
   function excecuteExcel(base_id)
   {
	var hashvo=new ParameterSet();			
	hashvo.setValue("basetype","${lawbaseForm.basetype}");
	hashvo.setValue("base_id",base_id);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showExcel,functionId:'10400201048'},hashvo);
   }	
   function showExcel(outparamters)
   {
	var url=outparamters.getValue("excelfile");	
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url,"excel");
   }
   
   function showhidden(){
		var thecodeurl ="/selfservice/lawbase/law_maintenance.do?b_view_hide=link"; 
	    var return_vo= window.showModalDialog(thecodeurl, "", 
	              "dialogWidth:400px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
	    if(return_vo&&return_vo!=null){
	    	/*var _href=parent.mil_body.location.href;
	    	var _index = _href.indexOf('&viewhide${lawbaseForm.basetype}');
	    	if(_index>0)
	    		_href = _href.substring(0,_index);*/
	    	parent.mil_body.location.href="/selfservice/lawbase/law_maintenance.do?b_query=link&&viewhide${lawbaseForm.basetype}="+return_vo;
	    }
	}
</script>
<style>
.myfixedDiv
{ 
	overflow:auto; 
	width:expression(document.body.clientWidth-10); 
    border-bottom: #99BBE8 1pt solid; 
	border-left: #99BBE8 1pt solid; 
	border-right: #99BBE8 1pt solid; 
	border-top: #99BBE8 1pt solid;
}
</style>
<bean:define id="flgs" name="lawbaseForm" property="base_id"/>
<%
	String flg=PubFunc.decrypt(flgs.toString());
%>
<input type="hidden" id="flg" value="<%=flg %>a"/>
<html:form action="/selfservice/lawbase/law_maintenance">
	<logic:equal name="lawbaseForm" property="basetype" value="5">
	<!-- 【7577】文档维护界面有些内容没对齐，  jingq upd 2015.2.28 -->
	<table width="100%" cellspacing="0" align="center" style="margin-top: -2px;">
		<tr align="right">
			<td align="right"  class="tdFontcolor" colspan="2" style="padding-right:0;">
			
				<FONT color='black' style="vertical-align: middle;">按</FONT>
				<html:select  property="order_name" size="1"  style="width:75">
				<logic:notEmpty name="lawbaseForm" property="colums">
					<logic:match name="lawbaseForm" property="colums" value=" title,">           						
					<html:option value="title"><bean:message key="lable.lawfile.title" /></html:option>
					</logic:match>
					<logic:match name="lawbaseForm" property="colums" value=" content_type,">  
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",content_type,">           						
							<html:option value="content_type"><bean:message key="lable.lawfile.contenttype" /></html:option>
					</logic:notMatch>
					</logic:match>
					<logic:match name="lawbaseForm" property="colums" value=" note_num,">  
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",note_num,">           						
							<html:option value="note_num"><bean:message key="lable.lawfile.notenum" /></html:option>
					</logic:notMatch>
					</logic:match>
					<logic:match name="lawbaseForm" property="colums" value=" issue_date,">  
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_date,">           						
							<html:option value="issue_date"><bean:message key="lable.lawfile.printmandate" /></html:option>>
					</logic:notMatch>
					</logic:match>
				</logic:notEmpty>
				<logic:empty name="lawbaseForm" property="colums">
					<html:option value="title"><bean:message key="lable.lawfile.title" /></html:option>
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",content_type,">           						
							<html:option value="content_type"><bean:message key="lable.lawfile.contenttype" /></html:option>
					</logic:notMatch>
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",note_num,">           						
							<html:option value="note_num"><bean:message key="lable.lawfile.notenum" /></html:option>
					</logic:notMatch>
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_date,">           						
							<html:option value="issue_date"><bean:message key="lable.lawfile.printmandate" /></html:option>>
					</logic:notMatch>
				</logic:empty>
				</html:select>
				<html:select property="order_type" size="1"  style="width:50">
					<html:option value="asc"><bean:message key="label.query.sortBase" /></html:option>
					<html:option value="desc"><bean:message key="label.query.sortDesc" /></html:option>
				</html:select>
				
				<input type="button" style="margin-right:0;" name="b_quickorder" class="mybutton" value="<bean:message key="label.zp_exam.sort" />" onclick="check('<%=flg %>')" />
			</td>
		</tr>
	</table>
	</logic:equal>
<div id="divid" class="myfixedDiv " style="padding: 0;">

	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" style="border-collapse: collapse;">
		<thead>
			<tr>
				<td align="center" class="TableRow" style="border-left: none;border-top:none;" width="30" nowrap>
					<input type="checkbox" name="box" id="checkAll" onclick="batch_select(this,'lawbaseForm.select');" title='<bean:message key="button.all.select"/>'/>
				</td>
				<td align="center" class="TableRow" style="border-top:none;" nowrap>
					插入
				</td>
				<logic:notEmpty name="lawbaseForm" property="colums">
				<logic:iterate id="item" name="lawbaseForm" property="fieldlist">
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",${item.itemid },">           						
					<td align="center" class="TableRow" style="border-top:none;" nowrap>
						
						<bean:write name="item" property="itemdesc"/>
						
					</td>
					</logic:notMatch>
				</logic:iterate>
				</logic:notEmpty>
				<logic:empty name="lawbaseForm" property="colums">
				<logic:equal name="lawbaseForm" property="basetype" value="5">
					<logic:equal name="lawbaseForm" property="sign" value="1">
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",originalfile,">           						
					<td align="center" class="TableRow" style="border-top:none;"  nowrap>
						<bean:message key="column.law_base.original" />
					</td>
					</logic:notMatch>
					</logic:equal>
				</logic:equal>
				
				<td align="center" style="border-top:none;"  class="TableRow" nowrap>
					<bean:message key="column.law_base.title" />
				</td>
																
				<logic:notEqual name="lawbaseForm" property="basetype" value="5">
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",type,">           						
				<td align="center" class="TableRow" width="70" style="border-top:none;"  nowrap>
					<bean:message key="lable.lawfile.typenum" />
				</td>
				</logic:notMatch>
				</logic:notEqual>	
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",content_type,">           						
				<td align="center" class="TableRow" width="70" style="border-top:none;"  nowrap>
					<bean:message key="lable.lawfile.contenttype" />
				</td>
				</logic:notMatch>
				<logic:notEqual name="lawbaseForm" property="basetype" value="5">
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",valid,">           						
				<td align="center" class="TableRow" width="70" style="border-top:none;"  nowrap>
					<bean:message key="lable.lawfile.valid" />
				</td>
				</logic:notMatch>
				</logic:notEqual>	
				
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",note_num,">           						
					<td align="center" class="TableRow" style="border-top:none;"  nowrap>
					<bean:message key="column.law_base.notenum" />
					</td>
				</logic:notMatch>						
				
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_org,">           						
				<td align="center" class="TableRow" width="70" style="border-top:none;"  nowrap>
					<bean:message key="lable.lawfile.issue_org" />
				</td>
				</logic:notMatch>
				
				<logic:notEqual name="lawbaseForm" property="basetype" value="5">
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",notes,">           						
				<td align="center" class="TableRow" width="50" style="border-top:none;"  nowrap>
					<bean:message key="lable.lawfile.note" />
				</td>
				</logic:notMatch>
				</logic:notEqual>
				
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_date,">           						
				<td align="center" class="TableRow" width="70" style="border-top:none;"  nowrap>
					<bean:message key="column.law_base.issuedate" />
				</td>
				</logic:notMatch>
				
					<logic:notEqual name="lawbaseForm" property="basetype" value="5">
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",implement_date,">           						
					<td align="center" class="TableRow" width="70" style="border-top:none;"  nowrap>
						<bean:message key="column.law_base.impdate" />
					</td>
					</logic:notMatch>
				</logic:notEqual>
				
				<logic:notEqual name="lawbaseForm" property="basetype" value="5">
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",valid_date,">           						
				<td align="center" class="TableRow" width="70" style="border-top:none;"  nowrap>
					<bean:message key="lable.lawfile.invalidationdate" />
				</td>
				</logic:notMatch>	
				</logic:notEqual>	
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",name,">           						
				<td align="center" class="TableRow" width="70" style="border-top:none;"  nowrap>
					<bean:message key="column.law_base.filename" />
				</td>
				</logic:notMatch>															
				<logic:notEqual name="lawbaseForm" property="basetype" value="5">
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",digest,">           						
				<td align="center" class="TableRow" width="70" style="border-top:none;"  nowrap>
					<bean:message key="label.law_base.affixdigest" />
				</td>
				</logic:notMatch>
				</logic:notEqual>				
				
				<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",viewcount,">           						
				<td align="center" class="TableRow" width="70" style="border-top:none;"  nowrap>
					<bean:message key="lable.lawfile.viewcount" />
				</td>
				</logic:notMatch>
			
				
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",extfile,">           						
					<td align="center" class="TableRow" style="border-top:none;" width="30" nowrap>
						附件
					</td>
					</logic:notMatch>
				 </logic:empty>
				 <td align="center" class="TableRow" style="border-top:none;"  nowrap>
					<bean:message key="label.edit" />
				</td>
				<logic:equal name="lawbaseForm" property="basetype" value="5">
					<logic:equal name="lawbaseForm" property="sign" value="1">
					<td align="center" width="70px" class="TableRow" style="border-top:none;border-right:none;"  nowrap>
						关联人员
					</td>
					</logic:equal>
				</logic:equal>
				<%if(!"false".equals(law_file_priv.trim())){ %>
				<td align="center" width="75px" class="TableRow" style="border-top:none;"  nowrap>
					查看角色
				</td>
				<hrms:priv func_id="28028,0707018,0711018,34028"> 
				<td align="center" width="75px" class="TableRow" style="border-top:none;border-right: none;"  nowrap>
					清除角色
				</td>
				</hrms:priv> 
				<%} %>
			</tr>
		</thead>
		<hrms:extenditerate id="element" name="lawbaseForm" property="lawbaseForm.list" indexes="indexes" pagination="lawbaseForm.pagination" pageCount="${lawbaseForm.pagerows}" scope="session">
			<%
          if(i%2==0)
          {
          %>
			<tr class="trShallow">
				<%}
          else
          {%>
			<tr class="trDeep"> 
				<%
          }
          i++;          
          %>
          <bean:define id="file_id" name="element" property="String(file_id)"/>
         <bean:define id="title" name="element" property="String(title)"/>
          <%String fileid = PubFunc.encrypt(file_id.toString()); 
          String temp = "";
			if (title.toString().length() > 40) 
				temp = title.toString().substring(0, 40) + "...";
			else
				temp = title.toString();
        	  
          %>
				<td align="center" class="RecordRow" style="border-left: none;" nowrap>
					<hrms:checkmultibox name="lawbaseForm" property="lawbaseForm.select" value="true" indexes="indexes" />
				</td>
				<td align="center" class="RecordRow" nowrap>
					<IMG src="/images/goto_input.gif" title="插入" onclick="insertValue('<%=fileid %>','<%=flg %>');"/>
				</td>
				<logic:empty name="lawbaseForm" property="colums"> 
				<logic:equal name="lawbaseForm" property="basetype" value="5">
					<logic:equal name="lawbaseForm" property="sign" value="1">
						<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",originalfile,">           						
						<td align="center" class="RecordRow" nowrap>
							<logic:notEqual name="element" property="string(originalext)" value="">
								<a href="###" onclick="ajaxcheck('<%=fileid %>','original')"><img src="/images/view.gif" border=0></a>
							</logic:notEqual>
						</td>
						</logic:notMatch>
					</logic:equal>
				</logic:equal>
				<td align="left" class="RecordRow" nowrap>
				<logic:notEqual name="element" property="string(ext)" value="">
					<a href="###" onclick="ajaxcheck('<%=fileid %>','download')"><abbr style="text-decoration:none" title="<%=title%>"><%=temp %></abbr></a>
				</logic:notEqual>
				<logic:equal name="element" property="string(ext)" value="">
					<abbr style="text-decoration:none" title="<%=title%>"><%=temp %></abbr>
				</logic:equal>
					
				</td>										
								
				    <logic:notEqual name="lawbaseForm" property="basetype" value="5">
				    <logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",type,">           						
					<td align="left" class="RecordRow" nowrap>
						<bean:write name="element" property="string(type)" filter="true" />		
					</td>
					</logic:notMatch>
					</logic:notEqual>
				    <logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",content_type,">           						
					<td align="left" class="RecordRow" nowrap>
						<bean:write name="element" property="string(content_type)" filter="true" />				
					</td>
					</logic:notMatch>
					<logic:notEqual name="lawbaseForm" property="basetype" value="5">
				    <logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",valid,">           						
					<td align="center" class="RecordRow" nowrap>
						<logic:equal name="element" property="string(valid)" value="1">
					 	   	 <bean:message key="lable.lawfile.availability"/>
					 	 </logic:equal>
					 	 <logic:equal name="element" property="string(valid)" value="0">
					 	    <bean:message key="lable.lawfile.allabolish" />
					 	 </logic:equal>
					 	 <logic:equal name="element" property="string(valid)" value="2">
					 	   <bean:message key="lable.lawfile.partabolish" />
					 	 </logic:equal>
					 	 <logic:equal name="element" property="string(valid)" value="3">
					 	    <bean:message key="lable.lawfile.editing" />
					 	 </logic:equal>	  	
					 	  <logic:equal name="element" property="string(valid)" value="4">
					 	    <bean:message key="lable.lawfile.other"/>
					 	 </logic:equal>	  	
					</td>
					</logic:notMatch>					
				
					</logic:notEqual>
					
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",note_num,">           						
				    <td align="left" class="RecordRow" nowrap>
					<bean:write name="element" property="string(note_num)" filter="true" />					
				    </td>
				    </logic:notMatch>
				    				    											
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_org,">           						
					<td align="left" class="RecordRow" nowrap>
						<bean:write name="element" property="string(issue_org)" filter="true" />					
					</td>														
					</logic:notMatch>
										
				    <logic:notEqual name="lawbaseForm" property="basetype" value="5">
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",notes,">           						
					<td align="left" class="RecordRow" nowrap>
						<bean:write name="element" property="string(notes)" filter="true" />				
					</td>
					</logic:notMatch>
					</logic:notEqual>
					
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_date,">           						
					<td align="left" class="RecordRow" nowrap>
					<bean:write name="element" property="string(issue_date)" filter="true" />					
					</td>
				    </logic:notMatch>	
					
					<logic:notEqual name="lawbaseForm" property="basetype" value="5">
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",implement_date,">           						
					<td align="left" class="RecordRow" nowrap>
					<bean:write name="element" property="string(implement_date)" filter="true" />						
					</td>
					</logic:notMatch>	
					
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",valid_date,">           						
					<td align="left" class="RecordRow" nowrap>
						<bean:write name="element" property="string(valid_date)" filter="true" />				
					</td>
					</logic:notMatch>
					</logic:notEqual>
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",name,">           						
					<td align="left" class="RecordRow" nowrap>
						<bean:write name="element" property="string(name)" filter="true" />				
					</td>
					</logic:notMatch>	
				
					<logic:notEqual name="lawbaseForm" property="basetype" value="5">
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",digest,">           						
					<td align="left" class="RecordRow" nowrap>
						<bean:write name="element" property="string(digest)" filter="true" />				
					</td>
					</logic:notMatch>
					</logic:notEqual>							
					
					
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",viewcount,">           						
					<td align="right" class="RecordRow" nowrap>
						<bean:write name="element" property="string(viewcount)" filter="true" />				
					</td>
					</logic:notMatch>												  					
		
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",extfile,">           						
					<td align="center" class="RecordRow" nowrap>
						<a href="###" onclick="ajaxcheck('<%=fileid %>','affix');">附件</a>
					</td>
					</logic:notMatch>
				 </logic:empty>
				 <logic:notEmpty name="lawbaseForm" property="colums">
				 <logic:iterate id="item" name="lawbaseForm" property="fieldlist">
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",${item.itemid },">           						
					<logic:equal value="originalext" name="item" property="itemid">
						<td align="center" class="RecordRow" nowrap>
							<logic:notEqual name="element" property="string(originalext)" value="">
								<a href="###" onclick="ajaxcheck('<%=fileid %>','original')"><img src="/images/view.gif" border=0></a>
							</logic:notEqual>
							
						</td>
					</logic:equal>
					<logic:equal value="ext" name="item" property="itemid">
					<td align="left" class="RecordRow" nowrap>
					<logic:notEqual name="element" property="string(ext)" value="">
						<a href="###" onclick="ajaxcheck('<%=fileid %>','download')">文件</a>
					</logic:notEqual>
						
					</td>
					</logic:equal>
					<logic:equal value="name" name="item" property="itemid">
					
					<td align="left" class="RecordRow" nowrap>
					<logic:notEqual name="element" property="string(ext)" value="">
						<a href="###" onclick="ajaxcheck('<%=fileid %>','download')"><bean:write name="element" property="string(name)" filter="true" /></a>
					</logic:notEqual>
					<logic:equal name="element" property="string(ext)" value="">
						<bean:write name="element" property="string(name)" filter="true" />
					</logic:equal>
						
					</td>
					</logic:equal>
					<logic:equal value="title" name="item" property="itemid">
					<td align="left" class="RecordRow" nowrap>
						<a href="###" onclick="ajaxcheck('<%=fileid %>','view')"><bean:write name="element" property="string(title)" filter="true" /></a>
						
					</td>
					</logic:equal>
					
					<logic:equal value="extfile" name="item" property="itemid">
					<td align="center" class="RecordRow" nowrap>
						<a href="###" onclick="ajaxcheck('<%=fileid %>','affix');">附件</a>
					</td>
					</logic:equal>
					<logic:equal value="viewcount" name="item" property="itemid">
					<td align="right" class="RecordRow" nowrap>
						<bean:write name="element" property="string(${item.itemid })" filter="true" />
					</td>
					</logic:equal>
					
					<logic:notEqual value="originalext" name="item" property="itemid">
					<logic:notEqual value="ext" name="item" property="itemid">
					<logic:notEqual value="name" name="item" property="itemid">
					<logic:notEqual value="title" name="item" property="itemid">
					<logic:notEqual value="extfile" name="item" property="itemid">
					<logic:notEqual value="viewcount" name="item" property="itemid">
					<logic:equal value="0"  name="item" property="codesetid">
					<td align="left" class="RecordRow" nowrap>
						<bean:write name="element" property="string(${item.itemid })" filter="true" />
						
					</td>
					</logic:equal>
					<logic:notEqual value="0" name="item" property="codesetid">
					<td align="left" class="RecordRow" nowrap>
						<hrms:codetoname codeitem="codeitem" codeid="${item.codesetid }" name="element" codevalue="string(${item.itemid})"  scope="page"/>
						<bean:write name="codeitem" property="codename" /> 
					</td>
					</logic:notEqual>
					</logic:notEqual>
					</logic:notEqual>
					</logic:notEqual>
					</logic:notEqual>
					</logic:notEqual>
					</logic:notEqual>
					</logic:notMatch>
				</logic:iterate>	
				</logic:notEmpty>
				<td align="center" class="RecordRow" nowrap>
					<a href="###" onclick="ajaxcheck('<%=fileid %>','update');"><img src="/images/edit.gif" border=0></a>
				</td>	
				<logic:equal name="lawbaseForm" property="basetype" value="5">
					<logic:equal name="lawbaseForm" property="sign" value="1">
					<td align="center" class="RecordRow" style="border-right: none;"  nowrap>
						<a href="###" onclick="relating_person('<%=fileid %>');"><img src="/images/overview_obj.gif" border=0></a>
					</td>
					</logic:equal>
				</logic:equal>
				<%if(!"false".equals(law_file_priv.trim())){ %>
				<td align="center" class="RecordRow" nowrap>
					<a href="###" onclick="query_role_assign('<%=fileid %>');"><img src="/images/view.gif" border=0></a>
				</td>
				<logic:equal name="lawbaseForm" property="basetype" value="1">
				  <hrms:priv func_id="28028,0707018"> 
				  <td align="center" class="RecordRow" style="border-right: none;"  nowrap>
					<a href="###" onclick="delete_role_assign('<%=fileid %>');"><img src="/images/assign_priv.gif" border=0></a>
				  </td>
				  </hrms:priv>
				</logic:equal>	
				<logic:equal name="lawbaseForm" property="basetype" value="4">
				  <hrms:priv func_id="0711018"> 
				  <td align="center" style="border-right: none;"  class="RecordRow" nowrap>
					<a href="###" onclick="delete_role_assign('<%=fileid %>');"><img src="/images/assign_priv.gif" border=0></a>
				  </td>
				  </hrms:priv>
				</logic:equal>
				<logic:equal name="lawbaseForm" property="basetype" value="5">
				  <hrms:priv func_id="34028"> 
				  <td align="center" class="RecordRow"  style="border-right: none;"  nowrap>
					<a href="###" onclick="delete_role_assign('<%=fileid %>');"><img src="/images/assign_priv.gif" border=0></a>
				  </td>
				  </hrms:priv>
				</logic:equal>		
				<%} %>
			</tr>
		</hrms:extenditerate>
	</table>
</div>
	<table width="100%" align="center" class="RecordRowP">
		<tr>
			<td valign="middle" class="tdFontcolor">
		    	<hrms:paginationtag name="lawbaseForm" pagerows="${lawbaseForm.pagerows}" property="lawbaseForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="lawbaseForm" property="lawbaseForm.pagination" nameId="lawbaseForm" propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
	<table width="100%" align="center" cellpadding="0" cellspacing="0">
		<tr>
			<td align="left" style="padding-top: 5px;">
						
				<logic:equal name="lawbaseForm" property="basetype" value="1">
					<input type="button" name="b_add" class="mybutton" value="<bean:message key="button.insert"/>" onclick="javascript:checkValid('<%=flg %>');">
				<hrms:priv func_id="28029,0707019">
				<input type="button" name="b_delete" class="mybutton" value="<bean:message key="button.delete" />" onclick="javascript:deletes();">
				 </hrms:priv>
				<hrms:priv func_id="28023">
				 <input type="button" name="b_order" class="mybutton" value="调整顺序" onclick="adjust_order('<%=flg %>');" />
				 </hrms:priv>
				 <hrms:priv func_id="28033"> 
				 	<input type="button" name="b_order" class="mybutton" value="显示隐藏指标" onclick="showhidden();" />
				 </hrms:priv>
				 <hrms:priv func_id="28030"> 
                 <input type="button" name="b_range" class="mybutton" value="重新归类" onclick="javascript:afresh_range();">
                 </hrms:priv>
                 <%if(!"false".equals(law_file_priv.trim())){ %>
					<hrms:priv func_id="28027"> 
	                 <input type="button" name="b_add" class="mybutton" value="角色授权" onclick="javascript:assign_role();">
	                 </hrms:priv>
	                 <hrms:priv func_id="28031">
	                 <input type="button" name="b_order" class="mybutton" value="授权初始化" onclick="clear_allrole();" />
	                 </hrms:priv>
                 <%} %>
                 </logic:equal>
                 <logic:equal name="lawbaseForm" property="basetype" value="4">
                   <hrms:priv func_id="0711023">
                  		<input type="button" name="b_add" class="mybutton" value="<bean:message key="button.insert"/>" onclick="javascript:checkValid('<%=flg %>');">
                   </hrms:priv> 
                    <hrms:priv func_id="0711019">
				<input type="button" name="b_delete" class="mybutton" value="<bean:message key="button.delete" />" onclick="javascript:deletes();">
				 </hrms:priv>
				 <hrms:priv func_id="0711024">
				 	<input type="button" name="b_order" class="mybutton" value="调整顺序" onclick="adjust_order('<%=flg %>');" />
				 </hrms:priv>
				 <hrms:priv func_id="0711022"> 
				 	<input type="button" name="b_order" class="mybutton" value="显示隐藏指标" onclick="showhidden();" />
				 </hrms:priv>
				 <hrms:priv func_id="0711020"> 
                 <input type="button" name="b_range" class="mybutton" value="重新归类" onclick="javascript:afresh_range();">
                 </hrms:priv>
                 <%if(!"false".equals(law_file_priv.trim())){ %>
				<hrms:priv func_id="0711017"> 
                 <input type="button" name="b_add" class="mybutton" value="角色授权" onclick="javascript:assign_role();">
                 </hrms:priv>
                 <hrms:priv func_id="0711021"> 
                 <input type="button" name="b_order" class="mybutton" value="授权初始化" onclick="clear_allrole();" />
                 </hrms:priv>
                 <%} %>
                 </logic:equal>
                 <logic:equal name="lawbaseForm" property="basetype" value="5">
                  	<input type="button" name="b_add" class="mybutton" value="<bean:message key="button.insert"/>" onclick="javascript:checkValid('<%=flg %>');">
				<hrms:priv func_id="34029">
				<input type="button" name="b_delete" class="mybutton" value="<bean:message key="button.delete" />" onclick="javascript:deletes();">
				 </hrms:priv>
				 <hrms:priv func_id="34023">
				 <input type="button" name="b_order" class="mybutton" value="调整顺序" onclick="adjust_order('<%=flg %>');" />
				 </hrms:priv>
				 <hrms:priv func_id="34034"> 
				 	<input type="button" name="b_order" class="mybutton" value="显示隐藏指标" onclick="showhidden();" />
				 </hrms:priv>
				 <hrms:priv func_id="34030"> 
                 <input type="button" name="b_range" class="mybutton" value="重新归类" onclick="javascript:afresh_range();">
                 </hrms:priv>
                 <%if(!"false".equals(law_file_priv.trim())){ %>
				<hrms:priv func_id="34027"> 
                 <input type="button" name="b_add" class="mybutton" value="角色授权" onclick="javascript:assign_role();">
                 </hrms:priv>
                 <hrms:priv func_id="34032"> 
                 <input type="button" name="b_order" class="mybutton" value="授权初始化" onclick="clear_allrole();" />
                 </hrms:priv>
                 <%} %>
                 <hrms:priv func_id="34031"> 
                 <input type="button" name="b_excel" value="<bean:message key="goabroad.collect.educe.excel"/>" class="mybutton" onclick="excecuteExcel('<%=flg %>');">
                 </hrms:priv>
                 </logic:equal>
                 
                 <logic:equal value="dxt" name="lawbaseForm" property="returnvalue">       
                   <hrms:tipwizardbutton flag="law" target="il_body" formname="lawbaseForm"></hrms:tipwizardbutton>
                 </logic:equal>
			</td>
		</tr>
	</table>

</html:form>
   <div id='wait' style='position:absolute;top:140;left:180;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style" height=24>正在初始化授权，请稍后...</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div> 
<%
	out.println(lawBaseForm.getMessage());
	if(lawBaseForm.getMessage().equals(""))
	{
	}
	else
	{
		%>
<script>
		alert('<%=lawBaseForm.getMessage()%>');
	</script>
<%
		lawBaseForm.setMessage("");
	}
%>

<%
 
 if(request.getAttribute("fileexterror")!=null && request.getAttribute("fileexterror").toString().length()>0)
 {
   %>
<script>
      alert('操作不成功,必须选doc,xls,txt类型的文件');
   </script>
<%
   request.setAttribute("fileexterror","");
 }
%>
<script language="javascript">
 MusterInitData();
 var h=window.top.document.body.clientHeight;
 document.getElementById("divid").style.height=h-197;
</script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>