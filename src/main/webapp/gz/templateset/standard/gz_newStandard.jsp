<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
    
  </head>
   <style>
  	.RecordRow_self {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
	font-weight: bold;
	}
  
  </style>
    <hrms:themes />
  <script language='javascript' >
  /*
   *type: 0:横向栏目 1:横向子栏目  2:纵向栏目  3:纵向子栏目  4:结果指标
   */
  function addItem(type)
  {
 	   
        var infos=new Array();
  		var thecodeurl="/gz/templateset/standard.do?br_selectItem=select`type="+type;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
        var return_value= window.showModalDialog(iframe_url, infos, 
		        "dialogWidth:420px; dialogHeight:360px;resizable:no;center:yes;scroll:no;status:no");	
	    if(return_value!=null)
		{
				
				
				var itemid=setContent_factor(type,return_value);
				var In_paramters="type="+type;
				var hashvo=new ParameterSet();
				hashvo.setValue("itemid",itemid); 	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:ReturnInfo,functionId:'3020010111'},hashvo);			
		
			
		}       
		          
  }
  
  function ReturnInfo(outparamters)
  {
   	 	 var itemName=outparamters.getValue("itemName");
		 var type=outparamters.getValue("type");
		 var obj=eval("a"+type);
		 if(itemName!=null&&itemName!='')
			 obj.innerHTML=itemName;
  }
  
  function analyseReturnValue(returnValue)
  {
  	 var list=new Array();
  	 var temps=returnValue.split(",");
  	 var num=0;
  	 var index=0;
  	 for(var i=0;i<temps.length;i++)
  	 {
  	 	if(trim(temps[i]).length>0)
  	 	{
  	 		var temps1=temps[i].split("~");
  	 		if(num==0)
  	 		{
  	 			if(temps1[0]==3||temps1[0]==4||temps1[0]==5||temps1[0]==6)
  	 			{
  	 				list[index]=temps1[1].split("/")[0];
  	 			}
  	 			else if(temps1[0]==7||temps1[0]==8)
  	 			{
  	 				list[index]=temps1[1].split("#")[0];
  	 			}
  	 			index++;
  	 			num++;
  	 		}
			if(temps1[0]==2)
  	 			list[index]=temps1[1];
  	 		else
	  	 		list[index]=temps1[1].split("#")[1];
  			index++;
  		}
  	 }
  	 return list;
  }
  /*
   *type: 0:横向栏目 1:横向子栏目  2:纵向栏目  3:纵向子栏目  4:结果指标
   */
  function setContent_factor(type,returnValue)
  {
  	 var a_returnValue=analyseReturnValue(returnValue);
  	// alert(a_returnValue+"    "+returnValue);
  	 
  
	  	 if(type==0)  //0:横向栏目
	  	 {
	  	 	document.salaryStandardForm.hfactor.value=a_returnValue[0]; 
	  	 	var content="";
	  	 	var a_content1="";
	  	 	if(document.salaryStandardForm.s_hfactor.value.length>0)
	  	 	{	
	  	 		var a_content0=document.salaryStandardForm.hcontent.value;
	  	 		a_content1=a_content0.substring(1);
	  	 		a_content1=a_content1.substring(0,a_content1.length-1);
	  	 	}
	  	 	for(var i=1;i<a_returnValue.length;i++)
	  	 	{
	  	 			content=content+";"+a_returnValue[i]+"["+a_content1+"]";
	  	 	} 
	  	 	document.salaryStandardForm.hcontent.value=content.substring(1);
	  	 	
	  	 //	alert(document.salaryStandardForm.hfactor.value+"  "+document.salaryStandardForm.hcontent.value);
	  	 }
	  	 else if(type==1) //1:横向子栏目
	  	 {
	  		document.salaryStandardForm.s_hfactor.value=a_returnValue[0]; 
	  		var content="";
	  		
	  		var a_content0="";
	  		for(var i=1;i<a_returnValue.length;i++)
	  	 	{
	  	 			a_content0=a_content0+","+a_returnValue[i];
	  	 	} 
	  	 	
	  		if(document.salaryStandardForm.hfactor.value.length>0)
	  		{
	  			var a=document.salaryStandardForm.hcontent.value;			
				var a_content1="";
				var temps=a.split(";");
				for(var i=0;i<temps.length;i++)
				{
					a_content1=a_content1+";"+temps[i].substring(0,temps[i].indexOf("[")+1)+a_content0.substring(1)+"]";
				}
	  			content=a_content1.substring(1);
	  		}
	  		else
	  		{
	  			content="["+a_content0.substring(1)+"]"
	  		}
	  		document.salaryStandardForm.hcontent.value=content;
	  		
	  	//	alert(document.salaryStandardForm.s_hfactor.value+"  "+document.salaryStandardForm.hcontent.value);
	  	 }
	  	 else if(type==2)  //2:纵向栏目
	  	 {
	  	 	document.salaryStandardForm.vfactor.value=a_returnValue[0]; 
	  	 	var content="";
	  	 	var a_content1="";
	  	 	if(document.salaryStandardForm.s_vfactor.value.length>0)
	  	 	{	
	  	 		var a_content0=document.salaryStandardForm.vcontent.value;
	  	 		a_content1=a_content0.substring(1);
	  	 		a_content1=a_content1.substring(0,a_content1.length-1);
	  	 	}
	  	 	for(var i=1;i<a_returnValue.length;i++)
	  	 	{
	  	 			content=content+";"+a_returnValue[i]+"["+a_content1+"]";
	  	 	} 
	  	 	document.salaryStandardForm.vcontent.value=content.substring(1);
	  	 	
	  	 //	alert(document.salaryStandardForm.vfactor.value+"  "+document.salaryStandardForm.vcontent.value);
	  	 }
	  	 else if(type==3) //3:纵向子栏目
	  	 {
	  		document.salaryStandardForm.s_vfactor.value=a_returnValue[0]; 
	  		var content="";
	  		
	  		var a_content0="";
	  		for(var i=1;i<a_returnValue.length;i++)
	  	 	{
	  	 			a_content0=a_content0+","+a_returnValue[i];
	  	 	} 
	  	 	
	  		if(document.salaryStandardForm.vfactor.value.length>0)
	  		{
	  			var a=document.salaryStandardForm.vcontent.value;			
				var a_content1="";
				var temps=a.split(";");
				for(var i=0;i<temps.length;i++)
				{
					a_content1=a_content1+";"+temps[i].substring(0,temps[i].indexOf("[")+1)+a_content0.substring(1)+"]";
				}
	  			content=a_content1.substring(1);
	  		}
	  		else
	  		{
	  			content="["+a_content0.substring(1)+"]"
	  		}
	  		document.salaryStandardForm.vcontent.value=content;
	  		
	  	//	 alert(document.salaryStandardForm.s_vfactor.value+"  "+document.salaryStandardForm.vcontent.value);
	  	 }
	  	 else if(type==4) //4:结果指标
	  	 {
	  	 	document.salaryStandardForm.item.value=a_returnValue[1];
	  	 	a_returnValue[0]=a_returnValue[1];
	  	 //	alert(document.salaryStandardForm.item.value);
	  	 }
	
  	 return a_returnValue[0];
  }
  
  
  
  function enter()
  {
  		
  		if(document.salaryStandardForm.hfactor.value==""&&document.salaryStandardForm.s_hfactor.value==""&&document.salaryStandardForm.item.value=="")
  		{
  			 
  			return;
  		}
  		if(document.salaryStandardForm.vfactor.value==""&&document.salaryStandardForm.s_vfactor.value==""&&document.salaryStandardForm.item.value=="")
  		{
  			return;
  		}
  		if(document.salaryStandardForm.vfactor.value==""&&document.salaryStandardForm.s_vfactor.value==""&&document.salaryStandardForm.hfactor.value==""&&document.salaryStandardForm.s_hfactor.value=="")
  		{
  			return;
  		}
  		if(document.salaryStandardForm.item.value=="" )
  		{
  			return;
  		}
  	//	alert(document.salaryStandardForm.hfactor.value+"   "+document.salaryStandardForm.s_hfactor.value);
  	//	alert(document.salaryStandardForm.vfactor.value+"   "+document.salaryStandardForm.s_vfactor.value);
  		document.salaryStandardForm.action="/gz/templateset/standard.do?b_initItem=init&typeSpec=1";
  		document.salaryStandardForm.submit();
  }
  
  
  function goback()
  {
  	document.salaryStandardForm.action="/gz/templateset/standard.do?b_query=query&pkg_id=${salaryStandardForm.pkg_id}";
  	document.salaryStandardForm.submit();
  }
  
  
  </script>
  <body>
    <html:form action="/gz/templateset/standard">

    <table width="700px" border="1" bordercolor="#94B6E6" cellspacing="1"  align="center" cellpadding="1" class="ListTable complex_border_color" style="margin-top:60px;">
    	<tr>
    		 <td colspan="2" rowspan="2"  align="center" width='30%' class="RecordRow_self  common_background_color common_border_color"  >&nbsp;</td>
    		<td colspan="3" id='a0' onclick='addItem(0)'  align="center" class="TableRow_5rows  common_background_color common_border_color"  height='35'  width='70%'  bgcolor='#f4f7f7' >${salaryStandardForm.hfactor_name}</td>  		
    	</tr>
    	
    	<tr> 
		    <td align="center"  onclick='addItem(1)' class="TableRow_5rows  common_background_color common_border_color"  height='35' width='20%' bgcolor='#f4f7f7' >&nbsp;</td>
		    <td align="center"  id='a1' onclick='addItem(1)' class="TableRow_5rows  common_background_color common_border_color"  height='35' width='20%'   bgcolor='#f4f7f7' >${salaryStandardForm.s_hfactor_name}</td>
		    <td align="center"  onclick='addItem(1)' class="TableRow_5rows  common_background_color common_border_color"  height='35' width='20%'   bgcolor='#f4f7f7' >&nbsp;</td>
	    </tr>
    	
    	 <tr> 
		    <td  align="center"  id='a2' onclick='addItem(2)'  class="RecordRow_self  common_background_color common_border_color"  bgcolor='#f4f7f7' height='200'   rowspan="3">${salaryStandardForm.vfactor_name}</td>
		    <td  align="center"  onclick='addItem(3)' class="RecordRow_self  common_background_color common_border_color"  bgcolor='#f4f7f7'  >&nbsp;</td>
		    <!-- 【6734】薪资标准表，线条颜色不对  jingq add 2015.01.20 -->
		    <td  align="center"  id='a4' onclick='addItem(4)' class="RecordRow_self common_border_color"  colspan="3" rowspan="3">&nbsp;<bean:message key="label.gz.resultPoint"/></td>
		</tr>
    	<tr>
		    <td  align="center" id='a3'  onclick='addItem(3)'class="RecordRow_self  common_background_color common_border_color"  bgcolor='#f4f7f7' >${salaryStandardForm.s_vfactor_name}</td>
		</tr>
		<tr>
		    <td  align="center"  onclick='addItem(3)' class="RecordRow_self  common_background_color common_border_color"  bgcolor='#f4f7f7' >&nbsp;</td>
		</tr>

     </table>

   	<table width="200" border="0" cellspacing="0" cellpadding="0" align="center">
	  <tr>
	    <td height="35px;"><div align="center">
	      <input name="Button" type="button" class="mybutton" value="<bean:message key="gz.bankdisk.nextstep"/>" onclick="enter()"/>

	      <input name="Button" type="button" class="mybutton" value="<bean:message key="reportcheck.return"/>" onclick="goback()"/>
	    </div></td>
	  </tr>
	</table>
	
	
	<Input type='hidden' name='hfactor'  value='${salaryStandardForm.hfactor}' />
	<Input type='hidden' name='s_hfactor'  value='${salaryStandardForm.s_hfactor}' />
	<Input type='hidden' name='vfactor'  value='${salaryStandardForm.vfactor}' />
	<Input type='hidden' name='s_vfactor'  value='${salaryStandardForm.s_vfactor}' />
	<Input type='hidden' name='hcontent'  value='${salaryStandardForm.hcontent}' />
	<Input type='hidden' name='vcontent'  value='${salaryStandardForm.vcontent}' />
	<Input type='hidden' name='item'  value='${salaryStandardForm.item}' />
  </html:form>
  </body>
</html>
