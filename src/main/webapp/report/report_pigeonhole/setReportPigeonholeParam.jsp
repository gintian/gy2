<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html>
  <head>

  </head>
  <script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
  <script language="javascript" src="/module/utils/js/template.js"></script>
  <script language="JavaScript" src="/js/constant.js"></script>
  <script language='javascript' >
  function setPigeonholeReport(type)
  {
  		var operate=type;
  		for(var i=0;i<document.reportPigeonholeForm.operate.length;i++)
  		{
  			if(document.reportPigeonholeForm.operate[i].value==type)
  				document.reportPigeonholeForm.operate[i].checked=true;
  		}
  		
  		
		var infos=new Array();
		var thecodeurl="/report/report_pigeonhole/reportBatchPigeonhole.do?b_setReport=1&operate="+operate;
		var info
		if(operate==1)
			info= window.showModalDialog(thecodeurl, infos,"dialogWidth:700px; dialogHeight:450px;resizable:yes;center:yes;scroll:yes;status:no");	
  		if(operate==2)
			info= window.showModalDialog(thecodeurl, infos,"dialogWidth:900px; dialogHeight:650px;resizable:yes;center:yes;scroll:yes;status:no");	
  		if(info==undefined)
  			return;
  		document.reportPigeonholeForm.selectedIDs.value=info.substring(1);
  		//alert(document.reportPigeonholeForm.selectedIDs.value)
  }


	function setUnit()
	{
			var operate="2";
	  		for(var i=0;i<document.reportPigeonholeForm.selectUnitType.length;i++)
	  		{
	  			if(document.reportPigeonholeForm.selectUnitType[i].value=="2")
	  				document.reportPigeonholeForm.selectUnitType[i].checked=true;
	  		}
	  		
			
			
			// var infos=new Array();
			var thecodeurl="/report/report_collect/reportOrgCollecttree2.jsp?type=pigeonhole";
			//兼容谷歌 wangbs 20190318
			Ext.create("Ext.window.Window",{
				id:'selectArchiveUnit',
				title:'选择归档单位',
				width:340,
				height:350,
				resizable:false,
				modal:true,
				autoScroll:false,
				autoShow:true,
				autoDestroy:true,
				html:'<iframe style="background-color:#fff;" frameborder="0" scrolling=no height="100%" width="100%" src="'+thecodeurl+'"></iframe>',
				renderTo:Ext.getBody(),
				listeners:{
					close:function(){
                        if(this.info){
                            document.reportPigeonholeForm.unitIDs.value=this.info;
                        }
					}
				}
			});

			// var info= window.showModalDialog(thecodeurl, infos,"dialogWidth:300px; dialogHeight:350px;resizable:yes;center:yes;scroll:no;status:no");
			// if(info==undefined)
  			// 	return;
  			// document.reportPigeonholeForm.unitIDs.value=info;
	}

	function setNulll()
	{
		document.reportPigeonholeForm.selectedIDs.value="";
	}
	
	
	function sub()
	{
		var operate="1";	
		if(document.reportPigeonholeForm.selectedIDs.value=="")
		{
			alert(REPORT_INFO53+"!");
			return;
		}
		
		var selectUnitType="";
  		for(var i=0;i<document.reportPigeonholeForm.selectUnitType.length;i++)
  		{
  			if(document.reportPigeonholeForm.selectUnitType[i].checked==true)
  				selectUnitType=document.reportPigeonholeForm.selectUnitType[i].value;
  		}
  		if(selectUnitType==2)
  		{
  			if(document.reportPigeonholeForm.unitIDs.value.length==0)
			{
	  			alert(REPORT_INFO54+"！");
				return;
			}
  		}
  		
		
		var hashvo=new ParameterSet();
	   	hashvo.setValue("operate",operate);
	   	hashvo.setValue("selectedIDs",document.reportPigeonholeForm.selectedIDs.value);
		var In_paramters="flag=1"; 		
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo3,functionId:'05601000034'},hashvo);
	}
	var infos;
	var year_value;
	function returnInfo3(outparamters)
	{
		year_value="";
		var info=outparamters.getValue("info");
		var narch=outparamters.getValue("narch");
		if(info.length==0)
		{
			infos=narch;
			var thecodeurl="/report/report_pigeonhole/selectYear2.jsp"; 
			
			Ext.create("Ext.window.Window",{
				id:'selectArchiveUnit',
				title:'报表归档',
				width:430,
				height:330,
				resizable:false,
				modal:true,
				autoScroll:false,
				autoShow:true,
				autoDestroy:true,
				html:'<iframe style="background-color:#fff;" frameborder="0" scrolling=no height="100%" width="100%" src="'+thecodeurl+'"></iframe>',
				renderTo:Ext.getBody(),
				listeners:{
					close:function(){
                       	if(year_value)
               			{			
                			var operate="1";
                			var selectUnitType="";
                	  		for(var i=0;i<document.reportPigeonholeForm.selectUnitType.length;i++)
                	  		{
                	  			if(document.reportPigeonholeForm.selectUnitType[i].checked==true)
                	  				selectUnitType=document.reportPigeonholeForm.selectUnitType[i].value;
                	  		}
                			var hashvo=new ParameterSet();
                		    hashvo.setValue("operate",operate); 
                		    hashvo.setValue("reportType",year_value[0]);
                			hashvo.setValue("year",year_value[1]);
                			if(year_value[0]>2)
                			   hashvo.setValue("count",year_value[2]);
                			if(year_value[0]==6)
                			   hashvo.setValue("week",year_value[3]);   
                			   
                			hashvo.setValue("selectedIDs",document.reportPigeonholeForm.selectedIDs.value);
                			hashvo.setValue("selectUnitType",selectUnitType);
                			hashvo.setValue("unitIDs",document.reportPigeonholeForm.unitIDs.value);
                			var In_paramters="flag=1"; 		
                			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo4,functionId:'05601000025'},hashvo);
               			}
                			
					}
				}
			});
		}
		else
			alert(info);
	}
	
	function returnInfo4(outparamters)
	{
	
		var info=outparamters.getValue("info");
		if(info==1)
			alert(COLLECTSUCCESS+"！");
		else 
		{
			var temp=info.split("~@");
			alert(getDecodeStr(temp[1])+" "+REPORT_INFO64+"!");
			
		}
	}
	
	function narchSet()
	{
		var operate="1";
  	/*	for(var i=0;i<document.reportPigeonholeForm.operate.length;i++)
  		{
  			if(document.reportPigeonholeForm.operate[i].checked==true)
  				operate=document.reportPigeonholeForm.operate[i].value;
  		}*/
		if(document.reportPigeonholeForm.selectedIDs.value.length==0)
		{
			if(operate==1)
  				alert(REPORT_INFO53+"！");	
  			else 
  				alert(REPORT_INFO56+"！");
			return;
		}
		var dw = 430;
		var dh = 330;
		if(getBrowseVersion()&&getBrowseVersion()!=10){
            dw =410;
			dh =330;
		}
		// var infos=new Array();
		var thecodeurl="/report/report_pigeonhole/selectYear.jsp";
		//兼容谷歌 wangbs 20190318
		Ext.create("Ext.window.Window",{
            id:'formArchiveType',
            title:'设置归档类型',
            width:dw,
            height:dh,
            resizable:false,
            modal:true,
            autoScroll:false,
            autoShow:true,
            autoDestroy:true,
            html:'<iframe style="background-color:#fff;" frameborder="0" scrolling=no height="100%" width="100%" src="'+thecodeurl+'"></iframe>',
            renderTo:Ext.getBody(),
			listeners:{
                close:function(){
                    if(this.year_value){
                        var hashvo=new ParameterSet();
                        hashvo.setValue("narch",this.year_value);
                        hashvo.setValue("operate",operate);
                        hashvo.setValue("selectedIDs",document.reportPigeonholeForm.selectedIDs.value);
                        var In_paramters="flag=1";
                        var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'05601000031'},hashvo);
					}
				}
			}
		});
		// var year_value= window.showModalDialog(thecodeurl, infos,
		//         "dialogWidth:400px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");
	}
	
	function replaceAll( str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	   
	    return str;
	}
	
	function returnInfo(outparamters)
	{
		
		var info=getDecodeStr(outparamters.getValue("info"));
		
		var operate="1";
		var selectedIDs=outparamters.getValue("selectedIDs");
		var narch=outparamters.getValue("narch");
		if(info.length>0)
		{
	    	
			info=replaceAll(info,'#','\r\n  ')
			if(confirm(REPORT+"：\r\n  "+info+"\r\n\r\n"+REPORT_INFO57+"？"))
			{
				var hashvo=new ParameterSet();
				hashvo.setValue("narch",narch); 
			   	hashvo.setValue("operate",operate);
			   	hashvo.setValue("selectedIDs",selectedIDs);
				var In_paramters="flag=1"; 		
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'05601000032'},hashvo);
					
			}
		}
		else
		{
				var hashvo=new ParameterSet();
				hashvo.setValue("narch",narch); 
			   	hashvo.setValue("operate",operate);
			   	hashvo.setValue("selectedIDs",selectedIDs);
				var In_paramters="flag=1"; 		
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'05601000032'},hashvo);
				
		}
	}
	
	
	function returnInfo2(outparamters)
	{
		var info=outparamters.getValue("info");
		if(info==1)
			alert(SETSUCCESS+"！");
		else
			alert(SETFAILURE+"！");
	}
	
	
  </script>
  <hrms:themes />
  <style>
  .mybutton{
  	width: 85px;
  	padding:0 5px 0 5px;
  }
  </style>
  <body>
  <html:form action="/report/report_pigeonhole/reportBatchPigeonhole">			
 <table width="540" height="250px" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top:20px">
          <tr height="20"> 
       		<td align="left" colspan="4" class="TableRow" style="padding-left: 5px;"><bean:message key="report_collect.pigeonhole"/>&nbsp;</td>             	      
          </tr> 
          <tr height="150">
            <td colspan="4" class="framestyle3" style="width:540px" >
				<table width='100%' border="0" align="left">
					<tr valign="top">
						<td width='80%'>
							<fieldset align="left" style="width:90%;"><legend ><bean:message key="report.reportlist.selectReportSet"/></legend>
								<table border="0" cellspacing="0" width="100%" align="left" cellpadding="0" >					
									<tr>
										<td width="100%" align="center"><br>
										<html:select name="reportPigeonholeForm" property="selectedIDs" size="1" style="width:90%;">
					                        <html:optionsCollection property="infoList" value="dataValue" label="dataName"/>
					        			</html:select><br>&nbsp;
										</td>
									</tr>
								</table>
							</fieldset>
			</td>
			<td  width='20%' align="left" valign='bottom' style="">
				<table style="margin-left: -2px;margin-top: 6px">
					<tr>
						<td>
							<hrms:priv func_id="2903401">
							    <Input type='button' name='a' value='<bean:message key="report_collect.pigeonholeType"/>' class="mybutton" onclick="narchSet()" ><br><br>	
							</hrms:priv>
								<Input type='button' name='a' value=' <bean:message key="report_collect.pigeonhole2"/>' class="mybutton" onclick="sub()" >
						</td>
					</tr>
				</table>
			</td>
		  </tr>
		  <tr>
			<td width='80%' >
				<fieldset align="left" style="width:90%;"><legend ><bean:message key="report_collect.collectUnit"/></legend>
					<table border="0" cellspacing="0"  align="left" cellpadding="0" >					
						<tr>
							<td width="100%" style="padding-left:23px; ">
								<html:radio name="reportPigeonholeForm" property="selectUnitType" value="1"   />&nbsp;<bean:message key="task.state.all"/>
							</td>									
						</tr>	
						<tr>
							<td valign='top' width="100%" style="padding-left:23px; ">
								<html:radio name="reportPigeonholeForm" property="selectUnitType" value="2"   />&nbsp;<bean:message key="report_collect.lotsUnit"/>
								<a href='javascript:setUnit()'><img style="vertical-align: middle;" border=0 src='/images/code.gif' ></a>
							</td>								
						</tr>			
					</table>
				</fieldset>
			</td>
		  </tr>
		  <tr>
		  	<td height="0px"></td>
		  </tr>
		</table>
			
			</td>
          </tr>    
          <tr height="30" align="center">
          <td>
          <hrms:tipwizardbutton flag="report" target="il_body" formname="reportPigeonholeForm"/> 
          </td> 
          </tr>
          
      </table>
	  <input type='hidden' name='unitIDs'   value='' />
  </html:form>
  </body> 
</html>
