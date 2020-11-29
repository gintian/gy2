<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
	 function savefield()
	 {
	 	var hashvo=new ParameterSet();
	 	var dblist = document.getElementsByName("dbstr");
	 	var dbtrue=new Array();
	 	for(var i=0;i<dblist.length;i++)
	 	{
	 		if(dblist[i].checked==true)
	 			dbtrue[i] = "true";
	 		else
	 			dbtrue[i] = "false";
	 	}
	 	hashvo.setValue("dbtrue",dbtrue);
	 	var request=new Request({method:'post',onSuccess:showSelect,functionId:'1010100105'},hashvo);
	 	//window.close();
	 }
	 function showSelect(outparamters)
	 {
	 	 var mess=outparamters.getValue("mess");        
         var thevo=new Object();
		 thevo.mess=mess;
		 
		 if(parent.parent.Ext && parent.parent.Ext.getCmp('select_db')){//ext 弹窗返回数据 wangb 20190320
			var win = parent.parent.Ext.getCmp('select_db');
			win.return_vo = thevo;
		 }else{
			 window.returnValue=thevo;
		 }
		 //window.close();
		 winclose(); 
	 	
	 } 
	 //关闭弹窗方法  wangb 20190320
	 function winclose(){
	 	if(parent.parent.Ext && parent.parent.Ext.getCmp('select_db')){
			var win = parent.parent.Ext.getCmp('select_db');
			win.close();
			return;
		}
	 	window.close();
	 }
</script>
<html:form action="/sys/export/SetHrSyncDb">
<table width="290" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3">
		请选择库&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr>
	   	  <td width="100%" align="left" nowrap>
	   	  	<!-- 【7692】系统管理/应用设置/数据交换/数据视图，出现的界面滚动条可以去掉  jingq upd 2015.02.25 -->
	   	  	<!-- 【10963】系统管理/应用设置/数据交换/数据视图，人员库设置界面出现滚动条  hej upd 2015.07.09 -->
	   	  	<div style="width:100%;height:155px;overflow:auto;">
	   	  	<table >
	           		<logic:iterate id="db" name="hrSyncForm" property="dbprelist">
	           		<tr>
	           			<td nowrap>&nbsp;
	           				<logic:notEqual name="db" property="dbname" value="">
	           					<logic:equal name="db" property="check" value="1">
	           						<input   type="checkbox"   name="dbstr" checked>
										<bean:write name="db" property="dbname" />
	           					</logic:equal>
	           					<logic:notEqual name="db" property="check" value="1">
	           						<html:checkbox name="hrSyncForm" property="dbstr" >
										<bean:write name="db" property="dbname" />
				           			</html:checkbox>
	           					</logic:notEqual>
				           		
			           		</logic:notEqual>
			           		<logic:equal name="db" property="dbname" value="">
			           			&nbsp;
			           		</logic:equal>
		           		</td>
		           	</tr>
	           		</logic:iterate>
	           	</table>
	           	</div>
	      </td>
      </tr>
      
</table>
<table width="100%">
	<tr>
	      <td align="center" nowrap  colspan="3" height="35px;">
	      		<input type="button" name="btnreturn" value='确定' class="mybutton" onclick=" savefield();">
	     		<input type="button" name="btnreturn" value='关闭' class="mybutton" onclick=" winclose();">
	      </td>
      </tr>
</table>
</html:form>
<script>
if(!getBrowseVersion()){//非ie浏览器样式兼容  wangb 20190320
	var table = document.getElementsByClassName('RecordRow')[0];
	var td = table.getElementsByTagName('tr')[1].getElementsByTagName('td')[0];
	td.style.borderRight='#C4D8EE 1pt solid';
}
</script>