<%@page import="com.hjsj.hrms.actionform.sys.AccountForm"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="org.apache.commons.beanutils.LazyDynaBean,com.hrms.frame.dao.RecordVo"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>

<%
//String account_logon_interval=SystemConfig.getPropertyValue("account_logon_interval");
	String    account_logon_interval=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.ACCOUNT_LOGON_INTERVAL);
	int paramlength = account_logon_interval.length();
	AccountForm accountForm = (AccountForm)session.getAttribute("accountForm");
	String dbpre = accountForm.getDbpre();
	String loguser = accountForm.getLoguser();
	String cond_str = accountForm.getCond_str();
	cond_str = PubFunc.decrypt(cond_str);
 %>
<hrms:themes></hrms:themes>
<script language="javascript">
   function change()
   {
      accountForm.action="/system/security/assign_login.do?b_query=link";
      accountForm.submit();
   }
  function createPWD(type)
  {
  if("1"==type){
  var len=document.accountForm.elements.length;
	       var uu;
	       for(var i=0;i<len;i++)
		   {			
		      if(document.accountForm.elements[i].type=='checkbox'&&document.accountForm.elements[i].name!="selbox")
		      {	
		         if(document.accountForm.elements[i].name!="orglike2"&&document.accountForm.elements[i].name!="querlike2")
			     {
	               if(document.accountForm.elements[i].checked==true)
	                {
	                  uu="dd";
	                  break;
	               }
	             }
	          }
	       }
	       if(uu!="dd")
	       {
	          alert(NOTING_SELECT);
	          return false;
	       }
	       }
    if(confirm(LBL_SYS_PWD))
    {
       accountForm.action="/system/security/assign_login.do?b_create=create&createtype="+type;
       accountForm.submit();
    }
  }
  
  function lockunlock(type){
  	var s = document.getElementById("hid").value;
  	if(s==""){
  		alert('<bean:message key="error.assign_login.lockunlock.message"/>');
  		return;
  	}
  	var len=document.accountForm.elements.length;
	       var uu;
	       for(var i=0;i<len;i++)
		   {		
		      if(document.accountForm.elements[i].type=='checkbox'&&document.accountForm.elements[i].name!="selbox")
		      {	
		         if(document.accountForm.elements[i].name!="orglike2"&&document.accountForm.elements[i].name!="querlike2")
			     {
	               if(document.accountForm.elements[i].checked==true)
	                {
	                  uu="dd";
	                  break;
	               }
	             }
	          }
	       }
	       if(uu!="dd")
	       {
	          alert(NOTING_SELECT);
	          return false;
	       }
	     accountForm.action="/system/security/assign_login.do?b_lock=link&type="+type;
      	 accountForm.submit(); 
  }
  	function selectQ()
   {
       var a_code="<%=request.getParameter("a_code") %>";
         
       //var thecodeurl="/general/inform/search/gmsearch.do?b_query=link&type=1&a_code="+a_code+"&tablename="+tablename;
       var thecodeurl="/general/inform/search/generalsearch.do?b_query=link&type=1&a_code="+a_code+"&tablename=${accountForm.dbpre}&fieldsetid=A01";
       var dw=700,dh=370,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
       
       if(window.showModalDialog){
    	   var  return_vo= window.showModalDialog(thecodeurl, "", 
    	              "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no"); 
    	   if(return_vo!=null){
               var expr= return_vo.expr;
               var factor=return_vo.factor;
               var history=return_vo.history;
               var o_obj=document.getElementById('factor');
               o_obj.value=factor;
               o_obj=document.getElementById('expr');          
               o_obj.value=expr;
               o_obj=document.getElementById('history');
               o_obj.value=history;
               document.getElementById('likeflag').value=return_vo.likeflag;
               accountForm.action="/system/security/assign_login.do?b_query=link";
               accountForm.submit();
         } 
       }else{
    	   window.open(thecodeurl,'','width='+dw+',height='+dh+',left='+dl+',top='+dt+',resizable=no,titlebar=yes,location=no,status=no,scrollbars=no');
      	 	window.selectReturn = function(return_vo){
      	 		if(return_vo!=null){
      	            var expr= return_vo.expr;
      	            var factor=return_vo.factor;
      	            var history=return_vo.history;
      	            var o_obj=document.getElementById('factor');
      	            o_obj.value=factor;
      	            o_obj=document.getElementById('expr');          
      	            o_obj.value=expr;
      	            o_obj=document.getElementById('history');
      	            o_obj.value=history;
      	            document.getElementById('likeflag').value=return_vo.likeflag;
      	            accountForm.action="/system/security/assign_login.do?b_query=link";
      	            accountForm.submit();
      	      } 
      	 		
      	 	}
      	 	
       }
       
      
   }
   
   
   function batchassignrole(){
   		var len=document.accountForm.elements.length;
	       var uu;
	       for(var i=0;i<len;i++)
		   {			
		      if(document.accountForm.elements[i].type=='checkbox'&&document.accountForm.elements[i].name!="selbox")
		      {	
		         if(document.accountForm.elements[i].name!="orglike2"&&document.accountForm.elements[i].name!="querlike2")
			     {
	               if(document.accountForm.elements[i].checked==true)
	                {
	                  uu="dd";
	                  break;
	               }
	             }
	          }
	       }
	       if(uu!="dd")
	       {
	          alert(NOTING_SELECT);
	          return false;
	       }     	       
	       accountForm.action="/system/security/assign_role.do?b_query=link&encryptParam=<%=PubFunc.encrypt("ret_ctrl=0&a_userflag=1&a_roleid=batch")%>";
   			accountForm.submit();	
   }
   
