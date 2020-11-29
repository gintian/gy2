<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,java.text.SimpleDateFormat,com.hrms.frame.dao.RecordVo,
				com.hjsj.hrms.actionform.report.org_maintenance.SearchReportUnitForm,
				com.hjsj.hrms.utils.PubFunc" %>
<%
	Calendar d=Calendar.getInstance();
	d.set(Calendar.DAY_OF_YEAR,d.get(Calendar.DAY_OF_YEAR) - 1);   
	SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
	String enddate=df.format(d.getTime());

	SearchReportUnitForm searchReportUnitForm=(SearchReportUnitForm)session.getAttribute("searchReportUnitForm"); 
	String codeflag=searchReportUnitForm.getCodeFlag();
	
	String codeurl="";
	if(codeflag!=null&&codeflag.trim().length()>0)
		codeurl="&code="+codeflag;
 %>
 <head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
</head>
<!-- 引入Ext 框架 -->
<script language="JavaScript"src="../../../module/utils/js/template.js"></script> 
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
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
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript">
   function deleterec()
   {
   var n=0;
   		for(var i=0;i<document.searchReportUnitForm.elements.length;i++)
   		{
   			if(document.searchReportUnitForm.elements[i].type=='checkbox'&&document.searchReportUnitForm.elements[i].checked&&document.searchReportUnitForm.elements[i].name!='selbox')
   			{
   				n++;
   			}
   		}
   		if(n==0)
   		{
   			alert("请选择需删除的填报单位!");
   			return;
   		}
   		
      if(confirm("提示：删除功能将会把报表系统里与所选填报单位及下属子单位\r\n相关联的一切填报数据全部清空，请谨慎使用！"))
      {
      	 if(confirm("您确定要删除填报单位吗?"))
      	 {
       	 	searchReportUnitForm.action="/report/org_maintenance/reportunitlist.do?b_delete=del";
       	 	searchReportUnitForm.submit(); 
       	 }
       } 
   }
   function importOrganization()
   {
   		if(confirm("提示:导入组织机构功能将会把整个报表系统\r\n以前提交和归档过的数据全部清空,请谨慎使用!"))
   		{
	   		if(confirm("<bean:message key="ortlist.reportunitlist.isimport"/>?"))
	      	{
	      		searchReportUnitForm.action="/report/org_maintenance/reportunitlist.do?b_delete_save=del_save";
	      		searchReportUnitForm.submit(); 
	       }
	    } 
   }
   function load(){
   	 var info ="${searchReportUnitForm.addFlag}";
   	 if(info == "yes"){
   	 	parent.mil_menu.document.location = "reportunittree.jsp"; 
   	 }
   }
   //kangkai start 2007-12-14  add  a  tree node  and refresh  reportunittree.jsp
     function add_ok()
  {
   
     var info ="${searchReportUnitForm.addFlag}";
     if(info=="b_delete_save"){
    	parent.mil_menu.document.location = "reportunittree.jsp"; 
      	parent.mil_body.document.location = "/report/org_maintenance/reportunitlist.do?b_query=link"; 
      	
     }
      if(info != "yes"){
   	 	return;
   	 }
  
     var currnode=parent.mil_menu.Global.selectedItem;
     var parentuid=currnode.uid
     var parentCode ="${searchReportUnitForm.parentCode}";
     var unitName ="${searchReportUnitForm.addUnitCode}";
     var unitCode ="${searchReportUnitForm.addUnitName}";
     
     //form   重置
     <%
		SearchReportUnitForm pbf1 = (SearchReportUnitForm)session.getAttribute("searchReportUnitForm");
        //add by wangchaoqun on 2014-9-24 加密参数
        String params = PubFunc.encrypt("where parentid='"+pbf1.getParentCode()+pbf1.getAddUnitName()+"' and unitcode <> parentid ");
		pbf1.setAddUnitCode("");
		pbf1.setAddUnitName("");
		pbf1.setAddFlag("no");
	 %>
     var uid = parentCode+unitCode;
	 var text = unitName;
	 var action ="/report/org_maintenance/reportunitlist.do?b_query=link&code="+parentCode+unitCode;
	 
	 var title = unitName;
	 var imgurl="/images/unit.gif";
	 var xml = "report_unit_tree.jsp?params=<%=params%>";
   	
     if(currnode.load)
     {
    	//alert(uid+'  '+text+'  '+action+'   '+"mil_body"+'     '+title+'     '+imgurl+'   '+xml);
		var tmp=new xtreeItem(uid,text,action,"mil_body",title,imgurl,xml);
	 	parent.mil_menu.add(uid,text,action,"mil_body",title,imgurl,xml);
     }
     else
     {
     	currnode.expand();
     }
   
  
  }
   
  //kangkai  end 
   
   //批量授权表类
   function batchAccreditReportSort(){
//    searchReportUnitForm.target="il_body";
   	searchReportUnitForm.action="/report/org_maintenance/reportunitlist2.do?b_batchAccredit=link";
   	searchReportUnitForm.submit(); 
   }
	vo = new Object();
	vo.back = "back";
	function reportUser(uc){
		unit = uc;
		var config = {
			width:310,
			height:434,
			title:'报表负责人',
			theurl:"/system/logonuser/select_user_obj.do?b_query=link&isShowFullName=1&priv=1&treeselecttype=1&report=1&uc="+uc,
			id:'searchPersonnelWin'
		}
		openWin(config);
		Ext.getCmp("searchPersonnelWin").addListener('close',function(){saveTurnRest(vo);});
   }
	function saveTurnRest(retvo){
		//retvo.back防止直接关掉窗口将数据清空
		if(retvo!=null&&retvo!='undefined'&&!retvo.back)
		{
			var title = retvo.title;	//用户名全称
	 		var content = retvo.content;//用户名	
	 		searchReportUnitForm.content.value=content;
	 		var url="/report/org_maintenance/reportunitlist.do?b_save_user=save&unitcode="+unit;	
	 		searchReportUnitForm.action=url;
	 		searchReportUnitForm.submit()
		}
	}
   function upItem(counts,unitcode,a0000,page,pagecount,id,pages){
   				
	   var hashvo=new ParameterSet();
			hashvo.setValue("page",page);
			hashvo.setValue("a0000",a0000);
			hashvo.setValue("unitcode",unitcode);
			hashvo.setValue("pagecount",pagecount);
		    hashvo.setValue("method","up");
		   	hashvo.setValue("parentidcode","${searchReportUnitForm.codeFlag}");
		   	
		   	
		   	hashvo.setValue("id",id);
		   	hashvo.setValue("pages",pages);
		   	hashvo.setValue("counts",counts);
		   	
			var request=new Request({asynchronous:false,onSuccess:changeview,functionId:'03050000016'},hashvo);
   			
   }
     function changeview(outparamters)
  {
		var bodyhtml =	outparamters.getValue("bodyHtml");

		var parentCode = outparamters.getValue("parentCode");
		window.location.target="_blank";
		window.location.href = "/report/org_maintenance/reportunitlist.do?b_query=link&code="+parentCode;	
  		
  }
  function downItem(counts,unitcode,a0000,page,pagecount,id,pages){
  		
	   var hashvo=new ParameterSet();
			hashvo.setValue("page",page);
			hashvo.setValue("a0000",a0000);
			hashvo.setValue("unitcode",unitcode);
			hashvo.setValue("pagecount",pagecount);

		    hashvo.setValue("method","down");
		    hashvo.setValue("parentidcode","${searchReportUnitForm.codeFlag}");
		    
		    
		   	hashvo.setValue("id",id);
		   	hashvo.setValue("pages",pages);
		   	hashvo.setValue("counts",counts);
			var request=new Request({asynchronous:false,onSuccess:changeview,functionId:'03050000016'},hashvo);
   
   }
