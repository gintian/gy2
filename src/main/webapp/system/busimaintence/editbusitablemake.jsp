<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hjsj.hrms.actionform.sys.busimaintence.BusiMaintenceForm"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
BusiMaintenceForm busiMaintenceForm=(BusiMaintenceForm)session.getAttribute("busiMaintenceForm");
String returnvalue = busiMaintenceForm.getReturnvalue();
%>
<script language="javascript">

	
	/**从后台取得相应的数据,初始化前台*/
	function showSetList(outparamters)
	{
		
		var setlist=outparamters.getValue("syselist");
		AjaxBind.bind(busiMaintenceForm.setlist,/*$('setlist')*/setlist);	
		if($('setlist').options.length>0)
		{
		  $('setlist').options[0].selected=true;
		  // $('setlist').fireEvent("onchange");
            myFireEvent($('setlist'));
		 
		}
		
	}
	
	/*选择不同系统子集*/
	function showlist(outparamters){
		var zijilist=outparamters.getValue("zijilist");
		
		AjaxBind.bind(busiMaintenceForm.zijilist,/*$('setlist')*/zijilist);
		if($('zijilist').options.length>0)
		{
		  $('zijilist').options[0].selected=true;
		  // $('zijilist').fireEvent("onchange");
		  myFireEvent($('zijilist'));
		}
	}
	function searchsetList(){
		var sysvalue=busiMaintenceForm.setlist.value;
		// var operation=document.getElementById("operation").value;
		var operation=document.getElementByName("operation")[0].value;
		var hashVo=new ParameterSet();
        hashVo.setValue("operation",operation);
		var pars="sysvalue="+sysvalue;
   	    var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showlist,functionId:'1010060005'},hashVo);
	}
	/**显示指标*/
	function showFieldList(outparamters)
	{
		var contractedFieldList=outparamters.getValue("contractedFieldList");
		
		var uncontractedFiledList=outparamters.getValue("uncontractedFiledList");
		AjaxBind.bind(busiMaintenceForm.right_fields,contractedFieldList);
		AjaxBind.bind(busiMaintenceForm.left_fields,uncontractedFiledList);
	}

				
	/**查询指标*/
	function searchFieldList()
	{
	   var sysvalue=$F('sysel');
	   var zijivalue=$F('zijisel');
	   var in_paramters=zijivalue+"/"+sysvalue;
	   var hashvo = new ParameterSet();
	   hashvo.setValue("tabname",in_paramters);
   	   var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'1010061004'},hashvo);
	}
	
	function changedictionary(theurl){
	setselectitem('right_fields');
	setselectitem('left_fields');
	var right=document.getElementsByName("right_fields");
	var left=document.getElementsByName("left_fields");
	var zero="no";
	if(right[0].options.length==0)
	{
	    zero="r";
	}
	if(left[0].options.length==0)
	{
	    zero="l";
	}
	if(right[0].options.length==0&&left[0].options.length==0)
	    return;
    busiMaintenceForm.action=theurl+"&zero="+zero;
    busiMaintenceForm.submit();
	}
	/**初化数据*/
	function MusterInitData(infor)
	{
	  if(infor=="1")
	  {
	     alert(KJG_YWZD_INFO11);	  
	  }else if(infor=="2"){
	     alert(KJG_YWZD_INFO12);	
	  }
	  var ziji="${busiMaintenceForm.zijisel}";
	  if(ziji=="")
	  {
	     if($('zijisel').options.length>0)
	     {
		   $('zijisel').options[0].selected=true;
		   // $('zijisel').fireEvent("onchange");
             myFireEvent($('zijisel'));
	     }
	  }else
	  {
	     if($('zijisel').options.length>0)
	     {
	       // $('zijisel').fireEvent("onchange");
             myFireEvent($('zijisel'));
	     }
	  }	  	  
	}
	function editfield()
    {
     var vos= document.getElementById("right");
     if(vos.length==0)
     	{
       		alert("已选指标项不能为空！");
       		return false;
     	}else
     	{
     		if(confirm(KJG_YWZD_INFO13))
	   	{
	     setselectitem('right_fields');
	     setselectitem('left_fields');	    
	     busiMaintenceForm.action="/system/busimaintence/editbusitablemake.do?b_edit=link&encryptParam=<%=PubFunc.encrypt("returnvalue="+returnvalue)%>";
         busiMaintenceForm.submit();
	    }
     	}
	}
