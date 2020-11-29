<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes></hrms:themes>

<logic:equal value="ok" name="itemOptionForm" property="submitflag">
     <script>
         // window.returnValue="ok";
         // window.close();
         parent.window.opener.setRelevanItemReturn('ok');
         parent.window.close();
     </script>
</logic:equal>

<style type="text/css">
.selectDiv{
	border: 0px solid #C4D8EE;
	margin-top:10px; margin-left:10px;
	margin-right:10px;margin-bottom:10px;
	padding-top:10px; padding-left:10px;
	padding-right:10px;padding-bottom:10px;
}	
</style>

<script type="text/javascript">
    var sourcedarr=new Array();
    var targetarr=new Array();
    var mess = "";
     function additems(fieldsetid){
          var S_source=document.getElementById("S_source");
          var S_target=document.getElementById("S_target");
          if(S_source.selectedIndex=='-1' || S_target.selectedIndex=='-1'){
              alert("请选择对应选项");
              return;
          }
          var svalue=S_source.options[S_source.selectedIndex].value;
          var stext=S_source.options[S_source.selectedIndex].text;
          var tvalue=S_target.options[S_target.selectedIndex].value;
          var ttext=S_target.options[S_target.selectedIndex].text;
         
          var str=sourcedarr.join();
          var indexs=str.indexOf(svalue);
          if(indexs>=0){
              alert("基准岗位指标已关联；请选择其他指标");
              return;
          }
          str=targetarr.join();
          indexs=str.indexOf(tvalue);
          if(indexs>=0){
              alert("岗位指标选择指标已关联；请选择其他指标");
              return;
          }
          if(fieldsetid != 'H00'){
	          var hashvo = new ParameterSet();
	          hashvo.setValue("svalue",svalue);
	          hashvo.setValue("tvalue",tvalue);
	          var request = new Request({method:'post',onSuccess:combineitem,functionId:'18010000070'},hashvo);
	          function combineitem(out){
	        	  mess = out.getValue("mess");
	          }
	          
	          if(mess == "N"){
	        	  alert(stext+"  长度大于   "+ttext+"  长度！");
	        	  return;
	          }
          }	  
          var sss=svalue+":"+stext+"<=>"+tvalue+":"+ttext;
          document.getElementById("relevantitem").options.add(new Option(sss,sss)); 
          sourcedarr.push(svalue);
          targetarr.push(tvalue);
     }

     function saveitem(){
         var options=document.getElementById("relevantitem").options;
         //if(options.length<1){
        //	 window.close();
        //	 return;
        // }
         var state = "";
         if(options.length<1)
        	 state = "0";
         for(var i=0;i<options.length;i++){
        	 document.getElementById("relevantitem").options[i].selected="selected";
         }
         document.itemOptionForm.action = "/system/options/standardduty_duty_item.do?b_saveitem=link&state="+state;
         document.itemOptionForm.submit();
         
     }

     function deleteitem(){
          var relevantitem=document.getElementById("relevantitem");
          if(relevantitem.length<1 || relevantitem.selectedIndex == -1)
        	  return;
         // document.getElementById("relevantitem").options.remove(relevantitem.selectedIndex);
          var options=document.getElementById("relevantitem").options;  //获取选中的个数，点击删除时可以全部删除，而不是删除第一个 liuzy 20150803
          var len=options.length;
          for(var key=0;key<len;key++){
              document.getElementById("relevantitem").options.remove(options.selectedIndex);
          }
          setselecedarr();
     }
     function setItemWinClose(){
     	  parent.window.close();
     }
     function setselecedarr(){
    	 var options=document.getElementById("relevantitem").options;
    	 sourcedarr.length=0;
    	 targetarr.length=0;
    	    for(var key=0;key<options.length;key++){
    	      var value=options[key].value;
    	      var arr=value.split("<=>");
    	      var setid=arr[0].substr(0,5);
    	      var targetid=arr[1].substr(0,5);
    	      sourcedarr.push(setid);
    	      targetarr.push(targetid);
    	    }
     }

     function loadS_target(){
    	 var S_source=document.getElementById("S_source");
    	 if(S_source.selectedIndex<0)
    		 return;
    	 var svalue=S_source.options[S_source.selectedIndex].value;
    	 var hashvo = new ParameterSet();
    	 hashvo.setValue("itemid",svalue);
    	 hashvo.setValue("targetsetid",'${itemOptionForm.targetsetid}');
    	 var request = new Request({method:'post',onSuccess:setS_target,functionId:'18010000069'},hashvo);
    	 
     }
     function setS_target(outParameters){
    	 var itemlist = outParameters.getValue("itemlist");
    	 document.getElementById("S_target").innerHTML = "";

    	 for(var i=0;i<itemlist.length;i++){
    		 var item = itemlist[i].split(':');
    		 document.getElementById("S_target").options.add(new Option(item[1],item[0]));
    	 }
	 }
