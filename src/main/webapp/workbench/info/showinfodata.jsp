<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hjsj.hrms.actionform.selfinfomation.SelfInfoForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.constant.SystemConfig"%>
<%@ page import="java.util.HashMap" %> 
<%@page import="java.util.List,com.hjsj.hrms.utils.PubFunc,com.hrms.hjsj.sys.FieldItem"%>
<%// 在标题栏显示当前用户和日期 2004-5-10 
			String userName = null;
			String css_url = "/css/css1.css";
			UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			int status=userView.getStatus();
			//String manager=userView.getManagePrivCodeValue();
			String manager=userView.getUnitIdByBusi("4");
			String way=request.getParameter("way");
			way=way==null?"":way;
			SelfInfoForm selfInfoForm = (SelfInfoForm)session.getAttribute("selfInfoForm");
			int llen=selfInfoForm.getLlen(); 
			int pagerows = selfInfoForm.getPagerows();
			int fflag=1;
			String webserver=SystemConfig.getPropertyValue("webserver");
			if(webserver.equalsIgnoreCase("websphere"))
				fflag=2;
			String inforquery_extend=SystemConfig.getPropertyValue("inforquery_extend");			
	        HashMap partMap=(HashMap)selfInfoForm.getPart_map();
	        String bosflag="";
		    if(userView!=null){
		     	bosflag = userView.getBosflag();
		    }
		    
		    String unitID = (String)partMap.get("unit");
			String deptID = (String)partMap.get("dept");
			String posID = (String)partMap.get("pos");
%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<hrms:linkExtJs/><!--add by xiegh on date 20180314 由于模板中是ext4  该界面不能在引入ext6-->
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
<script language="javascript" src="/js/constant.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
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
	var webserver=<%=fflag%>;
	
</script>
<style type="text/css">
	.fixedDiv2 {  
	    overflow:auto;
	    height:expression(document.body.clientHeight-150);
	    width:expression(document.body.clientWidth-15); 
	    BORDER-BOTTOM: #99BBE8 1pt solid; 
	    BORDER-LEFT: #99BBE8 1pt solid; 
	    BORDER-RIGHT: #99BBE8 1pt solid; 
	    BORDER-TOP: #99BBE8 1pt solid;
	}
