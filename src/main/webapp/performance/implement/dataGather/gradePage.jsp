<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*, 
				 com.hjsj.hrms.actionform.performance.implement.DataGatherForm, 
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 com.hrms.hjsj.utils.Sql_switcher,
				 com.hrms.struts.taglib.CommonData,com.hjsj.hrms.utils.PubFunc,
				 com.hrms.hjsj.sys.Des" %>

<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="../../../../module/utils/js/template.js"></script>

<%			
	DataGatherForm dataGatherForm=(DataGatherForm)session.getAttribute("dataGatherForm");	
	Hashtable planParaSet = (Hashtable)dataGatherForm.getPlanParamSet();
	String readerType = (String)planParaSet.get("ReaderType");//机读类型:0光标阅读机(默认),1扫描仪	
 	String gather_type=dataGatherForm.getGather_type();//0 网上 1 机读 2：网上+机读
 	ArrayList planbodys = dataGatherForm.getPlanBodys();
 	String PlanId = dataGatherForm.getPlanId();
 	PlanId = PubFunc.encryption(PlanId);
 	String temp_objectid = PubFunc.decrypt(dataGatherForm.getObject_id());
 	String mainbody_id = dataGatherForm.getMainbody_id();
 	mainbody_id = PubFunc.decrypt(mainbody_id);
%>

