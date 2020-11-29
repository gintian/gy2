var activeobj="";
var deleteSubView;
var validateFlag=true;
var activeobjTable="";
/*普通指标处理onclick事件**/
function processClick(obj)
{
	activeobj =obj.id;
	activeobjTable="";
	var fieldType=obj.getAttribute("fieldtype");
	///alert("fieldType is: "+fieldType);
	switch(fieldType)
	{
		case "string":///字符串型指标，如姓名
			var codesetid = obj.getAttribute("codesetid");//代码类名称
			var desc = obj.getAttribute("desc");//代码值
			var objid =obj.id;
			if(codesetid){
				if(validateFlag){
					
					var ctrltype='0'; //机构编码是否权限控制 0不控制,1按管理范围控制,2走userView.getUnitIdByBusi("4")机构业务操作单位——》操作单位——》管理范围 ，3其它业务范围,来自busicode 20170605 dengcan
					var busicode='';
					if(typeof(obj.getAttribute("ctrltype"))!='undefined'&&obj.getAttribute("ctrltype")=='3')
					{
						ctrltype='3';
						busicode='8';//人事异动业务范围
					}
					if(window.Android!=null){
						window.Android.showDialog(objid,codesetid,desc,ctrltype,busicode);//返回值是一个数组 第一个值是codevalue 第二个值是codeddesc
					}else{
						var parameters = objid+","+codesetid+","+desc+","+ctrltype+","+busicode;
						//parameters =escape(parameters).replace(/%u/gi,'\\u'); IOS SDK 8 不用转码了，汉子
						document.location="objc::showDialog::"+parameters; 
					}
				}
				
			}else{
				obj.focus();
//						obj.select();//选中里面的文本 该方法在webview中不会显示被选中的状态 ，导致输入时会覆盖原有的信息；
				break;
			}
		case "float"://数值型指标
			break;
		case "int":
			break;
		case "memory"://备注型指标
			break;
		case "date"://日期型指标
			var tempdate=obj.getAttribute("keyvalue");
			if(!tempdate){
				tempdate="";
			}
			if(validateFlag){
				if(window.Android!=null)
					window.Android.getDateForWeb(activeobj,tempdate,"true");
				else{
					var parameters = activeobj+","+tempdate+","+"true";
					// parameters =escape(parameters).toLocaleLowerCase().replace(/%u/gi,'\\u');
					document.location="objc::getDateForWeb::"+parameters; 
				}
					
			}
			break;
		default:
			break;
	}
	
}


/*普通指标处理onblur事件**/
function processBlur(obj)
{
	function checkIsNum(value)//验证是否是数字
	{
		return /^-?\d+(\.\d+)?$/.test(value);
	}
	var codesetid = obj.getAttribute("codesetid");//代码类ID
	var goon=1;
	var desc = obj.getAttribute("desc");
	var fieldType=obj.getAttribute("fieldtype");
	///alert("fieldType is: "+fieldType);
	switch(fieldType){
		case "string":///字符串型指标，如姓名
			
			if(!codesetid){//如果不是代码类的字符型,那么keyvalue要和value保持一直，而原有的oldvalue值不变，方便后面的比较
				var tempvalue=obj.value;
				var tempindex=tempvalue.indexOf(",");
				if(tempvalue.indexOf(",")>0){
					tempvalue=replaceAll(tempvalue,",", "`g`g" );
				}
				if(tempvalue.indexOf("，")>0){
					tempvalue=replaceAll(tempvalue,"，", "`g`g" );
				}
				obj.setAttribute("keyValue",tempvalue);
			}
			break;
		case "memory"://备注型指标
			obj.setAttribute("keyValue",obj.value);
			break;
		case "float"://数值型指标
			if((obj.value.length>0)&&!checkIsNum(obj.value)){//处理将数字型为空的除外
				validateFlag=false;
				if(window.Android!=null){
					window.Android.showToast("请输入数字");
				}else{
					var parameters = "请输入数字";
					// parameters =escape(parameters).toLocaleLowerCase().replace(/%u/gi,'\\u');
					document.location="objc::showToast::"+parameters; 
				}
				obj.value="";
				obj.setAttribute("oldValue",obj.value);
				obj.setAttribute("keyValue",obj.value);///value,keyValue,oldValue都一样。
				window.setTimeout( function(){ obj.focus(); }, 0);//然后把该文本域聚焦
				goon=0;
			}else{
				var length=obj.getAttribute("format");
				if(obj.value.length>0){
					obj.value=Digit.round(obj.value, length)
				}
				obj.setAttribute("value", obj.value);
				obj.setAttribute("keyValue",obj.value);
			}
			break;
		case "int"://数值型指标
			if((obj.value.length>0)&&!checkIsNum(obj.value)){
				validateFlag=false;
				if(window.Android!=null){
					window.Android.showToast("请输入数字");
				}else{
					var parameters = "请输入数字";
					// parameters =escape(parameters).toLocaleLowerCase().replace(/%u/gi,'\\u');
					document.location="objc::showToast::"+parameters; 
				}
				obj.value="";
				obj.setAttribute("oldValue",obj.value);
				obj.setAttribute("keyValue",obj.value);///value,keyValue,oldValue都一样。
				window.setTimeout( function(){ obj.focus(); }, 0);//然后把该文本域聚焦
				goon=0;
			}
			if((obj.value.length>0)&&checkIsNum(obj.value)){
				if(isIntOrNull(obj.value)){
					obj.setAttribute("keyValue",obj.value);
				}
				if(!isIntOrNull(obj.value)){
					validateFlag=false;
					if(window.Android!=null){
						window.Android.showToast(desc+"只能是整数");
					}else{
						var parameters = desc+"只能是整数";
						// parameters =escape(parameters).toLocaleLowerCase().replace(/%u/gi,'\\u');
						document.location="objc::showToast::"+parameters; 
					}
					obj.value="";
					obj.setAttribute("oldValue",obj.value);
					obj.setAttribute("keyValue",obj.value);///value,keyValue,oldValue都一样。
					window.setTimeout( function(){ obj.focus(); }, 0);//然后把该文本域聚焦
					goon=0;
				}
			}
			if(obj.value.length==0){
				obj.setAttribute("value", obj.value);
				obj.setAttribute("keyValue",obj.value);
			}
			break;
		case "date":///日期型指标
			obj.setAttribute("keyValue",obj.value);
		break;
	}
	if(goon==1)
	{
		if(obj.getAttribute("keyValue")!=obj.getAttribute("oldValue"))///如果值被修改过了 keyValue 是用来做比较判断的如果keyValue 与oldValue 不一致那么说明修改了
		{
			obj.setAttribute("oldValue",obj.getAttribute("keyValue"));//修改了以后就要把oldValue和keyValue 修改成一致的
			var dataset=getDataset();
			if(dataset)
			{
				var current=dataset.record;
				if(current)
				{
					if(!codesetid){
						current.setValue(obj.getAttribute("field"),obj.getAttribute("keyValue"));
					}else{
						current.setValue(obj.getAttribute("field"),obj.getAttribute("codeValue"));
					}
					
				}
				var name=obj.id;
				if(name&&(name=="codeitemdesc_2"||name=="a0101_2")){
					
					if(window.Android!=null){
						window.Android.setName(obj.value);
					}else{
						
					}
				}
			}
			
		}
	}
	
}
var subsetInitArray=new Array();///判断子集是否已经初始化了   格式  subsetInitArray["t_a04_2"]=1

