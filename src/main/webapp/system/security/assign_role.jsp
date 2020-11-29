<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.sys.AccountForm"%>
<%
	int i=0;
	AccountForm accountForm=(AccountForm)session.getAttribute("accountForm");
	int maxpage=accountForm.getMaxpage();
	int current=accountForm.getRoleListForm().getPagination().getCurrent();
	maxpage=maxpage<current?current:maxpage;
	accountForm.setMaxpage(maxpage);
	String ret_ctrl =accountForm.getRet_ctrl();
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
.fixedDiv3 
{ 
	overflow:auto; 	
	width:540px; 
	align:center;
	BORDER-BOTTOM: #94B6E6 1px solid !important; 
    BORDER-LEFT: #94B6E6 1px solid !important; 
    BORDER-RIGHT: #94B6E6 1px solid !important; 
    BORDER-TOP: #94B6E6 0px solid !important; 
}
.fixedDiv2 
{ 
	overflow:auto; 
	height:450px;
	width:540px;
	BORDER-BOTTOM: #94B6E6 1px solid !important; 
    BORDER-LEFT: #94B6E6 1px solid !important; 
    BORDER-RIGHT: #94B6E6 1px solid !important; 
    BORDER-TOP: #94B6E6 1px solid !important; 
    margin:0 auto; 
}
.TableRow1 {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1px solid ; 
	BORDER-LEFT: 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1px solid; 
	BORDER-TOP: 0pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
.TableRow2 {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1px solid; 
	BORDER-LEFT: #C4D8EE 1px solid;  
	BORDER-RIGHT: 0pt solid;  
	BORDER-TOP: 0pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
.RecordRow1 {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1px solid; 
	BORDER-LEFT: 0pt; 
	BORDER-RIGHT: #C4D8EE 1px solid; 
	BORDER-TOP: #C4D8EE 1px solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22px;
}
.RecordRow2 {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1px solid; 
	BORDER-LEFT: #C4D8EE 1px solid; 
	BORDER-RIGHT: 0pt; 
	BORDER-TOP: #C4D8EE 1px solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22px;
}
.ListTable3{
/*
	BORDER-BOTTOM:1pt solid !important; 
	BORDER-LEFT:none !important;
	BORDER-TOP:none !important;
	border-right:1pt solid !important;
	border-collapse:separate !important;
	*/
}
.ListTable3 td{
	/*BORDER-LEFT:none !important;
	BORDER-BOTTOM:1pt solid !important; 
	BORDER-TOP:none !important;
	BORDER-right:1pt solid !important;
	*/
}
</style>
<hrms:themes></hrms:themes>
<script language="javascript">
	function saveAssignRole()
	{
	    /**如果这样做的话，有的机器不提交b_o_save参数为空*/
        accountForm.action="/system/security/assign_role.do?b_o_save=link";
		accountForm.submit();
		var i=0,j=0;
		/*延时*/
		//alert("hello ");
		for(i=0;i<10000;i++)
		{
			j++;
		}
	}	
</script>
<base target="_self"/>
<html:form action="/system/security/assign_role">
<logic:equal name="accountForm" property="ret_ctrl" value="0">	 	 
                 <logic:equal name="accountForm" property="status" value="1">
                 	<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" style="margin-top:7px;">
                 </logic:equal>
                 <logic:notEqual name="accountForm" property="status" value="1">
                 		<div class="fixedDiv2"> 
						<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF">
                 </logic:notEqual>
</logic:equal>
<logic:notEqual name="accountForm" property="ret_ctrl" value="0">	 
	<!-- 用户管理，关联角色，勾选需要关联的角色时，出现双滚动条 jingq upd 2014.10.24  -->
	<div class="fixedDiv2" style="margin-top:5px;overflow: auto;height:400px;"> 
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable3">
</logic:notEqual>
<thead>
           
            <logic:equal name="accountForm" property="ret_ctrl" value="0">	 	 
                 <logic:equal name="accountForm" property="status" value="1">
                 <tr>
                 	<td align="center" class="TableRow" nowrap>
                 </logic:equal>
                 <logic:notEqual name="accountForm" property="status" value="1">
                 <tr class="fixedHeaderTr">
                 		<td align="center" class="TableRow1" nowrap>
                 </logic:notEqual>
			</logic:equal>
			<logic:notEqual name="accountForm" property="ret_ctrl" value="0">	
			<tr class="fixedHeaderTr"> 
				<td align="center" class="TableRow" nowrap style="border-left:none;">
			</logic:notEqual>
            &nbsp;<input type="checkbox" name="selbox" onclick="batch_select(this,'roleListForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;	    
            </td>           
            <td align="center" class="TableRow" nowrap style="">
		<bean:message key="column.name"/>&nbsp;
	    </td>
            <logic:equal name="accountForm" property="ret_ctrl" value="0">	 	 
                 <logic:equal name="accountForm" property="status" value="1">
                 	<td align="center" class="TableRow" nowrap>
                 </logic:equal>
                 <logic:notEqual name="accountForm" property="status" value="1">
                 		<td align="center" class="TableRow2" nowrap>
                 </logic:notEqual>
			</logic:equal>
			<logic:notEqual name="accountForm" property="ret_ctrl" value="0">	 
				<td align="center" class="TableRow" nowrap style="">
			</logic:notEqual>
            
		<bean:message key="column.desc"/>
	    </td>
   		        	        	        
           </tr>
   	  </thead>
   	  		<%if(!"1".equals(ret_ctrl)){ %>
          <hrms:extenditerate id="element" name="accountForm" property="roleListForm.list" indexes="indexes"  pagination="roleListForm.pagination" pageCount="${accountForm.pagerows}" scope="session">
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
            <logic:equal name="accountForm" property="ret_ctrl" value="0">	 	 
                 <logic:equal name="accountForm" property="status" value="1">
                 	<td align="center" class="RecordRow" nowrap>
                 </logic:equal>
                 <logic:notEqual name="accountForm" property="status" value="1">
                 		<td align="center" class="RecordRow1" nowrap>
                 </logic:notEqual>
			</logic:equal>
			<logic:notEqual name="accountForm" property="ret_ctrl" value="0">	 
				<td align="center" class="RecordRow" nowrap>
			</logic:notEqual>
                 <logic:equal name="element" property="string(valid)" value="1">
     		   &nbsp;<hrms:checkmultibox name="accountForm" property="roleListForm.select" value="false" indexes="indexes"/>&nbsp;
            	 </logic:equal>  
                 <logic:equal name="element" property="string(valid)" value="0">
     		   &nbsp;<hrms:checkmultibox name="accountForm" property="roleListForm.select" value="true" indexes="indexes"/>&nbsp;
            	 </logic:equal>
            	 <!--其他人授予的本人没有的角色，不可编辑 guodd 2016-07-01 -->
            	 <logic:equal name="element" property="string(valid)" value="2">
     		   &nbsp;<input type="checkbox" checked=true disabled/>&nbsp;
     		   <input type="hidden" name="roleListForm.select[${indexes}]" />
            	 </logic:equal>             	    		   
	    </td>            
            <td align="left" class="RecordRow" nowrap>
                  <bean:write name="element" property="string(role_name)" filter="true"/>
	    </td>
         	<logic:equal name="accountForm" property="ret_ctrl" value="0">	 	 
                 <logic:equal name="accountForm" property="status" value="1">
                 	<td align="left" class="RecordRow" style="word-break:break-all;">
                 </logic:equal>
                 <logic:notEqual name="accountForm" property="status" value="1">
                 		<td align="left" class="RecordRow2" style="word-break:break-all;">
                 </logic:notEqual>
			</logic:equal>
			<logic:notEqual name="accountForm" property="ret_ctrl" value="0">	 
				<td align="left" class="RecordRow2" style="word-break:break-all;">
			</logic:notEqual>
            
                    <bean:write  name="element" property="string(role_desc)" filter="false"/>
            </td>
          </tr>
        </hrms:extenditerate>
         <%}else{%>
          <hrms:extenditerate id="element" name="accountForm" property="roleListForm.list" indexes="indexes"  pagination="roleListForm.pagination" pageCount="20" scope="session">
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
            <logic:equal name="accountForm" property="ret_ctrl" value="0">	 	 
                 <logic:equal name="accountForm" property="status" value="1">
                 	<td align="center" class="RecordRow" nowrap>
                 </logic:equal>
                 <logic:notEqual name="accountForm" property="status" value="1">
                 		<td align="center" class="RecordRow1" nowrap>
                 </logic:notEqual>
			</logic:equal>
			<logic:notEqual name="accountForm" property="ret_ctrl" value="0">	 
				<td align="center" class="RecordRow" nowrap style="border-left:none;">
			</logic:notEqual>
                 <logic:equal name="element" property="string(valid)" value="1">
     		   &nbsp;<hrms:checkmultibox name="accountForm" property="roleListForm.select" value="false" indexes="indexes"/>&nbsp;
            	 </logic:equal>  
                 <logic:equal name="element" property="string(valid)" value="0">
     		   &nbsp;<hrms:checkmultibox name="accountForm" property="roleListForm.select" value="true" indexes="indexes"/>&nbsp;
            	 </logic:equal>
            	 <!--其他人授予的本人没有的角色，不可编辑 guodd 2016-07-01 -->
            	 <logic:equal name="element" property="string(valid)" value="2">
     		   &nbsp;<input type="checkbox" checked=true disabled/>&nbsp;
     		   <input type="hidden" name="roleListForm.select[${indexes}]" />
            	 </logic:equal>              	    		   
	    </td>            
            <td align="left" class="RecordRow" nowrap>
                   &nbsp;<bean:write name="element" property="string(role_name)" filter="true"/>
	    </td>
         	<logic:equal name="accountForm" property="ret_ctrl" value="0">	 	 
                 <logic:equal name="accountForm" property="status" value="1">
                 	<td align="left" class="RecordRow" style="word-break:break-all;">
                 </logic:equal>
                 <logic:notEqual name="accountForm" property="status" value="1">
                 		<td align="left" class="RecordRow2" style="word-break:break-all;">
                 </logic:notEqual>
			</logic:equal>
			<logic:notEqual name="accountForm" property="ret_ctrl" value="0">	 
				<td align="left" class="RecordRow2" style="word-break:break-all;">
			</logic:notEqual>
            
                    &nbsp;<bean:write  name="element" property="string(role_desc)" filter="false"/>
            </td>
          </tr>
        </hrms:extenditerate>
         <%} %>
         
          
        
</table>
<logic:equal name="accountForm" property="ret_ctrl" value="0">
	<logic:equal name="accountForm" property="status" value="1">
                 	<table  width="70%" align="center" class="RecordRowP">
    </logic:equal>
	<logic:notEqual name="accountForm" property="status" value="1">
                 	</div>
					<div class="fixedDiv3"> 
					<table  width="100%" align="center" >
	</logic:notEqual>
</logic:equal>
<logic:notEqual name="accountForm" property="ret_ctrl" value="0">
					</div>
					<div class="fixedDiv3"> 
					<table  width="100%" align="center" >
</logic:notEqual>

<logic:equal name="accountForm" property="ret_ctrl" value="1">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="accountForm" property="roleListForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="accountForm" property="roleListForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="accountForm" property="roleListForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
			<td  align="right" nowrap class="tdFontcolor">
		          <p align="right"> <hrms:paginationlink name="accountForm" property="roleListForm.pagination"
				nameId="roleListForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</logic:equal>


<logic:notEqual name="accountForm" property="ret_ctrl" value="1">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <hrms:paginationtag name="accountForm"
								pagerows="${accountForm.pagerows}" property="roleListForm.pagination"
								scope="session" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="accountForm" property="roleListForm.pagination"
				nameId="roleListForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</logic:notEqual>
</table>
<logic:equal name="accountForm" property="ret_ctrl" value="0">	 	 
                 <logic:equal name="accountForm" property="status" value="1">
                 </logic:equal>
                 <logic:notEqual name="accountForm" property="status" value="1">
	                 	</div>
				</logic:notEqual>
</logic:equal>
<logic:notEqual name="accountForm" property="ret_ctrl" value="0">	 
	</div>
</logic:notEqual>



<table align="center">
          <tr>
            <td align="center" height="35px;">
	 	 <!--根据登录的用户类型,返回到不同的页面 -->
      <logic:equal name="accountForm" property="ret_ctrl" value="0">	 	 
                 <logic:equal name="accountForm" property="status" value="1">
         	   <hrms:submit styleClass="mybutton" property="b_save">
            	   	<bean:message key="button.save"/>
	 	   </hrms:submit>                 
         	   <hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	   </hrms:submit>
            	 </logic:equal> 
                 <logic:equal name="accountForm" property="status" value="0">
         	   <hrms:submit styleClass="mybutton" property="b_save_user">
            		<bean:message key="button.save"/>
	 	   </hrms:submit>                 
         	   <hrms:submit styleClass="mybutton" property="br_return_user">
            		<bean:message key="button.return"/>
	 	   </hrms:submit>
            	 </logic:equal>    
                 <logic:equal name="accountForm" property="status" value="2">
         	   <hrms:submit styleClass="mybutton" property="b_save_org">
            		<bean:message key="button.save"/>
	 	   </hrms:submit>                 
         	   <hrms:submit styleClass="mybutton" property="br_return_org">
            		<bean:message key="button.return"/>
	 	   </hrms:submit>
            	 </logic:equal>   
       </logic:equal> 
      <logic:equal name="accountForm" property="ret_ctrl" value="1">	
           <html:submit styleClass="mybutton" property="b_o_save">
            	   	<bean:message key="button.save"/>
  	       </html:submit> 
  	      
           <html:button styleClass="mybutton" property="b_o_close" onclick="top.close();">
            	   	<bean:message key="button.close"/>
  	       </html:button>   	       
  	      
      </logic:equal>                     	           	          	   	 	  
            </td>
          </tr>          
</table>

<!-- 下面的排版浏览器不兼容，线错乱，改用上面的排版 
<logic:equal name="accountForm" property="ret_ctrl" value="0">	 	 
                 <logic:equal name="accountForm" property="status" value="1">
                 	<table  width="70%" align="center" class="RecordRowP">
                 </logic:equal>
                 <logic:notEqual name="accountForm" property="status" value="1">
                 	</div>
					<div class="fixedDiv3"> 
					<table  width="100%" align="center" class="RecordRowP">
				</logic:notEqual>
</logic:equal>
<logic:notEqual name="accountForm" property="ret_ctrl" value="0">	 
	</div>
	<div class="fixedDiv3"> 
	<table  width="100%" align="center" class="RecordRowP">
</logic:notEqual>
<logic:equal name="accountForm" property="ret_ctrl" value="1">
<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="accountForm" property="roleListForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="accountForm" property="roleListForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="accountForm" property="roleListForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
			<td  align="right" nowrap class="tdFontcolor">
		          <p align="right"> <hrms:paginationlink name="accountForm" property="roleListForm.pagination"
				nameId="roleListForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</logic:equal>
<logic:notEqual name="accountForm" property="ret_ctrl" value="1">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <hrms:paginationtag name="accountForm"
								pagerows="${accountForm.pagerows}" property="roleListForm.pagination"
								scope="session" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="accountForm" property="roleListForm.pagination"
				nameId="roleListForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</logic:notEqual>
</table>
<table align="center">
          <tr>
            <td align="center" height="35px;">
	 	 <!--根据登录的用户类型,返回到不同的页面 -!->
      <logic:equal name="accountForm" property="ret_ctrl" value="0">	 	 
                 <logic:equal name="accountForm" property="status" value="1">
         	   <hrms:submit styleClass="mybutton" property="b_save">
            	   	<bean:message key="button.save"/>
	 	   </hrms:submit>                 
         	   <hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	   </hrms:submit>
            	 </logic:equal> 
                 <logic:equal name="accountForm" property="status" value="0">
         	   <hrms:submit styleClass="mybutton" property="b_save_user">
            		<bean:message key="button.save"/>
	 	   </hrms:submit>                 
         	   <hrms:submit styleClass="mybutton" property="br_return_user">
            		<bean:message key="button.return"/>
	 	   </hrms:submit>
            	 </logic:equal>    
                 <logic:equal name="accountForm" property="status" value="2">
         	   <hrms:submit styleClass="mybutton" property="b_save_org">
            		<bean:message key="button.save"/>
	 	   </hrms:submit>                 
         	   <hrms:submit styleClass="mybutton" property="br_return_org">
            		<bean:message key="button.return"/>
	 	   </hrms:submit>
            	 </logic:equal>   
       </logic:equal> 
      <logic:equal name="accountForm" property="ret_ctrl" value="1">	
           <html:submit styleClass="mybutton" property="b_o_save">
            	   	<bean:message key="button.save"/>
  	       </html:submit> 
  	      
           <html:button styleClass="mybutton" property="b_o_close" onclick="top.close();">
            	   	<bean:message key="button.close"/>
  	       </html:button>   	       
  	      
      </logic:equal>                     	           	          	   	 	  
            </td>
          </tr>          
</table>
<logic:equal name="accountForm" property="ret_ctrl" value="0">	 	 
                 <logic:equal name="accountForm" property="status" value="1">
                 </logic:equal>
                 <logic:notEqual name="accountForm" property="status" value="1">
	                 	</div>
				</logic:notEqual>
</logic:equal>
<logic:notEqual name="accountForm" property="ret_ctrl" value="0">	 
	</div>
</logic:notEqual>

-->
</html:form>
<script language="javascript">
  
</script>