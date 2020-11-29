<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<%
	String rightStr = request.getParameter("rightStr");
%>
<script language="javascript">
       	function showquerycon(outparamters)
	{
	  var querycon=outparamters.getValue("querycon");
          $('message').innerHTML = querycon; 
        }
        /**从后台取得相应的数据,初始化前台*/
	function showSetList(outparamters)
	{
	    var setlist=outparamters.getValue("setlist");
	    AjaxBind.bind(arrayGroupSelectForm.setlist,/*$('setlist')*/setlist);
        if($('setlist').options.length>0)
	    {
	        $('setlist').options[0].selected=true;
	    	$('setlist').fireEvent("onchange");
	    }
    	var rightField = "<%=rightStr%>";  
    	var selectlist = new Array();
    	if(rightField != null && rightField.length > 0 && rightField != "null" && rightField != "undefined"){
    		var obj = document.getElementsByName("right_fields");
    		var obj_vo = obj[0];
			selectlist = rightField.split("^");
			var onefield;
			for(var j=0;j < selectlist.length;j++){
				onefield = new Array();
				if(selectlist[j] != null && selectlist[j].length > 0 && selectlist[j] != "undefined"){
					onefield = selectlist[j].split("`");
					var no = new Option();
			    	no.value = onefield[0];
			    	no.text = onefield[1];
			    	obj_vo.options[j]=no;
				}
			}
       	}
	}
	/**显示指标*/
	function showFieldList(outparamters)
	{
	  var queryfieldlist=outparamters.getValue("queryfieldlist");
	  AjaxBind.bind(arrayGroupSelectForm.left_fields,queryfieldlist);
	
	}
	/**查询指标*/
	function searchFieldList()
	{
	  var setname=$F('setlist');
	  var in_paramters="setname="+setname;
   	  var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'1510010017'});
	}
	function next()
	{
	  if($('right_fields').options.length)
	  {
		  var rightStr = "";
		  var len = $('right_fields').options.length;
		  for(var i=0;i<len;i++){
			  rightStr += $('right_fields').options[i].value + "`" + $('right_fields').options[i].text + "^";
		  }
	      setselectitem('right_fields');
	      arrayGroupSelectForm.action="/kq/team/array_group/selectfiled.do?b_next=link&rightStr="+$URL.encode(rightStr);
          arrayGroupSelectForm.submit(); 
      }else
          alert("未定义查询指标");	
	}
	/**初始化数据*/
	function InitSetData()
	{
	   setselectitem('right_fields');	 
	   var request=new Request({method:'post',asynchronous:false,onSuccess:showSetList,functionId:'1510010016'});
	}
	
	function goback()
	{
	    //history.back();
	    window.location.href = "/kq/team/array_group/search_array_emp_data.do?b_search=link&group_id=${arrayGroupSelectForm.group_id}";
	}
</script>
<html:form action="/kq/team/array_group/selectfiled">
<br><br>
<!--指标选择-->
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="3">
		&nbsp;<bean:message key="label.query.selectfield"/>&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap style="border-bottom:none;border-top:none">
              <table>
                <tr>
                 <td align="center"  width="46%">
                   <table align="center" width="100%">
                    <tr>
                    <td align="left">
                        <bean:message key="selfservice.query.queryfield"/>&nbsp;&nbsp;
                    </td>
                    </tr>
                    <tr>
                      <td align="center">
			<select name="setlist" size="1"  style="width:100%" onchange="searchFieldList();">    
			    <option value="1111">#</option>
                        </select>
                      </td>
                    </tr>
                   <tr>
                       <td align="center">
                         <select name="left_fields" multiple="multiple" ondblclick="additem('left_fields','right_fields');" style="height:209px;width:100%;font-size:9pt">
                         </select>
                       </td>
                    </tr>
                   </table>
                </td>
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
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
 		   
 		  
 		   
              
                    <select name="right_fields" multiple="multiple" size="11" ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
 		     </select>
      
 		               
                  </td>
                  </tr>
                  </table>             
                </td>
                          
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap  colspan="3" style="height:35px;">
             <html:button styleClass="mybutton" property="b_pres" onclick="goback();">
            		<bean:message key="button.query.pre"/>
	      </html:button>
              <html:button styleClass="mybutton" property="b_next" onclick="next();">
            	  <bean:message key="button.query.next"/>
	      </html:button> 	
	             
          </td>
          </tr>   
</table>
</html:form>
<script language="javascript">
   InitSetData();
</script>