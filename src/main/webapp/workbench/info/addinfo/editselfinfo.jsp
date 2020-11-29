<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.actionform.selfinfomation.SelfInfoForm"%> 
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	// 在标题栏显示当前用户和日期 2004-5-10 
	String userName = null;
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	String codevalue = "";
	String code = "";
	String isAll = "";
	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
		if (!userView.isSuper_admin()) {
			code = userView.getManagePrivCode();
			codevalue = userView.getManagePrivCodeValue();
			if ("UN".equalsIgnoreCase(code) && (codevalue == null || codevalue.length() == 0)) {
				isAll = "all";
			}
		} else {
			isAll = "all";
		}
	}
%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<hrms:linkExtJs/>
<script language="JavaScript" src="../../../ext/rpc_command.js"></script>
<script language="JavaScript" src="../../../components/codeSelector/codeSelector.js"></script>
<script type="text/javascript" src="../../../components/extWidget/proxy/TransactionProxy.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script type="text/javascript" src="/js/dict.js"></script>
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="javascript" src="/js/wz_tooltip.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
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
	var date_desc;
  
   function setSelectValue()
   {
     if(date_desc)
     {
       date_desc.value=$F('date_box');
       Element.hide('date_panel');   
     }
   }
   function exeButtonAction(actionStr,target_str)
   {
       target_url=actionStr;       
       window.open(target_url,target_str); 
   }
   function showDateSelectBox(srcobj)
   {
          date_desc=srcobj;
          Element.show('date_panel');   
          var pos=getAbsPosition(srcobj);
	  with($('date_panel'))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
		style.posTop=pos[1]-1+srcobj.offsetHeight;
		style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
          }                 
   }
     function addDict(code,itemid,obj)
{
  Element.hide('dict');
  var value=obj.value;  
  if(value=="")
   return false;
  var dmobj;
  var vos= document.getElementsByName('dict_box');
  var dict_vo=vos[0];
  var isC=true;
  code_desc=obj;
  for(var i=dict_vo.options.length-1;i>=0;i--)
  {
      dict_vo.options.remove(i);
  }
   var no = new Option();
   no.value="";
   no.text="";
   dict_vo.options[0]=no;
   var r=1;
       var vos;
       if(itemid.toLowerCase()=="e01a1" || itemid.toLowerCase()=="e0122")
		    if(code=="UM")
		      vos= document.getElementById('b0110');
		    else if(code=="@K"){
		    	vos= document.getElementById('e0122');
		    	if(vos.value==''){
		    		vos= document.getElementById('b0110');
		    	}
		    }
		    var code_value="<%=codevalue%>";
		    
   for(var i=0;i<g_dm.length;i++)
   {
		dmobj=g_dm[i];	
		if(code=="UM"||code=="@K"||code=="UN")
		{
		    if(vos)
		    {
		       var b_value=vos.value;		       
		       if(b_value==null||b_value=="")
		       {
		          b_value=""
		       }
		       if((code_value!=""||'<%=isAll %>'=='all')&&code_value.length<=dmobj.ID.substring(2).length)
		       {
		           
		           if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code+code_value)==0&&dmobj.ID.indexOf(code+b_value)==0)|| (dmobj.ID.indexOf(code+value)==0&&dmobj.ID.indexOf(code+b_value)==0))
		           {
		             if(dmobj.ID.substring(2).indexOf(code_value)==0)
		              {
		                  var hashvo=new ParameterSet();
     					  hashvo.setValue("a_code",dmobj.ID);
     					  var request=new Request({method:'post',onSuccess:getBirthdayAge,functionId:'10200770001'},hashvo);
							function getBirthdayAge(outparamters)
 							 {
     							var parentdesc=outparamters.getValue("parentdesc");
				                 var no = new Option();
		    	                 no.value=dmobj.ID;
		    	                 no.text=(parentdesc.length>0?parentdesc+"/":"")+dmobj.V;
				                 dict_vo.options[r]=no;
					             r++;
					          }
		              }
		           }
		       }else
		       {
		          if(code_value.length>0&&((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0)||(dmobj.ID.indexOf(code+value)==0)))
		          {
		          if(dmobj.ID.substring(2).indexOf(code_value)==0)
		              {
		              var hashvo=new ParameterSet();
     					  hashvo.setValue("a_code",dmobj.ID);
     					  var request=new Request({method:'post',onSuccess:getBirthdayAge,functionId:'10200770001'},hashvo);
							function getBirthdayAge(outparamters)
 							 {
 							 var parentdesc=outparamters.getValue("parentdesc");
		            var no = new Option();
    	            no.value=dmobj.ID;
    	            no.text=(parentdesc.length>0?parentdesc+"/":"")+dmobj.V;
		            dict_vo.options[r]=no;
			        r++;
			        }
			        }
		          }
		       }
		       
		    }else
		    {
		       if(code_value!=""&&code_value.length>=dmobj.ID.substring(2).length)
		       {
		           if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0&&dmobj.ID.indexOf(code+b_value)==0)||(dmobj.ID.indexOf(code+value)==0&&dmobj.ID.indexOf(code+b_value)==0))
		           {
		              if(code_value.indexOf(dmobj.ID.substring(2))==0)
		              {
		              var hashvo=new ParameterSet();
     					  hashvo.setValue("a_code",dmobj.ID);
     					  var request=new Request({method:'post',onSuccess:getBirthdayAge,functionId:'10200770001'},hashvo);
							function getBirthdayAge(outparamters)
 							 {
 							  var parentdesc=outparamters.getValue("parentdesc");
		                 var no = new Option();
    	                 no.value=dmobj.ID;
    	                no.text=(parentdesc.length>0?parentdesc+"/":"")+dmobj.V;
		                 dict_vo.options[r]=no;
			             r++;
			             }
		              }
		           }
		       }else
		       {
		              if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0&&dmobj.ID.indexOf(code+b_value)==0)||(dmobj.ID.indexOf(code+value)==0&&dmobj.ID.indexOf(code+b_value)==0))
		              {
		              var hashvo=new ParameterSet();
     					  hashvo.setValue("a_code",dmobj.ID);
     					  var request=new Request({method:'post',onSuccess:getBirthdayAge,functionId:'10200770001'},hashvo);
							function getBirthdayAge(outparamters)
 							 {
 							  var parentdesc=outparamters.getValue("parentdesc");
		               var no = new Option();
    	               no.value=dmobj.ID;
    	                no.text=(parentdesc.length>0?parentdesc+"/":"")+dmobj.V;
		               dict_vo.options[r]=no;
			           r++;
			           }
		             }
		       }
		    }
		    
		}else
		{	 
		  if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0)||(dmobj.ID.indexOf(code+value)==0))
		  {
		    
		    var no = new Option();
    	    no.value=dmobj.ID;
    	    no.text=dmobj.V;
		    dict_vo.options[r]=no;		    
			r++;
		  }
	    }
   }
   if(r==1)
   {
      obj.value="";
      Element.hide('dict'); 
      return false;      
   }   
   Element.show('dict');  
   var pos=getAbsPosition(obj);  
   with($('dict'))
   {
	   style.position="absolute";
       style.posLeft=pos[0]-1;
 	   style.posTop=pos[1]-1+obj.offsetHeight;
	   style.width=(obj.offsetWidth<150)?150:obj.offsetWidth+1;
   }  
}
</script>
<%String orgtemp="";
  String orgtempview="";
  String postemp="";
  String postempview="";
  String kktemp="";
  String kktempview="";
  
  String birthdayfield="";
  String agefield="";
  String workagefield="";
  String postagefield="";
  String axfield="";
  String axviewfield="";
  int rowss;
  %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

