<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<STYLE type=text/css>
.div2
{
 overflow:auto; 
 width: 100%;height: 230px;
 line-height:15px; 
 border-width:1px; 
 border-style: groove;
 border-width :thin ;
}
</STYLE>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
 <script language="JavaScript">
   	function savecode()
   	{
    	   var currnode,codeitemid;
    	   currnode=Global.selectedItem;
    	   if(currnode==null)
    	    	return;   
    	   codeitemid=currnode.uid;    	       	  
    	   if(root.getSelected()=="root,")
    	     statForm.querycond.value="";
    	   else    	     
    	     statForm.querycond.value=root.getSelected();
    	  <logic:equal name="userView" property="status" value="0"> 
    	    var curr_vos= document.getElementsByName('curr_id');
             var curr_vo=curr_vos[0];  
             var curr_value="";              
             for(var i=0;i<curr_vo.options.length;i++)
             {
                   if(curr_vo.options[i].selected)
                     curr_value=curr_vo.options[i].value ;         
             }      	     
    	     if(statForm.result.checked==true)
                statForm.result.value="1";
             else
                statForm.result.value="0";             
           
             if(curr_value!="#")
             {
                statForm.preresult.value="2";
             }
             else
             {
               if(root.getSelected()=="root")
               {
                 statForm.preresult.value="0";
               }else if(root.getSelected()=="")
               {
                 statForm.preresult.value="1";
               }else
               {
                 statForm.preresult.value="1";
               }                 
             }
             statForm.preresult.checked=true;   
			</logic:equal>
             statForm.submit();           
    	   //alert(root.getSelected());  
    	  // alert(window.opener.location.toString());
    	  
    	   <logic:equal name="statForm" property="isoneortwo" value="1">
    	      //window.opener.location.href="/workbench/stat/statshow.do?b_setreturnchart=link";  
    	        window.opener.location.href="/workbench/stat/statshow.do?b_chart=link&statid=${statForm.statid}"; 
    	   </logic:equal>
    	   <logic:equal name="statForm" property="isoneortwo" value="2">
    	      window.opener.location.href="/workbench/stat/statshow.do?b_doubledata=link&statid=${statForm.statid}";  
    	   </logic:equal>
    	  // window.opener.location.statForm.submit();	
    	   window.close();	
   	}
   	function preresultclick()
   	{
    	     	
  	    	if(statForm.preresult.checked==true)
             {
                statForm.preresult.value="2";
                 Element.show('querylist');
             }
             else
             {
               statForm.preresult.value="1";
               Element.hide('querylist');
             }
    	            
   	}
   	function setDefaultStat_Id(obj)
   	{
   	   var statid=obj.value;
   	   var hashvo=new ParameterSet();
           hashvo.setValue("statid",statid);
           var request=new Request({method:'post',asynchronous:false,functionId:'11080204030'},hashvo);
   	}
   </SCRIPT>
<html:form action="/workbench/stat/statset"> 

    <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >
        <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="3">
		      <bean:message key="workbench.stat.statsettitle"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>    	  
   	   <tr>
   	       <td align="left" width="35%" height="30"  nowrap>
     	    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="menu.base"/>
    	    </td>
            <td align="left"  nowrap>
    	         <hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="true" paraValue="" 
                 sql="statForm.dbcond" collection="list" scope="page"/>
                 <html:select name="statForm" property="userbase" size="1" style="width:220px;">                     
                      <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                 </html:select>
       </tr> 
      <logic:equal name="userView" property="status" value="0">
        <tr>
          <td align="left" height="30" nowrap>
                             
                             <html:hidden name="statForm" property="preresult"/> 
          	                 &nbsp;&nbsp;&nbsp;&nbsp;  <bean:message key="workbench.stat.commonfindresult"/>          
                               
          </td>
          <td align="left"  nowrap>          
                   <html:select name="statForm" property="curr_id" size="1" style="width:220px;">
                         <html:option value="#">请选择...&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                         <html:optionsCollection property="condlist" value="id" label="name"/>
                   </html:select>  
          </td>
       </tr>
     </logic:equal>
       <tr>
           <td align="left" colspan="2"> 
             <div id="tbl_container"  class="div2" >
                 <div id="treemenu"> 
                  <SCRIPT LANGUAGE=javascript>    
                   <bean:write name="statForm" property="treeCode" filter="false"/>
                  </SCRIPT>
                 </div> 
             </div>         
           </td>
        </tr>   
       <tr>
         <td align="left"  nowrap colspan="2">
                   <logic:equal name="userView" property="status" value="0">
          	            &nbsp;&nbsp;&nbsp;&nbsp;    <html:checkbox name="statForm" property="result" value="1"/>&nbsp;<bean:message key="workbench.stat.preresult"/>&nbsp;&nbsp;&nbsp;&nbsp;
                           <img  src="/images/code.gif" onclick='get_common_query("1",1)'/>
                  </logic:equal>
                           <html:hidden name="statForm" property="querycond"/>    
         </td>
       <tr>  
          <tr>
          <td align="center"  nowrap colspan="3">
               <input type="button" name="btncance2" value="<bean:message key="button.ok"/>" class="mybutton" onclick="savecode();"> 
	       <input type="button" name="btncancel" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="window.close();">  
          </td>
          </tr>
    </table>
      <script language="JavaScript">
      <logic:equal name="userView" property="status" value="0">
       preresultclick();
       </logic:equal>
    </script>
</html:form>
