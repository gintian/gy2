<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language='javascript'>
	function setState(flag)
	{
		var obj=eval("document.signUpAuditingForm.selected");
		var n=0;
		if(obj)
		{
			if(obj.length)
			{		
				for(var a=0;a<obj.length;a++)
				{
					if(obj[a].checked==true)
						n++;
				}
			}
			else
			{
				if(obj.checked==true)
					n++;
			}
		}
		if(n==0)
		{
			alert(PLASE_SELECT_RECORD);
		}
		else
		{
			document.signUpAuditingForm.action="/train/signUp/browseSignUpAuditingList.do?b_setState=query&state="+flag;
			document.signUpAuditingForm.submit();
		}
	
	}
	
	
	function search()
	{
		document.signUpAuditingForm.action="/train/signUp/browseSignUpAuditingList.do?b_query=search&operate=init";
		document.signUpAuditingForm.submit();
	}
	
	
	function delegateRegister()
	{
		var classid = document.signUpAuditingForm.trainMovementID2.value;
		if(classid=="")
		{
			alert(SELECT_TRAIN_CLASS);
			return;
		}

		var theurl="/train/request/selectpre.do?b_query=link`itemkey=" + classid + "`nbase=all`preflag=1";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
	   	var return_vo= window.showModalDialog(iframe_url, 'trainClass_win2', 
	      				"dialogWidth:600px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");	
	    if(return_vo!=null&&return_vo.length>0){
	    	 var hashvo=new ParameterSet();
	     	hashvo.setValue("personstr",return_vo);
	     	hashvo.setValue("classid",classid);
	     	hashvo.setValue("msg","4");
	     	var request=new Request({method:'post',asynchronous:false,onSuccess:savePerson,functionId:'2020040012'},hashvo);
	     	function savePerson(outparamters){
	     		var flag=outparamters.getValue("flag");
	    		if(flag=="true"){
	    			var hashvo=new ParameterSet();
					hashvo.setValue("personstr",return_vo);
					hashvo.setValue("r3101",classid);
					var request=new Request({onSuccess:returnInfo,asynchronous:false,functionId:'2020020208'},hashvo);
	    		}else{
					alert(flag);
	            }
	     	}
		}
	}
	
	 function returnInfo(outparamters)
  	 {
  	 	alert(outparamters.getValue("info"));
 		document.signUpAuditingForm.action="/train/signUp/browseSignUpAuditingList.do?b_query=link";
		document.signUpAuditingForm.submit();
   		
   	 }

	 function showSelect(){
		 var options = document.getElementById("selectE").options;
		 if(options.length != alloptions.length ){
			 
			 initSelectOptions(options,alloptions);
		 }
		 document.getElementById("selectDiv").style.display="block";
	 }
	 function selectTrain(selectObj){
		 if(selectObj.selectedIndex <0 ){
			 return;
		 }
		 var trainName = selectObj.options[selectObj.selectedIndex].text;
		 document.getElementById("trainName").value=trainName;
		 document.getElementById("selectDiv").style.display="none";
	 }
	 
	 function filterOption(obj){
		 var value = obj.value; 
		 if(value==null || value == ""){
			 showSelect();
			 var selectO = document.getElementById("selectE");
			 selectO.options[selectO.selectedIndex].selected = false; 
			 selectO.options[0].selected = true;
			 return;
		 }
		 var Opt = document.getElementById("selectE").options;
		 var objs = new Array();
		 for(var i=0; i< alloptions.length;i++){
			 var text = alloptions[i].text;
			 if(text.indexOf(value) != -1){
				 objs.push(alloptions[i]);
			 }
		 }
		 initSelectOptions(Opt,objs);
	}
	 
	 function initSelectOptions(/* Object(select).options */targetOption,/* Array[options]*/dataArray){
		 while(true){
			 if(targetOption.length>0)
				 targetOption.remove(0);
			 else
				 break;
		 }
		 //alert(dataArray.length);//return;
		 for(var i=0;i<dataArray.length;i++){
			 targetOption.add(dataArray[i]); 
			 if(targetOption.length==dataArray.length)
				 break;
		 }
	 }
</script>
<html:form action="/train/signUp/browseSignUpAuditingList">
<table width="100%" cellspacing="0" cellpadding="0">
 <tr>
   <td align="left" >
		<table cellspacing="0" cellpadding="0">
		<tr>
		<td>
		    <bean:message key="train.job.trainClassName"/>&nbsp;<input type="text" id="trainName" class="TEXT4" onclick="showSelect()" style="width:200px;" onkeyup="filterOption(this);"/>
