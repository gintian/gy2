/* Demo Note:  This demo uses a FileProgress class that handles the UI for displaying the file name and percent complete.
The FileProgress class is not part of SWFUpload.
*/


/* **********************
   Event Handlers
   These are my custom event handlers to make my
   web application behave the way I went when SWFUpload
   completes different tasks.  These aren't part of the SWFUpload
   package.  They are part of my application.  Without these none
   of the actions SWFUpload makes will show up in my application.
   ********************** */
var loadFlag = true;
function preLoad() {
	if (!this.support.loading) {
		alert("你需要Flash Player 9.028或更高版本才能正常使用SWFUpload！");
		loadFlag = false;
		return false;
	}
}

function checkLoad() {
	return loadFlag;
}

function loadFailed() {
	alert("运行错误，文件加载失败！");
}

function fileQueued(file) {
	try {
		var progress = new FileProgress(file, this.customSettings.progressTarget,this.customSettings.obj);
		progress.setStatus("等待上传...");
		progress.toggleCancel(true, this);

	} catch (ex) {
		this.debug(ex);
	}

}

function fileQueueError(file, errorCode, message) {
	try {
		if (errorCode === SWFUpload.QUEUE_ERROR.QUEUE_LIMIT_EXCEEDED) {
			alert("您上传的文件过多！\n" + (message === 0 ? "您上传的文件数已达到最大值" : "您只能上传" + (message > 1 ? "" + message + " 文件！" : "一个文件！")));
			return;
		}

		var progress = new FileProgress(file, this.customSettings.progressTarget,this.customSettings.obj);
		progress.setError();
		progress.toggleCancel(false);

		switch (errorCode) {
		case SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT:
			progress.setStatus("文件太大。");//File is too big.
			this.debug("Error Code: File too big, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
			progress.setStatus("不能上传0 Byte文件。");//Cannot upload Zero Byte files.
			this.debug("Error Code: Zero byte file, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.QUEUE_ERROR.INVALID_FILETYPE:
			progress.setStatus("文件类型错误。");//Invalid File Type.
			this.debug("Error Code: Invalid File Type, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		default:
			if (file !== null) {
				progress.setStatus("上传错误。");//Unhandled Error
			}
			this.debug("Error Code: " + errorCode + ", File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		}
	} catch (ex) {
        this.debug(ex);
    }
}

function fileDialogComplete(numFilesSelected, numFilesQueued) {
	try {
		if (numFilesSelected > 0) {
			document.getElementById(this.customSettings.cancelButtonId).disabled = false;
			forbiddenButton(true,this.customSettings.obj.forbiddenButton);
		}
		
		/* I want auto start the upload and I can do that here */
		this.startUpload();
	} catch (ex)  {
        this.debug(ex);
	}
}

function uploadStart(file) {
	try {
		/* I don't want to do any file validation or anything,  I'll just update the UI and
		return true to indicate that the upload should start.
		It's important to update the UI here because in Linux no uploadProgress events are called. The best
		we can do is say we are uploading.
		 */
		var progress = new FileProgress(file, this.customSettings.progressTarget,this.customSettings.obj);
		progress.setStatus("0%");
		progress.toggleCancel(true, this);
		
		
	}
	catch (ex) {}
	
	return true;
}

function uploadProgress(file, bytesLoaded, bytesTotal) {
	try {
		var percent = Math.ceil((bytesLoaded / bytesTotal) * 100);

		var progress = new FileProgress(file, this.customSettings.progressTarget,this.customSettings.obj);
		progress.setProgress(percent);
		progress.setStatus(percent+"%");
	} catch (ex) {
		this.debug(ex);
	}
}

function uploadSuccess(file, serverData) {
	
	var rtnArr = serverData.split(",");
	var isSuccess = (rtnArr[0].indexOf("successed")==0?true:false);

	try {
		if (isSuccess) {
			var progress = new FileProgress(file, this.customSettings.progressTarget,this.customSettings.obj);
			progress.setComplete();
			progress.setStatus("上传成功");
			progress.toggleCancel(false);
			progress.fileProgressWrapper.innerHTML="上传成功!";
			
			var newPath = document.getElementById("newPathId");
			if(newPath)
				newPath.value=rtnArr[1];
			
			var imgde = document.getElementById(this.customSettings.obj.divId + "_delete_div");
			if (imgde) {
				imgde.style.display='none';
			}
		} else {
			var progress = new FileProgress(file, this.customSettings.progressTarget,this.customSettings.obj);
			progress.setComplete();
			progress.setStatus("上传失败");
			progress.toggleCancel(false);
			progress.fileProgressWrapper.innerHTML="上传失败!" + getDecodeStr(serverData);			
		}
		
		forbiddenButton(false,this.customSettings.obj.forbiddenButton);

	} catch (ex) {
		this.debug(ex);
	}
}

function forbiddenButton(flag,forbidden) {
		if (forbidden) {
			var buttons = forbidden.split(",");
			for (i = 0; i < buttons.length; i++) {
				var obj = document.getElementById(buttons[i]);
				if (obj) {
					obj.disabled = flag;
				}
			}
		}
}

function uploadError(file, errorCode, message) {
	try {
		var progress = new FileProgress(file, this.customSettings.progressTarget,this.customSettings.obj);
		progress.setError();
		progress.toggleCancel(false);
		
		switch (errorCode) {
		case SWFUpload.UPLOAD_ERROR.HTTP_ERROR:
			progress.setStatus("Upload Error: " + message);
			this.debug("Error Code: HTTP Error, File name: " + file.name + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_FAILED:
			progress.setStatus("Upload Failed.");
			this.debug("Error Code: Upload Failed, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.IO_ERROR:
			progress.setStatus("Server (IO) Error");
			this.debug("Error Code: IO Error, File name: " + file.name + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.SECURITY_ERROR:
			progress.setStatus("Security Error");
			this.debug("Error Code: Security Error, File name: " + file.name + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_LIMIT_EXCEEDED:
			progress.setStatus("Upload limit exceeded.");
			this.debug("Error Code: Upload Limit Exceeded, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.FILE_VALIDATION_FAILED:
			progress.setStatus("Failed Validation.  Upload skipped.");
			this.debug("Error Code: File Validation Failed, File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		case SWFUpload.UPLOAD_ERROR.FILE_CANCELLED:
			// If there aren't any files left (they were all cancelled) disable the cancel button
			if (this.getStats().files_queued === 0) {
				document.getElementById(this.customSettings.cancelButtonId).disabled = true;
			}
			progress.setStatus("取消上传");
			progress.setCancelled();
			break;
		case SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED:
			progress.setStatus("停止上传");
			break;
		default:
			progress.setStatus("Unhandled Error: " + errorCode);
			this.debug("Error Code: " + errorCode + ", File name: " + file.name + ", File size: " + file.size + ", Message: " + message);
			break;
		}
	} catch (ex) {
        this.debug(ex);
    }
    
    forbiddenButton(false,this.customSettings.obj.forbiddenButton);
}

function uploadComplete(file) {
	if (this.getStats().files_queued === 0) {
		document.getElementById(this.customSettings.cancelButtonId).disabled = true;
	}
}

// This event comes from the Queue Plugin
function queueComplete(numFilesUploaded) {
	var status = document.getElementById(this.customSettings.obj.divId + "_progress_div");
	//status.innerHTML = numFilesUploaded + " file" + (numFilesUploaded === 1 ? "" : "s") + " uploaded.";
	//status.innerHTML = "上传成功";
}


