<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.general.muster.hmuster.HmusterForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.struts.valueobject.UserView,
				com.hrms.struts.constant.WebConstant" %>
<%
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	String bosflag = "";
	if (userView != null) {
		bosflag = userView.getBosflag();
	}
%>
<% 
	Calendar S=Calendar.getInstance();
	int year=S.get(Calendar.YEAR);
	HmusterForm hmusterForm=(HmusterForm)session.getAttribute("hmusterForm");	
	ArrayList subPointList=hmusterForm.getSubPointList(); 
	String modelFlag=hmusterForm.getModelFlag();
 %>
<!--<script language="JavaScript" src="/js/meizzDate.js"></script> 日期js目前没有找到有关调用 先注释掉  26766 -->
<script type='text/javascript' src='../../../ext/ext6/ext-all.js'></script>
<script type='text/javascript' src='../../../ext/ext6/locale-zh_CN.js' ></script>
<script type='text/javascript' src='../../../ext/rpc_command.js'></script>
<link rel='stylesheet' href='../../../ext/ext6/resources/ext-theme.css' type='text/css' />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="../../../components/codeSelector/codeSelector.js"></script>


<script language="javascript">
var dt=(window.screen.availHeight - 30 - 460) / 2;  //获得窗口的垂直位置
var dl=(window.screen.availWidth - 10 - 790) / 2; //获得窗口的水平位置 
var userAgent = window.navigator.userAgent; //取得浏览器的userAgent字符串  
var isOpera = userAgent.indexOf("Opera") > -1;
var isIE = (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera); 
var now = new Date();
function initOption()
{
	var v="${hmusterForm.flag}";
	var v2="${hmusterForm.isResultTable}";
		
	if(v==1)
		document.hmusterForm.history.checked=true;
	else if(v>1)
	{
		if(document.hmusterForm.history[0])
			document.hmusterForm.history[0].checked=true;
		else
			document.hmusterForm.history.checked=true;
	}
	if(v2==0){
		if(document.hmusterForm.queryScope!=null)
			document.hmusterForm.queryScope.checked=true;
	}
	<%
	if(modelFlag.equals("81"))
	{
	%>
	   showB();
	<%
	}
	%>
	
}


function validateNum(obj)
{
	if(!checkNUM1(obj))
	{	
		obj.value="1";
		return;
	}
	else
	{
		if(obj.value<1||obj.value>1000)
		{
			alert(INPUT_NUMBER_RANGE);
			obj.value="1";
			return;
		}
	}
}


function closeAll()
{
    var a,b,c
    var flag=${hmusterForm.flag};        
    if(flag==3)
		a=eval("aa");		
	var b11=document.getElementById("b1");
	var b22=document.getElementById("b2");
	var b33=document.getElementById("b3");
	if(flag==3)
		a.style.display="none"; 
	if(b11)
    	b11.style.display="none"; 
    if(b22)
    	b22.style.display="none"; 
    if(b33)
    	b33.style.display="none"; 
}

function showA()
{	
	var a=eval("aa");
	var b11=document.getElementById("b1");
	var b22=document.getElementById("b2");
	var b33=document.getElementById("b3");
	a.style.display="block";
	if(b11) 
	   b11.style.display="none"; 
	if(b22)
	   b22.style.display="none"; 
    if(b33)
       b33.style.display="none"; 
}

function showB()
{		
		var flag=${hmusterForm.flag};
		var a;
		if(flag==3)
			a=eval("aa");	
		var b11=document.getElementById("b1");
		var b22=document.getElementById("b2");
		var b33=document.getElementById("b3");
		if(flag==3)
			a.style.display="none"; 
		if(b11)
    		b11.style.display="block"; 
    	if(b22)
	    	b22.style.display="block"; 
	    if(b33)
		    b33.style.display="block";
		if(hmusterForm.selectedPoint.options.length>0)
		{
			hmusterForm.selectedPoint.options[0].selected=true;
			if(hmusterForm.selectedPoint.fireEvent)
				hmusterForm.selectedPoint.fireEvent("onchange");
			else{
				hmusterForm.selectedPoint.onchange();
			}
		}
					
}

