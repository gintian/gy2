<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.general.muster.MusterForm,org.apache.commons.beanutils.LazyDynaBean" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.hjsj.utils.Sql_switcher"%>
<%@ page import="com.hrms.hjsj.sys.ConstantParamter"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    
    String userName = null;
    String css_url = "/css/css1.css";
    UserView userView = (UserView) session
            .getAttribute(WebConstant.userView);
    String fields = "";
    String tables = "";
    String a_code="UN";
    
    if (userView != null) {
        css_url = userView.getCssurl();
        if (css_url == null || css_url.equals(""))
            css_url = "/css/css1.css";
        userName=userView.getUserName();
    }
    String superUser = "0";
    if(userView!=null){
        if (userView.isSuper_admin()){
            superUser = "1";
        }else {
            a_code=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
            fields = userView.getFieldpriv().toString();
            tables = userView.getEmp_tablepriv().toString();
        }
    }
   String bosflag=userView.getBosflag();
   String toolbarTop = "-2px";
   if("hcm".equalsIgnoreCase(userView.getBosflag()))
   		toolbarTop = "8px";
%>
<script language="javascript" src="/js/page_options.js"></script>

<script language="javascript">
function setColumnTitle(outparamters){
   	var setname=outparamters.getValue("setname");
   	var tablename,table,col,title;
   	title=outparamters.getValue("title");
    tablename="table"+setname;
    table=$(tablename);
    col=table.getActiveCellIndex();
    table.resetColumnTitle(table,col,title);
	//for(var i=0;i<2;i++)
	//{
	//   alert(table.getColWidth(table,i));
	//}
    //table.tHead.rows[0].cells[table.getActiveCellIndex()].innerHTML="hello"   	
}
   