/*判断该子集是否已经初始化**/
function isSubsetInit(subsetname)
{
	if(typeof(subsetInitArray[subsetname])=="undefined")
	{
		return false;
	}
	else if(subsetInitArray[subsetname]==1)
	{
		return true;
	}
	return false;
}

//var subviewArray = new Array();///存储所有子集的subview  格式：subviewArray["t_a04_2"]=object  xcs 2013-11-13 已经将其作为dataset的属性

/*根据子集的名称得到子集的对象**/
function getSubView(subsetname)
{
	var dataset = getDataset();
	var subviewArray=dataset.subviewArray;
	if(subviewArray[subsetname]==null)//如果子集没有被初始化
	{
		return null;
	}
	return subviewArray[subsetname];
}

/*如果没有初始化，那么就初始化该子集   element**/
function initSubSet(subsetname)
{
	try
	{
		if(isSubsetInit(subsetname))//如果已经初始化了
		{
			return;
		}
		var subsettablename=subsetname+"_table";
		var element=document.getElementById(subsettablename);
		var field_name=element.getAttribute("field");// 例子中 t_a04_2 
		if(field_name.indexOf("t_")==0 && field_name.indexOf("_2")==(field_name.length-2))//如果是子集，并且是变化后的
		{
			var dataset=getDataset();
			var record=dataset.record;
			if(record)
			{
				var value=record.getValue(field_name);
				showSubDomainView(element,value);
			}
			else
			{
				validateFlag=false;
				
				if(window.Android!=null){
					window.Android.showToast(CANNOT_FIND_RECORD_FROM_DATASET);
				}else{
					var parameters = CANNOT_FIND_RECORD_FROM_DATASET;
					// parameters =escape(parameters).toLocaleLowerCase().replace(/%u/gi,'\\u');
					document.location="objc::showToast::"+parameters; 
				}
			}	
		}
	}
	catch (e)
	{
		validateFlag=false;
		
		if(window.Android!=null){
			window.Android.showToast(e.message);
		}else{
			var parameters = e.message;
			// parameters =escape(parameters).toLocaleLowerCase().replace(/%u/gi,'\\u');
			document.location="objc::showToast::"+parameters; 
		}
		//alert(e.message);
	}
	
}

