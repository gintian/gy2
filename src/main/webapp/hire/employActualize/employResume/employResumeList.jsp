<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.hire.employActualize.EmployResumeForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.struts.valueobject.UserView,com.hrms.hjsj.utils.Sql_switcher,
				com.hrms.struts.constant.WebConstant,com.hjsj.hrms.utils.PubFunc,
				com.hrms.hjsj.sys.Des"%>
<%@ page import="com.hrms.hjsj.sys.ResourceFactory"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
 <script type="text/javascript" src="/js/wz_tooltip.js"></script>
<script language="javascript" src="/hire/employActualize/employResume/employResume.js"></script>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">AxManager.writeCard();</script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<%
  
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String unitid=userView.getUnit_id();
	int dbtype=Sql_switcher.searchDbServer();
	
%>

 <%
  	      EmployResumeForm employResumeForm=(EmployResumeForm)session.getAttribute("employResumeForm");	
  	      String fielditem1=(employResumeForm.getFielditem1()+"1").toLowerCase();
  	      String fielditem2=(employResumeForm.getFielditem2()+"2").toLowerCase();
  	      String fielditem3=(employResumeForm.getFielditem3()+"3").toLowerCase();
  	      String fielditem4=(employResumeForm.getFielditem4()+"4").toLowerCase();
  	      String fielditem5=(employResumeForm.getFielditem5()+"5").toLowerCase();
          ArrayList posConditionList=employResumeForm.getPosConditionList();
          ArrayList resumeStateList=employResumeForm.getResumeStateList();
          String resumeState=employResumeForm.getResumeState();
          String personType=employResumeForm.getPersonType();  // 0:应聘库 1：人才库
          String upValue=employResumeForm.getUpValue();
          String isShowCompare=employResumeForm.getIsShowCompare();
          String schoolPosition=employResumeForm.getSchoolPosition();
          String url_p=SystemConfig.getServerURL(request);
          String isCode=employResumeForm.getIsCode();
          
       
  %>
<html>
  <head>
   <script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
  <script language='javascript'>
 
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
  
  function setPosList()
{
	var aa=document.getElementsByName("s.value")
	if(trim(aa[0].value).length==0)
		return;
	var hashvo=new ParameterSet();
	hashvo.setValue("operator","3");
	var In_paramters="orgID="+aa[0].value;  
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo1,functionId:'3000000174'},hashvo);
}



