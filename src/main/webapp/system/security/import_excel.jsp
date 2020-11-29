<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language='javascript'>
  //导入
  function imports(){
  	var dir=sysForm.importfile.value;
  	if(trim(dir).length==0){
  		alert("<bean:message key='kjg.title.importdocuments'/>");
  		return;
  	}
  	var index = dir.lastIndexOf(".");
  	if(!(dir.substring(index)==".xls"||dir.substring(index)==".xlsx")){
  		alert("<bean:message key='kjg.title.fileexpansionname'/>");
  		return;
  	}
  	var waitInfo=eval("wait");	
	waitInfo.style.display="block";
   sysForm.action="/system/security/rolesearch.do?b_upload=upload&opt=2";
   sysForm.submit();
  }
  function goback(){
  	window.location="/system/security/rolesearch.do?b_query=link";
  }
  function initwindow(){
	  
   <%if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("0")){%>
     
   <%}
   else if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("2")){
   %>
      var info="${sysForm.info}";
      var returnInfo="${sysForm.returnInfo}";
      if(info=="0"){
      	alert("导入完成!");
      	if(window.showModalDialog){
  	 	// var ab = new Object();
      		top.returnValue="aa";
          	top.close();
  		}else{
  			parent.parent.commonWinSuccess(true);
  		}
      	
      }
      else
      {
          alert(returnInfo);
      }
  <%}%>
}
function goback(){
	/*var obj = new Object();
   obj.fresh="0";
   top.returnValue=obj;*/
   top.close();
   if(window.showModalDialog){
	   top.close();
   }else{
   		parent.parent.closeWin();
   }
}
</script>
  	<html:form  action="/system/security/rolesearch" enctype="multipart/form-data">
  	<div id='wait' style='position:absolute;top:10;left:50;display:none;'>
		<table border="1" width="67%" cellspacing="0" cellpadding="0" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					正在导入，请稍候...
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
	</div>
  		<table width="390" border="0" cellspacing="0"  align="center" cellpadding="0">
  		<tr>
  		<td align="center" nowrap>
  		<fieldset align="center">
  			<legend ><bean:message key="jx.import.selectfile"/></legend>
  			<table width="100%" height="50px" border="0" cellspacing="0"  align="center" cellpadding="0">
  				<tr>
  					<td align="center" nowrap><!-- 【7109】进入“角色管理”界面，点击”导入权限分配表“，出现的界面，框内背景颜色不对  jingq add 2015.02.03 -->
              		<input type="file" name="importfile" style="background: #FFF;" size="30" class="complex_border_color">
              		</td>
  				</tr>
  				
  			</table>
  		</fieldset>
  		</td>
  		</tr>
  		<tr style="height: 35px">
                    <td align="center" nowrap> 
                        <input type="button" name="b_update" value="<bean:message key='hire.jp.apply.upload'/>" class="mybutton" onClick="imports()">
                        <input type="button" name="b_date" value="<bean:message key='kq.register.kqduration.cancel'/>" class="mybutton" onClick="goback()">
                    </td>
                </tr>
  		</table>
  <script type="text/javascript">
	initwindow();
  </script>
</html:form>
