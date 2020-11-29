<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="../../../../module/utils/js/template.js"></script>
<script language="javascript">
   function savefield()
  { 	  
     var hashvo=new ParameterSet();          
     var vos= document.getElementById("right");
     if(vos.length==0)
     {
       alert("已选指标项不能为空！");
       return false;
     }else
     {
        var code_fields=new Array();        
        for(var i=0;i<vos.length;i++)
        {
          var valueS=vos.options[i].value;          
          code_fields[i]=valueS;
          for(var j=i+1;j<vos.length;j++)
          {
          	if(valueS==vos.options[j].value)
          	{
          		alert("有相同指标存在，请重新选择");
          		return false;
          	}
          }
        }       
     }
     if('${dbinitForm.type}'=="0")
     {
     	var s= $F('setlist');
     	var strurl="/system/dbinit/inforlist.do?b_dbname=link`tableid="+s;
     	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
     	// var dw=410,dh=180,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
     	<%--var return_vo= window.showModalDialog(iframe_url, 'template_win', --%>
      				 <%--"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");--%>
      	<%--if(return_vo!=null){--%>
	     <%--hashvo.setValue("tablename",$F('setlist'));--%>
	     <%--hashvo.setValue("code_fields",code_fields); --%>
	     <%--hashvo.setValue("type",'${dbinitForm.type}');--%>
	     <%--hashvo.setValue("infor",'${dbinitForm.infor}');--%>
	     <%--var request=new Request({method:'post',onSuccess:showSelect,functionId:'1020010143'},hashvo);--%>
		<%--}			--%>
         return_vo ='';
         var theUrl = iframe_url;
         Ext.create('Ext.window.Window', {
             id:'inforlist',
             height:210,
             width: 430,
             resizable:false,
             modal:true,
             autoScroll:false,
             autoShow:true,
             html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
             renderTo:Ext.getBody(),
             listeners: {
                 'close': function () {
                     if (return_vo) {
                         hashvo.setValue("tablename",$F('setlist'));
                         hashvo.setValue("code_fields",code_fields);
                         hashvo.setValue("type",'${dbinitForm.type}');
                         hashvo.setValue("infor",'${dbinitForm.infor}');
                         var request=new Request({method:'post',onSuccess:showSelect,functionId:'1020010143'},hashvo);
                     }
                 }
             }

         });
     }else{
     	 if(confirm(WHETHER_WANT_REWORK_SUBCLASS+"?")){
		     hashvo.setValue("tablename",$F('setlist'));
		     hashvo.setValue("code_fields",code_fields); 
		     hashvo.setValue("type",'${dbinitForm.type}');
		     hashvo.setValue("infor",'${dbinitForm.infor}');
		     var request=new Request({method:'post',onSuccess:showSelect,functionId:'1020010143'},hashvo);
		 }
     }
     
     function showSelect(outparamters)
	   { 
	   	 if('${dbinitForm.type}'=="0"){
	   	 	alert("构建成功!");
	   	 	MusterInitData();
	   	 	/* 【7747】系统管理/库结构/指标体系，将基准岗位的所有子集都构库后，再点一下构库界面的确定按钮，
	   	 	 * 输入变化后的子集名称，前台就会报空指针异常，不对。  jingq add 2015.03.03
	   	 	 */
	   	 	document.getElementById("right").innerHTML = "";
	   	 }else{
	   	 	alert("修改成功!");
	   	 }
	     var thevo=new Object();
		 // window.returnValue=thevo;
		 //window.close();
            parent.parent.return_vo = thevo;
	   }
   }	
   
   function searchFieldList()
	{
	   var tablename=$F('setlist');
	   var hashvo=new ParameterSet();
	   hashvo.setValue("tablename",tablename);
	   hashvo.setValue("infor",'${dbinitForm.infor}');
	   hashvo.setValue("type",'${dbinitForm.type}');
   	   var request=new Request({method:'post',onSuccess:showFieldList,functionId:'1020010142'},hashvo);
	}
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(dbinitForm.left_fields,fieldlist);
		var itemlist = outparamters.getValue("itemlist");
		AjaxBind.bind(dbinitForm.right_fields,itemlist);
	}
	
	function MusterInitData()
	{
	   var hashvo=new ParameterSet();
	   hashvo.setValue("base",'${dbinitForm.infor}');
	   hashvo.setValue("type",'${dbinitForm.type}');
   	   var request=new Request({method:'post',onSuccess:showSetList,functionId:'1020010141'},hashvo);
	}
	
	/**从后台取得相应的数据,初始化前台*/
	function showSetList(outparamters)
	{
		var setlist=outparamters.getValue("setlist");
		var itemlist = outparamters.getValue("itemlist");
		var infor = outparamters.getValue("base");
		AjaxBind.bind(dbinitForm.setlist,setlist);
		if($('setlist').options.length>0)
		{
	  		$('setlist').options[0].selected=true;
	  		// $('setlist').fireEvent("onchange");
            myFireEvent($('setlist'));
		}
		//AjaxBind.bind(dbinitForm.right_fields,itemlist);
	}
