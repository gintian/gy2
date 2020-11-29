
    //  xgq //////////////////////////////////////////
    
   //作用：根据鼠标定位签章
	function DoMouseSignature(setname,signaturename,PageID)
	{ 
	   if(signxml.length>0){
	      var mLength=document.getElementsByName("iHtmlSignature").length;
   		  var signstr ="";
    	  for (var a=0;a<mLength;a++){
	       var vItem=document.getElementsByName("iHtmlSignature")[a];
	       if(signstr.indexOf(""+vItem.SignatureID)<0){//去掉重复的签章
	     		 signstr+=vItem.SignatureID+",";
	      }
	   }
	   
	 // var mx=event.clientX + document.body.scrollLeft - document.body.clientLeft-100;  //获取X坐标值
	 // var my=event.clientY + document.body.scrollTop  - document.body.clientTop;   //获取Y坐标值
	   var obj=eval(signaturename);//多个地方签章有问题，待修改（这个是得到div对象）
	    var mx=0;
	  var my=0;
	   if(obj){
	   mx=obj.style.left;  //获取X坐标值
	   my=obj.style.top;   //获取Y坐标值
	 }else{
	 }
	 if((""+mx).indexOf("px"))
	 mx = mx.substring(0,mx.length-2);
	 if((""+my).indexOf("px"))
	 my = my.substring(0,my.length-2);
	  //alert(obj.style.top);
	  if(batchsignatureid=="1"&&!isSelectedObjBySign())
   	    {
   	     
	       alert("没有选中需要批量签章的记录");
        	return;   	    
   	    } 
   	    if(batchsignatureid=="1"){
   	    		var table=$("obj_table");//得到左侧(人员、机构的table)
    			var objarr=new Array();
    			var a0100;	     
   			    var baseperid = recordbasepre;
    			var a0100id = recorda0100;
    
			    for (var i=table.rows.length-1; i>0; i--)
			    {
			        var thetr = table.rows[i];
			        var thechkbox=thetr.cells[0].children[0];
			       	if(!thechkbox.checked)
			        		continue;
			        if(operationtype=='8'||(infor_type==2&&operationtype=='9'))
			          a0100+=thetr.cells[3].innerHTML+",";
			        else			
			      	  a0100+=thetr.cells[2].innerHTML+",";
			      	  
			    }
      //获得选中的documentid
			      getdocumentids(a0100);	
			      var obj = document.getElementById('SignatureControl'); 
			      obj.FieldsList="HTJB=合同级别;XYBH=协议编号;BMJH=保密级别;JF=甲方签章;YF=乙方签章;HZNR=合作内容;QLZR=权利责任;CPMC=产品名称;DGSL=订购数量;DGRQ=订购日期"       //所保护字段
				  obj.UserName="wjd";                         //文件版签章用户
				  obj.PositionBySignType = 0;                 //设置签章所处位置，1表示中间,0表示左上角2表示右上角
				  obj.DivId= signaturename;//放到相应的div里
				  obj.Position(mx,my);                        //签章位置
			//	  obj.WebSetFontOther("True","","4","宋体",11,"$000000","False"); //盖章时自动显示日期
				  obj.SaveHistory="false";
				  obj.DocumentID=documentids;
				  if(obj.RunSignature()){                         //执行签章操作  
				     mLength=document.getElementsByName("iHtmlSignature").length;
				     for (var a=0;a<mLength;a++){
				        vItem=document.getElementsByName("iHtmlSignature")[a];
				       if(signstr.indexOf(""+vItem.SignatureID)<0){
				      //signstr+=vItem.SignatureID+",";
				      		addDocumentid(vItem.DocumentID,vItem.SignatureID,mx,my,a0100,PageID);
				      }
				   }
				  
				  }
    
    // recordbasepre = baseperid;
   //  recorda0100= a0100id;
    }else{
		  var obj = document.getElementById('SignatureControl'); 
		  obj.FieldsList="HTJB=合同级别;XYBH=协议编号;BMJH=保密级别;JF=甲方签章;YF=乙方签章;HZNR=合作内容;QLZR=权利责任;CPMC=产品名称;DGSL=订购数量;DGRQ=订购日期"       //所保护字段
		  obj.UserName="wjd";                         //文件版签章用户
		  obj.PositionBySignType = 0;                 //设置签章所处位置，1表示中间,0表示左上角2表示右上角
		  obj.DivId= signaturename;//放到相应的div里
		  obj.SaveHistory="false";
		  obj.Position(mx,my);                        //签章位置
	//	  obj.WebSetFontOther("True","","4","宋体",11,"$000000","False"); //盖章时自动显示日期
		  if(DocumentrecordID==""){
			  obj.DocumentID=DocumentID;
		  }else{
		 	  obj.DocumentID=DocumentrecordID;
		  }
		  obj.SIGNATUREID = DocumentID;
		  if(obj.RunSignature()){                         //执行签章操作  
		     mLength=document.getElementsByName("iHtmlSignature").length;
		     for (var a=0;a<mLength;a++){
		        vItem=document.getElementsByName("iHtmlSignature")[a];
		        if(signstr.indexOf(""+vItem.SignatureID)<0){
				      signstr+=vItem.SignatureID+",";
				      addDocumentid(vItem.DocumentID,vItem.SignatureID,mx,my,"",PageID);
		        }
		  	 }
		  
		  }
	  }
	}
	}
	//作用：获得documentid的所有签章
	function ShowSignature2(visibleValue)
	{
	   DeleteSignature();
	   var obj = document.getElementById('SignatureControl'); 
	   obj.ShowSignature(visibleValue);

	}
    //作用：显示或隐藏签章
    function ShowSignature(visibleValue)
    {
        
        ShowSignature2(visibleValue);
       var mLength=document.getElementsByName("iHtmlSignature").length;
       var signstr ="";
       var signstr2 ="";
       for (var i=0;i<mLength;i++){
           var vItem=document.getElementsByName("iHtmlSignature")[i];
           var mx = vItem.style.top;
           if((""+mx).indexOf("px"))
             mx = mx.substring(0,mx.length-2);
           if(signstr.indexOf(""+vItem.SignatureID)<0&&mx>0){
            signstr+=vItem.SignatureID+",";
           }else{
                vItem.Visiabled = "0";
                continue;
           }
          var divsign = vItem.DivId;
          if((""+divsign)!=null && (""+divsign)!='undefined' && (""+divsign).indexOf("S"))
             divsign = divsign.substring(0,(""+divsign).indexOf("S"));
           if(visibleValue=="0"){//传0进来为所有的都隐藏
                vItem.Visiabled = "0";
           }else if(divsign!="signature"+pageno){//不是本页的签章隐藏
                vItem.Visiabled = "0";
           }
           else{
               if(vItem.DocumentID==visibleValue&&signstr2.indexOf(""+vItem.SignatureID)<0&&mx>0){//如果当前Vitem的id等于传进来的ID并且signstr2没有这个ID,并且这个签章的top>0则显示
                   signstr2+=vItem.SignatureID+",";
                   vItem.Visiabled = "1";//是本页的并且这个ID不是重复出现的显示
               }else{//否则签章隐藏
                    vItem.Visiabled = "0";
               }
           }
           
       }
    }
	//作用：生成签章图片  xgq
	function CreateSignatureJif(flag)
	{
		if(signxml.length>0){
	   var mLength=document.getElementsByName("iHtmlSignature").length;
	   for (var i=0;i<mLength;i++){
	       var vItem=document.getElementsByName("iHtmlSignature")[i];
	        var mx = vItem.style.top;
	       if((""+mx).indexOf("px"))
			 mx = mx.substring(0,mx.length-2);
			 //if(mx<0)
			// continue;
	      // vItem.SaveSignatureAsGif("","1", 1,true,1);
	      // vItem.SaveSignatureAsGif("");
	      // vItem.SaveSignatureAsGif("","1",2);
	      var  signatureid  = vItem.SignatureID;
	        vItem.SaveImage= "1";//可以获取IMAGEVALUE
	         //vItem.RunSignature();
	         if(flag=="0") 
	       vItem.SaveSignatureAsGif(""+signatureid,null,null,false,1);
	       else if(flag=="1"){
	       vItem.SaveSignatureAsGif("C:\\Windows\\temp\\"+signatureid+".gif",null,null,true,"1");
	       }
	        //  vItem.SaveSignatureAsGif("E:/Tomcat5.5/temp");
	      // vItem.SaveSignatureAsGif("E:\\Tomcat5.5\\temp","1",2);
	      
	   }
	   } 
	    
	}
