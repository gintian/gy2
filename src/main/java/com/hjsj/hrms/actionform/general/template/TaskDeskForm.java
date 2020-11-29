/**
 * 
 */
package com.hjsj.hrms.actionform.general.template;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:TaskDeskForm</p>
 * <p>Description:任务控制</p> 
 * <p>Company:hjsj</p> 
 * create time at:Oct 27, 20069:24:42 AM
 * @author chenmengqing
 * @version 4.0
 */
public class TaskDeskForm extends FrameForm {
	/*
	 * 1:人事异动；2：是薪资管理	；8：保险变动；21：劳动合同；12：出国管理 ;10:单位管理机构调整;11:岗位管理机构调整
	 */
	private String type="1";
	
	/**来源，自助平台　还是业务平台　*/
	private String fromflag="3";
	/**审批标志*/
	private String sp_flag;
	/**任务查询方式
	 * =1按最近多少天查
	 * =2按时间段果
	 * */
	private String query_type="1";
	/**任务查询方式
	 * =1按运行中
	 * =2按结束
	 * */
	private String query_method="1";
	/**多少天*/
    private String days="0";//由30改成0，在下面validate方法里面设置为30 wangrd 20160409
	/**起始终止日期*/
	private String start_date;
	
	private String end_date;
		/**查询语句及需要显示字段列表*/
	private String strsql;
	private String columns;
	private String order_sql="";
	
	/**查询语句及需要显示字段列表*/
	private PaginationForm taskListForm = new PaginationForm(); 
	private ArrayList columnList=new ArrayList();
	private int current=1;
	
	
	private ArrayList templateList=new ArrayList();  //任务监控 模板列表
	private String    templateId="-1";
	
	
	/**流程实例号*/
	private String ins_id;
	private String ins_ids="";
	private String taskid="";
	private String sp_batch="0";
	
	private String view="";  //默认进入页面方式 list列表、card卡片
	private String batch_task="0";
	private String tasklist_str="";
	private String tabid="";
	/**任务分配的对象*/
	private String actorid;
	private String actorname;
	private String actor_type="1";
	/**当前任务处理各个环节处理的意见的列表*/
    private PaginationForm sp_yjListForm=new PaginationForm();
    
    private String titlename;
    
    private String excelfile;
    
    private String isTask="1";  //是否是待批任务
    private String a0101="";	//查询人员姓名，机构名称，岗位名称
    private String tableName="";//模版名称
    private String a0101s="";
    private String specialRoleUserStr="";
    
    private String bs_flag="";
    private ArrayList bs_flag_list=new ArrayList();
    
    
    
	public String getExcelfile() {
		return excelfile;
	}

