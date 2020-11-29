<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.hjsj.sys.DataDictionary,com.hrms.hjsj.sys.FieldItem,com.hjsj.hrms.actionform.general.sprelationmap.RelationMapForm"%>

<%
   FieldItem b0110=DataDictionary.getFieldItem("b0110");
   FieldItem e0122=DataDictionary.getFieldItem("e0122");
   FieldItem e01a1=DataDictionary.getFieldItem("e01a1");
   FieldItem a0101=DataDictionary.getFieldItem("a0101");
   String b0110desc="单位";
   if(b0110!=null)
       b0110desc=b0110.getItemdesc();
   String e0122desc="部门";
   if(e0122!=null)
      e0122desc=e0122.getItemdesc();
   String e01a1desc="岗位";
   if(e01a1!=null)
       e01a1desc=e01a1.getItemdesc();
   String a0101desc="姓名";
   if(a0101!=null)
      a0101desc=a0101.getItemdesc();
   // 因为后台传值形式如'部门名/编号'，所以要进行截取字符串 
   RelationMapForm relationMapForm = (RelationMapForm) session.getAttribute("relationMapForm");
   String unit = relationMapForm.getUnit();//单位/单位编号
   String unitName = unit!=null?unit.substring(0,unit.indexOf("/")):"";
   String unitCode = unit!=null?unit.substring(unit.indexOf("/")+1):"";
   String department = relationMapForm.getDepartment();//部门/部门编号
   String departmentName = department!=null?department.substring(0,department.indexOf("/")):"";
   String departmentCode = department!=null?department.substring(department.indexOf("/")+1):"";
   String position = relationMapForm.getPosition();//岗位/岗位编号
   String positionName = position!=null?position.substring(0,position.indexOf("/")):"";
   String positionCode = position!=null?position.substring(position.indexOf("/")+1):"";
   UserView userView=(UserView)session.getAttribute(WebConstant.userView);
   String busi_org_dept = userView.getUnitIdByBusi("4");
 %>
<style>
<!--
.div2
{
 overflow:auto; 
 width: 280px;
 height: 200px;
 line-height:15px; 
 border-width:1px; 
 border-style: groove;
 border-width :thin ;
}
-->
</style>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/general/sprelationmap/relationMap.js"></script>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript">
 var busiorgdept="<%=busi_org_dept%>";

function getemp()
  {
    var targetobj,hiddenobj;
    var currnode=Global.selectedItem;	
    if(currnode==null)
    	return;  
    var id = currnode.uid;
    var text=currnode.text;
    if(id.indexOf("root")!=-1)//id.indexOf("UN")!=-1||id.indexOf("UM")!=-1||id.indexOf("@K")!=-1||
      return;  
    var no = new Option();
    no.value=id;
    no.text=text;
    var vos= document.getElementsByName('downId');
    var emp_vo=vos[0];
    var isC=true;
    for(i=0;i<emp_vo.options.length;i++)
    {
       var select_ob=emp_vo.options[i];
       if(id==select_ob.value)
       {
          isC=false;
          break;
       }
    }
      if(isC)
    {
      if(id.indexOf("UN")!=-1||id.indexOf("UM")!=-1||id.indexOf("@K")!=-1)
          emp_vo.options[emp_vo.options.length]=no;
      else{
        var downvos=document.getElementsByName("upId");
        var dvo = downvos[0];
        var upId="";
        for(var i=0;i<dvo.options.length;i++){
          var select_obj=dvo.options[i];
          if(select_obj.selected){
            upId=select_obj.value;
            break;
          }
        }
        var rel=document.getElementsByName("spRelationId");
        var relat=rel[0];
        var relation_id="";
        for(i=0;i<relat.options.length;i++)
        {
           var select_ob=relat.options[i];
           if(select_ob.selected){
        	   relation_id=select_ob.value;
               break;
           }
        }
         var hashvo = new ParameterSet();
	     hashvo.setValue("opt","getname");
	     hashvo.setValue("id",id);
	     hashvo.setValue("upId",upId);
	     hashvo.setValue("relation_id",relation_id);
	     var request=new Request({method:'post',onSuccess:insertOptions,functionId:'302001020252'},hashvo);
      }
        setTitle();
    } 
  }
  //设置右下方机构选择框的title
 function setTitle(){
     var downPersons=document.getElementsByName("downId");
     var dvo = downPersons[0];
     for(var i=0;i<dvo.options.length;i++){
         var select_obj=dvo.options[i];
         select_obj.title=select_obj.text;
     }
 }
  function insertOptions(param){
      var message=getDecodeStr(param.getValue("message"));
      if(message==''){
	      var dataValue=param.getValue("dataValue");
	      var dataName=param.getValue("dataName");
	      var on = new Option();
	      on.value=dataValue;
	      on.text=dataName;
	      var vos= document.getElementsByName('downId');
	      var emp_vo=vos[0];
	      emp_vo.options[emp_vo.options.length]=on;
          setTitle();
	 }else{
	 
	      alert(message);
	      return;
	 }
  }
     //田野添加单位、部门、岗位树根据业务/或人员范围控制方法
/*
 * 单位部门和职位级联
 *codeid,相关代码类
 *mytarget,选中的代码值需填充的Element,for examples input text
 */

