<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="java.net.URLDecoder"%>
<HTML>
<title></title>
<%	String filename = request.getParameter("filename"); 
	filename = URLDecoder.decode(filename);
	int index = filename.indexOf(".");
	String type = filename.substring(index);
	String recordId = filename.substring(0,index);
	
	//filename = URLEncoder.encode(filename);
	//System.out.println(filename);
%>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">

<script language="javascript">
//作用：显示操作状态
function StatusMsg(mString){
  //StatusBar.innerHTML=mString;
}

//初始化对象
function initObject(){
  WebOffice.WebUrl="OfficeServer.jsp";      //后台处理页路径，用来执行后台数据处理业务。本属性支持相对路径
  WebOffice.RecordID="<%=recordId %>";                //文档记录号
  WebOffice.Template="";                    //模板记录号
  WebOffice.FileName="<%=filename %>";            //文档名称
  WebOffice.FileType="<%=type %>";                //文档类型  .doc  .xls
  WebOffice.UserName="文档编辑人";          //当前操作员
  WebOffice.EditType="4";                   //编辑状态：第一位可以为0,1,2,3其中：0不可编辑；1可以编辑,无痕迹；2可以编辑,有痕迹,不能修订；3可以编辑,有痕迹,能修订。

  WebOffice.MaxFileSize = 10 * 1024;         //最大的文档大小控制，默认是8M，现在设置成4M。
  WebOffice.showMenu="0";                  //是否显示菜单：1显示；0不显示

  WebOffice.CreateFile();                   //创建空白文档
}

//作用：打开服务器文档
function LoadDocument(){
  if (!WebOffice.WebOpen()){                //打开该文档    交互OfficeServer的OPTION="LOADFILE"
    StatusMsg(WebOffice.Status);            //显示状态，从OfficeServer中读取
  }else{
    StatusMsg(WebOffice.Status);            //显示状态，从OfficeServer中读取
  }
}

//作用：保存服务器文档
function SaveDocument(){
  if (!WebOffice.WebSave()){     //交互OfficeServer的OPTION="SAVEFILE"
    StatusMsg(WebOffice.Status);
  }else{
    StatusMsg(WebOffice.Status);
  }
}

//作用：创建空白文档
function CreateFile(){
  WebOffice.CreateFile();
  StatusMsg(WebOffice.Status);
}

//作用：打开本地文件
function WebOpenLocal(){
  var result = WebOffice.WebOpenLocalFile();
  if(result){
    StatusMsg("打开本地文档成功！");
  }else{
    StatusMsg(WebOffice.Status);
  }
}

//作用：保存文件到本地
function WebSaveLocal(){
  var result = WebOffice.WebSaveLocalFile();
  if(result){
    StatusMsg("保存文档到本地成功！");
  }else{
    StatusMsg(WebOffice.Status);
  }
}

//作用：获取文档页数（VBA扩展应用）
function WebDocumentPageCount(){
  if (WebOffice.FileType==".doc"){
    var intPageTotal = WebOffice.WebObject.Application.ActiveDocument.BuiltInDocumentProperties(14);
    alert("文档页总数："+intPageTotal);
  }
  
  if (WebOffice.FileType==".wps"){
    var intPageTotal = WebOffice.WebObject.PagesCount();
    alert("文档页总数："+intPageTotal);
  }
}

//作用：接受文档中全部痕迹（VBA扩展应用）
function WebAcceptAllRevisions(){
  WebOffice.WebObject.Application.ActiveDocument.AcceptAllRevisions();
  var mCount = WebOffice.WebObject.Application.ActiveDocument.Revisions.Count;
  if(mCount>0){
    StatusMsg("接受痕迹失败！");
    return false;
  }else{
    StatusMsg("文档中的痕迹已经全部接受！");
    return true;
  }
}

//作用：退出iWebOffice
function UnLoad(){
  try{
    if (!WebOffice.WebClose()){
      StatusMsg(WebOffice.Status);
    }
    else{
      StatusMsg("关闭文档...");
    }
  }
  catch(e){
    alert(e.description);
  }
}


</script>

<BODY onLoad="" onunload="UnLoad()"><!--创建新文件-->
  <script src="/system/options/customreport/iWebOffice2000.js"></script>
</BODY>
</HTML>
<script>
initObject();
LoadDocument();
</script>