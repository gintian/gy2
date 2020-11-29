
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript>
	
	function getParticularItem()
	{
		var provisionTerm=document.reportAnalyseForm.provisionTerm.value;
		
			var hashvo=new ParameterSet();
		hashvo.setValue("tabid","${reportAnalyseForm.reportTabid}"); 
		hashvo.setValue("unitcode","${reportAnalyseForm.unitcode}"); 
		hashvo.setValue("reportTypes","${reportAnalyseForm.reportTypes}"); 
		hashvo.setValue("provisionTerm",provisionTerm); 
		hashvo.setValue("backdate","${reportAnalyseForm.backdate2}"); 
		In_paramters='flag=1';	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'03030000016'},hashvo);		
		
	}
	
	function returnInfo(outparamters)
	{
		var fieldlist=outparamters.getValue("defaultItemList");	
		var provisionTerm=outparamters.getValue("provisionTerm");
		var vos,right_vo,i;
	  vos= document.getElementsByName("right_fields");
	  if(vos!=null){
	  right_vo=vos[0];
	  for(i=right_vo.options.length-1;i>=0;i--)
	  {
	
			right_vo.options.remove(i);
		
	  }	
	  }
//		if(provisionTerm.substring(0,2)=="##"){
		AjaxBind.bind(reportAnalyseForm.left_fields,fieldlist);
//		}
		
	}
	
	
	//总计
	function totalize()
	{
	var provisionTerm=document.reportAnalyseForm.provisionTerm.value;
	if(provisionTerm.substring(0,2)=="##"){
	var vos= eval('document.reportAnalyseForm.right_fields');  
  		if(vos==null)
  			return false;
        var no = new Option();
    	no.value=':'+TOTALACCOUNT+'::2:';
    	no.text=TOTALACCOUNT;
    	vos.options[vos.options.length]=no;
	}else{
	var vos= eval('document.reportAnalyseForm.right_fields');  
  		if(vos==null)
  			return false;
        var no = new Option();
    	no.value=':'+TOTALACCOUNT+':总计;9;9;,:2:;';/*xiegh add 2005;1;2;,为归档时间标识 bug:28986  */
    	no.text=TOTALACCOUNT;
    	vos.options[vos.options.length]=no;
	}
	
	
		
	}
	
	
	//flag 1:撤选  2:全撤 
	function removeitem(sourcebox_id,flag)
	{
	
	var vos,right_vo,i;
	  vos= document.getElementsByName(sourcebox_id);
	  if(vos==null)
	  	return false;
	  right_vo=vos[0];
	  for(i=right_vo.options.length-1;i>=0;i--)
	  {
	  	if(flag==1)
	  	{
		    if(right_vo.options[i].selected)
		    {
			right_vo.options.remove(i);
		    }
		}
		else if(flag==2)
		{
			right_vo.options.remove(i);
		}
	  }
	  return true;	  	
	
	  
	}
	
	
	//全选
	function selectAll(sourcebox_id)
	{
	
	vos= document.getElementsByName(sourcebox_id);
		if(vos==null)
	  		return false;
	    right_vo=vos[0];
		for(i=right_vo.options.length-1;i>=0;i--)
	 	{
	 		right_vo.options[i].selected=true;
	 	}
	
		
	}
	
	//合并
	function merger(sourcebox_id,targetbox_id)
	{
	var provisionTerm=document.reportAnalyseForm.provisionTerm.value;
	
	if(provisionTerm.substring(0,2)=="##"){
	 var variable=window.prompt(REPORT_INFO47,"");
		 if(variable==null||variable==''||variable==' ')
		 {
			return;
		 }
		  if(variable.indexOf("#")!=-1||variable.indexOf("\"")!=-1||variable.indexOf(";")!=-1||variable.indexOf("@@")!=-1)
		 {
		 	alert(REPORT_INFO65);
		 	return;
		 }
		  var left_vo,right_vo,vos,i;
		  vos= document.getElementsByName(sourcebox_id);
		  if(vos==null)
		  	return;
		  left_vo=vos[0];
		  vos= document.getElementsByName(targetbox_id);  
		  if(vos==null)
		  	return;
		  right_vo=vos[0];  
		  var provisionTerm=eval("document.reportAnalyseForm.provisionTerm");		  
		  var no = new Option();	 
		  var final_value="";
		  if(provisionTerm.value.substring(0,2)=='##')
		  {
		    		no.value="UN:"+variable+":";
		  }
		  else
		  {
		    		var left=provisionTerm.value.split("##");
		    		no.value=left[0]+":"+variable+":";
		    		final_value=left[1];
		   }
		  
		  var mergerItem="";
		  for(i=0;i<left_vo.options.length;i++)
		  {
		    if(left_vo.options[i].selected)
		    {
		    	mergerItem+="\'"+left_vo.options[i].value+"\',";
		    	
		    }
  		  }
  		  if(mergerItem=="")
  		  {
  		  	alert(REPORT_INFO49+"!");
  		  	return;
  		  }  
  		  no.value+=mergerItem+":1:"+final_value;
  		  no.text=variable;
  		  right_vo.options[right_vo.options.length]=no;
	}else{
	var variable=window.prompt(REPORT_INFO47,"");
		 if(variable==null||variable==''||variable==' ')
		 {
			return;
		 }
		 if(variable.indexOf("#")!=-1||variable.indexOf("\"")!=-1||variable.indexOf(";")!=-1||variable.indexOf("@@")!=-1)
		 {
		 	alert(REPORT_INFO65);
		 	return;
		 }
		  var left_vo,right_vo,vos,i;
		  vos= document.getElementsByName(sourcebox_id);
		  if(vos==null)
		  	return;
		  left_vo=vos[0];
		  vos= document.getElementsByName(targetbox_id);  
		  if(vos==null)
		  	return;
		  right_vo=vos[0];  
		 // var provisionTerm=eval("document.reportAnalyseForm.provisionTerm");		  
		  var no = new Option();	 
		  var final_value="";
		 // if(provisionTerm.value.substring(0,2)=='##')
		 // {
		 //   		no.value="UN:"+variable+":";
		 // }
		//  else
		//  {
		    //		var left=provisionTerm.value.split("##");
		    //		no.value=":"+variable+":";
		    //		final_value=left[1];
		//   }
		  
		  var mergerItem="";
		  for(i=0;i<left_vo.options.length;i++)
		  {
		    if(left_vo.options[i].selected)
		    {
		    	mergerItem+=""+left_vo.options[i].value+",";
		    
		    }
  		  }
  		  if(mergerItem=="")
  		  {
  		  	alert(REPORT_INFO49+"!");
  		  	return;
  		  }  
  		  no.value=":"+variable+":"+mergerItem+":1:";
  		  no.text=variable;
  		  right_vo.options[right_vo.options.length]=no;
	}
		 
  		
	}
	//平均值
	function avg(sourcebox_id,targetbox_id)
	{
		  var left_vo,right_vo,vos,i;
		  vos= document.getElementsByName(sourcebox_id);
		  if(vos==null)
		  	return;
		  left_vo=vos[0];
		  vos= document.getElementsByName(targetbox_id);  
		  if(vos==null)
		  	return;
		  right_vo=vos[0];  
		  var provisionTerm=eval("document.reportAnalyseForm.provisionTerm");		  
		  var no = new Option();	 
		  var final_value="";
		
		    		no.value="UN:平均值:";
		 
		  
		  var mergerItem="";
		  for(i=0;i<left_vo.options.length;i++)
		  {
		    if(left_vo.options[i].selected)
		    {
		    	mergerItem+=""+left_vo.options[i].value+",";
		    	
		    }
  		  }
  		  if(mergerItem=="")
  		  {
  		   	alert(REPORT_INFO70+"!");
  		  	return;

  		  }  
  		  no.value+=mergerItem+":5:"+final_value;
  		  no.text="平均值";
  		  right_vo.options[right_vo.options.length]=no;
  		
	}
	//最大值
	function maxValue(sourcebox_id,targetbox_id)
	{
		  var left_vo,right_vo,vos,i;
		  vos= document.getElementsByName(sourcebox_id);
		  if(vos==null)
		  	return;
		  left_vo=vos[0];
		  vos= document.getElementsByName(targetbox_id);  
		  if(vos==null)
		  	return;
		  right_vo=vos[0];  
		  var provisionTerm=eval("document.reportAnalyseForm.provisionTerm");		  
		  var no = new Option();	 
		  var final_value="";
		
		    		no.value="UN:最大值:";
		 
		  
		  var mergerItem="";
		  for(i=0;i<left_vo.options.length;i++)
		  {
		    if(left_vo.options[i].selected)
		    {
		    	mergerItem+=""+left_vo.options[i].value+",";
		    	
		    }
  		  }
  		  if(mergerItem=="")
  		  {
  		   	alert(REPORT_INFO71+"!");
  		  	return;
  		  }  
  		  no.value+=mergerItem+":4:"+final_value;
  		  no.text="最大值";
  		  right_vo.options[right_vo.options.length]=no;
  		
	}
	//最小值
	function minValue(sourcebox_id,targetbox_id)
	{
		  var left_vo,right_vo,vos,i;
		  vos= document.getElementsByName(sourcebox_id);
		  if(vos==null)
		  	return;
		  left_vo=vos[0];
		  vos= document.getElementsByName(targetbox_id);  
		  if(vos==null)
		  	return;
		  right_vo=vos[0];  
		  var provisionTerm=eval("document.reportAnalyseForm.provisionTerm");		  
		  var no = new Option();	 
		  var final_value="";
		 
		    		no.value="UN:最小值:";
		 
		  
		  var mergerItem="";
		  for(i=0;i<left_vo.options.length;i++)
		  {
		    if(left_vo.options[i].selected)
		    {
		    	mergerItem+=""+left_vo.options[i].value+",";
		    	
		    }
  		  }
  		  if(mergerItem=="")
  		  {
  		   	alert(REPORT_INFO72+"!");
  		  	return;
  		  }  
  		  no.value+=mergerItem+":3:"+final_value;
  		  no.text="最小值";
  		  right_vo.options[right_vo.options.length]=no;
  		
	}
	
	//选择  1:按条件选取  0：全选
	function selectItem(flag)
	{
		var provisionTerm=document.reportAnalyseForm.provisionTerm.value;
	if(provisionTerm.substring(0,2)=="##"){
	 var left_vo,right_vo,vos,i;
		  vos= document.getElementsByName("left_fields");
		  if(vos==null)
		  	return;
		  left_vo=vos[0];
		  vos= document.getElementsByName("right_fields");  
		  if(vos==null)
		  	return;
		  right_vo=vos[0];
		  
		  var provisionTerm=eval("document.reportAnalyseForm.provisionTerm");		  
		  for(i=0;i<left_vo.options.length;i++)
		  {
		  		if(flag==1)
		  		{
					    if(left_vo.options[i].selected)
					    {
					    	  var no = new Option();
							  var left_text=left_vo.options[i].text.split(":");
							  var final_value="";
							  if(provisionTerm.value.substring(0,2)=='##')
							  {
							    		no.value="UN:";
							  }
							  else
							  {
							    		var left=provisionTerm.value.split("##");
							    		no.value=left[0]+":";
							    		final_value=left[1];
							   }
					    	  no.value+=left_text[1]+":\'"+left_text[0]+"\':0:"+final_value;	
					    	  no.text=left_text[1];					    	  
					    	  right_vo.options[right_vo.options.length]=no;
					    	  
					    }
					 
				}
				else
				{
					      var no = new Option();
							  var left_text=left_vo.options[i].text.split(":");
							  var final_value="";
							  if(provisionTerm.value.substring(0,2)=='##')
							  {
							    		no.value="UN:";
							  }
							  else
							  {
							    		var left=provisionTerm.value.split("##");
							    		no.value=left[0]+":";
							    		final_value=left[1];
							 
							   }
					    	  no.value+=left_text[1]+":\'"+left_text[0]+"\':0:"+final_value;	
					    	  no.text=left_text[1];
					    	  right_vo.options[right_vo.options.length]=no;
			    }
  		  }
	}else{
	var left_vo,right_vo,vos,i;
		  vos= document.getElementsByName("left_fields");
		  if(vos==null)
		  	return;
		  left_vo=vos[0];
		  vos= document.getElementsByName("right_fields");  
		  if(vos==null)
		  	return;
		  right_vo=vos[0];
		  
		  var provisionTerm=eval("document.reportAnalyseForm.provisionTerm");	  
		  for(i=0;i<left_vo.options.length;i++)
		  {
		  		if(flag==1)
		  		{
					    if(left_vo.options[i].selected)
					    {
					    	  var no = new Option();
							  var left_text=left_vo.options[i].value.split(";");
							
					    	  no.value+=":"+left_vo.options[i].value+":"+left_vo.options[i].value+":0";	
					    	  no.text=getText(left_text);
					    	 				    	  
					    	  right_vo.options[right_vo.options.length]=no;
					    	  
					    }
					 
				}
				else
				{
					       var no = new Option();
						   var left_text=left_vo.options[i].value.split(";");
							
					    	no.value+=":"+left_vo.options[i].value+":"+left_vo.options[i].value+":0";	
					    	no.text=getText(left_text);
					    	right_vo.options[right_vo.options.length]=no;
			    }
  		  }
	}
		  
	}
	//获得name
	function  getText(left_text){
	var text ='';
	text+=left_text[0]+REPORT_PIGIONHOLE_YEAR;
	if(left_text[2]=="1"){//一般报表
	text+=left_text[1]+REPORT_PIGIONHOLE_COUNT;
	}else if(left_text[2]=="2"){//年
	
	}else if(left_text[2]=="3"){//半年
	if(left_text[1]=="1")
	text+=REPORT_PIGIONHOLE_UPHALFYRAR;
	else
	text+=REPORT_PIGIONHOLE_DOWNHALFYEAR;
	}else if(left_text[2]=="4"){//季度
	if(left_text[1]=="1")
	text+=REPORT_PIGIONHOLE_ONEQUARTER;
	else if(left_text[1]=="2")
	text+=REPORT_PIGIONHOLE_TOWQUARTER;
	else if(left_text[1]=="3")
	text+=REPORT_PIGIONHOLE_THREEQUARTER;
	else if(left_text[1]=="4")
	text+=REPORT_PIGIONHOLE_FOURQUARTER;
	}else if(left_text[2]=="5"){//月
	if(left_text[1]=="1")
	text+=REPORT_PIGIONHOLE_JANUARY;
	else if(left_text[1]=="2")
	text+=REPORT_PIGIONHOLE_FEBRUARY;
	else if(left_text[1]=="3")
	text+=REPORT_PIGIONHOLE_MARCH;
	else if(left_text[1]=="4")
	text+=REPORT_PIGIONHOLE_APRIL;
	else if(left_text[1]=="5")
	text+=REPORT_PIGIONHOLE_MAY;
	else if(left_text[1]=="6")
	text+=REPORT_PIGIONHOLE_JUNE;
	else if(left_text[1]=="7")
	text+=REPORT_PIGIONHOLE_JULY;
	else if(left_text[1]=="8")
	text+=REPORT_PIGIONHOLE_AUGUEST;
	else if(left_text[1]=="9")
	text+=REPORT_PIGIONHOLE_SEPTEMBER;
	else if(left_text[1]=="10")
	text+=REPORT_PIGIONHOLE_OCTOBER;
	else if(left_text[1]=="11")
	text+=REPORT_PIGIONHOLE_NOVEMBER;
	else if(left_text[1]=="12")
	text+=REPORT_PIGIONHOLE_DECEMBER;
	}else if(left_text[2]=="6"){//周
	if(left_text[1]=="1")
	text+=REPORT_PIGIONHOLE_JANUARY;
	else if(left_text[1]=="2")
	text+=REPORT_PIGIONHOLE_FEBRUARY;
	else if(left_text[1]=="3")
	text+=REPORT_PIGIONHOLE_MARCH;
	else if(left_text[1]=="4")
	text+=REPORT_PIGIONHOLE_APRIL;
	else if(left_text[1]=="5")
	text+=REPORT_PIGIONHOLE_MAY;
	else if(left_text[1]=="6")
	text+=REPORT_PIGIONHOLE_JUNE;
	else if(left_text[1]=="7")
	text+=REPORT_PIGIONHOLE_JULY;
	else if(left_text[1]=="8")
	text+=REPORT_PIGIONHOLE_AUGUEST;
	else if(left_text[1]=="9")
	text+=REPORT_PIGIONHOLE_SEPTEMBER;
	else if(left_text[1]=="10")
	text+=REPORT_PIGIONHOLE_OCTOBER;
	else if(left_text[1]=="11")
	text+=REPORT_PIGIONHOLE_NOVEMBER;
	else if(left_text[1]=="12")
	text+=REPORT_PIGIONHOLE_DECEMBER;
	
	if(left_text[3]=="1")
	text+=REPORT_PIGIONHOLE_ONE_WEEK;
	else if(left_text[3]=="2")
	text+=REPORT_PIGIONHOLE_TOW_WEEK;
	else if(left_text[3]=="3")
	text+=REPORT_PIGIONHOLE_THREE_WEEK;
	else if(left_text[3]=="4")
	text+=REPORT_PIGIONHOLE_FOUR_WEEK;
	else if(left_text[3]=="5")
	text+=REPORT_PIGIONHOLE_FIVE_WEEK;
	else if(left_text[3]=="6")
	text+=REPORT_PIGIONHOLE_SIX_WEEK;
	}
	
	return text;
	}
	
	//保存方案
	function saveScheme()
	{

	var vos= document.getElementsByName("right_fields"); 
		if(vos==null)
		  	return;
		var right_vo=vos[0];
		var a_value='';
		for(i=0;i<right_vo.options.length;i++)
		{

				a_value+="##"+right_vo.options[i].value;
		}
		if(a_value=='')
		{
			alert(REPORT_INFO50);
			return;
		}
		var a_scheme=eval("document.reportAnalyseForm.scheme");
		var secid=0;
		if(a_scheme.value==0)
		{
			 var no = new Option();
			 
			 var maxValue=0;
			 for(var a=0;a<a_scheme.options.length;a++)
			 {
				if(a_scheme.options[a].value>0)
				{
					maxValue=a_scheme.options[a].value;
				} 
			 }
			 
			 
			 no.value=maxValue*1+1;	
			 secid=maxValue*1+1;	
			 no.text=REPORTPLAN+secid;					    	  
			 a_scheme.options[a_scheme.options.length]=no;
			 a_scheme.options[0].selected=true;	 
		}
		else
		{
			for(var a=0;a<a_scheme.options.length;a++)
			{
				if(a_scheme.options[a].selected==true)
					secid=a_scheme.options[a].value;
			}
		}

		var hashvo=new ParameterSet();
		hashvo.setValue("tabid","${reportAnalyseForm.reportTabid}"); 
		hashvo.setValue("unitcode","${reportAnalyseForm.unitcode}"); 
		hashvo.setValue("secid",secid); 
		hashvo.setValue("content",a_value.substring(2)); 
		hashvo.setValue("type","1"); 
		In_paramters='flag=1';	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'03030000017'},hashvo);		
	
	
	}
	
	
	//删除方案
	function delScheme()
	{
	var a_scheme=eval("document.reportAnalyseForm.scheme");
		var a_value;
		for(var a=0;a<a_scheme.options.length;a++)
		{
				if(a_scheme.options[a].selected==true)
					a_value=a_scheme.options[a].value;
		}
		if(a_value!=0)
		{
			var hashvo=new ParameterSet();
			hashvo.setValue("tabid","${reportAnalyseForm.reportTabid}"); 
			hashvo.setValue("unitcode","${reportAnalyseForm.unitcode}"); 
			hashvo.setValue("secid",a_value); 
			hashvo.setValue("type","1");
			In_paramters='flag=1';	
		    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo4,functionId:'03030000021'},hashvo);		
		}
	}
		

	
	
	function returnInfo4(outparamters)
	{	
	
	var secid=outparamters.getValue("secid");
		var a_scheme=eval("document.reportAnalyseForm.scheme");
		a_scheme.options[0].selected=true
	    removeitem('right_fields',2)
	    
	    for(var a=0;a<a_scheme.options.length;a++)
		{
				if(a_scheme.options[a].value==secid)
					a_scheme.options.remove(a);	
		}
	}
		

	
	
	function returnInfo2(outparamters)
	{
		alert(SAVESUCCESS);
	}
	
	
	//选择方案
	function selectScheme()
	{
	
	var a_scheme=eval("document.reportAnalyseForm.scheme");
		var a_value;
		for(var a=0;a<a_scheme.options.length;a++)
		{
				if(a_scheme.options[a].selected==true)
					a_value=a_scheme.options[a].value;
		}
		removeitem('right_fields',2)
		if(a_value!=0)
		{
				var hashvo=new ParameterSet();
				hashvo.setValue("tabid","${reportAnalyseForm.reportTabid}"); 
				hashvo.setValue("unitcode","${reportAnalyseForm.unitcode}"); 
				hashvo.setValue("secid",a_value); 
				hashvo.setValue("type","1");
				In_paramters='flag=1';	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo3,functionId:'03030000018'},hashvo);		
		
		}
	
		
	}
	
	//返回方案结果
	function returnInfo3(outparamters)
	{
		
	var content=outparamters.getValue("content");
		var vos= document.getElementsByName("right_fields"); 
		var right_vo=vos[0];
		if(content.indexOf("##")==-1)
		{
				var no = new Option();
			 	no.value=content;	
			 	var a_value=content.split(":");
			 	if(a_value[1].indexOf(";")>=0){
				no.text=getText(a_value[1].split(";"));
				}else{
				no.text=a_value[1];
				}
				right_vo.options[right_vo.options.length]=no;
		}	
		else
		{
			var a_content=content.split('##');
		
			for(var a=0;a<a_content.length;a++)
			{
				var no = new Option();
			 	no.value=a_content[a];	
			 	var a_value=a_content[a].split(":");
				if(a_value[1].indexOf(";")>=0){
				no.text=getText(a_value[1].split(";"));
				}else{
				no.text=a_value[1];
				}
					
				right_vo.options[right_vo.options.length]=no;
			}
		}
	
	
		
	}
	
	
	<% 
	if(request.getParameter("opt")!=null&&request.getParameter("opt").trim().equalsIgnoreCase("new"))
	{
	%>
		window.open("/report/report_analyse/reportanalyse.do?br_executeTable=exce","_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");//xiegh 26519 半屏改成全屏
	
	<%
	}
	%>
	
	
	
	
	//生成综合表
	function executeTable()
	{
	
	var vos= document.getElementsByName('right_fields');
		if(vos==null){
			alert(SELECTLEFTITEM);
			return false ;
		}
	  	
	    var right_vo=vos[0];
	     if(right_vo.options.length<1){
	     alert(SELECTLEFTITEM);
			return false ;
	     }
//	    if(right_vo.options.length>140)
//	    {
//	    	alert(REPORT_INFO51+"！");
//	    	return;
//	    }

		    for(i=right_vo.options.length-1;i>=0;i--)
	 	{

	 		right_vo.options[i].selected=true;
	 	
	 	}
		  
	 	//reportAnalyseForm.action="/report/report_analyse/reportanalyse.do?b_executeTable=exce";
		//reportAnalyseForm.submit();
		 var temp_vo = new Object();
		 var values ='';
		    for(i=0;i<right_vo.options.length-1;i++)
	 	{

	 		values+=right_vo.options[i].value+"@@";
	 	
	 	}
	 	values+=right_vo.options[right_vo.options.length-1].value;
	 	values=getEncodeStr(values);
	 	//temp_vo.values = getEncodeStr(values);
	 	//alert(values);
	 	//window.returnValue=temp_vo;
   		//window.close();
   		 var cols='<%=(request.getParameter("cols"))%>';
   		  var selfUnitcode='<%=(request.getParameter("unitcode"))%>';
   		   var tabid='<%=(request.getParameter("tabid"))%>';
   		 var nums='<%=(request.getParameter("nums"))%>';
   		  var reportTypes='<%=(request.getParameter("reportTypes"))%>';
   		    var yearid='<%=(request.getParameter("yearid"))%>';
   		      var reportCount='<%=(request.getParameter("reportCount"))%>';
   		        var weekid='<%=(request.getParameter("weekid"))%>';
   		         var width='<%=(request.getParameter("width"))%>';
   		          var height='<%=(request.getParameter("height"))%>';
   		          document.reportAnalyseForm.rightfields.value=values;
  		 reportAnalyseForm.action="/report/report_analyse/reportanalyse.do?b_executeTable=exce&opt=new&cols="+cols+"&unitcode="+selfUnitcode+"&tabid="+tabid+"&nums="+nums+"&reportTypes="+reportTypes+"&flag=1&yearid="+yearid+"&reportCount="+reportCount+"&weekid="+weekid;
  		reportAnalyseForm.target="_self";
  		reportAnalyseForm.submit();
		
	}
	
	
	
	 function outFile(outparamters)
	 {

   window.close();
	 }
	
	
	function additem(sourcebox_id,targetbox_id)
	{
	var provisionTerm=document.reportAnalyseForm.provisionTerm.value;
	if(provisionTerm.substring(0,2)=="##"){
	var left_vo,right_vo,vos,i;
 	    vos= document.getElementsByName(sourcebox_id);

  		if(vos==null)
  			return false;
  		left_vo=vos[0];
  		vos= document.getElementsByName(targetbox_id);  
  		if(vos==null)
  			return false;
 		right_vo=vos[0];
 		var provisionTerm=eval("document.reportAnalyseForm.provisionTerm");	
  		for(i=0;i<left_vo.options.length;i++)
  		{
    		if(left_vo.options[i].selected)
    		{
        	
        	
        		  var no = new Option();
				  var left_text=left_vo.options[i].text.split(":");
				  var final_value="";
				  if(provisionTerm.value.substring(0,2)=='##')
				  {
							    		no.value="UN:";
				  }
				  else
				  {
							    		var left=provisionTerm.value.split("##");
							    		no.value=left[0]+":";
							    		final_value=left[1];
				  }
				  no.value+=left_text[1]+":\'"+left_text[0]+"\':0:"+final_value;	
				  no.text=left_text[1];					    	  
				  right_vo.options[right_vo.options.length]=no;
    		}
  		}
	
	
	}else{
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
        	
        	
        		    var no = new Option();
					var left_text=left_vo.options[i].value.split(";");
							
					no.value+=":"+left_vo.options[i].value+":"+left_vo.options[i].value+":0";	
					no.text=getText(left_text);					    	  
				    right_vo.options[right_vo.options.length]=no;
    		}
  		}
	
	
	}
	
	
	}
	
	
	
	</SCRIPT>