function DoJFSignature()
{
 var obj = document.getElementById('SignatureControl'); 
  obj.FieldsList="XYBH=协议编号;BMJH=保密级别;JF=甲方签章;HZNR=合作内容;QLZR=权利责任;CPMC=产品名称;DGSL=订购数量;DGRQ=订购日期"       //所保护字段
  obj.Position(460,260);                      //签章位置，屏幕坐标
  obj.UserName="lyj";                         //文件版签章用户
  alert("lyj");
  obj.RunSignature();                         //执行签章操作
}

		var wnd;  //定义辅助功能全局变量
//作用：进行乙方签章
function DoYFSignature()
{
     var obj = document.getElementById('SignatureControl'); 
  if(wnd != undefined){
    var results = wnd.split(";");
    obj.CharSetName = results[0];		  //多语言集
    obj.WebAutoSign = results[1];		  //自动数字签名
    obj.WebCancelOrder = results[2];	  //撤消顺序
    obj.PassWord = results[3];		  //签章密码

    var tmp = obj.WebSetFontOther((results[4]=="1"?true:false),
		results[5],results[6],results[7],results[8],results[9],
			(results[10]=="1"?true:false));		    //设置签章附加文字格式
    obj.WebIsProtect=results[11];		    //保护表单数据， 0不保护  1保护表单数据，可操作  2保存表单数据，并不能操作  默认值1
  }else{
    obj.WebIsProtect=1;			    //保护表单数据， 0不保护  1保护表单数据，可操作  2保存表单数据，并不能操作  默认值1
    obj.WebCancelOrder=0;			    //签章撤消原则设置, 0无顺序 1先进后出  2先进先出  默认值0
  }

  obj.FieldsList="HTJB=合同级别;XYBH=协议编号;BMJH=保密级别;JF=甲方签章;YF=乙方签章;HZNR=合作内容;QLZR=权利责任;CPMC=产品名称;DGSL=订购数量;DGRQ=订购日期"       //所保护字段
  obj.Position(0,0);                          //签章位置
  obj.SaveHistory="False";                    //是否自动保存历史记录,true保存  false不保存  默认值false
  obj.UserName="wjd";                         //文件版签章用户
  obj.SetPositionRelativeTag("YF",1);         //设置签章位置是相对于哪个标记的什么位置
  obj.PositionBySignType = 1;                 //设置签章所处位置，1表示中间
  //obj.ValidateCertTime = '1';                 //检测数字证书有效性，安装目前下必须有根证书Root.cer和吊销列表Crl.crl
  obj.RunSignature();                         //执行签章操作
}


