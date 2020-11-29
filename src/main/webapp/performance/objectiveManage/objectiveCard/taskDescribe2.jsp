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
				com.hjsj.hrms.utils.ResourceFactory,
				com.hjsj.hrms.actionform.performance.objectiveManage.ObjectCardForm,
				org.apache.commons.beanutils.LazyDynaBean,
				com.hrms.struts.valueobject.UserView,
				com.hrms.struts.constant.WebConstant" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%
   String tt4CssName="ttNomal4";
   String tt3CssName="ttNomal3";
   if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
   {
      tt4CssName="tt4";
      tt3CssName="tt3";
   }
 %>
<%

	ObjectCardForm objectCardForm=(ObjectCardForm)session.getAttribute("objectCardForm");
	ArrayList taskDescribeList=objectCardForm.getTaskDescribeList();
	ArrayList leafItemList=objectCardForm.getLeafItemList();
	String   plan_objectType=objectCardForm.getPlan_objectType();
	String   status=objectCardForm.getStatus();
	String    itemKind=objectCardForm.getItemKind();
	String isTraceOrMust=objectCardForm.getIsTraceOrMust();//=3即是必填指标又是跟踪指标=1是跟踪指标=2必填指标
	String planStatus=objectCardForm.getPlanStatus();
	Hashtable planParam=objectCardForm.getPlanParam();
	String AllowLeaderTrace=(String)planParam.get("AllowLeaderTrace");//允许领导制定及批准跟踪指标, True(默认) False
	String TaskSupportAttach=(String)planParam.get("TaskSupportAttach");  //任务支持附件上传
	String objectSpFlag=objectCardForm.getObjectSpFlag();
	int  itemIndex=0;
	int  rankIndex=0;
	int  scoreIndex=0;
	int  p0400Index=0;
	String p0400="";
	String file_max_size="512";
	if(SystemConfig.getPropertyValue("appendix_size")!=null&&SystemConfig.getPropertyValue("appendix_size").trim().length()>0)
	{
		file_max_size=SystemConfig.getPropertyValue("appendix_size").trim();
		if(file_max_size.toLowerCase().indexOf("k")!=-1)
		file_max_size=file_max_size.substring(0,file_max_size.length()-1);
	}
 %>
<html>
<head>

<title>Insert title here</title>
</head>
<link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<style type="text/css">

.RecordRow 
{
	border: inset 1px #94B6E6;
	BACKGROUND-COLOR: #FFFFFF;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
}

.RecordRow_top 
{
	border: inset 1px #94B6E6;
	BACKGROUND-COLOR: #FFFFFF;
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
}

.tabHeader
{
   COLOR : #103B82;
   FONT-SIZE: 12px;
   vertical-align : bottom;
   background-image:url(/images/tabCenter.gif);
   background-position : center top;
   background-repeat: no repeat; 
   BACKGROUND-COLOR:#4680D2;
}

.TableRowTD
{
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 0pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}

