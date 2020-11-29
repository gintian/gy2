<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
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
</style>
<script language='javascript' >

var posState="<%=posState%>";
function goback()
{
	window.close();

}




function opt(flag)
{
	if(flag==3)
	{
		if(!confirm(CONFIRM_DELETE_HIRE_REQUIREMENT+"？"))
			return;
	}
	if(flag*1==1)
	{
	    var hashVo=new ParameterSet();
        hashVo.setValue("z0301","<%=(request.getParameter("z0301"))%>");
        hashVo.setValue("model","1");
        var In_parameters="opt=1";
        var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:checkBZ_OK,functionId:'3000000228'},hashVo);			
	   
	}
	else
	{
    	var hashvo=new ParameterSet();
	    hashvo.setValue("opt",flag);
	    var In_paramters="z0301=<%=(request.getParameter("z0301"))%>";  
	    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000202'},hashvo);
}
}
function checkBZ_OK(outparameters)
{
   var message =getDecodeStr(outparameters.getValue("message"));
   var opt=outparameters.getValue("opt");
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
        var hashvo=new ParameterSet();
	    hashvo.setValue("opt",opt);
	    var In_paramters="z0301=<%=(request.getParameter("z0301"))%>";  
	    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000202'},hashvo);
   }
}
function returnInfo(outparamters)
{
	var opt=outparamters.getValue("opt");	 //1:发布  2：暂停  3：删除 
	if(opt=='1')
	{
		alert(RELEASE_SUCCESS+"！");
		window.opener.location="/hire/employActualize/employPosition.do?b_query=link";
		window.location="/hire/demandPlan/positionDemand/positionDemandTree.do?b_read=read&operate=read&posState=04&z0301=<%=(request.getParameter("z0301"))%>";
		posState="04";
	}
	else if(opt=='3')
	{
		window.opener.location="/hire/employActualize/employPosition.do?b_query=link";
		window.close();
	}
	else if(opt=='2')
	{
		alert(STOP_SUCCESS+"！");
		window.opener.location="/hire/employActualize/employPosition.do?b_query=link";
		window.location="/hire/demandPlan/positionDemand/positionDemandTree.do?b_read=read&operate=read&posState=09&z0301=<%=(request.getParameter("z0301"))%>";
		posState="09";
	}
	
}


function setResumeCondition()
{
	document.positionDemandForm.action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_initCondition=set&posState="+posState+"&z0301=<%=(request.getParameter("z0301"))%>";
	document.positionDemandForm.submit();

}


function edit()
{
	document.positionDemandForm.action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_edit=edit&operate=edit&posState="+posState+"&z0301=<%=(request.getParameter("z0301"))%>";
	document.positionDemandForm.submit();

}


</script>
<html>
  <head>
  </head>
  <hrms:themes></hrms:themes>
  <body>
  <html:form action="/hire/demandPlan/positionDemand/positionDemandTree">
<%
String bosflag= userView.getBosflag();//得到系统的版本号
if(bosflag!=null&&!bosflag.equalsIgnoreCase("hcm")){
%>
  <br>
<%
}
%>
<%
			PositionDemandForm positionDemandForm=(PositionDemandForm)session.getAttribute("positionDemandForm");
			ArrayList positionDemandDescList=positionDemandForm.getPositionDemandDescList();
			ArrayList posConditionList=positionDemandForm.getPosConditionList();
			String isPosBooklet=positionDemandForm.getIsPosBooklet();
			String e01a1=positionDemandForm.getE01a1();
			