</style>
<!-- 引入ext      wangb 20171117 -->
<!-- <script language="JavaScript" src="/module/utils/js/template.js"></script> -->
<script language='JavaScript' src='/components/codeSelector/codeSelector.js'></script>
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/ajax/basic.js"></SCRIPT>
<html:form action="/workbench/info/showinfodata">
<script language="javascript">
   function change()
   {
      selfInfoForm.action="/workbench/info/showinfodata.do?b_searchinfo=link&code=${selfInfoForm.code}&kind=${selfInfoForm.kind}";
      selfInfoForm.submit();
   }
   function change1()//人员分类
   {
      selfInfoForm.action="/workbench/info/showinfodata.do?b_searchinfo=link&query=1&code=${selfInfoForm.code}&kind=${selfInfoForm.kind}";
      selfInfoForm.submit();
   }
   function exeAdd(addStr)
   {
      // alert(addStr);
       target_url=addStr;
       window.open(target_url,'nil_body'); 
   }
   function deleterec()
   {
       var isCorrect=false;
       var len=document.selfInfoForm.elements.length;
       var i;
       for (i=0;i<len;i++)
       {//tianye 删除时没有选中进行提示，这里原来判断缺少条件只有：document.selfInfoForm.elements[i].type=="checkbox"
          if(document.selfInfoForm.elements[i].type=="checkbox"&&document.selfInfoForm.elements[i].name!='selbox'&&document.selfInfoForm.elements[i].name!='orglike2')
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
        selfInfoForm.action="/workbench/info/showinfodata.do?b_delete=del";
        selfInfoForm.submit(); 
       }
   }
    function moverec()
   {
       var isCorrect=false;
       var len=document.selfInfoForm.elements.length;
       var i;
       for (i=0;i<len;i++)
       {
          if(document.selfInfoForm.elements[i].type=="checkbox"&&document.selfInfoForm.elements[i].name!='selbox'&&document.selfInfoForm.elements[i].name!='orglike2')
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
       var code="${selfInfoForm.code}";
       var kind="${selfInfoForm.kind}";
       var tablename="${selfInfoForm.userbase}";
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
	   //update by xiegh on date20171125 修改自助服务-员工信息-信息维护：浏览器兼容问题         
       var thecodeurl="/general/inform/search/generalsearch.do?b_query=link`type=1`a_code="+a_code+"`tablename="+tablename+"`fieldsetid=A01`callback=closeAction";
       var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
       var dw=770,dh=380,dl=(screen.width-dw)/2;
       var dt=(screen.height-dh)/2;
       var iTop = (window.screen.availHeight - 30 - 460) / 2;  //获得窗口的垂直位置
	   var iLeft = (window.screen.availWidth - 10 - 790) / 2; //获得窗口的水平位置 
       window.open(iframe_url,'_blank','width='+dw+'px,height='+dh+'px;,toolbar=no,location=no,resizable=no,top='+iTop+',left='+iLeft);
   }
   //open弹窗返回值调用方法  wangb 20180206 bug 34583
   function selectReturn(return_vo){
   		if(return_vo!=null){
            var expr= return_vo.expr;
            var factor=return_vo.factor;
            var history=return_vo.history;
            var o_obj=document.getElementById('factor');
            o_obj.value=factor;
            o_obj=document.getElementById('expr');
            o_obj.value=expr;
            o_obj=document.getElementById('history');
            o_obj.value=history;
            document.getElementsByName('likeflag')[0].value=return_vo.likeflag;
            selfInfoForm.action="/workbench/info/showinfodata.do?b_searchinfo=link&code=${selfInfoForm.code}&kind=${selfInfoForm.kind}&check=ok&isAdvance=0&query=1";
            //zgd 2014-1-13 高级查询，在任意页进入某人员信息后，点返回，都跳到第一页；通过修改isAdvance参数为0，返回后就还在先前所在页。
            selfInfoForm.submit();
      }
   }
   
   function closeAction(outparamters){
		var fieldSetId=outparamters.getValue("fieldSetId");
		if(fieldSetId=='A01'){
			document.getElementById('factor').value = outparamters.getValue("sfactor");
			document.getElementById('expr').value = outparamters.getValue("sexpr");
			document.getElementById('likeflag').value =outparamters.getValue("likeflag");
			document.getElementById('history').value = outparamters.getValue("history");
	    }
	   selfInfoForm.action="/workbench/info/showinfodata.do?b_searchinfo=link&code=${selfInfoForm.code}&kind=${selfInfoForm.kind}&check=ok&isAdvance=0&query=1";
	 	//zgd 2014-1-13 高级查询，在任意页进入某人员信息后，点返回，都跳到第一页；通过修改isAdvance参数为0，返回后就还在先前所在页。
       browseForm.submit();
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

function searchinfo(query)
{
      selfInfoForm.action="/workbench/info/showinfodata.do?b_searchinfo=link&code=${selfInfoForm.code}&kind=${selfInfoForm.kind}&query=1&isAdvance=0";
      selfInfoForm.submit();
}
function showOrClose()
{
		var obj=document.getElementById("aa");
	    var obj3=document.getElementById("vieworhidd");
	    if(obj.style.display=='none')
	    {
    		obj.style.display=''
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
      var vo=document.getElementsByName(hiddname)[0];
      if(vo)
         vo.value="1";
         var Info=eval("info_cue1");	
	   Info.style.display="";
   }else
   {
      var Info=eval("info_cue1");	
	  Info.style.display="none";
      var vo=document.getElementsByName(hiddname)[0];
      if(vo)
         vo.value="0";
   }

}

function batchInOut(){
      selfInfoForm.action="/workbench/info/showinfodata.do?b_batchinout=link";
      selfInfoForm.submit();
}
//tianye add
function batchPOMImportBefore(parameter) {
    	selfInfoForm.action= "/workbench/info/showinfodata.do?b_batchPOMimportbefore=link&batchImportType="+parameter;
    	selfInfoForm.submit();
	}
//ty end

function selectValueStr(){
	var str="";
	var len=document.selfInfoForm.elements.length;
    var i;
    for (i=0;i<len;i++)
    {
          if(document.selfInfoForm.elements[i].type=="checkbox"&&document.selfInfoForm.elements[i].name!='selbox'&&document.selfInfoForm.elements[i].name!='orglike2'&&document.selfInfoForm.elements[i].name!='querlike2')
          {
              if(document.selfInfoForm.elements[i].checked==true)
              {
                var index=document.selfInfoForm.elements[i].name.indexOf("select[");
                var end=document.selfInfoForm.elements[i].name.indexOf("]");
                //默认取name，有select[时再进行截取
                var name=document.selfInfoForm.elements[i].name;
                if (index > 0)
                  name="a"+document.selfInfoForm.elements[i].name.substring(index+7,end);
                //批量修改获取被选中的复选框时，获取了表格之外的复选框，所有导致判断修改的总条数时，比选择的数据条数多了 chenxg 2017-01-12
                if(index > -1)
                	str+=document.getElementsByName(name)[0].value+"`";
              }
          }
    }   
	
	 
	return str;
}

function batchHand(flag,a_code,dbname,viewsearch){
	//alert(a_code+" "+dbname+"  "+viewsearch);
	var thecodeurl =""; 
	var return_vo=null;	
	var dialog = [];
	switch(flag){ 
         case 2 : //批量修改
          	  var strId = selectValueStr(dbname+setname);  //20141021 dengcan 记录录入追加批量修改已选记录功能
          	  var arguments=new Array(); 
          	  arguments[0]=strId;
          	  
          	  var setname = "";
			  var dw=520,dh=500,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
			  if(getBrowseVersion()){
				    thecodeurl="/general/inform/emp/batch/alertmoreind.do?b_query=link`setname="+setname+"`a_code="+a_code+"`dbname="+dbname+"`viewsearch="+viewsearch+"`infor=1`path=2`strid="+strId;
				    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
			  		return_vo= window.showModalDialog(iframe_url,arguments, 
        	 	    "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:570px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
			  }else{//非IE浏览器  wangb 20180127
			  	//strid参数中有` 需要特殊处理 update by xiegh on date 20180316 bug35645
				  thecodeurl="/general/inform/emp/batch/alertmoreind.do?b_query=link`setname="+setname+"`a_code="+a_code+"`dbname="+dbname+"`viewsearch="+viewsearch+"`infor=1`path=2`strid="+strId;
				  var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
			  	  dialog.dw=dw;dialog.dh=dh;dialog.iframe_url=iframe_url;
			  	  openWin(dialog,'批量修改');//bug 34790  wangb 20180209
			  }
				  
     		  
			  break ; 
         case 5 : //计算
              var setname = "A01";
         	  var dw=410,dh=420,dl=(screen.width-dw)/2;
         	  var dt=(screen.height-dh)/2;
              thecodeurl="/general/inform/emp/batch/calculation.do?b_query=link`unit_type=2`setname="+setname+"`a_code="+a_code+"`dbname="+dbname+"`viewsearch="+viewsearch+"`infor=1";
              var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
			  dh=450;
              //改用Ext弹窗  wangb 20190307
       		  dialog.dw=dw;dialog.dh=dh;dialog.iframe_url=iframe_url;
		  	  openWin(dialog,'计算公式');
              break ; 
         default:
             var setname = "A01";
         	thecodeurl="";
    } 
    if(thecodeurl.length<1){
    	return;
    }
    if(return_vo!=null){
  	 	selfInfoForm.action = "/workbench/info/showinfodata.do?b_search=update";
		selfInfoForm.submit();   
  	}else{
  		return ;
  	}
}
function winClose(){
	Ext.getCmp('info_date').close();
}
//非IE浏览器 ext。window   wangb 20180127
function openWin(dialog,titles){
		Ext.create("Ext.window.Window",{
		    	id:'info_date',
		    	width:dialog.dw+40,
		    	height:dialog.dh,
		    	title:titles,
		    	resizable:false,
		    	modal:true,
		    	autoScroll:false,
		    	closable:false,
		    	renderTo:Ext.getBody(),
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' scrolling='no'  height='"+dialog.dh+"' width='100%' src='"+dialog.iframe_url+"'></iframe>"
		 }).show();	
}
//非IE浏览器 弹窗调用方法  wangb 20180127
function openReturn(return_vo){
	winClose();
	if(return_vo!=null){
  	 	selfInfoForm.action = "/workbench/info/showinfodata.do?b_search=update";
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
function resetQuery()
{
    var vo=document.getElementById("query");
    var inps=vo.getElementsByTagName("input") ;
    for(i=0;i<inps.length;i++)
    {
      if(inps[i].type=="hidden"||inps[i].type=="text")
        inps[i].value="";
      else if(inps[i].type=="checkbox")      
         inps[i].checked=false;
      
    }   
    var sels=document.getElementsByTagName("select") ;
    for(i=0;i<sels.length;i++)
    {
     sels[i].options[0].selected=true ;
    }
    var o_obj=document.getElementById('factor');
     o_obj.value="";
     o_obj=document.getElementById('expr');          
     o_obj.value="";
     o_obj=document.getElementById('history');
     o_obj.value="";
     searchinfo("");
}

function to_sort_main_info(){
		var thecodeurl ="/gz/sort/sorting.do?b_query=link`flag=r1`mark=zzfw"; 
		var iframe_url = "/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
		var dw=530,dh=420,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    	if(getBrowseVersion()){
    		dw=560;
    		var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
			if(return_vo!=null){
				if(return_vo!='not')
				if(confirm("您将对当前显示人员记录进行排序?")) {
					selfInfoForm.action ="/workbench/info/showinfodata.do?b_sort=link&sortstr="+$URL.encode(return_vo);
     				selfInfoForm.submit();
				}	
			}
    	}else{//人员排序非IE浏览器使用ext弹窗   wangb 20180206 bug 34437
    		var dialog=[];dialog.dw=dw;dialog.dh=dh;dialog.iframe_url=iframe_url;
    		openWin(dialog,'人员排序');
    	}
}
//人员弹窗回调方法   wangb 20180206 bug 34437 
function returnSort(return_vo){
	if(return_vo!=null){
				//alert(return_vo);
				if(return_vo!='not')
				if(confirm("您将对当前显示人员记录进行排序?"))
				{
					//alert(return_vo);
					winClose();
					selfInfoForm.action ="/workbench/info/showinfodata.do?b_sort=link&sortstr="+$URL.encode(return_vo);
     				selfInfoForm.submit();
				}	
	}
}



function upItem(rowid,codeitemid,i9999){
	var _table=$("tableid");
	var _rowid=parseInt(rowid);
	/*if(_table.rows.length<3||_rowid<1){
		return;
	}*/
	var hashvo=new ParameterSet();  
	hashvo.setValue("rowid", _rowid);      
    hashvo.setValue("fieldsetid", '${selfInfoForm.userbase}A01');
    hashvo.setValue("codeitemid", codeitemid);
    hashvo.setValue("i9999", i9999);
    hashvo.setValue("type", 'up');
    var request=new Request({method:'post',onSuccess:upItemview,functionId:'3409000022'},hashvo);
}

function upItemview(outparamters){
	selfInfoForm.submit();
}
function downItem(rowid,codeitemid,i9999){
	var _table=$("tableid");
	var _rowid=parseInt(rowid);
	var hashvo=new ParameterSet();          
   	hashvo.setValue("rowid", _rowid);          
    hashvo.setValue("fieldsetid", '${selfInfoForm.userbase}A01');
    hashvo.setValue("codeitemid", codeitemid);
    hashvo.setValue("i9999", i9999);
    hashvo.setValue("type", 'down');
    var request=new Request({method:'post',onSuccess:downItemview,functionId:'3409000022'},hashvo);
}

function downItemview(outparamters){
	selfInfoForm.submit();
}

function createrandomusername(){	
    if(!confirm("确定要生成随机账号及口令吗?"))
      return false;
	var hashvo=new ParameterSet();  
	hashvo.setValue("action", 	"clew"); 
    var request=new Request({method:'post',onSuccess:viewClewInfo,functionId:'0201001067'},hashvo);
}
function viewClewInfo(outparamters)
{
   var info=outparamters.getValue("info");
   if(info=="")
     return false;
   if(confirm(info))
   {
      var waitInfo=eval("wait");	   
	  waitInfo.style.display="block";
      var usernameFld=outparamters.getValue("usernameFld");
      var passwordFld=outparamters.getValue("passwordFld");
      var hashvo=new ParameterSet();  
	  hashvo.setValue("action", "create"); 
	  hashvo.setValue("nbase","${selfInfoForm.userbase}");     
      hashvo.setValue("usernameFld", usernameFld);
      hashvo.setValue("passwordFld", passwordFld);      
      var request=new Request({method:'post',asynchronous:true,onSuccess:createRandomInfo,functionId:'0201001067'},hashvo);
   }   
}
function createRandomInfo(outparamters)
{
  var waitInfo=eval("wait");	   
  waitInfo.style.display="none";
  var info=outparamters.getValue("info");
  alert(info);  
}
function multimediahref(setprv,dbname,a0100){
	var result=false;
	if(setprv==2){
		result=true;
	}else{
		result=false;
	}
	var thecodeurl =""; 
	var return_vo=null;
	var setname = "A01";
	var dw=800,dh=500,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
  	thecodeurl="/general/inform/multimedia/multimedia_tree.do?b_query=link&setid="+setname+"&a0100="+a0100+"&nbase="+dbname+"&dbflag=A&canedit="+result;
  	thecodeurl = thecodeurl.replace(/&/g,"`");
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
   
  	if(getBrowseVersion()){
  		return_vo= window.showModalDialog(iframe_url, "", 
  		"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:800px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");
  	}else{//非IE浏览器
  		var iTop = (window.screen.availHeight - 30 - dh) / 2;  //获得窗口的垂直位置
		var iLeft = (window.screen.availWidth - 10 - dw) / 2; //获得窗口的水平位置 
		window.open(iframe_url,"","width="+dw+",height="+dh+",resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
  	}
}

function batchDeleteSubsetData(dbname) {
	var strId = selectValueStr();
	if(strId)
		strId = strId.replace(/`/g, ',')
		
    var arguments=new Array(); 
    arguments[0]=strId;
	var dw=400,dh=300,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
    var thecodeurl="/workbench/info/showinfodata.do?b_DeleteSubsetDataSet=link`strId=" + strId;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
    
    if(getBrowseVersion()){
        return_vo= window.showModalDialog(iframe_url, arguments, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:450px; dialogHeight:280px;resizable:no;center:yes;scroll:no;status:no");
        if("true" == return_vo)
        	window.location.reload();
    }else{//非IE浏览器
        window.open(iframe_url,'_blank','width=450,height=250,toolbar=no,location=yes,resizable=no,top='+dt+',left='+dl);
    }
}

function refreshPage(return_vo){
    if(!return_vo)
    	return false;
    
    selfInfoForm.action = "/workbench/info/showinfodata.do?b_search=update";
    selfInfoForm.submit();   
}

window.onresize = function(){
	setDivStyle();
}

function setDivStyle(){
	document.getElementById("dataBox").style.height = (document.body.clientHeight-150) + "px";
    document.getElementById("dataBox").style.width = (document.body.clientWidth-30) + "px"; 
    document.getElementById("bottomid").style.width = (document.body.clientWidth-30) + "px"; 
}

function checkDate(obj){
    var radio = document.getElementById("day");
    if(radio && radio.checked) {
        if(!obj.value){
            return true;
        }
        
        var checkFlag = checkDateTime(obj.value);
        if(!checkFlag) {
            obj.value="";
            obj.focus();
            alert(INPUT_FORMAT_DATE);
            return false;
        }
    }
}

function checkDates(itemid){
    var radio = document.getElementById("day");
    if(radio && radio.checked) {
        var flag = checkDate(document.getElementById(itemid + "S"));
        if(!flag){
            return false;
        }
        
        flag = checkDate(document.getElementById(itemid + "E"));
        if(!flag){
            return false;
        }
    }
}
</script>
<%int i=0;%>
<hrms:themes />
<style>
.TableRow{
	border-top: #C4D8EE 0pt solid;
}
<%if("hcm".equals(bosflag)){%>
.partdescdiv{
	margin-top:-5px;
}
<%}%>
.x-btn-button {
    margin-top: -1px!important;
    margin-top: 0px\9;
    padding-top: 3px\9;
}
</style>
<!--zgd 2014-7-9 信息列表中岗位中有兼职情况的特殊处理。partdescdiv在ParttimeTag中写入-->
 <table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0">
 <html:hidden name="selfInfoForm" property="factor" styleId="factor" styleClass="text"/>
<html:hidden name="selfInfoForm" property="expr" styleId="expr" styleClass="text"/> 
<html:hidden name="selfInfoForm" property="history" styleId="history" styleClass="text"/>   
<html:hidden name="selfInfoForm" property="orgparentcode" />
<html:hidden name="selfInfoForm" property="deptparentcode" />
<html:hidden name="selfInfoForm" property="posparentcode" />
<html:hidden name="selfInfoForm" property="likeflag" styleId="likeflag" />
 <input type="hidden" name="a0100" id="a0100">
 <tr>
   <td aling="left" >
     <table id="query_table2" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td align="left"  nowrap>
     	     
              <logic:notEmpty  name="selfInfoForm" property="code">
	          <bean:message key="system.browse.info.currentorg"/>:
	          <hrms:codetoname codeid="UN" name="selfInfoForm" codevalue="code" codeitem="codeitem" scope="session" uplevel="${browseForm.uplevel}"/>  	      
          	  <bean:write name="codeitem" property="codename" />
          	  <hrms:codetoname codeid="UM" name="selfInfoForm" codevalue="code" codeitem="codeitem" scope="session" uplevel="${browseForm.uplevel}"/>  	      
          	  <bean:write name="codeitem" property="codename" />
          	  <hrms:codetoname codeid="@K" name="selfInfoForm" codevalue="code" codeitem="codeitem" scope="session" uplevel="${browseForm.uplevel}"/>  	      
          	  <bean:write name="codeitem" property="codename" />
          	  &nbsp;&nbsp;
	         </logic:notEmpty>
	         <logic:notEmpty name="selfInfoForm" property="personsortlist" >
	           <html:select name="selfInfoForm" property="personsort" size="1" onchange="changesort()">
                   <html:option value="All">全部</html:option>
                   <html:optionsCollection property="personsortlist" value="codeitem" label="codename"/>
               </html:select> 
			<html:hidden name="selfInfoForm" property="sortfield" />
	       </logic:notEmpty>
	    </td> 
	     <td nowrap>
             <table  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
                <tr>
                       <td nowrap>&nbsp;[&nbsp;
                       </td>
                       <td nowrap id="vieworhidd"> 
                          <a href="javascript:showOrClose();"> 
                              <logic:equal name="selfInfoForm" property="isShowCondition" value="none" >查询显示</logic:equal>   
                              <logic:equal name="selfInfoForm" property="isShowCondition" value="block" >查询隐藏</logic:equal>   
                          </a>
                       </td>                       
                       <td nowrap>&nbsp;]&nbsp;&nbsp;&nbsp;&nbsp;
                       </td>
                    </tr>
             </table>
         </td>  
         <td nowrap>
                 <logic:equal name="selfInfoForm" property="orglike" value="1">
                     <input type="checkbox" id="orglikeid1" name="orglike2" value="true" onclick="selectCheckBox(document.getElementById('orglikeid1'),'orglike');change();" checked>
                 </logic:equal>
                 <logic:notEqual name="selfInfoForm" property="orglike" value="1">
                     <input type="checkbox" id="orglikeid2" name="orglike2" value="true" onclick="selectCheckBox(document.getElementById('orglikeid2'),'orglike');change();">
                 </logic:notEqual>                 
                 <html:hidden name="selfInfoForm" property='orglike' styleId="orglike" styleClass="text"/>                 
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
 <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id='aa' style='display:${selfInfoForm.isShowCondition}'>
  <tr>
   <td>    
     <!-- 查询开始 -->
 
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id="query" class="RecordRow">
       <hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="false" paraValue="" 
                 sql="selfInfoForm.dbcond" collection="list" scope="page"/>
       <%
           List list=(List) pageContext.getAttribute("list");
           if(list!=null&&list.size()>1){
       %>
         <tr>
           <td align="right" height='28' nowrap>
             <bean:message key="label.dbase"/>&nbsp;
           </td>
           <td align="left"  nowrap><!-- 人员库 -->
           
              <html:select name="selfInfoForm" property="userbase" size="1" onchange="javascript:change()">
                      <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select>
              <!-- 人员分类 -->
              <logic:notEqual value="0" name="selfInfoForm" property="showflag">
				  &nbsp;人员分类
				  <hrms:optioncollection name="selfInfoForm" property="condlist" collection="cond" />
	              <html:select name="selfInfoForm" property="stock_cond" onchange="change1();" size="1">
	            	<option value="-1" label="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" />	
	                <html:options collection="cond" property="dataValue" labelProperty="dataName"/>
	              </html:select>
              </logic:notEqual>
           </td>
           <td align="right" height='28' nowrap><!-- 姓名 -->
            <bean:message key="label.title.name"/>&nbsp;
           </td>
           <td align="left"  nowrap>
             <input type="text" name="select_name" value="${selfInfoForm.select_name}" size="32" maxlength="31" class="text4" >
            </td>
          </tr>
        <%}else{ %>         
           <logic:notEqual value="0" name="selfInfoForm" property="showflag">
             <tr>
              <td align="right" height='28' nowrap>
               人员分类&nbsp;
              </td>
              <td align="left"  nowrap><!-- 人员库 -->  
				  <hrms:optioncollection name="selfInfoForm" property="condlist" collection="cond" />
	              <html:select name="selfInfoForm" property="stock_cond" onchange="change1();" size="1">
	            	<option value="-1" label="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" />	
	                <html:options collection="cond" property="dataValue" labelProperty="dataName"/>
	              </html:select>              
              </td>
              <td align="right" height='28' nowrap><!-- 姓名 -->
                <bean:message key="label.title.name"/>&nbsp;
              </td>
              <td align="left"  nowrap>
               <input type="text" name="select_name" value="${selfInfoForm.select_name}" size="32" maxlength="31" class="text4" >
              </td>
             </tr>
            </logic:notEqual>
            <logic:equal value="0" name="selfInfoForm" property="showflag">
                <tr>
                  <td align="right" height='28' nowrap><!-- 姓名 -->
                     <bean:message key="label.title.name"/>&nbsp;
                  </td>
                  <td align="left"  nowrap>
                        <input type="text" name="select_name" value="${selfInfoForm.select_name}" size="32" maxlength="31" class="text4" >
                  </td>
                  <%flag=1; %>
              </logic:equal>
         <%} %>       
       <logic:iterate id="element" name="selfInfoForm"  property="queryfieldlist" indexId="index">            
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
                  <html:text name="selfInfoForm" property='<%="queryfieldlist["+index+"].value"%>' size="13" maxlength="10" styleId="${element.itemid}S" onblur="checkDate(this)" styleClass="text4" title="输入格式：2008.08.08" onclick=""/>
                  <bean:message key="label.query.to"/>
                  <html:text name="selfInfoForm" property='<%="queryfieldlist["+index+"].viewvalue"%>' size="13" maxlength="10" styleId="${element.itemid}E" onblur="checkDate(this)" styleClass="text4" title="输入格式：2008.08.08"  onclick=""/>
			          <!-- 没有什么用，仅给用户与视觉效果-->
			      <INPUT type="radio" name="${element.itemid}"  checked="true"><bean:message key="label.query.age"/>	
			      <INPUT type="radio" name="${element.itemid}" onclick="checkDates('${element.itemid}')" id="day"><bean:message key="label.query.day"/>
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
                  <html:text name="selfInfoForm" property='<%="queryfieldlist["+index+"].value"%>' size="32" maxlength='<%="queryfieldlist["+index+"].itemlength"%>' styleClass="text4"/>
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
              <html:text name="selfInfoForm" property='<%="queryfieldlist["+index+"].value"%>' size="32" maxlength="${element.itemlength}" styleClass="text4"/> 
             </td>
              <%
                 if(flag==0)
        			out.println("</tr>");
              %> 
              
           </logic:equal>
           <logic:equal name="element" property="itemtype" value="A">
              <logic:notEqual name="element" property="codesetid" value="0">  
              <%String delFuntion =  "deleteData(this,'queryfieldlist["+index+"].value');"; %>            
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
                       <html:hidden name="selfInfoForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="selfInfoForm" onkeydown='<%=delFuntion %>' property='<%="queryfieldlist["+index+"].viewvalue"%>' size="32" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                       <logic:equal name="element" property="itemid" value="b0110"> 
                            <img src="/images/code.gif"  plugin="codeselector" codesetid='UN'  nmodule='4' ctrltype='3' inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle" />
                       </logic:equal> 
                       <logic:notEqual name="element" property="itemid" value="b0110">                                         
                            <img src="/images/code.gif"  plugin="codeselector" codesetid='${element.codesetid}' inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle" />
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
                       <html:hidden name="selfInfoForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="selfInfoForm" onkeydown='<%=delFuntion %>' property='<%="queryfieldlist["+index+"].viewvalue"%>' size="32" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                       <logic:equal name="element" property="itemid" value="e0122"> 
                            <img src="/images/code.gif"  plugin="codeselector" codesetid='UM' nmodule='4' ctrltype='3' inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle" />
                       </logic:equal> 
                       <logic:notEqual name="element" property="itemid" value="e0122">                                         
                            <img src="/images/code.gif"  plugin="codeselector" codesetid='${element.codesetid}' inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle" />
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
                       <html:hidden name="selfInfoForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="selfInfoForm" onkeydown='<%=delFuntion %>' property='<%="queryfieldlist["+index+"].viewvalue"%>' size="32" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                        <logic:equal name="element" property="itemid" value="e01a1"> 
                            <img src="/images/code.gif"  plugin="codeselector" codesetid='@K' nmodule='4' ctrltype='3' inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle" />
                       </logic:equal> 
                       <logic:notEqual name="element" property="itemid" value="e01a1"> 
                       <img src="/images/code.gif"  plugin="codeselector" codesetid='${element.codesetid}' inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle" />
                    	</logic:notEqual>
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
                                  <html:hidden name="selfInfoForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                                  <html:text name="selfInfoForm" onkeydown='<%=delFuntion %>' property='<%="queryfieldlist["+index+"].viewvalue"%>' size="32" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                                  <img src="/images/code.gif"  plugin="codeselector" codesetid='${element.codesetid}' inputname='<%="queryfieldlist["+index+"].viewvalue"%>' valuename='<%="queryfieldlist["+index+"].value"%>' multiple='true' onlySelectCodeset='true' align="absmiddle" />
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
        	             	    <html:hidden name="selfInfoForm" styleId='<%="queryfieldlist["+index+"].value"%>' property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
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
               <html:text name="selfInfoForm" property='<%="queryfieldlist["+index+"].value"%>' size="32" maxlength="${element.itemlength}" styleClass="text4"/>
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
             if("true".equals(inforquery_extend)){
        %>
             <td align="right" height='28' nowrap>
                   创建日期&nbsp;
              </td>
              <td align="left"  nowrap>
              		<html:text name="selfInfoForm" property='createtimestart' size="13" maxlength="10" styleClass="text4" title="输入格式：2008.08.08" onclick=""/>
                  <bean:message key="label.query.to"/>&nbsp;
                  <html:text name="selfInfoForm" property='createtimeend' size="13" maxlength="10" styleClass="text4" title="输入格式：2008.08.08" onclick=""/>
              </td>
              </tr>
              <tr>
              <td align="right" height='28' nowrap>
                   创建人&nbsp;
              </td>
              <td align="left"  nowrap>
              		<html:text name="selfInfoForm" property='createusername' size="32" styleClass="text4"/>
              </td>
         <%     
             }
             out.println("<td colspan=\"2\">");
             out.println("</td>");
             out.println("</tr>");
    	}else{
    		if("true".equals(inforquery_extend)){
         %>
         <tr>
         	<td align="right" height='28' nowrap>
                   创建日期&nbsp;
              </td>
              <td align="left"  nowrap>
                  <html:text name="selfInfoForm" property='createtimestart' size="13" maxlength="10" styleClass="text4" title="输入格式：2008.08.08" onclick=""/>
                  <bean:message key="label.query.to"/>&nbsp;
                  <html:text name="selfInfoForm" property='createtimeend' size="13" maxlength="10" styleClass="text4" title="输入格式：2008.08.08" onclick=""/>
             </td>
              <td align="right" height='28' nowrap>
                   创建人&nbsp;
              </td>
              <td align="left"  nowrap>
                  <html:text name="selfInfoForm" property='createusername' size="32" styleClass="text4"/>
              </td>
           </tr>
         <% 
             }
    	}
    	%> 
    	<tr>
    	  <td align="right" height='20'  nowrap>
    	     <bean:message key="label.query.like"/>&nbsp;
    	  </td>
    	  <td align="left" colspan="3" height='20'  nowrap>
    	    <table width="100%" border="0" cellspacing="0" cellpadding="0" >
    	      <tr>
    	        <td>
    	             <logic:equal name="selfInfoForm" property="querylike" value="1">
    	              <input type="checkbox" name="querlike2" value="true" onclick="selectCheckBox(this,'querylike');" checked>
    	             </logic:equal>  
    	             <logic:notEqual name="selfInfoForm" property="querylike" value="1">
    	              <input type="checkbox" name="querlike2" value="true" onclick="selectCheckBox(this,'querylike');">
    	              </logic:notEqual>    	   
    	               <html:hidden name="selfInfoForm" property='querylike' styleClass="text"/>
    	                
    	        </td>
    	        <td>
    	        <!-- 【5652】员工管理，查询浏览，点击信息浏览，状态栏报错。   jingq upd 2014.12.04 -->
    	        	<logic:equal name="selfInfoForm" property="querylike" value="1">
	    	           <div  id="info_cue1" class="query_cue1"> 
	    	             <bean:message key="infor.menu.query.cue1"/>&nbsp;
	    	           </div>
    	           </logic:equal>
    	           <logic:notEqual name="selfInfoForm" property="querylike" value="1">
    	           		<div  id="info_cue1" style='display:none;' class="query_cue1"> 
	    	             <bean:message key="infor.menu.query.cue1"/>
	    	           </div>
    	           </logic:notEqual>
    	        </td>
    	       </tr>
    	     </table>    	 
    	  </td>
    	</tr>    	
      </table>
    </td>
    </tr>
    <tr>
    <td align="center" style="padding-top: 5px;padding-bottom: 5px;">
            <Input type='button' value="<bean:message key="infor.menu.query"/>" onclick="searchinfo('1');" class='mybutton' /> 
    	    <%if(status!=4)
    	    { %>
              <html:button styleClass="mybutton" property="bc_btn1" onclick="selectQ();"><bean:message key="button.sys.cond"/></html:button>
	         <%}else{ %>  
	         	<hrms:priv func_id="2601008,0303014">
    	     		<Input type='button' value="<bean:message key="button.sys.cond"/>" onclick='selectQ();' class='mybutton' />
    	     	</hrms:priv> 
	         <%} %>
	         <Input type='button' value="<bean:message key="button.clear"/>" onclick=' resetQuery();' class='mybutton' />
  	  </td>
    </tr>
 </table>   
   <!-- 查询结束 --> 
 </td>
</tr>
 <tr>
   <td width="100%" nowrap>
     <div id="dataBox" class="fixedDiv2"> <!-- 【6939】员工管理-信息维护-记录录入，当滚动条拖到最下面的时候，界面上会有点问题  jingq upd 2015.02.13-->
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" id="tableid" style="margin-top:-1;">
           <tr class="fixedHeaderTr">
             <td align="center" class="TableRow TableRow_right" position:relative;top:expression(this.offsetParent.scrollTop);" nowrap width="5%">
              <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>
             </td>
             <logic:equal name="selfInfoForm" property="setprv" value="2">
               <hrms:priv func_id="260601,0304011" module_id="">
                 <td align="center" class="TableRow TableRow_right" position:relative;top:expression(this.offsetParent.scrollTop);" nowrap  width="40">
                              插入
                 </td>
               </hrms:priv>
             </logic:equal>
             <logic:equal value="1" name="selfInfoForm" property="approveflag">  
            <!--   <td align="center" class="TableRow" nowrap>
                状态
             </td>-->
             </logic:equal>
          <td align="center" class="TableRow TableRow_right" position:relative;top:expression(this.offsetParent.scrollTop);" nowrap width="40">
		     <bean:message key="label.edit"/>            	
	      </td>
	      <logic:equal name="selfInfoForm" property="multimedia_file_flag" value="1">
	 		      <td align="center" class="TableRow TableRow_right" position:relative;top:expression(this.offsetParent.scrollTop);" nowrap>
					<bean:message key="conlumn.resource_list.name"/>             	
				  </td> 
		</logic:equal> 
          <logic:iterate id="info"    name="selfInfoForm"  property="browsefields">   
              <td align="center" class="TableRow TableRow_right" position:relative;top:expression(this.offsetParent.scrollTop);" nowrap>
                   <bean:write  name="info" property="itemdesc" filter="true"/>
              </td>
          </logic:iterate> 
             <hrms:priv func_id="260608" module_id="">
	         <td align="center" class="TableRow_top TableRow_right"  style="border-top: none;" position:relative;top:expression(this.offsetParent.scrollTop);" nowrap>
				<bean:message key="label.zp_exam.sort"/>             	
			</td>   
			</hrms:priv> 	    	    	    		        	        	        
           </tr>

          <hrms:paginationdb id="element" name="selfInfoForm" sql_str="selfInfoForm.strsql" table="" where_str="selfInfoForm.cond_str" columns="selfInfoForm.columns" order_by="selfInfoForm.order_by" page_id="pagination" pagerows="${selfInfoForm.pagerows}" keys="${selfInfoForm.userbase}A01.a0100" indexes="indexes">
          <%
          int len = llen;
            int curpage = selfInfoForm.getPagination().getCurrent()-1;
			int remainder = len-(pagerows*curpage);
			/*if(remainder>pagerows){
				len=pagerows;
			}else{
				len=remainder;
			}*/
			len=remainder;
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
          <bean:define id="a0100" name="element" property="a0100"/>
          <Input type='hidden' name='a${indexes}' value='<bean:write name="element" property="a0100" filter="true"/>' />
          <hrms:parttime a0100="${a0100}" nbase="${selfInfoForm.userbase}" part_map="<%=partMap%>" name="element" scope="page" code="${selfInfoForm.code}" kind="${selfInfoForm.kind}" uplevel="${selfInfoForm.uplevel}"  b0110_desc="b0110_desc" e0122_desc="e0122_desc" part_desc="part_desc" descOfPart="descOfPart"/>
            <td align="center" class="RecordRow_right" style="border-top: none;" nowrap>
               <hrms:checkmultibox name="selfInfoForm" property="pagination.select" value="true" indexes="indexes"/>
            </td>  
            <logic:equal name="selfInfoForm" property="setprv" value="2">
               <hrms:priv func_id="260601,0304011" module_id="">
                 <td align="center" class="RecordRow" style="border-top: none;border-left: none;" nowrap>
                   <a href="###" onclick="insertInfo('<bean:write name="element" property="a0000" filter="true"/>');"><img src="/images/goto_input.gif" border=0></a>
                </td>
               </hrms:priv>
             </logic:equal>
             <td align="center" class="RecordRow" style="border-top: none;border-left: none;" nowrap>
            	<a href="###" onclick="winhrefOT('<bean:write name="element" property="a0100" filter="true"/>','nil_body');"><img src="/images/edit.gif" border=0></a>
	         </td>
	         <logic:equal name="selfInfoForm" property="multimedia_file_flag" value="1">
	      		<logic:equal name="selfInfoForm" property="setprv" value="2">
	             	<td align="center" class="RecordRow" style="border-top: none;border-left: none;" nowrap>
	            		<a href="###"  onclick='multimediahref("${selfInfoForm.setprv}","${selfInfoForm.userbase}","<bean:write name="element" property="a0100" filter="true"/>");'><img src="/images/muli_view.gif" border=0></a>
		      		</td>
	      		</logic:equal>
	      		<logic:notEqual name="selfInfoForm" property="setprv" value="2">
	             	<td align="center" class="RecordRow" style="border-top: none;border-left: none;" nowrap>
	            		<a href="###"  onclick='multimediahref("${selfInfoForm.setprv}","${selfInfoForm.userbase}","<bean:write name="element" property="a0100" filter="true"/>");'><img src="/images/muli_view.gif" border=0></a>
		      		</td>
	      		</logic:notEqual>
	      	</logic:equal>		        
	         <logic:iterate id="info"    name="selfInfoForm"  property="browsefields">  	
	            <bean:define id="a0100" name="element" property="a0100"/>	      
                  <logic:notEqual  name="info" property="itemtype" value="N">               
                    <td align="left" class="RecordRow" style="border-top: none;border-left: none;" nowrap>        
                  </logic:notEqual>
                  <logic:equal  name="info" property="itemtype" value="N">               
                    <td align="right" class="RecordRow" style="border-top: none;border-left: none;" nowrap>        
                  </logic:equal>   
                  &nbsp;    
                  <logic:equal  name="info" property="codesetid" value="0">   
                 	 
                 	<logic:equal  name="info" property="itemtype" value="D">
                   	 	<bean:define id="elementvalue" name="element" property="${info.itemid}"></bean:define>
                   	 	<%
                   	 		FieldItem item = (FieldItem)pageContext.getAttribute("info");
                   	 		String value = (String)pageContext.getAttribute("elementvalue");
                   	 		int length = item.getItemlength();
               	 			if(18 == length)
               	 				length = length + 1;
               	 			
               	 			out.write(PubFunc.splitString(value,length));
						%>
                     </logic:equal>
                     <logic:notEqual  name="info" property="itemtype" value="D"> 
                    <bean:write  name="element" property="${info.itemid}" filter="true"/>
                  	</logic:notEqual>
                  </logic:equal>
                  <logic:notEqual  name="info" property="codesetid" value="0">  
                   <logic:notEqual  name="info"   property="itemid" value="e0122">  
                    <logic:notEqual  name="info"   property="itemid" value="b0110">  
                     <logic:notEqual  name="info"   property="itemid" value="e01a1">  
                      <logic:equal name="info" property="codesetid" value="UM">
                       <hrms:codetoname codeid="UM" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${selfInfoForm.uplevel}"/>  	      
          	          		<!-- 
          	            	//tianye update start
							//关联部门的指标支持指定单位（部门中查不出信息就去单位中查找）
          	            	-->
          	          		<logic:notEqual  name="codeitem" property="codename" value="">
          	           			  <bean:write name="codeitem" property="codename" /> 
          	           		</logic:notEqual>
          	          		<logic:equal  name="codeitem" property="codename" value="">
          	          		 	<hrms:codetoname codeid="UN" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${selfInfoForm.uplevel}"/>  
          	           			  <bean:write name="codeitem" property="codename" /> 
          	           		</logic:equal>
          	           		<!-- end -->
                      </logic:equal>
                      <logic:notEqual name="info" property="codesetid" value="UM">                      
                        <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
          	    	     <bean:write name="codeitem" property="codename" />  
                        
                      </logic:notEqual>                
                     </logic:notEqual>
                    </logic:notEqual>
                   </logic:notEqual>
                   <logic:equal name="info"   property="itemid" value="b0110">
                      ${b0110_desc}     
                       <%if(StringUtils.isNotEmpty(unitID) && StringUtils.isEmpty(deptID) && StringUtils.isEmpty(posID)){%>
          	          ${empty b0110_desc ? descOfPart : part_desc}
          	          <%} %>
                   </logic:equal>
                   <logic:equal name="info"   property="itemid" value="e0122">
                     ${e0122_desc}   
                     <%if(StringUtils.isNotEmpty(unitID) && StringUtils.isNotEmpty(deptID) && StringUtils.isEmpty(posID)){%>
          	         ${empty e0122_desc ? descOfPart : part_desc}
          	         <%} %>
                   </logic:equal>
                   <logic:equal name="info"   property="itemid" value="e01a1">
                         <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	             <bean:write name="codeitem" property="codename" />
          	             <%if(StringUtils.isNotEmpty(posID)){%>          
          	             <logic:empty name="codeitem" property="codename">${descOfPart}</logic:empty>  
          	             <logic:notEmpty name="codeitem" property="codename">${part_desc}</logic:notEmpty>
          	             <%} %>
                   </logic:equal>
          	     </logic:notEqual>  
              </td>
             </logic:iterate>  
             <hrms:priv func_id="260608" module_id="">   
              <td align="left" class="RecordRow_left" width="50" style="padding:0px;border-top: none;border-left: none;" nowrap>
                 	<%if(i!=1||curpage!=0){ %>
					&nbsp;<a href="javaScript:upItem('${indexes }','<bean:write name="element" property="a0100" filter="true"/>','<bean:write name="element" property="a0000" filter="true"/>');">
					<img src="/images/up01.gif" width="12" height="17" border=0></a> 
					<%}else{ %>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<%} %>
				    <%if(len==i){ %>
				    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				    <%}else{ %>
					&nbsp;<a href="javaScript:downItem('${indexes }','<bean:write name="element" property="a0100" filter="true"/>','<bean:write name="element" property="a0000" filter="true"/>');">
					<img src="/images/down01.gif" width="12" height="17" border=0></a> 
					<%} %>
				</td> 
				</hrms:priv>    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
        
</table>
</div>
</td></tr>
<tr><td>
<table id="bottomid" width="100%" align="left" class="RecordRowP" >
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
</td></tr>
<tr><td>
<table  align="center" style="margin-top: 2px">
          <tr>
            <td align="left">
            <html:hidden name="selfInfoForm" property="tolastpageflag" value="no"/>
             <logic:equal name="selfInfoForm" property="setprv" value="2">
               <hrms:priv func_id="260601,0304011" module_id="">	
                 <input type="button" name="addbutton"  value="<bean:message key="button.insert"/>" class="mybutton" onclick="exeAdd('/workbench/info/addselfinfo.do?b_add=add&a0100=A0100&i9999=I9999&actiontype=new&setname=A01&tolastpageflag=yes&orgparentcode=${selfInfoForm.orgparentcode}&deptparentcode=${selfInfoForm.deptparentcode}&posparentcode=${selfInfoForm.posparentcode}&returnvalue=2')">  
               </hrms:priv>
                 <!--<a href="/workbench/info/addselfinfo.do?b_add=add&a0100=A0100&i9999=I9999&actiontype=new&setname=A01" target="il_body"><bean:message key="button.insert"/></a>-->
              <hrms:priv func_id="260602,0304012" module_id="">	
              	 <input type="button" name="addbutton"  value="<bean:message key="button.delete"/>" class="mybutton" onclick='deleterec()'/>  
	 	      </hrms:priv>
	 	      <hrms:priv func_id="260603,0304013" module_id="">	
	 	         <input type="button" name="addbutton"  value="<bean:message key="button.move"/>" class="mybutton" onclick='moverec()'/>  	
               </hrms:priv>
              </logic:equal>
              <hrms:priv func_id="260606,0304015,030401A" module_id="">	
	 	         <input type="button" name="addbutton"  value="<bean:message key="button.computer"/>" class="mybutton" onclick='batchHand(5,"","${selfInfoForm.userbase}","")'/>  	
               </hrms:priv> 
               <hrms:priv func_id="260608" module_id="">	
	 	         <input type="button" name="addbutton"  value="<bean:message key="infor.menu.msort"/>" class="mybutton" onclick='to_sort_main_info();'/>  	
               </hrms:priv>
               <hrms:priv func_id="260607,0304016,030401B" module_id="">	
	 	         <input type="button" name="addbutton"  value="<bean:message key="menu.gz.batch.update"/>" class="mybutton" onclick='batchHand(2,"","${selfInfoForm.userbase}","")'/>  	
               </hrms:priv>  
              	<hrms:priv func_id="260605,0304014,030401C" module_id="">	
	 	         <input type="button" name="addbutton"  value="<bean:message key="button.batchinout"/>" class="mybutton" onclick='batchInOut()'>  	
               </hrms:priv>
               <hrms:priv func_id="260611,030401D" module_id="">    
                 <input type="button" name="addbutton"  value="<bean:message key="workbench.info.batchdeletesubsetdata"/>" class="mybutton" onclick='batchDeleteSubsetData("${selfInfoForm.userbase}")'>    
               </hrms:priv>
               <hrms:priv func_id="260609" module_id="">	
	 	         <input type="button" name="addbutton"  value="<bean:message key="workbench.browse.photoimport"/>" class="mybutton" onclick='batchPOMImportBefore("photo")'/>  	
               </hrms:priv>
               <hrms:priv func_id="260610" module_id="">	
	 	         <input type="button" name="addbutton"  value="<bean:message key="workbench.browse.multimediaimport"/>" class="mybutton" onclick='batchPOMImportBefore("multimedia")'/>  	
               </hrms:priv>
               <logic:equal value="1" name="selfInfoForm" property="returns">
               	<input type="button" name="b_retrun" value="<bean:message key="button.return"/>" class="mybutton" onclick="hrbreturn('emp','il_body','selfInfoForm');">
               </logic:equal> 
               <%
               	if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("create_random_info"))){
                %>
               <hrms:priv func_id="26060A" module_id="">	
	 	         <input type="button" name="addbutton"  value="生成随机账号" class="mybutton" onclick='createrandomusername();'>  	
               </hrms:priv>
               <%} %>
               <logic:notEqual value="1" name="selfInfoForm" property="returns">
               <logic:equal name="selfInfoForm" property="returnvalue1" value="dxt">
					<input type="button" name="b_retrun" value="返回" class="mybutton" onclick="hrbreturn('emp','il_body','selfInfoForm');" />
				 </logic:equal> 
               </logic:notEqual> 
               <!-- 自助服务导航图返回 -->
               <logic:equal value="zdxt" name="selfInfoForm" property="returnvalue1">
               		<input type="button" name="b_return" value="<bean:message key="button.return"/>" class="mybutton"  onclick="hrbreturn('selfinfo','il_body','selfInfoForm')">
               </logic:equal>
               

            </td>
          </tr>          
</table>
   </td>
 </tr>
 </table>

</html:form>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>

             <td class="td_style" height="24">正在生成随机账号及口令...</td>

           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="400" scrollamount="5" scrolldelay="10">
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
if(!getBrowseVersion()){//兼容非IE浏览器  wangb 20180130 bug 34329  调整table 文字和文本框之间的距离 和table边框显示不全
	var query = document.getElementById('query');
	var trs = query.getElementsByTagName('tr');
	for(var i = 0; i < trs.length-1 ; i++){
		var tds = trs[i].getElementsByTagName('td');
		tds[0].style.paddingRight = '10px';
		tds[2].style.paddingRight = '10px';
	}
}
var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串  
var isFF = userAgent.indexOf("Firefox") > -1; //判断是否Firefox浏览器
if(isFF){//火狐浏览器 单独处理样式   wangb  20180127
	var fixedHeaderTr = document.getElementsByClassName('fixedHeaderTr')[0];
	console.log(fixedHeaderTr);
	fixedHeaderTr.setAttribute('style','border-collapse:separate','important');
	fixedHeaderTr.style.top = '-1px';
	var tds = fixedHeaderTr.getElementsByTagName('td');
	for(var i =0; i <tds.length;i++){
		tds[i].style.backgroundColor='transparent';
	}
} 

	var textTd = document.getElementsByTagName('td');
	if(textTd) {
		for(var i = 0; i < textTd.length; i++) {
			if(textTd) {
		        var textValue = textTd[i].innerHTML;
		        if(!textValue)
		        	textTd[i].innerHTML = '&nbsp;';
			}
		}
	}
	
	setTimeout("setDivStyle()", 100);
</script>
<%if(way.equals("importdate")){ %>
<script type="text/javascript">
function showimportmsg(){
    // 王建华， 增加编制检查的提示信息
    var warninfo = "${selfInfoForm.info}";
    var msg = "${selfInfoForm.message}";
    var error = "${selfInfoForm.error}";
    var num = "${selfInfoForm.num}";
    var noExistsField = "${selfInfoForm.noExistsField}";
    var noExistsField = "${selfInfoForm.noExistsField}";
    var info;
    if(msg) {
    	info = msg;
    	if(error)
        	info = info + "\n错误信息：\n" + error;
    } else {
	    if (num!=0) {
	        info = num + "条记录被成功导入！\n\n";
	        if (warninfo != null && warninfo.length>0) {
	            info = info + "提示：" + warninfo;
	        }
	
	        if(noExistsField)
	          	info = info + "以下指标不存在或未构库，对应列的数据未导入：\n"+ noExistsField;
	      	
	    } else {
	        info = "没有记录被导入！\n\n" + warninfo;
	    }
    }
    
    alert(info);
    
	// alert("${selfInfoForm.num}条记录被成功导入！");
}
window.setTimeout(showimportmsg, 100);
</script>
<%} %>
<logic:notEmpty name="selfInfoForm" property="msg">
<script language='javascript'>
alert('<bean:write  name="selfInfoForm" property="msg"/>');
<%
// WJH 提示完清除
selfInfoForm.setMsg("");
%>
</script>

</logic:notEmpty> 