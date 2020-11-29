<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.general.muster.MusterForm,org.apache.commons.beanutils.LazyDynaBean" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
%>
<script language="javascript" src="/js/page_options.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript">
CheckBrowserCompatibility();
function setColumnTitle(outparamters){
   	var setname=outparamters.getValue("setname");
   	var tablename,table,col,title;
   	title=outparamters.getValue("title");
    tablename="table"+setname;
    table=$(tablename);
    col=table.getActiveCellIndex();
    table.resetColumnTitle(table,col,title);
    dataset=table.getDataset();
    temp=table.getActiveCell();
    field_name=temp.getField();
    field=dataset.getField(field_name);
    field.label = title;  //wangcq 记录当前修改的title值
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
	var urlstr = "/general/muster/add_del_fields.do?b_search=link&infor_Flag=${musterForm.infor_Flag}&currid="+tabid;
	var bok =window.showModalDialog(urlstr,null,"dialogWidth=500px;dialogHeight=450px");   
	if(bok){
	  	musterForm.action="/general/muster/fillout_musterdata.do?b_search=link&checkflag=${musterForm.checkflag}&tabid=${musterForm.currid}&dbpre=${musterForm.dbpre}";
		musterForm.submit();  	  
	}
}
function isSuccess(outparamters){
	musterForm.action="/general/muster/fillout_musterdata.do?b_search=link&checkflag=${musterForm.checkflag}&tabid=${musterForm.currid}";
	musterForm.submit();     
}
function onchanges(obj){
    musterForm.action="/general/muster/fillout_musterdata.do?b_search=link&isGetData=1&checkflag=${musterForm.checkflag}&tabid=${musterForm.currid}&dbpre="+obj.value;
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
function setSort(){
	var sortitem = document.getElementById("sortitem").value;
	var infor = "${musterForm.infor_Flag}";
	var infor_Flag = "r1"
	if(infor=="1")
		infor_Flag="r1";
	else if(infor=="2")
		infor_Flag="21";
	else if(infor=="3")
		infor_Flag="41";
	var thecodeurl ="/gz/sort/sorting.do?b_query=link&flag="+infor_Flag+"&sortitem="+$URL.encode(getEncodeStr(sortitem)); 
	var return_vo= window.showModalDialog(thecodeurl, "", 
          "dialogWidth:570px; dialogHeight:370px;resizable:no;center:yes;scroll:yes;status:no;");
   if(return_vo!=null){
   		return_vo=return_vo!='not'?return_vo:"";
    	document.getElementById("sortitem").value = return_vo;
    	var hashvo=new ParameterSet();
    	hashvo.setValue("sortitem",return_vo);	   
		hashvo.setValue("tabid","${musterForm.currid}");  
		hashvo.setValue("infor",infor); 
		hashvo.setValue("dbpre","${musterForm.dbpre}"); 
		var request=new Request({asynchronous:false,functionId:'0521010020'},hashvo); 
		resetStyleRoster();
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
		//for(var i=0;i<objlist.length;i++){
		//	objlist[i]=objlist[i];		    //注释无用代码 changxy 20160816
		//}		 
		addObject(objlist,setname,infor);	
	}
}
function get_hand_query(infor,setname,dbpre){
   	var dbpre_arr=new Array();
    dbpre_arr[0]=dbpre;   
    if(infor==3)
        infor=4;      
	var objlist=handwork_selectObject2(infor,dbpre);	
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
	newwindow=window.open("/servlet/muster/Print?mustername=${musterForm.mustername}",'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,top=170,left=220,resizable=no');
}
function showFieldList(outparamters){
	var waitInfo;
    waitInfo=eval("waitFile");
 	waitInfo.style.display="none";
	var outName=outparamters.getValue("outName");
	var flag=outparamters.getValue("flag");
	//alert(outName+"  "+flag)
	window.location.target="_blank";
	window.location.href="/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true";
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
	hashvo.setValue("infor_Flag","${musterForm.infor_Flag}");
	hashvo.setValue("dbpre","${musterForm.dbpre}");
	hashvo.setValue("tabid","${musterForm.currid}");
	var In_paramters="flag="+flag;  
	var waitInfo;
    waitInfo=eval("waitFile");
 	waitInfo.style.display="block";
	var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:showFieldList,functionId:'0521010010'},hashvo);
}
function save(){
   if(!parent.frames['nil_menu'])
      return;
   self.parent.nil_menu.location ="/general/muster/hmuster/rostertree.jsp";
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
	musterForm.action="/general/muster/fillout_musterdata.do?b_search=link&checkflag=${musterForm.checkflag}&tabid="+tabid;
	musterForm.submit();  
}
function blackMaint(checkflag){
	if(checkflag=='2'){
		musterForm.action="/templates/index/hcm_portal.do?b_query=link";
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
              if(confirm("简单查询将根据查询结果重填花名册，是否继续？"))
              {
                 thecodeurl="/general/inform/search/generalsearch.do?b_query=link&type="+type+"&a_code="+a_code+"&tablename="+tablename;
                 return_vo= window.showModalDialog(thecodeurl, "", 
                 	"dialogWidth:700px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no;");
              }
              break ; 
         case 2 : 
               if(confirm("通用查询将根据查询结果重填花名册，是否继续?"))
              {
             	   thecodeurl="/general/inform/search/gmsearch.do?b_query=link&type="+type+"&a_code="+a_code+"&tablename="+tablename;
                   return_vo= window.showModalDialog(thecodeurl, "", 
                      "dialogWidth:700px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no;");
              }
              break ; 
         case 3 : 
           if(confirm("常用查询将根据查询结果重填花名册，是否继续?"))
              {
                 thecodeurl="/general/inform/search/searchcommon.do?b_query=link&type="+type+"&flag=search&a_code="+a_code+"&tablename="+tablename;
                 return_vo= window.showModalDialog(thecodeurl, "", 
                   "dialogWidth:400px; dialogHeight:280px;resizable:no;center:yes;scroll:no;status:no;");
              }
              break ; 
         default:
         	thecodeurl="";
    } 
    if(thecodeurl.length<1){
    	return;
    }
    if(typeof return_vo == "object"){
    	displayProcessBar();
		setTimeout(function(){resetStyleRoster(return_vo)},100);
    }else if(return_vo!=null&&return_vo!='aaa'){
  	 	resetStyleRoster();
  	}else{
  		return ;
  	}
}
function searchGeneral(type,id,a_code,tablename){
    if(confirm("常用查询将根据查询结果重填花名册，是否继续?"))
    {
    	var hashvo=new ParameterSet();
	    hashvo.setValue("id",id);	
    	hashvo.setValue("a_code",a_code);
    	hashvo.setValue("tablename",tablename);
    	hashvo.setValue("type",type);
    	hashvo.setValue("flag","search");				
	    var request=new Request({method:'post',asynchronous:false,onSuccess:checkSearchGeneral,functionId:'3020110076'},hashvo);
	 }
}
function checkSearchGeneral(outparamters){
	var check = outparamters.getValue("check");
	if(check=='ok'){
		var obj = new Object();
		obj.isExistField=outparamters.getValue("isExistField");
		obj.wherestr=outparamters.getValue("wherestr");
		obj.wheresql=outparamters.getValue("wheresql");
		displayProcessBar();
		setTimeout(function(){resetStyleRoster(obj)},100);
    }
}
function resetStyleRoster(return_vo){
	var tabid = "${musterForm.currid}";
	var dbpre ="${musterForm.dbpre}";
	var inforkind = "${musterForm.infor_Flag}";
	var a_code = "${musterForm.a_code}";

	var hashvo=new ParameterSet();
	hashvo.setValue("tabid",tabid);	
	hashvo.setValue("dbpre",dbpre);	
	hashvo.setValue("inforkind",inforkind);	
	hashvo.setValue("a_code",a_code);
	if(return_vo){
		if(return_vo.isExistField)
			hashvo.setValue("isExistField",return_vo.isExistField);
		if(return_vo.wherestr)
			hashvo.setValue("wherestr",return_vo.wherestr);
		if(return_vo.wheresql)
			hashvo.setValue("wheresql",return_vo.wheresql);//常用花名册筛选条件
	}					
	var request=new Request({method:"post",asynchronous:false,functionId:"0540000010"},hashvo);
	musterForm.action="/general/muster/fillout_musterdata.do?b_search=link&checkflag=${musterForm.checkflag}&tabid="+tabid;
	musterForm.submit();  
}
function displayProcessBar(){
	var x=(window.screen.width-700)/2;
	var y=(window.screen.height-500)/2; 
	var waitInfo=document.getElementById("wait");
	if (waitInfo!=null){
		waitInfo.style.top=y;
		waitInfo.style.left=x;
	  	waitInfo.style.display="block";
	}
}
function returnFirst(){
   	self.parent.location= "/general/tipwizard/tipwizard.do?br_employee=link";
}
function closeWin()
{
   window.parent.close();
}
function closeWinD()
{
  window.close();
}
</script>

    <!-- 
    <hrms:menuitem name="mitem7" label="查找定位" icon="/images/quick_query.gif" url="locate('${musterForm.infor_Flag}','${musterForm.mustername}','${musterForm.currid}');" command="" />      
    -->
