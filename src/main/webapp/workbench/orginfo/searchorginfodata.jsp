<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@page import="com.hjsj.hrms.actionform.org.OrgInfoForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean,java.text.SimpleDateFormat"%>
<hrms:themes></hrms:themes>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="../../../components/extWidget/proxy/TransactionProxy.js"></script>
<script language="JavaScript" src="../../../components/codeSelector/codeSelector.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css">

<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	/**cmq changed at 20121001 for 权限控制优先级 业务范围-操作单位-管理范围*/
	//String manager=userView.getManagePrivCodeValue();
	String manager=userView.getUnitIdByBusi("4");
	String bosflag =userView.getBosflag();
	//end.
	int i=0;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	session.removeAttribute("code");
	String themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());
%>
<%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
  
  <%} %> 
<script language="javascript">

var  divHeight = window.screen.availHeight - window.screenTop -120;
var code="";
var kind="";
var orgtype="";
var parentid="";
var codesetid="";	
var selectid = new Array();
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
	   waitInfo.style.display='';
	   
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
		//var obj2=eval("document.orgInfoForm.isShowCondition");
	    if(obj.style.display=='none')
	    {
    		obj.style.display='';
        	obj3.innerHTML="<a href=\"javascript:showOrClose();\" > 查询隐藏 </a>";
    	}
    	else
	    {
	    	obj.style.display='none';
	    	obj3.innerHTML="<a href=\"javascript:showOrClose();\" > 查询显示 </a>";
	    	
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
function query(query)
{
   orgInfoForm.action="/workbench/orginfo/searchorginfodata.do?b_query=link&query="+query;
   orgInfoForm.submit();
}
function search()
{
    orgInfoForm.action="/workbench/orginfo/searchorginfodata.do?b_search=link";
    orgInfoForm.submit();
}
function showEmp(codeitemid,end_dates)
{
	//var end_dates = document.getElementById("end_dates").value;
	//end_dates = formatDate(end_dates,'yyyy-MM-dd');
	var backdate='${orgInfoForm.backdate }';
	var crrdate=formatDate(new Date(),'yyyy-MM-dd');
	
	//if(backdate==null||backdate.length==0||crrdate==backdate){
	if(crrdate <end_dates){
    	orgInfoForm.action="/workbench/browse/scaninfodata.do?b_init=link&code="+codeitemid+"&orgflag=2&returnvalue=scan&scantype=scan&return_codeid=${orgInfoForm.code}&userbase=";
    }else{
    	orgInfoForm.action="/workbench/browse/history/showinfo.do?b_orgsearch=link&code="+codeitemid+"&orgflag=2&returnvalue=scan&scantype=scan&return_codeid=${orgInfoForm.code}&orgbackdate="+backdate;
   }
    clearCheckbox();
    turn();
    orgInfoForm.submit();
    
}
function showEmp1(codeitemid){
	var backdate='${orgInfoForm.backdate }';
	var crrdate=formatDate(new Date(),'yyyy-MM-dd');
	orgInfoForm.action="/workbench/browse/history/showinfo.do?b_orgsearch=link&code="+codeitemid+"&orgflag=2&returnvalue=scan&scantype=scan&return_codeid=${orgInfoForm.code}&orgbackdate="+backdate;
	clearCheckbox();
    turn();
    orgInfoForm.submit();
}
function showLeader(codeitemid)
{
	var backdate='${orgInfoForm.backdate }';
	var crrdate=formatDate(new Date(),'yyyy-MM-dd');
	if(backdate==null||backdate.length==0||crrdate==backdate){
    	orgInfoForm.action="/workbench/browse/scaninfodata.do?b_init=link&code="+codeitemid+"&orgflag=2&returnvalue=scan&scantype=scan&return_codeid=${orgInfoForm.code}";
    }else{
    	orgInfoForm.action="/workbench/browse/history/showinfo.do?b_orgsearch=link&code="+codeitemid+"&orgflag=2&returnvalue=scan&scantype=scan&return_codeid=${orgInfoForm.code}&orgbackdate="+backdate;
    }
    clearCheckbox();
    turn();
    orgInfoForm.submit();
}
function turn()
{
   parent.menupnl.toggleCollapse(false);
}   
function  clearCheckbox()
{
   var len=document.orgInfoForm.elements.length;
       var i;
     
        for (i=0;i<len;i++)
        {
         if (document.orgInfoForm.elements[i].type=="checkbox")
          {
             
            document.orgInfoForm.elements[i].checked=false;
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
	codesetid=outparamters.getValue("codesetid");
}
function editorg()
{
   orgInfoForm.action="/workbench/orginfo/editorginfodata.do?b_search=link&code="+code+"&kind="+kind+"&orgtype="+orgtype+"&parentid="+parentid+"&returnvalue=scan&scantype=scan&return_codeid=${orgInfoForm.code}&edittype=update&isself=0";
   orgInfoForm.submit();
}
function sacnorg()
{
	for(var i=0;i<document.orgInfoForm.elements.length;i++)
	{			
	   if(document.orgInfoForm.elements[i].type=='checkbox'&&document.orgInfoForm.elements[i].name!="selbox")
	   {	
		  if(document.orgInfoForm.elements[i].name!="orglike2"&&document.orgInfoForm.elements[i].name!="querlike2")
		  {		   			
		    document.orgInfoForm.elements[i].checked=false;
		  }
	   }
    }
   orgInfoForm.action="/general/inform/org/searchorgbrowse.do?b_search=link&code="+code+"&kind="+kind+"&orgtype="+orgtype+"&parentid="+parentid+"&returnvalue=scan&scantype=scan&return_codeid=${orgInfoForm.code}";
   orgInfoForm.submit();
}
//copySelectOrgWinReturn 要用到这两个变量 wangbs 20190315
var codeSetId;
var codeItemId;
function onecopy(codeitemid,codesetid)
{
    codeSetId = codesetid;
    codeItemId = codeitemid;
   <logic:equal value="1" name="orgInfoForm" property="busi_have" >
	var return_vo=select_org_dialog1(0,1,0,1,0,1,4);
    </logic:equal>  
    <logic:notEqual value="1" name="orgInfoForm" property="busi_have" >
	var return_vo=select_org_dialog(0,1,0,1,0,1);
    </logic:notEqual>  
    if(return_vo){
       var content=return_vo.content; 
      if(content!=""){
      	  var setid=content.substring(0,2);
      	  if(codesetid=="UN"&&setid!="UN"){
      	 	alert("指定的组织单元必须为单位!");
      	 	return;
      	  }
         if(confirm("确定把当前选中组织单元复制到指定的组织单元?"))
         {
        	  jindu1();
             var hashvo=new ParameterSet();     
             var selectid=new Array();   
             selectid[0]=codeitemid;  
             hashvo.setValue("orgcodeitemid", selectid);
             hashvo.setValue("content", content);
             hashvo.setValue("type","org");
             var request=new Request({asynchronous:true,method:'post',onSuccess:addduty,functionId:'0402000028'},hashvo);
         }
      } 
   }
   if(Ext.getCmp('select_org_dialog1_win'))
   	 Ext.getCmp('select_org_dialog1_win').type='onecopy';
}
//窗口关闭的回调，本页面与子页面共用抽个方法 wangbs 20190315
function copySelectOrgWinReturn(thevo){
if(Ext.getCmp('select_org_dialog1_win')){    
	var win = Ext.getCmp('select_org_dialog1_win');
	if(win.type == 'onecopy'){
		var content=thevo.content;
    	if(content!="")
    	{
        	var setid=content.substring(0,2);
        	if(codeSetId=="UN"&&setid!="UN"){
	            alert("指定的组织单元必须为单位!");
    	        return;
        	}
        	if(confirm("确定把当前选中组织单元复制到指定的组织单元?"))
        	{
            	jindu1();
	            var hashvo=new ParameterSet();
    	        var selectid=new Array();
        	    selectid[0]=codeItemId;
            	hashvo.setValue("orgcodeitemid", selectid);
            	hashvo.setValue("content", content);
            	hashvo.setValue("type","org");
            	var request=new Request({asynchronous:true,method:'post',onSuccess:addduty,functionId:'0402000028'},hashvo);
        	}
    	}	
	}else if(win.type == 'copyduty'){
		if(thevo)
       {
           if(thevo.content!="")
           {
           		var setid=thevo.content.substring(0,2);
           		if(selectcheckeditemUN()&&setid!="UN"){
      	 			alert("指定的组织单元必须为单位!");
      	 			return;
           		}
              if(confirm("确定把所选机构复制到指定的组织单元?"))
              {
            	jindu1();
                var hashvo=new ParameterSet();          
                hashvo.setValue("content", thevo.content);
                hashvo.setValue("orgcodeitemid", selectcheckeditem());
                hashvo.setValue("type","org");
                var request=new Request({asynchronous:true,method:'post',onSuccess:addduty,functionId:'0402000028'},hashvo);
              }
           }
       }
	}
}
    
}
//操作过程中屏蔽整个页面不可操作
function jindu1(){
	document.all.ly.style.display="";   
	document.all.ly.style.width=document.body.clientWidth;   
	document.all.ly.style.height=document.body.clientHeight; 
	
	var x=(window.screen.width-700)/2;
    var y=(window.screen.height-500)/2; 
	var waitInfo=eval("wait2");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="";
}
function addduty(outparamters)
{
	document.all.ly.style.display="none"; 
	var waitInfo=eval("wait2");
	waitInfo.style.display="none";
    var flag=outparamters.getValue("flag");
    var errordes=outparamters.getValue("errordes");
	if(flag=="ok")
	{
	  alert("复制操作成功！");
	  
	  parent.document.location.href="/workbench/orginfo/searchorginfo.do?b_search=link&backdate=&action=searchorginfodata.do&treetype=vorg&kind=2&target=nil_body&loadtype=1";
	  
	}else
	{
		if(errordes){
			alert(errordes);
		}else{
	    	alert("复制操作失败！");
		}
	}	
}
function delorg(codeitemid,rownum)
{
   if(confirm("你确定删除吗?注意:同时将删除单位库相对应的数据!"))
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
     		 rightDeleteSelectPosWinReturn(v,hashvo);
  	}else{
  		openSelectWin(hashvo);
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
     if(i==1){
     	while(table.rows.length>1){
     		table.deleteRow(1);
     	}
     }else{ 
     	table.deleteRow(i); 
     }
      deleter('delete',codesetid+code);
   }else
     alert("删除失败！");
}
function deleter(isrefresh,code,transfercodeitemidall,issuperuser,manageprive,newid,firstnode,newcodeitemdesc,codeleng,index,isnewcombineorg)
 {
	if(isrefresh=='delete')
	{
		var currnode=parent.frames['nil_menu'].Global.selectedItem;
		if(currnode==null)
				return;
		if(currnode.load){
			if(code.toUpperCase()==currnode.uid.toUpperCase())
					currnode.remove();
			for(var i=0;i<=currnode.childNodes.length-1;i++){
				if(code.toUpperCase()==currnode.childNodes[i].uid.toUpperCase())
					currnode.childNodes[i].remove();
			}
		}
	//currnode.expand();
	}
	if(isrefresh=='transfer')
	{
		var currnode=parent.frames['nil_menu'].Global.selectedItem;
		var issuperuser = issuperuser;
 		var manageprive = manageprive;
	 	 		//var new_id = newid;
		if(currnode==null)
				return;
		var oldnode = new Object();
		var root = currnode.root();
		var nodeid = "";
		a(root,transfercodeitemidall.toUpperCase());
		function a(root,name)
		{
			for(var z=0;z<=root.childNodes.length-1;z++){
				if(name==root.childNodes[z].uid){
					for(var q=0;q<=root.childNodes[z].childNodes.length-1;q++)
					{
						nodeid = root.childNodes[z].childNodes[q].uid;
					}
				}
				else
					a(root.childNodes[z],name);
			}
		}
		if(currnode.load && currnode.childNodes.length>0){
			for(var i=0;i<=currnode.childNodes.length-1;i++){
				if(code.toUpperCase()==currnode.childNodes[i].uid.toUpperCase()){
					oldnode.uid = currnode.childNodes[i].uid;
					oldnode.text = currnode.childNodes[i].text;
					oldnode.action = currnode.childNodes[i].action;
					oldnode.title = currnode.childNodes[i].title;
					oldnode.imgurl = currnode.childNodes[i].imgurl;
					oldnode.Xml = currnode.childNodes[i].Xml;
					var codesetid = currnode.childNodes[i].uid.substring(0,2);
					currnode.childNodes[i].remove();
					var kind = '0';
			   	 	if(codesetid=='UM')
			   	 		kind='1';
			   	 	else if(codesetid=='UN')
			   	 		kind='2';
			   	 	var imgurl;
		   	 		if(codesetid=='UM')
		   	 			imgurl="/images/dept.gif";
		   	 		else if(codesetid=='UN')
		   	 			imgurl="/images/unit.gif";
					var action = "/workbench/orginfo/searchorginfodata.do?b_search=link&code="+newid+"&kind="+kind;
					var xml = "/common/vorg/loadtree?params=child&treetype=org&parentid="  + newid + "&kind="+kind+"&issuperuser=" + issuperuser + "&manageprive=" + manageprive + "&action=searchorginfodata.do&target=nil_body";
					if(codesetid!='@K'){
						if(currnode.load)
							parent.frames['nil_menu'].addtrs(transfercodeitemidall,codesetid+newid,oldnode.text,action,"nil_body",oldnode.title,imgurl,xml);
						else
							currnode.expand();
					}
				}
			}
		}
		else if(currnode.uid.toUpperCase() == code.toUpperCase())
		{
			oldnode.uid = currnode.uid;
			oldnode.text = currnode.text;
			oldnode.action = currnode.action;
			oldnode.title = currnode.title;
			oldnode.imgurl = currnode.imgurl;
			oldnode.Xml = currnode.Xml;
			var codesetid = currnode.uid.substring(0,2);
			
			var kind = '0';
	   	 	if(codesetid=='UM')
	   	 		kind='1';
	   	 	else if(codesetid=='UN')
	   	 		kind='2';
	   	 	var imgurl;
	  	 		if(codesetid=='UM')
	  	 			imgurl="/images/dept.gif";
	  	 		else if(codesetid=='UN')
	  	 			imgurl="/images/unit.gif";
			var action = "/workbench/orginfo/searchorginfodata.do?b_search=link&code="+newid+"&kind="+kind;
			var xml = "/common/vorg/loadtree?params=child&treetype=org&parentid="  + newid + "&kind="+kind+"&issuperuser=" + issuperuser + "&manageprive=" + manageprive + "&action=searchorginfodata.do&target=nil_body";
			if(codesetid!='@K'){
					parent.frames['nil_menu'].addtrs(transfercodeitemidall,codesetid+newid,oldnode.text,action,"nil_body",oldnode.title,imgurl,xml);
			}
			currnode.remove();
		}
	}

	if(isrefresh=='combineorg')
	{
		var currnode=parent.frames['nil_menu'].Global.selectedItem;
		var codesetid = firstnode.substring(0,2);
		var imgurl;
		var newnode = new Object();
 		if(codesetid=='UM')
 			imgurl="/images/dept.gif";
 		else if(codesetid=='UN')
 			imgurl="/images/unit.gif";
		if(currnode==null)
				return;
		if(isnewcombineorg=='yes'){
	   	 	var pt = currnode.getLastChild();
	   	 	if(pt.uid.indexOf(firstnode)==-1){
		   	 	var uid = firstnode;
		   	 	var text = newcodeitemdesc;
		   	 	var title = newcodeitemdesc;
		   	 	var issuperuser = issuperuser;
		   	 	var manageprive = manageprive;
		   	 	var kind = '0';
		   	 	if(codesetid=='UM')
		   	 		kind='1';
		   	 	else if(codesetid=='UN')
		   	 		kind='2';
		   	 	var orgtype = "org";
		   	 	var action = "/workbench/orginfo/searchorginfodata.do?b_search=link&code="+firstnode.substring(2)+"&kind="+kind;
		   	 	var xml = "/common/vorg/loadtree?params=child&treetype=org&parentid="+firstnode.substring(2) + "&kind="+kind+"&issuperuser=" + issuperuser + "&manageprive=" + manageprive + "&action=searchorginfodata.do&target=nil_body";
		   	 	if(currnode==currnode.root()&&code.length>0)
		   	 		currnode = currnode.getFirstChild();
		   	 	if(currnode.load)
		   	 	{
		   	 		var imgurl;
		   	 		if(codesetid=='UM'){
		   	 			imgurl="/images/dept.gif";
		   	 		}
		   	 		else if(codesetid=='UN'){
		   	 			imgurl="/images/unit.gif";
		   	 		}
		   	 		if(codesetid!='@K')
		   	 			parent.frames['nil_menu'].add(uid,text,action,"nil_body",title,imgurl,xml);
	   	 		}else
	   	 			currnode.expand();
	  	 		}
		}else{
			for(var i=0;i<=currnode.childNodes.length-1;i++){
				if(firstnode.toUpperCase()==currnode.childNodes[i].uid.toUpperCase()){
					newnode.uid = currnode.childNodes[i].uid;
					newnode.text = currnode.childNodes[i].text;
					newnode.action = currnode.childNodes[i].action;
					newnode.title = currnode.childNodes[i].title;
					newnode.xml = currnode.childNodes[i].Xml;
				}
			}
		}
		if(codesetid!='@K'){
			if(currnode.load)
			{
				for(var i=0;i<=currnode.childNodes.length-1;i++){
					if(code.toUpperCase()==currnode.childNodes[i].uid.toUpperCase())
						currnode.childNodes[i].remove();
				}
			}
			if(index==codeleng&&isnewcombineorg!='yes')
			{
				parent.frames['nil_menu'].add(newnode.uid,newcodeitemdesc,newnode.action,"nil_body",newnode.title,imgurl,newnode.xml);
			}
	    }
	}
 }

function bolish()
{
	var len=document.orgInfoForm.elements.length;
	var uu;
	for(var i=0;i<len;i++)
	{			
		if(document.orgInfoForm.elements[i].type=='checkbox'&&document.orgInfoForm.elements[i].name!="selbox")
		{	
		   if(document.orgInfoForm.elements[i].name!="orglike2"&&document.orgInfoForm.elements[i].name!="querlike2")
		   {
				if(document.orgInfoForm.elements[i].checked==true)
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
	var hashvo=new ParameterSet();          
	hashvo.setValue("orgcodeitemid", selectcheckeditem());
	var In_paramters="";
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:exebolish,functionId:'16010000029'},hashvo);
}
function exebolish(outparamters){
	var msg=outparamters.getValue('msg');
	if(msg=="equals"){
		alert(ORG_NEW_NOT_REVOKE);
	}else if(msg="ok"){
		var maxstartdate=outparamters.getValue('maxstartdate');
		if(confirm("你确定撤销吗?"))
		{
			orgInfoForm.action="/workbench/orginfo/searchorginfodata.do?b_initbolish=link&maxstartdate="+maxstartdate;
			orgInfoForm.submit();
		} 
	}else{
		alert("检查能否此操作时失败，不允许此操作！");
	}
	 
}
function onebolish(s)
{
	//if(confirm("你确定撤销吗?")){
	   s--;
	   var len=document.orgInfoForm.elements.length;
	   for(var i=0;i<len;i++)
	   {			
		      if(document.orgInfoForm.elements[i].type=='checkbox'&&document.orgInfoForm.elements[i].name!="selbox")
		      {	
		         if(document.orgInfoForm.elements[i].name!="orglike2"&&document.orgInfoForm.elements[i].name!="querlike2")
			     {
	               document.orgInfoForm.elements[i].checked=false;
	             }
	          }
	    }
	   var vo=document.getElementsByName("pagination.select["+s+"]")[0];
	   vo.checked=true;
	   var hashvo=new ParameterSet();          
           hashvo.setValue("orgcodeitemid", selectcheckeditem());
           var In_paramters="";
           var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:exebolish,functionId:'16010000029'},hashvo);
	   //orgInfoForm.action="/workbench/orginfo/searchorginfodata.do?b_initbolish=link";
	   //orgInfoForm.submit();
	 //}
}
function copyduty()
{
   	   var len=document.orgInfoForm.elements.length;
       var uu;
       for(var i=0;i<len;i++)
	   {			
	      if(document.orgInfoForm.elements[i].type=='checkbox'&&document.orgInfoForm.elements[i].name!="selbox")
	      {	
	         if(document.orgInfoForm.elements[i].name!="orglike2"&&document.orgInfoForm.elements[i].name!="querlike2")
		     {
               if(document.orgInfoForm.elements[i].checked==true)
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
       var hashvo=new ParameterSet(); 
       hashvo.setValue("orgcodeitemid", selectcheckeditem());
       var request=new Request({method:'post',onSuccess:execopy,functionId:'0402000032'},hashvo);
}

function execopy(outparamter){
<logic:equal value="1" name="orgInfoForm" property="busi_have" >
	var return_vo=select_org_dialog1(0,1,0,1,0,1,4);
</logic:equal>  
<logic:notEqual value="1" name="orgInfoForm" property="busi_have" >
	var return_vo=select_org_dialog(0,1,0,1,0,1);
</logic:notEqual> 
       if(return_vo)
       {
           if(return_vo.content!="")
           {
           		var setid=return_vo.content.substring(0,2);
           		if(selectcheckeditemUN()&&setid!="UN"){
      	 			alert("指定的组织单元必须为单位!");
      	 			return;
           		}
              if(confirm("确定把所选机构复制到指定的组织单元?"))
              {
            	jindu1();
                var hashvo=new ParameterSet();          
                hashvo.setValue("content", return_vo.content);
                hashvo.setValue("orgcodeitemid", selectcheckeditem());
                hashvo.setValue("type","org");
                var request=new Request({asynchronous:true,method:'post',onSuccess:addduty,functionId:'0402000028'},hashvo);
              }
           }
       }
       if(Ext.getCmp('select_org_dialog1_win'))
   	 		Ext.getCmp('select_org_dialog1_win').type='copyduty';
}
function deleterec()
{
       var len=document.orgInfoForm.elements.length;
       var uu;
       for (var i=0;i<len;i++)
       {
           if(document.orgInfoForm.elements[i].type=='checkbox'&&document.orgInfoForm.elements[i].name!="selbox")
	       {	
		      if(document.orgInfoForm.elements[i].name!="orglike2"&&document.orgInfoForm.elements[i].name!="querlike2")
		      {	
                if(document.orgInfoForm.elements[i].checked==true)
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
      if(confirm("你确定删除吗?注意:同时将删除单位库相对应的数据!"))
      {
      
        if(selectcheckeditem()!=null)
        {
			selectid = selectcheckeditem();
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
        	openSelectWin("");
        }
     }else
     {
        orgInfoForm.action="/workbench/orginfo/searchorginfodata.do?b_delete=del&delpersonorg=f&type=org";
        orgInfoForm.submit(); 
		alert("删除成功！");
        refreshTree();
     }  
 }
 function bottomDeleteSelectPosWinReturn(return_value){
     if(return_value==6){
    	 orgInfoForm.action="/workbench/orginfo/searchorginfodata.do?b_delete=del&delpersonorg=t&type=org";
     }
     else if(return_value==7){
    	 orgInfoForm.action="/workbench/orginfo/searchorginfodata.do?b_delete=del&delpersonorg=f&type=org";
     }else{
         return false;
     }
     orgInfoForm.submit(); 
	alert("删除成功！");
     refreshTree();
 }
 function refreshTree(){
	 for(var i=0;i<selectid.length;i++){
		 showOrgContext(selectid[i]);
		 deleter('delete',codesetid+code);
	 }
 }
 function openSelectWin(hashvo){//是否清空下属人员的机构信息弹窗
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
            	 if (hashvo=="") {
                	 bottomDeleteSelectPosWinReturn(this.return_vo);
				}else
				{ 
					rightDeleteSelectPosWinReturn(this.return_vo,hashvo);
				}
				}
         }
     });
}
 function selectcheckeditem()
   {
	 var a=0;
	var b=0;
	var selectid=new Array();
	var a_IDs=eval("document.orgInfoForm.orgcodeitemid");	
	var nums=0;		
	for(var i=0;i<document.orgInfoForm.elements.length;i++)
	{			
	   if(document.orgInfoForm.elements[i].type=='checkbox'&&document.orgInfoForm.elements[i].name!="selbox")
	   {	
		  if(document.orgInfoForm.elements[i].name!="orglike2"&&document.orgInfoForm.elements[i].name!="querlike2")
		  {		   			
		    nums++;
		  }
	   }
    }
	
	if(nums>1)
	{
	    for(var i=0;i<document.orgInfoForm.elements.length;i++)
	    {			
		   if(document.orgInfoForm.elements[i].type=='checkbox'&&document.orgInfoForm.elements[i].name!="selbox")
		   {	
		     if(document.orgInfoForm.elements[i].name!="orglike2"&&document.orgInfoForm.elements[i].name!="querlike2")
		     {
		       if(document.orgInfoForm.elements[i].checked==true)
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
	   for(var i=0;i<document.orgInfoForm.elements.length;i++)
	   {			
	      if(document.orgInfoForm.elements[i].type=='checkbox'&&document.orgInfoForm.elements[i].name!="selbox")
	      {	
	         if(document.orgInfoForm.elements[i].name!="orglike2"&&document.orgInfoForm.elements[i].name!="querlike2")
		     {
		        if(document.orgInfoForm.elements[i].checked==true)
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
		return;
	}
	return selectid;	
 }
 
  function selectcheckeditemUN()
   {
   	var ishave=false;
      	var a=0;
	var b=0;
	var a_IDs=eval("document.orgInfoForm.orgcodeitemsetid");	
	var nums=0;		
	for(var i=0;i<document.orgInfoForm.elements.length;i++)
	{			
	   if(document.orgInfoForm.elements[i].type=='checkbox'&&document.orgInfoForm.elements[i].name!="selbox")
	   {	
		  if(document.orgInfoForm.elements[i].name!="orglike2"&&document.orgInfoForm.elements[i].name!="querlike2")
		  {		   			
		    nums++;
		  }
	   }
    }
	if(nums>1)
	{
	    for(var i=0;i<document.orgInfoForm.elements.length;i++)
	    {			
		   if(document.orgInfoForm.elements[i].type=='checkbox'&&document.orgInfoForm.elements[i].name!="selbox")
		   {	
		     if(document.orgInfoForm.elements[i].name!="orglike2"&&document.orgInfoForm.elements[i].name!="querlike2")
		     {
		       if(document.orgInfoForm.elements[i].checked==true)
		       {
			      if(a_IDs[b].value=="UN"){
			      	ishave=true;
			      	break;
			      }					
		       }
		       b++;
		    }
		  }
	   }
	}
	if(nums==1)
	{
	   for(var i=0;i<document.orgInfoForm.elements.length;i++)
	   {			
	      if(document.orgInfoForm.elements[i].type=='checkbox'&&document.orgInfoForm.elements[i].name!="selbox")
	      {	
	         if(document.orgInfoForm.elements[i].name!="orglike2"&&document.orgInfoForm.elements[i].name!="querlike2")
		     {
		        if(document.orgInfoForm.elements[i].checked==true)
		        {
			       if(a_IDs.value=="UN"){
			      	ishave=true;
			      }					
		        }
		     }
	      }
	   }
	}
	return ishave;	
 }
 function newduty()
{	
	var currnode=parent.frames['nil_menu'].Global.selectedItem;
	if(currnode==null){
		alert("请先选择左侧的组织单元！");
		return false;
	}
	if("<bean:write name="orgInfoForm" property="code"/>"==""&&"<bean:write name="orgInfoForm" property="kind"/>"=="") {
			window.alert("请先选择左侧的组织单元！");
		} else {
			if('${orgInfoForm.code}'.length>=30){
				alert("当前组织机构代码 已达最大长度，无法新增下级机构！");
				return false;
			}
	   orgInfoForm.action="/workbench/orginfo/editorginfodata.do?b_search=link&code=${orgInfoForm.code}&kind=${orgInfoForm.kind}&orgtype=${orgInfoForm.orgtype}&returnvalue=scan&scantype=scan&edittype=add&isself=0";
	   orgInfoForm.submit();
	   }
}
function transfer()
   {
   		/*if(selectcheckedorg())
   		{
   			alert("您选择了虚拟机构，不允许此操作！");
   			return;
   		}*/
   	   var len=document.orgInfoForm.elements.length;
       var uu;
       for (var i=0;i<len;i++)
       {
           if (document.orgInfoForm.elements[i].type=="checkbox"&&document.orgInfoForm.elements[i].name!='selbox' 
        	   && document.orgInfoForm.elements[i].name!='orglike2' && document.orgInfoForm.elements[i].name!='querlike2')
           {
              if(document.orgInfoForm.elements[i].checked==true)
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
   		if(msg=="equals"){
   			alert(ORG_NEW_NOT_REVOKE);
   		}else if(msg="ok"){
	   		var maxstartdate=outparamters.getValue('maxstartdate');
	   		orgInfoForm.action="/workbench/orginfo/searchorginfodata.do?b_transfer=link&maxstartdate="+maxstartdate;
	        orgInfoForm.submit(); 
        }else{
        	alert("检查能否此操作时失败，不允许此操作！");
        }
   }
   
function combine()
   {
   		/*if(selectcheckedorg())
   		{
   			alert("您选择了虚拟机构，不允许此操作！");
   			return;
   		}*/
   	   var len=document.orgInfoForm.elements.length;
       var uu;
       for (var i=0;i<len;i++)
       {
           if (document.orgInfoForm.elements[i].type=="checkbox"&&document.orgInfoForm.elements[i].name!='selbox')
           {
              if(document.orgInfoForm.elements[i].checked==true)
              {
                uu="dd";
                break;
               }
           }
       }
       if(uu!="dd")
       {
          alert("请选择需要合并的机构！");
          return false;
       }
          var codeitemid = selectcheckeditem();
          if(codeitemid.length<2){
        	  alert("单个机构不能合并！");
        	  return;
          }
      	   var hashvo=new ParameterSet();    
           hashvo.setValue("orgcodeitemid", codeitemid);
           var In_paramters="";
           var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:execombine,functionId:'16010000029'},hashvo);
   }
   function execombine(outparamters){
   		var msg=outparamters.getValue('msg');
   		if(msg=="equals"){
   			alert(ORG_NEW_NOT_REVOKE);
   		}else if(msg="ok"){
	   		var maxstartdate=outparamters.getValue('maxstartdate');
	   		orgInfoForm.action="/workbench/orginfo/searchorginfodata.do?b_combine=link&maxstartdate="+maxstartdate;
	        orgInfoForm.submit(); 
        }else{
        	alert("检查能否此操作时失败，不允许此操作！");
        }
   }
   //add by wangchaoqun on 2014-9-17 begin
   function getTrueValue(index){
       var elements =  document.getElementsByName("selectfieldlist["+index+"].value");
       var v = elements[index].value;
       if(v != null && v.length>0 && (v.indexOf('UN')==0 || v.indexOf('UM')==0 || v.indexOf('@K')==0)){
           v = v.substring(2);
       }
       elements[index].value = v;
   }
   //add by wangchaoqun on 2014-9-17 end
   
    function openCodeDialog(codeid,mytarget,managerstr,flag) 
    {
        var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
        if(mytarget==null)
          return;
        var oldInputs=document.getElementsByName(mytarget);
        oldobj=oldInputs[0];
        //根据代码显示的对象名称查找代码值名称	
        target_name=oldobj.name;
        hidden_name=target_name.replace(".viewvalue",".value");
        hidden_name=hidden_name.replace(".hzvalue",".value");
        hidden_name=hidden_name.replace("name1","namevalue");  
        var hiddenInputs=document.getElementsByName(hidden_name);
        if(hiddenInputs!=null&&hiddenInputs.length>0)
        {
        	hiddenobj=hiddenInputs[0];
        	codevalue=managerstr;
        }else{
        	hiddenobj=document.getElementById(hidden_name);
        	codevalue=managerstr;
        }
        var params=new Array(codeid,codevalue,oldobj,hiddenobj,flag);  
/*      thecodeurl="/org/orgpre/getorgcode.jsp?ctrl_type=1&levelctrl=0";
        var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
        var popwin= window.showModalDialog(thecodeurl, theArr, 
            "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
*/
        var dw = 330;
        var dh = 420;
        var theurl = '/org/orgpre/getorgcode.jsp?ctrl_type=1&levelctrl=0';
        var obj;
    	if(getBrowseVersion()) //IE浏览器
    		obj = parent.frames[2];
    	else //非IE
    		obj = parent.frames['center_iframe'][2].contentWindow;
    	
    	if(obj.Ext.getCmp("code_dialog")){
    		obj.Ext.getCmp("code_dialog").close(); //防止再次点击
        }
    	var win = obj.Ext.create('Ext.window.Window',{
    		id:'code_dialog',
    		title:'选择机构',
    		width:dw,
    		height:dh,
    		resizable:false,
    		modal:true,
    		autoScroll:false,
    		autoShow:true,
    		autoDestroy:true,
    		html:'<iframe style="background-color:#fff;" frameborder="0" scrolling=no height="100%" width="100%" src="'+theurl+'"></iframe>',
    		renderTo:obj.Ext.getBody()
    	});
    	win.params = params;
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
<html:form action="/workbench/orginfo/searchorginfodata">
  <table width='97%' border=0 style="margin-top: -4px;" cellpadding="0" cellspacing="0">
    <tr style="margin-bottom:-3px;">
      <td height="25px">
          <table>
             <tr>
                <td >
                    当前组织单元：<!--  ${orgInfoForm.codemess}-->
                    <logic:empty name="orgInfoForm" property="codemess"><bean:write name="orgInfoForm" property="root"/></logic:empty>
                    <logic:notEmpty name="orgInfoForm" property="codemess"><bean:write name="orgInfoForm" property="codemess"/></logic:notEmpty>
                    &nbsp;&nbsp;&nbsp;&nbsp;
                </td>
                <td>
                <logic:notEmpty name="orgInfoForm"  property="selectfieldlist">
                  <table  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
                    <tr height=17>
                       <td valign="top"><font size="1">[</font>&nbsp;
                       </td>
                       <td id="vieworhidd" valign="middle"> 
                                                                                
                            <a href="javascript:showOrClose();" > 
                                     <logic:equal name="orgInfoForm" property="isShowCondition" value="none" >查询显示</logic:equal>   
                                     <logic:equal name="orgInfoForm" property="isShowCondition" value="" >查询隐藏</logic:equal>                                      
                            </a>
                       </td>                       
                       <td  valign="top">&nbsp;<font size="1">]</font>&nbsp;&nbsp;&nbsp;&nbsp;
                       </td>
                    </tr>
                  </table>
                  </logic:notEmpty>
                </td>
                <td>
                 <logic:equal name="orgInfoForm" property="orglike" value="1">
                     <input type="checkbox" id="orglikeid1" name="orglike2" value="true" onclick="selectCheckBox(document.getElementById('orglikeid1'),'orglike');search();" checked>
                 </logic:equal>
                 <logic:notEqual name="orgInfoForm" property="orglike" value="1">
                     <input type="checkbox" id="orglikeid2" name="orglike2" value="true" onclick="selectCheckBox(document.getElementById('orglikeid2'),'orglike');search();">
                 </logic:notEqual>
                 
               <html:hidden name="orgInfoForm" property='orglike' styleId="orglike" styleClass="text"/>                 
                     显示当前组织单元下所有组织单元
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
<!-- 非兼容模式浏览器兼容修改：div的height设置成0，查询面板无法显示。去掉俩样式：height:0;  class="fixedDiv2 " guodd 2018-03-01 -->
<div  style=" border:0px;">
   <table  border="0" cellspacing="0" width="100%"  align="center" cellpadding="0" id='aa'  style='display:${orgInfoForm.isShowCondition}; '>
     <tr>
      <td>   
         <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow">
          <tr class="trShallow1">
           <td align="center" colspan="4" height='20' class="RecordRow" nowrap>
            <bean:message key="label.query.inforquery"/><!-- 请选择查询条件! -->
           </td>
          </tr> 
          <logic:iterate id="element" name="orgInfoForm"  property="selectfieldlist" indexId="index"> 
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
                          <td align="right" class="tdFontcolor" nowrap>                
                            <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;&nbsp;&nbsp;&nbsp;
                          </td>
                          <!--日期型 -->                            
                          <logic:equal name="element" property="itemtype" value="D">
                            <td align="left" height='28' nowrap>    
                               
                               <html:text name="orgInfoForm" property='<%="selectfieldlist["+index+"].value"%>' size="13" maxlength="10" styleClass="textColorWrite" title="输入格式：2008.08.08" onclick=""/>
                               <bean:message key="label.query.to"/><html:text name="orgInfoForm" property='<%="selectfieldlist["+index+"].viewvalue"%>' size="13" maxlength="10" styleClass="textbox" title="输入格式：2008.08.08"  onclick=""/>
			                   <!-- 没有什么用，仅给用户与视觉效果-->
			                   <INPUT type="radio" name="${element.itemid}"  checked="true"><bean:message key="label.query.age"/>	
			                   <INPUT type="radio" name="${element.itemid}" id="day"><bean:message key="label.query.day"/>
			                    	
                            </td>                           
                          </logic:equal>
                          <!--备注型 -->                              
                          <logic:equal name="element" property="itemtype" value="M">
                            <td align="left" height='28' nowrap>                
                               <html:text name="orgInfoForm" property='<%="selectfieldlist["+index+"].value"%>' size="30" maxlength='<%="selectfieldlist["+index+"].itemlength"%>' styleClass="textColorWrite"/>                               
                            </td>                           
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="itemtype" value="A">
                            <td align="left" height='28' nowrap>
                              <logic:notEqual name="element" property="codesetid" value="0">
                                <html:hidden name="orgInfoForm" property='<%="selectfieldlist["+index+"].value"%>' styleClass="text" onchange='<%="getTrueValue("+index+");" %>'/>                               
                                <html:text name="orgInfoForm" property='<%="selectfieldlist["+index+"].viewvalue"%>' size="30" maxlength="50" styleClass="textColorWrite" onchange="fieldcode(this,2);"/>
                                  <logic:notEqual name="element" property="codesetid" value="UN">  
                                    <logic:equal name="element" property="itemid" value="e0122">
                  						  <img src="/images/code.gif"  plugin="codeselector" codesetid='UM'  onlySelectCodeset="true" inputname="<%="selectfieldlist["+index+"].viewvalue"%>" valuename='<%="selectfieldlist["+index+"].value"%>'  align="absmiddle" />	
                                    </logic:equal>    
                                    <logic:notEqual name="element" property="itemid" value="e0122"> 
                                         <%-- <img src="/images/code.gif" onclick='openInputCodeDialog("${element.codesetid}","<%="selectfieldlist["+index+"].viewvalue"%>","0");' align="absmiddle"/>  --%>
                                         <img src="/images/code.gif"  plugin="codeselector" codesetid='${element.codesetid}'  inputname="<%="selectfieldlist["+index+"].viewvalue"%>" valuename='<%="selectfieldlist["+index+"].value"%>'  align="absmiddle" />
                                     </logic:notEqual>                                                                                                 
                                  </logic:notEqual>   
                                  <logic:equal name="element" property="codesetid" value="UN">                                      
                                         <logic:equal name="element" property="itemid" value="b0110"> 
                                           <%-- <img src="/images/code.gif" onclick='openCodeDialog("UN","<%="selectfieldlist["+index+"].viewvalue"%>","<%=manager %>",1)' align="absmiddle"/> --%>
                                           <img src="/images/code.gif"  plugin="codeselector" codesetid='UN'  onlySelectCodeset="true"  inputname="<%="selectfieldlist["+index+"].viewvalue"%>" valuename='<%="selectfieldlist["+index+"].value"%>'  align="absmiddle" />
                                         </logic:equal> 
                                         <logic:notEqual name="element" property="itemid" value="b0110"> 
                                           <%-- <img src="/images/code.gif" onclick='openInputCodeDialog("UM","<%="selectfieldlist["+index+"].viewvalue"%>","0");' align="absmiddle"/> --%>
                                           <img src="/images/code.gif"  plugin="codeselector" codesetid='UM'  onlySelectCodeset="true"  inputname="<%="selectfieldlist["+index+"].viewvalue"%>" valuename='<%="selectfieldlist["+index+"].value"%>'  align="absmiddle" />
                                         </logic:notEqual>   
                                  </logic:equal>                                                                                                       
                              </logic:notEqual> 
                              <logic:equal name="element" property="codesetid" value="0">
                                <html:text name="orgInfoForm" property='<%="selectfieldlist["+index+"].value"%>' size="30" maxlength="${element.itemlength}" styleClass="textColorWrite"/>                               
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="itemtype" value="N">
                            <td align="left" height='28' nowrap>                
                               <html:text name="orgInfoForm" property='<%="selectfieldlist["+index+"].value"%>' size="30" maxlength="${element.itemlength}" styleClass="textColorWrite"/>                               
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
                 <tr><!--  class="trShallow1" -->
    	          <td align="right" height='20'  nowrap>
    	            <bean:message key="label.query.like"/>&nbsp; 
    	         </td>
    	         <td align="left" colspan="3" height='20' nowrap>
    	          <logic:equal value="1" name="orgInfoForm" property='querylike'>
    	            <input type="checkbox" name="querlike2" value="true" checked="checked" onclick="selectCheckBox(this,'querylike');">
    	          </logic:equal>
    	          <logic:notEqual value="1" name="orgInfoForm" property='querylike'>
                    <input type="checkbox" name="querlike2" value="true" onclick="selectCheckBox(this,'querylike');">
                  </logic:notEqual>
                     <html:hidden name="orgInfoForm" property='querylike' styleId="querylike" styleClass="text"/>
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
      </div>
    </td>
   </tr> 
   <tr>
     <td>
         <table  width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style='display:' >
          <tr>
             <td>
             <div id="databox" class="fixedDiv2" style="height:600px">
                 <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id="pag">
   	             <tr class="fixedHeaderTr">
   	             <td align="center" class="TableRow" style="border-top:none;border-left:none;border-right: none;" nowrap>
		            <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
                 </td>
		          <logic:iterate id="element"    name="orgInfoForm"  property="fieldList" indexId="index">
                   <td align="center" class="TableRow" style="border-top:none;border-right:none;" nowrap>
                 &nbsp;&nbsp;&nbsp; <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;&nbsp;&nbsp;
	       	       </td>            
                 </logic:iterate> 
                 <logic:equal value="leader" name="orgInfoForm" property="leader" >
                 <hrms:priv func_id="23152">
                   <td align="center" class="TableRow" style="border-top:none;border-right:none;" nowrap>
		     		&nbsp;领导班子&nbsp;
		            </td> 
		          </hrms:priv>
		          </logic:equal>
		          <logic:notEqual value="leader" name="orgInfoForm" property="leader" >
                 <hrms:priv func_id="2306008">
                   <td align="center" class="TableRow" style="border-top:none;border-right:none;" nowrap>
		     		&nbsp;人员浏览&nbsp;
		            </td> 
		          </hrms:priv>
		          </logic:notEqual>
		          <logic:equal value="leader" name="orgInfoForm" property="leader" >
                 <hrms:priv func_id="23153">
                   <td align="center" class="TableRow" style="border-top:none;border-right:none;" nowrap>
		     		&nbsp;人员浏览&nbsp;
		            </td> 
		          </hrms:priv>
		          </logic:equal>
		          <td align="center" class="TableRow" style="border-top:none;border-right:none;" nowrap>
		     		&nbsp;操作&nbsp;
		          </td> 		         
               </tr>
               <hrms:paginationdb id="element" name="orgInfoForm" sql_str="orgInfoForm.sqlstr" table="" where_str="orgInfoForm.wherestr" columns="orgInfoForm.columnstr" order_by="orgInfoForm.orderby" page_id="pagination" pagerows="${orgInfoForm.pagerows}" distinct="" keys="">
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
             	   String codeitme=(String)abean.get("code"); 
             	   String codesetid=(String)abean.get("codesetid");             	            	   
                   request.setAttribute("codeitme",codeitme);  
                   request.setAttribute("codesetid",codesetid);
                   String end_date = (String)abean.get("end_date");   
                   // 如遇9999.12.31的情况，下面sdf.parse(end_date)会报日期格式化错。故把.替换成-再进行格式化 chent 20180321 add
                   end_date = end_date.replace(".", "-");
                   %>
                   <td align="center" class="RecordRow"  style="border-left:none;border-top:none;border-right:none;" nowrap>
                      <input type="hidden" name="orgcodeitemid" value="${codeitme}"> 
                      <input type="hidden" name="orgcodeitemsetid" value="${codesetid}"> 
                      
                      <hrms:checkmultibox name="orgInfoForm" property="pagination.select"  value="true" indexes="indexes"/>&nbsp;
                   </td>                   
                   <logic:iterate id="fielditem"  name="orgInfoForm"  property="fieldList" indexId="index">
                        <logic:notEqual name="fielditem" property="codesetid" value="0">
                           <td align="left" class="RecordRow" style="border-top:none;border-right:none;" nowrap>&nbsp;
                                <logic:equal name="fielditem" property="itemid" value="b0110">
	                                <%if(sdf.parse(sdf.format(new Date())).compareTo(sdf.parse(end_date))<=0){ %>
	                                	<logic:equal name="element" property="codesetid" value="UN">
						            		<logic:equal name="element" property="orgtype" value="vorg">
						            			<img src="/images/vroot.gif" border=0>
						            		</logic:equal>
						            		<logic:notEqual name="element" property="orgtype" value="vorg">
						            			<img src="/images/unit.gif" border=0>
						            		</logic:notEqual>
						            	</logic:equal>
						            	<logic:equal name="element" property="codesetid" value="UM">
						            		<logic:equal name="element" property="orgtype" value="vorg">
						            			<img src="/images/vdept.gif" border=0>
						            		</logic:equal>
						            		<logic:notEqual name="element" property="orgtype" value="vorg">
						            			<img src="/images/dept.gif" border=0>
						            		</logic:notEqual>
						            	</logic:equal>
						            	<logic:equal name="element" property="codesetid" value="@K">
						            		<logic:equal name="element" property="orgtype" value="vorg">
						            			<img src="/images/vpos_l.gif" border=0>
						            		</logic:equal>
						            		<logic:notEqual name="element" property="orgtype" value="vorg">
						            			<img src="/images/pos_l.gif" border=0>
						            		</logic:notEqual>
						            	</logic:equal>
						            <%}else{ %>
	                                	<logic:equal name="element" property="codesetid" value="UN">
						            		<logic:equal name="element" property="orgtype" value="vorg">
						            			<img src="/images/b_vroot.gif" border=0>
						            		</logic:equal>
						            		<logic:notEqual name="element" property="orgtype" value="vorg">
						            			<img src="/images/b_unit.gif" border=0>
						            		</logic:notEqual>
						            	</logic:equal>
						            	<logic:equal name="element" property="codesetid" value="UM">
						            		<logic:equal name="element" property="orgtype" value="vorg">
						            			<img src="/images/b_vdept.gif" border=0>
						            		</logic:equal>
						            		<logic:notEqual name="element" property="orgtype" value="vorg">
						            			<img src="/images/b_dept.gif" border=0>
						            		</logic:notEqual>
						            	</logic:equal>
						            	<logic:equal name="element" property="codesetid" value="@K">
						            		<logic:equal name="element" property="orgtype" value="vorg">
						            			<img src="/images/b_vpos_1.gif" border=0>
						            		</logic:equal>
						            		<logic:notEqual name="element" property="orgtype" value="vorg">
						            			<img src="/images/b_pos_1.gif" border=0>
						            		</logic:notEqual>
						            	</logic:equal>
						            <%} %>
						            
	                                  <hrms:codetoname codeid="UN" name="element" codevalue="code" codeitem="codeitem"  scope="page"/>  	      
	          	                      <bean:write name="codeitem" property="codename" />
	          	                      <logic:empty name="codeitem" property="codename">
	                                  	<hrms:codetoname codeid="UM" name="element" codevalue="code" codeitem="codeitem" uplevel="${orgInfoForm.uplevel}" scope="page"/>  	      
	                                  	<bean:write name="codeitem" property="codename" />
	                                  </logic:empty>
	                                  &nbsp;  
          	                   	</logic:equal>
          	                    <logic:notEqual name="fielditem" property="itemid" value="b0110">
          	                     <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" asOrg="true" codeitem="codeitem" scope="page"/>  	      
          	                      <bean:write name="codeitem" property="codename" />&nbsp;                    
                                </logic:notEqual>                 
                            </td>
                        </logic:notEqual>
	                        <logic:equal name="fielditem" property="codesetid" value="0">
	                            <logic:equal name="fielditem" property="itemtype" value="N">
	                              <td align="right" class="RecordRow" style="border-top:none;border-right:none;" nowrap>&nbsp;
	                                 <bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;
	                              </td>
	                            </logic:equal>
	                            <logic:notEqual name="fielditem" property="itemtype" value="N">
	                              <td align="left" class="RecordRow" style="border-top:none;border-right:none;" nowrap>&nbsp;
	                                <logic:notEqual name="element" property="${fielditem.itemid}" value="null">
	                                 <bean:write name="element" property="${fielditem.itemid}" filter="false"/>
	                                </logic:notEqual>
	                                 &nbsp;
	                              </td>
	                            </logic:notEqual>
	                        </logic:equal>                          
                     </logic:iterate>
                     <logic:equal value="leader" name="orgInfoForm" property="leader" >
                    		<hrms:priv func_id="23152">
	                          <td align="center" class="RecordRow" style="border-top:none;border-right:none;" nowrap>
			     		        &nbsp;&nbsp;
			     		       <a href="javascript:showOrgContext('${codeitme}');editorg();"><img src="/images/view.gif" border="0"></a>
			     		        &nbsp;&nbsp;		     		      
			                  </td> 
		                  </hrms:priv>
		           </logic:equal>
		           <logic:notEqual value="leader" name="orgInfoForm" property="leader" >
		               <hrms:priv func_id="2306008">
                          <td align="center" class="RecordRow" style="border-top:none;border-right:none;" nowrap>
		     		        <bean:define id="timess" name="element" property="end_date"></bean:define>
                     		 <input type="hidden" name="end_dates" value="${timess}">
		     		       <a href="javascript:showEmp('${codeitme}','${timess}');"><img src="/images/view.gif" border="0"></a>
		     		           		      
		                  </td> 
		               </hrms:priv>
		           </logic:notEqual>
		           <logic:equal value="leader" name="orgInfoForm" property="leader" >
		               <hrms:priv func_id="23153">
                          <td align="center" class="RecordRow" style="border-top:none;border-right:none;" nowrap>
		     		        <bean:define id="timess" name="element" property="end_date"></bean:define>
                     		 <input type="hidden" name="end_dates" value="${timess}">
		     		       <a href="javascript:showEmp('${codeitme}','${timess}');"><img src="/images/view.gif" border="0"></a>
		     		           		      
		                  </td> 
		               </hrms:priv>
		           </logic:equal>
		                  <td align="center" class="RecordRow" style="border-top:none;border-right:none;" style="border-right:none;" nowrap>
		     	         	<logic:notEqual value="leader" name="orgInfoForm" property="leader" >
		     	         	<hrms:priv func_id="2306001">
		                    <a href="javascript:onecopy('${codeitme}','${codesetid }');">复制</a>
		     	         	&nbsp;&nbsp;
		     	         	</hrms:priv>
		     	         	<hrms:priv func_id="2306002">
		     	         	<a href="javascript:showOrgContext('${codeitme}');editorg();">编辑</a>
		     	         	&nbsp;&nbsp;
		     	         	</hrms:priv>
		     	         	<hrms:priv func_id="2306003">
		     	         	<a href="javascript:showOrgContext('${codeitme}');delorg('${codeitme}','<%=i%>')">删除</a>
		     	         	&nbsp;&nbsp;
		     	         	</hrms:priv>
		     	         	<hrms:priv func_id="2306004">
		     	         	<a href="javascript:onebolish(<%=i%>);">撤销</a>
		     	         	&nbsp;&nbsp;
		     	         	</hrms:priv>
		     	         	</logic:notEqual>
		     	         	<hrms:priv func_id="2306005,23154">
		     	         	<a href="javascript:showOrgContext('${codeitme}');sacnorg();">浏览</a>
		     	         	</hrms:priv>
		                  </td> 
                    </tr>
                  </hrms:paginationdb>
               </table>
               
               </div>
                 <%--tablebuilder翻页按钮消失 height调高 wangbs 20190314 --%>
                 <%--<div  style="height:0px; border:0;" class="fixedDiv2 ">--%>
                 <div  style="height:50px; border:0;" class="fixedDiv2 ">
               <table class="RecordRowP" width="100%" border="0" cellspacing="0" cellpadding="0">
			     <tr>
			      <td class="tdFontcolor">
			           <p align="left">
			           		<hrms:paginationtag name="orgInfoForm" pagerows="${orgInfoForm.pagerows}" property="pagination" scope="page" refresh="true">
			           		</hrms:paginationtag>
					  </p>
				  </td>
		               <td  align="right" nowrap class="tdFontcolor">
			           <p align="right">
				            <hrms:paginationdblink name="orgInfoForm" property="pagination" nameId="orgInfoForm" scope="page">
						    </hrms:paginationdblink>
					  </p>
				   </td>
			     </tr>
	            </table>
               </div>
             </td>
          </tr>
         <tr height="35">
          <td nowrap="nowrap">
          	<logic:notEqual value="leader" name="orgInfoForm" property="leader" >
            <hrms:priv func_id="2306000">
             <input type="button" name="addbutton"  value="新建组织单元" class="mybutton" onclick='newduty();' >
            </hrms:priv> 
            <hrms:priv func_id="2306001">
              <input type="button" name="addbutton"  value="复制所选组织单元" class="mybutton" onclick='copyduty();' > 
            </hrms:priv> 
            <hrms:priv func_id="2306009">
              <input type="button" name="addbutton"  value="合并所选组织单元" class="mybutton" onclick='combine();' >
            </hrms:priv> 
            <hrms:priv func_id="2306006">
              <input type="button" name="addbutton"  value="划转所选组织单元" class="mybutton" onclick='transfer();' >
            </hrms:priv> 
            <hrms:priv func_id="2306004">
               <input type="button" name="addbutton"  value="撤销所选组织单元" class="mybutton" onclick='bolish();' >
            </hrms:priv>    
            <hrms:priv func_id="2306003">   
              <input type="button" name="addbutton"  value="删除所选组织单元" class="mybutton" onclick='deleterec();' >
            </hrms:priv>
            </logic:notEqual>
            <logic:equal name="orgInfoForm" property="returnvalue1" value="dxt">
               <input type="button" name="returnbutton"  value="返回" class="mybutton" onclick="hrbreturn('org','2','orgInfoForm');"> 
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
var value_s="";
var newid_s="";
var codelength = "";
var index = "";
<%
	OrgInfoForm oif = (OrgInfoForm)request.getSession().getAttribute("orgInfoForm");
	ArrayList codelist = oif.getCodelist();
	ArrayList newidlist = oif.getNewidlist();
	if(codelist!=null&&codelist.size()>0){
	%>
		codelength = "<%=codelist.size()-1%>";
		<%
			for(int y=0;y<codelist.size();y++){
		%>
				value_s="<%=codelist.get(y)%>";
				index = "<%=y%>";
		<%
				if(newidlist!=null&&newidlist.size()>0){
		%>
					newid_s = "<%=newidlist.get(y)%>";
				<%}%>
	  		//deleter('<bean:write name="orgInfoForm" property="isrefresh" filter="true"/>',value_s,'<bean:write name="orgInfoForm" property="transfercodeitemidall" filter="true"/>','<bean:write name="orgInfoForm" property="issuperuser" filter="true"/>','<bean:write name="orgInfoForm" property="manageprive" filter="true"/>',newid_s);
			deleter('<bean:write name="orgInfoForm" property="isrefresh" filter="true"/>',value_s,'<bean:write name="orgInfoForm" property="transfercodeitemidall" filter="true"/>','<bean:write name="orgInfoForm" property="issuperuser" filter="true"/>','<bean:write name="orgInfoForm" property="manageprive" filter="true"/>',newid_s,'<bean:write name="orgInfoForm" property="firstNodeCode" filter="true"/>','<bean:write name="orgInfoForm" property="combinetext" filter="true"/>',codelength,index,'<bean:write name="orgInfoForm" property="isnewcombineorg" filter="true"/>');
<%
		}
		
		if(codelist!=null)
			codelist.clear();
		if(newidlist!=null)
			newidlist.clear();
		}
	%>  	
</script>
<%
	oif.setIsrefresh("");
%>
<div id='wait1' style='position:absolute;top:250px;left:80px;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style common_background_color" height=24>
					正在刷新数据字典，请稍候...
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
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
    <%--设置蒙版透明度opacity: 0;兼容非ie浏览器  wangbs 20190315--%>
	<div id="ly" style="position:absolute;top:0px;FILTER:alpha(opacity=0);opacity: 0;background-color:#FFF;z-index:2;left:0px;display:none;"></div>
	<div id='wait2' style='position:absolute;top:250px;left:80px;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style common_background_color" height=24>
					操作中，请稍候...
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
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
	
<script>
	//if(!getBrowseVersion() || getBrowseVersion() == 10){//非ie兼容模式样式修改  wangb 20190321
	//全部浏览器样式修改 
	var databox = document.getElementById('databox');
	//var table = databox.getElementsByTagName('table')[0];
	//table.style.tableLayout='fixed';
	databox.style.height = window.screen.height-400+'px';
	//}

</script>