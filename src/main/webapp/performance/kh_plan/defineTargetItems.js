
var o1, o2,aa;
var o1a = new Array();
var o2a = new Array();
var v_tr = null;

function SetRow(dir) {
	var o;
	var start = false;
	var num = 0;
	var v_radio = document.getElementById("targetCalcTable").getElementsByTagName("tr");
	
	if(v_radio.length>0)
	{
		o = v_radio[aa];
		if(o!=undefined)
		{
			start = true;
			num = v_radio[aa].rowIndex;
		}
	}
	
	if (!start) {
		alert("请点击选择一个需要移动的指标行！");
		return;
	}
	if (num <1 && dir == "up") {
		alert("已经无法再向上移动！");
		return;
	}
	if (num >= (v_radio.length - 1) && dir == "down") {
		alert("已经无法再向下移动！");
		return;
	}
	var p = o;
	var trs = document.getElementById("targetCalcTable").getElementsByTagName("tr");
	o1 = trs[p.rowIndex];
	var tdLen = trs[p.rowIndex].cells.length;
	o1a.length = tdLen;
	o2a.length = tdLen;
	for (var i = 0; i < tdLen; i++) {
		o1a[i] = trs[p.rowIndex].cells[i].innerHTML;
		if (dir == "down") {
			o2a[i] = trs[p.rowIndex + 1].cells[i].innerHTML;
		} else {
			o2a[i] = trs[p.rowIndex - 1].cells[i].innerHTML;
		}
	}
	if (dir == "down") {
		o2 = trs[p.rowIndex + 1];
	} else {
		o2 = trs[p.rowIndex - 1];
	}
	switchTd(o1, o2);
} 
function switchTd(tr1, tr2) {
	for (x = 0; x < o1a.length; x++) {
		tr1.cells[x].innerHTML = o2a[x];
		tr2.cells[x].innerHTML = o1a[x];
	}
	_toObj=tr2.cells[0];
	clickMouse(); 
}  

 
function clickMouse() {
	var obj=_toObj.parentElement;
	aa=obj.rowIndex;   
	obj.bgColor = "#fff8d2";
	if(v_tr!=null&&v_tr!=obj){
          v_tr.bgColor="";          
        }
    v_tr = obj;
}

function clickMouse2() {
	var obj=window.event.srcElement.parentElement;
	aa=obj.rowIndex;   
	obj.bgColor = "#fff8d2";
	if(v_tr!=null&&v_tr!=obj){
          v_tr.bgColor="";          
        }
    v_tr = obj;
}
