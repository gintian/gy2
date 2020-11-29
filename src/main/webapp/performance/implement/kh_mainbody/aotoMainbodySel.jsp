<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes />
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<style>
.myfixedDiv 
{ 
	overflow:auto; 
	/*height:expression(document.body.clientHeight-100);*/
	/*width:100%;*/
    margin-right:10px;
    border:0;
	/*BORDER-BOTTOM: #94B6E6 1pt solid;
    BORDER-LEFT: #94B6E6 1pt solid;
    BORDER-RIGHT: #94B6E6 1pt solid;
    BORDER-TOP: #94B6E6 1pt solid ; */
}
   TABLE.list{   
       BORDER-BOTTOM: 1 none;   
       BORDER-LEFT: 1 none;   
       BORDER-RIGHT: 1 none;   
       BORDER-TOP: 1 none;   
       FONT: messagebox;     
       overflow: hidden;
    	FONT-SIZE: 9pt;    
   }   
   TABLE.list TR.first{   
       BACKGROUND-COLOR: rgb(255,255,255);   
       COLOR: black;   
       FONT: messagebox;   
       cursor:pointer;   
    	FONT-SIZE: 9pt;    
   }   
   TABLE.list TR.second{   
       BACKGROUND-COLOR: rgb(240,240,240);   
       COLOR: black;   
       FONT: messagebox;   
       cursor:pointer;   
   }   
   TABLE.list TR.mouseover{   
       BACKGROUND-COLOR: #FFF8D2;   
       cursor:pointer;   
   }   
   TABLE.list TR.selected{   
       BACKGROUND-COLOR: FFF8D2;   
       COLOR: black;   
 
   } 