</style>
<hrms:themes />
<body>
<html:form action="/performance/objectiveManage/objectiveCard" enctype="multipart/form-data">
<Br>
<table width="70%" align="center" border="0" cellpadding="0"
		cellspacing="0">
		<tr>
			<td valign="top">
					<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable">
					<tr height="20">
						<td colspan="2" align="left" class="TableRowTD common_background_color common_border_color">
					     <font class="<%=tt4CssName%>">
					     <%
					     if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt")) //中国联通
								{
								 out.println("<font class='"+tt4CssName+"'>填写|修改</font>&nbsp;");
								}
								else
								 out.println("<font class='"+tt4CssName+"'>"+ResourceFactory.getProperty("per.achivement.edittask")+"</font>&nbsp;");
					     %>
					    
						</td>
					</tr>
					<tr>
					<td class="RecordRow" colspan="2" style="border-top:0px;">
				    	${objectCardForm.desc}
				    	</td>
				    </tr>
					<% 
					  	
					  	for(int i=0;i<taskDescribeList.size();i++){
							LazyDynaBean abean=(LazyDynaBean)taskDescribeList.get(i);
							String itemid=(String)abean.get("itemid");
							String itemdesc=(String)abean.get("itemdesc");
							String value=(String)abean.get("value");
							if(itemid.equalsIgnoreCase("P0407"))
							{
								
								out.println("<tr><td align='left' valign='top' width='30%' class='RecordRow' nowrap><font class='"+tt3CssName+"'>"+itemdesc+"</font></td>");
								out.println("<td align='left'  width='70%'   class='RecordRow' nowrap><font class='"+tt3CssName+"'>");
							//	out.print(" <textarea name='taskDescribeList["+i+"].value' cols='40' rows='6' >");
								out.print(value.replaceAll("\r\n","<br>").replaceAll(" ","&nbsp;&nbsp;"));
							//	out.print("</textarea>");
								out.print("</font></td></tr>");
								break;
					  		 } 
					  	} 
					  	
					  	
					  	
					  	 for(int i=0;i<taskDescribeList.size();i++)
			             {
			           
			                 LazyDynaBean abean=(LazyDynaBean)taskDescribeList.get(i);
			                 String itemid=(String)abean.get("itemid");
			                 String fillable=(String)abean.get("fillable");
			                 String value=(String)abean.get("value");
			                 String isCalc=(String)abean.get("isCalc");
			                 if(itemid.equalsIgnoreCase("p0413"))
			                 	scoreIndex=i;
			                 if(itemid.equalsIgnoreCase("p0415"))
			                 	rankIndex=i;
			                 if(itemid.equalsIgnoreCase("p0400"))
			                 {
			                 	p0400Index=i;
			                 	p0400=value;
			                 }
							 String itemtype=(String)abean.get("itemtype");
							 String codesetid=(String)abean.get("codesetid");
							 String itemdesc=(String)abean.get("itemdesc");
							 
							 String viewvalue=(String)abean.get("viewvalue");	
							 String state=(String)abean.get("state");
							 String itemmust = (String)abean.get("itemmust");//采集指标必填
							 String noFillCollect=abean.get("noFillCollect")!=null?(String)abean.get("noFillCollect"):"0";
							 String disabledFlag = "";
							 String readonly="";
							 String idFlag = itemid;
							 if("1".equals(itemmust)){
							     disabledFlag = "disabled"; 
							     readonly="readonly='true'";
							     idFlag = itemid+"Flag";
							     if(noFillCollect.equals("1")) ////如果在system.properties里设置了参数noMustFillPoint=WZHR,TDHZ ，则此指标任务的跟踪指标即使设置了必填，也可不判断
							     	idFlag = itemid;
							 }
							 if(itemid.equalsIgnoreCase("P0413")||itemid.equalsIgnoreCase("P0415")||itemid.equalsIgnoreCase("P0407")||itemid.equalsIgnoreCase("item_id")||state.equals("0"))
							 	continue;			 
			          		 out.println("<tr><td align='left' ");
			          		  if(itemtype.equals("M"))
							 	out.print(" valign='top' ");
			          		 out.print("  width='30%' class='RecordRow' nowrap><font class='"+tt3CssName+"'>"+itemdesc+"</font></td><td align='left'  width='70%'  class='RecordRow' nowrap>");
			          		
			          		 if(itemtype.equals("A"))//字符、代码
							 {
											if(codesetid.equals("0")||codesetid.length()==0)
											{//字符
												
												out.println("<input type=\"text\"   class='inputtext' ");	
												if(itemid.equalsIgnoreCase("A0101")||itemid.equalsIgnoreCase("P0401"))
													out.print(" readOnly ");
												 if("1".equals(itemmust)){
					                                 out.print("style=\"background-color: whitesmoke\" ");
					                             }
												out.print(" name=\"taskDescribeList["+i+"].value\"  size='30' "+disabledFlag+" id=\""+idFlag+"\"   value=\""+value+"\"  />&nbsp;");
											}
											else//代码
											{
											      if(itemid.equalsIgnoreCase("score_org"))
												 {
												   	out.print(" <input type='text' name='taskDescribeList["+i+"].viewvalue'  class='inputtext' ");
												   	if("1".equals(itemmust)){
                                                        out.print("style=\"background-color: whitesmoke\" ");
                                                    }
									              	out.print(" size='30' "+disabledFlag+"  id=\""+idFlag+"\" value='"+viewvalue+"'  />");
									              	if(itemmust.equals("0")) {
									              	
												        out.print("   <img  onclick='openInputCodeDialogOrg_handwork(\"UM\",\"taskDescribeList["+i+"].viewvalue\",\"\",\"s\");' src='/images/code.gif' border=0 align=\"absmiddle\"/>&nbsp;");		
														
														out.print("<input type='hidden' value='"+value+"'  name='taskDescribeList["+i+"].value' /> &nbsp;");									
												
									              	}
												 }
												else
												{
									              	
									              	out.print(" <input type='text' "+disabledFlag+" id=\""+idFlag+"\" name='taskDescribeList["+i+"].viewvalue'  class='inputtext' ");
									              	if(itemid.equalsIgnoreCase("B0110")||itemid.equalsIgnoreCase("E0122")||itemid.equalsIgnoreCase("E01A1"))
														out.print(" readOnly ");
									              	 if("1".equals(itemmust)){
						                                 out.print("style=\"background-color: whitesmoke\" ");
						                             }
									              	out.print(" size='30' value='"+viewvalue+"'  />");
									              	if(!itemid.equalsIgnoreCase("B0110")&&!itemid.equalsIgnoreCase("E0122")&&!itemid.equalsIgnoreCase("E01A1"))
													{	
									             		out.print("<a href='javascript:");
									             		out.print("openInputCodeDialog(\""+codesetid+"\",\"taskDescribeList["+i+"].viewvalue\");' ");
									             		out.print(" >   <img  src='/images/code.gif' border=0 align=\"absmiddle\" /></a>&nbsp;");		
													}
													out.print("<input type='hidden' value='"+value+"'  name='taskDescribeList["+i+"].value' /> &nbsp;");	
												}								
											}
										
									}
									else if(itemtype.equals("D"))//日期
									{
											out.println("<input type='text'  class='inputtext'  "+disabledFlag+" id=\""+idFlag+"\" size='20'  name='taskDescribeList["+i+"].value'  value='"+value+"'  readOnly ");
										    out.print("  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' ");	
										    if("1".equals(itemmust)){
				                                 out.print("style=\"background-color: whitesmoke\" ");
				                             }
											out.print("  />&nbsp;");
									}
									else if(itemtype.equals("N"))//数字型
									{
											if((itemKind.equals("1")&&(itemid.equalsIgnoreCase("p0413")||itemid.equalsIgnoreCase("p0415")))||isCalc.equals("1"))
												out.println("<input type=\"text\"  id=\""+idFlag+"\" name=\"taskDescribeList["+i+"].value\"   value='"+value+"' readonly  size='30'  class='inputtext'  />&nbsp;");
											else
											{
												out.print("<input type=\"text\" "+disabledFlag+" id=\""+idFlag+"\" name=\"taskDescribeList["+i+"].value\" class='inputtext' ");
												 if("1".equals(itemmust)){
					                                 out.print("style=\"background-color: whitesmoke\" ");
					                             }
												if(itemid.equalsIgnoreCase("P0419"))
												{
													out.print(" onblur='validateNum(this)'  ");
												}
												out.print("  value='"+value+"'   size='30'  />&nbsp;");
											}
											if(itemid.equalsIgnoreCase("P0419"))
											{
												out.println("(0-100)");
											}									
												
									}
									else if(itemtype.equals("M"))//备注型
									{
											out.print("<textarea name=\"taskDescribeList["+i+"].value\" "+readonly+" id=\""+idFlag+"\"  ");
											 if("1".equals(itemmust)){
				                                 out.print("style=\"background-color:#dddddd\" ");
				                             }
											 out.println(" rows='5' cols='40' class='textboxMul'>"+value+"</textarea>&nbsp; ");
									}
									if(fillable!=null&&fillable.equals("1"))
									{
										out.print("&nbsp;&nbsp;<font color='red'>*</font>");
									}
									out.print("</td></tr>");
			          }
					  	%><%
					   if(TaskSupportAttach.equalsIgnoreCase("True"))//isTraceOrMust=3即是必填指标又是跟踪指标=1是跟踪指标=2必填指标
			          {
			             if(isTraceOrMust.equals("1")||isTraceOrMust.equals("3"))
			             {
			                 if(objectSpFlag.equals("03"))
			                 {
			             %>
			              <tr><td align='left' class='RecordRow' colspan='2'> <BR>
                 	     	<table border=0 ><tr><td valign='top'>
                 	   		  <bean:message key="label.zp_employ.uploadfile"/>：
                 	   		  </td>
                 	   		  </tr> 
                 	   		   <logic:iterate id="element" name="objectCardForm" property="attachList" >
                 	   		  	<tr><td>&nbsp;&nbsp;
                 	   		  	<a href='/servlet/performance/fileDownLoad?article_id=<bean:write name="element" property="id" />'  target="_blank"  border='0' >  
                 	   		  	<bean:write name="element" property="name" />
                 	   		  	</a>
                 	   		  	</td><td>  &nbsp;
                 	   		  	<% if(planStatus.equals("8")||AllowLeaderTrace.equalsIgnoreCase("true")){ %>
                 	   		  	 <a href="javascript:del(<bean:write name="element" property="id" />)">
                 	   		  		<image src='/images/del.gif' border=0 title='<bean:message key="label.zp_employ.deletefile"/>' >
                 	   		  	       </a>
                 	   		     <% } %>
                 	   		   	</td></tr>
                 	   		  </logic:iterate> 
                 	     	  </table>
			          	<% if(planStatus.equals("8")||AllowLeaderTrace.equalsIgnoreCase("true")){ %>
			          	<BR>
			          		<fieldset align="center" style="width:98%;">
    							 <legend><bean:message key="label.zp_employ.uploadfileInfo"/><%=file_max_size%>K</legend>
                 	 	  
                 	 	    &nbsp;文件名称:<input type='text'  maxLength=30  class='TEXT_NB inputtext'  size='20'  name='fileName' />
                 	 	   <Br>&nbsp;&nbsp;<input name="file" onchange='upload()' onkeydown= "if(event.keyCode==13) this.fireEvent('onchange');"  type="file" size="40">  
                 	 	     <%if(isTraceOrMust.equals("3")){ %>
                 	 	   &nbsp;&nbsp;<font color='red'>*</font>
                 	 	   <%} %> 
                 	 	   &nbsp;&nbsp;
                 	    	<br> &nbsp;
                 	    	</fieldset>
                 	    <% }%>	
                 	    	<br>&nbsp;
			          
			          
			          </td></tr>
			               <%}
			               }}
			          %>
			         
					  <tr><td align='center'  colspan='2' class='RecordRow'  style="padding-top:3px;padding-bottom:3px;"  nowrap>
					  		<Input type='button' value='<bean:message key="kq.emp.button.save"/>' onclick='checkAttach();' class="mybutton" >&nbsp;
				  	
					  		<Input type='button' value='<bean:message key="kq.search_feast.back"/>' onclick='goback();' class="mybutton" >&nbsp;
					  </td></tr>
					  	
					
