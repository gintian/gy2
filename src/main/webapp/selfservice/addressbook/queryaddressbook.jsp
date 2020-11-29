<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hjsj.hrms.actionform.kq.register.DailyRegisterForm" %>
<!-- 引入ext6 框架和 代码框控件   wangb 20171123 -->
<script type="text/javascript" src="/module/utils/js/template.js" ></script>
<script type="text/javascript" src="/components/codeSelector/codeSelector.js" ></script>
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js" ></script>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String date = DateStyle.getSystemDate().getDateString();
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
	//String manager=userView.getManagePrivCodeValue();
		
%>
<hrms:themes/>
<html:form action="/selfservice/addressbook/queryaddressbook.do">

<table width="80%" align="center" border="0" cellspacing="0"  align="left" cellpadding="0"> 

 <tr>
  <td align= "left" nowrap>
    <table>
      <tr>
	      <td>
	        <bean:message key="label.title.name"/>
	        <input type="text" name="select_name" class="textColorWrite" value="${addressBookConstantForm.select_name}" style="width:100px;font-size:10pt;text-align:left">
	        
	        <!-- 导出excel时用到select_name_hidden隐藏域，防止改动姓名后导出的excel不准 -->
	        <input type="hidden" name="select_name_hidden" id="select_name_hidden" value="${addressBookConstantForm.select_name}"/>
	        &nbsp;
	        <span style="vertical-align: middle;">     <button name="asva" class="mybutton" onclick="change();">查询</button>&nbsp;</span> 
	      </td>
	      <td nowrap>&nbsp;[&nbsp;
	      </td>
	      <td nowrap id="vieworhidd"> 
	         <a href="javascript:showOrClose();"> 
	                                                             查询显示
	         </a>
	      </td>                       
	      <td nowrap>&nbsp;]&nbsp;&nbsp;&nbsp;&nbsp;
	      </td>
      </tr>
    </table>
  </td> 
 </tr>
 
 <tr>
    <td>
    <%
		int flag2=0;
		int j=0;
		int n=0;
		int column=0;
    %>    
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id='aa' style='display:none'>
         <tr>
           <td>
		     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow" id="query">
		        <tr class="trShallow1">
		          <td align="center" colspan="4" height='20' class="RecordRow" nowrap>
		            <bean:message key="label.query.inforquery"/><!-- 请选择查询条件! -->
		          </td> 
		        </tr>    
		           
		        <logic:greaterThan name="addressBookConstantForm" property="dbaseCount" value="1">
				<tr>
				  <td align="right" height='28' nowrap>
				    &nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="label.dbase"/>&nbsp;
				  </td>
				  <td align="left"  nowrap><!-- 人员库 -->  
				     <html:select name="addressBookConstantForm" property="nbase" size="1" onchange="change();">
				        <html:optionsCollection property="dbaselist" value="dataValue" label="dataName"/>	        
				     </html:select>      
				  </td> 
				</tr>
				<%flag2++; %>
		       </logic:greaterThan>  
		      
		       <logic:iterate id="element" name="addressBookConstantForm"  property="fieldlist" indexId="index">            
		           <!-- 时间类型 -->
		          <logic:equal name="element" property="itemtype" value="D">
		               <% 
		                  if(flag2==0)
		                  {
		                       out.println("<tr>");
		                       flag2=1;          
		                  }else{
		                       flag2=0;           
		                  }
		               %>  
		              <td align="right" height='28' nowrap>
		                   <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
		              </td>
		              <td align="left"  nowrap >
		                  <html:text name="addressBookConstantForm" property='<%="fieldlist["+index+"].value"%>' 
		                      size="13" maxlength="10" styleClass="textColorWrite" style="width:91px"
		                      title="输入格式：2008.08.08" onblur="getDate(this.parentNode,'${index}')"/>
		                  <bean:message key="label.query.to"/>
		                  <html:text name="addressBookConstantForm" property='<%="fieldlist["+index+"].viewvalue"%>' 
		                      size="13" maxlength="10" styleClass="textColorWrite" style="width:91px"
		                      title="输入格式：2008.08.08"  onblur="getDate(this.parentNode,'${index}')"/>
					      <div id="<%="checkdate"+index%>" style="color: red"></div>
		              </td>
		              <%
		                 if(flag2==0)
		        			out.println("</tr>");
		              %>   
		          </logic:equal>
		          <logic:equal name="element" property="itemtype" value="M">
		               <% 
		                  if(flag2==0)
		                  {
		                       out.println("<tr>");
		                       flag2=1;          
		                  }else{
		                       flag2=0;           
		                  }
		              %> 
		              <td align="right" height='28' nowrap>
		                   <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
		              </td>
		              <td align="left"  nowrap>
		                  <html:text name="addressBookConstantForm" property='<%="fieldlist["+index+"].value"%>' size="31" maxlength='<%="fieldlist["+index+"].itemlength"%>' styleClass="textColorWrite"/>
		              </td>
		              <%
		                 if(flag2==0)
		        			out.println("</tr>");
		              %> 
		          </logic:equal> 
		           <logic:equal name="element" property="itemtype" value="N">   
		              <% 
		                  if(flag2==0)
		                  {
		                       out.println("<tr>");
		                       flag2=1;          
		                  }else{
		                       flag2=0;           
		                  }
		              %> 
		              <td align="right" height='28' nowrap>
		                   <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
		              </td>
		             <td align="left"  nowrap> 
		                <html:text name="addressBookConstantForm" property='<%="fieldlist["+index+"].value"%>' size="31" maxlength="${element.itemlength}" styleClass="textColorWrite"/> 
		             </td>
		              <%
		                 if(flag2==0)
		        			out.println("</tr>");
		              %> 
		              
		           </logic:equal>
		           <logic:equal name="element" property="itemtype" value="A">
		              <logic:notEqual name="element" property="codesetid" value="0">
		                  <logic:equal name="element" property="codesetid" value="UN">
		                     <%
		                       if(flag2==0)
		                       {
		                           out.println("<tr>");
		                           flag2=1;          
		                       }else{
		                            flag2=0;           
		                       }
		                      %> 
		                     <td align="right" height='28' nowrap>
		                        <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
		                     </td>
		                     <td align="left" nowrap>
		                       <html:hidden name="addressBookConstantForm" property='<%="fieldlist["+index+"].value"%>' styleClass="text"/>                               
		                       <html:text name="addressBookConstantForm" property='<%="fieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="textColorWrite" onchange="fieldcode(this,2);"/>
		                       <logic:equal name="element" property="itemid" value="b0110"> 
		                            <!--改用代码框控件 img 标签   wangb 20171123-->
		                            <img align="absmiddle" src="/images/code.gif" plugin='codeselector' onlySelectCodeset='true' codesetid='UN' nmodule='4' ctrltype='3' inputname='<%="fieldlist["+index+"].viewvalue"%>' valuename='<%="fieldlist["+index+"].value"%>'/>
		                       </logic:equal> 
		                       <logic:notEqual name="element" property="itemid" value="b0110">                                         
		                            <!-- 改用代码框控件 img 标签   wangb 20171123 and 代码选择控件没有居中显示 添加 align="absmiddle" bug 34374  20180131 参数 -->
		                     		<img align="absmiddle" src="/images/code.gif" plugin='codeselector' codesetid='${element.codesetid}' inputname='<%="fieldlist["+index+"].viewvalue"%>' valuename='<%="fieldlist["+index+"].value"%>'/>
		                      </logic:notEqual>   
		                    </td>
		                     <%
		                       if(flag2==0)
		        	               out.println("</tr>");
		                     %>                                  
		                   </logic:equal>                          
		                   <logic:equal name="element" property="codesetid" value="UM">
		                       <%
		                       if(flag2==0)
		                       {
		                           out.println("<tr>");
		                           flag2=1;          
		                       }else{
		                            flag2=0;           
		                       }
		                      %>  
		                      <td align="right" height='28' nowrap>
		                        <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
		                      </td>
		                      <td align="left" nowrap>
		                        <html:hidden name="addressBookConstantForm" property='<%="fieldlist["+index+"].value"%>' styleClass="text"/>                               
		                        <html:text name="addressBookConstantForm" property='<%="fieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="textColorWrite" onchange="fieldcode(this,2);"/>
		                        <logic:equal name="element" property="itemid" value="e0122"> 
		                        	<!-- 改用代码框控件 img 标签   wangb 20171123 and 代码选择控件没有居中显示 添加 align="absmiddle" bug 34374  20180131 参数  -->
		                            <img align="absmiddle" src="/images/code.gif" plugin='codeselector' onlySelectCodeset='true' codesetid='UM'  nmodule='4' ctrltype='3' inputname='<%="fieldlist["+index+"].viewvalue"%>' valuename='<%="fieldlist["+index+"].value"%>'/>
		                        </logic:equal> 
		                        <logic:notEqual name="element" property="itemid" value="e0122">                                         
		                            <!-- 改用代码框控件 img 标签   wangb 20171123 and 代码选择控件没有居中显示 添加 align="absmiddle" bug 34374  20180131 参数  -->
		                     		<img align="absmiddle" src="/images/code.gif" plugin='codeselector' codesetid='${element.codesetid}' inputname='<%="fieldlist["+index+"].viewvalue"%>' valuename='<%="fieldlist["+index+"].value"%>'/>
		                        </logic:notEqual>    
		                      </td>
		                     <%
		                       if(flag2==0)
		        	               out.println("</tr>");
		                     %>           
		                   </logic:equal>
		                   <logic:equal name="element" property="codesetid" value="@K">
		                       <%
		                       if(flag2==0)
		                       {
		                           out.println("<tr>");
		                           flag2=1;          
		                       }else{
		                            flag2=0;           
		                       }
		                      %>  
		                      <td align="right" height='28' nowrap>
		                        <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
		                      </td>
		                      <td align="left" nowrap>
		                        <html:hidden name="addressBookConstantForm" property='<%="fieldlist["+index+"].value"%>' styleClass="text"/>                               
		                        <html:text name="addressBookConstantForm" property='<%="fieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="textColorWrite" onchange="fieldcode(this,2);"/>
		                      	<!--改用代码框控件 img 标签   wangb 20171123 and 代码选择控件没有居中显示 添加 align="absmiddle" bug 34374  20180131 参数  -->
		                        <img align="absmiddle" src="/images/code.gif" plugin='codeselector' onlySelectCodeset='true' codesetid='${element.codesetid}'  nmodule='4' ctrltype='3' inputname='<%="fieldlist["+index+"].viewvalue"%>' valuename='<%="fieldlist["+index+"].value"%>'/>
		                      </td>
		                     <%
		                       if(flag2==0)
		        	               out.println("</tr>");
		                     %>           
		                   </logic:equal>
		                   <logic:notEqual name="element" property="codesetid" value="UN">
		                      <logic:notEqual name="element" property="codesetid" value="UM">
		                         <logic:notEqual name="element" property="codesetid" value="@K">
		                               <!-- 大于 -->
		                                <%
		                                 if(flag2==0)
		                                 {
		                                     out.println("<tr>");
		                                     flag2=1;          
		                                 }else{
		                                     flag2=0;           
		                                 }
		                                %>  
		                                <td align="right" height='28' nowrap>
		                                  <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
		                                </td>
		                                <td align="left" nowrap>
		                                  <html:hidden name="addressBookConstantForm" property='<%="fieldlist["+index+"].value"%>' styleClass="text"/>                               
		                                  <html:text name="addressBookConstantForm" property='<%="fieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="textColorWrite" onchange="fieldcode(this,2);"/>
		                                  <!-- 改用代码框控件 img 标签   wangb 20171123 and 代码选择控件没有居中显示 添加 align="absmiddle" bug 34374  20180131 参数 -->
		                        		  <img align="absmiddle" src="/images/code.gif" plugin='codeselector' codesetid='${element.codesetid}' inputname='<%="fieldlist["+index+"].viewvalue"%>' valuename='<%="fieldlist["+index+"].value"%>'/>
		                                </td>
		                               <%
		                                if(flag2==0)
		        	                    out.println("</tr>");
		                                %>         
		                         </logic:notEqual>
		                      </logic:notEqual>
		                   </logic:notEqual>
		              </logic:notEqual>
		              <logic:equal name="element" property="codesetid" value="0">
		                <logic:notEqual name="element" property="itemid" value="a0101">
		                                                              
		               <% 
		                  if(flag2==0)
		                  {
		                   out.println("<tr>");
		                         flag2=1;          
		                  }else{
		                       flag2=0;           
		                  }    
		              %> 
		              <td align="right" height='28' nowrap>
		                   <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
		              </td>
		              <td align="left"  nowrap>
		               <html:text name="addressBookConstantForm" property='<%="fieldlist["+index+"].value"%>' size="31" maxlength="${element.itemlength}" styleClass="textColorWrite"/>
		              </td>
		              <%
		                 if(flag2==0)
		        			out.println("</tr>");
		              %> 
		              </logic:notEqual>
		            </logic:equal>             
		         </logic:equal>
		       </logic:iterate>
		        <%
                 if(flag2==1)
                {
                     out.println("<td colspan=\"2\">");
                     out.println("</td>");
                     out.println("</tr>");
                }
                %> 
		    	<tr>
		    	  <td align="right" height='20'  nowrap>
		    	    
		    	     <bean:message key="label.query.like"/>&nbsp; 
		    	    
		    	  </td>
		    	  <td align="left" colspan="3" height='20' nowrap>
		    	      <html:checkbox name="addressBookConstantForm" property='querylike'/>
		    	  </td>    	  
		    	</tr>
		     </table>
		               
		       </td>
		       </tr>
		       
		       <tr>
		      <td height="5">
		      </td>
		    </tr>
         <tr>
    	  <td align="center" valign="middle" colspan="4" height='30'  nowrap>    	   
    	    <Input type='button' value="<bean:message key="infor.menu.query"/>" onclick="change();" class='mybutton' />
    	    <Input type='button' value="<bean:message key="button.clear"/>" onclick='resetQuery();' class='mybutton' />
    	  </td>
         </tr>
   </table>        
  
 <%int i=0;%>
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
      <thead>
         <tr>
          
            <logic:iterate id="element"    name="addressBookConstantForm"  property="fieldlist" indexId="index" scope="session"> 
             
                <td align="center" class="TableRow" nowrap>
                 &nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/> &nbsp;
                </td>
              
           </logic:iterate>
                 	        
         </tr>
      </thead>     
        <hrms:paginationdb id="element" name="addressBookConstantForm" sql_str="addressBookConstantForm.sqlstr" table="" where_str="addressBookConstantForm.strwhere" columns="addressBookConstantForm.columns" order_by="addressBookConstantForm.orderby" pagerows="${addressBookConstantForm.pagerows}" page_id="pagination" keys="">
        
          <%
          if(i%2==0){ 
          %>
          <tr class="trShallow">
          <%
          }else{
          %>
          <tr class="trDeep">
          <%}i++; 
            
          %>           
            <logic:iterate id="info" name="addressBookConstantForm"  property="fieldlist" indexId="index"> 
                       <logic:notEqual name="info" property="codesetid" value="0">
                          <td align="left" class="RecordRow" nowrap>     
                            <logic:equal name="info" property="itemid" value="e0122">
                              <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" uplevel="${addressBookConstantForm.uplevel}" codeitem="codeitem" scope="page"/>  	      
                            &nbsp;  <bean:write name="codeitem" property="codename" />&nbsp;
                            </logic:equal>               
                            <logic:notEqual name="info" property="itemid" value="e0122">
                            <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                            &nbsp;  <bean:write name="codeitem" property="codename" />&nbsp;
                            </logic:notEqual>
                          </td>  
                       </logic:notEqual>
                       <logic:equal name="info" property="codesetid" value="0">
                           <td align="left" class="RecordRow" nowrap>
                            &nbsp;    <bean:write name="element" property="${info.itemid}" filter="true"/>
                               
                             </td>                          
                       </logic:equal>                                            
              </logic:iterate>     
          </tr>          
        </hrms:paginationdb> 
                                 	    		        	        	        
      </table>
     </td>
   </tr> 
   <tr>
   <td>
     <table  width="100%" align="left" class="RecordRowP">
       <tr>        
         <td valign="bottom"  class="tdFontcolor" nowrap>
			 <hrms:paginationtag name="addressBookConstantForm" pagerows="${addressBookConstantForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>             
	     </td>
	  <td  align="right" nowrap class="tdFontcolor">
	     <p align="right"><hrms:paginationdblink name="addressBookConstantForm" property="pagination" nameId="addressBookConstantForm" scope="page">
             </hrms:paginationdblink>
	  </td>
	</tr>	
     </table>
   </td>
 </tr>