<script language="javascript">
  function validate()
  {
 	  check();
	  var idcardflag = "";
		//获取证件类型指标id
		var idType = document.getElementById("idType").value;
		//获取证件类型值
		var idTypeValue = "";
		if(idType&&document.getElementById(idType))
			idTypeValue = document.getElementById(idType).value;
		//身份证证件默认值
		var temp = document.getElementById("idTypeValue").value;
		var idcarditem = document.getElementById("idcardflag");
		if(idcarditem!=null&&idcarditem!=undefined)
			idcardflag = idcarditem.value;
		var idcard = document.getElementById("idcard");
		var idcardvalue = "";
		if(idcard!=null&&idcard!=undefined)
			idcardvalue = idcard.value;
		
		var flag = IdCardValidate(idcardvalue);
		if(idcardflag=="true"){
			//有默认身份证类型"1"or"01"跟选择的证件类型一致
			if(temp&&idTypeValue&&temp==idTypeValue||!idType || !idTypeValue)
				if(!flag){
					alert("身份证格式错误，请重新填写！");
					return false;
				}
		} else {
			if(idcardvalue!=""){
				if(temp&&idTypeValue&&temp==idTypeValue||!idType)
					if(!flag){
						alert("身份证格式错误，请重新填写！");
						return false;
					}
			}
		}
	
    var tag=true;    
     <logic:iterate  id="element"    name="selfInfoForm"  property="infoFieldList" indexId="index"> 
     <logic:notEqual value="#####" name="element" property="itemid">
        <bean:define id="fl" name="element" property="fillable"/>
		  <bean:define id="desc" name="element" property="itemdesc"/>
          <logic:equal name="element" property="itemid" value="b0110">
              <%orgtemp="infoFieldList["+index+"].value";%>  
              <%orgtempview="infoFieldList["+index+"].viewvalue";%>              
          </logic:equal>
          <logic:equal name="element" property="itemid" value="e0122">
                 <%postemp="infoFieldList["+index+"].value";%>
                  <%postempview="infoFieldList["+index+"].viewvalue";%>
          </logic:equal>
          <logic:equal name="element" property="itemid" value="e01a1">
                 <%kktemp="infoFieldList["+index+"].value";%>
                  <%kktempview="infoFieldList["+index+"].viewvalue";%>
          </logic:equal>
          
          
        	  <logic:equal name="element" property="itemid" value="${selfInfoForm.birthdayfield}">
                  <%birthdayfield="infoFieldList["+index+"].value";%>
             </logic:equal>
          	 <logic:equal name="element" property="itemid" value="${selfInfoForm.agefield}">
                  <%agefield="infoFieldList["+index+"].value";%>
             </logic:equal>
               <logic:equal name="element" property="itemid" value="${selfInfoForm.workagefield}">
                  <%workagefield="infoFieldList["+index+"].value";%>
             </logic:equal>
               <logic:equal name="element" property="itemid" value="${selfInfoForm.postagefield}">
                  <%postagefield="infoFieldList["+index+"].value";%>
             </logic:equal>
             <logic:equal name="element" property="itemid" value="${selfInfoForm.axfield}">
                  <%axfield="infoFieldList["+index+"].value";%>
                 <%axviewfield="infoFieldList["+index+"].viewvalue";%>
             </logic:equal>
          
          var valueInputs=document.getElementsByName("<%="infoFieldList["+index+"].value"%>");
          var dobj=valueInputs[0];
           if("${fl}"=='true'&&dobj.value.length<1){
          	alert("${desc}"+'必须填写！');

          	return false;
          }
        <logic:equal name="element" property="itemtype" value="D">   
            var d_value=dobj.value;
           d_value=replaceAll(d_value,"-",".");
          tag= validateDate(dobj,d_value) && tag;    
              
	  if(tag==false)
	  {
	    dobj.focus();
	    return false;
	  }
        </logic:equal> 
        <logic:equal name="element" property="itemtype" value="N"> 
           <logic:lessThan name="element" property="decimalwidth" value="1"> 
             var valueInputs=document.getElementsByName("<%="infoFieldList["+index+"].value"%>");
             var dobj=valueInputs[0];
              tag=checkNUM1(dobj) &&  tag ;  
	      if(tag==false)
	      {
	        dobj.focus();
	        return false;
	      }
	    </logic:lessThan>
	    <logic:greaterThan name="element" property="decimalwidth" value="0"> 
	     var valueInputs=document.getElementsByName('<%="infoFieldList["+index+"].value"%>');
             var dobj=valueInputs[0];
             tag=checkNUM2(dobj,${element.itemlength},${element.decimalwidth}) &&  tag ;  
              if(tag==false)
	      {
	        dobj.focus();
	        return false;
	      }
	    </logic:greaterThan>
	</logic:equal>  
	</logic:notEqual>
      </logic:iterate>    
     return tag;   
  }
  var orgtemp="<%=orgtemp%>";
  var orgtempview="<%=orgtempview%>";
  var postemp="<%=postemp%>";
  var postempview="<%=postempview%>";
  var kktemp="<%=kktemp%>";
  var kktempview="<%=kktempview%>";
  var returnValue = "${selfInfoForm.returnvalue}";
  var uplevel = "${selfInfoForm.uplevel}";
  var nbase = "${selfInfoForm.userbase}";
  var birthdayfield = "<%=birthdayfield%>";
  var agefield = "<%=agefield%>";
  var axfield = "<%=axfield%>";
  var axviewfield = "<%=axviewfield%>";
  var workagefield = "<%=workagefield%>";
  var postagefield = "<%=postagefield%>";
  var startpostfield = "${selfInfoForm.startpostfield}";
  var workdatefield = "${selfInfoForm.workdatefield}";
