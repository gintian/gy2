package com.hjsj.hrms.actionform.report.edit_report;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;
import java.util.HashMap;

public class SendReceiveForm extends FrameForm {

	private String[] check;//选中接收报表样式集合

	private String[] operate; //操作集合(覆盖/添加)

	private ArrayList voList;

	private String[] tabid;

	private FormFile file;  //上传报表样式文件对象

	private String[] selectTabid; //选中的报表集合

	private ArrayList selectVoList; //上传的报表样式文件中的报表信息集合

	private String flg = "";

	public SendReceiveForm() {
		super();
	}

	@Override
    public void outPutFormHM() {
		voList = (ArrayList) this.getFormHM().get("list");
		
		//报表样式文件中的报表列表
		selectVoList = (ArrayList) this.getFormHM().get("selectVoList");
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
	}

	@Override
    public void inPutTransHM() {
		// if (check != null) {
		// System.out.println(Arrays.asList(check));
		// System.out.println(Arrays.asList(operate));
		// }
		
		this.getFormHM().put("file", this.getFile()); //文件对象
		
		this.getFormHM().put("tabids", tabid); //
		
		this.getFormHM().put("selectTabid", selectTabid);//
		
		this.getFormHM().put("flg", flg);
		
		HashMap h = new HashMap();
		if (check != null) { //要上传的表样式文件中表ID集合
			
			String[] c = this.getCheck();//表ID集合
			String[] o = this.getOperate();//要执行的操作集合(添加/覆盖)
			
			for (int i = 0; i < c.length; i++) {
				h.put(c[i], o[i]);
			}
			
			this.getFormHM().put("map", h);
		}
	}

	public ArrayList getVoList() {
		return voList;
	}

	public void setVoList(ArrayList voList) {
		this.voList = voList;
	}

	public void setTabid(String[] tabid) {
		this.tabid = tabid;
	}

	public String[] getTabid() {
		return tabid;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String[] getSelectTabid() {
		return selectTabid;
	}

	public void setSelectTabid(String[] selectTabid) {
		this.selectTabid = selectTabid;
	}

	public ArrayList getSelectVoList() {
		return selectVoList;
	}

	public void setSelectVoList(ArrayList selectVoList) {
		this.selectVoList = selectVoList;
	}

	public String getFlg() {
		return flg;
	}

	public void setFlg(String flg) {
		this.flg = flg;
	}

	public String[] getCheck() {
		return check;
	}

	public void setCheck(String[] check) {
		this.check = check;
	}

	public String[] getOperate() {
		return operate;
	}

	public void setOperate(String[] operate) {
		this.operate = operate;
	}

}