&nbsp;
		    <div id="selectDiv" style="display:none;position:absolute;top:35;left:69px;">
	        	<html:select name="signUpAuditingForm" property="trainMovementID"  styleId="selectE" size="1" multiple="true" style="width:200px;height:200px;" onchange="selectTrain(this)" onblur="document.getElementById('selectDiv').style.display='none';">
	                <html:optionsCollection property="trainMovementList" value="dataValue" label="dataName"/>
	        	</html:select>
        	</div>
	       	<bean:message key="column.sys.status"/>&nbsp;
       	</td>
       	<td>
	       	<html:select name="signUpAuditingForm" property="sp_flag">
	       	   <html:optionsCollection property="spList" value="codeitem" label="codename"/>
	       	</html:select>
	    </td>
	    <td>
	       	&nbsp;&nbsp;
	       	<bean:message key="hire.employActualize.name"/>&nbsp;<input type='text' name="a0101" class="TEXT4" value="${signUpAuditingForm.a0101}" >&nbsp;&nbsp;
		</td>
		<td>
        	<Input type='button' value="<bean:message key='infor.menu.query'/>" onclick='search()' style="margin-bottom: 0px;"  class="mybutton"  />  		
		</td>
		</tr>
		</table>
   </td>
 </tr>
 <tr>
   <td width="100%" onclick='document.getElementById("selectDiv").style.display="none";'>
     <div class="fixedDiv2" style="margin-top: 5px;"> 
     <table width="100%" border="0" cellspacing="0" cellpadding="0">
   	    <thead>
           <tr class="fixedHeaderTr">
            <td align="center" class="TableRow" style="border-left: none;border-top:none;" nowrap>
			  &nbsp;<input type="checkbox" name="box" id="checkAll" onclick="batch_select_all(this);" title='<bean:message key="button.all.select"/>'/>&nbsp;
		    </td>
		     <td align="center" class="TableRow" style="border-left: none;border-top:none;"  nowrap>
			<bean:message key="b0110.label"/>&nbsp;
		    </td>
	        <td align="center" class="TableRow" style="border-left: none;border-top:none;"  nowrap>
			<bean:message key="e0122.label"/>&nbsp;
		    </td>
		    <td align="center" class="TableRow" style="border-left: none;border-top:none;"  nowrap>
			<bean:message key="e01a1.label"/>&nbsp;
		    </td>  
	    	<td align="center" class="TableRow" style="border-left: none;border-top:none;"  nowrap>
			<bean:message key="hire.employActualize.name"/>&nbsp;
		    </td>
		    <td align="center" class="TableRow" style="border-left: none;border-top:none;"  nowrap>
			<bean:message key="sys.res.trainjob"/>&nbsp;
		    </td>
		    <td align="center" class="TableRow" style="border-right: none;border-left: none;border-top:none;"  nowrap>
			<bean:message key="label.zp_resource.status"/>&nbsp;
		    </td>   	        	        
           </tr>
   	    </thead>
<% String className="trShallow"; 
   int i=0;
 %>
		<hrms:extenditerate id="element" name="signUpAuditingForm" property="studentListForm.list" indexes="indexes" pagination="studentListForm.pagination" pageCount="12" scope="session">
		
		<%i++;
			if(i%2==0)
				className="trDeep";
			else
				className="trShallow";
		%>
			
			<tr  class="<%=className%>" >    
	            <td align="center" class="RecordRow"  style="border-left: none;border-top:none;"  nowrap>
	              &nbsp;
				   <logic:equal name="element" property="flag" value="1">
				   	<input type="checkbox" name='selected' value='<bean:write  name="element" property="a0100"/>/<bean:write  name="element" property="r3101"/>/<bean:write  name="element" property="dbname"/>' />
				   </logic:equal>
				   &nbsp;
			    </td>
		    	<td align="left" class="RecordRow"  style="border-left: none;border-top:none;"  nowrap>
				  &nbsp; <bean:write  name="element" property="b0110"/>&nbsp;
			    </td>
		    	<td align="left" class="RecordRow"  style="border-left: none;border-top:none;"  nowrap>
				 &nbsp;  <bean:write  name="element" property="e0122"/>&nbsp;
			    </td>
		       	<td align="left" class="RecordRow"  style="border-left: none;border-top:none;"  nowrap>
				 &nbsp;  <bean:write  name="element" property="e01a1"/>&nbsp;
			    </td>
			    <td align="left" class="RecordRow"  style="border-left: none;border-top:none;"  nowrap>
				 &nbsp;  <bean:write  name="element" property="a0101"/>&nbsp;
			    </td>
			    <td align="left" class="RecordRow"  style="border-left: none;border-top:none;"  nowrap>
			    	<a href="/train/job/browseTrainClassList.do?b_queryDesc=query&operator=3&r3101=<bean:write  name="element" property="r3101"/>" >
				&nbsp;   <bean:write  name="element" property="r3130"/>
			    	</a>&nbsp;
			    </td>
			    <td align="left" class="RecordRow" style="border-left: none;border-top:none;border-right: none;"  nowrap>
				&nbsp;   <bean:write  name="element" property="r4013"/>&nbsp;
			    </td>
			           	        
           </tr>
		</hrms:extenditerate>	
		</table>
		</div>
   <table width="100%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				<bean:message key="label.page.serial"/>
				<bean:write name="signUpAuditingForm" property="studentListForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum"/>
				<bean:write name="signUpAuditingForm" property="studentListForm.pagination.count" filter="true" />
				<bean:message key="label.page.row"/>
				<bean:write name="signUpAuditingForm" property="studentListForm.pagination.pages" filter="true" />
				<bean:message key="label.page.page"/>
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="signUpAuditingForm" property="studentListForm.pagination" nameId="studentListForm">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>				
	<table width="100%" align="left" style="margin-top: 8px;">
		<tr><td>
				<Input type="button" style="margin-bottom: 0px;" value='<bean:message key="approve.personinfo.oks"/>' name="confirm" class="mybutton" onclick='setState("02")' />
				<Input type="button" style="margin-bottom: 0px;" value='<bean:message key="info.appleal.state2"/>' name="reject" class="mybutton" onclick='setState("07")' />	
		
				&nbsp;&nbsp;&nbsp;&nbsp;
				<html:select name="signUpAuditingForm" property="trainMovementID2"  size="1" >
                              <html:optionsCollection property="trainMovementList2" value="dataValue" label="dataName"/>
        		</html:select>
		        <Input type="button" style="margin-bottom: 0px;" value="<bean:message key="train.job.app.behalf"/>" name="reject" class="mybutton" onclick='delegateRegister()' />	
		
		</td></tr>
	</table>
	</td>
 </tr>
 </table>
</html:form>
<script>
   var alloptions = new Array();
   var options = document.getElementById("selectE").options;
   for(var i=0;i<options.length;i++){
	   alloptions[i] = options[i];
   }
   selectTrain(document.getElementById("selectE"));
   
</script>