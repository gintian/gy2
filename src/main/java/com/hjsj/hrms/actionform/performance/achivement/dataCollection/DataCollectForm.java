package com.hjsj.hrms.actionform.performance.achivement.dataCollection;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:DataCollectForm.java</p>
 * <p>Description:数据采集</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class DataCollectForm extends FrameForm
{
    private ArrayList khPlans = new ArrayList();

    private ArrayList khPoints = new ArrayList();

    private ArrayList khObjs = new ArrayList();

    private ArrayList khItems = new ArrayList();

    private String point = "";

    private String planId = "";

    private String tableHtml = "";
    
    private String rule = "";
    
    private String pointype = "0"; 	  //0|1|2:基本指标|加分指标|扣分指标

    private String paramStr="";
    
    private String isReadOnly="0";
    
    private String isShowTargetTrace="0";   //是否显示目标跟踪界面
    
    // list页面用
    private PaginationForm setlistform = new PaginationForm();
    // list页面用
    private ArrayList setlist = new ArrayList(); 
    
    /** 导入文件 */
    private FormFile file;
    
    private String sql = "";
    
    private String sqlWhere = "";

    private ArrayList fieldlist = new ArrayList();
    
    private String object_type="2";
    
    private String isFromTarget="0";//是否来自目标考核模块的调用
    
    private String isHaveRecords="1";//是否有记录
    
    private String onlyname="a0101";//人员唯一标识
    
    /** 判断是否满足以下三个条件
     * 1.设置了组织机构考核指标数据;
     * 2.计划下的定量统一打分指标(业绩指标)超过15个以上;
     * 3.某指标对应需录入数据的考核对象数为计划下总对象数的50%以下的指标数超5个以上,
     * 
     * false 不满足
     * true  满足
     * */
    private String determine="false";  
    
    /******    判断定量统一打分指标是否有计算公式  =true 有 =false 没有  ***/
    private String pointFormula = "false";
    private String    searchname="";
    private String    searchid="";
    @Override
    public void inPutTransHM()
    {
    	
    	this.getFormHM().put("pointFormula", this.getPointFormula());
    	this.getFormHM().put("determine", this.getDetermine());
		this.getFormHM().put("khPlans", this.getKhPlans());
		this.getFormHM().put("khPoints", this.getKhPoints());
		this.getFormHM().put("point", this.getPoint());
		this.getFormHM().put("planId", this.getPlanId());
		this.getFormHM().put("tableHtml", this.getTableHtml());
		this.getFormHM().put("khObjs", this.getKhObjs());
		this.getFormHM().put("khItems", this.getKhItems());
		this.getFormHM().put("file",this.getFile());
		this.getFormHM().put("rule",this.getRule());
		this.getFormHM().put("pointype",this.getPointype());
		this.getFormHM().put("paramStr",this.getParamStr());
		this.getFormHM().put("isReadOnly", this.getIsReadOnly());
		this.getFormHM().put("isShowTargetTrace", this.getIsShowTargetTrace());
		this.getFormHM().put("sql", this.getSql());
		this.getFormHM().put("fieldlist", this.getFieldlist());
		this.getFormHM().put("object_type", this.getObject_type());
		this.getFormHM().put("sqlWhere", this.getSqlWhere());
		this.getFormHM().put("isFromTarget", this.getIsFromTarget());
		this.getFormHM().put("isHaveRecords", this.getIsHaveRecords());
		this.getFormHM().put("onlyname", this.getOnlyname());
		this.getFormHM().put("searchname",this.getSearchname());
		this.getFormHM().put("searchid",this.getSearchid());
		this.getFormHM().put("setlistform",this.getSetlistform());
    }

    @Override
    public void outPutFormHM()
    {
    	
    	this.setPointFormula((String) this.getFormHM().get("pointFormula"));
    	this.setDetermine((String) this.getFormHM().get("determine"));
	    this.setOnlyname((String) this.getFormHM().get("onlyname"));
		this.setIsHaveRecords((String) this.getFormHM().get("isHaveRecords"));
		this.setIsFromTarget((String) this.getFormHM().get("isFromTarget"));
		this.setObject_type((String) this.getFormHM().get("object_type"));
		this.setIsShowTargetTrace((String) this.getFormHM().get("isShowTargetTrace"));
		this.setKhPlans((ArrayList) this.getFormHM().get("khPlans"));
		this.setKhPoints((ArrayList) this.getFormHM().get("khPoints"));
		this.setPoint((String) this.getFormHM().get("point"));
		this.setPlanId((String) this.getFormHM().get("planId"));
		this.setTableHtml((String) this.getFormHM().get("tableHtml"));
		this.setKhItems((ArrayList) this.getFormHM().get("khItems"));
		this.setKhObjs((ArrayList) this.getFormHM().get("khObjs"));
		this.setRule((String) this.getFormHM().get("rule"));
		this.setPointype((String)this.getFormHM().get("pointype"));
		this.getSetlistform().setList((ArrayList) this.getFormHM().get("setlist"));
		this.setSetlist((ArrayList) this.getFormHM().get("setlist"));
		this.setParamStr((String) this.getFormHM().get("paramStr"));
		this.setIsReadOnly((String) this.getFormHM().get("isReadOnly"));
		this.setFieldlist((ArrayList) this.getFormHM().get("fieldlist"));
		this.setSql((String) this.getFormHM().get("sql"));
		this.setSqlWhere((String) this.getFormHM().get("sqlWhere"));
		this.setSearchname((String) this.getFormHM().get("searchname"));
		this.setSearchid((String) this.getFormHM().get("searchid"));
    }

    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
		try
		{
		    if ("/performance/achivement/dataCollection/dataCollect".equals(arg0.getPath()) && arg1.getParameter("b_query") != null && !"search".equals(arg1.getParameter("b_query")))
		    {	
				if (this.setlistform.getPagination() != null)
				{
				    this.setlistform.getPagination().firstPage();
				    this.setSearchid("");
				    this.setSearchname("");
				}
		    }
		    if ("/performance/achivement/dataCollection/importExcel".equals(arg0.getPath()))
		    {	
		    	arg1.setAttribute("targetWindow", "1");
		    }
		    if ("/performance/achivement/dataCollection/dataCollect".equals(arg0.getPath()) && arg1.getParameter("b_query") != null && "search".equals(arg1.getParameter("b_query")))
		    {		
				if (this.setlistform.getPagination() != null)
				{
					//总记录数
					int totalSize = this.setlistform.getList().size();
					//每页显示数
					int pageCount = this.setlistform.getPagination().getPageCount();
					//System.out.println("每页显示数:"+pageCount);
					//总页数
					int totalPage = this.setlistform.getPagination().getPages();
					//System.out.println("总页数:"+totalPage);
					//当前记录所在的索引
					int currIndex = 0;
					for (int i = 0; i < this.setlistform.getAllList().size(); i++) {
						LazyDynaBean bean = (LazyDynaBean)this.setlistform.getAllList().get(i);
						if(bean.get("object_id")!=null && bean.get("object_id").equals(this.getSearchid())){
							currIndex = i;
                           break;
						}
					}
					//System.out.println("当前记录所在的索引:"+currIndex);
					int thePage = (currIndex+1)/pageCount;
					int yushu = (currIndex+1)%pageCount;
					int currentPage = thePage>1?thePage:1;
					if((currentPage!=1 || (currentPage==1 && yushu>0 && (currIndex+1)>pageCount)) && currentPage!=totalPage)
					currentPage = currentPage+((currIndex+1)%pageCount==0?0:1);
					//System.out.println("跳到哪页:"+currentPage);
					this.setlistform.getPagination().gotoPage(currentPage);
				    //this.setlistform.getPagination().firstPage();
				}
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return super.validate(arg0, arg1);
    }
    
    public ArrayList getKhPlans()
    {

    	return khPlans;
    }

    public void setKhPlans(ArrayList khPlans)
    {

    	this.khPlans = khPlans;
    }

    public ArrayList getKhPoints()
    {

    	return khPoints;
    }

    public void setKhPoints(ArrayList khPoints)
    {

    	this.khPoints = khPoints;
    }

    public String getPoint()
    {

	return point;
    }

    public void setPoint(String point)
    {

	this.point = point;
    }

    public String getPlanId()
    {

	return planId;
    }

    public void setPlanId(String planId)
    {

	this.planId = planId;
    }

    public String getTableHtml()
    {

	return tableHtml;
    }

    public void setTableHtml(String tableHtml)
    {

	this.tableHtml = tableHtml;
    }

    public ArrayList getKhItems()
    {
    
        return khItems;
    }

    public void setKhItems(ArrayList khItems)
    {
    
        this.khItems = khItems;
    }

    public ArrayList getKhObjs()
    {
    
        return khObjs;
    }

    public void setKhObjs(ArrayList khObjs)
    {
    
        this.khObjs = khObjs;
    }

    public FormFile getFile()
    {
    
        return file;
    }

    public void setFile(FormFile file)
    {
    
        this.file = file;
    }

    public String getRule()
    {
    
        return rule;
    }

    public void setRule(String rule)
    {
    
        this.rule = rule;
    }

    public String getPointype()
    {
    
        return pointype;
    }

    public void setPointype(String pointype)
    {
    
        this.pointype = pointype;
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

    public String getParamStr()
    {
    
        return paramStr;
    }

    public void setParamStr(String paramStr)
    {
    
        this.paramStr = paramStr;
    }

    public String getIsReadOnly()
    {
    
        return isReadOnly;
    }

    public void setIsReadOnly(String isReadOnly)
    {
    
        this.isReadOnly = isReadOnly;
    }

    public String getIsShowTargetTrace()
    {
    
        return isShowTargetTrace;
    }

    public void setIsShowTargetTrace(String isShowTargetTrace)
    {
    
        this.isShowTargetTrace = isShowTargetTrace;
    }

    public ArrayList getFieldlist()
    {
    
        return fieldlist;
    }

    public void setFieldlist(ArrayList fieldlist)
    {
    
        this.fieldlist = fieldlist;
    }

    public String getSql()
    {
    
        return sql;
    }

    public void setSql(String sql)
    {
    
        this.sql = sql;
    }

    public String getObject_type()
    {
    
        return object_type;
    }

    public void setObject_type(String object_type)
    {
    
        this.object_type = object_type;
    }

    public String getSqlWhere()
    {
    
        return sqlWhere;
    }

    public void setSqlWhere(String sqlWhere)
    {
    
        this.sqlWhere = sqlWhere;
    }

    public String getIsFromTarget()
    {
    
        return isFromTarget;
    }

    public void setIsFromTarget(String isFromTarget)
    {
    
        this.isFromTarget = isFromTarget;
    }

    public String getIsHaveRecords()
    {
    
        return isHaveRecords;
    }

    public void setIsHaveRecords(String isHaveRecords)
    {
    
        this.isHaveRecords = isHaveRecords;
    }

	public String getOnlyname()
	{
		return onlyname;
	}

	public void setOnlyname(String onlyname)
	{
		this.onlyname = onlyname;
	}

	public String getDetermine() {
		return determine;
	}

	public void setDetermine(String determine) {
		this.determine = determine;
	}

	public String getPointFormula() {
		return pointFormula;
	}

	public void setPointFormula(String pointFormula) {
		this.pointFormula = pointFormula;
	}

	public String getSearchname() {
		return searchname;
	}

	public void setSearchname(String searchname) {
		this.searchname = searchname;
	}

	public String getSearchid() {
		return searchid;
	}

	public void setSearchid(String searchid) {
		this.searchid = searchid;
	}
    
}
