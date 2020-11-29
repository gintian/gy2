<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hrms.struts.constant.WebConstant,
				 com.hjsj.hrms.module.recruitment.parameter.actionform.ParameterForm, com.hrms.struts.valueobject.UserView,
				 org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.sys.EncryptLockClient,
				 com.hrms.struts.taglib.CommonData,com.hrms.hjsj.sys.ResourceFactory" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<html>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/components/personPicker/PersonPicker.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/module/recruitment/parameter/setBusiParam.js"></script>
<script language="JavaScript" src="/components/codeSelector/codeSelector.js"></script>
<script language="JavaScript" src="/components/tableFactory/tableFactory.js"></script>
<script language="JavaScript" src="/module/utils/js/createWindow.js"></script>
<script language="JavaScript" src="../../../../../components/dateTimeSelector/dateTimeSelector.js"></script> 
<script type="text/javascript" src="/components/codeSelector/deepCodeSelector.js"></script>
<script language="JavaScript" src="../../../module/recruitment/recruitment_resource_zh_CN.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script> 
<link href="/module/recruitment/css/style.css" rel="stylesheet"	type="text/css" />
<link href="/components/personPicker/PersonPicker.css" rel="stylesheet" type="text/css"><link >

<script language="javascript">
    var codelist;
    var codeId;
    var destStore;
	function showFieldItems(fieldsetid,alreadyExits){
		Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'EHR': '/components'
			}
		});
		Ext.require('EHR.fielditemmultiselector.Selector', function(){
			Ext.create("EHR.fielditemmultiselector.Selector",{fieldset:fieldsetid,items:Ext.encode(),afterfunc:''});
		});
	}
	
	//屏幕分辨率
  	var screenHeight =  window.screen.height;
  	var screenWidth = window.screen.width;
  	
	var gloleft = (window.screen.availWidth-540-10)/2;//计算窗口距离屏幕左侧的间距 540是窗口宽度，10是边框大小
	var glotop = (window.screen.availHeight-400-30)/2;//窗口距离屏幕上方的间距         400是窗口高度，30是边框和标题栏大小(20)
	function isIE() { //ie?  
	    if (!!window.ActiveXObject || "ActiveXObject" in window)  
	        return true;  
	    else  
	        return false;  
	 }

	function createWindow(thecodeurl,funcMethod,flag,width,height){
	   var values="";
	   width=Ext.isEmpty(width)?540:width;
	   height=Ext.isEmpty(height)?400:height;
	   var aleft = Ext.isEmpty(width)?gloleft:(2*gloleft-width+540)/2;
	   var atop = Ext.isEmpty(height)?glotop:(2*glotop-height+400)/2;
	   if(Ext.isChrome){//chrome浏览器
		   values=window.open(thecodeurl, "", 
	               "width="+width+"px,height="+height+"px,top="+atop+",left="+aleft+",resizable=no,center=yes,scroll=yes,location=no,status=no");
	   }else if(!isIE()&&!Ext.isChrome){//非ie和chrome  主要针对火狐和safari中弹窗的位置
		   values=window.open(thecodeurl, "", 
	               "width="+width+"px,height="+height+"px,screenY="+atop+",screenX="+aleft+",resizable=no,center=yes,scroll=yes,location=no,status=no");
	   }else{
		   values=window.showModalDialog(thecodeurl,null,"dialogWidth:540px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
		   eval(funcMethod);
	   }
	}
	
	function structureAttachHire()
	{
	    var datas = Ext.getCmp('channelGridpanel').store.config.data;
        var list = Ext.getCmp('channelGridpanel').store.config.data;
        var fields = Ext.getCmp('channelGridpanel').store.config.fields;
        var linkMap = new HashMap();
        var sonMap = new HashMap();
        for (var i = 0; i < list.length; i++) {
            sonMap = new HashMap();
            var listBean = list[i];
            var codelistBean = this.codelist[i];
            var itemid = "";
            for (var j = 0; j < fields.length; j++) {
                field = fields[j];
                var value;
                if (j == 0) 
                    value = codelistBean.dataValue;
                else value = listBean[field];
                if (0 == j) {
                    itemid = value;
                    continue;
                }
                sonMap.put(field, value);
            }
            linkMap.put(itemid, sonMap);
        }
        var attachHireValue = JSON.stringify(linkMap);
        document.moudleParameterForm.attachHire.value = attachHireValue;
	}
	
	function sub()
	{
		if(codeId=="#"){
	        document.moudleParameterForm.attachHire.value ="0";
	    }else{
	        structureAttachHire();
	    }
		
		var value="";
		var markType="";
		var testTemplateIDs=document.getElementsByName("testTemplateIDs");
		var zhengzhengshu=/^[0-9]*[1-9][0-9]*$/;///正整数
		var zhengshu=/^[1-9]\d*|0$/;  ///正数
		var passwordMinLength=Ext.getDom("passwordMinLength").value;
		var passwordMaxLength=Ext.getDom("passwordMaxLength").value;
		var failedTime=Ext.getDom("failedTime").value;
		var unlockTime=Ext.getDom("unlockTime").value;
		if(Ext.getDom("complexPassword").checked){
			if(!zhengzhengshu.test(passwordMinLength)){
				alert("密码最小长度请输入正整数!");
				return;
			}else{
				if(passwordMinLength<3){
					alert("密码最小长度不能小于3!");
					return;
				}
			}
			if(!zhengzhengshu.test(passwordMaxLength)){
				alert("密码最大长度请输入正整数!");
				return;
			}else{
			if(passwordMaxLength<=3){
					alert("密码最大长度必须大于3!");
					return;
				}
			if(passwordMaxLength>30){
				alert("密码最大长度不能大于30!");
				return;
			}
		 }		
		}
		
		if(!zhengshu.test(failedTime)){
			alert("最大登录失败次数请输入非负整数!");
			return;
		}
		if(zhengzhengshu.test(failedTime)&&!zhengzhengshu.test(unlockTime)){
			alert("解锁时间间隔请输入正整数!");
			return;
		}
		
	    var resumeAnalysisName=Ext.getDom("resumeAnalysisName");//解析服务用户名
	    var resumeAnalysisPassword=Ext.getDom("resumeAnalysisPassword");//解析服务密码
	    var resumeAnalysisForeignJob=Ext.getDom("resumeAnalysisForeignJob");//对外应聘职位
		for(var i=0;i<testTemplateIDs.length;i++)
		{
			value+="~"+testTemplateIDs[i].value;
			var obj=document.getElementsByName("markType"+i);
			for(var j=0;j<obj.length;j++){
			if(obj[j].checked){
			markType+="#"+ obj[j].value;
			}
			}
		}
		var checkInteger = /^[1-9]\d*$/;
		 var v = moudleParameterForm.max_count.value;
		 if(trim(v).length>0)
		 {
		    if(!checkInteger.test(v))
		    {
		      alert(POSITION_MAX_COUNT+"！");
		      return;
		    }
		 }
		if(Ext.getDom("startResumeAnalysis")!=null&&Ext.getDom("startResumeAnalysis").checked){
			if(Ext.getDom("resumeAnalysisName").value==""){
				alert("简历解析服务用户名不能为空！");
				return;
			}
			if(Ext.getDom("resumeAnalysisPassword").value==""){
				alert("简历解析服务密码不能为空！");
				return;
			}
		}	
		if(Ext.getDom("startResumeAnalysis")!=null&&!Ext.getDom("startResumeAnalysis").checked){
			Ext.getDom("startResumeAnalysis").value="0";
			Ext.getDom("startResumeAnalysis").checked=true;
		}
		var photo = Ext.getDom("otohp");
		 var explain = Ext.getDom("explain");
		 var att = Ext.getDom("att");
		 var aba=Ext.getDom("aba");
		 var hpbl=Ext.getDom("hpbl");
		 var complexPassword=Ext.getDom("complexPassword");
		 
		 if(!aba.checked)
		    document.moudleParameterForm.acountBeActivedH.value="0";
		 
		 if(!hpbl.checked)
		    document.moudleParameterForm.hirePostByLayerH.value="0";
		 
		 if(!complexPassword.checked)
		    document.moudleParameterForm.complexPasswordH.value="0";
		 
		 if(!photo.checked)
			 document.moudleParameterForm.photoH.value="0";
		 
		 if(!explain.checked)
			 document.moudleParameterForm.explainationH.value="0";
		
		 if(!att.checked)
			 document.moudleParameterForm.attachH.value="0";
		 
				 
		 var subValue = '';
		document.moudleParameterForm.mark_type.value=markType.substring(1);	
		document.moudleParameterForm.testTemplateID.value=value.substring(1);
		var formd = "yyyy-mm-dd";
		
		var appliedPosItems = "";
		if(Ext.getDom("resume_state").checked)
			appliedPosItems += "resume_state,";
		if(Ext.getDom("z0329").checked)
			appliedPosItems += "z0329,";
		if(Ext.getDom("z0333").checked)
			appliedPosItems += "z0333,";
		if(Ext.getDom("z0315").checked)
			appliedPosItems += "z0315,";
		var flag = '${moudleParameterForm.flag}';//区分前台还是后台
		if(flag=="1"){
			var personValue =  Ext.getDom("personStore").value;
			var destValue =  Ext.getDom("destNbase").value;
		    if(personValue == destValue && personValue != "" ){
			      alert("招聘人员库与入职人员库不能相同，请重新选择 ！");
			      return;
			}
			
			var hireChannelPrivValue = JSON.stringify(BusiParam.ChannelPrivMap);
		    document.moudleParameterForm.hireChannelPriv.value = hireChannelPrivValue;
			var itemIds = '${moudleParameterForm.cardItemIds}';
			var itemIdsArray = itemIds.split(";");
			var allItemsId = "";
			for(var i = 1; i < itemIdsArray.length; i++) {
				allItemsId += ";" + itemIdsArray[i] + "~" + Ext.getDom(itemIdsArray[i]).value;
			}
			document.moudleParameterForm.allItemsId.value=allItemsId;
		}else {
			var candidateStatusValue=Ext.getDom("candidate_status").value;
			var displayValue=Ext.getCmp("candidate_status_div_check").getDisplayValue();
			if(!displayValue||'请选择…'==displayValue){
				candidateStatusValue = "";
			}
			if(candidateStatusValue == null || candidateStatusValue == "" || candidateStatusValue == "#"){
				Ext.showAlert(APPLICANT_STATUS_MUST_SET); 
			    return; 
			}
			
			document.moudleParameterForm.allItemsId.value="";
			var register_endtime =document.moudleParameterForm.register_endtime.value;
			var dataType = /^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])\s+(20|21|22|23|[0-1]\d):[0-5]\d$/;
			if(!dataType.test(register_endtime) && register_endtime != ""){
				Ext.showAlert(REGISTRATION_DEADLINE_INCORRECT);
	        	return;
	        }
			
		}
		document.moudleParameterForm.appliedPosItems.value=appliedPosItems;
		var cultureCodeItem = "${moudleParameterForm.cultureCodeItem}";
		if(Ext.getDom("prev1Value"))
			cultureCodeItem = Ext.getDom("prev1Value").value;
		Ext.getDom("cultureCodeItem").value = cultureCodeItem;
		moudleParameterForm.action='/recruitment/parameter/configureParameter.do?b_newSave=save';
		moudleParameterForm.submit();
	
	}
	function IsOverStrLength2(str,len)
	{
	   return str.length>len;
	}
	function isValidDate(day, month, year) {
    	if (month < 1 || month > 12) {
            return false;
        }
        if (day < 1 || day > 31) {
            return false;
        }
        if ((month == 4 || month == 6 || month == 9 || month == 11) &&
            (day == 31)) {
            return false;
        }
        if (month == 2) {
            var leap = (year % 4 == 0 &&
                       (year % 100 != 0 || year % 400 == 0));
            if (day>29 || (day == 29 && !leap)) {
                return false;
            }
        }
        return true;
    }

	function resumeChannel(){
	    Ext.getCmp('channelWindow').show();	
	}
	
	function attachCheck(){
	    if(!Ext.getDom("att").checked){
	        Ext.getCmp('prev_attach_codeset_check').disable();
	        Ext.getCmp('attach_Hire').disable(); 
		}else{
		    Ext.getCmp('prev_attach_codeset_check').enable();
		    var codesetValue = Ext.getCmp('prev_attach_codeset_check').getValue()
		    if(codesetValue != "#"){
		        Ext.getCmp('attach_Hire').enable(); 
		    }
		}
	}
	
	function getMusterFields(flag)
	{
		var selectFields="";
		var selectFieldNames="";
		var titile="";
		if(flag==1)
		{
			selectFields=document.moudleParameterForm.musterFieldIDs.value;
			selectFieldNames="${moudleParameterForm.musterFieldNames}";
			title="1";
		}
		else if(flag==2)
		{
			selectFields=document.moudleParameterForm.posQueryFieldIDs.value;;
			selectFieldNames="${moudleParameterForm.posQueryFieldNames}";
			title="职位快速查询指标";
		}
		else if(flag==3)
		{
			selectFields=document.moudleParameterForm.viewPosFieldIDs.value;
			selectFieldNames="${moudleParameterForm.viewPosFieldNames}";
			title="职位描述指标 ";
		}
		else if(flag==4)
		{
		   selectFields=document.moudleParameterForm.pos_listfield.value;
		   selectFieldNames="${moudleParameterForm.pos_listfieldNames}";
		   title="外网职位列表显示指标";
		}else if(flag==5)//dml 2011-6-22 10:55:21
		{
		   selectFields=document.moudleParameterForm.posCommQueryFieldIDs.value;
		   selectFieldNames="${moudleParameterForm.posCommQueryFieldNames}";
		   title="职位查询指标";
		}else if(flag==6){
		   selectFields=document.moudleParameterForm.pos_listfield_sort.value;
           selectFieldNames="${moudleParameterForm.pos_listfieldNames}";
           title="外网职位列表指标排序";
		}
		var thecodeurl="/recruitment/parameter/configureParameter.do?br_search=search&flag="+flag+"&selectedFields="+$URL.encode(selectFields)+"&selectFieldNames="+$URL.encode(selectFieldNames); 
		if(flag==6){
		   thecodeurl="/recruitment/parameter/configureParameter.do?b_query=search&flag=zppostsort"+"&selectedFields="+$URL.encode(selectFields)+"&selectFieldNames="+$URL.encode(selectFieldNames);
		}
		me.openWindow({
			title:title,
			width:550,
			height:365,
			url:thecodeurl,
			callBack:"setPrevParamValues("+flag+")"
		});
	}
	//将弹窗的返回值设置到前台参数中
	function  setPrevParamValues(values,flag){
		if(values!=null)
		{
			var tempvalue="";
            var tempid="";
			var afield;
			if(flag==1)
				afield=eval('fieldIds');
			else if(flag==2)
				afield=eval('fieldIds2');
			else if(flag==3)
				afield=eval('fieldIds3');
			else if(flag==4)
			    afield=eval('fieldIds12');
			else if(flag==5)
				afield=eval('fieldIdsCom');
			else if(flag==6){//招聘外网显示列表排序
			    afield=eval('fieldIds13');
			}
			if(flag!=6){
			    if(values[1]=='')
                       afield.innerHTML="&nbsp;";
                   else
                       afield.innerHTML=values[1];
			}else{
			      if(values=="not"){
			            afield.innerHTML="&nbsp;";
			      }else{
			            if(values.indexOf("`")!=-1){
			                values=values.substring(0,values.length-1);
			            }
			            var temparr = values.split("`");//
			            var descSort="";
			            for(var i=0;i<temparr.length;i++){
			                var tempIDS=temparr[i].split(":");//存放格式 itemid：itemdesc:[desc=0|asc=1]
			                var itemid=tempIDS[0];
			                var itemdesc=tempIDS[1];
			                var sort=tempIDS[2];
			                if(sort==1){
			                    descSort="升序";
			                    sort="ASC";
			                }else{
			                    descSort="降序";
			                    sort="DESC";
			                }
			                tempvalue=tempvalue+(itemdesc+":"+descSort+",");
			                tempid=tempid+(trim(itemid)+":"+trim(sort)+",")
			            }
			            if(tempvalue.indexOf(",")!=-1){
			                tempvalue=tempvalue.substring(0,tempvalue.length-1);
			            }
			            if(tempid.indexOf(",")!=-1){
			                tempid=tempid.substring(0,tempid.length-1);
			            }
			            afield.innerHTML=tempvalue;
			      }
			}
			if(flag==1)	
				document.moudleParameterForm.musterFieldIDs.value=values[0];
			else if(flag==2)
				document.moudleParameterForm.posQueryFieldIDs.value=values[0];
			else if(flag==3)
				document.moudleParameterForm.viewPosFieldIDs.value=values[0];
			else if(flag==4)
			    document.moudleParameterForm.pos_listfield.value=values[0];
			else if(flag==5)
			    document.moudleParameterForm.posCommQueryFieldIDs.value=values[0];
		    else if(flag==6){
		        document.moudleParameterForm.pos_listfield_sort.value=tempid;
		    }
		}
	}

	
	function setOrgIntro()
	{
		var thecodeurl="/recruitment/parameter/configureParameter.do?b_neworgIntro=inti`isVisible=1`type=1"; 
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
		 
     	//处理屏幕分辨率过小时页面显示不全问题
     	var temHeight = 545;
     	if(screenHeight * screenWidth <= 1280*768)
         	temHeight = 420;
		 me.openWindow({
				title:'单位介绍指标',
				width:590,
				height:temHeight,
				url:iframe_url,
				callBack:"setOrgValue()"
		 });
	}
	function setOrgValue(values){
		if(values ==null)
	        return;
	     
	     var returnVo= new Object();
	     returnVo.orgFieldIDsView = values.orgFieldIDsView;
	     returnVo.orgFieldIDs =values.orgFieldIDs;
	     returnVo.contentType=values.contentType;
	     returnVo.contentTypeView=values.contentTypeView;
	     var afield=eval('fieldIds4');
	    
	     afield.innerHTML=INTRODUCTION_FIELD+":"+ returnVo.orgFieldIDsView+"&nbsp;&nbsp;"+CONTENT_FIELD+":"+returnVo.contentTypeView;
	     document.moudleParameterForm.orgFieldIDs.value=returnVo.orgFieldIDs+","+returnVo.contentType;
	}
	
	function getResumeFields(){
		var url="/hire/parameterSet/configureParameter/getResumeFieldsList.do?b_search=search";
		var returnValue=window.showModalDialog(url,null, "dialogWidth:540px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
		if(returnValue != null){
			var ids=returnValue[0];
			//var names=returnValue[1];
			var afield=eval('fieldIds5');
			if(returnValue[1] == '' || returnValue[1] ==null)
				afield.innerHTML="&nbsp;";
			else
				afield.innerHTML=returnValue[1];
			document.moudleParameterForm.resumeFieldIds.value = ids;
		}
	}
	function getResumeStaticFields(){
		var url="/hire/parameterSet/configureParameter/getResumeStaticFieldsList.do?b_search=search";
		var returnValue=window.showModalDialog(url,null,"dialogWidth:540px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
		if(returnValue != null){
			var ids=returnValue[0];
			//var names=returnValue[1];
			var afield=eval('fieldIds6');
			if(returnValue[1] == '' || returnValue[1] ==null)
				afield.innerHTML="&nbsp;";
			else
				afield.innerHTML="&nbsp;&nbsp;" + returnValue[1];
			document.moudleParameterForm.resumeStaticIds.value = ids;
		}
	}
	function getCommonQueryCond()
	{
	   var url="/hire/parameterSet/configureParameter/getCommonQueryCond.do?b_search=search&opt=0";
	   createWindow(url,"setWorkClassConditions(values)");
	}
	//岗位分类统计条件赋值
	function setWorkClassConditions(returnValue){
	   if(returnValue !=null){
		  var ids = returnValue[0];
		  var afield=eval('fieldIds7');
		  if(returnValue[1] == '' || returnValue[1] ==null)
		 	 afield.innerHTML="&nbsp;";
		  else
		 	 afield.innerHTML=returnValue[1];
		  document.moudleParameterForm.commonQueryIds.value = ids;
		} 
	}
	function getResumeStateCode()
	{
	   var url="/hire/parameterSet/configureParameter/getCommonQueryCond.do?b_search=search&opt=1";
	   //createWindow(url,"setResumeState(values)");
	   me.openWindow({
			title:'禁止修改简历状态',
			width:540,
			height:400,
			url:url,
			callBack:"setResumeState()"
		});
	}
	//禁止简历状态
	function  setResumeState(returnValue){
		if(returnValue !=null){
		var ids = returnValue[0];
		var afield=eval('fieldIds9');
		if(returnValue[1] == '' || returnValue[1] ==null)
			afield.innerHTML="&nbsp;";
		else
			afield.innerHTML=returnValue[1];
		document.moudleParameterForm.resumeCodeValue.value = ids;
		} 
	}
	function getBusinessTemplate()
	{
		var select_id=document.moudleParameterForm.businessTemplateIds.value;
		var t_url="/system/warn/config_maintenance.do?b_template=link&type=1&dr=2&select_id="+select_id;
		var return_vo= window.showModalDialog(t_url,'rr',"dialogWidth:300px; dialogHeight:390px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
		if(!return_vo)
			return false;
		else
		{
			if(return_vo.flag=="true")
			{
				var afield=eval('fieldIds8');   
				var name=return_vo.title;
				var ids=return_vo.content;
				var a_name=name.split(",");
				var a_ids=ids.split(",");
				var bnames="";
				var ids="";
				for(var i=0;i<a_ids.length;i++)
				{
					if(a_ids[i]==null||a_ids[i]=='')
						continue;
					bnames+=a_ids[i]+":"+a_name[i]+"<br>";
					ids+=","+a_ids[i];
				}
				if(ids.length>0)
					ids=ids.substring(1);
				if(bnames=='')
					bnames="&nbsp;&nbsp;";
				afield.innerHTML=bnames;
				document.moudleParameterForm.businessTemplateIds.value=ids;
			}
		}      
	}
	var store1 = Ext.create('Ext.data.Store',{
		fields:['dataName','dataValue'],
		data:${moudleParameterForm.cultureListJson}
	});
	
	function hireList(codeId,flag)
	{
	  	var In_paramters="flag=1"; 	
	   	var map = new HashMap();
		map.put("codesetid", codeId);
		map.put("flag", flag);
		map.put("codeValue", "${moudleParameterForm.attachCodeset}");
		Rpc({
			functionId : '3000000297',
			success : change_attachHire
		}, map);
	}
	
	function change_attachHire(response) {
	    var param = Ext.decode(response.responseText);
	    this.codeId = param.codeSetid;
	    var level = param.level;
	    this.codelist = param.codeList;

	    if (Ext.getCmp('channelWindow')) 
	        Ext.getCmp('channelWindow').destroy();

	    if (!Ext.getDom("att").checked) {
	        Ext.getCmp('prev_attach_codeset_check').disable();
	        Ext.getCmp('attach_Hire').disable(); 
	    }

	    if (codeId == "#") {
	        Ext.getCmp('attach_Hire').disable();
	        return false;
	    } else {
	        Ext.getCmp('attach_Hire').enable();
	    }
	    
	    var nameValue = "${moudleParameterForm.nameValue}";
	    nameValue = nameValue.split(";");
	    var cardItemIds = "${moudleParameterForm.cardItemIds}";
	    cardItemIds = cardItemIds.split(";");
	    for (var i = 0; i < cardItemIds.length; i++) {
	        cardvalue = cardItemIds[i];
	        cardvalue = cardvalue.substring(10);
	        cardItemIds[i] = cardvalue;
	    }

	    if(Ext.getCmp('channelWindow')) 
	        Ext.getCmp('channelWindow').destroy();

	    var attachHire = '${moudleParameterForm.attachHire}';
	    var attachCodeset = "${moudleParameterForm.attachCodeset}";
	    if (attachCodeset == codeId && attachHire != null && attachHire != '' && attachHire != '0' ) {
	        var mapHire = ${moudleParameterForm.attachHire};
	        var list = new Array();
	        
	        for (var i = 0; i < cardItemIds.length; i++) {
	            var map = new HashMap();
	            if (i == 0) {
	                map.put("text", nameValue[i]);
	                map.put("dataIndex", cardItemIds[i]);
	                map.put("locked", true);
	            } else {
	                map.put("text", nameValue[i]);
	                map.put("dataIndex", cardItemIds[i]);
	                map.put("renderer",
	                function(value, metaData, record, rowIndex, colIndex) {
	                    return backStr(value, metaData, record, rowIndex, colIndex);
	                });
	            }
	            map.put("menuDisabled", true);
	            map.put("width", 150);
	            list.push(map);
	        }

	        var dataList = new Array();
	        for (var i = 0; i < codelist.length; i++) {
	            codeName = codelist[i].dataName;
	            codeDataValue = codelist[i].dataValue;
	            layer = codelist[i].layer;
	            for(var y = 1; y < layer; y++){
	                codeName ="&nbsp;&nbsp;"  + codeName;
	            }
	            mapValue = mapHire[codeDataValue];
	            var map = new HashMap();
	            map.put(cardItemIds[0], codeName);

	            if(typeof(mapValue)!="undefined"){
	 				   for (var j = 1; j < cardItemIds.length; j++) {
	 		                map.put(cardItemIds[j], mapValue[cardItemIds[j]]);
	 		            }
	 				  
		            }else{
		                for (var j = 1; j < cardItemIds.length; j++) {
		 				      map.put(cardItemIds[j], '0');
		 		            }
		            }
	            dataList.push(map);
	        }
	    } else {
	        var list = new Array();
	        for (var i = 0; i < cardItemIds.length; i++) {
	            var map = new HashMap();
	            if (i == 0) {
	                map.put("text", nameValue[i]);
	                map.put("dataIndex", cardItemIds[i]);
	                map.put("locked", true);
	            } else {
	                map.put("text", nameValue[i]);
	                map.put("dataIndex", cardItemIds[i]);
	                map.put("renderer",
	                function(value, metaData, record, rowIndex, colIndex) {
	                    return backStr(value, metaData, record, rowIndex, colIndex);
	                });
	            }
	            map.put("menuDisabled", true);
	            map.put("width", 150);
	            list.push(map);
	        }

	        var dataList = new Array();
	        for (var i = 0; i < codelist.length; i++) {
	        	layer = codelist[i].layer;
	            codeName = codelist[i].dataName;
	            for(var y = 1; y < layer; y++){
	                codeName ="&nbsp;&nbsp;"  + codeName;
	            }
	            var map = new HashMap();
	            map.put(cardItemIds[0], codeName);
	            for (var j = 1; j < nameValue.length; j++) {
	                map.put(cardItemIds[j], '0');
	            }
	            dataList.push(map);
	        }
	    }

	    if (Ext.getCmp('channelWindow')) 
	        Ext.getCmp('channelWindow').destroy();
	    
	    //简历附件分渠道设置表格数据
	    var channelGridStore = Ext.create('Ext.data.Store', {
	        fields: cardItemIds,
	        data: dataList,
	        proxy: {
	            type: 'memory',
	            reader: {
	                type: 'json',
	                root: 'items'
	            }
	        }
	    });

	    //简历附件分渠道设置表格 
	    var channelGridpanel = Ext.create('Ext.grid.Panel', {
	        store: channelGridStore,
	        id: 'channelGridpanel',
	        columns: list,
	        height: 360,
	        width: cardItemIds.length * 150,
	        columnLines:true,
	        rowLines :true,
	        plugins: {
	            ptype: 'cellediting',
	            clicksToEdit: 1,
	            listeners: {
	                beforeedit: function(editor, context) {
	                    var record = context.record;
	                    var datas = record.data;
	                    var flag = false; //true:可编辑的，false:不可编辑的
	                    for (var obj in datas) {
	                        var value = record.get(obj);
	                        if (value == '1' || value == '2') 
	                            flag = true;
	                    }
	                    return flag;
	                }
	            }
	        },
	        bbar: ['->', {
	            text: "确定",
	            handler: function() {
	                structureAttachHire();
	                channelBox.hide();
	            }
	        },'',{
	            text: "取消",
	            handler: function() {
	                channelBox.hide();
	            }
	        },
	        '->']
	    });
	    
	    //简历附件分渠道设置窗口
	    var channelBox = Ext.create('Ext.window.Window', {
	        title: '简历附件分渠道设置',
	        id: 'channelWindow',
	        closeAction:'hide',
	        height: 410,
	        layout:"fit",
	        width: 700,
	        modal:true,//设置是否添加遮罩
	        items: channelGridpanel,
	        draggable: true,
	        resizable: false,
	        //禁止缩放
	    });
	}
	
	  function changestatus(value,obj,row,colName){//可选和必填要么都不选，要么只选一个 0：都没选，1：可选，2：必填
	        var type = 0;
	    	var objs = document.getElementsByName(obj.name);
	    	var record = Ext.getCmp('channelGridpanel').store.getAt(row);//获得操作行的下标
	    	status:if(row>=0){
		    	if(value == '1'){
		    	    if(obj.value == '0')
		    	    {
		    			obj.value = '1';
		    			type = '1';
		    	    }else{
		    			objs[0].checked = false;
		    			obj.value = '0';
		    	    }
		    	}
		    	if(value == '2'){
		    	    if(obj.value == '0')
		    	    {
		    			obj.value = '1';
		    			type = '2';
		    	    }else{
		    			objs[1].checked = false;
		    			obj.value = '0';
		    	    }
		    	}
		    	if(!objs[1].checked && !objs[0].checked){
		    		objs[0].checked = false;
		    		objs[1].checked = false;
		    		type = '0';
		    	}
	    	}
	    	var record = Ext.getCmp('channelGridpanel').store.getAt(row);//获得操作行的下标
	    	record.set(colName,type);//重新赋值
	    }
	
	
	 function  backStr(value, metaData, record, rowIndex, colIndex){//根据数据渲染复选框按钮
	    	var id = record.getId()+this.i;
	        var  colName=metaData.column.dataIndex;
	        this.i++;
	    	var backStr;
	        if(value==1)  
	        	backStr = "<input type='radio' value='1' onclick='changestatus(1,this,"+rowIndex+", \""+colName+"\")' name='"+id+"_a01' checked=true />可选 <input name='"+id+"_a01' value='0' onclick='changestatus(2,this,"+rowIndex+", \""+colName+"\")' type='radio' />必填";
	      	else if( value == null || value == 0 ){
	    	   backStr = "<input type='radio' value='0' onclick='changestatus(1,this,"+rowIndex+", \""+colName+"\")'  name='"+id+"_b01'/>可选 <input name='"+id+"_b01' value='0' onclick='changestatus(2,this,"+rowIndex+", \""+colName+"\")' type='radio'/>必填";
	      	}
	    	else if(value==2)
	        	backStr = "<input type='radio' value='0' onclick='changestatus(1,this,"+rowIndex+", \""+colName+"\")'  name='"+id+"_c01'  />可选 <input name='"+id+"_c01' type='radio' value='1'  onclick='changestatus(2,this,"+rowIndex+", \""+colName+"\")' checked=true />必填";
	        return backStr;
	    }
	
	function cultureList(codeId,flag)
	{
	  	var In_paramters="flag=1"; 	
	   	var map = new HashMap();
		map.put("codesetid", codeId);
		map.put("flag", flag);
		map.put("codeValue", "${moudleParameterForm.cultureCodeItem}");
		Rpc({
			functionId : '3000000297',
			success : change_ok
		}, map);
	}
	function change_ok(response)
	{
		var param = Ext.decode(response.responseText);
		var codeId = param.codeSetid;
		var level = param.level;
		var codelist = param.codeList;
		var dataValue = "${moudleParameterForm.cultureCodeItem}";
		var codeDesc = param.codeDesc;
		var id = "prev1Value";
		if(Ext.getCmp('prev1Value'))
			Ext.getCmp('prev1Value').destroy();
		if(Ext.getCmp('prev1ValueId'))
			Ext.getCmp('prev1ValueId').destroy();
		var html = "";
		Ext.getDom("prevItem").innerHTML=html;
		if("1"==level){
			var store = Ext.create('Ext.data.Store', {
			    fields: ['dataName', 'dataValue'],
			    data : codelist
			});
			var combox = Ext.create('Ext.form.ComboBox', {
				id:'prev1ValueId',
				width:200,
			    store: store,
			    autoSelect:true,
			    queryMode: 'local',
			   	displayField: 'dataName',
			    valueField: 'dataValue',
			    fieldStyle:'padding-left:5px',
			    labelPad:0,
			    labelWidth:0,
			    renderTo: 'prevItem',
			    listeners:{
					beforeselect:{
						fn:function(combox,record){
							var value = record.data.dataValue;
							var node = document.createElement("input");
							node.type="hidden";
							node.name=id+"_value";
							node.id=id;
							node.value=value;
							var element=document.getElementById("prevItem");
							element.appendChild(node);
						}
					}
			    }
			});
			if(codeId=="${moudleParameterForm.cultureCode}")
				combox.setValue(dataValue);
		}else {
			if("3"==level||"2"==level){
				if(""==dataValue)
					dataValue="`";
				html="<input type='hidden' name='"+id+"_value' id='"+id+"' value='"+dataValue+"' /><input id='"+id+"value' class='hj-qtcs-select' type='text' name='"+id+"_view' value='"+codeDesc+"' /><img  class='img-middle' style='margin-left:-19px;' id='"+id+ "deepaaa' src='/module/recruitment/image/xiala2.png' plugin='deepcodeselector' codesetid='"+codeId+"' inputname='"+id+"_view'/>";
			}else if("UM"==codeId||"UN"==codeId||"@K"==codeId||level>"3"){
				if("UM"==codeId)
				{
					if(dataValue!=null&&dataValue.length>0)
						html="<input type='hidden' name='"+id+"_value'  id='"+id+"' value='"+dataValue+"' /><input id='"+id+"value' class='hj-zm-cj-xqbmz hj-qtcs-select' type='text' name='"+id+"_view' value='"+codeDesc+"' /><img ctrltype='3' nmodule='7'  class='img-middle' style='margin-left:-19px;' id='"+id+ "aaa' src='/module/recruitment/image/xiala2.png' plugin='codeselector' codesetid='"+codeId+"' inputname='"+id+"_view'/>";
					else
						html="<input type='hidden' name='"+id+"_value'  id='"+id+"' /><input id='"+id+"value' class='hj-zm-cj-xqbmz hj-qtcs-select' type='text' name='"+id+"_view' /><img ctrltype='3' nmodule='7'  class='img-middle' style='margin-left:-19px;' id='"+id+ "aaa' src='/module/recruitment/image/xiala2.png' plugin='codeselector' codesetid='"+codeId+"' inputname='"+id+"_view'/>";
				}else if("UN"==codeId)
				{
					if(dataValue!=null&&dataValue.length>0)
						html="<input type='hidden' name='"+id+"_value'  id='"+id+"' value='"+dataValue+"' /><input id='"+id+"value' class='hj-zm-cj-xqbmz hj-qtcs-select' type='text'  onchange='Global.clears()' name='"+id+"_view' value='"+codeDesc+"' /><img ctrltype='3' nmodule='7'  class='img-middle' style='margin-left:-19px;' id='"+id+ "aaa' src='/module/recruitment/image/xiala2.png' plugin='codeselector' codesetid='"+codeId+"' inputname='"+id+"_view'/>";
					else
						html="<input type='hidden' name='"+id+"_value'  id='"+id+"' /><input id='"+id+"value' class='hj-zm-cj-xqbmz hj-qtcs-select' type='text'  onchange='Global.clears()' name='"+id+"_view' /><img ctrltype='3' nmodule='7'  class='img-middle' style='margin-left:-19px;' id='"+id+ "aaa' src='/module/recruitment/image/xiala2.png' plugin='codeselector' codesetid='"+codeId+"' inputname='"+id+"_view'/>";
				}else{
					if(dataValue!=null&&dataValue.length>0)
						html="<input type='hidden' name='"+id+"_value' id='"+id+"' value='"+dataValue+"' /><input id='"+id+"value' class='hj-zm-cj-xqbmz hj-qtcs-select' type='text' name='"+id+"_view' value='"+codeDesc+"' /><img ctrltype='3' nmodule='7'  class='img-middle' style='margin-left:-19px;' id='"+id+ "aaa' src='/module/recruitment/image/xiala2.png' plugin='codeselector' codesetid='"+codeId+"' inputname='"+id+"_view'/>";
					else
						html="<input type='hidden' name='"+id+"_value' id='"+id+"'  /><input id='"+id+"value' class='hj-zm-cj-xqbmz hj-qtcs-select' type='text' name='"+id+"_view'  /><img ctrltype='3' nmodule='7' class='img-middle' style='margin-left:-19px;' id='"+id+ "aaa' src='/module/recruitment/image/xiala2.png' plugin='codeselector' codesetid='"+codeId+"' inputname='"+id+"_view'/>";
				}
			}
			var tdelem = Ext.getDom("prevItem");
			tdelem.innerHTML=html;
			setDeepEleConnect([id+'deepaaa']);
		}
	}
	function setLicenseAgreement(flag)
	{
	   var url="/recruitment/parameter/configureParameter.do?b_search=search`opt=1`flag="+flag;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url); 
	    var title;
	    if(flag=="l")
	        title="许可协议";
	    else if(flag=="p")
	        title="招聘外网提示信息";
	  	//处理屏幕分辨率过小时页面显示不全问题
	 	var temHeight = 520;
	 	if(screenHeight * screenWidth <= 1280*768)
	     	temHeight = 450;
	   	if(screenHeight * screenWidth <= 1024*768)
	   		temHeight = 360;
	    me.openWindow({
			title:title,
			width:750,
			height:temHeight,
			resizable:true,
			url:iframe_url,
			callBack:"licenseAgreementReturn("+flag+")"
		});
	}
	function licenseAgreementReturn(returnValue,flag){
		 if(returnValue !=null){
		      var obj=new Object();
		      obj.len=returnValue.len;
		      if(flag=="l")
		      {
		         var afield=eval('fieldIds10');
		         if(obj.len=='1')
		            afield.innerHTML="&nbsp;&nbsp;"+AGREEMENT_ALREADY_DEFINITION;
		         else if(obj.len=='0')
		            afield.innerHTML="&nbsp;&nbsp;"+AGREEMENT_NOT_ALREADY_DEFINITION;
		      }
		      else
		      {
	            var afield=eval('fieldIds11');
	            if(obj.len=='1')
	               afield.innerHTML="&nbsp;&nbsp;"+PROMPT_ALREADY_DEFINITION;
	            else if(obj.len=='0')
	               afield.innerHTML="&nbsp;&nbsp;"+PROMPT_NOT_ALREADY_DEFINITION;
		      }
		  } 
	}
	function hiddenPositionSalaryStandardItemList(obj)
	{
	   var trElement= Ext.getDom("str");
	   if(obj.checked)
	      trElement.style.display="block";
	   else
	      trElement.style.display="none";
	}
	function hiddenhirePositionNotUnionOrg(obj)
	{
	  var trElement= Ext.getDom("hpnuo");
	   if(obj.checked)
	      trElement.style.display="block";
	   else
	      trElement.style.display="none";
	}
	function hiddenSmg(obj){
		var trElement= Ext.getDom("tsmg");
		if(obj.checked){
		    trElement.style.display="block";
		}else{
		    trElement.style.display="none";
		    Ext.getDom("approve").value="";
		}
	}
	function visibleRES(obj)
	{
	   var trElement= Ext.getDom("res");
	   if(obj.checked)
	      trElement.style.display="block";
	   else
	      trElement.style.display="none";
	}
	function getCommonInfo()
	{
	  var remenberExamineSet=Ext.getDom("subset");
	  var set='';
	  for(var i=0;i<remenberExamineSet.options.length;i++)
	  {
	     if(remenberExamineSet.options[i].selected)
	     {
	        set=remenberExamineSet.options[i].value;
	        break;
	     }
	  }
	  if(set=='')
	  {
	     alert("请选择面试过程记录子集!");
	     return;
	  }
		var url="/hire/parameterSet/configureParameter/examine_info_config.do?b_search=search`setid="+set;
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url); 
		var returnValue=window.showModalDialog(iframe_url,null,"dialogWidth:400px; dialogHeight:250px;resizable:no;center:yes;scroll:yes;status:no");
	  if(returnValue)
	  {
	    var obj = new Object();
	    obj.tf=returnValue.tf;
	    obj.cf=returnValue.cf;
	    obj.lf=returnValue.lf;
	    obj.cdf=returnValue.cdf;
	    obj.cuf=returnValue.cuf;
	    Ext.getDom("cf").value=obj.cf;
	    Ext.getDom("tf").value=obj.tf;
	    Ext.getDom("lf").value=obj.lf;
	    Ext.getDom("cdf").value=obj.cdf;
	    Ext.getDom("cuf").value=obj.cuf;
	  } 
	}
	function setSchoolPosition(posid)
	{
	   var url="/hire/parameterSet/configureParameter/new_school_position.do?b_init=init`positionID="+posid;
	   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url); 
	   var returnValue=window.showModalDialog(iframe_url,null, "dialogWidth:450px; dialogHeight:180px;resizable:no;center:yes;scroll:yes;status:no");
	   if(returnValue)
	   {
	       var obj=new Object();
	       obj.pid=returnValue.pid;
	       obj.pdesc=returnValue.pdesc;
	       var afield=eval('fieldIds20');
	       afield.innerHTML="&nbsp;&nbsp;"+obj.pdesc+"&nbsp;&nbsp;";
	        document.moudleParameterForm.schoolPosition.value=obj.pid;
	   }
	}
	function getTables()
	{
	   var url="/hire/parameterSet/configureParameter/select_card.do?b_init=init";
	   var iframe_url="/general/query/common/iframe_query.jsp?src="+url; 
	   var returnValue=window.showModalDialog(iframe_url,null, "dialogWidth:450px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
	   if(returnValue&&returnValue!="no")
	   {
	       document.moudleParameterForm.cardIDs.value=returnValue;
	   }
	}
	function changemajorcode()
	{///当改变招聘专业时
		var hashvo=new ParameterSet(); 
		hashvo.setValue("hireMajor",getEncodeStr(moudleParameterForm.hireMajor.value));
	   	var request=new Request({asynchronous:false,onSuccess:displayMajorCode,functionId:'3000000259'},hashvo);
	}
	function displayMajorCode(outParameters)
	{
		var isCharField=outParameters.getValue("isCharField");
		if(isCharField==0)
		{
			Ext.getDom("after5").style.display="none";
			Ext.getDom("majorCodeName").style.display="none";
		}
		if(isCharField==1)
		{
			Ext.getDom("after5").style.display="";
			Ext.getDom("majorCodeName").style.display="";
		}
	}
	///初始化时执行的函数
	function init()
	{
		var appliedPosItems="${moudleParameterForm.appliedPosItems}";
		var items = "";
		if(appliedPosItems!=null&&appliedPosItems!=""&&appliedPosItems.indexOf(",")!=-1){
			items = appliedPosItems.split(",");
			for(var i=0;i<items.length;i++){
				if(items[i]=="resume_state"){
					Ext.getDom("resume_state").checked=true;
				}
				if(items[i]=="z0329"){
					Ext.getDom("z0329").checked=true;
				}
				if(items[i]=="z0333"){
					Ext.getDom("z0333").checked=true;
				}
				if(items[i]=="z0315"){
					Ext.getDom("z0315").checked=true;
				}
			}
		}
		var isCharField="${moudleParameterForm.isCharField}";
		if(isCharField==0)
		{
			Ext.getDom("after5").style.display="none";
			Ext.getDom("majorCodeName").style.display="none";
		}
		aboutPassword();
		<hrms:priv func_id="31025">
		resumeAnalysis();
		</hrms:priv>
	}
	function aboutPassword(){
		if(Ext.getDom("complexPassword").checked){
			Ext.get("changdu").show();
		
		}else{
			Ext.get("changdu").hide();
	
		}
	}
	function resumeAnalysis(){
		if(Ext.getDom("startResumeAnalysis")!=null){
			if(Ext.getDom("startResumeAnalysis").checked){
				Ext.getDom("resumeAnalysis_name_password").style.display="block";
				Ext.getDom("resumeAnalysis_foreignJob").style.display="block";
			}else{
				Ext.getDom("resumeAnalysis_name_password").style.display="none";
				Ext.getDom("resumeAnalysis_foreignJob").style.display="none";	
			}
		}
	
	}
	function setTPinput(){
	    var InputObject=document.getElementsByTagName("input");
	    for(var i=0;i<InputObject.length;i++){
	        var InputType=InputObject[i].getAttribute("type");
	        if(InputType!=null&&(InputType=="text"||InputType=="password")){
	            InputObject[i].className=" "+"TEXT4";
	        }
	    }
	}
</script>
<%
	boolean isFive = false;
	EncryptLockClient lockclient = (EncryptLockClient) session
			.getServletContext().getAttribute("lock");
	if (lockclient != null) {
		if (lockclient.getVersion() >= 50)
			isFive = true;
	}
	int versionFlag = 1;
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
%>
<head>
<style type="text/css">
 .mybutton{
    border:1px solid #c5c5c5;
    background-color:#f9f9f9;
    height:23px;
    line-height:20px;
    padding:1px 6px;
	width: 30px;
	background-color:TRANSPARENT ;
	font:12px/16px 微软雅黑, 宋体, tahoma, arial, verdana, sans-serif;
    background-repeat:no-repeat;
    text-align:center;
    cursor:pointer;
}
 
.divStyle {
	padding: 5px;
	border: 1pt solid #c5c5c5;
	height: auto;
	min-height: 50px;
	width: auto;
}

.boxMiddle {
	padding: 1px 3px 3px 3px;
}
</style>
</head>
<%
	String bosflag = userView.getBosflag();//得到系统的版本号
%>
<body>
	<%
		if (bosflag != null && !bosflag.equals("hcm")) {
	%>
	<br>
	<%
		}
	%>
	<div id="lcbase_div" class="hj-wzm-xq-all" style="display: none;overflow-x:hidden">
		<div class="hj-sbp-cj-all">
			<html:form action="/recruitment/parameter/configureParameter">
				<table width="100%" border="0" cellspacing="0px" align="center"	cellpadding="0px">
					<tr>
						<td width="100%">
							<table width="100%">
								<tr>
									<td class="hj-zm-cj-two">
										<div class="hj-zm-lc-one" id="after">
											<div>
												<span class="hj-qtcs-line_02">基础参数</span>
												<table border='0' width="100%" style="margin-left:20px;" cellspacing="10px">
													<tr>
														<td align="right" width="15%" height='50' nowrap>招聘人员库&nbsp;&nbsp;	</td>
														<td align='left' width='450' nowrap>
															<div id="after7" style="float:left;padding-top:5px"></div>
															<div style="display:inline" width="100px"></div> 
															<input type="hidden" name="personStore" id="personStore" value="${moudleParameterForm.personStore }" />
														</td>
														<td></td>
													</tr>
													<tr>
														<td align="right" width="15%" height='50' nowrap>入职人员库&nbsp;&nbsp;	</td>
														<td align='left' width='450' nowrap>
															<div id="after9" style="float:left;padding-top:5px"></div>
															<div style="display:inline" width="100px"></div> 
															<input type="hidden" name="destNbase" id="destNbase" value="${moudleParameterForm.destNbase }" />
														</td>
														<td></td>
													</tr>
													<%
														ParameterForm moudleParameterForm = (ParameterForm) session.getAttribute("moudleParameterForm");
														ArrayList hireObjectList = moudleParameterForm.getHireObjectList();
														ArrayList testTemplateList = moudleParameterForm.getTestTemplateList();
														ArrayList markList = moudleParameterForm.getMarkList();
													%>
													<tr>
														<td id="majorCodeName" align="right" height='50' nowrap>招聘专业代码&nbsp;&nbsp;
														</td>
														<td align='left' width='450' nowrap id="after5">
															<input type="hidden" name="hireMajorCode" id="hireMajorCode" value="${moudleParameterForm.hireMajorCode }" />
														</td>
														<td></td>
													</tr>
													<tr>
														<td align="right" height='50' nowrap>准考证打印模板&nbsp;&nbsp;
														</td>
														<td align='left' width='450' nowrap id="after6">
															<input type="hidden" name="admissionCard" id="admissionCard" value="${moudleParameterForm.admissionCard }" />
														</td>
														<td></td>
													</tr>
													<tr>
														<td align="right" height='50' nowrap>考试成绩模板&nbsp;&nbsp;
														</td>
														<td align='left' width='450' nowrap id="after8">
															<input type="hidden" name="scoreCard" id="scoreCard" value="${moudleParameterForm.scoreCard }" />
														</td>
														<td></td>
													</tr>
												</table>
											</div>
											<div id="jl_mb"></div>
										</div>
									</td>
								</tr>
								<TR>
									<td width="100%">
										<div id="prev">
										<span class="hj-qtcs-line_02">帐号注册与登录</span>
											<table border='0' width="100%" style="margin-left:20px;" cellpadding="0" cellspacing="15px">
											<tr>
													<td align="left" height='30' width="54" style="padding-left: 9px;" nowrap>证件类型</td>
													<td align='left' valign="middle" nowrap>
														<div id="candidate_status_div2" style="float:left;"></div>
														<span style="vertical-align:middle;line-height:22px;">&nbsp;(人员基本情况子集,代码型指标)</span>
														<input type="hidden" name="certificate_type" id="certificate_type" value="${moudleParameterForm.certificate_type}" />
													</td>
													<td width="100px"></td>
												</tr>
												<tr>
													<td align="left" height='30' width="54" style="padding-left: 9px;" nowrap>证件号码</td>
													<td align='left' valign="middle" nowrap>
														<div id="candidate_status_div3" style="float:left;"></div>
														<span style="vertical-align:middle;line-height:22px;">&nbsp;(人员基本情况子集,唯一性指标)</span>
														<input type="hidden" name="func_only" id="func_only" value="${moudleParameterForm.func_only}" />
													</td>
													<td width="100px"></td>
												</tr>
											
											<tr>
													<td align="left" height='30' width="60" nowrap><font color='red' style='white-space:nowrap;'>*&nbsp;</font>应聘身份</td>
													<td align='left' valign="middle" nowrap>
														<div id="candidate_status_div" style="float:left;"></div>
														<span style="vertical-align:middle;line-height:22px;">&nbsp;(人员基本情况子集,关联代码类35)</span>
														<input type="hidden" name="candidate_status" id="candidate_status" value="${moudleParameterForm.candidate_status }" />
													</td>
													<td width="100px"></td>
												</tr>
												
												
												<tr>
													<td align="left" height='30'  colspan="2" nowrap>
													<div style="float:left;margin-top:2px;padding-left: 8px;">注册截止时间</div>
														<input name="register_endtime" type="text"  value="${moudleParameterForm.register_endtime}" id="register_endtime"  class="hj-zm-cj-xqbm"  style="width:170px;margin-left: 20px;" /><img id="register_end" class="img-middle" style="margin-left:-1px;height:23px;" plugin="datetimeselector" inputname="register_endtime" src="/module/recruitment/image/TIME.bmp" format="Y-m-d H:i" >
													</td>
													<td width="100px"></td>
												</tr>
												
												
												<tr style="display:none;">
													<td align="left" height='30' width="54"  nowrap>
														<bean:message key="hire.culture.type" />
													</td>
													<td align='left'>
														<div id="prev1" style="float:left"></div>
														<div id="prevItem" style="float:left;margin-left:5px;"></div> 
														<input type="hidden" name="cultureCode" id="cultureCode" value="" /> 
														<input type="hidden" name="cultureCodeItem" id="cultureCodeItem" value="" />
													</td>
													<td width="100px"></td>
												</tr>
												<tr style="display:none;">
													<td align="left" height='50' width="54" style="padding-left: 9px;" nowrap>工作经验</td>
													<td align='left' valign="middle" nowrap>
														<div id="prev3" style="float:left;"></div>
														<span style="vertical-align:middle;line-height:22px;">&nbsp;(人员基本情况子集,关联代码类45)</span>
														<input type="hidden" name="workExperience" id="workExperience" value="" />
													</td>
													<td width="100px"></td>
												</tr>
												<tr>
													<td align="left" valign='top' height='50' width="54" style="padding-left: 9px;" nowrap>
														<bean:message key="hire.agreement.permit" />
													</td>
													<td class="RecordRow" align='left' valign="top">
														<div id='fieldIds10' class="divStyle">
															${moudleParameterForm.licenseAgreementParameter}
														</div>
													</td>
													<td valign='top' width="100px">
														<input type="button" value="..." class="mybutton" name="" onclick='setLicenseAgreement("l");'>
													</td>
												</tr>
												<tr>

													<td align="left" valign='middle' height='50' width="110" colspan="2" style="padding-left: 9px;" nowrap>
													<html:checkbox styleId="aba" property="acountBeActived" name="moudleParameterForm" value="1" styleClass="boxMiddle"></html:checkbox>注册帐号需通过邮箱激活才生效
													</td>
												</tr>
												<tr>
													<td align="left" valign='middle' height='50' colspan="2" style="padding-left: 9px;" nowrap>
														<html:checkbox styleId="complexPassword" onclick="aboutPassword();" property="complexPassword" name="moudleParameterForm" value="1" styleClass="boxMiddle"></html:checkbox>注册帐号使用复杂密码 
														<span id="changdu" style="padding-left:25px">
														<span id="geshi" style="padding-left:0px">
															格式要求：字母、数字、特殊字符的组合； </span> 长度要求：&nbsp;
														<html:text name="moudleParameterForm" property="passwordMinLength" styleId="passwordMinLength" style="width:30px;text-align:right;" maxlength="2"></html:text>
														&nbsp;至&nbsp;
														<html:text name="moudleParameterForm" property="passwordMaxLength" styleId="passwordMaxLength" style="width:30px;text-align:right;" maxlength="2"></html:text>
														&nbsp;位
														 </span>
													 </td>
													<td valign='top' width="100px">&nbsp;&nbsp;&nbsp;&nbsp;</td>
												</tr>
												<tr>
													<td align="left" valign='middle' height='50' width="110" colspan="2" style="padding-left: 9px;" nowrap>一天内登录失败累计
													<html:text name="moudleParameterForm" property="failedTime" styleId="failedTime" style="width:50px;border:1px #c5c5c5 solid;text-align:right;margin-top:-1px;"></html:text>
													次后锁定
													<html:text name="moudleParameterForm" property="unlockTime" styleId="unlockTime" style="width:50px;border:1px #c5c5c5 solid;text-align:right;margin-top:-1px;"></html:text>
													分钟
													</td>
													<td valign='top' width="100px">&nbsp;&nbsp;&nbsp;&nbsp;</td>
												</tr>
												</table>
												<span class="hj-qtcs-line_02">简历填写与职位申请</span>
												<table border='0' width="100%" style="margin-left:20px;"	cellpadding="0" cellspacing="20px">
												<tr>
													<td align="left" height='20'  colspan="2" nowrap>
													<div style="float:left;margin-top:2px">开放问答子集</div>
														<div id="prev2" style="float:left;margin-left:6px;"></div>
														<input type="hidden" name="answerSet" id="answerSet" value="${moudleParameterForm.answerSet }" />
													</td>
													<td width="100px"></td>
												</tr>
												<tr>
													<td valign='middle' height='20' colspan="2" nowrap>
														<html:checkbox styleId="explain" property="explaination" name="moudleParameterForm" value="1" styleClass="boxMiddle"></html:checkbox>
														<bean:message key="hire.visible.explanation" />
													</td>
												</tr>
												<tr>
													<td align="left" valign='middle' height='20' width="110" colspan="2" nowrap>
														<html:checkbox styleId="otohp" property="photo" name="moudleParameterForm" value="1" styleClass="boxMiddle">
														</html:checkbox>
														<bean:message key="hire.photo.mustupload" />
													</td>
												</tr>
												<tr>
												<td valign='middle' height='20' colspan="2" nowrap>
													<div style="display: block;float: left;">
													<html:checkbox styleId="att" property="attach" onclick="attachCheck()" name="moudleParameterForm" value="1" styleClass="boxMiddle"></html:checkbox>
													上传简历附件，单个简历附件最大为
													<html:text styleId="maxFileSizeId" onkeyup="checkNumber(this);" property="maxFileSize" name="moudleParameterForm" style="width:50px;border:1px #c5c5c5 solid;text-align:right;"></html:text>
													M（默认为10M，最大为100M）
									                </div>
												</tr>
												<tr>
													<td align="left" height='20'  colspan="2" nowrap>
													<div style="float:left;margin-top:2px">简历附件分类</div>
														<div id="prev_attach_codeset" style="float:left;margin-left:6px;display: block;"></div>
														<input type="hidden" name="attachCodeset" id="attachCodeset" value="${moudleParameterForm.attachCodeset}" />
														<div id="prev_attach_button" style="height:30px;"></div>
													</td>
													<td width="100px"></td>
												</tr>
												<tr <%if (isFive) {
														if (versionFlag == 1) {
														} else {
															out.print("style='display:none'");
														}
													} else {
														out.print("style='display:none'");
													}%>>
												<td valign='middle' height='20' colspan="2" nowrap>
												每批次最多允许申请
												<html:text property="max_count" name="moudleParameterForm" style="width:50px;border:1px #c5c5c5 solid;text-align:right;"></html:text>
												个职位
												</td>
												</tr>
												<tr>
													<td align="left" valign='middle' height='20' width="165" nowrap>外网已申请职位列表显示指标</td>
													<td align="left" height='20' nowrap>
														<input type="checkbox" id="resume_state" value="resume_state" class="boxMiddle" />&nbsp;简历状态&nbsp;&nbsp;
														<input type="checkbox" id="z0329" value="z0329" class="boxMiddle" />&nbsp;开始日期&nbsp;&nbsp;
														<input type="checkbox" id="z0333" value="z0333"	class="boxMiddle" />&nbsp;工作地点&nbsp;&nbsp; 
														<input type="checkbox" id="z0315" value="z0315" class="boxMiddle" />&nbsp;招聘人数&nbsp;&nbsp;
													</td>
													<td valign='top' width="100px">&nbsp;&nbsp;&nbsp;&nbsp;</td>
												</tr>
												</table>
												<span class="hj-qtcs-line_02">职位查询与显示</span>
												<table border='0' width="100%" style="margin-left:20px;"	cellpadding="0" cellspacing="20px">
												<tr>
													<td align="right" valign='top' height='50' width="130" nowrap>职位快速查询指标</td>
													<td class="RecordRow" align='left' valign="top"	style="border:1;border-color:rgb(153, 187, 232);height:auto">
														<div id='fieldIds2' class="divStyle">
														${moudleParameterForm.posQueryFieldNames}
														</div>
													</td>
													<td valign='top' width="100px">
														<input type="button" name="" class="mybutton" onclick='getMusterFields(2)' value="...">
													</td>
												</tr>
												<tr
													<%if (isFive) {
														if (versionFlag == 1) {
														} else {
															out.print("style='display:none'");
														}
													} else {
														out.print("style='display:none'");
													}%>>
													<td align="right" valign='top' height='50' width="130" nowrap>
														<bean:message key="hire.position.queryfield" />
													</td>
													<td class="RecordRow" align='left' valign="top">
														<div id='fieldIdsCom' class="divStyle">
															${moudleParameterForm.posCommQueryFieldNames}
														</div>
													</td>
													<td valign='top' width="100px">
														<input type="button" value="..." name="" class="mybutton" onclick='getMusterFields(5)'>
													</td>
												</tr>
												<tr>
													<td align="right" valign='top' height='50' width="130"	nowrap>外网职位列表显示指标</td>
													<td class="RecordRow" align='left' valign="top">
														<div id='fieldIds12' class="divStyle">
															${moudleParameterForm.pos_listfieldNames}
														</div>
													</td>
													<td valign='top' width="100px">
														<input type="button"	value="..." name="" class="mybutton" onclick='getMusterFields(4)'>
													</td>
												</tr>
												<tr>
													<td align="right" valign='top' height='50' width="130"	nowrap>外网职位列表指标排序</td>
													<td class="RecordRow" align='left' valign="top">
														<div id='fieldIds13' class="divStyle">
															${moudleParameterForm.pos_listfield_sortNames}
														</div>
													</td>
													<td valign='top' width="100px">
														<input type="button" value="..." name="" class="mybutton" onclick='getMusterFields(6)'>
													</td>
												</tr>

												<tr>
													<td align="right" valign='top' height='50' width="130"	nowrap>
														<bean:message key="hire.position.descriptionfield" />
													</td>
													<td class="RecordRow" align='left' valign="top">
														<div id='fieldIds3' class="divStyle">
															${moudleParameterForm.viewPosFieldNames}
														</div>
													</td>
													<td valign='top' width="100px">
														<input type="button" value="..." class="mybutton" name="" onclick='getMusterFields(3)'>
													</td>
												</tr>

												<tr style="display: none">
													<td align="right" valign='top' height='50' width="130"
														nowrap><bean:message key="hire.disable.resumestate" />
													</td>
													<td class="RecordRow" align='left' valign="top">
														<div id='fieldIds9' class="divStyle">
														${moudleParameterForm.resumeCodeName}&nbsp;&nbsp;
														</div>
													</td>
													<td valign='top' width="100px">
														<input type="button" value="..." class="mybutton" name="" onclick='getResumeStateCode();'>
													</td>
												</tr>
												<tr>
													<td align="right" valign='top' height='50' width="130" nowrap>
														<bean:message key="hire.unit.descriptionfield" />
													</td>
													<td class="RecordRow" align='left' valign="top">
														<div id='fieldIds4' class="divStyle">
														${moudleParameterForm.orgFieldNames}
														</div>
													</td>
													<td valign='top' width="100px">
														<input type="button" value="..." class="mybutton" name="" onclick='setOrgIntro()'>
													</td>
												</tr>
												<tr>
													<td align="left" valign='middle' height='20' width="120" colspan="2" nowrap>
													<html:checkbox styleId="hpbl" property="hirePostByLayer" name="moudleParameterForm" value="1" styleClass="boxMiddle"></html:checkbox>
													只显示本级机构招聘职位
													</td>
												</tr>
												<tr>
													<td align="left" valign='middle' height='20' colspan="2" nowrap>
														<div style="float:left;margin-top:1px">招聘职位按第 
													<html:text styleId="unitLevelId" property="unitLevel" name="moudleParameterForm" style="width:50px;border:1px #c5c5c5 solid;text-align:right;"></html:text>
														级</div>
														<div id="prev4" style="float:left;;margin-top:-1px"></div>
														<input type="hidden" name="unitOrDepart" id="unitOrDepart" value="${moudleParameterForm.unitOrDepart }" />
														分组显示 
													</td>
													<td valign='top' width="100px">&nbsp;&nbsp;&nbsp;&nbsp;</td>
												</tr>
												<tr <%if (isFive) {
														if (versionFlag == 1) {
														} else {
															out.print("style='display:none'");
														}
													} else {
														out.print("style='display:none'");
													}%>>
													<td align="left" valign='middle' height='20' colspan="2" nowrap>
														机构下显示职位个数
														<html:text property="positionNumber" name="moudleParameterForm" style="width:50px;border:1px #c5c5c5 solid;text-align:right;"></html:text>
													</td>
												</tr>
												</table>
												<span class="hj-qtcs-line_02">其他参数</span>
												<table border='0' width="100%" style="margin-left:20px;"	cellpadding="0" cellspacing="20px">
												<tr>
													<td align="right" valign='middle' height='20' width="100" nowrap>
														<bean:message key="hire.netlogo.href" />
													</td>
													<td align='left'>
														<html:text property="netHref" name="moudleParameterForm" size="30" style="border:1px #c5c5c5 solid;"></html:text>
													</td>
													<td valign='top' width="100px">&nbsp;&nbsp;&nbsp;&nbsp;</td>
												</tr>
												<tr>
													<td align="right" valign='top' height='50' width="120"	nowrap>
														<bean:message key="hire.lable.promptcontent" />
													</td>
													<td class="RecordRow" align='left' valign="top">
														<div id='fieldIds11' align="left" class="divStyle">
															${moudleParameterForm.promptContentParameter}
														</div>
													</td>
													<td valign='top' width="120">
														<input type="button" value="..." class="mybutton" name="" onclick='setLicenseAgreement("p");'>
													</td>
												</tr>
												
												<tr>
													<td colspan="3" nowrap></td>
												</tr>
											</table>

											<input type='hidden' name='musterFieldIDs' value="${moudleParameterForm.musterFieldIDs}" />
											<input type='hidden' name='posQueryFieldIDs' value="${moudleParameterForm.posQueryFieldIDs}" /> 
											<input type='hidden' name='posCommQueryFieldIDs' value="${moudleParameterForm.posCommQueryFieldIDs}" /> 
											<input type='hidden' name='viewPosFieldIDs' value="${moudleParameterForm.viewPosFieldIDs}" /> 
											<input type='hidden' name='orgFieldIDs' value="${moudleParameterForm.orgFieldIDs}" /> 
											<input type='hidden' name='resumeFieldIds' value="${moudleParameterForm.resumeFieldIds}" /> 
											<input type='hidden' name='resumeStaticIds' value="${moudleParameterForm.resumeStaticIds}" /> 
											<input type="hidden" name="commonQueryIds" value="${moudleParameterForm.commonQueryIds}" /> 
											<input type="hidden" id="t_i" name="businessTemplateIds" value="${moudleParameterForm.businessTemplateIds}" />
											<input type="hidden" name="resumeCodeValue" value="${moudleParameterForm.resumeCodeValue}" />
											<html:hidden name="moudleParameterForm" styleId="tf" property="titleField" />
											<html:hidden name="moudleParameterForm" styleId="cf" property="contentField" />
											<html:hidden name="moudleParameterForm" styleId="lf" property="levelField" />
											<html:hidden name="moudleParameterForm" styleId="cdf" property="commentDateField" />
											<html:hidden name="moudleParameterForm" styleId="cuf" property="commentUserField" />
											<input type='hidden' name='newTime' value="${moudleParameterForm.newTime}" /> 
											<input type='hidden' name='pos_listfield' value="${moudleParameterForm.pos_listfield}" /> 
											<input type='hidden' name='pos_listfield_sort' value="${moudleParameterForm.pos_listfield_sort}" /> 
											<input type="hidden" name="schoolPosition" value="${moudleParameterForm.schoolPosition}" /> 
											<input type="hidden" name="attachHire" id="attachHire" value="${moudleParameterForm.attachHire}" /> 
											<input type="hidden" name="hireChannelPriv" id="hireChannelPriv" value='${moudleParameterForm.hireChannelPriv}' /> 
											<input type='hidden' name='appliedPosItems' value="${moudleParameterForm.appliedPosItems}" />
											<html:hidden name="moudleParameterForm" property="cardIDs" />
											<input type='hidden' name='photoH' value="1" /> 
											<input type='hidden' name='explainationH' value="1" /> 
											<input type='hidden' name='attachH' value="1" /> 
											<input type="hidden" name="acountBeActivedH" value="1" /> 
											<input type='hidden' name='hirePostByLayerH' value="1" /> 
											<input type='hidden' name='complexPasswordH' value="1" />
											<input type='hidden' name='allItemsId' value="1" />
										</div>
									</td>
								</tr>
							</table> 
							<input type='hidden' name="testTemplateID" /> 
							<input type='hidden' name='mark_type' /> 
							</html:form>
							</div>
							</div> 
	<script language="javascript">
		Ext.onReady(function(){
			Ext.QuickTips.init();
			 var flag = '${moudleParameterForm.flag}';
			 var tname="";
			    if(flag=="1"){
			    	tname = "后台配置参数";
			    }else{
			    	tname = "前台配置参数";
			    }
			Ext.form.Field.prototype.msgTarget = 'side';
		 	var Panel = new Ext.Panel({      
		 		xtype:'panel',
				id:'view_panel',
				title:"<div id='tname' style='width:100px;display:inline;float:left'>"+tname+"</div><div style='float: right;margin-right: 5%;white-space:nowrap;display:inline' align='center'><a href='javascript:void(0)' style='margin-right: 2%' onclick='sub();'>保存</a></div>",
			  	html:"<div id='topPanel'></div>",
			  	region:'center',
			  	border:false
			});
		 	new Ext.Viewport({
		    	layout:'border',
		    	title:'创建流程',
		        padding:"0 5 0 5",
		        renderTo: Ext.get('panel'),
		     	style:'backgroundColor:white',
		        items:[Panel]
		         
		     });
		     
		 	Ext.getDom('topPanel').appendChild(Ext.getDom('lcbase_div'));
		 	Ext.getDom('lcbase_div').style.display="block";
		 	
		 	var view_panel = Ext.getCmp('view_panel');
		    view_panel.setAutoScroll(true);
		    var winHeight =parent.document.body.clientHeight;
		    view_panel.setHeight(winHeight);
		    if(flag=="1"){
		        Ext.getDom("prev").style.height="0px";
		        Ext.getDom("lcbase_div").style.overflow="hidden";
		        Ext.get("prev").hide();
		        dynamicCreateHtml();
		    }else{
		        Ext.getDom("after").style.height="0px";
		        Ext.get("after").hide();
		    }
		   	createCombo("prev1","cultureCode","");
		   	createCombo("prev2","answerSet","");
		   	createCombo("prev3","workExperience","");
		   	createCombo("prev4","unitOrDepart","");
		   	createCombo("candidate_status_div","candidate_status");
		   	createCombo("candidate_status_div2","certificate_type");
		   	createCombo("candidate_status_div3","func_only");
		 	createCombo("prev_attach_codeset","attachCodeset","");
		    createCombo("after5","hireMajorCode","");
		    createCombo("after6","admissionCard","");
		    createCombo("after7","personStore","");
		    createCombo("after8","scoreCard","");
		    createCombo("after9","destNbase","");
		});
		
		function dynamicCreateHtml() {
			var jl_mb = Ext.getDom("jl_mb");
			var selectValue = '${moudleParameterForm.selectValue}';
			var nameValue = '${moudleParameterForm.nameValue}';
			var cardItemIds = '${moudleParameterForm.cardItemIds}';
			var selectArray = selectValue.split(";");
			var nameArray = nameValue.split(";");
			var itemIdArray = cardItemIds.split(";");
			var html = "";
			html += "<span class='hj-qtcs-line_02'>简历模板</span>";
			html += "<table border='0' style='margin-left:20px;' width='100%' cellspacing='10px'>";
			for(var i = 1; i < selectArray.length; i++) {
				html += "<tr>";
				html += "<td align='right' width='15%' height='20' nowrap>" + nameArray[i] + "&nbsp;&nbsp;";
				html += "<td align='left' width='450'  height='50' nowrap id=after" + (i+9) + ">";
				html += "<input type='hidden' name='" + itemIdArray[i] + "' id='" + itemIdArray[i] + "' value=" + selectArray[i] + " />";
				html += "<td>";
				html += "<td></td>";
				html += "</tr>";
			}
			html += "</table>";
			html += "<span class='hj-qtcs-line_02'>渠道授权</span>";
			html += "<table border='0' class='hj-zm-cj-two' style='margin-left:20px;' width='100%' cellspacing='10px'>";
			var style = " href='javascript:void(0)' style='float:left;margin-right:10px;line-height:50px;white-space: nowrap;' ";
			for(var i = 1; i < selectArray.length; i++) {
				var channelCode = itemIdArray[i].substring(10);
				html += "<tr>";
				html += "<td align='right' width='15%' height='20' nowrap>" + nameArray[i] + "&nbsp;&nbsp;";
				html += "<td align='left' width='800' height='50'; nowrap id=add" + channelCode + ">";
				html += " <a id='add" + channelCode + "A1' " + style + " onclick='BusiParam.addEvent(this,\"" + channelCode + "\")'>添加人员</a>";
				html += " <a id='add" + channelCode + "A2' " + style + " onclick='BusiParam.addEvent(this,\"" + channelCode + "\")'>添加角色</a>";
				html += " <a id='add" + channelCode + "A3' " + style + " onclick='BusiParam.addEvent(this,\"" + channelCode + "\")'>添加用户</a>";
				html += "<td></td>";
				html += "</tr>";
			}
			html += "</table>";
			jl_mb.innerHTML = html;
			for(var j = 1; j < selectArray.length; j++) {
				createCombo("after" + (j+9),itemIdArray[j],selectArray[j]);//因为必须在innerHTML之后才能获取到这个input的id，这里另加一个循环
			}
			BusiParam.show(${moudleParameterForm.hireChannelPriv});
		}
		/**
		 * 创建下拉框 hideId为隐藏域对应id
		 */
		flagnew = "";
		function createCombo(id,hideId,selectValue){
			var store;
			var index=0;
			switch(id){
				case "prev1":
					store = Ext.create('Ext.data.Store',{
						fields:['dataName','dataValue'],
						data:${moudleParameterForm.resumeLevelFieldListJson}
					});
					index=store.find("dataValue","${moudleParameterForm.cultureCode}");
					break;
				case "prev2":
					store = Ext.create('Ext.data.Store',{
						fields:['dataName','dataValue'],
						data:${moudleParameterForm.answerSetListJson}
					});
					index = store.find("dataValue","${moudleParameterForm.answerSet}");
					break;
				case "prev3":
					store = Ext.create('Ext.data.Store',{
						fields:['dataName','dataValue'],
						data:${moudleParameterForm.workExperienceListJson}
					});
					index = store.find("dataValue","${moudleParameterForm.workExperience}");
					break;
				case "prev4":
					store = Ext.create('Ext.data.Store',{
						fields:['dataName','dataValue'],
						data:${moudleParameterForm.unitOrDepartListJson}
					});
					index = store.find("dataValue","${moudleParameterForm.unitOrDepart}");
					break;
				case "candidate_status_div":
                    store = Ext.create('Ext.data.Store',{
                        fields:['dataName','dataValue'],
                        data:${moudleParameterForm.candidate_status_ListJson}
                    });
                    index = store.find("dataValue","${moudleParameterForm.candidate_status}");
                    break;
                    
                    
				case "candidate_status_div2":
                    store = Ext.create('Ext.data.Store',{
                        fields:['dataName','dataValue'],
                        data:${moudleParameterForm.certificate_type_ListJson}
                    });
                    index = store.find("dataValue","${moudleParameterForm.certificate_type}");
                    break;
				case "candidate_status_div3":
                    store = Ext.create('Ext.data.Store',{
                        fields:['dataName','dataValue'],
                        data:${moudleParameterForm.certificate_number_ListJson}
                    });
                    index = store.find("dataValue","${moudleParameterForm.func_only}");
                    break;
                    
				case "prev_attach_codeset":
                    store = Ext.create('Ext.data.Store',{
                        fields:['dataName','dataValue'],
                        data:${moudleParameterForm.resumeLevelFieldListJson}
                    });
                    index = store.find("dataValue","${moudleParameterForm.attachCodeset}");
                    break;
				case "after5":
					store = Ext.create('Ext.data.Store',{
						fields:['dataName','dataValue'],
						data:${moudleParameterForm.hireMajorCodeListJson}
					});
					index = store.find("dataValue","${moudleParameterForm.hireMajorCode}");
					break;
				case "after6":
					store = Ext.create('Ext.data.Store',{
						fields:['dataName','dataValue'],
						data:${moudleParameterForm.previewTableListJson}
					});
					index = store.find("dataValue","${moudleParameterForm.admissionCard}");
					break;
				case "after7":
					store = Ext.create('Ext.data.Store',{
						fields:['dataName','dataValue'],
						data:${moudleParameterForm.script_str}
					});
					index = store.find("dataValue","${moudleParameterForm.personStore}");
					break;
				case "after8":
		            store = Ext.create('Ext.data.Store',{
		                fields:['dataName','dataValue'],
		                data:${moudleParameterForm.previewTableListJson}
		            });
		            index = store.find("dataValue","${moudleParameterForm.scoreCard}");
		            break;
				case "after9":
						var after9Items =new Array();
						after9Items =${moudleParameterForm.script_str};
	                    for(var i = 0;i<after9Items.length;i++){  //循环LIST
	                    	var dataValue = after9Items[i].dataValue;//获取LIST里面的对象
	                        var afterValue =  Ext.getDom("personStore").value;//获取LIST里面的对象
	                        if(dataValue == afterValue && dataValue !="" ){
	                        	after9Items.remove(after9Items[i]);
	                           	break;
	                          }
	                    }
						
						store = Ext.create('Ext.data.Store',{
							fields:['dataName','dataValue'],
							data:after9Items
						});
						index = store.find("dataValue","${moudleParameterForm.destNbase}");
						break;
				default:
		            store = Ext.create('Ext.data.Store',{
		                fields:['dataName','dataValue'],
		                data:${moudleParameterForm.previewTableListJson}
		            });
		            index = store.find("dataValue",selectValue);
		            break;
			}
			if(index==-1)
				index=0;	
			
			 var combox = Ext.create('Ext.form.ComboBox', {
				width:250,
				height:20,
			    store: store,
			    autoSelect:true,
			    id:id + "_check",
			    queryMode: 'local',
			   	displayField: 'dataName',
			    valueField: 'dataValue',
			    fieldStyle:'padding-left:5px',
			    labelPad:0,
			    labelWidth:0,
			    renderTo: id,
			    listeners:{
					blur:{
						fn:function(combox){
							if(!Ext.isEmpty(combox.getValue())){
								var res = store.find("dataValue",combox.getValue());
								if(res==-1){//无效输入值
									combox.setValue(store.getAt(index).data.dataValue);
								}
							}
						}
					},
					beforeselect:{
						fn:function(combox,record){
							var value = record.data.dataValue;
							Ext.getDom(hideId).value=value;
						}
				    }
				}
			});
			if("prev4"==id)
				combox.setWidth(90);
			 
			if("after1,after2,after3".indexOf(id) != -1 && index == 0){
				if(store.getAt(1)!=null){
					combox.setValue(store.getAt(1).data.dataValue);
					Ext.getDom(hideId).value=store.getAt(1).data.dataValue;
				}else{
					flagnew += id+",";
					}
			}else{
				combox.setValue(store.getAt(index).data.dataValue);
			}
			if(id=="prev1"){
				//给第一个下拉框添加选中事件
				cultureList(combox.getValue());
				combox.on("select",function(comb,records){
					cultureList(comb.getValue(),"select");
				});
			}
			
			if(id=="after7"){
				combox.on("select",function(comb,records){
					var destValue =  Ext.getCmp("after9_check").value;
					var afterItems =new Array();
					var dataValue = records.data.dataValue;
					
					if(dataValue == ""){
						afterItems =${moudleParameterForm.script_str};
					}else{
						if(destValue == dataValue){
							Ext.getCmp("after9_check").setValue("");
						}
						
						afterItems =${moudleParameterForm.script_str};
	                    for(var i = 0;i<afterItems.length;i++){  //循环LIST
	                        var afterValue = afterItems[i].dataValue;//获取LIST里面的对象
	                        if(dataValue == afterValue){
	                        	afterItems.remove(afterItems[i]);
	                           	break;
	                          }
	                    }
                    
					}
                    
                    destStore = Ext.create('Ext.data.Store',{
						fields:['dataName','dataValue'],
						data:afterItems
					});
                    
				    Ext.getCmp("after9_check").store.removeAll();
					Ext.getCmp("after9_check").setStore(destStore);
					Ext.getCmp("after9_check").getStore().load();
				});
			
			}
			
			
			if(id=="prev_attach_codeset"){
				//给简历附件分类分渠道设置下拉框添加选中事件
				hireList(combox.getValue());
				combox.on("select",function(comb,records){
				    hireList(comb.getValue(),"select");
				});
			}
			
		}
		init();
		function setAdvanceValue(){
		    var url="/hire/parameterSet/configureParameter/setAdvance.do?b_search=search";
		    var returnValue=window.showModalDialog(url,null, "dialogWidth:600px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
		}
		
		function checkNumber(obj){
			if(obj){
				var re = new RegExp("[^0-9]");
				var num = obj.value;
				var s;
			    if(s = num.match(re)) {
			    	obj.value = 10;
			    	num = 10;
				}
		
			    if(1 > num || 100 < num) {
					alert("简历附件的大小只能限制在1M至100M之间！");
			    	obj.value = "10";	
				}
			}
		}
		
		//分渠道设置按钮
	    Ext.create("Ext.Button", {
	        renderTo: "prev_attach_button",
	        text: "分渠道设置",
	        height:22,
	        id:"attach_Hire",
	        margin:"0 0 0 5",
	        border:false,
	        padding:0,
	        handler: function () {
	            resumeChannel();
	        }
	    });				
		
	</script>
</body>
</html>