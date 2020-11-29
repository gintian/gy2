<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/common.js"></script>
<script language="JavaScript" src="/train/traincourse/traindata.js"></script>
<script language="JavaScript">
function delRecord(){
	
	var tablevos=document.getElementsByTagName("input");
	var nid="";
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].type=="checkbox"&&tablevos[i].name!="selbox"){
	     	if(tablevos[i].checked){
	     		nid += tablevos[i].value+",";
	     	}
		 }
     }
     if(nid!=null&&nid.length>0){
     	if(!confirm("<bean:message key='train.b_plan.del'/>"))
			return false;
     	var hashvo=new ParameterSet();
		hashvo.setValue("r3101",nid);
		hashvo.setValue("checkflag","del");
		var request=new Request({method:'post',asynchronous:false,onSuccess:checkDelOk,functionId:'2020050011'},hashvo);	
     }else{
     	alert("<bean:message key='train.b_plan.selectdel'/>");
     	return false;
     }
}
function abolishTrains(){
	
	var tablevos=document.getElementsByTagName("input");
	var nid="";
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].type=="checkbox"&&tablevos[i].name!="selbox"){
	     	if(tablevos[i].checked){
	     		nid += tablevos[i].value+",";
	     	}
		 }
     }
     if(nid!=null&&nid.length>0){
     	if(!confirm("<bean:message key='train.b_plan.delabolishtrain'/>"))
			return false;
     	var hashvo=new ParameterSet();
		hashvo.setValue("r3101",nid);
		hashvo.setValue("checkflag","abolish");
		var request=new Request({method:'post',asynchronous:false,onSuccess:checkDelOk,functionId:'2020050011'},hashvo);	
     }else{
     	alert("<bean:message key='train.b_plan.abolishtrain'/>");
     	return false;
     }
}
function checkDelOk(outparamters){
	var flag = outparamters.getValue("flag");
	if(flag=='ok'){
		var exper = outparamters.getValue("exper");
		if(exper!=null&&exper.length>10){
			alert(getDecodeStr(exper));
		}
		trainCourseForm.action="/train/b_plan/train.do?b_query=link&r2501=${param.r2501}&model=${param.model}&b0110=${param.b0110}&e0122=${param.e0122}&spflag=${param.spflag}";
		trainCourseForm.submit();
	}
}
function introdTrains(){
	var theurl="/train/b_plan/introdTrain.do?b_query=link`r2501=${param.r2501}`model=${param.model}`b0110=${param.b0110}`e0122=${param.e0122}&spflag=${param.spflag}";
	if(window.$URL)
		theurl = $URL.encode(theurl);
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   	var return_vo= window.showModalDialog(iframe_url, 'trainClass_win2', 
      				"dialogWidth:800px; dialogHeight:620px;resizable:no;center:yes;scroll:yes;status:no");	
    trainCourseForm.action="/train/b_plan/train.do?b_query=link&r2501=${param.r2501}&model=${param.model}&b0110=${param.b0110}&e0122=${param.e0122}&spflag=${param.spflag}";
	trainCourseForm.submit();				
}
//添加某培训计划的培训班
function addTrains(b0110,e0122,model,r2501){
	var initValue='';
	var readonlyFilds='';
	var hideFilds ='';
	var hidepics = '';
	var a_code='';
    var isUnUmRela = '';
    
	var date = new Date(); 
	var currentYear=date.getFullYear();
	var currentMonth=date.getMonth()+1;
	var currentDay=date.getDate();
	var now = currentYear+'-'+currentMonth+'-'+currentDay;

	if(model=='1')
		initValue='r3127:03,r3118:'+now+',r3125:'+r2501;//初始值
	else if(model=='2')
		initValue='r3127:03,r3118:'+now+',r3125:'+r2501;//初始值
		
	if(e0122=='' && b0110!='')
		a_code='UN'+b0110;
	else if(e0122!='' && b0110!='')
		a_code='UM'+e0122;

	readonlyFilds='b0110,e0122,r3104,r3103,r3120,r3121,r3126,r3127,r3128,r3125,';//只读字段
	hideFilds ='';//隐藏字段 r3111,r3112,
    hidepics = 'imgr3127,';//需要隐藏的字段旁边的图片	
	isUnUmRela = 'true';
	var theurl="/train/traincourse/traindataAdd.do?b_query=link`fieldset=r31`a_code="+a_code+'`initValue='+getEncodeStr(initValue)+'`readonlyFilds='+readonlyFilds+'`hideFilds='+hideFilds+'`hidepics='+hidepics+'`isUnUmRela='+isUnUmRela;
    if(window.$URL)
    	theurl = $URL.encode(theurl);
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   	var return_vo= window.showModalDialog(iframe_url, 'trainClass_win2', 
      				"dialogWidth:780px; dialogHeight:640px;resizable:no;center:yes;scroll:yes;status:no");	
   if(return_vo){
		trainCourseForm.action="/train/b_plan/train.do?b_query=link&r2501=${param.r2501}&model=${param.model}&b0110=${param.b0110}&e0122=${param.e0122}&spflag=${param.spflag}";
		trainCourseForm.submit();  	
	}			
}
//编辑培训计划对应的培训班
function editRelaTrain(priFldValue,model,r2501,b0110,e0122){
	var initValue='';
	var readonlyFilds='';
	var hideFilds ='';
	var hidepics = '';
	var a_code='';
    var isUnUmRela = 'true';

	readonlyFilds='b0110,e0122,r3104,r3103,r3120,r3121,r3126,r3127,r3128,r3125,';//只读字段
	hideFilds ='';//隐藏字段 r3111,r3112,
    hidepics = 'imgr3127,';//需要隐藏的字段旁边的图片	
	
	var theurl="/train/traincourse/traindataAdd.do?b_query=link`fieldset=r31`a_code="+a_code+'`initValue='+initValue+'`readonlyFilds='+readonlyFilds+'`hideFilds='+hideFilds+'`hidepics='+hidepics+'`isUnUmRela='+isUnUmRela+'`priFldValue='+priFldValue;
	if(window.$URL)
    	theurl = $URL.encode(theurl);
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   	var return_vo= window.showModalDialog(iframe_url, 'trainClass_win2', 
      				"dialogWidth:700px; dialogHeight:620px;resizable:no;center:yes;scroll:yes;status:no");	
   if(!return_vo)
		return;	   
   if(return_vo.flag=="true")
	{   
		trainCourseForm.action="/train/b_plan/train.do?b_query=link&r2501=${param.r2501}&model=${param.model}&b0110=${param.b0110}&e0122=${param.e0122}&spflag=${param.spflag}";
		trainCourseForm.submit();  	
	}    				
}