%>
  
  <table width="90%" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">       		
       		<td width='100%'  align="left" class="TableRow_lrt"><bean:message key="hire.browse.position"/></td>
          </tr> 
          <tr >
          	<td class='RecordRow' > 
          <table width='100%' align='center' >
		  	<tr>
		  		<td align='right'>
		  		<hrms:priv func_id="3102201">
				<input type='button' value="<bean:message key="hire.button.modify"/>" onclick='edit()' class="mybutton" style='margin-left:-7px;' ><!-- 修改 -->
				</hrms:priv>
				&nbsp;
		  		<% if(posState.equals("04")){ %>
		  		<hrms:priv func_id="3102203">
		  		<input type='button' value="<bean:message key="hire.button.relesae"/>"  name='issue' onclick='opt(1)'  disabled  class="mybutton" style='margin-left:-7px;' ><!-- 发布 -->
				&nbsp;	
				</hrms:priv>
				<hrms:priv func_id="3102204">
				<input type='button' value="<bean:message key="hire.button.stop"/>"  name='stop' onclick='opt(2)'  class="mybutton" style='margin-left:-7px;' ><!--暂停-->
				&nbsp;	
				</hrms:priv>
				<hrms:priv func_id="3102205">			
				<input type='button' value="<bean:message key="hire.button.delete"/>"  name='del' onclick='opt(3)'  disabled  class="mybutton" style='margin-left:-7px;' ><!--删除-->
				&nbsp;
				</hrms:priv>
		  		<% }else if(posState.equals("09")){ %>
		  		<hrms:priv func_id="3102203">
		  		<input type='button' value="<bean:message key="hire.button.relesae"/>"  name='issue'  onclick='opt(1)'  class="mybutton" style='margin-left:-7px;' >
				&nbsp;				
				</hrms:priv>
				<hrms:priv func_id="3102204">
				<input type='button' value="<bean:message key="hire.button.stop"/>"  name='stop' onclick='opt(2)' disabled   class="mybutton" style='margin-left:-7px;' >
				&nbsp;
				</hrms:priv>
				<hrms:priv func_id="3102205">				
				<input type='button' value="<bean:message key="hire.button.delete"/>"  name='del' onclick='opt(3)'  disabled  class="mybutton" style='margin-left:-7px;' >
				&nbsp;
		  		</hrms:priv>
		  		<% } else if(posState.equals("06")){ %>
		  		<hrms:priv func_id="3102203">
		  		<input type='button' value="<bean:message key="hire.button.relesae"/>"  name='issue' onclick='opt(1)'   class="mybutton" style='margin-left:-7px;' >
				&nbsp;
				</hrms:priv>
				<hrms:priv func_id="3102204">
				<input type='button' value="<bean:message key="hire.button.stop"/>"   name='stop' onclick='opt(2)' disabled   class="mybutton" style='margin-left:-7px;' >
				&nbsp;
				</hrms:priv>
				<hrms:priv func_id="3102205">
				<input type='button' value="<bean:message key="hire.button.delete"/>"  name='del'  onclick='opt(3)'  class="mybutton" style='margin-left:-7px;' >
				&nbsp;
		  		</hrms:priv>
		  		<% } %>
		  		<hrms:priv func_id="3102202">
				<input type='button' value="<bean:message key="hire.filter.condition"/>" onclick='setResumeCondition()'  class="mybutton" style='margin-left:-7px;' ><!--设置简历过滤条件 -->
				</hrms:priv>
				</td>
		  	</tr>
		  </table>
          
          
          <bean:message key="hire.position.desc"/>
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
						z0336_value=value;				 
				 String viewvalue=(String)abean.get("viewvalue");				 
          		 out.print("<tr ");
          		 if(z0336_value!=null&&z0336_value.equalsIgnoreCase("01")&&itemid.equalsIgnoreCase("posID"))
						out.print(" style='display:none'");          		 
          		 out.print("  ><td width='15%' ");
          		  if(itemtype.equals("M"))
				 	out.print(" valign='top' ");
          		 out.print(" align='left' >");
				 out.print(itemdesc);
				 out.print("</td><td  width='85%'  align='left' >");
				 
          		 if(itemtype.equals("A"))
				 {
								if(codesetid.equals("0"))
								{
									out.println(value+"&nbsp;");
								}
								else
								{
						              	out.print(viewvalue);
						              	if(itemid.equalsIgnoreCase("posID")&&isPosBooklet.equals("1"))
						              		out.print("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href='/servlet/performance/fileDownLoad?e01a1="+PubFunc.encrypt(e01a1)+"&opt=hire'  target='_blank'  border='0' >(岗位说明书)</a>");
								}
							
						}
						else if(itemtype.equals("D"))
						{
								out.println(value+"&nbsp;");
						}
						else if(itemtype.equals("N"))
						{
								out.println(value+"&nbsp;");
						}
						else if(itemtype.equals("M"))
						{
								value=value.replaceAll("\r\n","<br>");
								value=value.replaceAll("\n\n","<br>");
								out.println(value+"&nbsp;");
						}
						out.print("</td></tr>");
          		
          
          }
          %>
          
          </table>
          <br><br>
          <bean:message key="hire.employ.request.blank"/>
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
           				if((s_value!=null&&s_value.trim().length()>0)||(e_value!=null&&e_value.trim().length()>0))
           				{
           					out.print(" <tr><td width='20%' align='right' >"+itemdesc+"&nbsp;&nbsp;&nbsp;&nbsp;</td><td  width='85%' valign='bottom'  align='left' >");          			
	           				 if(itemtype.equals("A"))
							 {
								if(codesetid.equals("0"))
								{
									
									out.println(s_value+"&nbsp;");
									if(flag.equals("false")&&e_value!=null&&e_value.trim().length()>0)
									{
										out.print("&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.init.tand")+"&nbsp;&nbsp;");
										out.println(e_value+"&nbsp;");
									}
								}
								else
								{
									
						              	out.print(view_s_value);
						             	if(flag.equals("false")&&view_e_value!=null&&view_e_value.trim().length()>0)
										{
											out.print("&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.init.tand")+"&nbsp;&nbsp;");
											out.print(view_e_value);
										}
										else
										{
											 if(type!=null&&type.equals("1"))
											 {
											    out.print("<input type='checkbox' name='posConditionList["+j+"].type'  ");
											   	out.print(" checked ");
											    out.print(" value='1' disabled /> ");
											}
										}
								}
									
							}
							else if(itemtype.equals("D"))
							{
								out.println(s_value+"&nbsp;");
								if(flag.equals("false")&&e_value!=null&&e_value.trim().length()>0)
								{
										out.print("&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.init.tand")+"&nbsp;&nbsp;");
										out.println(e_value+"&nbsp;");								
								}
								else
								{
									 if(type!=null&&type.equals("1"))
									 {
									    out.print("<input type='checkbox' name='posConditionList["+j+"].type'  ");
									    out.print(" checked ");
									    out.print(" value='1' disabled /> "+ResourceFactory.getProperty("hire.over"));
									 }
								}
								
							}
							else if(itemtype.equals("N"))
							{
								out.println(s_value+"&nbsp;");
								if(flag.equals("false")&&e_value!=null&&e_value.trim().length()>0)
								{
										out.print("&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.init.tand")+"&nbsp;&nbsp;");
										out.println(e_value+"&nbsp;");								
								}
								else
								{
									if(type!=null&&type.equals("1"))
									{
									    out.print("<input type='checkbox' name='posConditionList["+j+"].type'  ");
									    out.print(" checked ");
									    out.print(" value='1' disabled  />"+ResourceFactory.getProperty("hire.over"));
									}
								}
							
							}
							
           					out.println("</td></tr>");
           				}
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
           					
           						String displayDesc="none";
           						if(show!=null&&show.equals("1"))
           							displayDesc="block";
           							
           						if(aa_setname.equals(setname3)&&itemID.equals(itemid3))
           						{
           							count++;
           							d=false;
           						}
           						if(count==1)
           							displayDesc="block";	
           						if(e==j)
           							out.print(" <tr id='"+aa_setname+"' style='display="+displayDesc+"' ><td width='20%' align='right' >"+itemdesc+"&nbsp;&nbsp;&nbsp;&nbsp;</td><td  width='85%' valign='bottom'  align='left' >");          			
           						
           						
           						
           						if(aa_setname.equals(setname3)&&!itemID.equals(itemid3))
           						{
           							 if(itemID.equals(""))
										itemID=itemid3;
           							 if(f!=0)
	           							 out.print("&nbsp;&nbsp;&nbsp;&nbsp;"+itemdesc3+"&nbsp;&nbsp;");
           							 if(itemtype3.equals("A"))
									 {
										if(codesetid3.equals("0"))
										{
											if(s_value3!=null&&s_value3.trim().length()>0)
												out.println(s_value3+"&nbsp;");
											else 
												out.println(ResourceFactory.getProperty("hire.nothing")+"&nbsp;");
										}
										else
										{
											if(view_s_value3!=null&&view_s_value3.trim().length()>0)
												out.print(view_s_value3+"&nbsp;");
											else 
												out.println(ResourceFactory.getProperty("hire.nothing")+"&nbsp;");
										}
									
									}
									else if(itemtype3.equals("D"))
									{
										if(s_value3!=null&&s_value3.trim().length()>0)
											out.println(s_value3+"&nbsp;");
										else
											out.println(ResourceFactory.getProperty("hire.nothing")+"&nbsp;");						
									}
									else if(itemtype3.equals("N"))
									{
										//out.println(s_value3+"&nbsp;");
										if(s_value3!=null&&s_value3.trim().length()>0)
											out.println(s_value3+"&nbsp;");
										else
											out.println(ResourceFactory.getProperty("hire.nothing")+"&nbsp;");	
										
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
          <bean:message key="hire.auto.reemail"/>
          <table width='97%' align='center' cellpadding="0" cellspacing="0"   ><tr><td class='RecordRow_top common_border_color' >
         &nbsp;
          </td></tr></table>
          <table border=0 width='90%' align='center' >
          <tr>	      
          	<td width='15%' align='left' ></td>
          	<td  width='85%'  align='left' >
          		<table><tr><td>
					<html:checkbox property="isRevert" name="positionDemandForm" value="1"  disabled="true" onclick='showTemplate()' />  自动回复邮件给应聘者
					&nbsp;&nbsp;
					</td><td>
					<logic:equal name="positionDemandForm" property="isRevert" value="1">
						<hrms:optioncollection name="positionDemandForm" property="mailTemplateList" collection="list" />
						<html:select name="positionDemandForm" property="mailTemplateID" size="1" disabled="true"  >
							             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
						</html:select>
					</logic:equal>
				</td></tr></table>
			</td>
          </tr>
           </table>
          
          
          
          <br><br>
			</td>          
          </tr>
  </table>       
  
  
  <table width='90%' align='center' cellpadding="0" cellspacing="0">
  	<tr>
  		<td align='right' height="35px;">
  				<hrms:priv func_id="3102201">
				<input type='button' value="<bean:message key="hire.button.modify"/>" onclick='edit()'   class="mybutton" >
				</hrms:priv>
				&nbsp;
		  		<% if(posState.equals("04")){ %>
		  		<hrms:priv func_id="3102203">
		  		<input type='button' value="<bean:message key="hire.button.relesae"/>"  name='issue' onclick='opt(1)'  disabled  class="mybutton" style='margin-left:-7px;' >
				&nbsp;	
				</hrms:priv>
				
				<hrms:priv func_id="3102204">
				<input type='button' value="<bean:message key="hire.button.stop"/>"  name='stop' onclick='opt(2)'  class="mybutton" style='margin-left:-7px;' >
				&nbsp;
				</hrms:priv>
				
				<hrms:priv func_id="3102205">				
				<input type='button' value="<bean:message key="hire.button.delete"/>"  name='del' disabled  onclick='opt(3)'  class="mybutton" style='margin-left:-7px;' >
				&nbsp;
				</hrms:priv>
				
		  		<% }else if(posState.equals("09")){ %>
		  		<hrms:priv func_id="3102203">
		  		<input type='button' value="<bean:message key="hire.button.relesae"/>"   name='issue'  onclick='opt(1)'  class="mybutton" style='margin-left:-7px;' >
				&nbsp;	
				</hrms:priv>
				<hrms:priv func_id="3102204">			
				<input type='button' value="<bean:message key="hire.button.stop"/>"  name='stop' onclick='opt(2)' disabled   class="mybutton" style='margin-left:-7px;' >
				&nbsp;	
				</hrms:priv>
				<hrms:priv func_id="3102205">			
				<input type='button' value="<bean:message key="hire.button.delete"/>"  name='del' disabled  onclick='opt(3)'  class="mybutton" style='margin-left:-7px;' >
				&nbsp;
				</hrms:priv>
		  		
		  		<% } else if(posState.equals("06")){ %>
		  		<hrms:priv func_id="3102203">
		  		<input type='button' value="<bean:message key="hire.button.relesae"/>"   name='issue'  onclick='opt(1)'  class="mybutton" style='margin-left:-7px;' >
				&nbsp;
				</hrms:priv>
				<hrms:priv func_id="3102204">
				<input type='button' value="<bean:message key="hire.button.stop"/>"  name='stop' onclick='opt(2)' disabled   class="mybutton" style='margin-left:-7px;' >
				&nbsp;
				</hrms:priv>
				<hrms:priv func_id="3102205">
				<input type='button' value="<bean:message key="hire.button.delete"/>"  name='del'  onclick='opt(3)'  class="mybutton" style='margin-left:-7px;' >
				&nbsp;
		  		</hrms:priv>
		  		<% } %>
		  		<hrms:priv func_id="3102202">
				<input type='button' value="<bean:message key="hire.filter.condition"/>"  onclick='setResumeCondition()'  class="mybutton" style='margin-left:-7px;' >  
				</hrms:priv>
				</td>
  	</tr>
  </table>
  
  
  
  
  
  
  
  
  </html:form>
  </body>
</html>
