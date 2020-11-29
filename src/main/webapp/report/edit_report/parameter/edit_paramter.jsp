<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.report.edit_report.EditReportForm,com.hjsj.hrms.actionform.report.edit_report.parameter.ParameterForm"%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="javascript" src="/js/function.js"></script>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>

<script type="text/javascript">
	 /*变量声明部分*/ 
  var start1;//用于判断-号出现的位置
  var i;//用于判断字符串中'-'号的出现位置,定义的循环变量
  var chkyear;//用于截取年
  var chkyearinteger;
  var chkmonths;//用于截取月
  var chkmonthsinteger;
  var chkdays;//用于截取日
  var chkdaysinteger;
  var chk1;//用于按位判断输入的年,月,日是否为整数
  var chk2;
  var mon=new Array(12);/*声明一个日期天数的数组*/
  mon[0]=31;
  mon[1]=28;
  mon[2]=31;
  mon[3]=30;
  mon[4]=31;
  mon[5]=30;
  mon[6]=31;
  mon[7]=31;
  mon[8]=30;
  mon[9]=31;
  mon[10]=30;
  mon[11]=31;
  
  function checkData(obj0)
  {
  	if(obj0.value!='')
  	{
  		obj0.value = replaceAll(obj0.value,'.','-');
  		if(!checkData1(obj0))
  		{
  			obj0.value =obj0.defaultValue;
  			return;
  		}else{
  			obj0.defaultValue=obj0.value;
  		}
  	}
  	else
  	{
  		obj0.defaultValue=obj0.value;
  	}
  }
  
  function checkData1(obj)
  {
	   var aa=obj
	   if(aa.value == "")
	   {
	    alert(REPORT_INFO13+"！");
	    aa.focus();
	    return false;
	   }
	   //输入的字符串不为固定格式10个字符时,提示错误
	   if(aa.value.length != 10)
	   {
	    alert(REPORT_INFO14+"！");
	    aa.focus();
	    return false;
	   }
	   //输入的字符串为10个字符时,按项判断是否符合规定的格式
	   else
	   {
		    if(aa.value.substring(4,5)!='-')
		   {
		     alert(REPORT_INFO14+"！");
		    aa.focus();
		    return false;
		   }
		   if(aa.value.substring(7,8)!='-')
		   {
		     alert(REPORT_INFO14+"！");
		    aa.focus();
		    return false;
		   }
		    for(i=0;i<6;i++)
		    {
		     start1=aa.value.substring(i,i+1);
		     if(start1=='-' && i!=4)
		     {
		      alert(REPORT_INFO5);
		      aa.focus();
		      return false;
		     }
		     else
		     {
		      continue;
		     }
		    }
		    for(i=6;i<=9;i++)
		    {
		     start1=aa.value.substring(i,i+1);
		     if(start1=='-'&& i!=7)
		     {
		      alert(REPORT_INFO5);
		      aa.focus();
		      return false;
		     }
		     else
		     {
		      continue;
		     }
		    }
	    
	    
		    //按位判断每位是否为整数
		    for(i=0;i<=9;i++)
		    {
		     chk1=aa.value.substring(i,i+1);
		     if(chk1=='-')
		     {
		     continue;
		     }
		     chk2=parseInt(chk1,10);
		     if(!(chk2>=0&&chk2<=9))
		     {
		      alert(chk1+REPORT_NONUMBER+"！");
		      aa.focus();
		      return false;
		     } 
		    }
		    //判断年是否符合条件
		    chkyear=aa.value.substring(0,4);
		    chkyearinteger=parseInt(chkyear,10);
		    if(!(chkyearinteger>=1900&&chkyearinteger<=2100))
		    {
		     alert(REPORT_INFO6+"!");
		     aa.focus();
		     return false;
		    }
	    
		    //根据年设2月份的日期
		    if(chkyearinteger%100==0||chkyearinteger%4==0)
		    {
		    mon[1]=29;
		    }
		    else
		    {
		    mon[1]=28;
		    }
		    //判断月是否符合条件
		    chkmonths=aa.value.substring(5,7);
		    chkmonthsinteger=parseInt(chkmonths,10);
		    if(!(chkmonthsinteger>=1&&chkmonthsinteger<=12))
		    {
		     alert(REPORT_INFO7+"!");
		     aa.focus();
		     return false;
		    }
	    //判断日期是否符合条件
		    chkdays=aa.value.substring(8,10);
		    chkdaysinteger=parseInt(chkdays,10);
		    switch(chkmonths)
		    {
		     case "01":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[0]))
		        {
		         alert("1"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case "02":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[1]))
		        {
		         alert("2"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		    
		     case "03":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[2]))
		        {
		         alert("3"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case "04":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[3]))
		        {
		         alert("4"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case "05":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[4]))
		        {
		         alert("5"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case "06":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[5]))
		        {
		         alert("6"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		    
		     case "07":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[6]))
		        {
		         alert("7"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case "08":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[7]))
		        {
		         alert("8"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     
		     case "09":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[8]))
		        {
		         alert("9"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case "10":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[9]))
		        {
		         alert("10"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case "11":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[10]))
		        {
		         alert("11"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case "12":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[11]))
		        {
		         alert("12"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     }//日期判断结束
		  }
  		 return true;
  }









	var message = "";
	var b=false;
    var url = "";
    var operateObject = <bean:write name="parameterForm" property="operateObject"/>;
    if (operateObject == 1) {
        url = '/report/edit_report/reportSettree.do?b_query=link&code=${parameterForm.tabid}&status=${parameterForm.status}&operateObject=1';
    } else {
        url = '/report/report_collect/reportOrgCollecttree.do?b_query=link&a_code=<%=request.getParameter("unitcode")%>&operateObject=2'; 
    }
    
    function save() {
       arrays = message.split("@");
       for( var i=0; i<arrays.length-1; i++){
       		dba = arrays[i].split("#");
       		var object = document.getElementsByName(dba[1]);     	
       		var n = object.length;
       		if(n == 0){
       			var temp0=dba[1].split(".");
       			object = document.getElementsByName(temp0[0]+".value");
       			var value=object[0].value;
       			if(value.match(/^\s*$/)){
	       			alert(dba[0]+REPORT_INFO15+"!");
	       			return;
       			}
       		}else{
       			var value=object[0].value;
       			if(value.match(/^\s*$/)){
	       			alert(dba[0]+REPORT_INFO15+"!");
	       			return;
       			}
       		}
       }
       
        var flg = 0;
        var hashvo=new ParameterSet();	   
        for(var i = 0; i < parameterForm.elements.length; i++) {
            if (parameterForm.elements[i].type == "text" || parameterForm.elements[i].type == "hidden") {
                 if (parameterForm.elements[i].name.indexOf(".hzvalue") != -1) 
                     continue;
                 flg = 1;    
                 var value=parameterForm.elements[i].value;
                 if(value.match(/^\s*$/)){//正则过滤空串内容  50269
                	 value="";
                	 document.getElementsByName(parameterForm.elements[i].name)[0].value=""
                 }
                 if(value.indexOf("\'")!=-1||value.indexOf("\"")!=-1)
		  		 {
		  					alert("参数值不支持 \' 和 \" 符号!");
		  					return ;
		  		 }
                 hashvo.setValue(parameterForm.elements[i].name,getEncodeStr(value));
            }  
        }
        if (flg == 0) {
            alert('<bean:message key="report_parameternull"/>!');
            return;
        }
        
        hashvo.setValue("tsortid","<bean:write name="parameterForm" property="tsortid" filter="true" />");
        hashvo.setValue("paramscope",'<%=(request.getParameter("paramscope"))%>');
        hashvo.setValue("unitcode", "${parameterForm.unitcode}");
        hashvo.setValue("operateObject", operateObject);
         var In_paramters="";  
   	     var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList,functionId:'0305000001'},hashvo);
    }
    function checkNum(mythis, len1, len2, isEmpty)
	{
	    if (isEmpty == "1") {
	        checkEmpty(mythis);
	    }
	    var i,j,strTemp;
	    var str1,str2;
	    var n=0;
	   
	  
	    strTemp="0123456789.";
	    if ( mythis.value.length== 0)
	    {
	        return true;
	    }    
	    for (i=0;i<mythis.value.length;i++)
	    {
	        j=strTemp.indexOf(mythis.value.charAt(i)); 
	        if (j==-1)
	        {
	            //说明有字符不是数字
	            alert(PLEASEWRITENUMBER+'！');
	            mythis.focus();
	            return false;
	        }   
	        if(mythis.value.charAt(i)==".")
	        {
	            n=n+1;
	        }   
	        if(n>1)
	        {
	            alert(REPORT_INFO16+'!');
	            mythis.focus();
	            return false;
	        }
	    }
	     //alert(mythis.value  + "dd"  + len1);
	  
	    if(mythis.value.indexOf(".")!=-1)
	    {
	     	str1 = mythis.value.substr(0,mythis.value.indexOf("."));
	     	str2 = mythis.value.substr(mythis.value.indexOf(".")+1,mythis.value.length);
	     	
	     	if(str1.length>len1)
	     	  {
	     	  	alert(REPORT_INFO17+len1);
	     	  	mythis.focus();
	     	  	return false;
	     	  }
	        if(str2.length>len2)
	        {
	        	alert(REPORT_INFO18+len2);
	        	mythis.focus();
	     	  	return false;
	        }
	    }
	    else
	    {
	    	str1 = mythis.value;
	    	if(str1.length>len1)
	     	  {
	     	  	alert(REPORT_INFO17+len1);
	     	  	mythis.focus();
	     	  	return false;
	     	  }
	    }   
	    //说明是数字
	    return true;
	}
    function showFieldList() {
        alert(SAVESUCCESS+"!");
    }
    function mychange() {
        parameterForm.action = "/report/edit_report/parameter.do?b_query=query&unitcode=<%=request.getParameter("unitcode")%>";
        parameterForm.submit();
    }
    function check() {
    	
        if(!(event.keyCode >= 48 && event.keyCode <= 57 || event.keyCode == 8 || (event.keyCode>=96 && event.keyCode <=105) ||event.keyCode==190||event.keyCode==110))
        {
            alert(PLEASEWRITENUMBER+"！");
            event.returnValue = "";
        } 
    }
    
    function mysubmit() {
        parameterForm.action = url;
        parameterForm.submit();
    }
    
    //检测是否为空
    function checkEmpty(mythis) {
        if (mythis.value == "" && b==true) {
       		//b=false;
        	alert('<bean:message key="edit_report.parameter.checkempty"/>');
           mythis.focus();
        }
        b=true;
    }
    
    function setNull(hiddenValue,value) {
        hiddenValue = document.getElementsByName(hiddenValue);
        value = document.getElementsByName(value);
        if (event.keyCode == 8) {
           hiddenValue[0].value = "";
           value[0].value= "";
        } else {
           event.returnValue = "";
        }
    }
	function alertlength(mythis,length){
		//mythis.value.length
		var c, b = 0 ,n = 0, l = mythis.value.length;   
		while(l) {    
			c = mythis.value.charCodeAt(--l);
			b += c < 128 ? 1 : 2; 
			if(b<length)
				n++;
		} 
		if(b>length){
			alert("输入的字符串的长度超过限制！");
			mythis.value = mythis.value.substring(0,n);
		}
	}
   
   /*****************************
   *代码字段内容修改时，相应修改对应的hidden字段的内容
   *
   ******************************/
   function fieldcode(sourceobj,flag)
   {
	var targetobj,target_name,hidden_name,hiddenobj;
       target_name=sourceobj.name;
       if(flag==1)
         hidden_name=target_name.replace(".hzvalue",".value");
       else
         hidden_name=target_name.replace(".viewvalue",".value");       	
       var hiddenInputs=document.getElementsByName(hidden_name);    
       if(hiddenInputs!=null)
       {
       	hiddenobj=hiddenInputs[0];    	
       	codevalue="";
       }   
       hiddenobj.value=sourceobj.value;
   }
    
