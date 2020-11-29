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
	String bosflag="";
    if(userView!=null){
    	bosflag = userView.getBosflag();
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
         multMediaForm.action="/workbench/media/searchmediainfolist.do?b_delete=link";
         multMediaForm.submit();
      }
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
 		multMediaForm.action = "/workbench/media/searchmediainfolist.do?b_search=link&setname="+va+"&setprv=2&flag=notself&returnvalue=${selfInfoForm.returnvalue}&userbase=<bean:write name="selfInfoForm" property="userbase"/>&isAppEdite=1";
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
</script>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes />
<html:form action="/workbench/media/searchmediainfolist">
<html:hidden name="multMediaForm" property="setname"/>
<%if("hcm".equals(bosflag)){ %> 		
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" style="margin-top: 8px">
<%}else{ %>
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
<%} %>
  <tr>
     <td align="left"  nowrap>
        (&nbsp;<bean:message key="label.title.org"/>: <bean:write  name="multMediaForm" property="b0110" filter="true"/>&nbsp;
        <bean:message key="label.title.dept"/>: <bean:write  name="multMediaForm" property="e0122" filter="true"/>&nbsp;
        <bean:message key="label.title.name"/>: <bean:write  name="multMediaForm" property="a0101" filter="true"/>&nbsp;)
     </td>
  </tr>
