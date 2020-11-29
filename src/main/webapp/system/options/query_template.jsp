<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style>
/*
body {  
	background-color:#DEEAF5;
	font-size: 12px;
	margin:4 0 0 4;
}*/
</style>

<script language="javascript">
	
	/**从后台取得相应的数据,初始化前台*/
	function showSetList(outparamters)
	{
		var setlist=outparamters.getValue("setlist");
		var dlist=outparamters.getValue("dlist");
		AjaxBind.bind(optionsForm.setlist,/*$('setlist')*/setlist);
		AjaxBind.bind(optionsForm.right_fields,dlist);
		
	   	while ($('left_fields').childNodes.length > 0) {
				$('left_fields').removeChild($('left_fields').childNodes[0]);
	   	}	
		if($('setlist').options.length>0)
		{
		  $('setlist').options[0].selected=true;
		//火狐浏览器不支持 fireEvent 方法  12771   wangb  20170520
		  if(document.all){
		  		$('setlist').fireEvent("onchange");
		  }else{
		  		var evt=document.createEvent('HTMLEvents');
		  		evt.initEvent('change',true,true);
		  		$('setlist').dispatchEvent(evt);
		  }
		}
	}
	/**显示指标*/
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(optionsForm.left_fields,fieldlist);
	}
	
			
	/**查询指标*/
	function searchFieldList()
	{
	   var tablename=$F('setlist');
	   var in_paramters="tablename="+tablename;
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'0520000002'});
	}
	
	/**初化数据*/
	function InitData()
	{  
	   var infor=$F('classpre');
	   if(infor=="A")
	      infor="1"
	   if(infor=="B")
	      infor="2"
	   if(infor=="K")
	      infor="3"	      
	   if(infor=="H")
	      infor="4";
	   if(infor=="Y")
	      infor="5";	      
	   if(infor=="V")
	      infor="6";
	   if(infor=="W")
	      infor="7";	      	      	     
	   var pars="base="+infor;

	   while ($('right_fields').childNodes.length > 0) {
		$('right_fields').removeChild($('right_fields').childNodes[0]);
	   }	   
   	   var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showSetList,functionId:'1010010050'});
	}
	function additem(sourcebox_id,targetbox_id)
{
  var left_vo,right_vo,vos,voss,i;
  vos= document.getElementsByName(sourcebox_id);
  if(vos==null)
  	return false;
  left_vo=vos[0];
  var nofield=true;

  voss= document.getElementsByName(targetbox_id);  
  right_vo=voss[0];
  for(i=0;i<left_vo.options.length;i++)
  {
   if(left_vo.options[i].selected)
   {
    if(right_vo.options.length==0)
     {
        var no = new Option();
        no.value=left_vo.options[i].value;
        no.text=left_vo.options[i].text;
        right_vo.options[right_vo.options.length]=no;
     }else{
        for(var j=0;j<right_vo.options.length;j++)
        {
           if(right_vo.options[j].text==left_vo.options[i].text)
           {
               nofield=false; 
           }         
        }
          if(nofield)
           {
              var no = new Option();
              no.value=left_vo.options[i].value;
              no.text=left_vo.options[i].text;
              right_vo.options[right_vo.options.length]=no;
           }
           nofield=true;
    }
   }
 }
 return true;	  	
}
</script>
<html:form action="/system/options/query_template">
<!--查询模板指标-->
<div id="first">
<table width="700" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3">
		<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr >
   	   <td class="RecordRow" style="border-right:none;"><bean:message key="label.query.template"  />
                    <html:select name="optionsForm" property="classpre" size="1" onchange="InitData();">
                      <html:optionsCollection property="list" value="dataValue" label="dataName"/>
                    </html:select>   	     
   	   </td>
   	  </tr>
   	   <tr>
            <td width="100%" align="left" class="RecordRow" nowrap style="border-right:none;">
              <table>
                <tr>
                 <td align="center"  width="46%" align="left">
                   <table align="left" width="100%">
                    <tr>
                    <td align="left">
                        <bean:message key="selfservice.query.queryfield"/>&nbsp;&nbsp;
                    </td>
                    </tr>
                    <tr>
                      <td align="left">
			<select name="setlist" size="1"  style="width:100%" onchange="searchFieldList();">    
			    <option value="1111">#</option>
                        </select>
                      </td>
                    </tr>
                   <tr>
                       <td align="left">
                         <select name="left_fields" multiple="multiple" ondblclick="additem('left_fields','right_fields');" style="height:209px;width:100%;font-size:9pt">
                         </select>
                       </td>
                    </tr>
                   </table>
                </td>
               
                <td width="4%" align="center" valign="middle">
                   <html:button  styleClass="smallbutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button>
	           <html:button  styleClass="smallbutton" property="b_delfield" onclick="removeitem('right_fields');" style="margin-top:30px;">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button>	     
                </td>         
                
                <td width="46%" align="center">
                 <table width="100%" align="left">
                  <tr>
                  <td align="left">
                     <bean:message key="selfservice.query.queryfieldselected"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="center">
 		     <html:select name="optionsForm" property="right_fields" multiple="multiple" size="10" ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
 		     </html:select>
                  </td>
                  </tr>
                  </table>             
                </td>
                <td width="4%" align="right" valign="middle">
                   <html:button  styleClass="smallbutton" property="b_up" onclick="upItem($('right_fields'));">
            		     <bean:message key="button.previous"/> 
	           </html:button>
	           <html:button  styleClass="smallbutton" property="b_down" onclick="downItem($('right_fields'));" style="margin-top:30px;">
            		     <bean:message key="button.next"/>    
	           </html:button>	     
                </td>                                
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap  colspan="3" style="height:35px">
              <html:submit styleClass="mybutton" property="b_save" onclick="setselectitem('right_fields');">
            		      <bean:message key="button.save"/>
	      </html:submit> 	       
          </td>
          </tr>   
</table>
</div>

</html:form>
<script language="javascript">
   InitData();
   
   if(!getBrowseVersion()){//非ie兼容模式 样式修改  wangb 20190318
   	  var first = document.getElementById('first');
   	  var tds = document.getElementsByClassName('RecordRow');
   	  tds[1].style.borderRight='';
   	  tds[2].style.borderRight='';
   }
</script>