</script>
   <link href="../../css/css1.css" rel="stylesheet" type="text/css">
<!--kangkai start update -->
<hrms:themes/>
<body onLoad="add_ok()">
<!--kangkai  end-->
<%int i=0; %>
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
<tr>  
<td valign="top">
<form name="searchReportUnitForm" method="post" action="/report/org_maintenance/addreportunit.do?b_add=link2">

 <html:hidden name="searchReportUnitForm" property="content"/> 
	
	<div id="allselect">
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
		<thead>
		<tr>
			<td align="center" class="TableRow" nowrap>
				<input type="checkbox" name="selbox" onclick="batch_select(this,'reportUnitListForm.select');" title='<bean:message key="label.query.selectall"/>'>
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="orglist.reportunitlist.codename"/>&nbsp;
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="orglist.reportunitlist.code"/>&nbsp;
			</td>                  
			<td align="center" class="TableRow" nowrap>
				<bean:message key="orglist.reportunitlist.edit"/>           	
			</td>  
			<td align="center" class="TableRow" nowrap>
				<bean:message key="orglist.reportunitlist.tsortcarveup"/>             	
			</td>  
			<td align="center" class="TableRow" nowrap>
				<bean:message key="orglist.reportunitlist.reportprincipal"/>             	
			</td>
			<hrms:priv func_id="2905006">  
			<td align="center" class="TableRow" nowrap>
				<bean:message key="label.zp_exam.sort"/>             	
			</td>    
			</hrms:priv>          			       
		</tr>
		</thead>
		
		
		<hrms:extenditerate id="element" name="searchReportUnitForm" property="reportUnitListForm.list"   
			indexes="indexes"  pagination="reportUnitListForm.pagination" pageCount="15" scope="session">
			<div id="<%="selected"+i %>">
						<%
			if (i % 2 == 0) {%>
					<tr class="trShallow" onclick='tr_onclick(this,"#F3F5FC");' >
						<%} else {%>
					<tr class="trDeep"  onclick='tr_onclick(this,"#E4F2FC");' >
						<%}
			%>
				<td align="center" class="RecordRow" nowrap>
				
				<hrms:checkmultibox name="searchReportUnitForm" property="reportUnitListForm.select" value="true" indexes="indexes"/>
				</td>
				<td align="left" class="RecordRow" style="word-wrap:break-word; word-break:break-all;" nowrap>                
					&nbsp;<bean:write name="element" property="unitname" filter="false"/>&nbsp;
					<Input type='hidden' value='<bean:write name="element" property="unitcode" filter="false"/>' name='select[${indexes}]' />
					<Input type='hidden' value='<bean:write name="element" property="unitname" filter="false"/>' name='select[${indexes}]_name' /> 
					<Input type='hidden' value='<bean:write name="element" property="start_date" filter="false"/>' name='select[${indexes}]_startdate' />
				</td>
				<td align="left" class="RecordRow" nowrap>                
					&nbsp;<bean:write name="element" property="unitcode" filter="false"/>&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap> 
					<hrms:priv func_id="2905003">
					&nbsp;<a href="/report/org_maintenance/reportunitupdate.do?b_updates=link&unitid=<bean:write  name='element' property='unitid' filter='true'/>"  target="mil_body"><img src="../../images/edit.gif" width="11" height="17" border=0></a> 
					</hrms:priv>
				</td>
				<td align="left" class="RecordRow" nowrap> 
					&nbsp;<a href="/report/org_maintenance/reportunitlist.do?b_typelist=link&rtunitcode=<bean:write name='element' property='unitcode' filter='false'/>"  >
					<img src="../../images/edit.gif" width="11" height="17" border=0></a> 
					<bean:write name="element" property="reporttypes" filter="false"/>
				</td>   
				<td align="left" class="RecordRow" nowrap>
					&nbsp;<a href="javaScript:reportUser('<bean:write name='element' property='unitcode' filter='false'/>')">
					<img src="../../images/edit.gif" width="11" height="17" border=0></a> 
					<bean:write name="element" property="b0110" filter="false"/>
				</td>  
					<hrms:priv func_id="2905006">  
				<td align="center" class="RecordRow" nowrap>
					&nbsp;<a href="javaScript:upItem('<bean:write name='searchReportUnitForm' property='reportUnitListForm.pagination.count' filter='true' />','<bean:write name='element' property='unitcode' filter='false'/>','<bean:write name='element' property='a0000' filter='false'/>','<bean:write name='searchReportUnitForm' property='reportUnitListForm.pagination.current' filter='true' />','<bean:write name='searchReportUnitForm' property='reportUnitListForm.pagination.pageCount' filter='true' />','<%=i %>','<bean:write name='searchReportUnitForm' property='reportUnitListForm.pagination.pages' filter='true' />')">
					<img src="../../images/up01.gif"  border=0></a> 
					&nbsp;<a href="javaScript:downItem('<bean:write name='searchReportUnitForm' property='reportUnitListForm.pagination.count' filter='true' />','<bean:write name='element' property='unitcode' filter='false'/>','<bean:write name='element' property='a0000' filter='false'/>','<bean:write name='searchReportUnitForm' property='reportUnitListForm.pagination.current' filter='true' />','<bean:write name='searchReportUnitForm' property='reportUnitListForm.pagination.pageCount' filter='true' />','<%=i %>','<bean:write name='searchReportUnitForm' property='reportUnitListForm.pagination.pages' filter='true' />')">
					<img src="../../images/down01.gif"  border=0></a> 
				</td>      
					</hrms:priv>                             	    		        	        	        
			</tr>
			<%
				i++;
				%>
			</div>
		</hrms:extenditerate>
		
	</table>
