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
				com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo,
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
	
	ParameterXMLBo bo2 = new ParameterXMLBo();
	HashMap map = bo2.getAttributeValues();
	String hireMajor="";
	if(map.get("hireMajor")!=null)
				hireMajor=(String)map.get("hireMajor");  //招聘专业指标
	String hireMajorCode="";
	if(map.get("hireMajorCode")!=null)
				hireMajorCode=(String)map.get("hireMajorCode");  //招聘专业代码		
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


</style>
  <%
			PositionDemandForm positionDemandForm=(PositionDemandForm)session.getAttribute("positionDemandForm");
			ArrayList positionDemandDescList=positionDemandForm.getPositionDemandDescList();
			ArrayList posConditionList=positionDemandForm.getPosConditionList();
			
			
  %>

<script language='javascript' >
var hireMajor='<%=hireMajor%>';
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
	var aa=document.getElementsByName("positionDemandDescList[1].viewvalue")
	aa[0].fireEvent("onChange");
}


function setPosList()
{
	var aa=document.getElementsByName("positionDemandDescList[1].value")
	var hashvo=new ParameterSet();
	hashvo.setValue("operator","1");
	var In_paramters="orgID="+aa[0].value;  
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000174'},hashvo);
}

function returnInfo(outparamters)
{
	var poslist=outparamters.getValue("poslist");	
	var aa=document.getElementsByName("positionDemandDescList[2].value")
	AjaxBind.bind(aa[0],poslist);
}

function validatePos()
{
	var a=document.getElementsByName("positionDemandDescList[2].value")
	var aa=document.getElementsByName("positionDemandDescList[1].value")
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

		var aa=document.getElementsByName("positionDemandDescList[2].value")
		var a_Option=aa[0].options[aa[0].options.length-1];
		 aa[0].options[aa[0].options.length-1]=new Option(popwin[1],orgID+popwin[0]);		 
		 aa[0].options[aa[0].options.length]=a_Option;
		 aa[0].options[aa[0].options.length-2].selected=true;
		 var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo3,functionId:'3000000175'},hashvo);

    }
}

