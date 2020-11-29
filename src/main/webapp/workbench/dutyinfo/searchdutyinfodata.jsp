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
<%@ page import="com.hjsj.hrms.actionform.duty.DutyInfoForm" %>
<% 
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager="";
	boolean bookflag=false;
	int i=0;
	String bosflag="";
	ArrayList selectfieldlist = new ArrayList();
	if(userView!=null){
		//manager=userView.getManagePrivCodeValue();
	    /**cmq changed at 20121001 for 权限控制优先级 业务范围-操作单位-管理范围*/
		manager=userView.getUnitIdByBusi("4");
	    //end.
		DutyInfoForm dutyInfoForm=(DutyInfoForm)session.getAttribute("dutyInfoForm");	
		selectfieldlist= dutyInfoForm.getSelectfieldlist();
		String cardID = dutyInfoForm.getCardID();
		String ps_card_attach=dutyInfoForm.getPs_card_attach();
		if((!"-1".equalsIgnoreCase(cardID))||"true".equalsIgnoreCase(ps_card_attach)){
			bookflag=true;
		}
		bosflag=userView.getBosflag(); 
	}
	session.removeAttribute("code");
%>
<script language="javascript" src="/module/utils/js/template.js"></script>
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
		//var obj2=eval("document.dutyInfoForm.isShowCondition");
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
   var len=document.dutyInfoForm.elements.length;
       var i;
     
        for (i=0;i<len;i++)
        {
         if (document.dutyInfoForm.elements[i].type=="checkbox"&&document.dutyInfoForm.elements[i].name!="orglike2"&&document.dutyInfoForm.elements[i].name!="querlike2")
          {
             
            document.dutyInfoForm.elements[i].checked=false;
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
function showEmp(codeitemid,end_dates)
{
	var backdate='${orgInfoForm.backdate }';
	var crrdate=formatDate(new Date(),'yyyy-MM-dd');
	//if(backdate==null||backdate.length==0||crrdate==backdate){
	if(crrdate <end_dates){
    	dutyInfoForm.action="/workbench/browse/scaninfodata.do?b_init=link&code="+codeitemid+"&orgflag=2&returnvalue=scanduty&scantype=scanduty&return_codeid=${dutyInfoForm.code}";
    }else{
    	dutyInfoForm.action="/workbench/browse/history/showinfo.do?b_orgsearch=link&code="+codeitemid+"&orgflag=2&returnvalue=scan&scantype=scanduty&return_codeid=${dutyInfoForm.code}&orgbackdate="+backdate;
    }
    clearCheckbox();
    turn();
    dutyInfoForm.submit();
}
function openwin(url)
{
	window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",channelmode=yes,scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=yes,status=no");
}
function query(query)
{
   dutyInfoForm.action="/workbench/dutyinfo/searchdutyinfodata.do?b_query=link&query="+query;
   dutyInfoForm.submit();
}
function search()
{
   dutyInfoForm.action="/workbench/dutyinfo/searchdutyinfodata.do?b_search=link";
   dutyInfoForm.submit();
}
function editorg()
{
   dutyInfoForm.action="/workbench/dutyinfo/editorginfodata.do?b_search=link&edit_flag=edit&code="+code+"&kind="+kind+"&orgtype="+orgtype+"&parentid="+parentid+"&returnvalue=scanduty&return_codeid=${dutyInfoForm.code}&isself=0";
   dutyInfoForm.submit();
}
function sacnorg()
{
	for(var i=0;i<document.dutyInfoForm.elements.length;i++)
	{			
	   if(document.dutyInfoForm.elements[i].type=='checkbox'&&document.dutyInfoForm.elements[i].name!="selbox")
	   {	
		  if(document.dutyInfoForm.elements[i].name!="orglike2"&&document.dutyInfoForm.elements[i].name!="querlike2")
		  {		   			
		    document.dutyInfoForm.elements[i].checked=false;
		  }
	   }
    }
   dutyInfoForm.action="/general/inform/pos/searchorgbrowse.do?b_search=link&code="+code+"&kind="+kind+"&orgtype="+orgtype+"&parentid="+parentid+"&returnvalue=scanduty&return_codeid=${dutyInfoForm.code}";
   dutyInfoForm.submit();
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
     	if(getBrowseVersion()){
     		execScript("r=msgbox('是否清空下属人员的机构信息?',3,'提示')","vbscript");
     		v=r;
            rightDeleteSelectPosWinReturn(v,hashvo);
     	}else{
            Ext.create("Ext.window.Window",{
                id:"deleteSelectPosWin",
                title:'提示',
                width:250,
                height:120,
                content:"是否清空下属人员的机构信息?",
                resizable:false,
                modal:true,
                autoScroll:false,
                autoShow:true,
                autoDestroy:true,
                renderTo:Ext.getBody(),
                html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='/templates/info/msgbox.jsp'></iframe>",
                listeners:{
                    close:function () {
                        rightDeleteSelectPosWinReturn(this.return_vo,hashvo);
                    }
                }
            });
     	}
     }
     else
     {
        hashvo.setValue("delpersonorg","f");
        hashvo.setValue("orgid","");
        var request=new Request({method:'post',onSuccess:detRow,functionId:'0402000022'},hashvo);
     }  
}
function rightDeleteSelectPosWinReturn(return_vo,hashvo){

    if(return_vo==6){
        hashvo.setValue("delpersonorg","t");
        hashvo.setValue("orgid","");
        var request=new Request({method:'post',onSuccess:detRow,functionId:'0402000022'},hashvo);
    }else if(return_vo==7){
        hashvo.setValue("delpersonorg","f");
        hashvo.setValue("orgid","");
        var request=new Request({method:'post',onSuccess:detRow,functionId:'0402000022'},hashvo);
    }else{
        return false;
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
function deleterec(btn)
{ 
       window.delbtn = btn;
       var len=document.dutyInfoForm.elements.length;
       var uu;
       for (var i=0;i<len;i++)
       {
           if(document.dutyInfoForm.elements[i].type=='checkbox'&&document.dutyInfoForm.elements[i].name!="selbox")
	       {	
		      if(document.dutyInfoForm.elements[i].name!="orglike2"&&document.dutyInfoForm.elements[i].name!="querlike2")
		      {	
                if(document.dutyInfoForm.elements[i].checked==true)
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
         if(getBrowseVersion()){
            execScript("r=msgbox('是否清空下属人员的机构信息?',3,'提示')","vbscript");
            v=r;
            bottomDeleteSelectPosWinReturn(v);
        }else{
             Ext.create("Ext.window.Window",{
                 id:"deleteSelectPosWin",
                 title:'提示',
                 width:250,
                 height:120,
                 content:"是否清空下属人员的机构信息?",
                 resizable:false,
                 modal:true,
                 autoScroll:false,
                 autoShow:true,
                 autoDestroy:true,
                 renderTo:Ext.getBody(),
                 html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='/templates/info/msgbox.jsp'></iframe>",
                 listeners:{
                     close:function () {
                         bottomDeleteSelectPosWinReturn(this.return_vo);
                     }
                 }
             });
        }
     }else{
        dutyInfoForm.action="/workbench/dutyinfo/searchdutyinfodata.do?b_delete=del&delpersonorg=f";
         window.delbtn.disabled = true;
         dutyInfoForm.submit();
     }
 }

 function bottomDeleteSelectPosWinReturn(return_value){
     if(return_value==6){
         dutyInfoForm.action="/workbench/dutyinfo/searchdutyinfodata.do?b_delete=del&delpersonorg=t";
     }
     else if(return_value==7){
         dutyInfoForm.action="/workbench/dutyinfo/searchdutyinfodata.do?b_delete=del&delpersonorg=f";
     }else{
         return false;
     }
     window.delbtn.disabled = true;
     dutyInfoForm.submit();
 }

 function selectcheckeditem()
   {
      	var a=0;
	var b=0;
	var selectid=new Array();
	var a_IDs=eval("document.dutyInfoForm.orgcodeitemid");	
	var nums=0;		
	for(var i=0;i<document.dutyInfoForm.elements.length;i++)
	{			
	   if(document.dutyInfoForm.elements[i].type=='checkbox'&&document.dutyInfoForm.elements[i].name!="selbox")
	   {	
		  if(document.dutyInfoForm.elements[i].name!="orglike2"&&document.dutyInfoForm.elements[i].name!="querlike2")
		  {		   			
		    nums++;
		  }
	   }
    }
	if(nums>1)
	{
	    for(var i=0;i<document.dutyInfoForm.elements.length;i++)
	    {			
		   if(document.dutyInfoForm.elements[i].type=='checkbox'&&document.dutyInfoForm.elements[i].name!="selbox")
		   {	
		     if(document.dutyInfoForm.elements[i].name!="orglike2"&&document.dutyInfoForm.elements[i].name!="querlike2")
		     {
		       if(document.dutyInfoForm.elements[i].checked==true)
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
	   for(var i=0;i<document.dutyInfoForm.elements.length;i++)
	   {			
	      if(document.dutyInfoForm.elements[i].type=='checkbox'&&document.dutyInfoForm.elements[i].name!="selbox")
	      {	
	         if(document.dutyInfoForm.elements[i].name!="orglike2"&&document.dutyInfoForm.elements[i].name!="querlike2")
		     {
		        if(document.dutyInfoForm.elements[i].checked==true)
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
   
   	   var len=document.dutyInfoForm.elements.length;
       var uu;
       for(var i=0;i<len;i++)
	   {			
	      if(document.dutyInfoForm.elements[i].type=='checkbox'&&document.dutyInfoForm.elements[i].name!="selbox")
	      {	
	         if(document.dutyInfoForm.elements[i].name!="orglike2"&&document.dutyInfoForm.elements[i].name!="querlike2")
		     {
               if(document.dutyInfoForm.elements[i].checked==true)
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
	
   		if(msg=="equals"){
   			alert(ORG_NEW_NOT_REVOKE);
   		}else if(msg="ok"){ 
	   		var maxstartdate=outparamters.getValue('maxstartdate');
		       dutyInfoForm.action="/workbench/dutyinfo/searchdutyinfodata.do?b_nullify=link&maxstartdate="+maxstartdate;
       			dutyInfoForm.submit();
        }else{
        	alert("检查能否此操作时失败，不允许此操作！");
        }
	 
}
function onebolish(s)
{
if(confirm("你确定撤销吗?")){
   s--;
   var len=document.dutyInfoForm.elements.length;
   for(var i=0;i<len;i++)
   {			
	      if(document.dutyInfoForm.elements[i].type=='checkbox'&&document.dutyInfoForm.elements[i].name!="selbox")
	      {	
	         if(document.dutyInfoForm.elements[i].name!="orglike2"&&document.dutyInfoForm.elements[i].name!="querlike2")
		     {
               document.dutyInfoForm.elements[i].checked=false;
             }
          }
    }
   var vo=document.getElementsByName("pagination.select["+s+"]")[0];
   vo.checked=true;
   var hashvo=new ParameterSet();          
           hashvo.setValue("orgcodeitemid", selectcheckeditem());
           var In_paramters="";
           var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:exebolish,functionId:'16010000029'},hashvo);
   //dutyInfoForm.action="/workbench/dutyinfo/searchdutyinfodata.do?b_nullify=link";
   //dutyInfoForm.submit();
   }
}
var codeItemId;
function onecopy(codeitemid)
{
   codeItemId = codeitemid;
   var return_vo=select_org_dialog1(0,1,0,1,0,1,4);   
   if(return_vo)
   {
       if(return_vo.content)
    	{
        	if(confirm("确定把岗位复制到指定的组织单元?"))
	        {
    	        var hashvo=new ParameterSet();
        	    var selectid=new Array();
            	selectid[0]=codeItemId;
	            hashvo.setValue("orgcodeitemid", selectid);
	            hashvo.setValue("content", return_vo.content);
    	        var request=new Request({method:'post',onSuccess:addduty,functionId:'0402000028'},hashvo);
        	}
    	}
   }
   if(Ext.getCmp('select_org_dialog1_win'))
     Ext.getCmp('select_org_dialog1_win').type='onecopy';
}
function copySelectOrgWinReturn1(return_vo){
if(Ext.getCmp('select_org_dialog1_win')){
	var win = Ext.getCmp('select_org_dialog1_win');
	if(win.type == 'onecopy'){
		if(return_vo.content)
    	{
        	if(confirm("确定把岗位复制到指定的组织单元?"))
	        {
    	        var hashvo=new ParameterSet();
        	    var selectid=new Array();
            	selectid[0]=codeItemId;
	            hashvo.setValue("orgcodeitemid", selectid);
	            hashvo.setValue("content", return_vo.content);
    	        var request=new Request({method:'post',onSuccess:addduty,functionId:'0402000028'},hashvo);
        	}
    	}
	}else if(win.type == 'copyduty'){
		if(return_vo)
    	{
        	if(return_vo.content!="")
        	{
           		if(confirm("确定把岗位复制到指定的组织单元?"))
           		{
            		var hashvo=new ParameterSet();          
            		hashvo.setValue("content", return_vo.content);
            		hashvo.setValue("orgcodeitemid", selectcheckeditem());
            		var request=new Request({method:'post',onSuccess:addduty,functionId:'0402000028'},hashvo);
            	}
       		}
   		} 
	}
}

       
}
function copyduty()
{
   	   var len=document.dutyInfoForm.elements.length;
       var uu;
       for(var i=0;i<len;i++)
	   {			
	      if(document.dutyInfoForm.elements[i].type=='checkbox'&&document.dutyInfoForm.elements[i].name!="selbox")
	      {	
	         if(document.dutyInfoForm.elements[i].name!="orglike2"&&document.dutyInfoForm.elements[i].name!="querlike2")
		     {
               if(document.dutyInfoForm.elements[i].checked==true)
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
       var return_vo=select_org_dialog1(0,1,0,1,0,1,4);   
       if(return_vo)
       {
           if(return_vo.content!="")
           {
              if(confirm("确定把岗位复制到指定的组织单元?"))
              {
                var hashvo=new ParameterSet();          
                hashvo.setValue("content", return_vo.content);
                hashvo.setValue("orgcodeitemid", selectcheckeditem());
                var request=new Request({method:'post',onSuccess:addduty,functionId:'0402000028'},hashvo);
              }
           }
       }    
       if(Ext.getCmp('select_org_dialog1_win'))
   	 		Ext.getCmp('select_org_dialog1_win').type='copyduty';   
}
function addduty(outparamters)
{
    var flag=outparamters.getValue("flag");
	if(flag=="ok")
	{
	  alert("复制操作成功！");
	  search();
	}else
	{
	    alert("复制操作失败！");
	}	
}
function newduty()
{
	if("<bean:write name="dutyInfoForm" property="code"/>"=="") {
		window.alert("不能在根目录下新建岗位！请先选择左侧的组织单元！");
	} else {
	    dutyInfoForm.action="/workbench/dutyinfo/editorginfodata.do?b_search=link&edit_flag=new&returnvalue=scanduty&code=${dutyInfoForm.code}&kind=${dutyInfoForm.kind}&isself=0";
	    dutyInfoForm.submit();
    }
}
function transfer()
   {
   		/*if(selectcheckedorg())
   		{
   			alert("您选择了虚拟机构，不允许此操作！");
   			return;
   		}*/
   	   var len=document.dutyInfoForm.elements.length;
       var uu;
       for (var i=0;i<len;i++)
       {
           if (document.dutyInfoForm.elements[i].type=="checkbox"&&document.dutyInfoForm.elements[i].name!="orglike2"&&document.dutyInfoForm.elements[i].name!="querlike2"&&document.dutyInfoForm.elements[i].name!="selbox")
           {
              if(document.dutyInfoForm.elements[i].checked==true)
              {
                uu="dd";
                break;
               }
           }
       }
       if(uu!="dd")
       {
          alert(ORG_ORGINFO_INFO01);
          return false;
       }
      	   var hashvo=new ParameterSet();          
           hashvo.setValue("orgcodeitemid", selectcheckeditem());
           var In_paramters="";
           var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:exetransfer,functionId:'16010000029'},hashvo);
   }
   function exetransfer(outparamters){
   		var msg=outparamters.getValue('msg');
   		//alert(msg);
   		if(msg=="equals"){
   			alert(ORG_NEW_NOT_REVOKE);
   		}else if(msg="ok"){
	   		var maxstartdate=outparamters.getValue('maxstartdate');
	   		dutyInfoForm.action="/workbench/dutyinfo/searchdutyinfodata.do?b_transfer=link&maxstartdate="+maxstartdate;
	        dutyInfoForm.submit(); 
        }else{
        	alert("检查能否此操作时失败，不允许此操作！");
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
 <hrms:themes />
<html:form action="/workbench/dutyinfo/searchdutyinfodata">
<table width='97%'  border=0 style="margin-top: -4px;" cellpadding="0" cellspacing="0">
    <tr>
      <td height="25">
          <table>
             <tr>
                <td >
                    当前组织单元：<!--  ${dutyInfoForm.codemess}-->
                    <logic:empty name="dutyInfoForm" property="codemess"><bean:write name="dutyInfoForm" property="root"/></logic:empty>
                    <logic:notEmpty name="dutyInfoForm" property="codemess"><bean:write name="dutyInfoForm" property="codemess"/></logic:notEmpty>
          			&nbsp;&nbsp;&nbsp;&nbsp;
                </td>
                <td>
              <%//if(selectfieldlist!=null&&selectfieldlist.size()>0) {%>
                <logic:notEmpty name="dutyInfoForm" property="selectfieldlist">
                 <table  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
                    <tr height=17>
                       <td valign="top"><font size="1">[</font>&nbsp;
                       </td>
                       <td id="vieworhidd" valign="bottom"> 
                                                                                
                            <a href="javascript:showOrClose();" > 
                                     <logic:equal name="dutyInfoForm" property="isShowCondition" value="none" >查询显示</logic:equal>   
                                     <logic:equal name="dutyInfoForm" property="isShowCondition" value="" >查询隐藏</logic:equal>                                      
                            </a>
                       </td>                       
                       <td  valign="top">&nbsp;<font size="1">]</font>&nbsp;&nbsp;&nbsp;&nbsp;
                       </td>
                    </tr>
                  </table>
                  </logic:notEmpty>
               <%//} %>
                </td>
                <td>
                 <logic:equal name="dutyInfoForm" property="orglike" value="1">
                     <input type="checkbox" id="orglikeid1" name="orglike2" value="true" onclick="selectCheckBox(document.getElementById('orglikeid1'),'orglike');search();" checked>
                 </logic:equal>
                 <logic:notEqual name="dutyInfoForm" property="orglike" value="1">
                     <input type="checkbox" id="orglikeid2" name="orglike2" value="true" onclick="selectCheckBox(document.getElementById('orglikeid2'),'orglike');search();">
                 </logic:notEqual>
                 
               <html:hidden name="dutyInfoForm" styleId="orglike" property='orglike' styleClass="text"/>                 
                     显示当前组织单元下所有组织单元的岗位
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
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id='aa' style='display:${dutyInfoForm.isShowCondition}'>
    <tr>
     <td>
       <!-- 查询开始 -->
       <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow">
          <tr class="trShallow1">
           <td align="center" colspan="4" height='20' class="RecordRow" nowrap>
            <bean:message key="label.query.inforquery"/><!-- 请选择查询条件! -->
           </td>
          </tr> 
           <logic:iterate id="element" name="dutyInfoForm"  property="selectfieldlist" indexId="index"> 
                  <% 
                    if(flag==0)
                     {
                    	//out.println("<tr class=\"trShallow1\">");
                        out.println("<tr>");
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
                               
                               <html:text name="dutyInfoForm" property='<%="selectfieldlist["+index+"].value"%>' size="13" maxlength="10" styleClass="textColorWrite" title="输入格式：2008.08.08" onclick=""/>
                               <bean:message key="label.query.to"/><html:text name="dutyInfoForm" property='<%="selectfieldlist["+index+"].viewvalue"%>' size="13" maxlength="10" styleClass="TEXT4" title="输入格式：2008.08.08"  onclick=""/>
			                   <!-- 没有什么用，仅给用户与视觉效果-->
			                   <INPUT type="radio" name="${element.itemid}"  checked="true"><bean:message key="label.query.age"/>	
			                   <INPUT type="radio" name="${element.itemid}" id="day"><bean:message key="label.query.day"/>
			                    	
                            </td>                           
                          </logic:equal>
                          <!--备注型 -->                              
                          <logic:equal name="element" property="itemtype" value="M">
                            <td align="left" height='28' nowrap>                
                               <html:text name="dutyInfoForm" property='<%="selectfieldlist["+index+"].value"%>' size="30" maxlength='<%="selectfieldlist["+index+"].itemlength"%>' styleClass="textbox"/>                               
                            </td>                           
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="itemtype" value="A">
                            <td align="left" height='28' nowrap>
                              <logic:notEqual name="element" property="codesetid" value="0">
                                <html:hidden name="dutyInfoForm" property='<%="selectfieldlist["+index+"].value"%>' />                               
                                <html:text name="dutyInfoForm" property='<%="selectfieldlist["+index+"].viewvalue"%>' size="30" maxlength="50" styleClass="textColorWrite" onchange="fieldcode(this,2);"/>
                                  <logic:notEqual name="element" property="codesetid" value="UN">  
                                    <logic:equal name="element" property="itemid" value="e0122"> 
                                           <img src="/images/code.gif" onclick='openInputCodeDialogOrgInputPos("UM","<%="selectfieldlist["+index+"].viewvalue"%>","<%=manager%>",1);' align="absmiddle"/>
                                    </logic:equal>    
                                    <logic:equal name="element" property="codesetid" value="@K"> 
                                           <img src="/images/code.gif" onclick='openInputCodeDialogOrgInputPos("@K","<%="selectfieldlist["+index+"].viewvalue"%>","<%=manager%>",1);' align="absmiddle"/>
                                    </logic:equal>                                      
                                    <logic:notEqual name="element" property="itemid" value="e0122"> 
                                       <logic:notEqual name="element" property="codesetid" value="@K">                                     
                                          <img src="/images/code.gif" onclick='openInputCodeDialog("${element.codesetid}","<%="selectfieldlist["+index+"].viewvalue"%>");' align="absmiddle"/>
                                       </logic:notEqual>                                           
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
                                <html:text name="dutyInfoForm" property='<%="selectfieldlist["+index+"].value"%>' size="30" maxlength="${element.itemlength}" styleClass="textColorWrite"/>                               
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="itemtype" value="N">
                            <td align="left" height='28' nowrap>                
                               <html:text name="dutyInfoForm" property='<%="selectfieldlist["+index+"].value"%>' size="30" maxlength="${element.itemlength}" styleClass="textColorWrite"/>                               
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
                  <tr>
    	             <td align="right" height='20'  nowrap>
    	            <bean:message key="label.query.like"/>&nbsp; 
    	            </td>
    	            <td align="left" colspan="3" height='20' nowrap>
                      <input type="checkbox" name="querlike2" value="true" onclick="selectCheckBox(this,'querylike');">
                    <html:hidden name="dutyInfoForm" property='querylike' styleId="querylike" styleClass="text"/> 
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
         <td align="center" height='30px'>                 
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
		          <logic:iterate id="element"    name="dutyInfoForm"  property="fieldList" indexId="index">
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
                <hrms:paginationdb id="element" name="dutyInfoForm" sql_str="dutyInfoForm.sqlstr" table="" where_str="dutyInfoForm.wherestr" columns="dutyInfoForm.columnstr" order_by="dutyInfoForm.orderby" pagerows="${dutyInfoForm.pagerows}" page_id="pagination"  distinct="" keys="">
             	 <%
                 if(i%2==0)
                  {
                 %>
                  <tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'')">
                   <%}
                  else
                 {%>
                 <tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'')">
                 <%
                  }
                 i++;                           
                   %> 
                   <%
             	   LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
             	   String codeitme=(String)abean.get("e01a1");              	            	   
                   request.setAttribute("codeitme",codeitme);  
                   request.setAttribute("codeitme_encrpt",PubFunc.encrypt(codeitme));      	                           
                   %>    
                   <td align="center" class="RecordRow" nowrap>
                      <input type="hidden" name="orgcodeitemid" value="${codeitme}"> 
                      <hrms:checkmultibox name="dutyInfoForm" property="pagination.select"  value="true" indexes="indexes"/>&nbsp;
                   </td>               
                   <logic:iterate id="fielditem"  name="dutyInfoForm"  property="fieldList" indexId="index">
                        <logic:notEqual name="fielditem" property="codesetid" value="0">
                           <td align="left" class="RecordRow" nowrap>&nbsp;
                                <logic:equal name="fielditem" property="itemid" value="e0122">
                                  <hrms:codetoname codeid="UN" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	                      <bean:write name="codeitem" property="codename" />  
                                  <hrms:codetoname codeid="UM" name="element" codevalue="${fielditem.itemid}" uplevel="${dutyInfoForm.uplevel}"  codeitem="codeitem" scope="page"/>  	      
          	                      <bean:write name="codeitem" property="codename" />&nbsp;  
          	                   </logic:equal>
          	                   <logic:notEqual value="e0122" name="fielditem" property="itemid" >
          	                    <logic:notEqual name="fielditem" property="itemid" value="e01a1">
          	                     <hrms:codetoname codeid="${fielditem.codesetid}" name="element" asOrg="true" codevalue="${fielditem.itemid}" codeitem="codeitem"  uplevel="${dutyInfoForm.uplevel}"  scope="page"/>  	      
          	                      <bean:write name="codeitem" property="codename" />&nbsp;                    
                                </logic:notEqual>  
          	                    <logic:equal name="fielditem" property="itemid" value="e01a1">
          	                     <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem"  scope="page"/>  	      
          	                      <bean:write name="codeitem" property="codename" />&nbsp;                    
                                </logic:equal>
								</logic:notEqual>                                                
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
                                <logic:notEqual name="element" property="${fielditem.itemid}" value="null">
                                 <bean:write name="element" property="${fielditem.itemid}" filter="false"/>
                                </logic:notEqual>
                                 &nbsp;
                              </td>
                            </logic:notEqual>
                        </logic:equal>                          
                     </logic:iterate>
                     <%if(bookflag){ %>
                      <hrms:priv func_id="23060106"> 
                     <td align="left" class="RecordRow" nowrap>
                     <logic:notEqual name="dutyInfoForm" property="cardID" value="-1">
                       		&nbsp;&nbsp;
                          <a href="###" onclick='openwin("/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase=&a0100=${codeitme_encrpt}&closeFlag=window.close&inforkind=4&tabid=${dutyInfoForm.cardID}&multi_cards=-1");'>
               			   <img src="../../images/table.gif" border="0"></a>  
               		</logic:notEqual>
                    <logic:equal name="dutyInfoForm" property="ps_card_attach" value="true">
			                 &nbsp;<hrms:browseaffix pertain_to="ps" a0100="${codeitme}" nbase=""></hrms:browseaffix>  
			         </logic:equal>
		              </td>
		              </hrms:priv>
                      <%} %>
                      <hrms:priv func_id="23110105">
                      <td align="center" class="RecordRow" nowrap>
		     		        <bean:define id="timess" name="element" property="end_date"></bean:define>
                     		 <input type="hidden" name="end_dates" value="${timess}">
		     		        	 &nbsp;&nbsp;
		     		       <a href="javascript:showEmp('${codeitme}','${timess}');"><img src="/images/view.gif" border="0"></a>
		     		        &nbsp;&nbsp;
		              </td> 
		              </hrms:priv>
		                  <td align="center" class="RecordRow" nowrap>
		                    <hrms:priv func_id="23110101"> 
		                    <a href="javascript:onecopy('${codeitme}');">复制</a>
		                    &nbsp;&nbsp;
		     	         	</hrms:priv>
		     	         	<hrms:priv func_id="23110102"> 
		     	         	<a href="javascript:showOrgContext('${codeitme}');editorg();">编辑</a>
		     	         	&nbsp;&nbsp;
		     	         	</hrms:priv>
		     	         	<hrms:priv func_id="23110103"> 
		     	         	<a href="javascript:showOrgContext('${codeitme}');delorg('${codeitme}','<%=i%>')">删除</a>
		     	         	&nbsp;&nbsp;
		     	         	</hrms:priv>
		     	         	<hrms:priv func_id="23110104"> 
		     	         	<a href="javascript:onebolish(<%=i%>);">撤销</a>
		     	         	&nbsp;&nbsp;
		     	         	</hrms:priv>
		     	         	<hrms:priv func_id="23110106">
		     	         	<a href="javascript:showOrgContext('${codeitme}');sacnorg();">浏览</a>
		     	         	</hrms:priv>
		     	         	
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
		      <td class="tdFontcolor">
		            <hrms:paginationtag name="dutyInfoForm"
								pagerows="${dutyInfoForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			  </td>
	               <td  align="right" nowrap class="tdFontcolor">
		           <hrms:paginationdblink name="dutyInfoForm" property="pagination" nameId="dutyInfoForm" scope="page">
				  </hrms:paginationdblink>
			   </td>
		     </tr>
            </table>
          </td>
         </tr>
         <tr height="35">
          <td>
            <hrms:priv func_id="23110100"> 
              <input type="button" name="addbutton"  value="新建岗位" class="mybutton" onclick='newduty();' > 
            </hrms:priv>
            <hrms:priv func_id="23110101"> 
            <input type="button" name="addbutton"  value="复制所选岗位" class="mybutton" onclick='copyduty();' >
            </hrms:priv>
            <hrms:priv func_id="23110104">    
            <input type="button" name="addbutton"  value="撤销所选岗位" class="mybutton" onclick='bolish();' >
            </hrms:priv>
            <hrms:priv func_id="23110111">
              <input type="button" name="addbutton"  value="划转所选岗位" class="mybutton" onclick='transfer();' >
            </hrms:priv> 
            <hrms:priv func_id="23110103"> 
            <input type="button" name="addbutton"  value="删除所选岗位" class="mybutton" onclick='deleterec(this);' > 
            </hrms:priv> 
            <%if(bosflag!=null&&(bosflag.equals("hl") || bosflag.equals("hcm"))) {%>
            	<logic:equal name="dutyInfoForm" property="returnvalue1" value="dxt">	
			     	<input type="button" name="b_delete" value='<bean:message key="button.return"/>' class="mybutton" onclick="clearCheckbox();hrbreturn('org','il_body','dutyInfoForm');"> 
				</logic:equal>
			<%} %>  	
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