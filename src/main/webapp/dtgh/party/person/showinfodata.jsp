<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>

<%// 在标题栏显示当前用户和日期 2004-5-10 
			String userName = null;
			String css_url = "/css/css1.css";
			UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			int status=userView.getStatus();
			String manager=userView.getManagePrivCodeValue();
	int version = 60;
	if(userView != null)
	{
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	  version = userView.getVersion();
	}
	String date = DateStyle.getSystemDate().getDateString();
	int flag1=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag1=2;
	
	String bosflag = "";
    if (null != userView) {
        bosflag = userView.getBosflag();
        bosflag = bosflag != null ? bosflag : "";
    }
%>


<hrms:themes></hrms:themes>
<style>
  .borderl0 {border-left: 0px;}
  .borderr0 {border-right: 0px;}
  .notop {border-top:none;}
</style>

<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<script language="javascript">
   function change()
   {
      personForm.action="/dtgh/party/person/searchbusinesslist.do?b_query=link&a_code=${personForm.a_code }&param=${personForm.param }&politics=${personForm.politics }";
      personForm.submit();
   }
   
   function changeUsr(q){
   	  personForm.action="/dtgh/party/person/searchbusinesslist.do?b_query=link&a_code=${personForm.a_code }&param=${personForm.param }&politics=${personForm.politics }&query="+q;
      personForm.submit();
   }
   function exeAdd(addStr)
   {
      // alert(addStr);
       var param='${personForm.param }';
	   if(param=='Y'){
	   		param=64;
	   }else if(param=='V'){
	   	param=65;
	   }else if(param=='W'){
	   	param=66;
	   }else{
	   	param=64;
	   }
       target_url=addStr+"&returnvalue="+param;
       window.open(target_url,'nil_body'); 
   }
   function exeTemplate(type,tabid){
    var version = '<%=version%>';
   	if(type=='add'){
    	if(version>=70)
   			window.open("/module/template/templatemain/templatemain.html?b_query=link&sys_type=1&module_id=1&ins_id=0&approve_flag=1&return_flag=8-${personForm.param }&view_type=card&tab_id="+tabid,'nil_body');
   		else 
   			window.open("/general/template/edit_form.do?b_query=link&returnflag=${personForm.param }&sp_flag=1&ins_id=0&tabid="+tabid+"&warn_id=${personForm.a_code }&operationname=${personForm.politics }",'nil_body');
   	}else if(type=='edit'){
   		var tab=$('tbl_r');
	   var rows=tab.rows.length;	  
	   if(rows<=1)
	   {
	       alert("没有人员！");
	       return;
	   }
	     
	   var  thetr;
	   var thechkbox,a0100;  
       var objarr=new Array();	   	   
	   for(var i=1;i<=tab.rows.length-1 ;i++)
	   {
           thetr = tab.rows[i];
           thechkbox=thetr.cells[0].children[0];
       	   if(!thechkbox.checked)
        		continue;	   
           a0100=thetr.cells[1].innerHTML;    
          // alert(a0100);         
           objarr.push(a0100);
	   }	  
       if(objarr.length>0)
       {
   	   		var hashvo=new ParameterSet();
       		hashvo.setValue("tabid",tabid);	
       		hashvo.setValue("ins_id","0");
       		hashvo.setValue("objlist",objarr);
       		var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0570010135'},hashvo);         
       }else
       {
          alert("请选择人员！");
       }
   	}else{
   		var objarr=new Array();	   	          
        objarr.push(type);	  
       if(objarr.length>0)
       {
   	   		var hashvo=new ParameterSet();
       		hashvo.setValue("tabid",tabid);	
       		hashvo.setValue("ins_id","0");
       		hashvo.setValue("objlist",objarr);
       		var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0570010135'},hashvo);         
       }else
       {
          alert("请选择人员！");
       }
   	}
   }
   function isSuccess(outparamters){
   		var flag=outparamters.getValue("succeed");
		var tabid=outparamters.getValue("tabid");
		if(flag=="false")
			return;	
		var version = '<%=version%>';
		if(version>=70)
   			window.open("/module/template/templatemain/templatemain.html?b_query=link&sys_type=1&module_id=1&ins_id=0&approve_flag=1&return_flag=8-${personForm.param }&view_type=card&tab_id="+tabid,'nil_body');
   		else
       		window.open("/general/template/edit_form.do?b_query=link&returnflag=${personForm.param }&sp_flag=1&ins_id=0&tabid="+tabid+"&warn_id=${personForm.a_code }&operationname=${personForm.politics }",'nil_body');
	
   }
   function deleterec()
   {
       var isCorrect=false;
       var tab=$('tbl_r');
      /* var len=document.personForm.elements.length;
       var i;
       for (i=0;i<len;i++)
       {
       	
          if(document.personForm.elements[i].type=="checkbox")
          {	
              if(document.personForm.elements[i].checked==true)
              {
                isCorrect=true;
                break;
              }
          }
      } */
      for(var i=1;i<=tab.rows.length-1 ;i++)
	   {
           thetr = tab.rows[i];
           thechkbox=thetr.cells[0].children[0];
       	   if(!thechkbox.checked)
        		continue; 
        	isCorrect=true;	
        	break;
        } 
      if(!isCorrect)
      {
         alert("请选择人员!");
         return false;
      }   
      if(confirm("<bean:message key="workbench.info.isdelete"/>?"))
      {
        personForm.action="/dtgh/party/person/searchbusinesslist.do?b_delete=del";
        personForm.submit(); 
       }
   }
    function moverec()
   {
       var isCorrect=false;
       var len=document.selfInfoForm.elements.length;
       var i;
       for (i=0;i<len;i++)
       {
          if(document.selfInfoForm.elements[i].type=="checkbox")
          {
              if(document.selfInfoForm.elements[i].checked==true)
              {
                isCorrect=true;
                break;
              }
          }
      }   
      if(!isCorrect)
      {
         alert("请选择人员!");
         return false;
      }  
      if(confirm("<bean:message key="workbench.info.ismove"/>?"))
      {
        selfInfoForm.action="/workbench/info/showinfodata.do?b_move=move";
        selfInfoForm.submit(); 
       }
   }
   function query()
   {	
      var info,queryType,dbPre;
      info="1";
      dbPre="Usr";
      
      //dbpre=selfInfoForm.userbase.value;
      queryType="1";
      var strExpression = generalExpressionDialog(info,dbPre,queryType,'');
      if(strExpression!=null)
      {
      var a=strExpression.split("|");
      var nextcondid=MyInt(a[0].length/2+2);
      var nc=getNextcond();
      if(nc.length>0){
       strExpression=a[0]+"*"+nextcondid+"|"+a[1]+nc;
      }
        strExpression=replaceAll(strExpression,"+","%2B");
        selfInfoForm.action="/workbench/info/showinfodata.do?b_query=link&strexpression="+ strExpression;
        selfInfoForm.submit(); 	
      }
   }
   function selectQ()
   {
       
       var tablename="${personForm.userbase}";
       var a_code="UN";
         
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
            personForm.action="/dtgh/party/person/searchbusinesslist.do?b_query=link&a_code=${personForm.a_code }&param=${personForm.param }&politics=${personForm.politics }";
      		personForm.submit();
      } 
   }
   function clearQ()
   {
       selfInfoForm.action="/workbench/info/showinfodata.do?b_search=link&code=${selfInfoForm.code}&kind=${selfInfoForm.kind}&check=no&query=0";
       selfInfoForm.submit();
   }
   function getNextcond(){
     
   	var personsort=selfInfoForm.personsort;
   	if(personsort!=null){
  	 var v=personsort.value;
  	 var c=selfInfoForm.sortfield.value;
   		return c+"="+v+"`";
   	}else{
  		 return  "";
   	}
   }
   function replaceAll( str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	   
	    return str;
	}
	function   MyInt   (nVar)   {   
         return   (   nVar   <   0   ?   Math.ceil   (nVar):Math.floor(nVar)   );     
  }
