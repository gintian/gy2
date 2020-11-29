function selectAll(obj)
{
   var arr=document.getElementsByName("idArray");
   if(arr)
   {
      for(var i=0;i<arr.length;i++)
      {
          if(obj.checked)
          {
             arr[i].checked=true;
          }
          else
          {
            arr[i].checked=false;
          }
      }
   }
}
//xus 19/9/2 浏览器兼容：能力素质-素质模型-岗位序列素质模型-新增 
var newPostModal_object_type = "";
var newPostModal_object_id = "";
var newPostModal_codesetid = "";
function newPostModal(object_type,object_id,codesetid)
{
    if(object_id=='')
    {
      if(object_type=='1')
        alert("请选择职务类别！");
      else if(object_type=='2')
        alert("请选择岗位序列！");
      else if(object_type=='3')
        alert("请选择岗位！");
      return;
    }
    if(object_type=='3'&&codesetid!='@K')
    {
       alert("请选择岗位！");
       return;
    }
    var infos=new Array();
	infos[0]="-1";
	infos[1]="35";
	infos[2]=object_type;
	infos[3]=object_id;
	var thecodeurl="/performance/kh_system/kh_template/init_kh_item.do?br_selectpoint=query"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl; 
	//xus 19/9/2 浏览器兼容：能力素质-素质模型-岗位序列素质模型-新增	
	newPostModal_object_type = object_type;
	newPostModal_object_id = object_id;
	newPostModal_codesetid = codesetid;	
	var config = {
	        width:450,
	        height:360,
	        type:'2',
	        dialogArguments:infos,
	        id:'newPostModal_win'
	    }
    if(!window.showModalDialog){
        window.dialogArguments = infos;
    }
    modalDialog.showModalDialogs(iframe_url,'newPostModal_win',config, newPostModal_callback);
	/*
	 * 原来代码
	var points= window.showModalDialog(iframe_url, infos, 
		        "dialogWidth:450px; dialogHeight:360px;resizable:no;center:yes;scroll:no;status:no;");	
    if(points)
    {		        			
	    if(points=='undefined'||points=='')
	    {			
	       return;
	    }
	    var hashvo=new ParameterSet();
	    hashvo.setValue("object_type",object_type);
	    hashvo.setValue("object_id",object_id);
	    hashvo.setValue("points",points);
	    hashvo.setValue("codesetid",codesetid);
       var request=new Request({asynchronous:false,onSuccess:additem_ok,functionId:'90100170030'},hashvo); 
	}
    */
}
//xus 19/9/2 浏览器兼容：能力素质-素质模型-岗位序列素质模型-新增	回调函数
function newPostModal_callback(points){
	if(points)
    {		        			
	    if(points=='undefined'||points=='')
	    {			
	       return;
	    }
	    var hashvo=new ParameterSet();
	    hashvo.setValue("object_type",newPostModal_object_type);
	    hashvo.setValue("object_id",newPostModal_object_id);
	    hashvo.setValue("points",points);
	    hashvo.setValue("codesetid",newPostModal_codesetid);
       var request=new Request({asynchronous:false,onSuccess:additem_ok,functionId:'90100170030'},hashvo); 
	}
}
function additem_ok(outparameters)
{
    var object_id=outparameters.getValue("object_id");
    var object_type=outparameters.getValue("object_type");
    var codesetid=outparameters.getValue("codesetid");
    alert("添加成功！");
    postModalForm.action="/competencymodal/postseq_commodal/post_modal_list.do?b_query=query&a_code="+codesetid+object_id+"&object_type="+object_type;
    postModalForm.submit();
    
}
function del(object_type,object_id,codesetid)
{
   var points=document.getElementsByName("idArray");
   var num=0;
   var ids="";
   if(points)
   {
       for(var i=0;i<points.length;i++)
       {
          if(points[i].checked)
          {
              num++;
              ids+="~"+points[i].value;
          }
       }
   }
   if(num==0)
   {
      alert("请选择要删除的项目！");
      return;
   }
   if(confirm("确定删除选择的项目?"))
   {
        var hashvo=new ParameterSet();
	    hashvo.setValue("object_type",object_type);
	    hashvo.setValue("object_id",object_id);
	    hashvo.setValue("points",ids.substring(1));
	    hashvo.setValue("codesetid",codesetid);
       var request=new Request({asynchronous:false,onSuccess:del_ok,functionId:'90100170031'},hashvo); 
 
   }
}
function del_ok(outparameters)
{
    var object_id=outparameters.getValue("object_id");
    var object_type=outparameters.getValue("object_type");
    var codesetid=outparameters.getValue("codesetid");
    alert("删除成功！");
    postModalForm.action="/competencymodal/postseq_commodal/post_modal_list.do?b_query=query&a_code="+codesetid+object_id+"&object_type="+object_type;
    postModalForm.submit();
 
}

