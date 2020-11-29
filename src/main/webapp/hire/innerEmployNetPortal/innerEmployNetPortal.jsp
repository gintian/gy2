<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<%@ page import="com.hjsj.hrms.actionform.hire.innerEmployNetPortal.InnerEmployPortalForm,
                 com.hjsj.hrms.actionform.sys.cms.ChannelForm,
                 com.hrms.hjsj.sys.EncryptLockClient,
			     org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.utils.PubFunc,
			     java.util.*,com.hrms.hjsj.sys.ResourceFactory,com.hrms.frame.codec.SafeCode"%>

<%
    boolean isFive=false;
    EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
    if(lockclient!=null){
       if(lockclient.getVersion()>=50)
           isFive=true;
     }
 %>
<html>
<script language='javascript'>
	function more(id)
	{
		
		document.innerEmployPortalForm.action="/hire/innerEmployNetPortal/initInnerEmployPos.do?br_more=link&unitCode="+id;
		document.innerEmployPortalForm.submit();
	}

	function openwindow(url)
	{
	   if(url.toLowerCase().indexOf("http")==-1)
	       url="http://"+url;
		window.open(url,"_blank");
	
	}
	
	function openwindow2(b0110)
	{
		window.open("/hire/employNetPortal/search_zp_position.do?b_showContent=show&b0110="+b0110,"_blank");
	}
	function newopenwindow2(b0110)
	{
		//因内容数据过大，js中eval函数会将大字符串截断，故不能用ajax进行数据提交
		//var hashvo=new ParameterSet();
		//hashvo.setValue("b0110",b0110);
		//var In_paramters="b0110="+b0110;  
		//var request=new Request({method:'post',asynchronous:true,parameters:'',onSuccess:showContent,functionId:'3000000207'},hashvo);
	    //window.open("/hire/hireNetPortal/search_zp_position.do?b_showCompany=show&b0110="+b0110,"_blank");
	    var strurl="/hire/hireNetPortal/search_zp_position.do?b_showCompany=show&b0110="+b0110;
	    var iframe_url="/templates/index/iframe_query.jsp?src="+strurl; 
	    var flag=window.showModalDialog(strurl, arguments, "dialogWidth:1000px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no;"); 
	}
	function showContent(outparamters)
	{
		var info=outparamters.getValue("info");
		alert(info);
	}
function uploadAttach()
{
    document.innerEmployPortalForm.action="/hire/innerEmployNetPortal/initInnerEmployPos.do?b_attach=link";
	document.innerEmployPortalForm.submit();
}

</script>

<body>
<html:form action="/hire/innerEmployNetPortal/initInnerEmployPos"> 

<%
			InnerEmployPortalForm employPortalForm=(InnerEmployPortalForm)session.getAttribute("innerEmployPortalForm");
			String pc=employPortalForm.getPosCount();
			int posCount=7;
			if(pc!=null&&pc.trim().length()>0)
			    posCount=Integer.parseInt(pc);
			
