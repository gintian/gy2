<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.actionform.selfinfomation.SelfInfoForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%// 在标题栏显示当前用户和日期 2004-5-10 
			String userName = null;
			String css_url = "/css/css1.css";
			UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			String codevalue="";
	        String code="";
			if (userView != null) {
				css_url = userView.getCssurl();
				if (css_url == null || css_url.equals(""))
					css_url = "/css/css1.css";
			    if(!userView.isSuper_admin())
	            { 
	               code=userView.getManagePrivCode();
	               codevalue=userView.getManagePrivCodeValue();
	             } 
				//out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
			}
			
			String a0100=request.getParameter("a0100");
			String i9999=request.getParameter("pi9999");
			String dbpre=request.getParameter("dbpre");
%>
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
<script type="text/javascript" src="/js/dict.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
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
	function document.oncontextmenu() 
   { 
      //return　false; 
   } 
</script>
<%String orgtemp = "";
			String orgtempview = "";
			String postemp = "";
			String postempview = "";
			String kktemp = "";
			String kktempview = "";

			String birthdayfield = "";
			String agefield = "";
			String workagefield = "";
			String postagefield = "";
			String axfield = "";
			String axviewfield = "";
			int rowss;

			%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
   var date_desc;
  
   function setSelectValue()
   {
     if(date_desc)
     {
       date_desc.value=$F('date_box');
       Element.hide('date_panel');
       event.srcElement.releaseCapture();   
     }
   }
   
   function showDateSelectBox(srcobj)
   {
       //if(event.button==2)
       //{
          date_desc=srcobj;
          Element.show('date_panel');   
          var pos=getAbsPosition(srcobj);
	  with($('date_panel'))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
		style.posTop=pos[1]-1+srcobj.offsetHeight;
		style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
          }                 
       //}
   }
   function trun()
   {
      parent.parent.menupnl.toggleCollapse(true);
   }
</script>
<script language="javascript">
  function validate()
  {
    var tag=true;    
     <logic:iterate  id="element"    name="selfInfoForm"  property="infoFieldList" indexId="index"> 
      <logic:equal name="element" property="visible" value="true">   
      		<bean:define id="fl" name="element" property="fillable"/>
		    <bean:define id="desc" name="element" property="itemdesc"/>
		     var valueInputs=document.getElementsByName("<%="infoFieldList["+index+"].value"%>");
        	  var dobj=valueInputs[0];
        	  if("${fl}"=="true"&&dobj.value.length<1){
          	  	alert("${desc}"+"必须填写！");
				return ;
          	  }
          <logic:equal name="element" property="itemid" value="b0110">
              <%orgtemp="infoFieldList["+index+"].value";%>  
              <%orgtempview="infoFieldList["+index+"].viewvalue";%>         
          </logic:equal>
          <logic:equal name="element" property="itemid" value="e0122">
                 <%postemp="infoFieldList["+index+"].value";%>
                  <%postempview="infoFieldList["+index+"].viewvalue";%>
          </logic:equal>
          <logic:equal name="element" property="itemid" value="e01a1">
                 <%kktemp="infoFieldList["+index+"].value";%>
                  <%kktempview="infoFieldList["+index+"].viewvalue";%>
          </logic:equal>
          

          <logic:equal name="element" property="itemid" value="${selfInfoForm.birthdayfield}">
                  <%birthdayfield="infoFieldList["+index+"].value";%>
             </logic:equal>
           <logic:equal name="element" property="itemid" value="${selfInfoForm.agefield}">
                  <%agefield="infoFieldList["+index+"].value";%>
             </logic:equal>
               <logic:equal name="element" property="itemid" value="${selfInfoForm.workagefield}">
                  <%workagefield="infoFieldList["+index+"].value";%>
             </logic:equal>
               <logic:equal name="element" property="itemid" value="${selfInfoForm.postagefield}">
                  <%postagefield="infoFieldList["+index+"].value";%>
             </logic:equal>
             <logic:equal name="element" property="itemid" value="${selfInfoForm.axfield}">
                  <%axfield="infoFieldList["+index+"].value";%>
                 <%axviewfield="infoFieldList["+index+"].viewvalue";%>
             </logic:equal>
          
          
        <logic:equal name="element" property="itemtype" value="D">   
         
          tag= checkDate(dobj) && tag;      
	  if(tag==false)
	  {
	    dobj.focus();
	    return false;
	  }
        </logic:equal> 
        <logic:equal name="element" property="itemtype" value="N"> 
           <logic:lessThan name="element" property="decimalwidth" value="1"> 
             var valueInputs=document.getElementsByName("<%="infoFieldList["+index+"].value"%>");
             var dobj=valueInputs[0];
              tag=checkNUM1(dobj) &&  tag ;  
	      if(tag==false)
	      {
	        dobj.focus();
	        return false;
	      }
	    </logic:lessThan>
	    <logic:greaterThan name="element" property="decimalwidth" value="0"> 
	     var valueInputs=document.getElementsByName('<%="infoFieldList["+index+"].value"%>');
             var dobj=valueInputs[0];
             tag=checkNUM2(dobj,${element.itemlength},${element.decimalwidth}) &&  tag ;  
              if(tag==false)
	      {
	        dobj.focus();
	        return false;
	      }
	    </logic:greaterThan>
	   </logic:equal>  
	  </logic:equal>  
     </logic:iterate>    
     return tag;   
  }
  
