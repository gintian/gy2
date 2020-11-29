<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<%@ page import="com.hjsj.hrms.actionform.selfinfomation.SelfInfoForm"%>

<%
	  UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	  int i=0;
	  SelfInfoForm selfInfoForm=(SelfInfoForm)session.getAttribute("selfInfoForm");
	  String inputchinfor = selfInfoForm.getInputchinfor();
	  String approveflag = selfInfoForm.getApproveflag();
	  String flag ="";
	  if(!userView.hasTheFunction("010301"))
	      return;
%>
<script language="javascript">
function ykcard()
{
  target_url="/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase=${selfInfoForm.userbase}&tabid=${selfInfoForm.emp_cardId}&multi_cards=-1&inforkind=1&npage=1&userpriv=${selfInfoForm.userpriv}&flag=infoself";
  window.open(target_url,"nn"); 
  //selfInfoForm.action=target_url;
  //selfInfoForm.target="_blank";
  //selfInfoForm.submit();
}
function bedit()
{
    selfInfoForm.action="/selfservice/selfinfo/searchselfdetailinfo.do?b_edit=link";
    selfInfoForm.target="_self";
    selfInfoForm.submit();  
}
</script>
<hrms:themes />
<html:form action="/selfservice/selfinfo/searchselfdetailinfo">
  <html:hidden name="selfInfoForm" property="a0100"/>
  <html:hidden name="selfInfoForm" property="setname"/>
    <html:hidden name="selfInfoForm" property="userbase"/>
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
    <tr>
           <td align="left"  nowrap>
                (<bean:message key="label.title.org"/>: <bean:write  name="selfInfoForm" property="b0110" filter="true"/>&nbsp;
                <bean:message key="label.title.dept"/>: <bean:write  name="selfInfoForm" property="e0122" filter="true"/>&nbsp;
                <bean:message key="label.title.name"/>: <bean:write  name="selfInfoForm" property="a0101" filter="true"/>&nbsp;
                 )
              </td>
          </tr>
</table>

