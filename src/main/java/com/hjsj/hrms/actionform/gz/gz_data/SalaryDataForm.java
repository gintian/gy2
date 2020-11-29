package com.hjsj.hrms.actionform.gz.gz_data;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;
/**
 *<p>Title:薪资数据</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2009-9-10:下午02:42:04</p> 
 *@author fanzhiguo
 *@version 4.0
 */
public class SalaryDataForm extends FrameForm 
{
    /**权限范围内的薪资类别列表*/
    private ArrayList setlist=new ArrayList();
    /**薪资类别分页控制*/
    private PaginationForm setlistform=new PaginationForm();
    /**薪资类别*/
    private String salaryid="-1";
    /**组织机构代码*/
    private String a_code;    
    /**项目过滤号*/
    private String itemid;
    /**条件过滤号*/
    private String condid; 
    private String cond_id_str="";
    private String filterWhl="";
    /**人员筛选SQL*/
    private String empfiltersql;
    /**工资类别是否加权限控制*/
    private String priv="1";  
    /**工资管理员*/
    private String manager="";   
    /**薪资项目列表*/
    private ArrayList fieldlist=new ArrayList();
    /**薪资表名称*/
    private String gz_tablename;
    /**数据过滤语句*/
    private String sql;
    /**薪资是否为已提交状态*/
    private String salaryIsSubed="false";  
    private String verify_ctrl;//是否是审核公式控制
    /**是否显示数据对比菜单*/
    private String priv_mode;//=0不显示,=1显示
    /**项目过滤列表*/
    private ArrayList itemlist=new ArrayList();
    /**条件过滤列表*/
    private ArrayList condlist=new ArrayList();
    /**项目过滤保存字符串*/
    private String proright_str;
    /** 应用库标识 */
    private String nbase="";
    /** 导入文件 */
    private FormFile file;
    private String returnFlag="0";//返回按钮的走向 0：返回薪资发放的类别界面 1：返回部门月奖金界面
    /**新的处理的业务日期-年份*/
    private String theyear;
    /**新的处理业务日期-月份*/
    private String themonth;  
    private String operOrg;
    private String isLeafOrg = "";// 是否叶子机构编码
    private String isAllDistri = "";//是否登录用户的所有操作单位都处于下发状态（顶层除外），如果指定了核算标志为否的单位就是下发到该单位就算都下发了
    private String isOnlyLeafOrgs = "";// 是否所有操作单位都是叶子机构
    private ArrayList musterList=new ArrayList();; //当前工资类别的高级花名册
    private String isLeafOrgReport = "0";// 是否叶子机构处于上报状态
    private String isOrgCheckNo = "0";
    private String isLeafOrgDistri = "0";//是否叶子节点已经处于下发状态
    
    private String showUnitCodeTree="0";   //是否按操作单位显示树
    
    @Override
    public void inPutTransHM()
    {
    this.getFormHM().put("verify_ctrl",this.getVerify_ctrl());	
	this.getFormHM().put("empfiltersql",this.getEmpfiltersql());	
    this.getFormHM().put("isOrgCheckNo", this.getIsOrgCheckNo());
	this.getFormHM().put("isLeafOrgReport", this.getIsLeafOrgReport());
	this.getFormHM().put("musterList", this.getMusterList());
	this.getFormHM().put("salaryid", this.getSalaryid());
	this.getFormHM().put("a_code", getA_code());
	this.getFormHM().put("itemid", this.getItemid());
	this.getFormHM().put("setlist", this.getSetlist());
	this.getFormHM().put("priv", this.getPriv());
	this.getFormHM().put("manager", this.getManager());
	this.getFormHM().put("sql", this.getSql());
	this.getFormHM().put("gz_tablename", this.getGz_tablename());
	this.getFormHM().put("fieldlist", this.getFieldlist());
	this.getFormHM().put("salaryIsSubed", this.getSalaryIsSubed());
	this.getFormHM().put("priv_mode", this.getPriv_mode());
	this.getFormHM().put("itemlist", this.getItemlist());
	this.getFormHM().put("proright_str", this.getProright_str());
	this.getFormHM().put("nbase", this.getNbase());
	this.getFormHM().put("file", this.getFile());	
	this.getFormHM().put("returnFlag", this.getReturnFlag());
	this.getFormHM().put("theyear", this.getTheyear());
	this.getFormHM().put("themonth", this.getThemonth());
	this.getFormHM().put("operOrg", this.getOperOrg());
	this.getFormHM().put("isLeafOrg", this.getIsLeafOrg());
	this.getFormHM().put("isAllDistri", this.getIsAllDistri());
	this.getFormHM().put("isOnlyLeafOrgs", this.getIsOnlyLeafOrgs());
	this.getFormHM().put("isLeafOrgDistri", this.getIsLeafOrgDistri());
	this.getFormHM().put("condid", this.getCondid());
	this.getFormHM().put("condlist", this.getCondlist());
	this.getFormHM().put("cond_id_str",cond_id_str);
	this.getFormHM().put("filterWhl",this.getFilterWhl());
    }

