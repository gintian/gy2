<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<HTML>
<HEAD>
	<TITLE>
	</TITLE>
	<link href="/css/css1.css" rel="stylesheet" type="text/css">
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript>
	
	function goback()
	{
		window.close();
	
	}
	
	
	
	
	
	
</SCRIPT>     
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String b_units=userView.getUnitIdByBusiOutofPriv("1");
   String aurl = (String)request.getServerName();
    String port=request.getServerPort()+"";
    String prl=request.getProtocol();
    int idx=prl.indexOf("/");
    prl=prl.substring(0,idx);
    String url_p=SystemConfig.getServerURL(request);
 %>  
</HEAD>
<style>
div#treemenu {
BORDER-BOTTOM:#94B6E6 1pt solid; 
BORDER-COLLAPSE: collapse;
BORDER-LEFT: #94B6E6 1pt solid; 
BORDER-RIGHT: #94B6E6 1pt solid; 
BORDER-TOP: #94B6E6 1pt solid; 
width: 400px;
height: 240px;
overflow: auto;
}

</style>
<hrms:themes />
<body >
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
<html:form action="/gz/gz_accounting/report">

	<table width="485px;" align="center" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	<tr>  
		<td valign="top">
			<table><tr><td>	
			<div id="treemenu"></div>
			</td></tr></table>
			
		</td>	
		<td valign="top"  >
			
			
					<table border=0 width="100%" height="100%" >
				
					
					<tr><td height='150' valign='top' >
					
					<hrms:priv func_id="32713080201,32712050301,32703090201,32702050301,32503100201,32502050301,32403100101,32402050301">
							<div id='b_add' style='display:none;height:35px;' >
						<input type='button'  class="mybutton" value=' 新增 '  onclick='add()' />
							</div>

				    </hrms:priv>
				           
				           <hrms:priv func_id="32713080202,32712050302,32703090202,32702050302,32503100202,32502050302,32403100102,32402050302">
				           
				           <div id='b_edit' style='display:none;height:35px;' >
			            <input type='button'  class="mybutton" value=' 修改 '  onclick='edit()' />
							</div>

							</hrms:priv>
							
							   <hrms:priv func_id="32713080203,32712050303,32703090203,32702050303,32503100203,32502050303,32403100103,32402050303">
							<div id='b_delete' style='display:none;height:35px;' >
			            <input type='button' class="mybutton" value=' 删除 '  onclick='del()' />
							</div>

						</hrms:priv>

					<div id='b_open' style='display:none;height:35px;' >
			           <input type='button'  class="mybutton" value=' 打开 '  onclick='enter()' />
			        </div> 
			        <div id="bbb_open" style="display:none;height:35px;">
<input type="button" class="mybutton" value=" 打开 " onclick="showOpenMusterOne();"/>
</div>
<div id="bbbb_open" style="display:none;height:35px;">
<input type="button" class="mybutton" value=" 打开 " onclick="showOpenMusterTwo();"/>
</div>
<div id="bbbbb_open" style="display:none;height:35px;">
<input type="button" class="mybutton" value=" 打开 " onclick="showOpenMusterThree();"/>
</div>
<div id="bbbbbb_open" style="display:none;height:35px;">
<input type="button" class="mybutton" value=" 打开 " onclick="showCustom();"/>
</div>
<div id="bbbbbbb_open" style="display:none;height:35px;">
<input type="button" class="mybutton" value=" 打开 " onclick="showXLSCustom();"/>
</div>
<div id="simpleMuster_open" style="display:none;height:35px;">
<input type="button" class="mybutton" value="打开" onclick="showSimpleMuster();"/>
</div>
						<div id="ffxx" style="height:35px;">
			           <input type='button'  class="mybutton" value=' 取消 '  onclick='goback()' />
			           </div>
					</td></tr>
					</table>
		
		</td>
	</tr>
</table>	

</html:form>
<BODY>
</HTML>


