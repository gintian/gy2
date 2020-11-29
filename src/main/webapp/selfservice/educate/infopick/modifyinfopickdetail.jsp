<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*"%>
<%@ page import="com.hjsj.hrms.transaction.train.BusifieldBean"%>
<%@ page import="com.hjsj.hrms.transaction.train.RelatingFactory"%>
<%@ page import="com.hjsj.hrms.transaction.train.RelatingcodeBean"%>
<%@ page import="org.apache.commons.beanutils.DynaBean"%>
<jsp:useBean id="doCodeBean" class="com.hjsj.hrms.transaction.train.DoCodeBean" scope="page"/>
<jsp:useBean id="relatingFactory" class="com.hjsj.hrms.transaction.train.RelatingFactory" scope="session"/>
<jsp:useBean id="infoPickForm" class="com.hjsj.hrms.actionform.train.InfoPickForm" scope="session"/>
<%
	
		relatingFactory.getInstance();
 int i=0;
 int flag=0;
 int count=0;
%>


<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
  
  
function checkDate(datestr)
{
	var reg = new RegExp("^\\d{4}[\\.]\\d{1,2}[\\.]\\d{1,2}$");
    if(!reg.exec(datestr.value)){
        alert("日期格式不正确,正确格式为yyyy.mm.dd!");
            return false;
    }
    var tmpy="";
    var tmpm="";
    var tmpd="";
    var status=0;  
    for (i=0;i<datestr.value.length;i++)
    {
        if (datestr.value.charAt(i)== '.')
        {
            status++;
        }
      
        if ((status==0) && (datestr.value.charAt(i)!='.'))
        {
            tmpy=tmpy+datestr.value.charAt(i)
        }
        if ((status==1) && (datestr.value.charAt(i)!='.'))
        {
            tmpm=tmpm+datestr.value.charAt(i)
        }
        if ((status==2) && (datestr.value.charAt(i)!='.'))
        {
            tmpd=tmpd+datestr.value.charAt(i)
        }
    }
   
      if (status!=2)
      {

            alert("日期格式不正确,正确格式为yyyy.mm.dd!");
            return false;
      }
    
    
    year=new String (tmpy);
    month=new String (tmpm);
    day=new String (tmpd)
    if(tmpm.length==0) month=new String ('01');
    if(tmpd.length==0) //day=new String ('01')
    {
        alert("日期格式不正确,正确格式为yyyy.mm.dd!");
        return false;
    }
    //tempdate= new String (year+month+day);
    //alert(tempdate);
    if ((tmpy.length!=4) || (tmpm.length>2) || (tmpd.length>2))
    {
        alert("日期格式不正确,正确格式为yyyy.mm.dd!");
        return false;
    }
    
    if(tmpy<1753)
    {
          alert("输入了不合法日期！");
     	  return false
    }
    
    if (!((1<=month) && (12>=month) && (31>=day) && (1<=day)) )
    {
        alert ("月份或天不正确!");
        return false;
    }
    if (!((year % 4)==0) && (month==2) && (day==29))
    {
        alert ("不是闰年!");
        return false;
    }
    if ((month<=7) && ((month % 2)==0) && (day>=31))
    {
        alert ("此月份是小月?");
        return false;
    }
    if ((month>=8) && ((month % 2)==1) && (day>=31))
    {
        alert ("此月份是小月!");
        return false;
    }
    if ((month==2) && (day==30))
    {
        alert("二月份没有此日!");
        return false;
    }
    if(datestr.value.substr(0,4)<1753)
    {
        alert("输入了不合法日期！");
     	return false
    }
    return true;
}	


  function validate3(xname)
  {
  	
  		var val = MM_findObj_( xname );	
		if ( val.value=='') 
		{
		alert ("时间长度不能为空！");
		val.focus();
		return false;
    		}	
  
   	 var tag=true;  
         <logic:iterate  id="element"    name="infoPickForm"  property="infoDetailAddList" indexId="index"> 
          <logic:equal name="element" property="itemtype" value="A">   
          var valueInputs=document.getElementsByName('<%="infoDetailAddList["+index+"].value"%>');
        
          var dobj=valueInputs[0];
          if(dobj.value=='')
          {
          <%
          	BusifieldBean busb2=(BusifieldBean)element;          	
                String str=busb2.getItemdesc()+"不能为空!";
          %>
          	alert('<%=str%>');
          	
          	return false;
          }
         
        </logic:equal>          
        <logic:equal name="element" property="itemtype" value="D">   
          var valueInputs=document.getElementsByName('<%="infoDetailAddList["+index+"].value"%>');
          var dobj=valueInputs[0];
          if(dobj.value=='')
          {
          	  <%
          	BusifieldBean busb2=(BusifieldBean)element;
                String str=busb2.getItemdesc()+"不能为空!";
                  %>
          	alert('<%=str%>');
          	
          	return false;
          }
          
          tag= checkDate(dobj) && tag;      
	  if(tag==false)
	  {
	    dobj.focus();
	    return false;
	  }
        </logic:equal> 
        <logic:equal name="element" property="itemtype" value="N"> 
           <logic:equal name="element" property="decimalwidth" value="0"> 
             var valueInputs=document.getElementsByName('<%="infoDetailAddList["+index+"].value"%>');
             var dobj=valueInputs[0];
             if(dobj.value=='')
             {
             	  <%
          	BusifieldBean busb2=(BusifieldBean)element;
                String str=busb2.getItemdesc()+"不能为空!";
                  %>
          	alert('<%=str%>');
          	
          	return false;
             }
             /*
              tag=checkNUM1(dobj) &&  tag ;  
	      if(tag==false)
	      {
	        dobj.focus();
	        return false;
	      }
	      */
	    </logic:equal>
	    <logic:notEqual name="element" property="decimalwidth" value="0"> 
	     var valueInputs=document.getElementsByName('<%="infoDetailAddList["+index+"].value"%>');
             var dobj=valueInputs[0];
             if(dobj.value=='')
             {
             	  <%
          	BusifieldBean busb2=(BusifieldBean)element;
                String str=busb2.getItemdesc()+"不能为空!";
                  %>
          	alert('<%=str%>');
          	
          	return false;
             }
             /*
             tag=checkNUM2(dobj,${element.itemlength},${element.decimalwidth}) &&  tag ;  
              if(tag==false)
	      {
	        dobj.focus();
	        return false;
	      }
	      */
	    </logic:notEqual>
	</logic:equal>  
      </logic:iterate>    
      
     return tag;  
  }
