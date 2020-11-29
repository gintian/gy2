package com.hjsj.hrms.actionform.train.request;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class CourseTrainForm extends FrameForm
{
    private String a_code = ""; // 机构代码

    private String tablename = "";

    private ArrayList flaglist = new ArrayList();

    private String spflag = ""; // 审批标识

    private String strsql = ""; // 查询语句
    
    private String sqlstr = "";//导出时用的sql

    private ArrayList timelist = new ArrayList();

    private String timeflag = ""; // 时间

    private String startime = ""; // 开始时间

    private String endtime = ""; // 结束时间

    private ArrayList itemlist = new ArrayList(); // 显示指标

    private String model = ""; // 1.需求征集 2.需求审批

    private String searchstr = ""; // 查询条件

    private ArrayList fieldlist = new ArrayList(); // 设置查询指标集

    private String item_field = "";

    private String fieldsetid = "";

    private ArrayList sortlist = new ArrayList(); // 记录排序
    
    private FormFile file; //文件路径

    private String[] sort_recode;

    private String username = "";

    private String fieldSize = "";

    private String viewunit = "";

    private String r3101 = "";

    private String r3122 = ""; // 课程安排

    private String r3127 = "";

    private ArrayList r41list = new ArrayList(); // 显示培训课程指标

    private String sql = "";

    private String wherestr = "";

    private String columns = "";

    private ArrayList setlist = new ArrayList();

    private ArrayList splist = new ArrayList();

    private String spid = "";

    private String tablestr = "";

    private String studyHour;

    private String isAutoHour = "0";// 是否自动计算学时,默认为否0.手动 1.自动

    private String permisView = "";// 审批意见
    
    private String type="";
    
    private String info="";    
    
    private String edit="";    
    
    private String returnvalue;
    
    private String num="0";//控制删除按钮的显示

    
    private String outName = "";
    
    private HashMap maps = new HashMap();

    private ArrayList msg = new ArrayList();
    
    private String persons = "";//培训学员模板中导入的人员编号
    
    private PaginationForm msgPageForm=new PaginationForm();


    
    private String pushitem;//培训课程推送列

    private String contentEv="";
	private String contentid="";
	private String wheresql="";
	private String readonly="";
	
	private ArrayList fieldSetDataList = new ArrayList();//培训班下载模板信息集
	private ArrayList msglist = new ArrayList();
	private String primarykeyLabel;
	private String flag;
	private String number;
	private String codeid;
	private ArrayList fielditemlist = new ArrayList();
	private String student;
	
	private ArrayList datelist = new ArrayList();
	private String hsearchstr="";//最后一次的查询定义的查询条件
	
	@Override
    public void outPutFormHM()
    {

    	this.setA_code((String) this.getFormHM().get("a_code"));
    	this.setFlaglist((ArrayList) this.getFormHM().get("flaglist"));
    	this.setSpflag((String) this.getFormHM().get("spflag"));
    	this.setStrsql((String) this.getFormHM().get("strsql"));
    	this.setTimelist((ArrayList) this.getFormHM().get("timelist"));
    	this.setTimeflag((String) this.getFormHM().get("timeflag"));
    	this.setStartime((String) this.getFormHM().get("startime"));
    	this.setEndtime((String) this.getFormHM().get("endtime"));
    	this.setItemlist((ArrayList) this.getFormHM().get("itemlist"));
    	this.setModel((String) this.getFormHM().get("model"));
    	this.setSearchstr((String) this.getFormHM().get("searchstr"));
    	this.setTablename((String) this.getFormHM().get("tablename"));
    	this.setFieldlist((ArrayList) this.getFormHM().get("fieldlist"));
    	this.setItem_field((String) this.getFormHM().get("item_field"));
    	this.setFieldsetid((String) this.getFormHM().get("fieldsetid"));
    	this.setSortlist((ArrayList) this.getFormHM().get("sortlist"));
    	this.setSort_recode((String[]) this.getFormHM().get("sort_recode"));
    	this.setUsername((String) this.getFormHM().get("username"));
    	this.setFieldSize((String) this.getFormHM().get("fieldSize"));
    	this.setViewunit((String) this.getFormHM().get("viewunit"));
    	this.setR3101((String) this.getFormHM().get("r3101"));
    	this.setR3122((String) this.getFormHM().get("r3122"));
    	this.setR3127((String) this.getFormHM().get("r3127"));
    	this.setR41list((ArrayList) this.getFormHM().get("r41list"));
    	this.setSql((String) this.getFormHM().get("sql"));
    	this.setWherestr((String) this.getFormHM().get("wherestr"));
    	this.setColumns((String) this.getFormHM().get("columns"));
    	this.setSetlist((ArrayList) this.getFormHM().get("setlist"));
    	this.setSplist((ArrayList) this.getFormHM().get("splist"));
    	this.setSpid((String) this.getFormHM().get("spid"));
    	this.setTablestr((String) this.getFormHM().get("tablestr"));
    	this.setStudyHour((String) this.getFormHM().get("studyHour"));
    	this.setIsAutoHour((String) this.getFormHM().get("isAutoHour"));
    	this.setPermisView((String) this.getFormHM().get("permisView"));
    	this.setInfo((String) this.getFormHM().get("info"));
    	this.setType((String) this.getFormHM().get("type"));
    	this.setEdit((String) this.getFormHM().get("edit"));
    	this.setNum((String)this.getFormHM().get("num"));

    	this.setOutName((String)this.getFormHM().get("outName"));
    	this.setMaps((HashMap)this.getFormHM().get("maps"));
    	this.setMsg((ArrayList)this.getFormHM().get("msg"));
    	this.getMsgPageForm().setList((ArrayList)this.getFormHM().get("msg"));

    	this.setPushitem((String)this.getFormHM().get("pushitem"));
    	
    	this.setContentEv((String)this.getFormHM().get("contentEv"));
		this.setContentid((String)this.getFormHM().get("contentid"));
		this.setWheresql((String)this.getFormHM().get("wheresql"));
		this.setReadonly((String)this.getFormHM().get("readonly"));
		this.setFieldSetDataList((ArrayList)this.getFormHM().get("fieldSetDataList"));
		this.getMsgPageForm().setList((ArrayList)this.getFormHM().get("msglist"));
		this.setMsglist((ArrayList)this.getFormHM().get("msglist"));
		this.setPrimarykeyLabel((String)this.getFormHM().get("primarykeyLabel"));
		this.setNumber((String)this.getFormHM().get("number"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setCodeid((String)this.getFormHM().get("codeid"));
		this.setFielditemlist((ArrayList)this.getFormHM().get("fielditemlist"));
		this.setStudent((String)this.getFormHM().get("student"));
		
		this.setDatelist((ArrayList)this.getFormHM().get("datelist"));
		this.setHsearchstr((String)this.getFormHM().get("hsearchstr"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setPersons((String)this.getFormHM().get("persons"));
    }

    @Override
    public void inPutTransHM()
    {
		this.getFormHM().put("spflag", this.getSpflag());
		this.getFormHM().put("timeflag", this.getTimeflag());
		this.getFormHM().put("startime", this.getStartime());
		this.getFormHM().put("endtime", this.getEndtime());
		this.getFormHM().put("searchstr", this.getSearchstr());
		this.getFormHM().put("r3122", this.getR3122());
		this.getFormHM().put("studyHour", this.getStudyHour());
		this.getFormHM().put("isAutoHour", this.getIsAutoHour());
		this.getFormHM().put("spid", this.getSpid());
		this.getFormHM().put("permisView", this.getPermisView());	
		this.getFormHM().put("info", this.getInfo());
		this.getFormHM().put("type", this.getType());	
		this.getFormHM().put("outName", this.getOutName());
		this.getFormHM().put("file", file);
		
		this.getFormHM().put("contentEv", this.getContentEv());
		this.getFormHM().put("fieldSetDataList", this.getFieldSetDataList());
		this.getFormHM().put("msglist", this.getMsglist());
		this.getFormHM().put("primarykeyLabel", this.getPrimarykeyLabel());
		this.getFormHM().put("number", this.getNumber());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("codeid", this.getCodeid());
		this.getFormHM().put(fielditemlist, this.getFielditemlist());
		this.getFormHM().put("student", this.getStudent());
		this.getFormHM().put("msg", this.getMsg());
		
		this.getFormHM().put("datelist", this.getDatelist());
		this.getFormHM().put("hsearchstr", this.getHsearchstr());
		this.getFormHM().put("sqlstr", this.getSqlstr());
		
		this.getFormHM().put("persons", this.getPersons());
    }

    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {

	try
	{
	    if ("/train/request/trainsData".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
	    {
	        if (this.getPagination() != null){
	            this.getPagination().firstPage();
	            this.pagerows=20;
	        }
	    } else if ("/train/request/trainCourse".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
	    {
		/** 定位到首页, */
	        if (this.getPagination() != null){
	            this.getPagination().firstPage();
	            this.pagerows=20;
	        }
	    } else if ("/train/request/trainStu".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
	    {
		/** 定位到首页, */
	        if (this.getPagination() != null){
	            this.getPagination().firstPage();
	            this.pagerows=20;
	        }
	    } else if ("/train/request/trainRes".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
	    {
		/** 定位到首页, */
	        if (this.getPagination() != null){
	            this.getPagination().firstPage();
	            this.pagerows=20;
	        }
	    } else if(arg1.getParameter("winState") != null && arg1.getParameter("winState").trim().length()>0){
	    	arg1.setAttribute("targetWindow", arg1.getParameter("winState"));
	    } else if("/train/request/import".equals(arg0.getPath()) && arg1.getParameter("b_importdata") != null){
	        arg1.setAttribute("targetWindow", "1");
	    } else if("/train/request/selectpre".equals(arg0.getPath()) && arg1.getParameter("b_query") != null){
	        arg1.setAttribute("targetWindow", "1");
        }
	    
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return super.validate(arg0, arg1);
    }

    public String getA_code()
    {

	return a_code;
    }

    public void setA_code(String a_code)
    {

	this.a_code = a_code;
    }

    public String getEndtime()
    {

	return endtime;
    }

    public void setEndtime(String endtime)
    {

	this.endtime = endtime;
    }

    public ArrayList getFlaglist()
    {

	return flaglist;
    }

    public void setFlaglist(ArrayList flaglist)
    {

	this.flaglist = flaglist;
    }

    public ArrayList getItemlist()
    {

	return itemlist;
    }

    public void setItemlist(ArrayList itemlist)
    {

	this.itemlist = itemlist;
    }

    public String getSpflag()
    {

	return spflag;
    }

    public void setSpflag(String spflag)
    {

	this.spflag = spflag;
    }

    public String getStartime()
    {

	return startime;
    }

    public void setStartime(String startime)
    {

	this.startime = startime;
    }

    public String getStrsql()
    {

	return strsql;
    }

    public void setStrsql(String strsql)
    {

	this.strsql = strsql;
    }

    public String getTimeflag()
    {

	return timeflag;
    }

    public void setTimeflag(String timeflag)
    {

	this.timeflag = timeflag;
    }

    public ArrayList getTimelist()
    {

	return timelist;
    }

    public void setTimelist(ArrayList timelist)
    {

	this.timelist = timelist;
    }

    public String getModel()
    {

	return model;
    }

    public void setModel(String model)
    {

	this.model = model;
    }

    public String getSearchstr()
    {

	return searchstr;
    }

    public void setSearchstr(String searchstr)
    {

	this.searchstr = searchstr;
    }

    public String getTablename()
    {

	return tablename;
    }

    public void setTablename(String tablename)
    {

	this.tablename = tablename;
    }

    public ArrayList getFieldlist()
    {

	return fieldlist;
    }

    public void setFieldlist(ArrayList fieldlist)
    {

	this.fieldlist = fieldlist;
    }

    public String getItem_field()
    {

	return item_field;
    }

    public void setItem_field(String item_field)
    {

	this.item_field = item_field;
    }

    public String getFieldsetid()
    {

	return fieldsetid;
    }

    public void setFieldsetid(String fieldsetid)
    {

	this.fieldsetid = fieldsetid;
    }

    public String[] getSort_recode()
    {

	return sort_recode;
    }

    public void setSort_recode(String[] sort_recode)
    {

	this.sort_recode = sort_recode;
    }

    public ArrayList getSortlist()
    {

	return sortlist;
    }

    public void setSortlist(ArrayList sortlist)
    {

	this.sortlist = sortlist;
    }

    public String getUsername()
    {

	return username;
    }

    public void setUsername(String username)
    {

	this.username = username;
    }

    public String getFieldSize()
    {

	return fieldSize;
    }

    public void setFieldSize(String fieldSize)
    {

	this.fieldSize = fieldSize;
    }

    public String getViewunit()
    {

	return viewunit;
    }

    public void setViewunit(String viewunit)
    {

	this.viewunit = viewunit;
    }

    public String getR3101()
    {

	return r3101;
    }

    public void setR3101(String r3101)
    {

	this.r3101 = r3101;
    }

    public String getR3122()
    {

	return r3122;
    }

    public void setR3122(String r3122)
    {

	this.r3122 = r3122;
    }

    public String getR3127()
    {

	return r3127;
    }

    public void setR3127(String r3127)
    {

	this.r3127 = r3127;
    }

    public ArrayList getR41list()
    {

	return r41list;
    }

    public void setR41list(ArrayList r41list)
    {

	this.r41list = r41list;
    }

    public String getSql()
    {

	return sql;
    }

    public void setSql(String sql)
    {

	this.sql = sql;
    }

    public ArrayList getSetlist()
    {

	return setlist;
    }

    public void setSetlist(ArrayList setlist)
    {

	this.setlist = setlist;
    }

    public String getColumns()
    {

	return columns;
    }

    public void setColumns(String columns)
    {

	this.columns = columns;
    }

    public String getWherestr()
    {

	return wherestr;
    }

    public void setWherestr(String wherestr)
    {

	this.wherestr = wherestr;
    }

    public ArrayList getSplist()
    {

	return splist;
    }

    public void setSplist(ArrayList splist)
    {

	this.splist = splist;
    }

    public String getSpid()
    {

	return spid;
    }

    public void setSpid(String spid)
    {

	this.spid = spid;
    }

    public String getTablestr()
    {

	return tablestr;
    }

    public void setTablestr(String tablestr)
    {

	this.tablestr = tablestr;
    }

    public String getStudyHour()
    {

	return studyHour;
    }

    public void setStudyHour(String studyHour)
    {

	this.studyHour = studyHour;
    }

    public String getIsAutoHour()
    {

	return isAutoHour;
    }

    public void setIsAutoHour(String isAutoHour)
    {

	this.isAutoHour = isAutoHour;
    }

    public String getPermisView()
    {

	return permisView;
    }

    public void setPermisView(String permisView)
    {

	this.permisView = permisView;
    }

    public String getInfo()
    {
    
        return info;
    }

    public void setInfo(String info)
    {
    
        this.info = info;
    }

    public String getType()
    {
    
        return type;
    }

    public void setType(String type)
    {
    
        this.type = type;
    }

	public String getEdit() {
		return edit;
	}

	public void setEdit(String edit) {
		this.edit = edit;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}


	public String getOutName() {
		return outName;
	}

	public void setOutName(String outName) {
		this.outName = outName;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}
	
    public HashMap getMaps() {
		return maps;
	}

	public void setMaps(HashMap maps) {
		this.maps = maps;
	}

	public PaginationForm getMsgPageForm() {
		return msgPageForm;
	}

	public void setMsgPageForm(PaginationForm msgPageForm) {
		this.msgPageForm = msgPageForm;
	}

	public ArrayList getMsg() {
		return msg;
	}

	public void setMsg(ArrayList msg) {
		this.msg = msg;
	}

	public String getPushitem() {
		return pushitem;
	}

	public void setPushitem(String pushitem) {
		this.pushitem = pushitem;
	}

	public String getContentEv() {
		return contentEv;
	}

	public void setContentEv(String contentEv) {
		this.contentEv = contentEv;
	}

	public String getContentid() {
		return contentid;
	}

	public void setContentid(String contentid) {
		this.contentid = contentid;
	}

	public String getWheresql() {
		return wheresql;
	}

	public void setWheresql(String wheresql) {
		this.wheresql = wheresql;
	}

	public String getReadonly() {
		return readonly;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	public ArrayList getFieldSetDataList() {
		return fieldSetDataList;
	}

	public void setFieldSetDataList(ArrayList fieldSetDataList) {
		this.fieldSetDataList = fieldSetDataList;
	}

	public ArrayList getMsglist() {
		return msglist;
	}

	public void setMsglist(ArrayList msglist) {
		this.msglist = msglist;
	}

	public String getPrimarykeyLabel() {
		return primarykeyLabel;
	}

	public void setPrimarykeyLabel(String primarykeyLabel) {
		this.primarykeyLabel = primarykeyLabel;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getCodeid() {
		return codeid;
	}

	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}

	public ArrayList getFielditemlist() {
		return fielditemlist;
	}

	public void setFielditemlist(ArrayList fielditemlist) {
		this.fielditemlist = fielditemlist;
	}

	public String getStudent() {
		return student;
	}

	public void setStudent(String student) {
		this.student = student;
	}

	public ArrayList getDatelist() {
		return datelist;
	}

	public void setDatelist(ArrayList datelist) {
		this.datelist = datelist;
	}

	public String getHsearchstr() {
		return hsearchstr;
	}

	public void setHsearchstr(String hsearchstr) {
		this.hsearchstr = hsearchstr;
	}

    public String getSqlstr() {
        return sqlstr;
    }

    public void setSqlstr(String sqlstr) {
        this.sqlstr = sqlstr;
    }

	public String getPersons() {
		return persons;
	}

	public void setPersons(String persons) {
		this.persons = persons;
	}

}
