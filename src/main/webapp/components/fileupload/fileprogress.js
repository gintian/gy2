/*
	A simple class for displaying file information and progress
	Note: This is a demonstration only and not part of SWFUpload.
	Note: Some have had problems adapting this class in IE7. It may not be suitable for your application.
*/

// Constructor
// file is a SWFUpload file object
// targetID is the HTML element id attribute that the FileProgress HTML structure will be added to.
// Instantiating a new FileProgress object with an existing file will reuse/update the existing DOM elements
/*
*	选择文件后执行的方法.
*	@param file 选择的文件对象，已经经过flash控件处理过了
*	@param fileListID WEB界面显示已选文件信息的位置区域
*	@param swfUploadInstance flash控件对象
*	@reference [handler.js].fileQueued(file)
*	@reference [handler.js].fileQueueError(file)
*/
function FileProgress(file,fileListID,swfUploadInstance) {
	if(!document.getElementById(file.id)){
		document.getElementById(fileListID+"Count").innerHTML=swfUploadInstance.getStats().files_queued;
		var tb = document.getElementById(fileListID);
		var tr = tb.insertRow(-1);
		tr.setAttribute("id",file.id);
		tr.className='';
		
		var td;
		
		td = tr.insertCell(-1);
		td.className='RecordRow';
		td.align='center';
		td.style.borderLeft="0px";
		td.innerHTML="<INPUT TYPE='checkbox' NAME='uploadCheckBox' disabled='disabled' id='"+file.id+"_checkbox' value='"+file.id+"' title='"+file.id+"'/><input type='hidden' name='hiddenvalue' id='"+file.id+"_hidden_value'/><input type='hidden' name='hiddenname' id='"+file.id+"_hidden_name'/>";
		
		td = tr.insertCell(-1);
		td.className='RecordRow';
		td.innerHTML="&nbsp;"+file.name;
		
		td = tr.insertCell(-1);
		td.className='RecordRow';
		if(file.size){
			td.innerHTML= "&nbsp;" + getNiceFileSize(file.size);
		}else{
			td.innerHTML= "&nbsp;" + "0B";
		}
		
		td = tr.insertCell(-1);
		td.className='RecordRow';
		td.innerHTML= "&nbsp;<div id="+file.id+"_bar_border_parent style='width:150px;height:15px;display:inline;'><div id='"+file.id+"_wait_upload' style='position: relative;float:left;width:50px;'>等待上传</div><div id="+file.id+"_bar_border style='position: relative;float:left;width:100px;height:15px;border-style:solid;border-width:1px;border-color:#84ADC9;display:none;'><span id="+file.id+"_bar style='height:15px;background-image:url(/images/shu_bg_bg.gif);'></span></div><div id="+file.id+"_bar_pens style='position: relative;float:left;width:30px;height:15px;'></div></div>";
		
		td = tr.insertCell(-1);
		td.className='RecordRow';
		td.style.borderRight="0px";
		td.innerHTML="&nbsp;" + "<a href='void();'><span id="+file.id+"_del style='cursor: pointer'>取消</span></a>";
		var delObject = document.getElementById(file.id+"_del");
		//对某个未上传文件，取消上传
		delObject.onclick = function () {
			swfUploadInstance.cancelUpload(file.id);
			var tr = document.getElementById(file.id);
			tr.style.color="red";
			var bar = document.getElementById(file.id+"_bar");
			var errInfo = "已取消";
			bar.parentNode.innerHTML=errInfo;
			var delObject = document.getElementById(file.id+"_del");
			delObject.parentNode.innerHTML="&nbsp;";
			document.getElementById(fileListID+"Count").innerHTML=swfUploadInstance.getStats().files_queued;
			
			var table = tr.parentElement;
			table.deleteRow(tr.rowIndex);
		};
		
		//alert(tb.innerHTML);
	}
	
	/* comment by stephen
	this.fileProgressID = file.id;

	this.opacity = 100;
	this.height = 0;

	this.fileProgressWrapper = document.getElementById(this.fileProgressID);
	if (!this.fileProgressWrapper) {
		this.fileProgressWrapper = document.createElement("div");
		this.fileProgressWrapper.className = "progressWrapper";
		this.fileProgressWrapper.id = this.fileProgressID;

		this.fileProgressElement = document.createElement("div");
		this.fileProgressElement.className = "progressContainer";

		var progressCancel = document.createElement("a");
		progressCancel.className = "progressCancel";
		progressCancel.href = "#";
		progressCancel.style.visibility = "hidden";
		progressCancel.appendChild(document.createTextNode(" "));

		var progressText = document.createElement("div");
		progressText.className = "progressName";
		progressText.appendChild(document.createTextNode(file.name));

		var progressBar = document.createElement("div");
		progressBar.className = "progressBarInProgress";

		var progressStatus = document.createElement("div");
		progressStatus.className = "progressBarStatus";
		progressStatus.innerHTML = "&nbsp;";

		this.fileProgressElement.appendChild(progressCancel);
		this.fileProgressElement.appendChild(progressText);
		this.fileProgressElement.appendChild(progressStatus);
		this.fileProgressElement.appendChild(progressBar);

		this.fileProgressWrapper.appendChild(this.fileProgressElement);

		document.getElementById(targetID).appendChild(this.fileProgressWrapper);
	} else {
		this.fileProgressElement = this.fileProgressWrapper.firstChild;
	}

	this.height = this.fileProgressWrapper.offsetHeight;
	*/

}
FileProgress.prototype.setProgress = function (percentage) {
	this.fileProgressElement.className = "progressContainer green";
	this.fileProgressElement.childNodes[3].className = "progressBarInProgress";
	this.fileProgressElement.childNodes[3].style.width = percentage + "%";
};
FileProgress.prototype.setComplete = function () {
	this.fileProgressElement.className = "progressContainer blue";
	this.fileProgressElement.childNodes[3].className = "progressBarComplete";
	this.fileProgressElement.childNodes[3].style.width = "";
	
	/* comment by stephen
	var oSelf = this;
	setTimeout(function () {
		oSelf.disappear();
	}, 10000);
	*/
};
FileProgress.prototype.setError = function () {
	this.fileProgressElement.className = "progressContainer red";
	this.fileProgressElement.childNodes[3].className = "progressBarError";
	this.fileProgressElement.childNodes[3].style.width = "";
	
	/* comment by stephen
	var oSelf = this;
	setTimeout(function () {
		oSelf.disappear();
	}, 5000);
	*/
};
FileProgress.prototype.setCancelled = function () {
	this.fileProgressElement.className = "progressContainer";
	this.fileProgressElement.childNodes[3].className = "progressBarError";
	this.fileProgressElement.childNodes[3].style.width = "";
	
	/* comment by stephen
	var oSelf = this;
	setTimeout(function () {
		oSelf.disappear();
	}, 2000);
	*/
};
FileProgress.prototype.setStatus = function (status) {
	this.fileProgressElement.childNodes[2].innerHTML = status;
};