function MM_findObj_(n, d)
{
	var p,i,x;

	if(!d)
		d=document;

	if((p=n.indexOf("?"))>0&&parent.frames.length)
	{
		d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);
	}
	if( !(x=d[n]) && d.all )
		x=d.all[n];

	for (i=0;!x&&i<d.forms.length;i++)
		x=d.forms[i][n];

	for(i=0;!x&&d.layers&&i<d.layers.length;i++)
		x=MM_findObj_(n,d.layers[i].document);

	return x;
}

function checkNum (xname)
	{
		var InString=xname.value;
		
			if(InString=='')
			{
			}
			else
			{
						
				if(isNaN(InString))
				{
					 alert(" 必须填写数字！");
					 xname.value='';
					 xname.select();
					 return (true);
				}
			}
	   		
		
		return (false);
	}
	function isnull(xname)
	{	
		var val = MM_findObj_( xname );	
		if ( val.value=='') 
		{
		alert ("时间长度不能为空！");
		val.focus();
		return false;
    		}
    		else
    		{
    			if(confirm('确认提交吗?'))
			{
			return true;
			}
			else
			{
		  	 return false;
			}
    		}
       }
       
	   /**
	   * 数据长度验证
	   */
       function checkNumPublic(xname,len1,len2)
	   {
				    var i,j,strTemp;
					var str1,str2;
					var n=0;
					var num2;
					var NUM;
					var valueInputs;
					/*
					if(parseInt(flag)==1)
					{
					 valueInputs=document.forms[0].elements[8];
					}
					if(parseInt(flag)==2)
					{
						valueInputs=document.forms[0].elements[5]
					}
					*/
					var dobj=xname;
									   			
				 	NUM=dobj;			  
					strTemp="0123456789.";
					if ( NUM.value.length== 0)
					{
						return true;
					}    
					for (i=0;i<NUM.value.length;i++)
					{
						j=strTemp.indexOf(NUM.value.charAt(i)); 
						if (j==-1)
						{
							//说明有字符不是数字
							alert('请输入数字！');
							dobj.value='';
							dobj.select();
							// return (true);
							return false;
						}   
						if(NUM.value.charAt(i)==".")
						{
							n=n+1;
						}   
						if(n>1)
						{
							alert('输入数据不能有两个以上的小数点!');
							dobj.value='';
							dobj.select();
							return false;
						}
					}
					 //alert(NUM.value  + "dd"  + len1);
				  
					if(NUM.value.indexOf(".")!=-1)
					{
						str1 = NUM.value.substr(0,NUM.value.indexOf("."));
						str2 = NUM.value.substr(NUM.value.indexOf(".")+1,NUM.value.length);
						
						if(str1.length>len1)
						  {
							alert('整数部分位数最大为'+len1);
							dobj.value='';
							dobj.select();
							return false;
						  }
						if(str2.length>len2)
						{
							alert('小数部分位数最大为'+len2);
							dobj.value='';
							dobj.select();
							return false;
						}
					}
					else
					{
						str1 = NUM.value;
						if(str1.length>len1)
						  {
							alert('整数部分位数最大为'+len1);
							dobj.value='';
							dobj.select();
							return false;
						  }
					}   
					//说明是数字
					return true;
    }
