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
	boolean version = false;
	if(userView.getVersion()>=50){//版本号大于等于50才显示这些功能
		version = true;
	}
%>

<script type="text/javascript">

	function add(uid,text,action,target,title,imgurl,xml){
		var currnode=Global.selectedItem;
		var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
		currnode.add(tmp);
		if(currnode.load){
			tmp.expand();
			Global.selectedItem = currnode;
		}
	}
	function addtrs(transfercodeitemidall,uid,text,action,target,title,imgurl,xml)
	{
		var currnode=Global.selectedItem;
		var root = currnode.root();
		var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
		a(root,transfercodeitemidall.toUpperCase());
		function a(root,name)
		{
			for(var z=0;z<=root.childNodes.length-1;z++){
				if(name==root.childNodes[z].uid){
					if(root.childNodes[z].load){
						root.childNodes[z].add(tmp);
						tmp.expand();
						Global.selectedItem = currnode;
					}
				}
				else
					a(root.childNodes[z],name);
			}
		}
	}
	
   function treeReload(orgId) {
       var currnode = root;
       if(currnode) {
           var itemNode = getTreeItem(orgId, currnode);
           if(itemNode) {
               itemNode.select(1);
           }
       }
   }
 //获取分类树上对应的节点
   function getTreeItem(itemid, currnode) {
       if(currnode) {
           if(itemid==currnode.uid) {
               return currnode;
           }
           
           var itemCode = currnode.uid.substring(2);
           var selectCode = itemid.substring(2,itemCode.length + 2);
           if(selectCode != itemCode && "root" != currnode.uid){
        	    return false;
           } 
           
           if(!currnode.load){
        	   currnode.expand();
           }

           var childNode;
           for (var i = 0; i < currnode.childNodes.length; i++) {
               childNode = currnode.childNodes[i];
               if(childNode.uid == "Loading..."){
                   childNode = undefined;
                   continue;
               }
               
               childNode = getTreeItem(itemid, childNode);
               if(childNode)
                   break;
           }
           
           if(childNode)
               return childNode;
       }
   }
</script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes></hrms:themes>
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<style>
<!--
	body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
	}
-->
</style>
<body>
<html:form action="/org/orginfo/searchorgtree" > 
   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >     
     
         <tr>
           <td align="left"> 
            <div id="treemenu"> 
             <SCRIPT LANGUAGE=javascript>                 
               <bean:write name="orgInformationForm" property="treeCode" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>           
    </table>
</html:form>
<script type="text/javascript">
var orgId = "";
var url = window.location.search.substring(1);
var params = url.split("&");
for (var i=0;i<params.length;i++) {
    var param = params[i].split("=");
    if(param[0] == "orgId"){
     	orgId = param[1];
        break;
    }
}

if(orgId){
	treeReload(orgId);
}
</script>
</body>