<script language="JavaScript">

  var perPointNoGrade="${dataGatherForm.perPointNoGrade}"  //目标卡中引入的绩效指标是否设置了标度  0：  1：没有设置标度
  var isEntireysub="${dataGatherForm.isEntireysub}"        //提交是否必填	
  var scoreflag="${dataGatherForm.scoreflag}";    //=2混合，=1标度(默认值=混合)
  var isScoreMainbody='${dataGatherForm.isScoreMainbody}';  //是否为必打分考核主体
  var gradeObject_id='${dataGatherForm.object_id}';  //考核对象ID

 var tempVal='-1';
 function saveBeforeVali(theVal)
 {	
 	if(ltrim(rtrim(theVal))!='')
 		tempVal=theVal;
 }
	//1:保存 或 2:提交
  function subScore(flag)
  {
  	if(perPointNoGrade=='1')
  	{
  		alert(P_I_INFO5+"!");
  		return;
  	}
  	
  	var values=new Array();
  	var isNull=0;
    for(var i=0;i<document.dataGatherForm.elements.length;i++)
  	{
  		var obj_name=document.dataGatherForm.elements[i].name;
  		//alert(document.dataGatherForm.elements[i].type);
  		
  		if(obj_name.length>2&&obj_name.substring(0,2)=='p_')
  		{
  			if(document.dataGatherForm.elements[i].value==''||trim(document.dataGatherForm.elements[i].value).length==0)
  			{
  				if(flag==2&&isEntireysub=='true'&&!document.dataGatherForm.elements[i].disabled)
  				{
  					alert(P_I_INFO6+"!");
  					return;
  				}
  				else
  					values[values.length]=obj_name+":null";
  			}
  			else
  			{
  					isNull++;
  					values[values.length]=obj_name+":"+document.dataGatherForm.elements[i].value;
  			}
  		}
  	}
  	if(isNull==0&&isScoreMainbody=='1')
  	{
  		alert(P_I_INF13+"!");
  		return;
  	}
  	var hashvo=new ParameterSet();
	hashvo.setValue("object_id","${dataGatherForm.object_id}");
	hashvo.setValue("planid","${dataGatherForm.planId}");
	hashvo.setValue("body_id","${dataGatherForm.mainbody_id}");
	
	if(document.dataGatherForm.konwDegree)
		hashvo.setValue("konwDegree",document.dataGatherForm.konwDegree.value);
	if(document.dataGatherForm.wholeEval)
		hashvo.setValue("wholeEval",document.dataGatherForm.wholeEval.value);
	
	hashvo.setValue("valueList",values);
	hashvo.setValue("flag",flag);
	var request=new Request({method:'post',asynchronous:false,onSuccess:returnSubOk,functionId:'9023000010'},hashvo);
  }
	
 function returnSubOk(outparamters)
 {
	var info=getDecodeStr(outparamters.getValue("info"));
	var flag=outparamters.getValue("flag");  //1:保存 或 2:提交
	var score=outparamters.getValue("score");
	if(info.length>0)
	{
		alert(info);
	}	
	else
	{
		if(flag=='1')
		{
			alert(SAVESUCCESS+"!");
		}
		else if(flag=='2')
		{
			alert(SUBSUCCESS+"!");
		}
		document.getElementById("totalScore").innerHTML=score;
		parent.l_menu2.editMainBodyRecord(flag)
	}
 }
 
  function saveMathineScores(theObj)
  {
   	if(ltrim(rtrim(theObj.value))=='' && tempVal!='-1')  
   	{
   		theObj.value=tempVal;
   		alert('标度代码无效!');
   		return;
   	}
  		
 	tempVal='-1';
  	
    var mainbody_status = 0;//0 未打分 2 都打分了 3 部分打分
  	if(perPointNoGrade=='1')
  	{
  		alert(P_I_INFO5+"!");
  		return;
  	}
  	
  	var values=new Array();
  	var isNull=0;
  	var pointCount=0;
    for(var i=0;i<document.dataGatherForm.elements.length;i++)
  	{
  		var obj_name=document.dataGatherForm.elements[i].name;
  		//alert(document.dataGatherForm.elements[i].type);
  		
  		if(obj_name.length>2&&obj_name.substring(0,2)=='p_')
  		{
  			pointCount++;
  			if(document.dataGatherForm.elements[i].value==''||trim(document.dataGatherForm.elements[i].value).length==0)
  			{
  					values[values.length]=obj_name+":null";
  			}
  			else
  			{
  					isNull++;
  					values[values.length]=obj_name+":"+document.dataGatherForm.elements[i].value;
  			}
  		}
  	}
  	if(isNull==0&&isScoreMainbody=='1')
  	{
  		alert(P_I_INF13+"!");
  		return;
  	}
  	if(isNull==0)
  		mainbody_status=0;
  	else  if(isNull>0 && isNull<pointCount)
  		mainbody_status=3;
  	else  if(isNull>0 && isNull==pointCount)
  		mainbody_status=2; 		
  		
  	var hashvo=new ParameterSet();
	hashvo.setValue("object_id","${dataGatherForm.object_id}");
	hashvo.setValue("planid","${dataGatherForm.planId}");
	hashvo.setValue("body_id","${dataGatherForm.mainbody_id}");
	
	if(document.dataGatherForm.konwDegree)
		hashvo.setValue("konwDegree",document.dataGatherForm.konwDegree.value);
	if(document.dataGatherForm.wholeEval)
		hashvo.setValue("wholeEval",document.dataGatherForm.wholeEval.value);
	
	hashvo.setValue("valueList",values);
	hashvo.setValue("flag",1);
	hashvo.setValue("mainbody_status",mainbody_status);
	var request=new Request({method:'post',asynchronous:false,onSuccess:returnSubOk2,functionId:'9023000010'},hashvo);
  }
	
 function returnSubOk2(outparamters)
 {	
	var info=getDecodeStr(outparamters.getValue("info"));
	var mainbody_status=outparamters.getValue("mainbody_status"); //0 未打分 2 都打分了 3 部分打分
	var score=outparamters.getValue("score");
	if(info.length>0)
	{
		alert(info);
	}	
	else
	{		
		document.getElementById("totalScore").innerHTML=score;
		parent.l_menu2.editMainBodyRecord2(mainbody_status);
	}
 }
 
 //校验输入框里的值是否为数字类型
 function validateValue(obj,isNull)
 {
 	if(isNull!=null&&isNull=='null'&&obj.value.length==0)
 		return;
 
    if(scoreflag=='1')
    {
	 	if(!checkIsNum(obj.value))
		{
				alert(FORMATERROR);
				obj.value=0;
				obj.focus();
				return;
		}
    }
 }
 
  function showDateSelectBox(srcobj)
   {
      
      var pos=getAbsPosition(srcobj);

      var hashvo=new ParameterSet();
	  hashvo.setValue("pos0",pos[0]);
	  hashvo.setValue("pos1",pos[1]);
      hashvo.setValue("srcobj_width",srcobj.offsetWidth);
      hashvo.setValue("srcobj_height",srcobj.offsetHeight);
      
      var In_paramters="point_id="+srcobj.id; 		
	  var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'90100160009'},hashvo);
   }
 
 	function returnInfo2(outparamters)
	{
	
		  Element.show('date_panel');   
	      var pos0=outparamters.getValue("pos0")*1;
	      var pos1=outparamters.getValue("pos1")*1;
		  var srcobj_width=outparamters.getValue("srcobj_width")*1;
		  var srcobj_height=outparamters.getValue("srcobj_height")*1;
          var op=eval('date_panel');
	
	
		var fieldlist=outparamters.getValue("dataList");	
		var dataHtml=getDecodeStr(outparamters.getValue("dataHtml"));	
		dataHtml=replaceAll(dataHtml,"#@#","<br>");		
		date_panel.innerHTML=dataHtml;
		date_panel.innerHTML=date_panel.innerHTML+"<iframe src=\"javascript:false\" style=\"position:absolute; visibility:inherit; top:0px; left:0px;"
   			 				+"width:"+op.offsetWidth+"; height:"+op.offsetHeight+"; " 					    	
   			 				+"z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';\"></iframe>";   				
	
		 
	    
		  with($('date_panel'))
		  {
		        style.position="absolute";
		        style.posLeft=15;	
			    if(window.document.body.offsetHeight<(window.event.y+op.offsetHeight))
			    {
			    	style.posTop=window.event.y-20+document.body.scrollTop-op.offsetHeight;			    	
			    }
			    else
			    {
			    	style.posTop=window.event.y+20+document.body.scrollTop;
			    }
			 
	      }  
		
		
		
	}
   //改名因为在谷歌浏览器下方法命名不规范
    function hidden1()
    {
   		Element.hide('date_panel');
    }

  function replaceAll( str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	   
	    return str;
	}
	
	//清空
	function clearScore(plan_id,mainbodyID,objectID)
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("object_id",objectID);
		hashvo.setValue("planid",plan_id);
		hashvo.setValue("body_id",mainbodyID);
		var request=new Request({method:'post',asynchronous:false,onSuccess:returnClearOk,functionId:'9023000011'},hashvo);
	}
	
	
	function returnClearOk(outparamters)
	{
		var planid=outparamters.getValue("planid");
		var objectID=outparamters.getValue("objectID");
		var mainbodyID=outparamters.getValue("mainbodyID");
		parent.l_menu2.editMainBodyRecord("-1");
		document.location="/performance/implement/dataGather.do?b_grade=show&planId="+planid+"&object_id="+objectID+"&mainbody_id="+mainbodyID;
	}
	
	//返回
	function goback()
	{
	  document.dataGatherForm.action="/performance/implement/performanceImplement.do?b_int=link&plan_id=<%=PlanId%>";
	  document.dataGatherForm.target="il_body";
	  document.dataGatherForm.submit();
	
	}
	
		
	//导出excel
	function exportExcel(planid,objectID,mainbodyID)
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("object_id",objectID);
		hashvo.setValue("planid",planid);
		hashvo.setValue("mainbody_id",mainbodyID);
		var request=new Request({method:'post',asynchronous:false,onSuccess:returnExportOk,functionId:'9023000012'},hashvo);
	}
	
	
	function returnExportOk(outparamters)
	{

		//zhangh 2020-4-7 下载改为使用VFS
		var outName=outparamters.getValue("filename");
		outName = decode(outName);
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
	}
	//显示图像
	function showPic()
	{	
		var picWidth = window.screen.availWidth;
		var picHeight = window.screen.availHeight;	
		var weighturl="/performance/implement/dataGather.do?b_showPic=link`picWidth="+picWidth+"`picHeight="+picHeight;
 		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(weighturl);
 		// var resultVo= window.showModalDialog(iframe_url, 'pointpowerset_win',"dialogWidth:800px; dialogHeight:600px;resizable:yes;center:yes;scroll:yes;status:no;minimize:yes;maximize:yes;");
		return_vo ='';
		var theUrl = iframe_url;
		Ext.create('Ext.window.Window', {
			id:'showPic',
			height: 550,
			width: 630,
			resizable:false,
			modal:true,
			autoScroll:false,
			autoShow:true,
			html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
			renderTo:Ext.getBody()
		});
	}
	//统计票数	
	function show()
	{
 		var weighturl="/performance/implement/dataGather.do?b_stat=link";
 		var iframe_url="/general/query/common/iframe_query.jsp?src="+weighturl;
 		var resultVo= window.showModalDialog(iframe_url, 'pointpowerset_win',"dialogWidth:450px; dialogHeight:340px;resizable:no;center:yes;scroll:no;status:no");
      		if(resultVo!=null){
      			reflesh();
      		}
      	//window.open(iframe_url); 
	}
	
	
	function selectRead()
	{
		
		if(document.dataGatherForm.type_box.value=='single')
		{
			showOmr();
		}
		else
		{
			selectObjects()
		}
	}
	
	//机读多个考核对象
	function selectObjects()
	{
		var weighturl="/performance/implement/dataGather.do?br_selectObject=link";
 		var iframe_url="/general/query/common/iframe_query.jsp?src="+weighturl;
 		var resultVo= window.showModalDialog(iframe_url, 'pointpowerset_win',"dialogWidth:600px; dialogHeight:340px;resizable:no;center:yes;scroll:no;status:no");
        
        parent.l_menu2.location="/performance/implement/dataGather.do?b_mainbody=query&objectId=${dataGatherForm.object_id}";
    //  document.location=document.location;		
	}
	
	//机读单个考核对象
	function showOmr()
	{
		var weighturl="/performance/implement/dataGather/readCard.jsp?readerType=<%=readerType%>";
 		var iframe_url="/general/query/common/iframe_query.jsp?src="+weighturl;
 		var resultVo= window.showModalDialog(iframe_url, 'pointpowerset_win',"dialogWidth:478px; dialogHeight:300px;resizable:yes;center:yes;scroll:yes;status:no");

        parent.l_menu2.location="/performance/implement/dataGather.do?b_mainbody=query&objectId=${dataGatherForm.object_id}";
	//	document.location=document.location;
	}
		
	function return_bt()
	{
		 dataGatherForm.action="/performance/kh_plan/performPlanList.do?b_query=return&currentPlanid=${dataGatherForm.planId}";
		 dataGatherForm.target="il_body";
		 dataGatherForm.submit(); 
	}

   function insertMainbody(theBodyId)
   {
   		if("<%=temp_objectid%>"=="root")
   			return;   
  		var hashvo=new ParameterSet();
		hashvo.setValue("object_id","${dataGatherForm.object_id}");
		hashvo.setValue("planid","${dataGatherForm.planId}");
		hashvo.setValue("mainbody_id","${dataGatherForm.mainbody_id}");
		hashvo.setValue("body_id",theBodyId);
		hashvo.setValue("opt",'27');
		var request=new Request({method:'post',asynchronous:false,onSuccess:refreshPage,functionId:'9023000003'},hashvo);	
   }
   function del(theFlag)
   {
   		var hashvo=new ParameterSet();
		hashvo.setValue("object_id","${dataGatherForm.object_id}");
		hashvo.setValue("planid","${dataGatherForm.planId}");
		hashvo.setValue("mainbody_id","${dataGatherForm.mainbody_id}");
		hashvo.setValue("delFlag",theFlag);
		hashvo.setValue("opt",'26');
		var info='';
		if(theFlag==1)
			info=KH_IMPLEMENT_INF14;
		else
			info=KH_IMPLEMENT_INF15;
		if(confirm(info))
			var request=new Request({method:'post',asynchronous:false,onSuccess:refreshPage,functionId:'9023000003'},hashvo);	
   }
   function refreshPage(outparameters)
   {
    	parent.l_menu2.location="/performance/implement/dataGather.do?b_mainbody=query&objectId=${dataGatherForm.object_id}";
   }