/*准备初始化子集**/
function showSubDomainView(element,xmlcontent)
{
    var field_name=element.getAttribute("field");//子集名称。格式为t_a19_2.永远是变化后的子集
    var bread=true;//=true  变化前  =false  变化后
    /**分析是变化前还是变化后*/
    if (field_name!=null&&field_name.length>1){
	    if(field_name.substring(field_name.length-2,field_name.length).indexOf("_1")==-1)//如果是变化后的子集，则插入子集区域
	    	bread=false;
    }
	var xmlrec=getDecodeStr(xmlcontent);
	xmlrec=replaceAll(xmlrec,"＜","<");
	xmlrec=replaceAll(xmlrec,"＞",">");
	xmlrec=replaceAll(xmlrec,"＇","'");
	xmlrec=replaceAll(xmlrec,"＂",'"');
	xmlrec=replaceAll(xmlrec,"&","");
//	alert("转码后的xml："+xmlrec);
	var XMLDoc = loadXMLString(xmlrec);
	XMLDoc.async=false;
	var rootNode = XMLDoc.documentElement;///records那层节点

	if(rootNode)
	{
		var divid=field_name+"_div";
		var div=document.getElementById(divid);
		if(!div)
		{
			div=document.createElement("div");
			div.style.width="100%";
			div.style.height="100%";
			div.style.overflow="auto";
			div.className="fixedDiv";
			div.id=divid;
			element.appendChild(div);
		}
		var fields=rootNode.getAttribute("columns");
		var fieldarr=fields.split("`");
		var cols=fieldarr.length;
		var recNodes= rootNode.childNodes;
		var rows=0;
		for(var i=0;i<recNodes.length;i++){
			if(recNodes[i].nodeType==1){
				rows++;
			}
		}
		var rwPriv=rootNode.getAttribute("rwPriv");//子集读写权限，用于控制子集中的三个按钮（新增，插入，删除）
		var fieldsPriv=rootNode.getAttribute("fieldsPriv");//指标权限
		var fieldsWidth=rootNode.getAttribute("fieldsWidth");//指标的列宽
		if(typeof(rwPriv)=="undefined" || typeof(fieldsPriv)=="undefined" ||typeof(fieldsWidth)=="undefined" ||rwPriv==null||fieldsPriv==null||fieldsWidth==null)
		{
			validateFlag=false;
			
			if(window.Android!=null){
				window.Android.showToast("生成子集的xml中缺少子集权限、指标权限、指标列宽这三个属性");
				
			}else{
				var parameters = "生成子集的xml中缺少子集权限、指标权限、指标列宽这三个属性";
				// parameters =escape(parameters).toLocaleLowerCase().replace(/%u/gi,'\\u');
				document.location="objc::showToast::"+parameters; 
			}
			///return;
		}
		setSubTableReadorWiter(rwPriv,fieldsPriv,div);
		var subview=new SubSetView(rows,cols,fieldarr,div,recNodes,field_name,element,rwPriv,fieldsPriv,fieldsWidth);
		var dataset=getDataset();
		var subviewArray=dataset.subviewArray;
		subviewArray[field_name]=subview;//把所有的对象都存储起来 
		subsetInitArray[field_name]=1;
	}
}
/*子集对象**/
function SubSetView(row,col,column,elementdiv,recNodes,field_name,element,rwPriv,fieldsPriv,fieldsWidth) {
	this._table=element;//document.getElementById(tableid);
	this._table.subview=this;
	this._row=row;//记录条数
	this._col=col;//列数
	this._column=column;//指标列的字符串数组
	this._parent=elementdiv;//外层div的对象
	this._recNodes=recNodes;//各个record记录，这个貌似是<record>..........</record>的个数
	this._field_name=field_name;//子集的名字
	this._element=element;
	this._rwPriv=rwPriv;//子集的读写权限
	this._fieldsPrivArr=fieldsPriv.split(",");//每个指标的读写权限。如果子集是写，而某个指标是读，那么指标是只读模式
	this._fieldsWidthArr=fieldsWidth.split(",");//每个指标的列宽。以像素为单位
	this._fieldsPriv=fieldsPriv;//子集指标失去焦点时，重新组装xml时用到。（populateSubXml（）方法。）
	this._fieldsWidth=fieldsWidth;//子集指标失去焦点时，重新组装xml时用到。（populateSubXml（）方法。）
	this._activeRow=null;//当前光标所在的行  xcs 2013-11-14
	this._activeRowIndex=1;//当前光标所在的行号  xcs 2013-11-14	
	this.rowcount=row;
    this._field_list=new Array();
    var elementtr =this._table.rows[0];
    var elementtd=elementtr.getElementsByTagName("TD");
    for(var i=0;i<this._col;i++)
    {
		var indexname="_"+this._column[i].toUpperCase();
		var currenttd=elementtd[i+1];	
		var nowtd=new Object();
		nowtd.desc=currenttd.getAttribute("desc");
		nowtd.codesetid=currenttd.getAttribute("codesetid");
		nowtd.datatype=currenttd.getAttribute("datatype");
		nowtd.descwidth=currenttd.getAttribute("descwidth");
		this._field_list[indexname]=i;
		this._field_list[i]=nowtd;
	}   
}
////////////////////////////////////////子集对象的一些重构方法  开始///////////////////////////////////////////////////////

/*子集某个指标失去焦点后重新组装xml**/
SubSetView.prototype.populateSubXml=function()
{
	var xml = "";
	xml += "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
	xml += "<records columns=\"" +this._column.join("`")+ "\"";
	xml += " rwPriv=\""+this._rwPriv+"\" fieldsPriv=\""+this._fieldsPriv+"\" fieldsWidth=\""+this._fieldsWidth+"\">";
	var content="";
	for(var i=1;i<this._table.rows.length;i++)
	{
	    var thetr = this._table.rows[i];
	    var i9999=thetr.getAttribute("I9999");
		content += "<record I9999=\""+i9999+"\"  ";
		if(thetr.style.display=='none')//被删除的
			content +="deleted=\"1\"";
		else
			content +="deleted=\"0\"";
		content +=" >";
		var values="";//存储数据
		var tmp="";//存储暂时的value值
		for(var j=1;j<thetr.cells.length;j++)//遍历tr下所有的td
		{
			var inputobj=this._field_list[j-1];//field_list 个数总是比 td的个数少一个
			var currentInput=thetr.cells[j].children[0];
			var dataType=inputobj.datatype;
  			var codesetid=inputobj.codesetid;
			if(codesetid!=null&&codesetid!="0")//如果是代码型
			{  			
			    var codevalue=currentInput.getAttribute("codevalue");
			    if(codevalue==null || codevalue=="")
			       codevalue="&";
				values=values+codevalue+"`";	
			}
			else//如果不是代码型，直接把value中的值得到
			{
				tmp=currentInput.value;
				if(tmp==null || tmp=="")
					tmp="&"; 
				if(dataType!=null&&dataType=="date"&&tmp!='&')//如果是日期型，但是格式不对，就要把数据给清了
				{
				    var _date=new Date(tmp);
					if (isNaN(_date))
					{
						currentInput.value="";
						tmp="&";
					}
				}   
				values=values+tmp+"`";
			}
		}
		values=values.substr(0,values.length-1);
		values=replaceAll(values,"〈","<");
		values=replaceAll(values,"〉",">");
		content += values;
		content += "</record>";	
	}
	xml = xml+content+"</records>";
	//alert(xml);
	return	xml;
}