var beforevalue;
var beforevalue2;
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
function sub()
{
	 	<% int m=0;  %>
		<logic:iterate  id="element"    name="positionDemandForm"  property="positionDemandDescList" indexId="index"> 
				<logic:equal name="element" property="itemtype" value="N">
					var a<%=m%>=document.getElementsByName("positionDemandDescList[<%=m%>].value")
					if(a<%=m%>[0]!=null&&a<%=m%>[0].value!='')
					{
					 var myReg =/^(-?\d+)(\.\d+)?$/
					 if(!myReg.test(a<%=m%>[0].value)) 
					 {
						alert("<bean:write  name="element" property="itemdesc"/>"+PLEASE_INPUT_NUMBER+"！");
						return;
					 }
					 }
				</logic:equal>
				<logic:equal name="element" property="itemtype" value="D">
					var a<%=m%>=document.getElementsByName("positionDemandDescList[<%=m%>].value")
					if(a<%=m%>[0]!=null&&a<%=m%>[0].value!='')
					{
				    	 if(!checkDateTime(a<%=m%>[0].value)) 
					     {
				    		alert("<bean:write  name="element" property="itemdesc"/>输入不正确,请输入有效的日期且格式为yyyy-mm-dd！");
				    		return;
				    	 }
				       
					   var year=a<%=m%>[0].value.substring(0,4);
						var month=a<%=m%>[0].value.substring(5,7);
						var day=a<%=m%>[0].value.substring(8,10);
						if(!isValidDate(day, month, year))
						{
						   alert("<bean:write  name="element" property="itemdesc"/> 输入值不正确！");
				    		return;
						}
					 }
				</logic:equal>
				<logic:equal name="element" property="itemtype" value="A">
				<logic:equal name="element" property="codesetid" value="0">
					var a<%=m%>=document.getElementsByName("positionDemandDescList[<%=m%>].value")
					if(a<%=m%>[0]!=null&&a<%=m%>[0].value!='')
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
	var z0329="";
	var z0331="";
	var school=document.getElementsByName("positionDemandDescList[<%=0%>].value")[0].value;
	<logic:iterate  id="element"    name="positionDemandForm"  property="positionDemandDescList" indexId="index"> 
		var itemid<%=m%>='<bean:write  name="element" property="itemid"/>';
		var aa<%=m%>=document.getElementsByName("positionDemandDescList[<%=m%>].value")
		var item<%=m%>='<bean:write  name="element" property="mustfill"/>';
		if(aa<%=m%>[0]!=null){
			if(itemid<%=m%>.toUpperCase()=='Z0329')
			{
			   z0329=aa<%=m%>[0].value;
			}
			if(itemid<%=m%>.toUpperCase()=='Z0331')
			{
			   z0331=aa<%=m%>[0].value;
			}
			//if((itemid<%=m%>=='z0313'||itemid<%=m%>=='z0311'||itemid<%=m%>=='z0329'||itemid<%=m%>=='z0331'||itemid<%=m%>=='z0333')&&)
			//{
							//if(aa<%=m%>[0].value==''&&item<%=m%>=='1')
							//{
								//alert("<bean:write  name="element" property="itemdesc"/>"+THIS_IS_MUST_FILL+"！");
								//return;						
							//}
			//}
			if(item<%=m%>=='1'&&itemid<%=m%>.toUpperCase()!=hireMajor.toUpperCase())
			{
			  if(school=='01'&&itemid<%=m%>.toUpperCase()=="POSID")
			     {
			     }else
			     {
	
							if(aa<%=m%>[0].value=='')
							{
								alert("<bean:write  name="element" property="itemdesc"/>"+THIS_IS_MUST_FILL+"！");
								return;						
							}
							}
			}else
			{
	
			  if(school=='01'&&itemid<%=m%>.toUpperCase()==hireMajor.toUpperCase())
			  {
			      if((aa<%=m%>[0].value==""))
							{
								alert("<bean:write  name="element" property="itemdesc"/>"+THIS_IS_MUST_FILL+"！");
								return;						
							}
			  }
			  }
		}
		<% m++; %>
	</logic:iterate>
	if(trim(z0329)!=''&&trim(z0331)!='')
	{
	   var xx=z0329.split("-");
	   var yy=z0331.split("-");
	   var xxD=new Date(xx[0]*1,xx[1]*1,xx[2]*1);
	   var yyD=new Date(yy[0]*1,yy[1]*1,yy[2]*1);
      if(xx[0]*1>yy[0]*1||(xx[0]*1==yy[0]*1&&xx[1]*1>yy[1]*1)||(xx[0]*1==yy[0]*1&&xx[1]*1==yy[1]*1&&xx[2]*1>yy[2]*1))
	   {
	     alert("有效起始日期不能大于有效结束日期！");
	     return;
	   }
	}
	
	
	<%
		for(int d=0;d<posConditionList.size();d++)
        {
  				
           			LazyDynaBean abean=(LazyDynaBean)posConditionList.get(d);          			
           			String itemtype=(String)abean.get("fieldType");
           			String itemdesc=(String)abean.get("itemdesc");
           			String flag=(String)abean.get("flag");
           			if(itemtype.equals("D"))
           			{
    	       		%>
    	       			var a<%=d%>=document.getElementsByName("posConditionList[<%=d%>].s_value")		
						if(a<%=d%>[0]!=null&&!validateData(a<%=d%>[0],'<%=itemdesc%>')){
							return;
						}
						<%
						if(flag.equals("false"))
						{
						%>	
							var b<%=d%>=document.getElementsByName("posConditionList[<%=d%>].e_value");							
							if(b<%=d%>[0]!=null&&!validateData(b<%=d%>[0],'<%=itemdesc%>')){
								return;
							}
								
						<%
						}
						%>
						
    	       		<%
    	       		}
    	       		if(itemtype.equals("N"))
           			{
    	       		%>	
    	       			var a<%=d%>=document.getElementsByName("posConditionList[<%=d%>].s_value")
						if(a<%=d%>[0]!=null&&trim(a<%=d%>[0].value).length!=0)
						{						
							 var myReg =/^(-?\d+)(\.\d+)?$/
							 if(!myReg.test(a<%=d%>[0].value)) 
							 {
								alert("<%=itemdesc%>"+PLEASE_INPUT_NUMBER+"！");
								return;
							 }
						 }
						 
						 <%
						if(flag.equals("false"))
						{
						%>	
							var b<%=d%>=document.getElementsByName("posConditionList[<%=d%>].e_value")
							if(b<%=d%>[0]!=null&&trim(b<%=d%>[0].value).length!=0)
							{						
								 var myReg =/^(-?\d+)(\.\d+)?$/
								 if(!myReg.test(b<%=d%>[0].value)) 
								 {
									alert("<%=itemdesc%>"+PLEASE_INPUT_NUMBER+"！");
									return;
								 }
							 }
						<%
						}
						%>
						 
						 
						 
    	       		<%
    	       		}
		}
		
		%>
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
 if(document.getElementById("z0313"))
     var z0313=document.getElementById("z0313").value;
 var z0315="-1";
  if(document.getElementById("z0315"))
  {
     z0315=document.getElementById("z0315").value;
  }

 var z0301=document.getElementById("z3").value;
 var obj=document.getElementById("z0316");
 var z0316="0";
 if(obj)
 {
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
	document.positionDemandForm.action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_editDemand=add&opt=1&posState=<%=posState%>&z0301=<%=z0301%>";
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
	<%
	if(request.getParameter("from")!=null&&request.getParameter("from").equals("employPosition"))
	{
	%>
		window.close();
	<%
	}
	else
	{
	%>
		document.positionDemandForm.action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_read=read&operate=read&posState=<%=posState%>&z0301=<%=z0301%>";
		document.positionDemandForm.submit();
	<%
	}
	%>

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
        "dialogWidth:420px; dialogHeight:220px;resizable:no;center:yes;scroll:yes;status:no");
   
	
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




<%
	if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("1"))
	{
	    out.println("window.opener.location='/hire/employActualize/employPosition.do?b_query=link'");
		out.println("window.location='/hire/demandPlan/positionDemand/positionDemandTree.do?b_read=read&operate=read&posState="+posState+"&z0301="+z0301+"'");
	}
