<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.gz.voucher.VoucherForm,java.util.*,com.hrms.hjsj.sys.VersionControl"%>
<%
    	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	VoucherForm voucherForm =(VoucherForm)session.getAttribute("financial_voucherForm"); 
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	} 
%>

<HTML>
<HEAD>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<hrms:themes />
<script language="javascript" src="../../ajax/constant.js"></script>
<script language="javascript" src="../../ajax/basic.js"></script>
<script language="javascript" src="../../ajax/common.js"></script>
<script language="javascript" src="../../ajax/control.js"></script>
<script language="javascript" src="../../ajax/dataset.js"></script>
<script language="javascript" src="../../ajax/editor.js"></script>
<script language="javascript" src="../../ajax/dropdown.js"></script>
<script language="javascript" src="../../ajax/table.js"></script>
<script language="javascript" src="../../ajax/menu.js"></script>
<script language="javascript" src="../../ajax/tree.js"></script>
<script language="javascript" src="../../ajax/pagepilot.js"></script>
<script language="javascript" src="../../ajax/command.js"></script>
<script language="javascript" src="../../ajax/format.js"></script>
<script language="javascript" src="../../js/validate.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="../../js/popcalendar.js"></script>
	<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>

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
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
   <SCRIPT LANGUAGE=javascript>
    	var newwindow=null;
    	function Accounts()
    	{
    	  var target_url;
          target_url="/gz/voucher/financial_voucher.do?b_account=link";
          	financial_voucherForm.action="/gz/voucher/financial_voucher.do?b_account=link";
          	financial_voucherForm.target="mil_body";         	
			financial_voucherForm.submit()
		}
    	
    	function add_base()
    	{
    	  var theArr=new Array(null);
    	  var target_url="/gz/voucher/financial_voucher.do?b_add=link`flagtemp=new`itFlag=0";
          var url="/gz/voucher/iframe_voucher.jsp?src="+$URL.encode(target_url);
          var return_vo=window.showModalDialog(url,theArr,"dialogWidth:500px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no"); 
          if(return_vo==null){
			return ;
			}else{
		  	var pn_id  = return_vo[0];
 			var c_name = return_vo[1];
 			var interface_type = return_vo[2];
 			var currnode=Global.selectedItem;
 			var root = currnode.root();
 			var tmp = new xtreeItem(pn_id,c_name,"/gz/voucher/searchvoucherdate.do?b_query=link&interface_type="+interface_type+"&pn_id="+pn_id+"&showflag=2","mil_body",c_name,
	 				"/images/table.gif","/gz/voucher/financial_voucher_list.jsp?params=1%3D1");
			root.add(tmp);
			selectedClass("treeItem-text-"+tmp.id);//设置新建的凭证叶子结点为选中的颜色
   			tmp.select();//设置新建的凭证叶子结点为选中,这个方法,默认会展开右侧的连接
		  }
		}
		
    	function modify_base()
    	{
    	   var currnode,codeitemid,target_url;
    	   currnode=Global.selectedItem;
    	   if(currnode==null)
    	    	return;
    	   codeitemid=currnode.uid;
    	   if(codeitemid=="root")
    	   {
        	   alert("不能修改根目录！");
    	   }
    	   else
    	   {
    	   	 var theArr=new Array(codeitemid);
    	 	 var target_url;
         	 target_url="/gz/voucher/financial_voucher.do?b_add=link`flagtemp=update`itFlag=1";
          	var url="/gz/voucher/iframe_voucher.jsp?src="+$URL.encode(target_url);
          	var return_vo=window.showModalDialog(url,theArr,"dialogWidth:500px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no"); 
    	   	if(return_vo==null){
    	   		currnode.openURL();	
    	   		return ;
    	   	}else{

    	   		var name = return_vo[1];
    	   		currnode.setAction("/gz/voucher/searchvoucherdate.do?b_query=link&interface_type="+return_vo[2]+"&pn_id="+return_vo[0]+"&showflag=2");
				currnode.openURL();				
				currnode.setText(name);
    	   	}
	   		}
    	}
    	//删除
    	
    	function delete_base()
    	{
    	   var currnode,codeitemid,target_url;
    	   currnode=Global.selectedItem;    	   
    	   if(currnode==null)
    	    	return;    	    
    	   codeitemid=currnode.uid;    	      	  
    	   if(codeitemid=="root")
    	   {
    	   	alert('不能删除根目录!');
    	   }
    	   else
    	   {
    	    if(confirm('是否删除选择的记录？'))
	      	{
	      		var theArr=new Array(currnode);
				var hashvo=new ParameterSet();
				hashvo.setValue("pn_id",codeitemid);
				var request=new Request({asynchronous:false,onSuccess:delete_item_ok,functionId:'3020073007'},hashvo)
    	  	}
    	     
    	   }
    	 }
function delete_item_ok(outparamters)
{
	var err_message = outparamters.getValue("err_message");
	if(err_message != null){
		alert(err_message);
		return false;
	}else{
		var currnode = Global.selectedItem; 
		var preitem=currnode.getPreviousSibling();
		currnode.remove();
		function delay()   
		{   
		    selectedClass("treeItem-text-"+preitem.id);//设置删除的凭证叶子结点上一个结点为选中的颜色
			preitem.select();//设置删除的凭证叶子结点上一个结点为选中
		} 
		window.setTimeout(delay,500);
	}
}
	function adjust_order() {
    	var winFeatures = "dialogHeight:400px; dialogLeft:300px;";
    	var target_url="/kq/options/class/kq_class_data.do?br_order=link";
    	      //newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=300,width=596,height=354'); 
		window.showModalDialog(target_url,1, 
        winFeatures + "resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
        window.parent.frames[0].location.reload();
	}
   </SCRIPT>  
</HEAD>

<body  style="margin-left:0px;margin-top:0px;">
<html:form action="/gz/voucher/financial_voucher">
  		<table  border="0" cellpadding="0" cellspacing="0" width=120% >
			<tr  class="toolbar">
				<td valign="middle" >	 
					<a href="###" onclick="add_base();"><img src="/images/add.gif"  alt="新增" border="0" align="middle"></a>               
				 
                    <a href="###" onclick="modify_base();"><img src="/images/edit.gif" alt="修改" border="0" align="middle"></a>  
                     
                    <a href="###" onclick="delete_base();"><img src="/images/del.gif" alt="删除" border="0" align="middle"></a>  
                  
					<a href="###" onclick="Accounts();"><img src="/images/img_o.gif"  alt="设置" border="0" align="middle"></a>  
				</td>
			</tr>	

  <tr>  
    <td valign="top">
	<div id="treemenu"></div>
    </td>
  </tr>
</table>
</html:form>
</body>
</HTML>

<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript>
  var m_sXMLFile= "/gz/voucher/financial_voucher_list.jsp?params=1%3D1";	 
  var root=new xtreeItem("root","凭证","/gz/voucher/searchvoucherdate.do?b_query=link&pn_id=","mil_body","凭证","/images/book.gif",m_sXMLFile);
  root.setup(document.getElementById("treemenu"));
</SCRIPT>
<script language="javascript">
  initDocument();
</script>