</div>
<div id="allselect2"></div>
	<table width="100%"   class='RecordRowP'   align="center">
		<tr>
			 <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="searchReportUnitForm" property="reportUnitListForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="searchReportUnitForm" property="reportUnitListForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="searchReportUnitForm" property="reportUnitListForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right">
		          <hrms:paginationlink name="searchReportUnitForm" property="reportUnitListForm.pagination" nameId="reportUnitListForm" >
				</hrms:paginationlink>
			</td>
		</tr>
	</table>

	<table  width="70%" align="center" style="margin-top: 2px">
		<tr>
			<td align="center"  nowrap colspan="4">
				<!--codeFlag标识了当前页面显示信息的父接点单位编码-->
				<input type="hidden" name="codeFlag" value="<bean:write name="searchReportUnitForm" property="codeFlag" filter="true" />">	
				<logic:notEqual name="searchReportUnitForm" property="analysereportflag" value="1" >		
				<hrms:priv func_id="2905002">
				<input type="submit" name="b_add" value="<bean:message key='orglist.reportunitlist.addreportunit'/>" class="mybutton" > 
				</hrms:priv>
				<hrms:priv func_id="2905004">
				<input type="button" name="addbutton"  value="<bean:message key='ortlist.reportunitlist.delete'/>" class="mybutton" onclick='deleterec()'>  
				</hrms:priv>
				
				<hrms:priv func_id="2905007">
				<input type="button" name="cancel"  value="<bean:message key='button.abolish'/>" class="mybutton" onclick="cancel_unit();return false;">  
				</hrms:priv>
				</logic:notEqual>
				<hrms:priv func_id="2905005">
				<input type="button" name="batch"  value="<bean:message key='ortlist.reportunitlist.batchtable'/>" class="mybutton" onclick='batchAccreditReportSort()'>  
				</hrms:priv>
				<logic:notEqual name="searchReportUnitForm" property="analysereportflag" value="1" >
				<hrms:priv func_id="2905001">
					<input type="button" name="import"  value="<bean:message key='ortlist.reportunitlist.import'/>" class="mybutton" onclick='importOrganization()'>  
				</hrms:priv>
				</logic:notEqual>
				<hrms:tipwizardbutton flag="report" target="il_body" formname="searchReportUnitForm"/>
			</td>	  
		</tr>
	</table>