%>

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
<html>
  <head>
  </head>
  <hrms:themes></hrms:themes>
  <body onload="setTPinput()">
  <html:form action="/hire/demandPlan/positionDemand/positionDemandTree">
  <table width="90%" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">       		
       		<td width='100%'  align="left" class="TableRow_lrt">&nbsp;&nbsp;<bean:message key="modify.position.request"/>&nbsp;</td>
          </tr> 
          <tr >
          	<td class='RecordRow' >
          	<table width='100%' style="margin-top:10px;">
          	<!-- hcm 7.0中按照要求去掉 xucs 2014-06-30  
          <table width='100%' align='center' >
		  	<tr>
		  		<td align='right'>
		  		<input type='button' value="<bean:message key="button.ok"/>"  class="mybutton" onclick='sub()' >
				&nbsp;
				<input type='button' value="<bean:message key="button.cancel"/>"  onclick='goback()' class="mybutton" >  
				</td>
		  	</tr>
		  </table>
          -->
          
          &nbsp;&nbsp; <bean:message key="hire.position.description"/>&nbsp;&nbsp;
          <table width='97%' align='center' cellpadding="0" cellspacing="0"   ><tr><td class='RecordRow_top common_border_color' >
         &nbsp;
          </td></tr></table>
          <table border=0 width='90%' align='center' >
          <%
            String z0336_value="";
            for(int i=0;i<positionDemandDescList.size();i++)
            {
           
                LazyDynaBean abean=(LazyDynaBean)positionDemandDescList.get(i);
                 String itemid=(String)abean.get("itemid");
				 String itemtype=(String)abean.get("itemtype");
				 String codesetid=(String)abean.get("codesetid");
				 String isMore=(String)abean.get("isMore");
				 String itemdesc=(String)abean.get("itemdesc");
				 String value=(String)abean.get("value");
				 
				 if(itemid.equalsIgnoreCase("z0336"))
				 	z0336_value=value!=null?value:"";
				 
				 String desc="";
				 if(abean.get("desc")!=null)
					 desc=(String)abean.get("desc");
				 String viewvalue=(String)abean.get("viewvalue");
				 String mustfill=(String)abean.get("mustfill");		
				  if(i==2)
					 out.print("<tr id='pos_tr' ><td width='15%' ");
				 else		 
	          		 out.print("<tr><td width='15%' ");
          		  if(itemtype.equals("M"))
				 	out.print(" valign='top' ");
          		 out.print(" align='right' >");
				 out.print(itemdesc);
				 out.print("&nbsp;&nbsp;</td><td  width='85%'  align='left' >");
          		 if(itemtype.equals("A"))
				 {
								if(codesetid.equals("0"))
								{
									if(itemid.equalsIgnoreCase(hireMajor) && !(hireMajorCode==null || hireMajorCode.equals(""))){
										out.println("<input type=\"hidden\"  ");			
									}else
										out.println("<input type=\"text\"  ");								
									out.print(" name=\"positionDemandDescList["+i+"].value\"  size='30'  value=\""+value+"\" ");
									//if((z0336_value.equals("01")&&itemid.equalsIgnoreCase(hireMajor)))
									//{
									  // out.print(" readOnly ");
									//}
									if((z0336_value.equals("01")&&itemid.equalsIgnoreCase(hireMajor))){//dml tianjia
										out.print(" disabled");
									}
									if(itemid.equalsIgnoreCase(hireMajor) && !(hireMajorCode==null || hireMajorCode.equals("")))
										out.print(" />");
									else
										out.print(" />&nbsp;");
									if(itemid.equalsIgnoreCase(hireMajor) && !(hireMajorCode==null || hireMajorCode.equals(""))){
										out.println("<input type=\"text\"  ");
										if(z0336_value.equals("0")||itemid.equalsIgnoreCase("z0309"))
											   out.print("disabled");
										out.print(" name=\"positionDemandDescList["+i+"].viewvalue\"  readOnly size='30'   value=\""+value+"\" ");
										if((z0336_value.equals("01")&&itemid.equalsIgnoreCase(hireMajor)))
										{
										   out.print(" disabled ");
										}
										out.print(" />&nbsp;");
										if(!(z0336_value.equals("01")&&itemid.equalsIgnoreCase(hireMajor))){
										    out.print("<span>");
											out.print("<img  src='/images/code.gif' border=0 width=20 height=20 onclick=\"openInputCodeDialog2('"+hireMajorCode+"','positionDemandDescList["+i+"].viewvalue','0','','1');\" style='position:relative;top:5px;'/>");
											out.print("</span>");
									    }
									}
									if(mustfill.equals("1")||((z0336_value.equals("01")||z0336_value.equals("04"))&&itemid.equalsIgnoreCase(hireMajor)))
									    {
										   out.print("&nbsp;<font color='red'>*</font>&nbsp;");
									    }
								}
								else
								{
									if(isMore.equals("0"))
									{
										ArrayList options=(ArrayList)abean.get("options");
										if(options==null)
										   options=new ArrayList();

										out.print(" <select  name='positionDemandDescList["+i+"].value' ");
										if(itemid.equalsIgnoreCase("z0316"))
										    out.print("id=\"z0316\"");
										if(i==2)
											out.print(" onChange='validatePos()'  disabled  ");
										if(i==0)
											out.print(" disabled ");
										else if(hireMajor.length()>0&&itemid.equalsIgnoreCase(hireMajor)&&z0336_value.equals("01"))
									        	 out.print("disabled");
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
										if(mustfill.equals("1")||((z0336_value.equals("01")||z0336_value.equals("04"))&&itemid.equalsIgnoreCase(hireMajor)))
									    {
										   out.print("&nbsp;<font color='red'>*</font>&nbsp;");
									    }
										if(i==2)
											out.print("<span id='aup'> <input type='button' value=\""+ResourceFactory.getProperty("hire.upload.positionexplanation")+"\"  class='mybutton' onclick='up_file(\""+value+"\")' >  </span> </td><td>");
										out.print("&nbsp;"+desc);
									}
									else
									{
										 
						              	out.print("<table  cellSpacing=0 cellPadding=0 border=0  ><tr><td>");
						              	out.print(" <input type='text' name='positionDemandDescList["+i+"].viewvalue' ");
						              	if(i==1)
						             		out.print(" onChange='setPosList()' disabled ");
						             	//else if(hireMajor.length()>0&&itemid.equalsIgnoreCase(hireMajor)&&z0336_value.equals("01"))
									         //out.print(" disabled");
						              	out.print(" size='30' value='"+viewvalue+"'  ");
						              	if(hireMajor.length()>0&&itemid.equalsIgnoreCase(hireMajor)&&z0336_value.equals("01"))
						              			out.print(" disabled /></td>");
						             	out.print("<td><a href='javascript:");
						             	if(i==1)
											out.print("' ");
										else if(hireMajor.length()>0&&itemid.equalsIgnoreCase(hireMajor)&&z0336_value.equals("01"))
									        out.print("' ");
										else
							             	out.print("openInputCodeDialog(\""+codesetid+"\",\"positionDemandDescList["+i+"].viewvalue\");' ");
						             	out.print(" > <span>  <img  src='/images/code.gif' border=0 width=20 height=20 style='position:relative;top:5px;' /></span></a>");	
						             	if(mustfill.equals("1")||(z0336_value.equals("01")&&itemid.equalsIgnoreCase(hireMajor)))
									    {
										   out.print("&nbsp;<font color='red'>*</font>&nbsp;");
									    }	
										out.print("</td><td valign='bottom' ><input type='hidden' value='"+value+"'  name='positionDemandDescList["+i+"].value' /> &nbsp;");									
									    out.print("&nbsp;"+desc);
									    out.print(" </td></tr></table>"); 
									}
								
								}
							
						}
						else if(itemtype.equals("D"))
						{
								out.println("<input type='text'  size='20'  name='positionDemandDescList["+i+"].value'  value='"+value+"' ");
								if(!itemid.equalsIgnoreCase("z0307"))
									out.print("  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' ");	
								else
								    out.print(" disabled ");
								out.print("  />&nbsp;");
								if(mustfill.equals("1")&&!itemid.equalsIgnoreCase("z0307"))
								{
									out.print("&nbsp;<font color='red'>*</font>&nbsp;");
								}
						}
						else if(itemtype.equals("N"))
						{
								out.print("<input type=\"text\" name=\"positionDemandDescList["+i+"].value\"   value='"+value+"'   size='20' ");
								if(itemid.equalsIgnoreCase("z0313")||itemid.equalsIgnoreCase("z0315"))
								   out.print(" id=\""+itemid.toLowerCase()+"\"");
								out.print(" />&nbsp;");
								if(mustfill.equals("1"))
							    {
									out.print("&nbsp;<font color='red'>*</font>&nbsp;");
							    }
						}
						else if(itemtype.equals("M"))
						{
								out.println("<textarea name=\"positionDemandDescList["+i+"].value\" rows='10'  wrap='OFF' cols='80' class='textboxMul'>"+value+"</textarea>&nbsp;");
								if(mustfill.equals("1"))
								{
									out.print("&nbsp;<font color='red'>*</font>&nbsp;");
								}
						}
						if(!(itemtype.equals("A")&&!codesetid.equals("0"))){
							out.print("&nbsp;"+desc);
						}
						out.print("</td></tr>");
          }
          %>
          
          </table>
          <br><br>
          &nbsp;&nbsp; <bean:message key="hire.employ.request"/>&nbsp;&nbsp;
          <table width='97%' align='center' cellpadding="0" cellspacing="0"   ><tr><td class='RecordRow_top common_border_color' >
         &nbsp;
          </td></tr></table>
           <table border=0 width='90%' align='center' >
           	<%
           	
           	 
           		String aa_setname="";
           		int count=1;
           		for(int j=0;j<posConditionList.size();j++)
           		{
           			boolean d=true;
           			
           			LazyDynaBean abean=(LazyDynaBean)posConditionList.get(j);
           			String setname=(String)abean.get("setname");
           			String itemtype=(String)abean.get("fieldType");
           			String codesetid=(String)abean.get("codesetid");
           			String isMore=(String)abean.get("isMore");
           			String itemdesc=(String)abean.get("itemdesc");
           			String s_value=(String)abean.get("s_value");
           			String e_value=(String)abean.get("e_value");
           			String flag=(String)abean.get("flag");
           			String type=(String)abean.get("type");
           			String view_s_value=(String)abean.get("view_s_value");
           			String view_e_value=(String)abean.get("view_e_value");
           			
           			     			
           			 boolean isFlag=false;           			
           			{
           				LazyDynaBean abean0=null;
           				String setname0="";
           				LazyDynaBean abean2=null;
           				String setname2="";
           				if((j+1)<posConditionList.size())
           				{
           					abean2=(LazyDynaBean)posConditionList.get(j+1);
           					setname2=(String)abean2.get("setname");
           					
           				}
           				if(j!=0)
           				{
           					abean0=(LazyDynaBean)posConditionList.get(j-1);
           					setname0=(String)abean0.get("setname");
           				}
           				if(abean0==null&&abean2==null)
           					isFlag=true;
           				else if((abean0==null&&abean2!=null)&&!setname2.equals(setname))
           					isFlag=true;
           				else if((abean0!=null&&abean2==null)&&!setname0.equals(setname))
           					isFlag=true;
           				else if(abean0!=null&&abean2!=null&&!setname2.equals(setname)&&!setname0.equals(setname))
           					isFlag=true;
           			}
           			
           			if(setname.equalsIgnoreCase("A01")||isFlag)
           			{
           					out.print(" <tr><td width='15%' align='right' >"+itemdesc+"&nbsp;&nbsp;&nbsp;&nbsp;</td><td  width='85%' valign='bottom'  align='left' >");          			
	           				 if(itemtype.equals("A"))
							 {
								if(codesetid.equals("0"))
								{
									
									out.println("<input type=\"text\"  class=textbox  name=\"posConditionList["+j+"].s_value\"   value=\""+s_value+"\"  />&nbsp;");
									if(flag.equals("false"))
									{
										out.print("&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.init.tand")+"&nbsp;&nbsp;");
										out.println("<input type=\"text\"  class=textbox  name=\"posConditionList["+j+"].e_value\"   value=\""+e_value+"\"  />&nbsp;");
									}
								}
								else
								{
									
									if(isMore.equals("0"))
									{
									
										ArrayList options=(ArrayList)abean.get("options");
										out.println("<select  name='posConditionList["+j+"].s_value' ><option value=''></option> ");
										for(int n=0;n<options.size();n++)
										{
											LazyDynaBean a_bean=(LazyDynaBean)options.get(n);
											String avalue=(String)a_bean.get("value");
											String aname=(String)a_bean.get("name");
											out.println("<option value='"+avalue+"' ");
											if(avalue.equals(s_value))
												out.print(" selected ");
											out.print(" >"+aname+"</option>");
										}
										out.print("</select>&nbsp;");
										
										if(flag.equals("false"))
										{
											out.print("&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.init.tand")+"&nbsp;&nbsp;");
											out.println("<select  name='posConditionList["+j+"].e_value' ><option value=''></option> ");
											for(int n=0;n<options.size();n++)
											{
												LazyDynaBean a_bean=(LazyDynaBean)options.get(n);
												String avalue=(String)a_bean.get("value");
												String aname=(String)a_bean.get("name");
												out.println("<option value='"+avalue+"' ");
												if(avalue.equals(e_value))
													out.print(" selected ");
												out.print(" >"+aname+"</option>");
											}
											out.print("</select>&nbsp;");
										
										}
										else
										{
											    out.print("<input type='checkbox' name='posConditionList["+j+"].type'  ");
											    if(type!=null&&type.equals("1"))
											    	out.print(" checked ");
											    out.print(" value='1' /> "+ResourceFactory.getProperty("hire.over"));
										
										}
										
									}
									else
									{
										
						           
						              	out.print(" <input type='text' size='15' name='posConditionList["+j+"].view_s_value'  class=textbox value='"+view_s_value+"'  />");
						             	out.print("<span> <img  src='/images/code.gif' border=0 width=20 height=20 onclick='openInputCodeDialogS_value(\""+codesetid+"\",\"posConditionList["+j+"].view_s_value\")' style='position:relative;top:5px;' /></span>&nbsp;");		
										out.print("<input type='hidden' value='"+s_value+"'  name='posConditionList["+j+"].s_value' /> &nbsp;");
										if(flag.equals("false"))
										{
											out.print("&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.init.tand")+"&nbsp;&nbsp;");
											out.print(" <input type='text' size='15'  name='posConditionList["+j+"].view_e_value'  class=textbox value='"+view_e_value+"'  />");
							             	out.print(" <span><img  src='/images/code.gif' style='position:relative;top:5px;'border=0 width=20 height=20 onclick='openInputCodeDialogE_value(\""+codesetid+"\",\"posConditionList["+j+"].view_e_value\")'  /></span>&nbsp;");		
											out.print("<input type='hidden' value='"+s_value+"'  name='posConditionList["+j+"].e_value' /> &nbsp;");
											
											
									
										}
										else
										{
											    out.print("<input type='checkbox' name='posConditionList["+j+"].type'  ");
											    if(type!=null&&type.equals("1"))
											    	out.print(" checked ");
											    out.print(" value='1' /> "+ResourceFactory.getProperty("hire.over"));
										
										}
									}
								
								}
							
							}
							else if(itemtype.equals("D"))
							{
								out.println("<input type='text'  name='posConditionList["+j+"].s_value'  class=textbox value='"+s_value+"'  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)'    />&nbsp;");
								if(flag.equals("false"))
								{
										out.print("&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.init.tand")+"&nbsp;&nbsp;");
										out.println("<input type='text'  name='posConditionList["+j+"].e_value'  class=textbox value='"+e_value+"'  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)'   />&nbsp;");								
								}
								else
								{
									    out.print("<input type='checkbox' name='posConditionList["+j+"].type'  ");
									    if(type!=null&&type.equals("1"))
									    	out.print(" checked ");
									    out.print(" value='1' /> "+ResourceFactory.getProperty("hire.over"));
								
								}
								
							}
							else if(itemtype.equals("N"))
							{
								out.println("<input type=\"text\"  size='10'  name=\"posConditionList["+j+"].s_value\"   value='"+s_value+"'   class=textbox   />&nbsp;");
								if(flag.equals("false"))
								{
										out.print("&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.init.tand")+"&nbsp;&nbsp;");
										out.println("<input type='text'  size='10'  name='posConditionList["+j+"].e_value'  class=textbox value='"+e_value+"'   />&nbsp;");								
								}
								else
								{
									    out.print("<input type='checkbox' name='posConditionList["+j+"].type'  ");
									    if(type!=null&&type.equals("1"))
									    	out.print(" checked ");
									    out.print(" value='1' /> "+ResourceFactory.getProperty("hire.over"));
								}
							
							}
							
           				out.println("</td></tr>");
           			}
           			else
           			{
           				if(aa_setname.equals(""))
           					aa_setname=setname;          			
           				int f=0;
           				String itemID="";
           				for(int e=j;e<posConditionList.size();e++)
           				{
           					
           						LazyDynaBean abean3=(LazyDynaBean)posConditionList.get(e);
			           			String setname3=(String)abean3.get("setname");
			           			String itemid3=(String)abean3.get("itemid");
			           			if(f==0)
			           			{
									aa_setname=setname3;			           			
			           				
			           			}
			           			String itemtype3=(String)abean3.get("fieldType");
			           			String codesetid3=(String)abean3.get("codesetid");
			           			String isMore3=(String)abean3.get("isMore");
			           			String itemdesc3=(String)abean3.get("itemdesc");
			           			String s_value3=(String)abean3.get("s_value");
			           			String e_value3=(String)abean3.get("e_value");
			           			String flag3=(String)abean3.get("flag");
			           			String type3=(String)abean3.get("type");
			           			String view_s_value3=(String)abean3.get("view_s_value");
			           			String view_e_value3=(String)abean3.get("view_e_value");
           						String show=(String)abean3.get("show");
           					
           			
           						if(e==j)
           							out.print(" <tr id='"+aa_setname+"' style='display=block' ><td width='15%' align='right' >"+itemdesc+"&nbsp;&nbsp;&nbsp;&nbsp;</td><td  width='85%' valign='bottom'  align='left' >");          			
           						
           						
           				
           						if(aa_setname.equals(setname3)&&!itemID.equals(itemid3))
           						{
           							 if(itemID.equals(""))
										itemID=itemid3;
           							 if(f!=0)
	           							 out.print("&nbsp;&nbsp;"+itemdesc3);
           							 if(itemtype3.equals("A"))
									 {
										if(codesetid3.equals("0"))
										{
											
											out.println("<input type=\"text\" size='10' class=textbox  name=\"posConditionList["+e+"].s_value\"   value=\""+s_value3+"\"  />&nbsp;");	
										}
										else
										{
											
											if(isMore3.equals("0"))
											{
											
												ArrayList options=(ArrayList)abean3.get("options");
												out.println("<select  name='posConditionList["+e+"].s_value' ><option value=''></option> ");
												for(int n=0;n<options.size();n++)
												{
													LazyDynaBean a_bean=(LazyDynaBean)options.get(n);
													String avalue=(String)a_bean.get("value");
													String aname=(String)a_bean.get("name");
													out.println("<option value='"+avalue+"' ");
													if(avalue.equals(s_value3))
														out.print(" selected ");
													out.print(" >"+aname+"</option>");
												}
												out.print("</select>");
												
												
												
											}
											else
											{
												
								           
								              	out.print(" <input type='text' size='10' name='posConditionList["+e+"].view_s_value'  class=textbox value='"+view_s_value3+"'  />");
								             	out.print(" <span><img  src='/images/code.gif' style='position:relative;top:5px;' border=0 width=20 height=20 onclick='openInputCodeDialogS_value(\""+codesetid3+"\",\"posConditionList["+e+"].view_s_value\")'  /></span>");		
												out.print("<input type='hidden' value='"+s_value3+"'  name='posConditionList["+e+"].s_value' /> &nbsp;");
												
											}
											out.print("<input type='checkbox' name='posConditionList["+e+"].type'  ");
									    	if(type3!=null&&type3.equals("1"))
									    		out.print(" checked ");
									    		out.print(" value='1' /> "+ResourceFactory.getProperty("hire.over"));
											}
									
									}
									else if(itemtype3.equals("D"))
									{
										out.println("<input type='text' size='10' name='posConditionList["+e+"].s_value'  class=textbox value='"+s_value3+"'  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' readOnly   />&nbsp;");
										
									}
									else if(itemtype3.equals("N"))
									{
										out.println("<input type=\"text\"  size='10'  name=\"posConditionList["+e+"].s_value\"   value='"+s_value3+"'   class=textbox   />&nbsp;");
										
									}
									if(e==(posConditionList.size()-1))
									{
										j=e;
									
										
									}
           						}
           						else
           						{
           							j=e-1;
           						
           							break;
           						}
           						
           						
           						
           				
           						f++;
           				}
           				
           				
           				out.println("</td></tr>");
           			}
           	
           			
           		}
