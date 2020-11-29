<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<%@ page import="java.util.*,
				com.hjsj.hrms.utils.ResourceFactory,com.hrms.struts.taglib.CommonData,
				com.hjsj.hrms.actionform.performance.objectiveManage.ObjectCardForm,
				org.apache.commons.beanutils.LazyDynaBean,
				com.hrms.struts.valueobject.UserView,
				com.hrms.struts.constant.SystemConfig,
				java.text.DecimalFormat,
				com.hrms.struts.constant.WebConstant,
				java.io.File" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%
	String separator = File.separator;
	String file_max_size="512";
	if(SystemConfig.getPropertyValue("appendix_size")!=null&&SystemConfig.getPropertyValue("appendix_size").trim().length()>0)
	{
		file_max_size=SystemConfig.getPropertyValue("appendix_size").trim();
		if(file_max_size.toLowerCase().indexOf("k")!=-1)
		file_max_size=file_max_size.substring(0,file_max_size.length()-1);
	}

   String tt4CssName="ttNomal4";
   String tt3CssName="ttNomal3";
   String buttonnamme="保存";
   if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
   {
      tt4CssName="tt4";
      tt3CssName="tt3";
      buttonnamme="保存&返回";
   }
      	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
   
 %>
<%

	ObjectCardForm objectCardForm=(ObjectCardForm)session.getAttribute("objectCardForm");
	ArrayList taskDescribeList=objectCardForm.getTaskDescribeList();
	Hashtable planParam=objectCardForm.getPlanParam();
	String AllowLeaderTrace=(String)planParam.get("AllowLeaderTrace");//允许领导制定及批准跟踪指标, True(默认) False
	String TaskSupportAttach=(String)planParam.get("TaskSupportAttach");  //任务支持附件上传
	String scoreflag=(String)planParam.get("scoreflag");  //=2混合，=1标度(默认值=混合)
	String errorInfo = objectCardForm.getErrorInfo();
	ArrayList leafItemList=objectCardForm.getLeafItemList();
	String   plan_objectType=objectCardForm.getPlan_objectType();
	String   status=objectCardForm.getStatus();
	String    itemKind=objectCardForm.getItemKind();//编辑的目标的项目属性  1:共性  2：个性
	String item_type=objectCardForm.getItemtype();//=1加扣分指标只显示2项=0普通指标=1加扣分指标
	ArrayList editableTaskList=objectCardForm.getEditableTaskList();
	String objectSpFlag=objectCardForm.getObjectSpFlag();
	String p0400=objectCardForm.getP0400();
	String fromflag=objectCardForm.getFromflag();
	String isTraceOrMust=objectCardForm.getIsTraceOrMust();
	String planStatus=objectCardForm.getPlanStatus();
	
	String objectCardGradeMembersRater=objectCardForm.getObjectCardGradeMembersRater();
	int  itemIndex=0;
	int  rankIndex=0;
	int  scoreIndex=0;
	int  p0400Index=0;
	String rankmusterfill="0";
	String scoremusterfill="0";
 %>
<html>
<head>
		<script type="text/javascript" src="../../../components/personPicker/PersonPicker.js"></script>
		<SCRIPT LANGUAGE=javascript src="/performance/objectiveManage/objectiveCard/objectiveCard.js"></SCRIPT>
		<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
		<script language="JavaScript" src="/js/wz_tooltip.js"></script>
		<script language="JavaScript" src="/js/function.js"></script>
		<script language="javascript" src="/ajax/command.js"></script>
		<script language="javascript" src="/ajax/constant.js"></script>
        <script language="JavaScript" src="/js/constant.js"></script>
<title>Insert title here</title>
</head>

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

.RecordRow_right_top {
    border: inset 1px #C4D8EE;
    border-bottom: #C4D8EE 1pt solid;
    border-left: 0pt;
    border-right: #C4D8EE 1pt solid;
    border-top: #C4D8EE 0pt solid;
    font-size: 12px;
    border-collapse: collapse;
    height: 30px;
    padding: 0 5px 0 5px;
}

.RecordRow_left_top {
    border: inset 1px #C4D8EE;
    border-bottom: #C4D8EE 1pt solid;
    border-left: #C4D8EE 1pt solid;
    border-right: 0pt;
    border-top: #C4D8EE 0pt solid;
    font-size: 12px;
    border-collapse: collapse;
    height: 30px;
    padding: 0 5px 0 5px;
}

</style>
<link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<hrms:themes />
<style>
.RecordRow_nol{
	border-top:none !important;
}
.RecordRow_nor{
	border-top:none !important;
}
.RecordRow_objInfo{
	border-bottom:none !important;
}
</style>
<script language='javascript' >
 var IVersion=getBrowseVersion();
 var ViewProperties=new ParameterSet();
 if(IVersion==8){
     document.writeln("<link href=\"/performance/objectiveManage/objectiveCard/objectiveCard_8.css\" rel=\"stylesheet\" type=\"text/css\">");
   }
   else{
     document.writeln("<link href=\"/performance/objectiveManage/objectiveCard/objectiveCard.css\" rel=\"stylesheet\" type=\"text/css\">");
   }