<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <logic:equal name="selfInfoForm" property="setprv" value="2">
             <td align="center" class="TableRow" nowrap>
              <!--<bean:message key="column.select"/>-->
              <input type="checkbox" name="sfull" onclick="full();">
             </td>
             <%if(!(inputchinfor.equals("1")&&approveflag.equals("1"))) {%>
             <td align="center" class="TableRow" nowrap>
             <bean:message key='button.new.insert'/>
             </td>
             <%} %>
             </logic:equal>
             <logic:equal value="1" name="selfInfoForm" property="approveflag">
             <logic:notEqual value="1" name="selfInfoForm" property="inputchinfor">   
               <td align="center" class="TableRow" nowrap>
                   <bean:message key="info.appleal.statedesc"/>
                </td>
             </logic:notEqual>
           </logic:equal>
           
             
            <logic:greaterThan name="selfInfoForm" property="setprv" value="1">
            <%if(!(inputchinfor.equals("1")&&approveflag.equals("1"))) {%>
             <td align="center" class="TableRow" nowrap>
		<bean:message key='column.operation'/>          	
             </td> 
             <%}%> 
             <!-- yuxiaochun add programe -->   
             
            </logic:greaterThan>  
             <logic:iterate id="element"    name="selfInfoForm"  property="infoFieldList"> 
              <td align="center" class="TableRow" nowrap>
                  <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>
              </td>
             </logic:iterate>  		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="selfInfoForm" property="selfInfoForm.list" indexes="indexes"  pagination="selfInfoForm.pagination" pageCount="${selfInfoForm.num_per_page}" scope="session">
         <bean:define id="i9" name="element" property="string(i9999)"/>
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
          RecordVo vo=(RecordVo)element;           
          %>   
           <html:hidden name="selfInfoForm" property="actiontype" value="new"/>
            <logic:equal name="selfInfoForm" property="setprv" value="2">
            <td align="center" class="RecordRow" nowrap>
             <logic:notEqual value="1" name="selfInfoForm" property="approveflag">  
             <logic:notEqual name="element" property="string(state)" value="1">
                    <logic:notEqual name="element" property="string(state)" value="3">
                    
                    <logic:notEqual name="element" property="string(state)" value="4">
                    <logic:notEqual name="element" property="string(state)" value="6">
               <hrms:checkmultibox name="selfInfoForm" property="selfInfoForm.select" value="true" indexes="indexes"/>
            	
            	</logic:notEqual>
            	</logic:notEqual>
            </logic:notEqual>
            </logic:notEqual>
            </logic:notEqual>
             <logic:equal value="1" name="selfInfoForm" property="approveflag">  
              <hrms:checkmultibox name="selfInfoForm" property="selfInfoForm.select" value="true" indexes="indexes"/>
            	
             </logic:equal>
            </td>
           <%if(!(inputchinfor.equals("1")&&approveflag.equals("1"))) {%>
            <td align="center" class="RecordRow" nowrap>
            
            <!-- 
            这里增加新的纪录
            -->
             <img src="/images/goto_input.gif" border=0 onclick="cadd(${i9})">
            </td>
            <%}%>
            </logic:equal>
            <logic:equal value="1" name="selfInfoForm" property="approveflag">
            <logic:notEqual value="1" name="selfInfoForm" property="inputchinfor">   
            <td align="center" class="RecordRow" nowrap>
               <logic:equal name="element" property="string(state)" value="0">
                  <bean:message key="info.appleal.state0"/>
               </logic:equal>
               <logic:equal name="element" property="string(state)" value="1">
                  <bean:message key="info.appleal.state1"/>
               </logic:equal>
               <logic:equal name="element" property="string(state)" value="2">
                  <bean:message key="info.appleal.state2"/>
               </logic:equal>
               <logic:equal name="element" property="string(state)" value="3">
                  <bean:message key="info.appleal.state3"/>
               </logic:equal>
               <logic:equal name="element" property="string(state)" value="4">
                  <bean:message key='button.app'/>
               </logic:equal>
               <logic:equal name="element" property="string(state)" value="5">
                  <bean:message key='workdiary.message.can.update'/>
               </logic:equal>
               <logic:equal name="element" property="string(state)" value="6">
                  <bean:message key='info.appleal.state6'/>
               </logic:equal>
               <logic:notEqual name="element" property="string(state)" value="0">
                 <logic:notEqual name="element" property="string(state)" value="1">
                   <logic:notEqual name="element" property="string(state)" value="2">
                      <logic:notEqual name="element" property="string(state)" value="3">
                      <logic:notEqual name="element" property="string(state)" value="4">
                      <logic:notEqual name="element" property="string(state)" value="5">
                      <logic:notEqual name="element" property="string(state)" value="6">
                        <bean:message key="info.appleal.state3"/>
                        </logic:notEqual>
                        </logic:notEqual>
                        </logic:notEqual>
                      </logic:notEqual>
                    </logic:notEqual>
                  </logic:notEqual>
               </logic:notEqual>
            </td>
             </logic:notEqual>
            </logic:equal>
            <%if(!(inputchinfor.equals("1")&&approveflag.equals("1"))) {%>
            <logic:greaterThan name="selfInfoForm" property="setprv" value="1">             
              <td align="center" class="RecordRow" nowrap>
               <logic:equal value="1" name="selfInfoForm" property="approveflag">
               <logic:notEqual value="1" name="selfInfoForm" property="inputchinfor"> 
                <logic:notEqual name="element" property="string(state)" value="">
                 <logic:notEqual name="element" property="string(state)" value="1">
                    <logic:notEqual name="element" property="string(state)" value="3">
                    <logic:notEqual name="element" property="string(state)" value="4">
                    <logic:notEqual name="element" property="string(state)" value="5">
                    <logic:notEqual name="element" property="string(state)" value="6">
            	       <img src="/images/edit.gif" border=0 onclick="cedit(${i9})">
	            	</logic:notEqual>
	            	</logic:notEqual>
	            	</logic:notEqual>
	            	</logic:notEqual>
	         	</logic:notEqual>
	          </logic:notEqual>
	        	<!-- yuxiaochun add programe -->   
	          <logic:equal name="element" property="string(state)" value="5">            	  
	            <img src="/images/edit.gif" border=0 onclick="cedit(${i9})">	            
	          </logic:equal>
	            
	          <logic:equal name="element" property="string(state)" value="3">
	        	<img src="/images/cards.bmp" border=0 onclick="capp(${i9})">
	          </logic:equal>
	          <logic:equal name="element" property="string(state)" value="">
            	 <img src="/images/cards.bmp" border=0 onclick="capp(${i9})">   
             </logic:equal>
            </logic:notEqual>
            <logic:equal value="1" name="selfInfoForm" property="inputchinfor"> 
				<img src="/images/edit.gif" border=0 onclick="cedit(${i9})">
			</logic:equal>
			</logic:equal>
			<logic:notEqual value="1" name="selfInfoForm" property="approveflag"> 
				<img src="/images/edit.gif" border=0 onclick="cedit(${i9})">
			</logic:notEqual>
	            <!-- yuxiaochun add programe -->   
	      </td>
	   
	      
	      
            </logic:greaterThan>
            <%}%>	      
	           <logic:iterate id="info"    name="selfInfoForm"  property="infoFieldList">            
               <logic:equal  name="info" property="itemtype" value="A">  
               	<logic:equal  name="info" property="codesetid" value="0">   
                	<td align="left" class="RecordRow" nowrap>  
                 		<bean:write  name="element" property="string(${info.itemid})" filter="true"/>
                	</td>
              	</logic:equal>
              	<logic:notEqual  name="info" property="codesetid" value="0">  
              		<bean:define id="viemvalue" name="element" property="string(${info.itemid})"/> 
                	<td align="left" class="RecordRow" nowrap>  
                 		${viemvalue}
                	</td>
              	</logic:notEqual>
              </logic:equal>
              <logic:equal  name="info" property="itemtype" value="D">               
                <td align="left" class="RecordRow" nowrap>  
                 <bean:write  name="element" property="string(${info.itemid})" filter="true"/>
                </td>   
              </logic:equal>   
              <logic:equal  name="info" property="itemtype" value="N">               
                <td align="right" class="RecordRow" nowrap>   
                  <bean:write  name="element" property="string(${info.itemid})" filter="true"/>
                </td>     
              </logic:equal>   
               <logic:equal  name="info" property="itemtype" value="M">  
               <%
                 FieldItem item=(FieldItem)pageContext.getAttribute("info");
                 String tx=vo.getString(item.getItemid());
               %> 
               <hrms:showitemmemo showtext="showtext" itemtype="M" setname="${selfInfoForm.setname}" tiptext="tiptext" text="<%=tx%>"></hrms:showitemmemo>                          
                <td align="left" ${tiptext} class="RecordRow" nowrap>   
                   ${showtext}
                </td>     
              </logic:equal>
             </logic:iterate>                 	    		        	        	        
          </tr>
        </hrms:extenditerate>
         
