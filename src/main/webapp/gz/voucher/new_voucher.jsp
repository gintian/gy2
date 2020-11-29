<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
<%@ page import="com.hjsj.hrms.actionform.gz.voucher.VoucherForm,java.util.*,com.hrms.hjsj.sys.VersionControl,org.apache.commons.beanutils.LazyDynaBean,java.lang.*,com.hrms.struts.constant.WebConstant,
com.hrms.struts.valueobject.UserView"%>
<%
    VoucherForm voucherForm=(VoucherForm)session.getAttribute("financial_voucherForm"); 
    ArrayList salarySelectedList=voucherForm.getSalarySelectedList();
    ArrayList dbselectedList=voucherForm.getDbSelectedList();
    ArrayList salarysetList =voucherForm.getSalarysetList();
    ArrayList list =voucherForm.getList();
    String contains="";
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
    
%>
<script language="Javascript" src="/gz/salary.js"/></script>
<script language="javascript" src="/js/dict.js"></script>
<script language="javascript">
var temp=dialogArguments;
function save(){
        var jiekou="";
        var huizong=document.getElementsByName("huizong");
        for(var i=0;i<huizong.length;i++){      
            if(huizong[i].checked==true){
                jiekou=huizong[i].value;
            }
        }
        var name=document.getElementById("ming").value;
        if(name==""){
            alert("凭证名称不能为空！");
            return false;
        }
        var leibie=document.getElementById("leibie").value;
        var salarySetArray =getSalaryId();

        var dbid=getDbId();
        var web1 =document.getElementById("web1").value;
        var web2 =document.getElementById("web2").value;
        if(salarySetArray==""){
            alert("请选择薪资类别！");
            return false;
        }
        if(dbid==""){
            alert("请选择人员库！");
            return false;
        }
        var hashvo=new ParameterSet();
        hashvo.setValue("flag","new");
        hashvo.setValue("jiekou",jiekou);
        hashvo.setValue("name",name);
        hashvo.setValue("leibie",leibie);
        hashvo.setValue("salarySetArray",salarySetArray);
        if(jiekou==1){
         	var checkbox = document.getElementById('checkboxid');
        	var selectbox = document.getElementById('selectbox');
        	var selvalue = selectbox.value;
        	
        	if(!checkbox.checked&&selectbox.value!=""){
        		alert("请勾选双币凭证！");
        		return false;
        	}
        	var c_code =document.getElementById("c_code").value;
	
        	var huizongItem =document.getElementById("zhibiao1").value;
            var voucherItem =document.getElementById("zhibiao2").value;
            if(checkbox.checked)
            	hashvo.setValue("is_dual_money",'true');
            else{
            	hashvo.setValue("is_dual_money",'false');
            	selvalue = "";
            }
            hashvo.setValue("huizongItem",huizongItem);
            hashvo.setValue("voucherItem",voucherItem);
            hashvo.setValue("c_code",c_code);
            hashvo.setValue("ratevalue",selvalue);
        }
        
        hashvo.setValue("dbid",dbid);   
        hashvo.setValue("web1",web1);
        hashvo.setValue("web2",web2);
        hashvo.setValue("b0110",document.getElementById("b0110").value);
        var request=new Request({method:'post',asynchronous:false,
        parameters:null,onSuccess:saveOk,functionId:'3020073008'},hashvo);
}
function saveOk(outparameters){
    var pn_id=outparameters.getValue("pn_id");
    var c_name=outparameters.getValue("c_name");
    var interface_type=outparameters.getValue("interface_type");
    var re=new Array();
    re[0]=pn_id;
    re[1]=c_name;
    re[2]=interface_type;
    window.returnValue=re;
    window.close();
}
function update(){
        var jiekou="";
        var huizong=document.getElementsByName("huizong");
        for(var i=0;i<huizong.length;i++){      
            if(huizong[i].checked==true){
                jiekou=huizong[i].value;
            }
        }
        var name=document.getElementById("ming").value;
        var leibie=document.getElementById("leibie").value;
        var salarySetArray =getSalaryId();
        var dbid=getDbId();
        var web1 =document.getElementById("web1").value;
        var web2 =document.getElementById("web2").value;
        if(salarySetArray==""){
            alert("请选择薪资类别！");
            return false;
        }
        if(dbid==""){
            alert("请选择人员库！");
            return false;
        }
        var hashvo=new ParameterSet();
        hashvo.setValue("flag","updated");
        hashvo.setValue("pnid",temp[0]);
        hashvo.setValue("jiekou",jiekou);
        hashvo.setValue("name",name);
        hashvo.setValue("leibie",leibie);
        hashvo.setValue("salarySetArray",salarySetArray);
        if(jiekou==1){
        	var c_code =document.getElementById("c_code").value;
         	var checkbox = document.getElementById('checkboxid');
        	var selectbox = document.getElementById('selectbox');
        	var selvalue = selectbox.value;
        	if(!checkbox.checked&&selectbox.value){
        		alert("请勾选双币凭证！");
        		return false;
        	}
            var huizongItem =document.getElementById("zhibiao1").value;
            var voucherItem =document.getElementById("zhibiao2").value;
            if(checkbox.checked)
            	hashvo.setValue("is_dual_money",'true');
            else{
            	hashvo.setValue("is_dual_money",'false');
            	selvalue = "";
            	
            }
            hashvo.setValue("huizongItem",huizongItem);
            hashvo.setValue("voucherItem",voucherItem);
            hashvo.setValue("ratevalue",selvalue);
            hashvo.setValue("c_code",c_code);
        }
        hashvo.setValue("dbid",dbid);   
        hashvo.setValue("web1",web1);
        hashvo.setValue("web2",web2);
        hashvo.setValue("b0110",document.getElementById("b0110").value);
        var request=new Request({method:'post',asynchronous:false,
        parameters:null,onSuccess:saveOk,functionId:'3020073008'},hashvo);
}
function updateOk(outparameters){
    var pn_id=outparameters.getValue("pn_id");
    var c_name=outparameters.getValue("c_name");
    var interface_type=outparameters.getValue("interface_type");
    var re=new Array();
    re[0]=pn_id;
    re[1]=c_name;
    re[2]=interface_type;
    window.returnValue=re;
    window.close();
}
function set(){
        var id =getSalaryId();  
        if(id==""){
            alert("请选择薪资类别！");
            return false;
        }
        var zhib=document.getElementById("zhibiao1").value;
          var target_url="/gz/voucher/financial_voucher.do?b_set=link`zhib="+zhib+"`salaryId="+id;
          var url="/gz/voucher/iframe_set.jsp?src="+$URL.encode(target_url);
          var return_vo=window.showModalDialog(url,null,"dialogWidth:500px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no"); 
          if(return_vo==null){
                return ;
          }else{
          document.getElementById("zhibiao").value=return_vo[1];
          document.getElementById("zhibiao1").value=return_vo[0];
          var zhib2 = document.getElementById("zhibiao2").value;
          if(zhib2==""){
              var xiangmuList=document.getElementById("xiangmufields").options;
              for(var i=0;i<xiangmuList.length;i++){
            	  zhib2=zhib2+xiangmuList[i].value+",";
              }
              zhib2=zhib2.substring(0,zhib2.length-1);
          }
          zhib2=zhib2+","+return_vo[0];
          document.getElementById("zhibiao2").value=zhib2;
          }     
}
function getSalaryId1(){//点击薪资类别 对汇率指标作数据联动
    var idArr = document.getElementsByName("salarySetArray");
    var id="";
    for(var i=0;i<idArr.length;i++){
        if(idArr[i].type=="checkbox"&&idArr[i].checked==true){
            id=id+idArr[i].value+",";
        }
    }
    var jiekou="";
    var huizong=document.getElementsByName("huizong");
    for(var i=0;i<huizong.length;i++){      
        if(huizong[i].checked==true){
            jiekou=huizong[i].value;
        }
    }
    if(1==jiekou){
    	var hashVo=new HashMap();
    	if(temp[0])
        	hashVo.put("pnid",temp[0]);
    	hashVo.put("salarySetArray",id);
    	hashVo.put("flag",'getRatelist');
	    Rpc({functionId:'3020073008',async:true,success:reflashRatelist},hashVo);
    }
}
 function reflashRatelist(outparameters){
 	 var res=Ext.decode(outparameters.responseText);
	 var rateList =res.rateList;
	 var rate =res.rate;
	 addOptions(rateList,rate,'');
}  
function addOptions(rateList,rate,isDualMoney){
	 var select = document.getElementById('selectbox');
	 var checkbox = document.getElementById('checkboxid');
	 select.options.length=0;//重新塞数据前，将历史数据清空
	 select.options.add(new Option(" "," "));
	 for(var i=0;i<rateList.length;i++){
		var data =rateList[i];
		select.options.add(new Option(data.dataName,data.dataValue)); 
	}
	 if("true"==isDualMoney)
		 checkbox.checked=true;
	 else if("false" == isDualMoney)
		 checkbox.checked=false;
	 
 	 if(rate.dataValue=='none'){
		 select.value = "";
		 select.label = "";
	 }else{
		 select.value = rate.dataValue;
		 select.label = rate.dataName;
	 } 
}
function checkstatus(){
	 var select = document.getElementById('selectbox');
	 var checkbox = document.getElementById('checkboxid');
	 if(!checkbox.checked){
		 select.value="";		 
		 select.label="";		 
	 }
}
function getSalaryId(){
    var idArr = document.getElementsByName("salarySetArray");
    var id="";
    for(var i=0;i<idArr.length;i++){
        if(idArr[i].type=="checkbox"&&idArr[i].checked==true){
            id=id+idArr[i].value+",";
        }
    }
    return id;
}
function getDbId(){
    var idArr = document.getElementsByName("dbValue");
    var id="";
    for(var i=0;i<idArr.length;i++){
        if(idArr[i].type=="checkbox"&&idArr[i].checked==true){
            id=id+idArr[i].value+",";
        }
    }
    return id;
}
function tempset(){
        
        var id =getSalaryId();
        var item = document.getElementById("zhibiao1").value;
        var zhibiao2=document.getElementById("zhibiao2").value;
        if(zhibiao2==""){
            var xiangmuList=document.getElementById("xiangmufields").options;
            for(var i=0;i<xiangmuList.length;i++){
                zhibiao2=zhibiao2+xiangmuList[i].value+",";
            }
        }
        var target_url="/gz/voucher/financial_voucher.do?b_tempset=link`zhibiao2="+zhibiao2+"`item="+item+"`pnid="+temp[0]+"`salaryId="+id;
        var url="/gz/voucher/iframe_tempset.jsp?src="+$URL.encode(target_url);
        if(isIE6()){
         var return_vo=window.showModalDialog(url,null,"dialogWidth:520px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no"); 
        }else{
         var return_vo=window.showModalDialog(url,null,"dialogWidth:500px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no"); 
        }       
        if(return_vo==null){
                return ;
        }else{
            var hashvo=new ParameterSet();
            hashvo.setValue("key",return_vo[0]);
            hashvo.setValue("value",return_vo[1]);
            var request=new Request({method:'post',asynchronous:false,
            parameters:null,onSuccess:okOk,functionId:'3020073037'},hashvo);
        }       
}
function okOk(outparameters){
    var keys=outparameters.getValue("key");
    var liat=outparameters.getValue("list");
    AjaxBind.bind(financial_voucherForm.xiangmufields,liat);
    document.getElementById("zhibiao2").value=keys;
}
function on(){
        var jiekou="";
        var huizong=document.getElementsByName("huizong");
        for(var i=0;i<huizong.length;i++){      
            if(huizong[i].checked==true){
                jiekou=huizong[i].value;
            }
        }
        var aa=document.getElementById("ww");
        if(jiekou==1){
            document.getElementById('xiangmu').style.display = "none";
        }else if(jiekou==2){
            document.getElementById('xiangmu').style.display = "block";
        }
}
 function show(obj){
        var pos=getAbsPosition(obj);    
        document.getElementById("sec_type").style.posLeft=pos[0]-1;
        document.getElementById("sec_type").style.posTop=pos[1]-1+obj.offsetHeight;
        document.getElementById("sec_type").style.width=(obj.offsetWidth<150)?150:obj.offsetWidth+1;
        document.getElementById("c_typeselect").style.width=(obj.offsetWidth<150)?150:obj.offsetWidth+1;
        document.getElementById("sec_type").style.display="block";          
}  
function remove(){
    var act = document.activeElement.id;
    if(act!="c_typeselect"){
        document.getElementById("sec_type").style.display="none";
    }
}
function getc_type(obj){    
    var dd = obj.selectedIndex;
    if(dd==-1){
        var item=document.getElementById("leibie");
        item.focus();
        return false;
    }
    var textValue=obj.options[obj.selectedIndex].text;
    document.getElementById("sec_type").style.display="none";
    var item = document.getElementById("leibie");
    item.value=textValue;
    item.blur();
}
function setMonthCount(){
    temp=new Array(null);
    var jiekou="";
    var huizong=document.getElementsByName("huizong");
    for(var i=0;i<huizong.length;i++){      
        if(huizong[i].checked==true){
            jiekou=huizong[i].value;
        }
    }
    
    var name=document.getElementById("ming").value;
    var leibie=document.getElementById("leibie").value;
    
    var dbid=getDbId();
    var salarySetArray =getSalaryId();
    var web1 =document.getElementById("web1").value;
    var web2 =document.getElementById("web2").value;
    document.financial_voucherForm.c_name.value=name;
    document.financial_voucherForm.c_type.value=leibie;
    document.financial_voucherForm.dbid.value=dbid;
    document.financial_voucherForm.webURL.value=web1;
    document.financial_voucherForm.webFunction.value=web2;
    document.financial_voucherForm.interface_type.value=jiekou;
    document.financial_voucherForm.resalarySetArray.value=salarySetArray;           
    if(jiekou==2){  
        var huizongItem =document.getElementById("zhibiao1").value;
        var voucherItem =document.getElementById("zhibiao2").value; 
        document.financial_voucherForm.huizongItem.value=huizongItem;
        document.financial_voucherForm.voucherItem.value=voucherItem;
    }
    document.financial_voucherForm.action="/gz/voucher/financial_voucher.do?br_add=link&itFlag=2";//itFlag 0:新增不刷新 1:修改式刷新 2:切换式刷新
    document.financial_voucherForm.submit();
}
</script>



