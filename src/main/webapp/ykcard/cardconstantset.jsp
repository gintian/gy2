<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.ykcard.CardConstantForm" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>

<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
EncryptLockClient lock = (EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
// 移动服务权限35
boolean modApp = lock.isBmodule(35, userView.getUserName());
%>
<script language="javascript" src="/module/utils/js/template.js"></script>
<style type="text/css">
.RecordRowC {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
}
</style>
<hrms:themes></hrms:themes>
<script language="javascript">
// 设置入口 
function setFashion(fashion, orderid, codename, codeitem, elementid, mobapp) {
    if (fashion == "0") {
    	// 按单位设置
        setTableFashion(elementid, mobapp);
    } else if (fashion == "1") { 
    	// 按类型设置
        setCodeFashion(orderid, codename, codeitem, elementid, mobapp)
    }
}
 // 按单位设置
function setTableFashion(elementid, mobapp) {
    var ro_obj = document.getElementById("radio_0");
    if (ro_obj.checked == true) {
        var theurl = "/ykcard/cardconstantset.do?b_selectconstant=link&encryptParam=";
        if (elementid == "mobtable") 
        	theurl = theurl + "<%=PubFunc.encrypt("fashion_flag=0&mobapp=true")%>";
        else 
        	theurl = theurl + "<%=PubFunc.encrypt("fashion_flag=0&mobapp=false")%>"
        /*
        var return_vo = window.showModalDialog(theurl, 1, "dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
        if (!return_vo) return false;
        if (return_vo.mess != '') {
            var in_obj = document.getElementById(elementid);
            in_obj.innerHTML = return_vo.mess;
        } else {
            var in_obj = document.getElementById(elementid);
            in_obj.innerHTML = "";
        }*/
        openExtWin(theurl,elementid);
    } else {
        alert("请选择按单位显示登记表！");
    }
}
//改用Ext 弹窗显示  wangb 20190318
function openExtWin(theurl,elementid){
	 var win = Ext.create('Ext.window.Window',{
		id:'selectSalaryTable',
		title:'选择薪酬表',
		width:520,
		height:380,
		resizable:'no',
		modal:true,
		autoScoll:false,
		autoShow:true,
		autoDestroy:true,
		html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+theurl+'"></iframe>',
		renderTo:Ext.getBody(),
		listeners:{
			'close':function(){
				if (!this.return_vo) 
        			return false;
        		if (this.return_vo.mess != '') {
            		var in_obj = document.getElementById(elementid);
            		in_obj.innerHTML = this.return_vo.mess;
        		} else {
            		var in_obj = document.getElementById(elementid);
            		in_obj.innerHTML = "";
        		}
			}
		}
	 });
}


// 按类型设置
function setCodeFashion(orderid, codesetname, codeitemid, elementid, mobapp) {
    var ro_obj = document.getElementById("radio_1");
    if (ro_obj.checked == true) {
        var theurl = "/ykcard/cardconstantset.do?b_selectconstant=link&fashion_flag=1&codeitemid=" + codeitemid + "&codesetname=" + codesetname + "&orderid=" + orderid + "&codename="+$URL.encode('${cardConstantForm.codename}') + "&mobapp=" + mobapp;
        /*
        var return_vo = window.showModalDialog(theurl, 0, "dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
        //var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
        //newwindow=window.open(theurl,'_blank','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=200,left=250,width=596,height=354'); 
        if (!return_vo) 
        	return false;
        if (return_vo.mess != '') {
            var in_obj = document.getElementById(elementid);
            in_obj.innerHTML = return_vo.mess;
        } else {
            var in_obj = document.getElementById(elementid);
            in_obj.innerHTML = "";
        }
        */
        openExtWin(theurl,elementid);
    } else {
        alert("请选择按代码类型显示登记表！");
    }
}
 function strCodetype()
 {
    var ro_obj=document.getElementById("radio_1");    
    if(ro_obj.checked==true)
    {
       cardConstantForm.action="/ykcard/cardconstantset.do?b_setchild=link";
       cardConstantForm.submit(); 
      
    }else
    {
       alert("请选择按代码类型显示登记表！");
    }
 }
 function showFashion(flag)
 {
   if(flag=="")
     flag="1";
   if(flag=="1")
   {
     var waitInfo=eval("wait");	
     waitInfo.style.display="block";
     waitInfo=eval("b0110");	
     waitInfo.style.display="none";
   }else if(flag=="0")
   {
     var waitInfo=eval("wait");	
     waitInfo.style.display="none";
     waitInfo=eval("b0110");	
     waitInfo.style.display="block";
   }
 }
 function cleanStruts()
 {
    var tt="确定是要清除薪酬设置吗？";
    <%
       if(userView.isSuper_admin())
       {
    %>
        tt="确定清除所有单位的薪酬表设置吗？";
    <%  }
    %>
    if(confirm(tt))
    {
      var request=new Request({method:'post',onSuccess:showClean,functionId:'1010030014'});
    }
      
 }
 function showClean(outparamters)
 {
     var flagmess=outparamters.getValue("flagmess");  
     alert(flagmess);
     location.reload();
     
 }
</script>
<html:form action="/ykcard/cardconstantset">
	<table width="80%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable complex_border_color">
		<thead>
			<tr>
				<td align="left" style="margin-left:5px;" class="TableRow"  nowrap>
					薪酬表设置&nbsp;
				</td>
			</tr>
		</thead>
<%
String ClientName=SystemConfig.getPropertyValue("clientName") ;
boolean readonly = true;
%>
<% if(ClientName.equalsIgnoreCase("FHJT")){ %>
	<hrms:priv func_id="3001520">
		<% readonly = false; %>
	</hrms:priv>
<% }else{
	readonly = false;
} %>
		<tr>
		     <td height="30" class="RecordRowC  common_border_color" nowrap >
		         <table width="90%" border="0" cellpadding="0" cellspacing="0">
		           <tr>
		             <td  width="15%" nowrap>
		               &nbsp;
		              </td>
		              <td width="20%" align="right">
		                &nbsp; 选择取数的方式&nbsp;
		              </td>
			       <td width="65%" nowrap style="padding-left:5px;">
					<% if(readonly){ %>
							<html:select name="cardConstantForm" property="type" size="1" disabled="true">
								<html:option value="0">按条件取数&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
								<html:option value="1">按时间取数</html:option>
							</html:select>
					<% }else{ %>
							<html:select name="cardConstantForm" property="type" size="1">
								<html:option value="0">按条件取数&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
								<html:option value="1">按时间取数</html:option>
							</html:select>
					<% } %>
			     </td>			     
		           </tr>
		         </table>
		        </td>
		        
		</tr>
		<tr>
		      <td height="30" class="RecordRowC  common_border_color" nowrap >
		         <table width="90%" border="0" cellpadding="0" cellspacing="0">
		           <tr>
		             <td  width="15%" nowrap>
		               &nbsp;
		              </td>
		              <td width="20%" align="right">
		                &nbsp;选择显示的方式&nbsp;
		              </td>
			       <td width="65%" nowrap>
					<% if(readonly){ %>
							<html:radio name="cardConstantForm" property="fashion_flag" styleId="radio_0" onclick="showFashion('0');" value="0" disabled="true"/>&nbsp; 按单位显示登记表
							<html:radio name="cardConstantForm" property="fashion_flag" styleId="radio_1" onclick="showFashion('1');" value="1" disabled="true"/> &nbsp; 按类型显示登记表
					<% }else{ %>
							<html:radio name="cardConstantForm" property="fashion_flag" styleId="radio_0" onclick="showFashion('0');" value="0"/>&nbsp; 按单位显示登记表
							<html:radio name="cardConstantForm" property="fashion_flag" styleId="radio_1" onclick="showFashion('1');" value="1"/> &nbsp; 按类型显示登记表
					<% } %>
			       </td>			       
		           </tr>
		         </table>
		        </td>
		        
	        </tr>
	        <tr>
	         <td class="RecordRow" nowrap>
		  <table width="100%" border="0" cellpadding="0" cellspacing="0">	
		   <tr>
		    <td nowrap>		      
		      <div id='b0110' style='width="100%";display:none;'>
		      <br>
		          <table width="60%" border="0" align="center" cellpadding="0" cellspacing="0">
		           <tr>
		               <td width="30%">
		                  选择分类指标
		               </td>
			           <td width="70%" nowrap>
				        <html:select name="cardConstantForm" property="relating" size="1">
                           <html:optionsCollection property="relatinglist" value="dataValue" label="dataName"/>	        
                        </html:select> 
                        &nbsp;&nbsp;<font color="#000000">*关联代码类UN的指标</font> 
			           </td>
		           </tr>
		         </table>
		         <br>		     
		    	 <% if (modApp) { //购买移动的用户显示 %>
						<fieldset align="center" style="width:60%;">
							<legend>按关联代码类UN的登记表</legend>
							<br>
							<fieldset align="center" style="width:60%;">
								<legend>电脑使用</legend>
								<table width="90%" border="0" cellpadding="0" cellspacing="0" style="margin-bottom:5px;">
									<tr>
										<td width="20%" align="right"></td>
										<td width="60%" id="table1" nowrap>
											<bean:write name="cardConstantForm" property="cardidmess" filter="false" />
										</td>
										<td width="15%">
											<input type="button" name="b_1" class="mybutton" value="设置" onclick="setFashion('0','0','0','','table1','false');">
										</td>
									</tr>
								</table>
							</fieldset>
							<br>
							<fieldset align="center" style="width:60%;">
								<legend>手机使用</legend>
								<table width="90%" border="0" cellpadding="0" cellspacing="0" style="margin-bottom:5px;">
									<tr>
										<td width="20%" align="right"></td>
										<td width="60%" id="mobtable" nowrap>
											<bean:write name="cardConstantForm" property="cardidmessapp" filter="false" />
										</td>
										<td width="15%">
											<input type="button" name="b_1" class="mybutton" value="设置" onclick="setFashion('0','0','0', '', 'mobtable', 'true');">
										</td>
									</tr>
								</table>
							</fieldset>
							<br><br>
						</fieldset>
					<% } else { %>
						<fieldset align="center" style="width:60%;">
							<legend>按关联代码类UN的登记表</legend>
							<table width="90%" border="0" cellpadding="0" cellspacing="0"
								style="margin-bottom:5px;">
								<tr>
									<td width="20%" align="right"></td>
									<td width="60%" id="table1" nowrap>
										<bean:write name="cardConstantForm" property="cardidmess" filter="false" />
									</td>
									<td width="15%">
										<input type="button" name="b_1" class="mybutton" value="设置" onclick="setFashion('0','0','0','', 'table1','false');">
									</td>
								</tr>
							</table>
						</fieldset>
					<% } %>
		          <br>
		         <div>
		       </td>		        
		     </tr>  
		     <tr>
		      <td nowrap>
		      <%  CardConstantForm cardConstantForm=(CardConstantForm)session.getAttribute("cardConstantForm");
                  ArrayList cardnomesslist=cardConstantForm.getCardnomesslist();
                  int i = 0;
                  int app = 0;
               %>
		       <div id='wait' style='width="100%";display:none;'>
		       <br>
		       <% if (modApp) { //购买移动的用户显示 %>
		       <fieldset align="center" style="width:60%;">
                   <legend >按类型显示登记表</legend>
                   <br>
		           <table width="90%" border="0" cellpadding="0" cellspacing="0">
		           	<tr>
		              <td width="20%" align="right"/>
		              <td width="60%" nowrap height="35" valign="middle">
		              	<hrms:importgeneraldata showColumn="itemdesc" valueColumn="codesetid" flag="true" paraValue="" sql="select itemid,itemdesc from  fielditem where useflag=1 and fieldsetid='A01' and  codesetid<>'0' and itemid not in('B0100','E0122','E01A1') and codesetid not in('UN','UM')" collection="list" scope="page" />
							<html:select name="cardConstantForm" property="codename" size="1" onchange="strCodetype();">
								<html:option value="#">请选择...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
								<html:options collection="list" property="dataValue" labelProperty="dataName" />
							</html:select>
					 </td>
			         <td width="15%">&nbsp;</td>
		            </tr>
		            </table>
		            <fieldset align="center" style="width:70%;">
							<legend>电脑使用</legend>
						<table width="90%" border="0" cellpadding="0" cellspacing="0">
		          			<tr>
		          			<td width="25%" align="right" nowrap>&nbsp;</td>
							<td nowrap>
								<table width="100%" border="0" cellpadding="0" cellspacing="0">
									<logic:iterate id="element" name="cardConstantForm" property="codesetlist" indexId="index">
										<tr>
											<td height="28">&nbsp;
												<hrms:codetoname codeid="${element.dataName}" name="element" codevalue="dataValue" codeitem="codeitem" scope="page" />
												<bean:write name="codeitem" property="codename" />:&nbsp;
											</td>
											<td id="${element.dataValue}">
												<%=(String) cardnomesslist.get(i)%>
											</td>
											<td>
												<input type="button" name="b_1" class="mybutton" value="设置" 
												onclick="setFashion('1','<%=i%>','${element.dataName}','${element.dataValue}','${element.dataValue}','false');">
											</td>
											</tr>
											<% i++; %>
										</logic:iterate>
									</table>
								</td>
							</tr>
		               	</table>
		             </fieldset>
		             <br>
		             <% ArrayList mobCardNoMessList = cardConstantForm.getMobcardnomesslist(); %>
		             <fieldset align="center" style="width:70%;">
							<legend>手机使用</legend>
						<table width="90%" border="0" cellpadding="0" cellspacing="0">
		          			<tr>
		          			<td width="25%" align="right" nowrap>&nbsp;</td>
							<td nowrap>
								<table width="100%" border="0" cellpadding="0" cellspacing="0">
									<logic:iterate id="element" name="cardConstantForm" property="codesetlist" indexId="index">
										<tr>
											<td height="28">&nbsp;
												<hrms:codetoname codeid="${element.dataName}" name="element" codevalue="dataValue" codeitem="codeitem" scope="page" />
												<bean:write name="codeitem" property="codename" />:&nbsp;
											</td>
											<td id="app${element.dataValue}">
												<%=(String) mobCardNoMessList.get(app)%>
											</td>
											<td>
												<input type="button" name="b_1" class="mybutton" value="设置" 
												onclick="setFashion('1','<%=app%>','${element.dataName}','${element.dataValue}','app${element.dataValue}','true');">
											</td>
											</tr>
											<% app++; %>
										</logic:iterate>
									</table>
								</td>
							</tr>
		               	</table>
		             </fieldset>
		             <br><br>
		  		</fieldset>
		       <% } else { // 没有购买移动的用户显示 %>
		       <fieldset align="center" style="width:60%;">
                   <legend >按类型显示登记表</legend>
		           <table width="90%" border="0" cellpadding="0" cellspacing="0">
		           	<tr>
		              <td width="20%" align="right"/>
		              <td width="60%" nowrap height="35" valign="middle">
		              	<hrms:importgeneraldata showColumn="itemdesc" valueColumn="codesetid" flag="true" paraValue="" sql="select itemid,itemdesc from  fielditem where useflag=1 and fieldsetid='A01' and  codesetid<>'0' and itemid not in('B0100','E0122','E01A1') and codesetid not in('UN','UM')" collection="list" scope="page" />
							<html:select name="cardConstantForm" property="codename" size="1" onchange="strCodetype();">
								<html:option value="#">请选择...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
								<html:options collection="list" property="dataValue" labelProperty="dataName" />
							</html:select>
					 </td>
			         <td width="15%">&nbsp;</td>
		            </tr>
	          		<tr>
	          			<td width="25%" align="right" nowrap>&nbsp;</td>
						<td nowrap>
							<table width="100%" border="0" cellpadding="0" cellspacing="0">
								<logic:iterate id="element" name="cardConstantForm" property="codesetlist" indexId="index">
									<tr>
										<td height="28">&nbsp;
											<hrms:codetoname codeid="${element.dataName}" name="element" codevalue="dataValue" codeitem="codeitem" scope="page" />
											<bean:write name="codeitem" property="codename" />:&nbsp;
										</td>
										<td id="${element.dataValue}">
											<%=(String) cardnomesslist.get(i)%>
										</td>
										<td>
											<input type="button" name="b_1" class="mybutton" value="设置" 
											onclick="setFashion('1','<%=i%>','${element.dataName}','${element.dataValue}','${element.dataValue}', 'false');">
										</td>
										</tr>
										<% i++; %>
									</logic:iterate>
								</table>
							</td>
						</tr>
		               	</table>
		              </fieldset>
		            <% } %>
		               <br>
		             </div>
		             <br>
		          </td>
		         </tr>
		     </table>
		   </td>
		</tr>
		
		<tr>
			<td align="center"  class="RecordRow" style="height: 35px" nowrap>
				&nbsp;&nbsp;
				<input type="submit" name="b_cardsetsucceed" class="mybutton" value="&nbsp;确定&nbsp;">
			        <input type="button" name="btnreturn" value='清除设置' class="mybutton" onclick=" cleanStruts();">       	      	       
			</td>
		</tr>
	</table>

</html:form>
<script language="javascript">
 showFashion('${cardConstantForm.fashion_flag}');	
 //if(!getBrowseVersion() || getBrowseVersion() == 10){//非ie兼容模式下 样式修改  wangb 20190318
 	var fieldset = document.getElementsByTagName('fieldset');
 	for(var i = 0 ; i < fieldset.length ;i++){
 		fieldset[i].style.margin ='0 auto';
 	}
 //}
</script>