</table>
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		    <!--<logic:equal name="selfInfoForm" property="setprv" value="2">
		    <bean:message key='label.query.selectall'/><input type="checkbox" name="sfull" onclick="full();">
		    </logic:equal>-->
		    		<bean:message key="label.page.serial"/>
					<bean:write name="selfInfoForm" property="selfInfoForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="selfInfoForm" property="selfInfoForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="selfInfoForm" property="selfInfoForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="selfInfoForm" property="selfInfoForm.pagination"
				nameId="selfInfoForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<!-- tianye修改 根据主键审批状态（是否报批）是否显示添加修改和报批按钮 -->
<hrms:user_state name="sss" userid="<%=userView.getUserId()%>" dbname="<%=userView.getDbname()%>" tablename="A01"></hrms:user_state>
<SCRIPT language="javascript">
    var flag=selfInfoForm.sss.value;
    var str = "";
    if(<%=inputchinfor %>=='0'&&<%=approveflag %>=='0'){//不需要审批直接入库
		 	document.write(
			'<table  width="70%" align="left" border="0" cellspacing="0" cellpadding="0">'+
					'<tr><td height="5px"></td></tr>'+
			          '<tr>'+
			            '<td align="left">'+
			             '<html:hidden name="selfInfoForm" property="actiontype" value="new"/>'+
			            '  <logic:equal name="selfInfoForm" property="setprv" value="2">  '+
			               ' <%if(inputchinfor.equals("1")&&approveflag.equals("1")) {%>'+
			                	'<button name="sf" class="mybutton" onclick="appEdite(\'<bean:write name="selfInfoForm" property="setname"/>\')"><bean:message key="selfinfo.defend"/></button>'+
			                '<%} else {%>   '+       
				 	       		'<button name="apd" class="mybutton" onclick="bedit();"><bean:message key="button.insert"/></button>    '+ 
				 				'<button name="apdd" class="mybutton" onclick="cdel();"> <bean:message key="button.delete"/></button>'+
				 				'<logic:equal value="1" name="selfInfoForm" property="approveflag">'+
				 					'<hrms:priv func_id="01030104">'+
				    					'<logic:notEqual value="1" name="selfInfoForm" property="inputchinfor">'+
				    						'<button name="apd" class="mybutton" onclick="prove();"><bean:message key="button.appeal"/></button>'+
				    					'</logic:notEqual>'+
				    				'</hrms:priv>'+
				    			'</logic:equal>'+
				    			'<%} %>'+
				   ' </logic:equal>  '+
				    '<logic:notEqual  name="selfInfoForm" property="emp_cardId" value="-1">   '+            
			                    '<button name="apd" class="mybutton" onclick="ykcard();"><bean:message key='sys.res.card'/></button> '+    
			           ' </logic:notEqual>'+
			            '</td>'+
			          '</tr>  '+        
			 '</table>');
		 }else{
		    	if(<%=inputchinfor %>=='1'&&<%=approveflag %>=='1'){
		   		document.write(
					'<table  width="70%" align="left" border="0" cellspacing="0" cellpadding="0">'+
							'<tr><td height="5px"></td></tr>'+
					          '<tr>'+
					            '<td align="left">'+
					             '<html:hidden name="selfInfoForm" property="actiontype" value="new"/>'+
					            '  <logic:equal value="1" name="selfInfoForm" property="isAble"> '+
					            <logic:notEqual value="${selfInfoForm.setname }" name="selfInfoForm" property="virAxx"> 
					             '<button name="sf" class="mybutton" onclick="appEdite(\'<bean:write name="selfInfoForm" property="setname"/>\')"><bean:message key="selfinfo.defend"/></button>'+
					             </logic:notEqual> 
								   ' </logic:equal>  '+
					            '</td>'+
					          '</tr>  '+        
					 '</table>');
	}else{
		if((flag=='0'||flag=='2'||flag==''||flag=='5'||<%=inputchinfor%>=='1')){
		document.write(
			'<table  width="70%" align="left" border="0" cellspacing="0" cellpadding="0">'+
					'<tr><td height="5px"></td></tr>'+
			          '<tr>'+
			            '<td align="left">'+
			             '<html:hidden name="selfInfoForm" property="actiontype" value="new"/>'+
			            '  <logic:equal name="selfInfoForm" property="setprv" value="2">  '+
			               ' <%if(inputchinfor.equals("1")&&approveflag.equals("1")) {%>'+
			                	'<button name="sf" class="mybutton" onclick="appEdite(\'<bean:write name="selfInfoForm" property="setname"/>\')"><bean:message key="selfinfo.defend"/></button>'+
			                '<%} else {%>   '+       
				 	       		'<button name="apd" class="mybutton" onclick="bedit();"><bean:message key="button.insert"/></button>    '+ 
				 				'<button name="apdd" class="mybutton" onclick="cdel();"> <bean:message key="button.delete"/></button>'+
				 				'<logic:equal value="1" name="selfInfoForm" property="approveflag">'+
				 					'<hrms:priv func_id="01030104">'+
				    					'<logic:notEqual value="1" name="selfInfoForm" property="inputchinfor">'+
				    						'<button name="apd" class="mybutton" onclick="prove();"><bean:message key="button.appeal"/></button>'+
				    					'</logic:notEqual>'+
				    				'</hrms:priv>'+
				    			'</logic:equal>'+
				    			'<%} %>'+
				   ' </logic:equal>  '+
				    '<logic:notEqual  name="selfInfoForm" property="emp_cardId" value="-1">   '+            
			                    '<button name="apd" class="mybutton" onclick="ykcard();"><bean:message key='sys.res.card'/></button> '+    
			           ' </logic:notEqual>'+
			            '</td>'+
			          '</tr>  '+        
			 '</table>');
		}
		 
	}
    }