<% if(errorInfo!=null && errorInfo.length()>0){%>
	 alert("<%=errorInfo%>");
<%}%>
 
</script>
  <% 
  String scrollHeight=request.getParameter("scrollHeight");//刷新页面后定位 滚动条位置，是页面保持原先的滚动条位置 zzk

  if(scrollHeight!=null){
%> 
<body onload="Wdiv.scrollTop=<%=scrollHeight%>;">   

<%  
 }else{%>
 <body >
 <%} %>
<form name="objectCardForm" method="post" action="/performance/objectiveManage/objectiveCard.do" enctype="multipart/form-data" >
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>

<table width="90%" align="center" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top">
				<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
					<tr >
						<td colspan="2" align="left" class="TableRowTD common_background_color common_border_color">
						<% if(request.getParameter("operator")!=null&&request.getParameter("operator").equals("edit"))
							{
								if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt")) //中国联通
								{
								out.println("<font class='"+tt4CssName+"'>填写|修改</font>&nbsp;");
								}
								else
									out.println("<font class='"+tt4CssName+"'>"+ResourceFactory.getProperty("per.achivement.edittask")+"</font>&nbsp;");
							}
							else
								out.println("<font class='"+tt4CssName+"'>"+ResourceFactory.getProperty("per.achivement.newtask")+"</font>&nbsp;");
						
						%>
						</td>
					</tr>
					<tr>
						<td colspan="2" align="left"  style="border-top:0px;border-bottom:none;">
					${objectCardForm.desc}
					</td>
					</tr>
<script language='javascript' >	
if(IVersion==9){
  document.writeln("<tr><td class='RecordRow_notop common_border_color' colspan='2'>");
}
else
{
  document.writeln("<tr><td class='RecordRow_notop common_border_color' colspan='2'>");
}	 		
					
