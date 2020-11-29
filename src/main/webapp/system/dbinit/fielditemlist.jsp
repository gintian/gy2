<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="../../../../module/utils/js/template.js"></script>
<script language="javascript">
function reflesh()
{
   		document.dbinitForm.action="/system/dbinit/fielditemlist.do?b_query=link";
	    document.dbinitForm.submit();
}

function refleshs()
{
   		document.dbinitForm.action="/system/dbinit/fielditemlist.do?b_query1=link";
	    document.dbinitForm.submit();
}

function checkdelete()
{
		var str="";
		for(var i=0;i<document.dbinitForm.elements.length;i++)
			{
				if(document.dbinitForm.elements[i].type=="checkbox")
				{
					if(document.dbinitForm.elements[i].checked==true)
					{
						if(document.dbinitForm.elements[i].name=="selbox")
							continue;
							str+=document.dbinitForm.elements[i].value+"/";
					}
				}
			}
		if(str.length==0)
			{
				alert("请选择！");
				return;
			}else{
				if(confirm("<bean:message key="workbench.info.isdelete"/>?"))
    			{
					dbinitForm.action="/system/dbinit/fielditemlist.do?b_delete=link&deletestr="+str; 
				 	dbinitForm.submit();
				}
			}
}
function voider(itemid,fieldsetid){

	//弹窗方式打开
	/*var theurl="/system/dbinit/fielditemlist.do?b_amend=link`itemid="+itemid+"`fieldsetid="+fieldsetid;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
	var return_vo= window.showModalDialog(iframe_url, 'template_win', 
					"dialogWidth:560px; dialogHeight:440px;resizable:no;center:yes;scroll:yes;status:yes");
	if(return_vo!=null){
			 refleshs();
		}*/
	//window.open(theurl);
	//在本窗口打开
	dbinitForm.action="/system/dbinit/fielditemlist.do?b_amend=link&itemid="+itemid+"&fieldsetid="+fieldsetid;
	dbinitForm.submit();
}
function toSorting(){
    var fieldsetid = document.getElementsByName('setid')[0].value;
    // var fieldsetid= document.getElementById("setid").value;
	var thecodeurl="/system/dbinit/fielditemlist.do?b_sorting=link&fieldsetid="+fieldsetid;
	// var dw=400,dh=430,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	// var return_vo= window.showModalDialog(thecodeurl, "",
	// 	"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	// if(return_vo!=null){
	// 	reflesh();
	// }
    return_vo ='';
    var theUrl = thecodeurl;
    Ext.create('Ext.window.Window', {
        id:'indexSorting',
        height: 460,
        width: 400,
        resizable:false,
        modal:true,
        autoScroll:false,
        autoShow:true,
        html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
        renderTo:Ext.getBody(),
        listeners:{
            'close':function () {
                if (return_vo) {
                    reflesh();
                }
            }}

    }).show();
}
function backtoup(){
 var infor = document.getElementById("infor").value;
 var inf = infor.substring(0,1);
	dbinitForm.action="/system/dbinit/fieldsetlist.do?b_query=bank&infor="+inf;
	dbinitForm.submit();
}
function changeTrColor(id)
 {
    var ob=document.getElementById("tb");
    var j=ob.rows.length;
    for(var i=0;i<j-1;i++)
    {
         var o="a_"+i;
         var obj=document.getElementById(o);
         if(o==id)
         {
           if(o!=null)
           {
               obj.className="selectedBackGroud";
           }
         }
         else
         {
           if(i%2==0)
           {
              if(o!=null)
              {
                obj.className="trShallow";
                
              }
           }
           else
           {
               if(o!=null)
               {
                  obj.className="trDeep";
               }
           }
         }
    }
      
 }	
