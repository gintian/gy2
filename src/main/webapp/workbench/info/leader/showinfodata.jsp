<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.selfinfomation.SelfInfoForm" %>
<%// 在标题栏显示当前用户和日期 2004-5-10 
			String userName = null;
			String css_url = "/css/css1.css";
			UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			int status=userView.getStatus();
			String manager=userView.getManagePrivCodeValue();
			String way=request.getParameter("way");
			way=way==null?"":way;
%>
<hrms:themes></hrms:themes>
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
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<script language="javascript" src="/module/utils/js/template.js"></script>
<html:form action="/workbench/info/leader/showinfodata">
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
	var dbvalue = '${selfInfoForm.dbvalue }';
   
   function deleterec()
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
      if(confirm("<bean:message key="workbench.info.isdelete"/>?"))
      {
        selfInfoForm.action="/workbench/info/leader/showinfodata.do?b_delete=del&flag=del";
        selfInfoForm.submit(); 
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
   var o_obj=document.getElementById('a0100');   
   if(o_obj)
     o_obj.value=a0100;
   <logic:equal value="1" name="selfInfoForm" property="isAdvance">
   selfInfoForm.action="/workbench/info/addselfinfo.do?b_add=add&i9999=I9999&actiontype=update&setname=A01&flag=notself&returnvalue=2&isAdvance=1";
   </logic:equal>
   <logic:notEqual value="1" name="selfInfoForm" property="isAdvance">
   selfInfoForm.action="/workbench/info/addselfinfo.do?b_add=add&i9999=I9999&actiontype=update&setname=A01&flag=notself&returnvalue=2";
   </logic:notEqual>
   selfInfoForm.target=target;
   selfInfoForm.submit();
}
document.oncontextmenu = function() 
{ 
      //return　false; 
} ;
function searchinfo(query)
{
      selfInfoForm.action="/workbench/info/showinfodata.do?b_searchinfo=link&code=${selfInfoForm.code}&kind=${selfInfoForm.kind}&query=1&isAdvance=0";
      selfInfoForm.submit();
}
function showOrClose()
{
		var obj=document.getElementById("aa");
	    var obj3=document.getElementById("vieworhidd");
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
      if(vo)
         vo.value="1";
   }else
   {
      var vo=document.getElementById(hiddname);
      if(vo)
         vo.value="0";
   }

}
function change()
   {
      selfInfoForm.action="/workbench/info/leader/showinfodata.do?b_leader=link";
			selfInfoForm.submit();
   }

function batchInOut(){
	 /*var thecodeurl="/workbench/info/showinfodata.do?b_batchinout=link";
       var  return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:500px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:yes");
      if(return_vo!=null){
      	
      } */
      selfInfoForm.action="/workbench/info/showinfodata.do?b_batchinout=link";
      selfInfoForm.submit();
}

