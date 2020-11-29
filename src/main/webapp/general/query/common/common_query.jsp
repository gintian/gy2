<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.general.query.CommonQueryForm,java.util.*"%>
<!-- 引入ext 和代码控件      wangb 20180125 -->
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="/components/codeSelector/codeSelector.js"></script>
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
<%
	CommonQueryForm commonQueryForm = (CommonQueryForm) session
			.getAttribute("commonQueryForm");
	String[] dbpre = commonQueryForm.getDbpre();
	ArrayList factorlist = commonQueryForm.getFactorlist();
	int i = 0;
	int j = 0;
	int num = factorlist.size();
	int status = 0;
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	status = userView.getStatus();
	String manager = userView.getManagePrivCodeValue();
	
	/**
	 * 由先前的按人员管理范围控制改成按如规则进行控制
	 * 人员、单位和岗位按业务范围-操作单位-人员管理范围优先级进行控制 
	 * cmq changed at 2012-09-29
	 */
	if (commonQueryForm.getType().equalsIgnoreCase("1")
			|| commonQueryForm.getType().equalsIgnoreCase("2")
			|| commonQueryForm.getType().equalsIgnoreCase("3")) {
		manager = userView.getUnitIdByBusi("4");
	}
	String css_url="/css/css1.css";
	String bosflag="";
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	  	bosflag=userView.getBosflag();
	}
	//end.
