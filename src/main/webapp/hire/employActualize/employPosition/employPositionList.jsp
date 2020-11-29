<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.hire.employActualize.EmployPositionForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.valueobject.UserView,
				com.hrms.struts.constant.WebConstant" %>
<%@ page import="com.hrms.hjsj.sys.ResourceFactory"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script language="JavaScript" src="/js/constant.js"></script>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>
<%
  
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String unitid="";
	if(!userView.isSuper_admin())
			//unitid=userView.getUnit_id();
	{
		unitid=userView.getUnitIdByBusi("7");
		unitid = PubFunc.getTopOrgDept(unitid);
		if(unitid.trim().length()==3)
		{
		if(unitid.equals("UN`"))
			unitid="ALL";
			else
			unitid="-1";
		}
		else if(unitid.indexOf("`")==-1&&unitid.trim().length()>2){
			unitid=unitid.substring(2);
		}	
	}
	String noExitz0301=PubFunc.encrypt("-1");
%>

 <%
  	      EmployPositionForm employPositionForm=(EmployPositionForm)session.getAttribute("employPositionForm");	
  	      String fielditem1=(employPositionForm.getFielditem1()).toLowerCase();
  	      String fielditem2=(employPositionForm.getFielditem2()).toLowerCase();
       	  String picName="";
       	  String stateName="";
       	  String posCount=employPositionForm.getPosCount();
       	  String posState=employPositionForm.getPos_state();
       	  String hirePath=employPositionForm.getHirePath();
       	  String schoolPosition=employPositionForm.getSchoolPosition();
       	  String columnName=ResourceFactory.getProperty("e01a1.label");
       	  String isCode=employPositionForm.getIsCode();
       	  //if(hirePath.equals("01"))
       	  	//columnName=ResourceFactory.getProperty("hire.employActualize.interviewProfessional");
       	 // else if(hirePath==null||hirePath.trim().length()==0)
			//columnName=ResourceFactory.getProperty("e01a1.label")+"|"+ResourceFactory.getProperty("hire.employActualize.interviewProfessional");       	  
       	 if(schoolPosition!=null&&schoolPosition.length()>0)
       	       columnName=ResourceFactory.getProperty("e01a1.major.label");
       	  if(posState.equals("04"))
       	  {
       	  		picName="icon_published.gif";
       	  		stateName=ResourceFactory.getProperty("hire.button.relesae");
       	  }
       	  else if(posState.equals("09"))
       	  {
       	  		picName="icon_suspend.gif";
       	  		stateName=ResourceFactory.getProperty("hire.button.stop");
       	  }
       	  else if(posState.equals("06"))
       	  {		
       	  		picName="icon_fbyjs.gif";
  				stateName=ResourceFactory.getProperty("label.hiremanage.status6");
  		  }
  %>
<html>
  <head>
  <script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
  <script language='javascript'>
  var noExistZ0301="<%=noExitz0301%>";
function isValidDate(day, month, year) {
    if (month < 1 || month > 12) {
            return false;
        }
        if (day < 1 || day > 31) {
            return false;
        }
        if ((month == 4 || month == 6 || month == 9 || month == 11) &&
            (day == 31)) {
            return false;
        }
        if (month == 2) {
            var leap = (year % 4 == 0 &&
                       (year % 100 != 0 || year % 400 == 0));
            if (day>29 || (day == 29 && !leap)) {
                return false;
            }
        }
        return true;
    }
    
    
    
  
  
   function setqd(obj)
   {
   		var value='';
   		var obj3=eval("document.employPositionForm.professional");
   		for(var i=0;i<obj.options.length;i++)
   		{
   			if(obj.options[i].selected)
   				value=obj.options[i].value;
   		}
   		if(value=='01')
   		{
	 	     var obj2=eval("document.employPositionForm.posID");
   		     document.getElementById("pos_str").style.display='none';
	 	  	 obj3.style.display='block';
			 document.getElementById("pos_p").style.display='block';
	 	}
	 	else
	 	{
	 		obj3.style.display='none';
			document.getElementById("pos_p").style.display='none';
	 		document.getElementById("pos_str").style.display='block';
	 	}
	 		
   }
  
  
  
  
  
  	function showOrClose()
	{
		var obj=eval("aa");
		var obj2=eval("document.employPositionForm.isShowCondition");
		var obj3=eval("document.employPositionForm.professional");
		if(obj.style.display=='none')
		{
			obj.style.display='block';
			obj2.value="block";
			obj3.style.display='none';
			document.getElementById("pos_p").style.display='none';
		}
		else
		{
			obj.style.display='none';
			obj2.value="none";	
		}
	}
	
