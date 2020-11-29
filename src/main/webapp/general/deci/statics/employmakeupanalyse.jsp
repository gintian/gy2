<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
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
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.general.deci.statics.MakeupAnalyseForm"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.hrms.struts.taglib.CommonData"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
	MakeupAnalyseForm makeupAnalyseForm=(MakeupAnalyseForm)session.getAttribute("makeupAnalyseForm");
	String crosstabtype=(String)request.getAttribute("crosstabtype");
	crosstabtype = crosstabtype==null?"":crosstabtype;
	ArrayList subIndexList = (ArrayList)makeupAnalyseForm.getSubIndexList();
	ArrayList statIdList = (ArrayList)makeupAnalyseForm.getStatIdList();
	String firstStatId="";
	if(statIdList!=null&&statIdList.size()>0){
		firstStatId=statIdList.get(0).toString();
	}
	int size=0;
	if(subIndexList!=null){
		size = subIndexList.size();
	}
	ArrayList statOptionList = (ArrayList)makeupAnalyseForm.getStatOptionList();
	ArrayList statNameList = (ArrayList)makeupAnalyseForm.getStatNameList();
%>
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
<script LANGUAGE=javascript>
   function change(obj)
   {
     var dbpre=obj.value;
     var substat='${makeupAnalyseForm.substat}';
   	 if(substat==""){
	     makeupAnalyseForm.action="/general/deci/statics/employmakeupanalyse.do?b_search=link&dbpre=" +dbpre;
   	 }else{
	     makeupAnalyseForm.action="/general/deci/statics/employmakeupanalyse.do?b_search=link&dbpre=" +dbpre+"&substat="+substat;
   	 }
     makeupAnalyseForm.submit();
   }
   function query()
   {
     jindu();
   	 var substat='${makeupAnalyseForm.substat}';
   	 if(substat==""){
	     makeupAnalyseForm.action="/general/deci/statics/employmakeupanalyse.do?b_search=link";
   	 }else{
	     makeupAnalyseForm.action="/general/deci/statics/employmakeupanalyse.do?b_search=link&substat="+substat;   	 	
   	 }
     makeupAnalyseForm.submit();
   }
   function changestatid(obj)
   {
     var statid=obj.value;
     makeupAnalyseForm.action="/general/deci/statics/employmakeupanalyse.do?b_search=link&dbpre=&statid=" + statid+"&lexprId=";//add by xiegh on 20171009 bug:31838
     makeupAnalyseForm.submit();
   }
   function resultclick()
   {
        if(makeupAnalyseForm.result.checked==true)
        {
            makeupAnalyseForm.action="/general/deci/statics/employmakeupanalyse.do?b_search=link&result=1";
            makeupAnalyseForm.submit();
        }
        else
        {
            makeupAnalyseForm.action="/general/deci/statics/employmakeupanalyse.do?b_search=link&result=0";
            makeupAnalyseForm.submit();
        }
   }
   function changechar(obj)
   {
      var type=obj.value;
      makeupAnalyseForm.action="/general/deci/statics/employmakeupanalyse.do?char_type="+type;
      makeupAnalyseForm.submit();
   }
   function choosechar(obj){
      makeupAnalyseForm.action="/general/deci/statics/employmakeupanalyse.do?char_type="+obj;
      makeupAnalyseForm.submit();
   }
   function testchart(e)
   {
     var name=e.name;    
     if(name!="")
     {
      	//name=getEncodeStr(name);    
      	var obj=document.getElementsByName('dbpre');        	
      	var nbase="";  
      	obj=obj[0];
      	for(var i=0;i<obj.options.length;i++)
        {
          if(obj.options[i].selected)
          {    	    
    	    nbase=obj.options[i].value;
    	    break;
          }
        }
        //tiany 注释 支持全部人员库统计图穿透
      	//if(nbase=="ALL")
      	//{
      	//   alert("请选择人员库！");
      //	   return false;
      	//}
      	var substat='${makeupAnalyseForm.substat}';
      	if(substat==""){
      		makeupAnalyseForm.action="/general/static/commonstatic/statshow.do?b_data=data&infokind=1&statid=${makeupAnalyseForm.statid}&showLegend="+$URL.encode(name)+"&showflag=1&flag=jgfx&userbases=${makeupAnalyseForm.dbpre}&userbase=${makeupAnalyseForm.dbpre}&querycond=${makeupAnalyseForm.a_code}&home=0&crosstabtype=3";
      	}else{
	      	var subIndex='${makeupAnalyseForm.subIndex}';
	      	if(subIndex==""||subIndex=="start"){
	      		subIndex=0;
	      	}
	      	if(subIndex=="next"){
				var commlexr='${makeupAnalyseForm.commlexr}';
				commlexr = getEncodeStr(commlexr);
	      		var commfacor = '${makeupAnalyseForm.commfacor}';
				commfacor=getEncodeStr(commfacor);
	      		makeupAnalyseForm.action="/general/static/commonstatic/statshow.do?b_data=data&infokind=1&statid=${makeupAnalyseForm.statid}&showLegend="+$URL.encode(name)+"&subIndex="+subIndex+"&showflag=1&flag=zqct&userbases=${makeupAnalyseForm.dbpre}&userbase=${makeupAnalyseForm.dbpre}&querycond=${makeupAnalyseForm.a_code}&home=0&crosstabtype=3&commlexr="+commlexr+"&commfacor="+commfacor+"&substat="+substat+"&firstStatId=<%=firstStatId%>";
	      	}else{
	      		makeupAnalyseForm.action="/general/deci/statics/employmakeupanalyse.do?b_search=link&infokind=1&statid=${makeupAnalyseForm.statid}&showLegend="+$URL.encode(name)+"&subIndex="+subIndex+"&showflag=1&userbases=${makeupAnalyseForm.dbpre}&userbase=${makeupAnalyseForm.dbpre}&querycond=${makeupAnalyseForm.a_code}&home=0&crosstabtype=3";      		
	      	}
      	}
      	makeupAnalyseForm.submit();
     }
   }
   function guidance(url){
       makeupAnalyseForm.action=url;         
       makeupAnalyseForm.submit();
   }
   function openpage(url)
   {
        var obj=document.getElementsByName('dbpre');        	
      	var nbase="";  
      	obj=obj[0];
      	for(var i=0;i<obj.options.length;i++)
        {
          if(obj.options[i].selected)
          {    	    
    	    nbase=obj.options[i].value;
    	    break;
          }
        }
      	if(nbase=="ALL")
      	{
      	   //alert("请选择人员库！");
      	   //return false;
      	   nbase = "";
      	   for(var i=0;i<obj.options.length;i++)
        	{
	          if(obj.options[i].value != "ALL")
	          {    	    
	    	    nbase+=obj.options[i].value+"`";
	          }
	        }
      	   
      	}
      	makeupAnalyseForm.action=url+"&userbases="+nbase+"&home=0&crosstabtype=3";
      	makeupAnalyseForm.submit();
   }
   function exportExcel()
   {
   		var obj=document.getElementsByName('dbpre');        	
      	var nbase="";  
      	obj=obj[0];
      	for(var i=0;i<obj.options.length;i++)
        {
          if(obj.options[i].selected)
          {    	    
    	    nbase=obj.options[i].value;
    	    break;
          }
        }
      	if(nbase=="ALL")
      	{
      	   nbase = "";
      	   for(var i=0;i<obj.options.length;i++)
        	{
	          if(obj.options[i].value != "ALL")
	          {    	    
	    	    nbase+=obj.options[i].value+",";
	          }
	        }
      	}
		var hashvo=new ParameterSet();
		hashvo.setValue("userbases",nbase);
		hashvo.setValue("statid","${makeupAnalyseForm.statid}");
		hashvo.setValue("a_code","${makeupAnalyseForm.a_code}");
		hashvo.setValue("result","${makeupAnalyseForm.result}");
		hashvo.setValue("lexprId","${makeupAnalyseForm.lexprId}");
		var request=new Request({method:'post',asynchronous:false,onSuccess:showExcel,functionId:'0560201007'},hashvo);
   }
   function showExcel(outparamters)
   {
    var filename=outparamters.getValue("filename");	
//	var win=open("/servlet/DisplayOleContent?filename="+filename,"excel");
	//20/3/17 xus vfs改造
    var win=open("/servlet/vfsservlet?fileid="+filename+"&fromjavafolder=true","excel");
   }
   function jindu(){
	  var waitInfo=document.getElementById("wait");
	  waitInfo.style.display="block";
   }
	
   function closejindu(){
	  var waitInfo=document.getElementById("wait");
	  waitInfo.style.display="none";
   }