</table>
<table align="center">
	<tr>
		<td align="center" style="height:35px;">
			<input type="button" name="export" value="<bean:message key="goabroad.collect.educe.excel"/>" class="mybutton" onclick="exportExcel()"/>
		
			<!-- 自助服务导航图返回 
			<input type="button" name="b_return" value="<bean:message key="button.return"/>" class="mybutton"  onclick="hrbreturn('selfinfo','il_body','addressBookConstantForm')">
			-->			
		</td>
	</tr>
</table>
</html:form>

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
<script language="javascript" src="/js/validate.js"></script>

<script language="javascript" src="/js/constant.js"></script>
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
    var webserver=<%=flag%>;
    
</script>
<script language="javascript">
  var checkdate=new Array(10);
  var position;
   function change()
   {
       
       for(var i=0;i<checkdate.length;i++){
          if(checkdate[i]=='false')
              return;
       }
          addressBookConstantForm.action="/selfservice/addressbook/queryaddressbook.do?b_search=link&query=2";
          addressBookConstantForm.submit();
   }

    // 生成excel   
    function exportExcel() {
        // 创建ajax参数对象
        var hashvo=new ParameterSet();
        //执行交易类
        var request=new Request({method:'post',onSuccess:showAddressBookExcel,functionId:'1020060007'},hashvo);
    }
    
    // 下载Excel
    function  showAddressBookExcel(outparamters) {
        var fileName = outparamters.getValue("fileName");
//      window.location.href="/servlet/DisplayOleFile?filename="+fileName;
		//20/3/5 xus vfs改造
        window.location.href="/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true";
    } 

    function showOrClose()
    {
            var obj=eval("aa");
            var obj3=eval("vieworhidd");
            //var obj2=eval("document.browseForm.isShowCondition");
            if(obj.style.display=='none')
            {
                obj.style.display='';
                obj3.innerHTML="<a href=\"javascript:showOrClose();\"> 查询隐藏 </a>";
            }
            else
            {
                obj.style.display='none';
                obj3.innerHTML="<a href=\"javascript:showOrClose();\"> 查询显示 </a>";
                
            }
    }

   function getDate(obj,index){
       var textbox=obj.getElementsByTagName("input");
       position=index;
       
           var start=textbox[0].value;
           var end=textbox[1].value;
           if(start==""&&end==""){
               checkdate[position]='true';
               document.getElementById("checkdate"+position).innerHTML="";
              return;
           }
           else if(start!=""&&end!=""){
               if(isDate(start) && isDate(end) && checkDate(start,end)){
                     checkdate[position]='true';
                     document.getElementById("checkdate"+position).innerHTML="";
               }
           }
           else{
               checkdate[position]='false';
               document.getElementById("checkdate"+position).innerHTML="提示:  请设置开始和结束日期！";
           }
   }

    
    function checkDate(sdate1,edate2)
      {
          
          // 对字符串进行处理
          // 以 – / 或 空格 为分隔符, 将日期字符串分割为数组
          var date1 = sdate1.split(".");
          var date2 = edate2.split(".");
          // 创建 Date 对象
          var myDate1 = new Date(date1[0],date1[1],date1[2]);
          var myDate2 = new Date(date2[0],date2[1],date2[2]);
          // 对日起进行比较
              if (myDate1 <= myDate2)
              {
              return true;
              }else
              {
                  document.getElementById("checkdate"+position).innerHTML="提示:  开始时间不能大于结束时间！";
                  checkdate[position]='false';
              return false;
              }
      }
      /**
      判断日期格式 2000-01-01
      strDate：检测的日期格式
      return： true/false
      **/
      function   isDate(strDate){
      var   strDateArray;
      var   intYear;
      var   intMonth;
      var   intDay;
      var   boolLeapYear;
      //var strDate=form1.a.value   //表单中的日期值
      strDateArray = strDate.split(".");
      if(strDateArray.length!=3)    {   document.getElementById("checkdate"+position).innerHTML="提示: 日期格式错误!";
      checkdate[position]='false'; return   false;   }

      intYear  =  strDateArray[0];
      intMonth  =  strDateArray[1];
      intDay   =   strDateArray[2];
      if(intYear.length!=4 || intMonth.length!=2 ||intDay.length!=2){document.getElementById("checkdate"+position).innerHTML="提示: 日期格式错误!";
      checkdate[position]='false'; return   false}
      
      if(isNaN(intYear)||isNaN(intMonth)||isNaN(intDay))   { document.getElementById("checkdate"+position).innerHTML="提示: 日期格式错误!";
      checkdate[position]='false'; return   false }

      if(intMonth>12||intMonth<1) {  document.getElementById("checkdate"+position).innerHTML="提示: 日期格式错误!";
      checkdate[position]='false'; return   false  }

      if((intMonth==1||intMonth==3||intMonth==5||intMonth==7||intMonth==8||intMonth==10||intMonth==12)&&(intDay>31||intDay<1))   {   document.getElementById("checkdate"+position).innerHTML="提示: 日期格式错误!";
      checkdate[position]='false'; return   false  }

      if((intMonth==4||intMonth==6||intMonth==9||intMonth==11)&&(intDay>30||intDay<1))   {   document.getElementById("checkdate"+position).innerHTML="提示: 日期格式错误!";
      checkdate[position]='false'; return   false  }

      if(intMonth==2){
      if(intDay<1)   {  document.getElementById("checkdate"+position).innerHTML="提示: 日期格式错误!";
      checkdate[position]='false'; return   false }

      boolLeapYear   =   false;
      if((intYear%4==0 && intYear %100!=0)||(intYear %400==0))
      {
      boolLeapYear=true;
      }

      if(boolLeapYear){
      if(intDay>29) {  document.getElementById("checkdate"+position).innerHTML="提示: 日期格式错误!";
      checkdate[position]='false'; return   false  }
      }
      else{
      if(intDay>28)  {   document.getElementById("checkdate"+position).innerHTML="提示: 日期格式错误!";
      checkdate[position]='false'; return   false  }
      }
      }

      return   true;
      }
</script>
<script type="text/javascript">
function resetQuery()
{
    var vo=document.getElementById("query");
    var inps=vo.getElementsByTagName("input") ;
    for(i=0;i<inps.length;i++)
    {
      if(inps[i].type=="hidden"||inps[i].type=="text")
        inps[i].value="";
      else if(inps[i].type=="checkbox")      
         inps[i].checked=false;
      
    }   
    var sels=document.getElementsByTagName("select") ;
    for(i=0;i<sels.length;i++)
    {
     sels[i].options[0].selected=true ;
    }
}

if(!getBrowseVersion()){//兼容非iE浏览器 wangb 20171127
	var imgs = document.getElementsByTagName('img');//获取img  
	for(var i = 0 ; i < imgs.length ; i++){
		imgs[i].style.verticalAlign='middle';// 设置图片居中对齐    wangb  20171127
	}
}

</script>
