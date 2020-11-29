<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.lawbase.LawBaseForm" %>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<script language="javascript" src="/ajax/common.js"></script>
<STYLE type=text/css>
.div2
{
 overflow:auto; 
 width: 260px;height: 230px;
 line-height:15px; 
 border:1px solid; 
 /*scrollbar-base-color:#ff66ff; 
 scrollbar-face-color:none;
 scrollbar-arrow-color:none;
 scrollbar-track-color:#ffffff;
 scrollbar-3dlight-color:#ffffff;
 scrollbar-darkshadow-color:#ffffff;
 scrollbar-highlight-color:#e5c8e5;
 scrollbar-shadow-color:#e5c8e5;
 SCROLLBAR-DARKSHADOW-COLOR: #ffffff;
 BORDER-BOTTOM: #ffccff 1px dotted;*/
}
</STYLE>
<%
LawBaseForm lawBaseForm=(LawBaseForm)session.getAttribute("lawbaseForm");
String str="";
String checkflag=lawBaseForm.getCheckflag();
if(checkflag.equals("8"))
{
    if(lawBaseForm.getDbpre_str()!=null&&!lawBaseForm.getDbpre_str().equals(""))
    {
       str="dbpre='"+lawBaseForm.getDbpre_str()+"'";
    }
}
 %>
<script language="javascript"><!--
   function savefield()
  {  	  
     var checkflag = "${lawbaseForm.checkflag}";  
     var vos= document.getElementById("right");      
      var code_fields=new Array();         
     /*if(vos==null)
     {
       alert("已选指标项不能为空！");
       return false;
     }else
     {*/
        for(var i=0;i<vos.options.length;i++)
        {
          var valueS=vos.options[i].value;          
          code_fields[i]=valueS;
        }  
    // }
     if(checkflag=="0"){
     	var hashvo=new ParameterSet();   
     	hashvo.setValue("code_fields",code_fields);
     	hashvo.setValue("a_id",'${lawbaseForm.a_id}'); 
     	var request=new Request({method:'post',onSuccess:showSelect,functionId:'10400201042'},hashvo);
     }else if(checkflag=='8'){
         var title="";
         var content="";
     	for(var i=0;i<vos.length;i++)
        {
          title+=","+vos.options[i].text;          
          content+=","+vos.options[i].value;
        } 
        if(trim(title).length>0)
        {
           title=title.substring(1);
           content=content.substring(1);
        }  
        var obj = new Object();
        obj.title=title;
        obj.content=content;
        window.returnValue=obj;
        window.close();   
     }
     else
     {
        	window.returnValue=code_fields;
     	    window.close();
     }
   }	
   function showSelect(outparamters)
   { 
	window.close(); 
   }  
   function getemp()
  {
    var targetobj,hiddenobj;
    var currnode=Global.selectedItem;	
    if(currnode==null)
    	return;  
    var id = currnode.uid;
    var text=currnode.text;
    if(id.indexOf("UN")!=-1||id.indexOf("UM")!=-1||id.indexOf("@K")!=-1||id.indexOf("root")!=-1)
      return;  
    var no = new Option();
    no.value=id;
    no.text=text;
    var vos= document.getElementsByName('right_fields');
    var emp_vo=vos[0];
    var isC=true;
    for(i=0;i<emp_vo.options.length;i++)
    {
       var select_ob=emp_vo.options[i];
       if(id==select_ob.value)
       {
          isC=false;
       }
    }
    if(isC)
    {
      emp_vo.options[emp_vo.options.length]=no;
    } 
  }
  function search()
  {
  	var db_arr=new Array();
    var hashvo=new ParameterSet();
    var persons=null;
   
    var chkflag="${lawbaseForm.checkflag}";
    var dbpre="${lawbaseForm.dbpre_str}";
    if(chkflag=='8')
    {
        db_arr=dbpre.split(",");
    }else{
       db_arr[0] = "Usr";
    }     
    if(dbpre.length==0){///dbpre为空时 默认为在职人员库
    	db_arr= "Usr";
    }
    persons=common_query("1",db_arr,"1");
    if(persons!=null && persons.length>0)
   	{
   		 hashvo.setValue("persons",persons);
		 var request=new Request({method:'post',onSuccess:getPersonlist,functionId:'10400201043'},hashvo);
   	}
  }
  function getPersonlist(outparamters)
  {
  	var personlist = new Array();
  	personlist = outparamters.getValue("personlist");
  	var personname = outparamters.getValue("personname");
  	
  	
  	for(i=0;i<personlist.length;i++)
  	{
  		var vos= document.getElementsByName('right_fields');
		var emp_vo=vos[0];
		var no = new Option();
  		var id;
  		id = personlist[i];
  		no.value= id;
    	no.text=personname[i];
	    var isC=true;
	    for(j=0;j<emp_vo.options.length;j++)
	    {
	       var select_ob=emp_vo.options[j];
	       if(id==select_ob.value)
	       {
	          isC=false;
	       }
	    }
	    if(isC)
		{
		      emp_vo.options[emp_vo.options.length]=no;
		} 
	}
}
	var date_desc;
	function setSelectValue()
	{
		if(date_desc)
		{
			var no = new Option();
		    no.value=$F('date_box');
		    var aaa = $('date_box');
		    var text = aaa[aaa.selectedIndex].text;
		    var kh1=text.indexOf("(");
		    text = text.substring(0,kh1);
		    no.text=text;
		    var vos= document.getElementsByName('right_fields');
		    var emp_vo=vos[0];
		    var isC=true;
		    for(i=0;i<emp_vo.options.length;i++)
		    {
		       var select_ob=emp_vo.options[i];
		       if($F('date_box')==select_ob.value)
		       {
		          isC=false;
		       }
		    }
		    if(isC)
		    {
		      emp_vo.options[emp_vo.options.length]=no;
		    }
		    var name = $('selectname'); 
		    name.value ="";
			Element.hide('date_panel'); 
		}
	}
	function showDateSelectBox(srcobj)
   {
   		if($F('selectname')=="")
   		{
   			Element.hide('date_panel');
   			return false ;
   		}
      date_desc=document.getElementById(srcobj);
      Element.show('date_panel');
      var pos=getAbsPosition(date_desc);
	  with($('date_panel'))
	  {
        style.position="absolute";
		style.posLeft=pos[0]-1;
		style.posTop=pos[1]-70-date_desc.offsetHeight;
		style.width=(date_desc.offsetWidth<150)?150:date_desc.offsetWidth+1;
      }
      var hashvo = new ParameterSet();
      hashvo.setValue("selname",getEncodeStr(date_desc.value));
      hashvo.setValue("priv","${lawbaseForm.priv}");
      hashvo.setValue("checkflag","${lawbaseForm.checkflag}");
      var request=new Request({method:'post',onSuccess:shownamelist,functionId:'10400201045'},hashvo);
   }
   function shownamelist(outparamters)
   {
   		var namelist=outparamters.getValue("namelist");
		if(namelist.length==0){
			Element.hide('date_panel');
		}
		else{
			AjaxBind.bind(lawbaseForm.contenttype,namelist);
		}
   }
   function remove()
    {
    	Element.hide('date_panel');
    }

