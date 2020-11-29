<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
  com.hjsj.hrms.actionform.sys.PrivForm form=(com.hjsj.hrms.actionform.sys.PrivForm)session.getAttribute("privForm");
  UserView userView=(UserView)session.getAttribute(WebConstant.userView);  
  String codeid=userView.getManagePrivCode();//"@K";// for h23公司
  String codevalue=userView.getManagePrivCodeValue();
  if(codeid.equals("@K")&&codevalue.equals(""))
     codevalue="ALL";
  if(userView.isSuper_admin()) 
  {
     codevalue="ALL";  
     codeid="@K";
  }
%>
<link rel="stylesheet" href="/css/tabpane.css" type="text/css">
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<script language="JavaScript">

       
	function setCurrent(tab)
	{
		var nodes,currnode;
		currnode=document.getElementById("current");
		if(currnode==null)
		   return;
		currnode.id="";
		nodes=tab.parent;
		if(nodes==null)
		   return;
		nodes.id="current";
		//nodes.setAttribute("id","current");
		
	}

      
      /*
      *树形菜单控制,功能菜单
      */
      function show(div_id)
      {
      	var oDiv;
      	oDiv=document.getElementById(div_id);
      	if(oDiv==null)
      	  return;
      	for(var i=0;i<oDiv.childNodes.length;i++)
      	{
      		if(oDiv.childNodes[i].tagName=='DIV')
      		{
      		   if(oDiv.childNodes[i].style.display=="none")
      		     oDiv.childNodes[i].style.display="block";
      		   else
      		     oDiv.childNodes[i].style.display="none";	  
      		}
      	}		
      }
      /*
      *递归找到子节点下的input=checkbox
      */
      function setchildvalue(oDiv,bflag)
      {
      	var childnode,ospan,childchild;
      	if(oDiv==null)
      	  return;
      	for(var i=0;i<oDiv.childNodes.length;i++)
      	{
      	  childnode=oDiv.childNodes[i];

      	  if(childnode.tagName=='DIV')
      	  {
		
 		for(var k=0;k<childnode.childNodes.length;k++)
 		{
            		if(childnode.childNodes[k].tagName=="SPAN")
            		{  
            		  ospan=childnode.childNodes[k];
            		  for(var j=0;j<ospan.childNodes.length;j++)
            		  {
            			if(ospan.childNodes[j].tagName=='INPUT')
            			{
            			  ospan.childNodes[j].checked=bflag;//false;		
            			}
            		  }
            		}
  		}
      	        setchildvalue(childnode,bflag);    		
      	  }
      	}
      }
      /*
      *递归找到父节点下的input=checkbox
      */
      function setparentvalue(oDiv)
      {
      	var parentnode,ospan;
      	if(oDiv==null)
      	  return;
      	parentnode=oDiv.parentElement;
      	if(parentnode==null)
      	   return;
      	for(var i=0;i<parentnode.childNodes.length;i++)
      	{
      		if(parentnode.childNodes[i].tagName=='SPAN')
      		{
  			ospan=parentnode.childNodes[i];
  			for(var j=0;j<ospan.childNodes.length;j++)
  			{
  				if(ospan.childNodes[j].tagName=='INPUT')
  				{
  				  ospan.childNodes[j].checked=true;		
  				}
  			}
      		}
      	}
      	setparentvalue(parentnode);      	
      }
      /*
      *选中不选中
      */
      function setvalue(div_id,input_id)
      {
      	var oDiv,oInput;
      	oDiv=document.getElementById(div_id);
      	if(oDiv==null)
      	  return;
      	oInput=document.getElementById(input_id);
      	if(oInput==null)
      	  return;
	if(oInput.checked)
	{
	   setparentvalue(oDiv);
	   setchildvalue(oDiv,true);　
	}
	else
	{
	　 setchildvalue(oDiv,false);　
	}     	
      }
            
      /**
      *组装表权限或字段权限串，最后通过document.privForm.field_set_str
      *隐藏域传到后台．
      */
      function combinePrivString()
      {
      	var tablevos,thecontent,tmp,tablename,tabname;
      	thecontent="";
      	tabname=document.privForm.current_tab.value;
      	if(tabname=="managepriv")
      	{	
      	   document.privForm.org.value=root.getSelected();
      	   //alert(root.getSelected());
      	   return ;
      	}
      	
      	if(!(tabname=="tablepriv"||tabname=="fieldpriv"))
      	  return;
      	tablevos=document.getElementsByTagName("INPUT");
      	for(var i=0;i<tablevos.length;i++)
      	{
      		if(tablevos[i].type=="radio")
      		{
      		  tmp=tablevos[i].value;
      		  tablename=tablevos[i].name;
      		  if(tmp=="0")
      		    continue;
      		  if(!tablevos[i].checked)  
      		    continue;
      		  tmp=tablename+tmp+",";
      		  thecontent=thecontent+tmp;
      		}
      	}
//     	if(thecontent.length==0)
//     	  return false;
      	thecontent=","+thecontent;
      	document.privForm.field_set_str.value=thecontent;
      	//alert(document.privForm.field_set_str.value);      	
      	return true;
      }
      
      /***全无,全写，全读0:无,=1全读,=2全写**/
      function allset(flag)
      {
      	var tablevos,tmp;
      	tablevos=document.getElementsByTagName("INPUT");
      	for(var i=0;i<tablevos.length;i++)
      	{
      		if(tablevos[i].type=="radio")
      		{
      		  tmp=tablevos[i].value;
      		  if(tmp==flag)
      		  {
      		    tablevos[i].checked=true;
      		  }
      		}
      	}      	
      }
 
      function allsetcheck(flag)
      {
      	var tablevos,tmp;
      	tablevos=document.getElementsByTagName("INPUT");
      	for(var i=0;i<tablevos.length;i++)
      	{
      		if(tablevos[i].type=="checkbox")
      		{
      		    
      		    if(flag==0)
      		      tablevos[i].checked=false;
      		    else
      		      tablevos[i].checked=true;
      		}
      	}      	
      } 
      /******高级授权**/
      function openpriv(flag,role_id)
      {
      	//document.privForm.b_save.fireEvent("onclick");
      	//combinePrivString();
        //privForm.action="/system/security/assignpriv?b_save=b_save";
        //privForm.submit();
      	//alert(document.privForm.org.value);
    	var target_url="/workbench/query/hquery_interface.do?b_query=link&a_query=3&a_flag="+flag+"&role_id="+role_id;
    	window.open(target_url,'_self'); 
      }
      
      /*资源分配*/
      function assign_res(flag,role_id)
      {
    	var target_url="/system/security/assign_resource.do?flag="+flag+"&roleid="+role_id;
    	window.open(target_url,'_self');        
      }     
         
      //组织机构树
      var m_sXMLFile="/system/get_code_tree.jsp?privflag=1&codesetid="+'<%=codeid%>'+"&codeitemid="+'<%=codevalue%>';	 //
      //alert(m_sXMLFile);
      Global.defaultInput=2;//1checkbox =2 radio
      <%
        if(codevalue.equals("ALL"))
        {
      %>
          Global.showroot=true;
      <%
        }
        else
        {
      %>      
          Global.showroot=false;      
      <%
        }
      %>
      var root=new xtreeItem("UN","组织机构","","","组织机构","/images/unit.gif",m_sXMLFile);
     		
