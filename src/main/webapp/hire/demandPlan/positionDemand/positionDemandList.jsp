<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<script language="javascript" src="/js/function.js"></script>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.hire.demandPlan.PositionDemandForm,com.hrms.struts.taglib.CommonData"%>

<%
	response.setHeader("Pragma", "No-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("Expires", 0);
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	PositionDemandForm positionDemandForm = (PositionDemandForm) session
			.getAttribute("positionDemandForm");
	String model = positionDemandForm.getModel().trim();
	String isOrgWillTableIdDefine = positionDemandForm
			.getIsOrgWillTableIdDefine();
	String showUMCard = positionDemandForm.getShowUMCard();
	String spRelation = positionDemandForm.getSpRelation();
	String spcount = positionDemandForm.getSpcount();
	/*
	Enumeration e = session.getAttributeNames();
	while(e.hasMoreElements()) {
	    System.out.println(e.nextElement());
	}
	 */
	String returnflag = positionDemandForm.getReturnflag();
	//String aurl = (String)request.getServerName();
	//String port=request.getServerPort()+"";
	//String prl=request.getProtocol();
	//int idx=prl.indexOf("/");
	//prl=prl.substring(0,idx);
	//String url_p=prl+"://"+aurl+":"+port;
	String url_p = SystemConfig.getServerURL(request);
	String hireMajor = positionDemandForm.getHireMajor();
%>

<style type="text/css"> 

.selectPreForBP{
    position:absolute;
    left:545px;
    top:30px;
    z-index: 10;
}

</style>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript">
   var codeid='<%=(request.getParameter("code"))%>'   
   //this.status ="招聘管理 / 用工需求";
   var hireMajor='<%=hireMajor%>';
  
   
   //查询
   function query()
   {
   		var hashvo=new ParameterSet();
		hashvo.setValue("opt","query");
		var In_paramters="tableName=Z03"; 	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:ReturnQuery,functionId:'3000000104'},hashvo);			
   }
   
   function ReturnQuery(outparamters)
   {
   		
   		var fields_temp=outparamters.getValue("fields");		
   		var fields=new Array();
   		for(var i=0;i<fields_temp.length;i++)
   		{
   			////数组[ 0:列名 1:列描述  2:列的类型 3:如果为代码型,则为代码值,否则为空 ]
   			var a_field=fields_temp[i].split("<@>");
   			fields[i]=a_field
   		}
    	 var extendSql=generalQuery("Z03",fields);
    	 if(extendSql)
    	 {
	    	 document.positionDemandForm.extendSql.value=extendSql;
    	 	 positionDemandForm.action='/hire/demandPlan/positionDemand/positionDemandTree.do?b_query='+'${positionDemandForm.linkDesc}'+'&model=1&codeset=<%=(request.getParameter("codeset"))%>&code=<%=(request.getParameter("code"))%>';
    	 	 positionDemandForm.submit();
    	 }
  
   }
   
   
   //排序
   function taxis()
   {
   		var hashvo=new ParameterSet();
		hashvo.setValue("opt","taxis");
   		var In_paramters="tableName=Z03"; 	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:ReturnTaxis,functionId:'3000000104'},hashvo);			
   }
   
   
   function ReturnTaxis(outparamters)
   {
   		var fields_temp=outparamters.getValue("fields");		
   		var fields=new Array();
   		for(var i=0;i<fields_temp.length;i++)
   		{
   			////数组[ 0:列名 1:列描述  2:列的类型 3:如果为代码型,则为代码值,否则为空 ]
   			var a_field=fields_temp[i].split("<@>");
   			fields[i]=a_field
   		}
    	 var orderSql=taxisDialog("Z03",fields);
    	 if(orderSql)
    	 {
	    	 document.positionDemandForm.orderSql.value=orderSql;
    	 	 positionDemandForm.action='/hire/demandPlan/positionDemand/positionDemandTree.do?b_query='+"${positionDemandForm.linkDesc}"+'&model=1&codeset=<%=(request.getParameter("codeset"))%>&code=<%=(request.getParameter("code"))%>';
    	 	 positionDemandForm.submit();
    	 }
    	
   }
   
   //空缺职位查询
   function SparePositionQuery()
   {
   		 positionDemandForm.action='/hire/demandPlan/positionDemand/positionDemandTree.do?b_sparePosition=link&model=1&codeid='+codeid;
    	 positionDemandForm.submit();
   }
   
   
   
   
   //招聘计划
   function hirePlan()
   {
    	 positionDemandForm.action='/hire/demandPlan/engagePlan.do?b_init=link&codeset=<%=(request.getParameter("codeset"))%>&code=<%=(request.getParameter("code"))%>&model=2&origin=a';
    	 positionDemandForm.submit();
   }
   
   
   
   
   //输出 EXCEL OR PDF
    function showfile(outparamters)
	{
		var outName=outparamters.getValue("outName");
		var flag=outparamters.getValue("flag");
		var fileflag="excel";
		if(flag==1)
			fileflag="pdf";
		
		outName = decode(outName);
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
	}

	/*
	*flag 1:pdf  2:excel
	*/
	function executeOutFile(flag)
	{
		var tablename,table,size,fieldWidths,whl_sql;       
        tablename="table${positionDemandForm.tablename}";
        table=$(tablename);
        size=${positionDemandForm.fieldSize};
        whl_sql="${positionDemandForm.whl_sql}";
        
   		for(var i=3;i<size+3;i++)
        {
        	///alert(table.getColWidth(table,i));
        	fieldWidths+="/"+table.getColWidth(table,i);
        }
          
		var hashvo=new ParameterSet();
		hashvo.setValue("fieldWidths",fieldWidths.substring(10));
		hashvo.setValue("tablename","${positionDemandForm.tablename}");
		hashvo.setValue("whl_sql","${positionDemandForm.sql}");
	    var In_paramters="flag="+flag;
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'0521010011'},hashvo);
		
	}
   
   
   function setPageFormat()
   {
   		
   		var param_vo=oageoptions_selete("3","${positionDemandForm.username}");
   
   }
   
   function goback(filk)
   {
   		var hashvo=new ParameterSet();
   		hashvo.setValue("flag",filk);
   		var In_paramters="flag="+filk; 	
   		
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:goback2,functionId:'3000000148'});			

   }
   function goback2(outparamters)
   {	
   		this.status="";
   		window.open("/templates/menu/hire_m_menu2.do?b_query=link&module=7","i_body")
   }
   
   
   function addPosition()
   {
   		 positionDemandForm.action='/hire/demandPlan/positionDemand/positionDemandTree.do?b_addPosition=init&operate=init';
    	 positionDemandForm.submit();
   
   }
   function queryBySp()
   {
      var id = "";
      var obj = document.getElementsByName("sp_flag");
      for(var i=0;i<obj[0].options.length;i++)
      {
          if(obj[0].options[i].selected)
          {
             id = obj[0].options[i].value;
             break;
          }
      } 
      var isstr="";    
      if(id!='-1')
      {
         isstr=id;
      }
     //document.positionDemandForm.extendSql.value=isstr;
     document.positionDemandForm.sp.value=id;
     var codeset=positionDemandForm.codeSetId.value;
     var code=positionDemandForm.codeItemId.value;
     positionDemandForm.action='/hire/demandPlan/positionDemand/positionDemandTree.do?b_query='+"${positionDemandForm.linkDesc}"+'&model=1&spflag='+id+'&codeset='+codeset+'&code='+code+'&isstr='+isstr;
     positionDemandForm.submit();
   }
   
   
   function delPosition()
   {
   	   if(confirm(CONFIRM_DELETE_SP_FLAG))
   	   {
     	    var tablename="table${positionDemandForm.tablename}";
            table=$(tablename);
            dataset=table.getDataset();
	        var record=dataset.getFirstRecord();
	        var selectID="";	
	        var num=0;	
	        var noNum=0;
		
	    	while (record) 
	    	{
			
		     	if (record.getValue("select"))
	    		{							
		     				num++;
		    				var status=record.getValue("z0319");
		    				if(status!='01'&&status!='02'&&status!='06'&&status!='07')
		    				{
		    					noNum++;
		     				}
		     				else
		     					selectID+="/"+record.getValue("z0301");	    
	    		}
	    		record=record.getNextRecord();
	    	}  	
     	   if(noNum>0)
	       {
	     		alert(ONLY_DELETE);
	     		return;
    	   }
     	   if(num==0)
     	   {
   	     		alert(PLEASE_SELECT_REQUESTMENT);
    	   		return;
    	   }
   	   
     		document.positionDemandForm.action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_del=del&z0301="+selectID.substring(1);
     		document.positionDemandForm.submit();
   		}
   }
