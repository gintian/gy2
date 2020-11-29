<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="java.util.Date"%>
<%@page import="com.hjsj.hrms.businessobject.kq.machine.KqParam"%>
<%@page import="com.hjsj.hrms.utils.OperateDate"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="javascript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/meizzDate_saveop.js"></script>
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="/kq/kq.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<%
	int i=0;	
	int r=0;
	String name=null;
%>
<style>
  .num
  {
    text-align: right;
  }
</style>
<SCRIPT language=JavaScript>
 
   function change()
   {
      feastForm.action="/kq/feast_manage/managerdata.do?b_search=link&code=${feastForm.code}&kind=${feastForm.kind}&hols_status=${feastForm.hols_status}";
      feastForm.submit();
   } 
   function go_exp(hols_status)
   {
       feastForm.action="/kq/feast_manage/managerdata.do?b_exp=link&code=${feastForm.code}&kind=${feastForm.kind}&hols_status=${feastForm.hols_status}";
       feastForm.submit();      
   } 
    function check_count()
   {
     var hashvo=new ParameterSet();
     hashvo.setValue("code","${feastForm.code}");
     hashvo.setValue("kind","${feastForm.kind}");
     hashvo.setValue("hols_status","${feastForm.hols_status}");
     hashvo.setValue("year","${feastForm.kq_year}");
     var request=new Request({method:'post',onSuccess:showCheck,functionId:'15208000013'},hashvo);
   }
   function showCheck(outparamters)
   {
      var mess=outparamters.getValue("mess")
      if(confirm(mess))
      {
         go_count();
      }
   }
   function go_count()
   {
       var target_url;
       var winFeatures = "dialogHeight:400px; dialogLeft:320px;"; 
       target_url="/kq/feast_manage/managerdata.do?b_countdate=link&code=${feastForm.code}&kind=${feastForm.kind}&hols_status=${feastForm.hols_status}";
       //newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=356,height=274'); 
        var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:390px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
       if(!return_vo)
		return false;	
       if(return_vo.start!=''&&return_vo.end!='')
       {
	     var waitInfo=eval("wait");	   
	     waitInfo.style.display="block";   	     
         feastForm.action="/kq/feast_manage/managerdata.do?b_count=link&feast_start="+return_vo.start+"&feast_end="+return_vo.end+"&hols_status=${feastForm.hols_status}&dbpre="+return_vo.dbpre+"&clear_zone="+return_vo.clear_zone+"&count_fields="+return_vo.exp_fields+"&balance="+return_vo.balance+"&balanceEnd="+return_vo.balanceEnd;
	     feastForm.submit();	
       }
   } 
    function IsDigit() 
   { 
    return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
   } 
    function getdate(tt)
   {
       var strvalue=tt.value;
       strvalue=strvalue.replace(/\-/g,".");       
       tt.value=strvalue;
   }
   
   
   function change_print(hols_status)
   {
       feastForm.action="/kq/feast_manage/managerdata.do?b_print=link&nFlag=81&relatTableid=${feastForm.relatTableid}&hols_status=${feastForm.hols_status}";
       feastForm.submit();       
                
       
   } 