/*点击“新增”按钮**/
SubSetView.prototype.appendRow   =function()
{
	for(var i=1;i<this._table.rows.length;i++){
		var temptr=this._table.rows[i];
		temptr.setAttribute("select","0");
	}
    var tr = this._table.insertRow(this._table.rows.length);
    this.rowcount=this.rowcount+1;
    this._activeRowIndex=this._table.rows.length-1;
	tr.subview=this;
	var temp=this._table.getAttribute("id");
	var tableid=temp.substring(0,temp.length-6);
    var td = tr.insertCell(tr.cells.length);
 	td.innerHTML="<input type=\"checkbox\" name=\""+this._field_name+"_chk_"+this._table.rows.length+"\">";
 	td.setAttribute("align","center");		    
	tr.setAttribute("I9999","-1");
	tr.setAttribute("select","1");
	for(var j=0;j<this._col;j++)
	{
		var inputid=tableid+"_"+this.rowcount+"_"+j;
		var currentPriv=this._fieldsPrivArr[j];
		var currentWidth=this._fieldsWidthArr[j];
		var desc=this._field_list[j].desc;
	   	currentWidth=parseInt(currentWidth);
		var td = tr.insertCell(tr.cells.length);
		if(currentPriv=="0")
		{
			//td.className="hideclass";
			td.style.display="none";
		}
	  	var fmobj=this._field_list[j];
	  	if(currentPriv==2)//（即指标有写权限）
	  	{
	  		if(fmobj)
			{  
				if(fmobj.codesetid=="0")
				{
					if(fmobj.datatype=="D")//日期型
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" dataType=\"D\" style=\"font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" extra=\"editor\" dropDown=\"dropDownDate\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
					else if(fmobj.datatype=="N")//数值型
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" dataType=\"N\" style=\"font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
					else//字符型
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" dataType=\"A\" style=\"font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
				}
				else
				{
					if(fmobj.C=='UN'||fmobj.C=='UM'||fmobj.C=='@K')
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" dataType=\"A\" codesetid=\""+fmobj.codesetid+"\"  style=\"font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" dropDown=\"dropdownCode\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
					else
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" dataType=\"A\" codesetid=\""+fmobj.codesetid+"\"  style=\"font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" dropDown=\"dropDownList\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\"  value=\"\" keyvalue=\"\" oldvalue=\"\">";
				}					
				td.setAttribute("align","left");				
			}
			else
			{
				td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" style=\"font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\"  value=\"\" keyvalue=\"\" oldvalue=\"\">";
				td.setAttribute("align","left");
			}
	  	} //currentPriv=2 结束
		else//currentPriv=1或0（即指标读或无权限）
		{
			if(fmobj)
			{  
				if(fmobj.codesetid=="0")
				{
					if(fmobj.datatype=="D")//日期型
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" disabled=\"true\" dataType=\"D\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" extra=\"editor\" dropDown=\"dropDownDate\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
					else if(fmobj.datatype=="N")//数值型
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" disabled=\"true\" dataType=\"N\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
					else//字符型
						td.innerHTML="<input type=\"text\"  id=\""+inputid+"\" desc=\""+desc+"\"disabled=\"true\" dataType=\"A\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
				}
				else
				{
					if(fmobj.codesetid=='UN'||fmobj.codesetid=='UM'||fmobj.codesetid=='@K')
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" dataType=\"A\" codesetid=\""+fmobj.codesetid+"\"  disabled=\"true\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" dropDown=\"dropdownCode\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
					else
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" dataType=\"A\" codesetid=\""+fmobj.codesetid+"\"  disabled=\"true\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" dropDown=\"dropDownList\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
				}					
				td.setAttribute("align","left");				
			}
			else
			{
				td.innerHTML="<input type=\"text\"  id=\""+inputid+"\" desc=\""+desc+"\" disabled=\"true\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
				td.setAttribute("align","left");
			}
		} ////currentPriv=1或0 结束
	} //for循环 结束
//	initElements(this._table);
    this.combineXml();
    initSubSet(tableid);		
}

