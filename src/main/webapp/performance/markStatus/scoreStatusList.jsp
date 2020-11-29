<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.markStatus.markStatusForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.frame.utility.AdminCode,	
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,		
				 com.hjsj.hrms.utils.PubFunc,	 
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>

<%  
		markStatusForm markStatusForm=(markStatusForm)session.getAttribute("markStatusForm");
		String object_type=(String)markStatusForm.getObject_type();	  // 1:部门  2:人员
		String selectFashion=(String)markStatusForm.getSelectFashion(); // 查询方式 1:按考核主体  2:考核对象
		String plan_id =(String)markStatusForm.getCheckPlanId(); // 考核计划号
		String planName=(String)markStatusForm.getPlanName(); // 考核计划名称
		LinkedHashMap personScoreMap = (LinkedHashMap)markStatusForm.getPersonScoreMap(); // 数据
		String scoreType = (String)markStatusForm.getScoreType();    // 评分状态
		
		UserView userView=(UserView)session.getAttribute(WebConstant.userView);
		
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">

<style>

.TableRow_self 
{	
	margin-left:auto;
	margin-right:auto;
	background-position : center;
	background-color:#f4f7f7;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:22px;
	font-weight: bold;	
	valign:middle;
}
</style>
 <hrms:themes />
<script type="text/javascript">

// 统计
function query()
{
   	markStatusForm.action="/performance/markStatus/markStatusList.do?b_select=query";
   	markStatusForm.submit();
}

// 导出Excel
function ecportExcel()
{	
 	var hashVo=new ParameterSet();
   	hashVo.setValue("plan_id",getEncodeStr("<%=plan_id%>"));
   	hashVo.setValue("selectFashion","<%=selectFashion%>");
   	hashVo.setValue("scoreType","<%=scoreType%>");
    var request=new Request({method:'post',asynchronous:false,onSuccess:sucess,functionId:'90100170287'},hashVo);			
}
function sucess(outparameters)
{
 	var outname=outparameters.getValue("name");
// 	var name=outname.substring(0,outname.length-1)+".xls";
// 	name=getEncodeStr(name);
  	window.location.target="_blank";
//	window.location.href = "/servlet/DisplayOleContent?filename="+outname;
	//20/3/6 xus vfs改造
  	window.location.href = "/servlet/vfsservlet?fileid="+outname+"&fromjavafolder=true";
}

// 返回打分状态页面
function backScoreType()
{
   	markStatusForm.action="/performance/markStatus/markStatusList.do?b_search=link";
	markStatusForm.submit();
}

// 反查结果
function reverseResult(b0110,e0122,type)
{
	markStatusForm.action="/performance/markStatus/reverseResultList.do?b_reverse=query&b0110="+b0110+"&e0122="+e0122+"&type="+type;	
	markStatusForm.submit();
}

</script>

<html:form action="/performance/markStatus/markStatusList">