<hrms:themes />
<%if("hcm".equalsIgnoreCase(bosflag)){ %>
<style>
.filloutmusterdataTable{
	margin-top:-3px;
}
.filloutmusterdataSelect{
	position:absolute;
	left:300px;
	top:39px;
}
</style>
<%}else{ %>
<style>
.filloutmusterdataTable{
	margin-top:7px;
}
.filloutmusterdataSelect{
	position:absolute;
	left:300px;
	top:39px;
}
</style>
<%} %>
<html:form action="/general/muster/fillout_musterdata"> 
<table border=0 cellpadding="0" cellspacing="0" class="filloutmusterdataTable"><tr><td>
<%
    MusterForm musterForm=(MusterForm)session.getAttribute("musterForm");
    String closeWindow=musterForm.getCloseWindow();
    if(closeWindow==null||"".equals(closeWindow)){
    	closeWindow="";
    }
    String returnflag=musterForm.getReturnflag();
    if(returnflag==null||"".equals(returnflag)){
    	returnflag="";
    }
    int i=0;
    ArrayList dblist = musterForm.getDblist();
    if(dblist!=null&&dblist.size()>1)
       i=1;
 %>
<html:hidden name="musterForm" property="infor_Flag"/>
<html:hidden name="musterForm" property="currid"/>
<html:hidden name="musterForm" property="sortitem"/>
<html:hidden name="musterForm" property="closeWindow"/>
<table  ><tr><td>
<hrms:menubar menu="menu1" id="menubar1">
  <hrms:menuitem name="file" label="menu.file.label" enabled="${musterForm.showMenu}" > 
	    <hrms:menuitem name="mitem1" label="kq.report.pagesetup" icon="/images/print.gif" url="get_page_options('${musterForm.currid}');"/> 
	    <hrms:menuitem name="mitem2" label="menu.out.label" icon="/images/print.gif" url="">   
	       <hrms:menuitem name="mitem1" label="edit_report.outPDF" icon="" url="executeOutFile('${musterForm.mustername}','1');" command="" enabled="true" visible="true"/>
	       <hrms:menuitem name="mitem2" label="general.inform.muster.output.excel" icon="" url="executeOutFile('${musterForm.mustername}','2');" command="" enabled="true" visible="true"/>
	    </hrms:menuitem> 
	    <hrms:menuitem name="mitem3" label="inform.muster.clear.record" icon="/images/delete_all.gif" url="clearAll('${musterForm.currid}');" command="" enabled="true"/>
  </hrms:menuitem>
  <hrms:menuitem name="rec" label="infor.menu.rec" enabled="${musterForm.showMenu}" >
	    <hrms:menuitem name="mitem1" label="general.inform.muster.hand.select" icon="/images/quick_query.gif" url="get_hand_query('${musterForm.infor_Flag}','${musterForm.mustername}','${musterForm.dbpre}');" command="" />
	    <hrms:menuitem name="mitem2" label="general.inform.muster.most.search" icon="/images/quick_query.gif" url="get_quick_query('${musterForm.infor_Flag}','${musterForm.mustername}','${musterForm.dbpre}');" command="" />  
	    <hrms:menuitem name="mitem3" label="infor.menu.squery" icon="/images/quick_query.gif" url="searchInform('${musterForm.infor_Flag}',1,'${musterForm.a_code}','${musterForm.dbpre}');" command="" />  
	    <hrms:menuitem name="mitem4" label="infor.menu.hquery" icon="/images/quick_query.gif" url="searchInform('${musterForm.infor_Flag}',2,'${musterForm.a_code}','${musterForm.dbpre}');" command="" />  
	    <hrms:menuitem name="mitem7" label="infor.menu.gquery" icon="/images/quick_query.gif"  command="">
	  	<%
	      
	       ArrayList condlist=musterForm.getCondlist();
	       int n=0;
	       for(int a=0;a<condlist.size();a++){
	          LazyDynaBean abean=(LazyDynaBean)condlist.get(a);
	          String name=(String)abean.get("name");
	          String id=(String)abean.get("id");
	          String  param="searchGeneral('"+musterForm.getInfor_Flag()+"',"+id+",'"+musterForm.getA_code()+"','"+musterForm.getDbpre()+"')";
	     %>
	    	<hrms:menuitem name='<%="mitem"+(n+8)+""%>' label="<%=name%>" icon="/images/quick_query.gif" url="<%=param%>"  command="" />      		
	    <% n++;
	    	if(n==10){
	    		break;
	    } } %>
	    <hrms:menuitem name='<%="mitem"+(n+9)+""%>' label='general.inform.search.themore' icon="" url="searchInform('${musterForm.infor_Flag}',3,'${musterForm.a_code}','${musterForm.dbpre}');" command="" enabled="true" visible="true"/>
	    </hrms:menuitem>     
	    <hrms:menuitem name="mitem5" label="infor.menu.move" icon="/images/sort.gif" url="moveRecord('${musterForm.mustername}');" command="" />     
  </hrms:menuitem>  
  <hrms:menuitem name="edit" label="rowcheckanalyse.col" enabled="${musterForm.showMenu}" >
      <hrms:menuitem name="mitem1" label="general.inform.muster.adddel.item" icon="/images/add_del.gif" url="openAddDel_fields('${musterForm.currid}');" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem2" label="menu.editcol.label" icon="/images/write.gif" url="renameTitle('${musterForm.mustername}');" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem2" label="修改排序指标" icon="/images/sort.gif" url="setSort();" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem3" label="general.inform.muster.hide.out" icon="" url="hideColumn('${musterForm.mustername}');" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem4" label="general.inform.muster.canhide.out" icon="" url="showAllColumn();" command="" enabled="true" visible="true"/>                
  </hrms:menuitem>  
