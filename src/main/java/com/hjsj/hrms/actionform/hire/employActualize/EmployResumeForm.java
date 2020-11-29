package com.hjsj.hrms.actionform.hire.employActualize;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class EmployResumeForm extends FrameForm {
	private String employType = "0"; // 0：业务平台 1：自助平台
	private ArrayList resumeStateList = new ArrayList(); // 简历状态列表
	private String resumeState = ""; // 简历状态
	private String z0301 = "-1";
	private String zp_pos_id = "-1";
	private String personType = "0"; // 0:应聘库 1：人才库 4:我的收藏夹

	private String colums = "";

	private ArrayList orderItemList = new ArrayList();
	private ArrayList orderDescList = new ArrayList();
	private String order_item = ""; // 排序字段
	private String order_desc = "desc"; // asc:升序 desc:降序
	private String str_sql = "";
	private String str_whl = "";
	private String order_str = "";
	private String resumeCount = "0"; // 简历数量
	private String isShowCondition = "none"; // 不显示查询条件 block：显示
	private String fielditem1 = "";
	private String fielditem2 = "";
	private String fielditem3 = "";
	private String fielditem4 = "";
	private String fielditem5 = "";
	private ArrayList resumeFieldList = new ArrayList();
	private ArrayList resumeFieldsList = new ArrayList();
	private HashMap resumeFieldMap = new HashMap();
	private String resume_state_item = "";
	private String dbname = "";
	private ArrayList posConditionList = new ArrayList();
	private PaginationForm cardlistform = new PaginationForm();

	private String sql_view = "";
	private String validAll="true";
	private String max_count="3";
	/** 
     * @return validAll 
     */
    public String getValidAll() {
        return validAll;
    }

    /** 
     * @param validAll 要设置的 validAll 
     */
    public void setValidAll(String validAll) {
        this.validAll = validAll;
    }

    private String name;

	private String vague = ""; // 是否按模糊查询
	private String upValue = ""; // 子集代码型 以上

	private ArrayList posList = new ArrayList(); // 推荐职位列表
	private String previewTabid;

	private ArrayList hireObjectList = new ArrayList(); // 招聘对象列表
	private String hireObjectId = "-1"; // 所有对象
	private String isShowCompare = "0"; // 是否显示简历同步按钮
	private String isSelectedAll = "0"; // 是否全选
	/*
	 * this.getFormHM().put("templateid", templateid);
	 * this.getFormHM().put("complexTemplateList", complexTemplateList);
	 */
	private String templateid = "";
	private ArrayList complexTemplateList = new ArrayList();
	private String score;
	private ArrayList columnList = new ArrayList();
	private PaginationForm dataListForm = new PaginationForm();
	private String schoolPosition;
	private String conditionSQL;
	private String queryType;// 在已选状态中，是查看全部已选，还是只查看自己的已选，=0查看自己(默认)，=1查看全部（权限）
	private ArrayList encryption_list = new ArrayList(); // 加密对象列表
	private String encryption_sql = "";

	private String value = "";
	private String viewvalue = "";
	private String isCode = "";
	private String posID = ""; // 应聘岗位id
	private ArrayList posIDList = new ArrayList();
	private String professional = ""; // 应聘专业
	private ArrayList pflist = new ArrayList();
	private String isShow = ""; // 1显示招聘岗位，2显示专业
	private FormFile file; // 上传附件
	private String applyStartDate = ""; // 查询应聘起始时间
	private String applyEndDate = ""; // 查询应聘结束时间
	private String from_flag = "";//代码对应返回的页面

	// xmsh
	private PaginationForm codeitemlistform = new PaginationForm(); // 代码项对应分页标签
	/** 当前页 */
	private int current = 1;

	private String itemID = ""; // 标识指标
	private String secitemID = ""; // 次关键指标
	private ArrayList itemIDList = new ArrayList();
	private String mode = ""; // 更新方式
	private ArrayList modeList = new ArrayList();
	private ArrayList flist = new ArrayList(); // 人员库指标集
	private ArrayList ilist = new ArrayList(); // 人员库指标
	private ArrayList clist = new ArrayList(); // 人员库代码项
	private String fieldSet = null;
	private ArrayList xitemIDList = new ArrayList(); // 指标对应人员库指标
	private String resumeset = null;
	private String resumeID = null; // 代码对应下拉列表值
	private String valid = null;
	private FormFile zipFile; // 简历zip包
	private ArrayList codelist;
	private ArrayList itemlist;

	private String commonvalue = "";
	private String log = "";
	/**这些字段为应聘简历中导入功能加的 begin**/
	private ArrayList inforNotExistList=new ArrayList();//在接收库中不存在的字段
    private ArrayList inforNotFormatList=new ArrayList();//与接收库字段类型不一致的字段
	private ArrayList itemNotInList=new ArrayList();//存放的是不一致和不存在字段的itemid
	private String importZipData="1";//导入数据是否成功 1：成功 0：不成功
	private String zipRecordcout="0";//导入总共多少条数据
	private String importcount="0";//导入成功了多少条数据
	private String zipdataFilepath="";
    private ArrayList setNotInList=new ArrayList();//在接收库中不存在的指标集
    private ArrayList setNotExistList=new ArrayList();//在前台提示不存在的指标集信息
    /**这些字段为应聘简历中导入功能加的 end**/
	@Override
    public void outPutFormHM() {
	    this.setSetNotInList((ArrayList) this.getFormHM().get("setNotInList"));
	    this.setSetNotExistList((ArrayList) this.getFormHM().get("setNotExistList"));
	    this.setZipdataFilepath((String) this.getFormHM().get("zipdataFilepath"));
	    this.setZipRecordcout((String) this.getFormHM().get("zipRecordcout"));
	    this.setImportcount((String) this.getFormHM().get("importcount"));
	    this.setImportZipData((String) this.getFormHM().get("importZipData"));
	    this.setInforNotExistList((ArrayList) this.getFormHM().get("inforNotExistList"));
	    this.setInforNotFormatList((ArrayList) this.getFormHM().get("inforNotFormatList"));
	    this.setItemNotInList((ArrayList) this.getFormHM().get("itemNotInList"));
		this.setQueryType((String) this.getFormHM().get("queryType"));
		this.setConditionSQL((String) this.getFormHM().get("conditionSQL"));
		this.setName((String) this.getFormHM().get("name"));
		this.setSchoolPosition((String) this.getFormHM().get("schoolPosition"));
		this.setReturnflag((String) this.getFormHM().get("returnflag"));
		this.getDataListForm().setList(
				(ArrayList) this.getFormHM().get("dataList"));
		this.setScore((String) this.getFormHM().get("score"));
		this.setColumnList((ArrayList) this.getFormHM().get("columnList"));
		this.setPagerows(Integer.parseInt(((String) this.getFormHM().get(
				"pagerows"))));
		this.setComplexTemplateList((ArrayList) this.getFormHM().get(
				"complexTemplateList"));
		this.setResumeFieldList((ArrayList) this.getFormHM().get(
				"resumeFieldList"));
		this.getCardlistform().setList(
				(ArrayList) this.getFormHM().get("resumeFieldsList"));
		this.setTemplateid((String) this.getFormHM().get("templateid"));
		this.setIsSelectedAll((String) this.getFormHM().get("isSelectedAll"));
		this.setHireObjectList((ArrayList) this.getFormHM().get(
				"hireObjectList"));
		this.setHireObjectId((String) this.getFormHM().get("hireObjectId"));
		if ("03".equals(this.getHireObjectId())) // 如果为内部招聘对象
			this.setIsShowCompare("1");
		else
			this.setIsShowCompare("0");

		this.setEmployType((String) this.getFormHM().get("employType"));
		this.setPosList((ArrayList) this.getFormHM().get("posList"));

		this.setUpValue((String) this.getFormHM().get("upValue"));
		this.setSql_view((String) this.getFormHM().get("sql_view"));
		this.setZp_pos_id((String) this.getFormHM().get("zp_pos_id"));

		this.setDbname((String) this.getFormHM().get("dbname"));
		this.setPersonType((String) this.getFormHM().get("personType"));
		this.setIsShowCondition((String) this.getFormHM()
				.get("isShowCondition"));
		this.setResumeCount((String) this.getFormHM().get("resumeCount"));
		this.setColums((String) this.getFormHM().get("colums"));

		this.setStr_sql((String) this.getFormHM().get("str_sql"));
		this.setStr_whl((String) this.getFormHM().get("str_whl"));
		this.setOrder_str((String) this.getFormHM().get("order_str"));
		this.setZ0301((String) this.getFormHM().get("z0301"));

		this.setResumeState((String) this.getFormHM().get("resumeState"));
		this.setResumeStateList((ArrayList) this.getFormHM().get(
				"resumeStateList"));
		this
				.setOrderItemList((ArrayList) this.getFormHM().get(
						"orderItemList"));
		this
				.setOrderDescList((ArrayList) this.getFormHM().get(
						"orderDescList"));
		this.setOrder_desc((String) this.getFormHM().get("order_desc"));
		this.setOrder_item((String) this.getFormHM().get("order_item"));

		this.setFielditem1((String) this.getFormHM().get("fielditem1"));
		this.setFielditem2((String) this.getFormHM().get("fielditem2"));
		this.setFielditem3((String) this.getFormHM().get("fielditem3"));
		this.setFielditem4((String) this.getFormHM().get("fielditem4"));
		this.setFielditem5((String) this.getFormHM().get("fielditem5"));
		this.setResumeFieldList((ArrayList) this.getFormHM().get(
				"resumeFieldList"));
		this
				.setResumeFieldMap((HashMap) this.getFormHM().get(
						"resumeFieldMap"));
		this.setResume_state_item((String) this.getFormHM().get(
				"resume_state_item"));
		this.setPreviewTabid((String) this.getFormHM().get("previewTabid"));
		this.setPosConditionList((ArrayList) this.getFormHM().get(
				"posConditionList"));
		this.setEncryption_list((ArrayList) this.getFormHM().get(
				"encryption_list"));
		this.setEncryption_sql((String) this.getFormHM().get("encryption_sql"));

		this.setIsShow((String) this.getFormHM().get("isShow"));
		this.setPosID((String) this.getFormHM().get("posID"));
		this.setPosIDList((ArrayList) this.getFormHM().get("posIDList"));
		this.setValue((String) this.getFormHM().get("value"));
		this.setViewvalue((String) this.getFormHM().get("viewvalue"));
		this.setIsCode((String) this.getFormHM().get("isCode"));
		this.setProfessional((String) this.getFormHM().get("professional"));
		this.setPflist((ArrayList) this.getFormHM().get("pflist"));
		this.setApplyStartDate((String) this.getFormHM().get("applyStartDate"));
		this.setApplyEndDate((String) this.getFormHM().get("applyEndDate"));

		// xmsh
		this.getCodeitemlistform().setList(
				(ArrayList) this.getFormHM().get("resumeList"));
		this.getCodeitemlistform().getPagination().gotoPage(current);
		this.setResumeset((String) this.getFormHM().get("resumeset"));
		this.setItemID((String) this.getFormHM().get("itemID"));
		this.setSecitemID((String) this.getFormHM().get("secitemID"));
		this.setItemIDList((ArrayList) this.getFormHM().get("itemIDList"));
		this.setMode((String) this.getFormHM().get("mode"));
		this.setModeList((ArrayList) this.getFormHM().get("modelist"));
		this.setFlist((ArrayList) this.getFormHM().get("flist"));
		this.setIlist((ArrayList) this.getFormHM().get("ilist"));
		this.setClist((ArrayList) this.getFormHM().get("clist"));
		this.setFieldSet((String) this.getFormHM().get("fieldSet"));
		this.setXitemIDList((ArrayList) this.getFormHM().get("xitemIDList"));
		this.setResumeID((String) this.getFormHM().get("resumeID"));
		this.setValid((String) this.getFormHM().get("valid"));
		this.setCommonvalue((String) this.getFormHM().get("commonvalue"));
		this.setLog((String) this.getFormHM().get("Log"));
		this.setCodelist((ArrayList) this.getFormHM().get("codelist"));
		this.setItemlist((ArrayList) this.getFormHM().get("itemlist"));
		this.setFrom_flag((String) this.getFormHM().get("from_flag"));
		this.setValidAll((String) this.getFormHM().get("validAll"));
		this.setFieldSet((String) this.getFormHM().get("fieldset"));
		this.setMax_count((String) this.getFormHM().get("max_count"));
	}

	@Override
    public void inPutTransHM() {
	    this.getFormHM().put("setNotInList", this.getSetNotInList());
	    this.getFormHM().put("zipdataFilepath", this.getZipdataFilepath());
	    this.getFormHM().put("inforNotExistList", this.getInforNotExistList());
	    this.getFormHM().put("inforNotFormatList",this.getInforNotFormatList());
	    this.getFormHM().put("itemNotInList",this.getItemNotInList());
		this.getFormHM().put("queryType", this.getQueryType());
		this.getFormHM().put("conditionSQL", this.getConditionSQL());
		this.getFormHM().put("schoolPosition", this.getSchoolPosition());
		this.getFormHM().put("returnflag", this.getReturnflag());
		this.getFormHM().put("selectedList",
				this.getDataListForm().getSelectedList());
		this.getFormHM().put("score", this.getScore());
		this.getFormHM().put("columnList", this.getColumnList());
		this.getFormHM().put("pagerows",
				this.getPagerows() == 0 ? "20" : (this.getPagerows() + ""));
		this.getFormHM().put("complexTemplateList",
				this.getComplexTemplateList());
		this.getFormHM().put("templateid", this.getTemplateid());
		if (this.getPagination() != null)
			this.getFormHM().put("selectedList",
					(ArrayList) this.getPagination().getSelectedList());
		this.getFormHM().put("isSelectedAll", this.getIsSelectedAll());
		this.getFormHM().put("hireObjectId", this.getHireObjectId());
		this.getFormHM().put("isShowCondition", this.getIsShowCondition());
		this.getFormHM().put("order_item", this.getOrder_item());
		this.getFormHM().put("order_desc", this.getOrder_desc());
		this.getFormHM().put("resumeState", this.getResumeState());
		this.getFormHM().put("fielditem1", this.getFielditem1());
		this.getFormHM().put("fielditem2", this.getFielditem2());
		this.getFormHM().put("fielditem3", this.getFielditem3());
		this.getFormHM().put("fielditem4", this.getFielditem4());
		this.getFormHM().put("fielditem5", this.getFielditem5());
		this.getFormHM().put("resumeFieldList", this.getResumeFieldList());
		this.getFormHM().put("resumeFieldMap", this.getResumeFieldMap());
		this.getFormHM().put("resumeStateList", this.getResumeStateList());
		this.getFormHM().put("resume_state_item", this.getResume_state_item());
		this.getFormHM().put("posConditionList", this.getPosConditionList());

		this.getFormHM().put("upValue", this.getUpValue());
		this.getFormHM().put("vague", this.getVague());
		this.getFormHM().put("selectcardlist",
				this.getCardlistform().getSelectedList());
		this.getFormHM().put("encryption_list", this.getEncryption_list());
		this.getFormHM().put("encryption_sql", this.getEncryption_sql());

		this.getFormHM().put("value", this.getValue());
		this.getFormHM().put("viewvalue", this.getViewvalue());
		this.getFormHM().put("isCode", this.getIsCode());
		this.getFormHM().put("posID", this.getPosID());
		this.getFormHM().put("posIDList", this.getPosIDList());
		this.getFormHM().put("professional", this.getProfessional());
		this.getFormHM().put("pflist", this.getPflist());
		this.getFormHM().put("file", this.getFile());
		this.getFormHM().put("applyStartDate", this.getApplyStartDate());
		this.getFormHM().put("applyEndDate", this.getApplyEndDate());

		// xmsh
		this.getFormHM().put("codelist", this.getCodelist());
		this.getFormHM().put("itemlist", this.getItemlist());

		this.getFormHM().put("itemID", this.getItemID());
		this.getFormHM().put("secitemID", this.getSecitemID());
		this.getFormHM().put("itemIDList", this.getItemIDList());
		this.getFormHM().put("mode", this.getMode());
		this.getFormHM().put("modelist", this.getModeList());
		this.getFormHM().put("flist", this.getFlist());
		this.getFormHM().put("ilist", this.getIlist());
		this.getFormHM().put("clist", this.getClist());
		this.getFormHM().put("fieldSet", this.getFieldSet());
		this.getFormHM().put("xitemIDList", this.getXitemIDList());
		this.getFormHM().put("resumeID", this.getResumeID());
		this.getFormHM().put("valid", this.getValid());
		this.getFormHM().put("zipFile", this.getZipFile());
		this.getFormHM().put("commonvalue", this.getCommonvalue());
		this.getFormHM().put("log", this.getLog());
		this.getFormHM().put("from_flag", this.getFrom_flag());
		this.getFormHM().put("validAll", this.getValidAll());
		this.getFormHM().put("max_count", this.getMax_count());

	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if (arg1.getParameter("operate") != null
				&& "init".equals(arg1.getParameter("operate"))) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();
		}

		if (arg1.getParameter("br_browse") != null
				&& "browse".equals(arg1.getParameter("br_browse"))) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();
		}

		if (arg1.getParameter("resumeState") != null
				&& this.getFormHM().get("resumeState") != null
				&& !arg1.getParameter("resumeState").equals(
						(String) this.getFormHM().get("resumeState"))) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();
		}
		if (arg1.getParameter("select") != null
				&& "1".equals(arg1.getParameter("select"))) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();
		}
		if (arg1.getParameter("b_codeCorrespond") != null
				&& "link".equals(arg1.getParameter("b_codeCorrespond"))) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();
		}
		return super.validate(arg0, arg1);
	}

	public String getFielditem1() {
		return fielditem1;
	}

	public void setFielditem1(String fielditem1) {
		this.fielditem1 = fielditem1;
	}

	public String getFielditem2() {
		return fielditem2;
	}

	public void setFielditem2(String fielditem2) {
		this.fielditem2 = fielditem2;
	}

	public String getFielditem3() {
		return fielditem3;
	}

	public void setFielditem3(String fielditem3) {
		this.fielditem3 = fielditem3;
	}

	public String getFielditem4() {
		return fielditem4;
	}

	public void setFielditem4(String fielditem4) {
		this.fielditem4 = fielditem4;
	}

	public String getFielditem5() {
		return fielditem5;
	}

	public void setFielditem5(String fielditem5) {
		this.fielditem5 = fielditem5;
	}

	public String getOrder_desc() {
		return order_desc;
	}

	public void setOrder_desc(String order_desc) {
		this.order_desc = order_desc;
	}

	public String getOrder_item() {
		return order_item;
	}

	public void setOrder_item(String order_item) {
		this.order_item = order_item;
	}

	public String getOrder_str() {
		return order_str;
	}

	public void setOrder_str(String order_str) {
		this.order_str = order_str;
	}

	public ArrayList getOrderItemList() {
		return orderItemList;
	}

	public void setOrderItemList(ArrayList orderItemList) {
		this.orderItemList = orderItemList;
	}

	public String getResume_state_item() {
		return resume_state_item;
	}

	public void setResume_state_item(String resume_state_item) {
		this.resume_state_item = resume_state_item;
	}

	public ArrayList getResumeFieldList() {
		return resumeFieldList;
	}

	public void setResumeFieldList(ArrayList resumeFieldList) {
		this.resumeFieldList = resumeFieldList;
	}

	public String getResumeState() {
		return resumeState;
	}

	public void setResumeState(String resumeState) {
		this.resumeState = resumeState;
	}

	public String getIsCode() {
		return isCode;
	}

	public void setIsCode(String isCode) {
		this.isCode = isCode;
	}

	public ArrayList getResumeStateList() {
		return resumeStateList;
	}

	public void setResumeStateList(ArrayList resumeStateList) {
		this.resumeStateList = resumeStateList;
	}

	public String getStr_sql() {
		return str_sql;
	}

	public void setStr_sql(String str_sql) {
		this.str_sql = str_sql;
	}

	public String getStr_whl() {
		return str_whl;
	}

	public void setStr_whl(String str_whl) {
		this.str_whl = str_whl;
	}

	public String getZ0301() {
		return z0301;
	}

	public void setZ0301(String z0301) {
		this.z0301 = z0301;
	}

	public HashMap getResumeFieldMap() {
		return resumeFieldMap;
	}

	public void setResumeFieldMap(HashMap resumeFieldMap) {
		this.resumeFieldMap = resumeFieldMap;
	}

	public ArrayList getPosConditionList() {
		return posConditionList;
	}

	public void setPosConditionList(ArrayList posConditionList) {
		this.posConditionList = posConditionList;
	}

	public ArrayList getOrderDescList() {
		return orderDescList;
	}

	public void setOrderDescList(ArrayList orderDescList) {
		this.orderDescList = orderDescList;
	}

	public String getColums() {
		return colums;
	}

	public void setColums(String colums) {
		this.colums = colums;
	}

	public String getResumeCount() {
		return resumeCount;
	}

	public void setResumeCount(String resumeCount) {
		this.resumeCount = resumeCount;
	}

	public String getIsShowCondition() {
		return isShowCondition;
	}

	public void setIsShowCondition(String isShowCondition) {
		this.isShowCondition = isShowCondition;
	}

	public String getPersonType() {
		return personType;
	}

	public void setPersonType(String personType) {
		this.personType = personType;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getSql_view() {
		return sql_view;
	}

	public void setSql_view(String sql_view) {
		this.sql_view = sql_view;
	}

	public String getZp_pos_id() {
		return zp_pos_id;
	}

	public void setZp_pos_id(String zp_pos_id) {
		this.zp_pos_id = zp_pos_id;
	}

	public String getUpValue() {
		return upValue;
	}

	public void setUpValue(String upValue) {
		this.upValue = upValue;
	}

	public String getVague() {
		return vague;
	}

	public void setVague(String vague) {
		this.vague = vague;
	}

	public ArrayList getPosList() {
		return posList;
	}

	public void setPosList(ArrayList posList) {
		this.posList = posList;
	}

	public String getEmployType() {
		return employType;
	}

	public void setEmployType(String employType) {
		this.employType = employType;
	}

	public String getPreviewTabid() {
		return previewTabid;
	}

	public void setPreviewTabid(String previewTabid) {
		this.previewTabid = previewTabid;
	}

	public String getHireObjectId() {
		return hireObjectId;
	}

	public void setHireObjectId(String hireObjectId) {
		this.hireObjectId = hireObjectId;
	}

	public ArrayList getHireObjectList() {
		return hireObjectList;
	}

	public void setHireObjectList(ArrayList hireObjectList) {
		this.hireObjectList = hireObjectList;
	}

	public String getIsShowCompare() {
		return isShowCompare;
	}

	public void setIsShowCompare(String isShowCompare) {
		this.isShowCompare = isShowCompare;
	}

	public String getIsSelectedAll() {
		return isSelectedAll;
	}

	public void setIsSelectedAll(String isSelectedAll) {
		this.isSelectedAll = isSelectedAll;
	}

	public String getTemplateid() {
		return templateid;
	}

	public void setTemplateid(String templateid) {
		this.templateid = templateid;
	}

	public ArrayList getComplexTemplateList() {
		return complexTemplateList;
	}

	public void setComplexTemplateList(ArrayList complexTemplateList) {
		this.complexTemplateList = complexTemplateList;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public ArrayList getColumnList() {
		return columnList;
	}

	public void setColumnList(ArrayList columnList) {
		this.columnList = columnList;
	}

	public PaginationForm getDataListForm() {
		return dataListForm;
	}

	public void setDataListForm(PaginationForm dataListForm) {
		this.dataListForm = dataListForm;
	}

	public String getSchoolPosition() {
		return schoolPosition;
	}

	public void setSchoolPosition(String schoolPosition) {
		this.schoolPosition = schoolPosition;
	}

	public String getConditionSQL() {
		return conditionSQL;
	}

	public void setConditionSQL(String conditionSQL) {
		this.conditionSQL = conditionSQL;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList getResumeFieldsList() {
		return resumeFieldsList;
	}

	public void setResumeFieldsList(ArrayList resumeFieldsList) {
		this.resumeFieldsList = resumeFieldsList;
	}

	public PaginationForm getCardlistform() {
		return cardlistform;
	}

	public void setCardlistform(PaginationForm cardlistform) {
		this.cardlistform = cardlistform;
	}

	public ArrayList getEncryption_list() {
		return encryption_list;
	}

	public void setEncryption_list(ArrayList encryption_list) {
		this.encryption_list = encryption_list;
	}

	public String getEncryption_sql() {
		return encryption_sql;
	}

	public void setEncryption_sql(String encryption_sql) {
		this.encryption_sql = encryption_sql;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getViewvalue() {
		return viewvalue;
	}

	public void setViewvalue(String viewvalue) {
		this.viewvalue = viewvalue;
	}

	public String getPosID() {
		return posID;
	}

	public void setPosID(String posID) {
		this.posID = posID;
	}

	public ArrayList getPosIDList() {
		return posIDList;
	}

	public void setPosIDList(ArrayList posIDList) {
		this.posIDList = posIDList;
	}

	public String getProfessional() {
		return professional;
	}

	public void setProfessional(String professional) {
		this.professional = professional;
	}

	public ArrayList getPflist() {
		return pflist;
	}

	public void setPflist(ArrayList pflist) {
		this.pflist = pflist;
	}

	public String getIsShow() {
		return isShow;
	}

	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getItemID() {
		return itemID;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}

	public String getSecitemID() {
		return secitemID;
	}

	public void setSecitemID(String secitemID) {
		this.secitemID = secitemID;
	}

	public ArrayList getItemIDList() {
		return itemIDList;
	}

	public void setItemIDList(ArrayList itemIDList) {
		this.itemIDList = itemIDList;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public ArrayList getModeList() {
		return modeList;
	}

	public void setModeList(ArrayList modeList) {
		this.modeList = modeList;
	}

	public ArrayList getFlist() {
		return flist;
	}

	public void setFlist(ArrayList flist) {
		this.flist = flist;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public String getFieldSet() {
		return fieldSet;
	}

	public void setFieldSet(String fieldSet) {
		this.fieldSet = fieldSet;
	}

	public ArrayList getXitemIDList() {
		return xitemIDList;
	}

	public void setXitemIDList(ArrayList xitemIDList) {
		this.xitemIDList = xitemIDList;
	}

	public String getResumeset() {
		return resumeset;
	}

	public void setResumeset(String resumeset) {
		this.resumeset = resumeset;
	}

	public PaginationForm getCodeitemlistform() {
		return codeitemlistform;
	}

	public void setCodeitemlistform(PaginationForm codeitemlistform) {
		this.codeitemlistform = codeitemlistform;
	}

	public String getResumeID() {
		return resumeID;
	}

	public void setResumeID(String resumeID) {
		this.resumeID = resumeID;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public FormFile getZipFile() {
		return zipFile;
	}

	public void setZipFile(FormFile zipFile) {
		this.zipFile = zipFile;
	}

	public String getCommonvalue() {
		return commonvalue;
	}

	public void setCommonvalue(String commonvalue) {
		this.commonvalue = commonvalue;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public ArrayList getCodelist() {
		return codelist;
	}

	public void setCodelist(ArrayList codelist) {
		this.codelist = codelist;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public ArrayList getIlist() {
		return ilist;
	}

	public void setIlist(ArrayList ilist) {
		this.ilist = ilist;
	}

	public ArrayList getClist() {
		return clist;
	}

	public void setClist(ArrayList clist) {
		this.clist = clist;
	}

	public String getApplyStartDate() {
		return applyStartDate;
	}

	public void setApplyStartDate(String applyStartDate) {
		this.applyStartDate = applyStartDate;
	}

	public String getApplyEndDate() {
		return applyEndDate;
	}

	public void setApplyEndDate(String applyEndDate) {
		this.applyEndDate = applyEndDate;
	}

	public String getFrom_flag() {
		return from_flag;
	}

	public void setFrom_flag(String from_flag) {
		this.from_flag = from_flag;
	}
	   /** 
     * @return inforNotExistList 
     */
    public ArrayList getInforNotExistList() {
        return inforNotExistList;
    }

    /** 
     * @param inforNotExistList 要设置的 inforNotExistList 
     */
    public void setInforNotExistList(ArrayList inforNotExistList) {
        this.inforNotExistList = inforNotExistList;
    }

    /** 
     * @return inforNotFormatList 
     */
    public ArrayList getInforNotFormatList() {
        return inforNotFormatList;
    }

    /** 
     * @param inforNotFormatList 要设置的 inforNotFormatList 
     */
    public void setInforNotFormatList(ArrayList inforNotFormatList) {
        this.inforNotFormatList = inforNotFormatList;
    }

    /** 
     * @return itemNotInList 
     */
    public ArrayList getItemNotInList() {
        return itemNotInList;
    }

    /** 
     * @param itemNotInList 要设置的 itemNotInList 
     */
    public void setItemNotInList(ArrayList itemNotInList) {
        this.itemNotInList = itemNotInList;
    }
    /** 
     * @return importZipData 
     */
    public String getImportZipData() {
        return importZipData;
    }

    /** 
     * @param importZipData 要设置的 importZipData 
     */
    public void setImportZipData(String importZipData) {
        this.importZipData = importZipData;
    }
    /** 
     * @return zipRecordcout 
     */
    public String getZipRecordcout() {
        return zipRecordcout;
    }

    /** 
     * @param zipRecordcout 要设置的 zipRecordcout 
     */
    public void setZipRecordcout(String zipRecordcout) {
        this.zipRecordcout = zipRecordcout;
    }

    /** 
     * @return importcount 
     */
    public String getImportcount() {
        return importcount;
    }

    /** 
     * @param importcount 要设置的 importcount 
     */
    public void setImportcount(String importcount) {
        this.importcount = importcount;
    }
    /** 
     * @return zipdataFilepath 
     */
    public String getZipdataFilepath() {
        return zipdataFilepath;
    }

    /** 
     * @param zipdataFilepath 要设置的 zipdataFilepath 
     */
    public void setZipdataFilepath(String zipdataFilepath) {
        this.zipdataFilepath = zipdataFilepath;
    }
    /** 
     * @return setNotInList 
     */
    public ArrayList getSetNotInList() {
        return setNotInList;
    }

    /** 
     * @param setNotInList 要设置的 setNotInList 
     */
    public void setSetNotInList(ArrayList setNotInList) {
        this.setNotInList = setNotInList;
    }

    /** 
     * @return setNotExistList 
     */
    public ArrayList getSetNotExistList() {
        return setNotExistList;
    }

    /** 
     * @param setNotExistList 要设置的 setNotExistList 
     */
    public void setSetNotExistList(ArrayList setNotExistList) {
        this.setNotExistList = setNotExistList;
    }

    public String getMax_count() {
        return max_count;
    }

    public void setMax_count(String maxCount) {
        max_count = maxCount;
    }


}