function tableZ03_z0301a_onRefresh(cell,value,record)
{
   if(record!=null)
   {
      cell.innerHTML="<a href=\"javascript:checkPosition('"+record.getString("z0301a")+"','"+record.getString("z0319a")+"','"+record.getString("z0311a")+"')\"><img src=\"/images/view.gif\" border=\"0\"/></a>";
   }
}
function openPosition(z0301,posState)
{
   var src="/hire/demandPlan/positionDemand/positionDemandTree.do?b_browse=edit`entertype=1`codeset=<%=request.getParameter("codeset")%>`operate=browse`from=employPosition`posState="+posState+"`z0301="+z0301+"`isClose=0";

   var iframe_url="/general/query/common/iframe_query.jsp?src="+src;
   var values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:"+(window.screen.width)+"px; dialogHeight:"+(window.screen.height-200)+"px;resizable:yes;center:yes;scroll:yes;status:no;minimize:yes;maximize:yes;");			
	//window.open("/hire/demandPlan/positionDemand/positionDemandTree.do?b_browse=edit&entertype=1&codeset=<%=request.getParameter("codeset")%>&code=<%=(request.getParameter("code"))%>&operate=browse&from=employPosition&posState="+posState+"&z0301="+z0301,"_blank","width="+(window.screen.width-40)+",left=15,height="+(window.screen.height-180)+",scrollbars=yes, resizable=yes");
   if(values)
   {
      var obj= new Object();
      obj.refresh=values.refresh;
      if(obj.refresh=='1')
      {
         queryBySp();
      }
   }

} 