function saveSort(){ 
	var hashvo=new ParameterSet();
	// var setid = document.getElementById("setlist").value;
	var setid = document.getElementsByName('setlist')[0].value;;
	hashvo.setValue("setid",setid);
	hashvo.setValue("displayid",selectTostr('right_fields'));
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:null,functionId:'1020010119'},hashvo);
}
function selectTostr(listbox){
  var vos,right_vo,i,str='';
  vos= document.getElementsByName(listbox);
  if(vos==null || vos[0].length==0){
  	return;  	
 	vos[0].options[0].selected=false;

  }
  //设为要可选状态
  right_vo=vos[0];  
  for(i=0;i<right_vo.options.length;i++){
	str += right_vo.options[i].value+",";
  }
  return str;  	
}
   /* 兼容fireEvent方法 */
   function myFireEvent(el) {
       var evt;
       if (document.createEvent) {
           evt = document.createEvent("MouseEvents");
           evt.initMouseEvent("change", true, true, window,
               0, 0, 0, 0, 0, false, false, false, false, 0, null);
           el.dispatchEvent(evt);
       } else if (el.fireEvent) { // IE
           el.fireEvent("onchange");
       }
   }
   function winClose(){
       if(parent.parent.Ext.getCmp('formationStoreroom')){
           parent.parent.Ext.getCmp('formationStoreroom').close();
       }
   }
</script>
<html:form action="/system/dbinit/inforlist">

<table width="530" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable1">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3">
		<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap >
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
                         <select name="left_fields" multiple="multiple" ondblclick="if(additem('left_fields','right_fields'))removeitem('left_fields');" style="height:229px;width:100%;font-size:9pt">
                         </select>
                    </td>
                    
                    </tr>
                   
                   </table>
                </td>
               
                <td width="4%" align="center">
                   <html:button  styleClass="smallbutton" property="b_addfield" onclick="if(additem('left_fields','right_fields'))removeitem('left_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <html:button  styleClass="smallbutton" property="b_delfield" onclick="additem('right_fields','left_fields');removeitem('right_fields');" style="margin-top:30px;">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	     
                </td>         
                
                
                <td width="46%" align="center">
                 
                 
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     <bean:message key="selfservice.query.queryfieldselected"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
                  <html:select styleId="right" name="dbinitForm" property="right_fields" multiple="multiple" size="10" ondblclick="additem('right_fields','left_fields');removeitem('right_fields');" style="height:250px;width:100%;font-size:9pt">
 		     </html:select>     
                  </td>
                  </tr>
                  </table>             
                </td>
                <td width="4%" align="center">
							<html:button styleClass="smallbutton" property="b_up" onclick="upItem($('right_fields'));saveSort();">
								<bean:message key="button.previous" />
							</html:button>
							<html:button styleClass="smallbutton" property="b_down" onclick="downItem($('right_fields'));saveSort();" style="margin-top:30px;">
								<bean:message key="button.next" />
							</html:button>
						</td>               
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap  colspan="3" style="height: 35">
               <input type="button" name="btnreturn" value='确定' class="mybutton" onclick=" savefield();">
	     <%--<input type="button" name="btnreturn" value='关闭' class="mybutton" onclick="window.close();">--%>
	     <input type="button" name="btnreturn" value='关闭' class="mybutton" onclick="winClose();">
          </td>
          </tr>
</table>
</html:form>

<script language="javascript">
   //var ViewProperties=new ParameterSet();
  	MusterInitData();
  	if(getBrowseVersion()){
  	    document.getElementsByName('b_addfield')[0].style.display = 'block'
  	    document.getElementsByName('b_delfield')[0].style.display = 'block'
  	    document.getElementsByName('b_up')[0].style.display = 'block'
  	    document.getElementsByName('b_down')[0].style.display = 'block'
    }
    /*
    var form = document.getElementsByName('dbinitForm')[0];
    var td = form.getElementsByTagName('table')[0].getElementsByTagName('tr')[1].getElementsByTagName('td')[0];
    if(!getBrowseVersion() || getBrowseVersion() == 10)
   		td.style.borderRight='';
   	else
   		td.style.borderRight="";
   	*/
    
</script>
