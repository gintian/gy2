<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant" %>
<%
	String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	  	bosflag=userView.getBosflag();
	}
%>
<%
		
	String expr = request.getParameter("expr")==null? "":request.getParameter("expr");
 %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
	var setid="${commonQueryForm.setidpiv}";
	var setdesc="${commonQueryForm.setdescpiv}";
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
		//获取url中"?"符后的字串
		var url = location.search;
		var selectedlist=outparamters.getValue("selectedlist");
		if(selectedlist && url) {
			AjaxBind.bind(commonQueryForm.right_fields,selectedlist);		
		}
	}
	/**显示指标*/
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(commonQueryForm.left_fields,fieldlist);
		//【5988】IE11上，预警设置，新增预警，简单条件，选择工资变动子集，然后拖动变动子集右侧的滚动条，选择下方的指标，拖动滚动条时IE崩溃了。    jingq add 2014.12.16
		document.getElementsByName("left_fields")[0].focus();
		//【49061】V76封版绩效管理：ie兼容，参数设置/主体类别/简单条件，打开“单位名称”有虚线，见附件，由于加上上面的focus导致，
		//现在让其默认选中第一个，这样就看不到虚线
		document.getElementById("left_fields_id")[0].selected = true;
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
	    	document.commonQueryForm.action="/general/query/common/select_query_fields.do?b_next=next";
	    	document.commonQueryForm.submit();
		}
	}
	
	/**初化数据*/
	function QueryInitData(infor){
	   var pars="base="+infor+"&path=2306514";
	   var priv = "${commonQueryForm.chpriv}";
   	   var hashvo=new ParameterSet();	   
  		hashvo.setValue("priv",priv);
  		//兼容非IE浏览器  wangb 20180129
  		var userAgent = navigator.userAgent;
  	    var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1;
  	    var arg;
  		if(parent.parent.Ext){//非IE浏览器弹窗获取数据  wangb 20190318
  		    if(parent.parent.Ext.getCmp('simple_query')){
  				var win = parent.parent.Ext.getCmp('simple_query');
  				arg = win.arguments;
  		    }else {
  		    	arg ='<%=expr%>'
  		    }
  		}else if(getBrowseVersion()){
  			arg=parent.dialogArguments;
  		}else //非IE浏览器获取数据方式  wangb 20180129
  		  arg ='<%=expr%>'
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
<html:form action="/general/query/common/select_query_fields">
<html:hidden property="type"/>
<html:hidden property="expr"/>
<html:hidden property="define"/> 
<html:hidden property="expression"/>
<!--查询指标-->
<div id="first" style="filter:alpha(Opacity=100);display=block;">
<%if("hcm".equals(bosflag)){ %>
<table width="590" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
<%}else{ %>
<table width="590" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top: 10px" >	
<%} %> 
   	  <thead>
           <tr>
            <td align="left" class="TableRow_lrt" nowrap>
		<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow_lrt" nowrap>
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
                       <td align="center" height="225px" style="padding-top: 3px;"><!-- modify by xiaoyun 2014-8-20 -->
                         <select id="left_fields_id" name="left_fields" multiple="multiple" ondblclick="additem('left_fields','right_fields');" style="height:100%;width:100%;font-size:9pt;">
                         </select>
                       </td>
                    </tr>
                   </table>
                </td>
               
                <td width="4%" align="center">
                   <html:button  styleClass="smallbutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
            		 <bean:message key="button.setfield.addfield"/> 
	           </html:button>
	           <html:button style="margin-top:30px;" styleClass="smallbutton" property="b_delfield" onclick="removeitem('right_fields');">
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
                  <td width="100%" height="248px" align="center"><!-- modify by xiaoyun 2014-8-20 -->
 		     		<html:select name="commonQueryForm" property="right_fields" multiple="multiple" ondblclick="removeitem('right_fields');" style="height:100%;width:100%;font-size:9pt;">
                       <html:optionsCollection property="selectedlist" value="dataValue" label="dataName"/>
 		     		</html:select>
                  </td>
                  </tr>
                  </table>             
                </td>
                
                <td width="4%" align="center">
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
          <td align="center" class="RecordRow" style="height: 35px" nowrap>
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