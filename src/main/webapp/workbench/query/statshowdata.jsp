<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
  UserView userView=(UserView)session.getAttribute(WebConstant.userView);
  String url="";
  if(userView != null)
  {
     url=userView.getBosflag();
  
  }
  String path=request.getParameter("path");
  path=path==null?"":path;
%>
<script language="javascript">
  function winhref(url,target,a0100)
  {
   if(a0100=="")
      return false;
   var o_obj=document.getElementById('a0100');   
   if(o_obj)
     o_obj.value=a0100;   
   queryInterfaceForm.action=url;
   queryInterfaceForm.target=target;
   queryInterfaceForm.submit();
  }
  function winhref1(url,target,a0100)
  {
   if(a0100=="")
      return false;   
   //queryInterfaceForm.action=url;
   //queryInterfaceForm.target=target;
  // queryInterfaceForm.submit();
	newwin=window.open(url+"&a0100="+a0100,target,"toolbar=no,location=0,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
   
  }    

  document.oncontextmenu=function() 
   { 
      return　false; 
   } 
   function back(flag,target)
   {
     if(flag=="char")
     {
        queryInterfaceForm.action="/general/static/commonstatic/statshow.do?b_retreechat=link";
        queryInterfaceForm.target=target;
        queryInterfaceForm.submit();
     }else if(flag=="char2")
     {
        queryInterfaceForm.action="/general/static/commonstatic/statshow.do?b_return=link";
        queryInterfaceForm.target=target;
        queryInterfaceForm.submit();
     }else if(flag=="double2")
     {
        queryInterfaceForm.action="/general/static/commonstatic/statshow.do?b_returndouble=link";
        queryInterfaceForm.target=target;
        queryInterfaceForm.submit();
     }else
     {
        queryInterfaceForm.action="/general/static/commonstatic/statshow.do?b_retreedouble=link";
        queryInterfaceForm.target=target;
        queryInterfaceForm.submit();
     }
     
   }
   function winopen(url,a0100)
  {
      if(a0100=="")
        return false;
        var o_obj=document.getElementById('a0100');   
      if(o_obj)
        o_obj.value=a0100;   
      //window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=yes,menubar=yes,location=yes,resizable=no,status=yes");
       queryInterfaceForm.action=url;
       queryInterfaceForm.target="_blank";
       queryInterfaceForm.submit();
  }  
  function returnH(flag)
   {
      if(flag=="1")
      {
         queryInterfaceForm.action="/general/static/commonstatic/statshow.do?b_return=link";
         queryInterfaceForm.target="_self";
         queryInterfaceForm.submit();
      }else if(flag=="2")
      {
         queryInterfaceForm.action="/general/static/commonstatic/statshow.do?b_returndouble=link";
         queryInterfaceForm.target="_self";
         queryInterfaceForm.submit();
      }else if(flag=="13")
      {
         queryInterfaceForm.action="/templates/index/bi_portal.do?br_query=link";
         queryInterfaceForm.target="_self";
         queryInterfaceForm.submit();
      }else if(flag=="jgfx")
      {
         queryInterfaceForm.action="/general/deci/statics/employmakeupanalyse.do?b_search=link";         
         queryInterfaceForm.target="_self";
         queryInterfaceForm.submit();
      }
                   	
   } 
  function viewPhoto()
   {
       queryInterfaceForm.action="/workbench/query/query_interface1_photo.do?b_view_photo=link&path=<%=path %>";
       queryInterfaceForm.target="_self";
       queryInterfaceForm.submit();
   }  
   function change(obj)
   {
      queryInterfaceForm.action="/general/static/commonstatic/statshow.do?b_data=link&userbases="+obj.value;      
      queryInterfaceForm.submit();
   }
   function openwin(url)
   {
     //alert(url);
     window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+(screen.availHeight-100)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
   }
   function multimediahref(dbname,a0100){
	var thecodeurl =""; 
	var return_vo=null;
	var setname = "A01";
	var dw=800,dh=500,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
  	thecodeurl="/general/inform/multimedia/multimedia_tree.do?b_query=link&setid="+setname+"&a0100="+a0100+"&nbase="+dbname+"&dbflag=A&canedit=false";
  	return_vo= window.showModalDialog(thecodeurl, "", 
  	"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:800px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");
}
</script>
<hrms:themes></hrms:themes>
<%int i=0;%>
<%if("hcm".equalsIgnoreCase(url)){ %>
<style>
.ListTable{
	width:expression(document.body.clientWidth-30);
}
</style>
<%}else{ %>
<style>
.ListTable{
	width:expression(document.body.clientWidth-30);
}
</style>
<%} %>
<html:form action="/workbench/query/query_interface1">
<input type="hidden" name="a0100" id="a0100">
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
  <td>
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	   <thead>
         
           <logic:iterate id="element"    name="queryInterfaceForm"  property="fieldlist" indexId="index">
                <td align="center" class="TableRow" nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>
	      		</td>  
            </logic:iterate>
      	    <logic:notEqual name="queryInterfaceForm" property="tabid" value="-1">
            <td align="center" class="TableRow" nowrap>
		     	<bean:message key="tab.base.info"/>          	
		    </td>	                
		    </logic:notEqual>            
            <td align="center" class="TableRow" nowrap>
		     	<bean:message key="tab.synthesis.info"/>          	
		    </td> 
		    <!-- 加上显示附件列  wangb 20170602 28065  --> 
		    <logic:equal name="queryInterfaceForm" property="multimedia_file_flag" value="1">
	 		      <td align="center" class="TableRow" nowrap>
					<bean:message key="conlumn.resource_list.name"/>             	
				  </td> 
			  </logic:equal>	 
   	  </thead>
          <hrms:paginationdb id="element" name="queryInterfaceForm" sql_str="queryInterfaceForm.strsql" table="" where_str="queryInterfaceForm.cond_str" columns="queryInterfaceForm.columns" order_by="queryInterfaceForm.order_by" distinct="${queryInterfaceForm.distinct}" pagerows="20" page_id="pagination">
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
         <bean:define id="db" name="element" property="db"/> 
         <bean:define id="a0100" name="element" property="a0100"/>
	    <logic:iterate id="fielditem"  name="queryInterfaceForm"  property="fieldlist" indexId="index">
              <td align="center" class="RecordRow" nowrap>  
                 <logic:notEqual name="fielditem" property="codesetid" value="0">
                   <logic:equal name="fielditem" property="codesetid" value="UM">
          	         <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem"  uplevel="${queryInterfaceForm.uplevel}" scope="page"/>  	      
          	         <bean:write name="codeitem" property="codename" />  
          	       </logic:equal>
          	        <logic:notEqual name="fielditem" property="codesetid" value="UM">
          	         <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	         <bean:write name="codeitem" property="codename" />  
          	       </logic:notEqual> 
          	                        
                 </logic:notEqual>
                 <logic:equal name="fielditem" property="codesetid" value="0">
                   <bean:write name="element" property="${fielditem.itemid}" filter="false"/>               
                 </logic:equal>                                
	      </td>   	                          
         </logic:iterate>
         <%
         LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
  	   String a0100_encrypt=(String)abean.get("a0100");              	            	   
        request.setAttribute("a0100_encrypt",PubFunc.encrypt(a0100_encrypt)); 
         %>
         <logic:notEqual name="queryInterfaceForm" property="tabid" value="-1">
              <td align="center" class="RecordRow" nowrap>
               		<a href="###" onclick="javascript:openwin('/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase=<%=((String)pageContext.getAttribute("db"))/*.substring(1)  不截取人员库字符 wangb 20170726*/ %>&inforkind=1&a0100=${a0100_encrypt}&tabid=${queryInterfaceForm.tabid}&multi_cards=-1','<bean:write name="element" property="a0100" filter="true"/>');"><img src="/images/table.gif" border="0"></a>
		      </td>	                
         </logic:notEqual>	
            <td align="center" class="RecordRow">   
            <%
  if("bi".equals(path)){
  	
  	%>
  	      <a href="###" onclick="javascript:winhref1('/workbench/browse/showselfinfo.do?b_search=link&userbase=<%=((String)pageContext.getAttribute("db"))/*.substring(1) 不截取人员库字符 wangb 20170726*/ %>&flag=notself&returnvalue=100001','_blank','<bean:write name="element" property="a0100" filter="true"/>');"><img src="/images/view.gif" border="0"></a>
  	<%
  } else{%>            
                       <a href="###" onclick="javascript:winhref('/workbench/browse/showselfinfo.do?b_search=link&userbase=<%=((String)pageContext.getAttribute("db"))/*.substring(1) 不截取人员库字符 wangb 20170726*/ %>&flag=notself&returnvalue=190','il_body','<bean:write name="element" property="a0100" filter="true"/>');"><img src="/images/view.gif" border="0"></a>
	         <%}%>
	         </td>	
	         <!-- 加上显示附件列  wangb 20170602 28065  --> 
	         <logic:equal name="queryInterfaceForm" property="multimedia_file_flag" value="1">    
	         	<td align="center" class="RecordRow" nowrap>
            		<!--dbpre 数据为空 wangb 20170726 <a href="###"  onclick='multimediahref("<bean:write name="queryInterfaceForm" property="dbpre" filter="true"/>","<bean:write name="element" property="a0100" filter="true"/>");'><img src="/images/muli_view.gif" border=0></a> -->
            		<a href="###"  onclick='multimediahref("<bean:write name="element" property="db" filter="true"/>","<bean:write name="element" property="a0100" filter="true"/>");'><img src="/images/muli_view.gif" border=0></a>
            		
	      		</td>	    	    		        	        	        
	      	</logic:equal>
          </tr>
        </hrms:paginationdb>  
   </table>
 </td>             	    	    	    		        	        	        
 </tr>
 <tr>
  <td>
    <table  width="100%" class="RecordRowP" align="center">

		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="queryInterfaceForm" property="pagination" nameId="queryInterfaceForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
   </table>
  </td>
 </tr>
</table>

<table  width="100%" align="center" style="margin-top:5px;margin-left:3px;">
          <tr>
            <td align="left">
         	    
	 	   <input type="button" name="addbutton"  value="<bean:message key="button.query.viewphoto"/>" class="mybutton" onclick='viewPhoto();' >  	
	 	     	   
            </td>            
          </tr>          
</table>

</html:form>