function openw(url){
	var ww = 800;//window.screen.width-5;
	var hh = 600;//window.screen.height - 40;
	window.open(url, "_new","toolbar=no,location=no,directories=no,menubar=no,scrollbars=yes,resizable=no,status=no,top=0,left=0,width="+ww+",height="+hh);
   
}
function eventDesc(id){
	var thecodeurl ="/train/b_plan/train.do?b_event=link`id=r3117`classid="+id;
	if(window.$URL)
		thecodeurl = $URL.encode(thecodeurl);
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    openw(iframe_url);     
}
</script>
<html:form action="/train/b_plan/train.do?b_query=link&r2501=${param.r2501}&model=${param.model}&b0110=${param.b0110}&e0122=${param.e0122}&spflag=${param.spflag}">
<%int i=0;%>

<table border="0" cellspacing="0"  align="left" cellpadding="0">
<tr><td>
<table  width="400" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td style="padding-bottom: 5px;">
		<bean:define id="idr2501" value="${param.r2501}" />
		<logic:notEqual value="" name="idr2501">
			<logic:equal name="trainCourseForm"  property="edit" value="true">
			<hrms:priv func_id="32322">
			<input type="button" value="<bean:message key='button.new.add'/>" class="mybutton" onclick="addTrains('${param.b0110}','${param.e0122}','${param.model}','${param.r2501}');">
			</hrms:priv>
			<hrms:priv func_id="32323">
			<input type="button" value="<bean:message key='button.setfield.delfield'/>" class="mybutton" onclick="delRecord();">
			</hrms:priv>
			<hrms:priv func_id="32324">
			<input type="button" value="<bean:message key='kh.field.introduce'/>" class="mybutton" onclick="introdTrains();">
			</hrms:priv>
			<hrms:priv func_id="32325">
			<input type="button" value="<bean:message key='button.abolish'/>" class="mybutton" onclick="abolishTrains();">
			</hrms:priv>
			<!-- <hrms:menubar menu="menu1" id="menubar1">					
				<hrms:menuitem name="mitem1" label="button.new.add" function_id="32322" icon="" url="addTrains('${param.b0110}','${param.e0122}','${param.model}','${param.r2501}');"  />
				<hrms:menuitem name="mitem2" label="button.setfield.delfield" function_id="32323" icon="" url="delRecord();"  />
				<hrms:menuitem name="mitem3" label="kh.field.introduce" function_id="32324" icon="" url="introdTrains();"/>
				<hrms:menuitem name="mitem4" label="button.abolish" function_id="32325" icon="" url="abolishTrains();"/>					
			</hrms:menubar> -->
			</logic:equal>
		</logic:notEqual>
		</td>
	</tr>
