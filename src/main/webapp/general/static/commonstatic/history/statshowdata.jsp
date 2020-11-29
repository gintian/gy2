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
%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"> 
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
   function turn()
{
   parent.menupnl.toggleCollapse(false);
}
   function winhrefOT(url,target,a0100)
{
   //if(a0100=="")
      //return false;
   var uniqueitem='<bean:write name="historyStatForm" property="uniqueitem" />';
   if(uniqueitem=='a0100'){
   	   if(a0100=="")
      		return false;    
	   historyStatForm.action=url+"&a0100="+a0100;
	   historyStatForm.target=target;
	  // turn();
	   historyStatForm.submit();
   }else{
  		if(a0100==""){
  			alert("唯一性指标值为空人员不予查看信息!");
  			return false;
  		}
   		var hashvo = new ParameterSet();
		hashvo.setValue("uniqueitem",uniqueitem);
		hashvo.setValue("a0100",a0100);
		hashvo.setValue("target",target);
		var request=new Request({method:"post",asynchronous:false,onSuccess:winlocation,functionId:"0201001191"},hashvo);
   }
}
function winlocation(outparamters){
	var newa0100=outparamters.getValue("a0100");
		var nbase=outparamters.getValue("nbase");
		var msg=outparamters.getValue("msg");
	if("ok"==msg){
		//turn();
	   var strUrl="/workbench/browse/showselfinfo.do?b_search=link&userbase="+nbase+"&flag=notself&returnvalue=ht&a0100="+newa0100;
	   window.location.href=strUrl;
   }else if(msg!='error'){
   		alert(msg);
   }
}

  function winhref(url,target,a0100)
  {
   if(a0100=="")
      return false;
   var o_obj=document.getElementById('a0100');   
   if(o_obj)
     o_obj.value=a0100;   
   historyStatForm.action=url;
   historyStatForm.target=target;
   historyStatForm.submit();
  }  
  document.oncontextmenu = function() {return false;}
   function back(flag,target)
   {
     if(flag=="char")
     {
        historyStatForm.action="/general/static/commonstatic/statshow.do?b_retreechat=link";
        historyStatForm.target=target;
        historyStatForm.submit();
     }else if(flag=="char2")
     {
        historyStatForm.action="/general/static/commonstatic/statshow.do?b_return=link";
        historyStatForm.target=target;
        historyStatForm.submit();
     }else
     {
        historyStatForm.action="/general/static/commonstatic/statshow.do?b_retreedouble=link";
        historyStatForm.target=target;
        historyStatForm.submit();
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
       historyStatForm.action=url;
       historyStatForm.target="_blank";
       historyStatForm.submit();
  }  
  function returnH(flag)
   {
      if(flag=="1")
      {
         historyStatForm.action="/general/static/commonstatic/history/statshow.do?b_chart=link";
         historyStatForm.target="_self";
         historyStatForm.submit();
      }else if(flag=="2")
      {
         historyStatForm.action="/general/static/commonstatic/history/statshow.do?b_doubledata=link";
         historyStatForm.target="_self";
         historyStatForm.submit();
      }else if(flag=="13")
      {
         //historyStatForm.action="/general/static/commonstatic/statshowmsgchart.do?b_msgchart2=link&statid=${historyStatForm.statid}&chart_type=${historyStatForm.chart_type}";
         historyStatForm.action="/templates/index/bi_portal.do?br_query=link";
         historyStatForm.target="_self";
         historyStatForm.submit();
      }
                   	
   } 
  function viewPhoto()
   {
       historyStatForm.action="/general/static/commonstatic/statshow.do?b_view_photo=link";
       historyStatForm.target="_self";
       historyStatForm.submit();
   }  
   function change()
   {
      historyStatForm.action="/general/static/commonstatic/statshow.do?b_data=link";      
      historyStatForm.submit();
   }
</script>
<%int i=0;%>
<hrms:themes />
<%if("hcm".equalsIgnoreCase(url)){ %>
<style>
.ListTable{
	width:expression(document.body.clientWidth-10);
	margin-left:0px;
	margin-top:4px;
}
</style>
<%}else{ %>
<style>
.ListTable{
	width:expression(document.body.clientWidth-10);
	margin-left:1px;
	margin-top:3px;
}
</style>
<%} %>

<html:form action="/general/static/commonstatic/history/statshow">
<input type="hidden" name="a0100" id="a0100">
<logic:equal name="historyStatForm" property="infokind" value="1">
<table width="98%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<logic:equal  name="historyStatForm"  property="flag" value="13">
<tr>
  <td>
  <bean:message key="static.stor"/>
     <html:select name="historyStatForm" property="userbase" size="1"  onchange="javascript:change();">
                           <html:optionsCollection property="nbaselist" value="dataValue" label="dataName"/>
                  </html:select> 
  </td>
</tr>
</logic:equal>
<tr>
  <td>
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	   <thead>
           <logic:iterate id="element"    name="historyStatForm"  property="fieldlist" indexId="index">
              <logic:notEqual name="element" property="priv_status" value="0">               
                <td align="center" class="TableRow" nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>
	      		</td>  
  	          </logic:notEqual>	 
            </logic:iterate>          
            <td align="center" class="TableRow" nowrap>
		     	<bean:message key="tab.synthesis.info"/>          	
		    </td>        
   	  </thead>
          <hrms:paginationdb id="element" name="historyStatForm" sql_str="historyStatForm.strsql" table="" where_str="historyStatForm.cond_str" columns="historyStatForm.columns" order_by="historyStatForm.order_by" pagerows="21" page_id="pagination">
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
          
	    <logic:iterate id="fielditem"  name="historyStatForm"  property="fieldlist" indexId="index">
              <logic:notEqual name="fielditem" property="priv_status" value="0">             
              <td align="left" class="RecordRow" nowrap>  
                 <logic:notEqual name="fielditem" property="codesetid" value="0">
                   <logic:equal name="fielditem" property="codesetid" value="UM">
          	         <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem"  uplevel="${historyStatForm.uplevel}" scope="page"/>  	      
          	         <!-- 
          	            	//tianye update start
							//关联部门的指标支持指定单位（部门中查不出信息就去单位中查找）
          	            	-->
          	            	<logic:notEqual  name="codeitem" property="codename" value="">
          	           			<bean:write name="codeitem" property="codename" /> 
          	           		</logic:notEqual>
          	          		<logic:equal  name="codeitem" property="codename" value="">
          	          		 	<hrms:codetoname codeid="UN" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page" uplevel="${historyStatForm.uplevel}"/>  
          	           			<bean:write name="codeitem" property="codename" /> 
          	           		</logic:equal>   
          	           		<!-- end -->    
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
	      </logic:notEqual>	 	                          
         </logic:iterate> 	
            <td align="center" class="RecordRow">
             <a href="###" onclick="javascript:winhrefOT('/workbench/browse/showselfinfo.do?b_search=link&userbase=<bean:write name="historyStatForm" property="userbase" filter="true"/>&flag=notself&returnvalue=ht','mmil_body','<bean:write name="element" property="${historyStatForm.uniqueitem }" filter="true"/>');"><img src="/images/view.gif" border="0"></a>      
	         </td>	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>  
   </table>
 </td>             	    	    	    		        	        	        
 </tr>
 <tr>
  <td>
    <table  width="100%" class="RecordRowP" align="center" border="0" cellpadding="0" cellspacing="0">
		
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
		          <p align="right"><hrms:paginationdblink name="historyStatForm" property="pagination" nameId="historyStatForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
   </table>
  </td>
 </tr>
</table>

<table  width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td height="5px"></td>
	</tr>
    <tr>
      <td align="center">
   	    
		<!-- input type="button" name="addbutton"  value="<bean:message key="button.query.viewphoto"/>" class="mybutton" onclick='viewPhoto();' >  	 -->
         <input type="button" name="addbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('${historyStatForm.type }');" >
      </td>            
    </tr>          
</table>
</logic:equal>

</html:form>