function batchHand(flag,a_code,dbname,viewsearch){
	//alert(a_code+" "+dbname+"  "+viewsearch);
	var thecodeurl =""; 
	var return_vo=null;	
	var setname = "A01";
	switch(flag){ 
         case 2 : //批量修改
          	  var strId = "";
              //thecodeurl="/general/inform/emp/batch/alertmoreind.do?b_query=link&setname="+setname+"&a_code="+a_code+"&dbname="+dbname+"&viewsearch="+viewsearch+"&infor=1&strid="+strId+"&path=2";
              thecodeurl="/general/inform/emp/batch/alertmoreind.do?b_query=link`setname="+setname+"`a_code="+a_code+"`dbname="+dbname+"`viewsearch="+viewsearch+"`infor=1&strid="+strId+"`path=2";
              //window.open(thecodeurl,"_blank",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=520,height=500');
			  var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
     		  return_vo= window.showModalDialog(iframe_url,1, 
        	  "dialogWidth:520px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
			  break ; 
         case 5 : //计算
              thecodeurl="/general/inform/emp/batch/calculation.do?b_query=link`unit_type=2`setname="+setname+"`a_code="+a_code+"`dbname="+dbname+"`viewsearch="+viewsearch+"`infor=1";
              var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
              return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:410px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");
              break ; 
         default:
         	thecodeurl="";
    } 
    if(thecodeurl.length<1){
    	return;
    }
    if(return_vo!=null){
  	 	selfInfoForm.action = "/workbench/info/showinfodata.do?b_search=link";
		selfInfoForm.submit();   
  	}else{
  		return ;
  	}
}
function insertInfo(a0000)
{
  if(a0000=="")
  {
    alert("没有得到插入序号！");
    return false;
  }else
  {
     selfInfoForm.action ="/workbench/info/addselfinfo.do?b_add=add&a0100=A0100&i9999="+a0000+"&actiontype=new&insert=1&setname=A01&tolastpageflag=yes&orgparentcode=${selfInfoForm.orgparentcode}&deptparentcode=${selfInfoForm.deptparentcode}&posparentcode=${selfInfoForm.posparentcode}&returnvalue=2";
     selfInfoForm.target='nil_body';
     selfInfoForm.submit();  
  }
  
}

function select_org_emp_dialog_dbvalue(flag,selecttype,dbtype,priv,isfilter,loadtype,dbvalue)
{
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_employ_tree.do?flag="+flag+"`showDb=1`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype + "`dbvalue="+dbvalue;
      var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);  
      var dw=320,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
      /* 
     var return_vo= window.showModalDialog(iframe_url,1, 
    		 "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
	 */
	 //改用ext 弹窗显示  wangb 20190318
     var win = Ext.create('Ext.window.Window',{
		id:'select_org_emp',
		title:'选择机构',
		width:dw,
		height:dh+40,
		resizable:'no',
		modal:true,
		autoScoll:false,
		autoShow:true,
		autoDestroy:true,
		html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
		renderTo:Ext.getBody()
	 });  
	 
}
//ext 弹窗回调方法  wangb 20190318
function openReturnValue(return_vo){
	if(return_vo==null){
		return;
	}
	var a0100 = return_vo.content;
	//alert(a0100);
	if(a0100.length<11){
		return;
	}
	addleaderpearson(a0100);
}
function addleader(){
	//var return_vo=select_org_emp_dialog(1,2,0); 
//	var return_vo=select_org_emp_dialog_dbvalue("1","1","0","1","0","1",dbvalue);
	select_org_emp_dialog_dbvalue("1","1","0","1","0","1",dbvalue);
	/*
	if(return_vo==null){
		return;
	}
	var a0100 = return_vo.content;
	//alert(a0100);
	if(a0100.length<11){
		return;
	}
	addleaderpearson(a0100);
	*/
}
function addleaderpearson(a0100){
	var b0110='${selfInfoForm.b0110 }';
	var i9999='${selfInfoForm.i9999 }';
	var emp_e='${selfInfoForm.emp_e }';
	var link_field='${selfInfoForm.link_field }';
	var hashvo=new ParameterSet();
	hashvo.setValue("flag",'add');
	hashvo.setValue("a0100",a0100);
	hashvo.setValue("b0110",b0110);
	hashvo.setValue("i9999",i9999);
	hashvo.setValue("emp_e",emp_e);
	hashvo.setValue("link_field",link_field);
	hashvo.setValue("b0110field",'${selfInfoForm.b0110field }');
	hashvo.setValue("orderbyfield",'${selfInfoForm.orderbyfield }');
	var request=new Request({asynchronous:false,onSuccess:addsuccess,functionId:'0201001054'},hashvo); 
	function addsuccess(outparamters){
		var msg=outparamters.getValue("msg");
		if('ok'==msg){
			selfInfoForm.action="/workbench/info/leader/showinfodata.do?b_leader=link";
			selfInfoForm.submit();
		}else if("error"!=msg){
			alert(getDecodeStr(msg));
		}
	}       
}

function returnback(){
	turn();
	window.location.href = "/workbench/orginfo/editorginfodata.do?b_search=link";
	//此页面的 分页标签  生成的 <input name="pagerows"> 会将pagerows值提交到要返回的Form中，
	//会破坏要返回的form数据，故将pagerows的name 改为notsubmit（随便）
//	var pagerowsObject= document.getElementsByName("pagerows")[0];
//	pagerowsObject.name="notsubmit";
//	selfInfoForm.action ="/workbench/orginfo/editorginfodata.do?b_search=link";
//    selfInfoForm.target='nil_body';
//    turn();
//    selfInfoForm.submit(); 
}
function turn()
{
   parent.menupnl.toggleCollapse(false);
}
   function edit(url)
{
   if(url=="")
      return false;
   selfInfoForm.action=url;
   selfInfoForm.target="nil_body";
   selfInfoForm.submit();
} 

