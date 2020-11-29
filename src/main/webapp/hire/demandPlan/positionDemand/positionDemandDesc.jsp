<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<%@ page import="java.util.*,
				com.hjsj.hrms.actionform.hire.demandPlan.PositionDemandForm,
				org.apache.commons.beanutils.LazyDynaBean,
				com.hrms.struts.valueobject.UserView,
				com.hrms.struts.constant.WebConstant" %>
<%@ page import="com.hrms.hjsj.sys.ResourceFactory"%>
				
				
<%
    PositionDemandForm positionDemandForm=(PositionDemandForm)session.getAttribute("positionDemandForm");
    String schoolPosition=positionDemandForm.getSchoolPosition();  //待岗职位
	ArrayList positionDemandDescList=positionDemandForm.getPositionDemandDescList();
	ArrayList posConditionList=positionDemandForm.getPosConditionList();
    String operateType=(String)positionDemandForm.getOperateType();
    String isOrgWillTableIdDefine=positionDemandForm.getIsOrgWillTableIdDefine();
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hireMajor=positionDemandForm.getHireMajor();
	String unitid="";
	String hireMajorCode = positionDemandForm.getHireMajorCode();
	if(!userView.isSuper_admin())
	{
	String tmp="";
		tmp=userView.getUnitIdByBusi("7");
		tmp = PubFunc.getTopOrgDept(tmp);
				unitid = tmp.replaceAll(",","`");
				if(tmp.trim().length()==3)
				{
				   if(unitid.equals("UN`"))
			       unitid="ALL";
			       else
			       unitid="-1";
				}
				else if(unitid.indexOf("`")==-1&&unitid.trim().length()>2){
					unitid=unitid.substring(2);
				}
		//if(userView.getStatus()==0)
		//{
			//if(userView.getUnit_id().length()>0)
			//{
			//	unitid=userView.getUnit_id(); //.substring(2);
			//	if(unitid.trim().length()==3)
			//	{
			//	   unitid="";
			//	}
			//	else if(unitid.indexOf("`")==-1&&unitid.trim().length()>0)
			//		unitid=unitid.substring(2);
		//	}
		//	else
		//		unitid="-1";
		//}
		//else
		//{	
		   // String codevalue=userView.getManagePrivCodeValue();
			//String codeset=userView.getManagePrivCode();
		   // unitid=userView.getManagePrivCodeValue();
		   // if(unitid==null||unitid.trim().length()==0)
			//{
						//String userDeptId=userView.getUserDeptId();
						//String userOrgId=userView.getUserOrgId();
					//	if(userDeptId!=null&&userDeptId.trim().length()>0)
					//	{
					//				unitid=userDeptId;
					//				//codeset="UM";
					//	}
					//	else if(userOrgId!=null&&userOrgId.trim().length()>0)
					//	{
					//				unitid=userOrgId;
									//codeset="UN";
					//	}
					
			//}
			//if((codeset==null||codeset.trim().length()==0)&&(codevalue==null||codevalue.trim().length()==0))
			//{
			//   codevalue="-1";
			//}
			//else if(codeset.length()!=0&&(codevalue==null||codevalue.trim().length()==0))
			//		codevalue="";
		    //unitid=	codevalue;		
		//}
	}
	
%>
<style type="text/css">
.RecordRow {
	border: inset 1px #94B6E6;
	BACKGROUND-COLOR: #FFFFFF;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;

}

.RecordRow_top {
	border: inset 1px #94B6E6;
	BACKGROUND-COLOR: #FFFFFF;
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;

}
</style>
<script language='javascript' >
var schoolPosition='<%=schoolPosition%>';
var hireMajor='<%=hireMajor%>';
//当设为校园招聘时，隐藏岗位选项，并自动赋值
function hiddenPos(obj)
{
	if(obj.value=='01')
	{
		if(schoolPosition.length==0)
		{
			alert("请先到参数设置-配置参数中为校园招聘岗位指定待岗岗位！");
			obj.options[0].selected=true;
			return;
		}
		if(hireMajor==null||hireMajor.length==0){
			alert("请先到参数设置-配置参数中指定招聘专业!");
			obj.options[0].selected=true;
			return;
		}
		var _obj=document.getElementsByName("positionDemandDescList[2].value");
		_obj[0].options[0].value=schoolPosition;
   		_obj[0].options[0].selected=true;   	
	 	document.getElementById("pos_tr").style.display="none";
	 	var aobj=document.getElementById("hireM");
	 	if(aobj)
	 	{
	 	   aobj.innerHTML="*";
	 	}
	 	var objj=document.getElementById("posf");
	 	objj.innerHTML="&nbsp&nbsp专业描述";
	 	}
	    else if(obj.value=='04'){
	      var _obj=document.getElementsByName("positionDemandDescList[2].value");
		  _obj[0].options[0].value=''; 
		  var a_obj=document.getElementById("aup");
		  a_obj.innerHTML="";
		  document.getElementById("pos_tr").style.display="block";
		  var aobj=document.getElementById("hireM");
	 	if(aobj)
	 	{
	 	   aobj.innerHTML="*";
	 	}
	 	var objj=document.getElementById("posf");
	 	objj.innerHTML="&nbsp&nbsp岗位描述";
	}
	else
	{
	    var _obj=document.getElementsByName("positionDemandDescList[2].value");
		_obj[0].options[0].value=''; 
		var a_obj=document.getElementById("aup");
		a_obj.innerHTML="";
		document.getElementById("pos_tr").style.display="block";
		var aobj=document.getElementById("hireM");
	 	if(aobj)
	 	{
	 	   aobj.innerHTML="&nbsp;";
	 	}
	 	var objj=document.getElementById("posf");
	 	objj.innerHTML="&nbsp&nbsp岗位描述";
    }
}


