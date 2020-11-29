<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.hjsj.utils.Sql_switcher,
				 com.hrms.struts.constant.WebConstant,
				 com.hjsj.hrms.actionform.performance.commend.insupportcommend.InSupportCommendForm"%>
<%

	String userName = null;
	UserView usView = (UserView)session.getAttribute(WebConstant.userView);
	if(usView != null){
		userName=usView.getUserName();
	}	
 %>
<script language="javascript" src="/js/page_options.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">
function add(){
inSupportCommendForm.action="/performance/commend/insupportcommend/addInSupportCommend.do?b_init=init&oper=1";
inSupportCommendForm.submit();
}
function del(){
if(confirm("确认操作，仅可以删除起草和结束状态下的记录！")){
 var hashVo=new ParameterSet();
var tablename="table${inSupportCommendForm.tabname}";
        table=$(tablename);
        dataset=table.getDataset();
	    var record=dataset.getFirstRecord();
	    var selectID="";	
	    var isUsed=0;	
	    var noNum=0;
	    
		while (record) 
		{
			
			if (record.getValue("select"))
			{							
						if(record.getValue("p0209")=='01'||record.getValue("p0209")=='06')
						     selectID+=","+record.getValue("p0201");	
				        	    
			}
			record=record.getNextRecord();
		}  
	    if(selectID.length<0 ||selectID.length==0){
	    alert("请选择要删除的记录");
	    return;
	    }
       if(selectID.length>0)
       {
       
        hashVo.setValue("selectID",selectID);
        var In_parameters="flag=1";
        var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:refresh,functionId:'9010030006'},hashVo);			
       		 
       }
   }else{
 return;

}
}


