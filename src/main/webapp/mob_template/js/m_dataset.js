锘匡豢/*鍒涘缓dataset**/
function createDataset(ID)
{
	var dataset=new Object();
	
	//dataset鐨勫睘鎬�   涓ゅ杩樻湁modified鍜宺ecord灞炴�с�傚湪鍒殑鍦版柟璧嬬殑鍊�
	
	dataset.fields=new Array();//鎵�鏈夌殑瀛楁
	dataset.fields.fieldCount=0;//fields涓瓧娈电殑涓暟銆�
	dataset.modified=false;///濡傛灉涓篺alse锛岄偅涔堢偣鍑讳繚瀛樻椂鏃犱换浣曞弽搴斻��
	dataset.subviewArray=new Array();
	//dataset鐨勬柟娉�
	dataset.addField=dataset_addField;//鍚慺ields灞炴�ф坊鍔犲��
	dataset.getField=dataset_getField;
	dataset.getFieldCount=dataset_getFieldCount;
	if(ID)
	{
		dataset.id=ID;
		_array_dataset[_array_dataset.length]=dataset;///涓轰簡璁╁鐣岋紙common.js锛夊彲浠ヨ闂畠
	}
	return dataset;
}

var _array_dataset=new Array();///涓哄鐣岃闂畠鎻愪緵鏂规硶
/*涓哄鐣岃闂畠鎻愪緵鏂规硶**/
function getDataset()
{
	try
	{
		if(_array_dataset.length>1)
		{
			throw new Error(ERROR_WHEN_GENERATE_DATASET);//鐢熸垚dataset瀵硅薄閿欒
		}
		if(_array_dataset.length>0)
		{
			return _array_dataset[0];
		}
		else
		{
			return null;
		}
	}
	catch (e)
	{
		if(window.Android!=null){
			window.Android.showToast(e.message);
		}else{
			var parameters =e.message;
			// parameters =escape(parameters).replace(/%u/gi,'\\u');
			document.location="objc::showToast::"+parameters; 
		}
	}
}

/*鍒濆鍖杁ataset**/
function initDataset(dataset)
{
	try
	{
		var divname=dataset.id;
		var xmldiv=document.getElementById(divname);
		var xmlvalue=xmldiv.innerHTML;
		var xmlIsland=loadXMLString(xmlvalue);
		var current=appendFromXml(dataset, xmlIsland.documentElement, true);///鎶妜ml涓殑鏁版嵁鍔犺浇鍒癱urrent涓�倄mlIsland.documentElement涓簒ml鐨勭涓�涓妭鐐�

		if (current)
		{
			dataset.record=current;
			initsubview(current, dataset);
		}
		
	}
	catch(e)
	{
		if(window.Android!=null){
			window.Android.showToast(e.message);
		}else{
			var parameters =e.message;
			// parameters =escape(parameters).replace(/%u/gi,'\\u');
			document.location="objc::showToast::"+parameters; 
		}
	}
}

/*鎶妜ml鐨勬暟鎹姞杞藉埌current涓�俢urrent鏄竴涓璞�**/
function appendFromXml(dataset, root, init)
{
	if (!root) return;
	var current;
	try
	{
		if (root) 
		{
			var recordNodes;
//			alert("鍔犺浇xml");
			for(var ii=0;ii<root.childNodes.length;ii++)
			{
				if(root.childNodes[ii].nodeType==1)
				{
					recordNodes=root.childNodes[ii];
					break;
				}
			}
//			alert("寰楀埌recordNodes");
			for (var i=0; i<recordNodes.childNodes.length; i++)///recordNodes.length鐨勫�间负1
			{
				if(recordNodes.childNodes[i].nodeType==1)
				{
					var recordNode=recordNodes.childNodes[i];//record鑺傜偣
					for(var k=0;k<recordNode.childNodes.length;k++)
					{
						if(recordNode.childNodes[k].nodeType==1)
						{
							var newData=recordNode.childNodes[k];//new鑺傜偣
							var temprecord=newData.textContent;
//							alert(temprecord);		
							var record=temprecord.split(",");
							if (init) 
							   initRecord(record, dataset);
							current=record;
							break;
						}
					}
					break;
				}
			}
		}
	}
	catch (e)
	{
		if(window.Android!=null){
			window.Android.showToast(e.message);
		}else{
			var parameters =e.message;
			// parameters =escape(parameters).replace(/%u/gi,'\\u');
			document.location="objc::showToast::"+parameters; 
		}
	}
	
	return current;
}


