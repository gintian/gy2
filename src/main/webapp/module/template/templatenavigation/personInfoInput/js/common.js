var CW_Version="1.1.12.0411";
var CW_Undefine						=	100			//未定义错误号

//摄像头相关
var CW_ProgrameErr					=	-1;			//程序内部错误
var CW_couldntConnect				=	7;			//网络异常		
var CW_TimeOut						=	28	;		//网络超时
var CW_ERR_CameraNotOpen			=	9000;		//摄像头未打开
var CW_ERR_CameraOpenError			=	9001;		//摄像头打开失败
var CW_ERR_CameraOpenAdy			=	9002;		//摄像头已经打开

var CW_ERR_InitSDK					=	9100;		//初始化SDK失败
var CW_BestFaceNull					=	9200;		//最佳人脸为空

var CW_NoFace						=	9301;		//没有检测到人脸
var CW_LostFace						=	9302;		//人脸丢失
var CW_ShakeFace					=	9303;		//人脸晃动
var CW_DetectLieveTimeOut			=	9304;		//检测超时



//------------------------------------------------------------------- local
var EnumLocalLoginError				=	101;		//登陆错误
var EnumLocalfaceInfoError			=	102;		//人脸检测异常
var EnumLocalNoFace					=	103;		//没有检测到人脸
var EnumLocalMultiFace				=	104;		//检测到多个人脸
var EnumLocalStdImgNoFace			=	105;		//标准照未检测到人脸
var EnumLocalStdImgMultiFace		=	106;		//标准照检测到多个人脸
var EnumLocalFaceIsSmall			=	107;		//人脸太小。
var EnumLocalFaceIsBig				=	108;		//人脸太大。
var EnumLocalIsBright				=	109;		//人脸太亮
var EnumLocalIsDark					=	110;		//人脸太暗
var EnumLocalImgIsBlur				=	111;		//图片模糊。
var EnumLocalIsWearGlasses			=	112;		//带眼镜。
var EnumLocalImgIsPoor				=	113;		//图片质量差
var EnumLocalImgDataError			=	114;		//图片格式错误
var EnumLocalImgIsLarge				=	115;		//图片太大
var EnumLocalNewImgBufIsSmall		=	116;		//图片分配空间太小
var EnumLocalNoImg					=	117;		//没有图片缓存
var EnumLocalWriteFile				=	118;		//本地写图片错误
var EnumLocalImgRoi					=	119;		//图片设置ROI错误
var EnumLocalImgScale				=	120;		//图片缩放错误
var EnumLocalImgConvert				=	121;		//图片转换错误
var EnumLocalInit					=	122;		//本地初始化错误
var EnumLocalInitDect				=	123;        //本地检测初始化错误
var EnumLocalInitKey				= 	124;        //本地关键点初始化错误
var EnumLocalInitQau				= 	125;        //本地质量评估初始化错误
var EnumLocalUnInit					= 	126;        //本地释放错误
var EnumLocalHttpInit				=	127;		//本地HTTP初始化错误
var EnumLocalHttpUnInit				=	128;		//本地HTTP释放错误
var  EnumLocalNoStartA				= 	129;        //本地没有启动分析
var EnumLocalIdFindError			=	130;		//本地身份证识别错误


var EnumLocalVideoBufNoFace 		=	200;		//图片队列为空
var EnumLocalVideoImgIsLarge		=	201;		//视频尺寸太大
var EnumLocalDataError				=	202;		//数据错误

var EnumLocalKeyPoint				=	300;		//本地关键点解析错误

var EnumLocalRegUserIDorPwd 		=	400;		//注册用户名或密码错误

var EnumLocalMsgFaceIsSmall 		=	500;		//请靠近屏幕
var EnumLocalMsgFaceStand			=	501;		//请对正取景框
var EnumLocalMsgSymScore			=	502;		//请确保脸上光照均匀
var EnumLocalMsgWearGlasses			=	503;		//如果您戴了眼镜,请摘掉

var EnumLocalProcessBusy			=	600;		//本地处理忙

