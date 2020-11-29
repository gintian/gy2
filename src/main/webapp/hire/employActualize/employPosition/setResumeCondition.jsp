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
			PositionDemandForm positionDemandForm=(PositionDemandForm)session.getAttribute("positionDemandForm");
		    ArrayList posConditionList=positionDemandForm.getPosConditionList();
		     String upValue=positionDemandForm.getUpValue();	
   %>
  				
<style type="text/css">
.RecordRow_top {
	border: inset 1px #94B6E6;
	BACKGROUND-COLOR: #FFFFFF;
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;

}

.RecordRow_b {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: 0pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}


</style>
  <script language='javascript' >

	//在initStr寻找所有的AFindText替换成ARepText
	function strReplaceAll(initStr,AFindText,ARepText){
	
	  var raRegExp = new RegExp(AFindText,"g");
	
	  return initStr.replace(raRegExp,ARepText);
	
	}
	
	function editUpValue(setName,obj)
	{
		
		var upValueObj=eval("document.positionDemandForm.upValue");
		var upValue_str=upValueObj.value.toLowerCase();
		
		if(upValue_str.indexOf(","+setName)!=-1)
		{
			if(obj.checked==false)
				upValue_str=strReplaceAll(upValue_str,","+setName,"")
		}
		else
		{
			if(obj.checked==true)
				upValue_str=upValue_str+","+setName
		}	
		upValueObj.value=upValue_str;
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




function goback()
{
	<%
		if(request.getParameter("b_initCondition")!=null&&request.getParameter("b_initCondition").equals("set"))
		{
			out.println("window.location='/hire/demandPlan/positionDemand/positionDemandTree.do?b_read=read&operate=read&posState="+request.getParameter("posState")+"&z0301="+request.getParameter("z0301")+"'");
		}
		else
		{
		    out.println("var obj=new Object();");
		    out.println("obj.opt='0'");
		    out.println("returnValue=obj;");
			out.println("window.close()");
		}
	%>

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
									 alert(itemdesc+RIGHT_FORMAT_IS+"！");
									 return false;
							 	}
							 	if(year<1900||year>2100)
							 	{
							 		 alert(itemdesc+YEAR_SCORPE+"！");
									 return false;
							 	}
							 	
							 	if(!isValidDate(day, month, year))
							 	{
									 alert(itemdesc+RIGHT_FORMAT_IS+"！");
									 return false;
							 	}
							 }
			}
			return dd
	}
	function clearCodeValue(hz,hzv)
  {
    var o1=document.getElementsByName(hz);
    if(o1)
    {
      if(trim(o1[0].value)=='')
      {
          var o2=document.getElementsByName(hzv);
          if(o2)
            o2[0].value='';
          o1[0].value='';
      }
    }
  }

