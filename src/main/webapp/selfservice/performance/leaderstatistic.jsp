<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
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
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
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
<jsp:useBean id="statisticForm" class="com.hjsj.hrms.actionform.performance.StatisticForm" scope="session"/>
<script>
	function doSubmit(value)
	{
		if(statisticForm.planNum.value=="#")
		{
			alert('请选择活动计划');
			return false;
		}
		else
		{
		    if(value==3)
			{
			     var o=eval('p');	
		         o.style.display="none";
                 window.open("/performance/showkhresult/showTotalEvaluate.do?b_query=link&planid="+statisticForm.planNum.value+"&objectid=${statisticForm.objectId}&type=5","tbi_body");
			}
			else if(value==4)
			{
				 window.open("/performance/showkhresult/showDirectionAnalyse.do?b_init1=link&objectid=${statisticForm.objectId}&operate=0","il_body");
			}
			else if(value!=2)
			{
			   //location.href="/selfservice/performance/statistic.do?b_search=link&picFlag="+value+"&planFlag=1";
			      statisticForm.action="/selfservice/performance/leaderstatistic.do?b_search=link&picFlag="+value+"&planFlag=1";
	                      statisticForm.target="mil_body";
	                      statisticForm.submit();
			}
			else
		    {
			  //location.href="/selfservice/performance/statistic.do?b_query3=link&picFlag="+statisticForm.planNum.value+"&planFlag=1";
			      statisticForm.action="/selfservice/performance/statistic.do?b_query3=link&picFlag="+statisticForm.planNum.value+"&planFlag=1";
	                      statisticForm.target="mil_body";
	                      statisticForm.submit();
			}
		  	
		}
	}
		
    //活动计划
	function doSubmitplan(value)
	{
		if(value=="#")
		{
			alert('请选择活动计划');
			return false;
		}
		else
		{
		
		
			if(statisticForm.drawingFlag.value==3)
			{
			     var o=eval('p');	
		         o.style.display="none"; 
				 window.open("/performance/showkhresult/showTotalEvaluate.do?b_query=link&planid="+statisticForm.planNum.value+"&objectid=${statisticForm.objectId}&type=5","tbi_body");
			}
			else if(statisticForm.drawingFlag.value==4)
			{
				 window.open("/performance/showkhresult/showDirectionAnalyse.do?b_init1=link&objectid=${statisticForm.objectId}&operate=0","i_body");
			}
			else if(statisticForm.drawingFlag.value!=2)
			{
			  //location.href="/selfservice/performance/statistic.do?b_search=link&planNum="+value+"&planFlag=1";
			    
			    statisticForm.action="/selfservice/performance/statistic.do?b_search=link&planNum="+value+"&planFlag=1";
                            statisticForm.target="mil_body";
                            statisticForm.submit();
			}				
			else
			{
			  //location.href="/selfservice/performance/statistic.do?b_query3=link&picFlag="+value+"&planFlag=1";	
			    statisticForm.action="/selfservice/performance/statistic.do?b_query3=link&picFlag="+value+"&planFlag=1";	
                            statisticForm.target="mil_body";
                            statisticForm.submit();
			}
				
		}
	}
	
	
	function set(){
		var chart = jfreechartSet("${statisticForm.title}","");
		if(chart != null){
		
			//location.href="/selfservice/performance/statistic.do?b_search=link&picFlag=1&planFlag=1&chartParameters="+chart;
		            statisticForm.action="/selfservice/performance/statistic.do?b_search=link&picFlag=1&planFlag=1&chartParameters="+chart;
                            statisticForm.target="mil_body";
                            statisticForm.submit();
		}else{
			return;
		}
	}
	
	function go_return()
	{
	      statisticForm.action="/selfservice/performance/statistic.do?br_leaderreturn=link";
              statisticForm.target="mil_body";
              statisticForm.submit();
	}
	
	function excecutePDF()
{
        var hashvo=new ParameterSet();        
        hashvo.setValue("nid","${statisticForm.nid}");
        hashvo.setValue("tabid","${statisticForm.tabid}");
        if(${statisticForm.cardparam.queryflagtype}==1)
        {
           hashvo.setValue("cyear","${statisticForm.cardparam.cyear}");
        }else if(${statisticForm.cardparam.queryflagtype}==3)
        {
           hashvo.setValue("cyear","${statisticForm.cardparam.csyear}");
        }else if(${statisticForm.cardparam.queryflagtype}==4)
        {
           hashvo.setValue("cyear","${statisticForm.cardparam.csyear}");
        }        
        hashvo.setValue("userpriv","noinfo");
        hashvo.setValue("istype","1");        
        hashvo.setValue("cmonth","${statisticForm.cardparam.cmonth}");
        hashvo.setValue("season","${statisticForm.cardparam.season}");
        hashvo.setValue("ctimes","${statisticForm.cardparam.ctimes}");
        hashvo.setValue("cdatestart","${statisticForm.cardparam.cdatestart}");
	hashvo.setValue("cdateend","${statisticForm.cardparam.cdateend}");
	hashvo.setValue("infokind","5");
	hashvo.setValue("plan_id","${statisticForm.planNum}");
	hashvo.setValue("querytype","${statisticForm.cardparam.queryflagtype}");	
    var In_paramters="exce=PDF";  
    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showPDF,functionId:'90100130017'},hashvo);
	
}	
function showPDF(outparamters)
{
 
    var url=outparamters.getValue("url");
    var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url,"pdf");	
}
		