/*鍒濆鍖杛ecord**/
function initRecord(record, dataset)
{
	try
	{
		//record鐨勫睘鎬�
		record.dataset=dataset;
		record.fields=dataset.fields;//瑕佹壘field鐨勪笅鏍囧拰field鐨勫��
		//record鐨勬柟娉�
		record.getValue=record_getValue;
		record.setValue=record_setValue;
		record.getIndexOfField=record_getIndexOfField;

		//涓嬮潰寮�濮嬬粰record鐨刦ields鐨勫�艰繕鍘熸渶鍒濋潰鐩�
//		alert("xml鐨勬暟鎹釜鏁颁负"+record.length);
//		alert("瀛楁鐨勪釜鏁颁负"+dataset.getFieldCount());
		if(record.length!=dataset.getFieldCount())
		{
			throw new Error(CANNOT_MATCH_COUNT);
		}
		for(var j=0; j<=record.length-1; j++)//record浠夸經鏄釜鏁扮粍锛岄噷闈㈠瓨鏀剧潃璁稿鍊�
		{
			if (record[j]!="")
			{
				switch (dataset.getField(j).dataType)
				{
					case "string":
					{
						record[j]=filter_ValidStr(record[j]);//鐢ㄩ�楀彿鏇挎崲 ````锛岃繕鍘熸渶鍒濈殑閫楀彿銆�
						break;
					}

					case "byte":;
					case "short":;
					case "int":;
					case "long":
					{
						record[j]=getInt(record[j]);
						break;
					}

					case "float":;
					case "double":;
					case "bigdecimal":
					{
						record[j]=getFloat(record[j]);
						break;
					}

					case "boolean":
					{
						record[j]=isTrue(record[j]);
						break;
					}

					case "date":;
					case "time":;
					case "datetime":
					{
						record[j]=new Date(replaceAll(replaceAll(record[j],'-','/'),'.','/'));//liuyz new Date(yyyy/MM/dd)锛岃繑鍥炵殑Date瀵硅薄鐨勬椂闂翠负 00:00:00,new Date(yyyy-MM-dd)锛岃繑鍥炵殑Date瀵硅薄鐨勬椂闂翠负 08:00:00
						break;
					}

					default:
					{
						record[j]=filter_ValidStr(record[j]);//鐢ㄩ�楀彿鏇挎崲 ````锛岃繕鍘熸渶鍒濈殑閫楀彿銆�
						break;
					}
				}// switch end
			}// if end
		}// for end
	}
	catch (e)
	{
		if(window.Android!=null){//鍦ㄥ畨鍗撲腑鐨勪俊鎭彁绀鸿鐢ㄨ繖鏍峰仛
			window.Android.showToast(e.message);
		}else{
			var parameters =e.message;
			// parameters =escape(parameters).replace(/%u/gi,'\\u');
			document.location="objc::showToast::"+parameters; 
		}
	}
	
}
function initsubview(record,dataset){
	try{
		for(var j=0; j<=record.length-1; j++)//record浠夸經鏄釜鏁扮粍锛岄噷闈㈠瓨鏀剧潃璁稿鍊�
		{
			if (record[j]!="")
			{
				var field_name=dataset.getField(j).fieldName;
				if(field_name.indexOf("t_")==0 && field_name.indexOf("_2")==(field_name.length-2)){
					initSubSet(field_name);
				}
			}// if end
		}// for end
	}
	catch (e)
	{
		if(window.Android!=null){//鍦ㄥ畨鍗撲腑鐨勪俊鎭彁绀鸿鐢ㄨ繖鏍峰仛
			window.Android.showToast(e.message);
		}else{
			var parameters =e.message;
			// parameters =escape(parameters).replace(/%u/gi,'\\u');
			document.location="objc::showToast::"+parameters; 
		}
		//alert(e.message);
	}
}

