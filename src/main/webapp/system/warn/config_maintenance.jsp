<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.businessobject.sys.warn.ContextTools"%>
<script language="JavaScript" src="../../js/showModalDialog.js"></script>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script language="javascript">

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

	function InitData(){
		
		// 预警对象分类(简单0  复杂1)
		var isRole="<bean:write name="warnConfigForm" property="xmlCtrlVo.domainType(isRole)" filter="true"/>";
		var warntyp="<bean:write name="warnConfigForm" property="dynaBean.warntype" filter="true"/>";
		//alert(isRole);
		if( isRole=="0" ) {
			onOrgClick();//组织机构
		}else {
			onRoleClick();//角色
		}			
		if(warntyp=="1" || warntyp=="2")
		{
		     onWarntypUN();
		}else if(warntyp=="3")
		{
		    onWarntypTran();
		}	
		
		//预警对象信息默认值 如 ： UN
		var strDomainCode="<bean:write name="warnConfigForm" property="xmlCtrlVo.strDomain" filter="true"/>";
		//xus 18/3/7谷歌浏览器getElementById取不到值
		document.getElementsByName("xmlCtrlVo.strDomain")[0].value=strDomainCode;

	    //公式类别 (简单公式0  复杂公式1)
		var isComplex="<bean:write name="warnConfigForm" property="xmlCtrlVo.isComplex" filter="true"/>";
		
		if( isComplex=="1") {
			onComplexQueryClick();
		}else {
			onSimpleQueryClick();//默认为简单选中
		}
		
		//预警提示频度：
	    //type="0" 每月   value="1..31"
	    //type="1" 每周   value="1..7"
	    //type="2" 每天   value="1..24"
	    //预警频度默认为-1
		var freqType="<bean:write name="warnConfigForm" property="xmlCtrlVo.strFreqType" filter="true"/>";
		//预警频度值默认为""
		var freqValue="<bean:write name="warnConfigForm" property="xmlCtrlVo.strFreqValue" filter="true"/>";
		if( typeof(freqType)=="undefined" || freqType==""){
			//隐藏下列层
			Element.hide('elemListEreryMonth');
			Element.hide('elemListEreryWeek');
			Element.hide('elemListEreryDay');
		}else if(freqType=="-1"){
            Element.hide('elemListEreryWeek');
            Element.hide('elemListEreryDay');
        }else if( freqType=="0"){//月
			//初始化月份列表默认选中值
			for(var i=0;i<$('listEveryMonth').options.length;i++){
				if( $('listEveryMonth').options[i].value==freqValue){
					$('listEveryMonth').options[i].selected=true;
					//$('listEveryMonth').fireEvent("onchange");
					myFireEvent($('listEveryMonth'));
					break;
				}
			}
			everyMonthClick();
		}else if( freqType=="1"){//周
			//初始化星期列表默认选中值
			for(var i=0;i<$('listEveryWeek').options.length;i++){
				if( $('listEveryWeek').options[i].value==freqValue){
					$('listEveryWeek').options[i].selected=true;
					//$('listEveryWeek').fireEvent("onchange");
					myFireEvent($('listEveryWeek'));
					break;
				}
			}
			everyWeekClick();
		}else if( freqType=="2"){//天
			$('textEveryDay').value=freqValue;
			everyDayClick();
		}
		
		// 预警额外提示 短信 电子邮件(默认为false)
		var isMobile="<bean:write name="warnConfigForm" property="xmlCtrlVo.strMobile" filter="true"/>";
		var isEmail="<bean:write name="warnConfigForm" property="xmlCtrlVo.strEmail" filter="true"/>";
		var isWeixin="<bean:write name="warnConfigForm" property="xmlCtrlVo.strWeixin" filter="true"/>";
		var isDingtalk="<bean:write name="warnConfigForm" property="xmlCtrlVo.strDingtalk" filter="true"/>";
		var isEveryone="<bean:write name="warnConfigForm" property="xmlCtrlVo.strEveryone" filter="true"/>";
		//提示复选框
		var remindCheckItems=document.getElementsByName("xmlCtrlVo.remindArray");
		if(isEmail=="true") remindCheckItems[0].checked=true;
		if(isMobile=="true") remindCheckItems[1].checked=true;
		if(isWeixin=="true") remindCheckItems[2].checked=true;
		if(isDingtalk=="true") remindCheckItems[3].checked=true;
		if(isEveryone=="true")
		{
		  remindCheckItems[4].checked=true;
		  Element.show('send_id');//显示发送摸版
		} 
		
		// cs中有空的valid，默认为启用 (启用/禁用)
		var isValid="<bean:write name="warnConfigForm" property="dynaBean.valid" filter="true"/>";
		if(isValid==""){
			//xus 18/3/7谷歌浏览器getElementById取不到值
			document.getElementsByName('dynaBean.valid')[0].checked="checked";
		}

	   	var pars="isRole=true";
	   //var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:initRoleList,functionId:'1010020305'});
		
		//复杂预警设置条件文本区为失效状态
		//xus 18/3/7谷歌浏览器getElementById取不到值
		var object = document.getElementsByName('dynaBean.csource')[0];
		//obj[0].disabled=(true); //失效变灰
		object.readOnly = (true); //只读
		
		<logic:equal value="1" name="warnConfigForm" property="flag">
			document.getElementById("norderid").value=999;
		</logic:equal>
	}
	function onWarntypUN()
	{
	   emptyTD_nbase();
	   Element.hide('elemNbaseNameShowRow');
	   Element.hide('elemNbaseSetShowRow');
	   Element.hide('elemNbaseShowRow');
	   Element.hide('elemSimpleQueryShow');
	   Element.show('elemTemplateShow');
	   Element.show('elemRemindShow');	
	   Element.hide('elemTranNameShow');  
	   Element.hide('elemTranNameSelectShow');  
	   Element.hide('elemTranSetShow'); 
	   Element.hide("elemRemindEmail");
	   Element.hide("elemRemindMobile");  
	   Element.hide("elemRemindEveryone");
	   Element.hide('send_id');
	  
	   
	}
	function onWarntypTran()
	{
	   emptyTD_nbase();
	   Element.hide('elemNbaseNameShowRow');
	   Element.hide('elemNbaseSetShowRow');
	   Element.hide('elemNbaseShowRow');
	   Element.hide('elemSimpleQueryShow');
	   Element.show('elemTemplateShow');
	   Element.show('elemRemindShow');		  
	   Element.hide("elemRemindEmail");
	   Element.hide("elemRemindMobile");  
	   Element.hide("elemRemindEveryone");
	   Element.hide('send_id');
	   Element.show('elemTranNameShow'); 
	   Element.show('elemTranNameSelectShow'); 
	   Element.show('elemTranSetShow'); 

	   selTranSet();
	   
	}
	function onWarntypEmp()
	{
	    emptyTD_nbase();  
	    Element.show('elemNbaseShowRow');
	    Element.show('elemNbaseNameShowRow');
	    Element.show('elemNbaseSetShowRow');
	    Element.show('elemSimpleQueryShow');
	    Element.show('elemTemplateShow');
	    Element.show('elemRemindShow');
	    Element.hide('elemTranNameShow'); 
	    Element.hide('elemTranNameSelectShow'); 
	    Element.hide('elemTranSetShow'); 
	    Element.show("elemRemindEmail");
	    Element.show("elemRemindMobile");  
	    Element.show("elemRemindEveryone");	  
	    
	}

	function selTranSet()
	{
		var setid=document.getElementsByName("dynaBean.setid")[0].value;
		if(setid!="Q03" && setid!="Q05")
		{
			Element.hide('elemRemindShow');
			Element.hide("elemRemindEmail");
	    	Element.hide("elemRemindMobile");
		}
		else
		{
			Element.show('elemRemindShow');
			Element.show("elemRemindEmail");
	    	Element.show("elemRemindMobile");
		}
	}

	function emptyTD_nbase()
	{
	   var id_vo=document.getElementById("elemNbaseShowRow");     
	   //id_vo.innerHTML="";
	}
	function onOrgClick(){
		//显示组织机构数 隐藏角色集合
		Element.show('elemOrgListTree');
		Element.hide('elemRoleList');
		//单选对象
		var typeRadio = document.getElementsByName("xmlCtrlVo.domainType(isRole)");
		typeRadio[0].checked=""; //角色单选不选中
		typeRadio[1].checked="checked";//组织单选选中状态
	
	}
	
	function onRoleClick(){
		//组织列表隐藏 角色集合显示
		Element.show('elemRoleList');
		Element.hide('elemOrgListTree');
		
		//组织于角色控制对象（单选按钮）
		var typeRadio = document.getElementsByName("xmlCtrlVo.domainType(isRole)");
		typeRadio[0].checked="checked";//角色为选中状态
		typeRadio[1].checked=""; //组织为不选中状态
		
		//onRoleListChange();
		//document.getElementById("xmlCtrlVo.strDomain").value="";
		//document.getElementById("strUnitNames").value="";
	}
	
	function onRoleListChange(){
		var listItem=document.getElementById('listRole'); //角色
		var strDomainItem=document.getElementById('xmlCtrlVo.strDomain');//角色 和 组织公用的预警对象值
		strDomainItem.value=listItem.value;//角色列表值赋给预警对象值
	}
	
	function onComplexQueryClick(){
		//预警规则控制单选按钮
		var typeRadio = document.getElementsByName("xmlCtrlVo.isComplex");		
		if( typeRadio[0].checked ){//简单
			document.getElementsByName('dynaBean.csource')[0].value="";
		}
		typeRadio[1].checked="checked"; //复杂规则为选中状态
		typeRadio[0].checked="";//简单规则为不选中
		Element.hide('elemSimpleShowRow');//隐藏简单
		Element.show('elemComplexShowRow');//显示复杂
		
	}
	
	function onSimpleQueryClick(){
		//预警规则控制单选按钮
		var typeRadio = document.getElementsByName("xmlCtrlVo.isComplex");
		typeRadio[0].checked="checked";
		typeRadio[1].checked="";
		Element.show('elemSimpleShowRow');//显示简单
		Element.hide('elemComplexShowRow');//隐藏复杂
	}
	
	//初始化角色下拉列表
	function initRoleList(outparamters){
		
		var roleList=outparamters.getValue("configRoleCommonDataList");
		AjaxBind.bind( document.getElementById('listRole'), roleList);
		
		//设置选中默认值
		var strDomainCode="<bean:write name="warnConfigForm" property="xmlCtrlVo.strDomain" filter="true"/>";
		for(var i=0;i<$('listRole').options.length;i++){
			if( $('listRole').options[i].value==strDomainCode){
				$('listRole').options[i].selected=true;
				//$('listRole').fireEvent("onchange");
				break;
			}
		}
		
		//预警频度
		var strFreqType='<bean:write name="warnConfigForm" property="xmlCtrlVo.strFreqType" filter="true"/>';
		if(strFreqType=="2"||strFreqType=="-1")
			$('radio_day').fireEvent("onclick"); //执行单击事件
		if(strFreqType=="0")
			$('radio_month').fireEvent("onclick");
		if(strFreqType=="1")
			$('radio_week').fireEvent("onclick");
			
	}
	
	function everyMonthClick(){//单击月
		var freqTypeRadio = document.getElementsByName("xmlCtrlVo.strFreqType");
		freqTypeRadio[0].checked="checked";
		freqTypeRadio[1].checked="";
		freqTypeRadio[2].checked="";
		Element.show('elemListEreryMonth');
		Element.hide('elemListEreryWeek');
		Element.hide('elemListEreryDay');
		//$('listEveryMonth').fireEvent("onchange");
		myFireEvent($('listEveryMonth'));	
	}

	function everyWeekClick(){//单击周
		var freqTypeRadio = document.getElementsByName("xmlCtrlVo.strFreqType");
		freqTypeRadio[0].checked="";
		freqTypeRadio[1].checked="checked";
		freqTypeRadio[2].checked="";
		
		Element.hide('elemListEreryMonth');
		Element.show('elemListEreryWeek');
		Element.hide('elemListEreryDay');
		//$('listEveryWeek').fireEvent("onchange");
		myFireEvent($('listEveryWeek'));	
	}
	
	function everyDayClick(){//单击天
		var freqTypeRadio = document.getElementsByName("xmlCtrlVo.strFreqType");
		freqTypeRadio[0].checked="";
		freqTypeRadio[1].checked="";
		freqTypeRadio[2].checked="checked";
		
		Element.hide('elemListEreryMonth');
		Element.hide('elemListEreryWeek');
		Element.show('elemListEreryDay');
		//$('textEveryDay').fireEvent("onchange"); //执行onchange事件
		myFireEvent($('textEveryDay'));
	}
	
	function everyoneClick()
	{
	    
	    var remindCheckItems=document.getElementsByName("xmlCtrlVo.remindArray");
	    if(remindCheckItems[4].checked==true)
	      Element.show('send_id');
	    else
	      Element.hide('send_id');
	}
	function onFreqValueChange(valueItemName){
		document.getElementsByName("xmlCtrlVo.strFreqValue")[0].value=document.getElementsByName(valueItemName)[0].value;
	}
	
	function openInputCodeDialogOrgWarn(){
	    var data=document.getElementsByName("xmlCtrlVo.strDomain")[0];
	    var selectid=","+data.value+",";
//	    alert(selectid);
	    var re_vo=select_org_emp_dialog(0,1,0,'','','',selectid);
	    if(getBrowseVersion()){//ie浏览器回调方法  wangb 20190318
	    	if(re_vo)
	    	{
	    		var orgNamesItem = document.warnConfigForm.strUnitNames;//机构组织的中文显示框
	    		var hiddenInputItem=document.getElementsByName("xmlCtrlVo.strDomain")[0];
	    		var tmp=re_vo.content;
	    		var len=tmp.length;
	    		hiddenInputItem.value=tmp.substring(0,len-1);
	    		orgNamesItem.value=re_vo.title;
	    	}
	    }
	    
	}
	//子页面回调父页面方法 返回数据  wangb 20190318
	function openReturnValue(re_vo){
		if(re_vo)
	    {
	    	var orgNamesItem = document.warnConfigForm.strUnitNames;//机构组织的中文显示框
	    	var hiddenInputItem=document.getElementsByName("xmlCtrlVo.strDomain")[0];
	    	var tmp=re_vo.content;
	    	var len=tmp.length;
	    	hiddenInputItem.value=tmp.substring(0,len-1);
	    	orgNamesItem.value=re_vo.title;
	    }
	}
	
	/**
	*复杂预警规则定义
	*/
	function exebolishsubmit(){
	    var warnTypeRadio = document.getElementsByName("dynaBean.warntype");
	    var warntype="";	   
	    for(var i=0;i<warnTypeRadio.length;i++)
	    {
	       if(warnTypeRadio[i].checked)
	       {
	          warntype=warnTypeRadio[i].value;
	       }
	    }
	    var setid="";	
	    if(warntype=="3")
	    {
	        var vos = document.getElementsByName("dynaBean.setid");
	        
	        if(vos!=null)
     	    {
     	       var set_vo=vos[0];
     	       if(set_vo)
     	       {
     	          for(var i=0;i<set_vo.options.length;i++)
                  {
                    if(set_vo.options[i].selected)
                    {  
                     setid=set_vo.options[i].value;
                    }
                  }
     	       }
     	       
     	    }            
	    }else
	    {
	       setid="";
	    }
		var url="/system/warn/complexquery.do?b_query=link&setid="+setid+"&warntype="+warntype; 				
		var object = document.getElementsByName('dynaBean.csource')[0];
		var parameter = object.value;

		//改用ext 弹窗显示  wangb 20190318
        var win = Ext.create('Ext.window.Window',{
			id:'exebolish',
			title:'复杂规则',
			width:540,
			height:540,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.obj)
					{
						//obj = obj.replace( /\r|\n/g, " " ); //去掉回车换行
						object.value=this.obj;
					}
				}
			}
		});  
		win.parameter = parameter;
	}
	
	/*通用查询及简单查询
	 *queryType查询类型 =1简单查询 =2通用查询
	 */
	function openSimpleQueryDialog(){
		var info,queryType,dbPre,oldExpress;
		info="1";
		dbPre="Usr";
		queryType="1";
		oldExpress = document.getElementsByName('xmlCtrlVo.strSimpleExpress')[0].value;
		var arguments=new Array(oldExpress);    
		//预警设置中的简单查询和组织机构信息维护登记表中的简单查询冲突，此处加上queryflag参数判断  jingq upd 2014.10.23
		var strurl="/general/query/common/select_query_fields.do?b_init=link`type="+info+"`show_dbpre="+dbPre+"`query_type="+queryType+"`queryflag=1";
		strurl = $URL.encode(strurl);
		var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
		var dw=700,dh=450,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
		if(getBrowseVersion()){
			var strExpression = window.showModalDialog(iframe_url,arguments,"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth=600px;dialogHeight=450px;resizable=no;status=no;");
			if(!strExpression){
				//alert("ok");
				strExpression="";
			}else{
				document.getElementsByName('xmlCtrlVo.strSimpleExpress')[0].value=strExpression;
			}
		}else{
		  //改用ext 弹窗显示  wangb 20190318
          var win = Ext.create('Ext.window.Window',{
			id:'simple_query',
			title:'向导',
			width:dw+20,
			height:dh+20,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(!this.strExpression){
						//alert("ok");
						//this.strExpression="";
					}else{
					 	document.getElementsByName('xmlCtrlVo.strSimpleExpress')[0].value=this.strExpression;
					}
				}
			}
		  });  
		  win.arguments = arguments;
		}
	}
	

	
	
	
	function onWebValid(){
	   
		if(!validate('R','dynaBean.wname','“预警名称”','R','dynaBean.cmsg','“提示内容”'))
		{
			   document.returnValue=false;
		}
		/*if(!($F('xmlCtrlVo.strDomain')))
		{
			alert("预警对象不能为空!");
			document.returnValue=false;
		}*/
		
		//预警规则方式（简单0  复杂1）	
		var typeRadio = document.getElementsByName("xmlCtrlVo.isComplex");		
		// 简单typeRadio[0]; 
		if(typeRadio[0].checked&&document.returnValue){
			validate('I','xmlCtrlVo.strDays','“提前天数”');
		}
		var ruleRadio = document.getElementsByName("xmlCtrlVo.strRule");
		if(ruleRadio)
		{
		    var objRule=ruleRadio[0];
		    if(typeRadio[0].checked)
		      objRule.value="0";
		    else
		       objRule.value="1";
		}
		//预警频度 月 周 日
		var freqTypeRadio = document.getElementsByName("xmlCtrlVo.strFreqType");
		if(freqTypeRadio[2].checked&&document.returnValue){
			var errors='';
			var tempValue = document.getElementsByName('textEveryDay')[0].value;
			var flag=true;
			/*
			if( tempValue.indexOf(':')!=2){
				flag = false;
			}*/
			var reg = /^(\d{1,2}):(\d{1,2})$/; //创建正则表达式校验时间对象
			var r = tempValue.match(reg);
			if(r==null)
				flag=false;			
			if(flag){
				var hour=tempValue.substring(0,2);
				var minute=tempValue.substring(3);
			    if( parseInt(hour)>23 || parseInt(minute)>59){
					flag=false;
				}
			}
			if(!flag){
				alert('时间不匹配00:00-23:59格式');
				document.getElementsByName('textEveryDay')[0].focus();
				document.returnValue=false;
			}
		}
		if(document.returnValue){
			var v=document.getElementById("norderid").value;
			if(v==""){
				alert("请输入优先级!");
				document.returnValue=false;
			}else{
				var checkInteger = /^[+-]?\d+$/;  //创建正则表达式校验整数对象
				if(!checkInteger.test(v)){
					alert("优先级只能输入整数!");
					document.returnValue=false;
				}
			}
		}
	}
	
	function save(){
		document.warnConfigForm.target='_self';
		onWebValid();
		if(document.returnValue == false){
			return false;
		}
		return (document.returnValue && ifqrbc());
	}
	//xus 18/3/6 获取子窗口返回值方法(兼容谷歌浏览器)
	function setnbaseByChildwin(return_vo){
		if(!return_vo)
            return false;
          else
          {
             if(return_vo.flag=="true")
             {
                var id_vo= document.getElementById("nbase");
                id_vo.value=return_vo.nbases;
                id_vo=document.getElementById("elemNbaseShowRow");                        
                id_vo.innerHTML=return_vo.names;
             }
          }
	}
	function setnbase()
	{
		//xus 18/3/6 谷歌浏览器不支持showModalDialog
		var t_url="/system/warn/config_maintenance.do?b_nbase=link";
	    var return_vo=undefined;
	    var theight=300;
	    if(window.showModalDialog){
            theight=260;
		}
	    var config={
	    		width:356,
	            height:theight,
	            id:"setnbaseWin",
	            title:"设置人员库"
	    }
	    return_vo=modalDialog.showModalDialogs(t_url,"人员库设置",config,setnbaseByChildwin)
	    if(return_vo)
	    	setnbaseByChildwin(return_vo);
	}
	//xus 18/3/6 获取子窗口返回值方法(兼容谷歌浏览器)
	function setTemplateByChildwin(return_vo){
		if(!return_vo)
            return false;
          else
          {
        	  if(return_vo.flag=="true")
              {
                 var id_vo= document.getElementById("template");
                 id_vo.value=return_vo.content;
                 id_vo=document.getElementById("text_Template");   
                 var text=return_vo.title;
                 var ids=return_vo.content;
                 if(text!=""&&text.length>0)
                 {
                    var at=text.split(",");
                    var tabids=ids.split(",");
                    text="";
                    for(var i=0;i<at.length;i++)                   
                    {
                       if(tabids[i]!="")
                         text=text+tabids[i]+":"+at[i]+"\r\n";
                    }
                    id_vo.value=text;
                 }else
                 {
                    id_vo.value="";
                 }                     
               
              }
          }
	}
	function setTemplate()
	{
	    var id_vo= document.getElementById("template");
	    var select_id=id_vo.value;
	    //控制是否去掉人员调入业务分类 dr=0去掉,dr=1保留 hej add 2017-3-11
	    var t_url="/system/warn/config_maintenance.do?b_template=link&dr=0&select_id="+select_id;
	    var return_vo=undefined;
	    var theight=400;
        if(window.showModalDialog){
            theight=355;
        }
	    var config={
	    		width:300,
	            height:theight,
	            id:"setTemplateWin",
	            title:"设置业务模板"
	    }
	    return_vo=modalDialog.showModalDialogs(t_url,"设置业务模板",config,setTemplateByChildwin);
	    if(return_vo)
	    	setTemplateByChildwin(return_vo);
	}
	function setDomain()
	{
	    var select_id="";
		var hiddenInputItem=document.getElementsByName("xmlCtrlVo.strDomain")[0];
		select_id=hiddenInputItem.value;
	    var t_url="/system/warn/config_maintenance.do?br_domain=link&role_id="+select_id;
        var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
        /*
		var return_vo= window.showModalDialog(t_url,'rr', 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
        
	     if(!return_vo)
            return false;
         else
          {
             if(return_vo.flag=="true")
             {
                var orgNamesItem = document.warnConfigForm.strUnitNames1;//机构组织的中文显示框
	    	    var hiddenInputItem=document.getElementsByName("xmlCtrlVo.strDomain")[0];
	    	    var tmp=return_vo.content
	    	    var len=tmp.length;
	    	    hiddenInputItem.value=tmp.substring(0,len-1);
	    	    orgNamesItem.value=return_vo.title; 
             }
          }*/
          //改用ext 弹窗显示  wangb 20190318
          var win = Ext.create('Ext.window.Window',{
			id:'select_role_emp',
			title:'选择角色',
			width:dw+20,
			height:dh+20,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+t_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					 if(this.return_vo &&  this.return_vo.flag && this.return_vo.flag=="true")
             		 {
                		var orgNamesItem = document.warnConfigForm.strUnitNames1;//机构组织的中文显示框
	    	    		var hiddenInputItem=document.getElementsByName("xmlCtrlVo.strDomain")[0];
	    	    		var tmp=this.return_vo.content
	    	    		var len=tmp.length;
	    	    		hiddenInputItem.value=tmp.substring(0,len-1);
	    	    		orgNamesItem.value=this.return_vo.title; 
             		 }
				}
			}
		 });   
	}
	function IsDigit() 
    { 
       return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
    } 
    
    function checkInt(e){
    	e=e?e:(window.event?window.event:null);
		var key = window.event?e.keyCode:e.which;
		return ((48<=key&&key<=57)||key==45||key==43);
    }
    
    function refresh(){
    	onWebValid();
    	if(document.returnValue == false){
			return false;
		}
    	<%--预警角色为空不能保存  wangb 20190730 bug 51019 --%>
    	var radio = document.getElementsByName('xmlCtrlVo.domainType(isRole)')[0];
    	if(radio.value == '1' && radio.checked){
    		var strUnitNames1 = document.getElementsByName('strUnitNames1')[0];
    		if(!strUnitNames1.value){
    			alert("预警角色不能为空！");
    			return;
    		}
    	}
    	var warntype =document.getElementsByName('dynaBean.warntype')[0];
    	var id_vo= document.getElementById("nbase");
    	if(!id_vo || id_vo.value == '' && warntype.checked){
    		alert("人事预警必须设置人员库！");
    		return;
    	}
    	/*预警通知消息，取消勾选无效问题处理 wangb 20190823 bug 52464*/
    	var remindCheckItems=document.getElementsByName("xmlCtrlVo.remindArray");
    	var sendMsg=['xmlCtrlVo.strEmail','xmlCtrlVo.strMobile','xmlCtrlVo.strWeixin','xmlCtrlVo.strDingtalk','xmlCtrlVo.strEveryone'];
    	for(var i=0;i<remindCheckItems.length ; i++){
    		if(remindCheckItems[i].checked)
    			document.getElementsByName(sendMsg[i])[0].value=true;
    		else
    			document.getElementsByName(sendMsg[i])[0].value=false;
    	}
    	
    	if(document.returnValue && ifqrbc()){
	    	warnConfigForm.target='_self';
	    	warnConfigForm.action="/system/warn/config_maintenance.do?b_save=refresh";
	   		warnConfigForm.submit();
	  		document.getElementById("resh").disabled=true;
    	}
    }
    //【7253】预警设置中，点击末页，点击修改按钮，再点击返回按钮，应该返回到末页，程序定位到了首页，不对  jingq add 2015.01.30
    function returnback(){
    	window.open("/system/warn/config_manager.do?b_query=link&noreset=1","_self");
    }