</script>
<script language="javascript">
  function exeButtonAction(actionStr,target_str)
   {
       target_url=actionStr;
       window.open(target_url,target_str); 
   }  
   function getchangeposun(outparamters)
    {
	  var result= Ext.decode(outparamters.responseText);
      var pretype=result.pretype;
      var orgparentcode=result.orgparentcode;
      var deptparentcode=result.deptparentcode;
      var posparentcode=result.posparentcode;
      AjaxBind.bind(selfInfoForm.orgparentcode, orgparentcode);
      AjaxBind.bind(selfInfoForm.deptparentcode, deptparentcode);
      AjaxBind.bind(selfInfoForm.posparentcode, posparentcode);  
      document.getElementById("deptId").setAttribute("parentid",deptparentcode);
      document.getElementById("jobId").setAttribute("parentid",deptparentcode);
      var valueInputsun=document.getElementsByName("<%=postemp%>");
      var dobjun=valueInputsun[0];
      if(dobjun!=null)
        dobjun.value="";
      valueInputsun=document.getElementsByName("<%=postempview%>");
      dobjun=valueInputsun[0];
      if(dobjun!=null)
        dobjun.value="";
      var cc=document.getElementsByName("<%=kktemp%>");
      if(cc.length>0){
      var dobjkk=cc[0];
      dobjkk.value="";
      cc=document.getElementsByName("<%=kktempview%>");
      dobjkk=cc[0];
      dobjkk.value="";
      }
  }
   function getchangeposum(outparamters)
   {
	  var result= Ext.decode(outparamters.responseText);
      var pretype=result.pretype;
      var orgparentcode=result.orgparentcode;
      var orgvalue=result.orgvalue;
      if(orgvalue!=null && orgvalue.length>0)
      {
         var orgvalueview=result.orgviewvalue;
         var valueInputsun=document.getElementsByName("<%=orgtemp%>");
         var dobjun=valueInputsun[0];         
         dobjun.value=orgvalue;
         document.getElementById("deptId").setAttribute("parentid",orgvalue);
         valueInputsun=document.getElementsByName("<%=orgtempview%>");
         dobjun=valueInputsun[0];
         dobjun.value=orgvalueview;
      }
      var valueInputskk=document.getElementsByName("<%=kktemp%>");
      if(valueInputskk.length>0){
      var dobjkk=valueInputskk[0];
      if(dobjkk!=null)
       dobjkk.value="";
      valueInputskk=document.getElementsByName("<%=kktempview%>");
      dobjkk=valueInputskk[0];
      if(dobjkk!=null)
       dobjkk.value="";
      }
      var deptparentcode=result.deptparentcode;
      var posparentcode=result.posparentcode;
      AjaxBind.bind(selfInfoForm.orgparentcode, orgparentcode);
      AjaxBind.bind(selfInfoForm.posparentcode, posparentcode);
  }
   function getchangeposkk(outparamters)
   {
	  var result = Ext.decode(outparamters.responseText);
      var pretype=result.pretype;
      var orgparentcode=result.orgparentcode;
      var deptparentcode=result.deptparentcode;
      var posparentcode=result.posparentcode;
      var orgvalue=result.orgvalue;
      if(orgvalue!=null && orgvalue.length>0)
      {
         var orgvalueview=result.orgviewvalue;
         var valueInputsun=document.getElementsByName("<%=orgtemp%>");
         var dobjun=valueInputsun[0];
         dobjun.value=orgvalue;
         valueInputsun=document.getElementsByName("<%=orgtempview%>");
         dobjun=valueInputsun[0];
         dobjun.value=orgvalueview;
      }
       var deptvalue=result.deptvalue;
      if(deptvalue!=null && deptvalue.length>0)
      {
         var deptviewvalue=result.deptviewvalue;
         var valueInputsum=document.getElementsByName("<%=postemp%>");
         var dobjum=valueInputsum[0];
         dobjum.value=deptvalue;
         valueInputsum=document.getElementsByName("<%=postempview%>");
         dobjum=valueInputsum[0];
         dobjum.value=deptviewvalue;
      }
      AjaxBind.bind(selfInfoForm.orgparentcode, orgparentcode);
  
  }
   
   function changeOrg (pretype) {
	      var value = "";
          var unIdInputs = document.getElementsByName("<%=orgtempview%>");
          var valueInputsun = document.getElementsByName("<%=orgtemp%>");
	      if('b0110' == pretype) {
	          if(unIdInputs != null && unIdInputs != "undefined" && unIdInputs.length > 0)
	              value = unIdInputs[0].value;
	          
	          if(valueInputsun != null && valueInputsun != "undefined" && valueInputsun.length > 0) {
	              if(!value) {
	                  valueInputsun[0].value = "";
	                  document.getElementById("deptId").setAttribute("parentid","");
	                  document.getElementById("jobId").setAttribute("parentid","");
	                  changepos("2");
	              }
	          }
	          
	      } else if('e0122' == pretype) {
	          var umIdInputs = document.getElementsByName("<%=postempview%>");
	          var valueInputsum = document.getElementsByName("<%=postemp%>");
	          if(umIdInputs != null && umIdInputs != "undefined" && umIdInputs.length > 0)
	              value = umIdInputs[0].value;
	          
	          if(valueInputsum != null && valueInputsum != "undefined" && valueInputsum.length > 0) {
	              if(!value) {
	                  valueInputsum[0].value = "";
	                  if(valueInputsun && valueInputsun.length > 0 && valueInputsun[0].value)
	                      document.getElementById("jobId").setAttribute("parentid",valueInputsun[0].value);
	                  else
	                      document.getElementById("jobId").setAttribute("parentid","");
	                  
	                  changepos("1");
	              }
	          }
	          
	      } else if('e01a1' == pretype) {
	          var kkIdInputs = document.getElementsByName("<%=kktempview%>");
	          var valueInputskk = document.getElementsByName("<%=kktemp%>");
	          if(kkIdInputs != null && kkIdInputs != "undefined" && kkIdInputs.length > 0)
	              value = kkIdInputs[0].value;
	          
	          if(valueInputskk != null && valueInputskk != "undefined" && valueInputskk.length > 0) {
	              if(!value) {
	                  valueInputskk[0].value = "";
	              }
	          }
	      }
	  }
   
  function changepos(pretype)
  {   
	  if('0'==pretype){
  	  	pretype='@K';
  	  }
  	  if('1'==pretype){
  	  	pretype='UM';
  	  }
  	  if('2'==pretype){
  	  	pretype='UN';
  	  }
      var valueInputsun=document.getElementsByName("<%=orgtemp%>");
      var dobjun;
      var dobjum;
       var dobjkk;
      if(valueInputsun!=null&&valueInputsun!="undefined"&&valueInputsun.length>0)
        dobjun=valueInputsun[0];
      var valueInputsum=document.getElementsByName("<%=postemp%>");
      if(valueInputsum!=null&&valueInputsum!="undefined"&&valueInputsum.length>0)      
       dobjum=valueInputsum[0];      
      var valueInputskk=document.getElementsByName("<%=kktemp%>");
      if(valueInputskk!=null&&valueInputskk!="undefined"&&valueInputskk.length>0)
        dobjkk=valueInputskk[0];
      var hashvo=new HashMap();
      hashvo.put("pretype",pretype);
      if(dobjun!=null&&dobjun!="undefined")
       hashvo.put("orgparentcodestart",dobjun.value);
      else
       hashvo.put("orgparentcodestart","");
      if(dobjum!=null&&dobjum!="undefined")
       hashvo.put("deptparentcodestart",dobjum.value);
      else
       hashvo.put("deptparentcodestart","");
      if(dobjkk!=null){
      hashvo.put("posparentcodestart",dobjkk.value);
      }else{
      hashvo.put("posparentcodestart","");
      }
      if(pretype=="UN")      {
        
    	  Rpc({functionId:'02010001012',async:false,success:getchangeposun},hashvo);
      }
     if(pretype=="UM")
      {
    	 Rpc({functionId:'02010001012',async:false,success:getchangeposum},hashvo);
      }
      if(pretype=="@K")
      {
    	  Rpc({functionId:'02010001012',async:false,success:getchangeposkk},hashvo);
      }
  }
  
	function calculatebirthday(obj) {
		//获取证件类型指标id
		var idType = document.getElementById("idType").value;
		//是否关联计算
		var cardflag = document.getElementById("cardflag").value;
		if("true"!=cardflag)
			return;
		//获取证件类型值
		var idTypeValue = "";
		if(idType&&document.getElementById(idType))
			idTypeValue = document.getElementById(idType).value;
		//身份证证件默认值
		var temp = document.getElementById("idTypeValue").value;
		//有默认身份证类型"1"or"01"跟选择的证件类型一致
		if(temp&&idTypeValue&&temp==idTypeValue||!idType || !idTypeValue){
			var hashvo = new ParameterSet();
			obj.value = trim(obj.value.replace(/ /g, ""));
		hashvo.setValue("idcardvalue", obj.value);
			var request = new Request({method : 'post', onSuccess : getBirthdayAge, functionId : '02010001013'}, hashvo);
		}
	}
  function getBirthdayAge(outparamters)
  {

     var birthdayvalue=outparamters.getValue("birthdayvalue");
    
     var agevalue=outparamters.getValue("agevalue");
     var axvalue=outparamters.getValue("axvalue"); 
     if(birthdayvalue!=null)
     {
         var valueInputs=document.getElementsByName("<%=birthdayfield%>");
         if(valueInputs[0]!=null){
         	var dobj=valueInputs[0];
        	dobj.value=birthdayvalue;
        }
    
     }
     if(agevalue!=null)
     {
         var valueInputs=document.getElementsByName("<%=agefield%>");
         if(valueInputs[0]!=null){
         var dobj=valueInputs[0];
          if(dobj!=null)
         dobj.value=agevalue;
         }
     }  
     if(axvalue!=null)
     {
         var valueInputs=document.getElementsByName("<%=axfield%>");
         if(valueInputs[0]!=null){
         var dobj=valueInputs[0];
          if(dobj!=null)
         dobj.value=axvalue;
         if(axvalue==1)
         {
            var valueInputs=document.getElementsByName("<%=axviewfield%>");
            dobj=valueInputs[0];
            if(dobj!=null)
            dobj.value="男";
         }else if(axvalue==2)
         {
            var valueInputs=document.getElementsByName("<%=axviewfield%>");
            dobj=valueInputs[0];
            if(dobj!=null)
            dobj.value="女";
         }
         }
     }        
     
  }
  function CalculateWorkDate(obj)
  {	
      var hashvo=new ParameterSet();
      hashvo.setValue("workdatevalue",obj.value);
      var request=new Request({method:'post',onSuccess:getWorkAge,functionId:'02010001014'},hashvo);
  }
  function getWorkAge(outparamters)
  {
     var workagevalue=outparamters.getValue("workagevalue");
     if(workagevalue!=null)
     {
         var valueInputs=document.getElementsByName("<%=workagefield%>");
         var dobj=valueInputs[0];
         if(dobj!=null){
         dobj.value=workagevalue;
         <logic:equal name="selfInfoForm" property="workdatefield" value="${selfInfoForm.startpostfield}">
             valueInputs=document.getElementsByName("<%=postagefield%>");
             dobj=valueInputs[0];
             dobj.value=workagevalue;
         </logic:equal>  
         } 
     }     
  }
  function CalculatePostAge(obj)
  {
      var hashvo=new ParameterSet();
      hashvo.setValue("postdatevalue",obj.value);
      var request=new Request({method:'post',onSuccess:getPostAge,functionId:'02010001015'},hashvo);
  }
  function getPostAge(outparamters)
  {
     var postagevalue=outparamters.getValue("postagevalue");
     if(postagevalue!=null)
     {
         var valueInputs=document.getElementsByName("<%=postagefield%>");
         var dobj=valueInputs[0];
         if(dobj!=null){
        	 dobj.value=postagevalue;  
        	 <logic:equal name="selfInfoForm" property="startpostfield" value="${selfInfoForm.workdatefield}">
           	 	 valueInputs=document.getElementsByName("<%=workagefield%>");
            	 dobj=valueInputs[0];
            	 dobj.value=workagevalue;
        	 </logic:equal>   
         }
     }     
  }
  
  
  function changedb()
  {
     selfInfoForm.action="/workbench/info/addinfo/add.do?b_reusebase=add&a0100=A0100&i9999=I9999&actiontype=new&setname=A01&tolastpageflag=yes&userbase=" +selfInfoForm.userbase.value;
     selfInfoForm.target="il_body";
     selfInfoForm.submit();
  }
  function changesort(){
  selfInfoForm.action="/workbench/info/addinfo/add.do?b_reusebase=add&a0100=A0100&i9999=I9999&actiontype=new&setname=A01&tolastpageflag=yes&userbase=" +selfInfoForm.userbase.value;
  selfInfoForm.target="il_body";
  selfInfoForm.submit();
  }
  
    function proves(){
if(validate()){
if(confirm("一旦报批数据，将不能进行修改，是否报批?")){
selfInfoForm.action="/workbench/info/addinfo/add.do?b_appeals=link";
selfInfoForm.submit();
}else{
return;
}
}
}


  function saves(){
	  if(validate()){
		  var button = document.getElementById("saveButton");
          if(button)
              button.disabled="true";
          
		  selfInfoForm.action="/workbench/info/addinfo/add.do?b_savesub=link";
		  selfInfoForm.submit();
	  }else{
		  return;
	  }
	
  }
  //【8693】员工管理-信息录入（记录录入、快速录入），姓名输入特殊字符后，报空指针，不能给出正确的提示 jingq add 2015.04.13
  function check(){
	  var item = document.getElementById("a0101");
	  //zxj 20151103 取不到值时要退出来，否则导致无法保存
	  if (!item) return;
	  
	  var value = item.value;
	  var itemvalue = "";
	  //【13349】山东英才学院：姓名不能输入空格，打入空格自动退格删除 ，导致客户无法正确输入外籍人员的姓名。
	  //解决方法：首尾空格去掉、中间空格保留
	  value = trim(value);
	  for(var i = 0;i < value.length;i++){
		  var index = value.substring(i,i+1);
		  if(index.charCodeAt(0) > 255){
			  itemvalue += index;
		  } else if((/^[a-zA-Z0-9]+$/).test(index)){
			  itemvalue += index;
		  } else if(index=="("||index==")"){
			  itemvalue += index;
		  }else if(index==" "){
		  	itemvalue +=index;
		  }
	  }
	  item.value = itemvalue;
  }
  

  
  var Wi = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 ];    // 加权因子   
  var ValideCode = [ 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 ];            // 身份证验证位值.10代表X   
  function IdCardValidate(idCard) { 
      idCard = trim(idCard.replace(/ /g, ""));               //去掉字符串头尾空格                     
      if (idCard.length == 15) {   
          return isValidityBrithBy15IdCard(idCard);       //进行15位身份证的验证    
      } else if (idCard.length == 18) {   
          var a_idCard = idCard.split("");                // 得到身份证数组   
          if(isValidityBrithBy18IdCard(idCard)&&isTrueValidateCodeBy18IdCard(a_idCard)){   //进行18位身份证的基本验证和第18位的验证
              return true;   
          }else {   
              return false;   
          }   
      } else {   
          return false;   
      }   
  }   
  /**  
   * 判断身份证号码为18位时最后的验证位是否正确  
   * @param a_idCard 身份证号码数组  
   * @return  
   */  
  function isTrueValidateCodeBy18IdCard(a_idCard) {   
      var sum = 0;                             // 声明加权求和变量   
      if (a_idCard[17].toLowerCase() == 'x') {   
          a_idCard[17] = 10;                    // 将最后位为x的验证码替换为10方便后续操作   
      }   
      for ( var i = 0; i < 17; i++) {   
          sum += Wi[i] * a_idCard[i];            // 加权求和   
      }   
      valCodePosition = sum % 11;                // 得到验证码所位置   
      if (a_idCard[17] == ValideCode[valCodePosition]) {   
          return true;   
      } else {   
          return false;   
      }   
  }   
  /**  
    * 验证18位数身份证号码中的生日是否是有效生日  
    * @param idCard 18位书身份证字符串  
    * @return  
    */  
  function isValidityBrithBy18IdCard(idCard18){   
      var year =  idCard18.substring(6,10);   
      var month = idCard18.substring(10,12);   
      var day = idCard18.substring(12,14);   
      var temp_date = new Date(year,parseFloat(month)-1,parseFloat(day));   
      // 这里用getFullYear()获取年份，避免千年虫问题   
      if(temp_date.getFullYear()!=parseFloat(year)   
            ||temp_date.getMonth()!=parseFloat(month)-1   
            ||temp_date.getDate()!=parseFloat(day)){   
              return false;   
      }else{   
          return true;   
      }   
  }   
    /**  
     * 验证15位数身份证号码中的生日是否是有效生日  
     * @param idCard15 15位书身份证字符串  
     * @return  
     */  
    function isValidityBrithBy15IdCard(idCard15){   
        var year =  idCard15.substring(6,8);   
        var month = idCard15.substring(8,10);   
        var day = idCard15.substring(10,12);   
        var temp_date = new Date(year,parseFloat(month)-1,parseFloat(day));   
        // 对于老身份证中的年龄则不需考虑千年虫问题而使用getYear()方法   
        if(temp_date.getYear()!=parseFloat(year)   
                ||temp_date.getMonth()!=parseFloat(month)-1   
                ||temp_date.getDate()!=parseFloat(day)){   
                  return false;   
          }else{   
              return true;   
          }   
    }   
  //去掉字符串头尾空格   
  function trim(str) {   
      return str.replace(/(^\s*)|(\s*$)/g, "");   
  } 
  
  function saveData() {
	  if(validate()){
		  var button = document.getElementById("savePerson");
          if(button)
              button.disabled="true";
          
		  selfInfoForm.action="/workbench/info/addinfo/add.do?b_save=link";
		  selfInfoForm.submit();
	  }
	  
  }
  
  function saveAndAdd() {
	  if(validate()){
		  var button = document.getElementById("saveAndAddButton");
		  if(button)
			  button.disabled="true";
		  
		  selfInfoForm.action="/workbench/info/addinfo/add.do?b_save_add=link";
		  selfInfoForm.submit();
	  }
  }
