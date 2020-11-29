<%@ page contentType="text/html; charset=UTF-8" language="java"%>
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
<script language="javascript" src="/js/validate.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes/>
<html:form action="/general/inform/org/map/searchorgmap"> 
<html:hidden name="orgMapForm" property="constant" /> 
	<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >
       <tr>
           <td align="left">
           <div id="postree" ></div>
   				<SCRIPT LANGUAGE=javascript>
	             var m_sXMLFile="/pos/posreport/pos_report_relations_tree.jsp?position=first&yfiles=1&sep="+escape('${orgMapForm.seprartor}');	                          
	             var root=new xtreeItem("root","汇报关系","/pos/posreport/show_relations_map.do?code=","mil_body","汇报关系","/images/pos_l.gif",m_sXMLFile);
	             root.setup(document.getElementById("postree"));	     
   				</SCRIPT>    
           </td>
           </tr>           
    </table>
</html:form>

<script>

var reloadfalg = false;
    function reloadTree(){
    	window.location.reload();
    }

    function getExpandPath(){
    	var selectObj = Global.selectedItem;
    	var objId = selectObj.uid;
    	var codeArray = new Array();
    	
    	while(objId!="root"){
    		codeArray.push(objId);
    		selectObj = selectObj.parent;
    		objId = selectObj.uid;
    	}
    	
    	codeArray.push(objId);
    	return codeArray;
    }
    
    
    function expandTree(pathArray){
    	var selectObj = Global.selectedItem;
    	var childList = selectObj.childNodes;
    	for(var i=pathArray.length-1;i>=0;i--){
    		if(pathArray[i] == "root")
    			continue;
    		for(var k = 0;k<childList.length;k++){
    			var obj = childList[k];
    			if(pathArray[i] == obj.uid){
    				
    				if(i > 0){
    					obj.expand();
    					childList = obj.childNodes;
    				}else{
    					obj.select(1);
    				}
    			   break;		
    			}
    		}
    	}
    }
    
    function selectTree(){
    	
    }
	if(!getBrowseVersion() || getBrowseVersion() == 10){
		var postree = document.getElementById('postree');
		postree.style.whiteSpace = 'nowrap';
	}    
</script>