package com.hjsj.hrms.actionform.general.template;

import com.hjsj.hrms.valueobject.ykcard.CardTagParamView;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;

public class TemplateListForm extends FrameForm {
	 private PaginationForm templatelistform=new PaginationForm();
	 private ArrayList templateSetList=new ArrayList();   //
	 private ArrayList tableHeadSetList=new ArrayList();  
	 private String    tabid="";
	 private String    isName="1";                        //是否有姓名列
	 private String    operationtype="10";                //模板业务类型
	 private String    codeid="";
	 
	 private String    warn_id="";                        //预警id
	 
	 private String    isEmployee="0";                    //是否是自助平台
	 /**检索条件表达式*/
	 private String sys_filter_factor;
	 /**数据库前缀串,for examples Usr,Trs*/
	 private String strpres;
	 /**当前任务处理各个环节处理的意见的列表*/
	 private PaginationForm sp_yjListForm=new PaginationForm();
	 private String    returnflag="";
	 private String    applyobj="";                    
	 private String    startflag="0";					  //实例，是否为当前用户发起的申请
	 private String    isFinishTask="0";					
	 private String    hasRecordFromMessage="0";          //是否有来自消息表的记录。
	 private String    task_state="-1";                   //任务状态 =1,初始化 =2运行中 =3等待 =4终止 =5结束 =6暂停
	 private String    sp_ctrl="";
	 private String    sp_mode="";                        ///**审批模式=0自动流转，=1手工指派*/
	 
	 private String isEndTask_flow="false"; //是否是自定义流程最后待处理的任务
	 
	 private String    ins_id_str="";
	 private String    ins_id="0";
	 private String    task_id="0";
	 private String    batch_task="0";
	 private String    operationname="";                  //业务分类 各业务模板id
	 private String    staticid="";
	 
	 private String    _static="1";						  //1:人事异动；2：是薪资管理	；8：保险变动；21：劳动合同；12：出国管理 ;10:单位管理机构调整;11:岗位管理机构调整
	 private String    isFinishedRecord="0";              //是否是结束了的单子
	 
	 private String def_flow_self="0";//自定义审批流程 0、1  wangrd 2013-12-13
	 private String allow_def_flow_self="false";
	
	 private String isDisSubMeetingButton;//是否显示上会按钮 2015-10-20
		//高级花名册的人员信息sql
	 String hmuster_sql="";
	 private String checkhmuster; //判断有没有高级花名册
	 /**打印输出单据列表
		 * 包括高级花名册，登记表,以及打印输出WORD,EXCEL模板
		 */
	 private ArrayList outformlist=new ArrayList();
	 transient private CardTagParamView cardparam=new CardTagParamView();
	 private String  isApplySpecialRole="0";  //是否自动报送给 角色属性为汇报关系 “直接领导”、“主管领导”，“第三级领导”、“第四级领导”、“全部领导”，属性值各自为“9，10，11，12，13”。
	///非审批模板发送消息
	 private String  isSendMessage="0";
	 
	 private String    isCompare="0";                 //是否比较
	 private String    hiddenItem="";                     //隐藏指标串  以,分割
	 private String    fieldSetSortStr="";                //指标排列顺序 以,分割
	 private String    lockedItemStr="";                  //锁定指标串   以,分割
	 private String    orderStr="";                       //人员排序sql串   order by XXXXX
	 private String    filterStr="";                      //查询过滤条件串  and XXXXXX
	 private String    tasklist_str="";
	
	 private String    table_name="";
	 private String    isAppealTable="0";                 //是否是审批表 1:是
	 private String    isSelectAll="0";                   //是否全选
	 private String   sp_batch="0";
	 