function changesort()
{ 
		
		selfInfoForm.action="/workbench/info/showinfodata.do?b_search=link&code=${selfInfoForm.code}&kind=${selfInfoForm.kind}";
       	
        selfInfoForm.submit(); 	
  
}
function initDates(){
	var o=selfInfoForm.personsort;
         if(o.options.length>0)
		{			
		 	o.options[0].selected=true;
		 	o.fireEvent("onchange");		 	
		}

}
function winhrefOT(a0100,target)
{
   if(a0100=="")
      return false;
   var param='${personForm.param }';
   if(param=='Y'){
   		param=64;
   }else if(param=='V'){
   	param=65;
   }else if(param=='W'){
   	param=66;
   }else{
   	param=64;
   }
   window.open("/workbench/info/addselfinfo.do?b_add=add&i9999=I9999&actiontype=update&setname=A01&flag=notself&returnvalue="+param+"&isAdvance=1&a0100="+a0100+"&a_code=${personForm.a_code }&politics=${personForm.politics }",target);
}
   function browseOT(a0100,target)
{
   if(a0100=="")
      return false;
   var param='${personForm.param }';
   if(param=='')
   	param="Y";
   window.open("/workbench/browse/showselfinfo.do?b_search=link&userbase=${personForm.userbase}&a0100="+a0100+"&flag=notself&returnvalue="+param+"&a_code=${personForm.a_code }&politics=${personForm.politics }",target);
}

document.oncontextmenu = function() {return false;}

function showOrClose()
{
		var obj=eval("aa");
	    var obj3=eval("vieworhidd");
		//var obj2=eval("document.personForm.isShowCondition");
	    if(obj.style.display=='none')
	    {
	    	//obj2.value='block';
    		obj.style.display='block'
        	obj3.innerHTML="<a href=\"javascript:showOrClose();\">[&nbsp;查询隐藏 &nbsp;]</a>";
    	}
    	else
	    {
	    	//obj2.value='none';
	    	obj.style.display='none';
	    	obj3.innerHTML="<a href=\"javascript:showOrClose();\">[&nbsp;查询显示 &nbsp;] </a>";
	    	
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
      if(vo)
         vo.value="1";
   }else
   {
         var vo=document.getElementById(hiddname);
      if(vo)
         vo.value="0";
   }

}

function returnback(){
	personForm.action="/dtgh/party/person/searchbusinesstree.do?b_query=link&param=${personForm.param}&backdate=";
	personForm.target="il_body";
	personForm.submit();
}
var a0100s="";
function docan(outparamters){
	var as=a0100s.split(",");
	var param=outparamters.getValue("param");
	for(var i=0;i<as.length;i++){
		if((as[i]).length>0){
			var display=outparamters.getValue(as[i]);
			if(display=='none')
			($(param+'span'+as[i])).style.display=display;
		}
	}
}
function canresumeparty(){
	//alert(a0100s);
	if(a0100s.length<=1)
	return;
	var hashvo=new ParameterSet();
	hashvo.setValue("a0100s",a0100s);
	hashvo.setValue("param","Y");
	hashvo.setValue("userbase",'${personForm.userbase }');
	var request=new Request({method:'post',asynchronous:true,onSuccess:docan,functionId:'3409000021'},hashvo);
}

function canresumemember(){
	//alert(a0100s);
	if(a0100s.length<=1)
	return;
	var hashvo=new ParameterSet();
	hashvo.setValue("a0100s",a0100s);
	hashvo.setValue("param","V");
	hashvo.setValue("userbase",'${personForm.userbase }');
	var request=new Request({method:'post',asynchronous:true,onSuccess:docan,functionId:'3409000021'},hashvo);
}
function adda0100(a0100){
	if(a0100s.indexOf(a0100+",")==-1)
		a0100s+=a0100+",";
}
</script>
<script language="JavaScript">
function pf_ChangeFocus(e) 
			{
				  e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
			      var key = window.event?e.keyCode:e.which;
			      var t=e.target?e.target:e.srcElement;
			      if ( key==0xD && t.tagName!='TEXTAREA') /*0xD*/
			      {    
			   		   if(window.event)
			   		   	e.keyCode=9;
			   		   else
			   		   	e.which=9;
			      }
			   //按F5刷新问题,重复提交问题,右键菜单也设法去掉
			   if ( key==116)
			   {
			   		if(window.event){
			   		   	e.keyCode=0;
			   		   	e.returnValue=false;
			   		}else{
			   		   	e.which=0;
			   		   	e.preventDefault();
			   		}
			   }   
			   if ((e.ctrlKey)&&(key==82))//屏蔽 Ctrl+R  
			   {    
			        if(window.event){
			   		   	e.keyCode=0;
			   		   	e.returnValue=false;
			   		}else{
			   		   	e.which=0;
			   		   	e.preventDefault();
			   		}
			   } 
			}
