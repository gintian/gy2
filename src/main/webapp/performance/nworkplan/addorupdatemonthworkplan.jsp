<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/performance/nworkplan/nworkplan.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<%@page import="com.hjsj.hrms.actionform.performance.nworkplan.MonthWorkplanForm,
               org.apache.commons.beanutils.LazyDynaBean,
               java.util.ArrayList
               "%>
<%
MonthWorkplanForm myForm=(MonthWorkplanForm)session.getAttribute("monthWorkplanForm");
String type = myForm.getType();
String log_type = myForm.getLog_type();
String saveflag = myForm.getSaveflag();
String personPage = myForm.getPersonPage();
String isChuZhang = myForm.getIsChuZhang();
ArrayList zongjieFieldsList = myForm.getZongjieFieldsList();
ArrayList jihuaFieldsList = myForm.getJihuaFieldsList();
%>               
<script type="text/javascript">
var sflag = '<%=saveflag%>';
<% if(request.getParameter("addflag")==null || request.getParameter("addflag").equals("")){%>
		if(sflag=='1'){
		   var obj = new Object();
		   obj.saveflag = sflag;
		   obj.optflag = 'save';
		   returnValue=obj;
		   window.close();
		}else{
		   alert("保存成功");
		}		
<% }%>
function cancel() 
{
   var obj = new Object();
   obj.saveflag = sflag;
   obj.optflag = 'cancel';
   returnValue=obj;
   window.close(); 
}
function savevalue(saveflag){
   <% int m=0; if(log_type.equals("1")){ %>
	<logic:iterate  id="element"    name="monthWorkplanForm"  property="jihuaFieldsList" indexId="index"> 
			
			
			<logic:equal name="element" property="state"  value="1" >
			<logic:equal name="element" property="fillable"  value="1" >
				var zz=document.getElementsByName("jihuaFieldsList[<%=m%>].value")
				var _flag=true;
	
				if(trim(zz[0].value).length==0)
				{
					alert("<bean:write  name="element" property="itemdesc"/>为必填项!");
					return;
				}
			
			</logic:equal>
			</logic:equal>
			<logic:equal name="element" property="state"  value="1" >	
				<logic:equal name="element" property="itemtype" value="N">
					var a<%=m%>=document.getElementsByName("jihuaFieldsList[<%=m%>].value")
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
					 }
				</logic:equal>
				<logic:equal name="element" property="itemtype" value="M">
				<logic:notEqual  name="element"  property="itemlength" value="0">
					var a<%=m%>=document.getElementsByName("jihuaFieldsList[<%=m%>].value")
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
					var a<%=m%>=document.getElementsByName("jihuaFieldsList[<%=m%>].value")
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
   <%}else{%>
      <logic:iterate  id="element"    name="monthWorkplanForm"  property="zongjieFieldsList" indexId="index"> 
			
			
			<logic:equal name="element" property="state"  value="1" >
			<logic:equal name="element" property="fillable"  value="1" >
				var zz=document.getElementsByName("zongjieFieldsList[<%=m%>].value")
				var _flag=true;
	
				if(trim(zz[0].value).length==0)
				{
					alert("<bean:write  name="element" property="itemdesc"/>为必填项!");
					return;
				}
			
			</logic:equal>
			</logic:equal>
			<logic:equal name="element" property="state"  value="1" >	
				<logic:equal name="element" property="itemtype" value="N">
					var a<%=m%>=document.getElementsByName("zongjieFieldsList[<%=m%>].value")
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
					 }
				</logic:equal>
				<logic:equal name="element" property="itemtype" value="M">
				<logic:notEqual  name="element"  property="itemlength" value="0">
					var a<%=m%>=document.getElementsByName("zongjieFieldsList[<%=m%>].value")
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
					var a<%=m%>=document.getElementsByName("zongjieFieldsList[<%=m%>].value")
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
   <%}%>
   monthWorkplanForm.action = "/performance/nworkplan/searchMonthWorkplan.do?b_save=link&saveflag="+saveflag;
   monthWorkplanForm.submit();
}