function checkchoice()
{
	var b = false;
	for(var i=0;i<document.dbinitForm.elements.length;i++)
	{
		if(document.dbinitForm.elements[i].type=="checkbox")
		{
			if(document.dbinitForm.elements[i].checked==true)
			{
				b=true;
				break;
			}
		}
	}
	if(!b){
		alert(NOTING_SELECT);
		return b;
	}else{
		//parent.frames["mil_menu"].location.reload();
		//var currnode=parent.frames["mil_menu"].Global.selectedItem;
		//alert(currnode.icon);
		//if(currnode.icon!='/images/open1.png')
			//currnode.setIcon('/images/open1.png');
		
		return confirm("确认要将选中的指标进行构库吗?");	
	}
}
</script>

<html:form action="/system/dbinit/fielditemlist">
<table width="100%" border="0" cellspacing="0"  id="tb" align="center" cellpadding="0" class="ListTable">
   	  <thead>
      <tr>
        <td align="center" class="TableRow" nowrap>
		  <!-- <bean:message key="column.select"/>&nbsp;-->
		  <input type="checkbox" name="selbox" onclick="batch_select(this,'listfieldForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
        </td>              
        <td align="center" class="TableRow" nowrap>
		<bean:message key="system.set.state"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="system.item.name"/>&nbsp;
	    </td>
        <td align="center" class="TableRow" nowrap>
		<bean:message key="system.item.code"/>&nbsp;
	    </td>
        <td align="center" class="TableRow" nowrap>
		<bean:message key="system.item.type"/>&nbsp;
	    </td>   
        <td align="center" class="TableRow" nowrap>
		<bean:message key="system.item.length"/>&nbsp;
	    </td>      
        <td align="center" class="TableRow" nowrap>
		<bean:message key="system.item.declen"/>&nbsp;
	    </td> 
        <td align="center" class="TableRow" nowrap>
		<bean:message key="system.item.codesetid"/>&nbsp;
	    </td>  
	    <hrms:priv func_id="3007115">	         	    	            
        <td align="center" class="TableRow" nowrap>
		<bean:message key="system.infor.oper"/>            	
	    </td>
         </hrms:priv>    	    	    		        	        	        
           </tr>
   	  </thead>
   	  <%int i=0; %>
      <hrms:extenditerate id="element" name="dbinitForm" property="listfieldForm.list" indexes="indexes"  pagination="listfieldForm.pagination" pageCount="20" scope="session">
          <%if(i%2==0){ %>
	     <tr class="trShallow" id="<%="a_"+i%>" onclick="changeTrColor('<%="a_"+i%>');">
	     <%} else { %>
	     <tr class="trDeep" id="<%="a_"+i%>" onclick="changeTrColor('<%="a_"+i%>');">
	     <%}%>
          <bean:define id="itemid" name='element' property='string(itemid)'/> 
        <td align="center" class="RecordRow" nowrap>
            <logic:equal name="element" property="string(useflag)" value="0">     		  
     		   <hrms:checkmultibox name="dbinitForm" property="listfieldForm.select" value="${itemid}" indexes="indexes"/>&nbsp;
	    	</logic:equal>    		  
	    </td>           
        <td align="center" class="RecordRow" nowrap>
            <logic:notEqual name="element" property="string(useflag)" value="0">
               <img src="/images/open1.png" border=0>
            </logic:notEqual>
	    </td>
         
        <td align="left" class="RecordRow" nowrap>
             &nbsp;<bean:write  name="element" property="string(itemdesc)" filter="true"/>&nbsp;
	    </td>
        <td align="left" class="RecordRow" nowrap>
             &nbsp;<bean:write  name="element" property="string(itemid)" filter="true"/>&nbsp;
	    </td>
        <td align="left" class="RecordRow" nowrap>
            <logic:equal name="element" property="string(itemtype)" value="A">  
               	<logic:equal name="element" property="string(codesetid)" value="0">  
				   &nbsp;<bean:message key="system.item.ctype"/>
	    	    </logic:equal>
                <logic:notEqual name="element" property="string(codesetid)" value="0">	    	    	
				   &nbsp;<bean:message key="system.item.cdtype"/>
               </logic:notEqual>	    	    	    	    			   
	    	</logic:equal>
            <logic:equal name="element" property="string(itemtype)" value="M">  
				&nbsp;<bean:message key="system.item.mtype"/>
	    	</logic:equal> 
            <logic:equal name="element" property="string(itemtype)" value="N">  
				&nbsp;<bean:message key="system.item.ntype"/>
	    	</logic:equal> 	 
            <logic:equal name="element" property="string(itemtype)" value="D">  
				&nbsp;<bean:message key="system.item.dtype"/>
	    	</logic:equal> 	 	    	   		    	           
	    </td>
        <td align="left" class="RecordRow" nowrap>
             &nbsp;<bean:write  name="element" property="string(itemlength)" filter="true"/>&nbsp;
	    </td>
        <td align="left" class="RecordRow" nowrap>
           <logic:notEqual name="element" property="string(decimalwidth)" value="0">	  
             &nbsp;<bean:write  name="element" property="string(decimalwidth)" filter="true"/>&nbsp;
           </logic:notEqual>	             
	    </td>	    	    
        <td align="left" class="RecordRow" nowrap>
           <logic:notEqual name="element" property="string(codesetid)" value="0">	            
             &nbsp;<bean:write  name="element" property="string(codesetid)" filter="true"/>&nbsp;
           </logic:notEqual>             
	    </td>	 
	    <hrms:priv func_id="3007115"> 	    
        <td align="center" class="RecordRow" nowrap>
			<!--【58039】 A01Z0 为系统字段，不允许修改 guodd 2020-02-14 -->
			<logic:notEqual value="A01Z0" name="element" property="string(itemid)">
			<a href="javascript:voider('<bean:write  name="element" property="string(itemid)" filter="true"/>','<bean:write  name="element" property="string(fieldsetid)" filter="true"/>')"><bean:message key="label.edit"/></a>
			</logic:notEqual>&nbsp;
	    </td>
	    </hrms:priv>
        </tr>
         <% i++; %>
      </hrms:extenditerate>
      <html:hidden name="dbinitForm" property="setid"/>
      <html:hidden name="dbinitForm" property="setid" styleId="infor"/>  