function refresh(outparamters){
inSupportCommendForm.action="/performance/commend/insupportcommend/initInSupportCommend.do?b_init=init";
inSupportCommendForm.submit();
 //document.location.reload();
 }
 
 

 function execute_ok(outparamters){
 var ishave=outparamters.getValue("have");
 var name=outparamters.getValue("name");
 if(ishave=='no'){
 alert(name+"没有选择候选人!不能执行该操作");
 }else{
 inSupportCommendForm.action="/performance/commend/insupportcommend/initInSupportCommend.do?b_init=init";
inSupportCommendForm.submit();
 }
 }
 function parameterSet(){
 if(confirm("确认操作\r\n起草和暂停的计划才可以执行该操作!")){


var tablename="table${inSupportCommendForm.tabname}";
        table=$(tablename);
        dataset=table.getDataset();
	    var record=dataset.getFirstRecord();
	    var selectID="";	
	    var isUsed=0;	
	    var noNum=0;
	    
		while (record) 
		{
			
			if (record.getValue("select"))
			{	if(record.getValue("p0209")=="01"||record.getValue("p0209")=="09")						
			  selectID+=","+record.getValue("p0201");	
		    }
			record=record.getNextRecord();
		}  	
	    if(selectID.length<0 ||selectID.length==0){
	    alert("请选择记录");
	    return;
	    }
       if(selectID.length>0)
       {
       var theurl="/performance/commend/insupportcommend/ctrlParamSet.do?b_init=init&oper=1&selectIds="+selectID;
        var returnValue=window.showModalDialog(theurl,null, 
		        "dialogWidth:540px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");			
       		 
       }
  
}else{
return;
}
}
function select_ok(outparamters)
{
alert("操作执行成功");
return;
}
 function tablep02_extr_onRefresh(cell, value, record)
 {
    //table+数据集＋“要加连接的字段”
    if(record!=null&&(record.getValue("p0209")=="01"||record.getValue("p0209")=="09"))
	   cell.innerHTML = "<a href=\"javascript:extr('"+record.getString("p0201")+"');\" >&nbsp;(推荐参数)&nbsp;</a >";
	else
	   cell.innerHTML = "<a href=\"javascript:extr('"+record.getString("p0201")+"');\" style='color:#999999'>&nbsp;(推荐参数)&nbsp;</a >";   
 }
 function tablep02_b_onRefresh(cell, value, record)
 {
    //table+数据集＋“要加连接的字段”
    if(record!=null){
	   cell.innerHTML = "<a href='/performance/commend/insupportcommend/candidateVindicate.do?b_init=init&p0201=" + record.getString("p0201") + "' target='_self'>" + "提名"+ "</a >";
	   cell.align="center";
    }    
 }
 function subm(){
 inSupportCommendForm.action="/performance/commend/insupportcommend/initInSupportCommend.do?b_query=query";
 inSupportCommendForm.submit();
 }
 function showDateSelectBox(srcobj)
   {   
     Element.show('date_panel');   
      date_desc=srcobj;  
     var expr_editor=$('date_box');
       expr_editor.focus();
    
      for(var i=0;i<document.inSupportCommendForm.date_box.options.length;i++)
  	  {
  	  	document.inSupportCommendForm.date_box.options[i].selected=false;
  	  }
      var pos=getAbsPosition(srcobj);
	  with($('date_panel'))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
			style.posTop=pos[1]-1+srcobj.offsetHeight;
			style.width=75;
      }                 
      
   }
   
   function changeState()
   {

       Element.hide('date_panel');   
       var hashVo=new ParameterSet();
       var tablename="table${inSupportCommendForm.tabname}";
        table=$(tablename);
        dataset=table.getDataset();
	    var record=dataset.getFirstRecord();
	    var state;
	    for(var i=0;i<inSupportCommendForm.date_box.options.length;i++){
	    if(inSupportCommendForm.date_box.options[i].selected){
	    state=inSupportCommendForm.date_box.options[i].value+"";
	    }
	    }
	     var selectID="";	
	    var isUsed=0;	
	    var noNum=0;
	    if(state=='05'){
	    while (record) 
		{
			
			if (record.getValue("select"))
			{							
						     selectID+=","+record.getValue("p0201");	
				        	    
			}
			record=record.getNextRecord();
		}  	
	    if(selectID.length<0 ||selectID.length==0){
	    alert("请选择要执行的记录");
	    return;
	    }
       if(selectID.length>0)
       {
        hashVo.setValue("selectIds",selectID);
        var In_parameters="flag=1";
        var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:execute_ok,functionId:'9010030013'},hashVo);			
       		 
       }
	    }
	    if(state=='06'){//【5800】
	     if(confirm("确认操作")){
	    while (record) 
		{
			
			if (record.getValue("select"))
			{							
						if(record.getValue("p0209")=='05'||record.getValue("p0209")=='09')
						     selectID+=","+record.getValue("p0201");	
				        	    
			}
			record=record.getNextRecord();
		}  	
	    if(selectID.length<0 ||selectID.length==0){
	    alert("请选择要结束的记录");
	    return;
	    }
       if(selectID.length>0)
       {
        hashVo.setValue("selectIds",selectID);
        var In_parameters="flag=1";
        var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:refresh,functionId:'9010030009'},hashVo);			
       		 
       }
	    }
	    }
	    if(state=='09'){
	     if(confirm("确认操作，仅能把执行状态下的记录设置为暂停")){
	    while (record) 
		{
			
			if (record.getValue("select"))
			{							
						if(record.getValue("p0209")=='05')
						     selectID+=","+record.getValue("p0201");	
				        	    
			}
			record=record.getNextRecord();
		}  	
	    if(selectID.length<0 ||selectID.length==0){
	    alert("请选择要暂停的记录");
	    return;
	    }
       if(selectID.length>0)
       {
       
        hashVo.setValue("selectIds",selectID);
        var In_parameters="flag=1";
        var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:refresh,functionId:'9010030008'},hashVo);			
       		 
       }
	    }
	    }
}
function extr(p0201){
	var theurl="/performance/commend/insupportcommend/initInSupportCommend.do?b_pres=link&p0201="+p0201;
	var returnValue=window.showModalDialog(theurl,null, 
		        "dialogWidth:540px; dialogHeight:520px;resizable:no;center:yes;scroll:no;status:no");	
	if(returnValue!=null){
		var hashvo=new ParameterSet();
		hashvo.setValue("p0201",p0201);
		hashvo.setValue("dbpres",returnValue.dbpres);
		hashvo.setValue("bodys",returnValue.bodys);
	    hashvo.setValue("codes",returnValue.codes);
	    hashvo.setValue("counts",returnValue.counts);
	    hashvo.setValue("footer",returnValue.footer);
		var request=new Request({method:'post',asynchronous:false,onSuccess:null,functionId:'9010030032'},hashvo);			
	}
}
function scanner(){
	var table,dataset,record;	
    table=$("tablep02");
    dataset=table.getDataset();	
    record=dataset.getCurrent();
    if(!record)
	    alert("请选择！");
	var pid=record.getValue("p0201");
	if(!pid)
	  	return;
	if(record.getValue("p0209")!="05"){
		alert("只能对执行中的推荐项进行扫描！");
		return;
	}
	var theurl="/performance/commend/insupportcommend/initInSupportCommend.do?br_scan=link&p0201="+pid;
	var returnValue=window.showModalDialog(theurl,null, 
		        "dialogWidth:500px; dialogHeight:370px;resizable:no;center:yes;scroll:no;status:no");
}
  // 输出民主推荐票
  // excel模板放到c:\hrp2000\data目录下，文件名:后备推荐表.xlt
  function genExcel_Rm()
  {   
  	  var UserName="<%=userName%>"; 
  	  var table,dataset,record;	
      table=$("tablep02");
      dataset=table.getDataset();	
      record=dataset.getCurrent();
      if(!record)
		  alert("请选择！");
	  var pid=record.getValue("p0201");
	  if(!pid)
	  	  return;
      var aurl='<%="http://"+request.getServerName()+":"+request.getServerPort()%>';
      var DBType="<%=Sql_switcher.searchDbServer() %>";<%-- 数据库类型不能写死 lium --%>
      if(!AxManager.setup("axocr", "ocr1", 0, 0, null, AxManager.omrreaderPkgName, null, false, aurl))
  		return;
      var obj = document.getElementById('ocr1'); 
      obj.SetURL(aurl);      
      obj.SetDBType(DBType); 
      obj.SetPlanType(1);    
      obj.SetPlanId(pid);  
      obj.SetUserName(UserName); 
      
      var param = "";  // 参数为空
      obj.GenExcelAndMod(param);
  }