</script>
<script language="javascript">
  function exeButtonAction(actionStr,target_str)
   {
       //alert(actionStr);
       target_url=actionStr;       
       window.open(target_url,target_str); 
   }
  function getchangeposun(outparamters)
    {
      var pretype=outparamters.getValue("pretype");
      var orgparentcode=outparamters.getValue("orgparentcode");
      
      var deptparentcode=outparamters.getValue("deptparentcode");
      var posparentcode=outparamters.getValue("posparentcode");
      AjaxBind.bind(selfInfoForm.orgparentcode,/*$('orgparentcode')*/orgparentcode);
      AjaxBind.bind(selfInfoForm.deptparentcode,/*$('deptparentcode')*/deptparentcode);
      AjaxBind.bind(selfInfoForm.posparentcode,/*$('posparentcode')*/posparentcode);
      var valueInputsun=document.getElementsByName("<%=postemp%>");
      var dobjun=valueInputsun[0];
      dobjun.value="";
      valueInputsun=document.getElementsByName("<%=postempview%>");
      dobjun=valueInputsun[0];
      dobjun.value="";
      var cc=document.getElementsByName("<%=kktemp%>"); 
      var dobjkk;
      if(cc.length>0){
      dobjkk=cc[0];
      dobjkk.value="";
      cc=document.getElementsByName("<%=kktempview%>");
      dobjkk=cc[0];
      dobjkk.value="";
      }
      //AjaxBind.bind(selfInfoForm.posparentcode,/*$('posparentcode')*/posparentcode);
      
  
  }
   function getchangeposum(outparamters)
   {
     
      var pretype=outparamters.getValue("pretype");
      var orgparentcode=outparamters.getValue("orgparentcode");
      var orgvalue=outparamters.getValue("orgvalue");
      if(orgvalue!=null && orgvalue.length>0)
      {

         var orgvalueview=outparamters.getValue("orgviewvalue");
         var valueInputsun=document.getElementsByName("<%=orgtemp%>");
         var dobjun=valueInputsun[0];
         dobjun.value=orgvalue;
         valueInputsun=document.getElementsByName("<%=orgtempview%>");
         dobjun=valueInputsun[0];
         dobjun.value=orgvalueview;
      }

      var valueInputskk=document.getElementsByName("<%=kktemp%>");
      var dobjkk;
      if(valueInputskk.length>0){
      dobjkk=valueInputskk[0];
      dobjkk.value="";
      valueInputskk=document.getElementsByName("<%=kktempview%>");
      dobjkk=valueInputskk[0];
      dobjkk.value="";
      }
      var deptparentcode=outparamters.getValue("deptparentcode");
      var posparentcode=outparamters.getValue("posparentcode");
      AjaxBind.bind(selfInfoForm.orgparentcode,/*$('orgparentcode')*/orgparentcode);
      if(deptparentcode!=null && deptparentcode.length>0)
         AjaxBind.bind(selfInfoForm.deptparentcode,/*$('deptparentcode')*/deptparentcode);
      AjaxBind.bind(selfInfoForm.posparentcode,/*$('posparentcode')*/posparentcode);
  
  }
   function getchangeposkk(outparamters)
   {
      var pretype=outparamters.getValue("pretype");
      var orgparentcode=outparamters.getValue("orgparentcode");
      var deptparentcode=outparamters.getValue("deptparentcode");
      var posparentcode=outparamters.getValue("posparentcode");
      var orgvalue=outparamters.getValue("orgvalue");
      if(orgvalue!=null && orgvalue.length>0)
      {
         var orgvalueview=outparamters.getValue("orgviewvalue");
         var valueInputsun=document.getElementsByName("<%=orgtemp%>");
         var dobjun=valueInputsun[0];
         dobjun.value=orgvalue;
         valueInputsun=document.getElementsByName("<%=orgtempview%>");
         dobjun=valueInputsun[0];
         dobjun.value=orgvalueview;
      }
       var deptvalue=outparamters.getValue("deptvalue");
      if(deptvalue!=null && deptvalue.length>0)
      {
         var deptviewvalue=outparamters.getValue("deptviewvalue");
         var valueInputsum=document.getElementsByName("<%=postemp%>");
         var dobjum=valueInputsum[0];
         dobjum.value=deptvalue;
         valueInputsum=document.getElementsByName("<%=postempview%>");
         dobjum=valueInputsum[0];
         dobjum.value=deptviewvalue;
      }
      AjaxBind.bind(selfInfoForm.orgparentcode,/*$('orgparentcode')*/orgparentcode);
      //AjaxBind.bind(selfInfoForm.deptparentcode,/*$('deptparentcode')*/deptparentcode);
      //AjaxBind.bind(selfInfoForm.posparentcode,/*$('posparentcode')*/posparentcode);
  
  }
   
   function changeOrg (pretype) {
	      var value = "";
	      if('b0110' == pretype) {
	          var unIdInputs = document.getElementsByName("<%=orgtempview%>");
	          var valueInputsun = document.getElementsByName("<%=orgtemp%>");
	          if(unIdInputs != null && unIdInputs != "undefined" && unIdInputs.length > 0)
	              value = unIdInputs[0].value;
	          
	          if(valueInputsun != null && valueInputsun != "undefined" && valueInputsun.length > 0) {
	              if(!value) {
	                  valueInputsun[0].value = "";
	                  changepos("2");
	              }
	          }
	          
	      } else if('e0122' == pretype) {
	          var umIdInputs = document.getElementsByName("<%=postempview%>");
	          var valueInputsum = document.getElementsByName("<%=postemp%>");
	          if(umIdInputs != null && umIdInputs != "undefined" && umIdInputs.length > 0)
	              value = umIdInputs[0].value;
	          
	          if(valueInputsum != null && valueInputsum != "undefined" && valueInputsum.length > 0) {
	              if(!value) {
	                  valueInputsum[0].value = "";
	                  changepos("1");
	              }
	          }
	          
	      } else if('e01a1' == pretype) {
	          var kkIdInputs = document.getElementsByName("<%=kktempview%>");
	          var valueInputskk = document.getElementsByName("<%=kktemp%>");
	          if(kkIdInputs != null && kkIdInputs != "undefined" && kkIdInputs.length > 0)
	              value = kkIdInputs[0].value;
	          
	          if(valueInputskk != null && valueInputskk != "undefined" && valueInputskk.length > 0) {
	              if(!value) {
	                  valueInputskk[0].value = "";
	              }
	          }
	      }
	  }
   
  function changepos(pretype)
  {   
	  if('0'==pretype)
	        pretype='@K';
	  
	  if('1'==pretype)
	        pretype='UM';
	  
	  if('2'==pretype)
	        pretype='UN';
	  
      var valueInputsun=document.getElementsByName("<%=orgtemp%>");
      var dobjun;
      var dobjum;
      var dobjkk;
      if(valueInputsun!=null&&valueInputsun!="undefined"&&valueInputsun.length>0)
        dobjun=valueInputsun[0];
      var valueInputsum=document.getElementsByName("<%=postemp%>");
      if(valueInputsun!=null&&valueInputsun!="undefined"&&valueInputsun.length>0)      
       dobjum=valueInputsum[0];      
      var valueInputskk=document.getElementsByName("<%=kktemp%>");
      if(valueInputsun!=null&&valueInputsun!="undefined"&&valueInputsun.length>0)
        dobjkk=valueInputskk[0];
      var hashvo=new ParameterSet();
      if(pretype!=null)
         hashvo.setValue("pretype",pretype);
      hashvo.setValue("orgparentcodestart",dobjun.value);
      hashvo.setValue("deptparentcodestart",dobjum.value);
      if(dobjkk!=null){
      hashvo.setValue("posparentcodestart",dobjkk.value);
      }else{
      hashvo.setValue("posparentcodestart","");
      }
      if(pretype=="UN")
      {
        var request=new Request({method:'post',onSuccess:getchangeposun,functionId:'02010001012'},hashvo);
      }
     if(pretype=="UM")
      {
        var request=new Request({method:'post',onSuccess:getchangeposum,functionId:'02010001012'},hashvo);
      }
      if(pretype=="@K")
      {
        var request=new Request({method:'post',onSuccess:getchangeposkk,functionId:'02010001012'},hashvo);
      }
  }
  function calculatebirthday(obj)
  {
    // alert(obj.value);
      var hashvo=new ParameterSet();
      hashvo.setValue("idcardvalue",obj.value);
      var request=new Request({method:'post',onSuccess:getBirthdayAge,functionId:'02010001013'},hashvo);
  }
  function getBirthdayAge(outparamters)
  {
     var birthdayvalue=outparamters.getValue("birthdayvalue");
     var agevalue=outparamters.getValue("agevalue");
      var axvalue=outparamters.getValue("axvalue");
     if(birthdayvalue!=null)
     {
         var valueInputs=document.getElementsByName("<%=birthdayfield%>");
         var dobj=valueInputs[0];
         if(dobj!=null){
         dobj.value=birthdayvalue;
         }
     }
     if(agevalue!=null)
     {
         var valueInputs=document.getElementsByName("<%=agefield%>");
         var dobj=valueInputs[0];
          if(dobj!=null)
         dobj.value=agevalue;
     }  
     if(axvalue!=null)
     {
         var valueInputs=document.getElementsByName("<%=axfield%>");
         var dobj=valueInputs[0];
         //alert(axvalue);
          if(dobj!=null)
         dobj.value=axvalue;
         if(axvalue==1)
         {
            var valueInputs=document.getElementsByName("<%=axviewfield%>");
            dobj=valueInputs[0];
            //alert(axvalue);
            if(dobj!=null)
            dobj.value="男";
         }else if(axvalue==2)
         {
            var valueInputs=document.getElementsByName("<%=axviewfield%>");
            dobj=valueInputs[0];
            //alert(axvalue);
            if(dobj!=null)
            dobj.value="女";
         }
     }        
     
  }
  function CalculateWorkDate(obj)
  {
      var hashvo=new ParameterSet();
      hashvo.setValue("workdatevalue",obj.value);
      var request=new Request({method:'post',onSuccess:getWorkAge,functionId:'02010001014'},hashvo);
  }
  function getWorkAge(outparamters)
  {
     var workagevalue=outparamters.getValue("workagevalue");
     if(workagevalue!=null)
     {
         var valueInputs=document.getElementsByName("<%=workagefield%>");
         var dobj=valueInputs[0];
         if(dobj!=null){
         dobj.value=workagevalue;
         <logic:equal name="selfInfoForm" property="workdatefield" value="${selfInfoForm.startpostfield}">
             valueInputs=document.getElementsByName("<%=postagefield%>");
             dobj=valueInputs[0];
             dobj.value=workagevalue;
         </logic:equal>   
         }
     }     
  }
  function CalculatePostAge(obj)
  {
      var hashvo=new ParameterSet();
      hashvo.setValue("postdatevalue",obj.value);
      var request=new Request({method:'post',onSuccess:getPostAge,functionId:'02010001015'},hashvo);
  }
  function getPostAge(outparamters)
  {
     var postagevalue=outparamters.getValue("postagevalue");
     if(postagevalue!=null)
     {
         var valueInputs=document.getElementsByName("<%=postagefield%>");
         var dobj=valueInputs[0];
         if(dobj!=null){
         dobj.value=postagevalue;  
         <logic:equal name="selfInfoForm" property="startpostfield" value="${selfInfoForm.workdatefield}">
             valueInputs=document.getElementsByName("<%=workagefield%>");
             dobj=valueInputs[0];
             dobj.value=workagevalue;
         </logic:equal>   
     }    
     } 
  }
  
  
    function proves(){
if(validate()){
if(confirm("一旦报批数据，将不能进行修改，是否报批?")){
selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_appeals=link";
selfInfoForm.submit();
}else{
return;
}
}
}


  function savesub(){
if(validate()){

selfInfoForm.action="/workbench/info/leader/showinfodata.do?b_save=link&a0100=<%=a0100 %>&pi9999=<%=i9999 %>&dbpre=<%=dbpre %>";
selfInfoForm.submit();
}else{
return;
}

}