//2013-10-29,yangj,排查重复用户；
function repeat(){
	var request=new Request({method:'post',asynchronous:false,onSuccess:repeat_ok,functionId:'1010010117'},null);
}

 //保存的回调方法
function repeat_ok(outparamters){   
	 var flag=outparamters.getValue("flag");
	 if(flag=="exist"){	   
		 alert("无重复用户名！");  
		 return false;	
	 }else if(flag=="ok"){
		 var dw=600,dh=500,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
		 var strurl="/system/security/role_repeat.do?b_repeat=link";
		 var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl;
		 var return_vo=showModalDialog(iframe_url,null,"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
		 //window.location.href="/system/security/assign_role.do?br_return=link";
		 window.location.href="/system/security/assign_login.do?b_query=link&encryptParam=<%=PubFunc.encrypt("a_code=UN")%>";
		 //window.location.href="/system/security/assign_role.do?br_return=";
	}
}
</script>
<%
RecordVo vo = new RecordVo("usra01");
int i=0;%>
<html:form action="/system/security/assign_login" focus="a0101">
<html:hidden name="accountForm" property="factor" styleId="factor"/>
<html:hidden name="accountForm" property="expr" styleId="expr"/>
<html:hidden name="accountForm" property="history" styleId="history"/>
<html:hidden name="accountForm" property="likeflag" styleId="likeflag"/>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >
 <tr>
    <td align="left" nowrap>
    	<table border="0" cellspacing="1" cellpadding="1">
    	<tr><td valign="middle">
        <bean:message key="label.query.dbpre"/>
        </td><td valign="middle">
     	<hrms:optioncollection name="accountForm" property="dblist" collection="list" />
             <html:select name="accountForm" property="dbpre" size="1" onchange="change();">
             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
        </html:select>
        </td><td valign="middle"> 
        <bean:message key="label.title.name"/>
        </td><td valign="middle">
        <html:text name="accountForm" property="a0101" size="20" maxlength="30" styleClass="text4"></html:text>
        </td><td valign="middle">
        <html:button styleClass="mybutton" property="b_all" onclick="change();"><bean:message key="button.query"/></html:button>  
        <html:button styleClass="mybutton" property="b_all" onclick="selectQ();"><bean:message key="button.sys.cond"/></html:button>
        </td></tr></table>  
    </td>         
 </tr>
</table>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:5px;">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
            <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;	    
            </td>              
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="B0110" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
            </td>    
            <%if(vo.hasAttribute("e0122")) {%>       
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="E0122" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
	    <%} %>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="E01A1" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="A0101" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;          	
	    </td>
	    <hrms:priv func_id="3003201,08030101">
            <td align="center" class="TableRow" nowrap>
		<bean:message key="setaccount.label"/>            	
	    </td>
	    </hrms:priv>
	    <!--
            <td align="center" class="TableRow" nowrap>
		<bean:message key="account.label"/>            	
	    </td>
	    -->
	    <hrms:priv func_id="3003202,08030102">
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.role.assign"/>            	
	    </td>
	    </hrms:priv>
        <td align="center" class="TableRow" nowrap>
		<bean:message key="label.manage"/>            	
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.account.flag"/>            	
	    </td>	
        <td align="center" class="TableRow" nowrap>
			<bean:message key="button.resource.assign"/>            	
	    </td>		    
		<hrms:priv func_id="30035,080102">  
            <td align="center" class="TableRow" nowrap>
				<bean:message key="label.priv.mx"/>            	
	   		</td>				
		</hrms:priv>	        	    	    	    		        	        	        
           </tr>
   	  </thead>
          <hrms:paginationdb id="element" name="accountForm" sql_str="accountForm.sql_str" table="" where_str="<%=cond_str %>" columns="${accountForm.columns}" order_by=" order by a0000" pagerows="${accountForm.pagerows}" page_id="pagination" indexes="indexes" keys="${accountForm.dbpre}a01.a0100">
          <%
            	LazyDynaBean item=(LazyDynaBean)pageContext.getAttribute("element");
            	String desc=(String)item.get("e0122");
           		String a0100 = (String)item.get("a0100");
           		String a0101 = (String)item.get("a0101");
           		String user = (String)item.get(loguser);
          if(i%2==0)
          {
          %>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          <%}
          else
          {%>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'')">
          <%
          }
          i++;          
          %>  
            <td align="center" class="RecordRow" nowrap>
                 <logic:notEmpty name="element" property="${accountForm.loguser}">             
                   <hrms:checkmultibox name="accountForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
            	 </logic:notEmpty>                      
	    </td> 	
            <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
	    </td>     
	    <%if(vo.hasAttribute("e0122")) {%>         
            <td align="left" class="RecordRow"  title="<hrms:orgtoname codeitemid='<%=desc%>' level="10"/>" nowrap>
          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page" uplevel="${accountForm.uplevel}"/>  	      
          	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;    
	    </td>
	    <%} %>
            <td align="left" class="RecordRow" nowrap title=''>
                <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;     
	    </td>
            <td align="left" class="RecordRow" nowrap>
                 &nbsp;<bean:write name="element" property="a0101" filter="true"/>&nbsp;
	    </td>
	    <hrms:priv func_id="3003201,08030101">
            <td align="center" class="RecordRow" nowrap>
            	<a href="/system/security/setlogin_info.do?b_query=link&encryptParam=<%=PubFunc.encrypt("a_id="+a0100)%>"><img src="/images/edit.gif" border=0 align="middle"></a>
             	
	    </td>
	    </hrms:priv>
	    <!--
            <td align="center" class="RecordRow" nowrap>
                 <bean:write name="element" property="string(username)" filter="true"/>&nbsp;
	    </td>
	    -->
	    <hrms:priv func_id="3003202,08030102">
            <td align="center" class="RecordRow" nowrap>
		<a href="/system/security/assign_role.do?b_query=link&encryptParam=<%=PubFunc.encrypt("ret_ctrl=0&a_userflag=1&a_roleid="+a0100) %>"><img src="/images/role_assign.gif" border=0 align="middle"></a>            	
	    </td>
	    </hrms:priv>
	    	    
            <td align="center" class="RecordRow" nowrap>
            <!-- 
		<a href="/system/security/assignpriv.do?b_query=link&a_flag=4&a_tab=funcpriv&role_id=${accountForm.dbpre}<bean:write name="element" property="a0100" filter="true"/>"><img src="/images/assign_priv.gif" border=0 align="middle"></a>
		 -->
		<a href="/system/security/assignpriv_tab.do?br_query=link&encryptParam=<%=PubFunc.encrypt("user_flag=4&a_tab=funcpriv&role_id="+dbpre+a0100+"&rp=0"+"&role_name="+a0101) %>"><img src="/images/assign_priv.gif" border=0 align="middle"></a>
	    </td>
            <td align="center" class="RecordRow" nowrap>
            <%if(paramlength>0){ %>
            <logic:notEmpty name="accountForm" property="lockfield"> 
            	<logic:equal value="1" name="element" property="${accountForm.lockfield}">
            		<img src="/images/lock.png" border=0 align="middle" title='<bean:message key="label.account.lock"/>'>
            	</logic:equal> 
            	<logic:notEqual value="1" name="element" property="${accountForm.lockfield}">
            		<logic:notEmpty name="element" property="${accountForm.loguser}">              
                    	<img src="/images/unlock.gif" border=0 align="middle" title='<bean:message key="label.account.assign"/>'>
            	    </logic:notEmpty> 
            	</logic:notEqual>
            </logic:notEmpty>
            <logic:empty  name="accountForm" property="lockfield">
            	<logic:notEmpty name="element" property="${accountForm.loguser}">              
                    <img src="/images/unlock.gif" border=0 align="middle" title='<bean:message key="label.account.assign"/>'>
            	</logic:notEmpty> 
            </logic:empty>      
            	<%}else{ %>
            	<logic:notEmpty name="element" property="${accountForm.loguser}">              
                    <img src="/images/unlock.gif" border=0 align="middle" title='<bean:message key="label.account.assign"/>'>
            	</logic:notEmpty>
            	<%} %>           	 		
	    </td>
        <td align="center" class="RecordRow" nowrap>
		<a href="/system/security/assign_resource.do?encryptParam=<%=PubFunc.encrypt("fromflag=2&flag=4&roleid="+dbpre+a0100+"&role_name="+a0101) %>"><img src="/images/book.gif" border=0 align="middle"></a>
	    </td>		    	 
		<hrms:priv func_id="30035,080102">  
            <td align="center" class="RecordRow" nowrap>  <%--添加参数控制弹窗返回按钮  wangb 20190523 bug 48267--%>           
       		<a href="/system/options/userpopedom.do?b_query=link&encryptParam=<%=PubFunc.encrypt("operatorflag=1&modeflag=1&dbpre="+dbpre+"&name="+user) %>&callback=true"><img src="/images/viewpriv.gif" border=0 align="middle"></a>            	
	   		</td>				
		</hrms:priv>		       	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
