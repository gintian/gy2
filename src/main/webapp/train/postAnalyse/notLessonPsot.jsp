<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
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
   var returnvalue="train_no_post_lesson"; 
   trainStationForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase=${trainStationForm.dbpre}&a0100="+a0100+"&flag=notself&returnvalue="+returnvalue;
   trainStationForm.target="il_body";
   trainStationForm.submit();
}
function change()
{ 
   trainStationForm.action="/train/postAnalyse/notaccordpost.do?b_query=link"
   trainStationForm.submit();
}
function intoclass(){
	var tablevos=document.getElementsByName("checkboxid");
	var nid="";
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].type=="checkbox"){
	     	if(tablevos[i].checked){
	     		nid += tablevos[i].value+",";
	     		
	     	}
		 }
     }
    if(nid==null||nid.length<=0){
        alert(TRAIN_LESSON_SELECT_PERSON);
    	return;
    }

    var hashvo = new ParameterSet();
    hashvo.setValue("a0100",nid);
    var request=new Request({method:"post",asynchronous:false,onSuccess:checkinto,functionId:"202005101401"},hashvo);
    
    }
function checkinto(outparamters)
{
     var f=outparamters.getValue("f");
     var R4002=outparamters.getValue("R4002");
     var a0100=outparamters.getValue("a0100");
     var r3130=outparamters.getValue("r3130");
     a0100=getEncodeStr(a0100);
     if(f=="true"){
     	var theurl="/train/postAnalyse/notaccordpostwork/searchClass.do?b_intoclass=link`a0100="+a0100;
     	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    	var return_vo= window.showModalDialog(iframe_url, 'trainClass_win2', 
       				"dialogWidth:700px; dialogHeight:620px;resizable:no;center:yes;scroll:yes;status:no");	

    	if(return_vo == null || return_vo.length <1)
        	return;
    	 var hashvo=new ParameterSet();
    	hashvo.setValue("personstr",a0100);
    	hashvo.setValue("classid",return_vo);
    	hashvo.setValue("msg","3");
    	var request=new Request({method:'post',asynchronous:false,onSuccess:savePerson,functionId:'2020040012'},hashvo);
    	function savePerson(outparamters){
    		var flag=outparamters.getValue("flag");
    		if(flag=="true"){
        		trainStationForm.action="/train/postAnalyse/notaccordpostwork/searchClass.do?b_saveclass=link&a0100="+a0100+"&r3101="+return_vo;
 				trainStationForm.submit();
    		}else{
				alert(flag);
            }
    	}
  	} else {
		alert(R4002+TRAIN_ANALYSE_SELECT_CLASS1+r3130+TRAIN_ANALYSE_SELECT_CLASS2);
	 }
}