</hrms:menubar>
</td></tr></table>
</td>
<td>&nbsp;</td>
</tr>
</table >
<logic:notEqual name="musterForm" property="checkflag" value="0">
<hrms:dataset name="musterForm" property="fieldlist" scope="session" setname="${musterForm.mustername}" 
 setalias="muster_set" rowlock="true" readonly="${musterForm.dataset_readonly}" editable="${musterForm.dataset_editable}" select="${musterForm.dataset_select}"
  pagerows="${musterForm.pagerows}" sql="${musterForm.sql}" buttons="bottom"> 
   <hrms:commandbutton name="saveR" hint="" functionId="0521010021" refresh="true" type="all-change" 
        visible="${musterForm.canModifyData}" setname="${musterForm.mustername}" >
     <bean:message key="kq.kq_rest.submit"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="delselected" hint="general.inform.search.confirmed.del" functionId="0521010001" refresh="true" 
        type="selected" visible="${musterForm.canModifyData}" setname="${musterForm.mustername}" >
     <bean:message key="button.setfield.delfield"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="exportexcel" hint="" functionId="" refresh="true" type="selected" setname="${musterForm.mustername}" onclick="executeOutFile('${musterForm.mustername}','2');">
     <bean:message key="general.inform.muster.output.excel"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="exportpdf" hint="" functionId="" refresh="true" type="selected" setname="${musterForm.mustername}" onclick="executeOutFile('${musterForm.mustername}','1');">
     <bean:message key="edit_report.outPDF"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="intoback" hint="" functionId="" refresh="true" type="selected" setname="${musterForm.mustername}" onclick="blackMaint('${musterForm.checkflag}');">
     <bean:message key="kq.search_feast.back"/>
   </hrms:commandbutton>