<style>
	body{
		overflow:hidden;
	}
</style>
<%if(voucherForm.getFlagtemp().equals("new")){ %>
<body  >
<%}else{ %>
<body  onload="reflash()" >
<%} %>
<html:form action="/gz/voucher/financial_voucher">
<html:hidden  name="financial_voucherForm" property="pn_id" />
<html:hidden  name="financial_voucherForm" property="interface_type" />
<html:hidden  name="financial_voucherForm" property="flag" />
<html:hidden name="financial_voucherForm" property="c_name" />
<html:hidden name="financial_voucherForm" property="c_type" />
<html:hidden name="financial_voucherForm" property="dbid" />
<html:hidden name="financial_voucherForm" property="resalarySetArray" />
<html:hidden name="financial_voucherForm" property="webURL" />
<html:hidden name="financial_voucherForm" property="webFunction" />
<html:hidden name="financial_voucherForm" property="huizongItem" />
<html:hidden name="financial_voucherForm" property="voucherItem" />	
<html:hidden name="financial_voucherForm" property="itFlag"/>
<table style="position:relative; left:20px;">
<tr><td>
<%if("hl".equals(hcmflag)){ %>
 <div style="height:115px;width:350px; overflow: auto">
 <br>
<%}else{ %>
 <div style="height:130px;width:350px; overflow: auto"><!-- 由于双币凭证显示不全  将95px改成90px -->
<%} %>
	    

				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
					<tr>
						<td align="right" class="" nowrap width="100"><bean:message key="gz.voucher.FinanceInterface" />：</td>
						<logic:equal name="financial_voucherForm" property="interface_type" value="2">
							<td align="center" class="" nowrap width="100"><input type="radio" name ="huizong"  value="2" checked onclick=""/><bean:message key="gz.voucher.MonthlySummary" /></td>
							<td align="center" class="" nowrap width="100"><input type="radio" name ="huizong"  value="1"  onclick="setMonthCount()"/><bean:message key="gz.voucher.FinancialDocuments" /></td>
						</logic:equal >
						<logic:notEqual name="financial_voucherForm" property="interface_type" value="2">
							<td align="center" class="" nowrap width="100"><input type="radio" name ="huizong"  value="2"  onclick="setMonthCount()"/><bean:message key="gz.voucher.MonthlySummary" /></td>
							<td align="center" class="" nowrap width="100"><input type="radio" name ="huizong"  value="1"  checked onclick=""/><bean:message key="gz.voucher.FinancialDocuments" /></td>
						</logic:notEqual>
					</tr>
					<tr>
						<logic:notEqual name="financial_voucherForm" property="interface_type" value="2">
						<td align="right" nowrap width="100">账簿编号：</td><!-- xiegh -->
							<td colspan="2" align="center">
							<input type="text" id ="c_code"  style="width:230px" class="inputtext"/>
						</td>
						</logic:notEqual>
					</tr>
					<tr>
						<td align="right" nowrap width="100"><bean:message key="gz.voucher.name" />：</td>
						<td colspan="2" align="center"><input type="text" name ="ming"  style="width:230px" class="inputtext"/></td>
					</tr>
					<tr>
						<td align="right" nowrap width="100"><bean:message key="gz.voucher.type" />：</td>
						<td colspan="2" align="center"><input type="text" id="leibie" name ="leibie"  style="width:230px" onfocus="show(this)" onblur="remove()" class="inputtext"/></td>
					</tr>
					<tr>
						<td align="right" nowrap width="100"><bean:message key="lable.lawfile.ascriptionunit" />：</td>
						<td colspan="2" align="center">
							<html:select name="financial_voucherForm" property="b0110"   style="width:230px"   size="1" >
								<html:optionsCollection property="b0110List" value="dataValue" label="dataName"/>
							</html:select>
						</td>
					</tr>
				</table>

 		 </div>
