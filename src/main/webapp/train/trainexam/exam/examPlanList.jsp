<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.train.trainexam.exam.TrainExamPlanForm"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="/ajax/basic.js"></script>
<script type="text/javascript" src="/ajax/common.js"></script>
<script type="text/javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/codetree.js"></script>

<style>
.fixedDiv11
{ 
  overflow:auto; 
  height:expression(document.body.clientHeight-150);
  width:expression(document.body.clientWidth-22); 
  border: #C4D8EE 1px solid;
}
</style>
<script language="javascript">
  function execute(execType)
  {
    var unSelInfo;
    var confirmInfo;
    var isPaused = false;
    var r5400s="";
    if("del" == execType) //删除
    {
      unSelInfo = CHOISE_DELETE_NOT;
      confirmInfo = CONFIRMATION_DEL;
    }
    else if("start" == execType) //启动
    {
      unSelInfo = TRAIN_EXAMPLAN_SELSTART;
      confirmInfo = TRAIN_EXAMPLAN_START_CONFIRM;    
    }
    else if("pause" == execType) //暂停
    {
      unSelInfo = TRAIN_EXAMPLAN_SELPAUSE;
      confirmInfo = TRAIN_EXAMPLAN_PAUSE_CONFIRM;
    }
    else if("forcedel" == execType)//删除历史
    {
      unSelInfo = CHOISE_DELETE_NOT;
      confirmInfo = TRAIN_EXAMPLAN_FORCEDEL;
    }
    else if("publish" == execType)//发布成绩
    {
      unSelInfo = TRAIN_EXAMPLAN_SELPUBLISH;
      confirmInfo = TRAIN_EXAMPLAN_PUBLISH_CONFIRM;
    }
    else
    {
      unSelInfo = TRAIN_EXAMPLAN_SELARCHIVE;
      confirmInfo = TRAIN_EXAMPLAN_ARCHIVE_CONFIRM; 
    }
    
    //检查选中状态
  	var len=document.trainExamPlanForm.elements.length;
    var uu;
    for (var i=0;i<len;i++)
    {
        if (document.trainExamPlanForm.elements[i].type=="checkbox")
        {
           if(document.trainExamPlanForm.elements[i].checked==true)
           {
             uu="dd";
             break;
            }
        }
    }
    
    //检查选中计划状态是否符合操作要求
    if((uu=="dd")&&("forcedel"!=execType))
    {
	    var planTab = document.getElementById("PlanTab");
	    var rowCnt = planTab.rows.length;
	    var colCnt = planTab.rows[0].cells.length;
	    var ids = "";

	    for (var i=1;i<rowCnt;i++)
	    {   
	        if(planTab.rows[i].cells[0].getElementsByTagName("input")[0].checked==true)
	        { 
	           var state;
	           var planName;
	           var planId;
	           
               state=trimStr(document.getElementById("r5411."+(i-1)).value);
	           if(getIEVersion()!="")
	           {
	             //state=trimStr(planTab.rows[i].cells[6].innerText);
	             planName=trimStr(planTab.rows[i].cells[1].innerText);
	             planId=planTab.rows[i].cells[colCnt-1].innerText; 
	           }
	           else
	           {
	             //state=trimStr(planTab.rows[i].cells[6].textContent);
	             planName=trimStr(planTab.rows[i].cells[1].textContent);
	             planId=planTab.rows[i].cells[colCnt-1].textContent;
	           }  
	                       
	           ids = ids + trim(planId) + ",";
	           r5400s += planId+",";
	           if("pause" == execType)
	           {
	              if('05'!=state)
	              {
	                unSelInfo = "【" + planName + "】不是执行中的计划，不允许暂停！";
	                uu="";
	                break;
	              }
	           }
	           else if("publish" == execType)
	           {
	        	  if(state=="04")
        		  {
	        		unSelInfo = "【" + planName + "】已经发布过成绩了，不需要重复发布！";
	                uu="";
	                break;
        		  }
	        	  
	              if(state!="06")
	              {
	                unSelInfo = "【" + planName + "】还没有结束，不可以发布成绩！";
	                uu="";
	                break;
	              }
	           }
	           else if("start" == execType)
	           {
	              if((state!="01")&&(state!="09"))
	              {
	                unSelInfo = "【" + planName + "】不允许重新启动！只有起草或暂停的计划可以启动！";
	                uu="";
	                break;
	              }
	              else if(state=="09")
	              {
	                isPaused = true;
	              }
	          }
            else if("del" == execType)
            {
              if((state!="01")&&(state!="09"))
              {
                unSelInfo = "【" + planName + "】不允许删除！只有起草或暂停的计划可以删除！";
                uu="";
                break;
              }
            }
            else if("archive" == execType)
            {
              if((state!="06")&&(state!="04"))
              {
                unSelInfo = "【" + planName + "】不允许归档！只有结束或已发布成绩的计划可以进行归档！";
                uu="";
                break;
              }
            }
	        }
	    }
    }

    if(uu!="dd")
    {
       alert(unSelInfo);
       return false;
    }

    if("del" == execType) //删除
      trainExamPlanForm.action = "/train/trainexam/exam/plan.do?b_delete=link&fd=0";
    else if("start" == execType) //启动
      trainExamPlanForm.action = "/train/trainexam/exam/plan.do?b_start=link&sendmsg=1";
    else if("pause" == execType) //暂停
      trainExamPlanForm.action = "/train/trainexam/exam/plan.do?b_pause=link";
    else if("forcedel" == execType)//删除历史
      trainExamPlanForm.action = "/train/trainexam/exam/plan.do?b_delete=link&fd=1";    
    else if("publish" == execType)//发布成绩
      trainExamPlanForm.action = "/train/trainexam/exam/plan.do?b_publish=link";

    if("start" == execType)
    {
      if(confirm(confirmInfo))
      {
        if(isPaused&&checkParam(r5400s)&&(!confirm("暂停计划重新启动时，是否发送通知？\n确定：发送\n取消：不发送"))){
          trainExamPlanForm.action = "/train/trainexam/exam/plan.do?b_start=link&sendmsg=0"; 
        }else{
        	jindu1();
        }
        trainExamPlanForm.submit();
      }
    }
    else if("archive" == execType)
    {
      archive(ids);
    }
    else if("publish" != execType)
    { 
  	  if(confirm(confirmInfo))
  	    trainExamPlanForm.submit();  
    }
    else
      publishPlan(ids);
  }
  
  
  function checkParam(ids){
	  var flag = false;
	    var hashvo=new ParameterSet();
	    hashvo.setValue("r5400s",ids);
	    var request=new Request({asynchronous:false,onSuccess:setFlag,functionId:'2020081013'},hashvo);
	    function setFlag(param){
	    	if(param.getValue("flag") == "true")
	    		flag = true;
	    }
	    return flag;
  }
  
  function publishPlan(ids)
  {    
    var hashvo=new ParameterSet();
    hashvo.setValue("r5400",ids);
    hashvo.setValue("statetype","paper");

    var request=new Request({asynchronous:false,onSuccess:executePublish,functionId:'2020081072'},hashvo);
  } 

  function executePublish(outparamters)
  { 
    var flag = "";
    var hint = "以下考试计划中试卷还未阅完：\n";
    var planNames = "";
 
    if(outparamters!=null)
    {
      flag=outparamters.getValue("flag");
      planNames=outparamters.getValue("plannames"); 
      if("error"==flag)
      {
        hint = hint + planNames + '\n\n确定要发布成绩吗？';
        if(confirm(hint)){
        	jindu2();
          trainExamPlanForm.submit();
        }
        return;  
      }
    }

    if(flag == "ok")
    { 
      var confirmInfo = TRAIN_EXAMPLAN_PUBLISH_CONFIRM;
      if (confirm(confirmInfo)){
    	  jindu2();
        trainExamPlanForm.submit();
      }
	}
  }  
  
  function viewPlan(id)
  {
      trainExamPlanForm.action="/train/trainexam/exam/plan.do?b_edit=link&e_flag=view&r5400="+id;
      trainExamPlanForm.submit();  
  }
  function submitUp(id)
  {
      trainExamPlanForm.action="/train/trainexam/exam/plan.do?b_edit=link&e_flag=up&r5400="+id;
      trainExamPlanForm.submit();  
  }
  function submitNew()
  {
      trainExamPlanForm.action="/train/trainexam/exam/plan.do?b_edit=link&e_flag=add";
      trainExamPlanForm.submit();
        
  }
  
  function submitUpOrder(id)
  {
      trainExamPlanForm.action="/train/trainexam/exam/plan.do?b_order=link&e_flag=uporder&r5400=" + id;
      trainExamPlanForm.submit();  
  }
  
  function submitDownOrder(id)
  {
      trainExamPlanForm.action="/train/trainexam/exam/plan.do?b_order=link&e_flag=downorder&r5400=" + id;
      trainExamPlanForm.submit();  
  }
  
  // 查询提交
  function exchange() 
  {
      trainExamPlanForm.action="/train/trainexam/exam/plan.do?b_query=link";
      trainExamPlanForm.submit();
  }
  
  function setParam(planId)
  {
	var theurl="/train/trainexam/exam/plan.do?b_setparam=link`r5400=" + planId; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl); 
	var return_vo= window.showModalDialog(iframe_url, 'setparam_win', 
	           "dialogWidth:460px; dialogHeight:330px;resizable:no;center:yes;scroll:yes;status:no");
	if(!return_vo)
	 return;  
  }
  
  function finishPlan(id)
  {
      var hashvo=new ParameterSet();
	  hashvo.setValue("r5400",id);
	  hashvo.setValue("statetype","exam");
  
	  var request=new Request({asynchronous:false,onSuccess:executeFinish,functionId:'2020081072'},hashvo);
  } 

  function executeFinish(outparamters)
  {
    var flag = "";
    var hint = "确定要收卷并结束当前考试计划吗？";
    var id = "";
    if(outparamters!=null)
    {
      flag=outparamters.getValue("flag");
      id=outparamters.getValue("id"); 
      if("error"==flag)
        hint = hint + "\n提示：有人员还未完成考试（未考或正考），考试将被强制结束。";
    }

    if (confirm(hint))
    {
      trainExamPlanForm.action = "/train/trainexam/exam/plan.do?b_finish=link&r5400=" + id;
      trainExamPlanForm.submit();
    }
  }
  
  //归档
	function archive(ids)
	{ 
	  var theurl="/train/request/resultFiled.do?b_query=link`type=3`id=" + ids;
	  var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
	  var return_vo= window.showModalDialog(iframe_url, 'resultFiled_win', 
	              "dialogWidth:550px; dialogHeight:455px;resizable:no;center:yes;scroll:yes;status:no");   
	  if(!return_vo)
	    return false;    
	  if(return_vo.flag=="true")
	  {   
	   // courseTrainForm.action="/train/request/trainsData.do?b_query=link&a_code="+a_code+"&model="+model;
	   // courseTrainForm.submit(); 
	  } 
	}
	// 进度条
	function jindu1(){
		//新加的，屏蔽整个页面不可操作
		document.all.ly.style.display="";   
		document.all.ly.style.width=document.body.clientWidth;   
		document.all.ly.style.height=document.body.clientHeight; 
		
		var x=(window.screen.width-700)/2;
	    var y=(window.screen.height-500)/2; 
		var waitInfo=eval("wait1");
		waitInfo.style.top=y;
		waitInfo.style.left=x;
		waitInfo.style.display="";
	}
	function jindu2(){
		//新加的，屏蔽整个页面不可操作
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
</script>
<hrms:themes/>
<%
	int i=0;
	TrainExamPlanForm examPlanForm=(TrainExamPlanForm)session.getAttribute("trainExamPlanForm");
	int maxOrder = examPlanForm.getMaxOrder();
	
	int editPriv = 0;
%>

<hrms:priv func_id="3238302">
  <% editPriv = 1; %>
</hrms:priv>
<div id="ly" style="position:absolute;top:0px;FILTER:alpha(opacity=0);background-color:#FFF;z-index:2;left:0px;display:none;"></div> 
<div id='wait1' style='position:absolute;top:285;left:80;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					正在启动考试计划,请稍候......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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
<div id='wait2' style='position:absolute;top:285;left:80;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					正在发布成绩,请稍候......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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
<html:form action="/train/trainexam/exam/plan">
  <table border="0" cellspacing="0"  align="center" cellpadding="0" width="100%"  style="margin-left: 2px;margin-top: 5px;" >
   <tr>
     <td width="100%">
       &nbsp;<bean:message key="train.examplan.status"/><span style="vertical-align: middle;">
       <html:select name="trainExamPlanForm" property="status" size="1" onchange="exchange();">
         <html:optionsCollection name="trainExamPlanForm" property="statusList" label="dataName" value="dataValue"/> 
       </html:select></span>
       
       &nbsp;<bean:message key="train.examplan.papershowstyle"/><span style="vertical-align: middle;">
       <html:select name="trainExamPlanForm" property="showStyle" size="1" onchange="exchange();">
         <html:optionsCollection name="trainExamPlanForm" property="showStyleList" label="dataName" value="dataValue"/> 
       </html:select>  </span>
       
       &nbsp;<bean:message key="train.examplan.name"/>
       <html:text name="trainExamPlanForm" styleClass="text4" property="planName"></html:text>
      <span style="vertical-align: middle;"> <input type="button" name="b_filter" value="<bean:message key='button.query'/>" class="mybutton" onclick="exchange();"/>     </span>
     </td>
   </tr>
   <tr>
     <td style="padding-top: 5px;">
       <div id="divid" class="fixedDiv11 common_border_color" style="padding: 0;">
        <table id="PlanTab" width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
           <thead>
                <tr class="fixedHeaderTr">
                 <td align="center" class="TableRow"  nowrap style="border-left: none;border-top: none;">
                   <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'/>
                 </td>  
                 <td align="center" class="TableRow" style="border-top: none;border-left: none;" width="30%" nowrap>
                    <bean:message key="train.examplan.name"/>
                 </td> 
                 <td align="center" class="TableRow" style="border-top: none;border-left: none;" width="17%" nowrap>
                    <bean:message key="train.examplan.begindate"/>
                 </td>  
                 <td align="center" width="17%" class="TableRow" style="border-top: none;border-left: none;" nowrap>
                    <bean:message key="train.examplan.enddate"/>
                 </td>                    
                 <td align="center" width="8%" class="TableRow" style="border-top: none;border-left: none;" nowrap>
                    <bean:message key="train.examplan.examtimelen"/>         
                 </td>    
                 <td align="center" class="TableRow" width="8%" style="border-top: none;border-left: none;" nowrap>
                   <bean:message key="train.examplan.papershowstyle"/>
                 </td>     
                 <td align="center" class="TableRow" width="7%" style="border-top: none;border-left: none;" nowrap>
                   <bean:message key="train.examplan.status"/>
                 </td>     
                 <td align="center" class="TableRow" style="border-top: none;border-left: none;" nowrap>
                   <bean:message key="train.examplan.operate"/>
                 </td>
                 <hrms:priv func_id="3238312">
                 <td align="center" class="TableRow" width="4%" nowrap style="border-right: 0px;border-top: none;border-left: none;">
                   <bean:message key="train.examplan.order"/>
                 </td> 
                 </hrms:priv>
                 <td align="center" class="TableRow" style="display:none;border-top: none;" nowrap>
                   planid
                 </td> 
               </tr>                      
           </thead>
           <% pageContext.setAttribute("maxOrder",Integer.valueOf(maxOrder)); %>              
            <hrms:paginationdb id="element" name="trainExamPlanForm" 
              sql_str="trainExamPlanForm.sqlstr" table="" 
              where_str="trainExamPlanForm.where"
              columns="trainExamPlanForm.column" 
              order_by="order by nOrder" pagerows="${trainExamPlanForm.pagerows}" page_id="pagination" indexes="indexes" >
              <bean:define id="planid" name="element" property="r5400"/>
              <%String r5400 = SafeCode.encode(PubFunc.encrypt(planid.toString()));%>
             <% if(i%2==0){ %>
               <tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'');">
               <%  }else{ %>
               <tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'');">
               <%}%>
               
                <td align="center" class="RecordRow"  nowrap style="border-top: none;border-left: none;">
                   <hrms:checkmultibox name="trainExamPlanForm" property="pagination.select" value="true" indexes="indexes"/>
                </td>                 
                <td align="left" class="RecordRow" style="border-top: none;border-left: none;"  nowrap>               
                  &nbsp;<bean:write name="element" property="r5401" filter="true"/>
                </td>   
                <td align="center" class="RecordRow" style="border-top: none;border-left: none;"  nowrap>               
                  <bean:write name="element" property="r5405" filter="true"/>
                </td>  
                <td align="center" class="RecordRow" style="border-top: none;border-left: none;" nowrap>               
                  <bean:write name="element" property="r5406" filter="true"/>
                </td> 
                <td align="right" class="RecordRow"  style="border-top: none;border-left: none;" nowrap>               
                  <bean:write name="element" property="r5407" filter="true"/>&nbsp;
                </td>  
                <td align="center" class="RecordRow" style="border-top: none;border-left: none;" nowrap>     
                  <logic:equal name="element" property="r5409" value="1">
                    <bean:message key="train.examplan.paperstyle.all"/>&nbsp;
                  </logic:equal>
                  <logic:equal name="element" property="r5409" value="2">
                    <bean:message key="train.examplan.paperstyle.single"/>&nbsp;
                  </logic:equal> 
                </td> 
                <td align="center" class="RecordRow" style="border-top: none;border-left: none;" nowrap> 
                <input type="hidden" id="r5411.<%=i %>" value="<bean:write name="element" property="r5411" />">
                  <hrms:codetoname codeid="23" name="element" codevalue="r5411" codeitem="codeitem" scope="page"/>         
                  <bean:write name="codeitem" property="codename" />                            
                </td>  
                 <td class="RecordRow" style="border-top: none;border-left: none;" nowrap align="left">   
                    &nbsp;           
                    
                    <logic:equal name="element" property="r5411" value="01">
                      <% if(1==editPriv){ %>                  
                      <a href="###" onclick="submitUp('<%=r5400 %>');">
                        <bean:message key="button.edit" />
                      </a>
                      <% } else { %>
                      <a href="###" onclick="viewPlan('<%=r5400 %>');">                 
                        <bean:message key="button.view" />
                      </a>
                      <% } %>
                    </logic:equal>
                    <logic:equal name="element" property="r5411" value="09">
                      <% if(1==editPriv){ %> 
                      <a href="###" onclick="submitUp('<%=r5400 %>');">
                        <bean:message key="button.edit" />
                      </a>
                      <% } else { %>
                      <a href="###" onclick="viewPlan('<%=r5400 %>');">                 
                        <bean:message key="button.view" />
                      </a>
                      <% } %>
                    </logic:equal>
                    <logic:equal name="element" property="r5411" value="04">
                      <a href="###" onclick="viewPlan('<%=r5400 %>');">                 
                        <bean:message key="button.view" />
                      </a>
                    </logic:equal>
                    <logic:equal name="element" property="r5411" value="05">
                      <a href="###" onclick="viewPlan('<%=r5400 %>');">                 
                        <bean:message key="button.view" />
                      </a>
                    </logic:equal>
                    <logic:equal name="element" property="r5411" value="06">
                      <a href="###" onclick="viewPlan('<%=r5400 %>');">                 
                        <bean:message key="button.view" />
                      </a>
                    </logic:equal>
                    
                   <hrms:priv func_id="3238309">  
                     <a href="/train/trainexam/exam/student.do?b_org=link&model=1&planid=<%=r5400 %>">
                      <bean:message key="train.examplan.selectstudent"/>
                     </a> 
                   </hrms:priv>
                    
                   <hrms:priv func_id="3238310">  
                     <a href="###" onclick="setParam('<%=r5400 %>');">
                      <bean:message key="train.examplan.param"/>
                     </a> 
                   </hrms:priv>
                   
                   <hrms:priv func_id="3238311">
	                   <logic:equal name="element" property="r5411" value="05">
	                       <a href="###" onclick="finishPlan('<%=r5400 %>');">
	                         <bean:message key="train.examplan.askforpaper"/>
	                       </a>
	                   </logic:equal>
                   </hrms:priv>
                 </td> 
                 
                 <hrms:priv func_id="3238312">
	                 <td class="RecordRow" align="center" width="50" nowrap style="border-top: none;border-left: none;border-right: none;">
	                   <% if(i>0){ %>            
	                   <a href="###" onclick="submitUpOrder('<%=r5400 %>');">
	                    <img src="/images/up01.gif" alt="<bean:message key="button.previous" />" width="12" height="17" border=0>
	                   </a> 
	                   <% }else {%>
	                     &nbsp;&nbsp;
	                   <% } %>
	                   <logic:notEqual name="element" property="norder" value="${maxOrder}">                   
	                     <a href="###" onclick="submitDownOrder('<%=r5400 %>');">
	                      <img src="/images/down01.gif" alt="<bean:message key="button.next" />" width="12" height="17" border=0>
	                     </a>
	                   </logic:notEqual>
	                   <logic:equal name="element" property="norder" value="${maxOrder}">                   
	                     &nbsp;&nbsp;
	                   </logic:equal>
	                 </td> 
	                 </hrms:priv>
	                 <td style="display:none;">
	                   <%=r5400 %>
	                 </td>	               
               <%i++;%>  
         </tr>       
            </hrms:paginationdb>
        </table>
        </div>
        </td>
      </tr> 
      <tr><td>
      <div style="width:expression(document.body.clientWidth-22);">
      <table width="100%" class="RecordRowP"  align="center">
      <tr>
        <td valign="bottom" class="tdFontcolor">
           <hrms:paginationtag name="trainExamPlanForm"
                pagerows="${trainExamPlanForm.pagerows}" property="pagination"
                scope="page" refresh="true"></hrms:paginationtag>
        </td>
        <td  align="right" nowrap class="tdFontcolor">
         <p align="right">
         <hrms:paginationdblink name="trainExamPlanForm" property="pagination" nameId="trainExamPlanForm" scope="page">
         </hrms:paginationdblink>
        </td>
      </tr>
     </table>
     </div>
    </td>
    <tr>
      <td align="left" style="height:35px;">
        <hrms:priv func_id="3238301">
          <input type="button" name="tt" value="<bean:message key="button.insert" />"  class="mybutton" onclick="submitNew();">
        </hrms:priv>
        
        <hrms:priv func_id="3238303">
          <input type="button" name="tdf" value="<bean:message key="button.delete" />"  class="mybutton" onclick="execute('del');">
        </hrms:priv>
        
        <hrms:priv func_id="3238304">
          <input type="button" name="tdf" value="<bean:message key="train.examplan.forcedelete" />"  class="mybutton" onclick="execute('forcedel');">
        </hrms:priv>
        
        <hrms:priv func_id="3238305">
          <input type="button" name="start" value="<bean:message key="train.examplan.start" />"  class="mybutton" onclick="execute('start');">
        </hrms:priv>
        
        <hrms:priv func_id="3238306">
          <input type="button" name="pause" value="<bean:message key="train.examplan.pause" />"  class="mybutton" onclick="execute('pause');">
        </hrms:priv>
        
        <hrms:priv func_id="3238307">
          <input type="button" name="publish" value="<bean:message key="train.examplan.publish" />"  class="mybutton" onclick="execute('publish');">
        </hrms:priv>
        
        <hrms:priv func_id="3238308">
          <input type="button" name="arch" value="<bean:message key="train.examplan.archive" />" class="mybutton" onclick="execute('archive');">
        </hrms:priv>
        
        <hrms:tipwizardbutton flag="workrest" target="il_body" formname="trainExamPlanForm"/> 
      </td>
    </tr>
  </table>    
</html:form>