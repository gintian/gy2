<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%String [] onlyIndex=null; %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/hire/employNetPortal/employNetPortal.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
			     org.apache.commons.beanutils.LazyDynaBean,
			      com.hrms.struts.taglib.CommonData,com.hrms.hjsj.utils.Sql_switcher,com.hrms.hjsj.sys.Constant,
			     java.util.*,com.hrms.hjsj.sys.ResourceFactory,com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>

<html>
<LINK 
href="/css/employNetStyle.css" type=text/css rel=stylesheet>
<LINK href="/css/main.css" type=text/css rel=stylesheet>
<LINK href="/css/nav.css" type=text/css rel=stylesheet>
 <%
	String dbtype="1";
  if(Sql_switcher.searchDbServer()== Constant.ORACEL)
  {
    dbtype="2";
  }
  else if(Sql_switcher.searchDbServer()== Constant.DB2)
  {
    dbtype="3";
  }
  String aurl = (String)request.getServerName();
	    String port=request.getServerPort()+"";
	    String prl=request.getScheme();
	    String url_p=prl+"://"+aurl+":"+port;
	    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	    String userViewName="";
	    if(userView!=null)
	        userViewName=userView.getUserName();

  %>
<script language='javascript'>
<% int m=0;  %>

  var only_str="";
  var blackFieldValue="";
function sub(flag,anwserFlag)
{
        var onlyField=employPortalForm.onlyField.value;
        var blackField=employPortalForm.blackField.value;
        var arr=onlyField.split(",");
        if(trim(blackFieldValue).length!=0)
            blackFieldValue="";
		var a_flag=$("flag");
		if(flag=='0')
    		a_flag.value='2';
		else
     		a_flag.value=flag;
		if(trim(only_str).length!=0)
		   only_str="";
		 if(flag!='3'&&flag!='4')
		 {
		<%  m=0;  %>
		<logic:iterate  id="element"    name="employPortalForm"  property="resumeFieldList" indexId="index"> 
				<logic:equal name="element" property="itemtype" value="M">
						var a<%=m%>=document.getElementsByName("resumeFieldList[<%=m%>].value")
						if(trim(a<%=m%>[0].value).length!=0)
						{
							
							if(IsOverStrLength(a<%=m%>[0].value,5000))
							{
							   if(anwserFlag=='1')
							   {
							        var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
							        alert(replaceAll(vv.innerHTML,"<BR>","")+OVERSTEP_LENGTH_SCOPE);
								    return;
							   }
							   else
							   {
							    	alert("<bean:write  name="element" property="itemdesc"/>"+OVERSTEP_LENGTH_SCOPE);
								    return;
								}
							}
						}
					
				</logic:equal>
				
				
				
				<logic:equal name="element" property="itemtype" value="D">
					var a<%=m%>=document.getElementsByName("resumeFieldList[<%=m%>].value");
					if(trim(a<%=m%>[0].value).length!=0)
					{						
						 var reg = /^(\d{4})((-|\.)(\d{1,2}))?((-|\.)(\d{1,2}))?$/;
						
						if(!reg.test(a<%=m%>[0].value))
						 {
						    if(anwserFlag=='1')
							   {
							       var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
							       alert(replaceAll(vv.innerHTML,"<BR>","")+THE_RIGHT_DATEFORMAT+"！");
							       return;
							   }
							   else
							   {
						     	 alert("<bean:write  name="element" property="itemdesc"/> "+THE_RIGHT_DATEFORMAT+"！");
							     return;
							   }
						 }
						 var year="";a<%=m%>[0].value.substring(0,4);
						 var month="";
						 var day="";
						 if(trim(a<%=m%>[0].value).length<=4)
						 {
						      year=trim(a<%=m%>[0].value);
						      if(year<1900||year>2100)
						      {
						        if(anwserFlag=='1')
						        {
						           var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
						           alert(replaceAll(vv.innerHTML,"<BR>","")+YEAR_SCORPE+"！");
						     	   return;
						        }
						        else
						        {
						 	       alert("<bean:write  name="element" property="itemdesc"/> "+YEAR_SCORPE+"！");
						     	   return;
						     	}
					          }
						 }
						 if(trim(a<%=m%>[0].value).length>4&&trim(a<%=m%>[0].value).length<8)
						 {
						    year=a<%=m%>[0].value.substring(0,4);
						    month=a<%=m%>[0].value.substring(5,trim(a<%=m%>[0].value).length);
						     if(year<1900||year>2100)
						     {
						        if(anwserFlag=='1')
						        {
						           var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
						            alert(replaceAll(vv.innerHTML,"<BR>","")+YEAR_SCORPE+"！");
						     	    return;
						        }
						        else
						        {
						        	alert("<bean:write  name="element" property="itemdesc"/> "+YEAR_SCORPE+"！");
						     	    return;
						     	 }
					     	 }
					     	 if (month < 1 || month > 12) {
					         	 if(anwserFlag=='1')
					     	     {
					     	        var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
					     	        alert(replaceAll(vv.innerHTML,"<BR>","")+THE_MONTH_SCOPE+"！");
						     	    return;
					     	     }
					     	     else
					     	     {
                                   alert("<bean:write  name="element" property="itemdesc"/> "+THE_MONTH_SCOPE+"！");
						     	   return;
						     	 }
                              }
						 }
						 if(trim(a<%=m%>[0].value).length>=8)
						 {
						   var split="";
						   if(a<%=m%>[0].value.indexOf(".")!=-1)
						   {
						     split=".";
						   }
						   else
						   {
						    split="-";
						   }
						   year=a<%=m%>[0].value.substring(0,4);
						   month=a<%=m%>[0].value.substring(5,a<%=m%>[0].value.lastIndexOf(split));
						   day=a<%=m%>[0].value.substring(a<%=m%>[0].value.lastIndexOf(split)+1);
						    if(year<1900||year>2100)
						     {
						        if(anwserFlag=='1')
					     	     {
					     	        var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
					     	        alert(replaceAll(vv.innerHTML,"<BR>","")+YEAR_SCORPE+"！");
						     	    return;
					     	     }
					     	     else
					     	     {
						     	    alert("<bean:write  name="element" property="itemdesc"/>"+YEAR_SCORPE+"！");
						     	    return;
						     	 }
					     	 }
					     	  if(!isValidDate(day, month, year))
						    {
						        if(anwserFlag=='1')
						        {
						             var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
						              alert(replaceAll(vv.innerHTML,"<BR>","")+THE_MONTH_AND_THE_DAY_SCOPE+"！");
					    		      return;
						        }
						        else
						        {
					    		  alert("<bean:write  name="element" property="itemdesc"/>"+THE_MONTH_AND_THE_DAY_SCOPE+"！");
					    		  return;
					    		}
					    	 }
						 }
						
						 	
						

					 }
				</logic:equal>		
				
				
				<logic:equal name="element" property="itemtype" value="N">
					var a<%=m%>=document.getElementsByName("resumeFieldList[<%=m%>].value")
					if(trim(a<%=m%>[0].value).length!=0)
					{						
						 var myReg =/^(-?\d+)(\.\d+)?$/
						 if(!myReg.test(a<%=m%>[0].value)) 
						 {
						    if(anwserFlag=='1')
						    {
						       var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
						        alert(replaceAll(vv.innerHTML,"<BR>","")+PLEASE_INPUT_NUMBER+"！");
						    	return;
						    }
						    else
						    {
						    	alert("<bean:write  name="element" property="itemdesc"/>"+PLEASE_INPUT_NUMBER+"！");
						    	return;
						    }
						 }
						 
						 <logic:equal name="element" property="decimalwidth" value="0">
						 	if(a<%=m%>[0].value.indexOf(".")!=-1)
						 	{
						 	   if(anwserFlag=='1')
						 	   {
						 	      var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
						 	       alert(replaceAll(vv.innerHTML,"<BR>","")+THE_TYPE_IS_INTEGER+"！");
								   return;
						 	   }
						 	   else
						 	   {
						 		 alert("<bean:write  name="element" property="itemdesc"/>"+THE_TYPE_IS_INTEGER+"！");
								 return;
							   }
						 	}
						 </logic:equal>
						 <logic:notEqual name="element" property="decimalwidth" value="0">
						  var vv="";
						 	if(a<%=m%>[0].value.indexOf(".")!=-1)
						 	{
						 		vv=a<%=m%>[0].value.substring(0,a<%=m%>[0].value.indexOf("."))
						 	}
						 	else
						 	{
						 	   vv=a<%=m%>[0].value;
						 	}
						 	var dd="<bean:write  name="element" property="itemlength"/>";
						 	if(vv.length>(dd*1))
						 	{
						 	   if(anwserFlag=='1')
						 	   {
						 	       var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
						 	      alert(replaceAll(vv.innerHTML,"<BR>","")+"超出长度范围！");
								  return;
						 	   }
						 	   else
						 	   {
						 	     alert("<bean:write  name="element" property="itemdesc"/>超出长度范围！");
								 return;
							   }
						 	}
						 </logic:notEqual>
					 }
				</logic:equal>		
				<logic:equal name="element" property="itemtype" value="A">
					<logic:equal name="element" property="codesetid" value="0">
						var a<%=m%>=document.getElementsByName("resumeFieldList[<%=m%>].value")
						var itemid="<bean:write  name="element" property="itemid"/>";
						for(var t=0;t<arr.length;t++)
						{
						   if(arr[t]==itemid)
						    {
						       only_str+=","+itemid+"/"+a<%=m%>[0].value+"/"+"<bean:write  name="element" property="itemdesc"/>";
						       break;
						    }
						}
						if(itemid.toUpperCase()==blackField.toUpperCase())
						{
						   blackFieldValue=trim(a<%=m%>[0].value);
						}
						if(trim(a<%=m%>[0].value).length!=0)
						{
							if(IsOverStrLength(a<%=m%>[0].value,<bean:write  name="element" property="itemlength"/>))
							{
							  if(anwserFlag=='1')
							  {
							     var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
							    alert(replaceAll(vv.innerHTML,"<BR>","")+OVER_LENGTH_SCOPE);
								return;
							  }
							  else
							  {
								alert("<bean:write  name="element" property="itemdesc"/>"+OVER_LENGTH_SCOPE);
								return;
							  }
							}
						}
					
					</logic:equal>
					<logic:notEqual name="element" property="codesetid" value="0">
						<logic:equal name="element" property="isMore" value="1">
						var a<%=m%>=document.getElementsByName("resumeFieldList[<%=m%>].value")
						var aa<%=m%>=document.getElementsByName("resumeFieldList[<%=m%>].viewvalue")
						if(trim(aa<%=m%>[0].value).length==0)
						{							
							a<%=m%>[0].value="";
						}
						</logic:equal>
					</logic:notEqual>
				</logic:equal>			
			<% m++; %>	
		</logic:iterate>
		<% m=0; %>
		<logic:iterate  id="element"    name="employPortalForm"  property="resumeFieldList" indexId="index">
			<logic:equal name="element" property="must" value="1">
				var a<%=m%>=document.getElementsByName("resumeFieldList[<%=m%>].value")
				if(trim(a<%=m%>[0].value).length==0)
				{
				    if(anwserFlag=='1')
				    {
				       var vv=document.getElementById("<bean:write  name="element" property="itemid"/>memo");
				        alert(replaceAll(vv.innerHTML,"<BR>","")+THIS_IS_MUST_FILL+"！");
						return;
				    }
				    else
				    {
						alert("<bean:write  name="element" property="itemdesc"/>"+THIS_IS_MUST_FILL+"！");
						return;
					}
				}
			</logic:equal>
		<% m++; %>
		</logic:iterate>
		
		}
		sub2(flag);
}

