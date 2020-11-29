<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hrms.struts.constant.WebConstant,
				 com.hjsj.hrms.actionform.hire.parameterSet.ParameterForm, com.hrms.struts.valueobject.UserView,
				 org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.sys.EncryptLockClient,
				 com.hrms.struts.taglib.CommonData,com.hrms.hjsj.sys.ResourceFactory" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<html>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="javascript">
	//this.status ="招聘管理 / 配置参数";
	function sub()
	{
		var value="";
		var markType="";
		var testTemplateIDs=document.getElementsByName("testTemplateIDs");
		var zhengzhengshu=/^[0-9]*[1-9][0-9]*$/;///正整数
		var zhengshu=/^[1-9]\d*|0$/;  ///正数
		var passwordMinLength=document.getElementById("passwordMinLength").value;
		var passwordMaxLength=document.getElementById("passwordMaxLength").value;
		var failedTime=document.getElementById("failedTime").value;
		var unlockTime=document.getElementById("unlockTime").value;
		if(document.getElementById("complexPassword").checked){
			if(!zhengzhengshu.test(passwordMinLength)){
				alert("密码最小长度请输入正整数!");
				return;
			}else{
				if(passwordMinLength<3){
					alert("密码最小长度不能小于3!");
					return;
				}
			}
			if(!zhengzhengshu.test(passwordMaxLength)){
				alert("密码最大长度请输入正整数!");
				return;
			}else{
			if(passwordMaxLength<=3){
					alert("密码最大长度必须大于3!");
					return;
				}
			if(passwordMaxLength>30){
				alert("密码最大长度不能大于30!");
				return;
			}
		 }		
		}
		
		if(!zhengshu.test(failedTime)){
			alert("最大登录失败次数请输入非负整数!");
			return;
		}
		if(!zhengzhengshu.test(unlockTime)){
			alert("解锁时间间隔请输入正整数!");
			return;
		}
		
	    var resumeAnalysisName=document.getElementById("resumeAnalysisName");//解析服务用户名
	    var resumeAnalysisPassword=document.getElementById("resumeAnalysisPassword");//解析服务密码
	    var resumeAnalysisForeignJob=document.getElementById("resumeAnalysisForeignJob");//对外应聘职位
	
		for(var i=0;i<testTemplateIDs.length;i++)
		{
			value+="~"+testTemplateIDs[i].value;
			var obj=document.getElementsByName("markType"+i);
			for(var j=0;j<obj.length;j++){
			if(obj[j].checked){
			markType+="#"+ obj[j].value;
			}
			}
		}
		var checkInteger = /^[1-9]\d*$/;
		 var v = parameterForm2.max_count.value;
		 if(trim(v).length>0)
		 {
		    if(!checkInteger.test(v))
		    {
		      alert(POSITION_MAX_COUNT+"！");
		      return;
		    }
		 }
		 var photo = document.getElementById("otohp");
		 var explain = document.getElementById("explain");
		 var att = document.getElementById("att");
		 var aba=document.getElementById("aba");
		 var hpbl=document.getElementById("hpbl");
		 var complexPassword=document.getElementById("complexPassword");
		 if(!aba.checked)
		 {
		    aba.value="0";
		    aba.checked=true;
		 }
		 if(!hpbl.checked)
		 {
		    hpbl.value="0";
		    hpbl.checked=true;
		 }
 		 if(!complexPassword.checked)
		 {
		    complexPassword.value="0";
		    complexPassword.checked=true;
		 }
		 
		if(document.getElementById("startResumeAnalysis")!=null&&document.getElementById("startResumeAnalysis").checked){
			if(document.getElementById("resumeAnalysisName").value==""){
				alert("简历解析服务用户名不能为空！");
				return;
			}
			if(document.getElementById("resumeAnalysisPassword").value==""){
				alert("简历解析服务密码不能为空！");
				return;
			}
		}	
		if(document.getElementById("startResumeAnalysis")!=null&&!document.getElementById("startResumeAnalysis").checked){
			document.getElementById("startResumeAnalysis").value="0";
			document.getElementById("startResumeAnalysis").checked=true;
		}
	
		 if(!photo.checked)
		 {
		      photo.value = "0";
		      photo.checked = true;
		 }
		  if(!explain.checked)
		 {
		      explain.value = "0";
		      explain.checked = true;
		 }
		 if(!att.checked)
		 {
		    att.value="0";
		    att.checked=true;
		 }
		 var gzk=document.getElementById("gzk");
		 if(!gzk.checked)
		 {
		   gzk.value="0";
		   gzk.checked=true;
		 }
		 else
		 {
		   var pssi=document.getElementById("pssi");
		   if(pssi==null||pssi.options.length==0)
		   {
		      alert("招聘需求上报要进行工资总额控制，请选择职位最高工资标准指标！");
		      return;
		   }
		 }
		 var bzk=document.getElementById("bzk");
		 if(!bzk.checked)
		 {
		   bzk.value="0";
		   bzk.checked=true;
		 }
		 var ire=document.getElementById("ire");
		 if(!ire.checked)
		 {
		   ire.value="0";
		   ire.checked=true;
		 }
		 
		  var mlsp=document.getElementById("mlsp");
		 if(!mlsp.checked)
		 {
		   mlsp.value="0";
		   mlsp.checked=true;
		 }
		 var subset=document.getElementById("subset");
		 if(subset==null||subset.options.length==0)
		 {
		   if(ire.value=='1'&&ire.checked==true)
		   {
		     alert("请选择面试过程记录子集！");
		     return;
		   }
		 }
		 var subValue = '';
		 for(var i=0;i<subset.options.length;i++)
		 {
		    if(subset.options[i].selected)
		    {
		       subValue = subset.options[i].value;
		       break;
		    }
		 }
		 if(ire.value=='1'&&ire.checked&&subValue=='')
		 {
		    alert("请选择面试过程记录子集！");
		    return;
		 }
		 if(ire.value=='1'&&ire.checked)
		 {
		    var tf=document.getElementById("tf").value;
            var cf=document.getElementById("cf").value;
            var lf=document.getElementById("lf").value;
            var cdf=document.getElementById("cdf").value;
            var cuf=document.getElementById("cuf").value;
            if(tf=''||cf==''||lf==''||cdf==''||cuf=='')
            {
               alert("请为面试过程记录子集的指标进行设置!");
               return;
            }
		 }
		 var smg=document.getElementById("smg");
		 var isctrl=document.getElementById("isCtrlReportGZ");
		 if(isctrl.checked){
			 if(!smg.checked)
			 {
			   smg.value="0";
			   smg.checked=true;
			 }else{
			   smg.value="1";
			   smg.checked=true;
			 }
		 }else{
		 	   smg.value="0";
			   smg.checked=false;
		 }
		document.parameterForm2.mark_type.value=markType.substring(1);	
		document.parameterForm2.testTemplateID.value=value.substring(1);	
		var revert_itemid=parameterForm2.interviewingRevertItemid.value;
		if(revert_itemid!='#'&&revert_itemid==parameterForm2.personTypeId.value)
		{
		   alert("\"面试回复指标\" 不能与 \"人才库标识指标\" 选择同一个指标");
		   return;
		}
		if(revert_itemid!='#'&&revert_itemid==parameterForm2.resumeStateFieldIds.value)
		{
		   alert("\"面试回复指标\" 不能与 \"简历状态指标\" 选择同一个指标");
		   return;
		}
		var formd = "yyyy-mm-dd";
		var time=document.getElementById("newTime1");
		var value1=time.value;
		var re=/^\d+$/;
		var val=trim(value1);
			if(value1.length!=0&&val.length!=0){
				if(!re.test(trim(value1))){
					alert(value1+"不是数值类型！请输入正确的数值！");
					return;
				}else{
						document.parameterForm2.newTime.value=value1;
				}
			}else{
				document.parameterForm2.newTime.value=value1;
			}
		parameterForm2.action='/hire/parameterSet/configureParameter.do?b_save=save';
		parameterForm2.submit();
	
	}
	function IsOverStrLength2(str,len)
	{
	   return str.length>len
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

	function getMusterFields(flag)
	{
		var selectFields="";
		var selectFieldNames="";
		if(flag==1)
		{
			selectFields="${parameterForm2.musterFieldIDs}";
			selectFieldNames="${parameterForm2.musterFieldNames}";
		}
		else if(flag==2)
		{
			selectFields="${parameterForm2.posQueryFieldIDs}";
			selectFieldNames="${parameterForm2.posQueryFieldNames}";
		}
		else if(flag==3)
		{
			selectFields="${parameterForm2.viewPosFieldIDs}";
			selectFieldNames="${parameterForm2.viewPosFieldNames}";
		}
		else if(flag==4)
		{
		   selectFields="${parameterForm2.pos_listfield}";
		   selectFieldNames="${parameterForm2.pos_listfieldNames}";
		}else if(flag==5)//dml 2011-6-22 10:55:21
		{
		   selectFields="${parameterForm2.posCommQueryFieldIDs}";
		   selectFieldNames="${parameterForm2.posCommQueryFieldNames}";
		}else if(flag==6){
		   //selectFields="${parameterForm2.pos_listfield_sort}";
		   var selectFieldsArray =document.getElementsByName("pos_listfield_sort");
		   selectFields=selectFieldsArray[0].value;
           selectFieldNames="${parameterForm2.pos_listfieldNames}";
		}
		var thecodeurl="/hire/parameterSet/configureParameter.do?br_search=search&flag="+flag+"&selectedFields="+selectFields+"&selectFieldNames="+selectFieldNames; 
		if(flag==6){
		   thecodeurl="/gz/sort/sorting.do?b_query=search&flag=zppostsort"+"&selectedFields="+selectFields+"&selectFieldNames="+selectFieldNames;
		}
		 var values='';
       	if(isIE6()){
       	 values= window.showModalDialog(thecodeurl,null, 
		        "dialogWidth:550px; dialogHeight:410px;resizable:no;center:yes;scroll:yes;status:no");	
       	}else{
		 values= window.showModalDialog(thecodeurl,null, 
		        "dialogWidth:540px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");	
       	}		
		if(values!=null)
		{       var tempvalue="";
                var tempid="";
				var afield;
				if(flag==1)
					afield=eval('fieldIds');
				else if(flag==2)
					afield=eval('fieldIds2');
				else if(flag==3)
					afield=eval('fieldIds3');
				else if(flag==4)
				    afield=eval('fieldIds12');
				else if(flag==5)
					afield=eval('fieldIdsCom');
				else if(flag==6){//招聘外网显示列表排序
				    afield=eval('fieldIds13');
				}
				if(flag!=6){
				    if(values[1]=='')
                        afield.innerHTML="&nbsp;";
                    else
                        afield.innerHTML=values[1];
				}else{
				      if(values=="not"){
				            afield.innerHTML="&nbsp;";
				      }else{
				            if(values.indexOf("`")!=-1){
				                values=values.substring(0,values.length-1);
				            }
				            var temparr = values.split("`");//
				            var descSort="";
				            for(var i=0;i<temparr.length;i++){
				                var tempIDS=temparr[i].split(":");//存放格式 itemid：itemdesc:[desc=0|asc=1]
				                var itemid=tempIDS[0];
				                var itemdesc=tempIDS[1];
				                var sort=tempIDS[2];
				                if(sort==1){
				                    descSort="升序";
				                    sort="ASC";
				                }else{
				                    descSort="降序";
				                    sort="DESC";
				                }
				                tempvalue=tempvalue+(itemdesc+":"+descSort+",");
				                tempid=tempid+(trim(itemid)+":"+trim(sort)+",")
				            }
				            if(tempvalue.indexOf(",")!=-1){
				                tempvalue=tempvalue.substring(0,tempvalue.length-1);
				            }
				            if(tempid.indexOf(",")!=-1){
				                tempid=tempid.substring(0,tempid.length-1);
				            }
				            afield.innerHTML=tempvalue;
				      }
				}
				
					
				if(flag==1)	
					document.parameterForm2.musterFieldIDs.value=values[0];
				else if(flag==2)
					document.parameterForm2.posQueryFieldIDs.value=values[0];
				else if(flag==3)
					document.parameterForm2.viewPosFieldIDs.value=values[0];
				else if(flag==4)
				    document.parameterForm2.pos_listfield.value=values[0];
				else if(flag==5)
				    document.parameterForm2.posCommQueryFieldIDs.value=values[0];
			    else if(flag==6){
			        document.parameterForm2.pos_listfield_sort.value=tempid;
			    }
		}	
	}

	
	function setOrgIntro()
	{
			var thecodeurl="/hire/parameterSet/configureParameter.do?b_orgIntro=inti`isVisible=1`type=1"; 
			 var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	        var values='';
       	if(isIE6()){
       	 values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:610px; dialogHeight:600px;resizable:yes;center:yes;scroll:yes;status:no");		
       	}else{
		 values=window.showModalDialog(iframe_url,null, 
		        "dialogWidth:590px; dialogHeight:580px;resizable:yes;center:yes;scroll:yes;status:no");		
       	}		
	       
	       
	        if(values ==null)
	           return;
	        
	        var returnVo= new Object();
	        returnVo.orgFieldIDsView = values.orgFieldIDsView;
	        returnVo.orgFieldIDs =values.orgFieldIDs;
	        returnVo.contentType=values.contentType;
	        returnVo.contentTypeView=values.contentTypeView;
	        var afield=eval('fieldIds4');
	       
	        afield.innerHTML=INTRODUCTION_FIELD+":"+ returnVo.orgFieldIDsView+CONTENT_FIELD+"&nbsp;&nbsp;:"+returnVo.contentTypeView;
	        document.parameterForm2.orgFieldIDs.value=returnVo.orgFieldIDs+","+returnVo.contentType;
	        
}
function getResumeFields(){
var url="/hire/parameterSet/configureParameter/getResumeFieldsList.do?b_search=search";
var returnValue='';
       	if(isIE6()){
       	 returnValue= window.showModalDialog(url,null, "dialogWidth:550px; dialogHeight:410px;resizable:no;center:yes;scroll:yes;status:no");
       	}else{
		 returnValue= window.showModalDialog(url,null, "dialogWidth:540px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
       	}
if(returnValue != null){
var ids=returnValue[0];
//var names=returnValue[1];
var afield=eval('fieldIds5');
if(returnValue[1] == '' || returnValue[1] ==null)
 afield.innerHTML="&nbsp;";
 else
 afield.innerHTML=returnValue[1];
document.parameterForm2.resumeFieldIds.value = ids;
}
}
function getResumeStaticFields(){
var url="/hire/parameterSet/configureParameter/getResumeStaticFieldsList.do?b_search=search";
var returnValue='';
       	if(isIE6()){
       	 returnValue= window.showModalDialog(url,null,"dialogWidth:550px; dialogHeight:410px;resizable:no;center:yes;scroll:yes;status:no");
       	}else{
		 returnValue= window.showModalDialog(url,null,"dialogWidth:540px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
       	}
if(returnValue != null){
var ids=returnValue[0];
//var names=returnValue[1];
var afield=eval('fieldIds6');
if(returnValue[1] == '' || returnValue[1] ==null)
 afield.innerHTML="&nbsp;";
 else
 afield.innerHTML="&nbsp;&nbsp;" + returnValue[1];
document.parameterForm2.resumeStaticIds.value = ids;
}
}
function getCommonQueryCond()
{
   var url="/hire/parameterSet/configureParameter/getCommonQueryCond.do?b_search=search&opt=0";
   var returnValue='';
       	if(isIE6()){
       	 returnValue= window.showModalDialog(url,null,"dialogWidth:550px; dialogHeight:410px;resizable:no;center:yes;scroll:yes;status:no");
       	}else{
		 returnValue= window.showModalDialog(url,null,"dialogWidth:540px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
       	}
   
   if(returnValue !=null){
   var ids = returnValue[0];
   var afield=eval('fieldIds7');
  if(returnValue[1] == '' || returnValue[1] ==null)
  afield.innerHTML="&nbsp;";
  else
  afield.innerHTML=returnValue[1];
  document.parameterForm2.commonQueryIds.value = ids;
  } 
}
function getResumeStateCode()
{
   var url="/hire/parameterSet/configureParameter/getCommonQueryCond.do?b_search=search&opt=1";
   var returnValue='';
       	if(isIE6()){
       	 returnValue= window.showModalDialog(url,null,"dialogWidth:550px; dialogHeight:410px;resizable:no;center:yes;scroll:yes;status:no");
       	}else{
		 returnValue= window.showModalDialog(url,null,"dialogWidth:540px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
       	}
   
   if(returnValue !=null){
   var ids = returnValue[0];
   var afield=eval('fieldIds9');
   if(returnValue[1] == '' || returnValue[1] ==null)
   afield.innerHTML="&nbsp;";
   else
   afield.innerHTML=returnValue[1];
   document.parameterForm2.resumeCodeValue.value = ids;
  } 
}
function getBusinessTemplate()
{
      var select_id=document.parameterForm2.businessTemplateIds.value;
      var t_url="/system/warn/config_maintenance.do?b_template=link&type=1&dr=2&select_id="+select_id;
        var return_vo='';
       	if(isIE6()){
       	 return_vo= window.showModalDialog(t_url,'rr',"dialogWidth:310px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
       	}else{
		 return_vo= window.showModalDialog(t_url,'rr',"dialogWidth:300px; dialogHeight:390px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
       	}
        
        if(!return_vo)
            return false;
         else
          {
             if(return_vo.flag=="true")
             {
                var afield=eval('fieldIds8');   
               
                var name=return_vo.title;
                var ids=return_vo.content;
                var a_name=name.split(",");
                var a_ids=ids.split(",");
                var bnames="";
                var ids="";
                for(var i=0;i<a_ids.length;i++)
                {
                   if(a_ids[i]==null||a_ids[i]=='')
                   continue;
                   bnames+=a_ids[i]+":"+a_name[i]+"<br>";
                   ids+=","+a_ids[i];
                }
                if(ids.length>0)
                    ids=ids.substring(1);
                 if(bnames=='')
                     bnames="&nbsp;&nbsp;";
                 afield.innerHTML=bnames;
                 document.parameterForm2.businessTemplateIds.value=ids;
             }
             
          }      
}
function cultureList(obj)
{
  //3000000187
  var id="";
  for(var i=0;i<obj.options.length;i++)
  {
     if(obj.options[i].selected)
     {
        id=obj.options[i].value;
        break;
     }
  }
  	var hashvo=new ParameterSet();
    hashvo.setValue("codesetid",id);
   	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:change_ok,functionId:'3000000187'},hashvo);					
}
function change_ok(outparameters)
{
  	var fielditemlist=outparameters.getValue("itemList");
	AjaxBind.bind(parameterForm2.cultureCodeItem,fielditemlist);
}
function setLicenseAgreement(flag)
{
   var url="/hire/parameterSet/configureParameter/licenseAgreement.do?b_search=search`opt=1`flag="+flag;
  // window.open(url,"_blank");
    var iframe_url="/general/query/common/iframe_query.jsp?src="+url; 
  var returnValue='';
       	if(isIE6()){
       	 returnValue= window.showModalDialog(iframe_url,null,"dialogWidth:790px; dialogHeight:620px;resizable:no;center:yes;scroll:yes;status:no");
       	}else{
		 returnValue= window.showModalDialog(iframe_url,null,"dialogWidth:740px; dialogHeight:600px;resizable:no;center:yes;scroll:yes;status:no");
       	}
  
   if(returnValue !=null){
      var obj=new Object();
      obj.len=returnValue.len;
      if(flag=="l")
      {
         var afield=eval('fieldIds10');
         if(obj.len=='1')
            afield.innerHTML="&nbsp;&nbsp;"+AGREEMENT_ALREADY_DEFINITION;
         else if(obj.len=='0')
            afield.innerHTML="&nbsp;&nbsp;"+AGREEMENT_NOT_ALREADY_DEFINITION;
      }
      else
      {
            var afield=eval('fieldIds11');
            if(obj.len=='1')
               afield.innerHTML="&nbsp;&nbsp;"+PROMPT_ALREADY_DEFINITION;
            else if(obj.len=='0')
               afield.innerHTML="&nbsp;&nbsp;"+PROMPT_NOT_ALREADY_DEFINITION;
      }
  } 
}
function hiddenPositionSalaryStandardItemList(obj)
{
   var trElement= document.getElementById("str");
   if(obj.checked)
      trElement.style.display="block";
   else
      trElement.style.display="none";
}
function hiddenhirePositionNotUnionOrg(obj)
{
  var trElement= document.getElementById("hpnuo");
   if(obj.checked)
      trElement.style.display="block";
   else
      trElement.style.display="none";
}
function hiddenSmg(obj){
	var trElement= document.getElementById("tsmg");
	
	 if(obj.checked){
      trElement.style.display="block";
	 }else{
      trElement.style.display="none";
      document.getElementById("approve").value="";
	 }
}
function visibleRES(obj)
{
   var trElement= document.getElementById("res");
   if(obj.checked)
      trElement.style.display="block";
   else
      trElement.style.display="none";
}
function getCommonInfo()
{
  var remenberExamineSet=document.getElementById("subset");
  var set='';
  for(var i=0;i<remenberExamineSet.options.length;i++)
  {
     if(remenberExamineSet.options[i].selected)
     {
        set=remenberExamineSet.options[i].value;
        break;
     }
  }
  if(set=='')
  {
     alert("请选择面试过程记录子集!");
     return;
  }
   var url="/hire/parameterSet/configureParameter/examine_info_config.do?b_search=search`setid="+set;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+url; 
 var returnValue='';
       	if(isIE6()){
       	 returnValue= window.showModalDialog(iframe_url,null,"dialogWidth:410px; dialogHeight:260px;resizable:no;center:yes;scroll:yes;status:no");
       	}else{
		 returnValue= window.showModalDialog(iframe_url,null,"dialogWidth:400px; dialogHeight:250px;resizable:no;center:yes;scroll:yes;status:no");
       	}
 
  if(returnValue)
  {
    var obj = new Object();
    obj.tf=returnValue.tf;
    obj.cf=returnValue.cf;
    obj.lf=returnValue.lf;
    obj.cdf=returnValue.cdf;
    obj.cuf=returnValue.cuf;
    document.getElementById("cf").value=obj.cf;
    document.getElementById("tf").value=obj.tf;
    document.getElementById("lf").value=obj.lf;
    document.getElementById("cdf").value=obj.cdf;
    document.getElementById("cuf").value=obj.cuf;
  } 
}
function setSchoolPosition(posid)
{
   var url="/hire/parameterSet/configureParameter/new_school_position.do?b_init=init`positionID="+posid;
   var iframe_url="/general/query/common/iframe_query.jsp?src="+url; 
  var returnValue='';
       	if(isIE6()){
       	 returnValue= window.showModalDialog(iframe_url,null, "dialogWidth:470px; dialogHeight:190px;resizable:no;center:yes;scroll:yes;status:no");
       	}else{
		 returnValue= window.showModalDialog(iframe_url,null, "dialogWidth:450px; dialogHeight:180px;resizable:no;center:yes;scroll:yes;status:no");
       	}
   if(returnValue)
   {
       var obj=new Object();
       obj.pid=returnValue.pid;
       obj.pdesc=returnValue.pdesc;
       var afield=eval('fieldIds20');
       afield.innerHTML="&nbsp;&nbsp;"+obj.pdesc+"&nbsp;&nbsp;";
        document.parameterForm2.schoolPosition.value=obj.pid;
   }
}
function getTables()
{
   var url="/hire/parameterSet/configureParameter/select_card.do?b_init=init";
   var iframe_url="/general/query/common/iframe_query.jsp?src="+url; 
   var returnValue='';
       	if(isIE6()){
       	 returnValue= window.showModalDialog(iframe_url,null, "dialogWidth:470px; dialogHeight:220px;resizable:no;center:yes;scroll:yes;status:no");
       	}else{
		 returnValue= window.showModalDialog(iframe_url,null, "dialogWidth:450px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
       	}
   if(returnValue&&returnValue!="no")
   {
       document.parameterForm2.cardIDs.value=returnValue;
   }
}
function changemajorcode()
{///当改变招聘专业时
	var hashvo=new ParameterSet(); 
	hashvo.setValue("hireMajor",getEncodeStr(parameterForm2.hireMajor.value));
   	var request=new Request({asynchronous:false,onSuccess:displayMajorCode,functionId:'3000000259'},hashvo);
}
function displayMajorCode(outParameters)
{
	var isCharField=outParameters.getValue("isCharField");
	if(isCharField==0)
	{
		document.getElementById("majorCode").style.display="none";
		document.getElementById("majorCodeName").style.display="none";
	}
	if(isCharField==1)
	{
		document.getElementById("majorCode").style.display="";
		document.getElementById("majorCodeName").style.display="";
	}
}
///初始化时执行的函数
function init()
{
	var isCharField="${parameterForm2.isCharField}";
	if(isCharField==0)
	{
		document.getElementById("majorCode").style.display="none";
		document.getElementById("majorCodeName").style.display="none";
	}
	aboutPassword();
	<hrms:priv func_id="31025">
	resumeAnalysis();
	</hrms:priv>
}
function aboutPassword(){
	if(document.getElementById("complexPassword").checked){
		document.getElementById("changdu").style.display="block";
	
	}else{
		document.getElementById("changdu").style.display="none";

	}
}
function resumeAnalysis(){
	if(document.getElementById("startResumeAnalysis")!=null){
		if(document.getElementById("startResumeAnalysis").checked){
			document.getElementById("resumeAnalysis_name_password").style.display="block";
			document.getElementById("resumeAnalysis_foreignJob").style.display="block";
		}else{
			document.getElementById("resumeAnalysis_name_password").style.display="none";
			document.getElementById("resumeAnalysis_foreignJob").style.display="none";	
		}
	}

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
<% 
	boolean isFive=false;
    EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
    if(lockclient!=null){
       if(lockclient.getVersion()>=50)
           isFive=true;
   } 
   	int versionFlag = 1;
   	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
   	//zxj 20160613 招聘不再区分标准版专业版
	//if (userView != null)
	//	versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版
   %>
<head>
</head>
<body onload="setTPinput()">
<%
 if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<br>
<%
 }
%>
<html:form action="/hire/parameterSet/configureParameter">
        <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:0px;margin-left:0px;">
          <thead>
            <tr> 
              <td align="left" class="TableRow" nowrap><bean:message key="hire.parameterSet.configureParameter"/>&nbsp; </td>
            </tr>
          </thead>
          <tr>
          <td class="RecordRow" align="center">
         <table>
         <tr>
         <td>
         <fieldset align="center" style="width:95%;"> 
         <LEGEND><bean:message key="hire.background.parameterset"/></LEGEND>
         <table border='0' width="80%">
          <tr>
		         <td align="right" height='20' width="165"  nowrap><bean:message key="hire.personstore.markfield"/>&nbsp;&nbsp;</td>
					   <td align='left' width='450' nowrap>	
						<hrms:optioncollection name="parameterForm2" property="personTypeList" collection="list" />
						 <html:select name="parameterForm2" property="personTypeId" size="1" style="width:165px;">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select><bean:message key="hire.personset.44"/>
					</td>
			<td></td>
		          </tr>
		          <tr>
		         <td align="right" height='30' width="165" nowrap><bean:message key="hire.resume.state"/>&nbsp;&nbsp; </td>
					   <td align='left' width='450'>	
						<hrms:optioncollection name="parameterForm2" property="resumeStateFieldsList" collection="list" />
						 <html:select name="parameterForm2" property="resumeStateFieldIds" size="1" style="width:165px;">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select><bean:message key="hire.personset.36"/>
					</td>
					<td></td>
		          </tr>
		           <tr>
		         <td align="right" height='30' width="165" nowrap><bean:message key="hire.object.field"/>&nbsp;&nbsp;</td>
					   <td align='left' width='450'>	
						<hrms:optioncollection name="parameterForm2" property="hireObjectParameterList" collection="list" />
						 <html:select name="parameterForm2" property="hireObjectId" size="1" style="width:165px;">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>(业务字典维护中的用工需求表，关联代码类35)
					</td>
		         	<td></td>
		          </tr>
		          <tr>
		         <td align="right" height='30' width="165" nowrap>简历激活状态指标&nbsp;&nbsp;</td>
					   <td align='left' width='450'>	
						<hrms:optioncollection name="parameterForm2" property="activeFieldList" collection="list" />
						 <html:select name="parameterForm2" property="activeField" size="1" style="width:165px;">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>(人员基本情况子集,关联代码类45)
					</td>
		         	<td></td>
		          </tr>
		          
		          
		          
		          
		          <%
		          ParameterForm parameterForm2=(ParameterForm)session.getAttribute("parameterForm2");
		          ArrayList hireObjectList=parameterForm2.getHireObjectList();
		          ArrayList testTemplateList=parameterForm2.getTestTemplateList();
		          ArrayList markList=parameterForm2.getMarkList();
		          
		          for(int i=0;i<hireObjectList.size();i++)
		          {
		          	LazyDynaBean abean=(LazyDynaBean)hireObjectList.get(i);
		          	String name=(String)abean.get("codeitemdesc")+ResourceFactory.getProperty("hire.exam.table");
		          	String value=(String)abean.get("value");
		          %>
		          
		          <tr> 
		            <td align="right" height='30' width="165" nowrap><%=name%>&nbsp;&nbsp; </td>
					<td align='left' width='450'>
					
						<select name='testTemplateIDs'>
							<%    
								for(int j=0;j<testTemplateList.size();j++)
								{
									CommonData date=(CommonData)testTemplateList.get(j);
									String avalue=date.getDataValue();
									String text=date.getDataName();
									String select="";
									if(avalue.equals(value))
										select="selected";
							 %>
								<option value='<%=avalue%>' <%=select%> ><%=text%></option>
							<%  
								}
							%>
						</select>
						<%
						
						
						
						if(markList==null||markList.size()==0||(markList.size()>i&&((String)markList.get(i)).equals("1"))){
						%>
						<input type='radio' name='<%="markType"+i%>' value='1' checked><bean:message key="lable.performance.grademark"/>
						<input type='radio' name='<%="markType"+i%>' value='2'><bean:message key="label.performance.mixmark"/>
						<%
						}
						else
						{
						%>
						<input type='radio' name='<%="markType"+i%>' value='1'><bean:message key="lable.performance.grademark"/>
						<input type='radio' name='<%="markType"+i%>' value='2' checked><bean:message key="label.performance.mixmark"/>
						<%
						}
						%>
						<%
                        if(i==hireObjectList.size()-1){
                        %>
                        <hrms:priv func_id="3105301">
	                      <span>
	                        <input type="button" class="mybutton"  value="高级..." onclick="setAdvanceValue()" style="position:relative;top:3px;left:10px;"/>
	                      </span>
                        </hrms:priv>
                        <%
                        }
                        %>
		            </td>
		            <td></td>
		          </tr>
		          <% } %>
		          
		          
		          
		          
		          
		          
		          
		          
		            <tr>
		         <td align="right" height='30' width="165" nowrap><bean:message key="hire.resume.level"/>&nbsp;&nbsp;</td><!-- 简历评语等级 -->
					   <td align='left' width='400'>	
						<hrms:optioncollection name="parameterForm2" property="resumeLevelFieldList" collection="list" />
						 <html:select name="parameterForm2" property="resumeLevelIds" size="1">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
					</td>
						<td></td>
		          </tr>   
		          
		          <tr>
		         <td align="right" height='20' width="165"  nowrap><bean:message key="hire.browseresume.rollcall"/>&nbsp;&nbsp; </td><!-- 预览简历登记表 -->
					   <td align='left' width='450' nowrap>	
						<hrms:optioncollection name="parameterForm2" property="previewTableList" collection="list" />
						 <html:select name="parameterForm2" property="previewTableId" size="1">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
				        &nbsp;<button name="" class="mybutton" onclick='getTables();'>...</button>
				        
					</td>
			<td align="left"> 	</td>
		          </tr>
		           <tr>
		         <td align="right" height='20' width="165"  nowrap>准考证&nbsp;&nbsp;</td>
					   <td align='left' width='450' nowrap>	
						<hrms:optioncollection name="parameterForm2" property="previewTableList" collection="list" />
						 <html:select name="parameterForm2" property="admissionCard" size="1">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
					</td>
			<td></td>
		          </tr>
		          
		          
		        	       <tr>
		         <td align="right" height='20' width="165"  nowrap>面试回复指标&nbsp;&nbsp; </td>
					   <td align='left' width='450' nowrap>	
						<hrms:optioncollection name="parameterForm2" property="interviewingRevertItemList" collection="list" />
						 <html:select name="parameterForm2" property="interviewingRevertItemid" size="1">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
					</td>
			<td></td>
		          </tr>     
		                  	       <tr>
		         <td align="right" height='20' width="165"  nowrap>单位部门预算表&nbsp;&nbsp; </td>
					   <td align='left' width='450' nowrap>	
						<hrms:optioncollection name="parameterForm2" property="orgWillTableList" collection="list" />
						 <html:select name="parameterForm2" property="orgWillTableId" size="1">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
					</td>
			<td></td>
		          </tr>  
		      <tr>
		         <td align="right" height='20' nowrap>招聘专业&nbsp;&nbsp; </td>
				 <td align='left' style="width:165px;" nowrap>	
						<hrms:optioncollection name="parameterForm2" property="hireMajorList" collection="list" />
						 <html:select name="parameterForm2" property="hireMajor" size="1" onchange="changemajorcode();">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
				 </td>
				 <td>
				 </td>
		    </tr> 
		    
		    
		    <tr>
		    		<td id="majorCodeName"align="right" height='20' nowrap>招聘专业代码&nbsp;&nbsp; </td>
					<td id="majorCode" align='left' width='450' nowrap>
							<hrms:optioncollection name="parameterForm2" property="hireMajorCodeList" collection="list" />
									 <html:select name="parameterForm2" property="hireMajorCode" size="1" style="width:165px;">
							             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
							 </html:select>
					</td>
					<td>
					</td>
		    </tr>      
		          
		          
      
		          <tr> 
		            <td align="right" valign='top' kheight='50' width="165" nowrap>校园招聘岗位&nbsp;&nbsp; </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIds20'>
		            	&nbsp;&nbsp;${parameterForm2.schoolPosDesc}
		            	</div>
		            </td>
		            <td  valign='top' >  
		            	<button name="" class="mybutton" onclick='setSchoolPosition("${parameterForm2.schoolPosition}")'>...</button>	
		            </td>
		          </tr>
		           <tr> 
		            <td align="right" valign='top' kheight='50' width="165" nowrap><bean:message key="hire.resume.exportfield"/>&nbsp;&nbsp; </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIds'>
		            	&nbsp;&nbsp;${parameterForm2.musterFieldNames}
		            	</div>
		            </td>
		            <td  valign='top' >  
		            	<button name="" class="mybutton" onclick='getMusterFields(1)'>...</button>	
		            </td>
		          </tr>
		          <!-- 浏览简历指标 -->
		          <tr> 
		            <td align="right" valign='top' height='50' width="165" nowrap><bean:message key="hire.resume.browsefield"/>&nbsp;&nbsp; </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIds5'>
		            	&nbsp;&nbsp;${parameterForm2.resumeFieldNames}
		            	</div>
		            </td>
		            <td  valign='top' >  
		            	<button name="" class="mybutton" onclick='getResumeFields()'>...</button>	
		            </td>
		          </tr>
		          <!-- 简历质量分析指标 -->
		            <tr> 
		            <td align="right" valign='top' height='50' width="165" nowrap><bean:message key="hire.resume.analysefield"/>&nbsp;&nbsp;</td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIds6'>
		            	&nbsp;&nbsp;${parameterForm2.resumeStaticNames}
		            	</div>
		            </td>
		            <td  valign='top' >  
		            	<button name="" class="mybutton" onclick='getResumeStaticFields()'>...</button>	
		            </td>
		      	    </tr>  
		      	    
		      	    
		      	  <tr> 
		            <td align="right" valign='top' height='50' width="165" nowrap><bean:message key="hire.position.statisticscondition"/>&nbsp;&nbsp;</td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIds7'>
		            	${parameterForm2.commonQueryNames}&nbsp;&nbsp;
		            	</div>
		            </td>
		            <td  valign='top' >  
		            	<button name="" class="mybutton" onclick='getCommonQueryCond()'>...</button>	
		            </td>
		      	    </tr>       
		      	    
		      	     <tr> 
		            <td align="right" valign='top' height='50' width="165" nowrap><bean:message key="hire.employee.model"/>&nbsp;&nbsp;</td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIds8'>
		            	${parameterForm2.businessTemplatenames}&nbsp;&nbsp;
		            	</div>
		            </td>
		            <td  valign='top' >  
		            	<button name="" class="mybutton" onclick='getBusinessTemplate();'>...</button>	
		            </td>
		      	    </tr> 
		      	    
		      	    		      	    <!-- 简历解析服务 -->
		      	 <hrms:priv func_id="31025">
		      	 <tr>
		         <td align="right" height='20' nowrap>简历解析服务&nbsp;&nbsp; </td>
				 <td align='left' style="width:165px;" nowrap>	
					<html:checkbox onclick="resumeAnalysis();" styleId="startResumeAnalysis" property="startResumeAnalysis" name="parameterForm2" value="1"></html:checkbox>
				 </td>
				 <td>
				 </td>

		    	</tr> 	   
			      	 <tr id="resumeAnalysis_name_password" >
			         <td align="right" height='20' nowrap></td>
					 <td align='left' style="width:400px;" nowrap>	
						用户名&nbsp;&nbsp;<html:text name="parameterForm2"  property="resumeAnalysisName" styleId="resumeAnalysisName"   style="height=20px;width=150px"></html:text>
						&nbsp;密码&nbsp;&nbsp;<html:password name="parameterForm2"  property="resumeAnalysisPassword" styleId="resumeAnalysisPassword"   style="height=20px;width=150px"></html:password>
					 </td>
					 <td>
					 	
					 </td>
			    	</tr> 	
			    	
	 			     <tr id="resumeAnalysis_foreignJob" >
			         <td align="right" height='20' nowrap></td>
					 <td align='left' style="width:400px;" nowrap>	
						对外发布岗位&nbsp;&nbsp;
							<hrms:optioncollection name="parameterForm2" property="foreignJobList" collection="list" />
							 <html:select name="parameterForm2" property="resumeAnalysisForeignJob" size="1">
					             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
					        </html:select>
					 </td>
					 <td>
					 </td>
			    	</tr> 	
	             </hrms:priv>
		      	   
		      	    <tr>
		      	     <td colspan="2" align="left" width="550">
		      	    <table width="550">
		      	    <tr>
		      	    	<td align="right" width='30%'>
		      	    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                                                                  招聘需求上报进行编制控制<html:checkbox styleId="bzk" property="isCtrlReportBZ" name="parameterForm2" value="1"></html:checkbox>
		      	    	</td>
		      	    	<td width="70%"></td>
		      	    </tr>
		      	     <tr> 
		      	     <td align="right" width=''>
		      	      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		      	      需求上报进行工资总额控制<html:checkbox styleId="gzk" onclick="hiddenPositionSalaryStandardItemList(this);" property="isCtrlReportGZ" name="parameterForm2" value="1"></html:checkbox>
		      	      </td>
		      	     <logic:equal value="1" property="isCtrlReportGZ" name="parameterForm2">
		      	    <td id="str" align="left" width="" valign="middle">
		      	   &nbsp;&nbsp;岗位最高工资标准&nbsp;&nbsp;
		      	     <hrms:optioncollection name="parameterForm2" property="positionSalaryStandardItemList" collection="list" />
						 <html:select name="parameterForm2" styleId="pssi" property="positionSalaryStandardItem" size="1">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>&nbsp;&nbsp;（岗位子集，数值型指标）
                     </td>
                      </logic:equal>
                     <logic:equal value="0" property="isCtrlReportGZ" name="parameterForm2">
		      	    <td id="str" style="display:none" align="left" valign="middle" width="" nowrap>
		      	  &nbsp;&nbsp;岗位最高工资标准&nbsp;&nbsp;
		      	     <hrms:optioncollection name="parameterForm2" property="positionSalaryStandardItemList" collection="list" />
						 <html:select name="parameterForm2" styleId="pssi" property="positionSalaryStandardItem" size="1">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>&nbsp;&nbsp;（岗位子集，数值型指标）
                     </td>
                      </logic:equal>
                      </tr>
		      	      <tr>
		      	      <td align="right" width="200" nowrap>
		      	      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                                                          招聘需求支持多级审批<html:checkbox styleId="mlsp" property="moreLevelSP" name="parameterForm2" value="1" onclick="hiddenSmg(this);"></html:checkbox>
		      	      </td>
		      	      <logic:equal value="1" property="moreLevelSP" name="parameterForm2">
		      	        <td id="tsmg" valign="middle" nowrap>
                      &nbsp;&nbsp;短信通知<html:checkbox styleId="hsmg" property="smg" name="parameterForm2" value="1"  ></html:checkbox>
		      	      &nbsp;&nbsp;审批关系<hrms:optioncollection name="parameterForm2" property="approvelist" collection="list" />
						 <html:select name="parameterForm2" styleId="approve" property="spRelation" size="1">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
				      </td>
                     </logic:equal>
                     <logic:equal value="0" property="moreLevelSP" name="parameterForm2">
		      	        <td id="tsmg" style="display:none" valign="middle" nowrap>
                      &nbsp;&nbsp;短信通知<html:checkbox styleId="hsmg" property="smg" name="parameterForm2" value="1"  ></html:checkbox>
                      &nbsp;&nbsp;审批关系<hrms:optioncollection name="parameterForm2" property="approvelist" collection="list" />
						 <html:select name="parameterForm2" styleId="approve" property="spRelation" size="1">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
				      </td>
                     </logic:equal>
		      	      </tr>
		      	     <tr>   
		      	     <td align="right" width='200'>	
		      	      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		      	                    面试过程需全程记录<html:checkbox onclick="visibleRES(this);" styleId="ire" property="isRemenberExamine" name="parameterForm2" value="1"></html:checkbox>
                    </td>
                    <logic:equal value="1" property="isRemenberExamine" name="parameterForm2">
                    <td id="res" style="display:block" width="400">
                   &nbsp;&nbsp;面试过程记录子集&nbsp;&nbsp;
                     <hrms:optioncollection name="parameterForm2" property="remenberExamineSetList" collection="list" />
						 <html:select styleId="subset" name="parameterForm2" property="remenberExamineSet" style="width:180px" size="1">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
				        &nbsp;&nbsp;
				          <button name="pp" class="mybutton" onclick='getCommonInfo();'>...</button>	
				        </td>
				        </logic:equal>
				        <logic:equal value="0" property="isRemenberExamine" name="parameterForm2">
                    <td id="res" style="display:none" width="400">
                     &nbsp;&nbsp;面试过程记录子集&nbsp;&nbsp;
                     <hrms:optioncollection name="parameterForm2" property="remenberExamineSetList" collection="list" />
						 <html:select styleId="subset" name="parameterForm2" property="remenberExamineSet" style="width:180px" size="1">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
				        &nbsp;&nbsp;  
				        <button name="pp" class="mybutton" onclick='getCommonInfo();'>...</button>	
				        </td>
				        </logic:equal>     
				        </tr>
				        </table>
				        </td>      		          
		      	    </tr>
   	         		          
         </table>
         </fieldset>
         </td>
         </tr>
         <TR>
         <td width="100%">
         <fieldset align="center" style="width:95%;">
         <legend><bean:message key="hire.proscenium.parameterset"/></legend>
         <table border='0' width="100%">
           <tr>
		         <td align="right" height='30' width="165" nowrap><bean:message key="hire.culture.type"/>&nbsp;&nbsp;</td>
					   <td align='left' width='400'>	
						<hrms:optioncollection name="parameterForm2" property="cultureCodeList" collection="list" />
						 <html:select name="parameterForm2" property="cultureCode" size="1" onchange="cultureList(this);">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
				        <hrms:optioncollection name="parameterForm2" property="cultureList" collection="list" />
						 <html:select name="parameterForm2" property="cultureCodeItem" size="1">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
					</td>
		          </tr>   
		        <tr>
		         <td align="right" height='20' width="165"  nowrap>开放问答子集&nbsp;&nbsp; </td>
					   <td align='left' width='450' nowrap>	
						<hrms:optioncollection name="parameterForm2" property="answerSetList" collection="list" />
						 <html:select name="parameterForm2" property="answerSet" size="1">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
					</td>
			<td></td>
		          </tr> 
		          
		            <tr <%if(isFive){if(versionFlag==1){}else{out.print("style='display:none'");}}else{ out.print("style='display:none'");} %>>
		            
		         <td align="right" height='20' width="165"  nowrap>最近&nbsp;&nbsp;</td>
					   <td align='left' width='450' nowrap>	
						<input type="text" name="adddate" size="14" style="width:165px"  value="<bean:write name="parameterForm2" property="newTime" />" style="BACKGROUND-COLOR:#FFFFFF;border: 1pt solid;width:165px"  id='newTime1'/>天为最新岗位
					</td>
					<td>
				</td>
				
		          </tr> 
		           <tr>
		         <td align="right" height='20' width="165"  nowrap>工作经验&nbsp;&nbsp; </td>
					   <td align='left' width='450' nowrap>	
						<hrms:optioncollection name="parameterForm2" property="workExperienceList" collection="list" />
						 <html:select name="parameterForm2" property="workExperience" size="1">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>&nbsp;&nbsp;(人员基本情况子集,关联代码类45) 
					</td> 
			<td></td>
		          </tr>  
		        
		        
		          
        <tr> 
		            <td align="right" valign='top' height='50' width="165" nowrap>岗位快速查询指标&nbsp;&nbsp; </td>
					<td  class="RecordRow" align='left' width='440'>	
						<div id='fieldIds2'>
		            	&nbsp;&nbsp;${parameterForm2.posQueryFieldNames}
		            	</div>
		            </td>
		            <td  valign='top' >  
		            	<button name="" class="mybutton" onclick='getMusterFields(2)'>...</button>	
		            </td>
		          </tr>
		           <tr  <%if(isFive){if(versionFlag==1){}else{out.print("style='display:none'");}}else{ out.print("style='display:none'");} %>> 
		            <td align="right" valign='top' height='50' width="165" nowrap><bean:message key="hire.position.queryfield"/>&nbsp;&nbsp; </td>
					<td  class="RecordRow" align='left' width='440'>	
						<div id='fieldIdsCom'>
		            	&nbsp;&nbsp;${parameterForm2.posCommQueryFieldNames}
		            	</div>
		            </td>
		            <td  valign='top' >  
		            	<button name="" class="mybutton" onclick='getMusterFields(5)'>...</button>	
		            </td>
		          </tr>
		          <tr> 
		            <td align="right" valign='top' height='50' width="165" nowrap>外网岗位列表显示指标&nbsp;&nbsp;</td>
					<td  class="RecordRow" align='left' width='440'>	
						<div id='fieldIds12'>
		            	&nbsp;&nbsp;${parameterForm2.pos_listfieldNames}
		            	</div>
		            </td>
		            <td  valign='top' >  
		            	<button name="" class="mybutton" onclick='getMusterFields(4)'>...</button>	
		            </td>
		          </tr>
                  <tr>
                     <td align="right" valign='top' height='50' width="165" nowrap>外网岗位列表指标 排序&nbsp;&nbsp;</td>
                        <td  class="RecordRow" align='left' width='440'>    
                            <div id='fieldIds13'>
                              &nbsp;&nbsp;${parameterForm2.pos_listfield_sortNames}
                            </div>
                        </td>
                    <td  valign='top' >  
                        <button name="" class="mybutton" onclick='getMusterFields(6)'>...</button>  
                    </td>
                  </tr>		          
		          
		           <tr> 
		            <td align="right" valign='top' height='50' width="165" nowrap><bean:message key="hire.position.descriptionfield"/>&nbsp;&nbsp; </td>
					<td  class="RecordRow"  align='left' width='440'>	
						<div id='fieldIds3'>
		            	&nbsp;&nbsp;${parameterForm2.viewPosFieldNames}
		            	</div>
		            </td>
		            <td  valign='top' >  
		            	<button class="mybutton" name="" onclick='getMusterFields(3)'>...</button>	
		            </td>
		          </tr>
		          
		          <tr> 
		            <td align="right" valign='top' height='50' width="165" nowrap><bean:message key="hire.disable.resumestate"/>&nbsp;&nbsp;</td>
					<td  class="RecordRow"  align='left' width='440'>	
						<div id='fieldIds9'>
		            	${parameterForm2.resumeCodeName}&nbsp;&nbsp;
		            	</div>
		            </td>
		            <td  valign='top' >  
		            	<button class="mybutton" name="" onclick='getResumeStateCode();'>...</button>	
		            </td>
		          </tr>
		          
		           <tr> 
		            <td align="right" valign='top' height='50' width="165" nowrap><bean:message key="hire.unit.descriptionfield"/>&nbsp;&nbsp; </td>
					<td  class="RecordRow"  align='left' width='440'>	
						<div id='fieldIds4'>
		            	&nbsp;&nbsp;${parameterForm2.orgFieldNames}
		            	</div>
		            </td>
		            <td  valign='top'>  
		            	<button class="mybutton" name="" onclick='setOrgIntro()'>...</button>	
		            </td>
		          </tr>
		          
		          
		          <tr> 
		            <td align="right" valign='top' height='50' width="165" nowrap><bean:message key="hire.agreement.permit"/>&nbsp;&nbsp;</td>
					<td  class="RecordRow"  align='left' width='440'>	
						<div id='fieldIds10'>
		            	&nbsp;&nbsp;${parameterForm2.licenseAgreementParameter}
		            	</div>
		            </td>
		            <td  valign='top'>  
		            	<button class="mybutton" name="" onclick='setLicenseAgreement("l");'>...</button>	
		            </td>
		          </tr>
		           <tr> 
		            <td align="right" valign='top' height='50' width="165" nowrap><bean:message key="hire.lable.promptcontent"/>&nbsp;&nbsp;</td>
					<td  class="RecordRow"  align='left' width='440'>	
						<div id='fieldIds11'>
		            	&nbsp;&nbsp;${parameterForm2.promptContentParameter}
		            	</div>
		            </td>
		            <td  valign='top'>  
		            	<button class="mybutton" name="" onclick='setLicenseAgreement("p");'>...</button>	
		            </td>
		          </tr>
		          <tr> 
		            <td align="right" valign='top' height='20' width="165" nowrap><bean:message key="hire.apply.maxcount"/>&nbsp;&nbsp;</td>
					<td align='left'>	
					
					<html:text property="max_count" name="parameterForm2" size="20"></html:text>
		            </td>
		            <td  valign='top' >  
		            	&nbsp;&nbsp;&nbsp;&nbsp;
		            </td>
		          </tr>
		          <tr> 
		            <td align="right" valign='top' height='20' width="165" nowrap><bean:message key="hire.lable.positionnumber"/>&nbsp;&nbsp;</td>
					<td align='left'>	
					
					<html:text property="positionNumber" name="parameterForm2" size="20"></html:text>
		            </td>
		            <td  valign='top' >  
		            	&nbsp;&nbsp;&nbsp;&nbsp;
		            </td>
		          </tr>
		          <tr> 
		            <td align="right" valign='top' height='20' width="165" nowrap><bean:message key="hire.netlogo.href"/>&nbsp;&nbsp;</td>
					<td align='left'>	
					
					<html:text property="netHref" name="parameterForm2" size="30"></html:text>
		            </td>
		            <td  valign='top' >  
		            	&nbsp;&nbsp;&nbsp;&nbsp;
		            </td>
		          </tr>
		           <tr> 
		           <td align="right" colspan="3" nowrap>
		           
		           
		           
		         <table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
		         <tr>
		            <td align="right" valign='top' height='20' width="170" nowrap><bean:message key="hire.photo.mustupload"/>&nbsp;&nbsp;
					
					<html:checkbox styleId="otohp" property="photo" name="parameterForm2" value="1"></html:checkbox></td>
		          
		            <td  align="right" valign='top' height='20' width="250" nowrap><bean:message key="hire.visible.explanation"/>&nbsp;&nbsp;
		            	<html:checkbox styleId="explain" property="explaination" name="parameterForm2" value="1"></html:checkbox>
		            </td>
		            </tr>
		            </table> 
		            </td>
		          </tr>	   
		          <tr> 
		           <td align="right" colspan="3" nowrap> 
		         <table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
		         <tr>
		            <td align="right" valign='top' height='20' width="170" nowrap><bean:message key="hire.attach.upload"/>&nbsp;&nbsp;
					
					<html:checkbox styleId="att" property="attach" name="parameterForm2" value="1"></html:checkbox></td>
		          
		            <td  align="right" valign='top' height='20' width="250" nowrap>注册帐号需通过邮箱激活才生效&nbsp;&nbsp;<html:checkbox styleId="aba" property="acountBeActived" name="parameterForm2" value="1"></html:checkbox>            </td>
		            </tr>
		            </table> 
		            </td>
		          </tr>	
   		          <tr> 
		           <td align="right" colspan="3" nowrap> 
		           <table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
		             <tr>
		             		          
		          	 <td align="right" valign='top' height='20' width="170" nowrap>只显示本级单位招聘岗位&nbsp;&nbsp;<html:checkbox styleId="hpbl" property="hirePostByLayer" name="parameterForm2" value="1"></html:checkbox>  </td>
 					 <td  align="right" valign='top' height='20' width="250" nowrap>      </td>
		        	</tr>
		            </table> 
		            </td>	  
		         </tr> 
        		 <tr> 
		           <td align="right" colspan="3" nowrap> 
		           <table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
		             <tr>
		             <td  width="185" nowrap>
		             &nbsp;&nbsp;&nbsp;&nbsp;注册帐号使用复杂密码&nbsp;&nbsp;<html:checkbox styleId="complexPassword" onclick="aboutPassword();" property="complexPassword" name="parameterForm2" value="1"></html:checkbox>
		             </td>
		             <td width="38%" align="left">		
		             		<span id="geshi" >
		             		(格式：字母、数字、特殊字符的组合)
		             			&nbsp;
		             		</span>	
		             </td>
		             <td width="32%" align="left">		
		             		<span id="changdu">	
	             				长度&nbsp;&nbsp;
	             				<html:text name="parameterForm2"  property="passwordMinLength" styleId="passwordMinLength"   style="height=20px;width=30px"></html:text>
							    至
	 							<html:text name="parameterForm2"  property="passwordMaxLength" styleId="passwordMaxLength"   style="height=20px;width=30px"></html:text>
								位
		 					</span>     
		                      
				          
 					 </td>
		        	</tr>
		            </table> 
		            </td>	  
		         </tr> 
   		          <tr> 
		           <td align="right" colspan="3" nowrap> 
		           <table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
		          <tr> 
	
		
	           		 <td align="right" valign='top' height='20' width="150" nowrap>

	           		 最大登录失败次数&nbsp;&nbsp;
	           		
	           		 </td>
	           		 <td> 
	           		 <html:text name="parameterForm2"  property="failedTime" styleId="failedTime"   style="height=20px;width=50px"></html:text>次(一天内)
	           		 </td>
	           		  <td align="center" valign='top' height='20' width="" nowrap>
		     	
		     		解锁时间间隔&nbsp;&nbsp;
					<html:text name="parameterForm2"  property="unlockTime" styleId="unlockTime"   style="height=20px;width=50px"></html:text>分钟					
		            </td>
		          </tr>
		            </table> 
		            </td>	  
		         </tr>           
         </table>
         </fieldset>
         
                <input type='hidden' name='musterFieldIDs' value="${parameterForm2.musterFieldIDs}" />
          		<input type='hidden' name='posQueryFieldIDs' value="${parameterForm2.posQueryFieldIDs}" />
          		<input type='hidden' name='posCommQueryFieldIDs' value="${parameterForm2.posCommQueryFieldIDs}" />
          		<input type='hidden' name='viewPosFieldIDs' value="${parameterForm2.viewPosFieldIDs}" />
          		<input type='hidden' name='orgFieldIDs' value="${parameterForm2.orgFieldIDs}"/>
          		<input type='hidden' name='resumeFieldIds' value="${parameterForm2.resumeFieldIds}"/>
          		<input type='hidden' name='resumeStaticIds' value="${parameterForm2.resumeStaticIds}"/>
          		<input type="hidden" name="commonQueryIds" value="${parameterForm2.commonQueryIds}"/>
          		<input type="hidden" id="t_i" name="businessTemplateIds" value="${parameterForm2.businessTemplateIds}"/>
          		<input type="hidden" name="resumeCodeValue" value="${parameterForm2.resumeCodeValue}"/>
          		<html:hidden name="parameterForm2" styleId="tf" property="titleField"/>
          		<html:hidden name="parameterForm2" styleId="cf" property="contentField"/>
          		<html:hidden name="parameterForm2" styleId="lf" property="levelField"/>
          		<html:hidden name="parameterForm2" styleId="cdf" property="commentDateField"/>
          		<html:hidden name="parameterForm2" styleId="cuf" property="commentUserField"/>
          		<input type='hidden' name='newTime' value="${parameterForm2.newTime}" />
          		<input type='hidden' name='pos_listfield' value="${parameterForm2.pos_listfield}" />
          		<input type='hidden' name='pos_listfield_sort' value="${parameterForm2.pos_listfield_sort}" />
          		<input type="hidden" name="schoolPosition" value="${parameterForm2.schoolPosition}"/>
          		<html:hidden name="parameterForm2" property="cardIDs"/>
         </td>
         </tr>
          <tr> 
            <td align="center" nowrap> &nbsp;&nbsp; <input type="button" name="b_cardset" class="mybutton" value="&nbsp;<bean:message key="kq.kq_rest.submit"/>&nbsp;"  onclick='sub()'  > 
           
            </td>
          </tr>
        </table>
        </td>
        </tr>
        </table>
        <input type='hidden' name="testTemplateID" />
        <input type='hidden' name='mark_type'/>
</html:form>
	
<script language="javascript">
init();
function setAdvanceValue(){
    var url="/hire/parameterSet/configureParameter/setAdvance.do?b_search=search";
    var returnValue=window.showModalDialog(url,null, "dialogWidth:600px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
   if(returnValue&&returnValue!="no")
   {
       //document.parameterForm2.cardIDs.value=returnValue;
   }
}
</script>


</body>
</html>