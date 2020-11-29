 //String.fromCharCode(32);
//"a".charCodeAt(0)
 
 function Question() {
 	
 }
 
 // 选项最小值，默认为空，即没有选项
 Question.prototype.minValue = "";
 // 选项最大值，默认为空，即没有选项
 Question.prototype.maxValue = "";
 // 选项当前值，默认为空，即没有选项
 Question.prototype.currValue = "";
 // 选项类型，1为单选，2为多选，默认单选
 Question.prototype.type = 1;
 
 // 根据id获得对象
 Question.prototype.getObj = function (objId) {
 	return document.getElementById(objId);
 }
 
 // 隐藏对象
 Question.prototype.displayObj = function (objId) {
 	var obj = document.getElementById(objId);
 	if (obj) {
 		obj.style.display = "none";
 	}
 }
 
 // 显示对象
 Question.prototype.showObj = function (objId) {
 	var obj = document.getElementById(objId);
 	if (obj) {
 		obj.style.display = "";
 	}
 };
 
 // 隐藏选择项
 Question.prototype.displaySelectionTr = function () {
 	this.displayObj("trSelection");
 };
 
 // 显示选择项
 Question.prototype.showSelectionTr = function () {
 	this.showObj("trSelection");
 };
 
 Question.prototype.saveSelection = function (content) {
 	var currS = this.getObj(this.currValue + "span");
 	if (currS && content && content.length>0) {
 		currS.innerHTML = content;
 	} 
 };
 
 Question.prototype.addSelection = function (content) {
 	// 将先前的内容保存
 	
 	this.saveSelection(content);
 	var parentDiv = this.getObj("questionDiv");
 	var objective = this.getObj("objective");
 	if (this.type == 1) { // 单选题
		if (this.minValue == "") {
			parentDiv.innerHTML = "<div id='Adiv' class='divtableA common_border_color' onclick='changedivcolor(\"A\")'><img src='/images/del.gif' border='0' alt='"+STATUS_DELETE+"' onclick='delSelection(\"A\")' id='Aimg' style='cursor:pointer;' align='absmiddle'/>&nbsp;<input type='radio' name='selectionradio' id='Aradio' onclick='selectRadio(\"A\")' value='A'/><label for='Aradio'>A</label>&nbsp;<span id='Aspan'></span></div>";
			objective.innerHTML = "<div id='Aanswer' style='width:50px;float:left;'><input type='radio' name='questionAnswerl' value='A' id='AquestionAnswer'/>&nbsp;<label for='AquestionAnswer'>A</label></div>";	
			this.minValue = "A";
			this.currValue = "A";
			this.maxValue = "A";		
		} else {
			// 获得下一个选项
			if(this.maxValue<"Z")
			{
				var va = String.fromCharCode(this.maxValue.charCodeAt(0) + 1);
				parentDiv.innerHTML += "<div id='"+va+"div' class='divtable common_border_color' onclick='changedivcolor(\""+va+"\")'><img src='/images/del.gif' border='0' alt='"+STATUS_DELETE+"'  onclick='delSelection(\""+va+"\")' id='"+va+"img' style='cursor:pointer;' align='absmiddle'/>&nbsp;<input type='radio' name='selectionradio' id='"+va+"radio' onclick='selectRadio(\""+va+"\")' value='"+va+"'/><label for='"+va+"radio'>"+va+"</label>&nbsp;<span id='"+va+"span'></span></div>";
				objective.innerHTML += "<div id='"+va+"answer' style='width:50px;float:left;'><input type='radio' name='questionAnswerl' value='"+va+"' id='"+va+"questionAnswer'/>&nbsp;<label for='"+va+"questionAnswer'>"+va+"</label></div>";	
				this.currValue = va;
				this.maxValue = va;		
			}
			else
			  alert(MAX_QUESTION_ITEM);
		}
 		
 	} else if (this.type == 2) { // 多选题
 		if (this.minValue == "") {
			parentDiv.innerHTML = "<div id='Adiv' class='divtableA common_border_color' onclick='changedivcolor(\"A\")'><img src='/images/del.gif' border='0' alt='"+STATUS_DELETE+"'  onclick='delSelection(\"A\")' id='Aimg' style='cursor:pointer;' align='absmiddle'/>&nbsp;<input type='radio' name='selectionradio' id='Aradio' onclick='selectRadio(\"A\")' value='A'/><label for='Aradio'>A</label>&nbsp;<span id='Aspan'></span></div>";
			objective.innerHTML = "<div id='Aanswer' style='width:50px;float:left;'><input type='checkbox' name='questionAnswerl' value='A' id='AquestionAnswer'/>&nbsp;<label for='AquestionAnswer'>A</label></div>";	
			this.minValue = "A";
			this.currValue = "A";
			this.maxValue = "A";		
		} else {
			// 获得下一个选项
			if(this.maxValue<"Z")
			{
				var va = String.fromCharCode(this.maxValue.charCodeAt(0) + 1);
				
				parentDiv.innerHTML += "<div id='"+va+"div' class='divtable common_border_color' onclick='changedivcolor(\""+va+"\")'><img src='/images/del.gif' border='0' alt='"+STATUS_DELETE+"'  onclick='delSelection(\""+va+"\")' id='"+va+"img' style='cursor:pointer;' align='absmiddle'/>&nbsp;<input type='radio' name='selectionradio' id='"+va+"radio' onclick='selectRadio(\""+va+"\")' value='"+va+"'/><label for='Aradio'>"+va+"</label>&nbsp;<span id='"+va+"span'></span></div>";
				objective.innerHTML += "<div id='"+va+"answer' style='width:50px;float:left;'><input type='checkbox' name='questionAnswerl' value='"+va+"' id='"+va+"questionAnswer'/>&nbsp;<label for='"+va+"questionAnswer'>"+va+"</label></div>";	
				this.currValue = va;
				this.maxValue = va;
			}
            else
                alert(MAX_QUESTION_ITEM);		
		}
 	}
 };
 
 Question.prototype.insertSelection = function (content){
	 this.saveSelection(content);
	 	var parentDiv = this.getObj("questionDiv");
	 	var objective = this.getObj("objective");
	 	var radios = parentDiv.getElementsByTagName("input");
	 	
	 	if(!this.maxValue<"Z"){
	 		alert(MAX_QUESTION_ITEM);	
	 		return;
	 	}
	 	
	 	var selectedRadio = null; 
	 	
	 	var index=0;
	 	for(var i=0;i<radios.length;i++){
	 		if(radios[i].checked == true){
	 			selectedRadio = radios[i];
	 			index = i;
	 		}
	 	}
	 	
	 	//没选择任何选项或没有选项时 返回add直接添加
	 	if(selectedRadio == null)
	 		return "add";
	 	
	 	var divlist = parentDiv.getElementsByTagName("div");
	 	var divInnerHtml = "";
	 	var  va = "";
	 	var flag = false;
	 	var div;
	 	var va;
	 	var spantext;
	 	
	 	//重新加载questionDiv
	 	for(var k=0;k<divlist.length;k++){
	 		div = divlist[k];
	 		va = div.id.substr(0,1);
	 		spantext = div.getElementsByTagName("span")[0].innerHTML;
	 		
	 		//插入新选项
	 		if(index == k){
	 			flag = true;
	 			divInnerHtml += "<div id='"+va+"div' class='divtable common_border_color' onclick='changedivcolor(\""+va+"\")'><img src='/images/del.gif' border='0' alt='"+STATUS_DELETE+"'  onclick='delSelection(\""+va+"\")' id='"+va+"img' style='cursor:pointer;' align='absmiddle'/>&nbsp;<input type='radio' name='selectionradio' id='"+va+"radio' onclick='selectRadio(\""+va+"\")' value='"+va+"'/><label for='"+va+"radio'>"+va+"</label>&nbsp;<span id='"+va+"span'></span></div>";
	 		}
	 		
	 		//如果已插入新选项所有id+1
	 		if(flag){
	 			va = String.fromCharCode(va.charCodeAt(0) + 1);
	 		}
	 		divInnerHtml += "<div id='"+va+"div' class='divtable common_border_color' onclick='changedivcolor(\""+va+"\")'><img src='/images/del.gif' border='0' alt='"+STATUS_DELETE+"'  onclick='delSelection(\""+va+"\")' id='"+va+"img' style='cursor:pointer;' align='absmiddle'/>&nbsp;<input type='radio' name='selectionradio' id='"+va+"radio' onclick='selectRadio(\""+va+"\")' value='"+va+"'/><label for='"+va+"radio'>"+va+"</label>&nbsp;<span id='"+va+"span'>"+spantext+"</span></div>";
	 		
	 	}
	 	
	 	parentDiv.innerHTML = divInnerHtml;
	 	
	 	//插入完毕，添加一个答案选择项
	 	va = String.fromCharCode(this.maxValue.charCodeAt(0) + 1);
	 	if (this.type == 1) 
	 		objective.innerHTML += "<div id='"+va+"answer' style='width:50px;float:left;'><input type='radio' name='questionAnswerl' value='"+va+"' id='"+va+"questionAnswer'/>&nbsp;<label for='"+va+"questionAnswer'>"+va+"</label></div>";
	 	else
	 		objective.innerHTML += "<div id='"+va+"answer' style='width:50px;float:left;'><input type='checkbox' name='questionAnswerl' value='"+va+"' id='"+va+"questionAnswer'/>&nbsp;<label for='"+va+"questionAnswer'>"+va+"</label></div>";	
	 	
	 	this.currValue = va;
		this.maxValue = va;

		var answerlist = objective.getElementsByTagName("input");
		
		var checkid = new Array();
		for(index;index<answerlist.length;index++){
			inputObj = answerlist[index];
			if(inputObj.checked == true || inputObj.selected == true){
				inputObj.checked = false;
				inputObj.selected = false;
				checkid.push(index+1);
			}
				
		}
		
		for(var i=0;i<checkid.length;i++){
			answerlist[checkid[i]].checked =true;
			answerlist[checkid[i]].selected =true;
			
		}
 };

 
