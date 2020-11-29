/**
 * 支持IE控件的示例
 * 共通部分不需要修改，仅修改注释“调用控件接口的部分”之后的内容
 * 须在注释“调用控件接口的部分”之后加入对控件接口的转发调用
 */
 function AtlCtrlForRockeyArm(clsid) {
	// 共通部分不要修改
    this.ready_func_ = null;
    this.callbacks_ = new HashTable();
    this.module_ = "JS_IActiveXCtrl";
    if ('WebSocket' in window) {
    	this.ws = new WebSocket("ws://127.0.0.1:7321");
    }
    else{
    	throw "WebSocket not supported";
    }
	this.ws.onerror = function () {
		isSetUpAx=false;
		this.isConnect = false;
		throw "Unable to establish connection to WebSocket"; 
		}.bind(this);
	
	this.loadModule = function() {
    	var msg = JSON.stringify({
        	"MsgId": "LoadModule",
        	"Module": this.module_});
    	this.ws.onmessage = this._callback.bind(this);
    	this.ws.send(msg);
	};
	this.loadModuleCallBack = function(result, response) {
   	 	if (!result)
        	throw result.Response;
    	if (this.ready_func_) this.ready_func_();
	}.bind(this);
	
    this.callbacks_.add("LoadModule", this.loadModuleCallBack);
	
	this.exec = function(func, param, callback) {
        var msg_id = Object.id(callback).toString();
        this.callbacks_.add(msg_id, callback);
        var param_ = {
            "MsgId": msg_id,
            "Method": func + "|" + clsid
        };
        if (param) {
            param_["Param"] = JSON.stringify(param);
        }
        var msg = JSON.stringify(param_);
        this.ws.send(msg);
    }
    

    this.ready = function(func) {
        this.ready_func_ = func;
    }

    this._callback = function(response) {
        var r = JSON.parse(response.data);
        var msg_id = r.MsgId;
        if(r.Result){
            //console.info(r.Response);
        }else{
            //console.error(r.response);
        }
        if(!this.callbacks_.containsKey(msg_id)){
            return;
        }
        //console.log(this.callbacks_.getKeys());
        //console.log(msg_id);
        var callback = this.callbacks_.getValue(msg_id);
        callback(r.Result, r.Response);
        this.callbacks_.remove(msg_id);
    }

    this.ws.onopen = this.loadModule.bind(this);
    
    /** 
   	* 调用控件接口的部分
   	* 与test.html的调用名称相对应，以下均为示例
  	* exec第一个参数是 ActiveX控件 的接口名称，第二个参数是ActiveX控件的 参数 传入，第三个参数不需要修改
  	* 对每一个ActiveX控件接口都要写一个通过WebSocket调用的函数
  	*/  
  	
	
	this.Arm_Enum = function(callback){
    	this.exec("Arm_Enum", null, callback);
	  }  
	this.Arm_GetDongleInfo = function(callback){
    	this.exec("Arm_GetDongleInfo", [parseInt(Index), parseInt(DongleInfoNum)], callback);
	  }	    
	this.Arm_Open = function(callback){
    	this.exec("Arm_Open", [parseInt(Index)], callback);
	}
    this.Arm_Close = function(callback){
		this.exec("Arm_Close", [parseInt(Handle)], callback);
	}
	this.Arm_VerifyPIN = function(callback){
		this.exec("Arm_VerifyPIN", [parseInt(Handle), UserType, UserPin], callback);
	}
	this.Arm_ResetState = function(callback){
    	this.exec("Arm_ResetState", [parseInt(Handle)], callback);
	}
	this.Arm_GenRandom = function(callback){
		this.exec("Arm_GenRandom", [parseInt(Handle), parseInt(RandomLen)], callback);
	}
	this.Arm_LEDControl = function(callback){
    	this.exec("Arm_LEDControl", [parseInt(Handle), LED_flag], callback);
	}
	this.Arm_SwitchProtocol = function(callback){
    	this.exec("Arm_SwitchProtocol", [parseInt(Handle), protocol_flag], callback);
	}
	this.Arm_CreateFile = function(callback){
		this.exec("Arm_CreateFile", [parseInt(Handle), FileType, parseInt(FileID), AttrBuffer], callback);
	}
	this.Arm_WriteFile = function(callback){
		this.exec("Arm_WriteFile", [parseInt(Handle), FileType, parseInt(FileID), parseInt(FileOffset), DataInput], callback);
	}
	this.Arm_ReadFile = function(callback){
		this.exec("Arm_ReadFile", [parseInt(Handle), parseInt(FileID), parseInt(FileOffset), parseInt(ReadLength)], callback);
	}
	this.Arm_DownloadExeFile = function(callback){
    	this.exec("Arm_DownloadExeFile", [parseInt(Handle), parseInt(Count), ExeFileInfo], callback);
	}
	this.Arm_RunExeFile = function(callback){
		this.exec("Arm_RunExeFile", [parseInt(Handle), parseInt(FileID), InData, InData.length, parseInt(DataLen)], callback);
	}
	this.Arm_DeleteFile = function(callback){
		this.exec("Arm_DeleteFile", [parseInt(Handle), FileType, parseInt(FileID)], callback);
	}
	this.Arm_WriteData = function(callback){
		this.exec("Arm_WriteData", [parseInt(Handle), parseInt(Offset), DataInput], callback);
	}
	this.Arm_ReadData = function(callback){
		this.exec("Arm_ReadData", [parseInt(Handle), parseInt(Offset),	(ReadLength)], callback);
	}
	this.Arm_WriteShareMemory = function(callback){
		this.exec("Arm_WriteShareMemory", [parseInt(Handle), DataInput], callback);
	}
	this.Arm_ReadShareMemory = function(callback){
		this.exec("Arm_ReadShareMemory", [parseInt(Handle)], callback);
	}
	this.Arm_GenUniqueKey = function(callback){
		this.exec("Arm_GenUniqueKey", [parseInt(Handle), Seed.length, Seed], callback);
	}
	this.Arm_ChangePIN = function(callback){
		this.exec("Arm_ChangePIN", [parseInt(Handle), changePIN_flag, OldPIN, NewPIN, parseInt(TryCount)], callback);
	}
	this.Arm_ResetUserPIN = function(callback){
		this.exec("Arm_ResetUserPIN", [parseInt(Handle), AdminPIN], callback);
	}
	this.Arm_SetUserID = function(callback){
		this.exec("Arm_SetUserID", [parseInt(Handle), parseInt(UserID)], callback);
	}
    this.Arm_GetDeadline = function(callback){
    	this.exec("Arm_GetDeadline", [parseInt(Handle)], callback);
	}	
	this.Arm_SetDeadline = function(callback){
		this.exec("Arm_SetDeadline", [parseInt(Handle), parseInt(SetDeadTime)], callback);
	}	
    this.Arm_GetUTCTime = function(callback){
    	this.exec("Arm_GetUTCTime", [parseInt(Handle)], callback);
	}
    this.Arm_RFS = function(callback){
    	this.exec("Arm_RFS", [parseInt(Handle)], callback);
	}	
	this.Arm_RsaGenPubPriKey = function(callback){
		this.exec("Arm_RsaGenPubPriKey", [parseInt(Handle), parseInt(RsaFileId)], callback);
	}
	this.Arm_EccGenPubPriKey = function(callback){
	    this.exec("Arm_EccGenPubPriKey", [parseInt(Handle), parseInt(EccFileId)], callback);
	}
	this.Arm_Sm2GenPubPriKey = function(callback){
	    this.exec("Arm_Sm2GenPubPriKey", [parseInt(Handle), parseInt(Sm2FileId)], callback);
	}
	this.Arm_ReadRsaPri = function(callback){
    	this.exec("Arm_ReadRsaPri", null, callback);
	}
	this.Arm_ReadRsaPub = function(callback){
    	this.exec("Arm_ReadRsaPub", null, callback);
	}
	this.Arm_ReadEccPri = function(callback){
    	this.exec("Arm_ReadEccPri", null, callback);
	}
	this.Arm_ReadEccPub = function(callback){
    	this.exec("Arm_ReadEccPub", null, callback);
	}	
	this.Arm_ReadSm2Pri = function(callback){
    	this.exec("Arm_ReadSm2Pri", null, callback);
	}
	this.Arm_ReadSm2Pub = function(callback){
    	this.exec("Arm_ReadSm2Pub", null, callback);
	}
	this.Arm_RsaPri = function(callback){
		this.exec("Arm_RsaPri", [parseInt(Handle), parseInt(RsaPriFileID), parseInt(RsaPriFileSize), RsaPri_Flag, RsaPriInData], callback);
	}
	this.Arm_RsaPub = function(callback){
		this.exec("Arm_RsaPub", [parseInt(Handle), parseInt(RsaPubFileSize), RsaPub_Flag, RsaPubKey, RsaPubKey.length, RsaPubInData], callback);
	}
	this.Arm_EccSign = function(callback){
		this.exec("Arm_EccSign", [parseInt(Handle), parseInt(EccFileId), HashData], callback);
	}
	this.Arm_EccVerify = function(callback){
		this.exec("Arm_EccVerify", [parseInt(Handle), EccPubKey, EccPubKey.length, HashData, EccSignData], callback);
	}
	this.Arm_Sm2Sign = function(callback){
		this.exec("Arm_Sm2Sign", [parseInt(Handle), parseInt(Sm2FileId), HashData], callback);
	}
	this.Arm_Sm2Verify = function(callback){
		this.exec("Arm_Sm2Verify", [parseInt(Handle), Sm2PubKey, Sm2PubKey.length, HashData, Sm2SignData], callback);
	}
	this.Arm_TDES = function(callback){
		this.exec("Arm_TDES", [parseInt(Handle), parseInt(TdesFileID), Tdes_Flag, TdesInData], callback);
	}
	this.Arm_SM4 = function(callback){
		this.exec("Arm_SM4", [parseInt(Handle), parseInt(Sm4FileID), Sm4_Flag, Sm4InData], callback);
	}
	this.Arm_HASH = function(callback){
		this.exec("Arm_HASH", [parseInt(Handle), Hash_Flag, HashInData], callback);
	}
	this.Arm_Seed = function(callback){
		this.exec("Arm_Seed", [parseInt(Handle), SeedLength, SeedData], callback);
	}
	this.Arm_LimitSeedCount = function(callback){
		this.exec("Arm_LimitSeedCount", [parseInt(Handle), parseInt(SeedCount)], callback);
	}
	this.Arm_GenMotherKey = function(callback){
		this.exec("Arm_GenMotherKey", [parseInt(Handle), parseInt(SeedLen), SeedForPID, UserPIN, parseInt(UserTryCount), parseInt(AdminTryCount), UpdateRSAPriKey, parseInt(StartUserID), parseInt(SonCount)], callback);
	}
	this.Arm_RequestInit = function(callback){
    	this.exec("Arm_RequestInit", [parseInt(Handle)], callback);
	}
	this.Arm_GetInitDataFromMother = function(callback){
		this.exec("Arm_GetInitDataFromMother", [parseInt(Handle), Request], callback);
	}
	this.Arm_InitSon = function(callback){
		this.exec("Arm_InitSon", [parseInt(Handle), InitData], callback);
	}
	this.Arm_SetUpdatePriKey = function(callback){
		this.exec("Arm_SetUpdatePriKey", [parseInt(Handle), UpdatePriKey], callback);
	}
	this.Arm_MakeUpdatePacket = function(callback){
		this.exec("Arm_MakeUpdatePacket", [parseInt(Handle), HID, Func, FileType, parseInt(FileID), parseInt(Offset), DataBuffer, UpRSAPubKey, parseInt(DataLen)], callback);
	}
	this.Arm_MakeUpdatePacketFromMother = function(callback){
		this.exec("Arm_MakeUpdatePacketFromMother", [parseInt(Handle), HID, Func, FileType, parseInt(FileID), parseInt(Offset), DataBuffer, parseInt(DataLen)], callback);
	}
	this.Arm_Update = function(callback){
		this.exec("Arm_Update", [parseInt(Handle), UpdateData], callback);
	}
	this.Arm_Set_DATA_FILE_ATTR = function(callback){
		this.exec("Arm_Set_DATA_FILE_ATTR", [parseInt(Size), parseInt(ReadPriv), parseInt(WritePriv)], callback);	
	}
	this.Arm_Set_PRIKEY_FILE_ATTR = function(callback){
		this.exec("Arm_Set_PRIKEY_FILE_ATTR", [Type, parseInt(Size), parseInt(Count), parseInt(Priv), parseInt(IsDecOnRAM), parseInt(IsReset)], callback);
	}
	this.Arm_Set_KEY_FILE_ATTR = function(callback){
		this.exec("Arm_Set_KEY_FILE_ATTR", [parseInt(Size), parseInt(PrivEnc)], callback);
	}
	this.Arm_Set_EXE_FILE_ATTR = function(callback){
		this.exec("Arm_Set_EXE_FILE_ATTR", [parseInt(FileLen), parseInt(PrivExe)], callback);
	}
	this.Arm_Set_DATA_LIC = function(callback){
		this.exec("Arm_Set_DATA_LIC", [parseInt(ReadPriv), parseInt(WritePriv)], callback);
	}
	this.Arm_Set_PRIKEY_LIC = function(callback){
		this.exec("Arm_Set_PRIKEY_LIC", [parseInt(Count), parseInt(Priv), parseInt(IsDecOnRAM), parseInt(IsReset)], callback);
	}
	this.Arm_Set_KEY_LIC = function(callback){
		this.exec("Arm_Set_KEY_LIC", [parseInt(PrivEnc)], callback);
	}
	this.Arm_Set_EXE_LIC = function(callback){
		this.exec("Arm_Set_EXE_LIC", [parseInt(PrivExe)], callback);
	}
	this.Arm_Set_EXE_FILE_INFO = function(callback){
		this.exec("Arm_Set_EXE_FILE_INFO", [InBuffer, parseInt(FileSize), parseInt(FileID), parseInt(CallLimit), FileData, FileData.length], callback);
	}
	//释放可执行文件数据结构占用的内存
	this.Arm_Clear_EXE_FILE_INFO = function(callback){
		this.exec("Arm_Clear_EXE_FILE_INFO", [InBuffer], callback);
	}
	
};