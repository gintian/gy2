<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.transaction.sys.warn.ColumnBean" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.frame.utility.AdminCode,com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.struts.valueobject.UserView,java.util.List"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.actionform.sys.warn.ConfigForm"%>
<%
int i = 0;
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
boolean isUseNewPrograme=PubFunc.isUseNewPrograme(userView);
String ver = "";
String returnvalue="73";
String func="0";
//liuy 2014-8-19 修改预警提示详情页面返回按钮主页还是预警提示列表  begin
ConfigForm configForm = (ConfigForm)session.getAttribute("warnConfigForm");
String returnlistvalue = configForm.getReturnvalue();
returnlistvalue=returnlistvalue!=null&&returnlistvalue.length()>0?returnlistvalue:"";
////liuy 2014-8-19 修改预警提示详情页面返回按钮主页还是预警提示列表  end
if(userView!=null)
{
    ver=userView.getBosflag();
    if(ver!=null&&ver.equals("hl4"))
	{
	   returnvalue="74";
	}else if(ver!=null&&(ver.equals("hl")||ver.equals("hcm")))
	{
	   returnvalue="75";
	}
	if(userView.hasTheFunction("26060"))
	   func="1";
}
if(ver!=null&&ver.equals("el"))
   out.println("<link rel=\"stylesheet\" href=\"/css/css6.css\" type=\"text/css\">");
%>
<style id="iframeCss">
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
<script language="javascript" src="/js/validate.js"></script>
<script language="javaScript">

function returnH()
{
	warnConfigForm.action="/templates/index/bi_portal.do?b_query=link";
	warnConfigForm.target="_self";
      warnConfigForm.submit();
} 

	function changedb(){
		var wid = document.warnConfigForm.wid.value;
		var v = document.warnConfigForm.dbPre.value;
		document.warnConfigForm.action="/system/warn/result_manager.do?b_query=link&warn_wid="+wid +"&dbpre="+v;
		document.warnConfigForm.submit();
	}
	
	function isSuccess(outparamters)
	{
		<% if(isUseNewPrograme){  %>
		 var flag=outparamters.getValue("flag");
		 if(flag=="false")
			return;	
 		 var templateid=$F('template');
 		 var warn_id=$F('wid');	
		 var win=window.open("/module/template/templatemain/templatemain.html?b_query=link&approve_flag=1&module_id=1&return_flag=7-"+warn_id+"&tab_id="+templateid,"_self");
		
		<% }else{ %>
		var flag=outparamters.getValue("succeed");
		var tab_id=outparamters.getValue("tabid");
	    var warn_id=$F('wid');		
		if(flag=="false")
			return;	
 
		 var win=window.open("/general/template/edit_form.do?b_query=link&sp_flag=1&ins_id=0&returnflag=5&tabid="+tab_id+"&warn_id="+warn_id,"_self");
	 	<% } %>
	}
	
	function selectTenolate()
	{
	   var templateid=$F('template');

	   var tab=$('tbl_r');
	   var rows=tab.rows.length;	  
	   if(rows<=2)
	   {
	       alert("没有预警人员！");
	       return;
	   }
	     
	   var  thetr;
	   var thechkbox,a0100;  
       var objarr=new Array();	   	   
	   for(var i=2;i<=tab.rows.length-1 ;i++)
	   {
           thetr = tab.rows[i];
           thechkbox=thetr.cells[0].children[0];
       	   if(!thechkbox.checked)
        		continue;	   
           a0100=thetr.cells[1].innerHTML;     
           if(!getBrowseVersion())  //非ie兼容视图下  报错   去掉前后空格 wangb 20190321      
           		a0100=a0100.trim();
           objarr.push(a0100);
	   }	  
       if(objarr.length>0)
       {
   	   		var hashvo=new ParameterSet();
       		hashvo.setValue("tabid",templateid);	
       		hashvo.setValue("ins_id","0");
       		hashvo.setValue("objlist",objarr);
       		<% if(isUseNewPrograme){  %>
       			hashvo.setValue("tab_id",templateid);	
       			hashvo.setValue("infor_type","1");	
       			hashvo.setValue("from_module","yj"); 
       			var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'MB00001004'},hashvo); 
       		<% }else{ %>
       			var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0570010135'},hashvo); 
       		<% } %>        
       }else
       {
          alert("请选择人员！");
       }
	}
