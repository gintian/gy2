<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.sys.QueryLogForm" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script type="text/javascript" src="/js/wz_tooltip.js"></script> 
<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>
<%
   int i = 0;
   QueryLogForm queryLogForm = (QueryLogForm)session.getAttribute("queryLogForm");
   String commitor = queryLogForm.getCommitor();//request.getParameter("commitor");
   String name = queryLogForm.getName();//request.getParameter("name");
   String beginexectime =queryLogForm.getBeginexectime();// request.getParameter("beginexectime");
   String endexectime = queryLogForm.getEndexectime();//request.getParameter("endexectime");
   String bend = queryLogForm.getBeginexectimeend();
   String eend = queryLogForm.getEndexectimeend();

   UserView userView=(UserView)session.getAttribute(WebConstant.userView);
   String isthree=userView.haveTheRoleProperty("16")?"3":userView.haveTheRoleProperty("15")?"2":"#";
   String blog = userView.getBosflag();
%>
<script language="javascript">
   function excecuteExcel()
   {
	var hashvo=new ParameterSet();			
	hashvo.setValue("commitor","${queryLogForm.commitor}");
	hashvo.setValue("name","${queryLogForm.name}");
	hashvo.setValue("beginexectime","${queryLogForm.beginexectime}");
	hashvo.setValue("endexectime","${queryLogForm.endexectime}");
	hashvo.setValue("beginexectimeend","${queryLogForm.beginexectimeend}");
	hashvo.setValue("endexectimeend","${queryLogForm.endexectimeend}");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showExcel,functionId:'10100103334'},hashvo);
   }	
   function showExcel(outparamters)
   {
	var url=outparamters.getValue("excelfile");	
	//20/3/5 xus vfs改造
	var win=open("/servlet/vfsservlet?fileid=" + url + "&fromjavafolder=true");
   }
   
   function checkselect(){
   	var dd=false;
   	for(var i=0;i<document.queryLogForm.elements.length;i++)
	{			
	   if(document.queryLogForm.elements[i].type=='checkbox'&&document.queryLogForm.elements[i].name!="selbox")
	   {	
		  if(document.queryLogForm.elements[i].checked){
		  	dd=true;
		  	break;
		  }
	   }
    }
   	if(dd){
   		return confirm("确认删除记录？");
   	}else{
   		alert("请选择记录!");
   	 	return dd;   		
   	}
   }
   
   function clearquery(){
   		$('commitor').value='';
   		$('name').value='';
   		$('beginexectime').value='';
   		$('endexectime').value='';
   		$('beginexectimeend').value = '';
   		$('endexectimeend').value = '';
   		queryLogForm.action="/system/security/query_log.do?b_search=link";
   		queryLogForm.submit();
   }
   
   function prompt(){
		if(confirm("确认清空记录？")){
		var hashvo=new ParameterSet();			
		hashvo.setValue("commitor","${queryLogForm.commitor}");
	hashvo.setValue("name","${queryLogForm.name}");
	hashvo.setValue("beginexectime","${queryLogForm.beginexectime}");
	hashvo.setValue("endexectime","${queryLogForm.endexectime}");
	hashvo.setValue("beginexectimeend","${queryLogForm.beginexectimeend}");
	hashvo.setValue("endexectimeend","${queryLogForm.endexectimeend}");
			var request=new Request({method:'post',asynchronous:false,onSuccess:doClear,functionId:'10100103335'},hashvo);
			 function doClear(outparamters)
		   {
			var flag=outparamters.getValue("flag");	
			if(flag="ok"){
				queryLogForm.action="/system/security/query_log.do?b_clear=link";
   				queryLogForm.submit();
			}else{
				alert("清空日志失败");
			}
		   }
		}
   }
   /**
   *许硕时间判断
   *16/09/14
   **/
   /**
   *开始日期
   **/
   var prevTime ="";
   function myBlur(obj){
   		prevTime = obj.value;
   }
   function compareTime(begin,end,obj){
   		if(begin>end){
   			alert("终止时间应该大于起始时间");
   			obj.value = prevTime;
   		}
   }
   function timeComp(obj){
   		begin=document.getElementsByName("beginexectime")[0].value;
   		end=document.getElementsByName("beginexectimeend")[0].value;
   		if(begin!=null&&begin!=""&&end!=null&&end!=""){
   			compareTime(begin,end,obj);
   		}
   }
   /**
   *结束日期
   **/
   function edtimeComp(obj){
   		begin=document.getElementsByName("endexectime")[0].value;
   		end=document.getElementsByName("endexectimeend")[0].value;
   		if(begin!=null&&begin!=""&&end!=null&&end!=""){
   			compareTime(begin,end,obj);
   		}
   }
