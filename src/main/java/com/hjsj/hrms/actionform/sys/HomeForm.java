/**
 * 
 */
package com.hjsj.hrms.actionform.sys;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-18:16:29:32</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class HomeForm extends FrameForm {
	
	/**公告栏内容*/
	private String boardcontent;
	private String cond;
	private String muster;
	private String dbpre;
	private String ykcard;
	private String stat;
	private String matter;
	private String warn;
	private String report;
	private String hmuster;
	private String salary;
    private String statserial;
    private String warnserial;
    private String ykcardserial;
    private String musterserial;
    private String reportserial;
    private String condserial;
    private String boardcontentserial;
    private String matterserial;
    private String statisvisible;
    private String warnisvisible;
    private String ykcardisvisible;
    private String musterisvisible;
    private String reportisvisible;
    private String condisvisible;
    private String boardcontentisvisible;
    private String matterisvisible;
    private String salaryisvisible;
    private String salaryserial;
    private ArrayList morelist=new ArrayList();
    private ArrayList matterList=new ArrayList();
    private PaginationForm recordListForm=new PaginationForm();   
    
    private String staff_info;
	private String complex_expr="";
	private String complex_id="";
	private String complex_name="";
	private String strsql="";
	private String columns="";
	private String order="";
	private int peopleNumber=0;//明星员工人数
	private ArrayList peopledesc=new ArrayList();
	private ArrayList photourl = new ArrayList();
	private ArrayList starfields =new ArrayList();
	

	public ArrayList getStarfields() {
		return starfields;
	}

	public void setStarfields(ArrayList starfields) {
		this.starfields = starfields;
	}

	public String getWarn() {
		return warn;
	}

	public void setWarn(String warn) {
		this.warn = warn;
	}

	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getMuster() {
		return muster;
	}

	public void setMuster(String muster) {
		this.muster = muster;
	}

	public String getCond() {
		return cond;
	}

	public void setCond(String cond) {
		this.cond = cond;
	}

	public String getBoardcontent() {
		return boardcontent;
	}

	public void setBoardcontent(String boardcontent) {
		this.boardcontent = boardcontent;
	}

	@Override
    public void outPutFormHM() {
		this.setBoardcontent((String)this.getFormHM().get("board"));
		this.setCond((String)this.getFormHM().get("cond"));
		this.setMuster((String)this.getFormHM().get("muster"));
		this.setDbpre((String)this.getFormHM().get("dbpre"));
		this.setWarn((String)this.getFormHM().get("warn"));

		this.setStat((String)this.getFormHM().get("stat"));
		this.setYkcard((String)this.getFormHM().get("ykcard"));
		this.setHmuster((String)this.getFormHM().get("hmuster"));
		this.setReport((String)this.getFormHM().get("report"));
		this.setMatter((String)this.getFormHM().get("matter"));
		this.setSalary((String)this.getFormHM().get("salary"));
		this.setStatisvisible((String)this.getFormHM().get("statisvisible"));
		this.setStatserial((String)this.getFormHM().get("statserial"));
		this.setWarnisvisible((String)this.getFormHM().get("warnisvisible"));
		this.setWarnserial((String)this.getFormHM().get("warnserial"));
		this.setYkcardserial((String)this.getFormHM().get("ykcardserial"));
		this.setYkcardisvisible((String)this.getFormHM().get("ykcardisvisible"));
		this.setMusterisvisible((String)this.getFormHM().get("musterisvisible"));
		this.setMusterserial((String)this.getFormHM().get("musterserial"));
		this.setReportisvisible((String)this.getFormHM().get("reportisvisible"));
		this.setReportserial((String)this.getFormHM().get("reportserial"));
		this.setCondisvisible((String)this.getFormHM().get("condisvisible"));
		this.setCondserial((String)this.getFormHM().get("condserial"));
		this.setBoardcontentisvisible((String)this.getFormHM().get("boardcontentisvisible"));
		this.setBoardcontentserial((String)this.getFormHM().get("boardcontentserial"));	
		this.setMorelist((ArrayList)this.getFormHM().get("morelist"));
		this.setMatterisvisible((String)this.getFormHM().get("matterisvisible"));
		this.setMatterserial((String)this.getFormHM().get("matterserial"));
        this.setSalaryisvisible((String)this.getFormHM().get("salaryisvisible"));
        this.setSalaryserial((String)this.getFormHM().get("salaryserial"));
        this.getRecordListForm().setList((ArrayList)this.getFormHM().get("matterList"));
        
        
        
        this.setStaff_info((String)this.getFormHM().get("staff_info"));
		this.setComplex_expr((String)this.getFormHM().get("complex_expr"));
		this.setComplex_id((String)this.getFormHM().get("complex_id"));
		this.setDbpre((String)this.getFormHM().get("dbpre"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setOrder((String)this.getFormHM().get("order"));
		this.setPeopleNumber(Integer.parseInt(this.getFormHM().get("peopleNumber").toString()));
		this.setPeopledesc((ArrayList)this.getFormHM().get("peopledesc"));
		this.setPhotourl((ArrayList)this.getFormHM().get("photourl"));
		this.setStarfields((ArrayList)this.getFormHM().get("starfields"));
	}


	@Override
    public void inPutTransHM() {
		
		this.getFormHM().put("staff_info", this.getStaff_info());
		this.getFormHM().put("complex_expr", this.getComplex_expr());
		this.getFormHM().put("complex_id", this.getComplex_id());
		this.getFormHM().put("complex_name", this.getComplex_name());
		this.getFormHM().put("dbpre", this.getDbpre());
		this.getFormHM().put("peopleNumber", String.valueOf(peopleNumber));
		this.getFormHM().put("peopledesc", this.getPeopledesc());
		this.getFormHM().put("photourl", this.getPhotourl());
		this.getFormHM().put("starfields", this.getStarfields());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	    if("/general/template/matterList".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	    {
	    	if(this.recordListForm.getPagination()!=null)
		          this.recordListForm.getPagination().firstPage();//?
	    }
	    return super.validate(arg0, arg1);
	}
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		this.setBoardcontent("");
		this.setCond("");
		this.setMuster("");
		this.setDbpre("");
		this.setWarn("");
		super.reset(arg0, arg1);
	}


	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getYkcard() {
		return ykcard;
	}

	public void setYkcard(String ykcard) {
		this.ykcard = ykcard;
	}


	public String getHmuster() {
		return hmuster;
	}

	public void setHmuster(String hmuster) {
		this.hmuster = hmuster;
	}

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public String getBoardcontentserial() {
		return boardcontentserial;
	}

	public void setBoardcontentserial(String boardcontentserial) {
		this.boardcontentserial = boardcontentserial;
	}


	public String getStatisvisible() {
		return statisvisible;
	}

	public void setStatisvisible(String statisvisible) {
		this.statisvisible = statisvisible;
	}

	public String getStatserial() {
		return statserial;
	}

	public void setStatserial(String statserial) {
		this.statserial = statserial;
	}

	public String getWarnisvisible() {
		return warnisvisible;
	}

	public void setWarnisvisible(String warnisvisible) {
		this.warnisvisible = warnisvisible;
	}

	public String getWarnserial() {
		return warnserial;
	}

	public void setWarnserial(String warnserial) {
		this.warnserial = warnserial;
	}

	public String getYkcardserial() {
		return ykcardserial;
	}

	public void setYkcardserial(String ykcardserial) {
		this.ykcardserial = ykcardserial;
	}

	public String getBoardcontentisvisible() {
		return boardcontentisvisible;
	}

	public void setBoardcontentisvisible(String boardcontentisvisible) {
		this.boardcontentisvisible = boardcontentisvisible;
	}

	public String getCondisvisible() {
		return condisvisible;
	}

	public void setCondisvisible(String condisvisible) {
		this.condisvisible = condisvisible;
	}

	public String getCondserial() {
		return condserial;
	}

	public void setCondserial(String condserial) {
		this.condserial = condserial;
	}

	public String getMusterisvisible() {
		return musterisvisible;
	}

	public void setMusterisvisible(String musterisvisible) {
		this.musterisvisible = musterisvisible;
	}

	public String getMusterserial() {
		return musterserial;
	}

	public void setMusterserial(String musterserial) {
		this.musterserial = musterserial;
	}

	public String getReportisvisible() {
		return reportisvisible;
	}

	public void setReportisvisible(String reportisvisible) {
		this.reportisvisible = reportisvisible;
	}

	public String getReportserial() {
		return reportserial;
	}

	public void setReportserial(String reportserial) {
		this.reportserial = reportserial;
	}

	public String getYkcardisvisible() {
		return ykcardisvisible;
	}

	public void setYkcardisvisible(String ykcardisvisible) {
		this.ykcardisvisible = ykcardisvisible;
	}

	public ArrayList getMorelist() {
		return morelist;
	}

	public void setMorelist(ArrayList morelist) {
		this.morelist = morelist;
	}

	public String getMatter() {
		return matter;
	}

	public void setMatter(String matter) {
		this.matter = matter;
	}

	public String getMatterserial() {
		return matterserial;
	}

	public void setMatterserial(String matterserial) {
		this.matterserial = matterserial;
	}

	public String getMatterisvisible() {
		return matterisvisible;
	}

	public void setMatterisvisible(String matterisvisible) {
		this.matterisvisible = matterisvisible;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getSalaryisvisible() {
		return salaryisvisible;
	}

	public void setSalaryisvisible(String salaryisvisible) {
		this.salaryisvisible = salaryisvisible;
	}

	public String getSalaryserial() {
		return salaryserial;
	}

	public void setSalaryserial(String salaryserial) {
		this.salaryserial = salaryserial;
	}

	public ArrayList getMatterList() {
		return matterList;
	}

	public void setMatterList(ArrayList matterList) {
		this.matterList = matterList;
	}

	public PaginationForm getRecordListForm() {
		return recordListForm;
	}

	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}

	public String getComplex_expr() {
		return complex_expr;
	}

	public void setComplex_expr(String complex_expr) {
		this.complex_expr = complex_expr;
	}

	public String getComplex_id() {
		return complex_id;
	}

	public void setComplex_id(String complex_id) {
		this.complex_id = complex_id;
	}

	public String getComplex_name() {
		return complex_name;
	}

	public void setComplex_name(String complex_name) {
		this.complex_name = complex_name;
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

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getStaff_info() {
		return staff_info;
	}

	public void setStaff_info(String staff_info) {
		this.staff_info = staff_info;
	}

	public int getPeopleNumber() {
		return peopleNumber;
	}

	public void setPeopleNumber(int peopleNumber) {
		this.peopleNumber = peopleNumber;
	}

	public ArrayList getPeopledesc() {
		return peopledesc;
	}

	public void setPeopledesc(ArrayList peopledesc) {
		this.peopledesc = peopledesc;
	}

	public ArrayList getPhotourl() {
		return photourl;
	}

	public void setPhotourl(ArrayList photourl) {
		this.photourl = photourl;
	}

}