	 //xgq
	 private String    hire_table="";
	 private String    lock_table="";
	 private ArrayList sortlist = new ArrayList(); //指标list
	 private String[] sort_fields; 
	 private ArrayList itemlist = new ArrayList(); //人员排序指标list
	 private String    itemid="";
	 private String sortitem;
	 private ArrayList selectedFieldList  = new ArrayList(); //人员排序指标list
	 private String    rightFields;
	 private String[] right_fields = new String[0];
	 private String[] left_fields=new String[0];
	 private ArrayList allList = new ArrayList();
	 /**人员筛选条件列表*/
	 private ArrayList personFilterList=new ArrayList();
	 private String filterCondId;	
	 private String expr;
	 private String issave;
	   /**目标项目列表*/
	private ArrayList targitemlist=new ArrayList();
	    /**参考项目列表*/
	private ArrayList ref_itemlist=new ArrayList();
	   /**替换公式内容*/
    private String formula; 
    /**参考项目*/
    private String ref_itemid;
    private String targ_itemid;
    private String conditions; //计算条件
    private String[] codesetid_arr;
    private ArrayList operlist=new ArrayList();
    private ArrayList loglist=new ArrayList();
    private LazyDynaBean lockBean= new LazyDynaBean();
    private HashMap prechangemap=new HashMap();
    private ArrayList queryfieldlist=new ArrayList();
    private String isShowCondition="";
	private String querylike="";//模糊查询
	private HttpSession session2;
	private String needcondition ;//初始化条件
	/** 导入文件 */
    private FormFile file;
    private String errorFileName="";
    private String onlyname="";
    
    String user_="";
	String user_h="";
    
    private String num="0";
    private String description =""; //人员筛选报错描述
    private String b0110="";
    private String e01a1="";
    private String infor_type="1";   //1:人员  2：组织  3：职位
    
    private String codesetid;
    private String selectcodeitemids;
    private ArrayList codeitemlist = new ArrayList();
    private String changemsg;//是否显示变动信息
    private String tarcodeitemdesc;
    private String end_date;
    private ArrayList codesetidlist;
    private ArrayList combinefieldlist=new ArrayList();
    private String msg="";
    private String combinecodeitemid="";
    private String datefillable="";
    private String codedescfillable="";
    private String transfercodeitemid="";
    private String tarorgname="";
    
    private String isInitData="1";
	private String a0101s="";
	private String tableName="";
	private String sequence="0";
	private  String no_priv_ctrl="0"; //手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按
	private String filter_by_factor="0";  //手工选人、条件选人按检索条件过滤, 0不过滤(默认值),1过滤
	private String generalmessage="";	//手工选人的姓名查询的提示信息
    private String index_template="";
    private String nodeprive="-1";
    
