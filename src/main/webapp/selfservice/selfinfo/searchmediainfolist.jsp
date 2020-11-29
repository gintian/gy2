<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hjsj.hrms.actionform.media.MultMediaForm,com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.struts.constant.WebConstant,org.apache.commons.beanutils.DynaBean"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
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
<%
	  int i=0;
%>
<script language="javascript">
    function exeButtonAction(actionStr,target_str)
   {
       target_url=actionStr;
       window.open(target_url,target_str); 
   }
   function deleteC()
   {
   	  var len=document.multMediaForm.elements.length;
       var uu;
       for (var i=0;i<len;i++)
       {
           if (document.multMediaForm.elements[i].type=="checkbox")
           {
              if(document.multMediaForm.elements[i].checked==true)
              {
                uu="dd";
                break;
               }
           }
       }
       if(uu!="dd")
       {
          alert("请选择要删除的记录！");
          return false;
       }
      if(ifdel())
      {
         multMediaForm.action="/workbench/media/searchmediainfolist.do?b_appdelete=link";
         multMediaForm.submit();
      }
   }
   function addC() {
   		multMediaForm.action="/workbench/media/searchmediainfolist.do?b_appadd=link";
         multMediaForm.submit();
   }
   function trun()
   {
      parent.parent.menupnl.toggleCollapse(true);
   }
   function change() {
 	var list = document.getElementById("list");
 	va = list.value;
 	if (va == "A01") {
 		multMediaForm.action = "/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname=A01&isAppEdite=1";
 	} else if(va == "A00"){
 		<logic:equal name="multMediaForm" property="button" value="1">
 		multMediaForm.action = "/workbench/media/searchmediainfolist.do?b_search=link&setname="+va+"&setprv=2&flag=notself&returnvalue=${browseForm.returnvalue}&userbase=<bean:write name="browseForm" property="userbase"/>&isAppEdite=1";
 		</logic:equal>
 		<logic:notEqual name="multMediaForm" property="button" value="1">
 		multMediaForm.action = "/workbench/media/searchmediainfolist.do?b_search=link&setname="+va+"&setprv=2&flag=notself&returnvalue=${selfInfoForm.returnvalue}&userbase=<bean:write name="selfInfoForm" property="userbase"/>&isAppEdite=1";
 		</logic:notEqual>
 	}else {
 	
 	multMediaForm.action = "/workbench/info/searchselfdetailinfo.do?b_searchsort=search&setname="+va+"&flag=noself&isAppEdite=1";
 	}
 	multMediaForm.target="mil_body";
	multMediaForm.submit();
} 
function exeReturn(url,target) {
	multMediaForm.action = url;
	multMediaForm.target = target;
	multMediaForm.submit();
}
function approve() {
	if (confirm("您确定要报批？报批后该数据将不能修改！")) {
		multMediaForm.action = "/workbench/media/searchmediainfolist.do?b_appealss=link&isAppEdite=1";
		multMediaForm.target="mil_body";
		multMediaForm.submit();
	}
}

function approveall() {
	if (confirm("您确定要整体报批吗？整体报批后个人信息将不能修改！")) {
	<logic:equal name="multMediaForm" property="button" value="1">
		multMediaForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_approveall=link&savEdit=appbaopi&a0100=${browseForm.a0100}&userbase=${browseForm.userbase}";
	</logic:equal>
	<logic:notEqual name="multMediaForm" property="button" value="1">
		multMediaForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_approveall=link&savEdit=appbaopi&a0100=${selfInfoForm.a0100}&userbase=${selfInfoForm.userbase}";
	</logic:notEqual>
		multMediaForm.target="mil_body";
		multMediaForm.submit();
	}
}

