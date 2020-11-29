<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<script type="text/javascript">  
  function subid()
  {
     var currnode=Global.selectedItem; 
	 var thevo=new Object();		
	 thevo.content=root.getSelected();
	 thevo.title=root.getSelectedTitle();	
	 thevo.flag="true";
	 if(parent.Ext){//ext 弹窗  wangb 20190319
	 	 var win = parent.Ext.getCmp('select_org');
	 	 win.return_vo = thevo;
	 	 win.close();
	 }else{
	     window.returnValue=thevo;
    	 window.close(); 
	 }
  }
  // 弹窗关闭方法   wangb 20190319
  function winclose(){
  	 if(parent.Ext){
  	 	parent.Ext.getCmp('select_org').close();
  	 	return;
  	 }
  	 window.close();
  }
</script>
<hrms:themes></hrms:themes>
   <style type="text/css">
	#treemenu {  
	height: 330px;
	overflow: auto;
	border-style:solid;
	border-width:1px;
	}
   </style>   
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<html:form action="/system/warn/config_maintenance"> 
   <div id="treemenu" class="fixedDiv3"> 
    <SCRIPT LANGUAGE=javascript>    
      Global.defaultInput=2;  
      Global.defaultradiolevel=2;//原因生成模板树后台逻辑改变，层级发生改变此处也对应修改 wangb 2019-12-5 bug 55984   
      //Global.checkvalue="${pparameterForm.select_id}";      
      <bean:write name="pparameterForm" property="bs_tree" filter="false"/>
    </SCRIPT>
   </div>    
   <div class="fixedDiv3" align="center" style="margin-top: 5px;">        
	<html:button styleClass="mybutton" property="b_save" onclick="subid();">
   		<bean:message key="button.ok"/>
    </html:button>
    <html:button styleClass="mybutton" property="br_return" onclick="winclose();">
    	<bean:message key="button.close"/>
    </html:button>         
  </div>    
<script type="text/javascript">
	//root.expandAll();
	//root.expand2level();
	if(getBrowseVersion()==10 || !getBrowseVersion()){//非ie兼容性  样式 修改  wangb 20190323
		var treemenu = document.getElementById('treemenu');
		treemenu.style.width = parseInt(document.body.clientWidth)-10+'px';
		treemenu.style.whiteSpace = 'nowrap';
	}
</script>
</html:form>
