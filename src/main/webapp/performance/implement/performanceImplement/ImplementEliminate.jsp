<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script>
var passvalue="";
var flag=true;

	function test2(){
		document.getElementsByName("deleteplanid")[0].disabled=false;
   		flag=false;
	}
	
	function test1(){
   		document.getElementsByName("deleteplanid")[0].disabled=true;
   		flag=true;
	}
	
	function delallvalue(){
	  var allids='';
	  var bb =document.getElementsByName("deleteplanid")[0];
		for(var ii=0;ii<bb.options.length;ii++){
			allids+=bb.options[ii].value;
				if(ii!=bb.options.length-1){
    				allids=allids+","
 				}
 		}
 		passvalue =allids;

	}
	 
	function delvalue(){ 
		if(flag){
			delallvalue();
		}else{
        var selids = "";
        var b =document.getElementsByName("deleteplanid")[0];
		for(var i=0;i<b.options.length;i++){
			if(b.options[i].selected){
				if (selids==""){
					 selids+=b.options[i].value;
				}
				 else{
					selids+=","+b.options[i].value;
				 }
			}
		}
			passvalue =selids;

		 }   
			passdelvalue();

	}

	/**
     *关闭窗口
     */
	function closeWindow(){
        if(window.showModalDialog){
            window.close();
        }else{
            parent.parent.Ext.getCmp("valuedeleteWin").close();
        }
    }

	function passdelvalue(){
		var planid = document.getElementById("planid").value;
		if(passvalue==""){
   			alert(KHSS_JHSS_XZNAME);
   				return;
		}
		var info = DELETE_MAINBODY_SCORE1;
		if(document.getElementById('rad1').checked)
			info=DELETE_MAINBODY_SCORE2;
		else if(document.getElementById('rad2').checked)
			info=DELETE_MAINBODY_SCORE1;
		if(confirm(info))
		{
			implementForm.paramStr.value=passvalue;
			implementForm.action="/performance/implement/performanceImplement.do?b_deta=link&planid="+planid;
			implementForm.submit();
		}
		/*
 		target_url="/performance/implement/performanceImplement.do?b_deta=link&delids="+passvalue+"&planid="+planid;
				if(confirm(KHSS_JHSS_YSEDETA)){
					var ww=window.open(target_url);
	    			ww.close();
	    			window.close();
				} */
				
	}
	<%if(request.getParameter("b_deta")!=null){%>
        closeWindow();
	<%}%>
</script>
<html:form action="/performance/implement/performanceImplement">
	<html:hidden name="implementForm" property="paramStr"/>
	<table align="center" border="0" cellpadding="1"
		cellspacing="1" style="margin-top:-10px">
		<tr><html:hidden name="implementForm" property="planid" styleId="planid"/></tr>
		<tr>
			<td width='250'>
				<br>
				<fieldset align="center" style="width:240px;height:293px">

					<table width="100%" align="center" border="0" cellpadding="1"
						cellspacing="">
						<tr>
							<td>
								<input type="radio" id="rad1" name="rad" value="delall" checked
									onclick="test1()">
								<bean:message key='lable.performance.show.purgehaving1' />
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" id="rad2" name="rad" value="seldel"
									onclick="test2();">
								<bean:message key='lable.performance.show.purgeselect1' />
							</td>
						</tr>
						<tr>
							<td>
								<br>
								<span id="datepnl"> 
								&nbsp;<html:select name="implementForm"
										property="deleteplanid" size="10" disabled="true"
										multiple="true" style="height:230px;width:80%;font-size:9pt">
										<html:optionsCollection property="planidselect"
											value="dataValue" label="dataName" />
									</html:select> </span>
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
			<td valign='top' width="80">
				<br>
				<br>
				&nbsp;
				<input type="button" class="mybutton" name="dela" value="确 定"
					onclick="delvalue();" />&nbsp;&nbsp;
				<br>
				<br>
				&nbsp;
				<input type="button" class="mybutton" value="取 消"
					onClick="closeWindow();">&nbsp;&nbsp;
			</td>
		</tr>
	</table>
</html:form>