function renameTitle(setname){
   	var tablename,table,dataset,temp,field_name,field,title;
    var hashvo=new ParameterSet();
    tablename="table"+setname;
    table=$(tablename); 
    dataset=table.getDataset();
    temp=table.getActiveCell();
    field_name=temp.getField();
    if(field_name=='select'){
        alert(SELECT_UPDATE_TITLE_CELL+'!');
        return;
    }
   	field=dataset.getField(field_name);
   	title=field.getLabel();
   	title=prompt(INPUT_ROES_TITLE+"：",title);
   	if(title)
   	{
		hashvo.setValue("title",title);
		hashvo.setValue("setname",setname);
		hashvo.setValue("field_name",field_name);		
     	var request=new Request({asynchronous:false,onSuccess:setColumnTitle,functionId:'0521010003'},hashvo);   
    }     
}
function hideColumn(setname){
    var tablename,table,temp,field_name;
    tablename="table"+setname;
    table=$(tablename);
    temp=table.getActiveCell();
    field_name=temp.getField();
    var coumsize="${musterForm.coumsize}";
	musterForm.action="/general/muster/fillout_musterdata.do?b_hide=hide&field_name="+field_name+"&coumsize="+coumsize;
	musterForm.submit();        
}
function showAllColumn(){
	musterForm.action="/general/muster/fillout_musterdata.do?b_show=allshow";
	musterForm.submit();        
}   
function openAddDel_fields(tabid){
	var urlstr = "/general/muster/add_del_fields.do?b_open=link&infor_Flag=${musterForm.infor_Flag}&currid="+tabid;
	var bok =window.showModalDialog(urlstr,null,"dialogWidth=500px;dialogHeight=450px");   
	if(bok){
	  	musterForm.action="/general/muster/open_musterdata.do?b_open=link&tabid=${musterForm.currid}&dbpre=${musterForm.dbpre}";
		musterForm.submit();  	  
	}
}
function isSuccess(outparamters){
	musterForm.action="/general/muster/fillout_musterdata.do";
	musterForm.submit();     
}
function onchanges(){
    musterForm.action="/general/muster/open_musterdata.do?b_open=link&tabid=${musterForm.currid}&infor_Flag=${musterForm.infor_Flag}&res=1";
	musterForm.submit();     
}
function moveRecord(setname){
    var tablename,table,dataset,dec_idx,recidx,keyno,infor;
    var hashvo=new ParameterSet();
    tablename="table"+setname;
    table=$(tablename);
    dataset=table.getDataset();	
    recidx=dataset.getValue("recidx");
    if(recidx==null||recidx.length<1){
    	alert(SELECT_SHIFT_RECORD);
    	return false;
    }
    
    infor=$F("infor_Flag");
    if(infor=="2")
       keyno=dataset.getValue("B0110_CODE");
    else if(infor=="3")
       keyno=dataset.getValue("E01A1_CODE");
    else
       keyno=dataset.getValue("A0100");                 
   	dec_idx=prompt(SHIFT_WHERE_RECORD_BEFORE,recidx);
   	if(dec_idx==recidx)
   	   return;
   	if(dec_idx&&getInt(dec_idx)>0)
   	{
		hashvo.setValue("dec_idx",dec_idx);
		hashvo.setValue("setname",setname);
		hashvo.setValue("recidx",recidx);	
		hashvo.setValue("keyno",keyno);
		hashvo.setValue("infor",infor);
     	var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0521010008'},hashvo); 
    }       
}
function addObject(objlist,setname,infor){	
    var hashvo=new ParameterSet();
    hashvo.setValue("objlist",objlist);
	hashvo.setValue("setname",setname);
	hashvo.setValue("infor",infor);	   
	hashvo.setValue("history","${musterForm.history}");   
	var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0521010009'},hashvo); 
}
/*快速查询*/
function get_quick_query(infor,setname,dbpre){
    var dbpre_arr=new Array();
    dbpre_arr[0]=dbpre;
	var objlist=quick_query(infor,dbpre_arr);
	if(objlist&&objlist.length>0){
		for(var i=0;i<objlist.length;i++){
			objlist[i]=objlist[i];		  
		}		 
		addObject(objlist,setname,infor);	
	}
}
function get_hand_query(infor,setname,dbpre){
   	var dbpre_arr=new Array();
    dbpre_arr[0]=dbpre;   
    if(infor==3)
        infor=4;      
	var objlist=handwork_selectObject(infor,dbpre);	
	if(infor==4)
		infor=3;
	if(objlist&&objlist.length>0){
		if(infor==1){
			for(var i=0;i<objlist.length;i++){
			    objlist[i]=dbpre+objlist[i];
			}
		}
		addObject(objlist,setname,infor);
	}
}   
function get_common_query(infor,setname,dbpre,query_type){
    var dbpre_arr=new Array();
    dbpre_arr[0]=dbpre;
	var objlist=common_query(infor,dbpre_arr,query_type);
	if(objlist&&objlist.length>0){
		for(var i=0;i<objlist.length;i++){
		    objlist[i]=objlist[i];
		}		 
		addObject(objlist,setname,infor);
	}
}   
function get_usually_query(id,setname,infor){
    var hashvo=new ParameterSet();
	hashvo.setValue("id",id);
	hashvo.setValue("setname",setname);
	var In_paramters="infor="+infor;  
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnObj,functionId:'0540000005'},hashvo);
}
function returnObj(outparamters){
   	var infor=outparamters.getValue("infor");
   	var setname="${musterForm.mustername}";
   	var objIDs=outparamters.getValue("objIDs");
   	if(objIDs!=null&&objIDs.length>0){
   		var obj=objIDs.split("/");
   		addObject(obj,setname,infor);
   	}
}
function get_page_options(tabid){
   var param_vo=oageoptions_selete("2",tabid);
}
function reture_list(infor){
   	var open='<%=(request.getParameter("b_open"))%>';
   	if(open=="0"){
   	  	musterForm.action="/system/home.do?b_query=link";
   	}else{
		musterForm.action="/general/muster/muster_list.do?b_query=link&a_inforkind="+infor;
	}
	musterForm.submit();     
}
function executePDF2(setname){
	newwindow=window.open("/servlet/muster/Print?mustername=${musterForm.mustername}",'glWin','toolbar=no,location=no,directories=no,status=no,menubar=yes,scrollbars=no,top=170,left=220,resizable=no');
}
function showFieldList(outparamters){
	var outName=outparamters.getValue("outName");
	var flag=outparamters.getValue("flag");
	//alert(outName+"  "+flag)
	if(flag==1){
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
	}else{
		window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
	}
}
function executeOutFile(setname,flag){
	//var tablename,table,col,fieldWidths;       
   // tablename="table"+setname;
   //	table=$(tablename);
  //  var coumsize = '${musterForm.coumsize}';
	//if(coumsize<2)
	//	return ;
   //	for(var i=2;i<=coumsize;i++){
  // 		var convalue = table.getColWidth(table,i);
   //		if(convalue==null||convalue==undefined||convalue.length<1)
   //			continue;
   //    	fieldWidths+="/"+table.getColWidth(table,i);
  //  }
	var hashvo=new ParameterSet();
//	hashvo.setValue("fieldWidths",fieldWidths);
	hashvo.setValue("mustername","${musterForm.mustername}");
	var In_paramters="flag="+flag;  
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList,functionId:'0521010010'},hashvo);
}
function save(){
   if(!parent.frames['mil_menu'])
      return;
   if(parent.frames['mil_menu'].Global==null)
   		return;
   var currnode=parent.frames['mil_menu'].Global.selectedItem;
   var uid = "${musterForm.moduleflag}";
   uid=uid.substring(1,3);
   var text = "${musterForm.currid}"+"."+"${musterForm.mustertitle}";
   var title = "${musterForm.currid}"+"."+"${musterForm.mustertitle}";
	
   var action = "/general/muster/open_musterdata.do?b_open=link&tabid=${musterForm.currid}";
   var xml = "/general/muster/hmuster/searchrostertree?flag=2&moduleflag=${musterForm.moduleflag}&flaga=${musterForm.infor_Flag}";
   if(currnode){
	   if(currnode.load){
	   	 	var imgurl="/images/overview_obj.gif";
	   	 	parent.frames['mil_menu'].add(uid,text,action,"mil_body",title,imgurl,xml);
	   }else{
	   	 	parent.frames['mil_menu'].location.reload();
	   }
	}
	else{
		parent.frames['mil_menu'].location.reload();
	}
}
	
