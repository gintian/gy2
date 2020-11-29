package com.hjsj.hrms.actionform.general.template;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;

public class TemplateHistorydataForm extends FrameForm {
	 private PaginationForm templateHistorydataForm=new PaginationForm();
	 private ArrayList headSetList=new ArrayList(); 
	 private HttpSession session2;
	 private String    table_name="";
	 private String    tabid="";
	 private String  condition;
	 private String startdate;
	 private String appDate;
	 private String codeid;
	 private String hmuster_sql;
	 private String returnflag;
	 private String strsql;//查询数据的sql
	 private String orderBy;//排序条件
	 private String display_e0122;//部门几级显示
	 private String columns;//列字段

	 /**资源类型*/
	 	private String type;
		private String bostype;
		private String res_flag;
		/**左边业务模板树形结构*/
		private String bs_tree;
		/**查询条件*/
		private String select_type="";
		private String tabidtemp="";
		private String returnback="";
	public String getBs_tree() {
			return bs_tree;
		}


		public void setBs_tree(String bs_tree) {
			this.bs_tree = bs_tree;
		}


	public String getBostype() {
			return bostype;
		}


		public void setBostype(String bostype) {
			this.bostype = bostype;
		}


		public String getRes_flag() {
			return res_flag;
		}


		public void setRes_flag(String res_flag) {
			this.res_flag = res_flag;
		}


	public String getTabid() {
		return tabid;
	}


	public void setTabid(String tabid) {
		this.tabid = tabid;
	}


	@Override
    public void inPutTransHM() {
		this.getFormHM().put("session2",this.session2);
		this.getFormHM().put("selectedlist",(ArrayList)this.getTemplateHistorydataForm().getSelectedList());
		this.getFormHM().put("res_flag",this.getRes_flag());
		this.getFormHM().put("type",this.getType());
		this.getFormHM().put("tabidtemp",this.getTabidtemp());
		this.getFormHM().put("columns",this.getColumns());
		this.getFormHM().put("strsql",this.getStrsql());
		this.getFormHM().put("orderBy",this.getOrderBy());
		this.getFormHM().put("display_e0122",this.getDisplay_e0122());
	}





	@Override
    public void outPutFormHM() {
		
		this.setHeadSetList((ArrayList)this.getFormHM().get("headSetList"));
		this.setSession2(this.session2);
		this.getTemplateHistorydataForm().setList((ArrayList)this.getFormHM().get("historylist"));
		this.setTable_name((String)this.getFormHM().get("table_name"));
		this.setTabid((String)this.getFormHM().get("tabid"));
		this.setStartdate((String)this.getFormHM().get("startdate"));
		this.setAppDate((String)this.getFormHM().get("appDate"));
		this.setCodeid((String)this.getFormHM().get("codeid"));
		this.setHmuster_sql((String)this.getFormHM().get("hmuster_sql"));
		this.setBostype((String)this.getFormHM().get("bostype"));
		this.setBs_tree((String)this.getFormHM().get("bs_tree"));
		this.setCondition((String)this.getFormHM().get("condition"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setSelect_type((String)this.getFormHM().get("select_type"));
		this.setTabidtemp((String)this.getFormHM().get("tabidtemp"));
		this.setReturnback((String)this.getFormHM().get("returnback"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setOrderBy((String)this.getFormHM().get("orderBy"));
		this.setDisplay_e0122((String)this.getFormHM().get("display_e0122"));
		if(this.getFormHM().get("pagerows")==null||((String)this.getFormHM().get("pagerows")).trim().length()==0)
			this.setPagerows(20);
		else
			this.setPagerows(Integer.parseInt(((String)this.getFormHM().get("pagerows"))));
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		HttpSession session = arg1.getSession();
		this.setSession2(arg1.getSession());
		return super.validate(arg0, arg1);
	}
	
	public PaginationForm getTemplateHistorydataForm() {
		return templateHistorydataForm;
	}


	public void setTemplateHistorydataForm(PaginationForm templateHistorydataForm) {
		this.templateHistorydataForm = templateHistorydataForm;
	}



	public ArrayList getHeadSetList() {
		return headSetList;
	}


	public void setHeadSetList(ArrayList headSetList) {
		this.headSetList = headSetList;
	}


	public HttpSession getSession2() {
		return session2;
	}


	public void setSession2(HttpSession session2) {
		this.session2 = session2;
	}

	public String getTable_name() {
		return table_name;
	}


	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	public String getCondition() {
		return condition;
	}


	public void setCondition(String condition) {
		this.condition = condition;
	}


	public String getStartdate() {
		return startdate;
	}


	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}


	public String getAppDate() {
		return appDate;
	}


	public void setAppDate(String appDate) {
		this.appDate = appDate;
	}


	public String getCodeid() {
		return codeid;
	}


	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}


	public String getHmuster_sql() {
		return hmuster_sql;
	}


	public void setHmuster_sql(String hmuster_sql) {
		this.hmuster_sql = hmuster_sql;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	@Override
    public String getReturnflag() {
		return returnflag;
	}


	@Override
    public void setReturnflag(String returnflag) {
		this.returnflag = returnflag;
	}


	public String getSelect_type() {
		return select_type;
	}


	public void setSelect_type(String select_type) {
		this.select_type = select_type;
	}


	public String getTabidtemp() {
		return tabidtemp;
	}


	public void setTabidtemp(String tabidtemp) {
		this.tabidtemp = tabidtemp;
	}


	public String getReturnback() {
		return returnback;
	}


	public void setReturnback(String retrunback) {
		this.returnback = retrunback;
	}
	
	 public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getDisplay_e0122() {
			return display_e0122;
		}

	public void setDisplay_e0122(String display_e0122) {
		this.display_e0122 = display_e0122;
	}
	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	 public String getStrsql() {
		return strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}
}