</script>
<html:form action="/system/security/assignpriv">
  <!--保存计算过的需要递交的子集或指标内容 -->
  <html:hidden name="privForm" property="field_set_str"/>
  <html:hidden name="privForm" property="current_tab"/>
  <html:hidden name="privForm" property="org"/>  
  <br>
  <table width="80%"  border="0" cellpadding="1" cellspacing="1" class="framestyle">
   <tr>
     <td>
         <div id="header">
          <ul>
            <li><a href="/system/security/assignpriv.do?b_query=link&a_tab=funcpriv&a_flag=<bean:write name="privForm" property="user_flag" filter="true"/>&role_id=<bean:write name="privForm" property="role_id" filter="true"/>" onclick="setCurrent(this);"><bean:message key="menu.function"/></a></li>
            <li><a href="/system/security/assignpriv.do?b_query=link&a_tab=dbpriv&a_flag=<bean:write name="privForm" property="user_flag" filter="true"/>&role_id=<bean:write name="privForm" property="role_id" filter="true"/>" onclick="setCurrent(this);"><bean:message key="menu.base"/></a></li>
	    <logic:equal name="privForm" property="viewflag" value="0"> 	
            	<li><a href="/system/security/assignpriv.do?b_query=link&a_tab=managepriv&a_flag=<bean:write name="privForm" property="user_flag" filter="true"/>&role_id=<bean:write name="privForm" property="role_id" filter="true"/>" onclick="setCurrent(this);"><bean:message key="menu.manage"/></a></li>
	    </logic:equal>            
            <li><a href="/system/security/assignpriv.do?b_query=link&a_tab=tablepriv&a_flag=<bean:write name="privForm" property="user_flag" filter="true"/>&role_id=<bean:write name="privForm" property="role_id" filter="true"/>" onclick="setCurrent(this);"><bean:message key="menu.table"/></a></li>
            <li><a href="/system/security/assignpriv.do?b_query=link&a_tab=fieldpriv&a_flag=<bean:write name="privForm" property="user_flag" filter="true"/>&role_id=<bean:write name="privForm" property="role_id" filter="true"/>" onclick="setCurrent(this);"><bean:message key="menu.field"/></a></li>
            <li><a href="/system/security/assignpriv.do?b_query=link&a_tab=mediapriv&a_flag=<bean:write name="privForm" property="user_flag" filter="true"/>&role_id=<bean:write name="privForm" property="role_id" filter="true"/>" onclick="setCurrent(this);"><bean:message key="menu.media"/></a></li>
            <!--
            <li><a href="/system/security/assignpriv.do?b_query=link&a_tab=reportpriv&a_flag=<bean:write name="privForm" property="user_flag" filter="true"/>&a_roleid=<bean:write name="privForm" property="role_id" filter="true"/>" onclick="setCurrent(this);"><bean:message key="menu.report"/></a></li>
            <li><a href="/system/security/assignpriv.do?b_query=link&a_tab=rulepriv&a_roleid=<bean:write name="privForm" property="role_id" filter="true"/>" onclick="setCurrent(this);"><bean:message key="menu.rule"/></a></li>
            -->
          </ul>
        </div>
      </td>
      <tr>
      	<td>	
	 	<logic:equal name="privForm" property="current_tab" value="managepriv"> 
		 <div id="treemenu"></div> 	 	
      		 <script>
      		   //alert(document.privForm.org.value);
      		   Global.checkvalue=document.privForm.org.value;        		 
            	   root.setup(document.getElementById("treemenu"));
      		 </script>      			 	
	 	</logic:equal>
	 	<logic:notEqual name="privForm" property="current_tab" value="managepriv"> 
                	<bean:write  name="privForm" property="script_str" filter="false"/>	 		 	      	
	 	</logic:notEqual>      		
      	</td>
      </tr>

 </table> 
