package com.hjsj.hrms.actionform.performance.nworkplan.season;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Title:NewWorkPlanForm.java</p>
 * <p>Description:工作计划总结(国网)</p>
 * <p>Company:hjsj</p>
 * <p>create time:2013-03-01 11:11:11</p> 
 * @author JinJiaWei
 * @version 
 */
public class NewWorkPlanForm extends FrameForm{
	
	private String year;   //年份
	private String season; //季度
	private String startMonth; //当前季度 开始月份 用于显示
	private String endMonth;   //当前季度 结束月份 用于显示
	private String type;       // 1 季报  2 年报
	private String content;     //查询时用到的变量
	private String sql;//所有的页面数据都是通过sql语句查出来的
	private String where;
	private String cols;
	private String opt;        //判断是从哪个入口进入的 1 、 从自己进入 2、从团队工作计划进入(领导进入)
	private String returnUrl;//从团队那里返回来的链接
	private String isread;     //2 可写 1 只读
	private String p0100;      //传入参数
	private String isdept;     //传入参数 判断是进个人还是部门季报与年报 1 个人 2 部门
	private String isseason;   //进入的是年报还是季报 1 季报 2 年报
	private String belong_type; // 团队进入时带入参数 1 进入处室 2进入部门
	private FormFile file;
	private String file_id;    //删除的时候传入的文件ID
	private String isCommitOk;//是否已经提交
	private String isTooBig;//上传的文件是否过大 0：正常1：文件过大 2：文件内容为空
	
	//以下是用不到的变量
	private String message;    //填报内容
	private String shows;
	private String states;     // 审批状态汉字
	private String code;       // 编码
	private String search_type; // 查询参数
	private String islike;     //控制参数 是否模糊查询
	private String in_type;    
	private String manypeople;  //有多个审批人的时候选择的当前审批人进行报批
	private String a0100;      //传入的a0100 从团队工作计划入口进入的时候需传入的参数
	private String nbase;      //传入的nbase 从团队工作计划入口进入的时候需传入的参数
	