</SCRIPT>
<!-- tianye修改前 
<table  width="70%" align="left">
          <tr>
            <td align="left">
             <html:hidden name="selfInfoForm" property="actiontype" value="new"/>
             <logic:equal name="selfInfoForm" property="setprv" value="2"> 
                <%if(inputchinfor.equals("1")&&approveflag.equals("1")) {%>
                	
                	<button name="sf" class="mybutton" onclick="appEdite('<bean:write name="selfInfoForm" property="setname"/>');"><bean:message key="selfinfo.defend"/></button>
                	
                <%} else {%>          
	 	       		<button name="apd" class="mybutton" onclick="bedit();"><bean:message key="button.insert"/></button>     
              	
	 				<button name="apdd" class="mybutton" onclick="cdel();"> <bean:message key="button.delete"/></button>
	 				<logic:equal value="1" name="selfInfoForm" property="approveflag">
	 					<hrms:priv func_id="01030104">
	    					<logic:notEqual value="1" name="selfInfoForm" property="inputchinfor">
	    						<button name="apd" class="mybutton" onclick="prove();"><bean:message key="button.appeal"/></button>
	    					</logic:notEqual>
	    				</hrms:priv>
	    			</logic:equal>
	    			<%} %>
	    </logic:equal>  
	    <logic:notEqual  name="selfInfoForm" property="emp_cardId" value="-1">               
                    <button name="apd" class="mybutton" onclick="ykcard();"><bean:message key='sys.res.card'/></button>     
            </logic:notEqual>
	    
	       
            </td>
          </tr>          
 </table>-->