</form>
</td>
</tr>
</table>
<script language="javascript">
var value_s="";
<%
	SearchReportUnitForm pbf = (SearchReportUnitForm)session.getAttribute("searchReportUnitForm");
	ArrayList codelist = pbf.getReportUId();
	if(codelist!=null){
		for(int y=0;y<codelist.size();y++){
		String str=(String)codelist.get(y);
%>
	value_s="<%=str%>";
	deleteRefresh(value_s);
<%
		}
		codelist.clear();
	}
%>
	function openWin(config){
	    Ext.create("Ext.window.Window",{
	    	id:config.id,
	    	width:config.width,
	    	height:config.height,
	    	title:config.title,
	    	resizable:false,
	    	autoScroll:false,
	    	modal:true,
	    	renderTo:Ext.getBody(),
	    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+config.theurl+"'></iframe>"
		    }).show();	
	}

	function getArguments(){
		var arguments=new Array();
		var n=0;
   		var name="";
   		for(var i=0;i<document.searchReportUnitForm.elements.length;i++)
   		{
   			if(document.searchReportUnitForm.elements[i].type=='checkbox'&&document.searchReportUnitForm.elements[i].checked&&document.searchReportUnitForm.elements[i].name!='selbox')
   			{
   				n++;
   				name=document.searchReportUnitForm.elements[i].name;
   			}
   		}
   		var temp=name.split(".");
   		arguments[0]=document.getElementsByName(temp[1]+"_name")[0].value
   		arguments[1]='<%=enddate%>'
   		return arguments;
	}
	var goalunit;
	function openWin(config){
	    Ext.create("Ext.window.Window",{
	    	id:config.id,
	    	width:config.width,
	    	height:config.height,
	    	title:config.title,
	    	resizable:false,
	    	autoScroll:false,
	    	modal:true,
	    	renderTo:Ext.getBody(),
	    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=yes height='100%' width='100%' src='"+config.theurl+"'></iframe>",
    		listeners :{
	    		'close':function(){
	    			if(goalunit)
	    			{
	    				 var hashvo=new ParameterSet();
	    				 hashvo.setValue("end_date",goalunit[0]);
	    				 hashvo.setValue("goal_unit",goalunit[1]);
	    				  hashvo.setValue("transfer_date",goalunit[2]);
	    				 hashvo.setValue("unit",document.getElementsByName(config.temp[1])[0].value);
	    				 var request=new Request({asynchronous:false,onSuccess:showlist,functionId:'0305000026'},hashvo);
	    			} 
	    		}
	    	}
 	    }).show();	
	}
	
   function cancel_unit()
   {
   		var n=0;
   		var name="";
   		for(var i=0;i<document.searchReportUnitForm.elements.length;i++)
   		{
   			if(document.searchReportUnitForm.elements[i].type=='checkbox'&&document.searchReportUnitForm.elements[i].checked&&document.searchReportUnitForm.elements[i].name!='selbox')
   			{
   				n++;
   				name=document.searchReportUnitForm.elements[i].name;
   			}
   		}
   		if(n==0)
   		{
   			alert("请选择需撤销的填报单位!");
   			return;
   		}
   		if(n>1)
   		{
   			alert("只能逐个撤销填报单位!");
   			return;
   		}
   		
   		var temp=name.split(".");
   		var startdate_value=document.getElementsByName(temp[1]+"_startdate")[0].value
   		var myDate=new Date() ;
   		var current="";
   		var curyear = ""+myDate.getFullYear();
   		var curmonth = "";
   		var curdate = "";
   		if((myDate.getMonth()+1)<10)
   			curmonth="-0"+(myDate.getMonth()+1);
   			else
   			curmonth="-"+(myDate.getMonth()+1);
   		if((myDate.getDate())<10)
   			curdate="-0"+myDate.getDate();	
   			else
   			curdate="-"+myDate.getDate();	
   			current = curyear+curmonth+curdate;
   		if(current==startdate_value) 
   		{
   			alert("您选择了有效日期为当日的机构，不允许此操作!");
   			return;
   		}
   		var strurl="/report/org_maintenance/reportunitlist.do?b_cancelUnit=link";
		var config = {
				width:500,
				height:330,
				title:'',
				theurl:strurl,
				id:'showBatId',
				temp:temp
			}
			openWin(config);
   }
   
   function showlist(outparamters)
   {
   		parent.mil_menu.document.location="/report/org_maintenance/reportunittree.jsp";
   		searchReportUnitForm.action="/report/org_maintenance/reportunitlist.do?b_query=link<%=codeurl%>";
   		searchReportUnitForm.submit(); 
   }
   
   

   function deleteRefresh(code)
   {
   			var currnode=parent.frames['mil_menu'].Global.selectedItem;
			if(currnode==null)
					return;
			if(currnode.load)
			for(var i=0;i<=currnode.childNodes.length-1;i++){
				if(code==currnode.childNodes[i].uid)
					currnode.childNodes[i].remove();
			}
   }
   
   
   function updateRefresh()
   {
    var addFlag ="${searchReportUnitForm.addFlag}";
   	if(addFlag == "update")
   	{
   	
   		var unitCode ="${searchReportUnitForm.unitCode}";
   		var unitName ="${searchReportUnitForm.unitName}";
   		var currnode=parent.frames['mil_menu'].Global.selectedItem;
   		if(currnode.load)
			for(var i=0;i<=currnode.childNodes.length-1;i++){
				if((unitCode)==currnode.childNodes[i].uid)
					currnode.childNodes[i].setText(unitName);
   		}
   	}
   	<%
		SearchReportUnitForm pbf2 = (SearchReportUnitForm)session.getAttribute("searchReportUnitForm");
		pbf2.setAddFlag("no");
	 %>
   }
   updateRefresh();
</script>
</body>