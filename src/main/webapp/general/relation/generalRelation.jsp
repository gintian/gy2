<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*, 
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 com.hjsj.hrms.actionform.general.relation.GenRelationForm" %>

<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
<script language="javascript" src="/general/relation/gen_relation.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<style>
.fixedtab 
{ 
	width:100%;
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 	
}
</style>
<hrms:themes></hrms:themes>
<%
	GenRelationForm myForm=(GenRelationForm)session.getAttribute("genRelationForm");	
	HashMap joinedObjs = (HashMap)myForm.getJoinedObjs();
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String operation = request.getParameter("operation");
	String isDelMainBody = (String)myForm.getIsDelMainbody();//郭峰
	if("1".equals(isDelMainBody))
		operation="alertcotent";
	myForm.setIsDelMainbody("0");
%>
<%
        String bosflag= userView.getBosflag();//得到系统的版本号
%>  

<style>
.td_no_t_r_l
{ 
    BORDER-TOP: 0pt;  
    BORDER-RIGHT: 0pt; 
    BORDER-LEFT: 0pt; 
}
.td_no_t_r
{ 
    BORDER-TOP: 0pt;  
    BORDER-RIGHT: 0pt; 
}
.td_no_t
{ 
    BORDER-TOP: 0pt;  
}

</style>
<script>

//window.status=KH_RELATION_INFO3;
var aclientHeight=document.body.clientHeight;
document.body.onbeforeunload=function(){ 
	window.status='';
}
function resizeWindowRefrsh()
{
	//if(aclientHeight*1!=document.body.clientHeight*1)//上下移动分界 刷新页面
		//window.location=window.location;
		aclientHeight=document.body.clientHeight;
		if(aclientHeight-84>0)
		{
			document.getElementById('a_table_div').style.height=aclientHeight-84;
		<%
		  if(bosflag!=null&&!bosflag.equals("hcm")){
		%>
		  document.getElementById('page').style.top=aclientHeight-55;
		<%
		  }else{
	   %>
	      document.getElementById('page').style.top=aclientHeight-47;
	   <%
		  }
		%>	
		}		
}
   function change()
   {
      genRelationForm.action="/general/relation/relationobjectlist.do?b_query=query&objSelected=";
      genRelationForm.submit();
   }
   function goback(){
  	window.parent.location.href="/general/relation/relationmaintence.do?b_query=link&objSelected=";
   }
</script>
<body onResize="resizeWindowRefrsh()" style="overflow:hidden;" >
<html:form action="/general/relation/relationobjectlist"> 
<table width='30%' cellpadding="0" cellspacing="0" style="margin-top:-1px;"><tr>
 <td align="left" nowrap style="padding:0px;">
        <logic:equal name ="genRelationForm" property="actor_type" value="1">
	        <bean:message key="label.query.dbpre"/>&nbsp;
	     	<hrms:optioncollection name="genRelationForm" property="dblist" collection="list" />
	     	     <span>
	             <html:select name="genRelationForm" property="dbpre" size="1" onchange="change();" style="position:relative;top:3px;">
	             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	             </span>
	        </html:select>&nbsp;
	        <bean:message key="label.title.name"/> 
        </logic:equal>
        <logic:notEqual name ="genRelationForm" property="actor_type" value="1">
            <bean:message key="label.username"/>
        </logic:notEqual>
        <html:text name="genRelationForm" property="a0101" size="20" maxlength="30" styleClass="text4"></html:text>
       <span>
        <html:button styleClass="button" property="b_all" onclick="change();" style="position:relative;top:2px;padding:0px;margin:0px;"><bean:message key="button.query"/></html:button>
       </span>  
    <span>
	<button extra="button" menu="p2" allowPushDown="false"  down="false" style="position:relative;top:2px;">审批关系定义</button>
	</span> 
 		 <hrms:menubar menu="p2" id="menubar2" container="" visible="false">
 		 <logic:equal name ="genRelationForm" property="actor_type" value="1">
		 <hrms:menuitem name="m21" label="performance.relation.autocreat.individual" icon="/images/compute.gif" url="autoGetBody('individual')"  enabled="true" function_id="9A5105" />
		 <hrms:menuitem name="m20" label="performance.relation.autocreat.batch" icon="/images/compute.gif" url="autoGetBody('batch')"  enabled="true" function_id="9A5106" />
		 </logic:equal>
		 <%-- 
			<!-- 田野 还原指定审批关系
		 //<hrms:menuitem name="m22" label="指定审批主体" icon="/images/bm5.bmp" url="mainBodySelNew('${genRelationForm.relationid}','${genRelationForm.dbpre}','${genRelationForm.actor_type}','1')" enabled="true" function_id="9A5107" />  
		 -->			 
		 --%>
		 <hrms:menuitem name="m22" label="指定审批主体" icon="/images/bm5.bmp" url="mainBodySel('${genRelationForm.relationid}','${genRelationForm.dbpre}','${genRelationForm.actor_type}')" enabled="true" function_id="9A5107" /> 
		 <hrms:menuitem name="m23" label="清除审批主体" icon="/images/del.gif" url="cleanMainBody();" enabled="true" function_id="9A5108" />	
		 <hrms:menuitem name="m24" label="复制审批主体" icon="/images/edit.gif" url="copyGenMainBody()" enabled="true" function_id="9A5109" /> 
		 <hrms:menuitem name="m25" label="粘贴审批主体" icon="/images/edit.gif" url="pasteGenMainBody()" enabled="true" function_id="9A5110" /> 
		 <hrms:menuitem name="m26" label="信息同步" icon="/images/refresh.gif" url="messagesyn('${genRelationForm.relationid}','${genRelationForm.actor_type}')" enabled="true" function_id="" /> 
 		</hrms:menubar> 
	<span>	
		<input type="button" value="<bean:message key='button.return'/>" name="b_retrun" onclick="goback();" extra="button" style="position:relative;top:2px;" />
		<logic:equal name="genRelationForm" property="isshowbutton" value="true">
		<input type="button" value="关闭" onclick="window.close();" class="mybutton">
		</logic:equal>
	</span>