</table>

<%
			for(int i=0;i<taskDescribeList.size();i++)
			{
			           
			                 LazyDynaBean abean=(LazyDynaBean)taskDescribeList.get(i);
			                 String itemid=(String)abean.get("itemid");
			                 String value=(String)abean.get("value");
			                 if(itemid.equalsIgnoreCase("p0413"))
			                 	scoreIndex=i;
			                 if(itemid.equalsIgnoreCase("p0415"))
			                 	rankIndex=i;
			                 if(itemid.equalsIgnoreCase("p0400"))
			                 {
			                 	p0400Index=i;
			                 	p0400=value;
			                 }
							 String itemtype=(String)abean.get("itemtype");
							 String codesetid=(String)abean.get("codesetid");
							 String itemdesc=(String)abean.get("itemdesc");
							 
							 String viewvalue=(String)abean.get("viewvalue");	
							 String state=(String)abean.get("state");
							 if(state.equals("0"))
		       				 {
		       					 if(!itemid.equals("item_id")&&itemid.equalsIgnoreCase("P0413")&&!itemid.equalsIgnoreCase("P0415"))
		       						 out.println("<input type='hidden' value='"+value+"'  name='taskDescribeList["+i+"].value' /> &nbsp;");	
	       					 }
	       					 if(itemid.equals("item_id")||itemid.equalsIgnoreCase("P0413")||itemid.equalsIgnoreCase("P0415"))
	       						out.println("<input type='hidden' value='"+value+"'  name='taskDescribeList["+i+"].value' /> &nbsp;");	
	      
	       }
 %>