function locate(infor,setname,tabid)   {
	var tablename,table,dataset;
	tablename="table"+setname;
    table=$(tablename);
    dataset=table.getDataset();
	var retvo=locate_dialog(infor,"0",tabid,dataset);        
	/*
		var record=dataset.find(["A0101"],["赵一军"]);
		if(record)
			dataset.setRecord(record);
	*/
}
function clearAll(tabid){
	if(!confirm(CLEAR_ALL_RECORD)){
		return;
	}
	var dbpre="${musterForm.dbpre}";
	var infor_kind="${musterForm.infor_Flag}";
	var hashvo=new ParameterSet();
	hashvo.setValue("tabid",tabid);
	hashvo.setValue("dbpre",dbpre);
	hashvo.setValue("infor_Flag",infor_kind);
	var request=new Request({method:'post',asynchronous:false,functionId:'0540000011'},hashvo);
	musterForm.action="/general/muster/open_musterdata.do?b_open=link&tabid="+tabid;
	musterForm.submit();  
}
function blackMaint(checkflag){
	if(checkflag=='2'){
		musterForm.action="/templates/index/portal.do?b_query=link";
		musterForm.submit(); 
	}else if(checkflag=='3'){
		musterForm.action="/general/muster/muster_list.do?b_query=link&checkflag=1&a_inforkind=1&ver=5";
		musterForm.submit(); 
	}else if(checkflag=='4'){
		musterForm.action="/general/muster/muster_list.do?b_query=link&checkflag=2&a_inforkind=1&ver=5";
		musterForm.submit(); 
	}else{
		musterForm.action="/system/home.do?b_query=link";
		musterForm.submit(); 
	}
}
function searchInform(type,query_type,a_code,tablename){
	var thecodeurl =""; 
	var return_vo;	
	switch(query_type){ 
         case 1	: 
              thecodeurl="/general/inform/search/generalsearch.do?b_query=link&type="+type+"&a_code="+a_code+"&tablename="+tablename;
              return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:700px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no;");
              break ; 
         case 2 : 
         	   thecodeurl="/general/inform/search/gmsearch.do?b_query=link&type="+type+"&a_code="+a_code+"&tablename="+tablename;
               return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:700px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no;");
              break ; 
         case 3 : 
              thecodeurl="/general/inform/search/searchcommon.do?b_query=link&type="+type+"&flag=search&a_code="+a_code+"&tablename="+tablename;
               return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:280px;resizable:no;center:yes;scroll:no;status:no;");
              break ; 
         default:
         	thecodeurl="";
    } 
    if(thecodeurl.length<1){
    	return;
    }
    if(return_vo!=null){
  	 	resetStyleRoster();
  	}else{
  		return ;
  	}
}
function searchGeneral(type,obj,a_code,dbpre){
	var hashvo=new ParameterSet();
	hashvo.setValue("id",obj.value);	
	hashvo.setValue("a_code",a_code);
	hashvo.setValue("tablename",dbpre);
	hashvo.setValue("type",type);
	hashvo.setValue("flag","search");				
	var request=new Request({method:'post',asynchronous:false,onSuccess:checkSearchGeneral,functionId:'3020110076'},hashvo);
}
function checkSearchGeneral(outparamters){
	var check = outparamters.getValue("check");
	if(check=='ok'){
		resetStyleRoster();
    }
}
function resetStyleRoster(){
	var tabid = "${musterForm.currid}";;
	var dbpre ="${musterForm.dbpre}";
	var inforkind = "${musterForm.infor_Flag}";
	var a_code = "${musterForm.a_code}";

	var hashvo=new ParameterSet();
	hashvo.setValue("tabid",tabid);	
	hashvo.setValue("dbpre",dbpre);	
	hashvo.setValue("inforkind",inforkind);	
	hashvo.setValue("a_code",a_code);					
	var request=new Request({method:"post",asynchronous:false,functionId:"0540000010"},hashvo);
	musterForm.action="/general/muster/open_musterdata.do?b_open=link&tabid="+tabid;
	musterForm.submit();  
}
function comback(checkflag){
	if(checkflag=='1'){ //修改Search6PortalInfoServlet传递参数 checkflag的值，由2改为1 MusterForm中checkflag注释为0.不显示返回按钮 1.首页
	<%if("hcm".equalsIgnoreCase(bosflag)){%>
        document.location.href="/templates/index/hcm_portal.do?b_query=link";    //changxy【22921】 九宫格内点击打开的花名册返回应该是返回到九宫格页面
    <%}else{%>
        document.location.href="/templates/index/portal.do?b_query=link";
    <%}%>
	}else{
	document.location.href="/general/muster/emp_muster.do?b_query=link&backCurrentPage=1&returnvalue=2";//liuy 2014-12-26 634：主页花名册，在第二页选择个花名册打开后返回，返回到第一页了
	}
}
</script>
<html:form action="/general/muster/open_musterdata">  
<%int i=0;int j=0; %>
 <logic:iterate id="info"  name="musterForm" property="dblist"> 
 <%i++; %>
 </logic:iterate>
 <logic:iterate id="info" name="musterForm" property="condlist"> 
 <%j++; %>
 </logic:iterate>
