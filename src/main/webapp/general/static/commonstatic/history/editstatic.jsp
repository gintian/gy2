<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	// 在标题栏显示当前用户和日期 2004-5-10 
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
	String count = request.getParameter("count");
%>
<script language="JavaScript" src="/js/function.js"></script>
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
	height:450px!important;
	height:expression(document.body.clientHeight-100);
	width:610px!important;
	width:expression(document.body.clientWidth); 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
</STYLE>
<link href="../../css/css1.css" rel="stylesheet" type="text/css">
<%
  int i=0;
%>
<script language="javascript">
	
		   var date_desc;
  
   function setSelectValue()
   {
     if(date_desc)
     {
       date_desc.value=$F('date_box');
       Element.hide('date_panel');   
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
	   /*var vosId= document.getElementsByName('hz');
	   var vosoper= document.getElementsByName('oper');
	   var vosFieldname=document.getElementsByName("itemid");*/
	   var vosId=new Array();
	   var vosoper= new Array();
	   var vosFieldname= new Array();
	   for(var i=0;i<ccc;i++){
	   	if(document.getElementsByName('factorlist['+i+'].value').length==0)//过滤没有获取统计条件
	   		continue;
	   	vosId[i]=(document.getElementsByName('factorlist['+i+'].value')[0]);
	   	vosoper[i]=(document.getElementsByName('factorlist['+i+'].oper')[0]);
	   	vosFieldname[i]=(document.getElementsByName(i+'feildname')[0]);
	   }  
	   var arr=new Array();	   
	   if(vosId&&vosoper&&vosFieldname&&vosFieldname.length==vosId.length&&vosId.length==vosoper.length)
	   {
	       for(var r=0;r<vosId.length;r++)
	       {
	              var objId=vosId[r];
	              if(!objId)
	            	continue;
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
	    //过滤删除的条件  wangb bug 38812 20180713 
	   var arr_new = new Array();
	   for(var i = 0 ; i < arr.length ; i++){
	   		if(!arr[i])
	   			continue;
	   		arr_new.push(arr[i]);
	   }	
	   hashvo.setValue("texts",expr);
//	   hashvo.setValue("sno",ccc);
	   hashvo.setValue("sno",arr_new.length);
       hashvo.setValue("type","${staticFieldForm.infor_Flag}");
	   hashvo.setDataType("sno","string");	
	   hashvo.setValue("arr",arr_new);
       var  request=new Request({onSuccess:editsubmit,functionId:'05301010010'},hashvo);
   	  
}
function editsubmit(outparamters)
{
    var flag=outparamters.getValue("flag");
	if(flag=="true")
	{
	   if(ccc>0)
	   {
	      /*var vosId= document.getElementsByName('hz');
	      var vosoper= document.getElementsByName('oper');
	      var vosFieldname=document.getElementsByName("itemid");  */
	      var vosId=new Array();
	   var vosoper= new Array();
	   var vosFieldname= new Array();
	   for(var i=0;i<ccc;i++){
	   	if(document.getElementsByName('factorlist['+i+'].value').length==0)//过滤没有获取统计条件
	   		continue;
	   	vosId[i]=(document.getElementsByName('factorlist['+i+'].value')[0]);
	   	vosoper[i]=(document.getElementsByName('factorlist['+i+'].oper')[0]);
	   	vosFieldname[i]=(document.getElementsByName(i+'feildname')[0]);
	   }
	       
	      if(vosId&&vosoper&&vosFieldname&&vosFieldname.length==vosId.length&&vosId.length==vosoper.length)
	      {
	           var arr=new Array();
	           for(var r=0;r<vosId.length;r++)
	           {
	              var objId=vosId[r];
	              if(!objId)
	            	continue;
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
               //vost= document.getElementsByName("history");
               //objt=vost[0];
               var history="0";
               /*if(objt.checked==true)
               {
                  history="1";
               }*/
                 //过滤删除的条件  wangb bug 38812 20180713 
	   			var arr_new = new Array();
	   			for(var i = 0 ; i < arr.length ; i++){
	   				if(!arr[i])
	   					continue;
	   				arr_new.push(arr[i]);
	   			}	
               hashvo.setValue("history",history);
               hashvo.setValue("editid","${staticFieldForm.editid}");
               hashvo.setValue("statid","${staticFieldForm.statid}");
               hashvo.setValue("flagtype","${staticFieldForm.opflag}");
               hashvo.setValue("arr",arr_new);
               var request=new Request({onSuccess:submitRe,functionId:'11080204096'},hashvo);
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
      if(getBrowseVersion()){
	      window.returnValue=vo;
	      window.close();
      }else{
      	  parent.opener.openReturn(vo,<%=count%>);
      	  //top.returnValue=vo;
	      top.close();
      }
  }else
  {
     alert("操作失败！")
  }
}
function check()
{
       //IE 11非兼容模式 不识别$F  wangbs
	   var expr=document.getElementsByName("texts")[0].value;
	   // var expr=$F('texts');
	   if(expr==null||expr=="")
       {
         alert("表达式不能为空！");
         return false;
       }      
	   var hashvo=new ParameterSet();
	   /*var vosId= document.getElementsByName('hz');
	   var vosoper= document.getElementsByName('oper');
	   var vosFieldname=document.getElementsByName("itemid");  */
	   var vosId=new Array();
	   var vosoper= new Array();
	   var vosFieldname= new Array();
	   for(var i=0;i<ccc;i++){
	   	if(document.getElementsByName('factorlist['+i+'].value').length==0)//过滤没有获取统计条件
	   		continue;
	   	vosId[i]=(document.getElementsByName('factorlist['+i+'].value')[0]);
	   	vosoper[i]=(document.getElementsByName('factorlist['+i+'].oper')[0]);
	   	vosFieldname[i]=(document.getElementsByName(i+'feildname')[0]);
	   }
	   var arr=new Array();
	   if(vosId&&vosoper&&vosFieldname&&vosFieldname.length==vosId.length&&vosId.length==vosoper.length)
	   {
	       for(var r=0;r<vosId.length;r++)
	       {
	              var objId=vosId[r];
	              if(!objId)//过滤没有获取统计条件
	              	continue;
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
	   //过滤删除的条件  wangb bug 38812 20180713 
	   var arr_new = new Array();
	   for(var i = 0 ; i < arr.length ; i++){
	   		if(!arr[i])
	   			continue;
	   		arr_new.push(arr[i]);
	   }	                 
	   hashvo.setValue("texts",expr);
	   hashvo.setValue("sno",ccc);
       hashvo.setValue("type","${staticFieldForm.infor_Flag}");
       hashvo.setValue("arr",arr_new);
	   hashvo.setDataType("sno","string");	
       var  request=new Request({onSuccess:showSelect,functionId:'05301010010'},hashvo);

}

 function addTxt(strtxt)
	{
        //IE 11非兼容模式 不识别$  wangbs
		var expr_editor=document.getElementsByName("texts")[0];
		// var expr_editor=$('texts');
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
    var theurl="/general/static/commonstatic/editstatic.do?br_field=link&infor_Flag=h";
    if(getBrowseVersion()){
    	var return_vo= window.showModalDialog(theurl,0, 
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:480px; dialogHeight:360px;resizable:no;center:yes;scroll:no;status:no");
    	if(return_vo&&return_vo.tran_flag=="1")
    	{
       		var list=return_vo.list;       
       		var hashvo=new ParameterSet();
	   		hashvo.setValue("list",list);	
       		var  request=new Request({onSuccess:addSelect,functionId:'11080204052'},hashvo);
    	}
    }else{
   		 //获得窗口的垂直位置 
         var iTop = (window.screen.availHeight - 30 - dh) / 2; 
         //获得窗口的水平位置 
         var iLeft = (window.screen.availWidth - 10 - dw) / 2; 
         window.open(theurl, '', 'height='+(dh+10)+'px,width=' + dw + 'px,top=' + iTop + ',left=' + iLeft + ',status=no,toolbar=no,menubar=no,location=no,resizable=no,scrollbars=0,titlebar=no'); 
    }
    
}
function openReturn(return_vo){
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
  	    var cell_1=""+list[s].hz+"";
  	    cell_1=cell_1+"<input type=\"hidden\" name=\""+(index-1)+"feildname\" id='itemid' value=\""+list[s].fieldname+"\">";
  	    cell_1=cell_1+"";
  	    var cell_2="<td nowrap>";
  	    cell_2=cell_2+"<select name=\"factorlist["+(index-1)+"].oper\" id='oper' size=\"1\">";
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
  	    var cell_3="<td align=\"center\"  class=\"RecordRow\" nowrap>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";  	    
  	    if(fieldtype=="D")
  	    {
  	       cell_3=cell_3+"<input type=\"text\" name=\"factorlist["+(index-1)+"].value\" maxlength=\"10\" id=\"hz\" size=\"30\" value=\"\" ondblclick=\"showDateSelectBox(this);\" class=\"text4\">";
  	    }else if(fieldtype=="M")
  	    {
  	       cell_3=cell_3+"<input type=\"text\" name=\"factorlist["+(index-1)+"].value\" maxlength=\"10\" id=\"hz\" size=\"30\" value=\"\" maxlength='"+itemlen+"' class=\"text4\">";
  	       
  	    }else if(fieldtype=="N")
  	    {
  	        cell_3=cell_3+"<input type=\"text\" name=\"factorlist["+(index-1)+"].value\" maxlength=\"10\" id=\"hz\" size=\"30\" value=\"\" maxlength='"+itemlen+"' class=\"text4\">"; 
  	    }else if(fieldtype=="A")
  	    {
  	       if(codesetid!="0")
  	       {
  	                                     
              cell_3=cell_3+"<input type=\"hidden\" name=\"factorlist["+(index-1)+"].value\" id=\"hz\"  value=\"\" class=\"text4\">";               
  	          cell_3=cell_3+"<input type=\"text\" name=\"factorlist["+(index-1)+"].hzvalue\" maxlength=\"10\" size=\"30\" value=\"\" maxlength='50' class=\"text4\" onchange=\"fieldcode(this,1)\">";
  	          cell_3=cell_3+" <img src=\"/images/code.gif\" align=\"middle\" onclick='openCondCodeDialog(\""+codesetid+"\",\"factorlist["+(index-1)+"].hzvalue\");'/>";
  	       }else
  	       {
  	          cell_3=cell_3+"<input type=\"text\" name=\"factorlist["+(index-1)+"].value\" id=\"hz\" maxlength=\"10\" size=\"30\" value=\"\" maxlength='"+itemlen+"' class=\"text4\">";
  	       }  	    
  	    }else
  	    {
  	        cell_3=cell_3+"";
  	    }  	    
  	    cell_3=cell_3+"</td>";  
  	    var cell_4="<img src=\"/images/del.gif\" align=\"middle\" border=0 title='<bean:message key="button.delete"/>' style=\"cursor:hand;\" onclick=\"delete_stat('"+index+"');\"/>"	    
  	    for (var i=0;i<5;i++)
        { 
             var newCell=tRow.insertCell(i);
             switch (i) 
             { 
               case 0 : newCell.innerHTML=cell_0;newCell.style.cssText="text-align:center;";newCell.className="RecordRow";break; 
               case 1 : newCell.innerHTML=cell_1;newCell.style.cssText="text-align:center;";newCell.className="RecordRow";break; 
               case 2 : newCell.innerHTML=cell_2;newCell.style.cssText="text-align:center;";newCell.className="RecordRow";break; 
               case 3 : newCell.innerHTML=cell_3;newCell.style.cssText="text-align:left;";newCell.className="RecordRow";break; 
               case 4 : newCell.innerHTML=cell_4;newCell.style.cssText="text-align:left;";newCell.className="RecordRow";break;  
             } 
         
        }  
         ccc++; 	
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
          //ccc--;不减少条件数  wangb 20180713 bug 38812          
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
  	          var cell_4="<img src=\"/images/del.gif\" align=\"middle\" border=0 title='<bean:message key="button.delete"/>' style=\"cursor:hand;\" onclick=\"delete_stat('"+i+"');\"/>"	    
  	          tRow.cells[4].innerHTML=cell_4;
  	          //alert(tRow.cells[0].innerHTML);
  	      }
       }
     }      
}
</script>
<hrms:themes />
<%if("hcm".equalsIgnoreCase(bosflag)){ %>
<style>
.fixedDiv2 {
	height:470px!important;
	*height:expression(document.body.clientHeight-85)!important;
}
.TableRow_top{
	padding:0 5px 0 5px;
}
.TEXT4{
	height:25px;
}
</style>
<%}else{ %>
<style>
.fixedDiv2 {
	height:470px!important;
	*height:expression(document.body.clientHeight-85)!important;
}
.TableRow_top{
	padding:0 5px 0 5px;
}
.TEXT4{
}
</style>
<%} %>
<html:form action="/general/static/commonstatic/editstatic/history" style="padding-left:5px;">
<html:hidden property="infor_Flag"/>
<html:hidden name="staticFieldForm" property="mess"/>
<div id="tbl-container"  class="fixedDiv2" style="overflow-x: hidden;">
 <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
     <tr class=fixedHeaderTr>
      <td align=left class="TableRow_top">     
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
        <table border="0"  cellspacing="0" width="588px"  cellpadding="0" align="center" class="ListTable" style="margin-top:5px;"> 
         <tr>
         <td>
           <table  border="0"  cellspacing="0" width="100%" cellpadding="0">
              <tr>
                  <td>统计条件名称&nbsp;
                    <html:text name="staticFieldForm" property='titles' size="30" maxlength='15' styleClass="text4"/> 
                    <html:hidden name="staticFieldForm" property='editid' styleClass="text4"/>     
                    <html:hidden name="staticFieldForm" property='statid' styleClass="text4"/>                    
                  </td>
                 </tr>
                </table>
                </td>
            </tr>      
          
            <tr><td height="5">            
            </td></tr>          
            <tr>
            <td align="center"> <!-- 【8797】员工管理-查询浏览-历史时点-统计分析（统计条件设置页面样式与其他统计条件设置页面样式不一样） jingq add 2015.04.16 -->
            <div style="border:1px solid;height:260px;overflow:auto;" class="common_border_color">
             <table border="0"  cellspacing="0" width="100%" class="ListTable1"  cellpadding="0" align="center" id="tab">
              <tr> 
               <td align="center"  class="TableRow" height="30" style="border-top:none;border-left:none;"><bean:message key="label.query.number"/></td>
               <td align="center"  class="TableRow" height="30" style="border-top:none;"><bean:message key="static.target"/></td>
               <td align="center"  class="TableRow" height="30" style="border-top:none;"><bean:message key="static.relation"/></td>
               <td align="center"  class="TableRow" style="border-top:none;">
                 <table  border="0"  cellspacing="0" width="100%" cellpadding="0">
                  <tr>
                    <td align="center" >
                      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                      <b>
                      <bean:message key="static.title"/>
                      </b>
                      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    </td>
                  </tr>
                 </table>
              
               </td>
               <td align="center"  class="TableRow" style="border-top:none;border-right:none;">
                 &nbsp;
               </td>               
              </tr>
               <logic:iterate id="element" name="staticFieldForm"  property="factorlist" indexId="index"> 
                 <script language="javascript"> 
               			ccc++
               	</script> 
               	<style><%--  员工管理，历史时点，修改统计条件页面样式不对 jingq upd 2014.10.13
					.RecordRow {
						border-color:#93C566;
						color: #93C566;
					}
					.text4{
						border-color:#93C566;
						color: #93C566;
					}
				--%></style>                  
                 <tr id="tr<%=i+1%>">       
                   <td align="center" class="RecordRow" nowrap style="border-left:none;">
				            <%=i+1%>　	
                    </td>                  
                   <td align="center" class="RecordRow" nowrap >
                     <bean:write name="element" property="hz" />
                     <input type="hidden" name="${index}feildname" id='itemid' value="<bean:write name="element" property="fieldname" />" >
                    </td>  
                   <td align="center" class="RecordRow" nowrap >
                 	  <hrms:optioncollection name="staticFieldForm" property="operlist" collection="list"/>
                     <html:select name="staticFieldForm" property='<%="factorlist["+index+"].oper"%>' styleId="oper" size="1">
                       <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                      </html:select>
                     </td>                                                  
                      <!--日期型 -->                            
                     <logic:equal name="element" property="fieldtype" value="D">
                      <td align="left" class="RecordRow" nowrap>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                          <html:text name="staticFieldForm" property='<%="factorlist["+index+"].value"%>' styleId="hz" size="30" maxlength="10" styleClass="text4" ondblclick="showDateSelectBox(this);" />
                       </td>                           
                      </logic:equal>
                       <!--备注型 -->                              
                      <logic:equal name="element" property="fieldtype" value="M">
                        <td align="left" class="RecordRow" nowrap>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                 <html:text name="staticFieldForm" property='<%="factorlist["+index+"].value"%>' styleId="hz"  size="30" maxlength='<%="factorlist["+index+"].itemlen"%>' styleClass="text4"/>
                         </td>                
                       </logic:equal>
                        <!--字符型 -->                                                  
                       <logic:equal name="element" property="fieldtype" value="A">
                         <td align="left" class="RecordRow" nowrap>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                 <logic:notEqual name="element" property="codeid" value="0">
                                  <html:text name="staticFieldForm" property='<%="factorlist["+index+"].hzvalue"%>' size="30" maxlength="50" styleClass="text4" onchange="fieldcode(this,1)"/>
                                  <img src="/images/code.gif" align="absmiddle" onclick='openCondCodeDialog("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>");'/>
                                  <html:hidden name="staticFieldForm" property='<%="factorlist["+index+"].value"%>' styleId="hz"  styleClass="text4"/>  
                                 </logic:notEqual> 
                                 <logic:equal name="element" property="codeid" value="0">
                                   <html:text name="staticFieldForm" property='<%="factorlist["+index+"].value"%>' styleId="hz"  size="30" maxlength="${element.itemlen}" styleClass="text4"/>                               
                                 </logic:equal>         
                         </td>                           
                        </logic:equal> 
                        <!--数据值-->                            
                        <logic:equal name="element" property="fieldtype" value="N">
                          <td align="left" class="RecordRow" nowrap>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                 <html:text name="staticFieldForm" property='<%="factorlist["+index+"].value"%>' styleId="hz"  size="30" maxlength="${element.itemlen}" styleClass="text4"/>
                           </td>                           
                         </logic:equal>    
                         <td class="RecordRow" align="left" style="border-right:none;"><img src="/images/del.gif" border=0 title='<bean:message key="button.delete"/>' style="cursor:hand;" onclick="delete_stat('<%=i+1%>');"/></td>                       
                       </tr>        
                       <%
                       	++i;
                       %>    
                                
                 </logic:iterate>  
                    <tr> 
                      <td align="left" nowrap class="RecordRow" colspan="5" style="border-left:none;border-right:none;"> 
                      <table border="0" align="center" cellpadding="5" width="100%" cellspacing="0">
                        <tr>
                          <td align="right" >
                           <html:button styleClass="mybutton" property="b_save" onclick="edit_field();" style="margin-right:-5px;"> 
                           <bean:message key="button.new.add"/>
	                       </html:button>  
                          </td>
                        </tr>
                      </table>
                      </td>
                    </tr>
                  </table>
                  </div>
                 </td>
                </tr>
               <tr>
                <td  colspan="0" valign="top"> 
                 <fieldset style="height: 90px">
                  <legend><bean:message key="kq.wizard.expre"/></legend>				
                   <table width="576px" border="0" align="center"  cellpadding="0"  cellspacing="0" class="ListTable1">
                    <tr > 
                      <td width="100%" colspan="2" nowrap class="RecordRow_lrt">
                      	<html:text name="staticFieldForm" property="texts" styleClass="text4" style="width:564px"/>
                      	</td>
                      </tr>
                      <tr height="10" width="100%" nowrap class="RecordRow"> 
                      <td width="80%" align="left" nowrap class="RecordRowTop0" style="border-right:0pt;">
                       	 <input type="button" name="Submit46" value="(" class="mybutton" onclick="addTxt(this.value)"> 
                         <input type="button" name="Submit462" value=")" class="mybutton" onclick="addTxt(this.value)"> 
                         <input type="button" name="Submit463" value="<bean:message key="general.mess.and"/>" title="*" class="mybutton" onclick="addTxt(this.title)"> 
                         <input type="button" name="Submit464" value="<bean:message key="general.mess.or"/>" title="+" class="mybutton" onclick="addTxt(this.title)">
                       </td>
                      <td width="20%" align="right" nowrap class="RecordRowTop0" style="border-left:0pt;">
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
     </table>
</div> 
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
<tr>
          <td align="center" style="height: 35">
	       <html:button styleClass="mybutton" property="b_save" onclick="return submits();"> 
                     <bean:message key="button.ok"/>
	       </html:button>  	       
	       <html:button styleClass="mybutton" property="" onclick="top.close();">
            		<bean:message key="button.close"/>
	 	    </html:button>  
          </td>
       </tr> 
</table>   
</html:form>
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
<script language="javascript">
   Element.hide('date_panel');
</script>

