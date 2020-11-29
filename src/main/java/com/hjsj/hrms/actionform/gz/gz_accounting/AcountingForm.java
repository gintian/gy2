/**
 * 
 */
package com.hjsj.hrms.actionform.gz.gz_accounting;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 *<p>Title:薪资核算表单</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-6-29:下午02:42:04</p> 
 *@author cmq
 *@version 4.0
 */
public class AcountingForm extends FrameForm {
	private String priv="1";  //工资类别是否加权限控制
	
	private String royalty_valid="0"; //是否为提成工资
	/** 应用库标识 */
	private String nbase="";
	
	/**权限范围内的薪资类别列表*/
	private ArrayList setlist=new ArrayList();
	/**薪资类别分页控制*/
    private PaginationForm setlistform=new PaginationForm();
    /**薪资类别*/
    private String salaryid="-1";
    /**薪资项目列表*/
    private ArrayList fieldlist=new ArrayList();
    /**薪资表名称*/
    private String gz_tablename;
    /**薪资表别名*/
    private String gz_table_alias;
    /**数据过滤语句*/
    private String sql;
    /**组织机构代码*/
    private String a_code;
    /**薪资审批标识*/
    private String appflag="false";
    /**数据集是否可编辑,=true,=false薪资历史数据不可编辑,历史数据不让确认提交)*/
    private String subFlag="false";  /** 是否可以提交 */    
    private String bedit="true";
    private String salaryIsSubed="false";  //薪资是否为已提交状态
    private String spRowCanEditStatus=",02,07,";  //薪资审批记录可编辑状态 02,07,如允许提交后也可更改 则加上06
    private String gzRowCanEditStatus=",01,07,";  //薪资发放记录可编辑状态01,07,如允许提交后也可更改 则加上06； 走报审有报审状态控制。
    /**项目过滤列表*/
    private ArrayList itemlist=new ArrayList();
    /**条件过滤列表*/
    private ArrayList condlist=new ArrayList();
    /**项目过滤号和条件过滤号*/
    private String itemid;
    private String condid;
    /** 过滤条件--sql */
    private String filterWhl="";
    private String msg="";
    private String rowNums="";
    /** 最后工资发放日期*/
    private String finalDate=""; 
    
    private String isShowManagerFunction="1";   //是否显示工资管理员的操作功能（针对 共享的工资类别）
    private String isEditDate="true";              //是否可以通过菜单或按钮 修改薪资数据
    private String manager="";    //工资管理员
    private String isNotSpFlag2Records="0";     //是否有未报审的记录
    
    private String ff_bosdate="";//发放的业务日期
    private String ff_count="";  //发放的次数
    private String ff_setname=""; 
    private String sp=""; 
    /**
     *flow_flag,薪资审批控制
     *=1,取得需要审批的薪资类别
     *=0,取得全部薪资类别 
     */
    private String flow_flag;
    /**薪资和保险福利标志,默认为工资业务
     *保险福利为1 
     */
    private String gz_module="0";
    /**薪资审批属性*/
    /**处理的业务日期*/
    private String bosdate;
    /**处理的业务次数*/
    private String count;
    /**新的处理的业务日期-年份*/
    private String theyear;
    /**新的处理业务日期-月份*/
    private String themonth;    
    /**截止日期*/
    private String appdate;
    
    /**当前薪资类别处理过的业务日期列表*/
    private ArrayList datelist=new ArrayList();
    /**处理的业务日期对应的发放次数列表*/
    private ArrayList countlist=new ArrayList();

    /** 工资审批  */
    private String  approveObject="";
    private String    userid="";
    private String isAppealData="0";   // 0:无可报批的数据  1：有可报批的数据
    private String selectGzRecords="";
    private String rejectCause="";     //驳回原因
    private String sendMen="";  //工资批准发送消息对象
    private String isSendMessage="0";  //当前薪资类别是否发送消息
    /** 导入文件 */
    private FormFile file;
    private ArrayList originalDataList=new ArrayList();  //源数据
    private ArrayList aimDataList=new ArrayList();		 //目标数据
    private ArrayList sameDataList=new ArrayList();      //同号数据表头
    
    /**人员筛选SQL*/
    private String empfiltersql;

    /**设置薪资审批变动指标数组*/
	private String left_fields[];
	private ArrayList leftlist=new ArrayList(); 
	/**选中的薪资审批变动指标数组*/
	private String right_fields[]; 
	private ArrayList rightlist=new ArrayList();  	

	private String addleft_fields[];
	private String addright_fields[]; 
	private ArrayList addleftlist=new ArrayList();
	private ArrayList addrightlist=new ArrayList();
	private String delleft_fields[];
	private String delright_fields[]; 
	private ArrayList delleftlist=new ArrayList();
	private ArrayList delrightlist=new ArrayList();

    private ArrayList oriDataList=new ArrayList();		 //对应 或 同号数据
    private String[]  oppositeItem=null;				 //对应指标 
    private ArrayList oppositeItemList=new ArrayList();
    private String[]  relationItem=null;                 //关联指标
    private ArrayList relationItemList=new ArrayList();
    private String    schemeName="";    //对应关系表名称

    private ArrayList  tablenamelist= new ArrayList();    //信息变动表中文字段
    private ArrayList  tableidlist1= new ArrayList();    //信息变动表变化前字段id
    private ArrayList  tableidlist2= new ArrayList();    //信息变动表变化前字段id
    private ArrayList  totallist= new ArrayList();    //合计
    private ArrayList  varianceTotal= new ArrayList();    //差额合计
    /*
	 * 分页显示属性
	 */
	 private String sqlstr;
	 private String where;
	 private String column;
	 private String orderby;
	 
	 private String changeflag;// 0、信息变动，1、增加人员，2、减少人员

    /**人员排序list*/
    
	 
	 /*
	  * JinChunhai 新增
	  */
	private ArrayList itemSumList = new ArrayList();  //薪资项目
	private String  decwidth=""; 		//薪资项目合计
    