function change() {
 	var list = document.getElementById("list");
 	va = list.value;
 	<logic:equal name="multMediaForm" property="button" value="1">
 		if (va == "A01") {
 		multMediaForm.action = "/workbench/browse/appeditselfinfo.do?b_edit=edit&actiontype=update&setname=A01&isAppEdite=1&isBrowse=1&a0100=${browseForm.a0100}&userbase=${browseForm.userbase}";
 	}  else if(va == "A00"){
 		multMediaForm.action ="/workbench/media/searchmediainfolist.do?b_appsearch=link&setname=A00&flag=notself&returnvalue=3&isUserEmploy=0&button=1";
 		
 	} else{
 		multMediaForm.action = "/workbench/browse/appeditselfinfo.do?b_defendother=search&actiontype=update&a0100=${browseForm.a0100}&userbase=${browseForm.userbase}&setname="+va+"&isAppEdite=1&isBrowse=1&i9999=i9999&flag=notself";
 	}
 	</logic:equal>
 	<logic:notEqual name="multMediaForm" property="button" value="1">
 	if (va == "A01") {
 		multMediaForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_defend=link&isAppEdite=1&setname="+va+"&i9999=I9999&actiontype=update";
 	} else if(va == "A00"){
 		multMediaForm.action ="/workbench/media/searchmediainfolist.do?b_appsearch=link&setname=A00&setprv=2&flag=self&returnvalue=3&isUserEmploy=0&button=0";
 	} else{
 		multMediaForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_defendother=search&setname="+va+"&flag=infoself&isAppEdite=1"
 	}
 	</logic:notEqual>
 	multMediaForm.target="mil_body";
	multMediaForm.submit();
}

function proveAllss(){
if(confirm("您确定要整体报批吗？整体报批后将不能修改！")){
		multMediaForm.action= "/workbench/browse/appeditselfinfo.do?b_prove=link&isAppEdite=1&&savEdit=appbaopi&a0100=${browseForm.a0100}&userbase=${browseForm.userbase}";
		multMediaForm.target = "mil_body";	 
   		multMediaForm.submit();
}else{
return;
}

}
</script>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes />
<html:form action="/workbench/media/appsearchmediainfolist">
<html:hidden name="multMediaForm" property="setname"/>
<logic:equal value="1" name="multMediaForm" property="button">
	<hrms:priv func_id="260112">
<div style="margin-top: 8px">
				<bean:message key="selfinfo.listinfo"/>
					<select id="list" name="fieldsetid" onchange="change();">
						<logic:iterate id="setList" name="browseForm" property="infosetlist">
							<logic:equal name="setList" property="priv_status" value="2">
								<logic:notEqual value="${setList.fieldsetid }" name="multMediaForm" property="virAxx"> 
									<logic:equal value="${setList.fieldsetid }" name="multMediaForm" property="setname">
										<option value="${setList.fieldsetid }" selected="selected">
											<bean:write name="setList" property="customdesc"/>
										</option>
									</logic:equal>
									<logic:notEqual value="${setList.fieldsetid }" name="multMediaForm" property="setname">
										<option value="${setList.fieldsetid }">
											<bean:write name="setList" property="customdesc"/>
										</option>
									</logic:notEqual>
								</logic:notEqual>
							</logic:equal>
						</logic:iterate>
					</select>
			</div> 
			</hrms:priv>		
</logic:equal>
<logic:notEqual value="1" name="multMediaForm" property="button">
<div style="margin-top: 8px">
				<bean:message key="selfinfo.listinfo"/>
					<select id="list" name="fieldsetid" onchange="change();">
						<logic:iterate id="setList" name="selfInfoForm" property="infoSetList">
							<logic:equal name="setList" property="priv_status" value="2">
								<logic:notEqual value="${setList.fieldsetid }" name="multMediaForm" property="virAxx"> 
									<logic:equal value="${setList.fieldsetid }" name="multMediaForm" property="setname">
										<option value="${setList.fieldsetid }" selected="selected">
											<bean:write name="setList" property="customdesc"/>
										</option>
									</logic:equal>
									<logic:notEqual value="${setList.fieldsetid }" name="multMediaForm" property="setname">
										<option value="${setList.fieldsetid }">
											<bean:write name="setList" property="customdesc"/>
										</option>
									</logic:notEqual>
								</logic:notEqual>
							</logic:equal>
						</logic:iterate>
					</select>
			</div> 		
