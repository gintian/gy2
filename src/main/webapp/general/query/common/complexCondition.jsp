<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
	  <meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
	  <script language="javascript" src="/module/utils/js/template.js"></script>
<style type="text/css"> 
.btn1 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 0px;
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 0px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn2 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 3px; 
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 3px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn3 {
BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 2px;
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 2px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
#scroll_box {
    border: 1px solid #eee;
    height: 280px;    
    width: 282px;            
    overflow: auto;            
    margin: 1em 0;
}
</style>
<hrms:themes />
  </head>
  <%
  String mode="";
  if(request.getParameter("mode")!=null)
  	  mode = request.getParameter("mode");
   %>
  <script language='javascript'>
  var info=window.dialogArguments ||parent.opener.dialogArguments || parent.parent.dialogArguments ;
  function symbol(cal){
  	var computeFormula=document.getElementById("shry");
      computeFormula.focus();
    if(!cal)
        return;
	var element = document.selection;
      if(element){
          var rge = element.createRange();
          if (rge!=null)
              rge.text=cal;

      }else{
          var start =computeFormula.selectionStart;
          computeFormula.value = computeFormula.value.substring(0, start) + cal + computeFormula.value.substring(start, computeFormula.value.length);
          computeFormula.setSelectionRange(start + cal.length, start + cal.length);
      }


  }
  
  
  function initSelectBox()
  {
  		var hashvo=new ParameterSet();
		hashvo.setValue("flag","1");
		var request=new Request({asynchronous:false,onSuccess:setvalue,functionId:'0202011011'},hashvo);
  }
  
  function getItem()
  {
  	   if(document.commonQueryForm.fieldSet.value!='')
  	   {

  	   		var hashvo=new ParameterSet();
			hashvo.setValue("flag","2");
			hashvo.setValue("value",document.commonQueryForm.fieldSet.value);
			var request=new Request({asynchronous:false,onSuccess:setvalue,functionId:'0202011011'},hashvo);
  	   }
  }
  
  function setText()
  {
  	  if(document.commonQueryForm.fielditem.value!='')
  	   {
  	        for(var i=0;i<document.commonQueryForm.fielditem.options.length;i++)
  	        {
  	        	if(document.commonQueryForm.fielditem.options[i].selected==true)
  	        		 symbol(document.commonQueryForm.fielditem.options[i].text)
  	        }
  	     	var hashvo=new ParameterSet();
			hashvo.setValue("flag","3");
			hashvo.setValue("value",document.commonQueryForm.fielditem.value);
			var request=new Request({asynchronous:false,onSuccess:setvalue,functionId:'0202011011'},hashvo);
  	   }
  }
  
  function setCode()
  {
  		if(document.commonQueryForm.codeitem.value!='')
  	   {
  	        for(var i=0;i<document.commonQueryForm.codeitem.options.length;i++)
  	        {
  	        	if(document.commonQueryForm.codeitem.options[i].selected==true)
  	        		 symbol("\""+document.commonQueryForm.codeitem.options[i].value+"\"")
  	        } 	
  	   }
  }
  
  function setvalue(outparamters)
  {
  		var flag = outparamters.getValue("flag");
  		if(flag=='1')
  		{
  			var codelist=outparamters.getValue("list");
			if(codelist.length>1)
				AjaxBind.bind(commonQueryForm.fieldSet,codelist);
  		}
  		if(flag=='2')
  		{
  			var codelist=outparamters.getValue("list");
			if(codelist.length>1)
				AjaxBind.bind(commonQueryForm.fielditem,codelist);
  		}
  		if(flag=='3')
  		{
  			var codelist=outparamters.getValue("list");
			if(codelist.length>1)
			{
				var obj=document.getElementById("codeTd");
				obj.style.display="block";
				AjaxBind.bind(commonQueryForm.codeitem,codelist);
			}
			else{
				var obj=document.getElementById("codeTd");
				obj.style.display="none";
			}
  		}
  }
  
  //常用条件
  function getGzWhere()
  {
  		var arguments=new Array();     
	    var strurl="/general/query/common/complexCondition.do?b_initCondition=link`callBackfunc=symbol";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
      //var ss = window.showModalDialog(iframe_url, arguments, "dialogWidth=310px;dialogHeight=420px;resizable=yes;scroll=no;status=no;");
      var width= 310;
      if (!window.showModalDialog){
          width = 500;
      }
      var config = {
          width:width,
          height: 420,
          type: '2',
		  id:'GzWhereWin'
      };
      modalDialog.showModalDialogs(iframe_url, 'GzWhereWin', config, symbol);
  
  }
  
  function saveCond()
  {
  	if(trim(document.commonQueryForm.expr.value).length>0)
  	{
  	
  		var hashvo=new ParameterSet();
  		var m = document.commonQueryForm.expr.value;
		m = m.replace( /\r/g, "!" ); 
		m = m.replace( /\n/g, "`" ); 	
	  	//规范字符串
		for(var i = 0 ; i<m.length; i++){
			if(m.charAt(i) == "\""){
				m = m.replace("\"" , "'");
			}
		}
  		
  		
		hashvo.setValue("c_expr",getEncodeStr(m));
		hashvo.setValue("type","2");
		hashvo.setValue("model","2");
		var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:resultCheckExpr,functionId:'3020050005'},hashvo);		

  	}
  }
  
  
  function resultCheckExpr(outparamters){
	  	var info = outparamters.getValue("info");
	  	
	  	
		if(info=="ok"||info.length==0){
			var name=window.prompt("请输入条件名称：","");
	  		if(name&&name.length>0)
	  		{
	  		    document.getElementById("na").value=name;
	  			document.commonQueryForm.action="/general/query/common/complexCondition.do?b_save=save&name="+$URL.encode(name);
	  			document.commonQueryForm.submit();
	  		}
		
		}
		else
		{
		/*	for(var i = 0 ; i<info.length; i++){
				if(info.charAt(i) == "'"){
					info = info.replace("'" , "\"");
				}
				if(info.charAt(i) == "`"){
					info = info.replace("`" , "\r\n");
				}
			}*/
			alert(getDecodeStr(info));
		}
		
	}
	
	
  
  function enter()
  {
	  	if(trim(document.commonQueryForm.expr.value).length>0)
	  	{
	  	
	  		var hashvo=new ParameterSet();
	  		var m = document.commonQueryForm.expr.value;
			m = m.replace( /\r/g, "!" ); 
			m = m.replace( /\n/g, "`" ); 	
		  	//规范字符串
			for(var i = 0 ; i<m.length; i++){
				if(m.charAt(i) == "\""){
					m = m.replace("\"" , "'");
				}
			}
	  		
	  		
			hashvo.setValue("c_expr",getEncodeStr(m));
			if(info[3]==null||info[3]=='0')
			{
				hashvo.setValue("type","2");
				hashvo.setValue("model","2");
			}
			else if(info[3]=='4')//工资类别定义人员范围时应为逻辑型的
			{
			    hashvo.setValue("type","4");
				hashvo.setValue("model","1");
			}
			else
			{
				hashvo.setValue("model","1");
				var type="2";
				if(info[3]=="N")
					type="1";
				if(info[3]=="D")
					type="3";
				hashvo.setValue("type",type);
			}
			
			var In_paramters="flag=1"; 	
			var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultCheckExpr2,functionId:'3020050005'},hashvo);		
	
	  	}
	  	else
	  	{
	  		var return_val = "";
	  		if(document.commonQueryForm.heapFlag!=null){
	    		var result=new Array();
	    		result[0]=document.commonQueryForm.expr.value;
	    		result[1]=document.commonQueryForm.heapFlag.value;
	    		return_val=result;
    		}
    		else
    			return_val=document.commonQueryForm.expr.value;
	  		
	  		sendRev(return_val);
	  		close_complex();
	    }	
  }
  
  
   function resultCheckExpr2(outparamters){
	   	var return_val = "";
	  	var ainfo = outparamters.getValue("info");
		if(ainfo=="ok"||ainfo.length==0){
			if(info.length==4){
				return_val=document.commonQueryForm.expr.value;
	    	}
	    	else
	    	{
	    		var result=new Array();
	    		result[0]=document.commonQueryForm.expr.value;
	    		result[1]=document.commonQueryForm.heapFlag.value;
	    		return_val=result;
	    	}
			sendRev(return_val);
			close_complex();
		}
		else
		{
			/*for(var i = 0 ; i<info.length; i++){
				if(info.charAt(i) == "'"){
					info = info.replace("'" , "\"");
				}
				if(info.charAt(i) == "`"){
					info = info.replace("`" , "\r\n");
				}
			}*/
			alert(getDecodeStr(ainfo));
		}
		
	}
  	
   function sendRev(returnVal){
		if(window.showModalDialog)
			parent.window.returnValue=returnVal;
		else
			parent.opener.complexCondition_ok(returnVal);
   }
   function close_complex() {
 		parent.window.close();
   }
   /*
    *函数向导
    */
   function function_Wizard_(formula,param){
       var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link`mode="+param; 
       var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
       formula_function = formula;
       //var return_vo= window.showModalDialog(iframe_url, "", 
       //         "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
       
       var config = {
   	    width:420,
   	    height:430,
   	    type:'2',
   	    id:"function_Wizard"
   	}

   	modalDialog.showModalDialogs(iframe_url,"function_Wizard",config,openReturn);
   }

   function openReturn(return_vo){
   		if(return_vo!=null){
     	 	if(document.getElementsByName("expr")[0].pos!=null){
   				document.getElementsByName("expr")[0].pos.text=return_vo;
	   		}else{
	   			var formula_sys = document.getElementsByName("expr")[0];
	   			formula_sys.focus();
	   			var element = document.selection;
	   			if (element!=null) 
	   			{
	   				var rge = element.createRange();
	   				if (rge!=null)	
	   					rge.text=return_vo;
	   			}else{
	   		        var start =formula_sys.selectionStart;
	   		        formula_sys.value = formula_sys.value.substring(0, start) + return_vo + formula_sys.value.substring(start, formula_sys.value.length);
	   		        formula_sys.setSelectionRange(start + return_vo.length, start + return_vo.length);
	   		    }
	   		}
     	}else{
     		return ;
     	}
   }
  </script>
  <body>
   <html:form action="/general/query/common/complexCondition">
   <table width='850' border=0>
   <tr>
   <td> &nbsp;</td>
   <td>
   
   <table width="100%"   border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
        <tr>
         <td align="left"  class="TableRow" nowrap>
        	&nbsp; 
        	<script language='javascript' >
        		document.write(info[2]);
        	</script>
         </td>
        </tr>
      </thead>
      
      <tr class="trShallow">
            <td align="center"   class="RecordRow" nowrap>
       		<table border=0 width='100%'>
	       		<tr>
    			<td width='95%' valign='top'  >
	    			<table width='100%' border=0 >
	    				<tr><td valign='top' style="padding:3px 14px 0 2px;">
					    <html:textarea name="commonQueryForm" property="expr" style="width: 100%;" rows="14" styleId="shry" ></html:textarea>
					    </td>
					    <input id="na" type="hidden" name=name" value=""/>
					   	<td width='10%' valign='top' > 
	    				
		    				<table>		    				
			    			<script language='javascript'>
			    			
			    			if(info[1]==0)
			    			{
			    				document.write("<tr><td height='35' align='center'>	<input type='button'   value='常用条件' onclick='getGzWhere()' class='mybutton'></td></tr>");	
			    				document.write("<tr><td height='35' align='center'>	<input type='button'   value='保存条件' onclick='saveCond()' class='mybutton'></td></tr>");
			    			}
			    			</script>
			    			
			    			
			    			<tr><td height='35' align='center'>	<input type="button"   value="函数向导" onclick="function_Wizard_('expr','<%=mode %>');" class="mybutton"></td></tr>
			    			<tr><td height='35' align='center'>	<input type="button"   value="确      定" onclick="enter()" class="mybutton"></td></tr>
			    			<tr><td height='35' align='center'>	<input type="button"   value="取      消" onclick="close_complex()" class="mybutton"></td></tr>		
		    		   		</table>
	    		   		 </td>
					    </tr>
					 </table>
				   </td>
				   </tr>
				   <tr>
				   
				   <td algin='left' height='90'  width='100%' > 
    					<table width='90%' border=0  ><tr>
    					<td width='45%' > 
    						<fieldset align="center" style="width:90%;height:110px;">
    							 <legend align="center" style="text-align:center">参考项目</legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<script language='javascript'>
			    							if(info.length==5){
			    							document.write("<tr><td height='25' >");
			    							document.write("&nbsp;&nbsp;方&nbsp;&nbsp;式&nbsp;&nbsp;&nbsp;");
			    							document.write("<select name='heapFlag' style='width:200;font-size:9pt'>");
			    							document.write("<option value='0' "+(info[4]==0?"selected":"")+" >当前记录</option>");
			    							document.write("<option value='1' "+(info[4]==1?"selected":"")+" >月内最初第一条</option>");
			    							document.write("<option value='2' "+(info[4]==2?"selected":"")+" >月内最近第一条</option>");
			    							document.write("<option value='3' "+(info[4]==3?"selected":"")+" >小于本次月内最初第一条</option>");
			    							document.write("<option value='4' "+(info[4]==4?"selected":"")+" >小于本次月内最近第一条</option>");
			    							document.write("<option value='5' "+(info[4]==5?"selected":"")+" >同月同次</option>");
			    							document.write("<option value='6' "+(info[4]==6?"selected":"")+" >扣减同月已发金额</option>");
			    							document.write("</select>");
			    							document.write("</td></tr>");	
		                      				}
		                      				</script>
		                      				<tr>
			                					<td width="90%" height="25" >
			                		            &nbsp;&nbsp;子&nbsp;&nbsp;集&nbsp;&nbsp;
				                			 	<SELECT NAME='fieldSet' onchange='getItem()' style="width:200;font-size:9pt" >
				                			
				                			 	</SELECT>
			                		            </td>
			                				</tr>
			                				<tr>
			                					<td width="90%" height="25" >
			                		            &nbsp;&nbsp;指&nbsp;&nbsp;标&nbsp;&nbsp;
				                			 	<SELECT NAME='fielditem' onchange='setText()' style="width:200;font-size:9pt" >
				                			
				                			 	</SELECT>
			                					</td>
			                				</tr>
			                				<tr>
			                					<td width="90%"  height="25" >
			                		          
			                		           <div id='codeTd' style='display:none'>
			                		            &nbsp;&nbsp;代&nbsp;&nbsp;码&nbsp;&nbsp;
				                			 	<SELECT NAME='codeitem' onchange='setCode()' style="width:200;font-size:9pt" >
				                			
				                			 	</SELECT>
			                					</div>
			                					</td>
			                				</tr>
			                				
		                      			</table>
		                	 </fieldset>
    					
    					</td>
    					<td width='55%' >
    						<fieldset align="center" style="width:90%;height: 110px;">
    							 <legend align="center" style="text-align:center;">计算符号</legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
			                					<td width="90%">
			                		            	<table style="margin-left:10px;">
				                		            <tr><td height="22" >
				                		            <Input type='button' value='0'  class="smallbutton" onclick="symbol('0')" />
				                		            <Input type='button' value='1'  class="smallbutton" onclick="symbol('1')"  />
				                		            <Input type='button' value='2'  class="smallbutton" onclick="symbol('2')"  />
				                		            <Input type='button' value='3'  class="smallbutton" onclick="symbol('3')"  />
				                		            <Input type='button' value='4'  class="smallbutton" onclick="symbol('4')"  />
				                		            
				                		            <Input type='button' value='+'  class="smallbutton" onclick="symbol('+')"  />
				                		            <Input type='button' value='*'  class="smallbutton" onclick="symbol('*')"  />
				                		            <Input type='button' value='\'  class="smallbutton" onclick="symbol('\\')"  />
				                		            <Input type='button' value='且'  class="smallbutton" onclick="symbol(' 且 ')"  />
				                		            <Input type='button' value='非'  class="smallbutton" onclick="symbol(' 非 ')"  />
				                		            
				                		            </td></tr>
				                		            <tr><td height="22" >
				                		            <Input type='button' value='5'  class="smallbutton" onclick="symbol('5')" />
				                		            <Input type='button' value='6'  class="smallbutton" onclick="symbol('6')"  />
				                		            <Input type='button' value='7'  class="smallbutton" onclick="symbol('7')"  />
				                		            <Input type='button' value='8'  class="smallbutton" onclick="symbol('8')"  />
				                		            <Input type='button' value='9'  class="smallbutton" onclick="symbol('9')"  />
				                		            
				                		            <Input type='button' value='-'  class="smallbutton" onclick="symbol('-')"   />
				                		            <Input type='button' value='/'  class="smallbutton" onclick="symbol('/')"   />
				                		            <Input type='button' value='%'  class="smallbutton" onclick="symbol('%')"  />
				                		            <Input type='button' value='~'  class="smallbutton" onclick="symbol('~')"  />
				                		            <Input type='button' value='或'  class="smallbutton" onclick="symbol(' 或 ')"  />
				                		            </td></tr>
				                		            <tr><td height="22" >
				                		            <Input type='button' value='('  class="smallbutton" onclick="symbol('(')" />
				                		            <Input type='button' value=')'  class="smallbutton" onclick="symbol(')')" />
				                		            <Input type='button' value='='  class="smallbutton" onclick="symbol('=')" />
				                		            <Input type='button'  value='<'  class="smallbutton" onclick="symbol('<')"  />
				                		            <Input type='button'  value='>'  class="smallbutton"  onclick="symbol('>')" />
				                		            
				                		             <Input type='button' value='<>'  class="smallbutton" onclick="symbol('<>')" />
				                		            <Input type='button' value='<='  class="smallbutton" onclick="symbol('<=')" />
				                		            <Input type='button' value='>='  class="smallbutton" onclick="symbol('>=')" />
				                		            <Input type='button' value='包含'  class="smallbutton" onclick="symbol(' LIKE ')" />
				                		           
				                		            </td></tr>
				                		            </table>
			                		            </td>
			                				</tr>
		                      			</table>
		                	 </fieldset>
    					
    					 </td>
    					</tr></table>
    				 </td></tr>
    			</table>	
         	</td>
      </tr>
    </table>
    
    </td>
    </tr>
    </table>
    
    
   </html:form>
   
   <script language='javascript' >
   <%
   if(request.getParameter("b_save")==null||!request.getParameter("b_save").equals("save"))
   {
   %>
   
   initSelectBox()
   if(info!=null&&info[0]!=null&&info[0]!='')
   {
   		
	   	document.commonQueryForm.expr.value=info[0];
   }
   <% } %>
   </script>
   
  </body>
</html>
