<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%
	String asetid="";
	if(request.getParameter("setid")!=null)
		asetid=request.getParameter("setid");
	String asetdesc="";
	if(request.getParameter("setdesc")!=null)
		asetdesc=request.getParameter("setdesc");
 %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
	var setid="<%=asetid%>";
	var setdesc="<%=asetdesc%>";
	/**从后台取得相应的数据,初始化前台*/
	function showSetList(outparamters)
	{
		var setlist=outparamters.getValue("setlist");
		if(setid&&trim(setid).length>0&&setid!='undefined')
		{
			var list=new Array();
			var data=new Object();
			data.dataValue=setid;
			data.dataName=setdesc;
			list[0]=data;
			setlist=list;
		}
		AjaxBind.bind(commonQueryForm.setlist,/*$('setlist')*/setlist);
		if($('setlist')!=null && $('setlist').options.length>0)
		{
		  $('setlist').options[0].selected=true;
		  try{
	    	   if (navigator.appName.indexOf("Microsoft")!= -1) { 
			        $('setlist').fireEvent('onchange'); //ie 
			    }else{ 
			        $('setlist').onchange();  
			    }  
			}catch(e){
			}
			  
		  //$('setlist').fireEvent("onchange");
		}
		var selectedlist=outparamters.getValue("selectedlist");
		if(selectedlist)
		{
			AjaxBind.bind(commonQueryForm.right_fields,selectedlist);		
		}
	}
	/**显示指标*/
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(commonQueryForm.left_fields,fieldlist);
	}

				
	/**查询指标*/
	function searchFieldList()
	{
	   var priv = "${commonQueryForm.chpriv}";
	   var hashvo=new ParameterSet();	   
  	   hashvo.setValue("priv",priv);
	   var tablename=$F('setlist');
	   var in_paramters="tablename="+tablename;
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'0520000002'},hashvo);
	}
		

	/**填充花名册指标和排序指标*/
	function filloutData()
	{
		var d=$('right_fields')
		if(d.options.length>0)
		{
	    	setselectitem('right_fields');
	    	document.commonQueryForm.action="/kq/options/sign_point/select_query_fields.do?b_next=next";
	    	document.commonQueryForm.submit();
		}
	}
	
	/**初化数据*/
	function QueryInitData(infor){
	   var pars="base="+infor+"&path=2306514";
	   var priv = "${commonQueryForm.chpriv}";
   	   var hashvo=new ParameterSet();	   
  		hashvo.setValue("priv",priv);
  		var arg=parent.dialogArguments;
	   if(arg)//for edit 查询条件定义
	   {
	       var vos= document.getElementsByName("expression");
	       vos[0].value=arg[0];	      
	       hashvo.setValue("expr",arg[0]);
	       var newexpr=$F('expr');
	      if(!(newexpr==null||newexpr==""))
	   	   	hashvo.setValue("expr",newexpr);
	       else
	      {
	   	   	hashvo.setValue("expr",arg[0]);
	   	    $('expr').value=arg[0];
	   	  }
	   	   $('define').value="1";
	   }
   	   var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showSetList,functionId:'0520000001'},hashvo);
	}
	
	
	function clearData()
	{
			//returnValue=" | ";
			//window.close();
	  var vos,right_vo,i;
       vos= document.getElementsByName("right_fields");
      if(vos==null)
  	  return false;
      right_vo=vos[0];
      for(i=right_vo.options.length-1;i>=0;i--)
      {
        
	    right_vo.options.remove(i);
      }
	}
	
	
</script>
<base id="mybase" target="_self">
<html:form action="/kq/options/sign_point/select_query_fields">
<html:hidden property="type"/>
<html:hidden property="expr"/>
<html:hidden property="define"/> 
<html:hidden property="expression"/>
<!--查询指标-->
<div  id="first"  class="fixedDiv2" style="height: 100%;border: none;filter:alpha(Opacity=100);display=block;">
<table width="100%"  border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable" >
   	  <thead>
           <tr>
            <td align="left" class="TableRow_lrt" nowrap>
		&nbsp;<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
   	 	  <td width="100%" align="center" class="common_border_color" style="border: solid 1px;" nowrap>
           <!--  <td width="100%" align="center" class="RecordRow_lrt" nowrap> -->
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
                         <select name="left_fields" multiple="multiple" ondblclick="additem('left_fields','right_fields');" style="height:209px;width:100%;font-size:9pt;">
                         </select>
                       </td>
                    </tr>
                   </table>
                </td>
               
                <td width="4%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
            		 <bean:message key="button.setfield.addfield"/> 
	           </html:button>
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button>	     
                </td>         
                
                <td width="46%" align="center">
                 <table align="center" width="100%">
                  <tr>
                  <td width="100%" align="left">
                     <bean:message key="selfservice.query.queryfieldselected"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="center">
 		     		<html:select name="commonQueryForm" property="right_fields" multiple="multiple" ondblclick="removeitem('right_fields');" style="height:239px;width:100%;font-size:9pt;">
                        
                            <html:optionsCollection property="selectedlist" value="dataValue" label="dataName"/>
                          		      
 		     		</html:select>
                  </td>
                  </tr>
                  </table>             
                </td>
                
                <td width="4%" align="center">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('right_fields'));">
            		     <bean:message key="button.previous"/> 
	           </html:button>
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_down" onclick="downItem($('right_fields'));">
            		     <bean:message key="button.next"/>    
	           </html:button>	     
                </td>                                
                </tr>
              </table>             
            </td>
          </tr>
            
          <tr>
          <td align="center" class="RecordRow" style="height: 35px;border: none" nowrap>
          <!-- 
            <hrms:submit styleClass="mybutton"  property="b_next" onclick="filloutData();">
            		      <bean:message key="button.query.next"/>
	        </hrms:submit>	-->	      
	       	<html:button  styleClass="mybutton" property="b_next" onclick="filloutData();">
	       			<bean:message key="button.query.next"/>
	       	</html:button>
	       	<!--
	       	<html:button  styleClass="mybutton" property="b_clear" onclick="clearData();">
	       			清除条件
	       	</html:button>  
	       	 -->
          </td>
          </tr>   
</table>
</div>
</html:form>
<script language="javascript">
   QueryInitData('<bean:write name="commonQueryForm"  property="type"/>');
</script>