function checkPosition(z0301,posState,z0311)
{
   var hashVo=new ParameterSet();
   hashVo.setValue("z0301",z0301);
   hashVo.setValue("z0311",z0311);
   hashVo.setValue("type","6");
   hashVo.setValue("posState",posState);
   var In_parameters="opt=1";
   var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:check_ok,functionId:'1010021116'},hashVo);			
		
}
function check_ok(outparameters)
{
   var msg=outparameters.getValue("msg");
   if(msg=='0')
   {
       alert(THIS_POSITION_DELETE_IN_ORG+"！");
       return; 
   }
   else
   {
     var z0301=outparameters.getValue("z0301");
     var posState=outparameters.getValue("posState");
     openPosition(z0301,posState);
   }
}
//报批

function reportSP()
{
   var tablename="table${positionDemandForm.tablename}"
   table=$(tablename);
   dataset=table.getDataset();
   var record=dataset.getFirstRecord();
   var isctrl="${positionDemandForm.isCtrl}";
   var selectID="";
   
   while (record) 
	{
		if(record.getValue("select"))
		{							
			if(record.getValue("z0319")!='01'&&record.getValue("z0319")!='07')
			{
			   alert("只能报批起草和驳回状态的用工需求");
			   return;
			}
           if(record.getValue("z0329")==null||record.getValue("z0329")==''||record.getValue("z0331")==null||record.getValue("z0331")=='')
			{
			   alert("招聘需求的有效起始日期或结束日期为空，不予报批\r\n请填写完整后进行报批！");
			   return;
			}
			if(record.getValue("z0329")*1>record.getValue("z0331")*1)
			{
			     alert("招聘需求的有效起始日期大于结束日期，不予报批\r\n请修改后进行报批！");
			     return;
			}
			if(record.getValue("z0313")==null||record.getValue("z0313")=='')
			{
			  alert("招聘需求的需求人数没有填写，不予报批\r\n请修改后进行报批！");
			     return;
			}
			 if(isctrl=='1'){

		    	 if(record.getValue("z0316")==null||record.getValue("z0316")=='')
			     {
		    	     alert("招聘需求的编制指标没有填写，不予报批\r\n请修改后进行报批！");
			         return;
			     }
			   }
			selectID+=","+record.getValue("z0301");		        	    
		}
		record=record.getNextRecord();
	}
	if(trim(selectID).length<=0)
	{ 
	   alert("请选择要报批的用工需求");
	   return;
	}
   var hashVo=new ParameterSet();
   hashVo.setValue("z0301",selectID);
   hashVo.setValue("model","1");
   var In_parameters="opt=1";
   var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:checkBZ_OK,functionId:'3000000228'},hashVo);			
   	
}
function checkBZ_OK(outparameters)
{
   var message =getDecodeStr(outparameters.getValue("message"));
   var isHaveOuter=outparameters.getValue("isHaveOuter");
   if(message!='0'&&message!='1')
   {
      alert(message);
      return;
   }
   if(message=='1')
   {
     var alertmessage=getDecodeStr(outparameters.getValue("alertmessage"));
     alert(alertmessage);
     return;
   }
   if(message=='0')
   {
     var moreLevelSP=outparameters.getValue("moreLevelSP");
     var title="";
     var content="";
     var objecttype="";
     if(moreLevelSP=='1')
     {
    	 var xx;
         var spRelation=document.getElementById("spr").value;
         var spcount=document.getElementById("spcount").value;
         var zpappfalg=document.getElementById("zpappfalg").value;
        if(spRelation!=null&&spRelation.length>0&&zpappfalg=="true" ){
            if(spcount<1){
                alert(HIRE_ERROR_SPCOUNT);
                return;
            }
            
            if(spcount>1){	
            	var thecodeurl="/hire/demandPlan/positionDemand/positionDemandTree.do?b_searchearrp=link`spRelation="+spRelation+"`spcount="+spcount; 
        		var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
        		xx=window.showModalDialog(iframe_url,null, 
        		        "dialogWidth:350px; dialogHeight:240px;resizable:yes;center:yes;scroll:no;status:no");
            }else{
            	if(!confirm(HIRE_APP_OK))
                    return;
                
            	var hashVo=new ParameterSet();
                hashVo.setValue("spRelation",spRelation);
                hashVo.setValue("spcount",spcount);
                var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:spselect,functionId:'3000000271'},hashVo);
                function spselect(outparameters){
                    var obj = new Object();
                    if(outparameters==null)
                        return;
                    obj.title=outparameters.getValue("title");
                    obj.content=outparameters.getValue("content");
                    obj.objecttype=outparameters.getValue("actortype");
                    xx=obj;
                }	
            }
        } else{
             var thecodeurl="/hire/demandPlan/positionDemand/positionDemandTree.do?b_moresp=search`intype=1"; 
			 var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
			 var winFeature="dialogWidth:410px; dialogHeight:240px;resizable:no;center:yes;scroll:no;status:no";
			 if (isIE6())
			   winFeature="dialogWidth:420px; dialogHeight:250px;resizable:no;center:yes;scroll:no;status:no";
		     xx=window.showModalDialog(iframe_url,null, winFeature);		   
        }
        
        if(xx==null)
        {
           return;
        }
        if(xx.content==null||xx.content=='')
        {
           alert("请选择报批人员!");
           return;
        }
        title=xx.title;
        content=xx.content;
        objecttype=xx.objecttype;
     }
     var z0301=outparameters.getValue("z0301");
     var model=outparameters.getValue("model");
     var hashVo=new ParameterSet();
     hashVo.setValue("title",title);
     hashVo.setValue("content",content);
     hashVo.setValue("moreLevelSP",moreLevelSP);
     hashVo.setValue("isSendMessage",'${positionDemandForm.isSendMessage}');
     hashVo.setValue("z0301",z0301);
     hashVo.setValue("type",objecttype);
     hashVo.setValue("model","1");
     hashVo.setValue("url_p",document.getElementById("hostname").href);
     
     var In_parameters="opt=1";
     var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:refreshPage,functionId:'3000000151'},hashVo);		
   }
}
function refreshWEB(outparameters)
{
  queryBySp();
}
/*报批 异步发送邮件 asynchronous:true*/
function refreshPage(outparameters)
{	 var title=outparameters.getValue("title");
     var content=outparameters.getValue("content");
     var moreLevelSP=outparameters.getValue("moreLevelSP");
     var z0301=outparameters.getValue("z0301");
     var type=outparameters.getValue("type");
 
     var hashVo=new ParameterSet();
     hashVo.setValue("title",title);
     hashVo.setValue("content",content);
     hashVo.setValue("moreLevelSP",moreLevelSP);
     hashVo.setValue("isSendMessage",'${positionDemandForm.isSendMessage}');
     hashVo.setValue("z0301",z0301);
     hashVo.setValue("type",type);
     hashVo.setValue("model","1");
     hashVo.setValue("url_p",document.getElementById("hostname").href);
	 var In_parameters="opt=2";
	 var request=new Request({method:'post',asynchronous:true,parameters:In_parameters,functionId:'3000000151'},hashVo);	
	 queryBySp();
}


