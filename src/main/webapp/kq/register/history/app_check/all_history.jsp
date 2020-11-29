<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
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
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.kq.app_check_in.AppForm" %>
<%
	boolean isSuper = false;
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	if (userView.isSuper_admin())
		isSuper = true;
%>
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
	int i = 0;
	int r = 0;
%>
<script language="javascript">
   function changes(str)
   {
      if(str=="1")
      {
        //Element.allselect('kqitem');
      }

      if(str=="2")
      {
        //Element.allselect('spflag');      	
      }     
      var falg=true;
      if(!validate(eval("document.appForm.start_date"),"起始日期"))
      {
         return false;
      }
      if(!validate(eval("document.appForm.end_date"),"结束日期"))
      {
         return false;
      }
      appForm.action="/kq/register/history/app_check.do?b_search=link&wo="+str+"&select_flag=1&dotflag=1&jump=link";
      appForm.submit();
   }

   function change_print()
   {
      var returnURL = getEncodeStr("${appForm.returnURL}");
      var urlstr = "/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=81&relatTableid=${appForm.relatTableid}&kqtable=${appForm.table}&closeWindow=1";
      	urlstr+="&returnURL="+returnURL;
     window.showModalDialog(urlstr,1, 
        "dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes");
   }
   function selectKq()
   {
		var winFeatures = "dialogWidth:715px; dialogHeight:375px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes"
		var table = '${appForm.table}';
		if('Q11_arc' == table){
			table = 'q11';
		}else if('Q13_arc' == table){
			table = 'q13';
		}else if('Q15_arc' == table){
			table = 'q15';
		}
		var target_url = "/kq/query/searchfiled.do?b_init=link`table="+ table;
		var iframe_url = "/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
		var return_vo= window.showModalDialog(iframe_url,1,winFeatures);
      	if(return_vo){
      	 	appForm.action ="/kq/register/history/app_check.do?b_search=link&select_flag=2&selectResult="+$URL.encode(return_vo);
      	 	appForm.submit();
      	}
   }
   function changeDb()
   {
      appForm.action="/kq/register/history/app_check.do?b_search=link";
      appForm.submit();
   }   
   
   //增加全部选项
function changelocations(obj)
{
	var obj = obj.value;
	var str = "3";
	if(obj==0||obj==1)
	{
		value1.style.display="block";
		return;
	}else
	{
		value1.style.display="none";
		appForm.action="/kq/register/history/app_check.do?b_search=link&wo="+str+"&select_flag=1&dotflag=1";
       	appForm.submit();
	}
}
function change(obj)
{
	var obj = obj;
	if(obj==0||obj==1)
	{
		value1.style.display="block";
		return;
	}else
	{
		value1.style.display="none";
		return;
	}
}

function initvalue()
{
	var ss = document.getElementById("va").value
	change(ss);
}
function openappstatistics(nbase,a0100)
{
   var target_url="/kq/register/history/view_app.do?b_statistics=link&a0100="+a0100+"&nbase="+nbase;
        return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:440px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
}
</script>

<iframe name="mysearchframe" style="display: none;"></iframe>
<html:form action="/kq/register/history/app_check">
    <logic:equal name="appForm" property="sign" value="8"> 
   	    <font color=#ff000><bean:write name="appForm" property="message"/></font>
 	</logic:equal>
 	<logic:notEqual name="appForm" property="sign" value="8"> 
 	</logic:notEqual>