</script>
<div style="height:350px;overflow-x:hidden;overflow-y:auto;">
<html:form action="/performance/nworkplan/searchMonthWorkplan">
<br/>
<table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr height="20">
	<td align='left' class='TableRow_lrt'>
	<logic:equal value="1" name="monthWorkplanForm" property="type">
	   <logic:equal value="2" name="monthWorkplanForm" property="log_type">
		新增月总结
		</logic:equal>
	   <logic:equal value="1" name="monthWorkplanForm" property="log_type">
		新增月计划
		</logic:equal>
		</logic:equal>
		<logic:equal value="2" name="monthWorkplanForm" property="type">
		<logic:equal value="2" name="monthWorkplanForm" property="log_type">
		修改月总结
		</logic:equal>
	   <logic:equal value="1" name="monthWorkplanForm" property="log_type">
		修改月计划
		</logic:equal>
		</logic:equal>
	</td>
</tr>
<tr>
	<td align="left" class="framestyle" height="120px">
	   <table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0">
	      <% 
	        if(log_type.equals("1")){
	           for(int i=0;i<jihuaFieldsList.size();i++){
	        	   LazyDynaBean bean=(LazyDynaBean)jihuaFieldsList.get(i);
				   String itemid=(String)bean.get("itemid");
				   String itemtype=(String)bean.get("itemtype");
				   String codesetid=(String)bean.get("codesetid");
				   String itemdesc=(String)bean.get("itemdesc");
				   String itemlength=(String)bean.get("itemlength");
				   String decimalwidth=(String)bean.get("decimalwidth");
				   String state=(String)bean.get("state");
				   String value=(String)bean.get("value");
				   String viewvalue=(String)bean.get("viewvalue");
				   out.print(" <tr> ");
				     out.print("<td width='80' align='right'>"+itemdesc+":"+"</td>");
				     out.print("<td align='left'>");
				       if(itemtype.equals("M")){
				    	   out.print("<textarea name=\"jihuaFieldsList["+i+"].value\" rows='10' cols='45'>"); 
				    	   out.print(value);
				    	   out.print("</textarea>");
				       }
				       if(itemtype.equals("A")){
				    	   if(codesetid.equals("0")||codesetid.length()==0)
						   {
				    		   out.print("<input type='text' name=\"jihuaFieldsList["+i+"].value\"  value='"+value+"'");
				    		   if(itemid.equalsIgnoreCase("principal"))
				    			   out.print(" id='principal' ");
				    	  	   out.print("/>");
				    	  	    if(itemid.equalsIgnoreCase("principal")){
				    	  		  out.print("<a href=\"###\"> <img  onclick=\"showperson();\" src=\"/images/group_p.gif\" border=\"0\"/></a>");
				    	  	    }
						   }else{
							   out.print(" <input type='text' name='jihuaFieldsList["+i+"].viewvalue' ");
					              
				               out.print(" size='30' value='"+viewvalue+"'  />");
				              	
				               out.print("<img  onclick='openInputCodeDialog(\""+codesetid+"\",\"jihuaFieldsList["+i+"].viewvalue\");' src='/images/code.gif' border=0 align=\"absmiddle\"/>&nbsp;");			
								
							   out.print("<input type='hidden' value='"+value+"'  name='jihuaFieldsList["+i+"].value' /> &nbsp;");
						   }
				       }
				       if(itemtype.equals("D"))
					   {
								out.print("<input type='text'  size='20'  name='jihuaFieldsList["+i+"].value'  value='"+value+"'  onkeydown='checkKeyCode();' ");
							    out.print("  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' ");	
								out.print("  />&nbsp;");
					   }
				       if(itemtype.equals("N")){
					        out.print("<input type=\"text\" name=\"jihuaFieldsList["+i+"].value\" ");
							out.print("  value='"+value+"'   size='30'  ");
							out.print("/>&nbsp;");
				       }
				     out.print(" </td> ");
				   out.print(" </tr> ");
	           }
	        }else{
	           for(int i=0;i<zongjieFieldsList.size();i++){

	        	   LazyDynaBean bean=(LazyDynaBean)zongjieFieldsList.get(i);
				   String itemid=(String)bean.get("itemid");
				   String itemtype=(String)bean.get("itemtype");
				   String codesetid=(String)bean.get("codesetid");
				   String itemdesc=(String)bean.get("itemdesc");
				   String itemlength=(String)bean.get("itemlength");
				   String decimalwidth=(String)bean.get("decimalwidth");
				   String state=(String)bean.get("state");
				   String value=(String)bean.get("value");
				   String viewvalue=(String)bean.get("viewvalue");
				   String canWrite=(String)bean.get("canWrite");
				   out.print(" <tr> ");
				     out.print("<td width='80' align='right'>"+itemdesc+":"+"</td>");
				     out.print("<td align='left'>");
				       if(itemtype.equals("M")){
				    	   out.print("<textarea name=\"zongjieFieldsList["+i+"].value\" rows='10' cols='45'"); 
				    	   if(canWrite.equals("0"))
				    			   out.print(" disabled ");
				    	   out.print(">");
				    
				    	   out.print(value);
				    	   out.print("</textarea>");
				       }
				       if(itemtype.equals("A")){
				    	   if(codesetid.equals("0")||codesetid.length()==0)
						   {
				    		   out.print("<input type='text' name=\"zongjieFieldsList["+i+"].value\"  value='"+value+"'");
				    		   if(canWrite.equals("0"))
				    			   out.print(" disabled ");
				    		   if(itemid.equalsIgnoreCase("principal"))
				    			   out.print(" id='principal' ");
				    	  	   out.print("/>");
				    	  	    if(itemid.equalsIgnoreCase("principal")&&!canWrite.equals("0")){
				    	  		  out.print("<a href=\"###\"> <img  onclick=\"showperson();\" src=\"/images/group_p.gif\" border=\"0\"/></a>");
				    	  	    }
						   }else{
							   out.print(" <input type='text' name='zongjieFieldsList["+i+"].viewvalue' ");
					             
							   if(canWrite.equals("0"))
				    			   out.print(" disabled ");
							   
				               out.print(" size='30' value='"+viewvalue+"'  />");
				              	
				               if(canWrite.equals("1"))
				                  out.print("<img  onclick='openInputCodeDialog(\""+codesetid+"\",\"zongjieFieldsList["+i+"].viewvalue\");' src='/images/code.gif' border=0 align=\"absmiddle\"/>&nbsp;");	
								
							   out.print("<input type='hidden' value='"+value+"'  name='zongjieFieldsList["+i+"].value' /> &nbsp;");
						   }
				       }
				       if(itemtype.equals("D"))
					   {
								out.print("<input type='text'  size='20'  name='zongjieFieldsList["+i+"].value'  value='"+value+"'  onkeydown='checkKeyCode();' ");
							    out.print("  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' ");	
							    if(canWrite.equals("0"))
					    			   out.print(" disabled ");
							    out.print("  />&nbsp;");
					   }
				       if(itemtype.equals("N")){
					        out.print("<input type=\"text\" name=\"zongjieFieldsList["+i+"].value\" ");
							out.print("  value='"+value+"'   size='30'  ");
							if(canWrite.equals("0"))
				    			   out.print(" disabled ");
							out.print("/>&nbsp;");
				       }
				     out.print(" </td> ");
				   out.print(" </tr> ");
	              
		       }
	        }
	      %>
	      
	      
	      <!--  <tr>
			<td width="80" align="right">工作内容:</td>
			<td align="left"><textarea rows="10" cols="45">sdfsdfsdfsadfsdfsdfsafsdf</textarea></td>
			</tr>
		  <tr>
			<td width="80" align="right">工作标题:</td>
			<td align="left"><input type="text" value="sdfsdfasdfsadfsdfsdfsdfsd"/></td>
		  </tr>-->
	   </table>
	</td>
</tr>
</table>
</html:form>
</div>
<br/>
<table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td align="center">
<input type="button" class="mybutton" onclick="savevalue('1');"  value="<bean:message key="button.save"/>"/>
<logic:equal value="1" name="monthWorkplanForm" property="type"> 
<input type="button" class="mybutton" onclick="savevalue('2');"  value="<bean:message key="button.savereturn"/>"/>
</logic:equal>
<input type="button" class="mybutton" onclick="cancel()"  value="<bean:message key="button.cancel"/>"/>
</td>
</tr>
</table>
