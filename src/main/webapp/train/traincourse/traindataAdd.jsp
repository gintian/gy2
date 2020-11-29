<%@page import="java.util.Date"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.hjsj.sys.FieldItem"%>
<%@page import="com.hjsj.hrms.valueobject.common.FieldItemView"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.train.traincourse.TrainAddForm" %>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7;">
<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
<script type="text/javascript" src="/components/ckEditor/CKEditor.js"></script>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null){
	userView.getHm().put("fckeditorAccessTime", new Date().getTime());
}
%>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/train/traincourse/trainAdd.js"></script>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<style>
.fixedDiv11
{   border: #C4D8EE 1pt solid;
    overflow:auto; 
    height:expression(document.body.clientHeight-90);
    width:expression(document.body.clientWidth-10); 

}
.RecordRow {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
}
.text4{
	width: 200px;
}
	.m_arrow {
	width: 16px;
	height: 10px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px ;
	padding-left: 3px;
	padding-bottom:2px;
	padding-top:0px;
	cursor: default;
	}
	.selectTd{
	line-height: 6px !important ;
	}
	</style>
<script type="text/javascript">
 var orgid;
    if("${param.oper}"=='saveClose')
    {
        var thevo=new Object();
        thevo.flag="true";
        window.returnValue=thevo;
        window.close();
    }
    
    function myClose()
    {
        if("${param.oper}"=="saveContinue")
        {
            var thevo=new Object();
            thevo.flag="true";
            window.returnValue=thevo;
        }
    }
    function qxFunc()
    {
        if("${param.oper}"=="saveContinue")
        {
            var thevo=new Object();
            thevo.flag="true";
            window.returnValue=thevo;
            window.close();
        }
        else
        window.close();
    }

