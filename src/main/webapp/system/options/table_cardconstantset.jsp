<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<script langauge="javaScript">

	function changeFieldSet(){
		var v = cardConstantForm.setid.value;
		var titlev=cardConstantForm.title.value;
	  	var hashvo=new ParameterSet();
	        hashvo.setValue("fieldsetid",v);	       
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultChangeFieldSet,functionId:'1010050015'},hashvo);					
	 }
	  
    function resultChangeFieldSet(outparamters){
  		var fielditemlist=outparamters.getValue("fielditemlist");
		AjaxBind.bind(cardConstantForm.itemid,fielditemlist);
		var selectedList=outparamters.getValue("selectedList");
		AjaxBind.bind(cardConstantForm.selectedItemId,selectedList);
		var query_f=outparamters.getValue("query_f");
		var titlev=outparamters.getValue("title");
		cardConstantForm.title.value=titlev;
		var changeflag=outparamters.getValue("changeflag");		
		changediv(changeflag);
		var dateitemlist=outparamters.getValue("dateitemlist");
		AjaxBind.bind(cardConstantForm.query_field,dateitemlist);
		cardConstantForm.query_field.value=query_f;
    }
	
	//效验进行行合计的指标是否都是数值型
	function checkItem(sourcebox){
		var message = "";
		for(i=0;i<sourcebox.options.length;i++){  
			if(sourcebox.options[i].selected){				
				var v = sourcebox.options[i].value; 
				message += v;
				message +='`';
			}   	
   		}
   		if(message == ""){
   			return;
   		}
	  	var hashvo=new ParameterSet();
	    hashvo.setValue("items",message);
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultCheckItem,functionId:'1010050017'},hashvo);
	}  
	
	function resultCheckItem(outparamters){
		var info=outparamters.getValue("info");
		if(info == "ok"){
			var sourcebox = document.cardConstantForm.selectedItemId;
			for(i=0;i<sourcebox.options.length;i++){  
				if(sourcebox.options[i].selected){				
					var v = sourcebox.options[i].value; 
					var t = sourcebox.options[i].text;
					if(v.indexOf('$')!= -1){
					}else{
						sourcebox.options[i].value= v + "$";
						sourcebox.options[i].text= t +"(∑)";
					}
				}   	
   			}
		}else{
			alert("所选指标中 "+info+" 为非数值型指标!");
		}
	}
	  
	//添加着重标识(行合计功能)
	function addItemsFlag(sourcebox){
		checkItem(sourcebox);
	}
	
	//取消着重标识(行合计功能)
	function deleteItemsFlag(sourcebox){
		for(i=0;i<sourcebox.options.length;i++){  
			if(sourcebox.options[i].selected){				
				var v = sourcebox.options[i].value; 
				var t = sourcebox.options[i].text;
				if(v.indexOf('$')!=-1){
					v = v.substring(0,v.length-1);
					t = t.substring(0,t.length-3);
				}
				sourcebox.options[i].value= v;
				sourcebox.options[i].text= t ;			
			}   	
   		}
	}
	function saveRe()
	{
	  var v = cardConstantForm.setid.value;
	  var titlev=cardConstantForm.title.value;
	  if(titlev=="")
	  {
	   alert("薪酬表名不能为空！");
	   return false;
	  }else
	  {
	     var hashvo=new ParameterSet();
	     hashvo.setValue("fieldsetid",v);
	     hashvo.setValue("title",titlev);	       
	     var In_paramters="flag=1"; 	
	     var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultSaveRe,functionId:'1010050016'},hashvo);
	  }	  						
	}
	function resultSaveRe(outparamters)
	{
  		var retype=outparamters.getValue("retype");
  		if(retype=="false")
  		{
  		   alert("薪酬表名称重复请更改这个薪酬表名！");
  		   return false;
  		}else
  		{
  		   save();
  		}
	}
	function save(){
		var info = "";
		var sourcebox = document.cardConstantForm.selectedItemId;
		 var titlev=cardConstantForm.title.value;
		for(i=0;i<sourcebox.options.length;i++){  				
			var v = sourcebox.options[i].value;
			info +=v;
			info +="`";  	
		}
		
		if(info == ""||info=="`"){
			alert("已选指标不能为空!");
			return;
		}else if(titlev=="")
		{
		   alert("薪酬表名不能为空！");
	           return false;
		}else{
			var hashvo=new ParameterSet();
		        hashvo.setValue("items",info);
		        var v = cardConstantForm.setid.value;		       
		        hashvo.setValue("fieldsetid",v);
		        hashvo.setValue("title",titlev);
		        var query_field=cardConstantForm.query_field.value;
		        hashvo.setValue("query_field",query_field);
		   	var In_paramters="flag=1"; 	
			var request=new Request({method:'post',asynchronous:false,
				parameters:In_paramters,onSuccess:resultSave,functionId:'1010050019'},hashvo);	
		}
	}
	
	function resultSave(outparamters){
		var info=outparamters.getValue("info");		
		if(info == "ok"){
		var til=outparamters.getValue("title");
		var fieldsetid=outparamters.getValue("fieldsetid");
		var obj=document.getElementById("setname");
		
		for(i=0,j=0;i<obj.options.length;i++)
                {
                  if(obj.options[i].selected)
                  {
    	           var no = new Option();
    	           obj.options[i].value=fieldsetid;
    	           //obj.options[i].text=til;    	   
                  }
		}
			alert("操作成功!");
		}else{
			alert("操作失败!");
		}
	}	
	function go_up()
	{
	  cardConstantForm.action="/ykcard/cardconstantset.do?b_cardset0=link";
          cardConstantForm.submit();
	}  
	function initdb()
	{
	   var changeflag="${cardConstantForm.changeflag}";	   
	    changediv(changeflag);
	}
	function changediv(changeflag)
	{
	   if(changeflag==""||changeflag=="0")
	   {
	    
	      document.getElementById("changeflag").style.display="none";
	      //document.getElementById("changeflag1").style.display="none";
	   }else
	   {
	     document.getElementById("changeflag").style.display="block";
	     //document.getElementById("changeflag1").style.display="block";
	   }
	}
	
	  function additem$(sourcebox_id,targetbox_id)
	  {
	      var left_vo,right_vo,vos,i;
	      vos= document.getElementsByName(sourcebox_id);
	    
	      if(vos==null)
	        return false;
	      left_vo=vos[0];
	      vos= document.getElementsByName(targetbox_id);  
	      if(vos==null)
	        return false;
	      right_vo=vos[0];
	      for(i=0;i<left_vo.options.length;i++)
	      {
	        if(left_vo.options[i].selected)
	        {
	            var isExist=0;
	            for(var j=0;j<right_vo.options.length;j++)
	            {
	                if(right_vo.options[j].value==left_vo.options[i].value+"$")
	                    isExist=1;
	            }
	            if(isExist==0)
	            {
	                var no = new Option();
	                no.value=left_vo.options[i].value;
	                no.text=left_vo.options[i].text;
	                right_vo.options[right_vo.options.length]=no;
	            }
	        }
	      }
	    }
