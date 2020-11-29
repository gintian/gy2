/**
 * 
 */
package com.hjsj.hrms.actionform.general.template;

import com.hjsj.hrms.valueobject.ykcard.CardTagParamView;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:TemplateForm</p>
 * <p>Description:业务变动的表单</p> 
 * <p>Company:hjsj</p> 
 * create time at:Sep 26, 200611:44:29 AM
 * @author chenmengqing
 * @version 4.0
 */
public class TemplateForm extends FrameForm {
  
	private String photo_maxsize="0";
	private String endusertype="";    //手工指派模式最终办理人类型, 0: 用户, 1: 人员, 空表示未指定
	private String enduser="";        //手工指派模式最终办理人, enduser保存用户名或人员编号(人员库+A0100), 空表示未指定
	private String enduser_fullname="";
	private String navigation="none";//来自合同办理是否要显示返回按钮
	 
	/** 
     * @return navigation 
     */
    public String getNavigation() {
        return navigation;
    }

    /** 
     * @param navigation 要设置的 navigation 
     */
    public void setNavigation(String navigation) {
        this.navigation = navigation;
    }

    /**预警号*/
	private String warn_id;

	/**左边业务模板树形结构*/
	private String bs_tree;
	/**业务类型
	 * =1,日常管理
	 * =2,工资管理
	 * =3,警衔管理
	 * =8,保险管理
	 * 
	 * //1:人事异动；2：是薪资管理	；8：保险变动；21：劳动合同；12：出国管理 ;10:单位管理机构调整;11:岗位管理机构调整   23：考勤业务办理  24：非考勤业务(业务申请不包含考勤信息)
	 */
	private String type;
	
	private String isFinishedRecord="0";   //是否是已结束的单子
	private String _static="1";
	
	/**资源类型*/
	private String bostype;//=true 按业务分类展示菜单树    =false 按模板展示菜单树
	private String res_flag;
	/**
	 * 人事异动业务类型
	 * "s人员调入"单独处理
	 */
	private String operationtype;
	/**数据库前缀串,for examples Usr,Trs*/
	private String strpres;
	/**页号*/
	private String pageno;
	/**模板号*/
	private String tabid;
	/**模板名称*/
	private String name;
	/**数据集名称*/
	private String setname;
	/**前台表单保存是否刷新数据*/
	private String refresh="false";
	private String actor_type=null;//bug 43677 无法发送短信通知 回传选择的是用户还是人员
	
	/**模板页列表*/
	private ArrayList pagelist=new ArrayList();
	/**打印输出单据列表
	 * 包括高级花名册，登记表,以及打印输出WORD,EXCEL模板
	 */
	private ArrayList outformlist=new ArrayList();
	private String hmtlview;
	/**设置为不打印的表页,主要用于输出单位的意见表*/
	private ArrayList noprintlist=new ArrayList();
	/**紧急程序
	 * =1 特急
	 * =2 紧急
	 * =3 一般
	 * =4 不紧迫
	 * */
	private String emergency="#";
	private ArrayList emergencylist=new ArrayList();
	/**
	 * 01 =同意
	 * 02 =不同意
	 * 03 =其它
	 */
	private String sp_yj="#";
	private ArrayList sp_yjlist=new ArrayList();
	/**任务议题*/
	private String topic="";
	/**对送对象类型
	 * =1,具体审批人,=2角色 =3组织单位,
	 * */
	private String objecttype="#";
	private ArrayList rolelist=new ArrayList();
	private String sp_mainbody="#";
	private String signLogo="noHand";
	/**
	 * 流程实例
	 * 发起申请时，流程实例为0
	 */
	private String ins_id="0";
	/**任务号*/
	private String taskid="0";
	
	/**同一个业务模板多个任务进行审批处理 */
	private String ins_ids="";
	
	/**是否为操作人发起的任务 */
	private String starttask="0";
	
	/**开始节点标识
	 *=0 非开始节点
	 *=1 开始节点，也即是发起申请的节点　
	 */
	private String startflag="0";
	/**审批标识
	 * =1待批
	 * =2已批
	 * =3申请
	 * */
	private String sp_flag="1";
	/**批量审批控制方式=0单个任务审批,=1多个任务审批,前提条件是同一业务模板*/
	private String sp_batch="0";
	private ArrayList tasklist=new ArrayList();
	/**批量任务号*/
	private String batch_task="";
	private String photofile="";
	/**
	 * 审批模式
	 * =0自动流转(不用填写意见及选择审批对象)，=1手工指派
	 * */
	private String sp_mode="0"; 
	private String isEndTask_flow="false"; //是否是自定义流程最后待处理的任务
	
	private String sp_objname="";
	private String def_flow_self="0";//自定义审批流程 0、1	wangrd 2013-12-13
	private String allow_def_flow_self="false";
	
	/**当前任务处理各个环节处理的意见的列表*/
    private PaginationForm sp_yjListForm=new PaginationForm();
    /**审批控制环节
     * =0 不需要审批，数据直接进档案库
     * =1 需要审批，数据经过审批批准后，数据方可进档案库
     * */
    private String sp_ctrl="0";
    private String content;

