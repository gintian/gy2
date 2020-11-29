<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hjsj.hrms.actionform.gz.gz_accounting.piecerate.PieceRateForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
		<%PieceRateForm pieceRateForm = (PieceRateForm) session
					.getAttribute("pieceRateForm");%>
<%
			String userName = null;
			String css_url = "/css/css1.css";
			UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			String codevalue="";
	        String code="";
	        String isAll="";
			if (userView != null) {
				css_url = userView.getCssurl();
				if (css_url == null || css_url.equals(""))
					css_url = "/css/css1.css";
			    if(!userView.isSuper_admin())
	            { 
	               code=userView.getManagePrivCode();
	               codevalue=userView.getManagePrivCodeValue();
	               if("UN".equalsIgnoreCase(code)&&(codevalue==null||codevalue.length()==0)){
	               	isAll="all";
	               }
	             }else{
	             	isAll="all";
	             }  
			}
%>		
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script LANGUAGE=javascript src="/js/xtree.js"></script>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script type="text/javascript" src="/js/dict.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<hrms:themes />
<script language="JavaScript" 	src="/gz/gz_accounting/piecerate/piecerate.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>

<script language="javascript">
function checkValue(obj,itemlength,decimalwidth)
  {
     if(decimalwidth=='')
      return true;
     if(itemlength=='')
      return true;
     var t_len=obj.value;
     if(t_len!="")
     {
        var decimalw=parseInt(decimalwidth,10);	
        var itemlen=parseInt(itemlength,10);	
        var inde=t_len.indexOf(".");
        if(inde==-1)
        {
          if(t_len.length>itemlen)
          {
            alert("整数位长度超过定义"+itemlen+",请修改！");
            obj.focus(); 
            return false;
          }
        }else
        {
           var q_srt=t_len.substring(0,inde);
           var n_srt=t_len.substring(inde+1);           
           if(q_srt.length>itemlen)
           {
             alert("整数位长度超过定义"+itemlen+",请修改！");
             obj.focus(); 
             return false;
           }else if(n_srt.length>decimalw)
           {
              alert("小数位长度超过定义"+decimalw+",请修改！");
              obj.focus(); 
              return false;
           }
        }
     }
  }  
  function validate()
  {
    var tag=true;    
     <logic:iterate  id="element"    name="pieceRateForm"  property="fielditemlist" indexId="index"> 
       <logic:equal name="element" property="visible" value="true">   
      		<bean:define id="fl" name="element" property="fillable"/>
		    <bean:define id="desc" name="element" property="itemdesc"/>
		     var valueInputs=document.getElementsByName("<%="fielditemlist["+index+"].value"%>");
        	  var dobj=valueInputs[0];  
        	  if("${fl}"=="true"&&dobj.value.length<1){
          	  	alert("${desc}"+"必须填写！");
				return ;
          	  }
			  <logic:equal name="element" property="itemtype" value="D">            
			    <logic:notEqual name="element" property="itemlength" value="18">            
			        tag= checkDate(dobj) && tag;      
				  if(tag==false)
				  {
				    dobj.focus();
				    return false;
				  }
				  
			   </logic:notEqual> 
			 </logic:equal> 
		    <logic:equal name="element" property="itemtype" value="N"> 
		           <logic:lessThan name="element" property="decimalwidth" value="1"> 
		             var valueInputs=document.getElementsByName("<%="fielditemlist["+index+"].value"%>");
		             var dobj=valueInputs[0];
		             if (dobj!=null){
			              tag=checkNUM1(dobj) &&  tag ;  
					      if(tag==false)
					      {
					        dobj.focus();
					        return false;
					      }
		             }

			       </logic:lessThan>
			       <logic:greaterThan name="element" property="decimalwidth" value="0"> 
			         var valueInputs=document.getElementsByName('<%="fielditemlist["+index+"].value"%>');
		             var dobj=valueInputs[0];
		             if (dobj!=null){
			             tag=checkNUM2(dobj,${element.itemlength},${element.decimalwidth}) &&  tag ;  
			              if(tag==false)
				        {
				        dobj.focus();
				        return false;
				         }
		             }

			    </logic:greaterThan>
		   </logic:equal>        
 
      </logic:equal> 
   </logic:iterate>    
     return tag;   
  }
  
  
  function addDict(code,obj)
{
  Element.hide('dict');
  var value=obj.value;  
  if(value=="")
   return false;
  var dmobj;
  var vos= document.getElementsByName('dict_box');
  var dict_vo=vos[0];
  var isC=true;
  code_desc=obj;
  for(var i=dict_vo.options.length-1;i>=0;i--)
  {
      dict_vo.options.remove(i);
  }
   var no = new Option();
   no.value="";
   no.text="";
   dict_vo.options[0]=no;
   var r=1;
   for(var i=0;i<g_dm.length;i++)
   {
		dmobj=g_dm[i];	
		if(code=="UM"||code=="@K"||code=="UN")
		{
		    var vos;
		    if(code=="UM")
		    {
		       var b_value="";		       
               var code_value="<%=codevalue%>";
		       if((code_value!=""||'<%=isAll %>'=='all')&&code_value.length<=dmobj.ID.substring(2).length)
		       {
		           
		           if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0&&dmobj.ID.indexOf(code+b_value)==0)||(dmobj.ID.indexOf(code+value)==0&&dmobj.ID.indexOf(code+b_value)==0))
		           {
		             if(dmobj.ID.substring(2).indexOf(code_value)==0)
		              {
		                 var no = new Option();
    	                 no.value=dmobj.ID;
    	                 no.text=dmobj.V;
		                 dict_vo.options[r]=no;
			             r++;
		              }
		           }
		       }else
		       {
		          if(code_value.length>0&&((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0)||(dmobj.ID.indexOf(code+value)==0)))
		          {
		          if(dmobj.ID.substring(2).indexOf(code_value)==0)
		              {
		            var no = new Option();
    	            no.value=dmobj.ID;
    	            no.text=dmobj.V;
		            dict_vo.options[r]=no;
			        r++;
			        }
		          }
		       }
		       
		    }else
		    {
		       if(code_value!=""&&code_value.length>=dmobj.ID.substring(2).length)
		       {
		           if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0&&dmobj.ID.indexOf(code+b_value)==0)||(dmobj.ID.indexOf(code+value)==0&&dmobj.ID.indexOf(code+b_value)==0))
		           {
		              if(code_value.indexOf(dmobj.ID.substring(2))==0)
		              {
		                 var no = new Option();
    	                 no.value=dmobj.ID;
    	                 no.text=dmobj.V;
		                 dict_vo.options[r]=no;
			             r++;
		              }
		           }
		       }else
		       {	
		              if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0&&dmobj.ID.indexOf(code+b_value)==0)||(dmobj.ID.indexOf(code+value)==0&&dmobj.ID.indexOf(code+b_value)==0))
		              {
		               var no = new Option();
    	               no.value=dmobj.ID;
    	               no.text=dmobj.V;
		               dict_vo.options[r]=no;
			           r++;
		             }
		       }
		    }
		    
		}else
		{	 
		  if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0)||(dmobj.ID.indexOf(code+value)==0))
		  {
		    
		    var no = new Option();
    	    no.value=dmobj.ID;
    	    no.text=dmobj.V;
		    dict_vo.options[r]=no;		    
			r++;
		  }
	    }
   } 
   if(r==1)
   {
      obj.value="";
      Element.hide('dict'); 
      return false;      
   }   
   Element.show('dict');  
   var pos=getAbsPosition(obj);  
   with($('dict'))
   {
	   style.position="absolute";
       style.posLeft=pos[0]-1;
 	   style.posTop=pos[1]-1+obj.offsetHeight;
	   style.width=(obj.offsetWidth<150)?150:obj.offsetWidth+1;
   }  
}

  function changetitle(obj){
  	//alert(obj.value);
  	//alert(obj.name);
  	var hashvo=new ParameterSet();
    hashvo.setValue("codeitemid",obj.value);
    hashvo.setValue("uplevel",'${selfInfoForm.uplevel}');
    var request=new Request({method:'post',onSuccess:changetitlevalue,functionId:'02010001016'},hashvo);
  	function changetitlevalue(outparamters){
  		var name=outparamters.getValue("name");
  		var targetobj=document.getElementsByName(obj.name.replace('.value','.viewvalue'))[0];
  		targetobj.title=name;
  	}
  }

 function save(){
	if(validate())
	{	
    	pieceRateForm.action="/gz/gz_accounting/piecerate/search_piecerate.do?b_savetask=link";
	    pieceRateForm.submit();
	}
	else
	{
    	return;
	}
}

 function savegoon(){
	if(validate()){	
		pieceRateForm.action="/gz/gz_accounting/piecerate/search_piecerate.do?b_savetaskgoon=link";
		pieceRateForm.submit();
	}else{
	return;
   }
} 
  
 function returnlist(){
	pieceRateForm.action="/gz/gz_accounting/piecerate/search_piecerate.do?b_query=link";
	pieceRateForm.submit();

} 
  
  

  
</script>