var aa ;
function additemg(sourcebox_id,targetbox_id)
{
  var left_vo,right_vo,vos,i;
  var subsetid = document.getElementById("subsetid").value;
  vos= document.getElementsByName(sourcebox_id);
  if(vos==null)
  	return false;
  left_vo=vos[0];
  vos= document.getElementsByName(targetbox_id);
  if(vos==null)
  	return false;
  right_vo=vos[0];
  aa = right_vo;
  var hashvo = new ParameterSet();
  for(i=0;i<left_vo.options.length;i++)
  {
    if(left_vo.options[i].selected)
    {
        var no = new Option();
    	no.value=left_vo.options[i].value;
    	no.text=left_vo.options[i].text;
    	
		hashvo.setValue("codeid",no.value);
		hashvo.setValue("codename",no.text);
		hashvo.setValue("subsetid",subsetid);
		var request=new Request({method:'post',asynchronous:true,onSuccess:check_ok,functionId:'1010061011'},hashvo);
    	
    }
  }
 return true;	  	
}
function check_ok(outparameter)
{
	var msg = outparameter.getValue("msg");
	var codeid=outparameter.getValue("codeid");
	var codename=outparameter.getValue("codename");
	if(msg=='1')
    {
       amends(codeid,codename);
    }
   else
   {
     alert("所选指标不允许更改");
     return;
   }
}
function amends(codeid,codename)
{
	var nos = new Option();
	nos.value=codeid;
	nos.text=codename;
	var value=nos.value;
	aa.options[aa.options.length]=nos;
	
	removeitemg('right_fields',value);
}
function removeitemg(sourcebox_id,value)
{
  var vos,right_vo,i;
  vos= document.getElementsByName(sourcebox_id);
  if(vos==null)
  	return false;
  right_vo=vos[0];
  for(i=right_vo.options.length-1;i>=0;i--)
  {
    	if(right_vo[i].value==value){
    		if(right_vo.options[i].selected)
    		{
    		//alert(i);
			right_vo.options.remove(i);
			}
    	}
  } 
  return true;	  	
}

function saveSort(){ 
	var hashvo=new ParameterSet();
	// var setid = document.getElementById("zijisel").value;
	var setid = document.getElementsByName("zijisel")[0].value;
	hashvo.setValue("fsetid",setid);
	hashvo.setValue("displayid",selectTostr('right_fields'));
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:null,functionId:'1010061023'},hashvo);
}
function selectTostr(listbox){
  var vos,right_vo,i,str='';
  vos= document.getElementsByName(listbox);
  if(vos==null || vos[0].length==0){
  	return;  	
 	vos[0].options[0].selected=false;

  }
  //设为要可选状态
  right_vo=vos[0];  
  for(i=0;i<right_vo.options.length;i++){
	str += right_vo.options[i].value+",";
  }
  return str;  	
}

    /* 兼容fireEvent方法 */
    function myFireEvent(el) {
        var evt;
        if (document.createEvent) {
            evt = document.createEvent("MouseEvents");
            evt.initMouseEvent("change", true, true, window,
                0, 0, 0, 0, 0, false, false, false, false, 0, null);
            el.dispatchEvent(evt);
        } else if (el.fireEvent) { // IE
            el.fireEvent("onchange");
        }
    }
