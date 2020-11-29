<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hjsj.hrms.businessobject.train.TrainCourseBo"%>
<div align="right"> 
<%@taglib uri="/tags/struts-bean" prefix="bean"%> 
<%@taglib uri="/tags/struts-html" prefix="html"%> 
<%@taglib uri="/tags/struts-logic" prefix="logic"%> 
<%@taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%> 
<%@page import="com.hrms.struts.valueobject.UserView"%> 
<%@page import="com.hrms.struts.constant.WebConstant"%> 
<%@page import="com.hrms.hjsj.sys.FieldItem"%>
<%@page import="com.hrms.struts.constant.SystemConfig"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	TrainCourseBo bo = new TrainCourseBo(userView);
	String manager=bo.getUnitIdByBusi();//.getManagePrivCodeValue();
	int i=0;
	String caution_field=SystemConfig.getPropertyValue("caution_field");
	String caution_codeitems=SystemConfig.getPropertyValue("caution_codeitems");
	String caution_colors=SystemConfig.getPropertyValue("caution_colors");
	session.setAttribute("caution_field",caution_field);
	session.setAttribute("caution_codeitems",caution_codeitems);
	session.setAttribute("caution_colors",caution_colors);
%>
<%// 在标题栏显示当前用户和日期 2004-5-10 
		
%> 
 
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
<script language="javascript" src="train.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script> 
 
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
function openwin(url)
{
   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+(screen.availHeight-100)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
}

function viewPhoto()
   {
       trainStationForm.action="/workbench/browse/showinfodata.do?b_view_photo=link&code=${trainStationForm.code}&kind=${trainStationForm.kind}&query=1";       
       trainStationForm.target="nil_body";
       trainStationForm.submit();
}   
function winhrefOT(a0100,target)
{
   if(a0100=="")
      return false; 
   var returnvalue="train_post"; 
   trainStationForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase=${trainStationForm.dbpre}&a0100="+a0100+"&flag=notself&returnvalue="+returnvalue;
   trainStationForm.target="il_body";
   trainStationForm.submit();
}
function change()
{ 
   var obj = document.getElementsByName("flag")[0];      
   if(obj.options[1].selected)
   {
      var code = document.getElementsByName("code")[0].value;
      if(code=="")
      {
        alert("请先选择岗位！");
        obj.options[0].selected=true;
        return false;
      }
      trainStationForm.action="/train/postAnalyse/accordpost.do?br_init=link&query=3&flag=2&code="+code;
      trainStationForm.submit();
   }else
   {
      trainStationForm.action="/train/postAnalyse/accordpost.do?b_query=link"
      trainStationForm.submit();
   }
   
}
function changeFlag(obj)
{
  var flag=obj.value;
  if(flag=="2")
  {
     var code = document.getElementsByName("code")[0].value;
     if(code=="")
     {
        alert("请先选择岗位！");
        obj.options[0].selected=true;
        return false;
     }
     trainStationForm.action="/train/postAnalyse/accordpost.do?br_init=link&query=3&flag=2&code="+code;
     trainStationForm.submit();
  }else
  {
     trainStationForm.action="/train/postAnalyse/accordpost.do?br_init=link&query=4&flag=1";
     trainStationForm.submit();
  }
  
}
function executeOutFile(){
	var hashvo = new ParameterSet();
	hashvo.setValue("userbase","${trainStationForm.dbpre}");
	hashvo.setValue("code","${trainStationForm.code}");
	hashvo.setValue("orgtype","org");
	hashvo.setValue("roster","${trainStationForm.roster}");
	hashvo.setValue("checksort","1");
	var request=new Request({method:"post",asynchronous:false,onSuccess:showFieldList,functionId:"0521010019"},hashvo);
}
function showFieldList(outparamters){
	var outName=outparamters.getValue("outName");
    window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"xls");
}
function selectQ()
{
       var tablename="Usr";
       var thecodeurl="/train/postAnalyse/generalsearch.do?b_query=link&type=3&a_code=${trainStationForm.code }&tablename="+tablename+"&fieldsetid=K01";
       var dw=700,dh=430,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
       var  return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:700px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no"); 
      if(return_vo!=null){
            var expr= return_vo.expr;
            var factor=return_vo.factor;
            var history=return_vo.history;
            var likeflag=return_vo.likeflag;            
            //alert("sexpr:"+expr+"\r\nsfactor:"+factor+"\r\nhistory:"+history+"\r\nlikeflag"+return_vo.likeflag);
            var hashvo = new ParameterSet();
            hashvo.setValue("sfactor",factor);
	        hashvo.setValue("sexpr",expr);
	        hashvo.setValue("history",history);
	        hashvo.setValue("likeflag",likeflag);
	        hashvo.setValue("userbase","${trainStationForm.dbpre}");
	        var request=new Request({method:"post",asynchronous:false,onSuccess:selectRes,functionId:"2020051008"},hashvo);
            //trainStationForm.action="";
           // trainStationForm.submit();           
      } 
}
function selectRes(outparamters)
{
     if(outparamters)
     {
        var value=outparamters.getValue("value");
        var name=outparamters.getValue("name");
        var chwhere=outparamters.getValue("chwhere");
        document.getElementById('codename').value=name;
        document.getElementById('code').value=value;
        document.getElementById('chwhere').value=chwhere;
        change();
     }
}
</script> 
<style type="text/css">
    .RecordRow_top {
	border: inset 1px #94B6E6;	
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;

	}	
 </style>