    private String isSyncStruct="0";  //是否已同步过表结构
    private String no_sp_yj="0";// no_sp_yj:审批不填写意见  1:选中   0:空表示没选中（默认）
    private String limit_manage_priv="";
    private String updateCount="0";//导入成功的数据条数
	/** 
     * @return limit_manage_priv 
     */
    public String getLimit_manage_priv() {
        return limit_manage_priv;
    }
    /** 
     * @param limitManagePriv 要设置的 limit_manage_priv 
     */
    public void setLimit_manage_priv(String limitManagePriv) {
        limit_manage_priv = limitManagePriv;
    }
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
    public String getNodeprive() {
		return nodeprive;
	}
	public void setNodeprive(String nodeprive) {
		this.nodeprive = nodeprive;
	}
	public ArrayList getCodesetidlist() {
		return codesetidlist;
	}
	public ArrayList getCombinefieldlist() {
		return combinefieldlist;
	}
	public void setCombinefieldlist(ArrayList combinefieldlist) {
		this.combinefieldlist = combinefieldlist;
	}
	public void setCodesetidlist(ArrayList codesetidlist) {
		this.codesetidlist = codesetidlist;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public TemplateListForm()
	{
		 CommonData vo=new CommonData("=","=");
	        operlist.add(vo);
	        vo=new CommonData(">",">");
	        operlist.add(vo);  
	        vo=new CommonData(">=",">=");
	        operlist.add(vo); 
	        vo=new CommonData("<","<");
	        operlist.add(vo);
	        vo=new CommonData("<=","<=");
	        operlist.add(vo);   
	        vo=new CommonData("<>","<>");
	        operlist.add(vo);
	        
	         vo=new CommonData("*","且");
	         loglist.add(vo);
		        vo=new CommonData("+","或");
		        loglist.add(vo);  
	       
	}
	public String[] getCodesetid_arr() {
		return codesetid_arr;
	}


	public void setCodesetid_arr(String[] codesetid_arr) {
		this.codesetid_arr = codesetid_arr;
	}


	public String getConditions() {
		return conditions;
	}


	public void setConditions(String conditions) {
		this.conditions = conditions;
	}


	public String getRef_itemid() {
		return ref_itemid;
	}


	public void setRef_itemid(String ref_itemid) {
		this.ref_itemid = ref_itemid;
	}


	@Override
    public void inPutTransHM() {
		this.getFormHM().put("staticid",this.getStaticid());
		this.getFormHM().put("operationname",this.getOperationname());
	    this.getFormHM().put("isCompare",this.getIsCompare());
	    
	    this.getFormHM().put("hiddenItem",this.getHiddenItem());
	    this.getFormHM().put("fieldSetSortStr", this.getFieldSetSortStr());
	    this.getFormHM().put("lockedItemStr", this.getLockedItemStr());
	    this.getFormHM().put("orderStr",this.getOrderStr());
	    this.getFormHM().put("filterStr",this.getFilterStr());
	    
	    this.getFormHM().put("isSelectAll", this.getIsSelectAll());
	    
	    this.getFormHM().put("sort_fields",this.getSort_fields());
	    this.getFormHM().put("sortitem",this.getSortitem());

	    this.getFormHM().put("rightFields",this.getRightFields());
	   	this.getFormHM().put("rightFields",this.getRightFields());
	   	this.getFormHM().put("selectedFieldList",this.getSelectedFieldList());
	   	this.getFormHM().put("right_fields",this.getRight_fields());
	   	this.getFormHM().put("left_fields",this.getLeft_fields());
	   	this.getFormHM().put("personFilterList",this.getPersonFilterList());
	   	this.getFormHM().put("filterCondId",this.getFilterCondId());
	   	this.getFormHM().put("queryfieldlist", this.getQueryfieldlist());
	    this.getFormHM().put("querylike", this.getQuerylike());
	    this.getFormHM().put("expr", this.getExpr());
	    this.getFormHM().put("file",this.getFile());
		this.getFormHM().put("b0110",this.getB0110());
		this.getFormHM().put("e01a1",this.getE01a1());
		
		this.getFormHM().put("tarcodeitemdesc", tarcodeitemdesc);
		this.getFormHM().put("end_date", this.getEnd_date());
	  	this.getFormHM().put("combinefieldlist", this.getCombinefieldlist());
	  	this.getFormHM().put("combinecodeitemid", this.getCombinecodeitemid());
	  	this.getFormHM().put("transfercodeitemid", this.getTransfercodeitemid());
	  	this.getFormHM().put("no_sp_yj",this.getNo_sp_yj());

	  	this.getFormHM().put("errorFileName", this.getErrorFileName());
	  	this.getFormHM().put("updateCount", this.getUpdateCount());
	  	this.getFormHM().put("onlyname", this.getOnlyname());

	  	this.getFormHM().put("isDisSubMeetingButton", this.getIsDisSubMeetingButton());
	  	this.getFormHM().put("tasklist_str", this.getTasklist_str());
	  	
	  	this.getFormHM().put("batch_task", this.getBatch_task());

	}


	@Override
    public void outPutFormHM() {
		
		this.setIsSyncStruct((String)this.getFormHM().get("isSyncStruct"));
		
		this.setNo_priv_ctrl((String)this.getFormHM().get("no_priv_ctrl"));
		this.setUser_((String)this.getFormHM().get("user_"));
		this.setUser_h((String)this.getFormHM().get("user_h"));
		
		this.setFilter_by_factor((String)this.getFormHM().get("filter_by_factor"));
		this.setIsFinishedRecord((String)this.getFormHM().get("isFinishedRecord"));
		this.set_static((String)this.getFormHM().get("_static"));
		this.setIsInitData((String)this.getFormHM().get("isInitData"));
		this.setB0110((String)this.getFormHM().get("b0110"));
		this.setE01a1((String)this.getFormHM().get("e01a1"));
		this.setInfor_type((String)this.getFormHM().get("infor_type"));
		this.setNum((String)this.getFormHM().get("num"));
		this.setWarn_id((String)this.getFormHM().get("warn_id"));
		this.setHmuster_sql((String)this.getFormHM().get("hmuster_sql"));
		this.setCheckhmuster((String)this.getFormHM().get("checkhmuster"));
		this.setOutformlist((ArrayList)this.getFormHM().get("outformlist"));
		
		this.setIsEmployee((String)this.getFormHM().get("isEmployee"));
		this.setOperationname((String)this.getFormHM().get("operationname"));
		this.setSys_filter_factor((String)this.getFormHM().get("sys_filter_factor"));
		this.getSp_yjListForm().setList((ArrayList)this.getFormHM().get("splist"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setApplyobj((String)this.getFormHM().get("applyobj"));
		this.setStartflag((String)this.getFormHM().get("startflag"));
		this.setIsFinishTask((String)this.getFormHM().get("isFinishTask"));
		
		this.setHasRecordFromMessage((String)this.getFormHM().get("hasRecordFromMessage"));
		this.setTask_state((String)this.getFormHM().get("task_state"));
		this.setIsCompare((String)this.getFormHM().get("isCompare"));
		this.setIns_id((String)this.getFormHM().get("ins_id"));
		this.setTask_id((String)this.getFormHM().get("task_id"));
		this.setIsSendMessage((String)this.getFormHM().get("isSendMessage"));
		this.setStrpres((String)this.getFormHM().get("dbpres"));
		this.setSp_ctrl((String)this.getFormHM().get("sp_ctrl"));
		this.setSp_mode((String)this.getFormHM().get("sp_mode"));
		this.setDef_flow_self((String)this.getFormHM().get("def_flow_self"));
		this.setIsEndTask_flow((String)this.getFormHM().get("isEndTask_flow"));
		this.setIns_id_str((String)this.getFormHM().get("ins_id_str"));
		
		this.setIsSelectAll((String)this.getFormHM().get("isSelectAll"));
		this.setCodeid((String)this.getFormHM().get("codeid"));
		this.setTable_name((String)this.getFormHM().get("table_name"));
		this.setIsAppealTable((String)this.getFormHM().get("isAppealTable"));
		
		this.getTemplatelistform().setList((ArrayList)this.getFormHM().get("templatelist"));
//		this.getTemplatelistform().getPagination().setCurrent(1);
		this.setTemplateSetList((ArrayList)this.getFormHM().get("templateSetList"));
		this.setTableHeadSetList((ArrayList)this.getFormHM().get("tableHeadSetList"));
		this.setTabid((String)this.getFormHM().get("tabid"));
		this.setTasklist_str((String)this.getFormHM().get("tasklist_str"));
		this.setIsName((String)this.getFormHM().get("isName"));
		this.setOperationtype((String)this.getFormHM().get("operationtype"));
		
		
		this.setHiddenItem((String)this.getFormHM().get("hiddenItem"));
		this.setFieldSetSortStr((String)this.getFormHM().get("fieldSetSortStr"));
		this.setLockedItemStr((String)this.getFormHM().get("lockedItemStr"));
		this.setOrderStr((String)this.getFormHM().get("orderStr"));
		this.setFilterStr((String)this.getFormHM().get("filterStr"));
		
		
		this.setIsApplySpecialRole((String)this.getFormHM().get("isApplySpecialRole"));
		this.setLimit_manage_priv((String)this.getFormHM().get("limit_manage_priv"));
		
		this.setHire_table((String)this.getFormHM().get("hire_table"));
		this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));
		this.setSort_fields((String[])this.getFormHM().get("sort_fields"));
		this.setLock_table((String)this.getFormHM().get("lock_table"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		this.setSortitem((String)this.getFormHM().get("sortitem"));
		this.setRightFields((String)this.getFormHM().get("rightFields"));
		this.setSelectedFieldList((ArrayList)this.getFormHM().get("selectedFieldList"));
		this.setAllList((ArrayList)this.getFormHM().get("allList"));
		this.setPersonFilterList((ArrayList)this.getFormHM().get("personFilterList"));
		this.setFilterCondId((String)this.getFormHM().get("filterCondId"));
		this.setExpr((String)this.getFormHM().get("expr"));
		this.setIssave((String)this.getFormHM().get("issave"));
		this.setTargitemlist((ArrayList)this.getFormHM().get("targitemlist"));
		this.setRef_itemlist((ArrayList)this.getFormHM().get("ref_itemlist"));
		this.setConditions((String)this.getFormHM().get("conditions"));
		this.setCodesetid_arr((String[])this.getFormHM().get("codesetid_arr"));
		this.setLockBean((LazyDynaBean)this.getFormHM().get("lockBean"));
		this.setPrechangemap((HashMap)this.getFormHM().get("prechangemap"));
		this.setQueryfieldlist((ArrayList)this.getFormHM().get("queryfieldlist"));
	    this.setIsShowCondition((String)this.getFormHM().get("isShowCondition"));
	    this.setQuerylike((String)this.getFormHM().get("querylike"));
	    this.setNeedcondition((String)this.getFormHM().get("needcondition"));
	    
	    this.getFormHM().put("num",String.valueOf((Integer.parseInt(this.num==null?"0":this.num))+1));
	    this.setDescription((String)this.getFormHM().get("description"));
	    
	    this.setChangemsg((String)this.getFormHM().get("changemsg"));
	    this.setCodesetid((String)this.getFormHM().get("codesetid"));
	    this.selectcodeitemids=(String)this.getFormHM().get("selectcodeitemids");
	    this.setCodeitemlist((ArrayList)this.getFormHM().get("codeitemlist"));
	    this.setCodesetidlist((ArrayList)this.getFormHM().get("codesetidlist"));
	    this.setCombinefieldlist((ArrayList)this.getFormHM().get("combinefieldlist"));
	    this.setMsg((String)this.getFormHM().get("msg"));
	    this.setDatefillable((String)this.getFormHM().get("datefillable"));
	    this.setCodedescfillable((String)this.getFormHM().get("codedescfillable"));
	    this.setSp_batch((String)this.getFormHM().get("sp_batch"));
	    this.setTarorgname((String)this.getFormHM().get("tarorgname"));
	    this.setA0101s((String)this.getFormHM().get("a0101s"));
		this.setTableName((String)this.getFormHM().get("tableName"));
		this.setSequence((String)this.getFormHM().get("sequence"));
		this.setGeneralmessage((String)this.getFormHM().get("generalmessage"));
		this.setNodeprive((String)this.getFormHM().get("nodeprive"));
		this.setAllow_def_flow_self((String)this.getFormHM().get("allow_def_flow_self"));
		this.setNo_sp_yj((String) this.getFormHM().get("no_sp_yj"));

		this.setErrorFileName((String)this.getFormHM().get("errorFileName"));
		this.setUpdateCount((String)this.getFormHM().get("updateCount"));
		this.setOnlyname((String)this.getFormHM().get("onlyname"));

		this.setIsDisSubMeetingButton((String)this.getFormHM().get("isDisSubMeetingButton"));
		this.setBatch_task((String)this.getFormHM().get("batch_task"));
	}
	
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		this.getFormHM().put("session",arg1.getSession());
		
		
		if("/general/template/templatelist".equals(arg0.getPath())&&arg1.getParameter("b_init")!=null){
            /**定位到首页,*/
            if(this.getTemplatelistform()!=null)
            	this.getTemplatelistform().getPagination().firstPage();              
        }
		if("/general/template/templatelist".equals(arg0.getPath())&&arg1.getParameter("pagecurent")!=null&& "1".equals(arg1.getParameter("pagecurent"))){
            /**定位到首页,*/
            if(this.getTemplatelistform()!=null)
            	this.getTemplatelistform().getPagination().firstPage();              
        }
		if("/general/template/personFilterResult".equals(arg0.getPath())&&arg1.getParameter("pagecurent")!=null&& "1".equals(arg1.getParameter("pagecurent"))){
            /**定位到首页,*/
            if(this.getTemplatelistform()!=null)
            	this.getTemplatelistform().getPagination().firstPage();              
        }
		
		
		if("/general/template/templatelist".equals(arg0.getPath())&&arg1.getParameter("selectAll")==null&&(arg1.getParameter("b_init")!=null||arg1.getParameter("b_query")!=null))
		{
			this.isSelectAll="0";
		}
		if("/general/template/personFilterResult".equals(arg0.getPath())&&arg1.getParameter("selectAll")==null&&arg1.getParameter("b_search")!=null)
		{
			this.isSelectAll="0";
		}
		if("/general/template/templatelist".equals(arg0.getPath())&&arg1.getParameter("cancelfilter")!=null&& "1".equals(arg1.getParameter("cancelfilter"))){
            /**定位到首页,*/
            this.filterStr="";            
        }
		this.setSession2(arg1.getSession());
		return super.validate(arg0, arg1);
	}
	
	
	

	public PaginationForm getTemplatelistform() {
		return templatelistform;
	}

	public void setTemplatelistform(PaginationForm templatelistform) {
		this.templatelistform = templatelistform;
	}

	public ArrayList getTemplateSetList() {
		return templateSetList;
	}

	public void setTemplateSetList(ArrayList templateSetList) {
		this.templateSetList = templateSetList;
	}

	public String getIsCompare() {
		return isCompare;
	}

	public void setIsCompare(String isCompare) {
		this.isCompare = isCompare;
//		this.outPutFormHM();
	}

	public String getHiddenItem() {
		return hiddenItem;
	}

	public void setHiddenItem(String hiddenItem) {
		this.hiddenItem = hiddenItem;
	}

	public String getFieldSetSortStr() {
		return fieldSetSortStr;
	}

	public void setFieldSetSortStr(String fieldSetSortStr) {
		this.fieldSetSortStr = fieldSetSortStr;
	}

	public String getLockedItemStr() {
		return lockedItemStr;
	}

	public void setLockedItemStr(String lockedItemStr) {
		this.lockedItemStr = lockedItemStr;
	}

	public String getOrderStr() {
		return orderStr;
	}

	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}

	public String getFilterStr() {
		return filterStr;
	}

	public void setFilterStr(String filterStr) {
		this.filterStr = filterStr;
	}

	public ArrayList getTableHeadSetList() {
		return tableHeadSetList;
	}

	public void setTableHeadSetList(ArrayList tableHeadSetList) {
		this.tableHeadSetList = tableHeadSetList;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}


	public String getTasklist_str() {
		return tasklist_str;
	}


	public void setTasklist_str(String tasklist_str) {
		this.tasklist_str = tasklist_str;
	}


	public String getIsName() {
		return isName;
	}


	public void setIsName(String isName) {
		this.isName = isName;
	}


	public String getOperationtype() {
		return operationtype;
	}


	public void setOperationtype(String operationtype) {
		this.operationtype = operationtype;
	}


	public String getTable_name() {
		return table_name;
	}


	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}