/*点击“插入” 按钮**/
SubSetView.prototype.insRow   =function()
{
	for(var i=1;i<this._table.rows.length;i++){
		var temptr=this._table.rows[i];
		temptr.setAttribute("select","0");
	}
	if(this._activeRowIndex>this._table.rows.length)
	{
	  this_activeRowIndex=1;
	}
	//alert(this._activeRowIndex);

    var tr = this._table.insertRow(this._activeRowIndex);
    this.rowcount=this.rowcount+1;
	var temp=this._table.getAttribute("id");
	var tableid=temp.substring(0,temp.length-6);
	tr.subview=this;
    var td = tr.insertCell(tr.cells.length);
 	td.innerHTML="<input type=\"checkbox\" name=\""+this._field_name+"_chk_"+this._table.rows.length+"\">";
 	td.setAttribute("align","center");		    
	tr.setAttribute("I9999","-1");
	tr.setAttribute("select","1");
	for(var j=0;j<this._col;j++)
	{
		var inputid=tableid+"_"+(this._table.rows.length-1)+"_"+j;
		var currentPriv=this._fieldsPrivArr[j];
		var currentWidth=this._fieldsWidthArr[j];
		var desc=this._field_list[j].desc;
	   	currentWidth=parseInt(currentWidth);
		var td = tr.insertCell(tr.cells.length);
		if(currentPriv=="0")
		{
			//td.className="hideclass";
			td.style.display= "none";
		}
	  	var fmobj=this._field_list[j];
	  	if(currentPriv==2)//（即指标有写权限）
	  	{
	  		if(fmobj)
			{  
				if(fmobj.codesetid=="0")
				{
					if(fmobj.datatype=="D")//日期型
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" dataType=\"D\" style=\"font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" extra=\"editor\" dropDown=\"dropDownDate\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
					else if(fmobj.datatype=="N")//数值型
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" dataType=\"N\" style=\"font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
					else//字符型
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" dataType=\"A\" style=\"font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
				}
				else
				{
					if(fmobj.C=='UN'||fmobj.C=='UM'||fmobj.C=='@K')
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" dataType=\"A\" codesetid=\""+fmobj.codesetid+"\"  style=\"font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" dropDown=\"dropdownCode\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
					else
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" dataType=\"A\" codesetid=\""+fmobj.codesetid+"\"  style=\"font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" dropDown=\"dropDownList\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
				}					
				td.setAttribute("align","left");				
			}
			else
			{
				td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" style=\"font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
				td.setAttribute("align","left");
			}
	  	} //currentPriv=2 结束
		else//currentPriv=1或0（即指标读或无权限）
		{
			if(fmobj)
			{  
				if(fmobj.codesetid=="0")
				{
					if(fmobj.datatype=="D")//日期型
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" disabled=\"true\" dataType=\"D\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" extra=\"editor\" dropDown=\"dropDownDate\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
					else if(fmobj.datatype=="N")//数值型
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" disabled=\"true\" dataType=\"N\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
					else//字符型
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" disabled=\"true\" dataType=\"A\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
				}
				else
				{
					if(fmobj.codesetid=='UN'||fmobj.codesetid=='UM'||fmobj.codesetid=='@K')
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" codesetid=\""+fmobj.codesetid+"\"  disabled=\"true\" dataType=\"A\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" dropDown=\"dropdownCode\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
					else
						td.innerHTML="<input type=\"text\" id=\""+inputid+"\" desc=\""+desc+"\" codesetid=\""+fmobj.codesetid+"\"  disabled=\"true\" dataType=\"A\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" dropDown=\"dropDownList\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
				}					
				td.setAttribute("align","left");				
			}
			else
			{
				td.innerHTML="<input type=\"text\"  id=\""+inputid+"\" desc=\""+desc+"\" disabled=\"true\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";border:1px #000 solid;\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\" onclick=\"subviewClick('"+tableid+"',this);\" value=\"\" keyvalue=\"\" oldvalue=\"\">";
				td.setAttribute("align","left");
			}
		} ////currentPriv=1或0 结束
	} //for循环 结束
//	initElements(this._table);
    this.combineXml();
    initSubSet(tableid);				
}

/*点击"删除" 按钮**/
SubSetView.prototype.delRow   =function(tableid)
{
	
      for (var i=this._table.rows.length-1; i>0; i--)
      {
        var thetr = this._table.rows[i];
        var thechkbox=thetr.cells[0].children[0];
       	if(!thechkbox.checked)
        		continue;
        if(thetr.getAttribute('i9999')==-1)		//只可对非档案库里的子集记录作此操作  dengcan 2011-3-11
//	        thetr.removeNode(true);       
			removechildren(this._table.rows[i]);
	    else if(thetr.getAttribute('i9999')!=-1)
	    	thetr.style.display='none';
      }	
      this.combineXml();	
//table 中增加删除行都要重新初始化这个table所对应的subview
      initSubSet(tableid);
      this._activeRowIndex=this._table.rows.length; 
}
SubSetView.prototype.alertConfirm   =function()
{
	var temp=this._table.getAttribute("id");
	var tableid=temp.substring(0,temp.length-6);
	//先判断是否选中了记录。
    var isSelected="0";
	  for (var i=this._table.rows.length-1; i>0; i--)
    {
      var thetr = this._table.rows[i];
      var thechkbox=thetr.cells[0].children[0];
     	if(thechkbox.checked)
     	{
     		isSelected="1";
			break;
     	}
    }
    if(isSelected=="0")
    {
    	validateFlag=false;
  
    	var parameters = "请选择一条记录";
    	if(window.Android!=null){
			window.Android.showToast(parameters);	
		}else{
			// parameters =escape(parameters).toLocaleLowerCase().replace(/%u/gi,'\\u');
			document.location="objc::showToast::"+parameters; 
		}
    	//alert("请选择一条记录");
		return;
    }
    validateFlag=false;
    
    var parameters = "确定要删除吗？";
	if(window.Android!=null){
		window.Android.showAlert(parameters,tableid);
	}else{
		// parameters =escape(parameters).replace(/%u/gi,'\\u');
		document.location="objc::showAlert::"+parameters+","+tableid; 
	}
    //alert("确定要删除吗？");
}
/*增加、插入、删除一条记录后，都要重新组装sql**/
SubSetView.prototype.combineXml=function()
{

	var xml=this.populateSubXml();//this指的是subview
	var dataset=getDataset();
	dataset.modified=true;//dataset的值被修改过了。
	var record=dataset.record;
	if(!record)
	  return;
	var xml=getEncodeStr(xml);
	record.setValue(this._field_name,xml);

}


