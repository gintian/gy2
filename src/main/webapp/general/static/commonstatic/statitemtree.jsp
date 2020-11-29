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
			 var tmp = new xtreeItem(uid,text,action,"mil_body",text,imgurl,xml);
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
				 var tmp = new xtreeItem(uid,text,action,"mil_body",text,imgurl,xml);
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
<body style="margin:0px;padding:0px;overflow:auto;height:100%">
<html:form action="/general/static/commonstatic/statshow"> 
    <table border="0" cellspacing="1"  align="left" cellpadding="1" >
       <tr>
           <td align="left"> 
            <div id="treemenu" ondragend="dragend('sname','id','categories','11080204059');" style="margin:0px; padding:0px;width:100%;"> 
             <SCRIPT LANGUAGE=javascript>  
             <hrms:priv func_id="231704,2602304,04010104,2311034"> 
               	setDrag(true); 
             </hrms:priv>	
               <bean:write name="statForm" property="stattreeCode" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>           
    </table>
</html:form>
</body>
<script>
if(!getBrowseVersion()){//非ie浏览器样式修改  wangb  20190614 bug 48891
	parent.document.getElementsByName('mil_menu')[0].style.marginTop='5px';
}
</script>