function isValidDate(day, month, year) {
    if (month < 1 || month > 12) {
            return false;
        }
        if (day < 1 || day > 31) {
            return false;
        }
        if ((month == 4 || month == 6 || month == 9 || month == 11) &&
            (day == 31)) {
            return false;
        }
        if (month == 2) {
            var leap = (year % 4 == 0 &&
                       (year % 100 != 0 || year % 400 == 0));
            if (day>29 || (day == 29 && !leap)) {
                return false;
            }
        }
        return true;
    }
function openInputCodeDialog_self(codeid,mytarget) 
{
	
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
   
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name.replace(".viewvalue",".value");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    codevalue='<%=(unitid)%>'
   var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,1,0);
   thecodeurl="/system/codeselectposinputpos.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=1";
   var winFeature = "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no";
   if (isIE6())
	   winFeature = "dialogWidth:310px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no";
    var popwin= window.showModalDialog(thecodeurl, theArr,winFeature);
	var aa=document.getElementsByName("positionDemandDescList[1].viewvalue");   //author:dengcan
	aa[0].fireEvent("onChange");
	if(document.getElementsByName("positionDemandDescList[0].value")[0].value=='01')
	{
	   var _obj=document.getElementsByName("positionDemandDescList[2].value");
		_obj[0].options[0].value=schoolPosition;
   		_obj[0].options[0].selected=true; 
   		if(document.documentMode == 6){//xucs不知道为何改成下面的那个样子了 但是在IE8、9中这样的style会导致selelct选择框不显示
   		   document.getElementById("posup").style.visibility = "hidden";///使IE6 需求岗位下拉选择框不显示
   		}
   		  //document.getElementById("posup").style.visibility = "hidden";///使IE6 需求岗位下拉选择框不显示
	}else{
	      var _obj=document.getElementsByName("positionDemandDescList[2].value");
		_obj[0].options[0].value="";  
	}
		
}

/*************由需求部门 改变需求岗位******************/
function setPosList(isOrgWillTableIdDefine)
{
	var aa=document.getElementsByName("positionDemandDescList[1].value");   //author:dengcan
	if(aa)
	{
	   if(aa[0].value=='')
	     return;
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("operator","1");
	hashvo.setValue("isOrgWillTableIdDefine",isOrgWillTableIdDefine);
	var In_paramters="orgID="+aa[0].value;  
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000174'},hashvo);////取得组织下职位
}

function returnInfo(outparamters)
{
	var poslist=outparamters.getValue("poslist");
	var isOrgWillTableIdDefine=outparamters.getValue("isOrgWillTableIdDefine");
	var orgID = outparamters.getValue("orgID");
	var unCode=outparamters.getValue("unCode");
	var showUM=outparamters.getValue("showUM");
	if(isOrgWillTableIdDefine=='1')
	{
	   document.getElementById("oUNid").value=unCode;
	   document.getElementById("oUMid").value=orgID;
	   document.getElementById("showButton").style.display="block";
	   if(showUM=='1')
	   {
	        document.getElementById("showUMButton").style.display="block";
	   }
	}
	var aa=document.getElementsByName("positionDemandDescList[2].value")   //author:dengcan
	AjaxBind.bind(aa[0],poslist);
}


function up_file(e01a1)
{
    var obj=document.getElementById("posup");
    if(obj)
    {
       var ids="";
       for(var i=0;i<obj.options.length;i++)
       {
          if(obj.options[i].selected)
          {
             ids=obj.options[i].value;
             break;
          }
       }
       thecodeurl="/hire/demandPlan/positionDemand/positionDemandTree.do?b_initUpFile=up`opt=s`posID="+ids; 
       var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
       var popwin= window.showModalDialog(iframe_url, ids, 
         "dialogWidth:420px; dialogHeight:180px;resizable:no;center:yes;scroll:yes;status:no");
   }
	
}


function validatePos(obj)
{
	
	var a=document.getElementsByName("positionDemandDescList[2].value")  //author:dengcan
	var aa=document.getElementsByName("positionDemandDescList[1].value") //author:dengcan
   <%if(userView.hasTheFunction("310116")){%>
   var objvalue="";
    for(var i=0;i<obj.options.length;i++)
    {
       if(obj.options[i].selected)
       {
           objvalue=obj.options[i].value;
           break;
       }
    }
	if(trim(objvalue).length!=0)
	{
		var a_obj=document.getElementById("aup");
		a_obj.innerHTML=" <input type='button' value='"+UPLOAD_POSITION_EXPLANATION+"'  class='mybutton' onclick='up_file(\""+obj.value+"\")' > ";
	}
	else
	{
		var a_obj=document.getElementById("aup");
		a_obj.innerHTML="";
	}
	<%}%>
	if(a[0].value=='-1'&&aa[0].value!=""&&aa[0].value!="ot")
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("operator","2");
		var In_paramters="orgID="+aa[0].value;  
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'3000000174'},hashvo);
	}
}


function returnInfo2(outparamters)
{
	
	var allowLength=outparamters.getValue("allowLength");	
	var existItemid=outparamters.getValue("existItemid");
	var orgID=outparamters.getValue("orgID");
	var newValue=outparamters.getValue("newValue");
	var theArr=new Array(allowLength,existItemid,orgID,newValue); 
    thecodeurl="addPos.jsp"; 
    var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogWidth:430px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
    if(popwin)
    {
    	var hashvo=new ParameterSet();
		hashvo.setValue("posID",popwin[0]);
		hashvo.setValue("posName",popwin[1]);
		var In_paramters="orgID="+orgID;  

		var aa=document.getElementsByName("positionDemandDescList[2].value")  //author:dengcan
		var a_Option=aa[0].options[aa[0].options.length-1];
		 aa[0].options[aa[0].options.length-1]=new Option(popwin[1],orgID+popwin[0]);		 
		 aa[0].options[aa[0].options.length]=a_Option;
		 aa[0].options[aa[0].options.length-2].selected=true;
		 var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo3,functionId:'3000000175'},hashvo);

    }
}


