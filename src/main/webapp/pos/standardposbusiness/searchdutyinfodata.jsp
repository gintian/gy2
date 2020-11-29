<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hjsj.hrms.actionform.pos.StandardPosForm" %>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager="";
	boolean bookflag=false;
	int i=0;
	if(userView!=null){
		manager=userView.getManagePrivCodeValue();
		StandardPosForm standardPosForm=(StandardPosForm)session.getAttribute("standardPosForm");	
		String cardID = standardPosForm.getCardID();
		String ps_card_attach=standardPosForm.getPs_card_attach();
		if((!"-1".equalsIgnoreCase(cardID))||"true".equalsIgnoreCase(ps_card_attach)){
			bookflag=true;
		}
	}
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript">
var  divHeight = window.screen.availHeight - window.screenTop -120;
var code="";
var kind="";
var orgtype="";
var parentid="";
function checkDay(obj,ve)
{
    var o_obj=document.getElementById('day');   
    if(o_obj&&o_obj.checked==true)
    {
       var ttop  = obj.offsetTop;     //TT控件的定位点高
	   var thei  = obj.clientHeight;  //TT控件本身的高
	   var tleft = obj.offsetLeft;    //TT控件的定位点宽
	   var waitInfo=eval("wait")
	   while (obj = obj.offsetParent){ttop+=obj.offsetTop; tleft+=obj.offsetLeft;}
	   waitInfo.style.top=ttop+thei+6;
	   ve=3;
	   if(ve==1)
	      waitInfo.style.left=tleft+326;
	   else if(ve==2)   
	      waitInfo.style.left=tleft+220;
	   else
	      waitInfo.style.left=tleft;
	   waitInfo.style.display="";
	   
    }else
    { 
       var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
    }
}
function checkHide()
{
  Element.hide('wait');
}
function showOrClose()
{
		var obj=eval("aa");
	    var obj3=eval("vieworhidd");
		//var obj2=eval("document.standardPosForm.isShowCondition");
	    if(obj.style.display=='none')
	    {
    		obj.style.display='';
        	obj3.innerHTML="<a href=\"javascript:showOrClose();\"> 隐藏 </a>";
    	}
    	else
	    {
	    	obj.style.display='none';
	    	obj3.innerHTML="<a href=\"javascript:showOrClose();\"> 显示 </a>";
	    	
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
function turn()
{
   parent.menupnl.toggleCollapse(false);
}   
function  clearCheckbox()
{
   var len=document.standardPosForm.elements.length;
       var i;
     
        for (i=0;i<len;i++)
        {
         if (document.standardPosForm.elements[i].type=="checkbox"&&document.standardPosForm.elements[i].name!="orglike2"&&document.standardPosForm.elements[i].name!="querlike2")
          {
             
            document.standardPosForm.elements[i].checked=false;
          }
        }
}
function showOrgContext(codeitemid)
{
   var hashvo=new ParameterSet();
   hashvo.setValue("codeitemid",codeitemid);	
   var request=new Request({asynchronous:false,onSuccess:getContext,functionId:'0401004003'},hashvo);
}
function getContext(outparamters)
{
    code=outparamters.getValue("codeitemid");
	kind=outparamters.getValue("kind");
	orgtype=outparamters.getValue("orgtype");
	parentid=outparamters.getValue("parentid");
}
function showEmp(codeitemid)
{
	var backdate='${standardPosForm.backdate }';
	var crrdate=formatDate(new Date(),'yyyy-MM-dd');
	if(backdate==null||backdate.length==0||crrdate==backdate){
    	standardPosForm.action="/workbench/browse/scaninfodata.do?b_init=link&code="+codeitemid+"&orgflag=2&returnvalue=scanduty&scantype=scanduty&return_codeid=${standardPosForm.a_code}";
    }else{
    	standardPosForm.action="/workbench/browse/history/showinfo.do?b_orgsearch=link&code="+codeitemid+"&orgflag=2&returnvalue=scan&scantype=scanduty&return_codeid=${standardPosForm.a_code}&orgbackdate="+backdate;
    }
    clearCheckbox();
    turn();
    standardPosForm.submit();
}
function openwin(url)
{
   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
}
function query(query)
{
   standardPosForm.action="/workbench/dutyinfo/searchdutyinfodata.do?b_query=link&query="+query;
   standardPosForm.submit();
}
function search()
{
   standardPosForm.action="/pos/standardposbusiness/searchposlist.do?b_query=link";
   standardPosForm.submit();
}
function editorg()
{
   standardPosForm.action="/workbench/dutyinfo/editorginfodata.do?b_search=link&edit_flag=edit&code="+code+"&kind="+kind+"&orgtype="+orgtype+"&parentid="+parentid+"&returnvalue=scanduty&return_codeid=${standardPosForm.a_code}&isself=0";
   standardPosForm.submit();
}
function sacnorg()
{
	for(var i=0;i<document.standardPosForm.elements.length;i++)
	{			
	   if(document.standardPosForm.elements[i].type=='checkbox'&&document.standardPosForm.elements[i].name!="selbox")
	   {	
		  if(document.standardPosForm.elements[i].name!="orglike2"&&document.standardPosForm.elements[i].name!="querlike2")
		  {		   			
		    document.standardPosForm.elements[i].checked=false;
		  }
	   }
    }
   standardPosForm.action="/general/inform/pos/searchorgbrowse.do?b_search=link&code="+code+"&kind="+kind+"&orgtype="+orgtype+"&parentid="+parentid+"&returnvalue=scanduty&return_codeid=${standardPosForm.a_code}";
   standardPosForm.submit();
}
function delorg(codeitemid,rownum)
{
   if(confirm("你确定删除吗?注意:同时将删除职位库相对应的数据!"))
   {
       var hashvo=new ParameterSet();     
       var selectid=new Array();   
       selectid[0]=codeitemid;  
       hashvo.setValue("orgcodeitemid", selectid);
       hashvo.setValue("rownum",rownum);
       var request=new Request({method:'post',onSuccess:detRowTran,functionId:'16010000022'},hashvo);
      
   }
}
function detRowTran(outparamters)
{
     var checkperson=outparamters.getValue("checkperson"); 
     var orgitem=outparamters.getValue("orgitem");
     var rownum=outparamters.getValue("rownum");
     var orgcodeitemid=outparamters.getValue("orgcodeitemid");
     if(orgcodeitemid.length<=0)
        return false;
     var codeitemid=orgcodeitemid[0];
     var hashvo=new ParameterSet();  
     hashvo.setValue("rownum",rownum);
     hashvo.setValue("codeitemid",codeitemid);     
     if(checkperson=="true")
     {
     	var v="";
     	if(navigator.appName.indexOf("Microsoft")!= -1){
     		execScript("r=msgbox('是否清空下属人员的机构信息?',3,'提示')","vbscript");
     		v=r;
     	}else{
     		var dw=250,dh=100,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    		v = window.showModalDialog("/templates/info/msgbox.jsp","是否清空下属人员的机构信息?","dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogHeight:100px;dialogWidth:250px;status:no; help:no;scroll:no");
    	}
        //返回值必须是全局变量  
       if(v==6)
        {
          hashvo.setValue("delpersonorg","t");
          hashvo.setValue("orgid","");
          var request=new Request({method:'post',onSuccess:detRow,functionId:'0402000022'},hashvo);
        }
        else if(v==7)
        {
       /* var dw=500,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
          var target_url="/org/orginfo/searchorglist.do?b_choose=link&orgitem="+orgitem+"&code=${standardPosForm.a_code}";
          var return_vo= window.showModalDialog(target_url,0, 
          "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:yes");
          if(return_vo!=null)
          {
             var orgid=return_vo.orgid;  */           
             hashvo.setValue("delpersonorg","f");
             hashvo.setValue("orgid","");
             var request=new Request({method:'post',onSuccess:detRow,functionId:'0402000022'},hashvo);
          //}
        }
        else
        {
            return false;
        }     
     }
     else
     {
        hashvo.setValue("delpersonorg","f");
        hashvo.setValue("orgid","");
        var request=new Request({method:'post',onSuccess:detRow,functionId:'0402000022'},hashvo);
     }  
}
function detRow(outparamters)
{
   var flag=outparamters.getValue("flag");
   var rownum=outparamters.getValue("rownum");
   if(flag=="ok")
   {
     alert("删除成功！");
     var table=document.getElementById("pag");
     if(table==null)
  	   return false;  	
     var td_num=table.rows.length;    
     var i=parseInt(rownum,10);
     table.deleteRow(i);  
   }else
     alert("删除失败！");
}
function deleterec()
{
       var len=document.standardPosForm.elements.length;
       var uu;
       for (var i=0;i<len;i++)
       {
           if(document.standardPosForm.elements[i].type=='checkbox'&&document.standardPosForm.elements[i].name!="selbox")
	       {	
		      if(document.standardPosForm.elements[i].name!="orglike2"&&document.standardPosForm.elements[i].name!="querlike2")
		      {	
                if(document.standardPosForm.elements[i].checked==true)
                 {
                  uu="dd";
                  break;
                 }
              }
           }
       }
       if(uu!="dd")
       {
          alert("没有选择记录！");
          return false;
       }     
      if(confirm("你确定删除吗?注意:同时将删除岗位库相对应的数据!"))
      {
      
        if(selectcheckeditem()!=null)
        {
           var hashvo=new ParameterSet();          
           hashvo.setValue("orgcodeitemid", selectcheckeditem());
           var request=new Request({method:'post',onSuccess:deleteorg,functionId:'16010000022'},hashvo);
         }
      }
 }
 function deleteorg(outparamters)
 {
     var checkperson=outparamters.getValue("checkperson"); 
     var orgitem=outparamters.getValue("orgitem");
     if(checkperson=="true")
     {
     var v="";
       if(navigator.appName.indexOf("Microsoft")!= -1){
     		execScript("r=msgbox('是否清空下属人员的机构信息?',3,'提示')","vbscript");
     		v=r;
     	}else{
     		var dw=250,dh=100,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    		v = window.showModalDialog("/templates/info/msgbox.jsp","是否清空下属人员的机构信息?","dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogHeight:100px;dialogWidth:250px;status:no; help:no;scroll:no");
    	}
    	if(v==6)
        {
          standardPosForm.action="/workbench/dutyinfo/searchdutyinfodata.do?b_delete=del&delpersonorg=t";
          standardPosForm.submit();        
        }
        else if(v==7)
        {
        	/*var dw=500,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
          var target_url="/org/orginfo/searchorglist.do?b_choose=link&orgitem="+orgitem+"&code=${standardPosForm.a_code}";
          var return_vo= window.showModalDialog(target_url,0, 
          "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:yes");
          if(return_vo!=null)
          {
             var orgid=return_vo.orgid; */            
             standardPosForm.action="/workbench/dutyinfo/searchdutyinfodata.do?b_delete=del&delpersonorg=f";//&orgid="+orgid;
             standardPosForm.submit(); 
          //}
        }
        else
        {
        return false;
        }     
     }
     else
     {
        standardPosForm.action="/workbench/dutyinfo/searchdutyinfodata.do?b_delete=del&delpersonorg=f";
        standardPosForm.submit(); 
     }  
 }
 function selectcheckeditem()
   {
      	var a=0;
	var b=0;
	var selectid=new Array();
	var a_IDs=eval("document.standardPosForm.orgcodeitemid");	
	var nums=0;		
	for(var i=0;i<document.standardPosForm.elements.length;i++)
	{			
	   if(document.standardPosForm.elements[i].type=='checkbox'&&document.standardPosForm.elements[i].name!="selbox")
	   {	
		  if(document.standardPosForm.elements[i].name!="orglike2"&&document.standardPosForm.elements[i].name!="querlike2")
		  {		   			
		    nums++;
		  }
	   }
    }
	if(nums>1)
	{
	    for(var i=0;i<document.standardPosForm.elements.length;i++)
	    {			
		   if(document.standardPosForm.elements[i].type=='checkbox'&&document.standardPosForm.elements[i].name!="selbox")
		   {	
		     if(document.standardPosForm.elements[i].name!="orglike2"&&document.standardPosForm.elements[i].name!="querlike2")
		     {
		       if(document.standardPosForm.elements[i].checked==true)
		       {
			      selectid[a++]=a_IDs[b].value;						
		       }
		       b++;
		    }
		  }
	   }
	}
	if(nums==1)
	{
	   for(var i=0;i<document.standardPosForm.elements.length;i++)
	   {			
	      if(document.standardPosForm.elements[i].type=='checkbox'&&document.standardPosForm.elements[i].name!="selbox")
	      {	
	         if(document.standardPosForm.elements[i].name!="orglike2"&&document.standardPosForm.elements[i].name!="querlike2")
		     {
		        if(document.standardPosForm.elements[i].checked==true)
		        {
			       selectid[a++]=a_IDs.value;						
		        }
		     }
	      }
	   }
	}	
	if(selectid.length==0)
	{
		alert(REPORT_INFO9+"!");
		return ;
	}
	return selectid;	
 }  
 function bolish()
   {
   
   	   var len=document.standardPosForm.elements.length;
       var uu;
       for(var i=0;i<len;i++)
	   {			
	      if(document.standardPosForm.elements[i].type=='checkbox'&&document.standardPosForm.elements[i].name!="selbox")
	      {	
	         if(document.standardPosForm.elements[i].name!="orglike2"&&document.standardPosForm.elements[i].name!="querlike2")
		     {
               if(document.standardPosForm.elements[i].checked==true)
                {
                  uu="dd";
                  break;
               }
             }
          }
       }
       if(uu!="dd")
       {
          alert(NOTING_SELECT);
          return false;
       }
       if(confirm("你确定撤销吗?")){
        var hashvo=new ParameterSet();          
           hashvo.setValue("orgcodeitemid", selectcheckeditem());
           var In_paramters="";
           var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:exebolish,functionId:'16010000029'},hashvo);
       
       
       }
}