<table  width="80%" align="center">
          <tr>
            <td align="left">
     	     <hrms:priv func_id="080801"> 
	 	<logic:equal name="privForm" property="current_tab" value="managepriv"> 
         	    <hrms:submit styleClass="mybutton" property="b_clear">
            		<bean:message key="button.clearup"/>
	 	    </hrms:submit>  	 	
       	     	   <input type="button" name="priv"  value="<bean:message key="button.sys.cond"/>" class="mybutton" onclick="openpriv('<bean:write name="privForm" property="user_flag" filter="true"/>','<bean:write name="privForm" property="role_id" filter="true"/>')">             
	 	</logic:equal>
             </hrms:priv> 
	 	<logic:equal name="privForm" property="current_tab" value="tablepriv"> 
		   <input type="submit" name="b_save"  value="<bean:message key="button.all.clear"/>" class="mybutton" onclick="allset(0);combinePrivString();">
		   <input type="submit" name="b_save"  value="<bean:message key="button.all.read"/>" class="mybutton" onclick="allset(1);combinePrivString();">
		   <input type="submit" name="b_save"  value="<bean:message key="button.all.write"/>" class="mybutton" onclick="allset(2);combinePrivString();">
	 	</logic:equal>
	 	<logic:equal name="privForm" property="current_tab" value="fieldpriv"> 
		   <input type="submit" name="b_save"  value="<bean:message key="button.all.clear"/>" class="mybutton" onclick="allset(0);combinePrivString();">
		   <input type="submit" name="b_save"  value="<bean:message key="button.all.read"/>" class="mybutton" onclick="allset(1);combinePrivString();">
		   <input type="submit" name="b_save"  value="<bean:message key="button.all.write"/>" class="mybutton" onclick="allset(2);combinePrivString();">
	 	</logic:equal>	
	 	<logic:equal name="privForm" property="current_tab" value="funcpriv"> 
		   <input type="submit" name="b_save"  value="<bean:message key="button.all.clear"/>" class="mybutton" onclick="allsetcheck(0);combinePrivString();">
		   <input type="submit" name="b_save"  value="<bean:message key="label.query.selectall"/>" class="mybutton" onclick="allsetcheck(1);combinePrivString();">
	 	</logic:equal>	
        <hrms:submit styleClass="mybutton" property="b_save" onclick="combinePrivString();">
            		<bean:message key="button.save"/>
	 	</hrms:submit>
		<hrms:priv func_id="30034,0810">  	 	
    		<input type="button" name="b_resource"  value="<bean:message key="button.resource.assign"/>" class="mybutton" onclick="assign_res('<bean:write name="privForm" property="user_flag" filter="true"/>','<bean:write name="privForm" property="role_id" filter="true"/>');">             
        </hrms:priv>  	 	
	 	<logic:equal name="privForm" property="user_flag" value="1">
         	    <hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	    </hrms:submit>
	 	</logic:equal>
	 	<logic:equal name="privForm" property="user_flag" value="4">
         	    <hrms:submit styleClass="mybutton" property="br_return_user">
            		<bean:message key="button.return"/>
	 	    </hrms:submit>
	 	</logic:equal> 
	 	<!-- 
	 	<logic:equal name="privForm" property="user_flag" value="0">
         	    <hrms:submit styleClass="mybutton" property="br_return_login_user">
            		<bean:message key="button.return"/>
	 	    </hrms:submit>
	 	</logic:equal>
	 	 --> 	 	       
            </td>
          </tr>          
</table>
</html:form>
