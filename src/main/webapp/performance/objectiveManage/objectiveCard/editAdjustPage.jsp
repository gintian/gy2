<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				com.hjsj.hrms.actionform.performance.objectiveManage.ObjectCardForm,
				org.apache.commons.beanutils.LazyDynaBean,
				com.hrms.hjsj.sys.DataDictionary,
                com.hrms.hjsj.sys.FieldItem,
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
	String opt=request.getParameter("operate");  //1:删除调整后的任务 2：编辑调整后的任务
	String desc="&nbsp;删除任务";
	if(opt.equals("2"))
		desc="&nbsp;任务变更说明";
	if(opt.equals("3"))
		desc="&nbsp;任务调整";
	ObjectCardForm objectCardForm=(ObjectCardForm)session.getAttribute("objectCardForm");
	ArrayList adjustBeforePointList=objectCardForm.getAdjustBeforePointList();
	Hashtable planParam=objectCardForm.getPlanParam();
	
	String ProcessNoVerifyAllScore=(String)planParam.get("ProcessNoVerifyAllScore");
	
	String status=objectCardForm.getStatus();  //　０:分值模版  1:权重模版
	FieldItem p0407Item=DataDictionary.getFieldItem("p0407");
	int p0407_itemlength=p0407Item.getItemlength();
	FieldItem item=DataDictionary.getFieldItem("p0425");//指标|任务变更说明
	int adjustDescLength=item.getItemlength();
	FieldItem item_d=DataDictionary.getFieldItem("p0427");//调整日期
	FieldItem itembefore_s=DataDictionary.getFieldItem("p0413");
	FieldItem itemafter_s=DataDictionary.getFieldItem("p0421");
	FieldItem itembefore_r=DataDictionary.getFieldItem("p0415");
	FieldItem itemafter_r=DataDictionary.getFieldItem("p0423");
	String itemKind=objectCardForm.getItemKind();
	String itemtyped=objectCardForm.getItemtype();
	String fromflag=objectCardForm.getFromflag();
	if(itemtyped==null)
		itemtyped="0";
	 
	//item.isFillable();必填属性,内容为p0407
	//P0413	标准分值
   // P0415	权重
    //P0421	调整后标准分值
   // P0423	调整后权重
	
	String valueName1="分值";
	String valueName2="调整后分值";
	if(status.equals("1"))
	{
		valueName1="权重";
		valueName2="调整后权重";
	}
	
 %>				
				
