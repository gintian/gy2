<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*,com.hrms.hjsj.sys.EncryptLockClient" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.hjsj.sys.VersionControl,com.hrms.struts.constant.WebConstant"%>
<%
	EncryptLockClient lock = (EncryptLockClient)request.getSession().getServletContext().getAttribute("lock");
 	boolean isH=lock.isHaveBM(31);
 	boolean isS=true;
 	VersionControl ver_ctrl=new VersionControl();	
 	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
 		isH=ver_ctrl.searchFunctionId("350", userView.hasTheFunction("350"));
					isH=false;
		isS=ver_ctrl.searchFunctionId("25012", userView.hasTheFunction("25012"));			
	}
	
	
 %>
<hrms:themes></hrms:themes>
<script type="text/javascript" language="javascript">
function isNum(i_value)
  {
  	re=new RegExp("[^0-9]");
    var s;
    if(s=i_value.match(re)){
        return false;
     }
    return true;
  }
function validateData(obj){
 	if(!isNum(obj.value)){
 		obj.value='';
 		return;
 	}
}
function mincrease1(obj_name){
	var obj=eval("document.dbinitForm."+obj_name);
	var d = obj.value;
	var s = isNaN(d);
	if(s){
		alert(KJG_ZBTX_INFO2);
		return;
	}
	if(obj.value<9999){
	obj.value = obj.value*1+1;
	}
	
}
 function msubtract1(obj_name)
  { 
  		var obj=eval("document.dbinitForm."+obj_name);
  		if(obj.value>1)
			obj.value = obj.value*1-1;
		
  }
function sub(chk){
	n=document.all.index_list.options.length;
	if (n>0){
		for (i=0;i<n;i++){
			document.all.index_list.options[i].selected=chk;
		} 
	}
	return;
}
function subs(sourcebox_id){
	var cole= document.getElementById("usedata").value;
	 if(cole==null||cole==0){
	 	alert(KJG_ZBTX_INFO3);
	 	return;
	 }
	var left_vo,vos;
	vos= document.getElementsByName(sourcebox_id);
	  if(vos==null)
	  		return false;
	  left_vo=vos[0];
	  var set="";
	  var num=0;
	  	for(i=0;i<left_vo.options.length;i++)
	  		{
	  	 		if(left_vo.options[i].selected)
		    		{
		    			set+="/"+left_vo.options[i].value;
		    			num++;
		    			
		    		}
	  		}
	  		
	  		if(num==0)
	  		{
	  		 alert(KJG_ZBTX_INFO4); 
	  		 return;     
	  		}
	  var tablename=$F('alllist');
	  var usename = tablename;
	  var udata  = document.getElementById("usedata").value;
	  var usefy = document.getElementsByName("usefy")[0].checked;
	  if(usefy==true){
	  	usefy='1';
	  }
	  if(usefy==false){
	  	usefy='0';
	  }
	  var hashvo=new ParameterSet();
	  hashvo.setValue("set",set.substring(1));
	  hashvo.setValue("usename",usename);
	  hashvo.setValue("udata",udata); 
	  hashvo.setValue("num",num);
	  hashvo.setValue("usefy",usefy);
	  var request=new Request({method:'post',asynchronous:false,onSuccess:returnExportOk,functionId:'1020010129'},hashvo);
}
function returnExportOk(outparameters)
	{
		var outName=outparameters.getValue("outName");

		//var name=outName.substring(0,outName.length-1)+".xls";
		//var win=open("/servlet/DisplayOleContent?filename="+outName,"excel");
		//xus 20/4/29 vfs改造
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"excel");
	}
function back(){
	window.location="/system/dbinit/fieldset_tree.jsp";
}
function searchFieldList()
{
	var tablename=$F('alllist');
	var hashvo=new ParameterSet();
	hashvo.setValue("tablename",tablename);
	var request=new Request({method:'post',onSuccess:showFieldList,functionId:'1020010134'},hashvo);
	
}
function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("indexlist");
		AjaxBind.bind(dbinitForm.index_list,fieldlist);
	}