function upItem(rowid,nbase,order){
	var _table=$("tableid");
	var _rowid=parseInt(rowid);
	if(_table.rows.length<3||_rowid<1){
		return;
	}
	var hashvo=new ParameterSet();  
	hashvo.setValue("rowid", _rowid);      
    hashvo.setValue("nbase",nbase);
    hashvo.setValue("order", order);
    hashvo.setValue("link_field", '${selfInfoForm.link_field}');
    hashvo.setValue("i9999", '${selfInfoForm.i9999}');
    hashvo.setValue("b0110field", '${selfInfoForm.b0110field}');
    hashvo.setValue("orderbyfield", '${selfInfoForm.orderbyfield}');
    hashvo.setValue("b0110", '${selfInfoForm.b0110}');
    hashvo.setValue("emp_e", '${selfInfoForm.emp_e}');
    hashvo.setValue("type", 'up');
    var request=new Request({method:'post',onSuccess:upItemview,functionId:'0201001056'},hashvo);
}

function upItemview(outparamters){
	/*var rowid=parseInt(outparamters.getValue("rowid"));
	var _rowid=rowid;
	var _table=$("tableid");
	var _row1=_table.rows[rowid];
	var _row2=_table.rows[rowid+1];
	var tempclass=_row1.className;
	_row1.className=_row2.className;
	_row2.className=tempclass;
	if(_table.rows.length<3||_rowid<=1){
		var _cell1=_row1.cells[_row1.cells.length-1].innerHTML;
		var _cell2=_row2.cells[_row2.cells.length-1].innerHTML;
		_row1.cells[_row1.cells.length-1].innerHTML=_cell2;
		_row2.cells[_row2.cells.length-1].innerHTML=_cell1;
	}else{
		var _cell1=_row1.cells[_row1.cells.length-1].innerHTML;
		var _cell2=_row2.cells[_row2.cells.length-1].innerHTML;
		_row1.cells[_row1.cells.length-1].innerHTML=_cell2;
		_row2.cells[_row2.cells.length-1].innerHTML=_cell1;
	}
	_table.moveRow(_rowid+1,_rowid);*/
	//selfInfoForm.action="/workbench/info/leader/showinfodata.do?b_leader=link";
	selfInfoForm.submit();
}
function downItem(rowid,nbase,order){
	var _table=$("tableid");
	var _rowid=parseInt(rowid);
	if(_table.rows.length<3||(_rowid+2)==_table.rows.length){
		return;
	}
	var hashvo=new ParameterSet();          
   	hashvo.setValue("rowid", _rowid);          
    hashvo.setValue("nbase",nbase);
    hashvo.setValue("order", order);
    hashvo.setValue("link_field", '${selfInfoForm.link_field}');
    hashvo.setValue("i9999", '${selfInfoForm.i9999}');
    hashvo.setValue("b0110field", '${selfInfoForm.b0110field}');
    hashvo.setValue("orderbyfield", '${selfInfoForm.orderbyfield}');
    hashvo.setValue("b0110", '${selfInfoForm.b0110}');
    hashvo.setValue("emp_e", '${selfInfoForm.emp_e}');
    hashvo.setValue("type", 'down');
    var request=new Request({method:'post',onSuccess:downItemview,functionId:'0201001056'},hashvo);
}

function downItemview(outparamters){
	/*var rowid=parseInt(outparamters.getValue("rowid"));
	var _rowid=rowid;
	var _table=$("tableid");
	var _row1=_table.rows[rowid+1];
	var _row2=_table.rows[rowid+2];
	var tempclass=_row1.className;
	_row1.className=_row2.className;
	_row2.className=tempclass;
	var _cell1=_row1.cells[_row1.cells.length-1].innerHTML;
	var _cell2=_row2.cells[_row2.cells.length-1].innerHTML;
	_row1.cells[_row1.cells.length-1].innerHTML=_cell2;
	_row2.cells[_row2.cells.length-1].innerHTML=_cell1;
	_table.moveRow(_rowid+1,_rowid+2);*/
	//selfInfoForm.action="/workbench/info/leader/showinfodata.do?b_leader=link";
	selfInfoForm.submit();
}
   function winhrefOT(dbpre,a0100,target)
{
   if(a0100=="")
     return false;
     	clearallcheck()
	   var returnvalue="189";     
	   selfInfoForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase="+$URL.encode(dbpre)+"&a0100="+$URL.encode(a0100)+"&flag=notself&returnvalue="+$URL.encode(returnvalue);
	   selfInfoForm.target=target;
	   //turn();
	   selfInfoForm.submit();
   
}