//作用：进行手写签名
function DoSXSignature()
{
     var obj = document.getElementById('SignatureControl'); 
  if(wnd != undefined){
    var results = wnd.split(";");
    obj.CharSetName = results[0];		  //多语言集
    obj.WebAutoSign = results[1];		  //自动数字签名
    obj.WebCancelOrder = results[2];	  //撤消顺序
    obj.PassWord = results[3];		  //签章密码
    
    var tmp = obj.WebSetFontOther((results[4]=="1"?true:false),
		results[5],results[6],results[7],results[8],results[9],
			(results[10]=="1"?true:false));		    //设置签章附加文字格式
    obj.WebIsProtect=results[11];		    //保护表单数据， 0不保护  1保护表单数据，可操作  2保存表单数据，并不能操作  默认值1
  }else{
    obj.WebIsProtect=1;			    //保护表单数据， 0不保护  1保护表单数据，可操作  2保存表单数据，并不能操作  默认值1
    obj.WebCancelOrder=0;			    //签章撤消原则设置, 0无顺序 1先进后出  2先进先出  默认值0
  }
  
  obj.FieldsList="XYBH=协议编号;BMJH=保密级别;JF=甲方签章;YF=乙方签章;HZNR=合作内容;QLZR=权利责任;CPMC=产品名称;DGSL=订购数量;DGRQ=订购日期"       //所保护字段
  obj.Position(0,0);                           //手写签名位置
  //obj.SaveHistory="false";                   //是否自动保存历史记录,true保存  false不保存  默认值false
  obj.Phrase = "同意;不同意;请核实";           //设置文字批注常用词
  obj.HandPenWidth = 1;                        //设置、读取手写签名的笔宽
  obj.HandPenColor = 100;                      //设置、读取手写签名笔颜色
  obj.SetPositionRelativeTag("HZNR",1);        //设置签章位置是相对于哪个标记的什么位置
  obj.PositionBySignType = 1;                  //设置签章所处位置，1表示中间
  obj.UserName="wjd";                        //文件版签章用户
  alert(obj.UserName);
  obj.RunHandWrite();                          //执行手写签名
}