<html>
<head>
<link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<title>Insert title here</title>
</head>
<script language="JavaScript" src="/js/popcalendar3.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script language='javascript' >
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
function sub2()
{

	<% 
	if(opt.equals("2")||opt.equals("3")){
		if(p0407_itemlength!=0)
		{ %> 
			//与新建任务时的判断保持一致。 haosl 2018-9-10
			if(<%=p0407_itemlength%>!=10){
				if(IsOverStrLength2(objectCardForm.pointContent.value,<%=p0407_itemlength%>))
				{
							alert("任务内容"+OBJECTCARDINFO12);
							return;
				}
			}
	<%
		}
	 %>
	if(objectCardForm.pointContent.value.length==0)
	{
		alert("任务内容不能为空!");
		return;
	}

	<%
	   if(status.equals("1"))
	   {
	     if(itembefore_r.isFillable()){
	 %>
	   if(objectCardForm.before_value.value.length==0)
	   {
	      alert("<%=valueName1%>不能为空！");
	      return;
	   }
	 <%
	   }
	   if(itemafter_r.isFillable()){
	   %>
	      
	       if(objectCardForm.after_value.value.length==0)
	      {
	      alert("<%=valueName2%>不能为空！");
	      return;
	     }
	      <%
	      }
	   }
	   else
	   {
	     if(itembefore_s.isFillable()){
	   %>
	       if(objectCardForm.before_value.value.length==0)
	      {
	         alert("<%=valueName1%>不能为空！");
	         return;
	      }
	      <%}
	       if(itemafter_s.isFillable())
	       {
	      %>
	      if(objectCardForm.before_value.value.length==0)
	      {
	         alert("<%=valueName2%>不能为空！");
	         return;
	      }
	      
	   <%
	   }
	 } 
	 %>
	<%if(item.isFillable()){%>
	   if(objectCardForm.adjustDesc.value.length==0)
	   {
	    	alert("<%=(item.getItemdesc())%>不能为空!");
		    return;
	   }
     <%}
     }%>
	<%
	
	 if(opt.equals("1")){ %>
	if(confirm(OBJECTCARDINFO6+"?"))
	{
	   <%if(item.isFillable()){%>
	   if(objectCardForm.adjustDesc.value.length==0)
	   {
	    	alert("<%=(item.getItemdesc())%>不能为空!");
		    return;
	   }
   	  <%} 
   	    
	    if(adjustDescLength!=0 && adjustDescLength!=10)//备注型的如果是0或者10则为不限制
		{ %> 
				if(IsOverStrLength2(objectCardForm.adjustDesc.value,<%=adjustDescLength%>))
				{
							alert("变更说明"+OBJECTCARDINFO12);
							return;
				}
		<%
		}
		%>
	
		document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_editAdjust=edit&operate=<%=(request.getParameter("operate"))%>&p0400=<%=(request.getParameter("p0400"))%>";
		document.objectCardForm.submit();
	}
	<% } else { %>
		
		 var myReg =/^(-?\d+)(\.\d+)?$/
		if(!myReg.test(objectCardForm.after_value.value)) 
		{
			alert("<%=valueName2%>"+PLEASEWRITENUMBER+"！");
			return;
		}
		
		if(objectCardForm.after_value.value<0)
		{
			alert("<%=valueName2%>"+"不能为负数!");
			return;
		}
		
		
		<% if(status.equals("1")&&ProcessNoVerifyAllScore.equalsIgnoreCase("False")){ %>
		if(objectCardForm.after_value.value>100||(objectCardForm.after_value.value!=0&&objectCardForm.after_value.value<0.01))
		{
		//此处控制没什么意义啦
		//	alert("<%=valueName2%>超出数值范围!");
		//	return;
		}
		<% } 	
		if(adjustDescLength!=0 && adjustDescLength!=10)//备注型的如果是0或者10则为不限制
		{ %> 
				if(IsOverStrLength2(objectCardForm.adjustDesc.value,<%=adjustDescLength%>))
				{
							alert("变更说明"+OBJECTCARDINFO12);
							return;
				}
		<%
		}
		%>
	
		
		<% int m=0;  %>
	<logic:iterate  id="element"    name="objectCardForm"  property="adjustBeforePointList" indexId="index"> 
				var mustFill='<bean:write  name="element" property="mustFill"/>';
				var d<%=m%>=document.getElementsByName("adjustBeforePointList[<%=m%>].value")
			     if(d<%=m%>[0].value=='')
			     {
			     	  var _flag=true;
			     	  <% if(status.equals("1")){ %> 
						if(objectCardForm.after_value.value*1==0)
							_flag=false;
			     	  <% } %>  
					   if(mustFill=='1'&&_flag)
					   {
					      alert("<bean:write  name="element" property="itemdesc"/>"+"不能为空!");
					      return;
					   }
				}
				<logic:equal name="element" property="itemtype" value="N">
					var a<%=m%>=document.getElementsByName("adjustBeforePointList[<%=m%>].value")
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
					var a<%=m%>=document.getElementsByName("adjustBeforePointList[<%=m%>].value")
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
						var a<%=m%>=document.getElementsByName("adjustBeforePointList[<%=m%>].value")
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
			
			<% m++; %>
	</logic:iterate>
		
		document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_editAdjust=edit&operate=<%=(request.getParameter("operate"))%>&p0400=<%=(request.getParameter("p0400"))%>";
		document.objectCardForm.submit();
	<% } %>
	
}

<%  
	if(request.getParameter("b_editAdjust")!=null)
	{
%>
		closeWin();
<%
	}	
%>
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
function closeWin(){
	if(parent && parent.parent && parent.parent.Ext && parent.parent.editadjustpointWinClose && !/msie/i.test(navigator.userAgent)){
		parent.parent.editadjustpointWinClose();
	}else {
		parent.window.close();
	}
}
</script>

<style>

#Wdiv
{ 	
	BORDER-BOTTOM: #C4D8EE 0pt solid; 
    BORDER-LEFT: #C4D8EE 0pt solid; 
    BORDER-RIGHT: #C4D8EE 0pt solid; 
    BORDER-TOP: #C4D8EE 0pt solid ; 
    margin-left:30px;
    margin-top:10px;
}

</style>

