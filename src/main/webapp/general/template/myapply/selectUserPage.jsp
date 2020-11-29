<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<script language="JavaScript" src="/js/calendar.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/dict.js"></script>
<script language="JavaScript" src="../template.js"></script>
<script language="JavaScript" src="../template_signature.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
<link href="/css/css1_template.css" rel="stylesheet" type="text/css">
<%@ page import="java.util.*,
				com.hrms.hjsj.utils.Sql_switcher,
				com.hrms.struts.constant.WebConstant,
				com.hrms.struts.constant.SystemConfig,
				org.apache.commons.beanutils.LazyDynaBean,
				com.hjsj.hrms.actionform.general.template.TemplateForm,
				com.hrms.struts.valueobject.UserView" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>	
<%
	TemplateForm templateForm=(TemplateForm)session.getAttribute("templateForm");
	HashMap specialRoleMap=templateForm.getSpecialRoleMap();
	String node_str="";
	
 %>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
    
%>  
<html>
  <head>
   

  </head>
  <hrms:themes />
  <body>
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<Br>
<%
}
%>
    <% if(specialRoleMap.size()==0){ %>
  <table width='100%' ><tr><td align='center' >审批关系中没有定义当前用户，不能继续报批! </td></tr></table>
    <% }else{ %>
    <table  id="cs" width="100%" align="center" border="0" cellpadding="0" cellspacing="0"  height="100%">
	<tr>  
    <td valign="top" align="left" height="100%">
      <fieldset align="center" style="width:97%; height:85%; ">
          <legend >选择审批用户</legend>
        	
        	<div style="height: 290px;overflow: auto; position:absolute; top:20px; width:100%;">
        	
        	 <% 
        	 	Set keySet=specialRoleMap.keySet();
        	 	for(Iterator t=keySet.iterator();t.hasNext();)
        	 	{
        	 		String key=(String)t.next();
        	 		ArrayList list=(ArrayList)specialRoleMap.get(key);
        	 		LazyDynaBean abean=(LazyDynaBean)list.get(0);
        	 		String actor_type=(String)abean.get("actor_type"); //1 自助用户  4：业务用户
        	 		String node_id=(String)abean.get("node_id"); 
        	 		node_str+=","+node_id;
        	 		String[] temps={"",""};
        	 		if(key==null)
        	 			key="";
        	 		else
	        	 		temps=key.split("`");	
        	  %>
        	<br> 
        	
        	&nbsp;&nbsp;&nbsp;&nbsp;<%=temps[0]%>
        
        	 <table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	         <thead>
        	  <tr>
              <td align="center" class="TableRow" nowrap >  &nbsp; </td>
              <% if(actor_type.equals("1")){ %>
              <td align="center" class="TableRow"  width="20%"  nowrap >单位名称</td>
              <td align="center" class="TableRow" width="20%" nowrap >部门</td>
              <td align="center" class="TableRow" width="20%" nowrap >职位名称</td>
              <td align="center" class="TableRow" width="20%" nowrap >姓名</td>
              <% }else{ %>
               <td align="center" class="TableRow" width="50%" nowrap >用户组</td>
              <td align="center" class="TableRow" width="40%" nowrap >用户名</td>  
              <% } %>
              
        	 </tr>
        	</thead>
        	
        	<% for(int n=0;n<list.size();n++){
        			  abean=(LazyDynaBean)list.get(n);
        			  String mainbodyid=(String)abean.get("mainbodyid");
        			  String a0101="";
        			   a0101 = abean.get("a0101")==null?"":""+abean.get("a0101");
        			
        			 
        	 %>
        	<tr class="trShallow"   > 
				 
			  <td align="center" class="RecordRow" nowrap > <input type='radio' value='<%=mainbodyid%>`<%=a0101%>' name='<%=node_id%>' /> </td>
              <% if(actor_type.equals("1")){ %>
              <td align="left" class="RecordRow" nowrap >&nbsp;<%=(String)abean.get("b0110")%></td>
              <td align="left" class="RecordRow" nowrap >&nbsp;<%=(String)abean.get("e0122")%></td>
              <td align="left" class="RecordRow" nowrap >&nbsp;<%=(String)abean.get("e01a1")%></td>
              <td align="left" class="RecordRow" nowrap >&nbsp;<%=(String)abean.get("a0101")%></td>
              <% }else{ %>
               <td align="left" class="RecordRow" nowrap >&nbsp;<%=(String)abean.get("groupname")%></td>
              <td align="left" class="RecordRow" nowrap >&nbsp;<%=(String)abean.get("mainbodyid")%></td>  
              <% } %> 
			</tr>
			
			<% } %>
			
		    </table>
		    
		    
		    <% } %>
        	
        	</div>
        	  <br><BR><BR>&nbsp;
         
      </fieldset>
      <table align="center" style="margin-top:5px;">
    		<tr >
		  	  <td> 
				<button name="import" Class="mybutton" onclick="sub()"><bean:message key="button.ok"/></button> 
				<button name="cancel" Class="mybutton" style="margin-left:10px;"onclick="window.close();"><bean:message key="button.cancel"/></button>
			  </td>
	    	</tr>
    	</table>
      
      
      
    </td>
    </tr>
    </table> 
    
 <% } %>   
    
  </body>
  
  <script language='javascript' >
  function sub()
  {
    var values='';
  	var node_str='<%=node_str%>';
  	var temps=node_str.split(',');
  	for(var i=0;i<temps.length;i++)
  	{
  		if(trim(temps[i]).length>0)
  		{
  			var obj=document.getElementsByName(temps[i]);
  			if(obj)
  			{
  				var j=0;
  				for(var n=0;n<obj.length;n++)
  				{
  					if(obj[n].checked)
	  				{	
	  					values=values+','+temps[i]+':'+obj[n].value;
	  					j=1;	
	  				}
  				}
  				
  				if(j==0)
  				{
  					alert('请选择审批用户!');
  					return;
  				}
  				
  			}
  		}
  	}
  	if(values.length>0)
	    returnValue=values.substring(1);
	window.close();	
  
  }
  
  </script>
  
</html>