</script>
<%
	int i=0;
	int flag=0;

%>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<body>
<hrms:themes />
<style>
.AddTableRow {
	height:22px;
	valign:middle;
	padding:0 5px 0 5px;
}
.textColorRead{
	align:middle;
}
</style>
<html:form action="/workbench/info/addinfo/add" onsubmit="return validate();">
  <% SelfInfoForm selfInfoForm=(SelfInfoForm)session.getAttribute("selfInfoForm");
  rowss= Integer.parseInt(selfInfoForm.getRownums());
      if(selfInfoForm.getInfoFieldList().size()>0){%>
  <%
     if(selfInfoForm.isIs_save_add() == true){
   %>
    <script language="javascript">
        exeButtonAction('/workbench/info/addinfo/add.do?b_add=add&a0100=${selfInfoForm.a0100}&i9999=I9999&actiontype=new&tolastpageflag=yes&setname=${selfInfoForm.setname}','il_body')
    </script>
  <%
      selfInfoForm.setIs_save_add(false);
   } 
  %>
<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1" style="margin-top: 8px">
<logic:equal name="selfInfoForm" property="setname" value="A01">  
  <tr>
        <td align="left"  nowrap colspan="4" class="AddTableRow">
     	     <bean:message key="label.query.dbpre"/>
    	         <hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="false" paraValue="" 
                 sql="selfInfoForm.dbcond" collection="list" scope="page"/>
              <html:select name="selfInfoForm" property="userbase" size="1" onchange="changedb()">
                      <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select>
                <logic:notEmpty name="selfInfoForm" property="personsortlist" >
	           <html:select name="selfInfoForm" property="personsort" size="1" onchange="changesort()">
                           <html:optionsCollection property="personsortlist" value="codeitem" label="codename"/>
                   </html:select>  
	       </logic:notEmpty>
	</td>      
     </tr>
   </logic:equal>

	<html:hidden name="selfInfoForm" property="actiontype"/> 
	<html:hidden name="selfInfoForm" property="a0100"/> 
	<html:hidden name="selfInfoForm" property="i9999"/> 
	<html:hidden name="selfInfoForm" property="setname"/>
	<html:hidden name="selfInfoForm" property="orgparentcode"/> 
	<html:hidden name="selfInfoForm" property="deptparentcode"/> 
	<html:hidden name="selfInfoForm" property="posparentcode"/> 
	<html:hidden name="selfInfoForm" property="idTypeValue" styleId="idTypeValue" />
	<html:hidden name="selfInfoForm" property="idType" styleId="idType" />
	<html:hidden name="selfInfoForm" property="cardflag" styleId="cardflag" />
 
<logic:iterate id="element" name="selfInfoForm"  property="infoFieldList" indexId="index">
<logic:equal value="#####" name="element" property="itemid">
	<logic:notEqual value="0" name="element" property="itemlength">
		</table>
		</div>
		</td></tr>
		<%i++; %>
	</logic:notEqual>
	<tr class='trDeep1'>
		<td colspan='4'>
    		<img src='/images/new_target_wiz.gif'>&nbsp;<a href='javascript:void(0)' onclick="showinfo('show${element.itemlength }')">
    		<bean:write name="element" property="itemdesc"/></a>
    	</td>
    </tr>
    <tr class='trShallow1'><td colspan='4'>
    <logic:equal value="0" name="element" property="itemlength">
    	<div id="show${element.itemlength }" style='display:block;'>
    </logic:equal>
    <logic:notEqual value="0" name="element" property="itemlength">
    	<div id="show${element.itemlength }" style='display:none;'>
    </logic:notEqual>
    <table  border='0' cellspacing='1' cellpadding='1' width='100%' class='ListTable3'>
</logic:equal>
<logic:notEqual value="#####" name="element" property="itemid">
<hrms:itemmemo id="itemmemo" img="image" setname="${selfInfoForm.setname}" itemid="${element.itemid}"></hrms:itemmemo>
  <logic:notEqual name="element" property="itemtype" value="M">
   <%if(flag==0){
        if(i%2==0){
       %>
         <tr class="trShallow1">            
        <%}
        else
        {%>
          <tr class="trDeep1">  
        <%}
        i++;
        flag=rowss;          
        }else{
          flag=0;           
        }%>
    
    <td align="right" nowrap class="AddTableRow" valign="middle" ${itemmemo}>
       ${image} <hrms:textnewline text="${element.itemdesc}" len="20"></hrms:textnewline>
    </td>
    </logic:notEqual>
   <logic:equal name="element" property="priv_status" value="1"> 
    <logic:equal name="element" property="codesetid" value="0">
      <logic:equal name="element" property="itemtype" value="D">
         <td align="left"  nowrap valign="middle" class="AddTableRow">
             <html:text   name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true" styleClass="textColorRead" maxlength="${element.itemlength}"  onclick="Element.hide('dict');" /> 
       		<logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal> 
         </td> 
      </logic:equal> 
      <logic:equal name="element" property="itemtype" value="A">
         <td align="left"  nowrap valign="middle" class="AddTableRow">
            <html:text  name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true" styleClass="textColorRead" maxlength="${element.itemlength}"  onclick="Element.hide('dict');"/> 
       <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal>
         </td>
      </logic:equal> 
       <logic:equal name="element" property="itemtype" value="N">
           <td align="left"  nowrap valign="middle" class="AddTableRow">
               <html:text  name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true" styleClass="textColorRead" maxlength="${element.itemlength + element.decimalwidth + 1}"  onclick="Element.hide('dict');"/> 
      	 <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal>  
           </td>
       </logic:equal>     
      <logic:equal name="element" property="itemtype" value="M">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=rowss;%>
         <td align="right" nowrap class="AddTableRow" valign="middle" ${itemmemo}>
            ${image} <hrms:textnewline text="${element.itemdesc}" len="20"></hrms:textnewline>
         </td>
                 <td align="left"  nowrap valign="middle"  colspan="3" class="AddTableRow">
                  <html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true"  rows="10"  cols="66" styleClass="textColorRead" style="height:100px;width:550px;" onclick="Element.hide('dict');"/>
       		<logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal> 
                 </td> 
             <%       
             }else{
               flag=0;%>               
              <td colspan="2" class="AddTableRow">
              </td>
              </td>
               
             <%
            if(flag==0){
              if(i%2==0){
             %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=rowss;          
             }else{
               flag=0;           
             }%>               
         <td align="right" nowrap class="AddTableRow" valign="middle" ${itemmemo}>
            ${image} <hrms:textnewline text="${element.itemdesc}" len="20"></hrms:textnewline>
         </td>
                <td align="left"  nowrap valign="middle" colspan="3" class="AddTableRow">
                  <html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true"  rows="10"  cols="66" styleClass="textColorRead" style="height:100px;width:550px;" onclick="Element.hide('dict');"/>
       <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal> 
                </td> 
               
               <%         
             }%>
         
           <%flag=0;%>
           
          </tr>
      </logic:equal>
    </logic:equal>
    <logic:notEqual name="element" property="codesetid" value="0">
           <td align="left"  nowrap valign="middle" class="AddTableRow">
                 <html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>'/>  
                    <html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>' readonly="true"  styleClass="textColorRead" onclick="Element.hide('dict');"/> 
                           <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal> 
           </td>
      </logic:notEqual>
   </logic:equal>  
     
   <logic:equal name="element" property="priv_status" value="2"> 
    <logic:equal name="element" property="codesetid" value="0">
      <logic:equal name="element" property="itemtype" value="D">
         <td align="left"  nowrap valign="middle" class="AddTableRow">
            <logic:notEmpty name="selfInfoForm" property="workdatefield">
                 <logic:equal name="selfInfoForm" property="workdatefield" value="${element.itemid}">
                     <input type="text" name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />" class="textColorRead" style='width:200px;background-color:#ffffff;' extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate"  onchange="CalculateWorkDate(this)" onBlur="CalculateWorkDate(this)"/> 
                 </logic:equal>
                <logic:notEqual name="selfInfoForm" property="workdatefield" value="${element.itemid}">
                   <logic:notEmpty name="selfInfoForm" property="startpostfield">
                        <logic:equal name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                            <input type="text" name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />" class="textColorRead" style='width:200px;background-color:#ffffff;' extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate" onchange="CalculatePostAge(this)" onBlur="CalculatePostAge(this)"/>
                        </logic:equal>
                        <logic:notEqual name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                          <input type="text" name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />" class="textColorRead" style='width:200px;background-color:#ffffff;' extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate"/>
                        </logic:notEqual>
                   </logic:notEmpty>
                  <logic:empty name="selfInfoForm" property="startpostfield">
                    <input type="text" name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />" class="textColorRead" style='width:200px;background-color:#ffffff;' extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate"/> 
                   </logic:empty>
                </logic:notEqual>
            </logic:notEmpty>
            <logic:empty name="selfInfoForm" property="workdatefield">
               <logic:notEmpty name="selfInfoForm" property="startpostfield">
                 <logic:equal name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                     <input type="text" name='<%="infoFieldList["+index+"].value"%>' onblur="CalculatePostAge(this)" value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />" class="textColorRead" style='width:200px;background-color:#ffffff;' extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate"/>  
                 </logic:equal>
                <logic:notEqual name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                  <input type="text" name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />" class="textColorRead" style='width:200px;background-color:#ffffff;' extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate"/>
                </logic:notEqual>
              </logic:notEmpty>
              <logic:empty name="selfInfoForm" property="startpostfield">
                  <input type="text" name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />" class="textColorRead" style='width:200px;background-color:#ffffff;' extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate"/>
              </logic:empty>
            </logic:empty>
             <logic:equal name="element"  property="fillable" value="true">
               <font color="red">*</font>
             </logic:equal>
         </td> 
      </logic:equal> 
      <logic:equal name="element" property="itemtype" value="A">
		<td align="left"  nowrap valign="middle" class="AddTableRow">
              <logic:notEmpty name="selfInfoForm" property="idcardfield">
                <logic:equal name="selfInfoForm" property="idcardfield" value="${element.itemid}">
            		<html:hidden name="element" property="fillable" styleId="idcardflag"/>
                   <html:text styleId="idcard" name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');" styleClass="textColorWrite" maxlength="${element.itemlength}"  onblur="calculatebirthday(this)"/>  
                </logic:equal>
                <logic:notEqual name="selfInfoForm" property="idcardfield" value="${element.itemid}">
                	<logic:equal name="element" property="itemid" value="a0101">
	                   <html:text  name="selfInfoForm" styleId="a0101" property='<%="infoFieldList["+index+"].value"%>' onkeyup="check();" onclick="Element.hide('dict');" styleClass="textColorWrite" maxlength="${element.itemlength}" />   
	                </logic:equal>
	                <logic:notEqual name="element" property="itemid" value="a0101">
	                   <html:text  name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');" styleClass="textColorWrite" maxlength="${element.itemlength}" />   
	                </logic:notEqual>
                </logic:notEqual>
             </logic:notEmpty>
              <logic:empty name="selfInfoForm" property="idcardfield">
                 <html:text  name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');" styleClass="textColorWrite" maxlength="${element.itemlength}" />   
              </logic:empty> 
                     <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal>
         </td>
      </logic:equal> 
       <logic:equal name="element" property="itemtype" value="N">
           <td align="left"  nowrap valign="middle" class="AddTableRow">
               <html:text  name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');" styleClass="textColorWrite" maxlength="${element.itemlength + element.decimalwidth + 1}" />

       <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal>
           </td>
       </logic:equal>     
      <logic:equal name="element" property="itemtype" value="M">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=rowss;%>
         <td align="right" nowrap class="AddTableRow" valign="middle" ${itemmemo}>
            ${image} <hrms:textnewline text="${element.itemdesc}" len="20"></hrms:textnewline>
         </td>
                 <td align="left"  nowrap valign="middle"  colspan="3" class="AddTableRow">
                  <html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>'  rows="10"  cols="66" onclick="Element.hide('dict');" style="height:100px;width:550px;" styleClass="textColorWrite"/>
       <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal>
                 </td> 
             <%       
             }else{
               flag=0;%>               
              <td colspan="2" class="AddTableRow">
              </td>
              </td>
               
             <%
            if(flag==0){
              if(i%2==0){
             %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=rowss;          
             }else{
               flag=0;           
             }%>               
         <td align="right" nowrap class="AddTableRow" valign="middle" ${itemmemo}>
            ${image} <hrms:textnewline text="${element.itemdesc}" len="20"></hrms:textnewline>
         </td>
                <td align="left"  nowrap valign="middle" colspan="3" class="AddTableRow">
                  <html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>'  rows="10"  cols="66" onclick="Element.hide('dict');" style="height:100px;width:550px;" styleClass="textColorWrite"/>
       <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal> 
                </td> 
               
               <%         
             }%>
         
           <%flag=0;%>
           
          </tr>
      </logic:equal>
    </logic:equal>
    <logic:notEqual name="element" property="codesetid" value="0">
           <td align="left"  nowrap valign="middle" class="AddTableRow">
           
           <!-------------------------------------------------start------------------------------------------------------------------  -->
           <!-- 
				方法1：changepos(参数1)参数1: 0=岗位 1=部门 2=单位  功能：更新text框里面的内容
				
				方法2：changeLowerLevel(参数1,参数2)参数1：当前选择input的valuename 参数2：孩子节点的ID 功能：更新下级codeselector的parentid
				
				update by xiegh on date 20180315 
			 -->
                <logic:equal name="element" property="itemid" value="b0110">
                   <html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleId="b0110" onchange="changepos('${element.codesetid}')"/>  <%-- onkeyup="addDict('${element.codesetid}','${element.itemid}',this);" onkeydown="return inputType2(this,event)"--%>
                   <html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>'   styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);changeOrg('b0110');"  onclick="styleDisplay(this);"/> 
                   <img src="/images/code.gif"  plugin="codeselector" codesetid='${element.codesetid}'  onlySelectCodeset="false" inputname="<%="infoFieldList["+index+"].viewvalue"%>" valuename='<%="infoFieldList["+index+"].value"%>'  align="absmiddle" afterfunc="changeLowerLevel('<%="infoFieldList["+index+"].value"%>','deptId');changepos('2');" />
                 </logic:equal>
                 
                <logic:equal name="element" property="itemid" value="e0122">
                     <html:hidden name="selfInfoForm"  property='<%="infoFieldList["+index+"].value"%>' styleId="e0122" onchange="changepos('${element.codesetid}')"/>  
                     <html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>'   styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);changeOrg('e0122');"   onclick="styleDisplay(this);"/> <%--onkeyup="addDict('${element.codesetid}','${element.itemid}',this);" onkeydown="return inputType2(this,event)"--%>
                     <img src="/images/code.gif" id='deptId'  plugin="codeselector" codesetid='${element.codesetid}'  onlySelectCodeset="true" inputname="<%="infoFieldList["+index+"].viewvalue"%>" valuename='<%="infoFieldList["+index+"].value"%>'  align="absmiddle" afterfunc="changeLowerLevel('<%="infoFieldList["+index+"].value"%>','jobId');changepos('1');" />
                </logic:equal>
                
                 <logic:equal name="element" property="itemid" value="e01a1">
                  	 <html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleId="e01a1" onchange="changepos('${element.codesetid}')"/>  
                     <html:text name="selfInfoForm"   property='<%="infoFieldList["+index+"].viewvalue"%>'   styleClass="textColorWrite"  onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);changeOrg('e01a1');"  onclick="styleDisplay(this);"/> <%-- onkeyup="addDict('${element.codesetid}','${element.itemid}',this);" onkeydown="return inputType2(this,event)"--%>
                     <img src="/images/code.gif"  id='jobId' plugin="codeselector" codesetid='${element.codesetid}'  onlySelectCodeset="true" inputname="<%="infoFieldList["+index+"].viewvalue"%>" valuename='<%="infoFieldList["+index+"].value"%>'  align="absmiddle"  afterfunc="changepos('0');"/>
                </logic:equal>
                
                <script>
						function changeLowerLevel(curentId,childId){
								document.getElementById(childId).setAttribute("parentid",document.getElementsByName(curentId)[0].value);
						}
				</script>
                
               <logic:notEqual name="element" property="itemid" value="b0110">
                <logic:notEqual name="element" property="itemid" value="e0122">
                    <logic:notEqual name="element" property="itemid" value="e01a1">                      
                      <html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>'/>  
                      <html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>'   styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);"  onclick="styleDisplay(this);"/> <%--  onkeyup="addDict('${element.codesetid}','${element.itemid}',this);" onkeydown="return inputType2(this,event)"--%>
                     <logic:equal name="selfInfoForm" property="setname" value="${selfInfoForm.part_setid}">
						<logic:equal name="element" property="itemid" value="${selfInfoForm.part_unit}">
						 <img src="/images/code.gif"  plugin="codeselector" codesetid='${element.codesetid}' inputname="<%="infoFieldList["+index+"].viewvalue"%>" valuename='<%="infoFieldList["+index+"].value"%>'  align="absmiddle" />
						</logic:equal>
						<logic:notEqual name="element" property="itemid" value="${selfInfoForm.part_unit}">
						<logic:equal name="element" property="codesetid" value="UM">
							 <img src="/images/code.gif"  plugin="codeselector" codesetid='${element.codesetid}' inputname="<%="infoFieldList["+index+"].viewvalue"%>" valuename='<%="infoFieldList["+index+"].value"%>' onlySelectCodeset="false" align="absmiddle" />
						</logic:equal>
						<logic:notEqual name="element" property="codesetid" value="UM">
                             <img src="/images/code.gif"  plugin="codeselector" codesetid='${element.codesetid}' inputname="<%="infoFieldList["+index+"].viewvalue"%>" valuename='<%="infoFieldList["+index+"].value"%>' onlySelectCodeset="true" align="absmiddle" />
                        </logic:notEqual>
						</logic:notEqual>
					 </logic:equal>
					 <logic:notEqual name="selfInfoForm" property="setname" value="${selfInfoForm.part_setid}">
					  <logic:notEqual name="element" property="itemid" value="${selfInfoForm.part_unit}">					    
                      <img src="/images/code.gif"  plugin="codeselector" codesetid='${element.codesetid}' inputname="<%="infoFieldList["+index+"].viewvalue"%>" valuename='<%="infoFieldList["+index+"].value"%>'  align="absmiddle" />
                      </logic:notEqual>
					</logic:notEqual>
                   </logic:notEqual>
                </logic:notEqual>
               </logic:notEqual>
                      <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal>
               </td>
      </logic:notEqual>
               
   </logic:equal>  
   <logic:notEqual name="element" property="itemtype" value="M">  
   <%if(flag==0){%>           
      </tr>
   <%}else{%>
       <logic:equal name="element" property="rowflag" value="${index}"> 
          <td colspan="2" class="AddTableRow">
          </td>
          </tr>
       </logic:equal>
   <%}%> 
   </logic:notEqual>
   </logic:notEqual>