function exebolish(outparamters){
	var msg=outparamters.getValue('msg');
   		//alert(msg);
   		if(msg=="equals"){
   			alert(ORG_NEW_NOT_REVOKE);
   		}else if(msg="ok"){
	   		var maxstartdate=outparamters.getValue('maxstartdate');
		       standardPosForm.action="/workbench/dutyinfo/searchdutyinfodata.do?b_nullify=link&maxstartdate="+maxstartdate;
       			standardPosForm.submit();
        }else{
        	alert("检查能否此操作时失败，不允许此操作！");
        }
	 
}
function onebolish(s)
{
if(confirm("你确定撤销吗?")){
   s--;
   var len=document.standardPosForm.elements.length;
   for(var i=0;i<len;i++)
   {			
	      if(document.standardPosForm.elements[i].type=='checkbox'&&document.standardPosForm.elements[i].name!="selbox")
	      {	
	         if(document.standardPosForm.elements[i].name!="orglike2"&&document.standardPosForm.elements[i].name!="querlike2")
		     {
               document.standardPosForm.elements[i].checked=false;
             }
          }
    }
   var vo=document.getElementsByName("pagination.select["+s+"]")[0];
   vo.checked=true;
   var hashvo=new ParameterSet();          
           hashvo.setValue("orgcodeitemid", selectcheckeditem());
           var In_paramters="";
           var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:exebolish,functionId:'16010000029'},hashvo);
   //standardPosForm.action="/workbench/dutyinfo/searchdutyinfodata.do?b_nullify=link";
   //standardPosForm.submit();
   }
}

