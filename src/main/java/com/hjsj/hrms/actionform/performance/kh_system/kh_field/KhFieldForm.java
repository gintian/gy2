package com.hjsj.hrms.actionform.performance.kh_system.kh_field;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class KhFieldForm extends FrameForm
{
	
	private String returnURL = "";   //返回路径
	private String computeFormula = "";  //  指标计算公式 	
	private String kpiTargetType = "";   // KPI指标类别
	private ArrayList kpiTargetTypeList = new ArrayList();
	private String kpiTarget_id = "";    // KPI指标
	private ArrayList kpiTarget_idList = new ArrayList();
	private boolean isSave = false; //19/9/11 xus 判断是否执行了新增的保存
	
	/**指标分类树*/
	private String tree;
	/**指标分类号*/
    private String pointsetid;
    /**指标要素列表*/
    private ArrayList fieldinfolist = new ArrayList();
    /**分页显示*/
    private PaginationForm fieldinfolistForm = new PaginationForm();
    /**是否有效=0有效=1无效*/
    private String validflag;
    /**指标分类名称*/
    private String pointname;
    /**子系统号=33*/
    private String subsys_id;
    /**修改或者新增*/
    private String type; 
    /**要调整顺序的指标分类列表*/
    private ArrayList list = new ArrayList();
    /**-------------------------标度摸板---*/
    /**标度代码*/
    private String grade_id;
    private String hiddenGradeid;
    /**标度列表*/
    private ArrayList gradeList = new ArrayList();
    /**标度分值*/
    private String gradevalue;
    /**标度描述*/
    private String gradedesc;
    /**上限值*/
    private String top_value;
    /**下限值*/
    private String bottom_value;
    private String isClose;
    private ArrayList fieldGradeList = new ArrayList();
    private String point_id;
    /**---------指标表------*/
    /**指标要素名称*/
    private String fieldname;
    /**要素类型*/
    private String pointkind;
    /**计算公式*/
    private String formula;
    /**指标有效标识*/
    private String fieldvlidflag;
    /**指标解释*/
    private String description;
    /**行为建议*/
    private String proposal;       
    /**是否显示解释/标度*/
    private String visible;
    /**上级关联指标*/
    private String fielditem;
    /**下级关联指标*/
    private String l_fielditem;
    /**打分方式*/
    private String status;
    /**要素类型*/
    private String pointtype;
    /**要素属性=0 or null基本指标=1加分指标=2扣分指标*/
    private String pointctrl;
    /**指标编号*/
    private String fieldnumber;
    private String hiddennumber;
    private String display;
    private ArrayList newgradeList = new ArrayList();
    private String aastr;
    private String ffstr;
    private String ccstr;
    private String ddstr;
    private String eestr;
    private FormFile fieldfile;
    private String counter;
    private String isSucess;
    private String parent_id;
    private String sorttype;
    private String isrefresh;
    //后加修改
    /**基本指标计算规则=0差额=1比列*/
    private String ltype;
    /**加分指标=0每低=1每高*/
    private String add_type;
    /**减分指标=0每低=1每高*/
    private String minus_type;
    /**0|1|2|3 计分规则（录分｜简单｜分段｜排名）*/
    private String rule;
    
    private String add_value;
    private String minus_value;
    /**加或扣的分数*/
    private String add_score;
    private String minus_score;
    /**=0|1(无效|有效)，有效则进行加分或扣分处理*/
    private String add_valid;
    private String minus_valid;
    /**=0|1,等于1按折算计分*/
    private String convert;
    private String rulePointid;
    private String pointCount="";
    private String tabid="";
    private String kh_content;
    private String gd_principle;
    private String flag;
    private String gradeContent;
    private String gradeid;
    private String grade_point_id;
    private ArrayList pointList=new ArrayList();
    
    private String orgpoint="";//单位子集
    private ArrayList khpidlist=new ArrayList();
    private String khpid="";
    private String khpname="";
    private ArrayList khpnamelist=new ArrayList();
    private ArrayList alllist=new ArrayList();
    private String allitems="";
	private String sql="";
	private String tablename="";
	private ArrayList fieldlist=new ArrayList();
	private String innerhtml="";
	private String showmenus="";
	private String unitcode="";
	private String points;
	private String delpoints="";
	private String dflag="";
	private String codeitemid="";
	private String priv="";
	private String aflag="";
	private String lag="";
	private ArrayList unitlist=new ArrayList();
	private String info="";
	private String personStation="";
	
	/**指标对应的能力素质课程  暂时未用到 */
    private ArrayList pointCourseList = new ArrayList();
    
    private String itemize = "";//分类编码
	private String itemizevalue = "";//课程分类
	private String coursename = "";//课程名称
	private String courseintro = "";//课程内容
	private String scope="0";
	private String duxie="";
	private String isgs="";
	
	

	public String getIsgs() {
		return isgs;
	}

	public void setIsgs(String isgs) {
		this.isgs = isgs;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
    public void inPutTransHM()
	{   
		
		this.getFormHM().put("isgs", this.getIsgs());
		this.getFormHM().put("duxie", this.getDuxie());
		this.getFormHM().put("scope", this.getScope());
		this.getFormHM().put("itemize", this.getItemize());
		this.getFormHM().put("itemizevalue", this.getItemizevalue());
		this.getFormHM().put("coursename", this.getCoursename());
		this.getFormHM().put("courseintro", this.getCourseintro());		
		this.getFormHM().put("pointCourseList", this.getPointCourseList());
		this.getFormHM().put("proposal", this.getProposal());
		this.getFormHM().put("computeFormula", this.getComputeFormula());
		this.getFormHM().put("kpiTargetType", this.getKpiTargetType());
		this.getFormHM().put("kpiTargetTypeList", this.getKpiTargetTypeList());
		this.getFormHM().put("kpiTarget_id", this.getKpiTarget_id());
		this.getFormHM().put("kpiTarget_idList", this.getKpiTarget_idList());
		
		this.getFormHM().put("returnflag", this.getReturnflag());
		this.getFormHM().put("grade_point_id", this.getGrade_point_id());
		this.getFormHM().put("gradeContent", this.getGradeContent());
		this.getFormHM().put("gradeid", this.getGradeid());
		this.getFormHM().put("kh_content", this.getKh_content());
		this.getFormHM().put("gd_principle", this.getGd_principle());
		this.getFormHM().put("rulePointid", this.getRulePointid());
	    this.getFormHM().put("ltype",this.getLtype());
	    this.getFormHM().put("add_type",this.getAdd_type());
	    this.getFormHM().put("minus_type",this.getMinus_type());
	    this.getFormHM().put("rule",this.getRule());
	    this.getFormHM().put("add_value",this.getAdd_value());
	    this.getFormHM().put("minus_value", this.getMinus_value());
	    this.getFormHM().put("add_score", this.getAdd_score());
	    this.getFormHM().put("minus_score",this.getMinus_score());
	    this.getFormHM().put("add_valid",this.getAdd_valid());
	    this.getFormHM().put("minus_valid",this.getMinus_valid());
	    this.getFormHM().put("convert",this.getConvert());
		this.getFormHM().put("parent_id",this.getParent_id());
		this.getFormHM().put("hiddennumber", this.getHiddennumber());
		this.getFormHM().put("fieldnumber",this.getFieldnumber());
		this.getFormHM().put("status", this.getStatus());
		this.getFormHM().put("visible",this.getVisible());
		this.getFormHM().put("description", this.getDescription());
		this.getFormHM().put("fieldvlidflag", this.getFieldvlidflag());
		this.getFormHM().put("pointkind", this.getPointkind());
		this.getFormHM().put("pointname",this.getPointname());
		this.getFormHM().put("pointsetid",this.getPointsetid());
		this.getFormHM().put("selectedList", this.getFieldinfolistForm().getSelectedList());
		this.getFormHM().put("subsys_id",this.getSubsys_id());
		this.getFormHM().put("grade_id",this.getGrade_id());
		this.getFormHM().put("gradevalue",this.getGradevalue());
		this.getFormHM().put("gradedesc",this.getGradedesc());
		this.getFormHM().put("top_value",this.getTop_value());
		this.getFormHM().put("bottom_value",this.getBottom_value());
		this.getFormHM().put("hiddenGradeid", this.getHiddenGradeid());
		this.getFormHM().put("isClose",this.getIsClose());
		this.getFormHM().put("fieldname", this.getFieldname());
		this.getFormHM().put("aastr",this.getAastr());
		this.getFormHM().put("ffstr",this.getFfstr());
		this.getFormHM().put("ccstr",this.getCcstr());
		this.getFormHM().put("ddstr",this.getDdstr());
		this.getFormHM().put("eestr",this.getEestr());
		this.getFormHM().put("fieldfile", this.getFieldfile());
		this.getFormHM().put("pointtype", this.getPointtype());
		this.getFormHM().put("orgpoint", this.getOrgpoint());
		this.getFormHM().put("khpid", this.getKhpid());
		this.getFormHM().put("khpname", this.getKhpname());
		this.getFormHM().put("allitems", this.getAllitems());
		this.getFormHM().put("fieldlist", this.getFieldlist());
		this.getFormHM().put("innerhtml", this.getInnerhtml());
		this.getFormHM().put("showmenus", this.getShowmenus());
		this.getFormHM().put("points", this.getPoints());
		this.getFormHM().put("unitcode", this.getUnitcode());
		this.getFormHM().put("delpoints", this.getDelpoints());
		this.getFormHM().put("dflag", this.getDflag());
		this.getFormHM().put("lag", this.getLag());
		this.getFormHM().put("alllist", this.getAlllist());
		this.getFormHM().put("unitlist", this.getUnitlist());
		this.getFormHM().put("personStation", this.getPersonStation());
	}

	@Override
    public void outPutFormHM()
	{   
		
		this.setIsgs((String)this.getFormHM().get("isgs"));
		this.setDuxie((String)this.getFormHM().get("duxie"));
		this.setScope((String)this.getFormHM().get("scope"));
		this.setItemize((String)this.getFormHM().get("itemize"));
		this.setItemizevalue((String)this.getFormHM().get("itemizevalue"));
		this.setCoursename((String)this.getFormHM().get("coursename"));
		this.setCourseintro((String)this.getFormHM().get("courseintro"));				
		this.setPointCourseList((ArrayList)this.getFormHM().get("pointCourseList"));
		this.setProposal((String)this.getFormHM().get("proposal"));
		this.setComputeFormula((String)this.getFormHM().get("computeFormula"));
		this.setKpiTargetType((String)this.getFormHM().get("kpiTargetType"));
		this.setKpiTargetTypeList((ArrayList)this.getFormHM().get("kpiTargetTypeList"));
		this.setKpiTarget_id((String)this.getFormHM().get("kpiTarget_id"));
		this.setKpiTarget_idList((ArrayList)this.getFormHM().get("kpiTarget_idList"));
		
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setGrade_point_id((String)this.getFormHM().get("grade_point_id"));
		this.setGradeContent((String)this.getFormHM().get("gradeContent"));
		this.setGradeid((String)this.getFormHM().get("gradeid"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setKh_content((String)this.getFormHM().get("kh_content"));
		this.setGd_principle((String)this.getFormHM().get("gd_principle"));
		this.setTabid((String)this.getFormHM().get("tabid"));
		this.setPointCount((String)this.getFormHM().get("pointCount"));
		this.setRulePointid((String)this.getFormHM().get("rulePointid"));
		this.setLtype((String)this.getFormHM().get("ltype"));
		this.setAdd_type((String)this.getFormHM().get("add_type"));
		this.setAdd_score((String)this.getFormHM().get("add_score"));
		this.setAdd_valid((String)this.getFormHM().get("add_valid"));
		this.setAdd_value((String)this.getFormHM().get("add_value"));
		this.setRule((String)this.getFormHM().get("rule"));
		this.setConvert((String)this.getFormHM().get("convert"));
		this.setMinus_score((String)this.getFormHM().get("minus_score"));
		this.setMinus_type((String)this.getFormHM().get("minus_type"));
		this.setMinus_valid((String)this.getFormHM().get("minus_valid"));
		this.setMinus_value((String)this.getFormHM().get("minus_value"));
		this.setPointtype((String)this.getFormHM().get("pointtype"));
		this.setSorttype((String)this.getFormHM().get("sorttype"));
		this.setNewgradeList((ArrayList)this.getFormHM().get("newgradeList"));
		this.setHiddennumber((String)this.getFormHM().get("hiddennumber"));
		this.setFieldnumber((String)this.getFormHM().get("fieldnumber"));
		this.setFieldname((String)this.getFormHM().get("fieldname"));
		this.setPointkind((String)this.getFormHM().get("pointkind"));
		this.setFieldvlidflag((String)this.getFormHM().get("fieldvlidflag"));
		this.setDescription((String)this.getFormHM().get("description"));
		this.setVisible((String)this.getFormHM().get("visible"));
		this.setStatus((String)this.getFormHM().get("status"));
		this.setGrade_id((String)this.getFormHM().get("grade_id"));
		this.setGradeList((ArrayList)this.getFormHM().get("gradeList"));
		this.setGradevalue((String)this.getFormHM().get("gradevalue"));
		this.setGradedesc((String)this.getFormHM().get("gradedesc"));
		this.setTop_value((String)this.getFormHM().get("top_value"));
		this.setBottom_value((String)this.getFormHM().get("bottom_value"));
		this.setValidflag((String)this.getFormHM().get("validflag"));
		this.setTree((String)this.getFormHM().get("tree"));
		this.setPointsetid((String)this.getFormHM().get("pointsetid"));
		this.getFieldinfolistForm().setList((ArrayList)this.getFormHM().get("fieldinfolist"));
		this.setPointname((String)this.getFormHM().get("pointname"));
		this.setSubsys_id((String)this.getFormHM().get("subsys_id"));
		this.setType((String)this.getFormHM().get("type"));
		this.setList((ArrayList)this.getFormHM().get("list"));
		this.setHiddenGradeid((String)this.getFormHM().get("hiddenGradeid"));
		this.setIsClose((String)this.getFormHM().get("isClose"));
		this.setFieldGradeList((ArrayList)this.getFormHM().get("fieldGradeList"));
		this.setPoint_id((String)this.getFormHM().get("point_id"));
		this.setDisplay((String)this.getFormHM().get("display"));
		this.setCounter((String)this.getFormHM().get("counter"));
		this.setIsSucess((String)this.getFormHM().get("isSucess"));
		this.setParent_id((String)this.getFormHM().get("parent_id"));
		this.setIsrefresh((String)this.getFormHM().get("isrefresh"));
		this.setPointList((ArrayList)this.getFormHM().get("pointList"));
		this.setOrgpoint((String)this.getFormHM().get("orgpoint"));
		this.setKhpidlist((ArrayList)this.getFormHM().get("khpidlist"));
		this.setKhpnamelist((ArrayList)this.getFormHM().get("khpnamelist"));
		this.setAlllist((ArrayList)this.getFormHM().get("alllist"));
		this.setAllitems((String)this.getFormHM().get("allitems"));
		this.setKhpid((String)this.getFormHM().get("khpid"));
		this.setKhpname((String)this.getFormHM().get("khpname"));
		this.setPriv((String)this.getFormHM().get("priv"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setInnerhtml((String)this.getFormHM().get("innerhtml"));
		this.setShowmenus((String)this.getFormHM().get("showmenus"));
		this.setTablename((String)this.getFormHM().get("tablename"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setPoints((String)this.getFormHM().get("points"));
		this.setUnitcode((String)this.getFormHM().get("unitcode"));
		this.setDflag((String)this.getFormHM().get("deflag"));
		this.setDelpoints((String)this.getFormHM().get("delpoints"));
		this.setCodeitemid((String)this.getFormHM().get(codeitemid));
		this.setAflag((String)this.getFormHM().get("aflag"));
		this.setUnitlist((ArrayList)this.getFormHM().get("unitlist"));
		this.setInfo((String)this.getFormHM().get("info"));
		this.setPersonStation((String)this.getFormHM().get("personStation"));
	}
	
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
		try
		{
		    if (("/performance/kh_system/kh_field/select_ability_class".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
		    		|| ("/performance/kh_system/kh_field/init_kh_field".equals(arg0.getPath()) && (arg1.getParameter("b_search") != null || arg1.getParameter("b_query") != null)))
		    {		
				if (this.getFieldinfolistForm().getPagination() != null)
				{
					//19/9/11 xus 【51723】绩效管理，考核指标，新建指标，定为的2页上，新增指标后，又跑到第1页了，不对。
					if(isSave && "/performance/kh_system/kh_field/init_kh_field".equals(arg0.getPath()) && arg1.getParameter("b_query") !=null)
					{
						this.getFieldinfolistForm().getPagination().lastPage();
						isSave = false;
					}else
					{
						this.getFieldinfolistForm().getPagination().firstPage();
					}
				}								
		    }
		    if ((arg1.getParameter("b_query") != null) && (arg1.getParameter("b_query").trim().length()>0) && ("link".equals(arg1.getParameter("b_query"))))
		    {
		    	this.setItemize("");
		    	this.setItemizevalue("");
		    	this.setCoursename("");
		    	this.setCourseintro("");
		    }
		    if ((arg1.getParameter("b_init") != null) && (arg1.getParameter("b_init").trim().length()>0) && (!"searchKpi".equals(arg1.getParameter("b_init"))))
		    {
		    	this.setKpiTargetType("");
		    	this.setKpiTarget_id("");
		    }
		    if("/performance/kh_system/kh_field/add_edit_field".equals(arg0.getPath()) && arg1.getParameter("b_save") != null){
		    	isSave = true;
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return super.validate(arg0, arg1);
    }
		
	public String getTree() {
		return tree;
	}

	public void setTree(String tree) {
		this.tree = tree;
	}

	public ArrayList getFieldinfolist() {
		return fieldinfolist;
	}

	public void setFieldinfolist(ArrayList fieldinfolist) {
		this.fieldinfolist = fieldinfolist;
	}

	public String getPointsetid() {
		return pointsetid;
	}

	public void setPointsetid(String pointsetid) {
		this.pointsetid = pointsetid;
	}

	public PaginationForm getFieldinfolistForm() {
		return fieldinfolistForm;
	}

	public void setFieldinfolistForm(PaginationForm fieldinfolistForm) {
		this.fieldinfolistForm = fieldinfolistForm;
	}

	public String getValidflag() {
		return validflag;
	}

	public void setValidflag(String validflag) {
		this.validflag = validflag;
	}

	public String getPointname() {
		return pointname;
	}

	public void setPointname(String pointname) {
		this.pointname = pointname;
	}

	public String getSubsys_id() {
		return subsys_id;
	}

	public void setSubsys_id(String subsys_id) {
		this.subsys_id = subsys_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	public String getBottom_value() {
		return bottom_value;
	}

	public void setBottom_value(String bottom_value) {
		this.bottom_value = bottom_value;
	}

	public String getGrade_id() {
		return grade_id;
	}

	public void setGrade_id(String grade_id) {
		this.grade_id = grade_id;
	}

	public String getGradedesc() {
		return gradedesc;
	}

	public void setGradedesc(String gradedesc) {
		this.gradedesc = gradedesc;
	}

	public ArrayList getGradeList() {
		return gradeList;
	}

	public void setGradeList(ArrayList gradeList) {
		this.gradeList = gradeList;
	}

	public String getGradevalue() {
		return gradevalue;
	}

	public void setGradevalue(String gradevalue) {
		this.gradevalue = gradevalue;
	}

	public String getTop_value() {
		return top_value;
	}

	public void setTop_value(String top_value) {
		this.top_value = top_value;
	}

	public String getHiddenGradeid() {
		return hiddenGradeid;
	}

	public void setHiddenGradeid(String hiddenGradeid) {
		this.hiddenGradeid = hiddenGradeid;
	}

	public String getIsClose() {
		return isClose;
	}

	public void setIsClose(String isClose) {
		this.isClose = isClose;
	}

	public ArrayList getFieldGradeList() {
		return fieldGradeList;
	}

	public void setFieldGradeList(ArrayList fieldGradeList) {
		this.fieldGradeList = fieldGradeList;
	}

	public String getPoint_id() {
		return point_id;
	}

	public void setPoint_id(String point_id) {
		this.point_id = point_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFielditem() {
		return fielditem;
	}

	public void setFielditem(String fielditem) {
		this.fielditem = fielditem;
	}

	public String getFieldname() {
		return fieldname;
	}

	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	public String getFieldvlidflag() {
		return fieldvlidflag;
	}

	public void setFieldvlidflag(String fieldvlidflag) {
		this.fieldvlidflag = fieldvlidflag;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getL_fielditem() {
		return l_fielditem;
	}

	public void setL_fielditem(String l_fielditem) {
		this.l_fielditem = l_fielditem;
	}

	public String getPointctrl() {
		return pointctrl;
	}

	public void setPointctrl(String pointctrl) {
		this.pointctrl = pointctrl;
	}

	public String getPointkind() {
		return pointkind;
	}

	public void setPointkind(String pointkind) {
		this.pointkind = pointkind;
	}

	public String getPointtype() {
		return pointtype;
	}

	public void setPointtype(String pointtype) {
		this.pointtype = pointtype;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

	public String getFieldnumber() {
		return fieldnumber;
	}

	public void setFieldnumber(String fieldnumber) {
		this.fieldnumber = fieldnumber;
	}

	public String getHiddennumber() {
		return hiddennumber;
	}

	public void setHiddennumber(String hiddennumber) {
		this.hiddennumber = hiddennumber;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public ArrayList getNewgradeList() {
		return newgradeList;
	}

	public void setNewgradeList(ArrayList newgradeList) {
		this.newgradeList = newgradeList;
	}

	public String getAastr() {
		return aastr;
	}

	public void setAastr(String aastr) {
		this.aastr = aastr;
	}

	public String getCcstr() {
		return ccstr;
	}

	public void setCcstr(String ccstr) {
		this.ccstr = ccstr;
	}

	public String getDdstr() {
		return ddstr;
	}

	public void setDdstr(String ddstr) {
		this.ddstr = ddstr;
	}

	public String getEestr() {
		return eestr;
	}

	public void setEestr(String eestr) {
		this.eestr = eestr;
	}

	public String getFfstr() {
		return ffstr;
	}

	public void setFfstr(String ffstr) {
		this.ffstr = ffstr;
	}

	public FormFile getFieldfile() {
		return fieldfile;
	}

	public void setFieldfile(FormFile fieldfile) {
		this.fieldfile = fieldfile;
	}

	public String getCounter() {
		return counter;
	}

	public void setCounter(String counter) {
		this.counter = counter;
	}

	public String getIsSucess() {
		return isSucess;
	}

	public void setIsSucess(String isSucess) {
		this.isSucess = isSucess;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public String getSorttype() {
		return sorttype;
	}

	public void setSorttype(String sorttype) {
		this.sorttype = sorttype;
	}

	public String getIsrefresh() {
		return isrefresh;
	}

	public void setIsrefresh(String isrefresh) {
		this.isrefresh = isrefresh;
	}

	public String getAdd_score() {
		return add_score;
	}

	public void setAdd_score(String add_score) {
		this.add_score = add_score;
	}

	public String getAdd_type() {
		return add_type;
	}

	public void setAdd_type(String add_type) {
		this.add_type = add_type;
	}

	public String getAdd_valid() {
		return add_valid;
	}

	public void setAdd_valid(String add_valid) {
		this.add_valid = add_valid;
	}

	public String getAdd_value() {
		return add_value;
	}

	public void setAdd_value(String add_value) {
		this.add_value = add_value;
	}

	public String getConvert() {
		return convert;
	}

	public void setConvert(String convert) {
		this.convert = convert;
	}

	public String getLtype() {
		return ltype;
	}

	public void setLtype(String ltype) {
		this.ltype = ltype;
	}

	public String getMinus_score() {
		return minus_score;
	}

	public void setMinus_score(String minus_score) {
		this.minus_score = minus_score;
	}

	public String getMinus_type() {
		return minus_type;
	}

	public void setMinus_type(String minus_type) {
		this.minus_type = minus_type;
	}

	public String getMinus_valid() {
		return minus_valid;
	}

	public void setMinus_valid(String minus_valid) {
		this.minus_valid = minus_valid;
	}

	public String getMinus_value() {
		return minus_value;
	}

	public void setMinus_value(String minus_value) {
		this.minus_value = minus_value;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getRulePointid() {
		return rulePointid;
	}

	public void setRulePointid(String rulePointid) {
		this.rulePointid = rulePointid;
	}

	public String getPointCount() {
		return pointCount;
	}

	public void setPointCount(String pointCount) {
		this.pointCount = pointCount;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public String getKh_content() {
		return kh_content;
	}

	public void setKh_content(String kh_content) {
		this.kh_content = kh_content;
	}

	public String getGd_principle() {
		return gd_principle;
	}

	public void setGd_principle(String gd_principle) {
		this.gd_principle = gd_principle;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getGradeContent() {
		return gradeContent;
	}

	public void setGradeContent(String gradeContent) {
		this.gradeContent = gradeContent;
	}

	public String getGradeid() {
		return gradeid;
	}

	public void setGradeid(String gradeid) {
		this.gradeid = gradeid;
	}

	public String getGrade_point_id() {
		return grade_point_id;
	}

	public void setGrade_point_id(String grade_point_id) {
		this.grade_point_id = grade_point_id;
	}

	public ArrayList getPointList() {
		return pointList;
	}

	public void setPointList(ArrayList pointList) {
		this.pointList = pointList;
	}

	public String getOrgpoint() {
		return orgpoint;
	}

	public void setOrgpoint(String orgpoint) {
		this.orgpoint = orgpoint;
	}

	public ArrayList getKhpidlist() {
		return khpidlist;
	}

	public void setKhpidlist(ArrayList khpidlist) {
		this.khpidlist = khpidlist;
	}
	public String getKhpid() {
		return khpid;
	}

	public void setKhpid(String khpid) {
		this.khpid = khpid;
	}

	public String getKhpname() {
		return khpname;
	}

	public void setKhpname(String khpname) {
		this.khpname = khpname;
	}
	public ArrayList getKhpnamelist() {
		return khpnamelist;
	}

	public void setKhpnamelist(ArrayList khpnamelist) {
		this.khpnamelist = khpnamelist;
	}

	public ArrayList getAlllist() {
		return alllist;
	}

	public void setAlllist(ArrayList alllist) {
		this.alllist = alllist;
	}
	public String getAllitems() {
		return allitems;
	}

	public void setAllitems(String allitems) {
		this.allitems = allitems;
	}
	public String getPriv() {
		return priv;
	}

	public void setPriv(String priv) {
		this.priv = priv;
	}
	public String getSql() {
		return sql;
	}
	
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getDelpoints() {
		return delpoints;
	}

	public void setDelpoints(String delpoints) {
		this.delpoints = delpoints;
	}

	public String getDflag() {
		return dflag;
	}

	public void setDflag(String dflag) {
		this.dflag = dflag;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}

	public String getShowmenus() {
		return showmenus;
	}

	public void setShowmenus(String showmenus) {
		this.showmenus = showmenus;
	}

	public String getInnerhtml() {
		return innerhtml;
	}

	public void setInnerhtml(String innerhtml) {
		this.innerhtml = innerhtml;
	}
	public String getAflag() {
		return aflag;
	}

	public void setAflag(String aflag) {
		this.aflag = aflag;
	}

	public String getCodeitemid() {
		return codeitemid;
	}

	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}
	public String getLag() {
		return lag;
	}

	public void setLag(String lag) {
		this.lag = lag;
	}

	public ArrayList getUnitlist() {
		return unitlist;
	}

	public void setUnitlist(ArrayList unitlist) {
		this.unitlist = unitlist;
	}
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getComputeFormula() {
		return computeFormula;
	}

	public void setComputeFormula(String computeFormula) {
		this.computeFormula = computeFormula;
	}

	public String getKpiTargetType() {
		return kpiTargetType;
	}

	public void setKpiTargetType(String kpiTargetType) {
		this.kpiTargetType = kpiTargetType;
	}

	public ArrayList getKpiTargetTypeList() {
		return kpiTargetTypeList;
	}

	public void setKpiTargetTypeList(ArrayList kpiTargetTypeList) {
		this.kpiTargetTypeList = kpiTargetTypeList;
	}

	public String getKpiTarget_id() {
		return kpiTarget_id;
	}

	public void setKpiTarget_id(String kpiTarget_id) {
		this.kpiTarget_id = kpiTarget_id;
	}

	public ArrayList getKpiTarget_idList() {
		return kpiTarget_idList;
	}

	public void setKpiTarget_idList(ArrayList kpiTarget_idList) {
		this.kpiTarget_idList = kpiTarget_idList;
	}

	public String getProposal() {
		return proposal;
	}

	public void setProposal(String proposal) {
		this.proposal = proposal;
	}

	public ArrayList getPointCourseList() {
		return pointCourseList;
	}

	public void setPointCourseList(ArrayList pointCourseList) {
		this.pointCourseList = pointCourseList;
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

	public String getReturnURL() {
		return returnURL;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

	public String getPersonStation() {
		return personStation;
	}

	public void setPersonStation(String personStation) {
		this.personStation = personStation;
	}

	public String getDuxie() {
		return duxie;
	}

	public void setDuxie(String duxie) {
		this.duxie = duxie;
	}

	
}
