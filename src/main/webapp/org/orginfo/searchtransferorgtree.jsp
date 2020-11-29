<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
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
   <style type="text/css">
	#treemenu {  
	height: 300px;overflow: 
	auto;border-style:inset ;
	border-width:2px
	}
   </style>  
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 

 <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<script language="JavaScript">
   	
   	function savecode()
   	{
       	   var currnode,codeitemid;
    	   currnode=Global.selectedItem;	
    	   if(currnode==null)
    	    	return;  
    	   if(currnode.uid=='root'){
    	   		alert("根节点不是物理的组织单元，不能划转到根节点！");  
    	   		return;
    	   	}
    	   codeitemid=currnode.uid;
    	   /*输入相关代码类和选择的相关代码一致*/      	 
       	   targetobj.value=currnode.text
       	   //alert(codeitemid.substring(0,2));
    	   targethidden.value=codeitemid.substring(2);
    	   //alert(root.getSelected());	  
    	  // window.close();	
    	   winclose();
   	}
   	//关闭ext弹窗方法   wangb 20190306
   	function winclose(){
   		var win = parent.Ext.getCmp('chooseorgtree');
   		if(win)
   			win.close();
   	}
   </SCRIPT>
<hrms:themes></hrms:themes>
<html:form action="/org/orginfo/searchtarorgtree"> 
<div class="fixedDiv3">
   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >          
         <tr>
           <td align="left">         
            <div id="treemenu" style="height: 300px;overflow: auto;border-style:solid ;border-width:1px;width:expression(document.body.clientWidth-14);" class="complex_border_color"> 
             <SCRIPT LANGUAGE=javascript>     
                var codesetid,codevalue,name;
               // var paraArray=dialogArguments;
               var paraArray = parent.Ext.getCmp('chooseorgtree').theArr; //改为获取ext弹出属性 wangb 20190306
               var targetobj,targethidden;	
              //显示代码描述的对象
               targetobj=paraArray[0];
               
               //代码值对象
              targethidden=paraArray[1];   
              input_code_id=codesetid;            
               <bean:write name="orgInformationForm" property="tarTreeCode" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>  
            <tr>
          <td align="center"  nowrap height="35px;">
               <input type="button" name="btncance2" value="<bean:message key="button.ok"/>" class="mybutton" onclick="return savecode();"> 
	       <input type="button" name="btncancel" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="winclose();">  
          </td>
          </tr>         
    </table>
</div>
</html:form>