function returnback(){
	selfInfoForm.action="/workbench/info/leader/showinfodata.do?b_leader=link";
	selfInfoForm.submit();
}

function save(){
if(validate()){
selfInfoForm.action="/workbench/info/editselfinfo.do?b_save=link";
selfInfoForm.submit();
}else{
return;
}

}
function checkValue(obj,itemlength,decimalwidth)
  {
     if(decimalwidth=='')
      return true;
     if(itemlength=='')
      return true;
     var t_len=obj.value;
     if(t_len!="")
     {
        var decimalw=parseInt(decimalwidth,10);	
        var itemlen=parseInt(itemlength,10);	
        var inde=t_len.indexOf(".");
        if(inde==-1)
        {
          if(t_len.length>itemlen)
          {
            alert("整数位长度超过定义"+itemlen+",请修改！");
            obj.focus(); 
            return false;
          }
        }else
        {
           var q_srt=t_len.substring(0,inde);
           var n_srt=t_len.substring(inde+1);           
           if(q_srt.length>itemlen)
           {
             alert("整数位长度超过定义"+itemlen+",请修改！");
             obj.focus(); 
             return false;
           }else if(n_srt.length>decimalw)
           {
              alert("小数位长度超过定义"+decimalw+",请修改！");
              obj.focus(); 
              return false;
           }
        }
     }
  }  


