package com.hjsj.hrms.actionform.kq.register;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class DailyRegisterForm extends FrameForm {

	private String treeCode;// 树形菜单，在HtmlMenu中
	private String userbase; // **应用库表前缀*/
	private String setname = "A01";// **记录集名称*/
	private String registerdate;// 登记日期
	private String code;// 连接级别
	private String dbcond;//
	private String kind;
	private String collectdate;
	private String columns;
	private ArrayList datelist = new ArrayList();// 日期list
	private ArrayList showalldate = new ArrayList();// 考勤日明细显示全部数据的日期列表
	private ArrayList fielditemlist = new ArrayList();
	private ArrayList fieldlist = new ArrayList();
	private String s_strsql;
	private String s_columns;
	private String sqlstr;
	private String strwhere;
	private String orderby;
	private PaginationForm recordListForm = new PaginationForm();
	private PaginationForm singListForm = new PaginationForm();
	private ArrayList list = new ArrayList();
	private String A0100;
	private String dbase;
	private String validate;
	private String num;
	private String changestatus;
	private String rest_state;
	private String start_date;
	private String end_date;
	private String kq_duration;
	private String creat_duration;
	private ArrayList kq_dbase_list = new ArrayList();
	private RecordVo one_vo = new RecordVo("Q05", 1);
	private ArrayList courselist = new ArrayList();
	private String workcalendar;
	private String count_type;// 计算标志
	private String count_duration;
	private String count_start;
	private String count_end;
	private ArrayList vo_list = new ArrayList();
	private String infor_Flag = "1";
	private String po_wherestr;
	private String po_column;
	private String po_sqlstr;
	private String destfld;
	private String temp_table;
	private String bytesid;
	private String setlist;
	private String pigeonhole_flag;
	private String showtype;
	private ArrayList showtypelist = new ArrayList();
	private String relatTableid;// 高级花名册对应的单表名称
	private String condition;// 高级花名册打印的条件
	private String returnURL;// 返回的连接
	private String error_message;
	private String error_flag;
	private String error_return;
	private String error_stuts;
	private String pick_type;
	private String creat_type;
	private String creat_pick;
	private String creat_state;// 生成日明细数据是否重新生成已上报人员数据
	private ArrayList kq_list = new ArrayList();// 人员库
	private String select_name;// 筛选名字
	private String select_flag;// 筛选表示
	private String select_type = "0";
	private String select_pre;
	/************** 表单提交数据 ****************/
	private String re_flag;// 返回也面标记
	private String overrule;
	private String overrule_status;
	private String state_message;
	/** 审批状态 */
	private String sp_flag;
	/**** 审批结果 *****/
	private String sp_result;// 07:报审成功;17:报审失败
	private String re_url;
	private String re_target;
	private String up_dailyregister;
	private String selectResult;//
	private String selectsturs;// 查询标记
	private ArrayList overrulelist = new ArrayList();
	private String settype;
	private String returnvalue = "1";
	/** 审批状态列表 */
	private ArrayList splist = new ArrayList();
	ArrayList forms = new ArrayList();

	/** 人员库列表 */
	private ArrayList dblist = new ArrayList();
	/******* 查询 ********/
	/** 关系操作符 */
	private ArrayList operlist = new ArrayList();
	/** 逻辑操作符 */
	private ArrayList logiclist = new ArrayList();

	private ArrayList selectedlist = new ArrayList();
	/** factor list */
	private ArrayList factorlist = new ArrayList();
	private String like;
	private String left_fields[];
	/** 选中的字段名数组 */
	private String right_fields[];
	/** 能用查询的表达式:!(1+2*3),!非，＋或，*且 */
	private String expression;
	private HashMap kqItem_hash = new HashMap();
	/** 员工日明细批量修改 */
	private ArrayList batchlist = new ArrayList();
	private String selectys;// 转换小时 1=默认；2=HH:MM
	private String uplevel; // 显示层级
	private String flag; // 0:日明细;1:月汇总；2：不定期

	private FormFile file;
	private String msg;
	private String lockedNum;

	private String checkflag;
	private String itemid;
	private ArrayList itemlist;
	private String temporder;
	private String sortitem;
	private String salaryid;

	private String kqitem;
	private String kqstatus;
	private String status_flag;

	private ArrayList kqitemlist;
	private ArrayList showlist;
	private ArrayList listid;
	private HashMap collectMap;
	private ArrayList kqstatuslist;
	
	private String haveApprove;//有审批权限
	private String moduleFlag;//是自助1还是业务

	public int getColumnCount() {
		String[] columnitem= columns.split(",");
		int columnCount=columnitem.length;
		return columnCount;
	}

	
	public String getModuleFlag() {
		return moduleFlag;
	}


	public void setModuleFlag(String moduleFlag) {
		this.moduleFlag = moduleFlag;
	}


	public String getHaveApprove() {
		return haveApprove;
	}


	public void setHaveApprove(String haveApprove) {
		this.haveApprove = haveApprove;
	}


	public String getKqstatus() {
		return kqstatus;
	}
	
	public void setKqstatus(String kqstatus) {
		this.kqstatus = kqstatus;
	}
	
	public String getStatus_flag() {
		return status_flag;
	}

	public void setStatus_flag(String statusFlag) {
		status_flag = statusFlag;
	}

	public ArrayList getKqstatuslist() {
		return kqstatuslist;
	}

	public void setKqstatuslist(ArrayList kqstatuslist) {
		this.kqstatuslist = kqstatuslist;
	}

	public HashMap getCollectMap() {
		return collectMap;
	}

	public ArrayList getShowalldate() {
		return showalldate;
	}

	public void setShowalldate(ArrayList showalldate) {
		this.showalldate = showalldate;
	}

	public void setCollectMap(HashMap collectMap) {
		this.collectMap = collectMap;
	}

	public String getKqitem() {
		return kqitem;
	}

	public void setKqitem(String kqitem) {
		this.kqitem = kqitem;
	}

	public ArrayList getKqitemlist() {
		return kqitemlist;
	}

	public void setKqitemlist(ArrayList kqitemlist) {
		this.kqitemlist = kqitemlist;
	}

	public String getSortitem() {
		return sortitem;
	}

	public void setSortitem(String sortitem) {
		this.sortitem = sortitem;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getTemporder() {
		return temporder;
	}

	public void setTemporder(String temporder) {
		this.temporder = temporder;
	}

	public String getLockedNum() {
		return lockedNum;
	}

	public void setLockedNum(String lockedNum) {
		this.lockedNum = lockedNum;
	}

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}

	public ArrayList getOperlist() {
		return operlist;
	}

	public void setOperlist(ArrayList operlist) {
		this.operlist = operlist;
	}

	public ArrayList getLogiclist() {
		return logiclist;
	}

	public void setLogiclist(ArrayList logiclist) {
		this.logiclist = logiclist;
	}

	public ArrayList getSelectedlist() {
		return selectedlist;
	}

	public void setSelectedlist(ArrayList selectedlist) {
		if (selectedlist == null)
			selectedlist = new ArrayList();
		this.selectedlist = selectedlist;
	}

	public ArrayList getFactorlist() {
		return factorlist;
	}

	public void setFactorlist(ArrayList factorlist) {
		this.factorlist = factorlist;
	}

	/*******************/
	// **********链接树**********
	public String getTreeCode() {
		return treeCode;
	}

	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}

	// ***********人员库************
	public String getUserbase() {
		return userbase;
	}

	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}

	// ***********登记日期**************
	public String getRegisterdate() {
		return registerdate;
	}

	public void setRegisterdate(String registerdate) {
		this.registerdate = registerdate;
	}

	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            The code to set.
	 */
	public void setCode(String code) {
		this.code = code;
	}

	public DailyRegisterForm() {
		CommonData vo = new CommonData("=", "=");
		operlist.add(vo);
		vo = new CommonData(">", ">");
		operlist.add(vo);
		vo = new CommonData(">=", ">=");
		operlist.add(vo);
		vo = new CommonData("<", "<");
		operlist.add(vo);
		vo = new CommonData("<=", "<=");
		operlist.add(vo);
		vo = new CommonData("<>", "<>");
		operlist.add(vo);
		// vo=new CommonData("like","包含");
		// operlist.add(vo);
		vo = new CommonData("*", "并且");
		logiclist.add(vo);
		vo = new CommonData("+", "或");
		logiclist.add(vo);
	}

	@Override
    public void outPutFormHM() {
		this.setTreeCode((String) this.getFormHM().get("treeCode"));
		this.setDbcond((String) this.getFormHM().get("dbcond"));
		this.setDbase((String) this.getFormHM().get("dbase"));
		this.setUserbase((String) this.getFormHM().get("userbase"));
		this.setCode((String) this.getFormHM().get("code"));
		this.setDatelist((ArrayList) this.getFormHM().get("datelist"));
		this.setShowalldate((ArrayList) this.getFormHM().get("showalldate"));
		this.setSqlstr((String) this.getFormHM().get("sqlstr"));
		this.setColumns((String) this.getFormHM().get("columns"));
		this.setStrwhere((String) this.getFormHM().get("strwhere"));
		this.setOrderby((String) this.getFormHM().get("orderby"));
		this.setKind((String) this.getFormHM().get("kind"));
		this.setRest_state((String) this.getFormHM().get("rest_state"));
		this
				.setFielditemlist((ArrayList) this.getFormHM().get(
						"fielditemlist"));
		this.setCollectdate((String) this.getFormHM().get("collectdate"));
		this.setRegisterdate((String) this.getFormHM().get("registerdate"));
		this.setValidate((String) this.getFormHM().get("validate"));
		this.setNum((String) this.getFormHM().get("num"));
		// this.getRecordListForm().setList((ArrayList)this.getFormHM().get("changelist"));
		this.getSingListForm().setList(
				(ArrayList) this.getFormHM().get("vo_list"));
		this.setChangestatus((String) this.getFormHM().get("changestatus"));
		this.setStart_date((String) this.getFormHM().get("start_date"));
		this.setEnd_date((String) this.getFormHM().get("end_date"));
		this.setKq_duration((String) this.getFormHM().get("kq_duration"));
		this.setCreat_duration((String) this.getFormHM().get("creat_duration"));
		this
				.setKq_dbase_list((ArrayList) this.getFormHM().get(
						"kq_dbase_list"));
		this.setOne_vo((RecordVo) this.getFormHM().get("one_vo"));
		this.setWorkcalendar((String) this.getFormHM().get("workcalendar"));
		this.setCourselist((ArrayList) this.getFormHM().get("courselist"));
		this.setCount_duration((String) this.getFormHM().get("count_duration"));
		this.setCount_type((String) this.getFormHM().get("count_type"));
		this.setVo_list((ArrayList) this.getFormHM().get("vo_list"));
		this.getRecordListForm().setList(
				(ArrayList) this.getFormHM().get("vo_list"));
		this.setS_columns((String) this.getFormHM().get("s_columns"));
		this.setS_strsql((String) this.getFormHM().get("s_strsql"));
		this.setFieldlist((ArrayList) this.getFormHM().get("fieldlist"));
		this.setPo_sqlstr((String) this.getFormHM().get("po_sqlstr"));
		this.setPo_wherestr((String) this.getFormHM().get("po_wherestr"));
		this.setPo_column((String) this.getFormHM().get("po_column"));
		this.setDestfld((String) this.getFormHM().get("destfld"));
		this.setTemp_table((String) this.getFormHM().get("temp_table"));
		this.setBytesid((String) this.getFormHM().get("bytesid"));
		this.setPigeonhole_flag((String) this.getFormHM()
				.get("pigeonhole_flag"));
		this.setShowtype((String) this.getFormHM().get("showtype"));
		this.setShowtypelist((ArrayList) this.getFormHM().get("showtypelist"));
		this.setCondition((String) this.getFormHM().get("condition"));
		this.setReturnURL((String) this.getFormHM().get("returnURL"));
		this.setRelatTableid((String) this.getFormHM().get("relatTableid"));
		this.setError_flag((String) this.getFormHM().get("error_flag"));
		this.setError_message((String) this.getFormHM().get("error_message"));
		this.setError_return((String) this.getFormHM().get("error_return"));
		this.setError_stuts((String) this.getFormHM().get("error_stuts"));
		this.setRe_flag((String) this.getFormHM().get("re_flag"));
		this.setOverrule((String) this.getFormHM().get("overrule"));
		this.setPick_type((String) this.getFormHM().get("pick_type"));
		this.setState_message((String) this.getFormHM().get("state_message"));
		this.setPigeonhole_flag((String) this.getFormHM()
				.get("pigeonhole_flag"));
		this.setCreat_type((String) this.getFormHM().get("creat_type"));
		this.setCreat_pick((String) this.getFormHM().get("creat_pick"));
		this.setCreat_state((String) this.getFormHM().get("creat_state"));
		this.setOverrule_status((String) this.getFormHM()
				.get("overrule_status"));
		this.setSplist((ArrayList) this.getFormHM().get("splist"));
		this.setKq_list((ArrayList) this.getFormHM().get("kq_list"));
		this.setSelect_name((String) this.getFormHM().get("select_name"));
		this.setSelect_flag((String) this.getFormHM().get("select_flag"));
		this.setSelect_pre((String) this.getFormHM().get("select_pre"));
		this.setSp_result((String) this.getFormHM().get("sp_result"));
		this.setRe_url((String) this.getFormHM().get("re_url"));
		this.setRe_target((String) this.getFormHM().get("re_target"));
		this.setUp_dailyregister((String) this.getFormHM().get(
				"up_dailyregister"));
		this.setOverrulelist((ArrayList) this.getFormHM().get("overrulelist"));
		this.setSelectResult((String) this.getFormHM().get("selectResult"));

		this.setSelectedlist((ArrayList) this.getFormHM().get("selectedlist"));
		this.setFactorlist((ArrayList) this.getFormHM().get("factorlist"));
		this.setSelectsturs((String) this.getFormHM().get("selectsturs"));
		this.setKqItem_hash((HashMap) this.getFormHM().get("kqItem_hash"));

		this.setBatchlist((ArrayList) this.getFormHM().get("batchlist"));
		this.setSettype((String) this.getFormHM().get("settype"));
		this.setSelectys((String) this.getFormHM().get("selectys"));

		this.setUplevel((String) this.getFormHM().get("uplevel"));
		this.setMsg((String) this.getFormHM().get("msg"));
		this.getFormHM().remove("msg");
		this.setFlag((String) this.getFormHM().get("flag"));
		this.setLockedNum((String) this.getFormHM().get("lockedNum"));
		this.setCheckflag((String) this.getFormHM().get("checkflag"));
		this.setItemid((String) this.getFormHM().get("itemid"));
		this.setItemlist((ArrayList) this.getFormHM().get("itemlist"));
		this.setSortitem((String) this.getFormHM().get("sortitem"));
		this.setSalaryid((String) this.getFormHM().get("salaryid"));

		this.setKqitemlist((ArrayList) this.getFormHM().get("kqitemlist"));
		this.setShowlist((ArrayList) this.getFormHM().get("showlist"));
		this.setListid((ArrayList) this.getFormHM().get("listid"));
		this.setCollectMap((HashMap) this.getFormHM().get("collectMap"));
		this.setSelect_type((String) this.getFormHM().get("select_type"));
		this.setKqstatuslist((ArrayList) this.getFormHM().get("kqstatuslist"));
		this.setStatus_flag((String) this.getFormHM().get("status_flag"));
		this.setHaveApprove((String)this.getFormHM().get("haveApprove"));
		this.setModuleFlag((String)this.getFormHM().get("moduleFlag"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("selectedlist",
				(ArrayList) this.getRecordListForm().getSelectedList());
		// this.getFormHM().put("opinlist",(ArrayList)this.getSingListForm().getSelectedList());
		this.getFormHM().put("userbase", userbase);
		this.getFormHM().put("setname", setname);
		this.getFormHM().put("registerdate", registerdate);
		this.getFormHM().put("code", code);
		this.getFormHM().put("kind", kind);
		this.getFormHM().put("forms", forms);
		this.getFormHM().put("datelist", datelist);
		this.getFormHM().put("showalldate", showalldate);
		this.getFormHM().put("changestatus", changestatus);
		this.getFormHM().put("start_date", start_date);
		this.getFormHM().put("end_date", end_date);
		this.getFormHM().put("kq_dbase_list", kq_dbase_list);
		this.getFormHM().put("kq_duration", kq_duration);
		this.getFormHM().put("count_type", this.getCount_type());
		this.getFormHM().put("count_duration", this.getCount_duration());
		this.getFormHM().put("count_start", this.getCount_start());
		this.getFormHM().put("count_end", this.getCount_end());
		if (this.getPagination() != null)
			this.getFormHM().put("selectedinfolist",
					(ArrayList) this.getPagination().getSelectedList());
		if (this.getPagination() != null)
			this.getFormHM().put("overrulelist",
					(ArrayList) this.getPagination().getSelectedList());
		if (this.getPagination() != null)
			this.getFormHM().put("opinlist",
					this.getSingListForm().getAllList());
		this.getFormHM().put("destfld", this.getDestfld());
		this.getFormHM().put("temp_table", this.getTemp_table());
		this.getFormHM().put("bytesid", this.getBytesid());
		if (this.getPagination() != null)
			this.getFormHM().put("list", this.getPagination().getAllList());
		this.getFormHM().put("setlist", this.getSetlist());
		this.getFormHM().put("showtype", this.getShowtype());
		this.getFormHM().put("error_stuts", this.getError_stuts());
		this.getFormHM().put("error_flag", this.getError_flag());
		this.getFormHM().put("error_return", this.getError_return());
		this.getFormHM().put("relatTableid", this.getRelatTableid());
		this.getFormHM().put("re_flag", this.getRe_flag());
		this.getFormHM().put("overrule", this.getOverrule());
		this.getFormHM().put("pick_type", pick_type);
		this.getFormHM().put("pigeonhole_flag", this.getPigeonhole_flag());
		this.getFormHM().put("creat_type", this.getCreat_type());
		this.getFormHM().put("creat_pick", this.getCreat_pick());
		this.getFormHM().put("creat_state", this.getCreat_state());
		this.getFormHM().put("relatTableid", this.getRelatTableid());
		this.getFormHM().put("condition", this.getCondition());
		this.getFormHM().put("returnUrl", this.getReturnURL());
		this.getFormHM().put("sp_flag", this.sp_flag);
		this.getFormHM().put("select_name", this.getSelect_name());
		this.getFormHM().put("select_flag", this.getSelect_flag());
		this.getFormHM().put("select_pre", this.getSelect_pre());
		this.getFormHM().put("sp_result", this.getSp_result());
		this.getFormHM().put("overrulelist", this.getOverrulelist());
		this.getFormHM().put("selectResult", this.getSelectResult());
		this.getFormHM().put("right_fields", this.getRight_fields());
		this.getFormHM().put("expression", this.getExpression());
		this.getFormHM().put("like", this.getLike());
		this.getFormHM().put("selectsturs", this.getSelectsturs());
		this.getFormHM().put("batchlist", this.getBatchlist());
		this.getFormHM().put("settype", this.getSettype());
		this.getFormHM().put("selectys", this.getSelectys());
		this.getFormHM().put("file", this.getFile());
		this.getFormHM().put("sortitem", this.getSortitem());

		this.getFormHM().put("kqitem", this.getKqitem());
		this.getFormHM().put("select_type", this.select_type);

		this.getFormHM().put("showlist", this.getShowlist());
		this.getFormHM().put("listid", this.getListid());
		this.getFormHM().put("kqstatus", this.getKqstatus());
		this.getFormHM().put("kqstatuslist", this.getKqstatuslist());
		this.getFormHM().put("status_flag", this.getStatus_flag());
		this.getFormHM().put("haveApprove", this.getHaveApprove());
		this.getFormHM().put("moduleFlag", this.getModuleFlag());
	}

	/*
	 * public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	 * {
	 * 
	 * 
	 * }
	 */
	public String getSetname() {
		return setname;
	}

	public void setSetname(String setname) {
		this.setname = setname;
	}

	public String getDbcond() {
		return dbcond;
	}

	public void setDbcond(String dbcond) {
		this.dbcond = dbcond;
	}

	public ArrayList getDblist() {
		return dblist;
	}

	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}

	public ArrayList getDatelist() {
		return datelist;
	}

	public void setDatelist(ArrayList datelist) {
		this.datelist = datelist;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public String getStrwhere() {
		return strwhere;
	}

	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public ArrayList getFielditemlist() {
		return fielditemlist;
	}

	public void setFielditemlist(ArrayList fielditemlist) {
		this.fielditemlist = fielditemlist;
	}

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	public ArrayList getForms() {
		return forms;
	}

	public void setForms(ArrayList forms) {
		this.forms = forms;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getCollectdate() {
		return collectdate;
	}

	public void setCollectdate(String collectdate) {
		this.collectdate = collectdate;
	}

	public String getA0100() {
		return A0100;
	}

	public void setA0100(String a0100) {
		A0100 = a0100;
	}

	public String getDbase() {
		return dbase;
	}

	public void setDbase(String dbase) {
		this.dbase = dbase;
	}

	public String getValidate() {
		return validate;
	}

	public void setValidate(String validate) {
		this.validate = validate;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getChangestatus() {
		return changestatus;
	}

	public void setChangestatus(String changestatus) {
		this.changestatus = changestatus;
	}

	public PaginationForm getRecordListForm() {
		return recordListForm;
	}

	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}

	public String getRest_state() {
		return rest_state;
	}

	public void setRest_state(String rest_state) {
		this.rest_state = rest_state;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getKq_duration() {
		return kq_duration;
	}

	public void setKq_duration(String kq_duration) {
		this.kq_duration = kq_duration;
	}

	public String getCreat_duration() {
		return creat_duration;
	}

	public void setCreat_duration(String creat_duration) {
		this.creat_duration = creat_duration;
	}

	public ArrayList getKq_dbase_list() {
		return kq_dbase_list;
	}

	public void setKq_dbase_list(ArrayList kq_dbase_list) {
		this.kq_dbase_list = kq_dbase_list;
	}

	public RecordVo getOne_vo() {
		return one_vo;
	}

	public void setOne_vo(RecordVo one_vo) {
		this.one_vo = one_vo;
	}

	public String getWorkcalendar() {
		return workcalendar;
	}

	public void setWorkcalendar(String workcalendar) {
		this.workcalendar = workcalendar;
	}

	public ArrayList getCourselist() {
		return courselist;
	}

	public void setCourselist(ArrayList courselist) {
		this.courselist = courselist;
	}

	public String getCount_duration() {
		return count_duration;
	}

	public void setCount_duration(String count_duration) {
		this.count_duration = count_duration;
	}

	public String getCount_end() {
		return count_end;
	}

	public void setCount_end(String count_end) {
		this.count_end = count_end;
	}

	public String getCount_start() {
		return count_start;
	}

	public void setCount_start(String count_start) {
		this.count_start = count_start;
	}

	public String getCount_type() {
		return count_type;
	}

	public void setCount_type(String count_type) {
		this.count_type = count_type;
	}

	public ArrayList getVo_list() {
		return vo_list;
	}

	public void setVo_list(ArrayList vo_list) {
		this.vo_list = vo_list;
	}

	public PaginationForm getSingListForm() {
		return singListForm;
	}

	public void setSingListForm(PaginationForm singListForm) {
		this.singListForm = singListForm;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getS_columns() {
		return s_columns;
	}

	public void setS_columns(String s_columns) {
		this.s_columns = s_columns;
	}

	public String getS_strsql() {
		return s_strsql;
	}

	public void setS_strsql(String s_strsql) {
		this.s_strsql = s_strsql;
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if ("/kq/register/search_registerdata".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
		}
		if ("/kq/register/daily_registerdata".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
		}
		if ("/kq/register/daily_registerdata".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
			this.setSp_result("");
			this.getFormHM().put("sp_result", "");
			// if(this.getSelect_flag()!=null&&this.getSelect_flag().equals("0"))
			// {
			// this.setSelect_name("");
			// this.getFormHM().put("select_name","");
			// }
		}
		if ("/kq/register/daily_registerdata_hour".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
		}
		if ("/kq/register/search_registerdata".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
		}
		if ("/kq/register/daily_register".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
			this.getFormHM().clear();
			this.setSelectResult("");
			this.getFormHM().put("selectResult", "");
			this.setSelect_flag("");
			this.getFormHM().put("select_flag", "");
			this.setSp_flag("");
			this.getFormHM().put("sp_flag", "");
			this.getFormHM().put("showtype", "");
			this.setShowtype("");
		}
		if ("/kq/register/search_register".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
			this.setSp_result("");
			this.getFormHM().put("sp_result", "");
			this.getFormHM().clear();
		}
		if ("/kq/register/select_collect".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
			// this.getFormHM().clear();
			// this.setSelect_name("");
			// this.getFormHM().put("select_name","");
			// this.setSelect_flag("");
			// this.getFormHM().put("select_flag","");
		}
		if ("/kq/register/select_collectdata".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
			// this.getFormHM().clear();
		}
		if ("/kq/register/select_collectdata".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null) {
			//if (this.getPagination() != null)
			//	this.getPagination().firstPage();
			// this.getFormHM().clear();
		}
		if ("/kq/register/select_collectdata".equals(arg0.getPath())
				&& arg1.getParameter("b_approve") != null) {
			this.getFormHM().put("re_url",
					"/kq/register/select_collectdata.do?b_query=link");
			this.getFormHM().put("re_target", "mil_body");
		}
		if ("/kq/register/daily_registerdata".equals(arg0.getPath())
				&& arg1.getParameter("b_pickup") != null) {
			this.getFormHM().put("re_url",
					"/kq/register/daily_registerdata.do?b_query=link");
			this.getFormHM().put("re_target", "mil_body");
		}
		if ("/kq/register/daily_registerdata".equals(arg0.getPath())
				&& arg1.getParameter("b_collect") != null) {
			this
					.getFormHM()
					.put(
							"re_url",
							"/kq/register/select_collect.do?b_search=link&action=select_collectdata.do&target=mil_body");
			this.getFormHM().put("re_target", "il_body");
		}
		if ("/kq/register/select_collectdata".equals(arg0.getPath())
				&& arg1.getParameter("b_audit") != null) {
			this.getFormHM().put("re_url",
					"/kq/register/select_collectdata.do?b_search=link");
			this.getFormHM().put("re_target", "mil_body");
		}
		if ("/kq/register/select_collectdata".equals(arg0.getPath())
				&& arg1.getParameter("b_approve") != null) {
			this.getFormHM().put("re_url",
					"/kq/register/select_collectdata.do?b_search=link");
			this.getFormHM().put("re_target", "mil_body");
		}
		if ("/kq/register/select_collectdata".equals(arg0.getPath())
				&& arg1.getParameter("b_refer") != null) {
			this.getFormHM().put("re_url",
					"/kq/register/select_collectdata.do?b_search=link");
			this.getFormHM().put("re_target", "mil_body");
		}
		if ("/kq/register/select_collectdata".equals(arg0.getPath())
				&& arg1.getParameter("b_overrule") != null) {
			this.getFormHM().put("re_url",
					"/kq/register/select_collectdata.do?b_search=link");
			this.getFormHM().put("re_target", "mil_body");
		}
		if ("/kq/register/daily_registerdata".equals(arg0.getPath())
				&& arg1.getParameter("b_rule") != null) {
			this.getFormHM().put("re_url",
					"/kq/register/daily_registerdata.do?b_search=link");
			this.getFormHM().put("re_target", "mil_body");
		}
		if ("/kq/register/select/selectresult".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?

		}
		if ("/kq/register/select/selectfiled".equals(arg0.getPath())
				&& arg1.getParameter("b_init") != null) {
			this.setSelectsturs("0");
			this.getFormHM().put("selectsturs", new Integer(0));
			;
		}
		if ("/kq/register/select_collectdata".equals(arg0.getPath())
				&& (arg1.getParameter("br_appopin") != null)) {
			this.setOverrule("");
			this.getFormHM().put("overrule", "");
		}
		if ("/kq/register/sing_oper/sing_operation".equals(arg0.getPath())
				&& (arg1.getParameter("b_operation") != null)) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
		}
		if ("/kq/register/pigeonhole".equals(arg0.getPath()) && arg1.getParameter("b_pige") != null) {
			arg1.setAttribute("targetWindow", "1");
		}

		return super.validate(arg0, arg1);
	}

	public String getPigeonhole_flag() {
		return pigeonhole_flag;
	}

	public void setPigeonhole_flag(String pigeonhole_flag) {
		this.pigeonhole_flag = pigeonhole_flag;
	}

	public String getBytesid() {
		return bytesid;
	}

	public void setBytesid(String bytesid) {
		this.bytesid = bytesid;
	}

	public String getDestfld() {
		return destfld;
	}

	public void setDestfld(String destfld) {
		this.destfld = destfld;
	}

	public String getInfor_Flag() {
		return infor_Flag;
	}

	public void setInfor_Flag(String infor_Flag) {
		this.infor_Flag = infor_Flag;
	}

	public String getSetlist() {
		return setlist;
	}

	public void setSetlist(String setlist) {
		this.setlist = setlist;
	}

	public String getTemp_table() {
		return temp_table;
	}

	public void setTemp_table(String temp_table) {
		this.temp_table = temp_table;
	}

	public String getPo_column() {
		return po_column;
	}

	public void setPo_column(String po_column) {
		this.po_column = po_column;
	}

	public String getPo_sqlstr() {
		return po_sqlstr;
	}

	public void setPo_sqlstr(String po_sqlstr) {
		this.po_sqlstr = po_sqlstr;
	}

	public String getPo_wherestr() {
		return po_wherestr;
	}

	public void setPo_wherestr(String po_wherestr) {
		this.po_wherestr = po_wherestr;
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		if (this.getPagination() != null) {
			this.getPagination().unSelectedAll();
		}
		super.reset(arg0, arg1);
	}

	public String getShowtype() {
		return showtype;
	}

	public void setShowtype(String showtype) {
		this.showtype = showtype;
	}

	public ArrayList getShowtypelist() {
		return showtypelist;
	}

	public void setShowtypelist(ArrayList showtypelist) {
		this.showtypelist = showtypelist;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getReturnURL() {
		return returnURL;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

	public String getError_flag() {
		return error_flag;
	}

	public void setError_flag(String error_flag) {
		if (error_flag == null || error_flag.length() <= 0)
			error_flag = "0";
		this.error_flag = error_flag;
	}

	public String getError_message() {
		return error_message;
	}

	public void setError_message(String error_message) {
		this.error_message = error_message;
	}

	public String getError_return() {
		return error_return;
	}

	public void setError_return(String error_return) {
		this.error_return = error_return;
	}

	public String getError_stuts() {
		return error_stuts;
	}

	public void setError_stuts(String error_stuts) {
		this.error_stuts = error_stuts;
	}

	public String getRelatTableid() {
		return relatTableid;
	}

	public void setRelatTableid(String relatTableid) {
		this.relatTableid = relatTableid;
	}

	public String getRe_flag() {
		return re_flag;
	}

	public void setRe_flag(String re_flag) {
		this.re_flag = re_flag;
	}

	public String getOverrule() {
		return overrule;
	}

	public void setOverrule(String overrule) {
		this.overrule = overrule;
	}

	public String getPick_type() {
		return pick_type;
	}

	public void setPick_type(String pick_type) {
		this.pick_type = pick_type;
	}

	public String getState_message() {
		return state_message;
	}

	public void setState_message(String state_message) {
		this.state_message = state_message;
	}

	public String getCreat_pick() {
		return creat_pick;
	}

	public void setCreat_pick(String creat_pick) {
		this.creat_pick = creat_pick;
	}

	public String getCreat_type() {
		return creat_type;
	}

	public void setCreat_type(String creat_type) {
		this.creat_type = creat_type;
	}

	public String getOverrule_status() {
		return overrule_status;
	}

	public void setOverrule_status(String overrule_status) {
		this.overrule_status = overrule_status;
	}

	public String getSp_flag() {
		return sp_flag;
	}

	public void setSp_flag(String sp_flag) {
		this.sp_flag = sp_flag;
	}

	public ArrayList getSplist() {
		return splist;
	}

	public void setSplist(ArrayList splist) {
		this.splist = splist;
	}

	public ArrayList getKq_list() {
		return kq_list;
	}

	public void setKq_list(ArrayList kq_list) {
		this.kq_list = kq_list;
	}

	public String getSelect_name() {
		return select_name;
	}

	public void setSelect_name(String select_name) {
		this.select_name = select_name;
	}

	public String getSelect_flag() {
		return select_flag;
	}

	public void setSelect_flag(String select_flag) {
		this.select_flag = select_flag;
	}

	public String getSelect_pre() {
		return select_pre;
	}

	public void setSelect_pre(String select_pre) {
		this.select_pre = select_pre;
	}

	public String getSp_result() {
		return sp_result;
	}

	public void setSp_result(String sp_result) {
		this.sp_result = sp_result;
	}

	public String getRe_url() {
		return re_url;
	}

	public void setRe_url(String re_url) {
		this.re_url = re_url;
	}

	public String getRe_target() {
		return re_target;
	}

	public void setRe_target(String re_target) {
		this.re_target = re_target;
	}

	public String getCreat_state() {
		return creat_state;
	}

	public void setCreat_state(String creat_state) {
		this.creat_state = creat_state;
	}

	public String getUp_dailyregister() {
		return up_dailyregister;
	}

	public void setUp_dailyregister(String up_dailyregister) {
		this.up_dailyregister = up_dailyregister;
	}

	public ArrayList getOverrulelist() {
		return overrulelist;
	}

	public void setOverrulelist(ArrayList overrulelist) {
		this.overrulelist = overrulelist;
	}

	public String getSelectResult() {
		return selectResult;
	}

	public void setSelectResult(String selectResult) {
		this.selectResult = selectResult;
	}

	public String[] getLeft_fields() {
		return left_fields;
	}

	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getSelectsturs() {
		return selectsturs;
	}

	public void setSelectsturs(String selectsturs) {
		this.selectsturs = selectsturs;
	}

	public HashMap getKqItem_hash() {
		return kqItem_hash;
	}

	public void setKqItem_hash(HashMap kqItem_hash) {
		this.kqItem_hash = kqItem_hash;
	}

	public ArrayList getBatchlist() {
		return batchlist;
	}

	public void setBatchlist(ArrayList batchlist) {
		this.batchlist = batchlist;
	}

	public String getSettype() {
		return settype;
	}

	public void setSettype(String settype) {
		this.settype = settype;
	}

	public String getSelectys() {
		return selectys;
	}

	public void setSelectys(String selectys) {
		this.selectys = selectys;
	}

	public String getUplevel() {
		return uplevel;
	}

	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

	public String getCheckflag() {
		return checkflag;
	}

	public void setCheckflag(String checkflag) {
		this.checkflag = checkflag;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public String getSelect_type() {
		return select_type;
	}

	public void setSelect_type(String select_type) {
		this.select_type = select_type;
	}

	public ArrayList getShowlist() {
		return showlist;
	}

	public void setShowlist(ArrayList showlist) {
		this.showlist = showlist;
	}

	public ArrayList getListid() {
		return listid;
	}

	public void setListid(ArrayList listid) {
		this.listid = listid;
	}

}