function newduty()
{
	if("<bean:write name="standardPosForm" property="a_code"/>"=="<bean:write name="standardPosForm" property="codesetid"/>") {
		window.alert("不能在根目录下新建基准岗位！请先选择左侧的岗位分类！");
	} else {
	    //standardPosForm.action="/workbench/dutyinfo/editorginfodata.do?b_search=link&edit_flag=new&returnvalue=scanduty&code=${standardPosForm.a_code}&kind=${standardPosForm.kind}&isself=0";
	    standardPosForm.action="/pos/standardposbusiness/searchposlist.do?b_tab=link&edit_flag=new&a_code=${standardPosForm.a_code}";
	    standardPosForm.submit();
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
<html:form action="/pos/standardposbusiness/searchposlist">
<table width='97%' border=0>
    <tr>
      <td height="30">
          <table>
             <tr>
                <td >
                    岗位分类：<!--  ${standardPosForm.codemess}-->
                    <logic:empty name="standardPosForm" property="codemess"><bean:write name="standardPosForm" property="codesetdesc"/></logic:empty>
                    <logic:notEmpty name="standardPosForm" property="codemess"><bean:write name="standardPosForm" property="codemess"/></logic:notEmpty>
          			&nbsp;&nbsp;&nbsp;&nbsp;
                </td>
                <td>
                <logic:notEmpty name="standardPosForm" property="selectfieldlist">
                  <table  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
                    <tr>
                       <td>岗位查询[&nbsp;
                       </td>
                       <td id="vieworhidd"> 
                       
                            <a href="javascript:showOrClose();"> 
							<logic:equal name="standardPosForm" property="isShowCondition" value="none" >显示</logic:equal>   
                            <logic:equal name="standardPosForm" property="isShowCondition" value="" >隐藏</logic:equal> 
							 </a>
                       
                       </td>                       
                       <td>&nbsp;]&nbsp;&nbsp;&nbsp;&nbsp;
                       </td>
                    </tr>
                  </table>
                  </logic:notEmpty>
                </td>
                <td>
                 <logic:equal name="standardPosForm" property="orglike" value="1">
                     <input type="checkbox" id="orglikeid1" name="orglike2" value="true" onclick="selectCheckBox(document.getElementById('orglikeid1'),'orglike');search();" checked>
                 </logic:equal>
                 <logic:notEqual name="standardPosForm" property="orglike" value="1">
                     <input type="checkbox" id="orglikeid2" name="orglike2" value="true" onclick="selectCheckBox(document.getElementById('orglikeid2'),'orglike');search();">
                 </logic:notEqual>
                 
               <html:hidden name="standardPosForm" styleId="orglike" property='orglike' styleClass="text"/>                 
                     显示当前分类下所有岗位
                </td>
             </tr>
          </table>
      </td>
    </tr>
   <tr>     
     <td>
     
        <%
	
	int flag=0;
	int j=0;
	int n=0;
%>
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id='aa' style='display:${standardPosForm.isShowCondition}'>
    <tr>
     <td>
       <!-- 查询开始 -->
       <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow">
          <tr class="trShallow1">
           <td align="center" colspan="4" height='20' class="RecordRow" nowrap>
            <bean:message key="label.query.inforquery"/><!-- 请选择查询条件! -->
           </td>
          </tr> 
           <logic:iterate id="element" name="standardPosForm"  property="selectfieldlist" indexId="index"> 
                  <% 
                    if(flag==0)
                     {
                        out.println("<tr class=\"trShallow1\">");
                         flag=1;          
                      }else{
                           flag=0;       
                      }
                    %>            
                          <td align="right" height='28' nowrap>                
                            <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;&nbsp;&nbsp;&nbsp;
                          </td>
                          <!--日期型 -->                            
                          <logic:equal name="element" property="itemtype" value="D">
                            <td align="left" height='28' nowrap>    
                               
                               <html:text name="standardPosForm" property='<%="selectfieldlist["+index+"].value"%>' size="13" maxlength="10" styleClass="textbox" title="输入格式：2008.08.08" onclick=""/>
                               <bean:message key="label.query.to"/><html:text name="standardPosForm" property='<%="selectfieldlist["+index+"].viewvalue"%>' size="13" maxlength="10" styleClass="TEXT4" title="输入格式：2008.08.08"  onclick=""/>
			                   <!-- 没有什么用，仅给用户与视觉效果-->
			                   <INPUT type="radio" name="${element.itemid}"  checked="true"><bean:message key="label.query.age"/>	
			                   <INPUT type="radio" name="${element.itemid}" id="day"><bean:message key="label.query.day"/>
			                    	
                            </td>                           
                          </logic:equal>
                          <!--备注型 -->                              
                          <logic:equal name="element" property="itemtype" value="M">
                            <td align="left" height='28' nowrap>                
                               <html:text name="standardPosForm" property='<%="selectfieldlist["+index+"].value"%>' size="30" maxlength='<%="selectfieldlist["+index+"].itemlength"%>' styleClass="textbox"/>                               
                            </td>                           
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="itemtype" value="A">
                            <td align="left" height='28' nowrap>
                              <logic:notEqual name="element" property="codesetid" value="0">
                                <html:hidden name="standardPosForm" property='<%="selectfieldlist["+index+"].value"%>' styleClass="text"/>                               
                                <html:text name="standardPosForm" property='<%="selectfieldlist["+index+"].viewvalue"%>' size="30" maxlength="50" styleClass="textbox" onchange="fieldcode(this,2);"/>
                                  <logic:notEqual name="element" property="codesetid" value="UN">  
                                    <logic:equal name="element" property="itemid" value="e0122"> 
                                           <img src="/images/code.gif" onclick='openInputCodeDialogOrgInputPos("UM","<%="selectfieldlist["+index+"].viewvalue"%>","<%=manager%>",1);' align="absmiddle"/>
                                    </logic:equal>    
                                    <logic:notEqual name="element" property="itemid" value="e0122"> 
                                        <img src="/images/code.gif" onclick='openInputCodeDialog("${element.codesetid}","<%="selectfieldlist["+index+"].viewvalue"%>");' align="absmiddle"/>
                                     </logic:notEqual>                                                                                                 
                                  </logic:notEqual>   
                                  <logic:equal name="element" property="codesetid" value="UN">                                      
                                         <logic:equal name="element" property="itemid" value="b0110"> 
                                           <img src="/images/code.gif" onclick='openInputCodeDialog("UM","<%="selectfieldlist["+index+"].viewvalue"%>","0");' align="absmiddle"/>
                                         </logic:equal> 
                                         <logic:notEqual name="element" property="itemid" value="b0110"> 
                                           <img src="/images/code.gif" onclick='openInputCodeDialog("UM","<%="selectfieldlist["+index+"].viewvalue"%>","0");' align="absmiddle"/>
                                         </logic:notEqual>   
                                  </logic:equal>                                                                                                       
                              </logic:notEqual> 
                              <logic:equal name="element" property="codesetid" value="0">
                                <html:text name="standardPosForm" property='<%="selectfieldlist["+index+"].value"%>' size="30" maxlength="${element.itemlength}" styleClass="textbox"/>                               
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="itemtype" value="N">
                            <td align="left" height='28' nowrap>                
                               <html:text name="standardPosForm" property='<%="selectfieldlist["+index+"].value"%>' size="30" maxlength="${element.itemlength}" styleClass="textbox"/>                               
                            </td>                           
                          </logic:equal>                           
                        <%
                      if(flag==0)
        			    out.println("</tr>");
                     %>           
                  </logic:iterate>
                   <%
                  if(flag==1)
    	          {
    		        out.println("<td colspan=\"2\">");
                     out.println("</td>");
                     out.println("</tr>");
                  }
                 %> 
                  <tr class="trShallow1">
    	             <td align="right" height='20'  nowrap>
    	            <bean:message key="label.query.like"/>&nbsp; 
    	            </td>
    	            <td align="left" colspan="3" height='20' nowrap>
                      <input type="checkbox" name="querlike2" value="true" onclick="selectCheckBox(this,'querylike');">
                    <html:hidden name="standardPosForm" property='querylike' styleClass="text"/> 
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
         <td align="center" height='20'>                 
              <Input type='button' value="<bean:message key="infor.menu.query"/>" onclick='query(1);' class='mybutton' />  
         </td>   
      </tr>
   </table>
  </td>
 </tr>

   <tr>
     <td>
         <table  width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >
          <tr>
             <td>
                <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" id="pag">
   	             <tr>
   	              <td align="center" class="TableRow" nowrap>
		            <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
                 </td>
		          <logic:iterate id="element"    name="standardPosForm"  property="fieldList" indexId="index">
                   <td align="center" class="TableRow" nowrap>
                 &nbsp;&nbsp;&nbsp; <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;&nbsp;&nbsp;
	       	       </td>            
                 </logic:iterate> 
                 <%if(bookflag){ %>
                   <hrms:priv func_id="23060106"> 
                    <td align="center" class="TableRow" nowrap>
		     		&nbsp;说明书&nbsp;
		            </td>
		           </hrms:priv>
                 <%} %>
                 <hrms:priv func_id="23110105"> 
                 <td align="center" class="TableRow" nowrap>
		     		&nbsp;人员浏览&nbsp;
		          </td> 
		          </hrms:priv>
		          <td align="center" class="TableRow" nowrap>
		     		&nbsp;操作&nbsp;
		          </td> 		         
               </tr>
                <hrms:paginationdb id="element" name="standardPosForm" sql_str="standardPosForm.sqlstr" table="" where_str="standardPosForm.wherestr" columns="standardPosForm.columnstr" order_by="${standardPosForm.orderby}" pagerows="${standardPosForm.pagerows}" page_id="pagination"  distinct="" keys="">
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
                   %> 
                   <%
             	   LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
             	   String codeitme=(String)abean.get("h0100");              	            	   
                   request.setAttribute("codeitme",codeitme);  
                   request.setAttribute("codeitme_encrypt",PubFunc.encrypt(codeitme));    	                           
                   %>    
                   <td align="center" class="RecordRow" nowrap>
                      <input type="hidden" name="orgcodeitemid" value="${codeitme}"> 
                      <hrms:checkmultibox name="standardPosForm" property="pagination.select"  value="true" indexes="indexes"/>&nbsp;
                   </td>               
                   <logic:iterate id="fielditem"  name="standardPosForm"  property="fieldList" indexId="index">
                        <logic:notEqual name="fielditem" property="codesetid" value="0">
                           <td align="left" class="RecordRow" nowrap>&nbsp;
                                <logic:equal name="fielditem" property="itemid" value="e0121">
                                  <hrms:codetoname codeid="UN" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	                      <bean:write name="codeitem" property="codename" />  
                                  <hrms:codetoname codeid="UM" name="element" codevalue="${fielditem.itemid}" uplevel="${standardPosForm.uplevel}"  codeitem="codeitem" scope="page"/>  	      
          	                      <bean:write name="codeitem" property="codename" />&nbsp;  
          	                   </logic:equal>
          	                    <logic:notEqual name="fielditem" property="itemid" value="e01a1">
          	                     <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" uplevel="5"  scope="page"/>  	      
          	                      <bean:write name="codeitem" property="codename" />&nbsp;                    
                                </logic:notEqual>  
          	                    <logic:equal name="fielditem" property="itemid" value="e01a1">
          	                     <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem"  scope="page"/>  	      
          	                      <bean:write name="codeitem" property="codename" />&nbsp;                    
                                </logic:equal>                                                 
                            </td>
                        </logic:notEqual>
                        <logic:equal name="fielditem" property="codesetid" value="0">
                            <logic:equal name="fielditem" property="itemtype" value="N">
                              <td align="right" class="RecordRow" nowrap>&nbsp;
                                 <bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;
                              </td>
                            </logic:equal>
                            <logic:notEqual name="fielditem" property="itemtype" value="N">
                              <td align="left" class="RecordRow" nowrap>&nbsp;
                                 <bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;
                              </td>
                            </logic:notEqual>
                        </logic:equal>                          
                     </logic:iterate>
                     <%if(bookflag){ %>
                      <hrms:priv func_id="23060106"> 
                     <td align="left" class="RecordRow" nowrap>
                     <logic:notEqual name="standardPosForm" property="cardID" value="-1">
                       		&nbsp;&nbsp;
                          <a href="###" onclick='openwin("/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase=&a0100=${codeitme_encrypt}&inforkind=4&tabid=${standardPosForm.cardID}&multi_cards=-1");'>
               			   <img src="../../images/table.gif" border="0"></a>
               			
               		</logic:notEqual>
                    <logic:equal name="standardPosForm" property="ps_card_attach" value="true">
			                 &nbsp;<hrms:browseaffix pertain_to="ps" a0100="${codeitme}" nbase=""></hrms:browseaffix>  
			         </logic:equal>
		              </td>
		              </hrms:priv>
                      <%} %>
                      <hrms:priv func_id="23110105">
                      <td align="center" class="RecordRow" nowrap>
		     		        &nbsp;&nbsp;
		     		       <a href="javascript:showEmp('${codeitme}');"><img src="/images/view.gif" border="0"></a>
		     		        &nbsp;&nbsp;
		              </td> 
		              </hrms:priv>
		                  <td align="center" class="RecordRow" nowrap>
		                    &nbsp;&nbsp;
		     	         	<hrms:priv func_id=""> 
		     	         	<a href="javascript:showOrgContext('${codeitme}');editorg();">编辑</a>
		     	         	&nbsp;&nbsp;&nbsp;
		     	         	</hrms:priv>
		     	         	<hrms:priv func_id=""> 
		     	         	<a href="javascript:showOrgContext('${codeitme}');delorg('${codeitme}','<%=i%>')">删除</a>
		     	         	&nbsp;&nbsp;&nbsp;
		     	         	</hrms:priv>
		     	         	<hrms:priv func_id=""> 
		     	         	<a href="javascript:onebolish(<%=i%>);">撤销</a>
		     	         	</hrms:priv>
		     	         	&nbsp;&nbsp;
		     	         	<a href="javascript:showOrgContext('${codeitme}');sacnorg();">浏览</a>
		     	         	&nbsp;&nbsp;
		                </td> 
		                
                    </tr>
                  </hrms:paginationdb>
               </table>
             </td>
          </tr>
          <tr> 
          <td>
            <table  width="100%" align="center" class="RecordRowP">
		     <tr>
		      <td valign="bottom" class="tdFontcolor">
		            <hrms:paginationtag name="standardPosForm"
								pagerows="${standardPosForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			  </td>
	               <td  align="left" nowrap class="tdFontcolor">
		           <p align="left"><hrms:paginationdblink name="standardPosForm" property="pagination" nameId="standardPosForm" scope="page">
				  </hrms:paginationdblink></p>
			   </td>
		     </tr>
            </table>
          </td>
         </tr>
         <tr height="35">
          <td>
            <hrms:priv func_id=""> 
              <input type="button" name="addbutton"  value="新建基准岗位" class="mybutton" onclick='newduty();' > 
            </hrms:priv>
            <hrms:priv func_id="">    
            <input type="button" name="addbutton"  value="撤销所选基准岗位" class="mybutton" onclick='bolish();' >
            </hrms:priv>
            <hrms:priv func_id=""> 
            <input type="button" name="addbutton"  value="删除所选基准岗位" class="mybutton" onclick='deleterec();' > 
            </hrms:priv> 
          </td>
         </tr>
       </table>
     </td>
   </tr>  
  </table>
</html:form>
<div id='wait' style='display:none;position: absolute; left:0; top:0;'>
   <font color="red">输入格式：2008.08.08</font>
</div> 