function DetailZ03()
{
   var url="/hire/demandPlan/positionDemand/auto_logon_sp.do?b_query=query&id=0000000004";
   window.open(url,"_blank");
}
<%if (isOrgWillTableIdDefine != null
					&& isOrgWillTableIdDefine.equals("1")) {%>
function tableZ03_z0321_onRefresh(cell,value,record)
{
   if(record!=null)
   {
      cell.innerHTML="<a href=\"javascript:openCard('"+record.getString("z0321a")+"');\">"+record.getString("z0321")+"</a>";
   }
}
 <%if (showUMCard.equals("1")) {%>
function tableZ03_z0325_onRefresh(cell,value,record)
{
   if(record!=null)
   {
      cell.innerHTML="<a href=\"javascript:openCard('"+record.getString("z0325a")+"');\">"+record.getString("z0325")+"</a>";
   }
}
<%}%>
<%}%>
function openCard(z0321)
{
   var src="/hire/demandPlan/positionDemand/unit_card.do?b_query=query`z0321="+z0321;
   //window.open(src,"_blank");
   var iframe_url="/general/query/common/iframe_query.jsp?src="+src;
   var values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:"+(window.screen.width-100)+"px; dialogHeight:"+(window.screen.height-200)+"px;resizable:no;center:yes;scroll:yes;status:no");			
	
}
function tableZ03_z0311_onRefresh(cell,value,record)
{
    if(record!=null)
   {
      cell.innerHTML="<a href=\"javascript:lookGC('"+record.getString("z0301")+"');\">"+record.getString("z0311")+"</a>";
   }
}
  ///zzk 校园招聘  专业附审批流程链接
  <%if (hireMajor != null && hireMajor.length() > 0) {%>
	   function tableZ03_<%=hireMajor%>_onRefresh(cell,value,record)
		{
		    if(record!=null&&(record.getString("z0311")==null||record.getString("z0311")==""))
		   {
		      cell.innerHTML="<a href=\"javascript:lookGC('"+record.getString("z0301")+"');\">"+record.getString("<%=hireMajor%>")+"</a>";
		   }else{
		   		if(record!=null)
		   	   	  cell.innerHTML=record.getString("<%=hireMajor%>");
		   }
		}
//var r_z0301='';
//var r_hireMajor=''; 
//eval(" function tableZ03_"+hireMajor+"_onRefresh(cell,value,record){   if(record!=null&&(record.getString('z0311')==null||record.getString('z0311')=='')){r_z0301=record.getString('z0301');r_hireMajor= record.getString('"+hireMajor+"');alert(r_z0301); alert(r_hireMajor);cell.innerHTML='<a href=\"javascript:lookGC("+r_z0301+");\" >1"+r_hireMajor+"</a>';alert('dddd'+r_hireMajor);}}");

  <%}%>
