<%@page import="com.hjsj.hrms.actionform.sys.options.otherparam.SysOthParamForm"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>       
  
  <script language='javascript'>
  <% 
	if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("setTab")){
		out.print("if(window.showModalDialog){");
		out.print(" parent.window.returnValue='ok'"); 
		out.print("}else{");
		out.print(" parent.parent.setTableStructure_ok('ok')");
		out.print("}");
		out.print(" goback();");
	}
  %>
 
  function initDocument_me()
  {
  	    var hashvo=new ParameterSet();
	    var In_paramters="opt=1";  
	    hashvo.setValue("planid",document.evaluationForm.planid.value);	 
	    hashvo.setValue("object_type",document.evaluationForm.object_type.value);	    
	    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldSetList,functionId:'9024000288'},hashvo); 
  }  
  function showFieldSetList(outparamters)
  {
		var fieldlist=outparamters.getValue("list");
		var rightList=outparamters.getValue("rightList");
				
//		document.getElementById("fieldSet").options.length = 0;  		
		var vos=document.getElementsByName("fieldSet");				  
	    if(vos==null)
	  		return false;
	    var left_vo=vos[0];		
		for(i=0;i<fieldlist.length;i++)
	    {	    		    	
	    	var no = new Option();
	    	no.value=getDecodeStr(fieldlist[i].dataValue);	    		
			no.text=getDecodeStr(fieldlist[i].dataName);											
	    	left_vo.options[left_vo.options.length]=no;	    	
	    }
		
//		document.getElementById("salarySetIDs").options.length = 0;
		var voss=document.getElementsByName("salarySetIDs");
	    if(voss==null)
	  		return false;
	    var right_vo=voss[0];		
		for(i=0;i<rightList.length;i++)
	    {	    		    	
	    	var no = new Option();
	    	no.value=getDecodeStr(rightList[i].dataValue);	    		
			no.text=getDecodeStr(rightList[i].dataName);											
	    	right_vo.options[right_vo.options.length]=no;	    	
	    }
		
  }
  
  function showFieldList()
  {
  		var onlyFild="${evaluationForm.onlyFild}"; 
  		var importPlanIds="${evaluationForm.importPlanIds}"; 
  		
  		var onlyTrOrFa="noHave";  
  		var objlist=new Array(); 		
  		for(var i=0;i<evaluationForm.salarySetIDs.options.length;i++)
   	 	{
   	 		var temp=evaluationForm.salarySetIDs.options[i].value;   	 		
   	 		if(temp.indexOf(";")!=-1)
			{						
				var newid=temp.substring(0,temp.indexOf(";")); // 计划ID
				
			}else
			{	
				var parmter=temp.substring(0,temp.indexOf(":")); // 子集代码				
				if((parmter.toLowerCase())==(onlyFild.toLowerCase()))  				
					onlyTrOrFa="Have";								
			}
			objlist.push(getEncodeStr(evaluationForm.salarySetIDs.options[i].value)); 
   	 	}
//	  	if(document.evaluationForm.fieldSet.value!='')
	  	{
	  		var hashvo=new ParameterSet();	
		  	var In_paramters="opt=2";  
		  	hashvo.setValue("fieldSetid",document.evaluationForm.fieldSet.value);
		  	hashvo.setValue("objlist",objlist);
		  	hashvo.setValue("onlyTrOrFa",onlyTrOrFa);
		  	hashvo.setValue("importPlanIds",importPlanIds);
		  	hashvo.setValue("planid",document.evaluationForm.planid.value);
		  	hashvo.setValue("object_type",document.evaluationForm.object_type.value);
		    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList2,functionId:'9024000288'},hashvo);
	  	}
  }  
  function showFieldList2(outparamters)
  {
		var fieldlist=outparamters.getValue("list");
		
		document.getElementById("left_fields").options.length = 0;  		
		var vos=document.getElementsByName("left_fields");		
	    if(vos==null)
	  		return false;
	    var left_vo=vos[0];		
		for(i=0;i<fieldlist.length;i++)
	    {	
	        var  gvalue= getDecodeStr(fieldlist[i].dataValue);
            gvalue =gvalue.substring(gvalue.indexOf(":")+1,gvalue.length);
	        if(only()=="Have"&&(gvalue.indexOf("Mark")!=-1||gvalue.indexOf("Grade")!=-1))
	        continue;	    	
	    	var no = new Option();
	    	no.value=getDecodeStr(fieldlist[i].dataValue);	    		
			no.text=getDecodeStr(fieldlist[i].dataName);											
	    	left_vo.options[left_vo.options.length]=no;	    	
	    }
			
//		AjaxBind.bind(evaluationForm.left_fields,fieldlist);
  }
    
  function savecode()
  {		
   		var objlist=new Array();	 	
   	 	for(var i=0;i<evaluationForm.salarySetIDs.options.length;i++)
   	 	{   
   	 		var temp=evaluationForm.salarySetIDs.options[i].value;
   	 		var temp2=evaluationForm.salarySetIDs.options[i].text;
   	 		var num=0;
   	 		for(var j=0;j<evaluationForm.salarySetIDs.options.length;j++)
   	 		{
   	 			if(evaluationForm.salarySetIDs.options[j].value==temp)
   	 				num++;
   	 		}
   	 		if(num>1)
   	 		{
   	 			alert(temp2+"  "+ITEM_NOT_RESET+"！");
   	 			return;
   	 		}
   	 	
   	 		objlist.push(evaluationForm.salarySetIDs.options[i].value); 		
   	 	}	
   	 	setselectitem('salarySetIDs');
   	 	evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_saveTableStructure=add&opt=setTab&rightCount="+objlist.length;
   	 	evaluationForm.submit();
  }
   
  function goback()
  {
	  if(!window.showModalDialog){
		  var win = parent.parent.Ext.getCmp('setTableStructure_win');
	 	  if(win) {
	  			win.close();
	 	  }
	  }
  	  parent.window.close();
  }
  function lookGC()
	{
         var thecodeurl="/hire/demandPlan/positionDemand/auto_logon_sp.do?b_look=search"; 
		 var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
		 var values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:250px; dialogHeight:160px;resizable:yes;center:yes;scroll:yes;status:no");  
    }
  
  // option 选项事件 把选项从左侧添加到右侧
  function addLfromRitem(sourcebox_id,targetbox_id)
  {

	    var left_vo,right_vo,vos,i,jhid;
	    var importis = "false";
	    var onlyTrOrFa = "noHave";
	    vos= document.getElementsByName(sourcebox_id);
	    var importPlanIds="${evaluationForm.importPlanIds}";
	    var onlyFild="${evaluationForm.onlyFild}";	
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
	    	 var ss = left_vo.options[i].value;
			 jhid=ss.substring(0,ss.indexOf(";"));
	
	        	
	    	 }
	      }
	    
	    for(var i=0;i<evaluationForm.salarySetIDs.options.length;i++)
   	 	{
   	 		var temp=evaluationForm.salarySetIDs.options[i].value;  	 		
   	 		if(temp.indexOf(";")!=-1)
			{						
				var newid=temp.substring(0,temp.indexOf(";")); // 计划ID
				//alert(newid);
				
			}else
			{	
				var parmter=temp.substring(0,temp.indexOf(":")); // 子集代码	
							
				if((parmter.toLowerCase())==(onlyFild.toLowerCase()))  //检查是否有唯一性指标				
					onlyTrOrFa="Have";								
			}	
   	 	 }
	     
	     var fild =importPlanIds.split(",")//检查是否是负责人计划
   	 	 for(z=0;z<fild.length;z++){
   	 	        if(jhid==fild[z]){
   	 	        importis="true";
   	 	        }  
   	 	 }
	     for(i=0;i<left_vo.options.length;i++)
	     {
	    	if(left_vo.options[i].selected)
	    	{   
	    	   var ss5=left_vo.options[i].value;
	           ss5 =ss5.substring(ss5.indexOf(":")+1,ss5.length);
	          
	        	var no = new Option();
	        	var textDesc="";
	        	if(onlyTrOrFa!="noHave"&&importis!="false"){
	        
                  no.value=left_vo.options[i].value+"_ZAVG";
	    		  textDesc = left_vo.options[i].text;
	    		 }else{
	    		  no.value=left_vo.options[i].value;
	    		  textDesc = left_vo.options[i].text;
	    		  }
	    		
	    		if((no.value).indexOf(";")!=-1)
				{	
					var newid=(no.value).substring(0,(no.value).indexOf(";"));
					if(textDesc.indexOf(".")!=-1)					
						no.text=textDesc.substring(textDesc.indexOf(".")+1);
					else{
					
					   if(onlyTrOrFa!="noHave"&&importis!="false"&&ss5.indexOf("_Z")==-1)
						no.text=newid+"."+left_vo.options[i].text+PERFORMANCE_ZAVG;
					   else
					    no.text=newid+"."+left_vo.options[i].text;
						}
				}else
				{	
					no.text=left_vo.options[i].text;							
				}
	    		right_vo.options[right_vo.options.length]=no;
	    	 }
	      }
	  
	    //设为要可选状态
	    /*
	    for(i=0;i<right_vo.options.length;i++)
	    {
	    	right_vo.options[i].selected=true;
	    }
	 	*/
	     return true;	  	
  }
  
  // option 选项事件 把选项从option列表中删除
  function removeLfromRitem(sourcebox_id)
  {
  		var vos,right_vo,i;
  		vos= document.getElementsByName(sourcebox_id);
  		if(vos==null)
  			return false;
  		right_vo=vos[0];
  		for(i=right_vo.options.length-1;i>=0;i--)
  		{
    		if(right_vo.options[i].selected)
    		{
    			//alert(i);
				right_vo.options.remove(i);
    		}
  		}
  		//设为要可选状态
  		/*
  		for(i=0;i<right_vo.options.length;i++)
  		{
			right_vo.options[i].selected=true;
  		}  
  		*/  
  		return true;	  	
  }
  function popRegulateGather(sourcebox_id,targetbox_id){
       var vos,right_vo,i, right_vo,jhid,fild,fild2;
       var filds ="";
       var fildSs ="";
       var onlyFild="${evaluationForm.onlyFild}";
       var importPlanIds="${evaluationForm.importPlanIds}";
       var onlyTrOrFa="noHave";
       var importis ="false";

  		vos= document.getElementsByName(sourcebox_id);
  		if(vos==null)
  			return false;
  		right_vo=vos[0];

		var ss = right_vo.value;
		jhid=ss.substring(0,ss.indexOf(";"));
		fild = ss.substring(ss.indexOf(":")+1,ss.length);
		if(fild.indexOf("_")!=-1)
		fild = fild.substring(0,fild.indexOf("_"));

  		
  		for(i=right_vo.options.length-1;i>=0;i--)
  		{
    		
    		if(right_vo.options[i])
    		{
				var ss = right_vo.options[i].value;
				
				fild2 = ss.substring(ss.indexOf(":")+1,ss.length);
				fildSs+= fild2+",";
				if(fild2.indexOf(fild)!=-1&&fild2.indexOf("_")!=-1){
				
				filds+= fild2.substring(fild2.length-3,fild2.length)+",";//右面的指标
				}
    		}
  		}
  		//alert(fildSs)
  		  		
        for(var i=0;i<evaluationForm.salarySetIDs.options.length;i++)
   	 	{
   	 		var temp=evaluationForm.salarySetIDs.options[i].value;  	 		
   	 		if(temp.indexOf(";")!=-1)
			{						
				var newid=temp.substring(0,temp.indexOf(";")); // 计划ID
				//alert(newid);
				
			}else
			{	
				var parmter=temp.substring(0,temp.indexOf(":")); // 子集代码				
				if((parmter.toLowerCase())==(onlyFild.toLowerCase()))  //检查是否有唯一性指标				
					onlyTrOrFa="Have";								
			}	
   	 	}
   	 	
   	 	if(jhid.length>0&&importPlanIds.indexOf(jhid)!=-1){
   	 	importis="true";
   	 	}

   	 	if(onlyTrOrFa=="noHave"||importis=="false")
   	 	return true;
   	 	//&fieldsetid="+fieldsetid
        var thecodeurl="/performance/evaluation/performanceEvaluation.do?br_gather=link`filds="+filds; 
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	    var values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:400px; dialogHeight:250px;resizable:yes;center:yes;scroll:yes;status:no");
		if(!values)
		return;	
 	    else if(values.flag=="true") 
	    {
    	  var vv = values.degrees;
   	      }
   	      regulateGather(sourcebox_id,targetbox_id,vv,fildSs,fild)	 
  }
  
 
  function regulateGather(sourcebox_id,targetbox_id,vv,fildSs,fild){
        var vos,right_vo,i,ss,fild,texts;
  		vos= document.getElementsByName(sourcebox_id);
  		right_vo = vos[0];
  		if(vos==null)
  			return false;
  		var vvs =vv.split(",")
  			
  		for(i=right_vo.options.length-1;i>0;i--)
  		{ 

  		    var ss4= "";
	        ss=right_vo.options[i].value;
	        if(ss.indexOf("_Z")!=-1){
	         ss = ss.substring(0,ss.indexOf("_Z"));
	         }
	         
	        texts=right_vo.options[i].text;
	        if(texts.indexOf("之")!=-1){
	        texts = texts.substring(0,texts.indexOf("之"));
	         }
	        for(j=0;j<vvs.length;j++){
	        var ss2 =ss.substring(ss.indexOf(":")+1,ss.length);
	        if(ss2.indexOf("_Z")!=-1)
	        ss2 = ss2.substring(0,ss2.indexOf("_"));
	        ss2 = ss2+"_"+vvs[j];
	        ss4 += ss2+",";
	        
	        var ss5=right_vo.options[i].value;
	        var ss3 =ss5.substring(ss5.indexOf(":")+1,ss5.length);
	        
	        var selected=right_vo.value;
	        selected =selected.substring(selected.indexOf(":")+1,selected.length);
	        
	    	if(right_vo.options[i]!=null&&right_vo.options[i].selected&&fildSs.indexOf(ss2)==-1)
	    	{
	        	var no = new Option();
	    		no.value=ss+"_"+vvs[j];
	    		var textDesc=right_vo.options[i].text;
	    		if((no.value).indexOf(";")!=-1)
				{	

					var newid=(no.value).substring(0,(no.value).indexOf(";"));

					  if(ss3.indexOf("_Z"))
						no.text=texts+translation(vvs[j]);
					  else
					    no.text=right_vo.options[i].text;
						
				}else
				{	
					no.text=right_vo.options[i]+vvs[j];							
				}
	    		right_vo.options[right_vo.options.length]=no;
	    	}
	    }
        
	    if(ss4.indexOf(ss3)==-1&&ss3.indexOf("_Z")!=-1&&ss3.substring(0,ss3.indexOf("_Z"))==selected.substring(0,selected.indexOf("_Z"))){
	    
	    right_vo.options.remove(i);

	    }

	  }
  		return true;
  }
  function translation(vvs){

   var HZMenusValue = "";
    	if(vvs=="ZSUM")
    		HZMenusValue = PERFORMANCE_ZSUM;
    	if(vvs=="ZAVG")
    		HZMenusValue = PERFORMANCE_ZAVG;
    	if(vvs=="ZMAX")
    		HZMenusValue = PERFORMANCE_ZMAX;
    	if(vvs=="ZMIN")
    		HZMenusValue = PERFORMANCE_ZMIN;
        return HZMenusValue;
  }
  
  function only(){
    var onlyTrOrFa= "";
    var onlyFild="${evaluationForm.onlyFild}";
    for(var i=0;i<evaluationForm.salarySetIDs.options.length;i++)
   	 	{
   	 		var temp=evaluationForm.salarySetIDs.options[i].value;  	 		
   	 		if(temp.indexOf(";")!=-1)
			{						
				var newid=temp.substring(0,temp.indexOf(";")); // 计划ID
				//alert(newid);
				
			}else
			{	
				var parmter=temp.substring(0,temp.indexOf(":")); // 子集代码				
				if((parmter.toLowerCase())==(onlyFild.toLowerCase()))  //检查是否有唯一性指标				
					onlyTrOrFa="Have";								
			}	
   	 	}
   	 	return onlyTrOrFa;
  
  }
  
  </script>
  </head>
  <style type="text/css">

	#scroll_box {
	    border: 0px solid #94B6E6;
	    BORDER-BOTTOM:#94B6E6 0pt solid;
	    BORDER-TOP: #94B6E6 0pt solid;
	    height: 295px;
	    width: 240px;               
	    overflow: auto;            
	    margin: 1em 0;
	}
  </style>
  
  <body>
   <html:form action="/performance/evaluation/performanceEvaluation">
   	 <html:hidden name="evaluationForm" property="planid" />
   	 <html:hidden name="evaluationForm" property="object_type" />
   	 <html:hidden name="evaluationForm" property="code" />
   		
		<table width="100%" align="center" border="0" style="margin:0;padding:0;" cellpadding="0" cellspacing="0">
		  <tr style="margin:0;padding:0;">  
		    <td align="center" style="margin:0;padding:0;">  
		    <fieldset style="margin-left:3px;">
				<legend id="printcardpingu"><bean:message key="jx.evalution.printcardpingu"/></legend>
			     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">		   	  
			   	   
			        <tr>
			             <td align="center"  width="46%">
			               <div id="scroll_box" style='height:313px;'>
			             	<fieldset style="width:90%;">
	    						<legend><bean:message key="gz.bankdisk.preparefield"/></legend>
	    							
	    						
					               <table align="center" width="100%">              
					                <tr>
					                 <td align="center">
					                  
					                  	<select name='fieldSet' style="height:22px;width:100%;font-size:9pt"  onchange="showFieldList()" >
					                  	
					                  	</select>
					                                       
					                  </td>
					                 </tr>
					                <tr>
					                 <td align="center">
					                  <select name="left_fields" id='left_fields' multiple="multiple" ondblclick="addLfromRitem('left_fields','salarySetIDs');removeLfromRitem('left_fields');" style="height:249px;width:100%;font-size:9pt">
					                   </select>
					                   </td>
					                  </tr>
					                </table>
					             
			                </fieldset>
			               </div> 
			             </td>
			               
			             <td width="8%" align="center">  
				            <html:button  styleClass="mybutton" property="b_addfield" onclick="addLfromRitem('left_fields','salarySetIDs');removeLfromRitem('left_fields');">
			            		     <bean:message key="button.setfield.addfield"/> 
				            </html:button >
				            <br>
				            <br>
				            <html:button  styleClass="mybutton" property="b_delfield" onclick="addLfromRitem('salarySetIDs','left_fields');removeLfromRitem('salarySetIDs');">
			            		     <bean:message key="button.setfield.delfield"/>    
				            </html:button >	
			             </td>         
			             <td width="46%" align="center">
			               <div id="scroll_box" style='height:310px;'>
							<fieldset style="width:90%;">
	    						<legend><bean:message key="gz.bankdisk.selectedfield"/></legend>
	    						
						            <table width="100%" >                
						              <tr>
							             <td width="100%" align="left">
							     	            
							 		     	<select name="salarySetIDs" oncontextmenu="popRegulateGather('salarySetIDs','left_fields');" multiple="multiple" size="10"   ondblclick="addLfromRitem('salarySetIDs','left_fields');removeLfromRitem('salarySetIDs');" style="height:272px;width:100%;font-size:9pt">
							                </select>            
							 		                 
							             </td>
						              </tr>
						            </table> 
						        
					        </fieldset> 
					      </div>          
			           </td>               
			     </tr>			         			       
			 </table>
		  </fieldset>
		 </td>		 
		</tr>
		<tr>      
		   <td align="center">		
  	
			  <html:button style="margin-top:5px;" styleClass="mybutton" property="b_save" onclick="savecode()">
			        <bean:message key="button.ok"/>
			  </html:button >&nbsp;
			  <input type='button' style="margin-top:5px;" class="mybutton" value="<bean:message key="kq.register.kqduration.cancel"/>"  onclick='goback()'  />
			       
		   </td>
		</tr>
	  </table>   
		
   </html:form> 
   
   <script language='javascript' >
   
   initDocument_me();
   if(!getBrowseVersion()){
       var printcardpingu = document.getElementById("printcardpingu");
       if(printcardpingu){
           printcardpingu.style.display='none';
       }
   }
   </script>
  </body>
</html>