<html:hidden name="appForm" property="returnURL" styleClass="text"/>
<html:hidden name="appForm" property="sp_result" styleId="sp_result" styleClass="text"/>
<html:hidden name="appForm" property="bflag" styleId="bflag" styleClass="text"/>
<table width="99%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow" style="border: solid 1px;margin-right: 3px;">
  	 <thead>
     <tr height="25">
     	<td align="left"  class="RecordRow" nowrap colspan="${appForm.cols}">
     	 <table>
     	 	<tr>
     	 	<td nowrap >
         <html:select name="appForm" property="select_pre" styleId="select_pre" size="1" onchange="changeDb();">
                <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
          </html:select>         
        <bean:message key="kq.app.byitem"/>
	        <html:select name="appForm" property="showtype" size="1" onchange="changes('1');">
				<html:optionsCollection property="showtypelist" value="dataValue" label="dataName"/>                
            </html:select>&nbsp; 
	    <bean:message key="label.by.spflag"/>
	        <html:select name="appForm" property="sp_flag" size="1" onchange="changes('2');">
                <html:optionsCollection property="splist" value="dataValue" label="dataName"/>	        
            </html:select>&nbsp; 
             姓名
            <input type="text" name="select_name" value="${appForm.select_name}" class="inputtext" style="width:100px;font-size:10pt;text-align:left">			
            <bean:message key="label.by.time.domain"/>
            <html:select name="appForm" styleId="va" property="select_time_type"  size="1" onchange="changelocations(this);">
                      <html:option value="2">全部显示</html:option>
            		  <html:option value="0">按起止时间</html:option>                      
                      <html:option value="1">按申请日期</html:option>
           </html:select>
           </td>
           <td nowrap>
           <div id="value1" style="display:none;table-layout: inherit">
		 	<bean:message key="label.from"/>
   	  	 	<input type="text" name="start_date" value="${appForm.start_date}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor1" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>
   	  	 	<bean:message key="label.to"/>
   	  	 	<input type="text" name="end_date"  value="${appForm.end_date}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor2" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>
         </div>
         </td>
         <td nowrap>
   	  &nbsp;<input type="button" name="br_return" value='查询' class="mybutton" onclick="changes('3');">          
         </td>
         </tr>
         </table>
     	</td>
     </tr>
     <tr>
       <logic:equal name="appForm" property="table" value="Q15_arc">
       <hrms:priv func_id="270530201,0C3470201"> 
       	<td align="center" class="TableRow" nowrap>
	     <bean:message key="label.view"/>      	
        </td>
        </hrms:priv>
       </logic:equal>
       <logic:equal name="appForm" property="table" value="Q11_arc">
       <hrms:priv func_id="270530101,0C3470101"> 
       	<td align="center" class="TableRow" nowrap>
	     <bean:message key="label.view"/>      	
        </td>
        </hrms:priv>
       </logic:equal>
       <logic:equal name="appForm" property="table" value="Q13_arc">
       <hrms:priv func_id="270530301,0C3470301"> 
       	<td align="center" class="TableRow" nowrap>
	     <bean:message key="label.view"/>      	
        </td>
        </hrms:priv>
       </logic:equal>
       <logic:equal name="appForm" property="table" value="Q15_arc">
          <td align="center" class="TableRow" nowrap>
           销假标识	
          </td>
       </logic:equal>
      <logic:iterate id="element" name="appForm"  property="searchfieldlist" indexId="index">
         <logic:equal name="element" property="visible" value="true">
            <td align="center" class="TableRow" nowrap>
                <bean:write name="element" property="itemdesc" filter="true"/>&nbsp;
            </td>
         </logic:equal>    
      </logic:iterate>
           
    </tr>
  </thead>
    <hrms:paginationdb id="element" pagerows="${appForm.pagerows}" name="appForm" sql_str="appForm.sql_str" table=""  where_str="appForm.cond_str" order_by="${appForm.orderby}" columns="${appForm.columns}" page_id="pagination"  indexes="indexes">
          <%
          		AppForm appForm = (AppForm) request.getSession().getAttribute(
          		"appForm");
          		String table = appForm.getTable();
          		LazyDynaBean abean = (LazyDynaBean) pageContext
          		.getAttribute("element");
          		String id;
          		String classidtoname;
          		Object obj = abean.get(table.toLowerCase() + "04");
          		obj = obj != null ? obj : "";
          		classidtoname = String.valueOf(obj);
          		String z5 = (String) abean.get(table.toLowerCase() + "z5");

          		// 开始时间
          		String z1 = (String) abean.get(table.toLowerCase() + "z1");

          		if (i % 2 == 0) {
          %>
          <tr class="trShallow">  
          <%
            } else {
            %>
          <tr class="trDeep">
          <%
          		}
          		i++;
          %>  
		         <bean:define id="nbase1" name="element" property="nbase"/>
		         <bean:define id="a01001" name="element" property="a0100"/>
               <logic:equal name="appForm" property="table" value="Q11_arc">
                 <hrms:priv func_id="270530101,0C3470101">
	             <bean:define id="q11011" name="element" property="q1101"/>
		         <%
		         		//参数加密
		    		     String str1 = "bill_id=" + q11011 + "&dbpre=" + nbase1 + "&a0100=" + a01001;
		         %>
                  <td align="center" class="RecordRow" nowrap> 
                   <a href="/kq/register/history/view_app.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str1) %>"><img src="/images/view.gif" border=0></a>
                  </td>
                  </hrms:priv>
               </logic:equal> 
               <logic:equal name="appForm" property="table" value="Q13_arc">
                 <hrms:priv func_id="270530301,0C3470301"> 
                 <bean:define id="q13011" name="element" property="q1301"/>
		         <%
		         		//参数加密
		    		     String str2 = "bill_id="+q13011+"&dbpre="+nbase1+"&a0100="+a01001;
		         %>
                  <td align="center" class="RecordRow" nowrap> 
                   <a href="/kq/register/history/view_app.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str2) %>"><img src="/images/view.gif" border=0></a>
                  </td>
                 </hrms:priv>
               </logic:equal> 
               <logic:equal name="appForm" property="table" value="Q15_arc">     
                  <hrms:priv func_id="270530201,0C3470201">
                   <bean:define id="q15011" name="element" property="q1501"/>
		         <%
		         		//参数加密
		    		     String str3 = "bill_id=" + q15011 + "&dbpre=" + nbase1 + "&a0100=" + a01001;
		         %>
                   <td align="center" class="RecordRow" nowrap>  
                    <a href="/kq/register/history/view_app.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str3) %>"><img src="/images/view.gif" border=0></a>
                   </td>
                  </hrms:priv>
               </logic:equal> 
            
            <logic:equal name="appForm" property="table" value="Q15_arc">
             <%
             		abean = (LazyDynaBean) pageContext.getAttribute("element");
             		id = (String) abean.get("q1501");
             %>
                 <td align="center"  class="RecordRow"  nowrap>
                 	<hrms:taghissell id='<%=id%>' >
                 	
                 	</hrms:taghissell>
                 </td>
              </logic:equal>
				<logic:iterate id="fielditem" name="appForm"
					property="searchfieldlist" indexId="index">
					<logic:equal name="fielditem" property="visible" value="true">

						<logic:notEqual name="fielditem" property="itemtype" value="D">
							<logic:notEqual name="fielditem" property="codesetid" value="0">
								<logic:empty name="fielditem" property="codesetid">
									<logic:equal name="fielditem" property="itemtype" value="N">
										<td align="left" class="RecordRow" nowrap>
											<%
													abean = (LazyDynaBean) pageContext.getAttribute("element");
													id = (String) abean.get("q1104");
											%>
		                                &nbsp;<hrms:kqclassname classid="<%=id%>"></hrms:kqclassname>&nbsp;
										</td>
									</logic:equal>
								</logic:empty>


								<logic:notEmpty name="fielditem" property="codesetid">
									<td align="left" class="RecordRow" nowrap>
										<logic:notEqual name="fielditem" property="itemid"
											value="q1104">
											<logic:notEqual name="fielditem" property="itemid" value="e0122">
												<hrms:codetoname codeid="${fielditem.codesetid}"
													name="element" codevalue="${fielditem.itemid}"
													codeitem="codeitem" scope="page" />  	      
		                           				&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;   
		                          			</logic:notEqual>
										</logic:notEqual>
										<logic:equal name="fielditem" property="itemid" value="q1104">
											<%
													abean = (LazyDynaBean) pageContext.getAttribute("element");
													id = (String) abean.get("q1104");
											%>
		                                &nbsp;<hrms:kqclassname classid="<%=id%>"></hrms:kqclassname>&nbsp;
		                          </logic:equal>
										<logic:equal name="fielditem" property="itemid" value="e0122">
											<hrms:codetoname codeid="${fielditem.codesetid}"
												name="element" codevalue="${fielditem.itemid}"
												codeitem="codeitem" scope="page"
												uplevel="${appForm.uplevel}" />  	      
                              			&nbsp;<bean:write name="codeitem"
												property="codename" />&nbsp;  
                             		<html:hidden name="element" property="${fielditem.itemid}" />
										</logic:equal>
									</td>
								</logic:notEmpty>
							</logic:notEqual>
							<logic:equal name="fielditem" property="codesetid" value="0">

								<logic:equal name="fielditem" property="itemid" value="q1104">
									<%
													abean = (LazyDynaBean) pageContext
													.getAttribute("element");
											id = (String) abean.get("q1104");
									%>
									<td align="left" class="RecordRow" nowrap>
										&nbsp;
										<hrms:kqclassname classid="<%=id%>"></hrms:kqclassname>
										&nbsp;ddddddd
									</td>
								</logic:equal>
								<logic:notEqual name="fielditem" property="itemid" value="q1104">
									<logic:equal name="appForm" property="visi"
										value="${fielditem.itemid}">
										<td align="left" class="RecordRow" nowrap width="200">
											&nbsp;
											<bean:write name="element" property="${fielditem.itemid}"
												filter="false" />
											&nbsp;
										</td>
									</logic:equal>
									<logic:notEqual name="appForm" property="visi"
										value="${fielditem.itemid}">
										<td align="left" class="RecordRow" nowrap>
											<logic:equal name="appForm" property="table" value="Q15_arc">
												<logic:notEqual name="fielditem" property="itemid"
													value="a0101"> 
                          	        			&nbsp;<bean:write name="element"
														property="${fielditem.itemid}" filter="false" />&nbsp;                 
                          	     			</logic:notEqual>
												<logic:equal name="fielditem" property="itemid"
													value="a0101">
														<bean:define id="nbase2" name="element" property="nbase"/>
			        									 <bean:define id="a01002" name="element" property="a0100"/>
			        									 <%
			        									 	 String nbase3 = PubFunc.encrypt(nbase2.toString());
			        										 String a01003 = PubFunc.encrypt(a01002.toString());
			        									 %>
													<a href="###"
														onclick="openappstatistics('<%=nbase3 %>','<%=a01003 %>');">
														&nbsp;<bean:write name="element"
															property="${fielditem.itemid}" filter="false" />&nbsp; </a>
												</logic:equal>
											</logic:equal>
											<logic:notEqual name="appForm" property="table"
												value="Q15_arc">
                          	    			&nbsp;<bean:write name="element"
													property="${fielditem.itemid}" filter="false" />&nbsp;                 
                          	 			</logic:notEqual>


										</td>
									</logic:notEqual>
								</logic:notEqual>
							</logic:equal>

						</logic:notEqual>
						<logic:equal name="fielditem" property="itemtype" value="D">
							<td align="left" class="RecordRow" nowrap>
								&nbsp;
								<bean:write name="element" property="${fielditem.itemid}"
									filter="false" />
								&nbsp;
							</td>
						</logic:equal>

					</logic:equal>
				</logic:iterate>
			</tr>
          <%
          r++;
          %>
    </hrms:paginationdb>
    <tr>
      <td colspan="${appForm.cols}">
        <table  width="100%" align="center" class="RecordRowP" style="border: 0px;">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
					<hrms:paginationtag name="appForm" pagerows="${appForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
			</td>
	               <td align="right" nowrap class="tdFontcolor">
		          <hrms:paginationdblink name="appForm" property="pagination" nameId="appForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
        </table>
      </td>
    </tr>