function query()
	{
		
		<% int x=0;  %>
		<logic:iterate  id="element"    name="employPortalForm"  property="conditionFieldList" indexId="index"> 
				<logic:equal name="element" property="itemtype" value="D">
					var a<%=x%>=document.getElementsByName("conditionFieldList[<%=x%>].value")
					if(trim(a<%=x%>[0].value).length!=0)
					{						
						 var myReg =/^(-?\d+)(\.\d+)?$/
						 if(IsOverStrLength(a<%=x%>[0].value,10))
						 {
							 alert("<bean:write  name="element" property="itemdesc"/> "+DATE_FORMAT_IS_NOT_RIGHT+"！");
							 return;
						 }
						 else
						 {
						 	if(trim(a<%=x%>[0].value).length!=10)
						 	{
						 		 alert("<bean:write  name="element" property="itemdesc"/> "+DATE_FORMAT_IS_NOT_RIGHT+"！");
								 return;
						 	}
							var year=a<%=x%>[0].value.substring(0,4);
							var month=a<%=x%>[0].value.substring(5,7);
							var day=a<%=x%>[0].value.substring(8,10);
							if(!myReg.test(year)||!myReg.test(month)||!myReg.test(day)) 
						 	{
								 alert("<bean:write  name="element" property="itemdesc"/> "+DATE_FORMAT_IS_NOT_RIGHT+"！");
								 return;
						 	}
						 	if(year<1900||year>2100)
						 	{
						 		 alert("<bean:write  name="element" property="itemdesc"/> "+YEAR_SCORPE+"！");
								 return;
						 	}
						 	
						 	if(!isValidDate(day, month, year))
						 	{
								 alert("<bean:write  name="element" property="itemdesc"/> "+DATE_FORMAT_IS_NOT_RIGHT+"！");
								 return;
						 	}
						 }
					 }
				</logic:equal>	
				
							
				<logic:equal name="element" property="itemtype" value="N">
					var a<%=x%>=document.getElementsByName("conditionFieldList[<%=x%>].value")
					if(trim(a<%=x%>[0].value).length!=0)
					{						
						 var myReg =/^(-?\d+)(\.\d+)?$/
						 if(!myReg.test(a<%=x%>[0].value)) 
						 {
							alert("<bean:write  name="element" property="itemdesc"/>"+PLEASE_INPUT_NUMBER+"！");
							return;
						 }
					 }
				</logic:equal>		
				<logic:equal name="element" property="itemtype" value="A">
					<logic:equal name="element" property="codesetid" value="0">
						var a<%=x%>=document.getElementsByName("conditionFieldList[<%=x%>].value")
						if(trim(a<%=x%>[0].value).length!=0)
						{
							if(IsOverStrLength(a<%=x%>[0].value,<bean:write  name="element" property="itemlength"/>))
							{
								alert("<bean:write  name="element" property="itemdesc"/>"+OVER_LENGTH_SCOPE);
								return;
							}
						}
					
					</logic:equal>
					<logic:notEqual name="element" property="codesetid" value="0">
						<logic:equal name="element" property="isMore" value="1">
						var a<%=x%>=document.getElementsByName("conditionFieldList[<%=x%>].value")
						var aa<%=x%>=document.getElementsByName("conditionFieldList[<%=x%>].viewvalue")
						if(trim(aa<%=x%>[0].value).length==0)
						{							
							a<%=x%>[0].value="";
						}
						</logic:equal>
					</logic:notEqual>
					
				</logic:equal>			
			<% x++; %>	
		</logic:iterate>	
		
		document.employPortalForm.action="/hire/employNetPortal/search_zp_position.do?b_query=link";
		document.employPortalForm.submit();
	}
	function previewTableByActive()
  {
   var hashvo=new ParameterSet();
   hashvo.setValue("dbname","${employPortalForm.dbName}");   
   hashvo.setValue("inforkind","1"); 
   hashvo.setValue("flag","hire");
   hashvo.setValue("id","${employPortalForm.a0100}"); 
   var request=new Request({method:'post',onSuccess:showPrint,functionId:'07020100078'},hashvo);
  }
  function showPrint(outparamters)
{
   var personlist=outparamters.getValue("personlist");  
   var obj = document.getElementById('CardPreview1');    
   if(obj==null)
   {
      alert("没有下载打印控件，请设置IE重新下载！");
      return false;
   }
   obj.SetCardID(tabid);
   obj.SetDataFlag("1");
   obj.SetNBASE("${employPortalForm.dbName}");
   obj.ClearObjs();   
   if(personlist!=null&&personlist.length>0)
   {
     for(var i=0;i<personlist.length;i++)
     {
       obj.AddObjId(personlist[i].dataValue);
     }
   }
   try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
   obj.ShowCardModal();
   
}
	function initCard()
{
      var rl = document.getElementById("hostname").href;     
      var aurl=rl;
      var DBType="<%=dbtype%>";
      var UserName="<%=userViewName%>";
      var obj = document.getElementById('CardPreview1');   
      var superUser="1";
      var menuPriv="";
      var tablePriv="";
      if(obj==null)
      {
         return false;
      }
      obj.SetSuperUser(superUser);
      obj.SetUserMenuPriv(menuPriv);
      obj.SetUserTablePriv(tablePriv);
      obj.SetURL(aurl);
      obj.SetDBType(DBType);
      obj.SetUserName(UserName);
      obj.SetUserFullName("su");
}	
</script>