</script>			<!-- 【5391】绩效管理：目标考核自助用户在进行目标卡的制定的时候点击新建按钮，编辑目标任务界面线粗   jingq upd 2015.01.09 -->		
					<div id="Wdiv" style="overflow:auto;width:100%;height:400px;valign:top;border-top:1px solid;" class="common_border_color" >
					<table class="ListTable" width="100%" align="center">
					<%
					String display="";
					if(request.getParameter("operator")!=null&&request.getParameter("operator").equals("edit"))
					{
					   display="style='display=none'";
					}
					 for(int i=0;i<taskDescribeList.size();i++){
							LazyDynaBean abean=(LazyDynaBean)taskDescribeList.get(i);
							String itemid=(String)abean.get("itemid");
							String itemdesc=(String)abean.get("itemdesc");
							String value=(String)abean.get("value");
							
							if(itemid.equals("item_id"))
							{
								itemIndex=i;
								out.println("<tr "+display+"><td align='right' width='20%' class='RecordRow_right_top' nowrap><font class='"+tt3CssName+"'>"+ResourceFactory.getProperty("per.achivement.itemclassify")+"</font>&nbsp;</td>");
								out.println("<td align='left'  width='80%'  class='RecordRow_left_top' nowrap>");
								out.print("<table><tr><td><select");
								if(item_type.equals("0"))
							    	out.print(" onchange='showScope()' ");
								out.println(" name='taskDescribeList["+i+"].value' style='width:370px'>");
								for(int j=0;j<leafItemList.size();j++)
								{
									LazyDynaBean aa_bean=(LazyDynaBean)leafItemList.get(j);
									String item_id=(String)aa_bean.get("item_id");
									String kind=(String)aa_bean.get("kind");
									if(itemKind.equals("1"))
									{
										if(value.equals(item_id))
										{
											out.println("<option value='"+item_id+"' ");
											out.print(" >"+(String)aa_bean.get("itemdesc"));
											out.print("</option>");
										}
									}
									else
									{
										if(kind.equals("2"))
										{
											String rank=(String)aa_bean.get("rank");
											String score=(String)aa_bean.get("score");
											out.println("<option value='"+item_id+"' ");
											if(value.equals(item_id))
												out.print(" selected ");
											out.print(" >"+(String)aa_bean.get("itemdesc"));
											if(status.equals("0"))
											{
												if(!score.trim().equals("0"))
													out.print(" ("+ResourceFactory.getProperty("jx.param.mark")+":"+score+")");
											}
											else if(status.equals("1"))
											{
											   
												if(rank==null||rank.trim().length()==0)
													rank="0";
												else
													rank=rank.trim();
												DecimalFormat myformat1 = new DecimalFormat("########.####");//
												String temp=myformat1.format(Double.parseDouble(rank)*100);
												if(!temp.trim().equals("0"))
													out.print(" ("+ResourceFactory.getProperty("label.kh.template.qz")+":"+temp+"%)");
											}
											out.print("</option>");
										}
									}
								}
								out.print("</select></td><td><div id='scope' ></div></td></tr></table>        </td></tr>");
								break;
					  		 } 
					  	} 
					  	if(request.getParameter("operator")!=null&&request.getParameter("operator").equals("edit"))
					  	{
					  	    out.println("<tr><td align='right' width='20%' class='RecordRow_right' nowrap><font class='"+tt3CssName+"'>任务</font>&nbsp;</td>");
							out.println("<td align='left'  width='80%'  class='RecordRow_left' nowrap>");
							out.print("<table><tr><td><select name='sel' id='editList' onchange='changeSelect();' style='width:370px'>");
							for(int j=0;j<editableTaskList.size();j++)
							{
							    CommonData cd=(CommonData)editableTaskList.get(j);
							    out.println("<option value='"+cd.getDataValue()+"' ");
							    if(p0400.equalsIgnoreCase(cd.getDataValue()))
							        out.println(" selected ");
								out.print(" >"+cd.getDataName());
								out.print("</option>");
							}
							out.print("</select></td><td><div id='scope1' ></div></td></tr></table> </td></tr>");
					  	}
					  	for(int i=0;i<taskDescribeList.size();i++){
							LazyDynaBean abean=(LazyDynaBean)taskDescribeList.get(i);
							String itemid=(String)abean.get("itemid");
							if(itemid.equalsIgnoreCase("P0407"))
							{
								String itemdesc=(String)abean.get("itemdesc");
								String value=(String)abean.get("value");
								StringBuffer buf = new StringBuffer();
								 if(itemdesc.length()>10)
								 {
								     int ss=itemdesc.length()/10+1;
								     int index=0;
								     for(int j=1;j<=ss;j++)
								     {
								       if(itemdesc.length()>(index+1)*10)
								           buf.append(itemdesc.substring(index*10,(index+1)*10)+"<br>");
								       else
								           buf.append(itemdesc.substring(index*10,itemdesc.length()));
								       index++;
								     }
								 }
								 else
								 {
								    buf.append(itemdesc);
								 }		 

								
								out.println("<tr><td align='right' valign='middle' width='20%' class='RecordRow_right' nowrap><font class='"+tt3CssName+"'>"+buf.toString()+"</font>&nbsp;</td>");
								out.println("<td align='left'  width='80%'  class='RecordRow_left' nowrap>");
								out.print(" <textarea name='taskDescribeList["+i+"].value' cols='50' style='margin:1px 0 1px 0;' ");
								if((itemKind.equals("1")||item_type.equals("3")||!fromflag.equals("1"))&&itemid.equalsIgnoreCase("p0407"))
												out.print(" readonly ");
								out.print("  rows='6' >");
								out.print(value);
								out.print("</textarea>");
								out.print("</td></tr>");
								break;
					  		 }
					  	}
					  	for(int i=0;i<taskDescribeList.size();i++){
							LazyDynaBean abean=(LazyDynaBean)taskDescribeList.get(i);
							String itemid=(String)abean.get("itemid");
							if(itemid.equalsIgnoreCase("rater"))
							{
								String itemdesc=(String)abean.get("itemdesc");
								String value=(String)abean.get("value");	 
								out.println("<tr><td align='right' valign='middle' width='20%' class='RecordRow_right' nowrap><font class='"+tt3CssName+"'>评价人</font>&nbsp;</td>");
								out.println("<td align='left'  width='80%'  class='RecordRow_left' nowrap>");
								out.print(" <input type='text' class='inputtext' onmouseover='this.title=this.value' size ='30' readonly='true' id='ratername' name=\"taskDescribeList["+i+"].value\" value='"+value+"' />");
								out.print("<input type='hidden' id='raterid' name='objectCardGradeMembersRater' value='"+objectCardGradeMembersRater+"'/> ");
								out.print("<img src=\"/images/code.gif\" onclick=\"showPersonPicker()\" style=\"vertical-align: middle;\">");
								out.print("</td></tr>");
								break;
					  		 }
					  	}
					  	
					  	
					  	 for(int i=0;i<taskDescribeList.size();i++)
			             {
			           
			                 LazyDynaBean abean=(LazyDynaBean)taskDescribeList.get(i);
			                 String itemid=(String)abean.get("itemid");
			                 String fillable=(String)abean.get("fillable");
			                 
			                 if(itemid.equalsIgnoreCase("p0413"))
			                 {
			                 	scoreIndex=i;
			                 	scoremusterfill=fillable;
			                 }
			                 if(itemid.equalsIgnoreCase("p0415"))
			                 {
			                 	rankIndex=i;
			                 	rankmusterfill=fillable;
			                 }
			                 if(itemid.equalsIgnoreCase("p0400"))
			                 	p0400Index=i;
							 String itemtype=(String)abean.get("itemtype");
							 String codesetid=(String)abean.get("codesetid");
							 String isCalc=(String)abean.get("isCalc");
							 String itemdesc=(String)abean.get("itemdesc");
							 StringBuffer buf = new StringBuffer();
							 if(itemdesc.length()>20)
							 {
							     int ss=itemdesc.length()/20+1;
							     int index=0;
							     for(int j=1;j<=ss;j++)
							     {
							       if(itemdesc.length()>(index+1)*20)
							           buf.append(itemdesc.substring(index*20,(index+1)*20)+"<br>");
							       else
							           buf.append(itemdesc.substring(index*20,itemdesc.length()));
							       index++;
							     }
							 }
							 else
							 {
							    buf.append(itemdesc);
							 }		 
							 String value=(String)abean.get("value");
							 String viewvalue=(String)abean.get("viewvalue");	
							 String state=(String)abean.get("state");
							 if(itemid.equalsIgnoreCase("P0407")||itemid.equalsIgnoreCase("item_id")||state.equals("0"))
							 	continue;	
							 		 
			          		 out.println("<tr"+(item_type.equals("1")?" style=\"display=none\"":"")+"><td align='right' ");
			          		  if(itemtype.equals("M"))
							 	out.print(" valign='middle' ");
			          		 out.print("  width='20%' class='RecordRow_right' nowrap><font class='"+tt3CssName+"'>"+buf.toString()+"&nbsp;</td><td align='left'  width='80%'  class='RecordRow_left' nowrap>");
			          		
			          		 if(itemtype.equals("A"))
							 {
											if(codesetid.equals("0")||codesetid.length()==0)
											{
												
												out.println("<input type=\"text\"  ");	
												if(itemid.equalsIgnoreCase("A0101")||itemid.equalsIgnoreCase("P0401"))
													out.print(" readOnly ");							
												
												out.print(" name=\"taskDescribeList["+i+"].value\"  size='30'   value=\""+value+"\"  />&nbsp;");
											}
											else
											{
									              if(itemid.equalsIgnoreCase("score_org"))
												 {
												   	out.print(" <input type='text'  class='inputtext' id='score_org_view' onchange='checkUM();' name='taskDescribeList["+i+"].viewvalue' ");
									              
									              	out.print(" size='30' value='"+viewvalue+"'  />");
									              	
											        out.print("   <img  onclick='openInputCodeDialogOrg_handwork(\"UM\",\"taskDescribeList["+i+"].viewvalue\",\"\",\"s\");' src='/images/code.gif' border=0 align=\"absmiddle\"/>&nbsp;");		
													
													out.print("<input type='hidden'  id='score_org_hidden' value='"+value+"'  name='taskDescribeList["+i+"].value' /> &nbsp;");									
												}
												else{
									              	out.print(" <input type='text'   class='inputtext'  name='taskDescribeList["+i+"].viewvalue' ");
									              	if(itemid.equalsIgnoreCase("B0110")||itemid.equalsIgnoreCase("E0122")||itemid.equalsIgnoreCase("E01A1"))
														out.print(" readOnly ");
									              	
									              	out.print(" size='30' value='"+viewvalue+"'  />");
									              	if(!itemid.equalsIgnoreCase("B0110")&&!itemid.equalsIgnoreCase("E0122")&&!itemid.equalsIgnoreCase("E01A1"))
													{	
									                   out.print("   <img  onclick='openInputCodeDialog(\""+codesetid+"\",\"taskDescribeList["+i+"].viewvalue\");' src='/images/code.gif' border=0 align=\"absmiddle\"/>&nbsp;");		
													}
													out.print("<input type='hidden' value='"+value+"'  name='taskDescribeList["+i+"].value' /> &nbsp;");									
									    		}
											
											}
										
									}
									else if(itemtype.equals("D"))
									{
											out.println("<input type='text'   class='inputtext'  size='20'  name='taskDescribeList["+i+"].value'  value='"+value+"'  onkeydown='checkKeyCode();' ");
										    out.print("  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' ");	
											out.print("  />&nbsp;");
									}
									else if(itemtype.equals("N"))
									{
											if(itemKind.equals("1")&&(itemid.equalsIgnoreCase("p0413")||itemid.equalsIgnoreCase("p0415")))
												out.println("<input type=\"text\"   class='inputtext'  name=\"taskDescribeList["+i+"].value\"   value='"+value+"'    size='30'  />&nbsp;");
											else
											{
												out.print("<input type=\"text\"    class='inputtext'  name=\"taskDescribeList["+i+"].value\" ");
												if(itemid.equalsIgnoreCase("P0419"))
												{
													out.print(" onblur='validateNum(this)'  ");
												}
												out.print("  value='"+value+"'   size='30'  ");
												if(isCalc.equals("1"))
												    out.print(" readonly");
												out.print("/>&nbsp;");
											}
											if(itemid.equalsIgnoreCase("P0419"))
											{
												out.println("(0-100)");
											}									
												
									}
									else if(itemtype.equals("M"))
									{
											out.println("<textarea name=\"taskDescribeList["+i+"].value\" rows='5' style='margin:1px 0 1px 0;' cols='50' class='textboxMul'>"+value+"</textarea>&nbsp;");
									}
									
									if(itemid.equalsIgnoreCase("P0415"))
										out.print("%");
									
									
									if(fillable!=null&&fillable.equals("1"))
									{
										out.print("&nbsp;&nbsp;<font color='red'>*</font>");
									}
									
									out.print("</td></tr>");
			          }
			          if(item_type.equalsIgnoreCase("1")){
			             out.println("<tr><td align='left' class='RecordRow_top' colspan='2'>&nbsp;</td></tr>");
			          }
			          
			          
			          if(TaskSupportAttach.equalsIgnoreCase("True"))
			          {
			            if(isTraceOrMust.equals("0")||isTraceOrMust.equals("2"))//isTraceOrMust=3即是必填指标又是跟踪指标=1是跟踪指标=2必填指标=0普通指标
			               {
			          %>
			          <tr><td align='left' class='RecordRow_top common_border_color' colspan='2'> <BR>
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
                 	   		  	<% if(objectSpFlag.equals("01")||objectSpFlag.equals("07")||objectSpFlag.equals("02")){ %>
                 	   		  	 <a href="javascript:del(<bean:write name="element" property="id" />)">
                 	   		  		<image src='/images/del.gif' border=0 title='<bean:message key="label.zp_employ.deletefile"/>' >
                 	   		  	       </a>
                 	   		     <% } %>
                 	   		   	</td></tr>
                 	   		  </logic:iterate> 
                 	     	  </table>
			          	<% if(objectSpFlag.equals("01")||objectSpFlag.equals("07")||objectSpFlag.equals("02")){ %>
			          	<BR>
			          		<fieldset align="left" style="width:98%;">
    							 <legend><bean:message key="label.zp_employ.uploadfileInfo"/><%=file_max_size%>K</legend>
                 	 	  
                 	 	    &nbsp;文件名称:<input type='text'  maxLength=30  class='TEXT_NB'  size='20'  name='fileName' />
                 	 	   &nbsp;&nbsp;<input name="file" onchange='upload()' onkeydown= "if(event.keyCode==13) this.fireEvent('onchange');"  type="file" size="40">  
                 	 	    <%if(isTraceOrMust.equals("2")){ %>
                 	 	   &nbsp;&nbsp;<font color='red'>*</font>
                 	 	   <%} %> 
                 	 	   &nbsp;&nbsp;
                 	    	<br> &nbsp;
                 	    	</fieldset>
                 	    <% } %>	
                 	    	<br>&nbsp;
			          
			          
			          </td></tr>
					  <%			          
			          }
			          }
					  %>
					  	</table>
					  	</div>
					  	</td>
					  	</tr>
					  <tr><td align='center' valign="middle"  colspan='2' class='RecordRow' style="padding-top:3px;padding-bottom:3px;" nowrap>
					  		<Input type='button' value='<%=buttonnamme%>' onclick='sub(1)' class="mybutton" >
					  		<% if(request.getParameter("operator")!=null&&request.getParameter("operator").equals("new")){ %>
					  		<Input type='button' value='<bean:message key="kq.emp.button.save"/>＆<bean:message key="edit_report.continue"/>' onclick='sub(2)'  class="mybutton" >
					  		<% } else{%>
					  		 <Input type='button' value='<bean:message key="kq.emp.button.save"/>＆<bean:message key="edit_report.continue"/>' onclick='sub(3);'  class="mybutton" >
					  		<%} %>
					  		<Input type='button' value='<bean:message key="kq.search_feast.back"/>' onclick='goback()' class="mybutton" >
					 <html:hidden name="objectCardForm" property="itemtype"/>
					  </td></tr>
					  	
					