function openInputCodeDialog_self(codeid,mytarget) 
{
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".viewvalue",".value");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    	
    }
    codevalue='<%=(unitid.length()>=2?unitid:"")%>'  
   var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,1,0);
   thecodeurl="/system/codeselectposinputpos.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=1";
    var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	var aa=document.getElementsByName("s.viewvalue")
	aa[0].fireEvent("onChange");
}	
	
	
	
	
	
function setPosList()
{
	var aa=document.getElementsByName("s.value")
	if(trim(aa[0].value).length==0)
		return;
	var hashvo=new ParameterSet();
	hashvo.setValue("operator","3");
	var In_paramters="orgID="+aa[0].value;  
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000174'},hashvo);
}



function returnInfo(outparamters)
{
	var poslist=outparamters.getValue("poslist");	
	var aa=document.getElementsByName("posID")
	AjaxBind.bind(aa[0],poslist);
}
	
	
function sub(opt)
{
	var objs=new Array();
	objs[0]="s_startDate";
	objs[1]="e_startDate";
	objs[2]="s_endDate";
	objs[3]="e_endDate";

	var pagenum=document.getElementById("pagenum").value;
	var zhengzhengshu=/^[0-9]*[1-9][0-9]*$/;///正整数
	if(!zhengzhengshu.test(pagenum)){
		alert("每页显示条数请输入正整数!");
		return;
	}
	for(var n=0;n<objs.length;n++)
	{
	
			var aa=document.getElementsByName(objs[n])
			if(trim(aa[0].value).length!=0)
			{						
						 var myReg =/^(-?\d+)(\.\d+)?$/
						 if(IsOverStrLength(aa[0].value,10))
						 {
							 alert(DATE_FORMAT_IS_WRONG);
							 aa[0].focus();
							 return;
						 }
						 else
						 {
						 	if(trim(aa[0].value).length!=10)
						 	{
						 		 alert(DATE_FORMAT_IS_WRONG);
						 		 aa[0].focus();
								 return;
						 	}
							var year=aa[0].value.substring(0,4);
							var month=aa[0].value.substring(5,7);
							var day=aa[0].value.substring(8,10);
							if(!myReg.test(year)||!myReg.test(month)||!myReg.test(day)) 
						 	{
								 alert(DATE_FORMAT_IS_WRONG);
								 aa[0].focus();
								 return;
						 	}
						 	if(year<1900||year>2100)
						 	{
						 		 alert(YEAR_SCOPE_IS);
						 		 aa[0].focus();
								 return;
						 	}
						 	
						 	if(!isValidDate(day, month, year))
						 	{
								 alert(DATE_FORMAT_IS_WRONG);
								 aa[0].focus();
								 return;
						 	}
						 }
				}
	
	
	}
	
	

	var pf=document.employPositionForm.professional.value;

	var aa=document.getElementsByName("s.viewvalue")
	var ab=document.getElementsByName("s.value")
	var a_value=document.getElementsByName("value")
	var aa_value=document.getElementsByName("viewvalue")
	if(trim(aa[0].value).length!=0)
	{
		a_value[0].value=ab[0].value;
		aa_value[0].value=aa[0].value;
	}
	else
	{
		a_value[0].value="";
		aa_value[0].value="";
	}
	if(opt==0)
		document.employPositionForm.action="/hire/employActualize/employPosition.do?b_query=query&isStart=init";
	else
		document.employPositionForm.action="/hire/employActualize/employPosition.do?b_query=query&Professional="+pf;
	document.employPositionForm.submit();
	
}	






function opt(flag)
{
	
	// 1:发布 2暂停 3删除 4结束
	var desc="";
	if(flag==1)
	{
		desc=STATUS_RELEASE;
	}
	else if(flag==2)
	{
		desc=STATUS_STOP;
	}
	else if(flag==3)
	{
		desc=STATUS_DELETE;
	}
	else if(flag==4)
	{
		desc=STATUS_FINISH;
	}
	
	if(flag!=4)
	{
		if(confirm(CONFIRM_EXECUTE+desc+HIRE_OPERATION))
		{
			document.employPositionForm.action="/hire/employActualize/employPosition.do?b_opt=opt&opt="+flag;
			document.employPositionForm.submit();
		}
	}
	else
	{
		if(confirm(CONFIRM_EXECUTE+desc+HIRE_OPERATION))
		{
			document.employPositionForm.action="/hire/employActualize/employPosition.do?b_opt=opt&opt="+flag;
			document.employPositionForm.submit();
		}
	}
}

