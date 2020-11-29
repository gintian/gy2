<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
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
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="/kq/kq.js"></script>
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
	 var start = $F('start_date');
	 var end = $F('end_date');
	 
	 if(!isDate(start,"yyyy.MM.dd"))
     {
        alert("起始日期格式不正确,请输入正确的时间格式！\nyyyy.MM.dd");
        return false;
     }
	 if(!isDate(end,"yyyy.MM.dd"))
     {
        alert("结束日期格式不正确,请输入正确的时间格式！\nyyyy.MM.dd");
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
      exchangeClassForm.action="/kq/kqself/exchange_class/exchangedata.do?b_search=link&frist_flag="+str;
      exchangeClassForm.submit();
 }
 function exchange_app()
 {
     var target_url;
     var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
     target_url="/kq/kqself/exchange_class/app_exchange.do?b_app=link&target=rr";
     newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=200,left=320,width=506,height=450');
 }
  function exchange_view(id)
 {
     var target_url;
     var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
     target_url="/kq/kqself/exchange_class/app_exchange.do?b_view=link&target=rr&id="+id;
     newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=200,left=320,width=506,height=530');
 }
  function exchange_edit(id)
 {
     var target_url;
     var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
     target_url="/kq/kqself/exchange_class/app_exchange.do?b_edit=link&target=rr&id="+id;
     newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=200,left=320,width=506,height=450');
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
           exchangeClassForm.action="/kq/kqself/exchange_class/exchangedata.do?b_select=link";
           exchangeClassForm.submit();
    	   target_url="/kq/kqself/exchange_class/exchangedata.do?b_choose=link&audit_flag="+flag;
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
  	 var len=document.exchangeClassForm.elements.length;
     var i;
     var isCorrect=false;
     for (i=0;i<len;i++)
     {
          if(document.exchangeClassForm.elements[i].type=="checkbox")
          {
              if(document.exchangeClassForm.elements[i].checked==true)
              {
                isCorrect=true;
                break;
              }
          }
     }
     if(isCorrect)
     {
    	if(confirm("确定要删除吗?"))
    	{
      		exchangeClassForm.action="/kq/kqself/exchange_class/exchangedata.do?b_delete=link&table=q19";
      		exchangeClassForm.submit();
    	}else
    	{
      		return false;
      	}
     }else
     {
     	alert("请先选择人员！");
        return false;
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
  function approves()
   {
        exchangeClassForm.action="/kq/kqself/exchange_class/exchangedata.do?b_approve=link&table=q19";
        exchangeClassForm.submit();
   }
</script>
<html:form action="/kq/kqself/exchange_class/exchangedata">
<table width="100%" border="0" cellspacing="0"  cellpadding="0" align="center" style="margin-top:3px;">
 <tr class="">
    <td align= "left" nowrap >
   		<span id="datepnl">
	 		<bean:message key="label.from"/>
  	  	 	<input type="text" name="start_date" value="${exchangeClassForm.start_date}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor1" 
onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);">
  	  	 	<bean:message key="label.to"/>
  	  	 	<input type="text" name="end_date"  value="${exchangeClassForm.end_date}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor2" 
onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);">
  	  	 </span>   
   	  	 	<input type="button" name="br_return" value='<bean:message key="button.query"/>' class="mybutton" onclick="changes('3');"> 
         <span id="all_b">
           <!--<input type="button" name="br_return" value='<bean:message key="button.allview"/>' class="mybutton" onclick="changes('0');"> -->
         </span>
           &nbsp;<hrms:kqcourse/>
   </td> 
</tr>
<tr>
  <td>
  	<div class="fixedDiv2" style="margin-top: 2px;">
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
  <thead>
    <tr>
      <td align="center" class="TableRow" style="border-top:none;border-left: none;" nowrap>
		<input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
      </td>  
      <logic:iterate id="element" name="exchangeClassForm"  property="fieldlist" indexId="index">
         <logic:equal name="element" property="visible" value="true">
            <td align="center" class="TableRow" style="border-top:none;" nowrap>
                <bean:write name="element" property="itemdesc" filter="true"/>&nbsp;
            </td>
         </logic:equal>    
      </logic:iterate>      
      <td align="center" class="TableRow" style="border-top:none;" nowrap>
	    <bean:message key="label.edit"/>            	
      </td>
      <td align="center" class="TableRow" style="border-top:none;border-right: none;" nowrap>
	    <bean:message key="kq.strut.more"/>            	
      </td>  
    </tr>  
  </thead>
    <hrms:paginationdb id="element" name="exchangeClassForm" sql_str="exchangeClassForm.sql" table="" where_str="" columns="${exchangeClassForm.column}" page_id="pagination"  indexes="indexes">
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
          <td align="center" class="RecordRow" style="border-top:none;border-left: none;" nowrap>
          	<hrms:kqdurationjudge startDate="<%=z1 %>" endDate="<%=z3 %>">
             <logic:match name="element" property="q19z5" value="01">
               <hrms:checkmultibox name="exchangeClassForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
          	 </logic:match>
          	 <logic:match name="element" property="q19z5" value="07">
          	 	<hrms:checkmultibox name="exchangeClassForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
          	 </logic:match>
          	 </hrms:kqdurationjudge>
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
          <bean:define id="q19011" name='element' property="q1901"/>
          <%
          	//参数加密
          	String q19012=PubFunc.encrypt(q19011.toString());
          %>
          <td align="center" class="RecordRow" nowrap>
          <hrms:kqdurationjudge startDate="<%=z1 %>" endDate="<%=z3 %>">
          <logic:equal name="element" property="q19z5" value="01"> 
             <a href="###" onclick="exchange_edit('<%=q19012 %>');"><img src="/images/edit.gif" border=0></a>
          </logic:equal>
           <logic:equal name="element" property="q19z5" value="07"> 
             <a href="###" onclick="exchange_edit('<%=q19012 %>');"><img src="/images/edit.gif" border=0></a>
          </logic:equal>
          </hrms:kqdurationjudge>
          </td>  
          <td align="center" class="RecordRow" style="border-top:none;border-right: none;" nowrap>
                 <a href="###" onclick="exchange_view('<%=q19012 %>');"><img src="/images/view.gif" border=0></a>
           </td>     
         </tr>
    </hrms:paginationdb>
</table>    
</div>
<div style="*width:expression(document.body.clientWidth-10);">
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="exchangeClassForm" property="pagination" nameId="exchangeClassForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table></div>
<table  width="80%" align="center">
       <tr>
         <td align="center">
         
         <input type="button" name="overrule" value='<bean:message key="button.app"/>' class="mybutton" onclick="exchange_app();">
         <input type="button" name="br_return" value='<bean:message key="button.appeal"/>' class="mybutton" onclick="approves()"> 
         <!--<input type="button" name="b_delete" value='<bean:message key="label.query.selectall"/>' class="mybutton" onclick="selAll()"> -->
         <input type="button" name="overrule" value="<bean:message key="button.delete"/>" class="mybutton" onclick="delete_app();">
        
         </td>
      </tr>          
</table>
  </td>
</tr>
</table>

</html:form>
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