</table>

<%
			for(int i=0;i<taskDescribeList.size();i++)
			{
			           
			                 LazyDynaBean abean=(LazyDynaBean)taskDescribeList.get(i);
			                 String itemid=(String)abean.get("itemid");
			                 String fillable=(String)abean.get("fillable");
			                 if(itemid.equalsIgnoreCase("p0413"))
			                 {
			                 	scoreIndex=i;
			                 	scoremusterfill=fillable;
			                 }
			                 if(itemid.equalsIgnoreCase("p0415"))
			                 {
			                 	rankIndex=i;
			                 	rankmusterfill=fillable;
			                 }
			                 if(itemid.equalsIgnoreCase("p0400"))
			                 	p0400Index=i;
							 String itemtype=(String)abean.get("itemtype");
							 String codesetid=(String)abean.get("codesetid");
							 String itemdesc=(String)abean.get("itemdesc");
							 String value=(String)abean.get("value");
							 String viewvalue=(String)abean.get("viewvalue");	
							 String state=(String)abean.get("state");
							 if(state.equals("0"))
		       					 out.println("<input type='hidden' value='"+value+"'  name='taskDescribeList["+i+"].value' /> &nbsp;");	
	       					
	       }
 %>

</form>

<script language='javascript' >
var scoreflag='<%=scoreflag%>';
var scorefill="<%=scoremusterfill%>";
var rankfill="<%=rankmusterfill%>";
function TaskIsOverStrLength2(str,len)
{
	var inputlengh=0;
	for(var i=0;i<str.length;i++)
	{
		if(str.charCodeAt(i)>255)
			inputlengh+=2;
		else
			inputlengh++;
	}
   return inputlengh>len
   
}
function TaskIsOverStrLength(str,len)
{
	var inputlengh=0;
	var tempstr=str.replace(/[^\x00-\xff]/g,"**");
	for(var i=0;i<tempstr.length;i++)
	{
		if(tempstr.charCodeAt(i)>255)
			inputlengh+=2;
		else
			inputlengh++;
	}
   return inputlengh>len
   
}
//判断超出了几个字符
function isLengthOverControl2(str,len)
{
	var inputlengh=0;
	for(var i=0;i<str.length;i++)
	{
		if(str.charCodeAt(i)>255)
			inputlengh+=2;
		else
			inputlengh++;
	}
	var overlengh=inputlengh-len;
	var returnstr="";
	returnstr="指标可录入"+len+"个字符，现已超出"+overlengh+"个";
	return returnstr;
}
function isLengthOverControl(str,len)
{
	var inputlengh=0;
	var tempstr=str.replace(/[^\x00-\xff]/g,"**");
	for(var i=0;i<tempstr.length;i++)
	{
		if(tempstr.charCodeAt(i)>255)
			inputlengh+=2;
		else
			inputlengh++;
	}

	var overlengh=inputlengh-len;
	var returnstr="";
	returnstr="指标可录入"+len+"个字符，现已超出"+overlengh+"个";
	return returnstr;
	
}
function checkKeyCode()
{
   var code=window.event.keyCode;
  if(code==8||code==46)
  {
  }
  else{
     window.event.returnValue=false;
  }
}
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


