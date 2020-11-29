/*
 * Created on 2005-5-23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.selfinfomation;


import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.PaginationForm;
import com.hrms.struts.valueobject.UserView;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SelfInfoForm extends FrameForm {
	/**
	 * @return Returns the action.
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
	private String isAble;//是否显示申请修改，整体报批后不显示申请修改按钮，0为不显示，1为显示
	private String std;
	private List infoFieldList=new ArrayList();
	private List infoSetList=new ArrayList();
	private String setname="A01";
	private String userbase="";
	private String a0100;
	private String i9999;
	private String actiontype;
	private FormFile picturefile;
	private String photoname;

	public String getLowimage() {
		return lowimage;
	}

	public void setLowimage(String lowimage) {
		this.lowimage = lowimage;
	}

	/**
	 * 低分辨率图片文件id，
	 * 原来两个图片路径是固定的，一个过来，两个都能用
	 * 现在改用VFS之后，两个图片文件id没有任何关联，得加个字段来传
	 */
	private String lowimage;
	private String treeCode;
	private String strsql;
	private String cond_str;
	private String code;
	private String kind;
	private String touserbase;
	private String dbcond;
	private String order_by;
    private int current=1;
    private int pageCurrent = 1;
    private String setprv;
    private String todbcond;
    private String orgparentcode;
    private String deptparentcode;
    private String posparentcode;
    private String ismove;
    private String strexpression;
	private String b0110;
	private String e0122;
	private String e01a1;
	private String a0101;
	private ArrayList browsefields=new ArrayList();
	private String columns;
	private ArrayList personsortlist=new ArrayList();
	private String personsort;
	/**子集移动到的序号*/
	private String ordernum;
	private String a01desc;
	
	private String idcardfield;
	private String birthdayfield;
	private String agefield;
	private String axfield;
	private HashMap part_map=new HashMap();
	
	private String workdatefield;
	private String workagefield;
	private String startpostfield;
	private String postagefield;

	private String fenlei_priv="";//人员分类
	private String sortfield;
	private String pdbflag;
	private String cset;
	private String citem;
	private String emp_cardId;
	private String inputchinfor;
	private ArrayList fieldlist = new ArrayList();
	private ArrayList itemlist = new ArrayList();
	private String setid;
	private RecordVo inforVo;
	private ArrayList keylist = new ArrayList();
	private String keyid;
	private ArrayList oldFieldList=new ArrayList();
	private String chg_id;
	private String sp_flag;
	private String viewitem;
	private String sflag;
	private String allflag;
	private String instructions;
	private String checksave;
	private ArrayList newFieldList=new ArrayList();
	private String typeid;
	private ArrayList typelist = new ArrayList();
	private ArrayList spflaglist=new ArrayList();
	private String sql;
	private String where;
	private String column;
	private String userpriv="selfinfo";
	private ArrayList sequenceList=new ArrayList();
	private String sequenceid;
	private String viewbutton;
	private String uplevel="";
	private String select_name;
	private String returnvalue;
	private String expr="";
	private String factor="";
	private String history="";
	private String isAdvance="";
	//虚拟机构子集
	private String virAxx="";	
	private HashMap<String, HashMap<String, String>> repeatMap = new HashMap<String, HashMap<String, String>>();
    private String error;
	/**
	 * 是否是申请修改，0为否，1为是
	 */
	private String isAppEdite = "0";
	/**
	 * 信息录入编辑的字段列表，单双控制
	 * =1双
	 * =0单
	 */
	private String rownums="1";
	/**
	 * 人员信息是否需要审批功能
	 * =0或null需要
	 * =1不需要
	 */
	private String approveflag;
	/*
	 * 信息审核浏览的时候修改信息
	 */
	private String writeable;
	/*
	 * 信息审核每页显示纪录数
	 */
	private String num_per_page;
	/**
	 * 是否对起草信息就行修改，0为否，1为是
	 */
	private String isDraft = "0";
	/**
	 * 
	 * @return Returns the ismove.
	 */
    private String check;//通用查询标志ok表示有查询
	private String orgtype;
	private String infosort;//子集分栏
	private String photo_maxsize;
	private String multimedia_maxsize;
	private String batchImportType;//photo为照片批量导入，multimedia为多媒体
	private String part_unit="";//兼职
	private String part_setid="";//兼职
	private String part_pos="";//兼职职务
	
	
	private ArrayList queryfieldlist=new ArrayList();
	private String orglike="";
	private String query="";//快速查询标识
	private String isShowCondition="";
	private String querylike="";//模糊查询
	private String isBrowse = "";//是否为浏览，在浏览时点击“申请修改”进入信息维护界面，
								//为区分是否从信息浏览进入的信息维护界面
								//0为从信息维护直接进入修改页面
								//1为从信息浏览界面进入修改页面
	
	private String politics;//政治面貌标示 xuj 2010-2-24
	private String a_code;//党团工会组织机构树代码
	
	private ArrayList fieldSetDataList=new ArrayList();//批量导入信息集  xuj 2010-4-21
	private String fieldsetid;
	private String fieldsetdesc;
	private String isupdate;
	private String issameunique;
	private String seconditems;
	private String selectitems;
	private FormFile file;
	private PaginationForm msgPageForm=new PaginationForm();
	private Object[] maps;
	private ArrayList mapsList;
	private HashMap unusekey;
	private StringBuffer A01primarykeys = new StringBuffer();//tianye add  存放Excle中主集信息集中人员对应的唯一标志(导入的人员中库中没有对应但是主集中存在为新增信息)
	private HashMap a0100s=new HashMap();
	private String updatestr;//用于记录有提示报告后如果有提示重复记录用户选择是否更新的主键
	private String primarykeyLabel;
	private String bb0110;
	private String be0122;
	private String be01a1;
	private String codeOfB0110;
	private String codeOfE0122;
	private String codeOfE01a1;
	private HashMap temptablemap=new HashMap();
	private ArrayList msgList = new ArrayList();
	private String outName;
	private String num;
	//批量导入人员信息，数据库中不存在的指标
	private String noExistsField;
	private String returns;//从导航图进入的标志，1为从导航图进入，0为从菜单中进入
	private ArrayList condlist=new ArrayList();//常用查询条件
    private String stock_cond;//常用条件
	private String likeflag;
	private int showflag;//人员分类是否显示 =0不显示
	private String RepeatPrimaryKey;//在导入的Excel中主集中有无重复主键，0没有，1有

	
	private String leadNext;//领导班子>班子成员页面 包含下级参数
	private String analyserInfo;//领导班子分析结果
	private String emp_e;
	private String link_field;
	private String flag;
	private String b0110field;
	private String orderbyfield;
	private String leaderTypeValue;
	private String sessionValue;
	private int len=0;
	private String org_m;
	private int llen=0;
	private String createtimestart;
	private String createtimeend;
	private String createusername;
	
	private String msg;
	private String formationMsg;//超编信息 申请控制 保存时使用
	private String ruleItemid;//批量导入图片时使用的照片命名规则(对于主集中的某一字符串指标)
	private String info;//批量导入图片后存放用户信息
	private String multimediaSortFlag;//批量导入多媒体时的文件分类标记(对应多媒体表中的flag字段)
	private String multimediaName;//批量导入多媒体时，指定的导入文件的统一名称
	
	private String lebal;//自定义唯一性关联指标
	private String codeid;
	private ArrayList fielditemlist=new ArrayList();
	
	private ArrayList rightlist=new ArrayList();
	private ArrayList right_fields=new ArrayList();
	
	private String multimedia_file_flag;//信息集是否显示附件
	private ArrayList multimediaFilelist=new ArrayList();//具有附件的子集
	private String mulSetid;//具有附件的子集id
	private ArrayList mulFileItemlist=new ArrayList();//具有附件的子集de指标项
	private String mulItemid;//批量导入多媒体附件时使用的文件命名规则子集指标项
	private String isRecordEntry;
	
	private String generalsearch;
	//tiany add 头像剪切位置参数
	private String x;
	private String y;
	private String width;
	private String height;
	private String photoType;//头像文件后缀类型
	private String scale;//头像缩放比例
	
	private boolean is_save_add;  //保存并新增标志位
	private String dbvalue;
	
	private String selfA0100;
	private String selfBase;
	private String message;
	private ArrayList multimediaInfoList = new ArrayList();
	//批量删除时，页面选择人员的编号，格式为：00000001,00000002,00000003,……
	private String strId;
	//证件类型指标
	private String idType="";
	//证件类型
	private String idTypeValue;
	//是否关联计算
	private String cardflag;
	private String mainsort;
	
	public String getCardflag() {
		return cardflag;
	}
	public void setCardflag(String cardflag) {
		this.cardflag = cardflag;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	
	
	public String getIdTypeValue() {
		return idTypeValue;
	}
	public void setIdTypeValue(String idTypeValue) {
		this.idTypeValue = idTypeValue;
	}
	public String getDbvalue() {
		return dbvalue;
	}
	public void setDbvalue(String dbvalue) {
		this.dbvalue = dbvalue;
	}
	public String getScale() {
		return scale;
	}
	public void setScale(String scale) {
		this.scale = scale;
	}
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	// tiany end
	public String getGeneralsearch() {
		return generalsearch;
	}
	public void setGeneralsearch(String generalsearch) {
		this.generalsearch = generalsearch;
	}
	public String getMultimedia_file_flag() {
		return multimedia_file_flag;
	}
	public void setMultimedia_file_flag(String multimedia_file_flag) {
		this.multimedia_file_flag = multimedia_file_flag;
	}
	public String getLebal() {
		return lebal;
	}
	public void setLebal(String lebal) {
		this.lebal = lebal;
	}
	public String getMultimediaName() {
		return multimediaName;
	}
	public void setMultimediaName(String multimediaName) {
		this.multimediaName = multimediaName;
	}
	public String getMultimediaSortFlag() {
		return multimediaSortFlag;
	}
	public void setMultimediaSortFlag(String multimediaSortFlag) {
		this.multimediaSortFlag = multimediaSortFlag;
	}
	public String getRuleItemid() {
		return ruleItemid;
	}
	public void setRuleItemid(String ruleItemid) {
		this.ruleItemid = ruleItemid;
	}
	
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public ArrayList getCondlist() {
		return condlist;
	}
	public void setCondlist(ArrayList condlist) {
		this.condlist = condlist;
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
	private String returnvalue1;
	

	public ArrayList getQueryfieldlist() {
		return queryfieldlist;
	}
	public void setQueryfieldlist(ArrayList queryfieldlist) {
		this.queryfieldlist = queryfieldlist;
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
	public String getInfosort() {
		return infosort;
	}
	public void setInfosort(String infosort) {
		this.infosort = infosort;
	}
	public String getOrgtype() {
		return orgtype;
	}
	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}

	public String getIsmove() {
		return ismove;
	}
	/**
	 * @param ismove The ismove to set.
	 */
	public void setIsmove(String ismove) {
		this.ismove = ismove;
	}
	/**
	 * @return Returns the setprv.
	 */
	public String getSetprv() {
		return setprv;
	}
	/**
	 * @param setprv The setprv to set.
	 */
	public void setSetprv(String setprv) {
		this.setprv = setprv;
	}
	/**
	 * @return Returns the tolastpageflag.
	 */
	public String getTolastpageflag() {
		return tolastpageflag;
	}
	/**
	 * @param tolastpageflag The tolastpageflag to set.
	 */
	public void setTolastpageflag(String tolastpageflag) {
		this.tolastpageflag = tolastpageflag;
	}

	private String tolastpageflag;   //新增纪录时标志返回到最后一页
	private String tolastpageflagsub;
	
	/**
	 * @return Returns the tolastpageflagsub.
	 */
	public String getTolastpageflagsub() {
		return tolastpageflagsub;
	}
	/**
	 * @param tolastpageflagsub The tolastpageflagsub to set.
	 */
	public void setTolastpageflagsub(String tolastpageflagsub) {
		this.tolastpageflagsub = tolastpageflagsub;
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
	 * @return Returns the touserbase.
	 */
	public String getTouserbase() {
		return touserbase;
	}
	/**
	 * @param touserbase The touserbase to set.
	 */
	public void setTouserbase(String touserbase) {
		this.touserbase = touserbase;
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
    public HttpSession getSession() {
        return session;
    }
    public void setSession(HttpSession session) {
        this.session = session;
    }
	private HttpSession session;
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
	private PaginationForm selfInfoForm=new PaginationForm(); 
	private PaginationForm page=new PaginationForm();

	
	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub	
		this.setIsRecordEntry((String)this.getFormHM().get("isRecordEntry"));
		this.setSql((String) this.getFormHM().get("sql"));
		this.setWhere((String) this.getFormHM().get("where"));
		this.setColumn((String) this.getFormHM().get("column"));
		this.setInfoFieldList((List)this.getFormHM().get("infofieldlist"));
		this.setSetname((String)this.getFormHM().get("setname"));
		this.setInfoSetList((List)this.getFormHM().get("infosetlist"));
		this.getSelfInfoForm().setList((ArrayList)this.getFormHM().get("detailinfolist"));
		this.getPage().setList((ArrayList)this.getFormHM().get("pageinfolist"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setI9999((String)this.getFormHM().get("i9999")); 
		this.setActiontype((String)this.getFormHM().get("actiontype"));
		this.setPhotoname((String)this.getFormHM().get("photoname"));
		this.setLowimage((String)this.getFormHM().get("lowimage"));
		this.setTreeCode((String)this.getFormHM().get("treeCode"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setCond_str((String)this.getFormHM().get("cond_str"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setKind((String)this.getFormHM().get("kind"));
		this.setDbcond((String)this.getFormHM().get("dbcond"));
		this.setTodbcond((String)this.getFormHM().get("todbcond"));
		this.setOrder_by((String)this.getFormHM().get("order_by"));
		this.getSelfInfoForm().getPagination().gotoPage(current);
		this.getPage().getPagination().gotoPage(pageCurrent);
		this.setSetprv((String)this.getFormHM().get("setprv"));
		this.setUserbase((String)this.getFormHM().get("userbase"));
		this.setOrgparentcode((String)this.getFormHM().get("orgparentcode"));
		this.setPosparentcode((String)this.getFormHM().get("posparentcode"));
		this.setDeptparentcode((String)this.getFormHM().get("deptparentcode"));
		this.setIsmove((String)this.getFormHM().get("ismove"));
	    this.setB0110((String)this.getFormHM().get("b0110"));
	    this.setE0122((String)this.getFormHM().get("e0122"));
	    this.setE01a1((String)this.getFormHM().get("e01a1"));
	     this.setA0101((String)this.getFormHM().get("a0101"));
	     this.setPersonsortlist((ArrayList)this.getFormHM().get("personsortlist"));
	     this.setPersonsort((String)this.getFormHM().get("personsort"));
	     this.setBrowsefields((ArrayList)this.getFormHM().get("browsefields"));
	     this.setColumns((String)this.getFormHM().get("columns"));
	     this.setAgefield((String)this.getFormHM().get("agefield"));
	     this.setBirthdayfield((String)this.getFormHM().get("birthdayfield"));
	     this.setIdcardfield((String)this.getFormHM().get("idcardfield"));
	     this.setWorkagefield((String)this.getFormHM().get("workagefield"));
	     this.setWorkdatefield((String)this.getFormHM().get("workdatefield"));
	     this.setStartpostfield((String)this.getFormHM().get("startpostfield"));
	     this.setPostagefield((String)this.getFormHM().get("postagefield"));
	     this.setAxfield((String)this.getFormHM().get("axfield"));
	     this.setSelect_name((String)this.getFormHM().get("select_name"));
	     this.setStd((String) this.getFormHM().get("std"));
	     this.setSortfield((String) this.getFormHM().get("Sortfield"));
	     
	     this.setPdbflag((String) this.getFormHM().get("pdbflag"));
	     this.setRownums((String)this.getFormHM().get("rownums"));
	     this.setApproveflag((String)this.getFormHM().get("approveflag"));
	     this.setOrgtype((String)this.getFormHM().get("orgtype"));
	     this.setNum_per_page((String)this.getFormHM().get("num_per_page"));
	     this.setWriteable((String)this.getFormHM().get("writeable"));
	     this.setEmp_cardId((String)this.getFormHM().get("emp_cardId"));
	     this.setInputchinfor((String)this.getFormHM().get("inputchinfor"));
	     this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
	     this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
	     this.setSetid((String) this.getFormHM().get("setid"));
	     this.setInforVo((RecordVo) this.getFormHM().get("inforVo"));
	     this.setKeylist((ArrayList)this.getFormHM().get("keylist"));
	     this.setKeyid((String) this.getFormHM().get("keyid"));
	     this.setOldFieldList((ArrayList)this.getFormHM().get("oldFieldList"));
	     this.setChg_id((String) this.getFormHM().get("chg_id"));
	     this.setSp_flag((String) this.getFormHM().get("sp_flag"));
	     this.setSflag((String) this.getFormHM().get("sflag"));
	     this.setViewitem((String) this.getFormHM().get("viewitem"));
	     this.setAllflag((String) this.getFormHM().get("allflag"));
	     this.setInstructions((String) this.getFormHM().get("instructions"));
	     this.setChecksave((String) this.getFormHM().get("checksave"));
	     this.setNewFieldList((ArrayList)this.getFormHM().get("newFieldList"));
	     this.setTypeid((String) this.getFormHM().get("typeid"));
	     this.setTypelist((ArrayList)this.getFormHM().get("typelist"));
	     this.setSpflaglist((ArrayList)this.getFormHM().get("spflaglist"));
	     this.setSequenceList((ArrayList)this.getFormHM().get("sequenceList"));
	     this.setSequenceid((String) this.getFormHM().get("sequenceid"));
	     this.setViewbutton((String) this.getFormHM().get("viewbutton"));
	     this.setUplevel((String)this.getFormHM().get("uplevel"));
	     this.setInfosort((String)this.getFormHM().get("infosort"));
	     this.setCheck((String)this.getFormHM().get("check"));
	     this.setPhoto_maxsize((String)this.getFormHM().get("photo_maxsize")); 
	     this.setMultimedia_maxsize((String)this.getFormHM().get("multimedia_maxsize"));
	     this.setBatchImportType((String)this.getFormHM().get("batchImportType"));
	     this.setReturnvalue((String)this.getFormHM().get("returnvalue"));
	     this.setPart_unit((String)this.getFormHM().get("part_unit"));
	     this.setPart_setid((String)this.getFormHM().get("part_setid"));
	     this.setPart_pos((String)this.getFormHM().get("part_pos"));
//	     this.getFormHM().remove("writeable");
	     this.setIsShowCondition((String)this.getFormHM().get("isShowCondition"));
	     this.setQuerylike((String)this.getFormHM().get("querylike"));
	     this.setQuery((String)this.getFormHM().get("query"));
	     this.setOrglike((String)this.getFormHM().get("orglike"));
	     this.setQueryfieldlist((ArrayList)this.getFormHM().get("queryfieldlist"));
	     this.setIsAppEdite((String) this.getFormHM().get("isAppEdite"));
	     this.setIsAdvance((String) this.getFormHM().get("isAdvance"));
	     this.setIsDraft((String) this.getFormHM().get("isDraft"));
	     this.setIsBrowse((String) this.getFormHM().get("isBrowse"));
	     this.setIsAble((String) this.getFormHM().get("isAble"));
	     this.setFieldSetDataList((ArrayList)this.getFormHM().get("fieldSetDataList"));
//	     this.setRownums();
	     if(this.getMsgPageForm().getList()!=null){
		     if(!this.getMsgPageForm().getList().equals(this.getFormHM().get("msglist"))){
		    	 this.setUpdatestr("");
		     }
	     }
	     this.getMsgPageForm().setList((ArrayList)this.getFormHM().get("msglist"));
	     this.setMaps((Object[])this.getFormHM().get("maps"));
	     this.setMapsList((ArrayList)this.getFormHM().get("mapsList"));
	     this.setUnusekey((HashMap)this.getFormHM().get("unusekey"));
	     this.setA01primarykeys((StringBuffer)this.getFormHM().get("A01primarykeys"));
	     this.a0100s=(HashMap)this.getFormHM().get("a0100s");
	     this.setPart_map((HashMap)this.getFormHM().get("part_map"));
	     this.setFieldsetid((String)this.getFormHM().get("fieldsetid"));
	     this.setFieldsetdesc((String)this.getFormHM().get("fieldsetdesc"));
	     this.setIsupdate((String)this.getFormHM().get("isupdate"));
	     this.setPrimarykeyLabel((String)this.getFormHM().get("primarykeyLabel"));
	     this.setBb0110((String)this.getFormHM().get("bb0110"));
	     this.setBe0122((String)this.getFormHM().get("be0122"));
	     this.setBe01a1((String)this.getFormHM().get("be01a1"));
	     this.setCodeOfB0110((String)this.getFormHM().get("codeOfB0110"));
	     this.setCodeOfE0122((String)this.getFormHM().get("codeOfE0122"));
	     this.setCodeOfE01a1((String)this.getFormHM().get("codeOfE01a1"));
	     this.temptablemap=(HashMap)this.getFormHM().get("temptablemap");
	     this.setMsgList((ArrayList)this.getFormHM().get("msglist"));
	     this.setRepeatPrimaryKey((String)this.getFormHM().get("RepeatPrimaryKey"));
	     this.setOutName((String)this.getFormHM().get("outName"));
	     this.setNum((String)this.getFormHM().get("num"));
	     this.setReturns((String) this.getFormHM().get("returns"));
	     this.setA01desc((String)this.getFormHM().get("a01desc"));
	     this.setCondlist((ArrayList)this.getFormHM().get("condlist"));
	     this.setStock_cond((String)this.getFormHM().get("stock_cond"));
	     int len=0;
	     if(this.getFormHM().get("len")!=null){
	    	 len=((Integer)this.getFormHM().get("len")).intValue();
	     }
	     this.setLen(len);
	     Connection conn = null;
	        RowSet rs = null;
	        try {
	        	String buf = (String)this.getFormHM().get("strsql");
	        	if(buf!=null){
			        int index = buf.indexOf("from");
			        if(index!=-1){
			        	conn = com.hrms.frame.utility.AdminDb.getConnection();
						String sql ="select count("+(userbase==null||userbase.length()==0?"Usr":userbase)+"A01.a0100) count "+buf.substring(index);
						ContentDAO dao = new ContentDAO(conn);
						sql= PubFunc.keyWord_reback(sql);
						rs = dao.search(sql);
						if(rs.next()){
							int llen=rs.getInt("count");
							this.getFormHM().put("llen", new Integer(llen));
							this.setLlen(llen);
						}
			        }
	        	}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (GeneralException e) {
				e.printStackTrace();
			}finally{
				try {
					if(rs!=null)
						rs.close();
					if(conn!=null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
	     int llen=0;
	     if(this.getFormHM().get("llen")!=null){
	    	 llen=((Integer)this.getFormHM().get("llen")).intValue();
	     }
	     this.setLlen(llen);
	     
	     this.setError((String)this.getFormHM().get("error"));
	     this.setMessage((String)this.getFormHM().get("message"));
	     this.setMsg((String)this.getFormHM().get("msg"));
	     this.setFormationMsg((String)this.getFormHM().get("formationMsg"));
	     this.setIssameunique((String)this.getFormHM().get("issameunique"));
	     this.setRuleItemid((String)this.getFormHM().get("ruleItemid"));
	     this.setMultimediaSortFlag((String)this.getFormHM().get("multimediaSortFlag"));
	     this.setMultimediaName((String)this.getFormHM().get("multimediaName"));
	     this.setInfo((String)this.getFormHM().get("info"));
	     this.setLebal((String)this.getFormHM().get("lebal"));
	     this.setCodeid((String)this.getFormHM().get("codeid"));
	     this.setFielditemlist((ArrayList)this.getFormHM().get("fielditemlist"));
	     this.setLeadNext((String)this.getFormHM().get("leadNext"));
	     this.setAnalyserInfo((String)this.getFormHM().get("analyserInfo"));
	     
	     this.setRightlist((ArrayList)this.getFormHM().get("rightlist"));
	     this.setRight_fields((ArrayList)this.getFormHM().get("right_fields"));
	     this.setTouserbase((String) this.getFormHM().get("touserbase"));
	     this.setMultimedia_file_flag((String)this.getFormHM().get("multimedia_file_flag"));
	     this.setMultimediaFilelist((ArrayList)this.getFormHM().get("multimediaFilelist"));
	     this.setMulSetid((String)this.getFormHM().get("mulSetid"));
	     this.setMulFileItemlist((ArrayList)this.getFormHM().get("mulFileItemlist"));
	     this.setMulItemid((String)this.getFormHM().get("mulItemid"));
	     this.setGeneralsearch((String)this.getFormHM().get("generalsearch"));
	     x = (String)(this.getFormHM().get("x"));
	     y = (String)(this.getFormHM().get("y"));
	     width = (String)(this.getFormHM().get("width"));
	     height = (String)(this.getFormHM().get("height"));
	     photoType = (String)(this.getFormHM().get("photoType"));
	     this.setScale((String) this.getFormHM().get("scale"));
	     this.setDbvalue((String)this.getFormHM().get("dbvalue"));
	     
	     this.setSelfA0100((String)this.getFormHM().get("selfA0100"));
	     this.setSelfBase((String)this.getFormHM().get("selfBase"));
	     this.setVirAxx((String)this.getFormHM().get("virAxx"));
	     this.setNoExistsField((String)this.getFormHM().get("noExistsField"));
	     this.setMultimediaInfoList((ArrayList)this.getFormHM().get("multimediaInfoList"));
	     this.setRepeatMap((HashMap<String, HashMap<String,String>>)this.getFormHM().get("repeatMap"));
	     this.setStrId((String)this.getFormHM().get("strId"));
	     this.setIdType((String) this.getFormHM().get("idType"));
	     this.setIdTypeValue((String) this.getFormHM().get("idTypeValue"));
	     this.setCardflag((String) this.getFormHM().get("cardflag"));
	     this.setMainsort((String) this.getFormHM().get("mainsort"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("msg", "");
		this.getFormHM().put("formationMsg", formationMsg);
		this.getFormHM().put("isRecordEntry", isRecordEntry);
		// TODO Auto-generated method stub
		if (this.getSelfInfoForm() != null)
		this.getFormHM().put("selectedlist",(ArrayList)this.getSelfInfoForm().getSelectedList());
		if (this.getPage() != null)
		this.getFormHM().put("pageselectedlist",(ArrayList)this.getPage().getSelectedList());
		if(this.getPagination()!=null)
		   this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
		this.getFormHM().put("infofieldlist",infoFieldList);
		this.getFormHM().put("infosetlist",infoSetList);
		this.getFormHM().put("setname",setname);
		this.getFormHM().put("lowimage",lowimage);
		this.getFormHM().put("userbase",userbase);
		this.getFormHM().put("session",session);
		this.getFormHM().put("a0100",a0100);
		this.getFormHM().put("i9999",i9999);
		this.getFormHM().put("actiontype",actiontype);
		this.getFormHM().put("touserbase",touserbase);
		this.getFormHM().put("picturefile",this.getPicturefile());
		this.getFormHM().put("strexpression",strexpression);
		this.getFormHM().put("ordernum",this.ordernum);
		this.getFormHM().put("personsort",personsort);
	    this.getFormHM().put("orgtype",this.getOrgtype());
	    this.getFormHM().put("num_per_page",this.getNum_per_page());
	    this.getFormHM().put("inforVo",this.getInforVo());
	    this.getFormHM().put("chg_id",this.getChg_id());
	    this.getFormHM().put("sp_flag",this.getSp_flag());
	    this.getFormHM().put("viewitem",this.getViewitem());
	    this.getFormHM().put("allflag",this.getAllflag());
	    this.getFormHM().put("instructions",this.getInstructions());
	    this.getFormHM().put("newFieldList",this.getNewFieldList());
	    this.getFormHM().put("itemlist",this.getItemlist());
	    if(this.getPagination()!=null){
	    	this.getFormHM().put("sel_update_info",(ArrayList)this.getPagination().getSelectedList());
		}
	    this.getFormHM().put("check", this.getCheck());
	    this.getFormHM().put("select_name",select_name);
	    this.getFormHM().put("returnvalue", this.getReturnvalue());
	    this.getFormHM().put("expr", this.getExpr());
        this.getFormHM().put("factor", this.getFactor());
        this.getFormHM().put("orgparentcode", this.orgparentcode);
        this.getFormHM().put("deptparentcode", this.deptparentcode);
        this.getFormHM().put("posparentcode", this.posparentcode);
        this.getFormHM().put("code", code);
        this.getFormHM().put("kind", this.getKind());
        this.getFormHM().put("querylike", this.getQuerylike());       
        this.getFormHM().put("query", this.getQuery());      
		this.getFormHM().put("orglike", this.getOrglike());
		this.getFormHM().put("queryfieldlist", this.getQueryfieldlist());
		this.getFormHM().put("createtimestart", createtimestart);
		this.getFormHM().put("createtimeend", createtimeend);
		this.getFormHM().put("createusername", createusername);
		this.setIsAppEdite("0");
		this.getFormHM().remove("isAppEdite");
		Map map = (Map) this.getFormHM().get("requestPamaHM");
		if (map.get("isAppEdite") != null) {
			this.setIsAppEdite((String)map.get("isAppEdite"));
			((Map) this.getFormHM().get("requestPamaHM")).remove("isAppEdite");
		}
		this.getFormHM().put("isAppEdite", this.getIsAppEdite());
		
		this.setIsDraft("0");
		if (map.get("isDraft") != null) {
			this.setIsDraft((String)map.get("isDraft"));
			((Map) this.getFormHM().get("requestPamaHM")).remove("isDraft");
		}
		this.getFormHM().put("isDraft", this.getIsDraft());
		this.setPageCurrent(this.getPage().getPagination().getCurrent());
		if(map.get("isBrowse") == null){
			this.isBrowse = "";
		} else {
			this.setIsBrowse((String) map.get("isBrowse"));
		}
		this.getFormHM().put("isBrowse", this.getIsBrowse());
		
		this.getFormHM().put("fieldsetid", fieldsetid);
		this.getFormHM().put("fieldsetdesc", fieldsetdesc);
		this.getFormHM().put("isupdate", isupdate);
		this.getFormHM().put("selectitems", selectitems);
		this.getFormHM().put("seconditems", seconditems);
		this.getFormHM().put("file", file);
		this.getFormHM().put("maps", maps);
		this.getFormHM().put("mapsList", mapsList);
		this.getFormHM().put("unusekey", unusekey);
		this.getFormHM().put("A01primarykeys", A01primarykeys);
		this.getFormHM().put("a0100s", a0100s);
		this.getFormHM().put("updatestr", updatestr);
		this.getFormHM().put("bb0110", bb0110);
		this.getFormHM().put("be0122", be0122);
		this.getFormHM().put("be01a1", be01a1);
		this.getFormHM().put("codeOfB0110", codeOfB0110);
		this.getFormHM().put("codeOfE0122", codeOfE0122);
		this.getFormHM().put("codeOfE01a1", codeOfE01a1);
		this.getFormHM().put("temptablemap", temptablemap);
		this.getFormHM().put("msglist", msgList);
		this.getFormHM().put("RepeatPrimaryKey", RepeatPrimaryKey);
		this.getFormHM().put("stock_cond", this.getStock_cond());
		this.getFormHM().put("likeflag", likeflag);
		this.getFormHM().put("b0110", b0110);
		this.getFormHM().put("leadNext", leadNext);
		this.getFormHM().put("emp_e", emp_e);
		this.getFormHM().put("link_field", link_field);
		this.getFormHM().put("flag", flag);
		this.getFormHM().put("b0110field", b0110field);
		this.getFormHM().put("orderbyfield", orderbyfield);
		this.getFormHM().put("org_m", org_m);
		this.getFormHM().put("history", this.getHistory());
		this.getFormHM().put("cond_str", cond_str);
		this.getFormHM().put("strsql", strsql);
		this.getFormHM().put("ruleItemid", this.getRuleItemid());
		this.getFormHM().put("info", this.getInfo());
		this.getFormHM().put("multimediaSortFlag", this.getMultimediaSortFlag());
		this.getFormHM().put("multimediaName", this.getMultimediaName());
		this.getFormHM().put("multimedia_maxsize", this.getMultimedia_maxsize());
		this.getFormHM().put("batchImportType", this.getBatchImportType());
		this.getFormHM().put("lebal", lebal);
		this.getFormHM().put("codeid", codeid);
		this.getFormHM().put("fielditemlist", fielditemlist);
		this.getFormHM().put("leaderTypeValue", leaderTypeValue);
		this.getFormHM().put("sessionValue", sessionValue);
		
		this.getFormHM().put("right_fields", this.getRight_fields());
		this.getFormHM().put("rightlist", this.getRightlist());
		this.getFormHM().put("multimedia_file_flag", this.getMultimedia_file_flag());
		this.getFormHM().put("multimediaFilelist", this.getMultimediaFilelist());
		this.getFormHM().put("mulSetid", this.getMulSetid());
		this.getFormHM().put("mulFileItemlist", this.getMulFileItemlist());
		this.getFormHM().put("mulItemid", this.getMulItemid());
		this.getFormHM().put("generalsearch", this.getGeneralsearch());
		this.getFormHM().put("x", x);
		this.getFormHM().put("y", y);
		this.getFormHM().put("width", width);
		this.getFormHM().put("height", height);
		this.getFormHM().put("photoType", photoType);
		this.getFormHM().put("scale", this.getScale());
		this.getFormHM().put("photo_maxsize", this.getPhoto_maxsize());
		this.getFormHM().put("virAxx", this.getVirAxx());
		this.getFormHM().put("noExistsField", this.getNoExistsField());
		this.getFormHM().put("multimediaInfoList", this.getMultimediaInfoList());
		this.getFormHM().put("repeatMap", this.getRepeatMap());
		this.getFormHM().put("strId", this.getStrId());
		this.getFormHM().put("idType", this.getIdType());
		this.getFormHM().put("idTypeValue", this.getIdTypeValue());
		this.getFormHM().put("cardflag", this.getCardflag());
		this.getFormHM().put("mainsort", this.getMainsort());
	}

	public String getPhotoType() {
		return photoType;
	}
	public void setPhotoType(String photoType) {
		this.photoType = photoType;
	}
	/**
	 * @return Returns the infoFieldList.
	 */
	public List getInfoFieldList() {
		return infoFieldList;
	}
	/**
	 * @param infoFieldList The infoFieldList to set.
	 */
	public void setInfoFieldList(List infoFieldList) {
		this.infoFieldList = infoFieldList;
	}


	/**
	 * @return Returns the infoSetList.
	 */
	public List getInfoSetList() {
		return infoSetList;
	}
	/**
	 * @param infoSetList The infoSetList to set.
	 */
	public void setInfoSetList(List infoSetList) {
		this.infoSetList = infoSetList;
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
	/**
	 * @return Returns the selfInfoForm.
	 */
	public PaginationForm getSelfInfoForm() {
		return selfInfoForm;
	}
	/**
	 * @param selfInfoForm The selfInfoForm to set.
	 */
	public void setSelfInfoForm(PaginationForm selfInfoForm) {
		this.selfInfoForm = selfInfoForm;
	}
	/**
	 * @return Returns the picturefile.
	 */
	public FormFile getPicturefile() {
		return picturefile;
	}
	/**
	 * @param picturefile The picturefile to set.
	 */
	public void setPicturefile(FormFile picturefile) {
		this.picturefile = picturefile;
	}
	   @Override
       public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	   {
		   if("/workbench/info/searchselfdetailinfo".equals(arg0.getPath())&&arg1.getParameter("b_searchsort")!=null)
	        {
		       //查看子集信息时，每次进入显示子集信息的页面都强制跳转到第一页    chenxg  2016-11-03
		       if(!"refresh".equalsIgnoreCase(arg1.getParameter("b_searchsort")))
		           this.current = 1;
//			   if(this.num_per_page!=null&&this.num_per_page.length()>0)
//		    	 this.pagerows=Integer.parseInt(num_per_page);
	        }
		   if("/selfservice/selfinfo/addselfinfo".equals(arg0.getPath())&&arg1.getParameter("b_add")!=null&&"infoself".equals(arg1.getParameter("flag")))
	        {
			   this.a0100="A0100";
			   UserView userView = (UserView)arg1.getSession().getAttribute("userView");
			   userbase = userView.getDbname();
			   this.getFormHM().put("a0100", this.a0100);
			   this.getFormHM().put("userbase", this.userbase);
	        }
		   if("/selfservice/selfinfo/editselfinfo".equals(arg0.getPath())&&arg1.getParameter("b_edit")!=null/*&&"infoself".equals(arg1.getParameter("flag"))*/)
	        {
			   this.a0100="A0100";
			   UserView userView = (UserView)arg1.getSession().getAttribute("userView");
			   userbase = userView.getDbname();
			   this.getFormHM().put("a0100", this.a0100);
			   this.getFormHM().put("userbase", this.userbase);
	        }
		   if("/selfservice/selfinfo/searchselfdetailinfo".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null&&"infoself".equals(arg1.getParameter("flag")))
	        {
			   this.a0100="A0100";
			   UserView userView = (UserView)arg1.getSession().getAttribute("userView");
			   userbase = userView.getDbname();
			   this.getFormHM().put("a0100", this.a0100);
			   this.getFormHM().put("userbase", this.userbase);
	        }
	        if("/workbench/info/showinfodata".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	        {
	            if(this.getPagination()!=null && "link".equals(arg1.getParameter("b_search")))
	                this.getPagination().firstPage();//?
	                
	            this.setIsShowCondition("none");
	            this.getFormHM().put("isShowCondition", "none");
	            //【6942】员工管理-信息维护-记录录入（系统其它地方都是每页显示20条数据，这块显示的是21条） jingq upd 2015.01.23
	            //问题 21741 记录录入主界面调整显示数量后，点击修改员工信息进入后再返回，会还原成初始每页显示2 0人状态，点击具体部门查看人员也会还原回每页20人状态。wup upd 2016.08.16
	            //this.pagerows=20;
	            /*this.getFormHM().put("query", "");
	        	this.setQuery("");
	        	this.getFormHM().put("querylike", "");
	        	this.setQuerylike("");
	        	this.setQueryfieldlist(null);
	        	this.getFormHM().put("queryfieldlist", null);*/
	        }	
	        if("/workbench/info/showinfodata".equals(arg0.getPath())&&arg1.getParameter("b_queryinfo")!=null)
	        {
	        	//问题 21741 记录录入主界面调整显示数量后，点击修改员工信息进入后再返回，会还原成初始每页显示2 0人状态，点击具体部门查看人员也会还原回每页20人状态。wup upd 2016.08.16
	        	//this.pagerows=21;
	        }
	        if("/workbench/info/showinfodata".equals(arg0.getPath())&&arg1.getParameter("b_searchinfo")!=null)
	        {
	        	if(this.getPagination()!=null)
		              this.getPagination().firstPage();//?	        	
	        }
	        if("/selfservice/selfinfo/appEditselfinfo".equals(arg0.getPath())&&arg1.getParameter("b_defendother")!=null&&arg1.getParameter("current")!=null)
	        {
	        	if(this.page.getPagination()!=null)
		              this.page.getPagination().firstPage();
	        	this.pageCurrent = this.page.getPagination().getCurrent();
	        }
	       
	        if("/workbench/info/addinfo/add".equals(arg0.getPath())&&arg1.getParameter("b_searchdetail")!=null)
	        {
	            if(this.selfInfoForm.getPagination()!=null)
	            	this.selfInfoForm.getPagination().firstPage();//?
	        }	
	       //br_return 和tolastpageflag 表示主集添加的返回到最后页
	       //b_returnsub 和tolastpageflagsub表示子集的添加的返回到最后页
	       //b_savesub和tolastpageflagsub表示保存子集的添加纪录并返回到最后页
	        if("/workbench/info/editselfinfo".equals(arg0.getPath())&&(arg1.getParameter("br_return")!=null  && this.tolastpageflag!=null && "yes".equals(this.tolastpageflag)))
	        {
	            /*if(this.getPagination()!=null)
	              this.getPagination().lastPage();//?
*/	        }	
	        if("/workbench/info/addinfo/add".equals(arg0.getPath())&&(arg1.getParameter("b_savesub")!=null))
	        {
	            if(this.selfInfoForm.getPagination()!=null)
	            	this.selfInfoForm.getPagination().lastPage();//?
	            current=this.selfInfoForm.getPagination().getCurrent();
	        }	
	        if("/workbench/info/addinfo/add".equals(arg0.getPath())&&(arg1.getParameter("b_add")!=null))
	        {
	            
	        }
	        
	        //add by wangchaoqun on 20140820     为标志位赋值
	        if("/workbench/info/addinfo/add".equals(arg0.getPath())&&(arg1.getParameter("b_save_add")!=null))
	        {
	            this.setIs_save_add(true);      
//	            this.getFormHM().put("is_save_add", this.getIs_save_add());
	        }
	        
	        if("/workbench/info/editselfinfo".equals(arg0.getPath()) &&
	        		((arg1.getParameter("b_returnsub")!=null && this.tolastpageflagsub!=null && "yes".equals(this.tolastpageflagsub)) ||
					(arg1.getParameter("b_savesub")!=null) && this.tolastpageflagsub!=null && "yes".equals(this.tolastpageflagsub)))
	        {
	        	if(this.selfInfoForm.getPagination()!=null)
	        	 this.selfInfoForm.getPagination().lastPage();
	        	 current=this.selfInfoForm.getPagination().getCurrent();
	        }  
	        if("/workbench/info/searchselfdetailinfo".equals(arg0.getPath()) &&
	        		arg1.getParameter("b_delete")!=null)
	        {
	        	if(this.selfInfoForm.getPagination()!=null)
	        	  current=this.selfInfoForm.getPagination().getCurrent();
	        }
	        if("/workbench/info/showinfo".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
	        {
	            if(this.getPagination()!=null)
	              this.getPagination().firstPage();//?
	            this.getFormHM().clear();
	            this.getFormHM().put("select_name","");
	            this.setSelect_name("");
	            this.getFormHM().put("p_select_name", "");       
	            this.getFormHM().put("check","");
	            this.setCheck("");
	            this.setIsShowCondition("none");
	            this.getFormHM().put("isShowCondition", "none");
	            this.getFormHM().put("query", "");
	        	this.setQuery("");
	        	this.getFormHM().put("querylike", "");
	        	this.setQuerylike("");
	        	
	        }
	        if("/selfservice/selfinfo/inforchange".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
	            /**定位到首页,*/
	            if(this.getPagination()!=null)
	            	this.getPagination().firstPage();              
	        }
	        if("/workbench/info/showinfo".equals(arg0.getPath())) {
	        	if(arg1.getParameter("b_searchsort")!=null) {
	        		if(this.getPagination()!=null)
	        			this.getPagination().firstPage();//?
	        		this.getFormHM().clear();
	        		this.getFormHM().put("select_name","");
	        		this.setSelect_name("");
	        		this.getFormHM().put("p_select_name", "");       
	        		this.getFormHM().put("check","");
	        		this.setCheck("");	            
	        		this.setQueryfieldlist(null);
	        		this.getFormHM().put("queryfieldlist", null);
	        		this.getFormHM().put("createtimestart", null);
	        		this.getFormHM().put("createtimeend", null);
	        		this.getFormHM().put("createusername", null);
	        		if ("dxt".equalsIgnoreCase(arg1.getParameter("returnvalue"))) {
	        			this.getFormHM().put("returns", "1");
	        		} else {
	        			this.getFormHM().put("returns", "0");
	        		}
	        		this.setOrglike("1");
	        		this.getFormHM().put("orglike", "1");
	        	} else if(arg1.getParameter("b_search")!=null) {
	        		if ("dxt".equalsIgnoreCase(arg1.getParameter("returnvalue"))) {
	        			this.getFormHM().put("returns", "1");
	        		} else {
	        			this.getFormHM().put("returns", "0");
	        		}
	        		
	        		if (arg1.getParameter("returnvalue1") == null)
	        			this.setReturnvalue1("");
	        	}
	            //this.pagerows=21;
	            //问题 21741 记录录入主界面调整显示数量后，点击修改员工信息进入后再返回，会还原成初始每页显示2 0人状态，点击具体部门查看人员也会还原回每页20人状态。wup upd 2016.08.16
	            //bug 29048  要求记录每页显示人数
//	            this.pagerows=20;
	        }
	        if("/workbench/info/showinfodata1".equals(arg0.getPath())&&arg1.getParameter("b_importdata")!=null){
	            /**定位到首页,*/
	            if(this.getMsgPageForm().getPagination()!=null)
	            	this.getMsgPageForm().getPagination().firstPage();              
	        }
	        if("/workbenck/info/upphotoinfo".equals(arg0.getPath())&&arg1.getParameter("b_photosearch")!=null){
	        	if(this.getScale()!=null){
	        		this.getFormHM().put("scale", "");
	        	}
	        }
	    /*	try
		{
	   	    session=arg1.getSession();
	   		if(this.getPicturefile()!=null && arg0.getPath().equals("/workbench/info/upphotoinfo") && arg1.getParameter("b_save")!=null)
		       new StructureExecSqlString().PictureInsert(this.getUserbase() + "A00",this.getA0100(),this.getPicturefile()); 	   	
	   		
		}catch(Exception e)
		{
	   		e.printStackTrace();
	   	}*/
         return super.validate(arg0, arg1);
	    }
	/**
	 * @return Returns the todbcond.
	 */
	public String getTodbcond() {
		return todbcond;
	}
	/**
	 * @param todbcond The todbcond to set.
	 */
	public void setTodbcond(String todbcond) {
		this.todbcond = todbcond;
	}
	/**
	 * @return Returns the deptparentcode.
	 */
	public String getDeptparentcode() {
		return deptparentcode;
	}
	/**
	 * @param deptparentcode The deptparentcode to set.
	 */
	public void setDeptparentcode(String deptparentcode) {
		this.deptparentcode = deptparentcode;
	}
	/**
	 * @return Returns the orgparentcode.
	 */
	public String getOrgparentcode() {
		return orgparentcode;
	}
	/**
	 * @param orgparentcode The orgparentcode to set.
	 */
	public void setOrgparentcode(String orgparentcode) {
		this.orgparentcode = orgparentcode;
	}
	/**
	 * @return Returns the posparentcode.
	 */
	public String getPosparentcode() {
		return posparentcode;
	}
	/**
	 * @param posparentcode The posparentcode to set.
	 */
	public void setPosparentcode(String posparentcode) {
		this.posparentcode = posparentcode;
	}
	/**
	 * @return Returns the actiontype.
	 */
	public String getActiontype() {
		return actiontype;
	}
	/**
	 * @param actiontype The actiontype to set.
	 */
	public void setActiontype(String actiontype) {
		this.actiontype = actiontype;
	}
	public String getStrexpression() {
		return strexpression;
	}
	public void setStrexpression(String strexpression) {
		this.strexpression = strexpression;
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
	public int getCurrent() {
		return current;
	}
	public void setCurrent(int current) {
		this.current = current;
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
	public String getOrdernum() {
		return ordernum;
	}
	public void setOrdernum(String ordernum) {
		this.ordernum = ordernum;
	}
	public ArrayList getPersonsortlist() {
		return this.personsortlist;
	}
	public void setPersonsortlist(ArrayList personsortlist) {
		this.personsortlist = personsortlist;
	}
	public String getPersonsort() {
		return this.personsort;
	}
	public void setPersonsort(String personsort) {
		this.personsort = personsort;
	}
	public String getAgefield() {
		return this.agefield;
	}
	public void setAgefield(String agefield) {
		this.agefield = agefield;
	}
	public String getBirthdayfield() {
		return this.birthdayfield;
	}
	public void setBirthdayfield(String birthdayfield) {
		this.birthdayfield = birthdayfield;
	}
	public String getIdcardfield() {
		return this.idcardfield;
	}
	public void setIdcardfield(String idcardfield) {
		this.idcardfield = idcardfield;
	}
	public String getPostagefield() {
		return this.postagefield;
	}
	public void setPostagefield(String postagefield) {
		this.postagefield = postagefield;
	}
	public String getStartpostfield() {
		return this.startpostfield;
	}
	public void setStartpostfield(String startpostfield) {
		this.startpostfield = startpostfield;
	}
	public String getWorkagefield() {
		return this.workagefield;
	}
	public void setWorkagefield(String workagefield) {
		this.workagefield = workagefield;
	}
	public String getWorkdatefield() {
		return this.workdatefield;
	}
	public void setWorkdatefield(String workdatefield) {
		this.workdatefield = workdatefield;
	}
	public String getAxfield() {
		return this.axfield;
	}
	public void setAxfield(String axfield) {
		this.axfield = axfield;
	}
	public String getStd() {
		return std;
	}
	public void setStd(String std) {
		this.std = std;
	}
	public String getSortfield() {
		return sortfield;
	}
	public void setSortfield(String sortfield) {
		this.sortfield = sortfield;
	}
	public String getPdbflag() {
		return pdbflag;
	}
	public void setPdbflag(String pdbflag) {
		this.pdbflag = pdbflag;
	}
	public String getCitem() {
		return citem;
	}
	public void setCitem(String citem) {
		this.citem = citem;
	}
	public String getCset() {
		return cset;
	}
	public void setCset(String cset) {
		this.cset = cset;
	}
	public String getRownums() {
		return rownums;
	}
	public void setRownums(String rownums) {
		if(rownums==null|| "".equals(rownums))
			rownums="1";
		this.rownums = rownums;
	}
	public String getApproveflag() {
		return approveflag;
	}
	public void setApproveflag(String approveflag) {
		if(approveflag==null||approveflag.length()<1)
			approveflag="0";
		this.approveflag = approveflag;
	}
	public String getWriteable() {
		return writeable;
	}
	public void setWriteable(String writeable) {
		this.writeable = writeable;
	}
	public String getNum_per_page() {
		return num_per_page;
	}
	public void setNum_per_page(String num_per_page) {
		if(num_per_page==null|| "".equals(num_per_page)){
			num_per_page="20";
		}
		this.num_per_page = num_per_page;
	}
	public String getEmp_cardId() {
		return emp_cardId;
	}
	public void setEmp_cardId(String emp_cardId) {
		this.emp_cardId = emp_cardId;
	}
	public String getInputchinfor() {
		return inputchinfor;
	}
	public void setInputchinfor(String inputchinfor) {
		this.inputchinfor = inputchinfor;
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public ArrayList getItemlist() {
		return itemlist;
	}
	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}
	public String getSetid() {
		return setid;
	}
	public void setSetid(String setid) {
		this.setid = setid;
	}
	public RecordVo getInforVo() {
		return inforVo;
	}
	public void setInforVo(RecordVo inforVo) {
		this.inforVo = inforVo;
	}
	public ArrayList getKeylist() {
		return keylist;
	}
	public void setKeylist(ArrayList keylist) {
		this.keylist = keylist;
	}
	public String getKeyid() {
		return keyid;
	}
	public void setKeyid(String keyid) {
		this.keyid = keyid;
	}
	public String getChg_id() {
		return chg_id;
	}
	public void setChg_id(String chg_id) {
		this.chg_id = chg_id;
	}
	public String getSp_flag() {
		return sp_flag;
	}
	public void setSp_flag(String sp_flag) {
		this.sp_flag = sp_flag;
	}

	public String getSflag() {
		return sflag;
	}
	public void setSflag(String sflag) {
		this.sflag = sflag;
	}
	public ArrayList getOldFieldList() {
		return oldFieldList;
	}
	public void setOldFieldList(ArrayList oldFieldList) {
		this.oldFieldList = oldFieldList;
	}
	public String getViewitem() {
		return viewitem;
	}
	public void setViewitem(String viewitem) {
		this.viewitem = viewitem;
	}
	public String getAllflag() {
		return allflag;
	}
	public void setAllflag(String allflag) {
		this.allflag = allflag;
	}
	public String getInstructions() {
		return instructions;
	}
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	public String getChecksave() {
		return checksave;
	}
	public void setChecksave(String checksave) {
		this.checksave = checksave;
	}
	public ArrayList getNewFieldList() {
		return newFieldList;
	}
	public void setNewFieldList(ArrayList newFieldList) {
		this.newFieldList = newFieldList;
	}
	public String getTypeid() {
		return typeid;
	}
	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}
	public ArrayList getTypelist() {
		return typelist;
	}
	public void setTypelist(ArrayList typelist) {
		this.typelist = typelist;
	}
	public ArrayList getSpflaglist() {
		return spflaglist;
	}
	public void setSpflaglist(ArrayList spflaglist) {
		this.spflaglist = spflaglist;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
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
	public String getUserpriv() {
		return userpriv;
	}
	public void setUserpriv(String userpriv) {
		this.userpriv = userpriv;
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
	public String getSequenceid() {
		return sequenceid;
	}
	public void setSequenceid(String sequenceid) {
		this.sequenceid = sequenceid;
	}
	public ArrayList getSequenceList() {
		return sequenceList;
	}
	public void setSequenceList(ArrayList sequenceList) {
		this.sequenceList = sequenceList;
	}
	public String getViewbutton() {
		return viewbutton;
	}
	public void setViewbutton(String viewbutton) {
		this.viewbutton = viewbutton;
	}
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
	public String getCheck() {
		return check;
	}
	public void setCheck(String check) {
		this.check = check;
	}
	public String getPhoto_maxsize() {
		return photo_maxsize;
	}
	public void setPhoto_maxsize(String photo_maxsize) {
		this.photo_maxsize = photo_maxsize;
	}
	
	public String getBatchImportType() {
		return batchImportType;
	}
	public void setBatchImportType(String batchImportType) {
		this.batchImportType = batchImportType;
	}
	public String getMultimedia_maxsize() {
		return multimedia_maxsize;
	}
	public void setMultimedia_maxsize(String multimediaMaxsize) {
		multimedia_maxsize = multimediaMaxsize;
	}
	public String getSelect_name() {
		return select_name;
	}
	public void setSelect_name(String select_name) {
		this.select_name = select_name;
	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
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
	public String getPart_pos() {
		return part_pos;
	}
	public void setPart_pos(String part_pos) {
		this.part_pos = part_pos;
	}
	public String getIsAppEdite() {
		return isAppEdite;
	}
	public void setIsAppEdite(String isAppEdite) {
		this.isAppEdite = isAppEdite;
	}
	public String getIsAdvance() {
		return isAdvance;
	}
	public void setIsAdvance(String isAdvance) {
		this.isAdvance = isAdvance;
	}
	public PaginationForm getPage() {
		return page;
	}
	public void setPage(PaginationForm page) {
		this.page = page;
	}
	public int getPageCurrent() {
		return pageCurrent;
	}
	public void setPageCurrent(int pageCurrent) {
		this.pageCurrent = pageCurrent;
	}
	public String getIsDraft() {
		return isDraft;
	}
	public void setIsDraft(String isDraft) {
		this.isDraft = isDraft;
	}
	public String getIsBrowse() {
		return isBrowse;
	}
	public void setIsBrowse(String isBrowse) {
		this.isBrowse = isBrowse;
	}
	public String getIsAble() {
		return isAble;
	}
	public void setIsAble(String isAble) {
		this.isAble = isAble;
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
	public ArrayList getFieldSetDataList() {
		return fieldSetDataList;
	}
	public void setFieldSetDataList(ArrayList fieldSetDataList) {
		this.fieldSetDataList = fieldSetDataList;
	}
	public String getFieldsetid() {
		return fieldsetid;
	}
	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}
	public String getIsupdate() {
		return isupdate;
	}
	public void setIsupdate(String isupdate) {
		this.isupdate = isupdate;
	}
	public String getSeconditems() {
		return seconditems;
	}
	public void setSeconditems(String seconditems) {
		this.seconditems = seconditems;
	}
	public String getSelectitems() {
		return selectitems;
	}
	public void setSelectitems(String selectitems) {
		this.selectitems = selectitems;
	}
	public FormFile getFile() {
		return file;
	}
	public void setFile(FormFile file) {
		this.file = file;
	}
	public PaginationForm getMsgPageForm() {
		return msgPageForm;
	}
	public void setMsgPageForm(PaginationForm msgPageForm) {
		this.msgPageForm = msgPageForm;
	}
	public Object[] getMaps() {
		return maps;
	}
	public void setMaps(Object[] maps) {
		this.maps = maps;
	}
	public ArrayList getMapsList() {
	    return mapsList;
	}
	public void setMapsList(ArrayList mapsList) {
	    this.mapsList = mapsList;
	}
	
	public String getUpdatestr() {
		return updatestr;
	}
	public void setUpdatestr(String updatestr) {
		this.updatestr = updatestr;
	}
	public HashMap getUnusekey() {
		return unusekey;
	}
	public void setUnusekey(HashMap unusekey) {
		this.unusekey = unusekey;
	}
	
	public StringBuffer getA01primarykeys() {
        return A01primarykeys;
    }
    public void setA01primarykeys(StringBuffer a01primarykeys) {
        A01primarykeys = a01primarykeys;
    }
    public String getPrimarykeyLabel() {
		return primarykeyLabel;
	}
	public void setPrimarykeyLabel(String primarykeyLabel) {
		this.primarykeyLabel = primarykeyLabel;
	}
	public String getBb0110() {
		return bb0110;
	}
	public void setBb0110(String bb0110) {
		this.bb0110 = bb0110;
	}
	public String getBe0122() {
		return be0122;
	}
	public void setBe0122(String be0122) {
		this.be0122 = be0122;
	}
	public String getBe01a1() {
		return be01a1;
	}
	public void setBe01a1(String be01a1) {
		this.be01a1 = be01a1;
	}
	public ArrayList getMsgList() {
		return msgList;
	}
	public void setMsgList(ArrayList msgList) {
		this.msgList = msgList;
	}
	public String getOutName() {
		return outName;
	}
	public void setOutName(String outName) {
		this.outName = outName;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getReturnvalue1() {
		return returnvalue1;
	}
	public void setReturnvalue1(String returnvalue1) {
		this.returnvalue1 = returnvalue1;
	}
	public String getA01desc() {
		return a01desc;
	}
	public void setA01desc(String a01desc) {
		this.a01desc = a01desc;
	}
	public String getLikeflag() {
		return likeflag;
	}
	public void setLikeflag(String likeflag) {
		this.likeflag = likeflag;
	}
	public String getEmp_e() {
		return emp_e;
	}
	public void setEmp_e(String emp_e) {
		this.emp_e = emp_e;
	}
	public String getLink_field() {
		return link_field;
	}
	public void setLink_field(String link_field) {
		this.link_field = link_field;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getB0110field() {
		return b0110field;
	}
	public void setB0110field(String b0110field) {
		this.b0110field = b0110field;
	}
	public String getOrderbyfield() {
		return orderbyfield;
	}
	public void setOrderbyfield(String orderbyfield) {
		this.orderbyfield = orderbyfield;
	}
	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}
	public String getOrg_m() {
		return org_m;
	}
	public void setOrg_m(String org_m) {
		this.org_m = org_m;
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
	public int getLlen() {
		return llen;
	}
	public void setLlen(int llen) {
		this.llen = llen;
	}
	public String getCreatetimestart() {
		return createtimestart;
	}
	public void setCreatetimestart(String createtimestart) {
		this.createtimestart = createtimestart;
	}
	public String getCreatetimeend() {
		return createtimeend;
	}
	public void setCreatetimeend(String createtimeend) {
		this.createtimeend = createtimeend;
	}
	public String getCreateusername() {
		return createusername;
	}
	public void setCreateusername(String createusername) {
		this.createusername = createusername;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getFenlei_priv() {
		return fenlei_priv;
	}
	public void setFenlei_priv(String fenlei_priv) {
		this.fenlei_priv = fenlei_priv;
	}
	public HashMap getPart_map() {
		return part_map;
	}
	public void setPart_map(HashMap part_map) {
		this.part_map = part_map;
	}
	public String getIssameunique() {
		return issameunique;
	}
	public void setIssameunique(String issameunique) {
		this.issameunique = issameunique;
	}
	public String getFormationMsg() {
		return formationMsg;
	}
	public void setFormationMsg(String formationMsg) {
		this.formationMsg = formationMsg;
	}
	public String getFieldsetdesc() {
		return fieldsetdesc;
	}
	public void setFieldsetdesc(String fieldsetdesc) {
		this.fieldsetdesc = fieldsetdesc;
	}
	public String getCodeid() {
		return codeid;
	}
	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}
	public ArrayList getFielditemlist() {
		return fielditemlist;
	}
	public void setFielditemlist(ArrayList fielditemlist) {
		this.fielditemlist = fielditemlist;
	}
	public String getLeadNext() {
		return leadNext;
	}
	public void setLeadNext(String leadNext) {
		this.leadNext = leadNext;
	}
	public String getAnalyserInfo() {
		return analyserInfo;
	}
	public void setAnalyserInfo(String analyserInfo) {
		this.analyserInfo = analyserInfo;
	}
	public String getLeaderTypeValue() {
		return leaderTypeValue;
	}
	public void setLeaderTypeValue(String leaderTypeValue) {
		this.leaderTypeValue = leaderTypeValue;
	}
	public String getSessionValue() {
		return sessionValue;
	}
	public void setSessionValue(String sessionValue) {
		this.sessionValue = sessionValue;
	}
	public ArrayList getRightlist() {
		return rightlist;
	}
	public void setRightlist(ArrayList rightlist) {
		this.rightlist = rightlist;
	}
	public ArrayList getRight_fields() {
		return right_fields;
	}
	public void setRight_fields(ArrayList rightFields) {
		right_fields = rightFields;
	}
	public String getRepeatPrimaryKey() {
		return RepeatPrimaryKey;
	}
	public void setRepeatPrimaryKey(String repeatPrimaryKey) {
		RepeatPrimaryKey = repeatPrimaryKey;
	}
	public String getCodeOfB0110() {
		return codeOfB0110;
	}
	public void setCodeOfB0110(String codeOfB0110) {
		this.codeOfB0110 = codeOfB0110;
	}
	public String getCodeOfE0122() {
		return codeOfE0122;
	}
	public void setCodeOfE0122(String codeOfE0122) {
		this.codeOfE0122 = codeOfE0122;
	}
	public String getCodeOfE01a1() {
		return codeOfE01a1;
	}
	public void setCodeOfE01a1(String codeOfE01a1) {
		this.codeOfE01a1 = codeOfE01a1;
	}
	public boolean isIs_save_add() {
		return is_save_add;
	}
	public void setIs_save_add(boolean is_save_add) {
		this.is_save_add = is_save_add;
	}
	@Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		//记录录入，批量导入照片，导入完成后清空表单中的文件  jingq add 2014.10.27
		this.setFile(null);
		super.reset(mapping, request);
	}
	public void setMultimediaFilelist(ArrayList multimediaFilelist) {
		this.multimediaFilelist = multimediaFilelist;
	}
	public ArrayList getMultimediaFilelist() {
		return multimediaFilelist;
	}
	public void setMulFileItemlist(ArrayList mulFileItemlist) {
		this.mulFileItemlist = mulFileItemlist;
	}
	public ArrayList getMulFileItemlist() {
		return mulFileItemlist;
	}
	public void setMulSetid(String mulSetid) {
		this.mulSetid = mulSetid;
	}
	public String getMulSetid() {
		return mulSetid;
	}
	public void setMulItemid(String mulItemid) {
		this.mulItemid = mulItemid;
	}
	public String getMulItemid() {
		return mulItemid;
	}
	public String getSelfA0100() {
		return selfA0100;
	}
	public void setSelfA0100(String selfA0100) {
		this.selfA0100 = selfA0100;
	}
	public String getSelfBase() {
		return selfBase;
	}
	public void setSelfBase(String selfBase) {
		this.selfBase = selfBase;
	}
	public String getIsRecordEntry() {
		return isRecordEntry;
	}
	public void setIsRecordEntry(String isRecordEntry) {
		this.isRecordEntry = isRecordEntry;
	}
    public String getVirAxx() {
        return virAxx;
    }
    public void setVirAxx(String virAxx) {
        this.virAxx = virAxx;
    }
    public String getNoExistsField() {
        return noExistsField;
    }
    public void setNoExistsField(String noExistsField) {
        this.noExistsField = noExistsField;
    }
    public ArrayList getMultimediaInfoList() {
        return multimediaInfoList;
    }
    public void setMultimediaInfoList(ArrayList multimediaInfoList) {
        this.multimediaInfoList = multimediaInfoList;
    }
	public HashMap<String, HashMap<String, String>> getRepeatMap() {
		return repeatMap;
	}
	public void setRepeatMap(HashMap<String, HashMap<String, String>> repeatMap) {
		this.repeatMap = repeatMap;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
    public String getStrId() {
        return strId;
    }
    public void setStrId(String strId) {
        this.strId = strId;
    }
    public String getMainsort() {
		return mainsort;
	}
	public void setMainsort(String mainsort) {
		this.mainsort = mainsort;
	}
}