var EnumLocalDetectMouthOpen		=	700;		//检查到张嘴
var EnumLocalDetectMouthOpenTimeOut	=	701;		//检查到张嘴超时
var EnumLocalHeadRotateDetectPitchU	=	702;		//检查到人脸仰
var EnumLocalHeadRotateDetectPitchD	=	703;		//检查到人脸俯
var EnumLocalHeadRotateDetectYawL	=	704;		//检查到左摇头
var EnumLocalHeadRotateDetectYawR	=	705;		//检查到右摇头
var EnumLocalHeadRotateDetectRollL	=	706;		//检查到向左偏
var EnumLocalHeadRotateDetectRollR	=	707;		//检查到向右偏
var EnumLocalHeadRotateDetect		=	708;		//检查到人脸俯仰不分左右
var EnumLocalHeadRotateDetectTimeOut=	709;		//检查到人脸偏转超时
var EnumLocalDetectBlinkEye			=	710;		//检查到眨眼
var EnumLocalBlinkEyeDetectTimeOut	=	711;		//检查眨眼超时

var EnumLocalNetError				=	800;		//网络异常。
var EnumLocalNetErrorUrl			=	803;		//URL格式不正确。
var EnumLocalNetErrorSerAddr		=	806;		//无法解析人脸识别服务主机地址。
var EnumLocalNetErrorConSer			=	807;		//无法连接到人脸识别服务主机。
var EnumLocalNetErrorTimeOut		=	828;		//访问人脸识别服务主机超时。
var EnumLocalNetErrorActData		=	890;		//无法解析人脸识别服务主机返回数据。


	//------------------------------------------------------------------- Engine
var EnumEngineUndefine				=	1000;

	//Face Detection
var EnumEngineNoFace				=	1100;		//没有检测到人脸
var EnumEngineMultiFace				=	1101;		//检测到多个人脸
var EnumEngineFaceIsSmall			=	1102;		//人脸太小
var EnumEngineFaceIsBig				=	1103;		//人脸太大

	//Face Quality
var EnumEngineIsBright				=	1200;		//人脸太亮
var EnumEngineIsDark				=	1201;		//人脸太暗
var EnumEngineIsBlur				=	1202;		//人脸图片模糊
var EnumEngineIsWearGlasses			=	1203;		//带眼镜

	//Face Image Data
var EnumEngineImgDataError			=	1301;		//图片数据错误
var EnumEngineImgIsLarge			=	1302;		//图片太大
var EnumEngineImgIsSmall			=	1303;		//图片太小
var EnumEngineBufferTooSmall		=	1304;		//图片缓冲区太小
	
	//算法相关
var EnumEngineGetOrigFeatureError	=	2000;		//提取原始特征错误
var EnumEngineOrigFeatureSizeError	=	2001;		//原始特征长度错误
var EnumEngineGetKeyPtError			=	2002;		//得到关键点错误
var EnumEngineGetPCAError			=	2003;		//降维错误
var EnumEnginePCAFeatureSizeError	=	2004;		//降维特征长度错误
var EnumEngineRecogError			=	2005;		//1:N识别错误
var EnumEngineSimilarityError		=	2006;		//1:1比对错误
	
	//1:N操作
var EnumEngineUserAlreadyRegistered	=	2100;		//用户已经存在
var EnumEngineUserNotExist			=	2101;		//用户不存在
var EnumEngineUserRegisterError		=	2102;		//用户注册错误
var EnumEngineUserCancellationError	=	2103; 		//用户注销错误
	
	//协议相关
var EnumEngineProtocolError			=	3000;		//协议内容错误
var EnumEngineInfoIncomplete		=	3001;		//信息不完整
var EnumEngineImgUploadError		=	3002;		//上传图片错误
var EnumEngineImgNotExist			=	3003;		//图片不存在

	//System
var EnumEngineTimeout				=	4000;		//服务器处理超时
var EnumEngineUnInitialized			=	4001;		//系统没有初始化
var EnumEngineBusy					=	4002;		//服务器繁忙
var EnumEngineParamBufferIsSmall	=	4003;		//函数返回值缓冲区太小

	//web
var EnumWebQuestPic					=	5000;		//服务器无法获取身份证照片
var EnumWebAccessDenied				=	5001;		//AppKey或Secret无效
var EnumWebDatabaseError			=	5002;		//数据库错误
var EnumSystemBusy					=	5003;		//服务器忙
var EnumAlgorithmFailed				=	5004;		//调用识别算法过程发生未知异常
var EnumWebInvalidURLParameters		=	5006;		//无效的URL参数
var EnumWebGrpcTimeOut				=	5005;		//远程调用超时
var EnumWebURLDownloadFailed		=	5007;		//下载URL图片失败
var EnumWebBase64Error				=	5008;		//Base64数据错误

