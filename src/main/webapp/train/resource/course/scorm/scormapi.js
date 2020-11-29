
/**
*在SCORM标准中，SCO与LMS的通讯是由API Adapter来完成的。
*在SCORM1.2的标准中，API定义了8个主要 的function，分别是LMSInitilize, LMSFinish, LMSSetValue, 
*LMSGetValue, LMSCommit, LMSGetLastError, LMSGetErrorString, LMSGetDiagnostic，
*其中又以LMSGetValue和LMSSetValue最为复杂
*
*使用javascript实现scrom标准API
*/
function SCOAPI() {
	// 与LMS交互的链接
	this.servletURL = "";
	// 是否初始化
	this.isLMSInitialized = false;
	// 错误代码
	this.lmsErrorManager = new LMSErrorManager();
	// false字符窜
	this.strFalse = "false";
	// true字符窜
	this.strTrue = "true";
	// 数据map
	this.map = new ParameterSet();
	// 保存是否成功
	this.saveSucc = "false";
	// 是否保存学习进度,0为不保存，1为保存
	this.isLearn = "0";
	// 章节id
	this.scoId = "";
	// r5100，课件id
	this.r5100 = "";
	// 课程id
	this.r5000 = "";
	// 是否开启调试
	this.debug = false;
		
}

SCOAPI.prototype.setScoId = function(sco) {
	this.scoId=sco;
	this.map.setValue("scoId", this.scoId);
};

SCOAPI.prototype.setR5100 = function(r5100) {
	this.r5100 = r5100;
	this.map.setValue("r5100", this.r5100);
};

SCOAPI.prototype.setR5000 = function(r5000) {
	this.r5000 = r5000;
	this.map.setValue("r5000", this.r5000);
};
/**
*设置数据map
*/
//SCOAPI.prototype.setMap = 
function setMap(param) {
	if (param instanceof ParameterSet) {
		API.map = param;
	} else {
		API.lmsErrorManager.SetCurrentErrorCode("201");
	}
}


/**
*初始化与LMS交互的url
*/
SCOAPI.prototype.init = function (url) {
	this.servletURL = url;
};

/**
*LMSInitialize 负责启动SCO，当学习者进入开始阅读一个SCO时，
*SCO第一步就是先要呼叫LMSInitialize，LMSInitialize function判断该学员之上课记录，
*当学员第一次阅读该门课的该SCO时，LMSInitialize就会将设定初值至相关的环境变量；
*若学习者并不 是第一次阅读该SCO，LMSInitialize则必须将该学习者之前的上课记录取出，
*并存入环境变量中，如此即完成启动SCO之动作。
*/
SCOAPI.prototype.LMSInitialize = function(param) {

	if (this.debug) {
		alert("开始调用LMSInitilize方法");
		alert("参数为：----" + param + "----");
	}
	// 默认没有初始化
	var result = this.strFalse;
	
	// 如果传入参数，返回错误
	if (param) {
		this.lmsErrorManager.SetCurrentErrorCode("201");
		return result;
	}
	
	// 检测是否已经初始化
	if (this.isLMSInitialized) {
		this.lmsErrorManager.SetCurrentErrorCode("101");
	} else {
		if (this.debug) {
			alert("初始化方法----"+"调用ajax");
		}
		// ajax与LMS交互，取得数据
		this.ajaxLMS("get");
		// 清空错误
		this.lmsErrorManager.ClearCurrentErrorCode();
		// 已完成初始化
		this.isLMSInitialized = true;
		// 初始化成功
		result = this.strTrue;
	}
	
	return result;	
}


