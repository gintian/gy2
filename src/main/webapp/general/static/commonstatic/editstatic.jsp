<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.general.statics.StaticFieldForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    StaticFieldForm staticFieldForm=(StaticFieldForm)session.getAttribute("staticFieldForm");
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String manager=userView.getManagePrivCodeValue();  
   /**
    * 由先前的按人员管理范围控制改成按如规则进行控制
    * 人员、单位和岗位按业务范围-操作单位-人员管理范围优先级进行控制 
    * cmq changed at 2012-09-29
    */  
    if(staticFieldForm.getInfor_Flag().equalsIgnoreCase("1")||staticFieldForm.getInfor_Flag().equalsIgnoreCase("2")||staticFieldForm.getInfor_Flag().equalsIgnoreCase("3"))
    {
        manager=userView.getUnitIdByBusi("4");
    }
    String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
    //end.  
    
    //非IE浏览器获取标识  wangb 20180126
    String count = request.getParameter("count")==null? "":request.getParameter("count");
%>
<script language="JavaScript" src="/js/function.js"></script>
<!-- 引入ext 和代码控件      wangb 20180127 -->
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="/components/codeSelector/codeSelector.js"></script>
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
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
</script>
<STYLE type=text/css>
.div2
{
 overflow:auto; 
 width: 100%;height: 440px;
 line-height:15px; 
 border-width:1px; 
 border-style: groove;
 border-width :thin ; 
 position:absolute;top:28;left:1;
} 
.fixedDiv2 
{ 
	overflow:auto; 
	height:420px;
	*height:expression(document.body.clientHeight-100);
	width:auto;
	*width:expression(document.body.clientWidth); 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
</STYLE>

<%
  int i=0;
%>
<script language="javascript">
			var selfun;
		   var date_desc;
  
	/*代码项 hidden_name ='xxx.hz' text_name='xxx.hzvalue' 参数flag值为1时，单独处理  wangb  20180726*/
	function fieldcode1(sourceobj,flag)
	{
		var targetobj,target_name,hidden_name,hiddenobj;
    	target_name=sourceobj.name;
    	if(flag==1)
      		hidden_name=target_name.replace(".hzvalue",".hz");
    	else
      		hidden_name=target_name.replace(".viewvalue",".value");       	
    	var hiddenInputs=document.getElementsByName(hidden_name);    
    	if(hiddenInputs!=null)
    	{
    		hiddenobj=hiddenInputs[0];    	
    		codevalue="";
    	}   
    	hiddenobj.value=sourceobj.value;
	}
  
   function setSelectValue()
   {
     if(date_desc)
     {
       date_desc.value=$F('date_box');
       Element.hide('date_panel');   
     }
   }
    function setSelectsValue()
   {
     if(selfun)
     {
       selfun.value=$F('selfun_box');
       Element.hide('selfun');   
     }
   }
    function showDatesSelectBox(srcobj)
   {
          selfun=srcobj;
          Element.show('selfun');   
          var pos=getAbsPosition(srcobj);
	  with($('selfun'))
	  {
	        style.position="absolute";
	        if(navigator.appName.indexOf("Microsoft")!= -1){
	        	style.posLeft=pos[0]-1;
				style.posTop=pos[1]-1+srcobj.offsetHeight;
				style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
	        }else{
	    		style.left=pos[0];
				style.top=pos[1]+srcobj.offsetHeight;
				style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
			}
       }                 

   }
	 function showDateSelectBox(srcobj)
   {
          date_desc=srcobj;
          Element.show('date_panel');   
          var pos=getAbsPosition(srcobj);
	  with($('date_panel'))
	  {
	        style.position="absolute";
	        if(navigator.appName.indexOf("Microsoft")!= -1){
	        	style.posLeft=pos[0]-1;
				style.posTop=pos[1]-1+srcobj.offsetHeight;
				style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
	        }else{
	    		style.left=pos[0];
				style.top=pos[1]+srcobj.offsetHeight;
				style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
			}
       }                 

   }
	
	
	
       	var ccc=0;
        var temp =new Array();
        var indexArray = new Array();
        var b=0;
function showSelect(outparamters)
{ 
	  var flag=outparamters.getValue("flag");
	  if(flag=="true")
	  {
	    alert('<bean:message key="kq.formula.tcheck"/>');
	  }else
	  {
	     alert('<bean:message key="errors.query.expression"/>');
	     var vos= document.getElementsByName('texts');
	     if(vos)
	     {
	        var obj=vos[0];
	        obj.value="";
	     }
	  }
	  
}


function submits()
{
       var expr=$F('texts');
       if(expr==null||expr=="")
       {
         alert("表达式不能为空！");
         return false;
       }      
	   var hashvo=new ParameterSet();
	   //var vosId= document.getElementsByName('hz');
	   //使用ext代码选择控件，不能通过hz直接获取全部 text 代码框       wangb 20180209 34826  and  bug 36454 20180409
	   var count = document.getElementsByName("itemid").length;//获取所有统计指标数
	   var vosId=[];
	   var itemCount =0;//非代码型指标下标
	   for(var i=0;i<ccc;i++){//统计时，区分代码型 和非代码型
	   		var textname = 'factorlist['+ i +'].hz';//代码型 指标 文本框 name属性 格式   factorlist[i].hz
	   		var hiddenText = document.getElementsByName(textname)[0];
	   		if(hiddenText && !hiddenText.value){//直接在文本框里写值情况处理
	   			var codeText = document.getElementsByName('factorlist['+ i +'].hzvalue')[0];
	   			vosId.push(codeText);
	   			continue;
	   		}
	   		if(hiddenText){
	   			vosId.push(hiddenText);
	   			continue;
	   		}
	   		if(indexArray.length>0){
	   			for( var j = 0 ; j < indexArray.length ; j++){
	   				if(indexArray[j] != i)
	   					continue;
	   				hiddenText = document.getElementsByName('hz')[itemCount];//非代码型指标 文本框 name属性 格式 hz
	   				if(hiddenText){
	   					vosId.push(hiddenText);
	   					itemCount++;
	   				}
	   			}
	   		}else{
	   			hiddenText = document.getElementsByName('hz')[itemCount];//非代码型指标 文本框 name属性 格式 hz
	   			if(hiddenText){
	   				vosId.push(hiddenText);
	   				itemCount++;
	   			}
	   		}
	   }
	   var vosoper= document.getElementsByName('oper');
	   var vosFieldname=document.getElementsByName("itemid");  
	   var arr=new Array();	   
	   if(vosId&&vosoper&&vosFieldname&&vosFieldname.length==vosId.length&&vosId.length==vosoper.length)
	   {
	       for(var r=0;r<vosId.length;r++)
	       {
	              var objId=vosId[r];
	              var objfieldname=vosFieldname[r];	              
	              var value=objId.value;
	              var fieldname=objfieldname.value;
	              var objOper=vosoper[r];
	              var oper="";
	              for(var i=0;i<objOper.options.length;i++)
                  {
                      if(objOper.options[i].selected)
                      {
                         oper=objOper.options[i].value;
                         break;
                      }
                  }                  
                  var oobj=new Object();
                  oobj.value=value;
                  oobj.oper=oper;
                  oobj.fieldname=fieldname;                  
                  arr[r]=oobj;                  
	       }  
	   }
	   hashvo.setValue("texts",expr);
	   hashvo.setValue("sno",arr.length);
       hashvo.setValue("type","${staticFieldForm.infor_Flag}");
	   hashvo.setDataType("sno","string");	
	   hashvo.setValue("arr",arr);
       var  request=new Request({onSuccess:editsubmit,functionId:'05301010010'},hashvo);
   	  
}
function editsubmit(outparamters)
{
    var flag=outparamters.getValue("flag");
	if(flag=="true")
	{
	   if(ccc>0)
	   {
	   	
	      //var vosId= document.getElementsByName('hz');
	      //使用ext代码选择控件，不能通过hz直接获取全部 text 代码框       wangb 20180209 34826  and  bug 36454 20180409
	      var count = document.getElementsByName("itemid").length;//获取所有统计指标数
	      var vosId=[];
	      var itemCount =0;//非代码型指标下标
	      for(var i=0;i<ccc;i++){//统计时，区分代码型 和非代码型
	   		   var textname = 'factorlist['+ i +'].hz';//代码型 指标 文本框 name属性 格式   factorlist[i].hz
	   		   var hiddenText = document.getElementsByName(textname)[0];
	   		   if(hiddenText && !hiddenText.value){//直接在文本框里写值情况处理
	   			   var codeText = document.getElementsByName('factorlist['+ i +'].hzvalue')[0];
	   			   vosId.push(codeText);
	   			   continue;
	   		   }
	   		   if(hiddenText){
	   			   vosId.push(hiddenText);
	   			   continue;
	   		   }
	   		   if(indexArray.length>0){
	   				for( var j = 0 ; j < indexArray.length ; j++){
	   					if(indexArray[j] != i)
	   						continue;
	   					hiddenText = document.getElementsByName('hz')[itemCount];//非代码型指标 文本框 name属性 格式 hz
	   					if(hiddenText){
	   						vosId.push(hiddenText);
	   						itemCount++;
	   					}
	   				}
	   			}else{
	   				hiddenText = document.getElementsByName('hz')[itemCount];//非代码型指标 文本框 name属性 格式 hz
	   				if(hiddenText){
	   					vosId.push(hiddenText);
	   					itemCount++;
	   				}
	   			}
	      }
	      var vosoper= document.getElementsByName('oper');
	      var vosFieldname=document.getElementsByName("itemid");  
	       
	      if(vosId&&vosoper&&vosFieldname&&vosFieldname.length==vosId.length&&vosId.length==vosoper.length)
	      {
	           var arr=new Array();
	           for(var r=0;r<vosId.length;r++)
	           {
	              var objId=vosId[r];
	              var objfieldname=vosFieldname[r];	              
	              var value=objId.value;
	              var fieldname=objfieldname.value;
	              var objOper=vosoper[r];
	              var oper="";
	              for(var i=0;i<objOper.options.length;i++)
                  {
                      if(objOper.options[i].selected)
                      {
                         oper=objOper.options[i].value;
                         break;
                      }
                  }                  
                  var oobj=new Object();
                  oobj.value=value;
                  oobj.oper=oper;
                  oobj.fieldname=fieldname;
                  arr[r]=oobj;                  
	           }
	           var vost= document.getElementsByName('texts');
	           var objt=vost[0];
	           var texts=objt.value;
	           var hashvo=new ParameterSet();
	           hashvo.setValue("texts",texts);
	           var titles=$F('titles');
	           if(titles==null||titles=="")
               {
                 alert("统计条件名称不能为空！");
                 return false;
               } 
               if(titles.indexOf("\‘")>-1||titles.indexOf("\”")>-1||titles.indexOf("\'")>-1||titles.indexOf("\"")>-1)
			  {	
			       		alert("统计条件名称不能包含\’或\"或\’或\”");
			       		return false;
			  }      
               hashvo.setValue("titles",titles);
               vost= document.getElementsByName("history");
               objt=vost[0];
               var history="0";
               if(objt.checked==true)
               {
                  history="1";
               }
               hashvo.setValue("history",history);
               hashvo.setValue("editid","${staticFieldForm.editid}");
               hashvo.setValue("statid","${staticFieldForm.statid}");
               hashvo.setValue("flagtype","${staticFieldForm.opflag}");
               hashvo.setValue("arr",arr);
               var request=new Request({onSuccess:submitRe,functionId:'11080204053'},hashvo);
	      }	     
	      
	   }
	}else
	{
	    alert('<bean:message key="errors.query.expression"/>');
	    var vos= document.getElementsByName('texts');
	    if(vos)
	    {
	        var obj=vos[0];
	        obj.value="";
	    }
	}    
}
function submitRe(outparamters)
{
  var opflag=outparamters.getValue("opflag");  
  if(opflag=="true")
  {
      alert("操作成功！");
      var vo=new Object();
      vo.flag="true";
      var action=outparamters.getValue("action");
      vo.action=action;
      var legend=outparamters.getValue("legend");
      vo.legend=legend;
      var uid=outparamters.getValue("uid");
      vo.uid=uid;
      if(getBrowseVersion()/*navigator.appName.indexOf("Microsoft")!= -1*/){
      	//window.returnValue=vo;
      	openReturn(vo);
      }else{
      	//top.returnValue=vo;
      	openReturn(vo);
      }
      //top.close();
      openClose();
  }else
  {
     alert("操作失败！")
  }
}
function check()
{
	   var expr=$F('texts');
	   if(expr==null||expr=="")
       {
         alert("表达式不能为空！");
         return false;
       }   
	   var hashvo=new ParameterSet();
	   //var vosId= document.getElementsByName('hz');
	   //使用ext代码选择控件，不能通过hz直接获取全部 text 代码框       wangb 20180209 34826  and  bug 36454 20180409
	   var count = document.getElementsByName("itemid").length;//获取所有统计指标数
	   var vosId=[];
	   var itemCount =0;//非代码型指标下标
	   for(var i=0;i<ccc;i++){//统计时，区分代码型 和非代码型
	   		var textname = 'factorlist['+ i +'].hz';//代码型 指标 文本框 name属性 格式   factorlist[i].hz
	   		var hiddenText = document.getElementsByName(textname)[0];
	   		if(hiddenText && !hiddenText.value){//直接在文本框里写值情况处理
	   			var codeText = document.getElementsByName('factorlist['+ i +'].hzvalue')[0];
	   			vosId.push(codeText);
	   			continue;
	   		}
	   		if(hiddenText){
	   			vosId.push(hiddenText);
	   			continue;
	   		}
	   		if(indexArray.length>0){
	   			for( var j = 0 ; j < indexArray.length ; j++){
	   				if(indexArray[j] != i)
	   					continue;
	   				hiddenText = document.getElementsByName('hz')[itemCount];//非代码型指标 文本框 name属性 格式 hz
	   				if(hiddenText){
	   					vosId.push(hiddenText);
	   					itemCount++;
	   				}
	   			}
	   		}else{
	   			hiddenText = document.getElementsByName('hz')[itemCount];//非代码型指标 文本框 name属性 格式 hz
	   			if(hiddenText){
	   				vosId.push(hiddenText);
	   				itemCount++;
	   			}
	   		}
	   }
	   var vosoper= document.getElementsByName('oper');
	   var vosFieldname=document.getElementsByName("itemid");  
	   var arr=new Array();
	   //alert(vosId.length+" "+vosoper.length+" "+vosFieldname.length);
	   if(vosId&&vosoper&&vosFieldname&&vosFieldname.length==vosId.length&&vosId.length==vosoper.length)
	   {
	       for(var r=0;r<vosId.length;r++)
	       {
	              var objId=vosId[r];
	              var objfieldname=vosFieldname[r];	              
	              var value=objId.value;
	              var fieldname=objfieldname.value;
	              var objOper=vosoper[r];
	              var oper="";
	              for(var i=0;i<objOper.options.length;i++)
                  {
                      if(objOper.options[i].selected)
                      {
                         oper=objOper.options[i].value;
                         break;
                      }
                  }                  
                  var oobj=new Object();
                  oobj.value=value;
                  oobj.oper=oper;
                  oobj.fieldname=fieldname;                  
                  arr[r]=oobj;                  
	       }  
	   }	 
	   hashvo.setValue("texts",expr);
	   hashvo.setValue("sno",arr.length);
       hashvo.setValue("type","${staticFieldForm.infor_Flag}");
       hashvo.setValue("arr",arr);
	   hashvo.setDataType("sno","string");	
       var  request=new Request({onSuccess:showSelect,functionId:'05301010010'},hashvo);

}

 function addTxt(strtxt)
	{
		var expr_editor=$('texts');
                expr_editor.focus();
		var element = document.selection;
		if (element!=null) 
		{
		  var rge = element.createRange();
		  if (rge!=null)	
		  	   rge.text=strtxt;
	   }else{
	   			var word = expr_editor.value;
				var _length=strtxt.length;
				var startP = expr_editor.selectionStart;
				var endP = expr_editor.selectionEnd;
				var ddd=word.substring(0,startP)+strtxt+word.substring(endP);
		    	expr_editor.value=ddd;
        		expr_editor.setSelectionRange(startP+_length,startP+_length); 
	   	
	   }
	}
	
 function addEpre(strtxt)
	{
		vos= document.getElementsByName(strtxt);
		if(vos==null)
  	           return false;
                left_vo=vos[0];
	   	for(i=0;i<left_vo.options.length;i++)
              {
                   if(left_vo.options[i].selected)
                   {
    	               staticFieldForm.texts.value=left_vo.options[i].text;
    	               $('selects').options.remove(i);
      		          }
              }
 	} 	

function edit_field()
{
	var dw=480,dh=360,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    var theurl="/general/static/commonstatic/editstatic.do?br_field=link";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   	iframe_url = iframe_url.replace(/&/g,"`");
    if(getBrowseVersion()){ //IE浏览器
    	var return_vo= window.showModalDialog(iframe_url,0, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:480px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
    	if(return_vo&&return_vo.tran_flag=="1")
    	{
      	 	var list=return_vo.list;       
       		var hashvo=new ParameterSet();
	   		hashvo.setValue("list",list);	
       		var  request=new Request({onSuccess:addSelect,functionId:'11080204052'},hashvo);
    	}
    }else{//非IE浏览器  改用EXT window 显示
    	var dialog=[];dialog.dw=dw;dialog.dh=dh+50;dialog.iframe_url=iframe_url;
    	openWin(dialog);
    }
    
}
//非IE浏览器 改用ext.window wangb 20180127
function openWin(dialog){
	Ext.create("Ext.window.Window",{
		    	id:'edit_field',
		    	width:dialog.dw,
		    	height:dialog.dh+10,
		    	title:'选择指标',
		    	resizable:false,
		    	modal:true,
		    	autoScroll:true,
		    	renderTo:Ext.getBody(),
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+dialog.iframe_url+"'></iframe>"
	}).show();
}
function winClose(){
	Ext.getCmp('edit_field').close();
}
//非IE浏览器 弹窗调用方法
function openEdit_field(return_vo){
	winClose();
	if(return_vo&&return_vo.tran_flag=="1")
    {
       var list=return_vo.list;       
       var hashvo=new ParameterSet();
	   hashvo.setValue("list",list);	
       var  request=new Request({onSuccess:addSelect,functionId:'11080204052'},hashvo);
    }
}

function addSelect(outparamters)
{
   var list=outparamters.getValue("list");
   if(list==null||list.length<=0)
       return false;
    var style_RecordRow="border: inset 1px #94B6E6;"
	    style_RecordRow=style_RecordRow+"BORDER-BOTTOM: #94B6E6 1pt solid;";;
	    style_RecordRow=style_RecordRow+"BORDER-LEFT: #94B6E6 1pt solid;"; 
	    style_RecordRow=style_RecordRow+"BORDER-RIGHT: #94B6E6 1pt solid;"; 
	    style_RecordRow=style_RecordRow+"BORDER-TOP: #94B6E6 1pt solid;";
	    style_RecordRow=style_RecordRow+"font-size: 12px;";
	    style_RecordRow=style_RecordRow+"border-collapse:collapse;"; 
	    style_RecordRow=style_RecordRow+"height:22;";    
   for(var s=0;s<list.length;s++)
   {
        var table=document.getElementById('tab');
        if(table==null)
  	      return false;
  	    var td_num=table.rows.length;
  	    var rowCount=table.rows.length;	  
  	    var index=rowCount-1;
  	    
	    var tRow = table.insertRow(index);
  	    var cell_0=""+index+"";
	    index = ccc+1;
	    indexArray[ccc]=ccc;
  	    var cell_1=""+list[s].hz+"";
  	    cell_1=cell_1+"<input type=\"hidden\" name=\"itemid\" id='itemid' value=\""+list[s].fieldname+"\">";
  	    cell_1=cell_1+"";
  	    var cell_2="<td nowrap>";
  	    cell_2=cell_2+"<select id=\""+index+"oper\" name='oper' size=\"1\">";
  	    cell_2=cell_2+"<option value=\"=\" selected=\"selected\">=</option>";
        cell_2=cell_2+"<option value=\"&gt;\">&gt;</option>";
        cell_2=cell_2+"<option value=\"&gt;=\">&gt;=</option>";
        cell_2=cell_2+"<option value=\"&lt;\">&lt;</option>";
        cell_2=cell_2+"<option value=\"&lt;=\">&lt;=</option>";
        cell_2=cell_2+"<option value=\"&lt;&gt;\">&lt;&gt;</option>";
        cell_2=cell_2+"</select>";
        cell_2=cell_2+"</td>";
        var codesetid=list[s].codeid;
        var fieldtype=list[s].fieldtype;
        var itemlen=list[s].itemlen;
        var fieldname=list[s].fieldname;
  	    var cell_3="<td align=\"left\" class=\"RecordRow\" nowrap>";      
  	    if(fieldtype=="D")
  	    {
  	       cell_3=cell_3+"<input type=\"text\" id=\""+index+"hz\" maxlength=\"10\" name=\"hz\" size=\"30\" value=\"\" ondblclick=\"showDateSelectBox(this);\" class=\"text4\">";
  	    }else if(fieldtype=="M")
  	    {
  	       cell_3=cell_3+"<input type=\"text\" id=\""+index+"hz\" maxlength=\"30\" name=\"hz\" size=\"30\" value=\"\" maxlength='"+itemlen+"' class=\"text4\">";
  	       
  	    }else if(fieldtype=="N")
  	    {
  	        cell_3=cell_3+"<input type=\"text\" id=\""+index+"hz\" maxlength=\"30\" name=\"hz\" size=\"30\" value=\"\" maxlength='"+itemlen+"' class=\"text4\">"; 
  	    }else if(fieldtype=="A")
  	    {
  	       if(codesetid!="0")
  	       {
  	       	  indexArray[ccc]=undefined;
              cell_3=cell_3+"<input type=\"hidden\" id=\"factorlist["+(index-1)+"].hz\" name=\"factorlist["+(index-1)+"].hz\"  value=\"\" class=\"text4\" onchange='getTrueValue("+(index-1)+")'>";               
  	          cell_3=cell_3+"<input type=\"text\" name=\"factorlist["+(index-1)+"].hzvalue\" maxlength=\"30\" size=\"30\" value=\"\" class=\"text4\" onchange=\"fieldcode1(this,1)\">";
  	         	<logic:equal name="staticFieldForm" property="infor_Flag" value="#">
                                <--cell_3=cell_3+" <img src=\"/images/code.gif\" onclick='openCondCodeDialog(\""+codesetid+"\",\"factorlist["+(index-1)+"].hzvalue\");'/>";-->
                                <!-- 使用代码组件控件兼容非IE浏览器 wangb 20180127  -->
                      cell_3=cell_3+"  <img src=\"/images/code.gif\" align=\"absmiddle\" id=\"factorlist["+(index-1)+"]\" onlySelectCodeset=\"true\" plugin=\"codeselector\" codesetid=\""+codesetid+"\" inputname=\"factorlist["+(index-1)+"].hzvalue\" valuename=\"factorlist["+(index-1)+"].hz\"/>";
                                 </logic:equal>
                                  <logic:notEqual name="staticFieldForm" property="infor_Flag" value="1">
                                   </logic:notEqual>
                                  		if(fieldname=="b0110"){
                                          <!-- cell_3=cell_3+" <img src=\"/images/code.gif\" align=\"middle\" onclick='openInputCodeDialogOrgInputPos(\"UM\",\"factorlist["+(index-1)+"].hzvalue\",\"<%=manager%>\",3);'/>";-->
                                        	 <!-- 使用代码组件控件兼容非IE浏览器 wangb 20180127  -->
                      cell_3=cell_3+"  <img src=\"/images/code.gif\" editable=\"true\" align=\"absmiddle\" id=\"factorlist["+(index-1)+"]\" onlySelectCodeset=\"true\" plugin=\"codeselector\" codesetid=\"UN\" nmodule=\"4\" ctrltype=\"3\" inputname=\"factorlist["+(index-1)+"].hzvalue\" valuename=\"factorlist["+(index-1)+"].hz\"/>";
                                        }else if(fieldname=="e0122"){
                                               <!-- cell_3=cell_3+" <img src=\"/images/code.gif\" align=\"middle\" onclick='openInputCodeDialogOrgInputPos(\"UM\",\"factorlist["+(index-1)+"].hzvalue\",\"<%=manager%>\",1);'/>";-->
                                             <!-- 使用代码组件控件兼容非IE浏览器 wangb 20180127  -->
                     cell_3=cell_3+"  <img src=\"/images/code.gif\" editable=\"true\" align=\"absmiddle\" id=\"factorlist["+(index-1)+"]\" onlySelectCodeset=\"true\" plugin=\"codeselector\" codesetid=\"UM\" nmodule=\"4\" ctrltype=\"3\" inputname=\"factorlist["+(index-1)+"].hzvalue\" valuename=\"factorlist["+(index-1)+"].hz\"/>";
                                            }else if(fieldname=="e01a1"){
                                               <!-- cell_3=cell_3+" <img src=\"/images/code.gif\" align=\"middle\" onclick='openInputCodeDialogOrgInputPos(\"@K\",\"factorlist["+(index-1)+"].hzvalue\",\"<%=manager%>\",1);'/>";-->
                                                <!-- 使用代码组件控件兼容非IE浏览器 wangb 20180127  -->
                      cell_3=cell_3+"  <img src=\"/images/code.gif\" editable=\"true\" align=\"absmiddle\" id=\"factorlist["+(index-1)+"]\" onlySelectCodeset=\"true\" plugin=\"codeselector\" codesetid=\"@K\" nmodule=\"4\" ctrltype=\"3\" inputname=\"factorlist["+(index-1)+"].hzvalue\" valuename=\"factorlist["+(index-1)+"].hz\"/>";
                                            }else{
                                                <!--cell_3=cell_3+" <img src=\"/images/code.gif\" align=\"middle\" onclick='openCondCodeDialog(\""+codesetid+"\",\"factorlist["+(index-1)+"].hzvalue\");'/>";-->
                                                 <!-- 使用代码组件控件兼容非IE浏览器 wangb 20180127  -->
                      cell_3=cell_3+"  <img src=\"/images/code.gif\" align=\"absmiddle\" id=\"factorlist["+(index-1)+"]\" onlySelectCodeset=\"true\" plugin=\"codeselector\" codesetid=\""+codesetid+"\" inputname=\"factorlist["+(index-1)+"].hzvalue\" valuename=\"factorlist["+(index-1)+"].hz\"/>";
                                            }                                                                                                                         
                                
  	       }else
  	       {
  	          cell_3=cell_3+"<input type=\"text\" id=\""+index+"hz\" name=\"hz\" maxlength=\"30\" size=\"30\" value=\"\" maxlength='"+itemlen+"' class=\"text4\">";
  	       }  	    
  	    }else
  	    {
  	        cell_3=cell_3+"";
  	    }  	    
  	    cell_3=cell_3+"</td>";
  	    var cell_4="&nbsp; <img src=\"/images/del.gif\" align=\"middle\" border=0 title='<bean:message key="button.delete"/>' style=\"cursor:hand;\" onclick=\"delete_stat('"+index+"');\"/> &nbsp;"	    
  	    for (var i=0;i<5;i++)
        { 
             var newCell=tRow.insertCell(i);
             switch (i) 
             { 
               case 0 : newCell.innerHTML=cell_0;newCell.style.cssText="text-align:center;border-left:none;";newCell.className="RecordRow";break; 
               case 1 : newCell.innerHTML=cell_1;newCell.style.cssText="text-align:center;";newCell.className="RecordRow";break; 
               case 2 : newCell.innerHTML=cell_2;newCell.style.cssText="text-align:center;";newCell.className="RecordRow";break; 
               case 3 : newCell.innerHTML=cell_3;newCell.style.cssText="text-align:left;";newCell.className="RecordRow";break; 
               case 4 : newCell.innerHTML=cell_4;newCell.style.cssText="text-align:center;border-right:none;";newCell.className="RecordRow";break;  
             } 
         
        }  
         ccc++; 
        //绑定codeselect 代码控件 wangb 20180127 
        var idList=["factorlist["+(index-1)+"]"];
        setEleConnect(idList);	
   }
}
function delete_stat(trl)
{
     var table=document.getElementById('tab');
     if(table==null)
  	    return false; 
  	 if(!trl)
  	    return false; 
     var rowNums=table.rows.length;
     index=parseInt(trl);      
     if(rowNums>2)
     {
       if(index>=rowNums)
         index=rowNums-2;
       if(index<=0)
         index=1;
       if(confirm("确定删除此条件吗？"))
       {
          //ccc--;  
          var tr = table.rows[index];
          var td = tr.getElementsByTagName('td')[3];
          var text_name = tr.getElementsByTagName('input')[1];
          if(text_name.name='hz')
          	indexArray[parseInt(text_name.id)]=undefined;   
          table.deleteRow(index);          
          table=document.getElementById('tab');
          if(table==null)
  	         return false;
  	      var td_num=table.rows.length;
  	      var rowCount=table.rows.length;	
  	      for(var i=1;i<rowCount-1;i++)
  	      {
  	          var tRow = table.rows[i];
  	          var cellv=tRow.cells[0].innerHTML;  	          
  	          var v=parseInt(cellv);  	         
  	          tRow.cells[0].innerHTML=i;  	          
  	          var cell_4="&nbsp; <img src=\"/images/del.gif\" border=0 title='<bean:message key="button.delete"/>' style=\"cursor:hand;\" onclick=\"delete_stat('"+i+"');\"/> &nbsp;"	    
  	          tRow.cells[4].innerHTML=cell_4;
  	          //alert(tRow.cells[0].innerHTML);
  	      }
       }
     }      
}
//add by wangchaoqun on 2014-10-15 begin
function getTrueValue(index){
       var elements =  document.getElementById("factorlist["+index+"].value");
       var v = elements.value;
       if(v != null && v.length>0 && (v.indexOf('UN')==0 || v.indexOf('UM')==0 || v.indexOf('@K')==0)){
           v = v.substring(2);
       }
       elements.value = v;
   }
   //add by wangchaoqun on 2014-10-15 end
</script>
<hrms:themes />
<%if("hcm".equalsIgnoreCase(bosflag)){ %>
<style>
.fixedDiv2 {
	height:expression(document.body.clientHeight-80)!important;
}
.TableRow_top{
	padding:0 5px 0 5px;
}
</style>
<%}else{ %>
<style>
.fixedDiv2 {
	margin-top:10px;
	height:expression(document.body.clientHeight-80)!important;
}
.TableRow_top{
	padding:0 5px 0 5px;
}
</style>
<%} %>
<html:form action="/general/static/commonstatic/editstatic" >
<html:hidden property="infor_Flag"/>
<html:hidden name="staticFieldForm" property="mess"/>
<div id="tbl-container"  class="fixedDiv2">
 <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
     <tr class="fixedHeaderTr" style="35px;">
      <td align="left" class="TableRow_top">       
        <logic:equal name="staticFieldForm" property="opflag" value="new">
                         新增
        </logic:equal>
        <logic:equal name="staticFieldForm" property="opflag" value="edit">
                          修改
        </logic:equal>
         【<bean:write name="staticFieldForm" property="stat_name" />】
          统计条件
     </td>
      </tr> 
      <tr> 
      <td align="center" width="100%" height="100%">   
        <table border="0"  cellspacing="0" width="598px"  cellpadding="0" align="center" class="ListTable"> 
         <tr>
         <td>
           <table  border="0"  cellspacing="0" width="100%" cellpadding="0">
       		<tr><td height="5px"></td></tr>
              <tr>
                  <td align="left">统计条件名称
                    <html:text name="staticFieldForm" styleClass="text4" property='titles' size="30" maxlength='15'/> 
                    <html:hidden name="staticFieldForm" property='editid' styleClass="text4"/>     
                    <html:hidden name="staticFieldForm" property='statid' styleClass="text4"/>                    
                  </td>
                 </tr>
                </table>
                </td>
            </tr>      
            <tr><td height="5">&nbsp;            
            </td></tr>          
            <tr>
            <td align="center" height="218">
            <div style="height:100%; width: 100%;overflow-y:scroll;border: 1px solid;" class="common_border_color"><!--update by xiegh on date 20180418 bug36734 ie9中滚动条不出现 -->
             <table border="0"  cellspacing="0" width="100%" class="ListTable"  cellpadding="0" align="center" id="tab">
              <tr> 
               <td align="center"  class="TableRow" style="border-left: none;border-top: none;" height="30"><bean:message key="label.query.number"/></td>
               <td align="center"  class="TableRow" style="border-top: none;" height="30"><bean:message key="static.target"/></td>
               <td align="center"  class="TableRow" style="border-top: none;" height="30"><bean:message key="static.relation"/></td>
               <td align="center"  class="TableRow" style="border-top: none;">
                 <table  border="0"  cellspacing="0" width="100%" cellpadding="0">
                  <tr>
                    <td align="center">
                      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                      <b>
                      <bean:message key="static.title"/>
                      </b>
                      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    </td>
                  </tr>
                 </table>
               </td>
               <td align="center"  class="TableRow" style="border-top: none;border-right: none;border-collapse:collapse;">
                 &nbsp;
               </td>               
              </tr>
               <logic:iterate id="element" name="staticFieldForm"  property="factorlist" indexId="index"> 
                 <script language="javascript">
               			indexArray[ccc]=ccc;
               			ccc++;
               	</script>                   
                 <tr id="tr<%=i+1%>">       
                   <td align="center" class="RecordRow" style="border-left: none;" nowrap >
				            <%=i+1%>
                    </td>                  
                   <td align="center" class="RecordRow" style="border-left: none;" nowrap >	
                     <bean:write name="element" property="hz" />
                     <input type="hidden" name="itemid" id='itemid' value="<bean:write name="element" property="fieldname" />" >
                     &nbsp;
                    </td>  
                   <td align="center" class="RecordRow" nowrap >
                      <select name="oper" id="${index }select" size="1">
                                <option value="=">=</option>
                               	<option value="&gt;">&gt;</option>
								<option value="&gt;=">&gt;=</option>
								<option value="&lt;">&lt;</option>
								<option value="&lt;=">&lt;=</option>
								<option value="&lt;&gt;">&lt;&gt;</option>  
                               </select>
                               <script type="text/javascript">
                               		var selected="<bean:write name="staticFieldForm" property='<%="factorlist["+index+"].oper"%>' />";
                               		var _options=document.getElementById('${index }select').options;
                               			if("="==selected){
                               				_options[0].selected=true;
                               			}
                               			if("&gt;"==selected){
                               				_options[1].selected=true;
                               			}
                               			if("&gt;="==selected){
                               				_options[2].selected=true;
                               			}
                               			if("&lt;"==selected){
                               				_options[3].selected=true;
                               			}
                               			if("&lt;="==selected){
                               				_options[4].selected=true;
                               			}
                               			if("&lt;&gt;"==selected){
                               				_options[5].selected=true;
                               			}
                               </script>
                     </td>                                                  
                      <!--日期型 -->                            
                     <logic:equal name="element" property="fieldtype" value="D">
                      <td align="left" class="RecordRow" nowrap>       
                       		<input type="text" id="${index }hz" name="hz" value="<bean:write name="staticFieldForm" property='<%="factorlist["+index+"].value"%>' />" size="30" maxlength="10" class="text4" ondblclick="showDateSelectBox(document.getElementById('${index }hz'));" />&nbsp;&nbsp;&nbsp;&nbsp;
                       </td>                           
                      </logic:equal>
                       <!--备注型 -->                              
                      <logic:equal name="element" property="fieldtype" value="M">
                        <td align="left" class="RecordRow" nowrap>      
                            <input type="text" id="${index }hz" name="hz" value="<bean:write name="staticFieldForm" property='<%="factorlist["+index+"].value"%>' />" size="30" maxlength='<%="factorlist["+index+"].itemlen"%>' class="text4" />&nbsp;&nbsp;&nbsp;&nbsp;
                         </td>                
                       </logic:equal>
                        <!--字符型 -->                                                  
                       <logic:equal name="element" property="fieldtype" value="A">
                         <td align="left" class="RecordRow" style="border-right: none;" nowrap>
                                 <logic:notEqual name="element" property="codeid" value="0">
                                 	<script language="javascript"> 
               							indexArray[ccc-1]=undefined;
               						</script>  
                                 	<input type="hidden" id='<%="factorlist["+index+"].value"%>' name='<%="factorlist["+index+"].hz"%>' onchange='<%="getTrueValue("+index+");" %>' value="<bean:write name="staticFieldForm" property='<%="factorlist["+index+"].value"%>' />" class="text4" />
                                  <html:text name="staticFieldForm" property='<%="factorlist["+index+"].hzvalue"%>' styleId='<%="factorlist["+index+"].hzvalue"%>' size="30" maxlength="30" styleClass="text4" onchange="fieldcode1(this,1)"/>
                                  <logic:equal name="staticFieldForm" property="infor_Flag" value="1">
                                 <!-- <img src="/images/code.gif" onclick='openCondCodeDialog("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>");'/> -->
                                 </logic:equal>
                                  <logic:notEqual name="staticFieldForm" property="infor_Flag" value="1">
                                  </logic:notEqual>
                                  		<logic:equal name="element" property="fieldname" value="b0110"> 
                                           <!--<img align="absMiddle" src="/images/code.gif" onclick='openInputCodeDialogOrgInputPos("UM","<%="factorlist["+index+"].hzvalue"%>","<%=manager%>",3);'/>-->
                                           <!-- 使用代码组件控件兼容非IE浏览器 wangb 20180127  -->
                        <img src="/images/code.gif" align="absmiddle" editable="true" id="factorlist<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="UN" nmodule=\"4\" ctrltype=\"3\" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="<%="factorlist["+index+"].hz"%>"/>
                                         </logic:equal> 
                                         <logic:notEqual name="element" property="fieldname" value="b0110">   
                                            <logic:equal name="element" property="fieldname" value="e0122"> 
                                                <!--<img src="/images/code.gif" align="absMiddle" onclick='openInputCodeDialogOrgInputPos("UM","<%="factorlist["+index+"].hzvalue"%>","<%=manager%>",1);'/>-->
                                                 <!-- 使用代码组件控件兼容非IE浏览器 wangb 20180127  -->
                        <img src="/images/code.gif" align="absmiddle" editable="true" id="factorlist<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="UM" nmodule=\"4\" ctrltype=\"3\" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="<%="factorlist["+index+"].hz"%>"/>
                                            </logic:equal>
                                            <logic:equal name="element" property="fieldname" value="e01a1"> 
                                                <!--<img src="/images/code.gif" align="absMiddle" onclick='openInputCodeDialogOrgInputPos("@K","<%="factorlist["+index+"].hzvalue"%>","<%=manager%>",1);'/>-->
                                                 <!-- 使用代码组件控件兼容非IE浏览器 wangb 20180127  -->
                        <img src="/images/code.gif" align="absmiddle" editable="true" id="factorlist<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="@K" nmodule=\"4\" ctrltype=\"3\" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="<%="factorlist["+index+"].hz"%>"/>
                                            </logic:equal>
                                            <logic:notEqual name="element" property="fieldname" value="e0122"> 
                                            <logic:notEqual name="element" property="fieldname" value="e01a1"> 
                                                <!--<img src="/images/code.gif" align="absMiddle" onclick='openCondCodeDialog("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>");'/>-->
                                                 <!-- 使用代码组件控件兼容非IE浏览器 wangb 20180127  -->
                        <img src="/images/code.gif" align="absmiddle" id="factorlist<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="${element.codeid}" inputname='<%="factorlist["+index+"].hzvalue"%>'  valuename="<%="factorlist["+index+"].hz"%>"/>
                                            </logic:notEqual>     
                                            </logic:notEqual>                                                                                                                           
                                         </logic:notEqual>
                                 </logic:notEqual> 
                                 <logic:equal name="element" property="codeid" value="0">
                                   <input type="text" id="${index }hz" name="hz" value="<bean:write name="staticFieldForm" property='<%="factorlist["+index+"].value"%>' />" size="30" maxlength='${element.itemlen}' class="text4" />&nbsp;&nbsp;&nbsp;&nbsp;                               
                                 </logic:equal>         
                         </td>                           
                        </logic:equal> 
                        <!--数据值-->                            
                        <logic:equal name="element" property="fieldtype" value="N">
                          <td align="left" class="RecordRow" nowrap>    
                             <input type="text" id="${index }hz" name="hz" value="<bean:write name="staticFieldForm" property='<%="factorlist["+index+"].value"%>' />" size="30" maxlength='${element.itemlen}' class="text4" />&nbsp;&nbsp;&nbsp;&nbsp; 
                           </td>                           
                         </logic:equal>    
                         <td class="RecordRow" style="border-right: none;" align="center"><img src="/images/del.gif" border=0 title='<bean:message key="button.delete"/>' style="cursor:pointer;" onclick="delete_stat('<%=i+1%>');"/></td>                       
                       </tr>        
                       <%
                       	++i;
                       %>    
                                
                 </logic:iterate>  
                    <tr> 
                      <td align="left" nowrap class="RecordRow" style="border-left: none;border-right: none;" colspan="5"> 
                      <table border="0" align="center"  cellpadding="5" width="100%" cellspacing="0">
                        <tr>
                          <td align="left">
                       <logic:equal name="staticFieldForm" property="history" value="1">
                          <input type="checkbox" name="history" value="1" checked style="margin-left:-5px;">&nbsp;<bean:message key="static.history"/>&nbsp;&nbsp;
                       </logic:equal>
                       <logic:notEqual name="staticFieldForm" property="history" value="1">
                          <input type="checkbox" name="history" value="1" style="margin-left:-5px;">&nbsp;<bean:message key="static.history"/>&nbsp;&nbsp;
                       </logic:notEqual>
                          </td>
                          <td align="right">
                           <html:button styleClass="mybutton" property="b_save" onclick="edit_field();" style="margin-right:-5px;"> 
                           		<bean:message key="button.new.add"/>
	                       </html:button>  
                          </td>
                        </tr>
                      </table>
                    </tr>
                  </table></div>
                 </td>
                </tr>
               <tr>
                <td  colspan="0" valign="top"> 
                 <fieldset style="height:90px;" >
                  <legend><bean:message key="kq.wizard.expre"/></legend>				
                   <table width="586px" border="0" align="center"  cellpadding="0"  cellspacing="0" style="margin:5px 5px;">
                    <tr > 
                      <td width="100%" colspan="2" align="left" nowrap>
                      	<html:text name="staticFieldForm" property="texts" styleClass="text4" style="width:586px"/>
                      	</td>
                      </tr>
                      <tr height="10" width="100%" align="left" nowrap > 
                      <td width="80%" align="left" nowrap align="left" nowrap style="padding-top:5px;">
                       	 <input type="button" name="Submit46" value="(" class="mybutton" onclick="addTxt(this.value)"> 
                         <input type="button" name="Submit462" value=")" class="mybutton" onclick="addTxt(this.value)"> 
                         <input type="button" name="Submit463" value="<bean:message key="general.mess.and"/>" title="*" class="mybutton" onclick="addTxt(this.title)"> 
                         <input type="button" name="Submit464" value="<bean:message key="general.mess.or"/>" title="+" class="mybutton" onclick="addTxt(this.title)">
                       </td>
                       <td width="20%" align="right" nowrap style="padding-top:5px;">
                       	 <input type="button" value="<bean:message key="kq.formula.check"/>" class="mybutton" name="br_incept" onclick="check();" style="margin-right:0px;"> 
                       </td>
                     </tr>                   
                  </table>
                </fieldset>				  					  
					  </td>
				   </tr>                                      
	       </table>	   
      </td>
     </tr>
     <tr><td height="5px"></td></tr>                    
     </table>
</div> 
<table width="100%" border="0" align="center" cellpadding="0" style="margin-top:5px;" cellspacing="0">
<tr>
          <td align="center" style="height: 35">
	       <html:button styleClass="mybutton" property="b_save" onclick="return submits();"> 
                     <bean:message key="button.ok"/>
	       </html:button>  	       
	       <html:button styleClass="mybutton" property="" onclick="openClose();">
            		<bean:message key="button.close"/>
	 	    </html:button>  
          </td>
       </tr> 
</table>   
<div id="date_panel">
   			<select name="date_box" multiple="multiple" size="10"  style="width:200" onchange="setSelectValue();" onclick="setSelectValue();">    
			    <option value="$YRS[10]">年限</option>
			    <option value="当年">当年</option>
			    <option value="当月">当月</option>
			    <option value="当天">当天</option>			    
			    <option value="今天">今天</option>
			    <option value="截止日期">截止日期</option>
                <option value="1992.4.12">1992.4.12</option>	
                <option value="1992.4">1992.4</option>	
                <option value="1992">1992</option>			    
			    <option value="????.??.12">????.??.12</option>
			    <option value="????.4.12">????.4.12</option>
			    <option value="????.4">????.4</option>			    			    		    
            </select>
     </div>
     <div id="selfun">
   			<select name="selfun_box" multiple="multiple" size="10"  style="width:200" onchange="setSelectsValue();" onclick="setSelectsValue();">    
			    <option value="">本单位</option>		    			    		    
            </select>
     </div>
</html:form>
<script language="javascript">
   Element.hide('date_panel');
   Element.hide('selfun');
//兼容非IE浏览器
//关闭弹窗方法  wangb 20180126
function openClose(){
	if(getBrowseVersion()){
		top.close();
	}else{
		parent.window.close();
	}
}
//回调父页面方法 等同windowShowDialog 弹窗返回值    wangb 20180126
function openReturn(return_vo){
	if(getBrowseVersion()){
		top.window.returnValue = return_vo;
	}else{
		parent.opener.openReturn(return_vo,'<%=count%>');
	}
}
</script>