</table>
</td></tr>
<tr><td>
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="ListTable1">
	<thead>
	<tr>
		<logic:iterate id="element" name="trainCourseForm"  property="itemlist" indexId="index">
			<logic:equal name="element" property="itemid" value="r3101">
    		<td width="30" align="center" class="TableRow" nowrap>
    			<input type="checkbox" name="selbox" onclick="batch_select_all(this);" title='<bean:message key="label.query.selectall"/>'>                              
	      	</td> 
	      </logic:equal>
	      <logic:notEqual name="element" property="itemid" value="r3101">
			<td align="center" class="TableRow" nowrap>
                 &nbsp;<bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
	       </td>
	       </logic:notEqual>
	       
		</logic:iterate>
		<logic:equal name="trainCourseForm"  property="edit" value="true">
		<td align="center" width="60" class="TableRow" nowrap>&nbsp;<bean:message key='system.infor.oper'/>&nbsp;</td> 
		</logic:equal>
	</tr>
	</thead>
	<hrms:paginationdb id="element" name="trainCourseForm" sql_str="trainCourseForm.sql" table="" 
	where_str="trainCourseForm.wherestr" columns="trainCourseForm.columns" 
	order_by="order by r3101" page_id="pagination" pagerows="${trainCourseForm.pagerows}">
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
          <bean:define id="nid" name='element' property='r3101'/>
          <%String r3101 = SafeCode.encode(PubFunc.encrypt(nid.toString())); %>
    	<logic:iterate id="fielditem"  name="trainCourseForm"  property="itemlist" indexId="index">
    		<logic:equal name="fielditem" property="itemid" value="r3101">
    		<td align="center" class="RecordRow" nowrap>
    			<input type="checkbox" name="${nid}" value="<%=r3101 %>">                                 
	      	</td> 
	      </logic:equal>
	      <logic:notEqual name="fielditem" property="itemid" value="r3101">
		      <logic:equal name="fielditem" property="itemid" value="r3117">
				<td align="center"class="RecordRow" nowrap>
	                 &nbsp; <a href="###" onclick='eventDesc("<%=r3101 %>");'> 
	          	   		<img src="/images/view.gif" alt="浏览" border="0">
	            	 </a>&nbsp;
		       </td>
		       </logic:equal>
		       <logic:notEqual name="fielditem" property="itemid" value="r3117">
		    		<td align="left" class="RecordRow" nowrap>
		               <logic:notEqual name="fielditem" property="codesetid" value="0">
		          	   		<hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
		          	   		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;            
		               </logic:notEqual>
		               <logic:equal name="fielditem" property="codesetid" value="0">
		                   &nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;                 
		               </logic:equal>                              
			      </td> 
			    </logic:notEqual>
	      </logic:notEqual>
    	</logic:iterate>
    	<logic:equal name="trainCourseForm"  property="edit" value="true">
    	<td align="center" class="RecordRow" nowrap>
    		&nbsp;<a href='javascript:editRelaTrain("<%=r3101 %>","${param.model}","${param.r2501}","${param.b0110}","${param.e0122}");'><img src="/images/edit.gif" border=0></a>&nbsp;                                
	    </td> 
	    </logic:equal>
    </tr>
    </hrms:paginationdb>      
</table>
</td></tr>
<tr><td>
<table  width="100%" border="0" class="RecordRowP" cellpadding="0" cellspacing="0">
	<tr>
		<td valign="middle" class="tdFontcolor" nowrap>
		&nbsp;<hrms:paginationtag name="trainCourseForm" pagerows="${trainCourseForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
		</td>
	    <td align="right" nowrap class="tdFontcolor">
		     <hrms:paginationdblink name="trainCourseForm" property="pagination" nameId="trainCourseForm" scope="page">
			</hrms:paginationdblink>
		</td>
		<td>&nbsp;</td>
	</tr>
</table>
</td></tr>
</table>

</html:form>