</table>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
         
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
              <bean:message key="column.select"/>
             </td>
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
             
             <td align="center" class="TableRow" nowrap>
		      	<bean:message key="label.edit"/>            	
             </td>  
        	        	        
           </tr>
   	  </thead>
         <hrms:paginationdb id="element" name="multMediaForm"   sql_str="multMediaForm.strsql"  where_str="" columns="a0100,i9999,state,title,sortname,fileid" order_by=" order by i9999 " page_id="pagination"  pagerows="${multMediaForm.pagerows}"  keys="i9999"  indexes="indexes">          <%
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
               <hrms:checkmultibox name="multMediaForm" property="pagination.select" value="true" indexes="indexes"/>
            </td>     
            <logic:equal value="1" name="multMediaForm" property="approveflag">      
             <td align="center" class="RecordRow" nowrap>
               <logic:equal name="element" property="state" value="0">
                  <bean:message key="info.appleal.state0"/>
               </logic:equal>
               <logic:equal name="element" property="state" value="1">
                  <bean:message key="info.appleal.state1"/>
               </logic:equal>
               <logic:equal name="element" property="state" value="2">
                  <bean:message key="info.appleal.state2"/>
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
            	<a href="/servlet/vfsservlet?fileid=<%=fileid %>"  target="_blank"><bean:write  name="element" property="title" filter="false"/></a>
            </td>
             <td align="left" class="RecordRow" nowrap>                
               <bean:write  name="element" property="sortname" filter="true"/>
            </td>            
            <logic:equal name="multMediaForm" property="isUserEmploy" value="0">
              <td align="center" class="RecordRow" nowrap>
              <hrms:priv func_id="2606503,01030103">  
                 <a href="/workbench/media/searchmediainfolist.do?br_update=link&encryptParam=<%=PubFunc.encrypt("userbase="+userbase+"&a0100="+a0100+"&i9999="+i9999+"&filetitle="+title) %>"  target="mil_body"><img src="/images/edit.gif" border=0></a>
	          </hrms:priv> 
	           </td>
            </logic:equal>  
            <logic:notEqual name="multMediaForm" property="isUserEmploy" value="0">  
              <td align="center" class="RecordRow" nowrap>
                <hrms:priv func_id="03050203,01030103">  
                 <a href="/workbench/media/searchmediainfolist.do?br_update=link&encryptParam=<%=PubFunc.encrypt("userbase="+userbase+"&a0100="+a0100+"&i9999="+i9999+"&filetitle="+title) %>"  target="mil_body"><img src="/images/edit.gif" border=0></a>
	            </hrms:priv> 
	          </td>
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
<table  width="70%" align="left" cellpadding="0" cellspacing="0">
		<tr>
			<td></td>
		</tr>
        <tr>
           <td align="left" height="35px;">
           <logic:equal name="multMediaForm" property="check_main" value="yes">
           		<logic:equal name="multMediaForm" property="isUserEmploy" value="0">
           			<logic:equal name="multMediaForm" property="setprv" value="2">
		            	<logic:notEqual name="multMediaForm" property="a0100" value="su">
		        			<logic:notEqual name="multMediaForm" property="a0100" value="A0100">
				                 <hrms:priv func_id="2606501,01030101"> 
				               	   <hrms:submit styleClass="mybutton" property="b_add">
				            		<bean:message key="button.insert"/>
					 	   			</hrms:submit>  
					 			 </hrms:priv> 
						         <hrms:priv func_id="2606502,01030102">          	   
						 	       <input type="button" name="b_delete" value='<bean:message key="button.delete"/>' onclick="deleteC();" class="mybutton"> 
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
               	   <hrms:submit styleClass="mybutton" property="b_add">
            			<bean:message key="button.insert"/>
	 	   		   </hrms:submit>  
	 			</hrms:priv> 
			    <hrms:priv func_id="03050202,01030102">          	  
			 	   <input type="button" name="b_delete" value='<bean:message key="button.delete"/>' onclick="deleteC();" class="mybutton"> 
			    </hrms:priv>
			     		</logic:notEqual>
		         	</logic:notEqual>
		         </logic:equal>
	       </logic:notEqual>
         </logic:equal>
              
	         <logic:equal name="multMediaForm" property="isUserEmploy" value="3">
	 	     <hrms:submit styleClass="mybutton" property="b_appeal" function_id="0709040">
            		<bean:message key="button.appeal"/>
	 	     </hrms:submit> 
	 	    </logic:equal>
	 	   <logic:equal name="multMediaForm" property="returnvalue" value="1">
	 	   		<logic:equal value="1" name="selfInfoForm" property="isBrowse">
	 	   			<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00&a0100=${selfInfoForm.a0100 }','mil_body')">
	 	   		</logic:equal>
	 	   		<logic:notEqual value="1" name="selfInfoForm" property="isBrowse">	
	 	     <hrms:submit styleClass="mybutton" property="br_return">
            		  <bean:message key="button.return"/>
	 	     </hrms:submit>
	 	     </logic:notEqual>  	 	
	 	    </logic:equal>
	 	    <logic:equal name="multMediaForm" property="returnvalue" value="11">
	 	      <hrms:submit styleClass="mybutton" property="br_returnphoto">
            		  <bean:message key="button.return"/>
	 	       </hrms:submit>  	 	
	 	    </logic:equal>
	 	 
	 	     <logic:equal name="multMediaForm" property="returnvalue" value="2">
	 	     	<logic:equal value="1" name="selfInfoForm" property="isBrowse">
	 	     		<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/workbench/browse/showmediainfodetail.do?b_search=link&setname=A00','mil_body')">
	 	     	</logic:equal>
	 	     	<logic:notEqual value="1" name="selfInfoForm" property="isBrowse">
	 	          <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/workbench/info/showinfodata.do?b_queryinfo=link&check=no','nil_body')"> 	 	<!-- zgd 2014-2-11 修改记录录入切换到末页，单击多媒体的返回，返回到第一页 -->
	 	          <!-- <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/workbench/info/editselfinfo.do?b_return=link','il_body')"> 	 	 -->
	 	    	</logic:notEqual>
	 	    </logic:equal>	 	    	 	  
	 	     <logic:equal name="multMediaForm" property="returnvalue" value="5">
	 	          <input type="button" name="returnbutton" value='<bean:message key="button.return"/>' class="mybutton" onclick="window.close();">
	 	    </logic:equal>
	 	     <logic:equal name="multMediaForm" property="returnvalue" value="74">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/system/warn/result_manager.do?b_query=link','il_body');trun();">                 
	        </logic:equal>
	        <logic:equal name="multMediaForm" property="returnvalue" value="73">
	           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/system/warn/result_manager.do?b_query=link','i_body')">                 
	        </logic:equal>
	        <logic:equal name="multMediaForm" property="returnvalue" value="75">
	           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/system/warn/result_manager.do?b_query=link','il_body')">                 
	        </logic:equal>
	     </td>
          </tr>   
 </table>
</html:form>