function returnInfo3(outparamters)
{

}
function sub()
{
	 	<% int m=0;  %>
		<logic:iterate  id="element"    name="positionDemandForm"  property="positionDemandDescList" indexId="index"> 
				<logic:equal name="element" property="itemtype" value="N">
					var a<%=m%>=document.getElementsByName("positionDemandDescList[<%=m%>].value")
					if(a<%=m%>[0].value!='')
					{
					 var myReg =/^(-?\d+)(\.\d+)?$/
					 if(!myReg.test(a<%=m%>[0].value)||a<%=m%>[0].value<=0) 
					 {
						alert("<bean:write  name="element" property="itemdesc"/>"+OBJECTCARDINFO7+"！");
						return;
					 }
					 }
				</logic:equal>
				
				
				
				<logic:equal name="element" property="itemtype" value="D">
					var a<%=m%>=document.getElementsByName("positionDemandDescList[<%=m%>].value")
					if(a<%=m%>[0].value!='')
					{
				    	 if(!checkDateTime(a<%=m%>[0].value)) 
					     {
				    		alert("<bean:write  name="element" property="itemdesc"/>输入不正确,请输入有效的日期且格式为yyyy-mm-dd！");
				    		return;
				    	 }
				    	  var year=a<%=m%>[0].value.substring(0,4);
						var month=a<%=m%>[0].value.substring(5,7);
						var day=a<%=m%>[0].value.substring(8,10);
						if(!isValidDate(day, month, year))
						{
						   alert("<bean:write  name="element" property="itemdesc"/> 输入值不正确！");
				    		return;
						}
					 
					 }
				</logic:equal>
				
				<logic:equal name="element" property="itemtype" value="A">
				<logic:equal name="element" property="codesetid" value="0">
					var a<%=m%>=document.getElementsByName("positionDemandDescList[<%=m%>].value")
					if(a<%=m%>[0].value!='')
					{
						if(IsOverStrLength(a<%=m%>[0].value,<bean:write  name="element" property="length"/>))
						{
							alert("<bean:write  name="element" property="itemdesc"/>"+OVER_LENGTH_SCOPE+"!");
							return;
						}
					}
				</logic:equal>
				</logic:equal>
			<% m++; %>
		</logic:iterate>
	
	<% m=0; %>
	var z0329="";
	var z0331="";
	var z0311='';
	var school=document.getElementsByName("positionDemandDescList[<%=0%>].value")[0].value;
	<logic:iterate  id="element"    name="positionDemandForm"  property="positionDemandDescList" indexId="index"> 
		var item<%=m%>='<bean:write  name="element" property="mustfill"/>';
		var itemid<%=m%>='<bean:write  name="element" property="itemid"/>';
		var aa<%=m%>=document.getElementsByName("positionDemandDescList[<%=m%>].value")
		if(itemid<%=m%>.toUpperCase()=='Z0329')
		{
		   z0329=aa<%=m%>[0].value;
		}
		if(itemid<%=m%>.toUpperCase()=='Z0331')
		{
		   z0331=aa<%=m%>[0].value;
		}
		if(itemid<%=m%>.toUpperCase()=='POSID')
		{
		   z0311=aa<%=m%>[0].value;
		}
		if(z0311==null||z0311=='-1'){
			alert("请选择正确的岗位！");
			return;						
		}
		if(item<%=m%>=='1'&&itemid<%=m%>.toUpperCase()!=hireMajor.toUpperCase())
		{
		          		
						if((aa<%=m%>[0].value==""||aa<%=m%>[0].value=='ot')&&itemid<%=m%>!="z0307"&&itemid<%=m%>!="z0309") ///&&itemid<%=m%>!="z0329"&&itemid<%=m%>!="z0331"
						{
							alert("<bean:write  name="element" property="itemdesc"/>"+THIS_IS_MUST_FILL+"！");
							return;						
						}
						
		}
		else
		{
		  if((school=='01'||school=='04')&&itemid<%=m%>.toUpperCase()==hireMajor.toUpperCase())
		  {
		      if((aa<%=m%>[0].value==""))
						{
							alert("<bean:write  name="element" property="itemdesc"/>"+THIS_IS_MUST_FILL+"！");
							return;						
						}
		  }
		}
		
		<% m++; %>
	</logic:iterate>
	//dateJHRQ=new Date(parseInt(arrJHRQ[0]),parseInt(arrJHRQ[1])-1,parseInt(arrJHRQ[2]),0,0,0); //新建日期对象
	if(trim(z0329)!=''&&trim(z0331)!='')
	{
	   var xx=z0329.split("-");
	   var yy=z0331.split("-");
	   var xxD=new Date(xx[0]*1,xx[1]*1,xx[2]*1);
	   var yyD=new Date(yy[0]*1,yy[1]*1,yy[2]*1);
	  // alert("xx="+z0329+"---yy="+z0331);
	   //alert(xx[0]+"--"+xx[1]+"--"+xx[2]+"\r\n"+yy[0]+"--"+yy[1]+"--"+yy[2]);
	   //alert(parseInt(xx[0])+"--"+parseInt(xx[1])+"--"+xx[2]*1+"\r\n"+parseInt(yy[0])+"--"+parseInt(yy[1])+"--"+yy[2]*1);
	  // alert(parseInt(xx[0])+"--"+xxD.getMonth()+"--"+xxD.getDate()+"\r\n"+parseInt(yy[0])+"--"+yyD.getMonth()+"--"+yyD.getDate());
	   if(xx[0]*1>yy[0]*1||(xx[0]*1==yy[0]*1&&xx[1]*1>yy[1]*1)||(xx[0]*1==yy[0]*1&&xx[1]*1==yy[1]*1&&xx[2]*1>yy[2]*1))
	   {
	     alert("有效起始日期不能大于有效结束日期！");
	     return;
	   }
	}
	
	for(var a=0;a<document.positionDemandForm.elements.length;a++)
	{
		if(document.positionDemandForm.elements[a].type=='checkbox')
		{
			if(document.positionDemandForm.elements[a].checked==false)
			{
					document.positionDemandForm.elements[a].value="0"
					document.positionDemandForm.elements[a].checked=true;
			}
			
		
		}
	}
	
	document.positionDemandForm.action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_addDemand=add&opt=0";
	document.positionDemandForm.submit();

}