</html:form>
<%
	String a01001 = (String)request.getParameter("a0100");
	if(a01001==null){
%>
<logic:equal value="02" name="selfInfoForm" property="checksave">
<script language="javascript">
alert(LAST_INFOR_NOTUPDATE_APP+"!");
</script>
</logic:equal>
<%}%>
<script type="text/javascript">
function prove(){
if(confirm(APP_DATA_NOT_UPDATE+"?")){
selfInfoForm.action="/selfservice/selfinfo/searchselfdetailinfo.do?b_detailappeal=link";
selfInfoForm.submit();
}else{
return;
}
}
function backdel(fieldsetid){
if (confirm("您确定要撤销删除吗？"))
selfInfoForm.action="/selfservice/selfinfo/appEditselfinfo.do?b_search=backdel&setname="+fieldsetid+"&flag=infoself&isAppEdite=1";
selfInfoForm.submit();
}

function proveAll(fieldsetid){
if(confirm(APP_DATA_NOT_UPDATE+"?")){
selfInfoForm.action="/selfservice/selfinfo/appEditselfinfo.do?b_search=prove&setname="+fieldsetid+"&flag=infoself&isAppEdite=1";
selfInfoForm.submit();
}else{
return;
}
}
function cdel(){
if(confirm(CONFIRMATION_DEL)){
<%if(inputchinfor.equals("1")&&approveflag.equals("1")) {%>
selfInfoForm.action="/selfservice/selfinfo/searchselfdetailinfo.do?b_delete=link&isAppEdite=1";
<%}else{%>
selfInfoForm.action="/selfservice/selfinfo/searchselfdetailinfo.do?b_delete=link";
<%}%>
selfInfoForm.submit();
}else{
return;
}
}
function getcheckbox(){
	var tablevos=document.getElementsByTagName("INPUT");
		var flag=false;
      	for(var i=0;i<tablevos.length;i++)
      	{
      		if(tablevos[i].type=="checkbox"&&tablevos[i].value=="on")
      		{
      		  flag= true;
      		  return flag;
      		}
      	}
      	return flag;

}

  function capp(i9999){
  
   if(!confirm(APPLIC_OK+'？')){
   
  		return;
   }else{
   selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_apps=link&a0100=${selfInfoForm.a0100}&i9999="+i9999+"&actiontype=update";
   selfInfoForm.submit();
   
   }
  }

  function cedit(i9999){
  
  
   selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_edit=edit&a0100=${selfInfoForm.a0100}&i9999="+i9999+"&actiontype=update";
   selfInfoForm.submit();
   
  
  }
  function cadd(i9999){
  
  	selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_edit=edit&a0100=${selfInfoForm.a0100}&i9999="+i9999+"&actiontype=new&insert=1";
    selfInfoForm.submit();
  
  }

function full(){ 
  			for(var i=0;i<document.forms[0].elements.length;i++)
				{			
					if(document.forms[0].elements[i].type=='checkbox')
			   		{	
						document.forms[0].elements[i].checked =selfInfoForm.sfull.checked;
					}
				}
		}
function appEdite(fieldsetid) {
	selfInfoForm.action="/selfservice/selfinfo/appEditselfinfo.do?b_defendother=search&setname="+fieldsetid+"&flag=infoself&isAppEdite=1";
	selfInfoForm.target="mil_body";
    selfInfoForm.submit();
}
</script>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  