    @Override
    public void outPutFormHM()
    {
	this.setShowUnitCodeTree((String)this.getFormHM().get("showUnitCodeTree"));
	this.setVerify_ctrl((String)this.getFormHM().get("verify_ctrl"));	
    this.setFilterWhl((String)this.getFormHM().get("filterWhl")); 
    this.setEmpfiltersql((String)this.getFormHM().get("empfiltersql")); 
    this.setCond_id_str((String)this.getFormHM().get("cond_id_str"));
    this.setIsOrgCheckNo((String)this.getFormHM().get("isOrgCheckNo"));
	this.setIsLeafOrgReport((String) this.getFormHM().get("isLeafOrgReport"));
	this.setMusterList((ArrayList) this.getFormHM().get("musterList"));
	this.setIsOnlyLeafOrgs((String) this.getFormHM().get("isOnlyLeafOrgs"));
	this.setIsAllDistri((String) this.getFormHM().get("isAllDistri"));
	this.setIsLeafOrg((String) this.getFormHM().get("isLeafOrg"));
	this.setOperOrg((String)this.getFormHM().get("operOrg"));
	this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
	this.setSalaryid((String)this.getFormHM().get("salaryid"));
	this.setA_code((String)this.getFormHM().get("a_code"));
	this.setItemid((String)this.getFormHM().get("itemid"));
	this.getSetlistform().setList((ArrayList)this.getFormHM().get("setlist"));
	this.getSetlistform().getCurrent();
	this.setPriv((String)this.getFormHM().get("priv"));
	this.setManager((String)this.getFormHM().get("manager"));
	this.setSql((String)this.getFormHM().get("sql"));
	this.setGz_tablename((String)this.getFormHM().get("gz_tablename"));
	this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
	this.setSalaryIsSubed((String)this.getFormHM().get("salaryIsSubed"));
	this.setPriv_mode((String)this.getFormHM().get("priv_mode"));
	this.setProright_str((String)this.getFormHM().get("proright_str"));
	this.setNbase((String)this.getFormHM().get("nbase"));
	this.setReturnFlag((String)this.getFormHM().get("returnFlag"));
	this.setThemonth((String)this.getFormHM().get("themonth"));
	this.setTheyear((String)this.getFormHM().get("theyear"));
	this.setIsLeafOrgDistri((String)this.getFormHM().get("isLeafOrgDistri"));
	this.setCondlist((ArrayList)this.getFormHM().get("condlist"));
	this.setCondid((String)this.getFormHM().get("condid"));
    }

    public String getA_code()
    {
    
        return a_code;
    }

    public void setA_code(String a_code)
    {
    
        this.a_code = a_code;
    }

    public String getItemid()
    {
    
        return itemid;
    }

    public void setItemid(String itemid)
    {
    
        this.itemid = itemid;
    }

    public String getSalaryid()
    {
    
        return salaryid;
    }

    public void setSalaryid(String salaryid)
    {
    
        this.salaryid = salaryid;
    }

    public ArrayList getSetlist()
    {
    
        return setlist;
    }

    public void setSetlist(ArrayList setlist)
    {
    
        this.setlist = setlist;
    }

    public PaginationForm getSetlistform()
    {
    
        return setlistform;
    }

    public void setSetlistform(PaginationForm setlistform)
    {
    
        this.setlistform = setlistform;
    }

    public String getPriv()
    {
    
        return priv;
    }

    public void setPriv(String priv)
    {
    
        this.priv = priv;
    }

    public String getManager()
    {
    
        return manager;
    }

    public void setManager(String manager)
    {
    
        this.manager = manager;
    }

    public ArrayList getFieldlist()
    {
    
        return fieldlist;
    }

    public void setFieldlist(ArrayList fieldlist)
    {
    
        this.fieldlist = fieldlist;
    }

    public String getGz_tablename()
    {
    
        return gz_tablename;
    }

    public void setGz_tablename(String gz_tablename)
    {
    
        this.gz_tablename = gz_tablename;
    }

    public String getSql()
    {
    
        return sql;
    }