////////////////////////////////////////子集对象的一些重构方法  结束///////////////////////////////////////////////////////

/*子集指标点击事件**/
function subviewClick(tableid,obj)
{
	activeobj =obj.id;
	var subviewtable =tableid+"_table";
	var elem=document.getElementById(subviewtable);//获取到子集的table
	activeobjTable=tableid;
	var subview=elem.subview;
	for(var i=1;i<elem.rows.length;i++){
		var temptr=elem.rows[i];
		temptr.setAttribute("select","0");
	}
	var currentTR=obj.parentNode.parentNode;//obj 是 input输入框
	currentTR.setAttribute("select","1");
	for(var i=1;i<elem.rows.length;i++){
		var temptr=elem.rows[i];
		if(temptr.getAttribute("select")=="1"){
			subview._activeRowIndex=i;
			break;
		}
	}
	var datatype=obj.getAttribute("datatype");
	if(datatype){
		switch(datatype)
		{
			
			case "A":///字符串型指标，如姓名
					var codesetid = obj.getAttribute("codesetid");//代码值
					var desc = obj.getAttribute("desc");//代码类名称
					var objid =obj.id;
					if(codesetid){
						if(codesetid!=""&&codesetid!="0"){
							if(validateFlag){
								
								if(window.Android!=null){
									
									window.Android.showDialog(objid,codesetid,desc);//返回值是一个数组 第一个值是codevalue 第二个值是codeddesc
								}else{
									var parameters = objid+","+codesetid+","+desc;
									// parameters =escape(parameters).replace(/%u/gi,'\\u');
									document.location="objc::showDialog::"+parameters; 
								}
								
							}
						}
						break;
					}
					break;
			case "N"://数值型指标
				break;
			case "M"://备注型指标
				break;
			case "D"://日期型指标
				var tempdate=obj.getAttribute("keyvalue");
				if(!tempdate){
					tempdate="";
				}
				if(validateFlag){
					
					if(window.Android!=null){
						
						window.Android.getDateForWeb(activeobj,tempdate,"true");
					}else{
						var parameters = activeobj+","+tempdate+",true";
						// parameters =escape(parameters).toLocaleLowerCase().replace(/%u/gi,'\\u');
						document.location="objc::getDateForWeb::"+parameters; 
					}
				}
				
				break;
			default:
				break;
		}
	}
}


/*子集指标失去焦点**/
function subviewMouseout(tableid,obj)
{
	//alert("走指标失去焦点了");
	var datatype=obj.getAttribute("datatype");
	if(datatype){
		switch(datatype)
		{
			
			case "A":///字符串型指标，如姓名
					break;
			case "N"://数值型指标
				function checkIsNum(value)//验证是否是数字
				{
					return /^-?\d+(\.\d+)?$/.test(value);
				}
				if(obj.value!=""&&!checkIsNum(obj.value)){
					validateFlag=false;
					
					if(window.Android!=null){
						window.Android.showToast("请输入数字");
					}else{
						var parameters = "请输入数字";
						// parameters =escape(parameters).toLocaleLowerCase().replace(/%u/gi,'\\u');
						document.location="objc::showToast::"+parameters; 
					}
					obj.value="";
					obj.setAttribute("oldValue",obj.value);
					obj.setAttribute("keyValue",obj.value);///value,keyValue,oldValue都一样。
					window.setTimeout( function(){ obj.focus(); }, 0);//然后把该文本域聚焦
					return false;
				}else{
					break;
				}
			case "M"://备注型指标
				break;
			case "D"://日期型指标
				break;
			default:
				break;
		}
	}
	var codesetid=obj.getAttribute("codesetid");
	if(codesetid){
		if(codesetid==""||codesetid=="0"){
			obj.setAttribute("keyvalue",obj.value);
		}
	}else{
		obj.setAttribute("keyvalue",obj.value);
	}
	var keyvalue=obj.getAttribute("keyvalue");
	var oldvalue=obj.getAttribute("oldvalue");
	initSubSet(tableid);
	var subviewtable =tableid+"_table";
	var elem=document.getElementById(subviewtable);//获取到子集的table
	if(oldvalue!=keyvalue){
		var xml=elem.subview.populateSubXml();//生成新的子集的xml
		var dataset=getDataset();
		var record=dataset.record;
		if(!record)
			return;
		var newxml=getEncodeStr(xml);//将xml加密
		record.setValue(elem.subview._field_name,newxml);//向dataset的record中赋值
		obj.setAttribute("oldvalue",obj.getAttribute("keyvalue"));
	}
}

