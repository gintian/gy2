<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.selfinfomation.AgentForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
  String bosflag="";
  UserView userView=(UserView)session.getAttribute(WebConstant.userView);
  if(userView!=null)
  {
     bosflag=userView.getBosflag();
  }
%>
<script language="javascript">
  function adds()
  {
  	 var dw=450,dh=340,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
     var revo=showModalDialog('/selfservice/selfinfo/agent/agentinfo.do?b_edit=link&editflag=add','_blank',"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	 if(revo)
	 {
	    if(revo=="ok")
	    {
	      agentForm.action="/selfservice/selfinfo/agent/agentinfo.do?b_search=link";
          agentForm.submit();
	    }
	 }
	 
  }  
  function update(id)
  {
  	 var dw=450,dh=340,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
     var url="/selfservice/selfinfo/agent/agentinfo.do?b_edit=link&editflag=update&id="+id;
     var revo=showModalDialog(url,'_blank',"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	 if(revo)
	 {
	    if(revo=="ok")
	    {
	      agentForm.action="/selfservice/selfinfo/agent/agentinfo.do?b_query=link";
          agentForm.submit();
	    }
	 }
  }
  function purview(id)
  {
       agentForm.action="/selfservice/selfinfo/agent/purviewagent.do?b_query=link&id="+id;      
       agentForm.submit();
  }
  function deletes()
  {
     var len=document.agentForm.elements.length;
     var uu="";
     for (i=0;i<len;i++)
     {
         if (document.agentForm.elements[i].type=="checkbox")
         {
              if(document.agentForm.elements[i].checked==true)
              {
                uu="dd";
                break;
              }
        }
    }
    if(uu=="")
    {
      alert("请选择所要删除的记录！");
      return false;
    }
    if(confirm("确定删除所选信息？"))
    {
          agentForm.action="/selfservice/selfinfo/agent/agentinfo.do?b_delete=link";
          agentForm.submit();
    }
          
  }
 </script> 
 <hrms:themes />
<html:form action="/selfservice/selfinfo/agent/agentinfo" onsubmit="">
<% int i=0; %>
<table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0" >
   <tr>
       <td>
        <table  id="tbl_r"  width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
         <tr>
            <td align="center" class="TableRow" nowrap>
		      <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
            </td>    
                                
            <td align="center" class="TableRow" nowrap>
               单位
            </td>        
            <td align="center" class="TableRow" nowrap>
               部门
	        </td>
	        <td align="center" class="TableRow" nowrap>
               岗位
	        </td>
            <td align="center" class="TableRow" nowrap>
               姓名
	        </td>
            <td align="center" class="TableRow" nowrap>
               起始时间
	        </td>	       
	        <td align="center" class="TableRow" nowrap>
               结束时间
	        </td>  
	        <td align="center" class="TableRow" nowrap>
               编辑
	        </td>	   
	        <td align="center" class="TableRow" nowrap>
               授权
	        </td>	     
         </tr>
         <hrms:paginationdb id="element" name="agentForm" sql_str="agentForm.sql" table="" where_str="agentForm.where" columns="agentForm.column" order_by="agentForm.orderby" pagerows="${agentForm.pagerows}" page_id="pagination">
          <%
          if(i%2==0){ 
          %>
          <tr class="trShallow">
          <%
          }else{
          %>
          <tr class="trDeep">
          <%}i++;            
          %>  
          <%
             	LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
             	String agent_id=(String)abean.get("agent_id");  
             	String id=(String)abean.get("id"); 
             	String start=(String)abean.get("start_date"); 
             	if(start!=null&&start.length()>=10)
             	{
             	   start=start.substring(0,10);
             	}
             	String end=(String)abean.get("end_date"); 
             	if(end!=null&&end.length()>=10)
             	{
             	   end=end.substring(0,10);
             	}
          %>
            <hrms:agentmess id="<%=id%>" a0101="a0101" b0110="b0110" e0122="e0122" e01a1="e01a1"></hrms:agentmess>
            <td align="center" class="RecordRow" nowrap>
		          <hrms:checkmultibox name="agentForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
            </td> 
            <td class="RecordRow" nowrap>&nbsp;
			   ${b0110}
			</td>
            <td class="RecordRow" nowrap>&nbsp; 
                ${e0122}
            </td>          
            <td  class="RecordRow" nowrap>&nbsp; 
                 ${e01a1}
	        </td>
	        <td  class="RecordRow" nowrap>&nbsp; 
                ${a0101}
	        </td>
            <td align="left" class="RecordRow" nowrap>&nbsp; 
               <%=start %>
	        </td>
            <td align="left" class="RecordRow" nowrap>&nbsp; 
                 <%=end %>
	        </td>
	        <td align="center" class="RecordRow" nowrap>
	         <a href="###">  <img src="/images/edit.gif" onclick="update('<%=id %>');" align="absmiddle" border="0" /></a>
	         
	        </td>
	        <td align="center" class="RecordRow" nowrap>
             <a href="###">  
             <img src="/images/assign_priv.gif" onclick="window.location.replace('/selfservice/selfinfo/agent/proxyauthorization.do?b_query=link&id=<%=id %>');""purview('<%=id %>');" align="absmiddle" border="0"/>
             </a>
	        </td>	        
          </tr>
          </hrms:paginationdb>
        </table>
     </td>
  </tr>
  <tr>
    <td>
       <table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
					<hrms:paginationtag name="agentForm" pagerows="${agentForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="agentForm" property="pagination" nameId="agentForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
    </td>
  </tr>
  <tr>
    <td>
      <table width="85%" align="left" border="0" cellspacing="0" cellpadding="0">
		<tr height="35px;">
		    <td align="center">
		    	<input type="button" name="br_return" value='<bean:message key="button.insert"/>' class="mybutton hj-wzm-jtys-top-bctx" onclick="adds();"> 
		    	<input type="button" name="br_return" value='<bean:message key="button.delete"/>' class="mybutton hj-wzm-jtys-top-bctx" onclick="deletes();">
		    	<%if(bosflag!=null&&bosflag.equals("hl")){ %>
		    	  <input type="button" name="bc_btn1" value="返回" onclick="window.location.replace('/templates/index/portal.do?b_query=link');" class="mybutton">
			    <%} %>
			    <%if(bosflag!=null&&bosflag.equals("hcm")){ %>
                  <input type="button" name="bc_btn1" value="返回" onclick="window.location.replace('/templates/index/hcm_portal.do?b_query=link');" class="mybutton hj-wzm-jtys-top-bctx">
                <%} %>
			</td>
		</tr>
	</table>
    </td>
  </tr>
</table>
</html:form>