</script>
<%
	int i=0;
	int count=Integer.parseInt(statisticForm.getItemTotalCount());
	int k=0;
%>

<html:form action="/selfservice/performance/statistic">

 <table border="0" width="100%" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
 <tr class="list3" >
               <td align="center" nowrap width="100" ></td>
		<td align="center" nowrap width="100" >考核计划</td>
 		<td align="left" nowrap  width="200">
       		 		
            				<html:select name="statisticForm" property="planNum" onchange="doSubmitplan(this.value);" size="1">
                              <html:optionsCollection property="planList" value="dataValue" label="dataName"/>
        		    		</html:select>  
			</td>
 		    <td width="150" >
 		                <hrms:optioncollection name="statisticForm" property="enrol_list" collection="list" />
      				<html:select name="statisticForm" property="drawingFlag" size="1" onchange="doSubmit(this.value);"> 
            		  <logic:notEqual name="statisticForm" property="model" value="1">   
            		    <html:option value="1">&nbsp;&nbsp;&nbsp;图形&nbsp;&nbsp;&nbsp;</html:option>
            			<html:option value="0">&nbsp;&nbsp;&nbsp;表格&nbsp;&nbsp;&nbsp;</html:option>
            			<html:option value="2">&nbsp;&nbsp;&nbsp;考核评语&nbsp;&nbsp;</html:option>
            			<html:option value="3">&nbsp;&nbsp;&nbsp;总体评价&nbsp;&nbsp;</html:option>
            		   </logic:notEqual>
            			<logic:equal name="statisticForm" property="model" value="1">
            			<html:option value="3">&nbsp;&nbsp;&nbsp;总体评价&nbsp;&nbsp;</html:option>
            			<html:option value="4">&nbsp;&nbsp;&nbsp;趋势分析图&nbsp;&nbsp;</html:option>
            			</logic:equal>
            			<html:options collection="list" property="dataValue" labelProperty="dataName"/> 
            			</html:select>
            		</td>
            	<td align='left'>
            		<div id="b" style="display:none;" >
	            			<input type="button" value="设置" onclick="set()" class="mybutton">
 			</div>	
 			<div id="p" style="display:none;" >
	            	<input type="button" value="<bean:message key="button.cardpdf"/>" onclick="excecutePDF()" class="mybutton">
 		        </div>
            	</td>
          
 		<td>
 		<input type="button" value="<bean:message key="button.return"/>" onclick="go_return();" class="mybutton">
 		   
 		</td>
 					
	</tr>
</table>
<logic:equal name="statisticForm" property="drawingFlag" value="2">
<script language='javascript'>
	
	var a_desc=eval("statisticForm.desc");
	var context="${statisticForm.appraise}";
	var contexts=context.split('<br>')
	var a_t=''
	if(context.indexOf('<br>')==-1)
		a_t=context;	
	else
	{
		for(var i=0;i<contexts.length;i++)
		{
			a_t+=contexts[i]+'\n\r';
		}
	}
	if(a_desc)
		a_desc.value=a_t;
</script>
</logic:equal>


<script language='javascript'>
	var o=eval('b');
	var a_value=document.statisticForm.drawingFlag.value;
	if(a_value==1)
	{
		o.style.display="block"; 
	}
	else
	{
		o.style.display="none"; 
	}
</script>

<script language='javascript'>
	var o=eval('p');
	var a_value=document.statisticForm.drawingFlag.value;	
	if(a_value.indexOf("P")!=-1)
	{
		o.style.display="block"; 
	}
	else
	{
		o.style.display="none"; 
	}
</script>
</html:form>
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
</script>