<body >
<form name="employPortalForm" method="post" action="/hire/employNetPortal/search_zp_position.do" enctype="multipart/form-data" onsubmit="return validate()">
			<%
			EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
			ArrayList fieldSetList=employPortalForm.getFieldSetList();			
			int index=Integer.parseInt((String)employPortalForm.getCurrentSetID());
			String a0100=employPortalForm.getA0100()==null?"":employPortalForm.getA0100();
			String dbName=employPortalForm.getDbName();
			String writeable=employPortalForm.getWriteable();
            String onlyField=employPortalForm.getOnlyField();
            String isOnlyChecked=employPortalForm.getIsOnlyCheck();
			String emailColumn=employPortalForm.getEmailColumn();
			ArrayList showFieldList=employPortalForm.getShowFieldList();
			ArrayList showFieldDataList=employPortalForm.getShowFieldDataList();
			String isPhoto=employPortalForm.getIsPhoto();
		    String isUpPhoto=employPortalForm.getIsUpPhoto();
		    String isExp=employPortalForm.getIsExp();
		    String isAttach=employPortalForm.getIsAttach();
		    ArrayList uploadFIleList = employPortalForm.getUploadFileList();
            ArrayList mediaList=employPortalForm.getMediaList();
		    String opt=employPortalForm.getOpt();
		    HashMap editableMap=employPortalForm.getEditableMap();
		    String isHaveEditableField=employPortalForm.getIsHaveEditableField();
		    String idItemId="";
		    String blackFieldItem=employPortalForm.getBlackField();
		    String blackNbase=employPortalForm.getBlackNbase();
		    int idIndex=-1;
		    int blackIndex=-1;
		    String answerSet = employPortalForm.getAnswerSet();
		    String onlyName=employPortalForm.getOnlyName()==null?"":employPortalForm.getOnlyName();
		  	String hirechannel=employPortalForm.getHireChannel();
		  	String finished=PubFunc.getReplaceStr2(request.getParameter("finished"));
		    %>
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
<table width='94%'  border="0" cellpadding="0" cellspacing="0">
<tr>
 <%if(a0100==null||a0100.equals("")) {
	out.println("<script type=\"text/javascript\">");
	out.println("employPortalForm.action=\"/hire/employNetPortal/search_zp_position.do?b_query=link&operate=init\";");
    out.println("employPortalForm.submit();");
	out.println("</script>");
	}%>
<html:hidden name="employPortalForm" property="isDefinitionActive"/>
	<td height='37' width='23' class="SearchLeftHead" >&nbsp; </td>
	
	<script language='javascript' >
		var awidth=Math.round(window.screen.width*0.85*0.9);	
		document.write("<td height='37' class='SearchBackColor' width="+awidth+" >");
	</script>
	
		<table  border="0" width='98%' cellpadding="0" cellspacing="0">  
		<tr>
			<td valign="middle" width='13%'class="cx">
			<font class='FontStyle'>&nbsp; <strong>岗位(专业)搜索：</strong></font>
			</td>
			<td valign='middle' width='77%' class="cx">
			<table width='100%' cellpadding="0" cellspacing="0"  border="0"  ><tr>
			<%
			String isQueryCondition=employPortalForm.getIsQueryCondition();
			ArrayList conditionFieldList=employPortalForm.getConditionFieldList();
			for(int i=0;i<conditionFieldList.size();i++)
			{
				out.print("<td width='30%' align='center' nowrap");
				LazyDynaBean abean=(LazyDynaBean)conditionFieldList.get(i);
				String itemid=(String)abean.get("itemid");
				String itemtype=(String)abean.get("itemtype");
				String codesetid=(String)abean.get("codesetid");
				String isMore=(String)abean.get("isMore");
				String itemdesc=(String)abean.get("itemdesc");
				String value=(String)abean.get("value");
				String viewvalue=(String)abean.get("viewvalue");
				value=PubFunc.getReplaceStr2(value);
				viewvalue=PubFunc.getReplaceStr2(viewvalue);
				//if(itemdesc.length()>=5)
				 // j++;
				//if(j!=0&&j%2==0)
				  // out.print("<br>");
				out.print("<font class='FontStyle'>"+itemdesc+":&nbsp;</font>");
				if(itemtype.equals("A"))
				{
					if(codesetid.equals("0"))
					{
						out.println("<input  class='TEXT' type=\"text\" name=\"conditionFieldList["+i+"].value\"  value=\""+value+"\"   size='18'   />");
					}
					else
					{
						if(isMore.equals("0"))
						{
							ArrayList options=(ArrayList)abean.get("options");
							out.print("<select name='conditionFieldList["+i+"].value'  style='width:100;font-size:9pt;color:#666'   ><option value=''>全部</option> ");
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
							out.print("</select>");
						}
						else
						{
							out.println("<input type='hidden' name='conditionFieldList["+i+"].value' value='"+value+"'  />&nbsp;");  
			              	out.print("<input  class='TEXT' type='text' name='conditionFieldList["+i+"].viewvalue' value='"+viewvalue+"'  size='15'   readonly='true'   />");
			             	out.print("<img  src='/images/code.gif' onclick='javascript:openInputCodeDialog(\""+codesetid+"\",\"conditionFieldList["+i+"].viewvalue\");'/>");		
						}
					
					}
				
				}
				else if(itemtype.equals("D"))
				{
					out.println("<input  class='TEXT' type='text'  name='conditionFieldList["+i+"].value'  size='15' value='"+value+"'  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)'/>");
				
				}
				else if(itemtype.equals("N"))
				{
					out.println("<input class='TEXT' type=\"text\" name=\"conditionFieldList["+i+"].value\"   value=\""+value+"\"   size='15'   />");
				}
				out.print("</td>");
			
			
			
			}
			%>
			</tr></table>
			</td>
			<td align='right' height='37' width='10%' class="cx">
			 <input type="button" name="q" id="button" onclick='query();' value="查询" class="hj_zhaopin_list_tab_but"/>
			</td></tr></table>
	</td>
	<td height='37' width='23' class="SearchRightHead">&nbsp;</td>
	</tr>
</table>
<table width='94%' border='0'  class="c_bgn">
<tr height='5'><td>&nbsp;</td></tr>
<tr><td width='15%'  valign='top'>
	<TABLE cellSpacing=0 cellPadding=0 width="150" 
                        align=center border=0 class="cb">
                          <TBODY>
                          <TR>
                            <TD align='center' >
                            
                            			<TABLE cellSpacing=0 cellPadding=0 width="100%" border=0 class='search_w_long'>
						              <TBODY>
						              <tr>
						              <td>
						              <table>
						              <tr>
						              	<td align='center'  class="welcomYouColor">
						              		<IMG height=20 hspace=5 src="/images/group_p.gif" width=20 >
						              		<font class='FontStyle'><bean:message key="hire.welcome.you"/> ${employPortalForm.userName}</font>					    	
						              	</td>
						              	</tr>
						              	</table>
						              	</td>
						              </tr>
						              <tr>
						              	<td align='center' >         		
						              		<table>
						              			<tr><td class='blue12' ><bean:message key="hire.fill.resume"/></td></tr>
						              			<tr><td class='blue12' ><bean:message key="hire.mailing.inteserted"/></td></tr>
						              			<tr><td class='blue12' ><bean:message key="hire.position"/></td></tr>
						              		</table>
						              	</td>
						              </tr>
						              <TR >
						              	<td height=5 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1 src="/images/l_8_T.gif" width=140 > 
						              	</td>
						              </TR>
						              <TR height=10>
						                <TD class=gary12 align=left width="32%">
						                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                  <a href='javascript:resumeBrowse("<%=dbName%>","<%=a0100%>")' />
						                  <IMG border=0 height=10  src="/images/forumme.gif" width=10 >&nbsp;&nbsp;<bean:message key="hire.browse.resume"/>
						                  </a>
						                </TD>
						                </TR>
						                     <logic:equal name="employPortalForm" property="canPrint" value="1">
						                       <TR >
						              	<td style='padding-top:0px'>
						              	 <IMG border= '0' height=1  src="/images/l_8_T.gif" width=140 >
						              	</td>
						              </TR>
						              <TR >
						                <TD align=left width="32%">
						                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                  <a href='javascript:ysmethod("<bean:write name="employPortalForm" property="previewTableId"/>");' />
						                  <IMG border=0 height=10  src="/images/forumme.gif" width=10 >&nbsp;&nbsp;打印简历
						                  </a>
						                </TD>
						                </TR>
		            	      	 		</logic:equal>
		            	      	 		<logic:notEqual value="#" name="employPortalForm" property="admissionCard">
		            	      	 		<logic:notEqual value="" name="employPortalForm" property="admissionCard">
		            	      	 		  <TR >
						              	<td style='padding-top:0px'>
						              	 <IMG border= '0' height=1  src="/images/l_8_T.gif" width=140 >
						              	</td>
						            	  </TR>
						            	  <TR >
						                <TD align=left width="32%">
						                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                  <a href='javascript:ysmethod("<bean:write name="employPortalForm" property="admissionCard"/>");' />
						                  <IMG border=0 height=10  src="/images/forumme.gif" width=10 >&nbsp;&nbsp;打印准考证
						                  </a>
						                </TD>
						                </TR>
						                </logic:notEqual>
		            	      	 		</logic:notEqual>
						              <TR>
						                <td height=5 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1 src="/images/l_8_T.gif" width=140 > 
						              	</td>  
						              </tr>
						              <TR height=10>
						                <TD class=gary12 align=left width="32%">
						                	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                	<a href='/hire/employNetPortal/search_zp_position.do?b_showResumeList=show&setID=0&opt=1'> 
						                	<IMG height=10 border=0 src="/images/forumme.gif" width=10 >&nbsp;&nbsp;
						                	<font color='red'><bean:message key="hire.my.resume"/></font>
						                	</a>
						                	</TD>
						              		
						              <TR>
						                <td height=5 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 > 
						              	</td>  
						              </tr>
						              <TR height=10>
						                <TD class=gary12 align=left width="32%">
						                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                <a href="/hire/employNetPortal/search_zp_position.do?b_applyedPosition=query" >
						                <IMG height=10 border=0  src="/images/forumme.gif" width=10 >&nbsp;
						              
						                  <%if(hirechannel.equals("01")){ %>
						                 <bean:message key="hire.apply.position1"/>
						                <%}else{ %>
						               		<bean:message key="hire.apply.position"/>
						                <%} %>
						                </a>
						                </TD>
						              <TR>
						                <td height=5 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 > 
						              	</td>  
						              </tr>
						              <TR height=10>
						                <TD class=gary12 align=left width="32%">
						                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                <a href='/hire/employNetPortal/search_zp_position.do?br_editPassword=edit' >
						                <IMG height=10 border=0 src="/images/forumme.gif" width=10 >&nbsp;&nbsp;<bean:message key="label.banner.changepwd"/>
						                </a>
						                </TD>
						                </TR>
						                <logic:equal value="1" name="employPortalForm" property="isDefinitionActive">
						                    <TR>
						                <td height=5 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 >
						              	</td>  
						              </tr >
						                 <TR height=10>
						                <TD class=gary12 align=left width="32%">
						                	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                	<a href='javascript:activeResume("<%=dbName%>","<%=a0100%>","${employPortalForm.activeValue}");'> 
						                	<IMG height=10 border=0 src="/images/forumme.gif" width=10 >&nbsp;&nbsp;<logic:equal value="1" name="employPortalForm" property="activeValue">关闭简历</logic:equal><logic:equal value="2" name="employPortalForm" property="activeValue">激活简历	</logic:equal>						                						                							                							                	
						                	</a>
						                	</TD>
						              		</TR>
						                </logic:equal>
						              <TR>
						                <td height=5 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 > 
						              	</td>  
						              </tr>
						              
						              <TR height=10>
						                <TD class=gary12 align=left width="32%" >
						                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                <a href="javascript:exit()" >
						                <IMG height=10 border=0 src="/images/forumme.gif" width=10 >&nbsp;&nbsp;<bean:message key="hire.exit.login"/></a>
						                </TD>
						              	</TR>
						              <TR>
						                <td height=5 class="NavigationMenuSeparator">
						              	  &nbsp;<IMG height=1  src="/images/l_8_T.gif" width=140 > 
						              	</td>  
						              </tr>
						             
						             </TBODY>
						              </TABLE>
                            			
			       			</TD>
			              </TR>
                            </TBODY>
                          </TABLE>