function winhref(dbpre,a0100,returnvalue)
{
   if(a0100=="")
      return false;
   <%if(returnvalue.equals("74")){
       out.print("parent.menupnl.toggleCollapse(false);");
   }%>
   var o_obj=document.getElementById('a0100');   
   if(o_obj)
     o_obj.value=a0100;
    warnConfigForm.action="/workbench/info/addselfinfo.do?b_add=add&i9999=I9999&actiontype=update&setname=A01&flag=notself&userbase="+dbpre+"&returnvalue="+returnvalue+"";
     <%if(ver!=null&&ver.equals("el")){
       out.println("warnConfigForm.target=\"_self\";");
   }%>
    warnConfigForm.submit();        
}

function allSelectResult()
{
    var tab=$('tbl_r');
	var rows=tab.rows.length;	  
	if(rows<=2)
	{
	       alert("没有预警人员！");
	       return;
	}
   var hashvo=new ParameterSet();
   var sql="${warnConfigForm.encodeSql}";
   hashvo.setValue("sql",sql);	   
   var request=new Request({asynchronous:false,onSuccess:reResult,functionId:'1010020315'},hashvo); 
}
function reResult(outparamters)
{
       var objarr=new Array();
       var list=outparamters.getValue("list");	
       var templateid=$F('template');
	   for(var i=0;i<list.length ;i++)
	   {
           a0100=list[i];   
           objarr.push(a0100);
	   }	  
       if(objarr.length>0)
       {
   	   		var hashvo=new ParameterSet();
       		hashvo.setValue("tabid",templateid);	
       		hashvo.setValue("ins_id","0");
       		hashvo.setValue("objlist",objarr);
       		<% if(isUseNewPrograme){  %>
       			hashvo.setValue("tab_id",templateid);	
       			hashvo.setValue("infor_type","1");	
       			hashvo.setValue("from_module","yj"); 
       			var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'MB00001004'},hashvo); 
       		<% }else{ %>
       			var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0570010135'},hashvo); 
       		<% } %>        
       }else
       {
          alert("请选择人员！");
       }
}
function refreshWarn()
{
   if(confirm("确定需要重新进行预警处理?"))
   {
      var waitInfo=eval("wait");	   
	  waitInfo.style.display="block";	 
      warnConfigForm.action="/system/warn/result_manager.do?b_refresh=link";
      warnConfigForm.submit();
   }
}
function MusterInitData()
{
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
}
var code="";
var kind="";
var orgtype="";
var parentid="";
var codesetid="";	
function showOrgContext(codeitemid)
{
   var hashvo=new ParameterSet();
   hashvo.setValue("codeitemid",codeitemid);	
   var request=new Request({asynchronous:false,onSuccess:getContext,functionId:'0401004003'},hashvo);
}
function getContext(outparamters)
{
    code=outparamters.getValue("codeitemid");
	kind=outparamters.getValue("kind");
	orgtype=outparamters.getValue("orgtype");
	parentid=outparamters.getValue("parentid");
	codesetid=outparamters.getValue("codesetid");
}
function editorgUM(returnvalue)
{
   returnvalue=75;
   warnConfigForm.action="/workbench/orginfo/editorginfodata.do?b_search=link&code="+code+"&kind="+kind+"&orgtype="+orgtype+"&parentid="+parentid+"&returnvalue="+returnvalue+"&scantype=scan&return_codeid=&edittype=update";
   warnConfigForm.submit();
}
function editorgUK(returnvalue,UM_code)
{
  returnvalue=75;
  warnConfigForm.action="/workbench/dutyinfo/editorginfodata.do?b_search=link&edit_flag=edit&code="+code+"&kind="+kind+"&orgtype="+orgtype+"&parentid="+parentid+"&returnvalue="+returnvalue+"&return_codeid="+UM_code;
  warnConfigForm.submit();
}
function exportExcel(){
<bean:define id="cmsg" name="warnConfigForm" property="dynaBean.cmsg"></bean:define>
               
	var hashvo=new ParameterSet();
    hashvo.setValue("title",getEncodeStr('<%=((String)cmsg).replaceAll("\\n\\r","").replaceAll("\\n","").replaceAll("\\r","") %>'));
    hashvo.setValue("sql","${warnConfigForm.encodeSql}");
    hashvo.setValue("order","${warnConfigForm.order}");
    hashvo.setValue("columns","${warnConfigForm.columns}");
    hashvo.setValue("level","${warnConfigForm.uplevel}");
    hashvo.setValue("warntype","${warnConfigForm.warntype}");
    hashvo.setValue("fieldItemclumn","${warnConfigForm.fieldItemclumn}");
    var request=new Request({asynchronous:false,onSuccess:outFile,functionId:'1010020300'},hashvo);
}
function outFile(outparamters){
	if(outparamters){
		var name=outparamters.getValue("filename");
		window.location.target="_blank";
		//xus 20/4/28 vfs改造
		window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+name;
	}
}
</script>
<html:form action="/system/warn/result_manager">
<input type="hidden" name="a0100" id="a0100">
	<br>