</table>
<table  width="90%" align="left">
       <tr>
         <td align="center" nowrap="nowrap">
       <logic:equal name="appForm" property="table" value="Q11_arc">
	     <hrms:priv func_id="270530102,0C3470102" module_id="">
	     <input type="button" name="br_return" value='条件查询' class="mybutton" onclick="selectKq();">
	     </hrms:priv>
              <hrms:priv func_id="270530103,0C3470103" module_id="">	
               <logic:equal name="appForm" property="sortid" value="1">
                <input type="button" name="br_return" value='打印名册' class="mybutton" onclick="change_print();"> 
               </logic:equal>
              </hrms:priv>
          </logic:equal>  
          <logic:equal name="appForm" property="table" value="Q13_arc">
	       <hrms:priv func_id="270530302,0C3470302" module_id="">
	       <input type="button" name="br_return" value='条件查询' class="mybutton" onclick="selectKq();">
	       </hrms:priv>
          <hrms:priv func_id="270530303,0C3470303" module_id="">	
              <logic:equal name="appForm" property="sortid" value="1">
                <input type="button" name="br_return" value='打印名册' class="mybutton" onclick="change_print();"> 
              </logic:equal>
            </hrms:priv>
          </logic:equal>
          <logic:equal name="appForm" property="table" value="Q15_arc">
	      <hrms:priv func_id="270530202,0C3470202" module_id="">  
	      <input type="button" name="br_return" value='条件查询' class="mybutton" onclick="selectKq();">
	      </hrms:priv>
        <hrms:priv func_id="270530203,0C3470203" module_id="">	
               <logic:equal name="appForm" property="sortid" value="1">
                <input type="button" name="br_return" value='打印名册' class="mybutton" onclick="change_print();"> 
               </logic:equal>
          </hrms:priv>
         </logic:equal>        	
         <logic:equal value="dxt" name="appForm" property="returnvalue"> 
           <hrms:tipwizardbutton flag="workrest" target="il_body" formname="appForm"/> 
         </logic:equal>
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
        initvalue();
        hide_nbase_select('select_pre');
</script>
