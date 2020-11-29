
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%

String username = request.getParameter("user_name");

%>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<script type="text/javascript">
	//系统管理—用户授权—给用户重新关联人员后，节点title还是原先的。  jingq add 2014.10.27
	var can;
	function link_success(outparamters)
	{
		var userinfo=outparamters.getValue("userinfo");
			AjaxBind.bind($('nbase'),userinfo.nbase);
			AjaxBind.bind($('b0110'),userinfo.b0110);
			AjaxBind.bind($('e0122'),userinfo.e0122);
			AjaxBind.bind($('e01a1'),userinfo.e01a1);
			AjaxBind.bind($('name'),userinfo.name);
			can = userinfo.name;
			//top.returnValue = can;
	}
	
	function getSelectedEmploy()
	{
	 /*operuser中用户名*/
	 var oper_id='<%=username%>';//window.dialogArguments;
	 
	 
	 if(window.showModalDialog){
		 var return_vo=select_org_emp_dialog(1,2,1); 
		 if(return_vo)
		 {

		     var hashvo=new ParameterSet();
		     hashvo.setValue("a0100",return_vo.content);
		     hashvo.setValue("username",oper_id);
	        
		   　 var request=new Request({asynchronous:false,onSuccess:link_success,functionId:'1010010043'},hashvo);        
		 }
     }else{
    	 var theurl="/system/logonuser/org_employ_tree.do?flag=1`selecttype=2`dbtype=1`priv=1";
    	 var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    	 var dw=500,dh=400,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
    	 window.open(iframe_url,'','width='+dw+',height='+dh+',left='+dl+',top='+dt+',resizable=no,titlebar=yes,location=no,status=no,scrollbars=no');
    	 window.type='orgEmp';
    	 window.openEmpHistoryReturn = function(return_vo){
    		 if(return_vo)
    		 {
    		     var hashvo=new ParameterSet();
    		     hashvo.setValue("a0100",return_vo.content);
    		     hashvo.setValue("username",oper_id);
    	        
    		   　 var request=new Request({asynchronous:false,onSuccess:link_success,functionId:'1010010043'},hashvo);        
    		 }
    		 
    	 };
     }
	 /*
     var theurl="/system/logonuser/org_employ_tree.do?flag=1&selecttype=1";
     var return_vo= window.showModalDialog(theurl,0, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:yes");*/
     //var return_vo=select_org_emp_dialog(1,2,1); 
        
        
	 
	}
	
	function cancellink_success(outparamters)
	{
		if(window.showModalDialog){
			top.returnValue = false;
		}else{
			top.opener.linkemploy_success(false);
		}
		top.close();
	}
		
	function cancel_link()
	{
	 	var oper_id= '<%=username%>';   //window.dialogArguments;	
	    var hashvo=new ParameterSet();	 
	    hashvo.setValue("username",oper_id);	    		
      　 var request=new Request({asynchronous:false,onSuccess:cancellink_success,functionId:'1010010045'},hashvo);        
	}
	
	function wclose(){
		if(window.showModalDialog){
			top.returnValue = can;
		}else{
			top.opener.linkemploy_success(can);
		}
		//top.returnValue = can;
		top.close();
	}
</script>

<html:form action="/system/logonuser/link_employ">
      <table width="440" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
          <tr height="20">

       		<td align="left" class="TableRow" colspan="2"><bean:message key="label.link.employ"/>&nbsp;</td>
           	      
          </tr> 
                      <tr class="list3">
                	      <td align="right" nowrap width="40%"><bean:message key="menu.base"/></td>
                	      <td align="left"  class="Btb" id="nbase" nowrap >
          					<hrms:codetoname codeid="@@" name="logonUserForm" codevalue="userinfo.nbase" codeitem="codeitem" scope="session"/>  	      
          					<bean:write name="codeitem" property="codename" />&nbsp;                	      
                          </td>
                      </tr>
                      <tr class="list3">
                	      <td align="right" nowrap ><bean:message key="column.sys.org"/></td>
                	      <td align="left" class="Btb" id="b0110" nowrap >
          					<hrms:codetoname codeid="UN" name="logonUserForm" codevalue="userinfo.b0110" codeitem="codeitem" scope="session"/>  	      
          					<bean:write name="codeitem" property="codename" />&nbsp; 
          				  </td>
                      </tr>   
                      <tr class="list3">
                	      <td align="right" nowrap ><bean:message key="column.sys.dept"/></td>
                	      <td align="left" class="Btb"  id="e0122" nowrap >
          					<hrms:codetoname codeid="UM" name="logonUserForm" codevalue="userinfo.e0122" codeitem="codeitem" scope="session"/>  	      
          					<bean:write name="codeitem" property="codename" />&nbsp; 
                          </td>
                      </tr>                                           
                      <tr class="list3">
                	      <td align="right" nowrap ><bean:message key="column.sys.pos"/></td>
                	      <td align="left" class="Btb" id="e01a1" nowrap >
          					<hrms:codetoname codeid="@K" name="logonUserForm" codevalue="userinfo.e01a1" codeitem="codeitem" scope="session"/>  	      
          					<bean:write name="codeitem" property="codename" />&nbsp; 
                          </td>
                      </tr>
                      <tr class="list3">
                	      <td align="right" nowrap ><bean:message key="label.title.name"/></td>
                	      <td align="left" class="Btb" id="name" nowrap>
          					<bean:write name="logonUserForm" property="userinfo.name" />&nbsp; 
                          </td>
                      </tr>                      
                                           
                                                     
          <tr class="list3">
            <td align="center" colspan="2" style="height:35px">
         	<html:button styleClass="mybutton" property="b_save" onclick="getSelectedEmploy();">
            		<bean:message key="lable.relink"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="b_cancel" onclick="cancel_link();">
            		<bean:message key="label.cancel.link"/>
	 	    </html:button>	 	    
         	<html:button styleClass="mybutton" property="br_return" onclick="wclose();">
            		<bean:message key="button.close"/>
	 	    </html:button>            
            </td>
          </tr>          
      </table>
</html:form>