//////////////////dataset鎵�鏈夋柟娉曠殑鍏蜂綋瀹炵幇  寮�濮�///////////////////////////
/*鍚慸ataset涓坊鍔犲瓧娈点�傛坊鍔犲畬涔嬪悗锛宖ields灏卞垵濮嬪寲濂戒簡銆�**/
function dataset_addField(fieldName, dataType)
{
	//鐩墠field鏈�6涓睘鎬с�俤ataType,label,format,visible,codesetid,fieldName
	var dataset=this;
	try
	{
		var field=new Object;
		var i=dataset.fields.length;
		dataset.fields[i]=field;
		dataset.fields["_index_"+fieldName.toLowerCase()]=i;//鏂逛究鎵句笅鏍�
		field.dataType=dataType;
		field.fieldName=fieldName;
		dataset.fields.fieldCount++;

		return field;
	}
	catch(e)
	{
		if(window.Android!=null){//鍦ㄥ畨鍗撲腑鐨勪俊鎭彁绀鸿鐢ㄨ繖鏍峰仛
			window.Android.showToast(e.message);
		}else{
			var parameters =e.message;
			// parameters =escape(parameters).replace(/%u/gi,'\\u');
			document.location="objc::showToast::"+parameters; 
		}
		//alert(e.message);
	}
}
/*鏍规嵁瀛楁鍚嶅瓧鎴栦笅鏍囧彇寰楁暣涓瓧娈靛璞�**/
function dataset_getField(name)
{
	var dataset=this;
	return _dataset_getField(dataset.fields, name);
}

/*鏍规嵁瀛楁鍚嶅瓧鎴栦笅鏍囧彇寰楁暣涓瓧娈靛璞�  杈呭姪鍑芥暟**/
function _dataset_getField(fields, name)
{
	var field=null;
	if (typeof(name)=="number")//鏍规嵁涓嬭〃鎵�
	{
		field=fields[name];
	}
	else if (typeof(name)=="string")
	{
		var fieldIndex=fields["_index_"+name.toLowerCase()];
		if (!isNaN(fieldIndex))
			field=fields[fieldIndex];
	}
	return field;
}

/*寰楀埌dataset涓瓧娈电殑涓暟**/
function dataset_getFieldCount()
{
	return this.fields.fieldCount;
}

/////////////////////////dataset鎵�鏈夋柟娉曠殑鍏蜂綋瀹炵幇  缁撴潫/////////////////////////////////////////////