    /**模块标志,加密锁方式*/
    private String module;    
    /**选人时，第一人的应用库前缀及人员编号*/
    private String basepre;
    private String a0100;
    
    private String b0110="";
    private String e01a1="";
    private String infor_type="1";   //1:人员  2：组织  3：职位
    private String priv_html="";
    
    private String tablename;
    /**返回标志1返回到我的信息。2返回到监控信息*/
    private String returnflag;
    /**规则条件*/
    private String llexpr;
    private String judgeisllexpr;
    /**检索条件表达式*/
    private String sys_filter_factor;
    /**不用串行化*/
    transient private  FormFile picturefile; 
	private String checkhmuster; //判断有没有高级花名册
	transient private CardTagParamView cardparam=new CardTagParamView();
	private String openseal="";
	private String operationcode="";
	private String operationname="";
	private String staticid="";
	private ArrayList templist=new ArrayList();
	
	private String taskState="";  //活动状态
	private String a0101s="";
	private String tableName="";
	private String nodeprive="-1";

	///非审批模板发送消息
	String isSendMessage="0";
	String user_="";
	String user_h="";
	String title="";
	String context="";
	ArrayList mailTempletList=new ArrayList(); //邮件模板列表
	String mailTempletID="";
	String email_staff="false";
	String email_staff_value="";
	String template_staff="";
	String template_bos="";
	
	//针对审批中一个节点多个用户处理报批，判断其他审批人是否已处理完
	String isFinishTask="0";
	ArrayList rejectObjList=new ArrayList();
	String  isApplySpecialRole="0";  //是否自动报送给 角色属性为汇报关系 “直接领导”、“主管领导”，“第三级领导”、“第四级领导”、“全部领导”，属性值各自为“9，10，11，12，13”。
	
	String  businessModel="0";  //1:考核申诉
	String  businessModel_yp="0";  //1:考核申诉
	String  num="0";  
	
	/** 普天代办 id */
	String pre_pendingID="";
	//高级花名册的人员信息sql
	String hmuster_sql="";
	String filterStr="";     	//人员筛选
	private String sequence="0";
	private String signxml="";
	private String username="";
	
	private String filter_by_factor="0";  //手工选人、条件选人按检索条件过滤, 0不过滤(默认值),1过滤
	private  String no_priv_ctrl="0"; //手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按

	private String nextNodeStr="";  //自动流转下一节点id串
	private HashMap specialRoleMap=new HashMap();   //特殊角色包含的用户
	private String index_template="";

	private String limit_manage_priv="";
	private String generalmessage="";
	private ArrayList affixList = new ArrayList();//附件列表
	private FormFile filecontent; 
	private String file_id;
	private String uploadattach;//附件中控制是否能上传附件
	private String selfplatform;//=1 自助平台  =null 业务平台   附件用到这个变量 郭峰
	private String isFileViewer;//只有当模板中有照片和附件时，才加载fileviewer.ocx控件  1：有附件或照片  0:没有  郭峰
	private String isDisSubMeetingButton;//是否显示上会按钮 2015-10-20
	private ArrayList targitemlist=new ArrayList();//批量修改  郭峰
	private String attachmentcount="0";//模板中附件区域的总个数 郭峰
	private String attachmentareatotype;//附件下标与附件类型的对应关系 郭峰
	private ArrayList mediasortList = new ArrayList();//多媒体分类
	private String mediasortid = "";//选中的多媒体分类  如果没有，则数据库中存入的值为-9999
	
	private String page_num="1";  //页码
	private String pageCount="1";	
	
	private String no_sp_yj="0";//是否要填写审批意见 0:填写1：不填
	
	private String objectId="";//通过链接打开卡片时，只显示此ID的数据，此数据为加密数据


	/** 
     * @return no_sp_yj 
     */
    public String getNo_sp_yj() {
        return no_sp_yj;
    }

    /** 
     * @param noSpYj 要设置的 no_sp_yj 
     */
    public void setNo_sp_yj(String noSpYj) {
        no_sp_yj = noSpYj;
    }

    public String getOperationcode() {
		return operationcode;
	}

	public void setOperationcode(String operationcode) {
		this.operationcode = operationcode;
	}

	public ArrayList getTemplist() {
		return templist;
	}

	public void setTemplist(ArrayList templist) {
		this.templist = templist;
	}

	public String getOpenseal() {
		return openseal;
	}

	public void setOpenseal(String openseal) {
		this.openseal = openseal;
	}

	public CardTagParamView getCardparam() {
		return cardparam;
	}

	public void setCardparam(CardTagParamView cardparam) {
		this.cardparam = cardparam;
	}

	public FormFile getPicturefile() {
		return picturefile;
	}

