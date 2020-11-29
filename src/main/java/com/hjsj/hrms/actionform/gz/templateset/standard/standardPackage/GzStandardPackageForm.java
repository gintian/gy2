package com.hjsj.hrms.actionform.gz.templateset.standard.standardPackage;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * 
 *<p>Title:GzStandardPackageForm</p> 
 *<p>Description:薪资标准包(薪资历史沿革)</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 3, 2007:9:25:23 AM</p> 
 *@author dengcan
 *@version 4.0
 */
public class GzStandardPackageForm extends FrameForm {
	private PaginationForm standardPackagelistform=new PaginationForm();
	private String    startDate="";      //启动日期
	private String    resetName="";      //重命名
	
	private String    packName="";       //历史沿革名称
	private String    isStart="0";       // 是否启用  0：不启用  1：启用
	private ArrayList     standardList=new ArrayList();
	private ArrayList 	  currentStandardList=new ArrayList();   //当前标准列表
	private String[]  newStandards=null;                         //新建历史沿革标准
	private String    pkg_id="";
	private String    startUpIndex="";
	private FormFile file;
	
	public ArrayList gzStandardPackageInfo=new ArrayList();    //导出的工资标准列表
	public String[]  importStandardIds=null;
	
	@Override
    public void outPutFormHM() {
		this.setPkg_id((String)this.getFormHM().get("pkg_id"));
		this.setStandardList((ArrayList)this.getFormHM().get("standardList"));
		this.getStandardPackagelistform().setList((ArrayList)this.getFormHM().get("standardPackagelist"));
		this.getStandardPackagelistform().getCurrent();
		this.setCurrentStandardList((ArrayList)this.getFormHM().get("currentStandardList"));
		this.setStartUpIndex((String)this.getFormHM().get("startUpIndex"));
		/* 新建薪资标准历史沿革a，勾选启用后，进行保存；再次新建薪资标准历史沿革b，不勾选启用，进行保存，在列表中看到b被启用了 xiaoyun 2014-10-20 start */
		this.setIsStart((String)this.getFormHM().get("isStart"));
		/* 新建薪资标准历史沿革a，勾选启用后，进行保存；再次新建薪资标准历史沿革b，不勾选启用，进行保存，在列表中看到b被启用了 xiaoyun 2014-10-20 end */
		this.setGzStandardPackageInfo((ArrayList)this.getFormHM().get("gzStandardPackageInfo"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("importStandardIds",this.getImportStandardIds());
		this.getFormHM().put("file", this.getFile());
		
		this.getFormHM().put("selectedList",this.getStandardPackagelistform().getSelectedList());
		this.getFormHM().put("startDate",this.getStartDate());
		this.getFormHM().put("resetName",this.getResetName());
		this.getFormHM().put("packName",this.getPackName());
		this.getFormHM().put("isStart",this.getIsStart());
		//this.getFormHM().put("currentStandardList",this.getCurrentStandardList());
		this.getFormHM().put("newStandards",this.getNewStandards());
		
	}

	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if(arg1.getParameter("b_query")!=null&& "init".equals(arg1.getParameter("b_query")))
		{
			if(this.getStandardPackagelistform()!=null)
				this.getStandardPackagelistform().getPagination().firstPage();
		}
		return super.validate(arg0, arg1);
	}
	
	public PaginationForm getStandardPackagelistform() {
		return standardPackagelistform;
	}

	public void setStandardPackagelistform(PaginationForm standardPackagelistform) {
		this.standardPackagelistform = standardPackagelistform;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getResetName() {
		return resetName;
	}

	public void setResetName(String resetName) {
		this.resetName = resetName;
	}

	public ArrayList getCurrentStandardList() {
		return currentStandardList;
	}

	public void setCurrentStandardList(ArrayList currentStandardList) {
		this.currentStandardList = currentStandardList;
	}

	public String getIsStart() {
		return isStart;
	}

	public void setIsStart(String isStart) {
		this.isStart = isStart;
	}

	public String[] getNewStandards() {
		return newStandards;
	}

	public void setNewStandards(String[] newStandards) {
		this.newStandards = newStandards;
	}

	public String getPackName() {
		return packName;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public ArrayList getStandardList() {
		return standardList;
	}

	public void setStandardList(ArrayList standardList) {
		this.standardList = standardList;
	}

	public String getPkg_id() {
		return pkg_id;
	}

	public void setPkg_id(String pkg_id) {
		this.pkg_id = pkg_id;
	}

	public ArrayList getGzStandardPackageInfo() {
		return gzStandardPackageInfo;
	}

	public void setGzStandardPackageInfo(ArrayList gzStandardPackageInfo) {
		this.gzStandardPackageInfo = gzStandardPackageInfo;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String[] getImportStandardIds() {
		return importStandardIds;
	}

	public void setImportStandardIds(String[] importStandardIds) {
		this.importStandardIds = importStandardIds;
	}

	public String getStartUpIndex() {
		return startUpIndex;
	}

	public void setStartUpIndex(String startUpIndex) {
		this.startUpIndex = startUpIndex;
	}


}
