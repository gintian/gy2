<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hjsj.hrms.actionform.performance.objectiveManage.setUnderlingObjective.SetUnderlingObjectiveForm" %>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
	
	
	SetUnderlingObjectiveForm setUnderlingObjectiveForm = (SetUnderlingObjectiveForm)session.getAttribute("setUnderlingObjectiveForm");
	String plan_id=setUnderlingObjectiveForm.getPlan_id();
	if(plan_id==null)
		plan_id="";
%>
<html>
<head>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes />
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" ></link>
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
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
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
	var plan_id='<%=plan_id%>';
</script>
</head>
<body>
<script type="text/javascript">
function initTreeNode()
{
  initDocument();
  if(plan_id=='')
  {
	  var obj=root.childNodes[0];
	  if(obj)
	  {
	    obj.expand();
	    var objfirst=obj.childNodes[0];
	    selectedClass("treeItem-text-"+objfirst.id);
	    setUnderlingObjectiveForm.action="/performance/objectiveManage/setUnderlingObjective/underling_objective_view_list.do?b_view=init&posid=-1&a0100=-1&plan_id="+objfirst.uid+"&opt=1";
		 setUnderlingObjectiveForm.target="mil_body";
         setUnderlingObjectiveForm.submit();
	   }
   }
   else
   {
   	  var n=0;	
   	  var objfirst;
   	  for(var i=0;i<root.childNodes.length;i++)
   	  {
   			var obj=root.childNodes[i];
   	  		if(obj)
			{
	   			 obj.expand();
	   			 for(var e=0;e<obj.childNodes.length;e++)
		    	 {
		    		if(obj.childNodes[e].uid==plan_id)
		    		{
		    			objfirst=obj.childNodes[e];
		    			n++;
		    			break;	
		    		}
		    	 }
	   			 
	   		}
   	  		if(n>0)
   	  			break;
   	  }
   	  if(n==0)
      {
        var obj=root.childNodes[0];
        if(obj)
	    {
		    objfirst=obj.childNodes[0];
		}
   		
   	  }
   	  if(objfirst)
   	  {
     	 selectedClass("treeItem-text-"+objfirst.id);
     	 setUnderlingObjectiveForm.action="/performance/objectiveManage/setUnderlingObjective/underling_objective_view_list.do?b_view=init&posid=-1&a0100=-1&plan_id="+objfirst.uid+"&opt=1";
   	     setUnderlingObjectiveForm.target="mil_body";
         setUnderlingObjectiveForm.submit();
   	  
   	  }
   }
   
 

}

</script>
<html:form action="/performance/objectiveManage/setUnderlingObjective/underling_objective_tree">

 <table align="left" class="mainbackground">
		 	            
         <tr>
           <td align="left"> 
            <div id="treemenu"> 
             <SCRIPT LANGUAGE="javascript">                 
               <bean:write name="setUnderlingObjectiveForm" property="tree" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>           
    </table>  
</html:form>
<script language="javascript">
 initTreeNode();
</script>
</body>
</html>