</style>
<script>

	 var hasclear=false;

	function myClose()
	{
		var thevo=new Object();
		thevo.flag="false";
		window.returnValue=thevo;
	}
	function setType(id1,id2,id3){
 		var obj=eval(id1);
	    var obj2=eval(id2);
	    var obj3=eval(id3);
    	obj.style.display='inline';
    	obj2.style.display='none';
    	if(id1.indexOf("b")!=-1){
    		obj3.style.display='inline';
    	}else{
    		obj3.style.display='none';
    	}
	}
	function simpleCondition(exist,body_id,a_cexpr,a_condStr)
	  	{
	  	    window.tempObj = {};
            window.tempObj.body_id = body_id;
	  		var info,queryType,dbPre;
		    info="5";
		    dbPre="Usr";
	        queryType="1";
	        var express="";
	       if(exist==1){
	        express=a_cexpr+'|'+a_condStr;
	       }
	         if(hasclear){
	         	express="";
	         }
	     	//var strExpression = generalExpressionDialog(info,dbPre,queryType,express);//dml 2012-2-28 15:43:48
	        var thecodeurl="/general/inform/search/generalsearch.do?b_query=link`type="+info+"`a_code=UN`tablename="+dbPre+"`expr="+express;
            var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
      		var config={
      		    width:750,
                height:420,
                type:'2'
            }
            modalDialog.showModalDialogs(iframe_url,"simpleCondition",config,selectReturn);

	  	}
	  function selectReturn(strExpression){
          if(strExpression)
          {
              if(strExpression=='no'){
                  alert("条件选择错误，请重新选择！");
                  return;
              }
              var temps=strExpression.split("|");
              var hashvo=new ParameterSet();
              hashvo.setValue("plan_id",${implementForm.planid});
              hashvo.setValue("body_id",window.tempObj.body_id);
              hashvo.setValue("flag","1");
              hashvo.setValue("cond",getEncodeStr(temps[1]));
              hashvo.setValue("cexpr",temps[0]);
              var request=new Request({method:'post',asynchronous:false,onSuccess:setCondResult,functionId:'9026001008'},hashvo);
          }
      }
	    function complexCondition(exist,body_id,a_cexpr,a_condStr)
	  	{
	  	 if(exist!=1){
	        a_condStr="";
	       }
	       var parm="";
	       if("2"=="${implementForm.object_type}")
	       parm = "jixiao_aoto_1"
	       else
	        parm = "jixiao_aoto_2"
	        a_condStr =getDecodeStr(a_condStr);
	        if(hasclear){
	        	a_condStr="";
	        }
            var arguments=new Array(a_condStr,"0",GZ_TEMPLATESET_LOOKCONDITION,"4");
            var strurl="/general/query/common/complexCondition.do?br_init=link`mode="+parm;
            var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
            if (!window.showModalDialog){
                window.dialogArguments = arguments;
            }
            window.tempObj = {};
            window.tempObj.body_id=body_id;
            var width=860
            if(window.showModalDialog){
                width=880
            }
            var config={
                width:width,
                height:500,
                dialogArguments:arguments,
                type:'2'
            }
            modalDialog.showModalDialogs(iframe_url,null,config,complexCondition_ok);
	  	}
	  	function complexCondition_ok(strExpression) {
            if(strExpression)
            {
                var hashvo=new ParameterSet();
                hashvo.setValue("plan_id",${implementForm.planid});
                hashvo.setValue("body_id",window.tempObj.body_id);
                hashvo.setValue("flag","1");
                if(hasclear){
                    hashvo.setValue("cond","");
                }else{
                    hashvo.setValue("cond",getEncodeStr(strExpression));
                }

                hashvo.setValue("cexpr","");
                var request=new Request({method:'post',asynchronous:false,onSuccess:setCondResult,functionId:'9026001008'},hashvo);
            }
        }
	  	function setCondResult(outparamters){
                window.tempObj=undefined;
				var info=outparamters.getValue("info");
				if(info=="ok"){
					alert(PERFORMANCE_COND_SUCCES);
					reflesh(); 
					
				}else if(info=="ok2"){
				
				}else if(info=="ok3"){
					hasclear=true;
					alert(" 主体类别筛选条件清空成功！");
					reflesh(); 
				}
				else{
					alert(PERFORMANCE_COND_FAIL);
				}
		}
    function reflesh(){
		window.location.href="/performance/implement/kh_mainbody/aotoMainbodySel.do?b_query=link";
   }	
  function save(){
  /*
   var scopevos = eval("document.implementForm.scope");
   var scopestr ="";  
   for(var i=0;i<scopevos.length;i++){
   scopestr+=scopevos[i].value+"`";
   }
   */
   window.location.href="/performance/implement/kh_mainbody/aotoMainbodySel.do?b_query=link&type=save";
   
   }
   function  operation(){ 
     if(!confirm("确认执行自动分配考核主体吗？"))
     {
     	return;
     }
     var setinfo = '${implementForm.setinfomation}';
     if(setinfo!=null&&setinfo.length>0){
       if(!confirm("考核主体:"+setinfo+"没有设置考核范围或条件！\r\n是否继续？"))
     {
     	return;
     }
     }
      var waitInfo=eval("wait");	   
	  waitInfo.style.display="block";
      var hashvo=new ParameterSet(); 
	  hashvo.setValue("plan_id",${implementForm.planid});
				//hashvo.setValue("queryA0100",${implementForm.queryA0100});
	  hashvo.setValue("orderSql","${implementForm.orderSql}");
	  var request=new Request({method:'post',asynchronous:true,onSuccess:closepage,functionId:'9026001009'},hashvo);
   }
   function closepage(outparamters)
   {
		var waitInfo=eval("wait");	   
   		waitInfo.style.display="none";
		var info=outparamters.getValue("info");
		if(info=="ok")
		{
			alert(PERFORMANCE_APPLAY_SUCCES);			
			var thevo = new Object();
			thevo.flag="true";
			if(window.showModalDialog){
				parent.window.returnValue=thevo;
			}else {
	  			<%
	  				String callBackFunc = request.getParameter("callBackFunc");
					if(callBackFunc != null && callBackFunc.length() > 0) {
				%>
					parent.window.opener.<%=callBackFunc%>(thevo);
				<%}%>
			}
  			parent.window.close();  	
		} 
		else{
			alert(PERFORMANCE_APPLAY_FAIL);
		}
	}
	function selectRange(bodythis,body_id){
	var selectid = bodythis.value;
	var hashvo=new ParameterSet();
				hashvo.setValue("plan_id",${implementForm.planid});
					hashvo.setValue("body_id",body_id);
				hashvo.setValue("flag","2");
				hashvo.setValue("cexpr","");
				hashvo.setValue("scope",selectid);
				var request=new Request({method:'post',asynchronous:false,onSuccess:setCondResult,functionId:'9026001008'},hashvo);
	}
	var ctrl = false;   
  var shift = false;   
  var tableStyle;//存放table各列的样式   
  var ifFirst=true;//用来第一次初始化table各列的样式   
  var selectNum=0;//选择行的个数，用来判断选中行的个数   
  var rows;//当前行序号   
  var selectElement;//选择行对象   
	var rows;//存放table各列的样式   
  //处理按ctrl和shift键   
  document.onkeydown = function () { 
   if (event.keyCode == 17) {   
    ctrl = true;   
   } else {   
    if (event.keyCode == 16) {   
     shift = true;   
    }   
   }   
  };   
  document.onkeyup = function () {   
   ctrl = false;   
   shift = false;   
  };   
     
  //鼠标经过时   
  function dmlover(theTR){   
   //鼠标第一次经过时初始化   
   if(ifFirst){   
    //遍历table所有行的样式，并放到数组tableStyle中，第一次点击的时候遍历   
    rows=new Array(infoTable.rows.length);   
    tableStyle=new Array(infoTable.rows.length);   
    for (var i = 0; i < infoTable.rows.length; i++) {   
     tableStyle[i]=infoTable.rows[i].className;
    }   
    ifFirst=false;   
   }   
      if(theTR.className != "selected"){   
          theTR.className = "mouseover";   
      } 
  }   
  //鼠标离开时   
  function dmlrestore(theTR){   
      if(theTR.className != "selected")   
          theTR.className = tableStyle[theTR.rowIndex]   
  }   
     
  //鼠标按下时   
  //每行中selectState属性可以在需要的时候判断是否选中某一行或某几行   
  function clickRow(theTR) {   
   if (ctrl && shift) {//同时按下ctrl和shift则不操作   

    return;   
   }   
   rowI=theTR.rowIndex;   

   //如果选择的是复选框按钮时   
   if(event.srcElement.tagName=="INPUT" && event.srcElement.type=="checkbox" &&!shift ){   
    var state = event.srcElement.checked;   
          if(state){   
        infoTable.rows[rowI].className = "selected";
        infoTable.rows[rowI].selectState = "yes";
        selectNum++;
       }else{   
        infoTable.rows[rowI].className = tableStyle[rowI];
        infoTable.rows[rowI].selectState = "no";
        selectNum--;   
       }   
          
       for (var i = 1; i < infoTable.rows.length; i++) {   
	     if(infoTable.rows(i).selectState == "yes"){   
	         //赋当前操作行对象   
	         selectElement=infoTable.rows[i];
	     }   
       }   
    return;   
   }   
   if (!ctrl && !shift) {//没有按ctrl或shift时   
	     
	     try {   
	      infoTable.rows(rowI).cells(0).firstChild.checked = true;   
	     } catch (e) {   
	      //页面中没有复选框或单选框时   
	     } 
	     if(infoTable.rows[rowI].className == "selected"){
	     	infoTable.rows[rowI].className = tableStyle[rowI];
	        infoTable.rows[rowI].selectState = "no";
	         rows[rowI]='false';
	     }  else{
		    rows[rowI]='true';  
	     	infoTable.rows[rowI].className = "selected";
	    	infoTable.rows[rowI].selectState = "yes";
	     }
	      
	    infoTable.currentRow = rowI;   
	    selectNum=1;   
   }  
   if (ctrl) {//按ctrl键时   
       if(infoTable.rows[rowI].className == "selected"){   alert("12");
	        try {   
	      		infoTable.rows[rowI].cells[0].firstChild.checked = false;
	     	} catch (e) {   
	      //页面中没有复选框或单选框时   
	     	}   
	        infoTable.rows[rowI].className = tableStyle[rowI];
	        infoTable.rows[rowI].selectState = "no";
	        selectNum--;   
       }else{   alert("13");
	        try {   
	      		infoTable.rows[rowI].cells[0].firstChild.checked = true;
	     	} catch (e) {   
	      	//页面中没有复选框或单选框时   
	     	}   
	        infoTable.rows[rowI].className = "selected";
	        infoTable.rows[rowI].selectState = "yes";
	        selectNum++;   
       }   
   }   
   if (shift) {//按shift键时   
	    for (var i = 1; i < infoTable.rows.length; i++) {   
		     try {   
		      	infoTable.rows[i].cells[0].firstChild.checked = false;
		     } catch (e) {   
		      //页面中没有复选框或单选框时   
		     }   
		     infoTable.rows[i].className = tableStyle[i];
		     infoTable.rows[rowI].selectState = "no";
	    }   
	       
	    if (rowI < infoTable.currentRow) {   
		     for (var i = rowI; i <= infoTable.currentRow; i++) {   
			      try {   
			     	 infoTable.rows[parseInt(i)].cells[0].firstChild.checked = true;
			      } catch (e) {   
			      //页面中没有复选框或单选框时   
			      }   
			      infoTable.rows[parseInt(i)].className = "selected";
			      infoTable.rows[parseInt(i)].selectState = "yes";
		     }   
	    }else{   
		     for (var i = infoTable.currentRow; i <= rowI; i++) {   
			      try {   
			      	infoTable.rows[parseInt(i)].cells[0].firstChild.checked = true;
			      } catch (e) { 
			        
			      }   
			      infoTable.rows[parseInt(i)].className = "selected";
			      infoTable.rows[parseInt(i)].selectState = "yes";
		     }   
	    }   
	       
	    selectNum=Math.abs(parseInt(infoTable.currentRow)-rowI)+1;
   }   
      
   for (var i = 1; i < infoTable.rows.length; i++) {   
	    if(infoTable.rows[i].selectState == "yes"){
	        //赋当前操作行对象   
	        selectElement=infoTable.rows[i];
	    }   
   }   
  } 
  function ClearCDT(){
  	var bodysetid='';
  	if(rows!=null&&rows.length>0){
  		for(var i=1;i<rows.length;i++){
  			if(rows[i]!=null&&rows[i]=='true'){
  				var tt=infoTable.rows[i].cells[0].innerText;
  				if(tt.length>1){
  					tt=tt.substr(1);
  					bodysetid+=","+tt;
  				}	
  			}
  		}
  		if(bodysetid.length>1){
  			if(confirm("确认要清除考核主体筛选条件吗？")){;
  			 	var hashvo=new ParameterSet();
				hashvo.setValue("plan_id",${implementForm.planid});
				hashvo.setValue("body_id",bodysetid.substr(1));
				hashvo.setValue("flag","1");
				hashvo.setValue("editflag","clear");
				hashvo.setValue("cond","");
				hashvo.setValue("cexpr","");
				var request=new Request({method:'post',asynchronous:false,onSuccess:setCondResult,functionId:'9026001008'},hashvo);
				}else{
				
				return;
				}
  		}else{
  			alert("请选择要清空条件的考核主体类别！");
  			return;
  		}
  	}
  }
