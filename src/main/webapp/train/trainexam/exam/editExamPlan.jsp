<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.hjsj.sys.FieldItem"%>
<%@page import="com.hjsj.hrms.valueobject.common.FieldItemView"%>
<%@page import="com.hjsj.hrms.actionform.train.trainexam.exam.TrainExamPlanForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
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
<script language="javascript" src="/train/traincourse/trainAdd.js"></script>
<script type="text/javascript">
//查询是否存在相同的名称
function selectName(nameid){
	if(nameid!="r5401"&&nameid!="R5401"){
		return;
	}
	var hashvo = new ParameterSet();
	var planName=document.getElementById(nameid).value;
	
	hashvo.setValue("planName", planName);
	hashvo.setValue("r5400", $F('r5400'));
	var request=new Request({method:'post',onSuccess:isexistSame,functionId:'2020081012'},hashvo);
}
//定义一个变量来保存是否存在相同的名称
var isexist;
function isexistSame(outparamters){
	isexist=outparamters.getValue("isexist");
	  if(isexist=="yes"){
		  alert("已经存在相同的名称，请修改！");
		  return false;
	  }
}
//保存
function save(oper)
{
	if(isexist=="yes"){
		  alert("已经存在相同的名称，请修改！");
		  return false;
	  }
  //添加必填项验证
  var itemid='r5401';
  var itemvalue="";    
  itemvalue = document.getElementById(itemid).value;  
  if(true && !itemvalue){
    alert('名称不能为空！');
    return;
  }
  
  //检查考试时间填写是否正确
  var start_d = document.trainExamPlanForm.r5405.value;
  if(!start_d)
  {
    alert("请输入起始时间!");
    return;
  }
  if(!checkDateTime(start_d))
  {
     alert("起始时间格式不正确,请输入正确的日期格式!\nyyyy-MM-dd");
     return;
  }
  
  var end_d = document.trainExamPlanForm.r5406.value;
  if(!end_d)
  {
    alert("请输入结束时间!");
    return;
  }  
  if(!checkDateTime(document.trainExamPlanForm.r5406.value))
  {
     alert("结束时间格式不正确,请输入正确的日期格式!\nyyyy-MM-dd");
     return;
  }  
 
  var initValue = $F('initValue');  
  var hideFilds = $F('hideFilds');
  var readonlyFilds = $F('readonlyFilds');
  var hideimgids =  $F('hideimgids');
  var hideSaveFlds = $F('hideSaveFlds');
  var isUnUmRela = $F('isUnUmRela');
  var r5400=$F('r5400'); 
  
  var start_h=$('start_h');
  var start_m=$('start_m');
  var start_mm=$('start_mm');
  var end_h=$('end_h');
  var end_m=$('end_m');
  var end_mm=$('end_mm');
  
  var paramStr="&initValue="+initValue+"&hideFilds="+hideFilds+"&readonlyFilds="+readonlyFilds+"&hideimgids="+hideimgids+"&hideSaveFlds="+hideSaveFlds+"&isUnUmRela="+isUnUmRela;
  
  if(start_h!=null)
  { 
    var startTime = start_h.value+":"+start_m.value+":"+start_mm.value;
    paramStr+="&r5405_time="+startTime;
    
    start_d = start_d + " " + startTime;
  }   
  if(end_h!=null)
  {
    var endTime = end_h.value+":"+end_m.value+":"+end_mm.value;
    paramStr+="&r5406_time="+endTime;
    
    end_d = end_d + " " + endTime;
  }
  
  var start_date = new Date(start_d.replace("-","/"));
  var end_date = new Date(end_d.replace("-","/"));
  if(start_date>=end_date)
  {
    alert("起始时间必须小于结束时间!");
    return;
  }
  
  //添加考试时长的判断
  //说明:这里用了Math.ceil()函数,向上取整,即零头算一天,^_^
  var   strDate1   =  start_d;
  var   strDate2   =   end_d;
  strDate1=strDate1.substring(0,strDate1.lastIndexOf(":")+2).replace(/-/g, "/ ");
  strDate2=strDate2.substring(0,strDate2.lastIndexOf(":")+2).replace(/-/g, "/ ");
  //去掉毫秒 把-替换成/ 如果不替换转成时间戳类型火狐会出问题
  var   date1   =  Date.parse(strDate1);
  var   date2   =  Date.parse(strDate2);
  var longTime=(date2-date1)/(60*1000);
  //如果考试时长小于开始结束两者之间的时间差，则返回false
  var howlong=document.getElementById("r5407").value;
  if(howlong>longTime){
	  alert("考试时长不应该大于结束时间与开始时间之差");
	  return false;
  }
  
  
  if(r5400!=null)
  {
    paramStr+="&r5400=" + r5400;  
  }    
  trainExamPlanForm.action="/train/trainexam/exam/plan.do?b_save=link&e_flag="+oper+paramStr; 
  trainExamPlanForm.submit();  
}

   function goBack()
   {
      trainExamPlanForm.action="/train/trainexam/exam/plan.do?b_query=back&amp;returnvalue=";
      trainExamPlanForm.submit(); 
   }
   function changeSel(id){
   		var id = document.getElementById(id);
   			var imgR5410 = document.getElementById("imgr5410");
   		if(id&&id.value==1){
   			if(imgR5410)
   				imgR5410.style.display="none";
   			
   			document.getElementById("r5410").disabled="disabled";
   			document.getElementById("r5410").value="";
   		}
   		else{
   			if(imgR5410)
   				imgR5410.style.display="";
   			
   			document.getElementById("r5410").disabled="";
   		}
   }
