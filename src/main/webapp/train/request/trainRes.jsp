<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style>
  .top{  
  	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	valign:middle;
	
    position:relative; 
	top:expression(this.offsetParent.scrollTop);
	z-index: 15;
	}
  .fixedHeaderTr{
	position:relative; 
    left:expression(this.offsetParent.scrollLeft);
    top:expression(this.offsetParent.scrollTop);
    z-index: 20;
    margin-top: -10;
    padding-top: 10px;
	}
  .left{
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	border-collapse:collapse;
	background-color: #ffffff; 
	height:22px;
	
    position:relative; 
    left:expression(this.offsetParent.scrollLeft);
    z-index: 10;
    }
    .top_left{
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	border-collapse:collapse;
	background-color:#f4f7f7;; 
	height:22px;
	
    position:relative; 
    left:expression(this.offsetParent.scrollLeft);
    top:expression(this.offsetParent.scrollTop);
    z-index: 20;
    }
    
    .RecordRow1{
    border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	height:22px;
    }
</style>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<script language="JavaScript">
function approve(type)
{
	var stuids='';
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++)  
	{
	  	 if(tablevos[i].type=="checkbox" && tablevos[i].checked==true)	    
	 	 {
	 	 	if(tablevos[i].value!="on")
	    		stuids +=tablevos[i].value+"@";	    	
		 }
   	}
   	if(stuids=='')
   	{
   		alert("<bean:message key='label.select'/>！");
   		return;
   	}
	if(type=='pz'){
		if(!confirm("<bean:message key='org.orgpre.orgpretable.approvalok'/>")){
			return false;
		}
	}else if(type=='bh'){
		if(!confirm("<bean:message key='org.orgpre.orgpretable.bohuiok'/>")){
			return false;
		}
	}
	if(type=='pz'){
		var hashvo=new ParameterSet();
		hashvo.setValue("personstr",stuids);
		hashvo.setValue("classid",'${courseTrainForm.r3101}');
		hashvo.setValue("msg","5");
		var request=new Request({method:'post',asynchronous:false,onSuccess:savePerson,functionId:'2020040012'},hashvo);
		function savePerson(outparamters){
			var flag=outparamters.getValue("flag");
			if(flag=="true"){
				var theurl="/train/request/permisView.do?b_query=link`classid=${courseTrainForm.r3101}`type="+type+"`stuids="+stuids;
			    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
			   	var return_vo= window.showModalDialog(iframe_url, 'trainClass_win2', 
	      				"dialogWidth:430px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");   
			    if(!return_vo)
					return;	   
				if(return_vo.flag=="true") 				
					changeInfor();
			}else{
				alert(flag);
			}
		}
	}else if(type=='bh'){
		var theurl="/train/request/permisView.do?b_query=link`classid=${courseTrainForm.r3101}`type="+type+"`stuids="+stuids;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
	   	var return_vo= window.showModalDialog(iframe_url, 'trainClass_win2', 
  				"dialogWidth:430px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");   
	    if(!return_vo)
			return;	   
		if(return_vo.flag=="true") 				
			changeInfor();
	}
}
function selectPerson(){
	var theurl="/train/request/selectpre.do?b_query=link`a_code=${courseTrainForm.a_code}`itemkey=${courseTrainForm.r3101}`nbase=all`preflag=1`winState=1";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	var return_vo= window.showModalDialog(iframe_url, 'trainClass_win2', 
      				"dialogWidth:600px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");	
    if(return_vo!=null&&return_vo.length>0){
    	var r3101 = '${param.r3101}';
    	if(!r3101)
    		r3101 = '${courseTrainForm.r3101}';
    		
    	var hashvo=new ParameterSet();
		hashvo.setValue("personstr",return_vo);
		hashvo.setValue("classid",r3101);
		hashvo.setValue("msg","1");
		var request=new Request({method:'post',asynchronous:false,onSuccess:savePerson,functionId:'2020040012'},hashvo);
		
	}
}
function searchPerson(){
	var theurl="/general/inform/search/generalsearch.do?b_set=link`a_code=${courseTrainForm.a_code}`itemkey=${courseTrainForm.r3101}`nbase=all`checkflag=1`winState=1";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	var return_vo= window.showModalDialog(iframe_url, 'trainClass_win2', 
      				"dialogWidth:750px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");	
    if(return_vo!=null&&return_vo.length>0){
    	var hashvo=new ParameterSet();
		hashvo.setValue("personstr",return_vo);
		hashvo.setValue("classid",'${courseTrainForm.r3101}');
		hashvo.setValue("msg","1");
		var request=new Request({method:'post',asynchronous:false,onSuccess:savePerson,functionId:'2020040012'},hashvo);
		
	}
}

