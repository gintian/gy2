/**
 *
 */
package com.hjsj.hrms.businessobject.general.template.selfplatform;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hjsj.hrms.businessobject.general.template.*;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.businessobject.DownAttachUtils;
import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateModuleParam;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * <p>Title:TemplatePageBo.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-12-18 上午10:13:22</p>
 * <p>@version: 7.0</p>
 */
public class TemplateSelfApplyBo {
	private Connection conn=null;
	private UserView userView = null;
	/**员工发启申请，还是业务人员发启申请*/
	private boolean bEmploy=false;
	/**模板号*/
	private int tabid=-1;
	/**流程实例号*/
	private int ins_id=0;
	/**流程任务号*/
	private int task_id=0;
	/**实例列表*/
	private ArrayList inslist=new ArrayList();
	/**任务列表*/
	private ArrayList tasklist=new ArrayList();
	
	/**通过链接打开卡片时，只显示此ID的数据，格式 usr`00000009
	    */
	private String objectId="";  
		
	private String name;
	/**模板对象内容*/
	private RecordVo table_vo=null;
	/**是否发送邮件*/
	private boolean bemail=false;
	/**是否发送短信*/
	private boolean bsms=false;
	/**邮件通知到本人*/
	private boolean email_staff=false;
	private String template_staff="";  //员工本人的邮件模板
	private String template_bos="";    ////业务办理人员的邮件模板
	private String template_sp="";     //审批模板
	private String change_after_get_data="0";  //1：变化后指标取当前值  0：不取
	private String filter_by_manage_priv="0";  //接收通知单数据方式：0接收全部数据，1接收管理范围内数据
	private String include_suborg="1"; //0不包括下属单位, 1包括(默认值)
	private String import_notice_data = "0"; //默认变化后对应变化后 true  变化后对应变化前 false	
	private boolean isComputeVar=false; //是否已经计算过临时变量 2014-04-15
	
	/**人员移库后是否删除原库中的信息,1删除(默认值),0保留 */
	private String move_person="1";
	
	/** 模板关联的审批关系 */
	private String Relation_id="";
	
	/**是否需要审批*/
	private boolean bsp_flag=false;
	/** 是否自定义审批流程 */
	private boolean allow_defFlowSelf=false; 
	/**审批模式=0自动流转，=1手工指派*/
	private int sp_mode=0;
	/**目标库，确认时，表单数据提交至的原始数据库*/
	private String  dest_base="";
	
	/**数据提交入库不判断子集和指标权限, 0判断(默认值),1不判断  */
	private String UnrestrictedMenuPriv="0";
	
	/**数据录入不判断子集和指标权限, 0判断(默认值),1不判断  */
	private String UnrestrictedMenuPriv_Input="0";
	/**关联序号的变化后指标是否手工生成序号, 0加人时自动生成(默认值),1手工生成 */
	private String id_gen_manual="0";
	/**判断模板中包含了关联序号的变化后指标 0表示未包含,1表示包含*/
	private String existid_gen_manual="0";
	/**员工发启业务申请，已报批（有单子在途时）不允许再次申请,0:否(默认值) */
	private String  unique_check="0";
	private String opinion_field ="";
	
	
	
	//进入人员库
	private String  init_base="";
	
	/**输出的高级花名册的号列表:for examples 1`2`3`*/
	private String muster_str="";
	private String out_type="";//导出类型1 分页导出 2 连续页导出且绘制子集单元格时程序判断当子集数据少于2条，子集表格高度不按模板绘制的高度设置最小值，最小值高度仅为2空行高度 
	
	/**登记表号表表for examples 1`2`3`*/
	private String card_str="";
	/**执行的工资标准,标准号数组*/
	private String[] gz_stand;
	/**消息通知模板对象，模板号数组*/
	private String[] msg_template;
	/**业务流程描述*/
	private String content;
	/**业务类型
	 * 对人员调入的业务单独处理
	 * =0人员调入,=1调出（须指定目标人员库）,=2离退(须指定目标人员库),=3内部调动, =4系统内部调动
	 * =10其它不作特殊处理的业务
	 * 如果目标库未指定的话，则按源库进行处理
	 */
	private int operationtype=10; 
	private String operationcode;
	private String operationname;
	
	
	private String filter_by_factor="0"; //手工选人、条件选人按检索条件过滤, 0不过滤(默认值),1过滤
	private String no_priv_ctrl="0"; //手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按
	private String notice_initiator="false";//notice_initiator:提交时通知到发起人 false:不通知 true：通知
	private String template_initiator="";//提交时通知到发起人时要通知的模版
	
	private String insid_pro="";  //批量计算存储过程函数时，将此内容替换 流程号
	   //以下参数作用：批量提交时，归档增加效率
    private TemplateTableOutBo outPdfBo=null;//导出pdf类，批量提交时缓存使用。稍增加效率
    private boolean isSynedArchiveStruct=false;
    
	/** 
     * @return notice_initiator 
     */
    public String getNotice_initiator() {
        return notice_initiator;
    }

    /** 
     * @param noticeInitiator 要设置的 notice_initiator 
     */
    public void setNotice_initiator(String noticeInitiator) {
        notice_initiator = noticeInitiator;
    }

    /** 
     * @return template_initiator 
     */
    public String getTemplate_initiator() {
        return template_initiator;
    }

    /** 
     * @param templateInitiator 要设置的 template_initiator 
     */
    public void setTemplate_initiator(String templateInitiator) {
        template_initiator = templateInitiator;
    }



    private String headCount_control="1"; //编制控制  1|无此属性：控制（默认）　  0表示不控制
	/**
	 * 业务模块标志
	 * =1,日常管理（人事异动） =2,工资管理 =8,保险管理  =10,单位管理 =11,职位管理
	 */
	private int _static=1;
	
	/**人员过滤条件*/
	private String factor;
	/**规则条件,分析业务合法性*/
	private String llexpr;
	/**信息群类型
	 * 为了以后人事代码机构增加用
	 * =1对人员处理的业务模板
	 * =2对单位处理
	 * =3对职位处理,
	 * */
	private int infor_type=1;
	/**每英寸像素数
	 * default:windows 96
	 * mac             72
	 * */
	private int PixelInInch=96;  
	
	private String   archflag="0";  //原始表单是否归档, 0否(默认值),1归档
	private String   autoCaculate="";  //申请时自动计算  0不计算(默认值),1计算
	private String   spAutoCaculate="";  //审批时时自动计算  0不计算(默认值),1计算	
	private ArrayList subUpdateList=new ArrayList();
	private Document doc;
	/**与用户名有关的变动临时表的名称,当然对审批也可计算*/
	private String bz_tablename;
	/**关联更新上条记录*/
	private HashMap linkmap=new HashMap();
	/**关联更新上条记录*/
	private HashMap linkdateflagmap=new HashMap();
	/***下通知单***/
    private String msg_flag="0";
    private ArrayList mag_condlist=new ArrayList();
    private ArrayList mag_condlist_complex=new ArrayList();
    
    /** 调用的业务模块 0：为正常的人事异动 1:申诉业务  4：招聘录入    2：目标里的面谈记录  3:人事异动已批任务    5:我的申请  61:报备任务(待办) 62：报备任务（已办） 71：加签任务（待办）  72：加签任务（已办）    */
    private String business_model="0";
    
    private boolean  isValidateM_L=false; //是否已经验证过必填 和 逻辑表达式
    
    
    private HashMap otherParaMap=new HashMap();
    private String reject_cause="";  //驳回动态角色对象时的驳回原因
    
    private String endusertype="";    //手工指派模式最终办理人类型, 0: 用户, 1: 人员, 空表示未指定
    private String enduser="";        //手工指派模式最终办理人, enduser保存用户名或人员编号(人员库+A0100), 空表示未指定

    private String split_data_model="";  //分组类型  groupfield: 分组指标  superior:直接领导
    private String split_data_fields=""; //fields:定义的分组指标值  (x)代表控制的代码层级
    
    private String hmuster_sql="";    //当前模板处理人员的sql
    private boolean onlyComputeFieldVar=false;  //只计算模板指标中引入的临时变量
    private String filterStr = ""; 				//人员筛选
    
    private String priv_html="";
    private String unit_code_field="";   //单位代码指标
    private String pos_code_field="";    //岗位代码指标
    private String mappingStr="";        //机构划转、合并时原机构编码与新机构编码的对应关系 xxxx=yyyy,xxxx=yyyyy
    
    
    private HashMap sub_domain_map =null;
    private HashMap field_name_map =null;//存储人为改变类型的字段
    private String signxml="";			//电子签章
    private String approve_opinion="";  //审批信息
    private String view="";//<!-- 默认显示方式，list列表(默认),card卡片 -->
    
    private String midValue=""; //如果变量为空，用户设置的默认值 //已废弃 2016-5-4 

    private String task_sp_flag="";//任务状态，2为已批从任务监控里进到卡片方式。
    private ArrayList currentFieldlist=new ArrayList();  //模板当前页涉及的指标
    private String reject_type = "";//驳回方式 =1：逐级驳回 =2：驳回到发起人  郭峰
    private String def_flow_self = "0";//自定义审批流程 
    private String attachmentcount = "0";//模板中附件区域的总个数  郭峰
    private String attachmentAreaToType = "";//附件区域与附件类型的对应关系 郭峰
    private HashMap destination_a0100 = new HashMap();//最终的a0100  个人附件归档专用 郭峰
    private String no_sp_yj="0"; // no_sp_yj:审批不填写意见  1:选中   0:空表示没选中（默认）
    private String pcOrMobile = "-1"; //模板页取自哪 默认-1,取全部模板页  0,只取电脑端页 1,只取手机端页
    private String defaultselected="1"; //defaultselected  : 1 或 无此属性时表示审批记录默认全选
    private boolean isRepreateSubmit=false;//结束单据重复提交
	