var FILE_ALREAD_EXSIST				=	5501; 		// 文件已经存在
var FILE_OPEN_ERROR					=	5503; 		// 打开文件错误
var FILE_NOT_EXSIST					=	5503; 		// 文件不存在
var VALID_RECORD					=	5504; 		// 无效记录
var SECRET_ERROR					=	5505; 		// 密码错误
var OVER_COUNT						=	5506; 		// 超过次数
var OVER_TIME						=	5507; 		// 超过日期
var UKEY_MISSED						=	5508; 		// UKEY失效
var PARAMETER_ERROR					=	5509; 		// 参数错误

	//RegAndLoginByDB
var EnumWebByDbIdUserd				=	8000;       //账号已存在
var EnumWebByDbWriteDb				=	8001;       //保存数据库失败
var EnumWebByDbNoIdUserd			=	8002;       //账号已存在
var EnumWebByDbPwdError				=	8003;       //密码错误
var EnumWebByDbSessionError			=	8004;       //session错误

//身份证检测
var	CW_IDCARD_DET_INPUT_INVALID 	= 21100;  	// 输入参数非法
var	CW_IDCARD_DET_INPUT_UNRESOLVABLE= 21101;     // 输入图像数据解码失败
var	CW_IDCARD_DET_FAILED			= 21102;      // 身份证检测失败
var	CW_IDCARD_DET_OUTPUT_UNRESOLVABLE = 21103;    // 输出图像数据编码失败
var	CW_IDCARD_DET_OUTPUT_SIZE_UNMATCHED = 21104;  // 输出数据长度大于预设长度
var	CW_IDCARD_DET_UNKNOWN = 21105;                // 未知结果

//算法通用错误码
var CW_FACE_EMPTY_FRAME_ERR = 20000;			//空图像
var CW_FACE_UNAUTHORIZED_ERR = 20005;			// 未授权
var	CW_FACE_UNINITIALIZED_ERR = 20006;				// 尚未初始化
var	CW_FACE_BUILDIN_MODEL_ABSENCE = 20007;			// 没有内置模型

//云之眼
var CW_NORIGHT				= 4096;			//无权限
var CW_NOFACE_DTETCT		= 8464;			//没有人脸
var CW_FACENOTEXIT			= 12292;		//人脸不存在
var CW_IMGQUALITY_NOT_GOOD	= 65539;		//图片质量差


function showLoading(){
    document.getElementById("over").style.display = "block";
    document.getElementById("layout").style.display = "block";
}
		
function hideLoading(){
	document.getElementById("over").style.display = "none";
    document.getElementById("layout").style.display = "none";
}
		
function showMessage(message, el)
{
	if(message !== '')
	{
		el.innerHTML = "";
		var str;
		str = "&nbsp;&nbsp;&nbsp;&nbsp;<strong><font color='red'>";
		str += message;
		str += "</font><strong>";
		el.innerHTML = str;
	}
	else
	{
		el.innerHTML = "";
	}
}

function showMessage2(message, el)
{
	if(message !== '')
	{
		el.innerHTML = "";
		var str;
		str = "&nbsp;&nbsp;&nbsp;&nbsp;<strong><font color='green'>";
		str += message;
		str += "</font><strong>";
		el.innerHTML = str;
	}
	else
	{
		el.innerHTML = "";
	}
}

function showImg(ImgObj, imgPath, imgData)
{
	var browser = navigator.appName;
	if(browser == "Microsoft Internet Explorer")  
	{
		var b_version = navigator.appVersion;
		var version = b_version.split(";");   
		var trim_Version = version[1].replace(/[ ]/g, "");  
		//alert(trim_Version);
		if(trim_Version=="MSIE8.0")
		{
			if(imgPath.length > 0)
			{
				ImgObj.src = "file:///" + imgPath;//在服务器下无法显示，只能用于本地
			}				
			//ImgObj.src="data:image/jpeg;base64," + imgData;	
		}
		else if(trim_Version == "MSIE7.0")
		{
			if(imgPath.length > 0)
			{
				ImgObj.src=imgPath;
			}
		}
		else if(trim_Version=="MSIE6.0")
		{
			if(imgPath.length > 0)
			{
				ImgObj.src = imgPath;//在服务器下可以显示
			}			
		}
		else
		{
			//对于IE8,img.src的值最大为 32K, 如果超过该值，就会被截断; 而IE6不支持img.src
			//ImgObj.src = imgPath;
			ImgObj.src = "data:image/jpeg;base64," + imgData;		
		}
	}
	else
	{
		ImgObj.src = "data:image/jpeg;base64," + imgData;			
	}
}