function savePerson(outparamters){
	var flag=outparamters.getValue("flag");
	flag = getDecodeStr(flag);
	var personstr=outparamters.getValue("personstr");
	var r3101=outparamters.getValue("r3101");
	if(flag=="true"){
		var hashvo=new ParameterSet();
		hashvo.setValue("personstr",personstr);
		hashvo.setValue("r3101",r3101);
		var request=new Request({asynchronous:false,functionId:'2020040008'},hashvo);
		document.getElementById("spid").value="03";
		changeInfor();
	}else{
		alert(flag);
	}
}
function delPerson(){
	var stuids='';
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++) {
	  	 if(tablevos[i].type=="checkbox" && tablevos[i].checked==true){
	  	 	if(tablevos[i].value!="on")
	    		stuids +=tablevos[i].value+",";	    	
		 }
   	}
   	if(stuids==''){
   		alert("请选择将要删除的培训学员！");
   		return;
   	}
	if(!confirm("<bean:message key='gz.acount.determined.del'/>")){
		return false;
	}
   	var hashvo=new ParameterSet();
	hashvo.setValue("r4001",stuids);
	hashvo.setValue("r4005",'${courseTrainForm.r3101}');
	var request=new Request({asynchronous:false,functionId:'2020040011'},hashvo);
	changeInfor();
}
function changeInfor(){
	courseTrainForm.action="/train/request/trainRes.do?b_query=link&r3127=${courseTrainForm.r3127}&r3101=${courseTrainForm.r3101}&flag=3";
	courseTrainForm.submit();
}

function editMemoFild2(priFld,memoFldName,classid,r3127,dbname)
{
	var target_url="/train/resource/memoFld.do?b_query=link`type=9`priFld="+priFld+"`memoFldName="+memoFldName+"`classid="+classid+"`dbname="+dbname;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	var return_vo= window.showModalDialog(iframe_url, "memoFld_win", 
	              "dialogWidth:390px; dialogHeight:360px;resizable:no;center:yes;scroll:no;status:no");	
	if(!return_vo)
	   	return;	   
   	if(return_vo.flag=="true")
   {
		courseTrainForm.action="/train/request/trainRes.do?b_query=link&r3127="+r3127+"&r3101="+classid+"&flag=3";
		courseTrainForm.submit();	
   } 	
}

//备注字段
function editMemoFild(priFld,memoFldName,classid,r3127)
{
	var target_url="/train/resource/memoFld.do?b_query=link`type=9`priFld="+priFld+"`memoFldName="+memoFldName+"`classid="+classid;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	var return_vo= window.showModalDialog(iframe_url, "memoFld_win", 
	              "dialogWidth:390px; dialogHeight:360px;resizable:no;center:yes;scroll:no;status:no");	
	if(!return_vo)
	   	return;	   
   	if(return_vo.flag=="true")
   {
		courseTrainForm.action="/train/request/trainRes.do?b_query=link&r3127="+r3127+"&r3101="+classid+"&flag=3";
		courseTrainForm.submit();	
   } 	
}
//批量修改
function batcheditFild()
{
	var stuids='';
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++) {
	  	 if(tablevos[i].type=="checkbox" && tablevos[i].checked==true){
	  	 	if(tablevos[i].value!="on")
	    		stuids +=tablevos[i].value+",";	    	
		 }
   	}
   	if(stuids==''){
   		alert("请选择将要修改的培训学员！");
   		return;
   	}
	
	var target_url="/train/request/trainRes.do?b_batchedit=link";
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	var return_vo= window.showModalDialog(iframe_url, "memoFld_win", 
	              "dialogWidth:580px; dialogHeight:470px;resizable:no;center:yes;scroll:no;status:no");	
	if(!return_vo)
	   	return;	   
   	if(return_vo.flag=="true")
   {
   		var hashvo=new ParameterSet();
   		hashvo.setValue("stuids",stuids);
		hashvo.setValue("itemids",return_vo.itemids);
		hashvo.setValue("values",return_vo.values);
		hashvo.setValue("classid","${courseTrainForm.r3101}");
		var request=new Request({asynchronous:false,functionId:'2020040100'},hashvo);
		courseTrainForm.action="/train/request/trainRes.do?b_query=link&r3127=${courseTrainForm.r3127}&r3101=${courseTrainForm.r3101}&flag=3";
		courseTrainForm.submit();
   } 	
}
//编辑课程
function editRes(priFldValue,classid,r3127,dbname)
{
	var initValue='';
	var readonlyFilds='';
	var hideFilds ='';
	var hidepics = '';
	
	initValue='';//初始值	
	readonlyFilds='b0110,r4002,e0122,';//只读字段
	hideFilds ='r4001,r4005,r4013,r4015,';//隐藏字段
    hidepics = 'imgb0110,imge0122,';//需要隐藏的字段旁边的图片 img+字段名称
    
    var theurl='/train/traincourse/traindataAdd.do?b_query=link`fieldset=r40`initValue='+initValue+'`readonlyFilds='+readonlyFilds+'`hideFilds='+hideFilds+'`hidepics='+hidepics+'`priFldValue='+priFldValue+'`classid='+classid+'`dbname='+dbname;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	var return_vo= window.showModalDialog(iframe_url, 'trainCourse_win', 
      				"dialogWidth:700px; dialogHeight:410px;resizable:no;center:yes;scroll:yes;status:no");		    				
   if(!return_vo)
		return;	   
   if(return_vo.flag=="true")
   {   
		courseTrainForm.action="/train/request/trainRes.do?b_query=link&r3127="+r3127+"&r3101="+classid+"&flag=3";
		courseTrainForm.submit();	
   }
}
//导出excel (导出签名表)
function outTemplete(){
		var hashvo=new ParameterSet();	
		hashvo.setValue("model","1");
		hashvo.setValue("r3101",'${courseTrainForm.r3101}');
		var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'2020040018'},hashvo);
	}
