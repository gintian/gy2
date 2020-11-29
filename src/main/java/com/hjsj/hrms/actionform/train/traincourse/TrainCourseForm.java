package com.hjsj.hrms.actionform.train.traincourse;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class TrainCourseForm extends FrameForm {
	private String a_code = ""; // 机构代码
	private String tablename = "";
	private ArrayList flaglist = new ArrayList();
	private String spflag = ""; // 审批标识
	private String strsql = ""; // 查询语句
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
	private String[] sort_recode;
	private String username = "";
	private String fieldSize = "";
	private String viewunit = "";
	private String r2501 = "";
	private String sql = "";
	private String wherestr = "";
	private String columns = "";
	private String edit = "";
	private String returnvalue;
	private String isPriv;
	private String b0110;
	private String e0122;

	private String contentEv = "";
	private String contentid = "";
	private String wheresql = "";
	private String readonly = "";
	private String pivFlag = "";
	private String encryptParam = "";
	
	private String classid = "";

	@Override
    public void outPutFormHM() {

		this.setIsPriv((String) this.getFormHM().get("isPriv"));
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
		this.setR2501((String) this.getFormHM().get("r2501"));
		this.setSql((String) this.getFormHM().get("sql"));
		this.setWherestr((String) this.getFormHM().get("wherestr"));
		this.setColumns((String) this.getFormHM().get("columns"));
		this.setEdit((String) this.getFormHM().get("edit"));

		this.setContentEv((String) this.getFormHM().get("contentEv"));
		this.setContentid((String) this.getFormHM().get("contentid"));
		this.setWheresql((String) this.getFormHM().get("wheresql"));
		this.setReadonly((String) this.getFormHM().get("readonly"));
		this.setB0110((String) this.getFormHM().get("b0110"));
		this.setE0122((String) this.getFormHM().get("e0122"));
		this.setEncryptParam((String)this.getFormHM().get("encryptParam"));
		this.setClassid((String)this.getFormHM().get("classid"));
	}

	@Override
    public void inPutTransHM() {

		this.getFormHM().put("isPriv", this.getIsPriv());
		this.getFormHM().put("spflag", this.getSpflag());
		this.getFormHM().put("timeflag", this.getTimeflag());
		this.getFormHM().put("startime", this.getStartime());
		this.getFormHM().put("endtime", this.getEndtime());
		this.getFormHM().put("searchstr", this.getSearchstr());

		this.getFormHM().put("contentEv", this.getContentEv());
		this.getFormHM().put("b0110", this.getB0110());
		this.getFormHM().put("e0122", this.getE0122());
		this.getFormHM().put("pivFlag", this.getPivFlag());
		this.getFormHM().put("encryptParam", this.getEncryptParam());
		
		this.getFormHM().put("classid", this.getClassid());
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		try {
			if ("/train/trainCosts/trainCosts".equals(arg0.getPath())
					&& arg1.getParameter("b_query") != null) {
				if (this.getPagination() != null)
					this.getPagination().firstPage();
			} else if ("/train/b_plan/train".equals(arg0.getPath())
					&& arg1.getParameter("b_query") != null) {
				if (this.getPagination() != null)
					this.getPagination().firstPage();
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	public String getR2501() {
		return r2501;
	}

	public void setR2501(String r2501) {
		this.r2501 = r2501;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getWherestr() {
		return wherestr;
	}

	public void setWherestr(String wherestr) {
		this.wherestr = wherestr;
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

	public String getIsPriv() {
		return isPriv;
	}

	public void setIsPriv(String isPriv) {
		this.isPriv = isPriv;
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

	public String getB0110() {
		return b0110;
	}

	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}

	public String getE0122() {
		return e0122;
	}

	public void setE0122(String e0122) {
		this.e0122 = e0122;
	}

    public String getPivFlag() {
        return pivFlag;
    }

    public void setPivFlag(String pivFlag) {
        this.pivFlag = pivFlag;
    }

    public String getEncryptParam() {
        return encryptParam;
    }

    public void setEncryptParam(String encryptParam) {
        this.encryptParam = encryptParam;
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

}