function showScope()
{
	var objs=document.getElementsByName("taskDescribeList[<%=itemIndex%>].value");
	var p0400objs=document.getElementsByName("taskDescribeList[<%=p0400Index%>].value");
	var hashvo=new ParameterSet();
	hashvo.setValue("item_id",objs[0].value);
	hashvo.setValue("status","${objectCardForm.status}");
	hashvo.setValue("p0400",p0400objs[0].value);
	hashvo.setValue("object_id","${objectCardForm.object_id}")
	hashvo.setValue("plan_objectType","${objectCardForm.plan_objectType}");
	hashvo.setValue("plan_id","${objectCardForm.planid}");
	
	var request=new Request({method:'post',asynchronous:false,onSuccess:writeDesc,functionId:'9028000612'},hashvo);
}

function writeDesc(outparamters)
{
		var info=outparamters.getValue("info");	
		var obj=document.getElementById("scope");
		obj.innerHTML="&nbsp;&nbsp;&nbsp;&nbsp;"+info;
		var obj1=document.getElementById("scope1");
		if(obj1)
			obj1.innerHTML="&nbsp;&nbsp;&nbsp;&nbsp;"+info;
}
 
function sub(type)
{
	var objs=document.getElementsByName("taskDescribeList[<%=itemIndex%>].value");
	var rankobjs=document.getElementsByName("taskDescribeList[<%=rankIndex%>].value");
	var scoreobjs=document.getElementsByName("taskDescribeList[<%=scoreIndex%>].value");
	var p0400objs=document.getElementsByName("taskDescribeList[<%=p0400Index%>].value");
	
	if(trim(objs[0].value).length==0)
	{
		alert("无项目分类不能保存!");
		return;
	}	
    var hashvo=new ParameterSet();
	hashvo.setValue("item_id",objs[0].value);
	hashvo.setValue("status","${objectCardForm.status}");
	hashvo.setValue("p0400",p0400objs[0].value);
	var status="${objectCardForm.status}";
	if(status=='0')
	{
		if(scoreobjs[0].value.length==0&&scorefill=='0')
			scoreobjs[0].value=0;
		/*	
		if(scoreflag=='4')	
		{
			if(scoreobjs[0].value.search("^-?\\d+$")!=0)
			{
				scoreobjs[0].value="";
				scoreobjs[0].focus();
				alert(OBJECTCARDINFO9);
				return;
			}
		}
		else
		{
			if(!checkIsNum(scoreobjs[0].value))
			{
				scoreobjs[0].value="";
				scoreobjs[0].focus();
				alert(OBJECTCARDINFO9);
				return;
			}
		}*/
		hashvo.setValue("score",scoreobjs[0].value);
	}
	if(status=='1')
	{
		if(rankobjs[0].value.length==0&&rankfill=='0')
			rankobjs[0].value=0;
		/*
		if(!checkIsNum(rankobjs[0].value))
		{
			rankobjs[0].value="";
			rankobjs[0].focus();
			alert(OBJECTCARDINFO10+"!");
			return;
		}*/
		hashvo.setValue("rank",rankobjs[0].value);
	}
	var In_paramters="type="+type;  
	hashvo.setValue("itemType","${objectCardForm.itemKind}")
	hashvo.setValue("object_id","${objectCardForm.object_id}")
	hashvo.setValue("plan_objectType","${objectCardForm.plan_objectType}");
	hashvo.setValue("plan_id","${objectCardForm.planid}");
	hashvo.setValue("item_type","${objectCardForm.itemtype}");
	hashvo.setValue("isTraceOrMust","<%=isTraceOrMust%>");
	hashvo.setValue("objectSpFlag","<%=objectSpFlag%>");
	hashvo.setValue("AllowLeaderTrace","<%=AllowLeaderTrace%>");
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'9028000605'},hashvo);
}	
	

