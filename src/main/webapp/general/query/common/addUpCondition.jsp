<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
   
  </head>
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
</style>
  
  
  <script language='javascript'>
  var info=dialogArguments;
 
  
  function symbol(cal){
	if(document.getElementById("expr").pos!=null){
		document.getElementById("expr").pos.text=cal;
	}else{
		document.getElementById("expr").value +=cal;
	}
  }
  
  
  function initSelectBox()
  {
  		var hashvo=new ParameterSet();
		hashvo.setValue("flag","0");
		var request=new Request({asynchronous:false,onSuccess:setvalue,functionId:'0202011011'},hashvo);
  }
  
  function getItem()
  {
  	  
  	   		var hashvo=new ParameterSet();
			hashvo.setValue("flag","2");
			hashvo.setValue("value",info[1]);
			var request=new Request({asynchronous:false,onSuccess:setvalue,functionId:'0202011011'},hashvo);
  	   
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
  	        		 symbol("\'"+document.commonQueryForm.codeitem.options[i].value+"\'")
  	        } 	
  	   }
  }
  
  function setvalue(outparamters)
  {
  		var flag = outparamters.getValue("flag");
  		if(flag=='0')
  		{
  			var codelist=outparamters.getValue("list");
			if(codelist.length>1)
				AjaxBind.bind(commonQueryForm.fieldSet,codelist);
			getItem();
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
	    var strurl="/general/query/common/complexCondition.do?b_initCondition=link";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
	    var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=300px;dialogHeight=360px;resizable=yes;scroll=no;status=no;");  
	    if(ss)
	    {
	    	//alert(ss);
	    	//document.commonQueryForm.expr.value="";
	    	symbol(ss);
	    }
  
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
	  			document.commonQueryForm.action="/general/query/common/complexCondition.do?b_save=save&name="+name;
	  			document.commonQueryForm.submit();
	  		}
		
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
			hashvo.setValue("model","1");
			hashvo.setValue("type","1");
			var In_paramters="flag=1"; 	
			var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultCheckExpr2,functionId:'3020050005'},hashvo);		
	
	  	}
	  	else
	  	{
	  		var value=new Array();
	  		value[0]=document.commonQueryForm.fieldSet.value;
	  		value[1]=document.commonQueryForm.expr.value;
	  		returnValue=value;
	    	window.close();
	    }	
  }
  
  
   function resultCheckExpr2(outparamters){
	  	var info = outparamters.getValue("info");
		if(info=="ok"||info.length==0){
			
			var value=new Array();
	  		value[0]=document.commonQueryForm.fieldSet.value;
	  		value[1]=document.commonQueryForm.expr.value;
	  		returnValue=value;
	    	window.close();
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
  
  </script>
  <hrms:themes/>
  <body>
   <html:form action="/general/query/common/complexCondition">
   <table width='100%' border=0>
   <tr>
   <td> &nbsp;</td>
   <td>
   
   <table width="99%"   border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
        <tr>
         <td align="left"  class="TableRow" nowrap>
        	&nbsp; 
        	<script language='javascript' >
        		document.write(info[3]);
        	</script>
         </td>
        </tr>
      </thead>
      
      <tr class="trShallow">
            <td align="center"   class="RecordRow" nowrap>
       		<table border=0 width='100%'>
	       		<tr>
    			<td width='90%' valign='top'  >
	    			<table width='100%' border=0 >
	    				<tr><td valign='top' > 
	    				
					    <html:textarea name="commonQueryForm" property="expr" onclick="this.pos=document.selection.createRange();" cols="110" rows="14" styleId="shry" ></html:textarea> 
					    
					    </td>
					   	<td width='10%' valign='top' > 
	    				
		    				<table>
		    				<tr><td height='48' > &nbsp;</td></tr>
			    			
			    			
			    			<tr><td height='25' >	<input type="button"  value="函数向导" width="60" onclick="function_Wizard('expr');" class="mybutton"></td></tr>
			    			<tr><td height='25' >	<input type="button"  value="确      定" width="60" onclick="enter()" class="mybutton"></td></tr>
			    			<tr><td height='25' >	<input type="button"  value="取      消" width="60" onclick="window.close()" class="mybutton"></td></tr>		
		    		   		</table>
	    		   		 </td>
					    </tr>
					 </table>
				   </td>
				   </tr>
				   <tr>
				   
				   <td algin='left' height='80'  width='100%' > 
    					<table width='90%' border=0  ><tr>
    					<td width='45%' > 
    						<fieldset align="center" style="width:90%;">
    							 <legend >参考项目</legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
			                					<td width="90%" height="25" >
			                		            &nbsp;&nbsp;方&nbsp;&nbsp;式&nbsp;&nbsp;
				                			 	<SELECT NAME='fieldSet'  style="width:200;font-size:9pt" >
				                			
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
    						<fieldset align="center" style="width:90%;">
    							 <legend >运算符号</legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
			                					<td width="90%">
			                		            	<table>
				                		            <tr><td height="22" >
				                		            <Input type='button' value=' 0 '  class="btn1 common_btn_bg" onclick='symbol(0)' />
				                		            <Input type='button' value=' 1 '  class="btn1 common_btn_bg" onclick='symbol(1)'  />
				                		            <Input type='button' value=' 2 '  class="btn1 common_btn_bg" onclick='symbol(2)'  />
				                		            <Input type='button' value=' 3 '  class="btn1 common_btn_bg" onclick='symbol(3)'  />
				                		            <Input type='button' value=' 4 '  class="btn1 common_btn_bg" onclick='symbol(4)'  />
				                		            
				                		            <Input type='button' value=' + '  class="btn1 common_btn_bg" onclick="symbol('+')"  />
				                		            <Input type='button' value=' * '  class="btn1 common_btn_bg" onclick="symbol('*')"  />
				                		            <Input type='button' value=' \ '  class="btn1 common_btn_bg" onclick="symbol('\\')"  />
				                		            <Input type='button' value=' 且 '  class="btn1 common_btn_bg" onclick="symbol(' 且 ')"  />
				                		            <Input type='button' value=' 非 '  class="btn1 common_btn_bg" onclick="symbol(' 非 ')"  />
				                		            
				                		            </td></tr>
				                		            <tr><td height="22" >
				                		            <Input type='button' value=' 5 '  class="btn1 common_btn_bg" onclick='symbol(5)' />
				                		            <Input type='button' value=' 6 '  class="btn1 common_btn_bg" onclick='symbol(6)'  />
				                		            <Input type='button' value=' 7 '  class="btn1 common_btn_bg" onclick='symbol(7)'  />
				                		            <Input type='button' value=' 8 '  class="btn1 common_btn_bg" onclick='symbol(8)'  />
				                		            <Input type='button' value=' 9 '  class="btn1 common_btn_bg" onclick='symbol(9)'  />
				                		            
				                		            <Input type='button' value=' - '  class="btn1 common_btn_bg" onclick="symbol('-')"   />
				                		            <Input type='button' value=' / '  class="btn1 common_btn_bg" onclick="symbol('/')"   />
				                		            <Input type='button' value=' % '  class="btn1 common_btn_bg" onclick="symbol('%')"  />
				                		            <Input type='button' value=' ~ '  class="btn1 common_btn_bg" onclick="symbol('~')"  />
				                		            <Input type='button' value=' 或 '  class="btn1 common_btn_bg" onclick="symbol(' 或 ')"  />
				                		            </td></tr>
				                		            <tr><td height="22" >
				                		            <Input type='button' value=' ( '  class="btn1 common_btn_bg" onclick="symbol('(')" />
				                		            <Input type='button' value=' ) '  class="btn1 common_btn_bg" onclick="symbol(')')" />
				                		            <Input type='button' value=' = '  class="btn1 common_btn_bg" onclick="symbol('=')" />
				                		            <Input type='button'  value=' < '  class="btn1 common_btn_bg" onclick="symbol('<')"  />
				                		            <Input type='button'  value=' > '  class="btn1 common_btn_bg"  onclick="symbol('>')" />
				                		            
				                		             <Input type='button' value=' <> '  class="btn1 common_btn_bg" onclick="symbol('<>')" />
				                		            <Input type='button' value=' <= '  class="btn1 common_btn_bg" onclick="symbol('<=')" />
				                		            <Input type='button' value=' >= '  class="btn1 common_btn_bg" onclick="symbol('>=')" />
				                		            <Input type='button' value=' 包含 '  class="btn1 common_btn_bg" onclick="symbol(' LIKE ')" />
				                		           
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
   if(info!=null&&info[2]!=null&&info[2]!='')
   {
   		for(var i=0;i<document.commonQueryForm.fieldSet.options.length;i++)
   		{
   			if(info[2]==document.commonQueryForm.fieldSet.options[i].value)
   				document.commonQueryForm.fieldSet.options[i].selected=true;
   		}
	   
   }
   
   <% } %>
   </script>
   
  </body>
</html>