<table>
<tr>
<td>
${employPortalForm.promptContent}
</td>
</tr>
</table>
</td>
<td width='85%' valign='top' >


<table cellSpacing=0 cellPadding=0 width='100%' ><tr>
		<td width='10%' >&nbsp;</td>
		<td align='center' width='90%' valign='top' >	
				 
			<TABLE  cellSpacing=0 cellPadding=0  width='100%' >	
			<tr>
              <td class="zpaboutHJ_mainTD"><font class='FontStyle'>我的简历</font></td>
              </tr>
              <tr height='3'>
              <td>&nbsp;</td>
              </tr>
             <TR>
             <TD align=middle>	
				
                            
                            <TABLE class=border01  cellSpacing=2 cellPadding=2 width="100%"  BORDER=0 >
                            <tr><TD class=RecordRow_net>
                            
                            	<%
                            		String currentName="";
                            		String fieldSetId="";
                            		String answerFlag="0";
                            		boolean openanswer =false;
                            		out.print("<table border='0' width='100%' >");
                            		for(int i=0;i<fieldSetList.size();i++)
                            		{
                            			if(i%5==0)
                            				out.print("<tr>");
                            			LazyDynaBean abean=(LazyDynaBean)fieldSetList.get(i);
                            			String a_fieldSetId=(String)abean.get("fieldSetId");
                            			if(a_fieldSetId.equals("-1"))
                            			{
                            			   if(isAttach.equals("1"))
                            		    	{
                            	    		    out.print("<td align='left'><a href='/hire/employNetPortal/search_zp_position.do?b_showResumeList=show&opt=2&setID="+a_fieldSetId+"'>");
                            			    }
                            			    else
                            			    {
                            			       out.print("<td>&nbsp;");
                            			    }
                            			}
                            			else
                            			{
                            			    out.print("<td align='left'><a href='/hire/employNetPortal/search_zp_position.do?b_showResumeList=show&opt=1&setID="+a_fieldSetId+"'>");
                            			}
                            		if(index==i)
                            			{
                            				out.print("<font color='red'>");
                            				currentName=(String)abean.get("fieldSetDesc");
                            				fieldSetId=a_fieldSetId;
                            				if(answerSet!=null&&answerSet.equalsIgnoreCase(a_fieldSetId))
                            				{
                            					openanswer =true;
                            					answerFlag="1";
                            				}
                            			}
                            			out.print(" <IMG  src='/images/link.gif' border=0 >");
                            			out.print((String)abean.get("fieldSetDesc")+"&nbsp;&nbsp;");
                            			if(index==i)
                            				out.print("</font>");
                            			out.print("</a></td>");
                            			
                            			if(i!=0&&(i+1)%5==0)
                            				out.print("</tr>");
                            		}
                            		if(isAttach.equals("1"))
                            		{ 
                            		    if((fieldSetList.size()+1)%5!=0)
                            		    {  
                            		         out.print("<td align='left'><a href='/hire/employNetPortal/search_zp_position.do?b_showResumeList=show&opt=2&setID=-1'>");
                            		         out.print("<IMG  src='/images/link.gif' border=0 >");
                            		         if(index==-1)
                            		            out.print("<font color='red'>");
                            		         out.print(ResourceFactory.getProperty("hire.resume.attach"));
                            		         if(index==-1)
                            		            out.print("</font");
                            		         out.print("</a></td>");
                            	    		out.print("</tr>");
                            	        }
                            	        else
                            	        {     
                            	             out.print("</tr>");
                            	             out.print("<tr>");
                            	             out.print("<td align='left'><a href='/hire/employNetPortal/search_zp_position.do?b_showResumeList=show&opt=2&setID=-1'>");
                            		         out.print("<IMG  src='/images/link.gif' border=0 >");
                            		         if(index==-1)
                            		            out.print("<font color='red'>");
                            		         out.print(ResourceFactory.getProperty("hire.resume.attach"));
                            		         if(index==-1)
                            		            out.print("</font");
                            		         out.print("</a></td>");
                            	    		 out.print("</tr>");
                            	        }
                            		}
                            		else{
                            		    if(fieldSetList.size()%5!=0)
                            		    	out.print("</tr>");
                            		}
                            		
                            		
                            		out.print("</table>");
                            	%>
                            	
                            	</TD></TR></TABLE><BR>
                              
          <% if(opt.equals("1")){ %>
                        <TR>
                            <TD align='left'> <font class='FontStyle'><strong>  
                         <%=currentName%>
                          <%if(!fieldSetId.equalsIgnoreCase("A01")&&!openanswer){ %>
                         &nbsp;&nbsp;<bean:message key="hire.fill.timeseq"/>
                         <% } %></strong>
                         </font>
                         <Br>
                         	</td>
                        </TR>
                        <%if(openanswer){ %>
                        
                         <tr><td>
                        <TABLE class=border01 cellSpacing=1 cellPadding=2 
                        width=100% border=0>
                          <TBODY>
                          <%
                          ArrayList  resumeFieldList=employPortalForm.getResumeFieldList();
                          for(int i=0;i<resumeFieldList.size();i++)
                          {
                          	LazyDynaBean abean=(LazyDynaBean)resumeFieldList.get(i);
                          	String itemid=(String)abean.get("itemid");
							String itemtype=(String)abean.get("itemtype");
							String codesetid=(String)abean.get("codesetid");
							String isMore=(String)abean.get("isMore");
							String itemdesc=(String)abean.get("itemdesc");
							String value=(String)abean.get("value");
							String itemlength=(String)abean.get("itemlength");
							String decimalwidth=(String)abean.get("decimalwidth");
							if(blackFieldItem!=null&&blackFieldItem.equalsIgnoreCase(itemid))
							{
							   blackIndex=i;
							}
							if(decimalwidth==null||decimalwidth.equals(""))
							   decimalwidth="0";
							 int deci=Integer.parseInt(decimalwidth);
							 int totallength=Integer.parseInt(itemlength)+(deci>0?(1+deci):deci);
							String viewvalue=(String)abean.get("viewvalue");
							String must=(String)abean.get("must");   //是否为必填项 1：是 0：否
							String isseqn=(String)abean.get("isseqn");
							String itemmemo=(String)abean.get("itemmemo");
							if(i!=0)
							itemmemo="<br>"+itemmemo;
							itemmemo = itemmemo.replace("\r\n","<br>");
							if(itemdesc.length()==2)
								itemdesc=itemdesc.charAt(0)+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+itemdesc.charAt(1);
							if(itemdesc.length()==3)
							    itemdesc=itemdesc.charAt(0)+"&nbsp;&nbsp;"+itemdesc.charAt(1)+"&nbsp;"+itemdesc.charAt(2);
                         
                          %>
                          
                          
                          <TR>
                            <TD class=tdTitle  style="TEXT-ALIGN: left;" ><font class="fieldDescriptionColor" id="<%=itemid+"memo"%>"><%=itemmemo%></font></TD>
                            </TR>
                            <TR>
                            <TD class=tdValue>
                          
                            
                            <%
                            if(itemtype.equals("A"))
							{
								if(codesetid.equals("0"))
								{
									
									out.println("<input type=\"text\"  class=textbox ");
									if(itemdesc.equalsIgnoreCase("身份证号")||itemdesc.equalsIgnoreCase("身份证号码"))
									{
										idIndex=i;
										idItemId=itemid;
									}
									if(blackFieldItem.equalsIgnoreCase(itemid)||onlyName.equalsIgnoreCase(itemid))
									{
									  out.print(" readOnly ");
									}
									else if(itemid.equalsIgnoreCase(emailColumn))
									{
									   out.print(" readOnly ");
									}
									else if(fieldSetId.equalsIgnoreCase("A01")&&writeable.equals("1"))
								    {
								        if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null&isseqn.equals("0"))
							             	out.print("");
							            else
							                out.print(" readOnly ");
							         }
							        else
							        {
							           if(isseqn.equals("0"))
							               out.print("");
							           else
							               out.print(" readOnly ");
							        }
									out.print(" name=\"resumeFieldList["+i+"].value\"   value=\""+value+"\"  />&nbsp;");
								}
								else
								{
									if(isMore.equals("0"))
									{
										
										out.print("<select  style='border:1px solid #D3D3D3' name='resumeFieldList["+i+"].value'");
										if(fieldSetId.equalsIgnoreCase("A01")&&writeable.equals("1"))
								       {
								         if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null&isseqn.equals("0"))
							             	 out.print("");
							             else
							                 out.print(" disabled ");
							          }
							         else
							         {
							            if(isseqn.equals("0"))
							                out.print("");
							            else
							               out.print(" disabled ");
							         }
										
										
										out.print("><option value=''></option> ");
										if(abean.get("options")!=null){
											ArrayList options=(ArrayList)abean.get("options");
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
										out.print("</select>&nbsp;");
									}
									else
									{
										 
						              	out.print("<table  cellSpacing=0 cellPadding=0 border=0  ><tr><td>");
						              	out.print(" <input type='text' name='resumeFieldList["+i+"].viewvalue'  class=textbox value='"+viewvalue+"' readonly /></td>");
						              	
						             	out.print("<td>");
						             	if(fieldSetId.equalsIgnoreCase("A01")&&writeable.equals("1"))
						             	{
						             	    if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null)
						             	    {
						             	      if(!isseqn.equals("1"))
						                         	out.print("<a href='javascript:openInputCodeDialog(\""+codesetid+"\",\"resumeFieldList["+i+"].viewvalue\");' >   ");
						                     }
						                }
						                else
						                {
						                    if(!isseqn.equals("1"))
						                        out.print("<a href='javascript:openInputCodeDialog(\""+codesetid+"\",\"resumeFieldList["+i+"].viewvalue\");' >   ");
						                }
						             	out.print("<img  src='/images/overview_obj.gif' border=0 width=20 height=20 />");
						             	if(fieldSetId.equalsIgnoreCase("A01")&&writeable.equals("1"))
						             	{
						                 	if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null)
						             	       out.print("</a>");
						             	}
						             	else
						             	{
						             	    out.print("</a>");
						             	}
						             	out.print("&nbsp;</td>");		
										out.print("<td valign='bottom' ><input type='hidden' value='"+value+"'  name='resumeFieldList["+i+"].value' /> &nbsp;");
										if(must.equals("1")||itemdesc.equals("身份证号")||itemdesc.equalsIgnoreCase("身份证号码"))
											out.println("<FONT color=red>* </FONT>");
									    out.print(" </td></tr></table>"); 
									}
								
								}
							
							}
							else if(itemtype.equals("D"))
							{
								out.print("<input type='text'  name='resumeFieldList["+i+"].value'  class=textbox value='"+value+"'  ");
								if(fieldSetId.equalsIgnoreCase("A01")&&writeable.equals("1"))
								{
								 if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null&isseqn.equals("0"))
							    	out.print("onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' ");
							     else
							        out.print(" readOnly ");
							    }
							    else
							    {
							       if(isseqn.equals("0"))
							          out.print("onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' ");
							       else
							          out.print(" readOnly ");
							    }
								out.println(" />&nbsp;");
							
							}
							else if(itemtype.equals("N"))
							{
								out.print("<input type=\"text\" name=\"resumeFieldList["+i+"].value\"   value='"+value+"'   class=textbox  maxlength='"+totallength+"' ");
								if(fieldSetId.equalsIgnoreCase("A01")&&writeable.equals("1"))
								    {
								        if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null&isseqn.equals("0"))
							             	out.print("");
							            else
							                out.print(" readOnly ");
							         }
							        else
							        {
							           if(isseqn.equals("0"))
							               out.print("");
							           else
							               out.print(" readOnly ");
							        }
		
								out.println("/>&nbsp;");
							}
							else if(itemtype.equals("M"))
							{
								out.print("<textarea name=\"resumeFieldList["+i+"].value\" rows='10'   wrap='OFF'  class='textboxMul' style='WIDTH: 500px;'" );
								if(fieldSetId.equalsIgnoreCase("A01")&&writeable.equals("1"))
								    {
								        if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null&isseqn.equals("0"))
							             	out.print("");
							            else
							                out.print(" readOnly ");
							         }
							        else
							        {
							           if(isseqn.equals("0"))
							               out.print("");
							           else
							               out.print(" readOnly ");
							        }
								out.println(">"+value+"</textarea>&nbsp;");
							}
							
							if(isMore.equals("0")&&must.equals("1"))
								out.print("&nbsp;<FONT color=red>* </FONT>");
							 //if(isExp.equals("1"))
								//out.print("</td><td ><font color='#535455'> "+itemmemo+"</font></td></tr></table>");
							//out.print("</td></tr></table>");
						%>
                            </TD>
                          </TR>
                          
                          <%
                          
                          }
                          if(fieldSetId.equalsIgnoreCase("A01"))
                          {
                          %>
                          
                          <TR>
                            <TD class=tdTitle valign='bottom'
                              width="30%"><font class="fieldDescriptionColor"><bean:message key="hire.cloumn.photo"/>：</font></TD>
                            <TD class=tdValue   >
                            
                            	 <font color='red'>
                            	  <% if(isUpPhoto.equals("1")){ %>
                            	 <bean:message key="hire.mustupload.photo"/>
                            	 <%} else
                            	 {%>
                            	 <bean:message key="hire.upload.photo"/>
                            	 <%} %>
                            	 </font><br>
                         		 <input name="file" type="file" size="35" style="border-top:1px solid #d0d0d0;border-left:1px solid #d0d0d0;border-right:1px solid #d0d0d0;border-bottom:1px solid #d0d0d0;">&nbsp;
                         		 <% if(isUpPhoto.equals("1")){ %>
                         		 <FONT color=red>* </FONT>
                         		 <% } %>
                          	</TD>
                          </TR>
                          <% } %>       
                          </TBODY></TABLE>
                          <br>
                        <TABLE id=Table2 cellSpacing=0 cellPadding=0 
                        width="100%" border=0>
                          <TBODY>
                          <TR> <TD style="TEXT-ALIGN:center">
                          <%
                          if(!fieldSetId.equalsIgnoreCase("A01")){ %>
                           
                            <a href='javascript:sub(4,"<%=answerFlag%>")' >
                             <IMG  src="/images/upstep.gif" border=0 >
                            </a>
                         
                          <% } 
                          	if((index)!=fieldSetList.size())
                          	{
                          %>
                         
                            <%if(!fieldSetId.equalsIgnoreCase("A01")){ %>
                           <logic:equal value="0" name="employPortalForm" property="writeable">
                           &nbsp;&nbsp;&nbsp;&nbsp;
                            <a href='javascript:sub(2,"<%=answerFlag%>")' >
                            <IMG  src="/images/next.gif" border=0 >
                            </a>
                            &nbsp;&nbsp;&nbsp;&nbsp;
                            </logic:equal>
                            <%}else{ %>
                             <logic:equal value="0" name="employPortalForm" property="writeable">
                                <a href='javascript:sub(2,"<%=answerFlag%>")' >
                            <IMG  src="/images/next.gif" border=0 >
                            </a>
                            &nbsp;&nbsp;&nbsp;&nbsp;
                            </logic:equal>
                            <%} %>
                            
                            <a href='javascript:sub(3,"<%=answerFlag%>")' >
                            <IMG  src="/images/nextstep.gif" border=0 >
                            </a>
                           <%
                           }
                           if(index==(fieldSetList.size()-1))
                           {
                           if(!isAttach.equals("1")){
                           %>
                            <logic:equal value="0" name="employPortalForm" property="writeable">
                             <a href='javascript:sub(0,"<%=answerFlag%>")' >
                            <IMG  src="/images/add3.gif" border=0 >
                            </a>
                             </logic:equal>
                           <%
                           } }%>
                            </TD>
                            </TR>
                            </TBODY></TABLE>
                        <%}else{ %>
                      <%if(!fieldSetId.equalsIgnoreCase("A01"))
                          { %>
                        <TR><TD align='center'>  	
                      <TABLE class="hj_zhaopin_list_tab_title" id=rptb cellSpacing=0 
                        cellPadding=0 width="100%" align=center  border=0>
                          <TBODY>
                          <TR>
                          <!-- 
                          <TD class=rptHead 
                            background=/images/r_titbg01.gif>编号 </TD>
                           -->
                           <% int xx=0; %>
                           <logic:iterate id="element" name="employPortalForm" property="showFieldList"  offset="0"> 
                            <Th><bean:write name="element" property="itemdesc" /></Th>
                           <% xx++; %>
                           </logic:iterate>
                            <logic:equal value="0" name="employPortalForm" property="writeable">
                            <% if(xx>0){ %>
                           <Th ><bean:message key="system.infor.oper"/></Th>
                           <%} %>
                            </logic:equal>
						  </TR>
                          <%
                          for(int i=0;i<showFieldDataList.size();i++)
                          {
                          		LazyDynaBean abean=(LazyDynaBean)showFieldDataList.get(i);
                          		String i9999=(String)abean.get("i9999");
                          		//out.println("<tr><TD class=rptItem>"+i9999+"</TD>");
                          		for(int n=0;n<showFieldList.size();n++)
                          		{
                          			LazyDynaBean a_bean=(LazyDynaBean)showFieldList.get(n);
                          			String itemid=(String)a_bean.get("itemid");
                          			String value=(String)abean.get(itemid);
                          			if(value.equals(""))
                          			    value="&nbsp;";
                          			out.print("<TD >"+value+"</TD>");
                          		}
                          		if(writeable.equals("0"))
                          		{
                          	    	out.println("<TD><A href='/hire/employNetPortal/search_zp_position.do?b_showResumeList=show&setID="+fieldSetId+"&userid="+a0100+"&i9999="+i9999+"'>"+ResourceFactory.getProperty("approve.approve.d")+"</A>");
                          	     	out.print("/ <A href='javascript:deleteRecord(\""+i9999+"\")'>"+ResourceFactory.getProperty("kq.shift.cycle.del")+"</A></TD>");
                          	    }
                          		out.print("</TR>");
                          }
                          %>
                          </TBODY>
                         </TABLE>
                        <br>
                        </TD></TR>
                        <% } %>
                        
                         <tr><td>
                        <TABLE class=border01 cellSpacing=1 cellPadding=2 
                        width=100% border=0>
                          <TBODY>
                          <%
                          ArrayList  resumeFieldList=employPortalForm.getResumeFieldList();
                          for(int i=0;i<resumeFieldList.size();i++)
                          {
                          	LazyDynaBean abean=(LazyDynaBean)resumeFieldList.get(i);
                          	String itemid=(String)abean.get("itemid");
							String itemtype=(String)abean.get("itemtype");
							String codesetid=(String)abean.get("codesetid");
							String isMore=(String)abean.get("isMore");
							String itemdesc=(String)abean.get("itemdesc");
							String value=(String)abean.get("value");
							String itemlength=(String)abean.get("itemlength");
							String decimalwidth=(String)abean.get("decimalwidth");
							if(blackFieldItem!=null&&blackFieldItem.equalsIgnoreCase(itemid))
							{
							   blackIndex=i;
							}
							if(decimalwidth==null||decimalwidth.equals(""))
							   decimalwidth="0";
							 int deci=Integer.parseInt(decimalwidth);
							 int totallength=Integer.parseInt(itemlength)+(deci>0?(1+deci):deci);
							String viewvalue=(String)abean.get("viewvalue");
							String must=(String)abean.get("must");   //是否为必填项 1：是 0：否
							String isseqn=(String)abean.get("isseqn");
							String itemmemo=(String)abean.get("itemmemo");
							if(itemdesc.length()==2)
								itemdesc=itemdesc.charAt(0)+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+itemdesc.charAt(1);
							if(itemdesc.length()==3)
							    itemdesc=itemdesc.charAt(0)+"&nbsp;&nbsp;"+itemdesc.charAt(1)+"&nbsp;"+itemdesc.charAt(2);
                         
                          %>
                          
                          
                          <TR>
                            <TD class=tdTitle  width="30%"><font class="fieldDescriptionColor"><%=itemdesc%>：</font></TD>
                            <TD class=tdValue>
                            <% if(isExp.equals("1")){ %>
                            <table><tr><td >
                            
                            <%
                            }
                            if(itemtype.equals("A"))
							{
								if(codesetid.equals("0"))
								{
									
									out.println("<input type=\"text\"  class=textbox ");
									if(itemdesc.equalsIgnoreCase("身份证号")||itemdesc.equalsIgnoreCase("身份证号码"))
									{
										idIndex=i;
										idItemId=itemid;
									}
									if(blackFieldItem.equalsIgnoreCase(itemid)||onlyName.equalsIgnoreCase(itemid))
									{
									  out.print(" readOnly ");
									}
									else if(itemid.equalsIgnoreCase(emailColumn))
									{
									   out.print(" readOnly ");
									}
									else if(fieldSetId.equalsIgnoreCase("A01")&&writeable.equals("1"))
								    {
								        if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null&isseqn.equals("0"))
							             	out.print("");
							            else
							                out.print(" readOnly ");
							         }
							        else
							        {
							           if(isseqn.equals("0"))
							               out.print("");
							           else
							               out.print(" readOnly ");
							        }
									out.print(" name=\"resumeFieldList["+i+"].value\"   value=\""+value+"\"  />&nbsp;");
								}
								else
								{
									if(isMore.equals("0"))
									{
										
										out.print("<select  style='border:1px solid #D3D3D3' name='resumeFieldList["+i+"].value'");
										if(fieldSetId.equalsIgnoreCase("A01")&&writeable.equals("1"))
								       {
								         if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null&isseqn.equals("0"))
							             	 out.print("");
							             else
							                 out.print(" disabled ");
							          }
							         else
							         {
							            if(isseqn.equals("0"))
							                out.print("");
							            else
							               out.print(" disabled ");
							         }
										
										
										out.print("><option value=''></option> ");
										if(abean.get("options")!=null){
											ArrayList options=(ArrayList)abean.get("options");
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
										out.print("</select>&nbsp;");
									}
									else
									{
										 
						              	out.print("<table  cellSpacing=0 cellPadding=0 border=0  ><tr><td>");
						              	out.print(" <input type='text' name='resumeFieldList["+i+"].viewvalue'  class=textbox value='"+viewvalue+"' readonly /></td>");
						              	
						             	out.print("<td>");
						             	if(fieldSetId.equalsIgnoreCase("A01")&&writeable.equals("1"))
						             	{
						             	    if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null)
						             	    {
						             	      if(!isseqn.equals("1"))
						                         	out.print("<a href='javascript:openInputCodeDialog(\""+codesetid+"\",\"resumeFieldList["+i+"].viewvalue\");' >   ");
						                     }
						                }
						                else
						                {
						                    if(!isseqn.equals("1"))
						                        out.print("<a href='javascript:openInputCodeDialog(\""+codesetid+"\",\"resumeFieldList["+i+"].viewvalue\");' >   ");
						                }
						             	out.print("<img  src='/images/overview_obj.gif' border=0 width=20 height=20 />");
						             	if(fieldSetId.equalsIgnoreCase("A01")&&writeable.equals("1"))
						             	{
						                 	if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null)
						             	       out.print("</a>");
						             	}
						             	else
						             	{
						             	    out.print("</a>");
						             	}
						             	out.print("&nbsp;</td>");		
										out.print("<td valign='bottom' ><input type='hidden' value='"+value+"'  name='resumeFieldList["+i+"].value' /> &nbsp;");
										if(must.equals("1")||itemdesc.equals("身份证号")||itemdesc.equalsIgnoreCase("身份证号码"))
											out.println("<FONT color=red>* </FONT>");
									    out.print(" </td></tr></table>"); 
									}
								
								}
							
							}
							else if(itemtype.equals("D"))
							{
								out.print("<input type='text'  name='resumeFieldList["+i+"].value'  class=textbox value='"+value+"'  ");
								if(fieldSetId.equalsIgnoreCase("A01")&&writeable.equals("1"))
								{
								 if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null&isseqn.equals("0"))
							    	out.print("onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' ");
							     else
							        out.print(" readOnly ");
							    }
							    else
							    {
							       if(isseqn.equals("0"))
							          out.print("onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' ");
							       else
							          out.print(" readOnly ");
							    }
								out.println(" />&nbsp;");
							
							}
							else if(itemtype.equals("N"))
							{
								out.print("<input type=\"text\" name=\"resumeFieldList["+i+"].value\"   value='"+value+"'   class=textbox  maxlength='"+totallength+"' ");
								if(fieldSetId.equalsIgnoreCase("A01")&&writeable.equals("1"))
								    {
								        if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null&isseqn.equals("0"))
							             	out.print("");
							            else
							                out.print(" readOnly ");
							         }
							        else
							        {
							           if(isseqn.equals("0"))
							               out.print("");
							           else
							               out.print(" readOnly ");
							        }
		
								out.println("/>&nbsp;");
							}
							else if(itemtype.equals("M"))
							{
								out.print("<textarea name=\"resumeFieldList["+i+"].value\" rows='10'  wrap='OFF'  class='textboxMul'");
								if(fieldSetId.equalsIgnoreCase("A01")&&writeable.equals("1"))
								    {
								        if(isHaveEditableField.equals("1")&&editableMap.get(itemid.toLowerCase())!=null&isseqn.equals("0"))
							             	out.print("");
							            else
							                out.print(" readOnly ");
							         }
							        else
							        {
							           if(isseqn.equals("0"))
							               out.print("");
							           else
							               out.print(" readOnly ");
							        }
								out.println(">"+value+"</textarea>&nbsp;");
							}
							
							if(isMore.equals("0")&&must.equals("1"))
								out.print("&nbsp;<FONT color=red>* </FONT>");
							 if(isExp.equals("1"))
								out.print("</td><td ><font color='#535455'> "+itemmemo+"</font></td></tr></table>");
						%>
                            </TD>
                          </TR>
                          
                          <%
                          
                          }
                          if(fieldSetId.equalsIgnoreCase("A01"))
                          {
                          %>
                          
                          <TR>
                            <TD class=tdTitle valign='bottom'
                              width="30%"><font class="fieldDescriptionColor"><bean:message key="hire.cloumn.photo"/>：</font></TD>
                            <TD class=tdValue   >
                            
                            	 <font color='red'>
                            	  <% if(isUpPhoto.equals("1")){ %>
                            	 <bean:message key="hire.mustupload.photo"/>
                            	 <%} else
                            	 {%>
                            	 <bean:message key="hire.upload.photo"/>
                            	 <%} %>
                            	 </font><br>
                         		 <input name="file" type="file" size="35" style="border-top:1px solid #d0d0d0;border-left:1px solid #d0d0d0;border-right:1px solid #d0d0d0;border-bottom:1px solid #d0d0d0;">&nbsp;
                         		 <% if(isUpPhoto.equals("1")){ %>
                         		 <FONT color=red>* </FONT>
                         		 <% } %>
                          	</TD>
                          </TR>
                          <% } %>       
                          </TBODY></TABLE>
                          <br>
                        <TABLE id=Table2 cellSpacing=0 cellPadding=0 
                        width="100%" border=0>
                          <TBODY>
                          <TR> <TD style="TEXT-ALIGN:center">
                          <%
                          if(!fieldSetId.equalsIgnoreCase("A01")){ %>
                           
                            <a href='javascript:sub(4,"<%=answerFlag%>")' >
                             <IMG  src="/images/upstep.gif" border=0 >
                            </a>
                            &nbsp;&nbsp;&nbsp;&nbsp;
                           <logic:equal value="0" name="employPortalForm" property="writeable">
                            <a href='javascript:sub(1,"<%=answerFlag%>")' >
                            <IMG  src="/images/add2.gif" border=0 >
                            </a>
                            &nbsp;&nbsp;&nbsp;&nbsp;
                            </logic:equal>
                          <% } 
                          	if((index+1)!=fieldSetList.size())
                          	{
                          %>
                         
                            <%if(!fieldSetId.equalsIgnoreCase("A01")){ %>
                           <logic:equal value="0" name="employPortalForm" property="writeable">
                            <a href='javascript:sub(2,"<%=answerFlag%>")' >
                            <IMG  src="/images/next.gif" border=0 >
                            </a>
                            &nbsp;&nbsp;&nbsp;&nbsp;
                            </logic:equal>
                            <%}else{ %>
                             <logic:equal value="0" name="employPortalForm" property="writeable">
                                <a href='javascript:sub(2,"<%=answerFlag%>")' >
                            <IMG  src="/images/next.gif" border=0 >
                            </a>
                            &nbsp;&nbsp;&nbsp;&nbsp;
                            </logic:equal>
                            <%} %>
                            <a href='javascript:sub(3,"<%=answerFlag%>")' >
                            <IMG  src="/images/nextstep.gif" border=0 >
                            </a>
                           <%
                           }
                           if(index==(fieldSetList.size()-1))
                           {
                             if(!isAttach.equals("1")){
                           %>
                            <logic:equal value="0" name="employPortalForm" property="writeable">
                             <a href='javascript:sub(0,"<%=answerFlag%>")' >
                            <IMG  src="/images/add3.gif" border=0 >
                            </a>
                             </logic:equal>
                           <%
                           }else{%>
                           <a href='javascript:sub(2,"<%=answerFlag%>")' >
                            <IMG  src="/images/nextstep.gif" border=0 >
                            </a>
                            <logic:equal value="0" name="employPortalForm" property="writeable">
                            &nbsp;&nbsp;
                           <a href='javascript:sub(3,"<%=answerFlag%>")' >
                            <IMG  src="/images/next.gif" border=0 >
                            </a>
                            </logic:equal>
                           <%}} %>
                            </TD>
                            </TR>
                            </TBODY></TABLE>
                            
                            <%}                            
                            }else
                            {//简历附件
                            %>
                            <tr>
                          <TD colspan="4" height="25" style="hieght:11px; font-size:12px;*padding-top:4px;font-weight:bold; color:#1E1E1E;"><bean:message key="hire.resume.attach"/></TD>         
                          </tr>
                                
                           <tr>
                           <td> 
                            	<TABLE align="center" cellSpacing=0 cellPadding=0 width=100% border=0 class="hj_zhaopin_list_tab_title">                       
                          	 		     
			                      <tr  align='center' > 
			                        <th nowrap>
			                      		<bean:message key="conlumn.mediainfo.info_id"/>          			
			                      	</th>
			                     	<th nowrap>
			                      		<bean:message key="column.law_base.filename"/>         			
			                      	</th>
			                      	 <logic:equal value="0" name="employPortalForm" property="writeable">
			                          <th nowrap>
			                      		<bean:message key="lable.tz_template.delete"/>         			
			                      	</th>
			                      	</logic:equal>
			                      </tr>

			                      	<%
			                      	for(int i=0;i<uploadFIleList.size();i++)
			                      	{
			                      	 LazyDynaBean abean = (LazyDynaBean)uploadFIleList.get(i);
			                      	 out.println("<tr>");
			                      	
			                      	out.println("<td align='right' height='25'  >"+(String)abean.get("seq")+"</td>");
			                      	out.println("<td  nowrap >"+(String)abean.get("title")+"</td>");
			                      	if(writeable.equals("0"))
			                      	{
			                         	out.println("<td align='center' ><img src='/images/delete.gif' border='0' style='cursor:hand' onclick=\"deleteattach('"+(String)abean.get("a0100")+"','"+(String)abean.get("i9999")+"','"+(String)abean.get("nbase")+"')\"/></td>");
			                        }
			                      	out.println("</tr>"); 
			                      	}
			                      	 %>
			                      	 </table><br>
			                      	 <table align="center" cellSpacing=0 cellPadding=0 width=100% border=0 class="hj_zhaopin_list_tab_title">
                      				<TR>                           
                           			  <TD   align="center" colspan=3>
                         		 		<input style="height:20px;border-top:1px solid #d0d0d0;border-left:1px solid #d0d0d0;border-right:1px solid #d0d0d0;border-bottom:1px solid #d0d0d0;" id="fff" name='attachFile' type="file" size=50>
                      				 </TD>
                      				
                          		  </TR> 
                          		  <tr>
                          		  </tr>  
                          		   <td  valign="bottom" align="center" colspan=3>
                       				   <logic:equal value="0" name="employPortalForm" property="writeable">
                          				<img src="\images\upload.gif" border=0 onclick="upload('0');" style="cursor:hand"/>
                          					&nbsp;&nbsp;                        
                            			<a href="javascript:upload('1');" >
                            			 <IMG  src="/images/zp_upload_finish1.png" border=0 >
                            				</a>
                             
                         				</logic:equal>
                           			</td> 
                          		</TABLE>                           
                          	 </td>
                           </tr>
                            <%} %>
                            
				<td></td></tr>
				</TABLE>
		</td></tr></table>