function delSelection(id) {

	
 	// 将标志改回
 	var obj = question.getObj(id + "radio");
 	
 	if (obj) {
 		if (obj.checked == true) {
 			// 将选择项前移一个，如果前一个没有就向后移动一个
 			var qian = String.fromCharCode(id.charCodeAt(0) - 1);
 			var qianObj = question.getObj( qian + "radio")
 			if (qianObj) {
 				selectRadio(qian);
 			} else {
 				var next = String.fromCharCode(id.charCodeAt(0) + 1);
 				var nextObj = question.getObj( next + "radio");
 				if (nextObj) {
 					selectRadio(next);
 				} 
 			}
 			
 		} 
 		
 		var parentDiv = question.getObj("questionDiv")
 		var objective = question.getObj("objective");
 		for (var i = id.charCodeAt(0) ; i < question.maxValue.charCodeAt(0); i++) {
 			var cuValue = String.fromCharCode(i+1);
 			var cuSpan = question.getObj(cuValue + "span");
 			var va = String.fromCharCode(i)
 			
 			
 			
 			if (i ==  id.charCodeAt(0)) {
 				var divObj = question.getObj(va + "div");
 				divObj.parentNode.removeChild(divObj);
 				var answerObj = question.getObj(va + "answer");
 				answerObj.parentNode.removeChild(answerObj);
 			}
 			// 隐藏
 			var divObj = question.getObj(cuValue + "div");
 			divObj.parentNode.removeChild(divObj);
 			//question.getObj(cuValue + "div").style.display = "none";
 			var answerObj = question.getObj(cuValue + "answer");
 			//question.getObj(cuValue + "answer").style.display = "none";
 			answerObj.parentNode.removeChild(answerObj);
 			
 			
	 			if (question.type == 1) {
	 				if ("A" == va) {
	 					parentDiv.innerHTML += "<div id='"+va+"div' class='divtableA common_border_color' onclick='changedivcolor(\""+va+"\")'><img src='/images/del.gif' border='0' alt='"+STATUS_DELETE+"'  onclick='delSelection(\""+va+"\")' id='"+va+"img' style='cursor:pointer;' align='absmiddle'/>&nbsp;<input type='radio' name='selectionradio' id='"+va+"radio' onclick='selectRadio(\""+va+"\")'  value='"+va+"'/><label for='"+va+"radio'>"+va+"、</label>&nbsp;<span id='"+va+"span' style='width:100%'>"+cuSpan.innerHTML+"</span></div>";
	 				} else {
	 					parentDiv.innerHTML += "<div id='"+va+"div' class='divtable common_border_color'  onclick='changedivcolor(\""+va+"\")'><img src='/images/del.gif' border='0' alt='"+STATUS_DELETE+"'  onclick='delSelection(\""+va+"\")' id='"+va+"img' style='cursor:pointer;' align='absmiddle'/>&nbsp;<input type='radio' name='selectionradio' id='"+va+"radio' onclick='selectRadio(\""+va+"\")'  value='"+va+"'/><label for='"+va+"radio'>"+va+"、</label>&nbsp;<span id='"+va+"span' style='width:100%'>"+cuSpan.innerHTML+"</span></div>";
	 				}
					objective.innerHTML += "<div id='"+va+"answer' style='width:50px;float:left;'><input type='radio' name='questionAnswerl' value='"+va+"' id='"+va+"questionAnswer'/>&nbsp;<label for='"+va+"questionAnswer'>"+va+"</label></div>";
	 			} else if (question.type == 2) {
	 				if ("A" == va) {
	 					parentDiv.innerHTML += "<div id='"+va+"div' class='divtableA common_border_color'  onclick='changedivcolor(\""+va+"\");'><img src='/images/del.gif' alt='"+STATUS_DELETE+"' border='0' onclick='delSelection(\""+va+"\")' id='"+va+"img' style='cursor:pointer;' align='absmiddle'/>&nbsp;<input type='radio' name='"+va+"radio' id='"+va+"radio' onclick='selectRadio(\""+va+"\")' value='"+va+"'/><label for='"+va+"radio'>"+va+"、</label>&nbsp;<span id='"+va+"span'  style='width:100%'>"+cuSpan.innerHTML+"</span></div>";
	 				} else {
	 					parentDiv.innerHTML += "<div id='"+va+"div' class='divtable common_border_color'  onclick='changedivcolor(\""+va+"\")'><img src='/images/del.gif' alt='"+STATUS_DELETE+"'  border='0' onclick='delSelection(\""+va+"\")' id='"+va+"img' style='cursor:pointer;' align='absmiddle'/>&nbsp;<input type='radio' name='"+va+"radio' id='"+va+"radio' onclick='selectRadio(\""+va+"\")' value='"+va+"'/><label for='"+va+"radio'>"+va+"、</label>&nbsp;<span id='"+va+"span'  style='width:100%'>"+cuSpan.innerHTML+"</span></div>";
	 				}
	 				
					objective.innerHTML += "<div id='"+va+"answer' style='width:50px;float:left;'><input type='checkbox' name='questionAnswerl' value='"+va+"' id='"+va+"questionAnswer'/>&nbsp;<label for='"+va+"questionAnswer'>"+va+"、</label></div>";	
	 			}

 			
 			/**if ("A"== question.maxValue  && i == "A".charCodeAt(0) ) {
 				question.minValue = "";
 				question.currValue = "";
 				question.maxValue = "";
 			} **/
 			
 			/**if (i == question.maxValue.charCodeAt(0) && i != "A".charCodeAt(0) ) {
 				question.maxValue = va;
 			}**/
 		}
 		
 		if (id == question.maxValue) {
				// 隐藏选项
				var delObj = question.getObj(id + "div");
				delObj.parentNode.removeChild(delObj);
			 	//question.getObj(id + "div").style.display = 'none';
			 	// 隐藏答案
			 	var delAnswer = question.getObj(id + "answer");
			 	delAnswer.parentNode.removeChild(delAnswer);
			 	//question.getObj(id + "answer").style.display = 'none';
			}
 		
 			if ("A"== id && question.maxValue == question.minValue) {
	 				question.minValue = "";
	 				question.currValue = "";
	 				question.maxValue = "";
 			} else {
 				if (question.currValue ==question.maxValue ) {
 					question.currValue = String.fromCharCode(question.maxValue.charCodeAt(0) - 1);
 				}
 				question.maxValue = String.fromCharCode(question.maxValue.charCodeAt(0) - 1);
 				
 			}
 		
 	}
 	
}

function selectRadio(id) {
	// 选中改选项
	question.getObj(id + "radio").checked = "true";
	// 设置当前值
	question.currValue = id;
	// 修改fck的值
	var oEditor = FCKeditorAPI.GetInstance('selectionView');	
	oEditor.SetHTML(question.getObj(id + "span").innerHTML,true);
}


 