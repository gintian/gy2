package com.hjsj.hrms.actionform.general.email_template;

import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
/**
 * <p>Title:GzEmailForm.java</p>
 * <p>Description:定义薪资发放的邮件模板的form</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-9-10 9:29:11 am</p>
 * @author LiZhenWei
 * @version 4.0
 */
public class GzEmailForm extends FrameForm{
	
	/**邮件模板编号*/
	private String id;
	/**模板名称*/
	private String name;
	/**模块标志位*/
	private String nmodule;//1:人员管理,2:薪资管理;5:工资审批的邮件模板
	/**信息群类型*/
	private String ninfoclass;//1:人员,2:单位,3:职位
	/**邮件主题内容*/
	private String subject;
	/**主体内容*/
	private String content;
	/**附件*/
	private String attach;
	/**附件列表*/
	private ArrayList attachlist = new ArrayList();
	/**邮件地址指标*/
	private String address;
	/**是否保存过的标志,只有保存过的模板才可以上传附件*/
	private String nflag;//=1保存过的,=2未保存过的
	/**人员子集id*/
	private String fieldsetid;
	/**人员子集列表*/
	private ArrayList fieldsetlist = new ArrayList();
	/**指标id*/
	private String itemid;
	/**指标列表*/
	private ArrayList itemlist = new ArrayList();
	/**代码型指标的值列表*/
	private ArrayList codefieldlist = new ArrayList();
	private String codevalue;
	/**日期格式列表*/
	private ArrayList dateFormatList = new ArrayList();
	/**日期格式*/
	private String dateFormat;
	/**整数位数*/
	private String integerdigit;
	/**小数位数*/
	private String decimalfractiondigit;
	/**公式标题*/
	private String formulatitle;
	/**公式类型*/
	private String formulatype;
	/**公式内容*/
	private String formulacontent;
	/**字符型公式长度*/
	private String formulalength;
	/**邮件模板项目id*/
	private String fieldid;
	/**插入指标的fieldsetid*/
	private String formulafieldsetid;
	/**上传附件*/
	private FormFile file;
	/**模板项目最大id*/
	private String maxid;
	/**上传附件的路径*/
	private String path;
	/**模板项目列表*/
	private ArrayList fieldList;
	/**上传附件页面是确定还是确定并附加另一个*/
	private String isok;//=1为确定,=2为确定并附加另一个
	/**人员库列表*/
	private ArrayList nbaselist = new ArrayList();
	private String nbase;
	/**邮件发送状态*/
	private String sendok;//=1全部,=2未成功,=3成功,=4未发
	/**发送邮件人员列表*/
	private ArrayList sendPersonList = new ArrayList();
	/**薪资类别id*/
	private String salaryid;
	/**组织机构代码*/
	private String code;
	/**时时分页标签用sql语句*/
	/**1 select 部分*/
	private String select_sql;
	/**2 where 部分*/
	private String where_sql;
	/**3 order 部分*/
	private String order_sql;
	/**4 列名*/
	private String columns;
	/**删除选中人员的a0100（多删）*/
	private String selectid;
	/**权限范围内的应用库*/
	private String privdbpre;
	/**人名*/
	private String a0101;
	/**邮件指标的名称*/
	private String fieldname;
	/**邮件模板列表*/
	private String priv;
	private ArrayList templateList=new ArrayList();
	private String templateId;
	private String queryvalue="";
	private ArrayList querylist = new ArrayList();
	private String projectpath;
	/**一个参数，解决从form里取值，还是从连接后面取值的问题*/
	private String optValue="";
	/**附件列表的大小*/
    private String attachSize;
    /**按姓名查询*/
    private String queryName;
    /**按选择的人员发送邮件*/
    private String selectedid;
    private ArrayList constantList = new ArrayList();
    private String constantID;
    private String order_by;
    private String treeJS;
    private String message;
    private String showUnitCodeTree;
	private String queryYearValue="";//年份过滤
	private ArrayList queryListYear = new ArrayList();
    /**人员筛选条件*/
    private String filterId;
    private ArrayList filterList = new ArrayList();
    private String beforeSql="";
    private String tableName;
    //邮件模板，新建模板公式修改，公式信息  jingq add 2014.10.10
    private String newcontent;
    private String dxFlag;//短信参数配置
    private String wxFlag;//微信参数配置
	public String getNewcontent() {
		return newcontent;
	}