</script>
<html:form action="/system/options/cardconstantset">
	<table width="700" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<thead>
			<tr>
				<td align="left" class="TableRow" nowrap>
					选择指标&nbsp;&nbsp;
				</td>
			</tr>
		</thead>
		<tr>
			<td width="100%" align="center" class="RecordRow" nowrap>
				<table>
					<tr>
						<td align="center" width="46%">
							<table align="center" width="100%">
								<tr>
									<td align="left">
										<bean:message key="selfservice.query.queryfield" />
										&nbsp;&nbsp;
									</td>
								</tr>
								<tr>
									<td align="center">
										<hrms:optioncollection name="cardConstantForm" property="fieldSetList" collection="list" />
										<html:select name="cardConstantForm" property="setid" size="1" onchange="changeFieldSet();" styleId="setname" style="width:100%" >
											<html:options collection="list" property="dataValue" labelProperty="dataName" />
										</html:select>

									</td>
								</tr>
								<tr>
									<td align="center">
										<hrms:optioncollection name="cardConstantForm" property="fieldItemList" collection="list" />
										<html:select name="cardConstantForm" property="itemid" multiple="true" style="height:209px;width:100%;font-size:9pt" ondblclick="additem$('itemid','selectedItemId');">
											<html:options collection="list" property="dataValue" labelProperty="dataName" />
										</html:select>
									</td>
								</tr>
							</table>
						</td>
						<td width="4%" align="center">
							<input type="button" value="<bean:message key="button.setfield.addfield" />" class="smallbutton" onclick="additem$('itemid','selectedItemId');" />
							<input type="button" value="<bean:message key="button.setfield.delfield" />" class="smallbutton" onclick="removeitem('selectedItemId');" style="margin-top:30px;"/>
						</td>
						<td width="46%" align="center">
							<table width="100%">
								<tr>
									<td width="100%" align="left">
										<bean:message key="selfservice.query.queryfieldselected" />
										&nbsp;&nbsp;
									</td>
								</tr>
								<tr>
									<td width="100%" align="left">
										<hrms:optioncollection name="cardConstantForm" property="selectedItemList" collection="list" />
										<html:select name="cardConstantForm" property="selectedItemId" size="10" multiple="true" style="height:230px;width:100%;font-size:9pt" ondblclick="removeitem('selectedItemId');">
											<html:options collection="list" property="dataValue" labelProperty="dataName" />
										</html:select>
									</td>
								</tr>
							</table>
						</td>
						<td width="4%" align="center">
							<input type="button" value="<bean:message key="button.previous" />" onclick="upItem($('selectedItemId'));" class="smallbutton" />
							<input type="button" value="<bean:message key="button.next" />" onclick="downItem($('selectedItemId'));" class="smallbutton" style="margin-top:30px;"/>
						</td>
					</tr>					
					<tr>
					 
					  <td align="right" colspan="2">
					  	<table>
					  		<tr>
					  			<td>薪酬表名称</td>
					  			<td style="padding-left:5px;"><html:text name="cardConstantForm" property="title" maxlength="20" size="20" styleClass="text4"/>&nbsp;</td>
					  		</tr>
					  	</table>
					  </td>
					  <td colspan="2" align="center">
					  <div id="changeflag" style="display:none;" >	
					  <table>
					  		<tr>
					  			<td>过滤指标</td>
					  			<td style="padding-left:5px;">
					  				<hrms:optioncollection name="cardConstantForm" property="dateitemlist" collection="list"/>
									<html:select name="cardConstantForm" property="query_field" size="1">
									<html:options collection="list" property="dataValue" labelProperty="dataName" />
					  				</html:select>
								</td>
					  		</tr>
					  	</table>
					  	</div>
					  </td>
					  
					</tr>
				        
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" class="RecordRow" nowrap style="height:35px;">
			<input type="button" value="上一步" class="mybutton" onClick="go_up()" />			
			<input type="button" value="<bean:message key="button.ok" />" class="mybutton" onClick="saveRe();" />
			<input type="button" value="行合计保存" class="mybutton" onClick="addItemsFlag($('selectedItemId'));" />
			</td>
		</tr>
	</table>

</html:form>
<script language="javaScript">
	initdb();
	if(getBrowseVersion() == 10){//ie11 下调整样式  wangb 20190318
		var td = document.getElementsByName('cardConstantForm')[0].getElementsByTagName('table')[0].getElementsByTagName('table')[0].children[0].children[0].children[1];
		td.setAttribute('width','40px');
	}
</script>