<table id="selectprename"  style="position:absolute;left:280px;top:<%=toolbarTop %>;z-index:10;"><tr>
	<tr>
		<td>
			<%if(i>1){ %>
			人员库:
			<html:select name="musterForm" property="dbpre" size="1" onchange="onchanges();">
   				<html:optionsCollection property="dblist" value="dataValue" label="dataName"/>
			</html:select>
			<%}else{%>
				<html:hidden name="musterForm" property="dbpre"/>
			<%} if(j>1){ %>
			查询条件:
			<html:select name="musterForm" property="condid" size="1" onchange="onchanges();">
   				<html:optionsCollection property="condlist" value="dataValue" label="dataName"/>
			</html:select>
			<%} else{%>
			<html:hidden name="musterForm" property="condid"/>
			<%} %>
		</td>
		<td>
		${musterForm.mustertitle}
		</td>
	</tr>
</table>
<div id="sapcecontrol" style="border:0px;margin-top:2px;" >
</div>
<hrms:dataset name="musterForm" property="fieldlist" scope="session" setname="${musterForm.mustername}"  
setalias="muster_set" rowlock="true" readonly="true" editable="true" select="true" 
pagerows="${musterForm.pagerows}" sql="${musterForm.sql}" buttons="bottom">
<hrms:commandbutton name="saveR" hint="" functionId="" refresh="true" type="all-change" setname="${musterForm.mustername}" onclick="executeOutFile('${musterForm.mustername}','2');" >
     <bean:message key="general.inform.muster.output.excel"/>
   </hrms:commandbutton>
   
   <hrms:commandbutton name="save" hint="" functionId="" refresh="true" type="all-change" setname="${musterForm.mustername}" onclick="executeOutFile('${musterForm.mustername}','1');" >
     <bean:message key="edit_report.outPDF"/>
   </hrms:commandbutton>
   <%if(!"bi".equals(bosflag)){%>
    <hrms:commandbutton name="ackb" hint="" functionId="" refresh="true" type="all-change" setname="${musterForm.mustername}" onclick='comback("${musterForm.checkflag}")' >
     <bean:message key="button.return"/>
   </hrms:commandbutton>
   <%} %>
   
</hrms:dataset>
</html:form>