function clearallcheck(){
	var checkobjs=document.getElementsByTagName('input');
	for(var i=0;i<checkobjs.length;i++){
		if(checkobjs[i].type=='checkbox'){
			checkobjs[i].checked=false;
		}
	}

}

function searchInform(){
	var thecodeurl =""; 
	var return_vo;	
    thecodeurl="/general/inform/search/gmsearcher.do?b_querysearch=link`type=1`a_code=UN`tablename=Usr`checkflag=3";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
    /*
    return_vo= window.showModalDialog(iframe_url, "", 
    "dialogWidth:750px; dialogHeight:470px;resizable:no;center:yes;scroll:no;status:no");
    if(typeof(return_vo)=="undefined")
        return;
    addleaderpearson(return_vo);
    */
	//改用ext 弹窗显示  wangb 20190318
    var win = Ext.create('Ext.window.Window',{
		id:'simple_query',
		title:'条件分析',
		width:750,
		height:480,
		resizable:'no',
		modal:true,
		autoScoll:false,
		autoShow:true,
		autoDestroy:true,
		html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
		renderTo:Ext.getBody(),
		listeners:{
			'close':function(){
				if(typeof(this.return_vo)=="undefined")
        			return;
    			addleaderpearson(this.return_vo);
			}
    	}
    });  
	
}
function changeNext(obj){
	leadNext = "0";
	if(obj.checked)
		leadNext = "1";
	selfInfoForm.action="/workbench/info/leader/showinfodata.do?b_leader=link&leadNext="+leadNext;
	selfInfoForm.submit();
}


var analyserText="";
function analyserLeader(){
	var analyserShow = document.getElementById("analyserShow");
	var showButton = document.getElementById("showButton");
	var display = analyserShow.style.display;
	if(display == 'block'){
		showButton.innerHTML = "[显示]";
		analyserShow.style.display = 'none';
	}else{
		showButton.innerHTML = "[隐藏]";
		analyserShow.style.display = 'block';
		
		if(analyserText.length<1){
			//document.getElementById("wait").style.display='block';
			//setTimeout('analyserData()',1000);
			jindu();
			analyserData();
		   
		}
		
		document.getElementById("analyserPanel").innerHTML=analyserText;
		
	}
}

function analyserData(){
	
	
	var hashvo=new ParameterSet(); 
    hashvo.setValue("b0110", '${selfInfoForm.b0110}');
    hashvo.setValue("i9999", '${selfInfoForm.i9999}');
    hashvo.setValue("leadNext", '${selfInfoForm.leadNext}');
    hashvo.setValue("leaderTypeValue", '${selfInfoForm.leaderTypeValue}');
    hashvo.setValue("sessionValue", '${selfInfoForm.sessionValue}');
    var request=new Request({method:'post',asynchronous:'true',onSuccess:getData,functionId:'0201001063'},hashvo);
}

function getData(out){
	analyserText = out.getValue("anaRs");
	document.getElementById("analyserPanel").innerHTML=analyserText;
	closejindu();
}