</td></tr></table>
	<script language='javascript' >
		var theHeight=0;
			theHeight=document.body.clientHeight-82;
			document.write("<div class=\"fixedtab  common_border_color\" id=\"a_table_div\" style='position:absolute;left:5;height:"+theHeight+";width:99%;margin-top:5px;'>");
 	</script> 
  <table id='a_table' width="90%" border="0" cellspacing="0"    align="left" cellpadding="0"  style="border:0px;">
   	  <thead>
 		<tr class="fixedHeaderTr"> 
	         <td align="center" class="TableRow td_no_t_r_l" width="10%" nowrap  >				
				 <input type="checkbox" name="selbox" onclick="batch_select(this,'objectID');" title='<bean:message key="label.query.selectall"/>'>
	         </td>    
	         <logic:equal name ="genRelationForm" property="actor_type" value="1">     
	         <td align="center" class="TableRow  td_no_t_r" width="150" nowrap  >
			   	<bean:message key="b0110.label"/>
		     </td>          
	         <td align="center" class="TableRow td_no_t_r" nowrap  >
				 <%
	         		 FieldItem fielditem = DataDictionary.getFieldItem("E0122");
	         	 %>	         
			 	 <%=fielditem.getItemdesc()%>
		     </td>
		     <td align="center" class="TableRow  td_no_t_r" width="150" nowrap  >
			 	 <bean:message key="e01a1.label"/>
		     </td>
		     </logic:equal>
		     <logic:equal name ="genRelationForm" property="actor_type" value="4">  
		      <td align="center" class="TableRow  td_no_t_r" width="150" nowrap  >
			   	用户组
		     </td>    
		      </logic:equal>
		      <td align="center" class="TableRow  td_no_t" width="150" nowrap  >
			 	 <bean:message key="hire.zp_persondb.username"/><!--xcs 修改 @2013-10-09 使前台和后台名字显示一致  -->
		     </td>
         </tr>
   	  </thead>
   	  <%  int i=0; %>
   	  <hrms:extenditerate id="element" name="genRelationForm" property="genObjectForm.list" indexes="indexes"  pagination="genObjectForm.pagination" pageCount="${genRelationForm.pagerows}" scope="session">
   	  	<bean:define id="oid" name="element" property="object_id" />
   	  	<% 
   	  			  	
		     		LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
		     		String object_id=(String)abean.get("object_id");
		     	 	String canEdit = joinedObjs.get(object_id)!=null?"0":"1";
   	  	
   	  	i++;
   	  	   if(i%2==1){ %>
   	  	   <tr class='trShallow' onClick="javascript:tr_onclick(this,'#F3F5FC')">
   	  	   <% } else { %>	   
   	  	   	<tr class='trDeep'  onClick="javascript:tr_onclick(this,'#E4F2FC')">
   	  	   <% } %>
		
	         <td align="center" class="RecordRow td_no_t_r_l"   width="10%" nowrap onclick='selectRow("${oid}","<%=canEdit %>","<%=i %>")' style="border-left:0px;">
			  	<input type='checkbox' name='objectID' value='${oid}'  />
	         </td>         
             <logic:equal name ="genRelationForm" property="actor_type" value="4">  
               <td align="left" class="RecordRow td_no_t_r"  onclick='selectRow("${oid}","<%=canEdit %>","<%=i %>")' nowrap >
                 <bean:write name="element" property="b0110" filter="true"/>&nbsp;
                  </td>
             </logic:equal>
             <logic:notEqual name ="genRelationForm" property="actor_type" value="4">  
                 <td align="left" class="RecordRow td_no_t_r"  onclick='selectRow("${oid}","<%=canEdit %>","<%=i %>")' nowrap >
                    <hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>
                     <bean:write name="codeitem" property="codename" /> &nbsp;
                 </td>
             </logic:notEqual>   
		      <logic:equal name ="genRelationForm" property="actor_type" value="1">            
	         <td align="left" class="RecordRow td_no_t_r" onclick='selectRow("${oid}","<%=canEdit %>","<%=i %>")' nowrap>
			    <hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page" uplevel="${genRelationForm.uplevel}"/>
			    <bean:write name="codeitem" property="codename" /> &nbsp;
		     </td>   
		     <td align="left" class="RecordRow td_no_t_r" onclick='selectRow("${oid}","<%=canEdit %>","<%=i %>")' nowrap>
			     <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>
                <bean:write name="codeitem" property="codename" />&nbsp;
	         </td>   
	         </logic:equal>      
	         <td align="left" class="RecordRow td_no_t" onclick='selectRow("${oid}","<%=canEdit %>","<%=i %>")' nowrap >
			    
			    <logic:notEqual name="element" property="actor_type" value="">
			   &nbsp; <img src="/images/role_assign.gif" border=0 align="middle">&nbsp;
			    </logic:notEqual>
			    &nbsp; <bean:write name="element" property="a0101" filter="true"/>
		     </td>          
	              	        
         </tr>   	  
   	   </hrms:extenditerate>  	  
	</table>
	</div>
	<%
	    if(bosflag!=null&&!bosflag.equals("hcm")){
	%>
	   <script language='javascript' >
        document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-53)+";width:99%'  >");
        </script>
	<%
	}else{
	%>
	    <script language='javascript' >
        document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-45)+";width:99%'  >");
        </script>
	<%
	}
	%>
	<table width="100%" align="center" class="RecordRowP"  style="margin-top:2px;">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				 <hrms:paginationtag name="genRelationForm" pagerows="${genRelationForm.pagerows}" property="genObjectForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="genRelationForm"
						property="genObjectForm.pagination" nameId="genObjectForm"
						propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>	
	</div>
	<html:hidden name="genRelationForm" property="paramStr"/>