function selectAll(obj)
{
		var value=obj.checked;
   		for(var i=0;i<document.employPositionForm.elements.length;i++)
   		{
	   		
   			if(document.employPositionForm.elements[i].type=='checkbox')
   				document.employPositionForm.elements[i].checked=value;
   		
   		}

}
	
function openPosition(z0301)
{
     if(z0301==noExistZ0301)
    {
        alert(THIS_POSITION_DELETE_IN_ORG+"！");
        return;
    }
	window.open("/hire/demandPlan/positionDemand/positionDemandTree.do?b_read=read&operate=read&posState=<%=posState%>&z0301="+z0301,"_blank","width="+(window.screen.width-40)+",left=15,top=0,height="+(window.screen.height-80)+",scrollbars=yes, resizable=no,toolbar=no,menubar=no,status=no,location=no");
}

function setResumeCondition(z0301)
{
      if(z0301=='-1')
    {
        alert(THIS_POSITION_DELETE_IN_ORG+"！");
        return;
    }
	var thecodeurl="/hire/demandPlan/positionDemand/positionDemandTree.do?b_initCondition=open`posState=<%=posState%>`z0301="+z0301;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var winFeature="dialogWidth:830px; dialogHeight:490px;resizable:no;center:yes;scroll:yes;status:no";
	if (isIE6())
	  winFeature="dialogWidth:840px; dialogHeight:510px;resizable:no;center:yes;scroll:yes;status:no";
	var retvo=window.showModalDialog(iframe_url, null,winFeature);			
	if(retvo)
	{
    	var obj=new Object();
    	obj.opt=retvo.opt;
    	if(obj.opt=='1')
    	{
	         employPositionForm.action="/hire/employActualize/employResume.do?b_query=link&z0301="+z0301+"&personType=0&operate=init";
	         employPositionForm.submit();
    	}
	}
}

function editPosition(z0301)
{
	 if(z0301=='-1')
    {
        alert(THIS_POSITION_DELETE_IN_ORG+"！");
        return;
    }		 
	window.open("/hire/demandPlan/positionDemand/positionDemandTree.do?b_edit=edit&operate=edit&from=employPosition&posState=<%=posState%>&z0301="+z0301,"_blank","width="+(window.screen.width-40)+",left=15,height="+(window.screen.height-200)+",scrollbars=yes, resizable=no,toolbar=no,menubar=no,status=no,location=no");

}

function analyse(z0301,start_date,end_date)
{
     if(z0301==noExistZ0301)//z0301  涉及到加密的问题,将noExistZ0301的相关数据也进行了加密
    {
        alert(THIS_POSITION_DELETE_IN_ORG+"！");
        return;
    }
	window.open("/hire/zp_option/stat/weekly/weeklystmt.do?b_query=link&id="+z0301+"&start_date="+start_date+"&end_date="+end_date
				,"_blank","width="+window.screen.width+",left=1,hight="+window.screen.hight+",scrollbars=yes, resizable=no,toolbar=no,menubar=no,status=no,location=no");

}