</script>
<html:form action="/selfservice/educate/infopick/addinfopickdetail">
      <br>
      <br>
      <table width="400" border="0" cellpadding="0" cellspacing="0"  align="center">
          <tr height="20">
       		<!-- <td width=4 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="lable.infopick.repair"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top"  width="750" class="tabremain" ></td> -->  
       		<td  align=center class="TableRow">&nbsp;<bean:message key="lable.infopick.repair"/>&nbsp;</td>            	      
          </tr> 
          <tr>
            <td  class="framestyle9">
              	<table border="0" cellpmoding="0" cellspacing="8"  class="DetailTable"  cellpadding="0" align="center">
       			
        		<tr class="list3"> 
         			 <td align="left" nowrap valign="center">
         			 	<hrms:fieldtoname name="infoPickForm" fieldname="R2206" fielditem="fielditem"/>
					<bean:write name="fielditem" property="dataValue"/>&nbsp;
         			</td>
          			<td>
          				&nbsp;<html:text name="infoPickForm" property="infoPickDetailvo.string(r2206)" styleClass="text6" maxlength="${infoPickForm.timeLength}" onchange="checkNumPublic(this,${infoPickForm.timeLength},${infoPickForm.timeDecimalwidth})" />
          			</td>
          						<!--采集表明细操作-->
        		 <%
 				i=1;
 				flag=1;
 				count=((ArrayList)infoPickForm.getInfoDetailAddList()).size();
 			%>
        		<logic:iterate  id="element"    name="infoPickForm"  property="infoDetailAddList" indexId="index"> 
        				<logic:equal name="element" property="codesetid" value="0">
      				
      				 	<%
          				if(flag==0){
            				 if(i%2==0){
           				 %>
             				 <tr class="trShallow1">            
           				  <%}
            				 else
            				 {%>
             				  <tr class="trDeep1">  
            				 <%}
           				  i++;
           				  flag=1;          
           				  }else{
           				    flag=0;           
          				   }%>
          				    
          				     <td align="right" nowrap valign="top">        
          					  &nbsp;&nbsp;&nbsp;&nbsp;
          					  <bean:write  name="element" property="itemdesc"/>&nbsp;          
        				     </td>
          				   	<logic:equal name="element" property="itemtype" value="A">
          				    	 <td align="left"  nowrap valign="top">
            					 &nbsp;<html:text   name="infoPickForm" property='<%="infoDetailAddList["+index+"].value"%>'  styleClass="text6" maxlength="${element.itemlength}" /> &nbsp;&nbsp;&nbsp;&nbsp;  
         				  	</td> 	
         				  	</logic:equal>
          				    	<logic:equal name="element" property="itemtype" value="D">
          				    	 <td align="left"  nowrap valign="top">
            					 &nbsp;<html:text   name="infoPickForm" property='<%="infoDetailAddList["+index+"].value"%>'  styleClass="text6" maxlength="${element.itemlength}" /> &nbsp;&nbsp;&nbsp;&nbsp;  
         				  	</td> 	
         				  	</logic:equal>
         				  	<logic:equal name="element" property="itemtype" value="N">
            					 <td align="left"  nowrap valign="top">
            					 <logic:equal name="element" property="decimalwidth" value="0">
            					 &nbsp;<html:text   name="infoPickForm" property='<%="infoDetailAddList["+index+"].value"%>'  styleClass="text6" maxlength="${element.itemlength + element.decimalwidth}" onchange="checkNum(this);" /> &nbsp;&nbsp;&nbsp;&nbsp;  
            					 	</logic:equal>
            					 	<logic:notEqual name="element" property="decimalwidth" value="0">
            					 	 &nbsp;<html:text   name="infoPickForm" property='<%="infoDetailAddList["+index+"].value"%>' styleClass="text6" maxlength="${element.itemlength + element.decimalwidth}" onchange="checkNumPublic(this,'${element.itemlength}','${element.decimalwidth}');" /> &nbsp;&nbsp;&nbsp;&nbsp;  
            					 	</logic:notEqual>
         				  	</td> 
         				  	</logic:equal>
         				  	<logic:equal name="element" property="itemtype" value="M">
         				  	<td align="left"  nowrap valign="top"  colspan="3">
            					 &nbsp;<html:textarea   name="infoPickForm" property='<%="infoDetailAddList["+index+"].value"%>'  styleClass="text6" rows="3"  cols="66" /> &nbsp;&nbsp;&nbsp;&nbsp;  
         				  	</td>
         				  	</logic:equal>
         				  	   				   
         				   <%
         				   	if(flag==0)
         				   	{
         				   %>
         				   </tr>
         				   <%	
         				   	}
         				   	else
         				   	{
         				   		int intIndex=Integer.parseInt(index.toString());
         				   		if(count==intIndex)
         				   		{
         				   		%>
         				   		 <td colspan="2">
              						 </td>
               						</tr>
         				   		<%
         				   		}
         				   	}
         				   %>
      				      				 
      				 </logic:equal>
      				 <logic:notEqual name="element" property="codesetid" value="0">
      				 	<logic:notEqual name="element" property="codeflag" value="1">
      				 	<%
          				if(flag==0){
            				 if(i%2==0){
           				 %>
             				 <tr class="trShallow1">            
           				  <%}
            				 else
            				 {%>
             				  <tr class="trDeep1">  
            				 <%}
           				  i++;
           				  flag=1;          
           				  }else{
           				    flag=0;           
          				   }%>
          				    
          				     <td align="right" nowrap valign="top">        
          					  &nbsp;&nbsp;&nbsp;&nbsp;
          					  <bean:write  name="element" property="itemdesc"/>&nbsp;          
        				     </td>
          				   	 <td align="left"  nowrap valign="top">
          				    	 
                				 
            					 
            					 <logic:notEqual name="element" property="itemdesc" value="审批状态">
	                				  <html:hidden name="infoPickForm" property='<%="infoDetailAddList["+index+"].value"%>'/>  
	                    				  &nbsp;<html:text name="infoPickForm" property='<%="infoDetailAddList["+index+"].viewvalue"%>' readonly="true"  styleClass="text6"/> 
	                   				  <img  src="/images/code.gif" onclick="javascript:openInputCodeDialog('${element.codesetid}','<%="infoDetailAddList["+index+"].viewvalue"%>');"/>&nbsp;
	            					 &nbsp;&nbsp;&nbsp;&nbsp;  
	            				 </logic:notEqual>
            					 <logic:equal name="element" property="itemdesc" value="审批状态">
         				  			 <html:hidden name="infoPickForm" property='<%="infoDetailAddList["+index+"].value"%>'/>  
                    				  &nbsp;<html:text name="infoPickForm" property='<%="infoDetailAddList["+index+"].viewvalue"%>' readonly="true"  styleClass="text6"/> 
                   				 
            					 &nbsp;&nbsp;&nbsp;&nbsp;  &nbsp;&nbsp;
         				  		 </logic:equal>
            					 
            					 
            					 
         				  	</td> 	
         				  		   
         				   
         				   <%
         				   	if(flag==0)
         				   	{
         				   %>
         				   </tr>
         				   <%	
         				   	}
         				   	else
         				   	{
         				   		int intIndex=Integer.parseInt(index.toString());
         				   		if(count==intIndex)
         				   		{
         				   		%>
         				   		 <td colspan="2">
              						 </td>
               						</tr>
         				   		<%
         				   		}
         				   	}
         				   %>
      				      				 
      				 	</logic:notEqual>
      				 	
      				 	<logic:equal name="element" property="codeflag" value="1">
      				 		<%
          				if(flag==0){
            				 if(i%2==0){
           				 %>
             				 <tr class="trShallow1">            
           				  <%}
            				 else
            				 {%>
             				  <tr class="trDeep1">  
            				 <%}
           				  i++;
           				  flag=1;          
           				  }else{
           				    flag=0;           
          				   }%>
          				    
          				     <td align="right" nowrap valign="top">        
          					  &nbsp;&nbsp;&nbsp;&nbsp;
          					  <bean:write  name="element" property="itemdesc"/>&nbsp;          
        				     </td>
          				   	 <td align="left"  nowrap valign="top">
          				    	 
                				  <html:hidden name="infoPickForm" property='<%="infoDetailAddList["+index+"].value"%>'/>  
                    				  &nbsp;<html:text name="infoPickForm" property='<%="infoDetailAddList["+index+"].viewvalue"%>' readonly="true"  styleClass="text6"/> 
                   				  <img  src="/images/code.gif" onclick='javascript:openInputRelCodeDialog("${element.codesetid}","<%="infoDetailAddList["+index+"].viewvalue"%>","${element.relTableName}","${element.relFieldId}","${element.relFieldDesc}","${userView.userOrgId}");'/>&nbsp;
            					 &nbsp;&nbsp;&nbsp;&nbsp;  
         				  	</td> 	
         				          				   
         				   <%
         				   	if(flag==0)
         				   	{
         				   %>
         				   </tr>
         				   <%	
         				   	}
         				   	else
         				   	{
         				   		int intIndex=Integer.parseInt(index.toString());
         				   		if(count==intIndex)
         				   		{
         				   		%>
         				   		 <td colspan="2">
              						 </td>
               						</tr>
         				   		<%
         				   		}
         				   	}
         				   %>
      				 	</logic:equal>
      				 </logic:notEqual>
        		</logic:iterate>
        		
     		 </table>
            </td>
          </tr>
                 
               
                                                            
          <tr class="list3">
            	<td align="center" style="height:35px;">
         	<hrms:submit styleClass="mybutton" property="b_savemodify" onclick="document.infoPickForm.target='_self';validate('F','infoPickDetailvo.string(r2206)','时间长度');return (document.returnValue && validate3('infoPickDetailvo.string(r2206)'));">
            		<bean:message key="button.save"/>
	 	</hrms:submit>
		<html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset>	 	
         	            
            </td>
          </tr>          
      </table>
	  <br>
     