</div><% i=0;%>
<html:form action="/train/postAnalyse/accordpost">
<html:hidden name="trainStationForm" property="chwhere" styleId="chwhere"/>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" >
<tr>
   <td align="left"  nowrap>
    <table  border="0" cellspacing="0" height="25"  cellpadding="0">
     <tr>
        <td>
           <table>
           	<tr>
           	<td>
					人员库&nbsp; 
		               <span style="vertical-align: middle;">               
		               <html:select name="trainStationForm" property="dbpre" size="1" styleId="pre" onchange="change();">
		                <html:optionsCollection property="dblist" value="dataValue" label="dataName"/>	        
		              </html:select>&nbsp;
		              </span>
		                  岗位&nbsp; 
		              <span style="vertical-align: middle;">
		                <html:hidden name="trainStationForm" property="code"/>
		     	        <hrms:codetoname codeid="@K" name="trainStationForm" codevalue="code" codeitem="codeitem"/> 
		     	        <input type="text" name="codename" value="${trainStationForm.codename}"  readonly="readonly" class="textColorRead">&nbsp;                
		             </span>
		                <img align="absMiddle" src="/images/code.gif" onclick='javascript:openCodeCustomReportDialog("@K","codename","code","0");change();' />
		        <span style="vertical-align: middle;">            
		               <html:select name="trainStationForm" property="flag" size="1" styleId="pre" onchange="changeFlag(this);">
		                <html:optionsCollection property="flaglist" value="dataValue" label="dataName"/>	        
		              </html:select>&nbsp;
		       </span>
		        <logic:notEmpty name="trainStationForm" property="classlist">
		                 &nbsp;课程&nbsp; 
		              <span style="vertical-align: middle;">              
		               <html:select name="trainStationForm" property="classid" size="1" styleId="classid" onchange="change();">
		                <html:optionsCollection property="classlist" value="dataValue" label="dataName"/>	        
		              </html:select>&nbsp;
		            </span>
		        </logic:notEmpty>    
		        <span style="vertical-align: middle;">
		      		<input type="button" class="mybutton" value="条件查询" onclick="selectQ();"/>
		  		</span>   
	           	</td>
           	</tr>
           </table>
        </td>
     </tr>
   </table>
  </td>