// Show/Hide the cancel button
FileProgress.prototype.toggleCancel = function (show, swfUploadInstance) {
	this.fileProgressElement.childNodes[0].style.visibility = show ? "visible" : "hidden";
	if (swfUploadInstance) {
		var fileID = this.fileProgressID;
		this.fileProgressElement.childNodes[0].onclick = function () {
			swfUploadInstance.cancelUpload(fileID);
			return false;
		};
	}
};

// Fades out and clips away the FileProgress box.
FileProgress.prototype.disappear = function () {

	var reduceOpacityBy = 15;
	var reduceHeightBy = 4;
	var rate = 30;	// 15 fps

	if (this.opacity > 0) {
		this.opacity -= reduceOpacityBy;
		if (this.opacity < 0) {
			this.opacity = 0;
		}

		if (this.fileProgressWrapper.filters) {
			try {
				this.fileProgressWrapper.filters.item("DXImageTransform.Microsoft.Alpha").opacity = this.opacity;
			} catch (e) {
				// If it is not set initially, the browser will throw an error.  This will set it if it is not set yet.
				this.fileProgressWrapper.style.filter = "progid:DXImageTransform.Microsoft.Alpha(opacity=" + this.opacity + ")";
			}
		} else {
			this.fileProgressWrapper.style.opacity = this.opacity / 100;
		}
	}

	if (this.height > 0) {
		this.height -= reduceHeightBy;
		if (this.height < 0) {
			this.height = 0;
		}

		this.fileProgressWrapper.style.height = this.height + "px";
	}

	if (this.height > 0 || this.opacity > 0) {
		var oSelf = this;
		setTimeout(function () {
			oSelf.disappear();
		}, rate);
	} else {
		this.fileProgressWrapper.style.display = "none";
	}
};