</html:form>
</body>
<script type="text/javascript">
var a_code="${genRelationForm.a_code}";
var table = document.getElementById('a_table');
var objectid = "${genRelationForm.objSelected}";
var operation ="<%=operation%>";
	if(objectid!=null&&objectid.length>0){
	if(operation!=null&&operation=="alertcotent"){
	var selectid = "${genRelationForm.selectid}";
		//if(selectid!=null&&selectid.length>0)
		//selectid = selectid*1;
		
		if(selectid!=null&&selectid>0&&table.rows.length>=selectid){
			table.rows[selectid].cells[1].fireEvent("onclick");
			///改变滚动条暂不考虑。如果非要加上，则再加一个值存储滚动条的值 郭峰
		}else
		parent.ril_body2.location="/general/relation/relationmainbodylist.do?b_queryBody=link&objectid="+objectid;
	}else{
	if(table.rows.length>1)
		{
			table.rows[1].cells[1].fireEvent("onclick");
		}
		else
			parent.ril_body2.location="/general/relation/relationmainbodylist.do?b_queryBody=link&objectid=aaa";
	}
	}else
	{
	if(table.rows.length>1)
		{
			table.rows[1].cells[1].fireEvent("onclick");
		}
	else
		parent.ril_body2.location="/general/relation/relationmainbodylist.do?b_queryBody=link&objectid=aaa";
	}
</script>