    public void setSql(String sql)
    {
    
        this.sql = sql;
    }

    public String getSalaryIsSubed()
    {
    
        return salaryIsSubed;
    }

    public void setSalaryIsSubed(String salaryIsSubed)
    {
    
        this.salaryIsSubed = salaryIsSubed;
    }

    public String getPriv_mode()
    {
    
        return priv_mode;
    }

    public void setPriv_mode(String priv_mode)
    {
    
        this.priv_mode = priv_mode;
    }

    public ArrayList getItemlist()
    {
    
        return itemlist;
    }

    public void setItemlist(ArrayList itemlist)
    {
    
        this.itemlist = itemlist;
    }

    public String getProright_str()
    {
    
        return proright_str;
    }

    public void setProright_str(String proright_str)
    {
    
        this.proright_str = proright_str;
    }

    public String getNbase()
    {
    
        return nbase;
    }

    public void setNbase(String nbase)
    {
    
        this.nbase = nbase;
    }

    public FormFile getFile()
    {
    
        return file;
    }

    public void setFile(FormFile file)
    {
    
        this.file = file;
    }

    public String getReturnFlag()
    {
    
        return returnFlag;
    }

    public void setReturnFlag(String returnFlag)
    {
    
        this.returnFlag = returnFlag;
    }

    public String getThemonth()
    {
    
        return themonth;
    }

    public void setThemonth(String themonth)
    {
    
        this.themonth = themonth;
    }

    public String getTheyear()
    {
    
        return theyear;
    }

    public void setTheyear(String theyear)
    {
    
        this.theyear = theyear;
    }

    public String getOperOrg()
    {
    
        return operOrg;
    }

    public void setOperOrg(String operOrg)
    {
    
        this.operOrg = operOrg;
    }

    public String getIsLeafOrg()
    {
    
        return isLeafOrg;
    }

    public void setIsLeafOrg(String isLeafOrg)
    {
    
        this.isLeafOrg = isLeafOrg;
    }

    public String getIsAllDistri()
    {
    
        return isAllDistri;
    }

    public void setIsAllDistri(String isAllDistri)
    {
    
        this.isAllDistri = isAllDistri;
    }

    public String getIsOnlyLeafOrgs()
    {
    
        return isOnlyLeafOrgs;
    }

    public void setIsOnlyLeafOrgs(String isOnlyLeafOrgs)
    {
    
        this.isOnlyLeafOrgs = isOnlyLeafOrgs;
    }

    public ArrayList getMusterList()
    {
    
        return musterList;
    }

    public void setMusterList(ArrayList musterList)
    {
    
        this.musterList = musterList;
    }

    public String getIsLeafOrgReport()
    {
    
        return isLeafOrgReport;
    }

    public void setIsLeafOrgReport(String isLeafOrgReport)
    {
    
        this.isLeafOrgReport = isLeafOrgReport;
    }

	public String getIsOrgCheckNo() {
		return isOrgCheckNo;
	}

	public void setIsOrgCheckNo(String isOrgCheckNo) {
		this.isOrgCheckNo = isOrgCheckNo;
	}

	public String getIsLeafOrgDistri()
	{
		return isLeafOrgDistri;
	}

	public void setIsLeafOrgDistri(String isLeafOrgDistri)
	{
		this.isLeafOrgDistri = isLeafOrgDistri;
	}

	public String getCondid()
	{
		return condid;
	}

	public void setCondid(String condid)
	{
		this.condid = condid;
	}

	public ArrayList getCondlist()
	{
		return condlist;
	}

	public void setCondlist(ArrayList condlist)
	{
		this.condlist = condlist;
	}

	public String getCond_id_str()
	{
		return cond_id_str;
	}

	public void setCond_id_str(String cond_id_str)
	{
		this.cond_id_str = cond_id_str;
	}

	public String getEmpfiltersql()
	{
		return empfiltersql;
	}

	public void setEmpfiltersql(String empfiltersql)
	{
		this.empfiltersql = empfiltersql;
	}

	public String getFilterWhl()
	{
		return filterWhl;
	}

	public void setFilterWhl(String filterWhl)
	{
		this.filterWhl = filterWhl;
	}

	public String getShowUnitCodeTree() {
		return showUnitCodeTree;
	}

	public void setShowUnitCodeTree(String showUnitCodeTree) {
		this.showUnitCodeTree = showUnitCodeTree;
	}

	public String getVerify_ctrl() {
		return verify_ctrl;
	}

	public void setVerify_ctrl(String verifyCtrl) {
		verify_ctrl = verifyCtrl;
	}   
    
}
