<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.gz.gz_accounting.ChangeInfoForm,java.util.ArrayList"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@page import="com.hrms.hjsj.sys.FieldItem,org.apache.commons.beanutils.LazyDynaBean,java.math.BigDecimal"%>
<%@ page import="com.hrms.frame.utility.AdminCode,com.hrms.frame.utility.CodeItem"%>
<%
	ChangeInfoForm changeInfoForm=(ChangeInfoForm)session.getAttribute("changeInfoForm");
	FieldItem itemo = changeInfoForm.getOnlyitem();
	ArrayList fieldItemList=changeInfoForm.getFieldItemList();
	int clospan=8+fieldItemList.size()*2;
	String dise=changeInfoForm.getDisplayE0122();
%>
<script type="text/javascript" >
<!--
    function isSuccess(outparamters)
    {
	  
    }
	function setvalid(obj,dbname,a0100)
	{
		var flag;
		if(obj.checked)
		  flag="1";
		else
		  flag="0";
        var hashvo=new ParameterSet();
        hashvo.setValue("dbname",dbname);
		hashvo.setValue("a0100",a0100);
		hashvo.setValue("flag",flag);	   
		hashvo.setValue("chgtype",'chginfo');   
		hashvo.setValue("salaryid",'${changeInfoForm.salaryid}');  
	   	var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'3020072008'},hashvo); 
	
	}
	
	function exports()
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("salaryid",'${changeInfoForm.salaryid}');  
		hashvo.setValue("fieldstr",'${changeInfoForm.fieldstr}');  
		hashvo.setValue("filterid",'${changeInfoForm.filterid}');  
		hashvo.setValue("opt","change");
	   	var request=new Request({asynchronous:false,onSuccess:showfile,functionId:'3020072009'},hashvo); 
	}
	
	
	function showfile(outparamters)
	{
		var fileName=outparamters.getValue("fileName"); 
		fileName = getDecodeStr(fileName);
		var win=open("/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true","excel");
	}
	function allSelect()
	{
	   changeInfoForm.action="/gz/gz_accounting/changeinfo.do?b_all=all";
	   changeInfoForm.submit();
	}
	function allClear()
	{
	   changeInfoForm.action="/gz/gz_accounting/changeinfo.do?b_clear=clear";
	   changeInfoForm.submit();
	}
	
//-->
</script>
<style>
<!--
.TableRow{
	BACKGROUND-COLOR: #f4f7f7; 
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 0pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 0pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:23;
}
.TableRow_head_locked {
	background-repeat:repeat;
	background-position : center left;
	BACKGROUND-COLOR: #f4f7f7; 
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 0pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 0pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:20;
	font-weight: bold;	
	valign:middle;
	left: expression(document.getElementById("ss").scrollLeft-1); /*IE5+ only*/
	position: relative;
	
}
.TableRow_head_locked1{
	background-repeat:repeat;
	background-position : center left;
	BACKGROUND-COLOR: #f4f7f7; 
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 0pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:24;
	font-weight: bold;	
	valign:middle;
	left: expression(document.getElementById("ss").scrollLeft-1); /*IE5+ only*/
	position: relative;
}
.RecordRow_locked{
	background-repeat:repeat;
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 0pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
	background-color:white;
	left: expression(document.getElementById("ss").scrollLeft-1); /*IE5+ only*/
	position: relative;
}
.RecordRow_locked2{
	background-repeat:repeat;
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 0pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
	background-color: #FFF8D2;
	left: expression(document.getElementById("ss").scrollLeft-1); /*IE5+ only*/
	position: relative;
}
.RecordRow_button{
	border: inset 0px #94B6E6;
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
	background-color: white;
	left: expression(document.getElementById("ss").scrollLeft-1); /*IE5+ only*/
	position: relative;
}
div#ss
{

	BORDER-BOTTOM:#C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid; <!-- modify by xiaoyun 2014-10-8 hr页面缺线问题(薪资发放-变动对比) -->
}

/* 薪资发放-变动对比-固定多级表头 xiaoyun 2014-10-13 start */

/* 薪资发放-变动对比-固定多级表头 xiaoyun 2014-10-13 end */


-->
</style>
<hrms:themes />
<body>
<html:form action="/gz/gz_accounting/changeinfo">