</html:form>

<script language='javascript' >
function goback()
{
	document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query=query&opt=${objectCardForm.opt}&planid=${objectCardForm.mdplanid}&object_id=${objectCardForm.mdobject_id}";
	document.objectCardForm.submit();
}


function validateNum(obj)
{
	 var myReg =/^[0-9]*[1-9][0-9]*$/
	 if((!myReg.test(obj.value)||obj.value>100)&&obj.value!=0&&obj.value.length>0) 
	 {
			alert(OBJECTCARDINFO8+"!");
			obj.value="";
			obj.focus();
			return;
	 }
	 
}
function checkAttach()
{
	var info = "";
	<%
		for(int i=0;i<taskDescribeList.size();i++)
	    {
             LazyDynaBean abean=(LazyDynaBean)taskDescribeList.get(i);
             String itemid=(String)abean.get("itemid");
             String itemdesc=(String)abean.get("itemdesc");
             String value=(String)abean.get("value");
	%>
	var obj = document.getElementById('<%=itemid %>'+"Flag");
	if(obj != null){
		if(obj.value=='' || obj.value==null){
			info += '<%=itemdesc %>'+",";
		}
	}
	<%} %>
	info = info.substring(0,info.length-1);
	if(info.length>0){
		alert(PER_TARGET_COLLECT_ITEM+"\r\n"+info);
		return;
	}
    var hashvo=new ParameterSet();
	hashvo.setValue("status","${objectCardForm.status}");
	hashvo.setValue("p0400","<%=p0400%>");
	var status="${objectCardForm.status}";
	var In_paramters="type=1";  
	hashvo.setValue("itemType","${objectCardForm.itemKind}")
	hashvo.setValue("object_id","${objectCardForm.object_id}")
	hashvo.setValue("plan_objectType","${objectCardForm.plan_objectType}");
	hashvo.setValue("plan_id","${objectCardForm.planid}");
	hashvo.setValue("item_type","${objectCardForm.itemtype}");
	hashvo.setValue("isTraceOrMust","<%=isTraceOrMust%>");
	hashvo.setValue("objectSpFlag","<%=objectSpFlag%>");
	hashvo.setValue("AllowLeaderTrace","<%=AllowLeaderTrace%>");
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:sub,functionId:'9028000605'},hashvo);
}