</tr>
<tr>
    <td width="100%" nowrap>
     <div class="fixedDiv2">
      <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
           <tr>           
           <logic:iterate id="info"    name="trainStationForm"  property="browsefields">   
              <logic:equal name="info" property="visible" value="true">
              <td align="center" class="TableRow" style="border-left: none;border-top: none;" nowrap>
                   <bean:write  name="info" property="itemdesc" filter="true"/>&nbsp;              
              </td>
              </logic:equal>
             </logic:iterate> 	
		    <logic:notEqual name="trainStationForm" property="cardid" value="-1">
             <td align="center" class="TableRow" style="border-left: none;border-top: none;" nowrap>
		     	<bean:message key="tab.base.info"/>          	
             </td>	 
             </logic:notEqual>               
            <td align="center" class="TableRow" style="border-left: none;border-top: none;border-right: none;" nowrap>
		     	<bean:message key="tab.synthesis.info"/>          	
	    	</td>     	    	    		        	        	        
           </tr>           
        <hrms:paginationdb id="element" name="trainStationForm" sql_str="trainStationForm.sqlstr" table="" where_str="trainStationForm.where" columns="trainStationForm.cloumn" order_by="order by a0000" page_id="pagination" pagerows="${trainStationForm.pagerows}" keys="${trainStationForm.dbpre}A01.a0100">
          <%
          if(i%2==0)
          {	
          %>
          <tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'')">
          <%}
          else
          {%> 
          <tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'DDEAFE')">
          <%
          }
          i++;          
          %>  	   
	     <bean:define id="a0100" name="element" property="a0100"/>	
        <%
        	String a01001 = PubFunc.encrypt(a0100.toString());
        %>
	     <logic:iterate id="info"    name="trainStationForm"  property="browsefields">   
	     	   	     		
	     	   <logic:equal name="info" property="visible" value="true">
	     	   
                  <logic:notEqual  name="info" property="itemtype" value="N">               
                    <td align="left" class="RecordRow"  style="border-left: none;border-top: none;"  nowrap>&nbsp;        
                  </logic:notEqual>
                  <logic:equal  name="info" property="itemtype" value="N">               
                    <td align="right" class="RecordRow" style="border-left: none;border-top: none;" nowrap> &nbsp;       
                  </logic:equal>    
                  <logic:equal  name="info" property="codesetid" value="0">   
                   <logic:notEqual name="info"   property="itemid" value="a0101">        
                     <bean:write  name="element" property="${info.itemid}" filter="true"/>&nbsp;
                   </logic:notEqual>
                      <logic:equal name="info"   property="itemid" value="a0101">  
          	   			 <a href="###" onclick="winhrefOT('<%=a01001 %>','nil_body');"> 
          	   			 <bean:write name="element" property="a0101" filter="true"/>
          	   			  </a>
          	   			&nbsp;
          	   	 	</logic:equal>
                  </logic:equal>
                 <logic:notEqual  name="info" property="codesetid" value="0">  
                 <logic:notEqual  name="info"   property="itemid" value="e01a1">  
                  <logic:notEqual  name="info"   property="itemid" value="a0101">  
                   <logic:equal name="info" property="codesetid" value="UM">
                        <hrms:codetoname codeid="UM" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${trainStationForm.uplevel}"/>  	      
          	            <bean:write name="codeitem" property="codename" />&nbsp;          	           
                   </logic:equal>
                   <logic:notEqual name="info" property="codesetid" value="UM">
                      <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
          	    	   <bean:write name="codeitem" property="codename" />&nbsp;  
                   </logic:notEqual>
          	     </logic:notEqual>
          	    </logic:notEqual>          	    
          	    <logic:equal name="info"   property="itemid" value="e01a1"> 
          	       <logic:empty name="trainStationForm" property="ishavepostdesc">
                     <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	         <bean:write name="codeitem" property="codename" />&nbsp;           	         
                   </logic:empty>                
                   <logic:notEmpty name="trainStationForm" property="ishavepostdesc">
                      <logic:equal name="trainStationForm" property="ishavepostdesc" value="true">
                           <a href="###" onclick='openwin("/workbench/browse/showposinfo.do?b_browse=link&<%=a01001%>&npage=1");'>
                            <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	                <bean:write name="codeitem" property="codename" />
          	              </a>&nbsp; 
          	         </logic:equal>
          	         <logic:equal name="trainStationForm" property="ishavepostdesc" value="false">
                        <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	             <bean:write name="codeitem" property="codename" />&nbsp;           	      
          	          </logic:equal>
                   </logic:notEmpty> 
                   
          	   </logic:equal>
          	       
                </logic:notEqual>  
              </td>
              </logic:equal>
             </logic:iterate> 
		    <logic:notEqual name="trainStationForm" property="cardid" value="-1">
	    			 <td align="center" class="RecordRow" style="border-left: none;border-top: none;" nowrap>&nbsp;
               			<a href="###" onclick='openwin("/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase=${trainStationForm.dbpre}&a0100=<%=a01001%>&inforkind=1&tabid=${trainStationForm.cardid}&multi_cards=-1");'>
               			<img src="../../images/table.gif" border="0"></a>
				     </td>	                
             </logic:notEqual>	                		
             <td align="center" class="RecordRow" style="border-left: none;border-top: none;border-right: none;" nowrap>&nbsp;
             	<a href="###" onclick="winhrefOT('<%=a01001 %>','nil_body');"> 
          	   		<img src="../../images/view.gif" border="0">
            	 </a>            		 	            	     	   
	      </td>    	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>        
</table>
</div>
</td></tr>
<tr><td style="padding-right: 5px;">
<table width="100%"  align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
					<hrms:paginationtag name="trainStationForm"
								pagerows="${trainStationForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			</td>
			<td  align="right" nowrap class="tdFontcolor">
			<p align="right"><hrms:paginationdblink name="trainStationForm" property="pagination" nameId="trainStationForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</td></tr>
<tr><td>
<table  align="left"  >
          <tr>
            <td align="left">            
	 	    	<logic:notEqual name="trainStationForm" property="roster" value="no">     
	 	    		<input type="button" value="<bean:message key="goabroad.collect.educe.excel"/>" onclick="executeOutFile();" class="mybutton">
	 	    	&nbsp;&nbsp;
	 	        </logic:notEqual>	
	 	    	
                
            </td>
          </tr>          
</table>
    </td>
</tr>
</table>

</html:form>
