<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hrms.struts.constant.WebConstant,
                 com.hjsj.hrms.actionform.hire.parameterSet.ParameterForm, com.hrms.struts.valueobject.UserView,
                 org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.sys.EncryptLockClient,
                 com.hrms.struts.taglib.CommonData,com.hrms.hjsj.sys.ResourceFactory" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%
    ParameterForm parameterForm=(ParameterForm)session.getAttribute("parameterForm2");
    ArrayList testTemplatAdvance=parameterForm.getTestTemplatAdvance();
    ArrayList channelList=parameterForm.getChannelList();
    ArrayList modeList=parameterForm.getModeList();
    ArrayList testTemplateList=parameterForm.getTestTemplateList();
    ArrayList itemList=parameterForm.getItemList();
    int realLength=testTemplatAdvance.size();
%>
<html>
<head>
<style type="text/css">
.tbl-container{  
    overflow:auto; 
    BORDER-BOTTOM: #C4D8EE 0pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 0pt solid;
}
</style>
<hrms:themes></hrms:themes>
</head>
  <body>
    <form action=""></form>
    <div style="height:357px;width:590px;" class="tbl-container" id="tbl-container">
    <table width="100%"  class="ListTable" id="mainTable">
        <tr style="height:23px;">
            <td class="TableRow" style="border-left: 0px;" align="center"><bean:message key="zp.zpChannel"/></td><!--招聘渠道 -->
            <td class="TableRow" align="center" ><bean:message key="zp.zpMode"/></td><!-- 测评阶段 -->
            <td class="TableRow" align="center" ><bean:message key="zp.testTemplate"/></td><!--测评模版 -->
            <td class="TableRow" align="center" ><bean:message key="zp.goalMode"/></td><!-- 打分方式 -->
            <td class="TableRow" align="center" ><bean:message key="zp.testItemdesc"/></td><!-- 测评结果指标 -->
            <td class="TableRow" align="center" style="border-right:0px;">&nbsp;&nbsp;&nbsp;</td>
        </tr>
        <%
            for(int i=0;i<testTemplatAdvance.size();i++){
                HashMap advanceMap=(HashMap) testTemplatAdvance.get(i);
                String hire_obj_desc=(String)advanceMap.get("hire_obj_desc");//招聘渠道
                String interviewDesc=(String)advanceMap.get("interviewDesc");//测评阶段描述
                String templateDesc=(String)advanceMap.get("templateDesc");//测评模版名称
                String score_item_desc=(String)advanceMap.get("score_item_desc");//测评结果指标描述
                
                String hire_obj_code=(String)advanceMap.get("hire_obj_code");//招聘渠道id
                String interview=(String)advanceMap.get("interview");//测评阶段id
                String templateId=(String)advanceMap.get("templateId");//测评模版id
                String score_item=(String)advanceMap.get("score_item");//测评结果关联到的指标itemid
                
                String mark_type=(String)advanceMap.get("mark_type");//是否采用复杂打分
        %>
        <tr id="record<%=i%>">
            <td class="RecordRow" style="border-left: 0px;">
                <%=hire_obj_desc %>
            </td>
            <td class="RecordRow">
                <%=interviewDesc %>
            </td>
            <td class="RecordRow">
                <%=templateDesc %>
            </td>
            <td class="RecordRow">
            <%
                if(mark_type==null||mark_type.equals("")||mark_type.equals("1")){//这种方式采用标准打分
            %>
                    <input type='radio' name='markType<%=i %>' value='1' checked disabled /><bean:message key="lable.performance.grademark"/>
                    <input type='radio' name='markType<%=i %>'  value='2' disabled /><bean:message key="label.performance.mixmark"/>
            <%
                }else{
            %>
                    <input type='radio' name='markType<%=i %>'  value='1' disabled /><bean:message key="lable.performance.grademark"/>
                    <input type='radio' name='markType<%=i %>'  value='2' checked disabled><bean:message key="label.performance.mixmark"/>
            <%
                }
            %>
            </td>
            <td class="RecordRow">
               <%=score_item_desc %> 
            </td>
            <td class="RecordRow" style="border-right:0px;">
                <img  src='/images/del.gif' style='' id='del<%=i%>'border=0 width=20 height=20 onclick="delAdvance('<%=hire_obj_code%>','<%=interview %>','<%=templateId%>','<%=score_item%>','<%=mark_type %>',this)"/>
            </td>
        </tr>
        <%
            }
        %>
    </table>
    </div>
    <table class='ListTable1' width="590px">
        <tr>
            <td class="TableRow" style="padding-top: 2px;"><!-- 招聘渠道select -->
             <select id='hireObjCodes'>
                  <%    
                    for(int j=0;j<channelList.size();j++){
                    CommonData date=(CommonData)channelList.get(j);
                    String avalue=date.getDataValue();
                    String text=date.getDataName();
                    %>
                    <option value='<%=avalue%>'><%=text%></option>
                    <%  
                    }
                    %>
              </select>
            </td>
            <td class="TableRow" style="padding-top: 2px;border-left:0px;"><!--测评阶段select-->
              <select id='modes'>
                  <%    
                    for(int j=0;j<modeList.size();j++){
                    CommonData date=(CommonData)modeList.get(j);
                    String avalue=date.getDataValue();
                    String text=date.getDataName();
                    %>
                    <option value='<%=avalue%>'><%=text%></option>
                    <%  
                    }
                    %>
              </select>
            </td>
            <td class="TableRow" style="padding-top: 2px;border-left:0px;"><!-- 测评模版名称select -->
             <select id='testTemplates'>
                  <%    
                    for(int j=0;j<testTemplateList.size();j++){
                    CommonData date=(CommonData)testTemplateList.get(j);
                    String avalue=date.getDataValue();
                    String text=date.getDataName();
                    %>
                    <option value='<%=avalue%>'><%=text%></option>
                    <%  
                    }
                    %>
              </select>
            </td>
            <td class="TableRow" style="border-left:0px;padding-top: 2px;"><!-- 打分方式 1：标度打分 2：混合打分 -->
                <select id="markTypes">
                    <option value="1"><bean:message key="lable.performance.grademark"/></option>
                    <option value="2"><bean:message key="label.performance.mixmark"/></option>
                </select>
            </td>
            <td class="TableRow" style="padding-top: 2px;"><!-- 对应指标 -->
	            <select id='items'>
	                  <%    
	                    for(int j=0;j<itemList.size();j++){
	                    CommonData date=(CommonData)itemList.get(j);
	                    String avalue=date.getDataValue();
	                    String text=date.getDataName();
	                    %>
	                    <option value='<%=avalue%>'><%=text%></option>
	                    <%  
	                    }
	                    %>
	              </select>
	              <input type="button" class="mybutton" value='<bean:message key="lable.menu.main.add"/>' onclick='addAdvance()'/>
            </td>
        </tr>
    </table>
  </body>
  <script type="text/javascript">
  var theRealLength=<%=realLength%>
  var hireobjcode="";//招聘方式
  var hireobjcodeDesc="";
  var mode="";//测评阶段
  var modeDesc="";
  var testTemplate="";//测评模版名称
  var testTemplateDesc="";
  var markType="";//打分方式
  var markTypeDesc="";
  var itemid=""//对应指标
  var itemdesc="";
  var selectDEL;
  /**
         向库中添加方案信息
  **/
  function addAdvance(){
     hireobjcode=document.getElementById("hireObjCodes").options[document.getElementById("hireObjCodes").selectedIndex].value;//招聘方式
     hireobjcodeDesc=document.getElementById("hireObjCodes").options[document.getElementById("hireObjCodes").selectedIndex].text;//招聘方式
     mode=document.getElementById("modes").options[document.getElementById("modes").selectedIndex].value;//测评阶段
     modeDesc=document.getElementById("modes").options[document.getElementById("modes").selectedIndex].text;//测评阶段
     testTemplate=document.getElementById("testTemplates").options[document.getElementById("testTemplates").selectedIndex].value;//测评模版名称
     testTemplateDesc=document.getElementById("testTemplates").options[document.getElementById("testTemplates").selectedIndex].text;
     markType=document.getElementById("markTypes").options[document.getElementById("markTypes").selectedIndex].value;//打分方式
     markTypeDesc=document.getElementById("markTypes").options[document.getElementById("markTypes").selectedIndex].text;//打分方式
     itemid=document.getElementById("items").options[document.getElementById("items").selectedIndex].value;//对应指标
     itemdesc=document.getElementById("items").options[document.getElementById("items").selectedIndex].text;
     if(testTemplate=="#"||itemid=="#"){
         if(testTemplate=="#"){
             alert("请选择测评模板!");
         }else{
        	 alert("请选择指标!");
         }
         return;
     }     
    var hashvo=new ParameterSet();
    hashvo.setValue("flag","add");
    hashvo.setValue("hireobjcode",hireobjcode);
    hashvo.setValue("mode",mode);
    hashvo.setValue("testTemplate",testTemplate);
    hashvo.setValue("markType",markType);
    hashvo.setValue("itemid",itemid);
    var request=new Request({method:'post',asynchronous:false,onSuccess:addAdvanceOk,functionId:'3000000273'},hashvo);    
  }
  /***
       添加成功后，刷新界面，增加一行 
  **/
  function addAdvanceOk(outparameters){
	  var sucess=outparameters.getValue("sucess");
	  if(sucess=="ok"){
		 var table=document.getElementById("mainTable");
		 var tableRow=table.insertRow(table.rows.length);
		 tableRow.id="record"+theRealLength;
		 var td=tableRow.insertCell(0);
		 //<td class="RecordRow" style="border-left: 0px;">
		 td.innerHTML=hireobjcodeDesc;
		 td.style.borderLeft="0";
		 td.setAttribute("className","RecordRow");//这里一定要注意设置成className 否则浏览器不渲染 
		 td=tableRow.insertCell(1);
		 td.setAttribute("className","RecordRow");
		 td.innerHTML=modeDesc;
		 td=tableRow.insertCell(2);
		 td.setAttribute("className","RecordRow");
         td.innerHTML=testTemplateDesc;
         td=tableRow.insertCell(3);
         td.setAttribute("className","RecordRow");
         if(markType=="1"){
        	 td.innerHTML="<input type='radio' name='markType"+theRealLength+"' value='1' checked disabled />"+lable_performance_grademark+"<input type='radio' name='markType"+theRealLength+"' value='2'  disabled />"+label_performance_mixmark;
         }else{
        	 td.innerHTML="<input type='radio' name='markType"+theRealLength+"' value='1'  disabled />"+lable_performance_grademark+"<input type='radio' name='markType"+theRealLength+"' value='2'  checked disabled />"+label_performance_mixmark;
         }
         td=tableRow.insertCell(4);
         td.setAttribute("className","RecordRow");
         td.innerHTML=itemdesc;
         td=tableRow.insertCell(5);
         td.setAttribute("className","RecordRow");
         td.style.borderRight="0";
         //onclick="delAdvance('hire_obj_code%>','interview','templateId','score_item','mark_type',this)"
         td.innerHTML="<img  src='/images/del.gif' style='' id='del"+theRealLength+"'border=0 width=20 height=20 onclick='delAdvance(\""+hireobjcode+"\",\""+mode+"\",\""+testTemplate+"\",\""+itemid+"\",\""+markType+"\",this)'/>";
         theRealLength=theRealLength+1;
      }else if(sucess=="exits"){
          alert(hireobjcodeDesc+":"+modeDesc+"阶段，已经定义了测评方案!");
          return;
      }else{
          alert("添加测评方案失败!");
      }
  }
  //删除方案信息 
  function delAdvance(hire_obj_code,interview,templateId,score_item,mark_type,obj){
	selectDEL=obj;
    var hashvo=new ParameterSet();
    hashvo.setValue("flag","del");
    hashvo.setValue("hireobjcode",hire_obj_code);
    hashvo.setValue("mode",interview);
    hashvo.setValue("testTemplate",templateId);
    hashvo.setValue("markType",mark_type);
    hashvo.setValue("itemid",score_item);
    var request=new Request({method:'post',asynchronous:false,onSuccess:delAdvanceOk,functionId:'3000000273'},hashvo);   
  }
  //刷新界面 
  function delAdvanceOk(outparameters){
	  var sucess=outparameters.getValue("sucess");
      if(sucess=="ok"){
          var delId=selectDEL.id;
          var idI=delId.substring(3);
          var trID="record"+idI;
          var tr=document.getElementById(trID);
          var  trIndex=tr.rowIndex
          var table=document.getElementById("mainTable");
          //table.removeChild(tr);
          table.deleteRow(trIndex);
      }
  }
  </script>
</html>