<div id="ss" class='complex_border_color c' style="position:absolute;overflow:auto;width:100%;height:385px;left:5;right:5;top:15">
   <table class="t" width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
   	  <thead>
           <tr>
            <td align="center" class="TableRow_head_locked1 TableRow" style="border-top: none;border-right: none;" rowspan="2"  nowrap>
		<logic:equal value="0" name="changeInfoForm" property="checkall">
		<html:checkbox property="checkall" name="changeInfoForm" value="1" onclick="allSelect();"></html:checkbox>
		</logic:equal>
		<logic:equal value="1" name="changeInfoForm" property="checkall">
		<html:checkbox property="checkall" name="changeInfoForm" value="1" onclick="allClear();"></html:checkbox>
		</logic:equal>
            </td>    
            <td align="center" class="TableRow_head_locked1 TableRow" style="border-top: none;border-right: none;" rowspan="2" nowrap>
		&nbsp;<bean:message key="label.dbase"/>&nbsp;
            </td> 
            <td align="center" class="TableRow_head_locked TableRow" style="border-top: none;border-right: none;" colspan="2" nowrap>
		单位
            </td> 
            
               <td align="center" class="TableRow_head_locked TableRow" style="border-top: none;border-right: none;" colspan="2" nowrap>
		部门
            </td> 
            <td align="center" class="TableRow_head_locked TableRow" style="border-top: none;border-right: solid 1pt;" colspan="2" nowrap>
		姓名
            </td>    
         <% if(fieldItemList!=null&&fieldItemList.size()>0){ 
          for(int j=0;j<fieldItemList.size();j++)
          {
             FieldItem item = (FieldItem)fieldItemList.get(j);
             String itemdesc=item.getItemdesc();
         %>   
               <td align="center" class="TableRow" style="border-left:none;border-top: none;" colspan="2" nowrap>
		<%=itemdesc%>
            </td>    
            
           <%}
           }
            %>  
              <%if(itemo!=null){
             clospan++;
               %>
                <td align="center" class="TableRow_2rows" style="border-left:none;border-top: none;border-right: none;" rowspan="2" nowrap>
             <%=itemo.getItemdesc()%>       	
	          </td>
  
            <%} %> 
            </tr>
            <tr>  
            <td align="center" class="TableRow_head_locked1 TableRow" style="border-top: none;border-right: none;"  nowrap>
		<bean:message key="gz.info.sorg"/>&nbsp;
            </td>                                                                                                                        
            <td align="center" class="TableRow_head_locked1 TableRow"  style="border-top: none;border-right: none;" nowrap>
		<bean:message key="gz.info.org"/>&nbsp;
            </td>   
            <td align="center" class="TableRow_head_locked1 TableRow" style="border-top: none;border-right: none;" nowrap>
		<bean:message key="gz.info.sdept"/>&nbsp;
	    </td>        
            <td align="center" class="TableRow_head_locked1 TableRow " style="border-top: none;border-right: none;" nowrap>
		<bean:message key="gz.info.dept"/>&nbsp;
	    </td>       

	    
            <td align="center" class="TableRow_head_locked1 TableRow" style="border-top: none;border-right: none;" nowrap>
		<bean:message key="gz.info.sa0101"/>&nbsp;
	    </td>
            <td align="center" class="TableRow_head_locked1 TableRow" style="border-top: none;" nowrap>
		<bean:message key="gz.info.a0101"/>&nbsp;
	    </td>
	    <% if(fieldItemList!=null&&fieldItemList.size()>0){ 
          for(int j=0;j<fieldItemList.size();j++)
          {
             FieldItem item = (FieldItem)fieldItemList.get(j);
             String itemdesc=item.getItemdesc();
         %>   
               <td align="center" class="TableRow" style="border-left:none;border-top: none;" nowrap>
		原<%=itemdesc%>
            </td>    
              <td align="center" class="TableRow" style="border-left:none;border-top: none;" nowrap>
		现<%=itemdesc%>
            </td>    
            
           <%}
           }
            %>   
 
		   </tr>
      </thead>
      <% int i=0; %>
      <tbody>
      <hrms:paginationdb id="element" name="changeInfoForm" sql_str="changeInfoForm.strsql" table="" where_str="changeInfoForm.strwhere" columns="changeInfoForm.columns" order_by=" order by DBNAME,A0000,b0110,e0122" page_id="pagination" pagerows="15" distinct="">
	   
	    <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
         
          %>  
          
            <td align="center" class="RecordRow_locked common_border_color" style=" " nowrap>
           
            <logic:equal name="element" property="state" value="1">
				<input type="checkbox" name="chk" value="1" checked onclick ="setvalid(this,'<bean:write name="element" property="dbname" filter="true"/>','<bean:write name="element" property="a0100" filter="true"/>');"> 
		    </logic:equal>
            <logic:equal name="element" property="state" value="0">
				<input type="checkbox" name="chk" value="0" onclick ="setvalid(this,'<bean:write name="element" property="dbname" filter="true"/>','<bean:write name="element" property="a0100" filter="true"/>');"> 
		    </logic:equal>		    
  	       </td> 	
  	      
            <td align="left" class="RecordRow_locked common_border_color" style="" nowrap>&nbsp;
          
                <hrms:codetoname codeid="@@" name="element" codevalue="dbname" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;     
	    </td>  	       
	    <%
	       LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
	        /* 薪资发放-编辑-变动比对 xiaoyun 2014-10-13 start */
	        //String classname="RecordRow_locked common_border_color";
	    	String classname = "RecordRow_locked common_border_color";
	    	/* 薪资发放-编辑-变动比对 xiaoyun 2014-10-13 end */
	        String b0110=(String)abean.get("b0110");
	        String b01101=(String)abean.get("b01101");
	        if(!b0110.equals(b01101))
	           classname="RecordRow_locked2 common_border_color";
	     %>
	    
            <td align="left" class="<%=classname%>" style="" nowrap>&nbsp;
           
          	<hrms:codetoname codeid="UN" name="element" codevalue="b01101" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;
	    </td>          
	    
            <td align="left" class="<%=classname%>" style="" nowrap>&nbsp;
          
          	<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;
	    </td>   
	    <%
	    	/* 薪资发放-编辑-变动比对 xiaoyun 2014-10-13 start */
	        //String classname2="RecordRow_locked common_border_color";
	    	String classname2="RecordRow_locked common_border_color";
	    	/* 薪资发放-编辑-变动比对 xiaoyun 2014-10-13 end */
	        String e0122=(String)abean.get("e0122");
	        String e01221=(String)abean.get("e01221");
	        String bg="";
	        if(!e0122.equals(e01221)){
	           bg="background-color='#FFF8D2'"; 
	        }
	       String desc=AdminCode.getCodeName("UM",e0122);
	       CodeItem ci = AdminCode.getCode("UM",e0122,Integer.parseInt(dise));
	       if(ci!=null)
	           desc=ci.getCodename();
	       String desc1=AdminCode.getCodeName("UM",e01221);
	       CodeItem ci1=AdminCode.getCode("UM",e01221,Integer.parseInt(dise));
	       if(ci1!=null)
	              desc1=ci1.getCodename();
	     %>
	    
            <td align="left" class="<%=classname2%>" style="<%=bg %>" nowrap>&nbsp;
           
          	<%=desc1%>&nbsp;            
	    </td>
            
            <td align="left" class="<%=classname2%>" style="<%=bg %>" nowrap>&nbsp;
           
          	<%=desc%>&nbsp;            
	    </td>
	    <%
	    	/* 薪资发放-编辑-变动比对 xiaoyun 2014-10-13 start */
	        //String abgcolor="RecordRow_locked common_border_color";
	    	String abgcolor = "RecordRow_locked common_border_color";
	    	/* 薪资发放-编辑-变动比对 xiaoyun 2014-10-13 end */
	        String a0101=(String)abean.get("a0101");
	        String a01011=(String)abean.get("a01011");
	        if(!a0101.equals(a01011))
	           abgcolor="RecordRow_locked2 common_border_color";
	     %>
	    
	     
            <td align="left" class="<%=abgcolor%>" style="" nowrap>&nbsp;
           
                 <bean:write name="element" property="a01011" filter="true"/>&nbsp;
	    </td>	 
	    
            <td align="left" class="<%=abgcolor%> common_border_color" style="border-right: solid 1pt;" nowrap>&nbsp;
           
                 <bean:write name="element" property="a0101" filter="true"/>&nbsp;
	    </td>  
	     <% if(fieldItemList!=null&&fieldItemList.size()>0)
	     { 
             for(int j=0;j<fieldItemList.size();j++)
            {
                FieldItem item = (FieldItem)fieldItemList.get(j);
                String itemid=item.getItemid().toLowerCase();
                String itemidi=itemid+"1";
                String codesetid=item.getCodesetid();
                String itemtype=item.getItemtype();
                LazyDynaBean bean=(LazyDynaBean)pageContext.getAttribute("element");
                String itemdesc=(String)bean.get(itemid);
                String itemdesci=(String)bean.get(itemidi);
                String bgcolor="";
                if(!itemdesc.equalsIgnoreCase(itemdesci))
                  bgcolor="bgcolor='#FFF8D2'"; 
                if(itemtype.equalsIgnoreCase("N"))
                {
                  
                  BigDecimal a=new BigDecimal(itemdesc.equals("")?"0.00":itemdesc);
                  BigDecimal b=new BigDecimal(itemdesci.equals("")?"0.00":itemdesci);
                  if(a.compareTo(b)==0)
                     bgcolor="";
         %>
                 <td align="right" class="RecordRow" style="border-left:none;border-top: none;" <%=bgcolor%> nowrap>&nbsp;
                 <bean:write name="element" property="<%=itemid%>" filter="true"/>&nbsp;
	              </td>  
                  <td align="right" class="RecordRow" style="border-left:none;border-top: none;" <%=bgcolor%> nowrap>&nbsp;
                 <bean:write name="element" property="<%=itemidi%>" filter="true"/>&nbsp;
	              </td>  
	              <%}
	              else if(!codesetid.equals("0"))
	              {
	               %>
	            <td align="left" class="RecordRow" style="border-left:none;border-top: none;" <%=bgcolor%> nowrap>&nbsp;
          	<hrms:codetoname codeid="<%=codesetid%>" name="element" codevalue="<%=itemid%>" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;            
	    </td>
	    
	    <td align="left" class="RecordRow common_border_color" style="border-left:none;border-top: none;" <%=bgcolor%> nowrap>&nbsp;
          	<hrms:codetoname codeid="<%=codesetid%>" name="element" codevalue="<%=itemidi%>" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;            
	    </td>
	               <%}
	               else
	               {
	                %>
	                 <td align="left" class="RecordRow common_border_color" style="border-left:none;border-top: none;" <%=bgcolor%> nowrap>&nbsp;
                 <bean:write name="element" property="<%=itemid%>" filter="true"/>&nbsp;
	              </td>  
                  <td align="left" class="RecordRow common_border_color" style="border-left:none;border-top: none;" <%=bgcolor%> nowrap>&nbsp;
                 <bean:write name="element" property="<%=itemidi%>" filter="true"/>&nbsp;
	              </td>  
	                
           <%
           }
            }    
        }
        
        %>    
        <%
	       if(itemo!=null)
	       {
	        if(itemo.getItemtype().equalsIgnoreCase("a")&&!itemo.getCodesetid().equals("0"))
	        {
	         %>
	              <td align="left" class="RecordRow common_border_color" style="border-left:none;border-top: none;" nowrap>&nbsp;
             	<hrms:codetoname codeid="<%=itemo.getCodesetid() %>" name="element" codevalue="<%=itemo.getItemid()%>" codeitem="codeitem" scope="page"/>  	      
              	<bean:write name="codeitem" property="codename" />&nbsp;            
	           </td>
	         <%
	        }
	        else
	        {
	           String align="left";
	           if(itemo.getItemtype().equalsIgnoreCase("N"))
	              align="right";
	         %>
	            <td align="<%=align%>" class="RecordRow common_border_color" style="border-left:none;border-top: none;border-right: none;" nowrap>&nbsp;
                 <bean:write name="element" property="<%=itemo.getItemid()%>" filter="true"/>&nbsp;
	            </td>
	         <%
	        }
	       }
	     %>

     	</tr>
     	<% i++; %>
        </hrms:paginationdb>    
	</tbody>
</table>
</div>
<div style="position:absolute;width:100%;top:399px;left:5;right:5">
<table width="100%" align="left" class="ListTable">
<tr>
<td class="RecordRow" >
<table  width="100%" align="left" >
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="changeInfoForm" property="pagination" nameId="changeInfoForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table> 
</td>
</tr>
          <tr>
            <td align="left">
	 	      <html:button styleClass="mybutton" property="b_export" onclick="exports()">
            		<bean:message key="button.export"/>
	 	      </html:button>  	                 	      
            </td>
          </tr>   

</table>
</div>


</html:form>
</body>