<html:hidden name="markStatusForm" property="selectFashion" styleId="selectFashion" />
	
	<br>
	<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">		
		<% if(selectFashion.equalsIgnoreCase("1")){ %>
			<tr><td align="center">   
				<strong><font size='4'> <%= planName%><bean:message key="jx.selfScore.markStatusList"/></font></strong>
			</td></tr>
		<% }else{ %>
			<tr><td align="center">   
				<strong><font size='4'> <%= planName%><bean:message key="jx.selfScore.markStatusList"/></font></strong>
			</td></tr>
		<% } %>	
	</table>		
	<br>
	<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">	
		<tr><td style="height:35px">   
			<bean:message key="jx.selfScore.e0122Level"/>: 			
			<html:select name="markStatusForm" styleId="e0122Level" property="e0122Level" size="1" >
	  			<html:optionsCollection property="e0122LevelList" value="dataValue" label="dataName"/>
			</html:select>	
			&nbsp;
			<bean:message key="jx.selfScore.scoreTypeMainbody"/>: 			
			<html:select name="markStatusForm" styleId="scoreType" property="scoreType" size="1" >
	  			<html:optionsCollection property="scoreTypeList" value="dataValue" label="dataName"/>
			</html:select>						
			&nbsp;	
			<hrms:priv> 
		    	<input type="button" name="check" class="mybutton" value="统计" onclick="query();"/>
		  	</hrms:priv>
		  	<hrms:priv> 
		    	<input type="button" name="outExcel" class="mybutton" value="导出Excel" onclick="ecportExcel();"/>
		  	</hrms:priv>
		  	<hrms:priv> 
		    	<input type="button" name="back" class="mybutton" value="返回" onclick="backScoreType();"/>
		  	</hrms:priv>
		</td></tr>						
		
		<tr><td width='100%' style="border-top: 0px solid #8EC2E6;"  class="common_border_color" >		
		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
			<thead>
		        <tr>		        	 		        	  
					 <%
							FieldItem fielditem = DataDictionary.getFieldItem("E0122");			  			 	
					 %>
					 <td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><bean:message key="b0110.label"/></td>
					 <td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><%=fielditem.getItemdesc()%></td>
					 
					 <% if(selectFashion.equalsIgnoreCase("1")){ %>
					 	<td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><bean:message key="jx.selfScore.mainbodyList"/></td>
					 <% }else{ %>
					 	<td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><bean:message key="jx.selfScore.objectList"/></td>
					 <% } %>	
					 
					 <% if(scoreType.equalsIgnoreCase("all")){ %>					 
						 <td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><bean:message key="lable.performnace.wpf"/></td>
						 <td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><bean:message key="lable.performnace.nowpingscore"/></td>
						 <td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><bean:message key="lable.performnace.havepingscore"/></td>	
					 <% }else if(scoreType.equalsIgnoreCase("01")){ %>	
					 	<td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><bean:message key="lable.performnace.wpf"/></td>
					 <% }else if(scoreType.equalsIgnoreCase("02")){ %>
					 	<td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><bean:message key="lable.performnace.nowpingscore"/></td>
					 <% }else if(scoreType.equalsIgnoreCase("03")){ %>
					 	<td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><bean:message key="lable.performnace.havepingscore"/></td>	
					 <% } %>			 
				</tr>
			 </thead>
		 
			 <% 
			 		int n = 0;		
			 		HashMap existWriteB0100=new HashMap();  // 放已画过的单位	 		
			 		Set keySet=personScoreMap.keySet();
					java.util.Iterator t=keySet.iterator();
					while(t.hasNext())
					{
						String strKey = (String)t.next();  //键值	    
						
						ArrayList personScoreList = (ArrayList)personScoreMap.get(strKey);   //value值   
									 
				 		for(int i=0;i<personScoreList.size();i++)
				 		{
				 			LazyDynaBean abean=(LazyDynaBean)personScoreList.get(i);
		 					String e0122=(String)abean.get("e0122");
		 					String _e0122=PubFunc.encrypt((String)abean.get("e0122"));
		 					String allScore=(String)abean.get("allScore");
		 					String noScore=(String)abean.get("noScore");
		 					String nowScore=(String)abean.get("nowScore");
		 					String endScore=(String)abean.get("endScore");
							if(n%2==0)
						    {  
						    	out.println("<tr class='trShallow'>");   
						    }else{
						    	out.println("<tr class='trDeep'>");
							}	
							if(existWriteB0100.get(strKey)==null)
						    {	
						    	if(strKey.equalsIgnoreCase("unit"))		    			
					    			out.println("\r\n<td class='RecordRow common_background_color' valign='middle' align='left'");
					    		else
					    			out.println("\r\n<td class='RecordRow' valign='middle' align='left'");
								if(personScoreList.size()!=0)
									out.print(" rowspan='"+(personScoreList.size())+"' ");
								else
									out.print(" height='22' ");
								out.print(" nowrap>&nbsp;");
								if(strKey.equalsIgnoreCase("b0110"))
									out.print("总计");
								else
									out.print(AdminCode.getCodeName("UN", strKey));
								out.print("</td>");
					    	}
							existWriteB0100.put(strKey,"1");
								
							if(((strKey.equalsIgnoreCase("b0110")) && (e0122==null || e0122.trim().length()<=0)) || (e0122!=null && e0122.trim().length()>0)) 																				
								out.println("<td align='left' class='RecordRow' nowrap>&nbsp;");
							else
								out.println("<td align='left' class='RecordRow common_background_color' nowrap>&nbsp;");
							out.print(AdminCode.getCodeName("UM", e0122));
//					 		out.print(e0122);
					  		out.print("</td>");	
					  		
					  		if(((strKey.equalsIgnoreCase("b0110")) && (e0122==null || e0122.trim().length()<=0)) || (e0122!=null && e0122.trim().length()>0))																			
								out.println("<td align='right' class='RecordRow' nowrap>");
							else
								out.println("<td align='right' class='RecordRow common_background_color' nowrap>&nbsp;");
															
							out.print("<a href='javascript:reverseResult(\""+PubFunc.encrypt(strKey)+"\",\""+_e0122+"\",\"allScore\")' >");
					 		out.print(allScore);
					 		out.print("</a>");
					  		out.print("&nbsp;&nbsp;</td>");	
					  		
					  		if(scoreType.equalsIgnoreCase("all"))
					  		{
						  		if(((strKey.equalsIgnoreCase("b0110")) && (e0122==null || e0122.trim().length()<=0)) || (e0122!=null && e0122.trim().length()>0))																				
									out.println("<td align='right' class='RecordRow' nowrap>");
								else
									out.println("<td align='right' class='RecordRow common_background_color' nowrap>&nbsp;");
								
								out.print("<a href='javascript:reverseResult(\""+PubFunc.encrypt(strKey)+"\",\""+_e0122+"\",\"0\")' >");	
						 		out.print(noScore);
						 		out.print("</a>");						 		
						  		out.print("&nbsp;&nbsp;</td>");	
						  		
						  		if(((strKey.equalsIgnoreCase("b0110")) && (e0122==null || e0122.trim().length()<=0)) || (e0122!=null && e0122.trim().length()>0))																				
									out.println("<td align='right' class='RecordRow' nowrap>");
								else
									out.println("<td align='right' class='RecordRow common_background_color' nowrap>&nbsp;");
																
								out.print("<a href='javascript:reverseResult(\""+PubFunc.encrypt(strKey)+"\",\""+_e0122+"\",\"1\")' >");
						 		out.print(nowScore);
						 		out.print("</a>");						 		
						  		out.print("&nbsp;&nbsp;</td>");	
						  		
						  		if(((strKey.equalsIgnoreCase("b0110")) && (e0122==null || e0122.trim().length()<=0)) || (e0122!=null && e0122.trim().length()>0))																				
									out.println("<td align='right' class='RecordRow' nowrap>");
								else
									out.println("<td align='right' class='RecordRow common_background_color' nowrap>&nbsp;");
									
								out.print("<a href='javascript:reverseResult(\""+PubFunc.encrypt(strKey)+"\",\""+_e0122+"\",\"2\")' >");
						 		out.print(endScore);
						 		out.print("</a>");						 		
						  		out.print("&nbsp;&nbsp;</td>");	
						  		
					  		}else if(scoreType.equalsIgnoreCase("01"))
					  		{
					  			if(((strKey.equalsIgnoreCase("b0110")) && (e0122==null || e0122.trim().length()<=0)) || (e0122!=null && e0122.trim().length()>0))																				
									out.println("<td align='right' class='RecordRow' nowrap>");
								else
									out.println("<td align='right' class='RecordRow common_background_color' nowrap>&nbsp;");
									
						 		out.print("<a href='javascript:reverseResult(\""+PubFunc.encrypt(strKey)+"\",\""+_e0122+"\",\"0\")' >");	
						 		out.print(noScore);
						 		out.print("</a>");
						  		out.print("&nbsp;&nbsp;</td>");	
						  		
					  		}else if(scoreType.equalsIgnoreCase("02"))
					  		{
					  			if(((strKey.equalsIgnoreCase("b0110")) && (e0122==null || e0122.trim().length()<=0)) || (e0122!=null && e0122.trim().length()>0))																				
									out.println("<td align='right' class='RecordRow' nowrap>");
								else
									out.println("<td align='right' class='RecordRow common_background_color' nowrap>&nbsp;");						 		
						 		
								out.print("<a href='javascript:reverseResult(\""+PubFunc.encrypt(strKey)+"\",\""+_e0122+"\",\"1\")' >");
						 		out.print(nowScore);
						 		out.print("</a>");
						  		out.print("&nbsp;&nbsp;</td>");	
						  		
					  		}else if(scoreType.equalsIgnoreCase("03"))
					  		{
					  			if(((strKey.equalsIgnoreCase("b0110")) && (e0122==null || e0122.trim().length()<=0)) || (e0122!=null && e0122.trim().length()>0))																			
									out.println("<td align='right' class='RecordRow' nowrap>");
								else
									out.println("<td align='right' class='RecordRow common_background_color' nowrap>&nbsp;");
						 		
						 		out.print("<a href='javascript:reverseResult(\""+PubFunc.encrypt(strKey)+"\",\""+_e0122+"\",\"2\")' >");
						 		out.print(endScore);
						 		out.print("</a>");
						  		out.print("&nbsp;&nbsp;</td>");	
					  		}
					  							  													
					  		out.print("</tr>");
					 	} 			 				 						 	
			 		}			 
			 %>
			 	  
	  	</table>
		</td></tr>	
	</table>	

	
	<script>
											
	  
	</script>
	
</html:form>