//19/9/9 xus 能力素质，素质模型，岗位序列素质模型，点编辑没有反应
var editPostModal_codesetid = "";
var editPostModal_object_id = "";
var editPostModal_object_type = "";
function editPostModal(object_type,codeitemid,codesetid,point_id,object_id)
{
   var thecodeurl="/competencymodal/postseq_commodal/post_modal_list.do?b_edit=edit`codeitemid="+codeitemid+"`codesetid="+codesetid+"`object_type="+object_type+"`point_id="+point_id; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);

//	var points= window.showModalDialog(iframe_url, null, 
//		        "dialogWidth:450px; dialogHeight:320px;resizable:no;center:yes;scroll:no;status:no;");	
	
	//19/9/9 xus 浏览器兼容谷歌浏览器
	editPostModal_codesetid = codesetid;
	editPostModal_object_id = object_id;
	editPostModal_object_type = object_type;
	var config = {
	        width:450,
	        height:360,
	        type:'2',
	        id:'editPostModal_win'
	    }
    modalDialog.showModalDialogs(iframe_url,'editPostModal_win',config, editPostModal_callback);
}
//19/9/9 xus回调
function editPostModal_callback(points)
{
	if(points)
    {
		postModalForm.action="/competencymodal/postseq_commodal/post_modal_list.do?b_query=link&a_code="+editPostModal_codesetid+editPostModal_object_id+"&object_type="+editPostModal_object_type;
		postModalForm.submit();
    }	
}

