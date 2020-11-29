<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page contentType="text/html; charset=UTF-8"%>
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
	String manager=userView.getManagePrivCodeValue();
	int i=0;
%>
<%// 在标题栏显示当前用户和日期 2004-5-10 
		
%> 
<hrms:themes></hrms:themes> 
 
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"> 
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
function change()
{
      browseForm.action="/workbench/browse/showinfodata.do?b_query=link&code=${browseForm.code}&kind=${browseForm.kind}";
      browseForm.submit();
}
function query(query)
{
   browseForm.action="/workbench/browse/showinfodata.do?b_query=link&code=${browseForm.code}&kind=${browseForm.kind}&query="+query;
   browseForm.submit();
} 
 function changesort()
{
   browseForm.action="/workbench/browse/showinfodata.do?b_search=link&code=${browseForm.code}&kind=${browseForm.kind}";
   browseForm.submit();
}
/*
function executeOutFile(){
	var hashvo = new ParameterSet();
	hashvo.setValue("userbase","${browseForm.userbase}");
	hashvo.setValue("code","${browseForm.code}");
	hashvo.setValue("where_n","${browseForm.ensql}");
	hashvo.setValue("orgtype","${browseForm.orgtype}");
	hashvo.setValue("roster","${browseForm.roster}");
	hashvo.setValue("checksort","1");
	var request=new Request({method:"post",asynchronous:false,onSuccess:showFieldList,functionId:"0521010019"},hashvo);
}*/
function showFieldList(outparamters){
	var outName=outparamters.getValue("outName");
	//var name=outName.substring(0,outName.length-1)+".xls";
    //window.open("/servlet/DisplayOleContent?filename="+name,"xls");
    window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"xls");
}
function openwin(url)
{
   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+(screen.availHeight-100)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
}
function selectQ()
   {
       var code="${browseForm.code}";
       var kind="${browseForm.kind}";
       var tablename="${browseForm.userbase}";
       var a_code="UN";
       if(kind=="2")
       {
          a_code="UN"+code;
       }else if(kind=="1")
       {
          a_code="UM"+code;
       }else if(kind=="1")
       {
          a_code="@K"+code;
       }else
       {
          a_code="UN"+code;
       }
         
       //var thecodeurl="/general/inform/search/gmsearch.do?b_query=link&type=1&a_code="+a_code+"&tablename="+tablename;
       var thecodeurl="/general/inform/search/generalsearch.do?b_query=link&type=1&a_code="+a_code+"&tablename="+tablename+"&fieldsetid=A01";
       var  return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:700px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no"); 
                   
      if(return_vo!=null){
            var expr= return_vo.expr;
            var factor=return_vo.factor;
            var o_obj=document.getElementById('factor');
            o_obj.value=factor;
            o_obj=document.getElementById('expr');
            o_obj.value=expr;
            document.getElementById('likeflag').value=return_vo.likeflag;
            browseForm.action="/workbench/browse/showinfodata.do?b_query=link&code=${browseForm.code}&kind=${browseForm.kind}&check=ok";
            browseForm.submit();
      } 
   }
   function clearQ()
   {
       browseForm.action="/workbench/browse/showinfodata.do?b_search=link&code=${browseForm.code}&kind=${browseForm.kind}&check=no&query=0";
       browseForm.submit();
   }
   function viewPhoto()
   {
       browseForm.action="/workbench/browse/showinfodata.do?b_view_photo=link&code=${browseForm.code}&kind=${browseForm.kind}";
       browseForm.target="nil_body";
       browseForm.submit();
   }   
   function winhrefOT(a0100,target,nbase)
{
   if(a0100=="")
      return false;
   var returnvalue="${browseForm.returnvalue}";
   if(returnvalue!="dxt")
     returnvalue="1";
   browseForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase="+nbase+"&a0100="+a0100+"&flag=notself&returnvalue=nuclear";
   browseForm.target=target;
   browseForm.submit();
}
function document.oncontextmenu() 
   { 
      //return  false; 
   } 