    /** 薪资报表 定义 */
    private String  salaryReportName="";
    private String  isPrintWithGroup="0";  //是否按分组指标分页打印  1：是  0：否
    private String  f_groupItem="";          //第一分组指标
    private ArrayList f_groupItemList=new ArrayList();  //第一分组指标列表
    private String  s_groupItem="";          //第二分组指标
    private ArrayList s_groupItemList=new ArrayList();  //第二分组指标列表
    private String  reportStyleID="";         //表类id
    private String  reportDetailID="";        //工资报表id
    


    private ArrayList sortemplist;
    	
    private String[] sortleft_field = null;	
    
    private String sort_table;
    
    private String sort_table_detail;
    /**项目过滤list*/
    private ArrayList profilterlist;
    /**新建项目左边数组*/
    private String[] proleft_field = null;	
    /**新建项目右边数组*/
    private String[] proright_fields = null;
    private ArrayList filterFieldList = new ArrayList();
    /**项目过滤保存字符串*/
    private String proright_str;
    /**项目过滤删除list*/
    private ArrayList del_filter_pro;
    /**项目过滤删除id*/
    private String del_pro_str;
    
    private String sortmode;
    private String flag; //判断显示/隐藏审批字段
    /**是否显示数据对比菜单*/
    private String priv_mode;//=0不显示,=1显示
    /**是否显示新增人员和减少人员信息*/
    private String isVisible="";//=1显示;=0不显示
    /**人员排序*/
    private String order_by;
    private String cond_id_str="";
    
    private String sp_ori="0";  //0:从审批模块进入审批界面   1:从首页公告栏进入审批界面
    
    private ArrayList shFormulaList = new ArrayList();
    private String shFormulaIds;
    private ArrayList shPersonList = new ArrayList();
    private String fileName;
    /**model=0是发放进入报表，=1是审批进入报表*/
    private String model;
    /**在工资审批进入报表时，将人员筛选，业务日期，发放次数和审批人的限制组合成一个sql传入APPLET控件*/
    private String reportSql;
    /**判断是工资审批的比对，还是工资发放的比对*/
    private String entry_type;
    //引入奖金
    private String isImportBonus = "0";
    //引入计件
    private String isImportPiece = "0";
    private String param_flag;//判断是进行的指标对比业务还是汇总指标业务
	 /**是否工资管理员=y是=n不是*/
    private String isSalaryManager;
    private String originalDataFile;//原始文件    
    
    private ArrayList appUserList=new ArrayList();  //上报人员列表
    private String    appUser="";                   //上报人员
    
    private ArrayList spFlagList=new ArrayList();  //人员状态列表
    private String    sp_flag="";
    private String returnFlag="0";//返回按钮的走向 0：返回薪资发放的类别界面 1：返回部门月奖金界面
    private String returnvalue="";
    private String operOrg;
    
    private String isTotalControl="0"; //是否进行总额控制
    private String verify_ctrl="0";  // //是否按审核条件控制
    private String subNoShowUpdateFashion="0";   //确认时不显示操作方式
    private String isHistory="0";
    private String chkid;//项目过滤id
    private String chkName;//
    private String scopeflag="1";				//1私有0共有
    private String operitems ="" ;				//存放不能删除，修改，重命名的过滤项
    private String hidenflag ="0";				//1表示自定义显示隐藏功能
    private ArrayList statelist = new ArrayList(); //人员状态和人数
    private String appprocess;					//审批过程
    /**查询指标列表*/
    private ArrayList queryFieldList = new ArrayList();
    
    private String returnflag="";
    private String errorFileName="";
    private String okCount="";
    private String isVisibleItem="";//是否显示引入单位部门变动人员菜单，如果设置了子集，或者有设置子集的权限就显示=1，否则不显示=0，
    
    
    private String showUnitCodeTree="0";   //是否按操作单位显示树
    
    
    
    private String sp_actor_str="";     //审批领导信息
    private String spActorName="";      //审批人名称
    private String relation_id="";
    
    private String collectPoint;
    private ArrayList collectList;
    private String user_=""; //薪资批准时通知人员
    private String user_h="";//薪资批准时通知人员
    private String isRedo="0"; //当前数据是否是重发数据  1：重发
    private ArrayList updateDateList=new ArrayList();
    //zgd 2015-1-21 add 薪资审批人员排序
    private String sort_table_approval;
    
	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getAppprocess() {
		return appprocess;
	}

	public void setAppprocess(String appprocess) {
		this.appprocess = appprocess;
	}

