package com.hjsj.hrms.actionform.kq.machine;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class KqCardDataForm extends FrameForm
{
	private String treeCode;//树形菜单，在HtmlMenu中
	private ArrayList sessionlist=new ArrayList();
	private ArrayList datelist=new ArrayList();
	private ArrayList kq_dbase_list=new ArrayList();
	private String cur_session;
	private String cur_date;
	private String a_code;
	private String code_kind;
	private String error_message;
	private String error_flag;
	private String error_return;
	private String error_stuts;
	private String nbase;
	private String a0100;
	private String sqlstr;
	private String column;
	private ArrayList fielditemlist=new ArrayList();
	private String workcalendar;
    private String cur_flag;
    private String machine_num;
    private ArrayList machinelist=new ArrayList();
    private ArrayList file_list=new ArrayList();
    private String file_num;
    private String file_url;
    private FormFile file;
    private String machine_mm;
    private String machine_hh;
    private String machine_date;
    //*******过滤文件属性
    private String filter_date_s;
    private String filter_date_e;
    private String filter_hh_s;
    private String filter_mm_s;
    private String filter_hh_e;
    private String filter_mm_e;
    private String filter_card;
    private String orderby;
    
    private String start_date;//开始时间
    private String start_hh;
    private String start_mm;
    private String end_date;//结束时间
    private String end_hh;
    private String end_mm;
    private ArrayList kq_list=new ArrayList();//人员库
    private String select_name;//筛选名字
    private String select_flag;//筛选表示
    private String select_pre;
    private String select_type = "0";
    private String into_flag;
    private String inout_str;
    private String cardno_len;//考勤卡长度
    private String sp_flag;//审批标志
    private String sp_action="";
    private RecordVo view_vo=new RecordVo("kq_originality_data");;
    private RecordVo machine=new RecordVo("kq_machine_location");
    private String machine_data;
    private String datafrom;
    private String isInout_flag;
    /**这三个指标是用来判断是否隐藏**/
    private String a0101zx;
    private String b0110zx;
    private String e0122zx;
    private String kqj;
    private String returnvalue="1";
    private String sync_carddata="";
    private String viewPost;
    private String uplevel = "";
    private String view;//主页/我的任务/待审批刷卡数据 请求连接的参数，用于判断刷卡数据显示内容的范围
    
    private String return_start_date;
    private String return_end_date;
    
    private String iscommon;
    private String signs;//考勤标志
    
	public String getReturn_start_date() {
		return return_start_date;
	}
	public void setReturn_start_date(String returnStartDate) {
		return_start_date = returnStartDate;
	}
	public String getReturn_end_date() {
		return return_end_date;
	}
	public void setReturn_end_date(String returnEndDate) {
		return_end_date = returnEndDate;
	}
	public String getView() {
		return view;
	}
	public void setView(String view) {
		this.view = view;
	}
	public String getIsInout_flag() {
		return isInout_flag;
	}
	public void setIsInout_flag(String isInout_flag) {
		this.isInout_flag = isInout_flag;
	}
	public String getDatafrom() {
		return datafrom;
	}
	public void setDatafrom(String datafrom) {
		this.datafrom = datafrom;
	}
	public String getMachine_data() {
		return machine_data;
	}
	public void setMachine_data(String machine_data) {
		this.machine_data = machine_data;
	}
	public String getCardno_len() {
		return cardno_len;
	}
	public void setCardno_len(String cardno_len) {
		this.cardno_len = cardno_len;
	}
	public String getInout_str() {
		return inout_str;
	}
	public void setInout_str(String inout_str) {
		this.inout_str = inout_str;
	}
	public ArrayList getKq_list() {
		return kq_list;
	}
	public void setKq_list(ArrayList kq_list) {
		this.kq_list = kq_list;
	}
	public String getSelect_flag() {
		return select_flag;
	}
	public void setSelect_flag(String select_flag) {
		this.select_flag = select_flag;
	}
	public String getSelect_name() {
		return select_name;
	}
	public void setSelect_name(String select_name) {
		this.select_name = select_name;
	}
	public String getSelect_pre() {
		return select_pre;
	}
	public void setSelect_pre(String select_pre) {
		this.select_pre = select_pre;
	}
	public String getOrderby() {
		return orderby;
	}
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	public String getMachine_date() {
		return machine_date;
	}
	public void setMachine_date(String machine_date) {
		this.machine_date = machine_date;
	}
	public String getMachine_hh() {
		return machine_hh;
	}
	public void setMachine_hh(String machine_hh) {
		this.machine_hh = machine_hh;
	}
	public String getMachine_mm() {
		return machine_mm;
	}
	public void setMachine_mm(String machine_mm) {
		this.machine_mm = machine_mm;
	}
	public ArrayList getFile_list() {
		return file_list;
	}
	public void setFile_list(ArrayList file_list) {
		this.file_list = file_list;
	}
	public String getFile_num() {
		return file_num;
	}
	public void setFile_num(String file_num) {
		this.file_num = file_num;
	}
	public String getMachine_num() {
		return machine_num;
	}
	public void setMachine_num(String machine_num) {
		this.machine_num = machine_num;
	}
	public ArrayList getMachinelist() {
		return machinelist;
	}
	public void setMachinelist(ArrayList machinelist) {
		this.machinelist = machinelist;
	}
	public String getCur_flag() {
		return cur_flag;
	}
	public void setCur_flag(String cur_flag) {
		this.cur_flag = cur_flag;
	}
	public ArrayList getFielditemlist() {
		return fielditemlist;
	}
	public void setFielditemlist(ArrayList fielditemlist) {
		this.fielditemlist = fielditemlist;
	}
	public String getWorkcalendar() {
		return workcalendar;
	}
	public void setWorkcalendar(String workcalendar) {
		this.workcalendar = workcalendar;
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
	@Override
    public void outPutFormHM()
	{
		this.setSigns((String)this.getFormHM().get("signs"));
		this.setTreeCode((String)this.getFormHM().get("treeCode"));
		this.setCur_date((String)this.getFormHM().get("cur_date"));
		this.setCur_session((String)this.getFormHM().get("cur_session"));
		this.setSessionlist((ArrayList)this.getFormHM().get("sessionlist"));
		this.setDatelist((ArrayList)this.getFormHM().get("datelist"));
		/***error message***/
		this.setError_flag((String)this.getFormHM().get("error_flag"));
		this.setError_message((String)this.getFormHM().get("error_message"));
		this.setError_return((String)this.getFormHM().get("error_return"));
		this.setError_stuts((String)this.getFormHM().get("error_stuts"));
		
		this.setCode_kind((String)this.getFormHM().get("code_kind"));
		this.setKq_dbase_list((ArrayList)this.getFormHM().get("kq_dbase_list"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setFielditemlist((ArrayList)this.getFormHM().get("fielditemlist"));
		this.setWorkcalendar((String)this.getFormHM().get("workcalendar"));
		this.setColumn((String)this.getFormHM().get("column"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setMachinelist((ArrayList)this.getFormHM().get("machinelist"));
		this.setMachine_num((String)this.getFormHM().get("machine_num"));
		this.setFile_list((ArrayList)this.getFormHM().get("file_list"));
		this.setFile_num((String)this.getFormHM().get("file_num"));
	    this.setMachine_date((String)this.getFormHM().get("machine_date"));
	    this.setMachine_hh((String)this.getFormHM().get("machine_hh"));
	    this.setMachine_mm((String)this.getFormHM().get("machine_mm"));
	    /***过滤文件的***/
	    this.setFilter_date_s((String)this.getFormHM().get("filter_date_s"));
	    this.setFilter_date_e((String)this.getFormHM().get("filter_date_e"));
	    this.setFilter_hh_s((String)this.getFormHM().get("filter_hh_s"));
	    this.setFilter_hh_e((String)this.getFormHM().get("filter_hh_e"));
	    this.setFilter_mm_s((String)this.getFormHM().get("filter_mm_s"));
	    this.setFilter_mm_e((String)this.getFormHM().get("filter_mm_e"));
	    this.setFilter_card((String)this.getFormHM().get("filter_card"));
	    this.setOrderby((String)this.getFormHM().get("orderby"));
	    /**检索***/
	    this.setStart_date((String)this.getFormHM().get("start_date"));
	    this.setStart_hh((String)this.getFormHM().get("start_hh"));
	    this.setStart_mm((String)this.getFormHM().get("start_mm"));
	    this.setEnd_date((String)this.getFormHM().get("end_date"));
	    this.setEnd_hh((String)this.getFormHM().get("end_hh"));
	    this.setEnd_mm((String)this.getFormHM().get("end_mm"));
	    this.setKq_list((ArrayList)this.getFormHM().get("kq_list"));
	    this.setSelect_name((String)this.getFormHM().get("select_name"));
	    this.setSelect_flag((String)this.getFormHM().get("select_flag"));
	    this.setSelect_pre((String)this.getFormHM().get("select_pre"));
	    this.setInout_str((String)this.getFormHM().get("inout_str"));
	    this.setCardno_len((String)this.getFormHM().get("cardno_len"));	  
	    this.setView_vo((RecordVo)this.getFormHM().get("view_vo"));
	    this.setSp_flag((String)this.getFormHM().get("sp_flag"));
	    this.setMachine((RecordVo)this.getFormHM().get("machine"));
	    this.setIsInout_flag((String)this.getFormHM().get("isInout_flag"));
	    this.setA0101zx((String)this.getFormHM().get("a0101zx"));
	    this.setB0110zx((String)this.getFormHM().get("b0110zx"));
	    this.setE0122zx((String)this.getFormHM().get("e0122zx"));
	    this.setKqj((String)this.getFormHM().get("kqj"));
	    this.setSp_action((String)this.getFormHM().get("sp_action"));
	    this.setSync_carddata((String)this.getFormHM().get("sync_carddata"));//同步刷卡数据
	    this.setViewPost((String)this.getFormHM().get("viewPost"));
	    this.setSelect_type((String)this.getFormHM().get("select_type"));
	    this.setUplevel((String)this.getFormHM().get("uplevel"));
	    this.setView((String)this.getFormHM().get("view"));
	    this.setReturn_start_date((String)this.getFormHM().get("return_start_date"));
	    this.setReturn_end_date((String)this.getFormHM().get("return_end_date"));
	    this.setInto_flag((String)this.getFormHM().get("into_flag"));
	    this.setIscommon((String)this.getFormHM().get("iscommon"));
	    
	}
	@Override
    public void inPutTransHM()
    {
		this.getFormHM().put("cur_session",this.getCur_session());
		this.getFormHM().put("cur_date",this.getCur_date());
		this.getFormHM().put("a_code",this.getA_code());
		this.getFormHM().put("code_kind",this.getCode_kind());
		this.getFormHM().put("cur_flag",this.getCur_flag());
		/***error message***/
		this.getFormHM().put("error_stuts",this.getError_stuts());
		this.getFormHM().put("error_flag",this.getError_flag());
		this.getFormHM().put("error_return",this.getError_return());
		this.getFormHM().put("kq_dbase_list",this.getKq_dbase_list());
		this.getFormHM().put("a0100",this.getA0100());
		this.getFormHM().put("nbase",this.getNbase());
		this.getFormHM().put("machine_num",this.getMachine_num());
		this.getFormHM().put("file_num",this.getFile_num());
		this.getFormHM().put("file_url",this.getFile_url());
		this.getFormHM().put("file", file);
		this.getFormHM().put("filter_date_s",this.getFilter_date_s());
		this.getFormHM().put("filter_date_e",this.getFilter_date_e());
		this.getFormHM().put("filter_hh_s",this.getFilter_hh_s());
		this.getFormHM().put("filter_hh_e",this.getFilter_hh_e());
		this.getFormHM().put("filter_mm_s",this.getFilter_mm_s());
		this.getFormHM().put("filter_mm_e",this.getFilter_mm_e());
		this.getFormHM().put("filter_card",this.getFilter_card());
		if(this.getPagination()!=null)			
			   this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
		this.getFormHM().put("start_date",this.getStart_date());
		this.getFormHM().put("start_hh",this.getStart_hh());
		this.getFormHM().put("start_mm",this.getStart_mm());
		this.getFormHM().put("end_date",this.getEnd_date());
		this.getFormHM().put("end_hh",this.getEnd_hh());
		this.getFormHM().put("end_mm",this.getEnd_mm());
		this.getFormHM().put("select_name",this.getSelect_name());
	    this.getFormHM().put("select_flag",this.getSelect_flag());
	    this.getFormHM().put("select_pre",this.getSelect_pre());	 
	    this.getFormHM().put("sp_flag",this.getSp_flag());
	    this.getFormHM().put("into_flag", this.getInto_flag());
	    this.getFormHM().put("iscommon", this.getIscommon());//签到点信息
	    this.getFormHM().put("machine_data", this.getMachine_data());
	    this.getFormHM().put("datafrom", this.getDatafrom());
	    this.getFormHM().put("sp_action", this.sp_action);
	    this.getFormHM().put("sync_carddata",this.getSync_carddata());//同步刷卡数据
	    this.getFormHM().put("select_type",this.getSelect_type());
	    this.getFormHM().put("view", this.getView());
	    this.getFormHM().put("return_start_date", this.getReturn_start_date());
	    this.getFormHM().put("return_end_date", this.getReturn_end_date());
	    this.getFormHM().put("signs", this.getSigns());//标志
    }
	
	public String getA_code() {
		return a_code;
	}
	public void setA_code(String a_code) {
		this.a_code = a_code;
	}
	public String getCur_session() {
		return cur_session;
	}
	public void setCur_session(String cur_session) {
		this.cur_session = cur_session;
	}
	public ArrayList getDatelist() {
		return datelist;
	}
	public void setDatelist(ArrayList datelist) {
		this.datelist = datelist;
	}
	public ArrayList getSessionlist() {
		return sessionlist;
	}
	public void setSessionlist(ArrayList sessionlist) {
		this.sessionlist = sessionlist;
	}
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
		if("/kq/machine/search_card".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	        this.setEnd_date("");
	        this.setEnd_hh("");
	        this.setEnd_mm("");
	        this.setStart_date("");
	        this.setStart_hh("");
	        this.setStart_mm("");
	        this.getFormHM().clear();	 
	        this.setA_code("");
	        this.getFormHM().put("a_code","");
	    }
		if("/kq/machine/search_card_data".equals(arg0.getPath())&&arg1.getParameter("b_filtrate")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?	       
	    }
		if("/kq/machine/search_card_data".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?	       
	    }


		if("/kq/machine/historical/search_card".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	    {
	        if(this.getPagination()!=null)
	         this.getPagination().firstPage();//?
	        this.setEnd_date("");
	        this.setEnd_hh("");
	        this.setEnd_mm("");
	        this.setStart_date("");
	        this.setStart_hh("");
	        this.setStart_mm("");
	        this.getFormHM().clear();	 
	        this.setA_code("");
	        this.getFormHM().put("a_code","");
	    }		 			
			if("/kq/machine/historical/search_card_data".equals(arg0.getPath())&&arg1.getParameter("b_filtrate")!=null)
		    {
		        if(this.getPagination()!=null)
		          this.getPagination().firstPage();//?	       
		    }
			if("/kq/machine/historical/search_card_data".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
		    {
		        if(this.getPagination()!=null)
		          this.getPagination().firstPage();//?	       
		    }
			 return super.validate(arg0, arg1);
 
	}
	public String getTreeCode() {
		return treeCode;
	}
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	public String getCur_date() {
		return cur_date;
	}
	public void setCur_date(String cur_date) {
		this.cur_date = cur_date;
	}
	public String getError_flag() {
		return error_flag;
	}
	public void setError_flag(String error_flag) {
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
	public String getCode_kind() {
		return code_kind;
	}
	public void setCode_kind(String code_kind) {
		this.code_kind = code_kind;
	}
	public ArrayList getKq_dbase_list() {
		return kq_dbase_list;
	}
	public void setKq_dbase_list(ArrayList kq_dbase_list) {
		this.kq_dbase_list = kq_dbase_list;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getSqlstr() {
		return sqlstr;
	}
	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}
	public String getFile_url() {
		return file_url;
	}
	public void setFile_url(String file_url) {
		this.file_url = file_url;
	}
	public FormFile getFile() {
		return file;
	}
	public void setFile(FormFile file) {
		this.file = file;
	}
	public String getFilter_card() {
		return filter_card;
	}
	public void setFilter_card(String filter_card) {
		this.filter_card = filter_card;
	}
	public String getFilter_date_e() {
		return filter_date_e;
	}
	public void setFilter_date_e(String filter_date_e) {
		this.filter_date_e = filter_date_e;
	}
	public String getFilter_date_s() {
		return filter_date_s;
	}
	public void setFilter_date_s(String filter_date_s) {
		this.filter_date_s = filter_date_s;
	}
	public String getFilter_hh_e() {
		return filter_hh_e;
	}
	public void setFilter_hh_e(String filter_hh_e) {
		this.filter_hh_e = filter_hh_e;
	}
	public String getFilter_hh_s() {
		return filter_hh_s;
	}
	public void setFilter_hh_s(String filter_hh_s) {
		this.filter_hh_s = filter_hh_s;
	}
	public String getFilter_mm_e() {
		return filter_mm_e;
	}
	public void setFilter_mm_e(String filter_mm_e) {
		this.filter_mm_e = filter_mm_e;
	}
	public String getFilter_mm_s() {
		return filter_mm_s;
	}
	public void setFilter_mm_s(String filter_mm_s) {
		this.filter_mm_s = filter_mm_s;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getEnd_hh() {
		return end_hh;
	}
	public void setEnd_hh(String end_hh) {
		this.end_hh = end_hh;
	}
	public String getEnd_mm() {
		return end_mm;
	}
	public void setEnd_mm(String end_mm) {
		this.end_mm = end_mm;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getStart_hh() {
		return start_hh;
	}
	public void setStart_hh(String start_hh) {
		this.start_hh = start_hh;
	}
	public String getStart_mm() {
		return start_mm;
	}
	public void setStart_mm(String start_mm) {
		this.start_mm = start_mm;
	}
	public String getSp_flag() {
		return sp_flag;
	}
	public void setSp_flag(String sp_flag) {
		this.sp_flag = sp_flag;
	}
	public RecordVo getView_vo() {
		return view_vo;
	}
	public void setView_vo(RecordVo view_vo) {
		this.view_vo = view_vo;
	}
	public RecordVo getMachine() {
		return machine;
	}
	public void setMachine(RecordVo machine) {
		this.machine = machine;
	}
	public String getInto_flag() {
		return into_flag;
	}
	public void setInto_flag(String into_flag) {
		this.into_flag = into_flag;
	}
	public String getA0101zx() {
		return a0101zx;
	}
	public void setA0101zx(String a0101zx) {
		this.a0101zx = a0101zx;
	}
	public String getB0110zx() {
		return b0110zx;
	}
	public void setB0110zx(String b0110zx) {
		this.b0110zx = b0110zx;
	}
	public String getE0122zx() {
		return e0122zx;
	}
	public void setE0122zx(String e0122zx) {
		this.e0122zx = e0122zx;
	}
	public String getKqj() {
		return kqj;
	}
	public void setKqj(String kqj) {
		this.kqj = kqj;
	}
	public String getSp_action() {
		return sp_action;
	}
	public void setSp_action(String sp_action) {
		this.sp_action = sp_action;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	public String getSync_carddata() {
		return sync_carddata;
	}
	public void setSync_carddata(String sync_carddata) {
		this.sync_carddata = sync_carddata;
	}
	public String getViewPost() {
		return viewPost;
	}
	public void setViewPost(String viewPost) {
		this.viewPost = viewPost;
	}
	public String getSelect_type() {
		return select_type;
	}
	public void setSelect_type(String select_type) {
		this.select_type = select_type;
	}
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
	public void setIscommon(String iscommon) {
		this.iscommon = iscommon;
	}
	public String getIscommon() {
		return iscommon;
	}
	public void setSigns(String signs) {
		this.signs = signs;
	}
	public String getSigns() {
		return signs;
	}
	
}