<SCRIPT LANGUAGE=javascript>
	var rsid;
	var rsdtlid;
	var focus_obj_node;
	var desc="报表";
	<logic:equal  name="gzReportForm" property="gz_module"  value="1">
		desc="报表";
	</logic:equal>
	
	var m_sXMLFile	= "/gz/gz_accounting/report/gzReportTree.jsp?flag=${gzReportForm.gz_module}&salaryid=${gzReportForm.salaryid}";		
	var newwindow;
	var root=new xtreeItem("root",desc,"","mil_body",desc,"/images/add_all.gif",m_sXMLFile);
	//Global.defaultInput=1;
	Global.showroot=true;
	root.setup(document.getElementById("treemenu"));	
	if(newwindow!=null)
	{
		newwindow.focus();
	}
	if(parent.parent.myNewBody!=null)
	{
		parent.parent.myNewBody.cols="*,0"
	}
	
	
	var focus_obj_node=root.getFirstChild();
	focus_obj_node.expand();
	function showEidtButton()
	{
		
		var obj=document.getElementById("b_add");
		if(obj)
	    	obj.style.display="none";
		var obj1=document.getElementById("b_edit");
		if(obj1)
	    	obj1.style.display="block";
		var obj2=document.getElementById("b_delete");
		if(obj2)
    		obj2.style.display="block";
		var obj3=document.getElementById("b_open");
		if(obj3)
	    	obj3.style.display="block";
	    var obj4=document.getElementById("bbb_open");
		if(obj4)
	    	obj4.style.display="none";
	    var obj5=document.getElementById("bbbb_open");
		if(obj5)
	    	obj5.style.display="none";
	    var obj6=document.getElementById("bbbbb_open");
		if(obj6)
	    	obj6.style.display="none";
	    var obj7=document.getElementById("bbbbbb_open");
		if(obj7)
	    	obj7.style.display="none";
	    var obj8=document.getElementById("bbbbbbb_open");
	    if(obj8)
	        obj8.style.display="none";
	    var obj9 = document.getElementById("simpleMuster_open");
        if(obj9)
            obj9.style.display="none";
		focus_obj_node=Global.selectedItem;
		rsdtlid=focus_obj_node.uid.substring(0,focus_obj_node.uid.indexOf("#"));
		rsid=focus_obj_node.parent.uid.substring(0,focus_obj_node.parent.uid.indexOf("#"));
		
	}
	
	function showNewButton()
	{
		var obj=document.getElementById("b_add");
		if(obj)
    		obj.style.display="block";
		var obj1=document.getElementById("b_edit");
		if(obj1)
	    	obj1.style.display="none";
		var obj2=document.getElementById("b_delete");
		if(obj2)
     		obj2.style.display="none";
		var obj3=document.getElementById("b_open");
		if(obj3)
	    	obj3.style.display="none";
	       var obj4=document.getElementById("bbb_open");
		if(obj4)
	    	obj4.style.display="none";
	    var obj5=document.getElementById("bbbb_open");
		if(obj5)
	    	obj5.style.display="none";
	    var obj6=document.getElementById("bbbbb_open");
		if(obj6)
	    	obj6.style.display="none";
	    var obj7=document.getElementById("bbbbbb_open");
		if(obj7)
	    	obj7.style.display="none";
	    var obj8=document.getElementById("bbbbbbb_open");
	    if(obj8)
	        obj8.style.display="none";
	    var obj9 = document.getElementById("simpleMuster_open");
        if(obj9)
            obj9.style.display="none";
		focus_obj_node=Global.selectedItem;
	}
	 
	function showOpenButton()
	{
		var obj=document.getElementById("b_add");
		if(obj)
	    	obj.style.display="none";
		var obj1=document.getElementById("b_edit");
		if(obj1)
	    	obj1.style.display="none";
		var obj2=document.getElementById("b_delete");
		if(obj2)
	     	obj2.style.display="none";
		var obj3=document.getElementById("b_open");
		if(obj3)
	     	obj3.style.display="block";
	       var obj4=document.getElementById("bbb_open");
		if(obj4)
	    	obj4.style.display="none";
	    var obj5=document.getElementById("bbbb_open");
		if(obj5)
	    	obj5.style.display="none";
	    var obj6=document.getElementById("bbbbb_open");
		if(obj6)
	    	obj6.style.display="none";
	    var obj7=document.getElementById("bbbbbb_open");
		if(obj7)
	    	obj7.style.display="none";
	    var obj8=document.getElementById("bbbbbbb_open");
	    if(obj8)
	        obj8.style.display="none";
	    var obj9 = document.getElementById("simpleMuster_open");
        if(obj9)
            obj9.style.display="none";
		focus_obj_node=Global.selectedItem;
		rsdtlid="";
		rsid=focus_obj_node.uid.substring(0,focus_obj_node.uid.indexOf("#"));
		
	}
	function showOpenMusterOneButton()
{
       var obj=document.getElementById("b_add");
       if(obj)
		  obj.style.display="none";
		var obj1=document.getElementById("b_edit");
		if(obj1)
	    	obj1.style.display="none";
		var obj2=document.getElementById("b_delete");
		if(obj2)
	    	obj2.style.display="none";
		var obj3=document.getElementById("b_open");
		if(obj3)
	    	obj3.style.display="none";
		var obj5 = document.getElementById("bbb_open");
		if(obj5)
	    	obj5.style.display="block";
		var obj6 = document.getElementById("bbbb_open");
		if(obj6)
	    	obj6.style.display="none";
		var obj7 = document.getElementById("bbbbb_open");
		if(obj7)
	    	obj7.style.display="none";
		var obj8 = document.getElementById("bbbbbb_open");
		if(obj8)
	    	obj8.style.display="none";
		var obj9=document.getElementById("bbbbbbb_open");
	    if(obj9)
	        obj9.style.display="none";
	    var obj10 = document.getElementById("simpleMuster_open");
        if(obj10)
            obj10.style.display="none";
		focus_obj_node=Global.selectedItem;
}
function showOpenMusterTwoButton()
{
       var obj=document.getElementById("b_add");
       if(obj)
    		obj.style.display="none";
		var obj1=document.getElementById("b_edit");
		if(obj1)
		   obj1.style.display="none";
		var obj2=document.getElementById("b_delete");
		if(obj2)
	    	obj2.style.display="none";
		var obj3=document.getElementById("b_open");
		if(obj3)
    		obj3.style.display="none";
		var obj5 = document.getElementById("bbb_open");
		if(obj5)
	    	obj5.style.display="none";
		var obj6 = document.getElementById("bbbb_open");
		if(obj6)
	    	obj6.style.display="block";
		var obj7 = document.getElementById("bbbbb_open");
		if(obj7)
     		obj7.style.display="none";
		var obj8 = document.getElementById("bbbbbb_open");
		if(obj8)
	    	obj8.style.display="none";
		var obj9=document.getElementById("bbbbbbb_open");
	    if(obj9)
	        obj9.style.display="none";
	    var obj10 = document.getElementById("simpleMuster_open");
        if(obj10)
            obj10.style.display="none";
		focus_obj_node=Global.selectedItem;
}
function showOpenMusterThreeButton()
{
       var obj=document.getElementById("b_add");
       if(obj)
	    	obj.style.display="none";
		var obj1=document.getElementById("b_edit");
		if(obj1)
	    	obj1.style.display="none";
		var obj2=document.getElementById("b_delete");
		if(obj2)
	    	obj2.style.display="none";
		var obj3=document.getElementById("b_open");
		if(obj3)
	    	obj3.style.display="none";
		var obj5 = document.getElementById("bbb_open");
		if(obj5)
	    	obj5.style.display="none";
		var obj6 = document.getElementById("bbbb_open");
		if(obj6)
     		obj6.style.display="none";
		var obj7 = document.getElementById("bbbbb_open");
		if(obj7)
	    	obj7.style.display="block";
		var obj8 = document.getElementById("bbbbbb_open");
		if(obj8)
	    	obj8.style.display="none";
		var obj9=document.getElementById("bbbbbbb_open");
	    if(obj9)
	        obj9.style.display="none";
	    var obj10 = document.getElementById("simpleMuster_open");
        if(obj10)
            obj10.style.display="none";
		focus_obj_node=Global.selectedItem;
}
function showOpenCustomButton()
{
       var obj=document.getElementById("b_add");
       if(obj)
	    	obj.style.display="none";
		var obj1=document.getElementById("b_edit");
		if(obj1)
	    	obj1.style.display="none";
		var obj2=document.getElementById("b_delete");
		if(obj2)
	    	obj2.style.display="none";
		var obj3=document.getElementById("b_open");
		if(obj3)
    		obj3.style.display="none";
		var obj5 = document.getElementById("bbb_open");
		if(obj5)
    		obj5.style.display="none";
		var obj6 = document.getElementById("bbbb_open");
		if(obj6)
	    	obj6.style.display="none";
		var obj7 = document.getElementById("bbbbb_open");
		if(obj7)
	    	obj7.style.display="none";
		var obj8 = document.getElementById("bbbbbb_open");
		if(obj8)
	    	obj8.style.display="block";
		var obj9=document.getElementById("bbbbbbb_open");
	    if(obj9)
	        obj9.style.display="none";
	    var obj10 = document.getElementById("simpleMuster_open");
        if(obj10)
            obj10.style.display="none";
		focus_obj_node=Global.selectedItem;
}
	function showOpenButton2()
	{
		var obj=document.getElementById("b_add");
		if(obj)
     		obj.style.display="none";
		var obj1=document.getElementById("b_edit");
		if(obj1)
	    	obj1.style.display="none";
		var obj2=document.getElementById("b_delete");
		if(obj2)
	     	obj2.style.display="none";
		var obj3=document.getElementById("b_open");
		if(obj3)
	    	obj3.style.display="none";
	       var obj4=document.getElementById("bbb_open");
		if(obj4)
	    	obj4.style.display="none";
	    var obj5=document.getElementById("bbbb_open");
		if(obj5)
	    	obj5.style.display="none";
	    var obj6=document.getElementById("bbbbb_open");
		if(obj6)
	    	obj6.style.display="none";
	    var obj7=document.getElementById("bbbbbb_open");
		if(obj7)
	    	obj7.style.display="none";
	    var obj9=document.getElementById("bbbbbbb_open");
	    if(obj9)
	        obj9.style.display="none";
	    var obj10 = document.getElementById("simpleMuster_open");
	    if(obj10)
	        obj10.style.display="none";
		focus_obj_node=Global.selectedItem;
		rsdtlid="";
		rsid=focus_obj_node.uid.substring(0,focus_obj_node.uid.indexOf("#"));
		
	}
	function showOpenCustomXLSButton()
{
       var obj=document.getElementById("b_add");
       if(obj)
	    	obj.style.display="none";
		var obj1=document.getElementById("b_edit");
		if(obj1)
	    	obj1.style.display="none";
		var obj2=document.getElementById("b_delete");
		if(obj2)
	    	obj2.style.display="none";
		var obj3=document.getElementById("b_open");
		if(obj3)
	    	obj3.style.display="none";
		var obj5 = document.getElementById("bbb_open");
		if(obj5)
    		obj5.style.display="none";
		var obj6 = document.getElementById("bbbb_open");
		if(obj6)
	    	obj6.style.display="none";
		var obj7 = document.getElementById("bbbbb_open");
		if(obj7)
	    	obj7.style.display="none";
		var obj8 = document.getElementById("bbbbbb_open");
		if(obj8)
	    	obj8.style.display="none";
		var obj9 = document.getElementById("bbbbbbb_open");
		if(obj9)
	    	obj9.style.display="none";
		var obj10=document.getElementById("bbbbbbb_open");
	    if(obj10)
	        obj10.style.display="block";
	    var obj11 = document.getElementById("simpleMuster_open");
	    if(obj11)
	        obj11.style.display="none";
		focus_obj_node=Global.selectedItem;
}
	function showSimpleMusterButton()
	{
	       var obj=document.getElementById("b_add");
	       if(obj)
	          obj.style.display="none";
	        var obj1=document.getElementById("b_edit");
	        if(obj1)
	            obj1.style.display="none";
	        var obj2=document.getElementById("b_delete");
	        if(obj2)
	            obj2.style.display="none";
	        var obj3=eval("b_open");
	        obj3.style.display="none";
	        var obj4=eval("bb_open");
	        obj4.style.display="none";
	        var obj5 = eval("bbb_open");
	        obj5.style.display="none";
	        var obj6 = eval("bbbb_open");
	        obj6.style.display="none";
	        var obj7 = eval("bbbbb_open");
	        obj7.style.display="none";
	        var obj8 = eval("bbbbbb_open");
	        obj8.style.display="none";
	        var obj9 = eval("bbbbbbb_open");
	        obj9.style.display="none";
	        var obj10 = eval("simpleMuster_open");
	        obj10.style.display="block";
	        focus_obj_node=Global.selectedItem;
	}
	function del()
	{
	    var currnode=Global.selectedItem;
	   if(currnode.uid=="root")
	   {
	     return;
	   }
		if(!confirm("请确认执行删除操作?"))
			return false;
		 var a_parent=currnode.parent;
		 var hashvo=new ParameterSet();
	     hashvo.setValue("rsdtlid",currnode.uid.substring(0,currnode.uid.indexOf("#")));	
	     hashvo.setValue("rsid",a_parent.uid.substring(0,a_parent.uid.indexOf("#")));       
	   　 var request=new Request({asynchronous:false,onSuccess:delete_ok,functionId:'3020130003'},hashvo);      
	}
	
	function delete_ok(outparamters)
	{
	     var currnode=Global.selectedItem;
	     var preitem=currnode.getPreviousSibling();
	     var a_parent =currnode.parent;
	     currnode.remove();
	     if(preitem!=null)
	     {
	          preitem.select(preitem);
	          if(preitem.uid.substring(2)=='0')
	          {
	                var obj=document.getElementById("b_add");
	                if(obj)
		                obj.style.display="block";
	            	var obj1=document.getElementById("b_edit");
	            	if(obj1)
	                 	obj1.style.display="none";
		            var obj2=document.getElementById("b_delete");
		            if(obj2)
	                	obj2.style.display="none";
	            	var obj3=document.getElementById("b_open");
	            	if(obj3)
	                	obj3.style.display="none";
	            	focus_obj_node=Global.selectedItem;
	          }
	     }else
	     {
	      focus_obj_node=Global.selectedItem;
	     }	     
	   
	     
	}
	
	
	function add()
	{
		focus_obj_node=Global.selectedItem;
		var arguments=new Array();    
	    var strurl="/gz/gz_accounting/report.do?b_define=link`opt=new`salaryid=${gzReportForm.salaryid}`id="+focus_obj_node.uid.substring(0,focus_obj_node.uid.indexOf("#"));
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
        var flag=window.showModalDialog(iframe_url,arguments,"dialogWidth=370px;dialogHeight=450px;resizable=yes;scroll=no;status=no;");  
	    if(flag)
		{
			focus_obj_node.expand();
		   	focus_obj_node.clearChildren();
			focus_obj_node.loadChildren();
			focus_obj_node.expand();
		}
	}
	
	function edit()
	{
		var arguments=new Array();     
	    var strurl="/gz/gz_accounting/report.do?b_define=link`opt=edit`salaryid=${gzReportForm.salaryid}`id="+focus_obj_node.uid.substring(0,focus_obj_node.uid.indexOf("#"));
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	    var flag=window.showModalDialog(iframe_url,arguments,"dialogWidth=370px;dialogHeight=450px;resizable=yes;scroll=no;status=no;");  
		if(flag)
		{
		   	var a_parent=focus_obj_node.parent;
		   	a_parent.clearChildren();
	    	a_parent.loadChildren();
			a_parent.expand();
		}
	}
