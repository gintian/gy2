 <%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
			     org.apache.commons.beanutils.LazyDynaBean,
			     java.util.*,com.hrms.hjsj.sys.ResourceFactory"%>
<html>
<head>
<LINK 
href="/css/hireNetStyle.css" type=text/css rel=stylesheet>

<style>
#idDIV{width:45px;height:3px;background-color:#FF6633;color:#FFFFFF;padding:4px;}


</style>

<script language='javascript'>
	function openwindow(url)
	{
		window.open("http://"+url,"blank");
	
	}
	
	function openwindow2(b0110)
	{
		window.open("/hire/hireNetPortal/search_zp_position.do?b_showContent=show&b0110="+b0110,"_blank");
	}
	
	
	function pf_ChangeFocus() 
    { 
      key = window.event.keyCode;     
      if ( key==0xD)
      {
      	if(event.srcElement.name=='loginName'||event.srcElement.name=='password')
      			login();
      	else if(event.srcElement.name!=null&&event.srcElement.name.length>14&&event.srcElement.name.substring(0,14)=='conditionField')
      			query()	
      			
      }
    }   
	

</script>
</head>
<body onKeyDown="return pf_ChangeFocus();" >
<html:form action="/hire/hireNetPortal/search_zp_position"> 
<br>	
			<%
			EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
			%>
<br>
<table width='93%' border='0' >
<tr>
<td width='85%' valign='top' >

<table width='100%'   ><tr>
		<td align='left' width='5%' >&nbsp;</td>	
		<td align='left' width='95%' >	
				
				  <% 
					  		ArrayList unitList=employPortalForm.getUnitList();
					  		int n=0;
					  		for(Iterator t=unitList.iterator();t.hasNext();)
					  		{
						  		LazyDynaBean aBean=(LazyDynaBean)t.next();
					  			String unitName=(String)aBean.get("name");
					  			String id=(String)aBean.get("id");
					  			ArrayList posList=(ArrayList)aBean.get("list");
					  			String content=(String)aBean.get("content");
					  			String contentType=(String)aBean.get("contentType");
				  %>
				  
				  
				  <table   class=rptTable  cellSpacing=1 
                        cellPadding=0 width="100%" align="center"  border=0 >
                    <tbody>
                      <tr align="left" bgcolor="#FFFFFF" > 
                        <td height="20" colspan="5">
                        
                        <b><%=unitName%></b>
                        <%
                        	
                       if(contentType!=null&&contentType.equals("0")&&content.trim().length()>0)
                       {
                       		out.print("<a href=\"javascript:openwindow('"+content+"')\" ><IMG border=0  title='"+ResourceFactory.getProperty("hire.unit.introduction")+"' src='/images/cards.bmp' ></a>");
                       }
                       if(contentType!=null&&contentType.equals("1"))
                       {
                       	out.print("<a href=\"javascript:openwindow2('"+id+"')\" ><IMG border=0  title='"+ResourceFactory.getProperty("hire.unit.introduction")+"' src='/images/cards.bmp' ></a>");
                       }
                       
                       if(n!=0)
                        		out.print("<br>");
                        	n++;
                        %>

                       </td>
                      </tr>
                      <tr align="center" > 
                        <td width="30%" height='25' class=rptHead  background=/images/r_titbg01.gif  ><font color='#DF0024'><bean:message key="lable.zp_plan_detail.pos_id"/></font></td>
                        <td width="20%" height='25' class=rptHead  background=/images/r_titbg01.gif ><font color='#DF0024'><bean:message key="lable.zp_plan_detail.domain"/></font> </td>
                        <td width="20%" height='25' class=rptHead  background=/images/r_titbg01.gif><font color='#DF0024'> <bean:message key="label.zp_release_pos.valid_date"/></font> </td>
                        <td width="15%" height='25' class=rptHead  background=/images/r_titbg01.gif><font color='#DF0024'> <bean:message key="hire.applay.personcount"/></font> </td>
                        <td width="15%" height='25' class=rptHead  background=/images/r_titbg01.gif><font color='#DF0024'> <bean:message key="hire.column.applay"/></font> </td>
                      </tr>
						
						  <%  
						  	for(int i=0;i<posList.size();i++)
						  	{
						  		LazyDynaBean bean=(LazyDynaBean)posList.get(i);
						  		String posName=(String)bean.get("posName");
						  		String z0333=(String)bean.get("z0333");
						  		String Z0331=(String)bean.get("z0331");
						  		String Z0329=(String)bean.get("z0329");
						  		String z0301=(String)bean.get("z0301");
						  		String z0311=(String)bean.get("z0311");
						  		String state=(String)bean.get("state");
						  		String count=(String)bean.get("count");
						  		
						  %>
                      <tr > 
                        <td align="left"   class=rptItemMain >   
                          &nbsp;&nbsp;
                          
                          <a href="/hire/hireNetPortal/search_zp_position.do?b_posDesc=link&posID=<%=z0311%>&z0301=<%=z0301%>&posName=<%=posName%>&unitName=<%=unitName%>" style="cursor:hand" target="_blank"> 
                           <font color='#0066ff' ><%=posName%> 
                           <% if(state.equals("1")){ out.print("<IMG border=0 src='/images/hot.gif' >");  } %>
                            </font>
                          </a> 
                          
                        </td>
                        <td height="20" align="left"   class=rptItemMain ><font color='#000000' ><%=z0333%></font></td>
                        <td align="center"   class=rptItemMain ><font color='#000000' ><%=Z0329%></font></td>
                        <td align="right"   class=rptItemMain ><font color='red' ><%=count%></font></td>
                       <td align="center"   class=rptItemMain >
                         <a href="/hire/hireNetPortal/search_zp_position.do?b_posDesc=link&posID=<%=z0311%>&z0301=<%=z0301%>&posName=<%=posName%>&unitName=<%=unitName%>" style="cursor:hand" target="_blank"> <img src="/images/yp.gif" border="0"> </a>
                         </td>
                           
                      </tr>
	                   	  <%
	                   	  	}
	                   	  %>
	                    </tbody>
	                  </table>
	                  
		                  <%
		                  }
		     			
		              %>
</td>

</tr></table>


</td></tr></table>


<Br>

</html:form>

</body>
</html>