<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<style type="text/css"> 
.btn1 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 5px;
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 5px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 5px; 
 PADDING-BOTTOM: 5px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn2 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 8px; 
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 8px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid;  
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 5px; 
 PADDING-BOTTOM: 5px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn3 {
BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 5px;
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 5px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 22px; 
 PADDING-BOTTOM: 22px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn4 {
 BORDER-RIGHT: #C0C0C0 1px solid;
 BORDER-TOP: #C0C0C0 1px solid; 
 PADDING-LEFT: 0px; FONT-SIZE: 12px; 
 BORDER-LEFT: #C0C0C0 1px solid; 
 COLOR: #808080; 
 PADDING-TOP: 0px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #C0C0C0 1px solid;
  border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
</style>
<html:form action="/gz/gz_analyse/newcond">
<table width="100%" border="0" align="center">
  <tr> 
    <td width="80%" align="center">
    	<table width="100%" border="0">
        <tr> 
          <td height="20">筛选条件列表：</td>
        </tr>
        <tr> 
          <td height="250" align="center">
          	<hrms:optioncollection name="gzFilterForm" property="seivelist" collection="list"/>
				<html:select name="gzFilterForm" property="seiveid" multiple="multiple" style="height:250px;width:100%;font-size:9pt">
			 		<html:options collection="list" property="dataValue" labelProperty="dataName" />
				</html:select>
          </td>
        </tr>
      </table>
    </td>
    <td width="20%"  align="center">
    	<table width="100%" border="0">
        <tr> 
          <td height="120">&nbsp;</td>
        </tr>
        <tr> 
          <td height="30" align="center">
          	<input type="button" name="button1" value="新建" onclick="newFilter();"  Class="mybutton"> 
          </td>
        </tr>
        <tr> 
          <td height="30" align="center">
          	<input type="button" name="button2" value="删除" onclick="setSelectCond('alert');"  Class="mybutton"> 
          </td>
        </tr>
        <tr> 
          <td height="30" align="center">
          	<input type="button" name="button3" value="修改" onclick="alertFilter();"  Class="mybutton"> 
          </td>
        </tr>
        <tr> 
          <td height="30" align="center">
          	<input type="button" name="button4" value="关闭" onclick="parent.window.close();"  Class="mybutton"> 
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</html:form>
<script language="JavaScript">
function newFilter(){
	var thecodeurl = "/gz/gz_analyse/filter.do?b_query=link`tabid=${gzFilterForm.tabID}`flag=add";
	
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl); 
		var config = {
	    width:800,
	    height:500,
	    type:'2',
	    id:'newFilter_win'
	}
	var ss = modalDialog.showModalDialogs(iframe_url,"newFilter_win",config, setSelectCond);
				
   	/* var return_vo=window.showModalDialog(thecodeurl,"window2",
   				"dialogWidth:750px;dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no"); */
   	/* if(return_vo!=null){
   		setSelectCond('add');
   	} */
   			
}
function alertFilter(){
	var id=checkitemid();
	if(!id) {
		alert("请选中左侧列表，如没有请新建！");
		return;
	}
	var thecodeurl = "/gz/gz_analyse/filter.do?b_query=link`flag=alert`tabid=${gzFilterForm.tabID}`seiveid="+id;
	
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl); 
	var config = {
	    width:800,
	    height:500,
	    type:'2',
	    id:'alertFilter_win'
	}
	var ss = modalDialog.showModalDialogs(iframe_url,"alertFilter_win",config,"");
		
   	/* var return_vo=window.showModalDialog(thecodeurl,"window2",
   				"dialogWidth:750px;dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no"); */		
}
function checkitemid(){
	var fielditemid;
	var lefts= document.getElementsByTagName("select");
	var left_vo = lefts[0];
	if(lefts==null){
		return;
	}else{
		for(var i=0;i<left_vo.options.length;i++){
			if(left_vo.options[i].selected){
				fielditemid =left_vo.options[i].value;
				break;
			}
		}
		if(fielditemid!=null){
			return fielditemid;
		}else{
			return;
		}
	}
}
function setSelectCond(flag){
	var hashvo=new ParameterSet();
	if(flag=='alert'){
		var id=checkitemid();
		if(id) {
			if(!confirm('确认删除选择的项目？'))
				return ;
			hashvo.setValue("seiveid",id);
		}else {
			alert("请选中左侧列表，如没有请新建！");
			return;
		}
		
	}
	hashvo.setValue("flag",flag);
	hashvo.setValue("tabid",'${gzFilterForm.tabID}');
	var request=new Request({method:'post',asynchronous:false,onSuccess:setSelectCondOk,functionId:'0521010015'},hashvo);	
}
function setSelectCondOk(outparamters){
	var fieldlist = outparamters.getValue("seivelist");
	if(parent.window.opener) {
		parent.window.opener.setSelectCond('${gzFilterForm.tabID}');
	}
	AjaxBind.bind(gzFilterForm.seiveid,fieldlist);
}
</script>