</table>
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="dbinitForm" property="listfieldForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="dbinitForm" property="listfieldForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="dbinitForm" property="listfieldForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>  
	        <td  align="right" nowrap class="tdFontcolor">
		      <p align="right">
		          <hrms:paginationlink name="dbinitForm" property="listfieldForm.pagination"
				nameId="listfieldForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="center" height="35px;">
            <hrms:priv func_id="3007114">
             <hrms:submit styleClass="mybutton" property="b_add">
            		<bean:message key="button.new.add"/>
	 	     </hrms:submit>
	 	     </hrms:priv>
	 	     <hrms:priv func_id="3007116">
	 	     <input type='button' class="mybutton" property="b_delete"  onclick='checkdelete()'
	 	     		value='<bean:message key="button.delete"/>' />
	 	     		</hrms:priv>
	 	     		<hrms:priv func_id="3007117">
          	 <input type='button' class="mybutton" property="b_toSorting"  onclick='toSorting()'
	 	     		value='<bean:message key="button.movenextpre"/>' />
	 	     		</hrms:priv>
	 	     		<hrms:priv func_id="3007118">
	 	     <logic:equal value="1" name="dbinitForm" property="useflag">
	          	 <hrms:submit styleClass="mybutton" property="b_create" onclick="return checkchoice();">
	            		<bean:message key="button.create.base"/>
		 	     </hrms:submit>
	 	     </logic:equal>
	 	     </hrms:priv>
	 	     <input type='button' class="mybutton" property="b_backtoup" onclick='backtoup()'
	 	     		value='<bean:message key="button.return"/>' />
	 	     </button>	
            </td>
          </tr>          
</table>

</html:form>