</td></tr></table>
<Input type="hidden" name='i9999' value="${employPortalForm.i9999}" /> 
<Input type='hidden' name='flag' value='1' />
<html:hidden name="employPortalForm" property="writeable"/>
<html:hidden name="employPortalForm" property="onlyField"/>
<html:hidden name="employPortalForm" property="isOnlyCheck"/>
<html:hidden name="employPortalForm" property="blackField"/>
<html:hidden name="employPortalForm" property="hireChannel"/>
</form>
<!-- zxj 不再使用插件
<OBJECT
	  id="FileView"
	  classid="clsid:152FC577-6940-4B1E-99BB-D4D5B8BF182E"
      codebase="/cs_deploy/FileViewerX.cab#version=1,0,0,4"
	  width=0
	  height=0
	  align=center
	  hspace=0
	  vspace=0
>
</OBJECT>
-->

<script language='javascript' >	
	
	


	function sub2(flag)
	{
	
	   
	    var bool=true;
		<%
		if(fieldSetId.equalsIgnoreCase("A01"))
		{
			if(isPhoto.equals("0")&&isUpPhoto.equals("1"))
			{
			%>
			if(flag!='3'&&flag!='4'&&trim(document.employPortalForm.file.value).length==0)
			{
				alert(PLEASE_UPLOAD_PHOTO+"！");
				return;
			}
			if(flag!='3'&&flag!='4'&&trim(document.employPortalForm.file.value).length!=0)
			{
				
				
				if(!validateUploadFilePath(document.employPortalForm.file.value)){//文件上传漏洞
					alert("上传文件为不符合要求！请选择正确的文件上传！");
					return;
				}
				
			}
			<%
			}
		
		%>
		var filePath = document.employPortalForm.file.value;
		if(trim(filePath).length!=0)
		{
			var extendFile=trim(filePath.substring(filePath.indexOf(".")+1,filePath.length));
			while(extendFile.indexOf(".")!=-1)
			{
			      extendFile=trim(extendFile.substring(extendFile.indexOf(".")+1,extendFile.length));
			}
			if(extendFile.toLowerCase()!='jpg'&&extendFile.toLowerCase()!='gif'&&extendFile.toLowerCase()!='bmp'&&extendFile.toLowerCase()!='jpeg')
			{
				alert(UPLOAD_FILE_FORMAT_MUST+"!");
				return;
			}
			var  obj=document.getElementById('FileView'); 
             if (obj != null)
             {
                obj.SetFileName(filePath);
                var facSize=obj.GetFileSize(); 
                if(parseInt(facSize)==-1)   
                {
                   alert("文件不存在，请输入正确的文件路径！");
                   return;
                }              
                var  photo_maxsize="512";   
                if(parseInt(photo_maxsize,10)>0&&parseInt(photo_maxsize,10)<parseInt(facSize,10)/1024)
                {  
                   
                   alert("上传文件请控制在512KB以下！");
                   return;
                }     
                if(!validateUploadFilePath(filePath)){//文件上传漏洞
					alert("上传文件为不符合要求！请选择正确的文件上传！");
					return;
				}   
             }
		}
		<%
		}
		if(idIndex==-1)
		{
		     if(blackIndex!=-1)
		     {
		       %>
		       
		        bool=false;
		        var hashvo=new ParameterSet();
				hashvo.setValue("type","2");
				hashvo.setValue('a0100',"${employPortalForm.a0100}");
				hashvo.setValue("blackFieldItem","<%=blackFieldItem%>");
				hashvo.setValue("blackNbase","<%=blackNbase%>");
				if(blackFieldValue==null||blackFieldValue=='')
				    blackFieldValue=document.getElementsByName("resumeFieldList[<%=blackIndex%>].value")[0].value;
				hashvo.setValue("blackFieldValue",getEncodeStr(blackFieldValue));
				blackFieldValue="";
				var In_paramters="dbname=${employPortalForm.dbName}";  
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000171'},hashvo);
				return;
		       <%
		     }
		      if(isAttach.equals("1"))
		     {
		        
		    	 if(index==(fieldSetList.size()-1))
		    	 {
			 	%>
			 	
			 	  if(flag=='0')
			 	  {
			 	     employPortalForm.action='/hire/employNetPortal/search_zp_position.do?b_addeInfo=add&finished=1';
			 	  }
			 	  else
			 	  {
			 	     employPortalForm.action='/hire/employNetPortal/search_zp_position.do?b_addInfo=add&flag='+flag;
			 	  }
			 	<%
		    	 } else
			     {
			     %>
			    	employPortalForm.action='/hire/employNetPortal/search_zp_position.do?b_addInfo=add&flag='+flag;	 
			    <%
			     }
		      }
			 else
			 {
			    if(index==(fieldSetList.size()-1))
		    	 {
		    	 %>
		    	 
		    	   if(flag=='0')
			 	  {
			 	     employPortalForm.action='/hire/employNetPortal/search_zp_position.do?b_addeInfo=add&finished=1';
			 	  }
			 	  else
			 	  {
			 	     employPortalForm.action='/hire/employNetPortal/search_zp_position.do?b_addInfo=add&flag='+flag;
			 	  }
		    	 <%
		    	 }
		    	 else
		    	 {
		    	 %>
		    		employPortalForm.action='/hire/employNetPortal/search_zp_position.do?b_addInfo=add&flag='+flag;	 
			    <%
			      }
			 }
			 %>
			 if(bool)
		      	 employPortalForm.submit();
		<%
		}
		else if(isOnlyChecked.equals("0")||onlyField.length()<=0)
		{
		%>
		
			var a<%=idIndex%>=document.getElementsByName("resumeFieldList[<%=idIndex%>].value");
			var len=a<%=idIndex%>[0].value.replace(/[^\x00-\xff]/g,"**").length;
			
			if(len>0)
			{
				if(flag!='3'&&flag!='4'&&len!=13&&len!=15&&len!=18)
				{
					alert(IDCARD_FILL_NOT_RIGHT+"!");
					return;
				}
				var hashvo=new ParameterSet();
				hashvo.setValue("type","0");
				hashvo.setValue("idValue",a<%=idIndex%>[0].value);
				hashvo.setValue("idItem",'<%=idItemId%>');			
				hashvo.setValue('a0100',"${employPortalForm.a0100}");
				hashvo.setValue("blackFieldItem","<%=blackFieldItem%>");
				hashvo.setValue("blackNbase","<%=blackNbase%>");
				if(blackFieldValue==null||blackFieldValue=='')
				    blackFieldValue=document.getElementsByName("resumeFieldList[<%=blackIndex%>].value")[0]==null?"":document.getElementsByName("resumeFieldList[<%=blackIndex%>].value")[0].value;
				hashvo.setValue("blackFieldValue",getEncodeStr(blackFieldValue));
				blackFieldValue="";
				var In_paramters="dbname=${employPortalForm.dbName}";  
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000171'},hashvo);
				return;
			}
			else if(flag!='3'&&flag!='4')
			{
				alert(IDCARD_MUST_FILL+"！");
				return;
			}
		
		<%
		}
		if(fieldSetId.equalsIgnoreCase("A01"))
		{
		if((isOnlyChecked.equals("1")&&onlyField.length()>0))
		{
		%>
		        var hashvo=new ParameterSet();
				hashvo.setValue("type","1");
				hashvo.setValue('a0100',"${employPortalForm.a0100}");
				hashvo.setValue("only_str",getEncodeStr(only_str));
				hashvo.setValue("blackFieldItem","<%=blackFieldItem%>");
				hashvo.setValue("blackNbase","<%=blackNbase%>");
				hashvo.setValue("blackFieldValue",getEncodeStr(blackFieldValue));
				only_str="";
				blackFieldValue="";
				var In_paramters="dbname=${employPortalForm.dbName}";  
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000171'},hashvo);
				return;
		<%}
		else
		{
		  %>
		   employPortalForm.action='/hire/employNetPortal/search_zp_position.do?b_addInfo=add&flag='+flag;	 
		   employPortalForm.submit();
		  <%
		}
		}
		%>
		//employPortalForm.action='/hire/employNetPortal/search_zp_position.do?b_addInfo=add'
		//employPortalForm.submit();
	}

	function returnInfo(outparamters)
	{
		var info=outparamters.getValue("info");
		var type=outparamters.getValue("type")
		if(info=='failue')
		{
		    if(type=='0')
		    {
		        var msg=getDecodeStr(outparamters.getValue("msg"));
		        if(msg=='1')
		        {
		    	   alert(IDCARD_NUMBER_ALREADY_HAVE+"！");
		    	   return;
		    	}
		    	else
		    	{
		    	    alert(msg);
		            return;
		    	}
		    }
		    else
		    {
		        var msg=getDecodeStr(outparamters.getValue("msg"));
		        alert(msg);
		        return;
		    }
		}
		else
		{
			employPortalForm.action='/hire/employNetPortal/search_zp_position.do?b_addInfo=add'
			employPortalForm.submit();
		}
	}
	<%
	if(finished!=null&&finished.equals("1"))
	{
	%>
	window.setTimeout('alerts()',200);   
	function alerts()
	{
		alert(YOUR_RESUME_SUBMIT_SUCCESS);
		document.location="/hire/employNetPortal/search_zp_position.do?b_query=link&operate=init&abcd=1";
	}
	<%
	} %>
</script>

</body>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">AxManager.writeCard();</script>
 <script language="javascript"> 
         initCard();
</script> 
</html>
