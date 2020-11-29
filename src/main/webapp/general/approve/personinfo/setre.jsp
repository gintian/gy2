<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="java.util.*"%>
<%@ page import="com.hjsj.hrms.actionform.general.approve.personinfo.ApprovePersonForm"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript">
<!--
	function full(){ 
  			for(var i=0;i<document.forms[0].elements.length;i++)
				{			
					if(document.forms[0].elements[i].type=='checkbox')
			   		{	
						document.forms[0].elements[i].checked =approvePersonForm.sfull.checked;
					}
				}
		}
	
	function go_returnb(a_code)
   {
       approvePersonForm.action="/general/approve/personinfo/showstatret.do?b_query=link&a_code="+a_code;
       approvePersonForm.submit();
   }
 
  function go_return()
   {
       //approvePersonForm.action="/general/approve/personinfo/sumre.do?b_query=link&abkflag=a";   
       approvePersonForm.action ="/general/approve/personinfo/sum.do";
       approvePersonForm.target="il_body";
       approvePersonForm.submit();
   }
   function go_update()
   {
       approvePersonForm.action="/general/approve/personinfo/setre.do?b_update=link&todo=1";
       
       approvePersonForm.submit();
   }
   function go_reject(){
   		approvePersonForm.action="/general/approve/personinfo/setre.do?b_update=link&todo=0";
      
       approvePersonForm.submit();
   }
   
   
   function go_aupdate()
   {
       approvePersonForm.action="/general/approve/personinfo/setre.do?b_update=link&todo=aok&scope=a";
       
       approvePersonForm.submit();
   }
   function go_areject(){
   		approvePersonForm.action="/general/approve/personinfo/setre.do?b_update=link&todo=arj&scope=a";
      
       approvePersonForm.submit();
   }
   function go_aok()
   {
        approvePersonForm.action="/general/approve/personinfo/setre.do?b_update=link&todo=upok&scope=a";  
   	approvePersonForm.submit();
   }
   function go_aundo()
   {
        approvePersonForm.action="/general/approve/personinfo/setre.do?b_update=link&todo=upno&scope=a";  
        approvePersonForm.submit();
   }
   
   function go_ok(){
   	approvePersonForm.action="/general/approve/personinfo/setre.do?b_update=link&todo=5";  
   	approvePersonForm.submit();
   }
   function go_undo(){
   
   approvePersonForm.action="/general/approve/personinfo/setre.do?b_update=link&todo=1";  
   approvePersonForm.submit();
   }
   function sinfo(pdbflag,userid){
 //  approvePersonForm.action="/general/approve/personinfo/showpersoninfo.do?b_query=link&pdbflag="+pdbflag+"a0100="+userid;
   window.open("/general/approve/personinfo/showpersoninfo.do?b_query=link&pdbflag1="+pdbflag+"&a01001="+userid);
   }
   function openpage(a00){
   
   	var theurl="/general/approve/personinfo/showpersoninfo.do?b_query=link&pdbflag1=${approvePersonForm.pdbflag}&a01001="+a00;
	var retvalue=	window.showModalDialog(theurl, false, 
        "dialogWidth:800px; dialogHeight:1000px;resizable:no;center:yes;scroll:yes;status:no;");   
	if(retvalue!='re'){
		approvePersonForm.action="/general/approve/personinfo/setre.do";
		approvePersonForm.submit();
	}else{
		openpage(a00);
	}
	}
	
	function go_returnSet(){
       self.parent.location="/general/approve/personinfo/sumre.do?b_query=link&abkflag=a";   
   }
