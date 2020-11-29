<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@page import="com.hjsj.hrms.actionform.dtgh.party.PartyBusinessForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.frame.dao.RecordVo"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.sys.ConstantParamter"%>
<%--现window在右侧的iframe创建（也就是此页面），需要调用Ext对象，故需要引入template.js  wangbs 2019年3月6日15:32:40--%>
<script language="javascript" src="/module/utils/js/template.js"></script>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager=userView.getManagePrivCodeValue();
	int i=0;
	PartyBusinessForm partyBusinessForm=(PartyBusinessForm)session.getAttribute("partyBusinessForm");
	String param=partyBusinessForm.getParam();
	RecordVo constantuser_vo = ConstantParamter.getRealConstantVo("PS_C_CODE");
	String codesetid ="";
	if(constantuser_vo!=null)
		codesetid = constantuser_vo.getString("str_value");
	
	String bosflag = "";
	if (null != userView) {
		bosflag = userView.getBosflag();
	    bosflag = bosflag != null ? bosflag : "";
	}
%>
<script type="text/javascript">
<!--
function showOrClose()
{
		var obj=eval("aa");
	    var obj3=eval("vieworhidd");
		//var obj2=eval("document.partyBusinessForm.isShowCondition");
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
      var vo=document.getElementsByName(hiddname)[0];
      if(vo)
         vo.value="1";
   }else
   {
         var vo=document.getElementsByName(hiddname)[0];
      if(vo)
         vo.value="0";
   }

}
function search(query)
{
    partyBusinessForm.action="/dtgh/party/searchpartybusinesslist.do?b_query=link&query="+query;
    partyBusinessForm.submit();
}
function initquerylikecheck(querylike){
	var vo=document.getElementById("querylike2");
	if(querylike==1){
		vo.checked=true;
	}else{
		vo.checked=false;
	}
}

