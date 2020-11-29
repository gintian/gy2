<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@page import="com.hrms.hjsj.sys.FieldItem" %>
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
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
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

<script language="javascript">
   function change()
   {
      collectStatForm.action="/general/template/goabroad/collect/searchstatdata.do?b_search=link";
      collectStatForm.submit();
   }    
   function selectCode()
   {
        var fileset=$F('fileset');
        var subset=$F('subset');        
       if(fileset!="-1")
       {
           var hashvo=new ParameterSet();
	   hashvo.setValue("fileset",fileset);
	   hashvo.setValue("subset",subset);
   	   var request=new Request({method:'post',asynchronous:false,onSuccess:showCodeList,functionId:'0570010203'},hashvo);
           var obj_stat=document.getElementsByName('select_stat');
	   obj_stat[0].value="1";
       }else
       {
          var obj_stat=document.getElementsByName('select_stat');
	  	obj_stat[0].value="0";
          closes();
          var flag = document.getElementsByName("flag")[0];
          if (flag) {
          	flag.value = "";
          }
          selectData();
       }  
   }
  
	function showCodeList(outparamters)
	{
	    var flag=outparamters.getValue("flag");
	    var childset=outparamters.getValue("childset");	    
	    var obj_td=document.getElementById('tb');
	    var td = document.getElementById("flaglist");
	    var str_td="";
	    var button_str="&nbsp;&nbsp;<span style='vertical-align: middle'><input type='button' name='select' value='查询' onclick='selectData();' class='mybutton'></span>";
	    if(flag=="3")
	    {
	    	td.style.display = "none";
	       var selecthtml=outparamters.getValue("selecthtml");	      
	       obj_td.innerHTML=selecthtml+button_str;
	    }else if(flag=="2")
	    {
	    	
	       td.style.display = "block"; 
	       document.getElementById("start_date").value='';
	       document.getElementById("end_date").value='';
	       //str_td="&nbsp;<input type='text' name='start_date' extra='editor' style='width:100px;font-size:10pt;text-align:left'   dropDown='dropDownDate'>&nbsp;";
	       //str_td=str_td+"至&nbsp;<input type='text' name='end_date' extra='editor' style='width:100px;font-size:10pt;text-align:left'  dropDown='dropDownDate'>";
	       obj_td.innerHTML=str_td+button_str;
	       
	    }else
	    {
	    	td.style.display = "none";
	      obj_td.innerHTML="&nbsp;<span style='vertical-align: middle'><input type='text' class='text4' name='childset'></span>"+button_str;
	    }
	    var obj_flag=document.getElementsByName('flag');
	    obj_flag[0].value=flag;
	    show();
	}
	function showCode()
	{
	    var td = document.getElementById("flaglist");
	    var flag=$F('flag');	   	    	       
	    var obj_td=document.getElementById('tb');	    
	    var button_str="&nbsp;&nbsp;<span style='vertical-align: middle'><input type='button' name='select' value='查询' onclick='selectData();' class='mybutton'></span>";
	    if(flag=="3")
	    {
	    	td.style.display = "none";
	       var selecthtml="${collectStatForm.selecthtml}";	      
	       obj_td.innerHTML=selecthtml+button_str;
	    }else if(flag=="2")
	    {
	       var start_date='${collectStatForm.start_date}';
	       var end_date='${collectStatForm.end_date}';
	       var td = document.getElementById("flaglist");
	       td.style.display = "block"; 
	       document.getElementById("start_date").value=start_date;
	       document.getElementById("end_date").value=end_date;
	       //str_td="&nbsp;<input type='text' name='start_date' value='"+start_date+"' extra='editor' style='width:100px;font-size:10pt;text-align:left'  dropDown='dropDownDate'>&nbsp;";
	       //str_td=str_td+"至&nbsp;<input type='text' name='end_date' value='"+end_date+"' extra='editor' style='width:100px;font-size:10pt;text-align:left'  dropDown='dropDownDatex'>";
	       obj_td.innerHTML=button_str;
	       
	    }else
	    {
	    td.style.display = "none";
	      var childset='${collectStatForm.childset}';
	      obj_td.innerHTML="&nbsp;<span style='vertical-align: middle'><input type='text' name='childset' value='"+childset+"'></span>"+button_str;
	    }	        
	}
	function show()
        {
	  var bb=eval("tb");
	  bb.style.display="block";
        }
        function closes()
        {
          var bb=eval("tb");
          bb.innerHTML="";
	  		bb.style.display="none"; 
	  		var tf = document.getElementById("flaglist")
	  		tf.style.display="none";
	  		
        }
        function selectData()
        {
        	var flag = document.getElementsByName("flag")[0];
        	if (flag.value == "2") {
        		var start = document.getElementById("start_date");
	       		var end = document.getElementById("end_date");
	       		if (vali(start, "开始时间") && vali(end, "结束时间")) {	       			
	       		} else {
	       			return ;
	       		}
        	}  
            // collectStatForm.action="/general/template/goabroad/collect/selectstat.do?b_select=link&action=selectstatdata.do&target=mil_body";
             //collectStatForm.target="il_body";
             collectStatForm.action="/general/template/goabroad/collect/searchstatdata.do?b_select=link";
             collectStatForm.submit();
        }
        