function outPrintApp(){
	//document.mysearchform.submit();
	var returnURL = getEncodeStr("${feastForm.returnURL}");
	var urlstr = "/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=81&relatTableid=${feastForm.relatTableid}&closeWindow=1";
		urlstr+="&returnURL="+returnURL;
	window.showModalDialog(urlstr,1, 
		"dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes");
}
   function saveinfo(hols_status)
   {
      feastForm.action="/kq/feast_manage/managerdata.do?b_saveinfo=link&hols_status=${feastForm.hols_status}";
      feastForm.submit();
   }
   function deleteR(hols_status)
   {
	   var len=document.feastForm.elements.length;
	   var iscorrect = false;
       for (i=0;i<len;i++)
       {
           if (document.feastForm.elements[i].type=="checkbox")
           {
              if(document.feastForm.elements[i].checked==true)
	           {
	              iscorrect = true;
	           }
           }
       }
       if(iscorrect)
       {
	       if(confirm("确认要删除所选记录吗？"))
	       {
	           feastForm.action="/kq/feast_manage/managerdata.do?b_delete=link&hols_status=${feastForm.hols_status}";
	           feastForm.submit();
	       }
       }else
       {
			alert("请选择人员！");
       }
      
   }
   function MusterInitData()
   {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
	   //只有一个人员库时不显示选项 
	   hide_nbase_select('select_pre');
   }
   function view_single(a0100,nbase,q1709,start_date,end_date)
   {
       var iWidth=600; 
       var iHeight=360;
       var iTop = (window.screen.availHeight-30-iHeight)/2;
       var iLeft = (window.screen.availWidth-10-iWidth)/2;
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
       target_url="/kq/feast_manage/leave_record.do?b_search=link&a0100="+a0100+"&nbase="+nbase+"&q1709="+q1709+"&start_date="+start_date+"&end_date="+end_date;
       newwindow=window.open(target_url,'_blank','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top='+iTop+',left='+iLeft+',width='+iWidth+',height='+iHeight); 
   }
	function selectinfo(hols_status){
    // var target_url;
      // var winFeatures = "dialogHeight:600px; dialogLeft:450px;"; 
       //target_url="/kq/feast_manage/select/selectfiled.do?b_init=link`hols_status="+hols_status+"`kq_year="+${feastForm.kq_year};
       //newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=596,height=354');
      //var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
     // var return_vo= window.showModalDialog(iframe_url,window, 
       // "dialogWidth:596px; dialogHeight:375px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes"); 
		var winFeatures = "dialogWidth:715px; dialogHeight:375px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes"
		var target_url = "/kq/query/searchfiled.do?b_init=link`table=Q17";
		var iframe_url = "/general/query/common/iframe_query.jsp?src="+target_url;
		var return_vo= window.showModalDialog(iframe_url,1,winFeatures);
		if(return_vo){
			feastForm.action="/kq/feast_manage/managerdata.do?b_search=link&select_flag=2&selectResult="+return_vo;
			feastForm.submit();
		}
	}
  function selectflag()
  {
      feastForm.action="/kq/feast_manage/managerdata.do?b_search=link&code=${feastForm.code}&kind=${feastForm.kind}&hols_status=${feastForm.hols_status}&select_flag=1";
      feastForm.submit();
  }
  function quitRe()
   {
      window.location="/kq/register/daily_register.do?br_quit=link";
   }
   function viewAll()
   {
       feastForm.action="/kq/feast_manage/managerdata.do?b_search=link&code=${feastForm.code}&kind=${feastForm.kind}&hols_status=${feastForm.hols_status}&select_sturt=0";
       feastForm.submit();
   }
   function selectquery()
   {
      feastForm.action="/kq/feast_manage/managerdata.do?b_search=link&code=${feastForm.code}&kind=${feastForm.kind}&hols_status=${feastForm.hols_status}&select_sturt=1&select_flag=1";
      feastForm.submit();
   }
   function excecuteExcel()
   {
	var hashvo=new ParameterSet();			
	var In_paramters="exce=excel";	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showExcel,functionId:'15208000015'},hashvo);
   }	
   function showExcel(outparamters)
   {
	var url=outparamters.getValue("excelfile");	
	window.location.target = "mil_body";
	window.location.href="/servlet/vfsservlet?fileid="+url+"&fromjavafolder=true";
   }
   var checkflag = "false";
  function selAll()
   {
      var len=document.feastForm.elements.length;
       var i;
    if(checkflag == "false")
    {
        for (i=0;i<len;i++)
        {
         if (document.feastForm.elements[i].type=="checkbox")
          {
             
            document.feastForm.elements[i].checked=true;
          }
        }
        checkflag = "true";
    }else
    {
        for (i=0;i<len;i++)
        {
          if (document.feastForm.elements[i].type=="checkbox")
          {
             
            document.feastForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    }      
  } 
  
  
  function outHolidayTemplete(){
	  //var sql=$F('sqlstr')+$F('strwhere')+$F('orderby');
		var hashvo=new ParameterSet();	
		hashvo.setValue("tablename","Q17");
	   var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'2020051989'},hashvo);
	}
  
  function showfile(outparamters)
	{
	   var outName=outparamters.getValue("outName");
		var name=outName.substring(0,outName.length);
		window.location.target="_blank";
		window.location.href="/servlet/vfsservlet?fileid="+name+"&fromjavafolder=true";
	}


  
	//导入年假excel
	function inputTemplete(holidayType){
		var theurl="/kq/feast_manage/managerdata.do?br_selectfile=link`holidayType="+holidayType;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
	    var return_vo= window.showModalDialog(iframe_url, 'mytree_win', 
	      		"dialogWidth:500px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");		    				
	  	// alert(return_vo)
	  	 if(return_vo){
	  	 	var waitInfo=eval("wait");	//显示进度条
		    waitInfo.style.display="block";
		  
	   		form1.action="/kq/feast_manage/managerdata.do?b_search=query&amp;hols_status=06";
	      	form1.submit(); 
		}
	}

  
	
  
