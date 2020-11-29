package com.hjsj.hrms.actionform.train.b_plan;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class PlanTrainForm extends FrameForm {
	private String a_code = ""; //机构代码
	private String tablename=""; 
	private ArrayList flaglist = new ArrayList(); 
	private String spflag = ""; //审批标识
	private String strsql = ""; //查询语句
	private String sqlstr = ""; //导出文件的查询语句
	private ArrayList timelist = new ArrayList(); 
	private String timeflag = ""; //时间
	private String startime = ""; //开始时间
	private String endtime = ""; //结束时间
	private ArrayList itemlist = new ArrayList(); //显示指标
	private String model = ""; //1.计划制订 2.计划审核 3.培训费用
	private String model1 = ""; //用于保存model的值以区分是否要清空查询的条件
	private String searchstr = ""; //查询条件
	private ArrayList fieldlist = new ArrayList(); //设置查询指标集
	private String item_field=""; //查询指标
	private String fieldsetid="";
	private ArrayList sortlist = new ArrayList(); //记录排序
	private String[] sort_recode;
	private String username="";
	private String fieldSize="";
	private String viewunit="";
	private String novalue="";
	private String type="";
	private ArrayList dlist = new ArrayList(); 	//记录知识点
	private ArrayList tlist = new ArrayList();  //记录列名
	private String questionClass;			    //试题分类
	private ArrayList valueMapList = new ArrayList(); //键值对
	private ArrayList searchlist = new ArrayList(); //常用查询
	private ArrayList titlelist = new ArrayList(); //常用查询
	private String titleid="";
	private String edit=""; //是否可以编辑
	private String returnvalue;
	private FormFile file;
	private Object[] maps;
	private ArrayList msglist = new ArrayList();
	private PaginationForm msgPageForm=new PaginationForm();
	private String isupdate;
	private int [] counts;
	private String num;
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setFlaglist((ArrayList)this.getFormHM().get("flaglist"));
		this.setSpflag((String)this.getFormHM().get("spflag"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setTimelist((ArrayList)this.getFormHM().get("timelist"));
		this.setTimeflag((String)this.getFormHM().get("timeflag"));
		this.setStartime((String)this.getFormHM().get("startime"));
		this.setEndtime((String)this.getFormHM().get("endtime"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setModel((String)this.getFormHM().get("model"));
		this.setSearchstr((String)this.getFormHM().get("searchstr"));
		this.setTablename((String)this.getFormHM().get("tablename"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setItem_field((String)this.getFormHM().get("item_field"));
		this.setFieldsetid((String)this.getFormHM().get("fieldsetid"));
		this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));
		this.setSort_recode((String[])this.getFormHM().get("sort_recode"));
		this.setUsername((String)this.getFormHM().get("username"));
		this.setFieldSize((String)this.getFormHM().get("fieldSize"));
		this.setViewunit((String)this.getFormHM().get("viewunit"));
		this.setNovalue((String)this.getFormHM().get("novalue"));
		this.setType((String)this.getFormHM().get("type"));
		this.setSearchlist((ArrayList)this.getFormHM().get("searchlist"));
		this.setTitlelist((ArrayList)this.getFormHM().get("titlelist"));
		this.setTitleid((String)this.getFormHM().get("titleid"));
		this.setEdit((String)this.getFormHM().get("edit"));
		this.setMaps((Object[])this.getFormHM().get("maps"));
		this.setMsglist((ArrayList)this.getFormHM().get("msglist"));
		this.setIsupdate((String)this.getFormHM().get("isupdate"));
		this.getMsgPageForm().setList((ArrayList)this.getFormHM().get("msglist"));
		this.setCounts((int[])this.getFormHM().get("counts"));
		this.setNum((String)this.getFormHM().get("num"));
		this.setDList((ArrayList)this.getFormHM().get("dlist"));
		this.setValueMapList(((ArrayList)this.getFormHM().get("valueMapList")));
		this.setTlist(((ArrayList)this.getFormHM().get("tlist")));
		this.setQuestionClass((String) this.getFormHM().get("questionClass"));
		
		this.setModel1((String) this.getFormHM().get("model1"));
		this.setSqlstr((String) this.getFormHM().get("sqlstr"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("spflag",this.getSpflag());
		this.getFormHM().put("timeflag",this.getTimeflag());
		this.getFormHM().put("startime",this.getStartime());
		this.getFormHM().put("endtime",this.getEndtime());
		this.getFormHM().put("searchstr",this.getSearchstr());
		this.getFormHM().put("file", file);
		this.getFormHM().put("maps", maps);
		this.getFormHM().put("dlist", dlist);
		this.getFormHM().put("tlist", tlist);
		this.getFormHM().put("value2Map", valueMapList);
		this.getFormHM().put("questionClass", this.getQuestionClass());
		this.getFormHM().put("model1", this.getModel1());
		this.getFormHM().put("sqlstr", this.getSqlstr());
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/train/b_plan/planTrain".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
			if(this.getPagination()!=null)
            	this.getPagination().firstPage();//?    
        }else if("/train/b_plan/planTrain/import".equals(arg0.getPath())&&arg1.getParameter("b_exedata")!=null)
        {
        	this.setPagerows(0);
        }else if("/train/b_plan/planTrain/import".equals(arg0.getPath())&&arg1.getParameter("b_importdata")!=null)
        {
            arg1.setAttribute("targetWindow", "1");
        }else if("/train/trainexam/question/import".equals(arg0.getPath())&&arg1.getParameter("b_importdata")!=null)
        {
            if(this.getPagination()!=null)
                this.getPagination().firstPage();
            
            arg1.setAttribute("targetWindow", "1");
        }

		return super.validate(arg0, arg1);
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public ArrayList getFlaglist() {
		return flaglist;
	}

	public void setFlaglist(ArrayList flaglist) {
		this.flaglist = flaglist;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getSpflag() {
		return spflag;
	}

	public void setSpflag(String spflag) {
		this.spflag = spflag;
	}

	public String getStartime() {
		return startime;
	}

	public void setStartime(String startime) {
		this.startime = startime;
	}

	public String getStrsql() {
		return strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	public String getTimeflag() {
		return timeflag;
	}

	public void setTimeflag(String timeflag) {
		this.timeflag = timeflag;
	}

	public ArrayList getTimelist() {
		return timelist;
	}

	public void setTimelist(ArrayList timelist) {
		this.timelist = timelist;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getModel1() {
		return model1;
	}

	public void setModel1(String model1) {
		this.model1 = model1;
	}

	public String getSearchstr() {
		return searchstr;
	}

	public void setSearchstr(String searchstr) {
		this.searchstr = searchstr;
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

	public String getItem_field() {
		return item_field;
	}

	public void setItem_field(String item_field) {
		this.item_field = item_field;
	}

	public String getFieldsetid() {
		return fieldsetid;
	}

	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}

	public String[] getSort_recode() {
		return sort_recode;
	}

	public void setSort_recode(String[] sort_recode) {
		this.sort_recode = sort_recode;
	}

	public ArrayList getSortlist() {
		return sortlist;
	}

	public void setSortlist(ArrayList sortlist) {
		this.sortlist = sortlist;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFieldSize() {
		return fieldSize;
	}

	public void setFieldSize(String fieldSize) {
		this.fieldSize = fieldSize;
	}

	public String getViewunit() {
		return viewunit;
	}

	public void setViewunit(String viewunit) {
		this.viewunit = viewunit;
	}

	public String getNovalue() {
		return novalue;
	}

	public void setNovalue(String novalue) {
		this.novalue = novalue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList getSearchlist() {
		return searchlist;
	}

	public void setSearchlist(ArrayList searchlist) {
		this.searchlist = searchlist;
	}

	public String getTitleid() {
		return titleid;
	}

	public void setTitleid(String titleid) {
		this.titleid = titleid;
	}

	public ArrayList getTitlelist() {
		return titlelist;
	}

	public void setTitlelist(ArrayList titlelist) {
		this.titlelist = titlelist;
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

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public Object[] getMaps() {
		return maps;
	}

	public void setMaps(Object[] maps) {
		this.maps = maps;
	}

	public ArrayList getMsglist() {
		return msglist;
	}

	public void setMsglist(ArrayList msglist) {
		this.msglist = msglist;
	}

	public String getIsupdate() {
		return isupdate;
	}

	public void setIsupdate(String isupdate) {
		this.isupdate = isupdate;
	}

	public PaginationForm getMsgPageForm() {
		return msgPageForm;
	}

	public void setMsgPageForm(PaginationForm msgPageForm) {
		this.msgPageForm = msgPageForm;
	}

	public int[] getCounts() {
		return counts;
	}

	public void setCounts(int[] counts) {
		this.counts = counts;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public ArrayList getDList() {
		return dlist;
	}

	public void setDList(ArrayList list) {
		dlist = list;
	}

	public ArrayList getDlist() {
		return dlist;
	}

	public void setDlist(ArrayList dlist) {
		this.dlist = dlist;
	}


	public ArrayList getTlist() {
		return tlist;
	}

	public void setTlist(ArrayList tlist) {
		this.tlist = tlist;
	}

	public ArrayList getValueMapList() {
		return valueMapList;
	}

	public void setValueMapList(ArrayList valueMapList) {
		this.valueMapList = valueMapList;
	}

	public String getQuestionClass() {
		return questionClass;
	}

	public void setQuestionClass(String questionClass) {
		this.questionClass = questionClass;
	}

    public String getSqlstr() {
        return sqlstr;
    }

    public void setSqlstr(String sqlstr) {
        this.sqlstr = sqlstr;
    }

}