</script>
<html:form action="/system/security/query_log">
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center" <%if(!"hcm".equals(blog)){ %>style="margin-top:10px;"<%} %>>
          <tr>
            <td colspan="4">
               <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable"> 
               <tr> 
                  <td height="20" align="left" class="TableRow" nowrap colspan="4">日志查询</td>
               </tr>    
                  <tr class="trDeep1">
                     <td align="right" nowrap valign="center" class="RecordRow"><bean:message key="column.submit.username"/></td>
                     <td align="left"  nowrap valign="center" class="RecordRow">
                        <%if(commitor == null || commitor.equals("")){%>
                          &nbsp;<input type="text" name="commitor" maxlength="15" class="text6" style="width:200px;">&nbsp;
                         <%}else{%> 
                	  &nbsp;<input type="text" name="commitor" value=<%=commitor%> maxlength="15" class="text6" style="width:200px;">&nbsp;
                	 <%}%> 
                      </td>
                     <td align="right" nowrap valign="center" class="RecordRow"><bean:message key="column.submit.begindate"/></td>
                     <td align="left"  nowrap valign="center" class="RecordRow">
                         <%-- <%if(beginexectime == null || beginexectime.equals("")){%>
                          &nbsp;<input type="text" name="beginexectime" maxlength="12" class="text6" extra="editor" dropDown="dropDownDate" style="width:75px;">&nbsp;至&nbsp;<input type="text" name="beginexectimeend" maxlength="12" class="text6" extra="editor" dropDown="dropDownDate" style="width:75px;">
                         <%}else{%>  --%>
                	  &nbsp;<input type="text" name="beginexectime" value="<%=beginexectime%>" maxlength="12" class="text6" extra="editor" dropDown="dropDownDate" style="width:150px;" onblur="myBlur(this)" onchange="timeComp(this)">&nbsp;至&nbsp;<input type="text" name="beginexectimeend" value="<%=bend %>" maxlength="12" class="text6" extra="editor" dropDown="dropDownDate" style="width:150px;" onblur="myBlur(this)" onchange="timeComp(this)">
                	 <%-- <%}%>   --%>
                	 <!-- jingq upd 2014.4.22 添加操作日志页面日期控件
                	   <%if(beginexectime == null || beginexectime.equals("")){%>
                          &nbsp;<input type="text" name="beginexectime" maxlength="12" class="text6">&nbsp;
                         <%}else{%> 
                      &nbsp;<input type="text" name="beginexectime" value=<%=beginexectime%> maxlength="12" class="text6">&nbsp;
                     <%}%> 
                	 -->
                      </td>
                     
                   </tr>
                   <tr class="trDeep1">
                     <td align="right" nowrap valign="center" class="RecordRow"><bean:message key="column.submit.function"/></td>
                      <td align="left"  nowrap valign="center" class="RecordRow">
                         <%if(name == null || name.equals("")){%>
                          &nbsp;<input type="text" name="name" maxlength="12" class="text6"  style="width:200px;">&nbsp;
                         <%}else{%> 
                	  &nbsp;<input type="text" name="name" value=<%=name%> maxlength="12" class="text6"  style="width:200px;">&nbsp;
                	 <%}%>  
                      </td>   
                     <td align="right" nowrap valign="center" class="RecordRow"><bean:message key="column.submit.enddate"/></td>
                     <td align="left"  nowrap valign="center" class="RecordRow">
                     <!-- 
                       <%if(endexectime == null || endexectime.equals("")){%>
                          &nbsp;<input type="text" name="endexectime" maxlength="12" class="text6">&nbsp;
                         <%}else{%> 
                      &nbsp;<input type="text" name="endexectime" value=<%=endexectime%> maxlength="12" class="text6">&nbsp;
                     <%}%> 
                     -->
                         <%-- <%if(endexectime == null || endexectime.equals("")){%>
                          &nbsp;<input type="text" name="endexectime" maxlength="12" class="text6" extra="editor" dropDown="dropDownDate" style="width:75px;">&nbsp;至&nbsp;<input type="text" name="endexectimeend" maxlength="12" class="text6" extra="editor" dropDown="dropDownDate" style="width:75px;">
                         <%}else{%>  --%>
                	  &nbsp;<input type="text" name="endexectime" value="<%=endexectime%>" maxlength="12" class="text6" extra="editor" dropDown="dropDownDate"  style="width:150px;" onblur="myBlur(this)" onchange="edtimeComp(this)">&nbsp;至&nbsp;<input type="text" name="endexectimeend" value="<%=eend%>" maxlength="12" class="text6" extra="editor" dropDown="dropDownDate"  style="width:150px;" onblur="myBlur(this)" onchange="edtimeComp(this)">
                	 <%-- <%}%>  --%> 
                      </td>
                          
                   </tr>  
                   
 <tr>
  <td align="center" colspan="4" class="RecordRow" style="height: 35px">
    &nbsp;&nbsp;
               <hrms:submit styleClass="mybutton" property="b_search"><bean:message key="button.query"/>
	 	</hrms:submit>
	 	<html:button  styleClass="mybutton" property="b_search" onclick="clearquery();"><bean:message key="button.clear"/></html:button>
  </td>
 </tr>                                                        
          
          </table>     
        </td>
      </tr>         
          <tr>
          <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:5px;">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		 <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.functionID"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.function"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.begindate"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.enddate"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.execstate"/>            	
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.romoteaddr"/>            	
	    </td> 
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.username"/>            	
	    </td> 
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.usertype"/>            	
	    </td> 
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="tree.unroot.undesc"/>            	
	    </td> 
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="kq.strut.more"/>            	
	    </td>    	    		        	        	        
           </tr>
   	  </thead>
        <hrms:paginationdb id="element" name="queryLogForm" sql_str="queryLogForm.strsql" table="" where_str="" columns="sequenceno,id,name,description,type,commitor,beginexectime,endexectime,execstatus,remoteaddr,errormsg,isthree,b0110,eventlog," order_by="order by beginexectime desc" page_id="pagination" pagerows="${queryLogForm.pagerows }" keys="" allmemo="1">
   	  
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
            <td align="center" class="RecordRow" nowrap>
            <logic:notEqual value="<%=isthree %>" name="element" property="isthree">
	   	 <hrms:checkmultibox name="queryLogForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
	   	</logic:notEqual>
	    </td>            
         
            <td align="left" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="id" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="name" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="beginexectime" filter="true"/>&nbsp;
	    </td> 	                
            <td align="left" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="endexectime" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="execstatus" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="remoteaddr" filter="true"/>&nbsp;
	    </td> 	                
            <td align="left" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="commitor" filter="true"/>&nbsp;
	    </td>  
	    <td align="left" class="RecordRow" nowrap>
	    	<logic:equal value="0" name="element" property="isthree">
	    		&nbsp;<bean:message key="column.submit.usertype.putong"/>&nbsp;
	    	</logic:equal>
	    	<logic:equal value="1" name="element" property="isthree">
	    		&nbsp;<bean:message key="label.role.sys"/>&nbsp;
	    	</logic:equal>
	    	<logic:equal value="2" name="element" property="isthree">
	    		&nbsp;<bean:message key="label.role.sycrecy"/>&nbsp;
	    	</logic:equal>
	    	<logic:equal value="3" name="element" property="isthree">
	    		&nbsp;<bean:message key="label.role.auditor"/>&nbsp;
	    	</logic:equal>
	    </td> 
	    <td align="left" class="RecordRow" nowrap>
	    	 <hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
	    	 &nbsp; <bean:write name="codeitem" property="codename" />&nbsp;
	    	 <logic:empty name="codeitem">
	    	 	 <hrms:codetoname codeid="UM" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
	    	 &nbsp; <bean:write name="codeitem" property="codename" />&nbsp;
	    	 </logic:empty>
	    </td> 
	    <bean:define id="eventlog" name="element" property="eventlog"></bean:define>  	   	    
	    <hrms:showitemmemo showtext="showtext" itemtype="M" setname="fr_txlog" tiptext="tiptext" text="${eventlog }" ></hrms:showitemmemo>             
                <td align="left" class="RecordRow" ${tiptext} nowrap> 
                    &nbsp;${showtext}&nbsp;
               </td>   		        	        	        
          </tr>
        </hrms:paginationdb>        
     </table>
     <table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		           <hrms:paginationtag name="queryLogForm" pagerows="${queryLogForm.pagerows}" property="pagination" scope="session" refresh="true"></hrms:paginationtag>
            </td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right">			
				<hrms:paginationdblink name="queryLogForm" property="pagination" nameId="queryLogForm" scope="page">
				</hrms:paginationdblink>				

			</td>
		</tr>
    </table>
     <table  width="100%" align="center">
          <tr>
            <td align="center" height="35px;">
            <!-- 只有su、安全保密员或安全审计员授权后才会有清空、删除 -->
            <%if("2".equals(isthree)||"3".equals(isthree)||"su".equals(userView.getUserId())){ %>
            <hrms:priv func_id="0809011,300431" module_id=""> 
	 	<input type="button" name="b_clear" value="<bean:message key="button.clearup"/>" class="mybutton" onclick="prompt();"">
	 	</hrms:priv>
	 	<hrms:priv func_id="0809012,300432" module_id="">
         	<hrms:submit styleClass="mybutton" property="b_delete" onclick="return checkselect();">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	 	</hrms:priv>
	 	<%} %>
	 	<input type="button" name="b_excel" value="<bean:message key="goabroad.collect.educe.excel"/>" class="mybutton" onclick="excecuteExcel();">
        
            </td>
          </tr>          
    </table>
       
    </tr>        
 </table>
</html:form>