function returnInfo1(outparamters)
{
	var poslist=outparamters.getValue("poslist");	
	var aa=document.getElementsByName("posID");
	AjaxBind.bind(aa[0],poslist);
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
    .newbutton{
        height:23px;
    }
    
    </style>
  </head>
  
  
  <body onload="setTPinput()">
  <hrms:themes></hrms:themes>
  <%
  String bosflag= userView.getBosflag();//得到系统的版本号
  String _align="right";
  if(bosflag!=null&&bosflag.equalsIgnoreCase("hcm")){
    _align="right";
%>
  <link href="/hire/css/layout.css" rel="stylesheet" type="text/css">
<%
  }
%>
  <html:form action="/hire/employActualize/employResume">
  <a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
  <table width='97%' border=0 class="normalbuttompCKYP">
  <tr><td align='left' >
  <table width='100%'  class="containqueryExpTable">
  <tr><td align='left' >
  
  <table width='100%' border=0 class='nopaddingCKYPTable' ><tr><td align='left' valign="top">
  
  <%
  	if(personType.equals("0")){  
       for(int i=0;i<resumeStateList.size();i++)
       {
       		CommonData data=(CommonData)resumeStateList.get(i);
        	out.print("<Input type='radio' name='resumeState'");
        	if(data.getDataValue().equalsIgnoreCase(resumeState))
        	{
        		out.print(" checked ");
        	}
        	out.print(" value='"+data.getDataValue()+"' onclick=\"sub('0')\"/>"+data.getDataName()+"&nbsp;&nbsp; ");
       }
     }
       %>
  </td><td align='right'>
  
  <table width='90' class="ckypTopButtonCtrolTable">
  <tr>
	  <td  nowrap align="right"><!-- 查询说明 -->
	       <input type='button' value="<bean:message key="hire.query.explanation"/>"  class='mybutton' onclick='showDeclare(this)' onblur="Element.hide('date_panel2');" />
	       <Input type='button' value="<bean:message key="query.resume.condition"/>" class='mybutton'  onclick='showOrClose();' /><!-- 查询简历条件 -->
	  </td>
	  <td >
		  <div id='b' style='display:${employResumeForm.isShowCondition}' > 
		  <Input type='button' value="<bean:message key="infor.menu.query"/>" onclick='sub(1)' class='mybutton' />  </div><!-- 查询 -->
	  </td>
  </tr>
  </table>
  </td></tr>
 </table> 
  </td></tr>
  <tr><td>
  	<table border=0  id='aa' style='display:${employResumeForm.isShowCondition}'>
			
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
           			//{
           				//LazyDynaBean abean0=null;
           				//String setname0="";
           				//LazyDynaBean abean2=null;
           				//String setname2="";
           				//if((j+1)<posConditionList.size())
           				//{
           					//abean2=(LazyDynaBean)posConditionList.get(j+1);
           					//setname2=(String)abean2.get("setname");
           					
           				//}
           				//if(j!=0)
           				//{
           					//abean0=(LazyDynaBean)posConditionList.get(j-1);
           					//setname0=(String)abean0.get("setname");
           				//}
           				//if(abean0==null&&abean2==null)
           					//isFlag=true;
           				//else if((abean0==null&&abean2!=null)&&!setname2.equals(setname))
           					//isFlag=true;
           				//else if((abean0!=null&&abean2==null)&&!setname0.equals(setname))
           					//isFlag=true;
           				//else if(abean0!=null&&abean2!=null&&!setname2.equals(setname)&&!setname0.equals(setname))
           					//isFlag=true;
           			//}
           			
           			if(flag.equalsIgnoreCase("false"))//setname.equalsIgnoreCase("A01")||isFlag
           			{
           			        if(bosflag!=null&&bosflag.equals("hcm")){//hcm 7.0版本对齐方式修改
           			          out.print(" <tr><td  align='right' >"+itemdesc+"&nbsp;&nbsp;</td><td valign='bottom'  align='left' >");
           			        }else{//保留原有hr版本的界面对齐方式
           			          out.print(" <tr><td width='15%' align='right' >"+itemdesc+"&nbsp;&nbsp;&nbsp;&nbsp;</td><td  width='85%' valign='bottom'  align='left' >");
           			        }
           					          			
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
						             	out.print("<span> <img  src='/images/code.gif' border=0 width=20 height=20 onclick='openInputCodeDialogS_value(\""+codesetid+"\",\"posConditionList["+j+"].view_s_value\")'  style='position:relative;top:5px;'/></span>&nbsp;");		
										out.print("<input type='hidden' value='"+s_value+"'  name='posConditionList["+j+"].s_value' /> &nbsp;");
										if(flag.equals("false"))
										{
											out.print("&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.init.tand")+"&nbsp;&nbsp;");
											out.print(" <input type='text' onchange='clearCodeValue(\"posConditionList["+j+"].view_s_value\",\"posConditionList["+j+"].s_value\")' size='15'  name='posConditionList["+j+"].view_e_value'  class=textbox value='"+view_e_value+"'  />");
							             	out.print(" <span><img  src='/images/code.gif' border=0 width=20 height=20 onclick='openInputCodeDialogE_value(\""+codesetid+"\",\"posConditionList["+j+"].view_e_value\")' style='position:relative;top:5px;' /></span>&nbsp;");		
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
								out.println("<input type='text'  name='posConditionList["+j+"].s_value'  class=textbox value='"+s_value+"'  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' />&nbsp;");
								if(flag.equals("false"))
								{
										out.print("&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.init.tand")+"&nbsp;&nbsp;");
										out.println("<input type='text'  name='posConditionList["+j+"].e_value'  class=textbox value='"+e_value+"'  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)'  />&nbsp;");								
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
           					    if(flag3.equalsIgnoreCase("false"))
           					    {
           					       break;
           					    }
           			
           						if(e==j){
           						   if(bosflag!=null&&bosflag.equals("hcm")){//hcm 7.0版本对齐方式修改
           							out.print(" <tr id='"+aa_setname+"' style='display=block' ><td  align='right' >"+itemdesc+"&nbsp;&nbsp;</td><td  valign='bottom'  align='left' >");
           						   }else{//保留原有的hr界面对齐方式
           						    out.print(" <tr id='"+aa_setname+"' style='display=block' ><td width='15%' align='right' >"+itemdesc+"&nbsp;&nbsp;&nbsp;&nbsp;</td><td  width='85%' valign='bottom'  align='left' >");
           						   }          			
           						}
           						
           				
           						if(aa_setname.equals(setname3)&&!itemID.equals(itemid3))
           						{
           							 if(itemID.equals(""))
										itemID=itemid3;
           							 if(f!=0)
           							 {
	           							 out.print(itemdesc3);
	           							 j++;
	           						 }
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
												
								           
								              	out.print(" <input type='text' onchange='clearCodeValue(\"posConditionList["+j+"].view_s_value\",\"posConditionList["+j+"].s_value\")' size='10' name='posConditionList["+e+"].view_s_value'  class=textbox value='"+view_s_value3+"'  />");
								             	out.print(" <span><img  src='/images/code.gif' border=0 width=20 height=20 onclick='openInputCodeDialogS_value(\""+codesetid3+"\",\"posConditionList["+e+"].view_s_value\")'  style='position:relative;top:5px;' /></span>");		
											
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
										out.println("<input type='text' size='10' name='posConditionList["+e+"].s_value'  class=textbox value='"+s_value3+"'  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)'   />&nbsp;");
										
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
		
	<tr>
			<logic:equal name="employResumeForm" property="isShow" value="1">
			<td align="<%=_align %>"><!-- hcm7.0对其方式改变 -->
	  		所属机构&nbsp;&nbsp;
	  		</td>
	  		<td>
	  		<input type='hidden' name='s.value'    value="${employResumeForm.value}"   />  
	  		<input type='text' name='s.viewvalue'  onChange='setPosList()'   size='10' value="${employResumeForm.viewvalue}"   readonly/> 
	  		<span>
	  		<img  src="/images/code.gif" onclick='javascript:openInputCodeDialog_self("UM","s.viewvalue");' style='position:relative;top:5px;'/>&nbsp;		
			</span>
			<input type='hidden' name='value' value="${employResumeForm.value}" />
			<input type='hidden' name='viewvalue' value="${employResumeForm.viewvalue}" />
			<!-- 应聘岗位 -->			    
			
	  		<bean:message key="hire.employActualize.interviewPosition"/>
	  		<html:select name="employResumeForm" property="posID" size="1">
            <html:optionsCollection property="posIDList" value="dataValue" label="dataName"/>
        	</html:select>
        	</td>
			</logic:equal>
			<!-- 应聘专业 -->
			<logic:equal name="employResumeForm" property="isShow" value="2">
			<td align="<%=_align %>"><!-- hcm7.0对其方式改变 -->
	  		<bean:message key="hire.employActualize.interviewProfessional"/>&nbsp;&nbsp;&nbsp;&nbsp;
	  		</td>
	  		<td>
	  		<%if(isCode!=null&&isCode.length()!=0&&isCode.equalsIgnoreCase("true")){ %>
	  		<html:select name="employResumeForm" property="professional" size="1">
	  		<html:option value=" "></html:option>
            <html:optionsCollection property="pflist" value="dataValue" label="dataName"/>
        	</html:select>
		    <%}else{ %>
	        <input type="text" size='10' name="professional" value="" />
	        <%} %>
	        </td>
	        <input type='hidden' name='value' />
			<input type='hidden' name='viewvalue' />
			<input type='hidden' name='s.value' />
			<input type='hidden' name='s.viewvalue' />
	        </logic:equal>
	  </tr>
	  <% if("-1".equals(resumeState)){
	  %>
	   <tr id="ypdate" style="display=none">   
	  <%
	  }else{
	  %>
	   <tr id="ypdate" style="display=block">
	  <%
	  }
	  %>
	  
	  		<td align="<%=_align %>"><!-- hcm7.0对其方式改变 -->
	  			<bean:message key="hire.employActualize.interviewTime"/>&nbsp;&nbsp;<!-- 应聘时间 -->
	  		</td>
	  		<td>
	  		<input name="applyStartDate" id="applyStartDate" class="textbox" value="${employResumeForm.applyStartDate}"  onclick="popUpCalendar(this,this, dateFormat,'','',true,false)"  type="text"/>
	  		至
	  		<input name="applyEndDate" id="applyEndDate" class="textbox" value="${employResumeForm.applyEndDate}"  onclick="popUpCalendar(this,this, dateFormat,'','',true,false)"  type="text"/>
	  		</td>
	  </tr>
	  <tr>
	  <td align="<%=_align %>"><!-- hcm7.0对其方式改变 -->
	  <html:checkbox property='vague' name='employResumeForm' value='1' /><bean:message key="label.query.like"/>
	  <input type='hidden' value="${employResumeForm.upValue}" name='upValue' />
	  </td>
	  </tr>

	</table>  
  </td></tr>
  </table>
  
  
  
  <%
    String _width="97%";
    if(bosflag!=null&&bosflag.equals("hcm")){
        _width="100%";
    }
  %>
  <table width='<%=_width%>' align='center' cellpadding="0" cellspacing="0"   ><tr><td class='RecordRow_top common_border_color' height='2' >
      &nbsp;   
  </td> </tr></table>
  
  <table width='100%' align='center' cellpadding="0" cellspacing="0" class="nomalCommonTable" ><tr><td align='left' valign='top'>
   
    </td>
    <td align='right' valign='top'  >
    	<% if(isShowCompare.equals("1")){
    			out.print("<input type='button' value='"+ResourceFactory.getProperty("hire.resume.synchronize")+"'  class='mybutton' onclick='synchronize()' />");
    		}
    	 %>
    
    
    	<% if(personType.equals("0")&&!resumeState.equals("-1")&&!resumeState.equals("-3")){  %>
    	<input type='button' value="<bean:message key="hire.set.status"/>" onclick="showDateSelectBox(this);" onblur="Element.hide('date_panel');"  class="mybutton"   />   	
    	<% }
    		else if(!personType.equals("4")&&!resumeState.equals("-3")){
			out.print("<input type='button' value='"+ResourceFactory.getProperty("hire.recommend.position")+"'  class='mybutton'   onclick='introPos()'/>");
		}    
    	  if(personType.equals("1")){
    	 %>
    	 <input type='button' value="<bean:message key="hire.move.employstorehouse"/>"  class="mybutton"   onclick="switchPersonType(1)"/>
    	 <% } %>
    	
    	 <input type='button' value="<bean:message key="hire.employActualize.personnelFilter.comment"/>"  class="mybutton"   onclick="getMusterFields()"/><!-- 评语 --> 
    	<% if(personType.equals("0")){  %>
    	
    	<input type='button' value="<bean:message key="hire.move.personstorehouse"/>" class="mybutton"  onclick='switchPersonType(0)'/>
    	<% if(!resumeState.equals("-3")&&!resumeState.equals("-2")){ %>
    	<hrms:priv func_id="310232">
    	<input type='button' value="<bean:message key="hire.button.delete"/>"  class="mybutton"  onclick='delRecord()'  />
    	</hrms:priv>
    	<%}else{ %>
    	
    	<% }} 
    	if(personType.equals("4"))
    	{
    	if(!resumeState.equals("-3")){
    	%>
    	<input type='button' value="<bean:message key="hire.button.delete"/>"  class="mybutton"  onclick='delRecord2()'  />
    	<%
    	}else{
    	
    	}
    	}
    	  if(!personType.equals("4")){
    	%>
    	<logic:equal name="employResumeForm" property="employType" value="0">
    	<Input type='button' value="<bean:message key="hire.hold.inside"/>" class='mybutton'  onclick='collect()' /> 
    	 	<%if(!personType.equals("1")){%>
    	<input type='button' value='打印'  class='mybutton' onclick='PrintCardAnalysis();'/> 
    	<% } %>
    	</logic:equal>
    	<% } %>
    	<input type="button" value="<bean:message key="sys.export.derivedExcel"/>" class='mybutton' onclick='exportResume()'><!-- 导出Excel不需要功能授权 -->
    	<%
    	   if(!personType.equals("4")){//只有应聘简历中需要有导出zip的功能
    	%>
    	<hrms:priv func_id="310234"><!-- 导出zip格式需要功能权限控制 -->
           <input type="button"  menu="menu1" allowPushDown="false" name="hire_export_dropdown_button" extra="button" down="false" style="height:25px;" class="mybutton"  value='<bean:message key="sys.export.derived"/>' />   	
    	   <!--<input type='button' value="<bean:message key="sys.export.derived"/>" extra="button" class='mybutton'  onclick='exportResumeZip()' /> -->
    	</hrms:priv>
    	<hrms:priv func_id="310235"><!-- 导入zip格式文件需要功能权限控制 -->
           <input type='button' value="<bean:message key="sys.export.derivedIn"/>"  class='mybutton'  onclick='importResumeZip()'/>
        </hrms:priv>
        <%
        }
        %>
    	<% if(personType.equals("0")){  %> 
    	<hrms:priv func_id="310231">
    	<logic:equal name="employResumeForm" property="employType" value="0">
    	<input type='button' value="<bean:message key="label.zp_employ.sendmail"/>"  class='mybutton'  onclick='sendmail()' />
    	<input type='button' value="<bean:message key="hire.groupsend.email"/>"  class='mybutton'  onclick='sendAllmail()' />
    	</logic:equal>
    	</hrms:priv>
    	<% } %>
    	<Input type='button' value="<bean:message key="infor.menu.picture"/>" class='mybutton'  onclick='showPhoto()' />
        <%if(personType.equals("0")){ %>
    	<logic:notEqual name="employResumeForm" property="z0301" value="-1">
    		<input type='button' value="<bean:message key="button.return"/>"  class='mybutton'  onclick='retF();' />
    	</logic:notEqual>
    	<%if(resumeState.equals("12")){ %>
    	<hrms:priv func_id="310233">
    	<input type='button' value="查看全部"  class='mybutton'  onclick='queryAll();' />
    	</hrms:priv>
    	<%} %>
    	<%} %>
    	
    </td>
     </tr></table>   
        <hrms:menubar menu="menu1" id="menubar1" container="" visible="false">
            <hrms:menuitem name="mitem1" label="导出选中数据" icon="/images/export.gif" url="exportResumeZip('0');"  enabled="true" visible="true"/>
            <hrms:menuitem name="mitem1" label="导出全部数据" icon="/images/export.gif" url="exportResumeZip('1');"  enabled="true" visible="true"/>
        </hrms:menubar> 
  	<table align='center' width='100%' border=0 class="nomalCommonTable">
  		<tr><td align='left' ><Image src='/images/icon_speaker.gif' />
  		<%
  		   if(personType.equals("4")) out.print(ResourceFactory.getProperty("hire.hold.folder")+"：&nbsp;");
  		   else if(personType.equals("1")) out.print(ResourceFactory.getProperty("label.zp_options.baseoptions")+"：&nbsp;");
  		%>
  		<bean:message key="hire.manage.resume"/> ${employResumeForm.resumeCount}&nbsp; <bean:message key="hire.resume.lot"/></td>
  		<td align='right'>
  		<bean:message key="lable.zp_plan.zp_object"/><!-- 招聘对象 -->
  		<html:select name="employResumeForm" property="hireObjectId" size="1"  onchange='sub(1)'  >
                              <html:optionsCollection property="hireObjectList" value="dataValue" label="dataName"/>
        </html:select> 
  		&nbsp;&nbsp;
  		<bean:message key="system.option.an"/>
  		<html:select name="employResumeForm" property="order_item" size="1">
                              <html:optionsCollection property="orderItemList" value="dataValue" label="dataName"/>
        </html:select>  
  		<html:select name="employResumeForm" property="order_desc" size="1">
                              <html:optionsCollection property="orderDescList" value="dataValue" label="dataName"/>
        </html:select>
        &nbsp;&nbsp;<Input type='button' value="<bean:message key="label.zp_exam.sort"/>"  onclick='sub(1)'  class='mybutton' />
        <logic:equal value="dxt" name="employResumeForm" property="returnflag">
         <hrms:tipwizardbutton flag="retain" target="1" formname="employResumeForm"/> 
         </logic:equal>
  		</td></tr>
  	</table>
  	
  	
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable nomalCommonTable">
   	  <thead>
           <tr>
		      <td align="center" class="TableRow" nowrap>
				<Input type='checkbox' name='allOperat' onclick='operateCheckBox(this)' />
		      </td> 
		 	<%
		 		if(personType.equals("0"))
		 		{
		 			out.println("<td align='center' class='TableRow' nowrap>"+ResourceFactory.getProperty("general.mediainfo.state")+" </td>");
		 			if(!resumeState.equals("-1"))
			 			out.println("<td align='center' class='TableRow' nowrap>"+ResourceFactory.getProperty("hire.wish")+" </td>");
		 		}
		 		out.println("<td align='center' class='TableRow' nowrap>"+ResourceFactory.getProperty("hire.employActualize.name")+" </td>");
		 		if((personType.equals("0")||personType.equals("1"))&&!resumeState.equals("-1"))
		 		{
		 		    if(schoolPosition!=null&&schoolPosition.length()>0)
		 		        out.println("<td align='center' class='TableRow' nowrap>"+ResourceFactory.getProperty("hire.apply.majorposition")+" </td>");
		 		    else
		 		    	out.println("<td align='center' class='TableRow' nowrap>"+ResourceFactory.getProperty("hire.apply.position")+" </td>");
		 		}	
		 	%>
		     <!--  <td align="center" class="TableRow" nowrap>
				状态
		      </td>
		      <td align="center" class="TableRow" nowrap>
				志愿
		      </td> 
		      <td align="center" class="TableRow" nowrap>
				姓名
		      </td> 
		      <td align="center" class="TableRow" nowrap>
				职位名称
		      </td> 
		      -->

				<!-- 加入 岗位 -->
		      <td align="center" class="TableRow" style="padding-top: 3px;" nowrap>
				<html:select name="employResumeForm" property="fielditem1" onchange='sub(1)' size="1">
                              <html:optionsCollection property="resumeFieldList" value="dataValue" label="dataName"/>
        		</html:select>  
		      </td> 
		      
		      <td align="center" class="TableRow" style="padding-top: 3px;" nowrap>
				<html:select name="employResumeForm" property="fielditem2" onchange='sub(1)'  size="1">
                              <html:optionsCollection property="resumeFieldList" value="dataValue" label="dataName"/>
        		</html:select>  
		      </td> 
		      <td align="center" class="TableRow" style="padding-top: 3px;" nowrap>
				<html:select name="employResumeForm" property="fielditem3" onchange='sub(1)'  size="1">
                              <html:optionsCollection property="resumeFieldList" value="dataValue" label="dataName"/>
        		</html:select>  
		      </td> 
		      
		      <td  style="display:none" align="center" style="padding-top: 3px;" class="TableRow" nowrap>
				<html:select name="employResumeForm" property="fielditem4" onchange='sub(1)'  size="1">
                              <html:optionsCollection property="resumeFieldList" value="dataValue" label="dataName"/>
        		</html:select>  
		      </td> 
		      
		      <td align="center" class="TableRow" style="padding-top: 3px;" nowrap>
				<html:select name="employResumeForm" property="fielditem5" onchange='sub(1)'  size="1">
                              <html:optionsCollection property="resumeFieldList" value="dataValue" label="dataName"/>
        		</html:select>  
		      </td> 	
		             	        
         </tr>
   	  </thead>
   	  <% int i=0; String className="trShallow"; 
		    String dbName = employResumeForm.getDbname();
            dbName = PubFunc.encryption(dbName);
   	  %>
   	  <hrms:paginationdb id="element" name="employResumeForm" sql_str="${employResumeForm.str_sql}" table="" where_str="${employResumeForm.str_whl}"  order_by="${employResumeForm.order_str}" columns="${employResumeForm.colums}"  page_id="pagination" pagerows="${employResumeForm.pagerows}" indexes="indexes"  allmemo="1"   >
		<%
			  if(i%2==0)
			  	className="trShallow";
			  else
			  	className="trDeep";
			  i++;
			  
			  %>
	   <tr class='<%=className%>'  >
	   		<td align="center" class="RecordRow" nowrap>
	   			<hrms:checkmultibox name="employResumeForm" property="pagination.select" value="true" indexes="indexes"/>
	   		</td>
	   	<% 
	   		if(personType.equals("0"))
	   		{
	   	%>
	   		<td class="RecordRow" nowrap align='center' >
	   		<%
	   		if(resumeState.equals("-3")||resumeState.equals("-2"))
	   		{
	   		   %>
	   		   <logic:equal name="element" property="resume_flag" value="">
                    <Image src='/images/icon_wsx.gif' />
               </logic:equal>
	   		   <logic:equal name="element" property="resume_flag" value="10">
	   		        <Image src='/images/icon_wsx.gif' />
	   		   </logic:equal>
	   		   <logic:equal name="element" property="resume_flag" value="11">
	   	     	   <Image src='/images/icon_dd.gif' />
	   		   </logic:equal>
	   		   <logic:equal name="element" property="resume_flag" value="12">
	   		     <Image src='/images/icon_tgsx.gif' />
	   		   </logic:equal>
	   		  <logic:equal name="element" property="resume_flag" value="13">
	   		  <Image src='/images/icon_wtgsx.gif' />
	   		  </logic:equal>
	   		  <logic:notEqual name="element" property="resume_flag" value="">
	   		      <logic:notEqual name="element" property="resume_flag" value="10">
                    <logic:notEqual name="element" property="resume_flag" value="11">
                        <logic:notEqual name="element" property="resume_flag" value="12">
                            <logic:notEqual name="element" property="resume_flag" value="13">
                                <Image src='/images/cc1.gif' />
                            </logic:notEqual>
                        </logic:notEqual>
                    </logic:notEqual> 
                  </logic:notEqual>
	   		  </logic:notEqual>
	   		   <%
	   		}else
	   		{
	   	    	if(resumeState.equals("10")||resumeState.equals("-1"))
	   		    	out.print("<Image src='/images/icon_wsx.gif' />");
	   	    	else if(resumeState.equals("11"))
	   		    	out.print("<Image src='/images/icon_dd.gif' />");
	      		else if(resumeState.equals("12"))
	      			out.print("<Image src='/images/icon_tgsx.gif' />");
	       		else if(resumeState.equals("13"))
	   	    	 	out.print("<Image src='/images/icon_wtgsx.gif' />");
	   	    	else if(!resumeState.equals("-3"))
	   		        out.print("<Image src='/images/cc1.gif' />");
	   		 }
	   		%>
	   		
	   		
	   		</td>
	   		<%
	   		 if(!resumeState.equals("-1")){ 
	   		
	   		 %>
	   		<td class="RecordRow" nowrap align='right' >
	   		<bean:write name="element" property="thenumber" />
	   		</td>
	   		
	    	<%
	    		}
	    	 }
	  	     ArrayList encryption_list=employResumeForm.getEncryption_list();
	   	     LazyDynaBean bean=(LazyDynaBean)encryption_list.get(0);
	    	 LazyDynaBean abean2=(LazyDynaBean)pageContext.getAttribute("element");
	 		 String a0100 = (String)abean2.get("a0100");
	 		 String zp_pos_id = (String)abean2.get("zp_pos_id");
			 String  encryption_a0100="";
			 String  encryption_zp_pos_id="";
			 if(!resumeState.equals("-1")&&personType.equals("0")){
				 encryption_zp_pos_id=(String)bean.get(zp_pos_id);  
			 }
			 encryption_a0100=(String)bean.get(a0100);
	    	 %>
	   		
	   		<td class="RecordRow" nowrap align='left' >
	   		<a href='javascript:resumeBrowse("<%=encryption_a0100%>","<%=dbName%>","<%=encryption_zp_pos_id%>")'>
	   		<bean:write name="element" property="a0101" />
	   		</a>
	   		</td>
	   		<% 
	   		if(personType.equals("0"))
	   		{
	   			if(!resumeState.equals("-1")){
	   		%>
	   		<td class="RecordRow" nowrap align='left' >
	   		<bean:write name="element" property="zp_name" />
	   		</td>
	   		<%   } %>
	   		<input type='hidden'  name='ids' value='<%=encryption_a0100 %>/<%=encryption_zp_pos_id%>' />
	   		<input type='hidden'  name='zp_pos_id' value='<%=encryption_zp_pos_id%>'/>
	   		<input type='hidden'  name='thenumber' value='<bean:write name="element" property="thenumber" />' />
	   		<% } 
	   		else if(personType.equals("4"))
	   		{
	   		%>
	   		<input type='hidden'  name='ids' value='<%=encryption_a0100 %>' />
	   		<input type='hidden'  name='thenumber' value='<bean:write name="element" property="thenumber" />' />
	   		<% } else if(personType.equals("1"))
			  {%>
    		<td class="RecordRow" nowrap align='left' >
	   		<bean:write name="element" property="zp_name" />
	   		</td>
	   		<input type='hidden'  name='ids' value='<%=encryption_a0100 %>' />
	   		<input type='hidden'  name='thenumber' value='<bean:write name="element" property="thenumber" />' />
	   		<% } %>
	   		
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
	   		<bean:define id="eventen" name="element" property="<%=fielditem3%>" />
								<hrms:showitemmemo showtext="showtext" itemtype="M" setname=""
									tiptext="tiptext" text="${eventen}"></hrms:showitemmemo>
								<td align="left" class="RecordRow" ${tiptext}  nowrap>
									${showtext}
								</td>
	   		<td  style="display:none"  class="RecordRow" nowrap align='left' >
	   		<bean:write name="element" property="<%=fielditem4%>" />
	   		</td>
	   		<bean:define id="eventen" name="element" property="<%=fielditem5%>" />
								<hrms:showitemmemo showtext="showtext" itemtype="M" setname=""
									tiptext="tiptext" text="${eventen}"></hrms:showitemmemo>
								<td align="left" class="RecordRow" ${tiptext}  nowrap>
									${showtext}
								</td>
	   	  
   	  	</tr>
   	   </hrms:paginationdb>
  
  	</table>
  
  		
<table  width="100%"  class='RecordRowP nomalCommonTable'  align="center" >
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="hmuster.label.d"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="hmuster.label.paper"/>
					<bean:message key="hmuster.label.total"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.item"/>
					<bean:message key="hmuster.label.total"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="hmuster.label.paper"/>
					&nbsp;&nbsp;
			 每页显示<html:text property="pagerows" styleId="pagenum" name="employResumeForm" size="3"></html:text>条&nbsp;&nbsp;<a href="javascript:sub(1);">刷新</a>
			</td>
			
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="employResumeForm" property="pagination" nameId="employResumeForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table> 
  	
  
  
  
   <table  width="100%" align="center" class='nomalCommonTable'>
	<tr><td align='left'>
			<% 
	   		if(personType.equals("0"))
	   		{
	   		%>
  <Image src='/images/icon_speaker.gif' />&nbsp;<bean:message key="hire.employActualize.resumeState"/>：&nbsp;
  <%
  
            for(int j=0;j<resumeStateList.size();j++)
			        {
			             if(j!=resumeStateList.size()-1)
			             {
			              CommonData data=(CommonData)resumeStateList.get(j);
			                   if(data.getDataValue().equals("10"))
			                        out.print("<Image src='/images/icon_wsx.gif' />"+data.getDataName()+"&nbsp;");
			                   else if(data.getDataValue().equals("11"))
			                        out.print("<Image src='/images/icon_dd.gif' />"+data.getDataName()+"&nbsp;");
			                   else if(data.getDataValue().equals("12"))
			                        out.print("<Image src='/images/icon_tgsx.gif' />"+data.getDataName()+"&nbsp;");
			                   else if(data.getDataValue().equals("13"))
			                        out.print("<Image src='/images/icon_wtgsx.gif' />"+data.getDataName()+"&nbsp;");
                               else if(!data.getDataValue().equals("-3")&&!data.getDataValue().equals("-1")&&!data.getDataValue().equals("-2"))
                                    out.print("<Image src='/images/cc1.gif' />"+data.getDataName()+"&nbsp;");
			             }
			        }
			        }
   %>

  </td>
  <td align='right'>
  	<% if(isShowCompare.equals("1")){
    			out.print("<input type='button' value='"+ResourceFactory.getProperty("hire.resume.synchronize")+"'  class='mybutton' onclick='synchronize()' />");
    		}
    	 %>
  	<% if(personType.equals("0")&&!resumeState.equals("-1")&&!resumeState.equals("-3")){  %>
    	<input type='button' value="<bean:message key="hire.set.status"/>" onclick="showDateSelectBox(this);" onblur="Element.hide('date_panel');"  class="mybutton"   /> <!-- 设置筛选状态 -->  	
    	<% }
    		else if(!personType.equals("4")&&!resumeState.equals("-3")){
			out.print("<input type='button' value='"+ResourceFactory.getProperty("hire.recommend.position")+"'  class='mybutton'   onclick='introPos()'/>");
		}    
    	  if(personType.equals("1")){
    	 %>
    	 <input type='button' value="<bean:message key="hire.move.employstorehouse"/>"  class="mybutton"   onclick="switchPersonType(1)"/><!-- 移至应聘库 -->
    	 <% } %>
    	
    	 <input type='button' value="<bean:message key="hire.employActualize.personnelFilter.comment"/>"  class="mybutton"   onclick="getMusterFields()"/><!--评语 --> 
    	<% if(personType.equals("0")){  %>
    	
    	<input type='button' value="<bean:message key="hire.move.personstorehouse"/>" class="mybutton"  onclick='switchPersonType(0)'/><!-- 转入人才库 -->
    	<% if(!resumeState.equals("-3")&&!resumeState.equals("-2")) {%>
    	<input type='button' value="<bean:message key="hire.button.delete"/>"  class="mybutton"  onclick='delRecord()'  />
    	<% }else{
    	
    		}
    	 }
    	if(personType.equals("4"))
    	{
    		if(!resumeState.equals("-3")){
    	%>
    	<input type='button' value="<bean:message key="hire.button.delete"/>"  class="mybutton"  onclick='delRecord2()'  />
    	<%
    	}else{
    	
    	}
    	}
    	  if(!personType.equals("4")){
    	%>
    	<logic:equal name="employResumeForm" property="employType" value="0">
    	<Input type='button' value="<bean:message key="hire.hold.inside"/>" class='mybutton'  onclick='collect()' />
    	<%if(!personType.equals("1")){%>
    	<input type='button' value='打印'  class='mybutton' onclick='PrintCardAnalysis();'/> 
    	<% } %>
    	</logic:equal> 
    	<% } %>
    	<input type='button' value="<bean:message key="sys.export.derivedExcel"/>"  class='mybutton'  onclick='exportResume()' />
    	<%
           if(!personType.equals("4")){//只有应聘简历中需要有导出zip的功能
        %>
        <hrms:priv func_id="310234"><!-- 导出zip格式需要功能权限控制 -->
           <input type="button"  class="mybutton" menu="menu1" allowPushDown="false" name="hire_export_dropdown_button" extra="button" down="false" style="height:25px;" value='<bean:message key="sys.export.derived"/>' />      
           <!--<input type='button' value="<bean:message key="sys.export.derived"/>" extra="button" class='mybutton'  onclick='exportResumeZip()' /> -->
        </hrms:priv>
        <hrms:priv func_id="310235"><!-- 导入zip格式文件需要功能权限控制 -->
           <input type='button' value="<bean:message key="sys.export.derivedIn"/>"  class='mybutton'  onclick='importResumeZip()'/>
        </hrms:priv>
        <%
        }
        %>
    	<% if(personType.equals("0")){  %>   	
    	<hrms:priv func_id="310231">
    	<logic:equal name="employResumeForm" property="employType" value="0">
    	<input type='button' value="<bean:message key="label.zp_employ.sendmail"/>"  class='mybutton'  onclick='sendmail()' />
    	<input type='button' value="<bean:message key="hire.groupsend.email"/>"  class='mybutton'  onclick='sendAllmail()' />
    	</logic:equal>
    	
    	</hrms:priv>
    	<% } %>
    	<Input type='button' value="<bean:message key="infor.menu.picture"/>" class='mybutton'  onclick='showPhoto()' />
    	<%if(personType.equals("0")){ %>
    	<logic:notEqual name="employResumeForm" property="z0301" value="-1">
    		<input type='button' value="<bean:message key="button.return"/>"  class='mybutton'  onclick='retF();' />
    	</logic:notEqual>
    	<%if(resumeState.equals("12")){ %>
    	<hrms:priv func_id="310233">
    	<input type='button' value="查看全部"  class='mybutton'  onclick='queryAll();' />
    	</hrms:priv>
    	<%} %>
    	<%} %>
    	<input type='hidden' value="${employResumeForm.isShowCondition}"  name='isShowCondition'>
  		<input type='hidden' value="${employResumeForm.isSelectedAll}"  name='isSelectedAll'>
  		<html:hidden name="employResumeForm" property="conditionSQL"/>
  		<html:hidden name="employResumeForm" property="queryType"/>
  </td></tr></table>
  
  
  
  
  
         <div id="date_panel" style="display:none;">							
   			<select name="date_box" multiple="multiple"    style="width:110"  onchange="setSelectValue('${employResumeForm.resumeState}');" >    
			    
			    <%

			    
			        for(int j=0;j<resumeStateList.size();j++)
			        {
			             if(j!=resumeStateList.size()-1)
			             {
			                    CommonData data=(CommonData)resumeStateList.get(j);
			                    if(data.getDataValue().equals("-1")||data.getDataValue().equals("-2")||data.getDataValue().equals("-3"))
			                      continue;
			                    out.println("<option value='"+data.getDataValue()+"'>"+data.getDataName()+"</option>");
			             }
			        }
			        
			     %>
			    	    			    		    
              </select>
         </div>
  
  
  
  <script language="javascript">
   Element.hide('date_panel');
  
  
  
    if(document.employResumeForm.isSelectedAll.value=="1")
    {
    	document.employResumeForm.allOperat.checked=true;
    	for(var i=0;i<document.employResumeForm.elements.length;i++)
   		{
	   		
   			if(document.employResumeForm.elements[i].type=='checkbox'&&document.employResumeForm.elements[i].name.length>18&&document.employResumeForm.elements[i].name.substring(0,18)=='pagination.select[')
   				document.employResumeForm.elements[i].checked=true;
   		
   		}
    }
    
  

	function getMusterFields()
	{
		
	   
   		var num=0;
   		var a0100;
   		var isMultiple=0;
   		var ids=document.getElementsByName("ids");  
  	    for(var i=0;i<document.employResumeForm.elements.length;i++)
   	    {  	   		
   			if(document.employResumeForm.elements[i].type=='checkbox'&&document.employResumeForm.elements[i].name.length>18&&document.employResumeForm.elements[i].name.substring(0,18)=='pagination.select[')
   			{   				
   				if(document.employResumeForm.elements[i].checked==true)
   				{
   					isMultiple++; 	
   					var temps=new Array();
   					if(ids[num].value.indexOf("/")!=-1)
			   			temps=ids[num].value.split("/");
			   		else 
			   			temps[0]=ids[num].value;	
   								
   					a0100+="#"+temps[0];
   				}
   				num++;
   			}
   		}
   		
   		
   		if(isMultiple==0)
   		{
   			alert(PLASE_SELECT_RECORD+"!");
   			return;
   		}
   		
   		
   		var infos=new Array();
   		infos[0]=a0100.substring(10);
		var thecodeurl="/hire/employActualize/reviews/reviews.do?b_query=link`person_type=${employResumeForm.personType}"; 
		var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
       	var return_vo='';
       	if(isIE6()){
       	 return_vo= window.showModalDialog(iframe_url,infos, 
       		 "dialogWidth:570px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no");
       	}else{
		 return_vo= window.showModalDialog(iframe_url,infos, 
       		 "dialogWidth:550px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no");
       	}
       	if(return_vo)
	        alert(ADD_SUCCESS+"！");
	        return;
	}
	


	
	function sub(o)
	{
	
	var pagenum=document.getElementById("pagenum").value;
	var zhengzhengshu=/^[0-9]*[1-9][0-9]*$/;///正整数
	if(!zhengzhengshu.test(pagenum)){
		alert("每页显示条数请输入正整数!");
		return;
	}
		<%
		if(posConditionList.size()!=0){
		
		for(int m=0;m<posConditionList.size();m++)
        {
  			
           			LazyDynaBean abean=(LazyDynaBean)posConditionList.get(m);          			
           			String itemtype=(String)abean.get("fieldType");
           			String itemdesc=(String)abean.get("itemdesc");
           			String flag=(String)abean.get("flag");
           			if(itemtype.equals("D"))
           			{
    	       		%>
    	       			var a<%=m%>=document.getElementsByName("posConditionList[<%=m%>].s_value")						
						if(!validateData(a<%=m%>[0],'<%=itemdesc%>'))
							return;
						<%
						if(flag.equals("false"))
						{
						%>	
							var b<%=m%>=document.getElementsByName("posConditionList[<%=m%>].e_value")	
							
							if(b<%=m%>.length>0)
							{					
								if(!validateData(b<%=m%>[0],'<%=itemdesc%>'))
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
    	       			var a<%=m%>=document.getElementsByName("posConditionList[<%=m%>].s_value")
						if(trim(a<%=m%>[0].value).length!=0)
						{						
							 var myReg =/^(-?\d+)(\.\d+)?$/
							 if(!myReg.test(a<%=m%>[0].value)) 
							 {
								alert("<%=itemdesc%>"+PLEASE_INPUT_NUMBER+"！");
								return;
							 }
						 }
						 
						 <%
						if(flag.equals("false"))
						{
						%>	
							var b<%=m%>=document.getElementsByName("posConditionList[<%=m%>].e_value")
							if(trim(b<%=m%>[0].value).length!=0)
							{						
								 var myReg =/^(-?\d+)(\.\d+)?$/
								 if(!myReg.test(b<%=m%>[0].value)) 
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
		
		}
		%>
		var applyStartDate=document.getElementById("applyStartDate");
		var applyEndDate=document.getElementById("applyEndDate");
		if(applyStartDate!=null){
			if(!validateData(applyStartDate,'应聘时间(起始):'))///验证应聘时间格式
	   			return;
		}
		if(applyEndDate!=null){
			if(!validateData(applyEndDate,'应聘时间(结束):'))///
			   return;
		}

		for(var i=0;i<document.employResumeForm.elements.length;i++)
   		{
	   		
   			if(document.employResumeForm.elements[i].type=='checkbox'&&(document.employResumeForm.elements[i].name.length>22||document.employResumeForm.elements[i].name=='vague'))
   			{
   				//alert(document.employResumeForm.elements[i].checked+"  "+document.employResumeForm.elements[i].name );
   				if(!document.employResumeForm.elements[i].checked)
   				{
   					document.employResumeForm.elements[i].value="0";
   					document.employResumeForm.elements[i].checked=true;
   				}	
   			}
   		}
		if(o==1)
		{
		    document.getElementsByName("value")[0].value=document.getElementsByName("s.value")[0].value;
			document.getElementsByName("viewvalue")[0].value=document.getElementsByName("s.viewvalue")[0].value;
		
			document.employResumeForm.action="/hire/employActualize/employResume.do?b_query=link&select=1";
			
		}
		else if(o==0)
		{
			 		    var resumeStateArray = document.getElementsByName("resumeState");
		    var checkvalue="";
		    for(var i=0;i<resumeStateArray.length;i++){
		      if(resumeStateArray[i].checked){
                    checkvalue=resumeStateArray[i].value;
                    break;
              }
		    }
		    if(checkvalue=="-1"){
		          applyStartDate.value="";
		          applyEndDate.value="";
		    }
			document.employResumeForm.action="/hire/employActualize/employResume.do?b_query=link&operate=init2";
			document.getElementById("queryType").value="0";
		}
		document.employResumeForm.submit();
	
	}
	function queryAll()
	{
	    document.employResumeForm.action="/hire/employActualize/employResume.do?b_query=link&select=1";
		document.getElementById("queryType").value="1";
		document.employResumeForm.submit();
	}
   function validateState(recordIDs,state)
   {
   		var isMultiple=false;
   		for(var i=0;i<recordIDs.length;i++)
   		{
   			var temp=recordIDs[i];
   			var temps=recordIDs[i].split("/");
   			
   			for(var j=0;j<recordIDs.length;j++)
   			{
   				var temp2=recordIDs[j];
   				var temps2=recordIDs[j].split("/");
   				if(j!=i&&temps[0]==temps2[0])
   				{
   					isMultiple=true;
   					break;
   				}
   			}
   			if(isMultiple)
   				break;
   		}
   		
   		if(isMultiple)
   		{
   			alert(NOT_TO_CONFIG_THE_SAME_EMPLOY+"！");
   			return;
   		}
   
   		var hashvo = new ParameterSet();
		hashvo.setValue("recordIDs",recordIDs);
		var In_parameters="state="+state; 
		var request = new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:returnInfo,functionId:'3000000204'},hashvo);
   }
   
   
   //在initStr寻找所有的AFindText替换成ARepText
	function strReplaceAll(initStr,AFindText,ARepText){
	
	  var raRegExp = new RegExp(AFindText,"g");
	
	  return initStr.replace(raRegExp,ARepText);
	
	}
	

	
	
	
   function returnInfo(outparamters)
   {
  	 	var flag = outparamters.getValue("flag");
  	 	var state=outparamters.getValue("state");
  	 	if(flag=='1')
  	 	{
  	 		var info = outparamters.getValue("info");
  	 		info=strReplaceAll(info,"<br>","\r\n")
  	 		if(confirm(info))
  	 		{
  	 			document.employResumeForm.action="/hire/employActualize/employResume.do?b_setState=set&operate=set&state="+state; 	 		 
		        document.employResumeForm.submit();
  	 		}
  	 	}
  	 	else
  	 	{
  	 		 document.employResumeForm.action="/hire/employActualize/employResume.do?b_setState=set&operate=set&state="+state; 	 		 
		     document.employResumeForm.submit();
  	 	
  	 	}
  	 	
  	 	
   }
   
   
   function sendAllmail()
   {
   		var iWidth = 700;
  		var iHeight = 536;
  		var iTop = (window.screen.availHeight-30-iHeight)/2;
  		var iLeft = (window.screen.availWidth-10-iWidth)/2;
   		window.open("/hire/employActualize/employResume/batchSendMail.do?b_init=init&status=<%=resumeState%>&type=1","_blank","hotkeys=0,menubar=no,height="+iHeight+",width="+iWidth+",top="+iTop+",left="+iLeft+",toolbar=no,location=no,status=no,resizable=no");
   
   }
   
   function sendmail()
   {
  		var num=0;
   		var a0100;
   		var isMultiple=0;
   		var ids=document.getElementsByName("ids");  
  	    for(var i=0;i<document.employResumeForm.elements.length;i++)
   	    {  	   		
   			if(document.employResumeForm.elements[i].type=='checkbox'&&document.employResumeForm.elements[i].name.length>18&&document.employResumeForm.elements[i].name.substring(0,18)=='pagination.select[')
   			{   				
   				if(document.employResumeForm.elements[i].checked==true)
   				{
   					isMultiple++; 	
   					var temps=new Array();
   					
   					if(ids[num].value.indexOf("/")!=-1)
			   			temps=ids[num].value.split("/");
			   		else 
			   			temps[0]=ids[num].value;	
   					if(temps.length==2)		
   					{		
   						if(temps[1].length>0)
	 	  					a0100+=","+temps[0]+"/"+temps[1];
   						else
   							a0100+=","+temps[0];
   					}
   					else
   						a0100+=","+temps[0]
   				}
   				num++;
   			}
   		}
   		
   		
   		if(isMultiple==0)
   		{
   			alert(PLASE_SELECT_RECORD+"！");
   			return;
   		}
   		var iWidth = 700;
  		var iHeight = 536;
  		var iTop = (window.screen.availHeight-30-iHeight)/2;
  		var iLeft = (window.screen.availWidth-10-iWidth)/2;
   		window.open("/hire/employActualize/employResume/batchSendMail.do?b_init=init&status="+<%=resumeState%>+"&type=0&a0100s="+a0100.substring(1),"_blank","hotkeys=0,menubar=no,height="+iHeight+",width="+iWidth+",top="+iTop+",left="+iLeft+",toolbar=no,location=no,status=no,resizable=no");
   
   }
   function PrintCardAnalysis()
   {
      var num=0;
   		var a0100="";
   		var isMultiple=0;
   		var ids=document.getElementsByName("ids");  
   		var isSelectedAll=document.employResumeForm.isSelectedAll.value;
  	    for(var i=0;i<document.employResumeForm.elements.length;i++)
   	    {  	   		
   			if(document.employResumeForm.elements[i].type=='checkbox'&&document.employResumeForm.elements[i].name.length>18&&document.employResumeForm.elements[i].name.substring(0,18)=='pagination.select[')
   			{   				
   				if(document.employResumeForm.elements[i].checked==true)
   				{
   					isMultiple++; 	
   					var temps=new Array();
   					
   					if(ids[num].value.indexOf("/")!=-1)
			   			temps=ids[num].value.split("/");
			   		else 
			   			temps[0]=ids[num].value;	
   					if(temps.length==2)		
   					{		
   						if(temps[1].length>0)
	 	  					a0100+="'"+temps[0]+"',";
   						else
   							a0100+="'"+temps[0]+"',";
   					}
   					else
   						a0100+=","+temps[0]
   				}
   				num++;
   			}
   		}
   		if((isMultiple==0&&parseInt(isSelectedAll)==0)||trim(a0100)=='')
   		{
   			alert(PLASE_SELECT_RECORD+"！");
   			return;
   		}
		var iframe_url="/hire/employActualize/employResume.do?b_queryCard=queryCard&isSelectedAll="+isSelectedAll+"&a0100="+getEncodeStr(a0100)+"";
		var objlist= window.showModalDialog(iframe_url, arguments, 
    	    "dialogWidth:600px; dialogHeight:240px;resizable:no;center:yes;scroll:no;status:no");
       		
        //document.employResumeForm.action="/hire/employActualize/employResume.do?b_queryCard=queryCard"; 	 		 
		   
      //window.open("/hire/employActualize/employResume.do?b_queryCard=queryCard&a0100="+a0100+",_blank,height=840,left=0,top=0,scrollbars=yes, resizable=yes,location=no,status=no'");
         
   }
   
   
   //
   function exportResume(){
		var num=0;
   		var a0100="";
   		var number="";
   		var isMultiple=0;
   		var isMultiple2=0;
   		var isSelectedAll=document.employResumeForm.isSelectedAll.value;
   		var ids=document.getElementsByName("ids");
   		var thenumber=document.getElementsByName("thenumber");
   		
  	    for(var i=0;i<document.employResumeForm.elements.length;i++)
   	    {  	   		
   			if(document.employResumeForm.elements[i].type=='checkbox'&&document.employResumeForm.elements[i].name.length>18&&document.employResumeForm.elements[i].name.substring(0,18)=='pagination.select[')
   			{   				
   				if(document.employResumeForm.elements[i].checked==true)
   				{
   					isMultiple++; 	
   					var temps=new Array();
   					if(ids[num].value.indexOf("/")!=-1){
			   			temps=ids[num].value.split("/");
			   		}else {
			   			temps[0]=ids[num].value;
			   			}	
   					a0100+="#"+temps[0];
   					if(thenumber[num].value==null || thenumber[num].value==''){
   					thenumber[num].value='$';
   					}
   					number+="#"+thenumber[num].value;
   				}
   				num++;
   			}
   		}
   		
   		
   		if((isMultiple==0&&parseInt(isSelectedAll)==0)||trim(a0100)=='')
   		{
   			alert(PLASE_SELECT_RECORD+"！");
   			return;
   		}
   		var hashvo=new ParameterSet();
		hashvo.setValue("a0100",a0100.substring(1));
		hashvo.setValue("number",number.substring(1));
		hashvo.setValue("tablename",'resume');
		hashvo.setValue("nbase","${employResumeForm.dbname}");
		hashvo.setValue("isSelectedAll",employResumeForm.isSelectedAll.value);
		hashvo.setValue("resumeState",'<%=resumeState%>');
		hashvo.setValue("employType","${employResumeForm.employType}");
		hashvo.setValue("personType","${employResumeForm.personType}");
		hashvo.setValue("z0301","${employResumeForm.z0301}");
		hashvo.setValue("queryType","${employResumeForm.queryType}");
	    var In_paramters="flag=2";  
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'0521010011'},hashvo);
   }
   function exportResumeZip(selectAllValue){
        var num=0;
        var a0100="";
        var number="";
        var isMultiple=0;
        var isMultiple2=0;
        var isSelectedAll=selectAllValue;
        var ids=document.getElementsByName("ids");
        var thenumber=document.getElementsByName("thenumber");
        
        for(var i=0;i<document.employResumeForm.elements.length;i++)
        {           
            if(document.employResumeForm.elements[i].type=='checkbox'&&document.employResumeForm.elements[i].name.length>18&&document.employResumeForm.elements[i].name.substring(0,18)=='pagination.select[')
            {                   
                if(document.employResumeForm.elements[i].checked==true)
                {
                    isMultiple++;   
                    var temps=new Array();
                    if(ids[num].value.indexOf("/")!=-1){
                        temps=ids[num].value.split("/");
                    }else {
                        temps[0]=ids[num].value;
                        }   
                    a0100+="#"+temps[0];
                    if(thenumber[num].value==null || thenumber[num].value==''){
                    thenumber[num].value='$';
                    }
                    number+="#"+thenumber[num].value;
                }
                num++;
            }
        }
        
        
        if((isMultiple==0&&parseInt(isSelectedAll)==0))
        {
            alert(PLASE_SELECT_RECORD+"！");
            return;
        }
        var hashvo=new ParameterSet();
        hashvo.setValue("a0100",a0100.substring(1));
        hashvo.setValue("number",number.substring(1));
        hashvo.setValue("tablename",'resume');
        hashvo.setValue("nbase","${employResumeForm.dbname}");
        hashvo.setValue("isSelectedAll",isSelectedAll);
        hashvo.setValue("resumeState",'<%=resumeState%>');
        hashvo.setValue("employType","${employResumeForm.employType}");
        hashvo.setValue("personType","${employResumeForm.personType}");
        hashvo.setValue("z0301","${employResumeForm.z0301}");
        hashvo.setValue("queryType","${employResumeForm.queryType}");
        var In_paramters="flag=2";  
        var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showZipFile,functionId:'0521010027'},hashvo);
   }
   function showZipFile(outparamters){
    var infor=outparamters.getValue("infor");
    var name=outparamters.getValue("zipname");
    if(infor=="ok"){
		name = decode(name);
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+name);
    }
    if(infor=="error"){//这种情况一般不会存在的吧 
        alert("导出失败!");
    }
   }
   
    //输出 EXCEL OR PDF
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
			var win=open("/servlet/DisplayOleContent?filename="+outName+"&fromflag=register","excel");
		}
	}
   
   
   
   
   function collect()
   {
   		var num=0;
   		var a0100;
   		var isMultiple=0;
   		var ids=document.getElementsByName("ids");  
  	    for(var i=0;i<document.employResumeForm.elements.length;i++)
   	    {  	   		
   			if(document.employResumeForm.elements[i].type=='checkbox'&&document.employResumeForm.elements[i].name.length>18&&document.employResumeForm.elements[i].name.substring(0,18)=='pagination.select[')
   			{   				
   				if(document.employResumeForm.elements[i].checked==true)
   				{
   					isMultiple++; 	
   					var temps=new Array();
   					if(ids[num].value.indexOf("/")!=-1)
			   			temps=ids[num].value.split("/");
			   		else 
			   			temps[0]=ids[num].value;	
   								
   					a0100+="#"+temps[0];
   				}
   				num++;
   			}
   		}
   		
   		
   		if(isMultiple==0)
   		{
   			alert(PLASE_SELECT_RECORD+"！");
   			return;
   		}
   		var hashvo = new ParameterSet();
		hashvo.setValue("nbase","${employResumeForm.dbname}");
		var In_parameters="a0100="+a0100.substring(10); 
		var request = new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:returnInfo2,functionId:'3000000208'},hashvo);
   		
   
   }
   
   
    function returnInfo2(outparamters)
   {
  	 	alert(PUT_COLLECT_FOLDER_SUCCESS+"!");
   }
   
   
   
   function introPos()
   {
   
  	    var num=0;
   		var a0100;
   		var isMultiple=0;
   		var ids=document.getElementsByName("ids");  
  	    for(var i=0;i<document.employResumeForm.elements.length;i++)
   	    {  	   		
   			if(document.employResumeForm.elements[i].type=='checkbox'&&document.employResumeForm.elements[i].name.length>18&&document.employResumeForm.elements[i].name.substring(0,18)=='pagination.select[')
   			{   				
   				if(document.employResumeForm.elements[i].checked==true)
   				{
   					isMultiple++; 	
   					var temps=new Array();
   					if(ids[num].value.indexOf("/")!=-1)
			   			temps=ids[num].value.split("/");
			   		else 
			   			temps[0]=ids[num].value;	
   								
   					a0100+="#"+temps[0];
   				}
   				num++;
   			}
   		}
   		
   		
   		if(isMultiple==0)
   		{
   			alert(PLASE_SELECT_RECORD+"！");
   			return;
   		}
   		var thecodeurl="/hire/employActualize/employResume.do?b_initPositon=init"; 
		var objlist= window.showModalDialog(thecodeurl, arguments, 
    	    "dialogWidth:900px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
        if(objlist)
		{ 
			
			 document.employResumeForm.action="/hire/employActualize/employResume.do?b_setState=set&operate=selectPos&z0301s="+objlist; 	 		 
		     document.employResumeForm.submit();
		}
   }
   
   
   
   function resumeBrowse(a0100,dbname,zp_pos_id)
   {
   		window.open("/hire/employNetPortal/search_zp_position.do?b_resumeBrowse=browse&card=0&dbName="+dbname+"&a0100="+a0100+"&zp_pos_id="+zp_pos_id+"&personType=${employResumeForm.personType}","_blank","width="+(window.screen.width)+",height="+(window.screen.height)+",left=0,top=0,scrollbars=yes, resizable=no,location=no, status=no,menubar=no,toolbar=no'");
   }
	

	function showDeclare(srcobj)
	{
	 	window.open("/hire/employActualize/employResume/explain.html","_blank",'height=380,width=820,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
	}


	function synchronize()
	{
		var num=0;
   		var a0100;
   		var isMultiple=0;
   		var ids=document.getElementsByName("ids");  
  	    for(var i=0;i<document.employResumeForm.elements.length;i++)
   	    {  	   		
   			if(document.employResumeForm.elements[i].type=='checkbox'&&document.employResumeForm.elements[i].name.length>18&&document.employResumeForm.elements[i].name.substring(0,18)=='pagination.select[')
   			{   				
   				if(document.employResumeForm.elements[i].checked==true)
   				{
   					isMultiple++; 	
   					var temps=new Array();
   					if(ids[num].value.indexOf("/")!=-1)
			   			temps=ids[num].value.split("/");
			   		else 
			   			temps[0]=ids[num].value;	
   								
   					a0100+="#"+temps[0];
   				}
   				num++;
   			}
   		}
   		
   		
   		if(isMultiple==0)
   		{
   			alert(PLASE_SELECT_RECORD+"！");
   			return;
   		}
		
		var hashvo = new ParameterSet();
		hashvo.setValue("nbase","${employResumeForm.dbname}");
		var In_parameters="a0100="+a0100.substring(10); 
		var request = new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:returnInfo3,functionId:'3000000217'},hashvo);
   		
	}
  
  
  	function returnInfo3(outparamters)
  	{
  		alert(SYNCHRONOUS_COMPLETE+"!");
  	}
  	function retF()
  	{
  	    document.location="/hire/employActualize/employPosition.do?b_query=query";
  	}
  	function importResumeZip(){
  	     document.employResumeForm.action="/hire/employActualize/employResume.do?br_importzip=link";
  	     document.employResumeForm.submit();
  	}
  	document.all.date_box.size=document.all.date_box.options.length;
</script>
  </html:form>
  </body>
</html>
	