	private PaginationForm recordListForm=new PaginationForm(); 
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("year", this.getYear());
		this.getFormHM().put("season", this.getSeason());
		this.getFormHM().put("startMonth", this.getStartMonth());
		this.getFormHM().put("endMonth", this.getEndMonth());
		this.getFormHM().put("message", this.getMessage());
		this.getFormHM().put("shows", this.getShows());
		this.getFormHM().put("type", this.getType());
		this.getFormHM().put("states", this.getStates());
		this.getFormHM().put("code", this.getCode());
		this.getFormHM().put("search_type", this.getSearch_type());
		this.getFormHM().put("sql", this.getSql());
		this.getFormHM().put("where", this.getWhere());
		this.getFormHM().put("cols", this.getCols());
		this.getFormHM().put("islike", this.getIslike());
		this.getFormHM().put("content", this.getContent());
		this.getFormHM().put("in_type", this.getIn_type());
		this.getFormHM().put("manypeople", this.getManypeople());
		this.getFormHM().put("opt", this.getOpt());
		this.getFormHM().put("isread", this.getIsread());
		this.getFormHM().put("returnUrl", this.getReturnUrl());
		this.getFormHM().put("a0100", this.getA0100());
		this.getFormHM().put("nbase", this.getNbase());
		this.getFormHM().put("p0100", this.getP0100());
		this.getFormHM().put("isdept", this.getIsdept());
		this.getFormHM().put("isseason", this.getIsseason());
		this.getFormHM().put("belong_type", this.getBelong_type());
		this.getFormHM().put("file", this.getFile());
		this.getFormHM().put("file_id", this.getFile_id());
		this.getFormHM().put("isCommitOk", this.getIsCommitOk());
		this.getFormHM().put("isTooBig", this.getIsTooBig());
		
	}

	@Override
    public void outPutFormHM() {
		this.setYear((String)this.getFormHM().get("year"));
		this.setSeason((String)this.getFormHM().get("season"));
		this.setStartMonth((String)this.getFormHM().get("startMonth"));
		this.setEndMonth((String)this.getFormHM().get("endMonth"));
		this.setMessage((String)this.getFormHM().get("message"));
		this.setShows((String)this.getFormHM().get("shows"));
		this.setType((String)this.getFormHM().get("type"));
		this.setStates((String)this.getFormHM().get("states"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setSearch_type((String)this.getFormHM().get("search_type"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setWhere((String)this.getFormHM().get("where"));
		this.setCols((String)this.getFormHM().get("cols"));
		this.setIslike((String)this.getFormHM().get("islike"));
		this.setContent((String)this.getFormHM().get("content"));
		this.setIn_type((String)this.getFormHM().get("in_type"));
		this.setManypeople((String)this.getFormHM().get("manypeople"));
		this.setOpt((String)this.getFormHM().get("opt"));
		this.setIsread((String)this.getFormHM().get("isread"));
		this.setReturnUrl((String)this.getFormHM().get("returnUrl"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setP0100((String)this.getFormHM().get("p0100"));
		this.setIsdept((String)this.getFormHM().get("isdept"));
		this.setIsseason((String)this.getFormHM().get("isseason"));
		this.setBelong_type((String)this.getFormHM().get("belong_type"));
		this.setFile_id((String)this.getFormHM().get("file_id"));
		this.setIsCommitOk((String)this.getFormHM().get("isCommitOk"));
		this.setIsTooBig((String)this.getFormHM().get("isTooBig"));
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
	try
	{
	    if ("/kq/month_kq/searchkqinfo".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
	    {
		if (this.getPagination() != null)
		    this.getPagination().firstPage();
	    } else if ("/kq/month_kq/searchkqinfo".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
	    {
		/** 定位到首页, */
		if (this.getPagination() != null)
		    this.getPagination().firstPage();
	    } else if ("/kq/month_kq/searchkqinfo".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
	    {
		/** 定位到首页, */
		if (this.getPagination() != null)
		    this.getPagination().firstPage();
	    } else if ("/kq/month_kq/searchkqinfo".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
	    {
		/** 定位到首页, */
		if (this.getPagination() != null)
		    this.getPagination().firstPage();
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return super.validate(arg0, arg1);
    }
	
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public String getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}

	public String getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getShows() {
		return shows;
	}

	public void setShows(String shows) {
		this.shows = shows;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getStates() {
		return states;
	}

	public void setStates(String states) {
		this.states = states;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSearch_type() {
		return search_type;
	}

	public void setSearch_type(String search_type) {
		this.search_type = search_type;
	}
	
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getCols() {
		return cols;
	}

	public void setCols(String cols) {
		this.cols = cols;
	}
	
	public PaginationForm getRecordListForm() {
		return recordListForm;
	}

	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}

	public String getIslike() {
		return islike;
	}

	public void setIslike(String islike) {
		this.islike = islike;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getIn_type() {
		return in_type;
	}

	public void setIn_type(String in_type) {
		this.in_type = in_type;
	}

	public String getManypeople() {
		return manypeople;
	}

	public void setManypeople(String manypeople) {
		this.manypeople = manypeople;
	}

	public String getOpt() {
		return opt;
	}

	public void setOpt(String opt) {
		this.opt = opt;
	}

	public String getIsread() {
		return isread;
	}

	public void setIsread(String isread) {
		this.isread = isread;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public String getP0100() {
		return p0100;
	}

	public void setP0100(String p0100) {
		this.p0100 = p0100;
	}

	public String getIsdept() {
		return isdept;
	}

	public void setIsdept(String isdept) {
		this.isdept = isdept;
	}

	public String getIsseason() {
		return isseason;
	}

	public void setIsseason(String isseason) {
		this.isseason = isseason;
	}

	public String getBelong_type() {
		return belong_type;
	}

	public void setBelong_type(String belong_type) {
		this.belong_type = belong_type;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getFile_id() {
		return file_id;
	}

	public void setFile_id(String file_id) {
		this.file_id = file_id;
	}

	public String getIsCommitOk() {
		return isCommitOk;
	}

	public void setIsCommitOk(String isCommitOk) {
		this.isCommitOk = isCommitOk;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getIsTooBig() {
		return isTooBig;
	}

	public void setIsTooBig(String isTooBig) {
		this.isTooBig = isTooBig;
	}
	
}