<table width="500" border="0" cellpadding="0"  cellspacing="0" class="ListTable" align="center">
 <thead>
           <tr>
             <td align="center" class="TableRow" nowrap>
		<bean:message key="column.select"/>&nbsp;
            </td>          
           
            <td align="center" class="TableRow" nowrap>
		<hrms:fieldtoname name="infoPickForm" fieldname="R2206" fielditem="fielditem"/>
		<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
	     <logic:iterate id="elementcol" name="infoPickForm" property="dynamicColDetail">
	    <%
	     		BusifieldBean busb=(BusifieldBean)elementcol;
	     		String str=busb.getItemid();
	     		str=str.toUpperCase();
	     %>
	      <td align="center" class="TableRow" nowrap>
	     	
		<hrms:fieldtoname name="infoPickForm" fieldname="<%=str%>" fielditem="fielditem"/>
		<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>  
	    </logic:iterate>  	   
            <td align="center" class="TableRow" nowrap>
		 <bean:message key="label.edit"/>            	
	    </td>
	    
	    	    	    	    		        	        	        
           </tr>
        </thead>
     	
     <hrms:extenditerate id="element" name="infoPickForm" property="infoPickDetailForm.list" indexes="indexes"  pagination="infoPickDetailForm.pagination" pageCount="10" scope="session">
                   
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
          i++;          
          %>  
          	<td align="left" class="RecordRow" nowrap >
           		
           	    <hrms:checkmultibox name="infoPickForm" property="infoPickDetailForm.select" value="true" indexes="indexes"/>
           	</td>
           	
           	<td align="right" class="RecordRow" nowrap >
           	     <bean:write name="element" property="r2206" filter="true"/>&nbsp;
           	</td> 
           	<logic:iterate id="elementcol" name="infoPickForm" property="dynamicColDetail">
            	        <logic:equal name="elementcol" property="itemtype" value="N">
	  	  				<td align="right" class="RecordRow" width="100" style="word-break:break-all" nowrap>
	  	  	</logic:equal>
	  	  	<logic:notEqual name="elementcol" property="itemtype" value="N">
	  	  				<td align="left" class="RecordRow" width="100" style="word-break:break-all" nowrap>
	  	  	</logic:notEqual>
            	 	<logic:equal name="elementcol" property="codesetid" value="0">
	     		 <bean:write name="element" property="${elementcol.itemid}" filter="true"/>&nbsp; 
	     	  	</logic:equal>
	  	  	<logic:notEqual name="elementcol" property="codesetid" value="0">
	  	  		<logic:equal name="elementcol" property="codeflag" value="1">
	  	  		
	  	  		<%
	  	  			DynaBean voelement1=(DynaBean)element;
	  	  			BusifieldBean busb2=(BusifieldBean)elementcol;
	  	  			RelatingcodeBean rcb2=relatingFactory.getDisplayField(busb2);
	  	  			
	  	  			String str2=busb2.getItemid();
	  	  			String strcodeid2=rcb2.getCodesetid();
	  	  			if(str2.equals("") || strcodeid2.equals(""))
	  	  			{
	  	  			out.println("&nbsp;");
	  	  			}
	  	  			else
	  	  			{
	  	  			
	  	  			out.println(doCodeBean.getRelCodeName(rcb2,voelement1.get(str2).toString()));
	  	  			}
	  	  			
	  	  		%>
	  	  		
	  	  		</logic:equal>
	  	  		<logic:notEqual name="elementcol" property="codeflag" value="1">
	  	  		
	  	  		<%
	  	  			DynaBean voelement=(DynaBean)element;
	  	  			BusifieldBean busb1=(BusifieldBean)elementcol;
	     				String str1=busb1.getItemid();
	     				String strcodeid=busb1.getCodesetid();
	     				if(str1.equals("") || strcodeid.equals(""))
	     				{
	     				out.println("&nbsp;");
	     				}
	     				else
	     				{
	     					out.println(doCodeBean.getCodeName(strcodeid,voelement.get(str1).toString()));
	     				}
	     				
	  	  		%>
	  	  				  	
	  	  		
	  	  		</logic:notEqual>
	  	  	</logic:notEqual>
	  	  	 </td>
	   	 </logic:iterate>  
           	
           	<td align="center" class="RecordRow" nowrap>
           		<a href="/selfservice/educate/infopick/addinfopickdetail.do?b_modify=link&r22id=<bean:write name="element" property="r2202" filter="true"/>&r19id=<bean:write name="element" property="r2201" filter="true"/>"><img src="/images/edit.gif" border=0></a>
           	</td>
            
          </tr>   
                      
            </hrms:extenditerate>    
   </table>
   <table  width="50%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="infoPickForm" property="infoPickDetailForm.pagination.current" filter="true" />
					页
					共
					<bean:write name="infoPickForm" property="infoPickDetailForm.pagination.count" filter="true" />
					条
					共
					<bean:write name="infoPickForm" property="infoPickDetailForm.pagination.pages" filter="true" />
					页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="infoPickForm" property="infoPickDetailForm.pagination"
				nameId="infoPickDetailForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
   <center>
   <table>                                             
          <tr class="list3">
            	<td align="center" >
         		 	
         	 
	 	<hrms:submit styleClass="mybutton" property="b_deletemodify">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>          
            </td>
          </tr>          
      </table>
      </center>
</html:form>