function fomateDate(obj) {
	var	reg =/^\d{4}-\d{1,2}-\d{1,2}$/;
	
	if (reg.test(obj.value)) {
		return true;
	} else {
		return false;
	}
}

function vali(obj, desc) {
	if (fomateDate(obj)) {
		if(!validate(obj,desc)){  
			return false;
		}
	} else {
		alert(desc + "格式不正确，应为yyyy-MM-dd!");
		return false;
	}
		 
	return true;
}   
   function excecuteExcel()
   {
	var hashvo=new ParameterSet();	
	hashvo.setValue("nbase","${collectStatForm.nbase}");	
	hashvo.setValue("code","${collectStatForm.code}");
	hashvo.setValue("kind","${collectStatForm.kind}");
	var In_paramters="exce=excel";	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showExcel,functionId:'0570010204'},hashvo);
	
   }
   function showExcel(outparamters)
   {
	var url=outparamters.getValue("excelfile");	
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url,"excel");
	
   }
   function go_delete()
   {
   var len=document.collectStatForm.elements.length;
     var uu;
      for (i=0;i<len;i++)
        {
           if (document.collectStatForm.elements[i].type=="checkbox")
            {
              if(document.collectStatForm.elements[i].checked==true && document.collectStatForm.elements[i].name != "selbox")
              {
                uu="dd";
               
               }
            }
         }
        if(uu=="dd")
       {
         if(confirm('<bean:message key="goabroad.collect.select.delete"/>'))
         {
             collectStatForm.action="/general/template/goabroad/collect/searchstatdata.do?b_delete=link";
             collectStatForm.submit();
         }
       }else
       {
          alert('<bean:message key="goabroad.collect.no.select"/>');
          return false;
       }
   
   }

function paixu(field){
    var pxf_div = '${collectStatForm.sort_field}';
    if(field != pxf_div){
    	f_div = document.getElementById(field);
    	//f_div.innerHTML = '▼';
    	collectStatForm.action="/general/template/goabroad/collect/searchstatdata.do?b_select=link&sort_field=" + field + "&sort_flag=1";
    } else {
    	var f_div = document.getElementById(field);
    	var f_value = f_div.innerHTML;
    	if(f_value == '▼'){
    		//f_div.innerHTML = '▲';
    		collectStatForm.action="/general/template/goabroad/collect/searchstatdata.do?b_select=link&sort_field=" + field + "&sort_flag=2";
    	}else{
    		//f_div.innerHTML = '▼';
    		collectStatForm.action="/general/template/goabroad/collect/searchstatdata.do?b_select=link&sort_field=" + field + "&sort_flag=1";
    	}
    }
    collectStatForm.submit();
}

function showMessage(srcobj){
	Element.show('message_pnl');   
    var pos=getAbsPosition(srcobj);
	with($('message_pnl')){
		style.position="absolute";
		style.posLeft= pos[0]+1;
		style.posTop= pos[1] - srcobj.offsetHeight;
		style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
     }             
}

function closeMessage(){
	Element.hide('message_pnl');
}

function selectinfo(){
       var target_url;
       target_url="/general/template/goabroad/collect/searchstat.do?b_selinit=link}";
       var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
        var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:596px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:no");      
       if(return_vo == 'ok')
      	 	window.location.href="/general/template/goabroad/collect/searchstatdata.do?b_search=link";
}

</script>
<hrms:themes cssName="content.css"></hrms:themes>
<html:form action="/general/template/goabroad/collect/searchstatdata">

