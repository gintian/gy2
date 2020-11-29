<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
    String flag = request.getParameter("flag");
%>
<script language="JavaScript" src="/js/constant.js"></script>
 <script type="text/javascript" src="/ext/ext-all.js" ></script>
    <script type="text/javascript" src="/ext/ext-lang-zh_CN.js" ></script> 
<link  rel="stylesheet" type="text/css"  href="/module/recruitment/css/newParameterSet.css"/>
<script language="javascript">

function simulateClick(el) {
	var evt;
	if (document.createEvent) {
		evt = document.createEvent("HTMLEvents");
		evt.initEvent("change", true,false);
		el.dispatchEvent(evt);
	} else if (el.fireEvent) {
		el.fireEvent('onchange');
	}
}
	
	
	/**从后台取得相应的数据,初始化前台*/
	function showSetList(outparamters)
	{
		var setlist=outparamters.getValue("setlist");
		var selectList = outparamters.getValue("selectedList");
		var size=outparamters.getValue("size");
		if(setlist)
		{
		   AjaxBind.bind(parameterForm2.right_fields,selectList);
			AjaxBind.bind(parameterForm2.setlist,setlist);	
			if($('setlist').options.length>0)
			{
			  $('setlist').options[0].selected=true;
			  simulateClick($('setlist'));
			  //$('setlist').fireEvent("onchange");	
			}
		}
		if(size=='0'){
			alert(NOT_CONFIG_PERSONSTORE+"!");
		window.close();
		}
	}
	/**显示指标*/
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(parameterForm2.left_fields,fieldlist);
	}


				
	/**查询指标*/
	function searchFieldList()
	{
	   var hashvo=new ParameterSet();
	   hashvo.setValue("flag",'<%=request.getParameter("flag")%>');
	   var tablename=$F('setlist');
	   var in_paramters="tablename="+tablename;
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'3000000121'},hashvo);
	}
	
		
	function sub()
	{
		var rightFiledIDs="";
		var rightFieldNames="";
	
		var rightFields=$('right_fields')
		if(rightFields.options.length==0)
		{
		//	alert("请选择指标");
		//	return;
			 returnValue=0;
	    	 window.close();
		}
		var flag='<%=request.getParameter("flag")%>';
		if(flag=='2'&&rightFields.options.length>3)
		{
			alert(ONLY_SELECT_THREE_FIELD+"!");
			return;
		}		
		
		for(var i=0;i<rightFields.options.length;i++)
		{
			rightFiledIDs+="`"+rightFields.options[i].value;
			rightFieldNames+=","+rightFields.options[i].text;
			
			var a_value=rightFields.options[i].value;
			var n=0;
			var a_text="";
			for(var j=0;j<rightFields.options.length;j++)
			{
				if(rightFields.options[j].value==a_value)
				{
					n++;
					a_text=rightFields.options[j].text;
				}
			}
			if(n>1)
			{
				alert(a_text+FIELD_NOT_REPEAT);
				return;
			}
		}
		
		var infos=new Array();
		infos[0]=rightFiledIDs.substring(1);
		infos[1]=rightFieldNames.substring(1);
   	    returnValue=infos;
   	 if(window.parent.me){
   	 	window.parent.me.setCallBack({returnValue:returnValue});
   	    window.parent.Ext.getCmp('window').close();
   	 }else
   	   	 window.close();
	}
	
	
	
	/**填充花名册指标和排序指标*/
	function filloutData()
	{
	    setselectitem('right_fields');
	    setselectitem('sort_right_fields');		
	}
	
	/**初化数据*/
	function MusterInitData(flag)
	{
	   var pars="flag="+flag
   	   var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showSetList,functionId:'3000000120'});
	}
	
</script>
<html:form action="/hire/parameterSet/configureParameter">
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<Br>
<%
}
%>
<table width='530' border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <%--<thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
		<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   --%><tr>
   	   <td class="RecordRow" align="center" width="100%" nowrap>
   	   <table width="100%">
   	   <tr>
            <td width="100%" align="center" nowrap>
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
                
                <td width="46%" align="center"  style="border-left:#c5c5c5 1px solid;">
                 <table width="100%" style="margin-left:10px">
                  <tr>
                  <td width="100%" align="left">
                     <bean:message key="selfservice.query.queryfieldselected"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
		              <select  size="10"  name="right_fields" multiple="multiple" ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
		        </select>
                  </td>
                  </tr>
                  </table>             
                </td>
                <td width="8%" align="center" style="padding-left:15px">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('right_fields'));">
            		     <bean:message key="button.previous"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_down" onclick="downItem($('right_fields'));">
            		     <bean:message key="button.next"/>    
	           </html:button >	     
                </td>                                
                </tr>
              </table>             
            </td>
            </tr>
            </table>
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap style="padding-top:5px;padding-bottom:3px;">
              <html:button styleClass="mybutton" property="b_next" onclick="sub()">
            		      <bean:message key="reporttypelist.confirm"/>
	      </html:button> 	
          </td>
          </tr>   
</table>

</html:form>
<script language="javascript">
   MusterInitData('<%=flag%>');
</script>