<table width="85%" border="0" cellspacing="0" align="center" cellpadding="0">
  <tr> 
    <td>
       <table id="tbl_r" width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="RecordRow">
			<tr height="25">
			<input type="hidden" id="wid" value="<bean:write name="warnConfigForm" property="wid" filter="true" />">
			<td class="" nowrap colspan="10">
			    <logic:equal name="warnConfigForm" property="warntype" value="0">
				<%
					ConfigForm warnConfigForm = (ConfigForm)session.getAttribute("warnConfigForm");
					List dblist = (List)warnConfigForm.getDblist();
					if(dblist.size()>1){
				 %>
				人员库:
                 <html:select name="warnConfigForm" property="dbPre" size="1" onchange="changedb()">
                  <option value="ALL">全部</option>
                  <html:optionsCollection property="dblist" value="dataValue" label="dataName"/>
                </html:select> 
                <%} %>
                </logic:equal>
				&nbsp;预警提示:
				<bean:define id="cmsg" name="warnConfigForm" property="dynaBean.cmsg"></bean:define>
				<%=((String)cmsg).replaceAll("\\n\\r","").replaceAll("\\n","").replaceAll("\\r","") %>
			</td>			
		</tr>		
		<logic:equal name="warnConfigForm" property="warntype" value="0">
		    <tr>
		   <logic:notEmpty name="warnConfigForm" property="tenplatelist">		
		     <td align="center" class="TableRow" nowrap>
		      <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
            </td>  
		    </logic:notEmpty>  
			<td align="center" class="TableRow" style="display:none">
			</td>			              
			<td align="center" class="TableRow" nowrap>
				<bean:message key="column.sys.org" />
				&nbsp;
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="label.title.dept" />
				&nbsp;
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="column.sys.pos" />
				&nbsp;
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="label.title.name" />
				&nbsp;
			</td>
			
			<logic:iterate id="ColumnBean" name="warnConfigForm"  property="columnList" indexId="index">
	          <td align="center" class="TableRow" nowrap>
	             <bean:write  name="ColumnBean" property="columndesc" filter="true"/>&nbsp;
	     	 </td>              
	        </logic:iterate>     
				
		</tr>	
		<hrms:paginationdb id="element" name="warnConfigForm" sql_str="warnConfigForm.strsql" table="" where_str="" columns="warnConfigForm.columns" order_by="warnConfigForm.order" page_id="pagination" pagerows="${warnConfigForm.pagerows}" indexes="indexes">
			<%if (i % 2 == 0) {%>
			<tr class="trShallow">
				<%} else {%>			
			<tr class="trDeep">
				<%}i++;%>				
		   <logic:notEmpty name="warnConfigForm" property="tenplatelist">					
				<td align="center" class="RecordRow" nowrap>
		          <hrms:checkmultibox name="warnConfigForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
                </td> 
		    </logic:notEmpty>  
			    <td align="center" class="TableRow" style="display:none">
			    <bean:write name="element" property="nbase" filter="true"/><bean:write name="element" property="a0100" filter="true"/>
			    </td>			                      
				<td align="left" class="RecordRow" nowrap>
					<!--bean:write  name="element" property="o1name" filter="true"/-->
					&nbsp;<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page" />
					<bean:write name="codeitem" property="codename" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					<!--bean:write name="element" property="o2name" filter="true"/-->
					&nbsp;<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" uplevel="${warnConfigForm.uplevel}" scope="page" />
					<bean:write name="codeitem" property="codename" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					<!--bean:write name="element" property="o3name" filter="true"/-->
					&nbsp;<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page" />
					<bean:write name="codeitem" property="codename" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
				<%if(func!=null&&func.equals("1")){ %>
					&nbsp;<a href="javascript:winhref('<bean:write name="element" property="nbase" filter="true"/>','<bean:write name="element" property="a0100" filter="true"/>','<%=returnvalue%>')" >
					<bean:write name="element" property="a0101" filter="true" />
					&nbsp;
					</a>
				<%}else{ %>
				   &nbsp;<bean:write name="element" property="a0101" filter="true" />
				<%} %>
				</td>
				
				
			    <logic:iterate id="columnBean" name="warnConfigForm"  property="columnList" indexId="index">
			     <%String itemType = ((ColumnBean)columnBean).getColumnType();
		           String codesetid = ((ColumnBean)columnBean).getCodesetid();
		           String columnName=((ColumnBean)columnBean).getColumnName();		                
             	   LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
		           String value=(String)abean.get(columnName.toLowerCase());   		          
					if(itemType.equalsIgnoreCase("D")||itemType.equalsIgnoreCase("N")){%>
					<td align="right" class="RecordRow" nowrap>
				 <%}else{%>
					<td align="left" class="RecordRow" nowrap>
				 <%}%>   
				 <%if(codesetid!=null&&codesetid.length()>0&&!codesetid.equals("0"))
				 {
					 if("UM".equals(codesetid)){
						 if(AdminCode.getCode(codesetid,value)!=null){
							 out.println("&nbsp;"+AdminCode.getCodeName(codesetid,value));
						 }else{
							 out.println("&nbsp;"+AdminCode.getCodeName("UN",value));
						 } 
					 }else{
				   %>
				    &nbsp; <%=AdminCode.getCodeName(codesetid,value)%>
				   <%}				   
				 } else{%>
				 
		            &nbsp; <bean:write name="element" property="<%=((ColumnBean)columnBean).getColumnName().toLowerCase()%>" filter="true" />
				
				<%} %>		
				
				&nbsp;
		     	 	</td>              
		        </logic:iterate> 	
		        
			   </tr>
		    </hrms:paginationdb>
		</logic:equal>
		<logic:notEqual name="warnConfigForm" property="warntype" value="0">
		    <tr>
		      <logic:notEmpty name="warnConfigForm" property="tenplatelist">		
		       <td align="center" class="TableRow" nowrap>
		        <bean:message key="column.select"/>&nbsp;
               </td>  
		       </logic:notEmpty>  
		       <logic:equal name="warnConfigForm" property="warntype" value="1">
		           <td align="center" class="TableRow" nowrap>
				   <bean:message key="label.title.org" />
				   &nbsp;
			       </td>
		       </logic:equal>
		       <logic:equal name="warnConfigForm" property="warntype" value="2">
		       <td align="center" class="TableRow" nowrap>
				   <bean:message key="label.title.org" />
				   &nbsp;
			       </td>
		           <td align="center" class="TableRow" nowrap>
				   <bean:message key="label.title.dept" />
				   &nbsp;
			       </td>
		           <td align="center" class="TableRow" nowrap>
				   <bean:message key="tree.kkroot.gwdesc" />
				   &nbsp;
			       </td>
		       </logic:equal>
		        <logic:iterate id="ColumnBean" name="warnConfigForm"  property="columnList" indexId="index">
	               <td align="center" class="TableRow" nowrap>
	                <bean:write  name="ColumnBean" property="columndesc" filter="true"/>&nbsp;
	     	      </td>              
	             </logic:iterate>     
		    </tr>			    
		   <hrms:paginationdb id="element" name="warnConfigForm" sql_str="warnConfigForm.strsql" table="" where_str="" columns="warnConfigForm.columns" page_id="pagination" pagerows="${warnConfigForm.pagerows}" indexes="indexes">
			<%if (i % 2 == 0) {%>
			<tr class="trShallow">
				<%} else {%>
			</tr>
			<tr class="trDeep">
				<%}i++;%>				
		   <logic:notEmpty name="warnConfigForm" property="tenplatelist">					
				<td align="center" class="RecordRow" nowrap>
		          <hrms:checkmultibox name="warnConfigForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
                </td> 
		    </logic:notEmpty>  
		    <logic:equal name="warnConfigForm" property="warntype" value="1">
		      <td align="left" class="RecordRow" nowrap>
					&nbsp;<!--  bean:write  name="element" property="b0110" filter="true"/-->
					<a href="javascript:showOrgContext('<bean:write name="element" property="b0110" filter="true"/>');editorgUM('<%=returnvalue%>');">
					<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page" />
					<bean:write name="codeitem" property="codename" />					
					<hrms:codetoname codeid="UM" name="element" codevalue="b0110" codeitem="codeitem" uplevel="${warnConfigForm.uplevel}" scope="page" />
					<bean:write name="codeitem" property="codename" />
					</a>
					&nbsp;
				</td>
		    </logic:equal>
		     <logic:equal name="warnConfigForm" property="warntype" value="2">
		      <td align="left" class="RecordRow" nowrap>
					&nbsp;
					<%
						LazyDynaBean bean=(LazyDynaBean)pageContext.getAttribute("element");
						String e01a1 = (String)bean.get("e01a1");
						for(int n=e01a1.length()-1;n>0;n--){
							String codeitemid = e01a1.substring(0,n);
							String name = AdminCode.getCodeName("UN", codeitemid);
							if(name!=null&&name.length()>0){
								out.println(name);
								break;
							}
						}
					 %>
					&nbsp;
				</td>
		       <td align="left" class="RecordRow" nowrap>
					&nbsp;<!--bean:write name="element" property="o3name" filter="true"/-->
					<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" uplevel="${warnConfigForm.uplevel}" scope="page" />
					<bean:write name="codeitem" property="codename" />
					&nbsp;
				</td>
		       <td align="left" class="RecordRow" nowrap>
					&nbsp;<!--bean:write name="element" property="o3name" filter="true"/-->
					<a href="javascript:showOrgContext('<bean:write name="element" property="e01a1" filter="true"/>');editorgUK('<%=returnvalue%>','<bean:write name="element" property="e0122" filter="true"/>');">
					<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page" />
					<bean:write name="codeitem" property="codename" />
					</a>
					&nbsp;
				</td>
		     </logic:equal>
		          <logic:iterate id="columnBean" name="warnConfigForm"  property="columnList" indexId="index">
			     <%String itemType = ((ColumnBean)columnBean).getColumnType();
		           String codesetid = ((ColumnBean)columnBean).getCodesetid();
		           String columnName=((ColumnBean)columnBean).getColumnName();		       
             	   LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
		           String value=(String)abean.get(columnName.toLowerCase());   
					if(itemType.equalsIgnoreCase("D")||itemType.equalsIgnoreCase("N")){%>
					<td align="right" class="RecordRow">
				   <%}else{%>
					<td align="left" class="RecordRow">
				   <%}%>   
				   <%if(codesetid!=null&&codesetid.length()>0&&!codesetid.equals("0"))
				   {
					   if("UM".equals(codesetid)){
	                         if(AdminCode.getCode(codesetid,value)!=null){
	                             out.println("&nbsp;"+AdminCode.getCodeName(codesetid,value));
	                         }else{
	                             out.println("&nbsp;"+AdminCode.getCodeName("UN",value));
	                         } 
	                     }else{
	                   %>
	                    &nbsp; <%=AdminCode.getCodeName(codesetid,value)%>
				   <%}} else{%>
				 
		             <bean:write name="element" property="<%=((ColumnBean)columnBean).getColumnName().toLowerCase() %>" filter="true" />
				
				   <%} %>		
				
				   &nbsp;
		     	 	</td>  
		           </logic:iterate> 
		    </tr>
		    </hrms:paginationdb>
		</logic:notEqual>
      </table>
    </td>
  </tr>
  <tr>
   <td>
     <table width="100%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				<hrms:paginationtag name="warnConfigForm"
								pagerows="${warnConfigForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationdblink name="warnConfigForm" property="pagination" nameId="warnConfigForm" scope="page">
					</hrms:paginationdblink>
			</td>
		</tr>
	 </table>
   </td>
  </tr>