function document.oncontextmenu() 
{ 
  return　false; 
} 

//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题
/*oncontextmenu = "return false"*/

function gointo(param){
	var currnode=parent.frames['mil_menu'].Global.selectedItem;
	if(currnode.uid=='root'){
		alert("请您先选择非根节点的组织单元节点！");
		return;
	}
	personForm.action="/dtgh/party/person/searchbusinesslist.do?b_query=link&politics="+param;
	//personForm.target="il_body";
	personForm.submit();
}
</script>
<html:form action="/dtgh/party/person/searchbusinesslist">
<html:hidden name="personForm" property="factor" styleId="factor" styleClass="text"/>
<html:hidden name="personForm" property="expr" styleId="expr" styleClass="text"/>
<html:hidden name="personForm" property="likeflag" styleId="likeflag" styleClass="text"/> 
<%int i=0;%>
 <table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0">
 <tr>
   <td aling="left" >
     <table border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td align="left"  nowrap>
                	<logic:notEmpty name="personForm" property="codemess">
                    当前组织单元：
                    <bean:write name="personForm" property="codemess"/>
                    &nbsp;&nbsp;&nbsp;
                    </logic:notEmpty>
                    &nbsp;
                </td>
	     <td nowrap>
             <table  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
                <tr>
                       <!-- html:hidden name="personForm" property="isShowCondition" /-->
                       <td nowrap id="vieworhidd"> 
                          <a href="javascript:showOrClose();"> 
                          	  <%--切换时,字体中间多了空格 已去掉     wangb 20180329 bug 36136 and 37508 --%>
                              [&nbsp;查询<logic:equal name="personForm" property="isShowCondition" value="none" >显示 </logic:equal><logic:equal name="personForm" property="isShowCondition" value="block" >隐藏</logic:equal>&nbsp;]&nbsp; 
                          </a>
                       </td>                       
                    </tr>
             </table>
         </td>  
         <td nowrap>&nbsp;
                 <logic:equal name="personForm" property="partylike" value="1">
                     <input type="checkbox" name="orglike2" value="true" onclick="selectCheckBox(this,'partylike');change();" checked>
                 </logic:equal>
                 <logic:notEqual name="personForm" property="partylike" value="1">
                     <input type="checkbox" name="orglike2" value="true" onclick="selectCheckBox(this,'partylike');change();">
                 </logic:notEqual>                 
                 <html:hidden name="personForm" property='partylike' styleClass="text"/>                 
                 <bean:message key="system.browse.info.viewallpeople"/>
        </td>      
       </tr>
     </table>
   </td>
 </tr>
 <tr>
   <td nowrap>
<%
	
	int flag=0;
	int j=0;
	int n=0;
