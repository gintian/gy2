package com.hjsj.hrms.actionform.sys.options.param;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class SysParamForm extends FrameForm {
	/** 单位性质 */
	private String typeid;

	private ArrayList type_list = new ArrayList();
	
	private String[] typestr;

	/** 出国出境业务代码 */
	private String operationcode;

	private ArrayList codelist = new ArrayList();

	/** 业务子集 */
	private String goboardset;

/*	public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		// TODO Auto-generated method stub
		return super.validate(arg0, arg1);
	}*/
    private String path="";
	private ArrayList setlist = new ArrayList();
/**
 * formual参数设置
 */
	private ArrayList destbirthlist=new ArrayList();
	private ArrayList scrlist=new ArrayList();
	private ArrayList agelist=new ArrayList();
	private ArrayList axlist=new ArrayList();
	private String bycardnovalid;
	private String bycardnosrc;
	private String bycardnobirth;
	private String bycardnoage;
	private String bycardnoax;
	
	private String byworkvalid;
	private String byworksrc;
	private String byworkdest;

	private String byorgvalid;
	private String byorgsrc;
	private String byorgdest;
	
	private String cardflag;
	private String workflag;
	private String orgflag;
	private String edition;
	/**
	 * 身份证
	 */
	private String chk;
	//证件类型
	private String idType="";
	private String onlyname;
	private ArrayList chklist = new ArrayList();
	//证件类型可选列表
	private ArrayList idTypeList = new ArrayList();
	private String field_falg;
	private ArrayList dbprelist = new ArrayList();
	private String[] dbstr;
	private String[] validstr;
	private String chkcheck;
	private String uniquenesscheck;
	private String type="";
	
	//机构划转、合并、撤销人员变动信息模板设置
	private String transfer;
	private String transferview;
	private String combine;
	private String combineview;
	private String bolish;
	private String bolishview;
	private String del;
	private String delview;
	
	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}
	
	public ArrayList getIdTypeList() {
		return idTypeList;
	}

	public void setIdTypeList(ArrayList idTypeList) {
		this.idTypeList = idTypeList;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getField_falg() {
		return field_falg;
	}

	public void setField_falg(String field_falg) {
		this.field_falg = field_falg;
	}

	public ArrayList getChklist() {
		return chklist;
	}

	public void setChklist(ArrayList chklist) {
		this.chklist = chklist;
	}

	public ArrayList getCodelist() {
		return codelist;
	}

	public void setCodelist(ArrayList codelist) {
		this.codelist = codelist;
	}

	public String getGoboardset() {
		return goboardset;
	}

	public void setGoboardset(String goboardset) {
		this.goboardset = goboardset;
	}

	public String getOperationcode() {
		return operationcode;
	}

	public void setOperationcode(String operationcode) {
		this.operationcode = operationcode;
	}

	public ArrayList getSetlist() {
		return setlist;
	}

	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}

	public String getTypeid() {
		return typeid;
	}

	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}

	public ArrayList getType_list() {
		return type_list;
	}

	public void setType_list(ArrayList type_list) {
		this.type_list = type_list;
	}
	@Override
    public void outPutFormHM() {
		HashMap hm=this.getFormHM();
		this.setTypeid((String) this.getFormHM().get("typeid"));
		this.setType_list((ArrayList) this.getFormHM().get("type_list"));
		this.setOperationcode((String)this.getFormHM().get("operationcode"));
		this.setCodelist((ArrayList)this.getFormHM().get("codelist"));
		this.setGoboardset((String)this.getFormHM().get("goboardset"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		this.setScrlist((ArrayList) this.getFormHM().get("srclist"));
		this.setDestbirthlist((ArrayList) this.getFormHM().get("destlist"));
		this.setAgelist((ArrayList) this.getFormHM().get("agelist"));
		this.setBycardnoage((String) hm.get("bycardnoage"));
		this.setBycardnobirth((String) hm.get("bycardnobirth"));
		this.setBycardnosrc((String) hm.get("bycardnosrc"));
		this.setBycardnovalid((String)hm.get("bycardnovalid"));
		this.setByorgdest((String)hm.get("byorgdest"));
		this.setByorgsrc((String) hm.get("byorgsrc"));
		this.setByorgvalid((String) hm.get("byorgvalid"));
		this.setByworkdest((String)hm.get("byworkdest"));
		this.setByworksrc((String)hm.get("byworksrc"));
		this.setByworkvalid((String)hm.get("byworkvalid"));
		this.setCardflag((String) hm.get("cardflag"));
		this.setWorkflag((String) hm.get("workflag"));
		this.setOrgflag((String) hm.get("orgflag"));
		this.setAxlist((ArrayList) hm.get("axlist"));
		this.setBycardnoax((String) hm.get("bycardnoax"));
		this.setEdition((String)this.getFormHM().get("edition"));
		this.setTypestr((String[])this.getFormHM().get("typestr"));
		this.setChklist((ArrayList)this.getFormHM().get("chklist"));
		this.setChk((String)this.getFormHM().get("chk"));
		this.setIdType((String) this.getFormHM().get("idType"));
		this.setIdTypeList((ArrayList) this.getFormHM().get("idTypeList"));
		this.setOnlyname((String)this.getFormHM().get("onlyname"));
		this.setDbprelist((ArrayList)this.getFormHM().get("dbprelist"));
		this.setField_falg((String)this.getFormHM().get("field_falg"));
		this.setChkcheck((String)this.getFormHM().get("chkcheck"));
		this.setUniquenesscheck((String)this.getFormHM().get("uniquenesscheck"));
		this.setTransfer((String)this.getFormHM().get("transfer"));
		this.setTransferview((String)this.getFormHM().get("transferview"));
		this.setCombine((String)this.getFormHM().get("combine"));
		this.setCombineview((String)this.getFormHM().get("combineview"));
		this.setBolish((String)this.getFormHM().get("bolish"));
		this.setBolishview((String)this.getFormHM().get("bolishview"));
		this.setDel((String)this.getFormHM().get("delete"));
		this.setDelview((String)this.getFormHM().get("deleteview"));
	}

	@Override
    public void inPutTransHM() {
		HashMap hm=this.getFormHM();
		this.getFormHM().put("typeid", this.getTypeid());
		this.getFormHM().put("goboardset",this.getGoboardset());
		this.getFormHM().put("operationcode",this.getOperationcode());
		hm.put("bycardnoage",this.getBycardnoage());
		hm.put("bycardnobirth",this.getBycardnobirth());
		hm.put("bycardnosrc",this.getBycardnosrc());
		hm.put("bycardnovalid",this.getBycardnovalid());
		hm.put("byorgdest",this.getByorgdest());
		hm.put("byorgsrc",this.getByorgsrc());
		hm.put("byorgvalid",this.getByorgvalid());
		hm.put("byworkdest",this.getByworkdest());
		hm.put("byworksrc",this.getByworksrc());
		hm.put("byworkvalid",this.getByworkvalid());
		hm.put("bycardnoax",this.getBycardnoax());
		hm.put("path",this.getPath());
		hm.put("typestr",this.getTypestr());
		hm.put("chk",this.getChk());
		hm.put("idType",this.getIdType());
		hm.put("idTypeList",this.getIdTypeList());
		hm.put("onlyname",this.getOnlyname());
		hm.put("dbstr",this.getDbstr());
		hm.put("field_falg",this.getField_falg());
		hm.put("validstr",this.getValidstr());
		hm.put("type", this.getType());
		hm.put("transfer", transfer);
		hm.put("combine", combine);
		hm.put("bolish", bolish);
		hm.put("delete", del);
	}
	
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		this.typestr=new String[0];
		this.dbstr=new String[0];
		this.validstr=new String[0];
	}

	public ArrayList getAgelist() {
		return agelist;
	}

	public void setAgelist(ArrayList agelist) {
		this.agelist = agelist;
	}

	public String getBycardnoage() {
		return bycardnoage;
	}

	public void setBycardnoage(String bycardnoage) {
		this.bycardnoage = bycardnoage;
	}

	public String getBycardnobirth() {
		return bycardnobirth;
	}

	public void setBycardnobirth(String bycardnobirth) {
		this.bycardnobirth = bycardnobirth;
	}

	public String getBycardnosrc() {
		return bycardnosrc;
	}

	public void setBycardnosrc(String bycardnosrc) {
		this.bycardnosrc = bycardnosrc;
	}

	public String getBycardnovalid() {
		return bycardnovalid;
	}

	public void setBycardnovalid(String bycardnovalid) {
		this.bycardnovalid = bycardnovalid;
	}

	public String getByorgdest() {
		return byorgdest;
	}

	public void setByorgdest(String byorgdest) {
		this.byorgdest = byorgdest;
	}

	public String getByorgsrc() {
		return byorgsrc;
	}

	public void setByorgsrc(String byorgsrc) {
		this.byorgsrc = byorgsrc;
	}

	public String getByorgvalid() {
		return byorgvalid;
	}

	public void setByorgvalid(String byorgvalid) {
		this.byorgvalid = byorgvalid;
	}

	public String getByworkdest() {
		return byworkdest;
	}

	public void setByworkdest(String byworkdest) {
		this.byworkdest = byworkdest;
	}

	public String getByworksrc() {
		return byworksrc;
	}

	public void setByworksrc(String byworksrc) {
		this.byworksrc = byworksrc;
	}

	public String getByworkvalid() {
		return byworkvalid;
	}

	public void setByworkvalid(String byworkvalid) {
		this.byworkvalid = byworkvalid;
	}

	public ArrayList getDestbirthlist() {
		return destbirthlist;
	}

	public void setDestbirthlist(ArrayList destbirthlist) {
		this.destbirthlist = destbirthlist;
	}

	public ArrayList getScrlist() {
		return scrlist;
	}

	public void setScrlist(ArrayList scrlist) {
		this.scrlist = scrlist;
	}

	public String getCardflag() {
		return cardflag;
	}

	public void setCardflag(String cardflag) {
		this.cardflag = cardflag;
	}

	public String getOrgflag() {
		return orgflag;
	}

	public void setOrgflag(String orgflag) {
		this.orgflag = orgflag;
	}

	public String getWorkflag() {
		return workflag;
	}

	public void setWorkflag(String workflag) {
		this.workflag = workflag;
	}

	public ArrayList getAxlist() {
		return axlist;
	}

	public void setAxlist(ArrayList axlist) {
		this.axlist = axlist;
	}

	public String getBycardnoax() {
		return bycardnoax;
	}

	public void setBycardnoax(String bycardnoax) {
		this.bycardnoax = bycardnoax;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	/*this.getServlet().getServletContext().getRealPath("/js");*/
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		//String pajs=arg1.getSession().getServletContext().getRealPath("/js");
		//this.setPath(pajs);
		return super.validate(arg0, arg1);
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String[] getTypestr() {
		return typestr;
	}

	public void setTypestr(String[] typestr) {
		this.typestr = typestr;
	}

	public String getChk() {
		return chk;
	}

	public void setChk(String chk) {
		this.chk = chk;
	}

	public String getOnlyname() {
		return onlyname;
	}

	public void setOnlyname(String onlyname) {
		this.onlyname = onlyname;
	}

	public ArrayList getDbprelist() {
		return dbprelist;
	}

	public void setDbprelist(ArrayList dbprelist) {
		this.dbprelist = dbprelist;
	}

	public String[] getDbstr() {
		return dbstr;
	}

	public void setDbstr(String[] dbstr) {
		this.dbstr = dbstr;
	}

	public String getChkcheck() {
		return chkcheck;
	}

	public void setChkcheck(String chkcheck) {
		this.chkcheck = chkcheck;
	}

	public String getUniquenesscheck() {
		return uniquenesscheck;
	}

	public void setUniquenesscheck(String uniquenesscheck) {
		this.uniquenesscheck = uniquenesscheck;
	}

	public String[] getValidstr() {
		return validstr;
	}

	public void setValidstr(String[] validstr) {
		this.validstr = validstr;
	}

	public String getTransfer() {
		return transfer;
	}

	public void setTransfer(String transfer) {
		this.transfer = transfer;
	}

	public String getTransferview() {
		return transferview;
	}

	public void setTransferview(String transferview) {
		this.transferview = transferview;
	}

	public String getCombine() {
		return combine;
	}

	public void setCombine(String combine) {
		this.combine = combine;
	}

	public String getCombineview() {
		return combineview;
	}

	public void setCombineview(String combineview) {
		this.combineview = combineview;
	}

	public String getBolish() {
		return bolish;
	}

	public void setBolish(String bolish) {
		this.bolish = bolish;
	}

	public String getBolishview() {
		return bolishview;
	}

	public void setBolishview(String bolishview) {
		this.bolishview = bolishview;
	}

	public String getDel() {
		return del;
	}

	public void setDel(String del) {
		this.del = del;
	}

	public String getDelview() {
		return delview;
	}

	public void setDelview(String delview) {
		this.delview = delview;
	}

}