%>
           			    
			          
           </table>
         
          
          <br><br>
          &nbsp;&nbsp; <bean:message key="hire.revert.email"/>&nbsp;&nbsp;
          <table width='97%' align='center' cellpadding="0" cellspacing="0"   ><tr><td class='RecordRow_top common_border_color' >
         &nbsp;
          </td></tr></table>
          <table border=0 width='90%' align='center' >
          <tr>	      
          	<td width='15%' align='left' ></td>
          	<td  width='85%'  align='left' >
          		<table><tr><td>
					<html:checkbox property="isRevert" name="positionDemandForm" value="1"  onclick='showTemplate()' /> <bean:message key="hire.revert.emailtoemployee"/>
					&nbsp;&nbsp;
					</td><td>
					<logic:equal name="positionDemandForm" property="isRevert" value="1">
						<div id='a' style='display:block' >
						<hrms:optioncollection name="positionDemandForm" property="mailTemplateList" collection="list" />
						<html:select name="positionDemandForm" property="mailTemplateID" size="1"   >
							             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
						</html:select>
						</div>
					</logic:equal>
					<logic:equal name="positionDemandForm" property="isRevert" value="0">
						<div id='a' style='display:none' >
						<hrms:optioncollection name="positionDemandForm" property="mailTemplateList" collection="list" />
						<html:select name="positionDemandForm" property="mailTemplateID" size="1"   >
							             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
						</html:select>
						</div>
					</logic:equal>
				</td></tr></table>
			</td>
          </tr>
           </table>
          
          
          
          <br><br>
          </table>
			</td>          
          </tr>
  </table>       
  
  
  <table width='90%' align='center' >
  	<tr>
  		<td align='center' height="35px;">
  		<input type='button' value="<bean:message key="button.ok"/>" class="mybutton" onclick='sub()' >
		<input type='button' value="<bean:message key="button.cancel"/>"  onclick='goback()' class="mybutton" >  
		</td>
  	</tr>
  </table>
  
  
  
  
  <input type='hidden' id="z3" name='z0301' value='<%=(request.getParameter("z0301"))%>' />
  
  
  
  </html:form>
  </body>
  <script language='javascript' >
  initValue();
  if(document.getElementsByName("positionDemandDescList[0].value")[0].value=='01')
	  document.getElementById("pos_tr").style.display="none";
  </script>
</html>