%>
 <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id='aa' style='display:${personForm.isShowCondition}'>
  <tr>
   <td>    
     <!-- 查询开始 -->
       <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow">
       <tr class="trShallow1">
          <td align="center" colspan="4" height='20' class="RecordRow" nowrap>
            <bean:message key="label.query.inforquery"/><!-- 请选择查询条件! -->
          </td>
       </tr>
       <tr>
         <td align="right" height='28' nowrap>
             &nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="label.dbase"/>&nbsp;
         </td>
         <td align="left"  nowrap><!-- 人员库 -->
            <hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="false" paraValue="" 
                 sql="personForm.dbcond" collection="list" scope="page"/>
              <html:select name="personForm" property="userbase" size="1" onchange="javascript:changeUsr('2')">
                      <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select>&nbsp;
         </td>
         <td align="right" height='28' nowrap><!-- 姓名 -->
            <bean:message key="label.title.name"/>&nbsp;
         </td>
         <td align="left"  nowrap>
           <input type="text" name="select_name" value="${personForm.select_name}" size="31" maxlength="31" class="text4" >
         </td>
       </tr>
       <logic:iterate id="element" name="personForm"  property="queryfieldlist" indexId="index">            
           <!-- 时间类型 -->
          <logic:equal name="element" property="itemtype" value="D">
               <% 
                  if(flag==0)
                  {
                   out.println("<tr>");
                         flag=1;          
                  }else{
                       flag=0;           
                  }
               %>  
              <td align="right" height='28' nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
              </td>
              <td align="left"  nowrap>
                  <html:text name="personForm" property='<%="queryfieldlist["+index+"].value"%>' size="13" maxlength="10" styleClass="text4" title="输入格式：2008.08.08" onclick=""/>
                  <bean:message key="label.query.to"/>
                  <html:text name="personForm" property='<%="queryfieldlist["+index+"].viewvalue"%>' size="13" maxlength="10" styleClass="text4" title="输入格式：2008.08.08"  onclick=""/>
			          <!-- 没有什么用，仅给用户与视觉效果-->
			      <INPUT type="radio" name="${element.itemid}"  checked="true"><bean:message key="label.query.age"/>	
			      <INPUT type="radio" name="${element.itemid}" id="day"><bean:message key="label.query.day"/>
              </td>
              <%
                 if(flag==0)
        			out.println("</tr>");
              %>   
          </logic:equal>
          <logic:equal name="element" property="itemtype" value="M">
               <% 
                  if(flag==0)
                  {
                   out.println("<tr>");
                         flag=1;          
                  }else{
                       flag=0;           
                  }
              %> 
              <td align="right" height='28' nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
              </td>
              <td align="left"  nowrap>
                  <html:text name="personForm" property='<%="queryfieldlist["+index+"].value"%>' size="31" maxlength='<%="queryfieldlist["+index+"].itemlength"%>' styleClass="text4"/>
              </td>
              <%
                 if(flag==0)
        			out.println("</tr>");
              %> 
          </logic:equal> 
           <logic:equal name="element" property="itemtype" value="N">   
              <% 
                  if(flag==0)
                  {
                   out.println("<tr>");
                         flag=1;          
                  }else{
                       flag=0;           
                  }
              %> 
              <td align="right" height='28' nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
              </td>
             <td align="left"  nowrap> 
              <html:text name="personForm" property='<%="queryfieldlist["+index+"].value"%>' size="31" maxlength="${element.itemlength}" styleClass="text4"/> 
             </td>
              <%
                 if(flag==0)
        			out.println("</tr>");
              %> 
              
           </logic:equal>
           <logic:equal name="element" property="itemtype" value="A">
              <logic:notEqual name="element" property="codesetid" value="0">              
                  <logic:equal name="element" property="codesetid" value="UN">
                     <%
                       if(flag==0)
                       {
                           out.println("<tr>");
                           flag=1;          
                       }else{
                            flag=0;           
                       }
                      %> 
                      <td align="right" height='28' nowrap>
                        <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
                     </td>
                     <td align="left" nowrap>
                       <html:hidden name="personForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="personForm" property='<%="queryfieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                       <logic:equal name="element" property="itemid" value="b0110"> 
                            <img src="/images/code.gif" onclick='openInputCodeDialogOrgInputPos("UN","<%="queryfieldlist["+index+"].viewvalue"%>","<%=manager%>",1);' align="absmiddle"/>
                       </logic:equal> 
                       <logic:notEqual name="element" property="itemid" value="b0110">                                         
                            <img src="/images/code.gif" onclick='openInputCodeDialog("${element.codesetid}","<%="queryfieldlist["+index+"].viewvalue"%>","0");' align="absmiddle"/>
                      </logic:notEqual>   
                    </td>
                     <%
                       if(flag==0)
        	         out.println("</tr>");
                     %>                                  
                   </logic:equal>                          
                   <logic:equal name="element" property="codesetid" value="UM">
                       <%
                       if(flag==0)
                       {
                           out.println("<tr>");
                           flag=1;          
                       }else{
                            flag=0;           
                       }
                      %>  
                      <td align="right" height='28' nowrap>
                        <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
                      </td>
                      <td align="left" nowrap>
                       <html:hidden name="personForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="personForm" property='<%="queryfieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                       <logic:equal name="element" property="itemid" value="e0122"> 
                            <img src="/images/code.gif" onclick='openInputCodeDialogOrgInputPos("UM","<%="queryfieldlist["+index+"].viewvalue"%>","<%=manager%>",1);' align="absmiddle"/>
                       </logic:equal> 
                       <logic:notEqual name="element" property="itemid" value="e0122">                                         
                            <img src="/images/code.gif" onclick='openInputCodeDialog("${element.codesetid}","<%="queryfieldlist["+index+"].viewvalue"%>","0");' align="absmiddle"/>
                      </logic:notEqual>   
                    </td>
                     <%
                       if(flag==0)
        	         out.println("</tr>");
                     %>           
                   </logic:equal>
                   <logic:equal name="element" property="codesetid" value="@K">
                       <%
                       if(flag==0)
                       {
                           out.println("<tr>");
                           flag=1;          
                       }else{
                            flag=0;           
                       }
                      %>  
                      <td align="right" height='28' nowrap>
                        <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
                      </td>
                      <td align="left" nowrap>
                       <html:hidden name="personForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="personForm" property='<%="queryfieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                       <img src="/images/code.gif" onclick='openInputCodeDialog("${element.codesetid}","<%="queryfieldlist["+index+"].viewvalue"%>","0");' align="absmiddle"/>
                    </td>
                     <%
                       if(flag==0)
        	         out.println("</tr>");
                     %>           
                   </logic:equal>
                   <logic:notEqual name="element" property="codesetid" value="UN">
                      <logic:notEqual name="element" property="codesetid" value="UM">
                         <logic:notEqual name="element" property="codesetid" value="@K">
                             <logic:greaterThan name="element" property="itemlength" value="20">
                               <!-- 大于 -->
                                <%
                                 if(flag==0)
                                 {
                                   out.println("<tr>");
                                   flag=1;          
                                 }else{
                                   flag=0;           
                                 }
                                %>  
                                <td align="right" height='28' nowrap>
                                  <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
                                </td>
                                <td align="left" nowrap>
                                  <html:hidden name="personForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                                  <html:text name="personForm" property='<%="queryfieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                                  <img src="/images/code.gif" onclick='openInputCodeDialog("${element.codesetid}","<%="queryfieldlist["+index+"].viewvalue"%>","0");' align="absmiddle"/>
                                </td>
                               <%
                                if(flag==0)
        	                    out.println("</tr>");
                                %>         
                             </logic:greaterThan>
                             <logic:lessEqual  name="element" property="itemlength" value="20">
                               <!-- 小于等于 -->
                                 <%
                                   if(flag==1)
    				    {
    				      out.println("<td colspan=\"2\">");
                                      out.println("</td>");
                                      out.println("</tr>");
    				    }
    				%>		
    				<tr>
    				  <td align="right" height='28' nowrap>       
        	             	    <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;        
        	             	    <html:hidden name="personForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
        	             	 </td> 
       	             	        <td align="left" colspan="3" nowrap>
       	             	           <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
       	             	             <tr>
       	             	              <td>
       	             	                 <!--checkbox-->       	             	                 
       	             	                 <hrms:codesetmultiterm codesetid="${element.codesetid}" itemid="${element.itemid}" itemvalue="${element.value}" rownum="6" hiddenname='<%="queryfieldlist["+index+"].value"%>'/>
    				       </td>
                                    </tr> 
        	             	    </table> 
        	             	</td>
        	             	</tr>
        	             	 <%flag=0;%>
                             </logic:lessEqual>
                         </logic:notEqual>
                      </logic:notEqual>
                   </logic:notEqual>
              </logic:notEqual>
              <logic:equal name="element" property="codesetid" value="0">
              
                                                              
               <% 
                  if(flag==0)
                  {
                   out.println("<tr>");
                         flag=1;          
                  }else{
                       flag=0;           
                  }
              %> 
              <td align="right" height='28' nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
              </td>
              <td align="left"  nowrap>
               <html:text name="personForm" property='<%="queryfieldlist["+index+"].value"%>' size="31" maxlength="${element.itemlength}" styleClass="text4"/>
              </td>
              <%
                 if(flag==0)
        			out.println("</tr>");
              %> 
            </logic:equal>             
         </logic:equal>
       </logic:iterate>
        <%
         if(flag==1)
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
    	  <td align="left" colspan="3" height='20'  nowrap>
    	  <logic:equal name="personForm" property="querylike" value="1">
    	        <input type="checkbox" name="querlike2" value="true" onclick="selectCheckBox(this,'querylike');" checked>
    	    </logic:equal>  
    	    <logic:notEqual name="personForm" property="querylike" value="1">
    	        <input type="checkbox" name="querlike2" value="true" onclick="selectCheckBox(this,'querylike');">
    	    </logic:notEqual>    	   
    	      <html:hidden name="personForm" property='querylike' styleClass="text"/>
    	      &nbsp;&nbsp;
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
    <td align="center">
     <Input type='button' value="<bean:message key="infor.menu.query"/>" onclick="changeUsr('1');" class='mybutton' /> 
     <Input type='button' value="<bean:message key="button.sys.cond"/>" onclick='selectQ();' class='mybutton' />
  	  </td>
    </tr>
 </table>   
     
   <!-- 查询结束 --> 
 </td>