</logic:iterate> 
<logic:equal value="1" name="selfInfoForm" property="mainsort">
	</table>
		</div>
		</td></tr>
</logic:equal>
<tr>
  <td align="center"  nowrap colspan="4" class="AddTableRow" style="height:35px;">  
        <logic:equal name="selfInfoForm" property="setname" value="A01">
              <html:hidden name="selfInfoForm" property="tolastpageflagsub" value="no"/>
             <logic:equal name="selfInfoForm" property="actiontype" value="new">
                <html:hidden name="selfInfoForm" property="tolastpageflag" value="yes"/>
              </logic:equal>
            <logic:notEqual name="selfInfoForm" property="actiontype" value="new">
               <html:hidden name="selfInfoForm" property="tolastpageflag" value="no"/>
             </logic:notEqual>
          </logic:equal>
             <logic:notEqual name="selfInfoForm" property="setname" value="A01">
                 <logic:equal name="selfInfoForm" property="actiontype" value="new">
                    <html:hidden name="selfInfoForm" property="tolastpageflagsub" value="yes"/>
                 </logic:equal>
                 <logic:notEqual name="selfInfoForm" property="actiontype" value="new">
                   <html:hidden name="selfInfoForm" property="tolastpageflagsub" value="no"/>
                 </logic:notEqual>
              </logic:notEqual>
           <logic:equal name="selfInfoForm" property="setprv" value="2">
             <logic:equal name="selfInfoForm" property="setname" value="A01">
                 <input type="button" id="saveAndAddButton" class="mybutton" value="<bean:message key="button.insert"/>" onclick="saveAndAdd();"/>
              </logic:equal>
            </logic:equal>

             <logic:equal name="selfInfoForm" property="setprv" value="2">
             <logic:equal name="selfInfoForm" property="setname" value="A01">
                <input type="button" id="savePerson" class="mybutton" value="<bean:message key="button.save"/>" onclick="saveData();" />
	         </logic:equal>              
               <logic:notEqual name="selfInfoForm" property="setname" value="A01">
              <input type="button" id="saveButton" class="mybutton" onclick="saves();" value="<bean:message key="button.save"/>"/>
              </logic:notEqual>
             </logic:equal>
              <logic:equal name="selfInfoForm" property="setprv" value="3">
             <logic:equal name="selfInfoForm" property="setname" value="A01">
	             <input type="button" id="savePerson" class="mybutton" value="<bean:message key="button.save"/>" onclick="saveData();" />
		     </logic:equal>              
               <logic:notEqual name="selfInfoForm" property="setname" value="A01">
                <input type="button" id="saveButton" class="mybutton" value="<bean:message key="button.save"/>" onclick="saves();" />
              </logic:notEqual>
             </logic:equal>                    
              <hrms:tipwizardbutton flag="emp" target="il_body" formname="selfInfoForm"/> 
             
  </td>
 </tr>    
 </table> 
 <%}else{%>
 <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
 <br>
 <br>
 <tr>  
     <td align="center"  nowrap class="AddTableRow">
        <bean:message key="workbench.info.nomainfield"/>
     </td>
    
 </tr>    
 </table>  
 <%}%>
 <div id="date_panel">
   			<select name="date_box" multiple="multiple" size="10"  style="width:200" onchange="setSelectValue();" onclick="setSelectValue();">    
		            <option value="1992.4.12">1992.4.12</option>	
                            <option value="1992.4">1992.4</option>	
                            <option value="1992">1992</option>			    
                       </select>
                    </div>
