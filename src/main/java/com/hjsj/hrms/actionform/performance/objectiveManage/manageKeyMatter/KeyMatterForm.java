package com.hjsj.hrms.actionform.performance.objectiveManage.manageKeyMatter;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:KeyMatterForm.java</p>
 * <p>Description:关键事件管理(积分管理)</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-01 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 */

public class KeyMatterForm extends FrameForm
{
    private String flag;
    private String loadtype;
    private String act;
    private String code;
    private String kind;
    private String treeCode;
    private String unionOrgCode;  // 机构树后的 单位/部门/人员 编号
    private String checkName = ""; // 查询的 单位名称/姓名
    
    // 标志参数
    private String logo = "";
    private String sign = "false";
    private String dbname = "false";
    
    // 对象信息
    private String objectB0110="";
    private String objectE0122="";
    private String objectName="";
    private String objectA0100="";

    // 库前缀
    private String userbase;
    // 考核对象类型[1:团队 2:人员]
    private String objecType = "2";
    // 年度
    private String year;

    // 表对象(如果不存在这个表会报空指针)
//    private RecordVo keyEventVo = new RecordVo("per_key_event");
    private RecordVo keyEventVo = null;

    // list页面用
    private PaginationForm setlistform = new PaginationForm();
    // list页面用
    private ArrayList setlist = new ArrayList();
    private ArrayList yearList = new ArrayList();  
    private ArrayList objecTypeList = new ArrayList();   
    
    private String pointName = "";
    
    private ArrayList eventTypeList = new ArrayList();   
    private String eventType = "";
    
    
    
    @Override
    public void inPutTransHM()
    {  	
    	this.getFormHM().put("checkName", this.getCheckName());
    	this.getFormHM().put("sign", this.getSign());
    	this.getFormHM().put("dbname", this.getDbname());
    	this.getFormHM().put("logo", this.getLogo());
    	this.getFormHM().put("objecTypeList", this.getObjecTypeList());
    	this.getFormHM().put("eventTypeList", this.getEventTypeList());
    	this.getFormHM().put("eventType", this.getEventType());   	
		this.getFormHM().put("keyEventVo", this.getKeyEventVo());
		this.getFormHM().put("year", this.getYear());
		this.getFormHM().put("objectType", this.getObjecType());
		this.getFormHM().put("userbase", this.getUserbase());
		this.getFormHM().put("act", this.getAct());
		this.getFormHM().put("loadtype", this.getLoadtype());
		this.getFormHM().put("code", this.getCode());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("kind", this.getKind());
		this.getFormHM().put("yearList", this.getYearList());
		this.getFormHM().put("pointName", this.getPointName());		
		this.getFormHM().put("objectB0110", this.getObjectB0110());
		this.getFormHM().put("objectE0122", this.getObjectE0122());
		this.getFormHM().put("objectName", this.getObjectName());
		this.getFormHM().put("objectA0100", this.getObjectA0100());
		this.getFormHM().put("unionOrgCode", this.getUnionOrgCode());
    }

