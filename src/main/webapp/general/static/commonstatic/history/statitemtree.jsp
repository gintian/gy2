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
<body onclick="" style="margin:0;">
<html:form action="/general/static/commonstatic/history/statshow"> 
<SCRIPT LANGUAGE=javascript src="/ajax/basic.js"></SCRIPT> 
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
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
</script>
<script type="text/javascript">
function addparent(uid,text,action,target,title,imgurl,xml){
		var currnode=Global.selectedItem;
		var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
		if(currnode.load){
		    currnode.add(tmp);		
			Global.selectedItem = currnode;
		}else
		  currnode.expand();
	}
function add(uid,text,action,target,title,imgurl,xml){

		var currnode=Global.selectedItem;
		var parentnode=currnode.parent;		
		var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
			
		if(parentnode.load){
		    parentnode.add(tmp);
			Global.selectedItem = tmp;
		}else
		  parentnode.expand();
	}
function addName(uid,text,action,target,title,imgurl,xml){
		var currnode=Global.selectedItem;
		var parentnode=currnode.root();	
		if(parentnode.load){
			 var tmp = new xtreeItem(uid,text,action,"mmil_body",text,imgurl,xml);
	 	        parentnode.add(tmp);
		}		
	}
function addNameCategories(uid,text,action,target,title,imgurl,xml,categories){
		var currnode=Global.selectedItem;
		var parentnode=currnode.root();	
		var ishave=false;
		for(var i=0;i<=parentnode.childNodes.length-1;i++){
			if(categories.toUpperCase()==parentnode.childNodes[i].uid.toUpperCase()){
				ishave=true;
				parentnode=parentnode.childNodes[i];
				break;
			}
		}
		if(ishave){
			if(parentnode.load){
				 var tmp = new xtreeItem(uid,text,action,"mmil_body",text,imgurl,xml);
		 	        parentnode.add(tmp);
			}else
				parentnode.expand();
		}else{
			refresh();
			for(var i=0;i<=parentnode.childNodes.length-1;i++){
					if(categories.toUpperCase()==parentnode.childNodes[i].uid.toUpperCase()){
						parentnode.childNodes[i].expand();
						break;
					}
				}
		}		
	}
	
	function refresh(){
		var currnode=Global.selectedItem;
		if(currnode==null)
			return;
		//alert(currnode.uid);
		currnode=currnode.root();
		if(currnode.load)
		while(currnode.childNodes.length){
			//alert(currnode.childNodes[0].uid);
			currnode.childNodes[0].remove();
		}
		currnode.load=true;
		currnode.loadChildren();
		currnode.reload(1);
	}
	
	
</script>
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<hrms:themes />
    <table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" style="table-layout:fixed;">
       <tr>
           <td align="left" valign="top"> <!-- 【7780】员工管理/查询浏览/历史时点，统计分析，统计项共23项，界面上显示不全了，也没有出现上下滚动条，不对。jingq add 2015.03.05 -->
            <div id="treemenu" style="border:none;overflow:auto;" ondragend="dragend('hr_hisdata_sname','id','categories','11080204059');"> 
             <SCRIPT LANGUAGE=javascript>  
             <hrms:priv func_id="26012304"> 
               	setDrag(true); 
             </hrms:priv>	
               <bean:write name="historyStatForm" property="stattreeCode" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>           
    </table>
</html:form>
</body>

<script><%--浏览器兼容样式问题修改 bug 50988 wangb 2019-07-26 --%>
var treemenu = document.getElementById('treemenu');
var table = document.getElementsByName("historyStatForm")[0].getElementsByTagName('table')[0];
if(getBrowseVersion()){
	table.style.width = document.body.clientWidth;
	treemenu.style.width = document.body.clientWidth;
	treemenu.style.height = document.body.clientHeight-42+'px';
	window.onresize=function(){
		table.style.width = document.body.clientWidth;
		treemenu.style.width = document.body.clientWidth;
		treemenu.style.height = document.body.clientHeight-42+'px';
	}
}else{
	treemenu.style.height = document.body.clientHeight-38+'px';
}
</script>