</html:form>
</body>

<div id=dict style="border-style:nono;position:absolute;">
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
     <tr>
     <td>
       <select name="dict_box" multiple="multiple" size="10" class="dropdown_frame" style="width:200" ondblclick="setSelectCodeValue();" onkeydown="return inputType(this,event)"  onblur="Element.hide('dict');">    
       </select>
     </td>
     </tr>
</div>
<script type="text/javascript" src="/workbench/info/editselfinfo.js"></script>
<script language="javascript">

reloadMenu("${selfInfoForm.a0100}","${selfInfoForm.setname}","");

</script> 
 
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
   
   var dropDownList=createDropDown("dropDownList");
   var __t=dropDownList;
   __t.type="list";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);   
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
    __t.readFields="codeitemid";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
</script>
<logic:notEmpty name="selfInfoForm" property="msg">
<script language='javascript'>
alert('<bean:write  name="selfInfoForm" property="msg"/>');
</script>
</logic:notEmpty> 
<script language="javascript">
   Element.hide('date_panel');
   Element.hide('dict');
   getfirstfocuse();
   function getfirstfocuse(){
   var objsss=document.getElementsByTagName("input");
   
   for(var i=0;i<objsss.length;i++){
   var dobj=objsss[i];
   if(dobj.type=="text"&&dobj.className!="editor"){
   	 dobj.focus();
   	 return;
   }
   }   
   }
   	var inputs = document.getElementsByTagName("input");
	for ( var i = 0; i < inputs.length; i++) {
		if(inputs[i].getAttribute("type")=="text"){
			inputs[i].setAttribute("autocomplete","off");
		}
	}
	
	var valueInputsun=document.getElementsByName("<%=orgtemp%>");
	if(valueInputsun && valueInputsun.length>0)
		document.getElementById("deptId").setAttribute("parentid",valueInputsun[0].value);
	
    var valueInputsum=document.getElementsByName("<%=postemp%>");
    if(valueInputsum && valueInputsum.length>0){
        if(valueInputsum[0].value)  
            document.getElementById("jobId").setAttribute("parentid",valueInputsum[0].value);
        else
            document.getElementById("jobId").setAttribute("parentid",valueInputsun[0].value);      
    }
	
</script>