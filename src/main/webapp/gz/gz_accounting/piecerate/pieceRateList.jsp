<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.gz.gz_accounting.piecerate.PieceRateForm"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@page import="com.hrms.struts.constant.SystemConfig"%>
<%@page import="java.net.URLEncoder"%>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  

<script type="text/javascript"
	src="/gz/gz_accounting/piecerate/piecerate.js"></script>
<%
	  int i=0;
	  PieceRateForm pieceRateForm = (PieceRateForm)session
					.getAttribute("pieceRateForm");
%>
<style>
.myfixedDivz
{ 
	overflow:auto; 
	width:90%; 
	BORDER-BOTTOM: #C4D8EE 0pt solid; 
    BORDER-LEFT: #C4D8EE 0pt solid; 
    BORDER-RIGHT: #C4D8EE 0pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid ; 
} 

.myfixedDiv2 { 
	overflow:auto; 
	height:400px;
	*height:expression(document.body.clientHeight-150);
	width:100%;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}

.myfixedDiv3 { 
	overflow:auto; 
	height:30px;
	width:100%;
	BORDER-BOTTOM: #94B6E6 0pt solid; 
    BORDER-LEFT: #94B6E6 0pt solid; 
    BORDER-RIGHT: #94B6E6 0pt solid; 
    BORDER-TOP: #94B6E6 0pt solid ; 
}
</style>
<hrms:themes></hrms:themes>
<html:form action="/gz/gz_accounting/piecerate/search_piecerate">
<input id="htmlparam" type="hidden" name="html_param"/>
<!-- 【6733】薪资管理：计件薪资，页面问题 jingq add 2015.01.20 -->
<table width="98%" align="left" border="0" cellpadding="0" cellspacing="0">
	<tr>
	  <td>
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td align="left"  nowrap style="height:20">
					&nbsp;&nbsp;
					作业类别
					<hrms:optioncollection name="pieceRateForm" property="tasktypelist" collection="list"/>
					<html:select name="pieceRateForm" property="tasktype" onchange="" style="width:140">
							<html:options collection="list" property="dataValue" labelProperty="dataName" />
					</html:select>
					
					&nbsp;&nbsp;
					<bean:message key="jx.khplan.timeframe" />
					<span id="datepnl">							
					  <bean:message key="label.from" /> 				
					   <input type="text" name="start_date"
							value="${pieceRateForm.starttime}" extra="editor"
							style="width:100px;font-size:10pt;text-align:left" id="editor1"
							dropDown="dropDownDate"> 					
						<bean:message key="label.to" />			
					<input type="text" name="end_date"
							value="${pieceRateForm.endtime}" extra="editor"
							style="width:100px;font-size:10pt;text-align:left" id="editor2"
							dropDown="dropDownDate">  													
					</span>	

					&nbsp;&nbsp;		
					<bean:message key="jx.khplan.spstatus" />
					<html:select name="pieceRateForm" property="sp_status" size="1"	onchange="">
						<html:option value="all">
							所有
						</html:option>
						<html:option value="01">
							<bean:message key="hire.jp.pos.draftout" />
						</html:option>
						<html:option value="02">
							<bean:message key="label.hiremanage.status2" />
						</html:option>
						<html:option value="03">
							<bean:message key="label.hiremanage.status3" />
						</html:option>
						<html:option value="07">
							<bean:message key="button.reject" />
						</html:option>
					</html:select>
					
					&nbsp;
					<input type="button"
							onclick="search_kh_data();" class="mybutton"
							value="<bean:message key="button.query"/>">
				</td>
			</tr>        
		</table>
	  </td>
   </tr>   
   <tr>
	<tr>
	  <td style="padding-top:3px;">
		<table border="0" cellspacing="0" cellpadding="0">
	      <tr>
	        <td align="left">
	        	<hrms:priv func_id="3242101">
	    		<input type="button" class="mybutton" value="<bean:message key='button.insert'/>" onclick="addtask();">
	    		</hrms:priv>
	        	<hrms:priv func_id="3242102">
	    		<input type="button" class="mybutton" value="<bean:message key='label.edit'/>" onclick="edittask();">
	    		</hrms:priv>
	        	<hrms:priv func_id="3242103">
				<input type="button" class="mybutton" value="<bean:message key='button.delete'/>" onclick="deletetask();">
				</hrms:priv>
	        	<hrms:priv func_id="3242104">
				<input type="button" class="mybutton" value="<bean:message key='info.appleal.state1'/>" onclick="approval();">
				</hrms:priv>
	        	<hrms:priv func_id="3242105">
				<input type="button" class="mybutton" value="<bean:message key='info.appleal.state3'/>" onclick="reporting();">
				</hrms:priv>
	        	<hrms:priv func_id="3242106">
				<input type="button" class="mybutton" value="<bean:message key='info.appleal.state2'/>" onclick="reject();">
				</hrms:priv>
				<hrms:priv func_id="3242107">
				<input type="button" class="mybutton" value="考勤签到表" onclick="print1()">
				</hrms:priv>
				<hrms:priv func_id="3242113">
				<input type="button" class="mybutton" value="<bean:message key='gz.report.table'/>" onclick="reportable('${pieceRateForm.starttime}','${pieceRateForm.endtime}');">
				</hrms:priv>
	        	<hrms:priv func_id="3242111">
				<input type="button" class="mybutton" value="<bean:message key='menu.gz.options'/>" onclick="setUp();">
				</hrms:priv>
	        </td>
	      </tr>          
		</table>
	  </td>
   </tr>   
   <tr>
    <td style="padding-top:5px;">
    <div class="myfixedDiv2 common_border_color" >
   		<table  border="0" cellspacing="0"  align="left" width="100%" cellpadding="0">
	   	  <thead>
	           <tr class="fixedHeaderTr">     
            	<td align="center" class="TableRow" nowrap width="6" style="border-top:0px;border-left: none;border-right: none;">
					<input type="checkbox" name="selbox" onclick="batch_select(this,'pagelistform.select');" 
					      title='<bean:message key="label.query.selectall"/>'>&nbsp;
	    		</td>      

	        	<hrms:priv func_id="3242109">
                <td align="center" class="TableRow" nowrap style="border-top:0px;border-right: none;">
                   作业明细
                </td>
	        	</hrms:priv>
	        	<hrms:priv func_id="3242110">
                <td align="center" class="TableRow" nowrap style="border-top:0px;border-right: none;">
		          作业人员            	
                </td> 
	        	</hrms:priv>
                <hrms:priv func_id="3242111">
                <td align="center" class="TableRow" nowrap style="border-top:0px;border-right: none;">
		          作业票           	
                </td>  	
                </hrms:priv>
	            <logic:iterate id="element"   name="pieceRateForm"  property="fielditemlist"  > 
	              <td align="center" height="50" class="TableRow" nowrap style="border-top:0px;border-right:none;">
	                   <hrms:textnewline text="${element.itemdesc}" len="20"></hrms:textnewline>
	              </td>
	             </logic:iterate>  	            	                		        	        	        
	           </tr>
	   	  </thead>   	   	  

          <hrms:extenditerate id="element" name="pieceRateForm" property="pagelistform.list" indexes="indexes"  pagination="pagelistform.pagination" 
                                                                        pageCount="21" scope="session">
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
	  	  RecordVo vo=(RecordVo)element; 
	  	           
          %>    
          <bean:define id="s0100" name='element' property='String(s0100)' /> 
   	
            <td align="center" class="RecordRow" style="border-top:none;border-left:none;border-right:none;" nowrap>
   			  <hrms:checkmultibox name="pieceRateForm" property="pagelistform.select" value="true" indexes="indexes" />&nbsp; 
	    	</td>	
           <input type="hidden" name="s0100ids" value='${s0100}' />
           <hrms:priv func_id="3242109">  
            <td align="center" class="RecordRow" style="border-top:none;border-right:none;" onclick="changebox('${s0100}');" nowrap>
            	<a href="###" onclick="changebox('${s0100}');winhref('/gz/gz_accounting/piecerate/search_piecerate_detail.do?b_query=link&s0100=<bean:write  name="element" property="string(s0100)" filter="true"/>&model=detail');"><img src="/images/edit.gif" border=0></a>
	        </td>
	        </hrms:priv>
	        <hrms:priv func_id="3242110">
            <td align="center" class="RecordRow" style="border-top:none;border-right:none;" onclick="changebox('${s0100}');" nowrap>
            	<a href="###"  onclick="changebox('${s0100}');winhref('/gz/gz_accounting/piecerate/search_piecerate_detail.do?b_query=link&s0100=<bean:write  name="element" property="string(s0100)" filter="true"/>&model=people');"><img src="/images/edit.gif" border=0></a>
	        </td>
	        </hrms:priv>
	        <hrms:priv func_id="3242111">
            <td align="center" class="RecordRow" style="border-top:none;border-right:none;" onclick="changebox('${s0100}');" nowrap>
            	<a href="###"  onclick="changebox('${s0100}');print(<bean:write  name="element" property="string(s0100)" filter="true"/>)"><img src="/images/edit.gif" border=0></a>
	        </td>
	        </hrms:priv>
            <logic:iterate id="info"   name="pieceRateForm"  property="fielditemlist"> 
               <logic:notEqual  name="info" property="itemtype" value="N">    
                 <logic:notEqual  name="info" property="itemtype" value="M">               
                   <td align="left" class="RecordRow" onclick="changebox('${s0100}');" style="border-top:none;border-right:none;" nowrap>   
                    &nbsp;<bean:write  name="element" property="string(${info.itemid})" filter="true"/>&nbsp;                 
                   </td>     
                 </logic:notEqual>
              </logic:notEqual>
              <logic:equal  name="info" property="itemtype" value="N">               
                <td align="right" class="RecordRow" onclick="changebox('${s0100}');" style="border-top:none;border-right:none;" nowrap>        
                 &nbsp;<bean:write  name="element" property="string(${info.itemid})" filter="true"/>&nbsp;
                </td>
              </logic:equal>       
               <logic:equal  name="info" property="itemtype" value="M">    
                <%
                 FieldItem item=(FieldItem)pageContext.getAttribute("info");
                 String tx=vo.getString(item.getItemid());
               %>          
                <hrms:showitemmemo showtext="showtext" itemtype="M" setname="S01" tiptext="tiptext" text="<%=tx%>"></hrms:showitemmemo>
                <td align="left" class="RecordRow" ${tiptext} onclick="changebox('${s0100}');" style="border-top:none;border-right:none;" nowrap>   
                 ${showtext}&nbsp;
               </td>  
              </logic:equal> 
             </logic:iterate>
          </tr>
        </hrms:extenditerate>	       
 	  
     </table>  
     </div >
	</td>	
   </tr>   
   <tr>
     <td width ="90%" >
		<table width="100%" height=30px  border="0" cellspacing="0"  align="left" cellpadding="0" class="RecordRowP">
			<tr>
			    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pieceRateForm" property="pagelistform.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pieceRateForm" property="pagelistform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pieceRateForm" property="pagelistform.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			    </td>
	            <td  align="right" nowrap class="tdFontcolor">
		               <p align="right"><hrms:paginationlink name="pieceRateForm" property="pagelistform.pagination"
		                           nameId="pagelistform" propertyId="roleListProperty">
			           	</hrms:paginationlink>
				</td>
			</tr>
		</table>  
     </td>	 
   </tr>	     
</table>

	<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>

             <td class="td_style" height=24><bean:message key="classdata.isnow.wiat"/></td>

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
</html:form>
	<script language="javascript">
  var s0100 = '${pieceRateForm.s0100}';
  defCheck(s0100);
</script>