</logic:notEqual>
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
  <tr>
     <td align="left"  nowrap>
        (<bean:message key="label.title.org"/>: <bean:write  name="multMediaForm" property="b0110" filter="true"/>&nbsp;
        <bean:message key="label.title.dept"/>: <bean:write  name="multMediaForm" property="e0122" filter="true"/>&nbsp;
        <bean:message key="label.title.name"/>: <bean:write  name="multMediaForm" property="a0101" filter="true"/>&nbsp;
        )
     </td>
  </tr>
</table>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
         
   	  <thead>
           <tr>
           	<logic:equal value="1" name="multMediaForm" property="button">
           		<logic:equal value="1" name="browseForm" property="isAble">
            <td align="center" class="TableRow" nowrap>
              <bean:message key="column.select"/>
             </td>
             </logic:equal>
           	</logic:equal>
           	<logic:notEqual value="1" name="multMediaForm" property="button">
           <logic:equal value="1" name="selfInfoForm" property="isAble">
            <td align="center" class="TableRow" nowrap>
              <bean:message key="column.select"/>
             </td>
             </logic:equal>
            </logic:notEqual>
           <!-- <td align="center" class="TableRow" nowrap>
               <bean:message key="conlumn.mediainfo.info_id"/>
             </td>-->   
           <logic:equal value="1" name="multMediaForm" property="approveflag">              	
             <td align="center" class="TableRow" nowrap>
                   <bean:message key="info.appleal.statedesc"/>
              </td>    
           </logic:equal>      
               <td align="center" class="TableRow" nowrap>
               <bean:message key="general.mediainfo.title"/>
               </td>
            
             <td align="center" class="TableRow" nowrap>
                 <bean:message key="conlumn.mediainfo.info_title"/>
             </td> 
             <logic:equal value="1" name="multMediaForm" property="button">
             <logic:equal value="1" name="browseForm" property="isAble">
             <logic:equal name="multMediaForm" property="setprv" value="2">
             <hrms:priv func_id="260112">
             <td align="center" class="TableRow" nowrap>
		      	<bean:message key="label.edit"/>            	
             </td> 
             </hrms:priv>
             </logic:equal>
        	 </logic:equal>
             </logic:equal>
             <logic:notEqual value="1" name="multMediaForm" property="button">  
             <logic:equal value="1" name="selfInfoForm" property="isAble">
             <logic:equal name="multMediaForm" property="setprv" value="2">
             <td align="center" class="TableRow" nowrap>
		      	<bean:message key="label.edit"/>           	
             </td> 
             </logic:equal> 
        	 </logic:equal>
        	 </logic:notEqual>       	        
           </tr>
   	  </thead>
         <hrms:paginationdb id="element" name="multMediaForm"   sql_str="multMediaForm.strsql"  where_str="" columns="state,a0100,i9999,title,sortname,fileid" order_by=" order by i9999 " page_id="pagination"  pagerows="${multMediaForm.pagerows}"  keys="i9999"  indexes="indexes">          
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
		<logic:equal value="1" name="multMediaForm" property="button">
			<logic:equal value="1" name="browseForm" property="isAble">
            	<td align="center" class="RecordRow" nowrap>
            		<logic:equal value="1" name="browseForm" property="isAble">
  						<logic:notEqual name="element" property="state" value="1">
  							<logic:notEqual name="element" property="state" value="3">
               					<hrms:checkmultibox name="multMediaForm" property="multMediaForm.select" value="true" indexes="indexes"/>
               				</logic:notEqual>
						</logic:notEqual>
					</logic:equal>
            	</td> 
			</logic:equal>
		</logic:equal>
		
		<logic:notEqual value="1" name="multMediaForm" property="button">
			<logic:equal value="1" name="selfInfoForm" property="isAble">
            	<td align="center" class="RecordRow" nowrap>
  						<logic:notEqual name="element" property="state" value="1">
  							<logic:notEqual name="element" property="state" value="3">
               					<hrms:checkmultibox name="multMediaForm" property="multMediaForm.select" value="true" indexes="indexes"/>
               				</logic:notEqual>
						</logic:notEqual>
            	</td> 
            </logic:equal> 
		</logic:notEqual> 
		  
		<logic:equal value="1" name="multMediaForm" property="approveflag">      
			<td align="center" class="RecordRow" nowrap>
				<logic:equal name="element" property="state" value="0">
					<bean:message key="info.appleal.state0"/>
               	</logic:equal>
               <logic:equal name="element" property="state" value="1">
                  <bean:message key="info.appleal.state1"/>
               </logic:equal>
               <logic:equal name="element" property="state" value="2">
                  <bean:message key="button.rejeect2"/>
               </logic:equal>
               <logic:equal name="element" property="state" value="3">
                  <bean:message key="info.appleal.state3"/>
               </logic:equal>
               <logic:notEqual name="element" property="state" value="0">
                 <logic:notEqual name="element" property="state" value="1">
                   <logic:notEqual name="element" property="state" value="2">
                      <logic:notEqual name="element" property="state" value="3">
                        <bean:message key="info.appleal.state0"/>
                      </logic:notEqual>
                    </logic:notEqual>
                  </logic:notEqual>
               </logic:notEqual>
            </td>
            </logic:equal>
            <bean:define id="elementid" name="element"></bean:define>
            <%
            DynaBean vo = (DynaBean)pageContext.getAttribute("elementid");
            String a0100 = (String)vo.get("a0100");
            String i9999 = (String)vo.get("i9999");
            String title = (String)vo.get("title");
            MultMediaForm multMediaForm=(MultMediaForm)session.getAttribute("multMediaForm");
            String userbase = multMediaForm.getUserbase();
             %>
            <td align="left" class="RecordRow" nowrap>  
            	<bean:define id="fileid" name="element" property="fileid"></bean:define>
            	<a href="/servlet/vfsservlet?fileid=<%=fileid %>" target="_blank"><bean:write  name="element" property="title" filter="false"/></a>
            </td>
            
             <td align="left" class="RecordRow" nowrap>                
               <bean:write  name="element" property="sortname" filter="true"/>
            </td>
             
            <logic:equal value="1" name="multMediaForm" property="button">
            <logic:equal value="1" name="browseForm" property="isAble"> 
            <logic:equal name="multMediaForm" property="setprv" value="2"> 
			<td align="center" class="RecordRow" nowrap>
            	         
            		<logic:equal name="multMediaForm" property="isUserEmploy" value="0">
            			
            				<hrms:priv func_id="260112">
              					<logic:notEqual name="element" property="state" value="1">
              						<logic:notEqual name="element" property="state" value="3">
							                 <a href="/workbench/media/searchmediainfolist.do?br_appupdate=link&encryptParam=<%=PubFunc.encrypt("userbase="+userbase+"&a0100="+a0100+"&i9999="+i9999+"&filetitle="+title) %>"  target="mil_body"><img src="/images/edit.gif" border=0></a>
	          						</logic:notEqual>
	          					</logic:notEqual>
	           				</hrms:priv>
            		</logic:equal>
            		  
            		<logic:notEqual name="multMediaForm" property="isUserEmploy" value="0">
            			<logic:equal name="element" property="state" value="1">
							    	&nbsp;
              			</logic:equal>
              			<logic:equal name="element" property="state" value="3">
							    	&nbsp;
              			</logic:equal> 
             			<hrms:priv func_id="260112"> 
              				<logic:notEqual name="element" property="state" value="1">
              					<logic:notEqual name="element" property="state" value="3">
							                 <a href="/workbench/media/searchmediainfolist.do?br_appupdate=link&encryptParam=<%=PubFunc.encrypt("userbase="+userbase+"&a0100="+a0100+"&i9999="+i9999+"&filetitle="+title) %>"  target="mil_body"><img src="/images/edit.gif" border=0></a>
	            					
	            				</logic:notEqual>
	            			</logic:notEqual>
	          
	          			</hrms:priv>
            		</logic:notEqual>
            	 
			</td>
			</logic:equal>
			</logic:equal>
            </logic:equal>
            
            <logic:notEqual value="1" name="multMediaForm" property="button">
            	<logic:equal value="1" name="selfInfoForm" property="isAble">
            <td align="center" class="RecordRow" nowrap> 	
            	          
            		<logic:equal name="multMediaForm" property="isUserEmploy" value="0">
              			<logic:notEqual name="element" property="state" value="1">
              				<logic:notEqual name="element" property="state" value="3">
              					<logic:equal name="multMediaForm" property="setprv" value="2">
              						<hrms:priv func_id="2606503">  
							                 <a href="/workbench/media/searchmediainfolist.do?br_appupdate=link&encryptParam=<%=PubFunc.encrypt("userbase="+userbase+"&a0100="+a0100+"&i9999="+i9999+"&filetitle="+title) %>"  target="mil_body"><img src="/images/edit.gif" border=0></a>
	          						</hrms:priv> 
	          					</logic:equal>
	          				</logic:notEqual>
	          			</logic:notEqual>
	           
            		</logic:equal>  
            		<logic:notEqual name="multMediaForm" property="isUserEmploy" value="0">  
              			<logic:equal name="element" property="state" value="1">
              				<logic:equal name="multMediaForm" property="setprv" value="2">
							    	&nbsp;
	            			</logic:equal>
              			</logic:equal>
              			<logic:equal name="element" property="state" value="3">
              				<logic:equal name="multMediaForm" property="setprv" value="2">
							    	&nbsp;
	            			</logic:equal>
              			</logic:equal>
              			<logic:notEqual name="element" property="state" value="1">
              				<logic:notEqual name="element" property="state" value="3">
              					<logic:equal name="multMediaForm" property="setprv" value="2">
                					<hrms:priv func_id="03050203,01030103"> 
						                 <a href="/workbench/media/searchmediainfolist.do?br_appupdate=link&encryptParam=<%=PubFunc.encrypt("userbase="+userbase+"&a0100="+a0100+"&i9999="+i9999+"&filetitle="+title) %>"  target="mil_body"><img src="/images/edit.gif" border=0></a>
	            					</hrms:priv> 
	            				</logic:equal>
	            			</logic:notEqual>
	            		</logic:notEqual>
            		</logic:notEqual>
            </td>
            	</logic:equal> 
            </logic:notEqual>         	                           	    		        	        	        
          </tr>
        </hrms:paginationdb>
         