</td></tr>
</table>
<logic:equal name="financial_voucherForm" property="interface_type" value="2">
	<hrms:tabset name=""  width="45%" height="68%" type="false" align="right"> 

 <hrms:tab name="param3" label="类别" visible="true" >
 
 
 <table width="100%" height='100%' align="left"> 
 <tr>
 <td>
<div id="presalary" style="overflow:auto; width:100%; height:100%; BORDER-TOP: 1px inset; BORDER-LEFT: 1px inset;  BORDER-BOTTOM: 3px inset; BORDER-RIGHT: 2px inset; position:relative; top:0px;">
<table width="300px" align="left" border="0" cellpadding="0" cellspacing="0">
	<logic:iterate id="salaryset" name="financial_voucherForm" property="salarysetList" indexId="index">
		<tr>
			<td width="100%" align="left" nowrap>
				<input type="checkbox" name="salarySetArray" id="<bean:write name="salaryset" property="salaryid"/>" onclick="getSalaryId()" value="<bean:write name="salaryset" property="salaryid"/>"/><bean:write name="salaryset" property="cname"/>
			</td>
		</tr>
	</logic:iterate>
</table>
</div>
</td>
</tr>
	<logic:notEqual name="financial_voucherForm" property="interface_type" value="2">
		<tr>
			<td>
				<div id="salcount">
				<bean:message key="gz.voucher.SummaryFileditem" />：<input type="text" name ="zhibiao" size="30"  class="inputtext"/><input type="button" value="..." onclick="set();" class="mybutton" />
							<input type="hidden" name ="zhibiao1" size="30" />
				</div>
			</td>
		</tr>
	</logic:notEqual>