//培训资源评估－资料名称自定义对话框
    function openMyDialog()
    {
        var r3702 = $('r3702_value');
       if(r3702.value==null || r3702.value=="" || r3702.value==undefined){
            alert("请先选择培训资源类型");//zhangcq 2016-4-15
            return;
              }
        var theurl='/train/traincourse/trainAddTree.jsp?classid=${param.classid}`r3702='+r3702.value;
        var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
        var return_vo= window.showModalDialog(iframe_url, 'mytree_win', 
                    "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");                         
       if(!return_vo)
          return;      
       if(return_vo.flag=="true")
       {   
            var r3705 = $('r3705_value');
            var r3705_viewvalue =  $('r3705_viewvalue');
            r3705.value=return_vo.value;
            r3705_viewvalue.value=return_vo.text;
       }        
    }
    //培训计划
    function openTrainPlanDialog()
    {
        var thecodeurl="/train/traincourse/trainplan.jsp";
        var return_vo= window.showModalDialog(thecodeurl, "", 
                  "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
        if(return_vo!=null){
            var arr = return_vo.split("`");
            if(arr.length!=2)
                return false;
            var r3125 = $('r3125_value');
            var r3125_viewvalue =  $('r3125_viewvalue');
            r3125.value=arr[0];
            r3125_viewvalue.value=arr[1];    
        }
    }
//保存
function save(oper)
{
	var datestr='';
	var dateend='';
	var startdate='';
	var startTime='';
	var enddate='';
	var endTime='';
	//添加修改课时的验证
	if(!modifyClassTime){
		alert("该课程已经排课,修改的课时不能小于已经排了课的课时！");
		return false;
	}
    //添加必填项验证 LiWeichao  2011-09-09 15:46:05
    <logic:iterate  id="element1" name="trainAddForm" property="fieldlist" indexId="index">
    <%FieldItemView abean1=(FieldItemView)pageContext.getAttribute("element1");
        boolean isFillable=abean1.isFillable();%>   
    var itemid='<bean:write name="element1" property="itemid"/>';
    var itemvalue="";
    <logic:equal name="element1" property="codesetid" value="0">
        <logic:equal name="element1" property="itemid" value="r3705">
            itemvalue = document.getElementById(itemid+"_viewvalue").value;
        </logic:equal>
        <logic:equal name="element1" property="itemid" value="r3125">
            itemvalue = document.getElementById(itemid+"_viewvalue").value;
        </logic:equal>
        <logic:notEqual name="element1" property="itemid" value="r3705">
        <logic:notEqual name="element1" property="itemid" value="r3125">
            itemvalue = document.getElementById(itemid).value;
        </logic:notEqual>
        </logic:notEqual>

        <logic:equal name="element1" property="itemtype" value="M">
			if(IsOverStrLength(itemvalue,2000)){
				alert('<bean:write name="element1" property="itemdesc"/>'
						+TRAIN_ROOM_MORE_LENGTH1+2000+TRAIN_ROOM_MORE_LENGTH2+1000+TRAIN_ROOM_MORE_LENGTH3);
				return;
			}
		</logic:equal>
    </logic:equal>
    <logic:notEqual name="element1" property="codesetid" value="0">
        itemvalue = document.getElementById(itemid+"_value").value;
    </logic:notEqual>
    <logic:notEqual name="element1" property="itemid" value="b0110">
    if(<%=isFillable%> && !itemvalue){
        alert('<bean:write name="element1" property="itemdesc"/>不能为空！');
        return;
    }
    </logic:notEqual>
    //举办单位不能为空
    <logic:equal name="element1" property="itemid" value="b0110">
    if(!$F('b0110_value')||$F('b0110_value')=="defined"){
        alert('<bean:write name="element1" property="itemdesc"/>不能为空！');
        return;
    }
    </logic:equal>

    if(itemid.toUpperCase()=="R3113")
	    datestr = document.getElementById(itemid).value;
		 
	if(itemid.toUpperCase()=="R3114")	 
		dateend = document.getElementById(itemid).value;

    if(itemid.toUpperCase()=="R3115"){
        startdate = document.getElementById(itemid).value;
    }

    if(itemid.toUpperCase()=="R3116")	 
     	enddate = document.getElementById(itemid).value;
    </logic:iterate>
    
    //if(!$F('e0122_value')&&$F('e0122_value')==""){
    //  alert("部门不能为空");
    //  return ;
    //}
    var fieldset = $F('fieldset');
    var initValue = $F('initValue');    
    var hideFilds = $F('hideFilds');
    var a_code = $F('a_code');
    var readonlyFilds = $F('readonlyFilds');
    var hideimgids =  $F('hideimgids');
    var hideSaveFlds = $F('hideSaveFlds');
    var isUnUmRela = $F('isUnUmRela');
    
    var start_h=$('start_h');
    var start_m=$('start_m');
    var start_mm=$('start_mm');
    var end_h=$('end_h');
    var end_m=$('end_m');
    var end_mm=$('end_mm');
    
    var paramStr=$URL.encode(fieldset)+"&initValue="+$URL.encode(initValue)+"&hideFilds="+$URL.encode(hideFilds)+"&a_code="+$URL.encode(a_code)+"&readonlyFilds="+$URL.encode(readonlyFilds)+"&hideimgids="+$URL.encode(hideimgids)+"&hideSaveFlds="+$URL.encode(hideSaveFlds)+"&isUnUmRela="+$URL.encode(isUnUmRela);
    
    if(start_h!=null)
    {
        startTime = start_h.value+":"+start_m.value+":"+start_mm.value;
        paramStr+="&r3115_time="+startTime;
    }       
    if(end_h!=null)
    {
        endTime = end_h.value+":"+end_m.value+":"+end_mm.value;
        paramStr+="&r3116_time="+endTime;
    }

    var Datestr = new Date(datestr.replace("-","/"));
    var Dateend = new Date(dateend.replace("-","/"));
    if(Dateend<=Datestr){
    	alert(TRAIN_DATETIME_ERROR1);
    	return;
    }
          
    var startDate = new Date(startdate.replace("-","/")+" "+startTime);
    var endDate = new Date(enddate.replace("-","/")+" "+endTime);
    if(endDate<=startDate){
    	alert(TRAIN_DATETIME_ERROR2);
    	return;
    }
   
    if(endDate<=Dateend){
    	alert(TRAIN_DATETIME_ERROR3);
    	return;
    }
    
    if($F('isAutoHour')!='')//培训班模块 学时是否自动计算标识
        paramStr+='&isAutoHour='+$F('isAutoHour');
    if(fieldset=='r37')//培训资源评估自定义了资料名称输入框，需要这个参数
        paramStr+="&classid="+$URL.encode("${param.classid}");
        
       
    
     //读取编辑器数据
    setEditorValue();    
    if(oper=='saveClose')
        trainAddForm.action="/train/traincourse/traindataAdd.do?b_save=link&oper="+oper+"&fieldset="+paramStr; 
    else 
        trainAddForm.action="/train/traincourse/traindataAdd.do?b_saveContinue=link&addCourse=add&oper="+oper+"&fieldset="+paramStr; 
        
    trainAddForm.target="_self";
    trainAddForm.submit();  
}

var totalClassTime;
var modifyClassTime=true;
function checkIsOK(theElementId,theClassId,addorEdit,obj){
//用来判断是新建 还是编辑 课程
    var rvalue = isNum(obj);
    if(rvalue != undefined)
    	return;
    
	if(addorEdit=="add"){
		return;
	}
	//alert(theElementId);
	//alert(theClassId);
	//拿到课程id就是theClassId
	//拿到现在想修改的课时
	modifyClassTime=true;
	if("r4112"==theElementId||"R4112"==theElementId){
	totalClassTime=document.getElementById(theElementId).value;
	totalClassTime = totalClassTime.length>0?totalClassTime:"0";
	var hashvo=new ParameterSet();
	hashvo.setValue("flag","0"); 
	hashvo.setValue("r4101",theClassId);
	var request=new Request({method:'post',onSuccess:showEditInfo,functionId:'2020020234'},hashvo);
	if(!modifyClassTime){
		obj.value=document.getElementById("courseNum").value;
		obj.focus();
	}
	}else{
		return;
	}
}
var classnum1;
function showEditInfo(outparamters){
	classnum1=outparamters.getValue("classnum1");
	if(parseFloat(classnum1)>parseFloat(totalClassTime)){
		alert("该课程已经排课,您要修改的课时为："+totalClassTime+"不能小于已经排了课的课时："+classnum1+"！");
		modifyClassTime=false;
		return modifyClassTime;
	}
}

function getOrgid(){
	orgid=document.getElementById("b0110_value").value;
	if(orgid==null||orgid.length<1)
		orgid="${trainAddForm.orgparentcode }";
}
function getLesCode(){
	if(typeof(eval("document.all.r4117_value"))== "undefined") {
		return orgid="";
	} else 
		orgid= document.getElementById("r4117_value").value;
}
function onchangcode(){
	document.getElementById("r4118_value").value="";
	document.getElementById("r4118").value="";
}

function setEditorValue(){
	var obj = document.getElementById("r3117");
	if(obj){
		var oEditor = Ext.getCmp("ckeditorid");
        var tmpvalue = oEditor.getHtml();
	    obj.value = tmpvalue;
	}
}
</script>
<%
    TrainAddForm form = (TrainAddForm)session.getAttribute("trainAddForm");
    int len = form.getFieldlist().size(); 
   
 %>
 <hrms:themes></hrms:themes>
 <body onbeforeunload="myClose();">
<html:form action="/train/traincourse/traindataAdd" >    
    <html:hidden name="trainAddForm" property="fieldsetid" styleId="fieldset"/>
    <html:hidden name="trainAddForm" property="initValue" styleId="initValue"/>
    <html:hidden name="trainAddForm" property="hideFilds" styleId="hideFilds"/>
    <html:hidden name="trainAddForm" property="a_code" styleId="a_code"/>
    <html:hidden name="trainAddForm" property="readonlyFilds" styleId="readonlyFilds"/> 
    <html:hidden name="trainAddForm" property="hideimgids" styleId="hideimgids"/>   
    <html:hidden name="trainAddForm" property="hideSaveFlds" styleId="hideSaveFlds"/>   
    <html:hidden name="trainAddForm" property="isUnUmRela" styleId="isUnUmRela"/>   
        
    <html:hidden name="trainAddForm" property="orgparentcode" />
    <html:hidden name="trainAddForm" property="deptparentcode" />
    <input type="hidden" id="isAutoHour" value="${param.isAutoHour}">
    <table id="table" width="750" align="center" border="0" cellpadding="0" cellspacing="0">
        <tr>
            <td valign="top">
            <logic:equal name="trainAddForm" property="fieldsetid" value="r31">
            	<div id="Wdiv" class="fixedDiv11 common_border_color" style="border-top:none;height:560px;"><!-- zhangcq 2016-4-25 弹出窗口div要设置固定宽度 -->
            </logic:equal>
            <logic:notEqual name="trainAddForm" property="fieldsetid" value="r31">
            	<div id="Wdiv" class="fixedDiv11 common_border_color" style="border-top:none;"><!-- zhangcq 2016-4-25 弹出窗口div要设置固定宽度 -->
            </logic:notEqual>
            
                <table id="tableid" width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
                    <tr class="fixedHeaderTr" style="padding: 0px;">
                        <td colspan="4" align="left" class="TableRow" style="border-left: none;">  
                            &nbsp;&nbsp;<bean:write name="trainAddForm" property="titlename" />
                        </td>
                    </tr>
                    <tr class="trDeep">
                    <%int i=0,j=0; %>
                        <logic:iterate  id="element" name="trainAddForm" property="fieldlist" indexId="index">
                        <%
                        FieldItemView abean = (FieldItemView) pageContext.getAttribute("element");
                                    boolean isFillable1 = abean.isFillable();
                        if(i==2){ %>
                            <%if(j%2 == 0){%>
                        </tr><tr class="trShallow">
                        <%}else{%>
                        </tr><tr class="trDeep">
                        <%}i=0;j++;} %>
                            <logic:notEqual name="element" property="itemtype" value="M">
                                <td align="right" class="RecordRow_left" style="border-top: none;border-left: none;width: 100px;" nowrap>
                                 <bean:write name="element" property="itemdesc" filter="true" />
                                </td>
                                <% if(i==1){ %>
								<td align="left" class="RecordRow_right" style="border-top: none;" nowrap >
								<%}else{ %>
								<td align="left" class="RecordRow_inside" style="border-top: none;border-right: 1pt solid;border-right-color:#C4D8EE" nowrap >
								<%} %>
                                    <logic:equal name="element" property="codesetid" value="0">
                                        <logic:notEqual name="element" property="itemtype" value="D">
                                            <logic:notEqual name="element" property="itemtype" value="N">                                                                       
                                                <logic:equal name="element" property="itemid" value="r3705">
                                                    <html:hidden name="trainAddForm" property='<%="fieldlist[" + index + "].value"%>' styleId="r3705_value"/>
                                                    <html:text maxlength="${element.itemlength}" size="20" styleClass="text4" readonly="true" 
                                                            name="trainAddForm" styleId="r3705_viewvalue" property='<%="fieldlist[" + index + "].viewvalue"%>' />                                                                                                                               
                                                    <img id='imgr3705' src="/images/code.gif" onclick='javascript:openMyDialog();' align="absmiddle"/>&nbsp;    
                                                </logic:equal>  
                                                <logic:equal value="r3125" name="element" property="itemid">
                                                    <html:hidden name="trainAddForm" property='<%="fieldlist[" + index + "].value"%>' styleId="r3125_value"/>
                                                    <html:text maxlength="${element.itemlength}" size="20" styleClass="text4" readonly="true" 
                                                            name="trainAddForm" styleId="r3125_viewvalue" property='<%="fieldlist[" + index + "].viewvalue"%>' />                                                                                                                               
                                                    <img id='imgr3705' src="/images/code.gif" onclick='javascript:openTrainPlanDialog();' align="absmiddle"/>&nbsp;
                                                </logic:equal>
                                                <logic:notEqual name="element" property="itemid" value="r3705">
                                                <logic:notEqual name="element" property="itemid" value="r3125">
                                                    <html:text maxlength="${element.itemlength}" size="20" styleClass="text4" 
                                                            name="trainAddForm" styleId="${element.itemid}" property='<%="fieldlist[" + index + "].value"%>' />                                                                                                                                 
                                                </logic:notEqual>   
                                                </logic:notEqual>                                                                                                       
                                           </logic:notEqual>
                                           <logic:equal name="element" property="itemtype" value="N">
                                                <input type="hidden" id="courseNum" value='<bean:write name="trainAddForm" property='<%="fieldlist[" + index + "].value"%>'/>' />
	                                                <logic:equal name="element" property="decimalwidth" value="0">
	                                                    <html:text maxlength="${element.itemlength}" size="20" styleClass="text4" onkeypress="event.returnValue=IsDigit2(this);" onblur="checkIsOK('${element.itemid}','${trainAddForm.priFldValue}','${trainAddForm.addCourse}',this);"  
	                                                        name="trainAddForm" styleId="${element.itemid}" property='<%="fieldlist[" + index + "].value"%>' />                                 
	                                                </logic:equal>                                              
	                                                <logic:notEqual name="element" property="decimalwidth" value="0">
	                                                <bean:define id="dw" name="element" property="decimalwidth"/>
	                                                <bean:define id="itemlength" name="element" property="itemlength"/>
	                                                <% int d = Integer.parseInt(dw.toString());
	                                                   int l = Integer.parseInt(itemlength.toString());
	                                                   String length = d+l+1+"";
	                                                %>
	                                                    <html:text maxlength="<%=length %>" size="20" styleClass="text4" onkeypress="event.returnValue=IsDigit(this);" onblur="checkIsOK('${element.itemid}','${trainAddForm.priFldValue}','${trainAddForm.addCourse}',this);"  
	                                                        name="trainAddForm" styleId="${element.itemid}" property='<%="fieldlist[" + index + "].value"%>' />                                 
	                                                </logic:notEqual>
                                            </logic:equal>
                                        </logic:notEqual>   
                                            <logic:equal name="element" property="itemtype" value="D">  
                                                <logic:notEqual name="element" property="itemid" value="r3115"> 
                                                   <logic:notEqual name="element" property="itemid" value="r3116">  
                                                      <input type="text" name='<%="fieldlist[" + index + "].value"%>' maxlength="${element.itemlength}" size="20"  id="${element.itemid}" extra="editor"  class="textColorWrite"  style="font-size:10pt;text-align:left"
                                                             itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate" value="${element.value}"  onchange=" if(!checkDate(this.value)) { this.value='';}">
                                                   </logic:notEqual>
                                                </logic:notEqual>
                                                <logic:equal name="element" property="itemid" value="r3115">        
                                                    <table width="100%" border='0' cellspacing="0"  cellpadding="0">
                                                        <tr>
                                                            <td width="15">
                                                                 <input type="text" name='<%="fieldlist[" + index + "].value"%>' maxlength="${element.itemlength}" size="20"  id="${element.itemid}" extra="editor"  class="textColorWrite"  style="font-size:10pt;text-align:left"
                                                                    dropDown="dropDownDate" value="${element.value}" onchange=" if(!validate(this,'${element.itemdesc}')) { this.value='';}">
                                                            </td>
                                                        </tr>
                                                        <tr>                                                            
                                                            <td>
                                                                <table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
                                                                    <tr>
                                                                      <td align="right">
                                                                        <bean:message key="hours.minutes.second"/>：
                                                                      </td>
                                                                      <td width="90" nowrap style="background-color:#FFFFFF;"> 
                                                                         <div class="m_frameborder">
                                                                            <input type="text" class="textColorWrite" maxlength="2" size="2" name="intricacy_app_start_time_h" style="width: 20px;" id="start_h" value="00" onfocus="setFocusObj(this,24);" onkeypress="event.returnValue=IsDigit3(this);" onblur="if(this.value!='' && parseInt(this.value)>23){alert('请输入0－23之间的整数！');this.focus();}"><font color="#000000">:</font><input type="text" class="textColorWrite" size="2" maxlength="2" name="intricacy_app_start_time_m" style="width: 20px;" id="start_m" value="00" onfocus="setFocusObj(this,60);"  onkeypress="event.returnValue=IsDigit3(this);" onblur="if(this.value!='' && parseInt(this.value)>59){alert('请输入0－59之间的整数！');this.focus();}"><font color="#000000">:</font><input type="text" class="textColorWrite" maxlength="2" name="intricacy_app_start_time_mm" style="width: 20px;" id="start_mm" value="00" size="2" onfocus="setFocusObj(this,60);"  onkeypress="event.returnValue=IsDigit3(this);" onblur="if(this.value!='' && parseInt(this.value)>59){alert('请输入0－59之间的整数！');this.focus();}">
                                                                          </div>
                                                                      </td>
                                                                      <td>
                                                                        <table border="0" cellspacing="0" cellpadding="0">
                                                                            <tr><td class="selectTd"><button type="button" id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
                                                                            <tr><td class="selectTd"><button type="button" id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
                                                                         </table>
                                                                      </td>
                                                                    </tr>
                                                                 </table> 
                                                            </td>
                                                        </tr>
                                                    </table>    
                                                 </logic:equal> 
                                                 <logic:equal name="element" property="itemid" value="r3116">       
                                                    <table width="100%" border='0' cellspacing="0"  cellpadding="0">
                                                        <tr>
                                                            <td width="15">
                                                                 <input type="text" name='<%="fieldlist[" + index + "].value"%>' maxlength="${element.itemlength}" size="20"  id="${element.itemid}" extra="editor"  class="textColorWrite"  style="font-size:10pt;text-align:left"
                                                                    dropDown="dropDownDate" value="${element.value}" onchange=" if(!validate(this,'${element.itemdesc}')) { this.value='';}">
                                                            </td>
                                                        </tr>
                                                        <tr>                                                
                                                            <td>
                                                                <table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
                                                                   <tr>
                                                                     <td align="right">
                                                                        <bean:message key="hours.minutes.second"/>：
                                                                     </td>
                                                                      <td width="90" nowrap style="background-color:#FFFFFF";> 
                                                                         <div class="m_frameborder">
                                                                           <input type="text"  class="textColorWrite" maxlength="2" size="2" name="intricacy_app_start_time_h" style="width: 20px;" id="end_h" value="00" onfocus="setFocusObj(this,24);" onkeypress="event.returnValue=IsDigit3(this);" onblur="if(this.value!='' && parseInt(this.value)>23){alert('请输入0－23之间的整数！');this.focus();}"><font color="#000000">:</font><input type="text" class="textColorWrite" maxlength="2" size="2" name="intricacy_app_start_time_m" style="width: 20px;" id="end_m" value="00" onfocus="setFocusObj(this,60);"  onkeypress="event.returnValue=IsDigit3(this);" onblur="if(this.value!='' && parseInt(this.value)>59){alert('请输入0－59之间的整数！');this.focus();}"><font color="#000000">:</font><input type="text" class="textColorWrite" size="2" maxlength="2" name="intricacy_app_start_time_mm" style="width: 20px;" id="end_mm" value="00" onfocus="setFocusObj(this,60);"  onkeypress="event.returnValue=IsDigit3(this);" onblur="if(this.value!='' && parseInt(this.value)>59){alert('请输入0－59之间的整数！');this.focus();}">
                                                                          </div>
                                                                      </td>
                                                                      <td>
                                                                        <table border="0" cellspacing="2" cellpadding="0">
                                                                            <tr><td class="selectTd"><button type="button"  id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
                                                                            <tr><td class="selectTd"><button type="button"  id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
                                                                         </table>
                                                                      </td>
                                                                    </tr>
                                                                 </table> 
                                                              </td>
                                                        </tr>
                                                    </table>                                                    
                                                 </logic:equal>                                                                                 
                                           </logic:equal>   
                                    </logic:equal>                                  
                                    <logic:notEqual name="element" property="codesetid" value="0">                                  
                                       <logic:equal name="element" property="itemid" value="b0110">
                                            <html:hidden name="trainAddForm"    property='<%="fieldlist[" + index + "].value"%>' styleId='b0110_value' onchange="changepos('UN',this)" />
                                            <html:text maxlength="${element.itemlength}" size="20" styleClass="text4"  styleId="b0110"  
                                                    name="trainAddForm" property='<%="fieldlist[" + index + "].viewvalue"%>' readonly="true" onchange="fieldcode(this,2)"
                                                       />      
                                            <img src="/images/code.gif" id='img${element.itemid}' onclick='openInputCodeDialogOrgInputPos("${element.codesetid}","<%="fieldlist[" + index + "].viewvalue"%>","${trainAddForm.orgparentcode }","1");' align="absmiddle"/>    
                                        	<font color='red'>*</font>
                                         </logic:equal>         
                                         <logic:equal name="element" property="itemid" value="e0122">
                                            <html:hidden name="trainAddForm"    property='<%="fieldlist[" + index + "].value"%>' styleId='e0122_value' onchange="changepos('UM',this)" />  
                                            <html:text maxlength="${element.itemlength}" size="20" styleClass="text4"  styleId="e0122" 
                                                    name="trainAddForm" property='<%="fieldlist[" + index + "].viewvalue"%>' readonly="true" onchange="fieldcode(this,2)"
                                                       />   
                                            <img src="/images/code.gif" id='img${element.itemid}' onclick='getOrgid();openInputCodeDialogOrgInputPos("${element.codesetid}","<%="fieldlist[" + index + "].viewvalue"%>","${trainAddForm.orgparentcode }","2");' align="absmiddle"/>   
                                         </logic:equal> 
                                         <logic:equal name="element" property="itemid" value="r4117">
                                            <html:hidden name="trainAddForm"    property='<%="fieldlist[" + index + "].value"%>' styleId='r4117_value' onchange="onchangcode();" />  
                                            <html:text maxlength="${element.itemlength}" size="20" styleClass="text4"  styleId="r4117" 
                                                    name="trainAddForm" property='<%="fieldlist[" + index + "].viewvalue"%>' readonly="true"
                                                       />   
                                            <img src="/images/code.gif" id='img${element.itemid}' onclick='openTrainLessonInputCodeDialog("55_1","<%="fieldlist[" + index + "].viewvalue"%>","1","");' align="absmiddle"/>   
                                         </logic:equal>
                                         <logic:equal name="element" property="itemid" value="r4118">
                                            <html:hidden name="trainAddForm"    property='<%="fieldlist[" + index + "].value"%>' styleId='r4118_value' onchange="" />  
                                            <html:text maxlength="${element.itemlength}" size="20" styleClass="text4"  styleId="r4118" 
                                                    name="trainAddForm" property='<%="fieldlist[" + index + "].viewvalue"%>' readonly="true"
                                                       />   
                                            <img src="/images/code.gif" id='img${element.itemid}' onclick='getLesCode();openTrainLessonInputCodeDialog("55_2","<%="fieldlist[" + index + "].viewvalue"%>","1",orgid);' align="absmiddle"/>   
                                         </logic:equal>
                                         <logic:notEqual name="element" property="itemid" value="b0110">
                                            <logic:notEqual name="element" property="itemid" value="e0122">
                                            <logic:notEqual name="element" property="itemid" value="r4117">
                                            <logic:notEqual name="element" property="itemid" value="r4118">
                                            <html:hidden name="trainAddForm" property='<%="fieldlist[" + index + "].value"%>' styleId="${element.itemid}_value"/>  
                                            <html:text maxlength="${element.itemlength}" size="20" styleClass="text4" 
                                                    name="trainAddForm" property='<%="fieldlist[" + index + "].viewvalue"%>' readonly="true" onchange="fieldcode(this,2)"
                                                       styleId="${element.itemid}" />
                                            <img id='img${element.itemid}' src="/images/code.gif" onclick='javascript:openKhTargetCardInputCode("${element.codesetid}","<%="fieldlist[" + index + "].viewvalue"%>");' align="absmiddle"/>
                                            </logic:notEqual>
                                            </logic:notEqual>
                                            </logic:notEqual>
                                        </logic:notEqual>
                                    </logic:notEqual>                                   
                                            <%i++; 
                                                if (isFillable1) {
                                            %> 
                                            <logic:notEqual name="element" property="itemid" value="b0110">
                                            <font color='red'>*</font>
                                            </logic:notEqual>
                                            <%
                                        }
                                     %>
                                </td>
                                <%if(index<len-1) { %>
                                <logic:equal name="trainAddForm" property='<%="fieldlist[" + (index+1) + "].itemtype"%>' value="M">
                                    <%if(i<2){ %>
                                    <td align="left" class="RecordRow_inside" style="border-top: none;" nowrap >&nbsp;</td>
									<td align="left" class="RecordRow_right" style="border-top: none;" nowrap >&nbsp;</td>
                                    <%i++; }%>
                                    
                                </logic:equal>
                                <%} else if(index==len-1){%>
                                    <%if(i<2){ %>
                                    <td align="left" class="RecordRow_inside" style="border-top: none;" nowrap >&nbsp;</td>
									<td align="left" class="RecordRow_right" style="border-top: none;" nowrap >&nbsp;</td>
                                    <%i++; }%>      
                                <%} %>
                            </logic:notEqual>
                            <logic:equal name="element" property="itemtype" value="M">
                                <td align="right" class="RecordRow_left" style="border-top: none;border-left: none;" nowrap  valign="top" >
                                    <bean:write name="element" property="itemdesc" filter="true" />
                                </td>
                                <td align="left" class="RecordRow_right" style="border-top: none;" nowrap  colspan="3">
                                <logic:equal name="element" property="itemid" value="r3117">
                                
                                	<html:hidden name="trainAddForm"  styleId="${element.itemid }"
													property='<%="fieldlist[" + index  + "].value"%>'/>
									<div id="div_r3117" style="height: 300px;width:100%;"></div>
										<script type="text/javascript">
											  var oldInputs = document.getElementById('${element.itemid }'); 		
								              var CKEditor = Ext.create('EHR.ckEditor.CKEditor',{
							                      id:'ckeditorid',
							                      functionType:"standard",         
							                      width:'100%',
							                      height:'100%'      
							                    });  
							                
							                 var Panel = Ext.create('Ext.panel.Panel', {
							                     id:'ckeditorPanel',             
							                     border: false,
							                     width: '100%',
							                     height: 300, 
							                     items: [CKEditor],               
							                     renderTo: "div_r3117"
							                    });
							                 
							                var oEditor = Ext.getCmp("ckeditorid");
							                oEditor.setValue(oldInputs.value);
								            </script>
                                 </logic:equal>
                                <logic:notEqual name="element" property="itemid" value="r3117">
                                    <html:textarea name="trainAddForm" styleId="${element.itemid}"
                                        property='<%="fieldlist[" + index + "].value"%>'
                                        cols="67" rows="4" styleClass="textboxMul"></html:textarea>
                                 </logic:notEqual>
                                    <%
                                                if (isFillable1) {
                                            %> 
                                            <logic:notEqual name="element" property="itemid" value="b0110">
                                            <font color='red'>*</font>
                                            </logic:notEqual>
                                            <%
                                        }
                                     %>
                                </td>
                                <%i=2; %>
                            </logic:equal>
                        </logic:iterate>
                        </tr>
        </table>
    </div>
</td></tr>
<tr><td>
                <table width='100%' align='center'>
                    <tr>
                        <td></td>
                    </tr>
                    <tr>
                        <td align='center' style="padding-left: 3px;">
                            <input type='button' value='<bean:message key='button.save' />' class="mybutton" onclick='save("saveClose");'>
                            <logic:equal name="trainAddForm" property="chkflag" value="add">
                            <input type="button" value="<bean:message key='button.save'/>&<bean:message key='edit_report.continue'/>" onclick='save("saveContinue");' Class="mybutton">
                            </logic:equal>
                            <input type="button" class="mybutton" value="<bean:message key='button.cancel'/>" onClick="qxFunc()">                           
                        </td>
                    </tr>
                </table>
</td></tr>
</table>
</html:form>
<script>        
    //设置只读的文本框
    <logic:iterate  id="element1" name="trainAddForm" property="itemidarr" indexId="index">
        var itemid = '<bean:write name="element1" property="itemid" filter="true" />';
        obj = $(itemid);
        obj.readOnly="true";
        obj.className="textColorRead";
    </logic:iterate>
    
    //设置要隐藏的文本框旁边的图片
    <logic:iterate  id="element2" name="trainAddForm" property="hidePics" indexId="index">
        var imgid = '<bean:write name="element2" property="imgid" filter="true" />';
        obj = document.getElementById(imgid);//$(imgid);
        if(obj)
            obj.style.display="none";
    </logic:iterate>
    //修改模式时候 初始化培训班开始和结束时间的 时分秒部分
    <logic:notEqual name="trainAddForm" property="chkflag" value="add">
        var r3115_time = '${trainAddForm.r3115_time}';
        var r3116_time = '${trainAddForm.r3116_time}';
        var start_h=$('start_h');
        var start_m=$('start_m');
        var start_mm=$('start_mm');
        var end_h=$('end_h');
        var end_m=$('end_m');
        var end_mm=$('end_mm');
        if(start_h!=null)
        {
            start_h.value=r3115_time.substring(0,2);
            start_m.value=r3115_time.substring(3,5);
            start_mm.value=r3115_time.substring(6,8);
        }
        if(end_h!=null)
        {
            end_h.value=r3116_time.substring(0,2);
            end_m.value=r3116_time.substring(3,5);
            end_mm.value=r3116_time.substring(6,8);
        }
    </logic:notEqual>

</script>
</body>