function openInputCodeDialogOrgInputPosCheck(codeid,mytarget,managerstr,flag) {
	if(managerstr==""){
		managerstr = busiorgdept ;
	}
	openInputCodeDialogOrgInputPos(codeid,mytarget,managerstr,flag);
}
//关闭弹窗  wangb 20190329
function winclose(){
	if(parent.window)
		parent.window.close();
	else
		window.close();
}
//-->
</script>
<body scroll=no >
<html:form action="/general/sprelationmap/relation_maintain">
<table width='95%' border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr><td align="left" class="TableRow">审批关系维护</td></tr>
<tr><td align="center" class="RecordRow" style="padding-left:5px;padding-right:5px;padding-bottom:5px;">
<fieldset align="center" width="90%">
<legend>审批人</legend>
<table width="100%" >
<tr><td align="right" width="10%"><%=b0110desc%>:</td><td align="left" width="30%" >
       <input type="text" name="field[0].hzvalue"  readonly  value="<%= unitName %>" class="text4"/>
       <input type="hidden" name="field[0].value" value="<%= unitCode %>" id="one"/>
       <img src="/images/code.gif" align="middle" valign="inherit" onclick='openInputCodeDialogOrgInputPos("UN","field[0].hzvalue","<%=busi_org_dept%>","1");'/>
    </td>
    <td align="right" width="10%"><%=e0122desc%>:</td><td align="left" width="30%">
       <input type="text"  readonly  name="field[1].hzvalue" value="<%= departmentName %>" class="text4"/>
       <input type="hidden" name="field[1].value" value="<%= departmentCode %>" id="two"/>
       <img src="/images/code.gif" align="middle" valign="inherit" onclick='openInputCodeDialogOrgInputPosCheck("UM","field[1].hzvalue",document.getElementsByName("field[0].value")[0].value,"1");'/>
       
    </td>
    <td align="left" width="20%">
    &nbsp;
    </td>
    </tr>
<tr><td align="right" width="10%"><%=e01a1desc%>:</td><td align="left" width="30%">
       <input type="text"  readonly  name="field[2].hzvalue" value="<%= positionName %>" class="text4"/>
       <input type="hidden" name="field[2].value" value="<%= positionCode %>" id="three"/>
       <img src="/images/code.gif" align="middle" valign="inherit" onclick='openInputCodeDialogOrgInputPosCheck("@K","field[2].hzvalue",document.getElementsByName("field[1].value")[0].value,"1");'/>
    </td>
    <td align="right" width="10%"><%=a0101desc%>:</td><td align="left" width="30%">
       <input type="text" name="field[4].value" value="${relationMapForm.name }" id="four" class="text4"/>
    </td>
     <td align="left" width="20%">
    <input type="button" name="qy" value="查询" class="mybutton" onclick="queryUpPerson();"/>
     <input type="button" name="qk" value="清空" class="mybutton" onclick="clearCond();"/>
    </td>
</tr>
<tr><td colspan="5" width="100%" align="center" >
           <hrms:optioncollection name="relationMapForm" property="upPersonList" collection="list" />
		             <html:select name="relationMapForm" property="upId" size="10" onclick="getDown();" style="width:90%;" >
		             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>		
    </td>
</tr>
</table>
</fieldset>


<fieldset align="center" width="95%">
<legend>下级</legend>
<table width="100%">
<tr><td width="40%" align="center" nowrap>
     姓名：<input type="text" name="a0101" value="" size="27" onkeyup="showDateSelectBox('a0101')"  class="text4"/>&nbsp;
    </td>
    <td width="15%" nowrap>
    &nbsp;
    </td>
    <td align="center" width="35%" nowrap>
       汇报关系： <hrms:optioncollection name="relationMapForm" property="spRelationList" collection="list" />
		             <html:select name="relationMapForm" property="spRelationId" size="1" style="width:60%" onchange="changeDown();">
		             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>		
    </td>
    <td width="10%" align="left">
       <input type="button" name="cy" value="复制" class="mybutton" onclick="selectCopyTo();"/>
    </td>
</tr>
<tr>
<td align="center"  nowrap>
	<div align="left" id="tbl_container" ondblclick="getemp();" class="div2" >
	 <hrms:orgtree isAddAction="false" action="/general/sprelationmap/relation_map_drawable.do?b_init=init&relationType=1" flag="1" showroot="false"  dbtype="1" priv="1" target="app" nmodule="4" ></hrms:orgtree>
	</div>
</td>
 <td align="center" nowrap>
    <html:button  styleClass="mybutton" property="b_addfield" onclick="getemp();">
            		     <bean:message key="button.setfield.addfield"/>    
	           </html:button>
				<br><br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('downId');">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button>	   
    </td>
 <td align="center" colspan="2" nowrap>
 		<div height=200px>
  				<hrms:optioncollection name="relationMapForm" property="downPersonList" collection="list" />
	            <html:select name="relationMapForm"  property="downId" size="12"  style="width:285px;height:200px;" onchange="setTitle();">
		             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>	
        </div>	
    </td>
</tr>
</table>
</fieldset>
</td></tr>
<tr height="35"><td align="center" style="padding-top:3px;padding-bottom:3px;">
<input type="button" name="sv" class="mybutton" value="<bean:message key="button.save"/>" onclick="save();"/>
<input type="button" name="cn" class="mybutton" value="<bean:message key="button.cancel"/>" onclick="winclose();"/>

</td></tr>
</table>
<div id="date_panel" style="display:none;" onmouseout="remove();">

		<select id="date_box" name="contenttype" multiple="multiple"  style="" size="6" ondblclick="setSelectValue();">
        </select>
	 </div>
</html:form>
</body>
