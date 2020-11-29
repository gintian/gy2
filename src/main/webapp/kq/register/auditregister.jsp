<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.register.BrowseRegisterForm" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="java.util.HashMap" %>
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
<script language="javascript" src="/js/validate.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
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
<html:form action="/kq/register/audit_registerdata">
<SCRIPT LANGUAGE="JavaScript">
  var checkflag = "false";
  function selAll()
  {
      browseRegisterForm.action="/kq/register/audit_registerdata.do?b_selectall=link&code=${browseRegisterForm.code}&kind=${browseRegisterForm.kind}";
      browseRegisterForm.submit(); 
  } 
  
</script>
<script language="javascript">
function checkRadio(){
	var len=document.browseRegisterForm.elements.length;
	var i;
	for (i=0;i<len;i++)
		{
			if (document.browseRegisterForm.elements[i].type=="checkbox")
			{
				if(document.browseRegisterForm.elements[i].checked){
						return true;
				}
           	}
        }
   return false;
}
	function change()
   {
      browseRegisterForm.action="/kq/register/audit_registerdata.do?b_query=link&code=${browseRegisterForm.code}&kind=${browseRegisterForm.kind}";
      browseRegisterForm.submit();
   }   
   function return_overrule()
   {
       var len=document.browseRegisterForm.elements.length;
       var isCorrect=false;
       for (i=0;i<len;i++)
       {
           if (document.browseRegisterForm.elements[i].type=="checkbox")
            {
              if( document.browseRegisterForm.elements[i].checked==true)
                isCorrect=true;
            }
       }
       if(!isCorrect)
       {
          alert("请选择人员");
          return false;
       }else{
    	  	var data = "${browseRegisterForm.coursedate}";
   			if(confirm("是否要驳回考勤期间"+data+"的已报批数据？"))
     		{
   				var a=0; 
   	            var b=0;       
   	            var selectid=new Array();
   	            for (i=0;i<len;i++)
   	            {
   	               if(document.browseRegisterForm.elements[i].type=="checkbox")
   	               {
   	                   if(document.browseRegisterForm.elements[i].checked==false&&document.browseRegisterForm.elements[i].name!="aa")
   	                   {
   	                     selectid[a++]=document.getElementById("IDs_"+b).value; 
   	                   }
   	                   b++;
   	               }             
   	           } 
   	           var hashvo=new ParameterSet();
   		       hashvo.setValue("idlist",selectid);	
   		       hashvo.setValue("table","Q05");
   		       var request=new Request({method:'post',asynchronous:true,functionId:'1510010006'},hashvo);
     		}else
     		{
     			return false;
     		}
       }
      var target_url;
      var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
      target_url="/kq/register/audit_registerdata.do?br_saveover=link&sb=new";
      return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:no;status:no;scrollbars:yes");
      if(!return_vo)
        return false;	
      if(return_vo.save=="1")
      {
           var overrule=return_vo.text;
           var o_obj=document.getElementById('overrule');
           o_obj.value=overrule;
           browseRegisterForm.action="/kq/register/audit_registerdata.do?b_overrule=link";
           browseRegisterForm.target="mil_body";
           browseRegisterForm.submit();
      }
   }
   function writeOverrule(userbase,a0100,kq_duration)
   {
       
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
       target_url="/kq/register/browse_registerdata.do?b_searrule=link&userbase="+userbase+"&a0100="+a0100+"&kq_duration="+kq_duration;
       newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
	  
   }
   function selectKq()
   {
	   var winFeatures = "dialogWidth:738px; dialogHeight:375px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes"
	    var target_url = "/kq/query/searchfiled.do?b_init=link`table=q03";
	    if($URL)
	    	target_url = $URL.encode(target_url);
	    var iframe_url = "/general/query/common/iframe_query.jsp?src="+target_url;
	    var return_vo= window.showModalDialog(iframe_url,1,winFeatures);
	    if(return_vo){
	    	var o_obj=document.getElementById('selectResult');
	           o_obj.value=getEncodeStr(return_vo);
	           browseRegisterForm.action="/kq/register/audit_registerdata.do?b_query=link&select_flag=1";
	           browseRegisterForm.submit();
	    }
   } 
   function audit_data()
   {
	   var len=document.browseRegisterForm.elements.length;
       var num = 0;
       for (i=0;i<len;i++)
       {
           if (document.browseRegisterForm.elements[i].type=="checkbox")
            {
                num++;
            }
       }
		if(num <= 1)
		{
			alert("没有数据无法操作！ ");
			return false;
		}
		var data = "${browseRegisterForm.coursedate}";
		if(confirm("是否要审核考勤期间"+data+"的已报审数据？"))
  		{
			if(!app_opinion())
		        return false;
		      var waitInfo=eval("wait");	   
		      waitInfo.style.display="block";
		      browseRegisterForm.action="/kq/register/audit_registerdata.do?b_audit=link";
		      browseRegisterForm.submit();
  		}else
  		{
  			return false;
  		}
	  
   }   	
    function go_search()
   {
      browseRegisterForm.action="/kq/register/search_register.do?b_search=link&action=search_registerdata.do&target=mil_body";
      browseRegisterForm.target="il_body";
      browseRegisterForm.submit();
   }
   function approve_data()
   {
	   var len=document.browseRegisterForm.elements.length;
       var num = 0;
       for (i=0;i<len;i++)
       {
           if (document.browseRegisterForm.elements[i].type=="checkbox")
            {
                num++;
            }
       }
		if(num <= 1)
		{
			alert("没有数据无法操作！ ");
			return false;
		}
		var data = "${browseRegisterForm.coursedate}";
		if(confirm("是否要批准考勤期间"+data+"的已报批数据？"))
  		{
			if(!app_opinion())
		        return false;
		      var waitInfo=eval("wait");	   
		      waitInfo.style.display="block";
		      browseRegisterForm.action="/kq/register/audit_registerdata.do?b_approve=link";
		      browseRegisterForm.submit();
  		}else{
  			return false;
  	  	}
   }
     function sing_approve()
   {
      browseRegisterForm.action="/kq/register/sing_oper/singapprovedata.do?b_approve=link&flag=2";
      browseRegisterForm.submit();
   }   
   function selectflag()
   {
      browseRegisterForm.action="/kq/register/audit_registerdata.do?b_query=link&select_flag=1";
      browseRegisterForm.submit();
   } 	
   function viewAll()
   {
      browseRegisterForm.action="/kq/register/audit_registerdata.do?b_query=link&select_flag=0&select_name=";
      browseRegisterForm.submit();
   }    
   function quitRe()
   {
      window.location="/kq/register/daily_register.do?br_quit=link";
   }
    function MusterInitData()
   {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
   }
   function app_opinion()
   {
      var target_url="/kq/register/select_collectdata.do?br_appopin=link";
      var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:no;status:no;scrollbars:yes");
      if(return_vo==null)
       return false;
      if(return_vo.flag!="true")
        return false;
      var fObj=document.getElementById("overrule");
      if(fObj!=null)
      {
        fObj.value=return_vo.overrule;        
      }
       return true;
   }
   var checkflag = "false";
   function selAll1()
  {
      var len=document.browseRegisterForm.elements.length;
       var i;
       
     if(checkflag=="false")
     {
        for (i=0;i<len;i++)
        {
           if (document.browseRegisterForm.elements[i].type=="checkbox")
            {
              document.browseRegisterForm.elements[i].checked=true;
            }
         }
        checkflag="true";
     }else
     {
        for (i=0;i<len;i++)
        {
          if (document.browseRegisterForm.elements[i].type=="checkbox")
          {
            document.browseRegisterForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    } 
        
  }
function changeys(dd)
{
	if(dd==2){
 		browseRegisterForm.action="/kq/register/audit_registerdata.do?b_search=link&selectys=2&code=${browseRegisterForm.code}&kind=${browseRegisterForm.kind}";
    	browseRegisterForm.submit();
 	}else if(dd==1){
 		browseRegisterForm.action="/kq/register/audit_registerdata.do?b_search=link&selectys=1";
    	browseRegisterForm.submit();
 	}
} 
</script><hrms:themes /> <!-- 7.0css -->
<table>
 <tr>
  <td>
  	<!--  表格固定显示 wangb 20171026 -->
   <table width="100%" style="table-layout:fixed;" border="0" cellspacing="1" cellpadding="1">
    
    <tr> 
     <td>
        <table border="0" cellspacing="0"  align="left" cellpadding="0">
            <tr>
            
           <td align="left">
           <table border="0" cellspacing="0"  align="left" cellpadding="0"><tr><td>
            <hrms:menubar menu="menu2" id="menubar1" target="mil_body">  
              <hrms:menuitem name="rec" label="考勤待审" function_id="270206,0C32">
              <hrms:menuitem name="mitem2" label="审核" icon="/images/sort.gif" url="javascript:audit_data();" command="" function_id="270221,0C321" />  
              <hrms:menuitem name="mitem3" label="批准" icon="/images/write.gif" url="javascript:approve_data();" function_id="2702062,0C323"/> 
              <hrms:menuitem name="mitem3" label="驳回" icon="/images/sort.gif" url="javascript:return_overrule();" command=""  function_id="270222,0C322"/>   
                       
              </hrms:menuitem>              
              <hrms:menuitem name="recc" label="查询考勤" function_id="2702012,0C3114">
              <hrms:menuitem name="mitem1" label="查询" icon="/images/quick_query.gif" url="javascript:selectKq();" function_id="2702012,0C3114"/>       
              </hrms:menuitem>  
           </hrms:menubar>
           </td></tr></table>
        </td>        
      </tr>
    </table>
       </td> 
       <logic:equal name="browseRegisterForm" property="select_flag" value="1">
        <td align= "left" nowrap>
	          &nbsp; <html:button styleClass="mybutton" property="bc_btn1" onclick="viewAll();"><bean:message key="workdiary.message.view.all.infor"/></html:button>
	        </td>
	   </logic:equal>
       <td align= "left" nowrap> &nbsp;
           按<html:select name="browseRegisterForm" property="select_type"  size="1">
            	<html:option value="0"><bean:message key="label.title.name" /></html:option>                      
                <html:option value="1">工号</html:option>
                <html:option value="2">考勤卡号</html:option>
           </html:select>
           <input type="text" name="select_name" value="${browseRegisterForm.select_name}" class="inputtext" style="width:100px;font-size:10pt;text-align:left">
           &nbsp;<button extra="button" onclick="javascript:selectflag();">查找</button>            
      </td>

       <td align= "left" nowrap> &nbsp;
           <html:select name="browseRegisterForm" property="select_pre" size="1" onchange="change();">
                <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
            </html:select>
       </td>       
    <td align= "left" nowrap>&nbsp;
        <bean:message key="kq.register.daily.menu"/>
        <html:select name="browseRegisterForm" property="coursedate" size="1" onchange="javascript:change()" disabled="true">
        <html:optionsCollection property="courselist" value="dataValue" label="dataName"/>
        </html:select> 
        <html:hidden name="browseRegisterForm" property="code" styleClass="text"/>
        <html:hidden name="browseRegisterForm" property="kind" styleClass="text"/>    
        <html:hidden name="browseRegisterForm" property="overrule" styleId="overrule" styleClass="text"/>    
        <html:hidden name="browseRegisterForm" property="selectResult" styleId="selectResult" styleClass="text"/>                
      </td> 
<!-- 
       <td align= "left" nowrap> &nbsp;
           时间显示方式
           <logic:notEqual name="browseRegisterForm" property="selectys" value="2">
      		<select size="1"   name="selectysf"   onchange="changeys(this.value);">
      		 	<option   value="1">默认</option>   
      		 	<option   value="2">HH:mm</option> 
      		</select>
      		</logic:notEqual>
      		<logic:equal name="browseRegisterForm" property="selectys" value="2">
      		<select size="1"   name="selectysf"   onchange="changeys(this.value);">
      		 	<option   value="2">HH:mm</option> 
      		 	<option   value="1">默认</option>   
      		</select>
      		</logic:equal>             
      </td>      
   -->
      <td>
        &nbsp;&nbsp;<hrms:kqcourse/>
      </td> 
    </tr>
    </table>
  </td>
 </tr>
 <tr>
  <td>
 <script language='javascript' >
        document.write("<div id=\"tbl-container\"  style='overflow:auto;BORDER-BOTTOM: #94B6E6 1pt solid; BORDER-LEFT: #94B6E6 1pt solid; BORDER-RIGHT: #94B6E6 1pt solid; BORDER-TOP: #94B6E6 1pt solid ;height:400px;*height:"+(document.body.clientHeight-140)+";width:100%'>");
 </script> 
 <%int i=0;
   String name=null;
   int num_s=0;
 %>
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
      <thead>
         <tr>
	         <td align="center" class="TableRow" style="border-top: none;border-left: none;" nowrap>
			       <input type="checkbox" name="aa" value="true" onclick="selAll1();">&nbsp;
	         </td>  
           <logic:iterate id="element"    name="browseRegisterForm"  property="fielditemlist" indexId="index"> 
               <logic:equal name="element" property="visible" value="true">
                 <td align="center" class="TableRow" style="border-top: none;border-right: none;" nowrap>
                  <bean:write  name="element" property="itemdesc"/>&nbsp; 
                 </td>
               </logic:equal>
           </logic:iterate>
         </tr>
      </thead>      
      <hrms:paginationdb id="element" name="browseRegisterForm" sql_str="browseRegisterForm.sqlstr" table="" where_str="browseRegisterForm.strwhere" columns="browseRegisterForm.columns" order_by="browseRegisterForm.orderby" pagerows="${browseRegisterForm.pagerows}" page_id="pagination" indexes="indexes">
          <%
          LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
          if(i%2==0){ 
          %>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          <%
          }else{
          %>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'DDEAFE')">
          <%}i++; 
           
          %> 
            <td align="center" class="RecordRow" style="border-left: none;" nowrap>
              <logic:equal name="element" property="state" value="1">
                 <hrms:checkmultibox name="browseRegisterForm" property="pagination.select" value="false" indexes="indexes"/>&nbsp;
              </logic:equal>
              <logic:notEqual name="element" property="state" value="1">
                 <hrms:checkmultibox name="browseRegisterForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
              </logic:notEqual>
              <input type="hidden" name="IDs" id="IDs_<%=i%>" value='<bean:write name="element" property="q03z0" filter="false"/>`<bean:write name="element" property="nbase" filter="false"/>`<bean:write name="element" property="a0100" filter="false"/>' />
             </td>
             <% int  inNum=0;%> 
            <logic:iterate id="info" name="browseRegisterForm"  property="fielditemlist" indexId="index">
            <%
               BrowseRegisterForm browseRegisterForm=(BrowseRegisterForm)session.getAttribute("browseRegisterForm");
               FieldItem item=(FieldItem)pageContext.getAttribute("info");
               name=item.getItemid(); 
              %>  
              
                <logic:equal name="info" property="visible" value="false">
                  <html:hidden name="element" property="${info.itemid}"/>  
                </logic:equal>
                
                <logic:equal name="info" property="visible" value="true">
                    <!--字符型-->
                    <logic:equal name="info" property="itemtype" value="A">
                    
                         <logic:notEqual name="info" property="codesetid" value="0">
                          <td align="left" class="RecordRow" style="border-right: none;" nowrap>
                          	<% if ("e0122".equalsIgnoreCase(((FieldItem)info).getItemid())) { %>
                             <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${browseRegisterForm.uplevel}"/>  
                             <%} else { %>
                             <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	   
                             <%} %>   
                             &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
                          </td>  
                         </logic:notEqual>
                         
                         <logic:equal name="info" property="codesetid" value="0">
                          <td align="left" class="RecordRow" style="border-right: none;" nowrap>
                             <logic:notEqual name="info" property="itemid" value="a0101">
                               <logic:notEqual name="info" property="itemid" value="overrule">
                                 &nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
                               </logic:notEqual>
                             </logic:notEqual>
                             <bean:define id="nbase1" name='element' property="nbase"/>
                             <bean:define id="a01001" name='element' property="a0100"/>
                            <bean:define id="q03z01" name='element' property="q03z0"/> 
					          <%
					          	//参数加密
					          	String nbase2=PubFunc.encrypt(nbase1.toString());
					            String a01002=PubFunc.encrypt(a01001.toString());
					            String q03z02=PubFunc.encrypt(q03z01.toString());
					          %>
                             <logic:equal name="info" property="itemid" value="a0101">
                               <logic:notEqual name="info" property="itemid" value="overrule">
                                 <a href="/kq/register/browse_single.do?b_browse=link&rflag=08&code=${browseRegisterForm.code}&userbase=<%=nbase2 %>&kind=${browseRegisterForm.kind}&start_date=${browseRegisterForm.start_date}&end_date=${browseRegisterForm.end_date}&A0100=<%=a01002 %>&marker=0">
                                   &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/></a>&nbsp;
                               </logic:notEqual>
                             </logic:equal>
                             
                              <logic:equal name="info" property="itemid" value="overrule">
                               <logic:notEqual name="info" property="itemid" value="a0101">
                                &nbsp;
                                   <img src="/images/edit.gif" border="0" alt="填写审批意见" onclick="writeOverrule('<%=nbase2 %>','<%=a01002 %>','<%=q03z02 %>');">
                                  
                               </logic:notEqual>
                             </logic:equal>
                          </td> 
                        </logic:equal>
                    </logic:equal>
                    
                   <!--数字-->
                   <logic:equal name="info" property="itemtype" value="N">
                      <td align="center" class="RecordRow" style="border-right: none;" nowrap>
                       <logic:notEqual name="browseRegisterForm" property="selectys" value="2"> 
	                       <logic:greaterThan name="element" property="${info.itemid}" value="0">
	                         <bean:write name="element" property="${info.itemid}"/>	                         
	                       </logic:greaterThan>
                       </logic:notEqual>
                       
                       <logic:notEqual name="browseRegisterForm" property="selectys" value="1">
                      		<%
                         num_s++;
                         request.setAttribute("num_s",num_s+""); 
                         HashMap infoMap=(HashMap)browseRegisterForm.getKqItem_hash();
                         //out.println("d = "+abean.get(name));
                       %>
                       <hrms:kqvaluechange kqItem_hash="<%=infoMap%>" itemid="${info.itemid}" value='<%=abean.get(name)+""%>'/>
                       <%
                                inNum++;
                        %>  
                      </logic:notEqual>
                     </td>
                  </logic:equal>
                  
                  <logic:equal name="info" property="itemtype" value="D">
                   <td class="RecordRow" style="border-right: none;" nowrap>
                      <bean:write name="element" property="${info.itemid}" filter="false"/>&nbsp;
                   </td>
                  </logic:equal>
                  <logic:equal name="info" property="itemtype" value="M">
                   <td class="RecordRow" style="border-right: none;">
                      &nbsp;
                   </td>
                  </logic:equal>
                </logic:equal>                
            </logic:iterate>  
          </tr>
          
        </hrms:paginationdb>    	                           	    		        	        	        
        	                           	    		        	        	        
      </table>
      
      <script language='javascript' >
	  document.write("</div>");
    </script> 
<script language='javascript' >
        document.write("<div id=\"tbl-pagination\"  style='width:100%'>");
 </script> 
	  <table width="100%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				<hrms:paginationtag name="browseRegisterForm" pagerows="${browseRegisterForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
			</td>
	    	<td   align="right" nowrap class="tdFontcolor">
		     	<p align="right"><hrms:paginationdblink name="browseRegisterForm" property="pagination" nameId="browseRegisterForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
	   </table>  
	   <script language='javascript' >
	  document.write("</div>");
    </script> 
     <table  width="80%" align="left">       
       <tr>
          <td align="left">
            <!--<input type="button" name="b_delete" value='<bean:message key="label.query.selectall"/>' class="mybutton" onclick="selAll()">--> 
              <hrms:priv func_id="270221,0C321">
               <input type="button" name="btnreturn" value='审核' onclick="audit_data();" class="mybutton">
              </hrms:priv>
              <hrms:priv func_id="2702062,0C323">
                 <input type="button" name="btnreturn" value='批准' onclick="approve_data();" class="mybutton">
              </hrms:priv>
              <hrms:priv func_id="270222,0C322">
                <input type="button" name="btnreturn" value='驳回' onclick="return_overrule();" class="mybutton">
              </hrms:priv>
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
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>

             <td class="td_style common_background_color" height=24><bean:message key="classdata.isnow.wiat"/></td>

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
<script language="javascript">
initDocument();
MusterInitData();
hide_nbase_select('select_pre');

window.onresize = function(){
       var clientW = document.body.clientWidth;
       var clientH = document.body.clientHeight;
       
       var tblContainer = document.getElementById("tbl-container");
       var tbW = tblContainer.style.width;
       var tbH = tblContainer.style.height;
       
       var tblPagination = document.getElementById("tbl-pagination");
       if(clientW >= 800) {
          //tblContainer.style.width = clientW - 20;
          //tblPagination.style.width = clientW - 20;
       }
       
       if(clientH >= 600) {
           tblContainer.style.height = clientH - 150;
       }
   };
</script> 
