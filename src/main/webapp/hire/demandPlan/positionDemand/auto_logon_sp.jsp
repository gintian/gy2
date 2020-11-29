<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<%@ page import="java.util.*,
				com.hjsj.hrms.actionform.hire.demandPlan.PositionDemandForm,
				org.apache.commons.beanutils.LazyDynaBean,
				com.hrms.struts.valueobject.UserView,
				com.hrms.struts.constant.WebConstant" %>
<%@ page import="com.hrms.hjsj.sys.ResourceFactory"%>				
				
<%
  
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String unitid=userView.getUnit_id();
	if(unitid.indexOf("`")==-1)
		unitid=unitid.length()>=2?unitid.substring(2):"";
	String z0301=request.getParameter("z0301");
	String posState=request.getParameter("posState");
%>
<style type="text/css">
.RecordRow {
	border: inset 1px #94B6E6;
	BACKGROUND-COLOR: #FFFFFF;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;

}

.RecordRow_top {
	border: inset 1px #94B6E6;
	BACKGROUND-COLOR: #FFFFFF;
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;

}

.tabHeader{
   COLOR : #103B82;
   FONT-SIZE: 12px;
   vertical-align : bottom;
   background-image:url(/images/tabCenter.gif);
   background-position : center top;
   background-repeat: no repeat; 
   BACKGROUND-COLOR:#4680D2;
}


</style>
  <%
			PositionDemandForm positionDemandForm=(PositionDemandForm)session.getAttribute("positionDemandForm");
			ArrayList positionDemandDescList=positionDemandForm.getPositionDemandDescList();
			String positionState=positionDemandForm.getPositionState();
			String z0325=positionDemandForm.getOrgUM();
			String z0321=positionDemandForm.getOrgUN();
			String isOrgWillTableIdDefine=positionDemandForm.getIsOrgWillTableIdDefine();
			String showUMCard=positionDemandForm.getShowUMCard();
			String zpchanel=positionDemandForm.getZpchanel();
			String spRelation = positionDemandForm.getSpRelation();
			String spcount = positionDemandForm.getSpcount();
			String zpappfalg = positionDemandForm.getZpappfalg();
			
  %>

<script language='javascript' >

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
    codevalue='<%=unitid%>'
   var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,1,0);
   thecodeurl="/system/codeselectposinputpos.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=1";
    var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	var aa=document.getElementsByName("positionDemandDescList[0].viewvalue")
	aa[0].fireEvent("onChange");
}


function setPosList()
{
	var aa=document.getElementsByName("positionDemandDescList[0].value")
	var hashvo=new ParameterSet();
	hashvo.setValue("operator","1");
	var In_paramters="orgID="+aa[0].value;  
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000174'},hashvo);
}

function returnInfo(outparamters)
{
	var poslist=outparamters.getValue("poslist");	
	var aa=document.getElementsByName("positionDemandDescList[1].value")
	AjaxBind.bind(aa[0],poslist);
}

function validatePos()
{
	var a=document.getElementsByName("positionDemandDescList[1].value")
	var aa=document.getElementsByName("positionDemandDescList[0].value")
	if(a[0].value=='-1')
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("operator","2");
		var In_paramters="orgID="+aa[0].value;  
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'3000000174'},hashvo);
	}
}


function returnInfo2(outparamters)
{
	var allowLength=outparamters.getValue("allowLength");	
	var existItemid=outparamters.getValue("existItemid");
	var orgID=outparamters.getValue("orgID");
	var newValue=outParamters.getValue("newValue");
	var theArr=new Array(allowLength,existItemid,orgID,newValue); 
    thecodeurl="addPos.jsp"; 
    var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogWidth:430px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
    if(popwin)
    {
    	var hashvo=new ParameterSet();
		hashvo.setValue("posID",popwin[0]);
		hashvo.setValue("posName",popwin[1]);
		var In_paramters="orgID="+orgID;  

		var aa=document.getElementsByName("positionDemandDescList[1].value")
		var a_Option=aa[0].options[aa[0].options.length-1];
		 aa[0].options[aa[0].options.length-1]=new Option(popwin[1],orgID+popwin[0]);		 
		 aa[0].options[aa[0].options.length]=a_Option;
		 aa[0].options[aa[0].options.length-2].selected=true;
		 var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo3,functionId:'3000000175'},hashvo);

    }
}


