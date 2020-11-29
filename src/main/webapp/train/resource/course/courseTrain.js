//保存
function save(oper) {
	var id = document.getElementsByName("id")[0].value;
	var r5003 = document.getElementById("r5003").value;
	var r5004 = "";
	if(typeof(eval("document.all.r5004_value"))!= "undefined")
	    r5004 = document.getElementById("r5004_value").value;
	else
		r5004 = document.getElementById("acode").value;
	var hashvo=new ParameterSet();
	hashvo.setValue("id",id);
	hashvo.setValue("codeid",r5004);	
	hashvo.setValue("codeitemdesc",r5003);
   	var request=new Request({method:'post',asynchronous:true,onSuccess:addcode,onFailure:saveFail, functionId:'202003005101'},hashvo);
}

function saveFail(outparamters){
	var info = outparamters.getValue("flag");
	if(typeof(info) == "undefined")
		alert("与服务器断开链接！");
}

function addcode(outparamters) {
	var itemid = outparamters.getValue("itemid");
	var f = outparamters.getValue("flag");
	if(itemid!=null&&itemid!=""){
		var codeitemdesc = getDecodeStr(outparamters.getValue("codeitemdesc"));
		var codeid = outparamters.getValue("cid");
		var flag=true;
		var currnode=parent.frames['mil_menu'].Global.selectedItem;	

		if(currnode==null)
			return;

		currnode = currnode.root();
		//分类树上对应的节点
		var selectNode = getTreeItem(codeid, currnode);
		//课程放入对应的节点,无对应节点放在根节点下面
		if(selectNode)
		    currnode = selectNode;
		
		var itemNode = getTreeItem(itemid, currnode);
		if(itemNode) {
			currnode = itemNode;
			flag=false;			
			if(codeitemdesc!=currnode.text) {
				currnode.setText(codeitemdesc);
				currnode.setTitle(codeitemdesc);
				currnode.reload();
			}
		}
		
		if(f=="edit")
			flag=false;	
		
		if(flag) {
			var action="/train/resource/course.do?b_query=link&a_code="+itemid;
			var imgurl='/images/icon_wsx.gif';
			var xml="/train/resource/course/get_code_tree.jsp?codesetid=55&codeitemid="+itemid;
			parent.frames['mil_menu'].add(currnode,itemid,codeitemdesc,action,"mil_body",codeitemdesc,imgurl,xml);
		}
	}
	var a_code = $F("a_code1");
	courseForm.action = "/train/resource/course.do?b_save=link&a_code="+a_code+"&itemid="+itemid;
	courseForm.submit();
}
//获取分类树上对应的节点
function getTreeItem(itemid, currnode) {
	if(currnode) {
		if(itemid==currnode.uid)
			return currnode;
		else if(currnode.load){
			var childNode;
			for (var i = 0; i < currnode.childNodes.length; i++) {
				childNode = currnode.childNodes[i];
				if(childNode.uid == "Loading..."){
					childNode = undefined;
					continue;
				}
				
				childNode = getTreeItem(itemid, childNode);
				if(childNode)
					break;
			}
			
			if(childNode)
				return childNode;

		} else if(!currnode.load)
			return false;
	}
}

//添加培训课程记录
function add() {
	var a_code = $F("a_code");
	a_code=a_code=="root"?"":a_code;
	var hashvo=new ParameterSet();
	hashvo.setValue("codeitemid",a_code);
	var request=new Request({method:'post',asynchronous:true,onSuccess:adds,functionId:'202003005102'},hashvo);
}
function adds(outparamters) {
	var a_code = $F("a_code");
	var flag = outparamters.getValue("flag");
	if(flag=="true"){
	courseForm.action = "/train/resource/course.do?b_add=link&a_code=" + a_code;
	courseForm.submit();
	}else{
		alert(TRAINS_LESSON_CODE);
	}
}

//返回
function returnback() {
	var a_code = $F("a_code1");
	courseForm.action = "/train/resource/course.do?b_query=back&a_code=" + a_code;
	courseForm.submit();
}
//返回
function returnback1() {
	var a_code = $F("a_code1");
	coursewareForm.action = "/train/resource/course.do?b_query=link&a_code=" + a_code;
	coursewareForm.submit();
}

function returnFirst(){
   	self.parent.location= "/general/tipwizard/tipwizard.do?br_train=link";
}

//显示查处窗体
function searchInform() {
	var thecodeurl = "/train/resource/course.do?b_select=link&tablename=r50";
	var return_vo = window.showModalDialog(thecodeurl, "", "dialogWidth:700px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");
	if (return_vo != null) {
		var a_code = document.getElementsByName("a_code")[0].value;
		document.getElementsByName("searchstr")[0].value = return_vo;
		courseForm.action = "/train/resource/course.do?b_query=link&a_code=" + a_code;
		courseForm.submit();
	}
}