function isOutofPriv(){
	var b_units = '<%=b_units%>';
	var model = '${gzReportForm.model}';
	if((b_units=="UN"||b_units=="")&&model=='3'){
		alert("管理员没有给您授权业务范围或操作单位，您无权查看数据！");
	}
}	
	
	function enter()
	{
			isOutofPriv();
	 	  var theArr=new Array(focus_obj_node.parent.text,focus_obj_node.text);
	 	  var arguments=""; 
	 	  //alert(focus_obj_node.parent.text);
	 	  //alert(focus_obj_node.parent.uid.substring(0,focus_obj_node.parent.uid.indexOf("#")));
	 	 if(focus_obj_node.parent.uid.substring(0,focus_obj_node.parent.uid.indexOf("#"))=='0')
	 	 {
	 	 	var strurl="/gz/gz_accounting/report/gz_org_tree.do?b_query=link`";
	 	 	strurl+="salaryid=${gzReportForm.salaryid}`tabid="+focus_obj_node.uid.substring(0,focus_obj_node.uid.indexOf("#"));
	 	 	strurl+="`opt=int`a_code=<%=(request.getParameter("a_code"))%>`conid=${gzReportForm.condid}`";
	 	 	strurl+="gz_module=${gzReportForm.gz_module}`reset=1`";
	 	 	strurl+="model=${gzReportForm.model}`boscount=${gzReportForm.boscount}`bosdate=${gzReportForm.bosdate}`pageRows=init";
	 	 	
	 	 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
		     var flag=window.showModalDialog(iframe_url,"","dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");  
	 	 }
	 	 else
	 	 {
		 	 if(rsid==3||rsid==13)
		 	 {
		 	 	var strurl="/gz/gz_accounting/report.do?b_queryGroup=query`rsdtlid="+focus_obj_node.uid.substring(0,focus_obj_node.uid.indexOf("#"));
			    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
			    var values=window.showModalDialog(iframe_url,"","dialogWidth=450px;dialogHeight=330px;resizable=yes;scroll=no;status=no;");  
				if(values==null)
					return;
				else
					arguments=values;
			 }
			 var height=window.screen.height-85;
			 var Actual_Version=browserinfo();
			 if (Actual_Version!=null&&Actual_Version.length>0&&Actual_Version=='7.0') {
				  	
				   height=height-25;
			 }
			 var rl = document.getElementById("hostname").href;
		 	// var strurl="/gz/gz_accounting/report/open_gzbanner.do?b_query=query`screenWidth="+(window.screen.width-10)+"`screenHeight="+height+"`rsid="+rsid+"`rsdtlid="+rsdtlid+"`groupValues="+arguments+"`s=${gzReportForm.filterWhl}`pt="+rl;
		     var strurl="/gz/gz_accounting/report/open_gzbanner.do?b_query=query`screenWidth="+(window.screen.width-10)+"`screenHeight="+height+"`rsid="+rsid+"`rsdtlid="+rsdtlid+"`groupValues="+arguments+"`pt="+rl;
		    
		     var iframe_url="/gz/gz_analyse/gz_analyse_iframe.jsp?src="+$URL.encode(strurl);
		     var flag=window.showModalDialog(iframe_url,theArr,"dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");  
		 }		  	
	}
	 
	 //打开人员花名册