	/**插入子集区域*/
	private HashMap submap=new HashMap();
	/**
	 * @param conn
	 * @param pageid
	 * @return
	 */
	public TemplateSelfApplyBo(Connection conn, int tabid,UserView userView)throws GeneralException {
		super();
		this.conn = conn;
		this.userView = userView;
		initdata();
		this.tabid = tabid;
		this.table_vo=readTemplate(tabid);
		initdata();	
		bz_tablename=this.userView.getUserName()+"templet_"+this.tabid;
	}
	
    
    /**
	 * 校验必填项（批量）
	 * @param tablename
	 * @param fieldlist
	 */
	public void checkMustFillItem_batch(String tablename,ArrayList fieldlist,String task_ids)throws GeneralException
	{
		boolean bflag=false;
		boolean version = PubFunc.isUseNewPrograme(this.userView);
		HashMap<String,String> errorPageNameMap=new HashMap<String,String>();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		RowSet rset2=null;
		try
		{
			StringBuffer buf=new StringBuffer();
		//	StringBuffer cond=new StringBuffer();
			
			if(this.infor_type==1) {
                buf.append("select a0101_1,a0100,basepre ");
            } else if(this.infor_type==2) {
                buf.append("select codeitemdesc_1,b0110 ");
            } else if(this.infor_type==3) {
                buf.append("select codeitemdesc_1,e01a1 ");
            }
			
			ArrayList filllist=new ArrayList();
			
			boolean flag=false;
			if(submap==null||submap.size()==0) {
                flag=true;
            }
			String task_id=task_ids.split(",")[0]; 
			if(Integer.parseInt(task_id)!=0) {
                buf.append(",ins_id ");
            }
			HashMap FieldPriv  = getFieldPrivFillable(""+task_id,this.conn);
			HashMap fieldMap = getFieldPriv(""+task_id,this.conn);
	        boolean isHaveSign=false;//可以设置多个电子签章，只取一个
	        boolean isAttachmentState = false;//附件是否有权限
	        com.hjsj.hrms.module.template.utils.TemplateUtilBo utilBo=new com.hjsj.hrms.module.template.utils.TemplateUtilBo(conn, userView);
	        TemplateModuleParam templateModuleParam = new TemplateModuleParam(this.conn,this.userView);
	        int node_id=Integer.parseInt(utilBo.getNodeIdByTask_ids(String.valueOf(task_id), String.valueOf(tabid)));//根据task_id获取节点id
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				String fieldname=item.getItemid();
				if (fieldname.startsWith("S_")){//以s_开头的是签章
                    if ((FieldPriv!=null&&FieldPriv.get(fieldname.toLowerCase())!=null
                            && "3".equals((String)FieldPriv.get(fieldname.toLowerCase())))){
                    	filllist.add(item);
                    	if(!isHaveSign){//如果已经拼接签章字段，不再重复添加
	                        isHaveSign=true;
	                        buf.append(",");
	                        buf.append("signature");
                    	}
                        bflag=true;
                    }
                    continue;
                }
				/**变化后指标且必填  2014-04-02 dengcan */
				if((item.isFillable()||(FieldPriv!=null&&FieldPriv.get(fieldname.toLowerCase()+"_2")!=null&& "3".equals((String)FieldPriv.get(fieldname.toLowerCase()+"_2"))))&&item.isChangeAfter()&&item.getVarible()==0)
				{
					String state=this.userView.analyseFieldPriv(item.getItemid());
                	if(state!=null&& "0".equals(state)) {
                        state=this.userView.analyseFieldPriv(item.getItemid().toUpperCase(),0);	//员工自助权限
                    }
                	
                
                	
                	if(FieldPriv!=null&&FieldPriv.get(fieldname.toLowerCase()+"_2")!=null){
                		state = ""+FieldPriv.get(fieldname.toLowerCase()+"_2");
//                		if(state.equals("2"))//bug 49844 节点上设置的写权限不应置为无权限
//                			state ="0";
                		if("3".equals(state)) {
                            state ="2";
                        }
                	}
                	if((this.infor_type==2||this.infor_type==3)&&("codesetid".equalsIgnoreCase(fieldname)|| "codeitemdesc".equalsIgnoreCase(fieldname)|| "corcode".equalsIgnoreCase(fieldname)|| "parentid".equalsIgnoreCase(fieldname)|| "start_date".equalsIgnoreCase(fieldname))) {
                        state="2";
                    }
					if((state==null||!"2".equals(state))&& "0".equals(this.UnrestrictedMenuPriv_Input))//xgq this.UnrestrictedMenuPriv_Input.equals("0")
                    {
                        continue;
                    }
					if(this.opinion_field!=null&&this.opinion_field.equalsIgnoreCase(item.getItemid()))//xgq 2011-8-17
                    {
                        continue;
                    }
					bflag=true;
					filllist.add(item);
					if(!fieldname.startsWith("attachment")){//附件的值需要单独查 //liuyz bug29549
						buf.append(",");
					}
					String field_name=item.getItemid()+"_2";
					if(fieldname.startsWith("attachment")){//附件的值需要单独查
						if("2".equals(state)) {
                            isAttachmentState = true;
                        }
					}else {
                        buf.append(field_name);
                    }
					if(item.isChar())
					{
						
					}
				}
				
				if(flag&&("t_".equalsIgnoreCase(item.getItemid().toLowerCase().substring(0,2))))
				{
					if(item.isChangeAfter()) {
                        this.submap.put(item.getFieldsetid(), item);
                    }
				}
			} //for i loop end.
			
			
			ArrayList fillsetlist=new ArrayList();
			HashMap   setdomainMap=new HashMap();
			//判断子集必填项
			Object[] key = this.submap.keySet().toArray();
			TFieldFormat fieldformat=null;
			for(int i=0;i<key.length;i++)
	        {
	        	String setid=(String)key[i];
	        	FieldItem item=(FieldItem)this.submap.get(setid);
	        	/**默认值处理*/
	        	String xml_param=item.getFormula();
	        	TSubSetDomain setdomain=new TSubSetDomain(xml_param);
	        	String mustfillrecord = setdomain.getMustfillrecord();//子集记录必填
	        	String state = "";
	    		if(FieldPriv!=null&&FieldPriv.get(setid.toLowerCase()+"_2")!=null){
            		state = ""+FieldPriv.get(setid.toLowerCase()+"_2");
            		if("3".equals(state)) {//节点设置必填
            			mustfillrecord = "true";
    	    		}
	    		}
	    		
	        	String id = setdomain.getId();
	    		ArrayList list=setdomain.getFieldfmtlist();
	    		boolean isNeed=false;
	    		for(int j=0;j<list.size();j++)
	    		{
	    			fieldformat=(TFieldFormat)list.get(j);
	    			if(fieldformat.isBneed())
	    			{
	    				isNeed=true;
	    				break;
	    			}
	    		}
	    		if(!isNeed&&"true".equals(mustfillrecord)) {
                    isNeed=true;
                }
	    		if(isNeed&&item.isChangeAfter())
	    		{
	    			bflag=true;
	    			if(!"".equals(id)) {
                        buf.append(",t_"+setid.toLowerCase()+"_"+id+"_2");
                    } else {
                        buf.append(",t_"+setid.toLowerCase()+"_2");
                    }
	    			fillsetlist.add(item);
	    			setdomainMap.put(item.getItemid(), setdomain);
	    		}
	        }
			
			String whl="";
			if(this.operationtype==8||this.operationtype==9) //合并||划转
			{
				if(this.infor_type==2) //单位
				{
					buf.append(",b0110,to_id");
					whl=" and b0110=to_id ";
				}
				else if(this.infor_type==3)
				{
					buf.append(",e01a1,to_id");
					whl=" and e01a1=to_id ";
				}
			}
			
			buf.append(" from ");
			buf.append(tablename);
			
			if(bflag)
			{ 
				buf.append(" where  exists (select null from t_wf_task_objlink where templet_"+this.tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+this.tabid+".ins_id=t_wf_task_objlink.ins_id ");
				buf.append("  and task_id in ("+task_ids+") and tab_id="+this.tabid+"  and submitflag=1  and (state is null or  state=0 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )) ");	
					
				buf.append(whl);
				rset=dao.search(buf.toString());
				boolean bNotFill=false;
				StringBuffer strInfo=new StringBuffer();
				strInfo.append("下列信息不能为空，请填写完整！\n\r");
				ResultSetMetaData rsetmd=null;
	            rsetmd=rset.getMetaData();
				while(rset.next())
				{
					String a0101="";
					if(this.infor_type==1) {
                        a0101=rset.getString("a0101_1")==null?"":rset.getString("a0101_1");
                    } else if(this.infor_type==2||this.infor_type==3)
					{
						a0101=rset.getString("codeitemdesc_1")==null?"":rset.getString("codeitemdesc_1");
					}
					
					String a0100 = "";
					String basepre = "";
					int ins_id=0;
					if(isAttachmentState){
						if(this.infor_type==1){
							a0100= rset.getString("a0100")==null?"":rset.getString("a0100");
							basepre  = rset.getString("basepre")==null?"":rset.getString("basepre");
						}else if(this.infor_type==2) {
                            a0100= rset.getString("b0110")==null?"":rset.getString("b0110");
                        } else if(this.infor_type==3) {
                            a0100= rset.getString("e01a1")==null?"":rset.getString("e01a1");
                        }
						if(Integer.parseInt(task_id)!=0) {
                            ins_id = rset.getInt("ins_id");
                        }
					}
					for(int i=0;i<filllist.size();i++)
					{
						FieldItem item=(FieldItem)filllist.get(i);
						 if (item.getItemid().startsWith("S_")){//判断电子签章
	                            String value=PubFunc.getValueByFieldType(rset,rsetmd,"signature");
	                            String checkStr="UserName=\""+this.userView.getUserName()+"\"";
	                            if(StringUtils.isNotBlank(value)&&node_id>0){//判断此节点是否填写了签章
		                            Document doc=null;
									Element element=null;
									doc=PubFunc.generateDom(value);;
									String xpath="/params/record/item";
									XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
									List childlist=findPath.selectNodes(doc);
									if(childlist.size()>0){
										Boolean isHaveSingle=false;
										for(int num=0;num<childlist.size();num++){
											element=(Element)childlist.get(num);
											int eleNode_id=Integer.parseInt(element.getAttributeValue("node_id"));
											int elePageId=Integer.parseInt(element.getAttributeValue("PageID"));
											int eleGridno=Integer.parseInt(element.getAttributeValue("GridNO"));
											String delflag=StringUtils.isBlank(String.valueOf(element.getAttributeValue("delflag")))?"false":"true".equalsIgnoreCase(String.valueOf(element.getAttributeValue("delflag")))?"true":"false";
											if(templateModuleParam.getSignatureType()==2) {
												if(item.getItemid().equalsIgnoreCase("S_"+elePageId+"_"+eleGridno)&&"false".equalsIgnoreCase(delflag)){
													isHaveSingle=true;
												}
											}else {
												if(node_id==eleNode_id&&item.getItemid().equalsIgnoreCase("S_"+elePageId+"_"+eleGridno)&&"false".equalsIgnoreCase(delflag)){
													isHaveSingle=true;
												}
											}
										}
										if(!isHaveSingle){
											String itemDesc=item.getItemdesc();
	                                        String pageNames=getPageNameByItem(this.tabid+"",item.getItemid()+"_2",itemDesc);
	                                        int pageInd=pageNames.indexOf("``");
	                                        if(pageInd!=-1) {
	                                            itemDesc=pageNames.substring(pageInd+2);
	                                            pageNames=pageNames.substring(0,pageInd-1);
	                                        }else if(pageNames.indexOf(",")!=-1) {
	                                            pageNames=pageNames.substring(0,pageNames.length()-1);
	                                        }
	                                        if(errorPageNameMap.containsKey(pageNames)) {
	                                            errorPageNameMap.put(pageNames, errorPageNameMap.get(pageNames)+"["+itemDesc+"]、");
	                                        }else {
	                                            errorPageNameMap.put(pageNames, "["+itemDesc+"]、");
	                                        }
										}
									}else{
										String itemDesc=item.getItemdesc();
	                                    String pageNames=getPageNameByItem(this.tabid+"",item.getItemid()+"_2",itemDesc);
	                                    int pageInd=pageNames.indexOf("``");
	                                    if(pageInd!=-1) {
	                                        itemDesc=pageNames.substring(pageInd+2);
	                                        pageNames=pageNames.substring(0,pageInd-1);
	                                    }else if(pageNames.indexOf(",")!=-1) {
	                                        pageNames=pageNames.substring(0,pageNames.length()-1);
	                                    }
	                                    if(errorPageNameMap.containsKey(pageNames)) {
	                                        errorPageNameMap.put(pageNames, errorPageNameMap.get(pageNames)+"["+itemDesc+"]、");
	                                    }else {
	                                        errorPageNameMap.put(pageNames, "["+itemDesc+"]、");
	                                    }
									}
	                            }
	                            else if(value==null || !value.contains(item.getItemid())){
	                            	String itemDesc=item.getItemdesc();
	                                String pageNames=getPageNameByItem(this.tabid+"",item.getItemid()+"_2",itemDesc);
	                                int pageInd=pageNames.indexOf("``");
	                                if(pageInd!=-1) {
	                                    itemDesc=pageNames.substring(pageInd+2);
	                                    pageNames=pageNames.substring(0,pageInd-1);
	                                }else if(pageNames.indexOf(",")!=-1) {
	                                    pageNames=pageNames.substring(0,pageNames.length()-1);
	                                }
	                                if(errorPageNameMap.containsKey(pageNames)) {
	                                    errorPageNameMap.put(pageNames, errorPageNameMap.get(pageNames)+"["+itemDesc+"]、");
	                                }else {
	                                    errorPageNameMap.put(pageNames, "["+itemDesc+"]、");
	                                }
	                            }
	                        }
						else if(item.getItemid().startsWith("attachment")){//附件
                        	if(isAttachmentState){
                        		ArrayList valuelist = this.checkAttachMustFill(ins_id,item,basepre,a0100);//为了拿到附件的设置信息，需要窜入FieldItem对象。
        						if(valuelist.size()==0 && version){
        							String itemDesc=item.getItemdesc();
                                    String pageNames=getPageNameByItem(this.tabid+"",item.getItemid()+"_2",itemDesc);
                                    int pageInd=pageNames.indexOf("``");
                                    if(pageInd!=-1) {
                                        itemDesc=pageNames.substring(pageInd+2);
                                        pageNames=pageNames.substring(0,pageInd-1);
                                    }else if(pageNames.indexOf(",")!=-1) {
                                        pageNames=pageNames.substring(0,pageNames.length()-1);
                                    }
                                    if(errorPageNameMap.containsKey(pageNames)) {
                                        errorPageNameMap.put(pageNames, errorPageNameMap.get(pageNames)+"["+itemDesc+"]、");
                                    }else {
                                        errorPageNameMap.put(pageNames, "["+itemDesc+"]、");
                                    }
        						}
        					}
                        }
                        else {
                            String field_name=item.getItemid()+"_2";
                            String value=PubFunc.getValueByFieldType(rset,rsetmd,field_name.toLowerCase(),true);//liuyz 32377 数值型指标用户没有填，true:返回空串，false返回0.0。
                            if(value==null||value.length()==0/*||(item.getItemtype().equalsIgnoreCase("N")&&Double.parseDouble(value)==0)*/)
                            {
                            	String itemDesc=item.getItemdesc();
                                String pageNames=getPageNameByItem(this.tabid+"",item.getItemid()+"_2",itemDesc);
                                int pageInd=pageNames.indexOf("``");
                                if(pageInd!=-1) {
                                    itemDesc=pageNames.substring(pageInd+2);
                                    pageNames=pageNames.substring(0,pageInd-1);
                                }else if(pageNames.indexOf(",")!=-1) {
                                    pageNames=pageNames.substring(0,pageNames.length()-1);
                                }
                                if(errorPageNameMap.containsKey(pageNames)) {
                                    errorPageNameMap.put(pageNames, errorPageNameMap.get(pageNames)+"["+itemDesc+"]、");
                                }else {
                                    errorPageNameMap.put(pageNames, "["+itemDesc+"]、");
                                }
                            }
                        }
						
					}//for i loop end.
					
					//判断子集必填项
					for(int i=0;i<fillsetlist.size();i++)
					{
						FieldItem item=(FieldItem)fillsetlist.get(i);
						TSubSetDomain setDomain=(TSubSetDomain)setdomainMap.get(item.getItemid());
						String field_name="";
						String id = setDomain.getId();
						if(!"".equals(id)) {
                            field_name = item.getItemid()+"_"+id+"_2";
                        } else {
                            field_name = item.getItemid()+"_2";
                        }
						String fieldname = item.getFieldsetid().toLowerCase()+"_2";
						String value=PubFunc.getValueByFieldType(rset,rsetmd,field_name.toLowerCase());
						ArrayList fieldfmtlist=setDomain.getFieldfmtlist();
						String mustfillrecord = setDomain.getMustfillrecord();//子集记录必填
						String state = "";
			    		if(FieldPriv!=null&&FieldPriv.get(item.getFieldsetid().toLowerCase()+"_2")!=null){
		            		state = ""+FieldPriv.get(item.getFieldsetid().toLowerCase()+"_2");
		            		if("3".equals(state)) {//节点设置必填
		            			mustfillrecord = "true";
		    	    		}
			    		}
						boolean hasRecord = false;
						HashSet set=new HashSet();
						if(value!=null&&value.length()>0)
						{
							ArrayList recordList=setDomain.getRecordList(value.trim());
							if (recordList.size()>0) {
                                hasRecord=true;
                            }
							for(int j=0;j<recordList.size();j++)
							{
								HashMap map=(HashMap)recordList.get(j);
								for(int e=0;e<fieldfmtlist.size();e++)
					    		{
					    			fieldformat=(TFieldFormat)fieldfmtlist.get(e);
					    			if(fieldformat.isBneed())
					    			{
					    				String itemid=fieldformat.getName().toLowerCase();
					    				if(map.get(itemid)==null||((String)map.get(itemid)).trim().length()==0)
					    				{
						    				String itemDesc="";
		                                    String pageNames=getPageNameByItem(tabid+"",field_name,"");
		                                    int pageInd=pageNames.indexOf("``");
		                                    if(pageInd!=-1) {
		                                        itemDesc=pageNames.substring(pageInd+2);
		                                    }
					    					if("attach".equalsIgnoreCase(itemid))
					    					{
					    						FieldSet set_vo=DataDictionary.getFieldSetVo(setDomain.getSetname().toLowerCase());
					    						if(set_vo!=null) {
                                                    itemDesc=set_vo.getFieldsetdesc();
                                                } else {
                                                    itemDesc=item.getItemdesc();//bug 31468
                                                }
					    					}
					    					else{
		                                        itemDesc=StringUtils.isEmpty(itemDesc)?item.getItemdesc():itemDesc;
		                                    }
					    					set.add(itemDesc + "：" + fieldformat.getTitle());
					    				}
					    				
					    			}
					    		}
							}
							
							if(set.size()>0)
							{
                                StringBuffer temp_error=new StringBuffer();
                                for (Iterator t = set.iterator(); t.hasNext();) {
                                    temp_error.append("[");
                                    temp_error.append((String) t.next());
                                    temp_error.append("]、");
                                }
                                String pageNames=getPageNameByItem(tabid+"",field_name,"");
                                int pageInd=pageNames.indexOf("``");
                                if(pageInd!=-1) {
                                    pageNames=pageNames.substring(0,pageInd-1);
                                }else if(pageNames.indexOf(",")!=-1) {
                                    pageNames=pageNames.substring(0,pageNames.length()-1);
                                }
                                if(errorPageNameMap.containsKey(pageNames)) {
                                    errorPageNameMap.put(pageNames, errorPageNameMap.get(pageNames)+temp_error.toString());
                                }else {
                                    errorPageNameMap.put(pageNames, temp_error.toString());
                                }
                            
							}
							
						}
						
						//子集无数据 但是有必填项也需要提示出来 20170418 wangrd
						if (!hasRecord&&"true".equals(mustfillrecord)){
							for(int e=0;e<fieldfmtlist.size();e++)
				    		{
				    			fieldformat=(TFieldFormat)fieldfmtlist.get(e);
				    			String name=fieldformat.getName().toLowerCase();
								FieldItem item_=DataDictionary.getFieldItem(name);
								//判断子集的指标权限
				    			String a_state="2";
								if(item_!=null){
									if ("1".equals(this.UnrestrictedMenuPriv_Input)){
										a_state="2";
					                }else{
					                	a_state=this.userView.analyseFieldPriv(item_.getItemid());
					                }
					                if(fieldMap!=null&&fieldMap.get(fieldname)!=null){
			                			//if (!"0".equals(task_id)) {// 如果不是发起人的话,那么就要判断节点的读写权限
			                			a_state = ((String)fieldMap.get(fieldname)).toLowerCase();
			                			//}
				                	}
					                if("1".equals(this.userView.getHm().get("fillInfo"))) {
                                        a_state="2";
                                    }
								}
				    			if(fieldformat.isBneed()&&"2".equals(a_state))
				    			{
				    				String itemid=fieldformat.getName().toLowerCase();
				    				String itemDesc="";
                                    String pageNames=getPageNameByItem(tabid+"",field_name,"");
                                    int pageInd=pageNames.indexOf("``");
                                    if(pageInd!=-1) {
                                        itemDesc=pageNames.substring(pageInd+2);
                                    }
			    					if("attach".equalsIgnoreCase(itemid))
			    					{
			    						FieldSet set_vo=DataDictionary.getFieldSetVo(setDomain.getSetname().toLowerCase());
			    						if(set_vo!=null) {
                                            itemDesc=set_vo.getFieldsetdesc();
                                        } else {
                                            itemDesc=item.getItemdesc();//bug 31468
                                        }
			    					}
			    					else{
                                        itemDesc=StringUtils.isEmpty(itemDesc)?item.getItemdesc():itemDesc;
                                    }
			    					set.add(itemDesc + "：" + fieldformat.getTitle());
				    			}
				    		}
							if(set.size()>0)
							{
								StringBuffer temp_error=new StringBuffer();
                                for (Iterator t = set.iterator(); t.hasNext();) {
                                    temp_error.append("[");
                                    temp_error.append((String) t.next());
                                    temp_error.append("]、");
                                }
                                String pageNames=getPageNameByItem(tabid+"",field_name,"");
                                int pageInd=pageNames.indexOf("``");
                                if(pageInd!=-1) {
                                    pageNames=pageNames.substring(0,pageInd-1);
                                }else if(pageNames.indexOf(",")!=-1) {
                                    pageNames=pageNames.substring(0,pageNames.length()-1);
                                }
                                if(errorPageNameMap.containsKey(pageNames)) {
                                    errorPageNameMap.put(pageNames, errorPageNameMap.get(pageNames)+temp_error.toString());
                                }else {
                                    errorPageNameMap.put(pageNames, temp_error.toString());
                                }
                            } else {
                                String itemDesc=item.getItemdesc();
                                String pageNames=getPageNameByItem(tabid+"",field_name,itemDesc);
                                int pageInd=pageNames.indexOf("``");
                                if(pageInd!=-1) {
                                    itemDesc=pageNames.substring(pageInd+2);
                                    pageNames=pageNames.substring(0,pageInd-1);
                                }else if(pageNames.indexOf(",")!=-1) {
                                    pageNames=pageNames.substring(0,pageNames.length()-1);
                                }
                                StringBuffer error_info_sub = new StringBuffer("");
                                error_info_sub.append("[");
                                error_info_sub.append(itemDesc);
                                error_info_sub.append("]、");
                                if(errorPageNameMap.containsKey(pageNames)) {
                                    errorPageNameMap.put(pageNames, errorPageNameMap.get(pageNames)+error_info_sub.toString());
                                }else {
                                    errorPageNameMap.put(pageNames, error_info_sub.toString());
                                }
							}
						}
					
					}
					int i=1;
					strInfo.append(a0101+"的");
	                for(Map.Entry<String, String> entry:errorPageNameMap.entrySet()) {
	                    String val=entry.getValue();
	                    bNotFill = true;
	                    //23`基本信息,24`基本信息
	                    String pageInfo=entry.getKey();
	                    String pageDesc="";
	                    String[] pageArr=pageInfo.split(",");
	                    for(int m=0;m<pageArr.length;m++) {
	                        String pageOne=pageArr[m];
	                        if(pageOne.indexOf("`")!=-1) {
	                            String pageId=pageOne.split("`")[0];
	                            pageDesc+=pageOne.split("`")[1]+",";
	                        }
	                    }
	                    val=val.substring(0, val.length()-1);
	                    pageDesc=StringUtils.isEmpty(pageDesc)?"":pageDesc.substring(0, pageDesc.length()-1);
	                    strInfo.append(i+"、");
	                    strInfo.append(pageDesc+" 页的"+val+" 信息未填写");
	                    strInfo.append("\n\r<br>");
	                    i++;
	                }
				}//for while end.
				
				
				
				if(bNotFill)
				{
					throw new GeneralException(strInfo.toString());
				}
			}
			
			
			
			StringBuffer sql=new StringBuffer("");
			sql.append("select * from ");
			sql.append(tablename);
			 
			 
			sql.append(" where  exists (select null from t_wf_task_objlink where templet_"+this.tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+this.tabid+".ins_id=t_wf_task_objlink.ins_id ");
			sql.append("  and task_id in ("+task_ids+") and tab_id="+this.tabid+"  and submitflag=1  and (state is null or  state=0 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )) ");	
					
			if(operationtype!=0&&operationtype!=5)//不是人员调入型&&不是 新建（单位|部门|岗位）
			{
				String _sql=sql.toString();
				if(this.infor_type==1)
				{
					_sql=_sql.replaceAll("\\*", "distinct lower(basepre) basepre ");
					rset=dao.search(_sql); 
					while(rset.next())
					{
						String basepre=rset.getString("basepre");
						rset2=dao.search(sql+" and lower(basepre)='"+basepre+"' and not exists (select null from "+basepre+"a01 where "+basepre+"a01.a0100="+tablename+".a0100 )");
						StringBuffer noExitMen=new StringBuffer("");
						while(rset2.next())
						{
							String a0101=rset2.getString("a0101_1")==null?"":rset2.getString("a0101_1");
							noExitMen.append(","+a0101);
						}
						if(noExitMen.length()>0)
						{
							throw new GeneralException(noExitMen.substring(1)+" 已移库,请删除当前记录。");
						}	
					}
				}
				else
				{
					
					String join="e01a1";
					if(this.infor_type==2) {
                        join="b0110";
                    }
					StringBuffer noExitMen=new StringBuffer("");
					String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
					String _str=" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ";
					rset2=dao.search(sql+" and    not exists (select null from organization where  organization.codeitemid="+tablename+"."+join+" "+_str+" ) ");
					while(rset2.next())
					{
					    if(operationtype==8){//如果是合并
					        String combineb0110="";
					        String combinetoid="";
					        if(this.infor_type==3){
					            combineb0110=rset2.getString("e01a1");
					        }else if(this.infor_type==2){
					            combineb0110=rset2.getString("b0110");
					        }
					        combinetoid=rset2.getString("to_id");
					        if(!"".equals(combineb0110)&&!"".equals(combinetoid)&&combineb0110.equals(combinetoid)){
					            continue;
					        }
					    }
						noExitMen.append(",");
						noExitMen.append(rset2.getString("codeitemdesc_1")==null?"":rset2.getString("codeitemdesc_1"));
					}
					if(noExitMen.length()>0)
					{
						throw new GeneralException(noExitMen.substring(1)+" 已移库,请删除当前记录。");
					}
					
				}
			}
			
			if(this.infor_type==1) {
                validateOnlyValue(sql.toString(),tablename);
            }
			if(this.infor_type!=1) {
                checkNewOrgFillItem(sql.toString(),this.operationtype);
            }
			
		}
		catch(Exception ex)
		{
		//	ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		finally
		{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
				if(rset2!=null) {
                    rset2.close();
                }
			}
			catch(Exception ee)
			{
				
			}
			
		}
	}
	
	
	/**
	 * 校验必填项
	 * @param tablename
	 * @param fieldlist
	 */
	public void checkMustFillItem(String tablename,ArrayList fieldlist,int task_id)throws GeneralException
	{
		boolean bflag=false;
		 HashMap<String,String> errorPageNameMap=new HashMap<String,String>();
		//30546 增加锁版本校验
		boolean version = PubFunc.isUseNewPrograme(userView);
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		RowSet rset2=null;
		try
		{
			StringBuffer buf=new StringBuffer();
		//	StringBuffer cond=new StringBuffer();
			
			if(this.infor_type==1) {
                buf.append("select a0101_1,a0100,basepre ");
            } else if(this.infor_type==2) {
                buf.append("select codeitemdesc_1,b0110 ");
            } else if(this.infor_type==3) {
                buf.append("select codeitemdesc_1,e01a1 ");
            }
			if(task_id!=0) {
                buf.append(",ins_id ");
            }
			ArrayList filllist=new ArrayList();
			
			boolean flag=false;
			if(submap==null||submap.size()==0) {
                flag=true;
            }
			
			HashMap FieldPriv  = getFieldPrivFillable(""+task_id,this.conn);
			HashMap fieldMap = getFieldPriv(""+task_id,this.conn);
	        boolean isHaveSign=false;//可以设置多个电子签章，只取一个
	        boolean isAttachmentState = false;//附件是否有权限
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				String fieldname=item.getItemid();
                if (fieldname.startsWith("S_")){
                    if ((FieldPriv!=null&&FieldPriv.get(fieldname.toLowerCase())!=null
                            && "3".equals((String)FieldPriv.get(fieldname.toLowerCase())))){
                        filllist.add(item);
                        if(!isHaveSign){
	                        isHaveSign=true;
	                        buf.append(",");
	                        buf.append("signature");
	                        bflag=true;
                        }
                    }
                    continue;
                }
				/**变化后指标且必填  2014-04-02 dengcan */
				if((item.isFillable()||(FieldPriv!=null&&FieldPriv.get(fieldname.toLowerCase()+"_2")!=null&& "3".equals((String)FieldPriv.get(fieldname.toLowerCase()+"_2"))))&&(item.isChangeAfter()||((!"".equals(this.dest_base)&&"photo".equalsIgnoreCase(fieldname))||fieldname.startsWith("attachment")))&&item.getVarible()==0)
				{
					String state=userView.analyseFieldPriv(item.getItemid());
                	if(state!=null&& "0".equals(state)) {
                        state=userView.analyseFieldPriv(item.getItemid().toUpperCase(),0);	//员工自助权限
                    }
                	
                	if(FieldPriv!=null&&FieldPriv.get(fieldname.toLowerCase()+"_2")!=null){
                		state = ""+FieldPriv.get(fieldname.toLowerCase()+"_2");
//                		if(state.equals("2")) //bug 49844 节点上设置的写权限不应置为无权限
//                			state ="0";
                		if("3".equals(state)) {
                            state ="2";
                        }
                	}
                	if("1".equals(userView.getHm().get("fillInfo"))){//特殊情况 由外部链接进入
                		state ="2";
                	}
                	if((!"".equals(this.dest_base)&&"photo".equalsIgnoreCase(fieldname))||fieldname.startsWith("attachment")){//附件和照片不判断权限
                		state ="2";
                	}
                	if((this.infor_type==2||this.infor_type==3)&&("codesetid".equalsIgnoreCase(fieldname)|| "codeitemdesc".equalsIgnoreCase(fieldname)|| "corcode".equalsIgnoreCase(fieldname)|| "parentid".equalsIgnoreCase(fieldname)|| "start_date".equalsIgnoreCase(fieldname))) {
                        state="2";
                    }
					if((state==null||!"2".equals(state))&& "0".equals(this.UnrestrictedMenuPriv_Input))//xgq this.UnrestrictedMenuPriv_Input.equals("0")
                    {
                        continue;
                    }
					if(this.opinion_field!=null&&this.opinion_field.equalsIgnoreCase(item.getItemid()))//xgq 2011-8-17
                    {
                        continue;
                    }
					bflag=true;
					filllist.add(item);
					if(!fieldname.startsWith("attachment")){//附件的值需要单独查 //liuyz bug29549
						buf.append(",");
					}
					String field_name=item.getItemid()+"_2";
					if("photo".equalsIgnoreCase(fieldname)) {
                        field_name = item.getItemid();
                    }
					if(fieldname.startsWith("attachment")){//附件的值需要单独查
						if("2".equals(state)) {
                            isAttachmentState = true;
                        }
					}else {
                        buf.append(field_name);
                    }
					if(item.isChar())
					{
						
					}
				}
				
				if(flag&&("t_".equalsIgnoreCase(item.getItemid().toLowerCase().substring(0,2))))
				{
					if(item.isChangeAfter()){
						//this.submap.put(item.getFieldsetid()+"_"+i, item);
						//bug 33854 一个模版中同一子集插入多个变化后，用map存储会丢失部分，导致必填判断不准确。
						if(this.sub_domain_map!=null&&
								this.sub_domain_map.get(""+i)!=null
								&&this.sub_domain_map.get(""+i).toString().trim().length()>0){
							String tmp= item.getFieldsetid()+"_"+this.sub_domain_map.get(""+i).toString();
							submap.put(tmp, item);
							
						}	
						else {
							submap.put(item.getFieldsetid(), item);
						}
					}
				}
			} //for i loop end.
			
			if(buf.lastIndexOf(",")!=-1&&buf.lastIndexOf(",")==buf.length()-1){
				String buf_ = buf.substring(0,buf.lastIndexOf(","));
				buf.setLength(0);
				buf.append(buf_);
			}
			HashMap fillsetMap=new HashMap();//bug 33854 一个模版中同一子集插入多个变化后，用map存储会丢失部分，导致必填判断
			HashMap setdomainMap=new HashMap();
			//判断子集必填项
			Object[] key = this.submap.keySet().toArray();
			TFieldFormat fieldformat=null;
			for(int i=0;i<key.length;i++)
	        {
	        	String setid=(String)key[i];
	        	FieldItem item=(FieldItem)this.submap.get(setid);
	        	String field_name=setid.toLowerCase()+"_2";
	        	/**默认值处理*/
	        	String xml_param=item.getFormula();
	        	TSubSetDomain setdomain=new TSubSetDomain(xml_param);
	    		ArrayList list=setdomain.getFieldfmtlist();
	    		String mustfillrecord = setdomain.getMustfillrecord();//子集记录必填
	    		String id = setdomain.getId();
	    		boolean isNeed=false;
	    		for(int j=0;j<list.size();j++)
	    		{
	    			fieldformat=(TFieldFormat)list.get(j);
	    			if(fieldformat.isBneed())
	    			{
	    				isNeed=true;
	    				break;
	    			}
	    		}
	    		if(!isNeed&&"true".equals(mustfillrecord)) {
                    isNeed=true;
                }
	    		//判断子集的权限
	    		String state = "2";
	    		if ("1".equals(this.UnrestrictedMenuPriv_Input)){
                    state="2";
                }
                else {
                	state=userView.analyseTablePriv(setid);
                }
                if(fieldMap!=null&&fieldMap.get(field_name)!=null){
        			//if (task_id!=0) {// 如果不是发起人的话,那么就要判断节点的读写权限
        			state = (String)fieldMap.get(field_name);
        			//}
                }
                if("1".equals(userView.getHm().get("fillInfo"))) {
                    state="2";
                }
                //判断子集的权限
	    		if(isNeed&&item.isChangeAfter()&&"2".equals(state))
	    		{
	    			bflag=true;
	    			buf.append(",t_"+setid.toLowerCase()+"_2");
	    			//fillsetlist.add(item);
					//bug 33854 一个模版中同一子集插入多个变化后，用map存储会丢失部分，导致必填判断
	    			if(!"".equals(id)){
	    				setdomainMap.put(item.getItemid()+"_"+id, setdomain);
	    				fillsetMap.put(item.getItemid()+"_"+id, item);
	    			}else{
	    				setdomainMap.put(item.getItemid(), setdomain);
	    				fillsetMap.put(item.getItemid(), item);
	    			}
	    		}
	        }
			
			String whl="";
			if(this.operationtype==8||this.operationtype==9) //合并||划转
			{
				buf.append(",to_id");
				if(this.infor_type==2) //单位
				{
					whl=" and b0110=to_id ";
				}
				else if(this.infor_type==3)
				{
					whl=" and e01a1=to_id ";
				}
			}
			
			buf.append(" from ");
			buf.append(tablename);
			
			if(bflag)
			{
				if(task_id!=0)
				{
				/*	buf.append(" where task_id=");
					buf.append(task_id);
					buf.append(" and submitflag=1 ");
					*/
					 
					buf.append(" where  exists (select null from t_wf_task_objlink where templet_"+this.tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+this.tabid+".ins_id=t_wf_task_objlink.ins_id ");
					buf.append("  and task_id="+task_id+" and tab_id="+this.tabid+"  and submitflag=1  and (state is null or  state=0 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+userView.getUserName().toLowerCase()+"' or lower(username)='"+userView.getDbname().toLowerCase()+userView.getA0100()+"' ) ) )) ");	
					
				//	buf.append(" and ");
				//	cond.setLength(cond.length()-4);
				//	buf.append(cond.toString());
				}	
				else
				{
					buf.append(" where ");
					if(isBEmploy())//员工通过自助平台发动申请
					{
						buf.append(" a0100='"+userView.getA0100()+"' and lower(basepre)='"+userView.getDbname().toLowerCase()+"'");
					}
					else
					{
						
						buf.append(" submitflag=1 ");
					}
				//	buf.append(" and ");					
				//	cond.setLength(cond.length()-4);
				//	buf.append(cond.toString());					
				}		
				buf.append(whl);
				
				if((this.infor_type==2||this.infor_type==3)&&(this.operationtype==8||this.operationtype==9))
				{
					String key_="b0110";
					if(this.infor_type==3) {
                        key_="e01a1";
                    }
					buf.append("  order by "+Sql_switcher.isnull("to_id","100000000")+",case when "+key_+"=to_id then 100000000 else a0000 end asc ");
				}
				else {
                    buf.append(" order by a0000");
                }
				rset=dao.search(buf.toString());
				boolean bNotFill=false;
				StringBuffer strInfo=new StringBuffer();
				strInfo.append("下列信息不能为空，请填写完整！\n\r");
				if(PubFunc.isUseNewPrograme(userView)){
					strInfo.append("<br />");
				}
				ResultSetMetaData rsetmd=null;
	            rsetmd=rset.getMetaData();
	            com.hjsj.hrms.module.template.utils.TemplateUtilBo utilBo=new com.hjsj.hrms.module.template.utils.TemplateUtilBo(conn, userView);
	            int node_id=Integer.parseInt(StringUtils.isBlank(utilBo.getNodeIdByTask_ids(String.valueOf(task_id), String.valueOf(tabid)))?"-1":utilBo.getNodeIdByTask_ids(String.valueOf(task_id), String.valueOf(tabid)));//获取的值可能是空值，报错。
				while(rset.next())
				{
					String a0101="";
					if(this.infor_type==1) {
                        a0101=rset.getString("a0101_1")==null?"":rset.getString("a0101_1");
                    } else if(this.infor_type==2||this.infor_type==3)
					{
						a0101=rset.getString("codeitemdesc_1")==null?"":rset.getString("codeitemdesc_1");
					}
					
					String a0100 = "";
					String basepre = "";
					int ins_id=0;
					if(isAttachmentState){
						if(this.infor_type==1){
							a0100= rset.getString("a0100")==null?"":rset.getString("a0100");
							basepre  = rset.getString("basepre")==null?"":rset.getString("basepre");
						}else if(this.infor_type==2) {
                            a0100= rset.getString("b0110")==null?"":rset.getString("b0110");
                        } else if(this.infor_type==3) {
                            a0100= rset.getString("e01a1")==null?"":rset.getString("e01a1");
                        }
						if(task_id!=0) {
                            ins_id = rset.getInt("ins_id");
                        }
					}
					for(int i=0;i<filllist.size();i++)
					{
						FieldItem item=(FieldItem)filllist.get(i);
                        if (item.getItemid().startsWith("S_")){//判断电子签章
                            String value=PubFunc.getValueByFieldType(rset,rsetmd,"signature");
                            String checkStr="UserName=\""+userView.getUserName()+"\"";
                            if(StringUtils.isNotBlank(value)&&node_id>0){
	                            Document doc=null;
								Element element=null;
								doc=PubFunc.generateDom(value);;
								String xpath="/params/record/item";
								XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
								List childlist=findPath.selectNodes(doc);
								if(childlist.size()>0){
									Boolean isHaveSingle=false;
									for(int num=0;num<childlist.size();num++){
										element=(Element)childlist.get(num);
										int eleNode_id=Integer.parseInt(element.getAttributeValue("node_id"));
										int elePageId=Integer.parseInt(element.getAttributeValue("PageID"));
										int eleGridno=Integer.parseInt(element.getAttributeValue("GridNO"));
										String delflag=StringUtils.isBlank(String.valueOf(element.getAttributeValue("delflag")))?"false":"true".equalsIgnoreCase(String.valueOf(element.getAttributeValue("delflag")))?"true":"false";
										if(node_id==eleNode_id&&item.getItemid().equalsIgnoreCase("S_"+elePageId+"_"+eleGridno)&&"false".equalsIgnoreCase(delflag)){
											isHaveSingle=true;
										}
									}
									if(!isHaveSingle){
										String itemDesc=item.getItemdesc();
                                        String pageNames=getPageNameByItem(this.tabid+"",item.getItemid()+"_2",itemDesc);
                                        int pageInd=pageNames.indexOf("``");
                                        if(pageInd!=-1) {
                                            itemDesc=pageNames.substring(pageInd+2);
                                            pageNames=pageNames.substring(0,pageInd-1);
                                        }else if(pageNames.indexOf(",")!=-1) {
                                            pageNames=pageNames.substring(0,pageNames.length()-1);
                                        }
                                        if(errorPageNameMap.containsKey(pageNames)) {
                                            errorPageNameMap.put(pageNames, errorPageNameMap.get(pageNames)+"["+itemDesc+"]、");
                                        }else {
                                            errorPageNameMap.put(pageNames, "["+itemDesc+"]、");
                                        }
									}
								}else{
									String itemDesc=item.getItemdesc();
                                    String pageNames=getPageNameByItem(this.tabid+"",item.getItemid()+"_2",itemDesc);
                                    int pageInd=pageNames.indexOf("``");
                                    if(pageInd!=-1) {
                                        itemDesc=pageNames.substring(pageInd+2);
                                        pageNames=pageNames.substring(0,pageInd-1);
                                    }else if(pageNames.indexOf(",")!=-1) {
                                        pageNames=pageNames.substring(0,pageNames.length()-1);
                                    }
                                    if(errorPageNameMap.containsKey(pageNames)) {
                                        errorPageNameMap.put(pageNames, errorPageNameMap.get(pageNames)+"["+itemDesc+"]、");
                                    }else {
                                        errorPageNameMap.put(pageNames, "["+itemDesc+"]、");
                                    }
								}
                            }
                            else if(value==null || !value.contains(item.getItemid())){
                            	String itemDesc=item.getItemdesc();
                                String pageNames=getPageNameByItem(this.tabid+"",item.getItemid()+"_2",itemDesc);
                                int pageInd=pageNames.indexOf("``");
                                if(pageInd!=-1) {
                                    itemDesc=pageNames.substring(pageInd+2);
                                    pageNames=pageNames.substring(0,pageInd-1);
                                }else if(pageNames.indexOf(",")!=-1) {
                                    pageNames=pageNames.substring(0,pageNames.length()-1);
                                }
                                if(errorPageNameMap.containsKey(pageNames)) {
                                    errorPageNameMap.put(pageNames, errorPageNameMap.get(pageNames)+"["+itemDesc+"]、");
                                }else {
                                    errorPageNameMap.put(pageNames, "["+itemDesc+"]、");
                                }
                            }
                        }
                        else if(item.getItemid().startsWith("attachment")){//附件
                        	if(isAttachmentState){
                        		ArrayList valuelist = this.checkAttachMustFill(ins_id,item,basepre,a0100);//为了拿到附件的设置信息，需要窜入FieldItem对象。
                        		//30546  linbz 附件增加锁版本校验
        						if(valuelist.size()==0 && version){
        							String itemDesc=item.getItemdesc();
                                    String pageNames=getPageNameByItem(this.tabid+"",item.getItemid()+"_2",itemDesc);
                                    int pageInd=pageNames.indexOf("``");
                                    if(pageInd!=-1) {
                                        itemDesc=pageNames.substring(pageInd+2);
                                        pageNames=pageNames.substring(0,pageInd-1);
                                    }else if(pageNames.indexOf(",")!=-1) {
                                        pageNames=pageNames.substring(0,pageNames.length()-1);
                                    }
                                    if(errorPageNameMap.containsKey(pageNames)) {
                                        errorPageNameMap.put(pageNames, errorPageNameMap.get(pageNames)+"["+itemDesc+"]、");
                                    }else {
                                        errorPageNameMap.put(pageNames, "["+itemDesc+"]、");
                                    }
        						}
        					}
                        }else{
                            String field_name=item.getItemid()+"_2";
                            if("photo".equalsIgnoreCase(item.getItemid())) {
                                field_name = item.getItemid();
                            }
                            String value = "";
                            if("N".equalsIgnoreCase(item.getItemtype())) {
                            	value = rset.getString(field_name.toLowerCase());
                            	if(value!=null&&!"".equals(value)) {
                                    value=PubFunc.getValueByFieldType(rset,rsetmd,field_name.toLowerCase());
                                }
                            }else {
                                value=PubFunc.getValueByFieldType(rset,rsetmd,field_name.toLowerCase());
                            }
                            if("1".equals(userView.getHm().get("fillInfo"))){
                            	if(value.indexOf("临时人员_")!=-1){
                            		value="";
                            	}
                            }
                            if(value==null||value.length()==0/*||(item.getItemtype().equalsIgnoreCase("N")&&Double.parseDouble(value)==0)*/)
                            {
                            	String itemDesc=item.getItemdesc();
                                String pageNames=getPageNameByItem(this.tabid+"",item.getItemid()+"_2",itemDesc);
                                int pageInd=pageNames.indexOf("``");
                                if(pageInd!=-1) {
                                    itemDesc=pageNames.substring(pageInd+2);
                                    pageNames=pageNames.substring(0,pageInd-1);
                                }else if(pageNames.indexOf(",")!=-1) {
                                    pageNames=pageNames.substring(0,pageNames.length()-1);
                                }
                                if(errorPageNameMap.containsKey(pageNames)) {
                                    errorPageNameMap.put(pageNames, errorPageNameMap.get(pageNames)+"["+itemDesc+"]、");
                                }else {
                                    errorPageNameMap.put(pageNames, "["+itemDesc+"]、");
                                }
                            }
                        }
						
					}//for i loop end.
					
					//判断子集必填项 bug 33854 一个模版中同一子集插入多个变化后，用map存储会丢失部分，导致必填判断
					Object[] key_ =setdomainMap.keySet().toArray();
					for(int i=0;i<key_.length;i++)
					{
						FieldItem item=(FieldItem)fillsetMap.get(key_[i]);
						TSubSetDomain setDomain=(TSubSetDomain)setdomainMap.get(key_[i]);
						String id = setDomain.getId();
						String field_name = "";
						if(!"".equals(id)) {
                            field_name=item.getItemid()+"_"+id+"_2";
                        } else {
                            field_name=item.getItemid()+"_2";
                        }
						String fieldname = item.getFieldsetid().toLowerCase()+"_2";
						String value=PubFunc.getValueByFieldType(rset,rsetmd,field_name.toLowerCase());
						boolean hasRecord=false;//标识子集有记录数
						ArrayList fieldfmtlist=setDomain.getFieldfmtlist();
						ArrayList recordList=setDomain.getRecordList(value.trim());
						String mustfillrecord = setDomain.getMustfillrecord();
						HashSet set=new HashSet();
						if(value!=null&&value.length()>0)
						{
							if (recordList.size()>0) {
                                hasRecord=true;
                            }
							for(int j=0;j<recordList.size();j++)
							{
								HashMap map=(HashMap)recordList.get(j);
								for(int e=0;e<fieldfmtlist.size();e++)
					    		{
					    			fieldformat=(TFieldFormat)fieldfmtlist.get(e);
					    			String name=fieldformat.getName().toLowerCase();
									FieldItem item_=DataDictionary.getFieldItem(name);
									String a_state="2";
									if(item_!=null){
										//判断子集的指标权限
										if ("1".equals(this.UnrestrictedMenuPriv_Input)){
											a_state="2";
						                }else{
						                	a_state=userView.analyseFieldPriv(item_.getItemid());
						                }
						                if(fieldMap!=null&&fieldMap.get(fieldname)!=null){
				                			//if (task_id!=0) {// 如果不是发起人的话,那么就要判断节点的读写权限
				                			a_state = ((String)fieldMap.get(fieldname)).toLowerCase();
				                			//}
					                	}
						                if("1".equals(userView.getHm().get("fillInfo"))) {
                                            a_state="2";
                                        }
									}
					    			if(fieldformat.isBneed()&&"2".equals(a_state))
					    			{
					    				String itemid=fieldformat.getName().toLowerCase();
					    				if(map.get(itemid)==null||((String)map.get(itemid)).trim().length()==0)
					    				{
					    					String itemDesc="";
		                                    String pageNames=getPageNameByItem(tabid+"",field_name,"");
		                                    int pageInd=pageNames.indexOf("``");
		                                    if(pageInd!=-1) {
		                                        itemDesc=pageNames.substring(pageInd+2);
		                                    }
		                                    if ("attach".equalsIgnoreCase(itemid)) {
		                                        if(StringUtils.isEmpty(itemDesc)) {
		                                        	FieldSet set_vo=DataDictionary.getFieldSetVo(setDomain.getSetname().toLowerCase());
		    			    						if(set_vo!=null) {
                                                        itemDesc=set_vo.getFieldsetdesc();
                                                    } else {
                                                        itemDesc=item.getItemdesc();//bug 31468
                                                    }
		                                            
		                                        }
		                                    }
		                                    else {
		                                        itemDesc=StringUtils.isEmpty(itemDesc)?item.getItemdesc():itemDesc;
		                                    }
		                                    set.add(itemDesc + "：" + fieldformat.getTitle());
					    				}
					    				
					    			}
					    		}
							}
							
							if(set.size()>0)
							{
								StringBuffer temp_error=new StringBuffer();
                                for (Iterator t = set.iterator(); t.hasNext();) {
                                    temp_error.append("[");
                                    temp_error.append((String) t.next());
                                    temp_error.append("]、");
                                }
                                String pageNames=getPageNameByItem(tabid+"",field_name,"");
                                int pageInd=pageNames.indexOf("``");
                                if(pageInd!=-1) {
                                    pageNames=pageNames.substring(0,pageInd-1);
                                }else if(pageNames.indexOf(",")!=-1) {
                                    pageNames=pageNames.substring(0,pageNames.length()-1);
                                }
                                if(errorPageNameMap.containsKey(pageNames)) {
                                    errorPageNameMap.put(pageNames, errorPageNameMap.get(pageNames)+temp_error.toString());
                                }else {
                                    errorPageNameMap.put(pageNames, temp_error.toString());
                                }
							}
							
						}
						//子集无数据 但是有必填项也需要提示出来 20170418 wangrd
						if (!hasRecord&&"true".equals(mustfillrecord)){
							for(int e=0;e<fieldfmtlist.size();e++)
				    		{
				    			fieldformat=(TFieldFormat)fieldfmtlist.get(e);
				    			String name=fieldformat.getName().toLowerCase();
								FieldItem item_=DataDictionary.getFieldItem(name);
								//判断子集的指标权限
				    			String a_state="2";
								if(item_!=null){
									if ("1".equals(this.UnrestrictedMenuPriv_Input)){
										a_state="2";
					                }else{
					                	a_state=userView.analyseFieldPriv(item_.getItemid());
					                }
					                if(fieldMap!=null&&fieldMap.get(fieldname)!=null){
			                			//if (task_id!=0) {// 如果不是发起人的话,那么就要判断节点的读写权限
			                			a_state = ((String)fieldMap.get(fieldname)).toLowerCase();
			                			//}
				                	}
					                if("1".equals(userView.getHm().get("fillInfo"))) {
                                        a_state="2";
                                    }
								}
				    			if(fieldformat.isBneed()&&"2".equals(a_state))
				    			{
				    				String itemid=fieldformat.getName().toLowerCase();
				    				String itemDesc="";
                                    String pageNames=getPageNameByItem(tabid+"",field_name,"");
                                    int pageInd=pageNames.indexOf("``");
                                    if(pageInd!=-1) {
                                        itemDesc=pageNames.substring(pageInd+2);
                                    }
			    					if("attach".equalsIgnoreCase(itemid))
			    					{
			    						FieldSet set_vo=DataDictionary.getFieldSetVo(setDomain.getSetname().toLowerCase());
			    						if(set_vo!=null) {
                                            itemDesc=set_vo.getFieldsetdesc();
                                        } else {
                                            itemDesc=item.getItemdesc();//bug 31468
                                        }
			    					}
			    					else{
                                        itemDesc=StringUtils.isEmpty(itemDesc)?item.getItemdesc():itemDesc;
                                    }
			    					set.add(itemDesc + "：" + fieldformat.getTitle());
			    				}
				    		}
							if(set.size()>0)
							{
								StringBuffer temp_error=new StringBuffer();
                                for (Iterator t = set.iterator(); t.hasNext();) {
                                    temp_error.append("[");
                                    temp_error.append((String) t.next());
                                    temp_error.append("]、");
                                }
                                String pageNames=getPageNameByItem(tabid+"",field_name,"");
                                int pageInd=pageNames.indexOf("``");
                                if(pageInd!=-1) {
                                    pageNames=pageNames.substring(0,pageInd-1);
                                }else if(pageNames.indexOf(",")!=-1) {
                                    pageNames=pageNames.substring(0,pageNames.length()-1);
                                }
                                if(errorPageNameMap.containsKey(pageNames)) {
                                    errorPageNameMap.put(pageNames, errorPageNameMap.get(pageNames)+temp_error.toString());
                                }else {
                                    errorPageNameMap.put(pageNames, temp_error.toString());
                                }
                            } else {
                                String itemDesc=item.getItemdesc();
                                String pageNames=getPageNameByItem(tabid+"",field_name,itemDesc);
                                int pageInd=pageNames.indexOf("``");
                                if(pageInd!=-1) {
                                    itemDesc=pageNames.substring(pageInd+2);
                                    pageNames=pageNames.substring(0,pageInd-1);
                                }else if(pageNames.indexOf(",")!=-1) {
                                    pageNames=pageNames.substring(0,pageNames.length()-1);
                                }
                                StringBuffer error_info_sub = new StringBuffer("");
                                error_info_sub.append("[");
                                error_info_sub.append(itemDesc);
                                error_info_sub.append("]、");
                                if(errorPageNameMap.containsKey(pageNames)) {
                                    errorPageNameMap.put(pageNames, errorPageNameMap.get(pageNames)+error_info_sub.toString());
                                }else {
                                    errorPageNameMap.put(pageNames, error_info_sub.toString());
                                }
							}
						}
					
					}
					
					int i=1;
	                for(Map.Entry<String, String> entry:errorPageNameMap.entrySet()) {
	                    String val=entry.getValue();
	                    bNotFill = true;
	                    //23`基本信息,24`基本信息
	                    String pageInfo=entry.getKey();
	                    String pageDesc="";
	                    String[] pageArr=pageInfo.split(",");
	                    for(int m=0;m<pageArr.length;m++) {
	                        String pageOne=pageArr[m];
	                        if(pageOne.indexOf("`")!=-1) {
	                            String pageId=pageOne.split("`")[0];
	                            pageDesc+=pageOne.split("`")[1]+",";
	                        }
	                    }
	                    val=val.substring(0, val.length()-1);
	                    pageDesc=StringUtils.isEmpty(pageDesc)?"":pageDesc.substring(0, pageDesc.length()-1);
	                    strInfo.append(i+"、");
	                    strInfo.append(pageDesc+" 页的"+val+" 信息未填写");
	                    strInfo.append("\n\r<br>");
	                    i++;
	                }
				}//for while end.
				if(bNotFill)
				{
					throw new GeneralException(strInfo.toString());
				}
			}
			StringBuffer sql=new StringBuffer("");
			sql.append("select * from ");
			sql.append(tablename);
			if(task_id!=0)
			{
				/*
					sql.append(" where task_id=");
					sql.append(task_id);
					sql.append(" and submitflag=1 ");
				*/	
					sql.append(" where  exists (select null from t_wf_task_objlink where templet_"+this.tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+this.tabid+".ins_id=t_wf_task_objlink.ins_id ");
					sql.append("  and task_id="+task_id+" and tab_id="+this.tabid+"  and submitflag=1  and (state is null or  state=0 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+userView.getUserName().toLowerCase()+"' or lower(username)='"+userView.getDbname().toLowerCase()+userView.getA0100()+"' ) ) )) ");	
					
			}	
			else
			{
					sql.append(" where ");
					if(isBEmploy())//员工通过自助平台发动申请
					{
						sql.append(" a0100='"+userView.getA0100()+"' and lower(basepre)='"+userView.getDbname().toLowerCase()+"'");
					}
					else
					{
						
						sql.append(" submitflag=1 ");
					}
			}		
			
			if(operationtype!=0&&operationtype!=5)//不是人员调入型&&不是 新建（单位|部门|岗位）
			{
				String _sql=sql.toString();
				if(this.infor_type==1)
				{
					_sql=_sql.replaceAll("\\*", "distinct lower(basepre) basepre ");
					rset=dao.search(_sql); 
					while(rset.next())
					{
						String basepre=rset.getString("basepre");
						rset2=dao.search(sql+" and lower(basepre)='"+basepre+"' and not exists (select null from "+basepre+"a01 where "+basepre+"a01.a0100="+tablename+".a0100 )");
						StringBuffer noExitMen=new StringBuffer("");
						while(rset2.next())
						{
							String a0101=rset2.getString("a0101_1")==null?"":rset2.getString("a0101_1");
							noExitMen.append(","+a0101);
						}
						if(noExitMen.length()>0)
						{
							throw new GeneralException(noExitMen.substring(1)+" 已移库,请删除当前记录。");
						}	
					}
				}
				else
				{
					
					String join="e01a1";
					if(this.infor_type==2) {
                        join="b0110";
                    }
					StringBuffer noExitMen=new StringBuffer("");
					String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
					String _str=" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ";
					rset2=dao.search(sql+" and    not exists (select null from organization where  organization.codeitemid="+tablename+"."+join+" "+_str+" ) ");
					while(rset2.next())
					{
					    if(operationtype==8){//如果是合并
					        String combineb0110="";
					        String combinetoid="";
					        if(this.infor_type==3){
					            combineb0110=rset2.getString("e01a1");
					        }else if(this.infor_type==2){
					            combineb0110=rset2.getString("b0110");
					        }
					        combinetoid=rset2.getString("to_id");
					        if(!"".equals(combineb0110)&&!"".equals(combinetoid)&&combineb0110.equals(combinetoid)){
					            continue;
					        }
					    }
						noExitMen.append(",");
						noExitMen.append(rset2.getString("codeitemdesc_1")==null?"":rset2.getString("codeitemdesc_1"));
					}
					if(noExitMen.length()>0)
					{
						throw new GeneralException(noExitMen.substring(1)+" 已移库,请删除当前记录。");
					}
					
				}
			}
			
			if(this.infor_type==1) {
                validateOnlyValue(sql.toString(),tablename);
            }
			if(this.infor_type!=1) {
                checkNewOrgFillItem(sql.toString(),this.operationtype);
            }
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		finally
		{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
				if(rset2!=null) {
                    rset2.close();
                }
			}
			catch(Exception ee)
			{
				
			}
			
		}
	}
	
	public String getPageNameByItem(String tabId, String fieldName,String returnItemDesc) {
        String pageNames="";
        if(StringUtils.isEmpty(fieldName)) {
            return pageNames;
        }
        try {
            //当前节点审批人与操作人不是同一个人 可认为是查看阶段 应该走当前操作人模板页权限，不应该走当前审批节点权限
            String structJson = "";
            DownAttachUtils attachUtils=new DownAttachUtils(userView, conn, tabid+"");
            structJson = attachUtils.getHtmlJsoninfo();
            if(StringUtils.isNotBlank(structJson)) {
                JsonObject jsonObject = new JsonParser().parse(structJson).getAsJsonObject();
                JsonArray pageArray = jsonObject.getAsJsonArray("pages");
                for(int i=0;i<pageArray.size();i++) {
                    JsonObject pageJson=pageArray.get(i).getAsJsonObject();
                    JsonArray layout = pageJson.getAsJsonArray("layout");
                    String pageDesc = pageJson.get("page_desc").getAsString();
                    String pageId = pageJson.get("page_id").getAsString();
                    for(int j=0;j<layout.size();j++) {
                        JsonObject layoutJson=layout.get(j).getAsJsonObject();
                        JsonArray content = layoutJson.getAsJsonArray("content");
                        for(int k1=0;k1<content.size();k1++) {
                            JsonObject contentJson = content.get(k1).getAsJsonObject();
                            if(contentJson.get("element_id")!=null&&fieldName.equalsIgnoreCase(contentJson.get("element_id").getAsString())&&((","+pageNames).indexOf(","+pageId+"`"+pageDesc+",")==-1)) {
                                pageNames+=pageId+"`"+pageDesc+",";
                                returnItemDesc=contentJson.get("label").getAsString();
                                if(StringUtils.isEmpty(returnItemDesc)&&contentJson.has("label_hz")) {
                                    returnItemDesc=contentJson.get("label_hz").getAsString();
                                }
                            }
                        }
                    }
                }
            }
            if(StringUtils.isNotEmpty(pageNames)&&StringUtils.isNotEmpty(returnItemDesc)) {
                pageNames=pageNames+"``"+returnItemDesc;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return pageNames;
    }

	/**
	 * 获得节点定义的指标必填项，变化后指标，无读值为0，写值为2，写并且必填值3
	 * @param task_id
	 * @return
	 */
	public HashMap getFieldPrivFillable(String task_id,Connection conn)
	{
		HashMap _map=new HashMap();
		Document doc=null;
		Element element=null;
		try
		{
			if(task_id!=null)
			{
				ContentDAO dao=new ContentDAO(conn);
				String sql="";
				if(!"0".equalsIgnoreCase(task_id)){
					sql="select ext_param from t_wf_node where node_id=(select node_id from t_wf_task where task_id="+task_id+" )";
				}else{//如果是起始节点，通过tabid和nodeType查找节点
					sql="select ext_param from t_wf_node where tabid='"+this.tabid+"' and nodeType=1";
				}
				RowSet rowSet=dao.search(sql);
				if(rowSet.next())
				{
					String ext_param= Sql_switcher.readMemo(rowSet,"ext_param"); 
					if(ext_param!=null&&ext_param.trim().length()>0)
					{
						doc=PubFunc.generateDom(ext_param);; 
						String xpath="/params/field_priv/field";
						XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						List childlist=findPath.selectNodes(doc);	
						if(childlist.size()==0){
							xpath="/params/field_priv/field";
							 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							 childlist=findPath.selectNodes(doc);
						}
						if(childlist!=null&&childlist.size()>0)
						{
							for(int i=0;i<childlist.size();i++)
							{
								element=(Element)childlist.get(i);
								String editable="";
								//0|1|2(无|读|写)
								if(element!=null&&element.getAttributeValue("editable")!=null) {
                                    editable=element.getAttributeValue("editable");
                                }
								if(editable!=null&&editable.trim().length()>0)
								{
									String columnname=element.getAttributeValue("name").toLowerCase();
									if(columnname.endsWith("_2")|| columnname.startsWith("s_")||"photo".equals(columnname)||"attachment".equals(columnname) ){
										if("1".equals(editable)) {
                                            editable="0";
                                        }
										String fillable = element.getAttributeValue("fillable");
										if("2".equals(editable)&&fillable!=null&& "true".equalsIgnoreCase(fillable)) {
                                            editable="3";
                                        }
										_map.put(columnname, editable);
									}
									
								}
								
							}
						}
					}
				}
				PubFunc.closeDbObj(rowSet);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return _map;
	}
	
	/**
	 * 获得节点定义的指标权限
	 * @param task_id
	 * @return
	 */
	public HashMap getFieldPriv(String task_id,Connection conn)
	{
		HashMap _map=new HashMap();
		Document doc=null;
		Element element=null;
		try
		{
			if(task_id!=null)
			{
				ContentDAO dao=new ContentDAO(conn);
				String sql="";
				if(!"0".equalsIgnoreCase(task_id)){
					sql="select * from t_wf_node where node_id=(select node_id from t_wf_task where task_id="+task_id+" )";
				}else{
					sql="select * from t_wf_node where tabid='"+this.tabid+"' and nodeType=1";
				}
				RowSet rowSet=dao.search(sql);
				if(rowSet.next())
				{
					String ext_param= Sql_switcher.readMemo(rowSet,"ext_param"); 
					if(ext_param!=null&&ext_param.trim().length()>0)
					{
					doc=PubFunc.generateDom(ext_param);; 
					String xpath="/params/field_priv/field";
					XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
					List childlist=findPath.selectNodes(doc);
					if(childlist.size()==0){
						xpath="/params/field_priv/field";
						 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						 childlist=findPath.selectNodes(doc);
					}
					if(childlist!=null&&childlist.size()>0)
					{
						for(int i=0;i<childlist.size();i++)
						{
							element=(Element)childlist.get(i);
							String editable="";
							//0|1|2(无|读|写)
							if(element!=null&&element.getAttributeValue("editable")!=null) {
                                editable=element.getAttributeValue("editable");
                            }
							if(editable!=null&&editable.trim().length()>0)
							{
								String columnname=element.getAttributeValue("name").toLowerCase();
								_map.put(columnname, editable);
							}
							
						}
					  }
					}
					
				}
				PubFunc.closeDbObj(rowSet);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return _map;
	}
	
	private ArrayList checkAttachMustFill(int ins_id, FieldItem item, String basepre, String a0100) {
		ArrayList recordList = new ArrayList();
		StringBuffer sb = new StringBuffer("");
		String username = userView.getUserName();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		RowSet frowset=null;
		try{
			if(isBEmploy()&&userView.getStatus()==0&&StringUtils.isNotBlank(userView.getDbname())&&StringUtils.isNotBlank(userView.getA0100())){
				DbNameBo db = new DbNameBo(this.conn);
				String loginNameField = db.getLogonUserNameField();
				String usernameSele="";
				if(StringUtils.isNotBlank(loginNameField)) {
					loginNameField = loginNameField.toLowerCase();
					String sql="select "+loginNameField+" as username from "+userView.getDbname()+"A01 where a0100='"+userView.getA0100()+"' ";
					rowSet=dao.search(sql);
					while(rowSet.next()){
						usernameSele=rowSet.getString("username");
					}
					if(StringUtils.isNotBlank(usernameSele)){
						username=usernameSele;
					}
				}
			}
			if(ins_id!=0){//进入了审批流
				if(userView.getVersion()>=70){
					if("attachment_0".equals(item.getItemid())){//公共附件
						sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
						sb.append(ins_id);
						sb.append(" and t.tabid=");
						sb.append(tabid);
						sb.append(" and t.state<>1 ");
						sb.append(" and (t.attachmenttype=0 or t.attachmenttype is null) ");
					}else if("attachment_1".equals(item.getItemid())&&a0100.length()>0){//个人附件
						sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
						sb.append(ins_id);
						sb.append(" and t.tabid=");
						sb.append(tabid);
						sb.append(" and t.state<>1 ");
						sb.append(" and t.attachmenttype=1");
						sb.append(" and t.objectid='");
						sb.append(a0100);
						sb.append("'");
						if(StringUtils.isNotBlank(basepre)){//infor_type=1
							sb.append(" and t.basepre='");
							sb.append(basepre);
							sb.append("'");
						}
						TemplateParam param = new TemplateParam(this.conn,userView,Integer.valueOf(tabid));
						String sub_domain=item.getFormula();
						if(StringUtils.isNotBlank(sub_domain)){
							Document doc=null;
							Element element=null;
							doc=PubFunc.generateDom(sub_domain);;
							String xpath="/sub_para/para";
							XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							List childlist=findPath.selectNodes(doc);	
							if(childlist!=null&&childlist.size()>0)
							{
								element=(Element) childlist.get(0);
								String file_type=(String)element.getAttributeValue("file_type");
								if(StringUtils.isNotBlank(file_type)){
									sb.append(" and m.flag='"+file_type+"'");
								}
							}
						}
					}else{
						sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
						sb.append(ins_id);
						sb.append(" and t.tabid=");
						sb.append(tabid);
						sb.append(" and t.state<>1 ");
						sb.append(" and (t.attachmenttype=0 or t.attachmenttype is null) ");
					}
				}
			}else{//还未进入审批流
				if(userView.getVersion()>=70){
					if("attachment_0".equals(item.getItemid())){//公共附件
						sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
						sb.append(ins_id);
						sb.append(" and t.tabid=");
						sb.append(tabid);
						sb.append(" and t.state<>1 ");
						sb.append(" and (t.attachmenttype=0 or t.attachmenttype is null)");
						sb.append(" and t.create_user='");
						sb.append(username);
						sb.append("' ");
					}else if("attachment_1".equals(item.getItemid())&&a0100.length()>0){//个人附件
						sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
						sb.append(ins_id);
						sb.append(" and t.tabid=");
						sb.append(tabid);
						sb.append(" and t.state<>1 ");
						sb.append(" and t.attachmenttype=1");
						sb.append(" and t.create_user='");
						sb.append(username);
						sb.append("' and t.objectid='");
						sb.append(a0100);
						sb.append("'");
						
						if(StringUtils.isNotBlank(basepre)){//infor_type=1
							sb.append(" and t.basepre='");
							sb.append(basepre);
							sb.append("'");
						}
						String sub_domain=item.getFormula();
						if(StringUtils.isNotBlank(sub_domain)){
							Document doc=null;
							Element element=null;
							doc=PubFunc.generateDom(sub_domain);;
							String xpath="/sub_para/para";
							XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							List childlist=findPath.selectNodes(doc);	
							if(childlist!=null&&childlist.size()>0)
							{
								element=(Element) childlist.get(0);
								String file_type=(String)element.getAttributeValue("file_type");
								if(StringUtils.isNotBlank(file_type)){
									sb.append(" and m.flag='"+file_type+"'");
								}
							}
						}
					}
				}else{
					sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
					sb.append(ins_id);
					sb.append(" and t.tabid=");
					sb.append(tabid);
					sb.append(" and (t.attachmenttype=0 or t.attachmenttype is null)");
					sb.append(" and t.create_user='");
					sb.append(username);
					sb.append("' ");
				}
			}
			if(sb.length()>0){
				sb.append(" order by file_id");
				frowset = dao.search(sb.toString());
				while (frowset.next()) {
					HashMap map = new HashMap();
					map.put("attachmentname", frowset.getString("name"));
					if("attachment_1".equals(item.getItemid())) {
                        map.put("sortname", frowset.getString("sortname"));
                    }
					Date d_create=frowset.getDate("create_time");
					String d_str=DateUtils.format(d_create,"yyyy.MM.dd");
					map.put("create_time", d_str);
					String name = frowset.getString("fullname");
					String user_name = frowset.getString("create_user");//下载不要
					if(StringUtils.isBlank(name)) {
                        name = user_name;
                    }
					map.put("fullname", name);
					recordList.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(frowset);
			PubFunc.closeDbObj(rowSet);
		}
		return recordList;
	}
	/**验证唯一性 */
	public void validateOnlyValue(String sql,String tabName)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
            Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
    		String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name"); //身份证指标
    		chk=chk!=null?chk:"";
    		String id_type = sysbo.getValue(Sys_Oth_Parameter.CHK_IdTYPE);//证件类型
    		id_type=id_type!=null?id_type:"";
    		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name"); //验证唯一性指标
    		onlyname=onlyname!=null?onlyname:"";
    		String chkvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","valid");//身份证验证是否启用
    		chkvalid=chkvalid!=null?chkvalid:"";	
    		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//唯一性验证是否启用
    		uniquenessvalid=uniquenessvalid!=null?uniquenessvalid:"";
    		String dbchk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","db");//验证身份证适用的人员库
    		dbchk=dbchk!=null?dbchk:"";
    		String dbonly = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","db");//验证唯一性适用的人员库
    		dbonly=dbonly!=null?dbonly:"";
    		DbNameBo dbnamebo = new DbNameBo(this.conn);
			
    		RecordVo vo=new RecordVo(tabName);
    		ArrayList attris=vo.getModelAttrs();
    		HashMap att_map=new HashMap();
    		for(int i=0;i<attris.size();i++)
    		{
    			att_map.put(((String)attris.get(i)).toLowerCase(),"1");
    		}
    		
    		String chkvalid_value="";
    		String uniqueness_value="";
    		//校验身份证
    		if("1".equals(chkvalid)&&chk.length()>0){
				FieldItem fieldItem=DataDictionary.getFieldItem(chk);
				String itemid=fieldItem.getItemid().toLowerCase()+"_2";
				if(fieldItem!=null&&att_map.get(itemid)!=null)
				{
					String sql0=sql.replaceFirst("\\*", "count(*) num,"+itemid);
					if(sql0.toLowerCase().indexOf("where")!=-1)
					{
					//	sql0+=" and "+itemid+" is not null and "+itemid+"<>'' group by "+itemid+" having count(*) >1";
						sql0+=" and nullif("+itemid+",'')  is not null   group by "+itemid+" having count(*) >1";
					}
					else
					{
					//	sql0+=" where "+itemid+" is not null and "+itemid+"<>'' group by "+itemid+" having count(*) >1";
						sql0+=" where nullif("+itemid+",'')  is not null   group by "+itemid+" having count(*) >1";
					}
					RowSet rowSet2=dao.search(sql0);
					if(rowSet2.next())
					{
						chkvalid_value=rowSet2.getString(2);
					}
					PubFunc.closeDbObj(rowSet2);
				}
    		}
    		//校验指标唯一性
    		if("1".equals(uniquenessvalid)&&onlyname.length()>0){
				
				FieldItem fieldItem=DataDictionary.getFieldItem(onlyname);
				
				if(fieldItem!=null)
				{
					String itemid=fieldItem.getItemid().toLowerCase()+"_2";
					if(att_map.get(itemid)!=null){
					String sql0=sql.replaceFirst("\\*", "count(*) num,"+itemid);
					if(sql0.toLowerCase().indexOf("where")!=-1)
					{
					//	sql0+=" and "+itemid+" is not null and "+itemid+"<>'' group by "+itemid+" having count(*) >1";
						sql0+=" and nullif("+itemid+",'')  is not null   group by "+itemid+" having count(*) >1";
					}
					else
					{
					//	sql0+=" where "+itemid+" is not null and "+itemid+"<>'' group by "+itemid+" having count(*) >1";
						sql0+=" where nullif("+itemid+",'')  is not null   group by "+itemid+" having count(*) >1";
					}
					RowSet rowSet2=dao.search(sql0);
					if(rowSet2.next())
					{
						uniqueness_value=rowSet2.getString(2);
					}
					PubFunc.closeDbObj(rowSet2);
				}
				}
    		}
    		
    		
    		
    		
    		RowSet rset=dao.search(sql);
    		while(rset.next())
			{
				String a0100=rset.getString("a0100");
				String a0101=rset.getString("a0101_1");
				/**身份证校验规则：
				 * 1、设置了证件类型指标，如果选择类型不是1或01，不校验身份证规则和唯一性。如果选择类型是1或01，校验身份证规则，如果启用唯一性校验，身份证号校验唯一性。
				 * 2、没设置证件类型指标、设置了身份证指标，没启用唯一性校验，校验身份证规则。不校验唯一性
				 * 3、没设置证件类型指标、设置了身份证指标，启用唯一性校验，校验身份证规则。校验唯一性
				 */
				Boolean isCheckID=true;
				if(StringUtils.isNotBlank(id_type)){
					FieldItem fieldItem=DataDictionary.getFieldItem(id_type);
					if(fieldItem!=null&&att_map.get(fieldItem.getItemid().toLowerCase()+"_2")!=null){
						String id_typeValue= rset.getString(fieldItem.getItemid().toLowerCase()+"_2");
						if(!("1".equalsIgnoreCase(id_typeValue)||"01".equalsIgnoreCase(id_typeValue))){
							isCheckID=false;
						}
					}else if(fieldItem!=null&&att_map.get(fieldItem.getItemid().toLowerCase()+"_1")!=null){
						String id_typeValue=rset.getString(fieldItem.getItemid().toLowerCase()+"_1");
						if(!("1".equalsIgnoreCase(id_typeValue)||"01".equalsIgnoreCase(id_typeValue))){
							isCheckID=false;
						}
					}else{
						isCheckID=false;
					}
				}
				if(StringUtils.isNotBlank(chk)){//是否设置了证件指标
					FieldItem fieldItem=DataDictionary.getFieldItem(chk);
					if(fieldItem!=null&&att_map.get(fieldItem.getItemid().toLowerCase()+"_2")!=null)
					{
						if(isCheckID){//是否需要检查身份证规则
						if(rset.getString(fieldItem.getItemid().toLowerCase()+"_2")!=null&&rset.getString(fieldItem.getItemid().toLowerCase()+"_2").length()>0)
						{
							if(rset.getString(fieldItem.getItemid().toLowerCase()+"_2").length()!=15&&rset.getString(fieldItem.getItemid().toLowerCase()+"_2").length()!=18)
							{
								throw new GeneralException(a0101+" "+fieldItem.getItemdesc()+"填写不正确!");
							}
							String info=dbnamebo.checkID(rset.getString(fieldItem.getItemid().toLowerCase()+"_2").trim());
							if(info.length()>0) {
                                throw new GeneralException(a0101+" "+fieldItem.getItemdesc()+"填写不正确!");
                            }
							}
						}
						if("1".equals(chkvalid)){//是否需要校验唯一性
							if(chkvalid_value.length()>0&&chkvalid_value.equalsIgnoreCase(rset.getString(fieldItem.getItemid().toLowerCase()+"_2"))) {
                                throw new GeneralException(a0101+" "+fieldItem.getItemdesc()+"不唯一!");
                            }
							String onlynameflag = dbnamebo.checkOnlyName(dbchk,fieldItem.getFieldsetid(),fieldItem.getItemid(),rset.getString(fieldItem.getItemid().toLowerCase()+"_2"),a0100);
							if(!"true".equalsIgnoreCase(onlynameflag)) {
                                throw new GeneralException(a0101+" "+onlynameflag);
                            } else{
								onlynameflag = dbnamebo.checkOnlyName(dbchk,fieldItem.getFieldsetid(),fieldItem.getItemid()
										,dbnamebo.changeCardID(rset.getString(fieldItem.getItemid().toLowerCase()+"_2"),""),a0100);
								if(!"true".equalsIgnoreCase(onlynameflag)) {
                                    throw new GeneralException(a0101+" "+onlynameflag);
                                }
							}
						}
					}
				}
				if("1".equals(uniquenessvalid)){
				
					FieldItem fieldItem=DataDictionary.getFieldItem(onlyname);
					if(fieldItem!=null&&att_map.get(fieldItem.getItemid().toLowerCase()+"_2")!=null)
					{
						if(rset.getString(fieldItem.getItemid().toLowerCase()+"_2")!=null&&rset.getString(fieldItem.getItemid().toLowerCase()+"_2").length()>0)
						{
							if(uniqueness_value.length()>0&&uniqueness_value.equalsIgnoreCase(rset.getString(fieldItem.getItemid().toLowerCase()+"_2"))) {
                                throw new GeneralException(a0101+" "+fieldItem.getItemdesc()+"不唯一!");
                            }
							
							//wangrd  2015-07-06 增加逻辑只有目标库在需要校验的范围内，才校验
							if (this.dest_base!=null &&this.dest_base.length()>0 && dbonly.toUpperCase().indexOf(this.dest_base.toUpperCase())!=-1){
								String onlynameflag = dbnamebo.checkOnlyName(dbonly,fieldItem.getFieldsetid(),fieldItem.getItemid(),rset.getString(fieldItem.getItemid().toLowerCase()+"_2"),a0100);
								if(!"true".equalsIgnoreCase(onlynameflag)) {
                                    throw new GeneralException(a0101+" "+onlynameflag);
                                }
							}
						}
					}
					else {//如果没维护身份证的话，则去档案库中查找身份证号 wangrd 2015-07-08
						if(operationtype==1 || operationtype==2 || operationtype==2){//调出、离退 系统内
							String srcBase=rset.getString("basepre");
							//目标库不等于源库，且目标库需要校验
							if (this.dest_base!=null && !this.dest_base.equalsIgnoreCase(srcBase) && dbonly.toUpperCase().indexOf(this.dest_base.toUpperCase())!=-1){
								String tabname = srcBase+"A01";
								RecordVo personVo=new RecordVo(tabname); 
								personVo.setString("a0100", a0100);
								personVo= dao.findByPrimaryKey(personVo);
								String uniqueValue=personVo.getString(fieldItem.getItemid().toLowerCase()) ;
								//排除源库
								String onlynameflag = dbnamebo.checkOnlyName(dbonly,fieldItem.getFieldsetid(),fieldItem.getItemid(),uniqueValue,a0100,srcBase);
								if(!"true".equalsIgnoreCase(onlynameflag)) {
                                    throw new GeneralException(a0101+" "+onlynameflag);
                                }
								
							}
							
						}
					}
				}
				
			}
    		PubFunc.closeDbObj(rset);
		}
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	/**
	 * 校验新建组织单元模板系统指标是否建立，及内容是否有填写
	 * @param sql
	 * @throws GeneralException
	 */
	public void  checkNewOrgFillItem(String sql,int _operationType)throws GeneralException
	{
		RowSet rowSet=null; 
		RowSet rowSet2=null;
		RowSet rowSet3=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			 rowSet=dao.search(sql); 
			 rowSet2=null; 
			 ResultSetMetaData   rsmd = rowSet.getMetaData();
			 HashMap columnMap=new HashMap();
			 String codename=",";
				int columnCount  =  rsmd.getColumnCount();//得到列数 
					String[] temp3=new String[columnCount];
					for(int i=0;i<temp3.length;i++)
					{
							codename+=rsmd.getColumnName(i+1).trim().toLowerCase()+",";
							columnMap.put(rsmd.getColumnName(i+1).trim().toLowerCase(), "1");
					}
					
			validateSysItem(columnMap);		
			StringBuffer strInfo=new StringBuffer();//存储为空的信息
			strInfo.append("下列信息不能为空,请填写完整!\n\r");
			StringBuffer strInfo2=new StringBuffer();//存储有问题的信息
			strInfo2.append("下列信息填写有问题!\n\r");
			StringBuffer errorInfo=new StringBuffer("");
			StringBuffer errorInfo2=new StringBuffer("");
			boolean flag=false;
			HashMap map = getBKmap();
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			while(rowSet.next())
			{
				errorInfo2.setLength(0);
				String codeitemdesc="";
				if(_operationType==5) {
                    codeitemdesc=rowSet.getString("codeitemdesc_2");
                } else {
                    codeitemdesc=rowSet.getString("codeitemdesc_1");
                }
				errorInfo.setLength(0);
				 
				if(this.infor_type==2&&_operationType==5)//如果是单位
				{
					if(rowSet.getString("codesetid_2")==null||rowSet.getString("codesetid_2").trim().length()==0) {
                        errorInfo.append("拟[组织单元类型]不能为空,");
                    }
				}
				if(this.infor_type==2&&_operationType==5)//xgq
				{
					if(rowSet.getString("codeitemdesc_2")==null||rowSet.getString("codeitemdesc_2").trim().length()==0|| "--".equals(rowSet.getString("codeitemdesc_2").trim())) {
                        errorInfo.append("拟[组织单元名称]不能为空,");
                    }
				}
				if(this.infor_type==3&&_operationType==5)//如果是岗位
				{
					if(rowSet.getString("codeitemdesc_2")==null||rowSet.getString("codeitemdesc_2").trim().length()==0|| "--".equals(rowSet.getString("codeitemdesc_2").trim())) {
                        errorInfo.append("拟[职务名称]不能为空,");
                    }
				}
				if((_operationType==5)&&(rowSet.getString("parentid_2")==null||rowSet.getString("parentid_2").trim().length()==0)){
					if(this.infor_type==2&&rowSet.getString("codesetid_2")!=null&& "UN".equals(rowSet.getString("codesetid_2"))){//只有(新建)单位才能在组织机构下建
						
					}else {
                        errorInfo.append("拟[上级组织单元]不能为空,");
                    }
				}
				if(_operationType!=6&&_operationType!=10)
				{
					if(rowSet.getDate("start_date_2")==null||rowSet.getDate("start_date_2").toString().trim().length()==0) {
                        errorInfo.append("拟[生效日期]不能为空,");
                    }
				}	 
				
		/*		if(this.infor_type==2&&this.unit_code_field.length()>0&&_operationType==5)
				{
					if(rowSet.getString("corcode_2")==null||rowSet.getString("corcode_2").trim().length()==0)
						errorInfo.append("拟[单位代码]不能为空,");
				}
				else if(this.infor_type==3&&this.pos_code_field.length()>0&&_operationType==5)
				{
					if(rowSet.getString("corcode_2")==null||rowSet.getString("corcode_2").trim().length()==0)
						errorInfo.append("拟[岗位代码]不能为空,");
				}*/
				else if(_operationType==6&&codename.indexOf(",codeitemdesc_2,")!=-1)
				{
					if(rowSet.getString("codeitemdesc_2")==null||rowSet.getString("codeitemdesc_2").trim().length()==0|| "--".equals(rowSet.getString("codeitemdesc_2").trim()))
					{
						if(this.infor_type==2) {
                            errorInfo.append("拟[组织单元名称]不能为空,");
                        } else if(this.infor_type==3) {
                            errorInfo.append("拟[岗位名称]不能为空,");
                        }
						
						
					}
				}
				
				if(errorInfo.length()>0)
				{
					flag=true;
					strInfo.append(codeitemdesc+"  "+errorInfo.toString());
				}else{//单位不可挂在部门下
					if(this.infor_type==2&&(_operationType==5))//xgq
					{
						if(rowSet.getString("codesetid_2")!=null&& "UN".equals(rowSet.getString("codesetid_2"))&&rowSet.getString("parentid_2")!=null&&rowSet.getString("parentid_2").trim().length()>0){
							if(map!=null&&map.get(rowSet.getString("parentid_2"))!=null&& "UM".equals(map.get(rowSet.getString("parentid_2")))){
								flag=true;
								errorInfo2.append("的上级不能为部门,");
								strInfo2.append(codeitemdesc+"  "+errorInfo2.toString());
							}
						}
					}
					
					//您选择了有效日期起为当日的机构，不允许撤销||合并||划转
					 
					//生效日期不能小于开始日期
					if(this.infor_type!=1){
						String codeitemid ="";
						if(this.infor_type==2) {
                            codeitemid = rowSet.getString("B0110");
                        }
						if(this.infor_type==3) {
                            codeitemid = rowSet.getString("E01A1");
                        }
						 
						if(_operationType!=6&&_operationType!=10)
						{
							String start_date_2 = rowSet.getDate("start_date_2").toString();
							if(start_date_2.length()>=10){
							start_date_2=start_date_2.substring(0,10);
							}else{
								continue;
							}
							if(start_date_2.length()==10){
								StringBuffer ext_sql = new StringBuffer();
								Calendar d=Calendar.getInstance();
								String to_day=df.format(d.getTime());
								/*d.setTime(java.sql.Date.valueOf(start_date_2));
								int yy=d.get(Calendar.YEAR);
								int mm=d.get(Calendar.MONTH)+1;
								int dd=d.get(Calendar.DATE);*/
								
								ext_sql.append(PubFunc.getDateSql("<=","start_date",start_date_2));
								/*ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
								ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
								ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<"+dd+" ) ) ");*/	 			
								rowSet2=dao.search("select * from organization where codeitemid='"+codeitemid+"' ");
								boolean flagdate =false;
								String start_date="";
								if(rowSet2.next()){
									flagdate=true;
									if(rowSet2.getDate("start_date")!=null) {
                                        start_date=df.format(rowSet2.getDate("start_date"));
                                    }
								}
								if(flagdate){ 
									rowSet3=dao.search("select * from organization where codeitemid='"+codeitemid+"' "+ext_sql+"  "); 
									if(rowSet3.next()){
									}else{
										flag=true;
										errorInfo2.append("的生效日期需不小于创建日期:"+start_date+",");
										strInfo2.append(codeitemdesc+"  "+errorInfo2.toString());
									}
								}
								
								if(this.operationtype==7||this.operationtype==8||this.operationtype==9)
								{
									if(start_date.equalsIgnoreCase(to_day))
									{
										flag=true;
										errorInfo2.setLength(0);
										errorInfo2.append("不能 撤销|合并|划转 当天创建的机构,");
										if(strInfo2.length()>0){
										  strInfo2.append(errorInfo2.toString());
										}else{
										  strInfo2.append(codeitemdesc+"  "+errorInfo2.toString()); 
										}
                                        
										//errorInfo2.append("不能 撤销|合并|划转 当天创建的机构,");
										//strInfo2.append(codeitemdesc+"  "+errorInfo2.toString());
									}
								}
								
							}	
						}
						if(_operationType==6&&codename.indexOf(",codeitemdesc_2,")==-1){
							flag=true;
							strInfo2.setLength(0);
							if(this.infor_type==2) {
                                strInfo2.append("该模板不存在变化后组织单元名称!");
                            } else if(this.infor_type==3) {
                                strInfo2.append("该模板不存在变化后职位名称!");
                            }
							
						}
					}
					}
				
			}//while结束
			if(flag){
				if(strInfo.length()>20){
					
				}else{
					strInfo=strInfo2;	
				}
				strInfo.setLength(strInfo.length()-1);
				strInfo.append("!");//最后一个符号改为叹号  郭峰
				throw GeneralExceptionHandler.Handle(new Exception(strInfo.toString()));
			}
				
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally{
			try {
				if(rowSet!=null) {
                    rowSet.close();
                }
				if(rowSet2!=null) {
                    rowSet2.close();
                }
				if(rowSet3!=null) {
                    rowSet3.close();
                }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 验证组织模板是否缺少系统指标
	 * @param tableColumnMap
	 * @throws GeneralException
	 */
	public void validateSysItem(HashMap tableColumnMap)throws GeneralException
	{
		StringBuffer desc=new StringBuffer("");
		if(this.operationtype==5)
		{
			if(tableColumnMap.get("codeitemdesc_2")==null)
			{
				if(this.infor_type==2) {
                    desc.append("、组织单元名称");
                } else if(this.infor_type==3) {
                    desc.append("、岗位名称");
                }
			}
			if(tableColumnMap.get("codesetid_2")==null)
			{
				if(this.infor_type==2) {
                    desc.append("、组织单元类型");
                }
			}
			if(tableColumnMap.get("parentid_2")==null) {
                desc.append("、上级组织单元");
            }
			
			if(this.infor_type==2&&this.unit_code_field.length()>0)
			{
				if(tableColumnMap.get("corcode_2")==null) {
                    desc.append("、单位代码");
                }
			}
			else if(this.infor_type==3&&this.pos_code_field.length()>0)
			{
				if(tableColumnMap.get("corcode_2")==null) {
                    desc.append("、岗位代码");
                }
			}
			
		}
		if(this.operationtype==6)
		{
			if(tableColumnMap.get("codeitemdesc_2")==null)
			{
				if(this.infor_type==2) {
                    desc.append("、组织单元名称");
                } else if(this.infor_type==3) {
                    desc.append("、岗位名称");
                }
			}
		}
		if(this.infor_type==3&&this.operationtype==9) //如果为岗位划转模板
		{
			if(tableColumnMap.get("parentid_2")==null) {
                desc.append("、需划转的上级组织单元");
            }
		}
		
		if(this.operationtype!=6&&this.operationtype!=10&&tableColumnMap.get("start_date_2")==null) {
            desc.append("、变化后生效日期");
        }
		
		if(desc.length()>0) {
            throw new GeneralException("模板缺少 "+desc.substring(1)+" 指标定义,执行不成功!");
        }
	}
	
	/**
	 * 区分单位、部门、职位
	 * @param value
	 * @return
	 */
	public  HashMap  getBKmap()
	{
		HashMap map= new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=null;
			
				rowSet=dao.search("select codesetid,codeitemid from organization ");
				while(rowSet.next())
				{
					String codesetid=rowSet.getString("codesetid");
					String codeitemid=rowSet.getString("codeitemid");
					map.put(codeitemid, codesetid);
				}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	/**初始化数据*/
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
					if(pre.equalsIgnoreCase(this.dest_base)) {
                        this.dest_base=pre;
                    }
				}
		    }
			sxml=this.table_vo.getString("sp_flag");
			if(sxml==null|| "".equals(sxml)|| "0".equals(sxml)) {
                this.bsp_flag=false;
            } else {
                this.bsp_flag=true;
            }
				
			sxml=this.table_vo.getString("gzstandid");
			this.gz_stand=StringUtils.split(sxml,",");
			sxml=this.table_vo.getString("noticeid");	
			this.msg_template=StringUtils.split(sxml,",");
			this.content=this.table_vo.getString("content");
			this.name=this.table_vo.getString("name");
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				this._static=this.table_vo.getInt("static_o");
			}else {
				this._static=this.table_vo.getInt("static");
			}
			
			this.operationname=this.table_vo.getString("operationname");
			this.operationcode=this.table_vo.getString("operationcode");
			this.operationtype=findOperationType(operationcode);
			this.factor=this.table_vo.getString("factor");
			this.llexpr=this.table_vo.getString("llexpr");
			
			
			if(this._static==10) //单位
			{
				this.infor_type=2;
				RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.conn);
				if(unit_code_field_constant_vo!=null)
				{
					this.unit_code_field=unit_code_field_constant_vo.getString("str_value");
					if(this.unit_code_field==null||this.unit_code_field.trim().length()<2) {
                        this.unit_code_field="";
                    }
				}
			}
			if(this._static==11) //职位
			{
				this.infor_type=3;
				RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("POS_CODE_FIELD",this.conn);
				if(unit_code_field_constant_vo!=null)
				{
					this.pos_code_field=unit_code_field_constant_vo.getString("str_value");
					if(this.pos_code_field==null||this.pos_code_field.trim().length()<2) {
                        this.pos_code_field="";
                    }
				}
			}
			
			
		}
	}
	/**
	 * 查找业务类型 0,1,2,3,4,10
	 * 对人员调入，人员调出等业务对一些特殊的规则
	 * @param operationcode
	 * @return
	 */
	private int findOperationType(String operationcode)
	{
		int flag=TemplateStaticDataBo.getOperationType(operationcode, conn);
		return flag;		
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
		if(sxml==null|| "".equals(sxml)) {
            return false;
        }
		try
		{
			doc=PubFunc.generateDom(sxml);;
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
				this.out_type=(String)element.getAttributeValue("type");
				if(StringUtils.isEmpty(this.out_type)) {
					this.out_type="1";
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
			    if(element.getAttributeValue("include_suborg")!=null) {
                    this.include_suborg=(String)element.getAttributeValue("include_suborg");
                }
			    if(element.getAttributeValue("import_notice_data")!=null) {
                    this.import_notice_data=(String)element.getAttributeValue("import_notice_data");
                }
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
				if(element.getAttribute("opinion_field")!=null) {
                    this.opinion_field=(String)element.getAttributeValue("opinion_field");
                }
				if(this.sp_mode==1)
				{
					if(element.getAttributeValue("endusertype")!=null) {
                        this.endusertype=(String)element.getAttributeValue("endusertype");
                    }
					if(element.getAttributeValue("enduser")!=null) {
                        this.enduser=(String)element.getAttributeValue("enduser");
                    }
					
				}
				if(element.getAttributeValue("relation_id")!=null) {
                    this.Relation_id=(String)element.getAttributeValue("relation_id");
                }
				if(element.getAttributeValue("def_flow_self")!=null)
				{
					this.def_flow_self=(String)element.getAttributeValue("def_flow_self");
					if("1".equals(this.def_flow_self)) {
                        this.allow_defFlowSelf=true;
                    }
				}
				if(element.getAttributeValue("no_sp_yj")!=null){
				    this.no_sp_yj=(String)element.getAttributeValue("no_sp_yj");
				    
				}
				if(element.getAttributeValue("defaultselected")!=null) //defaultselected  : 1 或 无此属性时表示审批记录默认全选
                {
                    this.defaultselected=(String)element.getAttributeValue("defaultselected");
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
				if(element.getAttribute("email_staff")!=null) {
                    this.email_staff=(new Boolean((String)element.getAttributeValue("email_staff"))).booleanValue();
                }
				if(element.getAttribute("template_bos")!=null) {
                    this.template_bos=(String)element.getAttributeValue("template_bos");
                }
				if(element.getAttribute("template_staff")!=null) {
                    this.template_staff=(String)element.getAttributeValue("template_staff");
                }
				if(element.getAttribute("template_sp")!=null) {
                    this.template_sp=(String)element.getAttributeValue("template_sp");
                }
				
			}
			/** 原始表单是否归档 */
			
			xpath="/params/updates";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);	
			String DefaultTransIn="0";
			if(childlist!=null&&childlist.size()>0)
			{ 
				element=(Element)childlist.get(0);
				if(element.getAttribute("archflag")!=null) {
                    this.archflag=(String)element.getAttributeValue("archflag");
                }
				if(element.getAttribute("headCount_control")!=null) {
                    this.headCount_control=(String)element.getAttributeValue("headCount_control");
                }
				if(element.getAttribute("autocalc")!=null) {
                    this.autoCaculate=(String)element.getAttributeValue("autocalc"); //0不计算(默认值),1计算
                }
				if(element.getAttribute("spautocalc")!=null) {
                    this.spAutoCaculate=(String)element.getAttributeValue("spautocalc"); //0不计算(默认值),1计算
                }
				if(element.getAttribute("UnrestrictedMenuPriv")!=null) {
                    this.UnrestrictedMenuPriv=(String)element.getAttributeValue("UnrestrictedMenuPriv");
                }
				if(element.getAttribute("UnrestrictedMenuPriv_Input")!=null) {
                    this.UnrestrictedMenuPriv_Input=(String)element.getAttributeValue("UnrestrictedMenuPriv_Input");
                }
				if(element.getAttribute("unique_check")!=null) {
                    this.unique_check=(String)element.getAttributeValue("unique_check");
                }
				if(element.getAttribute("DefaultTransIn")!=null) {
                    DefaultTransIn=(String)element.getAttributeValue("DefaultTransIn");
                }
				if(element.getAttribute("id_gen_manual")!=null) {
                    this.id_gen_manual=(String)element.getAttributeValue("id_gen_manual");
                }
				if(element.getAttribute("filter_by_factor")!=null) {
                    this.filter_by_factor=(String)element.getAttributeValue("filter_by_factor");
                }
				if(element.getAttribute("no_priv_ctrl")!=null) {
                    this.no_priv_ctrl=(String)element.getAttributeValue("no_priv_ctrl");
                }
				
				
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
					
					if(element.getAttributeValue("SubMenu")!=null) {
                        subctrl.setSubMenu(((String)element.getAttributeValue("SubMenu")).trim());
                    }
					if(element.getAttributeValue("Type")!=null&&(Integer.parseInt(element.getAttributeValue("Type"))==3||Integer.parseInt(element.getAttributeValue("Type"))==4)&&element.getAttributeValue("CondFormula")!=null)//bug 36853 条件新增需要获取设置的条件。
                    {
                        subctrl.setCondFormula(((String)element.getAttributeValue("CondFormula")).trim());
                    } else {
                        subctrl.setCondFormula("");
                    }
					if("0".equals(DefaultTransIn))
					{
						if(element.getAttributeValue("SysTransType")!=null) {
                            subctrl.setInnerupdatetype(Integer.parseInt(element.getAttributeValue("SysTransType")));
                        } else
						{
							if("A01".equalsIgnoreCase((String)element.getAttributeValue("Name"))) {
                                subctrl.setInnerupdatetype(0);
                            } else {
                                subctrl.setInnerupdatetype(1);
                            }
						}
					}
					else {
                        subctrl.setInnerupdatetype(subctrl.getUpdatetype());
                    }
					if(element.getAttributeValue("RefPreRec")!=null) {
                        subctrl.setRefPreRec(Integer.parseInt(element.getAttributeValue("RefPreRec")));
                    } else {
                        subctrl.setRefPreRec(1);
                    }
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
							if(element.getAttributeValue("user")!=null&&element.getAttributeValue("user").trim().length()>0) {
                                user=element.getAttributeValue("user").trim();
                            }
							
							String type="4";
							if(element.getAttributeValue("type")!=null&&element.getAttributeValue("type").trim().length()>0) {
                                type=element.getAttributeValue("type");
                            }
							abean=new LazyDynaBean();
							abean.set("condid", element.getAttributeValue("condid"));
							abean.set("user", user);
							abean.set("type", type);
							
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
				if(element.getAttribute("view")!=null) {
                    this.view=(String)element.getAttributeValue("view");
                }
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
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
		this.setDoc(doc);
		return bflag;
	}
	
	/**
	 * 求模板中定义所有的指标(变化前及变化后)及变量的列表
	 * @param isMobile 0 非手机端模板  1：手机端模板 
	 * @return  列表中存放的是FieldItem对象
	 * @throws GeneralException
     * liuyz 30408 手机模版和非手机模版子集有相同子集造成子集必填判断不正确。 
	 */
	public ArrayList getAllFieldItem(String isMobile)throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			ArrayList pagelist=getAllTemplatePage();
			this.sub_domain_map =null;
			this.field_name_map = null;
			for(int i=0;i<pagelist.size();i++)
			{
				TemplatePageBo pagebo=(TemplatePageBo)pagelist.get(i); 
				if(isMobile!=null&&pagebo.getIsMobile()!=null&&isMobile.equalsIgnoreCase(pagebo.getIsMobile()))
				{
					if (!pagebo.isShow()) {
						continue;
					}
					list.addAll(pagebo.getAllFieldItem());
					if(this.sub_domain_map!=null){
						int n = this.sub_domain_map.size();
						if(n>0) {
                            n = n/2;
                        }
					if(pagebo.getSub_domain_map()!=null){
					Iterator it = pagebo.getSub_domain_map().entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry entry = (Map.Entry) it.next();
						String value = (String) entry.getValue();
						String key = (String)entry.getKey();
						if(key!=null&&key.indexOf("hz")!=-1){
							this.sub_domain_map.put(Integer.parseInt(key.substring(0,key.indexOf("hz")))+n+"hz", value);
						}else{
							this.sub_domain_map.put(Integer.parseInt(key)+n+"", value);
						}
						
					}
					}
					}else{
						this.sub_domain_map = pagebo.getSub_domain_map();
					}
					if(this.field_name_map!=null){
					if(pagebo.getField_name_map()!=null){
					Iterator it = pagebo.getField_name_map().entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry entry = (Map.Entry) it.next();
						String value = (String) entry.getValue();
						String key = (String)entry.getKey();
						this.field_name_map.put(key, value);
					}
					}
					}else{
						this.field_name_map = pagebo.getField_name_map();
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	
	/**
	 * 取得当前模板中所有页
	 * @return 列表存放的是TemplatePageBo对象
	 * @throws GeneralException
	 */
	public ArrayList getAllTemplatePage() throws GeneralException {
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rset = null;
		try {
			sql.append("select * from Template_Page where tabid=");
			sql.append(this.tabid);
			rset = dao.search(sql.toString());
			String task_id = "0";
			if (this.getTasklist().size() > 0) {
                task_id = "" + this.getTasklist().get(0);
            }
			boolean canGetIsMobile = false;
			DbWizard dbw = new DbWizard(this.conn);
			
			if(dbw.isExistField("template_Page", "IsMobile", false)){
			    canGetIsMobile = true;
			}
			boolean hasIsShow = false;
			if(dbw.isExistField("template_Page", "isShow", false)){
				hasIsShow = true;
			}
			while (rset.next()) {
			    String isMobile = null;
			    
			    if(canGetIsMobile){
			        isMobile = rset.getString("IsMobile");//获得页签模版标识    0||null 非手机端模板  1：手机端模板 
			    }
			    
                if(isMobile==null){//如果为null 表示没有设置走默认的代表的是非手机端模板,
                    isMobile = "0";
                }
                
                TemplatePageBo pagebo = new TemplatePageBo(this.conn, this.tabid, rset.getInt("pageid"), task_id,userView);
                pagebo.setTitle(rset.getString("title"));
                
                if (rset.getInt("isprn") == 0) {
                    pagebo.setIsprint(false);
                } else {
                    pagebo.setIsprint(true);
                }
                pagebo.setIsMobile(isMobile);
                pagebo.setShow(true);
                if(hasIsShow) {
                	String isShow = rset.getString("isShow");//页签是否显示 1||null 显示 0：不显示
                	if("0".equals(isShow)) {
                		pagebo.setShow(false);
                	}
                }
                list.add(pagebo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			PubFunc.closeResource(rset);
		}
		return list;
	}
	
	/**
	 * 取得业务模板的内容
	 * @param tabid
	 * @return
	 * @throws GeneralException
	 */
	private RecordVo readTemplate(int tabid)throws GeneralException
	{ 
		return TemplateUtilBo.readTemplate(tabid,this.conn);
	}
	
	public ArrayList getTasklist() {
		return tasklist;
	}

	public void setTasklist(ArrayList tasklist) {
		this.tasklist = tasklist;
	}
	
	private void setDoc(Document doc) {
		this.doc=doc;
	}

	public boolean isBEmploy() {
		return bEmploy;
	}

	public void setBEmploy(boolean employ) {
		bEmploy = employ;
		if(isBEmploy())//员工通过自助平台发动申请
        {
            this.bz_tablename="g_templet_"+this.tabid;
        } else {
            this.bz_tablename=userView.getUserName()+"templet_"+this.tabid;
        }
	}
}