</script>

<html:form action="/system/warn/config_maintenance">
	<input name="nbases" type="hidden" value="">
	<table width="740" border="0" cellpadding="0" cellspacing="0" align="center" 	>
		<tr height="20">
			<!-- <td width="10" valign="top" class="tableft"></td>
			<td width="130" align="center" class="tabcenter">&nbsp;<bean:message key="label.sys.warn.config_maintenance" />&nbsp;</td>
			<td width="10" valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="580"></td> -->
			<td  align="left" style="margin-left:5px;" class="TableRow">
				<bean:message key="label.sys.warn.config_maintenance" />
			</td>
		</tr>
		
		<tr>
			<td colspan="4" class="framestyle9">
				<table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0" width="100%">
					<tr class="trDeep" height=30>
						<!--xus 18/3/7 预警页面radio 布局有问题 -->
						<td align="left" nowrap style="width:70px;padding-left:10px;">
							预警类型
						</td>
						<td align="left" nowrap colspan=4><!-- 预警名称-->						
						  <logic:equal name="warnConfigForm" property="flag" value="1">
							<html:radio name="warnConfigForm" property="dynaBean.warntype" value="0" onclick="onWarntypEmp();"/>人事预警 
							<html:radio name="warnConfigForm" property="dynaBean.warntype" value="1" onclick="onWarntypUN();onComplexQueryClick();"/>单位预警 
							<html:radio name="warnConfigForm" property="dynaBean.warntype" value="2" onclick="onWarntypUN();onComplexQueryClick();"/>岗位预警
							<html:radio name="warnConfigForm" property="dynaBean.warntype" value="3" onclick="onWarntypTran();onComplexQueryClick();"/>业务预警
						  </logic:equal>
						  <logic:notEqual name="warnConfigForm" property="flag" value="1">
						    <html:radio name="warnConfigForm" property="dynaBean.warntype" value="0" onclick="onWarntypEmp();" disabled="true"/>人事预警 
							<html:radio name="warnConfigForm" property="dynaBean.warntype" value="1" onclick="onWarntypUN();onComplexQueryClick();" disabled="true"/>单位预警 
							<html:radio name="warnConfigForm" property="dynaBean.warntype" value="2" onclick="onWarntypUN();onComplexQueryClick();" disabled="true"/>岗位预警
							<html:radio name="warnConfigForm" property="dynaBean.warntype" value="3" onclick="onWarntypTran();onComplexQueryClick();" disabled="true"/>业务预警
						     
						  </logic:notEqual>
						</td>
					</tr>
					<tr class="trShallow" height=30>
						<td align="left" nowrap style="padding-left:10px;">
							<bean:message key="column.warn.wname" />
							&nbsp;
						</td>
						<td align="left" nowrap colspan=4><!-- 预警名称-->
							<html:text name="warnConfigForm" property="dynaBean.wname" size="30" maxlength="15" styleClass="text4" />
						</td>
					</tr>
					
					
					
					<tr class="trDeep" height=30>
						
						<!--角色与组织结构都共写一个strDomain隐藏域-->
						<input type="hidden" name="xmlCtrlVo.strDomain" value='<bean:write name="warnConfigForm" property="dynaBean.Obj" filter="true"/>' />

						<td  align="left" nowrap style="padding-left:10px;">
							<bean:message key="column.warn.domain" />
							&nbsp;
						</td>
											
						<td align="left" nowrap width="60px">
							<!--角色控制 -->
							
							<html:radio name="warnConfigForm" property="xmlCtrlVo.domainType(isRole)" value="1" onclick="onRoleClick();" />
							<a href="javascript:void(0)"="javascript:void(0)" onmousemove="this.style.cursor='hand'" onclick="onRoleClick();"><bean:message key="label.sys.warn.domain.role" />...</a>
						</td>
						<td nowrap width="90px" valign="middle">
							<!-- 角色列表-->
							<div id="elemRoleList" nowrap visible="false">
								<!--  
								<select name="listRole" size="1" onchange="onRoleListChange();">
									<option value="temp">
										#donotshowthis
									</option>
								</select>
								-->
								<table><tr>
								<td id="roletd" valign="middle">
								<logic:equal name="warnConfigForm" property="xmlCtrlVo.domainType(isRole)" value="1">
								  <input type="text" name='strUnitNames1' readonly="true" onChange="searchFieldList()"  size="25" value="<bean:write name="warnConfigForm" property="dynaBean.configDomainNames" filter="true"/>" class="text4">
								</logic:equal>
								<logic:notEqual name="warnConfigForm" property="xmlCtrlVo.domainType(isRole)" value="1">
								  <input type="text" name='strUnitNames1' readonly="true" onChange="searchFieldList()"  size="25" value="" class="text4">
								</logic:notEqual>
								   
								</td>
								<td valign="middle">
								  <img src="/images/code.gif" onclick='setDomain();' align="absmiddle"/>
								</td>
								</table>								
								
							</div>
						</td>
												
						<td nowrap align="right" width="90px">
							<!--组织控制 -->
							<html:radio name="warnConfigForm" property="xmlCtrlVo.domainType(isRole)" value="0" onclick="onOrgClick();" />
							<a href="javascript:void(0)" onmousemove="this.style.cursor='hand'" onclick="onOrgClick();openInputCodeDialogOrgWarn();"><bean:message key="label.sys.warn.domain.unit" />...</a>
						</td>
						<td nowrap align="left" width="130px">
							<div id="elemOrgListTree" nowrap>
							   <logic:equal name="warnConfigForm" property="xmlCtrlVo.domainType(isRole)" value="0">
								  <input type="text" name='strUnitNames' readonly="true" onChange="searchFieldList()" size="25" value="<bean:write name="warnConfigForm" property="dynaBean.configDomainNames" filter="true"/>" class="text4">
								</logic:equal>
								<logic:notEqual name="warnConfigForm" property="xmlCtrlVo.domainType(isRole)" value="0">
								  <input type="text" name='strUnitNames' readonly="true" onChange="searchFieldList()" size="25" value="" class="text4">
								</logic:notEqual>
								
								
								<img src="/images/code.gif" onclick='onOrgClick();openInputCodeDialogOrgWarn();' align="absmiddle"/>
							</div>
						</td>
					</tr>
					
					<!-- 公式定义 -->
					<tr class="trShallow" height=30>
						<td align="left" nowrap valign="top" style="padding-left:10px;">
							<bean:message key="column.warn.csource" />
							&nbsp;
						</td>
						
						<td align="left" nowrap colspan="4">
						<input type=hidden name="xmlCtrlVo.strRule" />
						      <table border="0" cellpadding="0" cellspacing="0">
						       <tr>
						          <td>
						             <!-- xus 18/3/7 谷歌浏览器 不支持直接加nowrap的方式 --> 
						             <div id="elemSimpleQueryShow" style="white-space: nowrap;">		
							           <!-- 简单预警设置-->
							            <html:radio name="warnConfigForm" property="xmlCtrlVo.isComplex" value="0" onclick="onSimpleQueryClick();" />
							            <a href="javascript:void(0)" onmousemove="this.style.cursor='hand'" onclick="onSimpleQueryClick();"><bean:message key="label.sys.warn.domain.simple" />...</a>
							         </div>
						          </td>
						          <td nowrap>					              <!-- 复杂预警设置 -->
							          <html:radio name="warnConfigForm" property="xmlCtrlVo.isComplex" value="1" onclick="onComplexQueryClick();" />
							          <a href="javascript:void(0)" onmousemove="this.style.cursor='hand'" onclick="onComplexQueryClick();"> <bean:message key="label.sys.warn.domain.complex" />... </a>
						          </td>
						          <td width="75%">
						            &nbsp;
						          </td>
						       </tr>
						    </table>
							
							
							
						</td>
						
					</tr>
					<!--人员库 -->
					
					<tr class="trDeep" height=30>
						<td  align="left" nowrap style="padding-left:10px;">
						   <div id="elemNbaseNameShowRow" nowrap>							
							预警人员库	
							&nbsp;
							</div>	
							<div id="elemTranNameShow" style='display:none;' nowrap>		
							  业务子集&nbsp;
							</div>		
						</td>
						
						<td align="left" nowrap colspan="3" id="td_nbase">	
						    <div id="elemNbaseShowRow" nowrap>					
							  <bean:write name="warnConfigForm" property="dynaBean.Nbase" />
							</div>
							<div id="elemTranNameSelectShow" style='display:none;' nowrap>								
							    <hrms:optioncollection name="warnConfigForm" property="tranfieldsetlist" collection="list" />
								<html:select name="warnConfigForm" property="dynaBean.setid" size="1" onchange="selTranSet();">
									<html:options collection="list" property="dataValue" labelProperty="dataName" />
								</html:select>
							</div>
						</td>
						<td align="right" nowrap>
						<div id="elemNbaseSetShowRow" nowrap>
						   <input type="hidden" name="xmlCtrlVo.strNbase" value="<bean:write name="warnConfigForm" property="xmlCtrlVo.strNbase" filter="true"/>" id="nbase"/>
							<input type="button" name="savebutton" value="设置" class="mybutton" onclick='setnbase();'>
						</div>
						</td>
					</tr>
					
					<tr class="trShallow" height=30>
						<td align="right" nowrap valign="top">
						<input type="hidden" name="xmlCtrlVo.strSimpleExpress" value="<bean:write name="warnConfigForm" property="xmlCtrlVo.strSimpleExpress" filter="true"/>" />
						</td>
						<td align="left" nowrap colspan="4">
							<!-- 简单预警-->
							<div id="elemSimpleShowRow" nowrap>
								<bean:message key="label.sys.warn.firstsimple" />
								<html:text name="warnConfigForm" property="xmlCtrlVo.strDays" size="4" maxlength="4" style="text-align:right"  />
								<bean:message key="label.sys.warn.lastsimple" />
								<input type="button" name="savebutton" value="<bean:message key="button.sys.warn.guide"/>" class="mybutton" onclick='openSimpleQueryDialog();'>
							</div>
							
							<!-- 复杂预警 -->
							<div id="elemComplexShowRow" nowrap style="border:none;">
								<table cellpadding="0" cellspacing="0" border="0"><tr>
								<td align="left" width="96%" valign="bottom">
								<!-- xus 18/3/7 34369 陈总提 火狐浏览器 系统管理/应用设置/参数设置/预警设置/新增 输入框与旁边的按钮没有距离 -->
								<html:textarea name="warnConfigForm" property="dynaBean.csource"  rows="4" style="width:583px;"/>
								</td><td align="right" valign="bottom" width="4%">
								<input type="button" name="savebutton" value="<bean:message key="button.sys.warn.guide"/>" class="mybutton" onclick='exebolishsubmit();'>
								</td></tr></table>
							</div>
						</td>
					</tr>
					<tr class="trDeep" height=30>
						<td align="left" nowrap valign="top" style="margin-top:10px;padding-left:10px;">
							<div style="margin-top:10px;">
							<bean:message key="column.warn.cmsg" />
							&nbsp;
							</div>
						</td>
						<td align="left" nowrap colspan="4" style="margin-top:10px;">
							<div style="margin-top:10px;">
							<!-- xus 18/3/7 34369 陈总提 火狐浏览器 系统管理/应用设置/参数设置/预警设置/新增 输入框与旁边的按钮没有距离 -->
							<html:textarea name="warnConfigForm" property="dynaBean.cmsg" rows="7" style="width:583px;" />
							</div>
						</td>
					</tr>
					<tr class="trShallow" height=30>
					  
						<td  align="left" nowrap valign="top" style="padding-left:10px;">
						 <div id="elemTemplateShow" nowrap style="margin-top:10px;">
							业务模板
							&nbsp;
						</div>
						</td>
					    <td align="left" nowrap colspan="4">
					     <div id="elemTemplateShow" nowrap style="margin-top:10px;">
					     <!-- xus 18/3/7 34369 陈总提 火狐浏览器 系统管理/应用设置/参数设置/预警设置/新增 输入框与旁边的按钮没有距离 -->
					     <table cellpadding="0" cellspacing="0" border="0" width="100%"><tr>
					     <td align="left" width="96%" valign="bottom">
					    <html:textarea name="warnConfigForm" property="dynaBean.Template" rows="7" style="width:583px;"  readonly="true" styleId="text_Template"/>
					    </td>
					    <td align="right" valign="bottom" width="96%">
					    <input type="button" name="savebutton" value="设置" class="mybutton" onclick='setTemplate();'>
					    <input type="hidden" name="xmlCtrlVo.strTemplate" value="<bean:write name="warnConfigForm" property="xmlCtrlVo.strTemplate" filter="true"/>" id="template"/>
					    </td></tr>
					    </table>
					    </div>	
						</td>
											
					</tr>
					<tr class="trDeep" height=30>
						<td valign="center" align="left" nowrap valign="top" style="padding-left:10px;">
							<bean:message key="column.warn.ntype" />
							&nbsp;
						</td>
						<td align="left" nowrap colspan="4">
							<html:hidden name="warnConfigForm" property="xmlCtrlVo.strFreqValue" />
							<table>
								<tr>
									<td nowrap>
										<!-- 预警频度 月 周 天-->
										<input type="radio" id="radio_month" name="xmlCtrlVo.strFreqType" onclick="everyMonthClick();" value="0" checked/>
										<a href="javascript:void(0)" onmousemove="this.style.cursor='hand'" onclick="everyMonthClick();"><bean:message key="label.sys.warn.freq.everymonth" /></a>
									</td>
									<td>
										<div id="elemListEreryMonth" show="false">
											<select name="listEveryMonth" size="1" onchange="onFreqValueChange('listEveryMonth');">
												<%for (int i = 1; i < 32; i++) {%>
												<option value="<%=i%>">
													<%=i%>
												</option>
												<%}%>
											</select>
										</div>
									</td>
									<td nowrap>
										<input type="radio" id="radio_week" name="xmlCtrlVo.strFreqType" onclick="everyWeekClick();" value="1" />
										<a href="javascript:void(0)" onmousemove="this.style.cursor='hand'" onclick="everyWeekClick();"><bean:message key="label.sys.warn.freq.everyweek" /></a>
									</td>
									<td>
										<div id="elemListEreryWeek">
											<select name="listEveryWeek" size="1" onchange="onFreqValueChange('listEveryWeek');">
												<%for (int i = 1; i < 8; i++) {%>
												<option value="<%=i%>">
													<%=ContextTools.getStringWeek("" + i)%>
												</option>
												<%}%>
											</select>
										</div>
									</td>
									<td nowrap>
										<input type="radio" id="radio_day" name="xmlCtrlVo.strFreqType" onclick="everyDayClick();" value="2" />
										<a href="javascript:void(0)" onmousemove="this.style.cursor='hand'" onclick="everyDayClick();"><bean:message key="label.sys.warn.freq.everyday" /></a>
									</td>
									<td>
										<div id="elemListEreryDay"><!-- 【6091】预警中，框线颜色不对   jingq upd 2014.12.17 -->
											<input type="text" name="textEveryDay" size="5" maxlength="5" class="text4" value="08:30" onchange="onFreqValueChange('textEveryDay');" />
										</div>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					
					
					<!-- 邮件 或 短信 -->
					<tr class="trShallow" height=30>
						<td  align="left" nowrap valign="top" style="padding-left:10px;"> 
						<div id="elemRemindShow" nowrap style="margin-top:0px;">
							<bean:message key="label.sys.warn.remind" />
							&nbsp;
						 </div>
						</td>
						<td align="left" nowrap colspan="4">
						
						  <table>
						    <tr>
						      <td>
						      <div id="elemRemindEmail" nowrap>
						         <html:multibox name="warnConfigForm" property="xmlCtrlVo.remindArray" value="email" />
						          <bean:message key="label.sys.warn.remind.email" />
						          <input type="hidden" name="xmlCtrlVo.strEmail" value="<bean:write name="warnConfigForm" property="xmlCtrlVo.strEmail" filter="true"/>" />
							  </div> 
							  </td>
							  <td>						    
							  <div id="elemRemindMobile" nowrap> 
							     <html:multibox name="warnConfigForm" property="xmlCtrlVo.remindArray" value="mobile" />
							     <bean:message key="label.sys.warn.remind.mobile" />
							     <input type="hidden" name="xmlCtrlVo.strMobile" value="<bean:write name="warnConfigForm" property="xmlCtrlVo.strMobile" filter="true"/>" />
							  </div>  
							  </td>
							  
							   <td>						    
							  <div id="elemRemindWeiXin" nowrap> 
							     <html:multibox name="warnConfigForm" property="xmlCtrlVo.remindArray" value="weixin" />
							     微信提醒
							      <input type="hidden" name="xmlCtrlVo.strWeixin" value="<bean:write name="warnConfigForm" property="xmlCtrlVo.strWeixin" filter="true"/>" />
							  </div>  
							  </td>
							  
							  <td>						    
							  <div id="elemRemindDingTalk" nowrap> 
							     <html:multibox name="warnConfigForm" property="xmlCtrlVo.remindArray" value="dingtalk" />
							     钉钉提醒
							      <input type="hidden" name="xmlCtrlVo.strDingtalk" value="<bean:write name="warnConfigForm" property="xmlCtrlVo.strDingtalk" filter="true"/>" />
							  </div>  
							  </td>
							<td width="200px">
							</td >  
							</tr>
						    <tr> 
							  <td colspan="2">
							  <div id="elemRemindEveryone" nowrap>  
							     <html:multibox name="warnConfigForm" property="xmlCtrlVo.remindArray" value="everyone" onclick="everyoneClick();"/>
							      通知预警结果本人
							       <input type="hidden" name="xmlCtrlVo.strEveryone" value="<bean:write name="warnConfigForm" property="xmlCtrlVo.strEveryone" filter="true"/>" />
							   </div>
						      </td>
						    
						      <td colspan="4">
						         <div  id="send_id" style='display:none;'>
							       邮件模板
							      <html:select name="warnConfigForm" property="xmlCtrlVo.strNote" size="1">
                                    <html:optionsCollection property="emailtemplateList" value="dataValue" label="dataName"/>	        
                                  </html:select>
                                       发送间隔   
                                  <html:text name="warnConfigForm" property="xmlCtrlVo.strSendspace" size="5" maxlength="5" styleClass="text4" onkeypress="event.returnValue=IsDigit();" />天
						         </div>
						      </td>
						    </tr>
						  </table>
						 
						</td>
					</tr>
					
					
					
					<tr class="trDeep" height=30>
						<td valign="center" align="left" nowrap valign="top" style="padding-left:10px;">
							<bean:message key="column.warn.valid" />
							&nbsp;
						</td>
						<td align="left" nowrap colspan="4">
							<html:radio name="warnConfigForm" property="dynaBean.valid" value="1" />
							<bean:message key="column.sys.valid" />
							<html:radio name="warnConfigForm" property="dynaBean.valid" value="0" />
							<bean:message key="column.sys.invalid" />
						</td>
					</tr>
					<tr class="trDeep" height=30>
						<td align="left" nowrap valign="top" style="padding-left:10px;">
							<bean:message key="conlumn.board.priority" />
							&nbsp;
						</td>
						<td align="left" nowrap colspan="4">
							<html:text property="dynaBean.norder" styleId="norderid" name="warnConfigForm" size="5" onkeypress="return checkInt(event);" maxlength="5" styleClass="text4"></html:text>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr class="list3">
			<td align="center" style="height:35px;">
				<!-- <hrms:submit styleClass="mybutton" property="b_save" onclick="document.warnConfigForm.target='_self';onWebValid();return (document.returnValue && ifqrbc());">
					<bean:message key="button.save" />
				</hrms:submit>-->
				<input type="button" name="b_save" class="mybutton" id="resh" onclick="refresh();" value="<bean:message key="button.save" />"/>
				
				<!--<hrms:submit styleClass="mybutton" property="br_return">
					<bean:message key="button.return" />
				</hrms:submit>-->
				<input type="button" class="mybutton" onclick="returnback();" value="<bean:message key="button.return" />"/>
			</td>
		</tr>
	</table>
</html:form>

<script language="javascript">
	//alert("start");
	InitData();
	if(!getBrowseVersion()){//非ie浏览器样式兼容问题修改  wangb 20190318
		var elemComplexShowRow= document.getElementById('elemComplexShowRow');
		var savebutton = elemComplexShowRow.getElementsByTagName('input')[0];
		savebutton.style.marginLeft = '34px';
	}
</script>