function returnInfo3(outparamters)
{

}


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
	
	function validateData(obj,itemdesc)
	{
		var dd=true;
		if(trim(obj.value).length!=0)
		{						
							 var myReg =/^(-?\d+)(\.\d+)?$/
							 if(IsOverStrLength(obj.value,10))
							 {
								 alert(itemdesc+RIGHT_FORMAT_IS+"！");
								 return false;
							 }
							 else
							 {
							 	if(trim(obj.value).length!=10)
							 	{
							 		 alert(itemdesc+RIGHT_FORMAT_IS+"！");
									 return false;
							 	}
								var year=obj.value.substring(0,4);
								var month=obj.value.substring(5,7);
								var day=obj.value.substring(8,10);
								if(!myReg.test(year)||!myReg.test(month)||!myReg.test(day)) 
							 	{
									 alert(itemdesc+RIGHT_FORMAT_IS+" ！");
									 return false;
							 	}
							 	if(year<1900||year>2100)
							 	{
							 		 alert(itemdesc+YEAR_SCORPE+" ！");
									 return false;
							 	}
							 	
							 	if(!isValidDate(day, month, year))
							 	{
									 alert(itemdesc+RIGHT_FORMAT_IS+" ！");
									 return false;
							 	}
							 }
			}
			return dd
	}


var beforevalue;
var beforevalue2;
function sub()
{
	 	<% int m=0;  %>
		<logic:iterate  id="element"    name="positionDemandForm"  property="positionDemandDescList" indexId="index"> 
				<logic:equal name="element" property="itemtype" value="N">
					var a<%=m%>=document.getElementsByName("positionDemandDescList[<%=m%>].value")
					if(a<%=m%>[0].value!='')
					{
					 var myReg =/^(-?\d+)(\.\d+)?$/
					 if(!myReg.test(a<%=m%>[0].value)) 
					 {
						alert("<bean:write  name="element" property="itemdesc"/>"+PLEASE_INPUT_NUMBER+"！");
						return;
					 }
					 }
				</logic:equal>
				<logic:equal name="element" property="itemtype" value="A">
				<logic:equal name="element" property="codesetid" value="0">
					var a<%=m%>=document.getElementsByName("positionDemandDescList[<%=m%>].value")
					if(a<%=m%>[0].value!='')
					{
						if(IsOverStrLength(a<%=m%>[0].value,<bean:write  name="element" property="length"/>))
						{
							alert("<bean:write  name="element" property="itemdesc"/>"+OVER_LENGTH_SCOPE);
							return;
						}
					}
				</logic:equal>
				</logic:equal>
			<% m++; %>
		</logic:iterate>
	
	<% m=0; %>
	<logic:iterate  id="element"    name="positionDemandForm"  property="positionDemandDescList" indexId="index"> 
		var item<%=m%>='<bean:write  name="element" property="mustfill"/>';
		var aa<%=m%>=document.getElementsByName("positionDemandDescList[<%=m%>].value")
		if(item<%=m%>=='1')
		{				if(aa<%=m%>[0]!=null){
			                if(aa<%=m%>[0].value=='')
							{
								alert("<bean:write  name="element" property="itemdesc"/>"+THIS_IS_MUST_FILL+"！");
								return;						
							}
						}

		}
		
		<% m++; %>
	</logic:iterate>
	
		for(var a=0;a<document.positionDemandForm.elements.length;a++)
		{
			if(document.positionDemandForm.elements[a].type=='checkbox')
			{
				if(document.positionDemandForm.elements[a].checked==false)
				{
						document.positionDemandForm.elements[a].value="0"
						document.positionDemandForm.elements[a].checked=true;
				}
				
			
			}
		}
 var z0313=document.getElementById("z0313").value;
 var z0315=document.getElementById("z0315").value;
 var z0301=document.getElementById("z01").value;
 var obj=document.getElementById("z0316");
 var z0316="";
 if(obj){
	 for(var i=0;i<obj.options.length;i++)
	 {
	    if(obj.options[i].selected)
	    {
	       z0316=obj.options[i].value;
	       break;
	    }
	 }
 }
 var hashvo=new ParameterSet();
 hashvo.setValue("z0313",z0313);
 hashvo.setValue("z0315",z0315);
 hashvo.setValue("z0301",z0301);
 hashvo.setValue("z0316",z0316);
 hashvo.setValue("entertype","2");
 var request=new Request({method:'post',asynchronous:false,onSuccess:check_bz_ok,functionId:'3020071030'},hashvo);
}
function check_bz_ok(outparameters)
{
 	var message=outparameters.getValue("message");
 	if(message!='0')
 	{
 	   alert(message);
 	  if(document.getElementById("z0315"))
       {
          document.getElementById("z0315").value=beforevalue;
       }
      if(document.getElementById("z0313"))
      {
         document.getElementById("z0313").value=beforevalue2;
      }
 	  return;
 	}
	document.positionDemandForm.action="/hire/demandPlan/positionDemand/auto_logon_sp.do?b_save=add";
	document.positionDemandForm.submit();
}
function initValue()
{
  if(document.getElementById("z0315"))
  {
     beforevalue=document.getElementById("z0315").value;
  }
  if(document.getElementById("z0313"))
  {
     beforevalue2=document.getElementById("z0313").value;
  }
}
function goback()
{
    var obj= new Object();
      obj.refresh="0";
      returnValue=obj;
      window.close();
}



