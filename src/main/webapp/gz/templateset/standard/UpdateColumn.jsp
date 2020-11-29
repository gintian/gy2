<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.gz.templateset.standard.SalaryStandardForm" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>    
<html>
  <head>
 
  </head>
  <script language='javascript' >
  	<%
  	SalaryStandardForm salaryStandardForm=(SalaryStandardForm)session.getAttribute("salaryStandardForm");
  	String optType=salaryStandardForm.getOptType();
  	ArrayList parentItemList=salaryStandardForm.getParentItemList();
  	
  	if(request.getParameter("type").equals("0")||request.getParameter("type").equals("2")||request.getParameter("type").equals("1")||request.getParameter("type").equals("3"))//无论哪种情况确定完都关闭窗口，bug0041275 zhaoxg add 2013-12-31
  	{
  		if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("finish"))
  		{
  			%>
  			window.close();
  			<% 
  		}
  	}
  	
  	%>
  
	function changeItem()
	{
		document.salaryStandardForm.action="/gz/templateset/standard.do?b_updateColumn=update&type=${salaryStandardForm.optType}&parentItem="+document.salaryStandardForm.parentItemId.value;
		document.salaryStandardForm.submit();
	}

	
	
	


  </script>
  <style>
	div#treemenu {
	BORDER-BOTTOM:#94B6E6 1pt inset; 
	BORDER-COLLAPSE: collapse;
	BORDER-LEFT: #94B6E6 1pt inset; 
	BORDER-RIGHT: #94B6E6 1pt inset; 
	BORDER-TOP: #94B6E6 1pt inset; 
	width: 250px;
	height: 258px;
	overflow: auto;
	}

  </style>	
  <hrms:themes />
  <body>
  <html:form action="/gz/templateset/standard">
  <table width='570px' style="margin-left:-5px;margin-top:-3px;"><tr><td><!-- modify by xiaoyun 进行【增减子横栏】、【增减子纵栏】时，界面问题 2014-10-16 -->
   	<table width='100%' align='center' >
   	<logic:equal  name="salaryStandardForm" property="optType" value="0" >
   		<tr><td colspan='3'  >  &nbsp; </td></tr>
   	</logic:equal>
   	<logic:equal  name="salaryStandardForm" property="optType" value="2" >
   		<tr><td colspan='3'  >  &nbsp; </td></tr>
   	</logic:equal>
   	<logic:equal  name="salaryStandardForm" property="optType" value="1" >
   		<tr><td colspan='3'  > 
   		<html:select name="salaryStandardForm" property="parentItemId" onchange='changeItem()' size='1' style="width:250;" >
                              <html:optionsCollection property="parentItemList" value="dataValue" label="dataName"/>
          </html:select> 
   		</td></tr>
   	</logic:equal>
   	<logic:equal  name="salaryStandardForm" property="optType" value="3" >
   		<tr><td colspan='3'  > 
   		<html:select name="salaryStandardForm" property="parentItemId" onchange='changeItem()' size='1' style="width:250;" >
                              <html:optionsCollection property="parentItemList" value="dataValue" label="dataName"/>
          </html:select> 
   		</td></tr>
   	</logic:equal>
   		
   		<tr>
   		<td>
   				<div id="treemenu"></div>
   		</td>
   		<td  >
   			
   			    <html:button  styleClass="mybutton" property="b_addfield" onclick="add()">
            		    &nbsp;<bean:message key="button.setfield.addfield"/>&nbsp; 
	            </html:button >
	            <br>
	            <br>
	            <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('columnsItemValue');">
            		     &nbsp;<bean:message key="button.setfield.delfield"/>&nbsp;    
	            </html:button >	
   			
   		</td>
   		<td>
   		<html:select name="salaryStandardForm" property="columnsItemValue"   multiple="multiple" size="10"  ondblclick="removeitem('columnsItemValue');" style="height:258px;width:240;font-size:9pt" >
                              <html:optionsCollection property="selectItemList" value="dataValue" label="dataName"/>
          </html:select> 
   		    
   		</td>
   		
   		
   		</tr>		
   		<tr><td colspan='3' align='center' >  
   		 &nbsp;<input type="button" class="mybutton" value=" <bean:message key="lable.tz_template.enter"/> " onclick="enter()" />&nbsp;
   		  <input type="button" class="mybutton" value=" <bean:message key="lable.welcomeboard.close"/> " onclick="javascript:window.returnValue='1';window.close()" />
   		<!--  <logic:equal  name="salaryStandardForm" property="optType" value="1" > 
             <input type="button" class="mybutton" value=" <bean:message key="lable.welcomeboard.close"/> " onclick="javascript:window.returnValue='1';window.close()" />
			  </logic:equal> --> 
   		<!--<logic:equal  name="salaryStandardForm" property="optType" value="3" >
             <input type="button" class="mybutton" value=" <bean:message key="lable.welcomeboard.close"/> " onclick="javascript:window.returnValue='1';window.close()" />
         </logic:equal>  -->
   		</td></tr>
    </table> 
      </td></tr></table>       
    