	public ArrayList getSetlist() {
		return setlist;
	}

	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}

	public String getScopeflag() {
		return scopeflag;
	}

	public void setScopeflag(String scopeflag) {
		this.scopeflag = scopeflag;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("updateDateList", this.updateDateList);
		this.getFormHM().put("isVisibleItem", this.getIsVisibleItem());
		this.getFormHM().put("itemSumList", this.getItemSumList());
		this.getFormHM().put("decwidth", this.getDecwidth());
		
	    this.getFormHM().put("queryFieldList", this.getQueryFieldList());
		this.getFormHM().put("chkName", this.getChkName());
		this.getFormHM().put("chkid", this.getChkid());
		this.getFormHM().put("filterFieldList", this.getFilterFieldList());
	    	this.getFormHM().put("operOrg",this.getOperOrg());
	    	this.getFormHM().put("returnFlag",this.getReturnFlag());
		this.getFormHM().put("sp_flag",this.getSp_flag());
		this.getFormHM().put("appUser",this.getAppUser());
		this.getFormHM().put("isSalaryManager", this.getIsSalaryManager());
		this.getFormHM().put("isImportBonus", this.getIsImportBonus());
		this.getFormHM().put("isImportPiece", this.getIsImportPiece());
		this.getFormHM().put("entry_type", this.getEntry_type());
		this.getFormHM().put("reportSql",this.getReportSql());
		this.getFormHM().put("model", this.getModel());
		this.getFormHM().put("shFormulaIds", this.getShFormulaIds());
		if(this.getPagination()!=null)
			this.getFormHM().put("selitem",(ArrayList)this.getPagination().getSelectedList());
		this.getFormHM().put("approveObject",this.getApproveObject());
		this.getFormHM().put("rejectCause",this.getRejectCause());
		this.getFormHM().put("sendMen",this.getSendMen());
		this.getFormHM().put("selectGzRecords",this.getSelectGzRecords());
		this.getFormHM().put("salaryid", this.getSalaryid());
		this.getFormHM().put("a_code", getA_code());
		this.getFormHM().put("itemid", this.getItemid());
		this.getFormHM().put("condid", this.getCondid());
		this.getFormHM().put("flow_flag", this.getFlow_flag());
		
		/**薪资审批*/
		this.getFormHM().put("bosdate", this.getBosdate());
		this.getFormHM().put("count", this.getCount());

		this.getFormHM().put("file",this.getFile());

		this.getFormHM().put("oppositeItem",this.getOppositeItem());
		this.getFormHM().put("relationItem",this.getRelationItem());

		this.getFormHM().put("empfiltersql",this.getEmpfiltersql());

		this.getFormHM().put("schemeName",this.getSchemeName());
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("addright_fields", this.getAddright_fields());
		this.getFormHM().put("delright_fields", this.getDelright_fields());

		this.getFormHM().put("proright_fields",this.getProright_fields());
		this.getFormHM().put("proright_str",this.getProright_str());
		this.getFormHM().put("del_pro_str",this.getDel_pro_str());
		this.getFormHM().put("sort_table_detail",this.getSort_table_detail());
		this.getFormHM().put("sort_table_approval",this.getSort_table_approval());
		this.getFormHM().put("order_by",this.getOrder_by());

		 /** 薪资报表 定义 */
		this.getFormHM().put("salaryReportName",this.getSalaryReportName());
		this.getFormHM().put("isPrintWithGroup",this.getIsPrintWithGroup());
		this.getFormHM().put("f_groupItem",this.getF_groupItem());
		this.getFormHM().put("s_groupItem",this.getS_groupItem());
		this.getFormHM().put("reportStyleID",this.getReportStyleID());
		this.getFormHM().put("flag",this.getFlag());
		/**工资保险标志*/
		this.getFormHM().put("gz_module",this.getGz_module());
		this.getFormHM().put("isVisible",this.getIsVisible());
		this.getFormHM().put("cond_id_str",cond_id_str);
		this.getFormHM().put("originalDataFile",this.getOriginalDataFile());
		
		this.getFormHM().put("okCount",this.getOkCount());
		this.getFormHM().put("errorFileName",this.getErrorFileName());
		this.getFormHM().put("sp",this.getSp());
		
		this.getFormHM().put("spRowCanEditStatus",this.getSpRowCanEditStatus());
		this.getFormHM().put("gzRowCanEditStatus",this.getGzRowCanEditStatus());
		this.getFormHM().put("collectPoint", this.getCollectPoint());
		this.getFormHM().put("collectList", this.getCollectList());
	}
	

	@Override
    public void outPutFormHM() {
		this.setUpdateDateList((ArrayList)this.getFormHM().get("updateDateList"));
		this.setIsRedo((String)this.getFormHM().get("isRedo"));
		this.setRoyalty_valid((String)this.getFormHM().get("royalty_valid"));
		this.setUser_((String)this.getFormHM().get("user_"));
		this.setUser_h((String)this.getFormHM().get("user_h"));
		
		this.setRelation_id((String)this.getFormHM().get("relation_id"));
		this.setSp_actor_str((String)this.getFormHM().get("sp_actor_str"));
		this.setSpActorName((String)this.getFormHM().get("spActorName"));
		this.setShowUnitCodeTree((String)this.getFormHM().get("showUnitCodeTree"));
		
		this.setIsVisibleItem((String)this.getFormHM().get("isVisibleItem"));
		this.setItemSumList((ArrayList)this.getFormHM().get("itemSumList"));
		this.setDecwidth((String)this.getFormHM().get("decwidth"));
						
		this.setOkCount((String)this.getFormHM().get("okCount"));
		this.setErrorFileName((String)this.getFormHM().get("errorFileName"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setReturnvalue((String)this.getFormHM().get("returnvalue"));
		
		this.setQueryFieldList((ArrayList)this.getFormHM().get("queryFieldList"));
		this.setFf_setname((String)this.getFormHM().get("ff_setname"));
		this.setFf_bosdate((String)this.getFormHM().get("ff_bosdate"));
		this.setFf_count((String)this.getFormHM().get("ff_count"));
		
		this.setChkName((String)this.getFormHM().get("chkName"));
		this.setChkid((String)this.getFormHM().get("chkid"));
		this.setFilterFieldList((ArrayList)this.getFormHM().get("filterFieldList"));
		this.setIsHistory((String)this.getFormHM().get("isHistory"));
		this.setSubNoShowUpdateFashion((String)this.getFormHM().get("subNoShowUpdateFashion"));
		this.setIsTotalControl((String)this.getFormHM().get("isTotalControl"));
		this.setVerify_ctrl((String)this.getFormHM().get("verify_ctrl"));
	    	this.setOperOrg((String)this.getFormHM().get("operOrg"));
	    	this.setReturnFlag((String)this.getFormHM().get("returnFlag"));
		this.setAppUserList((ArrayList)this.getFormHM().get("appUserList"));
		this.setAppUser((String)this.getFormHM().get("appUser"));
		this.setSp_flag((String)this.getFormHM().get("sp_flag"));
		this.setSpFlagList((ArrayList)this.getFormHM().get("spFlagList"));
		
	    this.setOriginalDataFile((String)this.getFormHM().get("originalDataFile"));
		this.setIsSalaryManager((String)this.getFormHM().get("isSalaryManager"));
	    this.setIsImportBonus((String)this.getFormHM().get("isImportBonus"));
	    this.setIsImportPiece((String)this.getFormHM().get("isImportPiece"));
		this.setEntry_type((String)this.getFormHM().get("entry_type"));
		this.setIsNotSpFlag2Records((String)this.getFormHM().get("isNotSpFlag2Records"));
		this.setIsSendMessage((String)this.getFormHM().get("isSendMessage"));
		this.setReportSql((String)this.getFormHM().get("reportSql"));
		this.setModel((String)this.getFormHM().get("model"));
		this.setFileName((String)this.getFormHM().get("fileName"));
		this.setShPersonList((ArrayList)this.getFormHM().get("shPersonList"));
		this.setShFormulaIds((String)this.getFormHM().get("shFormulaIds"));
		this.setShFormulaList((ArrayList)this.getFormHM().get("shFormulaList"));
		this.setSp_ori((String)this.getFormHM().get("sp_ori"));
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setIsShowManagerFunction((String)this.getFormHM().get("isShowManagerFunction"));
		this.setManager((String)this.getFormHM().get("manager"));
		this.setIsEditDate((String)this.getFormHM().get("isEditDate"));
		
		this.setIsVisible((String)this.getFormHM().get("isVisible"));
		this.setPriv_mode((String)this.getFormHM().get("priv_mode"));
		this.setGz_module((String)this.getFormHM().get("gz_module"));
		this.setPriv((String)this.getFormHM().get("priv"));
		this.setSalaryIsSubed((String)this.getFormHM().get("salaryIsSubed"));
		this.setFinalDate((String)this.getFormHM().get("finalDate"));
		this.setMsg((String)this.getFormHM().get("msg"));
		this.setRowNums((String)this.getFormHM().get("rowNums"));
		this.setFilterWhl((String)this.getFormHM().get("filterWhl"));
		this.setIsAppealData((String)this.getFormHM().get("isAppealData"));
		this.setUserid((String)this.getFormHM().get("userid"));
		this.setSubFlag((String)this.getFormHM().get("subFlag"));
		this.setSalaryReportName((String)this.getFormHM().get("salaryReportName"));
		this.setIsPrintWithGroup((String)this.getFormHM().get("isPrintWithGroup"));
		this.setF_groupItem((String)this.getFormHM().get("f_groupItem"));
		this.setS_groupItem((String)this.getFormHM().get("s_groupItem"));
		this.setF_groupItemList((ArrayList)this.getFormHM().get("f_groupItemList"));
		this.setS_groupItemList((ArrayList)this.getFormHM().get("s_groupItemList"));
		this.setReportStyleID((String)this.getFormHM().get("reportStyleID"));
		this.setReportDetailID((String)this.getFormHM().get("reportDetailID"));
		
		
		this.getSetlistform().setList((ArrayList)this.getFormHM().get("setlist"));
		this.getSetlistform().getCurrent();
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setGz_tablename((String)this.getFormHM().get("tablename"));

		this.setSalaryid((String)this.getFormHM().get("salaryid"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setAppflag((String)this.getFormHM().get("appflag"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setCondlist((ArrayList)this.getFormHM().get("condlist"));
		this.setCondid((String)this.getFormHM().get("condid"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		/**当前处理的*/
		this.setThemonth((String)this.getFormHM().get("themonth"));
		this.setTheyear((String)this.getFormHM().get("theyear"));
		
		this.setBedit((String)this.getFormHM().get("bedit"));
		this.setSpRowCanEditStatus((String)this.getFormHM().get("spRowCanEditStatus"));
		this.setGzRowCanEditStatus((String)this.getFormHM().get("gzRowCanEditStatus"));
		/**薪资审批*/
		this.setAppdate((String)this.getFormHM().get("appdate"));
		this.setBosdate((String)this.getFormHM().get("bosdate"));
		this.setCount((String)this.getFormHM().get("count"));
		this.setDatelist((ArrayList)this.getFormHM().get("datelist"));
		this.setCountlist((ArrayList)this.getFormHM().get("countlist"));

		//工资发放导入
		this.setOriginalDataList((ArrayList)this.getFormHM().get("originalDataList"));
		this.setAimDataList((ArrayList)this.getFormHM().get("aimDataList"));
		this.setSameDataList((ArrayList)this.getFormHM().get("sameDataList"));
		
		this.setLeftlist((ArrayList)this.getFormHM().get("leftlist"));
		this.setRightlist((ArrayList)this.getFormHM().get("rightlist"));
		this.setAddleftlist((ArrayList) this.getFormHM().get("addleftlist"));
		this.setAddrightlist((ArrayList) this.getFormHM().get("addrightlist"));
		this.setDelleftlist((ArrayList) this.getFormHM().get("delleftlist"));
		this.setDelrightlist((ArrayList) this.getFormHM().get("delrightlist"));


		this.setOriDataList((ArrayList)this.getFormHM().get("oriDataList"));
		this.setRelationItemList((ArrayList)this.getFormHM().get("relationItemList"));
		this.setOppositeItemList((ArrayList)this.getFormHM().get("oppositeItemList"));
		
		this.setTablenamelist((ArrayList)this.getFormHM().get("tablenamelist"));
		this.setTableidlist1((ArrayList)this.getFormHM().get("tableidlist1"));
		this.setTableidlist2((ArrayList)this.getFormHM().get("tableidlist2"));
		this.setTotallist((ArrayList)this.getFormHM().get("totallist"));
		this.setVarianceTotal((ArrayList)this.getFormHM().get("varianceTotal"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setWhere((String)this.getFormHM().get("where"));
		this.setColumn((String)this.getFormHM().get("column"));
		this.setOrderby((String)this.getFormHM().get("orderby"));
		this.setChangeflag((String)this.getFormHM().get("changeflag"));
		this.setEmpfiltersql((String)this.getFormHM().get("empfiltersql")); 
		this.setSortemplist((ArrayList)this.getFormHM().get("sortemplist"));
		this.setProfilterlist((ArrayList)this.getFormHM().get("profilterlist"));
		this.setProright_fields((String[])this.getFormHM().get("proright_fields"));

		this.setProright_str((String)this.getFormHM().get("proright_str"));
		this.setDel_filter_pro((ArrayList)this.getFormHM().get("del_filter_pro"));
		this.setSort_table((String)this.getFormHM().get("sort_table"));
		this.setSort_table_detail((String)this.getFormHM().get("sort_table_detail"));
		this.setSort_table_approval((String)this.getFormHM().get("sort_table_approval"));

		this.setFlag((String)this.getFormHM().get("flag"));
		this.setOrder_by((String)this.getFormHM().get("order_by"));
		this.setParam_flag((String)this.getFormHM().get("param_flag"));
		this.getFormHM().remove("param_flag");
		
		this.setScopeflag((String)this.getFormHM().get("scopeflag"));
		this.setOperitems((String)this.getFormHM().get("operitems"));
		this.setHidenflag(appprocess);
		this.setStatelist((ArrayList)this.getFormHM().get("statelist"));
		this.setAppprocess((String)this.getFormHM().get("appprocess"));
		this.setHidenflag((String)this.getFormHM().get("hidenflag"));
		this.setSp((String)this.getFormHM().get("sp"));
		this.setCollectPoint((String) this.getFormHM().get("collectPoint"));
		this.setCollectList((ArrayList) this.getFormHM().get("collectList"));
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/gz/gz_accounting/changesmore".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		if("/gz/gz_accounting/addStaff".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		if("/gz/gz_accounting/staffMinus".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		
		if("/gz/gz_accounting/gz_sp_setlist".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
            /**定位到首页,*/
            if(this.getSetlistform()!=null)
            	this.getSetlistform().getPagination().firstPage();              
        }
		
		if("/gz/gz_accounting/gz_set_list".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null&&!"link2".equals(arg1.getParameter("b_query"))){//当传递link2的时候不定位到首页，用于返回按钮  zhaoxg add 2014-1-17
            /**定位到首页,*/
            if(this.getSetlistform()!=null)
            	this.getSetlistform().getPagination().firstPage();              
        }
		if("/gz/gz_accounting/in_out".equals(arg0.getPath())&&arg1.getParameter("b_import")!=null&& "import".equals(arg1.getParameter("b_import"))){//薪资发放导入的时候如果抛异常中断程序，那么给返回按钮一个正确的链接 zhaoxg add 2015-3-4
			arg1.setAttribute("formpath","/gz/gz_accounting/in_out.do?b_getImportData=get");
		}
		return super.validate(arg0, arg1);
	}
	public PaginationForm getSetlistform() {
		return setlistform;
	}

	public void setSetlistform(PaginationForm setlistform) {
		this.setlistform = setlistform;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getGz_tablename() {
		return gz_tablename;
	}

	public void setGz_tablename(String gz_tablename) {
		this.gz_tablename = gz_tablename;
	}

	public String getGz_table_alias() {
		return gz_table_alias;
	}

	public void setGz_table_alias(String gz_table_alias) {
		this.gz_table_alias = gz_table_alias;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}




	public String getAppflag() {
		return appflag;
	}

	public void setAppflag(String appflag) {
		this.appflag = appflag;
	}

	public ArrayList getCondlist() {
		return condlist;
	}

	public void setCondlist(ArrayList condlist) {
		this.condlist = condlist;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getCondid() {
		return condid;
	}

	public void setCondid(String condid) {
		this.condid = condid;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getFlow_flag() {
		return flow_flag;
	}

	public void setFlow_flag(String flow_flag) {
		this.flow_flag = flow_flag;
	}

	public String getBosdate() {
		return bosdate;
	}

	public void setBosdate(String bosdate) {
		this.bosdate = bosdate;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public ArrayList getCountlist() {
		return countlist;
	}

	public void setCountlist(ArrayList countlist) {
		this.countlist = countlist;
	}

	public ArrayList getDatelist() {
		return datelist;
	}

	public void setDatelist(ArrayList datelist) {
		this.datelist = datelist;
	}

	public String getThemonth() {
		return themonth;
	}

	public void setThemonth(String themonth) {
		this.themonth = themonth;
	}

	public String getTheyear() {
		return theyear;
	}

	public void setTheyear(String theyear) {
		this.theyear = theyear;
	}


	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public ArrayList getAimDataList() {
		return aimDataList;
	}

	public void setAimDataList(ArrayList aimDataList) {
		this.aimDataList = aimDataList;
	}

	public ArrayList getOriginalDataList() {
		return originalDataList;
	}

	public void setOriginalDataList(ArrayList originalDataList) {
		this.originalDataList = originalDataList;
	}

	public String[] getLeft_fields() {
		return left_fields;
	}

	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public ArrayList getLeftlist() {
		return leftlist;
	}

	public void setLeftlist(ArrayList leftlist) {
		this.leftlist = leftlist;
	}

	public ArrayList getRightlist() {
		return rightlist;
	}

	public void setRightlist(ArrayList rightlist) {
		this.rightlist = rightlist;
	}

	public ArrayList getOriDataList() {
		return oriDataList;
	}

	public void setOriDataList(ArrayList oriDataList) {
		this.oriDataList = oriDataList;
	}

	public String[] getOppositeItem() {
		return oppositeItem;
	}

	public void setOppositeItem(String[] oppositeItem) {
		this.oppositeItem = oppositeItem;
	}

	public String[] getRelationItem() {
		return relationItem;
	}

	public void setRelationItem(String[] relationItem) {
		this.relationItem = relationItem;
	}

	public ArrayList getOppositeItemList() {
		return oppositeItemList;
	}

	public void setOppositeItemList(ArrayList oppositeItemList) {
		this.oppositeItemList = oppositeItemList;
	}

	public ArrayList getRelationItemList() {
		return relationItemList;
	}

	public void setRelationItemList(ArrayList relationItemList) {
		this.relationItemList = relationItemList;
	}


	public String getSchemeName() {
		return schemeName;
	}

	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public ArrayList getTablenamelist() {
		return tablenamelist;
	}
	public ArrayList getSortemplist() {
		return sortemplist;
	}

	public void setSortemplist(ArrayList sortemplist) {
		this.sortemplist = sortemplist;
	}

	public String[] getSortleft_field() {
		return sortleft_field;
	}

	public void setSortleft_field(String[] sortleft_field) {
		this.sortleft_field = sortleft_field;
	}

	public ArrayList getProfilterlist() {
		return profilterlist;
	}

	public void setProfilterlist(ArrayList profilterlist) {
		this.profilterlist = profilterlist;
	}

	public String[] getProleft_field() {
		return proleft_field;
	}

	public void setProleft_field(String[] proleft_field) {
		this.proleft_field = proleft_field;
	}

	public String[] getProright_fields() {
		return proright_fields;
	}

	public void setProright_fields(String[] proright_fields) {
		this.proright_fields = proright_fields;
	}

	public String getF_groupItem() {
		return f_groupItem;
	}

	public void setF_groupItem(String item) {
		f_groupItem = item;
	}

	public ArrayList getF_groupItemList() {
		return f_groupItemList;
	}

	public void setF_groupItemList(ArrayList itemList) {
		f_groupItemList = itemList;
	}

	public String getIsPrintWithGroup() {
		return isPrintWithGroup;
	}

	public void setIsPrintWithGroup(String isPrintWithGroup) {
		this.isPrintWithGroup = isPrintWithGroup;
	}

	public String getReportStyleID() {
		return reportStyleID;
	}

	public void setReportStyleID(String reportStyleID) {
		this.reportStyleID = reportStyleID;
	}

	public String getS_groupItem() {
		return s_groupItem;
	}

	public void setS_groupItem(String item) {
		s_groupItem = item;
	}

	public ArrayList getS_groupItemList() {
		return s_groupItemList;
	}

	public void setS_groupItemList(ArrayList itemList) {
		s_groupItemList = itemList;
	}

	public String getSalaryReportName() {
		return salaryReportName;
	}

	public void setSalaryReportName(String salaryReportName) {
		this.salaryReportName = salaryReportName;
	}

	public String getReportDetailID() {
		return reportDetailID;
	}

	public void setReportDetailID(String reportDetailID) {
		this.reportDetailID = reportDetailID;
	}


	public String getProright_str() {
		return proright_str;
	}

	public void setProright_str(String proright_str) {
		this.proright_str = proright_str;
	}

	public ArrayList getDel_filter_pro() {
		return del_filter_pro;
	}

	public void setDel_filter_pro(ArrayList del_filter_pro) {
		this.del_filter_pro = del_filter_pro;
	}

	public String getSortmode() {
		return sortmode;
	}

	public void setSortmode(String sortmode) {
		this.sortmode = sortmode;
	}

	public String getDel_pro_str() {
		return del_pro_str;
	}

	public void setDel_pro_str(String del_pro_str) {
		this.del_pro_str = del_pro_str;
	}


	public String getBedit() {
		return bedit;
	}

	public void setBedit(String bedit) {
		this.bedit = bedit;
	}

	public void setTablenamelist(ArrayList tablenamelist) {
		this.tablenamelist = tablenamelist;
	}

	public ArrayList getTableidlist1() {
		return tableidlist1;
	}


	public void setTableidlist1(ArrayList tableidlist1) {
		this.tableidlist1 = tableidlist1;
	}

	public ArrayList getTableidlist2() {
		return tableidlist2;
	}

	public void setTableidlist2(ArrayList tableidlist2) {
		this.tableidlist2 = tableidlist2;
	}

	public ArrayList getTotallist() {
		return totallist;
	}

	public void setTotallist(ArrayList totallist) {
		this.totallist = totallist;
	}

	public ArrayList getVarianceTotal() {
		return varianceTotal;
	}

	public void setVarianceTotal(ArrayList varianceTotal) {
		this.varianceTotal = varianceTotal;
	}

	public String getChangeflag() {
		return changeflag;
	}

	public void setChangeflag(String changeflag) {
		this.changeflag = changeflag;
	}

	public String getEmpfiltersql() {
		return empfiltersql;
	}

	public void setEmpfiltersql(String empfiltersql) {
		this.empfiltersql = empfiltersql;
	}

	public String getSort_table() {
		return sort_table;
	}

	public void setSort_table(String sort_table) {
		this.sort_table = sort_table;
	}

	public String getSort_table_detail() {
		return sort_table_detail;
	}

	public void setSort_table_detail(String sort_table_detail) {
		this.sort_table_detail = sort_table_detail;
	}
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getIsAppealData() {
		return isAppealData;
	}

	public void setIsAppealData(String isAppealData) {
		this.isAppealData = isAppealData;
	}

	public String getSelectGzRecords() {
		return selectGzRecords;
	}

	public void setSelectGzRecords(String selectGzRecords) {
		this.selectGzRecords = selectGzRecords;
	}

	public String getRejectCause() {
		return rejectCause;
	}

	public void setRejectCause(String rejectCause) {
		this.rejectCause = rejectCause;
	}

	public String getApproveObject() {
		return approveObject;
	}

	public void setApproveObject(String approveObject) {
		this.approveObject = approveObject;
	}

	public String getAppdate() {
		return appdate;
	}

	public void setAppdate(String appdate) {
		this.appdate = appdate;
	}

	public String getFilterWhl() {
		return filterWhl;
	}

	public void setFilterWhl(String filterWhl) {
		this.filterWhl = filterWhl;
	}

	public String getSubFlag() {
		return subFlag;
	}

	public void setSubFlag(String subFlag) {
		this.subFlag = subFlag;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getOrder_by() {
		return order_by;
	}

	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getFinalDate() {
		return finalDate;
	}

	public void setFinalDate(String finalDate) {
		this.finalDate = finalDate;
	}

	public String getGz_module() {
		return gz_module;
	}

	public void setGz_module(String gz_module) {
		this.gz_module = gz_module;
	}

	public String getRowNums() {
		return rowNums;
	}

	public void setRowNums(String rowNums) {
		this.rowNums = rowNums;
	}

	public ArrayList getSameDataList() {
		return sameDataList;
	}

	public void setSameDataList(ArrayList sameDataList) {
		this.sameDataList = sameDataList;
	}

	public String getSalaryIsSubed() {
		return salaryIsSubed;
	}

	public void setSalaryIsSubed(String salaryIsSubed) {
		this.salaryIsSubed = salaryIsSubed;
	}

	public String getPriv_mode() {
		return priv_mode;
	}

	public void setPriv_mode(String priv_mode) {
		this.priv_mode = priv_mode;
	}
	
	public String getPriv() {
		return priv;
	}

	public void setPriv(String priv) {
		this.priv = priv;
	}

	public String getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(String isVisible) {
		this.isVisible = isVisible;
	}

	public String getCond_id_str() {
		return cond_id_str;
	}

	public void setCond_id_str(String cond_is_str) {
		this.cond_id_str = cond_is_str;
	}

	public String getIsShowManagerFunction() {
		return isShowManagerFunction;
	}

	public void setIsShowManagerFunction(String isShowManagerFunction) {
		this.isShowManagerFunction = isShowManagerFunction;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getIsEditDate() {
		return isEditDate;
	}

	public void setIsEditDate(String isEditDate) {
		this.isEditDate = isEditDate;
	}

	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public String getSp_ori() {
		return sp_ori;
	}

	public void setSp_ori(String sp_ori) {
		this.sp_ori = sp_ori;
	}

	public ArrayList getShFormulaList() {
		return shFormulaList;
	}

	public void setShFormulaList(ArrayList shFormulaList) {
		this.shFormulaList = shFormulaList;
	}

	public String getShFormulaIds() {
		return shFormulaIds;
	}

	public void setShFormulaIds(String shFormulaIds) {
		this.shFormulaIds = shFormulaIds;
	}

	public ArrayList getShPersonList() {
		return shPersonList;
	}

	public void setShPersonList(ArrayList shPersonList) {
		this.shPersonList = shPersonList;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getReportSql() {
		return reportSql;
	}

	public void setReportSql(String reportSql) {
		this.reportSql = reportSql;
	}

	public String getSendMen() {
		return sendMen;
	}

	public void setSendMen(String sendMen) {
		this.sendMen = sendMen;
	}

	public String getIsSendMessage() {
		return isSendMessage;
	}

	public void setIsSendMessage(String isSendMessage) {
		this.isSendMessage = isSendMessage;
	}

	public String getIsNotSpFlag2Records() {
		return isNotSpFlag2Records;
	}

	public void setIsNotSpFlag2Records(String isNotSpFlag2Records) {
		this.isNotSpFlag2Records = isNotSpFlag2Records;
	}

	public String getEntry_type() {
		return entry_type;
	}

	public void setEntry_type(String entry_type) {
		this.entry_type = entry_type;
	}

	public String getIsImportBonus()
	{
	
	    return isImportBonus;
	}

	public void setIsImportBonus(String isImportBonus)
	{
	
	    this.isImportBonus = isImportBonus;
	}

	public String getParam_flag() {
		return param_flag;
	}

	public void setParam_flag(String param_flag) {
		this.param_flag = param_flag;
	}
	public String getIsSalaryManager() {
		return isSalaryManager;
	}

	public void setIsSalaryManager(String isSalaryManager) {
		this.isSalaryManager = isSalaryManager;
	}

	public String getOriginalDataFile()
	{
	
	    return originalDataFile;
	}

	public void setOriginalDataFile(String originalDataFile)
	{
	
	    this.originalDataFile = originalDataFile;
	}

	public String getAppUser() {
		return appUser;
	}

	public void setAppUser(String appUser) {
		this.appUser = appUser;
	}

	public ArrayList getAppUserList() {
		return appUserList;
	}

	public void setAppUserList(ArrayList appUserList) {
		this.appUserList = appUserList;
	}

	public String getSp_flag() {
		return sp_flag;
	}

	public void setSp_flag(String sp_flag) {
		this.sp_flag = sp_flag;
	}

	public ArrayList getSpFlagList() {
		return spFlagList;
	}

	public void setSpFlagList(ArrayList spFlagList) {
		this.spFlagList = spFlagList;
	}

	public String getReturnFlag()
	{
	
	    return returnFlag;
	}

	public void setReturnFlag(String returnFlag)
	{
	
	    this.returnFlag = returnFlag;
	}

	public String getOperOrg()
	{
	
	    return operOrg;
	}

	public void setOperOrg(String operOrg)
	{
	
	    this.operOrg = operOrg;
	}

	public String getVerify_ctrl() {
		return verify_ctrl;
	}

	public void setVerify_ctrl(String verify_ctrl) {
		this.verify_ctrl = verify_ctrl;
	}

	public String getSubNoShowUpdateFashion() {
		return subNoShowUpdateFashion;
	}

	public void setSubNoShowUpdateFashion(String subNoShowUpdateFashion) {
		this.subNoShowUpdateFashion = subNoShowUpdateFashion;
	}

	public String getIsHistory() {
		return isHistory;
	}

	public void setIsHistory(String isHistory) {
		this.isHistory = isHistory;
	}
	public String getIsTotalControl() {
		return isTotalControl;
	}

	public void setIsTotalControl(String isTotalControl) {
		this.isTotalControl = isTotalControl;
	}
	public ArrayList getFilterFieldList() {
		return filterFieldList;
	}

	public void setFilterFieldList(ArrayList filterFieldList) {
		this.filterFieldList = filterFieldList;
	}

	public String getChkid() {
		return chkid;
	}

	public void setChkid(String chkid) {
		this.chkid = chkid;
	}

	public String getChkName() {
		return chkName;
	}

	public void setChkName(String chkName) {
		this.chkName = chkName;
	}

	public String getFf_bosdate() {
		return ff_bosdate;
	}

	public void setFf_bosdate(String ff_bosdate) {
		this.ff_bosdate = ff_bosdate;
	}

	public String getFf_count() {
		return ff_count;
	}

	public void setFf_count(String ff_count) {
		this.ff_count = ff_count;
	}

	public String getFf_setname() {
		return ff_setname;
	}

	public void setFf_setname(String ff_setname) {
		this.ff_setname = ff_setname;
	}

	public ArrayList getQueryFieldList() {
		return queryFieldList;
	}

	public void setQueryFieldList(ArrayList queryFieldList) {
		this.queryFieldList = queryFieldList;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

	public String getOperitems() {
		return operitems;
	}

	public void setOperitems(String operitems) {
		this.operitems = operitems;
	}

	public String getHidenflag() {
		return hidenflag;
	}

	public void setHidenflag(String hidenflag) {
		this.hidenflag = hidenflag;
	}

	public ArrayList getStatelist() {
		return statelist;
	}

	public void setStatelist(ArrayList statelist) {
		this.statelist = statelist;
	}

	@Override
    public String getReturnflag() {
		return returnflag;
	}

	@Override
    public void setReturnflag(String returnflag) {
		this.returnflag = returnflag;
	}

	public String getOkCount()
	{
		return okCount;
	}

	public void setOkCount(String okCount)
	{
		this.okCount = okCount;
	}

	public String getErrorFileName()
	{
		return errorFileName;
	}

	public void setErrorFileName(String errorFileName)
	{
		this.errorFileName = errorFileName;
	}

	public ArrayList getItemSumList() {
		return itemSumList;
	}

	public void setItemSumList(ArrayList itemSumList) {
		this.itemSumList = itemSumList;
	}

	public String getDecwidth() {
		return decwidth;
	}

	public void setDecwidth(String decwidth) {
		this.decwidth = decwidth;
	}

	public String getIsVisibleItem() {
		return isVisibleItem;
	}

	public void setIsVisibleItem(String isVisibleItem) {
		this.isVisibleItem = isVisibleItem;
	}

	public String getShowUnitCodeTree() {
		return showUnitCodeTree;
	}

	public void setShowUnitCodeTree(String showUnitCodeTree) {
		this.showUnitCodeTree = showUnitCodeTree;
	}

	public String getSp_actor_str() {
		return sp_actor_str;
	}

	public void setSp_actor_str(String sp_actor_str) {
		this.sp_actor_str = sp_actor_str;
	}

	public String getSpActorName() {
		return spActorName;
	}

	public void setSpActorName(String spActorName) {
		this.spActorName = spActorName;
	}

	public String getRelation_id() {
		return relation_id;
	}

	public void setRelation_id(String relation_id) {
		this.relation_id = relation_id;
	}

	public String getUser_() {
		return user_;
	}

	public void setUser_(String user_) {
		this.user_ = user_;
	}

	public String getUser_h() {
		return user_h;
	}

	public void setUser_h(String user_h) {
		this.user_h = user_h;
	}

	public String getRoyalty_valid() {
		return royalty_valid;
	}

	public void setRoyalty_valid(String royalty_valid) {
		this.royalty_valid = royalty_valid;
	}

	public String getSp() {
		return sp;
	}

	public void setSp(String sp) {
		this.sp = sp;
	}

	public String getIsImportPiece() {
		return isImportPiece;
	}

	public void setIsImportPiece(String isImportPiece) {
		this.isImportPiece = isImportPiece;
	}

	public String getIsRedo() {
		return isRedo;
	}

	public void setIsRedo(String isRedo) {
		this.isRedo = isRedo;
	}


	public ArrayList getAddleftlist() {
		return addleftlist;
	}

	public void setAddleftlist(ArrayList addleftlist) {
		this.addleftlist = addleftlist;
	}

	public ArrayList getAddrightlist() {
		return addrightlist;
	}

	public void setAddrightlist(ArrayList addrightlist) {
		this.addrightlist = addrightlist;
	}

	public ArrayList getDelleftlist() {
		return delleftlist;
	}

	public void setDelleftlist(ArrayList delleftlist) {
		this.delleftlist = delleftlist;
	}

	public ArrayList getDelrightlist() {
		return delrightlist;
	}

	public void setDelrightlist(ArrayList delrightlist) {
		this.delrightlist = delrightlist;
	}

	public String[] getAddleft_fields() {
		return addleft_fields;
	}

	public void setAddleft_fields(String[] addleft_fields) {
		this.addleft_fields = addleft_fields;
	}

	public String[] getAddright_fields() {
		return addright_fields;
	}

	public void setAddright_fields(String[] addright_fields) {
		this.addright_fields = addright_fields;
	}

	public String[] getDelleft_fields() {
		return delleft_fields;
	}

	public void setDelleft_fields(String[] delleft_fields) {
		this.delleft_fields = delleft_fields;
	}

	public String[] getDelright_fields() {
		return delright_fields;
	}

	public void setDelright_fields(String[] delright_fields) {
		this.delright_fields = delright_fields;
	}

    public String getSpRowCanEditStatus() {
        return spRowCanEditStatus;
    }

    public void setSpRowCanEditStatus(String spRowCanEditStatus) {
        this.spRowCanEditStatus = spRowCanEditStatus;
    }
	public String getCollectPoint() {
		return collectPoint;
	}

	public void setCollectPoint(String collectPoint) {
		this.collectPoint = collectPoint;
	}

	public ArrayList getCollectList() {
		return collectList;
	}

	public void setCollectList(ArrayList collectList) {
		this.collectList = collectList;
	}

    public String getGzRowCanEditStatus() {
        return gzRowCanEditStatus;
    }

    public void setGzRowCanEditStatus(String gzRowCanEditStatus) {
        this.gzRowCanEditStatus = gzRowCanEditStatus;
    }

	public String getSort_table_approval() {
		return sort_table_approval;
	}

	public void setSort_table_approval(String sort_table_approval) {
		this.sort_table_approval = sort_table_approval;
	}

	public ArrayList getUpdateDateList() {
		return updateDateList;
	}

	public void setUpdateDateList(ArrayList updateDateList) {
		this.updateDateList = updateDateList;
	}

}