<body>
<html:form action="/performance/objectiveManage/objectiveCard">	
	
	<% 
	
		String height="425";
		if(opt!=null&&opt.equals("1")){
			height="270";
		}
	 %>
	
	<div id="Wdiv" style="overflow:auto;width:540px;height:<%=height%>px;" >
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable">
					<tr >
						<td colspan="2" align="left" class="TableRow">
						<font class="<%=tt4CssName%>"><%=desc%></font>
						</td>
					</tr>
	
	<%
		if(opt.equals("1")){
	%>	
		<tr><td align='right' valign='top'  width='30%' class='RecordRow' nowrap>&nbsp;&nbsp;&nbsp;<font class='<%=tt3CssName%>'><%=(item.getItemdesc())%></font></td>
		<td align='left'  width='70%'  class='RecordRow' nowrap>
			<html:textarea name="objectCardForm" property="adjustDesc" cols="46" rows="10" style="height: 95%;"/>
			<%
			if(item.isFillable())
			{
				 out.println("<font color=\"red\">*</font>");
			}
			
			 %>	
		</td></tr>
		
		<tr>
		<td align='right' valign='top'  width='30%' class='RecordRow' nowrap>&nbsp;&nbsp;&nbsp;<font class='<%=tt3CssName%>'><%=(item_d.getItemdesc())%></font></td>
		<td align='left'  width='70%'  class='RecordRow' nowrap>
			<html:text name="objectCardForm" property="adjustDate"  disabled="true"  ></html:text>
		</td></tr>
	
		
	<% 
		}
		else
		{
	%>	
		<tr><td align='right' valign='middle'  width='30%' class='RecordRow' nowrap>&nbsp;&nbsp;&nbsp;<font class='<%=tt3CssName%>'>任务内容</font></td>
		<td align='left'  width='70%'  class='RecordRow' nowrap>
		    <%if(itemKind.equals("1")||itemtyped.equals("3")||!fromflag.equals("1")){ %>
			<html:textarea name="objectCardForm" property="pointContent" cols="46" rows="4" disabled="true" style="height:92%"/>	
			<%}else{ %>
			<html:textarea name="objectCardForm" property="pointContent" cols="46" rows="4" style="height:92%"/>	
			<%} %>
		</td></tr>
		
		<%
			// 根据TargetDefineItem参数决定标准分值或权重是否可见 add by 刘蒙
			Map param = objectCardForm.getPlanParam();
			String targetItem = (String) param.get("TargetDefineItem");
			boolean isP0413 = targetItem.indexOf("P0413") == -1 ? false : true;
			boolean isP0415 = targetItem.indexOf("P0415") == -1 ? false : true;
			String styleHidden = "";
			if (("0".equals(status) && !isP0413) || ("1".equals(status) && !isP0415)) {
				styleHidden = "display:none;";
			}
		%>
		<tr style="<%=styleHidden %>"><td align='right' valign='top'  width='30%' class='RecordRow' nowrap><font class='<%=tt3CssName%>'><%=valueName1%></font></td>
		<td align='left'  width='70%'  class='RecordRow' nowrap>
				<html:text name="objectCardForm"  property="before_value" disabled="true"  ></html:text>
				<logic:equal name="objectCardForm"  property="status"  value="1">%</logic:equal>
				<%
				if(status.equals("1"))
				{
				  if(itembefore_r.isFillable())
				  {
				    out.println("<font color=\"red\">*</font>");
				  }
				}
				else
				{
				  if(itembefore_s.isFillable())
				  {
				    out.println("<font color=\"red\">*</font>");
				  }
				}
				 %>
		</td></tr>
		<tr style="<%=styleHidden %>"><td align='right' valign='top'  width='30%' class='RecordRow' nowrap><font class='<%=tt3CssName%>'><%=valueName2%></font></td>
		<td align='left'  width='70%'  class='RecordRow' nowrap>
				<html:text name="objectCardForm"  property="after_value"></html:text>
				<logic:equal name="objectCardForm"  property="status"  value="1">%</logic:equal>
				<%
				if(status.equals("1"))
				{
				  if(itemafter_r.isFillable())
				  {
				    out.println("<font color=\"red\">*</font>");
				  }
				}
				else
				{
				  if(itemafter_s.isFillable())
				  {
				    out.println("<font color=\"red\">*</font>");
				  }
				}
				 %>
		</td></tr>
		<tr><td align='right' valign='middle'  width='30%' class='RecordRow' nowrap>&nbsp;&nbsp;&nbsp;<font class='<%=tt3CssName%>'><%=(item.getItemdesc())%></font></td>
		<td align='left'  width='70%'  class='RecordRow' nowrap>
			<html:textarea name="objectCardForm" property="adjustDesc" cols="46" rows="10" style="height: 95%;"/>
			<%
			if(item.isFillable())
			{
				 out.println("<font color=\"red\">*</font>");
			}
			
			 %>		
		</td></tr>
		<tr><td align='right' valign='top'  width='30%' class='RecordRow' nowrap>&nbsp;&nbsp;&nbsp;<font class='<%=tt3CssName%>'><%=(item_d.getItemdesc())%></font></td>
		<td align='left'  width='70%'  class='RecordRow' nowrap>
			<html:text name="objectCardForm" property="adjustDate"  disabled="true"  ></html:text>
		</td></tr>
		
		<% for(int i=0;i<adjustBeforePointList.size();i++){
		
		
							 LazyDynaBean abean=(LazyDynaBean)adjustBeforePointList.get(i);
			                 String itemid=(String)abean.get("itemid");
							 String itemtype=(String)abean.get("itemtype");
							 String codesetid=(String)abean.get("codesetid");
							 String itemdesc=(String)abean.get("itemdesc");
							 String value=(String)abean.get("value");
							 String viewvalue=(String)abean.get("viewvalue");
							 String isCalc=(String)abean.get("isCalc");
							 StringBuffer buf = new StringBuffer();
							 if(itemdesc.length()>6)
							 {
							     int ss=itemdesc.length()/6+1;
							     int index=0;
							     for(int j=1;j<=ss;j++)
							     {
							       if(itemdesc.length()>(index+1)*6)
							           buf.append(itemdesc.substring(index*6,(index+1)*6)+"<br>&nbsp;&nbsp;&nbsp;");
							       else
							           buf.append(itemdesc.substring(index*6,itemdesc.length()));
							       index++;
							     }
							 }
							 else
							 {
							    buf.append(itemdesc);
							 }		 
			          		 out.println("<tr><td align='right' ");
			          		  if(itemtype.equals("M"))
							 	out.print(" valign='middle' ");
			          		 out.print("  width='30%' class='RecordRow' nowrap>&nbsp;&nbsp;&nbsp;<font class='"+tt3CssName+"'>"+buf.toString()+"</font></td><td align='left'  width='70%'  class='RecordRow' nowrap>");
			          		
			          		 if(itemtype.equals("A"))
							 {
											if(codesetid.equals("0")||codesetid.length()==0)
											{
										    		out.println("<input type=\"text\"  ");							
											    	out.print(" name=\"adjustBeforePointList["+i+"].value\"  size='30'   value=\""+value+"\"  />&nbsp;");
												
											}
											else
											{
									              	if(itemid.equalsIgnoreCase("score_org"))
												 {
												   	out.print(" <input type='text' name='adjustBeforePointList["+i+"].viewvalue' ");
									              
									              	out.print(" size='30' value='"+viewvalue+"'  />");
									              	
											        out.print("   <img  onclick='openInputCodeDialogOrg_handwork(\"UM\",\"adjustBeforePointList["+i+"].viewvalue\",\"\",\"s\");' src='/images/code.gif' border=0 align=\"absmiddle\"/>&nbsp;");		
													
													out.print("<input type='hidden' value='"+value+"'  name='adjustBeforePointList["+i+"].value' /> &nbsp;");									
												}
												else{
									              	out.print(" <input type='text' name='adjustBeforePointList["+i+"].viewvalue' ");
									              
									              	out.print(" size='30' value='"+viewvalue+"'  />");
									              	if(!itemid.equalsIgnoreCase("B0110")&&!itemid.equalsIgnoreCase("E0122")&&!itemid.equalsIgnoreCase("E01A1"))
													{	
											           out.print("    <img onclick='openInputCodeDialog(\""+codesetid+"\",\"adjustBeforePointList["+i+"].viewvalue\");'  src='/images/code.gif' border=0 align=\"absmiddle\"/>&nbsp;");		
													}
													out.print("<input type='hidden' value='"+value+"'  name='adjustBeforePointList["+i+"].value' /> &nbsp;");	
												}								
											}
										
									}
									else if(itemtype.equals("D"))
									{
											out.print("<input type='text'  size='20'  name='adjustBeforePointList["+i+"].value'  value='"+value+"' ");
											if(itemid.equalsIgnoreCase("p0427"))
											   out.print(" disabled ");
											else
											   out.print(" onkeydown='checkKeyCode();'");
										    out.print("  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' ");	
											out.println("  />&nbsp;");
									}
									else if(itemtype.equals("N"))
									{
											
										    out.print("<input type=\"text\" name=\"adjustBeforePointList["+i+"].value\" ");
											if(itemid.equalsIgnoreCase("P0419"))
											{
													out.print(" onblur='validateNum(this)'  ");
											}
											out.print("  value='"+value+"'   size='30' ");
											if(isCalc.equals("1"))
											    out.print(" readonly");
											out.print(" />&nbsp;");
											   
																			
												
									}
									else if(itemtype.equals("M"))
									{
											out.println("<textarea name=\"adjustBeforePointList["+i+"].value\" rows='5'   cols='46' class='textboxMul'>"+value+"</textarea>&nbsp;");
									}
									out.print("</td></tr>");
			          }
		 %>
		
		
		
		
		
		
	<% 
		}
	 %>	 	 
	 		<tr>
			<td align='center' style="height:35px" colspan="2">
				<input type='button' value='确定' onclick='sub2()' class="mybutton" />
		 		<input type='button' value='取消' onclick='javascript:closeWin();' class="mybutton" />
	 		</td>
	 	</tr>
	 </table>
	 </div>

</html:form>
</body>
	<script LANGUAGE=javascript>
		var aa=document.getElementsByTagName("input");
		for(var i=0;i<aa.length;i++){
			if(aa[i].type=="text"){
				aa[i].className="inputtext";
			}
		}
	</script>

</html>