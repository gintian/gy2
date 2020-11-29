<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<link href="/css/hcm/themes/default/content.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
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
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/validateDate.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="/kq/kq.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<hrms:themes /> <!-- 7.0css -->
<script language="javascript">
    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>
<%
	int i=0;
%>
<script language="JavaScript">
 function changes(str)
 {
      if(!validate(eval("document.exchangeClassForm.start_date"),"起始日期"))
      {
         return false;
      }
      if(!validate(eval("document.exchangeClassForm.end_date"),"结束日期"))
      {
         return false;
      }
    //szk验证时间 
      var dd = eval("document.exchangeClassForm.start_date");
   	  var ks = dd.value;
   	  var jsd=eval("document.exchangeClassForm.end_date");
   	  var js = jsd.value;
   	  ks=replaceAll(ks,"-",".");
   	  js=replaceAll(js,"-",".");
   	  if(ks>js)
   	  {
   	  	alert(KQ_CHECK_TIME_HINT);
   	  	return false;
   	  }
      exchangeClassForm.action="/kq/app_check_in/exchange_class/exchangedata.do?b_search=link&frist_flag="+str;
      exchangeClassForm.submit();
 }
 function exchange_app()
 {
 	var dh="646px";
 	var wh="620px";
	if(navigator.appVersion.indexOf('MSIE 6') != -1){
		dh="646px";
		wh="616px";
	}
     var target_url;
     var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
     target_url="/kq/app_check_in/exchange_class/app_exchange.do?b_app=link`target=rr";
     if($URL)
         target_url = $URL.encode(target_url);
     var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
     var return_vo= window.showModalDialog(iframe_url,1,"dialogWidth:"+wh+"; dialogHeight:"+dh+";resizable:no;center:yes;scroll:no;status:no;scrollbars:no");  
     //newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=100,left=220,width=606,height=500');
      if(return_vo&&return_vo.flag=="true")
      {
            exchangeClassForm.action= "/kq/app_check_in/exchange_class/exchangedata.do?b_search=link"
            exchangeClassForm.submit();
      }
 }
  function exchange_view(id)
 {
     var target_url;
     var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
     target_url="/kq/app_check_in/exchange_class/app_exchange.do?b_view=link`target=rr`id="+id;
     if($URL)
         target_url = $URL.encode(target_url);
     var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
     var return_vo= window.showModalDialog(iframe_url,1,"dialogWidth:616px; dialogHeight:646px;resizable:no;center:yes;scroll:no;status:no;scrollbars:no");  
     if(return_vo&&return_vo.flag=="true")
      {
            exchangeClassForm.action= "/kq/app_check_in/exchange_class/exchangedata.do?b_search=link"
            exchangeClassForm.submit();
      }
 }
  function exchange_edit(id)
 {
     var target_url;
     var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
     target_url="/kq/app_check_in/exchange_class/app_exchange.do?b_edit=link`target=rr`id="+id;
     if($URL)
         target_url = $URL.encode(target_url);
     var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
     var return_vo= window.showModalDialog(iframe_url,1,"dialogWidth:616px; dialogHeight:646px;resizable:no;center:yes;scroll:no;status:no;scrollbars:no");  
     //newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=200,left=320,width=506,height=406');
     if(return_vo&&return_vo.flag=="true")
      {
            exchangeClassForm.action= "/kq/app_check_in/exchange_class/exchangedata.do?b_search=link"
            exchangeClassForm.submit();
      }
 }
  function exchange_approve(id)
 {
	 var dh="654px";
	 var wh="616px";
	 if(navigator.appVersion.indexOf('MSIE 6') != -1){
 		 dh="654px";
		 wh="616px";
	 }
     var target_url;
     var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
     target_url="/kq/app_check_in/exchange_class/app_exchange.do?b_approve=link`target=rr`id="+id;
     if($URL)
         target_url = $URL.encode(target_url);
     var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
     var return_vo= window.showModalDialog(iframe_url,1,"dialogWidth:"+wh+"; dialogHeight:"+dh+";resizable:no;center:yes;scroll:no;status:no;scrollbars:no"); 
     if(return_vo&&return_vo.flag=="true")
     {
            exchangeClassForm.action= "/kq/app_check_in/exchange_class/exchangedata.do?b_search=link"
            exchangeClassForm.submit();
     }
 }
   var checkflag = "false";

 function selAll()
  {
      var len=document.exchangeClassForm.elements.length;
       var i;
       
     if(checkflag=="false")
     {
        for (i=0;i<len;i++)
        {
           if (document.exchangeClassForm.elements[i].type=="checkbox")
            {
              document.exchangeClassForm.elements[i].checked=true;
            }
         }
        checkflag="true";
     }else
     {
        for (i=0;i<len;i++)
        {
          if (document.exchangeClassForm.elements[i].type=="checkbox")
          {
            document.exchangeClassForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    } 
        
  } 
   function adds(flag)
  {
     var len=document.exchangeClassForm.elements.length;
     var uu;
      for (i=0;i<len;i++)
        {
           if (document.exchangeClassForm.elements[i].type=="checkbox")
            {
              if(document.exchangeClassForm.elements[i].checked==true)
              {
                uu="dd";
               
               }
            }
         }
        if(uu=="dd")
       {
           exchangeClassForm.action="/kq/app_check_in/exchange_class/exchangedata.do?b_select=link";
           exchangeClassForm.submit();
    	   target_url="/kq/app_check_in/exchange_class/exchangedata.do?b_choose=link&audit_flag="+flag;
    	    newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=180,left=350,width=380,height=270'); 
        }else
	{
	     alert("没有选择记录！");
             return false;
	}
  }
  function update()
  {
      
       alert("该信息已批复执行不可以修改！");
     
  }
  function delete_app()
  {
  	<logic:notEqual  name="exchangeClassForm" property="approved_delete" value="1">
  		var len=document.exchangeClassForm.elements.length;
     	var del = 0;
      	for (j=0; j<len; j++) {
			if(document.exchangeClassForm.elements[j].type=="checkbox") {
				if(document.exchangeClassForm.elements[j].checked==true) {
              	var checkName = document.exchangeClassForm.elements[j].name;
              	var obj = document.getElementsByName("hidden"+checkName+"_input");
              	if (obj[0] && obj[0].value=="03") {
              		del = 1;
					break;
              	}
               
              }
          }
      	}
      	if (del == 1) {
      		alert("已批数据不能删除!");
      		return ;
      	}
     </logic:notEqual>
     var len = document.exchangeClassForm.elements.length;
     var isC = false;
     for(var i=0;i<len;i++){
         if(document.exchangeClassForm.elements[i].type=="checkbox" && document.exchangeClassForm.elements[i].name != "selbox"){
			if(document.exchangeClassForm.elements[i].checked == true)
				isC = true;	
         }
     }
     if(!isC){
		alert("请选择需要删除的记录！");
		return false;
     }
    if(confirm("确定要删除吗？"))
    {
      exchangeClassForm.action="/kq/app_check_in/exchange_class/exchangedata.do?b_delete=link&table=q19";
      exchangeClassForm.target="mil_body";
      exchangeClassForm.submit();
    }
  }
  function detect_frist()
  {
       var frist="${exchangeClassForm.frist_flag}";    
       if(frist=="3")
       {
         Element.show('all_b');
       }else
       {
         Element.hide('all_b');
       }
  }
  function change_print()
   {
       exchangeClassForm.action="/kq/app_check_in/exchange_class/exchangedata.do?b_hprint=link";
       exchangeClassForm.submit();
   }
function outPrintApp(){
      var returnURL = getEncodeStr("${exchangeClassForm.returnURL}");
      var urlstr = "/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=81&relatTableid=${exchangeClassForm.relatTableid}&closeWindow=1";
      	urlstr+="&returnURL="+returnURL;
     // window.open(urlstr);
      window.showModalDialog(urlstr,1, 
        "dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes");
}
function export_excel(){
	var target_url="/kq/app_check_in/all_app_data.do?b_excel=link&table=Q19&flag=0";
    var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:500px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
	if(return_vo != null){
		var hashvo=new ParameterSet();
		var fieldArray = return_vo.split(",");
		hashvo.setValue("fileList",fieldArray);
		hashvo.setValue("table","Q19");
		var request = new Request({method:'post',onSuccess:showExportInfo,functionId:'15301110212'},hashvo);
	}
}

function showExportInfo(outparamters){
	if(outparamters){
		var name=outparamters.getValue("name");
		window.location.target="_blank";
		window.location.href="/servlet/vfsservlet?fileid=" + name +"&fromjavafolder=true";
	}
}

function batchsubscribe(){
	var len = document.exchangeClassForm.elements.length;
	var i;
	var isCorrect = false;
	for(i=0;i<len;i++){
		if(document.exchangeClassForm.elements[i].type == "checkbox"){
			if(document.exchangeClassForm.elements[i].checked == true){
				isCorrect = true;
			}
		}
	}
	if(isCorrect){
		if(confirm("是否进行批量签批？")){
			var target_url="/kq/app_check_in/all_app_data.do?br_batchsub=link&table=Q19&opinionlength=" + ${exchangeClassForm.opinionlength};
            var return_vo= window.showModalDialog(target_url,"app", 
            "dialogWidth:300px; dialogHeight:260px;resizable:no;center:yes;scroll:yes;status:no");
            if(!return_vo)
	        	return false;
            var sp_result=return_vo.text;
 	        if(sp_result==""||sp_result=="undefined")
 	        	sp_result="";
            exchangeClassForm.action="/kq/app_check_in/exchange_class/exchangedata.do?b_batchsub=link&radio="+return_vo.radio+"&flag="+return_vo.falg+"&table=Q19&sp_result="+getEncodeStr(sp_result);
            exchangeClassForm.submit();
		}
	}else{
		alert("请选择人员！");
		return false;
	}
}
</script>

<html:form action="/kq/app_check_in/exchange_class/exchangedata">
<table width="99%" border="0" cellspacing="0"  cellpadding="0" style="padding-left: 2px">
 <tr >
    <td align= "left" nowrap style=" padding-bottom: 3px;">
       
          <bean:message key="label.by.time.domain"/><span id="datepnl">
		 	<bean:message key="label.from"/>
   	  	 	<input type="text" class="inputtext" name="start_date" value="${exchangeClassForm.start_date}" style="width:100px;font-size:10pt;text-align:left" id="editor1" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>
   	  	 	<bean:message key="label.to"/>
   	  	 	<input type="text" class="inputtext" name="end_date"  value="${exchangeClassForm.end_date}" style="width:100px;font-size:10pt;text-align:left" id="editor2" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>
   	  	 	</span>   
   	  	 	<input type="button" name="br_return" value='<bean:message key="button.query"/>' class="mybutton" onclick="changes('3');"> 
         <span id="all_b">
           <input type="button" name="br_return" value='<bean:message key="button.allview"/>' class="mybutton" onclick="changes('0');"> 
         </span>
         <html:hidden name="exchangeClassForm" property="returnURL" styleClass="text"/>
         &nbsp;&nbsp;<hrms:kqcourse/>
   </td> 
</tr>
<tr>
  <td>
  <div class="fixedDiv2 complex_border_color" style="width:100%">
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
  <thead>
    <tr>
      <td align="center" class="TableRow" style="border-top: none;border-left: none;" nowrap>

		  <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;

          
      </td>  
        <td align="center" class="TableRow" style="border-top: none;" nowrap>
	    <bean:message key="kq.strut.more"/>            	
      </td>   
        <td align="center" class="TableRow" style="border-top: none;" nowrap>
         <bean:message key="button.apply"/>            	
       </td>    
      <logic:iterate id="element" name="exchangeClassForm"  property="fieldlist" indexId="index">
         <logic:equal name="element" property="visible" value="true">
            <td align="center" class="TableRow" style="border-top: none;" nowrap>
                &nbsp;<bean:write name="element" property="itemdesc" filter="true"/>&nbsp;
            </td>
         </logic:equal>    
      </logic:iterate>      
      
    </tr>  
  </thead>
    <hrms:paginationdb id="element" pagerows="${exchangeClassForm.pagerows}" name="exchangeClassForm" sql_str="exchangeClassForm.sql" table="" where_str="" columns="${exchangeClassForm.column}" page_id="pagination"  indexes="indexes">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;
          
          LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
          String z1 = (String)abean.get("q19z1");
          String z3 = (String)abean.get("q19z3");          
          %>  
          <td align="center" class="RecordRow" style="border-left: none;" nowrap>
          	<hrms:kqdurationjudge startDate="<%=z1 %>" endDate="<%=z3 %>">
             <logic:equal name="exchangeClassForm" property="approved_delete" value="1">
               &nbsp;<hrms:checkmultibox name="exchangeClassForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
             </logic:equal> 
               <logic:notEqual  name="exchangeClassForm" property="approved_delete" value="1">
                <logic:notEqual name="element" property="q19z5" value="03"> 
                &nbsp;<hrms:checkmultibox name="exchangeClassForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
                </logic:notEqual>
                <logic:equal name="element" property="q19z5" value="03"> 
                <logic:equal name="element" property="state" value="1">
               &nbsp;<hrms:checkmultibox name="exchangeClassForm" property="pagination.select"  value="false"  indexes="indexes"/>&nbsp;
               <input type="hidden" name="hiddenpagination.select[${indexes}]_input" value="03"/>
               </logic:equal>           
               <logic:notEqual name="element" property="state" value="1">
                 &nbsp;<hrms:checkmultibox name="exchangeClassForm" property="pagination.select"  value="true" indexes="indexes"/>&nbsp;
                 <input type="hidden" name="hiddenpagination.select[${indexes}]_input" value="03"/>
                </logic:notEqual>
                </logic:equal>
              </logic:notEqual>
              </hrms:kqdurationjudge>
          </td>   
          <bean:define id="q19011" name="element" property="q1901"/>
          <%
          	String str=PubFunc.encrypt(q19011.toString());
          %>
          <td align="center" class="RecordRow" nowrap>
                 &nbsp;<a href="###" onclick="exchange_view('<%=str %>');"><img src="/images/view.gif" border=0></a>&nbsp;
          </td> 
          <td align="center" class="RecordRow" nowrap>
          <hrms:priv func_id="270134,0C3442" module_id="">
          		<hrms:kqdurationjudge startDate="<%=z1 %>" endDate="<%=z3 %>">
             <logic:equal name="element" property="q19z5" value="02"> 
                 &nbsp;<a href="###" onclick="exchange_approve('<%=str %>');"><img src="/images/edit.gif" border=0></a>&nbsp;
             </logic:equal>
             </hrms:kqdurationjudge>
          </hrms:priv> 
          </td>       
            <logic:iterate id="fielditem" name="exchangeClassForm"  property="fieldlist" indexId="index">
                <logic:equal name="fielditem" property="visible" value="true">
                   <logic:notEqual name="fielditem" property="itemtype" value="D">
                      <logic:notEqual name="fielditem" property="codesetid" value="0">
                        <td align="left" class="RecordRow" nowrap >
                           <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
                           &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;   
                          </td>                 
                        </logic:notEqual>
                        <logic:equal name="fielditem" property="codesetid" value="0">                        	
                            <td align="left" class="RecordRow" nowrap >
                               &nbsp;<bean:write name="element" property="${fielditem.itemid}"  filter="false"/>&nbsp;                 
                            </td>
                          </logic:equal>                                            
                    </logic:notEqual>
                    <logic:equal name="fielditem" property="itemtype" value="D">
                       <td align="center" class="RecordRow" nowrap>
                           &nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;   
                       </td>
                    </logic:equal>    
            </logic:equal> 
            </logic:iterate>          
                   
         </tr>
    </hrms:paginationdb>
</table>  
</div>
<tr>
<td>  
<table  width="100%" align="left" class="RecordRowTop0">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><!--  第
					<bean:write name="pagination" property="current" filter="true" />
					页
					共
					<bean:write name="pagination" property="count" filter="true" />
					条
					共
					<bean:write name="pagination" property="pages" filter="true" />
					页--><hrms:paginationtag name="exchangeClassForm" pagerows="${exchangeClassForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="exchangeClassForm" property="pagination" nameId="exchangeClassForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</td>
</tr>
<tr>
<td> 
<table  width="100%" align="left">
       <tr>
         <td align="left">
          <!-- 
             <input type="button" name="b_delete" value='<bean:message key="label.query.selectall"/>' class="mybutton" onclick="selAll()"> 
          -->
         <hrms:priv func_id="270130,0C3440" module_id="">
         <input type="button" name="overrule" value='<bean:message key="button.app"/>' class="mybutton" onclick="exchange_app();">
         </hrms:priv>
          <hrms:priv func_id="270133,0C3443" module_id="">               
               <!--<input type="button" name="b_select" value="<bean:message key="conlumn.board.approveoperation"/>" class="mybutton" onclick="adds(1)">
               <input type="button" name="overrule" value="<bean:message key="edit_report.status.dh"/>" class="mybutton" onclick="adds(2);">-->
               <input type="button" name="overrule" value="<bean:message key="button.delete"/>" class="mybutton" onclick="delete_app();">
          </hrms:priv> 
          <hrms:priv func_id="270135,0C3445" module_id="">	
          	  <logic:equal name="exchangeClassForm" property="sortid" value="1">
                <input type="button" name="br_return" value='打印' class="mybutton" onclick="outPrintApp();"> 
               </logic:equal>
          </hrms:priv>
          <hrms:priv func_id="270136,0C3446" module_id="">	
          	<input type="button" name="br_return" value='导出Excel' class="mybutton" onclick="export_excel();">
          </hrms:priv>
          <hrms:priv func_id="0C3447" module_id="">
	          <logic:notEmpty name="exchangeClassForm" property="returnvalue">
	          	<input type="button" name="" value="批量审批" class="mybutton" onclick="batchsubscribe();"/>
	          </logic:notEmpty>
          </hrms:priv>
             <logic:equal value="dxt" name="exchangeClassForm" property="returnvalue"> 
               <hrms:tipwizardbutton flag="workrest" target="il_body" formname="exchangeClassForm"/> 
             </logic:equal>
         </td>
      </tr>          
</table>
  </td>
</tr>
</table>

</html:form>
<iframe name="mysearchframe" style="display: none;"></iframe>
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
  detect_frist();
</script>