function exportExcel(){
	
	var url="/workbench/info/leader/showinfodata.do?b_batchinout=link";
	 var iframe_url="/general/query/common/iframe_query.jsp?src="+url;
	/*
	var return_vo = window.showModalDialog(iframe_url, "", 
    "dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
    if("undefined"==typeof(return_vo)){
		return;
    }
    var hashvo=new ParameterSet(); 
    hashvo.setValue("b0110", '${selfInfoForm.b0110}');
    hashvo.setValue("i9999", '${selfInfoForm.i9999}');
    hashvo.setValue("leadNext", '${selfInfoForm.leadNext}');
    hashvo.setValue("leaderTypeValue", '${selfInfoForm.leaderTypeValue}');
    hashvo.setValue("sessionValue", '${selfInfoForm.sessionValue}');
    hashvo.setValue("displayFields", return_vo);
    var request=new Request({method:'post',onSuccess:showFile,functionId:'0201001060'},hashvo);
    */
    //改用ext 弹窗显示  wangb 20190318
    var win = Ext.create('Ext.window.Window',{
		id:'importExcel',
		title:'导出Excel',
		width:500,
		height:410,
		resizable:'no',
		modal:true,
		autoScoll:false,
		autoShow:true,
		autoDestroy:true,
		html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
		renderTo:Ext.getBody(),
		listeners:{
			'close':function(){
				if("undefined"==typeof(this.return_vo)){
					return;
    			}
    			var hashvo=new ParameterSet(); 
    			hashvo.setValue("b0110", '${selfInfoForm.b0110}');
    			hashvo.setValue("i9999", '${selfInfoForm.i9999}');
    			hashvo.setValue("leadNext", '${selfInfoForm.leadNext}');
    			hashvo.setValue("leaderTypeValue", '${selfInfoForm.leaderTypeValue}');
    			hashvo.setValue("sessionValue", '${selfInfoForm.sessionValue}');
    			hashvo.setValue("displayFields", this.return_vo);
    			var request=new Request({method:'post',onSuccess:showFile,functionId:'0201001060'},hashvo);
			}
		}
    });
}

function showFile(out){
	var outName=out.getValue("fileName");
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"excel");
}

function jindu(){
	var waitInfo=document.getElementById("wait");
	waitInfo.style.display="block";
}

function closejindu(){
	var waitInfo=document.getElementById("wait");
	waitInfo.style.display="none";
}
</script>
<%int i=0;%>
 <table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:5px;">
 <tr>
   <td align="left" >
     <table border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td align="left"  nowrap>
     	     
              <logic:notEmpty  name="selfInfoForm" property="b0110">
	          <bean:message key="system.browse.info.currentorg"/>:
	          <hrms:codetoname codeid="UN" name="selfInfoForm" codevalue="b0110" codeitem="codeitem" scope="session" uplevel="5"/>  	      
          	  <bean:write name="codeitem" property="codename" />
          	  <hrms:codetoname codeid="UM" name="selfInfoForm" codevalue="b0110" codeitem="codeitem" scope="session" uplevel="5"/>  	      
          	  <bean:write name="codeitem" property="codename" />
          	  <hrms:codetoname codeid="@K" name="selfInfoForm" codevalue="b0110" codeitem="codeitem" scope="session" uplevel="5"/>  	      
          	  <bean:write name="codeitem" property="codename" />
          	  &nbsp;&nbsp;
	         </logic:notEmpty>
	         
	           &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	         <html:checkbox property="leadNext" value="1" onclick="changeNext(this)"></html:checkbox>包含下级组织单元班子成员
	         <!-- 
	         <html:hidden name="selfInfoForm" property='orglike' styleClass="text"/> 
                 <logic:equal name="selfInfoForm" property="orglike" value="1">
                     <input type="checkbox" name="orglike2" value="true" onclick="selectCheckBox(this,'orglike');change();" checked>
                 </logic:equal>
                 <logic:notEqual name="selfInfoForm" property="orglike" value="1">
                     <input type="checkbox" name="orglike2" value="true" onclick="selectCheckBox(this,'orglike');change();">
                 </logic:notEqual>                 
                 <bean:message key="system.browse.info.viewallpeople"/>
                 
              -->
	    </td>       
       </tr>
     </table>
   </td>
 </tr>

   <td nowrap>
<%
	
	int flag=0;
	int j=0;
	int n=0;