function goback()
{
	history.go(-1);
}



function showTemplate()
{
	var obj=eval('a');
	if(document.positionDemandForm.isRevert.checked==true)
	{
		obj.style.display='block';
	}
	else
	{
		obj.style.display='none';
	}
}



function showRows(objName)
{
	var objs=eval(objName);
	for(var i=0;i<3;i++)
	{
		if(objs[i].style.display=='none')
		{
			objs[i].style.display='block';
			break;
		}	
	}

}

function delRows(objName)
{
	
	var objs=eval(objName);	
	for(var i=2;i>=0;i--)
	{
		if(objs[i].style.display=='block')
		{
			objs[i].style.display='none';
			var a_td=objs[i].cells[1];
			 var   oinput =a_td.getElementsByTagName("input");
			 for(var j=0;j<oinput.length;j++)
			 {
			 	oinput[j].value="";
			 }
			 var   oselect =a_td.getElementsByTagName("select");
			 for(var j=0;j<oselect.length;j++)
			 {
			 	oselect[j].value="";
			 }
			 
			break;
		}	
	}

}
function openCard(type)
{
   var z0321="";
   if(type=='un')
   {
      z0321=document.getElementById("oUNid").value;
   }
   else
   {
      z0321=document.getElementById("oUMid").value;
   }
   if(z0321==''||z0321=='ot')
   {
      alert("请选择需求部门!");
      return;
   }
   var src="/hire/demandPlan/positionDemand/unit_card.do?b_query=query`new=1`z0321="+z0321;
   //window.open(src,"_blank");
   var iframe_url="/general/query/common/iframe_query.jsp?src="+src;
   var values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:"+(window.screen.width-100)+"px; dialogHeight:"+(window.screen.height-200)+"px;resizable:no;center:yes;scroll:yes;status:no");			
	
}
function setTPinput(){
    var InputObject=document.getElementsByTagName("input");
    for(var i=0;i<InputObject.length;i++){
        var InputType=InputObject[i].getAttribute("type");
        if(InputType!=null&&(InputType=="text"||InputType=="password")){
            InputObject[i].className=" "+"TEXT4";
        }
    }
}
</script>
<hrms:themes></hrms:themes>
<html>
  <head>
  </head>
  
  <body onload="setTPinput()">
  <html:form action="/hire/demandPlan/positionDemand/positionDemandTree">