<SCRIPT LANGUAGE=javascript>
	var focus_obj_node;

	
	var m_sXMLFile	= "/gz/templateset/standard/select_item_tree.jsp?flag=${salaryStandardForm.flag}&id=${salaryStandardForm.id}&type=${salaryStandardForm.type}";		
	var newwindow;
	var root=new xtreeItem("root","${salaryStandardForm.desc}","","mil_body","${salaryStandardForm.desc}","/images/add_all.gif",m_sXMLFile);
	Global.defaultInput=1;
	Global.showroot=false;
	unsetselectitem('columnsItemValue');//显示新增子横纵栏目，横纵栏目的时候不用默认选中
	
	root.setup(document.getElementById("treemenu"));	
	if(newwindow!=null)
	{
		newwindow.focus();
	}
	if(parent.parent.myNewBody!=null)
	{
		parent.parent.myNewBody.cols="*,0"
	}
	<% if((optType.equals("1")||optType.equals("3"))&&parentItemList.size()==0){ %>
	document.getElementById("parentItemId").style.display='none';
	<%} %>
	
	function add()
	{
		
		var selectIds=root.getSelected().split(",");
		var selectTexts=root.getSelectedTitle().split(",");
		for(var j=0;j<selectIds.length;j++)
		{
			var id=selectIds[j];
			if(id.length>0)
			{
				id=id.split("#")[1];
				var isItem=false;
			
				for(var i=0;i<document.salaryStandardForm.columnsItemValue.options.length;i++)
				{				
					if(document.salaryStandardForm.columnsItemValue.options[i].value==id)
					{
						isItem=true;
						break;
					}
				}
				if(!isItem)
				{	
					document.salaryStandardForm.columnsItemValue.options[document.salaryStandardForm.columnsItemValue.options.length]=new Option(selectTexts[j],selectIds[j].split("#")[1])
				}
			}
		}
		
	}
	
	
	var oldValues=new Array();
	for(var i=0;i<document.salaryStandardForm.columnsItemValue.options.length;i++)
	{
		oldValues[i]=document.salaryStandardForm.columnsItemValue.options[i].value;
	}
	
	
	function enter()
	{

		<logic:equal  name="salaryStandardForm" property="optType" value="1" >
		if(document.salaryStandardForm.columnsItemValue.options.length==0)
			document.salaryStandardForm.columnsItemValue.options[document.salaryStandardForm.columnsItemValue.options.length]=new Option("#","#")
	 	</logic:equal>
	 	<logic:equal  name="salaryStandardForm" property="optType" value="3" >
		if(document.salaryStandardForm.columnsItemValue.options.length==0)
			document.salaryStandardForm.columnsItemValue.options[document.salaryStandardForm.columnsItemValue.options.length]=new Option("#","#")
	 	</logic:equal>
	 	
	 	<logic:equal  name="salaryStandardForm" property="optType" value="0" >
		if(document.salaryStandardForm.columnsItemValue.options.length==0)
			return;	
	 	</logic:equal>
	 	<logic:equal  name="salaryStandardForm" property="optType" value="2" >
		if(document.salaryStandardForm.columnsItemValue.options.length==0)
			return;
	 	</logic:equal>

	    setselectitem('columnsItemValue');
		document.salaryStandardForm.action="/gz/templateset/standard.do?b_resetColumn=update&opt=finish&type=${salaryStandardForm.optType}";
		document.salaryStandardForm.submit();
	}
		
</SCRIPT>

	</html:form>
  </body>
</html>
