package com.hjsj.hrms.actionform.kq.net_signin;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
/**
 * 网上签到、签退
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 30, 2007:3:53:26 PM</p> 
 *@author dengcan
 *@version 4.0
 */
public class BaseNetSignInForm extends FrameForm 
{
	private String sql_self;
	private String column_self;
	private String order_self;
	private String where_self;
	private String start_date;
	private String end_date;
	private String treeCode;//树形菜单，在HtmlMenu中
	private String code;//连接级别
    private String kind;
	private String sql_str;
	private String column_str;
	private String where_str;
	private String order_str;
	private String singin_flag;
	private String makeup_date;
	private String makeup_time;
	private String cardno;
	private ArrayList fieldlist=new ArrayList();
	private ArrayList locatlist=new ArrayList();
	private String inout_flag;
	private String oper_cause;
	private String location;
	private String select_name;
	private String select_flag;
	private String card_causation;
	private String isInout_flag;
	private String net_sign_check_ip;
	private String str_app;//补刷卡审批人
	private ArrayList app_e0122;
	private ArrayList app_a0101;
	private ArrayList app_account;//审批人账号
	private String kqempcal;//我的考勤日历参数
	
	public ArrayList getApp_account() {
		return app_account;
	}
	public void setApp_account(ArrayList app_account) {
		this.app_account = app_account;
	}
	public ArrayList getApp_e0122() {
		return app_e0122;
	}
	public void setApp_e0122(ArrayList app_e0122) {
		this.app_e0122 = app_e0122;
	}
	public ArrayList getApp_a0101() {
		return app_a0101;
	}
	public void setApp_a0101(ArrayList app_a0101) {
		this.app_a0101 = app_a0101;
	}
	public String getStr_app() {
		return str_app;
	}
	public void setStr_app(String str_app) {
		this.str_app = str_app;
	}
	public String getIsInout_flag() {
		return isInout_flag;
	}
	public void setIsInout_flag(String isInout_flag) {
		this.isInout_flag = isInout_flag;
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
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public ArrayList getLocatlist() {
		ArrayList list=new ArrayList();
		CommonData da=new CommonData();
		da.setDataName("全部");
		da.setDataValue("all");
		list.add(da);
		da=new CommonData();
		da.setDataName("签到");
		da.setDataValue("签到");
		list.add(da);
		da=new CommonData();
		da.setDataName("签退");
		da.setDataValue("签退");
		list.add(da);
		da=new CommonData();
		da.setDataName("补签到");
		da.setDataValue("补签到");
		list.add(da);
		da=new CommonData();
		da.setDataName("补签退");
		da.setDataValue("补签退");
		list.add(da);
		return list;
	}
	public void setLocatlist(ArrayList locatlist) {		
		this.locatlist = locatlist;
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public String getCardno() {
		return cardno;
	}
	public void setCardno(String cardno) {
		this.cardno = cardno;
	}
	public String getOrder_self() {
		return order_self;
	}
	public void setOrder_self(String order_self) {
		this.order_self = order_self;
	}
	@Override
    public void outPutFormHM()
	{
		this.setCardno((String)this.getFormHM().get("cardno"));
		this.setSql_self((String)this.getFormHM().get("sql_self"));
		this.setColumn_self((String)this.getFormHM().get("column_self"));
		this.setWhere_self((String)this.getFormHM().get("where_self"));
		this.setOrder_self((String)this.getFormHM().get("order_self"));
		this.setSingin_flag((String)this.getFormHM().get("singin_flag"));
		this.setMakeup_date((String)this.getFormHM().get("makeup_date"));
		this.setTreeCode((String)this.getFormHM().get("treeCode"));	
		this.setSql_str((String)this.getFormHM().get("sql_str"));
		this.setWhere_str((String)this.getFormHM().get("where_str"));
		this.setColumn_str((String)this.getFormHM().get("column_str"));
		this.setOrder_str((String)this.getFormHM().get("order_str"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setLocation((String)this.getFormHM().get("location"));
		this.setSelect_flag((String)this.getFormHM().get("select_flag"));
		this.setSelect_name((String)this.getFormHM().get("select_name"));
		this.setStart_date((String)this.getFormHM().get("start_date"));
		this.setEnd_date((String)this.getFormHM().get("end_date"));
		this.setInout_flag((String)this.getFormHM().get("inout_flag"));
		this.setOper_cause((String)this.getFormHM().get("oper_cause"));
		this.setCard_causation((String)this.getFormHM().get("card_causation"));
		this.setIsInout_flag((String)this.getFormHM().get("isInout_flag"));
		this.setNet_sign_check_ip((String)this.getFormHM().get("net_sign_check_ip"));
		this.setStr_app((String)this.getFormHM().get("str_app"));
		this.setApp_a0101((ArrayList)this.getFormHM().get("app_a0101"));
		this.setApp_e0122((ArrayList)this.getFormHM().get("app_e0122"));
		this.setApp_account((ArrayList)this.getFormHM().get("app_account"));
		this.setKqempcal((String)this.getFormHM().get("kqempcal"));
		this.setMakeup_time((String)this.getFormHM().get("makeup_time"));
	}
	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("start_date",start_date);
		this.getFormHM().put("end_date",this.getEnd_date());
		this.getFormHM().put("makeup_date",this.getMakeup_date());
		this.getFormHM().put("makeup_time",this.getMakeup_time());
		this.getFormHM().put("singin_flag",this.getSingin_flag());
		this.getFormHM().put("code",code);
	    this.getFormHM().put("kind",kind);
	    this.getFormHM().put("location",this.getLocation());
	    this.getFormHM().put("select_name",this.getSelect_name());
	    this.getFormHM().put("select_flag",this.getSelect_flag());
	    if(this.getPagination()!=null)			
			 this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
	    this.getFormHM().put("oper_cause",this.oper_cause);
	    this.getFormHM().put("inout_flag",inout_flag);
	    this.getFormHM().put("card_causation", this.getCard_causation());
	    this.getFormHM().put("str_app", this.getStr_app());
	    this.getFormHM().put("app_e0122", this.getApp_e0122());
	    this.getFormHM().put("app_a0101", this.getApp_a0101());
	    this.getFormHM().put("app_account", this.getApp_account());
	    this.getFormHM().put("kqempcal", this.getKqempcal());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	    if("/kq/kqself/net_signin/net_signin".equals(arg0.getPath())&&arg1.getParameter("b_self")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	    }
	    if("/kq/kqself/net_signin/allnet_signin".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	    }
	    if("/kq/kqself/net_signin/allnet_signin_date".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	    {
	        if(this.getPagination()!=null)
	          this.getPagination().firstPage();//?
	    }
	    if("/kq/kqself/card/carddata".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
	    {
	    	if(!"init".equalsIgnoreCase(arg1.getParameter("b_query")))
	    	{
	    		if(this.getPagination()!=null)
	    			this.getPagination().firstPage();
	    	}
	    }
	    return super.validate(arg0, arg1);
	}	
	public String getColumn_self() {
		return column_self;
	}
	public void setColumn_self(String column_self) {
		this.column_self = column_self;
	}
	public String getColumn_str() {
		return column_str;
	}
	public void setColumn_str(String column_str) {
		this.column_str = column_str;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getMakeup_date() {
		return makeup_date;
	}
	public void setMakeup_date(String makeup_date) {
		this.makeup_date = makeup_date;
	}
	public String getMakeup_time() {
		return makeup_time;
	}
	public void setMakeup_time(String makeup_time) {
		this.makeup_time = makeup_time;
	}
	public String getSingin_flag() {
		return singin_flag;
	}
	public void setSingin_flag(String singin_flag) {
		this.singin_flag = singin_flag;
	}
	public String getSql_self() {
		return sql_self;
	}
	public void setSql_self(String sql_self) {
		this.sql_self = sql_self;
	}
	public String getSql_str() {
		return sql_str;
	}
	public void setSql_str(String sql_str) {
		this.sql_str = sql_str;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getWhere_self() {
		return where_self;
	}
	public void setWhere_self(String where_self) {
		this.where_self = where_self;
	}
	public String getWhere_str() {
		return where_str;
	}
	public void setWhere_str(String where_str) {
		this.where_str = where_str;
	}
	public String getOrder_str() {
		return order_str;
	}
	public void setOrder_str(String order_str) {
		this.order_str = order_str;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getTreeCode() {
		return treeCode;
	}
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	public String getInout_flag() {
		return inout_flag;
	}
	public void setInout_flag(String inout_flag) {
		this.inout_flag = inout_flag;
	}
	public String getOper_cause() {
		return oper_cause;
	}
	public void setOper_cause(String oper_cause) {
		this.oper_cause = oper_cause;
	}
	public String getCard_causation() {
		return card_causation;
	}
	public void setCard_causation(String card_causation) {
		this.card_causation = card_causation;
	}
	public String getNet_sign_check_ip() {
		return net_sign_check_ip;
	}
	public void setNet_sign_check_ip(String net_sign_check_ip) {
		this.net_sign_check_ip = net_sign_check_ip;
	}
	public void setKqempcal(String kqempcal) {
		this.kqempcal = kqempcal;
	}
	public String getKqempcal() {
		return kqempcal;
	}
}

