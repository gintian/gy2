<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%int i=0; %>
<script type="text/javascript">
<!--
function OutExcel()
{
	var hashvo=new ParameterSet();
	hashvo.setValue("sort","${trainAtteForm.sort}"); 
	hashvo.setValue("funt","collect"); 		
   	var request=new Request({method:'post',asynchronous:true,onSuccess:showExportInfo,functionId:'20200203006'},hashvo);
   	var waitInfo=eval("wait");	   
    waitInfo.style.display="block";
}
function showCard(nbase,a0100){
	 var hashvo=new ParameterSet();
      hashvo.setValue("nbase",nbase);
      hashvo.setValue("a0100",a0100);     
      var request=new Request({method:'post',asynchronous:false,onSuccess:showCardDesc,functionId:'20200203008'},hashvo);
}
function showCardDesc(outparamters){
	if(outparamters){
		document.write(outparamters.getValue("desc"));
	}
}
//-->
</script>
<style>
<!--
.divStyle {
	
	height: expression(document.body.clientHeight-150);
	width: expression(document.body.clientWidth-40);
}
.divStyle1 {
	overflow: auto;
	width: expression(document.body.clientWidth-10);
}
.mytop
{
	border-top: none;
}
-->
</style>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/common.js"></script>
<script language="JavaScript" src="signcollect.js"></script>
<html:form action="/train/signCollect/signcollect"> 
<input type="hidden" name="search" value=""/>
<logic:notEqual name="trainAtteForm" property="view_record" value="false">
<table border="0" cellspacing="0"  align="left" cellpadding="0" width="100%" style="border-collapse: collapse;" >
  <tr>
    <td>
      <table border="0" cellspacing="0"  align="left" cellpadding="0" style="margin-buttom:5px;">
         <tr>
         <!-- 
           <td>
             <table> 
               <tr>
                <td>
                  <hrms:menubar menu="menu2" id="menubar1" target="mil_body">                
                    <hrms:menuitem name="rece2" label="编辑" >
	                <hrms:menuitem name="mitem3" label="签到汇总" icon="/images/write.gif" url="javascript:collectData();" command=""  function_id=""/>
	                <hrms:menuitem name="mitem2" label="条件查询" icon="/images/view.gif" url="javascript:selectTerm();" command="" function_id="" />
	                <hrms:menuitem name="mitem2" label="导出EXCEL" icon="/images/sort.gif" url="javascript:OutExcel();" command="" function_id="" />	                            
                  </hrms:menuitem>   
                  </hrms:menubar>
                </td>
              </tr>
             </table>
           </td>
            -->           
           <td>    
               &nbsp;<bean:message key="train.sign_collect.sort"/>    
               <html:select name="trainAtteForm" property="sort" size="1" onchange="change('nn');">
                  <html:optionsCollection property="sortlist" value="dataValue" label="dataName"/>	        
               </html:select>
               &nbsp;
               <logic:equal name="trainAtteForm" property="sort" value="1">
                 <bean:message key="train.class"/> 
                 <html:select name="trainAtteForm" property="classplan" styleId="classplan" size="1" onchange="loadclass('true');">
                  <html:optionsCollection property="classplanlist" value="dataValue" label="dataName"/>	        
                 </html:select>&nbsp;
                 <bean:message key="train.curriculum"/> 
                 <html:select name="trainAtteForm" property="courseplan" styleId="courseplan" onchange="searchinfo();"></html:select>&nbsp;
			   </logic:equal>
			   <logic:equal name="trainAtteForm" property="sort" value="2">
                 <bean:message key="train.class"/> 
                 <html:select name="trainAtteForm" property="classplan" size="1" onchange="change('');">
                  <html:optionsCollection property="classplanlist" value="dataValue" label="dataName"/>	        
                 </html:select>&nbsp;
              </logic:equal>
           </td>
           <td>
<bean:message key="train.classplan.timepath"/>
<hrms:optioncollection name="trainAtteForm" property="timelist" collection="tlist" />
<html:select name="trainAtteForm" property="timeflag" style="width:80px;font-size:10pt;text-align:left" onchange="timeFlagChange(this);">
	<html:options collection="tlist" property="dataValue" labelProperty="dataName"/>
