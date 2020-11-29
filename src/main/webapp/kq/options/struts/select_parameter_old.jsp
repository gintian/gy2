<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" href="/css/tabpane.css" type="text/css">

 <SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>  
<script language="JavaScript">
	 function saves()
   {
   
      kqStrutForm.action="/kq/options/struts/select_parameter.do?b_savec=link";
      kqStrutForm.submit();
   }  
   function save()
   {
      kqStrutForm.action="/kq/options/struts/select_parameter.do?b_ok=link";
      kqStrutForm.submit();
   }
   
    function secBoard(n)
  {

    for(i=0;i<mainTable.tBodies.length;i++)
      mainTable.tBodies[i].style.display="none";
    mainTable.tBodies[n].style.display="block";
       
  }
 function sss()
	{
	
	 var tag=true; 
	 var n=0,m=0;
	 var fir="",too="";
	 var mm=Array();
	 var ver=Array();
	 var ves=Array();
	 
	 var tem=Array();
	 var tes=Array();
	 
	   mm[0]=$F('one');
	   mm[1]=$F('ones');
	   mm[2]=$F('two');
	   mm[3]=$F('twos');
	   mm[4]=$F('thre');
	   mm[5]=$F('thres');
	   mm[6]=$F('four');
	   mm[7]=$F('fours');
	   
	   
	   
	for(m=0;m<7;m++)
		{
		   tag=isNull(mm[m],mm[m+1]);
		   if(tag==false)
          {
              return false;
         }
		     m++;
		}
	
  for(n=0;n<=7;n++)
  {
     var aa=0,cc=0,dd;
     if(mm[n].length==5||mm[n].length==3||mm[n].length==4)
      {  
        ver=mm[n].split(":");
        if(ver.length!=2)
        {
          alert("<bean:message key="error.kq.setime"/>");
          return false;
        }
       
        if(ver[0].length==1)
        { 
           fir=0+ver[0];
        }else{
           fir=ver[0];
        }
        if(ver[1].length==1)
        { 
          too=0+ver[1];
        }else{
           too=ver[1];
        }
        dd=fir+":"+too+":00";

        tag=isTime(dd);
        if(tag==false)
         {
            return false;
         }
        
        for(cc=n;cc<mm.length;cc++)
         {
             if(mm[cc]!=null&&mm[cc].length!=0)
              {
                 ves=mm[cc].split(":");
                  if(ves[0]==ver[0])
                      tag=over(ves[1],ver[1]);
                   else
                      tag=over(ves[0],ver[0]);
                      if(tag==false)
                      {
                           tso=mm[0].split(":");
                         if(tso[0]==ver[0])
                            tag=over(tso[1],ves[1]);
                         else
                            tag=over(tso[0],ves[0]);

                          if(tag==false)
                           {
                               tem=mm[2].split(":");
                                if(tem[0]==ver[0])
                                    tag=over(tem[1],ves[1]);
                                 else
                                    tag=over(tem[0],ves[0]);
                              if(tag==false)
                               {
                                   
                                   tes=mm[4].split(":");
                                  if(tes[0]==ver[0])
                                    tag=over(tes[1],ves[1]);
                                  else
                                     tag=over(tes[0],ves[0]);
                                if(tag==false)
                                 {
                           	  	    alert("<bean:message key="error.kq.exchage"/>");
                                    return false;
                                  }
                              }
                          }
                      }
               }
           }
       }
       if((mm[n].length!=5&&mm[n].length!=4&&mm[n].length!=3)&&mm[n].length!=0)
       {
          alert("<bean:message key="error.kq.setrue"/>");
           return false;
       }
    }
	  
 	  kqStrutForm.action="/kq/options/struts/select_parameter.do?b_saveb=link";
    kqStrutForm.submit();
	   
	}
	function over(str,str2)
	{
	    var u=0;
	     u= str-str2;
	     if(u<0)
	     	{
           return false;
	     	}
	}
	function isNull(str,str2)
	{
	     if((str.length!=0&&str2.length!=0)||(str.length==0&&str2.length==0))
	     {
	         return true;
	     }else{
	       	alert("<bean:message key="error.kq.noengh"/>");
           return false;
	     	}
	}
	 function isTime(str)
    {
         var  a=str.match(/^(\d{1,2})(:)?(\d{1,2})\2(\d{1,2})$/);
        if (a==null)
         {
       
             alert('<bean:message key="error.kq.setime"/>');
             return false;
          }
        if(a[1]>24||a[3]>60 )
        {
            alert("<bean:message key="error.kq.setrue"/>");
            return false;
        }
         return true;
    }
