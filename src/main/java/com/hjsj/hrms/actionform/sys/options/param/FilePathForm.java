package com.hjsj.hrms.actionform.sys.options.param;

import com.hrms.struts.action.FrameForm;

public class FilePathForm extends FrameForm {
	private String fileRootPath="";
	private String multimedia_maxsize="";
	private String asyn_maxsize="";
	private String doc_maxsize="";
	private String videostreams_maxsize="";
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("fileRootPath",this.getFileRootPath());
		this.getFormHM().put("multimedia_maxsize",this.getMultimedia_maxsize());
		this.getFormHM().put("asyn_maxsize",this.getAsyn_maxsize());
		this.getFormHM().put("doc_maxsize",this.getDoc_maxsize());
		this.getFormHM().put("videostreams_maxsize",this.getVideostreams_maxsize());
	}

	@Override
    public void outPutFormHM() {
		this.setFileRootPath((String)this.getFormHM().get("fileRootPath"));
		this.setMultimedia_maxsize((String)this.getFormHM().get("multimedia_maxsize"));
		this.setAsyn_maxsize((String)this.getFormHM().get("asyn_maxsize"));
		this.setDoc_maxsize((String)this.getFormHM().get("doc_maxsize"));
		this.setVideostreams_maxsize((String)this.getFormHM().get("videostreams_maxsize"));
	}
	public String getAsyn_maxsize() {
		return asyn_maxsize;
	}

	public void setAsyn_maxsize(String asyn_maxsize) {
		this.asyn_maxsize = asyn_maxsize;
	}
	public String getFileRootPath() {
		return fileRootPath;
	}

	public void setFileRootPath(String fileRootPath) {
		this.fileRootPath = fileRootPath;
	}

	public String getMultimedia_maxsize() {
		return multimedia_maxsize;
	}

	public void setMultimedia_maxsize(String multimedia_maxsize) {
		this.multimedia_maxsize = multimedia_maxsize;
	}

	public String getDoc_maxsize() {
		return doc_maxsize;
	}

	public void setDoc_maxsize(String doc_maxsize) {
		this.doc_maxsize = doc_maxsize;
	}

	public String getVideostreams_maxsize() {
		return videostreams_maxsize;
	}

	public void setVideostreams_maxsize(String videostreams_maxsize) {
		this.videostreams_maxsize = videostreams_maxsize;
	}

}