function newparty(){
	partyBusinessForm.action="/dtgh/party/searchpartybusinesslist.do?b_add=link&type=add";
    partyBusinessForm.submit();
}
function editorg(codeitemid){
	partyBusinessForm.action="/dtgh/party/searchpartybusinesslist.do?b_add=link&codeitemid="+codeitemid+"&type=edit";
    partyBusinessForm.submit();
}
function deleterec()
{
       var len=document.partyBusinessForm.elements.length;
       var uu;
       for (var i=0;i<len;i++)
       {
           if(document.partyBusinessForm.elements[i].type=='checkbox'&&document.partyBusinessForm.elements[i].name!="selbox")
	       {	
		      if(document.partyBusinessForm.elements[i].name!="orglike2"&&document.partyBusinessForm.elements[i].name!="querylike2")
		      {	
                if(document.partyBusinessForm.elements[i].checked==true)
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
       var param='${partyBusinessForm.param}';
       if(param=='Y'){
       		param='党务'
       }else if(param=='V'){
       		param='团务';
       }else if(param=='W'){
       		param='工会'
       } else if(param=='H'){
       		param='基准岗位'
       }     
      if(confirm("你确定删除吗?注意:同时将删除对应"+param+"库中的数据!"))
      {
	        if(selectcheckeditem()!=null){
	           var hashvo=new ParameterSet();          
	           hashvo.setValue("partycodeitemid", selectcheckeditem());
	           var request=new Request({method:'post',onSuccess:deleteorg,functionId:'3409000006'},hashvo);
	        }
      }
 }
 
  function selectcheckeditem()
   {
      	var a=0;
	var b=0;
	var selectid=new Array();
	var a_IDs=eval("document.partyBusinessForm.partycodeitemid");	
	var nums=0;		
	for(var i=0;i<document.partyBusinessForm.elements.length;i++)
	{			
	   if(document.partyBusinessForm.elements[i].type=='checkbox'&&document.partyBusinessForm.elements[i].name!="selbox")
	   {	
		  if(document.partyBusinessForm.elements[i].name!="orglike2"&&document.partyBusinessForm.elements[i].name!="querylike2")
		  {		   			
		    nums++;
		  }
	   }
    }
	if(nums>1)
	{
	    for(var i=0;i<document.partyBusinessForm.elements.length;i++)
	    {			
		   if(document.partyBusinessForm.elements[i].type=='checkbox'&&document.partyBusinessForm.elements[i].name!="selbox")
		   {	
		     if(document.partyBusinessForm.elements[i].name!="orglike2"&&document.partyBusinessForm.elements[i].name!="querylike2")
		     {
		       if(document.partyBusinessForm.elements[i].checked==true)
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
	   for(var i=0;i<document.partyBusinessForm.elements.length;i++)
	   {			
	      if(document.partyBusinessForm.elements[i].type=='checkbox'&&document.partyBusinessForm.elements[i].name!="selbox")
	      {	
	         if(document.partyBusinessForm.elements[i].name!="orglike2"&&document.partyBusinessForm.elements[i].name!="querylike2")
		     {
		        if(document.partyBusinessForm.elements[i].checked==true)
		        {
			       selectid[a++]=a_IDs.value;						
		        }
		     }
	      }
	   }
	}	
	return selectid;	
 }
 
  function deleteorg(outparamters)
 {
 	document.getElementsByName("codeitemid")[0].value="";
     var checkperson=outparamters.getValue("checkperson"); 
     //var partyitemstr=outparamters.getValue("partyitemstr");
     if(checkperson=="true")
     {
     execScript("r=msgbox('是否清空下属人员的机构信息?',3,'提示')","vbscript"); //返回值必须是全局变量  
       if(r==6)
        {
          partyBusinessForm.action="/workbench/orginfo/searchorginfodata.do?b_delete=del&delpersonorg=t&type=org";
          partyBusinessForm.submit();        
        }
        else if(r==7)
        {
          var target_url="/org/orginfo/searchorglist.do?b_choose=link&orgitem="+orgitem+"&code=";
          var return_vo= window.showModalDialog(target_url,0, 
          "dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;");
          if(return_vo!=null)
          {
             var orgid=return_vo.orgid;             
             partyBusinessForm.action="/workbench/orginfo/searchorginfodata.do?b_delete=del&delpersonorg=f&type=org&orgid="+orgid;
             partyBusinessForm.submit(); 
          }
       
        }
        else
        {
        return false;
        }     
     }
     else
     {
        partyBusinessForm.action="/dtgh/party/searchpartybusinesslist.do?b_delete=del&delpersonorg=f";
        partyBusinessForm.submit(); 
     }  
 }
 function deleter(isrefresh,code)
   {
   		if(isrefresh=='delete')
   		{
   			var currnode;
   			if(parent.frames['mil_menu'])
				currnode=parent.frames['mil_menu'].Global.selectedItem;
			else
				currnode=parent.parent.frames['mil_menu'].Global.selectedItem;
			if(currnode==null)
					return;
			if(currnode.load)
			for(var i=0;i<=currnode.childNodes.length-1;i++){
				if(code.toUpperCase()==currnode.childNodes[i].uid.toUpperCase())
					currnode.childNodes[i].remove();
			}
		}
	}
function delorg(codeitemid,rownum)
{
	var param='${partyBusinessForm.param}';
	var a_code='${partyBusinessForm.a_code}';
       if(param=='Y'){
       		param='党务'
       		
       }else if(param=='V'){
       		param='团务';
       }else if(param=='W'){
       		param='工会'
       }else if(param=='H'){
       		param='基准岗位'
       }
   if(confirm("你确定删除吗?注意:同时将删除"+param+"库相对应的数据!"))
   {
       var hashvo=new ParameterSet();     
       var selectid=new Array();   
       selectid[0]=codeitemid;  
       hashvo.setValue("partycodeitemid", selectid);
       hashvo.setValue("rownum",rownum);
       hashvo.setValue("a_code",a_code);
       var request=new Request({method:'post',onSuccess:detRowTran,functionId:'3409000006'},hashvo);
      
   }
}

function detRowTran(outparamters)
{
     var checkperson=outparamters.getValue("checkperson"); 
     //var orgitem=outparamters.getValue("orgitem");
     var rownum=outparamters.getValue("rownum");
     var orgcodeitemid=outparamters.getValue("partycodeitemid");
     if(orgcodeitemid.length<=0)
        return false;
     var codeitemid=orgcodeitemid[0];
     var hashvo=new ParameterSet();  
     hashvo.setValue("rownum",rownum);
     hashvo.setValue("codeitemid",codeitemid);     
     if(checkperson=="true")
     {
       execScript("r=msgbox('是否清空下属人员的机构信息?',3,'提示')","vbscript"); //返回值必须是全局变量  
       if(r==6)//点击是
        {
          hashvo.setValue("delpersonorg","t");
          hashvo.setValue("orgid","");
          var request=new Request({method:'post',onSuccess:detRow,functionId:'3409000007'},hashvo);
        }
        else if(r==7)
        {
          var target_url="/org/orginfo/searchorglist.do?b_choose=link&orgitem="+orgitem+"&code=";
          var return_vo= window.showModalDialog(target_url,0, 
          "dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;");
          if(return_vo!=null)
          {
             var orgid=return_vo.orgid;             
             hashvo.setValue("delpersonorg","f");
             hashvo.setValue("orgid",orgid);
            // alert( hashvo.getValue("codeitemid"));
             var request=new Request({method:'post',onSuccess:detRow,functionId:'3409000007'},hashvo);
          }
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
        var request=new Request({method:'post',onSuccess:detRow,functionId:'3409000007'},hashvo);
     }  
}
function detRow(outparamters)
{
   var flag=outparamters.getValue("flag");
   var rownum=outparamters.getValue("rownum");
   var uid=outparamters.getValue("uid");
   if(flag=="ok")
   {
     alert("删除成功！");
     var table=document.getElementById("pag");
     if(table==null)
  	   return false;  	
     var td_num=table.rows.length;    
     var i=parseInt(rownum,10);
     table.deleteRow(i); 
     //alert(codesetid+code);
      deleter('delete',uid);
   }else
     alert("删除失败！");
}
function onebolish(codeitemid){
	var param='${partyBusinessForm.param}';
	var codesetid="64";
       if(param=='Y'){
       		codesetid="64";
       }else if(param=='V'){
       		codesetid="64";
       }else if(param=='W'){
       		codesetid="64";
       }else if(param=='H'){
       		codesetid="<%=codesetid %>";
       }
	 var hashvo=new ParameterSet();  
	 var array=new Array();
	 array[0]=codeitemid;
     hashvo.setValue("codeitemidlist",array); 
     hashvo.setValue("codesetid",codesetid); 
     var request=new Request({method:'post',onSuccess:initbolish,functionId:'3409000008'},hashvo);
}	
function initbolish(outparamters){
	var msg=outparamters.getValue("msg");
	if(msg=='yes'){
	 	var target_url="/dtgh/party/searchpartybusinesslist.do?br_bolish=link";
        Ext.create("Ext.window.Window",{
            id:"undoBasePosWin",
            title:'撤销基准岗位',
            width:470,
            height:320,
            resizable:false,
            modal:true,
            autoScroll:false,
            autoShow:true,
            autoDestroy:true,
            renderTo:Ext.getBody(),
            html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+target_url+"'></iframe>",
            listeners:{
                close:function(){
                    if(this.return_vo){
                        var codeitemidlist=outparamters.getValue("codeitemidlist");
                        var codeitemid="";
                        for(var i=0;i<codeitemidlist.length;i++){
                            codeitemid+=codeitemidlist[i]+"`";
                        }
                        codeitemid=codeitemid.substring(0,codeitemid.length-1);
                        partyBusinessForm.action="/dtgh/party/searchpartybusinesslist.do?b_bolish=link&codeitemid="+codeitemid+"&end_date="+this.return_vo;
                        partyBusinessForm.submit();
                    }
                }
            }
        });
	}else if('sameday'==msg){
		var param='${partyBusinessForm.param}';
	       if(param=='Y'){
	       		param='党'
	       }else if(param=='V'){
	       		param='团';
	       }else if(param=='W'){
	       		param='工会'
	       }
	       if(param=='H'){
	       		alert("不能撤销当天新建的基准岗位！");
	       }else{
				alert("不能撤销当天新建的"+param+"组织机构！");
			}
	}else if('haveperson'==msg){
		var param='${partyBusinessForm.param}';
	       if(param=='Y'){
	       		param='党'
	       }else if(param=='V'){
	       		param='团';
	       }else if(param=='W'){
	       		param='工会'
	       }
		   alert("不能撤销"+param+"组织内有人员的"+param+"组织机构！");
	}else if('havechild'==msg){
		var param='${partyBusinessForm.param}';
	       if(param=='Y'){
	       		param='党'
	       }else if(param=='V'){
	       		param='团';
	       }else if(param=='W'){
	       		param='工会'
	       }
		alert("不能撤销有下级"+param+"组织的"+param+"组织机构！");
	}
}

function bolish(){
	var param='${partyBusinessForm.param}';
	var codesetid="64";
       if(param=='Y'){
       		codesetid="64";
       }else if(param=='V'){
       		codesetid="64";
       }else if(param=='W'){
       		codesetid="64";
       }else if(param=='H'){
       		codesetid="<%=codesetid %>";
       }
	 var hashvo=new ParameterSet();
	 var selectid=selectcheckeditem();
	if(selectid==null||selectid.length==0)
	{
		alert(REPORT_INFO9+"!");
		return false;
	}
     hashvo.setValue("codeitemidlist",selectid); 
     hashvo.setValue("codesetid",codesetid); 
     var request=new Request({method:'post',onSuccess:initbolish,functionId:'3409000008'},hashvo);
}

function showEmp(codeitemid)
{
	var backdate='${partyBusinessForm.backdate }';
	var crrdate=formatDate(new Date(),'yyyy-MM-dd');
	var hashvo=new ParameterSet();
	hashvo.setValue("ps_c_job","${partyBusinessForm.ps_c_job }"); 
    hashvo.setValue("codeitemid",codeitemid); 
    var request=new Request({method:'post',onSuccess:getE01a1s,functionId:'3409000023'},hashvo);
	function getE01a1s(outparamters){
		var msg=outparamters.getValue("msg");
		if(msg.length>0){
			if(backdate==null||backdate.length==0||crrdate==backdate){ 
				$('codeid').value=msg;
		    	partyBusinessForm.action="/workbench/browse/scaninfodata.do?b_init=link&orgflag=2&returnvalue=scanstandardduty&scantype=scanduty&return_codeid=${partyBusinessForm.a_code}&kind=H";
		    }else{
		    	partyBusinessForm.action="/workbench/browse/history/showinfo.do?b_orgsearch=link&code="+msg+"&orgflag=2&returnvalue=scan&scantype=scanduty&return_codeid=${partyBusinessForm.a_code}&orgbackdate="+backdate;
		    }
		    clearCheckbox();
		    turn();
		    partyBusinessForm.submit();
	    }else{
	    	alert("无岗位关联此基准岗位!");
	    }
    }
}
function turn()
{
   parent.menupnl.toggleCollapse(false);
}   
function  clearCheckbox()
{
   var len=document.partyBusinessForm.elements.length;
       var i;
     
        for (i=0;i<len;i++)
        {
         if (document.partyBusinessForm.elements[i].type=="checkbox"&&document.partyBusinessForm.elements[i].name!="orglike2"&&document.partyBusinessForm.elements[i].name!="querlike2")
          {
             
            document.partyBusinessForm.elements[i].checked=false;
          }
        }
}

function showjobinfo(codeid,jobname){
	encodeURI();
}

    function openwin(url)
	{
	   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+
			   screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
	}

//-->
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
 <hrms:themes></hrms:themes>
<html:form action="/dtgh/party/searchpartybusinesslist">
	<html:hidden name="partyBusinessForm" property="codeitemid"/>
	<input type=hidden name=code id=codeid />
  <table width='97%' border=0 style="margin-top: -4px;" cellpadding="0" cellspacing="0">
    <tr style="margin-bottom:-3px;">
      <td height="25px">
          <table>
             <tr>
                <td >
                	<logic:notEmpty name="partyBusinessForm" property="codemess">
	                <logic:notEqual name="partyBusinessForm" property="param" value="H">
	                    	当前组织单元：
	                </logic:notEqual>
	                <logic:equal name="partyBusinessForm" property="param" value="H">
	                  		岗位类别：
	                 </logic:equal>
                    <bean:write name="partyBusinessForm" property="codemess"/>
                    &nbsp;&nbsp;&nbsp;
                    </logic:notEmpty>
                    &nbsp;
                </td>
                <td>
                <logic:notEmpty name="partyBusinessForm"  property="selectfieldlist">
                  <table  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
                    <tr>
                       <td>
［&nbsp;
                       </td>
                       <td id="vieworhidd"> 
                       
                            <a href="javascript:showOrClose();"> 
                         <logic:equal name="partyBusinessForm" property="isShowCondition" value="none" >查询显示</logic:equal>   
                         <logic:equal name="partyBusinessForm" property="isShowCondition" value="block" >查询隐藏</logic:equal>   
                            </a>
                       </td>                       
                       <td>&nbsp;］&nbsp;&nbsp;&nbsp;&nbsp;
                       </td>
                    </tr>
                  </table>
                  </logic:notEmpty>
                </td>
                <td>
                 <logic:equal name="partyBusinessForm" property="partylike" value="1">
                     <input type="checkbox" name="orglike2" value="true" onclick="selectCheckBox(this,'partylike');search(0);" checked>
                 </logic:equal>
                 <logic:notEqual name="partyBusinessForm" property="partylike" value="1">
                     <input type="checkbox" name="orglike2" value="true" onclick="selectCheckBox(this,'partylike');search(0);">
                 </logic:notEqual>
                 
               <html:hidden name="partyBusinessForm" property='partylike' styleClass="text"/>                 
                 <logic:notEqual name="partyBusinessForm" property="param" value="H">
                     	显示当前组织单元下所有组织单元
                 </logic:notEqual>
                 <logic:equal name="partyBusinessForm" property="param" value="H">
                  		显示当前分类下所有岗位
                 </logic:equal>
                </td>
             </tr>
          </table>
      </td>
    </tr>
<%
	int flag=0;
%>
   <tr>     
     <td>
        <table  width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id='aa'  style='display:${partyBusinessForm.isShowCondition}'>
     <tr>
      <td>   
         <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow">
          <tr class="trShallow1">
           <td align="center" colspan="4" height='20' class="RecordRow" nowrap>
            <bean:message key="label.query.inforquery"/><!-- 请选择查询条件! -->
           </td>
          </tr> 
                  <logic:iterate id="element" name="partyBusinessForm"  property="selectfieldlist" indexId="index"> 
                      <% 
                    if(flag==0)
                     {
                        out.println("<tr>");
                         flag=1;          
                      }else{
                           flag=0;       
                      }
                    %>            
                          <td align="right" class="tdFontcolor" nowrap>                
                            <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;&nbsp;&nbsp;&nbsp;
                          </td>
                          <!--日期型 -->                            
                          <logic:equal name="element" property="itemtype" value="D">
                            <td align="left" height='28' nowrap>    
                               
                               <html:text name="partyBusinessForm" property='<%="selectfieldlist["+index+"].value"%>' size="13" maxlength="10" styleClass="textColorWrite" title="输入格式：2008.08.08" onclick=""/>
                               <bean:message key="label.query.to"/><html:text name="partyBusinessForm" property='<%="selectfieldlist["+index+"].viewvalue"%>' size="13" maxlength="10" styleClass="textbox" title="输入格式：2008.08.08"  onclick=""/>
			                   <!-- 没有什么用，仅给用户与视觉效果-->
			                   <INPUT type="radio" name="${element.itemid}"  checked="true"><bean:message key="label.query.age"/>	
			                   <INPUT type="radio" name="${element.itemid}" id="day"><bean:message key="label.query.day"/>
			                    	
                            </td>                           
                          </logic:equal>
                          <!--备注型 -->                              
                          <logic:equal name="element" property="itemtype" value="M">
                            <td align="left" height='28' nowrap>                
                               <html:text name="partyBusinessForm" property='<%="selectfieldlist["+index+"].value"%>' size="30" maxlength='<%="selectfieldlist["+index+"].itemlength"%>' styleClass="textbox"/>                               
                            </td>                           
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="itemtype" value="A">
                            <td align="left" height='28' nowrap>
                              <logic:notEqual name="element" property="codesetid" value="0">
                                <html:hidden name="partyBusinessForm" property='<%="selectfieldlist["+index+"].value"%>' styleClass="text"/>                               
                                <html:text name="partyBusinessForm" property='<%="selectfieldlist["+index+"].viewvalue"%>' size="30" maxlength="50" styleClass="textColorWrite" onchange="fieldcode(this,2);"/>
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
                                <html:text name="partyBusinessForm" property='<%="selectfieldlist["+index+"].value"%>' size="30" maxlength="${element.itemlength}" styleClass="textColorWrite"/>                               
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="itemtype" value="N">
                            <td align="left" height='28' nowrap>                
                               <html:text name="partyBusinessForm" property='<%="selectfieldlist["+index+"].value"%>' size="30" maxlength="${element.itemlength}" styleClass="textColorWrite"/>                               
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
    	            <input type="checkbox" name="querylike2" value="true" onclick="selectCheckBox(this,'querylike');">
                     <html:hidden name="partyBusinessForm" property='querylike' styleClass="text"/>
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
              <Input type='button' value="<bean:message key="infor.menu.query"/>" onclick='search(1);' class='mybutton' style="margin-bottom:5px;"/>  
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
   	             <td align="center" class="TableRow" width="25" nowrap>
		            <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
                 </td>
		          <logic:iterate id="element"    name="partyBusinessForm"  property="fieldList" indexId="index">
                   <td align="center" class="TableRow" nowrap>
                 &nbsp;&nbsp;&nbsp; <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;&nbsp;&nbsp;
	       	       </td>            
                 </logic:iterate>
                 <logic:equal value="H" name="partyBusinessForm" property="param">
                 <hrms:priv func_id="2501207">
                <!-- <logic:equal name="partyBusinessForm" property="ps_c_card_attach" value="true"> 
                      <td align="center" class="TableRow" nowrap width=70>
		     		        &nbsp;说明书&nbsp;
		              </td>
		         </logic:equal>-->  
		         <td align="center" class="TableRow" nowrap width=70>
		     		        &nbsp;说明书&nbsp;
		              </td>
		         </hrms:priv>
		          <logic:notEmpty property="ps_c_job"  name="partyBusinessForm">
			          <td align="center" class="TableRow" nowrap width=70>
			     		人员浏览
			          </td>
		          </logic:notEmpty>
		          </logic:equal> 
		          <td align="center" class="TableRow" nowrap  <logic:equal value="H" name="partyBusinessForm" property="param">width="200"</logic:equal>  <logic:notEqual value="H" name="partyBusinessForm" property="param">width="150"</logic:notEqual> >
		     		&nbsp;操作&nbsp; 
		          </td> 		         
               </tr>
               <hrms:paginationdb id="element" name="partyBusinessForm" sql_str="partyBusinessForm.sqlstr" table="" where_str="partyBusinessForm.wherestr" columns="partyBusinessForm.columnstr" order_by="partyBusinessForm.orderby" page_id="pagination" pagerows="${partyBusinessForm.pagerows}" distinct="" keys="">
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
             	   String codeitme=(String)abean.get("codeitemid");              	            	   
                   request.setAttribute("codeitme",codeitme);  
                   request.setAttribute("codeitme_encrypt",PubFunc.encrypt(codeitme));     	                           
                   %>
                   <td align="center" class="RecordRow" width="25" nowrap>
                      <input type="hidden" name="partycodeitemid" value="${codeitme}"> 
                      <hrms:checkmultibox name="partyBusinessForm" property="pagination.select"  value="true" indexes="indexes"/>&nbsp;
                   </td>                   
                   <logic:iterate id="fielditem"  name="partyBusinessForm"  property="fieldList" indexId="index">
                        <logic:notEqual name="fielditem" property="codesetid" value="0">
                           <td align="left" class="RecordRow" nowrap>&nbsp;
                                   <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	                      <bean:write name="codeitem" property="codename" />&nbsp;                                
                            </td>
                        </logic:notEqual>
                        <logic:equal name="fielditem" property="codesetid" value="0">
                            <logic:equal name="fielditem" property="itemtype" value="N">
                              <td align="right" class="RecordRow" nowrap>&nbsp;
                                 <bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;&nbsp;
                              </td>
                            </logic:equal>
                            <logic:notEqual name="fielditem" property="itemtype" value="N">
                              <td align="left" class="RecordRow" nowrap>&nbsp;
                                 <bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;&nbsp;
                              </td>
                            </logic:notEqual>
                        </logic:equal>                          
                     </logic:iterate>
						<logic:equal value="H" name="partyBusinessForm" property="param">
						  <hrms:priv func_id="2501207">
						<!--   <logic:equal name="partyBusinessForm" property="ps_c_card_attach" value="true">
			                  <td align="left" class="RecordRow" nowrap >
			                     &nbsp;&nbsp;
			                     <logic:notEmpty name="partyBusinessForm" property="zp_job_template">
			                     <a href="###" onclick='openwin("/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase=&a0100=${codeitme}&inforkind=6&tabid=${partyBusinessForm.zp_job_template }&multi_cards=-1");'>
			                     <img alt="基准岗位说明书" src="/images/table.gif" border="0">
			                     </a>
			                     &nbsp;&nbsp;
			                     </logic:notEmpty>
					     		  <hrms:browseaffix pertain_to="job" a0100="${codeitme}" nbase=""></hrms:browseaffix>  
					          </td>
				        </logic:equal>-->
				            <td align="left" class="RecordRow" nowrap >
			                     &nbsp;&nbsp;
			                     <logic:notEmpty name="partyBusinessForm" property="zp_job_template">
			                     <a href="###" onclick='openwin("/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase=&a0100=${codeitme_encrypt}&inforkind=6&tabid=${partyBusinessForm.zp_job_template }&multi_cards=-1");'>
			                     <img alt="基准岗位说明书" src="/images/table.gif" border="0">
			                     </a>
			                     &nbsp;&nbsp;
			                     </logic:notEmpty><!-- zhangcq 基准岗位说明书显示隐藏附件 -->
			                     <logic:equal name="partyBusinessForm" property="ps_c_card_attach" value="true">
					     		  <hrms:browseaffix pertain_to="job" a0100="${codeitme}" nbase=""></hrms:browseaffix>  
					     		  </logic:equal>
					          </td>
				          </hrms:priv>
				          <logic:notEmpty property="ps_c_job"  name="partyBusinessForm">
				          <td align="center" class="RecordRow" nowrap >
				     		&nbsp;<a href="javascript:showEmp('${codeitme}');"><img src="/images/view.gif" border="0"></a>&nbsp;
				          </td>
				          </logic:notEmpty>
				          </logic:equal> 
		                  <td align="center" class="RecordRow" <logic:equal value="H" name="partyBusinessForm" property="param">width="200"</logic:equal>  <logic:notEqual value="H" name="partyBusinessForm" property="param">width="150"</logic:notEqual> nowrap>
		     	         	<logic:equal value="Y" name="partyBusinessForm" property="param">
			     	         	<hrms:priv func_id="3501103">
				     	         	<a href="javascript:editorg('${codeitme}');">编辑</a>
				     	         	&nbsp;&nbsp;
			     	         	</hrms:priv>
			     	         	<hrms:priv func_id="3501105">
				     	         	<a href="javascript:delorg('${codeitme}','<%=i%>')">删除</a>
				     	         	&nbsp;&nbsp;
			     	         	</hrms:priv>
			     	         	<hrms:priv func_id="3501104">
			     	         		<a href="javascript:onebolish('${codeitme}');">撤销</a>
			     	         	</hrms:priv>
		     	         	</logic:equal>
		     	         	<logic:equal value="V" name="partyBusinessForm" property="param">
			     	         	<hrms:priv func_id="3502103">
				     	         	<a href="javascript:editorg('${codeitme}');">编辑</a>
				     	         	&nbsp;&nbsp;
			     	         	</hrms:priv>
			     	         	<hrms:priv func_id="3502105">
				     	         	<a href="javascript:delorg('${codeitme}','<%=i%>')">删除</a>
				     	         	&nbsp;&nbsp;
			     	         	</hrms:priv>
			     	         	<hrms:priv func_id="3502104">
			     	         		<a href="javascript:onebolish('${codeitme}');">撤销</a>
			     	         	</hrms:priv>
		     	         	</logic:equal>
		     	         	<logic:equal value="H" name="partyBusinessForm" property="param">
			     	         	<hrms:priv func_id="2501204">
				     	         	<a href="javascript:editorg('${codeitme}');">编辑</a>
				     	         	&nbsp;&nbsp;
			     	         	</hrms:priv>
			     	         	<hrms:priv func_id="2501202">
				     	         	<a href="javascript:delorg('${codeitme}','<%=i%>')">删除</a>
				     	         	&nbsp;&nbsp;
			     	         	</hrms:priv>
			     	         	<hrms:priv func_id="2501203">
			     	         		<a href="javascript:onebolish('${codeitme}');">撤销</a>
			     	         		&nbsp;&nbsp;
			     	         	</hrms:priv>
			     	         	<a href="/dtgh/party/searchpartybusinesslist.do?b_showinfo=link&codeitemid=${codeitme}">浏览</a>
		     	         	</logic:equal>
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
		            <hrms:paginationtag name="partyBusinessForm"
								pagerows="${partyBusinessForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
			  </td>
	               <td  align="right" nowrap class="tdFontcolor">
		           <p align="right"><hrms:paginationdblink name="partyBusinessForm" property="pagination" nameId="partyBusinessForm" scope="page">
				  </hrms:paginationdblink></p>
			   </td>
		     </tr>
            </table>
          </td>
         </tr>
         <tr height="35">
          <td nowrap="nowrap">
          <logic:equal value="Y" name="partyBusinessForm" property="param">
          	<hrms:priv func_id="3501102">
            	<input type="button" name="addbutton"  value="新建党组织机构" class="mybutton" onclick='newparty();' > 
            </hrms:priv>
            <hrms:priv func_id="3501104">
            	<input type="button" name="addbutton"  value="撤销所选党组织机构" class="mybutton" onclick='bolish();' >
            </hrms:priv>
            <hrms:priv func_id="3501105">
            	<input type="button" name="addbutton"  value="删除所选党组织机构" class="mybutton" onclick='deleterec();' >  	
          	</hrms:priv>
          </logic:equal>
          <logic:equal value="V" name="partyBusinessForm" property="param">
          	<hrms:priv func_id="3502102">
            	<input type="button" name="addbutton"  value="新建团组织机构" class="mybutton" onclick='newparty();' > 
            </hrms:priv>
            <hrms:priv func_id="3502104">
            	<input type="button" name="addbutton"  value="撤销所选团组织机构" class="mybutton" onclick='bolish();' >
            </hrms:priv>
            <hrms:priv func_id="3502105">
            	<input type="button" name="addbutton"  value="删除所选团组织机构" class="mybutton" onclick='deleterec();' >  	
          	</hrms:priv>
          </logic:equal>
          <logic:equal value="W" name="partyBusinessForm" property="param">
            <input type="button" name="addbutton"  value="新建工会组织机构" class="mybutton" onclick='newparty();' > 
            <input type="button" name="addbutton"  value="撤销所选工会组织机构" class="mybutton" onclick='bolish();' >
            <input type="button" name="addbutton"  value="删除所选工会组织机构" class="mybutton" onclick='deleterec();' >  	
          </logic:equal>
          <logic:equal value="H" name="partyBusinessForm" property="param">
	          <hrms:priv func_id="2501201">
	            <input type="button" name="addbutton"  value="新建基准岗位" class="mybutton" onclick='newparty();' > 
	          </hrms:priv>
	          <hrms:priv func_id="2501202">
	            <input type="button" name="addbutton"  value="撤销所选基准岗位" class="mybutton" onclick='bolish();' >
	          </hrms:priv>
	          <hrms:priv func_id="2501203">
	            <input type="button" name="addbutton"  value="删除所选基准岗位" class="mybutton" onclick='deleterec();' > 
	          </hrms:priv> 	
          </logic:equal>
          <logic:equal value="dxt" name="partyBusinessForm" property="returnvalue">
                <%
                   if (bosflag.equals("hcm"))
                   {
                %>
                <input type='button' class="mybutton" name="returnButton"
                    onclick='hrbreturn("dtgh", "il_body", "partyBusinessForm");'
                    value='<bean:message key='reportcheck.return'/>' />
                <%
                   }
                %>
           </logic:equal>
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
<script>
    if(document.getElementById('querylike')!=undefined) {
        initquerylikecheck(document.getElementById('querylike').value);
    }
var value_s="";
<%
	PartyBusinessForm oif = (PartyBusinessForm)request.getSession().getAttribute("partyBusinessForm");
	ArrayList codelist =oif.getCodeitemidlist();
	if(codelist!=null&&codelist.size()>0){
			for(int y=0;y<codelist.size();y++){
		%>
				value_s="<%=codelist.get(y)%>";
				//alert(value_s);
	  		deleter('<bean:write name="partyBusinessForm" property="isrefresh" filter="true"/>',value_s);
<%
		}
		
		if(codelist!=null)
			codelist.clear();
			}
	%>  	
</script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script>
var aa = document.getElementById('aa');
if(aa.style.display == 'block')
	aa.style.display = '';
</script>

