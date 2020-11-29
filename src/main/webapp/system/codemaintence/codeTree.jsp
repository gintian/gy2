<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
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
<!--
	function add3(uid,text,action,target,title,imgurl,xml)
	{
		var currnode=Global.selectedItem;
		//imgurl="/images/close.png";
		var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
		currnode.add(tmp);
		
	}
	function add2(uid,text,action,target,title,imgurl,xml)
	{
		var currnode=Global.selectedItem;
		//imgurl="/images/close.png";
		var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
		currnode.add(tmp);
		tmp.expand();
	}
	function add(uid,text,action,target,title,imgurl,xml)
	{
		var currnode=Global.selectedItem;
		//imgurl="/images/close.png";
		var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
		currnode.add(tmp);
		//if(currnode.load)
			//tmp.expand();
	}
	function add1(uid,text,action,target,title,imgurl,xml)
	{
		var currnode=Global.selectedItem;
		imgurl="/images/table.gif";
		var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
		currnode.add(tmp);
		//if(currnode.load)
		//	tmp.expand();
	}
//-->
</script>

<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<html:form action="/system/codemaintence/codetree">
   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >
         <tr>
           <td align="left"> 
            <div id="treemenu"  ondragend="dragend('codeset','codesetid','categories','1010050022');" style="width: expression(document.body.clientWidth)"> 
             <SCRIPT LANGUAGE=javascript> 
             	<hrms:priv func_id=""> 
               	setDrag(true); 
             </hrms:priv>                
               <bean:write name="codeMaintenceForm" property="treecode" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>           
   </table>  
   <script type="text/javascript">
	  // root.expand();
	  root.openURL();
	  
	  function dragend(table,primarykey_column_name,father_column_name,function_id)
{
	if(table==null||primarykey_column_name==null||father_column_name==null){
		alert("请设置完整参数，表名、主键列名、父节点列名、执行类ID（可选）");
		return;
	}
	var currnode=this.Global.selectedItem;
	if(currnode==null||currnode.dragFrom==null)
			return;
	//不能将自己拖动到自己身上  guodd 2015-09-14
	if(currnode==currnode.dragFrom){
		return;
	}
	var flag = false;
	if('1010050022'==function_id){//常用统计拖动功能
		var fromid=currnode.dragFrom.uid;
		var fromname=currnode.dragFrom.text;
		//alert(fromid+"  "+fromname.substring(0,fromid.length));
		if(fromid!=fromname.substring(0,fromid.length)||fromid==fromname){
			alert("请选择代码类修改其所属分类!");
			return;
		}
		//return;
		var toid=currnode.uid;
		/*if("root"==toid){
			alert("请将代码类修改到选择的分类名称下!");
			return;
		}*/
		var toname=currnode.text;
		var hashvo=new ParameterSet();
		hashvo.setValue("fromid",fromid);
		hashvo.setValue("toid",toid);
		hashvo.setValue("toname",toname);
		var request=new Request({method:'post',onSuccess:checkStaticDragResult,functionId:function_id},hashvo);
		function checkStaticDragResult(outparamters){
			var msg=outparamters.getValue("msg");
			if(msg!='ok'){
				alert(msg);
			}else{
				/*var to=outparamters.getValue("to");
				//alert(currnode.dragFrom.uid+"   "+currnode.dragFrom.text+"   "+currnode.dragFrom.action+"   "+currnode.dragFrom.target+"   "+currnode.dragFrom.title+"   "+currnode.dragFrom.icon+"   "+currnode.dragFrom.xml);
				var rootnode=currnode.root();
				var currnode1 = rootnode.childNodes[0];
     			var currnode2 = rootnode.childNodes[1];
				
				if(to=='`'){
					if(currnode.dragFrom.parent.uid!='00'){
						currnode.dragFrom.remove();
						add3(currnode.dragFrom.uid,currnode.dragFrom.text,currnode.dragFrom.action,currnode.dragFrom.target,currnode.dragFrom.title,currnode.dragFrom.icon,currnode.dragFrom.xml)
					}
					return;
				}
				
				if(to=='``'){
					if(currnode.dragFrom.parent.uid!='02'){
						currnode.dragFrom.remove();
						add3(currnode.dragFrom.uid,currnode.dragFrom.text,currnode.dragFrom.action,currnode.dragFrom.target,currnode.dragFrom.title,currnode.dragFrom.icon,currnode.dragFrom.xml)
						
					}
					return;
				}
				currnode.dragFrom.remove();	
					while(currnode.childNodes.length){
						currnode.childNodes[0].remove();
					}
					currnode.load=true;
					currnode.loadChildren();
					currnode.reload(1);
				currnode.expand();
				*/
				//add3(currnode.dragFrom.uid,currnode.dragFrom.text,currnode.dragFrom.action,currnode.dragFrom.target,currnode.dragFrom.title,currnode.dragFrom.icon,currnode.dragFrom.xml);
				
				currnode.dragFrom.remove();	
					while(currnode.childNodes.length){
						currnode.childNodes[0].remove();
					}
					currnode.load=true;
					currnode.loadChildren();
					currnode.reload(1);
			}
		}
	}
}
   </script>


</html:form>