</table>
 </hrms:tab>
  <hrms:tab name="param1" label="人员库" visible="true" >
  <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0"> 
       <logic:iterate  id="element" name="financial_voucherForm" property="dbList" >
              <tr>
              <td width="20%" height="25" >
              <input type='checkbox' name='dbValue'  value='<bean:write name="element" property="pre" filter="true"/>' />   
                <bean:write name="element" property="dbname" filter="true"/></td>
              </tr>
        </logic:iterate>
	</table>
  </hrms:tab>
  <hrms:tab name="param2" label="凭证参数" visible="true" >
  				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="position:absolute; top:20px;">
					<tr>
						<td align="right" ><bean:message key="label.Webservice.address" />：</td>
						<td colspan="2" align="left"><input type="text" name ="web1" size="45"  class="inputtext"/></td>
					</tr>
					<tr>
						<td align="right" ><bean:message key="label.Webservice.name" />：</td>
						<td colspan="2" align="left"><input type="text" name ="web2" size="45"  class="inputtext"/></td>
					</tr>
				</table>
  </hrms:tab>

</hrms:tabset>
</logic:equal >	
<!-- 财务凭证开始-->
<logic:notEqual name="financial_voucherForm" property="interface_type" value="2">

<hrms:tabset name="" width="45%" height="300px" type="false"> 

 <hrms:tab name="param3" label="类别" visible="true" >
 
 
 <table width="100%" height="100%' align="left"> 
 <tr>
 <td height="96%">
