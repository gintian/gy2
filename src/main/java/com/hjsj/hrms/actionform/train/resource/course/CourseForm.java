package com.hjsj.hrms.actionform.train.resource.course;

import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class CourseForm extends FrameForm {
	private String sqlstr = "";
	private String tablename = "";
	private ArrayList itemlist = new ArrayList();
	private String a_code;
	private String a_code1;
	private String item_field="item_field";
	private String sort_recode;
	private String searchstr;
	private List sortlist=new ArrayList();
	private String id;
	private String orgparentcode;//LiWeichao 对应单位
	private String returnvalue;
	private FormFile file;
	private String isP;//是否为上级分类下的课程 1:是
	private String r5022;//审批状态
	private String imagename;
	private String strsql;
	private String columns;
	private String strwhere;
	private String primaryField;
	private String filepath;
    
    
	// 培训公共代码树的代码类 
	private String trainsetid;
	
	// 培训课程---参数设置---DIY课程类别
	private String diyType;
	// 培训课程---参数设置---DIY课程类别名称
	private String diyTypeName;
	// 培训课程---参数设置---播放在前几位的为热门课程
	private String hotCount;
	// 保存参数是否成功，0为不需保存，1为保存成功，2为保存失败
	private String saveStatus;
	
	
	private String codeSetId;
	public String getCodeSetId() {
		return codeSetId;
	}

	public void setCodeSetId(String codeSetId) {
		this.codeSetId = codeSetId;
	}

	 
	private String order_by; 
	//岗位课程
	private ArrayList itemlist1 = new ArrayList();
	private String columns1;
	private String codesetid;
	private String codesetdesc;
	private String codeitemid;
	private String state;
	private String backdate;
	private String validateflag;
	private String checked;
	
	private String itemize;//分类编码
	private String itemizevalue;//课程分类
	private String coursename;//课程名称
	private String courseintro;//课程内容
	//培训课程关联素质指标
	private PaginationForm abilitylistForm = new PaginationForm();
	private ArrayList abilitylist = new ArrayList();
	private String r5000;//课程编号
	private String flag;
	private String pivflag;
	
	private String syspath;
	 
	@Override
    public void inPutTransHM() {
	    this.getFormHM().put("filepath", this.getFilepath());
		this.getFormHM().put("searchstr", this.getSearchstr());
		this.getFormHM().put("imagename", this.getImagename());
		this.setSearchstr(null);
		this.getFormHM().put("id", this.getId());
		this.setId(null);
		this.getFormHM().put("strsql", this.getStrsql());
		this.getFormHM().put("file", this.getFile());
		this.getFormHM().put("columns", this.getColumns());
		this.getFormHM().put("columns1", this.getColumns1());
		this.getFormHM().put("strwhere", this.getStrwhere());
		this.getFormHM().put("primaryField", this.getPrimaryField());
		this.getFormHM().put("isP", "");
		
		if(this.getPagination()!=null)
			this.getFormHM().put("selectedList",(ArrayList)this.getPagination().getSelectedList());
		
		this.getFormHM().put("a_code",a_code);
		this.getFormHM().put("state", state);
		this.getFormHM().put("backdate", backdate);
		this.getFormHM().put("itemize", this.getItemize());
		this.getFormHM().put("coursename", this.getCoursename());
		this.getFormHM().put("courseintro", this.getCourseintro());
		this.getFormHM().put("hotCount", this.getHotCount());
		this.getFormHM().put("diyType", this.getDiyType());
		this.getFormHM().put("diyTypeName", this.getDiyTypeName());
		
		this.getFormHM().put("r5000", this.getR5000());
		//获取系统的功能模块
		EncryptLockClient lock=(EncryptLockClient)this.getServlet().getServletContext().getAttribute("lock");
        this.getFormHM().put("lock",lock);
        this.getFormHM().put("flag", this.getFlag());
        this.getFormHM().put("pivflag", this.getPivflag());
        this.getFormHM().put("syspath", this.getSyspath());
	}

	@Override
    public void outPutFormHM() {
	    this.setFilepath((String) this.getFormHM().get("filepath"));
		this.setImagename((String) this.getFormHM().get("imagename"));
		this.setSqlstr((String) this.getFormHM().get("sqlstr"));
		this.setFile((FormFile) this.getFormHM().get("file"));
		this.setItemlist((ArrayList) this.getFormHM().get("itemlist"));
		this.setTablename((String) this.getFormHM().get("tablename"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setSortlist((List)this.getFormHM().get("sortlist"));
		this.setId((String)this.getFormHM().get("id"));
		this.setA_code1((String)this.getFormHM().get("a_code1"));
		this.setOrgparentcode((String)this.getFormHM().get("orgparentcode"));
		
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setColumns1((String)this.getFormHM().get("columns1"));
		this.setStrwhere((String)this.getFormHM().get("strwhere"));
		this.setPrimaryField((String)this.getFormHM().get("primaryField"));
		this.setIsP((String)this.getFormHM().get("isP"));
		this.setR5022((String)this.getFormHM().get("r5022"));
		this.setTrainsetid((String) this.getFormHM().get("trainsetid"));
		this.setCodeSetId((String) this.getFormHM().get("codesetid"));
		
		this.setItemlist1((ArrayList) this.getFormHM().get("itemlist1"));
		this.setCodesetid((String)this.getFormHM().get("codesetid"));
		this.setCodesetdesc((String)this.getFormHM().get("codesetdesc"));
		this.setCodeitemid((String)this.getFormHM().get("codeitemid"));
		this.setState((String)this.getFormHM().get("state"));
		this.setOrder_by((String)this.getFormHM().get("order_by"));
		this.setBackdate((String)this.getFormHM().get("backdate"));
		this.setValidateflag((String)this.getFormHM().get("validateflag"));
		this.setDiyType((String) this.getFormHM().get("diyType"));
		this.setHotCount((String) this.getFormHM().get("hotCount"));
		this.setSaveStatus((String) this.getFormHM().get("saveStatus"));
		this.setDiyTypeName((String) this.getFormHM().get("diyTypeName"));
		
		this.getAbilitylistForm().setList((ArrayList) this.getFormHM().get("abilitylist"));
		this.setR5000((String) this.getFormHM().get("r5000"));
		this.setFlag((String) this.getFormHM().get("flag"));
		this.setPivflag((String) this.getFormHM().get("pivflag"));
		this.setCourseintro((String) this.getFormHM().get("courseintro"));
		
		this.setSyspath((String) this.getFormHM().get("syspath"));
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {

	try
	{
	    if ("/train/resource/course".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
	    {
    		if (this.getPagination() != null && !"back".equalsIgnoreCase(arg1.getParameter("b_query")))
    		    this.getPagination().firstPage();
	    }else if ("/train/resource/course/pos".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
	    {
			if (this.getPagination() != null)
			    this.getPagination().firstPage();
	    }else if ("/train/resource/course/posrel".equals(arg0.getPath()) && arg1.getParameter("b_search") != null)
	    {
			if (this.getPagination() != null)
			    this.getPagination().firstPage();
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return super.validate(arg0, arg1);
    }

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getItem_field() {
		return item_field;
	}

	public void setItem_field(String item_field) {
		this.item_field = item_field;
	}

	public String getSearchstr() {
		return searchstr;
	}

	public void setSearchstr(String searchstr) {
		this.searchstr = searchstr;
	}

	public List getSortlist() {
		return sortlist;
	}

	public void setSortlist(List sortlist) {
		this.sortlist = sortlist;
	}

	public String getSort_recode() {
		return sort_recode;
	}

	public void setSort_recode(String sort_recode) {
		this.sort_recode = sort_recode;
	}

	public String getId() {
		String idtemp = this.id;
		this.id = null;
		return idtemp;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getA_code1() {
		return a_code1;
	}

	public void setA_code1(String a_code1) {
		this.a_code1 = a_code1;
	}

	public String getOrgparentcode() {
		return orgparentcode;
	}

	public void setOrgparentcode(String orgparentcode) {
		this.orgparentcode = orgparentcode;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

	public String getStrsql() {
		return strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getStrwhere() {
		return strwhere;
	}

	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}

	public String getPrimaryField() {
		return primaryField;
	}

	public void setPrimaryField(String primaryField) {
		this.primaryField = primaryField;
	}

	public String getIsP() {
		return isP;
	}

	public void setIsP(String isP) {
		this.isP = isP;
	}

	public String getR5022() {
		return r5022;
	}

	public void setR5022(String r5022) {
		this.r5022 = r5022;
	}

	public String getTrainsetid() {
		return trainsetid;
	}

	public void setTrainsetid(String trainsetid) {
		this.trainsetid = trainsetid;
	}

	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}

	public String getCodesetdesc() {
		return codesetdesc;
	}

	public void setCodesetdesc(String codesetdesc) {
		this.codesetdesc = codesetdesc;
	}

	public String getCodeitemid() {
		return codeitemid;
	}

	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getOrder_by() {
		return order_by;
	}

	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}

	public String getBackdate() {
		return backdate;
	}

	public void setBackdate(String backdate) {
		this.backdate = backdate;
	}

	public ArrayList getItemlist1() {
		return itemlist1;
	}

	public void setItemlist1(ArrayList itemlist1) {
		this.itemlist1 = itemlist1;
	}

	public String getColumns1() {
		return columns1;
	}

	public void setColumns1(String columns1) {
		this.columns1 = columns1;
	}

	public String getItemize() {
		return itemize;
	}

	public void setItemize(String itemize) {
		this.itemize = itemize;
	}

	public String getItemizevalue() {
		return itemizevalue;
	}

	public void setItemizevalue(String itemizevalue) {
		this.itemizevalue = itemizevalue;
	}

	public String getCoursename() {
		return coursename;
	}

	public void setCoursename(String coursename) {
		this.coursename = coursename;
	}

	public String getCourseintro() {
		return courseintro;
	}

	public void setCourseintro(String courseintro) {
		this.courseintro = courseintro;
	}

	public String getValidateflag() {
		return validateflag;
	}

	public void setValidateflag(String validateflag) {
		this.validateflag = validateflag;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String getDiyType() {
		return diyType;
	}

	public void setDiyType(String diyType) {
		this.diyType = diyType;
	}

	public String getHotCount() {
		return hotCount;
	}

	public void setHotCount(String hotCount) {
		this.hotCount = hotCount;
	}

	public String getSaveStatus() {
		return saveStatus;
	}

	public void setSaveStatus(String saveStatus) {
		this.saveStatus = saveStatus;
	}

	public String getDiyTypeName() {
		return diyTypeName;
	}

	public void setDiyTypeName(String diyTypeName) {
		this.diyTypeName = diyTypeName;
	}

	public ArrayList getAbilitylist() {
		return abilitylist;
	}

	public void setAbilitylist(ArrayList abilitylist) {
		this.abilitylist = abilitylist;
	}

	public PaginationForm getAbilitylistForm() {
		return abilitylistForm;
	}

	public void setAbilitylistForm(PaginationForm abilitylistForm) {
		this.abilitylistForm = abilitylistForm;
	}

	public String getR5000() {
		return r5000;
	}

	public void setR5000(String r5000) {
		this.r5000 = r5000;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

    public String getPivflag() {
        return pivflag;
    }

    public void setPivflag(String pivflag) {
        this.pivflag = pivflag;
    }

	public FormFile getFile() {
		return file;
	}

	public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setFile(FormFile file) {
		this.file = file;
	}

	public String getImagename() {
		return imagename;
	}

	public void setImagename(String imagename) {
		this.imagename = imagename;
	}

    public String getSyspath() {
        return syspath;
    }

    public void setSyspath(String syspath) {
        this.syspath = syspath;
    }

}
