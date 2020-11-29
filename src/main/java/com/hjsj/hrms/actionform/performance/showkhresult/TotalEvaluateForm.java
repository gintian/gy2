package com.hjsj.hrms.actionform.performance.showkhresult;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class TotalEvaluateForm extends FrameForm {
	private String    title="";
	private ArrayList totalEvaluateInfoList=new ArrayList();
	private ArrayList remarkList=new ArrayList();            //评语和意见列表
	private String    type="";								 // 5:饼图   11:直方图 
	@Override
    public void outPutFormHM() {
		this.setTotalEvaluateInfoList((ArrayList)this.getFormHM().get("totalEvaluateInfoList"));
		this.setTitle((String)this.getFormHM().get("title"));
		this.setRemarkList((ArrayList)this.getFormHM().get("remarkList"));
		this.setType((String)this.getFormHM().get("type"));
	}

	@Override
    public void inPutTransHM() {
		
	}

	public ArrayList getTotalEvaluateInfoList() {
		return totalEvaluateInfoList;
	}

	public void setTotalEvaluateInfoList(ArrayList totalEvaluateInfoList) {
		this.totalEvaluateInfoList = totalEvaluateInfoList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList getRemarkList() {
		return remarkList;
	}

	public void setRemarkList(ArrayList remarkList) {
		this.remarkList = remarkList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	

}