</script>
<html:form action="/performance/commend/insupportcommend/initInSupportCommend">

<table id="test" width="100%" border="0" ><tr><td>	
状态:
<html:radio name='inSupportCommendForm' property='state' value='00' onclick='subm();'> 全部</html:radio>
<html:radio name='inSupportCommendForm' property='state' value='01' onclick='subm();'> 起草</html:radio>
<html:radio name='inSupportCommendForm' property='state' value='09' onclick='subm();'> 暂停</html:radio>
<html:radio name='inSupportCommendForm' property='state' value='05' onclick='subm();'> 执行中</html:radio>
<html:radio name='inSupportCommendForm' property='state' value='06' onclick='subm();'> 结束</html:radio>

<hrms:dataset name="inSupportCommendForm" property="commendList" scope="session" setname="${inSupportCommendForm.tabname}"  setalias="p02_set"  rowlock="true" rowlockfield="p0209"  rowlockvalues=",01,09," select="true" sql="${inSupportCommendForm.sql}" buttons="movefirst,prevpage,moveprev,movenext,nextpage,movelast">

	     <hrms:commandbutton name="add" refresh="true" onclick="add();">新建</hrms:commandbutton>
	      <hrms:commandbutton name="delete" refresh="true" onclick="del();">删除</hrms:commandbutton>                                                                                                                                                                                                                             
	     
	     <hrms:commandbutton name="saveall"  functionId="9010030007" refresh="true" type="all-change" setname="${inSupportCommendForm.tabname}">
	     	保存
	     </hrms:commandbutton>
	     <hrms:commandbutton name="setstate" onclick="showDateSelectBox(this);" functionId="" refresh="true" type="" setname="${inSupportCommendForm.tabname}">
	     状态设置
	     </hrms:commandbutton>
	   <hrms:commandbutton name="outexcel" onclick="genExcel_Rm();" functionId="" refresh="true" type="" setname="${inSupportCommendForm.tabname}">
	     导出Excel
	     </hrms:commandbutton>
	     <hrms:commandbutton name="scan" onclick="scanner();" functionId="" function_id="0D4101" refresh="true" type="" setname="${inSupportCommendForm.tabname}">
	     扫描
	     </hrms:commandbutton>
	</hrms:dataset>
</td>
</tr>
</table>
<div id="date_panel" >
   			<select onblur="Element.hide('date_panel');" id="date_box" name="date_box" multiple="multiple" size="3"  style="width:75"  onchange="changeState();" onblur="Element.hide('date_panel');" >    
			    
			    
			    <option value="05">发布</option>
			    <option value="09">暂停</option>				    
			    <option value="06">结束</option>
			    	    			    		    
              </select>
         </div>
<div id="axocr" style="display: none;">
</div>
 <script language="javascript">
   Element.hide('date_panel');
</script>
</html:form>