function MusterInitData()
{
	var hashvo=new ParameterSet();
	hashvo.setValue("lock","<%=isH%>");
	hashvo.setValue("isS","<%=isS%>");
	var request=new Request({method:'post',onSuccess:showSetList,functionId:'1020010125'},hashvo);
}
function showSetList(outparamters)
	{
		var alllist=outparamters.getValue("alllist");
		AjaxBind.bind(dbinitForm.alllist,alllist);
		if($('alllist').options.length>0)
		{
	  		$('alllist').options[0].selected=true;
	  		// $('alllist').fireEvent("onchange");
           myFireEvent($('alllist'));

		}
	}
/* 兼容fireEvent方法 */
function myFireEvent(el) {
    var evt;
    if (document.createEvent) {
        evt = document.createEvent("MouseEvents");
        evt.initMouseEvent("change", true, true, window,
            0, 0, 0, 0, 0, false, false, false, false, 0, null);
        el.dispatchEvent(evt);
    } else if (el.fireEvent) { // IE
        el.fireEvent("onchange");
    }
}
</script>
<html:form action="/system/dbinit/inforlist">
<table width="100%" height='100%' align="center" cellspacing="0" cellpadding="0">
	<table>
	<tr><td width="100%" >
		<tr> <td class="framestyle" valign="top" align="center">
		<table width="100%"  border="0" cellpadding="0" cellspacing="0"  class="DetailTable"  cellspacing="0">
		<tr>
			<td align="center" style="padding-top:5px;">
				<select name="alllist" size="1"  style="width:90%" onchange="searchFieldList();">
					 <option value="1111">#</option>
				</select>
			</td>
		</tr> 
		<tr>
            <td align="center" style="padding-top:5px;"><!-- 【7101】指标体系中采集表界面去掉滚动条   jingq upd 2015.01.28 -->
                 <select name="index_list" multiple="multiple"  style="height:425px;width:90%;font-size:10pt">
                 </select>
            </td>
                    
        </tr>
  								
  								<tr>
  									<td align="left"  nowrap>
  										&nbsp;&nbsp;&nbsp;&nbsp;<input type="checkbox" name="usefy"
  											<logic:equal name="dbinitForm" property="usefy" value="1">
													checked="true"
											</logic:equal>
  										>
  										<bean:message key="kjg.gather.anzijifenye"/>
  									</td>
  								</tr>
  								<tr><td width="100%" height="100%">
  									<table><tr>
  									<td>&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="kjg.gather.codegeshu"/></td>
  									<td valign="middle">
  										<html:text property="usedata" name="dbinitForm"  onkeyup='validateData(this)' maxlength="4" size="5" styleId="usedata" styleClass="text4"/>
  									</td>
  									<td valign="middle" align="left">
  										<table border="0" cellspacing="2" cellpadding="0">
											<tr><td><button type="button" id="m_up" class="m_arrow" onclick="mincrease1('usedata');">5</button></td></tr>
											<tr><td><button type="button" id="m_down" class="m_arrow" onclick="msubtract1('usedata');">6</button></td></tr>
										</table>		
  									</td>
  									<td><bean:message key="kjg.gather.chaoguo"/></td>
  									</tr></table>
  								</td></tr>
	</table>
	</td>
	<td valign='bottom' height="35px;" align="right" style="padding-left:10px;">
  				<input type='button' value='<bean:message key="kjg.title.selectall"/>' onclick='sub(true)'  class="smallbutton" style="margin-bottom:30px;">
   				<input type='button' value='<bean:message key="kjg.title.run"/>' onclick=subs('index_list')  class="smallbutton" style="margin-bottom:30px;">
   				<input type='button' value='返 回' onclick='back()'  class="smallbutton">
   			</td>
	</tr>
	</table>
</html:form>
<script language="javascript">
   //var ViewProperties=new ParameterSet();
  	MusterInitData();
</script>