function showOpenMusterOne()
{
	isOutofPriv();
  var tabid=focus_obj_node.uid;
  var theArr=new Array(focus_obj_node.parent.text,focus_obj_node.text); 
  var thecodeurl ="/general/muster/hmuster/select_muster_name.do?b_custom=link`nFlag=3`a_inforkind=1`result=0`isGetData=1`operateMethod=direct`costID="+tabid;     	  
  var iframe_url="/gz/gz_analyse/gz_analyse_iframe.jsp?src="+$URL.encode(thecodeurl);
  var return_vo= window.showModalDialog(iframe_url,theArr, 
              "dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");
  
}
//打开机构花名册
function showOpenMusterTwo()
{
	isOutofPriv();
  var tabid=focus_obj_node.uid;
  var theArr=new Array(focus_obj_node.parent.text,focus_obj_node.text); 
  var thecodeurl ="/general/muster/hmuster/select_muster_name.do?b_custom=link`nFlag=21`a_inforkind=2`result=0`isGetData=1`operateMethod=direct`costID="+tabid;     	  
  var iframe_url="/gz/gz_analyse/gz_analyse_iframe.jsp?src="+$URL.encode(thecodeurl);
  var return_vo= window.showModalDialog(iframe_url,theArr, 
              "dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");
  
}
//打开职位花名册
function showOpenMusterThree()
{
	isOutofPriv();
  var tabid=focus_obj_node.uid;
  var theArr=new Array(focus_obj_node.parent.text,focus_obj_node.text); 
  var thecodeurl ="/general/muster/hmuster/select_muster_name.do?b_custom=link`nFlag=41`a_inforkind=3`result=0`isGetData=1`operateMethod=direct`costID="+tabid;     	  
  var iframe_url="/gz/gz_analyse/gz_analyse_iframe.jsp?src="+$URL.encode(thecodeurl);
  var return_vo= window.showModalDialog(iframe_url,theArr,  "dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");
  
}
//打开自定义报表
function showCustom()
{
	isOutofPriv();
   var url="/system/options/customreport/displaycustomreportservlet?ispriv=1&id="+focus_obj_node.uid;
   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
}
function showXLSCustom()
{
	isOutofPriv();
    //var hashvo=new ParameterSet();
	//hashvo.setValue("id",focus_obj_node.uid);	
	//hashvo.setValue("ispriv","1");
	//var request=new Request({method:'post',asynchronous:false,onSuccess:showReport,functionId:'10100103411'},hashvo);
   var url="/system/options/customreport/displaycustomreportservlet?ispriv=1&id="+focus_obj_node.uid;
   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
	
}
//打开简单名册报表
function showSimpleMuster()
{
    window.open(focus_obj_node.uid,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
}
function showReport(outparamters)
   {
      var ext=outparamters.getValue("ext");
	  var filename=outparamters.getValue("filename");	
	  if(ext.indexOf('xls')!=-1)
	  {
	     gzReportForm.action="/servlet/DisplayOleFile?filename="+filename;
	     gzReportForm.submit();
	  }
	  
	}
	
	
    function browserinfo(){
        var Browser_Name=navigator.appName;
        var Browser_Version=parseFloat(navigator.appVersion);
        var Browser_Agent=navigator.userAgent;
        
        var Actual_Version;
        var is_IE=(Browser_Name=="Microsoft Internet Explorer");
        if(is_IE){
            var Version_Start=Browser_Agent.indexOf("MSIE");
            var Version_End=Browser_Agent.indexOf(";",Version_Start);
            Actual_Version=Browser_Agent.substring(Version_Start+5,Version_End)
        }
       return Actual_Version;
    }
   
   
	 
</SCRIPT>