//导出模板
function batchInOut(){
	var target_url="/train/request/trainsData.do?b_batchinout=link&flag=student";
	var fileName = window.showModalDialog(target_url, "memoFld_win", 
     "dialogWidth:460px; dialogHeight:500px;resizable:yes;center:yes;scroll:yes;status:no");

	 if(fileName) {
			window.location.target="_blank";
			window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+fileName;
			($('b_loaddown')).disabled=false;
	    }
}

function showfile(outparamters)
	{
		var outName=outparamters.getValue("outName");
		window.location.target="_blank";
		window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
	}
//导入excel
	function inputTemplete(r3101){
	var theurl='/train/request/import.do?br_selectfile=link`r3101='+r3101;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    var return_vo= window.showModalDialog(iframe_url, 'mytree_win', 
      		"dialogWidth:500px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");		    				
  	 if(return_vo){
  	 	var waitInfo=eval("wait");	//显示进度条
	    waitInfo.style.display="block";
   		form1.action="/train/request/import.do?b_exedata=link&r3127=${courseTrainForm.r3127}&r3101=${courseTrainForm.r3101}&flag=3";
      	form1.submit(); 
		}
	}
	//学员查询
	function search(r3101,r3127)
	{	
		var code = $F('code');
		var thecodeurl ="/train/traincourse/generalsearch.do?b_query=link&fieldsetid=r40"; 
		var return_vo= window.showModalDialog(thecodeurl, "", 
	              	"dialogWidth:750px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no");
	    if(return_vo!=null) 
	    {
	    	document.getElementById("searchstr").value=return_vo;
	    	courseTrainForm.action="/train/request/trainRes.do?b_query=link&r3127="+r3127+"&r3101="+r3101+"&flag=3";
			courseTrainForm.submit();	
	    }
	}