</tr>

 <tr style="padding-top: 5px">
   <td width="100%" nowrap>
   	
     <div class="fixedDiv2"> 
     
     <table id='tbl_r' width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF borderl0 borderr0 notop">
           <tr class="fixedHeaderTr"><!-- 【7098】党团管理（页面上有缺线的现象）jingq upd 2015.02.03 -->
             <td align="center" class="TableRow borderl0 notop" width="25" nowrap>
              <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
             </td>
          <logic:iterate id="info"    name="personForm"  property="browsefields">   
              <td align="center" class="TableRow borderr0 notop" nowrap>
                   <bean:write  name="info" property="itemdesc" filter="true"/>&nbsp;
              </td>
             </logic:iterate> 
	        <logic:notEmpty name="personForm"  property="politics">
		    <td align="center" class="TableRow borderr0 notop" width="200" nowrap>
			<bean:message key="column.operation"/>            	
		    </td> 
		    </logic:notEmpty>        	    	    	    		        	        	        
           </tr>

          <hrms:paginationdb id="element" name="personForm" sql_str="personForm.strsql" table="" where_str="personForm.cond_str" columns="personForm.columns" order_by="personForm.order_by" page_id="pagination" pagerows="${personForm.pagerows}" keys="${personForm.userbase}A01.a0100" indexes="indexes">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          <%}
          else
          {%>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'DDEAFE')">
          <%
          }
          i++;          
          %>  
            <td align="center" class="RecordRow borderl0 borderr0" width="25" nowrap>
               <hrms:checkmultibox name="personForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
            </td> 
            <bean:define id="a0100" name="element" property="a0100"/>
            <td align="center" class="TableRow" style="display:none">
			    ${personForm.userbase }${a0100 }
			</td> 
			
	         <logic:iterate id="info"    name="personForm"  property="browsefields">  	
	            
	            <script>
	            	<logic:equal value="person" name="personForm"  property="politics">
						<logic:equal value="Y" name="personForm" property="param">
						<logic:notEmpty name="personForm"  property="resumeparty">
							adda0100('${a0100 }');
						</logic:notEmpty>
						</logic:equal>
						<logic:equal value="V" name="personForm" property="param">
						<logic:notEmpty name="personForm"  property="resumemember">
							adda0100('${a0100 }');
						</logic:notEmpty>
						</logic:equal>
						</logic:equal>
	            </script>	          
                  <logic:notEqual  name="info" property="itemtype" value="N">               
                    <td align="left" class="RecordRow" nowrap>        
                  </logic:notEqual>
                  <logic:equal  name="info" property="itemtype" value="N">               
                    <td align="right" class="RecordRow" nowrap>        
                  </logic:equal>    
                  <logic:equal  name="info" property="codesetid" value="0">   
	                 	 <logic:equal  name="info" property="itemid" value="a0101"> 
	                 	 &nbsp; <a href="###" onclick="browseOT('<bean:write name="element" property="a0100" filter="true"/>','nil_body');"><bean:write  name="element" property="${info.itemid}" filter="true"/></a>&nbsp;
	                 	 </logic:equal>
	                 	 <logic:notEqual  name="info" property="itemid" value="a0101"> 
	                    &nbsp; <bean:write  name="element" property="${info.itemid}" filter="true"/>&nbsp;
	                    </logic:notEqual>
                  </logic:equal>
                 <logic:notEqual  name="info" property="codesetid" value="0">  
                 <logic:equal name="info" property="codesetid" value="UM">
                     <hrms:codetoname codeid="UM" name="element" uplevel="${personForm.uplevel}" codevalue="${info.itemid}" codeitem="codeitem" scope="page" />  	      
          	           &nbsp;  <bean:write name="codeitem" property="codename" />&nbsp; 
                   </logic:equal>
                   <logic:notEqual name="info" property="codesetid" value="UM">
                      
                        <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
          	    	    &nbsp; <bean:write name="codeitem" property="codename" />&nbsp;  
                        <logic:equal name="info"   property="itemid" value="e01a1">
                         </logic:equal>
                   </logic:notEqual>                 
                     
          	     </logic:notEqual>  
              </td>
             </logic:iterate> 
             <logic:notEmpty  name="personForm"  property="politics"> 
             <td align="center" class="RecordRow borderr0" width="200" nowrap>
             	<logic:equal value="party" name="personForm"  property="politics">
             		<a href="###" onclick="browseOT('<bean:write name="element" property="a0100" filter="true"/>','nil_body');">查看</a>
             		<hrms:priv func_id="35012006">
            			<a href="###" onclick="winhrefOT('<bean:write name="element" property="a0100" filter="true"/>','nil_body');">编辑</a>
	    			</hrms:priv>
	    			<logic:notEmpty name="personForm"  property="leave">
	    			<hrms:priv func_id="35012002">
		   				<a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.leave }');">出党</a>
		   			</hrms:priv>
		   			</logic:notEmpty>
		   			<logic:notEmpty name="personForm"  property="iin">
		   				<!-- a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.iin }');">调入</a> -->
		   			</logic:notEmpty>
		   			<logic:notEmpty name="personForm"  property="out">
		   			<hrms:priv func_id="35012004">
		   				<a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.out }');">调出</a>
		   			</hrms:priv>
		   			</logic:notEmpty>
	    		</logic:equal>
	    		<logic:equal value="preparty" name="personForm"  property="politics">
             		<a href="###" onclick="browseOT('<bean:write name="element" property="a0100" filter="true"/>','nil_body');">查看</a>
            		<hrms:priv func_id="35012102">
            			<a href="###" onclick="winhrefOT('<bean:write name="element" property="a0100" filter="true"/>','nil_body');">编辑</a>
		   			</hrms:priv>
		   			<logic:notEmpty name="personForm"  property="iin">
		   				<!-- a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.iin }');">调入</a> -->
		   			</logic:notEmpty>
		   			<logic:notEmpty name="personForm"  property="out">
		   				<hrms:priv func_id="35012104">
		   					<a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.out }');">调出</a>
		   				</hrms:priv>
		   			</logic:notEmpty>
		   			<logic:notEmpty name="personForm"  property="leave">
		   				<hrms:priv func_id="35012106">
		   					<a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.leave }');">取消资格</a>
		   				</hrms:priv>
		   			</logic:notEmpty>
	    		</logic:equal>
	    		<logic:equal value="important" name="personForm"  property="politics">
             		<a href="###" onclick="browseOT('<bean:write name="element" property="a0100" filter="true"/>','nil_body');">查看</a>
            		<hrms:priv func_id="35012202">
            			<a href="###" onclick="winhrefOT('<bean:write name="element" property="a0100" filter="true"/>','nil_body');">编辑</a>
		   			</hrms:priv>
		   			<logic:notEmpty name="personForm"  property="iin">
		   				<!-- a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.iin }');">调入</a> -->
		   			</logic:notEmpty>
		   			<logic:notEmpty name="personForm"  property="out">
		   				<hrms:priv func_id="35012204">
		   					<a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.out }');">调出</a>
		   				</hrms:priv>
		   			</logic:notEmpty>
		   			<logic:notEmpty name="personForm"  property="leave">
		   				<hrms:priv func_id="35012206">
		   					<a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.leave }');">取消资格</a>
		   				</hrms:priv>
		   			</logic:notEmpty>
	    		</logic:equal>
	    		<logic:equal value="active" name="personForm"  property="politics">
             		<a href="###" onclick="browseOT('<bean:write name="element" property="a0100" filter="true"/>','nil_body');">查看</a>
            		<hrms:priv func_id="35012302">
            			<a href="###" onclick="winhrefOT('<bean:write name="element" property="a0100" filter="true"/>','nil_body');">编辑</a>
		   			</hrms:priv>
		   			<logic:notEmpty name="personForm"  property="iin">
		   				<!-- a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.iin }');">调入</a> -->
		   			</logic:notEmpty>
		   			<logic:notEmpty name="personForm"  property="out">
		   				<hrms:priv func_id="35012304">
		   					<a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.out }');">调出</a>
		   				</hrms:priv>
		   			</logic:notEmpty>
		   			<logic:notEmpty name="personForm"  property="leave">
		   				<hrms:priv func_id="35012306">
		   					<a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.leave }');">取消资格</a>
		   				</hrms:priv>
		   			</logic:notEmpty>
	    		</logic:equal>
	    		<logic:equal value="application" name="personForm"  property="politics">
             		<a href="###" onclick="browseOT('<bean:write name="element" property="a0100" filter="true"/>','nil_body');">查看</a>
            		<hrms:priv func_id="35012402">
            			<a href="###" onclick="winhrefOT('<bean:write name="element" property="a0100" filter="true"/>','nil_body');">编辑</a>
		   			</hrms:priv>
		   			<logic:notEmpty name="personForm"  property="iin">
		   				<!-- a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.iin }');">调入</a> -->
		   			</logic:notEmpty>
		   			<logic:notEmpty name="personForm"  property="out">
		   				<hrms:priv func_id="35012404">
		   					<a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.out }');">调出</a>
		   				</hrms:priv>
		   			</logic:notEmpty>
		   			<logic:notEmpty name="personForm"  property="leave">
		   				<hrms:priv func_id="35012406">
		   					<a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.leave }');">取消资格</a>
		   				</hrms:priv>
		   			</logic:notEmpty>
	    		</logic:equal>
	    		<logic:equal value="member" name="personForm"  property="politics">
             		<a href="###" onclick="browseOT('<bean:write name="element" property="a0100" filter="true"/>','nil_body');">查看</a>
            		<hrms:priv func_id="35022002">
            			<a href="###" onclick="winhrefOT('<bean:write name="element" property="a0100" filter="true"/>','nil_body');">编辑</a>
		   			</hrms:priv>
		   			<logic:notEmpty name="personForm"  property="iin">
		   				<!-- a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.iin }');">调入</a> -->
		   			</logic:notEmpty>
		   			<logic:notEmpty name="personForm"  property="out">
		   				<hrms:priv func_id="35022004">
		   					<a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.out }');">调出</a>
		   				</hrms:priv>
		   			</logic:notEmpty>
		   			<logic:notEmpty name="personForm"  property="leave">
						<hrms:priv func_id="35022005">
		   					<a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.leave }');"><bean:message key="dtgh.party.setup.bus.leavemember"/></a>
		   				</hrms:priv>
		   			</logic:notEmpty>
	    		</logic:equal>
	    		<logic:equal value="person" name="personForm"  property="politics">
             		<a href="###" onclick="browseOT('<bean:write name="element" property="a0100" filter="true"/>','nil_body');">查看</a>
		   			<logic:equal value="Y" name="personForm" property="param">
		   				<hrms:priv func_id="35012504">
		   					<a href="###" onclick="winhrefOT('<bean:write name="element" property="a0100" filter="true"/>','nil_body');">编辑</a>
		   				</hrms:priv>
		   				<logic:notEmpty name="personForm"  property="up">
		   					<hrms:priv func_id="35012502">
			   					<a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.up }');">申请入党</a>
			   				</hrms:priv>
			   			</logic:notEmpty>
			   			<logic:notEmpty name="personForm"  property="resumeparty">
			   				<hrms:priv func_id="35012505">
			   					<a href="###"  id="Yspan${a0100 }" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.resumeparty }');">恢复党籍</a>
			   				</hrms:priv>
			   			</logic:notEmpty>
		   			</logic:equal>
		   			<logic:equal value="V" name="personForm" property="param">
		   				<hrms:priv func_id="35022102">
		   					<a href="###" onclick="winhrefOT('<bean:write name="element" property="a0100" filter="true"/>','nil_body');">编辑</a>
		   				</hrms:priv>
		   				<logic:notEmpty name="personForm"  property="iin">
		   					<hrms:priv func_id="35022103">
			   					<a href="###" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.iin }');">入团</a>
			   				</hrms:priv>
			   			</logic:notEmpty>
			   			<logic:notEmpty name="personForm"  property="resumemember">
			   				<hrms:priv func_id="35022104">
			   					<a href="###" id="Vspan${a0100 }" onclick="exeTemplate('${personForm.userbase }${a0100 }','${personForm.resumemember }');">恢复团籍</a>
			   				</hrms:priv>
			   			</logic:notEmpty>
		   			</logic:equal>
	    		</logic:equal>
	    	</td>
	    	</logic:notEmpty>   	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
        