<div id="presalary" style="overflow:auto; width:100%; height:100%; BORDER-TOP: 1px inset; BORDER-LEFT: 1px inset;  BORDER-BOTTOM: 3px inset; BORDER-RIGHT: 2px inset; position:relative; top:0px;">
<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0">
	<logic:iterate id="salaryset" name="financial_voucherForm" property="salarysetList" indexId="index">
		<tr>
			<td width="100%" align="left" nowrap>
				<input type="checkbox" name="salarySetArray" id="<bean:write name="salaryset" property="salaryid"/>" onclick="getSalaryId1()" value="<bean:write name="salaryset" property="salaryid"/>"/><bean:write name="salaryset" property="cname"/>
			</td>
		</tr>
	</logic:iterate>
</table>
</div>
</td>
</tr>
	<logic:notEqual name="financial_voucherForm" property="interface_type" value="2">
		<tr>
			<td>
				<div id="salcount" style ="margin-bottom:-1px;">
				<bean:message key="gz.voucher.SummaryFileditem" />&nbsp;<input type="text" name ="zhibiao" size="30"  class="inputtext" style="vertical-align: middle;height: 23px;"/>&nbsp;<input type="button" value="..." onclick="set();" class="mybutton" style="height: 23px;vertical-align:middle;" />
							<input type="hidden" name ="zhibiao1" size="30" />
				</div>
			</td>
		</tr>
	</logic:notEqual>
