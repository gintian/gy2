/*
 * Created on 2005-10-18
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.param;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OtherParamForm extends FrameForm {
   
	private String fieldcond;
	/**
	 * @return Returns the fieldcond.
	 */
	public String getFieldcond() {
		return fieldcond;
	}
	/**
	 * @param fieldcond The fieldcond to set.
	 */
	public void setFieldcond(String fieldcond) {
		this.fieldcond = fieldcond;
	}
	private String email;
	private String phone;
	//固定电话
	private String telephone;
	/**
	 *单位|部门|职位登记表 
	 */
	private String emp_card_id;
	private String org_card_id;
	private String pos_card_id;
	
	private ArrayList empcardlist=new ArrayList();
	private ArrayList orgcardlist=new ArrayList();
	private ArrayList poscardlist=new ArrayList();
	private String syn_bound;
	private String org_root_caption;
	private String welcome_marquee;
	private String link_p_width;//友情连接图片宽度
	private String link_p_height;//友情连接图片高度
	private String lawrule_file_days;//规章制度新增图标显示天数
	private String announce_days;//设置公告栏中的公告项多少天内为new
	private ArrayList syn_list=new ArrayList();
	private String stat_id;
	private ArrayList statlist=new ArrayList();
	private String ykcard_auto;//登记表单元格内容字体是否自动适应大小
	private String lastdays;//设置周报和月报提交期限(上期末最后x天)
	private String firstdays;//设置周报和月报提交期限(前期末最后x天)
	private String photo_h;//设置照片高
	private String photo_w;//设置照片宽
	private String subunitup;  //报表上报判断下级单位是否上报。
	private String updisk;//报表上报需要校检
	private String editupdisk;//是否有权修改下级单位报表
	private String inputchinfor;//我的信息修改是否直接进库
	private String condisk;//报表上报需要校检（是否包含下级）
	private String browse_photo;////0默认为表格信息，1照片显示
	private String infosort_browse;//按信息分类浏览员工信息
	private ArrayList pinyin_fieldlist = new ArrayList();//拼音简码指标查询
	private String pinyin_field;//按信息分类浏览员工信息
	private String common_roster;//常用花名册
	private String display_e0122;//部门显示包含上X级名称
	private ArrayList common_rosterlist = new ArrayList();
	private String photo_other_view;
	private String photolength="";
	private String photo_other_itemid;
	private String seprartor;//部门层级间分隔符
	
	private String dairyinfolimit;//日报提交期限
	private String limit_HH;
	private String limit_MM;
	/**
	 * 人员信息必填;
	 */
	private String units;  //单位
	private String place; //职位
	/**
	 * @return Returns the email.
	 */
	private String rownums;
	private String org_browse_format;
	private String birthday_wid;
	private ArrayList warnlist=new ArrayList();
	private String photo_maxsize="";
	private String multimedia_maxsize="";//tianye add 多媒体最大值
	private String browse_search_state="";
	/**
	 * 是否审批信息
	 * 0 审批 默认状态
	 * 1 不需要审批
	 */
	private String approveflag;
	private String num_per_page;
	/**
	 * 黑名单
	 * 
	 */
	private String blacklist_per="";
	private String blacklist_field="";
	private ArrayList dblist=new ArrayList();//人员库
	private ArrayList fieldlist=new ArrayList() ;//主集列表
	/**
	 * 明星员工
	 */
	private String staff_info="";
	private String complex_id="";
	private ArrayList complexList=new ArrayList();//复杂查询常用条件
	private String setid="";
	private String codeItemId="";
	private String fieldItemId="";
	private ArrayList setlist=new ArrayList();
	private ArrayList itemlist=new ArrayList();
	/**
	 * 人员分类条件
	 */
	private ArrayList condlist=new ArrayList();
	private ArrayList g_conds=new ArrayList();
	private String gquery_conds="";
	private String gquery_cond="";
	private String g_cond="";
	/**
	 * 报表上报是否支持审批  zhaoxg 2013-1-25
	 * @return
	 */
	private String isApprove = "";
	private String relation_id = "";
	private ArrayList approvelist = new ArrayList();

	public String getLoglevel() {
		return loglevel;
	}

	public void setLoglevel(String loglevel) {
		this.loglevel = loglevel;
	}

	//系统主题皮肤
	private String themes="default";

	/**
	 * 应用日志级别设置
	 * @return
	 */
	private String loglevel;

	public String getThemes() {
		return themes;
	}
	public void setThemes(String themes) {
		this.themes = themes;
	}
	public String getGquery_cond() {
		return gquery_cond;
	}
	public void setGquery_cond(String gquery_cond) {
		this.gquery_cond = gquery_cond;
	}
	public String getG_cond() {
		return g_cond;
	}
	public void setG_cond(String g_cond) {
		this.g_cond = g_cond;
	}
	public ArrayList getCondlist() {
		return condlist;
	}
	public void setCondlist(ArrayList condlist) {
		this.condlist = condlist;
	}
	public String getEmail() {
		return email;
	}
	/**
	 * @param email The email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return Returns the phone.
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone The phone to set.
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setComplexList((ArrayList)this.getFormHM().get("complexList"));
		this.setStaff_info((String)this.getFormHM().get("staff_info"));
		this.setComplex_id((String)this.getFormHM().get("complex_id"));
		this.setSetid((String)this.getFormHM().get("setid"));
		this.setCodeItemId((String)this.getFormHM().get("codeItemId"));
		this.setFieldItemId((String)this.getFormHM().get("fieldItemId"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		
		this.setSeprartor((String)this.getFormHM().get("seprartor"));
		this.setOrg_browse_format((String)this.getFormHM().get("org_browse_format"));
		this.setEmail((String)this.getFormHM().get("email"));
		this.setPhone((String)this.getFormHM().get("phone"));
		this.setTelephone((String)this.getFormHM().get("telephone"));
		this.setFieldcond((String)this.getFormHM().get("fieldcond"));
		this.setEmpcardlist((ArrayList)this.getFormHM().get("empcardlist"));
		this.setOrgcardlist((ArrayList)this.getFormHM().get("orgcardlist"));
		this.setPoscardlist((ArrayList)this.getFormHM().get("poscardlist"));
		this.setEmp_card_id((String)this.getFormHM().get("empcard"));
		this.setOrg_card_id((String)this.getFormHM().get("orgcard"));
		this.setPos_card_id((String)this.getFormHM().get("poscard"));
		this.setRownums((String) this.getFormHM().get("rownums"));

		this.setApproveflag((String)this.getFormHM().get("approveflag"));

		this.setSyn_bound((String)this.getFormHM().get("syn_bound"));
		this.setSyn_list((ArrayList)this.getFormHM().get("syn_list"));
        this.setOrg_root_caption((String)this.getFormHM().get("org_root_caption"));
        this.setWelcome_marquee((String)this.getFormHM().get("welcome_marquee"));
        this.setNum_per_page((String) this.getFormHM().get("num_per_page"));
        this.setLink_p_width((String)this.getFormHM().get("link_p_width"));
        this.setLink_p_height((String)this.getFormHM().get("link_p_height"));
        this.setLawrule_file_days((String)this.getFormHM().get("lawrule_file_days"));
        this.setAnnounce_days((String)this.getFormHM().get("announce_days"));
        this.setStatlist((ArrayList)this.getFormHM().get("statlist"));
        this.setStat_id((String)this.getFormHM().get("stat_id"));
        this.setYkcard_auto((String)this.getFormHM().get("ykcard_auto"));
        this.setLastdays((String)this.getFormHM().get("lastdays"));
        this.setFirstdays((String)this.getFormHM().get("firstdays"));
        this.setPhoto_h((String)this.getFormHM().get("photo_h"));
        this.setPhoto_w((String)this.getFormHM().get("photo_w"));
        this.setSubunitup((String)this.getFormHM().get("subunitup"));
        this.setUpdisk((String)this.getFormHM().get("updisk"));
        this.setEditupdisk((String)this.getFormHM().get("editupdisk"));
        this.setInputchinfor((String)this.getFormHM().get("inputchinfor"));
        this.setCondisk((String)this.getFormHM().get("condisk"));
        this.setBrowse_photo((String)this.getFormHM().get("browse_photo"));
        this.setInfosort_browse((String)this.getFormHM().get("infosort_browse"));
        this.setPinyin_field((String)this.getFormHM().get("pinyin_field"));
        this.setPinyin_fieldlist((ArrayList)this.getFormHM().get("pinyin_fieldlist"));
        this.setCommon_roster((String)this.getFormHM().get("common_roster"));
        this.setCommon_rosterlist((ArrayList)this.getFormHM().get("common_rosterlist"));
        this.setWarnlist((ArrayList)this.getFormHM().get("warnlist"));
        this.setBirthday_wid((String)this.getFormHM().get("birthday_wid"));
        this.setDisplay_e0122((String)this.getFormHM().get("display_e0122"));
        this.setUnits((String)this.getFormHM().get("units"));
        this.setPlace((String)this.getFormHM().get("place"));
        this.setPhoto_maxsize((String)this.getFormHM().get("photo_maxsize"));
        this.setMultimedia_maxsize((String)this.getFormHM().get("multimedia_maxsize"));
        this.setPhoto_other_view((String)this.getFormHM().get("photo_other_view"));
        String photolength = "";
	     if(photo_other_view!=null&&photo_other_view.length()>0){
	    	 photolength=Integer.toString(photo_other_view.split(",").length);
	     }
	    this.setPhotolength(photolength);
        this.setPhoto_other_itemid((String)this.getFormHM().get("photo_other_itemid"));
        
        this.setBlacklist_field((String)this.getFormHM().get("blacklist_field"));
        this.setBlacklist_per((String)this.getFormHM().get("blacklist_per"));
        this.setDblist((ArrayList)this.getFormHM().get("dblist"));
        this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
        this.setCondlist((ArrayList)this.getFormHM().get("condlist"));
        this.setGquery_cond((String)this.getFormHM().get("gquery_cond"));
        this.setG_cond((String)this.getFormHM().get("g_cond"));
        this.setG_conds((ArrayList)this.getFormHM().get("g_conds"));
        this.setBrowse_search_state((String)this.getFormHM().get("browse_search_state"));
        this.setDairyinfolimit((String)this.getFormHM().get("dairyinfolimit"));
        this.setLimit_HH((String)this.getFormHM().get("limit_HH"));
        this.setLimit_MM((String)this.getFormHM().get("limit_MM"));
        this.setIsApprove((String) this.getFormHM().get("isApprove"));
        this.setApprovelist((ArrayList) this.getFormHM().get("approvelist"));
        this.setRelation_id((String) this.getFormHM().get("relation_id"));
        
        this.setThemes((String)this.getFormHM().get("themes"));

		this.setLoglevel((String) this.getFormHM().get("loglevel"));

	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("staff_info", this.getStaff_info());
		this.getFormHM().put("complex_id", this.getComplex_id());
		this.getFormHM().put("complexList", this.getComplexList());
		
		this.getFormHM().put("seprartor", this.getSeprartor());
		this.getFormHM().put("org_browse_format", this.getOrg_browse_format());
		this.getFormHM().put("email",email);
		this.getFormHM().put("phone",phone);
		this.getFormHM().put("telephone",telephone);
		String jsdir=getServlet().getServletContext().getRealPath("/ajax");
		this.getFormHM().put("jsdir",jsdir);
		this.getFormHM().put("emp_card_id", emp_card_id);
		this.getFormHM().put("org_card_id", org_card_id);
		this.getFormHM().put("pos_card_id", pos_card_id);
		this.getFormHM().put("rownums",this.getRownums());
		this.getFormHM().put("num_per_page",this.getNum_per_page());
		this.getFormHM().put("approveflag",this.getApproveflag());
        this.getFormHM().put("link_p_width",this.getLink_p_width());
		this.getFormHM().put("syn_bound",this.getSyn_bound());
        this.getFormHM().put("org_root_caption",this.getOrg_root_caption());
        this.getFormHM().put("welcome_marquee",this.getWelcome_marquee());
        this.getFormHM().put("link_p_height",this.getLink_p_height());
        this.getFormHM().put("lawrule_file_days",this.getLawrule_file_days());
        this.getFormHM().put("announce_days", this.getAnnounce_days());
        this.getFormHM().put("stat_id",this.getStat_id());
        this.getFormHM().put("ykcard_auto",this.getYkcard_auto());
        this.getFormHM().put("firstdays",this.getFirstdays());
        this.getFormHM().put("lastdays",this.getLastdays());
        this.getFormHM().put("photo_h",this.getPhoto_h());
        this.getFormHM().put("photo_w",this.getPhoto_w());
        this.getFormHM().put("subunitup",this.getSubunitup());
        this.getFormHM().put("updisk",this.getUpdisk());
        this.getFormHM().put("editupdisk",this.getEditupdisk());
        this.getFormHM().put("inputchinfor",this.getInputchinfor());
        this.getFormHM().put("condisk",this.getCondisk());
        this.getFormHM().put("browse_photo", this.getBrowse_photo());
        this.getFormHM().put("infosort_browse",this.getInfosort_browse());
        this.getFormHM().put("pinyin_field",this.getPinyin_field());
        this.getFormHM().put("common_roster",this.getCommon_roster());
        this.getFormHM().put("common_rosterlist",this.getCommon_rosterlist());
        this.getFormHM().put("birthday_wid", this.getBirthday_wid());
        this.getFormHM().put("display_e0122",this.getDisplay_e0122());
        this.getFormHM().put("units",this.getUnits());
        this.getFormHM().put("place",this.getPlace());
        this.getFormHM().put("photo_maxsize", this.getPhoto_maxsize());
        this.getFormHM().put("multimedia_maxsize", this.getMultimedia_maxsize());
        this.getFormHM().put("photo_other_view", this.getPhoto_other_view());
        this.getFormHM().put("photo_other_itemid", this.getPhoto_other_itemid());
        this.getFormHM().put("blacklist_per", this.getBlacklist_per());
        this.getFormHM().put("blacklist_field", this.getBlacklist_field());
        this.getFormHM().put("gquery_conds", this.getGquery_conds());
        this.getFormHM().put("gquery_cond", this.getGquery_cond());
        this.getFormHM().put("browse_search_state", this.getBrowse_search_state());
        this.getFormHM().put("dairyinfolimit", this.getDairyinfolimit());
        this.getFormHM().put("limit_HH", this.getLimit_HH());
        this.getFormHM().put("limit_MM", this.getLimit_MM());
        this.getFormHM().put("isApprove", this.getIsApprove());
        this.getFormHM().put("approvelist", this.getApprovelist());
        this.getFormHM().put("relation_id", this.getRelation_id());
        
        this.getFormHM().put("themes", this.getThemes());
		this.getFormHM().put("loglevel",this.getLoglevel());
	}
	public String getEmp_card_id() {
		return emp_card_id;
	}
	public void setEmp_card_id(String emp_card_id) {
		this.emp_card_id = emp_card_id;
	}
	public String getOrg_card_id() {
		return org_card_id;
	}
	public void setOrg_card_id(String org_card_id) {
		this.org_card_id = org_card_id;
	}
	public String getPos_card_id() {
		return pos_card_id;
	}
	public void setPos_card_id(String pos_card_id) {
		this.pos_card_id = pos_card_id;
	}
	public ArrayList getEmpcardlist() {
		return empcardlist;
	}
	public void setEmpcardlist(ArrayList empcardlist) {
		this.empcardlist = empcardlist;
	}
	public ArrayList getOrgcardlist() {
		return orgcardlist;
	}
	public void setOrgcardlist(ArrayList orgcardlist) {
		this.orgcardlist = orgcardlist;
	}
	public ArrayList getPoscardlist() {
		return poscardlist;
	}
	public void setPoscardlist(ArrayList poscardlist) {
		this.poscardlist = poscardlist;
	}
	public String getRownums() {
		return rownums;
	}
	public void setRownums(String rownums) {
		this.rownums = rownums;
	}

	public String getApproveflag() {
		return approveflag;
	}
	public void setApproveflag(String approveflag) {
		if(approveflag==null){
			approveflag="0";
		}
		this.approveflag = approveflag;
	}

	public String getSyn_bound() {
		return syn_bound;
	}
	public void setSyn_bound(String syn_bound) {
		this.syn_bound = syn_bound;
	}
	public ArrayList getSyn_list() {
		return syn_list;
	}
	public void setSyn_list(ArrayList syn_list) {
		this.syn_list = syn_list;
	}
	public String getOrg_root_caption() {
		return org_root_caption;
	}
	public void setOrg_root_caption(String org_root_caption) {
		this.org_root_caption = org_root_caption;
	}
	public String getWelcome_marquee() {
		return welcome_marquee;
	}
	public void setWelcome_marquee(String welcome_marquee) {
		this.welcome_marquee = welcome_marquee;
	}
	public String getNum_per_page() {
		if(this.num_per_page==null|| "".equals(this.num_per_page)){
			this.num_per_page="21";
		}
		return num_per_page;
	}
	public void setNum_per_page(String num_per_page) {
		if(num_per_page==null|| "".equals(num_per_page)){
			num_per_page="21";
		}
		this.num_per_page = num_per_page;
	}
	public String getLink_p_height() {
		return link_p_height;
	}
	public void setLink_p_height(String link_p_height) {
		this.link_p_height = link_p_height;
	}
	public String getLink_p_width() {
		return link_p_width;
	}
	public void setLink_p_width(String link_p_width) {
		this.link_p_width = link_p_width;
	}
	public String getLawrule_file_days() {
		return lawrule_file_days;
	}
	public void setLawrule_file_days(String lawrule_file_days) {
		this.lawrule_file_days = lawrule_file_days;
	}
	public String getStat_id() {
		return stat_id;
	}
	public void setStat_id(String stat_id) {
		this.stat_id = stat_id;
	}
	public ArrayList getStatlist() {
		return statlist;
	}
	public void setStatlist(ArrayList statlist) {
		this.statlist = statlist;
	}
	public String getYkcard_auto() {
		return ykcard_auto;
	}
	public void setYkcard_auto(String ykcard_auto) {
		this.ykcard_auto = ykcard_auto;
	}
	public String getFirstdays() {
		return firstdays;
	}
	public void setFirstdays(String firstdays) {
		this.firstdays = firstdays;
	}
	public String getLastdays() {
		return lastdays;
	}
	public void setLastdays(String lastdays) {
		this.lastdays = lastdays;
	}
	public String getPhoto_h() {
		return photo_h;
	}
	public void setPhoto_h(String photo_h) {
		this.photo_h = photo_h;
	}
	public String getPhoto_w() {
		return photo_w;
	}
	public void setPhoto_w(String photo_w) {
		this.photo_w = photo_w;
	}
	public String getUpdisk() {
		return updisk;
	}
	public void setUpdisk(String updisk) {
		this.updisk = updisk;
	}
	public String getEditupdisk() {
		return editupdisk;
	}
	public void setEditupdisk(String editupdisk) {
		this.editupdisk = editupdisk;
	}
	public String getInputchinfor() {
		return inputchinfor;
	}
	public void setInputchinfor(String inputchinfor) {
		this.inputchinfor = inputchinfor;
	}
	public String getCondisk() {
		return condisk;
	}
	public void setCondisk(String condisk) {
		this.condisk = condisk;
	}
	public String getBrowse_photo() {
		return browse_photo;
	}
	public void setBrowse_photo(String browse_photo) {
		this.browse_photo = browse_photo;
	}
	public String getInfosort_browse() {
		return infosort_browse;
	}
	public void setInfosort_browse(String infosort_browse) {
		this.infosort_browse = infosort_browse;
	}
	public String getPinyin_field() {
		return pinyin_field;
	}
	public void setPinyin_field(String pinyin_field) {
		this.pinyin_field = pinyin_field;
	}
	public ArrayList getPinyin_fieldlist() {
		return pinyin_fieldlist;
	}
	public void setPinyin_fieldlist(ArrayList pinyin_fieldlist) {
		this.pinyin_fieldlist = pinyin_fieldlist;
	}
	public String getCommon_roster() {
		return common_roster;
	}
	public void setCommon_roster(String common_roster) {
		this.common_roster = common_roster;
	}
	public ArrayList getCommon_rosterlist() {
		return common_rosterlist;
	}
	public void setCommon_rosterlist(ArrayList common_rosterlist) {
		this.common_rosterlist = common_rosterlist;
	}
	public String getOrg_browse_format() {
		return org_browse_format;
	}
	public void setOrg_browse_format(String org_browse_format) {
		this.org_browse_format = org_browse_format;
	}
	public String getBirthday_wid() {
		return birthday_wid;
	}
	public void setBirthday_wid(String birthday_wid) {
		this.birthday_wid = birthday_wid;
	}
	public ArrayList getWarnlist() {
		return warnlist;
	}
	public void setWarnlist(ArrayList warnlist) {
		this.warnlist = warnlist;
	}
	public String getDisplay_e0122() {
		return display_e0122;
	}
	public void setDisplay_e0122(String display_e0122) {
		this.display_e0122 = display_e0122;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getPhoto_maxsize() {
		return photo_maxsize;
	}
	public void setPhoto_maxsize(String photo_maxsize) {
		this.photo_maxsize = photo_maxsize;
	}
	public String getSubunitup() {
		return subunitup;
	}
	public void setSubunitup(String subunitup) {
		this.subunitup = subunitup;
	}
	public String getPhoto_other_view() {
		return photo_other_view;
	}
	public void setPhoto_other_view(String photo_other_view) {
		this.photo_other_view = photo_other_view;
	}
	public String getPhoto_other_itemid() {
		return photo_other_itemid;
	}
	public void setPhoto_other_itemid(String photo_other_itemid) {
		this.photo_other_itemid = photo_other_itemid;
	}
	public String getAnnounce_days() {
		return announce_days;
	}
	public void setAnnounce_days(String announce_days) {
		this.announce_days = announce_days;
	}
	public String getBlacklist_per() {
		return blacklist_per;
	}
	public void setBlacklist_per(String blacklist_per) {
		this.blacklist_per = blacklist_per;
	}
	public String getBlacklist_field() {
		return blacklist_field;
	}
	public void setBlacklist_field(String blacklist_field) {
		this.blacklist_field = blacklist_field;
	}
	public ArrayList getDblist() {
		return dblist;
	}
	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public String getSeprartor() {
		return seprartor;
	}
	public void setSeprartor(String seprartor) {
		this.seprartor = seprartor;
	}
	public ArrayList getG_conds() {
		return g_conds;
	}
	public void setG_conds(ArrayList g_conds) {
		this.g_conds = g_conds;
	}
	public String getGquery_conds() {
		return gquery_conds;
	}
	public void setGquery_conds(String gquery_conds) {
		this.gquery_conds = gquery_conds;
	}
	public String getBrowse_search_state() {
		return browse_search_state;
	}
	public void setBrowse_search_state(String browse_search_state) {
		this.browse_search_state = browse_search_state;
	}
	public String getDairyinfolimit() {
		return dairyinfolimit;
	}
	public void setDairyinfolimit(String dairyinfolimit) {
		this.dairyinfolimit = dairyinfolimit;
	}
	public String getLimit_HH() {
		return limit_HH;
	}
	public void setLimit_HH(String limit_HH) {
		this.limit_HH = limit_HH;
	}
	public String getLimit_MM() {
		return limit_MM;
	}
	public void setLimit_MM(String limit_MM) {
		this.limit_MM = limit_MM;
	}
	public String getIsApprove() {
		return isApprove;
	}
	public void setIsApprove(String isApprove) {
		this.isApprove = isApprove;
	}
	public String getRelation_id() {
		return relation_id;
	}
	public void setRelation_id(String ralation_id) {
		this.relation_id = ralation_id;
	}
	public ArrayList getApprovelist() {
		return approvelist;
	}
	public void setApprovelist(ArrayList approvelist) {
		this.approvelist = approvelist;
	}
	public String getMultimedia_maxsize() {
		return multimedia_maxsize;
	}
	public void setMultimedia_maxsize(String multimediaMaxsize) {
		multimedia_maxsize = multimediaMaxsize;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getPhotolength() {
		return photolength;
	}
	public void setPhotolength(String photolength) {
		this.photolength = photolength;
	}
	public ArrayList getComplexList() {
		return complexList;
	}
	public void setComplexList(ArrayList complexList) {
		this.complexList = complexList;
	}
	public String getComplex_id() {
		return complex_id;
	}
	public void setComplex_id(String complex_id) {
		this.complex_id = complex_id;
	}
	public String getSetid() {
		return setid;
	}
	public void setSetid(String setid) {
		this.setid = setid;
	}
	public String getCodeItemId() {
		return codeItemId;
	}
	public void setCodeItemId(String codeItemId) {
		this.codeItemId = codeItemId;
	}
	public String getFieldItemId() {
		return fieldItemId;
	}
	public void setFieldItemId(String fieldItemId) {
		this.fieldItemId = fieldItemId;
	}
	public ArrayList getSetlist() {
		return setlist;
	}
	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}
	public ArrayList getItemlist() {
		return itemlist;
	}
	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}
	public String getStaff_info() {
		return staff_info;
	}
	public void setStaff_info(String staff_info) {
		this.staff_info = staff_info;
	}
	
}