--></script>
<html:form action="/selfservice/lawbase/add_law_text_role">
<div class="fixedDiv3">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3" style="border-bottom: 0px;">
            <logic:equal value="0" name="lawbaseForm" property="checkflag">
		<bean:message key="wd.lawbase.choicerelatingpersonnel"/>&nbsp;&nbsp;
		</logic:equal>
		  <logic:equal value="8" name="lawbaseForm" property="checkflag">
		选择考官&nbsp;&nbsp;
		</logic:equal>
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
              <table>
                <tr>
                 <td align="center"  width="46%">
                   <table align="center" width="100%">
                    <tr>
                    <td align="left">
                     <bean:message key="wd.lawbase.standbypersonnel"/>&nbsp;&nbsp;
                    </td>
                    </tr>                   
                   <tr>
                    <td align="center">
                    	<div align="left" id="tbl_container" ondblclick="getemp();" class="div2 complex_border_color">
                    	<logic:notEqual value="8" name="lawbaseForm" property="checkflag">
              				<hrms:orgtree flag="1" showroot="false"  dbtype="0" priv="${lawbaseForm.priv}" target="app">
              				</hrms:orgtree>
              				</logic:notEqual>
              				<logic:equal value="8" name="lawbaseForm" property="checkflag">
              				<hrms:orgtree flag="1" showroot="false"  dbtype="2" priv="0"  target="app">
              				</hrms:orgtree>
              				</logic:equal>
           				</div>
                    </td>
                    
                    </tr>
                   
                   </table>
                </td>
               
                <td width="8%" align="center" >
				<html:button  styleClass="mybutton" property="b_addfield" onclick="getemp();">
            		     <bean:message key="button.setfield.addfield"/>    
	           </html:button>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');" style="margin-top:30px;">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button>	     
                </td>         
                
                <td width="46%" align="center">
                 
                 
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     <bean:message key="wd.lawbase.alreadypickpersonnel"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
                  <hrms:optioncollection name="lawbaseForm" property="selectrname" collection="selectedlist"/> 
     	             <html:select property="right_fields" size="10" multiple="true" style="height:200px;width:100%;font-size:9pt"  styleId="right" ondblclick="removeitem('right_fields');">
                        <html:options collection="selectedlist"  property="dataValue" labelProperty="dataName"/>
                     </html:select>   
                  </td>
                  </tr>
                  <TR>
                  	<td style="white-space:nowrap;">
                  		<bean:message key="hire.employActualize.name"/>&nbsp;
                  		<html:text styleId="selectname" name="lawbaseForm" styleClass="text4" property="selectname" size="32" maxlength="30" style="width:162px;" onkeyup="showDateSelectBox('selectname')" />
                  	</td>
                  </TR>
                  </table>             
                </td>
                     
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap  colspan="3" style="height: 35;border-top: 0px;">
             <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick=" savefield();">

	         <input type="button" name="button"  value='<bean:message key="button.query"/>' class="mybutton" onclick="javascript:search();">

		     <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' class="mybutton" onclick=" window.close();">
	     
          </td>
          </tr>
</table>
	<div id="date_panel" style="display:none;" onmouseout="remove();">

		<select id="date_box" name="contenttype" multiple="multiple"  style="width:140" size="6" ondblclick="setSelectValue();">
        </select>
	 </div>
	 </div>
</html:form>