function editComplexTemplate(tid)
{
   var theurl="/hire/zp_options/cond/getZpCondFieldsList.do?b_search=search`type=1`templateid="+tid;
   var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
	var retvo= window.showModalDialog(iframe_url, null, 
					        "dialogWidth:760px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");	
	if(retvo){
		window.location.href=window.location;
	}	
}
function sub(type)
{
    if(type=='0')
    {
	   if(!confirm(CONFIRM_EXECUTE_CONDITION_FILTER))
    		return;
	}
	var id="-1";
	if(type=='0')
	{
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
    	       			if(document.getElementsByName("posConditionList[<%=d%>].s_value")[0]){
	    	       			var a<%=d%>=document.getElementsByName("posConditionList[<%=d%>].s_value")						
							if(!validateData(a<%=d%>[0],'<%=itemdesc%>'))
								return;
						}
						<%
						if(flag.equals("false"))
						{
						%>	
							if(document.getElementsByName("posConditionList[<%=d%>].e_value")[0]){
								var b<%=d%>=document.getElementsByName("posConditionList[<%=d%>].e_value")						
								if(!validateData(b<%=d%>[0],'<%=itemdesc%>'))
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
    	       			if(document.getElementsByName("posConditionList[<%=d%>].s_value")[0]){
	    	       			var a<%=d%>=document.getElementsByName("posConditionList[<%=d%>].s_value")
							if(trim(a<%=d%>[0].value).length!=0)
							{						
								 var myReg =/^(-?\d+)(\.\d+)?$/
								 if(!myReg.test(a<%=d%>[0].value)) 
								 {
									alert("<%=itemdesc%>"+PLEASE_INPUT_NUMBER+"！");
									return;
								 }
							 }
						 }
						 <%
						if(flag.equals("false"))
						{
						%>	
						if(document.getElementsByName("posConditionList[<%=d%>].s_value")[0]){
							var b<%=d%>=document.getElementsByName("posConditionList[<%=d%>].e_value")
							if(trim(b<%=d%>[0].value).length!=0)
							{		
								 var myReg =/^(-?\d+)(\.\d+)?$/
								 if(!myReg.test(b<%=d%>[0].value)) 
								 {
									alert("<%=itemdesc%>"+PLEASE_INPUT_NUMBER+"！");
									return;
								 }
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
     }
     else
     {
        
        var obj=document.getElementsByName("tid");
        if(obj!=null)
        {
            for(var i=0;i<obj.length;i++)
            {
              if(obj[i].checked)
              {
                id=obj[i].value;
                break;
              }
            }
        }
        if(id=="-1")
        {
          alert("请选择一个模板进行过滤!");
          return;
        }
        if(!confirm(CONFIRM_EXECUTE_CONDITION_FILTER))
           return;
     }

	document.positionDemandForm.action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_setCondition=add&opt=1&z0301=<%=(request.getParameter("z0301"))%>&templateid="+id+"&type="+type;
	document.positionDemandForm.submit();
}


<%
	if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("1"))
	{
	    String type="0";
	    if(request.getParameter("type")!=null)
	       type=request.getParameter("type");
	    if(type.equals("0"))
	    	out.println("alert(FILTER_COMPLETE)");
	    else
	        out.println("alert('简历已经过滤完毕！')");
	    out.println(" var obj=new Object();");
	    out.println(" obj.opt='1';");
	    out.println("returnValue=obj;");
		out.print("window.close();");
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
<hrms:themes></hrms:themes>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
%>
<body onload="setTPinput()">
  <html:form action="/hire/demandPlan/positionDemand/positionDemandTree">
  <table height="400px" width="820px" style="margin-top:-2px;"><tr><td height="410px">
   <hrms:tabset name="pageset" width="90%" height="100%" type="false"> 
   <hrms:tab name="tab1" label="简单条件模板" visible="true">
   <table width="100%"  height='100%' cellpadding="0" cellspacing="0"  align="center"> 
			<tr> <td valign="top"  align='center'>
   <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top:5px">
          <tr >
          	<td class='RecordRow_b common_border_color' >
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<Br>
<%
}
%>            	
          	&nbsp;&nbsp;<Img src='/images/icon_speaker.gif' /><bean:message key="hire.filter.resume"/>
          </td>
          </tr>
          <tr>
          <td class='RecordRow'>
          <table border="0" width='95%' align='center' >
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
           					out.print(" <tr><td width='18%' align='right' >"+itemdesc+"&nbsp;&nbsp;&nbsp;&nbsp;</td><td  width='85%' valign='bottom'  align='left' >");          			
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
										
						           
						              	out.print(" <input type='text' onchange='clearCodeValue(\"posConditionList["+j+"].view_s_value\",\"posConditionList["+j+"].s_value\")' size='15' name='posConditionList["+j+"].view_s_value'  class=textbox value='"+view_s_value+"'  />");
						              	out.print("<span>");
						             	out.print(" <img  src='/images/code.gif' border=0 width=20 height=20 onclick='openInputCodeDialogS_value(\""+codesetid+"\",\"posConditionList["+j+"].view_s_value\")' style='position:relative;top:5px;' />&nbsp;");		
									    out.print("</span>");
										out.print("<input type='hidden' value='"+s_value+"'  name='posConditionList["+j+"].s_value' /> &nbsp;");
										if(flag.equals("false"))
										{
											out.print("&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.init.tand")+"&nbsp;&nbsp;");
											out.print(" <input type='text' onchange='clearCodeValue(\"posConditionList["+j+"].view_s_value\",\"posConditionList["+j+"].s_value\")' size='15'  name='posConditionList["+j+"].view_e_value'  class=textbox value='"+view_e_value+"'  />");
											out.print("<span>");
							             	out.print(" <img  src='/images/code.gif' border=0 width=20 height=20 onclick='openInputCodeDialogE_value(\""+codesetid+"\",\"posConditionList["+j+"].view_e_value\")' style='position:relative;top:5px;'/>&nbsp;");
							             	out.print("</span>");		
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
	           							 out.print(itemdesc3);
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
												
								           
								              	out.print(" <input onchange='clearCodeValue(\"posConditionList["+j+"].view_s_value\",\"posConditionList["+j+"].s_value\")' type='text' size='10' name='posConditionList["+e+"].view_s_value'  class=textbox value='"+view_s_value3+"'  />");
								              	out.print("<span>");
								             	out.print(" <img  src='/images/code.gif' border=0 width=20 height=20 onclick='openInputCodeDialogS_value(\""+codesetid3+"\",\"posConditionList["+e+"].view_s_value\")'style='position:relative;top:5px;'  />");
								             	out.print("</span>");		
												out.print("<input type='hidden' value='"+s_value3+"'  name='posConditionList["+e+"].s_value' /> &nbsp;");
												
											}
											out.print("<input type='checkbox' name='posConditionList["+e+"].type'  ");
									    	if(type3!=null&&type3.equals("1"))
									    		out.print(" checked ");
									    		out.print(" value='1' /> "+ResourceFactory.getProperty("hire.over")+"&nbsp;&nbsp;");
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
           
			  
    		<TR>
			<td>
			&nbsp;   
			</td>
			<td>
			 <html:checkbox property='vague' name='positionDemandForm' value='1' /><bean:message key="label.query.like"/>
			 <input type='hidden' value="${positionDemandForm.upValue}" name='upValue' />
			</td>
			</TR>
    
    
           </table>	<br><br>
  		</td></tr>
<!--   	<tr> -->
<!--   		<td align='right'> -->
<!--   		<br> -->
<%--   		<input type='button' value="<bean:message key="button.ok"/>"  class="mybutton" onclick="sub('0');" > --%>
<!-- 		&nbsp; -->
<%-- 		<input type='button' value="<bean:message key="button.cancel"/>"  onclick='goback()' class="mybutton" >   --%>
<!-- 		</td> -->
<!--   	</tr> -->
  </table>
  </td>
  </tr>
  </table>
  </hrms:tab>
  <hrms:tab name="tab2" label="复杂条件模板" visible="true">
     <table width="100%" height="100" border="0" cellpadding="0" cellspacing="0" align="center" class="ListTable" style="margin-top:5px;">
     <tr> <td valign="top"  align='center'>
				
	 <table width="90%" border="0" cellpadding="0" cellspacing="0" align="center" class="ListTable">
      <thead>
     <tr>
     <td colspan="3" align="left" class="RecordRow" nowrap/>
     <Img src='/images/icon_speaker.gif' /><bean:message key="hire.filter.resume"/>
     </td>
     </tr>
     <tr class="TableRow">
     <td align="center" class="TableRow"><bean:message key="column.select"/></td><td align="center" class="TableRow"><bean:message key="lable.tz_template.name"/></td><td align="center" class="TableRow"><bean:message key="label.edit"/></td>
     </tr>
     </thead>
     <% int j=0; %>
     <logic:iterate id="element" name="positionDemandForm" property="complexTemplateList" indexId="index">
     <tr>
     <td align="center" class="RecordRow">
     <%if(j==0){ %>
      <input type="radio" name="tid" value="<bean:write name="element" property="id"/>" checked/>
      <%} else{ %>
     <input type="radio" name="tid" value="<bean:write name="element" property="id"/>"/>
     <%} %>
     </td>
     <td align="left" class="RecordRow"><bean:write name="element" property="name"/></td>
     <td align="center" class="RecordRow">
     <img src="/images/edit.gif" border="0" onclick='editComplexTemplate("<bean:write name="element" property="id"/>");'/>
     </td>
     </tr>
     <%j++; %>
     </logic:iterate>
<!--   	<tr> -->
<!--   		<td colspan="3" align='right' valign="bottom"> -->
<!--   		<br> -->
<%--   		<input type='button' value="<bean:message key="button.ok"/>"  class="mybutton" onclick="sub('1');" > --%>
<!-- 		&nbsp; -->
<%-- 		<input type='button' value="<bean:message key="button.cancel"/>"  onclick='goback()' class="mybutton" >   --%>
<!-- 		</td> -->
<!--   	</tr> -->
  </table>
  </td>
  </tr>
  </table>
  </hrms:tab>
  </hrms:tabset>
  </td></tr>
  <tr>
  	<td align="center">
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<Br>
<%
}
%>  
  		<input type='button' id="okBtn" value="<bean:message key="button.ok"/>"  class="mybutton" onclick="sub('0')" >
		&nbsp;
		<input type='button' value="<bean:message key="button.cancel"/>"  onclick='goback()' class="mybutton" >
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  
  	</td>
  </tr>
  </table>
  </html:form>
  </body>
	<script type="text/javascript">
		<%-- 页面加载完毕为按钮添加事件 add by 刘蒙 --%>
		window.attachEvent("onload", function() {
			var arrDivs = document.getElementsByTagName("DIV");
			for (var i = 0; i < arrDivs.length; i++) {
				if (arrDivs[i].className != "tab") {
					continue;
				}
				if (arrDivs[i].innerHTML == "简单条件模板") {
					arrDivs[i].attachEvent("onclick", function() {
						document.getElementById("okbtn").onclick = function() {
							sub("0");
						};
					});
				} else if (arrDivs[i].innerHTML == "复杂条件模板") {
					arrDivs[i].attachEvent("onclick", function() {
						document.getElementById("okbtn").onclick = function() {
							sub("1");
						};
					});
				} 
			}
		});
	</script>

<%
    if(bosflag!=null&&bosflag.equalsIgnoreCase("hcm")){//得到系统的版本号)
%>
<script type="text/javascript">
        var _tabsetpane_pageset=document.getElementById("_tabsetpane_pageset");
        _tabsetpane_pageset.style.width="815px";
</script>
<%
   }
%>  