</script>


<style>
.fixedDiv11
{ 
  overflow:auto; 
  height:expression(document.body.clientHeight-70);
  width:expression(document.body.clientWidth-22); 
}
</style>

<%
  TrainExamPlanForm form = (TrainExamPlanForm)session.getAttribute("trainExamPlanForm");
  int len = form.getFieldlist().size();  
%>
<style>
body{padding-top: 5px;text-align: center;}
</style>
 
<body>
<html:form action="/train/trainexam/exam/plan">  
  <html:hidden name="trainExamPlanForm" property="initValue" styleId="initValue"/>
  <html:hidden name="trainExamPlanForm" property="hideFilds" styleId="hideFilds"/>
  <html:hidden name="trainExamPlanForm" property="readonlyFilds" styleId="readonlyFilds"/> 
  <html:hidden name="trainExamPlanForm" property="hideimgids" styleId="hideimgids"/> 
  <html:hidden name="trainExamPlanForm" property="hideSaveFlds" styleId="hideSaveFlds"/> 
  <html:hidden name="trainExamPlanForm" property="isUnUmRela" styleId="isUnUmRela"/>     
  <html:hidden name="trainExamPlanForm" property="orgparentcode" />
  <html:hidden name="trainExamPlanForm" property="deptparentcode" />
  <html:hidden name="trainExamPlanForm" property="r5400" styleId="r5400"/>
  <table width="94%" align="center" border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td valign="top">
	    <!--  <div id="divid" class="fixedDiv11" style="border: #C4D8EE 1pt solid;border-left:0px;margin-left: 8px;"> --> 
	        <table id="tableid" width="100%" border="0" cellpadding="3" cellspacing="0" align="center" class="ListTable">
	          <tr height="20"">
	            <td colspan="4" align="left" class="TableRow">  
	              &nbsp;&nbsp;
	              <logic:equal name="trainExamPlanForm" property="e_flag" value="add">
	                <bean:message key="train.examplan.titlename.new"/>
	              </logic:equal>
	              <logic:equal name="trainExamPlanForm" property="e_flag" value="up">
                  <bean:message key="train.examplan.titlename.edit"/>
                </logic:equal>
                <logic:equal name="trainExamPlanForm" property="e_flag" value="view">
                  <bean:message key="train.examplan.titlename.view"/>
                </logic:equal>
	            </td>
	          </tr>

            <tr class="trDeep">
            <%int i=0, j=0; %>
            
              <logic:iterate id="element" name="trainExamPlanForm" property="fieldlist" indexId="index">
                 
			           <%
			                FieldItemView abean = (FieldItemView) pageContext.getAttribute("element");
			                boolean isFillable1 = abean.isFillable();
			                if (i == 2)
			                {
			           %>
			           <%
			                 if (j % 2 == 0)
			                 {
			           %>
			           
			           </tr>
			           <tr class="trShallow">
			           <%
			             } else
			             {
			           %>
			           </tr>
			           <tr class="trDeep">
			           <%
			             }
			             i = 0;
			             j++;
			                   }
			           %>
                 
                <logic:notEqual name="element" property="itemtype" value="M">
	                <td align="right" class="RecordRow" nowrap>
	                  <bean:write name="element" property="itemdesc" filter="true" />
	                </td>
	                
	                <td align="left" class="RecordRow" nowrap>
	                
                  <logic:equal name="element" property="codesetid" value="0">
                    <logic:notEqual name="element" property="itemtype" value="D">
                      
                       <logic:notEqual name="element" property="itemtype" value="N">                 
                          <html:text maxlength="${element.itemlength}" size="30" 
                              styleClass="textColorWrite" 
                              name="trainExamPlanForm" styleId="${element.itemid}" 
                              onblur="selectName('${element.itemid}')"
                              property='<%="fieldlist[" + index + "].value"%>' style="width:70%"/>                                                                 
                       </logic:notEqual>
                      
                       <logic:equal name="element" property="itemtype" value="N">
                         <logic:notEqual name="element" property="itemid" value="r5300">
                           <logic:notEqual name="element" property="itemid" value="r5408">
                             <logic:notEqual name="element" property="itemid" value="r5409">  
                                 
                                 <logic:equal name="element" property="decimalwidth" value="0">
                                   <html:text maxlength="${element.itemlength}" size="30" styleClass="textColorWrite" onkeypress="event.returnValue=IsDigit2(this);" onblur='isNumber(this);'
                                     name="trainExamPlanForm" style="width:70%" styleId="${element.itemid}" property='<%="fieldlist[" + index + "].value"%>' />                   
                                 <logic:equal name="element" property="itemid" value="r5407">
                                 <bean:message key="train.trainexam.question.questiones.component"/>
                                 </logic:equal>
                                 </logic:equal>     
                                                    
                                <logic:notEqual name="element" property="decimalwidth" value="0">
                                <bean:define id="dw" name="element" property="decimalwidth"/>
	                            <bean:define id="itemlength" name="element" property="itemlength"/>
                                <% int d = Integer.parseInt(dw.toString());
	                               int l = Integer.parseInt(itemlength.toString());
	                               String length = d+l+1+"";
	                            %>
                                  <html:text maxlength="<%=length %>" size="30" styleClass="textColorWrite" onkeypress="event.returnValue=IsDigit(this);" onblur='isNumber(this);'
                                    name="trainExamPlanForm" styleId="${element.itemid}" property='<%="fieldlist[" + index + "].value"%>' style="width:70%"/>                 
                                	<logic:equal name="element" property="itemid" value="r5407">
                                 	<bean:message key="train.trainexam.question.questiones.component"/>
                                 	</logic:equal>
                                </logic:notEqual>
                                
                             </logic:notEqual>
                           </logic:notEqual>
                         </logic:notEqual>
                         
                         <logic:equal name="element" property="itemid" value="r5300">
                           <html:select name="trainExamPlanForm" property='<%="fieldlist[" + index + "].value"%>' size="1" style="width:70%;">                   
                             <html:optionsCollection name="trainExamPlanForm" property="examPapers" label="dataName" value="dataValue"/>
                           </html:select> 
                         </logic:equal>                          
                         
                         <logic:equal name="element" property="itemid" value="r5408">
                           <html:select name="trainExamPlanForm" property='<%="fieldlist[" + index + "].value"%>' size="1" style="width:70%;">                   
                             <html:option value="1">
                               <bean:message key="train.examstyle.online"/>
                             </html:option>
                             <html:option value="2">
                               <bean:message key="train.examstyle.offline"/>
                             </html:option>
                           </html:select> 
                         </logic:equal> 
                                                  
                         <logic:equal name="element" property="itemid" value="r5409">
                           <html:select name="trainExamPlanForm" styleId="idr5409" onchange="changeSel('idr5409');" property='<%="fieldlist[" + index + "].value"%>' size="1" style="width:70%;">                   
                             <html:option value="1">
                               <bean:message key="train.examplan.paperstyle.all"/>
                             </html:option>
                             <html:option value="2">
                               <bean:message key="train.examplan.paperstyle.single"/>
                             </html:option>
                           </html:select> 
                         </logic:equal>                
                         
                       </logic:equal>
                       
                    </logic:notEqual>
                     
                    <logic:equal name="element" property="itemtype" value="D">  
                        <logic:notEqual name="element" property="itemid" value="r5405"> 
                           <logic:notEqual name="element" property="itemid" value="r5406">  
                            <input type="text" class="textColorWrite" name='<%="fieldlist[" + index + "].value"%>' maxlength="${element.itemlength}" size="30"  id="${element.itemid}" extra="editor"  class="m_input"  style="font-size:10pt;text-align:left;width:70%;"
                                 dropDown="dropDownDate" value="${element.value}"  onchange=" if(!validate(this,'${element.itemdesc}')) { this.value='';}">
                           </logic:notEqual>
                        </logic:notEqual>
                        
                        <logic:equal name="element" property="itemid" value="r5405">    
                          <table width="100%" border='0' cellspacing="0"  cellpadding="0">
                            <tr>
                              <td width="15" style="padding-top: 2px;">
                                 <input type="text" name='<%="fieldlist[" + index + "].value"%>' 
                                    maxlength="${element.itemlength}" size="30"  id="${element.itemid}" 
                                    extra="editor"  class="textColorWrite"  style="font-size:10pt;text-align:left;width:150px"
                                    dropDown="dropDownDate" value="${element.value}" 
                                    onchange=" if(!validate(this,'${element.itemdesc}')) { this.value='';}" >
                              </td>
                              <td>
                              	<%
                           if (isFillable1)
                         {
                       %>    <font color='red'>&nbsp;*</font><%
                          }
                         %>  
                              </td>
                            </tr>
                            <tr>                              
                              <td colspan="2" >
                                <table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
                                  <tr>
                                    <td align="right">
                                      <bean:message key="hours.minutes.second"/>
                                    </td>
                                    <td width="92" style="padding-top: 2px;padding-bottom: 2px;" nowrap style="background-color:#FFFFFF;"> 
                                       <div class="m_frameborder">
                                        <input type="text" class="m_input text4" maxlength="2" size="2" name="intricacy_app_start_time_h" id="start_h" value="00" onfocus="setFocusObj(this,24);" onkeypress="event.returnValue=IsDigit3(this);" onblur="if(this.value!='' && parseInt(this.value)>23){alert('请输入0－23之间的整数！');this.focus();}"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input text4" size="2" maxlength="2" name="intricacy_app_start_time_m" id="start_m" value="00" onfocus="setFocusObj(this,60);"  onkeypress="event.returnValue=IsDigit3(this);" onblur="if(this.value!='' && parseInt(this.value)>59){alert('请输入0－59之间的整数！');this.focus();}"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input text4" maxlength="2" name="intricacy_app_start_time_mm" id="start_mm" value="00" size="2" onfocus="setFocusObj(this,60);"  onkeypress="event.returnValue=IsDigit3(this);" onblur="if(this.value!='' && parseInt(this.value)>59){alert('请输入0－59之间的整数！');this.focus();}">
                                       </div>
                                    </td>
                                    <td style="padding-top: 2px;">
                                    <logic:notEqual name="trainExamPlanForm" property="e_flag" value="view">
	                                    <table border="0" cellspacing="2" cellpadding="0">
	                                       <tr><td><button id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
	                                       <tr><td><button id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
	                                    </table>
                                    </logic:notEqual>
                                    </td>
                                  </tr>
                                </table> 
                              </td>
                            </tr>
                          </table>  
                        </logic:equal> 
                        
                        <logic:equal name="element" property="itemid" value="r5406">    
                          <table width="100%" border='0' cellspacing="0"  cellpadding="0">
                            <tr>
                              <td width="15"  style="padding-top: 2px;">
                                 <input type="text" name='<%="fieldlist[" + index + "].value"%>' maxlength="${element.itemlength}" size="30"  id="${element.itemid}" extra="editor"  class="m_input textColorWrite"  style="font-size:10pt;text-align:left;with:70%"
                                    dropDown="dropDownDate" value="${element.value}" onchange=" if(!validate(this,'${element.itemdesc}')) { this.value='';}">
                              
                              </td>
                              <td>
                              	<%
                    if (isFillable1)
                    {
                  %> <font color='red'>&nbsp;*</font><%
                    }
                  %>  
                              </td>
                            </tr>
                            
                            <tr>                              
                              <td colspan="2">
                              
                                <table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
                                   <tr>
                                     <td align="right">
                                       <bean:message key="hours.minutes.second"/>
                                     </td>
                                     <td width="92" style="padding-top: 2px;padding-bottom: 2px;" nowrap style="background-color:#FFFFFF;"> 
                                       <div class="m_frameborder">
                                         <input type="text" class="m_input text4" size="2" maxlength="2" name="intricacy_app_end_time_h" id="end_h" value="00" onfocus="setFocusObj(this,24);" onkeypress="event.returnValue=IsDigit3(this);" onblur="if(this.value!='' && parseInt(this.value)>23){alert('请输入0－23之间的整数！');this.focus();}"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input text4" size="2" maxlength="2" name="intricacy_app_end_time_m" id="end_m" value="00" onfocus="setFocusObj(this,60);"  onkeypress="event.returnValue=IsDigit3(this);" onblur="if(this.value!='' && parseInt(this.value)>59){alert('请输入0－59之间的整数！');this.focus();}"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input text4" size="2" maxlength="2" name="intricacy_app_end_time_mm" id="end_mm" value="00"  onfocus="setFocusObj(this,60);"  onkeypress="event.returnValue=IsDigit3(this);" onblur="if(this.value!='' && parseInt(this.value)>59){alert('请输入0－59之间的整数！');this.focus();}">
                                       </div>
                                     </td>
                                     <td>
                                     <logic:notEqual name="trainExamPlanForm" property="e_flag" value="view">
                                       <table border="0" cellspacing="2" cellpadding="0">
                                         <tr><td><button id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
                                         <tr><td><button id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
                                       </table>
                                       </logic:notEqual>
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
                      <html:hidden name="trainExamPlanForm"  property='<%="fieldlist[" + index + "].value"%>' styleId='b0110_value' onchange="changepos('UN',this)" />
                      <html:text maxlength="${element.itemlength}" size="30" styleClass="textColorWrite"  styleId="b0110"  
                          name="trainExamPlanForm" property='<%="fieldlist[" + index + "].viewvalue"%>' readonly="true" onchange="fieldcode(this,2)"
                           style="width:70%" />
                      <logic:notEqual name="trainExamPlanForm" property="e_flag" value="view">
                      <img src="/images/code.gif" id='img${element.itemid}' onclick='openInputCodeDialogOrgInputPos("${element.codesetid}","<%="fieldlist[" + index + "].viewvalue"%>","${trainExamPlanForm.orgparentcode}","1");' align="absmiddle"/>  
                      </logic:notEqual>
                     </logic:equal>     
                     <logic:equal name="element" property="itemid" value="e0122">
                      <html:hidden name="trainExamPlanForm"  property='<%="fieldlist[" + index + "].value"%>' styleId='e0122_value' onchange="changepos('UM',this)" />  
                      <html:text maxlength="${element.itemlength}" size="30" styleClass="textColorWrite"  styleId="e0122" 
                          name="trainExamPlanForm" property='<%="fieldlist[" + index + "].viewvalue"%>' readonly="true" onchange="fieldcode(this,2)"
                            style="width:70%" />
						<logic:notEqual name="trainExamPlanForm" property="e_flag" value="view">
                      <img src="/images/code.gif" id='img${element.itemid}' onclick='openInputCodeDialogOrgInputPos("${element.codesetid}","<%="fieldlist[" + index + "].viewvalue"%>",${trainExamPlanForm.deptparentcode.value},"2");' align="absmiddle"/> 
                     	</logic:notEqual>
                     </logic:equal> 
                     <logic:notEqual name="element" property="itemid" value="b0110">
                      <logic:notEqual name="element" property="itemid" value="e0122">
                     <html:hidden name="trainExamPlanForm" property='<%="fieldlist[" + index + "].value"%>' styleId="${element.itemid}_value"/>  
                      <html:text maxlength="${element.itemlength}" size="30" styleClass="textColorWrite" 
                          name="trainExamPlanForm" property='<%="fieldlist[" + index + "].viewvalue"%>' readonly="true" onchange="fieldcode(this,2)"
                             styleId="${element.itemid}" style="width:70%"/>
                      <logic:notEqual name="trainExamPlanForm" property="e_flag" value="view">
                      <img id='img${element.itemid}' src="/images/code.gif" onclick='javascript:openKhTargetCardInputCode("${element.codesetid}","<%="fieldlist[" + index + "].viewvalue"%>");' align="absmiddle"/>
                      </logic:notEqual>
                      </logic:notEqual>
                    </logic:notEqual>
                  </logic:notEqual>    
                  
                  <%i++; %>
                  <logic:notEqual name="element" property="itemid" value="r5406">               
                  	<logic:notEqual name="element" property="itemid" value="r5405">
                  <%  if (isFillable1)
                    {
                  %> <font color='red'>*</font><%
                    }
                  %>     
                  </logic:notEqual>
                  </logic:notEqual>             
                </td>
                
                <%if(index.intValue()<len-1) { %>
                <logic:equal name="trainExamPlanForm" property='<%="fieldlist[" + (index.intValue()+1) + "].itemtype"%>' value="M">
                  <%if(i<2){ %>
                  <td align="left" class="RecordRow" nowrap></td>
                  <td align="left" class="RecordRow" nowrap></td>
                  <%i++; }%>
                  
                </logic:equal>
                <%} else if(index.intValue()==len-1){%>
                  <%if(i<2){ %>
                  <td align="left" class="RecordRow" nowrap></td>
                  <td align="left" class="RecordRow" nowrap></td>
                  <%i++; }%>    
                <%} %>
              </logic:notEqual>
              
              <logic:equal name="element" property="itemtype" value="M">
                <td align="right" class="RecordRow" valign="top" nowrap>
                  <bean:write name="element" property="itemdesc" filter="true" />
                </td>
                <td align="left" class="RecordRow" colspan="3" nowrap>
                  <html:textarea name="trainExamPlanForm" styleId="${element.itemid}"
                    property='<%="fieldlist[" + index + "].value"%>'
                    cols="64" rows="4" styleClass="Mul"></html:textarea>
                  <%
                      if (isFillable1) 
                      {
                  %> <font color='red'>*</font>
                  <%
                     }
                  %>
                </td>
                <% i=2; %>
              </logic:equal>
                
              </logic:iterate>
            </tr>
	    
	        </table>
	      <!-- </div> -->
	    </td>
	  </tr>
    <tr>
      <td>
        <table width='100%' align='center' cellpadding="0" cellspacing="0" style="margin-top: 5px;">
          <tr>
            <td align="left" >
              <logic:notEqual name="trainExamPlanForm" property="e_flag" value="view">
                <input type="button" value="<bean:message key='button.save'/>" class="mybutton" onclick='save("saveClose");'>
              </logic:notEqual>
              <input type="button" class="mybutton" value="<bean:message key='button.return'/>" onClick="goBack();">               
            </td>
          </tr>
        </table>
      </td>
    </tr>		
  </table>  