<%
	String bosflag= userView.getBosflag();//得到系统的版本号
	if(bosflag!=null&&bosflag.equalsIgnoreCase("hcm")){
%>
    <link href="/hire/css/layout.css" rel="stylesheet" type="text/css">
<%
}else{
%>
  <br>
<%
}
 %>
  <table width="75%" border="0" cellpadding="0" cellspacing="0" align="center" class="normalmp">
          <tr height="20">       		
       		<td width='100%'  class="TableRow_lrt">&nbsp;&nbsp;<bean:message key="hire.requirement"/>&nbsp;</td>
          </tr> 
          <tr >
          	<td class='RecordRow' > 
     <table width='90%' align='center' >
		  	<tr>
		  		<td align='right'>
		  		<%
		  		  if(bosflag!=null&&bosflag.equalsIgnoreCase("hcm")){
		  		  }else{
		  		 %>
		  		<input type='button' value="<bean:message key="button.ok"/>"  class="mybutton" onclick='sub()' >
				&nbsp;
				<input type='button' value="<bean:message key="button.return"/>"  onclick='goback()' class="mybutton" >
				<%
				}
				%>  
				</td>
		  	</tr>
    </table>
          
          <div id="posf">
          	 <bean:message key="hire.position.desc"/>
          </div>
          
          <table width='97%' align='center' cellpadding="0" cellspacing="0"   ><tr><td class='RecordRow_top common_border_color' >
         &nbsp;
          </td></tr></table>
          <table border=0 width='90%' align='center' >
          <%
            for(int i=0;i<positionDemandDescList.size();i++)
            {
           
                LazyDynaBean abean=(LazyDynaBean)positionDemandDescList.get(i);
                 String itemid=(String)abean.get("itemid");
				 String itemtype=(String)abean.get("itemtype");
				 String codesetid=(String)abean.get("codesetid");
				 String desc="";
				 if(abean.get("desc")!=null)
					 desc=(String)abean.get("desc");
				 String isMore=(String)abean.get("isMore");
				 String itemdesc=(String)abean.get("itemdesc");
				 String value=(String)abean.get("value");
				 String viewvalue=(String)abean.get("viewvalue");	
				 String mustfill=(String)abean.get("mustfill");
				 if(i==2)
					 out.print("<tr id='pos_tr' ><td width='15%' ");
				 else
				 {
	          		 out.print("<tr><td width='15%' ");
	          	}
          		  if(itemtype.equals("M"))
				 	out.print(" valign='top' ");
          		 
          		 if(bosflag!=null&&!bosflag.equals("hcm")){
          		    out.print(" align='left' >");
          		 }else{
          		    out.print(" align='right' >");
          		 }
				 out.print(itemdesc+"&nbsp;&nbsp;");
				 out.print("</td><td  width='85%'  align='left' nowrap>");
          		 if(itemtype.equals("A"))
				 {
								if(codesetid.equals("0"))
								{
									if(itemid.equalsIgnoreCase(hireMajor) && !(hireMajorCode==null || hireMajorCode.equals(""))){
										out.println("<input type=\"hidden\"  ");	
										out.print(" name=\"positionDemandDescList["+i+"].value\"  size='30'   value=\""+value+"\" ");
									}else{
										out.println("<input type=\"text\"  ");	
										out.print(" name=\"positionDemandDescList["+i+"].value\"  size='30'   value=\""+value+"\" ");
									}
									if(itemid.equalsIgnoreCase("z0309"))
									   out.print(" readOnly");
									if(itemid.equalsIgnoreCase(hireMajor) && !(hireMajorCode==null || hireMajorCode.equals("")))
										out.print(" />");
									else
										out.print(" />&nbsp;");
									if(itemid.equalsIgnoreCase(hireMajor) && !(hireMajorCode==null || hireMajorCode.equals(""))){
										out.println("<input type=\"text\"  ");	
										out.print(" name=\"positionDemandDescList["+i+"].viewvalue\"  readOnly size='30'   value=\"\" ");
										out.print(" />&nbsp;");
										out.print("<img  src='/images/overview_obj.gif' border=0 width=20 height=20 onclick=\"openInputCodeDialog2('"+hireMajorCode+"','positionDemandDescList["+i+"].viewvalue','0','','1');\" />");
									}
									if(mustfill.equals("1")&&!itemid.equalsIgnoreCase("z0309")&&!itemid.equalsIgnoreCase(hireMajor))
									{
										   out.print("&nbsp;<font color='red'>*</font>&nbsp;");
									}else
									{
									    if(itemid.equalsIgnoreCase(hireMajor))
									    {
									       if(mustfill.equals("1"))
									          out.print("&nbsp;<font id='hireM' color='red'>*</font>&nbsp;");
									       else
									          out.print("&nbsp;<font id='hireM' color='red'>&nbsp;</font>&nbsp;");
									    }
									}
									
								}
								else
								{
									if(isMore.equals("0"))
									{
										ArrayList options=(ArrayList)abean.get("options");
										out.print("<select  name='positionDemandDescList["+i+"].value' ");
										if(i==2)//(i==1) author:dengc   //abean1.set("mustfill","1");
											out.print(" id=\"posup\" onChange='validatePos(this)' ");
										if(i==0)  //招聘渠道	 author:dengcan
											out.print(" id=\"Z0336\" onChange='hiddenPos(this)' ");
											
										out.print(" ><option value=''></option> ");
										if(i!=2) //(i!=1)  author:dengc 
										{
											for(int n=0;n<options.size();n++)
											{
												LazyDynaBean a_bean=(LazyDynaBean)options.get(n);
												String avalue=(String)a_bean.get("value");
												String aname=(String)a_bean.get("name");
												out.println("<option value='"+avalue+"' ");
												if(avalue.equals(value))
													out.print(" selected ");
												out.print(" >"+aname+"</option>");
											}
										 }
										out.print("</select>");
										if(mustfill.equals("1")&&!itemid.equalsIgnoreCase(hireMajor))
									    {
										   out.print("&nbsp;<font color='red'>*</font>&nbsp;");
									    }else
									   {
									     if(itemid.equalsIgnoreCase(hireMajor))
									     {
									       if(mustfill.equals("1"))
									          out.print("&nbsp;<font id='hireM' color='red'>*</font>&nbsp;");
									       else
									          out.print("&nbsp;<font id='hireM' color='red'>&nbsp;</font>&nbsp;");
									     }
									   }
								    	 //out.print("</td><td>");
								    	 if(i==2)
								    	   out.print("<span id='aup'></span>");
								    	 out.print("&nbsp;"+desc+"");
									}
									else
									{
									
						              	out.print("<table  cellSpacing=0 cellPadding=0 border=0  ><tr><td>");
						              	out.print(" <input type='text' name='positionDemandDescList["+i+"].viewvalue' ");
						              	if(i==1) //(i==0)  author:dengc //&&a_item.isVisible()
						             		out.print(" readOnly onChange='setPosList(\""+isOrgWillTableIdDefine+"\")' ");
						              	out.print(" size='30' value='"+viewvalue+"'  /></td>");
						             	out.print("<td><a href='javascript:");
						             	if(i==1) //(i==0)  author:dengc 
											out.print("openInputCodeDialog_self(\""+codesetid+"\",\"positionDemandDescList["+i+"].viewvalue\");' ");
										else
							             	out.print("openInputCodeDialog(\""+codesetid+"\",\"positionDemandDescList["+i+"].viewvalue\");' ");
						             	out.print(" > &nbsp; <span> <img  src='/images/code.gif' border=0 width=20 height=20 style='position:relative;top:2px;' /></span></a>&nbsp;");
						             	if(mustfill.equals("1")&&!itemid.equalsIgnoreCase(hireMajor))
									    {
										   out.print("&nbsp;<font color='red'>*</font>&nbsp;");
									    }else
									   {
									     if(itemid.equalsIgnoreCase(hireMajor))
									     {
									       if(mustfill.equals("1"))
									          out.print("&nbsp;<font id='hireM' color='red'>*</font>&nbsp;");
									       else
									          out.print("&nbsp;<font id='hireM' color='red'>&nbsp;</font>&nbsp;");
									     }
									   }
										out.print("</td><td valign='bottom' ><input type='hidden' value='"+value+"'  name='positionDemandDescList["+i+"].value' /> &nbsp;");									
									    out.print(" </td>"); 
									    if(i==1) //(i==0)  author:dengc 
									    {
									       out.print("<td id=\"showButton\" style=\"display:none\"><a href=\"javascript:openCard('un');\">单位信息</a>");
									       out.print("</td><td id=\"showUMButton\" style=\"display:none\">&nbsp;&nbsp;<a href=\"javascript:openCard('um');\">部门信息</a></td>");									      		
									    }
									    else
									    {
									       out.print("<td id=\"showButton\" style=\"display:none\">&nbsp;&nbsp;</td>");
									       out.print("<td id=\"showUMButton\" style=\"display:none\">&nbsp;&nbsp;</td>");  
									    }
									    out.print("<td>&nbsp;&nbsp;"+desc+"</td>");
									 out.print("</tr></table>");
									}
								
								}
							
						}
						else if(itemtype.equals("D"))
						{
								out.println("<input type='text'  size='20'  name='positionDemandDescList["+i+"].value'  value='"+value+"' ");
								if(!itemid.equalsIgnoreCase("z0307"))
									out.print("  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' ");	
								else
								    out.print(" disabled ");
								out.print("  />&nbsp;");
								if(mustfill.equals("1")&&!itemid.equalsIgnoreCase("z0307"))
								{
									out.print("&nbsp;<font color='red'>*</font>&nbsp;");
								}
						}
						else if(itemtype.equals("N"))
						{
								out.println("<input type=\"text\" name=\"positionDemandDescList["+i+"].value\"   value='"+value+"'   size='20'  />&nbsp;");
						       if(mustfill.equals("1"))
								{
									out.print("&nbsp;<font color='red'>*</font>&nbsp;");
								}
						}
						else if(itemtype.equals("M"))
						{
								out.println("<textarea name=\"positionDemandDescList["+i+"].value\" rows='10'  wrap='true' cols='60' class='textboxMul'>"+value+"</textarea>&nbsp;");//wrap='OFF' 去掉是能自动换行
								if(mustfill.equals("1"))
									{
										 out.print("&nbsp;<font color='red'>*</font>&nbsp;");
									}
						}
						
						if(!(itemtype.equals("A")&&!codesetid.equals("0")))
							out.print("&nbsp;"+desc);
						out.print("</td></tr>");
          		
          
          }
          %>
          
          </table>
          <br><br>
           <bean:message key="hire.employ.request.blank"/>
          <table width='97%' align='center' cellpadding="0" cellspacing="0"   ><tr><td class='RecordRow_top common_border_color' >
         &nbsp;
          </td></tr></table>
           <table border=0 width='90%' align='center' >
           	<%
           	
           	 
           		String aa_setname="";
           		int count=1;
           		for(int j=0;j<posConditionList.size();j++)
           		{
           			boolean d=true;
           			
           			LazyDynaBean abean=(LazyDynaBean)posConditionList.get(j);
           			String setname=(String)abean.get("setname");
           			String itemtype=(String)abean.get("fieldType");
           			String codesetid=(String)abean.get("codesetid");
           			String isMore=(String)abean.get("isMore");
           			String itemdesc=(String)abean.get("itemdesc");
           			String s_value=(String)abean.get("s_value");
           			String e_value=(String)abean.get("e_value");
           			String flag=(String)abean.get("flag");
           			String type=(String)abean.get("type");
           			String view_s_value=(String)abean.get("view_s_value");
           			String view_e_value=(String)abean.get("view_e_value");
           			
           			     			
           			 boolean isFlag=false;           			
           			{
           				LazyDynaBean abean0=null;
           				String setname0="";
           				LazyDynaBean abean2=null;
           				String setname2="";
           				if((j+1)<posConditionList.size())
           				{
           					abean2=(LazyDynaBean)posConditionList.get(j+1);
           					setname2=(String)abean2.get("setname");
           					
           				}
           				if(j!=0)
           				{
           					abean0=(LazyDynaBean)posConditionList.get(j-1);
           					setname0=(String)abean0.get("setname");
           				}
           				if(abean0==null&&abean2==null)
           					isFlag=true;
           				else if((abean0==null&&abean2!=null)&&!setname2.equals(setname))
           					isFlag=true;
           				else if((abean0!=null&&abean2==null)&&!setname0.equals(setname))
           					isFlag=true;
           				else if(abean0!=null&&abean2!=null&&!setname2.equals(setname)&&!setname0.equals(setname))
           					isFlag=true;
           			}
           			
           			if(setname.equalsIgnoreCase("A01")||isFlag)
           			{
           			        if(bosflag!=null&&bosflag.equals("hcm")){
           			          out.print(" <tr><td width='15%' align='right' >"+itemdesc+"&nbsp;&nbsp;&nbsp;&nbsp;</td><td  width='85%' valign='bottom'  align='left' >");
           			        }else{
           			          out.print(" <tr><td width='15%' align='left' >"+itemdesc+"&nbsp;&nbsp;&nbsp;&nbsp;</td><td  width='85%' valign='bottom'  align='left' >");
           			        }
           					          			
	           				 if(itemtype.equals("A"))
							 {
								if(codesetid.equals("0"))
								{
									
									out.println("<input type=\"text\"  class=textbox  name=\"posConditionList["+j+"].s_value\"   value=\""+s_value+"\"  />&nbsp;");
									if(flag.equals("false"))
									{
										out.print("&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.init.tand")+"&nbsp;&nbsp;");
										out.println("<input type=\"text\"  class=textbox  name=\"posConditionList["+j+"].e_value\"   value=\""+e_value+"\"  />&nbsp;");
									}
								}
								else
								{
									
									if(isMore.equals("0"))
									{
									
										ArrayList options=(ArrayList)abean.get("options");
										out.println("<select  name='posConditionList["+j+"].s_value' ><option value=''></option> ");
										for(int n=0;n<options.size();n++)
										{
											LazyDynaBean a_bean=(LazyDynaBean)options.get(n);
											String avalue=(String)a_bean.get("value");
											String aname=(String)a_bean.get("name");
											out.println("<option value='"+avalue+"' ");
											if(avalue.equals(s_value))
												out.print(" selected ");
											out.print(" >"+aname+"</option>");
										}
										out.print("</select>&nbsp;");
										
										if(flag.equals("false"))
										{
											out.print("&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.init.tand")+"&nbsp;&nbsp;");
											out.println("<select  name='posConditionList["+j+"].e_value' ><option value=''></option> ");
											for(int n=0;n<options.size();n++)
											{
												LazyDynaBean a_bean=(LazyDynaBean)options.get(n);
												String avalue=(String)a_bean.get("value");
												String aname=(String)a_bean.get("name");
												out.println("<option value='"+avalue+"' ");
												if(avalue.equals(e_value))
													out.print(" selected ");
												out.print(" >"+aname+"</option>");
											}
											out.print("</select>&nbsp;");
										
										}
										else
										{
											    out.print("<input type='checkbox' name='posConditionList["+j+"].type'  ");
											    if(type!=null&&type.equals("1"))
											    	out.print(" checked ");
											    out.print(" value='1' />"+ResourceFactory.getProperty("hire.over"));
										
										}
										
									}
									else
									{
										
						           
						              	out.print(" <input type='text' size='15' name='posConditionList["+j+"].view_s_value'  class=textbox value='"+view_s_value+"'  />");
						             	out.print(" <span><img  src='/images/code.gif' style='position:relative;top:5px;' border=0 width=20 height=20 onclick='openInputCodeDialogS_value(\""+codesetid+"\",\"posConditionList["+j+"].view_s_value\")'  /></span>&nbsp;");		
										out.print("<input type='hidden' value='"+s_value+"'  name='posConditionList["+j+"].s_value' /> &nbsp;");
										if(flag.equals("false"))
										{
											out.print("&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.init.tand")+"&nbsp;&nbsp;");
											out.print(" <input type='text' size='15'  name='posConditionList["+j+"].view_e_value'  class=textbox value='"+view_e_value+"'  />");
							             	out.print(" <img  src='/images/overview_obj.gif' border=0 width=20 height=20 onclick='openInputCodeDialogE_value(\""+codesetid+"\",\"posConditionList["+j+"].view_e_value\")'  />&nbsp;");		
											out.print("<input type='hidden' value='"+s_value+"'  name='posConditionList["+j+"].e_value' /> &nbsp;");
											
											
									
										}
										else
										{
											    out.print("<input type='checkbox' name='posConditionList["+j+"].type'  ");
											    if(type!=null&&type.equals("1"))
											    	out.print(" checked ");
											    out.print(" value='1' /> "+ResourceFactory.getProperty("hire.over"));
										
										}
									}
								
								}
							
							}
							else if(itemtype.equals("D"))
							{
								out.println("<input type='text'  name='posConditionList["+j+"].s_value'  class=textbox value='"+s_value+"'  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' readOnly   />&nbsp;");
								if(flag.equals("false"))
								{
										out.print("&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.init.tand")+"&nbsp;&nbsp;");
										out.println("<input type='text'  name='posConditionList["+j+"].e_value'  class=textbox value='"+e_value+"'  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' readOnly   />&nbsp;");								
								}
								else
								{
									    out.print("<input type='checkbox' name='posConditionList["+j+"].type'  ");
									    if(type!=null&&type.equals("1"))
									    	out.print(" checked ");
									    out.print(" value='1' /> "+ResourceFactory.getProperty("hire.over"));
								
								}
								
							}
							else if(itemtype.equals("N"))
							{
								out.println("<input type=\"text\"  size='10'  name=\"posConditionList["+j+"].s_value\"   value='"+s_value+"'   class=textbox   />&nbsp;");
								if(flag.equals("false"))
								{
										out.print("&nbsp;&nbsp;"+ResourceFactory.getProperty("kq.init.tand")+"&nbsp;&nbsp;");
										out.println("<input type='text'  size='10'  name='posConditionList["+j+"].e_value'  class=textbox value='"+e_value+"'   />&nbsp;");								
								}
								else
								{
									    out.print("<input type='checkbox' name='posConditionList["+j+"].type'  ");
									    if(type!=null&&type.equals("1"))
									    	out.print(" checked ");
									    out.print(" value='1' /> "+ResourceFactory.getProperty("hire.over"));
								}
							
							}
							
           				out.println("</td></tr>");
           			}
           			else
           			{
           				if(aa_setname.equals(""))
           					aa_setname=setname;          			
           				int f=0;
           				String itemID="";
           				for(int e=j;e<posConditionList.size();e++)
           				{
           					
           						LazyDynaBean abean3=(LazyDynaBean)posConditionList.get(e);
			           			String setname3=(String)abean3.get("setname");
			           			String itemid3=(String)abean3.get("itemid");
			           			if(f==0)
			           			{
									aa_setname=setname3;			           			
			           				
			           			}
			           			String itemtype3=(String)abean3.get("fieldType");
			           			String codesetid3=(String)abean3.get("codesetid");
			           			String isMore3=(String)abean3.get("isMore");
			           			String itemdesc3=(String)abean3.get("itemdesc");
			           			String s_value3=(String)abean3.get("s_value");
			           			String e_value3=(String)abean3.get("e_value");
			           			String flag3=(String)abean3.get("flag");
			           			String type3=(String)abean3.get("type");
			           			String view_s_value3=(String)abean3.get("view_s_value");
			           			String view_e_value3=(String)abean3.get("view_e_value");
           						String show=(String)abean3.get("show");
           					
           			
           						if(e==j){
           						   if(bosflag!=null&&bosflag.equals("hcm")){
           						       out.print(" <tr id='"+aa_setname+"' style='display=block' ><td width='15%' align='right' >"+itemdesc+"&nbsp;&nbsp;&nbsp;&nbsp;</td><td  width='85%' valign='bottom'  align='left' >");
           						   }else{
           						       out.print(" <tr id='"+aa_setname+"' style='display=block' ><td width='15%' align='left' >"+itemdesc+"&nbsp;&nbsp;&nbsp;&nbsp;</td><td  width='85%' valign='bottom'  align='left' >");
           						   }
           						}
           				
           						if(aa_setname.equals(setname3)&&!itemID.equals(itemid3))
           						{
           							 if(itemID.equals(""))
										itemID=itemid3;
           							 if(f!=0)
	           							 out.print("&nbsp;&nbsp"+itemdesc3);
           							 if(itemtype3.equals("A"))
									 {
										if(codesetid3.equals("0"))
										{
											
											out.println("<input type=\"text\" size='10' class=textbox  name=\"posConditionList["+e+"].s_value\"   value=\""+s_value3+"\"  />&nbsp;");	
										}
										else
										{
											
											if(isMore3.equals("0"))
											{
											
												ArrayList options=(ArrayList)abean3.get("options");
												out.println("<select  name='posConditionList["+e+"].s_value' ><option value=''></option> ");
												for(int n=0;n<options.size();n++)
												{
													LazyDynaBean a_bean=(LazyDynaBean)options.get(n);
													String avalue=(String)a_bean.get("value");
													String aname=(String)a_bean.get("name");
													out.println("<option value='"+avalue+"' ");
													if(avalue.equals(s_value3))
														out.print(" selected ");
													out.print(" >"+aname+"</option>");
												}
												out.print("</select>");
												
												
												
											}
											else
											{
												
								           
								              	out.print(" <input type='text' size='10' name='posConditionList["+e+"].view_s_value'  class=textbox value='"+view_s_value3+"'  />");
								             	out.print("<span> <img  src='/images/code.gif' style='position:relative;top:5px;' border=0 width=20 height=20 onclick='openInputCodeDialogS_value(\""+codesetid3+"\",\"posConditionList["+e+"].view_s_value\")'  /></span>");		
												out.print("<input type='hidden' value='"+s_value3+"'  name='posConditionList["+e+"].s_value' /> &nbsp;");
												
											}
											out.print("<input type='checkbox' name='posConditionList["+e+"].type'  ");
									    	if(type3!=null&&type3.equals("1"))
									    		out.print(" checked ");
									    		out.print(" value='1' /> "+ResourceFactory.getProperty("hire.over"));
											}
									
									}
									else if(itemtype3.equals("D"))
									{
										out.println("<input type='text' size='10' name='posConditionList["+e+"].s_value'  class=textbox value='"+s_value3+"'  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)' readOnly   />&nbsp;");
										
									}
									else if(itemtype3.equals("N"))
									{
										out.println("<input type=\"text\"  size='10'  name=\"posConditionList["+e+"].s_value\"   value='"+s_value3+"'   class=textbox   />&nbsp;");
										
									}
									if(e==(posConditionList.size()-1))
									{
										j=e;
									
										
									}
           						}
           						else
           						{
           							j=e-1;
           						
           							break;
           						}
           						
           						
           						
           				
           						f++;
           				}
           				
           				
           				out.println("</td></tr>");
           			}
           	
           			
           		}