//作用：获取签章信息，以XML格式返回，并且分析显示数据.具体的XML格式请参照技术白皮书
//      具体分析后的内容如何处理，请自己做适当处理,本示例仅将返回结果进行提示。
function WebGetSignatureInfo(){
     var obj = document.getElementById('SignatureControl'); 
  var mSignXMl=obj.GetSignatureInfo();   //读取当前文档签章信息，以XML返回
  alert(mSignXMl);                                      //调试信息

  var XmlObj = new ActiveXObject("Microsoft.XMLDOM");
  XmlObj.async = false;
  var LoadOk=XmlObj.loadXML(mSignXMl);
  var ErrorObj = XmlObj.parseError;

  if (ErrorObj.errorCode != 0){
     alert("返回信息错误...");
  }else{

    var CurNodes=XmlObj.getElementsByTagName("iSignature_HTML");
    for (var iXml=0;iXml<CurNodes.length;iXml++){
	    var TmpNodes=CurNodes.item(iXml);
		/*
		alert(TmpNodes.selectSingleNode("SignatureOrder").text);  //签章序列号
		alert(TmpNodes.selectSingleNode("SignatureName").text);   //签章名称
		alert(TmpNodes.selectSingleNode("SignatureUnit").text);   //签章单位
		alert(TmpNodes.selectSingleNode("SignatureUser").text);   //签章用户
		alert(TmpNodes.selectSingleNode("SignatureDate").text);   //签章日期
		alert(TmpNodes.selectSingleNode("SignatureIP").text);     //签章电脑IP
		alert(TmpNodes.selectSingleNode("KeySN").text);           //钥匙盘序列号
		alert(TmpNodes.selectSingleNode("SignatureSN").text);     //签章序列号
		alert(TmpNodes.selectSingleNode("SignatureResult").text); //签章验测结果
		*/
    }

  }
}


//作用：设置禁止(允许)签章的密钥盘   具体参数信息请参照技术白皮书
function WebAllowKeySN()
{
     var obj = document.getElementById('SignatureControl'); 
  var KeySn=window.prompt("请输入禁止在此页面上签章的钥匙盘序列号:");
  obj.WebAllowKeySN(false,KeySn);
}