</script>

<style>
.button{
    color:#414141 !important;
}
.ListTable_self {
    BACKGROUND-COLOR: #FFFFFF;
    BORDER-BOTTOM: medium none; 
    BORDER-COLLAPSE: collapse; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none; 
    
 }   

.RecordRow_self {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;

}

.trDeep_self {  
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
	background-color: #DDEAFE; 
	}


</style>
<hrms:themes />
<html>
<head>
<title>Insert title here</title>
</head>
<body>

<html:form action="/performance/implement/dataGather">
<div id="date_panel2"  >
   			<select name="type_box" multiple="multiple" size="2"  style="width:110"  onchange="selectRead()" >    
			    
			    <option value="single"><bean:message key="performance.implement.inputSingleObject"/></option>
			    <option value="multiple"><bean:message key="performance.implement.inputMultipleObject"/></option>
              </select>
 </div>
 
<table>
<tr><td align='left' style="padding-top:3px;">

     <logic:notEqual name="dataGatherForm" property="busitype" value="1">
			<logic:equal name="dataGatherForm" property="gather_type"  value="0">
				<hrms:priv func_id="326030201">
					<input type="button" extra="button" onclick='clearScore("${dataGatherForm.planId}","${dataGatherForm.mainbody_id}","${dataGatherForm.object_id}")' value='<bean:message key="button.clearup"/>' />
				</hrms:priv>	
				<hrms:priv func_id="326030202">
					<input type="button" extra="button" onclick='subScore(1)' value='<bean:message key="button.save"/>' />
				</hrms:priv>		
				<hrms:priv func_id="326030203">
					<input type="button" extra="button" onclick='subScore(2)' value='<bean:message key="button.submit"/>' />
				</hrms:priv>		
				<hrms:priv func_id="326030204">
					<input type="button" extra="button" onclick='exportExcel("${dataGatherForm.planId}","${dataGatherForm.object_id}","${dataGatherForm.mainbody_id}")' value='<bean:message key="button.export"/>' />
				</hrms:priv>		
				<hrms:priv func_id="326030205">
					<input type="button" extra="button" onclick='show()' value='<bean:message key="lable.performance.votestat"/>'/>
				</hrms:priv>
			</logic:equal>
			
			<logic:notEqual name="dataGatherForm" property="gather_type" value="0">
			   	<logic:notEqual name="dataGatherForm" property="object_id" value="root">
					<hrms:priv func_id="326030206">
						<input type="button" extra="button" menu="p1" value='<bean:message key="button.insert"/>'/>
					</hrms:priv>
			  	</logic:notEqual>
				
					<hrms:menubar menu="p1" id="menubar1" container="" visible="false">
		
						<%for(int i=0;i<planbodys.size();i++){
							CommonData data = (CommonData)planbodys.get(i);
							String datavalue = data.getDataValue();
							String dataname = data.getDataName();
							String jsfunc = "insertMainbody(\""+datavalue+"\")";
						 %>
						      <hrms:menuitem name='<%="mitem"+i%>' label='<%=dataname%>' icon="" url='<%=jsfunc%>' command="" enabled="true" visible="true"></hrms:menuitem>
							<%}%>
					
	  				</hrms:menubar>
	  				<logic:notEqual name="dataGatherForm" property="mainbody_id" value="">
	  					<hrms:priv func_id="326030207">
							<input type="button" extra="button" menu="p3" value='<bean:message key="button.delete"/>'/>
						</hrms:priv>
					</logic:notEqual>
					<hrms:menubar menu="p3" id="menubar3" container="" visible="false">
						 <hrms:menuitem name='mitemp31' label='jx.dataGather.delMainBody' icon="" url='del(1);' command="" enabled="true" visible="true"></hrms:menuitem>
						 <hrms:menuitem name='mitemp32' label='jx.dataGather.delObjData' icon="" url='del(2);' command="" enabled="true" visible="true"></hrms:menuitem>
					</hrms:menubar>
				<%if(readerType.equals("0")){ %>
					<hrms:priv func_id="326030208">
						<input type="button" extra="button" menu="p2" value='<bean:message key="lable.readFromCard"/>'  />
					</hrms:priv>
					<hrms:menubar menu="p2" id="menubar2" container="" visible="false">
						 <hrms:menuitem name='mitemp21' label='performance.implement.inputSingleObject' icon="" url='showOmr();' command="" enabled="true" visible="true"></hrms:menuitem>
						 <hrms:menuitem name='mitemp22' label='performance.implement.inputMultipleObject' icon="" url='selectObjects();' command="" enabled="true" visible="true"></hrms:menuitem>
					</hrms:menubar>
				<%}else if(readerType.equals("1")){ %>	
					<hrms:priv func_id="326030208">				
						<input type="button" extra="button" onclick="showOmr()"  value='<bean:message key="lable.readFromCard"/>'/>
					</hrms:priv>
					<%if(mainbody_id != null && mainbody_id.length() >0){ %>
					<hrms:priv func_id="326030209">
						<input type="button" extra="button" onclick="showPic()" value='<bean:message key="jx.dataGather.picture"/>'/>
					</hrms:priv>	
				<%	}
				} %>				
							
			</logic:notEqual>
	</logic:notEqual>
	<logic:equal name="dataGatherForm" property="busitype"  value="1">	
		<logic:equal name="dataGatherForm" property="gather_type"  value="0">
				<hrms:priv func_id="36030401">
					<input type="button" extra="button" onclick='clearScore("${dataGatherForm.planId}","${dataGatherForm.mainbody_id}","${dataGatherForm.object_id}")' value='<bean:message key="button.clearup"/>' />
				</hrms:priv>	
				<hrms:priv func_id="36030402">
					<input type="button" extra="button" onclick='subScore(1)' value='<bean:message key="button.save"/>' />
				</hrms:priv>		
				<hrms:priv func_id="36030403">
					<input type="button" extra="button" onclick='subScore(2)' value='<bean:message key="button.submit"/>' />
				</hrms:priv>		
				<hrms:priv func_id="36030404">
					<input type="button" extra="button" onclick='exportExcel("${dataGatherForm.planId}","${dataGatherForm.object_id}","${dataGatherForm.mainbody_id}")' value='<bean:message key="button.export"/>' />
				</hrms:priv>		
				<hrms:priv func_id="36030405">
					<input type="button" extra="button" onclick='show()' value='<bean:message key="lable.performance.votestat"/>'/>
				</hrms:priv>
			</logic:equal>
			
			<logic:notEqual name="dataGatherForm" property="gather_type" value="0">
			   	<logic:notEqual name="dataGatherForm" property="object_id" value="root">
					<hrms:priv func_id="36030406">
						<input type="button" extra="button" menu="p1" value='<bean:message key="button.insert"/>'/>
					</hrms:priv>
			  	</logic:notEqual>
				
					<hrms:menubar menu="p1" id="menubar1" container="" visible="false">
		
						<%for(int i=0;i<planbodys.size();i++){
							CommonData data = (CommonData)planbodys.get(i);
							String datavalue = data.getDataValue();
							String dataname = data.getDataName();
							String jsfunc = "insertMainbody(\""+datavalue+"\")";
						 %>
						      <hrms:menuitem name='<%="mitem"+i%>' label='<%=dataname%>' icon="" url='<%=jsfunc%>' command="" enabled="true" visible="true"></hrms:menuitem>
							<%}%>
					
	  				</hrms:menubar>
	  				<logic:notEqual name="dataGatherForm" property="mainbody_id" value="">
	  					<hrms:priv func_id="36030407">
							<input type="button" extra="button" menu="p3" value='<bean:message key="button.delete"/>'/>
						</hrms:priv>
					</logic:notEqual>
					<hrms:menubar menu="p3" id="menubar3" container="" visible="false">
						 <hrms:menuitem name='mitemp31' label='jx.dataGather.delMainBody' icon="" url='del(1);' command="" enabled="true" visible="true"></hrms:menuitem>
						 <hrms:menuitem name='mitemp32' label='jx.dataGather.delObjData' icon="" url='del(2);' command="" enabled="true" visible="true"></hrms:menuitem>
					</hrms:menubar>
				<%if(readerType.equals("0")){ %>
					<hrms:priv func_id="36030408">
						<input type="button" extra="button" menu="p2" value='<bean:message key="lable.readFromCard"/>'  />
					</hrms:priv>
					<hrms:menubar menu="p2" id="menubar2" container="" visible="false">
						 <hrms:menuitem name='mitemp21' label='performance.implement.inputSingleObject' icon="" url='showOmr();' command="" enabled="true" visible="true"></hrms:menuitem>
						 <hrms:menuitem name='mitemp22' label='performance.implement.inputMultipleObject' icon="" url='selectObjects();' command="" enabled="true" visible="true"></hrms:menuitem>
					</hrms:menubar>
				<%}else if(readerType.equals("1")){ %>	
					<hrms:priv func_id="36030408">				
						<input type="button" extra="button" onclick="showOmr()"  value='<bean:message key="lable.readFromCard"/>'/>
					</hrms:priv>
					<%if(mainbody_id != null && mainbody_id.length() >0){ %>
					<hrms:priv func_id="36030409">
						<input type="button" extra="button" onclick="showPic()" value='<bean:message key="jx.dataGather.picture"/>'/>
					</hrms:priv>	
				<%    } 
				 }%>		
				</logic:notEqual>	
		</logic:equal>
		<logic:equal name="dataGatherForm" property="fromUrl"  value="0">
			<input type="button" extra="button" onclick="goback()" value='<bean:message key="kq.search_feast.back"/>'/>	
		</logic:equal>
		<logic:notEqual name="dataGatherForm" property="fromUrl" value="0" >
			<input type="button" extra="button" onclick="return_bt()" value='<bean:message key="button.return"/>'/>	
		</logic:notEqual>
		
		&nbsp;&nbsp; </td></tr>
		