function savere(){
if(validate()){
selfInfoForm.action="/workbench/info/editselfinfo.do?b_savesubre=link";
selfInfoForm.submit();
}else{
return;
}

}

function change() {
 	var list = document.getElementById("list");
 	va = list.value;
 	if (va == "A01") {
 		selfInfoForm.action = "/workbench/info/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname=A01&isAppEdite=1";
 	} else if(va == "A00"){
 		selfInfoForm.action = "/workbench/media/searchmediainfolist.do?b_search=link&setname="+va+"&setprv=2&flag=notself&returnvalue=${selfInfoForm.returnvalue}&userbase=<bean:write name="selfInfoForm" property="userbase"/>&isAppEdite=1";
 	}else {
 	
 	selfInfoForm.action = "/workbench/info/searchselfdetailinfo.do?b_searchsort=search&setname="+va+"&flag=noself&isAppEdite=1";
 	}
 	selfInfoForm.target="mil_body";
	selfInfoForm.submit();
}  

</script>
<%int i = 0;
			int flag = 0;

			%>
<hrms:themes></hrms:themes>
<style>
<!--
  .width150 {width:200;margin-left:-4px;}
-->
</style>

<body onLoad="//parent.mil_menu.location.reload()" style="padding-top:10px;">
	<html:form action="/workbench/info/editselfinfo" onsubmit="return validate()">
	
		<%SelfInfoForm selfInfoForm = (SelfInfoForm) session
					.getAttribute("selfInfoForm");
			rowss = Integer.parseInt(selfInfoForm.getRownums());
			if (selfInfoForm.getInfoFieldList().size() > 0) {%>
		<table width="85%" border="0" cellspacing="1" align="center" cellpadding="1">
			<html:hidden name="selfInfoForm" property="userbase" />
			<html:hidden name="selfInfoForm" property="actiontype" />
			<html:hidden name="selfInfoForm" property="a0100" />
			<html:hidden name="selfInfoForm" property="i9999" />


			<html:hidden name="selfInfoForm" property="orgparentcode" />
			<html:hidden name="selfInfoForm" property="deptparentcode" />
			<html:hidden name="selfInfoForm" property="posparentcode" />

			<logic:iterate id="element" name="selfInfoForm" property="infoFieldList" indexId="index">
			<logic:equal name="element" property="visible" value="true">   
				<logic:equal name="element" property="priv_status" value="1">
					<logic:equal name="element" property="codesetid" value="0">
						<logic:equal name="element" property="itemtype" value="D">
							<%if (flag == 0) {
					if (i % 2 == 0) {

					%>
							<tr class="trShallow1">
								<%} else {%>
							<tr class="trDeep1">
								<%}
					i++;
					flag = rowss;
				} else {
					flag = 0;
				}%>
								<td align="right" width="250" valign="top" nowrap>
									&nbsp;&nbsp;&nbsp;&nbsp;								
									
									<hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>									
									&nbsp;
								</td>
								<td align="left" nowrap valign="middle">
									&nbsp;
									<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>'  readonly="true" styleClass="textColorRead" maxlength="${element.itemlength}" />
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>
								<%if (flag == 0) {%>
							</tr>
							<%} else {%>
							<logic:equal name="element" property="rowflag" value="${index}">
								<td colspan="2">
								</td>
								</tr>
							</logic:equal>
							<%}%>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="A">
							<%if (flag == 0) {
					if (i % 2 == 0) {

					%>
							<tr class="trShallow1">
								<%} else {%>
							<tr class="trDeep1">
								<%}
					i++;
					flag = rowss;
				} else {
					flag = 0;
				}%>
								<td align="right" nowrap valign="top">
									&nbsp;&nbsp;&nbsp;&nbsp;<hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>									
									&nbsp;
								</td>
								<td align="left" nowrap valign="middle">
									&nbsp;
									<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true" onclick="Element.hide('dict');" styleClass="textColorRead" maxlength="${element.itemlength}" />
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>
								<%if (flag == 0) {%>
							</tr>
							<%} else {%>
							<logic:equal name="element" property="rowflag" value="${index}">
								<td colspan="2">
								</td>
								</tr>
							</logic:equal>
							<%}%>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="N">
							<%if (flag == 0) {
					if (i % 2 == 0) {

					%>
							<tr class="trShallow1">
								<%} else {%>
							<tr class="trDeep1">
								<%}
					i++;
					flag = rowss;
				} else {
					flag = 0;
				}%>
								<td align="right" nowrap  valign="top">
									&nbsp;&nbsp;&nbsp;&nbsp;<hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>									
									&nbsp;
								</td>
								<td align="left" nowrap valign="middle">
									&nbsp;
									<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true" styleClass="textColorRead" maxlength="${element.itemlength + element.decimalwidth + 1}" onclick="Element.hide('dict');" onblur="checkValue(this,'${element.itemlength}','${element.decimalwidth}');"/>
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>
								<%if (flag == 0) {%>
							</tr>
							<%} else {%>
							<logic:equal name="element" property="rowflag" value="${index}">
								<td colspan="2">
								</td>
								</tr>
							</logic:equal>
							<%}%>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="M">
							<%if (flag == 0) {
					if (i % 2 == 0) {

					%>
							<tr class="trShallow1">
								<%} else {%>
							<tr class="trDeep1">
								<%}
					i++;
					flag = rowss;%>
								<td align="right" nowrap  valign="top">
									&nbsp;&nbsp;&nbsp;&nbsp;<hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>								
									&nbsp;
								</td>
								<td align="left" nowrap valign="middle" colspan="3">
									&nbsp;
									<html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true" rows="10" cols="66" styleClass="textColorRead" onclick="Element.hide('dict');"/>
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>
								<%} else {
					flag = 0;%>
								<td colspan="2">
								</td>
								</td>

								<%if (flag == 0) {
						if (i % 2 == 0) {

						%>
							<tr class="trShallow1">
								<%} else {%>
							<tr class="trDeep1">
								<%}
						i++;
						flag = rowss;
					} else {
						flag = 0;
					}%>
								<td align="right" nowrap valign="top">
									&nbsp;&nbsp;&nbsp;&nbsp;<hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>									
									&nbsp;
								</td>
								<td align="left" nowrap valign="middle" colspan="3">
									&nbsp;
									<html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true" rows="10" cols="66" styleClass="textColorRead" onclick="Element.hide('dict');"/>
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>

								<%}%>

								<%flag = 0;%>

							</tr>
						</logic:equal>
					</logic:equal>
					<logic:notEqual name="element" property="codesetid" value="0">
						<%if (flag == 0) {
					if (i % 2 == 0) {

					%>
						<tr class="trShallow1">
							<%} else {%>
						<tr class="trDeep1">
							<%}
					i++;
					flag = rowss;
				} else {
					flag = 0;
				}%>
							<td align="right" nowrap valign="top">
								&nbsp;&nbsp;&nbsp;&nbsp;
								<hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>								
								&nbsp;
							</td>
							<td align="left" nowrap valign="middle">
								<html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />
								&nbsp;
								<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>' readonly="true" styleClass="textColorRead" onclick="Element.hide('dict');"/>
								<logic:equal name="element" property="fillable" value="true">
									<font color="red">*</font>
								</logic:equal>
							</td>
							<%if (flag == 0) {%>
						</tr>
						<%} else {%>
						<logic:equal name="element" property="rowflag" value="${index}">
							<td colspan="2">
							</td>
							</tr>
						</logic:equal>
						<%}%>
					</logic:notEqual>

				</logic:equal>






				<logic:equal name="element" property="priv_status" value="2">				
					<logic:equal name="element" property="codesetid" value="0">
						<logic:equal name="element" property="itemtype" value="D">
							<%if (flag == 0) {
					if (i % 2 == 0) {

					%>
							<tr class="trShallow1">
								<%} else {%>
							<tr class="trDeep1">
								<%}
					i++;
					flag = rowss;
				} else {
					flag = 0;
				}%>
								<td align="right" nowrap valign="top">
									&nbsp;&nbsp;&nbsp;&nbsp;
									<hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>									
									&nbsp;
								</td>
								<td align="left"  nowrap valign="middle">
            <logic:notEmpty name="selfInfoForm" property="workdatefield">
                 <logic:equal name="selfInfoForm" property="workdatefield" value="${element.itemid}">
                     &nbsp;<input type="text" name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />" class="width150 textColorWrite" extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate"  onchange="CalculateWorkDate(this)" /> 
                 </logic:equal>
                <logic:notEqual name="selfInfoForm" property="workdatefield" value="${element.itemid}">
                   <logic:notEmpty name="selfInfoForm" property="startpostfield">
                        <logic:equal name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                            &nbsp;<input type="text" name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />" class="width150 textColorWrite" extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate" onchange="CalculatePostAge(this)"/>
                        </logic:equal>
                        <logic:notEqual name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                          &nbsp;<input type="text" name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />" class="width150 textColorWrite" extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate"/>
                        </logic:notEqual>
                   </logic:notEmpty>
                  <logic:empty name="selfInfoForm" property="startpostfield">
                    &nbsp;<input type="text" name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />" class="width150 textColorWrite" extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate"/> 
                   </logic:empty>
                </logic:notEqual>
            </logic:notEmpty>
            <logic:empty name="selfInfoForm" property="workdatefield">
               <logic:notEmpty name="selfInfoForm" property="startpostfield">
                 <logic:equal name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                     &nbsp;<input type="text" name='<%="infoFieldList["+index+"].value"%>' onblur="CalculatePostAge(this)" value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />" class="width150 textColorWrite" extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate"/>&nbsp;&nbsp;&nbsp;&nbsp;  
                 </logic:equal>
                <logic:notEqual name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                  &nbsp;<input type="text" name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />" class="width150 textColorWrite" extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate"/>
                </logic:notEqual>
              </logic:notEmpty>
              <logic:empty name="selfInfoForm" property="startpostfield">
                 &nbsp; <input type="text" name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />" class="width150 textColorWrite" extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate"/>
              </logic:empty>
            </logic:empty>
             <logic:equal name="element"  property="fillable" value="true">
               <font color="red">*</font>
             </logic:equal>
         </td> 
								<%if (flag == 0) {%>
							</tr>
							<%} else {%>
							<logic:equal name="element" property="rowflag" value="${index}">
								<td colspan="2">
								</td>
								</tr>
							</logic:equal>
							<%}%>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="A">
							<%if (flag == 0) {
					if (i % 2 == 0) {

					%>
							<tr class="trShallow1">
								<%} else {%>
							<tr class="trDeep1">
								<%}
					i++;
					flag = rowss;
				} else {
					flag = 0;
				}%>
								<td align="right" nowrap valign="top">
									&nbsp;&nbsp;&nbsp;&nbsp;
									<hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>									
									&nbsp;
								</td>
								<td align="left" nowrap valign="middle">
									<logic:notEmpty name="selfInfoForm" property="idcardfield">
										<logic:equal name="selfInfoForm" property="idcardfield" value="${element.itemid}">
            
                   &nbsp;<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleClass="textColorWrite" maxlength="${element.itemlength}" onblur="calculatebirthday(this)" onclick="Element.hide('dict');"/>
										</logic:equal>
										<logic:notEqual name="selfInfoForm" property="idcardfield" value="${element.itemid}">
                   &nbsp;<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleClass="textColorWrite" maxlength="${element.itemlength}" onclick="Element.hide('dict');"/>
										</logic:notEqual>
									</logic:notEmpty>
									<logic:empty name="selfInfoForm" property="idcardfield">
                 &nbsp;<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleClass="textColorWrite" maxlength="${element.itemlength}" onclick="Element.hide('dict');"/>
									</logic:empty>
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>
								<%if (flag == 0) {%>
							</tr>
							<%} else {%>
							<logic:equal name="element" property="rowflag" value="${index}">
								<td colspan="2">
								</td>
								</tr>
							</logic:equal>
							<%}%>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="N">
							<%if (flag == 0) {
					if (i % 2 == 0) {

					%>
							<tr class="trShallow1">
								<%} else {%>
							<tr class="trDeep1">
								<%}
					i++;
					flag = rowss;
				} else {
					flag = 0;
				}%>
								<td align="right" nowrap valign="top">
									&nbsp;&nbsp;&nbsp;&nbsp;
									<hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>									
									&nbsp;
								</td>
								<td align="left" nowrap valign="middle">
								    <logic:greaterThan name="element" property="decimalwidth" value="0">
								        &nbsp;<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');" styleClass="textColorWrite" maxlength="${element.itemlength + element.decimalwidth +1}" onblur="checkValue(this,'${element.itemlength}','${element.decimalwidth}');"/>
								    </logic:greaterThan>
								    <logic:lessEqual name="element" property="decimalwidth" value="0">
								       &nbsp;<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');" styleClass="textColorWrite" maxlength="${element.itemlength}" onblur="checkValue(this,'${element.itemlength}','${element.decimalwidth}');"/>
								    </logic:lessEqual>
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>
								<%if (flag == 0) {%>
							</tr>
							<%} else {%>
							<logic:equal name="element" property="rowflag" value="${index}">
								<td colspan="2">
								</td>
								</tr>
							</logic:equal>
							<%}%>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="M">
							<%if (flag == 0) {
					if (i % 2 == 0) {

					%>
							<tr class="trShallow1">
								<%} else {%>
							<tr class="trDeep1">
								<%}
					i++;
					flag = rowss;%>
								<td align="right" nowrap valign="top">
									&nbsp;&nbsp;&nbsp;&nbsp;
									<hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>									
									&nbsp;
								</td>
								<td align="left" nowrap valign="middle" colspan="3">
									&nbsp;
									<html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');" rows="10" cols="66" styleClass="textColorWrite" />
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>
								<%} else {
					flag = 0;%>
								<td colspan="2">
								</td>
								</td>

								<%if (flag == 0) {
						if (i % 2 == 0) {

						%>
							<tr class="trShallow1">
								<%} else {%>
							<tr class="trDeep1">
								<%}
						i++;
						flag = rowss;
					} else {
						flag = 0;
					}%>
								<td align="right" nowrap valign="top">
									&nbsp;&nbsp;&nbsp;&nbsp;
									<hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>									
									&nbsp;
								</td>
								<td align="left" nowrap valign="middle" colspan="3">
									&nbsp;
									<html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');" rows="10" cols="66" styleClass="textColorWrite" />
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>

								<%}%>

								<%flag = 0;%>

							</tr>
						</logic:equal>
					</logic:equal>
					<logic:notEqual name="element" property="codesetid" value="0">
						<%if (flag == 0) {
					if (i % 2 == 0) {

					%>
						<tr class="trShallow1">
							<%} else {%>
						<tr class="trDeep1">
							<%}
					i++;
					flag = rowss;
				} else {
					flag = 0;
				}%>
							<td align="right"  valign="top">
								&nbsp;&nbsp;&nbsp;&nbsp;
								<hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>								
								&nbsp;
							</td>
							<td align="left" nowrap valign="middle">
								<logic:equal name="element" property="itemid" value="b0110">
									<html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleId="b0110" onchange="changepos('${element.codesetid}')" />  
                  
                      &nbsp;<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>' styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);changeOrg('b0110');" onkeyup="addDict('${element.codesetid}','${element.itemid}',this);" onkeydown="return inputType2(this,event)" onclick="styleDisplay(this);"/>
									<img src="/images/code.gif" onclick='javascript:openInputCodeDialogOrgInputPos("${element.codesetid}","<%="infoFieldList["+index+"].viewvalue"%>",selfInfoForm.orgparentcode.value,"1");' />&nbsp;
                 </logic:equal>
								<logic:equal name="element" property="itemid" value="e0122">
									<html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleId="e0122" onchange="changepos('${element.codesetid}')" />  
                   
                      &nbsp;<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>' styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);changeOrg('e0122');" onkeyup="addDict('${element.codesetid}','${element.itemid}',this);" onkeydown="return inputType2(this,event)" onclick="styleDisplay(this);"/>
									<img src="/images/code.gif" onclick='javascript:openInputCodeDialogOrgInputPos("${element.codesetid}","<%="infoFieldList["+index+"].viewvalue"%>",selfInfoForm.deptparentcode.value,"2");' />&nbsp;
                </logic:equal>
								<logic:equal name="element" property="itemid" value="e01a1">

									<html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleId="e01a1" onchange="changepos('${element.codesetid}')" />  
                      &nbsp;<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>' styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);changeOrg('e01a1');" onkeyup="addDict('${element.codesetid}','${element.itemid}',this);" onkeydown="return inputType2(this,event)" onclick="styleDisplay(this);"/>
									<img src="/images/code.gif" onclick='javascript:openInputCodeDialogOrgInputPos("${element.codesetid}","<%="infoFieldList["+index+"].viewvalue"%>",selfInfoForm.posparentcode.value,"2");' />&nbsp;
                </logic:equal>
								<logic:notEqual name="element" property="itemid" value="b0110">
									<logic:notEqual name="element" property="itemid" value="e0122">
										<logic:notEqual name="element" property="itemid" value="e01a1">
											<html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' /> 
                      &nbsp;<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>' styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);" onkeyup="addDict('${element.codesetid}','${element.itemid}',this);" onkeydown="return inputType2(this,event)" onclick="styleDisplay(this);"/>
											<logic:equal name="selfInfoForm" property="setname" value="${selfInfoForm.part_setid}">
											   <logic:equal name="element" property="itemid" value="${selfInfoForm.part_unit}">
											    <img src="/images/code.gif" onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="infoFieldList["+index+"].viewvalue"%>","3");' />
										        </logic:equal>
										        <logic:notEqual name="element" property="itemid" value="${selfInfoForm.part_unit}">
											      <img src="/images/code.gif" onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="infoFieldList["+index+"].viewvalue"%>");' />
										        </logic:notEqual>
										    </logic:equal>
										    <logic:notEqual name="selfInfoForm" property="setname" value="${selfInfoForm.part_setid}">
											   <logic:notEqual name="element" property="itemid" value="${selfInfoForm.part_unit}">
											    <img src="/images/code.gif" onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="infoFieldList["+index+"].viewvalue"%>");' />
										        </logic:notEqual>
										    </logic:notEqual>
										</logic:notEqual>
									</logic:notEqual>
								</logic:notEqual>
								<logic:equal name="element" property="fillable" value="true">
									<font color="red">*</font>
								</logic:equal>
							</td>
							<%if (flag == 0) {%>
						</tr>
						<%} else {%>
						<logic:equal name="element" property="rowflag" value="${index}">
							<td colspan="2">
							</td>
							</tr>
						</logic:equal>
						<%}%>
					</logic:notEqual>

				</logic:equal>
			  </logic:equal>
			</logic:iterate>



			<tr>

				<td align="center" nowrap colspan="4">
					

					
					<button name="savass" class="myButton" onclick="savesub();">
								<bean:message key="button.save" />
							</button>

					&nbsp;<button name="savass" class="myButton" onclick="returnback();">
								<bean:message key="button.return" />
							</button>




				</td>
			</tr>
		</table>
		<%} else {%>
		
		<%}%>
		<div id="date_panel">
			<select name="date_box" multiple="multiple" size="10" style="width:200" onchange="setSelectValue();">
				<option value="1992.4.12">
					1992.4.12
				</option>
				<option value="1992.4">
					1992.4
				</option>
				<option value="1992">
					1992
				</option>
			</select>
		</div>
	</html:form>
	<script language="javascript">
   Element.hide('date_panel');
   getfirstfocuse();
   function getfirstfocuse(){
   var objsss=document.getElementsByTagName("input");
   
   for(var i=0;i<objsss.length;i++){
   var dobj=objsss[i];
   if(dobj.type=="text"){
   	 dobj.focus();
   	 return;
   }
   }   
   }