<table>
 <tr>    
    <td align= "left" nowrap>
    	<table><tr>
	   	 <td align= "left" nowrap style="padding-left: 0px">
		   	 <span style="vertical-align: middle">
		        <html:select name="collectStatForm" property="nbase" size="1" onchange="javascript:change()">
		        <html:optionsCollection property="nbaselist" value="dataValue" label="dataName"/>
		        </html:select>        
		             &nbsp; 按 <html:select name="collectStatForm" property="fileset" size="1" onchange="javascript:selectCode()">
		        <html:optionsCollection property="filelist" value="dataValue" label="dataName"/>
		        </html:select> 
		        
		        <html:hidden name="collectStatForm" property="subset" styleClass="text"/>   
		        <html:hidden name="collectStatForm" property="flag" styleClass="text"/> 
		        <html:hidden name="collectStatForm" property="select_stat" styleClass="text"/>  
	        </span>                      
	      </td>  
	      <td id="flaglist" style="display:none;">
		      <span style="vertical-align: middle">
			      <input type='text' id="start_date" name='start_date' value='' extra='editor' calss="text4"  dropDown='dropDownDate'>&nbsp;
				       至&nbsp;<input type='text' id="end_date" name='end_date' value='' extra='editor' class="text4"  dropDown='dropDownDate'>
			 </span>
		  </td>
	      <td id="tb"  style="display:none;" align="left">               
	              
	      </td>
	      <td>
	      <span style="vertical-align: middle">
	      	 <input class="mybutton" type="button" onclick="selectinfo();" value="条件查询" />
	      	 </span>
	      </td>
	      </tr>
	 </table>
	 </td>  
   </tr>
 <tr>
  <td>
 <%int i=0;
 	int col = 0;
 %>
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
      <thead>
         <tr>  
         <td align="center" class="TableRow" nowrap>
		<!--<bean:message key="column.select"/>&nbsp;-->
		<input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'/>&nbsp;
         </td> 
            <logic:iterate id="element"    name="collectStatForm"  property="columnlist" indexId="index"> 
               <logic:equal name="element" property="visible" value="true">
                 <td align="center" class="TableRow" onmouseover="showMessage(this)" onmouseout="closeMessage()" ondblclick="paixu('${element.itemid}')" nowrap="nowrap">
                  <table cellpadding="0" cellspacing="0" border="0"><tr><td nowrap="nowrap"><bean:write  name="element" property="itemdesc"/>&nbsp;</td><td id="${element.itemid}"> <logic:equal name="collectStatForm" property="sort_field" value="${element.itemid}"><bean:write name="collectStatForm" property="sort_sign"/></logic:equal><%col++; %></td></tr></table> 
                 </td>
               </logic:equal>
           </logic:iterate>         	        
         </tr>
      </thead>           
      <hrms:paginationdb id="element" name="collectStatForm" sql_str="collectStatForm.strsql" table="" where_str="collectStatForm.where" columns="collectStatForm.columns" order_by="collectStatForm.orderby" pagerows="18" page_id="pagination" indexes="indexes">

          <%
          if(i%2==0){ 
          %>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          <%
          }else{
          %>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'DDEAFE')">
          <%}i++; 
           
          %> 
           <td align="center" class="RecordRow" nowrap>
               <hrms:checkmultibox name="collectStatForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
          </td>
            <logic:iterate id="info" name="collectStatForm"  property="columnlist" indexId="index">  
              <logic:equal name="info" property="visible" value="true">
             
                 <logic:notEqual name="info" property="codesetid" value="0">
                          <td align="left" class="RecordRow" nowrap>
                          		<%FieldItem fieldItem = (FieldItem) info; 
                          			if (fieldItem.getItemid().equalsIgnoreCase("e0122")) {
                          		%>
                          		<hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${collectStatForm.uplevel}"/>  
                          		<%} else { %>
                              <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  
                              <%} %>	      
                             &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                              
                          </td>  
                 </logic:notEqual>
                 <logic:equal name="info" property="codesetid" value="0">
                 	<logic:equal name="info" property="itemtype" value="D">
                 		  <td align="right" class="RecordRow" nowrap>
	                           &nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
	                      </td> 
                 	</logic:equal>
                 	<logic:notEqual name="info" property="itemtype" value="D">
                 		<logic:equal name="info" property="itemtype" value="N">
                 			<td align="right" class="RecordRow" nowrap>
	                           &nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
	                        </td> 
                 		</logic:equal>
                 		<logic:notEqual name="info" property="itemtype" value="N">
		                 	  <td align="left" class="RecordRow" nowrap>
		                           &nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
		                      </td> 
                 		</logic:notEqual>
                 	</logic:notEqual>
                  </logic:equal>
              </logic:equal>                
           </logic:iterate>  
          </tr>
          
        </hrms:paginationdb>    	                           	    		        	        	        
        <tr>
        	<td colspan="<%=col+1 %>">
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
	        <td  align="right" class="tdFontcolor">
	     <p align="right"><hrms:paginationdblink name="collectStatForm" property="pagination" nameId="collectStatForm" scope="page">
             </hrms:paginationdblink></p>
			</td>
		</tr>
</table>
</td>
        </tr>	                           	    		        	        	        
      </table>
     </td>
   </tr> 
</table>
<div style="margin-top: 5px; padding-left:3px;">
	<input type="button" class="mybutton" onclick="excecuteExcel();"
	       value='<bean:message key="goabroad.collect.educe.excel"/>'/>
	<logic:notEqual name="collectStatForm" property="collectflag" value="other">
	    <hrms:priv func_id="32121" module_id="">
		   <input type="button" class="mybutton" onclick="go_delete();"
		         value='<bean:message key="button.delete"/>'/>
		</hrms:priv>
	</logic:notEqual>
</div>

<logic:notEqual name="collectStatForm" property="select_stat" value="1">
<script language="javascript">
    closes();
 </script>   
</logic:notEqual>
<logic:equal name="collectStatForm" property="select_stat" value="1">
<script language="javascript">
    showCode();
    show();
 </script>
</logic:equal>
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
<div id="message_pnl">
		<table border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" ><tr><td class="RecordRow"><p>双击标题排序</p></td></tr></table>
</div>
<script language="javascript">
  initDocument();
  Element.hide('message_pnl');
</script>