</script>
<%
int i = 0;
%>
<body>

	<html:form action="/performance/implement/kh_mainbody/aotoMainbodySel">
	<table width="100%" border="0" align="center" style="" class="list">
		<tr>
			<td>
			<div id="myfixedDiv" class="myfixedDiv">
		<table width="99%" border="0" cellspacing="0" style="margin-top:1px;" align="center" id="infoTable"
			cellpadding="1" class="ListTable">
			<thead>
				<tr class="fixedHeaderTr">
					<td align="center" class="TableRow" nowrap>
						<bean:message key="report.number" />
					</td>
					<td align="center" class="TableRow" nowrap>
						<bean:message key="jx.paramset.mainbodyname" />
					</td>
					<!--
					<td align="center" class="TableRow" nowrap>
						<bean:message key="menu.performance.mainBodyrange" />
					</td>
					-->
					<td align="center" class="TableRow" width="250" nowrap>
						<bean:message key="label.title.selectcond" />
					</td>
					<!--  td align="center" class="TableRow" width="100" nowrap>
						<bean:message key="gz.templateset.simpleCondition" />
					</td>
					<td align="center" class="TableRow" width="100" nowrap>
						<bean:message key="popedom.gjtj" />
					</td-->
				</tr>
			</thead>
			<hrms:extenditerate id="element" name="implementForm"
				property="setaotolistform.list" indexes="indexes"
				pagination="setaotolistform.pagination" pageCount="1000" scope="session">
				<bean:define id="body_id" name="element" property="string(body_id)" />
				<%
						if (i % 2 == 0)
						{
				%>
				<tr class="trShallow"  selectState="no" class="first" onMouseover="dmlover(this);" onMouseout="dmlrestore(this);" onclick="clickRow(this)">
					<%
							} else
							{
					%>
				
				<tr class="trDeep"  selectState="no" class="first" onMouseover="dmlover(this);" onMouseout="dmlrestore(this);" onclick="clickRow(this)">
					<%
							}
							i++;
					%>
					<td align="left" class="RecordRow" nowrap>
						 &nbsp;<bean:write name="element" property="string(body_id)"
							filter="true" />
					</td>
					<td align="left" class="RecordRow" nowrap>
						 &nbsp;<bean:write name="element" property="string(name)" filter="true" />
					</td>
					
					<!-- 
					<td align="left" class="RecordRow" nowrap> &nbsp;
					<logic:notEqual name="element" property="string(body_id)" value="5">
					
						 <html:select name="element" property="string(scope)" onchange="selectRange(this,'${body_id}')" styleId="scope" size="1">
						 <html:option value="-1">
						 &nbsp;
						</html:option>
						<html:option value="1">
							<bean:message key='label.title.org' />
						</html:option>
						<html:option value="2">
							<bean:message key='label.title.topdept' />
						</html:option>
						<html:option value="3">
							<bean:message key='label.title.deptself' />
						</html:option>
					</html:select>
					</logic:notEqual>
					</td>
					-->
					<td align="left" class="RecordRow" nowrap> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<logic:notEqual name="element" property="string(body_id)" value="5">
					<logic:notEqual name="element" property="string(cexpr)" value="">
						 <input type="radio" name="operater<%=i %>"  onclick="setType('operater<%=i %>a','operater<%=i %>b','operater<%=i %>c')"  checked>
						  <bean:message key="gz.templateset.simpleCondition" />
						 <div  id="operater<%=i %>a" style='display:inline;' nowrap>
						 <a
							onclick="simpleCondition('1','<bean:write name="element" property="string(body_id)" filter="true"/>','<bean:write name="element" property="string(cexpr)" filter="true"/>','<bean:write name="element" property="string(cond)" filter="true"/>');"><img
								src="/images/edit.gif" border=0 style="cursor:hand;">
						</a>
						</div>
						<div  id="operater<%=i %>c" style='display:none;' nowrap>&nbsp;&nbsp;&nbsp;&nbsp;</div>
						 </logic:notEqual>
						 <logic:equal name="element" property="string(cexpr)" value="">
						 <input type="radio" name="operater<%=i %>"  onclick="setType('operater<%=i %>a','operater<%=i %>b','operater<%=i %>c')" >
						  <bean:message key="gz.templateset.simpleCondition" />
						  <div  id="operater<%=i %>a" style='display:none;' nowrap>
						 <a
							onclick="simpleCondition('0','<bean:write name="element" property="string(body_id)" filter="true"/>','<bean:write name="element" property="string(cexpr)" filter="true"/>','<bean:write name="element" property="string(cond)" filter="true"/>');"><img
								src="/images/edit.gif" border=0 style="cursor:hand;">
						</a>
						</div>
						 <div  id="operater<%=i %>c" style='display:inline;' nowrap>&nbsp;&nbsp;&nbsp;&nbsp;</div>
						 </logic:equal>
						
					  
						
						<logic:notEqual name="element" property="string(cexpr)" value="">
						 <input type="radio" name="operater<%=i %>"  onclick="setType('operater<%=i %>b','operater<%=i %>a','operater<%=i %>c')" >
						 <bean:message key="popedom.gjtj" />
						  <div  id="operater<%=i %>b" style='display:none;' nowrap>
						  
						 <a
							onclick="complexCondition('0','<bean:write name="element" property="string(body_id)" filter="true"/>','<bean:write name="element" property="string(cexpr)" filter="true"/>','<bean:write name="element" property="string(cond)" filter="true"/>');"><img
								src="/images/edit.gif" border=0 style="cursor:hand;">
						</a>
						</div>
						 </logic:notEqual>
						 <logic:equal name="element" property="string(cexpr)" value="">
						  <logic:notEqual name="element" property="string(cond)" value="">
						 <input type="radio" name="operater<%=i %>"  onclick="setType('operater<%=i %>b','operater<%=i %>a','operater<%=i %>c')" checked>
						  <bean:message key="popedom.gjtj" />
						 <div  id="operater<%=i %>b" style='display:inline;' nowrap>
						
						 <a
							onclick="complexCondition('1','<bean:write name="element" property="string(body_id)" filter="true"/>','<bean:write name="element" property="string(cexpr)" filter="true"/>','<bean:write name="element" property="string(cond)" filter="true"/>');"><img
								src="/images/edit.gif" border=0 style="cursor:hand;">
						</a>
						</div>
						</logic:notEqual>
						<logic:equal name="element" property="string(cond)" value="">
						 <input type="radio" name="operater<%=i %>"  onclick="setType('operater<%=i %>b','operater<%=i %>a','operater<%=i %>c')" >
						  <bean:message key="popedom.gjtj" />
						 <div  id="operater<%=i %>b" style='display:none;' nowrap>
						
						 <a
							onclick="complexCondition('0','<bean:write name="element" property="string(body_id)" filter="true"/>','<bean:write name="element" property="string(cexpr)" filter="true"/>','<bean:write name="element" property="string(cond)" filter="true"/>');"><img
								src="/images/edit.gif" border=0 style="cursor:hand;">
						</a>
						</div>
						</logic:equal>
						 </logic:equal>
						 </logic:notEqual>
					</td>
					
				</tr>
			</hrms:extenditerate>
		</table>
		</div>
	</td>
				</tr>
	</table>

		<table width="100%">
			<tr>
				<td align="center">
				
						<input type="button" class="mybutton" value="<bean:message key='button.save' />" onClick="save();" />
						
						<logic:equal name="implementForm" property="busitype" value="0">	
							<hrms:priv func_id="326030131">
								<input type="button" class="mybutton" value="<bean:message key='performance.implement.apply' />" onClick="operation();" />
							</hrms:priv>
						</logic:equal>
						<logic:equal name="implementForm" property="busitype" value="1">	
							<hrms:priv func_id="360302201">
								<input type="button" class="mybutton" value="<bean:message key='performance.implement.apply' />" onClick="operation();" />
							</hrms:priv>
						</logic:equal>
						<input type="button" class="mybutton" value="清空条件" onClick="ClearCDT();" />
						<!-- xus 19/12/26 【56669】V77绩效管理：谷歌，自动分配考核主体，取消没反应   -->
						<input type="button" class="mybutton" value="<bean:message key='button.cancel'/>" onClick="parent.window.close();">  
						<%//if(request.getParameter("returnflag")!=null){ %>
					<hrms:tipwizardbutton flag="performance" target="il_body" formname="implementForm"/>  
					<%//} %>  
				</td>
			</tr>
		</table>
	</html:form>
	<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style" height=24>正在处理数据,请稍候....</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div>
<script type="text/javascript">
    var myfixedDiv = document.getElementById("myfixedDiv")
    if(myfixedDiv){
        myfixedDiv.style.height=(document.body.clientHeight-100)+"px";
    }
</script>
</body>

