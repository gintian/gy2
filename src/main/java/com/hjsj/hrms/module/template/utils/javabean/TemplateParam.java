package com.hjsj.hrms.module.template.utils.javabean;

import com.hjsj.hrms.businessobject.general.template.TSubsetCtrl;
import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TemplateParam {
    private Connection conn=null;
    private UserView userView;
    private ContentDAO dao; 
	private int tabId=0;
	private RecordVo table_vo=null;
	
	/**业务操作类型
	 * 对人员调入的业务单独处理
	 * =0人员调入,=1调出（须指定目标人员库）,=2离退(须指定目标人员库),=3内部调动, =4系统内部调动
	 * =5新建（单位|部门|岗位）=6更名（单位|部门|岗位）=7撤销（单位|部门|岗位）=8合并（单位|部门|岗位）
	 *=9划转（单位|部门|岗位）
	 *=10其他变动  其它不作特殊处理的业务
	 */
	private int operationType=10; 
	private String operationcode;
	private String operationname;
	
	/**
	 * 业务模块标志 
	 * =1日常管理，=2薪资管理，=3警衔管理，=4法官等级，=5关衔，=6检察官，=7未用，=8保险管理，=9档案变动，
	 * =10单位管理，=11职位管理，（未用=12考勤管理，=13招聘管理，=14培训管理)。
	 */
	private int templateStatic=1; 
	
    /** 调用的业务模块 todo：不应该在此定义
	0：为正常的人事异动 1:申诉业务  4：招聘录入    2：目标里的面谈记录  3:人事异动已批任务    
     * 5:我的申请  61:报备任务(待办) 62：报备任务（已办） 71：加签任务（待办）  72：加签任务（已办）    */
    private String business_model="0";
	
	/**信息群类型
	 * 为了以后人事代码机构增加用
	 * =1 人员模板
	 * =2 单位模板
	 * =3 岗位模板
	 * */
	private int infor_type=1; 	
	
	/**模板名称*/
	private String name;
	private String filter_by_factor="0"; //手工选人、条件选人按检索条件过滤, 0不过滤(默认值),1过滤
	private String no_priv_ctrl="0"; //手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按

	/**业务流程描述*/
	private String content;
	/**每英寸像素数
	 * default:windows 96
	 * mac             72
	 * */
	private int PixelInInch=96;  

	/**begin 进入页面相关*/
	/**人员过滤条件*/
	private String factor;
	/**规则条件,分析业务合法性*/
	private String llexpr;    
    private String view="list";//<!-- 默认显示方式，list列表(默认),card卡片 -->    
	/**初始进入人员库 */
	private String  init_base="";	
	private String change_after_get_data="0";  //1：变化后指标取当前值  0：不取
	private String filter_by_manage_priv="0";  //接收通知单数据方式：0接收全部数据，1接收管理范围内数据
	private String include_suborg="1"; //0不包括下属单位, 1包括(默认值)	
	private String import_notice_data = "0"; //默认变化后对应变化后 true  变化后对应变化前 false
	
	/**数据录入不判断子集和指标权限, 0判断(默认值),1不判断  */
	private String UnrestrictedMenuPriv_Input="0";
	/**关联序号的变化后指标是否手工生成序号, 0加人时自动生成(默认值),1手工生成 */
	private String id_gen_manual="0";
	/**判断模板中包含了关联序号的变化后指标 0表示未包含,1表示包含*/
	private String existid_gen_manual="0";
	/**员工发启业务申请，已报批（有单子在途时）不允许再次申请,0:否(默认值) */
	private String  unique_check="0";
	/**审批意见*/
	private String opinion_field ="";
	
    private String endUserType="";    //手工指派模式最终办理人类型, 0: 用户, 1: 人员, 空表示未指定
    private String endUser="";        //手工指派模式最终办理人, enduser保存用户名或人员编号(人员库+A0100), 空表示未指定
    
    private String reject_type = "";//驳回方式 =1：逐级驳回 =2：驳回到发起人  郭峰
    private String def_flow_self = "0";//自定义审批流程 
    private String no_sp_yj="0"; // no_sp_yj:审批不填写意见  1:选中   0:空表示没选中（默认）    
    private boolean sp_syncArchiveData=false ; // 审批时同步档案库信息  false:不同步 true：需要同步 20060629增加   
/**end 进入页面相关*/
		
	
/**begin 审批相关*/		
	/**是否需要审批*/
	private boolean bsp_flag=false;
	/**审批模式=0自动流转，=1手工指派*/
	private int sp_mode=0;
	/** 是否自定义审批流程 */
	private boolean allow_defFlowSelf=false; 	
	/** 模板关联的审批关系 */
	private String Relation_id="";	
  // 审批 发邮件有关
	/**是否发送邮件* todo: 改名字*/
	private boolean bemail=false;
	/**是否发送短信*/
	private boolean bsms=false;
	/**邮件通知到本人*/
	private boolean email_staff=false;	
	private String template_staff="";  //员工本人的邮件模板
	/**抄送模板*/
	private String template_bos="";   
	/**审批模板*/
	private String template_sp=""; 
	//notice_initiator:提交时通知到发起人 false:不通知 true：通知 todo: 改类型
	private String notice_initiator="false";
	//提交时通知到发起人时要通知的模版
	private String template_initiator="";
	
    private String split_data_model="";  //分组类型  groupfield: 分组指标  superior:直接领导
    private String split_data_fields=""; //fields:定义的分组指标值  (x)代表控制的代码层级
/**end 审批相关*/	

/**begin 提交相关*/	
	/**人员移库后是否删除原库中的信息,1删除(默认值),0保留 */
	private String move_person="1";

	/**目标库，确认时，表单数据提交至的原始数据库*/
	private String  dest_base="";
	
	/**数据提交入库不判断子集和指标权限, 0判断(默认值),1不判断  */
	private String UnrestrictedMenuPriv="0";
	
	private String headCount_control="1"; //编制控制  1|无此属性：控制（默认）　  0表示不控制
	/**个人附件归档至主集,不归档至多媒体子集A00了 20160818增加  */
	private boolean archiveAttachToMainSet=false; 
	/**个人附件归档方式  */
	private String archive_attach_to="A00"; 
	/**个人附件归档到主集和多媒体集是否可以维护历史数据 */
	private boolean attach_history=false;
	
	private String   archflag="0";  //原始表单是否归档, 0否(默认值),1归档
	private String   autoCaculate="";  //申请时自动计算  0不计算(默认值),1计算
	private String   spAutoCaculate="";  //审批时时自动计算  0不计算(默认值),1计算	
	
	private ArrayList subUpdateList=new ArrayList();
	/**关联更新上条记录*/
	private HashMap linkmap=new HashMap();
	/**关联更新上条记录*/
	private HashMap linkdateflagmap=new HashMap();
	/**插入子集区域*/
	private HashMap submap=new HashMap();
	/***下通知单***/
    private String msg_flag="0";
    private ArrayList mag_condlist=new ArrayList();
    private ArrayList mag_condlist_complex=new ArrayList();
	/**消息通知模板对象，模板号数组*/
	private String[] msg_template;
/**end 提交相关*/	

	/**输出的高级花名册的号列表:for examples 1`2`3`*/
	private String muster_str="";
	/**登记表号表表for examples 1`2`3`*/
	private String card_str="";
	/**执行的工资标准,标准号数组*/
	private String[] gz_stand;
    
    /**存储其他临时参数 暂时保留*/
    private HashMap otherParaMap=new HashMap();
    private String unit_code_field="";   //单位代码指标
    private String pos_code_field="";    //岗位代码指标
    //返回标识详见template_util
    private String returnFlag="";
    /** 模板设置的通过逻辑判断得到的显示的页签集合*/
    private ArrayList outPriPageList = new ArrayList();
    private String needJudgPre = "";//展现人事异动页面以及导出pdf,word是否需要判断指标权限 1 需要 0 不需要
    private String autosize="0";//单元格字体自适应 1 勾选 0 不勾选
    private String autosync_beforechg_item = "0"; //自动同步变化前指标数据 默认 0 自动 1
    private Boolean isAotuLog=false;//自动记录变动日志
    private Boolean isRejectAotuLog=false;//驳回后启用记录变动日志
	private String autoLogColor="#FF0000";
	private String factor_update_type = "";//检索条件默认更新方式  1：清空模板已有记录重新加载、 2：不清空仅追加 3：有数据不检索
//常量
	
	
	private String kq_type="";            // kq_type:考勤方式 1:加班申请 q11  2:请假申请 q15   3：公出申请 q13
	private String kq_setid="";
	private String kq_field_mapping="";   // kq_field_mapping:指标对应关系
	public String getKq_type() {
		return kq_type;
	}

	public String getKq_setid() {
		return kq_setid;
	}

	public String getKq_field_mapping() {
		return kq_field_mapping;
	}

	
    
	
	/**初始化参数Bo类，将所有参数加载
	 * @param conn
	 * @param userview
	 * @param tabid 模板编号
	 * @throws GeneralException
	 */
	public TemplateParam(Connection conn, UserView userview,int tabid) 
	{
		this.conn = conn;
		this.userView=userview;		
		this.tabId = tabid;		
		this.table_vo=readTemplate(tabid);
		initdata();
	}
	
	/**初始化参数Bo类，不加载参数 或者按需加载
	 * @param conn
	 * @param userview
	 * @throws GeneralException
	 */
	public TemplateParam(Connection conn, UserView userview)
	{
		this.conn = conn;
		this.userView=userview;
	}
	/**读取模板Vo*/
	private RecordVo readTemplate(int tabid) 
	{
		 
		return TemplateUtilBo.getTableVo(tabid, this.conn);
	}

	/**加载参数*/
	private void initdata()
	{
		String sxml=null;
		if(this.table_vo!=null)
		{
			sxml=this.table_vo.getString("ctrl_para");
			parse_xml_param(sxml);			
			this.dest_base=this.table_vo.getString("dest_base");
			
			ArrayList dbList=DataDictionary.getDbpreList();
		    if(this.dest_base!=null&&this.dest_base.trim().length()>0)
		    {
				for(int i=0;i<dbList.size();i++)
				{
					String pre=(String)dbList.get(i);
					if(pre.equalsIgnoreCase(this.dest_base))
						this.dest_base=pre;
				}
		    }
			sxml=this.table_vo.getString("sp_flag");
			if(sxml==null|| "".equals(sxml)|| "0".equals(sxml))
				this.bsp_flag=false;
			else
				this.bsp_flag=true;
				
			sxml=this.table_vo.getString("gzstandid");
			//过滤没有启用的工资标准表  兼容脏数据
			boolean isStart = this.filterStandid(sxml);
			if(isStart)
				this.gz_stand = StringUtils.split(sxml,",");
			else
				this.gz_stand = new String [0];
			sxml=this.table_vo.getString("noticeid");	
			this.msg_template=StringUtils.split(sxml,",");
			this.content=this.table_vo.getString("content");
			this.name=this.table_vo.getString("name");
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				this.templateStatic=this.table_vo.getInt("static_o");
			}else {
				this.templateStatic=this.table_vo.getInt("static");
			}
			
			this.operationname=this.table_vo.getString("operationname");
			this.operationcode=this.table_vo.getString("operationcode");
			this.operationType=findOperationType(operationcode);
			this.factor=this.table_vo.getString("factor");
			this.llexpr=this.table_vo.getString("llexpr");
			
			
			if(this.templateStatic==10) //单位
			{
				this.infor_type=2;
				RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.conn);
				if(unit_code_field_constant_vo!=null)
				{
					this.unit_code_field=unit_code_field_constant_vo.getString("str_value");
					if(this.unit_code_field==null||this.unit_code_field.trim().length()<2)
						this.unit_code_field="";
				}
			}
			if(this.templateStatic==11) //职位
			{
				this.infor_type=3;
				RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("POS_CODE_FIELD",this.conn);
				if(unit_code_field_constant_vo!=null)
				{
					this.pos_code_field=unit_code_field_constant_vo.getString("str_value");
					if(this.pos_code_field==null||this.pos_code_field.trim().length()<2)
						this.pos_code_field="";
				}
			}
			
			
		}
	}
	
    /**
     * 过滤没有启用的工资标准表  兼容脏数据
     * @param sxml
     */
	private boolean filterStandid(String sxml) {
		StringBuffer sql=new StringBuffer("");
		RowSet rowSet = null;
		boolean  isStart = false;
		String standid = "";
		String [] standids =  StringUtils.split(sxml,",");
		for(int i=0;i<standids.length;i++){
			if(!"".equals(standids[i])){
				standid = standids[i];
				break;
			}
		}
		sql.append("select a.* from gz_stand_pkg a,gz_stand_history b where a.pkg_id=b.pkg_id and b.id='"+standid+"' and a.status=1");
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			rowSet = dao.search(sql.toString());
			if(rowSet.next()){
				isStart = true;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return isStart;
	}

	/**
	 * 解释业务模板定义的参数
	 * @param sxml
	 * @return
	 */
	private boolean parse_xml_param(String sxml)
	{
		boolean bflag=true;
		Document doc=null;
		Element element=null;
		if(sxml==null|| "".equals(sxml))
				return false;
		try
		{
			doc=PubFunc.generateDom(sxml);
			String xpath="/params/out";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			List childlist=findPath.selectNodes(doc);	
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				this.muster_str=(String)element.getAttributeValue("muster");
				if(!(this.muster_str==null|| "".equals(this.muster_str)))
				{
					this.muster_str=this.muster_str.substring(0,this.muster_str.length()-1);
				}
				this.card_str=(String)element.getAttributeValue("card");
				if(!(this.card_str==null|| "".equals(this.card_str)))
				{
					this.card_str=this.card_str.substring(0,this.card_str.length()-1);
				}				
			}
			
			
			 //filter_by_manage_priv 接收通知单数据方式：0接收全部数据，1接收管理范围内数据
			xpath="/params/receive_notice";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);			
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				this.filter_by_manage_priv=(String)element.getAttributeValue("filter_by_manage_priv");
			    if(element.getAttributeValue("include_suborg")!=null)
					 this.include_suborg=(String)element.getAttributeValue("include_suborg");
			    if(element.getAttributeValue("import_notice_data")!=null)
			    	this.import_notice_data=(String)element.getAttributeValue("import_notice_data");
			}
			
			//init_base
			xpath="/params/init_base";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);			
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				this.init_base=(String)element.getAttributeValue("name");
			}
			
			
		    //change_after_get_data 变化后指标取值方式,0:不取(默认值),1:取当前记录
			xpath="/params/menu";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);			
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				this.change_after_get_data=(String)element.getAttributeValue("change_after_get_data");
			}
			
			/**审批方法*/
			xpath="/params/sp_flag";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);			
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				this.sp_mode=Integer.parseInt((String)element.getAttributeValue("mode"));
				if(element.getAttribute("opinion_field")!=null)
					this.opinion_field=(String)element.getAttributeValue("opinion_field");
				if(this.sp_mode==1)
				{
					if(element.getAttributeValue("endusertype")!=null)
						this.endUserType=(String)element.getAttributeValue("endusertype");
					if(element.getAttributeValue("enduser")!=null)
						this.endUser=(String)element.getAttributeValue("enduser");
					
				}
				if(element.getAttributeValue("relation_id")!=null)
					this.Relation_id=(String)element.getAttributeValue("relation_id");
				if(element.getAttributeValue("def_flow_self")!=null)
				{
					this.def_flow_self=(String)element.getAttributeValue("def_flow_self");
					if("1".equals(this.def_flow_self))
						this.allow_defFlowSelf=true;
				}
				if(element.getAttributeValue("no_sp_yj")!=null){
				    this.no_sp_yj=(String)element.getAttributeValue("no_sp_yj");
				}
				if(element.getAttributeValue("sync_archive_data")!=null){
				    if ("1".equals((String)element.getAttributeValue("sync_archive_data")))
				        this.sp_syncArchiveData=true;
				}
				
				
				if(element.getAttribute("kq_type")!=null)
				{
					this.kq_type=((String)element.getAttributeValue("kq_type")).trim();
					// kq_type:考勤方式 1:加班申请 q11  2:请假申请 q15   3：公出申请 q13
					if("1".equals(this.kq_type))
						this.kq_setid="q11";
					else if("2".equals(this.kq_type))
						this.kq_setid="q15";
					else if("3".equals(this.kq_type))
						this.kq_setid="q13"; 
					else if("4".equals(this.kq_type))
						this.kq_setid="qxj"; 
					if(element.getAttribute("kq_field_mapping")!=null)
						this.kq_field_mapping=(String)element.getAttributeValue("kq_field_mapping"); 
				}
				
			}
			
			//人员移库后是否删除原库中的信息,1删除(默认值),0保留
			xpath="/params/move_person";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);			
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				this.move_person=(String)element.getAttributeValue("delete_source");
			}

			//拆单模式 
			xpath="/params/split_data";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);			
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				if(element.getAttributeValue("mode")!=null&&((String)element.getAttributeValue("mode")).trim().length()>0)
				{
					this.split_data_model=(String)element.getAttributeValue("mode");
					this.split_data_fields=(String)element.getAttributeValue("fields");
				}
			}
			
			
			xpath="/params/notes";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);			
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				this.bemail=(new Boolean((String)element.getAttributeValue("email"))).booleanValue();//发送邮件
				if(element.getAttributeValue("notice_initiator")!=null){//在提交时通知到发起人 xcs add@2014-04-01
				    this.notice_initiator=element.getAttributeValue("notice_initiator");
				}                                                       
				if(element.getAttributeValue("template_initiator")!=null){//通到发起人的模版  xcs add@2014-04-01
				    this.template_initiator=element.getAttributeValue("template_initiator");
				}
				this.bsms=(new Boolean((String)element.getAttributeValue("sms"))).booleanValue();//发送短信
				if(element.getAttribute("email_staff")!=null)
					this.email_staff=(new Boolean((String)element.getAttributeValue("email_staff"))).booleanValue();
				if(element.getAttribute("template_bos")!=null)
					this.template_bos=(String)element.getAttributeValue("template_bos");
				if(element.getAttribute("template_staff")!=null)
					this.template_staff=(String)element.getAttributeValue("template_staff");
				if(element.getAttribute("template_sp")!=null)
					this.template_sp=(String)element.getAttributeValue("template_sp");
				
			}
			/** 原始表单是否归档 */
			
			xpath="/params/updates";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);	
			String DefaultTransIn="0";
			if(childlist!=null&&childlist.size()>0)
			{ 
				element=(Element)childlist.get(0);
				if(element.getAttribute("archflag")!=null)
					this.archflag=(String)element.getAttributeValue("archflag");
				if(element.getAttribute("headCount_control")!=null)
					this.headCount_control=(String)element.getAttributeValue("headCount_control");
				if(element.getAttribute("autocalc")!=null)
					this.autoCaculate=(String)element.getAttributeValue("autocalc"); //0不计算(默认值),1计算
				if(element.getAttribute("spautocalc")!=null)
					this.spAutoCaculate=(String)element.getAttributeValue("spautocalc"); //0不计算(默认值),1计算	
				if(element.getAttribute("UnrestrictedMenuPriv")!=null)
					this.UnrestrictedMenuPriv=(String)element.getAttributeValue("UnrestrictedMenuPriv");
				if(element.getAttribute("UnrestrictedMenuPriv_Input")!=null)
					this.UnrestrictedMenuPriv_Input=(String)element.getAttributeValue("UnrestrictedMenuPriv_Input");
				if(element.getAttribute("unique_check")!=null)
					this.unique_check=(String)element.getAttributeValue("unique_check");
				if(element.getAttribute("DefaultTransIn")!=null)
					DefaultTransIn=(String)element.getAttributeValue("DefaultTransIn"); 
				if(element.getAttribute("id_gen_manual")!=null)
					this.id_gen_manual=(String)element.getAttributeValue("id_gen_manual"); 
				if(element.getAttribute("filter_by_factor")!=null)
					this.filter_by_factor=(String)element.getAttributeValue("filter_by_factor"); 
				if(element.getAttribute("no_priv_ctrl")!=null)
					this.no_priv_ctrl=(String)element.getAttributeValue("no_priv_ctrl"); 
				if(element.getAttribute("archive_attach_to_mainset")!=null){
					this.archiveAttachToMainSet="1".equals((String)element.getAttributeValue("archive_attach_to_mainset")); 
					this.archive_attach_to="old";
				}
				if(element.getAttribute("autosync_beforechg_item")!=null)
					this.autosync_beforechg_item=(String)element.getAttributeValue("autosync_beforechg_item"); 
				/**userview中设置了fillInfo=1 表明是从外部链接进入的特殊情况
				 * 需要特殊处理UnrestrictedMenuPriv和UnrestrictedMenuPriv_Input这两个参数
				 */
				if("1".equals(this.userView.getHm().get("fillInfo"))){
					this.UnrestrictedMenuPriv="1";
					this.UnrestrictedMenuPriv_Input="1";
				}
				
			}
			/**个人附件归档方式 */
			xpath="/params/archive_attach_to";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);			
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				this.archive_attach_to=(String)element.getText();
				if("A01".equalsIgnoreCase(this.archive_attach_to))
					this.archiveAttachToMainSet = true;
				else
					this.archiveAttachToMainSet = false;
			}
			/**维护个人附件历史数据*/
			xpath="/params/attach_history";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);			
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				String attach_history_value=(String)element.getText();
				if("1".equalsIgnoreCase(attach_history_value))
					this.attach_history = true;
				else
					this.attach_history = false;
			}
			/**子集记录更新方式*/
			xpath="/params/updates/update";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);
			if(childlist!=null&&childlist.size()>0)
			{
				for(int i=0;i<childlist.size();i++)
				{
					element=(Element)childlist.get(i);
					TSubsetCtrl subctrl=new TSubsetCtrl();
					subctrl.setSetcode((String)element.getAttributeValue("Name"));
					subctrl.setUpdatetype(Integer.parseInt(element.getAttributeValue("Type")));
					
					if(element.getAttributeValue("SubMenu")!=null)
						subctrl.setSubMenu(((String)element.getAttributeValue("SubMenu")).trim());
					
					if(element.getAttributeValue("Type")!=null&&Integer.parseInt(element.getAttributeValue("Type"))==3&&element.getAttributeValue("CondFormula")!=null)
						subctrl.setCondFormula(((String)element.getAttributeValue("CondFormula")).trim());
					else
						subctrl.setCondFormula("");
					if("0".equals(DefaultTransIn))
					{
						if(element.getAttributeValue("SysTransType")!=null)
							subctrl.setInnerupdatetype(Integer.parseInt(element.getAttributeValue("SysTransType")));
						else
						{
							if("A01".equalsIgnoreCase((String)element.getAttributeValue("Name")))
								subctrl.setInnerupdatetype(0);
							else
								subctrl.setInnerupdatetype(1);
						}
					}
					else
						subctrl.setInnerupdatetype(subctrl.getUpdatetype());
					if(element.getAttributeValue("RefPreRec")!=null)
						subctrl.setRefPreRec(Integer.parseInt(element.getAttributeValue("RefPreRec")));
					else
						subctrl.setRefPreRec(1);
					subUpdateList.add(subctrl);
				}//for loop end.
			}
			/**子集关联更新*/
			xpath="/params/linkupdates/lu";
			/**	<lu src="A010X" dest="A0103"/>*/
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);
			if(childlist!=null&&childlist.size()>0)
			{
				
				for(int i=0;i<childlist.size();i++)
				{
					element=(Element)childlist.get(i);
					String src=(String)element.getAttributeValue("src");
					String dest=(String)element.getAttributeValue("dest");
					linkmap.put(dest, src);
					if(element.getAttributeValue("dateflag")!=null){
						String dateflag = (String)element.getAttributeValue("dateflag");
							linkdateflagmap.put(dest.toLowerCase(), dateflag);
					}else{
						linkdateflagmap.put(dest.toLowerCase(), "0");
					}
				}//for loop end.
			}
			xpath="/params/msg_type";
			/*<msg_type flag=”0|1”>
			<path condid=”x”>1,2,</path>
			<path condid=”3”>1,2,</path>
		    </msg_type>*/
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			Element tete = (Element)findPath.selectSingleNode(doc);
			if(tete!=null)
			{
				this.msg_flag=tete.getAttributeValue("flag");
				if(this.msg_flag!=null&& "1".equals(this.msg_flag))
				{
					childlist=tete.getChildren();
					LazyDynaBean abean=null;
					if(childlist!=null&&childlist.size()>0)
					{
						for(int i=0;i<childlist.size();i++)
						{
							element=(Element)childlist.get(i);
							CommonData da=new CommonData();
							da.setDataName(element.getAttributeValue("condid"));
							String user="";
							if(element.getAttributeValue("user")!=null&&element.getAttributeValue("user").trim().length()>0)
								user=element.getAttributeValue("user").trim();
							
							abean=new LazyDynaBean();
							abean.set("condid", element.getAttributeValue("condid"));
							abean.set("user", user);
							abean.set("templateid",element.getText());
							
							da.setDataValue(element.getText());
							this.mag_condlist.add(da);
							this.mag_condlist_complex.add(abean);
						}
					}
				}else
				{
					this.msg_flag="0";
				}
				
			}
			xpath="/params/init_view";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);	
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				if(element.getAttribute("view")!=null)
					this.view=(String)element.getAttributeValue("view");
			}
			/**驳回方式 郭峰*/
			xpath="params";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			Element ele = (Element) findPath.selectSingleNode(doc);
			Element child;
			if (ele != null){
				child = ele.getChild("rejectFlag");
				if (child != null){
					this.reject_type = child.getTextTrim();
				}
			}
			/**导出文件字体自适应 **/
			xpath="/params/export";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);	
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				if(element.getAttribute("autosize")!=null)
					this.autosize=(String)element.getAttributeValue("autosize");
			}
			
			xpath="params";
			findPath = XPath.newInstance(xpath);
			ele = (Element) findPath.selectSingleNode(doc);
			if (ele != null){
				child = ele.getChild("autoLog");
				if (child != null){
					String value=child.getTextTrim();
					if("1".equalsIgnoreCase(value)){
						this.isAotuLog=true;
					}else{
						this.isAotuLog=false;
					}
					if("2".equalsIgnoreCase(value)){
						this.isRejectAotuLog=true;
					}else{
						this.isRejectAotuLog=false;
					}
				}
			}
			xpath="/params/autoLog";
			findPath = XPath.newInstance(xpath);
			childlist=findPath.selectNodes(doc);	
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				if(element.getAttribute("font_color")!=null)
					this.autoLogColor=(String)element.getAttributeValue("font_color");
			}
			
			xpath = "/params/index_cond";
			findPath = XPath.newInstance(xpath);
			childlist=findPath.selectNodes(doc);	
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				if(element.getAttribute("update_type")!=null)
					this.factor_update_type=(String)element.getAttributeValue("update_type");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
		//this.setDoc(doc);
		return bflag;
	}
	   /**
     * @Title: findOperationType
     * @Description: 业务类型 对人员调入的业务单独处理
     *               =0人员调入,=1调出（须指定目标人员库）,=2离退(须指定目标人员库),=3内部调动, =4系统内部调动
     *               =10其它不作特殊处理的业务 如果目标库未指定的话，则按源库进行处理
     * @param operationcode
     * @return
     * @throws int
     */
    private int findOperationType(String operationcode) {
    	//int flag = -1;
        // TODO Auto-generated method stub
    	/*
        RowSet rset = null;
        StringBuffer strsql = new StringBuffer();
        strsql.append("select operationtype from operation where operationcode='");
        strsql.append(operationcode);
        strsql.append("'");
        ContentDAO dao = new ContentDAO(this.conn);
        
        try {
            rset = dao.search(strsql.toString());
            if (rset.next())
                flag = rset.getInt("operationtype");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }*/
        int flag=TemplateStaticDataBo.getOperationType(operationcode, conn);
        return flag;
    }

	public boolean isDef_flow_self(int task_id) {
		try
		{
			if(this.tabId!=-1&& "1".equals(def_flow_self))
			{
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rset=null;
				if(task_id==0&&this.userView!=null)
				{ 
					rset=dao.search("select count(*) from t_wf_node_manual where  bs_flag='1' and  tabid="+this.tabId+" and create_user='"+this.userView.getUserName()+"'   and ins_id=-1");
					if(rset.next())
					{
						if(rset.getInt(1)==0)
							this.allow_defFlowSelf=false;
					} 
				} 
				else if(task_id>0)
				{
					rset=dao.search("select count(*) from t_wf_node_manual where tabid="+this.tabId+"  and ins_id=(select ins_id from t_wf_task where task_id="+task_id+")");
					if(rset.next())
					{
						if(rset.getInt(1)>0)
							this.allow_defFlowSelf=true;
						else
							this.allow_defFlowSelf=false;
					}
					 
				}
				if(rset!=null)
					rset.close();
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return allow_defFlowSelf;
	}
	
	/**获取主集编号 A01 B01 K01*/
	private String getMainSetId()
	{
		String setid= "A01" ;
		if (this.infor_type==2){
			setid="B01";
		}
		else if (this.infor_type==3){
			setid="K01";
		}
		return setid;
	}
	
	/**获取主集的主键*/
	private String getMainSetKeyFld()
	{
		String setid= "A0100" ;
		if (this.infor_type==2){
			setid="B0100";
		}
		else if (this.infor_type==3){
			setid="E01a1";
		}
		return setid.toLowerCase();
	}	
	
	
	
	public ContentDAO getDao() {
		return dao;
	}


	public void setDao(ContentDAO dao) {
		this.dao = dao;
	}


	public String getOperationcode() {
		return operationcode;
	}


	public void setOperationcode(String operationcode) {
		this.operationcode = operationcode;
	}


	public String getOperationname() {
		return operationname;
	}


	public void setOperationname(String operationname) {
		this.operationname = operationname;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getFilter_by_factor() {
		return filter_by_factor;
	}


	public void setFilter_by_factor(String filterByFactor) {
		filter_by_factor = filterByFactor;
	}


	public String getNo_priv_ctrl() {
		return no_priv_ctrl;
	}


	public void setNo_priv_ctrl(String noPrivCtrl) {
		no_priv_ctrl = noPrivCtrl;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public int getPixelInInch() {
		return PixelInInch;
	}


	public void setPixelInInch(int pixelInInch) {
		PixelInInch = pixelInInch;
	}


	public String getFactor() {
	    if (factor==null) {
	        factor="";
	    }
		return factor;
	}


	public void setFactor(String factor) {
		this.factor = factor;
	}


	public String getLlexpr() {
		return llexpr;
	}


	public void setLlexpr(String llexpr) {
		this.llexpr = llexpr;
	}


	public String getView() {
		return view;
	}


	public void setView(String view) {
		this.view = view;
	}


	public String getInit_base() {
		return init_base;
	}


	public void setInit_base(String initBase) {
		init_base = initBase;
	}


	public String getChange_after_get_data() {
		return change_after_get_data;
	}


	public void setChange_after_get_data(String changeAfterGetData) {
		change_after_get_data = changeAfterGetData;
	}


	public String getFilter_by_manage_priv() {
		return filter_by_manage_priv;
	}


	public void setFilter_by_manage_priv(String filterByManagePriv) {
		filter_by_manage_priv = filterByManagePriv;
	}


	public String getInclude_suborg() {
		return include_suborg;
	}


	public void setInclude_suborg(String includeSuborg) {
		include_suborg = includeSuborg;
	}


	public String getUnrestrictedMenuPriv_Input() {
		return UnrestrictedMenuPriv_Input;
	}


	public void setUnrestrictedMenuPriv_Input(String unrestrictedMenuPrivInput) {
		UnrestrictedMenuPriv_Input = unrestrictedMenuPrivInput;
	}


	public String getId_gen_manual() {
		return id_gen_manual;
	}


	public void setId_gen_manual(String idGenManual) {
		id_gen_manual = idGenManual;
	}


	public String getExistid_gen_manual() {
		return existid_gen_manual;
	}


	public void setExistid_gen_manual(String existidGenManual) {
		existid_gen_manual = existidGenManual;
	}


	public String getUnique_check() {
		return unique_check;
	}


	public void setUnique_check(String uniqueCheck) {
		unique_check = uniqueCheck;
	}


	public String getOpinion_field() {
	    if (opinion_field==null) opinion_field="";
		return opinion_field;
	}


	public void setOpinion_field(String opinionField) {
		opinion_field = opinionField;
	}


	public String getEndUserType() {
		if (endUserType==null){
			endUserType="";
		}
		return endUserType;
	}


	public void setEndUserType(String endusertype) {
		this.endUserType = endusertype;
	}


	public String getEndUser() {
		if (endUser==null){
			endUser="";
		}
		return endUser;
	}


	public void setEndUser(String enduser) {
		this.endUser = enduser;
	}


	public String getReject_type() {
		return reject_type;
	}


	public void setReject_type(String rejectType) {
		reject_type = rejectType;
	}


	public String getDef_flow_self() {
		return def_flow_self;
	}


	public void setDef_flow_self(String defFlowSelf) {
		def_flow_self = defFlowSelf;
	}


	public String getNo_sp_yj() {
		return no_sp_yj;
	}


	public void setNo_sp_yj(String noSpYj) {
		no_sp_yj = noSpYj;
	}


	public boolean isBsp_flag() {
		return bsp_flag;
	}


	public void setBsp_flag(boolean bspFlag) {
		bsp_flag = bspFlag;
	}


	public int getSp_mode() {
		return sp_mode;
	}


	public void setSp_mode(int spMode) {
		sp_mode = spMode;
	}


	public boolean isAllow_defFlowSelf() {
		return allow_defFlowSelf;
	}


	public void setAllow_defFlowSelf(boolean allowDefFlowSelf) {
		allow_defFlowSelf = allowDefFlowSelf;
	}


	public String getRelation_id() {
		return Relation_id;
	}


	public void setRelation_id(String relationId) {
		Relation_id = relationId;
	}


	public boolean isBemail() {
		return bemail;
	}


	public void setBemail(boolean bemail) {
		this.bemail = bemail;
	}


	public boolean isBsms() {
		return bsms;
	}


	public void setBsms(boolean bsms) {
		this.bsms = bsms;
	}


	public boolean isEmail_staff() {
		return email_staff;
	}


	public void setEmail_staff(boolean emailStaff) {
		email_staff = emailStaff;
	}


	public String getTemplate_staff() {
		return template_staff;
	}


	public void setTemplate_staff(String templateStaff) {
		template_staff = templateStaff;
	}


	public String getTemplate_bos() {
		return template_bos;
	}


	public void setTemplate_bos(String templateBos) {
		template_bos = templateBos;
	}


	public String getTemplate_sp() {
		return template_sp;
	}


	public void setTemplate_sp(String templateSp) {
		template_sp = templateSp;
	}


	public String getNotice_initiator() {
		return notice_initiator;
	}


	public void setNotice_initiator(String noticeInitiator) {
		notice_initiator = noticeInitiator;
	}


	public String getTemplate_initiator() {
		return template_initiator;
	}


	public void setTemplate_initiator(String templateInitiator) {
		template_initiator = templateInitiator;
	}


	public String getSplit_data_model() {
		return split_data_model;
	}


	public void setSplit_data_model(String splitDataModel) {
		split_data_model = splitDataModel;
	}


	public String getSplit_data_fields() {
		return split_data_fields;
	}


	public void setSplit_data_fields(String splitDataFields) {
		split_data_fields = splitDataFields;
	}


	public String getMove_person() {
		return move_person;
	}


	public void setMove_person(String movePerson) {
		move_person = movePerson;
	}


	public String getDest_base() {
		return dest_base;
	}


	public void setDest_base(String destBase) {
		dest_base = destBase;
	}


	public String getUnrestrictedMenuPriv() {
		return UnrestrictedMenuPriv;
	}


	public void setUnrestrictedMenuPriv(String unrestrictedMenuPriv) {
		UnrestrictedMenuPriv = unrestrictedMenuPriv;
	}


	public String getHeadCount_control() {
		return headCount_control;
	}


	public void setHeadCount_control(String headCountControl) {
		headCount_control = headCountControl;
	}


	public String getArchflag() {
		return archflag;
	}


	public void setArchflag(String archflag) {
		this.archflag = archflag;
	}


	public String getAutoCaculate() {
		return autoCaculate;
	}


	public void setAutoCaculate(String autoCaculate) {
		this.autoCaculate = autoCaculate;
	}


	public ArrayList getSubUpdateList() {
		return subUpdateList;
	}


	public void setSubUpdateList(ArrayList subUpdateList) {
		this.subUpdateList = subUpdateList;
	}


	public HashMap getLinkmap() {
		return linkmap;
	}


	public void setLinkmap(HashMap linkmap) {
		this.linkmap = linkmap;
	}


	public HashMap getLinkdateflagmap() {
		return linkdateflagmap;
	}


	public void setLinkdateflagmap(HashMap linkdateflagmap) {
		this.linkdateflagmap = linkdateflagmap;
	}


	public HashMap getSubmap() {
		return submap;
	}


	public void setSubmap(HashMap submap) {
		this.submap = submap;
	}


	public String getMsg_flag() {
		return msg_flag;
	}


	public void setMsg_flag(String msgFlag) {
		msg_flag = msgFlag;
	}


	public ArrayList getMag_condlist() {
		return mag_condlist;
	}


	public void setMag_condlist(ArrayList magCondlist) {
		mag_condlist = magCondlist;
	}


	public ArrayList getMag_condlist_complex() {
		return mag_condlist_complex;
	}


	public void setMag_condlist_complex(ArrayList magCondlistComplex) {
		mag_condlist_complex = magCondlistComplex;
	}


	public String[] getMsg_template() {
		return msg_template;
	}


	public void setMsg_template(String[] msgTemplate) {
		msg_template = msgTemplate;
	}


	public String getMuster_str() {
		return muster_str;
	}


	public void setMuster_str(String musterStr) {
		muster_str = musterStr;
	}


	public String getCard_str() {
		return card_str;
	}


	public void setCard_str(String cardStr) {
		card_str = cardStr;
	}


	public String[] getGz_stand() {
		return gz_stand;
	}


	public void setGz_stand(String[] gzStand) {
		gz_stand = gzStand;
	}


	public HashMap getOtherParaMap() {
		return otherParaMap;
	}


	public void setOtherParaMap(HashMap otherParaMap) {
		this.otherParaMap = otherParaMap;
	}


	public String getUnit_code_field() {
		return unit_code_field;
	}


	public void setUnit_code_field(String unitCodeField) {
		unit_code_field = unitCodeField;
	}


	public String getPos_code_field() {
		return pos_code_field;
	}


	public void setPos_code_field(String posCodeField) {
		pos_code_field = posCodeField;
	}


	
	//get set 方法
	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public UserView getUserView() {
		return userView;
	}

	public void setUserView(UserView userView) {
		this.userView = userView;
	}


	public RecordVo getTable_vo() {
		return table_vo;
	}

	public void setTable_vo(RecordVo tableVo) {
		table_vo = tableVo;
	}
	
	public int getTabId() {
		return tabId;
	}

	public void setTabId(int tabId) {
		this.tabId = tabId;
	}

	public int getOperationType() {
		return operationType;
	}

	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}

	public int getTemplateStatic() {
		return templateStatic;
	}

	public void setTemplateStatic(int templateStatic) {
		this.templateStatic = templateStatic;
	}

	public int getInfor_type() {
		return infor_type;
	}

	public void setInfor_type(int inforType) {
		infor_type = inforType;
	}

    public boolean isSp_syncArchiveData() {
        return sp_syncArchiveData;
    }

    public void setSp_syncArchiveData(boolean sp_syncArchiveData) {
        this.sp_syncArchiveData = sp_syncArchiveData;
    }

    
    public TemplateModuleParam getTemplateModuleParam() {
    	return new TemplateModuleParam(this.conn,this.userView); 
    }
    
	public boolean isArchiveAttachToMainSet() {
		return archiveAttachToMainSet;
	}

	public void setArchiveAttachToMainSet(boolean archiveAttachToMainSet) {
		this.archiveAttachToMainSet = archiveAttachToMainSet;
	}
	
	public String getSpAutoCaculate() {
		return spAutoCaculate;
	}

	public String getArchive_attach_to() {
		return archive_attach_to;
	}

	public void setArchive_attach_to(String archive_attach_to) {
		this.archive_attach_to = archive_attach_to;
	}

	public String getReturnFlag() {
		return returnFlag;
	}

	public void setReturnFlag(String returnFlag) {
		this.returnFlag = returnFlag;
	}
	
	public ArrayList getOutPriPageList() {
		return outPriPageList;
	}
	
	public void setOutPriPageList(ArrayList outPriPageList) {
		this.outPriPageList = outPriPageList;
	}
	
	public String getNeedJudgPre() {
		return needJudgPre;
	}

	public void setNeedJudgPre(String needJudgPre) {
		this.needJudgPre = needJudgPre;
	}
	
	public String getAutosize() {
		return autosize;
	}

	public void setAutosize(String autosize) {
		this.autosize = autosize;
	}

	public String getAutosync_beforechg_item() {
		return autosync_beforechg_item;
	}

	public void setAutosync_beforechg_item(String autosync_beforechg_item) {
		this.autosync_beforechg_item = autosync_beforechg_item;
	}

	public String getImport_notice_data() {
		return import_notice_data;
	}

	public void setImport_notice_data(String import_notice_data) {
		this.import_notice_data = import_notice_data;
	}
	public Boolean getIsAotuLog() {
		return isAotuLog;
	}

	public void setIsAotuLog(Boolean isAotuLog) {
		this.isAotuLog = isAotuLog;
	}

	public String getAutoLogColor() {
		return autoLogColor;
	}

	public void setAutoLogColor(String autoLogColor) {
		this.autoLogColor = autoLogColor;
	}
	
	public Boolean getIsRejectAotuLog() {
		return isRejectAotuLog;
	}

	public void setIsRejectAotuLog(Boolean isRejectAotuLog) {
		this.isRejectAotuLog = isRejectAotuLog;
	}
	
	public boolean isAttach_history() {
		return attach_history;
	}

	public void setAttach_history(boolean attach_history) {
		this.attach_history = attach_history;
	}

	public String getFactor_update_type() {
		return factor_update_type;
	}

	public void setFactor_update_type(String factor_update_type) {
		this.factor_update_type = factor_update_type;
	}

}
