package com.hjsj.hrms.actionform.gz.premium.premium_allocate;

import com.hjsj.hrms.valueobject.ykcard.CardTagParamView;
import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>
 * Title:MonthPremiumForm.java
 * </p>
 * <p>
 * Description:部门月奖金管理
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-11-26 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class MonthPremiumForm extends FrameForm
{
    private ArrayList fieldlist = new ArrayList();

    private String sql = "";

    private String operOrg = "";

    private ArrayList operOrgList = new ArrayList();

    private String year = "";

    private String month = "";

    private String keep_save_field = "";// 封存字段

    private String orgsubset = "";

    private ArrayList formulaList = new ArrayList(); // 计算公式

    private String isTopOrg = "";// 是否顶层机构编码

    private String isDistribute = "0";// 是否可以下发

    private String dist_field = "";// 下发标识指标

    private String isKeepSave = "0";// 是否已经封存

    private String isCanReport = "0";// 是否可以上报

    private String paramStr = "";

    private CardTagParamView cardparam = new CardTagParamView();
    
    private String cardid = "";//下发奖金通知单
    
    private String isCanKeepSave = "0";// 是否可以封存    

    /** 导入文件 */
    private FormFile file;
    
    private ArrayList orgChilds = new ArrayList();
    
    private String salaryid="";//共享薪资类别
    
    private String isLeafOrg = "";// 是否叶子机构编码
    
    private String isOnlyLeafOrgs = "";// 是否所有操作单位都是叶子机构
    
    private String isAllDistri="0";//是否登录用户的所有操作单位都处于下发状态（顶层除外）\
    
    private String isGzManager = "0";//是否工资管理员
    
    private String isOrgCheckNo = "0";//是否当前机构不往下核算了
    
    @Override
    public void inPutTransHM()
    {
    this.getFormHM().put("isOrgCheckNo", this.getIsOrgCheckNo());
	this.getFormHM().put("isGzManager", this.getIsGzManager());
	this.getFormHM().put("cardid", this.getCardid());
	this.getFormHM().put("dist_field", this.getDist_field());
	this.getFormHM().put("fieldlist", this.getFieldlist());
	this.getFormHM().put("sql", this.getSql());
	this.getFormHM().put("operOrg", this.getOperOrg());
	this.getFormHM().put("operOrgList", this.getOperOrgList());
	this.getFormHM().put("year", this.getYear());
	this.getFormHM().put("month", this.getMonth());
	this.getFormHM().put("keep_save_field", this.getKeep_save_field());
	this.getFormHM().put("orgsubset", this.getOrgsubset());
	this.getFormHM().put("isTopOrg", this.getIsTopOrg());
	this.getFormHM().put("isDistribute", this.getIsDistribute());
	this.getFormHM().put("isKeepSave", this.getIsKeepSave());
	this.getFormHM().put("isCanReport", this.getIsCanReport());
	this.getFormHM().put("paramStr", this.getParamStr());
	this.getFormHM().put("cardparam", this.getCardparam());
	this.getFormHM().put("isCanKeepSave", this.getIsCanKeepSave());
	this.getFormHM().put("file", this.getFile());
	this.getFormHM().put("orgChilds", this.getOrgChilds());
	this.getFormHM().put("salaryid", this.getSalaryid());
	this.getFormHM().put("isLeafOrg", this.getIsLeafOrg());
	this.getFormHM().put("isOnlyLeafOrgs", this.getIsOnlyLeafOrgs());
	this.getFormHM().put("isAllDistri", this.getIsAllDistri());
    }

    @Override
    public void outPutFormHM()
    {
    this.setIsOrgCheckNo((String) this.getFormHM().get("isOrgCheckNo"));
	this.setIsGzManager((String) this.getFormHM().get("isGzManager"));
	this.setIsAllDistri((String) this.getFormHM().get("isAllDistri"));
	this.setIsOnlyLeafOrgs((String) this.getFormHM().get("isOnlyLeafOrgs"));
	this.setSalaryid((String) this.getFormHM().get("salaryid"));
	this.setFile((FormFile) this.getFormHM().get("file"));
	this.setIsCanKeepSave((String) this.getFormHM().get("isCanKeepSave"));
	this.setCardid((String) this.getFormHM().get("cardid"));
	this.setCardparam((CardTagParamView) this.getFormHM().get("cardparam"));
	this.setIsCanReport((String) this.getFormHM().get("isCanReport"));
	this.setIsKeepSave((String) this.getFormHM().get("isKeepSave"));
	this.setDist_field((String) this.getFormHM().get("dist_field"));
	this.setIsTopOrg((String) this.getFormHM().get("isTopOrg"));
	this.setIsDistribute((String) this.getFormHM().get("isDistribute"));
	this.setFieldlist((ArrayList) this.getFormHM().get("fieldlist"));
	this.setSql((String) this.getFormHM().get("sql"));
	this.setOperOrg((String) this.getFormHM().get("operOrg"));
	this.setOperOrgList((ArrayList) this.getFormHM().get("operOrgList"));
	this.setYear((String) this.getFormHM().get("year"));
	this.setMonth((String) this.getFormHM().get("month"));
	this.setKeep_save_field((String) this.getFormHM().get("keep_save_field"));
	this.setOrgsubset((String) this.getFormHM().get("orgsubset"));
	this.setFormulaList((ArrayList) this.getFormHM().get("formulaList"));
	this.setParamStr((String) this.getFormHM().get("paramStr"));
	this.setOrgChilds((ArrayList) this.getFormHM().get("orgChilds"));
	this.setIsLeafOrg((String) this.getFormHM().get("isLeafOrg"));
    }

    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {

	if ("/gz/premium/premium_allocate/monthPremiumList".equals(arg0.getPath()) && arg1.getParameter("br_premium") != null)
	{
	    cardparam.setPageid(0);
	}
	return super.validate(arg0, arg1);
    }

    public ArrayList getFieldlist()
    {

	return fieldlist;
    }

    public void setFieldlist(ArrayList fieldlist)
    {

	this.fieldlist = fieldlist;
    }

    public String getOperOrg()
    {

	return operOrg;
    }

    public void setOperOrg(String operOrg)
    {

	this.operOrg = operOrg;
    }

    public ArrayList getOperOrgList()
    {

	return operOrgList;
    }

    public void setOperOrgList(ArrayList operOrgList)
    {

	this.operOrgList = operOrgList;
    }

    public String getSql()
    {

	return sql;
    }

    public void setSql(String sql)
    {

	this.sql = sql;
    }

    public String getMonth()
    {

	return month;
    }

    public void setMonth(String month)
    {

	this.month = month;
    }

    public String getYear()
    {

	return year;
    }

    public void setYear(String year)
    {

	this.year = year;
    }

    public String getKeep_save_field()
    {

	return keep_save_field;
    }

    public void setKeep_save_field(String keep_save_field)
    {

	this.keep_save_field = keep_save_field;
    }

    public String getOrgsubset()
    {

	return orgsubset;
    }

    public void setOrgsubset(String orgsubset)
    {

	this.orgsubset = orgsubset;
    }

    public String getIsDistribute()
    {

	return isDistribute;
    }

    public void setIsDistribute(String isDistribute)
    {

	this.isDistribute = isDistribute;
    }

    public String getIsTopOrg()
    {

	return isTopOrg;
    }

    public void setIsTopOrg(String isTopOrg)
    {

	this.isTopOrg = isTopOrg;
    }

    public ArrayList getFormulaList()
    {

	return formulaList;
    }

    public void setFormulaList(ArrayList formulaList)
    {

	this.formulaList = formulaList;
    }

    public String getDist_field()
    {

	return dist_field;
    }

    public void setDist_field(String dist_field)
    {

	this.dist_field = dist_field;
    }

    public String getIsKeepSave()
    {

	return isKeepSave;
    }

    public void setIsKeepSave(String isKeepSave)
    {

	this.isKeepSave = isKeepSave;
    }

    public String getIsCanReport()
    {

	return isCanReport;
    }

    public void setIsCanReport(String isCanReport)
    {

	this.isCanReport = isCanReport;
    }

    public String getParamStr()
    {

	return paramStr;
    }

    public void setParamStr(String paramStr)
    {

	this.paramStr = paramStr;
    }

    public CardTagParamView getCardparam()
    {

	return cardparam;
    }

    public void setCardparam(CardTagParamView cardparam)
    {

	this.cardparam = cardparam;
    }

    public String getCardid()
    {
    
        return cardid;
    }

    public void setCardid(String cardid)
    {
    
        this.cardid = cardid;
    }

    public String getIsCanKeepSave()
    {
    
        return isCanKeepSave;
    }

    public void setIsCanKeepSave(String isCanKeepSave)
    {
    
        this.isCanKeepSave = isCanKeepSave;
    }

    public FormFile getFile()
    {
    
        return file;
    }

    public void setFile(FormFile file)
    {
    
        this.file = file;
    }

    public ArrayList getOrgChilds()
    {
    
        return orgChilds;
    }

    public void setOrgChilds(ArrayList orgChilds)
    {
    
        this.orgChilds = orgChilds;
    }

    public String getSalaryid()
    {
    
        return salaryid;
    }

    public void setSalaryid(String salaryid)
    {
    
        this.salaryid = salaryid;
    }

    public String getIsLeafOrg()
    {
    
        return isLeafOrg;
    }

    public void setIsLeafOrg(String isLeafOrg)
    {
    
        this.isLeafOrg = isLeafOrg;
    }



    public String getIsOnlyLeafOrgs()
    {
    
        return isOnlyLeafOrgs;
    }

    public void setIsOnlyLeafOrgs(String isOnlyLeafOrgs)
    {
    
        this.isOnlyLeafOrgs = isOnlyLeafOrgs;
    }

    public String getIsAllDistri()
    {
    
        return isAllDistri;
    }

    public void setIsAllDistri(String isAllDistri)
    {
    
        this.isAllDistri = isAllDistri;
    }

    public String getIsGzManager()
    {
    
        return isGzManager;
    }

    public void setIsGzManager(String isGzManager)
    {
    
        this.isGzManager = isGzManager;
    }

	public String getIsOrgCheckNo() {
		return isOrgCheckNo;
	}

	public void setIsOrgCheckNo(String isOrgCheckNo) {
		this.isOrgCheckNo = isOrgCheckNo;
	}
    
}
