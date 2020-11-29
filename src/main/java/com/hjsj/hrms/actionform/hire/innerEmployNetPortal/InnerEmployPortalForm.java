package com.hjsj.hrms.actionform.hire.innerEmployNetPortal;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;
import java.util.HashMap;

public class InnerEmployPortalForm extends FrameForm {
	private String    zpkA0100="";
	private String    zpDbName="";
	private ArrayList unitList=new ArrayList();
	private HashMap   unitPosMap=new HashMap();
	private ArrayList posDescFiledList=new ArrayList();  //职位详细信息 指标列表
	private ArrayList applyedPosList=new ArrayList();    //已申请的职位信息列表
	private String    posID="";                             //当前申请职位id
	private String    flag="";           //1:申请职位成功  2：已申请过该职位  3：已超过申请职位数 4：申请失败
	private String    isPosBooklet="0";  //是否有职位说明书
	private String    e01a1="";          //职位id
	private String posCount;
    private String alertMessage; 
    private  ArrayList attachList = new ArrayList();
    private FormFile attachFile = null;
    private ArrayList mediaList = new ArrayList();
    private String mediaId="";
    private String isSelfUser;
    private String fileName;
    private String i9999;
    private String type;
	@Override
    public void outPutFormHM() {
		this.setI9999((String)this.getFormHM().get("i9999"));
		this.setType((String)this.getFormHM().get("type"));
		this.setFileName((String)this.getFormHM().get("fileName"));
		this.setIsSelfUser((String)this.getFormHM().get("isSelfUser"));
		this.setMediaId((String)this.getFormHM().get("mediaId"));
		this.setMediaList((ArrayList)this.getFormHM().get("mediaList"));
		this.setAttachList((ArrayList)this.getFormHM().get("attachList"));
		this.setZpDbName((String)this.getFormHM().get("dbname"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setUnitList((ArrayList)this.getFormHM().get("unitList"));
		this.setUnitPosMap((HashMap)this.getFormHM().get("unitPosMap"));
		this.setPosDescFiledList((ArrayList)this.getFormHM().get("posDescFiledList"));
		this.setApplyedPosList((ArrayList)this.getFormHM().get("applyedPosList"));
		this.setPosID((String)this.getFormHM().get("posID"));
		this.setZpkA0100((String)this.getFormHM().get("zpkA0100"));
		
		this.setIsPosBooklet((String)this.getFormHM().get("isPosBooklet"));
		this.setE01a1((String)this.getFormHM().get("e01a1"));
		this.setPosCount((String)this.getFormHM().get("posCount"));
		this.setAlertMessage((String)this.getFormHM().get("alertMessage"));
		
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("zpkA0100", this.getZpkA0100());
		this.getFormHM().put("type", this.getType());
		this.getFormHM().put("i9999", this.getI9999());
		this.getFormHM().put("mediaId", this.getMediaId());
		this.getFormHM().put("posCount", this.getPosCount());
        this.getFormHM().put("attachFile", this.getAttachFile());
        this.getFormHM().put("fileName", this.getFileName());
	}

	public ArrayList getApplyedPosList() {
		return applyedPosList;
	}

	public void setApplyedPosList(ArrayList applyedPosList) {
		this.applyedPosList = applyedPosList;
	}

	public ArrayList getPosDescFiledList() {
		return posDescFiledList;
	}

	public void setPosDescFiledList(ArrayList posDescFiledList) {
		this.posDescFiledList = posDescFiledList;
	}

	public String getPosID() {
		return posID;
	}

	public void setPosID(String posID) {
		this.posID = posID;
	}

	public ArrayList getUnitList() {
		return unitList;
	}

	public void setUnitList(ArrayList unitList) {
		this.unitList = unitList;
	}

	public HashMap getUnitPosMap() {
		return unitPosMap;
	}

	public void setUnitPosMap(HashMap unitPosMap) {
		this.unitPosMap = unitPosMap;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getZpDbName() {
		return zpDbName;
	}

	public void setZpDbName(String zpDbName) {
		this.zpDbName = zpDbName;
	}

	public String getZpkA0100() {
		return zpkA0100;
	}

	public void setZpkA0100(String zpkA0100) {
		this.zpkA0100 = zpkA0100;
	}

	public String getE01a1() {
		return e01a1;
	}

	public void setE01a1(String e01a1) {
		this.e01a1 = e01a1;
	}

	public String getIsPosBooklet() {
		return isPosBooklet;
	}

	public void setIsPosBooklet(String isPosBooklet) {
		this.isPosBooklet = isPosBooklet;
	}

	public String getPosCount() {
		return posCount;
	}

	public void setPosCount(String posCount) {
		this.posCount = posCount;
	}

	public String getAlertMessage() {
		return alertMessage;
	}

	public void setAlertMessage(String alertMessage) {
		this.alertMessage = alertMessage;
	}

	public ArrayList getAttachList() {
		return attachList;
	}

	public void setAttachList(ArrayList attachList) {
		this.attachList = attachList;
	}

	public FormFile getAttachFile() {
		return attachFile;
	}

	public void setAttachFile(FormFile attachFile) {
		this.attachFile = attachFile;
	}

	public ArrayList getMediaList() {
		return mediaList;
	}

	public void setMediaList(ArrayList mediaList) {
		this.mediaList = mediaList;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getIsSelfUser() {
		return isSelfUser;
	}

	public void setIsSelfUser(String isSelfUser) {
		this.isSelfUser = isSelfUser;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getI9999() {
		return i9999;
	}

	public void setI9999(String i9999) {
		this.i9999 = i9999;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