//-->
</script>
<html:form action="/general/approve/personinfo/setre">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
 <tr><td width="100%" nowrap>
	<table width="100%" align="center" border="0" cellspacing="0"  cellpadding="0" class="ListTableF">
	<%int i = 0;
			ApprovePersonForm ap = (ApprovePersonForm) session
					.getAttribute("approvePersonForm");
			ArrayList codeidlists = ap.getCodeidlist();
			HashMap reqhm = (HashMap) ap.getFormHM().get("requestPamaHM");
			String setid = (String) reqhm.get("setid");
			%>
			<TR>
			<logic:notEqual value="0" name="approvePersonForm" property="state">	
			<td height='50'align='center' class="TableRow" nowrap>
			<bean:message key="column.select"/>		
			</td>
			</logic:notEqual>
			<td height='50'algin='center' class="TableRow" nowrap>
			<bean:message key="column.sys.status"/>
			</td>
			<!-- 
			<td height='50'align='center' class="RecordRow" nowrap><bean:message key="approve.modusername"/>
			</td>
			<td height='50'align='center' class="RecordRow" nowrap><bean:message key="approve.modtime"/>
			</td>
			-->
			<logic:equal value="b" name="approvePersonForm" property="abkflag"> 
			<%if (!setid.equalsIgnoreCase("b01")) {

			%>
			<td align="center" class="TableRow" nowrap>
			<bean:message key="column.sys.org"/>
          	</td>
			<%}

			%>
		</logic:equal>
		<logic:equal value="k" name="approvePersonForm" property="abkflag"> 
			<td height='50'align='center' class="TableRow" nowrap><bean:message key="column.sys.org"/>
			</td>
			<td height='50'align='center' class="TableRow" nowrap><bean:message key="lable.hiremanage.dept_id"/>
			</td>
			<%if (!setid.equalsIgnoreCase("k01")) {

			%>
			
			
			<td height='50'align='center' class="TableRow" nowrap><bean:message key="column.sys.pos"/>
			</td>
			<%}

			%>
		</logic:equal>	
			<logic:equal value="a" name="approvePersonForm" property="abkflag"> 
			<%if (!setid.equalsIgnoreCase("a01")) {

			%>
			<td height='50'align='center' class="TableRow" nowrap><bean:message key="column.sys.org"/>
			</td>
			<td height='50'align='center' class="TableRow" nowrap><bean:message key="lable.hiremanage.dept_id"/>
			</td>
			<td height='50'align='center' class="TableRow" nowrap><bean:message key="column.sys.pos"/>
			</td>
			<td height='50'align='center' class="TableRow" nowrap><bean:message key="hire.employActualize.name"/>
			</td>
			<%}

			%>
		</logic:equal>	
			<logic:iterate id="infotitle" name="approvePersonForm" property="itemnamelist">
			<td height='50'align='center' class="TableRow" nowrap>
			<bean:write  name="infotitle" />
			</td>
			</logic:iterate>		
			</TR>
		<hrms:paginationdb id="element" name="approvePersonForm" sql_str="approvePersonForm.sql" table="" where_str="approvePersonForm.where" columns="approvePersonForm.column" order_by="" pagerows="10" page_id="pagination" allmemo="1" indexes="indexes">
		<tr onClick="javascript:tr_onclick(this,'')">
		<%i = 0;%>
		<logic:notEqual value="0" name="approvePersonForm" property="state">
		
		<td align="center" class="RecordRow" nowrap>
							<hrms:checkmultibox name="approvePersonForm" property="pagination.select" value="true" indexes="indexes" />
						</td>
		</logic:notEqual>
		<td  class="RecordRow" nowrap>
		<logic:equal value="0" name="approvePersonForm" property="state">
		<font color="#993300"><bean:message key="approve.approve.d"/></font>&nbsp;
		</logic:equal>
		<logic:equal value="1" name="approvePersonForm" property="state">
		<font color="#00CC33">
		<bean:message key="button.appeal"/></font>
		&nbsp;
		</logic:equal>
		<logic:equal value="2" name="approvePersonForm" property="state">
		<font color="#FF0000">
		<bean:message key="button.reject"/></font>
		
		&nbsp;
		</logic:equal>
		
		<logic:equal value="3" name="approvePersonForm" property="state">
		<font color="#CC0099"><bean:message key="button.approve"/></font>&nbsp;
		</logic:equal>
		
		<logic:equal value="4" name="approvePersonForm" property="state">
		<font color="#CC6600">申请</font>&nbsp;
		</logic:equal>
		<logic:equal value="5" name="approvePersonForm" property="state">
		可修改&nbsp;
		</logic:equal>
		<logic:equal value="6" name="approvePersonForm" property="state">
		<font color="#666600">不同意</font>&nbsp;
		</logic:equal>
		</td>
		<!--  修改人，修改时间 另修修改
		com.hjsj.hrms.businessobject.general.approve.personinfo.ApproveSQLStr类中
		public static String[] getRetStr（）方法
		<td  class="RecordRow" nowrap>
		<bean:write name="element" property="modusername"/>
		</td>
		<td  class="RecordRow" nowrap>
		<bean:write name="element" property="modtime"/>
		</td>
		-->
		<logic:equal value="k" name="approvePersonForm" property="abkflag"> 
			
			<bean:define id ="item" name="element" property="e01a1"/>		
			<hrms:usrinfo itemid="${item}" abkflag="k" pdbflag="<%=setid%>"></hrms:usrinfo>
			
		</logic:equal>
		<logic:equal value="a" name="approvePersonForm" property="abkflag"> 
			<%if (!setid.equalsIgnoreCase("a01")) {

				%>
			<bean:define id ="item" name="element" property="a0100"/>		
			<hrms:usrinfo itemid="${item}" abkflag="a" pdbflag="<%=ap.getPdbflag()%>"></hrms:usrinfo>
			<%}

			%>
		</logic:equal>
		<logic:equal value="b" name="approvePersonForm" property="abkflag"> 
			<%if (!setid.equalsIgnoreCase("b01")) {

			%>
			<td  class="RecordRow" nowrap>
			<bean:define id ="item" name="element" property="b0110"/>		
			<hrms:codetoname codeid="UN" name="element" codevalue="${item}" codeitem="codeitem" scope="page"/>  	      
          		 <bean:write name="codeitem" property="codename" />&nbsp;
          	</td>
			<%}

			%>
		</logic:equal>
		<logic:iterate id="info" name="approvePersonForm" property="itemidlist">
		<%
            LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
               	                           
        %>
          <logic:equal value="M" name="info" property="itemtype">  
           <%
              FieldItem item=(FieldItem)pageContext.getAttribute("info");
              String tx=(String)abean.get(item.getItemid());
           %> 
              <hrms:showitemmemo showtext="showtext" itemtype="M" setname="" tiptext="tiptext" text="<%=tx%>"></hrms:showitemmemo>             
                <td class="RecordRow" ${tiptext} nowrap> 
                    ${showtext}&nbsp;
                <%i++;%>
		       </td>  
          </logic:equal>
          <logic:notEqual value="M" name="info" property="itemtype">
             <td  class="RecordRow" nowrap>
		     <bean:define  id = "tempinfo" name="element" property="${info.itemid}" ></bean:define>
			<hrms:codetoname codeid="<%=(String)codeidlists.get(i)%>" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
          		 <bean:write name="codeitem" property="codename" /> 
          		 
          		     
          		   <logic:equal value="a0101" name="info" property="itemid">		 
          		 	<logic:equal value="a0101" name="info" property="itemid">
          		 		<bean:define id ="a00" name="element" property="a0100"/>
          		 		<a href="###" onclick='openpage("${a00}")'>
          		 			<bean:write  name="element" property="${info.itemid}"/>
          				 </a>
          		 	</logic:equal>
          		 	<logic:equal value="A0101" name="info" property="itemid">
          		 		<bean:define id ="a00" name="element" property="a0100"/>
          		 		<a href="###" onclick='openpage("${a00}")'>
          		 			<bean:write  name="element" property="${info.itemid}"/>
          				 </a>
          		 	</logic:equal>
          		 	<logic:notEqual value="a0101" name="info" property="itemid">
          		 		<logic:notEqual value="A0101" name="info" property="itemid">
          		 			<bean:write  name="element" property="${info.itemid}" filter="false"/>
          		 		</logic:notEqual>
          			</logic:notEqual>
          		   </logic:equal>	
          		
		   		 <%i++;%>
		     </td>
          </logic:notEqual> 
				     
		</logic:iterate>			
		</tr>
		</hrms:paginationdb>
	</table>
	</td></tr>
	<tr><td>
	<table width="100%" align="left">
		<table width="100%"  class="RecordRowP">
			<tr>
				<td width="400" valign="bottom" align="left" nowrap>
				<p align="left">
				<logic:notEqual value="0" name="approvePersonForm" property="state">
				全选<input type="checkbox" name="sfull" onclick="full();">
				</logic:notEqual>
					<bean:message key="label.page.serial" />
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum" />
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row" />
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page" />
				</td>
				<td width="400">
					<hrms:paginationdblink name="approvePersonForm" property="pagination" nameId="browseRegisterForm" scope="page">
					</hrms:paginationdblink>
				</td>
				<td align="left" nowrap>&nbsp;</td>
			</tr>
		</table>
		<table width="100%">
			<tr>
				<td align="left" colspan="2" width="100%">	
			<logic:notEqual value="0" name="approvePersonForm" property="state">
			<logic:equal value="1" name="approvePersonForm" property="state">
			<hrms:priv func_id="03082,260632">
				<input type="button" name="dfss" value='<bean:message key="approve.personinfo.oks" />' class="mybutton" onclick="go_update();">&nbsp;
			</hrms:priv>
			<hrms:priv func_id="03081,260631">
				<input type="button" name="ddd" value='<bean:message key="button.reject" />' class="mybutton" onclick="go_reject();">	
			</hrms:priv>	
			<hrms:priv func_id="03083,260633">
				 &nbsp;<button name="abc" class="mybutton" onclick="go_aupdate();">整体批准</button>
			</hrms:priv>
			<hrms:priv func_id="03084,260634">
				&nbsp;<button name="abc" class="mybutton" onclick="go_areject();">整体驳回</button>
			</hrms:priv>
			</logic:equal>
			<logic:equal value="2" name="approvePersonForm" property="state">
				<hrms:priv func_id="03082,260632">
				<input type="button" name="dfss" value='<bean:message key="approve.personinfo.oks" />' class="mybutton" onclick="go_update();">&nbsp;
				</hrms:priv>
				<hrms:priv func_id="03083,260633">
				 &nbsp;<button name="abc" class="mybutton" onclick="go_aupdate();">整体批准</button> 
				 </hrms:priv>
			</logic:equal>	
			<logic:equal value="3" name="approvePersonForm" property="state">
			<hrms:priv func_id="03081,260631">
				<input type="button" name="ddd" value='<bean:message key="button.reject" />' class="mybutton" onclick="go_reject();">		
			</hrms:priv>
			<hrms:priv func_id="03084,260634">	
			 &nbsp;<button name="abc" class="mybutton" onclick="go_areject();">整体驳回</button> 
			 </hrms:priv>
			</logic:equal>			
			<logic:equal value="4" name="approvePersonForm" property="state">
		           <logic:equal value="a"  name="approvePersonForm" property="ff">		
				<input type="button" name="ddd" value='可修改' class="mybutton" onclick="go_aok();">	&nbsp;
				<input type="button" name="ddd" value='不同意' class="mybutton" onclick="go_aundo();">		
			   </logic:equal>  
		           <logic:notEqual value="a"  name="approvePersonForm" property="ff">		
				<input type="button" name="ddd" value='可修改' class="mybutton" onclick="go_ok();">	&nbsp;
				<input type="button" name="ddd" value='不同意' class="mybutton" onclick="go_undo();">		
			   </logic:notEqual>   
			</logic:equal>
			
			<logic:equal value="5" name="approvePersonForm" property="state">
				<input type="button" name="ddd" value='可修改' class="mybutton" onclick="go_ok();">	&nbsp;
				<input type="button" name="ddd" value='不同意' class="mybutton" onclick="go_undo();">		
					
			</logic:equal>
			
			<logic:equal value="6" name="approvePersonForm" property="state">
				<input type="button" name="ddd" value='可修改' class="mybutton" onclick="go_ok();">	&nbsp;
				<input type="button" name="ddd" value='不同意' class="mybutton" onclick="go_undo();">		
					
			</logic:equal>
			
			</logic:notEqual>
				&nbsp;
				<logic:equal value="a"  name="approvePersonForm" property="ff">
					<input type="button" name="dfs" value='<bean:message key="button.return"/>' class="mybutton" onclick="go_return();">
				</logic:equal>
				<logic:equal value="b"  name="approvePersonForm" property="ff">
					<input type="button" name="dfs" value='<bean:message key="button.return"/>' class="mybutton" onclick="go_returnSet();">
				</logic:equal>
			</td>
			</tr>			
		</table>
</td></tr>
</table>
</html:form>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>  