/*为子集的三个按钮增加点击事件**/
function processSubsetButton(flag,subviewtable)
{
	try
	{
		if(typeof(flag)=="undefined" || flag==null)
		{
			throw new Error(BUTTON_OF_SUBSET_INIT_ERROR);
		}
		if(flag==0)
		{
			var subview=getSubView(subviewtable);
			subview.appendRow();
		}
		else if(flag==1)
		{
			var subview=getSubView(subviewtable);
			subview.insRow();
		}
		else if(flag==2)
		{
			var subview=getSubView(subviewtable);
			deleteSubView=subview;
			if(validateFlag){
				subview.alertConfirm();
			}
			
		}
		else
		{
			throw new Error(BUTTON_OF_SUBSET_INIT_ERROR);
		}
	}
	catch (e)
	{
		validateFlag=false;
		if(window.Android!=null){
			window.Android.showToast(e.message);
		}else{
			var parameters  = e.message;
			// parameters =escape(parameters).toLocaleLowerCase().replace(/%u/gi,'\\u');
			document.location="objc::showToast::"+parameters; 
		}
	}
	
}
function getAlert(returnFlag,tableid){
	validateFlag=true;
	if(returnFlag=="1"){
		deleteSubView.delRow(tableid);
	}
}
/*
* 替换字符串中所有要替换的字符串
* text  指定的文本
* replacement 指定的旧字符
* target 指定的新字符
*/
function replaceAll(text,replacement,target){
    if(text==null||text==""){
    	return text;
    }
    if(replacement==null||replacement==""){ 
    	return text;
    }
    if(target==null) target="";
    var returnString="";
    var index=text.indexOf(replacement);
    while(index!=-1){
        if(index!=0) returnString+=text.substring(0,index)+target;
        text=text.substring(index+replacement.length);
        index=text.indexOf(replacement);
    }
    if(text!=""){
		returnString+=text;
	}
    return returnString;
}
function removechildren(element) {
	try {
		var new_tr = element.parentNode;//得到table那一层
		// 为了在ie和firefox下都能正常使用,就要用另一个方法代替,最取上一层的父结点,然后remove.
		new_tr.removeChild(element);//使用table移除行
	} catch(e) {
		validateFlag=false;
		if(window.Android!=null){
			window.Android.showToast(e.message);
		}else{
			var parameters = e.message;
			// parameters =escape(parameters).toLocaleLowerCase().replace(/%u/gi,'\\u');
			document.location="objc::showToast::"+parameters; 
		}
	}
}
//由于从后台输出过来的页面中的数据不好控制，所以加上这个方法对子集的指标进行控制
function setSubTableReadorWiter(objRwPriv,objFieldsPriv,objDiv){
	var rwPriv = objRwPriv;
	var fieldsPriv =objFieldsPriv.split(",");
	var objDiv=objDiv;
	if(rwPriv!="2"){
		var buttonList =objDiv.getElementsByTagName("button");
		for(var i=0;i<buttonList.length;i++){
			buttonList[i].style.display= "none";
		}
	}else{
		var objTr=objDiv.getElementsByTagName("tr");//得到当前div下面的所有tr
		for(var i=1;i<objTr.length;i++){
			var objTd=objTr[i].getElementsByTagName("td");//得到当前tr下的所有td(也就是子集中所有的指标)
			for(var j=1;j<objTd.length;j++){
				if(fieldsPriv[j-1]!="2"){
					var inputelement=objTd[j].getElementsByTagName("input");
					inputelement[0].disabled=true;
					inputelement[0].style.background="#DEDEDE";
				}
			} 
		}
	}
}
//前台JS回调填充数据
function getCodevalueSure(objid,codeitemid,codeitemdesc){
	var obj = document.getElementById(objid);
	var codesetid=obj.getAttribute("codesetid");
	obj.setAttribute("value",codeitemdesc);
	obj.setAttribute("keyvalue",codeitemid);
	obj.setAttribute("codevalue",codeitemid);
    if(window.Android==null){
        if (objid.indexOf("t_")==0){//子集
           // subviewMouseout(objid,obj);
        }
        else {
	        processBlur(obj);//obj.blur();//ios不触发 andriod不用触发 wangrd 2015-03-12
        }
    }
}
//若是代码类弹出框点了取消就走这个方法
function getCodevalueCanle(objid){
	var obj = document.getElementById(objid);
	obj.blur();
}
//自动保存和保存调用这个方法
function SaveTempletData(ins_id,tabid,task_id,infor_type,object_id,pagenum,saveflag,selfapply){
	try{
		var tempid=document.getElementById(activeobj);

		if(tempid){
			if(activeobjTable!=""){//这种情况下是子集指标最后一个失去焦点（直接点保存的那种）
				subviewMouseout(activeobjTable,tempid);
			}else{
				processBlur(tempid);
			}
			
		}
		var recordData="";
		var fieldItem="";
		var dataset=getDataset();
		if(validateFlag){//首先验证当前页面有没有提示框，valiateFalg=false  说明有提示框 不能做任何处理
			if(dataset && !dataset.modified){
				if(window.Android!=null){
					if(saveflag=="1"){
						window.Android.showToast("保存成功");
					}else{
						window.Android.workflowRun(saveflag);
					}
				}else{
					if(saveflag=="1"){
						document.location="objc::showToast::"+"保存成功"; 
					}else{
						document.location="objc::workflowRun::"+saveflag; 
					}
					
				}
				return false;
			}
			if(dataset && dataset.modified){
				var current=dataset.record;
				if(current){
					for(var j=0; j<current.length; j++){
						var str=current[j];
						if(typeof(str)=="string") 
						{
							str=str.replace(/,/g,"，");//替换所有英文逗号为中文逗号。
						}
						recordData+=str+",";
					}
				}
				
				var hashvo=new ParameterSet();
				hashvo.setValue("transType","15");
				hashvo.setValue("saveflag", saveflag);
				hashvo.setValue("task_id",task_id);
				hashvo.setValue("tabid",tabid);
				hashvo.setValue("infor_type",infor_type);
				hashvo.setValue("objectId",object_id);
				hashvo.setValue("recordData",recordData);
				hashvo.setValue("pagenum",pagenum);
				hashvo.setValue("ins_id",ins_id);
				hashvo.setValue("selfapply",selfapply);
				var request=new Request({asynchronous:false,onSuccess:saveOk,functionId:'9102009001'},hashvo); 

			}
		}
		
	}catch(e){
		validateFlag=false;	
		if(window.Android!=null){
			window.Android.showToast("远端服务出错了.......");
		}else{
			document.location="objc::showToast::"+"远端服务出错了...";
		}
	}
	
}

