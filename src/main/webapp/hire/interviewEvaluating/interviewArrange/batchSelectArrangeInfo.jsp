<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
	<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT> 
	<script language="JavaScript" src="/js/popcalendar.js"></script>
	<SCRIPT Language="JavaScript">dateFormat='yyyy-mm-dd'</SCRIPT>
	<style>
		.TEXT_NB {
			BACKGROUND-COLOR:transparent;
			BORDER-BOTTOM: #94B6E6 1pt solid; 
			BORDER-LEFT: medium none; 
			BORDER-RIGHT: medium none; 
			BORDER-TOP: medium none;
		}
   </style>
	<SCRIPT LANGUAGE=javascript>
	
	
	//选择考官
	function selectEmployer(obj)
	{		
		 //var return_vo=select_org_emp_byname_dialog(1,1,0,0);
		 var target_url="/selfservice/lawbase/add_law_text_role.do?b_relating=link&pri=0&chkflag=8&z0501=batch";
  	    //var return_vo=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=560,height=370'); 
		var return_vo='';
       	if(isIE6()){
       	 return_vo= window.showModalDialog(target_url,null,"dialogWidth:600px; dialogHeight:390px;resizable:no;center:yes;scroll:yes;status:no");
       	}else{
		 return_vo= window.showModalDialog(target_url,null,"dialogWidth:560px; dialogHeight:370px;resizable:no;center:yes;scroll:yes;status:no");
       	}
		 var z0501s=""; 	
		 if(return_vo)
		 {
		 	var a_textValue="";
		 	var textValue=return_vo.title.split(",");
		 	var contents=return_vo.content.split(",");
		 	var num=0;
		 	for(var i=0;i<contents.length;i++)
		 	{	
			 	    if(contents[i]=='')
			 	    {
			 	      continue;
			 	    }
			 	    //选考官 对于人名（00xxx） 只取人名展示
			 		if(textValue[i].indexOf("(")!=-1){
			 			textValue[i]=textValue[i].substring(0,textValue[i].indexOf("("));
			 		}
		 			a_textValue+=textValue[i]+",";
		 			z0501s+=contents[i]+",";	
		 	}	
		 	obj.value=a_textValue; 
		 }
		 if(z0501s.length>0)
		 {		
				var avalue=eval("document.interviewArrangeForm."+obj.name+"1");
				avalue.value=z0501s;				
		 }	 
		 
	}
	
	function setStateq(obj){
		var state= document.getElementsByName("state")[0];
		var state1=document.getElementsByName("state1")[0];
		for(var  i=0;i<state.options.length;i++){
			if(state.options[i].selected){
				state1.value=state.options[i].value;
				break;
			}
		}
	}
	
	
	
	
	function sub()
	{
		var objarr=new Array();
		var a_zydd=eval("document.interviewArrangeForm.zydd");
		var a_zykg1=eval("document.interviewArrangeForm.zykg1");
		var a_wykg1=eval("document.interviewArrangeForm.wykg1");
		var a_mssj=eval("document.interviewArrangeForm.mssj");
		var sate=document.getElementsByName("state")[0];

		if (eval("document.interviewArrangeForm.zydd_yes").checked)
			objarr[0]=a_zydd.value;
		else
			objarr[0]=null;

		if (eval("document.interviewArrangeForm.zykg_yes").checked)
		    objarr[1]=a_zykg1.value;
		else
			objarr[1]=null;

		if (eval("document.interviewArrangeForm.wykg_yes").checked)
		   objarr[2]=a_wykg1.value;
		else
			objarr[2]=null;

		if (eval("document.interviewArrangeForm.mssj_yes").checked)
		    objarr[3]=a_mssj.value;
		else
			objarr[3]=null;

		if (eval("document.interviewArrangeForm.state_yes").checked)
		    objarr[4]=sate.value;
		else
			objarr[4]=null;
		
		returnValue=objarr;
		window.close();
		
	}

	//截断时间格式
    function save(type,obj)
    {
        var value=obj.value;
        if(type=='D')
        {
           if(value.length>19)
           {
              alert("输入的时间格式应为 yyyy-mm-dd hh:mm 或者 yyyy-mm-dd hh:mm:ss");
              return;
           }
           if(value.length==19)
           {
              obj.value=value.substring(0,16);
              value=value.substring(0,16);
           }
           var  regx=/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}$/;
           if(value.length!=0&&!regx.test(obj.value))
           {
              alert("输入的时间格式应为 yyyy-mm-dd hh:mm 或者 yyyy-mm-dd hh:mm:ss");
             // obj.value="";
             // obj.focus();
              return;
           }
           if(value.length!=0&&value.length<=16)
              value=value+":00";
        }
    }
	
	
	
	</SCRIPT>
