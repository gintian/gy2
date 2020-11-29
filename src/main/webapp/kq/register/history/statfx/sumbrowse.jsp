<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.register.BrowseHistoryForm" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="java.util.HashMap" %>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript">

function changeys()
{
	var dd = document.getElementById("editor1").value;
	if(dd==null||dd=="")
		{
			alert("开始时间不能为空！");
			return;
		}
		if(!isDate(dd,"yyyy-MM-dd"))
		{
			alert("开始时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
			return;
		}
	var cc = document.getElementById("editor2").value;
	if(cc==null||cc=="")
	{
		alert("终止时间不能为空！");
	    return;
	}
	if(!isDate(cc,"yyyy-MM-dd"))
		{
			alert("终止时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
			return;
		}
	if(dd>cc)
	{
		alert("起始时间不能大于终止时间！");
		return;
	}
	var dbpre = $F('dbpre');
	if(dbpre.length<3){
		alert("请指定人员库！");
		return;
	}
	var waitInfo=eval("wait");	
	waitInfo.style.display="block";
	browseHistoryForm.action="/kq/register/history/statfx/statfxdata.do?b_query=link&registertime="+dd+"&jsdatetime="+cc+"";
	browseHistoryForm.submit();
}

function hr(link){
	//alert(link);
	document.location.href=link;
}	
</script>
<html:form action="/kq/register/history/statfx/statfxdata">
	<div id='wait' style='position:absolute;top:25%;left:30%;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style common_background_color" height=24>
					正在统计，请稍候...
				</td>
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
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
		<tr height="1px"><td></td></tr>
		<tr class="list3">
			<td align= "left" style="padding-bottom: 5px;" nowrap>
         <html:select name="browseHistoryForm" property="dbpre" styleId="dbpre" size="1" onchange="">
                <html:optionsCollection property="slist" value="dataValue" label="dataName"/>
          </html:select>
          
			<bean:message key="label.by.time.domain"/>
		 			<bean:message key="label.from"/>
   	  	 			<input type="text" name="start_date" value="${browseHistoryForm.start_datetj}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor1" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>
   	  	 		 <bean:message key="label.to"/>
   	  	 		 <input type="text" name="end_date"  value="${browseHistoryForm.end_datetj}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor2" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>
			<hrms:priv func_id="2705201,0C3901">
   	  	 	<input type="button" name="br_approve"
								value='统计' class="mybutton"
								onclick="javascript:changeys();"> 
			</hrms:priv>
			</td>
		</tr>
		<tr>
		  <td><div class="fixedDiv2">
		 <%int i=0;
		 	String name=null;
		 	String codename=null;
		 	BrowseHistoryForm browseHistoryForm=(BrowseHistoryForm)session.getAttribute("browseHistoryForm");
		 	codename = browseHistoryForm.getCodetj();
		 %>
		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
			<thead>
         		<tr>
         			<logic:iterate id="element"    name="browseHistoryForm"  property="kqq03list" indexId="index">
         			  <logic:equal name="element" property="visible" value="true">
                		<td align="center" class="TableRow" style="border-left: none;border-top: none;" nowrap>
                 			<bean:write  name="element" property="itemdesc"/>&nbsp; 
                		</td>
                	  </logic:equal>
         			</logic:iterate>
         		</tr>
         	</thead>
         	<hrms:paginationdb id="element" name="browseHistoryForm" sql_str="browseHistoryForm.sqlstr" table="" where_str="browseHistoryForm.strwhere" columns="browseHistoryForm.columns" order_by="browseHistoryForm.orderby" pagerows="20" page_id="pagination">
          		<%
          		LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
          if(i%2==0){ 
          %>
          <tr class="trShallow">
          <%
          }else{
          %>
          <tr class="trDeep">
          <%}i++; 
            
          %>
           <logic:iterate id="info" name="browseHistoryForm"  property="kqq03list">
           	 <%
               int r=0;
             %>
             <%
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
                          <td align="left" class="RecordRow" style="border-left: none;" nowrap>
                          	<table>
                         		<tr>
                         		   <logic:equal  name="element" property="setid" value="UN">
                         			<td align="left" nowrap>
                         				<img src="/images/unit.gif" border=0>
                         			</td>
                         			<td nowrap>
                         				<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
                                        <bean:write name="codeitem" property="codename" />
                         			</td>
                         			</logic:equal>
                         			<logic:equal  name="element" property="setid" value="UM">
                         				<td align="left" nowrap>
                         				<img src="/images/dept.gif" border=0>
                         			    </td>
                         			    <td nowrap>
                         				<hrms:codetoname codeid="UM" name="element" codevalue="b0110" codeitem="codeitem" scope="page" uplevel="${browseHistoryForm.uplevel}"/>  	      
                                        <bean:write name="codeitem" property="codename" />&nbsp;  
                         			    </td>
                         			</logic:equal>
                         		</tr>
                         	  </table>
                          </td>  
                         </logic:notEqual>
                      </logic:equal>
                   <!--数字-->
                      <logic:equal name="info" property="itemtype" value="N">
                      <td align="center" class="RecordRow" style="border-right: none;" nowrap align="right"> 
                         <logic:greaterThan name="element" property="${info.itemid}" value="0">
                         	
                             <bean:define id="b01101" name="element" property="b0110"/>
					         <bean:define id="itemid1" name="info" property="itemid"/>
					         <%
					         		//参数加密
					    		     String str1 = "b01101=" + b01101 + "&itemid=" + itemid1 + "&codetj=" + codename;
					         %>
                            	<a href="###" onclick="hr('/kq/register/history/statfx/statfxname.do?b_seename=link&encryptParam=<%=PubFunc.encrypt(str1) %>')">
                                  &nbsp;<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>
                                  <hrms:kqanalysis  value='<%=abean.get(name)+""%>'/> 人 &nbsp;	      
                                
                                </a>  
                            
                         </logic:greaterThan> 
                      </td>
                  </logic:equal>
                 </logic:equal> 
               <!----> 
           </logic:iterate>
            </tr>        
         	</hrms:paginationdb>
		</table></div>
		</td>
		</tr>
		<tr>
   		  <td><div style="*width:expression(document.body.clientWidth-10);">
     	   <table  width="100%" class="RecordRowP" align="left">
       		<tr>
          	  <td width="20%" valign="bottom"  class="tdFontcolor" nowrap>
             		第<bean:write name="pagination" property="current" filter="true" />页
             		共<bean:write name="pagination" property="count" filter="true" />条
             		共<bean:write name="pagination" property="pages" filter="true" />页
	  		  </td>
	  		  <td  width="80%" align="right" nowrap class="tdFontcolor">
	     			<p align="right"><hrms:paginationdblink name="browseHistoryForm" property="pagination" nameId="browseHistoryForm" scope="page">
             	</hrms:paginationdblink>
	  		  </td>
	        </tr>	
     	  </table></div>
         </td>
 	   </tr>
	</table>
</html:form>
<script language="javascript">
 hide_nbase_select('dbpre');
 
</script>