<hrms:themes />
<style>
.mybutton{
	width:50px;
	padding:0 5px 0 5px;
}
</style>
<html:form  action="/report/report_analyse/reportanalyse">
<html:hidden name="reportAnalyseForm" property="rightfields" />
	<br>
	<br>
	<table  width="100%">
	<tr>
	<td>
	<fieldset align="center" style="width:90%;">
		<legend>
			<bean:message key="report_collect.executeTable"/>
		</legend>

		<table width="100%" height="290">
			<tr>
				<td width="45%" height="268" align="center" >
					<table width="100%" >
						
						<tr>
							<td width="100%" align="left">

								<select name="provisionTerm"  style="width:100%" onchange="getParticularItem()">
									<logic:iterate id="element" name="reportAnalyseForm" property="provisionTermList">
										<option value='<bean:write name="element" property="value" />'>
											<bean:write name="element" property="name" />
										</option>
									</logic:iterate>
								</select>
							</td>
						</tr>	
						<tr>
							<td width="100%"  align="left">
								<select name="left_fields" multiple="multiple" size="10" ondblclick="additem('left_fields','right_fields');" style="height:230px;width:100%;font-size:9pt">
									<logic:iterate id="element" name="reportAnalyseForm" property="defaultItemList">
										<option value='<bean:write name="element" property="value" />'>
											<bean:write name="element" property="name" />
										</option>
									</logic:iterate>
								</select>
							</td>
						</tr>
					</table>
				</td>

				<td width="11%" align="center" valign="center">
					<table width="100%">
						<tr>
							<td style="height:25px" align="center">
								<input type="button" name="b_up" value="  <bean:message key="workdiary.message.total"/>  " onClick="totalize();" class="mybutton">
							</td>
						</tr>
						<tr>
							<td style="height:25px" align="center">					
								<input type="button" name="b_up" value="  <bean:message key="button.combine"/>  " onClick="merger('left_fields','right_fields')" class="mybutton">
							</td>
						</tr>
						<tr>
							<td style="height:25px" align="center">
								<input type="button" name="b_up" value="平均值 " onClick=" avg('left_fields','right_fields')" class="mybutton">
							</td>
						</tr>
						<tr>
							<td style="height:25px" align="center">
								<input type="button" name="b_up" value="最大值 " onClick="maxValue('left_fields','right_fields')" class="mybutton">
							</td>
						</tr>
						<tr>
							<td style="height:25px" align="center">
								<input type="button" name="b_up" value="最小值 " onClick="minValue('left_fields','right_fields')" class="mybutton">
							</td>
						</tr>
						<tr>
							<td style="height:25px" align="center">
								<input type="button" name="b_up" value="  <bean:message key="column.select"/>  " onClick="selectItem(1);" class="mybutton">
							</td>
						</tr>
						<tr>
							<td style="height:25px" align="center">
								<input type="button" name="b_up" value="  <bean:message key="label.query.selectall"/>  " onClick="selectItem(0);" class="mybutton">
							</td>
						</tr>
						<tr>
							<td style="height:25px" align="center">
								<input type="button" name="b_up" value="  <bean:message key="train.job.remove"/>  " onClick="removeitem('right_fields','1');" class="mybutton">
							</td>
						</tr>
						<tr>
							<td style="height:25px" align="center">
								<input type="button" name="b_up" value="  <bean:message key="lable.performance.clear"/>  " onClick="removeitem('right_fields','2');" class="mybutton">
							</td>
						</tr>					
					</table>
				</td>
				<td width="45%" align="center">
					<table width="100%">
						<tr>
							<td width="100%" align="left">
								<select name="scheme" style="width:100%" onchange='selectScheme()'>
									<logic:iterate id="element" name="reportAnalyseForm" property="schemeList">
										<option value='<bean:write name="element" property="value" />'>
											<bean:write name="element" property="name" />
										</option>
									</logic:iterate>
								</select>
							</td>
						</tr>
						<tr>
							<td width="100%" align="left">
								<select name="right_fields" multiple="multiple" size="10" ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">

								</select>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			
		</table>

	</fieldset>
	</td>
</tr>
<tr>
				<td colspan="3" style="height:35px" align="center">
					<input type="button" name="b_down" value="<bean:message key="report_collect.saveplan"/>" onClick="saveScheme();" class="mybutton" style="width:60px">
					<input type="button" name="b_down" value="<bean:message key="report_collect.deleteplan"/>" onClick="delScheme();" class="mybutton" style="width:60px">
					<input type="button" name="b_down" value="<bean:message key="kq.formula.true"/>" onClick="executeTable();" class="mybutton">
					<input name="reset" type="button" class="mybutton" onClick="removeitem('right_fields','2')" value="<bean:message key="button.clear"/>">
					<input name="button" type="button" class="mybutton" onClick="window.close()" value="<bean:message key="button.close"/>">

				</td>
			</tr>

</table>
</html:form>