	public String getIsAppealTable() {
		return isAppealTable;
	}


	public void setIsAppealTable(String isAppealTable) {
		this.isAppealTable = isAppealTable;
	}


	public String getIsSelectAll() {
		return isSelectAll;
	}


	public void setIsSelectAll(String isSelectAll) {
		this.isSelectAll = isSelectAll;
	}


	public String getCodeid() {
		return codeid;
	}


	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}


	public String getSp_ctrl() {
		return sp_ctrl;
	}


	public void setSp_ctrl(String sp_ctrl) {
		this.sp_ctrl = sp_ctrl;
	}


	public String getSp_mode() {
		return sp_mode;
	}


	public void setSp_mode(String sp_mode) {
		this.sp_mode = sp_mode;
	}


	public String getIns_id_str() {
		return ins_id_str;
	}


	public void setIns_id_str(String ins_id_str) {
		this.ins_id_str = ins_id_str;
	}


	public String getIns_id() {
		return ins_id;
	}


	public void setIns_id(String ins_id) {
		this.ins_id = ins_id;
	}


	public String getTask_id() {
		return task_id;
	}


	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}


	public String getIsApplySpecialRole() {
		return isApplySpecialRole;
	}


	public void setIsApplySpecialRole(String isApplySpecialRole) {
		this.isApplySpecialRole = isApplySpecialRole;
	}


	public String getIsSendMessage() {
		return isSendMessage;
	}


	public void setIsSendMessage(String isSendMessage) {
		this.isSendMessage = isSendMessage;
	}


	public String getHire_table() {
		return hire_table;
	}


	public void setHire_table(String hire_table) {
		this.hire_table = hire_table;
	}


	public String getLock_table() {
		return lock_table;
	}


	public void setLock_table(String lock_table) {
		this.lock_table = lock_table;
	}


	public ArrayList getSortlist() {
		return sortlist;
	}


	public void setSortlist(ArrayList sortlist) {
		this.sortlist = sortlist;
	}


	public String[] getSort_fields() {
		return sort_fields;
	}


	public void setSort_fields(String[] sort_fields) {
		this.sort_fields = sort_fields;
	}


	public ArrayList getItemlist() {
		return itemlist;
	}


	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}


	public String getItemid() {
		return itemid;
	}


	public void setItemid(String itemid) {
		this.itemid = itemid;
	}


	public String getSortitem() {
		return sortitem;
	}


	public void setSortitem(String sortitem) {
		this.sortitem = sortitem;
	}


	public String getTask_state() {
		return task_state;
	}


	public void setTask_state(String task_state) {
		this.task_state = task_state;
	}


	public String getHasRecordFromMessage() {
		return hasRecordFromMessage;
	}


	public void setHasRecordFromMessage(String hasRecordFromMessage) {
		this.hasRecordFromMessage = hasRecordFromMessage;
	}



	public String getIsFinishTask() {
		return isFinishTask;
	}


	public void setIsFinishTask(String isFinishTask) {
		this.isFinishTask = isFinishTask;
	}


	public String getStartflag() {
		return startflag;
	}


	public void setStartflag(String startflag) {
		this.startflag = startflag;
	}


	public String getApplyobj() {
		return applyobj;
	}


	public void setApplyobj(String applyobj) {
		this.applyobj = applyobj;
	}


	@Override
    public String getReturnflag() {
		return returnflag;
	}


	@Override
    public void setReturnflag(String returnflag) {
		this.returnflag = returnflag;
	}


	public PaginationForm getSp_yjListForm() {
		return sp_yjListForm;
	}


	public void setSp_yjListForm(PaginationForm sp_yjListForm) {
		this.sp_yjListForm = sp_yjListForm;
	}


	public String getStrpres() {
		return strpres;
	}


	public void setStrpres(String strpres) {
		this.strpres = strpres;
	}


	public String getSys_filter_factor() {
		return sys_filter_factor;
	}


	public void setSys_filter_factor(String sys_filter_factor) {
		this.sys_filter_factor = sys_filter_factor;
	}
 
	 
	public ArrayList getSelectedFieldList() {
		return selectedFieldList;
	}


	public void setSelectedFieldList(ArrayList selectedFieldList) {
		this.selectedFieldList = selectedFieldList;
	}


	public String getRightFields() {
		return rightFields;
	}


	public void setRightFields(String rightFields) {
		this.rightFields = rightFields;
	}


	public String getCombinecodeitemid() {
		return combinecodeitemid;
	}
	public void setCombinecodeitemid(String combinecodeitemid) {
		this.combinecodeitemid = combinecodeitemid;
	}
	public String[] getRight_fields() {
		return right_fields;
	}


	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}


	public String[] getLeft_fields() {
		return left_fields;
	}


	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}


	public ArrayList getAllList() {
		return allList;
	}


	public void setAllList(ArrayList allList) {
		this.allList = allList;
	}


	public ArrayList getPersonFilterList() {
		return personFilterList;
	}


	public void setPersonFilterList(ArrayList personFilterList) {
		this.personFilterList = personFilterList;
	}


	public String getFilterCondId() {
		return filterCondId;
	}


	public void setFilterCondId(String filterCondId) {
		this.filterCondId = filterCondId;
	}


	public String getExpr() {
		return expr;
	}


	public void setExpr(String expr) {
		this.expr = expr;
	}


	public String getIssave() {
		return issave;
	}


	public void setIssave(String issave) {
		this.issave = issave;
	}


	public ArrayList getTargitemlist() {
		return targitemlist;
	}


	public void setTargitemlist(ArrayList targitemlist) {
		this.targitemlist = targitemlist;
	}


	public ArrayList getRef_itemlist() {
		return ref_itemlist;
	}


	public void setRef_itemlist(ArrayList ref_itemlist) {
		this.ref_itemlist = ref_itemlist;
	}


	public String getFormula() {
		return formula;
	}


	public void setFormula(String formula) {
		this.formula = formula;
	}


	public String getTarg_itemid() {
		return targ_itemid;
	}


	public void setTarg_itemid(String targ_itemid) {
		this.targ_itemid = targ_itemid;
	}
	public ArrayList getOperlist() {
		return operlist;
	}
	public void setOperlist(ArrayList operlist) {
		this.operlist = operlist;
	}
	public LazyDynaBean getLockBean() {
		return lockBean;
	}
	public void setLockBean(LazyDynaBean lockBean) {
		this.lockBean = lockBean;
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
	public HashMap getPrechangemap() {
		return prechangemap;
	}
	public void setPrechangemap(HashMap prechangemap) {
		this.prechangemap = prechangemap;
	}
	public String getIsEmployee() {
		return isEmployee;
	}
	public void setIsEmployee(String isEmployee) {
		this.isEmployee = isEmployee;
	}
	public String getCheckhmuster() {
		return checkhmuster;
	}
	public void setCheckhmuster(String checkhmuster) {
		this.checkhmuster = checkhmuster;
	}
	public ArrayList getOutformlist() {
		return outformlist;
	}
	public void setOutformlist(ArrayList outformlist) {
		this.outformlist = outformlist;
	}
	public String getHmuster_sql() {
		return hmuster_sql;
	}
	public void setHmuster_sql(String hmuster_sql) {
		this.hmuster_sql = hmuster_sql;
	}
	public CardTagParamView getCardparam() {
		return cardparam;
	}
	public void setCardparam(CardTagParamView cardparam) {
		this.cardparam = cardparam;
	}
    public ArrayList getQueryfieldlist() {
		return queryfieldlist;
	}
	public void setQueryfieldlist(ArrayList queryfieldlist) {
		this.queryfieldlist = queryfieldlist;
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
	public ArrayList getLoglist() {
		return loglist;
	}
	public void setLoglist(ArrayList loglist) {
		this.loglist = loglist;
	}
	public HttpSession getSession2() {
		return session2;
	}
	public void setSession2(HttpSession session2) {
		this.session2 = session2;
	}
	public FormFile getFile() {
		return file;
	}
	public void setFile(FormFile file) {
		this.file = file;
	}
	
	public String getOnlyname() {
		return onlyname;
	}
	public void setOnlyname(String onlyname) {
		this.onlyname = onlyname;
	}
	public String getErrorFileName() {
		return errorFileName;
	}
	public void setErrorFileName(String errorFileName) {
		this.errorFileName = errorFileName;
	}
	public String getNeedcondition() {
		return needcondition;
	}
	public void setNeedcondition(String needcondition) {
		this.needcondition = needcondition;
	}
	public String getWarn_id() {
		return warn_id;
	}
	public void setWarn_id(String warn_id) {
		this.warn_id = warn_id;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public String getInfor_type() {
		return infor_type;
	}
	public void setInfor_type(String infor_type) {
		this.infor_type = infor_type;
	}
	public String getCodesetid() {
		return codesetid;
	}
	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}
	public String getSelectcodeitemids() {
		return selectcodeitemids;
	}
	public void setSelectcodeitemids(String selectcodeitemids) {
		this.selectcodeitemids = selectcodeitemids;
	}
	public ArrayList getCodeitemlist() {
		return codeitemlist;
	}
	public void setCodeitemlist(ArrayList codeitemlist) {
		this.codeitemlist = codeitemlist;
	}
	public String getChangemsg() {
		return changemsg;
	}
	public void setChangemsg(String changemsg) {
		this.changemsg = changemsg;
	}
	public String getTarcodeitemdesc() {
		return tarcodeitemdesc;
	}
	public void setTarcodeitemdesc(String tarcodeitemdesc) {
		this.tarcodeitemdesc = tarcodeitemdesc;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getDatefillable() {
		return datefillable;
	}
	public void setDatefillable(String datefillable) {
		this.datefillable = datefillable;
	}
	public String getCodedescfillable() {
		return codedescfillable;
	}
	public void setCodedescfillable(String codedescfillable) {
		this.codedescfillable = codedescfillable;
	}
	public String getTransfercodeitemid() {
		return transfercodeitemid;
	}
	public void setTransfercodeitemid(String transfercodeitemid) {
		this.transfercodeitemid = transfercodeitemid;
	}
	public String getTarorgname() {
		return tarorgname;
	}
	public String getSp_batch() {
		return sp_batch;
	}
	public void setSp_batch(String sp_batch) {
		this.sp_batch = sp_batch;
	}
	public void setTarorgname(String tarorgname) {
		this.tarorgname = tarorgname;
	}
	public String getIsInitData() {
		return isInitData;
	}
	public void setIsInitData(String isInitData) {
		this.isInitData = isInitData;
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
	public String getGeneralmessage() {
		return generalmessage;
	}
	public String getIndex_template() {
		return index_template;
	}
	public void setIndex_template(String index_template) {
		this.index_template = index_template;
	}
	public void setGeneralmessage(String generalmessage) {
		this.generalmessage = generalmessage;
	}
	public String getFilter_by_factor() {
		return filter_by_factor;
	}
	public void setFilter_by_factor(String filter_by_factor) {
		this.filter_by_factor = filter_by_factor;
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
	public String getNo_priv_ctrl() {
		return no_priv_ctrl;
	}
	public void setNo_priv_ctrl(String no_priv_ctrl) {
		this.no_priv_ctrl = no_priv_ctrl;
	}
	public String getIsSyncStruct() {
		return isSyncStruct;
	}
	public void setIsSyncStruct(String isSyncStruct) {
		this.isSyncStruct = isSyncStruct;
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
	public String getBatch_task() {
		return batch_task;
	}
	public void setBatch_task(String batch_task) {
		this.batch_task = batch_task;
	}
	//liuyz bug25753 
	public String getUpdateCount() {
		return updateCount;
	}
	public void setUpdateCount(String updateCount) {
		this.updateCount = updateCount;
	}
}