</script>
	
<% int i=0;
%>
<html:form action="/kq/options/struts/select_parameter" >
	<table border=1 cellSpacing=0 cellPadding=0   align="center">
		<tr>
			<td>
	    <TABLE align=left  cellSpacing=0 cellPadding=0 width=549 border=0 class="framestyle">
         <TBODY>
           <TR align=left height=30>
           <td>
          <div id="header" >
           <ul>
             <li><a href="/kq/options/struts/select_parameter.do?b_query=link&mess=1" ><font style=font:10pt face=新宋体 color=#33ccff><bean:message key="kq.strut.param"/></font></a></li>
             <li><a href="/kq/options/struts/select_parameter.do?b_query=link&mess=2" ><font style=font:10pt face=新宋体 color=#33ccff><bean:message key="kq.strut.wtime"/></font></a></li>
             <li><a href="/kq/options/struts/select_parameter.do?b_query=link&mess=3"><font style=font:10pt face=新宋体 color=#33ccff><bean:message key="kq.strut.ku"/></font></a></li>
           </ul>
          </div>
         </td>
        </TR>
       </TBODY>
      </TABLE>
    </td>
  </tr>  
  <tr>
  	<td>
    <TABLE align=left  height=220 cellSpacing=0 cellPadding=0 width=549 border=0>
        <TR>
         <TD vAlign=top align=middle><BR>
         	 <logic:equal name="kqStrutForm" property="sige" value="1">
         		<table border="0" cellspacing="0" width="350" align="center" cellpadding="2" >
            <tr><td>
	          <fieldset align="center"  style="width:100%;">
         		 <legend ><bean:message key="kq.kq_rest.shuoming"/></legend>
           <bean:message key="kq.strut.infos"/>
           </fieldset>
          </td>
          </tr>
        </table>
        <br>
        	<fieldset align="center" style="width:45%;">
           <legend ><bean:message key="kq.strut.param"/></legend>
           <table border="0" cellspacing="0" width="250" align="center" cellpadding="2" >
           	<tr>
            <td align="right" nowrap valign="left"><bean:message key="kq.strut.gno"/> </td>
             <td align="left"  nowrap valign="left">
             <html:select name="kqStrutForm" property="kq_g_no" size="1" >
                <html:optionsCollection  property="nlist" value="dataValue" label="dataName"/>
              </html:select>     
             </td>		
           </tr> 
            <tr>
              <td align="right" nowrap valign="left"><bean:message key="kq.strut.cno"/></td>
              <td align="left"  nowrap valign="center"> 
	            <html:select name="kqStrutForm" property="kq_cardno" size="1" >
                <html:optionsCollection  property="nlist" value="dataValue" label="dataName"/>
              </html:select> 
             </td>
           </tr>
          <tr >
           <td align="right" nowrap valign="left"><bean:message key="kq.strut.expr"/></td>
            <td align="left"  nowrap valign="left">
             <html:select name="kqStrutForm" property="kq_type" size="1" >
               <html:optionsCollection  property="tlist" value="dataValue" label="dataName"/>
              </html:select> 
            </td>		
          </tr> 
         </table>
       </fieldset>
       <br>
       <br>
      </logic:equal>
      </td>		
    </tr> 
     <TR>
      <TD vAlign=top align=middle><BR>
      	<logic:equal name="kqStrutForm" property="sige" value="2">
       <fieldset align="center"  style="width:50%;">
	      <legend ><bean:message key="kq.kq_rest.shuoming"/></legend>
         <bean:message key="kq.strut.info"/>
	       </fieldset>
          <br>
          <br>
           <table width="300" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
            <!--  <td width=10 valign="top" class="tableft"></td>
            <td width=130 align=left class="tabcenter">&nbsp;<bean:message key="kq.strut.stime"/>&nbsp;</td>
            <td width=10 valign="top" class="tabright"></td>
            <td valign="top" class="tabremain" width="250"></td>--> 
            <td  align=center class="TableRow">&nbsp;<bean:message key="kq.strut.stime"/>&nbsp;</td>            	      
           </tr>      
          <tr>
           <td class="framestyle9">           	
             <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
              <tr class="list3">
                <td align="center" height="28" nowrap >&nbsp;<bean:message key="recidx.label"/>&nbsp;</td> 
                <td align="center" height="28" nowrap ><bean:message key="kq.strut.start"/></td> 
                <td align="center" height="28" nowrap ><bean:message key="kq.strut.end"/></td> 
              </tr>
              <tr class="list3">
               <td align="center" height="28" nowrap >1</td> 
               <td align="left" height="28" nowrap valign="center">
           	    <html:text name="kqStrutForm" property="one" value="${kqStrutForm.one}" styleClass="text" size="15"   maxlength="5"/>
           	   </td>
                <td align="left" height="28" nowrap >
                <html:text name="kqStrutForm" property="ones" value="${kqStrutForm.ones}" styleClass="text" size="15"  maxlength="5"/>    	      
               </td>
             </tr>
             <tr class="list3">
              <td align="center" height="28" nowrap >2</td> 
                <td align="left" height="28" nowrap valign="center">
           	    <html:text name="kqStrutForm" property="two" value="${kqStrutForm.two}" styleClass="text" size="15"  maxlength="5"/>
           	    </td>
               <td align="left" height="28" nowrap >
                <html:text name="kqStrutForm" property="twos" value="${kqStrutForm.twos}" styleClass="text" size="15" maxlength="5"/>    	      
               </td>
             </tr>
             <tr class="list3">
               <td align="center" height="28" nowrap >3</td> 
               <td align="left" height="28"  nowrap valign="center">
           	   <html:text name="kqStrutForm" property="thre" value="${kqStrutForm.thre}"  styleClass="text" size="15"  maxlength="5"/>
           	  </td>
              <td align="left" height="28" nowrap >
               <html:text name="kqStrutForm" property="thres" value="${kqStrutForm.thres}" styleClass="text" size="15"  maxlength="5"/>    	      
              </td>
            </tr>
           <tr class="list3">
              <td align="center" height="28" nowrap >4</td> 
              <td align="left" height="28" nowrap valign="center">
           	   <html:text name="kqStrutForm" property="four" value="${kqStrutForm.four}" styleClass="text" size="15" maxlength="5"/>&nbsp;&nbsp;&nbsp;&nbsp;
           	  </td>
              <td align="left" height="28" nowrap >
               <html:text name="kqStrutForm" property="fours" value="${kqStrutForm.fours}" styleClass="text" size="15"  maxlength="5"/>    	      
             </td>
          </tr>                   
        </table>     
      </td>
     </tr>
    </table>       
   </logic:equal>
   </td>		
  </tr> 

     <TR>
       <TD vAlign=top align=middle><BR><BR>
       	<logic:equal name="kqStrutForm" property="sige" value="3">
        <fieldset align="center" style="width:45%;">
       	  <legend><FONT COLOR=#000080><bean:message key="label.select"/></font></legend>
       	  <table border="0" cellspacing="0"  align="center" cellpadding="2" >
             <logic:iterate id="element" name="kqStrutForm"  property="slist" indexId="index"> 
              <tr>
               <td align="left" nowrap class="tdFontcolor" colspan="4">
                <logic:equal name="kqStrutForm" property='<%="selist["+index+"]"%>' value="1">
                  <input type="checkbox" name="messi" value="${element.dataValue}" checked="true"><bean:write name="element" property="dataName"/>
                  </logic:equal>    
                 <logic:notEqual name="kqStrutForm" property='<%="selist["+index+"]"%>' value="1">
                	 <input type="checkbox" name="messi" value="${element.dataValue}"><bean:write name="element" property="dataName"/>
                  </logic:notEqual>                                               	      	
                </td>
               </tr>
            </logic:iterate>
      	 </table>
      	</fieldset>
     	 </td>
      </tr> 
  </logic:equal>
   </table>
   </td>
  </tr>

 </table>
    <table width="850" border=0 >
    	<logic:equal name="kqStrutForm" property="sige" value="1">
    		 <tr class="list3">
         <td align="center" colspan="2">
	        	 <hrms:submit styleClass="mybutton" property="b_savea">
            	    <bean:message key="button.save"/>
	     	    </hrms:submit> 
        </td>
       </tr> 
    	</logic:equal>
    	<logic:equal name="kqStrutForm" property="sige" value="2">
       <tr class="list3">
         <td align="center" colspan="2">
	        <input type="button" name="b_saveb" value="<bean:message key="button.save"/>" class="mybutton" onclick="sss()">     
        </td>
       </tr> 
       </logic:equal>
       <logic:equal name="kqStrutForm" property="sige" value="3">
       <tr class="list3">
          <td align="center" colspan="2">
	        	 <hrms:submit styleClass="mybutton" property="b_savec">
            	    <bean:message key="button.save"/>
	     	    </hrms:submit> 
          </td>
       </tr> 
       </logic:equal>
    </table>
  
</html:form>


