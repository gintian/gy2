/*
 * Created on 2005-6-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.browse;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import com.hrms.struts.valueobject.UserView;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BrowseForm extends FrameForm {

    
    private String userbase="Usr";
    private String setname="A01";
	private String a0100;
    ArrayList infodetailfieldlist=new ArrayList();
    ArrayList infofieldlist=new ArrayList();
    ArrayList infosetlist=new ArrayList();
    private String flag;
    private String cond_str;
    private String code;
    private String kind;
    private String treeCode;
    private String strsql;
    private String returnvalue;
    private String dbcond;
    private String order_by;
    private String i9999;
	private String b0110;
	private String e0122;
	private String e01a1;
	private String a0101;
	private String isUserEmploy;
	private ArrayList browsefields=new ArrayList();
	private String columns;
	private ArrayList personsortlist=new ArrayList();
	private String personsort;
	private String ishavepostdesc;
	private String cardid;
	private String emp_cardId;
	private String pos_cardId;
	private String userpriv="";
	private String select_name;
	private String infosortflag;
    private String a01desc;
	private String clientName;//客户名称 如：zfw
	private String ps_card_attach;//是否显示岗位附件
	private String task_card_attach;//显示任务说明书附件
    private String caution_color;//颜色警示
	/**
	 * 信息是否显示申请修改 0为否，1为是
	 */
	private String isAble;
	/**
	 * 主集是否报批 0为否，1为是
	 */
	private String isMainPeal;
	private String npage;
	private String fromphoto;
	/**
	 * 人员信息是否需要审批功能
	 * =0或null不需要
	 * =1需要
	 */
	private String approveflag;
	private String inputchinfor;
	private String num_per_page;
	private String type;
	private String browse_photo;
	private String infosort;
	private String mainsort;
	private ArrayList basesort_list=new ArrayList();
	private ArrayList subsort_list=new ArrayList();
	private HashMap infoMap=new HashMap();
	private ArrayList sortSetlist=new ArrayList();
	private String sortname;
	private String roster; //常用花名册
	private String mustername; //常用花名册数据库表名
	private String state;
	private String uplevel="";
	private String check="";
	private String home="";
	private String result="";
	private String orgtype="";
	private String where_n="";
	private String part_unit="";//兼职
	private String part_setid="";//兼职
	private String part_appoint="";//兼职任免指标
	private String part_pos="";//兼职职务
	private String part_dept="";
	private String part_order="";
	private String part_format="";
	private HashMap part_map=new HashMap();
	private String ensql="";
	private String photo_other_view="";
	private String expr="";
	private String factor="";
	private String history="";
	/***人员快速浏览****/
	private String isShowCondition="";
	private String querylike="";//模糊查询
	private ArrayList scanfieldlist=new ArrayList();//查询指标 
	private String unit_code_mess="";//岗位编码
	private String codemess="";//岗位名称
	private String query="";//快速查询标识
	private String orgflag="";//组织机构标志
	private String parentid="";
	private String codesetid="";
	private String return_codeid="";
	private String orglike="";
	private String scantype="";	
	private ArrayList queryfieldlist=new ArrayList();
	private String photolength="";//zgd 2014-3-17  显示照片下指标数量
	private String politics;//政治面貌标示 xuj 2010-2-24
	private String a_code;//党团工会组织机构树代码
	private String returns;
	private String likeflag;
    private ArrayList condlist=new ArrayList();//常用查询条件
    private String stock_cond;//常用条件
    private int showflag;//人员分类是否显示 =0不显示
    private String browse_search_state="";//人员信息浏览查询项状态 0，隐藏 1，显示
    private String fenlei_priv="";//人员分类
    private String definephotoname;
    private ArrayList stringfieldlist=new ArrayList();
    private String scrW;
    private String scrH;
    
    private String isphotoview;
    
    private String multimedia_file_flag;//信息集是否显示附件
    
    private String virAxx;
    //二次查询
    private String querySecond;
    //Vfs中照片的id
    private String photoId;
	
	public String getMultimedia_file_flag() {
		return multimedia_file_flag;
	}
	public void setMultimedia_file_flag(String multimedia_file_flag) {
		this.multimedia_file_flag = multimedia_file_flag;
	}
	
	public String getIsphotoview() {
		return isphotoview;
	}
	public void setIsphotoview(String isphotoview) {
		this.isphotoview = isphotoview;
	}
	public String getStock_cond() {
		return stock_cond;
	}
	public void setStock_cond(String stock_cond) {
		this.stock_cond = stock_cond;
	}
	public String getReturns() {
		return returns;
	}
	public void setReturns(String returns) {
		this.returns = returns;
	}
	public ArrayList getQueryfieldlist() {
		return queryfieldlist;
	}
	public void setQueryfieldlist(ArrayList queryfieldlist) {
		this.queryfieldlist = queryfieldlist;
	}
	
	public String getScantype() {
		return scantype;
	}
	public void setScantype(String scantype) {
		this.scantype = scantype;
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
	public String getUnit_code_mess() {
		return unit_code_mess;
	}
	public void setUnit_code_mess(String unit_code_mess) {
		this.unit_code_mess = unit_code_mess;
	}
	public String getCodemess() {
		return codemess;
	}
	public void setCodemess(String codemess) {
		this.codemess = codemess;
	}
	public String getIsShowCondition() {
		return isShowCondition;
	}
	public void setIsShowCondition(String isShowCondition) {
		this.isShowCondition = isShowCondition;
	}
	public String getQuerylike() {
		return querylike;
	}
	public void setQuerylike(String querylike) {
		this.querylike = querylike;
	}
	public ArrayList getScanfieldlist() {
		return scanfieldlist;
	}
	public void setScanfieldlist(ArrayList scanfieldlist) {
		this.scanfieldlist = scanfieldlist;
	}
	public String getPhoto_other_view() {
		return photo_other_view;
	}
	public void setPhoto_other_view(String photo_other_view) {
		this.photo_other_view = photo_other_view;
	}
	public String getEnsql() {
		return ensql;
	}
	public void setEnsql(String ensql) {
		this.ensql = ensql;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getHome() {
		return home;
	}
	public void setHome(String home) {
		this.home = home;
	}
	public String getCheck() {
		return check;
	}
	public void setCheck(String check) {
		this.check = check;
	}
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
	public String getSortname() {
		return sortname;
	}
	public void setSortname(String sortname) {
		this.sortname = sortname;
	}
	public ArrayList getSortSetlist() {
		return sortSetlist;
	}
	public void setSortSetlist(ArrayList sortSetlist) {
		this.sortSetlist = sortSetlist;
	}
	public String getBrowse_photo() {
		return browse_photo;
	}
	public void setBrowse_photo(String browse_photo) {
		this.browse_photo = browse_photo;
	}
	public String getCardid() {
		return cardid;
	}
	public void setCardid(String cardid) {
		this.cardid = cardid;
	}
	//private String 
	/**
	 * @return Returns the i9999.
	 */
	public String getI9999() {
		return i9999;
	}
	/**
	 * @param i9999 The i9999 to set.
	 */
	public void setI9999(String i9999) {
		this.i9999 = i9999;
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
	/**
	 * @return Returns the dbcond.
	 */
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
	 * @return Returns the returnvalue.
	 */
	public String getReturnvalue() {
		return returnvalue;
	}
	/**
	 * @param returnvalue The returnvalue to set.
	 */
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
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
    private String photoname;
	/**
	 * @return Returns the infosetlist.
	 */
	public ArrayList getInfosetlist() {
		return infosetlist;
	}
	/**
	 * @param infosetlist The infosetlist to set.
	 */
	public void setInfosetlist(ArrayList infosetlist) {
		this.infosetlist = infosetlist;
	}
	/**
	 * @return Returns the photoname.
	 */
	public String getPhotoname() {
		return photoname;
	}
	/**
	 * @param photoname The photoname to set.
	 */
	public void setPhotoname(String photoname) {
		this.photoname = photoname;
	}
	/**
	 * @return Returns the setname.
	 */
	public String getSetname() {
		return setname;
	}
	/**
	 * @param setname The setname to set.
	 */
	public void setSetname(String setname) {
		this.setname = setname;
	}
    private PaginationForm browseForm=new PaginationForm();    
    private ArrayList list=new ArrayList();

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub

		 this.setUserbase((String)this.getFormHM().get("userbase"));
		 this.getBrowseForm().setList((ArrayList)this.getFormHM().get("detailinfolist"));
		 this.setA0100((String)this.getFormHM().get("a0100"));
		 this.setInfodetailfieldlist((ArrayList)this.getFormHM().get("infodetailfieldlist"));
	     this.setInfofieldlist((ArrayList)this.getFormHM().get("infofieldlist"));
	     this.setInfosetlist((ArrayList)this.getFormHM().get("infosetlist"));	   
	     this.setCond_str((String)this.getFormHM().get("cond_str"));
	     this.setCode((String)this.getFormHM().get("code"));
	     this.setA01desc((String)this.getFormHM().get("a01desc"));
	     this.setKind((String)this.getFormHM().get("kind"));
	 	 this.setStrsql((String)this.getFormHM().get("strsql"));
	     this.setTreeCode((String)this.getFormHM().get("treeCode"));
	     this.setDbcond((String)this.getFormHM().get("dbcond"));
	     this.setOrder_by((String)this.getFormHM().get("order_by"));
	     this.setB0110((String)this.getFormHM().get("b0110"));
	     this.setE0122((String)this.getFormHM().get("e0122"));
	     this.setE01a1((String)this.getFormHM().get("e01a1"));
	     this.setA0101((String)this.getFormHM().get("a0101"));
	     this.setBrowsefields((ArrayList)this.getFormHM().get("browsefields"));
	     this.setColumns((String)this.getFormHM().get("columns"));
	     this.setPersonsortlist((ArrayList)this.getFormHM().get("personsortlist"));
	     this.setPersonsort((String)this.getFormHM().get("personsort"));
	     this.setIshavepostdesc((String)this.getFormHM().get("ishavepostdesc"));
	     this.setCardid((String)this.getFormHM().get("cardid"));
	     this.setApproveflag((String)this.getFormHM().get("approveflag"));
	     this.setInputchinfor((String) this.getFormHM().get("inputchinfor"));
	     this.setNum_per_page((String) this.getFormHM().get("num_per_page"));
	     this.setEmp_cardId((String)this.getFormHM().get("emp_cardId"));
	     this.setPos_cardId((String)this.getFormHM().get("pos_cardId"));
	     this.setType((String)this.getFormHM().get("type"));
	     this.setUserpriv((String)this.getFormHM().get("userpriv"));
	     this.setSelect_name((String)this.getFormHM().get("select_name"));
	     this.setBrowse_photo((String)this.getFormHM().get("browse_photo"));
	     this.setSubsort_list((ArrayList)this.getFormHM().get("subsort_list"));
	     this.setBasesort_list((ArrayList)this.getFormHM().get("basesort_list"));
	     this.setInfosort((String)this.getFormHM().get("infosort"));
	     this.setInfoMap((HashMap)this.getFormHM().get("infoMap"));
	     this.setSortSetlist((ArrayList)this.getFormHM().get("sortSetlist"));
	     this.setSortname((String)this.getFormHM().get("sortname"));
	     this.setMainsort((String)this.getFormHM().get("mainsort"));
	     this.setRoster((String)this.getFormHM().get("roster"));
	     this.setMustername((String)this.getFormHM().get("mustername"));
	     this.setState((String)this.getFormHM().get("state"));
	     this.setUplevel((String)this.getFormHM().get("uplevel"));
	     this.setSetname((String)this.getFormHM().get("setname"));
	     this.setCheck((String)this.getFormHM().get("check"));
	     this.setHome((String)this.getFormHM().get("home"));
	     this.setOrgtype((String)this.getFormHM().get("orgtype"));
	     this.setWhere_n((String)this.getFormHM().get("where_n"));
	     this.setPart_unit((String)this.getFormHM().get("part_unit"));
	     this.setPart_setid((String)this.getFormHM().get("part_setid"));
	     this.setPart_pos((String)this.getFormHM().get("part_pos"));
	     this.setEnsql((String)this.getFormHM().get("ensql"));
	     this.setPhoto_other_view((String)this.getFormHM().get("photo_other_view"));
	     this.setPart_appoint((String)this.getFormHM().get("part_appoint"));
	     this.setIsShowCondition((String)this.getFormHM().get("isShowCondition"));
	     this.setQuerylike((String)this.getFormHM().get("querylike"));
	     this.setScanfieldlist((ArrayList)this.getFormHM().get("scanfieldlist"));
	     this.setCodemess((String)this.getFormHM().get("codemess"));
	     this.setUnit_code_mess((String)this.getFormHM().get("unit_code_mess"));
	     this.setQuery((String)this.getFormHM().get("query"));
	     this.setParentid((String)this.getFormHM().get("parentid"));
	     this.setCodesetid((String)this.getFormHM().get("codesetid"));
	     this.setOrglike((String)this.getFormHM().get("orglike"));
	     this.setQueryfieldlist((ArrayList)this.getFormHM().get("queryfieldlist"));
	     String photolength = "";
	     if(photo_other_view!=null&&photo_other_view.length()>0){
	    	 photolength=Integer.toString(photo_other_view.split(",").length);
	     }
	     this.setPhotolength(photolength);
	     this.setClientName((String)this.getFormHM().get("clientName"));
	     this.setPs_card_attach((String)this.getFormHM().get("ps_card_attach"));
	     this.setTask_card_attach((String)this.getFormHM().get("task_card_attach"));
         this.setCaution_color((String)this.getFormHM().get("caution_color"));
	     this.setIsAble((String) this.getFormHM().get("isAble"));
	     this.setIsMainPeal((String) this.getFormHM().get("isMainPeal")); 
	     this.setNpage((String) this.getFormHM().get("npage"));
	     this.setCondlist((ArrayList)this.getFormHM().get("condlist"));
	     this.setStock_cond((String)this.getFormHM().get("stock_cond"));
	     this.setBrowse_search_state((String)this.getFormHM().get("browse_search_state"));
	     this.setPart_dept((String)this.getFormHM().get("part_dept"));
	     this.setPart_order((String)this.getFormHM().get("part_order"));
		 this.setPart_format((String)this.getFormHM().get("part_format"));			
		 this.setPart_map((HashMap)this.getFormHM().get("part_map"));
		 this.setStringfieldlist((ArrayList)this.getFormHM().get("stringfieldlist"));
		 this.setIsphotoview((String)this.getFormHM().get("isphotoview"));
		 this.setMultimedia_file_flag((String)this.getFormHM().get("multimedia_file_flag"));
		 this.setVirAxx((String)this.getFormHM().get("virAxx"));
		 this.setQuerySecond((String)this.getFormHM().get("querySecond"));
		 this.setIsUserEmploy((String)this.getFormHM().get("isUserEmploy"));
		 this.setPhotoId((String)this.getFormHM().get("photoId"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("roster", this.getRoster());
		this.getFormHM().put("isphotoview", this.getIsphotoview());
		this.getFormHM().put("selectedlist",(ArrayList)this.getBrowseForm().getSelectedList());
		this.getFormHM().put("userbase",this.getUserbase());
		this.getFormHM().put("setname",setname);
		this.getFormHM().put("a0100",a0100);
		this.getFormHM().put("i9999",i9999);
		this.getFormHM().put("personsort",personsort);
        this.getFormHM().put("cardid",cardid);
        this.getFormHM().put("userpriv", userpriv);
        this.getFormHM().put("select_name",select_name);
        this.getFormHM().put("code", code);
        this.getFormHM().put("kind", this.getKind());
        this.getFormHM().put("infosortflag", infosortflag);
        this.getFormHM().put("sortname", sortname);
        this.getFormHM().put("mainsort", this.getMainsort());
        this.getFormHM().put("check", this.getCheck());
        this.getFormHM().put("home",this.getHome());
        this.getFormHM().put("part_unit", part_unit);
        this.getFormHM().put("part_setid", part_setid);
        this.getFormHM().put("expr", this.getExpr());
        this.getFormHM().put("factor", this.getFactor());
        this.getFormHM().put("history", this.getHistory());
        this.getFormHM().put("part_appoint", part_appoint);
        this.getFormHM().put("querylike", this.getQuerylike());
        this.getFormHM().put("scanfieldlist", this.getScanfieldlist());
        this.getFormHM().put("query", this.getQuery());
        this.getFormHM().put("parentid", this.parentid);
		this.getFormHM().put("codesetid", this.codesetid);
		this.getFormHM().put("orglike", this.getOrglike());
		this.getFormHM().put("queryfieldlist", this.getQueryfieldlist());
		this.getFormHM().put("npage", this.getNpage());
		this.getFormHM().put("likeflag", likeflag);
		this.getFormHM().put("stock_cond", this.getStock_cond());
		this.getFormHM().put("browse_search_state", this.getBrowse_search_state());
		this.getFormHM().put("returnvalue", this.returnvalue);
		this.getFormHM().put("multimedia_file_flag", this.getMultimedia_file_flag());
		this.getFormHM().put("virAxx", this.getVirAxx());		
		this.getFormHM().put("querySecond", this.getQuerySecond());		
		this.getFormHM().put("photoId", this.getPhotoId());		
	}

	/**
	 * @return Returns the list.
	 */
	public ArrayList getList() {
		return list;
	}
	/**
	 * @param list The list to set.
	 */
	public void setList(ArrayList list) {
		this.list = list;
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
	 * @return Returns the infodetailfieldlist.
	 */
	public ArrayList getInfodetailfieldlist() {
		return infodetailfieldlist;
	}
	/**
	 * @param infodetailfieldlist The infodetailfieldlist to set.
	 */
	public void setInfodetailfieldlist(ArrayList infodetailfieldlist) {
		this.infodetailfieldlist = infodetailfieldlist;
	}
	/**
	 * @return Returns the infofieldlist.
	 */
	public ArrayList getInfofieldlist() {
		return infofieldlist;
	}
	/**
	 * @param infofieldlist The infofieldlist to set.
	 */
	public void setInfofieldlist(ArrayList infofieldlist) {
		this.infofieldlist = infofieldlist;
	}
	/**
	 * @return Returns the selfInfoForm.
	 */
	public PaginationForm getBrowseForm() {
		return browseForm;
	}
	/**
	 * @param selfInfoForm The selfInfoForm to set.
	 */
	public void setBrowseForm(PaginationForm browseForm) {
		this.browseForm = browseForm;
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
	  @Override
      public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if("/workbench/browse/showinfodata".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
        {
            if(this.getPagination()!=null)
              this.getPagination().firstPage();//?
            /*this.getFormHM().put("select_name","");
            this.setSelect_name("");*/
            //this.setIsShowCondition("none");
           // this.getFormHM().put("isShowCondition", "");
           /* this.getFormHM().put("query", "");
        	this.setQuery("");
        	this.getFormHM().put("querylike", "");
        	this.setQuerylike("");
        	this.setQueryfieldlist(null);
        	this.getFormHM().put("queryfieldlist", null);*/
        }
        if("/workbench/browse/showinfodata".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            if(this.getPagination()!=null)
              this.getPagination().firstPage();//?            
        }
        
        if("/workbench/browse/browseinfo".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
        {
            if("infoself".equals(flag)){
            	this.a0100="A0100";
            	UserView userView = (UserView)arg1.getSession().getAttribute("userView");
            	if(userView != null)
            		userbase = userView.getDbname();
            		
            	this.getFormHM().put("a0100", this.a0100);
            	this.getFormHM().put("userbase", this.userbase);
            }
        }
        if("/workbench/browse/showselfinfodetail".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
        {
            if(this.getBrowseForm().getPagination()!=null)
            	this.getBrowseForm().getPagination().firstPage();//? 
            //if(this.getPagination()!=null)
               // this.getPagination().firstPage();//?
            if("infoself".equals(flag)){
            	this.a0100="A0100";
            	UserView userView = (UserView)arg1.getSession().getAttribute("userView");
            	if(userView != null)
            		userbase = userView.getDbname();
            	
            	this.getFormHM().put("a0100", this.a0100);
            	this.getFormHM().put("userbase", this.userbase);
            }
        }
        if("/workbench/browse/showmediainfodetail".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
        {
            if(this.getBrowseForm().getPagination()!=null)
            	this.getBrowseForm().getPagination().firstPage();//?
            //if(this.getPagination()!=null)
                //this.getPagination().firstPage();//?
        }
        if("/workbench/browse/showinfo".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
        {
            if(this.getBrowseForm().getPagination()!=null)
            	this.getBrowseForm().getPagination().firstPage();//?    
            if(this.getPagination()!=null)
                this.getPagination().firstPage();//?
            this.getFormHM().put("select_name","");
            this.setSelect_name("");
            this.getFormHM().put("p_select_name", "");       
            this.getFormHM().put("check","no");
            this.setCheck("no");
            this.setHome("");
            this.setExpr("");
            this.setFactor("");
            this.getFormHM().put("expr", "");
            this.getFormHM().put("factor", "");
            this.getFormHM().put("home", "");
            if(arg1.getParameter("returnvalue")!=null)
	        {
	        	   this.setReturnvalue(arg1.getParameter("returnvalue"));
	        }
            if(arg1.getParameter("returns")!=null)
	        {
	        	   this.setReturns(arg1.getParameter("returns"));
	        } else {
	        	this.setReturns("");
	        }
            this.setOrglike("1");
            this.getFormHM().put("orglike", "1");
            this.getFormHM().put("queryfieldlist", null);
            this.setQueryfieldlist(null);
        }
        if("/workbench/browse/showphoto".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
        {
            if(this.getBrowseForm().getPagination()!=null)
            	this.getBrowseForm().getPagination().firstPage();//?
            if(this.getPagination()!=null)
                this.getPagination().firstPage();//?
            this.getFormHM().put("select_name","");
            this.setSelect_name("");
            this.getFormHM().put("p_select_name", "");       
            this.getFormHM().put("check","no");
            this.setCheck("no");
            this.setHome("");
            this.setExpr("");
            this.setFactor("");
            this.getFormHM().put("expr", "");
            this.getFormHM().put("factor", "");
            this.getFormHM().put("home", "");
        }
        if("/workbench/browse/showselfinfo".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
        {
        	 this.getFormHM().put("infosortflag","");
        	 this.setInfosortflag("");
        	 String pho = arg1.getParameter("fromphoto");
        	 if ("1".equalsIgnoreCase(pho)) {
        		 this.setFromphoto("1");
        	 } else {
        		 this.setFromphoto("0");
        	 }
        	 if("infoself".equals(flag)){
        		 this.a0100="A0100";
			   UserView userView = (UserView)arg1.getSession().getAttribute("userView");
			   if(userView != null)
			       userbase = userView.getDbname();
			   
			   this.getFormHM().put("a0100", this.a0100);
			   this.getFormHM().put("userbase", this.userbase);
        	 }
        }
        if("/workbench/browse/scaninfodata".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
        {
        	if(this.getBrowseForm().getPagination()!=null)
            	this.getBrowseForm().getPagination().firstPage();//?     
        	if(this.getPagination()!=null)
                this.getPagination().firstPage();//?
        	
        }
        if("/workbench/browse/scaninfodata".equals(arg0.getPath())&&arg1.getParameter("b_init")!=null)
        {
        	if(this.getBrowseForm().getPagination()!=null)
            	this.getBrowseForm().getPagination().firstPage();//? 
        	if(this.getPagination()!=null)
                this.getPagination().firstPage();//?
        	this.getFormHM().put("query", "");
        	this.setQuery("");
        }
        if("/workbench/browse/scan_photo".equals(arg0.getPath())&&arg1.getParameter("br_photo")!=null)
        {
        	/*if(this.getBrowseForm().getPagination()!=null)
            	this.getBrowseForm().getPagination().firstPage();//? 
        	if(this.getPagination()!=null)
                this.getPagination().firstPage();//?
*/        }
        return super.validate(arg0, arg1);
    }
	public String getA0101() {
		return a0101;
	}
	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}
	public String getB0110() {
		return b0110;
	}
	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}
	public String getE0122() {
		return e0122;
	}
	public void setE0122(String e0122) {
		this.e0122 = e0122;
	}
	public String getE01a1() {
		return e01a1;
	}
	public void setE01a1(String e01a1) {
		this.e01a1 = e01a1;
	}
	public String getIsUserEmploy() {
		return isUserEmploy;
	}
	public void setIsUserEmploy(String isUserEmploy) {
		this.isUserEmploy = isUserEmploy;
	}
	public ArrayList getBrowsefields() {
		return this.browsefields;
	}
	public void setBrowsefields(ArrayList browsefields) {
		this.browsefields = browsefields;
	}
	public String getColumns() {
		return this.columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public String getPersonsort() {
		return this.personsort;
	}
	public void setPersonsort(String personsort) {
		this.personsort = personsort;
	}
	public ArrayList getPersonsortlist() {
		return this.personsortlist;
	}
	public void setPersonsortlist(ArrayList personsortlist) {
		this.personsortlist = personsortlist;
	}
	public String getIshavepostdesc() {
		return this.ishavepostdesc;
	}
	public void setIshavepostdesc(String ishavepostdesc) {
		this.ishavepostdesc = ishavepostdesc;
	}
	public String getApproveflag() {
		return approveflag;
	}
	public void setApproveflag(String approveflag) {
		this.approveflag = approveflag;
	}
	public String getNum_per_page() {
		return num_per_page;
	}
	public void setNum_per_page(String num_per_page) {
		this.num_per_page = num_per_page;
	}
	public String getEmp_cardId() {
		return emp_cardId;
	}
	public void setEmp_cardId(String emp_cardId) {
		this.emp_cardId = emp_cardId;
	}
	public String getPos_cardId() {
		return pos_cardId;
	}
	public void setPos_cardId(String pos_cardId) {
		this.pos_cardId = pos_cardId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUserpriv() {
		return userpriv;
	}
	public void setUserpriv(String userpriv) {
		this.userpriv = userpriv;
	}
	public String getSelect_name() {
		return select_name;
	}
	public void setSelect_name(String select_name) {
		this.select_name = select_name;
	}
	public ArrayList getBasesort_list() {
		return basesort_list;
	}
	public void setBasesort_list(ArrayList basesort_list) {
		this.basesort_list = basesort_list;
	}
	public String getInfosort() {
		return infosort;
	}
	public void setInfosort(String infosort) {
		this.infosort = infosort;
	}
	public ArrayList getSubsort_list() {
		return subsort_list;
	}
	public void setSubsort_list(ArrayList subsort_list) {
		this.subsort_list = subsort_list;
	}
	public String getInfosortflag() {
		return infosortflag;
	}
	public void setInfosortflag(String infosortflag) {
		this.infosortflag = infosortflag;
	}
	public HashMap getInfoMap() {
		return infoMap;
	}
	public void setInfoMap(HashMap infoMap) {
		this.infoMap = infoMap;
	}
	public String getMainsort() {
		return mainsort;
	}
	public void setMainsort(String mainsort) {
		this.mainsort = mainsort;
	}
	public String getRoster() {
		return roster;
	}
	public void setRoster(String roster) {
		this.roster = roster;
	}
	public String getMustername() {
		return mustername;
	}
	public void setMustername(String mustername) {
		this.mustername = mustername;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getOrgtype() {
		return orgtype;
	}
	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}
	public String getWhere_n() {
		return where_n;
	}
	public void setWhere_n(String where_n) {
		this.where_n = where_n;
	}
	public String getPart_unit() {
		return part_unit;
	}
	public void setPart_unit(String part_unit) {
		this.part_unit = part_unit;
	}
	public String getPart_setid() {
		return part_setid;
	}
	public void setPart_setid(String part_setid) {
		this.part_setid = part_setid;
	}
	public String getExpr() {
		return expr;
	}
	public void setExpr(String expr) {
		this.expr = expr;
	}
	public String getFactor() {
		return factor;
	}
	public void setFactor(String factor) {
		this.factor = factor;
	}
	public String getPart_appoint() {
		return part_appoint;
	}
	public void setPart_appoint(String part_appoint) {
		this.part_appoint = part_appoint;
	}
	public String getOrgflag() {
		return orgflag;
	}
	public void setOrgflag(String orgflag) {
		this.orgflag = orgflag;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public String getCodesetid() {
		return codesetid;
	}
	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}
	public String getReturn_codeid() {
		return return_codeid;
	}
	public void setReturn_codeid(String return_codeid) {
		this.return_codeid = return_codeid;
	}
	public String getPart_pos() {
		return part_pos;
	}
	public void setPart_pos(String part_pos) {
		this.part_pos = part_pos;
	}
	public String getInputchinfor() {
		return inputchinfor;
	}
	public void setInputchinfor(String inputchinfor) {
		this.inputchinfor = inputchinfor;

	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getPs_card_attach() {
		return ps_card_attach;
	}
	public void setPs_card_attach(String ps_card_attach) {
		this.ps_card_attach = ps_card_attach;
	}
	public String getTask_card_attach() {
		return task_card_attach;
	}
	public void setTask_card_attach(String task_card_attach) {
		this.task_card_attach = task_card_attach;

	}
	public String getIsAble() {
		return isAble;
	}
	public void setIsAble(String isAble) {
		this.isAble = isAble;
	}
	public String getIsMainPeal() {
		return isMainPeal;
	}
	public void setIsMainPeal(String isMainPeal) {
		this.isMainPeal = isMainPeal;

	}
	public String getCaution_color() {
		return caution_color;
	}
	public void setCaution_color(String caution_color) {
		this.caution_color = caution_color;
	}
	public String getPolitics() {
		return politics;
	}
	public void setPolitics(String politics) {
		this.politics = politics;
	}
	public String getA_code() {
		return a_code;
	}
	public void setA_code(String a_code) {
		this.a_code = a_code;
	}
	public String getFromphoto() {
		return fromphoto;
	}
	public void setFromphoto(String fromphoto) {
		this.fromphoto = fromphoto;
	}
	public String getA01desc() {
		return a01desc;
	}
	public void setA01desc(String a01desc) {
		this.a01desc = a01desc;
	}
	public String getNpage() {
		return npage;
	}
	public void setNpage(String npage) {
		this.npage = npage;
	}
	public String getLikeflag() {
		return likeflag;
	}
	public void setLikeflag(String likeflag) {
		this.likeflag = likeflag;
	}
	public ArrayList getCondlist() {
		return condlist;
	}
	public void setCondlist(ArrayList condlist) {
		this.condlist = condlist;
	}	
	public int getShowflag() {
		int flag=0;
		if(this.getCondlist()!=null)
			flag=this.getCondlist().size();
		return flag;
	}
	public String getHistory() {
		return history;
	}
	public void setHistory(String history) {
		this.history = history;
	}
	public String getBrowse_search_state() {
		return browse_search_state;
	}
	public void setBrowse_search_state(String browse_search_state) {
		this.browse_search_state = browse_search_state;
	}
	public String getFenlei_priv() {
		return fenlei_priv;
	}
	public void setFenlei_priv(String fenlei_priv) {
		this.fenlei_priv = fenlei_priv;
	}
	public String getPart_dept() {
		return part_dept;
	}
	public void setPart_dept(String part_dept) {
		this.part_dept = part_dept;
	}
	public String getPart_order() {
		return part_order;
	}
	public void setPart_order(String part_order) {
		this.part_order = part_order;
	}
	public String getPart_format() {
		return part_format;
	}
	public void setPart_format(String part_format) {
		this.part_format = part_format;
	}
	public HashMap getPart_map() {
		return part_map;
	}
	public void setPart_map(HashMap part_map) {
		this.part_map = part_map;
	}
	public ArrayList getStringfieldlist() {
		return stringfieldlist;
	}
	public void setStringfieldlist(ArrayList stringfieldlist) {
		this.stringfieldlist = stringfieldlist;
	}
	public String getDefinephotoname() {
		return definephotoname;
	}
	public void setDefinephotoname(String definephotoname) {
		this.definephotoname = definephotoname;
	}
	public String getScrW() {
		return scrW;
	}
	public void setScrW(String scrW) {
		this.scrW = scrW;
	}
	public String getScrH() {
		return scrH;
	}
	public void setScrH(String scrH) {
		this.scrH = scrH;
	}
	public String getPhotolength() {
		return photolength;
	}
	public void setPhotolength(String photolength) {
		this.photolength = photolength;
	}
    public String getVirAxx() {
        return virAxx;
    }
    public void setVirAxx(String virAxx) {
        this.virAxx = virAxx;
    }
    public String getQuerySecond() {
        return querySecond;
    }
    public void setQuerySecond(String querySecond) {
        this.querySecond = querySecond;
    }
	public String getPhotoId() {
		return photoId;
	}
	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}
	
}
