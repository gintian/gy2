package com.hjsj.hrms.actionform.kq.feast_manage;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class FeastForm extends FrameForm 
{
   private String infor_Flag="1";
   private String treeCode;//树形菜单，在HtmlMenu中
   private String content;	
   private String strsql;
   private String columns;
   private String orderby;
   private String code;
   private String kind;
   private ArrayList fieldlist=new ArrayList();
   private ArrayList yearlist=new ArrayList();
   private String kq_year;
   private String c_expr;  
   private String sige;
   private String sigh;
   private String errormsg; 
   private String feast_start;
   private String feast_end;
   private String expr_flag;
   private ArrayList setlist=new ArrayList();
   private ArrayList onefiledlist=new ArrayList();
   private String setname;
   private String left_fields;
   private String relatTableid;//高级花名册对应的单表名称
   private String condition;//高级花名册打印的条件
   private String returnURL;//返回的连接
   private PaginationForm feastForm=new PaginationForm();   
   private String error_message;
   private String error_flag;
   private String error_return;
   private String error_flag_session;
   private String error_message_session;
   private String error_flag_nbase;
   private String error_message_nbase;
   private int current=1;
   //用来标识假期类型
   private String hols_status;
   private String hols_name;
   private String fieldItems;
   private ArrayList holi_list=new ArrayList();
   private ArrayList vo_list=new ArrayList();  
   private String whereIN;
   private String dbpre;
   private ArrayList dblist=new ArrayList();
   private String count_fields;
   private String strsql_encode;
   private String clear_zone="1";//自动清除0天休假人员
   private PaginationForm recordListForm=new PaginationForm();  
   private String returnvalue="1";
   //查询
   private ArrayList selectfieldlist=new ArrayList();
	 /**关系操作符*/
   private ArrayList operlist=new ArrayList();
   /**逻辑操作符*/
   private ArrayList logiclist=new ArrayList(); 
  
   private ArrayList selectedlist = new ArrayList();
   /**factor list*/
   private ArrayList factorlist=new ArrayList(); 
   private String like; 
   /**选中的字段名数组*/
   private String right_fields[];  
   /**能用查询的表达式:!(1+2*3),!非，＋或，*且*/
   private String select_flag;  
   private ArrayList kq_list=new ArrayList();//人员库
   private String select_name;//筛选名字
   private String select_sturt;//筛选表示
   private String select_pre;
   private String exp_field;
   private ArrayList exp_fieldlist=new ArrayList();
   private String exp_fields[];
   private String uplevel;  //显示层级
   private String balance; //上年结余
   private String balanceEnd;
   // 是否存在上年结余字段
   private String existBalance;
   // 是否存在上年结余字段
   private String existBalanceEnd;
   // 上年结余字段名称
   private String balanceName;
   
   private String isshow;
   //@author yhj 20130602
   //导入年假模板数据
   private FormFile file;
   //记录导入年假记录的信息
   private String importMsg="";
   
   
  
    public String getIsshow() {
	    return isshow;
	}
	public void setIsshow(String isshow) {
		this.isshow = isshow;
	}
	public String getBalanceName() {
		return balanceName;
	}
	public void setBalanceName(String balanceName) {
		this.balanceName = balanceName;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getExp_field() {
		return exp_field;
	}
	public void setExp_field(String exp_field) {
		this.exp_field = exp_field;
	}
	public ArrayList getExp_fieldlist() {
		return exp_fieldlist;
	}
	public void setExp_fieldlist(ArrayList exp_fieldlist) {
		this.exp_fieldlist = exp_fieldlist;
	}
	public String getSelect_flag() {
		return select_flag;
	}
	public void setSelect_flag(String select_flag) {
		this.select_flag = select_flag;
	}
	public ArrayList getFactorlist() {
		return factorlist;
	}
	public void setFactorlist(ArrayList factorlist) {
		this.factorlist = factorlist;
	}
	public String getLike() {
		return like;
	}
	public void setLike(String like) {
		this.like = like;
	}
	public ArrayList getLogiclist() {
		return logiclist;
	}
	public void setLogiclist(ArrayList logiclist) {
		this.logiclist = logiclist;
	}
	public ArrayList getOperlist() {
		return operlist;
	}
	public void setOperlist(ArrayList operlist) {
		this.operlist = operlist;
	}
	public ArrayList getSelectedlist() {
		return selectedlist;
	}
	public void setSelectedlist(ArrayList selectedlist) {
		if(selectedlist==null)
			selectedlist=new ArrayList();
		this.selectedlist = selectedlist;
		this.selectedlist = selectedlist;
	}
	public ArrayList getSelectfieldlist() {
		return selectfieldlist;
	}
	public void setSelectfieldlist(ArrayList selectfieldlist) {
		this.selectfieldlist = selectfieldlist;
	}
	/**权限范围内的人员库*/
	public PaginationForm getRecordListForm() {
		return recordListForm;
	}
	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}
	public ArrayList getVo_list() {
		return vo_list;
	}
	public void setVo_list(ArrayList vo_list) {
		this.vo_list = vo_list;
	}
	public ArrayList getHoli_list() {
		return holi_list;
	}
	public void setHoli_list(ArrayList holi_list) {
		this.holi_list = holi_list;
	}
	public String getHols_status() {
		return hols_status;
	}
	public void setHols_status(String hols_status) {
		this.hols_status = hols_status;
	}
	public String getSetname() {
		return setname;
	}
	public void setSetname(String setname) {
		this.setname = setname;
	}
	public String getC_expr() {
		return c_expr;
	}
	public void setC_expr(String c_expr) {
		this.c_expr = c_expr;
	}
	public String getKq_year() {
		return kq_year;
	}
	public void setKq_year(String kq_year) {
		this.kq_year = kq_year;
	}
	@Override
    public void outPutFormHM()
    {
	   this.getFeastForm().getPagination().gotoPage(current);
	   this.setTreeCode((String)this.getFormHM().get("treeCode"));
	   this.setContent((String)this.getFormHM().get("content"));
	   this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
	   this.setYearlist((ArrayList)this.getFormHM().get("yearlist"));
	   this.setKq_year((String)this.getFormHM().get("kq_year"));
	   this.setStrsql((String)this.getFormHM().get("strsql"));
	   this.setColumns((String)this.getFormHM().get("columns"));
	   this.setOrderby((String)this.getFormHM().get("orderby"));
	   this.setKind((String)this.getFormHM().get("kind"));
	   this.setCode((String)this.getFormHM().get("code"));
	   this.setC_expr((String)this.getFormHM().get("c_expr"));	  
	   this.setSige((String)this.getFormHM().get("sige"));
	   this.setSigh((String)this.getFormHM().get("sigh"));
	   this.setErrormsg((String)this.getFormHM().get("errormsg"));
	   this.setFeast_start((String)this.getFormHM().get("feast_start"));
	   this.setFeast_end((String)this.getFormHM().get("feast_end"));
	   this.setExpr_flag((String)this.getFormHM().get("expr_flag"));
	   this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
	   this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
	   this.setOnefiledlist((ArrayList)this.getFormHM().get("onefiledlist"));
	   this.setRelatTableid((String)this.getFormHM().get("relatTableid"));
	   this.setCondition((String)this.getFormHM().get("condition"));
	   this.setReturnURL((String)this.getFormHM().get("returnURL"));
	   this.setError_flag((String)this.getFormHM().get("error_flag"));
	   this.setError_flag_session((String)this.getFormHM().get("error_flag_session"));
	   this.setError_flag_nbase((String)this.getFormHM().get("error_flag_nbase"));
	   this.setError_message((String)this.getFormHM().get("error_message"));
	   this.setError_message_session((String)this.getFormHM().get("error_message_session"));
	   this.setError_message_nbase((String)this.getFormHM().get("error_message_nbase"));
	   this.setError_return((String)this.getFormHM().get("error_return"));
	   this.setHols_status((String)this.getFormHM().get("hols_status"));
	   this.setHoli_list((ArrayList)this.getFormHM().get("holi_list"));
	   this.setHols_name((String)this.getFormHM().get("hols_name"));
	   this.setVo_list((ArrayList)this.getFormHM().get("vo_list"));
	   this.getRecordListForm().setList((ArrayList)this.getFormHM().get("vo_list"));
       this.setFieldItems((String)this.getFormHM().get("fieldItems"));
       this.setSelectfieldlist((ArrayList)this.getFormHM().get("selectfieldlist"));
	   this.setSelectedlist((ArrayList)this.getFormHM().get("selectedlist"));
	   this.setFactorlist((ArrayList)this.getFormHM().get("factorlist"));
	   this.setOperlist((ArrayList)this.getFormHM().get("operlis"));
	   this.setLogiclist((ArrayList)this.getFormHM().get("logiclist"));
	   this.setWhereIN((String)this.getFormHM().get("whereIN"));
	   this.setSelect_flag((String)this.getFormHM().get("select_flag"));
	   this.setDbpre((String)this.getFormHM().get("dbpre"));
	   this.setDblist((ArrayList)this.getFormHM().get("dblist"));
	   this.setKq_list((ArrayList)this.getFormHM().get("kq_list"));
       this.setSelect_name((String)this.getFormHM().get("select_name"));
       this.setSelect_sturt((String)this.getFormHM().get("select_sturt"));
       this.setSelect_pre((String)this.getFormHM().get("select_pre"));
       this.setExp_field((String)this.getFormHM().get("exp_field"));
       this.setExp_fieldlist((ArrayList)this.getFormHM().get("exp_fieldlist"));
       this.setStrsql_encode((String)this.getFormHM().get("strsql_encode"));
       this.setUplevel((String)this.getFormHM().get("uplevel"));
       this.setExistBalance((String) this.getFormHM().get("existBalance"));
       this.setBalanceName((String) this.getFormHM().get("balanceName"));
       this.setExistBalanceEnd((String) this.getFormHM().get("existBalanceEnd"));
       this.setIsshow((String)this.getFormHM().get("isshow"));
       this.setImportMsg((String)this.getFormHM().get("importMsg"));
   }
   @Override
   public void inPutTransHM()
   {
	   this.getFormHM().put("file", this.getFile());
	   this.getFormHM().put("c_expr",PubFunc.keyWord_reback(this.getC_expr()));
	   this.getFormHM().put("content",this.getContent());
	   this.getFormHM().put("code",this.getCode());
	   this.getFormHM().put("feast_start",this.getFeast_start());
	   this.getFormHM().put("feast_end",this.getFeast_end());
	   this.getFormHM().put("kq_year",this.getKq_year());
	   if(this.getPagination()!=null)        
	          this.getFormHM().put("list",this.getPagination().getAllList());
	   if(this.getPagination()!=null)			
		   this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
	   this.getFormHM().put("hols_status",this.getHols_status());
	   this.getFormHM().put("holi_list",this.getHoli_list());
	   this.getFormHM().put("hols_name",this.getHols_name());
	   this.getFormHM().put("right_fields",this.getRight_fields());
	   this.getFormHM().put("like",this.getLike());
	   this.getFormHM().put("whereIN",this.getWhereIN());
	   this.getFormHM().put("select_flag",this.getSelect_flag());
	   this.getFormHM().put("dbpre",this.getDbpre());
	   this.getFormHM().put("select_name",this.getSelect_name());
       this.getFormHM().put("select_sturt",this.getSelect_sturt());
       this.getFormHM().put("select_pre",this.getSelect_pre());
       this.getFormHM().put("exp_field",this.getExp_field());
       this.getFormHM().put("count_fields",this.getCount_fields());
       this.getFormHM().put("clear_zone", this.getClear_zone());
       this.getFormHM().put("isshow", this.getIsshow());
   }
   
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getStrsql() {
		return strsql;
	}
	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}
	public ArrayList getYearlist() {
		return yearlist;
	}
	public void setYearlist(ArrayList yearlist) {
		this.yearlist = yearlist;
	}
	public String getTreeCode() {
		return treeCode;
	}
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	public String getInfor_Flag() {
		return infor_Flag;
	}
	public void setInfor_Flag(String infor_Flag) {
		this.infor_Flag = infor_Flag;
	}
	public String getErrormsg() {
		return errormsg;
	}
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}
	public String getSige() {
		return sige;
	}
	public void setSige(String sige) {
		this.sige = sige;
	}
	public String getSigh() {
		return sigh;
	}
	public void setSigh(String sigh) {
		this.sigh = sigh;
	}
	public String getFeast_end() {
		return feast_end;
	}
	public void setFeast_end(String feast_end) {
		this.feast_end = feast_end;
	}
	public String getFeast_start() {
		return feast_start;
	}
	public void setFeast_start(String feast_start) {
		this.feast_start = feast_start;
	}
	public String getExpr_flag() {
		return expr_flag;
	}
	public void setExpr_flag(String expr_flag) {
		this.expr_flag = expr_flag;
	}	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	 
	    if("/kq/feast_manage/manager".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	    	 if(this.getPagination()!=null)
		          this.getPagination().firstPage();//?
	        this.getFormHM().clear();
	        this.setHols_status("");
	    }
	    if("/kq/feast_manage/managerdata".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	    	 if(this.getPagination()!=null)
		          this.getPagination().firstPage();//?
	    }
	    if("/kq/feast_manage/managerdata".equals(arg0.getPath())&&arg1.getParameter("b_exp")!=null)
	    {
	    	 this.setSigh("");
	    	 this.setSige("");
	    	 this.setErrormsg("");
	    	 this.getFormHM().put("sige","");
	    	 this.getFormHM().put("sigh","");
	    }
	    if("/kq/feast_manage/select/selectfiled".equals(arg0.getPath())&&arg1.getParameter("b_next")!=null)
	    {
	    	 this.setLike("");
	    	 this.getFormHM().put("like","");
	    }
	    return super.validate(arg0, arg1);
	}
	public ArrayList getOnefiledlist() {
		return onefiledlist;
	}
	public void setOnefiledlist(ArrayList onefiledlist) {
		this.onefiledlist = onefiledlist;
	}
	public ArrayList getSetlist() {
		return setlist;
	}
	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}
	public String getLeft_fields() {
		return left_fields;
	}
	public void setLeft_fields(String left_fields) {
		this.left_fields = left_fields;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getRelatTableid() {
		return relatTableid;
	}
	public void setRelatTableid(String relatTableid) {
		this.relatTableid = relatTableid;
	}
	public String getReturnURL() {
		return returnURL;
	}
	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}
	public PaginationForm getFeastForm() {
		return feastForm;
	}
	public void setFeastForm(PaginationForm feastForm) {
		this.feastForm = feastForm;
	}
	public String getError_flag() {
		return error_flag;
	}
	public void setError_flag(String error_flag) {
		if(error_flag==null||error_flag.length()<=0)
			error_flag="0";
		this.error_flag = error_flag;
	}
	
	public String getError_flag_session() {
		return error_flag_session;
	}
	public void setError_flag_session(String error_flag_session) {
		if(error_flag_session==null||error_flag_session.length()<=0)
			error_flag_session="0";
		this.error_flag_session = error_flag_session;
	}
	public String getError_flag_nbase() {
		return error_flag_nbase;
	}
	public void setError_flag_nbase(String error_flag_nbase) {
		if(error_flag_nbase==null||error_flag_nbase.length()<=0)
			error_flag_nbase="0";
		this.error_flag_nbase = error_flag_nbase;
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
	public String getHols_name() {
		return hols_name;
	}
	public void setHols_name(String hols_name) {
		this.hols_name = hols_name;
	}
	public String getFieldItems() {
		return fieldItems;
	}
	public void setFieldItems(String fieldItems) {
		this.fieldItems = fieldItems;
	}
	public String[] getRight_fields() {
		return right_fields;
	}
	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}
	public String getWhereIN() {
		return whereIN;
	}
	public void setWhereIN(String whereIN) {
		this.whereIN = whereIN;
	}
	public ArrayList getDblist() {
		return dblist;
	}
	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}
	public String getDbpre() {
		return dbpre;
	}
	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
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
	public String getSelect_pre() {
		return select_pre;
	}
	public void setSelect_pre(String select_pre) {
		this.select_pre = select_pre;
	}
	public String getSelect_sturt() {
		return select_sturt;
	}
	public void setSelect_sturt(String select_sturt) {
		this.select_sturt = select_sturt;
	}
	public String[] getExp_fields() {
		return exp_fields;
	}
	public void setExp_fields(String[] exp_fields) {
		this.exp_fields = exp_fields;
	}
	public String getCount_fields() {
		return count_fields;
	}
	public void setCount_fields(String count_fields) {
		this.count_fields = count_fields;
	}
	public String getStrsql_encode() {
		return strsql_encode;
	}
	public void setStrsql_encode(String strsql_encode) {
		this.strsql_encode = strsql_encode;
	}
	public String getClear_zone() {
		return clear_zone;
	}
	public void setClear_zone(String clear_zone) {
		this.clear_zone = clear_zone;
	}
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	public String getExistBalance() {
		return existBalance;
	}
	public void setExistBalance(String existBalance) {
		this.existBalance = existBalance;
	}
	public String getBalanceEnd() {
		return balanceEnd;
	}
	public void setBalanceEnd(String balanceEnd) {
		this.balanceEnd = balanceEnd;
	}
	public String getExistBalanceEnd() {
		return existBalanceEnd;
	}
	public void setExistBalanceEnd(String existBalanceEnd) {
		this.existBalanceEnd = existBalanceEnd;
	}
	public String getError_message_session() {
		return error_message_session;
	}
	public void setError_message_session(String error_message_session) {
		this.error_message_session = error_message_session;
	}
	public String getError_message_nbase() {
		return error_message_nbase;
	}
	public void setError_message_nbase(String error_message_nbase) {
		this.error_message_nbase = error_message_nbase;
	}
	public FormFile getFile() {
		return file;
	}
	public void setFile(FormFile file) {
		this.file = file;
	}
	public String getImportMsg() {
		return importMsg;
	}
	public void setImportMsg(String importMsg) {
		this.importMsg = importMsg;
	}
    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }
    public String getOrderby() {
        return orderby;
    }
	
}