function sub(outparameters)
{
	var info=outparameters.getValue("info");	
	if(info.length>0)
	{
		alert(info);
		return;
	}
	var type="1";
	<% int m=0;  %>
	<logic:iterate  id="element"    name="objectCardForm"  property="taskDescribeList" indexId="index"> 
			<logic:equal name="element" property="state"  value="1" >
			<logic:equal name="element" property="fillable"  value="1" >
				var zz=document.getElementsByName("taskDescribeList[<%=m%>].value");
				if(trim(zz[0].value).length==0)
				{
					alert("<bean:write  name="element" property="itemdesc"/>为必填项!");
					return;
				}
			
			</logic:equal>	
				<logic:equal name="element" property="itemtype" value="N">
					var a<%=m%>=document.getElementsByName("taskDescribeList[<%=m%>].value");
					if(a<%=m%>[0].value!='')
					{
					 var myReg =/^(-?\d+)(\.\d+)?$/;
					 if(!myReg.test(a<%=m%>[0].value)) 
					 {
						alert("<bean:write  name="element" property="itemdesc"/>"+PLEASEWRITENUMBER+"！");
						return;
					 }
					 }
			</logic:equal>
				<logic:equal name="element" property="itemtype" value="M">
				<logic:notEqual  name="element"  property="itemlength" value="0">
					var a<%=m%>=document.getElementsByName("taskDescribeList[<%=m%>].value");
					if(a<%=m%>[0].value!='')
					{
						if(IsOverStrLength2(a<%=m%>[0].value,<bean:write  name="element" property="itemlength"/>))
						{
							alert("<bean:write  name="element" property="itemdesc"/>"+OBJECTCARDINFO12);
							return;
						}
					}
				 </logic:notEqual>
				</logic:equal>
				
				<logic:equal name="element" property="itemtype" value="A">
				<logic:equal name="element" property="codesetid" value="0">
					var a<%=m%>=document.getElementsByName("taskDescribeList[<%=m%>].value")
					if(a<%=m%>[0].value!='')
					{
						if(IsOverStrLength(a<%=m%>[0].value,<bean:write  name="element" property="itemlength"/>))
						{
							alert("<bean:write  name="element" property="itemdesc"/>"+OBJECTCARDINFO12);
							return;
						}
					}
				</logic:equal>
				</logic:equal>
			</logic:equal>
			<% m++; %>
	</logic:iterate>
	if(type=='1')
	{
		document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_addTask2=add&operator=<%=request.getParameter("operator")%>";
	}
	else if(type=='2')
	{
		document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_addTask1=add&operator=new";
	}	
	
	document.objectCardForm.submit();
}
function upload()
{
	var strPath = document.objectCardForm.file.value;
	var f_obj = document.getElementsByName("file");					
	
	// 防止上传漏洞
	var isRightPath = validateUploadFilePath(strPath);
	if(!isRightPath)	
		return;
	
	/*
	var value=f_obj[0].value;               
    var obj=document.getElementById('FileView'); 
    if (obj != null)
    {
    	obj.SetFileName(value);
    	var facSize=obj.GetFileSize();                  
        var photo_maxsize="<%=(file_max_size)%>"   
        if(parseInt(photo_maxsize,10)>0 && parseInt(photo_maxsize,10)<parseInt(facSize,10)/1024)
        {                    
	        alert("上传文件大小超过管理员定义大小，请修正！上传文件上限"+photo_maxsize+"KB");
	        return false;
        }     
    }
    */
			
	if(trim(document.objectCardForm.fileName.value).length==0)
	{		
		if(strPath==null || trim(strPath).length==0)
			return;		
			
		var temp_url=document.objectCardForm.file.value;
		document.objectCardForm.fileName.value=temp_url.substring(temp_url.lastIndexOf ("\\")+1);
	}	
				
	document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_upAttach2=add&opt=<%=request.getParameter("opt")%>&_opt=1&_p0400=<%=p0400%>&operator=<%=request.getParameter("operator")%>";
	document.objectCardForm.submit();
}

function del(id)
{
		if(!confirm("请确认执行删除操作?"))
		{
			return;
		}
		document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_upAttach2=del&opt=<%=request.getParameter("opt")%>&article_id="+id+"&_opt=2&_p0400=<%=p0400%>&operator=<%=request.getParameter("operator")%>";
		document.objectCardForm.submit();
}	



</script>
</body>
</html>