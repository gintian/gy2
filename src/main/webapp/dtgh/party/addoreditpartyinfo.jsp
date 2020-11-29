<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hjsj.hrms.actionform.dtgh.party.PartyBusinessForm"%>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/org/orgdata/orgedit.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script> 	
<script type="text/javascript" src="/js/dict.js"></script>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="javascript">
  function validate()
  {
    var tag=true; 
     <logic:iterate  id="element"    name="partyBusinessForm"  property="infofieldlist" indexId="index"> 
       <bean:define id="fl" name="element" property="fillable"/>
        <bean:define id="desc" name="element" property="itemdesc"/>
        var valueInputs=document.getElementsByName("<%="infofieldlist["+index+"].value"%>");
        var dobj=valueInputs[0];  
           if("${fl}"=='true'&&dobj.value.length<1){
          	alert("${desc}"+'必须填写！');

          	return false;
         }
        <logic:equal name="element" property="itemtype" value="D">   
          var valueInputs=document.getElementsByName('<%="infofieldlist["+index+"].value"%>');
          var dobj=valueInputs[0];
          tag= checkDate(dobj) && tag;      
	  if(tag==false)
	  {
	    dobj.focus();
	    return false;
	  }
        </logic:equal> 
        <logic:equal name="element" property="itemtype" value="N"> 
           <logic:lessThan name="element" property="decimalwidth" value="1"> 
             var valueInputs=document.getElementsByName('<%="infofieldlist["+index+"].value"%>');
             var dobj=valueInputs[0];
              tag=checkNUM1(dobj) &&  tag ;  
	      if(tag==false)
	      {
	        dobj.focus();
	        return false;
	      }
	    </logic:lessThan>
	    <logic:greaterThan name="element" property="decimalwidth" value="0"> 
	     var valueInputs=document.getElementsByName('<%="infofieldlist["+index+"].value"%>');
             var dobj=valueInputs[0];
               tag=checkNUM2(dobj,${element.itemlength},${element.decimalwidth}) &&  tag ;   
	      if(tag==false)
	      {
	        dobj.focus();
	        return false;
	      }
	    </logic:greaterThan>
	</logic:equal>  
      </logic:iterate>    
     return tag;   
  }

  var saveFlag = "${partyBusinessForm.sign}";
  function save()
  {   
	  saveFlag = "";
	   
	  if(!validate())
	  	return;
	  <% 
	  	PartyBusinessForm partyBusinessForm = (PartyBusinessForm)request.getSession().getAttribute("partyBusinessForm");
	  	String fieldsetid = partyBusinessForm.getFieldsetid();
	  	if(fieldsetid.indexOf("01")!=-1){
	  		
	  %> 
	   partyBusinessForm.action="/dtgh/party/searchpartybusinesslist.do?b_save=link";
	   
	   <%}else{%> 
	   	partyBusinessForm.action="/dtgh/party/searchpartybusinesslist.do?b_save_sub=link";
	   <%}%>
	   partyBusinessForm.submit();
	   
   }


  function saveAfter(){
	  if (saveFlag == "")
		  return;
	 <logic:equal name="partyBusinessForm" property="sign" value="update"> 
 		 alert("保存成功！");
	 </logic:equal>
  }

function exeReturn()
{
	<%
	if(fieldsetid.indexOf("01")!=-1){
  %>
  	partyBusinessForm.action='/dtgh/party/searchpartybusinesslist.do?b_query=link&a_code=${partyBusinessForm.a_code }';
   	partyBusinessForm.target='mil_body';
   <%}else{%>
   partyBusinessForm.action='/dtgh/party/searchpartybusinesslist.do?b_query_sub=link';
   <%}%>
   partyBusinessForm.submit();
}

</script>

<%
	int i=0;
	int flag=0;
	int j=0;
	int n=0;
%>

<hrms:themes/>