%>
<script language="javascript">
   var isIE = getBrowseVersion();
   var date_desc;
   /*只有一个库时,对库进行隐藏*/
   function hideDbase(infor)
   {
     if(infor!="1")
       return;
     
//     var elements=document.getElementById('dbpre');
     var elements=document.getElementsByName('dbpre')[0];
     if(elements==''||!(elements instanceof Array))
     {
       elements.checked=true;
       if(isIE && isIE != 10){
       	Element.hide('dbase');
       }else{
		document.getElementById('dbase').style.display ='none';
       }
     }
     
     
   }
   
   function setSelectValue()
   {
     if(date_desc)
     {
       date_desc.value=$F('date_box');
       
       if(isIE && isIE != 10){
   		  Element.hide('date_panel');
   		}else{
   			document.getElementById('date_panel').style.display='none';
  		}   
     }
   }
   
   function showDateSelectBox(srcobj)
   {
       //if(event.button==2)
       //{
          date_desc=srcobj;
    	   /* 19/3/23 浏览器兼容 ie下 常用查询-编辑-下一步 日期型不显示弹窗
          if(isIE && isIE != 10){
   			Element.hide('date_panel');
   		  }else{
   			   */
   			document.getElementById('date_panel').style.display='block';
  		  //}   
          var pos=getAbsPosition(srcobj);
      with($('date_panel'))
      {
            style.position="absolute";
            if(navigator.appName.indexOf("Microsoft")!= -1){
                style.posLeft=pos[0]-1;
                style.posTop=pos[1]-1+srcobj.offsetHeight;
            }else{
                style.left=pos[0]+"px";
                style.top=pos[1]+srcobj.offsetHeight+"px";
            }
            style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
          }                 
       //}
   }
   
    function submitCond1()
    {
        <logic:equal name="commonQueryForm" property="query_type" value="1">
           commonQueryForm.expression.value="";
        </logic:equal>
        commonQueryForm.action = "/general/query/common/common_query.do?b_save=link&define=1";
        commonQueryForm.submit();
    }   
    function submitCond()
    {//邓灿修改
        <logic:equal name="commonQueryForm" property="query_type" value="1">
            var n=1;
            var _str="1";
            var expressionObjs_self=document.getElementsByName('expression')[0].value;
            expressionObjs_self=replaceAll(expressionObjs_self, "+", "\*" );        
            var temps=expressionObjs_self.split("\*")
            for(var i=0;i<document.commonQueryForm.elements.length;i++)
            {
                if(document.commonQueryForm.elements[i].id&&document.commonQueryForm.elements[i].id.indexOf("log")!=-1&&document.commonQueryForm.elements[i].type=='select-one')
                {
                    n++;
                    _str+=document.commonQueryForm.elements[i].value+n;                             
                }       
            }
           var expression=_str;
        </logic:equal>
        <logic:notEqual name="commonQueryForm" property="query_type" value="1">
            var  expressionObjs=document.getElementsByName('expression');
            var expressionObj=expressionObjs[0];
            var expression=expressionObj.value;
        </logic:notEqual>
       var hashvo=new ParameterSet();
       hashvo.setValue("expression",expression);
       //var vosId= document.getElementsByName('hz');
       //支持ext代码文本框获取数据  wangb 20180302
       var vosId=[];
       var count=0;
       var inputs = document.getElementsByTagName('input');
       for(var i =0 ; i <inputs.length ; i++){
       		if(!(inputs[i].getAttribute('type') == 'hidden' || inputs[i].getAttribute('type') == 'text'))
       			continue;
           if(inputs[i].getAttribute('type') == 'hidden' && inputs[i].getAttribute('name').indexOf('factorlist')>-1){
               vosId[count]=inputs[i];
			   count++;       
			   continue;    
           }
           if(inputs[i].getAttribute('type') == 'text' && inputs[i].getAttribute('name').indexOf('factorlist')>-1 && inputs[i].getAttribute('name').substring(inputs[i].getAttribute('name').length-3,inputs[i].getAttribute('name').length) == '.hz'){
        	   vosId[count]=inputs[i];
           	   count++;
           	   continue;
           }
           if(inputs[i].getAttribute('type') == 'text' && inputs[i].getAttribute('name').indexOf('factorlist')>-1 && inputs[i].getAttribute('name').substring(inputs[i].getAttribute('name').length-6,inputs[i].getAttribute('name').length) == '.value'){
        	   vosId[count]=inputs[i];
           	   count++;
           	   continue;
           }
       }
       var vosoper= document.getElementsByName('oper');
       var vosFieldname=document.getElementsByName("itemid");  
       <logic:equal name="commonQueryForm" property="query_type" value="1">   
          var vosLog=document.getElementsByName("log");       
       </logic:equal>      
       var arr=new Array();
          // alert(vosId.length+":"+vosoper.length+":"+vosFieldname.length);
       if(vosId&&vosoper&&vosFieldname&&vosFieldname.length==vosId.length&&vosId.length==vosoper.length)
       {
           for(var r=0;r<vosId.length;r++)
           {
                  var objId=vosId[r];
                  var objfieldname=vosFieldname[r];               
                  var value=objId.value;
                  var fieldname=objfieldname.value;
                  var log="";
                  <logic:equal name="commonQueryForm" property="query_type" value="1">
                    if(r!=0)
                    {
                        var oolog=vosLog[r-1];                     
                        if(!oolog)
                          break;
                        for(var i=0;i<oolog.options.length;i++)
                        {
                          if(oolog.options[i].selected)
                          {
                            log=oolog.options[i].value;                           
                            break;
                          }
                        } 
                    }
                  </logic:equal>   
                  var objOper=vosoper[r];   
                  var oper="";
                  for(var i=0;i<objOper.options.length;i++)
                  {
                      if(objOper.options[i].selected)
                      {
                         oper=objOper.options[i].value;
                         break;
                      }
                  }     
                               
                  var oobj=new Object();
                  oobj.value=value;
                  oobj.oper=oper;
                  oobj.fieldname=fieldname;   
                  oobj.log=log;             
                  arr[r]=oobj;                  
           }  
       }    
       hashvo.setValue("type","${commonQueryForm.type}");
       hashvo.setValue("arr",arr); 
       var  request=new Request({onSuccess:showSelect,functionId:'0202011007'},hashvo);
    } 
    function showSelect(outparamters)
    {
       alert("校验成功！");
	   document.getElementById('buttonOk').style.color = '';
       var expr=outparamters.getValue("expr");
       var  objs=document.getElementsByName('expr');
       var obj=objs[0];
       obj.value=expr;
       //19/3/18 xus ie非兼容模式  确定按钮校验后没有样式 bug
       document.getElementById('buttonOk').removeAttribute("disabled");
    }
    function getCond(){ 
        if(navigator.appName.indexOf("Microsoft")!= -1){
        	if(parent.parent.extOpenStrExpression)//关闭弹窗方法  wangb 20190318
        		parent.parent.extOpenStrExpression($('expr').value);
        	else if(parent.parent.Ext){
        		 if(parent.parent.Ext.getCmp('simple_query')){
  					var win = parent.parent.Ext.getCmp('simple_query');
  					win.strExpression = $('expr').value;
  					win.close();
  		    	 }
        	}else{
        		window.returnValue=$('expr').value;         
           		window.close();
        	}
             
       }else{
        	if(parent.parent.extOpenStrExpression)//关闭弹窗方法  wangb 20190318
        		parent.parent.extOpenStrExpression($('expr').value);
        	else if(parent.parent.Ext){
        		if(parent.parent.Ext.getCmp('simple_query')){
  					var win = parent.parent.Ext.getCmp('simple_query');
  					win.strExpression = $('expr').value;
  					win.close();
  		    	}
        	}else{
        		top.returnValue=$('expr').value;           
           		 top.close();
        	}
       }        
    }
    
    function isHaveDbPre(info)
    {
        var pre=$F('dbpre');
        if(pre.length==0&&info=="1")
        {
            alert("未选择查询人员库！");
            
        }
        else{
        
    //var expression = document.getElementById("expression").value;
    var expression = document.getElementsByName("expression")[0].value;
    if(expression==null||expression.length<1){
        return;
    }
    var size ="<%=num%>";
    var hashvo=new ParameterSet();
    hashvo.setValue("expression",getDecodeStr(expression));
    hashvo.setValue("size",size);
    //27725 linbz 20170520传入查询类型参数
    hashvo.setValue("query_type","${commonQueryForm.query_type}");
    var request=new Request({method:'post',asynchronous:false,onSuccess:check_ok,functionId:'0202011017'},hashvo);  
    }       
            
    }
    function check_ok(outparameters)
{
   var info=outparameters.getValue("info");
   if(info=='')
   {
     commonQueryForm.action="/general/query/common/common_query.do?b_query=query";
     commonQueryForm.submit();
    
   }
   else
   {//27725 如果不为空弹出提示信息框
       alert(info);
       return ;
   }
}
function getTrueValue(index){
	var elements =  document.getElementsByName("factorlist["+index+"].value");
	//var v = elements[index].value;
	var v = elements.value;
	if(v != null && v.length>0 && (v.indexOf('UN')==0 || v.indexOf('UM')==0 || v.indexOf('@K')==0)){
	    v = v.substring(2);
	}
	elements.value = v;
}
</script>