</script>
<html:form action="/system/busimaintence/editbusitablemake">
<div id="first" style="filter:alpha(Opacity=100);display=block;">
<!-- 添加边框合并样式，IE浏览器拉伸 边框会一直存在 不会消失，右外边距 设置5px 与左外边距一致  wangb 20170719 29878  style="border-collapse:separate; border-spacing:1px;margin-right:1px;-->
<table width="700" border="0" style="border-collapse:separate; border-spacing:1px;margin-right:5px;" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow_lrt" nowrap colspan="">
            <!-- 显示边框必须嵌套一个table  内容放到table里的 td  wangb 20170719 29878 -->
           	<table>
           		<tr>
           			<td>
           			 <logic:equal value="0" name="busiMaintenceForm" property="operation">
		               <bean:message key='kjg.title.newapplydb'/>
		            </logic:equal>
		           <logic:equal value="1" name="busiMaintenceForm" property="operation"> 
					   <bean:message key='kjg.title.reviseddb'/>
					 </logic:equal>
           			</td>
           		</tr>
           	</table>
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%"  align="center" class="RecordRow" nowrap>
              <table>
                <tr>
                 <td align="center"  width="46%">
                   <table align="center" width="100%">
                    <tr>
                    <td align="left">
                     <bean:message key='kjg.title.ywmodule'/>&nbsp;&nbsp;
                    </td>
                    </tr>
                    <tr>
                      <td align="center">
                        <html:select name="busiMaintenceForm" property="sysel" style="width:100%" disabled="true">    
                                  <html:optionsCollection property="syselist" value="dataValue" label="dataName"/>                                  
                        </html:select>
                      </td>
                    </tr>
                    <tr>
                    <td align="left">
                        <bean:message key="selfservice.query.queryfield"/>&nbsp;&nbsp;
                    </td>
                    </tr>
                    <tr>
                      <td align="center">
                          <html:select styleId="subsetid" name="busiMaintenceForm" property="zijisel" style="width:100%"  onchange="searchFieldList();" >    
                                  <html:optionsCollection property="zijilist" value="dataValue" label="dataName"/>                                  
                          </html:select>
                      </td>
                    </tr>
                   <tr>
                       <td align="center">
                         <select name="left_fields" multiple="multiple" ondblclick="if(additem('left_fields','right_fields'))removeitem('left_fields');" style="height:200px;width:100%;font-size:9pt">
                         </select>
                       </td>
                    </tr>
                   </table>
                </td>
               
                <td width="4%" align="center">
                   <html:button  styleClass="smallbutton" property="b_addfield" onclick="if(additem('left_fields','right_fields'))removeitem('left_fields');">
            		     <bean:message key="button.setfield.addfield"/>
	               </html:button >
	           <html:button  styleClass="smallbutton" property="b_delfield" onclick="additemg('right_fields','left_fields');" style="margin-top:30px;">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	     
                </td>         
                
                <td width="46%" align="center">
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     <bean:message key="selfservice.query.queryfieldselected"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
 		     <html:select styleId="right" name="busiMaintenceForm" property="right_fields" multiple="multiple" size="10" ondblclick="additemg('right_fields','left_fields')" style="height:285px;width:100%;font-size:9pt">
 		         <html:optionsCollection property="mfieldlist" value="dataValue" label="dataName"/>
 		     </html:select>
                  </td>
                  </tr>
                  </table>             
                </td>
                <td width="4%" align="center">
                   <html:button  styleClass="smallbutton" property="b_up" onclick="upItem($('right_fields'));saveSort();">
            		     <bean:message key="button.previous"/> 
	           </html:button >
	           <html:button  styleClass="smallbutton" property="b_down" onclick="downItem($('right_fields'));saveSort();" style="margin-top:30px;">
            		     <bean:message key="button.next"/>    
	           </html:button >	     
                </td>                                
                </tr>
              </table>             
            </td>
            </tr>
            <tr style="height: 35px">
            <td colspan="3" align="center">
				<html:button  styleClass="mybutton" property="b_addfield" onclick="editfield();">
				        <bean:message key="button.ok" />
	           </html:button >
	            <logic:equal value="sbn" name="busiMaintenceForm" property="returnvalue">
				<hrms:submit styleClass="mybutton" property="br_return">
						<bean:message key="button.return" />
				</hrms:submit>
			    </logic:equal>
			   <logic:equal value="ssb" name="busiMaintenceForm" property="returnvalue">
				<hrms:submit styleClass="mybutton" property="br_returnssb">
						<bean:message key="button.return" />
				</hrms:submit>
			   </logic:equal> 
				
            </td>
            </tr>
           
</table>
</div>
<html:hidden property="operation" name="busiMaintenceForm"/>
</html:form>
<script language="javascript">
   MusterInitData('${busiMaintenceForm.editflag}');
   if(!getBrowseVersion()){
       document.getElementsByClassName('ListTable')[0].style.margin = '0 auto';
   }
</script>
<% 
busiMaintenceForm.setEditflag("-1");
session.setAttribute("busiMaintenceForm",busiMaintenceForm);
%>