</script>
<html:form action="/train/request/trainRes" styleId="form1">
<html:hidden name="courseTrainForm" property="searchstr"/>
<%int i=0;%>
<div id='wait'
				style='position: absolute; top: 200; left: 250; display: none;'>
				<table border="1" width="400" cellspacing="0" cellpadding="4"
					class="table_style" height="87" align="center">
					<tr>
						<td class="TableRow" height=24>
							正在导入培训学员....
						</td>
					</tr>
					<tr>
						<td style="font-size: 12px; line-height: 200%" align=center>
							<marquee class="marquee_style" direction="right" width="300"
								scrollamount="5" scrolldelay="10">
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
<table border="0" cellspacing="0" cellpadding="0" style="margin-bottom: -12px;">
<tr>
<td>
<div class="fixedHeaderTr"  style="background-color: #FFFFFF;">
<table border="0">
	<tr>
		<td>	
			<logic:equal name="courseTrainForm" property="r3127" value="03">
				<logic:equal name="courseTrainForm"  property="spid" value="02">
					<hrms:priv func_id="3233081">
	          			<input type="button" name="tt" value="<bean:message key="info.appleal.state3" />"  class="mybutton" onclick="approve('pz');">
	        		</hrms:priv>
	        		<hrms:priv func_id="3233080">
	          			<input type="button" name="tt" value="<bean:message key="info.appleal.state2" />"  class="mybutton" onclick="approve('bh');">
	        		</hrms:priv>
				</logic:equal>
			</logic:equal>
		
			<logic:equal name="courseTrainForm" property="r3127" value="04">
				<logic:equal name="courseTrainForm"  property="spid" value="03">
					<hrms:priv func_id="3233082">
	          			<input type="button" name="tt" value="<bean:message key="performance.implement.handSelEmp" />"  class="mybutton" onclick="selectPerson();">
	        		</hrms:priv>
	        		<hrms:priv func_id="3233083">
	          			<input type="button" name="tt" value="<bean:message key="performance.implement.condiSelEmp" />"  class="mybutton" onclick="searchPerson();">
	        		</hrms:priv>
	
					<logic:notEqual name="courseTrainForm"  property="num" value="0">
					  <hrms:priv func_id="3233085">
	    				<input type="button" name="tt" value="<bean:message key="menu.gz.batch.update" />"  class="mybutton" onclick="batcheditFild();">
					  </hrms:priv>
					</logic:notEqual>
				</logic:equal>
				
				<logic:equal name="courseTrainForm"  property="spid" value="02">
					<hrms:priv func_id="3233081">
	          			<input type="button" name="tt" value="<bean:message key="info.appleal.state3" />"  class="mybutton" onclick="approve('pz');">
	        		</hrms:priv>
	        		<hrms:priv func_id="3233080">
	          			<input type="button" name="tt" value="<bean:message key="info.appleal.state2" />"  class="mybutton" onclick="approve('bh');">
	        		</hrms:priv>
				</logic:equal>			
			</logic:equal>

			<logic:notEqual name="courseTrainForm" property="r3127" value="06">
				<logic:notEqual name="courseTrainForm" property="num" value="0">
					<hrms:priv func_id="3233084">
						<input type="button" name="tt" value="<bean:message key="gz.acount.filter.delete" />" class="mybutton" onclick="delPerson();">
					</hrms:priv>
				</logic:notEqual>
			</logic:notEqual>

			<logic:equal name="courseTrainForm" property="spid" value="03">
				<logic:notEqual name="courseTrainForm"  property="num" value="0">
				   <hrms:priv func_id="3233086">
					<input type="button" name="tt" value="<bean:message key="button.export.signup.sheet" />"  class="mybutton" onclick="outTemplete();">
				   </hrms:priv>
				</logic:notEqual>	
			
				<logic:equal name="courseTrainForm" property="r3127" value="04">
					<hrms:priv func_id="3233087">
						<input type="button" name="tt" value="<bean:message key="button.download.template" />"  class="mybutton" onclick="batchInOut();">
					</hrms:priv>
					<hrms:priv func_id="3233088">	
						<input type="button" name="tt" value="<bean:message key="import.tempData" />"  class="mybutton" onclick="inputTemplete('${courseTrainForm.r3101}');">
					</hrms:priv>
				</logic:equal>	
			</logic:equal>
				<hrms:priv func_id="323308a">
					<input type="button" name="tt" value="<bean:message key="button.query" />"  class="mybutton" onclick="search('${courseTrainForm.r3101}','${courseTrainForm.r3127}');">
				</hrms:priv>
	    </td>
		<td>
		  &nbsp;&nbsp;
		  <bean:message key='lable.zp_plan.status'/>
          <hrms:optioncollection name="courseTrainForm" property="splist" collection="list" />
			<html:select name="courseTrainForm" property="spid" onchange="changeInfor();" style="width:80px;text-align:left">
				<html:options collection="list" property="dataValue" labelProperty="dataName"/>
			</html:select>
	    </td>
	</tr>
