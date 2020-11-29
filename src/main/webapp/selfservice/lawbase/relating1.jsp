<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<script language="javascript" src="/ajax/common.js"></script>
<%@ page import="java.util.ArrayList,				 
				 com.hjsj.hrms.actionform.lawbase.LawBaseForm"%>
<%

		LawBaseForm lawbaseForm=(LawBaseForm)session.getAttribute("lawbaseForm");
		String checkFlag = (String)lawbaseForm.getCheckflag();
		String dbpre_str = (String)lawbaseForm.getDbpre_str();
				
%>

<STYLE type=text/css>
.div2
{
 overflow:auto; 
 width: 200px;height: 230px;
 line-height:15px; 
 border-width:1px; 
 border-style: groove;
 border-width :thin;
}
</STYLE>
<script language="javascript"><!--
   function savefield()
  {  	  
     var checkflag = "${lawbaseForm.checkflag}";  
     var vos= document.getElementById("right");       
     if(vos==null)
     {
       alert("已选指标项不能为空！");
       return false;
     }else
     {
        var code_fields=new Array();        
        for(var i=0;i<vos.length;i++)
        {
          var valueS=vos.options[i].value;          
          code_fields[i]=valueS;
        }       
     }
     if(checkflag=="0"){
     	var hashvo=new ParameterSet();   
     	hashvo.setValue("code_fields",code_fields);
     	hashvo.setValue("a_id",'${lawbaseForm.a_id}'); 
     	var request=new Request({method:'post',onSuccess:showSelect,functionId:'10400201042'},hashvo);
     }else{
    	 if(parent && parent.parent && parent.parent.Ext && parent.parent.chaosong_ok){
    		 parent.parent.chaosong_ok(code_fields);
    		 parent.parent.chaosongWinClose();
    	 } else {
    	 	if(getBrowseVersion()){
    	 		parent.window.returnValue=code_fields;
    	 	}else{
    	 		parent.opener.openReturn_vo(code_fields);
    	 	}
	     	parent.window.close();
    	 }
     }
   }	
   function showSelect(outparamters)
   { 
	closeWin();
   }  
   function getemp()
  {
    var targetobj,hiddenobj;
    var currnode=Global.selectedItem;	
    if(currnode==null)
    	return;  
    var id = currnode.uid;
    var text=currnode.text;
    if(id.indexOf("UN")!=-1||id.indexOf("UM")!=-1||id.indexOf("@K")!=-1||id.indexOf("root")!=-1)
      return;  
    var no = new Option();
    no.value=id;
    no.text=text;
    var vos= document.getElementsByName('right_fields');
    var emp_vo=vos[0];
    var isC=true;
    for(i=0;i<emp_vo.options.length;i++)
    {
       var select_ob=emp_vo.options[i];
       if(id==select_ob.value)
       {
          isC=false;
       }
    }
    if(isC)
    {
      emp_vo.options[emp_vo.options.length]=no;
    } 
  }
  
	function search() {
	    var db_arr = new Array();
	    var hashvo = new ParameterSet();
	    var persons = null;
	    var chkflag = "${lawbaseForm.checkflag}";
	    var dbpre = "${lawbaseForm.dbpre_str}";
	    if (chkflag == '8') {
	        db_arr = dbpre.split(",");
	    } else {
	        db_arr[0] = "Usr";
	    }
	    if (dbpre.length == 0) { ///dbpre为空时 默认为在职人员库
	        db_arr = "Usr";
	    }
	    if(getBrowseVersion()){
	    	persons = common_query_pri("1", db_arr, "1", "0");
	   		if (persons != null && persons.length > 0) {
	        	hashvo.setValue("persons", persons);
	        	var request = new Request({
	            	method: 'post',
	            	onSuccess: getPersonlist,
	            	functionId: '10400201043'
	        		},
	        	hashvo);
	    	}
	    }else{//非IE浏览器 使用open弹窗 
	    	common_query_pri("1", db_arr, "1", "0");
	    }
	    
	}
	//非IE浏览器 open 弹窗 返回数据方法   wangb 20180209 
  	function openReturn(objlist){
  		var persons = common_query_pri_returnvalue(objlist);
  		if (persons != null && persons.length > 0) {
  				var hashvo = new ParameterSet();
	        	hashvo.setValue("persons", persons);
	        	var request = new Request({
	            	method: 'post',
	            	onSuccess: getPersonlist,
	            	functionId: '10400201043'
	        		},
	        	hashvo);
	    }
  	}
  function getPersonlist(outparamters)
  {
  	var personlist = new Array();
  	personlist = outparamters.getValue("personlist");
  	var personname = outparamters.getValue("personname");
  	
  	
  	for(i=0;i<personlist.length;i++)
  	{
  		var vos= document.getElementsByName('right_fields');
		var emp_vo=vos[0];
		var no = new Option();
  		var id;
  		id = personlist[i];
  		no.value= id;
    	no.text=personname[i];
	    var isC=true;
	    for(j=0;j<emp_vo.options.length;j++)
	    {
	       var select_ob=emp_vo.options[j];
	       if(id==select_ob.value)
	       {
	          isC=false;
	       }
	    }
	    if(isC)
		{
		      emp_vo.options[emp_vo.options.length]=no;
		} 
	}
}
	var date_desc;
	function setSelectValue()
	{
		if(date_desc)
		{
			var no = new Option();
		    no.value=$F('date_box');
		    var aaa = $('date_box');
		    var text = aaa[aaa.selectedIndex].text;
		    no.text=text;
		    var vos= document.getElementsByName('right_fields');
		    var emp_vo=vos[0];
		    var isC=true;
		    for(i=0;i<emp_vo.options.length;i++)
		    {
		       var select_ob=emp_vo.options[i];
		       if($F('date_box')==select_ob.value)
		       {
		          isC=false;
		       }
		    }
		    if(isC)
		    {
		      emp_vo.options[emp_vo.options.length]=no;
		    }
		    var name = $('selectname'); 
		    name.value ="";
			Element.hide('date_panel'); 
		}
	}
	function showDateSelectBox(srcobj)
   {
   		if($F('selectname')=="")
   		{
   			Element.hide('date_panel');
   			return false ;
   		}
      date_desc=document.getElementById(srcobj);
      var pos=getAbsPosition(date_desc);
	  with($('date_panel'))
	  {
	  		style.position="absolute";
			style.left=(pos[0]-1)+"px";
			var IE7 = /msie 7.0/i.test(navigator.userAgent) ? true : false;
			var IE8 = /msie 8.0/i.test(navigator.userAgent) ? true : false;
			var IE9 = /msie 9.0/i.test(navigator.userAgent) ? true : false;
			//ie9以下得到的pos[1]有差别
			if(IE7||IE8||IE9){
				style.top=(pos[1]-100-date_desc.offsetHeight)+"px";
			}else{
				style.top=(pos[1]-80-date_desc.offsetHeight)+"px";
			}
			var width = (date_desc.offsetWidth<150)?150:date_desc.offsetWidth+1;
			style.width=width+"px"; 
      }
      var hashvo = new ParameterSet();
      hashvo.setValue("selname",getEncodeStr(date_desc.value));
      hashvo.setValue("checkflag","${lawbaseForm.checkflag}");
      hashvo.setValue("dbpre_str","${lawbaseForm.dbpre_str}");
      var request=new Request({method:'post',onSuccess:shownamelist,functionId:'10400201045'},hashvo);
   }
   function shownamelist(outparamters)
   {
   		var namelist=outparamters.getValue("namelist");
		if(namelist.length==0){
			Element.hide('date_panel');
		}
		else{
			AjaxBind.bind(lawbaseForm.contenttype,namelist);
			//wangb 20180123 add  解决chrome 浏览器中鼠标移动到select选项中下拉框隐藏
			var options = document.getElementById('date_panel').getElementsByTagName('option');
			for(var i = 0; i < options.length ; i++){
				options[i].setAttribute('onmouseover','show()');
			}
			Element.show('date_panel');
		}
   }
   
   function remove()
    {
    	Element.hide('date_panel');
    }
   function show()
    {
    	Element.show('date_panel');
    }
	function closeWin(){
	 if(parent && parent.parent && parent.parent.Ext && parent.parent.chaosongWinClose){
		 parent.parent.chaosongWinClose();
	 }else {
		 parent.window.close();
	 }
	}