//作用：获取KEY密钥盘的SN序列号
function WebGetKeySN()
{
     var obj = document.getElementById('SignatureControl'); 
  var KeySn=obj.WebGetKeySN();
  alert("您的钥匙盘序列号为:"+KeySn);
}


//作用：校验用户的 PIN码是否正确
function WebVerifyKeyPIN()
{
     var obj = document.getElementById('SignatureControl'); 
  var KeySn = obj.WebGetKeySN();
  var mBoolean = obj.WebVerifyKeyPIN("123456");
  if (mBoolean){
  	alert(KeySn+":通过校验");
  }else{
  	alert(KeySn+":未通过校验");
  }
}


//作用：修改钥匙盘PIN码,参数1为原PIN码,参数2为修改后的PIN码
function WebEditKeyPIN()
{
  var oldPIN = window.prompt("请输入原来的PIN码");
  if(oldPIN == null){
	return;
  }
  var newPIN = window.prompt("请输入修改后的PIN码");
  if(newPIN == null){
	return;
  }
       var obj = document.getElementById('SignatureControl'); 
  var mBoolean = obj.WebEditKeyPIN(oldPIN,newPIN);
  if (mBoolean){
  	alert("钥匙盘PIN码修改成功!");
  }else{
  	alert("钥匙盘PIN码修改不成功!");
  }
}


//作用：批量验证签章
function BatchCheckSign()
{
     var obj = document.getElementById('SignatureControl'); 
   obj.BatchCheckSign();
}


//作用：辅助功能
function ParameterSetting(){
     var obj = document.getElementById('SignatureControl'); 
	var mParameter = new Array();
	mParameter[0] = obj.CharSetName;		//多语言集
	mParameter[1] = obj.WebAutoSign;		//自动数字签名
	mParameter[2] = obj.WebCancelOrder;	//撤消顺序
	mParameter[3] = obj.PassWord;		//签章密码
    if( wnd != undefined ){
		var results = wnd.split(";");
		mParameter[4] = results;
    }

	tmp =
	window.showModalDialog("ParameterSetting.jsp",mParameter,"dialogWidth:350px;dialogHeight:520px;menubar:no;toolbar:no;scrollbars:no;resizable:no;center:yes;status:no;help:no;");
	if(tmp != undefined){
		wnd = tmp;
	}
    if( wnd != undefined ){
		var results = wnd.split(";");
		obj.CharSetName = results[0];
		obj.WebAutoSign = results[1];
		obj.WebCancelOrder = results[2];
		obj.PassWord = results[3];

		var tmp = obj.WebSetFontOther((results[4]=="1"?true:false),
			results[5],results[6],results[7],results[8],results[9],
			(results[10]=="1"?true:false));
    }
}



//作用：删除签章
function DeleteSignature()
{
   var mLength=document.getElementsByName("iHtmlSignature").length; 
   var mSigOrder = "";
   for (var i=mLength-1;i>=0;i--){
       var vItem=document.getElementsByName("iHtmlSignature")[i];
	   //mSigOrder := 
	  // if (vItem.SignatureOrder=="1")
	  // {
         vItem.DeleteSignature();
	  // }
   }
}

//作用：移动签章
function MoveSignature()
{
     var obj = document.getElementById('SignatureControl'); 
  obj.MovePositionByNoSave(100,100);
  alert("位置增加100");
  obj.MovePositionByNoSave(-100,-100);
  alert("回到原来位置");
  obj.MovePositionToNoSave(100,100);
  alert("移动到100，100");	
}


//作用：脱密
function ShedCryptoDocument()
{
     var obj = document.getElementById('SignatureControl'); 
  obj.ShedCryptoDocument();
}


//作用：脱密还原
function ResetCryptoDocument()
{
     var obj = document.getElementById('SignatureControl'); 
  obj.ResetCryptoDocument();
}