</html:form>

<script>    
  //设置只读的文本框
  <logic:iterate  id="element1" name="trainExamPlanForm" property="itemidarr" indexId="index">
    var itemid = '<bean:write name="element1" property="itemid" filter="true" />';
    obj = $(itemid);
    obj.readOnly="true";
    obj.className="textColorRead";
  </logic:iterate>
  
  //设置要隐藏的文本框旁边的图片
  <logic:iterate  id="element2" name="trainExamPlanForm" property="hidePics" indexId="index">
    var imgid = '<bean:write name="element2" property="imgid" filter="true" />';
    obj = $(imgid);
    if(obj!=null && obj.length > 0)
      obj.style.display="none";
  </logic:iterate>
  //修改模式时候 开始和结束时间的 时分秒部分
  <logic:notEqual name="trainExamPlanForm" property="chkflag" value="add">
    var r5405_time = '${trainExamPlanForm.r5405_time}';
    var r5406_time = '${trainExamPlanForm.r5406_time}';
    var start_h=$('start_h');
    var start_m=$('start_m');
    var start_mm=$('start_mm');
    var end_h=$('end_h');
    var end_m=$('end_m');
    var end_mm=$('end_mm');
    if(start_h!=null)
    {
      start_h.value=r5405_time.substring(0,2);
      start_m.value=r5405_time.substring(3,5);
      start_mm.value=r5405_time.substring(6,8);
    }
    if(end_h!=null)
    {
      end_h.value=r5406_time.substring(0,2);
      end_m.value=r5406_time.substring(3,5);
      end_mm.value=r5406_time.substring(6,8);
    }
  </logic:notEqual>
  changeSel('idr5409');
  <logic:equal name="trainExamPlanForm" property="e_flag" value="view">
	var inputs = document.getElementsByTagName('input');
	for(var i = 0; i < inputs.length; i++){
		var input = inputs[i];
		var type = input.getAttribute("type");
		if("text" == type)
			input.readOnly = "true";
		
		input.removeAttribute("dropDown");
	}
	
	var textareas = document.getElementsByTagName('textarea');
	for(var i = 0; i < textareas.length; i++){
		textareas[i].readOnly = "true";
	}
	
	var selects = document.getElementsByTagName('select');
	for(var i = 0; i < selects.length; i++){
		selects[i].disabled = "true";
	}
	
</logic:equal>
</script>

</body>