<html:form action="/dtgh/party/searchpartybusinesslist" onsubmit="return validate()">
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
<logic:iterate  id="element"    name="partyBusinessForm"  property="infofieldlist" indexId="index"> 
    <logic:equal name="element" property="codesetid" value="0">
      <logic:equal name="element" property="itemtype" value="D">
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
         <td align="right" nowrap valign="middle">        
            &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;          
         </td>
         <td align="left"  nowrap valign="middle">
             &nbsp;<input type="text" name='<%="infofieldlist[" + index
												+ "].value"%>'
											maxlength="${element.itemlength + element.decimalwidth + 1}" 
											 class="textColorWrite"
											 value="${element.value }"
											 extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate">&nbsp;  
           <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal>
         </td> 
        <%if(flag==0){%>           
           </tr>
        <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
      </logic:equal> 
      <logic:equal name="element" property="itemtype" value="A">
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
         <td align="right" nowrap valign="middle">        
            &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;          
         </td>
         <td align="left"  nowrap valign="middle">
            <logic:equal value="true" name="element" property="readonly">
            &nbsp;<html:text  name="partyBusinessForm" property='<%="infofieldlist["+index+"].value"%>'  styleClass="textColorRead" maxlength="${element.itemlength}" readonly="${element.readonly}"/>&nbsp;
            </logic:equal> 
            <logic:notEqual value="true" name="element" property="readonly">
            &nbsp;<html:text  name="partyBusinessForm" property='<%="infofieldlist["+index+"].value"%>'  styleClass="textColorWrite" maxlength="${element.itemlength}" readonly="${element.readonly}"/>&nbsp;
            </logic:notEqual>
         <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal>
         </td>
         <%if(flag==0){%>
           </tr>
       <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
      </logic:equal> 
       <logic:equal name="element" property="itemtype" value="N">
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
           <td align="right" nowrap valign="middle">        
              &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;          
           </td>
           <td align="left"  nowrap valign="middle">
           <logic:equal name="element" property="decimalwidth" value="0">
               &nbsp;<html:text  name="partyBusinessForm" property='<%="infofieldlist["+index+"].value"%>'  styleClass="textColorWrite" maxlength="${element.itemlength }" onkeypress="event.returnValue=IsDigit2(this);" onblur='isNumber(this);'/>&nbsp;  
            </logic:equal>
            <logic:notEqual name="element" property="decimalwidth" value="0">
            	&nbsp;<html:text  name="partyBusinessForm" property='<%="infofieldlist["+index+"].value"%>'  styleClass="textColorWrite" maxlength="${element.itemlength + element.decimalwidth + 1}" onkeypress="event.returnValue=IsDigit(this);" onblur='isNumber(this);'/>&nbsp;  
			</logic:notEqual>
            <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal>
           </td>
         <%if(flag==0){%>
           </tr>
         <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
       </logic:equal>     
      <logic:equal name="element" property="itemtype" value="M">
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
             flag=1;%>
                 <td align="right" nowrap valign="middle">        
                   &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;          
                 </td>
                 <td align="left"  nowrap valign="middle"  colspan="3">
                   
                    <logic:notEqual name="element" property="inputtype" value="1">
                  &nbsp;<html:textarea name="partyBusinessForm" property='<%="infofieldlist["+index+"].value"%>'   rows="7"  cols="80" style="width:80%"/>&nbsp; 
                    </logic:notEqual>
                 <logic:equal name="element"  property="fillable" value="true">
                  <font color="red">*</font>
                 </logic:equal>
                 </td> 
             <%       
             }else{
               flag=0;%>               
              <td colspan="2">
              </td>
              </td>
               
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
                <td align="right" nowrap valign="middle">        
                   &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;          
                </td>
                <td align="left"  nowrap valign="middle" colspan="3">
                  &nbsp;<html:textarea name="partyBusinessForm" property='<%="infofieldlist["+index+"].value"%>'   rows="7"  cols="80" style="width:80%"/>&nbsp; 
                  <logic:equal name="element"  property="fillable" value="true">
                    <font color="red">*</font>
                  </logic:equal>
                </td> 
               
               <%         
             }%>
         
           <%flag=0;%>
           
          </tr>
      </logic:equal>
    </logic:equal>
    <logic:notEqual name="element" property="codesetid" value="0">
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
           <td align="right" nowrap valign="middle">        
              &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;          
           </td>
           <td align="left"  nowrap valign="middle" >
                 
                 <logic:equal name="element" property="codesetid" value="UN">
						<html:hidden name="partyBusinessForm" property='<%="infofieldlist["+index+"].value"%>' styleId="b0110" />  <!-- onchange="changepos('${element.codesetid}')" -->
                        &nbsp;<html:text name="partyBusinessForm" property='<%="infofieldlist["+index+"].viewvalue"%>' styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);"  onkeydown="return inputType2(this,event)" onclick="styleDisplay(this);"/>
						<img align="absmiddle" align="absmiddle" src="/images/code.gif" onclick='javascript:openInputCodeDialogOrgInputPos("${element.codesetid}","<%="infofieldlist["+index+"].viewvalue"%>","","1");' />&nbsp;
                 </logic:equal>
				 <logic:equal name="element" property="codesetid" value="UM">
						<html:hidden name="partyBusinessForm" property='<%="infofieldlist["+index+"].value"%>' styleId="e0122" />  <!-- onchange="changepos('${element.codesetid}')"  -->
                        &nbsp;<html:text name="partyBusinessForm" property='<%="infofieldlist["+index+"].viewvalue"%>' styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);" onkeydown="return inputType2(this,event)" onclick="styleDisplay(this);"/>
						<img align="absmiddle" src="/images/code.gif" onclick='javascript:openInputCodeDialogOrgInputPos("${element.codesetid}","<%="infofieldlist["+index+"].viewvalue"%>","","2");' />&nbsp;
                </logic:equal>
				<logic:equal name="element" property="codesetid" value="@K">
						<html:hidden name="partyBusinessForm" property='<%="infofieldlist["+index+"].value"%>' styleId="e01a1" />  <!-- onchange="changepos('${element.codesetid}')" -->
                      	&nbsp;<html:text name="partyBusinessForm" property='<%="infofieldlist["+index+"].viewvalue"%>' styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);"  onkeydown="return inputType2(this,event)" onclick="styleDisplay(this);"/>
						<img align="absmiddle" src="/images/code.gif" onclick='javascript:openInputCodeDialogOrgInputPos("${element.codesetid}","<%="infofieldlist["+index+"].viewvalue"%>","","2");' />&nbsp;
                </logic:equal>
				<logic:notEqual name="element" property="codesetid" value="UN">
						<logic:notEqual name="element" property="codesetid" value="UM">
							<logic:notEqual name="element" property="codesetid" value="@K">
								<html:hidden name="partyBusinessForm" property='<%="infofieldlist["+index+"].value"%>' /> 
                      			&nbsp;<html:text name="partyBusinessForm" property='<%="infofieldlist["+index+"].viewvalue"%>' styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);"  onkeydown="return inputType2(this,event)" onclick="styleDisplay(this);"/>
								<img align="absmiddle" src="/images/code.gif" onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="infofieldlist["+index+"].viewvalue"%>");' />
							</logic:notEqual>
						</logic:notEqual>
				</logic:notEqual>
								
               <logic:equal name="element"  property="fillable" value="true">
               <font color="red">*</font>
              </logic:equal>
           </td>
         <%if(flag==0){%>
           </tr>
        <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
      </logic:notEqual>

</logic:iterate> 
 <tr><td height="10"></td></tr>
 <tr>
  <td align="center"  nowrap colspan="4" >
    &nbsp;&nbsp;
             
	       <input type="button" class="mybutton" value="<bean:message key="button.save"/>" onclick="save();"/>

           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn();">                 

	     
  </td>
 </tr>    
 </table>
</html:form>
<div id=dict style="border-style:nono">
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
     <tr>
     <td>
       <select name="dict_box" multiple="multiple" size="10" class="dropdown_frame" style="width:200" ondblclick="setSelectCodeValue();" onkeydown="return inputType(this,event)"  onblur="Element.hide('dict');">    
       </select>
     </td>
     </tr>
</div>

<script language="javascript">
var code_desc; 
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
		      vos= document.getElementById('b0110');
		    else if(code=="@K")
		      vos= document.getElementById('e0122');
		    var code_value="";
		    if(vos)
		    {
		       var b_value=vos.value;
		       if(b_value==null||b_value=="")
		       {
		          b_value=""
		       }
		       if(code_value!=""&&code_value.length<=dmobj.ID.substring(2).length)
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
		          if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0)||(dmobj.ID.indexOf(code+value)==0))
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
 function setSelectCodeValue()
   {
     if(code_desc)
     {
        var vos= document.getElementsByName('dict_box');
        var dict_vo=vos[0];
        var isC=true;
        for(var i=0;i<dict_vo.options.length;i++)
        {
          if(dict_vo.options[i].selected)
          {
            code_desc.value=dict_vo[i].text;
            var code_name=code_desc.name;
            if(code_name!="")
            {
               var code_viewname=code_name.substring(0,code_name.indexOf("."));
               var view_vos= document.getElementsByName(code_viewname+".value");
               var view_vo=view_vos[0];
               if(dict_vo[i].value!=null)          
                 view_vo.value=dict_vo[i].value.substring(2);
               view_vo.fireEvent("onchange");
             }
          }
        }
        Element.hide('dict');   
        event.srcElement.releaseCapture(); 
     }
  }
  function inputType(obj,event)
  {
     var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
     if(keyCode==13)
     {
       setSelectCodeValue();
     }
     
  }
  function inputType2(obj,event)
  {
    var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
    if(keyCode == 40)
    {
       var vos= document.getElementsByName('dict');
       var vos1=vos[0];       
       if(vos1.style.display!="none")
       {
          var vos= document.getElementsByName('dict_box');
          var dict_vo=vos[0];          
          dict_vo.focus(); 
       }
    }
  }
  function styleDisplay(obj)
  {
     var obj_name=obj.name;
     if(code_desc)
     {
        var code_name=code_desc.name;
        if(code_name!=obj_name)
        {
          Element.hide('dict');
        }
     }
  }
     function checkDict(code,obj)
  {
    var code_name=obj.name;
    var code_viewname=code_name.substring(0,code_name.indexOf("."));
    var view_vos= document.getElementsByName(code_viewname+".value");
    var view_vo=view_vos[0];  
    if(view_vo==null||view_vo=="")
    {
      obj.value="";
      return false;
    }
    var isC=false;
    for(var i=0;i<g_dm.length;i++)
    {
		dmobj=g_dm[i];	
		if(dmobj.ID==(code+view_vo))
		{
		    isC=true;
		    break;
		}
   } 
   if(!isC)
   {
      obj.value="";
      return false;
   } 
}
Element.hide('dict');
</script>  
<script>
function addsave(isrefresh,codesetid,codeitemid,codeitemdesc/*,issuperuser,manageprive*/)
   {
   	 //alert(isrefresh+" setid:"+codesetid+" itemid:"+codeitemid+" desc:"+codeitemdesc/*+" sup:"+issuperuser+" mana:"+manageprive*/);
   	 if(isrefresh==""||codesetid==""||codeitemid==""||codeitemdesc==""/*||issuperuser==""*/){
   	 	return;
   	 }
   	 if(isrefresh=='save')
   	 {
   	 	var currnode=parent.parent.frames['mil_menu'].Global.selectedItem;
   	 	var pt = currnode.getLastChild();
   	 	if(pt.uid==codesetid+codeitemid)
   	 		return;
   	 	var uid = codesetid+codeitemid;
   	 	var text = codeitemdesc;
   	 	var title = codeitemdesc;
   	 	//var issuperuser = issuperuser;
   	 	//var manageprive = manageprive;

   	 	var action = "/dtgh/party/searchpartybusinesslist.do?b_query=link&a_code="+uid;
   	 	var xml = "/dtgh/party/get_code_tree.jsp?codesetid="+codesetid+"&codeitemid="+codeitemid;
   	 	//if(currnode==currnode.root())
   	 		//currnode = currnode.getFirstChild();
   	 	if(currnode.load)
   	 	{
   	 		var imgurl="/images/table.gif"; 
   	 			parent.parent.frames['mil_menu'].add(uid,text,action,"mil_body",title,imgurl,xml);
   	 		currnode.expand();
   	 	}else
   	 		currnode.expand();
   	 }
   }
   function update(isrefresh,codesetid,codeitemid,codeitemdesc)
   {
   	//alert(isrefresh+" setid:"+codesetid+" itemid:"+codeitemid+" desc:"+codeitemdesc);
   	if(isrefresh=='update')
   	{
   		var currnode=parent.parent.frames['mil_menu'].Global.selectedItem;
   		if((codesetid+codeitemid)==currnode.uid){
   			currnode.setText(codeitemdesc);
   			return;
   		}
   		if(currnode.load)
			for(var i=0;i<=currnode.childNodes.length-1;i++){
				if((codesetid+codeitemid)==currnode.childNodes[i].uid)
					currnode.childNodes[i].setText(codeitemdesc);
   		}
   	}
   }
//addsave('save','64','001001','局长');
update('${partyBusinessForm.isrefresh}','${partyBusinessForm.codesetid}','${partyBusinessForm.codeitemid}','${partyBusinessForm.codeitemdesc}');
addsave('${partyBusinessForm.isrefresh}','${partyBusinessForm.codesetid}','${partyBusinessForm.codeitemid}','${partyBusinessForm.codeitemdesc}');

  saveAfter();
</script>