//作用：打印文档
function PrintDocument(){
     var obj = document.getElementById('SignatureControl'); 
   var tagElement = document.getElementById('documentPrintID');
   tagElement.className = 'print';                                                 //样式改变为可打印
   var mCount = obj.PrintDocument(false,2,5);  //打印控制窗体
   alert("实际打印份数："+mCount);
   tagElement.className = 'Noprint';                                               //样式改变为不可打印
}
function initgetDocumentid(){
if(signxml.length>0){ 
	reloop();
	if(signxml.length>0){  
	 XMLDoc = XmlDocument.create();
	XMLDoc.async=false;
	}
	if(XMLDoc!=null){  
	//xmlrec=replaceAll(xmlrec,"&","");
	if(!XMLDoc.loadXML(signxml))
	   return;
	var rootNode = XMLDoc.documentElement;
	var recordnode = rootNode.childNodes;
	for(var i=0;i<recordnode.length;i++){
	var cid = recordnode[i].getAttribute("DocuemntID");
	if(cid!=""){
	ShowSignature(cid);
	DocumentrecordID = cid;
	ShowSignature('0');//隐藏签章
	}
	var id = recordnode[i].getAttribute("id");
	recordbasepre=id.substring(0,id.indexOf("|"));
	recorda0100=id.substring(id.indexOf("|")+1,id.length);
	
	}
	}
	if(DocumentrecordID.length==0)
	DocumentrecordID=DocumentID;
 //	ShowSignature('0');//隐藏签章
	ShowSignature(DocumentrecordID);//显示签章
	}
	
}

  
function initDocsignature(basepre,a0100){
if(signxml.length>0){ 
	recordbasepre=basepre;
	recorda0100=a0100;
	if(initsignature=="0"){
	initsignature="1";
	if(signxml.length>0){  
	 XMLDoc = XmlDocument.create();
	XMLDoc.async=false;
	}
	if(XMLDoc!=null){  
	//xmlrec=replaceAll(xmlrec,"&","");
	if(!XMLDoc.loadXML(signxml))
	   return;
	var rootNode = XMLDoc.documentElement;
	var recordnode = rootNode.childNodes;
	for(var i=0;i<recordnode.length;i++){
	var cid = recordnode[i].getAttribute("DocuemntID");
	if(cid!=""){
	ShowSignature(cid);
	ShowSignature('0');//隐藏签章
	}
	}
	}
	}
	getDocumentid(basepre,a0100);
	if(DocumentrecordID.length==0)
	DocumentrecordID=DocumentID;
 	ShowSignature('0');//隐藏签章
	ShowSignature(DocumentrecordID);//显示签章
}
}
function getDocumentid(basepre,a0100){
	if(XMLDoc==null){
	if(signxml.length>0){  
	 XMLDoc = XmlDocument.create();
	XMLDoc.async=false;
		
	}
	}
	if(XMLDoc!=null){  
	//xmlrec=replaceAll(xmlrec,"&","");
	if(!XMLDoc.loadXML(signxml))
	   return;
	var rootNode = XMLDoc.documentElement;
	var recordnode = rootNode.childNodes;
	for(var i=0;i<recordnode.length;i++){
	if((basepre+"|"+a0100)==recordnode[i].getAttribute("id")){
	DocumentrecordID=recordnode[i].getAttribute("DocuemntID");
	}
	}
	}
	//signxml = XMLDoc.xml;
	//alert(signxml);
} 
function addDocumentid(DocuemntID,SignatureID,pointx2,pointy2,a0100s,PageID){
	if(XMLDoc==null){
	if(signxml.length>0){  
	 XMLDoc = XmlDocument.create();
	XMLDoc.async=false;
	}
	}
	if(XMLDoc!=null){  
	//xmlrec=replaceAll(xmlrec,"&","");
	if(!XMLDoc.loadXML(signxml))
	   return;
	var rootNode = XMLDoc.documentElement;
	var recordnode = rootNode.childNodes;
	for(var i=0;i<recordnode.length;i++){
	if(a0100s==""){
	if((recordbasepre+"|"+recorda0100)==recordnode[i].getAttribute("id")){
	if(recordnode[i].getAttribute("DocuemntID").trim()==""){
		recordnode[i].getAttributeNode("DocuemntID").value=DocuemntID;
	}
	//创建元素item
	var item = XMLDoc.createElement("item");
	var username2 = XMLDoc.createAttribute("UserName");
	var SignatureID2 = XMLDoc.createAttribute("SignatureID");
	var pointx = XMLDoc.createAttribute("pointx");
	var pointy = XMLDoc.createAttribute("pointy");
	var pageid = XMLDoc.createAttribute("PageID");
	username2.value=""+username;
	SignatureID2.value=""+SignatureID;
	pointx.value = ""+pointx2;
	pointy.value=""+pointy2;
	pageid.value=""+PageID;
	item.setAttributeNode(username2);
	item.setAttributeNode(SignatureID2);
	item.setAttributeNode(pointx);
	item.setAttributeNode(pointy);
	item.setAttributeNode(pageid);
	recordnode[i].appendChild(item);
	
	}
	}else{
		if(a0100s.indexOf(recordnode[i].getAttribute("id"))!=-1){
	//创建元素item
	var item = XMLDoc.createElement("item");
	var username2 = XMLDoc.createAttribute("UserName");
	var SignatureID2 = XMLDoc.createAttribute("SignatureID");
	var pointx = XMLDoc.createAttribute("pointx");
	var pointy = XMLDoc.createAttribute("pointy");
	var pageid = XMLDoc.createAttribute("PageID");
	username2.value=""+username;
	SignatureID2.value=""+SignatureID;
	pointx.value = ""+pointx2;
	pointy.value=""+pointy2;
	pageid.value=""+PageID;
	item.setAttributeNode(username2);
	item.setAttributeNode(SignatureID2);
	item.setAttributeNode(pointx);
	item.setAttributeNode(pointy);
	item.setAttributeNode(pageid);
	recordnode[i].appendChild(item);
	
	}
	}
	}
	signxml = XMLDoc.xml;
	//alert(signxml);
} 
} 
function updateDocumentid(){

   var mLength=document.getElementsByName("iHtmlSignature").length;
   var signstr ="";
   var pageid="" ;
    for (var a=0;a<mLength;a++){
	      var vItem=document.getElementsByName("iHtmlSignature")[a];
	      if(signstr.indexOf(""+vItem.SignatureID)<0){
	     		 signstr+=vItem.SignatureID+",";
	      }
	}
  
   	if(XMLDoc==null){
		if(signxml.length>0){  
			 XMLDoc = XmlDocument.create();
			 XMLDoc.async=false;
		}
	}
	if(XMLDoc!=null){  
	if(!XMLDoc.loadXML(signxml))
	   return;
	var rootNode = XMLDoc.documentElement;
	var recordnode = rootNode.childNodes;
	for(var i=0;i<recordnode.length;i++){
		var item = recordnode[i].childNodes;
		var signstr2="";
		var  m = item.length;
		for(var j=0;j<m;j++){
		
			 for (var a=0;a<mLength;a++){		//更新移动位置
			      var vItem=document.getElementsByName("iHtmlSignature")[a];
			      var  pointx = vItem.style.left;
			      var pointy = vItem.style.top;
			      var mx = vItem.style.top;
			      if((""+mx).indexOf("px"))
					 mx = mx.substring(0,mx.length-2);
			      if(vItem.SignatureID==item[j].getAttribute("SignatureID")&&mx>0) {
				     	item[j].getAttributeNode("pointx").value=pointx;
				        item[j].getAttributeNode("pointy").value=pointy;
				        if(signstr.indexOf(item[j].getAttribute("SignatureID"))>=0){
					        if(item[j].getAttribute("PageID")!="")
					     	   pageid=item[j].getAttribute("PageID");
					     	else 
					     	{
					     	   pageid=pageno;
					     	   item[j].getAttributeNode("PageID").value=pageno;
					     	}
			       		 }
			        
			     }
			   }
		}
		for(var j=0;j<m;j++){
		 		//维护删除的签章
		   signstr2+=item[j].getAttribute("SignatureID")+",";
		 if(signstr.indexOf(item[j].getAttribute("SignatureID"))<0){
		  if(pageid==item[j].getAttribute("PageID")){
		      recordnode[i].removeChild(item[j]);
		      m = m-1;
		      continue;
		      }
		      }
		}
		  //因刷新引起电子签章不一致
		   for (var a=0;a<mLength;a++){
		       var vItem=document.getElementsByName("iHtmlSignature")[a];
		        var mx = vItem.style.top;
		       if((""+mx).indexOf("px"))
				 mx = mx.substring(0,mx.length-2);
		    	if(signstr2.indexOf(vItem.SignatureID)<0&&vItem.DocumentID==recordnode[i].getAttribute("DocuemntID")&&mx>0){
			    	var item = XMLDoc.createElement("item");
					var username2 = XMLDoc.createAttribute("UserName");
					var SignatureID2 = XMLDoc.createAttribute("SignatureID");
					var pointx = XMLDoc.createAttribute("pointx");
					var pointy = XMLDoc.createAttribute("pointy");
					var _pageid = XMLDoc.createAttribute("PageID");
					username2.value=""+username;
					SignatureID2.value=""+vItem.SignatureID;
					pointx.value = ""+vItem.style.left;
					pointy.value=""+vItem.style.top;
					_pageid.value=""+pageid;
					item.setAttributeNode(username2);
					item.setAttributeNode(SignatureID2);
					item.setAttributeNode(pointx);
					item.setAttributeNode(pointy);
					item.setAttributeNode(_pageid);
					recordnode[i].appendChild(item);
		    	}
		       
		   }
	}
	
	signxml = XMLDoc.xml;
} 
	  
}