function savePostModal()
{
    postModalForm.action="/competencymodal/postseq_commodal/post_modal_list.do?b_save=save&isClose=1";
    postModalForm.submit();
}
function confirmSavePostModal(outparameters)
{	
	var infor=outparameters.getValue("infor");
	infor=getDecodeStr(infor);
	if(infor=="ok"){
	    postModalForm.action="/competencymodal/postseq_commodal/post_modal_list.do?b_save=save&isClose=1";
   		postModalForm.submit();
	}else{
		if(confirm(infor)){
	        postModalForm.action="/competencymodal/postseq_commodal/post_modal_list.do?b_save=save&isClose=1";
	   		postModalForm.submit();
		}
	}

}
function closeWin()
{
   window.returnValue=null;
   window.close();
}
function expOut(object_type,object_id,codesetid,historyDate)
{
    if(object_id=='')
    {
      if(object_type=='1')
      {
        alert("请选择职务类别！");
        return;
      }else if(object_type=='2'){
        alert("请选择岗位序列！");
        return;
      }
      else if(object_type=='3'){
        alert("请选择具体组织机构导出Excel！");
        return;
    }
      
    }
    var hashvo=new ParameterSet();
	hashvo.setValue("object_type",object_type);
	hashvo.setValue("object_id",object_id);
	hashvo.setValue("codesetid",codesetid);
	hashvo.setValue("historyDate",historyDate);
    var request=new Request({asynchronous:false,onSuccess:exp_ok,functionId:'90100170039'},hashvo); 
}
function exp_ok(outparameters)
{
    //zhangh 2020-4-7 岗位序列素质模型,下载改为使用VFS
    var fileName=outparameters.getValue("fileName");
    fileName = decode(fileName);
    var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+fileName);
}
function importModal(import_type,object_type,object_id,codesetid,historyDate)
{
    if(object_type=='3'&&(object_id==''||codesetid!='@K'))
    {
       alert("请选择岗位！");
       return;
    }
    var info="";
    if(import_type=='1')
        info="确认要引用岗位序列素质模型吗?";
    else
        info="确认要引用职务序列素质模型吗?";
    if(confirm(info))
    {
       var hashvo=new ParameterSet();
	   hashvo.setValue("object_type",object_type);
	   hashvo.setValue("object_id",object_id);
	   hashvo.setValue("codesetid",codesetid);
	   hashvo.setValue("import_type",import_type);
	   hashvo.setValue("historyDate",historyDate);
       var request=new Request({asynchronous:false,onSuccess:imp_ok,functionId:'90100170040'},hashvo); 
    }else{
       return;
    }
}
function imp_ok(outparameters)
{
    var object_id=outparameters.getValue("object_id");
    var object_type=outparameters.getValue("object_type");
    var codesetid=outparameters.getValue("codesetid");
    var info = outparameters.getValue("info");
    if(info=='ok')
    {
       alert("引入成功!");
       postModalForm.action="/competencymodal/postseq_commodal/post_modal_list.do?b_query=query&a_code="+codesetid+object_id+"&object_type="+object_type;
       postModalForm.submit();
    }else{
        alert(info);
        return;
    }
}
function queryHistory()
{
    var hist=document.getElementById("histime").value;
    if(hist==null||trim(hist)=='')
    {
        alert("请选择历史时间点！");
        return;
    }
    window.returnValue=hist;
    window.close();
}
function historyDate(object_type)
{
    var thecodeurl="/competencymodal/postseq_commodal/post_modal_tree.do?br_his=history"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var points= window.showModalDialog(iframe_url, null, 
		        "dialogWidth:450px; dialogHeight:380px;resizable:no;center:yes;scroll:no;status:no;");	
    if(points)
    {      
       postModalForm.action="/competencymodal/postseq_commodal/post_modal_tree.do?b_tree=tree&object_type="+object_type+"&historyDate="+points;
       postModalForm.target="il_body";
       postModalForm.submit();
    }		
}
function editClass(object_type,historyDate)
{
   parent.location="/system/codemaintence/codetree.do?b_query=link&codesetid=70&object_type="+object_type+"&historyDate="+historyDate;
}
function showFulldesc(srcobj,desc)
{
      date_desc=srcobj;
      Element.show('date_panel');   
      var pos=getAbsPosition(srcobj);
	  with($('date_panel'))
	  {
	        style.position="absolute";
	        style.posLeft=pos[0]-250;
	        style.posTop=pos[1]-15+srcobj.offsetHeight;  
      }  
	  var dataHtml="";
	  dataHtml+="<table width='400' border='0' cellspacing='0' bgColor='#FFFFFF'  align='center' cellpadding=0' class='ListTable'   > ";
	  dataHtml+="<thead><tr> <td  width='60'  align='center' class='TableRow' nowrap >&nbsp;</td>";
	  dataHtml+="</tr>  </thead>";
	  dataHtml+="<tr><td align='left' class='RecordRow' >"+getDecodeStr(desc)+"</td></tr>";
	  dataHtml+="</table>";	
	  date_panel.innerHTML=dataHtml;
	  date_panel.innerHTML=date_panel.innerHTML+"<iframe src=\"javascript:false\" style=\"position:absolute; visibility:inherit; top:0px; left:0px;"
   			 				+"width:"+date_panel.offsetWidth+"; height:"+date_panel.offsetHeight+"; " 					    	
   			 				+"z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';\"></iframe>";   				
	  var pos=getAbsPosition(srcobj);
	  var pos0=pos[0];
	  var pos1=pos[1];
	  var srcobj_width=srcobj.offsetWidth;
	  var srcobj_height=srcobj.offsetHeight;
      var op=eval('date_panel');	
      with($('date_panel'))
	 {
		  style.position="absolute";
		  style.posLeft=pos0-250;	 
		  if(window.document.body.offsetHeight<(pos[1]+srcobj.offsetHeight+20+op.offsetHeight))
		  {    	 
			   style.posTop=pos[1]-op.offsetHeight-20;			    	
		 }
		 else
		 {	 
			   style.posTop=pos[1]-10+srcobj.offsetHeight;  
		 }
	  }  
}
function hidden()
{
   	Element.hide('date_panel');
}