%>
 <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id='aa' style='display:'>

 <tr>
   <td width="100%" nowrap>
     <div class="fixedDiv2" style="height:307px;border-top:0px;width:expression(document.body.clientWidth-30);"> 
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id="tableid" style="border-collapse: collapse;border-left:0px;">
           <tr class="">
             <td align="center" class="TableRow" style="border-left:0px;" width="30" nowrap>
              <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
             </td>
             
          <logic:iterate id="info"    name="selfInfoForm"  property="browsefields"> 
          	<logic:equal value="b0110" name="info" property="itemid">
          		<td align="center" class="TableRow" width="150" nowrap>
                   <bean:write  name="info" property="itemdesc" filter="true"/>&nbsp;
              </td>
          	</logic:equal> 
          	<logic:equal value="a0101" name="info" property="itemid">
          		<td align="center" class="TableRow" width="100" nowrap>
                   <bean:write  name="info" property="itemdesc" filter="true"/>&nbsp;
              </td>
          	</logic:equal>   
          	<logic:notEqual value="b0110" name="info" property="itemid">
          	<logic:notEqual value="a0101" name="info" property="itemid">
              <td align="center" class="TableRow" style="border-right:0px;" nowrap>
                   <bean:write  name="info" property="itemdesc" filter="true"/>&nbsp;
              </td>
            </logic:notEqual>
            </logic:notEqual>
             </logic:iterate>  
             <logic:notEqual value="1" name="selfInfoForm" property="leadNext">
             <hrms:priv func_id="2315212">  
                 <td align="center" class="TableRow" width="50" nowrap>编辑</td>
             </hrms:priv>
             <hrms:priv func_id="2315214"> 
             
             <td align="center" class="TableRow" style="border-right:0px;" width="50" nowrap> 排序</td>  
             </hrms:priv>	
             </logic:notEqual>    	    	    		        	        	        
           </tr>
			 <%
           		SelfInfoForm selfInfoForm=(SelfInfoForm)session.getAttribute("selfInfoForm");
           		int len = selfInfoForm.getLen();
            %>
          <hrms:paginationdb id="element" name="selfInfoForm" sql_str="selfInfoForm.strsql" table="" where_str="selfInfoForm.cond_str" columns="selfInfoForm.columns" order_by="selfInfoForm.order_by" page_id="pagination" pagerows="${selfInfoForm.pagerows}" keys="" indexes="indexes" >
          
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'')">
          <%}
          else
          {%>
          <tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'E4F2FC')">
          <%
          }
          i++;          
          %>  
            <td align="center" class="RecordRow" style="border-left:0px;" width="30" nowrap>
               <hrms:checkmultibox name="selfInfoForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
            </td> 
            <bean:define id="a0100" name="element" property="a0100"/>
            <logic:notEqual value="1" name="selfInfoForm" property="leadNext">
             <bean:define id="i9999" name="element" property="i9999"/>
             <bean:define id="order" name="element" property="${selfInfoForm.orderbyfield }"/>
            </logic:notEqual>
             <bean:define id="dbpre" name="element" property="dbpre"/>
             
	         <logic:iterate id="info"    name="selfInfoForm"  property="browsefields">  	
	            
	               <logic:equal  name="info" property="itemid" value="b0110">               
                        <bean:define id="b0110value" name="element" property="b0110"/>   
                  </logic:equal>  
                  <logic:equal  name="info" property="itemid" value="a0101">  
                  	<td align="left" class="RecordRow" nowrap>             
                        <bean:define id="a0101value" name="element" property="a0101"/> 
                  		&nbsp; <a href='###' onclick="winhrefOT('${dbpre }','${a0100}','nil_body');" >${a0101value }</a>&nbsp;
                  	</td>
                  </logic:equal> 
                  <logic:notEqual  name="info" property="itemid" value="a0101">        
	                  <logic:notEqual  name="info" property="itemtype" value="N">               
	                    <td align="left" class="RecordRow" style="border-right:0px;" nowrap>        
	                  </logic:notEqual>
	                  <logic:equal  name="info" property="itemtype" value="N">               
	                    <td align="right" class="RecordRow" nowrap>        
	                  </logic:equal>    
	                  <logic:equal  name="info" property="codesetid" value="0">   
	                 
	                    &nbsp; <bean:write  name="element" property="${info.itemid}" filter="true"/>&nbsp;
	                  </logic:equal>
	                 <logic:notEqual  name="info" property="codesetid" value="0">  
		                 <logic:equal name="info" property="codesetid" value="UM">
		                     <hrms:codetoname codeid="UM" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="0"/>  	      
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
              </logic:notEqual>
             </logic:iterate> 
             <logic:notEqual value="1" name="selfInfoForm" property="leadNext">
             <hrms:priv func_id="2315212">  
              <td align="center" class="RecordRow" width="50" nowrap>
              	<a href="javascript:edit('/workbench/info/leader/showinfodata.do?b_update=edit&a0100=${a0100}&pi9999=${i9999 }&actiontype=update&dbpre=${dbpre }&a0101value='+$URL.encode(getEncodeStr('${a0101value }'))+'&b0110value='+getEncodeStr('${b0110value }') );">编辑</a>    	
              </td> 
              </hrms:priv> 
              <hrms:priv func_id="2315214">  
              
              
               	 <td align="center" class="RecordRow" style="border-right:0px;" width="50" nowrap>
                 	<%if(i!=1){ %>
					<a href="javaScript:upItem('${indexes }','${dbpre }','${order }')">
					<img src="/images/up01.gif" width="12" height="17" border=0></a> 
					<%}else{ %>
						&nbsp;&nbsp;
					<%} %>
				    <%if(len==i){%>
				    	&nbsp;&nbsp;
				    <%}else{ %>
					<a href="javaScript:downItem('${indexes }','${dbpre }','${order }')">
					<img src="/images/down01.gif" width="12" height="17" border=0></a> 
					<%} %>
				</td> 
			  
			</hrms:priv>	
			</logic:notEqual>     	    		        	        	        
          </tr>
        </hrms:paginationdb>
        