</table>
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		   <td class="tdFontcolor">
					<hrms:paginationtag name="multMediaForm"
								pagerows="${multMediaForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			</td>
	             <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="multMediaForm" property="pagination" nameId="multMediaForm" >
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
<table  width="100%" align="left" style="margin-top: 2px">
        <tr>
           <td align="center">
           <logic:equal name="multMediaForm" property="inputchinfor" value="1">
				<logic:equal name="multMediaForm" property="approveflag" value="1">
					<logic:equal value="1" name="multMediaForm" property="button">
						<logic:equal value="1" name="browseForm" property="isAble">
						<hrms:priv func_id="260112">
						<logic:notEqual value="A00" name="multMediaForm" property="setname">
						<input type="button" class="mybutton" name="zheng" value="整体报批" onclick="proveAllss();"/>
						</logic:notEqual>
						<input type="button" class="mybutton" name="app" value="<bean:message key="button.appeal"/>"  onclick="approve();"/>
			
		
			               	    			<input type="button" name="b_appadd" value='<bean:message key="button.insert"/>' onclick="addC();" class="mybutton"> 
				 	
				         	  
									 	   <input type="button" name="b_appdelete" value='<bean:message key="button.delete"/>' onclick="deleteC();" class="mybutton"> 
									    </hrms:priv>
	    			
			           <logic:equal name="multMediaForm" property="check_main" value="yes">
			           		<logic:equal name="multMediaForm" property="isUserEmploy" value="0">
			           			<logic:equal name="multMediaForm" property="setprv" value="2">
					            	<logic:notEqual name="multMediaForm" property="a0100" value="su"></logic:notEqual>
					        			<logic:notEqual name="multMediaForm" property="a0100" value="A0100">
							                 
									</logic:notEqual>
								</logic:equal>
				         </logic:equal>
				         <logic:notEqual name="multMediaForm" property="isUserEmploy" value="0">
				         	<logic:equal name="multMediaForm" property="setprv" value="2">
					        	<logic:notEqual name="multMediaForm" property="a0100" value="su">
					                <logic:notEqual name="multMediaForm" property="a0100" value="A0100">
			                			
						     		</logic:notEqual>
					         	</logic:notEqual>
					         </logic:equal>
				       </logic:notEqual>
         		</logic:equal>
         		
          </logic:equal>
					
					</logic:equal>
					<logic:notEqual value="1" name="multMediaForm" property="button">
					<logic:equal value="1" name="selfInfoForm" property="isAble">
						 <!-- 此处删除整体报批 按钮  guodd 2015-04-30 version：1.13-->
						<hrms:priv func_id="01030104">
						<input type="button" class="mybutton" name="app" value="<bean:message key="button.appeal"/>"  onclick="approve();"/>
						</hrms:priv>
	    			
			           <logic:equal name="multMediaForm" property="check_main" value="yes">
			           		<logic:equal name="multMediaForm" property="isUserEmploy" value="0">
			           			<logic:equal name="multMediaForm" property="setprv" value="2">
					            	<logic:notEqual name="multMediaForm" property="a0100" value="su">
					        			<logic:notEqual name="multMediaForm" property="a0100" value="A0100">
							                 <hrms:priv func_id="2606501"> 
							               	   <input type="button" name="b_appadd" value='<bean:message key="button.insert"/>' onclick="addC();" class="mybutton">   
								 			 </hrms:priv> 
									         <hrms:priv func_id="2606502">          	   
									 	       <input type="button" name="b_appdelete" value='<bean:message key="button.delete"/>' onclick="deleteC();" class="mybutton"> 
									         </hrms:priv> 
									    </logic:notEqual>
									</logic:notEqual>
								</logic:equal>
				         </logic:equal>
				         <logic:notEqual name="multMediaForm" property="isUserEmploy" value="0">
				         	<logic:equal name="multMediaForm" property="setprv" value="2">
					        	<logic:notEqual name="multMediaForm" property="a0100" value="su">
					                <logic:notEqual name="multMediaForm" property="a0100" value="A0100">
			                			<hrms:priv func_id="03050201,01030101"> 
			               	    			<input type="button" name="b_appadd" value='<bean:message key="button.insert"/>' onclick="addC();" class="mybutton"> 
				 						</hrms:priv> 
									    <hrms:priv func_id="03050202,01030102">          	  
									 	   <input type="button" name="b_appdelete" value='<bean:message key="button.delete"/>' onclick="deleteC();" class="mybutton"> 
									    </hrms:priv>
						     		</logic:notEqual>
					         	</logic:notEqual>
					         </logic:equal>
				       </logic:notEqual>
         		</logic:equal>
         		
          </logic:equal>
          </logic:notEqual>
	    		</logic:equal>
			</logic:equal>
			   <logic:equal name="multMediaForm" property="button" value="1"> 
	        <logic:equal value="3" name="multMediaForm" property="returnvalue">
	        	<logic:equal value="1" name="browseForm" property="fromphoto">
        		<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showinfodata.do?b_view_photo=link&code=${browseForm.code}&kind=${browseForm.kind}','nil_body')">
        	</logic:equal>
        		<logic:notEqual value="1" name="browseForm" property="fromphoto">
	        	<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_returnshow=browse','nil_body')">
	        	</logic:notEqual>
	        </logic:equal>
	        </logic:equal>
	        <logic:notEqual value="3" name="multMediaForm" property="returnvalue"> 
		        <logic:equal value="1" name="multMediaForm" property="button">
		        <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showselfinfo.do?br_returnquery=query','il_body')">
		     	</logic:equal>
	     	</logic:notEqual>
	     </td>
          </tr>   
 </table>
</html:form><!--这个地方不能这么改  如果是word文档 会出现在本界面当中导致界面卡死  -->
<!-- <script>
//a标签下载文件时，会先打开空白页面     bug 34721 wangb 20180208
var as = document.getElementsByTagName('a');
for(var i=0;i<as.length;i++){
	if(as[i].getAttribute('target') == '_blank'){
		as[i].setAttribute('target','_self');
	}
}
</script> -->