function selectQ()
{
       var tablename="Usr";
       var thecodeurl="/train/postAnalyse/generalsearch.do?b_query=link&type=1&a_code=${trainStationForm.code }&tablename="+tablename+"&fieldsetid=K01";
       var dw=700,dh=430,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
       var  return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:700px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no"); 
      if(return_vo!=null){
            var expr= return_vo.expr;
            var factor=return_vo.factor;
            var history=return_vo.history;
            var likeflag=return_vo.likeflag;            
            var hashvo = new ParameterSet();
            hashvo.setValue("sfactor",factor);
	        hashvo.setValue("sexpr",expr);
	        hashvo.setValue("history",history);
	        hashvo.setValue("likeflag",likeflag);
	        hashvo.setValue("userbase","${trainStationForm.dbpre}");
	        var request=new Request({method:"post",asynchronous:false,onSuccess:selectRes,functionId:"2020051005"},hashvo);
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
   function returned(){
	   trainStationForm.action="/train/postAnalyse/notaccordpost.do?b_init=link&query=1";
	   trainStationForm.submit();
}
   function intoclass2(id){
	    var hashvo = new ParameterSet();
	    hashvo.setValue("a0100",id);
	    var request=new Request({method:"post",asynchronous:false,onSuccess:checkinto,functionId:"202005101401"},hashvo);
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
<html:form action="/train/postAnalyse/notaccordpostwork">
<html:hidden name="trainStationForm" property="sfactor" styleId="sfactor"/>
<html:hidden name="trainStationForm" property="sexpr" styleId="sexpr"/>
<html:hidden name="trainStationForm" property="history" styleId="history"/>
<html:hidden name="trainStationForm" property="likeflag" styleId="likeflag"/>
<html:hidden name="trainStationForm" property="chwhere" styleId="chwhere"/>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" >

<tr>
    <td width="100%" nowrap>
     <div class="fixedDiv2">
      <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
           <tr class="fixedHeaderTr"> 
           <td width="20" align="center" class="TableRow" style="border-left: none;border-top: none;" nowrap>
    			<input type="checkbox" name="selbox" onclick="batch_select_all(this);" title='<bean:message key="label.query.selectall"/>'>                              
	      	</td>          
           <logic:iterate id="info"    name="trainStationForm"  property="browsefields">   
              <logic:equal name="info" property="visible" value="true">
              <td align="center" class="TableRow" style="border-left: none;border-top: none;"  nowrap>
                   <bean:write  name="info" property="itemdesc" filter="true"/>&nbsp;              
              </td>
              </logic:equal>
             </logic:iterate> 	
            <td align="center" class="TableRow" style="border-left: none;border-top: none;border-right: none;"  nowrap>
		     	<bean:message key="system.infor.oper"/>          	
	    	</td>     	    	    		        	        	        
           </tr>
        <hrms:paginationdb id="element" name="trainStationForm" sql_str="trainStationForm.sqlstr" table="" 
        where_str="trainStationForm.where" columns="trainStationForm.cloumn" order_by="order by AA.a0100"
         page_id="pagination" pagerows="${trainStationForm.pagerows}" keys="">
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
	     	String a01001 = SafeCode.encode(PubFunc.encrypt(a0100.toString()));
	     %>
        	<td align="center" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>
    			<input type="checkbox" name="checkboxid" value="<%=a01001 %>">                                 
	      		</td> 
	     <logic:iterate id="info"    name="trainStationForm"  property="browsefields">   
	     	   <logic:equal name="info" property="visible" value="true">
	     	   	  <logic:notEqual name="info"   property="itemtype" value="M">
                  <logic:notEqual  name="info" property="itemtype" value="N">               
                    <td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>&nbsp;        
                  </logic:notEqual>
                  <logic:equal  name="info" property="itemtype" value="N">               
                    <td align="right" class="RecordRow" style="border-left: none;border-top: none;"  nowrap> &nbsp;       
                  </logic:equal>  
                  </logic:notEqual>  
                  <logic:equal  name="info" property="codesetid" value="0">   
                   <logic:notEqual name="info"   property="itemid" value="a0101">
                   <logic:notEqual name="info"   property="itemtype" value="M">        
                     <bean:write  name="element" property="${info.itemid}" filter="true"/>&nbsp;
                   </logic:notEqual>
                   <logic:equal name="info"   property="itemtype" value="M">        
                     <td align="left" class="RecordRow" style="border-left: none;border-top: none;"  nowrap>
                     	<div STYLE="width: 100px; overflow: hidden; text-overflow: ellipsis; white-space:nowrap;">
		               		&nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
		               	</div>
                   </logic:equal>
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
                       <bean:define id="e01a1" name="element" property="e01a1"/>	
					     <%
					   		//liuy 2015-6-18 10345：培训管理-培训分析-岗位培训分析-不符合本岗位培训要求-培训分析报表-岗位名称（登记表空的）
					     	//String e01a11 = PubFunc.encrypt(e01a1.toString());
					     	String e01a11 = PubFunc.encrypt(e01a1.toString());
					     %>
                          <a href="###" onclick='openwin("/workbench/browse/showposinfo.do?b_browse=link&a0100=<%=e01a11 %>&npage=1");'>
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
		    	                		
             <td align="center" class="RecordRow" style="border-left: none;border-top: none;border-right: none;"  nowrap>&nbsp;
             	<a href="###" onclick="intoclass2('<%=a01001 %>');"> 
          	   		<img src="/images/edit.gif" border="0">
            	 </a>            		 	            	     	   
	      </td>    	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>        
</table>
</div>
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
<table  align="left">
          <tr>
            <td align="left">        
               
	 	    		<input type="button" value="<bean:message key='train.post.analyse.join.new.classes'/>" onclick="intoclass();" class="mybutton">

	 	    	<input type="button" value="<bean:message key='button.return'/>" onclick="returned();" class="mybutton">

                
            </td>
          </tr>          
</table>
    </td>
</tr>
</table>

</html:form>