</html:select>
</td>
<logic:equal value="04" name="trainAtteForm" property="timeflag">
<td id="viewtime">
</logic:equal>
<logic:notEqual value="04" name="trainAtteForm" property="timeflag">
<td id="viewtime" style="display:none">
</logic:notEqual>
<span style="vertical-align: top">
<bean:message key="hmuster.label.from"/><input type="text" name="startime"  extra="editor" value="${trainAtteForm.startime}" onblur="timeCheck(this);" style="width:100px;font-size:10pt;text-align:left" dropDown="dropDownDate">
<bean:message key="hmuster.label.to"/><input type="text" name="endtime"  extra="editor" value="${trainAtteForm.endtime}" onblur="timeCheck(this);" style="width:100px;font-size:10pt;text-align:left" dropDown="dropDownDate">
&nbsp;&nbsp;</span><input type="button" value="<bean:message key='infor.menu.query'/>" class="mybutton" onclick="change('nn');">
</td>
         </tr>
      </table>
      <div style="height:25px;width: 10px;overflow: hidden;"></div>
    </td>
   </tr>
   <tr>
     <td>
     <div class="fixedDiv2">
       <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
   	     <thead>
              <tr>       	    
               <logic:iterate id="element"    name="trainAtteForm"  property="fielditemlist" indexId="index"> 
                 <logic:equal name="element" property="visible" value="true">
                   <td align="center" class="TableRow mytop" style="border-left: 0px;border-top: none;" nowrap>
                    <logic:equal value="b0110" name="element" property="itemid">
                    	<bean:message key="b0110.label"/>
                    </logic:equal>
                    <logic:equal value="e0122" name="element" property="itemid">
                    	<bean:message key="e0122.label"/>
                    </logic:equal>
                    <logic:equal value="e01a1" name="element" property="itemid">
                    	<bean:message key="e01a1.label"/>
                    </logic:equal>
                    <logic:notEqual value="b0110" name="element" property="itemid">
                    <logic:notEqual value="e0122" name="element" property="itemid">
                    <logic:notEqual value="e01a1" name="element" property="itemid">
                    	&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp; 
                    </logic:notEqual>
                    </logic:notEqual>
                    </logic:notEqual>
                   </td>
                    <logic:equal value="a0101" name="element" property="itemid">
                    	<td align="center" class="TableRow mytop" style="border-left: 0px;border-top: none;" nowrap>
                    		&nbsp;卡号&nbsp;
                    	</td>
                    </logic:equal>
                 </logic:equal>
               </logic:iterate>
               </tr>
   	     </thead>   	   
   	     <hrms:paginationdb id="element" name="trainAtteForm" sql_str="trainAtteForm.sql_str" table="" where_str="trainAtteForm.where_str" columns="trainAtteForm.columns" order_by="trainAtteForm.order_str" page_id="pagination" pagerows="${trainAtteForm.pagerows}" indexes="indexes">
	         <%
               if(i%2==0){ 
             %>
             <tr class="trShallow">
             <%
               }else{
             %>
             <tr class="trDeep">
             <%}
             %>                
	           <logic:iterate id="info"  name="trainAtteForm"  property="fielditemlist" indexId="index">
	            <logic:equal name="trainAtteForm" property="sort" value="1">
	             <bean:define id="nbase" name="element" property="nbase"/>
	             <bean:define id="a0100" name="element" property="a0100"/>
	             <logic:equal name="info" property="visible" value="true">
	              <logic:equal name="info" property="itemtype" value="A"> 
	                 <td align="left" class="RecordRow mytop" style="border-left: 0px;border-top: none;" nowrap>
	                   &nbsp;
	                  <logic:notEqual name="info" property="codesetid" value="0">
                        <logic:notEqual name="info" property="itemid" value="e0122">
                            <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                             <bean:write name="codeitem" property="codename" />
                       </logic:notEqual>
                       <logic:equal name="info" property="itemid" value="e0122">
                     	<hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${trainAtteForm.uplevel}"/>  	      
                             <bean:write name="codeitem" property="codename" />
                       </logic:equal>      
                    </logic:notEqual>
                    <logic:equal name="info" property="codesetid" value="0">
                     	<bean:write name="element" property="${info.itemid}" filter="true" />
                    </logic:equal>
                    &nbsp;
	                 </td> 
	              </logic:equal>
	              <logic:equal name="info" property="itemtype" value="N">  
	                <td align="right" class="RecordRow mytop" style="border-left: 0px;border-top: none;" nowrap>
	                &nbsp;
	                  <logic:equal name="trainAtteForm" property="classpanSpflag" value="04">
	                      <logic:notEqual name="element" property="${info.itemid}" value="0">
                              <html:text name="element" property="${info.itemid}" size="8" styleClass="text" style= "text-align:right " onblur="checkValue(this,'${info.itemid}','${nbase}','${a0100}');" onkeypress="event.returnValue=IsDigit();"/>&nbsp; 
                          </logic:notEqual>
                          <logic:equal name="element" property="${info.itemid}" value="0">
                               <html:text name="element" property="${info.itemid}" value="" size="8" styleClass="text" style= "text-align:right" onblur="checkValue(this,'${info.itemid}','${nbase}','${a0100}');" onkeypress="event.returnValue=IsDigit();"/>&nbsp; 
                          </logic:equal>
	                  </logic:equal>
	                  <logic:notEqual name="trainAtteForm" property="classpanSpflag" value="04">
	                    <logic:notEqual name="element" property="${info.itemid}" value="0">   
	                      <bean:write name="element" property="${info.itemid}" filter="true" />
	                    </logic:notEqual>
	                  </logic:notEqual>
	                  &nbsp;
	                </td>
	              </logic:equal>
	              <logic:equal name="info" property="itemtype" value="D">  
	                    <td align="right" class="RecordRow" style="border-left: 0px;border-top: none;" nowrap>
	                    &nbsp;
	                      <bean:write name="element" property="${info.itemid}" filter="true" />
	                      &nbsp;
	                   </td>
	              </logic:equal>
	             </logic:equal>
	             <logic:equal value="a0101" name="info" property="itemid">
                    <td align="center" class="RecordRow mytop" style="border-left: 0px;border-top: none;" nowrap>
                   		&nbsp;<script type="text/javascript">showCard('${nbase}','${a0100}');</script>&nbsp;
                   	</td>
                  </logic:equal>
	            </logic:equal>
	            <logic:notEqual name="trainAtteForm" property="sort" value="1">
	               <logic:equal name="info" property="visible" value="true">
	                   <logic:equal name="info" property="itemtype" value="A"> 
	                     <td align="left" class="RecordRow" style="border-left: 0px;border-top: none;" nowrap>
	                      &nbsp;
	                      <logic:equal name="trainAtteForm" property="sort" value="2">  
	                         <logic:equal name="info" property="itemid" value="r4101">
	                          <bean:define id="r4101" name="element" property="r4101"/>
	                          <hrms:trainname id="${r4101}" sort="2"/>
	                         </logic:equal>
	                      </logic:equal>
	                      <logic:equal name="trainAtteForm" property="sort" value="3">  
	                         <logic:equal name="info" property="itemid" value="r4103">
	                          <bean:define id="r4103" name="element" property="r4103"/>
	                          <hrms:trainname id="${r4103}" sort="3"/>
	                         </logic:equal>
	                      </logic:equal>
	                      <logic:notEqual name="info" property="itemid" value="r4101">
	                       <logic:notEqual name="info" property="itemid" value="r4103">
	                         <bean:write name="element" property="${info.itemid}" filter="true" />
	                       </logic:notEqual>
	                      </logic:notEqual>
	                      &nbsp;
	                     </td>
	                   </logic:equal>
	                   <logic:equal name="info" property="itemtype" value="N">  
	                    <td align="right" class="RecordRow" style="border-left: 0px;border-top: none;" nowrap>
	                    &nbsp;
	                     <logic:notEqual name="element" property="${info.itemid}" value="0">   
	                      <bean:write name="element" property="${info.itemid}" filter="true" />
	                     </logic:notEqual>
	                      &nbsp;
	                   </td>
	                  </logic:equal>
	                  <logic:equal name="info" property="itemtype" value="D">  
	                    <td align="right" class="RecordRow" style="border-left: 0px;border-top: none;" nowrap>
	                    &nbsp;
	                      <bean:write name="element" property="${info.itemid}" filter="true" />
	                      &nbsp;
	                   </td>
	                  </logic:equal>
	               </logic:equal>
	             </logic:notEqual>
	           </logic:iterate>
	        </tr>
	        </hrms:paginationdb>
   	     </table>
   	     </div>
     </td>
   </tr>
   <tr>
     <td>
     <div class="divStyle1">
      <table border="0" cellspacing="0"  align="center" class="ListTableF"  style="border-top: none;" cellpadding="0" width="100%">
       <tr height="30px">          
        <td width="60%" valign="bottom" align="left" height="25" nowrap>
           &nbsp;<hrms:paginationtag name="trainAtteForm"
								pagerows="${trainAtteForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
	    </td>
	    <td  width="40%" valign="bottom" align="right" nowrap>
	     <hrms:paginationdblink name="trainAtteForm" property="pagination" nameId="trainAtteForm" scope="page">
             </hrms:paginationdblink>&nbsp;
	   </td>
	  </tr>
     </table>
     </div>
     </td>
   </tr>
   <tr style="padding-top: 5px;">
		<td align="left">
		  <hrms:priv func_id="323320301"> 
		   <logic:equal name="trainAtteForm" property="sort" value="1">
			<input type="button" name="b_collect" value='签到汇总' onclick="collectData();"
				class="mybutton" />
		   </logic:equal>
		  </hrms:priv>
		  <hrms:priv func_id="323320302"> 
			<input type="button" name="b_select" value='条件查询' onclick="selectTerm();"
				class="mybutton" />
		  </hrms:priv>
		  <hrms:priv func_id="323320303"> 
			<input type="button" name="b_add" value='导出Excel' onclick="OutExcel();"
				class="mybutton" />
		  </hrms:priv>
		</td>
	</tr>
 </table> 
 <div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
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
<logic:equal name="trainAtteForm" property="sort" value="1">
<script>loadclass('${trainAtteForm.loadclass}','${trainAtteForm.courseplan}');</script>
</logic:equal>
<script>MusterInitData();</script> 
 <logic:equal name="trainAtteForm" property="timeflag" value="04">
<script language="JavaScript">
toggles("viewtime");
</script>
</logic:equal>
</logic:notEqual>
<!--<logic:equal name="trainAtteForm" property="view_record" value="false">-->
<!-- <script>-->
<!--  alert("没有培训班或课程！");		   -->
<!--  history.back();-->
<!--</script>-->
<!--</logic:equal> -->
</html:form>