<tr><td>

${dataGatherForm.gradeHtml} 

</td></tr></table>

<br>
<div id="date_panel">
   			
</div>
</html:form>


<script language='javascript' >
Element.hide('date_panel2');

function showSelectBox(srcobj)
{	      
	date_desc=srcobj;      
	Element.show('date_panel2');   
	var pos=getAbsPosition(srcobj);
	if(pos[1]>35)
		pos[1]=3;
	var obj=document.getElementById("date_panel2");
	obj.style.position="absolute";
	obj.style.posLeft=pos[0]-1;
	obj.style.posTop=pos[1]-1+srcobj.offsetHeight;	      	      
}
var aa=document.getElementsByTagName("input");
for(var i=0;i<aa.length;i++){
	if(aa[i].type=="text"){
		aa[i].className="inputtext";
	}
}
if(!getBrowseVersion()){
	var inputs = document.getElementsByTagName('input');
	for(var i = 0;i<inputs.length;i++){
		if(inputs[i].getAttribute('type') == 'button'){
			if(inputs[i].getAttribute('menu') == 'p1' ||inputs[i].getAttribute('menu') =='p3' ||inputs[i].defaultValue =='读卡'){
				inputs[i].setAttribute('disabled','disabled');
				inputs[i].style.background = "url('')";
			}
		}
	}
}


</script> 

</body>
 
</html>