</table>
 </hrms:tab>
  <hrms:tab name="param4" label="凭证项目" visible="true" >
					<table width="100%" height='100%' align="left">
	                  <tr height="240px">
	                  <td width="100%" align="left">
	                   <hrms:optioncollection name="financial_voucherForm" property="xiangmuList" collection="list"/>
			            <html:select name="financial_voucherForm" size="10" property="xiangmufields" multiple="multiple" style="height:100%; width:100%; font-size:9pt">
			             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
			        	</html:select>	
	                  </td>
	                  </tr>
	                  <tr>
						<td>
						<input type="button" value="凭证项目设置..." onclick="tempset();" class="mybutton" />
						<input type="hidden" name ="zhibiao2" size="30" />
						</td>
						</tr>
                  </table>
  </hrms:tab>


  <hrms:tab name="param1" label="人员库" visible="true" >
  <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
       <logic:iterate  id="element" name="financial_voucherForm" property="dbList" >
              <tr>
              <td width="20%" height="25" >
              <input type='checkbox' name='dbValue'  value='<bean:write name="element" property="pre" filter="true"/>' />   
                <bean:write name="element" property="dbname" filter="true"/></td>
              </tr>
        </logic:iterate>
	</table>
  </hrms:tab>



  <hrms:tab name="param2" label="凭证参数" visible="true" >
  				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="position:absolute;top:20px;">
					<tr>
						<td align="right" ><bean:message key="label.Webservice.address" />：</td>
						<td colspan="2" align="left"><input type="text" name ="web1" size="45"  class="inputtext"/></td>
					</tr>
					<tr>
						<td align="right" ><bean:message key="label.Webservice.name" />：</td>
						<td colspan="2" align="left"><input type="text" name ="web2" size="45"  class="inputtext"/></td>
					</tr>
				</table>
  </hrms:tab>