</table>
</div>
<div style="width:expression(document.body.clientWidth-30);">
	<table width="100%"  align="center" class="RecordRowP">
			<tr>
			    <td valign="bottom" class="tdFontcolor">
			            	<hrms:paginationtag name="selfInfoForm"
									pagerows="${selfInfoForm.pagerows}" property="pagination"
									scope="page" refresh="true"></hrms:paginationtag>
				</td>
		               <td  align="right" nowrap class="tdFontcolor">
			          <p align="right"><hrms:paginationdblink name="selfInfoForm" property="pagination" nameId="selfInfoForm" scope="page">
					</hrms:paginationdblink>
				</td>
			</tr>
	</table>
</div>



<table  align="center">
          <tr>
            <td align="left">
              <logic:notEqual value="1" name="selfInfoForm" property="leadNext">
		                  <hrms:priv func_id="2315211" module_id="">
		               <%
								String emp_e=(String)selfInfoForm.getEmp_e();
							if(userView.analyseTablePriv(emp_e).equals("2")){ %>	
		                 <input type="button" name="addbutton"  value="<bean:message key="button.insert"/>" class="mybutton" onclick="addleader();"> 
		                 <input type="button" name="addbutton"  value="<bean:message key="train.examstudent.selbycond"/>" class="mybutton" onclick="searchInform();">   
		               	<%} %>
		               </hrms:priv>
		                
		              <hrms:priv func_id="2315213" module_id="">	
		              <%
								String emp_e=(String)selfInfoForm.getEmp_e();
							if(userView.analyseTablePriv(emp_e).equals("2")){ %>
		              	 <input type="button" name="addbutton"  value="<bean:message key="button.delete"/>" class="mybutton" onclick='deleterec()'>  
			 	      <%} %>
			 	      </hrms:priv>
              
              </logic:notEqual>
               
	 	      <input type="button" class="mybutton" value="<bean:message key="edit_report.importexcel"/>" onclick="exportExcel()">
              <input type="button" name="b_retrun" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnback();">

            </td>
          
</table>
   </td>
 </tr>
 
 </tr>  
          <tr>
            <td><strong>领导班子成员分析</strong> <a href="javascript:analyserLeader()" id="showButton">[显示]</a></td>
          
          </tr>        
          <tr id="analyserShow" style="display:none;">
             <td id="analyserPanel">
             </td>
          </tr>
 </table>

</html:form>

<div id='wait' style='position:absolute;top:200;left:35%;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style common_background_color" height=24>
					正在进行成员分析，请稍候...
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
	var form = document.getElementsByName('selfInfoForm')[0];
	var table = form.getElementsByTagName('table')[0];
	table.setAttribute('width','99%');
	var fixedDiv2 = document.getElementsByName('selfInfoForm')[0].getElementsByTagName('table')[2].getElementsByTagName('div')[0];
	fixedDiv2.style.overflowX = 'hidden';
</script>