<base id="mybase" target="_self">
<hrms:themes></hrms:themes>		
<html:form action="/hire/interviewEvaluating/interviewArrange">	
    <fieldset align="center" style="width:540px;">
        <legend ><bean:message key="hire.fileout.interviewArrange"/></legend>
        <table border="0" cellspacing="0" width="100%"  align="center" cellpadding="0">
          <tr > 
            <td> 
               <table border="0"  cellspacing="0" width="100%" class="ListTable"  cellpadding="2" align="center">
                <tr> 
                  <td colspan="4"> 
                  <table border="0"  cellspacing="0" width="97%" class="ListTable1"  cellpadding="2" align="center" style="margin-top:5px;margin-bottom:5px;">
                      <tr> 
                        <td width="25%" align="center" nowrap class="TableRow">项目</td>
                        <td width="60%" align="center" nowrap class="TableRow">修改值</td>   
                        <td width="15%" align="center" class="TableRow">是否修改</td>                   
                      </tr>
                       <tr> 
                        <td align="center" class="RecordRow" nowrap > 
                         <bean:message key="hire.employActualize.interviewTime2"/>&nbsp;
                        </td>
                        <td align="center" class="RecordRow" nowrap >
                          <input type='text' name='mssj' class='TEXT_NB common_border_color'  size='30' onchange="save('D',this)"  readonly   onclick='popUpCalendar(this,this, dateFormat,"","",true,true)'  value='' /> &nbsp;
                        </td>
                        
                        <td width="5%" align="center" class="RecordRow">
                          <input type="checkbox" name='mssj_yes'/>
                        </td>
                      </tr>
                      <tr> 
                        <td  align="center" class="RecordRow"   nowrap > 
                         <bean:message key="hire.employActualize.interviewArea"/>&nbsp;
                        </td>
                        <td align="center" class="RecordRow" nowrap >
                       		<input type='text'  name='zydd'  class='TEXT_NB common_border_color'  size='30'    value='' /> &nbsp;
                        </td>
                        <td width="5%" align="center" class="RecordRow">
                          <input type="checkbox" name='zydd_yes'/>
                        </td>
                      </tr>
                      
                      <tr> 
                        <td align="center" class="RecordRow" nowrap > 
                          <bean:message key="hire.employActualize.specialtyInterviewer"/>&nbsp;<!-- 专业考官 -->
                        </td>
                        <td align="center" class="RecordRow" nowrap >
                         <input type='text'  name='zykg'  class='TEXT_NB common_border_color' readonly size='30' onclick='selectEmployer(this)'   value='' /> &nbsp;
                         <input type='hidden' name='zykg1' value="" />
                        
                        </td>
                        <td width="5%" align="center"  class="RecordRow">
                          <input type="checkbox" name='zykg_yes'/>
                        </td>
                      </tr>
                      
                      <tr> 
                        <td align="center" class="RecordRow" nowrap > 
                          <bean:message key="hire.employActualize.foreignInterviewer"/>&nbsp;<!-- 外语考官 -->
                        </td>
                        <td align="center" class="RecordRow" nowrap >
                         <input type='text'  name='wykg'  class='TEXT_NB common_border_color' readonly size='30' onclick='selectEmployer(this)'   value='' /> &nbsp; 
                       	  <input type='hidden' name='wykg1' value="" />
                        </td>
                        <td width="5%" align="center" class="RecordRow">
                          <input type="checkbox" name='wykg_yes'/>
                        </td>
                      </tr>
                                   
                      <tr> 
                        <td align="center" class="RecordRow" nowrap > 
                          是否通知&nbsp;
                        </td>
                        <td align="center" class="RecordRow" nowrap >
                         	<select name='state' onchange='setStateq(this)' style="width:200px;margin-left:0px;padding-left:0px"> 

								<option value='21' selected >待通知</option> 
								<option value='22'>已通知</option> 
								<option value='23'>联系不上</option> 
								<option value='24'>已有工作</option>
								<option value='25'>个人放弃</option>
								</select>&nbsp;
                       	  <input type='hidden' name='state1' value="" />
                        </td>
                        <td width="5%" align="center" class="RecordRow">
                          <input type="checkbox" name='state_yes'/>
                        </td>
                      </tr> 
                    </table></td>
                </tr>
              </table>	            				
			</td>
          </tr>		  
         
        </table>
        <br/>
	</fieldset>
<table border="0" cellspacing="0" width="100%"  align="center" cellpadding="0" style="margin-top:5px;">
  <tr> 
            <td align="center">
            <input type="button" name="b_update" value="<bean:message key="button.ok"/>"  onclick='sub()'  class="mybutton"> 
            
            <input type="reset" value="<bean:message key="button.clear"/>" class="mybutton">          
            </td>
          </tr>
</table>


</html:form>