</hrms:dataset>
</logic:notEqual>
<logic:equal name="musterForm" property="checkflag" value="0">
<hrms:dataset name="musterForm" property="fieldlist" scope="session" setname="${musterForm.mustername}"  
setalias="muster_set" rowlock="true" readonly="${musterForm.dataset_readonly}" editable="${musterForm.dataset_editable}" select="${musterForm.dataset_select}" 
pagerows="${musterForm.pagerows}" sql="${musterForm.sql}" buttons="bottom">
   <hrms:commandbutton name="saveR" hint="" functionId="0521010021" refresh="true" type="all-change" 
        visible="${musterForm.canModifyData}" setname="${musterForm.mustername}" >
     <bean:message key="kq.kq_rest.submit"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="delselected" hint="general.inform.search.confirmed.del" functionId="0521010001" refresh="true" 
        type="selected" visible="${musterForm.canModifyData}" setname="${musterForm.mustername}" >
     <bean:message key="button.setfield.delfield"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="exportexcel" hint="" functionId="" refresh="true" type="selected" setname="${musterForm.mustername}" onclick="executeOutFile('${musterForm.mustername}','2');">
     <bean:message key="general.inform.muster.output.excel"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="exportpdf" hint="" functionId="" refresh="true" type="selected" setname="${musterForm.mustername}" onclick="executeOutFile('${musterForm.mustername}','1');">
     <bean:message key="edit_report.outPDF"/>
   </hrms:commandbutton>   
   <%if(returnflag.equalsIgnoreCase("dxt")){ %>
   <hrms:commandbutton name="returnHome" hint="" functionId="" refresh="true" type="selected" setname="${musterForm.mustername}" onclick="hrbreturn('emp','il_body','musterForm');">
     <bean:message key="button.return"/>
   </hrms:commandbutton>
   <%} %>
   <%if(closeWindow.equals("1")){ %>
   <hrms:commandbutton name="returnHome" hint="" functionId="" refresh="false" type="selected" setname="${musterForm.mustername}" onclick="closeWin();">
     <bean:message key="button.close"/>
   </hrms:commandbutton>
   <%} %>
    <%if(closeWindow.equals("2")){ %>
   <hrms:commandbutton name="returnHome" hint="" functionId="" refresh="false" type="selected" setname="${musterForm.mustername}" onclick="closeWinD();">
     <bean:message key="button.close"/>
   </hrms:commandbutton>
   <%} %>