	public void setPicturefile(FormFile picturefile) {
		this.picturefile = picturefile;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public ArrayList getEmergencylist() {
		return emergencylist;
	}

	public void setEmergencylist(ArrayList emergencylist) {
		this.emergencylist = emergencylist;
	}

	public ArrayList getRolelist() {
		return rolelist;
	}

	public void setRolelist(ArrayList rolelist) {
		this.rolelist = rolelist;
	}

	public String getEmergency() {
		return emergency;
	}

	public void setEmergency(String emergency) {
		this.emergency = emergency;
	}

	public String getHmtlview() {
		return hmtlview;
	}

	public String getFilterStr() {
		return filterStr;
	}

	public void setFilterStr(String filterStr) {
		this.filterStr = filterStr;
	}

	public void setHmtlview(String hmtlview) {
		this.hmtlview = hmtlview;
	}

	public String getPageno() {
		return pageno;
	}

	public void setPageno(String pageno) {
		this.pageno = pageno;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
		this.setname="templet_"+String.valueOf(tabid);
	}

	public String getBs_tree() {
		return bs_tree;
	}

	public void setBs_tree(String bs_tree) {
		this.bs_tree = bs_tree;
	}

	@Override
    public void outPutFormHM() {
		
		this.setTopic((String)this.getFormHM().get("topic"));
		
		this.setPageCount((String)this.getFormHM().get("pageCount"));
		this.setPage_num((String)this.getFormHM().get("page_num"));
		this.setRefresh((String)this.getFormHM().get("refresh"));
		this.setNo_priv_ctrl((String)this.getFormHM().get("no_priv_ctrl"));
		 
		this.setSignLogo((String)this.getFormHM().get("signLogo")); 
		this.setSp_flag((String)this.getFormHM().get("sp_flag")); 
		
		this.setSpecialRoleMap((HashMap)this.getFormHM().get("specialRoleMap"));
		this.setNextNodeStr((String)this.getFormHM().get("nextNodeStr"));
		this.setFilter_by_factor((String)this.getFormHM().get("filter_by_factor"));
		
		this.setIsFinishedRecord((String)this.getFormHM().get("isFinishedRecord"));
		this.set_static((String)this.getFormHM().get("_static"));
		this.setStarttask((String)this.getFormHM().get("starttask"));
		this.setPriv_html((String)this.getFormHM().get("priv_html"));
		this.setB0110((String)this.getFormHM().get("b0110"));
		this.setE01a1((String)this.getFormHM().get("e01a1"));
		this.setInfor_type((String)this.getFormHM().get("infor_type"));
		this.setType((String)this.getFormHM().get("type"));
		this.setNum((String)this.getFormHM().get("num"));
		this.setHmuster_sql((String)this.getFormHM().get("hmuster_sql"));
		this.setEnduser_fullname((String)this.getFormHM().get("enduser_fullname"));
		this.setEnduser((String)this.getFormHM().get("enduser"));
		this.setEndusertype((String)this.getFormHM().get("endusertype"));
		
		this.setSp_yj((String)this.getFormHM().get("sp_yj"));
		this.setEmergency((String)this.getFormHM().get("emergency"));
		
		this.setPre_pendingID((String)this.getFormHM().get("pre_pendingID"));
		this.setBusinessModel((String)this.getFormHM().get("businessModel"));
		this.setIns_ids((String)this.getFormHM().get("ins_ids"));
		
		this.setIsApplySpecialRole((String)this.getFormHM().get("isApplySpecialRole"));
		this.setMailTempletID((String)this.getFormHM().get("mailTempletID"));
		this.setMailTempletList((ArrayList)this.getFormHM().get("mailTempletList"));
		this.setEmail_staff((String)this.getFormHM().get("email_staff"));
		this.setTemplate_bos((String)this.getFormHM().get("template_bos"));
		this.setTemplate_staff((String)this.getFormHM().get("template_staff"));
		this.setIsSendMessage((String)this.getFormHM().get("isSendMessage"));
		
		this.setIsFinishTask((String)this.getFormHM().get("isFinishTask"));
		this.setRejectObjList((ArrayList)this.getFormHM().get("rejectObjList"));
		
		this.setUser_((String)this.getFormHM().get("user_"));
		this.setUser_h((String)this.getFormHM().get("user_h"));
		this.setTitle((String)this.getFormHM().get("title"));
		this.setContext((String)this.getFormHM().get("context"));
		
		this.setTaskState((String)this.getFormHM().get("taskState"));
		this.setPhoto_maxsize((String)this.getFormHM().get("photo_maxsize"));
		this.setBs_tree((String)this.getFormHM().get("bs_tree"));
		this.setPagelist((ArrayList)this.getFormHM().get("pagelist"));
		this.setHmtlview((String)this.getFormHM().get("htmlview"));
		this.setSetname((String)this.getFormHM().get("setname"));
		this.setName((String)this.getFormHM().get("name"));
		this.setOutformlist((ArrayList)this.getFormHM().get("outformlist"));
		this.setEmergencylist((ArrayList)this.getFormHM().get("emergencylist"));
		this.setSp_yjlist((ArrayList)this.getFormHM().get("sp_yjlist"));
		this.setRolelist((ArrayList)this.getFormHM().get("rolelist"));
		this.setOperationtype((String)this.getFormHM().get("operationtype"));
		this.setStrpres((String)this.getFormHM().get("dbpres"));
		this.getSp_yjListForm().setList((ArrayList)this.getFormHM().get("curryjlist"));
		this.setSp_ctrl((String)this.getFormHM().get("sp_ctrl"));
		this.setContent((String)this.getFormHM().get("content"));
		this.setNoprintlist((ArrayList)this.getFormHM().get("noprintlist"));
		this.setSp_mode((String)this.getFormHM().get("sp_mode"));
		this.setDef_flow_self((String)this.getFormHM().get("def_flow_self"));
		this.setIsEndTask_flow((String)this.getFormHM().get("isEndTask_flow"));
		this.setSp_objname((String)this.getFormHM().get("applyobj"));
		this.setAllow_def_flow_self((String)this.getFormHM().get("allow_def_flow_self"));
		this.setBatch_task((String)this.getFormHM().get("batch_task"));
		this.setLlexpr((String)this.getFormHM().get("llexpr"));
		this.setJudgeisllexpr((String)this.getFormHM().get("judgeisllexpr"));
		this.setCheckhmuster((String)this.getFormHM().get("checkhmuster"));
		if(this.getFormHM().get("sp_flag")!=null)
			this.setSp_flag((String)this.getFormHM().get("sp_flag"));
		this.setSys_filter_factor((String)this.getFormHM().get("sys_filter_factor"));
		
		this.setStartflag((String)this.getFormHM().get("startflag"));
		this.setPhotofile((String)this.getFormHM().get("photofile"));
		this.setOpenseal((String)this.getFormHM().get("openseal"));
		this.setOperationcode((String)this.getFormHM().get("operationcode"));
		this.setOperationname((String)this.getFormHM().get("operationname"));
		this.setTemplist((ArrayList)this.getFormHM().get("templist"));
		this.setStaticid((String)this.getFormHM().get("staticid"));
		this.setBostype((String)this.getFormHM().get("bostype"));
		this.setFilterStr((String)this.getFormHM().get("filterStr"));
		this.setA0101s((String)this.getFormHM().get("a0101s"));
		this.setTableName((String)this.getFormHM().get("tableName"));
		this.setSequence((String)this.getFormHM().get("sequence"));
		this.setSignxml((String)this.getFormHM().get("signxml")); 
		this.setUsername((String)this.getFormHM().get("username"));
		this.setLimit_manage_priv((String)this.getFormHM().get("limit_manage_priv"));
		this.setGeneralmessage((String)this.getFormHM().get("generalmessage"));
		this.setBusinessModel_yp((String)this.getFormHM().get("businessModel_yp"));
		this.setTaskid((String)this.getFormHM().get("taskid"));//慎重使用sp_flag=1,ins_id!=0时，taskid应该为0
		this.setIndex_template((String)this.getFormHM().get("index_template"));
		this.setNodeprive((String)this.getFormHM().get("nodeprive"));
		this.setAffixList((ArrayList)this.getFormHM().get("affixList"));
		this.setUploadattach((String)this.getFormHM().get("uploadattach"));
		this.setSelfplatform((String)this.getFormHM().get("selfplatform"));
		this.setIsFileViewer((String)this.getFormHM().get("isFileViewer"));
		this.setIsDisSubMeetingButton((String)this.getFormHM().get("isDisSubMeetingButton"));
		this.setTargitemlist((ArrayList)this.getFormHM().get("targitemlist"));
		this.setIns_id((String)this.getFormHM().get("ins_id"));
		this.setAttachmentcount((String)this.getFormHM().get("attachmentcount"));
		this.setAttachmentareatotype((String)this.getFormHM().get("attachmentareatotype"));
		this.setMediasortList((ArrayList)this.getFormHM().get("mediasortList"));
		this.setMediasortid((String)this.getFormHM().get("mediasortid"));
		this.setNo_sp_yj((String) this.getFormHM().get("no_sp_yj"));
		this.setObjectId((String) this.getFormHM().get("objectId"));
		this.setActor_type((String) this.getFormHM().get("actor_type"));
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		this.getFormHM().put("session",arg1.getSession());
        /**加密锁*/
        this.getFormHM().put("lock",arg1.getSession().getServletContext().getAttribute("lock"));
		this.getFormHM().put("module",this.getModule());
		//szk从邮件进时，关闭
		if("/general/template/edit_page".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null && "true".equals(arg1.getParameter("isemail"))){
			arg1.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
		}
		if("/general/template/upload_picture".equals(arg0.getPath())
		        &&arg1.getParameter("b_save")!=null){
		        arg1.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
		}
		if("/general/template/upload_attachment".equals(arg0.getPath()) && arg1.getParameter("b_save")!=null){
		        arg1.setAttribute("targetWindow", "1");//0不显示按钮 |1关闭|默认为返回
		}
        if("/general/template/upload_attachment".equals(arg0.getPath())
                && arg1.getParameter("b_select")!=null){
            arg1.setAttribute("targetWindow", "1");
        }
		if("/general/template/search_bs_tree".equals(arg0.getPath()) && arg1.getParameter("dht")!=null&& "htbl".equalsIgnoreCase(arg1.getParameter("dht"))){//来自合同办理导航图
		   this.setNavigation("htbl");
		}else if("/general/template/search_bs_tree".equals(arg0.getPath())){
		    this.setNavigation("none");
		}
		if("/general/template/search_bs_tree".equals(arg0.getPath())){//这样代表这来自菜单中的业务办理,或者说来自于合同办理的导航图
		    HttpSession session = arg1.getSession();
		    TaskDeskForm approvedTaskForm =(TaskDeskForm)session.getAttribute("approvedTaskForm");//已办任务的Form，要将翻页的数据置为1
		    TaskDeskForm taskDeskForm = (TaskDeskForm)session.getAttribute("taskDeskForm");//待办任务,将要翻页的数据置为1
		    TaskDeskForm ownerApplyForm = (TaskDeskForm)session.getAttribute("ownerApplyForm");//我的申请,将要翻页的数据置为1
		    TaskDeskForm monitorTaskForm = (TaskDeskForm)session.getAttribute("monitorTaskForm");//我的申请,将要翻页的数据置为1
		    
		    if(approvedTaskForm!=null){
		        approvedTaskForm.getTaskListForm().getPagination().gotoPage(1);//将已办任务的From的翻页数据置为1
		        approvedTaskForm.setQuery_type("1");//将查询方式置为默认按照天数查询
		        approvedTaskForm.setDays("10");//将默认查询方式的天数置为10天
		        approvedTaskForm.setStart_date("");//将开始日期置空
		        approvedTaskForm.setEnd_date("");//将结束日期置空
		        approvedTaskForm.setTemplateId("-1");//将查询模版ID置为全部
		    }
		    
		    if(taskDeskForm!=null){
		        taskDeskForm.getTaskListForm().getPagination().gotoPage(1);//将待办任务的From的翻页数据置为1
		        taskDeskForm.setQuery_type("1");//将查询方式置为默认按照天数查询
		        taskDeskForm.setDays("0");//置为默认
		        taskDeskForm.setStart_date("");//将开始日期置空
		        taskDeskForm.setEnd_date("");//将结束日期置空
		        taskDeskForm.setTemplateId("-1");//将查询模版ID置为全部
		    }
		    
		    if(ownerApplyForm!=null){
		        ownerApplyForm.getPagination().gotoPage(1);//将我的申请的From的翻页数据置为1
		        ownerApplyForm.setQuery_method("1");//将默认的查询状态置为运行中
		    }
		    
		    if(monitorTaskForm!=null){
		        monitorTaskForm.getPagination().gotoPage(1);//将任务监控的From的翻页数据置为1
		        monitorTaskForm.setQuery_method("1");//将默认的查询状态置为运行中
		        monitorTaskForm.setDays("30");//将默认查询方式的天数置为30天
		        monitorTaskForm.setStart_date("");//将开始日期置空
		        monitorTaskForm.setEnd_date("");//将结束日期置空
		        monitorTaskForm.setTemplateId("-1");//将查询模版ID置为全部
		        monitorTaskForm.setTitlename("");//将流程名称置为空
		    }
		}
		return super.validate(arg0, arg1);
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("page_num",this.getPage_num());
		this.getFormHM().put("signLogo",this.getSignLogo());
		this.getFormHM().put("content",this.getContent());
		this.getFormHM().put("type",this.getType());
		this.getFormHM().put("pageno",this.getPageno());
		this.getFormHM().put("tabid",this.getTabid());
		this.getFormHM().put("ins_id",this.getIns_id());
		this.getFormHM().put("taskid",this.getTaskid());
		this.getFormHM().put("sp_flag",this.getSp_flag());
		this.getFormHM().put("res_flag",this.getRes_flag());
	    this.getFormHM().put("sp_batch",this.getSp_batch());
	    this.getFormHM().put("tasklist",this.getTasklist());
		this.getFormHM().put("picturefile",this.getPicturefile());
		this.getFormHM().put("a0100", getA0100());
		this.getFormHM().put("basepre", getBasepre());
		this.getFormHM().put("b0110",this.getB0110());
		this.getFormHM().put("e01a1",this.getE01a1());
		this.getFormHM().put("tablename", getTablename());
		this.getFormHM().put("operationcode", operationcode);
		this.getFormHM().put("operationname", operationname);
		this.getFormHM().put("staticid", staticid);
		this.getFormHM().put("filecontent", this.getFilecontent());
		this.getFormHM().put("file_id", this.getFile_id());
		this.getFormHM().put("isFileViewer", this.getIsFileViewer());
		this.getFormHM().put("isDisSubMeetingButton", this.getIsDisSubMeetingButton());
		this.getFormHM().put("targitemlist", this.getTargitemlist());
		this.getFormHM().put("mediasortid", this.getMediasortid());
		this.getFormHM().put("no_sp_yj", this.getNo_sp_yj());
		this.getFormHM().put("objectId", this.getObjectId());
		this.getFormHM().put("actor_type", this.getActor_type());
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList getPagelist() {
		return pagelist;
	}

	public void setPagelist(ArrayList pagelist) {
		this.pagelist = pagelist;
	}

	public String getSetname() {
		return setname;
	}

	public void setSetname(String setname) {
		this.setname = setname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList getOutformlist() {
		return outformlist;
	}

	public void setOutformlist(ArrayList outformlist) {
		this.outformlist = outformlist;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getObjecttype() {
		return objecttype;
	}

	public void setObjecttype(String objecttype) {
		this.objecttype = objecttype;

	}

	public String getOperationtype() {
		return operationtype;
	}

	public void setOperationtype(String operationtype) {
		this.operationtype = operationtype;
	//	if(operationtype!=null&&operationtype.equalsIgnoreCase("0"))
	//		setRefresh("false");//true->false 2008-02-20 保存数据实时刷新出现问题，对插入子集
	//	else
	//		setRefresh("false");		
	}

	public String getStrpres() {
		return strpres;
	}

	public void setStrpres(String strpres) {
		this.strpres = strpres;
	}

	public String getIns_id() {
		return ins_id;
	}

	public void setIns_id(String ins_id) {
		this.ins_id = ins_id;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getSp_yj() {
		return sp_yj;
	}

	public void setSp_yj(String sp_yj) {
		this.sp_yj = sp_yj;
	}

	public ArrayList getSp_yjlist() {
		return sp_yjlist;
	}

	public void setSp_yjlist(ArrayList sp_yjlist) {
		this.sp_yjlist = sp_yjlist;
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		this.setEmergency("#");
		this.setObjecttype("#");
		this.setSp_mainbody("#");
		this.setSetname("#");
		//this.setTaskid("0");
		this.setSp_batch("0");
		this.setStartflag("0");
		this.setPhotofile("");
		super.reset(arg0, arg1);
	}

	public PaginationForm getSp_yjListForm() {
		return sp_yjListForm;
	}

	public void setSp_yjListForm(PaginationForm sp_yjListForm) {
		this.sp_yjListForm = sp_yjListForm;
	}

	public String getSp_flag() {
		return sp_flag;
	}

	public void setSp_flag(String sp_flag) {
		this.sp_flag = sp_flag;
	}

	public String getSp_ctrl() {
		return sp_ctrl;
	}

	public void setSp_ctrl(String sp_ctrl) {
		this.sp_ctrl = sp_ctrl;
	}

	public String getRefresh() {
		return refresh;
	}

	public void setRefresh(String refresh) {
		this.refresh = refresh;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


	public String getRes_flag() {
		return res_flag;
	}

	public void setRes_flag(String res_flag) {
		this.res_flag = res_flag;
	}

	public ArrayList getNoprintlist() {
		return noprintlist;
	}

	public void setNoprintlist(ArrayList noprintlist) {
		this.noprintlist = noprintlist;
	}

	public String getSp_mode() {
		return sp_mode;
	}

	public void setSp_mode(String sp_mode) {
		this.sp_mode = sp_mode;
	}

	public String getSp_objname() {
		return sp_objname;
	}

	public void setSp_objname(String sp_objname) {
		this.sp_objname = sp_objname;
	}

	public String getSp_batch() {
		return sp_batch;
	}

	public void setSp_batch(String sp_batch) {
		this.sp_batch = sp_batch;
	}

	public ArrayList getTasklist() {
		return tasklist;
	}

	public void setTasklist(ArrayList tasklist) {
		this.tasklist = tasklist;
	}

	public String getBatch_task() {
		return batch_task;
	}

	public void setBatch_task(String batch_task) {
		this.batch_task = batch_task;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getBasepre() {
		return basepre;
	}

	public void setBasepre(String basepre) {
		this.basepre = basepre;
	}

	@Override
    public String getReturnflag() {
		return returnflag;
	}

	@Override
    public void setReturnflag(String returnflag) {
		this.returnflag = returnflag;
	}



	public String getLlexpr() {
		return llexpr;
	}

	public void setLlexpr(String llexpr) {
		this.llexpr = llexpr;
	}

	public String getJudgeisllexpr() {
		return judgeisllexpr;
	}

	public void setJudgeisllexpr(String judgeisllexpr) {
		this.judgeisllexpr = judgeisllexpr;
	}

	public String getSys_filter_factor() {
		return this.sys_filter_factor;
	}

	public void setSys_filter_factor(String sys_filter_factor) {
		this.sys_filter_factor = sys_filter_factor;
	}

	public String getStartflag() {
		return startflag;
	}

	public void setStartflag(String startflag) {
		this.startflag = startflag;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getPhotofile() {
		return photofile;
	}

	public void setPhotofile(String photofile) {
		this.photofile = photofile;
	}

	public String getWarn_id() {
		return warn_id;
	}

	public void setWarn_id(String warn_id) {
		this.warn_id = warn_id;
	}

	public String getCheckhmuster() {
		return checkhmuster;
	}

	public void setCheckhmuster(String checkhmuster) {
		this.checkhmuster = checkhmuster;
	}

	public String getOperationname() {
		return operationname;
	}

	public void setOperationname(String operationname) {
		this.operationname = operationname;
	}

	public String getStaticid() {
		return staticid;
	}

	public void setStaticid(String staticid) {
		this.staticid = staticid;
	}

	public String getPhoto_maxsize() {
		return photo_maxsize;
	}

	public void setPhoto_maxsize(String photo_maxsize) {
		this.photo_maxsize = photo_maxsize;
	}

	public String getTaskState() {
		return taskState;
	}

	public void setTaskState(String taskState) {
		this.taskState = taskState;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getIndex_template() {
		return index_template;
	}

	public void setIndex_template(String index_template) {
		this.index_template = index_template;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getEmail_staff() {
		return email_staff;
	}

	public void setEmail_staff(String email_staff) {
		this.email_staff = email_staff;
	}

	public String getEmail_staff_value() {
		return email_staff_value;
	}

	public void setEmail_staff_value(String email_staff_value) {
		this.email_staff_value = email_staff_value;
	}

	public String getMailTempletID() {
		return mailTempletID;
	}

	public void setMailTempletID(String mailTempletID) {
		this.mailTempletID = mailTempletID;
	}

	public ArrayList getMailTempletList() {
		return mailTempletList;
	}

	public void setMailTempletList(ArrayList mailTempletList) {
		this.mailTempletList = mailTempletList;
	}

	public String getA0101s() {
		return a0101s;
	}

	public void setA0101s(String a0101s) {
		this.a0101s = a0101s;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTemplate_bos() {
		return template_bos;
	}

	public void setTemplate_bos(String template_bos) {
		this.template_bos = template_bos;
	}

	public String getTemplate_staff() {
		return template_staff;
	}

	public void setTemplate_staff(String template_staff) {
		this.template_staff = template_staff;
	}

	public String getIsSendMessage() {
		return isSendMessage;
	}

	public void setIsSendMessage(String isSendMessage) {
		this.isSendMessage = isSendMessage;
	}

	public String getIsFinishTask() {
		return isFinishTask;
	}

	public void setIsFinishTask(String isFinishTask) {
		this.isFinishTask = isFinishTask;
	}


	public ArrayList getRejectObjList() {
		return rejectObjList;
	}

	public void setRejectObjList(ArrayList rejectObjList) {
		this.rejectObjList = rejectObjList;
	}

	public String getIsApplySpecialRole() {
		return isApplySpecialRole;
	}

	public void setIsApplySpecialRole(String isApplySpecialRole) {
		this.isApplySpecialRole = isApplySpecialRole;
	}

	public String getBusinessModel() {
		return businessModel;
	}

	public void setBusinessModel(String businessModel) {
		this.businessModel = businessModel;
	}

	public String getPre_pendingID() {
		return pre_pendingID;
	}

	public void setPre_pendingID(String pre_pendingID) {
		this.pre_pendingID = pre_pendingID;
	}

	public String getIns_ids() {
		return ins_ids;
	}

	public void setIns_ids(String ins_ids) {
		this.ins_ids = ins_ids;
	}

	

	public String getEnduser() {
		return enduser;
	}

	public void setEnduser(String enduser) {
		this.enduser = enduser;
	}

	public String getEndusertype() {
		return endusertype;
	}

	public void setEndusertype(String endusertype) {
		this.endusertype = endusertype;
	}

	public String getEnduser_fullname() {
		return enduser_fullname;
	}

	public void setEnduser_fullname(String enduser_fullname) {
		this.enduser_fullname = enduser_fullname;
	}

	public String getHmuster_sql() {
		return hmuster_sql;
	}

	public void setHmuster_sql(String hmuster_sql) {
		this.hmuster_sql = hmuster_sql;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getBostype() {
		return bostype;
	}

	public void setBostype(String bostype) {
		this.bostype = bostype;
	}

	public String getB0110() {
		return b0110;
	}

	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}

	public String getE01a1() {
		return e01a1;
	}

	public void setE01a1(String e01a1) {
		this.e01a1 = e01a1;
	}

	public FormFile getFilecontent() {
		return filecontent;
	}

	public void setFilecontent(FormFile filecontent) {
		this.filecontent = filecontent;
	}

	public String getInfor_type() {
		return infor_type;
	}

	public void setInfor_type(String infor_type) {
		this.infor_type = infor_type;
	}

	public String getPriv_html() {
		return priv_html;
	}

	public void setPriv_html(String priv_html) {
		this.priv_html = priv_html;
	}

	public String getStarttask() {
		return starttask;
	}

	public void setStarttask(String starttask) {
		this.starttask = starttask;
	}

	public String get_static() {
		return _static;
	}

	public void set_static(String _static) {
		this._static = _static;
	}

	public String getIsFinishedRecord() {
		return isFinishedRecord;
	}

	public void setIsFinishedRecord(String isFinishedRecord) {
		this.isFinishedRecord = isFinishedRecord;
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getSignxml() {
		return signxml;
	}

	public void setSignxml(String signxml) {
		this.signxml = signxml;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFilter_by_factor() {
		return filter_by_factor;
	}

	public void setFilter_by_factor(String filter_by_factor) {
		this.filter_by_factor = filter_by_factor;
	}


	public String getNextNodeStr() {
		return nextNodeStr;
	}

	public void setNextNodeStr(String nextNodeStr) {
		this.nextNodeStr = nextNodeStr;
	}
 

	public HashMap getSpecialRoleMap() {
		return specialRoleMap;
	}

	public void setSpecialRoleMap(HashMap specialRoleMap) {
		this.specialRoleMap = specialRoleMap;
	}


	public String getLimit_manage_priv() {
		return limit_manage_priv;
	}

	public void setLimit_manage_priv(String limit_manage_priv) {
		this.limit_manage_priv = limit_manage_priv;
	}

	public String getBusinessModel_yp() {
		return businessModel_yp;
	}

	public void setBusinessModel_yp(String businessModel_yp) {
		this.businessModel_yp = businessModel_yp;
	}

	public String getGeneralmessage() {
		return generalmessage;
	}

	public String getNodeprive() {
		return nodeprive;
	}

	public void setNodeprive(String nodeprive) {
		this.nodeprive = nodeprive;
	}

	public void setGeneralmessage(String generalmessage) {
		this.generalmessage = generalmessage;
	}

	public ArrayList getAffixList() {
		return affixList;
	}

	public void setAffixList(ArrayList affixList) {
		this.affixList = affixList;
	}

	public String getFile_id() {
		return file_id;
	}

	public void setFile_id(String file_id) {
		this.file_id = file_id;
	}
	
	public String getSignLogo() {
		return signLogo;
	}

	public void setSignLogo(String signLogo) {
		this.signLogo = signLogo;
	}

	public String getSp_mainbody() {
		return sp_mainbody;
	}

	public void setSp_mainbody(String sp_mainbody) {
		this.sp_mainbody = sp_mainbody;
	}

	public String getIsFileViewer() {
		return isFileViewer;
	}

	public void setIsFileViewer(String isFileViewer) {
		this.isFileViewer = isFileViewer;
	}
	 
	public String getNo_priv_ctrl() {
		return no_priv_ctrl;
	}

	public void setNo_priv_ctrl(String no_priv_ctrl) {
		this.no_priv_ctrl = no_priv_ctrl;
	}

	public ArrayList getTargitemlist() {
		return targitemlist;
	}

	public void setTargitemlist(ArrayList targitemlist) {
		this.targitemlist = targitemlist;
	}

	public String getUploadattach() {
		return uploadattach;
	}

	public void setUploadattach(String uploadattach) {
		this.uploadattach = uploadattach;
	}

	public String getSelfplatform() {
		return selfplatform;
	}

	public void setSelfplatform(String selfplatform) {
		this.selfplatform = selfplatform;
	}

	public String getAttachmentcount() {
		return attachmentcount;
	}

	public void setAttachmentcount(String attachmentcount) {
		this.attachmentcount = attachmentcount;
	}

	public String getAttachmentareatotype() {
		return attachmentareatotype;
	}

	public void setAttachmentareatotype(String attachmentareatotype) {
		this.attachmentareatotype = attachmentareatotype;
	}

	public ArrayList getMediasortList() {
		return mediasortList;
	}

	public void setMediasortList(ArrayList mediasortList) {
		this.mediasortList = mediasortList;
	}

	public String getMediasortid() {
		return mediasortid;
	}

	public void setMediasortid(String mediasortid) {
		this.mediasortid = mediasortid;
	}

	public String getPage_num() {
		return page_num;
	}

	public void setPage_num(String page_num) {
		this.page_num = page_num;
	}

	

	public String getPageCount() {
		return pageCount;
	}

	public void setPageCount(String pageCount) {
		this.pageCount = pageCount;
	}

	public String getDef_flow_self() {
		return def_flow_self;
	}

	public void setDef_flow_self(String def_flow_self) {
		this.def_flow_self = def_flow_self;
	}

	public String getIsEndTask_flow() {
		return isEndTask_flow;
	}

	public void setIsEndTask_flow(String isEndTask_flow) {
		this.isEndTask_flow = isEndTask_flow;
	}

	public String getAllow_def_flow_self() {
		return allow_def_flow_self;
	}

	public void setAllow_def_flow_self(String allow_def_flow_self) {
		this.allow_def_flow_self = allow_def_flow_self;
	}
	public String getIsDisSubMeetingButton() {
		return isDisSubMeetingButton;
	}

	public void setIsDisSubMeetingButton(String isDisSubMeetingButton) {
		this.isDisSubMeetingButton = isDisSubMeetingButton;
	}
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	public String getActor_type() {
		return actor_type;
	}

	public void setActor_type(String actor_type) {
		this.actor_type = actor_type;
	}
 
}