%>

 
 		<% 
				  
				  		
				  		if(request.getParameter("unitCode")==null)
				  		{
					  		ArrayList unitList=employPortalForm.getUnitList();
					  		int n=0;
					  		for(Iterator t=unitList.iterator();t.hasNext();)
					  		{
						  		LazyDynaBean aBean=(LazyDynaBean)t.next();
					  			String unitName=(String)aBean.get("name");
					  			String id=PubFunc.encrypt((String)aBean.get("id"));
					  			ArrayList posList=(ArrayList)aBean.get("list");
					  			String content=(String)aBean.get("content");
					  			String contentType=(String)aBean.get("contentType");
					  			String uu=SafeCode.encode(unitName);
				  %>
				  
				  <br>
				  <table   width="80%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
				  <tr  class='trShallow' >		
					 <td align="left" colspan='3' class="TableRow" nowrap>&nbsp;&nbsp;<b><%=unitName%></b>
					 <%
                        	
                       if(contentType!=null&&contentType.equals("0")&&content.trim().length()>0)
                       {
                       		out.print("<a href=\"javascript:openwindow('"+content+"')\" ><IMG border=0  title='"+ResourceFactory.getProperty("hire.unit.introduction")+"' src='/images/cards.bmp' ></a>");
                       }
                       if(contentType!=null&&contentType.equals("1"))
                       {
                          if(isFive)
                            out.print("<a href=\"javascript:newopenwindow2('"+id+"')\" ><IMG border=0  title='"+ResourceFactory.getProperty("hire.unit.introduction")+"' src='/images/cards.bmp' ></a>");
                          else
                        	out.print("<a href=\"javascript:openwindow2('"+id+"')\" ><IMG border=0  title='"+ResourceFactory.getProperty("hire.unit.introduction")+"' src='/images/cards.bmp' ></a>");
                       }
                       
                       if(n!=0)
                        		out.print("<br>");
                        	n++;
                        %>
					 </td>				
				  </tr>
				  <tr class='trDeep'  >		
				 	<td align="left" width='60%' class="RecordRow"  nowrap>&nbsp;<bean:message key="lable.zp_plan_detail.pos_id"/>&nbsp;</td>
					 <td align="left" width='20%' class="RecordRow"  nowrap>&nbsp;<bean:message key="lable.zp_plan_detail.domain"/>&nbsp;</td>
					 <td align="center" width='20%' class="RecordRow"  nowrap>&nbsp;<bean:message key="label.zp_release_pos.valid_date"/>&nbsp;</td>
				  </tr>	

						  <%  
						  	for(int i=0;i<posList.size();i++)
						  	{
						  		if(i>=posCount)
						  			break;
						  		LazyDynaBean bean=(LazyDynaBean)posList.get(i);
						  		String posName=(String)bean.get("posName");
						  		String z0333=(String)bean.get("z0333");
						  		String Z0331=PubFunc.encrypt((String)bean.get("z0331"));
						  		String Z0329=(String)bean.get("z0329");
						  		String z0301=PubFunc.encrypt((String)bean.get("z0301"));
						  		String z0311=(String)bean.get("z0311");
						  		String state=(String)bean.get("state");
						  		String pp=SafeCode.encode(posName);
						  		
						  %>
                      <tr class='trShallow' >
                        <td align="left" class="RecordRow"  nowrap>   
                          &nbsp;&nbsp;
                          
                          <a href="/hire/innerEmployNetPortal/initInnerEmployPos.do?b_posDesc=link&z0301=<%=z0301%>&posName=<%=pp%>&unitName=<%=uu%>"> 
                           <font color='#0066ff' ><%=posName%></font>
                          </a> 
                          
                        </td>
                        <td align="left" class="RecordRow"  nowrap><font color='#000000' ><%=z0333%></font></td>
                        <td align="center"  class="RecordRow"  nowrap><font color='#000000' ><%=Z0329%></font></td>
                      </tr>
	                   	  <%
	                   	  	}
	                   	  	if(posList.size()>posCount)
	                   	  	{
	                   	  %>
	                      <tr > 
	                        <td align="left" class="RecordRow"  colspan='3'  nowrap>
	                         <div align="right"><a href="javascript:more('<%=id%>')"><font color='#000000' > <bean:message key="hire.column.more"/></font></a>&nbsp;&nbsp;</div></td>
	                      </tr>
	                      <% }
	                      
	                       if(posList.size()==0)
	                   	  	{
	                   	  %>
	                      <tr > 
	                        <td align="left" class="RecordRow"  colspan='3'  nowrap>
	                         <div align="left">&nbsp;&nbsp;<font color='#000000' > <bean:message key="hire.notinner.position"/></font>&nbsp;&nbsp;</div></td>
	                      </tr>
	                      <% } %>
	                      
	                    </tbody>
	                  </table>
	                  
		                  <%
		                  }
		     			}
		               else
		               {
		               		String unitCode=request.getParameter("unitCode");
		               		HashMap unitMap=employPortalForm.getUnitPosMap();
		               		ArrayList posList=(ArrayList)unitMap.get(unitCode);
		               		
		               		LazyDynaBean abean=(LazyDynaBean)posList.get(0);
		               		String unitName=(String)abean.get("unitName");
		                  %>
	                  		 <table   width="80%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
								<tr  class='trShallow' >		
									 <td align="left" colspan='3'  class="TableRow"   nowrap>&nbsp;&nbsp;<b><%=unitName%></b></td>				
								</tr>
								<tr class='trDeep'  >		
									 <td align="left" class="RecordRow"  nowrap>&nbsp;<bean:message key="lable.zp_plan_detail.pos_id"/>&nbsp;</td>
									 <td align="left" class="RecordRow"  nowrap>&nbsp;<bean:message key="lable.zp_plan_detail.domain"/>&nbsp;</td>
									 <td align="center" class="RecordRow"  nowrap>&nbsp;<bean:message key="label.zp_release_pos.valid_date"/>&nbsp;</td>
								</tr>	
								
								  <%  
								  	for(int i=0;i<posList.size();i++)
								  	{
								  		
								  		LazyDynaBean bean=(LazyDynaBean)posList.get(i);
								  		String posName=(String)bean.get("posName");
								  		String z0333=(String)bean.get("z0333");
								  		String Z0331=(String)bean.get("z0331");
								  		String Z0329=(String)bean.get("z0329");
								  		String z0301=PubFunc.encrypt((String)bean.get("z0301"));
								  		String z0311=(String)bean.get("z0311");
								  		String state=(String)bean.get("state");
								  %>
		                      <tr > 
		                       <td align="left" class="RecordRow"  nowrap>  
		                         &nbsp;&nbsp; <a href="/hire/innerEmployNetPortal/initInnerEmployPos.do?b_posDesc=link&z0301=<%=z0301%>&posName=<%=posName%>&unitName=<%=unitName%>"> 
		                             <font color='#0066ff' > <%=posName%></font>
		                          </a> 
		                        </td>
		                        <td align="left" class="RecordRow"  nowrap><font color='#000000' ><%=z0333%></font></td>
		                        <td align="center" class="RecordRow"  nowrap><font color='#000000' ><%=Z0329%></font></td>
		                      </tr>
			                   	  <%
			                   	  	}
			                   	  %>
			                    </tbody>
			                  </table>
	        
                  		<%
                  		}
                  		%>
                  		<br>
                  		<table   width="80%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
 <tr>
 <td align="left"><input type="button" name="upl" value="上传简历附件" class="mybutton" onclick="uploadAttach();"/></td>
 </tr>
 </table>

                  		
<br><br>
</html:form>
</body>
</html>