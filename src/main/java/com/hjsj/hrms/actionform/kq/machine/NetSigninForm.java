package com.hjsj.hrms.actionform.kq.machine;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class NetSigninForm extends FrameForm{

	private String code = "";
	private String kind = "";  
	private String registerdate = "";
	private ArrayList datelist=new ArrayList();
	private ArrayList kq_dbase_list=new ArrayList();
	private ArrayList kq_list=new ArrayList();
	private String select_pre="";
	private String select_flag="";
	private String select_name="";
	private String columns="";
	private String sqlstr="";
	private String ordeby="";
	private String kq_duration="";
	private String workcalendar="";
	private String treeCode="";
	private String issdao="";
	private String sdao_count_field="";
	private String start_date;  //网上签到 明细 开始时间
	private String end_date; //网上签到 明细 结束时间
	private String sql_self; //网上签到 明细
	private String column_self; //网上签到 明细
	private String where_self; //网上签到 明细
	private String order_self; //网上签到 明细
	private String isInout_flag; //网上签到 明细
	private String dbsign; //网上签到 明细 人员库前缀
	private String a0100sign; //网上签到 明细 人员编号
	private String makeup_time;
	private String inout_flag;
	private String oper_cause;
	private String singin_flag;
	private String makeup_date;
	private String card_causation;//补刷卡原因代码项
	private String sdb0110;
	private String sde0122;
	private String sda0101;
	private String uplevel;//显示部门级数
	private ArrayList fielditemlist=new ArrayList(); //上岛签到
	private ArrayList classlist=new ArrayList();//班次分类
	private String curclass="";//当前班次
	private ArrayList signinlist=new ArrayList();//签到分类
	private String cursignin="";//当前签到标签
	private String sdmakeup_date; //上岛补签时间
	private String sdjudge;
	
	private ArrayList fieldlist= new ArrayList();//主集班次
	private String classA01; //1=主集中有班组，0=没有班组
	private String cardnoName;//工号对应中文名称
	private String cardno;  //工号对应指标
	private String cardnoId;//1=展现身份证号，0=不展现
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

	public String getSingin_flag() {
		return singin_flag;
	}

	public void setSingin_flag(String singin_flag) {
		this.singin_flag = singin_flag;
	}

	public String getMakeup_date() {
		return makeup_date;
	}

	public void setMakeup_date(String makeup_date) {
		this.makeup_date = makeup_date;
	}

	public String getCard_causation() {
		return card_causation;
	}

	public void setCard_causation(String card_causation) {
		this.card_causation = card_causation;
	}

	public String getIssdao() {
		return issdao;
	}

	public void setIssdao(String issdao) {
		this.issdao = issdao;
	}

	public String getSdao_count_field() {
		return sdao_count_field;
	}

	public void setSdao_count_field(String sdao_count_field) {
		this.sdao_count_field = sdao_count_field;
	}

	public String getTreeCode() {
		return treeCode;
	}

	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}


	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("code", this.getCode());
		this.getFormHM().put("kind",this.getKind());
		this.getFormHM().put("registerdate", this.getRegisterdate());
		this.getFormHM().put("datelist", this.getDatelist());
		this.getFormHM().put("kq_dbase_list", this.getKq_dbase_list());
		this.getFormHM().put("select_pre", this.getSelect_pre());
		this.getFormHM().put("select_flag", this.getSelect_flag());
		this.getFormHM().put("select_name", this.getSelect_name());
		this.getFormHM().put("kq_duration", this.getKq_duration());
		this.getFormHM().put("sdao_count_field", this.getSdao_count_field());
		this.getFormHM().put("start_date",start_date);
		this.getFormHM().put("end_date",this.getEnd_date());
		this.getFormHM().put("dbsign",this.getDbsign());
		this.getFormHM().put("a0100sign",this.getA0100sign());
		
		this.getFormHM().put("inout_flag",inout_flag);
		this.getFormHM().put("card_causation", this.getCard_causation());
		this.getFormHM().put("makeup_date",this.getMakeup_date());
		this.getFormHM().put("makeup_time",this.getMakeup_time());
		this.getFormHM().put("singin_flag",this.getSingin_flag());    
		this.getFormHM().put("curclass", this.getCurclass());
		this.getFormHM().put("cursignin", this.getCursignin());
		this.getFormHM().put("sdmakeup_date",this.getSdmakeup_date());
		this.getFormHM().put("sdjudge",this.getSdjudge()); 
	}


	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setTreeCode((String)this.getFormHM().get("treeCode"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setKind((String)this.getFormHM().get("kind"));
		this.setRegisterdate((String)this.getFormHM().get("registerdate"));
		this.setKq_dbase_list((ArrayList)this.getFormHM().get("kq_dbase_list"));
		this.setSelect_pre((String)this.getFormHM().get("select_pre"));
		this.setSelect_flag((String)this.getFormHM().get("select_flag"));
		this.setSelect_name((String)this.getFormHM().get("select_name"));
		this.setKq_duration((String)this.getFormHM().get("kq_duration"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setOrdeby((String)this.getFormHM().get("ordeby"));
		this.setWorkcalendar((String)this.getFormHM().get("workcalendar"));
		this.setKq_list((ArrayList)this.getFormHM().get("kq_list"));
		this.setDatelist((ArrayList)this.getFormHM().get("datelist"));
		this.setIssdao((String)this.getFormHM().get("issdao"));
		this.setSdao_count_field((String)this.getFormHM().get("sdao_count_field"));
		this.setStart_date((String)this.getFormHM().get("start_date"));
		this.setEnd_date((String)this.getFormHM().get("end_date"));
		this.setSql_self((String)this.getFormHM().get("sql_self"));
		this.setColumn_self((String)this.getFormHM().get("column_self"));
		this.setWhere_self((String)this.getFormHM().get("where_self"));
		this.setOrder_self((String)this.getFormHM().get("order_self"));
		this.setIsInout_flag((String)this.getFormHM().get("isInout_flag"));
		this.setDbsign((String)this.getFormHM().get("dbsign"));
		this.setA0100sign((String)this.getFormHM().get("a0100sign"));
		this.setInout_flag((String)this.getFormHM().get("inout_flag"));
		this.setOper_cause((String)this.getFormHM().get("oper_cause"));
		this.setCard_causation((String)this.getFormHM().get("card_causation"));
		this.setSingin_flag((String)this.getFormHM().get("singin_flag"));
		this.setMakeup_date((String)this.getFormHM().get("makeup_date"));
		this.setSda0101((String)this.getFormHM().get("sda0101"));
		this.setSdb0110((String)this.getFormHM().get("sdb0110"));
		this.setSde0122((String)this.getFormHM().get("sde0122"));
		this.setUplevel((String)this.getFormHM().get("uplevel"));
		this.setFielditemlist((ArrayList)this.getFormHM().get("fielditemlist"));
		this.setClasslist((ArrayList)this.getFormHM().get("classlist"));
		this.setCurclass((String)this.getFormHM().get("curclass"));
		this.setSigninlist((ArrayList)this.getFormHM().get("signinlist"));
		this.setCursignin((String)this.getFormHM().get("cursignin"));
		this.setSdmakeup_date((String)this.getFormHM().get("sdmakeup_date"));
		this.setSdjudge((String)this.getFormHM().get("sdjudge"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setClassA01((String)this.getFormHM().get("classA01"));
		this.setCardnoName((String)this.getFormHM().get("cardnoName"));
		this.setCardno((String)this.getFormHM().get("cardno"));
		this.setCardnoId((String)this.getFormHM().get("cardnoId"));
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		 if("/kq/machine/netsignin/empNetSingnin_data".equals(arg0.getPath())&&(arg1.getParameter("b_search")!=null))
		 {
			 if(this.getPagination()!=null)
		          this.getPagination().firstPage();//?
		 }
		 if("/kq/machine/netsignin/signinlist".equals(arg0.getPath())&&(arg1.getParameter("b_self")!=null))
		 {
			 if(this.getPagination()!=null)
		          this.getPagination().firstPage();//?
		 }
		 if("/kq/machine/netsignin/sdsigninlist".equals(arg0.getPath())&&(arg1.getParameter("b_sdlist")!=null))
		 {
			 if(this.getPagination()!=null)
		          this.getPagination().firstPage();//上岛签到明细
		 }
		 if("/kq/machine/netsignin/empNetSingnin_data".equals(arg0.getPath()))
		 {
			 if(this.getPagination()!=null)
		          this.getPagination().firstPage();
		 }
		 return super.validate(arg0, arg1);
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

	public String getRegisterdate() {
		if(registerdate!=null&&registerdate.length()>0)
			registerdate=registerdate.replaceAll("-", ".");
		return registerdate;
	}

	public void setRegisterdate(String registerdate) {
		this.registerdate = registerdate;
	}

	public ArrayList getDatelist() {
		return datelist;
	}

	public void setDatelist(ArrayList datelist) {
		this.datelist = datelist;
	}

	public ArrayList getKq_dbase_list() {
		return kq_dbase_list;
	}

	public void setKq_dbase_list(ArrayList kq_dbase_list) {
		this.kq_dbase_list = kq_dbase_list;
	}

	public ArrayList getKq_list() {
		return kq_list;
	}

	public void setKq_list(ArrayList kq_list) {
		this.kq_list = kq_list;
	}

	public String getSelect_pre() {
		return select_pre;
	}

	public void setSelect_pre(String select_pre) {
		this.select_pre = select_pre;
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

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public String getOrdeby() {
		return ordeby;
	}

	public void setOrdeby(String ordeby) {
		this.ordeby = ordeby;
	}

	public String getKq_duration() {
		return kq_duration;
	}

	public void setKq_duration(String kq_duration) {
		this.kq_duration = kq_duration;
	}

	public String getWorkcalendar() {
		return workcalendar;
	}

	public void setWorkcalendar(String workcalendar) {
		this.workcalendar = workcalendar;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getSql_self() {
		return sql_self;
	}

	public void setSql_self(String sql_self) {
		this.sql_self = sql_self;
	}

	public String getColumn_self() {
		return column_self;
	}

	public void setColumn_self(String column_self) {
		this.column_self = column_self;
	}

	public String getWhere_self() {
		return where_self;
	}

	public void setWhere_self(String where_self) {
		this.where_self = where_self;
	}

	public String getOrder_self() {
		return order_self;
	}

	public void setOrder_self(String order_self) {
		this.order_self = order_self;
	}

	public String getIsInout_flag() {
		return isInout_flag;
	}

	public void setIsInout_flag(String isInout_flag) {
		this.isInout_flag = isInout_flag;
	}

	public String getDbsign() {
		return dbsign;
	}

	public void setDbsign(String dbsign) {
		this.dbsign = dbsign;
	}

	public String getA0100sign() {
		return a0100sign;
	}

	public void setA0100sign(String a0100sign) {
		this.a0100sign = a0100sign;
	}

	public String getMakeup_time() {
		return makeup_time;
	}

	public void setMakeup_time(String makeup_time) {
		this.makeup_time = makeup_time;
	}

	public String getSdb0110() {
		return sdb0110;
	}

	public void setSdb0110(String sdb0110) {
		this.sdb0110 = sdb0110;
	}

	public String getSde0122() {
		return sde0122;
	}

	public void setSde0122(String sde0122) {
		this.sde0122 = sde0122;
	}

	public String getSda0101() {
		return sda0101;
	}

	public void setSda0101(String sda0101) {
		this.sda0101 = sda0101;
	}

	public String getUplevel() {
		return uplevel;
	}

	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}

	public ArrayList getFielditemlist() {
		return fielditemlist;
	}

	public void setFielditemlist(ArrayList fielditemlist) {
		this.fielditemlist = fielditemlist;
	}

	public ArrayList getClasslist() {
		return classlist;
	}

	public void setClasslist(ArrayList classlist) {
		this.classlist = classlist;
	}

	public String getCurclass() {
		return curclass;
	}

	public void setCurclass(String curclass) {
		this.curclass = curclass;
	}

	public ArrayList getSigninlist() {
		return signinlist;
	}

	public void setSigninlist(ArrayList signinlist) {
		this.signinlist = signinlist;
	}

	public String getCursignin() {
		return cursignin;
	}

	public void setCursignin(String cursignin) {
		this.cursignin = cursignin;
	}

	public String getSdmakeup_date() {
		return sdmakeup_date;
	}

	public void setSdmakeup_date(String sdmakeup_date) {
		this.sdmakeup_date = sdmakeup_date;
	}

	public String getSdjudge() {
		return sdjudge;
	}

	public void setSdjudge(String sdjudge) {
		this.sdjudge = sdjudge;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getClassA01() {
		return classA01;
	}

	public void setClassA01(String classA01) {
		this.classA01 = classA01;
	}

	public String getCardnoName() {
		return cardnoName;
	}

	public void setCardnoName(String cardnoName) {
		this.cardnoName = cardnoName;
	}

	public String getCardno() {
		return cardno;
	}

	public void setCardno(String cardno) {
		this.cardno = cardno;
	}

	public String getCardnoId() {
		return cardnoId;
	}

	public void setCardnoId(String cardnoId) {
		this.cardnoId = cardnoId;
	}
	

}