</script>



<html:form action="/system/options/standardduty_duty_item">
<div class="fixedDiv3">
      <div class="RecordRow" style="width: 500px; height:90%;border:0px; margin-bottom: 8px;">
             <table border="0" cellspacing="0" align="center" cellpadding="0"
				class="ListTable" width="100%" >
                <tr>
                   <td align="center" width="100%" class="TableRow" nowrap>
                                                          对应指标关系设置
                   </td>
                </tr>
                <tr>
                   <td width="100%" class="RecordRow" style="border-top: 0px;" nowrap>
                      <table border=0 width="100%" >
                          <tr>
                                <td  width="50%">
                                    <fieldset style="margin:10px,10px,0px,10px;">
                                       <legend>基准岗位（${itemOptionForm.fieldsetdesc }）</legend>
                                       <div class="selectDiv">
                                           <html:select property="selectset" name="itemOptionForm" multiple="true" style="height:100px;width:160px;font-size:9pt" styleId="S_source" >
                                               <html:optionsCollection name="itemOptionForm" property="sourceitems" value="itemid" label="itemdesc" />
                                           </html:select>
                                           
                                       </div>
                                    </fieldset>
                                </td>
                                <td width="50%">
                                     <fieldset style="margin:10px,10px,0px,10px;">
                                       <legend>岗位（${itemOptionForm.targetsetdesc }）</legend>
                                       <div class="selectDiv">
                                          <logic:equal value="H00" name="itemOptionForm" property="fieldsetid">
                                                <html:select property="selectset" name="itemOptionForm" multiple="true" style="height:100px;width:160px;font-size:9pt" styleId="S_target" >
                                                   <html:optionsCollection name="itemOptionForm" property="targetitems" value="itemid" label="itemdesc" />
                                                </html:select>
                                          </logic:equal>
                                          <logic:notEqual value="H00" name="itemOptionForm" property="fieldsetid">
	                                           <select multiple="true" style="height:100px;width:160px;font-size:9pt" id="S_target" >
	                                           </select>
	                                           <script>  loadS_target(); document.getElementById("S_source").onchange=loadS_target;  </script>
                                           </logic:notEqual>
                                       </div>
                                    </fieldset>
                                </td>
                          </tr>
                          <tr>
                            
                              <logic:equal value="H00" name="itemOptionForm" property="fieldsetid">
                                     <td align="center" colspan=2 style="padding-top: 10px;">
                                     	  <!-- 19/3/13 xus ie非兼容模式 浏览器兼容 岗位管理-基准岗位-设置-操作设置，点击关联，界面空白-->
                                          <button type="button" class="mybutton" onclick="additems('${itemOptionForm.fieldsetid}');">关联</button> 
                                            
                                     </td>
                              </logic:equal>
                              <logic:notEqual value="H00" name="itemOptionForm" property="fieldsetid">
                                     <td align="center" colspan=2 style="padding-top: 10px;">
                                     	  <!-- 19/3/13 xus ie非兼容模式 浏览器兼容 岗位管理-基准岗位-设置-操作设置，点击关联，界面空白-->
                                          <button type="button" class="mybutton" onclick="additems('${itemOptionForm.fieldsetid}');">关联</button> 
                                     </td>
                              </logic:notEqual>
                          </tr>
                          <tr>
                            <td colspan=2>
                               <fieldset style="margin:0px,10px,10px,10px;">
                                   <legend>对应关系</legend>
                                   <div class="selectDiv">
                                        <html:select name="itemOptionForm" property="saveitems" multiple="true" style="height:100px;width:400px;font-size:9pt" styleId="relevantitem">
                                          <logic:iterate id="var" name="itemOptionForm" property="relevantitem">
                                                  <option value="${var }">${var}</option>
                                          </logic:iterate>
                                       </html:select>
                                   </div>
                               </fieldset>
                                <logic:equal value="H00" name="itemOptionForm" property="fieldsetid">
                                                （“岗位说明书”分类自动关联 ）
                                </logic:equal>
                               
                            </td>
                          </tr>
                      </table>
                   </td>
                </tr>
            </table>
      </div>
      <div align="center">
      	 <!-- 19/3/13 xus ie非兼容模式 浏览器兼容 岗位管理-基准岗位-设置-操作设置，点击关联，界面空白-->
      	 <button type="button" class="mybutton" onclick="saveitem();">确定</button>
         <button type="button" class="mybutton" onclick="deleteitem();">删除</button>
         <button type="button" class="mybutton" onclick="javascript:top.window.close();">取消</button>
      </div>
</div>         
</html:form>
<script>
 setselecedarr();
</script>