function returnInfo(outparamters)
{
	var info=outparamters.getValue("info");	
	var type=outparamters.getValue("type");  //1:保 存   2:保存继续
	var item_type=outparamters.getValue("item_type");
	if(info.length>0)
	{
		alert(info);
		return;
	}
	<% int m=0;  %>
	if(item_type=='0'||item_type=='3')
	{
	<logic:iterate  id="element"    name="objectCardForm"  property="taskDescribeList" indexId="index"> 
			<logic:equal name="element" property="itemid"  value="P0407" >
				var zz=document.getElementsByName("taskDescribeList[<%=m%>].value")
				if(trim(zz[0].value).length==0)
				{
					alert(OBJECTCARDINFO11+"!");
					return;
				}
					<logic:notEqual  name="element"  property="itemlength" value="0">
						<logic:notEqual  name="element"  property="itemlength" value="10">
							var a<%=m%>=document.getElementsByName("taskDescribeList[<%=m%>].value")
							if(a<%=m%>[0].value!='')
							{
								if(TaskIsOverStrLength2(a<%=m%>[0].value,<bean:write  name="element" property="itemlength"/>))
								{
									var str=isLengthOverControl2(a<%=m%>[0].value,<bean:write  name="element" property="itemlength"/>);
									alert("<bean:write  name="element" property="itemdesc"/>"+str);
									return;
								}
							}
					    </logic:notEqual>
				    </logic:notEqual>
			</logic:equal>
			<logic:equal name="element" property="itemid"  value="p0407" >
				var zz=document.getElementsByName("taskDescribeList[<%=m%>].value")
				if(trim(zz[0].value).length==0)
				{
					alert(OBJECTCARDINFO11+"!");
					return;
				}
					<logic:notEqual  name="element"  property="itemlength" value="0">
						<logic:notEqual  name="element"  property="itemlength" value="10">
							var a<%=m%>=document.getElementsByName("taskDescribeList[<%=m%>].value")
							if(a<%=m%>[0].value!='')
							{
								if(TaskIsOverStrLength2(a<%=m%>[0].value,<bean:write  name="element" property="itemlength"/>))
								{
									var str=isLengthOverControl2(a<%=m%>[0].value,<bean:write  name="element" property="itemlength"/>);
									alert("<bean:write  name="element" property="itemdesc"/>"+str);
									return;
								}
							}
					    </logic:notEqual>
				    </logic:notEqual>
			</logic:equal>
			<logic:equal name="element" property="state"  value="1" >
			<logic:equal name="element" property="fillable"  value="1" >
				var zz=document.getElementsByName("taskDescribeList[<%=m%>].value")
				var _flag=true;
				<%
				
				if(status.equals("1"))
				{
				%>
					var zz1=document.getElementsByName("taskDescribeList[<%=rankIndex%>].value")
					if(zz1&&zz1[0].value*1==0)
						_flag=false;
				<%
				}	
				%>
				if(trim(zz[0].value).length==0)
				{
					alert("<bean:write  name="element" property="itemdesc"/>为必填项!");
					return;
				}
			
			</logic:equal>
			</logic:equal>
			<logic:equal name="element" property="state"  value="1" >	
				<logic:equal name="element" property="itemtype" value="N">
					var a<%=m%>=document.getElementsByName("taskDescribeList[<%=m%>].value")
					var decimalwidth=<bean:write  name="element" property="decimalwidth"/>
					var lenth=<bean:write  name="element" property="itemlength"/>
					
					var itemid='<bean:write  name="element" property="itemid"/>';
					if(a<%=m%>[0].value!='')
					{
						 var myReg =/^(-?\d+)(\.\d+)?$/
						 if(!myReg.test(a<%=m%>[0].value)) 
						 {
							alert("<bean:write  name="element" property="itemdesc"/>"+PLEASEWRITENUMBER+"！");
							return;
						 }
						 
						 if(itemid.toLowerCase()!='p0415')
						 {
							 if(decimalwidth==0)
							 {
							 	if(a<%=m%>[0].value*1>2147483647||a<%=m%>[0].value*1<-2147483648)
							 	{
							 		alert("<bean:write  name="element" property="itemdesc"/>"+"超出了整型数值范围!");
							 		return;
							 	}
							 	if(a<%=m%>[0].value.indexOf(".")!=-1)
							 	{
							 	    alert("<bean:write  name="element" property="itemdesc"/>"+"请输入整数!");
							 		return;
							 	}
							 }
							 else
							 {
							 	if(a<%=m%>[0].value.indexOf(".")!=-1)
							 	{
							 		var t=a<%=m%>[0].value.substring(0,a<%=m%>[0].value.indexOf("."));
							 		var t1=a<%=m%>[0].value.substring(a<%=m%>[0].value.indexOf(".")+1)
							 		if(t.length>lenth)
							 		{
							 			alert("<bean:write  name="element" property="itemdesc"/>"+"超出了定义的数值范围!");
							 			return;
							 		}
							 		if(t1.length>decimalwidth)
							 		{
							 			alert("<bean:write  name="element" property="itemdesc"/>"+"超出了定义的数值范围!");
							 			return;
							 		}
							 	}
							 	else
							 	{
							 		if(a<%=m%>[0].value.length>lenth)
							 		{
							 			alert("<bean:write  name="element" property="itemdesc"/>"+"超出了定义的数值范围!");
							 			return;
							 		}
							 	}
							 }
						 }
						 
					 }
				</logic:equal>
				
					<logic:equal name="element" property="itemtype" value="M">
					<logic:notEqual  name="element"  property="itemlength" value="0">
						var a<%=m%>=document.getElementsByName("taskDescribeList[<%=m%>].value")
						if(a<%=m%>[0].value!='')
						{
							if(TaskIsOverStrLength2(a<%=m%>[0].value,<bean:write  name="element" property="itemlength"/>))
							{
								var str=isLengthOverControl2(a<%=m%>[0].value,<bean:write  name="element" property="itemlength"/>);
								alert("<bean:write  name="element" property="itemdesc"/>"+str);
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
						if(TaskIsOverStrLength(a<%=m%>[0].value,<bean:write  name="element" property="itemlength"/>))
						{
							var str=isLengthOverControl(a<%=m%>[0].value,<bean:write  name="element" property="itemlength"/>);
							alert("<bean:write  name="element" property="itemdesc"/>"+str);
							return;
						}
					}
				</logic:equal>
				</logic:equal>
			</logic:equal>
			<% m++; %>
	</logic:iterate>
	} 
	if(type=='1')
	{
		document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_addTask2=add&operator=<%=request.getParameter("operator")%>";
	   document.objectCardForm.submit();
	}
	else if(type=='2')
	{
		var objs=document.getElementsByName("taskDescribeList[<%=itemIndex%>].value");
		document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_addTask1=add&itemtype="+item_type+"&operator=new&itemid="+objs[0].value;
	    document.objectCardForm.submit();
	}else if(type=='3')
	{
	   editContinue();
	}
	
}
function editContinue()
{
    var p0400="";
    var index=0;
    var obj=document.getElementById("editList");
    for(var i=0;i<obj.options.length;i++)
    {
       if(obj.options[i].selected)
       {
         p0400=obj.options[i].value;
         index=i;
       }
    }
  document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_addTask1=add&operator=edit&index="+index;
  document.objectCardForm.target="_self";
  document.objectCardForm.submit();
}
function changeSelect()
{
    var p0400="";
    var obj=document.getElementById("editList");
    for(var i=0;i<obj.options.length;i++)
    {
       if(obj.options[i].selected)
       {
         p0400=obj.options[i].value;
       }
    }
    document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_showTaskDesc=show&operator=edit&p0400="+p0400;
 	document.objectCardForm.submit();
}
<% if(itemKind.equals("2")&&item_type.equals("0")){ %>
<% } %>
showScope();//原来在上面if里面，放开了  zhaoxg add 2016-8-20
 
function upload()
{
	var strPath = document.objectCardForm.file.value;
	var f_obj = document.getElementsByName("file");
	var p0400objs=document.getElementsByName("taskDescribeList[<%=p0400Index%>].value"); 						
	
	// 防止上传漏洞
	var isRightPath = validateUploadFilePath(strPath);
	if(!isRightPath)	
		return;
	//控制上传时的文件名的长度在20，否则不能上传 haosl 2017-11-14
	var fileName=document.objectCardForm.file.value;
	var fileName=fileName.substring(fileName.lastIndexOf('\\')+1,fileName.lastIndexOf('.'));
	var strLen = getStrLength(fileName);
	//与其他页面保持一致，限制为40个字节。
	if(strLen>40){
   	    alert("文件名的长度不能大于40个字节（一个汉字占两个字节）！");
        return false;
    }
	
	
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
    var scrollHeight=document.getElementById("Wdiv").scrollHeight;		
	document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_upAttach=add&operator=<%=request.getParameter("operator")%>&_opt=1&_p0400="+p0400objs[0].value+"&scrollHeight="+scrollHeight;
	document.objectCardForm.submit();
}
function getStrLength(str){
    var n=str.replace(/[^\u0000-\u00ff]/g,"aa").length;
    if(n==null)
        n=0;
    return n;
}
function del(id)
{
		var p0400objs=document.getElementsByName("taskDescribeList[<%=p0400Index%>].value");
		if(!confirm("请确认执行删除操作?"))
		{
			return;
		}
		document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_upAttach=del&operator=<%=request.getParameter("operator")%>&article_id="+id+"&_opt=2&_p0400="+p0400objs[0].value;
		document.objectCardForm.submit();
}
function checkUM()
{
	var obj=document.getElementById("score_org_hidden");
	if(obj!=null)
	{
		var tempvalue=obj.value;
		if(tempvalue.substring(0,2)!="UM")
		{
			var obj2=document.getElementById("score_org_view");
			if(obj2!=null)
			{
				obj2.value="";
				obj.value="";
				alert("只能选择部门");
			}
				
		}
	}
}	
function showPersonPicker(){
	
	var z=document.getElementById("raterid").value;
	var ids = new Array;
	ids=z.split(',');
	var p = new PersonPicker({
		multiple: true,
		isPrivExpression:false,
		isSelfUser:true,//是否选择自助用户
		nbases:'Usr',//限定为在职库
		text: "确定",
		defaultSelected:ids,
		callback: function (c) {
			var staffids = new Array;
			var staffname="";
			for (var i = 0; i < c.length; i++) {
				staffids[i] = c[i].id;
				staffname+=c[i].name + ",";
			}

			document.getElementById("raterid").value=staffids;
			document.getElementById("ratername").value=staffname.substr(0,staffname.length-1);
			
		}
	},Ext.getBody());
	p.open();
}
</script>


</body>
</html>

 