function lookGC(z0301)
	{
         var thecodeurl="/hire/demandPlan/positionDemand/auto_logon_sp.do?b_look=search`z0301="+z0301; 
		 var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
		 var winFeature="dialogWidth:650px; dialogHeight:490px;resizable:yes;center:yes;scroll:no;status:no";
		 if (isIE6())
			 winFeature="dialogWidth:660px; dialogHeight:490px;resizable:yes;center:yes;scroll:no;status:no"
		 var values= window.showModalDialog(iframe_url,null,winFeature);			   
  	    
	}
	function groupCount()
	{
	     var thecodeurl="/hire/demandPlan/positionDemand/select_group_field.do?b_query=search"; 
		 var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
		 var values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:660px; dialogHeight:560px;resizable:yes;center:yes;scroll:yes;status:no");		
		 //window.open(thecodeurl,'_blank');
	}
	function zpNeedReport(){
		var thecodeurl="/hire/demandPlan/positionDemand/getNeedFields.do?b_query=search"; 
		 var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
		 var values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:660px; dialogHeight:590px;resizable:no;center:yes;scroll:yes;status:no");
	}
	
  
function document._document_oncontextmenu() 
{
var e = window.event;

return false;   
}
function configPost()
{
   var thecodeurl="/hire/demandPlan/positionDemand/post_field_config.do?b_init=search"; 
   var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
   var values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:700px; dialogHeight:430px;resizable:yes;center:yes;scroll:yes;status:no");
  //window.open(thecodeurl,"_blank");
}	  

	function importDemand(){
		///var thecodeurl="/hire/demandPlan/positionDemand/positionDemandTree.do?br_importdemand=link"; 
		///var objlist= window.showModalDialog(thecodeurl, arguments, 
    	///    "dialogWidth:440px; dialogHeight:220px;resizable:no;center:yes;scroll:yes;status:yes");
    	window.location="/hire/demandPlan/positionDemand/positionDemandTree.do?br_importdemand=link&flag=1";
	}
   function exportDemandExcel(){
		var hashvo=new ParameterSet();
		var sql="${positionDemandForm.sql}";
		hashvo.setValue("sql",sql);
		var In_paramters=""; 
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:exportDemandZip,functionId:'3000000241'},hashvo);			

    }
    function exportDemandZip(outparamters){
      	var infor=outparamters.getValue("infor");
     	var name=outparamters.getValue("name");
  	  	if(infor=="error"){
			alert("导出失败!");
			return;
		}
		if(infor=="none"){
			alert("无导出数据!");
			return;
		}
     	var hashvo=new ParameterSet();
     	hashvo.setValue("name",name);
     	var request=new Request({method:'post',asynchronous:false,onSuccess:exportDemandOk,functionId:'3000000242'},hashvo);	
    }
  function exportDemandOk(outparamters){
  	var infor=outparamters.getValue("infor");
  	var name=outparamters.getValue("name");
  	if(infor=="ok"){
  		var win=open("/servlet/DisplayOleContent?filename="+name,"zip");
  	}
  	if(infor=="error"){
  		alert("导出失败!");
  	}
  	if(infor=="none"){
  		alert("无导出数据!");
  	}
  }