function showTemplate()
{
	var obj=eval('a');
	if(document.positionDemandForm.isRevert.checked==true)
	{
		obj.style.display='block';
	}
	else
	{
		obj.style.display='none';
	}
}

function up_file(e01a1)
{
    thecodeurl="/hire/demandPlan/positionDemand/positionDemandTree.do?b_initUpFile=up`opt=s`posID="+e01a1; 
    var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    var popwin= window.showModalDialog(iframe_url, e01a1, 
        "dialogWidth:420px; dialogHeight:180px;resizable:no;center:yes;scroll:yes;status:no");
   
	
}

function showRows(objName)
{
	var objs=eval(objName);
	for(var i=0;i<3;i++)
	{
		if(objs[i].style.display=='none')
		{
			objs[i].style.display='block';
			break;
		}	
	}

}

function delRows(objName)
{
	
	var objs=eval(objName);	
	for(var i=2;i>=0;i--)
	{
		if(objs[i].style.display=='block')
		{
			objs[i].style.display='none';
			var a_td=objs[i].cells[1];
			 var   oinput =a_td.getElementsByTagName("input")   ;
			 for(var j=0;j<oinput.length;j++)
			 {
			 	oinput[j].value="";
			 }
			 var   oselect =a_td.getElementsByTagName("select")   ;
			 for(var j=0;j<oselect.length;j++)
			 {
			 	oselect[j].value="";
			 }
			 
			break;
		}	
	}

}
function ifClose(isClose)
{
   if(isClose=='1')
   {
      var obj= new Object();
      obj.refresh="1";
      returnValue=obj;
      window.close();
   }
}
///02、03报批、批准 07驳回
function reportSP(sp_flag)
{
    var a0100="-1";
    var z0301=document.getElementById("z01").value;
    if(sp_flag=='02'||sp_flag=='03')
    {
        var hashVo=new ParameterSet();
        hashVo.setValue("z0301",z0301);
        hashVo.setValue("model","1");
        hashVo.setValue("sp_flag",sp_flag);
        var In_parameters="opt=1";
        var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:checkBZ_OK,functionId:'3000000228'},hashVo);			
       
    }
    else
    {
    var thecodeurl="/hire/demandPlan/positionDemand/auto_logon_sp.do?b_search=search`intype=1`spperson="+a0100+"`sp_flag="+sp_flag+"`z0301="+z0301; 
			 var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
		    var values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:650px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");		   
    if(values)
    {
      var obj = new Object();
      obj.clo=values.clo;
      if(obj.clo=='2')
         window.parent.close();
    }
    }
}
function checkBZ_OK(outparameters)
{
   var message =getDecodeStr(outparameters.getValue("message"));
   var isHaveOuter=outparameters.getValue("isHaveOuter");
   var sp_flag=outparameters.getValue("sp_flag");
   if(message!='0'&&message!='1')
   {
      alert(message);
      return;
   }
   if(message=='1')
   {
     var alertmessage=getDecodeStr(outparameters.getValue("alertmessage"));
     alert(alertmessage);
     return;
   }
   if(message=='0')
   {
       var a0100="-1";
       var z0301=document.getElementById("z01").value;
       var spRelation=document.getElementById("spRelation").value;
       var thecodeurl="/hire/demandPlan/positionDemand/auto_logon_sp.do?b_search=search`intype=1`spperson="+a0100+"`sp_flag="+sp_flag+"`z0301="+z0301+"`spRelation="+spRelation; 
			 var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
		    var values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:650px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no");		   
       if(values)
      {
        var obj = new Object();
        obj.clo=values.clo;
        if(obj.clo=='2')
           window.parent.close();
      }
   }
}
function openCard(z0321)
{
   var src="/hire/demandPlan/positionDemand/unit_card.do?b_query=query`z0321="+z0321;
   //window.open(src,"_blank");
   var iframe_url="/general/query/common/iframe_query.jsp?src="+src;
   var values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:"+(window.screen.width-100)+"px; dialogHeight:"+(window.screen.height-200)+"px;resizable:no;center:yes;scroll:yes;status:no");			
	
}
</script>
<html>
  <head>
  </head>
  <hrms:themes></hrms:themes>
  <body>
  <html:form action="/hire/demandPlan/positionDemand/auto_logon_sp">
  <br>
  

  
  <table width="90%" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">   
          <% if(zpchanel.equalsIgnoreCase("01")) {%>    		
       		<td width='100%'  class="TableRow_lrt common_background_color">专业需求(${positionDemandForm.positionStateDesc})</td>
       		<% }else {%>
       		<td width='100%'  class="TableRow_lrt common_background_color">岗位需求(${positionDemandForm.positionStateDesc})</td>
       		<%} %>
          </tr> 
          <tr >
          	<td class='RecordRow' > 
          <table width='100%' align='center' >
		  	<tr>
		  		<td align='right'>
		  <%	if (spRelation != null && spRelation.length() > 0 && "true".equalsIgnoreCase(zpappfalg.toString())) {
		  		 if(Integer.parseInt(spcount)>0){
		  %>		
		<logic:equal value="1" name="positionDemandForm" property="isReport">
  		 <hrms:priv func_id="310114,0A033">
  		 <input type="button" name="" value="报批" class="mybutton" onclick="reportSP('02');"/>
  		 &nbsp;
  		 </hrms:priv>
  		 </logic:equal>
		     <%} else{ %>
		  <logic:equal value="1" name="positionDemandForm" property="isReject">
           <hrms:priv func_id="310123,0A073">

             <input type="button" name="" value="批准" class="mybutton" onclick="reportSP('03');"/>
            &nbsp;
          </hrms:priv>
          </logic:equal>
          <%    }
		  	} else { %>
          <logic:equal value="1" name="positionDemandForm" property="isReport">
  		 <hrms:priv func_id="310114,0A033">
  		 <input type="button" name="" value="报批" class="mybutton" onclick="reportSP('02');"/>
  		 &nbsp;
  		 </hrms:priv>
  		 </logic:equal>

		  <logic:equal value="1" name="positionDemandForm" property="isReject">
           <hrms:priv func_id="310123,0A073">

             <input type="button" name="" value="批准" class="mybutton" onclick="reportSP('03');"/>
            &nbsp;
          </hrms:priv>
          </logic:equal>
		<%} %>
  		 <hrms:priv func_id="310124,0A074">
          <input type="button" name="" value="驳回" class="mybutton" onclick="reportSP('07');"/>
          &nbsp;
          </hrms:priv>
         
          <html:hidden name="positionDemandForm" styleId="z01" property="z0301"/>
          <logic:equal value="1" name="positionDemandForm" property="positionState">
  		<hrms:priv func_id="310111,310121">
  		<input type='button' value="<bean:message key="button.save"/>" class="mybutton" onclick='sub()' >
  				&nbsp;
		</hrms:priv>
        </logic:equal>
				<input type='button' value="<bean:message key="button.close"/>"  onclick='goback()' class="mybutton" >  
				</td>
		  	</tr>
		  </table>
          
          
          &nbsp;&nbsp; <bean:message key="hire.position.description"/>&nbsp;&nbsp;
          <table width='97%' align='center' cellpadding="0" cellspacing="0"   ><tr><td class='RecordRow_top common_border_color' >
         &nbsp;
          </td></tr></table>
          <table border=0 width='90%' align='center' >
          <%
          
            for(int i=0;i<positionDemandDescList.size();i++)
            {
           
                LazyDynaBean abean=(LazyDynaBean)positionDemandDescList.get(i);
                 String itemid=(String)abean.get("itemid");
				 String itemtype=(String)abean.get("itemtype");
				 String codesetid=(String)abean.get("codesetid");
				 String isMore=(String)abean.get("isMore");
				 String itemdesc=(String)abean.get("itemdesc");
				 String value=(String)abean.get("value");
				 String viewvalue=(String)abean.get("viewvalue");
				 String mustfill=(String)abean.get("mustfill");	
				 if(zpchanel.equalsIgnoreCase("01")&&itemid.equalsIgnoreCase("posID")){
						continue;
				 }			 
          		 out.print("<tr><td width='15%' ");
          		  if(itemtype.equals("M"))
				 	out.print(" valign='top' ");
          		 out.print(" align='left' >");
				 out.print(itemdesc);
				 out.print(":</td><td  width='85%'  align='left' >");
          		 if(itemtype.equals("A"))
				 {
								if(codesetid.equals("0"))
								{
									out.println("<input type=\"text\"  ");	
									if(positionState.equals("0")||itemid.equalsIgnoreCase("z0309"))
									   out.print("disabled");						
									out.print(" name=\"positionDemandDescList["+i+"].value\"  size='30'  value=\""+value+"\" class=\"text4\" />&nbsp;");
									if(mustfill.equals("1"))
									    {
										   out.print("&nbsp;<font color='red'>*</font>&nbsp;");
									    }
								}
								else
								{
									if(isMore.equals("0"))
									{
										ArrayList options=(ArrayList)abean.get("options");
										if(i==1)
											out.print("<table><tr><td>");
										out.print(" <select  name='positionDemandDescList["+i+"].value' ");
										if(itemid.equalsIgnoreCase("z0316"))
										    out.print("id=\"z0316\"");
										if(i==1)
											out.print(" onChange='validatePos()'  disabled  ");
										else
										{
										   if(positionState.equals("0"))
									            out.print("disabled");		
										}
										out.print(" ><option value=''></option> ");
										//if(i!=1)
										{
											for(int n=0;n<options.size();n++)
											{
												LazyDynaBean a_bean=(LazyDynaBean)options.get(n);
												String avalue=(String)a_bean.get("value");
												String aname=(String)a_bean.get("name");
												out.println("<option value='"+avalue+"' ");
												if(avalue.equals(value))
													out.print(" selected ");
												out.print(" >"+aname+"</option>");
											}
										 }
										
										out.print("</select>&nbsp;&nbsp;");
										if(mustfill.equals("1"))
									    {
										   out.print("&nbsp;<font color='red'>*</font>&nbsp;");
									    }
										if(i==1)
										{
											out.print(" </td><td><div id='aup'> <input type='button' value=\""+ResourceFactory.getProperty("hire.upload.positionexplanation")+"\"  class='mybutton' onclick='up_file(\""+value+"\")' ");
											if(positionState.equals("0"))
											     out.print(" disabled ");
											out.print(">  </div></td></tr></table>");
										}
									}
									else
									{
										 
						              	out.print("<table  cellSpacing=0 cellPadding=0 border=0  ><tr><td>");
						              	out.print(" <input type='text' name='positionDemandDescList["+i+"].viewvalue' ");
						              	if(i==0)
						             		out.print(" onChange='setPosList()' disabled ");
						             	else
						             	{
						             	  if(positionState.equals("0"))
									         out.print("disabled");		
									   }
						              	out.print(" size='30' value='"+viewvalue+"' class=\"text4\" /></td>");
						             	out.print("<td><a href='javascript:");
						             	if(i==0)
											out.print("' ");
										else
										{
										    if(positionState.equals("0"))
										       out.print("' ");
										    else		
							                 	out.print("openInputCodeDialog(\""+codesetid+"\",\"positionDemandDescList["+i+"].viewvalue\");' ");
						             
						                 }
						             	out.print(" >   <img  src='/images/overview_obj.gif' border=0 width=20 height=20 /></a>");	
						             	if(mustfill.equals("1"))
									    {
										   out.print("&nbsp;<font color='red'>*</font>&nbsp;");
									    }	
										out.print("</td><td valign='bottom' ><input type='hidden' value='"+value+"'  name='positionDemandDescList["+i+"].value' /> &nbsp;");									
									    out.print(" </td>");
									    if(i==0&&isOrgWillTableIdDefine.equals("1"))
									    {
									       out.print("<td id=\"showButton\" ><a href=\"javascript:openCard('"+z0321+"');\" >单位信息</a>&nbsp;&nbsp;");
									       if(showUMCard.equals("1"))
									          out.print("<a href=\"javascript:openCard('"+z0325+"');\">部门信息</a>");
									       out.print("</td>");
				
									    }
									    else
									    {
									       out.print("<td id=\"showButton\" style=\"display:none\">&nbsp;&nbsp;</td>");
									    }
									    out.print("</tr></table>"); 
									}
								
								}
							
						}
						else if(itemtype.equals("D"))
						{
								out.println("<input type='text'  size='20'  name='positionDemandDescList["+i+"].value'  value='"+value+"'  readOnly ");
								if(!itemid.equalsIgnoreCase("z0307"))
								   if(!positionState.equals("0"))
									   out.print("  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' ");	
								else
								{
								     if(positionState.equals("0"))
									         out.print(" disabled ");		
								}
								out.print(" class=\"text4\" />&nbsp;");
								if(mustfill.equals("1"))
								{
									out.print("&nbsp;<font color='red'>*</font>&nbsp;");
								}
						}
						else if(itemtype.equals("N"))
						{
								out.println("<input type=\"text\" name=\"positionDemandDescList["+i+"].value\"   value='"+value+"'   size='20' ");
								if(itemid.equalsIgnoreCase("z0313")||itemid.equalsIgnoreCase("z0315"))
								   out.print(" id=\""+itemid.toLowerCase()+"\"");
								 if(positionState.equals("0"))
									         out.print("disabled");		
							     out.print(" class=\"text4\"/>&nbsp;");
								if(mustfill.equals("1"))
							    {
									out.print("&nbsp;<font color='red'>*</font>&nbsp;");
							    }
						}
						else if(itemtype.equals("M"))
						{
								out.println("<textarea name=\"positionDemandDescList["+i+"].value\" rows='10'  wrap='OFF' cols='80' class='textboxMul' ");
								 if(positionState.equals("0"))
									         out.print(" readOnly ");		
							     out.print(">"+value+"</textarea>&nbsp;");
								
								if(mustfill.equals("1"))
								{
									out.print("&nbsp;<font color='red'>*</font>&nbsp;");
								}
						}
						out.print("</td></tr>");
          		
          
          }
          %>
          
          </table>                           
          <br><br>
			</td>          
          </tr>
  </table>       
  
  
  <table width='90%' align='center' >
  	<tr>
  		<td align='right'>
  		<%	if (spRelation != null && spRelation.length() > 0 && "true".equalsIgnoreCase(zpappfalg.toString())) {
		  		 if(Integer.parseInt(spcount)>0){
		  %>		
		<logic:equal value="1" name="positionDemandForm" property="isReport">
  		 <hrms:priv func_id="310114,0A033">
  		 <input type="button" name="" value="报批" class="mybutton" onclick="reportSP('02');"/>
  		 &nbsp;
  		 </hrms:priv>
  		 </logic:equal>
		     <%} else{ %>
		  <logic:equal value="1" name="positionDemandForm" property="isReject">
           <hrms:priv func_id="310123,0A073">

             <input type="button" name="" value="批准" class="mybutton" onclick="reportSP('03');"/>
            &nbsp;
          </hrms:priv>
          </logic:equal>
          <%    }
		  	} else { %>
  		<logic:equal value="1" name="positionDemandForm" property="isReport">
  		 <hrms:priv func_id="310114,0A033">
  		 <input type="button" name="" value="报批" class="mybutton" onclick="reportSP('02');"/>
  		 &nbsp;
  		 </hrms:priv>
  		 </logic:equal>
  		 
  		 <logic:equal value="1" name="positionDemandForm" property="isReject">
           <hrms:priv func_id="310123,0A073">

             <input type="button" name="" value="批准" class="mybutton" onclick="reportSP('03');"/>
            &nbsp;
          </hrms:priv>
          </logic:equal>
		<%} %>
  		 <hrms:priv func_id="310124,0A074">
          <input type="button" name="" value="驳回" class="mybutton" onclick="reportSP('07');"/>
          &nbsp;
          </hrms:priv>
         
          <html:hidden name="positionDemandForm" styleId="z01" property="z0301"/>
          <logic:equal value="1" name="positionDemandForm" property="positionState">
  		<hrms:priv func_id="310111,310121">
  		<input type='button' value="<bean:message key="button.save"/>" class="mybutton" onclick='sub()' >
  				&nbsp;
		</hrms:priv>
        </logic:equal>
		<input type='button' value="<bean:message key="button.close"/>"  onclick='goback()' class="mybutton" >  
		</td>
  	</tr>
  </table> 
  <html:hidden property="spRelation" name="positionDemandForm" styleId="spRelation"/>
  </html:form>
  </body>
   <script language='javascript' >
  initValue();
  </script>
</html>