function isIE6_8() 
{	
	//IE6-8判断(jquery 1.9.0 以上版本支持)
	if(!$.support.leadingWhitespace)
	{
		//alert("IE6-8");
		return 1;
	}

	//以下是IE6-8判断的另外一种方法（无需jquery）
	//var browser=navigator.appName; 
	//if(browser=="Microsoft Internet Explorer")  
	//{   
	//	var b_version=navigator.appVersion;
	//	var version=b_version.split(";");   
	//	var trim_Version=version[1].replace(/[ ]/g,"");  
	//	if(trim_Version=="MSIE8.0"){
	//		return 1;
	//	}else if(trim_Version=="MSIE6.0"){
	//		return 1;
	//	}
	//}
	return 0;
}

// 注册回调事件(插件名，插件提供的事件名称，调用的 JS 函数)
function registerCallBack(obj, name, proc) 
{
	if(typeof(proc) != "function")
		return;
		
	if(window.ActiveXObject || "ActiveXObject" in window) 
	{
		if (window.ActiveXObject && obj.attachEvent) 
		{
			obj.attachEvent(name, proc);
		} 
		else 
		{
			cloudWalk_AttachIE11Event(obj, name, proc);
		}
	} 
	else 
	{
		//on NetScape Browsers: firefox, chrome;
		obj[name] = proc;
		//alert("obj[" + name + "]: \n" + obj[name]);
	}
}

// IE11注册回调事件
function cloudWalk_AttachIE11Event(obj, _strEventId, _functionCallback)
{
	var nameFromToStringRegex = /^function\s?([^\s(]*)/;
	var paramsFromToStringRegex = /\(\)|\(.+\)/;
	var params = _functionCallback.toString().match(paramsFromToStringRegex)[0];
	var functionName = _functionCallback.name || _functionCallback.toString().match(nameFromToStringRegex)[1];
	var handler;
	try 
	{
		handler = document.createElement("script");
		handler.setAttribute("for", obj.id);
	} 
	catch (ex) 
	{
		handler = document.createElement('<script for="' + obj.id + '">');
	}
	handler.event = _strEventId + params;
	handler.appendChild(document.createTextNode(functionName + params + ";"));
	document.body.appendChild(handler);
}

function num(i) {
	var n = i.toFixed(2);
	return n;
}


/** 音乐播放器
 * @param obj  播放器id
 * @param file  音频文件 mp3: ogg:
 * @param loop  是否循环
 */
function audioplayer(id, file, loop)
{
	var audioplayer = document.getElementById(id);
	if (audioplayer != null)
	{
		document.body.removeChild(audioplayer);
	}
	if(typeof(file) != 'undefined')
	{
		if(navigator.userAgent.indexOf("MSIE") > 0)
		{
			// IE
			var player = document.createElement('bgsound');
			player.id = id;
			player.src = file['mp3'];
			player.setAttribute('autostart', 'true');
			if(loop)
			{
				player.setAttribute('loop', 'infinite');
			}
			document.body.appendChild(player);
		}
		else
		{
			// Other FF Chome Safari Opera
			var player = document.createElement('audio');
			player.id = id;
			player.setAttribute('autoplay', 'autoplay');
			if(loop)
			{
				player.setAttribute('loop', 'loop');
			}
			document.body.appendChild(player);
			var mp3 = document.createElement('source');
			mp3.src = file['mp3'];
			mp3.type = 'audio/mpeg';
			player.appendChild(mp3);
			var ogg = document.createElement('source');
			ogg.src = file['ogg'];
			ogg.type = 'audio/ogg';
			player.appendChild(ogg);
		}
	}
}
