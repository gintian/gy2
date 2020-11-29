/*
 * Created on 2005-5-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.ykcard;

import com.hjsj.hrms.valueobject.ykcard.CardTagParamView;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.UserView;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-5-12:13:56:24</p>
 * @author Administrator
 * @version 1.0
 * 
 */

public class CardTagParamForm extends FrameForm {

	private CardTagParamView cardparam=new CardTagParamView();
	private String treeCode;
	private String username="";
	private String strsql;
	private String cond_str;
	private String a0100;
	private String code;
	private String kind;
	private String flag;
	private String order_by;
	private String zp_pos_id;
	private String tabid="0";
	private String b0110;
	private String pre;
	private String tableid;
	private ArrayList personlist=new ArrayList();
	/**薪酬表列表,chenmengqing*/
	private ArrayList cardlist=new ArrayList();
	/**是否多张登记表可选*/
	private String multi_cards="1";
	
	/**桌面还是非桌面*/
	private String home;
	private String fieldpurv;
	
	private String showFlag;
	
	
	private String module;//模块判断   绩效模块=per，其他暂时为空
	/**1人员,2机构,4职位,5绩效反馈表,6基准岗位*/
	private String inforkind;
	/**登记表类型A人员,B机构,K职位,P绩效,H基准岗位*/
	private String cardtype="no";
	
	/**应用库表前缀*/
    private String userbase="usr";
    /**查询字段列表*/
    private ArrayList fieldlist=new ArrayList();
    /**应用过滤条件*/
    private String dbcond="''";
    /**模糊查询0:不用模糊查询１模糊查询*/
    private String like="0";
	/**
	 * @return Returns the dbcond.
	 */
    private ArrayList browsefields=new ArrayList();
    private String columns="";
	private String userpriv="";
	
	private String dbType="1";
	private String a0101;
	private String plan_id="";
	private HashMap part_map=new HashMap();//兼职
	private String uplevel="";//部门层级
	private String temp_id="";//模板id
	private String dataFlag="";/* <CARDSTYLE>A人员,B单位,K职位,R培训,P绩效</CARDSTYLE>
    <TEMPLATEID>考核模板号</TEMPLATEID><PLANID>考核计划号</PLANID>
     */  
	private String istype="";
	
	private String returnvalue;
	
	private String factor;
	private String expr;
	private String history;
	private String likeflag;
	private String orgparentcode;
	private String deptparentcode;
	private String posparentcode;
	private String isShowCondition;
	private String select_name;
	private ArrayList queryfieldlist=new ArrayList();
	private String querylike;
	private String orglike;
	private String query;
	private String check;
	private String bizDate; // 日志的业务日期
	private String firstFlag;
	
	private String sub_domain;//附件
	
	private int pageWidth=0;
	