%>
           
			     
			          
           </table>
         
          
          <br><br>
          &nbsp;&nbsp; 自动回复邮件&nbsp;&nbsp;
          <table width='97%' align='center' cellpadding="0" cellspacing="0"   ><tr><td class='RecordRow_top common_border_color' >
         &nbsp;
          </td></tr></table>
          <table border=0 width='90%' align='center' >
          <tr>	      
          	<td width='15%' align='left' ></td>
          	<td  width='85%'  align='left' >
          		<table><tr><td>
					<html:checkbox property="isRevert" name="positionDemandForm" value="1"  onclick='showTemplate()' />  自动回复邮件给应聘者
					&nbsp;&nbsp;
					</td><td>
					<div id='a' style='display:none' >
					<hrms:optioncollection name="positionDemandForm" property="mailTemplateList" collection="list" />
					<html:select name="positionDemandForm" property="mailTemplateID" size="1"   >
						             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
					</html:select>
					</div>
				</td></tr></table>
			</td>
          </tr>
           </table>
          
          
          
          <br><br>
			</td>          
          </tr>
  </table>       
  
  
  <table width='90%' align='center' >
  	<tr>
  		<td align='center'>
  		<input type="hidden" id="oUMid" name="orgumid"/>
  		<input type="hidden" id="oUNid" name="orgunid"/>
  		<input type='button' value=' 确 定 '  class="mybutton buttonmarginLeft" onclick='sub()' >
		<input type='button' value=' 返 回 '  onclick='goback()' class="mybutton buttonmarginLeft" >  
		</td>
  	</tr>
  </table>
  
  
  </html:form>
  </body>
</html>