//显示排序窗体
function sortRecord() {
	var a_code = document.getElementsByName("a_code")[0].value;
	var thecodeurl = "/train/resource/course.do?b_sort=link&tablename=r50&a_code=" + a_code;
	var return_vo = window.showModalDialog(thecodeurl, "", "dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
	if (return_vo != null) {
		courseForm.action = "/train/resource/course.do?b_query=link&a_code=" + a_code;
		courseForm.submit();
	}
}

//添加培训课程课件
function addcourseware() {
	var a_code = document.getElementsByName("a_code")[0].value;
	var r5000 = document.getElementsByName("r5000")[0].value;
	var thecodeurl = "/train/resource/courseware.do?b_add=link&a_code=" + a_code + "&id=" + r5000;
	coursewareForm.action = thecodeurl;
	coursewareForm.submit();
}
//从课件添加页面返回到课件列表 linbz 6813 返回增加newPath参数
function returnfromaddcourseware() {
  var a_code = document.getElementsByName("a_code")[0].value;
  var r5000 = document.getElementsByName("r5000")[0].value;
  var newPath = document.getElementById("newPathId").value;
  if(newPath.length>0 || newPath!=""){
	  if(confirm(TRAIN_COURSEWARE_TIPS)){
		//保存操作
		savecourseware0();
		return;
	  }
  } 
  coursewareForm.action = "/train/resource/courseware.do?b_query=link&id=" + r5000 + "&a_code=" + a_code + "&newPath=" + newPath;
  coursewareForm.submit();
}
//保存添加培训课程课件
function savecourseware() {
	  var a_code = document.getElementsByName("a_code")[0].value;
	  var r5000 = document.getElementsByName("r5000")[0].value;
	  var r5100 = document.getElementsByName("r5100")[0].value;
	  var filepath = $F("filepath");
	  // 陈旭光修改：增加课件时判断该课程下是否有学员 并提示是否让学员学习的新课件
	  if(r5100 == null || r5100 == ""){
	  var hashvo = new ParameterSet();
	  hashvo.setValue("id",r5000);
	  var request=new Request({method:'post',asynchronous:false,onSuccess:isSearch,functionId:'202003006101'},hashvo);
	  function isSearch(outparamters){
			if(outparamters){
			   var temp=outparamters.getValue("check")
			   if("yes" == temp){
				   if(confirm(TRAIN_COURSE_ADD)){
					   coursewareForm.action = "/train/resource/courseware.do?b_save=link&id=" + r5000 + "&a_code=" + a_code + "&filepath=" + filepath+"&slect=1";
				   }else{
					   coursewareForm.action = "/train/resource/courseware.do?b_save=link&id=" + r5000 + "&a_code=" + a_code + "&filepath=" + filepath;
				   }
			   }
				else if(!temp || "no" == temp)
					coursewareForm.action = "/train/resource/courseware.do?b_save=link&id=" + r5000 + "&a_code=" + a_code + "&filepath=" + filepath;
		    	else
		    		alert(temp);
		   	}
		} 
	  }else{
		  coursewareForm.action = "/train/resource/courseware.do?b_save=link&id=" + r5000 + "&a_code=" + a_code + "&filepath=" + filepath;
	  }
		coursewareForm.submit();
	}


function savecourseware1() {
  var courseName = document.getElementById("r5103name").value;//课程名称
  courseName = getEncodeStr(courseName);
  var filepath = $F("filepath");
  var courseDesc = document.getElementById("courseDesc").value; //得到课程简介
  courseDesc = getEncodeStr(courseDesc);
  var courseType = document.getElementById("courseType").value; //课程分类

  var pathName = document.getElementById("path_name").value;
  var pathOld = document.getElementById("path_old").value;
  var newPath = document.getElementById("newPathId").value;
  var hashvo = new ParameterSet();
	hashvo.setValue("courseName",courseName);
	hashvo.setValue("courseDesc",courseDesc);
	hashvo.setValue("filepath",filepath);
	hashvo.setValue("courseType",courseType);
	hashvo.setValue("pathName",pathName);
	hashvo.setValue("path_old",pathOld);
	hashvo.setValue("newPath",newPath);
	

  if(courseType == "2"){   //如果课程分类为文本课件  
	  var textName = document.getElementById("textName").value; //课件名称
	  textName=getEncodeStr(textName);
	  var text = FCKeditorAPI.GetInstance('text');
	  var textContent = text.EditorDocument.body.innerText; //则 文本课件内容
	  textContent=getEncodeStr(textContent);
	  hashvo.setValue("textName",textName);
	  hashvo.setValue("textContent",textContent);
  }
  
  var request=new Request({method:'post',onSuccess:submitSucce,functionId:'2020030098'},hashvo);
}

function submitSucce(outparamters) {
	var flag = outparamters.getValue("flag");
	if("yes" == flag){
		document.forms[0].action =  "/train/resource/myupload/myuploadcourse.do?b_query=link&init=1";
		document.forms[0].submit();
	} else 
		alert("上传课程失败！");
}
//输入浮点数值型
function IsDigit(obj) {
	if ((event.keyCode >= 46) && (event.keyCode <= 57) && event.keyCode != 47) {
		var values = obj.value;
		if ((event.keyCode == 46) && (values.indexOf(".") != -1)) {//有两个.
			return false;
		}
		if ((event.keyCode == 46) && (values.length == 0)) {//首位是.
			return false;
		}
		return true;
	}
	return false;
}
	//输入整数
function IsDigit2(obj) {
	if ((event.keyCode > 47) && (event.keyCode <= 57)) {
		return true;
	} else {
		return false;
	}
}
function isNumber(obj) {
	var checkOK = "-0123456789.";
	var checkStr = obj.value;
	var allValid = true;
	var decPoints = 0;
	var allNum = "";
	if (checkStr == "") {
		return;
	}
	var count = 0;
	var theIndex = 0;
	for (i = 0; i < checkStr.length; i++) {
		ch = checkStr.charAt(i);
		if (ch == "-") {
			count = count + 1;
			theIndex = i + 1;
		}
		for (j = 0; j < checkOK.length; j++) {
			if (ch == checkOK.charAt(j)) {
				break;
			}
		}
		if (j == checkOK.length) {
			allValid = false;
			break;
		}
		if (ch == ".") {
			allNum += ".";
			decPoints++;
		} else {
			if (ch != ",") {
				allNum += ch;
			}
		}
	}
	if (count > 1 || (count == 1 && theIndex > 1)) {
		allValid = false;
	}
	if (decPoints > 1 || !allValid) {
		alert("\u8bf7\u8f93\u5165\u6570\u503c\u7c7b\u578b\u7684\u503c\uff01");
		obj.value = "";
		obj.focus();
	}
}
/**
 * 过滤特殊字符
 * @param value
 * @return
 */
function replace_code(value){
	//过滤中文半角特殊字符
	value=value.replace("！","").replace("·","").replace("@","").replace("#","").replace("￥","");
	value=value.replace("%","").replace("……","").replace("&","").replace("*","").replace("（","");
	value=value.replace("）","").replace("——","").replace("-","").replace("+","").replace("=","");
	value=value.replace("|","").replace("｝","").replace("｛","").replace("【","").replace("】","");
	value=value.replace("”","").replace("“","").replace("’","").replace("‘","").replace("、","");
	value=value.replace("；","").replace("：","").replace("？","").replace("、","").replace("》","");
	value=value.replace("…","").replace("~","").replace("。","").replace("，","").replace("《","");
	//过滤中文全角特殊字符
	value=value.replace("～","").replace("·","").replace("！","").replace("＠","").replace("＃","");
	value=value.replace("×","").replace("＆","").replace("……","").replace("％","").replace("￥","");
	value=value.replace("（","").replace("）","").replace("——","").replace("－","").replace("＋","");
	value=value.replace("】","").replace("【","").replace("＼","").replace("｜","").replace("＝","");
	value=value.replace("｛","").replace("｝","").replace("：","").replace("；","").replace("“","");
	value=value.replace("、","").replace("？","").replace("’","").replace("‘","").replace("”","");
	value=value.replace("》","").replace("《","").replace("，","").replace("。","").replace("…","");
	//过滤英文全角特殊字符
	value=value.replace("￣","").replace("｀","").replace("！","").replace("＠","").replace("#","");
	value=value.replace("＊","").replace("＆","").replace("＾","").replace("％","").replace("＄","");
	value=value.replace("（","").replace("）","").replace("＿","").replace("－","").replace("＝","");
	value=value.replace("［","").replace("］","").replace("｜","").replace("＼","").replace("＋","");
	value=value.replace("{","").replace("}","").replace("＂","").replace("＂","").replace("＇","");
	value=value.replace("／","").replace("？","").replace("；","").replace("：","").replace("＇","");
	value=value.replace("．","").replace("，","").replace("＞","").replace("＜","").replace("—","");
	
	return value;
}