</script>
<%
String bosflag= userView.getBosflag();//得到系统的版本号
if(bosflag!=null&&bosflag.equalsIgnoreCase("hcm")){
%>
    <link href="/hire/css/layout.css" rel="stylesheet" type="text/css">
<%
}
%>
<body>
<html:form action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_query=1"  >
<table class="setmp" ><tr><td>

<table class="left1table"><tr><td>
<hrms:menubar menu="menu1" id="menubar1">
	<hrms:menuitem name="file" label="menu.file" >						
						<hrms:menuitem name="mitem6" label="button.cardpdf" icon="/images/print.gif" url="executeOutFile(1)"  />
						<hrms:menuitem name="mitem6" label="button.createescel" icon="/images/print.gif" url="executeOutFile(2)"  />
						<hrms:menuitem name="mitem6" label="导出" icon="/images/export.gif" url="exportDemandExcel()"  function_id="310119" />
						<hrms:menuitem name="mitem6" label="导入" icon="/images/import.gif" url="importDemand()"  function_id="310118" />
						<hrms:menuitem name="mitem6" label="kq.report.pagesetup" icon="/images/prop_ps.gif" url="setPageFormat()"  />
						<%-- <hrms:menuitem name="mitem8" label="分组汇总" icon="/images/export.gif" url="groupCount();"  /> --%>
						<hrms:menuitem name="mitem9" label="招聘需求汇总表" icon="/images/export.gif" url="zpNeedReport();"  />
						<hrms:menuitem name="mitem10" label="设置引入岗位信息" icon="/images/sys_config.gif" url="configPost();" function_id="310117,0A036"/> 
						
	</hrms:menuitem>
	<hrms:menuitem name="file" label="edit_report.status.bj" >						
						<hrms:menuitem name="mitem6" label="infor.menu.query" icon="/images/quick_query.gif" url="query()"  />
						<hrms:menuitem name="mitem6" label="label.zp_exam.sort" icon="/images/sort.gif" url="taxis()"  />								
						<hrms:menuitem name="mitem6" label="lable.zp_plan.short_pos" icon="/images/sb.gif" url="SparePositionQuery()"  function_id="310113,0A034"  />					
	</hrms:menuitem>
	

</hrms:menubar>
</td></tr></table>
</td>
<td>
&nbsp;&nbsp;
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>

	     </td>
<td>
<table><tr>
<td nowrap align="left" valign="middle">

