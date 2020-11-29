//获取锁的产品ID，用户ID和硬件ID
function Arm_GetDongleInfo(flag){
	var rtn="";
	var Index = Arm_Enum();	//锁的个数，一次只支持插一把锁绑定
	if(Index>1){
		if(flag==1){
			Ext.showAlert("不支持多个锁同时绑定，请依次插入锁进行绑定操作！");
			return "";
		}else if(flag==0){
			Ext.showAlert("检测到多个锁，系统仅支持一个用户操作一把锁！");
			return "";
		}
	}
	if(Index==0){
		Ext.showAlert("没有检测到有效的锁！",function(){
			return "";
		});
	}
	var DongleInfoNum = 2;	//硬件ID
	if(Index>0){
		if(!websock){
			ctrl = document.getElementById("ctrl");
			rtn = ctrl.Arm_GetDongleInfo(Index-1, DongleInfoNum);
		}
		else{
			ctrl.Arm_GetDongleInfo(function(result, response){
				if (!result){
					Ext.showAlert("Arm_GetDongleInfo error. " + response);
				}else{
	        		rtn = response;
				}
			})	
		}
	}
	return rtn;
}
//查找RockeyArm
function Arm_Enum(){
	var rtn = 0;
	if(!websock){
		ctrl = document.getElementById("ctrl");
		rtn = ctrl.Arm_Enum();
		if((rtn+'').length==10){
			rtn=0;
		}
	}
	else{
		ctrl.Arm_Enum(function(result, response){
			if (!result){
				Ext.showAlert("Arm_Enum error. " + response);
			}else{
        		rtn = response;
        		if(rtn<0){
        			rtn=0
        		}
			}
		})	
	}
	return rtn;
}
//写入锁内数据区数据
function Arm_WriteData(Offset,value){
	var Handle = ArmHandle;
	var rtn = -1;
	//var Offset = 2048;		//起始偏移 存储到2049~4096 区段
	//此处写入的数据是经过base64编码后直接传入，也可使用base64.js文件中encode和decode接口进行编码解码传递数据
	var DataInput = b.encode(value);
	if(!websock){
		ctrl = document.getElementById("ctrl");
		rtn = ctrl.Arm_WriteData(parseInt(Handle), parseInt(Offset), DataInput);
		if(0 == rtn){
			Ext.showAlert("绑定U盾成功");
		}else{
			//Ext.showAlert(rtn);
		}
	}
	else{
		ctrl.Arm_WriteData(function(result,response){
			if(!result){
				Ext.showAlert("Arm_WriteData error. " + response);
			}else{
				rtn = response;
				if(0 == rtn){
					Ext.showAlert("绑定U盾成功");
				}else{
					//Ext.showAlert(rtn);
				}
			}
		})
	}
}
//打开RockeyArm，索引值从0开始
function Arm_Open(){
	var Index = Arm_Enum();	
	if(!websock){
		ctrl = document.getElementById("ctrl");
		ArmHandle = ctrl.Arm_Open(Index-1);
	}
	else{
		ctrl.Arm_Open(function(result, response){
			if (!result){
				Ext.showAlert("Arm_Open error. " + response);
			}else{
				ArmHandle = response;
			}
		})
	}
}
//读取锁内数据区数据
function Arm_ReadData(Offset,ReadLength){
	var Handle = ArmHandle;
	var rtn = "";
	if(!websock){
		ctrl = document.getElementById("ctrl");
		rtn = ctrl.Arm_ReadData(parseInt(Handle), parseInt(Offset),	(ReadLength));
	}
	else{
		ctrl.Arm_ReadData(function(result,response){
			if(!result){
				Ext.showAlert("Arm_ReadData error. " + response);
			}else{
				rtn = response;
			}
		})
	}
	return rtn;
}
//设置数据文件属性结构，用于创建文件第四个参数传入
function Arm_Set_DATA_FILE_ATTR(){
	var Size = 4096;	//数据文件长度
	var ReadPriv = 2;	//读权限，0为最小匿名权限，1为最小用户权限，2为最小开发商权限
	var WritePriv = 2;	//写权限
	var FileAttr = "";  //文件属性结构
	if(!websock){
		ctrl = document.getElementById("ctrl");
		FileAttr = ctrl.Arm_Set_DATA_FILE_ATTR(parseInt(Size), parseInt(ReadPriv), parseInt(WritePriv));
	}
	else{
		ctrl.Arm_Set_DATA_FILE_ATTR(function(result, response){
			if (!result){
				Ext.showAlert("Arm_Set_DATA_FILE_ATTR error. " + response);
			}else{
				FileAttr = response;
			}
		})
	}
	return FileAttr;
}
//创建文件
function Arm_CreateFile(FileAttr,FileID){
	var Handle = ArmHandle;
	var FileType = 1;
	var rtn = -1;
	if(FileAttr == null){
		Ext.showAlert("请设置文件属性");
		return;
	}
	var AttrBuffer = FileAttr;  
	if(!websock){
		ctrl = document.getElementById("ctrl");
		rtn = ctrl.Arm_CreateFile(parseInt(Handle), FileType, parseInt(FileID), AttrBuffer);  //上面得到的FileID为string类型，需要转一下
		if(0 == rtn){
			//alert("创建文件成功");
		}else{
			//Ext.showAlert(rtn);
		}
	}
	else{
		ctrl.Arm_CreateFile(function(result, response){
			if(!result){
				Ext.showAlert("Arm_CreateFile error. " + response);
			}else{
				rtn = response;
				if(0 == rtn){
					//alert("创建文件成功");
				}else{
					//Ext.showAlert(rtn);
				}
			}
		})
	}
}
//写文件
function Arm_WriteFile(value,FileID){
	var Handle = ArmHandle;
	var FileType = 1;	//普通数据文件
	var FileOffset = 0;
	var rtn = -1;
	var DataInput = b.encode(value);
	if(!websock){
		ctrl = document.getElementById("ctrl");
		rtn = ctrl.Arm_WriteFile(parseInt(Handle), FileType, parseInt(FileID), parseInt(FileOffset), DataInput);
		if(0 == rtn){
			//Ext.showAlert("写文件件成功");
		}else{
			//Ext.showAlert(rtn);
		}
	}
	else{
		ctrl.Arm_WriteFile(function(result, response){
			if(!result){
				Ext.showAlert("Arm_WriteFile error. " + response);
			}else{
				rtn = response;
				if(0 == rtn){
					//Ext.showAlert("写文件件成功");
				}else{
					//Ext.showAlert(rtn);
				}
			}
		})
	}
}
//关闭RockeyArm
function Arm_Close(){
	var Handle = ArmHandle;
	if(!websock){
		ctrl = document.getElementById("ctrl");
		rtn = ctrl.Arm_Close(parseInt(Handle));
		if(rtn == 0){
			//alert("关闭锁成功");
		}else{
			//alert(rtn);
		}
	}
	else{
		ctrl.Arm_Close(function(result, response){
			if (!result){
				Ext.showAlert("Arm_Close error. " + response);
			}else{
				rtn = response;
				if(rtn == 0){
					//alert("关闭锁成功");
				}else{
					//alert(rtn);
				}
			}
		})
	}
}
//读文件
function Arm_ReadFile(FileID,ReadLength,FileOffset){
	var Handle = ArmHandle;
	var rtn = "";
	if(!websock){
		ctrl = document.getElementById("ctrl");
		rtn = ctrl.Arm_ReadFile(parseInt(Handle), parseInt(FileID), parseInt(FileOffset), parseInt(ReadLength));
	}
	else{
		ctrl.Arm_ReadFile(function(result, response){
			if(!result){
				Ext.showAlert("Arm_ReadFile error. " + response);
			}else{
				rtn = response;
			}
		})
	}
	return rtn;
}
//删除文件
function Arm_DeleteFile(FileID){
	var Handle = ArmHandle;  
	var FileType = 1;
	if(!websock){
		ctrl = document.getElementById("ctrl");
		rtn = ctrl.Arm_DeleteFile(parseInt(Handle), FileType, parseInt(FileID));  
		if(0 == rtn){
			//alert("删除文件成功");
		}else{
			//alert(rtn);
		}
	}
	else{
		ctrl.Arm_DeleteFile(function(result,response){
			if(!result){
				Ext.showAlert("Arm_DeleteFile error. " + response);
			}else{
				rtn = response;
				if(0 == rtn){
					//alert("删除文件成功");
				}else{
					//alert(rtn);
				}
			}
		})
	}
}