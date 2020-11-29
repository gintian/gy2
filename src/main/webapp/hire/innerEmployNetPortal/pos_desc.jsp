<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.hire.innerEmployNetPortal.InnerEmployPortalForm,
			     org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.utils.PubFunc,
			     java.util.*,com.hrms.hjsj.sys.ResourceFactory,com.hrms.frame.codec.SafeCode"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<html>
  <head>
  
  			<%
  			
  			
			InnerEmployPortalForm employPortalForm=(InnerEmployPortalForm)session.getAttribute("innerEmployPortalForm");
			ArrayList posDescFiledList=employPortalForm.getPosDescFiledList();
			String    zpDbName = PubFunc.encrypt(employPortalForm.getZpDbName());
			String    zpPosID = PubFunc.encrypt(employPortalForm.getPosID());
			String    zpkA0100 = PubFunc.encrypt(employPortalForm.getZpkA0100());
			String    isPosBooklet = employPortalForm.getIsPosBooklet();
			String    e01a1 = PubFunc.encrypt(employPortalForm.getE01a1());
			String posname=SafeCode.decode(request.getParameter("posName"));
			
			%>
			<%
		    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
		    String bosflag= userView.getBosflag();//得到系统的版本号
            %>  
  </head>
  <script language="JavaScript" src="/js/constant.js"></script>
  <script langugage='javascript'>
  
    function goback()
	{
		document.innerEmployPortalForm.action="/hire/innerEmployNetPortal/initInnerEmployPos.do?b_query=link";
		document.innerEmployPortalForm.submit();
	}
	
	function sub()
	{
		document.innerEmployPortalForm.action="/hire/innerEmployNetPortal/initInnerEmployPos.do?b_applyPos=link&apply=1&posName=<%=request.getParameter("posName")%>";
		document.innerEmployPortalForm.submit();
	}
  
  
  	<% 
  	if(request.getParameter("apply")!=null&&request.getParameter("apply").equals("1"))
  	{
  		String flag=employPortalForm.getFlag();
  		if(flag.equals("1"))
  		{
  			out.print("alert('"+ResourceFactory.getProperty("hire.position.applysuccess")+"！')");
  		}
  		else if(flag.equals("2"))
  		{
  			out.print("alert('"+ResourceFactory.getProperty("hire.position.applyed")+"！')");
  		}
  		else if(flag.equals("3"))
  		{
  			out.print("alert('"+ResourceFactory.getProperty("hire.over.applynumber")+"！')");
  		}
  		else if(flag.equals("4"))
  		{
  			out.print("alert('"+ResourceFactory.getProperty("hire.systemerorr.waiter")+"！')");
  		}
  		else if(flag.equals("5"))
  		{
  		     out.print("var alt='"+employPortalForm.getAlertMessage()+"';");
  		     out.print("alert(getDecodeStr(alt));");
  		}
  	}
  	%>
  
  
  	function del(zp_pos_id)
	{
		
		var hashvo=new ParameterSet();
		var In_paramters="a0100=<%=zpkA0100%>";  
		hashvo.setValue("zp_pos_id",zp_pos_id);
		hashvo.setValue("dbname",'<%=zpDbName%>');
		hashvo.setValue("opt","del");
		
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000172'},hashvo);
	
	}
  	
  	
  	function returnInfo(outparamters)
	{
		var info=outparamters.getValue("info");
		alert(info);
		document.innerEmployPortalForm.action="/hire/innerEmployNetPortal/initInnerEmployPos.do?b_posDesc=link&z0301=<%=zpPosID%>&posName=<%=request.getParameter("posName")%>";
		document.innerEmployPortalForm.submit();
	}
  	
  </script>
  
  
  <body>
  <html:form action="/hire/innerEmployNetPortal/initInnerEmployPos"> 
  <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" id="layouttable">
          <tr height="20">
       		<td align='left' class='TableRow_lrt'>
       		   <%=posname%>&nbsp;
       		</td>              	      
          </tr> 
          <tr>
            <td class="framestyle"  >
            	<br>
  
  					
  					<table width="90%" border="0" cellspacing="2"  align="center" cellpadding="2">
  						
  						<%
                          			for(int i=0;i<posDescFiledList.size();i++)
									{                          			
                          				LazyDynaBean abean=(LazyDynaBean)posDescFiledList.get(i);
                          				LazyDynaBean nextBean=null;
                          				if((i+1)<posDescFiledList.size())
                          					nextBean=(LazyDynaBean)posDescFiledList.get(i+1);
                          				 String desc=(String)abean.get("desc");
                          				 String desc2="";
                          				 if(desc.length()==2)
                          				    desc=desc.charAt(0)+"&nbsp;&nbsp;&nbsp;&nbsp;"+desc.charAt(1);	
                          				 if(nextBean!=null&&((String)nextBean.get("desc")).length()==2)
                          					desc2=((String)nextBean.get("desc")).charAt(0)+"&nbsp;&nbsp;&nbsp;&nbsp;"+((String)nextBean.get("desc")).charAt(1);
                          				 else if(nextBean!=null)
								            desc2=((String)nextBean.get("desc"));
								                          			
                          				out.println("<tr>");	
                          				if((((String)abean.get("type")).equals("A")||((String)abean.get("type")).equals("N"))&&nextBean!=null&&(((String)nextBean.get("type")).equals("A")||((String)nextBean.get("type")).equals("N")))	
                          				{
                          					String value="";
                          					String nextValue="";
                          					if(abean.get("value")!=null)
                          						value=(String)abean.get("value");
                          					if(nextBean.get("value")!=null)
                          						nextValue=(String)nextBean.get("value");
                          					out.println("<td height='30'  class='trDeep' align='right' width='15%' ><b>"+desc+"</b></td>");
	                          				out.println("<td  width='35%' class='trShallow' align='left' >"+value+"</td>");
	                          				out.println("<td  width='15%' class='trDeep' align='right' ><b>"+desc2+"</b></td>");
	                          				out.println("<TD  width='35%' class='trShallow' align='left'  >"+nextValue+"</TD>");
	                          				i++;
                          				}
                          				else
                          				{
                          				    String avalue="";
                          				    if(abean.get("value")!=null)
	                          				    avalue=(String)abean.get("value");
                          				    avalue=avalue.replaceAll("\r\n","<br>");
                          				    avalue=avalue.replaceAll("\n\n","<br>");
                          				    if(((String)abean.get("type")).equals("M"))
                          				    {
                          						out.println("<td height='30' class='trDeep'  align='right' width='15%' valign='top' ><b>"+desc+"</b></td>");
                          						out.println("<td  align='left' class='trShallow'  colspan=3 valign='top' >"+avalue+"</td>");
                          					}
                          					else
                          					{
                          						out.println("<td height='30' class='trDeep'  align='right' width='15%' ><b>"+desc+"</b></td>");
                          						out.println("<td  align='left' class='trShallow'  colspan=3  >"+avalue+"</td>");
                          					}
                          				}
                          				
                          				out.print("</tr>");
                          			 
									}
									%>                          		
  						
  						
  						
  						
  						
  						
  						<tr>
						<td height='30' class='trDeep'  align='right' width='15%' ><b><bean:message key="hire.parameterSet.positionDescrible"/>:</b></td>
						<td  align='left' class='trShallow'  colspan=3  >
							<%
								if(isPosBooklet.equals("1"))
								{
							%>
									<a href='/servlet/performance/fileDownLoad?e01a1=<%=e01a1%>&opt=hire'  target="_blank"  border='0' >  
	                 	   			  &nbsp;<image src='/images/detail.gif' border=0 >
	                 	   		  	</a>
							<% 
								}
								else
								{
									out.println(ResourceFactory.getProperty("gz.bankdisk.no"));
								}
							
							 %>
						
						</td>
						</tr> 
  						
  						
  						
  						
  						
  						  					
  					 </table>
  					   	<% if(!"recommend".equals(request.getParameter("fromRecommend"))){
  					   	%>
  					   	<%
  					   	   if(bosflag!=null&&!bosflag.equals("hcm")){
  					   	%>
  					   	 <BR>
  					   	<%
  					   	 }
  					   	%>
  					 <table width="90%" border="0" cellspacing="2"  align="center" cellpadding="2" style="margin-top:5px;">
						<tr>
							<td align="center">
							<Input type='button' name='a' value=' <bean:message key="hire.will.apply"/> ' class="mybutton" onclick="sub()" >							
							<Input type='button' name='a' value='<bean:message key="button.return"/> ' class="mybutton" onclick="goback()" >
							</td>
						</tr>
					</table>
  					 
  					 
  					 <BR>
  					<%} %>
  					
  					
  					
  
  
			 </td>
          </tr>       
      </table>
      <!-- fromRecommend 来源于推荐岗位标识 -->
     <%if(!"recommend".equals(request.getParameter("fromRecommend"))){%>
  	  <Br>
  	  
  	   <table   width="80%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
  	   			  <tr>
  	   			  <td align="left" colspan='7' class="TableRow" nowrap>&nbsp;&nbsp;<bean:message key="hire.apply.position"/>:
  	   			  </tr>
				  <tr  class='trDeep' >		
					 <td align="center"  class="RecordRow" nowrap>&nbsp;<bean:message key="kq.shift.employee.e01a1"/>&nbsp;</td>
  					 <td align="center"  class="RecordRow" nowrap>&nbsp;<bean:message key="hire.employActualize.resumeState"/>&nbsp;</td>
  					 <td align="center"  class="RecordRow" nowrap>&nbsp;<bean:message key="lable.zp_plan.start_date"/>&nbsp;</td>
  					 <td align="center"  class="RecordRow" nowrap>&nbsp;<bean:message key="lable.zp_plan_detail.domain"/>&nbsp;</td>
  					 <td align="center"  class="RecordRow" nowrap>&nbsp;<bean:message key="lable.zp_plan_detail.amount"/>&nbsp;</td>
  					 <td align="center"  class="RecordRow" nowrap>&nbsp;<bean:message key="hire.wish.order"/>&nbsp;</td>
  					 <td align="center"  class="RecordRow" nowrap>&nbsp;<bean:message key="system.infor.oper"/>&nbsp;</td>
  				  </tr>
  				   <logic:iterate  id="element"    name="innerEmployPortalForm"  property="applyedPosList" indexId="index"> 
                      
	                      <tr align="center" class='trShallow' > 
	                        <td  align="left"  class="RecordRow"  height='20'>
	                      		<bean:write  name="element" property="posName"/>         			
	                      	</td>
	                      	<td  align="left"  class="RecordRow" >
	                      		<bean:write  name="element" property="unitName"/>        			
	                      	</td>
	                      	<td  class="RecordRow"  >
	                      		<bean:write  name="element" property="z0329"/>               			
	                      	</td>
	                      	
	                      	<td  align="left" class="RecordRow" >
	                      		<bean:write  name="element" property="z0333"/>              			
	                      	</td>
	                      	<td  align="right"  class="RecordRow"  >
	                      		<bean:write  name="element" property="z0315"/>        			
	                      	</td>
	                      	<td  align="right"  class="RecordRow" >
	                      		<bean:write  name="element" property="thenumber"/>             			
	                      	</td>
	                      	<td  align="center"  class="RecordRow" >
	                      	    <bean:define id="posid" name="element" property="zp_pos_id"/>
	                      		<a href='javascript:del("<%=PubFunc.encrypt((String)posid) %>")' ><bean:message key="kq.shift.cycle.del"/> </a>          			
	                      	</td>
	                      </tr>
                      
                      </logic:iterate>
  				  
  	   </table>
  	   <%} %>
  </html:form>
  
  </body>
  <script type="text/javascript">
  var layouttable=document.getElementById("layouttable");
  layouttable.style.width=screen.availWidth -25;
  </script>
</html>