	public void setNewcontent(String newcontent) {
		this.newcontent = newcontent;
	}
	public void setDxFlag(String dxFlag) {
		this.dxFlag = dxFlag;
	}
	public void setWxFlag(String wxFlag) {
		this.wxFlag = wxFlag;
	}
	public String getDxFlag() {
		return dxFlag;
	}
	public String getWxFlag() {
		return wxFlag;
	}
	@Override
    public void outPutFormHM() {
	/*dateFormat;integerdigit;decimalfractiondigit;formulatitle; formulatype; formulalength;formulacontent*/
		this.setTableName((String)this.getFormHM().get("tableName"));
		this.setBeforeSql((String)this.getFormHM().get("beforeSql"));
		this.setFilterList((ArrayList)this.getFormHM().get("filterList"));
		this.setFilterId((String)this.getFormHM().get("filterId"));
		this.setShowUnitCodeTree((String)this.getFormHM().get("showUnitCodeTree"));
		this.setMessage((String)this.getFormHM().get("message"));
		this.setTreeJS((String)this.getFormHM().get("treeJS"));
		this.setOrder_by((String)this.getFormHM().get("order_by"));
		this.setIntegerdigit((String)this.getFormHM().get("integerdigit"));
		this.setDecimalfractiondigit((String)this.getFormHM().get("decimalfractiondigit"));
		this.setFormulatype((String)this.getFormHM().get("formulatype"));
		this.setFormulatitle((String)this.getFormHM().get("formulatitle"));
		this.setFormulalength((String)this.getFormHM().get("formulalength"));
		this.setFormulacontent((String)this.getFormHM().get("formulacontent"));
		this.setConstantID((String)this.getFormHM().get("constantID"));
		this.setConstantList((ArrayList)this.getFormHM().get("constantList"));
		this.setSelectedid((String)this.getFormHM().get("selectedid"));
		this.setPagerows(Integer.parseInt((String)this.getFormHM().get("pagerows")));
		this.setQueryName((String)this.getFormHM().get("queryName"));
		this.setAttachSize((String)this.getFormHM().get("attachSize"));
		this.setOptValue((String)this.getFormHM().get("optValue"));
		this.setNflag((String)this.getFormHM().get("nflag"));
		this.setSubject((String)this.getFormHM().get("subject"));
		this.setContent((String)this.getFormHM().get("content"));
		this.setName((String)this.getFormHM().get("name"));
		this.setFieldsetlist((ArrayList)this.getFormHM().get("fieldsetlist"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setFieldsetid((String)this.getFormHM().get("fieldsetid"));
		this.setDateFormatList((ArrayList)this.getFormHM().get("dateFormatList"));
		this.setDateFormat((String)this.getFormHM().get("dateFormat"));
		this.setCodefieldlist((ArrayList)this.getFormHM().get("codefieldlist"));
		this.setCodevalue((String)this.getFormHM().get("codevalue"));
		this.setFieldid((String)this.getFormHM().get("fieldid"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		this.setFormulafieldsetid((String)this.getFormHM().get("formulafieldsetid"));
		this.setId((String)this.getFormHM().get("id"));
		this.setAttachlist((ArrayList)this.getFormHM().get("attachlist"));
		this.setMaxid((String)this.getFormHM().get("maxid"));
		this.setFieldList((ArrayList)this.getFormHM().get("fieldList"));
		this.setIsok((String)this.getFormHM().get("isok"));
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setNbaselist((ArrayList)this.getFormHM().get("nbaselist"));
		this.setSalaryid((String)this.getFormHM().get("salaryid"));
		this.setSendok((String)this.getFormHM().get("sendok"));
		this.setSendPersonList((ArrayList)this.getFormHM().get("sendPersonList"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setSelect_sql((String)this.getFormHM().get("select_sql"));
		this.setWhere_sql((String)this.getFormHM().get("where_sql"));
		this.setOrder_sql((String)this.getFormHM().get("order_sql"));
		this.setColumns((String)this.getFormHM().get("columns"));
	    this.setPrivdbpre((String)this.getFormHM().get("privdbpre"));
	    this.setAddress((String)this.getFormHM().get("address"));
	    this.setA0101((String)this.getFormHM().get("a0101"));
	    this.setFieldname((String)this.getFormHM().get("fieldname"));
	    this.setTemplateList((ArrayList)this.getFormHM().get("templateList"));
        this.setTemplateId((String)this.getFormHM().get("templateId"));
        this.setQueryvalue((String)this.getFormHM().get("queryvalue"));
        this.setQuerylist((ArrayList)this.getFormHM().get("querylist"));
	    this.setPriv((String)this.getFormHM().get("priv"));
	    this.setNmodule((String)this.getFormHM().get("nmodule"));
	    this.setNewcontent((String) this.getFormHM().get("newcontent"));
	    this.setDxFlag((String) this.getFormHM().get("dxFlag"));
	    this.setWxFlag((String) this.getFormHM().get("wxFlag"));
	    
	    
        this.setQueryYearValue((String)this.getFormHM().get("queryYearValue"));
        this.setQueryListYear((ArrayList)this.getFormHM().get("queryListYear"));
	    
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("tableName", this.getTableName());
		this.getFormHM().put("beforeSql", this.getBeforeSql());
		this.getFormHM().put("filterId",this.getFilterId());
		this.getFormHM().put("showUnitCodeTree", this.getShowUnitCodeTree());
		this.getFormHM().put("order_sql", this.getOrder_sql());
		this.getFormHM().put("message", this.getMessage());
		this.getFormHM().put("treeJS", this.getTreeJS());
		this.getFormHM().put("order_by", this.getOrder_by());
		this.getFormHM().put("nmodule", this.getNmodule());
		this.getFormHM().put("selectedid", this.getSelectedid());
		this.getFormHM().put("pagerows", this.getPagerows()+"");
		this.getFormHM().put("queryName", this.getQueryName());
		this.getFormHM().put("attachSize", this.getAttachSize());
		this.getFormHM().put("optValue",this.getOptValue());
		this.getFormHM().put("projectpath",this.getProjectpath());
		this.getFormHM().put("subject",this.getSubject());
		this.getFormHM().put("content",this.getContent());
		this.getFormHM().put("name",this.getName());
		this.getFormHM().put("integerdigit",this.getIntegerdigit());
		this.getFormHM().put("decimalfractiondigit",this.getDecimalfractiondigit());
		this.getFormHM().put("formulatitle",this.getFormulatitle());
		this.getFormHM().put("formulalength",this.getFormulalength());
		this.getFormHM().put("codevalue",this.getCodevalue());
		this.getFormHM().put("itemid",this.getItemid());
		this.getFormHM().put("id",this.getId());
		this.getFormHM().put("address",this.getAddress());
		this.getFormHM().put("attach",this.getAttach());
		this.getFormHM().put("file",this.getFile());
		this.getFormHM().put("maxid",this.getMaxid());
		this.getFormHM().put("path",this.getPath());
		this.getFormHM().put("nbase",this.getNbase());
		this.getFormHM().put("sendok",this.getSendok());
		this.getFormHM().put("salaryid",this.getSalaryid());
		this.getFormHM().put("code",this.getCode());
		this.getFormHM().put("selectid",this.getSelectid());
		this.getFormHM().put("templateId",this.getTemplateId());
		this.getFormHM().put("queryvalue",this.getQueryvalue());
		this.getFormHM().put("queryYearValue",this.getQueryYearValue());
		this.getFormHM().put("newcontent", this.getNewcontent());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		String pajs=arg1.getSession().getServletContext().getRealPath("/UserFiles");
		this.setProjectpath(SafeCode.encode(pajs));
		if("/general/email_template/gz_send_email".equals(arg0.getPath())&&(arg1.getParameter("b_search")!=null||arg1.getParameter("b_init")!=null))
		{
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		return super.validate(arg0, arg1);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNinfoclass() {
		return ninfoclass;
	}

	public void setNinfoclass(String ninfoclass) {
		this.ninfoclass = ninfoclass;
	}

	public String getNmodule() {
		return nmodule;
	}

	public void setNmodule(String nmodule) {
		this.nmodule = nmodule;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getNflag() {
		return nflag;
	}

	public void setNflag(String nflag) {
		this.nflag = nflag;
	}

	public String getFieldsetid() {
		return fieldsetid;
	}

	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}

	public ArrayList getFieldsetlist() {
		return fieldsetlist;
	}

	public void setFieldsetlist(ArrayList fieldsetlist) {
		this.fieldsetlist = fieldsetlist;
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

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public ArrayList getDateFormatList() {
		return dateFormatList;
	}

	public void setDateFormatList(ArrayList dateFormatList) {
		this.dateFormatList = dateFormatList;
	}

	public String getDecimalfractiondigit() {
		return decimalfractiondigit;
	}

	public void setDecimalfractiondigit(String decimalfractiondigit) {
		this.decimalfractiondigit = decimalfractiondigit;
	}

	public String getIntegerdigit() {
		return integerdigit;
	}

	public void setIntegerdigit(String integerdigit) {
		this.integerdigit = integerdigit;
	}

	public String getFormulatitle() {
		return formulatitle;
	}

	public void setFormulatitle(String formulatitle) {
		this.formulatitle = formulatitle;
	}

	public String getFormulalength() {
		return formulalength;
	}

	public void setFormulalength(String formulalength) {
		this.formulalength = formulalength;
	}

	public ArrayList getCodefieldlist() {
		return codefieldlist;
	}

	public void setCodefieldlist(ArrayList codefieldlist) {
		this.codefieldlist = codefieldlist;
	}

	public String getCodevalue() {
		return codevalue;
	}

	public void setCodevalue(String codevalue) {
		this.codevalue = codevalue;
	}

	public String getFieldid() {
		return fieldid;
	}

	public void setFieldid(String fieldid) {
		this.fieldid = fieldid;
	}

	public String getFormulafieldsetid() {
		return formulafieldsetid;
	}

	public void setFormulafieldsetid(String formulafieldsetid) {
		this.formulafieldsetid = formulafieldsetid;
	}

	public ArrayList getAttachlist() {
		return attachlist;
	}

	public void setAttachlist(ArrayList attachlist) {
		this.attachlist = attachlist;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getMaxid() {
		return maxid;
	}

	public void setMaxid(String maxid) {
		this.maxid = maxid;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public ArrayList getFieldList() {
		return fieldList;
	}

	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}

	public String getIsok() {
		return isok;
	}

	public void setIsok(String isok) {
		this.isok = isok;
	}

	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public ArrayList getNbaselist() {
		return nbaselist;
	}

	public void setNbaselist(ArrayList nbaselist) {
		this.nbaselist = nbaselist;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public String getSendok() {
		return sendok;
	}

	public void setSendok(String sendok) {
		this.sendok = sendok;
	}

	public ArrayList getSendPersonList() {
		return sendPersonList;
	}

	public void setSendPersonList(ArrayList sendPersonList) {
		this.sendPersonList = sendPersonList;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getOrder_sql() {
		return order_sql;
	}

	public void setOrder_sql(String order_sql) {
		this.order_sql = order_sql;
	}

	public String getSelect_sql() {
		return select_sql;
	}

	public void setSelect_sql(String select_sql) {
		this.select_sql = select_sql;
	}

	public String getWhere_sql() {
		return where_sql;
	}

	public void setWhere_sql(String where_sql) {
		this.where_sql = where_sql;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getSelectid() {
		return selectid;
	}

	public void setSelectid(String selectid) {
		this.selectid = selectid;
	}

	public String getPrivdbpre() {
		return privdbpre;
	}

	public void setPrivdbpre(String privdbpre) {
		this.privdbpre = privdbpre;
	}

	public String getA0101() {
		return a0101;
	}

	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}

	public String getFieldname() {
		return fieldname;
	}

	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	public ArrayList getTemplateList() {
		return templateList;
	}

	public void setTemplateList(ArrayList templateList) {
		this.templateList = templateList;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public ArrayList getQuerylist() {
		return querylist;
	}

	public void setQuerylist(ArrayList querylist) {
		this.querylist = querylist;
	}

	public String getQueryvalue() {
		return queryvalue;
	}

	public void setQueryvalue(String queryvalue) {
		this.queryvalue = queryvalue;
	}

	public String getProjectpath() {
		return projectpath;
	}

	public void setProjectpath(String projectpath) {
		this.projectpath = projectpath;
	}

	public String getPriv() {
		return priv;
	}

	public void setPriv(String priv) {
		this.priv = priv;
	}

	public String getOptValue() {
		return optValue;
	}

	public void setOptValue(String optValue) {
		this.optValue = optValue;
	}

	public String getAttachSize() {
		return attachSize;
	}

	public void setAttachSize(String attachSize) {
		this.attachSize = attachSize;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public String getSelectedid() {
		return selectedid;
	}

	public void setSelectedid(String selectedid) {
		this.selectedid = selectedid;
	}

	public ArrayList getConstantList() {
		return constantList;
	}

	public void setConstantList(ArrayList constantList) {
		this.constantList = constantList;
	}

	public String getConstantID() {
		return constantID;
	}

	public void setConstantID(String constantID) {
		this.constantID = constantID;
	}

	public String getFormulatype() {
		return formulatype;
	}

	public void setFormulatype(String formulatype) {
		this.formulatype = formulatype;
	}

	public String getFormulacontent() {
		return formulacontent;
	}

	public void setFormulacontent(String formulacontent) {
		this.formulacontent = formulacontent;
	}

	public String getOrder_by() {
		return order_by;
	}

	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}

	public String getTreeJS() {
		return treeJS;
	}

	public void setTreeJS(String treeJS) {
		this.treeJS = treeJS;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getShowUnitCodeTree() {
		return showUnitCodeTree;
	}

	public void setShowUnitCodeTree(String showUnitCodeTree) {
		this.showUnitCodeTree = showUnitCodeTree;
	}

	public String getFilterId() {
		return filterId;
	}

	public void setFilterId(String filterId) {
		this.filterId = filterId;
	}

	public ArrayList getFilterList() {
		return filterList;
	}

	public void setFilterList(ArrayList filterList) {
		this.filterList = filterList;
	}

	public String getBeforeSql() {
		return beforeSql;
	}

	public void setBeforeSql(String beforeSql) {
		this.beforeSql = beforeSql;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getQueryYearValue() {
		return queryYearValue;
	}

	public void setQueryYearValue(String queryYearValue) {
		this.queryYearValue = queryYearValue;
	}

	public ArrayList getQueryListYear() {
		return queryListYear;
	}

	public void setQueryListYear(ArrayList queryListYear) {
		this.queryListYear = queryListYear;
	}
}