function analyse2(z0301,zpName)
{
    if(z0301==noExistZ0301)
    {
        alert(THIS_POSITION_DELETE_IN_ORG+"！");
        return;
    }
    var zp_fullname=getEncodeStr(zpName);
	window.open("/hire/zp_options/stat/itemstat/showstatresult.do?b_query=link&init=2&zp_pos_id="+z0301+"&zp_fullname="+zp_fullname,"_blank","width="+window.screen.width+",hight="+window.screen.hight+",left=1,scrollbars=yes, resizable=no,toolbar=no,menubar=no,status=no,location=no");
}
function checkPosition(z0301,z0311,type,zpName,sdate,edate)
{
   var hashVo=new ParameterSet();
   hashVo.setValue("z0301",z0301);
   hashVo.setValue("z0311",z0311);
   hashVo.setValue("type",type);
   hashVo.setValue("zpName",zpName);
   hashVo.setValue("sdate",sdate);
   hashVo.setValue("edate",edate);
   //另外添加一个参数区别来自于招聘岗位,还是需求报批
   hashVo.setValue("fromflag","zpgw");
   var In_parameters="opt=1";
   
   var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:check_ok,functionId:'1010021116'},hashVo);			
		
}
function check_ok(outparameters)
{
   var msg=outparameters.getValue("msg");
   if(msg=='0')
   {
       alert(THIS_POSITION_DELETE_IN_ORG+"！");
       return; 
   }
   else
   {
       var type=outparameters.getValue("type");
       var z0301=outparameters.getValue("z0301");
       if(type=='0')
       {
           editPosition(z0301);
       }
       if(type=='1')
       {
          var zpName=outparameters.getValue("zpName");
          analyse2(z0301,zpName);
       }
       if(type=='2')
       {
         var sdate=outparameters.getValue("sdate");
         var edate=outparameters.getValue("edate");
         analyse(z0301,sdate,edate);
       }
       if(type=='3')
       {
            setResumeCondition(z0301);
       }
       if(type=='4')
       {
          openPosition(z0301);
       }
   }
}
function exportResume()
{
   var num=0;
   var records="";
   var  obj = document.getElementsByName("ids");
   var index=0;
   		for(var i=0;i<document.employPositionForm.elements.length;i++)
   		{
   			if(document.employPositionForm.elements[i].type=='checkbox'&&document.employPositionForm.elements[i].name!='dd')
   			{
   			    
   			    if(document.employPositionForm.elements[i].checked){
   			    	 num++;
   			    	 records+=","+obj[index].value;
   			    	
   			    }
   			   index++;
   			   
   			}
   		}
   if(num==0)
   {
      alert("请选择岗位！");
      return;
   }
   var hashvo=new ParameterSet();
    hashvo.setValue("tablename",'position');
	hashvo.setValue("records",records.substring(1));
	var In_paramters="flag=2";  
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'0521010011'},hashvo);
		
}
  function showfile(outparamters)
	{
		var outName=outparamters.getValue("outName");	
		if(outName=='-1')
		{
		    alert("请在参数设置中设置简历导出指标！");
		    return;
		}	
		if(trim(outName).length>0)
		{
			outName = decode(outName);
			var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
		}
	}
	function subb()
	{
	document.employPositionForm.action="/hire/employActualize/employPosition.do?b_query=link&select=1";
	document.employPositionForm.submit();
	}
    function setTPinput(){
    var InputObject=document.getElementsByTagName("input");
    for(var i=0;i<InputObject.length;i++){
        var InputType=InputObject[i].getAttribute("type");
        if(InputType!=null&&(InputType=="text"||InputType=="password")){
            InputObject[i].className=" "+"TEXT4";
        }
    }
}
  </script>
    <style type="text/css">
    .RecordRow_top {
	border: inset 1px #94B6E6;	
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;

	}	
    
    
    </style>
  </head>
  <hrms:themes></hrms:themes>
  <%
    String bosflag= userView.getBosflag();//得到系统的版本号
    if(bosflag!=null&&bosflag.equalsIgnoreCase("hcm")){
  %>
    <link href="/hire/css/layout.css" rel="stylesheet" type="text/css">
  <%
    }
  %>
  <body onload="setTPinput()">
  <html:form action="/hire/employActualize/employPosition">
  <table width='100%' class="normalbuttomp">
  <tr><td align='left'>
  <table width='100%' class="nopaddingTable"><tr ><td align='left'>
  	<bean:message key="hire.position.status"/><html:radio property="pos_state"  onclick='sub(0)'   value="04" name="employPositionForm" /><bean:message key="lable.performance.status.issueed"/> &nbsp;<html:radio property="pos_state"  onclick='sub(0)'  value="09" name="employPositionForm" /> <bean:message key="lable.performance.status.pause"/>&nbsp;  <html:radio property="pos_state" value="06"  onclick='sub(0)'  name="employPositionForm" /><bean:message key="label.hiremanage.status6"/> 
  </td>
  <td align='right' >
  
    <% if(schoolPosition!=null&&schoolPosition.length()>0){%>
  	  <Input type='button' value="<bean:message key="hire.filter.position1"/>" class='mybutton' onclick='showOrClose()' /> <Input type='button' value="<bean:message key="infor.menu.query"/>"  onclick='sub(1)'   class='mybutton' />
  <%}else{ %>
   <Input type='button' value="<bean:message key="hire.filter.position"/>" class='mybutton' onclick='showOrClose()' /> <Input type='button' value="<bean:message key="infor.menu.query"/>"  onclick='sub(1)'   class='mybutton' />
  <%} %>
  </td></tr></table>
  
  </td> 
  </tr>
  <tr><td>
  	<table id='aa' style='display:${employPositionForm.isShowCondition}' class="nopaddingTable">
	  <tr><td align='left' valign='middle' >
	  		<bean:message key="hire.position.startdate"/></td><td align="left">
	  		<input type="text" size='15'  name="s_startDate"  value="${employPositionForm.s_startDate}"  onclick='popUpCalendar(this,this, dateFormat,"","",true,false)'   maxlength="10"   /> 
			<bean:message key="hmuster.label.to"/>  
	  		<input type="text" size='15'  name="e_startDate"  value="${employPositionForm.e_startDate}"  onclick='popUpCalendar(this,this, dateFormat,"","",true,false)'   maxlength="10"   /> 
			
	  </td></tr>
	  <tr><td align='left' valign='middle'  >
	  		<bean:message key="hire.position.enddate"/></td><td>
	  		<input type="text" size='15'  name="s_endDate"  value="${employPositionForm.s_endDate}"  onclick='popUpCalendar(this,this, dateFormat,"","",true,false)'   maxlength="10"   /> 
			<bean:message key="hmuster.label.to"/>   
	  		<input type="text" size='15'  name="e_endDate"  value="${employPositionForm.e_endDate}"  onclick='popUpCalendar(this,this, dateFormat,"","",true,false)'   maxlength="10"   /> 
			
	  </td></tr>
	
	  <tr><td align='left'>
	  		&nbsp;&nbsp;招聘渠道 </td><td>
	  				<html:select name="employPositionForm" property="hirePath" size="1"  onchange='setqd(this)'  >
                              <html:optionsCollection property="hirePathList" value="dataValue" label="dataName"/>
        			</html:select> 
	  	<!-- 	<select name='posID' ><Option value=''></option></select>  -->
	  	
	  </td></tr>
	  
	  <tr><td align='left'>
	  		<bean:message key="hire.belong.org"/></td><td>
	  		
	  		<input type='hidden' name='s.value'    value="${employPositionForm.value}"   />  
	  		<input type='text' name='s.viewvalue'  onChange='setPosList()'   size='30' value="${employPositionForm.viewvalue}"   readonly/> 
	  		<span>
	  		<img  src="/images/code.gif" onclick='javascript:openInputCodeDialog_self("UM","s.viewvalue");' style='position:relative;top:5px;'/>&nbsp;		
			</span>
			<input type='hidden' name='value' value="${employPositionForm.value}" />
			<input type='hidden' name='viewvalue' value="${employPositionForm.viewvalue}" />
			<input type='hidden' name='isShowCondition' value="${employPositionForm.isShowCondition}" />				    
	  </td></tr>
	    <tr  id='pos_str' ><td align='left'>
	  		<bean:message key="hire.poisition"/></td><td>
	  				<html:select name="employPositionForm" property="posID" size="1">
                              <html:optionsCollection property="posIDList" value="dataValue" label="dataName"/>
        			</html:select> 
	  	<!-- 	<select name='posID' ><Option value=''></option></select>  -->
	  	
	  </td></tr>
	  <tr id="pos_p">
	  <td align='left'>
	  &nbsp;&nbsp;专业:
	  </td>
	  <%if(isCode!=null&&isCode.length()!=0&&isCode.equalsIgnoreCase("true")){ %>
	  <td>
	  	<html:select name="employPositionForm" property="professional" size="1">
	  							<html:option value=" "></html:option>
                              <html:optionsCollection property="pflist" value="dataValue" label="dataName"/>
        			</html:select> 
        			</td>
	  <%}else{ %>
	  <td>
	  <input type="text" name="professional" value="" />
	  </td>
	  <%} %>
	  </tr>
	</table>  
  </td></tr>
  </table>
  
  
  
  
  <table width='100%' align='center' cellpadding="0" cellspacing="0" class="nomalCommonTable"><tr><td class='RecordRow_top common_border_color' >
         &nbsp;
  </td></tr></table><!-- 用于生成那一条线-->
  	<table align='center' width='100%' border=0 class="nomalCommonTable">
  		<tr><td align='left' ><Image src='/images/icon_speaker.gif' /><%=stateName%>
  		<% if(schoolPosition!=null&&schoolPosition.length()>0){%>
  		<bean:message key="hire.in.position1"/><%}else{ %>
  			<bean:message key="hire.in.position"/>
  		<%} %>
  		<%=posCount%><bean:message key="hire.count"/></td>
  		<td align='right'>
  		
  		<bean:message key="system.option.an"/>
  		<html:select name="employPositionForm" property="order_item" size="1">
                              <html:optionsCollection property="orderItemList" value="dataValue" label="dataName"/>
        </html:select>  
  		<html:select name="employPositionForm" property="order_desc" size="1">
                              <html:optionsCollection property="orderDescList" value="dataValue" label="dataName"/>
        </html:select>
        &nbsp;&nbsp;<Input type='button' value="<bean:message key="label.zp_exam.sort"/>" onclick='sub(1)'  class='mybutton' /><!--排序  -->
        <input type='button' value="导出简历"  class='mybutton'  onclick='exportResume()' />
        <logic:equal value="dxt" property="returnflag" name="employPositionForm">
         <hrms:tipwizardbutton flag="retain" target="il_body" formname="employPositionForm"/> 
         </logic:equal>
  		</td></tr>
  	</table>
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable nomalCommonTable" >
   	  <thead>
           <tr>
		      <td align="center" class="TableRow" nowrap>
				<Input type='checkbox' name="dd" onclick='selectAll(this)'  />
		      </td> 
		      <td align="center" class="TableRow" nowrap>
				<bean:message key="general.mediainfo.state"/>
		      </td> 
		      <td align="center" class="TableRow" nowrap>
				<%=columnName%>
		      </td> 
		      <td align="center" class="TableRow" nowrap>
				<bean:message key="hire.belong.organazition"/>
		      </td> 
		      <td align="center" class="TableRow" nowrap>
				<bean:message key="rsbd.wf.start_d"/>
		      </td> 
		      <td align="center" class="TableRow" nowrap>
				<bean:message key="rsbd.wf.end_d"/>
		      </td> 
		      <td align="center" class="TableRow" style="padding-top:2px; " nowrap>
				<html:select name="employPositionForm" property="fielditem1" onchange='subb()' size="1">
                              <html:optionsCollection property="laborDemandList" value="dataValue" label="dataName"/>
        		</html:select>  
		      </td> 
		      <td align="center" class="TableRow" style="padding-top:2px; " nowrap>
				<html:select name="employPositionForm" property="fielditem2" onchange='subb()' size="1">
                              <html:optionsCollection property="laborDemandList" value="dataValue" label="dataName"/>
        		</html:select>  
		      </td> 
		      <td align="center" class="TableRow" nowrap>
				<bean:message key="hire.zp_options.resumequalitys"/>
		      </td> 
		      <td align="center" class="TableRow" nowrap>
				<bean:message key="hire.zp_options.zp_result"/>
		      </td> 
		      <td align="center" class="TableRow" nowrap>
				<bean:message key="hire.zp_options.curriculum.vitae.volume"/>
		      </td> 	
		      <td align="center" class="TableRow" nowrap>
				<bean:message key="kh.field.opt"/>
		      </td> 	        	        
         </tr>
   	  </thead>
  
       <% int i=0; String className="trShallow";
       	
        %>
   	   <hrms:extenditerate id="element" name="employPositionForm" property="posDemandListform.list" indexes="indexes" pagination="posDemandListform.pagination"  pageCount="${employPositionForm.pagerows}" scope="session">
			  <%
			  LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
	   		  String e0122=(String)abean.get("e0122");
			  if(i%2==0)
			  	className="trShallow";
			  else
			  	className="trDeep";
			  i++;
			  
			  %>
	<bean:define id="z0301" name="element" property="z0301" />
	<bean:define id="z0311" name="element" property="z0311" />
	 <%
	   String _z0301=PubFunc.encrypt(z0301.toString());
	 String _z0311=PubFunc.encrypt(z0311.toString());
	 %>
	   <tr class='<%=className%>' >
	   		<td align="center" class="RecordRow" nowrap>
	   			<hrms:checkmultibox name="employPositionForm" property="posDemandListform.select" value="true" indexes="indexes" />
	   			<input type="hidden" name="ids" value="<%=_z0301%>"/>
	   		</td>
	   		<td align="center" class="RecordRow" nowrap>
	  			<%
	  			if(!posState.equals("04"))
	  			{
	  			%>	
	  			 <Image src='/images/<%=picName%>' />
	   			<%
	   			}
	   			else
	   			{
	   				
	   				if(((String)abean.get("isOverTime")).equals("1"))
	   				{
	   					out.print("<Image src='/images/guoqi_youxiaoqi.gif' />");
	   				}
	   				else
	   				{
	   					out.print("<Image src='/images/icon_published.gif' />");
	   				}
	   			%>
	   			
	   			<%
	   			}
	   			%>
	   		</td>
	   		<td align="left" class="RecordRow" nowrap>
	   			<a href='javascript:openPosition("<%=_z0301%>")' >
	   				<bean:write name="element" property="codeitemdesc" filter="false"/>
	   			</a>
	   		</td>
	   		<td align="left" class="RecordRow" title="<hrms:orgtoname codeitemid='<%=e0122%>' level="10"/>" nowrap>
	   			<bean:write name="element" property="org" filter="false"/>
	   		</td>
	   		<td align="right" class="RecordRow" nowrap>
	   			<bean:write name="element" property="startdate" filter="false"/>
	   		</td>
	   		<td align="right" class="RecordRow" nowrap>
	   			<bean:write name="element" property="enddate" filter="false"/>
	   		</td>
	   		
	   		<bean:define id="eventen" name="element" property="<%=fielditem1%>" />
								<hrms:showitemmemo showtext="showtext" itemtype="M" setname=""
									tiptext="tiptext" text="${eventen}"></hrms:showitemmemo>
								<td align="left" class="RecordRow" ${tiptext}  nowrap>
									${showtext}
								</td>
	   		
	   		
	   		
	   		
	   		<bean:define id="eventen" name="element" property="<%=fielditem2%>" />
								<hrms:showitemmemo showtext="showtext" itemtype="M" setname=""
									tiptext="tiptext" text="${eventen}"></hrms:showitemmemo>
								<td align="left" class="RecordRow" ${tiptext}  nowrap>
									${showtext}
										</td>
	   		
	   		<td align="center" class="RecordRow" nowrap><!-- 简历质量 -->
	  			<a href='javascript:analyse2("<%=_z0301%>","<bean:write name="element" property="codeitemdesc" filter="false"/>")' >
	  			<Image border=0 src='/images/icon_ypjlfx.gif' />
	  			</a>
	   		</td>
	   		<td align="center" class="RecordRow" nowrap><!-- 招聘效果 -->
	  			<a href='javascript:analyse("<%=_z0301%>","<bean:write name="element" property="startdate" filter="false"/>","<bean:write name="element" property="enddate" filter="false"/>")'>
	  			<Image border='0' src='/images/icon_fbxgfx.gif' />
	  			</a>
	   		</td>
	   		<td align="right" class="RecordRow" nowrap>
	  			<bean:write name="element" property="resumeNum" filter="false"/>
	  		</td>
	   		<td align="left" class="RecordRow" nowrap>
	   			<a href='/hire/employActualize/employResume.do?b_query=link&z0301=<%=_z0301%>&personType=0&operate=init&from=zpgw'>
	   			<bean:message key="hire.examine.employ"/><!-- 查看应聘 -->
	   			</a>
	   			<!--  修改-->
	   			<hrms:priv func_id="3102201"> &nbsp;&nbsp; <a href='javascript:checkPosition("<%=_z0301%>","<%=_z0311%>","0","","","")'> <bean:message key="kq.report.update"/></a> </hrms:priv>
	   			<!-- 简历过滤 -->
	   			<hrms:priv func_id="3102202"> &nbsp;&nbsp; <a href='javascript:checkPosition("<%=_z0301%>","<%=_z0311%>","3","","","")'> <bean:message key="hire.resume.filter"/> </a> </hrms:priv>
	   		<logic:equal value="1" name="employPositionForm" property="canshow">
	   				<a href='<bean:write name="element" property="href" filter="false"/>' target="il_body"> 匹配筛选 </a> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	   			</logic:equal>
	   		
	   		</td>
	   
	   
	    </tr>
       </hrms:extenditerate>
       <tr>
       <td colspan="12">
       		   <table width="100%"   class='RecordRowP'  align="center">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				 <bean:message key="hmuster.label.d"/>
				<bean:write name="employPositionForm" property="posDemandListform.pagination.current" filter="true" />
				<bean:message key="hmuster.label.paper"/><bean:message key="hmuster.label.total"/>
				<bean:write name="employPositionForm" property="posDemandListform.pagination.count" filter="true" />
				<bean:message key="label.every.row"/> <bean:message key="hmuster.label.total"/>
				<bean:write name="employPositionForm" property="posDemandListform.pagination.pages" filter="true" />
				<bean:message key="hmuster.label.paper"/>
				&nbsp;&nbsp; 每页显示<html:text property="pagerows" styleId="pagenum" name="employPositionForm" size="3"></html:text>条&nbsp;&nbsp;<a href="javascript:sub(0);">刷新</a>
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="employPositionForm" property="posDemandListform.pagination" nameId="posDemandListform">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
       </td>
       </tr>
   </table>
		

  <table  width="98%" align="center" class="nomalCommonTable">
	<tr><td align='left'>
  <Image src='/images/icon_speaker.gif' />
  <% if(schoolPosition!=null&&schoolPosition.length()>0){%>
  	 <bean:message key="position.status1"/>：&nbsp;&nbsp;
  <%}else{ %>
   <bean:message key="position.status"/>：&nbsp;&nbsp;
  <%} %>
 
  <Image src='/images/icon_published.gif' /><bean:message key="org.performance.Published"/>&nbsp;
  <Image src='/images/icon_suspend.gif' /><bean:message key="label.commend.stop"/>&nbsp;
  <Image src='/images/icon_fbyjs.gif' /><bean:message key="label.commend.finish"/>&nbsp;
  <Image src='/images/guoqi_youxiaoqi.gif' />
  <% if(schoolPosition!=null&&schoolPosition.length()>0){%>
  	 <bean:message key="hire.release.finish1"/>
  <%}else{ %>
  <bean:message key="hire.release.finish"/>
  <%} %>
  </td><td align='right' >
 
  <%
  if(posState.equals("04"))
  {
  if(userView.hasTheFunction("3102203")){
  	out.print("&nbsp;<Input type='button' value='"+ResourceFactory.getProperty("hire.button.relesae")+"' onclick='opt(1)' class='mybutton' disabled />");
  	}
  if(userView.hasTheFunction("3102204")){
  	out.print("&nbsp;<Input type='button' value='"+ResourceFactory.getProperty("hire.button.stop")+"' onclick='opt(2)' class='mybutton' />");
  	}
  if(userView.hasTheFunction("3102206")){
  	out.print("&nbsp;<Input type='button' value='"+ResourceFactory.getProperty("hire.button.finish")+"' onclick='opt(4)' class='mybutton' />");
  	}
  if(userView.hasTheFunction("3102205")){
  	out.print("&nbsp;<Input type='button' value='"+ResourceFactory.getProperty("hire.button.delete")+"' onclick='opt(3)'  class='mybutton'  disabled />");
    } 
  }
  else if(posState.equals("09"))
  {
    if(userView.hasTheFunction("3102203")){
  	out.print("&nbsp;<Input type='button' value='"+ResourceFactory.getProperty("hire.button.relesae")+"' onclick='opt(1)' class='mybutton'/>");
  	}
  	if(userView.hasTheFunction("3102204")){
  	out.print("&nbsp;<Input type='button' value='"+ResourceFactory.getProperty("hire.button.stop")+"' onclick='opt(2)' class='mybutton' disabled  />");
  	}
  	if(userView.hasTheFunction("3102206")){
  	out.print("&nbsp;<Input type='button' value='"+ResourceFactory.getProperty("hire.button.finish")+"' onclick='opt(4)' class='mybutton' />");
  	}
  	if(userView.hasTheFunction("3102205")){
  	out.print("&nbsp;<Input type='button' value='"+ResourceFactory.getProperty("hire.button.delete")+"' onclick='opt(3)'  class='mybutton'  disabled />");
    }
  }
   else if(posState.equals("06"))
  {
    if(userView.hasTheFunction("3102203")){
  	out.print("&nbsp;<Input type='button' value='"+ResourceFactory.getProperty("hire.button.relesae")+"' onclick='opt(1)' class='mybutton'/>");
  	}
  	  if(userView.hasTheFunction("3102204")){
  	out.print("&nbsp;<Input type='button' value='"+ResourceFactory.getProperty("hire.button.stop")+"' onclick='opt(2)' class='mybutton' disabled  />");
  	}
  	if(userView.hasTheFunction("3102206")){
  	out.print("&nbsp;<Input type='button' value='"+ResourceFactory.getProperty("hire.button.finish")+"' onclick='opt(4)' class='mybutton' disabled  />");
  	}
  	  if(userView.hasTheFunction("3102205")){
  	out.print("&nbsp;<Input type='button' value='"+ResourceFactory.getProperty("hire.button.delete")+"' onclick='opt(3)'  class='mybutton' />");
   } 
  }
  %>
  <input type='button' value="导出简历"  class='mybutton'  onclick='exportResume()' />
  </td></tr></table>
  
  </html:form>
  
  <script language='javascript'>
 var _obj=document.getElementsByName("hirePath");
 var _value='';
 for(var i=0;i<_obj[0].options.length;i++)
 {
   			if(_obj[0].options[i].selected)
   				_value=_obj[0].options[i].value;
  }
  if(_value=='01')
  	 document.getElementById("pos_str").style.display='none';
  else{
  	 document.getElementById("pos_p").style.display='none';
  }
  </script>
  
  
  </body>
</html>