/**
*与LMS交互，保存获取数据，=set为保存，=get为获得
*/
SCOAPI.prototype.ajaxLMS = function(type) {
	if (this.debug) {
		alert("进入ajaxLMS方法");
	}
	// 是否保存进度
	this.map.setValue("isLearn", this.isLearn);
	
	
	if ("set" == type) {
		this.map.setValue("type",type);
		
		var request=new Request({method:'post',asynchronous:false,onSuccess:ajaxSucc,functionId:'2020030193'},this.map);
	} else if ("get" == type) {
		this.map.setValue("type",type);
		var request=new Request({method:'post',asynchronous:false,onSuccess:setMap,functionId:'2020030193'},this.map);
	} else {
		this.lmsErrorManager.SetCurrentErrorCode("201");
	}
}

/**
*与LMS交互成功
*/
//SCOAPI.prototype.ajaxSucc = 
function ajaxSucc(outParameters) {
	if (!outParameters.getValue("setResult") == "OK") {
		API.lmsErrorManager.SetCurrentErrorCode("101");
		API.saveSucc = API.strFalse;
	} else {
		if (API.debug) {
			alert("this.lmsErrorManager的值：" + API.lmsErrorManager);
		}
		API.lmsErrorManager.ClearCurrentErrorCode();
		API.saveSucc = API.strTrue;
    }
}
/**
*当学习者阅读完并要离开一个SCO时，在结束时SCO便会将呼叫LMSFinish，
*LMSFinish主要负责将环境变量重设，并判断该SCO是否在结束
*之前己经有呼叫LMSCommit将所有记录回存至LMS，若尚未储存，
*则会自动呼叫将所有学习者在该SCO的上课记录回存。
*/
SCOAPI.prototype.LMSFinish = function (param) {
	// 设置初始状态
	var result = this.strFalse;
      if (param == "" || param) {
         if (this.isLMSInitialized) {
            var strExit = this.map.getValue("cmi.core.exit");
            if (!strExit) {
            	strExit = "";
            }
            
            var strStatus = this.map.getValue("cmi.core.lesson_status");
            if (!strStatus) {
            	strStatus = "";
            }
            
            if (strExit.toLowerCase() == "suspend" ){
               this.map.setValue("cmi.core.entry","resume");
            } else {
               this.map.setValue("cmi.core.entry","");
            }

            if (strStatus.toLowerCase() == "not attempted" ){
               this.map.setValue("cmi.core.lesson_status","incomplete");
             
            }


            result = this.LMSCommit("");

            if ( result == this.strTrue ) {
               this.isLMSInitialized = false;
               result = this.strTrue;  
            }
         }
      } else {
         this.lmsErrorManager.SetCurrentErrorCode("201");
      }
      
      return result;
	
}

/**
*在 LMSSetValue是相当复杂的Function，负责储存所有相关的学习记录，
*当SCO呼叫欲将某个data model回存时，LMSSetValue第一步先判断所欲回存之data model，
*判断该data model是否可以set(写入)，其次判断其型别，当型别错误时，记录其Error Code，
*当型别检查通过时，则依SCORM 1.2 RTE所订定该data model的处理规则，并将数据存入内存中。
*/
SCOAPI.prototype.LMSSetValue = function (param, values) {
	var result = this.strFalse;

	this.lmsErrorManager.ClearCurrentErrorCode();

	if ( !this.isLMSInitialized) {
		return result;
	}
	
	// 处理null的情况
	if (!values) {
		values = "";
	}
	
	this.map.setValue(param,values);
    
    result = this.strTrue;


    return result;

	
}

/**
*LMSGetValue 主要负责将数据由LMS取出，当SCO呼叫LMSGetValue时，
*LMSSetValue会先判断data model是否可以读取，若不可读取，则写入其错误代码；
*若该data model是可以读取，则进取出其值并回传给SCO。
*但在设计时，如同LMSSetValue并没有直接和receiver相连，所以是将数据由暂存的内存中取出。
*/
SCOAPI.prototype.LMSGetValue = function (param) {
	if (!this.isLMSInitialized) {
		return "";
	}
	
	this.lmsErrorManager.ClearCurrentErrorCode();
	if (this.debug) {
		//alert(this.map.getValue(param));
	}
	if (this.map.getValue(param)) {
		return this.map.getValue(param);
	} else {
		return "";
	}
}