</script> 
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes></hrms:themes>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<html:form action="/general/deci/statics/employmakeupanalyse">
    <table width="90%" border="0" cellspacing="1"  align="center" cellpadding="1" style="margin: 10 0 0 0;">
    <hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="false" paraValue="" 
                 sql="makeupAnalyseForm.dbcond" collection="list" scope="page"/>
                 <bean:size id="length" name="list" scope="page"/>
      <tr id="trselectcondition">
        <td id="tdselectcondition" align="left" nowrap >
        <!-- 
                 <span  <logic:lessThan value="2" name="length">style="display: none"</logic:lessThan> >
         -->
         <!-- zgd 2014-8-25 任务3911 常用统计信息集设置中指定了人员库后不显示人员库选项 -->
                 <span id="myspan" <logic:equal value="1" name="makeupAnalyseForm" property="basesize">style="display: none;"</logic:equal><%if(size>0){ %>style="display: none;"<%} %> >
                 
                 <bean:message key="label.query.dbpre"/>
              <html:select name="makeupAnalyseForm" property="dbpre" size="1" onchange="javascript:change(this)">
                      <logic:greaterThan value="1"  name="length">
                      <html:option value="ALL">全部人员库</html:option>
                      </logic:greaterThan>
                      <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              			
              </html:select>&nbsp;
              	 
              </span>
              <logic:equal value="1" name="makeupAnalyseForm" property="statlistsize">
	              <logic:notEqual value="0" name="makeupAnalyseForm" property="showstatname">
		              <bean:message key="makeupanalyse.stat"/>：<span style="font-weight: bold">${makeupAnalyseForm.statlist[0].dataName }</span>
	              </logic:notEqual>
              </logic:equal>
              <logic:notEqual value="1" name="makeupAnalyseForm" property="statlistsize">
              <bean:message key="makeupanalyse.stat"/>
	              <html:select name="makeupAnalyseForm" property="statid" size="1"  onchange="javascript:changestatid(this);">
	                           <html:optionsCollection property="statlist" value="dataValue" label="dataName"/>
	              </html:select>
              </logic:notEqual>
              &nbsp;  
              <logic:notEmpty name="makeupAnalyseForm" property="condlist">
              	<%if(size==0){ %>                 
                   <bean:message key="conlumn.mediainfo.info_title"/>
                   <html:select name="makeupAnalyseForm" property="lexprId" size="1"  onchange="javascript:query();">
                           <html:optionsCollection property="condlist" value="dataValue" label="dataName"/>
                  </html:select>&nbsp;
                <%} %>                       
              </logic:notEmpty> 
              <logic:equal name="makeupAnalyseForm" property="isonetwostat" value="1">
	              <logic:notEqual name="makeupAnalyseForm" property="showcharttype" value="0">
	              	  <%
	              	  ArrayList chartTypeList = makeupAnalyseForm.getChartTypeList();
	              	  %>
	              	  <%if(chartTypeList.size()>3){ %>
	                  <html:select name="makeupAnalyseForm" property="char_type" size="1"  onchange="javascript:changechar(this);">
	                           <html:optionsCollection property="chartTypeList" value="dataValue" label="dataName"/>
	                  </html:select>&nbsp; 
	                  <%}else{
	                	  if(chartTypeList.size()>1){
	                  	  for(int i = 0;i < chartTypeList.size();i++){
	                  	  	 CommonData cda = (CommonData)chartTypeList.get(i);
	                  	  	 String char_type = cda.getDataValue();
	                  	  	 if("12".equals(char_type)){
	                  %> 
				    <!--   &nbsp;&nbsp;<a href="###" onclick="choosechar('12')">立体直方图</a> -->
	                  <%}else if("11".equals(char_type)){ %>
				      &nbsp;&nbsp;<a href="###" onclick="choosechar('11')">平面直方图</a>
	                  <%}else if("5".equals(char_type)){ %>
				      <!-- &nbsp;&nbsp;<a href="###" onclick="choosechar('5')">立体圆饼图</a> -->
	                  <%}else if("20".equals(char_type)){ %>
				      &nbsp;&nbsp;<a href="###" onclick="choosechar('20')">平面圆饼图</a>
	                  <%}else if("1000".equals(char_type)){ %>
				      &nbsp;&nbsp;<a href="###" onclick="choosechar('1000')">平面折线图</a>
	                  <%}else if("40".equals(char_type)){ %>
				      &nbsp;&nbsp;<a href="###" onclick="choosechar('40')">柱状占比图</a>
	                  <%
	                  	  	  }
	                  	  }
	                	  }
	                  } 
	                  %>
	              </logic:notEqual>
              </logic:equal>
             <!--<html:checkbox name="makeupAnalyseForm" property="result" value="1" onclick="resultclick()"/>&nbsp; 
             <bean:message key="makeupanalyse.result"/>--> 
   	  </td>      
    </tr>
    <%if(size>0){ %>
    <tr>
    	<td>
    		<a href="###" onclick="guidance('/general/deci/statics/employmakeupanalyse.do?b_search=link&subIndex=start&flag=zqct');"><%=statNameList.get(0).toString() %>(<%=statOptionList.get(0).toString() %>)</a>
    	<%for(int i=0;i<size;i++){ %>
    	<%if(i==size-1){ %>
    		&nbsp;&nbsp;>&nbsp;&nbsp;<span>${makeupAnalyseForm.statName }</span>
    	<%}else{ %>
    		&nbsp;&nbsp;>&nbsp;&nbsp;<a href="###" onclick="guidance('/general/deci/statics/employmakeupanalyse.do?b_search=link&subIndex=<%=subIndexList.get(i).toString()%>&flag=zqct');"><%=statNameList.get(i+1).toString() %>(<%=statOptionList.get(i+1).toString() %>)</a>
    	<%} %>
    	<%} %>
    	</td>
    </tr>
    <%} %>
    <tr>
    <td>
     <logic:equal name="makeupAnalyseForm" property="isonetwostat" value="1">
      <table style="margin-left: -5px" align="left">
          <tr>
            <td align="left" nowrap colspan="5">
            <logic:equal value="1000" name="makeupAnalyseForm" property="char_type">
            <hrms:chart name="makeupAnalyseForm" title="${makeupAnalyseForm.snamedisplay}" isneedsum="${makeupAnalyseForm.isneedsum }" xangle="${makeupAnalyseForm.xangle}" scope="session" legends="jfreemap" data="" width="1200" height="530" chart_type="${makeupAnalyseForm.char_type}" pointClick="testchart">
	 		</hrms:chart>
            </logic:equal>
            <logic:equal value="5" name="makeupAnalyseForm" property="char_type">
	            <hrms:chart name="makeupAnalyseForm" title="${makeupAnalyseForm.snamedisplay}" isneedsum="${makeupAnalyseForm.isneedsum }" biDesk="false" scope="session" legends="datalist" data="" width="1200" height="530" chart_type="${makeupAnalyseForm.char_type}" pointClick="testchart">
		 		</hrms:chart>
            </logic:equal>
            <logic:equal value="20" name="makeupAnalyseForm" property="char_type">
	            <hrms:chart name="makeupAnalyseForm" title="${makeupAnalyseForm.snamedisplay}" isneedsum="${makeupAnalyseForm.isneedsum }" biDesk="false" scope="session" legends="datalist" data="" width="1200" height="530" chart_type="${makeupAnalyseForm.char_type}" pointClick="testchart">
		 		</hrms:chart>
            </logic:equal>
            <logic:notEqual value="1000" name="makeupAnalyseForm" property="char_type">
            	<logic:notEqual value="5" name="makeupAnalyseForm" property="char_type">
            		<logic:notEqual value="20" name="makeupAnalyseForm" property="char_type">
			       	 	<hrms:chart name="makeupAnalyseForm" title="${makeupAnalyseForm.snamedisplay}" isneedsum="${makeupAnalyseForm.isneedsum }" xangle="${makeupAnalyseForm.xangle}" scope="session" legends="datalist" data="" width="1200" height="530" chart_type="${makeupAnalyseForm.char_type}" pointClick="testchart">
				 		</hrms:chart>
		 			</logic:notEqual>
		 		</logic:notEqual>
		 	</logic:notEqual>
            </td>
          </tr>          
      </table>
     </logic:equal>
     <logic:equal name="makeupAnalyseForm" property="isonetwostat" value="2">
        <table width="70%" border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable">
   	     <tr>
          <td align="center" colspan="<%=(makeupAnalyseForm.getVarraylist().size()+1) %>" nowrap>
            	<%
            		//add by xiegh =0不显示总数 ，非0则显示
          			String showtitle = makeupAnalyseForm.getShowtitle(); 
          		  	if(!"0".equals(showtitle)){
          		%>
       	       <bean:write name="makeupAnalyseForm" property="snamedisplay" />&nbsp;(<bean:message key="workbench.stat.stattotalvalue"/><bean:write name="makeupAnalyseForm" property="totalvalue" />)
       	       <%
          			}
       	       %>
           </td>                	    	    	    		        	        	        
         </tr> 
             <tr>
                 <td align="center" class="TableRow" nowrap>
                 </td> 
                 <logic:iterate id="element" name="makeupAnalyseForm" property="varraylist">
                     <td align="center" class="TableRow" nowrap>
                         <bean:write name="element" property="legend"/>
                     </td>   
                 </logic:iterate>                      	    	    	    		        	        	        
              </tr>
   	     <logic:iterate id="element" name="makeupAnalyseForm" property="harraylist" indexId="indexh">
   	      <tr>
                <td align="center" class="TableRow" nowrap>
                    <bean:write name="element" property="legend"/> 
                 </td> 
                 <logic:iterate id="helement" name="makeupAnalyseForm" property="varraylist" indexId="indexv">
                   <td align="center" class="RecordRow" nowrap>
                      <!--   <a href="###" onclick="openpage('/general/deci/statics/employmakeupanalyse.do?b_double=link&a_code=${makeupAnalyseForm.a_code}&v=${indexv}&h=${indexh}&flag=jgfx&userbase=${makeupAnalyseForm.dbpre}&querycond=${makeupAnalyseForm.a_code}&home=0');">${makeupAnalyseForm.statdoublevalues[indexv][indexh]}</a>-->
                      <!-- 
                      zgd 2014-8-13 传输的参数中将人员范围（分类）组成的条件commlexr、commfacor传到后台
                      <a href="###" onclick="openpage('/general/static/commonstatic/statshow.do?b_double=link&statid=${makeupAnalyseForm.statid }&flag=jgfx&v=${indexv}&h=${indexh}&infokind=1&userbase=Usr&querycond=${makeupAnalyseForm.a_code}');">${makeupAnalyseForm.statdoublevalues[indexv][indexh]}</a> 
                      -->
                      <!-- liuy 2014-12-12 5888：员工管理/统计分析，模块中，去调0链接 start -->
                      <bean:define id="cellvalue" value="${makeupAnalyseForm.statdoublevalues[indexv][indexh]}"/>
                      <logic:equal name="cellvalue" value="0">
                              0
                      </logic:equal>
                      <logic:notEqual name="cellvalue" value="0">
                      	<a href="###" onclick="openpage('/general/static/commonstatic/statshow.do?b_double=link&statid=${makeupAnalyseForm.statid }&flag=jgfx&v=${indexv}&h=${indexh}&infokind=1&userbase=Usr&querycond=${makeupAnalyseForm.a_code}&commlexr=${makeupAnalyseForm.commlexr}&commfacor=${makeupAnalyseForm.commfacor}');">${makeupAnalyseForm.statdoublevalues[indexv][indexh]}</a>
                      </logic:notEqual>
                      <!-- liuy 2014-12-12 end -->
                   </td> 
                 </logic:iterate>          	    	    	    		        	        	        
              </tr> 
           </logic:iterate>  
           <tr>
           	<td height="30"><input type="button" class="mybutton" value="导出Excel" onclick="exportExcel();"/></td>
           </tr>	         
          </table>
         </logic:equal>
       
       </td>
      </tr>
   </table>
   <logic:equal name="makeupAnalyseForm" property="returnvalue" value="leaderdxt">
     <div style="position:relative; width:50px; margin-top:550px!important; margin-top:;left:35%;margin-left:-0px; ">
      <html:button styleClass="mybutton" property="bc_btn1" onclick="hrbreturn('leader','il_body','makeupAnalyseForm');">
         <bean:message key="button.return"/>
      </html:button>
     </div>
   </logic:equal>
    
</html:form>
<script type="text/javascript">
    var lexprId = document.getElementById("lexprId");
    var statid = document.getElementById("statid");
    var char_type = document.getElementById("char_type");
    var myspan = document.getElementById("myspan");
    var tdselectcondition = document.getElementById("tdselectcondition");
    var span = tdselectcondition.getElementsByTagName("span");
    var suma = tdselectcondition.getElementsByTagName("a");
    if(!lexprId && !statid && !char_type && myspan.style.display == "none" && span.length<2 && suma.length==0){
        document.getElementById("trselectcondition").style.display="none";
    }
</script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<div id='wait' style='position:absolute;top:200;left:35%;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style common_background_color" height=24>
					正在加载数据，请稍候...
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
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