</table>
	
	

	<table width="85%" align="center">
		<tr>
		    <td align="left">
		      <logic:equal name="warnConfigForm" property="warntype" value="0">
		        <logic:notEmpty name="warnConfigForm" property="tenplatelist">
				模板					     
		        <html:select name="warnConfigForm" property="tenplateId" size="1" styleId="template">
                  <html:optionsCollection property="tenplatelist" value="dataValue" label="dataName"/>	        
                </html:select>
                <html:button styleClass="mybutton" property="bc_btn1" onclick="allSelectResult()">
					全部办理
				</html:button>&nbsp;
				<html:button styleClass="mybutton" property="bc_btn1" onclick="selectTenolate()">
					个别办理
				</html:button>&nbsp;
		        </logic:notEmpty>	
		      </logic:equal>   
				<html:button styleClass="mybutton" property="bc_btn1" onclick="exportExcel()">
					导出Excel
				</html:button>&nbsp;
		      <hrms:priv func_id="301201">   
		        <html:button styleClass="mybutton" property="bc_btn1" onclick="refreshWarn();">
			 		刷新
			    </html:button>&nbsp;
			  </hrms:priv>
		    <%if(ver!=null&&ver.equals("hl")){ %>
		    	<%if(returnlistvalue!=null&&returnlistvalue.equals("list")){ %>
			        <html:button styleClass="mybutton" property="bc_btn1" onclick="window.location.replace('/system/warn/info_all.do?br_query=link&ver=5');">
						<bean:message key="button.return" />
					</html:button>
				<%}else{ %>
			        <html:button styleClass="mybutton" property="bc_btn1" onclick="window.location.replace('/templates/index/portal.do?b_query=link');">
						<bean:message key="button.return" />
					</html:button>
				<%} %>
		    <%}else if(ver!=null&&ver.equals("el")){ %>
		        <html:button styleClass="mybutton" property="bc_btn1" onclick="window.location.replace('/templates/index/bi_portal.do?br_query=link');">
					<bean:message key="button.return" />
				</html:button>
			<%}else if(ver!=null&&ver.equals("bi")){ %>
		        <html:button styleClass="mybutton" property="bc_btn1" onclick="returnH();">
					<bean:message key="button.return" />
				</html:button>
		    <%}else if(ver!=null&&ver.equals("hcm")){ %>
		        <%if(returnlistvalue!=null&&returnlistvalue.equals("list")){ %>
			        <html:button styleClass="mybutton" property="bc_btn1" onclick="window.location.replace('/system/warn/info_all.do?br_query=link&ver=5');">
						<bean:message key="button.return" />
					</html:button>
				<%}else{ %>
			        <html:button styleClass="mybutton" property="bc_btn1" onclick="window.location.replace('/templates/index/hcm_portal.do?b_query=link');">
						<bean:message key="button.return" />
					</html:button>
				<%} %>
		    <%}else{ %>
		        <html:button styleClass="mybutton" property="bc_btn1" onclick="window.location.replace('/system/home.do?b_query=link');">
					<bean:message key="button.return" />
				</html:button>
		    <%} %>
				
			</td>
		</tr>
	</table>

</html:form>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style"  height="87" align="center">
           <tr>
             <td class="td_style" height=24><bean:message key="classdata.isnow.wiat"/></td>
          </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div>
<script language="javascript">
 MusterInitData();
</script>