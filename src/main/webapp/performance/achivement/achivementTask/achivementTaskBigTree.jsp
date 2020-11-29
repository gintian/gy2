<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="java.util.*,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 javax.servlet.http.HttpSession,
                 com.hjsj.hrms.actionform.performance.achivement.AchievementTaskForm,
                 org.apache.commons.beanutils.LazyDynaBean" %>
<%
	String css_url="/css/css1.css";		
	String codeitemdesc = "组织机构";
	String action = "/performance/achivement/achivementTask.do?b_query=link";
//	UserView userView = (UserView) session.getAttribute(WebConstant.userView);

	
	AchievementTaskForm achievementTaskForm=(AchievementTaskForm)session.getAttribute("achievementTaskForm");
	String object_type = achievementTaskForm.getObj_type();
	String target_id = achievementTaskForm.getTarget_id();	
	
	String hjsoft = achievementTaskForm.getHjsoft();
	ArrayList orgLinks = (ArrayList)achievementTaskForm.getOrgLinks();	
	
	String jgroot="";
	for(int i=0;i<orgLinks.size();i++)
	{
		jgroot=(String)orgLinks.get(i);
	}

	String codevalue = "-1";
	
%>

<HTML>
	<HEAD>
		<TITLE>
		</TITLE>

		<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
		<hrms:themes />
		<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
		<script LANGUAGE=javascript src="/performance/achivement/achivementTask/achievement.js"></script> 
		<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
		<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
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
	    
		<SCRIPT LANGUAGE=javascript>
    	var _checkBrowser=true;
		var _disableSystemContextMenu=false;
		var _processEnterAsTab=true;
		var _showDialogOnLoadingData=true;
		var _enableClientDebug=true;
		var _theme_root="/ajax/images";
		var _application_root="";
		var __viewInstanceId="968";
		var ViewProperties=new ParameterSet();		
		</SCRIPT>    
	</HEAD>
	
	<body topmargin="10" leftmargin="5" marginheight="0" marginwidth="0">
			
		<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
			<tr>  
				<td valign="top">
					<div id="treemenu"></div>
				</td>		
				 
			</tr>
		</table>	
	
		<script language='javascript' >
//			bridgeParam();
			
			var m_sXMLFile	= "/performance/achivement/achivementTask/achivement_taskBigtree.jsp?init=1&codeid=<%=codevalue%>&object_type=<%=object_type%>&target_id=<%=target_id%>";		
			var newwindow;
			var root=new xtreeItem("root","<%=codeitemdesc%>","<%=action%>","mil_bodyto","<%=codeitemdesc%>","/images/root.gif",m_sXMLFile);
//			Global.defaultInput=0;
			root.setup(document.getElementById("treemenu"));	
//			root.expandAll();
			if(newwindow!=null)
			{
				newwindow.focus();
			}
			if(parent.parent.myNewBody!=null)
			{
				parent.parent.myNewBody.cols="*,0"
			}
			
			<%
			String hjsofts=hjsoft;
			if(hjsofts.equalsIgnoreCase("hjsj"))
			{%>
				findCheck();
			<%}else{%>
				initTreeNode();
			<%}%>
			
			function findCheck()
			{								
				var object_type=<%=object_type%>;
				
	 	 		var findNode = false;
	 	 		var orgLink = '<%=jgroot%>';	 	 			 	 		
				var temps=orgLink.split("/");					
				var obj=root;
				for(var i=temps.length-1;i>=0;i--)
				{
					obj.expand();												
					for(var j=0;j<obj.childNodes.length;j++)
					{
						if(obj.childNodes[j].text==temps[i])
						{
							obj=obj.childNodes[j];
							selectedClass("treeItem-text-"+obj.id);
							findNode = true;
							break;
						}					
					}
				}
				obj.expand();								
			}	
					
			function initTreeNode()
			{
				var object_type=<%=object_type%>;				
	  			var obj=root.childNodes[0];
	 	 		if(obj)
	  			{	   
	    			obj.expand();
	    			var objfirst=obj.childNodes[0];	    			
	    			if(object_type==1)
	    			{	    			
		    			if(objfirst)
		  				{
		  					objfirst.expand();
		    				var objsecond=objfirst.childNodes[0];
			    			selectedClass("treeItem-text-"+objsecond.id);
			    			objsecond.expand();
						}
					}else{
						if(objfirst)
		  				{
		  					objfirst.expand();
		  					var objsecond=objfirst.childNodes[0];
		  					if(objsecond)
		  					{
			  					objsecond.expand();
			    				var objthird=objsecond.childNodes[0];
				    			selectedClass("treeItem-text-"+objthird.id);
				    			objthird.expand();
				    		}
						}
					}
	   			}
			}
		</script>

	</body>
</HTML>
	