</table>
<table width="100%"  align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            	<hrms:paginationtag name="accountForm"
								pagerows="${accountForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="accountForm" property="pagination" nameId="accountForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
<table  width="100%" align="center">
          <tr>
            <td align="center" height="35px;">
            <!--  
         	<html:button styleClass="mybutton" property="b_all" onclick="setCheckState('1','pagination.select');">
            		<bean:message key="label.query.selectall"/>
	 	</html:button>	
         	<html:button styleClass="mybutton" property="b_all" onclick="setCheckState('2','pagination.select');">
            		<bean:message key="label.query.clearall"/>
	 	</html:button>	
	 	-->   	
	 		 <hrms:priv func_id="3003204,08030104">        
         	<html:button styleClass="mybutton" property="b_create" onclick="createPWD('1');">
            		<bean:message key="button.account.createpwd"/>
	 	    </html:button>
	 	    </hrms:priv>
	 	    <hrms:priv func_id="3003205,08030105">        
	 	    <html:button styleClass="mybutton" property="b_create" onclick="createPWD('2');">
            		<bean:message key="button.account.allcreatepwd"/>
	 	    </html:button>
	 	    </hrms:priv>
	 	    <% if(paramlength>0){ %>
	 	    	 <hrms:priv func_id="3003203,08030103">
		 	    <html:button styleClass="mybutton" property="b_lock" onclick="lockunlock('1');">
	            		<bean:message key="label.account.set.lock"/>
		 	    </html:button>
		 	    <html:hidden styleId="hid" name="accountForm" property="lockfield"/>
		 	    <html:button styleClass="mybutton" property="b_unlock" onclick="lockunlock('2');">
	            		<bean:message key="label.account.set.unlock"/>
		 	    </html:button>
		 	    </hrms:priv>
	 	    <%} %>
	 	    <hrms:priv func_id="3003202,08030102">
	 	    <html:button styleClass="mybutton" property="b_assignrole" onclick="batchassignrole()">
            		<bean:message key="system.options.customreport.button.juesepower.batch"/>
	 	    </html:button>
	 	    </hrms:priv>
	 	    <html:button styleClass="mybutton" property="b_query" onclick="repeat();">
		 	    <bean:message key="button.account.repeat.role"/>           		
	 	    </html:button>
			<!- 55019 前端传sql参数比较危险,没有发现此参数有什么作用，注释掉 guodd 2019-11-19
			html:hidden name="accountForm" property="sql_str"/->
			<!-html:hidden name="accountForm" property="columns"/->
	 	    <html:hidden name="accountForm" property="cond_str"/>
	 	    <html:hidden name="accountForm" property="loguser"/>
            </td>
          </tr>          
</table>
</html:form>