<bean:message key="hire.query.spflag"/></td><td>
   <%
   	ArrayList splist = positionDemandForm.getSplist();
   		String sp_flag = positionDemandForm.getSp_flag();
   		out.println("<select name='sp_flag' onchange='queryBySp();'>");
   		for (int i = 0; i < splist.size(); i++) {
   			CommonData com = (CommonData) splist.get(i);
   			if (sp_flag.equalsIgnoreCase(com.getDataValue())) {
   				out.println("<option value='" + com.getDataValue()
   						+ "' selected>");
   			} else {
   				out.println("<option value='" + com.getDataValue()
   						+ "'>");
   			}
   			out.println(com.getDataName());
   			out.println("</option>");
   		}
   		out.println("</select>");
   %> 
 </td></tr></table>

</td>
</tr>
</table>

  <bean:define id="appname" name="positionDemandForm" property="appname"/>
  <bean:define id="actortype" name="positionDemandForm" property="actortype"/>
  <bean:define name="positionDemandForm" property="zpappfalg" id="zpappfalg"/>
  <hrms:dataset name="positionDemandForm" property="fieldlist" scope="session" setname="${positionDemandForm.tablename}"  setalias="position_set" readonly="false" rowlock="true"  rowlockfield="z0319"  rowlockvalues=",01,07," editable="true" select="true" sql="${positionDemandForm.sql}" buttons="bottom"  pagerows="${positionDemandForm.pagerows}">
	     
	     <hrms:commandbutton name="add" onclick="addPosition()">
		    <bean:message key="hire.new.requirement"/>
		   </hrms:commandbutton> 
	     
	     
	     <hrms:commandbutton name="save" functionId="3000000102"  hint="hire.confirm.save"   function_id="310111,0A031"   refresh="true" type="all" setname="${positionDemandForm.tablename}"  >
	          <bean:message key="button.save"/>
	     </hrms:commandbutton>  
	     
	    <hrms:commandbutton name="delselected" onclick="delPosition()"  function_id="310112,0A032"  >
		    <bean:message key="button.delete"/>
		   </hrms:commandbutton>
		   <%
		   	if (spRelation != null && spRelation.length() > 0 && "true".equalsIgnoreCase(zpappfalg.toString()) && Integer.parseInt(spcount) > 0) {
		   			if ("1".equalsIgnoreCase(spcount)) {
		   %>
			     <hrms:commandbutton name="apply" onclick="reportSP();" function_id="310114,0A033"   refresh="true" type="selected" setname="${positionDemandForm.tablename}"  >
		     	 <bean:message key="button.appeal"/>[${appname}]
		         </hrms:commandbutton>  <!-- 报批 -->
		  <%
		  			} else {
		  %>
			  	<hrms:commandbutton name="apply" onclick="reportSP();" function_id="310114,0A033"   refresh="true" type="selected" setname="${positionDemandForm.tablename}"  >
		     	<bean:message key="button.appeal"/>
		        </hrms:commandbutton>  <!-- 报批 -->
		  <%
		  			}
		  	} else {
		  %>
		      <hrms:commandbutton name="apply" onclick="reportSP();" function_id="310114,0A033"   refresh="true" type="selected" setname="${positionDemandForm.tablename}"  >
		     	<bean:message key="button.appeal"/>
		      </hrms:commandbutton> 
	     <%
 	     	}
 	     %>		 
	      <%
		 	      	if (userView.getBosflag() != null&& (userView.getBosflag().equalsIgnoreCase("hl")||userView.getBosflag().equalsIgnoreCase("hcm"))&& returnflag.equalsIgnoreCase("dxt")) {
		 	      %>
   <hrms:commandbutton name="returnHome" hint="" functionId="" refresh="true" type="selected" setname="${positionDemandForm.tablename}" onclick="hrbreturn('retain','il_body','positionDemandForm');">
     <bean:message key="button.return"/>
   </hrms:commandbutton>
   <%
   	}
   %>       
	</hrms:dataset>
	
<input type='hidden' name='extendSql' value=" " />
<input type='hidden' name='orderSql'  value=" " />
<input type='hidden' name='sp' value="" />
<input type='hidden' name='isSendMessage' value="${positionDemandForm.isSendMessage}" />
<html:hidden name="positionDemandForm" property="codeSetId"/>
<html:hidden name="positionDemandForm" property="codeItemId"/>
<html:hidden name="positionDemandForm" property="spRelation"  styleId="spr"/>
<html:hidden name="positionDemandForm" property="spcount"  styleId="spcount"/>
<html:hidden name="positionDemandForm" property="zpappfalg" styleId="zpappfalg"/>
</html:form>
</body>