    @Override
    public void outPutFormHM()
    {
    	this.setCheckName((String) this.getFormHM().get("checkName"));
    	this.setSign((String) this.getFormHM().get("sign"));
    	this.setDbname((String) this.getFormHM().get("dbname"));
    	this.setLogo((String) this.getFormHM().get("logo"));
    	this.setObjecTypeList((ArrayList)this.getFormHM().get("objecTypeList"));
    	this.setEventType((String) this.getFormHM().get("eventType"));
		this.setEventTypeList((ArrayList)this.getFormHM().get("eventTypeList"));		
		this.getSetlistform().setList((ArrayList) this.getFormHM().get("setlist"));
		this.setSetlist((ArrayList) this.getFormHM().get("setlist"));
		this.setKeyEventVo((RecordVo) this.getFormHM().get("keyEventVo"));
		this.setYear((String) this.getFormHM().get("year"));
		this.setUserbase((String) this.getFormHM().get("userbase"));
		this.setObjecType((String) this.getFormHM().get("objectType"));
		this.setTreeCode((String) this.getFormHM().get("treeCode"));
		this.setAct((String) this.getFormHM().get("act"));
		this.setCode((String) this.getFormHM().get("code"));
		this.setLoadtype((String) this.getFormHM().get("loadtype"));
		this.setFlag((String) this.getFormHM().get("flag"));
		this.setKind((String) this.getFormHM().get("kind"));
		this.setYearList((ArrayList)this.getFormHM().get("yearList"));
		this.setPointName((String) this.getFormHM().get("pointName"));		
		this.setObjectB0110((String) this.getFormHM().get("objectB0110"));
		this.setObjectE0122((String) this.getFormHM().get("objectE0122"));
		this.setObjectName((String) this.getFormHM().get("objectName"));
		this.setObjectA0100((String) this.getFormHM().get("objectA0100"));
		this.setUnionOrgCode((String) this.getFormHM().get("unionOrgCode"));
    }  

    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
		try
		{
		    if ("/performance/objectiveManage/manageKeyMatter/keyMatterList".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
		    {		
				if (this.setlistform.getPagination() != null)
				{
				    this.setlistform.getPagination().firstPage();
				}								
		    }
		    
		    if ((arg1.getParameter("b_query") != null) && (arg1.getParameter("b_query").trim().length()>0) && (!"search".equals(arg1.getParameter("b_query"))))
		    {
		    	this.setCheckName("");
		    }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return super.validate(arg0, arg1);
    }

    public RecordVo getKeyEventVo()
    {
    	return keyEventVo;
    }

    public void setKeyEventVo(RecordVo keyEventVo)
    {
    	this.keyEventVo = keyEventVo;
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

    public String getYear()
    {
    	return year;
    }

    public void setYear(String year)
    {
    	this.year = year;
    }

    public String getTreeCode()
    {
    	return treeCode;
    }

    public void setTreeCode(String treeCode)
    {
    	this.treeCode = treeCode;
    }

    public String getAct()
    {
    	return act;
    }

    public void setAct(String act)
    {
    	this.act = act;
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

    public String getCode()
    {
    	return code;
    }

    public void setCode(String code)
    {
    	this.code = code;
    }

    public String getKind()
    {
    	return kind;
    }

    public void setKind(String kind)
    {
    	this.kind = kind;
    }

    public ArrayList getYearList()
    {   
        return yearList;
    }

    public void setYearList(ArrayList yearList)
    {   
        this.yearList = yearList;
    }

    public String getPointName()
    {   
        return pointName;
    }

    public void setPointName(String pointName)
    {   
        this.pointName = pointName;
    }

	public ArrayList getEventTypeList() 
	{
		return eventTypeList;
	}

	public void setEventTypeList(ArrayList eventTypeList) 
	{
		this.eventTypeList = eventTypeList;
	}

	public String getEventType() 
	{
		return eventType;
	}

	public void setEventType(String eventType) 
	{
		this.eventType = eventType;
	}

	public ArrayList getObjecTypeList() 
	{
		return objecTypeList;
	}

	public void setObjecTypeList(ArrayList objecTypeList) 
	{
		this.objecTypeList = objecTypeList;
	}

	public String getObjectB0110() 
	{
		return objectB0110;
	}

	public void setObjectB0110(String objectB0110) 
	{
		this.objectB0110 = objectB0110;
	}

	public String getObjectE0122() 
	{
		return objectE0122;
	}

	public void setObjectE0122(String objectE0122) 
	{
		this.objectE0122 = objectE0122;
	}

	public String getObjectName() 
	{
		return objectName;
	}

	public void setObjectName(String objectName) 
	{
		this.objectName = objectName;
	}

	public String getObjectA0100() 
	{
		return objectA0100;
	}

	public void setObjectA0100(String objectA0100) 
	{
		this.objectA0100 = objectA0100;
	}

	public String getLogo() 
	{
		return logo;
	}

	public void setLogo(String logo) 
	{
		this.logo = logo;
	}

	public String getDbname() 
	{
		return dbname;
	}

	public void setDbname(String dbname) 
	{
		this.dbname = dbname;
	}

	public String getSign() 
	{
		return sign;
	}

	public void setSign(String sign) 
	{
		this.sign = sign;
	}

	public String getUnionOrgCode() 
	{
		return unionOrgCode;
	}

	public void setUnionOrgCode(String unionOrgCode) 
	{
		this.unionOrgCode = unionOrgCode;
	}

	public String getCheckName() 
	{
		return checkName;
	}

	public void setCheckName(String checkName) 
	{
		this.checkName = checkName;
	}
	
}
