package com.hjsj.hrms.actionform.performance.achivement.kpiOriginalData;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:KpiOriginalDataForm.java</p>
 * <p>Description:KPI原始数据录入</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-07-25 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class KpiOriginalDataForm extends FrameForm
{
    private String flag;
    private String loadtype;
    private String refreshKey = "";
    
    // 机构树编号
    private String unionOrgCode = "";
    // 标志参数
    private String checkName = ""; // 查询的 单位名称/姓名
    private String targetName = ""; // 查询的 指标名称
    private String affB0110 = "";  // 归属单位代码
    private String affB0110Desc = "";  // 归属单位描述
    // 指标类别
    private String targetType;
    private ArrayList targetTypeList = new ArrayList(); 
    // 年度
    private String year;
    private ArrayList yearList = new ArrayList(); 
    // 考核周期
    private String cycle = "0";
    private ArrayList cycleList = new ArrayList(); 
    private String noYearCycle;                    // 考核周期下非年度的数据
    private ArrayList noYearCycleList=new ArrayList();  // 考核周期下非年度的数据List
    
    // 库前缀
    private String userbase;
    // 考核对象类型[1:团队 2:人员]
    private String objecType = "2";
    private ArrayList objecTypeList = new ArrayList();

    // list页面用
    private PaginationForm setlistform = new PaginationForm();
    private ArrayList setlist = new ArrayList();    
    // 对象id串
    private String object_ids = "";
    
    // 表对象
    private RecordVo kpiItemVo = new RecordVo("per_kpi_item");
    /** KPI指标信息 */
    private LazyDynaBean kpiBean = new LazyDynaBean();
    /** KPI指标类别信息 */
    private ArrayList kpiItemTypeList = new ArrayList();
    private String kpiItemType = "";
    private String hidKpiItemType = "";
    
    /** 导入目标 */
    private FormFile file;
    /** 人员唯一性指标 */
    private String onlyFild = "";   
    
	@Override
    public void inPutTransHM()
    {  	
    	
		this.getFormHM().put("onlyFild", this.getOnlyFild());  
		this.getFormHM().put("file",this.getFile());
		this.getFormHM().put("kpiItemType", this.getKpiItemType());  
    	this.getFormHM().put("hidKpiItemType", this.getHidKpiItemType());
    	this.getFormHM().put("kpiItemTypeList", this.getKpiItemTypeList());
    	this.getFormHM().put("kpiBean", this.getKpiBean());  	
    	this.getFormHM().put("unionOrgCode", this.getUnionOrgCode());  	
    	this.getFormHM().put("object_ids", this.getObject_ids());  	
    	this.getFormHM().put("kpiTargetvo", this.getKpiItemVo());
    	this.getFormHM().put("targetName", this.getTargetName());  	
    	this.getFormHM().put("affB0110", this.getAffB0110());  
    	this.getFormHM().put("affB0110Desc", this.getAffB0110Desc());  
		this.getFormHM().put("targetType", this.getTargetType());		
		this.getFormHM().put("targetTypeList", this.getTargetTypeList());		
    	this.getFormHM().put("checkName", this.getCheckName());  	
		this.getFormHM().put("year", this.getYear());		
		this.getFormHM().put("yearList", this.getYearList());
		this.getFormHM().put("noYearCycle", this.getNoYearCycle());
		this.getFormHM().put("noYearCycleList", this.getNoYearCycleList());
		this.getFormHM().put("cycle", this.getCycle());		
		this.getFormHM().put("cycleList", this.getCycleList());  	
		this.getFormHM().put("objectType", this.getObjecType());
		this.getFormHM().put("objecTypeList", this.getObjecTypeList());
		this.getFormHM().put("userbase", this.getUserbase());
		this.getFormHM().put("refreshKey", this.getRefreshKey());
		this.getFormHM().put("loadtype", this.getLoadtype());
		this.getFormHM().put("flag", this.getFlag());
		
    }

    @Override
    public void outPutFormHM()
    {	  
    	this.setOnlyFild((String) this.getFormHM().get("onlyFild")); 
    	this.setFile((FormFile)this.getFormHM().get("file"));
    	this.setKpiItemType((String) this.getFormHM().get("kpiItemType")); 
    	this.setHidKpiItemType((String) this.getFormHM().get("hidKpiItemType")); 
    	this.setKpiItemTypeList((ArrayList)this.getFormHM().get("kpiItemTypeList"));
    	this.setKpiBean((LazyDynaBean)this.getFormHM().get("kpiBean"));
    	this.setUnionOrgCode((String) this.getFormHM().get("unionOrgCode"));  
    	this.setObject_ids((String) this.getFormHM().get("object_ids"));  
    	this.setKpiItemVo((RecordVo) this.getFormHM().get("kpiTargetvo"));
    	this.setTargetName((String) this.getFormHM().get("targetName"));  
    	this.setAffB0110((String) this.getFormHM().get("affB0110")); 
    	this.setAffB0110Desc((String) this.getFormHM().get("affB0110Desc")); 
		this.setTargetType((String) this.getFormHM().get("targetType"));		
		this.setTargetTypeList((ArrayList)this.getFormHM().get("targetTypeList"));   	
    	this.setCheckName((String) this.getFormHM().get("checkName"));  	
		this.setYear((String) this.getFormHM().get("year"));		
		this.setYearList((ArrayList)this.getFormHM().get("yearList"));		
		this.setNoYearCycle((String)this.getFormHM().get("noYearCycle"));
		this.setNoYearCycleList((ArrayList)this.getFormHM().get("noYearCycleList"));
		this.setCycle((String) this.getFormHM().get("cycle"));		
		this.setCycleList((ArrayList)this.getFormHM().get("cycleList"));   	
		this.getSetlistform().setList((ArrayList) this.getFormHM().get("setlist"));
		this.setSetlist((ArrayList) this.getFormHM().get("setlist"));
		this.setUserbase((String) this.getFormHM().get("userbase"));
		this.setObjecType((String) this.getFormHM().get("objectType"));
		this.setObjecTypeList((ArrayList)this.getFormHM().get("objecTypeList"));
		this.setRefreshKey((String) this.getFormHM().get("refreshKey"));
		this.setLoadtype((String) this.getFormHM().get("loadtype"));
		this.setFlag((String) this.getFormHM().get("flag"));
		
		

    }  

    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
		try
		{
		    if ("/performance/achivement/kpiOriginalData/kpiOriginalDataList".equals(arg0.getPath())
		    		&& arg1.getParameter("b_query") != null
		    		&& (!"search".equals(arg1.getParameter("b_query")))
		    		&& ( arg1.getParameter("freshPage") == null||!"false".equals(arg1.getParameter("freshPage")))//使保存、生效、退回、删除不回到第一页
		    		)
		    {		
				if (this.setlistform.getPagination() != null)
				{
				    this.setlistform.getPagination().firstPage();
				}								
		    }
		    if ("/performance/achivement/kpiOriginalData/kpiTargetAssertList".equals(arg0.getPath()) && arg1.getParameter("b_query") != null && (
		            ("link".equals(arg1.getParameter("b_query")))|| ("search".equals(arg1.getParameter("b_query")))))
		    {	//加上search wangrd 解决bug5833如果不定位第一页，点击查询按钮时 不显示数据	
				if (this.setlistform.getPagination() != null)
				{
				    this.setlistform.getPagination().firstPage();
				}								
		    }
		    
		    if ((arg1.getParameter("b_query") != null) && (arg1.getParameter("b_query").trim().length()>0) && (!"checkSearch".equals(arg1.getParameter("b_query"))))
		    	this.setCheckName("");
		    	
		    if ((arg1.getParameter("b_query") != null) && (arg1.getParameter("b_query").trim().length()>0) && (!"search".equals(arg1.getParameter("b_query"))))
		    {
//		    	this.setCheckName("");
		    	this.setTargetName("");
		    	this.setTargetType("");
		    	this.setAffB0110("");
		    	this.setAffB0110Desc("");
//		    	this.setCycle("");
//		    	this.setYear("");
		    }
		    //【4971】绩效管理：绩效考评/任务业绩/KPI原始数据录入，下载模板，是空模板，然后导入，点击返回按钮不起作用  jingq add 2014.12.01
		    if("/performance/achivement/kpiOriginalData/kpiOriginalDataList".equals(arg0.getPath())&&arg1.getParameter("b_importData")!=null){
		    	arg1.setAttribute("targetWindow", "1");
		    }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return super.validate(arg0, arg1);
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

    public String getUserbase()
    {
    	return userbase;
    }

    public void setUserbase(String userbase)
    {
    	this.userbase = userbase;
    }

    public String getObjecType()
    {
    	return objecType;
    }

    public void setObjecType(String objecType)
    {
    	this.objecType = objecType;
    }

    public String getFlag()
    {
    	return flag;
    }

    public void setFlag(String flag)
    {
    	this.flag = flag;
    }

    public String getLoadtype()
    {
    	return loadtype;
    }

    public void setLoadtype(String loadtype)
    {
    	this.loadtype = loadtype;
    }

	public ArrayList getObjecTypeList() {
		return objecTypeList;
	}

	public void setObjecTypeList(ArrayList objecTypeList) {
		this.objecTypeList = objecTypeList;
	}

	public String getCheckName() {
		return checkName;
	}

	public void setCheckName(String checkName) {
		this.checkName = checkName;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public ArrayList getYearList() {
		return yearList;
	}

	public void setYearList(ArrayList yearList) {
		this.yearList = yearList;
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public ArrayList getCycleList() {
		return cycleList;
	}

	public void setCycleList(ArrayList cycleList) {
		this.cycleList = cycleList;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public ArrayList getTargetTypeList() {
		return targetTypeList;
	}

	public void setTargetTypeList(ArrayList targetTypeList) {
		this.targetTypeList = targetTypeList;
	}

	public RecordVo getKpiItemVo() {
		return kpiItemVo;
	}

	public void setKpiItemVo(RecordVo kpiItemVo) {
		this.kpiItemVo = kpiItemVo;
	}

	public String getObject_ids() {
		return object_ids;
	}

	public void setObject_ids(String object_ids) {
		this.object_ids = object_ids;
	}

	public String getNoYearCycle() {
		return noYearCycle;
	}

	public void setNoYearCycle(String noYearCycle) {
		this.noYearCycle = noYearCycle;
	}

	public ArrayList getNoYearCycleList() {
		return noYearCycleList;
	}

	public void setNoYearCycleList(ArrayList noYearCycleList) {
		this.noYearCycleList = noYearCycleList;
	}

	public String getAffB0110() {
		return affB0110;
	}

	public void setAffB0110(String affB0110) {
		this.affB0110 = affB0110;
	}

	public String getAffB0110Desc() {
		return affB0110Desc;
	}

	public void setAffB0110Desc(String affB0110Desc) {
		this.affB0110Desc = affB0110Desc;
	}

	public String getUnionOrgCode() {
		return unionOrgCode;
	}

	public void setUnionOrgCode(String unionOrgCode) {
		this.unionOrgCode = unionOrgCode;
	}

	public LazyDynaBean getKpiBean() {
		return kpiBean;
	}

	public void setKpiBean(LazyDynaBean kpiBean) {
		this.kpiBean = kpiBean;
	}

	public ArrayList getKpiItemTypeList() {
		return kpiItemTypeList;
	}

	public void setKpiItemTypeList(ArrayList kpiItemTypeList) {
		this.kpiItemTypeList = kpiItemTypeList;
	}
	
	public String getKpiItemType() {
		return kpiItemType;
	}

	public void setKpiItemType(String kpiItemType) {
		this.kpiItemType = kpiItemType;
	}

	public String getHidKpiItemType() {
		return hidKpiItemType;
	}

	public void setHidKpiItemType(String hidKpiItemType) {
		this.hidKpiItemType = hidKpiItemType;
	}

	public String getRefreshKey() {
		return refreshKey;
	}

	public void setRefreshKey(String refreshKey) {
		this.refreshKey = refreshKey;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getOnlyFild() {
		return onlyFild;
	}

	public void setOnlyFild(String onlyFild) {
		this.onlyFild = onlyFild;
	}
	
}