/**
*LMSCommit相对于LMSSetValue和LMSGetValue，LMSCommit可以说简单多了，
*其主要负责将所有暂存在内存中的学习记录，回存到LMS，在设 计时应用了XMLHTTP之技术，
*所以当LMSCommit被呼叫时，会将所有之暂存数据组成XML文件，
*再应用XMLHTTP对象将数据POST到 Receiver，当Receiver收到这个Request时，
*就会解译所传入之XML文件，再将XML文件中的数据直接存入数据库中。
*/
SCOAPI.prototype.LMSCommit = function (param) {
	var result = this.strFalse;
	if ( param == null || param == "" || (!param) ) {
		if ( !this.isLMSInitialized ) {
			return result;
		}

		this.ajaxLMS("set");
		result = this.saveSucc;
         
      } else {
         this.lmsErrorManager.SetCurrentErrorCode("201");
      }

      return result;
}

/**
*该函数将返回最后一个错误代码，每次API function呼叫后，
该函数的值将被重置。
*/
SCOAPI.prototype.LMSGetLastError = function () {
	return this.lmsErrorManager.GetCurrentErrorCode();
}

/**
*该函数将返回一个字符窜错误信息
*/
SCOAPI.prototype.LMSGetErrorString = function (param) {
	return this.lmsErrorManager.GetErrorDescription(param);
}

/**
*该函数将返回一个字符窜错误信息
*/
SCOAPI.prototype.LMSGetDiagnostic = function (param) {
	return this.lmsErrorManager.GetErrorDiagnostic(param); 
}



/*******************************************************
*
*scorm错误代码
*
********************************************************/
function LMSErrorManager() {
	this.currentErrorCode = "0";
	this.errors = [[ '0', 'No Error', 'The previous LMS API Function call completed successfully.' ], 
					[ "101", "General Exception", "An unspecified, unexpected exception has occured" ], 
					[ "201", "Invalid argument error", "" ], 
					[ "202", "Element cannot have children", "" ], 
					[ "203", "Element not an array - cannot have count", "" ], 
					[ "301", "Not initialized", "The LMS is not initialized." ], 
					[ "401", "Not implemented error", "The data model element in question was not implemented" ], 
					[ "402", "Invalid set value, element is a keyword", "Trying to set a reserved keyword in the data modelTrying to set a keyword (_count, _children, or _version) This is prohibited" ],
					[ "403", "Element is read only", "Data Element is Read Only (Not Writeable)Cannot call LMSSetValue() for the element in question" ], 
					[ "404", "Element is write only", "Data Element is Write Only (Not Readable)Cannot call LMSGetValue() for the element in question" ], 
					[ "405", "Incorrect Data Type", "Invalid Type being used for setting elementThe type being used as the set value argument does not match that of the element being set" ] 
					 ];
	
}


LMSErrorManager.prototype.GetCurrentErrorCode = function (){
    return this.currentErrorCode;
}

LMSErrorManager.prototype.SetCurrentErrorCode = function (paramString)
  {
    if (paramString) {
      this.currentErrorCode = paramString;
    } else {
      this.currentErrorCode = "0";
    }
  }

LMSErrorManager.prototype.ClearCurrentErrorCode = function () {
	this.currentErrorCode = this.errors[0][0];
}

LMSErrorManager.prototype.GetErrorDescription = function (paramString) {
    if (paramString) {
      return this.GetErrorElement(paramString)[1];
    }

    return "";
  }

LMSErrorManager.prototype.GetErrorDiagnostic = function (paramString) {
    if (paramString){
      return this.GetErrorElement(paramString)[2];
    }

    return this.GetErrorElement(currentErrorCode)[2];
  }

LMSErrorManager.prototype.GetErrorElement = function(paramString) {
    for (i = 0; i < this.errors.length; i++) {
      if (this.errors[i][0] == paramString)
        return this.errors[i];
    }

    var arrayOfString = [ "", "", "" ];
    return arrayOfString;
  }