function saveOk(outparameters){
	var savemessage=outparameters.getValue("savemessage");
	var reflush=outparameters.getValue("reflush");
	if(savemessage=="saveok"){
		var saveflag=outparameters.getValue("saveflag");
		if(saveflag=="1"){//点击保存按钮才会提示，其他的属于自动保存不给予提示
			if(reflush&&reflush=="true"){//如果需要刷新就开始刷新
				validateFlag=false;
				if(window.Android!=null){
					window.Android.showToastForReflush("保存成功",reflush);
				}else{
					document.location="objc::showToastForReflush::"+"保存成功,"+reflush;
				}
			}else{//如果不刷新 ，需要手动的将dataset的modified属性置为false
				validateFlag=false;
				if(window.Android!=null){
					window.Android.showToast("保存成功");
				}else{
					document.location="objc::showToast::"+"保存成功";
				}
				var dataset=getDataset();
				if(dataset){
					dataset.modified=false;//保存完成后，要将dataset的数据恢复到没有被更改的状态
				}
			}
		}else{
			
			if(window.Android!=null){
				window.Android.workflowRun(saveflag);
			}else{
				
				document.location="objc::workflowRun::"+saveflag; 
			}
		}
	}else{//保存不成功的话，提示出来错误信息
		validateFlag=false;
		
		if(window.Android!=null){
			window.Android.showToast(savemessage);
		}else{
			document.location="objc::showToast::"+savemessage;
		}
	}
}
function setDateForWeb(objId,date){
	var temp=document.getElementById(objId);
	date=date.replace(/-/g, ".");
	temp.setAttribute("value",date);
	temp.setAttribute("keyvalue",date);
    if(window.Android==null){
        if (objId.indexOf("t_")==0){//子集
           // subviewMouseout(objid,obj);
        }
        else {
	        processBlur(temp);//ios需要主动触发 但tmp.blur()不灵，弹出日期框时已经触发了 wangrd 2015-03-12
        }
    }
}
function getValadateFlag(){
	validateFlag=true;
}

function calculate(tabid,ins_id,selfapply,midValue){
	var hashvo=new ParameterSet();
	hashvo.setValue("transType","24");
	hashvo.setValue("midValue",midValue);
	hashvo.setValue("tabid",tabid);
	hashvo.setValue("ins_id",ins_id);
	hashvo.setValue("selfapply",selfapply);
	var request=new Request({asynchronous:false,onSuccess:calculateOk,functionId:'9102009001'},hashvo); 
}
function calculateOk(outparameters){
	var calculatemessage=outparameters.getValue("calculatemessage");
	if(calculatemessage=="calculateok"){
		validateFlag=false;
		
		if(window.Android!=null){
			window.Android.showToast("计算成功");
			window.location.reload();
		}else{		
			document.location="objc::showToast::"+"计算成功"; 
		}
	}else{
		validateFlag=false;
		if(window.Android!=null){
			window.Android.showToast(calculatemessage);
		}else{
			document.location="objc::showToast::"+calculatemessage; 
		}
	}	
}

function getShowToastForReflush(reflush){
	validateFlag=true;
	if(reflush){
		window.location.reload();
	}
}
//尝试处理安卓2.3以及以下的系统不支持div滚动的问题
function noBarsOnTouchScreen(arg)
{
  var elem, tx, ty;

  if('ontouchstart' in document.documentElement ) {
          if (elem = document.getElementById(arg)) {
              elem.style.overflow = 'hidden';
              elem.ontouchstart = ts;
              elem.ontouchmove = tm;
          }
  }

  function ts( e )
  {
    var tch;

    if(  e.touches.length == 1 )
    {
      e.stopPropagation();
      tch = e.touches[ 0 ];
      tx = tch.pageX;
      ty = tch.pageY;
    }
  }

  function tm( e )
  {
    var tch;

    if(  e.touches.length == 1 )
    {
      e.preventDefault();
      e.stopPropagation();
      tch = e.touches[ 0 ];
      this.scrollTop +=  ty - tch.pageY;
      ty = tch.pageY;
    }
  }
}
//处理机制已经完成