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
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<script type="text/javascript">
<!--
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
					//else
					//	root.childNodes[z].expand();
				}
				else
					a(root.childNodes[z],name);
			}
		}
	}
//-->
</script>

<hrms:themes />
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<body style="margin:0px;padding:0px;">
<html:form action="/performance/setyqdt/setyqdt"> <br/><br/>
    <table width="50%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
    	<tr align="center" nowrap class="trShallow">
			<td align="center" nowrap class="TableRow">
				狱情动态设置			
				
			</td>
		</tr>
       <tr align="center" class="trDeep">
           <td align="left" class="RecordRow" width="50%"> 
            <div id="treemenu" style="width:100%;height: 350px;overflow:auto;border-style:inset ;border-width:0px;"> 
               <SCRIPT LANGUAGE=javascript> 
               Global.defaultInput=1;  
    			//默认选中的值 
    			Global.checkvalue = ",<bean:write name="policeForm" property="policeConstant" filter="false"/>";
    			//根节点不显示复选框 
    			Global.showroot = false;
               <bean:write name="policeForm" property="treeCode" filter="false"/>
             </SCRIPT>		
             </div>             
           </td>
       </tr> 
       <tr class="trShallow">
		<td align="center" class="RecordRow">
			<br>
			<html:hidden name="policeForm" property="policeConstant"/> 
    <input type="button" name="btnok" value="保存" class="mybutton" onclick="savecode();">
		</td>
	</tr>          
    </table>
</html:form>
</body>

<script type="text/javascript">
<!--
	function savecode() {
		var val= root.getSelected();
		if (val != null) {
			document.getElementsByName("policeConstant")[0].value = val;
		} else {
			document.getElementsByName("policeConstant")[0].value = "";
		}
		policeForm.action="/performance/setyqdt/setyqdt.do?b_save=link";
		policeForm.submit();
    }
//-->
</script> 