	public String getA0101() {
		return a0101;
	}

	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}

	public void setExcelfile(String excelfile) {
		this.excelfile = excelfile;
	}

	public PaginationForm getSp_yjListForm() {
		return sp_yjListForm;
	}

	public void setSp_yjListForm(PaginationForm sp_yjListForm) {
		this.sp_yjListForm = sp_yjListForm;
	}

	public String getSp_flag() {
		return sp_flag;
	}

	public void setSp_flag(String sp_flag) {
		this.sp_flag = sp_flag;
	}

	@Override
    public void outPutFormHM() {
		this.setBs_flag((String)this.getFormHM().get("bs_flag"));
		this.setBs_flag_list((ArrayList)this.getFormHM().get("bs_flag_list"));
		this.setType((String)this.getFormHM().get("type"));
		this.setTemplateList((ArrayList)this.getFormHM().get("templateList"));
		
		if(this.getFormHM().get("pagerows")==null||((String)this.getFormHM().get("pagerows")).trim().length()==0)
			this.setPagerows(21);
		else
			this.setPagerows(Integer.parseInt(((String)this.getFormHM().get("pagerows"))));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.getSp_yjListForm().setList((ArrayList)this.getFormHM().get("curryjlist"));
		this.setSp_batch((String)this.getFormHM().get("sp_batch"));
		this.getFormHM().remove("sp_batch");
		this.setOrder_sql((String)this.getFormHM().get("order_sql"));
		this.setTitlename((String)this.getFormHM().get("titlename"));
		this.setExcelfile((String)this.getFormHM().get("excelfile"));
		
		this.setTaskid((String)this.getFormHM().get("taskid"));
		this.setIns_id((String)this.getFormHM().get("ins_id"));
		this.setIns_ids((String)this.getFormHM().get("ins_ids"));
		
		
		this.getTaskListForm().setList((ArrayList)this.getFormHM().get("taskList"));
		this.getTaskListForm().getPagination().gotoPage(this.taskListForm.getPagination().getCurrent());
		this.setA0101s((String)this.getFormHM().get("a0101s"));
		this.setTableName((String)this.getFormHM().get("tableName"));
		this.setDays((String) this.getFormHM().get("days"));
		
		this.setView((String)this.getFormHM().get("view"));
		this.setBatch_task((String)this.getFormHM().get("batch_task"));
		this.setTasklist_str((String)this.getFormHM().get("tasklist_str"));
		this.setTabid((String)this.getFormHM().get("tabid"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("pagerows", this.getPagerows()==0?"21":(this.getPagerows()+""));
		this.getFormHM().put("templateId",this.getTemplateId());
		
		this.getFormHM().put("sp_flag",this.getSp_flag());
		this.getFormHM().put("days",this.getDays());
		this.getFormHM().put("query_type",this.getQuery_type());
		this.getFormHM().put("query_method",this.getQuery_method());
		this.getFormHM().put("start_date",this.getStart_date());
		this.getFormHM().put("end_date",this.getEnd_date());
		this.getFormHM().put("ins_id",this.getIns_id());
	    
		if(this.getPagination()!=null)
			this.getFormHM().put("selectedlist",this.getPagination().getSelectedList());
		if(this.getFormHM().get("isTask")!=null&& "1".equals((String)this.getFormHM().get("isTask")))
			this.getFormHM().put("selectedlist",this.getTaskListForm().getSelectedList());
	    this.getFormHM().put("actorid",this.getActorid());
	    this.getFormHM().put("actorname",this.getActorname());
	    this.getFormHM().put("actortype",this.getActor_type());
	    this.getFormHM().put("titlename",this.getTitlename());
	    this.getFormHM().put("a0101", this.getA0101());
	    this.getFormHM().put("specialRoleUserStr", this.getSpecialRoleUserStr());
	    
	    this.getFormHM().put("view", this.getView());
		this.getFormHM().put("batch_task", this.getBatch_task());
		this.getFormHM().put("tasklist_str", this.getTasklist_str());
		this.getFormHM().put("tabid", this.getTabid());
	}

	public String getQuery_type() {
		return query_type;
	}

	public void setQuery_type(String query_type) {
		this.query_type = query_type;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
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

	public String getColumns() {
		return columns;
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		this.getFormHM().put("session",arg1.getSession());

        if("/general/template/task_list".equals(arg0.getPath())){
            if ("0".equals(this.days)){
                this.days="360";//待办列表默认更改为360天，其他还是30天
            }
            this.getFormHM().put("isTask","1");
        }
        else
            this.getFormHM().put("isTask","0");
        
        if(!"/general/template/task_desktop".equals(arg0.getPath())){
            if ("0".equals(this.days)){
                this.days="30";
            }
        }
		//代办任务初始化进来,将默认是日期置为10天,防止时间比较慢,初始化指得是从人事异动申请进来时
		if("/general/template/ins_obj_list2".equalsIgnoreCase(arg0.getPath())&&arg1.getParameter("b_query2")!=null&& "link".equalsIgnoreCase(arg1.getParameter("b_query2"))){
		        this.setDays("10");
		}
		if(arg1.getParameter("b_query")!=null&& "link_query".equalsIgnoreCase(arg1.getParameter("b_query"))){
		    //待办和已办向前台传递的是list,因此将TaskListForm置为首页
		    if("/general/template/task_list".equalsIgnoreCase(arg0.getPath())){
		        this.getTaskListForm().getPagination().gotoPage(1);
		    }else{
		        this.getPagination().gotoPage(1);
		    }
		}
		if(arg1.getParameter("b_query2")!=null&& "link_query".equalsIgnoreCase(arg1.getParameter("b_query2"))){
		    if("/general/template/ins_obj_list2".equalsIgnoreCase(arg0.getPath())){
		        this.getTaskListForm().getPagination().gotoPage(1);
		    }
		}
		return super.validate(arg0, arg1);
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getStrsql() {
		return strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	public String getIns_id() {
		return ins_id;
	}

	public String getSpecialRoleUserStr() {
		return specialRoleUserStr;
	}

	public void setSpecialRoleUserStr(String specialRoleUserStr) {
		this.specialRoleUserStr = specialRoleUserStr;
	}

	public void setIns_id(String ins_id) {
		this.ins_id = ins_id;
	}

	public String getActorid() {
		return actorid;
	}

	public void setActorid(String actorid) {
		this.actorid = actorid;
	}

	public String getActorname() {
		return actorname;
	}

	public void setActorname(String actorname) {
		this.actorname = actorname;
	}

	public String getSp_batch() {
		return sp_batch;
	}

	public void setSp_batch(String sp_batch) {
		this.sp_batch = sp_batch;
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		this.setSp_batch("0");
		//this.setFromflag("3");
	    if(this.getPagination()!=null)  
	    {
	    	ArrayList list=this.getPagination().getSelect();// getCurr_page_list(); 	  
	    	if(list!=null)
	    	{
	    		for(int i=0;i<list.size();i++)
	    		{
	    			list.set(i,new Boolean(false));
	    		}
	    	}
	    }
		super.reset(arg0, arg1);
	}

	public String getActor_type() {
		return actor_type;
	}

	public void setActor_type(String actor_type) {
		this.actor_type = actor_type;
	}

	public String getFromflag() {
		return fromflag;
	}

	public void setFromflag(String fromflag) {
		this.fromflag = fromflag;
	}

	public String getOrder_sql() {
		return order_sql;
	}

	public void setOrder_sql(String order_sql) {
		this.order_sql = order_sql;
	}

	public String getTitlename() {
		return titlename;
	}

	public void setTitlename(String titlename) {
		this.titlename = titlename;
	}

	public String getIns_ids() {
		return ins_ids;
	}

	public void setIns_ids(String ins_ids) {
		this.ins_ids = ins_ids;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public PaginationForm getTaskListForm() {
		return taskListForm;
	}

	public void setTaskListForm(PaginationForm taskListForm) {
		this.taskListForm = taskListForm;
	}

	public ArrayList getColumnList() {
		return columnList;
	}

	public void setColumnList(ArrayList columnList) {
		this.columnList = columnList;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public String getIsTask() {
		return isTask;
	}

	public void setIsTask(String isTask) {
		this.isTask = isTask;
	}

	public ArrayList getTemplateList() {
		return templateList;
	}

	public void setTemplateList(ArrayList templateList) {
		this.templateList = templateList;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getA0101s() {
		return a0101s;
	}

	public String getQuery_method() {
		return query_method;
	}

	public void setQuery_method(String query_method) {
		this.query_method = query_method;
	}

	public void setA0101s(String a0101s) {
		this.a0101s = a0101s;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBs_flag() {
		return bs_flag;
	}

	public void setBs_flag(String bs_flag) {
		this.bs_flag = bs_flag;
	}

	public ArrayList getBs_flag_list() {
		return bs_flag_list;
	}

	public void setBs_flag_list(ArrayList bs_flag_list) {
		this.bs_flag_list = bs_flag_list;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getBatch_task() {
		return batch_task;
	}

	public void setBatch_task(String batch_task) {
		this.batch_task = batch_task;
	}

	public String getTasklist_str() {
		return tasklist_str;
	}

	public void setTasklist_str(String tasklist_str) {
		this.tasklist_str = tasklist_str;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

}