<%int i = 0;
			int flag = 0;

			%>
<%String orgtemp = "";
			String orgtempview = "";
			String postemp = "";
			String postempview = "";
			String kktemp = "";
			String kktempview = "";

			String birthdayfield = "";
			String agefield = "";
			String workagefield = "";
			String postagefield = "";
			String axfield = "";
			String axviewfield = "";
			int rowss;
			%>	
<body>		
			
<br>

<html:form action="/gz/gz_accounting/piecerate/search_piecerate" onsubmit="return validate()">
   <html:hidden name="pieceRateForm" property='managerpriv' />	
<%
			rowss = 1;
			if (pieceRateForm.getFielditemlist().size() > 0) {%>

		<table width="85%" border="0" cellspacing="1" align="center" cellpadding="1">	
   <logic:iterate id="element" name="pieceRateForm" property="fielditemlist" indexId="index">
	 
	   <logic:equal name="element" property="codesetid" value="0">
	      <logic:equal name="element" property="itemtype" value="D">
							<%if (flag == 0) {
					if (i % 2 == 0) {

					%>
							<tr class="trShallow1">
								<%} else {%>
							<tr class="trDeep1">
								<%}
					i++;
					flag = rowss;
				} else {
					flag = 0;
				}%>
								<td align="right" nowrap valign="top">
									&nbsp;&nbsp;&nbsp;&nbsp;
									<hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>									
									&nbsp;
								</td>
								<td align="left"  nowrap valign="top">
								<!--由于底层包已改，18位的也不用特殊处理了，但程序暂不改，等以后此模块正式发版再改wangrd 2013-12-31   -->
									<logic:equal name="element" property="itemlength" value="28">
										 &nbsp;<input type="text" name='<%="fielditemlist["+index+"].value"%>' 
										 value="<bean:write name="pieceRateForm" property='<%="fielditemlist["+index+"].value"%>' />"
											maxlength="19" 
	 										style='border: 1pt solid #C4D8EE;width:200' extra="editor" dataType="simpledate" itemlength=${element.itemlength } 	 class="inputtext"
											 />
	   								</logic:equal>           
							                 &nbsp;<input type="text" name='<%="fielditemlist["+index+"].value"%>' 
							                 value="<bean:write name="pieceRateForm" property='<%="fielditemlist["+index+"].value"%>' />"
							                  style='border: 1pt solid #C4D8EE;width:200' extra="editor" itemlength=${element.itemlength } 
							                  dataType="simpledate" dropDown="dropDownDate" class="inputtext"/>
								             
						             <logic:equal name="element"  property="fillable" value="true">
						               <font color="red">*</font>
						             </logic:equal>
						              	<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemid"%>' />
										<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].fieldsetid"%>' />
										<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemtype"%>' />
										<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemlength"%>' />
										<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].codesetid"%>' />

								
         						</td> 
								<%if (flag == 0) {%>
							</tr>
							<%} else {%>
							<logic:equal name="element" property="rowflag" value="${index}">
								<td colspan="2">
								</td>
								</tr>
							</logic:equal>
							<%}%>
		</logic:equal>
		<logic:equal name="element" property="itemtype" value="A">
							<%if (flag == 0) {
							if (i % 2 == 0) {
		
							%>
									<tr class="trShallow1">
										<%} else {%>
									<tr class="trDeep1">
										<%}
							i++;
							flag = rowss;
						} else {
							flag = 0;
						}%>
					<td align="right" nowrap valign="top">
						&nbsp;&nbsp;&nbsp;&nbsp;
						<hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>									
						&nbsp;
					</td>
					<td align="left" nowrap valign="top">
                 &nbsp;<html:text name="pieceRateForm" property='<%="fielditemlist["+index+"].value"%>' styleClass="textColorWrite" maxlength="${element.itemlength}" onclick="Element.hide('dict');"/>
						<logic:equal name="element" property="fillable" value="true">
							<font color="red">*</font>
						</logic:equal>
						<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemid"%>' />
						<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].fieldsetid"%>' />
						<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemtype"%>' />
						<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemlength"%>' />
						<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].codesetid"%>' />
								</td>
								<%if (flag == 0) {%>
							</tr>
							<%} else {%>
							<logic:equal name="element" property="rowflag" value="${index}">
								<td colspan="2">
								</td>
								</tr>
							</logic:equal>
							<%}%>
		</logic:equal>
		<logic:equal name="element" property="itemtype" value="N">
							<%if (flag == 0) {
					if (i % 2 == 0) {

					%>
							<tr class="trShallow1">
								<%} else {%>
							<tr class="trDeep1">
								<%}
					i++;
					flag = rowss;
				} else {
					flag = 0;
				}%>
					<td align="right" nowrap valign="top">
						&nbsp;&nbsp;&nbsp;&nbsp;
						<hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>									
						&nbsp;
					</td>
					<td align="left" nowrap valign="top">
					    <logic:greaterThan name="element" property="decimalwidth" value="0">
					        &nbsp;<html:text name="pieceRateForm" property='<%="fielditemlist["+index+"].value"%>' onclick="Element.hide('dict');" styleClass="textColorWrite" maxlength="${element.itemlength + element.decimalwidth +1}" onblur="checkValue(this,'${element.itemlength}','${element.decimalwidth}');"/>
					    <html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemid"%>' />
						<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].fieldsetid"%>' />
						<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemtype"%>' />
						<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemlength"%>' />
						<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].codesetid"%>' />
					    </logic:greaterThan>
					    <logic:lessEqual name="element" property="decimalwidth" value="0">
					       &nbsp;<html:text name="pieceRateForm" property='<%="fielditemlist["+index+"].value"%>' onclick="Element.hide('dict');" styleClass="textColorWrite" maxlength="${element.itemlength}" onblur="checkValue(this,'${element.itemlength}','${element.decimalwidth}');"/>
					    <html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemid"%>' />
						<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].fieldsetid"%>' />
						<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemtype"%>' />
						<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemlength"%>' />
						<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].codesetid"%>' />
					    </logic:lessEqual>
						<logic:equal name="element" property="fillable" value="true">
							<font color="red">*</font>
						</logic:equal>
					</td>
						<%if (flag == 0) {%>
					</tr>
					<%} else {%>
					<logic:equal name="element" property="rowflag" value="${index}">
						<td colspan="2">
						</td>
						</tr>
					</logic:equal>
					<%}%>
	  </logic:equal>
	 <logic:equal name="element" property="itemtype" value="M">
							<%if (flag == 0) {
					if (i % 2 == 0) {

					%>
							<tr class="trShallow1">
								<%} else {%>
							<tr class="trDeep1">
								<%}
					i++;
					flag = rowss;%>
								<td align="right" nowrap valign="top">
									&nbsp;&nbsp;&nbsp;&nbsp;
									<hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>									
									&nbsp;
								</td>
								<td align="left" nowrap valign="top" colspan="3">
									&nbsp;<html:textarea name="pieceRateForm" property='<%="fielditemlist["+index+"].value"%>' onclick="Element.hide('dict');" rows="10" cols="66" styleClass="textColorWrite" />
									<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemid"%>' />
									<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].fieldsetid"%>' />
									<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemtype"%>' />
									<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemlength"%>' />
									<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].codesetid"%>' />
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>
								<%} else {
					flag = 0;%>
								<td colspan="2">
								</td>
								</td>

								<%if (flag == 0) {
						if (i % 2 == 0) {

						%>
							<tr class="trShallow1">
								<%} else {%>
							<tr class="trDeep1">
								<%}
						i++;
						flag = rowss;
					} else {
						flag = 0;
					}%>
								<td align="right" nowrap valign="top">
									&nbsp;&nbsp;&nbsp;&nbsp;
									<hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>									
									&nbsp;
								</td>
								<td align="left" nowrap valign="top" colspan="3">
									&nbsp;<html:textarea name="pieceRateForm" property='<%="fielditemlist["+index+"].value"%>' onclick="Element.hide('dict');" rows="10" cols="66" styleClass="textColorWrite" />
									<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemid"%>' />
									<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].fieldsetid"%>' />
									<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemtype"%>' />
									<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemlength"%>' />
									<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].codesetid"%>' />
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>

								<%}%>

								<%flag = 0;%>

							</tr>
						</logic:equal>
					</logic:equal>
					<logic:notEqual name="element" property="codesetid" value="0">
						<%if (flag == 0) {
					if (i % 2 == 0) {

					%>
						<tr class="trShallow1">
							<%} else {%>
						<tr class="trDeep1">
							<%}
					i++;
					flag = rowss;
				} else {
					flag = 0;
				}%>
							<td align="right"  valign="top">
								&nbsp;&nbsp;&nbsp;&nbsp;
								<hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>								
								&nbsp;
							</td>
							<td align="left" nowrap valign="center" style="position:relative;">		
	
									<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].value"%>' /> 
                                             &nbsp;<html:text name="pieceRateForm" property='<%="fielditemlist["+index+"].viewvalue"%>' styleClass="textColorWrite" 
                                             onchange="checkDict('${element.codesetid}',this);" onkeyup="addDict('${element.codesetid}',this);"
                                              onkeydown="return inputType2(this,event)" onclick="styleDisplay(this);"/>
                                              <!-- onkeyup="addDict('${element.codesetid}',this);" -->
	                                 <logic:notEqual name="element" property="codesetid" value="UM">
	                                	<span style="position: absolute;top:7px;">
	                                 	<img src="/images/code.gif" onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="fielditemlist["+index+"].viewvalue"%>");' />
	                                 	</span>
	                                 </logic:notEqual>
	                                 <logic:equal name="element" property="codesetid" value="UM">
	                                 <span style="position: absolute;top:7px;">
	                                 	<img src="/images/code.gif" onclick='javascript:openInputCodeDialogOrgInputPos("${element.codesetid}",
	                                               "<%="fielditemlist["+index+"].viewvalue"%>",pieceRateForm.managerpriv.value,"1");' />
	                                 </span>
	                                 </logic:equal>
								<logic:equal name="element" property="fillable" value="true">
									<font color="red">*</font>
								</logic:equal>
								<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemid"%>' />
								<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].fieldsetid"%>' />
								<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemtype"%>' />
								<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].itemlength"%>' />
								<html:hidden name="pieceRateForm" property='<%="fielditemlist["+index+"].codesetid"%>' />
							</td>
							<%if (flag == 0) {%>
						</tr>
						<%} else {%>
						<logic:equal name="element" property="rowflag" value="${index}">
							<td colspan="2">
							</td>
							</tr>
						</logic:equal>
						<%}%>
					</logic:notEqual>

	</logic:iterate>

		<% }%> 				
			<tr>
				<td align="center" nowrap colspan="4">							

							<logic:equal name="pieceRateForm" property="canEdit" value="true">
							<button name="savasss" class="myButton" onclick="save();">
								<bean:message key="button.save" />
							</button>
							</logic:equal>
							<logic:equal name="pieceRateForm" property="taskeditmodel" value="add">
							<button name="savass" class="myButton" onclick="savegoon();">
									<bean:message key="button.savereturn" />
								</button>
							</logic:equal>

							<button name="savass" class="myButton" onclick="returnlist();">
								<bean:message key="button.return" />
							</button>
					
				</td>
			</tr>		

		</table>
</body>
<div id=dict style="border-style:nono">
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">    
     <tr>
     <td>
       <select name="dict_box" multiple="multiple" size="10" class="dropdown_frame" style="width:200" ondblclick="setSelectCodeValue();" onkeydown="return inputType(this,event)" onblur="Element.hide('dict');">    
       </select>
     </td>
     </tr>
</div>


</html:form>
<script language='javascript'>
      Element.hide('dict');      
      
</script>