</script>
</body>
<div id=dict style="border-style:nono">
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">    
     <tr>
     <td>
       <select name="dict_box" multiple="multiple" size="10" class="dropdown_frame" style="width:200" ondblclick="setSelectCodeValue();" ondblclick="setSelectCodeValue();" onkeydown="return inputType(this,event)" onblur="Element.hide('dict');">    
       </select>
     </td>
     </tr>
</div>
<script language="javascript">
var code_desc; 
function addDict(code,itemid,obj)
{
  Element.hide('dict');
  var value=obj.value;  
  if(value=="")
   return false;
  var dmobj;
  var vos= document.getElementsByName('dict_box');
  var dict_vo=vos[0];
  var isC=true;
  code_desc=obj;
  for(var i=dict_vo.options.length-1;i>=0;i--)
  {
      dict_vo.options.remove(i);
  }
   var no = new Option();
   no.value="";
   no.text="";
   dict_vo.options[0]=no;
   var r=1;      
   for(var i=0;i<g_dm.length;i++)
   {
		dmobj=g_dm[i];
		if(code=="UM"||code=="@K"||code=="UN")
		{
		    var vos;
		    if(itemid.toLowerCase()=="e01a1" || itemid.toLowerCase()=="e0122")
			    if(code=="UM")
			      vos= document.getElementById('b0110');
			    else if(code=="@K")
			      vos= document.getElementById('e0122');
			    var code_value="<%=codevalue%>";
		    if(vos)
		    {
		       var b_value=vos.value;
		       if(b_value==null||b_value=="")
		       {
		          b_value=""
		       }
		       if(code_value!=""&&code_value.length<=dmobj.ID.substring(2).length)
		       {
		           if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0&&dmobj.ID.indexOf(code+b_value)==0)||(dmobj.ID.indexOf(code+value)==0&&dmobj.ID.indexOf(code+b_value)==0))
		           {
		              if(dmobj.ID.substring(2).indexOf(code_value)==0)
		              {
		                 var no = new Option();
    	                 no.value=dmobj.ID;
    	                 no.text=dmobj.V;
		                 dict_vo.options[r]=no;
			             r++;
		              }
		           }
		       }else
		       {
		          if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0)||(dmobj.ID.indexOf(code+value)==0))
		          {
		            var no = new Option();
    	            no.value=dmobj.ID;
    	            no.text=dmobj.V;
		            dict_vo.options[r]=no;
			       r++;
		          }
		       }
		       
		    }else
		    {
		       if(code_value!=""&&code_value.length>=dmobj.ID.substring(2).length)
		       {
		           if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0&&dmobj.ID.indexOf(code+b_value)==0)||(dmobj.ID.indexOf(code+value)==0&&dmobj.ID.indexOf(code+b_value)==0))
		           {
		              if(code_value.indexOf(dmobj.ID.substring(2))==0)
		              {
		                 var no = new Option();
    	                 no.value=dmobj.ID;
    	                 no.text=dmobj.V;
		                 dict_vo.options[r]=no;
			             r++;
		              }
		           }
		       }else
		       {
		              if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0&&dmobj.ID.indexOf(code+b_value)==0)||(dmobj.ID.indexOf(code+value)==0&&dmobj.ID.indexOf(code+b_value)==0))
		              {
		               var no = new Option();
    	               no.value=dmobj.ID;
    	               no.text=dmobj.V;
		               dict_vo.options[r]=no;
			           r++;
		             }
		       }
		    }
		    
		}else
		{	 
		  if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0)||(dmobj.ID.indexOf(code+value)==0))
		  {
		    
		    var no = new Option();
    	    no.value=dmobj.ID;
    	    no.text=dmobj.V;
		    dict_vo.options[r]=no;		    
			r++;
		  }
	    }
   }    
   if(r==1)
   {
      obj.value="";
      Element.hide('dict'); 
      return false;      
   }   
   Element.show('dict');  
   var pos=getAbsPosition(obj);  
   with($('dict'))
   {
	   style.position="absolute";
       style.posLeft=pos[0]-1;
 	   style.posTop=pos[1]-1+obj.offsetHeight;
	   style.width=(obj.offsetWidth<150)?150:obj.offsetWidth+1;
   }  
}
 function setSelectCodeValue()
   {
     if(code_desc)
     {
        var vos= document.getElementsByName('dict_box');
        var dict_vo=vos[0];
        var isC=true;
        for(var i=0;i<dict_vo.options.length;i++)
        {
          if(dict_vo.options[i].selected)
          {
            code_desc.value=dict_vo[i].text;
            var code_name=code_desc.name;
            if(code_name!="")
            {
               var code_viewname=code_name.substring(0,code_name.indexOf("."));
               var view_vos= document.getElementsByName(code_viewname+".value");
               var view_vo=view_vos[0];
               if(dict_vo[i].value!=null)          
                 view_vo.value=dict_vo[i].value.substring(2);
               view_vo.fireEvent("onchange");
             }
          }
        }
        Element.hide('dict');   
        event.srcElement.releaseCapture(); 
     }
  }
  function inputType(obj,event)
  {
     var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
     if(keyCode==13)
     {
       setSelectCodeValue();
     }
     
  }
  function inputType2(obj,event)
  {
    var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
    if(keyCode == 40)
    {
       var vos= document.getElementsByName('dict');
       var vos1=vos[0];       
       if(vos1.style.display!="none")
       {
          var vos= document.getElementsByName('dict_box');
          var dict_vo=vos[0];          
          dict_vo.focus(); 
       }
    }
  }
  function styleDisplay(obj)
  {
     var obj_name=obj.name;
     if(code_desc)
     {
        var code_name=code_desc.name;
        if(code_name!=obj_name)
        {
          Element.hide('dict');
        }
     }
  }
     function checkDict(code,obj)
  {
    var code_name=obj.name;
    var code_viewname=code_name.substring(0,code_name.indexOf("."));
    var view_vos= document.getElementsByName(code_viewname+".value");
    var view_vo=view_vos[0];  
    if(view_vo==null||view_vo=="")
    {
      obj.value="";
      return false;
    }
    var isC=false;
    for(var i=0;i<g_dm.length;i++)
    {
		dmobj=g_dm[i];	
		if(dmobj.ID==(code+view_vo))
		{
		    isC=true;
		    break;
		}
   } 
   if(!isC)
   {
      obj.value="";
      return false;
   } 
}
Element.hide('dict');
function reloadMenu(actiontype)
{
   if(actiontype=="update")
     return;
   var a0100="${selfInfoForm.a0100}";
   var setname="${selfInfoForm.setname}";
   if(a0100!=null&&a0100!=""&&a0100!="A0100"&&a0100!="a0100"&&a0100!="su")
   {
     if(setname=="A01")
        parent.mil_menu.location.reload(); 
   }
    
}
reloadMenu('${selfInfoForm.actiontype}');
</script>  
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
   
   var dropDownList=createDropDown("dropDownList");
   var __t=dropDownList;
   __t.type="list";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);   
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
	//__t.path="/system/gcodeselect.jsp";    
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
</script> 