<body scroll=no>
<base id="mybase" target="_self">
<html:form action="/general/query/common/common_query"> 
<html:hidden property="expr"/>  
<html:hidden property="setidpiv"/>
<html:hidden property="setdescpiv"/>  
<%if("hcm".equals(bosflag)){ %>
  <table width="590" border="0" cellpadding="0" cellspacing="0" align="center" >
<%}else{ %>
  <table width="590" border="0" cellpadding="0" cellspacing="0" align="center" >
<%} %> 
          <tr>
            <!--  td width=1 valign="top" class="tableft1"></td>
            <logic:equal name="commonQueryForm" property="query_type" value="1">
                <td width=130 align=center class="tabcenter">&nbsp;<bean:message key="label.query.hquery"/>&nbsp;</td>
                </logic:equal> 
            <logic:equal name="commonQueryForm" property="query_type" value="2">
                <td width=130 align=center class="tabcenter">&nbsp;<bean:message key="label.query.cquery"/>&nbsp;</td>
                </logic:equal>
            <logic:equal name="commonQueryForm" property="query_type" value="3">
                <td width=130 align=center class="tabcenter">&nbsp;<bean:message key="label.sys.cond"/>&nbsp;</td>
                </logic:equal>                                                      
            <td width=10 valign="top" class="tabright"></td>
            <td valign="top" class="tabremain" width="700"></td-->  
             <td align="left" colspan="4" class="TableRow">
             <logic:equal name="commonQueryForm" property="query_type" value="1">
              <bean:message key="label.query.hquery"/>
             </logic:equal> 
            <logic:equal name="commonQueryForm" property="query_type" value="2">
                <bean:message key="label.query.cquery"/>
                </logic:equal>
            <logic:equal name="commonQueryForm" property="query_type" value="3">
                <bean:message key="label.sys.cond"/>
                </logic:equal>  &nbsp;</td>                       
          </tr> 
          <tr>
            <td colspan="4" class="framestyle3">
               <table border="0"  cellspacing="0" width="100%" class="ListTable"  cellpadding="0" align="center">
               	<tr >
               		<td height="3" ></td>
               	</tr>
					<logic:equal name="commonQueryForm" property="type" value="1">
						<tr id="dbase">
							<td align="right" nowrap class="tdFontcolor">
								<bean:message key="label.query.dbpre" />
							</td>
							<td align="left" nowrap class="tdFontcolor">
								<logic:iterate id="element" name="commonQueryForm" property="dblist" indexId="index">
									<html:multibox name="commonQueryForm" property="dbpre" value="${element.dataValue}" />
									<bean:write name="element" property="dataName" filter="true" />
									<%
										++j;
										if (j % 4 == 0) {
									%>
									<br>
									<%
										}
									%>
								</logic:iterate>
							</td>
						</tr>
					</logic:equal>
					<tr>
					<td colspan="4" align="center" style="padding-left: 2px;"><!-- 【7849】员工管理-登记表-查询（页面有问题，中间多了一条线）  jingq upd 2015.03.07 -->
					<div class="common_border_color" style="overflow:auto;height:300px;width:expression(document.body.clientWidth-32);border: 1px solid;"><!-- modify by xiaoyun 2014-9-1 --> 
                      <table id="datatable"   cellspacing="0" width="100%" class="ListTable" cellpadding="0" align="center">
                      <tr>
                         <logic:equal name="commonQueryForm" property="query_type" value="1">                       
                          <td align="center" nowrap class="TableRow" style="border-left:none;border-top:none;"><bean:message key="label.query.logic"/></td>
                         </logic:equal> 
                         <logic:notEqual name="commonQueryForm" property="query_type" value="1">    
                          <td align="center" nowrap class="TableRow" style="border-left:none;border-top:none;"><bean:message key="label.query.number"/></td>
                         </logic:notEqual>                                                    
                          <td align="center" nowrap class="TableRow" style="border-top: none;"><bean:message key="label.query.field"/></td>
                          <td align="center" nowrap class="TableRow" style="border-top: none;"><bean:message key="label.query.relation"/></td>
                          <td align="center" nowrap class="TableRow" style="border-top: none;border-right: none;"><bean:message key="label.query.value"/></td>
                      </tr> 
                        <logic:equal name="commonQueryForm" property="define" value="1">         
                      <logic:iterate id="element" name="commonQueryForm"  property="factorlist" indexId="index"> 
                      <tr>       
                         <logic:equal name="commonQueryForm" property="query_type" value="1">    
                           <td align="center" class="RecordRow" nowrap style="border-left:none;">
                             <%
                             	if (i != 0) {
                             %>
                               <select name="log" id="${index }log" size="1">                               
                                <option value="*">并且</option>
                                <option value="+">或</option>
                               </select>
                               <script type="text/javascript">
                                    var selected="<bean:write name="commonQueryForm" property='<%="factorlist[" + index
												+ "].log"%>' />";
                                    //document.getElementById('${index }log').value=selected;
                                    var _options=document.getElementById('${index }log').options;
                                        if("*"==selected){
                                            _options[0].selected=true;
                                        }
                                        if("+"==selected){
                                            _options[1].selected=true;
                                        }
                               </script>
                             <%
                             	}
                             %>
                          </td>
                         </logic:equal>                          
                         <logic:notEqual name="commonQueryForm" property="query_type" value="1">    
                           <td align="center" class="RecordRow" nowrap style="border-left:none;border-top: none;">
                <%=i + 1%>　   
                          　</td>
                         </logic:notEqual>                          
                          <td align="center" class="RecordRow" style="border-left: none;"  nowrap >
                             <bean:write name="element" property="hz" />&nbsp;
                              <input type="hidden" name="itemid" id='itemid' value="<bean:write name="element" property="fieldname" />" >
                          </td>  
                          <td align="center" class="RecordRow" nowrap >
                               <select name="oper" id="${index }select" size="1">
                                <option value="=">=</option>
                                <option value="&gt;">&gt;</option>
                                <option value="&gt;=">&gt;=</option>
                                <option value="&lt;">&lt;</option>
                                <option value="&lt;=">&lt;=</option>
                                <option value="&lt;&gt;">&lt;&gt;</option>  
                               </select>
                               <script type="text/javascript">
                                    var selected="<bean:write name="commonQueryForm" property='<%="factorlist[" + index
												+ "].oper"%>' />";
                                    var _options=document.getElementById('${index }select').options;
                                        if("="==selected){
                                            _options[0].selected=true;
                                        }
                                        if("&gt;"==selected){
                                            _options[1].selected=true;
                                        }
                                        if("&gt;="==selected){
                                            _options[2].selected=true;
                                        }
                                        if("&lt;"==selected){
                                            _options[3].selected=true;
                                        }
                                        if("&lt;="==selected){
                                            _options[4].selected=true;
                                        }
                                        if("&lt;&gt;"==selected){
                                            _options[5].selected=true;
                                        }
                               </script>
                          </td>                                                  
                          <!--日期型 -->                            
                          <logic:equal name="element" property="fieldtype" value="D">
                            <td align="left" class="RecordRow" nowrap>                
                            <input type="text" value="<bean:write name="commonQueryForm" property='<%="factorlist[" + index
											+ "].value"%>' />" size="30" maxlength="30" name="factorlist[<%=index %>].hz" class="text4" ondblclick="showDateSelectBox(this);" />
                            </td>                           
                          </logic:equal>
                          <!--备注型 -->                              
                          <logic:equal name="element" property="fieldtype" value="M">
                            <td align="left" class="RecordRow" nowrap>                
                                <input type="text" value="<bean:write name="commonQueryForm" property='<%="factorlist[" + index
											+ "].value"%>' />" size="30" maxlength='<%="factorlist[" + index
											+ "].itemlen"%>' name="factorlist[<%=index %>].hz" class="text4" />
                            </td>                           
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="fieldtype" value="A">
                            <td align="left" class="RecordRow" style="border-left: none;"  nowrap>
                              <logic:notEqual name="element" property="codeid" value="0">
                                <input type="hidden" value="<bean:write name="commonQueryForm" property='<%="factorlist[" + index
												+ "].value"%>' />" name="factorlist[<%=index %>].value" id='<%="factorlist[" + index
												+ "].value"%>' class="text4"/>
                                <html:text name="commonQueryForm" property='<%="factorlist[" + index
												+ "].hzvalue"%>' size="30" maxlength="50" styleClass="text4" onchange="fieldcode(this,1)"/>
                                         <logic:equal name="element" property="fieldname" value="b0110">
                                         	<logic:equal name="commonQueryForm" property="type" value="2">
                                           		<!--<img src="/images/code.gif" onclick='openCodeCustomReportDialog("UM","<%="factorlist["+index+"].hzvalue"%>","<%="factorlist[" + index + "].value"%>",3,2);' align="absmiddle"/>-->
                                           		<!-- 使用代码组件控件兼容非IE浏览器 wangb 20180125 and bug 35900 代码框不显示 valuename属性值与 隐藏文本框的name属性没一致导致     wangb 20180326 -->
                       							<img src="/images/code.gif" align="absmiddle" id="factorlis<%=index %>" multiple="true" onlySelectCodeset="true" plugin="codeselector" codesetid="UM" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="factorlist[<%=index %>].value"/>
                                         	</logic:equal>
                                         	<logic:notEqual name="commonQueryForm" property="type" value="2">
                                           		<!--<img src="/images/code.gif" onclick='openCodeCustomReportDialog("UN","<%="factorlist["+index+"].hzvalue"%>","<%="factorlist[" + index + "].value"%>",1);' align="absmiddle"/>-->
                                           		<!-- 使用代码组件控件兼容非IE浏览器 wangb 20171117 and bug 35900 代码框不显示 valuename属性值与 隐藏文本框的name属性没一致导致     wangb 20180326 -->
                        						<img src="/images/code.gif" align="absmiddle" id="factorlis<%=index %>" multiple="true" onlySelectCodeset="true" plugin="codeselector" codesetid="UN" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="factorlist[<%=index %>].value"/>                                         	
                                         	</logic:notEqual> 
                                         </logic:equal> 
                                         <logic:notEqual name="element" property="fieldname" value="b0110">   
                                            <logic:equal name="element" property="fieldname" value="e0122">
                                                <logic:equal name="commonQueryForm" property="type" value="3">
                                                	<!--<img src="/images/code.gif" onclick='openCodeCustomReportDialog("UM","<%="factorlist["+index+"].hzvalue"%>","<%="factorlist[" + index + "].value"%>",1,3);' align="absmiddle"/>-->
                                                	<!-- 使用代码组件控件兼容非IE浏览器 wangb 20171117  and bug 35900 代码框不显示 valuename属性值与 隐藏文本框的name属性没一致导致     wangb 20180326 -->
                        							<img src="/images/code.gif" align="absmiddle" id="factorlis<%=index %>" multiple="true"  plugin="codeselector" codesetid="UM" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="factorlist[<%=index %>].value"/>
	                                         	</logic:equal>
	                                         	<logic:notEqual name="commonQueryForm" property="type" value="3">
	                                           		<!--<img src="/images/code.gif" onclick='openCodeCustomReportDialog("UM","<%="factorlist["+index+"].hzvalue"%>","<%="factorlist[" + index + "].value"%>",1);' align="absmiddle"/>-->
	                                           		<!-- 使用代码组件控件兼容非IE浏览器 wangb 20171117 and bug 35900 代码框不显示 valuename属性值与 隐藏文本框的name属性没一致导致     wangb 20180326  -->
                        							<img src="/images/code.gif" align="absmiddle" id="factorlis<%=index %>" multiple="true"  plugin="codeselector" codesetid="UM" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="factorlist[<%=index %>].value"/>                                         	
	                                         	</logic:notEqual>
	                                         </logic:equal>
                                            <logic:equal name="element" property="fieldname" value="e01a1"> 
                                               <!--<img src="/images/code.gif" onclick='openCodeCustomReportDialog("@K","<%="factorlist["+index+"].hzvalue"%>","<%="factorlist[" + index + "].value"%>",1);' align="absmiddle"/>-->
                                               <!-- 使用代码组件控件兼容非IE浏览器 wangb 20171117 and bug 35900 代码框不显示 valuename属性值与 隐藏文本框的name属性没一致导致     wangb 20180326 -->
                        						<img src="/images/code.gif" align="absmiddle" id="factorlis<%=index %>" onlySelectCodeset="true" multiple="true" plugin="codeselector" codesetid="@K" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="factorlist[<%=index %>].value"/>
                                            </logic:equal>
                                            <logic:notEqual name="element" property="fieldname" value="e0122"> 
                                            <logic:notEqual name="element" property="fieldname" value="e01a1"> 
                                                <!--<img src="/images/code.gif" onclick='openCodeCustomReportDialog("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>","<%="factorlist[" + index + "].value"%>",1);' align="absmiddle"/>-->
                                                <!-- 使用代码组件控件兼容非IE浏览器 wangb 20171117 and bug 35900 代码框不显示 valuename属性值与 隐藏文本框的name属性没一致导致     wangb 20180326 -->
                        						<img src="/images/code.gif" align="absmiddle" id="factorlis<%=index %>" onlySelectCodeset="true" multiple="true" plugin="codeselector" codesetid="${element.codeid}" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="factorlist[<%=index %>].value"/>
                                            </logic:notEqual>     
                                            </logic:notEqual>                                                                                                                           
                                         </logic:notEqual>                                    
                                
                              </logic:notEqual> 
                              <logic:equal name="element" property="codeid" value="0">
                                <input type="text" value="<bean:write name="commonQueryForm" property='<%="factorlist[" + index
												+ "].value"%>' />" size="30"  name="factorlist[<%=index %>].hz" class="text4" />
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="fieldtype" value="N">
                            <td align="left" class="RecordRow" nowrap>      
                                <html:text name="commonQueryForm" property='<%="factorlist[" + index
											+ "].value"%>' size="30" styleId="hz"  maxlength="${element.itemlen}" styleClass="text4"/>                               
                            </td>                           
                          </logic:equal>                           
                       </tr>        
                       <%
                               	++i;
                               %>                    
                       </logic:iterate>
                       </logic:equal>
                       <logic:equal name="commonQueryForm" property="define" value="0"> 
                                             <logic:iterate id="element" name="commonQueryForm"  property="factorlist" indexId="index"> 
                      <tr>       
                         <logic:equal name="commonQueryForm" property="query_type" value="1">    
                           <td align="center" class="RecordRow" style="border-left: none;"  nowrap >
                             <%
                             	if (i != 0) {
                             %>
                               <html:select name="commonQueryForm" property='<%="factorlist["
													+ index + "].log"%>' styleId="log" size="1">
                                  <html:optionsCollection property="logiclist" value="dataValue" label="dataName"/>                                  
                               </html:select>
                             <%
                             	}
                             %>
                          </td>
                         </logic:equal> 
                         <logic:notEqual name="commonQueryForm" property="query_type" value="1">    
                           <td align="center" class="RecordRow" nowrap style="border-left:none;">
                <%=i + 1%>　   
                          　</td>
                         </logic:notEqual>                          
                          <td align="center" class="RecordRow" style="border-left: none;border-top: none;"  nowrap >
                             <bean:write name="element" property="hz" />&nbsp;
                              <input type="hidden" name="feildname" id='itemid' value="<bean:write name="element" property="fieldname" />" >
                          </td>  
                          <td align="center" class="RecordRow" style="border-left: none;border-top: none;" nowrap >
                               <html:select name="commonQueryForm" property='<%="factorlist[" + index
											+ "].oper"%>' styleId="oper" size="1">
                                  <html:optionsCollection property="operlist" value="dataValue" label="dataName"/>                                   
                               </html:select>
                          </td>                                                  
                          <!--日期型 -->                            
                          <logic:equal name="element" property="fieldtype" value="D">
                            <td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>                
                <html:text name="commonQueryForm" property='<%="factorlist[" + index
											+ "].value"%>' size="30" maxlength="30" styleId="hz"  styleClass="text4" ondblclick="showDateSelectBox(this);" />
                            </td>                           
                          </logic:equal>
                          <!--备注型 -->                              
                          <logic:equal name="element" property="fieldtype" value="M">
                            <td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>                
                               <html:text name="commonQueryForm" property='<%="factorlist[" + index
											+ "].value"%>' size="30" styleId="hz"  maxlength='<%="factorlist[" + index
											+ "].itemlen"%>' styleClass="text4"/>                               
                            </td>                           
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="fieldtype" value="A">
                            <td align="left" class="RecordRow"  style="border-left: none;border-top: none;border-right: none;"   nowrap>
                              <logic:notEqual name="element" property="codeid" value="0">
                                <html:hidden name="commonQueryForm" property='<%="factorlist[" + index
												+ "].value"%>' styleId="hz"  styleClass="text4" onchange='<%="getTrueValue("+index+");" %>'/>                               
                                <html:text name="commonQueryForm" property='<%="factorlist[" + index
												+ "].hzvalue"%>' size="30" maxlength="50" styleClass="text4" onchange="fieldcode(this,1)"/>
                                         <logic:equal name="element" property="fieldname" value="b0110"> 
                                           	<logic:equal name="commonQueryForm" property="type" value="2">
                                           		<!--<img src="/images/code.gif" onclick='openCodeCustomReportDialog("UM","<%="factorlist["+index+"].hzvalue"%>","<%="factorlist[" + index + "].value"%>",3,2);' align="absmiddle"/>-->
                                           		<!-- 使用代码组件控件兼容非IE浏览器 wangb 20180125 and bug 35900 代码框不显示 valuename属性值与 隐藏文本框的name属性没一致导致     wangb 20180326 -->
                       							<img src="/images/code.gif" align="absmiddle" id="factorlis<%=index %>" multiple="true" onlySelectCodeset="true" plugin="codeselector" codesetid="UM" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="factorlist[<%=index %>].value"/>
                                         	</logic:equal>
                                         	<logic:notEqual name="commonQueryForm" property="type" value="2">
                                           		<!--<img src="/images/code.gif" onclick='openCodeCustomReportDialog("UN","<%="factorlist["+index+"].hzvalue"%>","<%="factorlist[" + index + "].value"%>",1);' align="absmiddle"/>-->
                                           		<!-- 使用代码组件控件兼容非IE浏览器 wangb 20180125 and bug 35900 代码框不显示 valuename属性值与 隐藏文本框的name属性没一致导致     wangb 20180326 -->
                       							<img src="/images/code.gif" align="absmiddle" id="factorlis<%=index %>" multiple="true" onlySelectCodeset="true" plugin="codeselector" codesetid="UN" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="factorlist[<%=index %>].value"/>                                         	
                                         	</logic:notEqual> 
                                         </logic:equal> 
                                         <logic:notEqual name="element" property="fieldname" value="b0110">   
                                            <logic:equal name="element" property="fieldname" value="e0122"> 
                                                <logic:equal name="commonQueryForm" property="type" value="3">
                                                	<!--<img src="/images/code.gif" onclick='openCodeCustomReportDialog("UM","<%="factorlist["+index+"].hzvalue"%>","<%="factorlist[" + index + "].value"%>",1,3);' align="absmiddle"/>-->
                                                	<!-- 使用代码组件控件兼容非IE浏览器 wangb 20180125 and bug 35900 代码框不显示 valuename属性值与 隐藏文本框的name属性没一致导致     wangb 20180326 -->
                       							<img src="/images/code.gif" align="absmiddle" id="factorlis<%=index %>" multiple="true" plugin="codeselector" codesetid="UM" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="factorlist[<%=index %>].value"/>
	                                         	</logic:equal>
	                                         	<logic:notEqual name="commonQueryForm" property="type" value="3">
	                                           		<!--<img src="/images/code.gif" onclick='openCodeCustomReportDialog("UM","<%="factorlist["+index+"].hzvalue"%>","<%="factorlist[" + index + "].value"%>",1);' align="absmiddle"/>-->
	                                           		<!-- 使用代码组件控件兼容非IE浏览器 wangb 20180125 and bug 35900 代码框不显示 valuename属性值与 隐藏文本框的name属性没一致导致     wangb 20180326 -->
                       							<img src="/images/code.gif" align="absmiddle" id="factorlis<%=index %>"  plugin="codeselector" multiple="true" codesetid="UM" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="factorlist[<%=index %>].value"/>                                         	
	                                         	</logic:notEqual>
                                            </logic:equal>
                                            <logic:equal name="element" property="fieldname" value="e01a1"> 
                                            <!--<img src="/images/code.gif" onclick='openCodeCustomReportDialog("@K","<%="factorlist["+index+"].hzvalue"%>","<%="factorlist[" + index + "].value"%>",1);' align="absmiddle"/>-->
                                            <!-- 使用代码组件控件兼容非IE浏览器 wangb 20180125 and bug 35900 代码框不显示 valuename属性值与 隐藏文本框的name属性没一致导致     wangb 20180326 -->
                       							<img src="/images/code.gif" align="absmiddle" id="factorlis<%=index %>" multiple="true" onlySelectCodeset="true" plugin="codeselector" codesetid="@K" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="factorlist[<%=index %>].value"/>
                                            </logic:equal>
                                            <logic:notEqual name="element" property="fieldname" value="e0122"> 
                                            <logic:notEqual name="element" property="fieldname" value="e01a1"> 
                                                <!--<img src="/images/code.gif" onclick='openCodeCustomReportDialog("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>","<%="factorlist[" + index + "].value"%>",1);' align="absmiddle"/>-->
                                                <!-- 使用代码组件控件兼容非IE浏览器 wangb 20180125 and bug 35900 代码框不显示 valuename属性值与 隐藏文本框的name属性没一致导致     wangb 20180326  -->
                       							<img src="/images/code.gif" align="absmiddle" id="factorlis<%=index %>" multiple="true"  plugin="codeselector" codesetid="${element.codeid}" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="factorlist[<%=index %>].value"/>
                                            </logic:notEqual>  
                                            </logic:notEqual>                                                                                                                             
                                         </logic:notEqual>                                    
                                
                              </logic:notEqual> 
                              <logic:equal name="element" property="codeid" value="0">
                                <html:text name="commonQueryForm" property='<%="factorlist[" + index
												+ "].value"%>' size="30" styleId="hz"  styleClass="text4"/>                               
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="fieldtype" value="N">
                            <td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>      
                                <html:text name="commonQueryForm" property='<%="factorlist[" + index
											+ "].value"%>' size="30" styleId="hz"  maxlength="${element.itemlen}" styleClass="text4"/>                               
                            </td>                           
                          </logic:equal>                           
                       </tr>        
                       <%
                               	++i;
                               %>                    
                       </logic:iterate>
                       </logic:equal>
                       
                                              
                      <logic:notEqual name="commonQueryForm" property="query_type" value="1">
                       <tr style="border-collapse: none;">
                       <td align="left" colspan="4" nowrap style="padding-left: 3px;">
                         <span><bean:message key="label.query.expression"/></span><br>
                             <html:textarea property="expression" rows="3" cols="90"/>
                          <!--    <textarea id="expression"  rows="3" cols="90" onchange="javascript:document.getElementById('buttonOk').disabled = true;"></textarea>   --> 
                        </td>
                       </tr>
                       <tr>
                       	<td colspan="4" style="padding-left: 3px;padding-top: 3px;">
                       	 <input type="button" value="&nbsp;(&nbsp;&nbsp;"
													onclick="symbol('expression','(');" class="mybutton">
												<input type="button" value="&nbsp;且&nbsp;"
													onclick="symbol('expression','*');" class="mybutton">
												<input type="button" value="&nbsp;非&nbsp;"
													onclick="symbol('expression','!');" class="mybutton">
												<input type="button" value="&nbsp;&nbsp;)&nbsp;"
													onclick="symbol('expression',')');" class="mybutton">
												<input type="button" value="&nbsp;或&nbsp;"
													onclick="symbol('expression','+');" class="mybutton">
                       	</td>
                       </tr>                             
                      </logic:notEqual>
                      <logic:equal name="commonQueryForm" property="query_type" value="1">
                        <html:hidden name="commonQueryForm" property='expression'/>  
                      </logic:equal>
                      </table>
                      </div>
                      <!--预警设置要求查询条件设置界面 简单查询不显示模糊查询、历史记录查询、查询结果选项，如果影响其他地方请过来商讨  jingq upd 2014.10.20
                      <logic:notEqual name="commonQueryForm" property="queryflag" value="1">
                      </logic:notEqual>
                      
                                                                    员工管理-常用查询：编辑查询条件时也不需要显示模糊查询、历史记录查询、查询结果选项
                                                                    用define参数同样也可以控制预警设置中查询条件设置界面不显示  ，如果有页面需要显示这三项请设置define参数的值  chenxg 2018-05-04
                       -->
                      <logic:equal name="commonQueryForm" property="define" value="0">                        
                      <table>
                      	 <tr>
                            <td align="center" nowrap colspan="4">
                                <html:checkbox name="commonQueryForm" property="like" value="1"><bean:message key="label.query.like"/></html:checkbox>
					          <%
					          	if (status == 0) {
					          %>                               
                                <html:checkbox name="commonQueryForm" property="result" value="1"><bean:message key="hmuster.label.search_result"/></html:checkbox>
			                  <%
			                  	}
			                  %>                                 
                                <html:checkbox name="commonQueryForm" property="history" value="1"><bean:message key="label.query.history"/></html:checkbox>           
                            </td>
                        </tr> 
                      </table>
                      </logic:equal>    
                      </td>
                      </tr>
                      <tr>
                      	<td>
                      		<logic:equal name="commonQueryForm" property="define" value="1"> 
							   <bean:message key="label.description" />:<bean:message key="label.query.desc" />
							</logic:equal>
                      	</td>
                      </tr>                                      
           </table> 
           
              <script>
              
                //调整table边线。css调整比较麻烦
                   var table = document.getElementById("datatable");
                   for(var i = 0;i<table.rows.length;i++){
                	   var row = table.rows[i];
                	   var lastCell = row.cells[row.cells.length-1];
                	   lastCell.style.borderWidth="1 0 1 1px";
                   }
              
              </script>                
            </td>
          </tr>
          <tr class="list3">
            <td colspan="4" align="center" height="35px;">
              <logic:equal name="commonQueryForm" property="define" value="0">          
                 <html:button styleClass="mybutton"  property="b_query" onclick=" isHaveDbPre('${commonQueryForm.type}');">
                    <bean:message key="button.query"/>
                 </html:button>
              </logic:equal>                 
              <logic:equal name="commonQueryForm" property="define" value="1">          
                 <html:button styleClass="mybutton"  property="b_save" onclick="submitCond();">

                   校验&保存

                 </html:button>    
                  <html:button styleId="buttonOk"  styleClass="mybutton"  property="b_save" onclick="getCond();" disabled="true">
                    <bean:message key="button.ok"/>
                 </html:button>                    
              </logic:equal>                 
                 <hrms:submit styleClass="mybutton" property="br_return">
                    <bean:message key="button.query.pre"/>
             </hrms:submit>              
            </td>
          </tr>  
  </table>
                     <div id="date_panel">
            <select name="date_box" multiple="multiple" size="10"  style="width:200" onchange="setSelectValue();" onclick="setSelectValue();">
                <option value="$AGE_Y[10]">年份差</option>                 
                <option value="$WORKAGE[10]">工龄</option>                    
                <option value="$YRS[10]">年限</option>
                <option value="当年">当年</option>
                <option value="当月">当月</option>
                <option value="当天">当天</option>                      
                <option value="今天">今天</option>
                <option value="截止日期">截止日期</option>
                <option value="1992.4.12">1992.4.12</option>    
                <option value="1992.4">1992.4</option>  
                <option value="1992">1992</option>              
                <option value="????.??.12">????.??.12</option>
                <option value="????.4.12">????.4.12</option>
                <option value="????.4">????.4</option>                                          
                        </select>
                    </div>
</html:form>
<script language="javascript">
   if(isIE && isIE != 10){
   		Element.hide('date_panel');
   }else{
   		document.getElementById('date_panel').style.display='none';
   }
   
   hideDbase('${commonQueryForm.type}');
   
   <% if(dbpre!=null&&dbpre.length>0){
            for(int e=0;e<dbpre.length;e++)
            {
                String a_dbpre=dbpre[e];
                if(a_dbpre!=null&&a_dbpre.length()>0){
    %>
                selectedDbpreBox('<%=a_dbpre%>');       
    <%      
                }
            }
        }
     %>
   
   //让XXX人员库复选框自动选中
   function selectedDbpreBox(a_value)
   {
        var objs=document.getElementsByName("dbpre");
        if(objs)
        {
            for(var j=0;j<objs.length;j++)
            {
                if(objs[j].value.toUpperCase()==a_value.toUpperCase())
                {
                    objs[j].selected=true;
                }
            }
        }
   }
   function symbol(editor,strexpr){
	/*if(document.getElementById(editor).pos!=null){
		if(document.getElementById(editor).pos.text.length>0){
			document.getElementById(editor).pos.text+=strexpr;
		}else{
			document.getElementById(editor).pos.text=strexpr;
		}
	}else{
		document.getElementById(editor).value +=strexpr;
	}*/
	//xuj update 2011-5-26
			//var expr_editor=document.getElementById(editor);
			var expr_editor=document.getElementsByName(editor)[0];// name属性不能当作id方式获取元素    bug 35029  wangb 20180302
			//alert(expr_editor.value);
		    expr_editor.focus();
			var element = document.selection;
			if (element&&element!=null) 
			{
			  var rge = element.createRange();
			  if (rge!=null)	
			  	  rge.text=strexpr;
			}else{
				var word = expr_editor.value;
				var _length=strexpr.length;
				var startP = expr_editor.selectionStart;
				var endP = expr_editor.selectionEnd;
				var ddd=word.substring(0,startP)+strexpr+word.substring(endP);
		    	expr_editor.value=ddd;
        		expr_editor.setSelectionRange(startP+_length,startP+_length); 
			}
} 
if(!getBrowseVersion() || getBrowseVersion() ==10){ // bug 34806 禁用按钮 样式没改变    wangb 20180209 and ie11不见兼容视图样式修改 wang 20190307
	var buttonOK = document.getElementById('buttonOk');
	buttonOK.style.color = '#C5C5C5';
	buttonOK.disabled='disabled';
	var common_border_color = document.getElementsByClassName('common_border_color')[0];
	//19/3/27 xus ie非兼容性视图浏览器兼容 ie非兼容模式 常用查询-编辑 通用查询页面 无滚动条bug
	common_border_color.style.overflowX='hidden';
	common_border_color.style.overflow='auto';
} 

</script>

</body>