</hrms:tabset>
</logic:notEqual>
<table id="selectprename"  style="position:absolute; left:415px; top:30px; z-index:10;"><tr>
<td id="querydesc" >
<%if(voucherForm.getFlagtemp().equals("update")){ %>
	<input type="button" name ="check" value="<bean:message key="button.ok" />" class="mybutton" onclick="update()"/>
<%}else{ %>
	<input type="button" name ="check" value="<bean:message key="button.ok" />" class="mybutton" onclick="save()"/>
<%} %>
	
</td>
</tr>
<tr>
<td><input type="button" name ="check" value="<bean:message key="button.cancel" />" class="mybutton" onclick="window.close()"/></td>
</tr></table>
		<div id="sec_type" style="display:none; bottom:2px; position:absolute; background-color:#FFFFFF;">
			<select id="c_typeselect" size="5" onclick="getc_type(this)" >
                 <logic:iterate  id="element" name="financial_voucherForm"  property="list">
                   <option value="<bean:write name='element'/>" > <bean:write name="element"/> </option>
                 </logic:iterate>
			</select>
		</div>	
		<logic:notEqual name="financial_voucherForm" property="interface_type" value="2">
			<div style="padding:2px 10px 0.5px 20px;margin-top:1px;">
				<input type = 'checkbox' id='checkboxid' onclick="checkstatus()" />双币凭证 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;汇率指标:<!--xiegh  -->	
			 <%-- 	<html:select name="financial_voucherForm" property="itemid" size="1" onchange="" style="width:150px" styleId = 'selectbox' >
  					 <html:optionsCollection property="rateList" value="dataValue" label="dataName"/>
				</html:select>   --%>
				<select style="width:150px" id = 'selectbox' ></select>
			</div>
		</logic:notEqual>