	public int getPageWidth() {
		return pageWidth;
	}
	public void setPageWidth(int pageWidth) {
		this.pageWidth = pageWidth;
	}
	public String getIstype() {
		return istype;
	}
	public void setIstype(String istype) {
		this.istype = istype;
	}
	public String getPlan_id() {
		return plan_id;
	}
	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}
	public HashMap getPart_map() {
		return part_map;
	}
	public void setPart_map(HashMap part_map) {
		this.part_map = part_map;
	}
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
	/**
	 * @return Returns the zp_pos_id.
	 */
	public String getZp_pos_id() {
		return zp_pos_id;
	}
	/**
	 * @param zp_pos_id The zp_pos_id to set.
	 */
	public void setZp_pos_id(String zp_pos_id) {
		this.zp_pos_id = zp_pos_id;
	}
	public String getDbType() {
		return dbType;
	}
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
	public String getUserpriv() {
		return userpriv;
	}
	public void setUserpriv(String userpriv) {
		this.userpriv = userpriv;
	}
	public String getDbcond() {
		return dbcond;
	}
	/**
	 * @param dbcond The dbcond to set.
	 */
	public void setDbcond(String dbcond) {
		this.dbcond = dbcond;
	}
	/**
	 * @return Returns the flag.
	 */
	public String getFlag() {
		return flag;
	}
	/**
	 * @param flag The flag to set.
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}
	/**
	 * @return Returns the code.
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code The code to set.
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return Returns the kind.
	 */
	public String getKind() {
		return kind;
	}
	/**
	 * @param kind The kind to set.
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}
	/**
	 * @return Returns the userbase.
	 */
	public String getUserbase() {
		return userbase;
	}
	/**
	 * @param userbase The userbase to set.
	 */
	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}
	/**
	 * @return Returns the a0100.
	 */
	public String getA0100() {
		return a0100;
	}
	/**
	 * @param a0100 The a0100 to set.
	 */
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	/**
	 * @return Returns the cond_str.
	 */
	public String getCond_str() {
		return cond_str;
	}
	/**
	 * @param cond_str The cond_str to set.
	 */
	public void setCond_str(String cond_str) {
		this.cond_str = cond_str;
	}
	/**
	 * @return Returns the strsql.
	 */
	public String getStrsql() {
		return strsql;
	}
	/**
	 * @param strsql The strsql to set.
	 */
	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}
	/**
	 * @return Returns the treeCode.
	 */
	public String getTreeCode() {
		return treeCode;
	}
	/**
	 * @param treeCode The treeCode to set.
	 */
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	/**
	 * @return Returns the sub_domain.
	 */
	public String getSub_domain() {
		return sub_domain;
	}
	/**
	 * @param sub_domain The sub_domain to set.
	 */
	public void setSub_domain(String sub_domain) {
		this.sub_domain = sub_domain;
	}
	/* 
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		this.setFirstFlag((String)this.getFormHM().get("firstFlag"));
		this.setPageWidth(Integer.parseInt(this.getFormHM().get("pageWidth").toString()));
		this.setBizDate((String)this.getFormHM().get("bizDate"));
		this.setTreeCode((String)this.getFormHM().get("treeCode"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setCond_str((String)this.getFormHM().get("cond_str"));
		this.setKind((String)this.getFormHM().get("kind"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setDbcond((String)this.getFormHM().get("dbcond"));
	    this.setOrder_by((String)this.getFormHM().get("order_by"));
	    this.setUserbase((String)this.getFormHM().get("userbase"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
	    if((String)this.getFormHM().get("currentpage")!=null)
	       this.getCardparam().setPageid(Integer.parseInt((String)this.getFormHM().get("currentpage")));
	    this.setPersonlist((ArrayList)this.getFormHM().get("personlist"));
	    this.like="0";
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setTabid((String)this.getFormHM().get("tabid"));
		this.setInforkind((String)this.getFormHM().get("inforkind"));
		this.setB0110((String)this.getFormHM().get("b0110"));
		this.setShowFlag((String)this.getFormHM().get("showflag"));
		this.setModule((String)this.getFormHM().get("module"));
		this.setCardtype((String)this.getFormHM().get("cardtype"));
		this.setPre((String)this.getFormHM().get("pre"));
		this.setCardlist((ArrayList)this.getFormHM().get("cardlist"));
		this.setUserpriv((String)this.getFormHM().get("userpriv"));
		this.setBrowsefields((ArrayList)this.getFormHM().get("browsefields"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setDbType((String)this.getFormHM().get("dbType"));
		this.setFieldpurv((String)this.getFormHM().get("fieldpurv"));
		this.setA0101((String)this.getFormHM().get("a0101"));
		this.setUsername((String)this.getFormHM().get("username"));
		this.setTableid((String) this.getFormHM().get("tableid"));
		this.setPlan_id((String)this.getFormHM().get("plan_id"));
		this.setPart_map((HashMap)this.getFormHM().get("part_map"));
		this.setUplevel((String)this.getFormHM().get("uplevel"));
		this.setDataFlag((String)this.getFormHM().get("dataFlag"));
		this.setIstype((String)this.getFormHM().get("istype"));
		this.setIsShowCondition((String)this.getFormHM().get("isShowCondition"));
	     this.setQuerylike((String)this.getFormHM().get("querylike"));
	     this.setQuery((String)this.getFormHM().get("query"));
	     this.setOrglike((String)this.getFormHM().get("orglike"));
	     this.setQueryfieldlist((ArrayList)this.getFormHM().get("queryfieldlist"));
	     this.setSub_domain((String)this.getFormHM().get("sub_domain"));
	}

	/* 
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("firstFlag", this.getFirstFlag());
		this.getFormHM().put("pageWidth", this.getPageWidth());
		this.getFormHM().put("bizDate",this.getBizDate());
		this.getFormHM().put("userbase",userbase);
        this.getFormHM().put("fieldlist",this.getFieldlist());
        this.getFormHM().put("like",this.getLike());
        this.getFormHM().put("a0100",this.getA0100());
        this.getFormHM().put("tabid",this.getTabid());   
        this.getFormHM().put("inforkind",this.getInforkind());  
        this.getFormHM().put("b0110",this.getB0110());
        this.getFormHM().put("tabid",this.getTabid());
        this.getFormHM().put("userpriv", this.getUserpriv());
        this.getFormHM().put("fieldpurv", this.getFieldpurv());
        this.getFormHM().put("username", this.getUsername());
        this.getFormHM().put("code", this.getCode());
        this.getFormHM().put("kind", this.getKind());
        Map map = (Map) this.getFormHM().get("requestPamaHM");
        this.getFormHM().put("tableid", (String) map.get("tableid"));
        this.getFormHM().put("plan_id", this.getPlan_id());
        this.getFormHM().put("temp_id", this.getTemp_id());
        
        this.getFormHM().put("check", this.getCheck());
	    this.getFormHM().put("select_name",select_name);
	    this.getFormHM().put("expr", this.getExpr());
        this.getFormHM().put("factor", this.getFactor());
        this.getFormHM().put("orgparentcode", this.orgparentcode);
        this.getFormHM().put("deptparentcode", this.deptparentcode);
        this.getFormHM().put("posparentcode", this.posparentcode);
        this.getFormHM().put("kind", this.getKind());
        this.getFormHM().put("querylike", this.getQuerylike());       
        this.getFormHM().put("query", this.getQuery());      
		this.getFormHM().put("orglike", this.getOrglike());
		this.getFormHM().put("queryfieldlist", this.getQueryfieldlist());
		this.getFormHM().put("flag", flag);
		this.getFormHM().put("module", this.getModule());
		this.getFormHM().put("sub_domain", this.getSub_domain());
	}

	/**
	 * @return Returns the cardparam.
	 */
	public CardTagParamView getCardparam() {
		return cardparam;
	}
	/**
	 * @param cardparam The cardparam to set.
	 */
	public void setCardparam(CardTagParamView cardparam) {
		this.cardparam = cardparam;
	}
	/**
	 * @return Returns the order_by.
	 */
	public String getOrder_by() {
		return order_by;
	}
	/**
	 * @param order_by The order_by to set.
	 */
	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}
	  @Override
      public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		  
		  if("/workbench/ykcard/showinfo".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	        {
	            if(this.getPagination()!=null)
	              this.getPagination().firstPage();//?
	            this.getFormHM().clear();
	            this.getFormHM().put("select_name","");
	            this.setSelect_name("");
	            this.getFormHM().put("check","");
	            this.setCheck("");	            
	            this.setQueryfieldlist(null);
	            this.getFormHM().put("queryfieldlist", null);
	            this.setOrglike("1");
	            this.getFormHM().put("orglike", "1");
	        }
        if("/workbench/ykcard/showinfodata".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
        {
            if(this.getPagination()!=null)
              this.getPagination().firstPage();//?
        }
        if("/workbench/ykcard/showykcardinfo".equals(arg0.getPath())&&arg1.getParameter("b_setpage")!=null)
        {
            if(this.getPagination()!=null)
              this.getPagination().firstPage();//?
            this.setTabid("");
        }        
        if("/ykcard/employeeselfcard".equals(arg0.getPath())&&arg1.getParameter("b_card")!=null)
        {
        	if(this.getPagination()!=null)
                this.getPagination().firstPage();//?
              this.setTabid("");
        }
        if("/general/inform/synthesisbrowse/synthesiscard".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
        {
        	if(this.getPagination()!=null)
                this.getPagination().firstPage();//? 
        	cardparam.setPageid(0);
        }
        if("/general/inform/synthesisbrowse/mycard".equals(arg0.getPath())&&arg1.getParameter("b_mysearch")!=null)
        {
        	if(arg1.getParameter("returnvalue")==null){
        		this.setReturnvalue("");
        	}
        	if(this.getPagination()!=null)
                this.getPagination().firstPage();//? 
        	cardparam.setPageid(0);
        	this.getFormHM().clear();
        	 this.setTabid("");
        	 if("infoself".equals(flag)){
			   UserView userView = (UserView)arg1.getSession().getAttribute("userView");
			   this.a0100=userView.getUserId();
			   userbase = userView.getDbname();
			   this.getFormHM().put("a0100", this.a0100);
			   this.getFormHM().put("userbase", this.userbase);
        	 }
        }
        if("/ykcard/employeeselfcard".equals(arg0.getPath())&&arg1.getParameter("b_card")!=null){
        	cardparam.setPageid(0);
        }
        this.getFormHM().put("session",arg1.getSession());
        return super.validate(arg0, arg1);
    }
	/**
	 * @return Returns the tabid.
	 */
	public String getTabid() {
		return tabid;
	}
	/**
	 * @param tabid The tabid to set.
	 */
	public void setTabid(String tabid) {
		this.tabid = tabid;
	}
	/**
	 * @return Returns the personlist.
	 */
	public ArrayList getPersonlist() {
		return personlist;
	}
	/**
	 * @param personlist The personlist to set.
	 */
	public void setPersonlist(ArrayList personlist) {
		this.personlist = personlist;
	}
	/**
	 * @return Returns the fieldlist.
	 */
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	/**
	 * @param fieldlist The fieldlist to set.
	 */
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	/**
	 * @return Returns the like.
	 */
	public String getLike() {
		return like;
	}
	/**
	 * @param like The like to set.
	 */
	public void setLike(String like) {
		this.like = like;
	}
	/**
	 * @return Returns the inforkind.
	 */
	public String getInforkind() {
		return inforkind;
	}
	/**
	 * @param inforkind The inforkind to set.
	 */
	public void setInforkind(String inforkind) {
		this.inforkind = inforkind;
	}
	public String getShowFlag() {
		return showFlag;
	}
	public void setShowFlag(String showFlag) {
		this.showFlag = showFlag;
	}
	
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getHome() {
		return home;
	}
	public void setHome(String home) {
		this.home = home;
	}
	public String getCardtype() {
		return cardtype;
	}
	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	}
	public String getB0110() {
		return b0110;
	}
	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}
	public String getPre() {
		return pre;
	}
	public void setPre(String pre) {
		this.pre = pre;
	}
	public ArrayList getCardlist() {
		return cardlist;
	}
	public void setCardlist(ArrayList cardlist) {
		this.cardlist = cardlist;
	}
	public String getMulti_cards() {
		return multi_cards;
	}
	public void setMulti_cards(String multi_cards) {
		this.multi_cards = multi_cards;
	}
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		//liuy 2015-4-29 9276：自助服务中点过我的信息中的我的薪酬后，再点员工信息中的信息浏览中的岗位说明书就是空的，要是直接点的话就是有内容的 begin
        this.setFlag("");
        //wangjl 2015-12-01 13243: 我的信息-信息浏览，点姓名后面的放大镜，默认显示的登记表值是可以显示的，一切换登记表就不显示
        if(!"/general/inform/synthesisbrowse/mycard".equals(arg0.getPath())&&!"link".equals(arg1.getParameter("b_mysearch")))
        {
        	if(!"/workbench/browse/showposinfo".equals(arg0.getPath()))//2015-9-28 liuy 12989:柳州东城投资开发有限公司------HCM7.0自助查看岗位说明书问题
            	this.setUserpriv("");
        }
        
		//liuy 2015-4-29 end
		this.setModule("");
		this.setMulti_cards("1");
		super.reset(arg0, arg1);
	}
	public ArrayList getBrowsefields() {
		return browsefields;
	}
	public void setBrowsefields(ArrayList browsefields) {
		this.browsefields = browsefields;
	}
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public String getFieldpurv() {
		return fieldpurv;
	}
	public void setFieldpurv(String fieldpurv) {
		this.fieldpurv = fieldpurv;
	}
	public String getA0101() {
		return a0101;
	}
	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTableid() {
		return tableid;
	}
	public void setTableid(String tableid) {
		this.tableid = tableid;
	}
	public String getDataFlag() {
		return dataFlag;
	}
	public void setDataFlag(String dataFlag) {
		this.dataFlag = dataFlag;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	public String getTemp_id() {
		return temp_id;
	}
	public void setTemp_id(String temp_id) {
		this.temp_id = temp_id;
	}
	public String getFactor() {
		return factor;
	}
	public void setFactor(String factor) {
		this.factor = factor;
	}
	public String getExpr() {
		return expr;
	}
	public void setExpr(String expr) {
		this.expr = expr;
	}
	public String getHistory() {
		return history;
	}
	public void setHistory(String history) {
		this.history = history;
	}
	public String getLikeflag() {
		return likeflag;
	}
	public void setLikeflag(String likeflag) {
		this.likeflag = likeflag;
	}
	public String getOrgparentcode() {
		return orgparentcode;
	}
	public void setOrgparentcode(String orgparentcode) {
		this.orgparentcode = orgparentcode;
	}
	public String getDeptparentcode() {
		return deptparentcode;
	}
	public void setDeptparentcode(String deptparentcode) {
		this.deptparentcode = deptparentcode;
	}
	public String getPosparentcode() {
		return posparentcode;
	}
	public void setPosparentcode(String posparentcode) {
		this.posparentcode = posparentcode;
	}
	public String getIsShowCondition() {
		return isShowCondition;
	}
	public void setIsShowCondition(String isShowCondition) {
		this.isShowCondition = isShowCondition;
	}
	public String getSelect_name() {
		return select_name;
	}
	public void setSelect_name(String select_name) {
		this.select_name = select_name;
	}
	public ArrayList getQueryfieldlist() {
		return queryfieldlist;
	}
	public void setQueryfieldlist(ArrayList queryfieldlist) {
		this.queryfieldlist = queryfieldlist;
	}
	public String getQuerylike() {
		return querylike;
	}
	public void setQuerylike(String querylike) {
		this.querylike = querylike;
	}
	public String getOrglike() {
		return orglike;
	}
	public void setOrglike(String orglike) {
		this.orglike = orglike;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getCheck() {
		return check;
	}
	public void setCheck(String check) {
		this.check = check;
	}
	public String getBizDate() {
		return bizDate;
	}
	public void setBizDate(String bizDate) {
		this.bizDate = bizDate;
	}
	public String getFirstFlag() {
		return firstFlag;
	}
	public void setFirstFlag(String firstFlag) {
		this.firstFlag = firstFlag;
	}
	
	
	
}
