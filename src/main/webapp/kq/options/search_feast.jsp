<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %> 
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="../../general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>

<% 
    int i=0;
    String type = (String) request.getParameter("type");
    String isshow = (String)request.getParameter("isshow");
    if(isshow == null){
        isshow = "0";
    }
%>

<script language="javascript">
  
    function deletess()
    {
       var objs = document.getElementsByTagName('input');
       var x=0;
       for(var i=0;i<objs.length;i++)
       {
            if(objs[i].type=="checkbox" && objs[i].name!='selbox')
                if(objs[i].checked==true)
                    x++;
       }
       if(x==0)
       {
          alert("请选择要删除的记录！");
          return false;
       }
       var len=document.kqFeastForm.elements.length;
       var uu;
       for (var i=0;i<len;i++)
       {
           if (document.kqFeastForm.elements[i].type=="checkbox")
           {
              //if(document.kqMachineForm.elements[i].checked==true)
              //{
                uu="dd";
                break;
               //}
           }
       }
       if(uu!="dd")
       {
          alert("请选择要删除的记录！");
          return false;
       }
         if(confirm("是否删除选择的记录?"))
      {
         kqFeastForm.action="/kq/options/search_feast.do?b_delete=link&isshow=1";
         kqFeastForm.submit();
      }

    }
    
  function adds()
  {
           var target_url="/kq/options/add_feast_type.do?b_add=link";
           var config={
   	    		width:450,
   	            height:320,
   	            id:"feastWin",
   	            title:"新增"
   	    }
   	    return_vo=modalDialog.showModalDialogs(target_url,"新增",config,refleshs1)
   	    if(return_vo)
   	    	refleshs1(return_vo);
  }
  function refleshs()
  {
        document.kqFeastForm.action="/kq/options/search_feast.do?b_query=link&isshow=2";
        document.kqFeastForm.submit();
    }
    function refleshs1(type)
    {
    	if (type == 0)
    		return;
    	
        if(type == 1){        	
        	document.kqFeastForm.action="/kq/options/search_feast.do?b_query=link&isshow=3";
        }else if(type == 2){
        	document.kqFeastForm.action="/kq/options/search_feast.do?b_query=link&isshow=2";
        }
        document.kqFeastForm.submit();
    }

  function edit(str) 
  {
	  var target_url="/kq/options/add_feast_type.do?b_edit=link&a_id="+str;
      var config={
 	    		width:450,
 	            height:320,
 	            id:"feastWin",
 	            title:"编辑"
 	    }
 	    return_vo=modalDialog.showModalDialogs(target_url,"编辑",config,refleshs1)
 	    if(return_vo)
 	    	refleshs1(return_vo);
  }
  function reload(){
	  window.location.href="/kq/options/search_feast.do?b_query=link";
  }
</script>
<html:form action="/kq/options/search_feast">
 <base id="mybase" target="_self">

 <table width="60%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-tOp:10px;">
      <thead>
           <tr>
            <td align="center" class="TableRow" style="width: 35px;" nowrap>
                &nbsp;<input type="checkbox" name="selbox" onclick="batch_select(this,'kqFeastForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
            </td>           
              <td align="center" class="TableRow" style="width: 35px;" nowrap><bean:message key="kq.feast_type_list.styleN"/></td>
              <td align="center" class="TableRow" nowrap><bean:message key="kq.feast_type_list.name"/></td>
                     <td align="center" class="TableRow" nowrap ><bean:message key="kq.search_feast.jday"/></td>
            <td align="center" class="TableRow" nowrap><bean:message key="kq.feast_type_list.modify"/></td>
        
           </tr>
      </thead>

      <hrms:extenditerate id="element" name="kqFeastForm" property="kqFeastForm.list" indexes="indexes"  pagination="kqFeastForm.pagination" pageCount="20" scope="session">
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
            <td align="center" class="RecordRow" nowrap>
               &nbsp;<hrms:checkmultibox name="kqFeastForm" property="kqFeastForm.select" value="true" indexes="indexes"/>&nbsp;
            </td>  
              <td align="center" class="RecordRow" nowrap style="display:none">                
                   &nbsp;<bean:write  name="element" property="feast_id" filter="true"/>&nbsp; 
              </td>
              <td align="center" class="RecordRow" nowrap>                
                   &nbsp;<bean:write  name="element" property="index" filter="true"/>&nbsp; 
              </td>
               <td align="left" class="RecordRow" nowrap>              
                  &nbsp; <bean:write  name="element" property="feast_name" filter="true"/>&nbsp;
              </td>                      
              <td align="left" class="RecordRow" nowrap>              
               &nbsp;<bean:write  name="element" property="sdate" filter="true"/>&nbsp;
                   
              </td>                         
                    <td align="center" class="RecordRow" nowrap>
                    <bean:define id="feast_id1" name="element" property="feast_id"/>
		         <%
		         		//参数加密
		    		     String feast_id = PubFunc.encrypt(feast_id1.toString());
		         %>
                <a href="javascript:edit('<%=feast_id %>');"><img src="/images/edit.gif" border=0></a>
             </td>  
    
          </tr>

        </hrms:extenditerate> 
        <tr>
             <td  align="right" nowrap class="RecordRow" colspan="5">
                <hrms:paginationlink name="kqFeastForm" property="kqFeastForm.pagination" nameId="kqFeastForm">
                </hrms:paginationlink>
            </td>
        </tr>
</table>

    <table  width="70%" align="center">
          <tr>
            <td align="center" height="30px">
	       	 <input type="button" name="b_saveb" value="<bean:message key="button.insert"/>" class="mybutton" onclick="adds()">     
	   	     <input type="button"  value="<bean:message key="button.delete"/>" class="mybutton" onclick="deletess()"> 

	     	 <hrms:tipwizardbutton flag="workrest" target="il_body" formname="kqFeastForm"/>
			<logic:notEmpty name="kqFeastForm" property="gw_flag" >
		   	     <input type="button" value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close();"/>
			</logic:notEmpty>
        </td>
      </tr>          
</table>
</html:form>