</table>
</div>
</td></tr>
<tr>
<td>
<table width="100%" border="0"  cellspacing="0" cellpadding="0" class="" style="border-collapse: collapse;">
	<tr>
		<logic:iterate id="element" name="courseTrainForm"  property="setlist" indexId="index">
			<logic:equal name="element" property="itemid" value="r4001">
			<td align="center" width="30" class="TableRow" nowrap>
                 <input type="checkbox" name="selbox" onclick="batch_select_all(this);" title='<bean:message key="label.query.selectall"/>'>
	       </td> 
			</logic:equal>
			<logic:notEqual name="element" property="itemid" value="r4001">
			<td align="center" class="TableRow" nowrap>
                 <bean:write  name="element" property="itemdesc" filter="true"/>
	       </td> 
	       </logic:notEqual>
		</logic:iterate>
		<logic:equal name="courseTrainForm" property="r3127" value="04">
    	<logic:equal name="courseTrainForm"  property="spid" value="03">
		<td align="center" class="TableRow" nowrap>
			操作
		</td>
		</logic:equal>
		</logic:equal>
	</tr>
	<hrms:paginationdb id="element" name="courseTrainForm" sql_str="courseTrainForm.sql" table="" 
	where_str="courseTrainForm.wherestr" columns="courseTrainForm.columns" 
	order_by="order by b0110,e0122,nbase,r4001" page_id="pagination" pagerows="${courseTrainForm.pagerows}">
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
          <bean:define id="dbname" name="element" property="nbase"></bean:define>
          <bean:define id="nid" name='element' property='r4001'/>
          <%String r4001 = SafeCode.encode(PubFunc.encrypt(nid.toString()));
          	String nbase = SafeCode.encode(PubFunc.encrypt(dbname.toString()));%>
    	<logic:iterate id="fielditem"  name="courseTrainForm"  property="setlist" indexId="index">
    		<logic:equal name="fielditem" property="itemid" value="r4001">
    		<td align="center" class="RecordRow" nowrap>
    			<bean:define id="nid" name='element' property='r4001'/>
    			<input type="checkbox" name="<%=r4001 %>" value="<%=r4001 %>">                                 
	      	</td> 
	      </logic:equal>
	      <logic:notEqual name="fielditem" property="itemid" value="r4001">
	      	<logic:equal name="fielditem" property="itemtype" value="N">
	      		<td align="right" class="RecordRow" nowrap>
	      		&nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;
	      		</td>
	      	</logic:equal>
	      	<logic:notEqual name="fielditem" property="itemtype" value="N">
    		<td align="left" class="RecordRow" nowrap>
               <logic:notEqual name="fielditem" property="codesetid" value="0">
          	   		<hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	   		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                    
               </logic:notEqual>
               <logic:equal name="fielditem" property="codesetid" value="0">
               	<logic:equal value="r4009" name="fielditem" property="itemid">
               		<logic:equal value="" name="element" property="${fielditem.itemid}">
               		<label style="width: 95%;text-align: center;">
               			<a href="javascript:editMemoFild2('<%=r4001 %>','r4009','${courseTrainForm.r3101}','${courseTrainForm.r3127}','<%=nbase %>');" style="color: #999999;">
               				未评估
               			</a>
               		</label>
               		</logic:equal>
               		<logic:notEqual value="" name="element" property="${fielditem.itemid}">
               			<a href="javascript:editMemoFild2('<%=r4001 %>','r4009','${courseTrainForm.r3101}','${courseTrainForm.r3127}','<%=nbase %>');">
               				&nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false"/>
               			</a>
               		</logic:notEqual>
               	</logic:equal>
               	<logic:notEqual value="r4009" name="fielditem" property="itemid">
                   &nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false"/>     
                </logic:notEqual>            
               </logic:equal>       
               &nbsp;                       
	      </td> 
	      </logic:notEqual>
	      </logic:notEqual>
    	</logic:iterate>
    	<logic:equal name="courseTrainForm" property="r3127" value="04">
    	<logic:equal name="courseTrainForm"  property="spid" value="03">
	    	<td align="center" class="RecordRow" nowrap>
	    		&nbsp;<a href='javascript:editRes("<%=r4001 %>","${courseTrainForm.r3101}","${courseTrainForm.r3127}","<%=nbase %>");'><img src="/images/edit.gif" border=0></a>
	    	</td>
	    </logic:equal>
    	</logic:equal>
    </tr>
    </hrms:paginationdb>      
</table>
</td></tr>
<tr><td>
<table  width="100%" border="0" class="RecordRowP">
	<tr>
		<td valign="bottom" class="tdFontcolor">
		<hrms:paginationtag name="courseTrainForm" pagerows="${courseTrainForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
		</td>
	    <td  align="right" nowrap class="tdFontcolor">
		     <p align="right"><hrms:paginationdblink name="courseTrainForm" property="pagination" nameId="courseTrainForm" scope="page">
			 </hrms:paginationdblink>
		</td>
	</tr>
</table>
</td></tr>
</table>

</html:form>
<script language='javascript' >			
	parent.parent.ril_body1.setSecondPage(3);
</script>