--></script>
<html:form action="/selfservice/lawbase/add_law_text_role"><br/>
<table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0" class="" style="border-collapse:separate;">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" style="border-bottom: 0px;" nowrap colspan="3">
		<bean:message key="general.impev.copy"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
              <table>
                <tr>
                 <td align="center"  width="46%">
                   <table align="center" width="100%">
                    <tr>
                    <td align="left">
                     <bean:message key="wd.lawbase.standbypersonnel"/>&nbsp;&nbsp;
                    </td>
                    </tr>                   
                   <tr>
                    <td align="center">
                    	<div align="left" id="tbl_container" ondblclick="getemp();" class="div2" >
                    	<%
							if((checkFlag!=null && checkFlag.trim().length()>0 && checkFlag.equalsIgnoreCase("11")) && (dbpre_str!=null && dbpre_str.trim().length()>0)){
				   		%>
              				<hrms:orgtree flag="1" showroot="false" dbtype="0" priv="${lawbaseForm.priv}" dbpre="${lawbaseForm.dbpre_str}" target="app"/>
              			<%}else{%>
              				<hrms:orgtree flag="1" showroot="false" dbtype="0" priv="${lawbaseForm.priv}" target="app"/>
              			<%}%>
           				</div>
                    </td>
                    
                    </tr>
                   
                   </table>
                </td>
               
                <td width="8%" align="center" >
				<html:button  styleClass="mybutton" property="b_addfield" onclick="getemp();">
            		     <bean:message key="button.setfield.addfield"/>    
	           </html:button>
				<br><br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button>	     
                </td>         
                
                <td width="46%" align="center" >
                 
                 
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     <bean:message key="wd.lawbase.alreadypickpersonnel"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
                  <hrms:optioncollection name="lawbaseForm" property="selectrname" collection="selectedlist"/> 
     	             <html:select property="right_fields" size="10" multiple="true" style="height:200px;width:100%;font-size:9pt"  styleId="right" ondblclick="removeitem('right_fields');">
                        <html:options collection="selectedlist"  property="dataValue" labelProperty="dataName"/>
                     </html:select>   
                  </td>
                  </tr>
                  <TR>
                  	<td>
                  		<bean:message key="hire.employActualize.name"/>&nbsp;
                  		<html:text styleId="selectname" name="lawbaseForm" property="selectname" size="11" maxlength="30" onkeyup="showDateSelectBox('selectname')" styleClass="inputtext"/>&nbsp;&nbsp;
                  	</td>
                  </TR>
                  </table>             
                </td>
                     
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap style="border-top:0px;" colspan="3" style="height: 35px;">
             <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick=" savefield();">
	         <input type="button" name="button"  value='<bean:message key="button.query"/>' class="mybutton" onclick="javascript:search();">
		     <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' class="mybutton" onclick="closeWin();">
	     
          </td>
          </tr>
</table>
	<div id="date_panel" style="display:none;" onmouseout="remove();"  onmouseover="show();">

		<select id="date_box" name="contenttype" multiple="multiple"  style="width:140" size="6" ondblclick="setSelectValue();">
        </select>
	 </div>
</html:form>