/////////////////////////////////reocrd 鎵�鏈夋柟娉曠殑鍏蜂綋瀹炵幇  寮�濮�/////////////////////////////////////
/*浠巖ecord涓緱鍒板瓧娈电殑鍊�**/
function record_getValue(fieldName)
{
	try
	{
		return _record_getValue(this, fieldName);
	}
	catch(e)
	{
		if(window.Android!=null){//鍦ㄥ畨鍗撲腑鐨勪俊鎭彁绀鸿鐢ㄨ繖鏍峰仛
			window.Android.showToast(e.message);
		}else{
			var parameters =e.message;
			// parameters =escape(parameters).replace(/%u/gi,'\\u');
			document.location="objc::showToast::"+parameters; 
		}
		//alert(e.message);
	}
}
/*浠巖ecord涓緱鍒板瓧娈电殑鍊� 杈呭姪鍑芥暟**/
function _record_getValue(record, fieldName)
{
	var dataset=record.dataset;
	var fields=record.fields;
	var fieldIndex=-1;///瀛楁鐨勪笅鏍�
	var value;
	try
	{
		if (typeof(fieldName)=="number")
		{
			fieldIndex=fieldName;
		}
		else if (typeof(fieldName)=="string")
		{
			fieldIndex=fields["_index_"+fieldName.toLowerCase()];
		}

		var field=fields[fieldIndex];
		if (typeof(field)=="undefined")
		{
			throw new Error(CANNOT_FIND_VALUE_FROM_RECORD);
		}

		value=record[fieldIndex];
		if (typeof(value)=="undefined" || value==null || (typeof(value)=="number" && isNaN(value)))
		{
			value="";
		}
	}
	catch (e)
	{
		if(window.Android!=null){//鍦ㄥ畨鍗撲腑鐨勪俊鎭彁绀鸿鐢ㄨ繖鏍峰仛
			window.Android.showToast(e.message);
		}else{
			var parameters =e.message;
			//parameters =escape(parameters).replace(/%u/gi,'\\u');
			document.location="objc::showToast::"+parameters; 
		}
		//alert(e.message);
	}
	return value;
}

/*鍚憆ecord涓煇瀛楁璧嬪��**/
function record_setValue(fieldName, value)
{
	try
	{
		_record_setValue(this, fieldName, value);
	}
	catch(e)
	{
		if(window.Android!=null){//鍦ㄥ畨鍗撲腑鐨勪俊鎭彁绀鸿鐢ㄨ繖鏍峰仛
			window.Android.showToast(e.message);
		}else{
			var parameters =e.message;
			// parameters =escape(parameters).replace(/%u/gi,'\\u');
			document.location="objc::showToast::"+parameters; 
		}
		//alert(e.message);
	}
}

/*鍚憆ecord涓煇瀛楁璧嬪�� 杈呭姪鍑芥暟**/
function _record_setValue(record, fieldName, value)
{
	
	var dataset=record.dataset;
	var fields=record.fields;
	var fieldIndex=-1;//涓嬫爣
	if (typeof(fieldName)=="number")
	{
		fieldIndex=fieldName;
	}
	else if (typeof(fieldName)=="string")
	{
		fieldIndex=fields["_index_"+fieldName.toLowerCase()];
	}

	if (typeof(fields[fieldIndex])=="undefined")
	{
		throw new Error(CANNOT_FIND_FIELD_FROM_RECORD);
	}

	var field=fields[fieldIndex];
	
	switch (field.dataType)
	{
		case "byte":
		case "short":
		case "int":
		case "long":
			break;
		case "float":;
		case "double":;
		case "bigdecimal":
			break;
		case "date":
		case "datetime":
		case "time":
			value=value.replace(/\./g, "/");
			value=new Date(value);
		case "boolean":
			break;
		case "string":
		break;	
	}
	record[fieldIndex]=value;
	dataset.modified=true;
}

/*鏍规嵁瀛楁鍚嶅瓧寰楀埌瀛楁鐨勪笅鏍�**/
function record_getIndexOfField(fieldName)
{
	_record_getIndexOfField(this,fieldName)
}

/*鏍规嵁瀛楁鍚嶅瓧寰楀埌瀛楁鐨勪笅鏍�  杈呭姪鍑芥暟**/
function _record_getIndexOfField(record,fieldName)
{
	var fields=record.fields;
	var fieldIndex=-1;//涓嬫爣
	if (typeof(fieldName)=="number")
	{
		fieldIndex=fieldName;
	}
	else if (typeof(fieldName)=="string")
	{
		fieldIndex=fields["_index_"+fieldName.toLowerCase()];
	}
	
	return fieldIndex;
}

/////////////////////////record鎵�鏈夋柟娉曠殑鍏蜂綋瀹炵幇  缁撴潫/////////////////////////////////////////////