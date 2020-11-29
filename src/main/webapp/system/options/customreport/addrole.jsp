<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.sys.customreport.CustomReportForm"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<%
	int i=0;
	CustomReportForm customReportForm = (CustomReportForm)request.getSession().getAttribute("customReportForm");
	String rolesHas = customReportForm.getRolesHas();
%>

<style id=iframeCss>
div{
	cursor:hand;font-size:12px;
   }
a{
text-decoration:none;color:black;font-size:12px;
}

a.a1:active {
	color: #003100;
	text-decoration: none;
}
a.a1:hover {
	color: #FFCC00;
	text-decoration: none;
}
a.a1:visited {	
	text-decoration: none;
}
a.a1:link {
	color: #003100;
	text-decoration: none;
}
</style>
<script language="javascript">

   function save()
   {
      var len=document.customReportForm.elements.length;
      var i;
      var role_id="";
      for (i=0;i<len;i++)
      {
         if (document.customReportForm.elements[i].type=="checkbox"&&document.customReportForm.elements[i].checked==true&&document.customReportForm.elements[i].name!='selbox')
         {
              var vv=document.customReportForm.elements[i].value;
              var vo=document.getElementById(vv+"_h");
              if(vo!=null)
                role_id=role_id+vo.value+",";
         }
      }
      var thevo=new Object();
      if(role_id!="")
        thevo.role_id=role_id;
      else
        thevo.role_id="";
      // window.returnValue=thevo;
	  // window.close();
       parent.parent.return_vo = thevo;
       winClose();
   }
   function checkall() {
   	var obj = document.getElementsByName("selbox")[0];
   	var flag = false;
   	if (obj.checked == true) {
   		flag = true;
   	}
   	var len=document.customReportForm.elements.length;
    for (i=0;i<len;i++)
      {
         if (document.customReportForm.elements[i].type=="checkbox"&&document.customReportForm.elements[i].name!='selbox')
         {
             document.customReportForm.elements[i].checked=flag;
         }
      }
   }
   function selectAll()
   {
   		var len=document.customReportForm.elements.length;
   		for (i=0;i<len;i++)
     	{
           	if (document.customReportForm.elements[i].type=="checkbox")
            {
            	document.customReportForm.elements[i].checked=true ;
            }
     	}
   }
   
   function deleteAll()
   {
   		var len = document.customReportForm.elements.length;
   		for(i=0;i<len;i++)
   		{
   			if(document.customReportForm.elements[i].type=="checkbox")
   				document.customReportForm.elements[i].checked=false;
   		}
   }
   function winClose() {
       // parent.return_vo = '';
       if(parent.parent.Ext.getCmp('juesepower')){
           parent.parent.Ext.getCmp('juesepower').close();
       }
   }
</script>
<base target="_self"/>
<!-- 报表关联-角色授权 修改每页显示记录数   jingq add 2014.08.15 -->
<html:form action="/system/options/customreport/addjuese.do?b_addjuese=link">
<table width="100%" cellpadding="0" cellspacing="0" border="0" align="center" style="border-collapse: collapse;">
	<tr><td> 
	<table width="590" border="0" cellspacing="0"  align="center" cellpadding="0">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" width="40" nowrap style="border-right:none;">
		       <input type=checkbox name=selbox onclick=checkall(); title=<bean:message key='label.query.selectall' />  width=35>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap width="180" style="border-right:none;">
		<bean:message key="column.name"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.desc"/>&nbsp;
	    </td>
   		        	        	        
           </tr>
   	  </thead>
   	  </table>
   	  </td></tr>
   	  <tr><td valign="top" align="left" style="height: 400px;">
   	  <div style="height: 400px;width:590px;overflow-y:auto;position: absolute;border:1px solid #C4D8EE;border-top:none;" class="common_border_color">
   	  	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTableF" style="margin-top:-1px;border:none;">
          <hrms:extenditerate id="element" name="customReportForm" property="roleForm.list" indexes="indexes"  pagination="roleForm.pagination" pageCount="${customReportForm.countrows}" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>  
            <td align="center" class="RecordRow" nowrap width="40" style="border-left:none;">
            	<%if (rolesHas.indexOf(","+((RecordVo)element).getString("role_id")+",") != -1) {%>
                 <!--<hrms:checkmultibox name="customReportForm" property="roleForm.select" value='false' />-->
                 <input type="checkbox" name="chec<%=i+"" %>" value="<%=i+"" %>" checked="checked"/>&nbsp;
                 <%}else{ %>
                  <!--<hrms:checkmultibox name="customReportForm" property="roleForm.select" value='true' />-->
                   <input type="checkbox" name="chec<%=i+"" %>" value="<%=i+"" %>"/>&nbsp;
                 <%} %>
                 <html:hidden name="element" property="string(role_id)" styleId='<%=i+"_h"%>' styleClass="text"/>
            </td>            
            <td align="left" class="RecordRow" nowrap width="180">
                   &nbsp;<bean:write name="element" property="string(role_name)" filter="true"/>&nbsp;
	    </td>
         
            <td align="left" class="RecordRow" style="word-break:break-all;">
                    &nbsp;<bean:write  name="element" property="string(role_desc)" filter="false"/>&nbsp;
            </td>
          </tr>
        </hrms:extenditerate>
</table>
</div>
</td></tr>
<tr><td align="left">
<table  width="100%" align="left" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <hrms:paginationtag name="customReportForm" pagerows="${customReportForm.countrows}" property="roleForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
	        <td  align="right" nowrap class="tdFontcolor">
				 <p align="right"><hrms:paginationlink name="customReportForm" property="roleForm.pagination" nameId="roleForm"  propertyId="roleFormProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
</td></tr>
<tr><td>
<table  width="100%" align="center">
          <tr>
            <td align="center" height="35px;">	       
  	       <html:button styleClass="mybutton" property="b_juesesave" onclick="save();">
            	   	<bean:message key="button.save"/>
  	       </html:button> 
              <%--<html:button styleClass="mybutton" property="b_o_close" onclick="window.close();">--%>
            	   	<%--<bean:message key="button.close"/>--%>
  	       <%--</html:button>--%>
                <html:button styleClass="mybutton" property="b_o_close" onclick="winClose();">
                    <bean:message key="button.close"/>
                </html:button>
                   	           	          	   	 	  
            </td>
          </tr>          
</table>
</td></tr>
</table>
</html:form>
<script language="javascript">
  if(!getBrowseVersion() || getBrowseVersion() =='10'){
      var common_border_color = document.getElementsByClassName('common_border_color')[0];
      common_border_color.style.width='588px';
      if(getBrowseVersion() =='10') //ie11 非兼容
        common_border_color.style.marginLeft='5px';
      else//google
        common_border_color.style.marginLeft='1px';
  }
</script>