function showOrClose()
{
		var obj=eval("aa");
	    var obj3=eval("vieworhidd");
		//var obj2=eval("document.browseForm.isShowCondition");
	    if(obj.style.display=='none')
	    {
    		obj.style.display='block'
        	obj3.innerHTML="<a href=\"javascript:showOrClose();\"> 查询隐藏 </a>";
    	}
    	else
	    {
	    	obj.style.display='none';
	    	obj3.innerHTML="<a href=\"javascript:showOrClose();\"> 查询显示 </a>";
	    	
    	}
}
function fieldCheckBox(hiddenname,id,obj)
{
   if(obj.checked==true)
   {
      var vo=document.getElementById(hiddenname);
      var iv=obj.value;
      var value=vo.value;
      value="`"+value+"`";
      if(value.indexOf("`"+iv+"`")==-1)
      {
         vo.value=vo.value+"`"+iv;
      }
   }else
   {
      var vo=document.getElementById(hiddenname);
      var voID=document.getElementsByName(id);      
      var len=voID.length;    
      var value="";
      for (i=0;i<len;i++)
      {
         if(voID[i].checked)
          {
             
            value=value+"`"+voID[i].value;
          }
       }
       vo.value=value;
   }
}
function selectCheckBox(obj,hiddname)
{
   if(obj.checked==true)
   {
      var vo=document.getElementById(hiddname);
      var Info=eval("info_cue1");	
	   Info.style.display="block";
      if(vo)
         vo.value="1";
   }else
   {
         var vo=document.getElementById(hiddname);
         var Info=eval("info_cue1");	
	   Info.style.display="none";
      if(vo)
         vo.value="0";
   }

}
function MusterInitData()
{
	   var vo=document.getElementsByName("querlike2");
	   var obj=vo[0];
	   if(obj.checked==true)
	   {
          
          var Info=eval("info_cue1");	
	      Info.style.display="block";
          
       }else
       {
         
         var Info=eval("info_cue1");	
	     Info.style.display="none";
         
   }
}
function returnTOWizard()
  {
     browseForm.action="/templates/attestation/police/wizard.do?br_postwizard=link";
     browseForm.target="il_body";
     browseForm.submit();
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
<html:form action="/workbench/browse/showinfodatanuclear">
<html:hidden name="browseForm" property="factor" styleId="factor" styleClass="text"/>
<html:hidden name="browseForm" property="expr" styleId="expr" styleClass="text"/>  
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" >

<tr>
   <td nowrap>
<%
	
	int flag=0;
	int j=0;
	int n=0;
%>
 <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id='aa'>
<tr>
     <td align='center'>
     <br>
 </td>
</tr>
<tr>
    <td width="100%" nowrap>
     <div class="fixedDiv2">
      <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" >
           <tr class="fixedHeaderTr">
                     
           <logic:iterate id="info"    name="browseForm"  property="browsefields">   
             
              <td align="center" class="TableRow" nowrap>
                   <bean:write  name="info" property="itemdesc" filter="true"/>&nbsp;              
              </td>
            
           </logic:iterate> 	
		    <logic:notEqual name="browseForm" property="cardid" value="-1">
             <td align="center" class="TableRow" nowrap>
		     	<bean:message key="tab.base.info"/>          	
             </td>	 
             </logic:notEqual>               
            <td align="center" class="TableRow" nowrap>
		     	<bean:message key="tab.synthesis.info"/>          	
	    	</td>     	    	    		        	        	        
           </tr>
           <hrms:paginationdb id="element" name="browseForm" sql_str="browseForm.strsql" table="" where_str="browseForm.cond_str" columns="browseForm.columns" order_by="order by a0000" page_id="pagination" pagerows="${browseForm.pagerows}">
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
	     <bean:define id="nbase" name="element" property="nbase"/>	    
	      <%
    	   LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
    	   String a0100_encrypt=(String)abean.get("a0100");              	            	   
          request.setAttribute("a0100_encrypt",PubFunc.encrypt(a0100_encrypt));    	                           

	      %>
        
	     <logic:iterate id="info"    name="browseForm"  property="browsefields">   
	     	 	<logic:notEqual  name="info" property="itemtype" value="N">               
                    <td align="left" class="RecordRow" nowrap>&nbsp;        
                </logic:notEqual>
                
                <logic:equal  name="info" property="itemtype" value="N">               
                    <td align="right" class="RecordRow" nowrap> &nbsp;       
                </logic:equal> 
                   
                <logic:equal  name="info" property="codesetid" value="0">   
                	<logic:notEqual name="info"   property="itemid" value="a0101">        
                     	<bean:write  name="element" property="${info.itemid}" filter="true"/>&nbsp;
                   	</logic:notEqual>
                   
                   	<logic:equal name="info"   property="itemid" value="a0101">  
          	   			<a href="###" onclick="winhrefOT('${a0100}','nil_body','${nbase}');"> 
          	   			 	<bean:write name="element" property="a0101" filter="true"/>
          	   			</a>
          	   			&nbsp;
          	   	 	</logic:equal>
                 </logic:equal>
                 
                 <logic:notEqual  name="info" property="codesetid" value="0"> 
                  
                 	<logic:notEqual  name="info"   property="itemid" value="e01a1">  
                  		<logic:notEqual  name="info"   property="itemid" value="a0101">  
                   			<logic:equal name="info" property="codesetid" value="UM">
                        		<hrms:codetoname codeid="UM" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${browseForm.uplevel}"/>  	      
          	            		<bean:write name="codeitem" property="codename" />&nbsp;          	           
                   			</logic:equal>
                   			
                   			<logic:notEqual name="info" property="codesetid" value="UM">
                      			<hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
          	    	   			<bean:write name="codeitem" property="codename" />&nbsp;  
                   			</logic:notEqual>
          	     		</logic:notEqual>
          	    	</logic:notEqual>  
          	    	        	    
          	    	<logic:equal name="info"   property="itemid" value="e01a1"> 
                     		<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	         		<bean:write name="codeitem" property="codename" />&nbsp;           	                           		

          	   		</logic:equal>
          	       
                </logic:notEqual>  
              </td>
             </logic:iterate> 
		    <logic:notEqual name="browseForm" property="cardid" value="-1">
	    			 <td align="center" class="RecordRow" nowrap>&nbsp;
               			<a href="###" onclick='openwin("/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase=${nbase}&a0100=${a0100_encrypt}&inforkind=1&tabid=${browseForm.cardid}&multi_cards=-1");'>
               			<img src="../../images/table.gif" border="0"></a>
				     </td>	                
             </logic:notEqual>	                		
             <td align="center" class="RecordRow" nowrap>&nbsp;
             	<a href="###" onclick="winhrefOT('${a0100}','nil_body','${nbase}');"> 
          	   		<img src="../../images/view.gif" border="0">
            	 </a>            		 	            	     	   
	      </td>    	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>        
</table>
</div>
<table width="100%"  align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            
					<hrms:paginationtag name="browseForm"
								pagerows="${browseForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="browseForm" property="pagination" nameId="browseForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>

    </td>
</tr>
</table>

<html:hidden name="browseForm" property="likeflag"/>
</html:form>
<script language="javascript">
 //MusterInitData();	
</script>