var codeseid;
function setFields()
{
	for(var i=0;i<hmusterForm.selectedPoint.options.length;i++)
	{
		if(hmusterForm.selectedPoint.options[i].selected==true)
		{
			var values=hmusterForm.selectedPoint.options[i].value;
			var codeid=values.split("/");
			var from="<bean:message key="hmuster.label.from"/>";
			var to="<bean:message key="hmuster.label.to"/>";
            var modelFlag = "${hmusterForm.modelFlag}";
			datepnl.style.display="none"; 
			if(codeid.length<2){
				var thecodeurl ="/train/traincourse/generalsearch.do?b_query=link&isPriv=1&muduleFlag=hmuster&allfields=1&fieldsetid="+codeid[0]; 
				var return_vo;
				if(isIE){
					return_vo= window.showModalDialog(thecodeurl, "", 
              		"dialogWidth:700px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no;"); 
				}else{
			      window.open(thecodeurl,"_blank",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top='+dt+'px,left='+dl+'px,width=710px,height=440px');
			      codeseid=codeid[0];
				}
				
    			if(return_vo!=null){
    				  hmusterForm.fromScope.value=return_vo;
    			      hmusterForm.toScope.value=codeid[0];
    			   
    			  
    			}
    			area.innerHTML="";
			}
			else if(codeid[2]=='D')
			{
				var month=now.getMonth()+1;
				if(month<10)
					month="0"+month;
				//非IE获取日期有问题
				var from_date=((isIE?now.getYear():now.getYear()+1900)-1)+"-"+month+"-"+now.getDate()
			    var to_date=(isIE?now.getYear():now.getYear()+1900)+"-"+month+"-"+now.getDate()

				//var aa="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+from+" <input type=text name=\"from.hzvalue\"  size=\"10\" styleClass=\"text\"  onfocus='inittime(false);setday(this);'  size='13' value='"+from_date+"'   readOnly  />&nbsp;"+to+" &nbsp;<input type=text name=\"to.hzvalue\"  size=\"10\" styleClass=\"text\"  value='"+to_date+"'   onfocus='inittime(false);setday(this);'  size='13'   readOnly   />";
				//var aa="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+from
				//+" <input type=text id=\"editor1\" name=\"from.hzvalue\"  size=\"10\" extra=\"editor\" dropDown=\"dropDownDate\" style=\"width:100px;font-size:10pt;text-align:left\"     value='"+from_date+"'  />&nbsp;"
				//+to+" &nbsp;<input type=text id=\"editor2\" name=\"to.hzvalue\"  size=\"10\"  extra=\"editor\" dropDown=\"dropDownDate\" style=\"width:100px;font-size:10pt;text-align:left\"  value='"+to_date+"' />&nbsp;";
				datepnl.style.display="block"; 	
				document.getElementById('editor1').value=from_date;
				document.getElementById('editor2').value=to_date;
				area.innerHTML="";
			}
			else if(codeid[1]==0)
			{
				/* 样式调整(组织机构高级花名册取数) xiaoyun 2014-8-10 start */
				//var aa="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+from+" <input type=text name=\"from.hzvalue\"  size=\"10\" styleClass=\"text\"   />&nbsp;"+to+" &nbsp;<input type=text name=\"to.hzvalue\"  size=\"10\" styleClass=\"text\"   />";
				var aa="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+from+" <input type=text name=\"from.hzvalue\"  size=\"10\" class=\"text4\"   />&nbsp;"+to+" &nbsp;<input type=text name=\"to.hzvalue\"  size=\"10\" class=\"text4\"   />";
				/* 样式调整(组织机构高级花名册取数) xiaoyun 2014-8-10 end */
				area.innerHTML=aa;
			}
			else
			{
			    var a1="";
			    var a2="";
			    var a3="";
			   /*  if(codeid[1]=="UN"||codeid[1]=='UM'||codeid[1]=="@K")
			    {
			         a1="<img src=\"/images/code.gif\" onclick='openCondCodeDialogsx(\""+codeid[1]+"\",\"from.hzvalue\");' align=\"middle\"/>&nbsp;";
			    	 a2="<img src=\"/images/code.gif\" onclick='openCondCodeDialogsx(\""+codeid[1]+"\",\"to.hzvalue\");' align=\"middle\"/>&nbsp;";
			    	 a3="<Input type='hidden' name='from.value' /><input type='hidden' name='to.value' /> ";
			    }
			    else
			    { */
			    	a1='<img src="../../../images/code.gif" id="fromhzvalue_0" plugin="codetree" codesetid="'+codeid[1]+'" inputname="from.hzvalue" valuename="from.value"   multiple ="false" onclick="codeClick([\'fromhzvalue_0\']);"/>';
			    	a2='<img src="../../../images/code.gif" id="tohzvalue_0" plugin="codetree" codesetid="'+codeid[1]+'" inputname="to.hzvalue" valuename="to.value"   multiple ="false" onclick="codeClick([\'tohzvalue_0\']);"/>';
			    	a3='<input type="hidden" name="from.value" /><input type="hidden" name="to.value" />';
			    	
		    	//	 a1="<img src=\"/images/code.gif\" onclick='openCondCodeDialog(\""+codeid[1]+"\",\"from.hzvalue\");' align=\"middle\"/>&nbsp;";
			    //	 a2="<img src=\"/images/code.gif\" onclick='openCondCodeDialog(\""+codeid[1]+"\",\"to.hzvalue\");' align=\"middle\"/>&nbsp;";
			    //	 a3="<Input type='hidden' name='from.value' /><input type='hidden' name='to.value' /> ";
			   // }
				/* 样式调整(员工管理高级花名册取数) xiaoyun 2014-8-10 start */
				//var aa=a3+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+from+" <input type=text name='from.hzvalue'  size=\"10\" styleClass=\"text\"  readOnly  /> "+a1+to+" <input type=text name='to.hzvalue'  size=\"10\" styleClass=\"text\"  readOnly  />&nbsp;"+a2; 
				var aa="<table><tr><td>"+a3+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+from+" <input type=text name='from.hzvalue'  size=\"10\" class=\"text4\"  readOnly  /></td><td>"+a1+"</td><td>"+to+"</td> <td><input type=text name='to.hzvalue'  size=\"10\" class=\"text4\"  readOnly  /></td><td>&nbsp;"+a2+"</td></tr></table>";
				/* 样式调整(员工管理高级花名册取数) xiaoyun 2014-8-10 end */
				area.innerHTML=aa;	
			}			
		}
	}

}

function returnValue(return_vo){
	
	if(return_vo!=null){
		  hmusterForm.fromScope.value=return_vo;
		  hmusterForm.toScope.value=codeseid;
	}
	area.innerHTML="";
}

function codeClick(eleId){
	setEleConnect(eleId);
}

function validates()
{
	var isHistory="${hmusterForm.flag}";
	if(isHistory>0)
	{
		var history;
		var countflag = "0";
		<%
		if(modelFlag.equals("81"))
		{
		%>
		   history=document.getElementById("history").value;
		<%}else{%>
	    	for(var i=0;i<document.hmusterForm.history.length;i++)
	    	{
	    		if(document.hmusterForm.history[i].checked)
		    	{
				
		    		 history=document.hmusterForm.history[i].value;
		    	}
	    	}
		<%}%>
		if(history==1)
		{
			
		}	
		else if(history==2){
			   var type="" ; //输入字符的类型
			   var formula = "";
			   for(var i=0;i<hmusterForm.selectedPoint.options.length;i++)
			   {
					if(hmusterForm.selectedPoint.options[i].selected==true)
					{
						var values=hmusterForm.selectedPoint.options[i].value;
						var codeid=values.split("/");	
						if(codeid.length<2){
							formula = codeid[0];
						}else
							type=codeid[2];
					}
			    }
			   	if(formula.length<1){
			   	if(document.getElementById("cflag"))
			   	{
		           if(document.getElementById("cflag").checked){
					  countflag="1";
				   }
				}		   
			   var oldInput1=document.getElementsByName("from.hzvalue");
			   var oldInput2=document.getElementsByName("to.hzvalue");
	    		   mytarget1=oldInput1[0]; 
	    		   mytarget2=oldInput2[0]; 
               <%
	         	if(!modelFlag.equals("81"))
	        	{
	        	%>
			   	if(mytarget1.value==""&&mytarget2.value=="")
			   	{
			     	alert(AT_LEAST_INPUT_COND)
			     	return ;
			   	}
			   	<%}%>
			   	if(type=='D')
	    		{
	    		   /*
				   if(checkDate(mytarget1)&&checkDate(mytarget2))
				   {
				   	if(mytarget1.value.length!=mytarget2.value.length)
				   	{
				   		alert("您输入的两个日期的长度不一致！");
				   		return ;
				   	}
				   }
				   else
				   {
				   	return;
				   } */
			     }
			     else if(type=='N')
			     {
			     	if(!(checkNUM2(mytarget1,15,5)&&checkNUM2(mytarget2,15,5)))
			     	{
			     		return;
			     	}		     	
			     }
			    
			     for(var i=0;i<hmusterForm.selectedPoint.options.length;i++)
			     {
					if(hmusterForm.selectedPoint.options[i].selected==true)
					{
					
						var values=hmusterForm.selectedPoint.options[i].value;
						var codeid=values.split("/");
			     			
						if(codeid[1]!=0)
						{	
							  var oldInput1=document.getElementsByName("from.value");
							   var oldInput2=document.getElementsByName("to.value");
					    		   mytarget1=oldInput1[0]; 
					    		   mytarget2=oldInput2[0];
						
							hmusterForm.fromScope.value=mytarget1.value;
							hmusterForm.toScope.value=mytarget2.value;
						}
						else
						{
							
							hmusterForm.fromScope.value=mytarget1.value;
							hmusterForm.toScope.value=mytarget2.value;
						
						}	
			     
			     	}
			     		
			     }
			    }else{
			    	if(document.getElementById("cflag"))
				    	if(document.getElementById("cflag").checked){//设置汇总不生效暂时修改办法 formula 长度大于1 时 无法设置是否汇总 设置自动查询条件带入汇总 导致两边不一致
						  countflag="1";
					   } 
			    }
		}	
		else if(history==3)
		{	
			var year=hmusterForm.year.value; 
			var month="1";
			if(isHistory=='4')
			    month=hmusterForm.month.value; 
			var count=hmusterForm.count.value; 
			
			if(year==null||year==""||month==null||month==""||count==null||count=="")
			{
				alert(YEAR_MONTH_NUMBER_REQUIRED);
				return ;
			}
			else
			{
			    if(isHistory=='4')
			    {
			       if(!checkNUM1(eval("hmusterForm.month")))
			          return;
			    }
				if(!(checkNUM1(eval("hmusterForm.year"))&&checkNUM1(eval("hmusterForm.count"))))
					return ;
				else
				{
					if(year<1900||year>2100)
					{
						alert(INPUT_CORRECT_YEAR);
						return;
					}
					if(month<1||month>12)
					{
						alert(INPUT_CORRECT_MONTH);
						return;
					}
				
				}
			
			}
		}
		
		document.getElementsByName("countflag")[0].value = countflag;
		hmusterForm.action="/general/muster/hmuster/select_muster_name.do?br_next2=next2";
		hmusterForm.submit();
	}else{
		var modelFlag = "${hmusterForm.modelFlag}";
		if(modelFlag=='15'){
			document.getElementsByName("startime")[0].value=document.getElementById("timestar").value
			document.getElementsByName("endtime")[0].value=document.getElementById("timeend").value
		}
		hmusterForm.action="/general/muster/hmuster/select_muster_name.do?br_next2=next2";
		hmusterForm.submit();
	}

}

function checkTime(times){
 	var result=times.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
 	if(result==null) return false;
 	var d= new Date(result[1], result[3]-1, result[4]);
 	return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate());
}
function timeCheck(obj){
	if(!checkTime(obj.value)){
		obj.value='';
	}
}
function goback()
{
	//hmusterForm.action="/general/muster/hmuster/select_muster_name.do?br_return=return";
/*	var inforFlag='${hmusterForm.infor_Flag}';
	if(inforFlag=='1') //人员
	{
		//hmusterForm.action='/general/muster/hmuster/select_muster_name.do?b_query=link&nFlag=3&a_inforkind=1';                   
		hmusterForm.action='/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&clears=1&operateMethod=direct&tabID=${hmusterForm.tabID}';
	}
	else if(inforFlag=='2') //机构
	{
		hmusterForm.action='/general/muster/hmuster/select_muster_name.do?b_query=link&nFlag=21&a_inforkind=2&tabID=${hmusterForm.tabID}';
	}
	else if(inforFlag=='3')//职位
	{
		                   
		hmusterForm.action='/general/muster/hmuster/select_muster_name.do?b_query=link&nFlag=41&a_inforkind=3&tabID=${hmusterForm.tabID}';
	}
	else
	{
		//hmusterForm.action='/general/muster/hmuster/select_muster_name.do?b_query=query';
		hmusterForm.action='/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&operateMethod=direct&tabID=${hmusterForm.tabID}';
	}*/
	//isAutoCount 行数是否自动计算
	hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&clears=1&operateMethod=direct&isAutoCount=${hmusterForm.isAutoCount}&tabID=${hmusterForm.tabID}";
	hmusterForm.submit();

}
function upblack(){
	hmusterForm.action="/general/inform/emp/output/printhroster.do?b_search=link&dbname=${hmusterForm.dbpre}&infor=${hmusterForm.infor_Flag}";
	hmusterForm.submit();
}


  function get_common_query(infor,dbpre,query_type,priv)
   {
        var dbpre_arr=new Array();
        var dbprelist="${hmusterForm.dbprelist}";
        dbpre_arr[0]=dbpre;
        if(infor=="5")  // 基准岗位
        	infor="9";
        if(dbpre=="ALL")
            dbpre=dbprelist;
            var height = 410;
            if(isIE6()){
                height += 20;
            }
         if(!priv)
         	priv = "false";
        
         
         var thecodeurl="/general/inform/search/gmsearch.do?b_query=link&type="+infor+"&a_code=UN&moduleFlag=hmuster&tablename="+$URL.encode(dbpre)+"&priv="+priv;
         if(isIE){
        	  var return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:700px; dialogHeight:"+height+"px;resizable:no;center:yes;scroll:no;status:no;");
         }else{
        	  window.open(thecodeurl,"_blank",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top='+dt+'px,left='+dl+'px,width=710px,height=410px');
         }
    
       //var  thecodeurl="/general/inform/search/gmsearch.do?b_query=link&visibleSave=1&resultType=1&changeResultTable=0&type="+infor+"&ps_flag=2&a_code=all&privflag=0&tablename="+dbpre;
      // var  return_vo= window.showModalDialog(thecodeurl, "", 
             // "dialogWidth:700px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:yes");
  		//var objlist=common_query(infor,dbpre_arr,query_type);
		//if(objlist && objlist.length >0){
			//var hashvo=new ParameterSet();
		   // hashvo.setValue("info",infor);
		   // hashvo.setValue("dbpre",dbpre); 
		  //  hashvo.setValue("objlist",objlist);
		    
		   //	var In_paramters="flag=1"; 	
			//var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:queryresult,functionId:'0550000007'},hashvo);		
		//}else{
			//alert("无相关数据");
		//}
   }   
	function queryresult(outparamters){
	}
function jinduo(){
	var x=document.body.clientWidth/2-300;
    var y=document.body.clientHeight/2-125;
	var waitInfo;
	waitInfo=eval("wait");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="block";
}
</script>
<hrms:themes />
<%
	if ("hl".equalsIgnoreCase(bosflag)) {
%>
<style>
.selectmusterscopeTable {
	margin-top: 10px;
}
</style>
<%
	}
%>
<html:form action="/general/muster/hmuster/select_muster_name">
<html:hidden name="hmusterForm" property="countflag"/>
<html:hidden name="hmusterForm" property="isReData"/>
      <table width="700px" border="0" cellpadding="0" cellspacing="0" align="center" class="selectmusterscopeTable">
          <tr height="20">
       		<td align='left' class="TableRow_lrt">
       		<bean:message key="hmuster.label.data_scope"/>
       		</td>              	      
          </tr> 
          <tr>
            <td class="framestyle">
               <table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0"> 
 				<logic:notEqual name="hmusterForm" property="searchResultFlag" value="no">
 				<logic:equal name="hmusterForm" property="isResultTable" value="1">	
 				<logic:notEqual name="hmusterForm" property="modelFlag" value="15">
 				<logic:notEqual name="hmusterForm" property="modelFlag" value="81">
	               	 <tr >  
	                      <td colspan="2"  height="30">
	                      <table>
	                        <tr><td>&nbsp;<html:radio name="hmusterForm" property="queryScope" value="1"/>&nbsp;<bean:message key="hmuster.label.search_result"/></td>
	                        <td> &nbsp;<img  src="/images/code.gif" onclick='get_common_query("${hmusterForm.infor_Flag}","${hmusterForm.dbpre}",1,"${hmusterForm.no_manager_priv}")' align="middle"/></td></tr>
	                      </table>
	                     </td>
	                 </tr> 
	                 </logic:notEqual>
	            </logic:notEqual>  
	     		</logic:equal>
		 		</logic:notEqual>  
		 		<logic:notEqual name="hmusterForm" property="modelFlag" value="15">
		 		<logic:notEqual name="hmusterForm" property="modelFlag" value="81">
	                 <tr>  
	                      <td colspan="2"  height="30">
	                      &nbsp;<html:radio name="hmusterForm" property="queryScope" value="2"/>&nbsp;<bean:message key="hmuster.label.all_record"/>    
	                     </td>
	                 </tr>
	                 </logic:notEqual>
	            </logic:notEqual> 
	            <logic:equal name="hmusterForm" property="modelFlag" value="15">
	              <tr>  
	                     <td colspan="2"  height="30">
	                        	汇&nbsp;&nbsp;&nbsp;&nbsp;总：
	                        		<hrms:optioncollection name="hmusterForm" property="combineFieldList" collection="list" />
				             <html:select name="hmusterForm" property="combineField" size="1" >
				             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
				             </html:select>
	                        		                    
	                     </tr>
	                 <tr>  
	                     <td colspan="2"  height="30">
	                        	<html:hidden name="hmusterForm" property="startime"/>
	                        	<html:hidden name="hmusterForm" property="endtime"/>
	                        	<bean:message key="gz.columns.taxdate"/> :<input type="text" name="timestar" id="timestar" value="${hmusterForm.startime}" extra="editor" onblur="timeCheck(this);" style="font-size:10pt;text-align:left" dropDown="dropDownDate">-
	                        	<input type="text" name="timeend" id="timeend" value="${hmusterForm.endtime}" extra="editor" onblur="timeCheck(this);" style="font-size:10pt;text-align:left" dropDown="dropDownDate">
	                     </td>
	                 </tr>
	               
	            </logic:equal>   
	            <logic:greaterEqual name="hmusterForm" property="flag" value="1" >
	            <logic:notEqual name="hmusterForm" property="modelFlag" value="81">
	            	<tr>
	                 	<td colspan="2"><hr style="height:1px;border-top:1px" class="complex_border_color"></td>
	                 </tr>
	                  <tr>  
	                      <td width="30%"  height="30">
	                      &nbsp;<input type="radio" name="history" value="1"  checked onclick="closeAll()"/>&nbsp;<bean:message key="hmuster.label.select_search_item1"/>
	                     </td>
	                     <td width="70%">
	                       &nbsp;
	                     </td>
	                     
	                  </tr>	
               </logic:notEqual>
	            </logic:greaterEqual>
	            <logic:greaterEqual name="hmusterForm" property="flag" value="3" >
	                  <tr>  
	                      <td   height="30">
	                      
	                      &nbsp;<html:radio name="hmusterForm" property="history" value="3"  onclick="showA()"/>&nbsp;<bean:message key="hmuster.label.select_search_item2"/>
	                      </td>
	                      <td>
	                     <div id="aa" style="display:none;">
	                     		<select name="year">
	                     			<% for(int i=0;i<11;i++){ %>
	                     				<option value="<%=(year-i)%>"><%=(year-i)%></option>
	                     			<% } %>
	                     			
	                     		</select>
	                     		<bean:message key="hmuster.label.year"/>
	                     		<logic:greaterEqual name="hmusterForm" property="flag" value="4" >
	                     		<select name="month">
	                     			<% for(int i=1;i<13;i++){
	                     				if(i<10){
	                     			 %>	              
	                     				<option value='0<%=i%>'>0<%=i%></option>
	                     			<%       }else{   %>
	                     				<option value='<%=i%>'><%=i%></option>
	                     			<% }  } %>
	                     		</select>
	                     		<bean:message key="hmuster.label.month"/>	
	                     		</logic:greaterEqual>                     
	                     			<!-- modify by xiaoyun 高级花名册取数 2014-8-12 start -->
	                     			<!-- <Input type='text' name='count' size=3 value='1' onchange='validateNum(this)' > -->
	                     			<Input type='text' class="text4" style="height: 20px;line-height: 20px;position: relative;top: -3px;" name='count' size=3 value='1' onchange='validateNum(this)' >
	                     			<!-- modify by xiaoyun 高级花名册取数 2014-8-12 end -->
	                     		<bean:message key="hmuster.label.count"/>
	                     		
	                     		
	                     
	                     </div>	   	
	                     </td>
	                  </tr>	

	            </logic:greaterEqual>
	            <%if(subPointList.size()>0){ %>
	            <logic:greaterEqual name="hmusterForm" property="flag" value="2" >
	                  <tr>  
	                      <td  height="30">
	                      &nbsp;<html:radio name="hmusterForm" property="history" value="2" onclick="showB()"  />&nbsp;<bean:message key="hmuster.label.select_search_item3"/>
	                     </td>
	                     <td>
	                    <logic:notEqual name="hmusterForm" property="modelFlag" value="81">
	                    	 <div id="b1" style="display:none;">
	                    </logic:notEqual>
	                    <logic:equal name="hmusterForm" property="modelFlag" value="81">
	                     	 <div id="b1">
	                    </logic:equal>
	                     	<hrms:optioncollection name="hmusterForm" property="subPointList" collection="list" />
				             <html:select name="hmusterForm" property="selectedPoint" size="1"  onchange="setFields()"  >
				             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
				             </html:select>
							</div>
	                    </td>
	                  </tr>	
	                  <tr >  
	                      <td colspan="2">
	                     
	                    	<div id="b2" style="display:none;">
	                     		<span id="area"></span>
	                     		<span id="datepnl" style="display:none">
	                     			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="hmuster.label.from"/><input type="text" name="from.hzvalue" size="10" extra="editor" class="text4" style="width:100px;font-size:10pt;text-align:left" 	dropDown="dropDownDate" id="editor1">
									<bean:message key="hmuster.label.to"/><input type="text" name="to.hzvalue" size="10" extra="editor" class="text4" style="width:100px;font-size:10pt;text-align:left" 	dropDown="dropDownDate" id="editor2">	                     		
	                     		</span>
	                     	</div>
	                     </td>
	                 </tr>
	                 <tr >  
	                      <td colspan="2">
	                    	<div id="b3" style="display:none;">
	                    	  <logic:notEqual name="hmusterForm" property="modelFlag" value="81">
	                     		<input type="checkbox" name="cflag" id="cflag" >&nbsp;按人员汇总
	                     		</logic:notEqual>
	                     	</div>
	                     </td>
	                 </tr>
	           </logic:greaterEqual>
	          <%}%>
	          <input type="hidden" name="fromScope"/><input type="hidden" name="toScope"/>                     
             </table>                   
            </td>
          </tr>
          <tr class="list3">
            <td align="left" colspan="4"  nowrap >
			<logic:equal name="hmusterForm" property="isRecords" value="1">
		        <html:radio name="hmusterForm" property="historyRecord" value="0"/>
                  	<bean:message key="hmuster.label.reGetData"/>
                  	&nbsp;&nbsp;
                  	<html:radio name="hmusterForm" property="historyRecord" value="1"/>
                  	<bean:message key="hmuster.label.privGetData"/>
           	</logic:equal>                         
			&nbsp;
          </td>
          </tr>                                                      
          <tr class="list3">
            <td align="left" colspan="4" height="35px">
	 			<html:button  styleClass="mybutton" property="br_next" onclick="goback();">
            		<bean:message key="button.query.pre"/>
	           	</html:button>
           	   	<html:button  styleClass="mybutton" property="br_next" onclick="validates()">
            		<bean:message key="button.query.next"/> 
	           </html:button>   
            </td>
          </tr>          
      </table>
      <div   id="wait" style='position:absolute;top:285;left:120;display:none;width:500px;heigth:250px'>
 
		<table border="1" width="50%" cellspacing="0" cellpadding="4" class="table_style" height="100" align="center">
			<tr>
			
				<td class="td_style" height=24>
					<bean:message key="hmuster.label.wait"/>
				</td>
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
		    <iframe src="javascript:false" style="position:absolute; visibility:inherit; top:0px; left:0px;width:285px;height:120px;z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';">
		    </iframe>	
	</div>
</html:form>
<script language="javascript">
	initOption();
</script>