</SCRIPT>
 <%
 	Date current = new Date();
 	String overTimeValidate = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_LIMIT();
 	if(overTimeValidate == null  || "".equals(overTimeValidate))
 		overTimeValidate = "0";
 	int validate = Integer.parseInt(overTimeValidate);
 	Date date = OperateDate.addDay(current,0 - validate);
 	String from = OperateDate.dateToStr(date, "yyyy.MM.dd");
 	String to = OperateDate.dateToStr(current, "yyyy.MM.dd");
 %>
 
 <div style="height:expression(document.body.clientHeight-50);width:expression(document.body.clientWidth-22); ">
<html:form action="/kq/feast_manage/managerdata" styleId="form1">
<logic:equal name="feastForm" property="error_flag" value="0">
    <table border="0" cellspacing="0"  align="left" cellpadding="0" width="100%" >
      <logic:notEqual value="q33" name="feastForm" property="hols_status">
      <tr>
       <td>
          <table>
            <tr>
               <td>
               <table border="0" cellspacing="0"  align="left" cellpadding="0">
                <tr>
	                <td>
		                <hrms:priv func_id="27041"> 
		                  <button extra="button" onclick="javascript:go_count();">计算</button>
		                </hrms:priv>
		                <hrms:priv func_id="27042">
		                  &nbsp;<button extra="button" onclick="javascript:go_exp('${feastForm.hols_status}');">公式</button>
		                </hrms:priv>
		                <hrms:priv func_id="27043">
		                  &nbsp;<button extra="button" onclick="javascript:outPrintApp();">打印</button>
		                </hrms:priv>   
		                <hrms:priv func_id="27043">
		                  &nbsp;<button extra="button" onclick="javascript:excecuteExcel('${feastForm.hols_status}');">导出</button>
		                </hrms:priv>              
		                <hrms:priv func_id=""> 
		                  &nbsp;<button extra="button" onclick="javascript:selectinfo('${feastForm.hols_status}');">条件查询</button>
		         	    </hrms:priv>
		         	    <logic:equal name="feastForm" property="select_flag" value="1">
		         	      <html:hidden name="feastForm" property="select_flag" value="0"/>
		                   &nbsp;<button extra="button" onclick="javascript:selectflag();">全显</button> 
		                </logic:equal>
		                <hrms:priv func_id="27040"> 
		                  &nbsp;<button extra="button" onclick="javascript:saveinfo('${feastForm.hols_status}');"><bean:message key="button.save"/></button>
		         	    </hrms:priv>
		         	    <hrms:priv func_id="27040">
		                  &nbsp;<button extra="button" onclick="javascript:deleteR('${feastForm.hols_status}');">删除</button>
		                </hrms:priv>
		                <hrms:priv func_id="27046">
		                   &nbsp;<button extra="button" onclick="javascript:outHolidayTemplete();">下载模板</button>
		                </hrms:priv>
		                <hrms:priv func_id="27048">
		                   &nbsp;<button extra="button" onclick="javascript:inputTemplete('${feastForm.hols_status}');">数据导入</button>
		                </hrms:priv>
	                </td>
                </tr>
              </table>
              </td>
              <td align= "left" nowrap>&nbsp;&nbsp;
               <html:select name="feastForm" property="select_pre" size="1" onchange="change();">
                  <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
               </html:select>
             </td>
               <td>
               &nbsp;&nbsp;
                <hrms:optioncollection name="feastForm" property="yearlist" collection="list" />
	         <html:select name="feastForm" property="kq_year" size="1" onchange="change();">
                <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                </html:select> 
                <html:hidden name="feastForm" property="returnURL" styleClass="text"/>
              </td>
              <td align= "right" nowrap>
              	姓名   
              </td>             
              <td align= "left" nowrap>
                   <input type="text" name="select_name" value="${feastForm.select_name}" class="inputtext" style="width:100px;font-size:10pt;text-align:left">
                   &nbsp;<button extra="button" onclick="javascript:selectquery();">查询</button> 
             </td>             
           </tr>
          </table>
       </td>
      </tr>
      </logic:notEqual>
      <logic:equal value="q33" name="feastForm" property="hols_status">
      	<tr>
			<td>
				<table>
					<tr>
						<td>
							<html:select name="feastForm" property="select_pre" size="1" onchange="change();">
			                	<html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
			                </html:select>
						</td>
						<td align= "right" nowrap>
              				姓名   
              			</td>             
		                <td align= "left" nowrap>
		                    <input type="text" name="select_name" value="${feastForm.select_name}" class="inputtext" style="width:100px;font-size:10pt;text-align:left;">
		                    &nbsp;<button extra="button" onclick="javascript:selectquery();">查询</button> 
		             	</td>  
		             	<td>
		             		&nbsp;<bean:message key="kq.kqself.feast.duration"/>&nbsp;<%=from %>&nbsp;-&nbsp;<%=to %>	  
		             	</td>
					</tr>
				</table>
			</td>
		</tr>
      </logic:equal>
      <tr>
        <td width="100%" >
        <div id="tbl-container" class="fixedDiv common_border_color" style="border: 1px solid  #94B6E6;">
           <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
   	     <thead>
              <tr class="fixedHeaderTr">
              <logic:notEqual value="q33" name="feastForm" property="hols_status">
              <td align="center" class="TableRow" style="border-left: none;border-top: none;border-right:none;" nowrap>
					<input type="checkbox" name="aa" value="true" onclick="selAll()">&nbsp;
               </td>
                <logic:iterate id="element"  name="feastForm"  property="fieldlist" indexId="index"> 
          
                 <logic:equal name="element" property="visible" value="true">
                   <td align="center" class="TableRow" style="border-top: none;border-right: none;" nowrap>
                    <bean:write  name="element" property="itemdesc"/>&nbsp; 
                   </td>
                 </logic:equal>
               </logic:iterate>
              </logic:notEqual>
              <!-- ----------------------------------------------------------------------------------------------- -->
              <logic:equal value="q33" name="feastForm" property="hols_status">  	    
               <logic:iterate id="element"  offset="0" length="1" name="feastForm"  property="fieldlist" indexId="index"> 
          
                 <logic:equal name="element" property="visible" value="true">
                   <td align="center" class="TableRow" style="border-left:none;border-top: none;border-right: none;" nowrap>
                    <bean:write  name="element" property="itemdesc"/>&nbsp; 
                   </td>
                 </logic:equal>
               </logic:iterate>
               <logic:iterate id="element"   offset="1"  name="feastForm"  property="fieldlist" indexId="index"> 
          
                 <logic:equal name="element" property="visible" value="true">
                   <td align="center" class="TableRow" style="border-top: none;border-right: none;" nowrap>
                    <bean:write  name="element" property="itemdesc"/>&nbsp; 
                   </td>
                 </logic:equal>
               </logic:iterate>
               </logic:equal> 
   	     </thead>
   	      	 <hrms:paginationdb id="element" name="feastForm" sql_str="feastForm.strsql" table="" where_str="" columns="feastForm.columns" order_by="feastForm.orderby" page_id="pagination" pagerows="${feastForm.pagerows}" indexes="indexes">
	         <%
               if(i%2==0){ 
             %>
             <tr class="trShallow">
             <%
               }else{
             %>
             <tr class="trDeep">
             <%}
             %>  
      		   <logic:notEqual value="q33" name="feastForm" property="hols_status">
               <td align="center" class="RecordRow" style="border-left: none;border-top:none;border-right:none;" nowrap>   
                <hrms:checkmultibox name="feastForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
               </td>
	           <logic:iterate id="info"  name="feastForm"  property="fieldlist" indexId="index">
                  <logic:equal name="info" property="visible" value="true">
		           <%
	            	FieldItem item=(FieldItem)pageContext.getAttribute("info");
	            	name=item.getItemid();
					if(name.equals("f1"))
					{
	               %>
	               <td align="center" class="RecordRow"  style="border-top: none;border-right:none;" nowrap>
            			<bean:write name="element" property="f1" filter="true"/>&nbsp;
            	   </td>  
            	   <%}else{ %>
                  <!--字符型-->
                    <logic:notEqual name="info" property="codesetid" value="0">
                     <logic:notEqual name="info" property="itemid" value="e0122">
                          <td align="left" class="RecordRow" style="border-top: none;border-right:none;" nowrap>                      
                            <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                             &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                            
                          </td> 
                     </logic:notEqual>
                     <logic:equal name="info" property="itemid" value="e0122">
                     	<td align="left" class="RecordRow" style="border-top: none;border-right:none;" nowrap>
                     		<hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${feastForm.uplevel}"/>  	      
                             &nbsp;<bean:write name="codeitem" property="codename" />&nbsp; 
                     	</td>
                     </logic:equal>      
                    </logic:notEqual>
                    <logic:equal name="info" property="codesetid" value="0">
                      <logic:equal name="info" property="itemtype" value="A">
                         <logic:equal name="info" property="itemid" value="a0101"> 
                           <td align="left" class="RecordRow" style="border-top: none;border-right:none;" nowrap>
                            &nbsp;
                             <bean:define id="a01001" name="element" property="a0100"/>
					         <bean:define id="nbase1" name="element" property="nbase"/>
					         <bean:define id="q17091" name="element" property="q1709"/>
					         <bean:define id="q17z11" name="element" property="q17z1"/>
					         <bean:define id="q17z31" name="element" property="q17z3"/>
					         <%
					         		//参数加密
					    		     String a0100 = PubFunc.encrypt(a01001.toString());
							         String nbase = PubFunc.encrypt(nbase1.toString());
							         String q1709 = PubFunc.encrypt(q17091.toString());
							         String q17z1 = PubFunc.encrypt(q17z11.toString());
							         String q17z3 = PubFunc.encrypt(q17z31.toString());
					         %>
                             <a href="###" onclick="view_single('<%=a0100 %>','<%=nbase %>','<%=q1709 %>','<%=q17z1 %>','<%=q17z3 %>')">
                            <bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
                            </a>
                           </td>                        
                        </logic:equal>
                        <logic:notEqual name="info" property="itemid" value="a0101"> 
                          <td align="left" class="RecordRow" style="border-top: none;border-right:none;" nowrap>
                            &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
                          </td>                        
                       </logic:notEqual>
                       </logic:equal>
                       <logic:equal name="info" property="itemtype" value="N">
                           <logic:equal name="info" property="itemid" value="q1707"> 
                             <td align="right" class="RecordRow" style="border-top: none;border-right:none;" style="font-size:12px; " nowrap>
                             &nbsp;<html:text name="feastForm" property='<%="pagination.curr_page_list["+r+"].q1707"%>' 
                                      size="8" maxlength="10" styleClass="text num" style="border:0;"/>&nbsp;
                            </td>                        
                          </logic:equal>  
                          <logic:notEqual name="info" property="itemid" value="q1707"> 
                             <td align="left" class="RecordRow" style="border-top: none;border-right:none;" nowrap>
                              &nbsp;<html:text name="feastForm" property='<%="pagination.curr_page_list["+r+"]."+name%>' 
                                      size="8" maxlength="10" styleClass="text num" onkeypress="event.returnValue=IsDigit();"/>&nbsp;
                             </td> 
                          </logic:notEqual>
                       </logic:equal>
                       <logic:equal name="info" property="itemtype" value="D">
                          <td align="left" class="RecordRow" style="border-top: none;border-right:none;" nowrap>
                            &nbsp;<html:text name="feastForm" style="border-right: none;" property='<%="pagination.curr_page_list["+r+"]."+name%>' 
                                    size="10" maxlength="10" styleClass="text" onclick="getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);" onchange="rep_dateValue(this);" readonly="true"/>&nbsp;
                          </td> 
                       </logic:equal>     
                    </logic:equal>
                    <%} %>
                  </logic:equal>
              </logic:iterate>     
              </logic:notEqual>
      		  <logic:equal value="q33" name="feastForm" property="hols_status">
      		  <logic:iterate id="info"  offset="0" length="1"  name="feastForm"  property="fieldlist" indexId="index">
	           <%
            	FieldItem item=(FieldItem)pageContext.getAttribute("info");
               %> 
                  <logic:equal name="info" property="visible" value="true">
		                    <logic:equal name="info" property="codesetid" value="0">
		                    	<logic:equal name="info" property="itemtype" value="N">
		                    		<td align="right" class="RecordRow" style="border-left:none;border-top:none;border-right: none;" nowrap>
		                            	&nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
		                        	</td>
		                    	</logic:equal>
		                    	<logic:equal name="info" property="itemtype" value="A">
			                    	<td align="left" class="RecordRow" style="border-left:none;border-top:none;border-right: none;" nowrap>
			                            &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
			                        </td>
		                    	</logic:equal>
		                    </logic:equal>
		                  	
		                    <logic:notEqual name="info" property="codesetid" value="0">
		                    	<td align="left" class="RecordRow" style="border-left:none;border-top:none;border-right: none;" nowrap>
		                     		<hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
		                             &nbsp;<bean:write name="codeitem" property="codename" />&nbsp; 
		                        </td>
		                    </logic:notEqual>
                  </logic:equal>
              </logic:iterate>  
               <logic:iterate id="info"  offset="1" name="feastForm"  property="fieldlist" indexId="index">
	           <%
            	FieldItem item=(FieldItem)pageContext.getAttribute("info");
               %> 
                  <logic:equal name="info" property="visible" value="true">
		                    <logic:equal name="info" property="codesetid" value="0">
		                    	<logic:equal name="info" property="itemtype" value="N">
		                    		<td align="right" class="RecordRow" style="border-top:none;border-right: none;" nowrap>
		                            	&nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
		                        	</td>
		                    	</logic:equal>
		                    	<logic:equal name="info" property="itemtype" value="A">
			                    	<td align="left" class="RecordRow" style="border-top:none;border-right: none;" nowrap>
			                            &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
			                        </td>
		                    	</logic:equal>
		                    </logic:equal>
		                  	
		                    <logic:notEqual name="info" property="codesetid" value="0">
		                    	<td align="left" class="RecordRow" style="border-top:none;border-right: none;" nowrap>
		                     		<hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
		                             &nbsp;<bean:write name="codeitem" property="codename" />&nbsp; 
		                        </td>
		                    </logic:notEqual>
                  </logic:equal>
              </logic:iterate>   
      		  </logic:equal>
	     <%i++;%>  
	     <%r++;%>
	     </tr>	     
             </hrms:paginationdb>
           </table>
           </div>
       <table  width="100%" class="RecordRowTop0" align="left">
       <tr>        
		<td valign="bottom" class="tdFontcolor">
		<hrms:paginationtag name="feastForm" pagerows="${feastForm.pagerows}" property="pagination" scope="page" refresh="true">
					    </hrms:paginationtag>
	
		</td>
	        <td  align="right" nowrap class="tdFontcolor">
		     <p align="right"><hrms:paginationdblink name="feastForm" property="pagination" nameId="feastForm" scope="page">
				</hrms:paginationdblink>
		</td>
	      </tr>
          </table>       
        </td>
      </tr>
      <logic:equal value="dxt" name="feastForm" property="returnvalue">  
      <tr>  
        <td width="100%" height="35px;">
          <hrms:tipwizardbutton flag="workrest" target="3" formname="feastForm"/> 
       </td>
      </tr>   
       </logic:equal>
    </table> 
</logic:equal>
<logic:notEqual name="feastForm" property="error_flag" value="0">
<script language="javascript">
var error_str=kqErrorProcess('<bean:write name="feastForm"  property="error_flag"/>','<bean:write name="feastForm"  property="error_message"/>','<bean:write name="feastForm"  property="error_return"/>');
document.write(error_str);
</script>
</logic:notEqual> 
</html:form>
</div>
<iframe name="mysearchframe" style="display: none;"></iframe>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style common_background_color" height=24>正在处理数据请稍候...</td>
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
<script language="javascript">
 MusterInitData();
 </script>