function batchSignature(id){
//	var obj=eval("vieworhiddsignature");
	 if(batchsignatureid=="0")
	 { 
	       batchsignatureid="1";
	       document.getElementById("sign_div").innerHTML="取消批量签章";
  	    /*
  	       var obj=eval("vieworhiddsignature");
	       var obj2=eval("vieworhiddsignature2");
    		obj.style.display='none';
    		obj2.style.display='block';*/
        	
      }
      else
	  {
		   batchsignatureid="0";
	       document.getElementById("sign_div").innerHTML="批量签章";
	       /*
	       var obj=eval("vieworhiddsignature");
		   var obj2=eval("vieworhiddsignature2");
		   obj.style.display='block';
    	   obj2.style.display='none';
    	   */
      }
}
function getdocumentids(a0100s){
	documentids="";
	if(XMLDoc==null){
	if(signxml.length>0){  
	 XMLDoc = XmlDocument.create();
	XMLDoc.async=false;
	}
	}
	if(XMLDoc!=null){  
	//xmlrec=replaceAll(xmlrec,"&","");
	if(!XMLDoc.loadXML(signxml))
	   return;
	var rootNode = XMLDoc.documentElement;
	var recordnode = rootNode.childNodes;
	for(var i=0;i<recordnode.length;i++){
	if(a0100s.indexOf(recordnode[i].getAttribute("id"))!=-1){
	if(recordnode[i].getAttribute("DocuemntID").trim()==""){
		var time=new Date();
	 var lg=time.getTime();
	 lg = lg+i;
	 documentids+=lg+",";
		recordnode[i].getAttributeNode("DocuemntID").value=lg;
	}else{
	documentids+=recordnode[i].getAttribute("DocuemntID").trim()+",";
	}
	}
	}
	signxml = XMLDoc.xml;
} 
}