</script>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title></title>
		<link href="../../../css/css1.css" rel="stylesheet" type="text/css">
		<hrms:themes/>
	</head>
	<body>

	  <html:form action="/report/edit_report/parameter" style="width:100%;">
		 
		<!--  <table width="60%" border="0" cellspacing="1" align="center" cellpadding="1" class="ListTable">
				<tr>
					<td colspan="3">
						<html:select name="parameterForm" property="paramscope" onchange="mychange();">
							<html:option value="0">
								            全局参数
								       </html:option>
							<html:option value="1">
								            表类参数
								       </html:option>
						</html:select>

					</td>
				</tr>
				<tr>
					<td colspan="3">
					</td>
				</tr>
			</table>  -->
			<table width="100%" height="90%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
				<thead>
					<tr>
						<td width="45%" align="center" nowrap class="TableRow">
							<bean:message key="column.name" />
						</td>
						<td width="10%" align="center" nowrap class="TableRow">
							<bean:message key="label.org.type_org" />
						</td>
						<td width="45%" align="center" nowrap class="TableRow">
							<bean:message key="edit_report.parameter.value" />

							<bean:message key="hmuster.label.total" />
							(
							<bean:write name="parameterForm" property="num" filter="true" />
							)
							<bean:message key="edit_report.parameter.Entries" />
						</td>
					</tr>
				</thead>
			<% int i = 0; %>
				<hrms:extenditerate id="element" name="parameterForm" property="paramForm.list" indexes="indexes" pagination="paramForm.pagination" pageCount="10000" scope="session">
					<%
			if (i % 2 == 0) {%>
					<tr class="trShallow">
						<%} else {%>
					<tr class="trDeep">
						<%}
			i++;%>
						<td align="left" class="RecordRow" nowrap>
							<bean:write name="element" property="paramname" filter="true" />
						</td>
						<td align="center" class="RecordRow" nowrap>
							<bean:write name="element" property="paramtype" filter="true" />
							&nbsp;
						</td>
						<td align="left" class="RecordRow" nowrap>
							<INPUT type="text" size="28"
								name=<logic:equal name="element" property="paramtype" value="代码">
                   
								   "<bean:write name="element" property="paramename" filter="true" />.hzvalue"
								   class="textColorRead"
							    </logic:equal>
								<logic:notEqual name="element" property="paramtype" value="代码">
								   "<bean:write name="element" property="paramename" filter="true" />.value"
								   class="textColorWrite"
							    </logic:notEqual>
								<logic:equal name="element" property="paramtype" value="日期">
									onchange="checkData(this)"
									class="textColorWrite"
								</logic:equal> 
								<logic:equal name="element" property="paramtype" value="数值">onkeydown="check()"</logic:equal>
								<logic:equal name="element" property="paramtype" value="代码">
									readonly
							       onclick='openCondCodeDialog("<bean:write name="element" property="paramcode" filter="true" />",
							       
							     "<bean:write name="element" property="paramename" filter="true" />.hzvalue")'
					              onkeydown="setNull('<bean:write name="element" property="paramename" filter="true" />.value'
					                  ,'<bean:write name="element" property="paramename" filter="true" />.hzvalue')"
				                  onchange="fieldcode(this,1)"
							    </logic:equal>
								value="<bean:write name="element" property="value" filter="true" />" <logic:equal name="element" property="paramtype" value="字符">
								   maxlength="<bean:write name="element" property="paramlen" filter="true"  />";
								   onchange="alertlength(this,'<bean:write name="element" property="paramlen" filter="true"  />')"
					              class="textColorWrite"
							    </logic:equal>
								
								<logic:equal name="element" property="paramtype" value="数字">
								   onchange="checkNum(this, <bean:write name="element" property="paramlen" filter="true" />
								   ,<bean:write name="element" property="paramfmt" filter="true" />
								   ,<bean:write name="element" property="paramNull" filter="true" />
								   )";
					              class="textColorWrite"
							    </logic:equal> 
							    <%
							    EditReportForm editReportForm = (EditReportForm) session.getAttribute("editReportForm");
							    if(Integer.parseInt(request.getParameter("operateObject"))==1){
									
							   		 if((Integer.parseInt(editReportForm.getStatus())==1)||(Integer.parseInt(editReportForm.getStatus())==3)) {%> 
							    	readonly
							    <%}}else{
							    	ParameterForm parameterForm=(ParameterForm)session.getAttribute("parameterForm");
							    	if(parameterForm.getFlag().equals("true")){
							   %>
							   		readonly
							    <%}else{%>
							    <% }
							    }%>
							    >
							<logic:equal name="element" property="paramtype" value="代码">
								<INPUT type="hidden" name="<bean:write name="element" property="paramename" filter="true" />.value" value="<bean:write name="element" property="valuehidden" filter="true" />">
							</logic:equal>
							
							<logic:equal name="element" property="paramNull" value="1">
						        <script language="javaScript">
						        	var t = "<bean:write name="element" property="paramname" filter="true" />";
						        	var m =  "<bean:write name="element" property="paramename" filter="true" />.hzvalue";
									message += t;
									message += "#";
									message += m;
									message += "@";
						        </script>
							</logic:equal>
						</td>
					</tr>
				</hrms:extenditerate>
			</table>

			<table width="100%" align="center" style="margin-top: 1px;">
				<tr>
					<td align="center">
					<% 
							    if(Integer.parseInt(request.getParameter("operateObject"))==1){
									 EditReportForm editReportForm = (EditReportForm) session.getAttribute("editReportForm");
							   		 if(!(Integer.parseInt(editReportForm.getStatus())==1)&&!(Integer.parseInt(editReportForm.getStatus())==3)) {%> 
							    	<input type="button" onclick="save()" name="b_save" value="<bean:message key="button.save" />" class="mybutton">
							    <%}}else{
							    		ParameterForm parameterForm=(ParameterForm)session.getAttribute("parameterForm");
							    		if(((String)parameterForm.getFlag()).equals("true")){%>
									
										<%}else{%>
									<input type="button" onclick="save()" name="b_save" value="<bean:message key="button.save" />" class="mybutton">
										<%} 
									}%>
					</td>
				</tr>
			</table>
		</html:form>
	</body>
</html>