</table>
</div>
<table style="width:expression(document.body.clientWidth-10);margin-right:5px;" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            	<hrms:paginationtag name="personForm"
								pagerows="${personForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="personForm" property="pagination" nameId="personForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>

   </td>
 </tr>
 <logic:notEmpty  name="personForm"  property="politics"> 
 <tr align="center">
   <td aling="center" style="padding-top: 5px">
   		<logic:equal value="party" name="personForm"  property="politics">
   			<logic:notEmpty name="personForm"  property="add">
   				<hrms:priv func_id="35012001">
   					<input type="button" class="mybutton" value="<bean:message key="button.insert"/>" onclick="exeTemplate('add','${personForm.add }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
   			<logic:notEmpty name="personForm"  property="leave">
   				<hrms:priv func_id="35012002">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.leaveparty"/>" onclick="exeTemplate('edit','${personForm.leave }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
   			<logic:notEmpty name="personForm"  property="iin">
   				<hrms:priv func_id="35012003">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.in"/>" onclick="exeTemplate('add','${personForm.iin }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
   			<logic:notEmpty name="personForm"  property="out">
   				<hrms:priv func_id="35012004">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.out"/>" onclick="exeTemplate('edit','${personForm.out }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
   			<hrms:priv func_id="35012005">
   				<input type="button" class="mybutton" value="<bean:message key="button.delete"/>" onclick="deleterec()"/>
   			</hrms:priv>
   		</logic:equal>
   		<logic:equal value="preparty" name="personForm" property="politics">
			<logic:notEmpty name="personForm"  property="add">
				<hrms:priv func_id="35012101">
   					<input type="button" class="mybutton" value="<bean:message key="button.insert"/>" onclick="exeTemplate('add','${personForm.add }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
			<logic:notEmpty name="personForm"  property="iin">
				<hrms:priv func_id="35012103">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.in"/>" onclick="exeTemplate('add','${personForm.iin }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
			<logic:notEmpty name="personForm"  property="out">
				<hrms:priv func_id="35012104">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.out"/>" onclick="exeTemplate('edit','${personForm.out }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
   			<logic:notEmpty name="personForm"  property="up">
   				<hrms:priv func_id="35012105">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.uppreparty"/>" onclick="exeTemplate('edit','${personForm.up }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
			<logic:notEmpty name="personForm"  property="leave">
   				<hrms:priv func_id="35012106">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.leave"/>" onclick="exeTemplate('edit','${personForm.leave }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
   			<hrms:priv func_id="35012107">
   				<input type="button" class="mybutton" value="<bean:message key="button.delete"/>" onclick="deleterec()"/>
			</hrms:priv>
		</logic:equal>
		<logic:equal value="important" name="personForm" property="politics">
			<logic:notEmpty name="personForm"  property="add">
				<hrms:priv func_id="35012201">
   					<input type="button" class="mybutton" value="<bean:message key="button.insert"/>" onclick="exeTemplate('add','${personForm.add }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
			<logic:notEmpty name="personForm"  property="iin">
				<hrms:priv func_id="35012203">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.in"/>" onclick="exeTemplate('add','${personForm.iin }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
			<logic:notEmpty name="personForm"  property="out">
   				<hrms:priv func_id="35012204">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.out"/>" onclick="exeTemplate('edit','${personForm.out }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
   			<logic:notEmpty name="personForm"  property="up">
   				<hrms:priv func_id="35012205">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.upimportant"/>" onclick="exeTemplate('edit','${personForm.up }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
			<logic:notEmpty name="personForm"  property="leave">
				<hrms:priv func_id="35012206">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.leave"/>" onclick="exeTemplate('edit','${personForm.leave }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
   			<hrms:priv func_id="35012207">
   				<input type="button" class="mybutton" value="<bean:message key="button.delete"/>" onclick="deleterec()"/>
			</hrms:priv>
		</logic:equal>
		<logic:equal value="active" name="personForm" property="politics">
			<logic:notEmpty name="personForm"  property="add">
				<hrms:priv func_id="35012301">
   					<input type="button" class="mybutton" value="<bean:message key="button.insert"/>" onclick="exeTemplate('add','${personForm.add }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
			<logic:notEmpty name="personForm"  property="iin">
				<hrms:priv func_id="35012303">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.in"/>" onclick="exeTemplate('add','${personForm.iin }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
			<logic:notEmpty name="personForm"  property="out">
				<hrms:priv func_id="35012304">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.out"/>" onclick="exeTemplate('edit','${personForm.out }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
   			<logic:notEmpty name="personForm"  property="up">
   				<hrms:priv func_id="35012305">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.upactive"/>" onclick="exeTemplate('edit','${personForm.up }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
			<logic:notEmpty name="personForm"  property="leave">
				<hrms:priv func_id="35012306">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.leave"/>" onclick="exeTemplate('edit','${personForm.leave }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
   			<hrms:priv func_id="35012307">
   				<input type="button" class="mybutton" value="<bean:message key="button.delete"/>" onclick="deleterec()"/>
			</hrms:priv>
		</logic:equal>
		<logic:equal value="application" name="personForm" property="politics">
			<logic:notEmpty name="personForm"  property="add">
				<hrms:priv func_id="35012401">
   					<input type="button" class="mybutton" value="<bean:message key="button.insert"/>" onclick="exeTemplate('add','${personForm.add }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
			<logic:notEmpty name="personForm"  property="iin">
				<hrms:priv func_id="35012403">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.in"/>" onclick="exeTemplate('add','${personForm.iin }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
			<logic:notEmpty name="personForm"  property="out">
				<hrms:priv func_id="35012404">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.out"/>" onclick="exeTemplate('edit','${personForm.out }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
   			<logic:notEmpty name="personForm"  property="up">
   				<hrms:priv func_id="35012405">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.upapplication"/>" onclick="exeTemplate('edit','${personForm.up }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
			<logic:notEmpty name="personForm"  property="leave">
				<hrms:priv func_id="35012406">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.leave"/>" onclick="exeTemplate('edit','${personForm.leave }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
   			<hrms:priv func_id="35012407">
   				<input type="button" class="mybutton" value="<bean:message key="button.delete"/>" onclick="deleterec()"/>
			</hrms:priv>
		</logic:equal>
		<logic:equal value="member" name="personForm" property="politics">
			<logic:notEmpty name="personForm"  property="add">
				<hrms:priv func_id="35022001">
   					<input type="button" class="mybutton" value="<bean:message key="button.insert"/>" onclick="exeTemplate('add','${personForm.add }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
			<logic:notEmpty name="personForm"  property="iin">
				<hrms:priv func_id="35022003">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.in"/>" onclick="exeTemplate('add','${personForm.iin }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
			<logic:notEmpty name="personForm"  property="out">
				<hrms:priv func_id="35022004">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.out"/>" onclick="exeTemplate('edit','${personForm.out }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
			<logic:notEmpty name="personForm"  property="leave">
				<hrms:priv func_id="35022005">
   					<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.leavemember"/>" onclick="exeTemplate('edit','${personForm.leave }');"/>
   				</hrms:priv>
   			</logic:notEmpty>
   			<hrms:priv func_id="35022006">
   				<input type="button" class="mybutton" value="<bean:message key="button.delete"/>" onclick="deleterec()"/>
			</hrms:priv>
		</logic:equal>
		<logic:equal value="person" name="personForm" property="politics">
			<logic:equal value="Y" name="personForm" property="param">
				<hrms:priv func_id="35012501">
   					<input type="button" class="mybutton" value="<bean:message key="button.insert"/>" onclick="exeAdd('/workbench/info/addselfinfo.do?b_add=add&a0100=A0100&i9999=I9999&userbase=${personForm.userbase }&actiontype=new&setname=A01&tolastpageflag=yes&a_code=${personForm.a_code }&politics=${personForm.politics }')"/> <!--zhaogd 2013-11-8 通过链接传userbase过去  --> 
				</hrms:priv>
				<logic:notEmpty name="personForm"  property="up">
					<hrms:priv func_id="35012502">
   						<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.upperson"/>" onclick="exeTemplate('edit','${personForm.up }');"/>
   					</hrms:priv>
   				</logic:notEmpty>
   				<hrms:priv func_id="35012503">
   					<input type="button" class="mybutton" value="<bean:message key="button.delete"/>" onclick="deleterec()"/>
				</hrms:priv>
			</logic:equal>
			<logic:equal value="V" name="personForm" property="param">
				<hrms:priv func_id="35022101">
   					<input type="button" class="mybutton" value="<bean:message key="button.insert"/>" onclick="exeAdd('/workbench/info/addselfinfo.do?b_add=add&a0100=A0100&i9999=I9999&userbase=${personForm.userbase }&actiontype=new&setname=A01&tolastpageflag=yes&a_code=${personForm.a_code }&politics=${personForm.politics }')"/>
				</hrms:priv>
				<logic:notEmpty name="personForm"  property="iin">
					<hrms:priv func_id="35022103">
   						<input type="button" class="mybutton" value="<bean:message key="dtgh.party.setup.bus.iin"/>" onclick="exeTemplate('edit','${personForm.iin }');"/>
   					</hrms:priv>
   				</logic:notEmpty>
   				<hrms:priv func_id="35022105">
   					<input type="button" class="mybutton" value="<bean:message key="button.delete"/>" onclick="deleterec()"/>
				</hrms:priv>
			</logic:equal>
		</logic:equal>
		<logic:equal value="dxt" name="personForm" property="returnvalue">
                <%
                   if (bosflag.equals("hcm"))
                   {
                %>
                <input type='button' class="mybutton" name="returnButton"
                    onclick='hrbreturn("dtgh", "il_body", "personForm");'
                    value='<bean:message key='reportcheck.return'/>' />
                <%
                   }
                %>
           </logic:equal>
     	<!-- input type="button" class="mybutton" value="<bean:message key='button.return'/>" onclick="returnback()"/-->
   </td>
 </tr>
 </logic:notEmpty>
 </table>

</html:form>
<script type="text/javascript">
<!--
	<logic:equal value="person" name="personForm"  property="politics">
	<logic:equal value="Y" name="personForm" property="param">
	<logic:notEmpty name="personForm"  property="resumeparty">
		canresumeparty();
	</logic:notEmpty>
	</logic:equal>
	<logic:equal value="V" name="personForm" property="param">
	<logic:notEmpty name="personForm"  property="resumemember">
		canresumemember();
	</logic:notEmpty>
	</logic:equal>
	</logic:equal>
//-->
</script>