</html:form>
</body>
<script language="javascript">
function reflash(){
    var interface_type=document.financial_voucherForm.interface_type.value;
    var itFlag=document.financial_voucherForm.itFlag.value;
    if(itFlag==2){
        var pn_id=document.financial_voucherForm.pn_id.value;
        var c_name=document.financial_voucherForm.c_name.value;
        var c_type=document.financial_voucherForm.c_type.value;
        var dbid=document.financial_voucherForm.dbid.value;
        var web1=document.financial_voucherForm.webURL.value;
        var web2=document.financial_voucherForm.webFunction.value;
        var resalarySetArray=document.financial_voucherForm.resalarySetArray.value;
        var hashvo=new ParameterSet();
        
        hashvo.setValue("flag","reflsh");
        hashvo.setValue("pnid",pn_id);
        hashvo.setValue("interface_type",interface_type);
        hashvo.setValue("c_name",c_name);
        hashvo.setValue("c_type",c_type);
        hashvo.setValue("resalarySetArray",resalarySetArray);
        if(interface_type==1){
            var huizongItem=document.financial_voucherForm.huizongItem.value;
            var voucherItem=document.financial_voucherForm.voucherItem.value;
            hashvo.setValue("huizongItem",huizongItem);
            hashvo.setValue("voucherItem",voucherItem);
        }
        hashvo.setValue("dbid",dbid);   
        hashvo.setValue("web1",web1);
        hashvo.setValue("web2",web2);
        var request=new Request({method:'post',asynchronous:false,
        parameters:null,onSuccess:reflashOk,functionId:'3020073008'},hashvo);
    }
    if(itFlag==1){
        if(temp[0]!=null){
            var hashvo=new ParameterSet();
            hashvo.setValue("flag","update");
            hashvo.setValue("pnid",temp[0]);
            var request=new Request({method:'post',asynchronous:false,
            parameters:null,onSuccess:reflashOk,functionId:'3020073008'},hashvo);
        }
    }
}
function reflashOk(outparameters){
    var pn_id=outparameters.getValue("pn_id");
    var c_name=outparameters.getValue("c_name");
    var interface_type=outparameters.getValue("interface_type");
    var c_type=outparameters.getValue("c_type");
    if(c_type==null){
        c_type="";
    }
    var c_code=outparameters.getValue("c_code");
    if(c_code==null){
    	c_code="";
    }
    var c_dbase=outparameters.getValue("c_dbase");
    var c_scope=outparameters.getValue("c_scope");
    var web1=outparameters.getValue("web1");
    var web2=outparameters.getValue("web2");
    var voucherList=outparameters.getValue("voucherList");
    var huizongList=outparameters.getValue("huizongList");
    var voucherItem=outparameters.getValue("voucherItem");
    var collect_fields=outparameters.getValue("collect_fields");
    var privflag=outparameters.getValue("privflag");
    if(privflag==3){
    	document.getElementsByName("check")[0].disabled=true;
    }
    document.getElementById("ming").value=c_name;
    document.getElementById("leibie").value=c_type;
    if(1==interface_type)
    	document.getElementById("c_code").value=c_code;
    var b0110List=outparameters.getValue("b0110List");
    AjaxBind.bind(financial_voucherForm.b0110,b0110List);
 	document.getElementById("b0110").value=outparameters.getValue("b0110");
    var c_scope1 = c_scope.split(",");
    var salarySetArray=document.getElementsByName("salarySetArray");
    for(var i=0;i<salarySetArray.length;i++){
        for(var j=0;j<c_scope1.length;j++){
            if(salarySetArray[i].value==c_scope1[j]){
            salarySetArray[i].checked=true;
            }
        }       
    }
    var c_dbase1 = c_dbase.split(",");
    var dbValue=document.getElementsByName("dbValue");
    for(var i=0;i<dbValue.length;i++){
        for(var j=0;j<c_dbase1.length;j++){
            if(dbValue[i].value==c_dbase1[j]){
            dbValue[i].checked=true;
            }
        }       
    }
    
    document.getElementById("web1").value=web1;
    document.getElementById("web2").value=web2;
    if(interface_type==1){
    	var ratelist=outparameters.getValue("rateList");
    	var rate=outparameters.getValue("rate");
    	var isDualMoney=outparameters.getValue("isDualMoney");
        addOptions(ratelist,rate,isDualMoney);
        document.getElementById("zhibiao1").value=collect_fields;
        document.getElementById("zhibiao").value=huizongList;
        document.getElementById("zhibiao2").value=voucherItem;
        AjaxBind.bind(financial_voucherForm.xiangmufields,voucherList);
    }
        
}
</script>