</hrms:dataset>
</logic:equal>
<div style="position:absolute;top:40;left:500;white-space: nowrap;">${musterForm.mustertitle}&nbsp;&nbsp;${musterForm.countStr}</div>
<%if(i>0){ %>
<logic:equal name="musterForm" property="infor_Flag" value="1">
	<html:select name="musterForm" property="dbpre" size="1" onchange="onchanges(this);" styleClass="filloutmusterdataSelect">
   		<html:optionsCollection property="dblist" value="dataValue" label="dataName"/>
	</html:select>
</logic:equal>
<%}else{ %>
<html:hidden name="musterForm" property="dbpre"/>
<%} %>
</html:form>
<logic:equal name="musterForm" property="save_flag" value="save">
<script language="javascript">
save();
</script>
</logic:equal>
<div id='wait' style='position:absolute;top:285;left:80;display:none;z-index:999;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" style="font-size:12px;" height=24>
					正在处理，请稍候...
				</td>
			</tr>
		</table>
</div>

<div   id="waitFile" style='position:absolute;top:285;left:240;display:none;width:500px;heigth:250px'>
 
		<table border="1" width="50%" cellspacing="0" cellpadding="4" class="table_style" height="100" align="center">
			<tr>
			
				<td class="td_style" height=24 id="hlw">
					正在导出，请稍候...
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

