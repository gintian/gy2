/**
 * 
 */
package com.hjsj.hrms.businessobject.general.template;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.RowSet;

import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.cyberneko.html.parsers.DOMParser;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.xml.sax.InputSource;

import com.hjsj.hrms.businessobject.general.template.workflow.MessageToOtherSys;
import com.hjsj.hrms.businessobject.general.template.workflow.NodeType;
import com.hjsj.hrms.businessobject.general.template.workflow.SendMessageBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Actor;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Node;
import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardBo;
import com.hjsj.hrms.businessobject.infor.BaseInfoBo;
import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.template.IPendingTask;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateModuleParam;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.service.SynOaService;
import com.hjsj.hrms.transaction.param.CreateCodeTableTrans;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.CreateSequence;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ajax.TransVo;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.taglib.DataTable;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.VfsFileEntity;
import com.hrms.virtualfilesystem.service.VfsService;

/**
 * <p>Title:TemplateTableBo</p>
 * <p>Description:业务模板类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-9-17:16:21:23</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class TemplateTableBo implements  Serializable {
	
	private Category log = Category.getInstance(TemplateTableBo.class.getName());

	private Connection conn=null;
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
	
	private UserView userview;
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
	/**插入子集区域*/
	private HashMap submap=new HashMap();
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
    public boolean isRepreateSubmit() {
		return isRepreateSubmit;
	}

	public void setRepreateSubmit(boolean isRepreateSubmit) {
		this.isRepreateSubmit = isRepreateSubmit;
	}

	public ArrayList getSubUpdateList() {
		return subUpdateList;
	}

	public void setSubUpdateList(ArrayList subUpdateList) {
		this.subUpdateList = subUpdateList;
	}
    public String getDefaultselected() { 
		return defaultselected;
	}
    
    public String getOut_type() {
		return out_type;
	}

	public void setOut_type(String out_type) {
		this.out_type = out_type;
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



    private int pageCount=1; //共几页
    
    private boolean bHaveCalcItem=false; //是否有计算项
    
    private String impOthTableName="";  //将人员导入到其它临时表中，高级花名册调用
    
    
    public String getFilterStr() {
		return filterStr;
	}

	public void setFilterStr(String filterStr) {
		this.filterStr = filterStr;
	}

public ArrayList getMag_condlist() {
		return mag_condlist;
	}

	public void setMag_condlist(ArrayList mag_condlist) {
		this.mag_condlist = mag_condlist;
	}

	// private 
	/*
	 * 解析文件，在Paraxml方法中得到。
	 */
	/**
	 * @param conn
	 * @param tabid
	 */
	public TemplateTableBo(Connection conn, int tabid,UserView userview)throws GeneralException {
		super();
		this.conn = conn;
		this.tabid = tabid;
		this.userview=userview;
		this.table_vo=readTemplate(tabid);
		initdata();	
		bz_tablename=this.userview.getUserName()+"templet_"+this.tabid;
	}

	public TemplateTableBo(Connection conn, RecordVo table_ov,UserView userview) throws GeneralException{
		super();
		this.conn = conn;
		this.tabid =table_ov.getInt("tabid");
		this.userview=userview;
		this.table_vo=readTemplate(this.tabid);;
		initdata();
		bz_tablename=this.userview.getUserName()+"templet_"+this.tabid;
	}	
	
	 
	
	public UserView getUserview() {
		return userview;
	}
	
	 
	
	/**
	 * 获得拆单后各单据下的人员的查询条件
	 * @return
	 */
	public ArrayList getSplitInstanceWhl() throws GeneralException
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		try
		{
			
			ContentDAO dao=new ContentDAO(conn);
			RowSet recset=null;
			String tablename=userview.getUserName()+"templet_"+tabid;
			String sql="";
			if(this.split_data_model!=null&&this.split_data_model.trim().length()>0)
			{
				if("superior".equalsIgnoreCase(this.split_data_model)&&this.Relation_id!=null&&this.Relation_id.trim().length()>0) //直接领导
				{
					if(!"gwgx".equalsIgnoreCase(this.Relation_id))
					{
						String join_str="+";
						if(Sql_switcher.searchDbServer()==2) {
                            join_str="||";
                        }

						sql="select st.seqnum,st.a0100,st.basepre,"+Sql_switcher.isnull("twm.mainbody_id", "'-'")+" groupid from "+tablename+"  st left join t_wf_mainbody twm "
							+" on   lower(st.basepre"+join_str+"st.a0100)=lower(twm.object_id)  and twm.relation_id="+this.Relation_id+" and twm.sp_grade=9 where st.submitflag=1  order by groupid ";
						rowSet=dao.search(sql);
						HashMap groupMap=new HashMap();
						HashMap seqMap=new HashMap();  //如果一个人定义了多个直接领导，只取一个
						while(rowSet.next())
						{
							String basepre=rowSet.getString("basepre");
							String groupid=rowSet.getString("groupid")!=null?rowSet.getString("groupid"):"";
							if("-".equals(groupid))
							{
								String b0110="";String e0122="";String e01a1="";
								recset=dao.search("select b0110,e0122,e01a1 from "+basepre+"A01 where a0100='"+rowSet.getString("a0100")+"'");
								if(recset.next())
								{
									b0110=recset.getString("b0110")!=null?"un"+recset.getString("b0110"):"";
									e0122=recset.getString("e0122")!=null?"um"+recset.getString("e0122"):"";
									b0110=recset.getString("e01a1")!=null?"@k"+recset.getString("e01a1"):"";
								}
								if(b0110.length()>0)
								{
									recset=dao.search("select * from t_wf_mainbody where relation_id="+this.Relation_id+" and sp_grade=9 and lower(object_id) in ('"+b0110+"','"+e0122+"','"+e01a1+"') ");
									b0110="";
									e0122="";
									e01a1="";
									while(recset.next())
									{
										String object_id=recset.getString("object_id").trim();
										String mainbody_id=recset.getString("mainbody_id").trim();
										if("un".equalsIgnoreCase(object_id.substring(0,2))) {
                                            b0110=mainbody_id;
                               } else if("um".equalsIgnoreCase(object_id.substring(0,2))) {
                                            e0122=mainbody_id;
                                        } else if("@k".equalsIgnoreCase(object_id.substring(0,2)))
										{
											e01a1=mainbody_id;
											break;
                                    		}
									}

									if(e01a1.length()>0) {
                                        groupid=e01a1;
                                    } else if(e0122.length()>0) {
                                        groupid=e0122;
                                    } else if(b0110.length()>0) {
                                        groupid=b0110;
                                    }
								}
							}

							String seqnum=rowSet.getString("seqnum");
							if(seqMap.get(seqnum)!=null) {
                                continue;
                            }
							seqMap.put(seqnum,"1");
							if(groupMap.get(groupid)==null)
							{
								ArrayList alist=new ArrayList();
								alist.add(seqnum);
								groupMap.put(groupid,alist);
							}
							else
							{
                            ArrayList alist=(ArrayList)groupMap.get(groupid);
								alist.add(seqnum);
							}
						}

						Set set=groupMap.keySet();
						for(Iterator t=set.iterator();t.hasNext();)
						{
							String key=(String)t.next();
							ArrayList tmpList=(ArrayList)groupMap.get(key);
							String whl_str="";
							for(int i=0;i<tmpList.size();i++) {
                                whl_str+=" or seqnum='"+(String)tmpList.get(i)+"'";
                            }
							if(whl_str.length()>0) {
                                list.add(" and ("+whl_str.substring(3)+")");
                            }
						}


					}
					else {
                        list.add("");
                    }
				}
				else if(this.split_data_fields!=null&&this.split_data_fields.trim().length()>0) //分组指标
				{
					String [] temps=split_data_fields.split(",");
					HashMap map0=new HashMap();
					StringBuffer order_str=new StringBuffer(" order by ");
					HashMap group_filed_map=new HashMap();
					ArrayList groupFields=new ArrayList();
					for(int i=0;i<temps.length;i++)
					{
						if(temps[i].trim().length()==0) {
                            continue;
                  }
						if(temps[i].indexOf("(")!=-1&&temps[i].indexOf(")")!=-1)
						{

							int index=temps[i].indexOf("(");
							String itemid=temps[i].substring(0,(index));

							if(group_filed_map.get(itemid.toLowerCase())!=null) {
                                continue;
                            }

							String itemid0=itemid.substring(0,itemid.length()-2);
							String layer=temps[i].substring(index+1,index+2);
							FieldItem item=DataDictionary.getFieldItem(itemid0.toLowerCase());

							String tab="";
							if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())) {
                                tab="organization";
                            } else {
                                tab="codeitem";
                            }
							sql="select  distinct  st."+itemid+",a.codeitemid   from "+tablename+" st,(select codeitemid from "+tab+" where codesetid='"+item.getCodesetid()+"' and layer="+layer+") a "
								+" where st."+itemid+" is not null  and  st."+itemid+" like a.codeitemid"+Sql_switcher.concat()+"'%'";
							rowSet=dao.search(sql);
							HashMap map=new HashMap();
							while(rowSet.next())
							{
								map.put(rowSet.getString(itemid),rowSet.getString("codeitemid"));
							}
							rowSet=dao.search("select distinct  "+itemid+" from "+tablename);
							while(rowSet.next())
							{
								if(map.get(rowSet.getString(1))==null)
								{
									map.put(rowSet.getString(1),rowSet.getString(1));
								}
							}
							group_filed_map.put(itemid.toLowerCase(),"1");
        				map0.put(itemid.toLowerCase(),map);
							order_str.append(itemid+",");
							groupFields.add(itemid);
						}
						else
						{
							if(group_filed_map.get(temps[i].toLowerCase())!=null) {
                                continue;
                            }
							group_filed_map.put(temps[i].toLowerCase(),"1");
							order_str.append(temps[i]+",");
							groupFields.add(temps[i]);
						}
					}
					order_str.setLength(order_str.length()-1);

					sql="select * from "+tablename+"  where submitflag=1 "+order_str.toString();
					rowSet=dao.search(sql);
					HashMap groupMap=new HashMap();
					while(rowSet.next())
					{
						String groupValue="";
						for(int i=0;i<groupFields.size();i++)
						{
							String temp=(String)groupFields.get(i);
							if(rowSet.getString(temp)==null||rowSet.getString(temp).trim().length()==0) {
                                groupValue+=",-";
} else
                                {
								if(map0.get(temp.toLowerCase())==null)
                                    {
                                        groupValue+=","+rowSet.getString(temp);
                                                		}
							else
								{
									HashMap map=(HashMap)map0.get(temp.toLowerCase());
									groupValue+=","+(String)map.get(rowSet.getString(temp));
								}
							}
						}

						String seqnum=rowSet.getString("seqnum");

						if(groupMap.get(groupValue)==null)
						{
							ArrayList alist=new ArrayList();
							alist.add(seqnum);
							groupMap.put(groupValue,alist);
						}
						else
						{
							ArrayList alist=(ArrayList)groupMap.get(groupValue);
							alist.add(seqnum);
						}
					}

					Set set=groupMap.keySet();
					for(Iterator t=set.iterator();t.hasNext();)
					{
						String key=(String)t.next();
                            ArrayList tmpList=(ArrayList)groupMap.get(key);
                            String whl_str="";
for(int i=0;i<tmpList.size();i++) {
whl_str+=" or seqnum='"+(String)tmpList.get(i)+"'";
                        }
						if(whl_str.length()>0) {
                            list.add(" and ("+whl_str.substring(3)+")");
                        }
					}

				}
				else {
                    list.add("");
                }
			}
			else {
                list.add("");
            }

			if(recset!=null) {
                recset.close();
            }

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception("人员拆单出错!"));
		} finally {
			try {
				if (rowSet != null){
					rowSet.close();
				}
			}catch (SQLException sql){

			}
		}
		return list;
	}





	/**
	 * 根据任务号获得节点的报备对象信息
	 * @param taskid
	 * @return
	 */
	public LazyDynaBean getUsersByfiling(String taskid,String tabid)
	{
		LazyDynaBean abean=new LazyDynaBean();
		String user_="";
		String user_h="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			if(bsp_flag) //走审批
			{
				String sql="";
				if(taskid==null|| "0".equals(taskid.trim())) {
                    sql="select  node_id,ext_param  from t_wf_node where tabid="+tabid+" and nodetype='1'";
                } else {
                    sql="select  node_id,ext_param  from t_wf_node where node_id=(select  node_id from t_wf_task  where task_id="+taskid+")";
                }
				RowSet rowSet=dao.search(sql);
				if(rowSet.next())
				{
					Document doc=null;
					Element element=null;
					String  ext_param=Sql_switcher.readMemo(rowSet,"ext_param");
					if(ext_param!=null&&ext_param.trim().length()>0)
					{
						doc=PubFunc.generateDom(ext_param);;
						String xpath="/params/filing_obj";
						XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
						element=(Element)findPath.selectSingleNode(doc);
						if(element!=null)
						{
                                        if(element.getValue()!=null&&element.getValue().length()>0)
							{
								String str=element.getValue();
								String[] temps=str.split(",");
								for(int i=0;i<temps.length;i++)
								{
									if(temps[i].length()>0)
									{
										rowSet=dao.search("select username,fullname from operuser where username='"+temps[i]+"'");
                                        if(rowSet.next())
{
                                                     user_h+=",4:" + rowSet.getString("username");
											String fullname=rowSet.getString("fullname");
    if(fullname==null||fullname.trim().length()==0) {
         fullname = rowSet.getString("username");
                                            }
    user_+=","+fullname;

} else if(temps[i].length()>3) {
                                            String dbname=temps[i].substring(0,3);

                                            rowSet=dao.search("select a0100,a0101 from "+dbname+"A01 where a0100='"+temps[i].substring(3)+"'");
											if(rowSet.next())
											{
                                                        					user_h+=",1:"+dbname+rowSet.getString("a0100");
												user_+=","+rowSet.getString("a0101");
											}
										}
									}
								}


							}
						}

					}
				}
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(user_.length()>0) {
            user_=user_.substring(1);
        }
		if(user_h.length()>0) {
            user_h=user_h.substring(1);
        }
		abean.set("user_",user_);
		abean.set("user_h",user_h);
		return abean;
	}


	public boolean  isCorrect(String tabid)
	{
		boolean isCorrect=false;
		if(this.userview.isHaveResource(IResourceConstant.RSBD,tabid))//人事移动
        {
            isCorrect=true;
        }
		if(!isCorrect) {
            if(this.userview.isHaveResource(IResourceConstant.ORG_BD,tabid))//组织变动
				{
isCorrect=true;}
        }
		if(!isCorrect) {
            if(this.userview.isHaveResource(IResourceConstant.POS_BD,tabid))//岗位变动
				{
isCorrect=true;}
        }
		if(!isCorrect) {
            if(this.userview.isHaveResource(IResourceConstant.GZBD,tabid))//工资变动
				{
isCorrect=true;}
        }
		if(!isCorrect) {
            if(this.userview.isHaveResource(IResourceConstant.INS_BD,tabid))//保险变动
				{
isCorrect=true;}
        }
		if(!isCorrect) {
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS,tabid))
{
isCorrect=true;}
        }
		if(!isCorrect) {
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS_FG,tabid))
				{
isCorrect=true;}
        }
		if(!isCorrect) {
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS_GX,tabid))
				{
isCorrect=true;}
        }
		if(!isCorrect) {
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS_JCG,tabid))
				{
isCorrect=true;}
        }
		return isCorrect;
	}




	/**
	 * 校验 业务模板中的人员 是否已存在流程中
	 * @return
	 */
	public String  validateExistData()
	{
		String info="";
		try
		{
			if("0".equals(unique_check)) {
                return info;
            }
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="";
			if(isBEmploy())//员工通过自助平台发动申请
			{
				sql="select count(task_id) from t_wf_task where task_id in ( select task_id from t_wf_task_objlink where seqnum in (select seqnum from templet_"+this.tabid+" where  a0100='"+this.userview.getA0100()+"' and lower(basepre)='"+this.userview.getDbname().toLowerCase()+"' ) ) and (  (task_state<>'5' and task_state<>'4') or task_state is null) and "+Sql_switcher.isnull("bs_flag", "1")+"=1 ";
			}
			else
			{
				if(this.infor_type==1)
				{
					sql="select count(task_id) from t_wf_task where task_id in ( select task_id from t_wf_task_objlink where state<>3 and seqnum in (select seqnum from templet_"+this.tabid+" where ";
					sql+=" exists (select null from "+this.userview.getUserName()+"templet_"+this.tabid+" where  submitflag=1 and   lower("+this.userview.getUserName()+"templet_"+this.tabid+".basepre)=lower(templet_"+this.tabid+".basepre) and "+this.userview.getUserName()+"templet_"+this.tabid+".a0100=templet_"+this.tabid+".a0100  ) ";
					sql+=") ) and ( (task_state<>'5' and task_state<>'4') or task_state is null)  and "+Sql_switcher.isnull("bs_flag", "1")+"=1 ";
					sql+="and ins_id not in (select ins_id from T_WF_INSTANCE  where T_WF_INSTANCE.ins_id=t_wf_task.INS_ID and tabid="+this.tabid+" and FINISHED='5' )";  //添加过滤条件，将可能存在的脏数据排除掉
    }
		else
				{
					String key="b0110";
					if(this.infor_type==3) {
                        key="e01a1";
                    }

					sql="select count(task_id) from t_wf_task where task_id in ( select task_id from t_wf_task_objlink where state<>3 and  seqnum in ( select seqnum from templet_"+this.tabid+" where ";
					sql+=" exists (select null from "+this.userview.getUserName()+"templet_"+this.tabid+" where submitflag=1 and   "+this.userview.getUserName()+"templet_"+this.tabid+"."+key+"=templet_"+this.tabid+"."+key+"  ) ";
					sql+=" ) ) and ( (task_state<>'5' and task_state<>'4')  or task_state is null) and "+Sql_switcher.isnull("bs_flag", "1")+"=1 ";
					sql+="and ins_id not in (select ins_id from T_WF_INSTANCE  where T_WF_INSTANCE.ins_id=t_wf_task.INS_ID and tabid="+this.tabid+" and FINISHED='5' )";  //添加过滤条件，将可能存在的脏数据排除掉
				}
			}
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				if(rowSet.getInt(1)>0)
				{
					if(isBEmploy())//员工通过自助平台发动申请
                    {
                        info="单据正在处理，不允许重复申请!";
                    } else
					{
						String tabName="templet_"+this.tabid;
						String tabName2=this.userview.getUserName()+"templet_"+this.tabid;

						sql="";

						if(this.infor_type==1)
						{
							sql=" select distinct a0101_1 from "+tabName+" where seqnum in ( select seqnum from t_wf_task_objlink where state<>3 and  task_id in (  ";
							sql+=" select task_id from t_wf_task where task_id in ( select task_id from t_wf_task_objlink where state<>3 and  seqnum in ( ";
							sql+=" select seqnum from "+tabName+" where exists (select null from "+tabName2+" where   submitflag=1 and    lower("+tabName2+".basepre)=lower("+tabName+".basepre) and "+tabName2+".a0100="+tabName+".a0100  ) ";
							sql+=" ) ) and (  (task_state<>'5' and task_state<>'4')  or task_state is null) and "+Sql_switcher.isnull("bs_flag", "1")+"=1 ";
							sql+=" ) ) and exists (select null from "+tabName2+" where   submitflag=1 and   lower("+tabName2+".basepre)=lower("+tabName+".basepre) and "+tabName2+".a0100="+tabName+".a0100  ) ";
						}
						else
						{
							String key="b0110";
							if(this.infor_type==3) {
                                key="e01a1";
                            }
							sql=" select distinct codeitemdesc_1 from "+tabName+" where seqnum in (select seqnum from t_wf_task_objlink where state<>3 and  task_id in (  ";
							sql+=" select task_id from t_wf_task where task_id in (select task_id from t_wf_task_objlink where state<>3 and  seqnum in ( ";
							sql+=" select seqnum from "+tabName+" where exists (select null from "+tabName2+" where   submitflag=1 and     "+tabName2+"."+key+"="+tabName+"."+key+"  ) ";
							sql+=" ) ) and ( (task_state<>'5' and task_state<>'4') or task_state is null) and "+Sql_switcher.isnull("bs_flag", "1")+"=1 ";
							sql+=" ) ) and exists (select null from "+tabName2+" where submitflag=1 and   "+tabName2+"."+key+"="+tabName+"."+key+" ) ";

						}

						rowSet=dao.search(sql);
						String info_str="";
						while(rowSet.next())
						{
							info_str+=","+rowSet.getString(1);
						}
						info=info_str.substring(1)+" 的单据正在处理，不允许重复申请!";
					}
				}

			}
			PubFunc.closeDbObj(rowSet);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return info;
	}



	/**
	 * 如果驳回的对象是 考核关系角色，则返回驳回对象的信息
	 * @param taskid
	 * @param dao
	 * @param ins_id
	 * @return
	 */
	public ArrayList getRejectObjList(String taskid,String ins_id)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			if(taskid!=null&&!"0".equals(taskid))
			{
				RecordVo task_vo=new RecordVo("t_wf_task");
				task_vo.setInt("task_id",Integer.parseInt(taskid));
				task_vo=dao.findByPrimaryKey(task_vo);
				String pri_task_id=String.valueOf(task_vo.getInt("pri_task_id"));
				RowSet rowSet=dao.search("select role_id,seqnum from t_wf_task_datalink where ins_id="+ins_id+" and task_id="+pri_task_id);
				String role_id="";
				String seqnum="";
				if(rowSet.next())
				{
					role_id=rowSet.getString("role_id");
					seqnum=rowSet.getString("seqnum");
				}
				if(role_id.length()>0)
				{
					WF_Actor wf_actor=new WF_Actor(role_id,"2");
					int role_property=wf_actor.decideIsKhRelation(wf_actor.getActorid(),wf_actor.getActortype(),this.conn);
					if(role_property!=0)
					{
						String tabid=String.valueOf(this.tabid);
						TemplateTableBo bo=new TemplateTableBo(this.conn,Integer.parseInt(tabid),this.userview);
                                String a0100="";
						String basepre="";
						rowSet=dao.search("select * from templet_"+tabid+" where seqnum='"+seqnum+"'");
						if(rowSet.next())
						{
							list=bo.getObjectApprovers(rowSet.getString("a0100"),rowSet.getString("basepre"),role_property);
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
		return list;
	}
	public ArrayList getNodeList(String tabid)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		try
		{
			String sql="select t_wf_node.*,t_wf_actor.actor_type from t_wf_node left join t_wf_actor on t_wf_node.node_id=t_wf_actor.node_id "
					+" where t_wf_node.tabid="+tabid+" and t_wf_node.nodetype not in (4,5,6,7) and t_wf_node.nodetype!=1 and t_wf_node.nodetype!=9 order by  t_wf_node.node_id ";
			rset=dao.search(sql);
			while(rset.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("node_id", rset.getString("node_id"));
				abean.set("nodename",rset.getString("nodename"));
				abean.set("actor_type", rset.getString("actor_type")!=null?rset.getString("actor_type"):"");
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rset);
		}
		return list;
	}

	/**
	 * 查询模板对应的已构库的子集列表
	 * @return
	 */
	private ArrayList searchUsedSetList()throws GeneralException{
		ArrayList setlist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		try
		{
			boolean isK01=false;
			boolean isB01=false;
			StringBuffer strsql=new StringBuffer();
			strsql.append("select distinct T.setname from template_set T,fieldset N where T.tabid=");
			strsql.append(this.tabid);
			strsql.append(" and T.setname=N.fieldsetid and N.useflag<>'0'");
			strsql.append(" and (T.chgstate=1 or T.chgstate=2) and (T.setname is not null) ");
			strsql.append(" order by T.setname");
        rset=dao.search(strsql.toString());
			while(rset.next())
			{
				setlist.add(rset.getString("setname"));
				if("K01".equalsIgnoreCase(rset.getString("setname"))) {
                    isK01=true;
                }
				if("B01".equalsIgnoreCase(rset.getString("setname"))) {
                    isB01=true;
                }
			}

			if(this.infor_type==2&&!isB01) {
                setlist.add("B01");
            } else if(this.infor_type==3&&!isK01) {
                setlist.add("K01");
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeDbObj(rset);
		}
		return setlist;
	}

	public WF_Node getWF_StartNode()throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.conn);
		WF_Node start_node=null;
		try
		{
			start_node=new WF_Node(this,this.conn);
			StringBuffer strsql=new StringBuffer();
			strsql.append("select * from t_wf_node where nodetype='");
			strsql.append(NodeType.START_NODE);
			strsql.append("' and tabid=");
			strsql.append(this.tabid);
			RowSet rset=dao.search(strsql.toString());
			if(rset.next())
			{

				start_node.setNode_id(rset.getInt("node_id"));
				start_node.setNodename(rset.getString("nodename"));
				start_node.setNodetype(Integer.parseInt(rset.getString("nodetype")));
				start_node.setExt_param(Sql_switcher.readMemo(rset,"ext_param"));
			}
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
{
ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return start_node;
	}




	/**
	 * 求实际的业务数,本次模板做了多少人的业务
	 * @return
	 */
	public String getRecordBusiTopic(String sql,String tabname)
	{
		int nmax=0;
		StringBuffer stopic=new StringBuffer();
		stopic.append("(");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);

			String a0101="a0101_1";
			RecordVo vo=new RecordVo(tabname);
			if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
			{
				a0101="a0101_2";
			}
			RowSet rset=dao.search(sql);
			int i=0;
			while(rset.next())
			{
				if(i>4) {
                    break;
                }
                if(i!=0) {
                    stopic.append(",");
                }
				stopic.append(rset.getString(a0101)==null?"":rset.getString(a0101));
				i++;
			}


			sql=sql.replaceAll(tabname+".\\*","count(*)");
			if(sql.indexOf("(*)")==-1) {
        sql=sql.replaceAll("\\*", "count(*)");
            }
			rset=dao.search(sql);
			if(rset.next()) {
                nmax=rset.getInt(1);
            }
			//if(nmax!=i)
			stopic.append(",");
			stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
			//stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
			stopic.append(nmax);
			stopic.append("人)");
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			return stopic.toString();
		}
		return stopic.toString();
	}




	/**
	 * 求实际的业务数,本次模板做了多少人的业务
	 * @return
	 */
	public String getRecordBusiTopic(String whl)
	{
		int nmax=0;
		StringBuffer stopic=new StringBuffer();
		stopic.append("(");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer strsql=new StringBuffer();
			String tabname=null;
			StringBuffer strWhere=new StringBuffer();
			if(this.isBEmploy())
			{
			   tabname="g_templet_"+this.tabid;
              strWhere.append(" where a0100='");
			   strWhere.append(this.userview.getA0100());
			   strWhere.append("' and basepre='");
			   strWhere.append(this.userview.getDbname());
			   strWhere.append("'");
			}
			else
			{
			   tabname=this.userview.getUserName()+"templet_"+this.tabid;
			   strWhere.append(" where submitflag=1");
			   if(whl!=null) {
                   strWhere.append(whl);
               }
}
			String a0101="a0101_1";
    RecordVo vo=new RecordVo(tabname);
			if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
			{
					a0101="a0101_2";
			}

			if(this.infor_type==2||this.infor_type==3)
			{
				a0101="codeitemdesc_1";
				if(this.operationtype==5) {
                    a0101="codeitemdesc_2";
                }
			}

			strsql.append("select  ");
			strsql.append(a0101);
			strsql.append(" from ");
			strsql.append(tabname);
			strsql.append(strWhere.toString());
			RowSet rset=dao.search(strsql.toString());
			int i=0;
			while(rset.next())
			{
				if(i>4) {
                    break;
     }
				if(i!=0) {
                    stopic.append(",");
                }
				stopic.append(rset.getString(a0101)==null?"":rset.getString(a0101));
				i++;
			}
			strsql.setLength(0);

			strsql.append("select count(*) as nmax from ");
			strsql.append(tabname);
			strsql.append(strWhere.toString());

			rset=dao.search(strsql.toString());
			if(rset.next()) {
                nmax=rset.getInt("nmax");
            }
			//if(nmax!=i)
			stopic.append(",");
			stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
			//stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
			stopic.append(nmax);
			if(this.infor_type==1) {
                stopic.append("人)");
            } else {
                stopic.append("条记录)");
            }
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
return stopic.toString();
		}
		return stopic.toString();
	}


	/**
	 * 求实际的业务数,本次模板做了多少人的业务
* @return
*/
public String getRecordBusiTopic(int task_id,int ins_id)
	{
		int nmax=0;
StringBuffer stopic=new StringBuffer();
		stopic.append(this.table_vo.getString("name")+"");
		stopic.append("(");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer strsql=new StringBuffer();
			String tabname="templet_"+this.tabid;
			String a0101="a0101_1";
			RecordVo vo=new RecordVo(tabname);
			if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
			{
					a0101="a0101_2";
			}

			if(this.infor_type==2||this.infor_type==3)
			{
				a0101="codeitemdesc_1";
				if(this.operationtype==5) {
                 a0101="codeitemdesc_2";
                }
			}
			String strWhere=" where ";
			if(ins_id!=0) {
                strWhere+=" ins_id="+ins_id+"  and ";
            }
			strWhere+=" exists (select null from t_wf_task_objlink where "+tabname+".seqnum=t_wf_task_objlink.seqnum and task_id="+task_id+" and (state is null or  state=0) )";
			strsql.append("select  ");
			strsql.append(a0101);
			strsql.append(" from ");
			strsql.append(tabname);
			strsql.append(strWhere);
			RowSet rset=dao.search(strsql.toString());
			int i=0;
			while(rset.next())
			{
				if(i>4) {
                    break;
}
				if(i!=0) {
                    stopic.append(",");
         }
				stopic.append(rset.getString(a0101)==null?"":rset.getString(a0101));
				i++;
			}
			strsql.setLength(0);

			strsql.append("select count(*) as nmax from ");
			strsql.append(tabname);
			strsql.append(strWhere.toString());

			rset=dao.search(strsql.toString());
			if(rset.next()) {
                nmax=rset.getInt("nmax");
            }
			//if(nmax!=i)
			stopic.append(",");
			stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
			//stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
			stopic.append(nmax);
			if(this.infor_type==1) {
                stopic.append("人)");
            } else {
                stopic.append("条记录)");
            }
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			return stopic.toString();
	}
		return stopic.toString();
	}


	/**
	 * 求实际的业务数,本次模板做了多少人的业务
	 * @return
	 */
	public String getRecordBusiTopicByState(int task_id,int state)
	{
		int nmax=0;
		StringBuffer stopic=new StringBuffer();
		stopic.append(this.table_vo.getString("name")+"");
		stopic.append("(");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer strsql=new StringBuffer();
			String tabname="templet_"+this.tabid;
			String a0101="a0101_1";
			RecordVo vo=new RecordVo(tabname);
			if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
			{
					a0101="a0101_2";
			}

			if(this.infor_type==2||this.infor_type==3)
			{
				a0101="codeitemdesc_1";
				if(this.operationtype==5) {
                    a0101="codeitemdesc_2";
                }
			}
			String strWhere=" where ";
			if(ins_id!=0) {
                strWhere+=" ins_id="+ins_id+"  and ";
            }

			String strWhere2="";
			if(state==0) {
                strWhere2=" and (state is null or  state=0) ";
            } else {
                strWhere2=" and state="+state+" ";
            }
			strWhere+=" exists (select null from t_wf_task_objlink where "+tabname+".seqnum=t_wf_task_objlink.seqnum and task_id="+task_id+" "+strWhere2+"  )";
			strsql.append("select  ");
			strsql.append(a0101);
			strsql.append(" from ");
			strsql.append(tabname);
			strsql.append(strWhere);
			RowSet rset=dao.search(strsql.toString());
			int i=0;
			while(rset.next())
			{
				if(i>4) {
                    break;
                }
				if(i!=0) {
                    stopic.append(",");
                }
				stopic.append(rset.getString(a0101)==null?"":rset.getString(a0101));
				i++;
			}
			strsql.setLength(0);

			strsql.append("select count(*) as nmax from ");
			strsql.append(tabname);
			strsql.append(strWhere.toString());

			rset=dao.search(strsql.toString());
			if(rset.next()) {
                nmax=rset.getInt("nmax");
            }
			//if(nmax!=i)
			stopic.append(",");
			stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
			//stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
			stopic.append(nmax);
			if(this.infor_type==1)
			{
				stopic.append("人");
			}
			else {
                stopic.append("条记录 ");
            }
			if(state==3) {
                stopic.append(" 被撤销");
            }
			stopic.append(")");
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			return stopic.toString();
		}
		return stopic.toString();
	}

	/**
	 * 求实际的业务数,本次模板做了多少人的业务
	 * @return
	 */
	public String getRecordBusiTopic(String tabname,String seqnumstr,int totalNum)
	{
		int nmax=0;
		StringBuffer stopic=new StringBuffer();
		stopic.append(this.table_vo.getString("name")+"(");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer strsql=new StringBuffer();

			String a0101="a0101_1";
			RecordVo vo=new RecordVo(tabname);
			if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
			{
					a0101="a0101_2";
			}

			if(this.infor_type==2||this.infor_type==3)
			{
				a0101="codeitemdesc_1";
				if(this.operationtype==5) {
                    a0101="codeitemdesc_2";
                }
			}

			strsql.append("select  ");
    strsql.append(a0101);
    strsql.append(" from ");
strsql.append(tabname);
strsql.append(" where "+seqnumstr.substring(3));
			RowSet rset=dao.search(strsql.toString());

			int i=0;
			while(rset.next())
			{

				if(i!=0) {
                    stopic.append(",");
                }
				stopic.append(rset.getString(a0101)==null?"":rset.getString(a0101));
				i++;
			}

			//if(nmax!=i)
			stopic.append(",");
			stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
			//stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
			stopic.append(totalNum);
			if(this.infor_type==1) {
                stopic.append("人)");
            } else {
                stopic.append("条记录)");
            }
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			return stopic.toString();
		}
		return stopic.toString();
	}


	/**
	 * 分析此模板中更新子集以及字段列表
	 * @param fieldlist
	 */
	public HashMap readUpdatesSetField(ArrayList fieldlist)
	{
		HashMap hm=new HashMap();
		//LinkedHashMap hm=new  LinkedHashMap(10,1,true);
		String setname=null;
		TSubsetCtrl subctrl=null;
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem fielditem=(FieldItem)fieldlist.get(i);
			if(fielditem==null) {
                continue;
            }
			setname=fielditem.getFieldsetid();
			/**照片去掉*/
			if("A00".equalsIgnoreCase(setname)&&("photo".equalsIgnoreCase(fielditem.getItemid())|| "ext".equalsIgnoreCase(fielditem.getItemid()))) {
                continue;
            }

			/**分析权限标志
			 * =0无任何权限
			 * =1读权限
			 * =2写权限
			 * */

			if(fielditem.getItemid().indexOf("t_")==-1||(fielditem.getItemid().length()>2&&!"t_".equalsIgnoreCase(fielditem.getItemid().trim().substring(0,2))))
			{
				String state=this.userview.analyseFieldPriv(fielditem.getItemid());//t_axx
				//特殊指标 直接给写权限 50667
				if (",start_date,codesetid,parentid,codeitemdesc,corcode,to_id,".indexOf(fielditem.getItemid())>-1){
					state="2";
				}
				fielditem.setPriv_status(Integer.parseInt(state));
				/**变化前、没有写权限时或者未构库，不作处理*/
				if(fielditem.isChangeBefore()||((fielditem.getPriv_status()!=2&& "0".equals(UnrestrictedMenuPriv))|| "0".equals(fielditem.getUseflag())))
				{
					String field_name=fielditem.getItemid();
					if((this.infor_type==2||this.infor_type==3)&&fielditem.getPriv_status()!=2&&!("codesetid".equalsIgnoreCase(field_name)|| "codeitemdesc".equalsIgnoreCase(field_name)|| "corcode".equalsIgnoreCase(field_name)|| "parentid".equalsIgnoreCase(field_name)|| "start_date".equalsIgnoreCase(field_name))) {
                        continue;
                    } else {
                        continue;
                    }
				}
			}
			else //插入子集区域
			{
				if(fielditem.isChangeAfter()){
					//兼容同一子集有多个变化后的情况 wangrd 20160826
					if(this.sub_domain_map!=null&&
							this.sub_domain_map.get(""+i)!=null
							&&this.sub_domain_map.get(""+i).toString().trim().length()>0){
						String tmp= setname+"_"+this.sub_domain_map.get(""+i).toString();
						submap.put(tmp, fielditem);

					}
					else {
						submap.put(setname, fielditem);
					}

				}
				continue;
			}
			if(hm.containsKey(setname))
			{
				subctrl=(TSubsetCtrl)hm.get(setname);
				subctrl.addField(fielditem);
			}
			else
			{
				subctrl=new TSubsetCtrl();
				/**default 状态下*/
				if(fielditem.isMainSet()) {
                    subctrl.setUpdatetype(SubSetUpdateType.UPDATE);
                } else {
                    subctrl.setUpdatetype(SubSetUpdateType.APPEND);
                }
				subctrl.setSetcode(fielditem.getFieldsetid());
				subctrl.addField(fielditem);
				hm.put(setname,subctrl);
			}
		}//for i loop end.
		Iterator iterator=hm.entrySet().iterator();
		String temp=null;

		while(iterator.hasNext())
		{
			Entry entry=(Entry)iterator.next();
			setname=(String)entry.getKey();
			subctrl=(TSubsetCtrl)entry.getValue();
			for(int j=0;j<subUpdateList.size();j++)
			{
				TSubsetCtrl subctrl0=(TSubsetCtrl)subUpdateList.get(j);
				temp=subctrl0.getSetcode();
				String submenu=subctrl0.getSubMenu();
				if(setname.equalsIgnoreCase(temp)&&(submenu==null||!"true".equalsIgnoreCase(submenu.trim())))
				{
					subctrl.setUpdatetype(subctrl0.getUpdatetype());
					subctrl.setInnerupdatetype(subctrl0.getInnerupdatetype());
					subctrl.setRefPreRec(subctrl0.getRefPreRec());
					subctrl.setCondFormula(subctrl0.getCondFormula());
					break;
				}
			}
		}//while loop end.
		return hm;
	}

	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public ArrayList getMidVariableList()throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where  nflag=0 and templetId <> 0 ");  //人事异动的nflag=0
			if(onlyComputeFieldVar){  //计算本模板中的临时变量和模板指标中引入的共享的临时变量。 20150929 liuzy
				buf.append(" and (templetId ="+this.tabid+" or (cstate ='1' and cname in (select field_name from template_set where tabid="+this.tabid+" and nullif(field_name,'') is not null ))) ");
			}else{
				buf.append(" and (templetId ="+this.tabid+"  or cstate ='1') ");
			}
			buf.append(" order by sorting");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid(""/*"A01"*/);//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setCodesetid(rset.getString("codesetid")==null?"0":rset.getString("codesetid"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4:
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.


			//只保留模板需要的临时变量 考虑 模板引入的临时变量指标、计算公式、审核公式
			PubFunc.closeDbObj(rset);


		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return fieldlist;
	}


	/**
	 * 从临时变量中取得对应指标列表
	 * @param func_id 1:计算公式    2：审核公式   3:计算公式+审核公式
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public ArrayList getMidVariableListByFunc(int func_id)throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid,templetId from ");
			buf.append(" midvariable where  nflag=0 and templetId <> 0 and (templetId ="+this.tabid+"  or cstate ='1') ");
			buf.append(" order by sorting");

			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			HashMap priVarMap=new HashMap();
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid(""/*"A01"*/);//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setCodesetid(rset.getString("codesetid")==null?"0":rset.getString("codesetid"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4:
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
        }
        item.setVarible(1);
				fieldlist.add(item);
				if(rset.getString("templetId")!=null&&rset.getString("templetId").equalsIgnoreCase(this.tabid+"")) {
                    priVarMap.put(rset.getString("cname"),"1");
                }
			}// while loop end.

			 ArrayList expList=new ArrayList();//用于存放计算条件，以及计算公式的表达式
			 if(func_id==1||func_id==3) // 获得计算公式
			 {

				    ArrayList formulalist=readFormula();
					for(int i=0;i<formulalist.size();i++)
					{
						FormulaGroupBo formulabo=(FormulaGroupBo)formulalist.get(i);
						/**先对计算条件进行处理*/
						String cond=formulabo.getStrWhere();
						if(cond!=null&&cond.trim().length()>0) {
                            expList.add(cond);
                        }
						ArrayList list=formulabo.getFormulalist();
						for(int j=0;j<list.size();j++)
						{
							LazyDynaBean dynabean=(LazyDynaBean)list.get(j);
							String fieldname=(String)dynabean.get("lexpr");
							String formula=(String)dynabean.get("rexpr");
							if(formula!=null&&formula.length()>0) {
                                expList.add(formula);
                            }
						}
					}

			}

			if(func_id==2||func_id==3)
			{
					ArrayList list=getLogicExpressList();
					if(list.size()>0)
					{
						for(int i=0;i<list.size();i++)
						{
							RecordVo vo=(RecordVo)list.get(i);
							String formula=vo.getString("formula");
							expList.add(formula);
						}
					}
			}
			buf.setLength(0);
			buf.append("select mts.* from ");
			buf.append("(select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from template_set ts ,midvariable m where ts.flag='V' and ts.tabid="+this.tabid);
			buf.append(" and (m.TempletID="+this.tabid+" or m.cstate='1') and TempletID<>0 and m.nFlag=0 and m.cName=ts.Field_name )mts");
			rset=dao.search(buf.toString());
			ArrayList templarVarList=new ArrayList();
			while(rset.next()){
			    FieldItem item=new FieldItem();
                item.setItemid(rset.getString("cname"));
                item.setFieldsetid(""/*"A01"*/);//没有实际含义
                item.setItemdesc(rset.getString("chz"));
                item.setItemlength(rset.getInt("fldlen"));
                item.setDecimalwidth(rset.getInt("flddec"));
                item.setCodesetid(rset.getString("codesetid")==null?"0":rset.getString("codesetid"));
                item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
                switch(rset.getInt("ntype"))
                {
                case 1://
                    item.setItemtype("N");
                    break;
                case 2:
                case 4:
                    item.setItemtype("A");
                    break;
                case 3:
                    item.setItemtype("D");
                    break;
                }
                item.setVarible(1);
                templarVarList.add(item);
			}
			if(expList.size()>0)
			{
				ArrayList newList=new ArrayList();

				HashMap usedMap=new HashMap();
				for(int i=0;i<fieldlist.size();i++)//fiedlist 如果是人事异动的话  存放的是该模版中涉及到的临时变量
				{
						FieldItem item=(FieldItem)fieldlist.get(i);
						checkValid(item,expList,fieldlist,usedMap,templarVarList);//检查模版中涉及到的临时变量那些被使用了
				}
				for(int i=0;i<fieldlist.size();i++)
				{
						FieldItem item=(FieldItem)fieldlist.get(i);
						String cname=item.getItemid();
						if(priVarMap.get(item.getItemid())!=null) //20151016  邓灿 私有临时变量都要计算
                        {
                            newList.add(item);
                        } else if(usedMap.get(cname)!=null)//意味着在计算公式或者计算条件中该临时变量被使用了，或者是该模版中插入了该临时变量
                        {
                            newList.add(item);
                        }
				}
				fieldlist=newList;
			}
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return fieldlist;
	}



	/**
	 * 检查临时变量在表达式中是否被引用
	 * @param cname
	 * @param chz
	 * @param formulaList
	 * @param templarVarList
	 * @return
	 */
	private void checkValid(FieldItem item,ArrayList formulaList,ArrayList varList,HashMap usedMap, ArrayList templarVarList)
	{
		String temp="";
		String cname=item.getItemid();
		String chz=item.getItemdesc();
		String formula=item.getFormula();
		if(!"1".equals(usedMap.get(cname))){
			for(int i=0;i<formulaList.size();i++)
			{
					temp=(String)formulaList.get(i);
					if(cname!=null&&cname.trim().length()>0&&temp.toLowerCase().indexOf(cname.toLowerCase())!=-1)
					{

						usedMap.put(cname,"1");
						importMap(varList,usedMap,formula);//递归查询临时变量调用临时变量
						break;
					}
					else if(chz!=null&&chz.trim().length()>0&&temp.toLowerCase().indexOf(chz.toLowerCase())!=-1)
					{

						usedMap.put(cname,"1");
						importMap(varList,usedMap,formula);//递归查询临时变量调用临时变量
						break;
					}
			}
			//如果计算公式等都没有引用该临时变量那么就需要检查当前模版中是否插入该临时变量 xcs 2014-6-6
			for(int i=0;i<templarVarList.size();i++){//主要是为了检查模版中是否插入了该临时变量
			    FieldItem tempitem = (FieldItem) templarVarList.get(i);
			    String name=tempitem.getItemid();
			    String tempFormula=item.getFormula();
			    if(name.equalsIgnoreCase(cname)){//这个临时变量在当前模版中被插入了
			        usedMap.put(name,"1");
			        importMap(varList,usedMap,tempFormula);//递归查询临时变量调用临时变量
			        break;
			    }
			}
		}
	}
	/**
	 * gao增加ao递归
	 * 临时变量调用临时变量
	 * varList：所有临时变量
	 * usedMap：用到的临时变量
	 * formula：用到的临时变量公式
	 * **/
	private void importMap(ArrayList varList,HashMap usedMap,String formula){

			for(int j=0;j<varList.size();j++){//循环所有的临时变量
				FieldItem _item=(FieldItem)varList.get(j);
				String _cname=_item.getItemid();
				String _chz=_item.getItemdesc();
				String formula1=_item.getFormula();//临时变量的计算公式
				if(!"1".equals(usedMap.get(_cname))){
					if(_cname!=null&&_cname.trim().length()>0&&formula.toLowerCase().indexOf(_cname.toLowerCase())!=-1){
						usedMap.put(_cname,"1");
						importMap(varList,usedMap,formula1);//临时变量调用临时变量时执行递归
					}
					else if(_chz!=null&&_chz.trim().length()>0&&formula.toLowerCase().indexOf(_chz.toLowerCase())!=-1&&!"1".equals(usedMap.get(_cname))){
						usedMap.put(_cname,"1");
						importMap(varList,usedMap,formula1);//临时变量调用临时变量时执行递归
					}
			    }
			}
	}



	/**
* 数值类型进行转换
* @param type
	 * @return
	 */
	private int getDataType(String type)
	{
		int datatype=0;
		switch(type.charAt(0))
		{
		case 'A':
			datatype=YksjParser.STRVALUE;
			break;
		case 'M':
			datatype=YksjParser.STRVALUE;
			break;
		case 'D':
			datatype=YksjParser.DATEVALUE;
			break;
		case 'N':
			datatype=YksjParser.FLOAT;
			break;
		}
		return datatype;
	}

	/**
	 * 初始设置使用字段列表
	 * @return
	 */
	private ArrayList initUsedFields()
	{
		ArrayList fieldlist=new ArrayList();

		if(this.infor_type==1)
		{
			/**人员排序号*/
			FieldItem fielditem=new FieldItem("A01","A0000");
			fielditem.setItemdesc("a0000");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("N");
			fielditem.setItemlength(9);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**人员编号*/
			fielditem=new FieldItem("A01","A0100");
			fielditem.setItemdesc("a0100");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("A");
			fielditem.setItemlength(8);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
		/**单位名称*/
			fielditem=new FieldItem("A01","B0110");
			fielditem.setItemdesc("单位名称");
			fielditem.setCodesetid("UN");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**姓名*/
			fielditem=new FieldItem("A01","A0101");
			fielditem.setItemdesc("姓名");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**人员排序号*/
			fielditem=new FieldItem("A01","I9999");
			fielditem.setItemdesc("I9999");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("N");
			fielditem.setItemlength(9);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/**部门名称*/
			fielditem=new FieldItem("A01","E0122");
			fielditem.setItemdesc("部门");
			fielditem.setCodesetid("UM");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
		}
		else if(this.infor_type==2)
		{
			/**排序号*/
			FieldItem fielditem=new FieldItem("B01","A0000");
			fielditem.setItemdesc("a0000");
        fielditem.setCodesetid("0");
			fielditem.setItemtype("N");
			fielditem.setItemlength(9);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);

			/**单位名称*/
			fielditem=new FieldItem("B01","B0110");
			fielditem.setItemdesc("单位ID");
			fielditem.setCodesetid("UN");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
		}
		else if(this.infor_type==3)
		{
			/**排序号*/
			FieldItem fielditem=new FieldItem("K01","A0000");
			fielditem.setItemdesc("a0000");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("N");
			fielditem.setItemlength(9);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);

			/**岗位名称*/
			fielditem=new FieldItem("K01","E01A1");
			fielditem.setItemdesc("岗位名称");
			fielditem.setCodesetid("@K");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
        fieldlist.add(fielditem);

		}
		return fieldlist;
	}

	/**
	 * 追加不同的指标
	 * @param slist
	 * @param dlist
	 */
	private void appendUsedFields(ArrayList slist,ArrayList dlist)
	{
		boolean bflag=false;
		for(int i=0;i<slist.size();i++)
		{
			FieldItem fielditem=(FieldItem)slist.get(i);
			String itemid=fielditem.getItemid();
			for(int j=0;j<dlist.size();j++)
			{
				bflag=false;
				FieldItem fielditem0=(FieldItem)dlist.get(j);
				String ditemid=fielditem0.getItemid();
				if(itemid.equalsIgnoreCase(ditemid))
				{
					bflag=true;
					break;
				}

			}//for j loop end.
			if(!bflag) {
                dlist.add(fielditem);
            }
		}//for i loop end.
	}

	/**
	 * 创建计算用的临时表
	 * @param fieldlist
	 * @param tablename
	 * @param keyfield
	 * @return
	 */
	private boolean createMidTable(ArrayList fieldlist,String tablename,String keyfield) throws GeneralException
	{
		boolean bflag=true;
		try
		{
			DbWizard dbw=new DbWizard(this.conn);

			//oracle表的命名规则:只能包括A-Z，a-z，0-9，_，$和#。  判断表名是否符合数据表的命名规范 liuzy 20150813
			String patternname ="^[a-zA-Z\u4e00-\u9fa5][A-Za-z0-9$#_\u4e00-\u9fa5]{0,29}$";
			if(!tablename.matches(patternname)){
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("kq.wizard.variable.notable")+tablename));
			}

			/*String regEx="[`~!@%^&*()+=|{}':;',//[//].<>/?~！@￥%……&*（）——+|{}【】‘；：”“’。，、？]";  //判断用户名是否存在特殊字符
			if(tablename.replaceAll(regEx, "").length()==0)
			{
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("kq.wizard.variable.notable")+tablename));
			}*/

			if(dbw.isExistTable(tablename, false)) {
                dbw.dropTable(tablename);
            }
			Table table=new Table(tablename);
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem fielditem=(FieldItem)fieldlist.get(i);
				Field field=fielditem.cloneField();
				if(field.getName().equalsIgnoreCase(keyfield))
				{
					field.setNullable(false);
					field.setKeyable(true);
				}
				table.addField(field);
			}//for i loop end.
			Field field=new Field("userflag","userflag");
			field.setLength(50);
			field.setDatatype(DataType.STRING);
			table.addField(field);
			dbw.createTable(table);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	/**
	 * 查询库前缀
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList searchDBPreList(String tablename)throws GeneralException
	{
		ArrayList dblist=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("select distinct basepre from ");
		//	buf.append(bz_tablename);
			buf.append(tablename);
			RowSet rset=dao.search(buf.toString());
			while(rset.next()) {
                dblist.add(rset.getString("basepre"));
            }
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
    throw GeneralExceptionHandler.Handle(ex);
		}
		return dblist;
	}


    /**查询库前缀
     * @param tablename 表名
     * @param strWhere 过滤条件 带where
     * @return
     * @throws GeneralException
     */
    private ArrayList searchDBPreList(String tablename,String strWhere )throws GeneralException
    {
        ArrayList dblist=new ArrayList();
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            StringBuffer buf=new StringBuffer();
            buf.append("select distinct basepre from ");
        //  buf.append(bz_tablename);
            buf.append(tablename);
            if (strWhere!=null && strWhere.length()>0){
                buf.append(strWhere);
            }
            RowSet rset=dao.search(buf.toString());
            while(rset.next()) {
                dblist.add(rset.getString("basepre"));
            }
            PubFunc.closeDbObj(rset);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return dblist;
    }











	/**
	 * 把临时变量增加到薪资表中去。
	 */
	public void addMidVarIntoGzTable(String strWhere,Object[] dbarr,ArrayList fieldlist)throws GeneralException
	{
		try
		{
			RecordVo vo=new RecordVo(this.bz_tablename);
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(this.bz_tablename);
			String tablename="t#"+this.userview.getUserName()+"_gz"; //this.userview.getUserName()+"midtable";
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			boolean bflag=false;
			if(fieldlist.size()==0) {
                return;
            }

			HashMap columnMap=new HashMap();
			RowSet rowSet=dao.search("select * from "+this.bz_tablename+" where 1=2");
			ResultSetMetaData md=rowSet.getMetaData();
			LazyDynaBean abean=null;
			for(int  i=1;i<=md.getColumnCount();i++)
			{
				 abean=new LazyDynaBean();
				int columnType=md.getColumnType(i);
				int size=md.getColumnDisplaySize(i);
				int scale=md.getScale(i);
				String type="A";
				 switch(columnType)
				 {
						 case java.sql.Types.INTEGER:
							type="N";
							break;
				 		case java.sql.Types.DOUBLE:
				 		case java.sql.Types.NUMERIC:
				 			type="N";
							break;
						 case java.sql.Types.DATE:
						 case java.sql.Types.TIMESTAMP:
						 case java.sql.Types.TIME :
						    type="D";
							break;
						 case java.sql.Types.CLOB:
						  case java.sql.Types.LONGVARCHAR:
						  case java.sql.Types.BLOB:
						  case java.sql.Types.LONGVARBINARY:
							  type="A";
							  break;
						  default:
							    type="A";
								break;
					}
				abean.set("columntype",type);
				abean.set("size",String.valueOf(size));
				abean.set("scale",String.valueOf(scale));
				columnMap.put(md.getColumnName(i).toLowerCase(),abean);
			}
			if(rowSet!=null) {
                rowSet.close();
            }

			ArrayList alterList=new ArrayList();
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				String fieldname=item.getItemid();
				//变量如果未加，则构建
				if(columnMap.get(fieldname.toLowerCase())!=null)
				{
					abean=(LazyDynaBean)columnMap.get(fieldname.toLowerCase());
					String type=(String)abean.get("columntype");
					String size=(String)abean.get("size");
					if(!type.equalsIgnoreCase(item.getItemtype()))
					{
						Field field=item.cloneField();
						bflag=true;
						table.addField(field);
					}
					else if("A".equalsIgnoreCase(type)|| "N".equalsIgnoreCase(type))
					{
						if(item.getItemlength()>Integer.parseInt(size))
						{
							alterList.add(item.cloneItem());
						//	bflag=true;
						//	table.addField(field);
						}
					}

				}
			}//for i loop end.
			if(bflag||alterList.size()>0)
			{
				if(bflag) {
                    dbw.dropColumns(table);
                }
				if(alterList.size()>0)
				{
					table=new Table(this.bz_tablename);
					if(Sql_switcher.searchDbServer()!=2)  //不为oracle
					{
						    for(int i=0;i<alterList.size();i++) {
                                table.addField(((FieldItem)alterList.get(i)).cloneField());
                            }
							if(alterList.size()>0) {
                                dbw.alterColumns(table);
                            }
							 table.clear();
					 }
					 else
					 {
						 for(int i=0;i<alterList.size();i++)
						 {
							 FieldItem _item=(FieldItem)alterList.get(i);
							 alertColumn(this.bz_tablename,_item,dbw,dao);
						 }
					 }
				}
				DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel(bz_tablename);
			}
			table=new Table(this.bz_tablename);
			vo=new RecordVo(this.bz_tablename);
			bflag=false;
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				String fieldname=item.getItemid();
				/**变量如果未加，则构建*/
				if(!vo.hasAttribute(fieldname.toLowerCase()))
				{
					Field field=item.cloneField();
					bflag=true;
					table.addField(field);
				}//if end.
			}//for i loop end.

			if(bflag)
			{
				dbw.addColumns(table);
				DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel(bz_tablename);
			}




			/**导入计算后的临时变量的值*/
			StringBuffer strFilter=new StringBuffer();
			String currym=ConstantParamter.getAppdate(this.userview.getUserName());
			String stry=currym.substring(0, 4);
			String strm=currym.substring(5, 7);
			String strc="1";
			YearMonthCount ymc=new YearMonthCount(Integer.parseInt(stry),Integer.parseInt(strm),Integer.parseInt(strc));
			/*
			HashMap midvarDefaultMap=new HashMap();
			if(this.midValue!=null&&this.midValue.trim().length()>0)
			{
				String[] temps=this.midValue.split(",");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i]!=null&&temps[i].length()>0)
					{
						String[] tmp=temps[i].split(":");
						midvarDefaultMap.put(tmp[0],tmp[1]);
					}
				}
			}
			*/

			if(this.infor_type==1)
			{
				/**按人员分库进行批量计算*/
				for(int i=0;i<dbarr.length;i++)
				{
					String dbpre=(String)dbarr[i];
					/**调入人员业务，不用计算变量*/
					if(dbpre==null||dbpre.length()==0) {
                        continue;
                    }
					for(int j=0;j<fieldlist.size();j++)
					{
						FieldItem item=(FieldItem)fieldlist.get(j);
						String fldtype=item.getItemtype();
						String fldname=item.getItemid();
						String formula= item.getFormula();
						String itemdesc=item.getItemdesc();
						if(formula==null||formula.trim().length()==0)
						{
						    /*
							String str="";
							if(midvarDefaultMap.get(itemdesc)!=null&&!((String)midvarDefaultMap.get(itemdesc)).equals("##"))
							{
								if(fldtype.equalsIgnoreCase("A"))
									str="'"+(String)midvarDefaultMap.get(itemdesc)+"'";
								else if(fldtype.equalsIgnoreCase("D"))
								{
									SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
									String _str=((String)midvarDefaultMap.get(itemdesc)).replaceAll("\\.","-");
									str=Sql_switcher.charToDate("'"+_str+"'");
								}
								else if(fldtype.equalsIgnoreCase("N"))
									str="'"+(String)midvarDefaultMap.get(itemdesc)+"'";
							}
							else
							{
								if(fldtype.equalsIgnoreCase("A"))
									str="''";
								else if(fldtype.equalsIgnoreCase("D"))
								{
									SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
									str=Sql_switcher.charToDate("'"+df.format(new Date())+"'");
								}
								else if(fldtype.equalsIgnoreCase("N"))
									str="0";
							}
							if(strWhere.length()==0)
								dao.update("update "+bz_tablename+" set "+fldname+"="+str+" where basepre='"+dbpre+"' ");
							else
							{
								dao.update("update "+bz_tablename+" set "+fldname+"="+str+" "+strWhere+" and basepre='"+dbpre+"' ");
							}
							*/
							continue;
						}
						if(formula.indexOf("取自于")!=-1)
						{
							continue;
						}
						ArrayList usedlist=initUsedFields();
            ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
								Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
						allUsedFields.addAll(fieldlist);
						YksjParser yp = new YksjParser(this.userview, allUsedFields,
								YksjParser.forSearch, getDataType(fldtype), YksjParser.forPerson, "Ht", dbpre);
						yp.setStdTmpTable(this.bz_tablename);
						yp.setTargetFieldDecimal(item.getDecimalwidth()); //why note this .chenmengqing added 20080322

                    /**追加公式中使用的指标*/
                    appendUsedFields(fieldlist,usedlist);
                    /**增加一个计算公式用的临时字段*/
                    FieldItem fielditem=new FieldItem("A01","AAAAA");
						fielditem.setItemdesc("AAAAA");
						fielditem.setCodesetid(item.getCodesetid());
						fielditem.setItemtype(fldtype);
						fielditem.setItemlength(item.getItemlength());
						fielditem.setDecimalwidth(item.getDecimalwidth());
						usedlist.add(fielditem);
						/**创建计算用临时表*/
						String tmptable="t#"+this.userview.getUserName()+"_gz"; //this.userview.getUserName()+"midtable";
						if(createMidTable(usedlist,tmptable,"A0100"))
						{
							/**导入人员主集数据A0100,A0000,B0110,E0122,A0101*/
							buf.setLength(0);
							buf.append("insert into ");
							buf.append(tablename);
							buf.append("(A0000,A0100,B0110,E0122,A0101) select A0000,A0100,B0110,E0122,A0101 FROM ");
							buf.append(dbpre+"A01");
							buf.append(" where A0100 in (select A0100 from ");
							buf.append(this.bz_tablename);
							if(strWhere.length()==0)
							{
								buf.append(" where basepre='");
								buf.append(dbpre);
								buf.append("'");
								/**计算临时变量的导入人员范围条件*/
								strFilter.append(" (select a0100 from ");
								strFilter.append(this.bz_tablename);
								strFilter.append(" where basepre='");
								strFilter.append(dbpre);
								strFilter.append("')");
							}
							else
							{
								buf.append(strWhere);
								buf.append(" and basepre='");
								buf.append(dbpre);
								buf.append("'");

								/**计算临时变量的导入人员范围条件*/
								strFilter.append(" (select a0100 from ");
								strFilter.append(this.bz_tablename);
								strFilter.append(" ");
								strFilter.append(strWhere);
								strFilter.append(" and basepre='");
								strFilter.append(dbpre);
								strFilter.append("')");

							}
							buf.append(")");
							dao.update(buf.toString());
			        /*      for(int m=0;m<fieldlist.size();m++)//传递临时变量的值 wangrd 2014-04-09
		                    {
		                        FieldItem item_m=(FieldItem)fieldlist.get(m);
		                        dbw.updateRecord(tablename,bz_tablename, tablename+".a0100"+"="+bz_tablename+".a0100",
		                                tablename+"."+item_m.getItemid()+"="+bz_tablename+"."+item_m.getItemid(),
		                                "", "");
		                    }  */
						}// 创建临时表结束.


						if(strWhere!=null&&strWhere.trim().length()>0) //2014-04-01 dengcan 为stdTmpTable添加数据条件
						{
							yp.setStdTmpTable_where(" and "+strWhere.substring(6));
						}
						yp.run(item.getFormula(),ymc,"AAAAA",tmptable,dao,strFilter.toString(),this.conn,fldtype,fielditem.getItemlength(),1,item.getCodesetid());
						buf.setLength(0);
						strFilter.setLength(0);
						if(strWhere.length()==0)
						{
							buf.append("where basepre='");
							buf.append(dbpre);
							buf.append("'");
						}
						else
						{
							buf.append(strWhere);
							buf.append(" and basepre='");
							buf.append(dbpre);
							buf.append("'");
						}

						/**前面去掉WHERE*/
						String strcond=buf.substring(6);


						if(yp.isStatMultipleVar())
						{
							StringBuffer set_str=new StringBuffer("");
							StringBuffer set_st2=new StringBuffer("");
							for(int e=0;e<yp.getStatVarList().size();e++)
							{
								String temp=(String)yp.getStatVarList().get(e);
								set_st2.append(","+temp+"=null");
								set_str.append(bz_tablename+"."+temp+"="+tablename+"."+temp);
								if(Sql_switcher.searchDbServer()==2) {
                                    set_str.append("`");
                                } else {
                                    set_str.append(",");
                                }
							}
							if(set_str.length()>0) {
                                set_str.setLength(set_str.length()-1);
                            } else {
                                continue;
                            }

							dao.update("update "+bz_tablename+" set "+set_st2.substring(1)+"   "+buf.toString());
							dbw.updateRecord(bz_tablename,tablename,bz_tablename+".A0100="+tablename+".A0100", set_str.toString(), strcond, strcond);
						}
						else {
                            dbw.updateRecord(bz_tablename,tablename,bz_tablename+".A0100="+tablename+".A0100", bz_tablename+"."+fldname+"="+tablename+".AAAAA", strcond, strcond);
                        }
					}//for j loop end.
				}//for i loop end.

			}
			else if(this.infor_type==2||this.infor_type==3)
			{

				for(int j=0;j<fieldlist.size();j++)
				{
					FieldItem item=(FieldItem)fieldlist.get(j);
					String fldtype=item.getItemtype();
					String fldname=item.getItemid();
					String formula= item.getFormula();
                if(formula==null||formula.trim().length()==0) {
                    continue;
                }
                if(formula.indexOf("取自于")!=-1)
					{
						continue;
					}
					ArrayList usedlist=initUsedFields();
					ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
							Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
					allUsedFields.addAll(fieldlist);

					int infoGroup=YksjParser.forUnit;
					String keyName="B0110";
					String mainset="B01";
					if(this.infor_type==3)
					{
						infoGroup=YksjParser.forPosition;
						keyName="E01A1";
						mainset="K01";
                            }




					YksjParser yp = new YksjParser(this.userview, allUsedFields,
							YksjParser.forSearch, getDataType(fldtype),infoGroup, "Ht", "");
					yp.setStdTmpTable(this.bz_tablename);
					yp.setTargetFieldDecimal(item.getDecimalwidth());

					/**追加公式中使用的指标*/
					appendUsedFields(fieldlist,usedlist);
					/**增加一个计算公式用的临时字段*/
					FieldItem fielditem=new FieldItem("B01","AAAAA");
					fielditem.setItemdesc("AAAAA");
					fielditem.setCodesetid(item.getCodesetid());
					fielditem.setItemtype(fldtype);
					fielditem.setItemlength(item.getItemlength());
					fielditem.setDecimalwidth(item.getDecimalwidth());
					usedlist.add(fielditem);
					/**创建计算用临时表*/
					String tmptable="t#"+this.userview.getUserName()+"_gz"; //this.userview.getUserName()+"midtable";
					if(createMidTable(usedlist,tmptable,keyName))
		 			{
						/**导入 主集数据 B0110 */
						buf.setLength(0);
						buf.append("insert into ");
						buf.append(tablename);
						buf.append("( "+keyName+") select "+keyName+" FROM ");
						buf.append(mainset);
						buf.append(" where "+keyName+" in (select "+keyName+" from ");
						buf.append(this.bz_tablename);
						if(strWhere.length()==0)
						{
							/**计算临时变量的导入人员范围条件*/
							strFilter.append(" (select "+keyName+" from ");
							strFilter.append(this.bz_tablename);
							strFilter.append(" )");
						}
						else
						{
							buf.append(strWhere);
							/**计算临时变量的导入人员范围条件*/
							strFilter.append(" (select "+keyName+" from ");
							strFilter.append(this.bz_tablename);
							strFilter.append(" ");
							strFilter.append(strWhere);
						    strFilter.append(" )");

						}
						buf.append(")");
						dao.update(buf.toString());
				/*		for(int m=0;m<fieldlist.size();m++)//传递临时变量的值 wangrd 2014-04-09
						{
						    FieldItem item_m=(FieldItem)fieldlist.get(m);
						    dbw.updateRecord(tablename,bz_tablename, tablename+"."+keyName+""+"="+bz_tablename+"."+keyName,
						            tablename+"."+item_m.getItemid()+"="+bz_tablename+"."+item_m.getItemid(),
						            "", "");
						}    */
					}// 创建临时表结束.

					if(strWhere!=null&&strWhere.trim().length()>0) //2014-04-01 dengcan 为stdTmpTable添加数据条件
					{
						yp.setStdTmpTable_where(" and "+strWhere.substring(6));
					}
					yp.run(item.getFormula(),ymc,"AAAAA",tmptable,dao,strFilter.toString(),this.conn,fldtype,fielditem.getItemlength(),1,item.getCodesetid());
					buf.setLength(0);
					strFilter.setLength(0);
					if(strWhere.length()==0)
					{

					}
					else
					{
						buf.append(strWhere);
					}

					/**前面去掉WHERE*/
					String strcond="";
					if(buf.toString().toUpperCase().startsWith("WHERE")) {
strcond=buf.substring(6);
                    }


					if(yp.isStatMultipleVar())
					{
						StringBuffer set_str=new StringBuffer("");
						StringBuffer set_st2=new StringBuffer("");
						for(int e=0;e<yp.getStatVarList().size();e++)
						{
							String temp=(String)yp.getStatVarList().get(e);
							set_st2.append(","+temp+"=null");
							set_str.append(bz_tablename+"."+temp+"="+tablename+"."+temp);
							if(Sql_switcher.searchDbServer()==2) {
                                set_str.append("`");
                            } else {
                                set_str.append(",");
                            }
						}
						if(set_str.length()>0) {
                            set_str.setLength(set_str.length()-1);
                        } else {
                            continue;
                        }

						dao.update("update "+bz_tablename+" set "+set_st2.substring(1)+"   "+buf.toString());
						dbw.updateRecord(bz_tablename,tablename,bz_tablename+"."+keyName+"="+tablename+"."+keyName+"", set_str.toString(), strcond, strcond);
					}
					else
						//dbw.updateRecord(bz_tablename,tablename,bz_tablename+"."+keyName+"="+tablename+"."+keyName+"", bz_tablename+"."+fldname+"="+tablename+".AAAAA", strcond, strcond);
                    {
                        dbw.updateRecord(bz_tablename,tablename,bz_tablename+"."+keyName+"="+tablename+"."+keyName+"",  bz_tablename+"."+fldname+"="+tablename+".AAAAA", strcond, strcond);
                    }
				}//for j loop end.
			}


		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	/**
	 * 查询标准表指标列表
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList searchStdTableFieldList()throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			/**查询执行标准的计算公式*/
			ContentDAO dao=new ContentDAO(this.conn);
			if(gz_stand.length==0) {
                return fieldlist;
            }
			StringBuffer stdbuf=new StringBuffer();
			for(int i=0;i<gz_stand.length;i++)
			{
				stdbuf.append(gz_stand[i]);
				stdbuf.append(",");
			}//for i loop end.
			stdbuf.setLength(stdbuf.length()-1);
			buf.setLength(0);
			/**薪资标准表*/
			buf.append("select id from gz_stand where id in(");
			buf.append(stdbuf.toString());
			buf.append(")");
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				//...薪资标准表涉及到指标列表
				SalaryStandardBo stdbo=new SalaryStandardBo(this.conn,rset.getString("id"),"");
				fieldlist.addAll(stdbo.getGzStandFactorList(1));
				fieldlist.addAll(stdbo.getGzStandFactorList(2));

			}// while loop end.
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return fieldlist;
	}

	/**
	 * 取得字段列表中包括子集名列表
	 * @param fieldlist
	 * @return
	 */
	private List getSetListByStd(ArrayList fieldlist)
	{
		List setlist=null;
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem item=(FieldItem)fieldlist.get(i);
			String setid=item.getFieldsetid();
			if(buf.indexOf(setid)==-1)
			{
				buf.append(setid);
				buf.append(",");
			}//if end.
		}//for i loop end.
		if(buf.length()>0)
		{
			String[] setarr=StringUtils.split(buf.toString(),",");
			setlist=Arrays.asList(setarr);
		}
		return setlist;
	}

	/**得到子集中的所有记录 flag=0:普通子集，只取最后一条记录  =1：兼职子集，所有记录都要取 郭峰*/
	public LinkedHashMap getAllRecordsMap(String xml,int flag){
		LinkedHashMap map = new LinkedHashMap();
		try{
			if(xml==null || "".equals(xml)){
				return map;
			}
			Document doc=PubFunc.generateDom(xml);;
			Element element=null;
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String xpath="/records";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			element =(Element) findPath.selectSingleNode(doc);
			if(element!=null){
				List list = element.getChildren("record");
				if(flag==0){//只取最后一条
					if(list.size()>0){
						boolean isBreak = false;
						for(int j=list.size()-1;j>=0;j--){
							Element temp = (Element) list.get(j);//最后一条记录
							String i9999 = temp.getAttributeValue("I9999");
							String state = temp.getAttributeValue("state");
							String fieldsetvalue = "";
							if(state!=null && "D".equalsIgnoreCase(state)){//如果当前最后一条记录是删除的记录，那么找上一条。如果上一条也被删除了，找上上一条
								if(j>0){//找上一条
									continue;
								}
								String tempstr = temp.getText();
								String[] array = tempstr.split("`");
								StringBuffer sb = new StringBuffer("");
								for(int m=0;m<array.length;m++){
									sb.append("emptyvalue`");
								}
								if(sb.length()>0) {
                                    sb.setLength(sb.length()-1);
                                }
								fieldsetvalue = sb.toString();
							}else{
								isBreak = true;
								fieldsetvalue = temp.getText();
							}
							map.put(i9999, fieldsetvalue);
							if(isBreak){
								break;
							}
						} //for loop end
					} //list.size()>0 结束
				}else if(flag==1){//取所有记录（兼职子集专用）
					for (int i = 0; i < list.size(); i++){
						Element temp = (Element) list.get(i);
						String i9999 = temp.getAttributeValue("I9999");
						String state = temp.getAttributeValue("state");
						String deleteflag = "0";
						if("D".equals(state)){
							deleteflag = "1";
						}
						if("-1".equals(i9999) && "1".equals(deleteflag)){//如果我自己新增了一条子集记录，然后又删除了
							continue;
						}
						String fieldsetvalue = temp.getText();
						String tempkey = i9999+"`"+deleteflag;
						map.put(tempkey, fieldsetvalue);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}

	/**得到是新增还是修改  用于编制控制  郭峰
	 * currentOperation:模板处理类型
	 * dbs：对哪个库进行编制控制
	 * _srcbase：调入型模板的目标库
	 * destBase：移库型模板的目标库
	 * return: =0修改  =1新增   =2不控制
	 * */
	public String getAddFlag(int currentOperation,String dbs,String srcbase,String destBase){
		if("".equals(dbs)){
			return "2";
		}
		dbs=dbs.toLowerCase();//为了兼容老数据中USR与现在数据中Usr人员库标识不一的情况做，所以改成这个模式
		srcbase=srcbase.toLowerCase();
		String addFlag = "1";
		if(currentOperation==0){//人员调入型
			if(dbs.indexOf(","+srcbase+",")!=-1){//目标库是编制库时
				addFlag = "1";
			}else{
				addFlag = "2";
			}
		}else if(currentOperation ==1 || currentOperation ==2|| currentOperation == 4){//人员调出、离退型
			if("".equals(destBase)){
				return "2";
			}else{
				if(dbs.indexOf(","+destBase.toLowerCase()+",")==-1){
					return "2";
				}else{
					if(dbs.indexOf(","+srcbase+",")==-1){//人员当前库为非编制库
						addFlag = "1";
					}else{
						addFlag = "0";
					}
				}
			}
		}else if(currentOperation == 3  || currentOperation == 10){//内部变动、其他变动、系统内调动
			if(dbs.indexOf(","+srcbase+",")!=-1){//人员所在库是编制库时
				addFlag = "0";
			}else{
				addFlag = "2";
			}
		}
		return addFlag;
	}

	/**人员移库模板 新增时，得到bean的list*/
	public ArrayList getNbaseA0100List(String querysql,String destinationDb){
		ArrayList list = new ArrayList();//格式：人员库`a0100`目标库
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;

		    rs = dao.search(querysql);
		    while(rs.next()){
		    	String srcDb = rs.getString("basepre");
		    	String a0100 = rs.getString("a0100");
		    	String str = srcDb.toLowerCase()+"`"+a0100+"`"+destinationDb;
		    	list.add(str);
		    }
		    if(rs!=null){
		    	rs.close();
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}

	/**得到目标库（非离退型模板）。同时它又是源库*/
	public String getDestinationDb(String sql){
		String destinationDb = "";
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
		    rs = dao.search(sql);
		    if(rs.next()){
		    	destinationDb = rs.getString("basepre");//进入时目标库
		    }
		    if(rs!=null){
		    	rs.close();
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
		return destinationDb;
	}

	/**得到各自需要的指标
	 * str[0]:普通子集需要的指标
	 * str[1]兼职子集需要的指标
	 * */
	public String[] getNeedField(ArrayList common_hasFieldList,ArrayList part_hasFieldList){
		String[] str = new String[2];
		StringBuffer str_common = new StringBuffer("");//普通子集需要的指标
		StringBuffer str_parttime = new StringBuffer("");//兼职子集需要的指标
		for(int i=0;i<3;i++){
			String temp_common = (String)common_hasFieldList.get(i);//普通子集的指标值
			String temp_parttime = (String)part_hasFieldList.get(i);//普通子集的指标值
			if(temp_common.equals(temp_parttime)){
				continue;
			}
			if("-1".equals(temp_common)){
				str_common.append(temp_parttime+",");
			}else if("-1".equals(temp_parttime)){
				str_parttime.append(temp_common+",");
			}
		}
		if(str_common.length()>0){
			str_common.setLength(str_common.length()-1);
		}
		if(str_parttime.length()>0){
			str_parttime.setLength(str_parttime.length()-1);
		}
		str[0] = str_common.toString();
		str[1] = str_parttime.toString();
		return str;
	}

	/**返回初始化的list
	 * flag:0普通子集   1兼职子集
	 * */
	public ArrayList initFieldList(int flag,ArrayList datalist,String part_unit,String part_depart,String part_pos){
		ArrayList toreturnlist = new ArrayList();
		if(datalist.size()<=0){
			toreturnlist.add(0,"-1");
			toreturnlist.add(1,"-1");
			toreturnlist.add(2,"-1");
			return toreturnlist;
		}
		if(flag==0){
			if(datalist.contains("b0110")){
				toreturnlist.add(0,"b0110");
			}else{
				toreturnlist.add(0,"-1");
			}

			if(datalist.contains("e0122")){
				toreturnlist.add(1,"e0122");
			}else{
				toreturnlist.add(1,"-1");
			}

			if(datalist.contains("e01a1")){
				toreturnlist.add(2,"e01a1");
			}else{
				toreturnlist.add(2,"-1");
}
		}else if(flag==1){
			if(datalist.contains(part_unit)){
				toreturnlist.add(0,"b0110");
			}else{
				toreturnlist.add(0,"-1");
			}

			if(datalist.contains(part_depart)){
				toreturnlist.add(1,"e0122");
			}else{
				toreturnlist.add(1,"-1");
			}

			if(datalist.contains(part_pos)){
				toreturnlist.add(2,"e01a1");
			}else{
				toreturnlist.add(2,"-1");
			}
		}

		return toreturnlist;
	}
	/**得到template_table中子集更新或新增记录的类型*/
	public String getUpdateSetType(String part_set,String xml){
		String type = "";
		try{
			HashMap recordTypeMap = new HashMap();
			Document doc=PubFunc.generateDom(xml);
			Element element=null;
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String xpath="/params/updates";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			element =(Element) findPath.selectSingleNode(doc);
			if(element!=null){
				List list = element.getChildren("update");
				for (int i = 0; i < list.size(); i++){
					Element temp = (Element) list.get(i);
					String name = temp.getAttributeValue("Name").toLowerCase();
					String temptype = temp.getAttributeValue("Type");
					recordTypeMap.put(name, temptype);
				}
			}
			type = (String)recordTypeMap.get(part_set);
		}catch(Exception e){
			e.printStackTrace();
		}
		return type;
	}

	/**得到兼职子集记录的i9999
	 * part_set:兼职子集
	 * */
	public String getPart_i9999(String part_set,String tabid,String nbase){
		String i9999 = "-1";
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			String xml = "";
			StringBuffer sb = new StringBuffer("");
		/*
		sb.append("select ctrl_para from template_table where tabid="+tabid);
			rs = dao.search(sb.toString());
			if(rs.next()){
				xml = Sql_switcher.readMemo(rs, "ctrl_para");
			}
			*/
			RecordVo tabvo=TemplateStaticDataBo.getTableVo(Integer.parseInt(tabid), conn); //20171111 邓灿，采用缓存解决并发下压力过大问题
			if(tabvo!=null)
			{
				xml=tabvo.getString("ctrl_para")!=null?tabvo.getString("ctrl_para"):"";
			}


			//解析到子集的更新记录方式
			String type = getUpdateSetType(part_set.toLowerCase(),xml);
			if("2".equals(type)){//更新
				//得到子集记录中的最大的i9999
				sb.setLength(0);
				sb.append("select max(i9999) i9999 from "+nbase+part_set);
				rs = dao.search(sb.toString());
				if(rs.next()){
					i9999 = rs.getString("i9999");
				}
			}
			if(rs!=null){
				rs.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return i9999;
	}

	/**判断兼职指标以何种方式维护
	 * afterFields
	 * temp_map
	 * return:  flag=0:以罗列指标的形式   =1：以兼职子集的形式
	 * */
	public String getPartType(HashMap temp_map){
		String flag = "0";
		ScanFormationBo scanFormationBo=new ScanFormationBo(this.conn,this.userview);
		if(scanFormationBo.getPart_setid()==null){
			return "1";
		}
		String parttime_job = scanFormationBo.getPart_setid().toLowerCase();//兼职子集
		if(temp_map.get(parttime_job)!=null){
			flag = "1";
		}else{
			flag = "0";
		}
		return flag;
	}

	/**得到存储bean的list。（此方法适用的情况是：兼职指标以单独的指标出现）编制控制最重要的方法
	 * currentOperation:模板类型
	 * addFlag：1新增 0修改 2不控制
	 * querysql：查询的sql语句
	 * destinationDb：目标库
	 * srcDb：源人员库
	 * afterFields:变化后的指标（仅仅是指标）
	 * temp_map:所有的子集与指标list的对应关系。指标存放在List中
	 * */
	public ArrayList getBeanList_1(int currentOperation,String addFlag,String querysql,String destinationDb,String srcDb,ArrayList afterFields,HashMap temp_map,String tabid){
		ArrayList beanlist = new ArrayList();
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			ScanFormationBo scanFormationBo=new ScanFormationBo(this.conn,this.userview);

			if((currentOperation ==1 || currentOperation ==2|| currentOperation == 4) && "1".equals(addFlag)){//移库 且 新增
				ArrayList nbaseA0100List = getNbaseA0100List(querysql,destinationDb.toLowerCase());
				beanlist = scanFormationBo.getMoveAddPersonData(nbaseA0100List);
				if(afterFields!=null&&afterFields.size()>0){
					//String part_flag = scanFormationBo.getPart_flag();//是否启用了兼任兼职
					//String part_unit = scanFormationBo.getPart_unit();//兼职单位
					//String part_depart = scanFormationBo.getPart_dept();//兼职部门
					//String part_pos = scanFormationBo.getPart_pos();//兼职岗位
					//String part_appoint = scanFormationBo.getPart_appoint();//任免指标
					rs = dao.search(querysql.toString());
					while(rs.next()){
						LazyDynaBean bean = null;
						//LazyDynaBean temp_bean = null;
						String a0100 = rs.getString("a0100");
						for(int j=0;j<beanlist.size();j++){//要考虑兼职信息
							LazyDynaBean _bean = (LazyDynaBean)beanlist.get(j);
							String _basepre = (String)_bean.get("nbase");
							String _a0100 = (String)_bean.get("a0100");
							String ispart = (String)_bean.get("ispart");
							if((destinationDb.toLowerCase()+a0100).equals(_basepre+_a0100)){
								if("0".equals(ispart)) {
                                    bean = _bean;
                                }
								//else
									//temp_bean =_bean;
							}
						}
						for(int i=0;i<afterFields.size();i++){
							String temp_field = (String)afterFields.get(i);
							FieldItem item = null;
							item = DataDictionary.getFieldItem(temp_field.toUpperCase());
							if(item==null) {
                                continue;
                            }
				    		String temp_value = "";
				    		temp_value = rs.getString(item.getItemid()+"_2");
				    		if(temp_value==null||"".equals(temp_value)) {
                                continue;
                            }
				    		if(bean!=null){
					    		if("b0110".equalsIgnoreCase(temp_field)&&bean.getMap().containsKey("b0110")){
					    			bean.set("b0110", temp_value);
					    		}
					    		else if("e0122".equalsIgnoreCase(temp_field)&&bean.getMap().containsKey("e0122")){
									bean.set("e0122", temp_value);
								}
					    		else if("e01a1".equalsIgnoreCase(temp_field)&&bean.getMap().containsKey("e01a1")){
									bean.set("e01a1", temp_value);
								}
				    		}
				    		//目前移库操作暂不考虑支持兼职子集的变更,因为从兼职子集表中可能会拿到多条记录,进而不知道去更改那一条,暂时还没考虑清楚怎么解决 2017-9-8
				    		/*if(temp_bean!=null&&"true".equals(part_flag)){
				    			if(temp_field.equalsIgnoreCase(part_unit)){
				    				temp_bean.set("b0110", temp_value);
					    			temp_bean.set(temp_field, temp_value);
					    		}
				    			else if(temp_field.equalsIgnoreCase(part_depart)){
				    				temp_bean.set("e0122", temp_value);
									temp_bean.set(temp_field, temp_value);
								}
				    			else if(temp_field.equalsIgnoreCase(part_pos)){
				    				temp_bean.set("e01a1", temp_value);
									temp_bean.set(temp_field, temp_value);
								}
				    		}*/
						}
					}
				}
			}else{//需要自己凑bean
				String part_flag = scanFormationBo.getPart_flag();//是否启用了兼任兼职
				String part_set = "";//兼职子集
				if(scanFormationBo.getPart_setid()==null){
					part_set="#**#***#***";
				}else{
					part_set = scanFormationBo.getPart_setid().toLowerCase();
				}
				String part_unit = scanFormationBo.getPart_unit();//兼职单位
				String part_depart = scanFormationBo.getPart_dept();//兼职部门
				String part_pos = scanFormationBo.getPart_pos();//兼职岗位
				String part_appoint = scanFormationBo.getPart_appoint();//任免指标
				//兼职另起一个bean时用到的指标
				ArrayList beanFieldList = new ArrayList();
				if("true".equals(part_flag)){
					if(afterFields.contains(part_unit)){
						beanFieldList.add(part_unit);
					}
					if(afterFields.contains(part_depart)){
						beanFieldList.add(part_depart);
					}
					if(afterFields.contains(part_pos)){
						beanFieldList.add(part_pos);
					}
					if(afterFields.contains(part_appoint)){
						beanFieldList.add(part_appoint);
					}
				}
				if(beanFieldList.size()<=0){//只有1个bean
					rs = dao.search(querysql.toString());
				    while(rs.next()){
				    	LazyDynaBean bean = new LazyDynaBean();//普通子集的bean
				    	//几个固定参数赋值
				    	bean.set("i9999", "-1");
				    	bean.set("addflag", addFlag);
				    	bean.set("ispart", "0");
				    	bean.set("objecttype", "1");
				    	bean.set("nbase", srcDb);
				    	bean.set("a0100", rs.getString("a0100"));

				    	//先给所有的指标赋值
				    	for(int j=0;j<afterFields.size();j++){
				    		String temp_field = (String)afterFields.get(j);
				    		FieldItem item = DataDictionary.getFieldItem(temp_field.toUpperCase());
				    		String temp_value = null;
				    		temp_value = getDataByType(rs,item);
				    		item = null;
				    		bean.set(temp_field.toLowerCase(), temp_value);
				    	}
				    	//再给所有的普通子集的指标赋值
				    	Set key = temp_map.keySet();
				    	for (Iterator it = key.iterator(); it.hasNext();) {//遍历所有的子集
							String s = (String) it.next();//子集的名称
							ArrayList innerlist = (ArrayList)temp_map.get(s);//子集下所有的指标
							String afterField = "T_"+s.toUpperCase()+"_2";
							String tempXml = Sql_switcher.readMemo(rs, afterField);
							LinkedHashMap recordMap = getAllRecordsMap(tempXml,0);//得到最后一条记录   recordMap以i9999为键
							Set key3 = recordMap.keySet();
							for (Iterator it3 = key3.iterator(); it3.hasNext();) {//遍历记录（遍历多个i9999）
								String temp_key = (String)it3.next();//i9999
								String fieldvalue = (String)recordMap.get(temp_key);//当前i9999对应的记录值
								if(fieldvalue.length()>0){
									String[] temparray = fieldvalue.split("`");
									//innerlist的size和temparray的size是相同的，并且顺序是对应的。
									for(int k=0;k<innerlist.size();k++){
										String temporary_field = (String)innerlist.get(k);//指标名称
										String temporary_value = null;
										if(k<temparray.length){
											temporary_value = temparray[k];//指标值
											if("emptyvalue".equals(temporary_value)){
												temporary_value = null;
											}
										}
										bean.set(temporary_field, temporary_value);
									}
								}
							} //for loop
						} //给普通子集的指标赋值 结束

				    	beanlist.add(bean);
				    } //while 结束
				} //只有1个bean 结束
				else{ //beanFieldList.size()>0，即有两个bean
					String part_i9999 = "";//查出兼职指标的i9999（从数据更新方式中去查）
					part_i9999 = getPart_i9999(part_set,tabid,srcDb);
					ArrayList newlist = new ArrayList();//除去兼职指标后的模板中的所有变化后指标
					for(int i=0;i<afterFields.size();i++){
						String tempstr = (String)afterFields.get(i);
						if(tempstr.equals(part_unit) || tempstr.equals(part_depart) || tempstr.equals(part_pos) || tempstr.equals(part_appoint)){
							continue;
						}
						newlist.add(afterFields.get(i));
					}
					ArrayList common_hasFieldList = new ArrayList();//普通子集有哪个指标（只考虑b0110,e0122,e01a1）
					ArrayList part_hasFieldList = new ArrayList();//兼职子集有哪个指标（只考虑b0110,e0122,e01a1）
					String common_needField = "";//普通子集相对于兼职子集还缺少哪个指标（只考虑b0110,e0122,e01a1）
					String part_needField = "";//兼职子集相对于普通子集还缺少哪个指标（只考虑b0110,e0122,e01a1）
					common_hasFieldList = initFieldList(0,newlist,"","","");
					part_hasFieldList = initFieldList(1,beanFieldList,part_unit,part_depart,part_pos);
					String[] temp_field_array = getNeedField(common_hasFieldList,part_hasFieldList);
					common_needField = temp_field_array[0];
					part_needField = temp_field_array[1];
					rs = dao.search(querysql.toString());
				    while(rs.next()){
				    	LazyDynaBean bean = new LazyDynaBean();//普通子集的bean
				    	LazyDynaBean temp_bean = new LazyDynaBean();//兼职指标的bean
				    	//几个固定参数赋值
				    	bean.set("i9999", "-1");
				    	bean.set("addflag", addFlag);
				    	bean.set("ispart", "0");
				    	bean.set("objecttype", "1");
				    	bean.set("nbase", srcDb);
				    	bean.set("a0100", rs.getString("a0100"));

				    	temp_bean.set("i9999", part_i9999);
				    	temp_bean.set("addflag", addFlag);
				    	temp_bean.set("ispart", "1");
				    	temp_bean.set("objecttype", "1");
				    	temp_bean.set("nbase", srcDb);
				    	temp_bean.set("a0100", rs.getString("a0100"));

				    	//b0110,e0122,e01a1这三个值。空缺的就补上
				    	if("1".equals(addFlag)){//新增
				    		if(common_needField.length()>0){
						    	String[] splitarray = common_needField.split(",");
						    	for(int m=0;m<splitarray.length;m++){
						    		bean.set(splitarray[m], "");
						    	}
					    	}
						}else{//修改
							if(common_needField.length()>0){
								bean = scanFormationBo.getPartOrMainOrg(bean,common_needField);
							}
						} //修改 结束

				    	if("-1".equals(part_i9999)){//新增
							if(part_needField.length()>0){
						    	String[] splitarray = part_needField.split(",");
						    	for(int m=0;m<splitarray.length;m++){
						    		temp_bean.set(splitarray[m], "");
						    	}
					    	}
						}else{//修改
							if(part_needField.length()>0){
								temp_bean = scanFormationBo.getPartOrMainOrg(temp_bean,part_needField);
							}
						} //修改 结束


				    	//开始处理所有的指标
				    	for(int j=0;j<afterFields.size();j++){
				    		String temp_field = (String)afterFields.get(j);
				    		FieldItem item = DataDictionary.getFieldItem(temp_field.toUpperCase());
				    		String temp_value = null;
				    		temp_value = getDataByType(rs,item);
				    		item = null;
				    		bean.set(temp_field.toLowerCase(), temp_value);
				    	}

				    	//开始处理所有的普通子集
				    	Set key = temp_map.keySet();
				    	for (Iterator it = key.iterator(); it.hasNext();) {//遍历所有的子集
							String s = (String) it.next();//子集的名称
							ArrayList innerlist = (ArrayList)temp_map.get(s);//子集下所有的指标
							String afterField = "T_"+s.toUpperCase()+"_2";
							String tempXml = Sql_switcher.readMemo(rs, afterField);
							LinkedHashMap recordMap = getAllRecordsMap(tempXml,0);//得到最后一条记录   recordMap以i9999为键
							Set key3 = recordMap.keySet();
							for (Iterator it3 = key3.iterator(); it3.hasNext();) {//遍历记录（遍历多个i9999）
								String temp_key = (String)it3.next();//i9999
								String fieldvalue = (String)recordMap.get(temp_key);//当前i9999对应的记录值
								if(fieldvalue.length()>0){
									String[] temparray = fieldvalue.split("`");
									//innerlist的size和temparray的size是相同的，并且顺序是对应的。
									for(int k=0;k<innerlist.size();k++){
										String temporary_field = (String)innerlist.get(k);//指标名称
										String temporary_value = null;
										if(k<temparray.length){
											temporary_value = temparray[k];//指标值
											if("emptyvalue".equals(temporary_value)){
												temporary_value = null;
											}
										}
										bean.set(temporary_field, temporary_value);
										temp_bean.set(temporary_field, temporary_value);
									}
								}
							}
						} //给普通子集的指标赋值 结束

				    	//把兼职单位转化为b0110
				    	for(int j=0;j<afterFields.size();j++){
				    		String temp_field = (String)afterFields.get(j);
				    		if("b0110".equalsIgnoreCase(temp_field) || "e0122".equalsIgnoreCase(temp_field) || "e01a1".equalsIgnoreCase(temp_field)){
				    			continue;
				    		}
				    		FieldItem item = DataDictionary.getFieldItem(temp_field.toUpperCase());
				    		String temp_value = null;
				    		temp_value = getDataByType(rs,item);
				    		item = null;
				    		if(temp_field.equalsIgnoreCase(part_unit)){//把兼职单位转换为b0110
								temp_bean.set("b0110", temp_value);
								temp_bean.set(temp_field, temp_value);
							}else if(temp_field.equalsIgnoreCase(part_depart)){
								temp_bean.set("e0122", temp_value);
								temp_bean.set(temp_field, temp_value);
							}else if(temp_field.equalsIgnoreCase(part_pos)){
								temp_bean.set("e01a1", temp_value);
								temp_bean.set(temp_field, temp_value);
							}else{
								temp_bean.set(temp_field, temp_value);
							}
				    	}
				    	beanlist.add(bean);
				    	beanlist.add(temp_bean);
				    } //while end
				} ////beanFieldList.size()>0 结束
			    if(rs!=null){
			    	rs.close();
			    }
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return beanlist;
	}

	/**得到存储bean的list。（此方法适用的情况是：兼职指标全部放在兼职子集中维护。不可能以单独的指标出现）编制控制最重要的方法
	 * currentOperation:模板类型
	 * addFlag：1新增 0修改 2不控制
	 * querysql：查询的sql语句
	 * destinationDb：目标库
	 * srcDb：源人员库
	 * afterFields:变化后的指标（仅仅是指标）
	 * temp_map:所有的子集与指标list的对应关系。指标存放在List中
	 * */
	public ArrayList getBeanList_2(int currentOperation,String addFlag,String querysql,String destinationDb,String srcDb,ArrayList afterFields,HashMap temp_map){
		ArrayList beanlist = new ArrayList();
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			ScanFormationBo scanFormationBo=new ScanFormationBo(this.conn,this.userview);

			if((currentOperation ==1 || currentOperation ==2|| currentOperation == 4) && "1".equals(addFlag)){//移库 且 新增
				ArrayList nbaseA0100List = getNbaseA0100List(querysql,destinationDb.toLowerCase());
				beanlist = scanFormationBo.getMoveAddPersonData(nbaseA0100List);
				if(afterFields!=null&&afterFields.size()>0){
					//String part_flag = scanFormationBo.getPart_flag();//是否启用了兼任兼职
					//String part_unit = scanFormationBo.getPart_unit();//兼职单位
					//String part_depart = scanFormationBo.getPart_dept();//兼职部门
					//String part_pos = scanFormationBo.getPart_pos();//兼职岗位
					//String part_appoint = scanFormationBo.getPart_appoint();//任免指标
					rs = dao.search(querysql.toString());
					while(rs.next()){
						LazyDynaBean bean = null;
						//LazyDynaBean temp_bean = null;
						String a0100 = rs.getString("a0100");
						for(int j=0;j<beanlist.size();j++){//要考虑兼职信息
							LazyDynaBean _bean = (LazyDynaBean)beanlist.get(j);
							String _basepre = (String)_bean.get("nbase");
							String _a0100 = (String)_bean.get("a0100");
							String ispart = (String)_bean.get("ispart");
							if((destinationDb.toLowerCase()+a0100).equals(_basepre+_a0100)){
								if("0".equals(ispart)) {
                                    bean = _bean;
                                }
								//else
									//temp_bean =_bean;
							}
						}
						for(int i=0;i<afterFields.size();i++){
							String temp_field = (String)afterFields.get(i);
							FieldItem item = null;
							item = DataDictionary.getFieldItem(temp_field.toUpperCase());
							if(item==null) {
                                continue;
                            }
				    		String temp_value = "";
				    		temp_value = rs.getString(item.getItemid()+"_2");
				    		if(temp_value==null||"".equals(temp_value)) {
                                continue;
                            }
				    		if(bean!=null){
					    		if("b0110".equalsIgnoreCase(temp_field)&&bean.getMap().containsKey("b0110")){
					    			bean.set("b0110", temp_value);
					    		}
					    		else if("e0122".equalsIgnoreCase(temp_field)&&bean.getMap().containsKey("e0122")){
									bean.set("e0122", temp_value);
								}
					    		else if("e01a1".equalsIgnoreCase(temp_field)&&bean.getMap().containsKey("e01a1")){
									bean.set("e01a1", temp_value);
								}
				    		}
				    		//目前移库操作暂不考虑支持兼职子集的变更,因为从兼职子集表中可能会拿到多条记录,进而不知道去更改那一条,暂时还没考虑清楚怎么解决 2017-9-8
				    		/*if(temp_bean!=null&&"true".equals(part_flag)){
				    			if(temp_field.equalsIgnoreCase(part_unit)){
				    				temp_bean.set("b0110", temp_value);
					    			temp_bean.set(temp_field, temp_value);
					    		}
				    			else if(temp_field.equalsIgnoreCase(part_depart)){
				    				temp_bean.set("e0122", temp_value);
									temp_bean.set(temp_field, temp_value);
								}
				    			else if(temp_field.equalsIgnoreCase(part_pos)){
				    				temp_bean.set("e01a1", temp_value);
									temp_bean.set(temp_field, temp_value);
								}
				    		}*/
						}
					}
				}
			}else{//自己凑bean
				//六个必须的参数nbase,a0100,objecttype(固定为1),addflag,ispart（普通子集还是兼职）,i9999(以上为bean中必填字段)
				String part_flag = scanFormationBo.getPart_flag();//是否启用了兼任兼职
				String parttime_job = "";//兼职子集
				if(scanFormationBo.getPart_setid()==null){
					parttime_job="#**#***#***";
				}else{
					parttime_job = scanFormationBo.getPart_setid().toLowerCase();
				}
				String part_unit = scanFormationBo.getPart_unit();//兼职单位
				String part_depart = scanFormationBo.getPart_dept();//兼职部门
				String part_pos = scanFormationBo.getPart_pos();//兼职岗位
				String part_appoint = scanFormationBo.getPart_appoint();//任免指标
				boolean partHasRecord = false;//兼职子集是否有记录
				boolean isHasAppointField = false;//子集中是否有任免标识这个指标
				boolean partHasDeletedRecord = false;//兼职子集是否有删除的记录
				ArrayList common_hasFieldList = new ArrayList();//普通子集有哪个指标（只考虑b0110,e0122,e01a1）
				ArrayList part_hasFieldList = new ArrayList();//兼职子集有哪个指标（只考虑b0110,e0122,e01a1）
				String common_needField = "";//普通子集相对于兼职子集还缺少哪个指标（只考虑b0110,e0122,e01a1）
				String part_needField = "";//兼职子集相对于普通子集还缺少哪个指标（只考虑b0110,e0122,e01a1）
				common_hasFieldList = initFieldList(0,afterFields,"","","");
				ArrayList parttimelist = (ArrayList)temp_map.get(parttime_job);//兼职子集涉及到的所有的指标
				if(parttimelist!=null && parttimelist.contains(part_appoint)){
					isHasAppointField = true;
				}
				if(parttimelist!=null){//有兼职子集，并且兼职子集有记录（兼职子集是否有记录在后面考虑，因为查库后才知道）
					part_hasFieldList = initFieldList(1,parttimelist,part_unit,part_depart,part_pos);
					String[] temp_field_array = getNeedField(common_hasFieldList,part_hasFieldList);
					common_needField = temp_field_array[0];
					part_needField = temp_field_array[1];
				}
				HashMap mapexceptparttime = (HashMap)temp_map.clone();//除去兼职子集的所有子集的map。键全为小写
				if(mapexceptparttime.containsKey(parttime_job)){
					mapexceptparttime.remove(parttime_job);
				}
			    rs = dao.search(querysql.toString());

			    while(rs.next()){

			    	LazyDynaBean bean = new LazyDynaBean();//普通子集的bean
			    	//几个固定参数赋值
			    	bean.set("i9999", "-1");
			    	bean.set("addflag", addFlag);
			    	bean.set("ispart", "0");
			    	bean.set("objecttype", "1");
			    	bean.set("nbase", srcDb);
			    	bean.set("a0100", rs.getString("a0100"));

			    	//先给所有的指标赋值
			    	for(int j=0;j<afterFields.size();j++){
			    		String temp_field = (String)afterFields.get(j);
			    		FieldItem item = DataDictionary.getFieldItem(temp_field.toUpperCase());
			    		String temp_value = null;
			    		temp_value = getDataByType(rs,item);
			    		item = null;
			    		bean.set(temp_field.toLowerCase(), temp_value);
			    	}
			    	//再给所有的普通子集的指标赋值
			    	HashMap partAssistantMap = new HashMap();//兼职子集中需要普通子集的数据，暂时存放在这个map中。指标名称为键，指标值为键值
			    	Set key2 = temp_map.keySet();
			    	for (Iterator it = key2.iterator(); it.hasNext();) {//遍历所有的子集
						String s = (String) it.next();//子集的名称
						ArrayList innerlist = (ArrayList)temp_map.get(s);//子集下所有的指标
						String afterField = "T_"+s.toUpperCase()+"_2";
						String tempXml = Sql_switcher.readMemo(rs, afterField);
						LinkedHashMap recordMap = getAllRecordsMap(tempXml,0);//得到最后一条记录   recordMap以i9999为键
						Set key3 = recordMap.keySet();
						for (Iterator it3 = key3.iterator(); it3.hasNext();) {//遍历记录（遍历多个i9999）
							String temp_key = (String)it3.next();//i9999
							String fieldvalue = (String)recordMap.get(temp_key);//当前i9999对应的记录值
							if(fieldvalue.length()>0){
								String[] temparray = fieldvalue.split("`");
								//innerlist的size和temparray的size是相同的，并且顺序是对应的。
								for(int k=0;k<innerlist.size();k++){
									String temporary_field = (String)innerlist.get(k);//指标名称
									String temporary_value = null;
									if(k<temparray.length){
										temporary_value = temparray[k];//指标值
										if("emptyvalue".equals(temporary_value)){
											temporary_value = null;
										}
									}
									bean.set(temporary_field, temporary_value);
									if(!parttime_job.equalsIgnoreCase(s)){//普通子集
										partAssistantMap.put(temporary_field, temporary_value);
									}
								}
							}
						}
					} //给普通子集的指标赋值 结束

			    	//开始给兼职子集赋值（任免标识是必须要的一个字段。）
			    	if(temp_map.containsKey(parttime_job) && "true".equals(part_flag)){
			    		//对于兼职子集，一条记录一个bean。
			    		String afterField = "T_"+parttime_job.toUpperCase()+"_2";
			    		String tempXml = Sql_switcher.readMemo(rs, afterField);
			    		LinkedHashMap recordMap = getAllRecordsMap(tempXml,1);
			    		Set key4 = recordMap.keySet();//以i9999为键的map
			    		//先为partHasDeletedRecord赋值
			    		for (Iterator it = key4.iterator(); it.hasNext();) {
			    			String temp_key = (String) it.next();//i9999`deletedflag
							String[] innerarray = temp_key.split("`");
							String innerdeleteflag = innerarray[1];
							if("1".equals(innerdeleteflag)){
								partHasDeletedRecord = true;
								break;
							}
			    		}
			    		for (Iterator it = key4.iterator(); it.hasNext();) {
			    			partHasRecord = true;
			    			LazyDynaBean temp_bean = new LazyDynaBean();
							String temp_key = (String) it.next();//i9999
							String[] innerarray = temp_key.split("`");
							String inneri9999 = innerarray[0];
							String innerdeleteflag = innerarray[1];
							temp_bean.set("ispart", "1");//是兼职子集
							temp_bean.set("objecttype", "1");
							temp_bean.set("nbase", srcDb);
							temp_bean.set("a0100", rs.getString("a0100"));

							if("-1".equals(inneri9999)){//如果是新增。
								temp_bean.set("i9999", "-1");
								temp_bean.set("addflag", "1");
								//任免标识
								if(!isHasAppointField && partHasDeletedRecord){//如果没有任免标识这个指标，并且兼职子集中有删除的记录
									temp_bean.set(part_appoint, "1");//免
								}
								//缺少的指标
								if(part_needField.length()>0){
							    	String[] splitarray = part_needField.split(",");
							    	for(int m=0;m<splitarray.length;m++){
							    		temp_bean.set(splitarray[m], "");
							    	}
						    	}
							}else{//如果是修改(只有修改时才考虑删除的记录)
								temp_bean.set("i9999",inneri9999);
								temp_bean.set("addflag", "0");
								if("1".equals(innerdeleteflag)){//如果这条记录是删除的
									temp_bean.set(part_appoint, "1");
								}else{//记录不是删除的
									if(partHasDeletedRecord){//兼职子集中有删除的记录
										temp_bean = scanFormationBo.getPartPoint(temp_bean);
									}
								}
								//缺少的指标
								if(part_needField.length()>0){
							    	temp_bean = scanFormationBo.getPartOrMainOrg(temp_bean,part_needField);
						    	}
							}
							//兼职单位转换为b0110。将兼职子集中指标的值赋给bean
							ArrayList l = (ArrayList)temp_map.get(parttime_job);//兼职子集中所有的指标
							String innerFieldValue = (String)recordMap.get(temp_key);//该i9999下的指标的数据
							if(innerFieldValue.length()>0){
								String[] array = innerFieldValue.split("`");
								for(int i=0;i<l.size();i++){
									String fieldname = (String)l.get(i);
									if("1".equals(innerdeleteflag) && fieldname.equals(part_appoint)){//如果当前记录是删除的，就跳过。不能把“免”给覆盖了。
										continue;
									}
									String fieldvalue = null;
									if(i<array.length){
										fieldvalue = array[i];
									}
									if(fieldname.equalsIgnoreCase(part_unit)){//把兼职单位转换为b0110
										temp_bean.set("b0110", fieldvalue);
										temp_bean.set(fieldname, fieldvalue);
									}else if(fieldname.equalsIgnoreCase(part_depart)){
										temp_bean.set("e0122", fieldvalue);
										temp_bean.set(fieldname, fieldvalue);
									}else if(fieldname.equalsIgnoreCase(part_pos)){
										temp_bean.set("e01a1", fieldvalue);
										temp_bean.set(fieldname, fieldvalue);
									}else{
										temp_bean.set(fieldname, fieldvalue);
									}
								}
							} //innerFieldValue.length()>0 结束

							//再给所有的指标赋值
					    	for(int j=0;j<afterFields.size();j++){
					    		String temp_field = (String)afterFields.get(j);
					    		if("b0110".equalsIgnoreCase(temp_field) || "e0122".equalsIgnoreCase(temp_field) || "e01a1".equalsIgnoreCase(temp_field)){
					    			continue;
					    		}
					    		FieldItem item = DataDictionary.getFieldItem(temp_field.toUpperCase());
					    		String temp_value = null;
					    		temp_value = getDataByType(rs,item);
					    		item = null;
					    		temp_bean.set(temp_field.toLowerCase(), temp_value);
					    	}

					    	//再取普通子集的最后一条记录
					    	Set key5 = partAssistantMap.keySet();
				    		for (Iterator ite = key5.iterator(); ite.hasNext();) {
								String tempfieldname = (String) ite.next();//指标名称
								String tempfieldvalue = (String)partAssistantMap.get(tempfieldname);
								temp_bean.set(tempfieldname, tempfieldvalue);
				    		}
					    	beanlist.add(temp_bean);
			    		} //遍历recordMap 结束

			    	} //给兼职子集赋值 结束

			    	//以下把普通子集的bean进行补充
			    	//只要兼职子集有记录，，并且有删除的记录，就要设置任免标识
			    	if(partHasRecord && partHasDeletedRecord){//如果指标中没有，就设为空。如果有，原来是什么就是什么。
			    		if(bean.get(part_appoint)==null){
                                        bean.set(part_appoint, "");
			    		}
			    	}
			    	//给普通子集补上b0110等兼职子集有但主集指标中没有的指标
			    	if(partHasRecord && common_needField.length()>0){
			    		if("1".equals(addFlag)){//新增
				    		String[] splitarray = common_needField.split(",");
				    		for(int m=0;m<splitarray.length;m++){
				    			bean.set(splitarray[m], "");
				    		}
				    	}else if("0".equals(addFlag)){//修改
				    		if(common_needField.length()>0){
				    			bean = scanFormationBo.getPartOrMainOrg(bean,common_needField);
				    		}
				    	}
			    	}
			    	beanlist.add(bean);
			    } //while 结束
			    if(rs!=null){
			    	rs.close();
			    }
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return beanlist;
	}

	/**根据指标类型取得数据,并转换成字符串型  人事异动专用* */
	private String getDataByType(RowSet rs,FieldItem item){
		String str = null;
		try{
			if(item == null) {
return str;
}
   String item_id = item.getItemid()+"_2";//变化后的
			String item_type = item.getItemtype();
			if("A".equals(item_type)){
				str = rs.getString(item_id);
			}else if("D".equals(item_type)){
				Date date = rs.getDate(item_id);
				str = String.valueOf(date);
			}else if("M".equals(item_type)){
				str = Sql_switcher.readMemo(rs, item_id);
			}else if("N".equals(item_type)){
			int decimal = item.getDecimalwidth();
				if(decimal>0){
					str = rs.getFloat(item_id)+"";
				}else{
					str = rs.getInt(item_id)+"";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}

	/**得到模板中所有涉及到的变化后的指标（如果是子集，就要把所包含的所有指标取出来）*/
	public ArrayList getAllFields(String tabid){
		ArrayList list = new ArrayList();
		try{
			ArrayList fielditemList = new ArrayList();//变化后的指标
			HashMap fieldsetToFielditemMap = new HashMap();
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			StringBuffer sql = new StringBuffer("");
			sql.append("select * from template_set where tabid="+tabid+" and upper(flag)='A' and chgstate=2 and setname is not null");//取变化后的指标和子集
rs = dao.search(sql.toString());
while(rs.next()){
String subflag = rs.getString("subflag");
if(subflag==null){
					continue;
				}
				if("1".equals(subflag)){//子集
					String setname = rs.getString("setname");
					String xml = Sql_switcher.readMemo(rs, "sub_domain");
					ArrayList templist = getItemListFromSet(xml);//得到子集中所有的指标
					fieldsetToFielditemMap.put(setname.toLowerCase(), templist);
				}else if("0".equals(subflag)){//普通指标
					String field_name = rs.getString("field_name");
					fielditemList.add(field_name.toLowerCase());
}
}
			list.add(fielditemList);
			list.add(fieldsetToFielditemMap);
			if(rs!=null){
		    	rs.close();
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
return list;
}

	/**得到子集下的指标*/
	public ArrayList getItemListFromSet(String value){
		ArrayList list = new ArrayList();
		if(value==null || value.length()<=0){
			return list;
		}
		try{
			String fields = "";
			Document doc=PubFunc.generateDom(value);;
			Element element=null;
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String xpath="/sub_para/para";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			element =(Element) findPath.selectSingleNode(doc);
			if(element!=null){
				if(element.getAttribute("fields")!=null){
                fields = element.getAttributeValue("fields");
            }
        }
        if(fields.length()>0){
String[] arr = fields.split("`");
for(int i=0;i<arr.length;i++){
        list.add(arr[i].toLowerCase());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 求标准表中，更新SQL语句
	 * @param fieldlist
	 * @param strS
	 * @param setid
	 * @return
	 */
	private String getStdUpdateSQL(ArrayList fieldlist,String strS,String setid)
	{
		StringBuffer buf=new StringBuffer();
		StringBuffer fields=new StringBuffer();
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem item=(FieldItem)fieldlist.get(i);

			/**子集名相同*/
			if(item.getFieldsetid().equalsIgnoreCase(setid))
			{
				if(fields.indexOf(item.getItemid())!=-1) {
                    continue;
                }
				fields.append(item.getItemid());
				fields.append(",");

				String fieldname=item.getItemid();
				buf.append(this.bz_tablename);
				buf.append(".");
				buf.append(fieldname);
				buf.append("=");
				buf.append(strS);
				buf.append(".");
				buf.append(fieldname);

				if(Sql_switcher.searchDbServer()==2) {
                    buf.append("`");
                } else {
buf.append(",");
                }
			}
		}//for i loop end.
		if(buf.length()>0) {
            buf.setLength(buf.length()-1);
        }
		return buf.toString();
	}

	/**
	 * 取得标准表字段列表
	 * @param fieldlist
	 * @param setid
	 * @return for examples a0xxx,a2000
	 */
	private String getStdFieldNameList(ArrayList fieldlist,String setid)
	{
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem fielditem=(FieldItem)fieldlist.get(i);
			if(fielditem.getFieldsetid().equalsIgnoreCase(setid))
			{
				if(buf.indexOf(fielditem.getItemid())==-1)
				{
					buf.append(fielditem.getItemid());
					buf.append(",");
				}
			}
		}//for i loop end.
		if(buf.length()>0) {
            buf.setLength(buf.length()-1);
        }
		return buf.toString();
	}
	/**
	 * 把标准表中指标加入薪资发放表中，从档案中取得标准表对应的指标值
* @param fieldlist
* @return
*/
private void addStdFieldIntoGzTable(String strWhere,Object[] dbarr)throws GeneralException
	{
		try
		{
    /**薪资标准计算*/
    ArrayList fieldlist=this.searchStdTableFieldList();

    if(fieldlist.size()==0) {
 return;
            }
			List setlist=getSetListByStd(fieldlist);
			RecordVo vo=new RecordVo(this.bz_tablename);
        DbWizard dbw=new DbWizard(this.conn);
        Table table=new Table(this.bz_tablename);
        String midtable="t#"+this.userview.getUserName()+"_gz"; //this.userview.getUserName()+"midtable";

ArrayList fieldlist_2=new ArrayList();
			/**
			 * 把标准中涉及到的指标加入至薪资表结构中
			 */
			boolean bflag=false;
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				if("0".equals(item.getUseflag())) //未构库
                {
                    throw GeneralExceptionHandler.Handle(new Exception("执行薪资标准出错, \""+item.getItemdesc()+"\" 没有构库!"));
                }
				String fieldname=item.getItemid();
				/**变量如果未加，则构建*/
				if(!vo.hasAttribute(fieldname.toLowerCase()))
				{
					Field field=item.cloneField();
					bflag=true;
					table.addField(field);
				}//if end.

				if(vo.hasAttribute(fieldname.toLowerCase()+"_2")) {
                    fieldlist_2.add(item);
                }

			}//for i loop end.
			if(bflag)
			{
				dbw.addColumns(table);
				DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel(bz_tablename);
			}

			/**从档案表中导入有关标准表涉及到的数据*/
			for(int i=0;i<dbarr.length;i++)
			{
				String dbpre=(String)dbarr[i];
				for(int j=0;j<setlist.size();j++)
				{
					String setid=(String)setlist.get(j);
					if("A00".equalsIgnoreCase(setid))//多媒体子集
                    {
                        continue;
                    }
					char cc=setid.charAt(0);
					switch(cc)
					{
					case 'A': //人员信息
							String strS=dbpre+setid;
							if("A01".equalsIgnoreCase(setid)) //主集
							{
								String strupdate=getStdUpdateSQL(fieldlist, strS, setid);
								if(strupdate.length()>0) {
                                    dbw.updateRecord(bz_tablename,strS,bz_tablename+".A0100="+strS+".A0100", strupdate, bz_tablename+".basepre='"+dbpre+"'", "");
                                }
							}
							else//子集
							{
								String strupdate=getStdUpdateSQL(fieldlist, midtable, setid);
								if(strupdate.length()==0) {
                                                    continue;
                                }
								String strfields=getStdFieldNameList(fieldlist, setid);
								/**子集当前子录生成临时表*/
								String tempt="t#"+this.userview.getUserName()+"_gz_1"; //this.userview.getUserName()+"midtable1";
								if(dbw.isExistTable(tempt, false)) {
                                    dbw.dropTable(tempt);
                                }
								dbw.createTempTable(strS, tempt,"A0100 as A0000,Max(I9999) as midid", "","A0100");
								if(dbw.isExistTable(midtable, false)) {
                                    dbw.dropTable(midtable);
                                }
								dbw.createTempTable(strS+" Left join "+tempt+" On "+strS+".A0100="+tempt+".A0000",midtable, "A0100,"+strfields,strS+".I9999="+tempt+".midid","");
								dbw.updateRecord(this.bz_tablename,midtable,this.bz_tablename+".A0100="+midtable+".A0100",strupdate, this.bz_tablename+".basepre='"+dbpre+"'", strWhere);
							}
							break;
					case 'B'://单位信息
							break;
					case 'K'://职位信息
							break;
					}
				}//for j 子集数据处理
			}//for i loop end.



			for(int i=0;i<fieldlist_2.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist_2.get(i);
				if(strWhere != null && !"".equals(strWhere)) {
                    dbw.execute("update "+this.bz_tablename+" set "+item.getItemid()+"="+item.getItemid()+"_2 where "+strWhere);
                } else {
                    dbw.execute("update "+this.bz_tablename+" set "+item.getItemid()+"="+item.getItemid()+"_2 ");
                }

			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	/**
	 * 执行所有薪资标准
	 * @param strWhere
	 * @throws GeneralException
	 */
	private void batchCalcGzStandard(String strWhere)throws GeneralException
	{
		try
		{
			for(int i=0;i<gz_stand.length;i++)
			{
				int standid=Integer.parseInt(gz_stand[i]);
   calcGzStandard(standid,strWhere.replaceAll("where"," "));
			}
		}
		catch(Exception ex)
	    {
	    	  throw GeneralExceptionHandler.Handle(new Exception("执行薪资标准出错!"));
	    }
	}
	/**
	 * 执行薪资标准
	 * @param standid	 标准号
	 * @param strWhere	 条件
	 */
	private void calcGzStandard(int standid,String strWhere)throws GeneralException
	{
      try
      {
		SalaryStandardBo stdbo=new SalaryStandardBo(this.conn,String.valueOf(standid),"");
		/**如果标准不存在，则退出*/
		if(!stdbo.isExist()) {
            return;
        }

		/**重新计算相关日期型或数值型区间范围的值*/
		StringBuffer buf=new StringBuffer();
		if(!stdbo.checkHVField(buf)) {
            throw new GeneralException(buf.toString());
        }
		/**把标准横纵坐标为日期型或数值型指标，加至薪资表中*/
ArrayList list=stdbo.addStdItemIntoTable(this.bz_tablename);
stdbo.updateStdItem(list, this.bz_tablename);
		/**关联更新串,0*/
		String joinon=stdbo.getStandardJoinOn(this.bz_tablename,0);
		/**结果指标*/
		String fieldname=stdbo.getItem();
		FieldItem item=DataDictionary.getFieldItem(fieldname);
		/**变化后的指标,模板中是否定义了变化后指标*/
		fieldname=fieldname+"_2";
		RecordVo vo=new RecordVo(bz_tablename);
		if(!vo.hasAttribute(fieldname.toLowerCase())) {
            return;
        }
		DbWizard dbw=new DbWizard(this.conn);
		switch(Sql_switcher.searchDbServer())
		{
		case 1: //MSSQL
			dbw.updateRecord(this.bz_tablename, "gz_item",joinon,this.bz_tablename+"."+fieldname+"=gz_item.standard", strWhere, "");
break;
case 2://oracle
			if("N".equalsIgnoreCase(item.getItemtype())) {
                dbw.updateRecord(this.bz_tablename, "gz_item",joinon,this.bz_tablename+"."+fieldname+"=to_number(gz_item.standard)", strWhere, "");
            } else {
                dbw.updateRecord(this.bz_tablename, "gz_item",joinon,this.bz_tablename+"."+fieldname+"=gz_item.standard", strWhere, "");
            }
			break;
		case 3://db2
			if("N".equalsIgnoreCase(item.getItemtype())) {
                dbw.updateRecord(this.bz_tablename, "gz_item",joinon,this.bz_tablename+"."+fieldname+"=double(gz_item.standard)", strWhere, "");
            } else {
                dbw.updateRecord(this.bz_tablename, "gz_item",joinon,this.bz_tablename+"."+fieldname+"=gz_item.standard", strWhere, "");
            }
			break;
		}
      }
      catch(Exception ex)
      {
    	  ex.printStackTrace();
    	  throw GeneralExceptionHandler.Handle(ex);
      }
	}
	/**
	 * 取得计算条件
	 * @param ins_id
	 * @return
	 */
	private String getWhereSQL(String ins_id)
	{
		StringBuffer buf=new StringBuffer();
		StringBuffer strWhere=new StringBuffer();
		String[] ins_arr=StringUtils.split(ins_id,",");
		for(int i=0;i<ins_arr.length;i++)
		{
			buf.append(ins_arr[i]);
			buf.append(",");
		}
		if(buf.length()!=0)
		{
			buf.setLength(buf.length()-1);
strWhere.append(" where  ins_id in (");
			strWhere.append(buf.toString());
strWhere.append(")");
		}
		return strWhere.toString();
	}


	/**
	 * 计算考勤可修天数、已修天数
* @param task_id
* @return
* @throws GeneralException
	 */
	public String  computeKqDays(String ins_id,String key_value,String qj_type)throws GeneralException
	{
		String str="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=null;
			String strwhere="";
			if("0".equalsIgnoreCase(ins_id))
			{
				if(this.bEmploy)
				{
					bz_tablename="g_templet_"+this.tabid;
					strwhere+=" where  "+bz_tablename+".a0100='"+this.userview.getA0100()+"' and lower("+bz_tablename+".basepre)='"+this.userview.getDbname().toLowerCase()+"'" ;

				}
				else
				{
					bz_tablename=this.userview.getUserName()+"templet_"+this.tabid;
					strwhere+=" where  1=1 ";
				}
			}
			else
			{
				bz_tablename="templet_"+this.tabid;
				strwhere=getWhereSQL(ins_id);
			}
			if(this.infor_type==1)
			{
				strwhere+=" and  "+bz_tablename+".a0100='"+key_value.substring(3)+"' and lower( "+bz_tablename+".basepre)='"+key_value.substring(0,3)+"'";
			}
			else if(this.infor_type==2) {
strwhere+=" and  b0110='"+key_value+"'";
            } else if(this.infor_type==3) {
                strwhere+=" and  e01a1='"+key_value+"'";
            }
			String[] temps=qj_type.split("=");
			dao.update("update "+bz_tablename+" set "+temps[0]+"='"+temps[1]+"' "+strwhere);

			/**计算临时变量,把临时变量加到变动处理表中去*/
			/**应用库前缀*/

			Object[] dbarr=null;
			if(this.infor_type==1)
			{
                ArrayList dblist=searchDBPreList(bz_tablename,strwhere);
				dbarr=dblist.toArray();
			}
			if(this.infor_type==1)
			{
				ArrayList fieldlist=getMidVariableList();
				addMidVarIntoGzTable(strwhere,dbarr,fieldlist);
			}
			str=calcKqFormula(strwhere);
			PubFunc.closeDbObj(rowSet);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();

			String message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
    {


        PubFunc.resolve8060(this.conn,bz_tablename);
				throw GeneralExceptionHandler.Handle(new Exception("请重新执行计算操作!"));
			}
			else {
                throw GeneralExceptionHandler.Handle(ex);
            }

		}

		return str;
	}





	/**
	 * 执行考勤计算公式
	 * @param formulalist
	 * @param strWhere
	 * @throws GeneralException
	 */
	private String calcKqFormula(String strWhere)throws GeneralException
	{
		String str="";
		try
		{
			String strfilter="";
			YksjParser yp=null;
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList fldvarlist=getAllFieldItem();
			ArrayList fieldlist=getMidVariableList();
			fldvarlist.addAll(fieldlist);
			for(int i=0;i<fldvarlist.size();i++)
			{
				FieldItem fielditem=(FieldItem)fldvarlist.get(i);
				if(fielditem.getVarible()!=1)
				{
					if(fielditem.isChangeAfter())
					{
						fielditem.setItemid(fielditem.getItemid()+"_2");
						String desc = (String)fielditem.getItemdesc();
						if(desc!=null && desc.trim().length()>0 && "拟".equalsIgnoreCase(desc.substring(0,1)))
    		 			{
							fielditem.setItemdesc(""+fielditem.getItemdesc());
    		 			}else {
                            fielditem.setItemdesc("拟"+fielditem.getItemdesc());
                     }
					}
					if(fielditem.isChangeBefore()){
							if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0){
								fielditem.setItemid(fielditem.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_1");
								fielditem.setItemdesc(""+this.sub_domain_map.get(""+i+"hz"));
								}else{
									fielditem.setItemid(fielditem.getItemid()+"_1");
								}

					}
				}
			}
			/**先对计算公式的条件进行分析*/
			ArrayList formulalist=readFormula();
			RowSet rowSet=null;
			for(int i=0;i<formulalist.size();i++)
    {
        int infoGroupFlag=YksjParser.forPerson;
				if(this.infor_type==2) {
                    infoGroupFlag=YksjParser.forUnit;
                }
				if(this.infor_type==3) {
                    infoGroupFlag=YksjParser.forPosition;
                }
				FormulaGroupBo formulabo=(FormulaGroupBo)formulalist.get(i);
				/**先对计算条件进行处理*/
				String cond=formulabo.getStrWhere();
				strfilter="";
				if(!(cond==null|| "".equalsIgnoreCase(cond)))
				{

					yp = new YksjParser( this.userview ,fldvarlist,
							YksjParser.forNormal, YksjParser.LOGIC,infoGroupFlag, "Ht", "");
					yp.run_where(cond);
					strfilter=yp.getSQL();
				}
				StringBuffer strcond=new StringBuffer();
				if(!(strWhere==null|| "".equalsIgnoreCase(strWhere))) {
                    strcond.append(strWhere);
                }
				if(!("".equalsIgnoreCase(strfilter)))
				{
					if(strcond.length()>0) {
                        strcond.append(" and ");
                    } else {
                        strcond.append(" where ");
                    }
					strcond.append(strfilter);
				}
				ArrayList list=formulabo.getFormulalist();
				for(int j=0;j<list.size();j++)
				{
					LazyDynaBean dynabean=(LazyDynaBean)list.get(j);
					String fieldname=(String)dynabean.get("lexpr");
					String formula=(String)dynabean.get("rexpr");
					if(formula.indexOf("可休天数")==-1&&formula.indexOf("已休天数")==-1) {
                        continue;
                    }

					/**进行公式计算*/
					FieldItem item=DataDictionary.getFieldItem(fieldname.substring(0, 5));
					if(item==null){
						if(fieldname.lastIndexOf("_")!=-1)
						{
							if("codesetid".equalsIgnoreCase(fieldname.substring(0, fieldname.lastIndexOf("_")))|| "codeitemdesc".equalsIgnoreCase(fieldname.substring(0, fieldname.lastIndexOf("_")))|| "corcode".equalsIgnoreCase(fieldname.substring(0, fieldname.lastIndexOf("_")))|| "parentid".equalsIgnoreCase(fieldname.substring(0, fieldname.lastIndexOf("_")))|| "start_date".equalsIgnoreCase(fieldname.substring(0, fieldname.lastIndexOf("_"))))
							{
								if(!"start_date".equalsIgnoreCase(fieldname.substring(0, fieldname.lastIndexOf("_")))){
									item = new FieldItem();
									item.setItemtype("A");
									}else{
										item.setItemtype("D");
									}
							}else {
                                throw new GeneralException("左表达式指标在指标体系中未定义!");
                            }
						}else {
                            throw new GeneralException("左表达式指标在指标体系中未定义!");
                        }
					}

					if(!isExistLexpr(fieldname,fldvarlist)) {
                        throw new GeneralException("左表达式指标在模板中未定义!");
                    }

					yp=new YksjParser( this.userview ,fldvarlist,
							YksjParser.forNormal, getDataType(item.getItemtype()),infoGroupFlag, "Ht", "");
					yp.run(formula,this.conn,strcond.toString().replaceAll("where"," "),this.bz_tablename);
					/**单表计算*/
					String strexpr=yp.getSQL();


					{
						StringBuffer strsql=new StringBuffer();
						strsql.append("update ");
						strsql.append(this.bz_tablename);
						strsql.append(" set ");
						strsql.append(fieldname);
						strsql.append("=");
						strsql.append(strexpr);
						if(strcond.length()>0)
						{
							//strsql.append(" where ");
							strsql.append(strcond.toString());
						}
						try
						{
							dao.update(strsql.toString());
						}
						catch(Exception cex)
						{
							;
						}

						rowSet=dao.search("select "+fieldname+" from "+this.bz_tablename+strWhere);
						while(rowSet.next())
						{
							if(rowSet.getString(1)!=null) {
                                str+=","+fieldname+":"+rowSet.getString(1);
                            } else {
                                str+=","+fieldname+":";
                            }
						}

					}
				}//for j loop end.
			}//for i loop end.

			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		if(str.length()>0) {
            return str.substring(1);
        }
		return "";
	}


	/**
	 * 批量计算计算公式和审核公式用到的临时变量
	 * @param ins_id
	 * @throws GeneralException
	 */
	public void batchComputeMidvariable(String ins_id)throws GeneralException
	{
		try
		{
			String strwhere="";
			if("0".equalsIgnoreCase(ins_id))
			{
				if(this.bEmploy)
				{
					bz_tablename="g_templet_"+this.tabid;
					strwhere+=" where  "+bz_tablename+".a0100='"+this.userview.getA0100()+"' and lower("+bz_tablename+".basepre)='"+this.userview.getDbname().toLowerCase()+"'" ;
				}
				else {
                    bz_tablename=this.userview.getUserName()+"templet_"+this.tabid;
                }
			}
			else
			{
				bz_tablename="templet_"+this.tabid;
				strwhere=getWhereSQL(ins_id);
			}
			Object[] dbarr=null;
			if(this.infor_type==1)
			{
			    ArrayList dblist=searchDBPreList(bz_tablename,strwhere);
				dbarr=dblist.toArray();
			}
			ArrayList fieldlist=new ArrayList();

			String noCheckTemplateIds=SystemConfig.getPropertyValue("noCheckTemplateIds");  //system.properties -->  noCheckTemplateIds=12,88
			//20141210  dengcan  汉口银行行长在批准单据时嫌速度太慢，与刘红梅商量针对行长的单据在审批时无需审核，提高程序执行效率
			if(noCheckTemplateIds!=null&&!"0".equalsIgnoreCase(ins_id)&&(","+noCheckTemplateIds+",").indexOf(","+this.tabid+",")!=-1)
			{
				fieldlist=getMidVariableListByFunc(1);
			}
			else {
                fieldlist=getMidVariableListByFunc(3);//getMidVariableList();
            }
			addMidVarIntoGzTable(strwhere,dbarr,fieldlist);
			this.isComputeVar=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();

			String message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{


				PubFunc.resolve8060(this.conn,bz_tablename);
				throw GeneralExceptionHandler.Handle(new Exception("请重新执行计算操作!"));
			}
			else {
                throw GeneralExceptionHandler.Handle(ex);
            }

		}




	}




	/**
	 * 批量执行计算公式
	 * @param ins_id 实例号，如果为0,则表示发起流程的用户有关的临时表进行处理
	 * @throws GeneralException
	 */
	public void batchCompute(String ins_id)throws GeneralException
	{
		try
		{
			String strwhere="";
			this.insid_pro=ins_id;
			if("0".equalsIgnoreCase(ins_id))
			{
				if(this.bEmploy)
				{
					bz_tablename="g_templet_"+this.tabid;
					strwhere+=" where  "+bz_tablename+".a0100='"+this.userview.getA0100()+"' and lower("+bz_tablename+".basepre)='"+this.userview.getDbname().toLowerCase()+"'" ;

				}
				else {
                    bz_tablename=this.userview.getUserName()+"templet_"+this.tabid;
                }
			}
			else
			{
				bz_tablename="templet_"+this.tabid;
				strwhere=getWhereSQL(ins_id);
			}

			/**计算临时变量,把临时变量加到变动处理表中去*/
			/**应用库前缀*/

			Object[] dbarr=null;
			if(this.infor_type==1)
			{
			    ArrayList dblist=searchDBPreList(bz_tablename,strwhere);
				dbarr=dblist.toArray();
			}
			if(!isComputeVar)
			{
				if(this.infor_type==1)
{
    ArrayList fieldlist=getMidVariableListByFunc(1);//getMidVariableList();
addMidVarIntoGzTable(strwhere,dbarr,fieldlist);
				}
				if(this.infor_type==2)//基于组织的计算也加临时变量的处理  zhaoxg add 2014-1-13
        {
            ArrayList fieldlist=getMidVariableListByFunc(1);//getMidVariableList();
					addMidVarIntoGzTable(strwhere,dbarr,fieldlist);
				}
        if(this.infor_type==3)//基于职位的计算也加临时变量的处理  zhaoxg add 2014-1-13
				{
					ArrayList fieldlist=getMidVariableListByFunc(1);//getMidVariableList();
					addMidVarIntoGzTable(strwhere,dbarr,fieldlist);
				}
			}

			/**处理标准表涉及的指标*/
			if(this.infor_type==1)
			{
				addStdFieldIntoGzTable(strwhere.replaceAll("where"," "),dbarr);
				/**执行薪资标准*/
				batchCalcGzStandard(strwhere);
			}
			/**执行计算公式*/
			calcGzFormula(strwhere);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();

			String message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{


				PubFunc.resolve8060(this.conn,bz_tablename);
				throw GeneralExceptionHandler.Handle(new Exception("请重新执行计算操作!"));
			}
			else {
                throw GeneralExceptionHandler.Handle(ex);
            }

		}
	}
	/**
	 * 左表达式在模板中是否存在,变化后指标
	 * @return
	 */
	private boolean isExistLexpr(String lexpr,ArrayList list)
	{
		boolean bflag=false;
		String field_name="";
		for(int i=0;i<list.size();i++)
		{
			FieldItem fielditem=(FieldItem)list.get(i);
			if(fielditem.getVarible()==1) {
                continue;
            }
			if(fielditem.getVarible()!=1)
			{
				//if(fielditem.isChangeAfter())
				//	field_name=fielditem.getItemid()+"_2";
				//if(fielditem.isChangeBefore())
				//	field_name=fielditem.getItemid()+"_1 ";
				field_name=fielditem.getItemid();
			}
			if(field_name.equalsIgnoreCase(lexpr))
			{
				bflag=true;
    break;
			}
		}
		return bflag;
	}
	/**
	 * 执行计算公式
* @param formulalist
	 * @param strWhere
	 * @throws GeneralException
	 */
	private void calcGzFormula(String strWhere)throws GeneralException
{
try
{
RowSet rowSet=null;
			String strfilter="";
YksjParser yp=null;
ContentDAO dao=new ContentDAO(this.conn);
			ArrayList fldvarlist=getAllFieldItem();
			/**必填项校验*/
		//	checkMustFillItem2(bz_tablename,fldvarlist,strWhere);
			boolean isE0122_2=false;

			boolean isE01A1_2=false;

			boolean executeZCYJ=false; //执行政策依据
			ArrayList zcyj_columns=new ArrayList();
			HashMap itemMap_before=new HashMap();
			HashMap itemMap_after=new HashMap();
			//计算政策依据
			if(this._static==2) //薪资管理
			{
				DbWizard dbw=new DbWizard(this.conn);
				if(dbw.isExistTable("template_"+this.tabid,false))
				{
					rowSet=dao.search("select * from template_"+this.tabid+" where 1=2");
					ResultSetMetaData rsmd=rowSet.getMetaData();
					int columnCount  =  rsmd.getColumnCount();//得到列数
					for(int i=0;i<columnCount;i++)
					{
						if(!"a0000".equalsIgnoreCase(rsmd.getColumnName(i+1).trim().toLowerCase())) {
                            zcyj_columns.add(rsmd.getColumnName(i+1).trim().toLowerCase());
                        }
					}
					executeZCYJ=true;
				}
			}


			ArrayList varlist=getMidVariableList();
			fldvarlist.addAll(varlist);
			for(int i=0;i<fldvarlist.size();i++)
			{
				FieldItem fielditem=(FieldItem)fldvarlist.get(i);
				if(fielditem.getVarible()!=1)//如果不是临时变量
				{
					if(fielditem.isChangeAfter())
					{
						if("e0122".equalsIgnoreCase(fielditem.getItemid())) {
                            isE0122_2=true;
                        }
						if("e01a1".equalsIgnoreCase(fielditem.getItemid())) {
                            isE01A1_2=true;
                        }
						fielditem.setItemid(fielditem.getItemid()+"_2");
						itemMap_after.put(fielditem.getItemid().toLowerCase(),"1");
						String desc = (String)fielditem.getItemdesc();
						if(desc!=null && desc.trim().length()>0 && "拟".equalsIgnoreCase(desc.substring(0,1)))
    		 			{
							fielditem.setItemdesc(""+fielditem.getItemdesc());
    		 			}else {
                            fielditem.setItemdesc("拟"+fielditem.getItemdesc());
}
                            }
                            if(fielditem.isChangeBefore()){
				if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0){
                            fielditem.setItemid(fielditem.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_1");
								fielditem.setItemdesc(""+this.sub_domain_map.get(""+i+"hz"));
								}else{
									fielditem.setItemid(fielditem.getItemid()+"_1");
									itemMap_before.put(fielditem.getItemid().toLowerCase(),"1");
								}

					}
				}
			}


			boolean isCal_b0110=false; //是否计算单位值
			boolean isCal_e0122=false; //是否计算部门值

			/**先对计算公式的条件进行分析*/
			ArrayList formulalist=readFormula();
			for(int i=0;i<formulalist.size();i++)
			{
				int infoGroupFlag=YksjParser.forPerson;
				if(this.infor_type==2) {
                    infoGroupFlag=YksjParser.forUnit;
                }
				if(this.infor_type==3) {
                    infoGroupFlag=YksjParser.forPosition;
                }
				FormulaGroupBo formulabo=(FormulaGroupBo)formulalist.get(i);
				/**先对计算条件进行处理*/
				String cond=formulabo.getStrWhere();
				strfilter="";
				if(!(cond==null|| "".equalsIgnoreCase(cond)))
				{

					yp = new YksjParser( this.userview ,fldvarlist,
							YksjParser.forNormal, YksjParser.LOGIC,infoGroupFlag, "Ht", "");
					yp.run_where(cond);
					strfilter=yp.getSQL();
				}
				StringBuffer strcond=new StringBuffer();
				if(!(strWhere==null|| "".equalsIgnoreCase(strWhere))) {
                    strcond.append(strWhere);
                }
				if(!("".equalsIgnoreCase(strfilter)))
				{
					if(strcond.length()>0) {
                        strcond.append(" and ");
                    } else {
                        strcond.append(" where ");
                    }
					strcond.append(strfilter);
				}
				ArrayList list=formulabo.getFormulalist();
				for(int j=0;j<list.size();j++)
				{
					LazyDynaBean dynabean=(LazyDynaBean)list.get(j);
					String fieldname=(String)dynabean.get("lexpr");
					String formula=(String)dynabean.get("rexpr");

					/**进行公式计算*/
					FieldItem item=DataDictionary.getFieldItem(fieldname.substring(0, 5));
					if(item==null){
						if(fieldname.lastIndexOf("_")!=-1)
						{
							if("codesetid".equalsIgnoreCase(fieldname.substring(0, fieldname.lastIndexOf("_")))|| "codeitemdesc".equalsIgnoreCase(fieldname.substring(0, fieldname.lastIndexOf("_")))|| "corcode".equalsIgnoreCase(fieldname.substring(0, fieldname.lastIndexOf("_")))|| "parentid".equalsIgnoreCase(fieldname.substring(0, fieldname.lastIndexOf("_")))|| "start_date".equalsIgnoreCase(fieldname.substring(0, fieldname.lastIndexOf("_"))))
							{
								item = new FieldItem();
								if(!"start_date".equalsIgnoreCase(fieldname.substring(0, fieldname.lastIndexOf("_")))){
									item.setItemtype("A");
								}else{
									item.setItemtype("D");
								}
							}else {
                                throw new GeneralException("左表达式指标在指标体系中未定义!");
                            }
						}else {
                            throw new GeneralException("左表达式指标在指标体系中未定义!");
                        }
					}

					if(!isExistLexpr(fieldname,fldvarlist)) {
                        throw new GeneralException("左表达式指标在模板中未定义!");
                    }
                            if("b0110_2".equalsIgnoreCase(fieldname)) {
isCal_b0110=true;
                    }
					if("e0122_2".equalsIgnoreCase(fieldname)) {
                                isCal_e0122=true;
                    }

                    /*从工号池取工号 wangrd 2013-11-26  */
                    if ((this.infor_type==1)&&!this.isBEmploy() &&(formula.indexOf("执行存储过程")!=-1)
                                &&(formula.indexOf("getJobNumber")!=-1||formula.indexOf("getStaffId")!=-1))
                    {
                        formula=formula.replace("当前表", this.bz_tablename);
                        formula=formula.replace("STR_WHERE", strWhere);
                        formula=formula.replace("TEMPLET_ITEMID", fieldname);
                    }

                    if(formula!=null&&formula.indexOf("执行存储过程")!=-1)
                    {
                    	formula=formula.replace("流程号",this.insid_pro);
                    }


					yp=new YksjParser( this.userview ,fldvarlist,
							YksjParser.forNormal, getDataType(item.getItemtype()),infoGroupFlag, "Ht", "");
					yp.setVarList(varlist);
					yp.setSupportVar(true);

					yp.setStdTmpTable_where(strcond.toString().replaceAll("where"," "));
					yp.run(formula,this.conn,strcond.toString().replaceAll("where"," "),this.bz_tablename);
					if (formula.indexOf("执行存储过程")!=-1) {
                        continue;
                    }

					/**单表计算*/
					String strexpr=yp.getSQL();

					if("M".equalsIgnoreCase(item.getItemtype()))
					{
						StringBuffer strsql=new StringBuffer("");
						String key_str=",a0100,basepre";
						if(this.infor_type==2) {
                            key_str=",b0110";
                        } else if(this.infor_type==3) {
                            key_str=",e01a1";
                        }

						if(this.bz_tablename.equalsIgnoreCase("templet_"+this.tabid)) {
                            strsql.append("select "+strexpr+key_str+",ins_id  from "+this.bz_tablename);
                        } else {
                            strsql.append("select "+strexpr+key_str+"  from "+this.bz_tablename);
                        }
						if(strcond.length()>0)
						{
							strsql.append(strcond.toString());
						}
						rowSet=dao.search(strsql.toString());
						RecordVo vo=new RecordVo(this.bz_tablename);
						while(rowSet.next())
						{

							 String value=rowSet.getString(1);

							 if(this.infor_type==1)
							 {
								 String a0100=rowSet.getString(2);
								 String basepre=rowSet.getString(3);
								 vo.setString("a0100",a0100);
								 vo.setString("basepre",basepre);
							 }
							 else if(this.infor_type==2)
							 {
								 vo.setString("b0110",rowSet.getString("b0110"));
							 }
							 else if(this.infor_type==3)
							 {
								 vo.setString("e01a1",rowSet.getString("e01a1"));
							 }

							 int ins_id=0;
							 if(this.bz_tablename.equalsIgnoreCase("templet_"+this.tabid))
							 {
								 ins_id=rowSet.getInt("ins_id");
								 vo.setInt("ins_id",ins_id);
							 }
							 vo=dao.findByPrimaryKey(vo);
							 vo.setString(fieldname.toLowerCase(), value);
							 dao.updateValueObject(vo);

						}



					}
					else
					{
						StringBuffer strsql=new StringBuffer();
						strsql.append("update ");
						strsql.append(this.bz_tablename);
						strsql.append(" set ");
						strsql.append(fieldname);
						strsql.append("=");
						strsql.append(strexpr);
						if(strcond.length()>0)
						{
							//strsql.append(" where ");
							strsql.append(strcond.toString());
						}
						try
						{
							dao.update(strsql.toString());
						}
						catch(Exception cex)
						{
							cex.printStackTrace();
						}

					}
				}//for j loop end.
			}//for i loop end.

			String join="+";
			String where=" where 1=1 ";
			if(Sql_switcher.searchDbServer()==2) {
                join="||";
            }
			if(strWhere.length()!=0) {
                where=strWhere;
            }
			if(isCal_b0110) //计算单位时，部门、岗位信息要联动
			{
				if(isE0122_2) {
                    dao.update("update "+this.bz_tablename+" set e0122_2=null "+where+" and ( e0122_2 not like b0110_2"+join+"'%' or b0110_2 is null ) ");
         }
				if(isE01A1_2) {
                    dao.update("update "+this.bz_tablename+" set e01a1_2=null "+where+" and ( e01a1_2 not like b0110_2"+join+"'%' or b0110_2 is null ) ");
                }
			}


			if(isCal_e0122&&isE01A1_2) //计算部门时，岗位信息要联动
			{
				dao.update("update "+this.bz_tablename+" set e01a1_2=null "+where+" and ( e01a1_2 not like e0122_2"+join+"'%' and nullif(e0122_2,'') is not null ) ");
			}

			//计算政策依据
			if(executeZCYJ)
			{
				executeZCYJ(zcyj_columns,itemMap_before,itemMap_after,dao,where);
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}


	/**
	 * 计算政策依据
	 * @param zcyj_columns 政策依据列
	 * @param itemMap_before  政策依据设置的变化前指标
	 * @param itemMap_after   政策依据设置的变化后指标
	 * @param dao
	 * @param where   更新数据条件
	 */
	private void executeZCYJ(ArrayList zcyj_columns,HashMap itemMap_before,HashMap itemMap_after,ContentDAO dao,String where)throws GeneralException
{
try
{
	String whl="";
			String sql="";
			for(int i=0;i<zcyj_columns.size();i++)
			{
				String column_name=(String)zcyj_columns.get(i);
				if(itemMap_before.get(column_name)!=null)
				{
						whl+=" and isnull("+this.bz_tablename+"."+column_name+",'-10aa0')="+"isnull(template_"+this.tabid+"."+column_name+",'-10aa0')";
				}
			}

			if(whl.length()>0)
			{
				for(int i=0;i<zcyj_columns.size();i++)
				{
					String column_name=(String)zcyj_columns.get(i);
					if(itemMap_after.get(column_name)!=null)
					{
						sql="update "+this.bz_tablename+" set "+column_name+"=(select "+column_name+" from template_"+this.tabid+"  where 1=1 "+whl+" ) ";
						sql+=where;
						dao.update(sql);
					}

				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
}

}




/**
* 读取公式列表
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList readFormula()throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("select chz,formula,cfactor,cexpr,id from gzadj_formula where flag=1 and ");
			buf.append(" tabid=");
			buf.append(this.tabid);
			buf.append(" and formula is not null order by nsort");
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				FormulaGroupBo groupbo=new FormulaGroupBo();
            groupbo.setFormula(Sql_switcher.readMemo(rset,"formula"));
				groupbo.setStrWhere(Sql_switcher.readMemo(rset,"cfactor"));
				groupbo.setGroupName(rset.getString("chz"));
				list.add(groupbo);
			}
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}



	/**
	 * 导出数据至消息库中
	 * @param fieldlist 字段列表
	 * @param desta0100 对于调入生成目标库人员编号
	 * @throws GeneralException
	 */
	private String t_username="";  //消息到人
	private String t_type="4";//默认4 用户
	private void expDataIntoTmessage(RowSet rset,ArrayList fieldlist,String desta0100,HashMap subhm)throws GeneralException
	{
	 try
	 {
		// if(subctrl.getUpdatetype()==SubSetUpdateType.NOCHANGE)
		DbWizard dbw = new DbWizard(this.conn);
		TSubsetCtrl subctrl=null;
		if(this.infor_type==1) {
            subctrl=(TSubsetCtrl)subhm.get("A01");
        } else if(this.infor_type==2) {
            subctrl=(TSubsetCtrl)subhm.get("B01");
        } else if(this.infor_type==3) {
            subctrl=(TSubsetCtrl)subhm.get("K01");
        }

		boolean setChange=true;   //主集更改
		if(subctrl!=null&&subctrl.getUpdatetype()==SubSetUpdateType.NOCHANGE) {
            setChange=false;
        }
		RowSet rowSet=null;
		if(this.msg_template!=null&&this.msg_template.length>0)
		{
			String fieldname=null;
			String value=null;
			StringBuffer strlast=new StringBuffer();
			StringBuffer strpre=new StringBuffer();
			StringBuffer strchg=new StringBuffer();
			StringBuffer buf=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			int nyear=0;
			int nmonth=0;
			nyear=DateUtils.getYear(new Date());
			nmonth=DateUtils.getMonth(new Date());
			for(int i=0;i<msg_template.length;i++)
			{
				String dest_id=msg_template[i];
				rowSet=dao.search("select tabid from Template_table where tabid="+dest_id);
				if(rowSet.next())
				{

				}
				else {
                    continue;
                }

				strlast.setLength(0);
				strpre.setLength(0);
				buf.setLength(0);
				strchg.setLength(0);

				String b0110_self="";
				boolean isB0110_2=false;
				String b0110_value="";
				boolean isE0122_2=false;
				String e0122_value="";
				for(int j=0;j<fieldlist.size();j++)
				{
					FieldItem item=(FieldItem)((FieldItem)fieldlist.get(j)).cloneItem();
            Field temp=item.cloneField();
            if("A00".equals(item.getFieldsetid())&&("photo".equalsIgnoreCase(item.getItemid())|| "ext".equalsIgnoreCase(item.getItemid())||
							"fileid".equalsIgnoreCase(item.getItemid()))) {
                        continue;
                    }
					if(item.getItemid().startsWith("attachment"))//liuyz 7x附件为attachment_1或者attachment_0，老程序附件为attachment，用equals比较过滤不对。
                    {
                        continue;
                    }
					if(item.getVarible()==1) {
                        continue;
                    }

					if(item.isChangeAfter())
					{
						fieldname=item.getItemid()+"_2";
						temp.setName(fieldname);
						strlast.append(item.getItemid().toUpperCase());
						strlast.append("=");
						HashMap map = new HashMap();
						map.put("xxxxxx", "");
						value=DataTable.getValueByFieldType(rset,temp,map);
						if(item.isDate()&&!"".equalsIgnoreCase(value))
						{
							Date ss=new Date(Long.parseLong(value));
							value=PubFunc.FormatDate(ss,"yyyy/MM/dd");
						}

						if("B0110_2".equalsIgnoreCase(fieldname)&&setChange)
						{
							isB0110_2=true;
							b0110_value=value;
							b0110_self=value;
						}
						if("E0122_2".equalsIgnoreCase(fieldname)&&setChange)
						{
							isE0122_2=true;
							e0122_value=value;
						}
						value = value.replace(",", "，");
						strlast.append(value);
						strlast.append(",");
					}
					else //if(item.isChangeBefore())
					{
						fieldname=item.getItemid()+"_1";
						if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+j)!=null&&this.sub_domain_map.get(""+j).toString().trim().length()>0){
							fieldname=item.getItemid()+"_"+this.sub_domain_map.get(""+j)+"_1";
							continue;
							}
						if(this.field_name_map!=null&&this.field_name_map.get(fieldname.toLowerCase())!=null){
							temp.setDatatype(DataType.CLOB);
							item.setItemtype("M");
						}
						temp.setName(fieldname);
						strpre.append(item.getItemid().toUpperCase());
						strpre.append("=");
						if("signature".equalsIgnoreCase(item.getItemid())){
							continue;
						}
						HashMap map = new HashMap();
						map.put("xxxxxx", "");
						if(temp.getName().startsWith("S_")){//签章特殊处理字段名
							temp.setName("Signature");
						}
						value=DataTable.getValueByFieldType(rset,temp,map);
						if(item.isDate()&&!"".equalsIgnoreCase(value))
						{
							Date ss=new Date(Long.parseLong(value));
							value=PubFunc.FormatDate(ss,"yyyy/MM/dd");
						}
						value = value.replace(",", "，");
						strpre.append(value);
						strpre.append(",");
					}
					strchg.append(item.getItemid().toUpperCase());
					strchg.append(",");
				} //for j loop end.

				if(this.infor_type==1)
				{
					if(isE0122_2&&e0122_value!=null&&e0122_value.trim().length()>0)
					{
						b0110_value=e0122_value;
						isB0110_2=true;
					}

					if(!isB0110_2)
					{
						String db_type="";
						if(operationtype!=0)
						{
							if(operationtype==1||operationtype==2) //1调出（须指定目标人员库）,=2离退(须指定目标人员库)
                            {
                                db_type=this.dest_base;  //主要为了移库操作
                            } else
							{
								db_type=rset.getString("basepre");
							}
						}
						else
						{
							db_type=this.dest_base;  //主要为了新调入人员，增加记录
						}


						String _a0100=rset.getString("a0100");
						/**如果目标库和源库不一致的话，则先更新数据，然后进行移库操作*/
						if(operationtype==2||operationtype==1) {
                            _a0100=desta0100;
                        }
	                    if (!"".equals(db_type)){//目标库没有设置 wangrd 2014-01-06
                            rowSet=dao.search("select  b0110,e0122 from "+db_type+"a01 where a0100='"+_a0100+"'");
                            if(rowSet.next())
                            {
                                b0110_self=rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"";
                                String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"";
                               if(e0122.length()==0) {
                                    b0110_value=rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"";
                                } else {
                                    b0110_value=e0122;
                                }
                            }
                        }
					}
				}

    RecordVo vo=new RecordVo("tmessage");
				vo.setString("username",this.t_username);
				if(dbw.isExistField("tmessage", "receivetype", false)) {
                    vo.setString("receivetype",this.t_type);
                }
				vo.setInt("state",0);
				if(this.infor_type==1)
				{
					vo.setString("b0110",b0110_value); //this.userview.getUserOrgId());
					if(b0110_self.length()==0)
					{
						CodeItem item=AdminCode.getCode("UN", b0110_value);
						if(item==null)
						{
							for(int e=b0110_value.length();e>0;e--)
							{
								if(AdminCode.getCode("UN", b0110_value.substring(0,e))!=null)
								{
									vo.setString("b0110_self", b0110_value.substring(0,e));
									break;
								}
							}
						}
					}
					else {
                        vo.setString("b0110_self", b0110_self);
                    }
				}
				else
				{
					vo.setString("b0110", desta0100);
					CodeItem item=AdminCode.getCode("UN", desta0100);
					if(item==null)
					{
						for(int e=desta0100.length();e>0;e--)
						{
							if(AdminCode.getCode("UN", desta0100.substring(0,e))!=null)
							{
								vo.setString("b0110_self", desta0100.substring(0,e));
								break;
							}
						}
					}
				}

				vo.setInt("nyear",nyear);
				vo.setInt("nmonth",nmonth);
				vo.setInt("type",0);
				vo.setInt("flag",0);
				if(this.infor_type==1) {
                    vo.setString("a0100",rset.getString("a0100"));
                }
				if(this.infor_type==1)
				{
					if(operationtype!=0)
					{
						if(operationtype==1||operationtype==2) //1调出（须指定目标人员库）,=2离退(须指定目标人员库)
						{
							vo.setString("a0100",desta0100);
							vo.setString("db_type",this.dest_base);  //主要为了移库操作
						}
						else
						{
							vo.setString("db_type",rset.getString("basepre"));
						}
					}
					else
					{
						/**对于调入模板，重新设置人员编码*/
						vo.setString("a0100",desta0100);
						vo.setString("db_type",this.dest_base);  //主要为了新调入人员，增加记录
					}
					vo.setString("a0101",rset.getString("a0101_1"));
				}
				vo.setInt("sourcetempid",this.tabid);
    	vo.setInt("noticetempid",Integer.parseInt(dest_id));
				vo.setString("changepre",strpre.toString());
				vo.setString("changelast",strlast.toString());
				vo.setString("change",strchg.toString());
				/**max id access mssql此字段是自增长类型*/
		if(Sql_switcher.searchDbServer()!=Constant.MSSQL)
				{
					int nid=DbNameBo.getPrimaryKey("tmessage", "id", this.conn);
					vo.setInt("id", nid);
				}
				vo.setInt("object_type",this.infor_type);
				vo.setInt("bread", 0);
				vo.setString("send_user", this.userview.getUserFullName()==""?this.userview.getUserName():this.userview.getUserFullName());//xcs modefied @2013-10-21
				if(Sql_switcher.searchDbServer()!=Constant.ORACEL){
				vo.setDate("receive_time", new Date());
				}else{
				vo.setDate("receive_time", new Date());
        }
        dao.addValueObject(vo);
			} //for i loop end.
		} // if end.
		if(rowSet!=null) {
            rowSet.close();
        }

	 }
	 catch(Exception ex)
	 {
		 ex.printStackTrace();
		 throw GeneralExceptionHandler.Handle(ex);
	 }
	}


	/**
	 * 校验必填项
	 * @param tablename
	 * @param fieldlist
	 */
	public void checkMustFillItem(String tablename,ArrayList fieldlist,String strWhere)throws GeneralException
	{
		boolean bflag=false;
		try
		{
			StringBuffer buf=new StringBuffer();
			StringBuffer cond=new StringBuffer();
			buf.append("select a0101_1 ");
			ArrayList filllist=new ArrayList();
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				//变化后指标且必填
				if(item.isFillable()&&item.isChangeAfter()&&item.getVarible()==0)
				{
					String state=this.userview.analyseFieldPriv(item.getItemid());
                	if(state!=null&& "0".equals(state)) {
                        state=this.userview.analyseFieldPriv(item.getItemid().toUpperCase(),0);	//员工自助权限
                    }
					if((state==null||!"2".equals(state))&& "0".equals(this.UnrestrictedMenuPriv_Input))//xgq this.UnrestrictedMenuPriv_Input.equals("0")
                    {
                        continue;
                    }
					//审批意见指标：变化后备注类型
					if(this.opinion_field!=null&&this.opinion_field.equalsIgnoreCase(item.getItemid()))//xgq 2011-8-17
                    {
                        continue;
                    }
					bflag=true;
					filllist.add(item);
					buf.append(",");
					String field_name=item.getItemid()+"_2";
					buf.append(field_name);
					cond.append("(");
					cond.append(field_name);
                cond.append("  is null ");
					if(item.isChar())
					{
						cond.append(" or ");
						cond.append(field_name);
						cond.append("=''");
					}
					cond.append(")");
					cond.append(" or ");
				}

			}//for i loop end.
			buf.append(" from ");
			buf.append(tablename);

			if(bflag)
			{
				buf.append(" where (");
    cond.setLength(cond.length()-4);
				buf.append(cond.toString());
    buf.append(") and ");
				buf.append(strWhere);


				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rset=dao.search(buf.toString());
				boolean bNotFill=false;
				StringBuffer strInfo=new StringBuffer();
				strInfo.append("下列信息不能为空,请填写完整!\n\r");
				ResultSetMetaData rsetmd=null;
	            rsetmd=rset.getMetaData();
				while(rset.next())
				{
					String a0101=rset.getString("a0101_1")==null?"":rset.getString("a0101_1");
					strInfo.append(a0101+"    ");
					for(int i=0;i<filllist.size();i++)
					{
						FieldItem item=(FieldItem)filllist.get(i);
						String field_name=item.getItemid()+"_2";
						String value=PubFunc.getValueByFieldType(rset,rsetmd,field_name.toLowerCase());
						if(value==null||value.length()==0)
						{
							strInfo.append("拟[");
							strInfo.append(item.getItemdesc());
							strInfo.append("]不能为空。");
                                }

                			}//for i loop end.
					bNotFill=true;
					strInfo.append("\n\r");
				}//for while end.
				if(bNotFill)
				{
					throw new GeneralException(strInfo.toString());
				}
				PubFunc.closeDbObj(rset);
			}


			String sql="select *  from "+tablename+" where "+strWhere;
			validateOnlyValue(sql,tablename);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 取得当前模板的校验公式(启用的)
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList getLogicExpressList()throws GeneralException
	{
		StringBuffer strsql=new StringBuffer();
		strsql.append("select * from hrpchkformula where flag=0 and validflag=1 and tabid=");
		strsql.append(this.tabid+"  order by seq");
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RowSet rset=dao.search(strsql.toString());
			String formula=null;
			while(rset.next())
			{
				formula=rset.getString("formula");
				if(formula==null|| "".equals(formula)) {
                    continue;
                }
				RecordVo vo=new RecordVo("hrpchkformula");
				vo.setString("name", rset.getString("name"));
				vo.setString("information", rset.getString("information"));
				vo.setString("formula", rset.getString("formula"));
				list.add(vo);
			}// loop end.
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	/**
	 * 审核逻辑表达式
	 */
	public void checkLogicExpress(String tablename,int task_id,ArrayList fieldlist)throws GeneralException
	{
		ArrayList list=getLogicExpressList();
		if(list.size()>0)
		{
			try
			{
				StringBuffer buf=new StringBuffer();
				StringBuffer strsql=new StringBuffer();
				ContentDAO dao=new ContentDAO(this.conn);
				StringBuffer strInfo=new StringBuffer();
				StringBuffer strInfoA=new StringBuffer();
				ArrayList conefieldlist=new ArrayList();
				for(int i=0;i<fieldlist.size();i++)
				{
					FieldItem oldfielditem=(FieldItem)fieldlist.get(i);
					FieldItem fielditem=(FieldItem)oldfielditem.clone();
					if(fielditem.getVarible()!=1)
					{
						if(fielditem.isChangeAfter()) {
                            fielditem.setItemid(fielditem.getItemid()+"_2");
                        }
						if(fielditem.isChangeBefore()){
							if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0){
								fielditem.setItemid(fielditem.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_1");
								fielditem.setItemdesc(""+sub_domain_map.get(""+i+"hz"));
								}
							else {
                                fielditem.setItemid(fielditem.getItemid()+"_1 ");
                            }
						}
					}
					conefieldlist.add(fielditem);
				}
				ArrayList midFieldlist=getMidVariableListByFunc(2);//getMidVariableList();
				conefieldlist.addAll(midFieldlist);

				/**计算临时变量,把临时变量加到变动处理表中去*/
				/**应用库前缀*/

				Object[] dbarr=null;
				if(this.infor_type==1)
				{
					ArrayList dblist=searchDBPreList(tablename);
					dbarr=dblist.toArray();
				}


					//公式解析类需要的where条件，不然计算整个表，计算可休天数的时候会慢很多。wangrd 20151103
					String whereText="";
					StringBuffer strwhere=new StringBuffer("");
					if(task_id!=0)
					{
						this.bz_tablename="templet_"+this.tabid;
						strwhere.append(" where  exists (select null from t_wf_task_objlink where templet_"+this.tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+this.tabid+".ins_id=t_wf_task_objlink.ins_id ");
						strwhere.append("  and task_id="+task_id+" and tab_id="+this.tabid+"  and submitflag=1  and (state is null or  state=0 ) ) ");
					}
					else
					{
						strwhere.append(" where ");
						if(isBEmploy())//员工通过自助平台发动申请
						{
							this.bz_tablename="g_templet_"+this.tabid;
							strwhere.append(" g_templet_"+this.tabid+".a0100='"+this.userview.getA0100()+"' and lower(g_templet_"+this.tabid+".basepre)='"+this.userview.getDbname().toLowerCase()+"'");
						}
						else
						{
							strwhere.append(" submitflag=1 ");
							this.bz_tablename=this.userview.getUserName()+"templet_"+this.tabid;
						}
					}
					if(!isComputeVar) {
                        addMidVarIntoGzTable(strwhere.toString(),dbarr,midFieldlist);
                    }
					whereText=strwhere.toString().substring(6);
			    boolean bUseTmpTable=false;
			    DbWizard dbw=new DbWizard(this.conn);
				String tmpTableName="t#"+this.userview.getUserName()+"_templet"; ;
				if(task_id!=0 || isBEmploy()){//审批表及自助申请表存在并发问题 ，需建临时表
					String strWhere =whereText;
					if(strWhere.toUpperCase().startsWith("WHERE")) {
                        strWhere=strWhere.substring(6);
                    } else if(strWhere.toUpperCase().startsWith(" WHERE")) {
                        strWhere=strWhere.substring(7);
                    }
					//生成临时表
					if(dbw.isExistTable(tmpTableName, false)) {
                        dbw.dropTable(tmpTableName);
                    }
					dbw.createTempTable(tablename, tmpTableName,"*", strWhere,"");
					if(dbw.isExistTable(tmpTableName, false)) {
                        bUseTmpTable=true;
                    } else {
                        tmpTableName=tablename;
                    }
				}
				else {
					tmpTableName=tablename;
				}

				//strInfo.append("下列人员信息审核有误!\n\r");
				for(int i=0;i<list.size();i++)
				{
					RecordVo vo=(RecordVo)list.get(i);
					String formula=vo.getString("formula");
					String name=vo.getString("name");
					String information=vo.getString("information");

					int infoGroupFlag=YksjParser.forPerson;
					if(this.infor_type==2) {
                        infoGroupFlag=YksjParser.forUnit;
                    }
					if(this.infor_type==3) {
                        infoGroupFlag=YksjParser.forPosition;
                    }
                    YksjParser yp=new YksjParser( this.userview ,conefieldlist,
							YksjParser.forNormal, YksjParser.LOGIC,infoGroupFlag, "Ht", "");
                            yp.setStdTmpTable(tmpTableName);
                            yp.setTempTableName(tmpTableName);
					yp.setCon(conn);
					if (!bUseTmpTable){//使用临时表时 就不用塞条件了
						if (whereText.length()>0){
							yp.setWhereText(whereText);
						}
					}

					if(formula.indexOf("执行存储过程")!=-1&&formula.indexOf("KqCheckResult")!=-1) //汉口银行
					{
						formula=formula.replaceAll("param", strwhere.toString().replaceAll("'", "\""));

					}

					yp.run(formula.trim());
					if(formula.indexOf("执行存储过程")!=-1) {
                        continue;
                    }
                    String strWhere = yp.getSQL();//公式的结果
					buf.setLength(0);
					if(task_id!=0)
					{
						buf.append(" where  exists (select null from t_wf_task_objlink where "+tmpTableName+".seqnum=t_wf_task_objlink.seqnum and "+tmpTableName+".ins_id=t_wf_task_objlink.ins_id ");
						buf.append("  and task_id="+task_id+" and tab_id="+this.tabid+"  and submitflag=1  and (state is null or  state=0 ) ) ");

						buf.append(" and  (");
						buf.append(strWhere+" )");
					}
					else
					{
						buf.append(" where ");
						if(isBEmploy())//员工通过自助平台发动申请
						{
							buf.append(tmpTableName+".a0100='"+this.userview.getA0100()+"' and lower("+tmpTableName+".basepre)='"+this.userview.getDbname().toLowerCase()+"'");
						}
						else
						{
							buf.append(" submitflag=1 ");
						}
						buf.append(" and (");
						buf.append(strWhere+" )");
					}
                strsql.setLength(0);
					String key="a0101_1";
					if(this.infor_type==2||this.infor_type==3) {
                        key="codeitemdesc_1";
                    }
					strsql.append("select "+key+" from ");
					strsql.append(tmpTableName);
					strsql.append(buf.toString());
					RowSet rset=dao.search(strsql.toString());
					strInfo.setLength(0);
					strInfo.append(name+"("+information+")\n\r");
					int idx=0;
					while(rset.next())
					{
						if(idx!=0) {
                            strInfo.append(",");
                        }
						if(this.infor_type==1) {
                            strInfo.append(rset.getString("a0101_1"));
                        } else if(this.infor_type==2||this.infor_type==3)
						{
							strInfo.append(rset.getString("codeitemdesc_1"));
						}
						idx++;
					}//loop end.
					if(idx>0)
					{
						if(strInfoA.length()>0) {
                            strInfoA.append("\n\r");
                        }
                                strInfoA.append(strInfo.toString());
					}
                        PubFunc.closeDbObj(rset);
                    }//for i loop end.
                    if (bUseTmpTable) {
              dbw.dropTable(tmpTableName);
                }
				if(strInfoA.length()>0) {
                    throw new GeneralException(strInfoA.toString());
                }
			}
			catch(Exception ex)
        {
            ex.printStackTrace();
				String message=ex.toString();
				if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
				{
					PubFunc.resolve8060(this.conn,this.bz_tablename);
					throw GeneralExceptionHandler.Handle(new Exception("请重新操作!"));
				}
				else {
                    throw GeneralExceptionHandler.Handle(ex);
                }

			}
		}
	}




	/**
	 * 校验必填项
	 * @param tablename
	 * @param fieldlist
	 */
	public void checkMustFillItem2(String tablename,ArrayList fieldlist,String where_str)throws GeneralException
	{
		boolean bflag=false;
		try
		{
			StringBuffer buf=new StringBuffer();
		//	StringBuffer cond=new StringBuffer();
			buf.append("select a0101_1 ");
			ArrayList filllist=new ArrayList();
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				/**变化后指标且必填*/
				if(item.isFillable()&&item.isChangeAfter()&&item.getVarible()==0)
				{
					bflag=true;
					filllist.add(item);
					buf.append(",");
					String field_name=item.getItemid()+"_2";
					buf.append(field_name);
				//	cond.append("(");
				//	cond.append(field_name);
				//	cond.append("  is null ");
					if(item.isChar())
					{
				//		cond.append(" or ");
				//		cond.append(field_name);
				//		cond.append("=''");
					}
				//	cond.append(")");
				//	cond.append(" or ");
				}

			}//for i loop end.


			ArrayList fillsetlist=new ArrayList();
			HashMap   setdomainMap=new HashMap();
			//判断子集必填项
			Object[] key = this.submap.keySet().toArray();
			TFieldFormat fieldformat=null;
			for(int i=0;i<key.length;i++)
	        {
	        	String setid=(String)key[i];
	        	FieldItem item=(FieldItem)this.submap.get(setid);
    String domain_id="";
    if(setid.contains("_")){//子集存在多个变化后的情况 wangrd 20160826
	        		String tmp =setid;
	        		int k = setid.indexOf("_");
	        		setid=tmp.substring(0,k);
	        		domain_id= tmp.substring(k+1,tmp.length());
	        	}
	        	String field_name = "t_"+setid.toLowerCase()+"_2";
	    		if (domain_id.length()>0){
	    			field_name = "t_"+setid.toLowerCase()+"_"+domain_id+"_2";;
	    		}
	        	/**默认值处理*/
	        	String xml_param=item.getFormula();
	        	TSubSetDomain setdomain=new TSubSetDomain(xml_param);
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
	    		if(isNeed&&item.isChangeAfter())
	    		{
	    			bflag=true;
	    			buf.append(","+field_name);
        		fillsetlist.add(item);
	    			setdomainMap.put(item.getItemid(), setdomain);
	    		}
	        }



			buf.append(" from ");
			buf.append(tablename);
			if(bflag)
			{
				buf.append(where_str);
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rset=dao.search(buf.toString());
				boolean bNotFill=false;
				StringBuffer strInfo=new StringBuffer();
				strInfo.append("下列信息不能为空,请填写完整!\n\r");
    if(PubFunc.isUseNewPrograme(userview)){
strInfo.append("<br />");
				}
				ResultSetMetaData rsetmd=null;
	            rsetmd=rset.getMetaData();
				while(rset.next())
				{
					String a0101=rset.getString("a0101_1")==null?"":rset.getString("a0101_1");

                StringBuffer error_info=new StringBuffer("");
					for(int i=0;i<filllist.size();i++)
					{
						FieldItem item=(FieldItem)filllist.get(i);
						String field_name=item.getItemid()+"_2";
						String value=PubFunc.getValueByFieldType(rset,rsetmd,field_name.toLowerCase());
						if(value==null||value.length()==0)
						{
							error_info.append("拟[");
							error_info.append(item.getItemdesc());
							error_info.append("]不能为空。");
						}

					}//for i loop end.

					//判断子集必填项
					for(int i=0;i<fillsetlist.size();i++)
					{
						FieldItem item=(FieldItem)fillsetlist.get(i);
						String field_name=item.getItemid()+"_2";
						String value=PubFunc.getValueByFieldType(rset,rsetmd,field_name.toLowerCase());
						if(value!=null&&value.length()>0)
						{
							TSubSetDomain setDomain=(TSubSetDomain)setdomainMap.get(item.getItemid());
							ArrayList fieldfmtlist=setDomain.getFieldfmtlist();
							ArrayList recordList=setDomain.getRecordList(value.trim());
							HashSet set=new HashSet();
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
					    					set.add(fieldformat.getTitle());
					    				}

					    			}
					    		}
							}

							if(set.size()>0)
							{
								FieldSet _set =DataDictionary.getFieldSetVo(setDomain.getSetname());
								error_info.append("子集["+_set.getFieldsetdesc()+"] ");
								for(Iterator t=set.iterator();t.hasNext();)
								{
									error_info.append("拟[");
									error_info.append((String)t.next());
									error_info.append("]不能为空。");
								}
							}

						}

					}

					if(error_info.length()>0)
					{
						bNotFill=true;
						error_info.append("\n\r");
						strInfo.append(a0101+"    ");
						strInfo.append(error_info.toString());
					}
				}//for while end.



				if(bNotFill)
				{
					throw new GeneralException(strInfo.toString());
				}
				PubFunc.closeDbObj(rset);
			}


			String sql="select *  from "+tablename+where_str;
			validateOnlyValue(sql,tablename);

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}



	/**
	 * 校验需提交的数据是否已经被删除或移库
	 * @param tablename
	 * @param task_id
	 * @throws GeneralException
	 */
	public void checkNoRecord(String tablename,int task_id)throws GeneralException
	{
		RowSet rowSet=null;
		try
		{
			StringBuffer buf_str=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
if(this.infor_type==1) {
                buf.append("select a0101_1 ");
            } else if(this.infor_type==2||this.infor_type==3)
			{
				buf.append("select codeitemdesc_1");
				if(this.infor_type==2) {
                    buf.append(",b0110");
                } else if(this.infor_type==3) {
                    buf.append(",e01a1");
                }
			}
			buf.append(" from ");
			buf.append(tablename);
			if(task_id!=0)
			{
					buf.append(" where task_id=");
					buf.append(task_id);
					buf.append(" and submitflag=1 ");
    }
    else
    {
            buf.append(" where ");
					if(isBEmploy())//员工通过自助平台发动申请
{
       return;
                    } else {
                        buf.append(" submitflag=1 ");
}
			}
			ArrayList basepre=new ArrayList();
			if(this.infor_type==1)
			{
				rowSet=dao.search(buf.toString().replaceAll("a0101_1"," distinct basepre "));
				while(rowSet.next()) {
                    basepre.add(rowSet.getString(1));
                }
				for(int i=0;i<basepre.size();i++)
				{
					String _str=buf.toString();
					String nbase=(String)basepre.get(i);
					_str+=" and lower(basepre)='"+nbase.toLowerCase()+"' and not exists (select null from "+nbase+"A01 where "+nbase+"A01.a0100="+tablename+".a0100 ) ";
					rowSet=dao.search(_str.toString());
					while(rowSet.next())
					{
						buf_str.append(","+rowSet.getString(1));
					}
				}
			}
			else
			{
				String _str=buf.toString();
				String _key="b0110";
				String _table="B01";
				if(this.infor_type==3)
				{
					_key="e01a1";
					_table="K01";
				}
				_str+=" and not exists (select null from "+_table+" where "+_table+"."+_key+"="+tablename+"."+_key+" ) ";
				rowSet=dao.search(_str.toString());
				while(rowSet.next())
				{
					String temp=rowSet.getString(2);
					if(temp!=null&&temp.trim().charAt(0)=='B') {
                        continue;
                    }
					buf_str.append(","+rowSet.getString(1));
				}
			}
			if(buf_str.length()>0)
			{
				String strInfo="下列数据在信息库中已不存在,不能提交入库!\n\r   ";
				strInfo+=buf_str.substring(1);
				throw new GeneralException(strInfo);
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
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
	}




	/**
	 * 校验必填项（批量）
	 * @param tablename
	 * @param fieldlist
	 */
	public void checkMustFillItem_batch(String tablename,ArrayList fieldlist,String task_ids)throws GeneralException
	{
		boolean bflag=false;
		boolean version = PubFunc.isUseNewPrograme(this.userview);
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
	        com.hjsj.hrms.module.template.utils.TemplateUtilBo utilBo=new com.hjsj.hrms.module.template.utils.TemplateUtilBo(conn, userview);
	        TemplateModuleParam templateModuleParam = new TemplateModuleParam(this.conn,this.userview);
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
					String state=this.userview.analyseFieldPriv(item.getItemid());
                	if(state!=null&& "0".equals(state)) {
                        state=this.userview.analyseFieldPriv(item.getItemid().toUpperCase(),0);	//员工自助权限
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
				buf.append("  and task_id in ("+task_ids+") and tab_id="+this.tabid+"  and submitflag=1  and (state is null or  state=0 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userview.getUserName().toLowerCase()+"' or lower(username)='"+this.userview.getDbname().toLowerCase()+this.userview.getA0100()+"' ) ) )) ");

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
					StringBuffer error_info=new StringBuffer("");
					StringBuffer error_info_sub=new StringBuffer("");
					for(int i=0;i<filllist.size();i++)
					{
						FieldItem item=(FieldItem)filllist.get(i);
						 if (item.getItemid().startsWith("S_")){//判断电子签章
	                            String value=PubFunc.getValueByFieldType(rset,rsetmd,"signature");
	                            String checkStr="UserName=\""+this.userview.getUserName()+"\"";
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
                                                                    error_info.append(item.getItemdesc());
                                                                                                error_info.append("、");
										}
									}else{
										error_info.append(item.getItemdesc());
			                            error_info.append("、");
									}
	                            }
	                            else if(value==null || !value.contains(item.getItemid())){
	                                error_info.append(item.getItemdesc());
	                                error_info.append("、");
	                            }
	                        }
						else if(item.getItemid().startsWith("attachment")){//附件
                        	if(isAttachmentState){
                        		ArrayList valuelist = this.checkAttachMustFill(ins_id,item,basepre,a0100);//为了拿到附件的设置信息，需要窜入FieldItem对象。
                                                if(valuelist.size()==0 && version){
							error_info.append("[");
        							error_info.append(item.getItemdesc());
                            							error_info.append("]、");
        						}
        					}
                        }
                        else {
                            String field_name=item.getItemid()+"_2";
                            String value=PubFunc.getValueByFieldType(rset,rsetmd,field_name.toLowerCase(),true);//liuyz 32377 数值型指标用户没有填，true:返回空串，false返回0.0。
                            if(value==null||value.length()==0/*||(item.getItemtype().equalsIgnoreCase("N")&&Double.parseDouble(value)==0)*/)
                            {
                                error_info.append("[");
                                error_info.append(item.getItemdesc());
                                error_info.append("]、");
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
					    					if("attach".equalsIgnoreCase(itemid))
					    					{
					    						FieldSet set_vo=DataDictionary.getFieldSetVo(setDomain.getSetname().toLowerCase());
					    						if(set_vo!=null) {
                                                    set.add(set_vo.getFieldsetdesc()+" : "+fieldformat.getTitle());
                                                } else {
                                                    set.add(fieldformat.getTitle());
                                                }
					    					}
					    					else {
                                                set.add(fieldformat.getTitle());
                                   }
					    				}

					    			}
					    		}
							}

							if(set.size()>0)
							{
								for(Iterator t=set.iterator();t.hasNext();)
								{
									error_info.append("[");
									error_info.append((String)t.next());
									error_info.append("]、");
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
					                	a_state=this.userview.analyseFieldPriv(item_.getItemid());
					                }
					                if(fieldMap!=null&&fieldMap.get(fieldname)!=null){
			                			//if (!"0".equals(task_id)) {// 如果不是发起人的话,那么就要判断节点的读写权限
			                			a_state = ((String)fieldMap.get(fieldname)).toLowerCase();
			                			//}
				                	}
					                if("1".equals(this.userview.getHm().get("fillInfo"))) {
                                        a_state="2";
                                    }
								}
				    			if(fieldformat.isBneed()&&"2".equals(a_state))
				    			{
				    				String itemid=fieldformat.getName().toLowerCase();
			    					if("attach".equalsIgnoreCase(itemid))
			    					{
			    						FieldSet set_vo=DataDictionary.getFieldSetVo(setDomain.getSetname().toLowerCase());
			    						if(set_vo!=null) {
                                            set.add(set_vo.getFieldsetdesc()+" : "+fieldformat.getTitle());
                                        } else {
                                            set.add(item.getItemdesc()+"："+fieldformat.getTitle());//bug 31468
                                  }
			    					}
			    					else {
                                        set.add(item.getItemdesc()+"："+fieldformat.getTitle());//bug 31468
                                    }
			    				}
				    		}
							if(set.size()>0)
							{
								for(Iterator t=set.iterator();t.hasNext();)
								{
									error_info.append("[");
									error_info.append((String)t.next());
									error_info.append("]、");
								}
							}else{
								if(error_info.length()==0) {
									error_info_sub.append("[");
									error_info_sub.append(item.getItemdesc());
									error_info_sub.append("]、");
								}else {
									error_info.append("[");
									error_info.append(item.getItemdesc());
									error_info.append("]、");
								}
							}
						}

					}
					if(error_info.length()>0&&error_info_sub.length()>0){
						error_info.append(error_info_sub);
						error_info.setLength(error_info.length()-1);
					    error_info.append(" 指标未填写。");
						bNotFill=true;
						error_info.append("\n\r");
						strInfo.append(a0101+"的 ");
						strInfo.append(error_info.toString());
					}
					else if(error_info.length()>0)
					{
					    error_info.setLength(error_info.length()-1);
					    error_info.append(" 指标未填写。");
						bNotFill=true;
						error_info.append("\n\r");
						strInfo.append(a0101+"的 ");
						strInfo.append(error_info.toString());
					}else if(error_info_sub.length()>0){
						error_info_sub.setLength(error_info_sub.length()-1);
						error_info_sub.append(" 子集未填写。");
						bNotFill=true;
						error_info_sub.append("\n\r");
						strInfo.append(a0101+"的 ");
						strInfo.append(error_info_sub.toString());
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
			sql.append("  and task_id in ("+task_ids+") and tab_id="+this.tabid+"  and submitflag=1  and (state is null or  state=0 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userview.getUserName().toLowerCase()+"' or lower(username)='"+this.userview.getDbname().toLowerCase()+this.userview.getA0100()+"' ) ) )) ");

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
		//30546 增加锁版本校验
		boolean version = PubFunc.isUseNewPrograme(this.userview);
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
					String state=this.userview.analyseFieldPriv(item.getItemid());
                	if(state!=null&& "0".equals(state)) {
                        state=this.userview.analyseFieldPriv(item.getItemid().toUpperCase(),0);	//员工自助权限
}

 	if(FieldPriv!=null&&FieldPriv.get(fieldname.toLowerCase()+"_2")!=null){
                		state = ""+FieldPriv.get(fieldname.toLowerCase()+"_2");
//                		if(state.equals("2")) //bug 49844 节点上设置的写权限不应置为无权限
//                			state ="0";
                		if("3".equals(state)) {
                            state ="2";
                        }
                	}
                	if("1".equals(this.userview.getHm().get("fillInfo"))){//特殊情况 由外部链接进入
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
                	state=this.userview.analyseTablePriv(setid);
                }
                if(fieldMap!=null&&fieldMap.get(field_name)!=null){
        			//if (task_id!=0) {// 如果不是发起人的话,那么就要判断节点的读写权限
        			state = (String)fieldMap.get(field_name);
        			//}
                }
                if("1".equals(this.userview.getHm().get("fillInfo"))) {
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
				/*if(this.infor_type==2) //单位
				{
					whl=" and b0110=to_id ";
				}
				else if(this.infor_type==3)
				{
					whl=" and e01a1=to_id ";
				}*/
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
					buf.append("  and task_id="+task_id+" and tab_id="+this.tabid+"  and submitflag=1  and (state is null or  state=0 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userview.getUserName().toLowerCase()+"' or lower(username)='"+this.userview.getDbname().toLowerCase()+this.userview.getA0100()+"' ) ) )) ");

				//	buf.append(" and ");
				//	cond.setLength(cond.length()-4);
				//	buf.append(cond.toString());
				}
				else
				{
					buf.append(" where ");
					if(isBEmploy())//员工通过自助平台发动申请
					{
						buf.append(" a0100='"+this.userview.getA0100()+"' and lower(basepre)='"+this.userview.getDbname().toLowerCase()+"'");
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
				if(PubFunc.isUseNewPrograme(userview)){
					strInfo.append("<br />");
				}
				ResultSetMetaData rsetmd=null;
	            rsetmd=rset.getMetaData();
	            com.hjsj.hrms.module.template.utils.TemplateUtilBo utilBo=new com.hjsj.hrms.module.template.utils.TemplateUtilBo(conn, userview);
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
					StringBuffer error_info=new StringBuffer("");
					StringBuffer error_info_sub=new StringBuffer("");
					for(int i=0;i<filllist.size();i++)
					{
						FieldItem item=(FieldItem)filllist.get(i);
                        if (item.getItemid().startsWith("S_")){//判断电子签章
                            String value=PubFunc.getValueByFieldType(rset,rsetmd,"signature");
                            String checkStr="UserName=\""+this.userview.getUserName()+"\"";
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
										error_info.append(item.getItemdesc());
			                            error_info.append("、");
                                                            }
                                                        }else{
                                                       error_info.append(item.getItemdesc());
		                            error_info.append("、");
								}
                            }
                            else if(value==null || !value.contains(item.getItemid())){
                                error_info.append(item.getItemdesc());
                                error_info.append("、");
                            }
                        }
                        else if(item.getItemid().startsWith("attachment")){//附件
                        	if(isAttachmentState){
                        		ArrayList valuelist = this.checkAttachMustFill(ins_id,item,basepre,a0100);//为了拿到附件的设置信息，需要窜入FieldItem对象。
                        		//30546  linbz 附件增加锁版本校验
        						if(valuelist.size()==0 && version){
        							error_info.append("[");
        							error_info.append(item.getItemdesc());
        							error_info.append("]、");
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
                            if("1".equals(this.userview.getHm().get("fillInfo"))){
                            	if(value.indexOf("临时人员_")!=-1){
                            		value="";
                            	}
                            }
                            if(value==null||value.length()==0/*||(item.getItemtype().equalsIgnoreCase("N")&&Double.parseDouble(value)==0)*/)
                            {
                                error_info.append("[");
                                error_info.append(item.getItemdesc());
                                error_info.append("]、");
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
						                	a_state=this.userview.analyseFieldPriv(item_.getItemid());
						                }
						                if(fieldMap!=null&&fieldMap.get(fieldname)!=null){
				                			//if (task_id!=0) {// 如果不是发起人的话,那么就要判断节点的读写权限
				                			a_state = ((String)fieldMap.get(fieldname)).toLowerCase();
				                			//}
					                	}
						                if("1".equals(this.userview.getHm().get("fillInfo"))) {
                                            a_state="2";
                                        }
									}
					    			if(fieldformat.isBneed()&&"2".equals(a_state))
					    			{
					    				String itemid=fieldformat.getName().toLowerCase();
					    				if(map.get(itemid)==null||((String)map.get(itemid)).trim().length()==0)
					    				{
					    					if("attach".equalsIgnoreCase(itemid))
					    					{
					    						FieldSet set_vo=DataDictionary.getFieldSetVo(setDomain.getSetname().toLowerCase());
					    						if(set_vo!=null) {
                                                    set.add(set_vo.getFieldsetdesc()+" : "+fieldformat.getTitle());
                                                } else {
                                                    set.add(item.getItemdesc()+"："+fieldformat.getTitle());//bug 31468
                                                }
					    					}
					    					else {
                                                set.add(item.getItemdesc()+"："+fieldformat.getTitle());//bug 31468
                                            }
					    				}

					    			}
					    		}
							}

							if(set.size()>0)
							{
								for(Iterator t=set.iterator();t.hasNext();)
                                        {
                                            error_info.append("[");
                							error_info.append((String)t.next());
									error_info.append("]、");
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
					                	a_state=this.userview.analyseFieldPriv(item_.getItemid());
					                }
					                if(fieldMap!=null&&fieldMap.get(fieldname)!=null){
			                			//if (task_id!=0) {// 如果不是发起人的话,那么就要判断节点的读写权限
			                			a_state = ((String)fieldMap.get(fieldname)).toLowerCase();
			                			//}
				                	}
					                if("1".equals(this.userview.getHm().get("fillInfo"))) {
                                        a_state="2";
                                    }
								}
				    			if(fieldformat.isBneed()&&"2".equals(a_state))
                                    {
                                        String itemid=fieldformat.getName().toLowerCase();
			    					if("attach".equalsIgnoreCase(itemid))
			    					{
			    						FieldSet set_vo=DataDictionary.getFieldSetVo(setDomain.getSetname().toLowerCase());
			    						if(set_vo!=null) {
                                            set.add(set_vo.getFieldsetdesc()+" : "+fieldformat.getTitle());
                                        } else {
                                            set.add(item.getItemdesc()+"："+fieldformat.getTitle());//bug 31468
                                        }
			    					}
			    					else {
                                        set.add(item.getItemdesc()+"："+fieldformat.getTitle());//bug 31468
                                    }
			    				}
				    		}
							if(set.size()>0)
							{
								for(Iterator t=set.iterator();t.hasNext();)
								{
									error_info.append("[");
									error_info.append((String)t.next());
									error_info.append("]、");
								}
							}else{
								if(error_info.length()==0) {
									error_info_sub.append("[");
									error_info_sub.append(item.getItemdesc());
									error_info_sub.append("]、");
								}else {
									error_info.append("[");
									error_info.append(item.getItemdesc());
									error_info.append("]、");
								}
							}
						}

					}

					if(error_info.length()>0&&error_info_sub.length()>0){
						error_info.append(error_info_sub);
						error_info.setLength(error_info.length()-1);
					    error_info.append(" 指标未填写。");
						bNotFill=true;
						error_info.append("\n\r");
						strInfo.append(a0101+"的 ");
						strInfo.append(error_info.toString());
					}
					else if(error_info.length()>0)
					{
					    error_info.setLength(error_info.length()-1);
					    error_info.append(" 指标未填写。");
						bNotFill=true;
						error_info.append("\n\r");
						strInfo.append(a0101+"的 ");
						strInfo.append(error_info.toString());
					}else if(error_info_sub.length()>0){
						error_info_sub.setLength(error_info_sub.length()-1);
						error_info_sub.append(" 子集未填写。");
						bNotFill=true;
						error_info_sub.append("\n\r");
						strInfo.append(a0101+"的 ");
						strInfo.append(error_info_sub.toString());
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
					sql.append("  and task_id="+task_id+" and tab_id="+this.tabid+"  and submitflag=1  and (state is null or  state=0 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userview.getUserName().toLowerCase()+"' or lower(username)='"+this.userview.getDbname().toLowerCase()+this.userview.getA0100()+"' ) ) )) ");

			}
			else
			{
					sql.append(" where ");
					if(isBEmploy())//员工通过自助平台发动申请
					{
						sql.append(" a0100='"+this.userview.getA0100()+"' and lower(basepre)='"+this.userview.getDbname().toLowerCase()+"'");
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

	private ArrayList checkAttachMustFill(int ins_id, FieldItem item, String basepre, String a0100) {
		ArrayList recordList = new ArrayList();
		StringBuffer sb = new StringBuffer("");
		String username = userview.getUserName();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		RowSet frowset=null;
		try{
			if(isBEmploy()&&userview.getStatus()==0&&StringUtils.isNotBlank(userview.getDbname())&&StringUtils.isNotBlank(userview.getA0100())){
				DbNameBo db = new DbNameBo(this.conn);
				String loginNameField = db.getLogonUserNameField();
				String usernameSele="";
				if(StringUtils.isNotBlank(loginNameField)) {
					loginNameField = loginNameField.toLowerCase();
					String sql="select "+loginNameField+" as username from "+userview.getDbname()+"A01 where a0100='"+userview.getA0100()+"' ";
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
				if(this.userview.getVersion()>=70){
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
						TemplateParam param = new TemplateParam(this.conn,this.userview,Integer.valueOf(tabid));
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
				if(this.userview.getVersion()>=70){
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
									sb.append(" and m.flag='" + file_type+"'");
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

	/**
	 * 校验必填项
	 * @param tablename
	 * @param fieldlist
	 */
	private void checkMustFillItem(String tablename,ArrayList fieldlist)throws GeneralException
{
boolean bflag=false;
try
{
    StringBuffer buf=new StringBuffer();
			StringBuffer cond=new StringBuffer();
			buf.append("select a0101_1 ");
			ArrayList filllist=new ArrayList();
			/**分析权限标志,对变化前和变化后都加上权限
			 * =0无任何权限
			 * =1读权限
			 * =2写权限
			 * */
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				/**变化后指标且必填*/
				if(item.isFillable()&&item.isChangeAfter()&&item.getVarible()==0)
				{

					String state=this.userview.analyseFieldPriv(item.getItemid());
                	if(state!=null&& "0".equals(state)) {
                        state=this.userview.analyseFieldPriv(item.getItemid().toUpperCase(),0);	//员工自助权限
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
					buf.append(",");
					String field_name=item.getItemid()+"_2";
					buf.append(field_name);
					cond.append("(");
					cond.append(field_name);
					cond.append("  is null ");
					if(item.isChar())
					{
						cond.append(" or ");
						cond.append(field_name);
                            cond.append("=''");
                            }
                            cond.append(")");
				cond.append(" or ");
				}

			}//for i loop end.
			buf.append(" from ");
			buf.append(tablename);

			if(bflag)
			{
				buf.append(" where ");
				cond.setLength(cond.length()-4);
				buf.append(cond.toString());

				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rset=dao.search(buf.toString());
				boolean bNotFill=false;
				StringBuffer strInfo=new StringBuffer();
				strInfo.append("下列信息不能为空,请填写完整!\n\r");
        ResultSetMetaData rsetmd=null;
	            rsetmd=rset.getMetaData();
				while(rset.next())
				{
					String a0101=rset.getString("a0101_1")==null?"":rset.getString("a0101_1");
					strInfo.append(a0101+"    ");
					for(int i=0;i<filllist.size();i++)
					{
                    FieldItem item=(FieldItem)filllist.get(i);
						String field_name=item.getItemid()+"_2";
						String value=PubFunc.getValueByFieldType(rset,rsetmd,field_name.toLowerCase());
						if(value==null||value.length()==0)
						{
							strInfo.append("拟[");
							strInfo.append(item.getItemdesc());
                                    strInfo.append("]不能为空。");
						}

					}//for i loop end.
					bNotFill=true;
					strInfo.append("\n\r");
				}//for while end.
				if(bNotFill)
				{
					throw new GeneralException(strInfo.toString());
				}
				PubFunc.closeDbObj(rset);
			}



			StringBuffer sql=new StringBuffer("");
			sql.append("select * from "+tablename);
			validateOnlyValue(sql.toString(),tablename);



		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
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
* 当前审批人是否是单一节点下多审批人中的一个
	 * @param taskid
	 * @param ins_id
	 * @return
	 */
	public boolean isParallel(int taskid,int ins_id)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("select count(*) as nm from t_wf_task_datalink where ");
			buf.append("  ins_id="+ins_id+" and task_id="+taskid);
			RowSet rset=dao.search(buf.toString());
			if(rset.next()) {
                if(rset.getInt(1)>0)
					{
flag=true;}
            }
			PubFunc.closeDbObj(rset);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
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
	 * 合并 和 划转业务 需判断同一组中的记录是否都被选中，同时去掉没有指定（划转|合并）目标记录的选中标记
	 * @param sql
	 * @throws GeneralException
	 */
	public void checkSelectedRule(String sql,String srctab,String task_id)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
HashSet set=new HashSet();
			RowSet rowSet=dao.search(sql);
			if(this.infor_type==2||(this.infor_type==3&&this.operationtype==8))
			{
				while(rowSet.next())
				{
					if(rowSet.getString("to_id")!=null) {
                        set.add(rowSet.getString("to_id"));
                }
				}
				String to_ids="";
				for(Iterator t=set.iterator();t.hasNext();) {
                    to_ids+=",'"+(String)t.next()+"'";
                }
				if(to_ids.length()==0)
				{
					String desc="合并";
					String target = "机构";
					if(this.infor_type==3) {
target = "岗位";
       }
					if(this.operationtype==9) {
                        desc="划转";
                    }
					//throw GeneralExceptionHandler.Handle(new Exception("没有选中符合业务要求"+desc+"的记录!"));
					throw GeneralExceptionHandler.Handle(new Exception("请选择需要"+desc+"的目标"+target+"！"));
				}
				if(task_id==null||task_id.trim().length()==0|| "0".equals(task_id.trim())) {
rowSet=dao.search("select count(*) from "+srctab+" where to_id in ("+to_ids.substring(1)+") and ( submitflag is null or submitflag=0 )");
} else{
					StringBuffer strsql = new StringBuffer();
					strsql.append("select count(*) from "+srctab+" where  to_id in ("+to_ids.substring(1)+") ");
        strsql.append(" and  exists (select null from t_wf_task_objlink where "+srctab+".seqnum=t_wf_task_objlink.seqnum and "+srctab+".ins_id=t_wf_task_objlink.ins_id ");
					strsql.append("  and task_id="+task_id+"   and ( submitflag is null or submitflag=0 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userview.getUserName().toLowerCase()+"' or lower(username)='"+this.userview.getDbname().toLowerCase()+this.userview.getA0100()+"' ) ) )) ");
					rowSet=dao.search(strsql.toString());

				}
				if(rowSet.next())
				{
					if(rowSet.getInt(1)>0)
					{
						if(this.operationtype==8) {
                            throw GeneralExceptionHandler.Handle(new Exception("合并业务同组内的记录需都被选中!"));
                        }
						if(this.operationtype==9) {
                            throw GeneralExceptionHandler.Handle(new Exception("划转业务同组内的记录需都被选中!"));
                        }
					}
				}
				//抛出同一组号只有一条数据的记录
				for(Iterator t=set.iterator();t.hasNext();){
					String to_id="'"+(String)t.next()+"'";
					if(task_id==null||task_id.trim().length()==0|| "0".equals(task_id.trim())) {
                        rowSet=dao.search("select count(*) from "+srctab+" where to_id in ("+to_id+") ");
                    } else{
						StringBuffer strsql = new StringBuffer();
						strsql.append("select count(*) from "+srctab+" where  to_id in ("+to_id+") ");
						strsql.append(" and  exists (select null from t_wf_task_objlink where "+srctab+".seqnum=t_wf_task_objlink.seqnum and "+srctab+".ins_id=t_wf_task_objlink.ins_id ");
						strsql.append("  and task_id="+task_id+"  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userview.getUserName().toLowerCase()+"' or lower(username)='"+this.userview.getDbname().toLowerCase()+this.userview.getA0100()+"' ) ) )  ) ");
						rowSet=dao.search(strsql.toString());
					}

					if(rowSet.next())
					{
						if(rowSet.getInt(1)<2)
						{
							if(this.operationtype==8) {
                                throw GeneralExceptionHandler.Handle(new Exception("合并业务同组内的记录仅一条数据不允许操作!"));
                            }
							if(this.operationtype==9) {
                                throw GeneralExceptionHandler.Handle(new Exception("划转业务同组内的记录仅一条数据不允许操作!"));
                            }
						}
					}
				}
				//去掉没有指定（划转|合并）目标记录的选中标记
				dao.update("update "+srctab+" set submitflag=0 where to_id is null or to_id=''");
				if(rowSet!=null) {
                    rowSet.close();
                }
}
else if(this.infor_type==3&&this.operationtype==9)
			{
				int num=0;
				while(rowSet.next())
				{
					num++;
					if(rowSet.getString("parentid_2")!=null) {
                        set.add(rowSet.getString("parentid_2"));
                    }
				}
				String to_ids="";
				for(Iterator t=set.iterator();t.hasNext();) {
                    to_ids+=",'"+(String)t.next()+"'";
                }
				if(num>0&&to_ids.length()==0)
				{
					String	desc="划转";
					throw GeneralExceptionHandler.Handle(new Exception("没有选中符合业务要求"+desc+"的记录!"));

				}

				//去掉没有指定（划转|合并）目标记录的选中标记
				dao.update("update "+srctab+" set submitflag=0 where parentid_2 is null or parentid_2=''");
				if(rowSet!=null) {
                    rowSet.close();
                }
			}

		}
		catch(Exception ex)
		{
ex.printStackTrace();
throw GeneralExceptionHandler.Handle(ex);
		}
	}




	String sp_yj="01";  //审批意见  01:同意


	/**
	 * 把本实例中，所有人员信息导入到相应的应用库中去。
	 * templet_xxx ->Usr[OTH|TRS]
	 * @param task_id 实例号 任务号
	 * @return
	 * @throws GeneralException
	 */
	public boolean expDataIntoArchive(int task_id)throws GeneralException
	{
		boolean bflag=true;
		this.task_id=task_id;
		ArrayList fieldlist=this.getAllFieldItem();
		TmessageBo tmessageBo=new TmessageBo(this.conn,this.userview,this.infor_type,this.msg_template,this.operationtype);
		/**流程实例*/
		//this.ins_id=ins_id;
		/**任务号实例*/

		/**
		 * 如果对于人员调入的操作业务时，未指定数据库时
* 则提示报错，否则按默认的应用库进行数据更新\插入等操作
*/
if((this.dest_base==null|| "".equals(this.dest_base))&&(this.operationtype==0)) {
            throw new GeneralException(ResourceFactory.getProperty("error.notdefine.desbase"));
        }

		if(this.operationtype==4&&(this.dest_base==null|| "".equals(this.dest_base))) {
            throw new GeneralException(ResourceFactory.getProperty("error.notdefine.desddbase"));
        }

		//人员离退,人员调出,也必须指定目标库
		if((this.operationtype==2||this.operationtype==1)&&(this.dest_base==null|| "".equals(this.dest_base))){
		    throw new GeneralException(ResourceFactory.getProperty("error.notdefine.desbase"));
		}
		HashMap subhm=readUpdatesSetField(fieldlist);
		if(this.operationtype==0&&subhm.get("A01")==null)//=1:人员调出
		{
			throw new GeneralException(ResourceFactory.getProperty("error.input.a01read"));
		}
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		RowSet rsetc=null;
		String desta0100=null;
		try
		{
			String srctab="templet_"+tabid;
			StringBuffer strsql=new StringBuffer();
			StringBuffer strsqlforplace=new StringBuffer();
			StringBuffer strsqlbetwwon=new StringBuffer();
			String orderBytemp="";
			if(task_id==0)//sutemplet_xxx，以用户名有关的临时表
            {
                srctab=this.userview.getUserName()+srctab;
            }
			if(isBEmploy()&&task_id==0)//员工通过自助平台发动申请 //liuyz 空节点导致用户报批直接提交入库，这里不加task_id==0判断会造成查询表不对，报task_id无效。
            {
                srctab="g_templet_"+tabid;
            }
			/**
			 * 得到临时表的表结构
			 */
			HashMap tableColumnMap=new HashMap();
			strsql.append("select * from ");
			strsql.append(srctab);
			strsql.append(" where 1=2");
			rset = dao.search(strsql.toString());
			ResultSetMetaData mt=rset.getMetaData();
			for(int i=1;i<=mt.getColumnCount();i++)
			{
				String columnName=mt.getColumnName(i);
				tableColumnMap.put(columnName.toLowerCase(),"1");
			}
			strsql.setLength(0);
			strsql.append("select ");
			if(this.infor_type==1)
			{
				strsql.append("a0100,basepre,");
        }
        else if(this.infor_type==2) {
                strsql.append("b0110,");
        } else if(this.infor_type==3) {
                strsql.append("e01a1,");
        }
			strsql.append("state,");
			if(this.infor_type==2||this.infor_type==3) {
				strsql.append("codeitemdesc_1,");
				/*if(this.operationtype==5)
					strsql.append("codeitemdesc_2,parentid_2,");*/
				if(tableColumnMap.get("codeitemdesc_2")!=null) {
                    strsql.append("codeitemdesc_2,");
                }
				if(tableColumnMap.get("parentid_2")!=null) {
                    strsql.append("parentid_2,");
                }
				if(tableColumnMap.get("codesetid_2")!=null) {
                    strsql.append("codesetid_2,");
                }
				if(tableColumnMap.get("corcode_2")!=null) {
                    strsql.append("corcode_2,");
                }
				if(tableColumnMap.get("codeitemdesc_2")!=null) {
                    strsql.append("codeitemdesc_2,");
                }
				if(tableColumnMap.get("start_date_2")!=null) {
                    strsql.append("start_date_2,");
                }
				if(this.operationtype==8||this.operationtype==9) {
                    strsql.append("to_id,");
                }
}
strsql.setLength(strsql.length()-1);
			strsql.append(" from ");
			strsqlforplace.append(strsql);
			/////////////////////////////////
			String strsqlforplace_ = strsqlforplace.substring(0,strsqlforplace.indexOf(" from"));
			for(Object column:tableColumnMap.keySet()){
			     String column_ = column.toString();
			     if("appprocess".equalsIgnoreCase(column_)||"lasttime".equalsIgnoreCase(column_)||"messageid".equalsIgnoreCase(column_)||"appstate".equalsIgnoreCase(column_)||
			    		 "key_no".equalsIgnoreCase(column_)||"appuser".equalsIgnoreCase(column_)||"chgpk32".equalsIgnoreCase(column_)) {
                     continue;
                 }
			     if(strsqlforplace_.indexOf(column_+",")==-1) {
                     strsqlforplace_+=","+column_;
                 }
			}
			if(strsqlforplace_.length()>0) {
                strsqlforplace_ = strsqlforplace_+" from ";
            }
			////////////////////////////////
			strsql.append(srctab);

			if(task_id!=0)
			{
			/*	strsql.append(" where task_id=");
				strsql.append(task_id);
				strsql.append(" and submitflag=1"); */
				//暂时不确定是否要加角色范围控制
				strsqlbetwwon.append(" where  exists (select null from t_wf_task_objlink where templet_"+this.tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+this.tabid+".ins_id=t_wf_task_objlink.ins_id ");
				strsqlbetwwon.append("  and task_id="+task_id+" and tab_id="+this.tabid+" and state=1 ) ");
			}
			else
			{
				if(isBEmploy())//员工通过自助平台发动申请
				{
					strsqlbetwwon.append(" where basepre='");
					strsqlbetwwon.append(this.userview.getDbname());
					strsqlbetwwon.append("' and a0100='");
					strsqlbetwwon.append(this.userview.getA0100());
					strsqlbetwwon.append("'");
				}
				else {
                    strsqlbetwwon.append(" where submitflag=1");
                }
			}

			if((this.infor_type==2||this.infor_type==3)&&this.operationtype==8)
			{
			    if(this.infor_type==3){
			    	strsqlbetwwon.append(" ORDER BY  TO_ID,E01a1 DESC");
			    	orderBytemp=" ORDER BY  TO_ID,E01a1 DESC";//bug 43827 合并机构报错 备份orderby语句，后面替换使用
			    }else{
			    	strsqlbetwwon.append(" ORDER BY  TO_ID,B0110 DESC");
			    	orderBytemp=" ORDER BY  TO_ID,B0110 DESC";//bug 43827 合并机构报错 备份orderby语句，后面替换使用
			    }
			}
			strsql.append(strsqlbetwwon);
			////////////////////////

			int state=0;

	//校验需提交的数据是否已经被删除或移库

			if(!(operationtype==0||((this.infor_type==2||this.infor_type==3)&&this.operationtype==5)))//调入
			{
				checkNoRecord(srctab,task_id);
			}

			/**必填项校验*/
			if(!this.isValidateM_L)
			{
				checkMustFillItem(srctab,fieldlist,task_id);
				checkLogicExpress(srctab, task_id, fieldlist);
			}
			/**验证唯一性  */
			rset=dao.search(strsql.toString().replace(strsqlforplace, strsqlforplace_));
			String ca0100=null,srcbase=null;
			ArrayList conefieldlist=new ArrayList();
			if(this.msg_flag!=null&& "1".equals(this.msg_flag))
			{
				setCondFieldMs(fieldlist,conefieldlist);
			}

			if(this.infor_type==2||this.infor_type==3)
			{
				validateSysItem(tableColumnMap);
				/**如果为新建组织单元业务  */
			//	if(this.operationtype==5)
				{
					checkNewOrgFillItem(strsql.toString(),this.operationtype);
				}

				/**  如果为合并 和 划转业务 需判断同一组中的记录是否都被选中，同时去掉没有指定（划转|合并）目标记录的选中标记 */
				if(this.operationtype==8||this.operationtype==9)
				{
					checkSelectedRule(strsql.toString(),srctab,String.valueOf(task_id));
				}

			}




			DbWizard dbw=new DbWizard(this.conn);
			if(!dbw.isExistField("tmessage","b0110_self",false))
			{
				Table table = new Table("tmessage");
				Field field=new Field("B0110_Self","B0110_Self");
				field.setDatatype(DataType.STRING);
				field.setLength(30);
				table.addField(field);
				dbw.addColumns(table);
			}
	//		StringBuffer org_str=new StringBuffer("");


			if (("templet_"+tabid).equals(srctab))
			{
				String archive_flag="0";
				if("01".equals(sp_yj)) {
                    archive_flag="1";
                }
				{
					String sql=strsql.toString().replaceAll(strsqlforplace+srctab,"update "+srctab+" set archive_flag="+archive_flag);
					if(StringUtils.isNotBlank(orderBytemp)){//如果orderBytemp不为空，替换掉update中的orderby语句
						sql=sql.replace(orderBytemp, "");
					}
					dao.update(sql);
				}
			}
			if(this.infor_type==1&& "01".equals(sp_yj)) //批量提交人员非调入、内部调动模板数据
			{

				TemplateBo templateBo=new TemplateBo (this.conn,this.userview,this.tabid);
				templateBo.setRepreateSubmit(this.isRepreateSubmit);
				rsetc=dao.search(strsql.toString().replace(strsqlforplace,"select distinct basepre from "));
				while(rsetc.next())
				{
				//	System.out.println("---------");
					String temp_srcbase=rsetc.getString(1);
					templateBo.batchChangeSubmit(subhm,temp_srcbase,srctab,ins_id,task_id,this.isBEmploy(),this.submap);
				}
			}


			CreateCodeTableTrans cctt = new CreateCodeTableTrans();
			String _oriToId="";
			String _toId="";
			while(rset.next())
			{
				StringBuffer org_str=new StringBuffer("");
				this.mappingStr="";

				if(this.infor_type==1)
				{
					ca0100=rset.getString("a0100");
					srcbase=rset.getString("basepre");
				}
				else if(this.infor_type==2) {
                    ca0100=rset.getString("b0110");
                } else if(this.infor_type==3) {
                    ca0100=rset.getString("e01a1");
                }

				state=rset.getInt("state");

				/**如果审批意见为同意，才能进行入库归档处理*/
				if(this.infor_type==1)
				{
					// 更新templet_tabid表的入库标志 wangrd 2013-11-28
					if (("templet_"+tabid).equals(srctab)){
					   //String ins_id = rset.getString("ins_id");
					   /*
					   String archive_flag="0";
					   if(sp_yj.equals("01")) archive_flag="1";
					   String sql="update  templet_"+tabid+" set archive_flag="+archive_flag
					              +" where upper(basepre)='"+srcbase.toUpperCase()+"'"
					              +" and A0100='"+ca0100+"'"
					              +" and ins_id="+ins_id;
	                   dao.update(sql);
					    */
					}

					if("01".equals(sp_yj)) {
                        desta0100=changeSubmit(subhm,srcbase,ca0100,srctab,state);
                    } else {
                        desta0100=ca0100;
                    }

				}
				else if(this.infor_type==2||this.infor_type==3)
				{
					LazyDynaBean beanInfo=new LazyDynaBean();
					beanInfo.set("keyValue", ca0100);
					beanInfo.set("codeitemdesc",rset.getString("codeitemdesc_1"));
					if(this.operationtype==5||(this.operationtype==8&&ca0100.charAt(0)=='B'))
					{
						if(this.infor_type==2) {
                            beanInfo.set("codesetid",rset.getString("codesetid_2"));
                        } else {
                            beanInfo.set("codesetid","@K");
                        }
						beanInfo.set("parentid",rset.getString("parentid_2")==null?"":rset.getString("parentid_2"));
					}
					if(tableColumnMap.get("corcode_2")!=null) {
                            beanInfo.set("corcode",rset.getString("corcode_2")!=null?rset.getString("corcode_2"):"");
                    }
					if(tableColumnMap.get("codeitemdesc_2")!=null) {
                        beanInfo.set("codeitemdesc",rset.getString("codeitemdesc_2"));
                    }
					if(tableColumnMap.get("start_date_2")!=null) {
                        beanInfo.set("start_date",rset.getDate("start_date_2"));
                    }

					boolean isToOrg=true;
					if(this.operationtype==8||this.operationtype==9)
					{
						String to_id=rset.getString("to_id")!=null?rset.getString("to_id"):"";
						if(this.infor_type==2)
						{
							if(!rset.getString("b0110").equalsIgnoreCase(to_id)) {
                                isToOrg=false;
                            }
						}
						else if(this.infor_type==3)
						{
							if(this.operationtype==8)
							{
								if(!rset.getString("e01a1").equalsIgnoreCase(to_id)) {
                                    isToOrg=false;
                                }
							}
							else
							{
								to_id=rset.getString("parentid_2");
									isToOrg=false;
							}
						}
						beanInfo.set("to_id",to_id);
						beanInfo.set("ori_to_id",to_id);

						if(this.operationtype==8)
						{
							if(_oriToId==null||_oriToId.trim().length()==0){
								_oriToId=to_id;
								_toId=to_id;
							}
							if(_oriToId.equalsIgnoreCase(to_id))
							{
								beanInfo.set("to_id",_toId);
								if(_oriToId.charAt(0)=='B'&&_toId.charAt(0)!='B'&&ca0100.charAt(0)!='B') //合并为新的组织
                                {
                                    this.mappingStr+=","+ca0100+"="+_toId;
                                }
							}
							else {
                                _oriToId=to_id;
                            }
						}
					}
					if("01".equals(sp_yj))
					{
						desta0100=changeSubmitBK(subhm,ca0100,srctab,state,isToOrg,beanInfo,tableColumnMap);
						if(this.operationtype==8) {
                            _toId=(String)beanInfo.get("to_id");
                        }
				 		if(this.mappingStr!=null&&this.mappingStr.trim().length()>0&&this.userview.getHm().get("js_path")!=null) //追加dict.js文件
                                  {
                                      String js_info=cctt.getNewOrgInfo(this.mappingStr,this.conn);
				 			if(js_info.length()>0) {
                                PubFunc.appendOrgToDictJS((String)this.userview.getHm().get("js_path"),js_info);
                            }
				 		}

						if(this.operationtype==8||this.operationtype==9)
						{
							if(!isToOrg)
							{
								if(AdminCode.getCodeName("UN",ca0100).trim().length()>0) {
                                    org_str.append(" and B0110 like '"+ca0100+"%' ");
                                } else if(AdminCode.getCodeName("UM",ca0100).trim().length()>0) {
                                    org_str.append(" and E0122 like '"+ca0100+"%' ");
                                } else if(AdminCode.getCodeName("@K",ca0100).trim().length()>0) {
                                    org_str.append(" and E01A1 like '"+ca0100+"%' ");
                                }
							}
						}
						else
						{
							if(AdminCode.getCodeName("UN",ca0100).trim().length()>0) {
                                org_str.append(" and B0110 like '"+ca0100+"%' ");
                            } else if(AdminCode.getCodeName("UM",ca0100).trim().length()>0) {
                                org_str.append(" and E0122 like '"+ca0100+"%' ");
                            } else if(AdminCode.getCodeName("@K",ca0100).trim().length()>0) {
                                org_str.append(" and E01A1 like '"+ca0100+"%' ");
                            }
						}
					}
					else {
                        desta0100=ca0100;
                    }
				}

				destination_a0100.put(ca0100,desta0100);

				if((this.infor_type!=1)&&(this.operationtype==7||this.operationtype==8||this.operationtype==9||this.operationtype==6)) //机构调整模板向人事异动模板下通知单
				{
					if(org_str.length()>0)
					{
						HashMap param=new HashMap();
						if(this.msg_flag!=null&& "1".equals(this.msg_flag))
						{
                                    param.put("mag_condlist_complex",this.mag_condlist_complex);
							param.put("conexlist", getConexprList());
							param.put("conefieldlist",conefieldlist);
						}
						tmessageBo.expOrgDataIntoMessage(strsql.toString().replace(strsqlforplace, strsqlforplace_),ca0100,this.msg_flag,org_str.toString(),param,mappingStr); //机构调整模板向人事异动模板下通知单 下通知单报找不到A0101_1字段。
					}
				}
				else
				{
					if(this.msg_flag!=null&& "1".equals(this.msg_flag))
					{
						/***************按规则条件定义下发通知单***************/
						String exp="";
						String msgids="";
						String where="";
						ArrayList conexlist=getConexprList();
						for(int i=0;i<conexlist.size();i++)
						{
							CommonData da=(CommonData)conexlist.get(i);
							LazyDynaBean _bean=(LazyDynaBean)this.mag_condlist_complex.get(i);

							exp=da.getDataName();
							msgids=da.getDataValue();
							if(msgids==null||msgids.length()<=0) {
                                continue;
                            }
							where=expRuleTerm(exp,conefieldlist);
							if(where==null||where.length()<=0) {
                                continue;
                            }
							this.msg_template=StringUtils.split(msgids,",");
							if(this.infor_type==1){
								rsetc=dao.search(strsql.toString().replace(strsqlforplace, strsqlforplace_)+" and lower(basepre)='"+srcbase.toLowerCase()+"' and a0100='"+ca0100+"' and ( "+where+" )");	//下通知单报找不到A0101_1字段。
							}else if(this.infor_type==2){
								rsetc=dao.search(strsql.toString().replace(strsqlforplace, strsqlforplace_)+" and b0110='"+ca0100+"' and ( "+where+" )");	//下通知单报找不到A0101_1字段。
							}else if(this.infor_type==3){
								rsetc=dao.search(strsql.toString().replace(strsqlforplace, strsqlforplace_)+" and e01a1='"+ca0100+"' and ( "+where+" )");	//下通知单报找不到A0101_1字段。
							}
							if(rsetc.next())
							{
								t_username="";
								if(_bean!=null&&((String)_bean.get("user")).length()>0) {
                                    t_username=(String)_bean.get("user");
                                }
								if(_bean!=null&&((String)_bean.get("type")).length()>0) {
                                    t_type=(String)_bean.get("type");
                                }
								expDataIntoTmessage(rsetc,fieldlist,desta0100,subhm);
							}
							PubFunc.closeDbObj(rsetc);
						}
				    }

					else
					{
				    	t_username="";
						expDataIntoTmessage(rset,fieldlist,desta0100,subhm);
					}
				}
			} //while end
			/***************按规则条件定义下发通知单,结束***************/

			 //将单据信息发送至外部系统
			 {
				 SynOaService sos=new SynOaService();
				 String tab_ids=sos.getTabids();
				 if(tab_ids.indexOf(","+tabid+",")!=-1)
				 {
					if("2".equals((String)sos.getTabOptMap().get(String.valueOf(tabid))))
					{
						if(task_id==0&&isBEmploy()) {
                            sos.setSelfapply("1");
                        }
						String _info=sos.synOaService(String.valueOf(task_id),String.valueOf(tabid),this.userview);  //创建成功返回1，否则返回详细错误信息
						if(!"1".equals(_info)) {
                            throw GeneralExceptionHandler.Handle(new Exception(_info));
                        }
					}
				 }
			 }

			if(task_id>0&& "1".equals(this.archflag)) //原始单据归档 && 走审批流程的单据
			{
					//subDataToArchive(strsql.toString().replace(strsqlforplace, strsqlforplace_),"templet_"+this.tabid,"2");
			}
			if(this.infor_type!=1&&(this.operationtype==9||this.operationtype==8)) //划转||合并 重置 organization layer\grade字段内容
            {
                updateOrgLayerGrade();
            }
			this.setDestination_a0100(destination_a0100);
			if(this.infor_type==1&&(this.operationtype==1||this.operationtype==2)) {//自助申请-调出 离退 要更新流程的actorid
				if(this.bsp_flag&&getActor_Type(task_id)) {
                    updateTempletActorid(desta0100,this.ins_id+"");
                }
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeDbObj(rset);
			PubFunc.closeDbObj(rsetc);
		}
		return bflag;
	}

	/**
	 * 自助申请-调出 离退 要更新流程的actorid
	 * @param desta0100
	 * @param ins_id
	 */
	public void updateTempletActorid(String desta0100, String ins_id) {
		try {
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList paramList = new ArrayList();
			String sql = "update t_wf_instance set actorid=? where ins_id=?";
			paramList.add(this.dest_base+desta0100);
    paramList.add(ins_id);
			dao.update(sql, paramList);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询是不是自助申请
	 * @param task_id
	 * @return
	 */
	private boolean getActor_Type(int task_id) {
    	boolean isSelf = false;
    	RowSet rset=null;
    	try {
	    	ContentDAO dao=new ContentDAO(this.conn);
	    	StringBuffer sb = new StringBuffer();
	    	ArrayList list = new  ArrayList();
	    	sb.append("select  twi.actor_type from t_wf_task twt,t_wf_instance twi where twt.ins_id = twi.ins_id  and twt.task_id=?");
	    	list.add(task_id);
			rset = dao.search(sb.toString(),list);
			if(rset.next()){
				int actor_type = rset.getInt("actor_type");
				if(actor_type==1) {
            isSelf = true;
                }
			}
} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rset);
		}
		return isSelf;
	}

	/**
	 * 为conefieldlist设置通知单高级条件需涉及的指标
* @param fieldlist
	 * @param conefieldlist
	 */
private void setCondFieldMs(ArrayList fieldlist,ArrayList conefieldlist)
{
boolean isB0110=false;
for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem oldfielditem=(FieldItem)fieldlist.get(i);
			if(oldfielditem.isChangeBefore()&& "B0110".equalsIgnoreCase(oldfielditem.getItemid())) {
                isB0110=true;
            }
			FieldItem fielditem=(FieldItem)oldfielditem.clone();
			if(fielditem.getVarible()!=1)
			{
				if(fielditem.isChangeAfter()) {
                    fielditem.setItemid(fielditem.getItemid()+"_2");
                }
				if(fielditem.isChangeBefore()) {
                    fielditem.setItemid(fielditem.getItemid()+"_1 ");
                }
			}
			conefieldlist.add(fielditem);
		}
if(this.infor_type==1&&!isB0110) //判断列表中是否有单位名称指标,没有则最加，下通知单定义条件时用到
		{
				FieldItem item=new FieldItem();
				item.setItemid("B0110_1");
				item.setItemdesc("单位名称");
				item.setCodesetid("UN");
				item.setFieldsetid("A01");
				item.setItemtype("A");
				item.setItemlength(30);
				item.setUseflag("1");
				item.setNChgstate(1);
			item.setVarible(0);
				conefieldlist.add(item);
		}
		else if(this.infor_type==2)
		{
			FieldItem item=new FieldItem();
			item.setItemdesc("单位名称");
			item.setCodesetid("UN");
			item.setItemid("B0110");
			item.setFieldsetid("B01");
			item.setItemtype("A");
			item.setItemlength(30);
			item.setUseflag("1");
			conefieldlist.add(item);
		}
	}


	/**
	 * 解析计算公式
	 * @param exp
	 * @param fieldlist
* @return
	 */
	private String expRuleTerm(String exp,ArrayList fieldlist)
	{
		//sunx
		YksjParser yp=null;
		String FSQL="";
		try
		{
			int infoGroup=YksjParser.forPerson;
			if(this.infor_type==2) {
                infoGroup=YksjParser.forUnit;
            } else if(this.infor_type==3) {
                infoGroup=YksjParser.forPosition;
            }
			yp = new YksjParser( this.userview ,fieldlist,
						YksjParser.forNormal, YksjParser.LOGIC,infoGroup, "Ht", "");
			yp.run_where(exp);
			FSQL=yp.getSQL();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return FSQL;
	}
	/**
	 * 取得更新字符串
	 * @param srcTab 源表
	 * @param desTab 目标表
	 * @param fieldlist 需要更新字段列表 ,实际
	 * @return
	 */
	private String getChangeUpdateSQL(String srcTab,String desTab,ArrayList fieldlist,boolean bDataSync)
	{
		StringBuffer strupdate_s=new StringBuffer();
		StringBuffer strupdate=new StringBuffer();
		StringBuffer strupdate_d=new StringBuffer();
		int db_type=Sql_switcher.searchDbServer();//数据库类型
boolean isDameng=Sql_switcher.searchDbServerFlag()==Constant.DAMENG;
		/***/
int n=0;
for(int i=0;i<fieldlist.size();i++)
		{

			String field_name=(String)fieldlist.get(i);
			if((this.infor_type==2||this.infor_type==3)&&("codesetid".equalsIgnoreCase(field_name)|| "codeitemdesc".equalsIgnoreCase(field_name)|| "corcode".equalsIgnoreCase(field_name)|| "parentid".equalsIgnoreCase(field_name)|| "start_date".equalsIgnoreCase(field_name))) {
                continue;
            }

		//	if(field_name.indexOf("t_")!=-1)
			if(field_name.length()>2&& "t_".equalsIgnoreCase(field_name.trim().substring(0,2))) {
continue;
}
switch(db_type)
{
case 2://ORACLE
			case 3://DB2
				if(n!=0)
				{
					strupdate_s.append(",");
        strupdate_d.append(",");
    }

    //xus 20/5/15 【60479】VFS+UTF- 8+达梦：组织机构/岗位管理/岗位合并，提交提示SELECT INTO中包含多行数据
				if(isDameng) {
					String itemid = fieldlist.get(i)+"_2";
					FieldItem item = DataDictionary.getFieldItem((String)fieldlist.get(i));
					if("M".equalsIgnoreCase(item.getItemtype())){
						strupdate_s.append(Sql_switcher.sqlToChar(srcTab+"."+itemid));
						strupdate_s.append(" as ");
						strupdate_s.append(itemid);
					}else {
						strupdate_s.append(srcTab);
						strupdate_s.append(".");
						strupdate_s.append(fieldlist.get(i));
						strupdate_s.append("_2");
					}
				}else {
					strupdate_s.append(srcTab);
					strupdate_s.append(".");
					strupdate_s.append(fieldlist.get(i));
					strupdate_s.append("_2");
				}
				strupdate_d.append(desTab);
				strupdate_d.append(".");
				strupdate_d.append(fieldlist.get(i));
					break;
			case 1: //MSSQL SERVER
				if(n!=0) {
                    strupdate.append(",");
                }
				strupdate.append(desTab);
				strupdate.append(".");
				strupdate.append(fieldlist.get(i));
				strupdate.append("=");
				strupdate.append(srcTab);
				strupdate.append(".");
				strupdate.append(fieldlist.get(i));
				strupdate.append("_2");
					break;
			}
			n++;
		}//for i loop end.

		if(db_type==2||db_type==3)
		{
		   if(bDataSync)
		   {
				strupdate.append("(");
				strupdate.append(strupdate_d.toString());
				if(strupdate_d.length()>0) {
                    strupdate.append(",");
                }
				strupdate.append("modtime,modusername)=(select ");
				strupdate.append(strupdate_s.toString());
				if(db_type==2)
                    {
                        if(strupdate_d.length()>0) {
                        strupdate.append(",");
                    }
					strupdate.append(Sql_switcher.sqlNow()+",'");
				//	strupdate.append(",getdate(),'");
				}
				else
				{
					if(strupdate_d.length()>0) {
                        strupdate.append(",");
                    }
					strupdate.append("Current Timestamp,'");
				}
				strupdate.append(this.userview.getUserName());
				strupdate.append("'");
		   }
		   else
		   {
			strupdate.append("(");
			strupdate.append(strupdate_d.toString());
			strupdate.append(")=(select ");
			strupdate.append(strupdate_s.toString());
		   }
		}
		else
		{
			if(strupdate.length()>0) {
                strupdate.append(",");
            }
			strupdate.append("modtime="+Sql_switcher.sqlNow()+",modusername='"+this.userview.getUserName()+"'");
		}

		return strupdate.toString();
	}



	/**
	 * 分析业务模板中的人员来自消息库，如果来自消息库，且对应子集的更新字段，在消息
	 * 中没有更新操作，则按用户定义的进行子集记录操作，如果有更新指标，则子集记录进
	 * 行更新操作
	 * @param fieldlist
	 * @param a0100
	 * @param state =0正常操作 =1来源于消息模板
	 * @return
	 */
	private boolean isFromMessage(ArrayList fieldlist,String a0100,String basepre,int state)
	{
		boolean bflag=false;
		String chglast="";
		if(state==0)
		{
			return bflag;
		}
		else
		{
			StringBuffer strsql=new StringBuffer();
			ArrayList paralist=new ArrayList();
			if(this.infor_type==1)
			{
				strsql.append("select a0100,changelast from tmessage where db_type=?");
				strsql.append(" and a0100=? and noticetempid=?");
				paralist.add(basepre);
				paralist.add(a0100);
			}
			else
			{
				strsql.append("select b0110,changelast from tmessage where  ");
				strsql.append(" b0110=? and noticetempid=?");
				paralist.add(a0100);
			}
			paralist.add(Integer.valueOf(tabid));
			ContentDAO dao=new ContentDAO(this.conn);

			try
			{
				RowSet rset=dao.search(strsql.toString(),paralist);
				if(rset.next()) {
                    chglast=Sql_switcher.readMemo(rset,"changelast");
                }
            //String[] updates=StringUtils.split(chglast,',');
				chglast=chglast.toUpperCase();
				for(int i=0;i<fieldlist.size();i++)
				{
					String fieldname=((String)fieldlist.get(i)).toUpperCase();
					if(chglast.indexOf(fieldname)!=-1)
    				{
						bflag=true;
						break;
					}
                }
                PubFunc.closeDbObj(rset);
			}
			catch(Exception ex)
    {
        ex.printStackTrace();
			}
		}
		return bflag;
	}
	/**
	 * 得到子集的字段列表
	 * @param setname
	 * @return
	 */
	private String getFieldString(String setname)
	{
		StringBuffer fields=new StringBuffer();
		ArrayList fieldlist=DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		try
		{
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem fielditem=(FieldItem)fieldlist.get(i);
				if(i!=0) {
                    fields.append(",");
                }
				fields.append(fielditem.getItemid());
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return fields.toString();
	}




	/**
	 * 对组织信息调入追加子集记录
	 * @param srcTab  源表
	 * @param setname 子集代码
	 * @param basepre 应用库前缀
	 * @param srcA0100 源表的人员编码
	 * @param desA0100 导入到目标表(档案表的)人员编码
	 * @param fieldlist 字段列表
	 * @param bDatasync 是否支持数据同步
	 *
	 */
	private void appendSubRecordBK(String srcTab,String setname,String srcKeyValue,String desKeyValue,ArrayList fieldlist,boolean bDatasync)throws GeneralException
	{
		String desTab=setname;
		StringBuffer strfield_s=new StringBuffer();
		StringBuffer strfield_d=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		String keyField="b0110";
		if(this.infor_type==3) {
            keyField="e01a1";
        }
		/**组装需要更新的字段*/
		for(int i=0;i<fieldlist.size();i++)
		{
			if(i!=0)
			{
				strfield_s.append(",");
				strfield_d.append(",");
			}
			strfield_s.append(fieldlist.get(i)+"_2");
			strfield_d.append(fieldlist.get(i));
}

		int i9999=1;
		StringBuffer strsql=new StringBuffer();
		ArrayList paralist=new ArrayList();
		try
		{
			strsql.append("insert into ");
			strsql.append(desTab);
			strsql.append(" ("+keyField+",i9999");
			if(bDatasync) {
                strsql.append(",createtime,createusername");
            }
			if(strfield_d.length()>0)
			{
				strsql.append(",");
				strsql.append(strfield_d.toString());
			}
			strsql.append(") select ?,?");

			paralist.add(desKeyValue);
			paralist.add(Integer.valueOf(i9999));

			if(bDatasync)
			{
				strsql.append(",");
		strsql.append(Sql_switcher.sqlNow());
				strsql.append(",?");
    paralist.add(this.userview.getUserName());
			}
			if(strfield_d.length()>0)
			{
				strsql.append(",");
				strsql.append(strfield_s.toString());
			}
			strsql.append(" from ");
			strsql.append(srcTab);
			if(this.infor_type==1){
				strsql.append(" where a0100=?");
			}else if(this.infor_type==2){
				strsql.append(" where b0110=?");

			}else if(this.infor_type==3){
				strsql.append(" where e01a1=?");

			}

			if(ins_id!=0)
			{
				strsql.append(" and ins_id="+ins_id);
			}
			paralist.add(srcKeyValue);
			dao.update(strsql.toString(),paralist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}





	/**
	 * 对人员调入追加子集记录
	 * @param srcTab  源表
	 * @param setname 子集代码
	 * @param basepre 应用库前缀
	 * @param srcA0100 源表的人员编码
	 * @param desA0100 导入到目标表(档案表的)人员编码
	 * @param fieldlist 字段列表
	 * @param bDatasync 是否支持数据同步
*
*/
private void appendSubRecord(String srcTab,String setname,String basepre,String srcA0100,String desA0100,ArrayList fieldlist,boolean bDatasync)throws GeneralException
{
String desTab=basepre+setname;
	StringBuffer strfield_s=new StringBuffer();
		StringBuffer strfield_d=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		/**组装需要更新的字段*/
		for(int i=0;i<fieldlist.size();i++)
		{
			if(i!=0)
			{
				strfield_s.append(",");
				strfield_d.append(",");
			}
			strfield_s.append(fieldlist.get(i)+"_2");
			strfield_d.append(fieldlist.get(i));
		}

		int i9999=1;
		BaseInfoBo infobo=new BaseInfoBo(this.conn,this.userview,this.infor_type);
		i9999=infobo.getMaxI9999(basepre,setname,desA0100);
		StringBuffer strsql=new StringBuffer();
		ArrayList paralist=new ArrayList();
		try
		{
			strsql.append("insert into ");
			strsql.append(desTab);
			strsql.append(" (a0100,i9999");
			if(bDatasync) {
                strsql.append(",createtime,createusername");
            }
			if(strfield_d.length()>0)
			{
				strsql.append(",");
				strsql.append(strfield_d.toString());
			}
			strsql.append(") select ?,?");

			paralist.add(desA0100);
			paralist.add(Integer.valueOf(i9999));

			if(bDatasync)
			{
				strsql.append(",");
				strsql.append(Sql_switcher.sqlNow());
				strsql.append(",?");
				paralist.add(this.userview.getUserName());
			}
			if(strfield_d.length()>0)
			{
				strsql.append(",");
				strsql.append(strfield_s.toString());
			}
			strsql.append(" from ");
			strsql.append(srcTab);
			strsql.append(" where a0100=?");
			if(ins_id!=0)
			{
				strsql.append(" and ins_id="+ins_id);
			}
			paralist.add(srcA0100);
			dao.update(strsql.toString(),paralist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 自动创建子集的下一条记录
	 * @param setname 子集代号
	 * @param basepre 库前缀
	 * @param a0100 人员编号
	 * @param i9999 子集记录序号
	 * @param bDatasync 数据同步
	 * @param refPreRec 1:引入上条记录 0:不引入
	 */
	private void AutoCreateNextRecord(String setname,String basepre,String a0100,int i9999,boolean bDatasync,int refPreRec)throws GeneralException
	{
		String srctab=basepre+setname;
		String alias=setname;
		String strfields=getFieldString(setname);
		StringBuffer strsql=new StringBuffer();
		ArrayList paralist=new ArrayList();
		String key_field="a0100";
		if(this.infor_type==2) {
            key_field="b0110";
        } else if(this.infor_type==3) {
            key_field="e01a1";
        }
		if(i9999==1)//此人无子集记录
		{
			strsql.append("insert into ");
			strsql.append(srctab);
			strsql.append(" ("+key_field+",i9999 ");
			if(bDatasync) {
                strsql.append(",createtime,createusername");
            }
			strsql.append(") values (");
			strsql.append("?,?");
			if(bDatasync)
			{
				strsql.append(",");
				strsql.append(Sql_switcher.sqlNow());
				strsql.append(",?");
			}
			strsql.append(")");
			paralist.add(a0100);
			paralist.add(Integer.valueOf(i9999));
			if(bDatasync) {
                paralist.add(this.userview.getUserName());
            }
		}
		else //子集中历史记录，则追加和上一条一样的记录
		{
			strsql.append("insert into ");
			strsql.append(srctab);
			strsql.append("("+key_field+",i9999");

			if(strfields.length()!=0&&refPreRec==1)
			{
				strsql.append(",");
				strsql.append(strfields);
			}
			if(bDatasync) {
                strsql.append(",createtime,createusername");
            }
			strsql.append(") select "+key_field+",");
			strsql.append(i9999);
			if(strfields.length()!=0&&refPreRec==1)
			{
				strsql.append(",");
				strsql.append(strfields);
			}
			if(bDatasync)
			{
				strsql.append(",");
				strsql.append(Sql_switcher.sqlNow());
				strsql.append(",'");
				strsql.append(this.userview.getUserName());
				strsql.append("'");
			}
			strsql.append(" from ");
			strsql.append(srctab);
			strsql.append(" ");
			strsql.append(alias);
			strsql.append(" where I9999=");
    strsql.append(i9999-1);
			strsql.append(" and "+key_field+"='");
			strsql.append(a0100);
			strsql.append("'");
		}
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			dao.update(strsql.toString(),paralist);
			/**关联更新*/
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}


	/**
	 * 根据业务模板中的数据生成人员主集记录 ,人员调入业务才执行此操作
	 * @param srcPre 应用库前缀
	 * @param setid  主集代号
	 * @param srcTab 数据源表
	 * @param a0100  人员编号
	 * @param fieldlist 更新字符串列表
	 * @return 返回的为人员编码
	 */
	private String expDataIntoArchiveEmpMainSet(String desPre,String setid,String srcTab,String a0100,ArrayList fieldlist)throws GeneralException
{
String desTab=desPre+setid;
		ArrayList paralist=new ArrayList();
		BaseInfoBo infobo=new BaseInfoBo(this.conn,this.userview,1);
		String[] strA0100A0000=infobo.getMaxA0100A0000(desPre);
		StringBuffer strIns_s=new StringBuffer();
		StringBuffer strIns_d=new StringBuffer();
		StringBuffer strsql=new StringBuffer();
		for(int i=0;i<fieldlist.size();i++)
		{
			if(i!=0)
			{
				strIns_s.append(",");
            strIns_d.append(",");
			}
			strIns_s.append(fieldlist.get(i));
    strIns_s.append("_2");
    strIns_d.append(fieldlist.get(i));
		}

		strsql.append("insert into ");
		strsql.append(desTab);
		strsql.append(" (a0100,a0000");
		strsql.append(",createusername,createtime");  //dengcan 2009-5-13
		if(strIns_d.length()>0)
		{
			strsql.append(",");
			strsql.append(strIns_d);
		}
		strsql.append(") select ?,?");
		strsql.append(",'"+this.userview.getUserName()+"',"+Sql_switcher.sqlNow()); //dengcan 2009-5-13
		if(strIns_s.length()>0)
		{
			strsql.append(",");
			strsql.append(strIns_s);
		}
		strsql.append(" from ");
		strsql.append(srcTab);
		strsql.append(" where a0100=? ");
paralist.add(strA0100A0000[0]);
		paralist.add(strA0100A0000[1]);
		paralist.add(a0100);
		if(ins_id!=0)
		{
			strsql.append(" and ins_id=?");
			paralist.add(Integer.valueOf(ins_id));
		}
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			dao.update(strsql.toString(),paralist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return strA0100A0000[0];
	}

	/**
	 * @param  strbase 源应用库,如果目标库未定义的话，则按源应用库进行数据操作
	 * @param  a0100   数据处理对象编号
	 * @param  srctab  源表
	 * @param  desa0100 目标人员编号
	 * @throws GeneralException
	 */
	private void subSetChangeSubmit(String strbase,String srctab,String a0100,String desa0100)throws GeneralException
	{
        Object[] key = this.submap.keySet().toArray();
    	RecordVo vo=new RecordVo(srctab);
    	try
    	{
    		ContentDAO dao=new ContentDAO(this.conn);
    		TSubSetDomain setdomain=null;
    		ArrayList list=new ArrayList();
    		StringBuffer buf=new StringBuffer();
    		for(int i=0;i<key.length;i++)
	        {
	        	String setid=(String)key[i];

	        	String temp="";
	        	int updatetype=1;
	        	for(int j=0;j<this.subUpdateList.size();j++)
				{
					TSubsetCtrl subctrl0=(TSubsetCtrl)subUpdateList.get(j);
					temp=subctrl0.getSetcode();
        String submenu=subctrl0.getSubMenu();
					if(setid.equalsIgnoreCase(temp)&&submenu!=null&& "true".equalsIgnoreCase(submenu))
					{
						updatetype=subctrl0.getUpdatetype();
						break;
					}
				}
	        	if(updatetype==0) {
                    continue;
                }


	        	String state=this.userview.analyseTablePriv(setid.toUpperCase());
	        	if((state==null|| "0".equalsIgnoreCase(state))&& "0".equals(this.UnrestrictedMenuPriv)) //判断子集是否有写权限
                {
continue;
}

FieldItem item=(FieldItem)this.submap.get(setid);
	        	/**默认值处理*/
	        	String xml_param=item.getFormula();
setdomain=new TSubSetDomain(xml_param);
setdomain.setCon(this.conn);
	    		setdomain.setUnrestrictedMenuPriv(this.UnrestrictedMenuPriv);
	    		setdomain.setUnrestrictedMenuPriv_Input(this.UnrestrictedMenuPriv_Input);
	    		setdomain.setId_gen_manual(this.id_gen_manual);
	    		setdomain.setUserview(this.userview);  //控制指标权限
	    		setdomain.setInfor_type(this.infor_type);
	        	String field_name=item.getItemid()+"_2";
	        	if(this.infor_type==1)
	        	{
		        	if(this.operationtype==0) {
                        vo.setString("basepre", this.dest_base);
                    } else {
                        vo.setString("basepre", "");  //
                    }
		        	vo.setString("a0100", a0100);
	        	}
	        	else if(this.infor_type==2) {
                    vo.setString("b0110",a0100);
                } else if(this.infor_type==3) {
                    vo.setString("e01a1",a0100);
                }
	        	if(this.ins_id!=0) {
                    vo.setInt("ins_id", this.ins_id);
                }
	        	try{
	        	    vo=dao.findByPrimaryKey(vo);
	        	}
	        	catch(Exception e){
	        	    //未找到记录 bug1928 20140606 wangrd
	        	    if(this.operationtype==0){
	        	        dao.update("update "+srctab+" set basepre='"+this.dest_base
	        	                      +"' where a0100 ='"+a0100+"'");
	        	        vo=dao.findByPrimaryKey(vo);
	        	        //(不走审批直接提交)特殊情况 即外部链接直接调用时,是调入模板走的自助业务申请,需要将目标库nbase更新到虚拟的人员userview中 add hej 20170623
	        	        if(isBEmploy()&&"1".equals(this.userview.getHm().get("fillInfo"))) {
                            this.userview.setDbname(this.dest_base);
                        }
	        	    }
	        	    else {
	        	        throw GeneralExceptionHandler.Handle(e);
	        	    }
	        	}

        	if(vo!=null)
	        	{
	        		String xml=vo.getString(field_name.toLowerCase());
	        		if(xml==null||xml.length()==0) {
                        continue;
                    }

	        		ArrayList reclist=setdomain.getChangeRecList(xml, strbase+setid, desa0100);
	        		HashMap filemap=setdomain.getFilemap();
	        		/**先更新*/
	        		list.clear();
	        		for(int j=0;j<reclist.size();j++)
	        		{
	        			RecordVo recvo=(RecordVo)reclist.get(j);
	        			int i9999=recvo.getInt("i9999");
	        			if(i9999==-1) {
                            continue;
                        }
	        			list.add(recvo);
	        		}//for j loop end.
        			dao.updateValueObject(list);
        			/**子集记录新增*/
        			int[] i9999arr=getSubSetI9999List(reclist);
        			int[] ins_flag=new int[1];
	        		for(int j=0;j<reclist.size();j++)
	        		{
	        			RecordVo recvo=(RecordVo)reclist.get(j);
	        			int i9999=recvo.getInt("i9999");
	        			if(i9999!=-1) {
                            continue;
                        }

	        			if(recvo.getValues().size()<=2) {
                                        continue;
             }

                        //子集附件
                        String value="";
                        if(filemap.get(j)!=null){
                           value=(String) filemap.get(j);
                        }
	        			i9999=getNextI9999(i9999arr,j,ins_flag);
	        			if(ins_flag[0]==0)
	        			{
	        				buf.append("update ");
	        				buf.append(strbase+setid);
	        				buf.append(" set i9999=i9999+1");
	        				if(this.infor_type==1) {
                                buf.append(" where a0100='");
                            } else if(this.infor_type==2) {
                                buf.append(" where b0110='");
                            } else if(this.infor_type==3) {
                                buf.append(" where e01a1='");
                            }
	        				buf.append(desa0100);
	        				buf.append("'");
	        				buf.append(" and i9999>=");
	        				buf.append(i9999);
	        				dao.update(buf.toString());

		        			recvo.setInt("i9999", i9999);
		        			dao.addValueObject(recvo);

	        			}
	        			else
	        			{
	        				String stri9999="";
	        				if(this.infor_type==1) {
                                stri9999=DbNameBo.insertSubSetA0100(strbase+setid, desa0100, conn,this.userview.getUserName());
                            } else {
                                stri9999=DbNameBo.insertSubSet(strbase+setid, desa0100, conn,this.userview.getUserName(),this.infor_type);
                            }

	        				recvo.setInt("i9999", Integer.parseInt(stri9999));
		        			dao.updateValueObject(recvo);
		        			i9999= Integer.parseInt(stri9999);
	        			}
	        			//处理i9999=-1的情况，将附件上传，
	        			if(!"".equals(value)){
	        			    MultiMediaBo multimediabo=new MultiMediaBo(conn, userview, "A",strbase,setid, desa0100, i9999);
	        			    ArrayList lists=new ArrayList();
	        			    if(value.indexOf(",")!=-1){
	        			        String[]arrValue=value.split(",");
	        			        for(int m=0;m<arrValue.length;m++){
	        			            lists.add(arrValue[m]);
	        			        }
	        			    }else{
	        			        lists.add(value);
	        			    }
	        			    //根据子集弹出窗体添加的附件，保存到hr_multimedia_file表中 liuzy 20151102
	        			    setdomain.saveMultimediaFile(lists, multimediabo);
	        			}
	        		}
	        	}//for j loop end.
	        }// for i loop end.
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
    	}
	}


	/**
	 * 取得当前表的字段串
	 * @param tablename
	 * @return   a0111,a0202,a0304
	 */
	private String getFieldColumns(String tablename)
	{
		StringBuffer buf=new StringBuffer();
		ArrayList fieldlist=DataDictionary.getFieldList(tablename,Constant.USED_FIELD_SET);
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem item=(FieldItem)fieldlist.get(i);
			buf.append(item.getItemid());
			buf.append(",");
		}
		/**去掉","*/
		if(buf.length()>0) {
            buf.setLength(buf.length()-1);
        }
		return buf.toString();
	}


	/**
	 * @param  strbase 源应用库,如果目标库未定义的话，则按源应用库进行数据操作
	 * @param  a0100   数据处理对象编号
	 * @param  srctab  源表
	 * @throws GeneralException
	 */
	private void subTrsSetChangeSubmit(HashMap subhm,String strbase,String srctab,String a0100,String desA0100)throws GeneralException
	{
        Object[] key = this.submap.keySet().toArray();
    	RecordVo vo=new RecordVo(srctab);
    	try
    	{
ContentDAO dao=new ContentDAO(this.conn);
//TSubSetDomain setdomain=new TSubSetDomain("");
ArrayList list=new ArrayList();
StringBuffer buf=new StringBuffer();
for(int i=0;i<key.length;i++)
{
String setid=(String)key[i];
TSubsetCtrl subctrl=(TSubsetCtrl)subhm.get(setid);

String state=this.userview.analyseTablePriv(setid.toUpperCase());
if((state==null||!"2".equalsIgnoreCase(state))&& "0".equals(this.UnrestrictedMenuPriv)) //判断子集是否有写权限
                {
                    continue;
                }

if(subctrl==null)
{
for(int j=0;j<subUpdateList.size();j++)
{
TSubsetCtrl subctrl0=(TSubsetCtrl)subUpdateList.get(j);
String temp=subctrl0.getSetcode();
if(setid.equalsIgnoreCase(temp))
{
subctrl=subctrl0;
continue;
}
}
}
if(subctrl!=null&&subctrl.getInnerupdatetype()==0) {
continue;
}
        	FieldItem item=(FieldItem)this.submap.get(setid);
	        	String field_name=item.getItemid();
	    		TSubSetDomain setdomain=new TSubSetDomain(item.getFormula());
	    		setdomain.setCon(this.conn);
	    		setdomain.setUnrestrictedMenuPriv(this.UnrestrictedMenuPriv);
	    		setdomain.setUnrestrictedMenuPriv_Input(this.UnrestrictedMenuPriv_Input);
	    		setdomain.setId_gen_manual(this.id_gen_manual);
	    		setdomain.setUserview(this.userview);  //控制指标权限
	    		setdomain.setInfor_type(this.infor_type);
	        	if(field_name.indexOf("_2")==-1) {
                    field_name=field_name+"_2";
                }

	        	if(this.infor_type==1)
	        	{
	        		vo.setString("a0100", a0100);
	        		vo.setString("basepre", strbase);
	        	}
	        	else if(this.infor_type==2) {
                    vo.setString("b0110",a0100);
              } else if(this.infor_type==3) {
                    vo.setString("e01a1",a0100);
                }

	        	if(this.ins_id!=0) {
                    vo.setInt("ins_id", this.ins_id);
                }
	        	vo=dao.findByPrimaryKey(vo);
	        	if(vo!=null)
	        	{
	        		String xml=vo.getString(field_name.toLowerCase());
	        		if(xml==null||xml.length()==0) {
                        continue;
                    }


	        		//////////////////////////////////////////////////////////
	        		String fieldstrs=getFieldColumns(setid);
	        		StringBuffer buf0=new StringBuffer("");
	        		buf0.append("insert into ");
	        		buf0.append(strbase+setid);
	        		buf0.append("(A0100,I9999,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
					if(fieldstrs.length()>0)
					{
						buf0.append(",");
						buf0.append(fieldstrs);
					}
					buf0.append(") select A0100,I9999*(-1)-10000,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
					if(fieldstrs.length()>0)
					{
						buf0.append(",");
						buf0.append(fieldstrs);
					}
					buf0.append(" from ");
					buf0.append(strbase+setid);
					buf0.append(" where A0100='" + a0100 + "'");
					dao.update(buf0.toString());
	        		////////////////////////////////////////////////////////////


	        		ArrayList reclist=setdomain.getChangeRecList(xml, strbase+setid, a0100);
	        		/**先更新*/
	        		list.clear();
	        		for(int j=0;j<reclist.size();j++)
	        		{
	        			RecordVo recvo=(RecordVo)reclist.get(j);
	        			int i9999=recvo.getInt("i9999");
	        			if(i9999==-1) {
                            continue;
                        }

	        			recvo.setString("modusername",this.userview.getUserName());
	        			recvo.setDate("modtime",new Date());

	        			list.add(recvo);
	        		}//for j loop end.
        			dao.updateValueObject(list);
        			/**子集记录新增*/
        			int[] i9999arr=getSubSetI9999List(reclist);
        			int[] ins_flag=new int[1];
	        		for(int j=0;j<reclist.size();j++)
	        		{
	        			RecordVo recvo=(RecordVo)reclist.get(j);
	        			int i9999=recvo.getInt("i9999");
	        			if(i9999!=-1) {
                            continue;
                        }
	        			if(recvo.getValues().size()<=2) {
                            continue;
                        }
	        			buf.setLength(0);
	        			i9999=getNextI9999(i9999arr,j,ins_flag);
	        			if(ins_flag[0]==0)
	        			{
	        				buf.append("update ");
	        				buf.append(strbase+setid);
	        				buf.append(" set i9999=i9999+1");
	        				if(this.infor_type==1) {
                                buf.append(" where a0100='");
                            } else if(this.infor_type==2) {
                                buf.append(" where b0110='");
                            } else if(this.infor_type==3) {
                                buf.append(" where e01a1='");
                            }
	        				buf.append(a0100);
	        				buf.append("'");
	        				buf.append(" and i9999>=");
	        				buf.append(i9999);
	        				dao.update(buf.toString());
	        			    recvo.setInt("i9999", i9999);
		        			dao.addValueObject(recvo);

	        			}
	        			else
	        			{
	        				String stri9999="";
	        				if(this.infor_type==1) {
                                stri9999=DbNameBo.insertSubSetA0100(strbase+setid, a0100, conn,this.userview.getUserName());
                            } else {
                                stri9999=DbNameBo.insertSubSet(strbase+setid, a0100, conn,this.userview.getUserName(),this.infor_type);
                            }

	        				recvo.setInt("i9999", Integer.parseInt(stri9999));
		        			dao.updateValueObject(recvo);

	        			}
	        		}

	        		dao.update("delete from "+this.dest_base+setid+" where  A0100='" + desA0100 + "'");
	        		buf0.setLength(0);
		        	buf0.append("insert into ");
		            buf0.append(this.dest_base+setid);
		        	buf0.append("(A0100,I9999,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
					if(fieldstrs.length()>0)
					{
							buf0.append(",");
							buf0.append(fieldstrs);
					}
					buf0.append(") select '"+desA0100+"',I9999,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
					if(fieldstrs.length()>0)
					{
							buf0.append(",");
							buf0.append(fieldstrs);
					}
					buf0.append(" from ");
					buf0.append(strbase+setid);
					buf0.append(" where A0100='" + a0100 + "' and i9999>-5000");
					dao.update(buf0.toString());

	        	    dao.update("delete from "+strbase+setid+" where  A0100='" + a0100 + "' and i9999>-5000");
	        	    buf0.setLength(0);
	        	    buf0.append("insert into ");
	        	    buf0.append(strbase+setid);
	        	    buf0.append("(A0100,I9999,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
					if(fieldstrs.length()>0)
					{
						buf0.append(",");
						buf0.append(fieldstrs);
					}
					buf0.append(") select A0100,I9999*(-1)-10000,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
					if(fieldstrs.length()>0)
					{
						buf0.append(",");
						buf0.append(fieldstrs);
					}
					buf0.append(" from ");
					buf0.append(strbase+setid);
					buf0.append(" where A0100='" + a0100 + "' and i9999<-5000");
					dao.update(buf0.toString());
					dao.update("delete from "+strbase+setid+" where  A0100='" + a0100 + "' and i9999<=-5000");


	        	}//for j loop end.
	        }// for i loop end.
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
    	}
	}



	/**
	 * @param  strbase 源应用库,如果目标库未定义的话，则按源应用库进行数据操作
	 * @param  a0100   数据处理对象编号
	 * @param  srctab  源表
	 * @throws GeneralException
	 */
	private void subSetChangeSubmit(String strbase,String srctab,String a0100)throws GeneralException
	{
        Object[] key = this.submap.keySet().toArray();
    	RecordVo vo=new RecordVo(srctab);
    	try
    	{
    		ContentDAO dao=new ContentDAO(this.conn);
    		//TSubSetDomain setdomain=new TSubSetDomain("");
    		ArrayList list=new ArrayList();
    		StringBuffer buf=new StringBuffer();
    		for(int i=0;i<key.length;i++)
	        {
	        	String setid=(String)key[i];
	        	FieldItem item=(FieldItem)this.submap.get(setid);
	        	String domain_id="";
	        	if(setid.contains("_")){///子集存在多个变化后的情况 wangrd 20160826
	        		String tmp =setid;
	        		int k = setid.indexOf("_");
	        		setid=tmp.substring(0,k);
	        		domain_id= tmp.substring(k+1,tmp.length());
	        	}

	        	String temp="";
	        	int updatetype=1;
	        	for(int j=0;j<this.subUpdateList.size();j++)
				{
					TSubsetCtrl subctrl0=(TSubsetCtrl)subUpdateList.get(j);
					temp=subctrl0.getSetcode();
					String submenu=subctrl0.getSubMenu();
					if(setid.equalsIgnoreCase(temp)&&submenu!=null&& "true".equalsIgnoreCase(submenu))
					{
						updatetype=subctrl0.getUpdatetype();
						break;
					}
				}
	        	if(updatetype==0) {
                    continue;
                }


	        	String state=this.userview.analyseTablePriv(setid.toUpperCase());
	        	if((state==null|| "0".equalsIgnoreCase(state))&& "0".equals(this.UnrestrictedMenuPriv)) //判断子集是否有写权限
                {
                    continue;
                }


	        	String field_name=item.getItemid();
	    		TSubSetDomain setdomain=new TSubSetDomain(item.getFormula());
	    		setdomain.setCon(this.conn);
	    		setdomain.setUnrestrictedMenuPriv(this.UnrestrictedMenuPriv);
	    		setdomain.setUnrestrictedMenuPriv_Input(this.UnrestrictedMenuPriv_Input);
	    		setdomain.setId_gen_manual(this.id_gen_manual);
	    		setdomain.setUserview(this.userview);  //控制指标权限
	    		setdomain.setInfor_type(this.infor_type);
	    		if (domain_id.length()>0){
	    			if(field_name.indexOf("_2")==-1) {
                        field_name=field_name+"_"+domain_id+"_2";
                    } else {
                        field_name=field_name+"_"+domain_id;
                    }
	    		}
	    		else {
                if(field_name.indexOf("_2")==-1) {
                        field_name=field_name+"_2";
                    }
	    		}

	        	if(this.infor_type==1)
                {
                    vo.setString("a0100", a0100);
vo.setString("basepre", strbase);
	        	}
	        	else if(this.infor_type==2) {
                    vo.setString("b0110",a0100);
                } else if(this.infor_type==3) {
                    vo.setString("e01a1",a0100);
                }

	        	if(this.ins_id!=0) {
                    vo.setInt("ins_id", this.ins_id);
                }
	        	vo=dao.findByPrimaryKey(vo);
	        	if(vo!=null)
	        	{
	        		String xml=vo.getString(field_name.toLowerCase());
	        		if(xml==null||xml.length()==0) {
                        continue;
                    }
	        		ArrayList reclist=setdomain.getChangeRecList(xml, strbase+setid, a0100);
	        		HashMap filemap=setdomain.getFilemap();
	        		/**先更新*/
	        		list.clear();
	        		for(int j=0;j<reclist.size();j++)
	        		{
	        			RecordVo recvo=(RecordVo)reclist.get(j);
	        			int i9999=recvo.getInt("i9999");
	        			if(i9999==-1) {
                            continue;
                        }

	        			recvo.setString("modusername",this.userview.getUserName());
	        			recvo.setDate("modtime",new Date());

	        			list.add(recvo);
	        		}//for j loop end.
        			dao.updateValueObject(list);
        			/**子集记录新增*/
        			int[] i9999arr=getSubSetI9999List(reclist);
        			int[] ins_flag=new int[1];
	        		for(int j=0;j<reclist.size();j++)
	        		{
	        			RecordVo recvo=(RecordVo)reclist.get(j);
	        			int i9999=recvo.getInt("i9999");
	        			if(i9999!=-1) {
                            continue;
                        }
	        			if(recvo.getValues().size()<=2) {
                            continue;
                        }
	        			buf.setLength(0);

	        			//处理i9999=-1的情况，先将附件对应存储在临时表中的值得到 liuzy 20151031
	        			String value="";
	        			if(filemap.get(j)!=null){
	        			   value=(String) filemap.get(j);
	        			}

	        			i9999=getNextI9999(i9999arr,j,ins_flag);//取得子集下一记录非负值序号
	        			int i9999s=i9999;
	        			if(ins_flag[0]==0)
	        			{
	        				buf.append("update ");
	        				buf.append(strbase+setid);
	        				buf.append(" set i9999=i9999+1");
	        				if(this.infor_type==1) {
                                buf.append(" where a0100='");
                            } else if(this.infor_type==2) {
                                buf.append(" where b0110='");
                            } else if(this.infor_type==3) {
                                buf.append(" where e01a1='");
                            }
	        				buf.append(a0100);
	        				buf.append("'");
	        				buf.append(" and i9999>=");
	        				buf.append(i9999);
	        				dao.update(buf.toString());
	        			    recvo.setInt("i9999", i9999);
		        			dao.addValueObject(recvo);

	        			}
	        			else
	        			{
	        				String stri9999="";
	        				if(this.infor_type==1) {
                                stri9999=DbNameBo.insertSubSetA0100(strbase+setid, a0100, conn,this.userview.getUserName());
                            } else {
                                stri9999=DbNameBo.insertSubSet(strbase+setid, a0100, conn,this.userview.getUserName(),this.infor_type);
                            }
	        				i9999s=Integer.parseInt(stri9999);
	        				recvo.setInt("i9999", Integer.parseInt(stri9999));
		        			dao.updateValueObject(recvo);

	        			}

	        			//处理i9999=-1的情况，将附件上传，提交入库 liuzy 20151031
	        			if(!"".equals(value)){
	        				MultiMediaBo multimediabo=new MultiMediaBo(conn, userview, "A",strbase,setid, a0100, i9999s);
	        				ArrayList lists=new ArrayList();
                                        if(value.indexOf(",")!=-1){
	        					String[]arrValue=value.split(",");
								for(int m=0;m<arrValue.length;m++){
									lists.add(arrValue[m]);
								}
	        				}else{
	        					lists.add(value);
                                        }
                                		//根据子集弹出窗体添加的附件，保存到hr_multimedia_file表中 liuzy 20151102
	        				setdomain.saveMultimediaFile(lists, multimediabo);
	        			}
	        		}
	        	}//for j loop end.
	        }// for i loop end.
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
    	}
	}
	/**
	 * 求当前插入子集记录的I9999列表
	 * @param reclist
	 * @return
	 */
	private int[] getSubSetI9999List(ArrayList reclist)
	{
		int[] i9999arr=new int[reclist.size()];
		for(int i=0;i<reclist.size();i++)
		{
			RecordVo recvo=(RecordVo)reclist.get(i);
			int i9999=recvo.getInt("i9999");
			i9999arr[i]=i9999;
		}
		return i9999arr;
	}
	/**
	 * 取得子集下一记录非负值序号
	 * @param list
	 * @param index
	 * @return
	 */
	private int getNextI9999(int[] i9999arr,int index,int[] iIns)
	{
		int i9999=1;
		int oldMini9999=0;
		boolean bflag=false;
		int i=index;
		for(i=index;i<i9999arr.length;i++)
		{
			int tmp=i9999arr[i];
			if(tmp==-1) {
                continue;
            } else
			{
				if(oldMini9999==0){
					oldMini9999=tmp;
				}
				else {
					if (tmp<oldMini9999) {
                        oldMini9999=tmp;
                    }
				}
				bflag=true;
			}
		}
		if(oldMini9999!=0){
			i9999=oldMini9999;
		}
		if(bflag)
		{
			iIns[0]=0;
			for(int j=index;j<i9999arr.length;j++)
			{
				if(i9999arr[j]==-1) {
                    continue;
                }
				i9999arr[j]=i9999arr[j]+1;
			}
		}
		else
		{
			iIns[0]=1;	//表示为最大值+1,非插入子集记录
		}
		return i9999;
	}
	/**
	 * 保存上传照片，人员调入操作或者其他业务操作
	 * @param a0100
	 * @param basepre
	 * @param desta0100 目标表的人员编号
	 * @param srctablename  源表的表名
	 * @param desttablename 目标表的表名
	 * @throws GeneralException
	 */
	private void submitPicture(String a0100,String basepre,String srctablename,String desta0100,String desttablename)throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.conn);
		RecordVo s_vo=new RecordVo(srctablename);
		if(!s_vo.hasAttribute("photo")) {
            return;
        }
		StringBuffer buf=new StringBuffer();
		buf.append("select photo,ext,fileid from ");
		buf.append(srctablename);
		buf.append(" where a0100=? and upper(basepre)=?");
		if(this.ins_id!=0)
		{
			buf.append(" and ins_id="+this.ins_id);
		}
ArrayList paralist=new ArrayList();
		paralist.add(a0100);
		if(Sql_switcher.searchDbServer()==Constant.MSSQL) {
            paralist.add(basepre.toUpperCase());
        } else {
            paralist.add(basepre.toUpperCase());
        }

		try
		{
			RowSet rset=dao.search(buf.toString(),paralist);
			if(rset.next())
			{
				String ext=rset.getString("ext");
				String fileid=rset.getString("fileid");
				if(!(ext==null|| "".equalsIgnoreCase(ext)))
				{
					if(!(this.dest_base==null||"".equals(this.dest_base))||this.operationtype==10){//其他业务类型或者具有目标库的模板  先删除照片 在提交照片
						//删除库表中的记录 删除前先查找A00中对应的fileid 删除此记录后 再删除子集记录
						List<LazyDynaBean> a00List=dao.searchDynaList("select fileid from "+desttablename+" where a0100='"+desta0100+"' and flag='P'");
						if(a00List!=null&&a00List.size()>0) {
							String fileid_old = (String)a00List.get(0).get("fileid");
							if(!fileid_old.equalsIgnoreCase(fileid)) {
								VfsService.deleteFile(this.userview.getUserName(), fileid_old);
							}
						}
						buf.setLength(0);
						buf.append("delete from "+desttablename+" where a0100='"+desta0100+"' and flag='P'");
						dao.update(buf.toString());
						//将照片存储到文件夹下
						PhotoImgBo photoImgBo = new PhotoImgBo(this.conn);
						//String rootdir = photoImgBo.getPhotoRootDir();
				        //String relativedir = photoImgBo.getPhotoRelativeDir(basepre, desta0100);
				       // String saveFile = rootdir + relativedir;
					    //判断存放照片的文件夹是否存在，不存在则创建
					    /*File tempDir = new File(saveFile);
					    boolean isMk = true;
					    if (!tempDir.exists()) {
					    	isMk = tempDir.mkdirs();
					    }*/
					    /*if(isMk){
					    	//删除文件夹下的照片文件
						    photoImgBo.delFileByName(saveFile,"photo");
						    //将提交的照片文件保存到文件夹下
						    InputStream in = rset.getBinaryStream("photo");
						    String filePath = saveFile+"photo" + ext;
						    // 保存文件
							File file = new File(filePath);
							OutputStream output = new FileOutputStream(file);
							byte[] bt = new byte[1024];
							int read = 0;
							while ((read = in.read(bt)) != -1) {
								output.write(bt, 0, read);
							}
					    }*/
					}
					String i9999=DbNameBo.insertSubSetA0100(desttablename, desta0100, this.conn);
					
					VfsFileEntity enty = VfsService.getFileEntity(fileid);
					String file_name=enty.getName();
					/*RecordVo vo=new RecordVo(desttablename);
					vo.setString("a0100", desta0100);
					vo.setInt("i9999", Integer.parseInt(i9999));
					//vo.setObject("ole", null);
					vo.setObject("title", file_name.substring(0, file_name.lastIndexOf(".")));
					vo.setString("ext", ext);
					vo.setString("flag", "P");
					vo.setString("fileid", fileid);
					*//**syl 20191204 华远地产上传照片没有创建时间和更新时间**//*
					vo.setDate("createtime", new Date());
					vo.setDate("modtime", new Date());
					vo.setString("createusername", this.userview.getUserName());
					vo.setString("modusername", this.userview.getUserName());
					dao.updateValueObject(vo);*/
					if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
					{
						RecordVo vo=new RecordVo(desttablename);
						vo.setString("a0100", desta0100);
                       vo.setInt("i9999", Integer.parseInt(i9999));
						vo.setObject("ole", "");
						vo.setString("ext", ext);
						vo.setString("flag", "P");
						vo.setString("fileid", rset.getString("fileid"));
						//**syl 20191204 华远地产上传照片没有创建时间和更新时间**//*
						vo.setDate("createtime", new Date());
						vo.setDate("modtime", new Date());
						vo.setString("createusername", this.userview.getUserName());
						vo.setString("modusername", this.userview.getUserName());
						dao.updateValueObject(vo);
					}
					else
					{
						buf.setLength(0);
						DbWizard dbw=new DbWizard(this.conn);
						String srctab=srctablename;
						String destab=basepre+"A00";
						if(this.ins_id!=0)//liuyz bug31448 templet_xx表中根据a0100可能查处多个记录，导致sql报错
                        {
                            dbw.updateRecord(destab,srctab ,destab+".A0100='"+desta0100+"'",destab+".fileid="+srctab+".fileid`"+"ole=photo`"+destab+".createtime=sysdate`"+destab+".modtime=sysdate`"+destab+".createusername='"+this.userview.getUserName()+"'`"+destab+".modusername='"+this.userview.getUserName()+"'`"+destab+".ext="+srctab+".ext`"+destab+".flag='P'","i9999="+i9999+" and "+destab+".A0100='"+desta0100+"'  ",srctablename+".A0100='"+a0100+"' and upper(basepre)='"+basepre.toUpperCase()+"' and ins_id="+ins_id);
                        } else {
                            dbw.updateRecord(destab,srctab ,destab+".A0100='"+desta0100+"'",destab+".fileid="+srctab+".fileid`"+"ole=photo`"+destab+".createtime=sysdate`"+destab+".modtime=sysdate`"+destab+".createusername='"+this.userview.getUserName()+"'`"+destab+".modusername='"+this.userview.getUserName()+"'`"+destab+".ext="+srctab+".ext`"+destab+".flag='P'","i9999="+i9999+" and "+destab+".A0100='"+desta0100+"'  ",srctablename+".A0100='"+a0100+"' and upper(basepre)='"+basepre.toUpperCase()+"'");
                        }
					}
				}
			}//if end.
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	/**
	 * 拼音简码转换
	 * @param tablename
	 * @param A0100
	 * @param pinyin_field
	 * @param value
	 */
	public void updatePinYinField(String tablename,String A0100)
	{

		ContentDAO dao = new ContentDAO(this.conn);
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		String pinyin_field=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);

		if((pinyin_field==null || "".equals(pinyin_field) || "#".equals(pinyin_field) )) {
            return;
        }

		if(DataDictionary.getFieldItem(pinyin_field.toLowerCase())==null) {
            return;
        }

		StringBuffer buf=new StringBuffer();
		buf.append("select a0101 from ");
		buf.append(tablename);
		buf.append(" where a0100='");
		buf.append(A0100);
		buf.append("'");
		try
		{
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
			{
				String value=rset.getString("a0101");
				if (value==null||value.length()<1) {
                    return;
                }
				PubFunc pf = new PubFunc();
				String pinyin = pf.getPinym(value);
				StringBuffer sb = new StringBuffer();
				sb.append(" update "+tablename);
				sb.append(" set "+pinyin_field+" = '"+pinyin+"'");
				sb.append(" where a0100 ='"+A0100+"'");
				dao.update(sb.toString());
			}
			PubFunc.closeDbObj(rset);
		}catch(Exception e){
			e.printStackTrace();
		}
	}




	/**
	 * 变动确定，从临时表中提交数据至档案库中去
* @param subhm
* @param srcbase 源应用库,如果目标库未定义的话，则按源应用库进行数据更新
	 * 				  如果目标库和源库不一致的话，则先更新数据，然后进行移库操作.对人员调入的业务
	 * 				  直接把数据导入档案库。
	 * @param a0100 人员编码|单位编码|职位编码
	 * @param srctab 临时表名
	 * @param state 人员来源 =0正常操作 =1从其它模板发过来的消息
	 * @param isToOrg 是否是合并、划转的目标记录
	 * @return 返回值为 人员调入至目标库的人员编号
	 */
	private String changeSubmitBK(HashMap subhm,String keyValue,String srctab,int state,boolean isToOrg,LazyDynaBean beanInfo,HashMap tableColumnMap)throws GeneralException
	{
        Object[]   key=subhm.keySet().toArray();
        Arrays.sort(key);
		/**数据库类型*/
		int db_type=Sql_switcher.searchDbServer();

		/**数据同步版*/
		boolean bDatasync=true; //false;
		String strvalue=null;
		String destab=null;
		StringBuffer strsql=new StringBuffer();
		BaseInfoBo infobo=new BaseInfoBo(this.conn,this.userview,this.infor_type);
		/**分析数据处理方式*/

		String desKeyValue=keyValue;
		boolean blinkupdate=false;
		/**记录操作方式*/
		int updatetype=SubSetUpdateType.NOCHANGE;
		RowSet rowSet=null;
		int i9999=1;
		String key_field="b0110";
		if(this.infor_type==3) {
            key_field="e01a1";
        }
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			for(int k=0;k<key.length;k++)
			{
				blinkupdate=false;
				/**清空*/
				strsql.setLength(0);
				String setname=(String)key[k];
				TSubsetCtrl subctrl=(TSubsetCtrl)subhm.get(setname);
				/**目标表*/
				destab=setname;
				strvalue=getChangeUpdateSQL(srctab,destab,subctrl.getFieldlist(),bDatasync);
					/**主集信息更新*/
				if("B01".equalsIgnoreCase(setname)|| "K01".equalsIgnoreCase(setname))
				{
						/**新建机构,先导入主集数据,然后再进行后续的操作*/
						if(this.operationtype==5||(this.operationtype==8&&isToOrg&&keyValue.charAt(0)=='B'))
						{
							String _corcodeField=this.unit_code_field;
							if(this.infor_type==3) {
                                _corcodeField=this.pos_code_field;
                            }
							FieldItem fieldItem = DataDictionary.getFieldItem(_corcodeField);//bug 42939 若库结构中的单位代码指标取消构库，组织机构/机构调整/新增机构时，填写组织单元代码，提交时提示“列名b0111无效”，提示信息不明确
							if(fieldItem==null||"0".equalsIgnoreCase(fieldItem.getUseflag())){
								if(this.infor_type==2){
									throw GeneralExceptionHandler.Handle(new Throwable("单位代码指标在库结构中未构库，需到“组织机构/岗位参数设置”重新指定“单位代码指标"));
								}else if(this.infor_type==3){
									throw GeneralExceptionHandler.Handle(new Throwable("岗位代码指标在库结构中未构库，需到“组织机构/岗位参数设置”重新指定“岗位代码指标"));
								}
							}
							desKeyValue=DbNameBo.expDataIntoArchiveEmpMainSetBK(this.conn,this.userview,this.ins_id,destab,srctab,beanInfo,subctrl.getFieldlist(),_corcodeField);
							continue;
						}

						if(subctrl.getUpdatetype()==SubSetUpdateType.NOCHANGE) {
                            continue;
                        }

						/**更新数据*/
						switch(db_type)
						{
						case 2:
						case 3:
							strsql.append("update ");
							strsql.append(destab);
							strsql.append(" set ");
							strsql.append(strvalue);
							strsql.append(" from ");
							strsql.append(srctab);
							strsql.append(" where ");
							strsql.append(destab);
							strsql.append("."+key_field+"=");
							strsql.append(srctab);
							strsql.append("."+key_field+" ");
							if(this.ins_id!=0)
							{
								strsql.append(" and ins_id=");
								strsql.append(this.ins_id);
							}
							strsql.append(") where ");
							strsql.append(destab);
							strsql.append("."+key_field+"='");
							strsql.append(keyValue);
							strsql.append("'");
							break;
						default:
							strsql.append("update ");
							strsql.append(destab);
							strsql.append(" set ");
							strsql.append(strvalue);
							strsql.append(" from ");
							strsql.append(destab);
							strsql.append(" left join ");
							strsql.append(srctab);
							strsql.append(" on ");
							strsql.append(srctab);
							strsql.append("."+key_field+"=");
							strsql.append(destab);
							strsql.append("."+key_field+"");
							strsql.append(" where ");
							strsql.append(destab);
							strsql.append("."+key_field+"='");
							strsql.append(keyValue);
							strsql.append("'");
							if(this.ins_id!=0)
							{
								strsql.append(" and "+srctab+".ins_id=");
								strsql.append(this.ins_id);
							}
							break;
						}
				}
				else//子集
				{
						/**新建机构,先导入主集数据,然后再进行后续的操作*/
						if(this.operationtype==5||(this.operationtype==8&&isToOrg&&keyValue.charAt(0)=='B'))
						{
							//条件新增
							Integer num=addNewByCondFormula(subctrl,srctab,key_field,keyValue,"","",setname);
							if(num==0){
								continue;
							}
							appendSubRecordBK(srctab,setname,keyValue,desKeyValue,subctrl.getFieldlist(),bDatasync);
							continue;
						}
						i9999=infobo.getMaxI9999("",setname,keyValue);

						/**来源于消息库，且有子集记录已作过追加*/
						if(!isFromMessage(subctrl.getFieldlist(),"",keyValue,state))
						{
							updatetype=subctrl.getUpdatetype();
							/**判断子集指标变化后的值是否和库中最后一条记录的值相同，如果相同，则不操作  */
							updatetype=getUpdateTypeBySubctrlBK(subctrl,destab,srctab,updatetype,keyValue);
							/**子集记录全为追加方式*/
							switch(updatetype)
							{
								case SubSetUpdateType.NOCHANGE:
									continue;
								case SubSetUpdateType.APPEND:
									AutoCreateNextRecord(setname,"",keyValue,i9999,bDatasync,subctrl.getRefPreRec());
									blinkupdate=true;
									break;
								case SubSetUpdateType.UPDATE:
									i9999=i9999-1;
									blinkupdate=true;
									break;
								case SubSetUpdateType.COND_UPDATE:
									i9999=i9999-1;
									blinkupdate=true;
									break;
								case SubSetUpdateType.COND_APPEND:
									//条件新增
									Integer num=addNewByCondFormula(subctrl,srctab,key_field,keyValue,"","",setname);
									if(num==0){
										continue;
									}
									AutoCreateNextRecord(setname,"",keyValue,i9999,bDatasync,subctrl.getRefPreRec());
									blinkupdate=true;
									break;
							}
						}
						else
						{
							i9999=i9999-1;
						}

						switch(db_type)
						{
						case 2:
						case 3:
							strsql.append("update ");
							strsql.append(destab);
							strsql.append(" set ");
							strsql.append(strvalue);
							strsql.append(" from ");
							strsql.append(srctab);
							strsql.append(" where ");
							strsql.append(destab);
							strsql.append("."+key_field+"=");
							strsql.append(srctab);
							strsql.append("."+key_field+" ");
							if(this.ins_id!=0)
							{
								strsql.append(" and ins_id=");
								strsql.append(this.ins_id);
							}

							strsql.append(") where ");
							strsql.append(destab);
							strsql.append("."+key_field+"='");
							strsql.append(keyValue);
							strsql.append("' and ");
							strsql.append(destab);
							strsql.append(".I9999=");
							strsql.append(i9999);
							break;
						default:
							strsql.append("update ");
							strsql.append(destab);
							strsql.append(" set ");
							strsql.append(strvalue);
							strsql.append(" from ");
							strsql.append(destab);
							strsql.append(" left join ");
							strsql.append(srctab);
							strsql.append(" on ");
							strsql.append(srctab);
							strsql.append("."+key_field+"=");
							strsql.append(destab);
							strsql.append("."+key_field+"");
							strsql.append(" where ");
							strsql.append(destab);
							strsql.append("."+key_field+"='");
							strsql.append(keyValue);
							strsql.append("'");
							strsql.append(" and ");
							strsql.append(destab);
							strsql.append(".I9999=");
							strsql.append(i9999);
							if(this.ins_id!=0)
							{
								strsql.append(" and "+srctab+".ins_id=");
								strsql.append(this.ins_id);
							}

							break;
						}
			}
				if(strsql.length()==0) {
                    continue;
                }
				dao.update(strsql.toString());
				if(blinkupdate) {
                    linkUpdatePreRec(setname,setname,keyValue,i9999);
                }
			}//while loop end.
			/**子集区域*/
			if(this.operationtype==5||(this.operationtype==8&&isToOrg&&keyValue.charAt(0)=='B')) {
                subSetChangeSubmit("",srctab,keyValue,desKeyValue);
            } else {
                subSetChangeSubmit("",srctab,keyValue);
            }
			/**清空消息库的记录*/
			if(state==1)
			{
				strsql.setLength(0);
strsql.append("delete from tmessage where noticetempid=?");
				strsql.append(" and b0110=?");
				ArrayList paralist=new ArrayList();
				paralist.add(Integer.valueOf(this.tabid));
				paralist.add(keyValue);
				dao.update(strsql.toString(),paralist);
			}

			//如果为撤销 ，被合并,被划转 的记录
			if(this.operationtype==7||(this.operationtype==8&&!isToOrg)||(this.operationtype==9&&!isToOrg))
			{
				Date startdate=(Date)beanInfo.get("start_date");
				SimpleDateFormat fm=new SimpleDateFormat("yyyy-MM-dd");
				Calendar cd=Calendar.getInstance();
				cd.setTime(startdate);
				String subsql="end_date="+Sql_switcher.charToDate("'"+fm.format(cd.getTime())+"'");
				String _sql = "";
				if(this.operationtype!=7){   //xyy 2014-12-3 当是划转或合并的时候
				_sql="update organization set "+subsql+" where codeitemid='"+keyValue+"'";

				}else{//下级机构也需要更改失效时间 like wangrd 2014-06-13
				_sql="update organization set "+subsql+" where codeitemid like '"+keyValue+"%'"+"and end_date >"+Sql_switcher.charToDate("'"+fm.format(cd.getTime())+"'");

				}
                dao.update(_sql);
                //合并划转修改 b01 k01 modtime
                _sql = "update B01 set modtime="+Sql_switcher.sqlNow()+" where b0110 like '"+keyValue+"%' ";
                dao.update(_sql);
_sql = "update k01 set modtime="+Sql_switcher.sqlNow()+" where e01a1 like '"+keyValue+"%'";
                dao.update(_sql);
			}
			if(this.operationtype==6) //更名
			{
				String codeitemdesc=(String)beanInfo.get("codeitemdesc");
				String _sql="update organization set codeitemdesc='"+codeitemdesc+"' where codeitemid='"+keyValue+"'";
            dao.update(_sql);

            try{
                RecordVo recordVo = new RecordVo("organization");
					recordVo.setString("codeitemid", keyValue);
					recordVo.setString("codesetid", "UN");
					recordVo=dao.findByPrimaryKey(recordVo);

					if(recordVo!=null){//自动更新数据字典里相关数据 xgq
						Map lenmap = recordVo.getAttrLens();
						  int codeitemdesclen = Integer.parseInt((String)lenmap.get("codeitemdesc"));
					 AdminCode.updateCodeItemDesc(recordVo.getString("codesetid").toUpperCase(), recordVo.getString("codeitemid").toUpperCase(),PubFunc.splitString(recordVo.getString("codeitemdesc"),codeitemdesclen));
                 }
             	}catch(Exception e2){
					try{
						RecordVo recordVo = new RecordVo("organization");
						recordVo.setString("codeitemid", keyValue);
						recordVo.setString("codesetid", "UM");
						recordVo=dao.findByPrimaryKey(recordVo);

						if(recordVo!=null){//自动更新数据字典里相关数据 xgq
							Map lenmap = recordVo.getAttrLens();
							  int codeitemdesclen = Integer.parseInt((String)lenmap.get("codeitemdesc"));
						 AdminCode.updateCodeItemDesc(recordVo.getString("codesetid").toUpperCase(), recordVo.getString("codeitemid").toUpperCase(),PubFunc.splitString(recordVo.getString("codeitemdesc"),codeitemdesclen));
					}
					}catch(Exception e3){
						try{
							RecordVo recordVo = new RecordVo("organization");
							recordVo.setString("codeitemid", keyValue);
							recordVo.setString("codesetid", "@K");
							recordVo=dao.findByPrimaryKey(recordVo);

							if(recordVo!=null){//自动更新数据字典里相关数据 xgq
								Map lenmap = recordVo.getAttrLens();
								  int codeitemdesclen = Integer.parseInt((String)lenmap.get("codeitemdesc"));
							 AdminCode.updateCodeItemDesc(recordVo.getString("codesetid").toUpperCase(), recordVo.getString("codeitemid").toUpperCase(),PubFunc.splitString(recordVo.getString("codeitemdesc"),codeitemdesclen));
						}
						}catch(Exception e4){

						}
					}
				}
			}
			String to_id=(String)beanInfo.get("to_id");
			if(this.operationtype==8&&to_id.charAt(0)=='B'&&to_id.equalsIgnoreCase(keyValue))//合并
            {
                beanInfo.set("to_id", desKeyValue);
            }
			if((this.operationtype==9||this.operationtype==8)&&!isToOrg) //划转||合并
			{
				if(this.operationtype==9)
				{
					delimit(beanInfo,dao,true,1);
					desKeyValue=(String)beanInfo.get("new_id");
				}
				else {
                    delimit(beanInfo,dao,true,2);
                }
			}

			if(this.operationtype==8&&isToOrg&&keyValue.charAt(0)!='B') //划转||合并
			{
				String updateStr="";
				if(tableColumnMap.get("corcode_2")!=null&&beanInfo.get("corcode")!=null&&((String)beanInfo.get("corcode")).trim().length()>0)
				{
					updateStr+=",corcode='"+(String)beanInfo.get("corcode")+"'";
				}
				if(tableColumnMap.get("codeitemdesc_2")!=null&&beanInfo.get("codeitemdesc")!=null&&((String)beanInfo.get("codeitemdesc")).trim().length()>0)
				{
					updateStr+=",codeitemdesc='"+(String)beanInfo.get("codeitemdesc")+"'";
				}
				if(updateStr.length()>0)
				{
					String _sql="update organization set "+updateStr.substring(1)+" where codeitemid='"+keyValue+"'";
					dao.update(_sql);
				}
			}

			return desKeyValue;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally {
			PubFunc.closeDbObj(rowSet);
		}
	}

	/**
	 * 更新组织机构层级
	 */
	public void updateOrgLayerGrade()
	{
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			dao.update("update organization set layer = null");
			String sql = "update organization set layer=1 where (codeitemid=parentid) or "+
		    " not (parentid in (select codeitemid from organization B where organization.codesetid=B.codesetid))";
			dao.update(sql);
			int i=1;
			while(true){
				sql = "update organization set layer='"+(i+1)+"' where codeitemid<>parentid and "+
			       " parentid in (select codeitemid from organization B where organization.codesetid=B.codesetid and B.layer='"+i+"')";;
				int j = dao.update(sql.toString());
				if(j==0) {
                    break;
                }
				i++;

			}
			//重置grade 不能清空，当组织机构表上有触发器时，会影响到数据视图  wangb 2019-08-26 bug 52655
			//dao.update("update organization set grade = null");
			sql ="update organization set grade=1 where parentid=codeitemid";
			dao.update(sql.toString());
			i=1;
			while(true){
				sql = "update organization set grade='"+(i+1)+"' where codeitemid<>parentid and "+
			       " parentid in (select codeitemid from organization B where  B.grade='"+i+"')";;
int j = dao.update(sql.toString());
				if(j==0) {
                    break;
                }
				i++;

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception ee)
			{

			}
		}
	}



	/**
	 * 机构划转
	 * @param beanInfo
	 * @param flag 1:划转  2：合并
	 */
	private void delimit(LazyDynaBean beanInfo,ContentDAO dao,boolean isStart,int flag)
	{
		RowSet rowSet=null;
		try
		{
		    long nowTime = System.currentTimeMillis(); //当前时间
			SimpleDateFormat fm=new SimpleDateFormat("yyyy-MM-dd");
		    if(!isStart)
		    {
		    	String parent_id=(String)beanInfo.get("parent_id");
				String src_parent_id=(String)beanInfo.get("src_parent_id");
				Date startdate=(Date)beanInfo.get("start_date");
				String a0000=(String)beanInfo.get("a0000");

				String self_id=(String)beanInfo.get("self_id");
				String new_id="";
	   		    if(flag==1) {
                    new_id=self_id.replaceFirst(src_parent_id,parent_id);
              } else
	   		    {
	   		    	LazyDynaBean bean=DbNameBo.getCodeitem(parent_id,this.conn);
	   		    	new_id=(String)bean.get("codeitemid");
	   		    }
			    rowSet=dao.search("select * from organization where parentid='"+self_id+"' order by a0000");
			    String childid="";
			    LazyDynaBean _bean=null;
			    int child_a0000=Integer.parseInt(a0000);
			    while(rowSet.next())
			    {
			    	child_a0000++;
			    	String codeitemid=rowSet.getString("codeitemid");
			    	_bean=new LazyDynaBean();
			    	_bean.set("a0000",String.valueOf(child_a0000));
			    	_bean.set("parent_id",new_id);
			    	_bean.set("src_parent_id",self_id);
			    	_bean.set("self_id",codeitemid);
			    	_bean.set("start_date",startdate);
			    	if(rowSet.getDate("end_date").getTime()-nowTime>0){  //xyy20141203是为了取出被合并单位或部门的没有被撤销的下级
                        delimit(_bean,dao,false,flag);

                    }

			    	childid=codeitemid.replaceFirst(self_id, new_id);
			    }
			    if(childid.length()==0) {
                    childid=new_id;
                }

			    Calendar cd=Calendar.getInstance();
				cd.setTime(startdate);//在机构合并的时候使合并和划转的机构生效日期由模版中设置的来控制
		    	String sql="insert into organization (codesetid,codeitemid,codeitemdesc,parentid,childid,corcode,end_date,start_date,a0000) select codesetid,'"+new_id+"'";
		    	sql+=",codeitemdesc,'"+parent_id+"','"+childid+"',corcode,"+Sql_switcher.charToDate("'9999-12-31'")+","+Sql_switcher.charToDate("'"+fm.format(cd.getTime())+"'")+","+a0000+" from organization where codeitemid='"+self_id+"' ";
		    	mappingStr+=","+self_id+"="+new_id; //机构编码的旧新对应关系

		    	dao.update(sql);
		    	cd.setTime(startdate);
		    //	cd.add(Calendar.DATE,-1);
			    dao.update("update organization set end_date="+Sql_switcher.charToDate("'"+fm.format(cd.getTime())+"'")+" where codeitemid='"+self_id+"'");

			    if(flag==2)
			    {
			    	dao.update("update organization set childid='"+new_id+"' where codeitemid='"+parent_id+"'");
			    }

			    if(this.infor_type==2)  //单位
	    		{
			    	 String codesetid="";
			    	 rowSet=dao.search("select codesetid from organization where codeitemid='"+self_id+"'");
			    	 if(rowSet.next()) {
                         codesetid=rowSet.getString(1);
                     }
			    	if(codesetid!=null&& "@K".equalsIgnoreCase(codesetid.trim()))
			    	{
			    		ArrayList orgList=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
				    	for(int n=0;n<orgList.size();n++)
						{
							 FieldSet fieldset=(FieldSet)orgList.get(n);
							 //if(fieldset.getFieldsetid().equalsIgnoreCase("K01")){//需要更新K01表中的数据的话，就变成复制一条出来，用以保留历史数据
							     String copysql=copyRecordIntoTable(fieldset.getFieldsetid(), dao, new_id, self_id,parent_id);
							     if(copysql.length()>0){
							         dao.update(copysql);
							     }

							   //dao.update("update "+fieldset.getFieldsetid()+"  set e01a1='"+new_id+"',e0122='"+parent_id+"' where e01a1='"+self_id+"'");
//							 }else{
//							     dao.update("update "+fieldset.getFieldsetid()+"  set e01a1='"+new_id+"'  where e01a1='"+self_id+"'");
//							 }

						}
			    	}
			    	else
			    	{
				    	ArrayList orgList=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
				    	for(int n=0;n<orgList.size();n++)
						{
							 FieldSet fieldset=(FieldSet)orgList.get(n);
                     // if(fieldset.getFieldsetid().equalsIgnoreCase("B01")){//需要更新B01表中的数据的话，就变成复制一条出来，用以保留历史数据
							     String copysql=copyRecordIntoTable(fieldset.getFieldsetid(), dao, new_id, self_id,"");
                                 if(copysql.length()>0){
                                     dao.update(copysql);
                                 }
//							 }else{
//							     dao.update("update "+fieldset.getFieldsetid()+" set b0110='"+new_id+"' where b0110='"+self_id+"'");
//							 }
						}
			    	}
	    		}
	    		else if(this.infor_type==3) //职位
	    		{
	    			ArrayList orgList=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
			    	for(int n=0;n<orgList.size();n++)
					{
						 FieldSet fieldset=(FieldSet)orgList.get(n);
						 //if(fieldset.getFieldsetid().equalsIgnoreCase("K01")){//需要更新K01表中的数据的话，就变成复制一条出来，用以保留历史数据
						     String copysql=copyRecordIntoTable(fieldset.getFieldsetid(), dao, new_id, self_id,parent_id);
if(copysql.length()>0){
                            dao.update(copysql);
                             }
                             //dao.update("update "+fieldset.getFieldsetid()+"  set e01a1='"+new_id+"',e0122='"+parent_id+"' where e01a1='"+self_id+"'");
//						 }else{
//						     dao.update("update "+fieldset.getFieldsetid()+"  set e01a1='"+new_id+"'  where e01a1='"+self_id+"'");
//						 }

					}
	    		}
		    }
		    else
		    {
		    	String to_id=(String)beanInfo.get("to_id");
		    	String key_value=(String)beanInfo.get("keyValue");
		    	Date startdate=(Date)beanInfo.get("start_date");

		    	String codeitemid="";
		    	int a0000=0;
		    	if(flag==1) //flag 1:划转  2：合并
		    	{
		    		//LazyDynaBean bean=DbNameBo.getCodeitem(to_id,this.conn); 改变划转生成新代码规则  2014-04-16 guodd
		    		LazyDynaBean bean=DbNameBo.getCodeitem(to_id,beanInfo.get("keyValue").toString(),this.conn,null);

		    		codeitemid=(String)bean.get("codeitemid");
		    		a0000=Integer.parseInt((String)bean.get("a0000"));
		    	}
		    	else
		    	{

		    		rowSet=dao.search("select max(a0000)  from organization where parentid='"+to_id+"' and codeitemid<>parentid ");
					if(rowSet.next()) {
                        a0000=rowSet.getInt(1)+1;
                    }
		    	}

String childid="";
LazyDynaBean _bean=null;
	rowSet=dao.search("select * from organization where parentid='"+key_value+"'  and codeitemid<>parentid order by a0000");
		    	int child_a0000=a0000;
		    	while(rowSet.next())
		    	{
		    		child_a0000++;
		    		_bean=new LazyDynaBean();
		    		if(flag==1)
		    		{
		    			_bean.set("parent_id",codeitemid);
                        _bean.set("src_parent_id",key_value);
		    		}
		    		else
		    		{
		    			_bean.set("parent_id",to_id);
		    			_bean.set("src_parent_id",key_value);
                    }
                    _bean.set("a0000",String.valueOf(child_a0000));
			    	_bean.set("self_id",rowSet.getString("codeitemid"));
			    	_bean.set("start_date",startdate);
			    	if(rowSet.getDate("end_date").getTime()-nowTime>0){ //xyy20141203是为了取出被合并单位或部门的没有被撤销的下级
			    	    delimit(_bean,dao,false,flag);

			    	}
			    	childid=rowSet.getString("codeitemid").replaceFirst(key_value, codeitemid);
		    	}
		        Calendar cd=Calendar.getInstance();
				cd.setTime(startdate);//在机构合并的时候使合并和划转的机构生效日期由模版中设置的来控制
				String sql="";
		    	if(flag==1)
		    	{
		    		sql="insert into organization (codesetid,codeitemid,codeitemdesc,parentid,childid,corcode,end_date,start_date,a0000) select codesetid,'"+codeitemid+"'";
                sql+=",codeitemdesc,'"+to_id+"','"+childid+"',corcode,"+Sql_switcher.charToDate("'9999-12-31'")+","+Sql_switcher.charToDate("'"+fm.format(cd.getTime())+"'")+","+a0000+" from organization where codeitemid='"+key_value+"' ";
		    		dao.update(sql);
		    		mappingStr+=","+key_value+"="+codeitemid; //机构编码的旧新对应关系
		    		dao.update("update organization set childid='"+codeitemid+"' where codeitemid='"+to_id+"'");

		    		beanInfo.set("new_id",codeitemid);
		    		if(this.infor_type==2)  //单位
		    		{
		    			 String codesetid="";
				    	 rowSet=dao.search("select codesetid from organization where codeitemid='"+key_value+"'");
				    	 if(rowSet.next()) {
                             codesetid=rowSet.getString(1);
                         }
				    	if(codesetid!=null&& "@K".equalsIgnoreCase(codesetid.trim()))
		    			{
		    				ArrayList orgList=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
					    	for(int n=0;n<orgList.size();n++)
							{
                                             FieldSet fieldset=(FieldSet)orgList.get(n);
								 //if(fieldset.getFieldsetid().equalsIgnoreCase("K01")){//需要更新K01表中的数据的话，就变成复制一条出来，用以保留历史数据
								     String copysql=copyRecordIntoTable(fieldset.getFieldsetid(), dao, codeitemid, key_value,to_id);
		                             if(copysql.length()>0){
		                                 dao.update(copysql);
		                             }
		                             //dao.update("update "+fieldset.getFieldsetid()+" set e01a1='"+codeitemid+"',e0122='"+to_id+"' where e01a1='"+key_value+"'");
//								 }else{
//									 dao.update("update "+fieldset.getFieldsetid()+"  set e01a1='"+codeitemid+"'  where e01a1='"+key_value+"'");
//								 }
							}
		    			}
		    			else
		    			{
			    			ArrayList orgList=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
					    	for(int n=0;n<orgList.size();n++)
							{
								 FieldSet fieldset=(FieldSet)orgList.get(n);
								 //if(fieldset.getFieldsetid().equalsIgnoreCase("B01")){//需要更新B01表中的数据的话，就变成复制一条出来，用以保留历史数据
	                                 String copysql=copyRecordIntoTable(fieldset.getFieldsetid(), dao, codeitemid, key_value,"");
	                                  if(copysql.length()>0){
	                                      dao.update(copysql);
	                                  }
//								 }else{
//								     dao.update("update "+fieldset.getFieldsetid()+" set b0110='"+codeitemid+"' where b0110='"+key_value+"'");
//								 }
							}
		    			}
		    		}
		    		else if(this.infor_type==3) //职位
		    		{
		    			//dao.update("update k01 set e01a1='"+codeitemid+"',e0122='"+to_id+"' where e01a1='"+key_value+"'");
		    			ArrayList orgList=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
				    	for(int n=0;n<orgList.size();n++)
						{
							 FieldSet fieldset=(FieldSet)orgList.get(n);
							 //if(fieldset.getFieldsetid().equalsIgnoreCase("K01")){
							     String copysql=copyRecordIntoTable(fieldset.getFieldsetid(), dao, codeitemid, key_value,to_id);
                                 if(copysql.length()>0){
                                     dao.update(copysql);
                                 }
							     //dao.update("update "+fieldset.getFieldsetid()+" set e01a1='"+codeitemid+"',e0122='"+to_id+"' where e01a1='"+key_value+"'");
//							 }else{
//							     dao.update("update "+fieldset.getFieldsetid()+"  set e01a1='"+codeitemid+"'  where e01a1='"+key_value+"'");
//							 }
						}
		    		}
		    	}
		    }

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception ee)
			{

			}
		}
	}


	/**
	 * 信息导入调转人员库
	 * @param subhm
	 * @param srcbase
	 * @param a0100
	 * @param srctab
	 * @return
	 * @throws GeneralException
	 */
	private void submitTrs(HashMap subhm,String srcbase,String a0100,String srctab)throws GeneralException
	{
		String desA0100="";
		DbNameBo dbbo=new DbNameBo(this.conn);
		desA0100=dbbo.moveDataBetweenBase3(a0100, srcbase, dest_base);
        Object[]   key   =     subhm.keySet().toArray();
        Arrays.sort(key);
		/**数据库类型*/
		int db_type=Sql_switcher.searchDbServer();
		/**数据同步版*/
		boolean bDatasync=true; //false;
		String strvalue=null;
		String destab=null;
		StringBuffer strsql=new StringBuffer();
		BaseInfoBo infobo=new BaseInfoBo(this.conn,this.userview,1);
		/**分析数据处理方式*/
		int noper=1;
/**记录操作方式*/
		int updatetype=SubSetUpdateType.NOCHANGE;
		int i9999=1;
		RowSet rowSet=null;
try
{
ContentDAO dao=new ContentDAO(this.conn);
boolean blinkupdate=false;
for(int k=0;k<key.length;k++)
			{
				blinkupdate=false;
				/**清空*/
				strsql.setLength(0);
            String setname=(String)key[k];
            TSubsetCtrl subctrl=(TSubsetCtrl)subhm.get(setname);
            /**目标表*/
            destab=this.dest_base+setname;
switch(setname.charAt(0))
{
case 'A'://人员信息
case 'a':
					strvalue=getChangeUpdateSQL(srctab,destab,subctrl.getFieldlist(),bDatasync);
					/**主集信息更新*/
					if("a01".equalsIgnoreCase(setname))
					{
						continue;
					}
					else//子集
					{
						i9999=infobo.getMaxI9999(this.dest_base,setname,desA0100);
						updatetype=subctrl.getInnerupdatetype();
						/**判断子集指标变化后的值是否和库中最后一条记录的值相同，如果相同，则不操作  */
						updatetype=getUpdateTypeBySubctrl(subctrl,srcbase+setname,srctab,updatetype,a0100,srcbase);
						/**人员调入，子集记录全为追加方式*/
						switch(updatetype)
						{
							case SubSetUpdateType.NOCHANGE:
								continue;
							case SubSetUpdateType.APPEND:
								AutoCreateNextRecord(setname,this.dest_base,desA0100,i9999,bDatasync,subctrl.getRefPreRec());
								blinkupdate=true;
								break;
                                case SubSetUpdateType.UPDATE:
                                    i9999=i9999-1;
                                    blinkupdate=true;
break;
          case SubSetUpdateType.COND_UPDATE:
								i9999=i9999-1;
								blinkupdate=true;
								break;
							case SubSetUpdateType.COND_APPEND:
								//条件新增
								Integer num=addNewByCondFormula(subctrl,srctab,"","",a0100,srcbase,setname);
								if(num==0){
									continue;
								}
								AutoCreateNextRecord(setname,this.dest_base,desA0100,i9999,bDatasync,subctrl.getRefPreRec());
								blinkupdate=true;
								break;

						}

						switch(db_type)
						{
						case 2:
						case 3:
							strsql.append("update ");
							strsql.append(destab);
							strsql.append(" set ");
							strsql.append(strvalue);
							strsql.append(" from ");
							strsql.append(srctab);
							strsql.append(" where ");
							strsql.append(destab);
							strsql.append(".a0100='"+desA0100+"' and ");
							strsql.append(srctab);
							strsql.append(".a0100='"+a0100+"' ");
							if(this.ins_id!=0)
							{
								strsql.append(" and ins_id=");
								strsql.append(this.ins_id);
							}
							if(operationtype!=0) {
                                strsql.append(" and lower("+srctab+".basepre)='"+srcbase.toLowerCase()+"' ");
                            }


							strsql.append(") where ");
							strsql.append(destab);
							strsql.append(".a0100='");
							strsql.append(desA0100);
							strsql.append("' and ");
							strsql.append(destab);
							strsql.append(".I9999=");
							strsql.append(i9999);
                    				break;
						default:
							strsql.append("update ");
							strsql.append(destab);
							strsql.append(" set ");
							strsql.append(strvalue);
							strsql.append(" from ");
							strsql.append(destab);
							strsql.append(" left join ");
							strsql.append(srctab);
							strsql.append(" on ");
							strsql.append(srctab);
							strsql.append(".a0100='"+a0100+"' and ");
							strsql.append(destab);
							strsql.append(".a0100='"+desA0100+"' ");
							strsql.append(" where ");
							strsql.append(destab);
							strsql.append(".a0100='");
							strsql.append(desA0100);
							strsql.append("'");
							strsql.append(" and ");
							strsql.append(destab);
							strsql.append(".I9999=");
							strsql.append(i9999);
							if(this.ins_id!=0)
							{
								strsql.append(" and "+srctab+".ins_id=");
								strsql.append(this.ins_id);
							}
							if(operationtype!=0) {
                                strsql.append(" and lower("+srctab+".basepre)='"+srcbase.toLowerCase()+"' ");
                            }

							break;
						}
					}
					break;
				case 'B'://单位信息
				case 'b':
					break;
				case 'K'://职位信息
				case 'k':
					break;
				}
				if(strsql.length()==0) {
                    continue;
                }
				dao.update(strsql.toString());
				if(blinkupdate) {
                    linkUpdatePreRec(setname,this.dest_base+setname,desA0100,i9999);
                }



			}//while loop end.
			/**子集区域*/
			subTrsSetChangeSubmit(subhm,srcbase,srctab,a0100,desA0100);

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally{
			PubFunc.closeDbObj(rowSet);
		}
	}


	/**
	 * 获得模板的目标库
	 * @return
	 */
	public String getDestBase()
	{
		String destBase="";
		if(this.operationtype==1||this.operationtype==2||this.operationtype==4||this.operationtype==0) //不知道为什么这样写目标库但是少了operationtype==0这种情况的判断
		{
				if(!(this.dest_base==null|| "".equals(this.dest_base))) {
                    destBase=this.dest_base;
                }
		}
		return destBase;
	}



	/**
	 * 变动确定，从临时表中提交数据至档案库中去
	 * @param subhm
	 * @param srcbase 源应用库,如果目标库未定义的话，则按源应用库进行数据更新
	 * 				  如果目标库和源库不一致的话，则先更新数据，然后进行移库操作.对人员调入的业务
	 * 				  直接把数据导入档案库。
	 * @param a0100 人员编码
	 * @param srctab 临时表名
	 * @param state 人员来源 =0正常操作 =1从其它模板发过来的消息
	 * @return 返回值为 人员调入至目标库的人员编号
	 */
	private String changeSubmit(HashMap subhm,String srcbase,String a0100,String srctab,int state)throws GeneralException
	{
		if (this.operationtype == 4 && this.infor_type == 1) {
			submitTrs(subhm, srcbase, a0100, srctab);
		}

		Object[] key = subhm.keySet().toArray();
		Arrays.sort(key);

		/** 数据库类型 */
		int db_type = Sql_switcher.searchDbServer();
		/** 数据同步版 */
		boolean bDatasync = true; // false;
		String strvalue = null;
		String destab = null;
		StringBuffer strsql = new StringBuffer();
		BaseInfoBo infobo = new BaseInfoBo(this.conn, this.userview, 1);
		/** 分析数据处理方式 */
		int noper = 0;
		String desA0100 = "";
		boolean blinkupdate = false;
		/** 记录操作方式 */
		int updatetype = SubSetUpdateType.NOCHANGE;
		if (operationtype != 0)// =0:人员调入 =1：人员调出
		{
			/** 如果目标库和源库不一致的话，则先更新数据，然后进行移库操作 */
			switch (operationtype) {
			case 1:// 人员调出
			case 2:// 人员离退
				if (!(this.dest_base == null || "".equals(this.dest_base)) && (!dest_base.equalsIgnoreCase(srcbase))) {
					noper = 2;
				} else/** 如果目标库未定义的话，则按源应用库进行数据更新 */
				{
					desA0100 = a0100;
					noper = 1;
				}
				break;
			default:// 其它业务，只进行更新
				noper = 1;
				break;
			}
		} else// 人员调入
		{
			noper = 3;
			srcbase = this.dest_base; // 主要为了新调入人员，增加记录
		}

		if (this.infor_type == 2 || this.infor_type == 3) {
			srcbase = "";
		}
		int i9999 = 1;
		boolean bSumitedSubArea = false;// 提交子集区域标志
		RowSet rowSet = null;
		try {
			YksjParser yp = null;
			ContentDAO dao = new ContentDAO(this.conn);

			if (this.operationtype == 4 || this.operationtype == 0) // 系统内部调动 || 人员调入
			{

				for (int k = 0; k < key.length; k++)
				// while(iterator.hasNext())
				{
					blinkupdate = false;
					/** 清空 */
					strsql.setLength(0);
					String setname = (String) key[k];
					TSubsetCtrl subctrl = (TSubsetCtrl) subhm.get(setname);
					// Entry entry=(Entry)iterator.next();
					// String setname=(String)entry.getKey();
					// TSubsetCtrl subctrl=(TSubsetCtrl)entry.getValue();
					/** 目标表 */
					destab = srcbase + setname;
					switch (setname.charAt(0)) {
					case 'A':// 人员信息
					case 'a':
						strvalue = getChangeUpdateSQL(srctab, destab, subctrl.getFieldlist(), bDatasync);
						/** 主集信息更新 */
						if ("a01".equalsIgnoreCase(setname)) {
							/** 人员调入操作业务,先导入主集数据,然后再进行后续的操作 */
							if (noper == 3) {
								desA0100 = DbNameBo.expDataIntoArchiveEmpMainSet(this.conn, this.userview, this.ins_id,
										this.dest_base, setname, srctab, a0100, subctrl.getFieldlist()); // expDataIntoArchiveEmpMainSet(this.dest_base,setname,srctab,a0100,subctrl.getFieldlist());
								/** 拼音简码处理 */
								updatePinYinField(this.dest_base + setname, desA0100);
								// 提交完主集后 就提交子集区域 wangrd 2014-06-27
								subSetChangeSubmit(srcbase, srctab, a0100, desA0100);
								bSumitedSubArea = true;
								continue;
							}

							if (subctrl.getUpdatetype() == SubSetUpdateType.NOCHANGE) {
								subSetChangeSubmit(srcbase, srctab, a0100);
								bSumitedSubArea = true;
								continue;
							}

							/** 更新数据 */
							switch (db_type) {
							case 2:
							case 3:
								strsql.append("update ");
								strsql.append(destab);
								strsql.append(" set ");
								strsql.append(strvalue);
								strsql.append(" from ");
								strsql.append(srctab);
								strsql.append(" where ");
								strsql.append(destab);
								strsql.append(".a0100=");
								strsql.append(srctab);
								strsql.append(".a0100 ");
								if (this.ins_id != 0) {
									strsql.append(" and ins_id=");
									strsql.append(this.ins_id);
								}
								if (operationtype != 0) {
									strsql.append(" and lower(basepre)='" + srcbase.toLowerCase() + "' ");
								}

								strsql.append(") where ");
								strsql.append(destab);
								strsql.append(".a0100='");
								strsql.append(a0100);
								strsql.append("'");
								break;
							default:
								strsql.append("update ");
								strsql.append(destab);
								strsql.append(" set ");
								strsql.append(strvalue);
								strsql.append(" from ");
								strsql.append(destab);
								strsql.append(" left join ");
								strsql.append(srctab);
								strsql.append(" on ");
								strsql.append(srctab);
								strsql.append(".a0100=");
								strsql.append(destab);
								strsql.append(".a0100");
								strsql.append(" where ");
								strsql.append(destab);
								strsql.append(".a0100='");
								strsql.append(a0100);
								strsql.append("'");
								if (this.ins_id != 0) {
									strsql.append(" and " + srctab + ".ins_id=");
									strsql.append(this.ins_id);
								}
								if (operationtype != 0) {
									strsql.append(
											" and lower(" + srctab + ".basepre)='" + srcbase.toLowerCase() + "' ");
								}

								break;
							}
							// 提交完主集后 马上提交子集区域 wangrd 2014-06-27
							subSetChangeSubmit(srcbase, srctab, a0100);
							bSumitedSubArea = true;
						} else// 子集
						{
							/** 人员调入业务，子集记录必为追加 */
							if (noper == 3) {
								if (subctrl.getUpdatetype() == 4) {// 条件新增
									Integer num = addNewByCondFormula(subctrl, srctab, "", "", a0100, srcbase, setname);
									if (num == 0) {
										continue;
									}
								}
								int _updatetype = getUpdateTypeBySubctrlCallIn(subctrl, destab, srctab,
										SubSetUpdateType.APPEND, a0100, srcbase);
								if (_updatetype == 0) {
									continue;
								}
								appendSubRecord(srctab, setname, srcbase, a0100, desA0100, subctrl.getFieldlist(),
										bDatasync);
								continue;
							}
							i9999 = infobo.getMaxI9999(srcbase, setname, a0100);
							int _i9999 = i9999;
							/** 来源于消息库，且有子集记录已作过追加 */
							if (!isFromMessage(subctrl.getFieldlist(), srcbase, a0100, state)) {
								updatetype = subctrl.getUpdatetype();
								/** 判断子集指标变化后的值是否和库中最后一条记录的值相同，如果相同，则不操作 */
								if ((noper == 1 || noper == 2) && operationtype != 0) {
									updatetype = getUpdateTypeBySubctrl(subctrl, destab, srctab, updatetype, a0100,
											srcbase);
								}

								/** 人员调入，子集记录全为追加方式 */
								switch (updatetype) {
								case SubSetUpdateType.NOCHANGE:
									continue;
								case SubSetUpdateType.APPEND:
									AutoCreateNextRecord(setname, srcbase, a0100, i9999, bDatasync,
											subctrl.getRefPreRec());
									blinkupdate = true;
									break;
								case SubSetUpdateType.UPDATE:
									if (i9999 == 1)// 子集中无记录 2013-12-31 邓灿
									{
										AutoCreateNextRecord(setname, srcbase, a0100, i9999, bDatasync, 0);
									} else {
										i9999 = i9999 - 1;
										blinkupdate = true;
									}
									break;
								case SubSetUpdateType.COND_UPDATE:
									i9999 = i9999 - 1;
									blinkupdate = true;
									break;
								case SubSetUpdateType.COND_APPEND:// 条件新增
									Integer num = addNewByCondFormula(subctrl, srctab, "", "", a0100, srcbase, setname);
									if (num == 0) {
										continue;
									}
									AutoCreateNextRecord(setname, srcbase, a0100, i9999, bDatasync,
											subctrl.getRefPreRec());
									blinkupdate = true;
									break;
								}
							} else {
								i9999 = i9999 - 1;
							}

							String cond_str = "";
							if (subctrl.getUpdatetype() == 3) // 子集条件更新
							{
								String condFormula = subctrl.getCondFormula();
								if (condFormula == null || condFormula.trim().length() == 0) {
									cond_str = " and ( 1=1 ) ";
								} else {
									yp = new YksjParser(this.userview, getCondUpdateFieldList(setname),
											YksjParser.forNormal, YksjParser.LOGIC, YksjParser.forPerson, "Ht", "");

									yp.run_where(condFormula);
									String strfilter = yp.getSQL();
									if (strfilter.length() > 0) {
										cond_str = " and (" + strfilter + ") ";
									}

								}
								String sql = "select count(" + destab + ".a0100) from  (select a.* from " + destab
										+ " a where a.i9999=(select max(b.i9999) from " + destab
										+ " b where a.a0100=b.a0100 group by b.a0100 ) " + " ) " + destab + "," + srctab
										+ " where " + destab + ".a0100=" + srctab + ".a0100 and  " + srctab + ".a0100='"
										+ a0100 + "'" + cond_str;
								if (this.ins_id != 0) {
									sql += " and ins_id=" + this.ins_id;
								}
								if (operationtype != 0) {
									sql += " and lower(" + srctab + ".basepre)='" + srcbase.toLowerCase() + "' ";
								}
								rowSet = dao.search(sql);
								if (rowSet.next()) {
									if (rowSet.getInt(1) == 0) {
										AutoCreateNextRecord(setname, srcbase, a0100, _i9999, bDatasync,
												subctrl.getRefPreRec());
										blinkupdate = true;
										i9999 = _i9999;
									}
								}
								PubFunc.closeDbObj(rowSet);
							}

							switch (db_type) {
							case 2:
							case 3:
								strsql.append("update ");
								strsql.append(destab);
								strsql.append(" set ");
								strsql.append(strvalue);
								strsql.append(" from ");
								strsql.append(srctab);
								strsql.append(" where ");
								strsql.append(destab);
								strsql.append(".a0100=");
								strsql.append(srctab);
								strsql.append(".a0100 ");
								if (this.ins_id != 0) {
									strsql.append(" and ins_id=");
									strsql.append(this.ins_id);
								}
								if (operationtype != 0) {
									strsql.append(
											" and lower(" + srctab + ".basepre)='" + srcbase.toLowerCase() + "' ");
								}

								strsql.append(") where ");
								strsql.append(destab);
								strsql.append(".a0100='");
								strsql.append(a0100);
								strsql.append("' ");
								strsql.append(" and ");
								strsql.append(destab);
								strsql.append(".I9999=");
								strsql.append(i9999);
								break;
							default:
								strsql.append("update ");
								strsql.append(destab);
								strsql.append(" set ");
								strsql.append(strvalue);
								strsql.append(" from ");
								strsql.append(destab);
								strsql.append(" left join ");
								strsql.append(srctab);
								strsql.append(" on ");
								strsql.append(srctab);
								strsql.append(".a0100=");
								strsql.append(destab);
								strsql.append(".a0100");
								strsql.append(" where ");
								strsql.append(destab);
								strsql.append(".a0100='");
								strsql.append(a0100);
								strsql.append("'");

								strsql.append(" and ");
								strsql.append(destab);
								strsql.append(".I9999=");
								strsql.append(i9999);

								if (this.ins_id != 0) {
									strsql.append(" and " + srctab + ".ins_id=");
									strsql.append(this.ins_id);
								}
								if (operationtype != 0) {
									strsql.append(
											" and lower(" + srctab + ".basepre)='" + srcbase.toLowerCase() + "' ");
								}

								break;
							}
						}
						break;
					case 'B':// 单位信息
					case 'b':
						break;
					case 'K':// 职位信息
					case 'k':
						break;
					}
					if (strsql.length() == 0) {
						continue;
					}

					dao.update(strsql.toString());
					if (blinkupdate) {
						linkUpdatePreRec(setname, srcbase + setname, a0100, i9999);
					}
				} // while loop end.
				if (!bSumitedSubArea) {
					if (noper == 3) {
						subSetChangeSubmit(srcbase, srctab, a0100, desA0100);
					} else {
						subSetChangeSubmit(srcbase, srctab, a0100);
					}
				}

			}
			/** 清空消息库的记录 */
			if (state == 1) {
				strsql.setLength(0);
				strsql.append("delete from tmessage where noticetempid=?");
				strsql.append(" and a0100=?");
				strsql.append(" and db_type=?");
				ArrayList paralist = new ArrayList();
				paralist.add(Integer.valueOf(this.tabid));
				paralist.add(a0100);
				paralist.add(srcbase);
				dao.update(strsql.toString(), paralist);
			}
			/** 人员调入保存上传的照片 */
			if (!this.isRepreateSubmit) {
				if (noper == 3 || this.operationtype == 10) {
					if (this.operationtype == 10) {
						submitPicture(a0100, srcbase, srctab, a0100, srcbase + "A00");
					} else {
						submitPicture(a0100, dest_base, srctab, desA0100, srcbase + "A00");
					}
				}
				// 目标库不为空的情况，如果上传了图片，需要更新图片信息
				else if (!(this.dest_base == null || "".equals(this.dest_base))) {
					submitPicture(a0100, srcbase, srctab, a0100, srcbase + "A00");
				}
			}
			/** 对提交数据作标志 */
			/** 移库操作 */
			if (noper == 2) {
				DbNameBo dbbo = new DbNameBo(this.conn, this.userview);
				desA0100 = dbbo.moveDataBetweenBase2(a0100, srcbase, dest_base, this.move_person);
			}
			return desA0100;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			PubFunc.closeDbObj(rowSet);
		}
	}



	/**
	 * 判断子集指标变化后的值是否和库中最后一条记录的值相同，如果相同，则不操作
	 * @param subctrl
	 * @param destab
	 * @param strctab
	 * @param src_updateType
	 * @return
	 */
	private int getUpdateTypeBySubctrlBK(TSubsetCtrl subctrl,String destab,String strctab,int src_updateType,String keyValue)
	{
		int updateType=src_updateType;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
        ArrayList fieldlist=subctrl.getFieldlist();
			if(fieldlist.size()==0) {
                return updateType;
            }
			StringBuffer sql=new StringBuffer("select count(*) from ");
			StringBuffer sqlnull=new StringBuffer("select count(*) from ");
			StringBuffer sql1=new StringBuffer("");
			StringBuffer sql2=new StringBuffer("");
			StringBuffer sql3=new StringBuffer("");
StringBuffer sql4=new StringBuffer("");
			boolean flag=false;
			FieldItem item=null;
			for(int i=0;i<fieldlist.size();i++)
			{
				String field_name=(String)fieldlist.get(i);
			//	if(field_name.indexOf("t_")!=-1)
				if(field_name.length()>2&& "t_".equalsIgnoreCase(field_name.trim().substring(0,2))) {
                    continue;
                }
				item=DataDictionary.getFieldItem(field_name.toLowerCase());
				if(item==null/*&&item.getItemtype().equalsIgnoreCase("M")*/) {
                    continue;
                }
				if("M".equalsIgnoreCase(item.getItemtype())){
					sql1.append(","+field_name+"_2 "+field_name);
					sql2.append(",a."+field_name);
					int itemlength = item.getItemlength();
					int maxLength = 500;//目前讨论的结果是如果指标长度超过500 就截出前500个字后面的不在做判断,如果相同就认为相同
					if(itemlength>maxLength&&Sql_switcher.searchDbServer()==Constant.ORACEL){
						StringBuffer sql3_betwwen_aa = new StringBuffer("");
						StringBuffer sql3_betwwen_bb = new StringBuffer("");
                            StringBuffer sql4_betwwen = new StringBuffer("");
						sql3_betwwen_aa.append(Sql_switcher.sqlToChar(Sql_switcher.substr("aa."+field_name, "1", maxLength+"")));
						sql3_betwwen_bb.append(Sql_switcher.sqlToChar(Sql_switcher.substr("bb."+field_name, "1", maxLength+"")));
						sql3.append(" and "+Sql_switcher.isnull(sql3_betwwen_aa.toString(), "' '")+"="+Sql_switcher.isnull(sql3_betwwen_bb.toString(), "' '"));
						sql4_betwwen.append(Sql_switcher.sqlToChar(Sql_switcher.substr(field_name+"_2", "1", maxLength+"")));
						sql4.append(" and ("+sql4_betwwen.toString()+" is null or "+sql4_betwwen.toString()+"='' )");
					}else{
                    sql3.append(" and "+Sql_switcher.isnull(Sql_switcher.sqlToChar(" aa."+field_name), "' '")+"="+Sql_switcher.isnull(Sql_switcher.sqlToChar(" bb."+field_name), "' '"));
						sql4.append(" and ("+Sql_switcher.sqlToChar(field_name+"_2")+" is null or "+Sql_switcher.sqlToChar(field_name+"_2")+"='' )");
					}
				}
				else if("D".equalsIgnoreCase(item.getItemtype()))
				{
					sql1.append(","+field_name+"_2 "+field_name);
					sql2.append(",a."+field_name);
					sql3.append(" and "+Sql_switcher.isnull(Sql_switcher.dateToChar("aa."+field_name,"yyyy-MM-dd"),"' '")+"="+Sql_switcher.isnull(Sql_switcher.dateToChar("bb."+field_name,"yyyy-MM-dd"), "' '"));
					sql4.append(" and ( "+field_name+"_2 is null )");
				}
				else if("N".equalsIgnoreCase(item.getItemtype()))
{
				sql1.append(","+field_name+"_2 "+field_name);
					sql2.append(",a."+field_name);
					sql3.append(" and "+Sql_switcher.isnull("aa."+field_name, "0")+"="+Sql_switcher.isnull("bb."+field_name, "0"));
				//	sql4.append(" and ("+Sql_switcher.isnull(field_name+"_2 ", "0")+"=0 or "+Sql_switcher.isnull(field_name+"_2 ", "0")+"=0 )");
					sql4.append(" and ( "+field_name+"_2 is null )");
				}
				else{
						sql1.append(","+field_name+"_2 "+field_name);
						sql2.append(",a."+field_name);
                    sql3.append(" and "+Sql_switcher.isnull("aa."+field_name, "' '")+"="+Sql_switcher.isnull("bb."+field_name, "' '"));
//		sql4.append(" and ("+Sql_switcher.isnull(field_name+"_2 ", "' '")+"=' ' or "+Sql_switcher.isnull(field_name+"_2 ", "' '")+"='' )");
						sql4.append(" and ( nullif("+field_name+"_2,'') is null)");
					}
				flag=true;
			}
			if(flag)
			{
				String keyField="b0110";
				if(this.infor_type==3) {
                    keyField="e01a1";
                }

				if(strctab.equalsIgnoreCase("g_templet_"+this.tabid)) {
                    sql.append("(select "+sql1.substring(1)+" from "+strctab+" where  "+keyField+"='"+keyValue+"'   ) aa,");
                } else if(strctab.equalsIgnoreCase("templet_"+this.tabid)) {
                    sql.append("(select "+sql1.substring(1)+" from "+strctab+" where ins_id="+this.ins_id+" and "+keyField+"='"+keyValue+"'  ) aa,");
                } else {
                    sql.append("(select "+sql1.substring(1)+" from "+strctab+" where  "+keyField+"='"+keyValue+"' ) aa,");
                }
				sql.append("(select "+sql2.substring(1)+" from "+destab+" a where a.i9999=(select max(b.i9999) from "+destab+" b where ");
				sql.append(" b."+keyField+"=a."+keyField+" and b."+keyField+"='"+keyValue+"' ) and "+keyField+"='"+keyValue+"' ) bb ");
				sql.append(" where "+sql3.substring(4));

    RowSet rowSet=dao.search(sql.toString());
    if(rowSet.next())
    {
        if(rowSet.getInt(1)==1) {
                        updateType=0;
     } else{
						//没填值，也不增 （新加一种不增记录的情况）
						sqlnull.append(" ( ");
                    if(strctab.equalsIgnoreCase("g_templet_"+this.tabid)) {
                            sqlnull.append("select "+sql1.substring(1)+" from "+strctab+" where  "+keyField+"='"+keyValue+"'   ");
                        } else if(strctab.equalsIgnoreCase("templet_"+this.tabid)) {
                            sqlnull.append("select "+sql1.substring(1)+" from "+strctab+" where ins_id="+this.ins_id+" and "+keyField+"='"+keyValue+"'  ");
                        } else {
                            sqlnull.append("select "+sql1.substring(1)+" from "+strctab+" where  "+keyField+"='"+keyValue+"' ");
                        }
						sqlnull.append(""+sql4);
						sqlnull.append(" ) a ");
rowSet=dao.search(sqlnull.toString());
				if(rowSet.next())
						{
							if(rowSet.getInt(1)==1) {
                                updateType=0;
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
		return updateType;
	}

	/**
	 * 判断子集指标变化后的值是否和库中最后一条记录的值相同，如果相同，则不操作
	 * @param subctrl
	 * @param destab
	 * @param strctab
	 * @param src_updateType
	 * @return
	 */
	private int getUpdateTypeBySubctrl(TSubsetCtrl subctrl,String destab,String strctab,int src_updateType,String a0100,String srcbase)
	{
		int updateType=src_updateType;
		try
		{
ContentDAO dao=new ContentDAO(this.conn);
			ArrayList fieldlist=subctrl.getFieldlist();
if(fieldlist.size()==0) {
                return updateType;
            }
			StringBuffer sql=new StringBuffer("select count(*) from ");
StringBuffer sqlnull=new StringBuffer("select count(*) from ");

			StringBuffer sql1=new StringBuffer("");
			StringBuffer sql2=new StringBuffer("");
			StringBuffer sql3=new StringBuffer("");
			StringBuffer sql4=new StringBuffer("");
			boolean flag=false;
			FieldItem item=null;
			for(int i=0;i<fieldlist.size();i++)
			{
				String field_name=(String)fieldlist.get(i);
			//	if(field_name.indexOf("t_")!=-1)
if(field_name.length()>2&& "t_".equalsIgnoreCase(field_name.trim().substring(0,2))) {
        continue;
                }
				item=DataDictionary.getFieldItem(field_name.toLowerCase());
				if(item==null/*&&item.getItemtype().equalsIgnoreCase("M")*/) {
                    continue;
                }
				if("M".equalsIgnoreCase(item.getItemtype()))
				{
					sql1.append(","+field_name+"_2 "+field_name);
					sql2.append(",a."+field_name);
					int itemlength = item.getItemlength();
					int maxLength = 500;//目前讨论的结果是如果指标长度超过500 就截出前500个字后面的不在做判断,如果相同就认为相同
					if(itemlength>maxLength&&Sql_switcher.searchDbServer()==Constant.ORACEL){
            StringBuffer sql3_betwwen_aa = new StringBuffer("");
            StringBuffer sql3_betwwen_bb = new StringBuffer("");
StringBuffer sql4_betwwen = new StringBuffer("");
						sql3_betwwen_aa.append(Sql_switcher.sqlToChar(Sql_switcher.substr("aa."+field_name, "1", maxLength+"")));
						sql3_betwwen_bb.append(Sql_switcher.sqlToChar(Sql_switcher.substr("bb."+field_name, "1", maxLength+"")));
						sql3.append(" and "+Sql_switcher.isnull(sql3_betwwen_aa.toString(), "' '")+"="+Sql_switcher.isnull(sql3_betwwen_bb.toString(), "' '"));
						sql4_betwwen.append(Sql_switcher.sqlToChar(Sql_switcher.substr(field_name+"_2", "1", maxLength+"")));
						sql4.append(" and ("+sql4_betwwen.toString()+" is null or "+sql4_betwwen.toString()+"='' )");
					}else{
						sql3.append(" and "+Sql_switcher.isnull(Sql_switcher.sqlToChar(" aa."+field_name), "' '")+"="+Sql_switcher.isnull(Sql_switcher.sqlToChar(" bb."+field_name), "' '"));
						sql4.append(" and ("+Sql_switcher.sqlToChar(field_name+"_2")+" is null or "+Sql_switcher.sqlToChar(field_name+"_2")+"='' )");
					}
				}
				else if("D".equalsIgnoreCase(item.getItemtype()))
				{
					sql1.append(","+field_name+"_2 "+field_name);
					sql2.append(",a."+field_name);
					sql3.append(" and "+Sql_switcher.isnull(Sql_switcher.dateToChar("aa."+field_name,"yyyy-MM-dd"),"' '")+"="+Sql_switcher.isnull(Sql_switcher.dateToChar("bb."+field_name,"yyyy-MM-dd"), "' '"));
					sql4.append(" and ( "+field_name+"_2 is null )");
				}
				else if("N".equalsIgnoreCase(item.getItemtype()))
				{
					sql1.append(","+field_name+"_2 "+field_name);
					sql2.append(",a."+field_name);
					sql3.append(" and "+Sql_switcher.isnull("aa."+field_name, "0")+"="+Sql_switcher.isnull("bb."+field_name, "0"));
				//	sql4.append(" and ("+Sql_switcher.isnull(field_name+"_2 ", "0")+"=0 or "+Sql_switcher.isnull(field_name+"_2 ", "0")+"=0 )");
					sql4.append(" and ( "+field_name+"_2 is null )");
				}else
				{
					sql1.append(","+field_name+"_2 "+field_name);
					sql2.append(",a."+field_name);
					sql3.append(" and "+Sql_switcher.isnull("aa."+field_name, "' '")+"="+Sql_switcher.isnull("bb."+field_name, "' '"));
					sql4.append(" and ( nullif("+field_name+"_2,'') is null)");
				}
				flag=true;
			}
			if(flag)
			{
				if(strctab.equalsIgnoreCase("g_templet_"+this.tabid)) {
                    sql.append("(select "+sql1.substring(1)+" from "+strctab+" where  a0100='"+a0100+"' and lower(basepre)='"+srcbase.toLowerCase()+"' ) aa,");
                } else if(strctab.equalsIgnoreCase("templet_"+this.tabid)) {
                    sql.append("(select "+sql1.substring(1)+" from "+strctab+" where ins_id="+this.ins_id+" and a0100='"+a0100+"' and lower(basepre)='"+srcbase.toLowerCase()+"'  ) aa,");
                } else {
                    sql.append("(select "+sql1.substring(1)+" from "+strctab+" where  a0100='"+a0100+"' and lower(basepre)='"+srcbase.toLowerCase()+"' ) aa,");
                }
				sql.append("(select "+sql2.substring(1)+" from "+destab+" a where a.i9999=(select max(b.i9999) from "+destab+" b where ");
				sql.append(" b.a0100=a.a0100 and b.a0100='"+a0100+"' ) and a0100='"+a0100+"' ) bb ");
				sql.append(" where "+sql3.substring(4));

				RowSet rowSet=dao.search(sql.toString());
				if(rowSet.next())
				{
					if(rowSet.getInt(1)==1) {
                        updateType=0;
                    } else if(!(updateType==0||updateType==2)){//bug 32613 当子集设置为更新当前记录或不更新当前记录时不再判断用户填写的值是否为空。
					//没填值，也不增 （新加一种不增记录的情况）

						sqlnull.append(" ( ");
						if(strctab.equalsIgnoreCase("g_templet_"+this.tabid)) {
                            sqlnull.append("select "+sql1.substring(1)+" from "+strctab+" where  a0100='"+a0100+"' and lower(basepre)='"+srcbase.toLowerCase()+"' ");
                        } else if(strctab.equalsIgnoreCase("templet_"+this.tabid)) {
                            sqlnull.append("select "+sql1.substring(1)+" from "+strctab+" where ins_id="+this.ins_id+" and a0100='"+a0100+"' and lower(basepre)='"+srcbase.toLowerCase()+"'  ");
                        } else {
                            sqlnull.append("select "+sql1.substring(1)+" from "+strctab+" where  a0100='"+a0100+"' and lower(basepre)='"+srcbase.toLowerCase()+"' ");
                        }
						sqlnull.append(""+sql4);
						sqlnull.append(" ) a ");
						rowSet=dao.search(sqlnull.toString());
						if(rowSet.next())
						{
							if(rowSet.getInt(1)==1) {
                                updateType=0;
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
		return updateType;
	}
	  /**
     *
     * @Title: getUpdateTypeBySubctrlCallIn
     * @Description: 调入型模版判断子集指标是否增加子集记录
     * @param subctrl
     * @param destab
     * @param strctab
     * @param src_updateType
     * @param a0100
     * @param srcbase
     * @return int
     * @throws
     */
    public int getUpdateTypeBySubctrlCallIn(TSubsetCtrl subctrl,String destab,String strctab,int src_updateType,String a0100,String srcbase){

        int updateType=src_updateType;
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            ArrayList fieldlist=subctrl.getFieldlist();
            if(fieldlist.size()==0) {
                return updateType;
            }
            StringBuffer sqlnull=new StringBuffer("select count(*) from ");

            StringBuffer sql1=new StringBuffer("");
            StringBuffer sql4=new StringBuffer("");
            boolean flag=false;
            FieldItem item=null;
            for(int i=0;i<fieldlist.size();i++)
            {
                String field_name=(String)fieldlist.get(i);
            //  if(field_name.indexOf("t_")!=-1)
                if(field_name.length()>2&& "t_".equalsIgnoreCase(field_name.trim().substring(0,2))) {
                    continue;
                }
                item=DataDictionary.getFieldItem(field_name.toLowerCase());
                if(item==null/*&&item.getItemtype().equalsIgnoreCase("M")*/) {
                    continue;
                }
                if("M".equalsIgnoreCase(item.getItemtype()))
                {
                	sql1.append(","+field_name+"_2 "+field_name);
					int itemlength = item.getItemlength();
					int maxLength = 500;//目前讨论的结果是如果指标长度超过500 就截出前500个字后面的不在做判断,如果相同就认为相同
					if(itemlength>maxLength&&Sql_switcher.searchDbServer()==Constant.ORACEL){
   StringBuffer sql4_betwwen = new StringBuffer("");
						sql4_betwwen.append(Sql_switcher.sqlToChar(Sql_switcher.substr(field_name+"_2", "1", maxLength+"")));
						sql4.append(" and ("+sql4_betwwen.toString()+" is null or "+sql4_betwwen.toString()+"='' )");
					}else{
						sql4.append(" and ("+Sql_switcher.sqlToChar(field_name+"_2")+" is null or "+Sql_switcher.sqlToChar(field_name+"_2")+"='' )");
					}
                }
                else if("D".equalsIgnoreCase(item.getItemtype()))
                {
                    sql1.append(","+field_name+"_2 "+field_name);
                    sql4.append(" and ( "+field_name+"_2 is null )");
                }
                else if("N".equalsIgnoreCase(item.getItemtype()))
                {
sql1.append(","+field_name+"_2 "+field_name);
    sql4.append(" and ( "+field_name+"_2 is null )");
    }else
                {
                    sql1.append(","+field_name+"_2 "+field_name);
sql4.append(" and ( nullif("+field_name+"_2,'') is null)");
                }
                flag=true;
            }
            if(flag)
            {
                sqlnull.append(" ( ");
                if(strctab.equalsIgnoreCase("g_templet_"+this.tabid)) {
                    sqlnull.append("select "+sql1.substring(1)+" from "+strctab+" where  a0100='"+a0100+"' and lower(basepre)='"+srcbase.toLowerCase()+"' ");
                } else if(strctab.equalsIgnoreCase("templet_"+this.tabid)) {
                    sqlnull.append("select "+sql1.substring(1)+" from "+strctab+" where ins_id="+this.ins_id+" and a0100='"+a0100+"' and lower(basepre)='"+srcbase.toLowerCase()+"'  ");
} else {
               sqlnull.append("select "+sql1.substring(1)+" from "+strctab+" where  a0100='"+a0100+"' and lower(basepre)='"+srcbase.toLowerCase()+"' ");
                }
                sqlnull.append(""+sql4);
                sqlnull.append(" ) a ");
                RowSet rowSet=dao.search(sqlnull.toString());
             if(rowSet.next())
                {
                    if(rowSet.getInt(1)==1) {
                        updateType=0;
                    }
                }
                PubFunc.closeDbObj(rowSet);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return updateType;

    }
	/**
	 * 关联更新上条记录
	 * @param fields
	 * @param srcbase
	 * @param a0100
	 * @param curri999
	 */
	private void linkUpdatePreRec(String setname,String tablename,String keyvalue,int curri9999)
	{
		Iterator iterator=linkmap.entrySet().iterator();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			String keyField="a0100";
			if(this.infor_type==2) {
                keyField="b0110";
            } else if(this.infor_type==3) {
                keyField="e01a1";
}

Calendar calendar=Calendar.getInstance();
			while(iterator.hasNext())
			{
				Entry entry=(Entry)iterator.next();
				String dest_field=((String)entry.getKey()).toLowerCase();
				String src_field=((String)entry.getValue()).toLowerCase();
				FieldItem item=DataDictionary.getFieldItem(dest_field,setname);
				if(item!=null)
				{
					RecordVo vo=new RecordVo(tablename);
					vo.setString(keyField, keyvalue);
					vo.setInt("i9999", curri9999);
					vo=dao.findByPrimaryKey(vo);
					RecordVo dest_vo=new RecordVo(tablename);
					dest_vo.setString(keyField, keyvalue);
					dest_vo.setInt("i9999", curri9999-1);

					Date d=vo.getDate(src_field.toLowerCase());
					calendar.setTime(d);
					if(linkdateflagmap!=null&&linkdateflagmap.get(dest_field)!=null&&"1".equals(linkdateflagmap.get(dest_field))){
					calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH));//日期相同
					}else{
					calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)-1);//让日期减1
					}


					dest_vo.setDate(dest_field.toLowerCase(),calendar.getTime());
					dao.updateValueObject(dest_vo);
				}
			}
		}
		catch(Exception ex)
		{
			//ex.printStackTrace();
		}
	}



	/**
	 * 消息库中是否存在对此模板的消息
	 * @state =0(未用)=1(正在处理)=2(处理完)
	 * @return
	 */
	private boolean isHaveMsg(int state)
	{
		boolean bflag=false;
		try
		{
			StringBuffer buf=new StringBuffer();
			DbWizard dbw = new DbWizard(this.conn);
			buf.append("select count(*) as nmax from tmessage where state="+state+" and noticetempid=");
			buf.append(this.tabid);
			if(!this.userview.isSuper_admin()&& "1".equals(this.filter_by_manage_priv))
			{
				String operOrg = userview.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板
				if(operOrg==null||!"UN`".equalsIgnoreCase(operOrg))
				{
					buf.append(" and ( ");
					if(operOrg!=null && operOrg.length() >3)
					{
						StringBuffer tempSql = new StringBuffer("");
						String[] temp = operOrg.split("`");
						for (int j = 0; j < temp.length; j++) {
							 if (temp[j]!=null&&temp[j].length()>0) {
        tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2)+ "%'");
}
}
if(tempSql.length()>0)
						{
							buf.append(tempSql.substring(3));
						}
						else {
                            buf.append(" tmessage.b0110='##'");
                        }
}
else {
             buf.append(" tmessage.b0110='##'");
                    }

buf.append(" or nullif(tmessage.b0110,'') is null)");
				}
			}
			if(dbw.isExistField("tmessage", "receivetype", false)){
				buf.append(" and (nullif(username,'') is null or (lower(username)='"+userview.getUserName().toLowerCase()+"' and (receivetype='4' or nullif(receivetype,'') is null)) ");
				if(this.getRoleArr(userview).length()>0) {
                    buf.append(" or (username in("+this.getRoleArr(userview)+") and receivetype='2'))");
   } else {
                    buf.append(" )");
                }
}else {
buf.append(" and ( nullif(username,'') is null  or lower(username)='"+userview.getUserName().toLowerCase()+"')");
            }
			//Object_type	 对象类型	Int	   1:人员  2:单位  3：职位
			if(this._static==10) //单位管理
			{
				buf.append(" and object_type=2 ");
			}
			else if(this._static==11) //职位管理
        {
            buf.append(" and object_type=3 ");
			}
			else
			{
				buf.append(" and ( object_type is null or object_type=1 ) ");
			}
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			int nrec=0;
			if(rset.next()) {
                nrec=rset.getInt("nmax");
            }
			if(nrec!=0) {
                bflag=true;
            }
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bflag;
	}

	/**
	 * 消息库中是否存在对此模板的消息
	 * @return
	 */
	private boolean isHaveMsg()
	{
		boolean bflag=false;
		try
		{
			StringBuffer buf=new StringBuffer();
			DbWizard dbw = new DbWizard(this.conn);
			buf.append("select count(*) as nmax from tmessage where state=0 and noticetempid=");
			buf.append(this.tabid);
			if(!this.userview.isSuper_admin()&& "1".equals(this.filter_by_manage_priv))
			{
				/*
				buf.append(" and (tmessage.b0110 like '");
				if((this.userview.getManagePrivCodeValue()==null||this.userview.getManagePrivCodeValue().trim().length()==0)&&this.userview.getManagePrivCode().length()==0)
					buf.append("##");
				else
					buf.append(this.userview.getManagePrivCodeValue());
				buf.append("%' or tmessage.b0110 is null or tmessage.b0110='')");*/

				String operOrg = userview.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板
				if(operOrg==null||!"UN`".equalsIgnoreCase(operOrg))
				{
					buf.append(" and ( ");
					if(operOrg!=null && operOrg.length() >3)
					{
						StringBuffer tempSql = new StringBuffer("");
						String[] temp = operOrg.split("`");
						for (int j = 0; j < temp.length; j++) {
							 if (temp[j]!=null&&temp[j].length()>0) {
                                 tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2)+ "%'");
                             }
						}
						if(tempSql.length()>0)
						{
							buf.append(tempSql.substring(3));
						}
						else {
                            buf.append(" tmessage.b0110='##'");
                        }
					}
					else {
                        buf.append(" tmessage.b0110='##'");
                    }

					buf.append(" or nullif(tmessage.b0110,'') is null)");
				}
			}
			if(dbw.isExistField("tmessage", "receivetype", false)){
				buf.append(" and (nullif(username,'') is null or (lower(username)='"+userview.getUserName().toLowerCase()+"' and (receivetype='4' or nullif(receivetype,'') is null)) ");
				if(this.getRoleArr(userview).length()>0) {
                    buf.append(" or (username in("+this.getRoleArr(userview)+") and receivetype='2'))");
                } else {
                    buf.append(" )");
                }
			}else {
                buf.append(" and ( nullif(username,'') is null  or lower(username)='"+this.userview.getUserName().toLowerCase()+"')");
}
//Object_type	 对象类型	Int	   1:人员  2:单位  3：职位
			if(this._static==10) //单位管理
			{
				buf.append(" and object_type=2 ");
			}
			else if(this._static==11) //职位管理
			{
				buf.append(" and object_type=3 ");
			}
			else
			{
				buf.append(" and ( object_type is null or object_type=1 ) ");
			}

			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			int nrec=0;
			if(rset.next()) {
                nrec=rset.getInt("nmax");
            }
			if(nrec!=0) {
                bflag=true;
            }
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
    ex.printStackTrace();
}
return bflag;
	}
	/**
	 * 根据消息库，更新对应的数据
	 * @param changelast
* @param changepre
* @param tabname
	 * @return
*/
private String getChgUpdateSQL(String changelast,String changepre,String tabname)
	{
		StringBuffer buf=new StringBuffer();
		RecordVo vo=new RecordVo(tabname);
		if(changelast==null) {
            changelast="";
        }
		String[] chglastarr=StringUtils.split(changelast,",");
		int idx=0;
		buf.append("state=1");
		HashMap existFieldMap=new HashMap();
		for(int i=0;i<chglastarr.length;i++)
		{
			String expr=chglastarr[i];
			expr = expr.replace("，", ",");
			idx=expr.indexOf("=");
			if(idx==-1) {
                continue;
            }

			String fieldname=expr.substring(0,idx);
			String value=expr.substring(idx+1);
			FieldItem fielditem=DataDictionary.getFieldItem(fieldname);
			if(fielditem==null) {
                continue;
            }
			if("0".equals(this.import_notice_data)) {
                fieldname=fieldname+"_1";
            } else {
                fieldname=fieldname+"_2";
            }
			if(vo.hasAttribute(fieldname.toLowerCase()))
			{
				if(existFieldMap.get(fieldname.toLowerCase())==null)
				{
					existFieldMap.put(fieldname.toLowerCase(),"1");
					buf.append(",");
					buf.append(fieldname);
					buf.append("=");
					if(fielditem.isFloat()||fielditem.isInt())
					{
						if(value.length()==0) {
                            buf.append("null");
                        } else {
                            buf.append(value);
                        }
					}
					else if(fielditem.isDate())
					{
						value=value.replaceAll("/","-");
						value=Sql_switcher.dateValue(value);
						buf.append(value);
					}
					else//if(fielditem.isChar())
					{
                            buf.append("'");
buf.append(value);
                                    buf.append("'");
					}
				}
			}
		}
		return buf.toString();
	}

	/**
	 * 审批流程中的单据也需同步通知单的 数据
	 * @param tabname
	 * @param where_str
	 */
	private void impPreDataFromMessageByInsid(String tabname,String where_str)
	{
		try
		{
			    ContentDAO dao=new ContentDAO(this.conn);
			    StringBuffer buf=new StringBuffer();
				buf.append("select count(*) as nmax from tmessage where state=1 and noticetempid="+this.tabid);
				//Object_type	 对象类型	Int	   1:人员  2:单位  3：职位
				if(this._static==10) //单位管理
                {
                    buf.append(" and object_type=2 ");
                } else if(this._static==11) //职位管理
                {
                    buf.append(" and object_type=3 ");
                } else {
                    buf.append(" and ( object_type is null or object_type=1 ) ");
                }
				RowSet rset=dao.search(buf.toString());
				int nrec=0;
				if(rset.next()) {
                    nrec=rset.getInt("nmax");
                }
				if(nrec>0)
				{
					StringBuffer sql=new StringBuffer("select "+tabname+".*,M.changepre from "+tabname+" ,tmessage M where ");
					if(this.infor_type==1) {
                        sql.append(" lower(M.db_type)=lower("+tabname+".basepre) and "+tabname+".a0100=M.a0100 ");
                    } else if(this.infor_type==2) {
                        sql.append("   "+tabname+".B0110=M.B0110 ");
                    } else if(this.infor_type==3) {
                        sql.append("   "+tabname+".E01A1=M.B0110 ");
                    }
					sql.append(" and M.state=1 and M.noticetempid="+this.tabid);
					if(this._static==10) //单位管理
                    {
                        sql.append(" and object_type=2 ");
                    } else if(this._static==11) //职位管理
                    {
                        sql.append(" and object_type=3 ");
                    } else {
               sql.append(" and ( object_type is null or object_type=1 ) ");
                    }
					sql.append(" and "+tabname+".state=1 ");//来源于消息
					if(where_str.length()>0) {
                        sql.append(" and "+where_str);
                    }
					RowSet rowSet=dao.search(sql.toString());
                        ArrayList paralist=new ArrayList();
					while(rowSet.next())
					{
						String basepre="";
						String a0100="";
						String b0110="";
						if(this.infor_type==1)
						{
							basepre=rowSet.getString("basepre");
							a0100=rowSet.getString("a0100");
						}
						else
						{
							if(this.infor_type==2) {
                                b0110=rowSet.getString("b0110");
                            } else if(this.infor_type==3) {
                                b0110=rowSet.getString("e01a1");
                            }
						}
						String changepre=Sql_switcher.readMemo(rowSet, "changepre");

						sql.setLength(0);
						paralist.clear();
						sql.append("update ");
						sql.append(tabname);
						sql.append(" set ");

						String up_str=getChgUpdateSQL(changepre,tabname);
                                if(up_str.length()==0) {
                            continue;
                        }
						sql.append(up_str.substring(1));
						//如果更新的值中存在问号，对预处理的参数传递会有影响，执行sql会无法获取准确的参数位置导致sql执行出错。
						if(this.infor_type==1) {
                            sql.append(" where a0100='"+a0100+"' and lower(basepre)='"+basepre.toLowerCase()+"' ");
                        } else if(this.infor_type==2) {
                            sql.append(" where b0110='"+b0110+"' ");
                        } else if(this.infor_type==3) {
                            sql.append(" where e01a1='"+b0110+"' ");
                        }
                            sql.append(" and state=1 ");
						if(where_str.length()>0) {
                            sql.append(" and "+where_str);
                        }
						/*if(this.infor_type==1)
						{
							paralist.add(a0100);
							paralist.add(basepre.toLowerCase());
						}
						else if(this.infor_type==2||this.infor_type==3)
						{
							paralist.add(b0110);
						}*/
						dao.update(sql.toString());
					}

				}
				PubFunc.closeDbObj(rset);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}


	/**
	 * 同步来自消息的变换前指标数据
	 * @param tabname
	 * @param where_str
	 * @throws GeneralException
	 */
	private void impPreDataFromMessage(String tabname,String where_str)throws GeneralException
	{
		 try
		 {
			    ContentDAO dao=new ContentDAO(this.conn);
    if("templet_".equalsIgnoreCase(tabname.substring(0,8))&&where_str.trim().length()>0) //20150425 DENGCAN 审批流程中的单据也需同步通知单的 数据(汉口银行)
			    {
			    	impPreDataFromMessageByInsid(tabname,where_str);
			    }
			    else
			    {
					if(isHaveMsg(1))
					{
						StringBuffer sql=new StringBuffer("select "+tabname+".*,M.changepre from "+tabname+" ,tmessage M where ");
						if(this.infor_type==1) {
                            sql.append(" lower(M.db_type)=lower("+tabname+".basepre) and "+tabname+".a0100=M.a0100 ");
                        } else if(this.infor_type==2) {
                            sql.append("   "+tabname+".B0110=M.B0110 ");
                        } else if(this.infor_type==3) {
                            sql.append("   "+tabname+".E01A1=M.B0110 ");
                        }
						sql.append(" and M.state=1 and M.noticetempid="+this.tabid);
						if(!this.userview.isSuper_admin()&& "1".equals(this.filter_by_manage_priv))
						{
                                        String operOrg = this.userview.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板
								if(operOrg==null||!"UN`".equalsIgnoreCase(operOrg))
								{
									sql.append(" and ( ");

									if(operOrg!=null && operOrg.length() >3)
									{
										StringBuffer tempSql = new StringBuffer("");
										String[] temp = operOrg.split("`");
										for (int j = 0; j < temp.length; j++) {
											 if (temp[j]!=null&&temp[j].length()>0) {
                                                 tempSql.append(" or  M.b0110 like '" + temp[j].substring(2)+ "%'");
                                             }
										}
										if(tempSql.length()>0)
										{
											sql.append(tempSql.substring(3));
										}
										else {
                                            sql.append(" M.b0110='##'");
                                        }
									}
									else {
                                        sql.append(" M.b0110='##'");
                                    }

									sql.append(" or nullif(M.b0110,'') is null)");
								}

						}
						sql.append(" and (M.username is null or M.username='' or lower(M.username)='"+this.userview.getUserName().toLowerCase()+"')");
						if(this._static==10) //单位管理
                        {
                            sql.append(" and object_type=2 ");
                        } else if(this._static==11) //职位管理
                        {
                            sql.append(" and object_type=3 ");
                        } else {
                            sql.append(" and ( object_type is null or object_type=1 ) ");
                        }
						sql.append(" and "+tabname+".state=1 ");//来源于消息
						if(where_str.length()>0) {
                            sql.append(" and "+where_str);
                        }
						RowSet rowSet=dao.search(sql.toString());
                            ArrayList paralist=new ArrayList();
                            while(rowSet.next())
                            {
                                String basepre="";
                                String a0100="";
							String b0110="";
							if(this.infor_type==1)
                                    {
                                        basepre=rowSet.getString("basepre");
                                        a0100=rowSet.getString("a0100");
                                    }
                                    else
							{
								if(this.infor_type==2) {
                                    b0110=rowSet.getString("b0110");
                                } else if(this.infor_type==3) {
                  b0110=rowSet.getString("e01a1");
}
						}
							String changepre=Sql_switcher.readMemo(rowSet, "changepre");

							sql.setLength(0);
							paralist.clear();
							sql.append("update ");
							sql.append(tabname);
							sql.append(" set ");

							String up_str=getChgUpdateSQL(changepre,tabname);
							up_str= up_str.replace("?", "");
							if(up_str.length()==0) {
                                continue;
                            }
							sql.append(up_str.substring(1));
							//如果更新的值中存在问号，对预处理的参数传递会有影响，执行sql会无法获取准确的参数位置导致sql执行出错。
							if(this.infor_type==1) {
                                sql.append(" where a0100='"+a0100+"' and lower(basepre)='"+basepre.toLowerCase()+"' ");
                            } else if(this.infor_type==2) {
                                sql.append(" where b0110='"+b0110+"' ");
                            } else if(this.infor_type==3) {
                                sql.append(" where e01a1='"+b0110+"' ");
                   }
							sql.append(" and state=1 ");
							if(where_str.length()>0) {
                                sql.append(" and "+where_str);
                            }
							/*if(this.infor_type==1)
							{
								paralist.add(a0100);
								paralist.add(basepre.toLowerCase());
							}
							else if(this.infor_type==2||this.infor_type==3)
							{
								paralist.add(b0110);
							}*/
							dao.update(sql.toString());
						}
						PubFunc.closeDbObj(rowSet);
					}
			    }
		 }
		 catch(Exception ex)
		 {
			  ex.printStackTrace();
			  throw GeneralExceptionHandler.Handle(ex);
		 }

	}

	/**
	 * 根据消息库，更新对应的数据
	 * @param changepre
	 * @param tabname
	 * @return
	 */
	private String getChgUpdateSQL(String changepre,String tabname)
	{
		StringBuffer buf=new StringBuffer();
		RecordVo vo=new RecordVo(tabname);
		String[] chglastarr=StringUtils.split(changepre,",");
		int idx=0;

		HashMap map=new HashMap();
		for(int i=0;i<chglastarr.length;i++)
		{
			String expr=chglastarr[i];
			expr = expr.replace("，", ",");
			idx=expr.indexOf("=");
			if(idx==-1) {
                continue;
            }

			String fieldname=expr.substring(0,idx);
			String value=expr.substring(idx+1);
			FieldItem fielditem=DataDictionary.getFieldItem(fieldname);
			if(fielditem==null) {
                continue;
            }
			if("0".equals(this.import_notice_data)) {
                continue;
            }
			fieldname=fieldname+"_1";
			if(vo.hasAttribute(fieldname.toLowerCase()))
			{
				if(map.get(fieldname.toLowerCase())!=null)
				{
					continue;
				}
				map.put(fieldname.toLowerCase(),"1");

				buf.append(",");
				buf.append(fieldname);
				buf.append("=");
				if(this.field_name_map!=null&&this.field_name_map.get(fieldname.toLowerCase())!=null){
					if(fielditem.isFloat()||fielditem.isInt())
					{
						if(value.length()==0) {
buf.append("null");
        } else {
                            buf.append("'"+value+"'");
                        }
					}
					else if(fielditem.isDate())
					{
						value=value.replaceAll("/","-");
				//		value=Sql_switcher.dateValue(value);  //2013-11-20 dengc 当日期型字段为按条件取值，字段类型则为M
						buf.append("'"+value+"'");
					}
					else//if(fielditem.isChar())
					{
						buf.append("'");
						buf.append(value);
						buf.append("'");
					}
				}else{
				if(fielditem.isFloat()||fielditem.isInt())
				{
					if(value.length()==0) {
                        buf.append("null");
                    } else{
						if(value.getBytes().length>fielditem.getItemlength()){
							String str[] =  value.split("`");
							if(str[0].length()!=0&&str[0].getBytes().length<=fielditem.getItemlength()){
								buf.append(str[0]);
							}else {
                                buf.append("null");
                            }
						}else{
						buf.append(value);
						}
					}
				}
				else if(fielditem.isDate())
				{
					value=value.replaceAll("/","-");
					String _value=value;
					value=Sql_switcher.dateValue(value);
					if(_value.getBytes().length>fielditem.getItemlength()){
						String str[] =  _value.split("`");
						if(str[0].length()!=0&&str[0].replace("'", "").length()<=fielditem.getItemlength()){
							_value=Sql_switcher.dateValue(str[0]);
							buf.append(_value);
						}else {
                            buf.append("null");
                        }
					}else{
						buf.append(value);
					}
				}
				else//if(fielditem.isChar())
				{
					//
					buf.append("'");
					if(value.getBytes().length>fielditem.getItemlength()){
						String str[] =  value.split("`");
						if(str[0].length()!=0&&str[0].getBytes().length<=fielditem.getItemlength()){
							buf.append(str[0]);
						}else {
                            buf.append("");
                        }
					}else{
					buf.append(value);
					}
					buf.append("'");
				}
				}
			}
		}
		return buf.toString();
	}







	/**
	 * 从消息库导入数据
	 * @throws GeneralException
	 */
	private void impDataFromMessage(String tabname)throws GeneralException
	{
		if(isHaveMsg())
		{
		  try
		  {
		      //tmesage 增加两个字段receiver receive_date 用了查看谁抢单了 2015-01-15
            DbWizard dbw=new DbWizard(this.conn);
            if(!dbw.isExistField("tmessage","receiver",false))
            {
                Table table = new Table("tmessage");
                Field field=new Field("receiver","receiver");
                field.setDatatype(DataType.STRING);
                field.setLength(50);
                table.addField(field);

                Field field1=new Field("receive_date","Receive_date");
                field1.setDatatype(DataType.DATETIME);
                table.addField(field1);

                dbw.addColumns(table);
                DBMetaModel dbmodel=new DBMetaModel(this.conn);
                dbmodel.reloadTableModel("tmessage");
            }

			String a0100=null;
			String base_pre=null;
			String b0110=null;
			HashMap hm=new HashMap();
			ArrayList a0100list=null;

			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=null;
			StringBuffer buf=new StringBuffer();




			buf.append("select a0100,db_type from tmessage where state=0 and noticetempid=");
			if(this._static==10||this._static==11) //单位管理
			{
				buf.setLength(0);
				buf.append("select b0110 from tmessage where state=0 and noticetempid=");
			}
			buf.append(this.tabid);
			if(!this.userview.isSuper_admin()&& "1".equals(this.filter_by_manage_priv))
			{
				/*
				buf.append(" and (tmessage.b0110 like '");
				if((this.userview.getManagePrivCodeValue()==null||this.userview.getManagePrivCodeValue().trim().length()==0)&&this.userview.getManagePrivCode().length()==0)
					buf.append("##");
				else
					buf.append(this.userview.getManagePrivCodeValue());
				buf.append("%' or tmessage.b0110 is null or tmessage.b0110='')");*/
				String operOrg = userview.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板
				if(operOrg==null||!"UN`".equalsIgnoreCase(operOrg))
				{
					buf.append(" and ( ");
					if(operOrg!=null && operOrg.length() >3)
					{
						StringBuffer tempSql = new StringBuffer("");
						String[] temp = operOrg.split("`");
						for (int j = 0; j < temp.length; j++) {
							 if (temp[j]!=null&&temp[j].length()>0)
							 {
								 if("0".equalsIgnoreCase(this.include_suborg))//不包含下属单位
								 {
									 if("UN".equalsIgnoreCase(temp[j].substring(0,2)))
									 {
										 tempSql.append(" or  tmessage.b0110_self ='" + temp[j].substring(2)+ "'");
									 }
									 else {
                                         tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2)+ "%'");
                                     }
								 }
								 else {
                                     tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2)+ "%'");
                                 }

							 }
						}
						if(tempSql.length()>0)
            {
                buf.append(tempSql.substring(3));
						}
						else {
                            buf.append(" tmessage.b0110='##'");
                        }
					}
    else {
buf.append(" tmessage.b0110='##'");
                    }

					buf.append(" or nullif(tmessage.b0110,'') is null)");
				}
			}
			if(dbw.isExistField("tmessage", "receivetype", false)){
				buf.append(" and (nullif(username,'') is null or (lower(username)='"+userview.getUserName().toLowerCase()+"' and (receivetype='4' or nullif(receivetype,'') is null)) ");
				if(this.getRoleArr(userview).length()>0) {
                    buf.append(" or (username in("+this.getRoleArr(userview)+") and receivetype='2'))");
                } else {
                    buf.append(" )");
                }
			}else {
                buf.append(" and ( nullif(username,'') is null  or lower(username)='"+this.userview.getUserName().toLowerCase()+"')");
            }
			if(this._static==10) //单位管理
			{
				buf.append(" and object_type=2 ");
			}
			else if(this._static==11) //职位管理
			{
				buf.append(" and object_type=3 ");
}
else
{
buf.append(" and ( object_type is null or object_type=1 ) ");
			}

			if(this._static==10||this._static==11)
			{
				ArrayList b0110list=new ArrayList();
				rset=dao.search(buf.toString());
				while(rset.next())
				{
					if(rset.getString("b0110")==null||rset.getString("b0110").trim().length()==0) {
                        continue;
                    }
					b0110list.add(rset.getString("b0110"));

				}
				if(this._static==10) {
                    this.impDataFromArchive(b0110list,"B"); //直接从档案库中把数据导入进来，
                }
				if(this._static==11) {
                    this.impDataFromArchive(b0110list,"K"); //直接从档案库中把数据导入进来，
                }
			}
			else
			{

				rset=dao.search(buf.toString());
				while(rset.next())
				{
					base_pre=rset.getString("db_type");
					if(base_pre==null|| "".equalsIgnoreCase(base_pre)) {
                        continue;
                    }
					a0100=rset.getString("a0100");
					/**按人员库进行分类*/
					if(!hm.containsKey(base_pre))
					{
						a0100list=new ArrayList();
					}
					else
					{
						a0100list=(ArrayList)hm.get(base_pre);
					}
					a0100list.add(a0100);
					hm.put(base_pre,a0100list);
				}//while loop end.
				Iterator iterator=hm.entrySet().iterator();
				while(iterator.hasNext())
				{
					Entry entry=(Entry)iterator.next();
					String pre=entry.getKey().toString();
					a0100list =(ArrayList)entry.getValue();

					ArrayList list=new ArrayList();
					for(int i=0;i<a0100list.size();i++)
					{
						list.add((String)a0100list.get(i));
						if(i!=0&&i%500==0)
						{
							this.impDataFromArchive(list,pre); //直接从档案库中把数据导入进来，
							list=new ArrayList();
						}
					}
					if(list.size()>0) {
                        this.impDataFromArchive(list,pre); //直接从档案库中把数据导入进来，
                    }
					//	this.impDataFromArchive(a0100list,pre); //直接从档案库中把数据导入进来，

				}
			}
//			buf.append("insert into ");
//			buf.append(tabname);
//			buf.append(" (a0100,a0101_1,basepre,state) select ");
//			buf.append(" distinct a0100,a0101,db_type,1 from tmessage where noticetempid=");
//			buf.append(this.tabid);
//			buf.append(" and a0100 not in (select a0100 from ");
//			buf.append(tabname);
//			buf.append(")");
//			dao.update(buf.toString());
/**把变化后数据更新到临时表中去*/
	buf.setLength(0);
			buf.append("select id,a0100,db_type,changelast,changepre from tmessage where state=0 and noticetempid=");
			if(this._static==10||this._static==11) //单位管理
			{
				buf.setLength(0);
				buf.append("select id,b0110,changelast,changepre from tmessage where state=0 and noticetempid=");
			}
			buf.append(this.tabid);
			if(!this.userview.isSuper_admin()&& "1".equals(this.filter_by_manage_priv))
			{
				/*
				buf.append(" and (tmessage.b0110 like '");
				if((this.userview.getManagePrivCodeValue()==null||this.userview.getManagePrivCodeValue().trim().length()==0)&&this.userview.getManagePrivCode().length()==0)
					buf.append("##");
else
buf.append(this.userview.getManagePrivCodeValue());
				buf.append("%' or tmessage.b0110 is null or tmessage.b0110='')");
				*/
				String operOrg = userview.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板
				if(operOrg==null||!"UN`".equalsIgnoreCase(operOrg))
				{
					buf.append(" and ( ");

					if(operOrg!=null && operOrg.length() >3)
					{
StringBuffer tempSql = new StringBuffer("");
						String[] temp = operOrg.split("`");
						for (int j = 0; j < temp.length; j++) {
							 if (temp[j]!=null&&temp[j].length()>0)
							 {
								 if("0".equalsIgnoreCase(this.include_suborg))//不包含下属单位
								 {
									 if("UN".equalsIgnoreCase(temp[j].substring(0,2)))
									 {
										 tempSql.append(" or  tmessage.b0110_self ='" + temp[j].substring(2)+ "'");
									 }
									 else {
                                         tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2)+ "%'");
}
						 }
								 else {
                                     tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2)+ "%'");
                                 }
							 }
						}
						if(tempSql.length()>0)
						{
							buf.append(tempSql.substring(3));
						}
						else {
                            buf.append(" tmessage.b0110='##'");
                        }
					}
					else {
                        buf.append(" tmessage.b0110='##'");
                    }

					buf.append(" or nullif(tmessage.b0110,'') is null)");
				}

			}
			if(dbw.isExistField("tmessage", "receivetype", false)){
				buf.append(" and (nullif(username,'') is null or (lower(username)='"+userview.getUserName().toLowerCase()+"' and (receivetype='4' or nullif(receivetype,'') is null)) ");
				if(this.getRoleArr(userview).length()>0) {
                    buf.append(" or (username in("+this.getRoleArr(userview)+") and receivetype='2'))");
                } else {
                    buf.append(" )");
                }
			}else {
                buf.append(" and ( nullif(username,'') is null  or lower(username)='"+this.userview.getUserName().toLowerCase()+"')");
            }
			if(this._static==10) //单位管理
            {
                buf.append(" and object_type=2 ");
            } else if(this._static==11) //职位管理
            {
                buf.append(" and object_type=3 ");
            } else {
                buf.append(" and ( object_type is null or object_type=1 ) ");
            }
			rset=dao.search(buf.toString());

			String chgpre=null;
			String chglast=null;
			ArrayList paralist=new ArrayList();
			while(rset.next())
			{
				if(this._static==10||this._static==11)
				{
					b0110=rset.getString("b0110");
				}
				else
				{
					a0100=rset.getString("a0100");
					base_pre=rset.getString("db_type");
				}
				if(!(this._static==10||this._static==11)){
				    if(base_pre==null){
				        base_pre="";
				    }
				}
				chgpre=rset.getString("changepre");
				chglast=rset.getString("changelast");
				/**数据更新操作*/
				buf.setLength(0);
				paralist.clear();
				buf.append("update ");
				buf.append(tabname);
				buf.append(" set ");
				buf.append(getChgUpdateSQL(chglast,chgpre,tabname));

				if(this._static==10)//单位管理
				{
					buf.append(" where b0110='"+b0110+"'");
					paralist.add(b0110);
				}
				else if(this._static==11)//职位管理
				{
					buf.append(" where e01a1='"+b0110+"'");
					paralist.add(b0110);
				}
				else
				{
					buf.append(" where a0100='"+a0100+"'");
    buf.append(" and lower(basepre)='"+base_pre.toLowerCase()+"'");
					paralist.add(a0100);
					paralist.add(base_pre.toLowerCase());
				}
				dao.update(buf.toString());
				//dao.update(buf.toString(),paralist);   当更新值带?号，程序解析sql会出错
				/**同时更新消息库*/
				buf.setLength(0);
				buf.append("update tmessage set state=1 ");
				buf.append(",receiver='");
				buf.append(this.userview.getUserName());
				buf.append("'");

				buf.append(",Receive_date=");
				buf.append(Sql_switcher.sqlNow());
				buf.append(" where id=");
				buf.append(rset.getInt("id"));
				dao.update(buf.toString());
//				RecordVo msgvo=new RecordVo("tmessage");
//				msgvo.setInt("id",rset.getInt("id"));  //
//			    msgvo.setInt("state",1);
//			    dao.updateValueObject(msgvo);

			}
			PubFunc.closeDbObj(rset);
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
			  throw GeneralExceptionHandler.Handle(ex);
		  }
		}
	}

	/**
	 * 判断模板中包含了关联序号的变化后指标
*/
public void existFilloutSequence()throws GeneralException
{
int i=0;
ArrayList fieldlist=getAllFieldItem();
		try
		{
			for(i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				if(item.isChangeAfter())
				{
					if(item.isSequenceable()){
						this.existid_gen_manual="1";
						break;
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/**
	 * 对于调入模板中自动生成序号
*/
public void filloutSequence(String a0100s,String dbpre,String tabname)throws GeneralException
	{
		int i=0;
ArrayList fieldlist=getAllFieldItem();
		HashMap seqHm=new HashMap();
try
{
for(i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				if(item.isChangeAfter())
				{
					if(item.isSequenceable()) {
                        seqHm.put(item.getItemid().toString(),item.getSequencename());
                    }
				}
			}
			/**生成序号*/
			createRuleSequenceNo(seqHm,a0100s,dbpre,tabname);
		}
		catch(Exception ex)
{
ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
}
}
/**
* 对人员调入模板业务，需要升级，前台人员列表姓名才不为空
	 * @throws GeneralException
	 */
	private void updateA0101_1()throws GeneralException
	{
		String strDesT=null;
		StringBuffer buf=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{

			String set_str=" set a0101_1=a0101_2";
			if(this.infor_type==2||this.infor_type==3) {
                set_str=" set codeitemdesc_1=codeitemdesc_2";
            }

			if(isBEmploy())//员工通过自助平台发动申请
            {
                strDesT="g_templet_"+this.tabid;
            } else {
 strDesT=this.userview.getUserName()+"templet_"+this.tabid;
}
buf.append("update  ");
			buf.append(strDesT);
			buf.append(set_str);
			dao.update(buf.toString());
			String sql="select * from "+strDesT ;
			RowSet rowSet=dao.search(sql);
			while(rowSet.next()){
			String	seqnum = rowSet.getString("seqnum");
			if(seqnum==null||seqnum.trim().length()==0){
			 seqnum=CreateSequence.getUUID();
				if(this.infor_type==1)
				{
					dao.update("update "+strDesT+" set seqnum='"+seqnum+"' where a0100='"+rowSet.getString("a0100")+"' and  lower(basepre)='"+rowSet.getString("basepre").toLowerCase()+"'");
				}
				else if(this.infor_type==2)
				{
					dao.update("update "+strDesT+" set seqnum='"+seqnum+"' where b0110='"+rowSet.getString("b0110")+"'");
				}
				else if(this.infor_type==3)
				{
					dao.update("update "+strDesT+" set seqnum='"+seqnum+"' where E01A1='"+rowSet.getString("E01A1")+"'");
				}
			}
			}
			PubFunc.closeDbObj(rowSet);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}
	/**
	 * 从档案中同步数据。
	 * @param a0100s
	 * @param dbpre
	 * @return
	 * @throws GeneralException
	 */
	public boolean syncDataFromArchive()throws GeneralException
	{
		boolean bflag=true;
		/**人员调入业务|新建组织，数据不用同步*/
		if(operationtype==0||operationtype==5)
		{
			updateA0101_1();
			return bflag;
		}
		String strDesT=null;
		StringBuffer buf=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		HashMap hm=new HashMap();
		ArrayList a0100list=null;
		String pre=null;
		String a0100=null;
		try
		{
			if(isBEmploy())//员工通过自助平台发动申请
            {
                strDesT="g_templet_"+this.tabid;
            } else {
                strDesT=this.userview.getUserName()+"templet_"+this.tabid;
            }
			buf.append("select basepre,a0100 from ");
			buf.append(strDesT);
			buf.append(" order by basepre");
			if(this.infor_type==2)
			{
				buf.setLength(0);
				buf.append("select b0110 from ");
				buf.append(strDesT);
			}
			else if(this.infor_type==3)
			{
				buf.setLength(0);
				buf.append("select e01a1 from ");
				buf.append(strDesT);
			}

			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				if(this.infor_type==1)
				{
					a0100=rset.getString("a0100");
					/**按人员库进行分类*/
					pre=rset.getString("basepre");
					/**人员库分空时，则按在职人员数据同步*/
					if(pre==null||pre.length()==0) {
                        pre="Usr";
                    }
					if(!hm.containsKey(pre))
					{
						a0100list=new ArrayList();
					}
					else
					{
						a0100list=(ArrayList)hm.get(pre);
					}
					a0100list.add(a0100);
					hm.put(pre,a0100list);
				}
				else if(this.infor_type==2||this.infor_type==3)
				{
					if(a0100list==null) {
                        a0100list=new ArrayList();
                    }
					if(rset.getString(1).charAt(0)=='B'||rset.getString(1).charAt(0)=='K') {
                        continue;
                    }
					a0100list.add(rset.getString(1));
				}
			}
			if(this.infor_type==2||this.infor_type==3) {
                hm.put("BK",a0100list);
            }

			Iterator iterator=hm.entrySet().iterator();
			while(iterator.hasNext())
			{
				Entry entry=(Entry)iterator.next();
				pre=entry.getKey().toString();
				a0100list =(ArrayList)entry.getValue();

				if(a0100list==null) {
                    continue;
                }
				if(a0100list.size()<=500)
				{
					StringBuffer stra0100 = getA0100String(a0100list);
					impDataFromArchive(stra0100.toString(),pre,1);
				}
				else
				{
					ArrayList tempList=null;
					int size=a0100list.size();
					int n=size/500+1;
					for(int i=0;i<n;i++)
					{
						tempList=new ArrayList();
						for(int j=i*500;j<(i+1)*500;j++)
						{
							if(j<a0100list.size()) {
                                tempList.add((String)a0100list.get(j));
                            } else {
                                break;
                            }
						}
						if(tempList.size()>0)
						{
							StringBuffer stra0100 = getA0100String(tempList);
							impDataFromArchive(stra0100.toString(),pre,1);
						}

					}
				}

			}
			if(strDesT.equals(this.userview.getUserName()+"templet_"+this.tabid)) {
                impPreDataFromMessage(strDesT,"");
            }
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}

	/**
	 *  从档案中同步数据。
	 * @param ins_id
	 * @param descTab
	 * @return
	 * @throws GeneralException
	 */
	public boolean syncDataFromArchive(int ins_id,String descTab)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer("select basepre,a0100 from ");

			if(this.infor_type==2)
			{
				buf.setLength(0);
				buf.append("select b0110 from ");
			}
			else if(this.infor_type==3)
			{
				buf.setLength(0);
				buf.append("select e01a1 from ");
			}

			buf.append("templet_"+tabid);
			buf.append(" where ins_id="+ins_id);
			if(this.infor_type==1) {
                buf.append(" order by basepre");
            }
			RowSet rset=dao.search(buf.toString());
			String a0100="";
			String pre="";
			HashMap hm=new HashMap();
			ArrayList a0100list=new ArrayList();
			while(rset.next())
			{
				if(this.infor_type==1)
				{
					a0100=rset.getString("a0100");
					/**按人员库进行分类*/
					pre=rset.getString("basepre");
					/**人员库分空时，则按在职人员数据同步*/
					if(pre==null||pre.length()==0) {
                        pre="Usr";
                    }
					if(!hm.containsKey(pre))
					{
						a0100list=new ArrayList();
					}
					else
					{
						a0100list=(ArrayList)hm.get(pre);
					}
					a0100list.add(a0100);
					hm.put(pre,a0100list);
				}
				else {
                    a0100list.add(rset.getString(1));
                }
			}

			if(a0100list.size()>0&&(this.infor_type==2||this.infor_type==3)) {
                hm.put("BK",a0100list);
            }

			Iterator iterator=hm.entrySet().iterator();
			while(iterator.hasNext())
			{
				Entry entry=(Entry)iterator.next();
				pre=entry.getKey().toString();
				a0100list =(ArrayList)entry.getValue();
				if(a0100list.size()<500)
				{
					StringBuffer stra0100 =getA0100String(a0100list);
					impDataFromArchive2(stra0100.toString(),pre,1,"templet_"+tabid);
				}
else
{
    int size=a0100list.size();
					int n=size/500+1;
					ArrayList tempList=new ArrayList();
					for(int i=0;i<n;i++)
					{
						tempList=new ArrayList();
						for(int j=i*500;j<(i+1)*500;j++)
						{
							if(j<a0100list.size()) {
                                tempList.add((String)a0100list.get(j));
                            } else {
            break;
                            }
}
if(tempList.size()>0)
						{
							StringBuffer stra0100 =getA0100String(tempList);
							impDataFromArchive2(stra0100.toString(),pre,1,"templet_"+tabid);
                }

					}

				}
			}

			impPreDataFromMessage("templet_"+tabid," ins_id="+ins_id);
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}

	/**
	 * 人事异动模板变化前指标的值每次都要导入档案库中最新的记录，但子集的不用导。
	 */
	private boolean isImpDataFromArchive_sub=true;
	/**
	 * 从档案中导入数据
	 * @param a0100s  for examples '0100000','20202020' or 是一个SQL条件
	 * @param dbpre
	 * @param sync =0导入　　=1更新
	 * @return
	 * @throws GeneralException
	 */
	public boolean impDataFromArchive2(String a0100s,String dbpre,int sync,String desTab)throws GeneralException
	{
		boolean bflag=true;
		int nmode=0,nhismode=0,ncount=0,nchgstate=0;
		try
		{
			/**导入*/
			if(sync==0) {
                impMainSetFromArchive(a0100s,dbpre);
            }
			ArrayList setlist=searchUsedSetList();
			String setname=null;
			String cname=null;
			String field_name=null;
			StringBuffer strsql=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=null;
			int db_type=Sql_switcher.searchDbServer();//数据库类型
			String strDesT=desTab;
			StringBuffer strUpdate=new StringBuffer();
			HashMap seqHm=new HashMap();//序号
			Document doc=null;
			Element element=null;
			String xpath="/sub_para/para";


			/*
			boolean isFinished_record=false;//判断当前显示的记录 是否为已结束任务的记录  用于浏览结束后记录的模板时，变化前指标不动态取库中的值
            if(getTasklist()!=null&&getTasklist().size()==1&&getInslist().size()==1&&getIns_id()!=0)
            {
            	String _task_id=(String)getTasklist().get(0);
            	rset=dao.search("select task_state from t_wf_task where task_id="+_task_id);
            	if(rset.next())
            	{
            		if(rset.getString(1).equals("5"))
            			isFinished_record=true;
            	}
            	if(rset!=null)
            		rset.close();
            }*/

			//获得要更新的数据
String paramname = "a0100";
			if(this.infor_type==2) {
paramname="b0110";
           } else if(this.infor_type==3) {
                paramname="e01a1";
            }
			ArrayList a0110list = new ArrayList();
			String arr0100s [] =a0100s.split(",");
			for(int a =0; a<arr0100s.length;a++){
				if(arr0100s[a].trim().length()>0){
					a0110list.add(arr0100s[a].trim().replace("'", ""));
				}
}

/**更新非插入子集区域的值*/
for(int i=0;i<setlist.size();i++)
			{
setname=(String)setlist.get(i);
strsql.setLength(0);
//				if(setname.equalsIgnoreCase("A01")||setname.equalsIgnoreCase("B01")||setname.equalsIgnoreCase("K01"))
//					continue;
				if(db_type==2)//oracle
					//strsql.append("select T.field_name,T.ChgState,T.hismode,T.rcount,T.mode_o,T.subflag,T.sub_domain from template_set T ,fielditem M where ");
                {
                    strsql.append("select  T.field_name,T.ChgState,T.hismode,T.rcount,T.mode_o,T.subflag,T.formula,T.Hz,T.sub_domain from template_set T  where ");
                } else
				//	strsql.append("select T.field_name,T.ChgState,T.hismode,T.rcount,T.mode,T.subflag,T.sub_domain from template_set T ,fielditem M where ");
                {
                    strsql.append("select  T.field_name,T.ChgState,T.hismode,T.rcount,T.mode,T.subflag,T.formula,T.Hz,T.sub_domain from template_set T where ");
                }
				strsql.append(" T.tabid=");
				strsql.append(this.tabid);
//				strsql.append(" and T.subflag=0 and ((T.field_name=M.itemid and M.useflag<>'0') or (T.field_name='B0110' or T.field_name='E01A1'))");
				strsql.append(" and T.subflag=0 ");
				strsql.append(" and T.flag<>'H' ");
				if(sync==1) {
                    strsql.append(" and T.chgstate=1 ");
                } else {
                    strsql.append(" and (T.chgstate=1 or T.chgstate=2) ");
                }
				if(this.infor_type==2&& "B01".equalsIgnoreCase(setname))
				{
					strsql.append(" and ( T.setname='"+setname+"' or T.field_name='codesetid'  or T.field_name='codeitemdesc'  or T.field_name='corcode'  or T.field_name='parentid'  or T.field_name='start_date'  ) ");
				}
				else if(this.infor_type==3&& "K01".equalsIgnoreCase(setname))
				{
					strsql.append(" and ( T.setname='"+setname+"' or T.field_name='codesetid'  or T.field_name='codeitemdesc'  or T.field_name='corcode'  or T.field_name='parentid'  or T.field_name='start_date'  ) ");
    }
    else
    {
		strsql.append(" and T.setname='");
					strsql.append(setname);
					strsql.append("'");
				}
				rset=dao.search(strsql.toString());
				String fieldstr="";
				String fieldstr1="";
				while(rset.next())
				{
					strUpdate.setLength(0);
					nchgstate=rset.getInt("chgstate");
					if(db_type==2)//oracle
                    {
                        nmode=rset.getInt("mode_o");
                    } else {
                        nmode=rset.getInt("mode");
                    }
					ncount=rset.getInt("rcount");
					nhismode=rset.getInt("hismode");
					cname=rset.getString("field_name");
					if(cname==null) {
                        continue;
                    }
					String formula=Sql_switcher.readMemo(rset,"formula");
					String sub_domain = Sql_switcher.readMemo(rset,"sub_domain");
					//获得sub_domain_id
					String sub_domain_id="";
					//获得第x到y中的x值
					String his_start2="";
					int his_start =0;
					if(sub_domain!=null&&sub_domain.trim().length()>0&&"1".equals(""+rset.getInt("ChgState"))){
						try{
								doc=PubFunc.generateDom(sub_domain);;
								XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
								List childlist=findPath.selectNodes(doc);
								if(childlist!=null&&childlist.size()>0)
								{
									element=(Element)childlist.get(0);
									if(element.getAttributeValue("id")!=null){
									sub_domain_id=(String)element.getAttributeValue("id");
									if(sub_domain_id!=null&&sub_domain_id.trim().length()>0){
										sub_domain_id = "_"+sub_domain_id;
									}else{
										sub_domain_id="";
									}
									}
									if(element.getAttributeValue("his_start")!=null){
										his_start2=(String)element.getAttributeValue("his_start");
										if(his_start2!=null&&his_start2.trim().length()>0){
										}else{
											his_start2="";
										}
										}
								}
						}catch(Exception e){

						}
					}

					if(his_start2.length()>0) {
                        his_start = Integer.parseInt(his_start2);
                    }
					field_name=cname+sub_domain_id+"_"+nchgstate;
					String strSrcT=setname;
					FieldItem fielditem=DataDictionary.getFieldItem(cname);
					/**未构库或指标体系不存在时则退出*/
					if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                        continue;
                    }
					/**自动生序规则序号，如备案号，只对变化后指标等*/
					if(nchgstate==2)
					{
						if(fielditem.isSequenceable()) {
                            seqHm.put(fielditem.getItemid().toString(),fielditem.getSequencename());
                        }
					}
			//		if(nchgstate!=1)
			//			continue;
					if(nchgstate==2&&this.opinion_field!=null&&this.opinion_field.length()>0&&this.opinion_field.equalsIgnoreCase(fielditem.getItemid()))
            {
                continue;
					}

					/*
					if(isFinished_record) //判断当前显示的记录 是否为已结束任务的记录  用于浏览结束后记录的模板时，变化前指标不动态取库中的值
				{
						if(nchgstate==1)
							continue;
					}*/

					if(nchgstate==2) {
                        nhismode=1;
                    }
					if(fieldstr.indexOf(field_name)!=-1) {
                        continue;
                    }
					if(db_type==2||db_type==3)
					{
						fieldstr="T."+field_name;
                                fieldstr1="U."+cname;
					}
					else {
                        fieldstr="T."+field_name+"=U."+cname;
                    }
					if("codesetid".equalsIgnoreCase(cname)|| "codeitemdesc".equalsIgnoreCase(cname)|| "corcode".equalsIgnoreCase(cname)|| "parentid".equalsIgnoreCase(cname)|| "start_date".equalsIgnoreCase(cname))
					{
						updateOrgInfo(field_name,cname,a0100s,strDesT);
						continue;
					}
					//更新标志
					boolean flag = false;
					ArrayList fieldlist = new ArrayList();
					fieldlist.add(fielditem);
					switch(setname.charAt(0))
					{
					case 'A'://人员信息
					case 'a':
						strSrcT=dbpre+strSrcT;
						if(db_type==2||db_type==3) //oracle,db2
						{
							strUpdate.append("update ");
							strUpdate.append(strDesT);
							strUpdate.append(" T set (");
							strUpdate.append(fieldstr);
							strUpdate.append(")=(select ");
							strUpdate.append(fieldstr1);
							strUpdate.append(" from ");
							strUpdate.append(strSrcT);
							strUpdate.append(" U Where T.A0100=U.A0100");
						}
						else
						{
							strUpdate.append("Update T set ");
							strUpdate.append(fieldstr);
							strUpdate.append(" from ");
							strUpdate.append(strDesT);
							strUpdate.append(" T Left join ");
							strUpdate.append(strSrcT);
							strUpdate.append(" U ON T.A0100=U.A0100");
						}
						if("A01".equalsIgnoreCase(setname))
						{
							if(db_type==2||db_type==3)
							{
								strUpdate.append(") where T.A0100 in (");
								strUpdate.append(a0100s);
								strUpdate.append(") and basepre='");
								strUpdate.append(dbpre);
								strUpdate.append("'");
								strUpdate.append(" and  exists (select A0100 from "+dbpre+"A01 where "+dbpre+"A01.a0100=T.A0100 ) ");
							}
							else
							{
								strUpdate.append(" where T.A0100 in (");
								strUpdate.append(a0100s);
								strUpdate.append(") and basepre='");
								strUpdate.append(dbpre);
								strUpdate.append("'");
								strUpdate.append(" and  exists (select A0100 from "+dbpre+"A01 where "+dbpre+"A01.a0100=T.A0100 ) ");
							}
						}
						else
						{
							if(nhismode==1) //当前记录
							{
								if(db_type==2||db_type==3)
								{
									strUpdate.append(" and (U.I9999=(select Max(I9999) from ");
									strUpdate.append(strSrcT);
									strUpdate.append(" where ");
									strUpdate.append(strSrcT);
									strUpdate.append(".A0100=U.A0100) or U.I9999 is null) "); // and (T.state<>1 or T.state is null)");
									strUpdate.append(") where T.A0100 in (");
									strUpdate.append(a0100s);
									strUpdate.append(") and basepre='");
									strUpdate.append(dbpre);
									strUpdate.append("'");
								}
								else
								{
									strUpdate.append(" where (U.I9999=(select Max(I9999) from ");
									strUpdate.append(strSrcT);
									strUpdate.append(" where ");
									strUpdate.append(strSrcT);
									strUpdate.append(".A0100=U.A0100) or U.I9999 is null) "); // and (T.state<>1 or T.state is null)");
									strUpdate.append(" and T.A0100 in (");
									strUpdate.append(a0100s);
									strUpdate.append(") and basepre='");
									strUpdate.append(dbpre);
									strUpdate.append("'");
								}
							}else if(nhismode==3) //条件定位
							{
								 //  <EXPR>1</EXPR><FACTOR>A0420=01</FACTOR>
								String expr="";
								String factor="";
								if(formula.trim().length()>0)
								{
									int f=formula.indexOf("<EXPR>");
									int t=formula.indexOf("</EXPR>");
									expr=formula.substring(f+6,t);
									f=formula.indexOf("<FACTOR>");
									t=formula.indexOf("</FACTOR>");
									factor=formula.substring(f+8,t);
								}
								factor=factor.replaceAll(",","`");
								factor=factor+"`";

								FactorList factorlist=new FactorList(expr,factor,"");
								String strw=factorlist.getSingleTableSqlExpression("F");

                                        //update sutemplet_12 T set (T.A0405_1)=
							//	  (select U.A0405 from usrA04 U Where T.A0100=U.A0100
                                //			     and U.i9999=(select max(F.i9999) from usrA04 F where U.a0100=F.a0100 and F.C0407='1' ) )
								//写入临时表里

								StringBuffer strUpdate2 = new StringBuffer();
								if(a0110list.size()>0){
									for(int m =0;m<a0110list.size();m++){
								StringBuffer buf = new StringBuffer();
								buf.append("select ");
								buf.append("F."+cname);
								buf.append(",F.i9999");
								buf.append(" from ");
								buf.append(strSrcT+" F ");
								if(this.infor_type==1) {
                                    buf.append(" where F.a0100='"+a0110list.get(m)+"'");
                                } else if(this.infor_type==2) {
                                    buf.append(" where F.b0110='"+a0110list.get(m)+"'");
                                } else if(this.infor_type==3) {
buf.append(" where F.e01a1='"+a0110list.get(m)+"'");
                                }
								if(strw.trim().length()>0) {
                                    buf.append(" and ("+strw+") ");
                                }
								//更新记录
								ArrayList reclist=getRecordValue(fieldlist,dao.search(buf.toString()));
								buf.setLength(0);
								for(int n=0;n<reclist.size();n++)
								{
									buf.append(((ArrayList)reclist.get(n)).get(1).toString().replaceAll("`", "^^"));
									if(n<reclist.size()-1){
										buf.append("`");
									}
								}
								strUpdate2.setLength(0);
								strUpdate2.append("Update ");
								strUpdate2.append(strDesT);
								strUpdate2.append(" set ");
								strUpdate2.append(field_name);
								strUpdate2.append(" ='"+buf.toString());
								strUpdate2.append("' where ");
								strUpdate2.append(paramname);
								strUpdate2.append(" = ");
								strUpdate2.append("'"+a0110list.get(m)+"'");
								if(this.infor_type==1&&dbpre.length()>0) {
                                    strUpdate2.append(" and basepre ='"+dbpre+"'");
                                }
								dao.update(strUpdate2.toString());
								flag =true;
									}
								}
//								strUpdate.append(" and  (U.i9999=(select max(F.i9999) from "+strSrcT+" F where U.a0100=F.a0100 ");
//								if(strw!=null&&strw.trim().length()>0)
//									strUpdate.append(" and "+strw+" ");
//								strUpdate.append(" ) ");
//
//							//	strUpdate.append(" and "+strw);
//								strUpdate.append(" ) ");
//								if(db_type==2||db_type==3) //oracle,db2
//									strUpdate.append(" ) ");
//								strUpdate.append(" where T.A0100 in (");
//								strUpdate.append(a0100s);
//								strUpdate.append(") and basepre='");
//								strUpdate.append(dbpre);
//								strUpdate.append("'");

    }
    else if(nhismode==4) //条件序号
							{
								 //  <EXPR>1</EXPR><FACTOR>A0420=01</FACTOR>
                                         String expr="";
                                 		String factor="";
								if(formula.trim().length()>0)
								{
									int f=formula.indexOf("<EXPR>");
									int t=formula.indexOf("</EXPR>");
									expr=formula.substring(f+6,t);
									f=formula.indexOf("<FACTOR>");
									t=formula.indexOf("</FACTOR>");
									factor=formula.substring(f+8,t);
								}
								factor=factor.replaceAll(",","`");
								factor=factor+"`";

                        	FactorList factorlist=new FactorList(expr,factor,"");
				String strw=factorlist.getSingleTableSqlExpression("F");

								//update sutemplet_12 T set (T.A0405_1)=
							//	  (select U.A0405 from usrA04 U Where T.A0100=U.A0100
							//			     and U.i9999=(select max(F.i9999) from usrA04 F where U.a0100=F.a0100 and F.C0407='1' ) )
								//写入临时表里

								StringBuffer strUpdate2 = new StringBuffer();
                            if(a0110list.size()>0){
                    			for(int m =0;m<a0110list.size();m++){

								StringBuffer buf = new StringBuffer();
								buf.append("select ");
								buf.append("F."+cname);
								buf.append(",F.i9999");
								buf.append(" from ");
								buf.append(strSrcT+" F ");
								if(this.infor_type==1) {
                                    buf.append(" where F.a0100='"+a0110list.get(m)+"'");
} else if(this.infor_type==2) {
                                    buf.append(" where F.b0110='"+a0110list.get(m)+"'");
                                } else if(this.infor_type==3) {
                                    buf.append(" where F.e01a1='"+a0110list.get(m)+"'");
                                }
								if(strw.trim().length()>0) {
                                    buf.append(" and ("+strw+") ");
                                }
								ArrayList i9999list=getSubSetI9999s(buf.toString()+" order by F.i9999");
								ArrayList paralist = new ArrayList();
								int size=i9999list.size();
								if(size>0)
								{
								/**初值为-1*/
								String curri9999="-1";
								switch(nmode)
								{
								case 0://倒数第...条（最近第）

										if(size>=ncount)//子集记录大于要取的的记录数
										{
											if(size==ncount) {
                                                curri9999=(String)i9999list.get(0);
                                            } else
											{
												if(ncount!=0) {
                                                    curri9999=(String)i9999list.get(size-ncount);
                                                } else {
                                                    curri9999=(String)i9999list.get(size-ncount-1);
                                                }
											}
										}

										buf.append(" and F.I9999=?");
									paralist.add(curri9999);
									break;
								case 1://倒数...条（最近）
									if(his_start==0){
										if(size>=ncount)
										{
											if(size==ncount) {
                                                curri9999=(String)i9999list.get(0);
                                            } else {
                                                curri9999=(String)i9999list.get(size-ncount);
                                            }
										}
										buf.append(" and F.I9999>=? order by F.I9999");
									}else{
										if(his_start>size){
											curri9999="-1";
											buf.append(" and F.I9999<=? order by F.I9999");
										}else{
											if(size>=ncount){
												String curri99992=(String)i9999list.get(size-his_start);
												buf.append(" and F.I9999<=? ");
												paralist.add(curri99992);
												if(size<his_start+ncount) {
                                                    curri9999=(String)i9999list.get(0);
                      } else {
                                                    curri9999=(String)i9999list.get(size-ncount-(his_start-1));
                                                }
												buf.append(" and F.I9999>=? order by F.I9999");

											}else{
												 curri9999=(String)i9999list.get(size-his_start);
												buf.append(" and F.I9999<=? order by F.I9999");
											}

										}
									}
									paralist.add(curri9999);
									break;
								case 2://正数第...条(最初第)
									if(size>=ncount) {
                                        curri9999=(String)i9999list.get(ncount-1);
                                    }
									buf.append(" and F.I9999=?");
									paralist.add(curri9999);
									break;
								case 3://正数...条（最初）
									if(his_start==0){
										if(size>=ncount){
                                                curri9999=(String)i9999list.get(ncount-1);
										buf.append(" and F.I9999<=? order by F.I9999");
										}else{
											buf.append(" and F.I9999>=? order by F.I9999");
										}
									}else{
										if(his_start>size){
											curri9999="-1";
											buf.append(" and F.I9999<=? order by F.I9999");
										}else{
											if(size>=ncount){
                                    String curri99992=(String)i9999list.get(his_start-1);
buf.append(" and F.I9999>=? ");
												paralist.add(curri99992);
												if(size<his_start+ncount){
													buf.append(" and I9999>=? ");
													curri9999=curri99992;
												}else{
												curri9999=(String)i9999list.get(his_start+ncount-2);
												buf.append(" and F.I9999<=? order by F.I9999");
                                            }
}else{
curri9999=(String)i9999list.get(his_start-1);
													buf.append(" and F.I9999>=? order by F.I9999");
											}

										}
									}
									paralist.add(curri9999);
									break;
								}
								}

								//更新记录
								ArrayList reclist=getRecordValue(fieldlist,dao.search(buf.toString(),paralist));
								buf.setLength(0);
								for(int n=0;n<reclist.size();n++)
								{
									buf.append(((ArrayList)reclist.get(n)).get(1).toString().replaceAll("`", "^^"));
                if(n<reclist.size()-1){
buf.append("`");
}
}
		strUpdate2.setLength(0);
								strUpdate2.append("Update ");
								strUpdate2.append(strDesT);
								strUpdate2.append(" set ");
								strUpdate2.append(field_name);
								strUpdate2.append(" ='"+buf.toString());
								strUpdate2.append("' where ");
								strUpdate2.append(paramname);
								strUpdate2.append(" = ");
								strUpdate2.append("'"+a0110list.get(m)+"'");
								if(this.infor_type==1&&dbpre.length()>0) {
              strUpdate2.append(" and basepre ='"+dbpre+"'");
                                }
								dao.update(strUpdate2.toString());
								flag =true;
									}
								}

							}
							else if(nhismode==2&&(nmode==0 || nmode==1||nmode==2||nmode==3)) //多条记录  wangrd 2015-03-20 加nmode==2 =0
							{
								//查找库中的数据个数
								StringBuffer strUpdate2 = new StringBuffer();
								if(a0110list.size()>0){
									for(int m =0;m<a0110list.size();m++){
										/**求子集序号列表*/
										ArrayList i9999list=getSubSetI9999s(strSrcT,""+a0110list.get(m));

											int size=i9999list.size();
											StringBuffer buf = new StringBuffer();
											ArrayList paralist = new ArrayList();
											buf.append("select ");
											buf.append(cname);
											buf.append(",i9999");
											buf.append(" from ");
											buf.append(strSrcT);
											if(this.infor_type==1) {
                                                buf.append(" where a0100=?");
                                            } else if(this.infor_type==2) {
                                                buf.append(" where b0110=?");
                                            } else if(this.infor_type==3) {
                                                buf.append(" where e01a1=?");
                                            }
											paralist.add(""+a0110list.get(m));
											if(size>0)
											{
												/**初值为-1*/
												String curri9999="-1";
												switch(nmode)
												{
												case 0://倒数第...条（最近第）

														if(size>=ncount)//子集记录大于要取的的记录数
														{
															if(size==ncount) {
                                                                curri9999=(String)i9999list.get(0);
                                                            } else
															{
																if(ncount!=0) {
                                                                    curri9999=(String)i9999list.get(size-ncount);
                                                                } else {
                                                                    curri9999=(String)i9999list.get(size-ncount-1);
                                                                }
															}
														}

														buf.append(" and I9999=?");
													paralist.add(curri9999);
													break;
												case 1://倒数...条（最近）
													if(his_start==0){
														if(size>=ncount)
														{
															if(size==ncount) {
                                                                curri9999=(String)i9999list.get(0);
                                                            } else {
                                                                curri9999=(String)i9999list.get(size-ncount);
                                                            }
														}
														buf.append(" and I9999>=? order by I9999");
													}else{
														if(his_start>size){
															curri9999="-1";
															buf.append(" and I9999<=? order by I9999");
														}else{
															if(size>=ncount){
																String curri99992=(String)i9999list.get(size-his_start);
																buf.append(" and I9999<=? ");
																paralist.add(curri99992);
																if(size<his_start+ncount) {
                                                                    curri9999=(String)i9999list.get(0);
                                                                } else {
                                                                    curri9999=(String)i9999list.get(size-ncount-(his_start-1));
                                                                }
																buf.append(" and I9999>=? order by I9999");

															}else{
																 curri9999=(String)i9999list.get(size-his_start);
																buf.append(" and I9999<=? order by I9999");
															}

														}
													}
													paralist.add(curri9999);
													break;
												case 2://正数第...条(最初第)
													if(size>=ncount) {
                                                        curri9999=(String)i9999list.get(ncount-1);
                                                    }
													buf.append(" and I9999=?");
													paralist.add(curri9999);
													break;
												case 3://正数...条（最初）
													if(his_start==0){
														if(size>=ncount){
															curri9999=(String)i9999list.get(ncount-1);
														buf.append(" and I9999<=? order by I9999");
														}else{
															buf.append(" and I9999>=? order by I9999");
														}
													}else{
														if(his_start>size){
															curri9999="-1";
															buf.append(" and I9999<=? order by I9999");
														}else{
															if(size>=ncount){
																String curri99992=(String)i9999list.get(his_start-1);
																buf.append(" and I9999>=? ");
																paralist.add(curri99992);
																if(size<his_start+ncount){
																	buf.append(" and I9999>=? ");
																	curri9999=curri99992;
																}else{
																curri9999=(String)i9999list.get(his_start+ncount-2);
																buf.append(" and I9999<=? order by I9999");
																}
															}else{
																 curri9999=(String)i9999list.get(his_start-1);
																	buf.append(" and I9999>=? order by I9999");
															}

														}
													}
													paralist.add(curri9999);
													break;
												}
											}
											ArrayList reclist=getRecordValue(fieldlist,dao.search(buf.toString(),paralist));
											buf.setLength(0);
											for(int n=0;n<reclist.size();n++)
											{
												buf.append(((ArrayList)reclist.get(n)).get(1).toString().replaceAll("`", "^^"));
												if(n<reclist.size()-1){
													buf.append("`");
												}
											}
											//更新记录

											strUpdate2.setLength(0);
											strUpdate2.append("Update ");
											strUpdate2.append(strDesT);
											strUpdate2.append(" set ");
											strUpdate2.append(field_name);
											strUpdate2.append(" ='"+buf.toString());
											strUpdate2.append("' where ");
											strUpdate2.append(paramname);
											strUpdate2.append(" = ");
											strUpdate2.append("'"+a0110list.get(m)+"'");
											if(this.infor_type==1&&dbpre.length()>0) {
                                                strUpdate2.append(" and basepre ='"+dbpre+"'");
                                            }
											dao.update(strUpdate2.toString());
											flag =true;
									}
									break;
								}
								//?oracle db2
								if(db_type==2||db_type==3)
								{
									//strUpdate.append(")");//一个一个人单独处理吧.for 按当前记录导入
									strUpdate.append(" and (U.I9999=(select Max(I9999) from ");
									strUpdate.append(strSrcT);
									strUpdate.append(" where ");
									strUpdate.append(strSrcT);
									strUpdate.append(".A0100=U.A0100) or U.I9999 is null) "); // and (T.state<>1 or T.state is null)");
									strUpdate.append(") where T.A0100 in (");
									strUpdate.append(a0100s);
									strUpdate.append(") and basepre='");
									strUpdate.append(dbpre);
									strUpdate.append("'");
								}
								else//MSSQL
								{
									strUpdate.append(" where U.I9999=(select min(I9999) from ");
									strUpdate.append(strSrcT);
									strUpdate.append(" where I9999 in (select top ");
									strUpdate.append(ncount);
									strUpdate.append(" I9999 from ");
									strUpdate.append(strSrcT);
									strUpdate.append(" where U.A0100=");
									strUpdate.append(strSrcT);
									strUpdate.append(".A0100 order by I9999 ");

									switch(nmode)
									{
									case 0://最近第
										strUpdate.append(" desc ");
										break;
									default://最初第
										strUpdate.append(" asc ");
										break;
									}
									strUpdate.append(") and U.A0100=");
									strUpdate.append(strSrcT);
                									strUpdate.append(".A0100)");
								}
							}
							else//历史记录 以后不走了 wangrd 2015-03-20
							{
								//?oracle db2
								if(db_type==2||db_type==3)
								{
									//strUpdate.append(")");//一个一个人单独处理吧.for 按当前记录导入
									strUpdate.append(" and (U.I9999=(select Max(I9999) from ");
									strUpdate.append(strSrcT);
									strUpdate.append(" where ");
									strUpdate.append(strSrcT);
									strUpdate.append(".A0100=U.A0100) or U.I9999 is null) "); // and (T.state<>1 or T.state is null)");
									strUpdate.append(") where T.A0100 in (");
									strUpdate.append(a0100s);
									strUpdate.append(") and basepre='");
									strUpdate.append(dbpre);
									strUpdate.append("'");
								}
								else//MSSQL
								{
									strUpdate.append(" where U.I9999=(select min(I9999) from ");
									strUpdate.append(strSrcT);
									strUpdate.append(" where I9999 in (select top ");
									strUpdate.append(ncount);
									strUpdate.append(" I9999 from ");
									strUpdate.append(strSrcT);
									strUpdate.append(" where U.A0100=");
									strUpdate.append(strSrcT);
									strUpdate.append(".A0100 order by I9999 ");
									switch(nmode)
									{
									case 0://最近第
										strUpdate.append(" desc ");
                                                        break;
                                                    default://最初第
										strUpdate.append(" asc ");
                                                        break;
									}
									strUpdate.append(") and U.A0100=");
                                                strUpdate.append(strSrcT);
									strUpdate.append(".A0100)");
								}
                                    }
                                }
            					break;
                            case 'B'://单位信息
					case 'b':
						if(db_type==2||db_type==3) //oracle,db2
						{
							strUpdate.append("update ");
							strUpdate.append(strDesT);
							strUpdate.append(" T set (");
							strUpdate.append(fieldstr);
							strUpdate.append(")=(select ");
							strUpdate.append(fieldstr1);
							strUpdate.append(" from ");
							strUpdate.append(strSrcT);
							if(this.infor_type==2) {
                                strUpdate.append(" U Where T.B0110=U.B0110");
                            } else {
                                strUpdate.append(" U Where T.B0110_1=U.B0110");
                            }
						}
						else
						{
							strUpdate.append("Update T set ");
							strUpdate.append(fieldstr);
							strUpdate.append(" from ");
							strUpdate.append(strDesT);
							strUpdate.append(" T Left join ");
							strUpdate.append(strSrcT);
							if(this.infor_type==2) {
                                strUpdate.append(" U ON T.B0110=U.B0110");
                            } else {
                                strUpdate.append(" U ON T.B0110_1=U.B0110");
                            }
						}

						if("B01".equalsIgnoreCase(setname))
						{
								if(db_type==2||db_type==3) {
                                    strUpdate.append(" ) ") ;
                                }
								if(this.infor_type==2)
								{
									strUpdate.append(" where T.B0110 in (");
                                                        strUpdate.append(a0100s);
                                    					strUpdate.append(") ");
								}
								else
								{
									strUpdate.append(" where T.A0100 in (");
									strUpdate.append(a0100s);
									strUpdate.append(") and basepre='");
									strUpdate.append(dbpre);
									strUpdate.append("'");
								}
						}
						else
						{
								if(db_type==2||db_type==3)
								{
									strUpdate.append(" and (U.I9999=(select Max(I9999) from ");
								}
                                                    else
                                                    {
                                                        strUpdate.append(" where (U.I9999=(select Max(I9999) from ");
								}
								strUpdate.append(strSrcT);
								strUpdate.append(" where ");
								strUpdate.append(strSrcT);
								strUpdate.append(".B0110=U.B0110) or U.I9999 is null) "); // and (T.state<>1 or T.state is null)");
                                            //	strUpdate.append(") ");
								if(db_type==2||db_type==3) {
                                    strUpdate.append(" ) where ");
                                } else {
                                    strUpdate.append(" and ");
                                }

								if(this.infor_type==2)
								{
									strUpdate.append(" T.B0110 in (");
									strUpdate.append(a0100s);
									strUpdate.append(") ");
								}
								else
								{
									strUpdate.append(" T.A0100 in (");
									strUpdate.append(a0100s);
									strUpdate.append(") and basepre='");
									strUpdate.append(dbpre);
									strUpdate.append("'");
								}
						}
						break;
					case 'K'://职位信息
					case 'k':

						if(db_type==2||db_type==3) //oracle,db2
						{
							strUpdate.append("update ");
							strUpdate.append(strDesT);
							strUpdate.append(" T set (");
							strUpdate.append(fieldstr);
							strUpdate.append(")=(select ");
							strUpdate.append(fieldstr1);
							strUpdate.append(" from ");
							strUpdate.append(strSrcT);

							if(this.infor_type==3) {
                                strUpdate.append(" U Where T.E01A1=U.E01A1");
                            } else {
                                strUpdate.append(" U Where T.E01A1_1=U.E01A1");
                            }
						}
						else
						{
							strUpdate.append("Update T set ");
							strUpdate.append(fieldstr);
							strUpdate.append(" from ");
							strUpdate.append(strDesT);
							strUpdate.append(" T Left join ");
							strUpdate.append(strSrcT);

							if(this.infor_type==3) {
                                strUpdate.append(" U ON T.E01A1=U.E01A1");
                            } else {
                                strUpdate.append(" U ON T.E01A1_1=U.E01A1");
                            }
						}
						if("K01".equalsIgnoreCase(setname))
						{
                                   if(db_type==2||db_type==3) {
                                   strUpdate.append(" ) ") ;
                       }

                              if(this.infor_type==3)
                              {
								    strUpdate.append(" where T.E01A1 in (");
									strUpdate.append(a0100s);
									strUpdate.append(") ");
							   }
							   else
							   {
									strUpdate.append(" where T.A0100 in (");
									strUpdate.append(a0100s);
									strUpdate.append(") and basepre='");
									strUpdate.append(dbpre);
									strUpdate.append("'");
							   }
						}
						else
						{
								if(db_type==2||db_type==3)
								{
									strUpdate.append(" and (U.I9999=(select Max(I9999) from ");
								}
								else
								{
									strUpdate.append(" where (U.I9999=(select Max(I9999) from ");
								}
								strUpdate.append(strSrcT);
								strUpdate.append(" where ");
								strUpdate.append(strSrcT);
								strUpdate.append(".E01A1=U.E01A1) or U.I9999 is null) "); // and (T.state<>1 or T.state is null)");
							//	strUpdate.append(") ");
								if(db_type==2||db_type==3) {
                                    strUpdate.append(" ) where ");
                                } else {
                              strUpdate.append(" and ");
                                }
								 if(this.infor_type==3)
								 {
									 strUpdate.append(" T.E01A1 in (");
									 strUpdate.append(a0100s);
									 strUpdate.append(")  ");
								 }
								 else
								 {
									strUpdate.append(" T.A0100 in (");
									strUpdate.append(a0100s);
									strUpdate.append(") and basepre='");
									strUpdate.append(dbpre);
									strUpdate.append("'");
								 }
						}
						break;
					}

					StringBuffer task_str=new StringBuffer("");
					if(getTasklist().size()>0)
					{
						for(int j=0;j<this.tasklist.size();j++)
						{
							if(this.tasklist.get(j)!=null&&((String)this.tasklist.get(j)).trim().length()>0) {
                                task_str.append(","+(String)this.tasklist.get(j));
                            }
						}
						//在templet_tabid表中task_id的值为第一次报批时的值，它不会根据流程的流转而发生变化，所以要根据task_id的值定位templet_tabid表中的数据需要根据t_wf_task_objlink表进行关联查询，liuzy 20150907
						strUpdate.append(" and exists (SELECT null from t_wf_task_objlink TT where TT.INS_ID=t.INS_ID and TT.SEQNUM=T.SEQNUM and TT.TASK_ID in ("+task_str.substring(1)+"))");
						if(flag) {
                            continue;
                        }
						dao.update(strUpdate.toString());
					}

				}//while rset loop end.
			}//for i loop end.

			/**导入插入子集区域的数据*/
			if(isImpDataFromArchive_sub)
			{
				strsql.setLength(0);
				if(db_type==2)//oracle
                {
                    strsql.append("select T.setname,T.ChgState,T.formula,T.hismode,T.rcount,T.mode_o,T.subflag,T.sub_domain from template_set T  where ");
                } else {
                    strsql.append("select T.setname,T.ChgState,T.formula,T.hismode,T.rcount,T.mode,T.subflag,T.sub_domain from template_set T  where ");
                }
				strsql.append(" T.tabid=");
				strsql.append(this.tabid);
				strsql.append(" and ");
				strsql.append(" subflag=1");
				rset=dao.search(strsql.toString());
				while(rset.next())
				{
					strUpdate.setLength(0);
					nchgstate=rset.getInt("chgstate");
					if(db_type==2)//oracle
                    {
                        nmode=rset.getInt("mode_o");
                    } else {
  nmode=rset.getInt("mode");
                    }
					ncount=rset.getInt("rcount");
            nhismode=rset.getInt("hismode");
cname=rset.getString("setname");
					field_name="t_"+cname+"_"+nchgstate;
					/**导入插入子集区域的数据*/
            String subxml=Sql_switcher.readMemo(rset, "sub_domain");
String formula=Sql_switcher.readMemo(rset, "formula");

/**对插入子集区域数据同步规则，变化前或第一次导入时*/
					if(sync==0||nchgstate==1){
						//获得sub_domain_id
                    String sub_domain_id="";
						//获得第x到y中的x值
						String his_start2="";
						int his_start =0;
						if(subxml!=null&&subxml.trim().length()>0){
							try{
									doc=PubFunc.generateDom(subxml);;
									XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
									List childlist=findPath.selectNodes(doc);
									if(childlist!=null&&childlist.size()>0)
									{
                                                element=(Element)childlist.get(0);
                                                if(element.getAttributeValue("id")!=null&&"1".equals(""+rset.getInt("ChgState"))){
										sub_domain_id=(String)element.getAttributeValue("id");
										if(sub_domain_id!=null&&sub_domain_id.trim().length()>0){
										}else{
											sub_domain_id="";
										}
										}
										if(element.getAttributeValue("his_start")!=null){
											his_start2=(String)element.getAttributeValue("his_start");
											if(his_start2!=null&&his_start2.trim().length()>0){
											}else{
												his_start2="";
											}
											}

									}

							}catch(Exception e){

							}
						}
						if(his_start2.length()>0) {
                            his_start = Integer.parseInt(his_start2);
                        }
						if(sub_domain_id!=null&&sub_domain_id.length()>0) {
  field_name="t_"+cname+"_"+sub_domain_id+"_"+nchgstate;
                        }
						impSubDomainData(a0100s,dbpre,subxml,cname,field_name.toLowerCase(),nhismode,nmode,ncount,formula,his_start);
					}
				}//while rset loop end.
			}
			/**导入插入子集区域的数据区域结束*/
			/**生成序号*/
			/**首次导入时,才需要进行序号生成*/
if(sync==0){
				if(!"1".equals(this.id_gen_manual)) {
                    createRuleSequenceNo(seqHm,a0100s,dbpre,strDesT);
                }
			}
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}


	/**
	 * 导入单位数据
	 * @param strSrcT
	 * @return
	 */
	public String  impDataFromBKarchive(String strSrcT,int db_type,String strDesT,String fieldstr,String fieldstr1,String setname,String a0100s,int ncount,int nmode,int nhismode,String formula,ContentDAO dao,ArrayList a0110list,String cname,ArrayList fieldlist,String field_name,String paramname,String dbpre,int his_start)
	{
		StringBuffer strUpdate=new StringBuffer("");
		try
		{
			String key_str="B0110";
			if(this.infor_type==3) {
                key_str="E01A1";
            }

			if(db_type==2||db_type==3) //oracle,db2
			{
				strUpdate.append("update ");
				strUpdate.append(strDesT);
				strUpdate.append(" T set (");
				strUpdate.append(fieldstr);
				strUpdate.append(")=(select ");
				strUpdate.append(fieldstr1);
				strUpdate.append(" from ");
				strUpdate.append(strSrcT);
				strUpdate.append(" U Where T."+key_str+"=U."+key_str+"");
			}
			else
			{
				strUpdate.append("Update T set ");
				strUpdate.append(fieldstr);
				strUpdate.append(" from ");
				strUpdate.append(strDesT);
				strUpdate.append(" T Left join ");
				strUpdate.append(strSrcT);
				strUpdate.append(" U ON T."+key_str+"=U."+key_str+"");
			}
			if("B01".equalsIgnoreCase(setname)|| "K01".equalsIgnoreCase(setname))
			{
				if(db_type==2||db_type==3)
				{
					strUpdate.append(") where T."+key_str+" in (");
					strUpdate.append(a0100s);
					strUpdate.append(") ");
				}
				else
				{
					strUpdate.append(" where T."+key_str+" in (");
					strUpdate.append(a0100s);
					strUpdate.append(") ");
				}
			}
			else
			{
				if(nhismode==1) //当前记录
				{
					if(db_type==2||db_type==3)
					{
						strUpdate.append(" and (U.I9999=(select Max(I9999) from ");
						strUpdate.append(strSrcT);
						strUpdate.append(" where ");
						strUpdate.append(strSrcT);
						strUpdate.append("."+key_str+"=U."+key_str+") or U.I9999 is null) "); // and (T.state<>1 or T.state is null)");
						strUpdate.append(") where T."+key_str+" in (");
						strUpdate.append(a0100s);
						strUpdate.append(") ");

					}
					else
					{
						strUpdate.append(" where (U.I9999=(select Max(I9999) from ");
						strUpdate.append(strSrcT);
						strUpdate.append(" where ");
						strUpdate.append(strSrcT);
						strUpdate.append("."+key_str+"=U."+key_str+") or U.I9999 is null) "); // and (T.state<>1 or T.state is null)");
						strUpdate.append(" and T."+key_str+" in (");
						strUpdate.append(a0100s);
						strUpdate.append(")  ");

					}
				}
				else if(nhismode==3) //条件定位
				{
					 //  <EXPR>1</EXPR><FACTOR>A0420=01</FACTOR>
					String expr="";
					String factor="";
					if(formula.trim().length()>0)
					{
						int f=formula.indexOf("<EXPR>");
						int t=formula.indexOf("</EXPR>");
						expr=formula.substring(f+6,t);
						f=formula.indexOf("<FACTOR>");
						t=formula.indexOf("</FACTOR>");
						factor=formula.substring(f+8,t);
					}
					factor=factor.replaceAll(",","`");
					factor=factor+"`";

					FactorList factorlist=new FactorList(expr,factor,"");
					String strw=factorlist.getSingleTableSqlExpression("F");

					//update sutemplet_12 T set (T.A0405_1)=
				//	  (select U.A0405 from usrA04 U Where T.A0100=U.A0100
				//			     and U.i9999=(select max(F.i9999) from usrA04 F where U.a0100=F.a0100 and F.C0407='1' ) )
					//写入临时表里

					StringBuffer strUpdate2 = new StringBuffer();
					if(a0110list.size()>0){
						for(int m =0;m<a0110list.size();m++){
					StringBuffer buf = new StringBuffer();
					buf.append("select ");
					buf.append("F."+cname);
					buf.append(",F.i9999");
					buf.append(" from ");
					buf.append(strSrcT+" F ");
					if(this.infor_type==1) {
                        buf.append(" where F.a0100='"+a0110list.get(m)+"'");
                    } else if(this.infor_type==2) {
                        buf.append(" where F.b0110='"+a0110list.get(m)+"'");
                    } else if(this.infor_type==3) {
                        buf.append(" where F.e01a1='"+a0110list.get(m)+"'");
                    }
					if(strw.trim().length()>0) {
                        buf.append(" and ("+strw+") ");
                    }
					//更新记录
					ArrayList reclist=getRecordValue(fieldlist,dao.search(buf.toString()));
					buf.setLength(0);
					for(int n=0;n<reclist.size();n++)
					{
						buf.append(((ArrayList)reclist.get(n)).get(1).toString().replaceAll("`", "^^"));
						if(n<reclist.size()-1){
							buf.append("`");
						}
					}
					strUpdate2.setLength(0);
					strUpdate2.append("Update ");
					strUpdate2.append(strDesT);
					strUpdate2.append(" set ");
					strUpdate2.append(field_name);
					strUpdate2.append(" ='"+buf.toString());
					strUpdate2.append("' where ");
					strUpdate2.append(paramname);
					strUpdate2.append(" = ");
					strUpdate2.append("'"+a0110list.get(m)+"'");
					if(this.infor_type==1&&dbpre.length()>0) {
                        strUpdate2.append(" and basepre ='"+dbpre+"'");
                    }
					dao.update(strUpdate2.toString());
						}
					}
					strUpdate.setLength(0);
//					strUpdate.append(" and  (U.i9999=(select max(F.i9999) from "+strSrcT+" F where U."+key_str+"=F."+key_str+" and "+strw+" ) ");
//
//				//	strUpdate.append(" and "+strw);
//					strUpdate.append(" ) ");
//					if(db_type==2||db_type==3) //oracle,db2
//						strUpdate.append(" ) ");
//					strUpdate.append(" where T."+key_str+" in (");
//					strUpdate.append(a0100s);
//					strUpdate.append(")  ");
				}
				else if(nhismode==4) //条件序号
				{
					 //  <EXPR>1</EXPR><FACTOR>A0420=01</FACTOR>
					String expr="";
					String factor="";
					if(formula.trim().length()>0)
					{
						int f=formula.indexOf("<EXPR>");
						int t=formula.indexOf("</EXPR>");
						expr=formula.substring(f+6,t);
						f=formula.indexOf("<FACTOR>");
						t=formula.indexOf("</FACTOR>");
						factor=formula.substring(f+8,t);
					}
					factor=factor.replaceAll(",","`");
					factor=factor+"`";

					FactorList factorlist=new FactorList(expr,factor,"");
					String strw=factorlist.getSingleTableSqlExpression("F");

					//update sutemplet_12 T set (T.A0405_1)=
				//	  (select U.A0405 from usrA04 U Where T.A0100=U.A0100
				//			     and U.i9999=(select max(F.i9999) from usrA04 F where U.a0100=F.a0100 and F.C0407='1' ) )
					//写入临时表里

					StringBuffer strUpdate2 = new StringBuffer();
					if(a0110list.size()>0){
						for(int m =0;m<a0110list.size();m++){

					StringBuffer buf = new StringBuffer();
					buf.append("select ");
					buf.append("F."+cname);
					buf.append(",F.i9999");
					buf.append(" from ");
					buf.append(strSrcT+" F ");
					if(this.infor_type==1) {
                        buf.append(" where F.a0100='"+a0110list.get(m)+"'");
                    } else if(this.infor_type==2) {
                        buf.append(" where F.b0110='"+a0110list.get(m)+"'");
                    } else if(this.infor_type==3) {
                        buf.append(" where F.e01a1='"+a0110list.get(m)+"'");
                    }
					if(strw.trim().length()>0) {
                        buf.append(" and ("+strw+") ");
                    }
					ArrayList i9999list=getSubSetI9999s(buf.toString()+" order by F.i9999");
					ArrayList paralist = new ArrayList();
					int size=i9999list.size();
					if(size>0)
					{
					/**初值为-1*/
					String curri9999="-1";
					switch(nmode)
					{
					case 0://倒数第...条（最近第）

							if(size>=ncount)//子集记录大于要取的的记录数
							{
								if(size==ncount) {
                                    curri9999=(String)i9999list.get(0);
                                } else
								{
									if(ncount!=0) {
                                        curri9999=(String)i9999list.get(size-ncount);
                                    } else {
                                        curri9999=(String)i9999list.get(size-ncount-1);
                                    }
								}
							}

							buf.append(" and F.I9999=?");
						paralist.add(curri9999);
						break;
					case 1://倒数...条（最近）
						if(his_start==0){
							if(size>=ncount)
							{
								if(size==ncount) {
                                    curri9999=(String)i9999list.get(0);
                                } else {
                                    curri9999=(String)i9999list.get(size-ncount);
                                }
							}
							buf.append(" and F.I9999>=? order by F.I9999");
						}else{
							if(his_start>size){
								curri9999="-1";
								buf.append(" and F.I9999<=? order by F.I9999");
							}else{
								if(size>=ncount){
									String curri99992=(String)i9999list.get(size-his_start);
									buf.append(" and F.I9999<=? ");
									paralist.add(curri99992);
									if(size<his_start+ncount) {
                                        curri9999=(String)i9999list.get(0);
                                    } else {
                                        curri9999=(String)i9999list.get(size-ncount-(his_start-1));
                                    }
									buf.append(" and F.I9999>=? order by F.I9999");

								}else{
									 curri9999=(String)i9999list.get(size-his_start);
									buf.append(" and F.I9999<=? order by F.I9999");
								}

							}
        }
        paralist.add(curri9999);
						break;
					case 2://正数第...条(最初第)
if(size>=ncount) {
       curri9999=(String)i9999list.get(ncount-1);
                        }
buf.append(" and F.I9999=?");
				paralist.add(curri9999);
						break;
					case 3://正数...条（最初）
    if(his_start==0){
							if(size>=ncount){
								curri9999=(String)i9999list.get(ncount-1);
							buf.append(" and F.I9999<=? order by F.I9999");
							}else{
								buf.append(" and F.I9999>=? order by F.I9999");
							}
						}else{
							if(his_start>size){
								curri9999="-1";
								buf.append(" and F.I9999<=? order by F.I9999");
							}else{
								if(size>=ncount){
									String curri99992=(String)i9999list.get(his_start-1);
									buf.append(" and F.I9999>=? ");
									paralist.add(curri99992);
									if(size<his_start+ncount){
										buf.append(" and I9999>=? ");
										curri9999=curri99992;
									}else{
									curri9999=(String)i9999list.get(his_start+ncount-2);
									buf.append(" and F.I9999<=? order by F.I9999");
									}
								}else{
									 curri9999=(String)i9999list.get(his_start-1);
										buf.append(" and F.I9999>=? order by F.I9999");
								}

							}
						}
						paralist.add(curri9999);
						break;
					}
					}

					//更新记录
					ArrayList reclist=getRecordValue(fieldlist,dao.search(buf.toString(),paralist));
					buf.setLength(0);
					for(int n=0;n<reclist.size();n++)
					{
						buf.append(((ArrayList)reclist.get(n)).get(1).toString().replaceAll("`", "^^"));
						if(n<reclist.size()-1){
							buf.append("`");
						}
					}
					strUpdate2.setLength(0);
					strUpdate2.append("Update ");
					strUpdate2.append(strDesT);
					strUpdate2.append(" set ");
					strUpdate2.append(field_name);
					strUpdate2.append(" ='"+buf.toString());
					strUpdate2.append("' where ");
					strUpdate2.append(paramname);
					strUpdate2.append(" = ");
					strUpdate2.append("'"+a0110list.get(m)+"'");
					if(this.infor_type==1&&dbpre.length()>0) {
                        strUpdate2.append(" and basepre ='"+dbpre+"'");
                    }
					dao.update(strUpdate2.toString());
						}
					}
					strUpdate.setLength(0);
				}
				else if(nhismode==2){

					//查找库中的数据个数
					StringBuffer strUpdate2 = new StringBuffer();
					if(a0110list.size()>0){
						for(int m =0;m<a0110list.size();m++){
							/**求子集序号列表*/
							ArrayList i9999list=getSubSetI9999s(strSrcT,""+a0110list.get(m));

								int size=i9999list.size();
								StringBuffer buf = new StringBuffer();
								ArrayList paralist = new ArrayList();
								buf.append("select ");
								buf.append(cname);
								buf.append(",i9999");
								buf.append(" from ");
								buf.append(strSrcT);
								if(this.infor_type==1) {
                                    buf.append(" where a0100=?");
                                } else if(this.infor_type==2) {
                                    buf.append(" where b0110=?");
                                } else if(this.infor_type==3) {
                                    buf.append(" where e01a1=?");
                                }
								paralist.add(""+a0110list.get(m));
								if(size>0)
								{
									/**初值为-1*/
									String curri9999="-1";
									switch(nmode)
									{
                            case 0://倒数第...条（最近第）

                            		if(size>=ncount)//子集记录大于要取的的记录数
								{
												if(size==ncount) {
                                                    curri9999=(String)i9999list.get(0);
                                                } else
												{
													if(ncount!=0) {
                                                        curri9999=(String)i9999list.get(size-ncount);
                                                    } else {
                                                        curri9999=(String)i9999list.get(size-ncount-1);
                                                    }
												}
											}

											buf.append(" and I9999=?");
                            paralist.add(curri9999);
										break;
									case 1://倒数...条（最近）
                if(his_start==0){
											if(size>=ncount)
                            {
                                if(size==ncount) {
                                                    curri9999=(String)i9999list.get(0);
                                                } else {
                                                    curri9999=(String)i9999list.get(size-ncount);
                                                }
											}
											buf.append(" and I9999>=? order by I9999");
										}else{
											if(his_start>size){
												curri9999="-1";
												buf.append(" and I9999<=? order by I9999");
											}else{
												if(size>=ncount){
													String curri99992=(String)i9999list.get(size-his_start);
													buf.append(" and I9999<=? ");
													paralist.add(curri99992);
													if(size<his_start+ncount) {
                                                        curri9999=(String)i9999list.get(0);
                                                    } else {
                                                        curri9999=(String)i9999list.get(size-ncount-(his_start-1));
                                                    }
													buf.append(" and I9999>=? order by I9999");

												}else{
													 curri9999=(String)i9999list.get(size-his_start);
													buf.append(" and I9999<=? order by I9999");
												}

											}
										}
										paralist.add(curri9999);
										break;
                            case 2://正数第...条(最初第)
    							if(size>=ncount) {
                                            curri9999=(String)i9999list.get(ncount-1);
                                        }
										buf.append(" and I9999=?");
										paralist.add(curri9999);
										break;
									case 3://正数...条（最初）
										if(his_start==0){
											if(size>=ncount){
												curri9999=(String)i9999list.get(ncount-1);
											buf.append(" and I9999<=? order by I9999");
											}else{
												buf.append(" and I9999>=? order by I9999");
											}
										}else{
											if(his_start>size){
												curri9999="-1";
												buf.append(" and I9999<=? order by I9999");
											}else{
												if(size>=ncount){
													String curri99992=(String)i9999list.get(his_start-1);
													buf.append(" and I9999>=? ");
													paralist.add(curri99992);
													if(size<his_start+ncount){
														buf.append(" and I9999>=? ");
														curri9999=curri99992;
													}else{
													curri9999=(String)i9999list.get(his_start+ncount-2);
													buf.append(" and I9999<=? order by I9999");
													}
												}else{
													 curri9999=(String)i9999list.get(his_start-1);
														buf.append(" and I9999>=? order by I9999");
												}

											}
										}
										paralist.add(curri9999);
										break;
									}
								}
								ArrayList reclist=getRecordValue(fieldlist,dao.search(buf.toString(),paralist));
								buf.setLength(0);
								for(int n=0;n<reclist.size();n++)
								{
									buf.append(((ArrayList)reclist.get(n)).get(1).toString().replaceAll("`", "^^"));
									if(n<reclist.size()-1){
										buf.append("`");
									}
								}
								//更新记录

								strUpdate2.setLength(0);
								strUpdate2.append("Update ");
								strUpdate2.append(strDesT);
								strUpdate2.append(" set ");
								strUpdate2.append(field_name);
								strUpdate2.append(" ='"+buf.toString());
								strUpdate2.append("' where ");
								strUpdate2.append(paramname);
								strUpdate2.append(" = ");
								strUpdate2.append("'"+a0110list.get(m)+"'");
                    if(this.infor_type==1&&dbpre.length()>0) {
                                    strUpdate2.append(" and basepre ='"+dbpre+"'");
                                }
								dao.update(strUpdate2.toString());
						}

					}
					strUpdate.setLength(0);
				}
				else//历史记录
				{
					//?oracle db2
					if(db_type==2||db_type==3)
					{
						//strUpdate.append(")");//一个一个人单独处理吧.for 按当前记录导入
						strUpdate.append(" and (U.I9999=(select Max(I9999) from ");
						strUpdate.append(strSrcT);
						strUpdate.append(" where ");
						strUpdate.append(strSrcT);
						strUpdate.append("."+key_str+"=U."+key_str+") or U.I9999 is null) "); // and (T.state<>1 or T.state is null)");
						strUpdate.append(") where T."+key_str+" in (");
						strUpdate.append(a0100s);
						strUpdate.append(")  ");

					}
					else//MSSQL
					{
strUpdate.append(" where U.I9999=(select min(I9999) from ");
						strUpdate.append(strSrcT);
	strUpdate.append(" where I9999 in (select top ");
strUpdate.append(ncount);
						strUpdate.append(" I9999 from ");
						strUpdate.append(strSrcT);
						strUpdate.append(" where U."+key_str+"=");
						strUpdate.append(strSrcT);
						strUpdate.append("."+key_str+" order by I9999 ");
						switch(nmode)
						{
						case 0://最近第
							strUpdate.append(" desc ");
							break;
						default://最初第
							strUpdate.append(" asc ");
		break;
						}
						strUpdate.append(") and U."+key_str+"=");
						strUpdate.append(strSrcT);
strUpdate.append("."+key_str+")");
					}
}
}
}
catch(Exception e)
{
e.printStackTrace();
		}
		return strUpdate.toString();
	}
	   /**
     * @Title: updateWorkCodeState
     * @Description: 撤销任务时 将工号表里的工号更改为未使用 汉口银行、长安保险用
     * @param @param tableName
     * @param @param strWhere
     * @return void
     * @author wangrd
     * @throws
    */
    public void updateWorkCodeState(String tableName,String strWhere){
try {
            //汉口银行
            String workcode_Tab="staff_id_pool";//工号表
            String staffid_set="";
            staffid_set=SystemConfig.getPropertyValue("staffid_set");
            staffid_set = staffid_set==null?"":staffid_set;
            if ("".equals(staffid_set.trim())){//没有配置汉口银行 再检查长安保险是否配置
                //长安保险
                staffid_set=SystemConfig.getPropertyValue("jobnumber_set");
                staffid_set = staffid_set==null?"":staffid_set;
                workcode_Tab="work_code_pool";
            }

            String [] arrStr = staffid_set.split(":");
            if (arrStr.length<2) {return;}
String tab_id = arrStr[0];
            String fieldName = arrStr[1].toLowerCase();
            if ("".equals(tab_id )||("".equals(fieldName))) {return;}

            if((","+tab_id+",").indexOf(","+this.tabid+",")==-1){ return; }
       //   if (Integer.parseInt(tab_id)!=this.tabid){return;}
            DbWizard dbw=new DbWizard(this.conn);
            if (!dbw.isExistField(tableName, fieldName)){return;}

            String strsql="";
strsql="select "+fieldName+" from "+tableName;
            if (!"".equals(strWhere.trim())){
                strsql = strsql +" where " +strWhere;
            }
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rowSet=dao.search(strsql);
            while(rowSet.next()){
                 String workId=rowSet.getString(fieldName);
                 if ((workId!=null)&&(!"".equals(workId.trim()))){
                     if ("staff_id_pool".equalsIgnoreCase(workcode_Tab)){
                         strsql ="update staff_id_pool Set is_used =0,create_time=null where Staff_id ='"+ workId+"'"
                              +" and is_used ='2'";
                     }
                     else {
                         strsql ="update jobnumber.work_code_pool Set is_used =0,create_date=null where work_code ='"+ workId+"'"
                         +" and is_used ='1'";
                     }
                     dao.update(strsql);
                 }
            }
            PubFunc.closeDbObj(rowSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * 更新组织信息
	 * @param field_name
	 * @param cname
	 * @param a0100s
	 */
	public void updateOrgInfo(String field_name,String cname,String a0100s,String strDesT)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="";
			String key="b0110";
			if(this.infor_type==3) {
                key="e01a1";
            }
			if("codesetid".equalsIgnoreCase(cname))
			{
				 sql="update "+strDesT+" set "+field_name+"=(select codesetid from organization where organization.codeitemid="+strDesT+"."+key+") ";
			}
			else if("codeitemdesc".equalsIgnoreCase(cname))
			{
				 sql="update "+strDesT+" set "+field_name+"=(select codeitemdesc from organization where organization.codeitemid="+strDesT+"."+key+") ";
			}
			else if("corcode".equalsIgnoreCase(cname))
			{
				 sql="update "+strDesT+" set "+field_name+"=(select corCode from organization where organization.codeitemid="+strDesT+"."+key+") ";
			}
			else if("parentid".equalsIgnoreCase(cname))
			{
				 sql="update "+strDesT+" set "+field_name+"=(select parentid from organization where organization.codeitemid="+strDesT+"."+key+") ";
}
else if("start_date".equalsIgnoreCase(cname))
			{
				 sql="update "+strDesT+" set "+field_name+"=(select start_date from organization where organization.codeitemid="+strDesT+"."+key+") ";
			}
			sql+=" where "+key+" in ("+a0100s+") ";
			dao.update(sql);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * 获得主集、子集下取最近值的指标集
	 * @param rset
	 * @param setname
	 * @return
	 */
	private String getSetCurrentItem(RowSet rset,String setname)
	{
		StringBuffer itemids=new StringBuffer("");
		try
		{
			String xpath="/sub_para/para";
			while(rset.next())
			{
				int nchgstate=rset.getInt("chgstate");
				int nhismode=rset.getInt("hismode");
				String cname=rset.getString("field_name");
				if(cname==null) {
                    continue;
                }
				String sub_domain = Sql_switcher.readMemo(rset,"sub_domain");
				//获得sub_domain_id
				String sub_domain_id="";
				if(sub_domain!=null&&sub_domain.trim().length()>0&&"1".equals(""+rset.getInt("ChgState"))){
					try{
							doc=PubFunc.generateDom(sub_domain);;
							XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							List childlist=findPath.selectNodes(doc);
							if(childlist!=null&&childlist.size()>0)
							{
								Element element=(Element)childlist.get(0);
								if(element.getAttributeValue("id")!=null){
								sub_domain_id=(String)element.getAttributeValue("id");
								if(sub_domain_id!=null&&sub_domain_id.trim().length()>0){
									sub_domain_id = "_"+sub_domain_id;
								}else{
									sub_domain_id="";
								}
								}
							}
					}catch(Exception e){

					}
				}
				String field_name=cname+sub_domain_id+"_"+nchgstate;
				String strSrcT=setname;
				FieldItem fielditem=null;
				if("codesetid".equalsIgnoreCase(cname)|| "codeitemdesc".equalsIgnoreCase(cname)|| "corcode".equalsIgnoreCase(cname)|| "parentid".equalsIgnoreCase(cname)|| "start_date".equalsIgnoreCase(cname))
				{
					fielditem=new FieldItem();
					fielditem.setItemid(cname);
					fielditem.setItemdesc(rset.getString("hz"));
					if(this.infor_type==2) {
                        fielditem.setFieldsetid("B01");
                    } else if(this.infor_type==3) {
                        fielditem.setFieldsetid("K01");
                    }
					if("start_date".equalsIgnoreCase(cname)) {
                        fielditem.setItemtype("D");
                    } else {
                        fielditem.setItemtype("A");
                    }
					if("parentid".equalsIgnoreCase(cname)) {
                        fielditem.setCodesetid("UM");
                    } else if("codesetid".equalsIgnoreCase(cname)) {
                        fielditem.setCodesetid("orgType");
                    } else {
                        fielditem.setCodesetid("0");
                    }
					if(!"start_date".equalsIgnoreCase(cname)) {
                        fielditem.setItemlength(50);
                    }
					fielditem.setUseflag("1");
				}
				else
				{
					fielditem=DataDictionary.getFieldItem(cname, setname);//.getFieldItem(cname); 20150708 dengcan

				}
				/**未构库或指标体系不存在时则退出*/
				if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                    continue;
                }
				if(nchgstate==2&&this.opinion_field!=null&&this.opinion_field.length()>0&&this.opinion_field.equalsIgnoreCase(fielditem.getItemid()))
				{
					continue;
				}
				if(nchgstate==2) {
                    nhismode=1;
                }

				if("A".equalsIgnoreCase(setname.substring(0,1))&&("A01".equalsIgnoreCase(setname)||nhismode==1))
				{//如果是（人员信息集&&一条记录）||人员主集
					if(itemids.indexOf(field_name+"=")!=-1) {
                        continue;
                    }

					itemids.append(field_name+"="+cname+",");
				}

			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return itemids.toString();
	}





	/**
	 * 从档案中导入数据
	 * @param a0100s  for examples '0100000','20202020' or 是一个SQL条件
	 * @param dbpre
	 * @param sync =0导入　　=1更新
	 * @return
	 * @throws GeneralException
	 */
	public boolean impDataFromArchive(String a0100s,String dbpre,int sync)throws GeneralException
	{
		TemplateBo templatebo=new TemplateBo(conn, userview, this.tabid);
		boolean bflag=true;
		int nmode=0,nhismode=0,ncount=0,nchgstate=0;
        String ErrorSetName="";//用于标志哪个子集的字段在更新的时出错（主要是数据类型出错）
        boolean ErrorSetFlag=false;//是否是由于更新子集信息字段时出的错
		try
		{
			/**导入*/
			if(sync==0) {
                impMainSetFromArchive(a0100s,dbpre);//导入主集中有的数据
            } else if(sync==1)//更新数据是更新照片 bug35130
            {
                impPhotoFromArchive2(a0100s,dbpre);
            }
			ArrayList setlist=searchUsedSetList();
			String setname=null;
			String cname=null;
			String field_name=null;
			StringBuffer strsql=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=null;
			RowSet rset2=null;
			int db_type=Sql_switcher.searchDbServer();//数据库类型
			String strDesT=null;
			if(isBEmploy())//员工通过自助平台发动申请
            {
                strDesT="g_templet_"+this.tabid;
            } else {
                strDesT=this.userview.getUserName()+"templet_"+this.tabid;
            }
			if(impOthTableName!=null&&impOthTableName.trim().length()>0)  //供高级花名册调用人事异动的人员引入功能，将数据导入到临时表中
            {
                strDesT=impOthTableName;
            }

			StringBuffer strUpdate=new StringBuffer();
			HashMap seqHm=new HashMap();//序号
			/**更新非插入子集区域的值*/
			Document doc=null;
			Element element=null;
			String xpath="/sub_para/para";
			//获得要更新的数据
			String paramname = "a0100";
			if(this.infor_type==2) {
                paramname="b0110";
            } else if(this.infor_type==3) {
                paramname="e01a1";
            }
//			rset = dao.search(" select "+paramname+" from  "+strDesT);
			ArrayList a0110list = new ArrayList();
			String arr0100s [] =a0100s.split(",");
			for(int a =0; a<arr0100s.length;a++){
				if(arr0100s[a].trim().length()>0){
					a0110list.add(arr0100s[a].trim().replace("'", ""));
				}
			}
//			while(rset.next()){
//				a0110list.add(rset.getString(paramname));
//			}

			for(int i=0;i<setlist.size();i++)//遍历所有的数据表  setlist如：[A01, A19, A55]
			{
				setname=(String)setlist.get(i);
				ErrorSetName=setname;
				strsql.setLength(0);
//				if(setname.equalsIgnoreCase("A01")||setname.equalsIgnoreCase("B01")||setname.equalsIgnoreCase("K01"))
//					continue;
				if(db_type==2)//oracle
				{	//strsql.append("select T.field_name,T.ChgState,T.hismode,T.rcount,T.mode_o,T.subflag,T.sub_domain from template_set T ,fielditem M where ");
	strsql.append("select  T.field_name,T.ChgState,T.hismode,T.rcount,T.mode_o,T.subflag,T.formula,T.Hz,T.sub_domain from template_set T  where ");
					//strsql.append("select distinct T.field_name,T.ChgState,T.hismode,T.rcount,T.mode_o,T.subflag from template_set T  where ");
				}
				else
				{
					//	strsql.append("select T.field_name,T.ChgState,T.hismode,T.rcount,T.mode,T.subflag,T.sub_domain from template_set T ,fielditem M where ");
					strsql.append("select  T.field_name,T.ChgState,T.hismode,T.rcount,T.mode,T.subflag,T.formula,T.Hz,T.sub_domain from template_set T where ");
					//strsql.append("select distinct T.field_name,T.ChgState,T.hismode,T.rcount,T.mode,T.subflag  from template_set T where ");
				}
				strsql.append(" T.tabid=");
				strsql.append(this.tabid);
//				strsql.append(" and T.subflag=0 and ((T.field_name=M.itemid and M.useflag<>'0') or (T.field_name='B0110' or T.field_name='E01A1'))");
				strsql.append(" and T.subflag=0 ");
				strsql.append(" and T.flag<>'H' ");
				if(sync==1) {
                    strsql.append(" and T.chgstate=1 ");
                } else
				{
					if("0".equals(this.change_after_get_data)) {
                        strsql.append(" and T.chgstate=1 ");
                    } else {
                        strsql.append(" and (T.chgstate=1 or T.chgstate=2) ");
                    }
				}
				if(this.infor_type==2&& "B01".equalsIgnoreCase(setname))
				{
					strsql.append(" and ( T.setname='"+setname+"' or T.field_name='codesetid'  or T.field_name='codeitemdesc'  or T.field_name='corcode'  or T.field_name='parentid'  or T.field_name='start_date'  ) ");
				}
				else if(this.infor_type==3&& "K01".equalsIgnoreCase(setname))
				{
					strsql.append(" and ( T.setname='"+setname+"' or T.field_name='codesetid'  or T.field_name='codeitemdesc'  or T.field_name='corcode'  or T.field_name='parentid'  or T.field_name='start_date'  ) ");
				}
				else
				{
					strsql.append(" and T.setname='");
					strsql.append(setname);
					strsql.append("'");
				}

				rset=dao.search(strsql.toString());
				String fieldstr="";
				String fieldstr1="";
				String fieldstr2_current="";
				String itemids=getSetCurrentItem(rset,setname); //取当前值指标
				rset.beforeFirst();
			while(rset.next())///遍历某个特定子集（主集）中sutemplet_20能用到的字段
				{
					strUpdate.setLength(0);
					nchgstate=rset.getInt("chgstate");
					if(db_type==2)//oracle
                    {
                        nmode=rset.getInt("mode_o");
                    } else {
                        nmode=rset.getInt("mode");
                    }
					ncount=rset.getInt("rcount");
					nhismode=rset.getInt("hismode");
					if(nchgstate==1&&nhismode==0){  //变化前等于0的情况不存在,但是存在的话就有可能是脏数据,这里处理一下，防止报错
					   nhismode=1;
					}
					cname=rset.getString("field_name");//指标名称（没有变化前变化后标志）
					String formula=Sql_switcher.readMemo(rset,"formula");
					if(cname==null) {
                        continue;
                    }
					String sub_domain = Sql_switcher.readMemo(rset,"sub_domain");
					//获得sub_domain_id
					String sub_domain_id="";
					//获得第x到y中的x值
					String his_start2="";
					int his_start =0;
					if(sub_domain!=null&&sub_domain.trim().length()>0&&"1".equals(""+rset.getInt("ChgState"))){
						try{
								doc=PubFunc.generateDom(sub_domain);;
								XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
								List childlist=findPath.selectNodes(doc);
								if(childlist!=null&&childlist.size()>0)
								{
									element=(Element)childlist.get(0);
									if(element.getAttributeValue("id")!=null){
									sub_domain_id=(String)element.getAttributeValue("id");
									if(sub_domain_id!=null&&sub_domain_id.trim().length()>0){
										sub_domain_id = "_"+sub_domain_id;
									}else{
										sub_domain_id="";
									}
									}
									if(element.getAttributeValue("his_start")!=null){
										his_start2=(String)element.getAttributeValue("his_start");
										if(his_start2!=null&&his_start2.trim().length()>0){
										}else{
											his_start2="";
										}
										}
								}
						}catch(Exception e){

						}
					} //sub_domain处理结束
					if(his_start2.length()>0) {
                        his_start = Integer.parseInt(his_start2);
                    }
				field_name=cname+sub_domain_id+"_"+nchgstate;//指标名称（后面带变化前变化后标志）
					String strSrcT=setname;
					FieldItem fielditem=null;//得到指标的详细信息
					if("codesetid".equalsIgnoreCase(cname)|| "codeitemdesc".equalsIgnoreCase(cname)|| "corcode".equalsIgnoreCase(cname)|| "parentid".equalsIgnoreCase(cname)|| "start_date".equalsIgnoreCase(cname))
					{//如果该指标是特殊指标
						fielditem=new FieldItem();
						fielditem.setItemid(cname);
						fielditem.setItemdesc(rset.getString("hz"));
						if(this.infor_type==2) {
                            fielditem.setFieldsetid("B01");
                        } else if(this.infor_type==3) {
                            fielditem.setFieldsetid("K01");
                        }
						if("start_date".equalsIgnoreCase(cname)) {
                            fielditem.setItemtype("D");
                        } else {
                            fielditem.setItemtype("A");
                        }
						if("parentid".equalsIgnoreCase(cname)) {
                            fielditem.setCodesetid("UM");
                        } else if("codesetid".equalsIgnoreCase(cname)) {
                            fielditem.setCodesetid("orgType");
                        } else {
                            fielditem.setCodesetid("0");
                        }
						if(!"start_date".equalsIgnoreCase(cname)) {
                            fielditem.setItemlength(50);
                        }
						fielditem.setUseflag("1");
					}
					else {
                        fielditem=DataDictionary.getFieldItem(cname,setname);//得到指标的详细信息  20150708 dengcan
                    }
					/**未构库或指标体系不存在时则退出*/
					if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                        continue;
                    }
					/**自动生序规则序号，如备案号，只对变化后指标等*/
					if(nchgstate==2)
					{
						if(fielditem.isSequenceable()) {
                            seqHm.put(fielditem.getItemid().toString(),fielditem.getSequencename());
                        }
					}
					if(nchgstate==2&&this.opinion_field!=null&&this.opinion_field.length()>0&&this.opinion_field.equalsIgnoreCase(fielditem.getItemid()))
					{
						continue;
					}
			//		if(nchgstate!=1)
			//			continue;
					if(nchgstate==2) {
                        nhismode=1;
                    }
					if(fieldstr.indexOf(field_name)!=-1) {
                        continue;
                    }

					if(fieldstr2_current.indexOf(field_name)!=-1) {
                        continue;
                    }

					if(itemids.length()>0&&("A01".equalsIgnoreCase(setname)||nhismode==1))//A01的全部数据都走这里；其他人员子集如果有一个是一条记录 就走这里，除非全部都是多条
					{
						String[] temps=itemids.split(",");
						for(int e=0;e<temps.length;e++)
						{
							if(temps[e]!=null&&temps[e].length()>0)
							{
								String[] _temp=temps[e].split("=");
								if(db_type==2||db_type==3)
								{
									fieldstr+=",T."+_temp[0];
									fieldstr1+=",U."+_temp[1];
								}
								else {
                                    fieldstr+=",T."+_temp[0]+"=U."+_temp[1];
                                }
							}
						}
						if(fieldstr.length()>0)
						{
							if(fieldstr.charAt(0)==',') {
                                fieldstr=fieldstr.substring(1);
                            }
fieldstr2_current=fieldstr;
						}
						if(fieldstr1.length()>0){
							if(fieldstr1.charAt(0)==',') {
                                fieldstr1=fieldstr1.substring(1);
                            }
						}
					}
					else if(nhismode==1)//K B 主集或子集的单条记录
					{
						if(db_type==2||db_type==3)
						{
							fieldstr="T."+field_name;
							fieldstr1="U."+cname;
						}
						else {
                            fieldstr="T."+field_name+"=U."+cname;
                        }
					}
					if("codesetid".equalsIgnoreCase(cname)|| "codeitemdesc".equalsIgnoreCase(cname)|| "corcode".equalsIgnoreCase(cname)|| "parentid".equalsIgnoreCase(cname)|| "start_date".equalsIgnoreCase(cname))
					{
						updateOrgInfo(field_name,cname,a0100s,strDesT);
						continue;
					}
					//更新标志
					boolean flag = false;
					ArrayList fieldlist = new ArrayList();
					fieldlist.add(fielditem);
					switch(setname.charAt(0))
					{
					case 'A'://人员信息
					case 'a':
						strSrcT=dbpre+strSrcT;
						if(db_type==2||db_type==3) //oracle,db2
						{
							strUpdate.append("update ");
							strUpdate.append(strDesT);
							strUpdate.append(" T set (");
							strUpdate.append(fieldstr);
							strUpdate.append(")=(select ");
							strUpdate.append(fieldstr1);
							strUpdate.append(" from ");
							strUpdate.append(strSrcT);
							strUpdate.append(" U Where T.A0100=U.A0100");
						}
						else
						{
							strUpdate.append("Update T set ");
							strUpdate.append(fieldstr);
							strUpdate.append(" from ");
							strUpdate.append(strDesT);
							strUpdate.append(" T Left join ");
							strUpdate.append(strSrcT);
							strUpdate.append(" U ON T.A0100=U.A0100");
						}
						if("A01".equalsIgnoreCase(setname))
						{
							if(db_type==2||db_type==3)
							{
								strUpdate.append(") where ");
								//strUpdate.append(a0100s);
								String temp[]=a0100s.split(",");
								if(temp!=null&&temp.length>0){

									int zheng=temp.length/999;
									int yu = temp.length%999;
									for (int j = 0; j < zheng; j++) {
										if(j!=0){
                                                       strUpdate.append("or ");
										}
										strUpdate.append(" T.A0100 in (");
										for(int a=j*999;a<(j+1)*999;a++){
											if(a!=j*999){
												strUpdate.append(",");
											}
											strUpdate.append(temp[a]);
										}
										strUpdate.append(")");
									}
									if(zheng==0){
										if(yu>0){
											strUpdate.append(" T.A0100 in (");
                                                                    for(int a=zheng*999;a<zheng*999+yu;a++){
if(a!=zheng*999){
                          									strUpdate.append(",");
												}
												strUpdate.append(temp[a]);
											}
											strUpdate.append(")");
										}
									}else{
										if(yu>0){
											strUpdate.append("or T.A0100 in (");
											for(int a=zheng*999;a<zheng*999+yu;a++){
												if(a!=zheng*999){
													strUpdate.append(",");
												}
												strUpdate.append(temp[a]);
											}
											strUpdate.append(")");
										}
									}

								}
								strUpdate.append(" and basepre='");
								strUpdate.append(dbpre);
								strUpdate.append("'");
								strUpdate.append(" and  exists (select A0100 from "+dbpre+"A01 where "+dbpre+"A01.a0100=T.A0100 ) ");
							}
							else
							{
								strUpdate.append(" where ");
								//strUpdate.append(a0100s);
                                                String temp[]=a0100s.split(",");
                                                if(temp!=null&&temp.length>0){

                int zheng=temp.length/999;
									int yu = temp.length%999;
									for (int j = 0; j < zheng; j++) {
										if(j!=0){
											strUpdate.append("or ");
										}
										strUpdate.append(" T.A0100 in (");
										for(int a=j*999;a<(j+1)*999;a++){
											if(a!=j*999){
												strUpdate.append(",");
											}
											strUpdate.append(temp[a]);
                                                                }
                                        						strUpdate.append(")");
									}
									if(zheng==0){
										if(yu>0){
											strUpdate.append(" T.A0100 in (");
											for(int a=zheng*999;a<zheng*999+yu;a++){
												if(a!=zheng*999){
													strUpdate.append(",");
												}
												strUpdate.append(temp[a]);
											}
											strUpdate.append(")");
										}
									}else{
										if(yu>0){
											strUpdate.append("or T.A0100 in (");
											for(int a=zheng*999;a<zheng*999+yu;a++){
												if(a!=zheng*999){
													strUpdate.append(",");
												}
												strUpdate.append(temp[a]);
											}
											strUpdate.append(")");
										}
									}

								}
								strUpdate.append(" and basepre='");
								strUpdate.append(dbpre);
								strUpdate.append("'");
								strUpdate.append(" and  exists (select A0100 from "+dbpre+"A01 where "+dbpre+"A01.a0100=T.A0100 ) ");
							}
						}
						else
						{
							if(nhismode==1) //当前记录
							{
								if(db_type==2||db_type==3)
								{
									strUpdate.append(" and (U.I9999=(select Max(I9999) from ");
									strUpdate.append(strSrcT);
									strUpdate.append(" where ");
									strUpdate.append(strSrcT);
									strUpdate.append(".A0100=U.A0100) or U.I9999 is null) "); // and (T.state<>1 or T.state is null)");
									strUpdate.append(") where ");
									//strUpdate.append(a0100s);
									String temp[]=a0100s.split(",");
                                                if(temp!=null&&temp.length>0){

										int zheng=temp.length/999;
                                                		int yu = temp.length%999;
										for (int j = 0; j < zheng; j++) {
											if(j!=0){
												strUpdate.append("or ");
											}
											strUpdate.append(" T.A0100 in (");
											for(int a=j*999;a<(j+1)*999;a++){
                                                                            if(a!=j*999){
                                                                                strUpdate.append(",");
												}
												strUpdate.append(temp[a]);
											}
											strUpdate.append(")");
										}
										if(zheng==0){
											if(yu>0){
												strUpdate.append(" T.A0100 in (");
												for(int a=zheng*999;a<zheng*999+yu;a++){
													if(a!=zheng*999){
														strUpdate.append(",");
													}
													strUpdate.append(temp[a]);
												}
												strUpdate.append(")");
											}
										}else{
                                                               if(yu>0){
                      				strUpdate.append("or T.A0100 in (");
												for(int a=zheng*999;a<zheng*999+yu;a++){
													if(a!=zheng*999){
                                                                                        strUpdate.append(",");
													}
													strUpdate.append(temp[a]);
												}
												strUpdate.append(")");
											}
										}

									}
									strUpdate.append(" and basepre='");
									strUpdate.append(dbpre);
									strUpdate.append("'");
								}
								else
								{
									strUpdate.append(" where (U.I9999=(select Max(I9999) from ");
									strUpdate.append(strSrcT);
									strUpdate.append(" where ");
									strUpdate.append(strSrcT);
									strUpdate.append(".A0100=U.A0100) or U.I9999 is null) "); // and (T.state<>1 or T.state is null)");
									strUpdate.append(" and ");
									//strUpdate.append(a0100s);
									String temp[]=a0100s.split(",");
									if(temp!=null&&temp.length>0){

										int zheng=temp.length/999;
										int yu = temp.length%999;
										for (int j = 0; j < zheng; j++) {
											if(j!=0){
												strUpdate.append("or ");
											}
											strUpdate.append(" T.A0100 in (");
											for(int a=j*999;a<(j+1)*999;a++){
												if(a!=j*999){
													strUpdate.append(",");
												}
												strUpdate.append(temp[a]);
                                                                        }
         										strUpdate.append(")");
										}
										if(zheng==0){
											if(yu>0){
												strUpdate.append(" T.A0100 in (");
												for(int a=zheng*999;a<zheng*999+yu;a++){
													if(a!=zheng*999){
														strUpdate.append(",");
													}
													strUpdate.append(temp[a]);
												}
												strUpdate.append(")");
											}
										}else{
											if(yu>0){
												strUpdate.append("or T.A0100 in (");
												for(int a=zheng*999;a<zheng*999+yu;a++){
													if(a!=zheng*999){
														strUpdate.append(",");
													}
													strUpdate.append(temp[a]);
												}
												strUpdate.append(")");
											}
										}

									}
									strUpdate.append(" and basepre='");
									strUpdate.append(dbpre);
									strUpdate.append("'");
								}
							}
							else if(nhismode==3) //条件定位
							{
								 //  <EXPR>1</EXPR><FACTOR>A0420=01</FACTOR>
								String expr="";
								String factor="";
								if(formula.trim().length()>0)
								{
									int f=formula.indexOf("<EXPR>");
									int t=formula.indexOf("</EXPR>");
									if (f>-1 && t>-1){
										expr=formula.substring(f+6,t);
									}
									f=formula.indexOf("<FACTOR>");
									t=formula.indexOf("</FACTOR>");
									if (f>-1 && t>-1){
										factor=formula.substring(f+8,t);
									}
								}
								factor=factor.replaceAll(",","`");
								factor=factor+"`";

								FactorList factorlist=new FactorList(expr,factor,"");
								String strw=factorlist.getSingleTableSqlExpression("F");

								//update sutemplet_12 T set (T.A0405_1)=
							//	  (select U.A0405 from usrA04 U Where T.A0100=U.A0100
							//			     and U.i9999=(select max(F.i9999) from usrA04 F where U.a0100=F.a0100 and F.C0407='1' ) )
								//写入临时表里

								StringBuffer strUpdate2 = new StringBuffer();
								if(a0110list.size()>0){
									for(int m =0;m<a0110list.size();m++){
										StringBuffer buf = new StringBuffer();
										buf.append("select ");
										buf.append("F."+cname);
										buf.append(",F.i9999");
										buf.append(" from ");
										buf.append(strSrcT+" F ");
										if(this.infor_type==1) {
                                            buf.append(" where F.a0100='"+a0110list.get(m)+"'");
                                        } else if(this.infor_type==2) {
                                 buf.append(" where F.b0110='"+a0110list.get(m)+"'");
                                        } else if(this.infor_type==3) {
                          buf.append(" where F.e01a1='"+a0110list.get(m)+"'");
                                        }
										if(strw.trim().length()>0) {
                                                                    buf.append(" and ("+strw+") ");
                                        }
                                                                //更新记录
								ArrayList reclist=getRecordValue(fieldlist,dao.search(buf.toString()));
										buf.setLength(0);
										for(int n=0;n<reclist.size();n++)
										{
											buf.append(((ArrayList)reclist.get(n)).get(1).toString().replaceAll("`", "^^"));
											if(n<reclist.size()-1){
												buf.append("`");
											}
										}
										strUpdate2.setLength(0);
										strUpdate2.append("Update ");
										strUpdate2.append(strDesT);
										strUpdate2.append(" set ");
										strUpdate2.append(field_name);
										strUpdate2.append(" ='"+buf.toString());
										strUpdate2.append("' where ");
										strUpdate2.append(paramname);
										strUpdate2.append(" = ");
										strUpdate2.append("'"+a0110list.get(m)+"'");
										if(this.infor_type==1&&dbpre.length()>0) {
                                            strUpdate2.append(" and basepre ='"+dbpre+"'");
                                        }
										dao.update(strUpdate2.toString());
										flag =true;
									}
                                            			}
//								strUpdate.append(" and  (U.i9999=(select max(F.i9999) from "+strSrcT+" F where U.a0100=F.a0100 ");
//								if(strw!=null&&strw.trim().length()>0)
//									strUpdate.append(" and "+strw+" ");
//								strUpdate.append(" ) ");
//
//							//	strUpdate.append(" and "+strw);
//								strUpdate.append(" ) ");
//								if(db_type==2||db_type==3) //oracle,db2
//									strUpdate.append(" ) ");
//								strUpdate.append(" where T.A0100 in (");
//								strUpdate.append(a0100s);
//								strUpdate.append(") and basepre='");
//								strUpdate.append(dbpre);
//								strUpdate.append("'");

							}
							else if(nhismode==4) //条件序号
							{
								 //  <EXPR>1</EXPR><FACTOR>A0420=01</FACTOR>
								String expr="";
								String factor="";
								if(formula.trim().length()>0)
								{
									int f=formula.indexOf("<EXPR>");
									int t=formula.indexOf("</EXPR>");
									if (f>-1 && t>-1){
										expr=formula.substring(f+6,t);
									}
									f=formula.indexOf("<FACTOR>");
									t=formula.indexOf("</FACTOR>");
									if (f>-1 && t>-1){
										factor=formula.substring(f+8,t);
									}
								}
								factor=factor.replaceAll(",","`");
								factor=factor+"`";

								FactorList factorlist=new FactorList(expr,factor,"");
								String strw=factorlist.getSingleTableSqlExpression("F");

								//update sutemplet_12 T set (T.A0405_1)=
							//	  (select U.A0405 from usrA04 U Where T.A0100=U.A0100
							//			     and U.i9999=(select max(F.i9999) from usrA04 F where U.a0100=F.a0100 and F.C0407='1' ) )
								//写入临时表里

								StringBuffer strUpdate2 = new StringBuffer();
								if(a0110list.size()>0){
									for(int m =0;m<a0110list.size();m++){

										StringBuffer buf = new StringBuffer();
										buf.append("select ");
										buf.append("F."+cname);
										buf.append(",F.i9999");
										buf.append(" from ");
										buf.append(strSrcT+" F ");
										if(this.infor_type==1) {
                                            buf.append(" where F.a0100='"+a0110list.get(m)+"'");
                                        } else if(this.infor_type==2) {
                                            buf.append(" where F.b0110='"+a0110list.get(m)+"'");
                                        } else if(this.infor_type==3) {
                                            buf.append(" where F.e01a1='"+a0110list.get(m)+"'");
                                        }
										if(strw.trim().length()>0) {
                                            buf.append(" and ("+strw+") ");
                                        }
										ArrayList i9999list=getSubSetI9999s(buf.toString()+" order by F.i9999");
										ArrayList paralist = new ArrayList();
										int size=i9999list.size();
										if(size>0)
										{
										/**初值为-1*/
										String curri9999="-1";
										switch(nmode)
										{
										case 0://倒数第...条（最近第）

												if(size>=ncount)//子集记录大于要取的的记录数
												{
													if(size==ncount) {
                                                        curri9999=(String)i9999list.get(0);
                                                    } else
													{
														if(ncount!=0) {
                                                            curri9999=(String)i9999list.get(size-ncount);
                                                        } else {
                                   curri9999=(String)i9999list.get(size-ncount-1);
                                                        }
													}
												}

												buf.append(" and F.I9999=?");
                                    					paralist.add(curri9999);
											break;
										case 1://倒数...条（最近）
											if(his_start==0){
												if(size>=ncount)
												{
													if(size==ncount) {
                                                        curri9999=(String)i9999list.get(0);
                                                    } else {
                                                        curri9999=(String)i9999list.get(size-ncount);
                                                    }
												}
												buf.append(" and F.I9999>=? order by F.I9999");
											}else{
												if(his_start>size){
													curri9999="-1";
													buf.append(" and F.I9999<=? order by F.I9999");
												}else{
													if(size>=ncount){
														String curri99992=(String)i9999list.get(size-his_start);
														buf.append(" and F.I9999<=? ");
														paralist.add(curri99992);
														if(size<his_start+ncount) {
                                                            curri9999=(String)i9999list.get(0);
                                                        } else {
                                                            curri9999=(String)i9999list.get(size-ncount-(his_start-1));
                                                        }
														buf.append(" and F.I9999>=? order by F.I9999");

													}else{
														 curri9999=(String)i9999list.get(size-his_start);
                                                                     buf.append(" and F.I9999<=? order by F.I9999");
													}

												}
											}
											paralist.add(curri9999);
											break;
										case 2://正数第...条(最初第)
											if(size>=ncount) {
                                                curri9999=(String)i9999list.get(ncount-1);
                                            }
											buf.append(" and F.I9999=?");
											paralist.add(curri9999);
											break;
										case 3://正数...条（最初）
											if(his_start==0){
												if(size>=ncount){
													curri9999=(String)i9999list.get(ncount-1);
												buf.append(" and F.I9999<=? order by F.I9999");
												}else{
													buf.append(" and F.I9999>=? order by F.I9999");
												}
											}else{
												if(his_start>size){
													curri9999="-1";
													buf.append(" and F.I9999<=? order by F.I9999");
                                                    }else{
                                                            if(size>=ncount){
											String curri99992=(String)i9999list.get(his_start-1);
														buf.append(" and F.I9999>=? ");
														paralist.add(curri99992);
														if(size<his_start+ncount){
															buf.append(" and I9999>=? ");
															curri9999=curri99992;
														}else{
														curri9999=(String)i9999list.get(his_start+ncount-2);
														buf.append(" and F.I9999<=? order by F.I9999");
														}
													}else{
														 curri9999=(String)i9999list.get(his_start-1);
															buf.append(" and F.I9999>=? order by F.I9999");
													}

												}
											}
											paralist.add(curri9999);
											break;
										}
										}

										//更新记录
										ArrayList reclist=getRecordValue(fieldlist,dao.search(buf.toString(),paralist));
										buf.setLength(0);
										for(int n=0;n<reclist.size();n++)
										{
											buf.append(((ArrayList)reclist.get(n)).get(1).toString().replaceAll("`", "^^"));
											if(n<reclist.size()-1){
												buf.append("`");
											}
										}
										strUpdate2.setLength(0);
										strUpdate2.append("Update ");
										strUpdate2.append(strDesT);
										strUpdate2.append(" set ");
										strUpdate2.append(field_name);
										strUpdate2.append(" ='"+buf.toString());
										strUpdate2.append("' where ");
										strUpdate2.append(paramname);
										strUpdate2.append(" = ");
										strUpdate2.append("'"+a0110list.get(m)+"'");
										if(this.infor_type==1&&dbpre.length()>0) {
                                            strUpdate2.append(" and basepre ='"+dbpre+"'");
                                        }
										dao.update(strUpdate2.toString());
										flag =true;
									}
								}

							}
							else if(nhismode==2&&(nmode==0 || nmode==1 || nmode==2 || nmode==3)) //多条记录
            {
                //查找库中的数据个数
								StringBuffer strUpdate2 = new StringBuffer();
								if(a0110list.size()>0){
									for(int m =0;m<a0110list.size();m++){
										/**求子集序号列表*/
										ArrayList i9999list=getSubSetI9999s(strSrcT,""+a0110list.get(m));

											int size=i9999list.size();
                                                StringBuffer buf = new StringBuffer();
											ArrayList paralist = new ArrayList();
											buf.append("select ");
											buf.append(cname);
											buf.append(",i9999");
											buf.append(" from ");
											buf.append(strSrcT);
											if(this.infor_type==1) {
                                                buf.append(" where a0100=?");
                                            } else if(this.infor_type==2) {
                                                buf.append(" where b0110=?");
                                            } else if(this.infor_type==3) {
                                                buf.append(" where e01a1=?");
                                            }
											paralist.add(""+a0110list.get(m));
											if(size>0)
											{
												/**初值为-1*/
												String curri9999="-1";
												switch(nmode)
                                                                {
                                								case 0://倒数第...条（最近第）

														if(size>=ncount)//子集记录大于要取的的记录数
														{
															if(size==ncount) {
                                                                curri9999=(String)i9999list.get(0);
                                                            } else
															{
																if(ncount!=0) {
                                                                    curri9999=(String)i9999list.get(size-ncount);
                                                                } else {
                                                                    curri9999=(String)i9999list.get(size-ncount-1);
                                                                }
															}
														}

														buf.append(" and I9999=?");
													paralist.add(curri9999);
													break;
												case 1://倒数...条（最近）
													if(his_start==0){
														if(size>=ncount)
														{
															if(size==ncount) {
                                                                curri9999=(String)i9999list.get(0);
                                                            } else {
                                                                curri9999=(String)i9999list.get(size-ncount);
                                                            }
														}
														buf.append(" and I9999>=? order by I9999");
													}else{
														if(his_start>size){
															curri9999="-1";
															buf.append(" and I9999<=? order by I9999");
                                    												}else{
															if(size>=ncount){
																String curri99992=(String)i9999list.get(size-his_start);
																buf.append(" and I9999<=? ");
																paralist.add(curri99992);
																if(size<his_start+ncount) {
                                                                                                        curri9999=(String)i9999list.get(0);
                                                                                                    } else {
                                    curri9999=(String)i9999list.get(size-ncount-(his_start-1));
}
                                                                        buf.append(" and I9999>=? order by I9999");

															}else{
																 curri9999=(String)i9999list.get(size-his_start);
																buf.append(" and I9999<=? order by I9999");
															}

														}
													}
                                                                        paralist.add(curri9999);
													break;
												case 2://正数第...条(最初第)
													if(size>=ncount) {
                                                        curri9999=(String)i9999list.get(ncount-1);
                                                    }
													buf.append(" and I9999=?");
													paralist.add(curri9999);
                                                							break;
												case 3://正数...条（最初）
													if(his_start==0){
														if(size>=ncount){
															curri9999=(String)i9999list.get(ncount-1);
														buf.append(" and I9999<=? order by I9999");
														}else{
                                                                                        buf.append(" and I9999>=? order by I9999");
														}
													}else{
														if(his_start>size){
															curri9999="-1";
															buf.append(" and I9999<=? order by I9999");
														}else{
															if(size>=ncount){
																String curri99992=(String)i9999list.get(his_start-1);
																buf.append(" and I9999>=? ");
																paralist.add(curri99992);
																if(size<his_start+ncount){
																	buf.append(" and I9999>=? ");
																	curri9999=curri99992;
																}else{
																curri9999=(String)i9999list.get(his_start+ncount-2);
																buf.append(" and I9999<=? order by I9999");
																}
															}else{
																 curri9999=(String)i9999list.get(his_start-1);
																	buf.append(" and I9999>=? order by I9999");
															}

														}
													}
													paralist.add(curri9999);
													break;
												}
											}
											ArrayList reclist=getRecordValue(fieldlist,dao.search(buf.toString(),paralist));
											buf.setLength(0);
											for(int n=0;n<reclist.size();n++)
											{
												buf.append(((ArrayList)reclist.get(n)).get(1).toString().replaceAll("`", "^^"));
												if(n<reclist.size()-1){
													buf.append("`");
												}
											}
											//更新记录

											strUpdate2.setLength(0);
											strUpdate2.append("Update ");
											strUpdate2.append(strDesT);
											strUpdate2.append(" set ");
											strUpdate2.append(field_name);
											strUpdate2.append(" ='"+buf.toString());
											strUpdate2.append("' where ");
											strUpdate2.append(paramname);
											strUpdate2.append(" = ");
											strUpdate2.append("'"+a0110list.get(m)+"'");
											if(this.infor_type==1&&dbpre.length()>0) {
                                                                    strUpdate2.append(" and basepre ='"+dbpre+"'");
                                            }
											dao.update(strUpdate2.toString());
											flag =true;
									}
									break;
								}
								//?oracle db2
								if(db_type==2||db_type==3)
								{
									//strUpdate.append(")");//一个一个人单独处理吧.for 按当前记录导入
									strUpdate.append(" and (U.I9999=(select Max(I9999) from ");
									strUpdate.append(strSrcT);
									strUpdate.append(" where ");
									strUpdate.append(strSrcT);
									strUpdate.append(".A0100=U.A0100) or U.I9999 is null) "); // and (T.state<>1 or T.state is null)");
									strUpdate.append(") where ");
									//strUpdate.append(a0100s);
									String temp[]=a0100s.split(",");
									if(temp!=null&&temp.length>0){

										int zheng=temp.length/999;
										int yu = temp.length%999;
										for (int j = 0; j < zheng; j++) {
											if(j!=0){
												strUpdate.append("or ");
											}
											strUpdate.append(" T.A0100 in (");
											for(int a=j*999;a<(j+1)*999;a++){
												if(a!=j*999){
													strUpdate.append(",");
												}
												strUpdate.append(temp[a]);
											}
											strUpdate.append(")");
										}
										if(zheng==0){
											if(yu>0){
												strUpdate.append(" T.A0100 in (");
												for(int a=zheng*999;a<zheng*999+yu;a++){
													if(a!=zheng*999){
														strUpdate.append(",");
													}
													strUpdate.append(temp[a]);
												}
												strUpdate.append(")");
											}
										}else{
											if(yu>0){
												strUpdate.append("or T.A0100 in (");
												for(int a=zheng*999;a<zheng*999+yu;a++){
													if(a!=zheng*999){
														strUpdate.append(",");
													}
													strUpdate.append(temp[a]);
												}
												strUpdate.append(")");
											}
										}

									}
									strUpdate.append(" and basepre='");
									strUpdate.append(dbpre);
									strUpdate.append("'");
								}
								else//MSSQL
								{
									strUpdate.append(" where U.I9999=(select min(I9999) from ");
									strUpdate.append(strSrcT);
									strUpdate.append(" where I9999 in (select top ");
									strUpdate.append(ncount);
									strUpdate.append(" I9999 from ");
									strUpdate.append(strSrcT);
									strUpdate.append(" where U.A0100=");
									strUpdate.append(strSrcT);
									strUpdate.append(".A0100 order by I9999 ");

									switch(nmode)
									{
									case 0://最近第
										strUpdate.append(" desc ");
										break;
									default://最初第
										strUpdate.append(" asc ");
										break;
									}
									strUpdate.append(") and U.A0100=");
									strUpdate.append(strSrcT);
									strUpdate.append(".A0100)");
								}
							}else{///现在程序控制住了，不走这个else了。

								//?oracle db2
								if(db_type==2||db_type==3)
								{
									//strUpdate.append(")");//一个一个人单独处理吧.for 按当前记录导入
									strUpdate.append(" and (U.I9999=(select Max(I9999) from ");
									strUpdate.append(strSrcT);
									strUpdate.append(" where ");
									strUpdate.append(strSrcT);
									strUpdate.append(".A0100=U.A0100) or U.I9999 is null) "); // and (T.state<>1 or T.state is null)");
									strUpdate.append(") where ");
									//strUpdate.append(a0100s);
									String temp[]=a0100s.split(",");
									if(temp!=null&&temp.length>0){

										int zheng=temp.length/999;
										int yu = temp.length%999;
										for (int j = 0; j < zheng; j++) {
											if(j!=0){
												strUpdate.append("or ");
											}
											strUpdate.append(" T.A0100 in (");
											for(int a=j*999;a<(j+1)*999;a++){
												if(a!=j*999){
													strUpdate.append(",");
												}
												strUpdate.append(temp[a]);
											}
											strUpdate.append(")");
										}
										if(zheng==0){
											if(yu>0){
												strUpdate.append(" T.A0100 in (");
												for(int a=zheng*999;a<zheng*999+yu;a++){
													if(a!=zheng*999){
														strUpdate.append(",");
													}
													strUpdate.append(temp[a]);
												}
												strUpdate.append(")");
											}
										}else{
											if(yu>0){
												strUpdate.append("or T.A0100 in (");
												for(int a=zheng*999;a<zheng*999+yu;a++){
													if(a!=zheng*999){
														strUpdate.append(",");
													}
													strUpdate.append(temp[a]);
												}
												strUpdate.append(")");
											}
										}

									}
									strUpdate.append(" and basepre='");
									strUpdate.append(dbpre);
									strUpdate.append("'");
								}
								else//MSSQL
								{
									strUpdate.append(" where U.I9999=(select min(I9999) from ");
									strUpdate.append(strSrcT);
									strUpdate.append(" where I9999 in (select top ");
									strUpdate.append(ncount);
									strUpdate.append(" I9999 from ");
									strUpdate.append(strSrcT);
									strUpdate.append(" where U.A0100=");
									strUpdate.append(strSrcT);
									strUpdate.append(".A0100 order by I9999 ");
									switch(nmode)
									{
									case 0://最近第
										strUpdate.append(" desc ");
										break;
									default://最初第
										strUpdate.append(" asc ");
										break;
									}
									strUpdate.append(") and U.A0100=");
									strUpdate.append(strSrcT);
                                                            strUpdate.append(".A0100)");
								}

							}
						}
						break;
					case 'B'://单位信息
					case 'b':
						if(this.infor_type==1)
						{
							if(db_type==2||db_type==3) //oracle,db2
							{
								strUpdate.append("update ");
                                    			strUpdate.append(strDesT);
								strUpdate.append(" T set (");
								strUpdate.append(fieldstr);
								strUpdate.append(")=(select ");
								strUpdate.append(fieldstr1);
								strUpdate.append(" from ");
								strUpdate.append(strSrcT);
								strUpdate.append(" U Where T.B0110_1=U.B0110");
							}
							else
							{
								strUpdate.append("Update T set ");
                                            strUpdate.append(fieldstr);
								strUpdate.append(" from ");
								strUpdate.append(strDesT);
								strUpdate.append(" T Left join ");
								strUpdate.append(strSrcT);
								strUpdate.append(" U ON T.B0110_1=U.B0110");
							}
							if("B01".equalsIgnoreCase(setname))
							{
									//cmq "where" 改成 "and"  at 20090821
								 if(db_type==2||db_type==3) //oracle,db2
                                 {
                                     strUpdate.append(" and  (");//wangrd 20160316 下面已有A0100 in信息 此处去掉 k01也做相应更改
                                 } else {
                                     strUpdate.append(" where ");
                                 }
								//  strUpdate.append(" and T.A0100 in (");
									//strUpdate.append(a0100s);
								 String temp[]=a0100s.split(",");
									if(temp!=null&&temp.length>0){

										int zheng=temp.length/999;
										int yu = temp.length%999;
										for (int j = 0; j < zheng; j++) {
											if(j!=0){
												strUpdate.append("or ");
											}
											strUpdate.append(" T.A0100 in (");
											for(int a=j*999;a<(j+1)*999;a++){
												if(a!=j*999){
													strUpdate.append(",");
												}
												strUpdate.append(temp[a]);
											}
											strUpdate.append(")");
										}
										if(zheng==0){
											if(yu>0){
												strUpdate.append(" T.A0100 in (");
												for(int a=zheng*999;a<zheng*999+yu;a++){
													if(a!=zheng*999){
														strUpdate.append(",");
													}
													strUpdate.append(temp[a]);
												}
												strUpdate.append(")");
											}
										}else{
											if(yu>0){
												strUpdate.append("or T.A0100 in (");
												for(int a=zheng*999;a<zheng*999+yu;a++){
													if(a!=zheng*999){
														strUpdate.append(",");
													}
													strUpdate.append(temp[a]);
												}
												strUpdate.append(")");
											}
										}

									}
									strUpdate.append(" and basepre='");
									strUpdate.append(dbpre);
									strUpdate.append("'");
								//	strUpdate.append("')"); //cmq added ")";
									if(db_type==2||db_type==3) //oracle,db2
                                    {
                                        strUpdate.append(" ))");
                                    }
							}
							else
							{
								if(nhismode==1) {
									if(db_type==2||db_type==3)
									{
										strUpdate.append(" and (U.I9999=(select Max(I9999) from ");
									}
									else
									{
										strUpdate.append(" where (U.I9999=(select Max(I9999) from ");
									}
									strUpdate.append(strSrcT);
									strUpdate.append(" where ");
									strUpdate.append(strSrcT);
									strUpdate.append(".B0110=U.B0110) or U.I9999 is null) "); // and (T.state<>1 or T.state is null)");
									//	strUpdate.append(") ");
									if(db_type==2||db_type==3) {
                                        strUpdate.append(" ) where ");
                                    } else {
                                        strUpdate.append(" and ");
                                    }
									//strUpdate.append(" T.A0100 in (");
									//strUpdate.append(a0100s);
									String temp[]=a0100s.split(",");
									if(temp!=null&&temp.length>0){

										int zheng=temp.length/999;
										int yu = temp.length%999;
										for (int j = 0; j < zheng; j++) {
											if(j!=0){
												strUpdate.append("or ");
											}
											strUpdate.append(" T.A0100 in (");
											for(int a=j*999;a<(j+1)*999;a++){
												if(a!=j*999){
													strUpdate.append(",");
                                                                        }
                                            							strUpdate.append(temp[a]);
                                                                            			}
											strUpdate.append(")");
										}
										if(zheng==0){
                            						if(yu>0){
												strUpdate.append(" T.A0100 in (");
												for(int a=zheng*999;a<zheng*999+yu;a++){
													if(a!=zheng*999){
                                                                            strUpdate.append(",");
													}
													strUpdate.append(temp[a]);
												}
												strUpdate.append(")");
											}
										}else{
											if(yu>0){
												strUpdate.append("or T.A0100 in (");
												for(int a=zheng*999;a<zheng*999+yu;a++){
													if(a!=zheng*999){
														strUpdate.append(",");
													}
													strUpdate.append(temp[a]);
												}
												strUpdate.append(")");
											}
										}

									}
									strUpdate.append(" and basepre='");
									strUpdate.append(dbpre);
									strUpdate.append("'");
								}else {
									//人员导入单位子集设置取历史记录修改
									templatebo.updateDataToField(formula,a0110list,cname,paramname,strSrcT,dbpre,nmode,ncount,strDesT,fieldlist,field_name,nhismode,setname,his_start);
									strUpdate.setLength(0);
									flag=true;
								}
							}
						}
						else if(this.infor_type==2)
						{
						 String  str =	impDataFromBKarchive(strSrcT,db_type,strDesT,fieldstr,fieldstr1,setname,a0100s,ncount,nmode,nhismode,formula,dao,a0110list,cname,fieldlist,field_name,paramname,dbpre,his_start);
						if(str.length()>0) {
                            strUpdate.append(str);
} else {
                            flag =true;
                        }
						}

						break;
					case 'K'://职位信息
					case 'k':
						if(this.infor_type==1)
						{
                    if(db_type==2||db_type==3) //oracle,db2
							{
								strUpdate.append("update ");
								strUpdate.append(strDesT);
								strUpdate.append(" T set (");
								strUpdate.append(fieldstr);
								strUpdate.append(")=(select ");
								strUpdate.append(fieldstr1);
								strUpdate.append(" from ");
								strUpdate.append(strSrcT);
								strUpdate.append(" U Where T.E01A1_1=U.E01A1");
							}
							else
							{
								strUpdate.append("Update T set ");
								strUpdate.append(fieldstr);
								strUpdate.append(" from ");
								strUpdate.append(strDesT);
								strUpdate.append(" T Left join ");
								strUpdate.append(strSrcT);
								strUpdate.append(" U ON T.E01A1_1=U.E01A1");
							}
							if("K01".equalsIgnoreCase(setname))
							{
	            				   if(db_type==2||db_type==3) //oracle,db2
                                   {
                                       strUpdate.append(" and (");
                                  } else {
              strUpdate.append(" where ");
                                   }
									//strUpdate.append(a0100s);
	            				   String temp[]=a0100s.split(",");
									if(temp!=null&&temp.length>0){

										int zheng=temp.length/999;
										int yu = temp.length%999;
										for (int j = 0; j < zheng; j++) {
											if(j!=0){
												strUpdate.append("or ");
											}
											strUpdate.append(" T.A0100 in (");
											for(int a=j*999;a<(j+1)*999;a++){
												if(a!=j*999){
													strUpdate.append(",");
												}
												strUpdate.append(temp[a]);
											}
											strUpdate.append(")");
										}
										if(zheng==0){
											if(yu>0){
												strUpdate.append(" T.A0100 in (");
												for(int a=zheng*999;a<zheng*999+yu;a++){
													if(a!=zheng*999){
														strUpdate.append(",");
													}
													strUpdate.append(temp[a]);
												}
												strUpdate.append(")");
											}
										}else{
											if(yu>0){
												strUpdate.append("or T.A0100 in (");
												for(int a=zheng*999;a<zheng*999+yu;a++){
													if(a!=zheng*999){
														strUpdate.append(",");
													}
													strUpdate.append(temp[a]);
												}
                                                                    strUpdate.append(")");
                                                                }
										}

                                                }
                                                strUpdate.append(" and basepre='");
            									strUpdate.append(dbpre);
									strUpdate.append("'");

									if(db_type==2||db_type==3) //oracle,db2
                                    {
                                        strUpdate.append(" )) ");
                                    }
							}
							else
							{
									if(nhismode==1) {//当前记录
										if(db_type==2||db_type==3)
										{
											strUpdate.append(" and (U.I9999=(select Max(I9999) from ");
										}
										else
										{
											strUpdate.append(" where (U.I9999=(select Max(I9999) from ");
										}
										strUpdate.append(strSrcT);
										strUpdate.append(" where ");
										strUpdate.append(strSrcT);
										strUpdate.append(".E01A1=U.E01A1) or U.I9999 is null) "); // and (T.state<>1 or T.state is null)");
										//	strUpdate.append(") ");
										if(db_type==2||db_type==3) {
                                            strUpdate.append(" ) where ");
                                        } else {
                                            strUpdate.append(" and ");
                                        }
										//strUpdate.append(" T.A0100 in (");
										//strUpdate.append(a0100s);
                                								String temp[]=a0100s.split(",");
										if(temp!=null&&temp.length>0){

											int zheng=temp.length/999;
											int yu = temp.length%999;
											for (int j = 0; j < zheng; j++) {
												if(j!=0){
													strUpdate.append("or ");
												}
												strUpdate.append(" T.A0100 in (");
												for(int a=j*999;a<(j+1)*999;a++){
													if(a!=j*999){
														strUpdate.append(",");
													}
													strUpdate.append(temp[a]);
												}
												strUpdate.append(")");
											}
											if(zheng==0){
												if(yu>0){
													strUpdate.append(" T.A0100 in (");
													for(int a=zheng*999;a<zheng*999+yu;a++){
														if(a!=zheng*999){
															strUpdate.append(",");
														}
														strUpdate.append(temp[a]);
													}
													strUpdate.append(")");
												}
											}else{
												if(yu>0){
													strUpdate.append("or T.A0100 in (");
													for(int a=zheng*999;a<zheng*999+yu;a++){
														if(a!=zheng*999){
															strUpdate.append(",");
														}
														strUpdate.append(temp[a]);
													}
													strUpdate.append(")");
												}
											}

										}
										strUpdate.append(" and basepre='");
										strUpdate.append(dbpre);
										strUpdate.append("'");
									}else {
										//人员导入岗位子集设置取历史记录修改
										templatebo.updateDataToField(formula,a0110list,cname,paramname,strSrcT,dbpre,nmode,ncount,strDesT,fieldlist,field_name,nhismode,setname,his_start);
										strUpdate.setLength(0);
                                                            flag=true;
                                                        }


							}
						}
						else if(this.infor_type==3)
						{
							String  str =	impDataFromBKarchive(strSrcT,db_type,strDesT,fieldstr,fieldstr1,setname,a0100s,ncount,nmode,nhismode,formula,dao,a0110list,cname,fieldlist,field_name,paramname,dbpre,his_start);
							if(str.length()>0) {
                                strUpdate.append(str);
                            } else {
                                flag =true;
                            }
						}
						break;
					}
					if(flag) {
                        continue;
                    }
			 		//System.out.println("="+strUpdate.toString());
					dao.update(strUpdate.toString());

				}//while rset loop end.
			}//for i loop end.
			ErrorSetFlag=true;
			//不被  change_after_get_data="0";  //1：变化后指标取当前值  0：不取  控制
			if(sync==0)
			{
				strsql.setLength(0);
				if(db_type==2) {
                    strsql.append("select distinct T.field_name,T.ChgState,T.hismode,T.rcount,T.mode_o,T.subflag from template_set T ");
                } else {
                    strsql.append("select distinct T.field_name,T.ChgState,T.hismode,T.rcount,T.mode,T.subflag from template_set T ");
           }
				strsql.append(" where  T.tabid="+this.tabid+" and T.subflag=0 ");
				rset=dao.search(strsql.toString());
				while(rset.next())
				{
					nchgstate=rset.getInt("chgstate");
                        cname=rset.getString("field_name");
					if(cname==null) {
                        continue;
                    }
					FieldItem fielditem=DataDictionary.getFieldItem(cname);
					/**未构库或指标体系不存在时则退出*/
					if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                        continue;
                    }
					/**自动生序规则序号，如备案号，只对变化后指标等*/
					if(nchgstate==2)
					{
						if(fielditem.isSequenceable()) {
                            seqHm.put(fielditem.getItemid().toString(),fielditem.getSequencename());
                        }
					}
				}
			}


			/**导入插入子集区域的数据*/
			strsql.setLength(0);
			if(db_type==2)//oracle
            {
                strsql.append("select T.setname,T.ChgState,T.formula,T.hismode,T.rcount,T.mode_o,T.subflag,T.sub_domain from template_set T  where ");
            } else {
                strsql.append("select T.setname,T.ChgState,T.formula,T.hismode,T.rcount,T.mode,T.subflag,T.sub_domain from template_set T  where ");
            }
			strsql.append(" T.tabid=");
			strsql.append(this.tabid);
			strsql.append(" and ");
			strsql.append(" subflag=1");
			rset=dao.search(strsql.toString());
			while(rset.next())
			{
				strUpdate.setLength(0);
				nchgstate=rset.getInt("chgstate");
				if(db_type==2)//oracle
                {
                    nmode=rset.getInt("mode_o");
} else {
nmode=rset.getInt("mode");
                }
				ncount=rset.getInt("rcount");
				nhismode=rset.getInt("hismode");
				cname=rset.getString("setname");
				field_name="t_"+cname+"_"+nchgstate;
				/**导入插入子集区域的数据*/
				String subxml=Sql_switcher.readMemo(rset, "sub_domain");
				String formula=Sql_switcher.readMemo(rset, "formula");
				/**对插入子集区域数据同步规则，变化前或第一次导入时*/
				if((sync==0||nchgstate==1)&&cname!=null&&cname.trim().length()>0){
					//获得sub_domain_id
					String sub_domain_id="";
					//获得第x到y中的x值
					String his_start2="";
					int his_start =0;
					if(subxml!=null&&subxml.trim().length()>0){
						try{
								doc=PubFunc.generateDom(subxml);;
								XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                                        List childlist=findPath.selectNodes(doc);
                                        if(childlist!=null&&childlist.size()>0)
                                        {
                                            element=(Element) childlist.get(0);
                                            if(element.getAttributeValue("id")!=null&&"1".equals(""+nchgstate)){
                                            sub_domain_id=(String)element.getAttributeValue("id");
									if(sub_domain_id!=null&&sub_domain_id.trim().length()>0){
									}else{
										sub_domain_id="";
									}
									}
									if(element.getAttributeValue("his_start")!=null){
										his_start2=(String)element.getAttributeValue("his_start");
										if(his_start2!=null&&his_start2.trim().length()>0){
										}else{
											his_start2="";
										}
										}
								}
						}catch(Exception e){

						}
					}


					if(his_start2.length()>0) {
                        his_start = Integer.parseInt(his_start2);
                    }
					if(sub_domain_id!=null&&sub_domain_id.length()>0) {
                        field_name="t_"+cname+"_"+sub_domain_id+"_"+nchgstate;
                    }
					impSubDomainData(a0100s,dbpre,subxml,cname,field_name.toLowerCase(),nhismode,nmode,ncount,formula,his_start);
				}
			}//while rset loop end.
			/**导入插入子集区域的数据区域结束*/
			/**生成序号*/
			/**首次导入时,才需要进行序号生成*/
			if(sync==0&&!"1".equals(this.id_gen_manual)) {
                createRuleSequenceNo(seqHm,a0100s,dbpre,strDesT);
            }

			//写入唯一标识 seqnum
			IDGenerator idg=new IDGenerator(2,this.conn);
			String kq_id_str="";
			String kq_seqnum_id="";
			String mb_seqnum_id=""; //模板对应的考勤单据号指标
			if(this.infor_type==1)
			{
				//考勤申请模板
				TemplateTableParamBo tp=new TemplateTableParamBo(tabid,this.conn);
				String mapping=getKqParam(1,tp);
				String kqTab=getKqParam(2,tp);
				kq_seqnum_id=kqTab+"01";
			    kq_id_str=getKqParam(4,tp);
			    mb_seqnum_id=getKqParam(5,tp);
			}

			 if (mb_seqnum_id!=null&&mb_seqnum_id.length()>0) //生成过就不再生成了。
			{
				String tablename=null;
				if(this.isBEmploy()) {
                    tablename="g_templet_"+this.tabid;
                } else {
                    tablename=this.userview.getUserName()+"templet_"+this.tabid;
                }
				if(impOthTableName!=null&&impOthTableName.trim().length()>0)  //供高级花名册调用人事异动的人员引入功能，将数据导入到临时表中
                {
                    tablename=impOthTableName;
                }

				String sql="select * from "+tablename ;
				if(this.infor_type==1)
				{
					StringBuffer str = new StringBuffer();
					String temp[]=a0100s.split(",");
		         	if(temp!=null&&temp.length>0){

						int zheng=temp.length/999;
						int yu = temp.length%999;
						for (int j = 0; j < zheng; j++) {
							if(j!=0){
								str.append("or ");
							}
							str.append(" a0100 in (");
							for(int i=j*999;i<(j+1)*999;i++){
								if(i!=j*999){
									str.append(",");
								}
								str.append(temp[i]);
							}
							str.append(")");
						}
						if(zheng==0){
							if(yu>0){
								str.append(" a0100 in (");
								for(int i=zheng*999;i<zheng*999+yu;i++){
									if(i!=zheng*999){
										str.append(",");
									}
									str.append(temp[i]);
								}
								str.append(")");
							}
						}else{
							if(yu>0){
								str.append("or a0100 in (");
								for(int i=zheng*999;i<zheng*999+yu;i++){
									if(i!=zheng*999){
										str.append(",");
									}
									str.append(temp[i]);
								}
								str.append(")");
							}
						}

					}
					sql+=" where "+str+" and basepre='"+dbpre+"'";

				}
				else if(this.infor_type==2) //单位信息处理
                {
                    sql+=" where B0110 in ("+a0100s+") ";
                } else if(this.infor_type==3) //职位信息处理
                {
                    sql+=" where E01A1 in ("+a0100s+") ";
                }
				RowSet rowSet=dao.search(sql);
				while(rowSet.next())
				{
					/*
					String	seqnum = rowSet.getString("seqnum");
					if(seqnum==null||seqnum.trim().length()==0){
					    seqnum=CreateSequence.getUUID();
						if(this.infor_type==1)
						{
							dao.update("update "+tablename+" set seqnum='"+seqnum+"',submitflag=1 where a0100='"+rowSet.getString("a0100")+"' and  lower(basepre)='"+dbpre.toLowerCase()+"'");
						}
						else if(this.infor_type==2)
						{
							dao.update("update "+tablename+" set seqnum='"+seqnum+"',submitflag=1 where b0110='"+rowSet.getString("b0110")+"'");
						}
						else if(this.infor_type==3)
						{
							dao.update("update "+tablename+" set seqnum='"+seqnum+"',submitflag=1 where E01A1='"+rowSet.getString("E01A1")+"'");
						}
					}*/
					//自动生成考勤单据号
                    try{
                        if (mb_seqnum_id.length()>0){//生成过就不再生成了。
                            String  id_value = rowSet.getString(mb_seqnum_id);
                            if (id_value==null || id_value.length()<1){
                                if(kq_id_str.length()>0&&mb_seqnum_id.length()>0&&this.infor_type==1)
                                {
                                   String kq_id=idg.getId(kq_id_str);
                                    dao.update("update "+tablename+" set "+mb_seqnum_id+"='"+kq_id+"' where a0100='"+rowSet.getString("a0100")+"' and  lower(basepre)='"+dbpre.toLowerCase()+"'");
                                }
                            }
                        }

                    }catch(Exception e){
                        throw new GeneralException("模板中考勤指标对应出现问题,请重新对应考勤指标!");
                    }
				}
				PubFunc.closeDbObj(rowSet);

			}
			if(rset!=null) {
                rset.close();
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			if(!ErrorSetFlag){//如果是由于更新子集字段出的错
			    String errorMessage=ex.getMessage();
			    if(errorMessage.indexOf("操作数类型冲突")!=-1){
			        ex=new GeneralException("子集"+ErrorSetName+"中有字段的数据类型和模板不一致,请核实模板指标!");
			    }

			}
			throw GeneralExceptionHandler.Handle(ex);
		}

		return bflag;
	}


	/**
	 * 获得模板设置的考勤参数
	 * @param flag 1:模板与申请单指标对应关系  2：考勤申请单表名  3：考勤申请单序号 4：申请单序号生成串  5：模板对应考勤申请单序号指标
	 * @return
	 */
	private String getKqParam(int flag,TemplateTableParamBo tp)
	{
		String param_str="";
		if(flag==1) {
            param_str=tp.getKq_field_mapping();
        } else if(flag==2)
		{
			param_str=tp.getKq_setid();
		}
		else if(flag==3)
		{
			String kqTab=tp.getKq_setid();
			if(kqTab.length()>0) {
                param_str=kqTab+"01";
            }
		}
		else if(flag==4)
		{
			String kqTab=tp.getKq_setid();
			String kq_id_str="";
			if("Q11".equalsIgnoreCase(kqTab)) //加班
            {
                kq_id_str="Q11.Q1101";
            } else if("Q13".equalsIgnoreCase(kqTab)) //公出
            {
                kq_id_str="Q13.Q1301";
            } else if("Q15".equalsIgnoreCase(kqTab)) //请假
            {
                kq_id_str="Q15.Q1501";
            }
			param_str=kq_id_str;
		}
		else if(flag==5)
		{
			String mb_seqnum_id="";
			String mapping=tp.getKq_field_mapping();
			String kqTab=tp.getKq_setid();
			String kq_seqnum_id=kqTab+"01";
			if(kqTab.length()>0&&mapping.length()>0)
			{
				String[] temps=mapping.toLowerCase().split(",");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						String[] temp=temps[i].toLowerCase().split(":");
						if(temp[0].equalsIgnoreCase(kq_seqnum_id))
						{
							mb_seqnum_id=temp[1];
							break;
						}
					}
				}

			}
			param_str=mb_seqnum_id;
		}
		return param_str;
	}



	/**
	 * 导入子集区域对应的数据
	 * @param a0100s      人员编号列表
	 * @param dbpre		  应用库前缀
	 * @param xmlfmt      xml格式
	 * @param setname     子集
	 * @param field_name  业务模板表中的字段
	 * @throws GeneralException
	 */
	private void impSubDomainData(String a0100s,String dbpre,String xmlfmt,String setname,String field_name,int nhismode,int mode,int count,String formula,int his_start)throws GeneralException
	{
		if(xmlfmt==null|| "".equalsIgnoreCase(xmlfmt)) {
            return;
        }
		DbWizard dbw = new DbWizard(this.conn);
		TSubSetDomain setdomain=new TSubSetDomain(xmlfmt);
		setdomain.setUserview(this.userview);
		String fields=setdomain.getFields();
		String[]  fieldarr=StringUtils.split(fields,"`");
		StringBuffer buf=new StringBuffer();
		StringBuffer sql=new StringBuffer();
		StringBuffer sql2=new StringBuffer();
		StringBuffer strsql=new StringBuffer();
		StringBuffer strsql2=new StringBuffer();
		ArrayList paralist=new ArrayList();
		String tablename=dbpre+setname;
		boolean attachFlag=false;  //是否支持附件标识 true为支持附件 false为不支持附件 liuzy 20151028
		if(this.infor_type==2||this.infor_type==3) {
            tablename=setname;
        }
		ArrayList fieldlist=new ArrayList();
		/**人员*/
	//	if(setname.charAt(0)=='A')
		{
			strsql.append("select i9999");
			strsql2.append("select i9999");
			for(int i=0;i<fieldarr.length;i++)
			{
				String name=fieldarr[i];

				//当子集中包含attach时，将attach设置为true liuzy 20151028
				if("attach".equals(name)){
					attachFlag=true;
				}

				FieldItem item=DataDictionary.getFieldItem(name);
				if(item==null|| "0".equals(item.getUseflag())) {
                    continue;
                }

	/*			if(isBEmploy())//员工通过自助平台发动申请
				{
					if(this.userview.analyseFieldPriv(name,0).equals("0"))
						continue;
				}
				else  */
				{
					if("0".equals(this.userview.analyseFieldPriv(name))&& "0".equals(this.UnrestrictedMenuPriv_Input)) {
                        continue;
                    }
				}
				fieldlist.add(item);
				buf.append(",");
				buf.append(name);
			}//for i loop end.
			if(buf.length()==0) {
                return;
            }

			//在指标集fieldSet中查询子表是否支持附件，只有子表支持附件，在模板中设置子集包含附件，才会在页面中显示附件，liuzy 20151028
			ContentDAO dao=new ContentDAO(this.conn);
			try {
				String flagsql="select multimedia_file_flag  from fieldSet where fieldSetId='"+setname+"'";
				RowSet flagset=dao.search(flagsql);
				if(flagset.next()){
					if("1".equals(flagset.getString("multimedia_file_flag")) && attachFlag){
						attachFlag=true;
					}else{
						attachFlag=false;
					}
				}else{
					attachFlag=false;
				}
				PubFunc.closeDbObj(flagset);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			strsql.append(buf.toString());

			//如果支持附件，查询子集数据时需将GuidKey也查询出来，方便去hr_multimedia_file查找附件 liuzy 20151028
			if(attachFlag){
				if(dbw.isExistField(tablename, "GuidKey",false)) {
                    strsql.append(",GuidKey");
                }
			}

			strsql.append(" from ");
			strsql.append(tablename);
			strsql2.append(buf.toString());
			strsql2.append(" from ");
			strsql2.append(tablename);
			if(this.infor_type==1){
				strsql.append(" where a0100=? ");
				strsql2.append(" where a0100=' ");
			}
			else if(this.infor_type==2){
				strsql.append(" where b0110=? ");
				strsql2.append(" where b0110=' ");
			}
			else if(this.infor_type==3){
				strsql.append(" where e01a1=? ");
				strsql2.append(" where e01a1=' ");
			}
			String[] a0100arr=StringUtils.split(a0100s,",");
			HashMap i9999Map=getSubSetI9999sByA0100(tablename,nhismode,formula,a0100arr);
			for(int i=0;i<a0100arr.length;i++)
			{
				String a0100=a0100arr[i];
				a0100=a0100.substring(1, a0100.length()-1);
				paralist.clear();
				paralist.add(a0100);
				sql.setLength(0);
				sql.append(strsql.toString());
				sql2.setLength(0);
				sql2.append(strsql2.toString().trim());
				sql2.append(a0100+"'");

					/**求子集序号列表*/
				//	ArrayList i9999list=getSubSetI9999s(tablename,a0100);
					ArrayList i9999list=new ArrayList();
					if(i9999Map.get(a0100)!=null) {
                        i9999list=(ArrayList)i9999Map.get(a0100);//getSubSetI9999s(tablename,a0100);
                    }
					int size=i9999list.size();
					if(size>0)
					{
						/**初值为-1*/
						String curri9999="-1";
						if(nhismode==1) //当前记录
						{
								if(size>=1) {
                                    curri9999=(String)i9999list.get(size-1);
                                }
							sql.append(" and I9999=?");
							paralist.add(curri9999);

						}
						else if(nhismode==3) //条件定位
						{

							String[] preCond=getPrefixCond(formula);
							String cond=preCond[1]!=null?preCond[1]:"";
							if(cond.length()>0)
							{
								FactorList factorlist=new FactorList(preCond[1],preCond[2],"");
								String strw=factorlist.getSingleTableSqlExpression(tablename);
								sql.append(" and ( ");
								sql.append(strw);
								sql.append(" ) order by I9999");
							}
							else
							{
								sql.append(" and 1=1");
							}

						}
						else if(nhismode==4) //条件序号
						{

							String[] preCond=getPrefixCond(formula);
							String cond=preCond[1]!=null?preCond[1]:"";
							if(cond.length()>0)
							{
								FactorList factorlist=new FactorList(preCond[1],preCond[2],"");
								String strw=factorlist.getSingleTableSqlExpression(tablename);
								sql.append(" and ( ");
								sql.append(strw);
								sql.append(" ) ");
								sql2.append(" and ( ");
								sql2.append(strw);
								sql2.append(" ) ");
							}
							else
							{
								sql.append(" and 1=1 ");
								sql2.append(" and 1=1 ");
							}
						//	 i9999list=getSubSetI9999s(sql2.toString()+" order by i9999");
							 size=i9999list.size();
							if(size>0)
							{
								switch(mode)
								{
							case 0:
									if(size>=count)//子集记录大于要取的的记录数
									{
										if(size==count) {
                                            curri9999=(String)i9999list.get(0);
                                        } else
										{
											if(count!=0) {
                                                curri9999=(String)i9999list.get(size-count);
                                            } else {
                                                curri9999=(String)i9999list.get(size-count-1);
                                            }
										}
									}
								sql.append(" and I9999=?");
								paralist.add(curri9999);
								break;
							case 1://倒数...条（最近）
								if(his_start==0){
									if(size>=count)
									{
										if(size==count) {
                                            curri9999=(String)i9999list.get(0);
                                        } else {
                                            curri9999=(String)i9999list.get(size-count);
                                        }
									}
									sql.append(" and I9999>=? order by I9999");
								}else{
									if(his_start>size){
										curri9999="-1";
										sql.append(" and I9999<=? order by I9999");
									}else{
										if(size>=count){
											String curri99992=(String)i9999list.get(size-his_start);
											sql.append(" and I9999<=? ");
											paralist.add(curri99992);
											if(size<his_start+count) {
                                                curri9999=(String)i9999list.get(0);
                                            } else {
                                                curri9999=(String)i9999list.get(size-count-(his_start-1));
                                            }
											sql.append(" and I9999>=? order by I9999");

										}else{
											 curri9999=(String)i9999list.get(size-his_start);
											 sql.append(" and I9999<=? order by I9999");
										}

									}
								}
								paralist.add(curri9999);
								break;
							case 2://正数第...条(最初第)
								if(size>=count) {
                                    curri9999=(String)i9999list.get(count-1);
                                }
                                                sql.append(" and I9999=?");
                                                paralist.add(curri9999);
					break;
							case 3://正数...条（最初）
								if(his_start==0){
									if(size>=count){
										curri9999=(String)i9999list.get(count-1);
										sql.append(" and I9999<=? order by I9999");
									}else{
										sql.append(" and I9999>=? order by I9999");
									}
								}else{
									if(his_start>size){
										curri9999="-1";
										sql.append(" and I9999<=? order by I9999");
									}else{
										if(size>=count){
											String curri99992=(String)i9999list.get(his_start-1);
											sql.append(" and I9999>=? ");
											paralist.add(curri99992);
											if(size<his_start+count){
												sql.append(" and I9999>=? ");
												curri9999=curri99992;
											}else{
											curri9999=(String)i9999list.get(his_start+count-2);
											sql.append(" and I9999<=? order by I9999");
											}
										}else{
											 curri9999=(String)i9999list.get(his_start-1);
											 sql.append(" and I9999>=? order by I9999");
										}

									}
								}
								paralist.add(curri9999);
								break;
							}
							}


						}
						else if(nhismode==2){
						switch(mode)
							{
						case 0:
								if(size>=count)//子集记录大于要取的的记录数
								{
									if(size==count) {
                                        curri9999=(String)i9999list.get(0);
                                    } else
									{
										if(count!=0) {
                                            curri9999=(String)i9999list.get(size-count);
                                        } else {
                                            curri9999=(String)i9999list.get(size-count-1);
                                        }
									}
								}
							sql.append(" and I9999=?");
							paralist.add(curri9999);
                                        break;
						case 1://倒数...条（最近）
							if(his_start==0){
								if(size>=count)
								{
                                                    if(size==count) {
                                        curri9999=(String)i9999list.get(0);
                                    } else {
                                        curri9999=(String)i9999list.get(size-count);
                                    }
								}
								sql.append(" and I9999>=? order by I9999");
							}else{
								if(his_start>size){
									curri9999="-1";
									sql.append(" and I9999<=? order by I9999");
								}else{
									if(size>=count){
										String curri99992=(String)i9999list.get(size-his_start);
										sql.append(" and I9999<=? ");
										paralist.add(curri99992);
										if(size<his_start+count) {
                                            curri9999=(String)i9999list.get(0);
                                        } else {
                                            curri9999=(String)i9999list.get(size-count-(his_start-1));
                                        }
										sql.append(" and I9999>=? order by I9999");

									}else{
										 curri9999=(String)i9999list.get(size-his_start);
										 sql.append(" and I9999<=? order by I9999");
									}

								}
							}
							paralist.add(curri9999);
							break;
						case 2://正数第...条(最初第)
							if(size>=count) {
                                curri9999=(String)i9999list.get(count-1);
                            }
							sql.append(" and I9999=?");
							paralist.add(curri9999);
							break;
						case 3://正数...条（最初）
							if(his_start==0){
								if(size>=count){
									curri9999=(String)i9999list.get(count-1);
									sql.append(" and I9999<=? order by I9999");
								}else{
									sql.append(" and I9999>=? order by I9999");
								}
							}else{
								if(his_start>size){
									curri9999="-1";
									sql.append(" and I9999<=? order by I9999");
								}else{
									if(size>=count){
										String curri99992=(String)i9999list.get(his_start-1);
										sql.append(" and I9999>=? ");
										paralist.add(curri99992);
										if(size<his_start+count){
											sql.append(" and I9999>=? ");
											curri9999=curri99992;
										}else{
										curri9999=(String)i9999list.get(his_start+count-2);
										sql.append(" and I9999<=? order by I9999");
										}
									}else{
										 curri9999=(String)i9999list.get(his_start-1);
										 sql.append(" and I9999>=? order by I9999");
									}

								}
							}
							paralist.add(curri9999);
							break;
						}
						}


					}


				try
				{
					RowSet rset=dao.search(sql.toString(),paralist);
					ArrayList reclist=getRecordValue(fieldlist,rset);
					String xmlcontent=setdomain.outContentxml(fieldlist, reclist, attachFlag);

					StringBuffer task_str=new StringBuffer("");
					if(getTasklist().size()>0)
					{
						for(int j=0;j<this.tasklist.size();j++)
						{
							if(this.tasklist.get(j)!=null&&((String)this.tasklist.get(j)).trim().length()>0) {
                                task_str.append(","+(String)this.tasklist.get(j));
                            }
						}

						String _tablename="templet_"+this.tabid;
						if(impOthTableName!=null&&impOthTableName.trim().length()>0)  //供高级花名册调用人事异动的人员引入功能，将数据导入到临时表中
                        {
                            _tablename=impOthTableName;
                        }

						if(this.infor_type==1) {
                            rset=dao.search("select ins_id from "+_tablename+" where a0100='"+a0100+"' and lower(basepre)='"+dbpre.toLowerCase()+"' and  task_id in ("+task_str.substring(1)+")");
                        } else if(this.infor_type==2) {
                            rset=dao.search("select ins_id from "+_tablename+" where b0110='"+a0100+"' and task_id in ("+task_str.substring(1)+")");
                        } else if(this.infor_type==3) {
                            rset=dao.search("select ins_id from "+_tablename+" where e01a1='"+a0100+"'   and task_id in ("+task_str.substring(1)+")");
                        }
						if(rset.next())
						{
							RecordVo vo=new RecordVo(_tablename);
							if(this.infor_type==1)
							{
								vo.setString("a0100", a0100);
								vo.setString("basepre", dbpre);
							}
							else if(this.infor_type==2) {
                                vo.setString("b0110",a0100);
                            } else if(this.infor_type==3) {
                                vo.setString("e01a1",a0100);
                            }
							vo.setInt("ins_id",rset.getInt("ins_id"));
							vo.setString(field_name, xmlcontent);
							dao.updateValueObject(vo);
						}
					}
					else
					{
						String _tablename=this.bz_tablename;
						if(impOthTableName!=null&&impOthTableName.trim().length()>0)  //供高级花名册调用人事异动的人员引入功能，将数据导入到临时表中
                        {
                            _tablename=impOthTableName;
                        }
						RecordVo vo=new RecordVo(_tablename);
						if(this.infor_type==1)
						{
							vo.setString("a0100", a0100);
							vo.setString("basepre", dbpre);
						}
						else if(this.infor_type==2) {
                            vo.setString("b0110",a0100);
                        } else if(this.infor_type==3) {
                            vo.setString("e01a1",a0100);
}
                                vo.setString(field_name, xmlcontent);
						dao.updateValueObject(vo);
					}
					PubFunc.closeDbObj(rset);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}

			}//for a0100 i loop end.
		}
	}

	 /**
	 * 取得所有子集序号列表
	 * @param subtab
	 * @param a0100
	 * @return
	 */
	private HashMap getSubSetI9999sByA0100(String subtab,int nhismode,String formula,String[] a0100arr)
	{
		HashMap i9999Map=new HashMap<String,String>();
		try {


			StringBuffer whl=new StringBuffer("");
			String itemid="";
			if(this.infor_type==1)
			{
				whl.append(" and a0100 in ('xxxff'");
				itemid="a0100";
			}
			else if(this.infor_type==2)
			{
				whl.append(" and b0110 in ('xxxff'");
				itemid="b0110";
			}
			else if(this.infor_type==3)
			{
				whl.append(" and e01a1 in ('xxxff'");
				itemid="e01a1";
			}
			String sql2="select "+itemid+",i9999 from "+subtab+" where 1=1 ";

			for(int i=0;i<a0100arr.length;i++)
			{
				String a0100=a0100arr[i];
				a0100=a0100.substring(1, a0100.length()-1);
				whl.append(",'"+a0100+"'");
			}
			whl.append(")");
			if(nhismode==4) //条件序号
			{
				String[] preCond=getPrefixCond(formula);
				String cond=preCond[1]!=null?preCond[1]:"";
				if(cond.length()>0)
				{
							FactorList factorlist=new FactorList(preCond[1],preCond[2],"");
							String strw=factorlist.getSingleTableSqlExpression(subtab);
							whl.append(" and ( ");
							whl.append(strw);
							whl.append(" ) ");

				}
			}
			sql2+=whl.toString()+" order by "+itemid+",I9999";

			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(sql2.toString());
			String oldValue="";
			ArrayList i9999List=new ArrayList<String>();
			while(rset.next())
			{
					String currentValue=rset.getString(itemid);
					String i9999=rset.getString("i9999");
					if(oldValue.length()==0) {
                        oldValue=currentValue;
                    }
					if(oldValue.equalsIgnoreCase(currentValue)) {
                        i9999List.add(i9999);
                    } else {
						i9999Map.put(oldValue,i9999List);
						oldValue=currentValue;
						i9999List=new ArrayList<String>();
						i9999List.add(i9999);
					}

			}
			i9999Map.put(oldValue,i9999List);
			PubFunc.closeDbObj(rset);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return i9999Map;
	}



	/**
	 * 判断RowSet集合中是否包含某个字段
	 * @param rset RowSet集合
	 * @param field 字段名
	 * @return
	 */
	public boolean isRowSetExistField(RowSet rset,String field)
	{
		boolean flag=false;
		try {
			ResultSetMetaData rsmd = rset.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for(int i=1;i<=columnCount;i++){
			   if(field.equals(rsmd.getColumnName(i))){
			      flag=true;
			      break;
			   }
		     }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 取得记录串
	 * @param list
	 * @param rset
	 * @return
	 */
	private ArrayList getRecordValue(ArrayList list ,RowSet rset)
	{
		ArrayList reclist=new ArrayList();
		String value="";
		try
		{
			boolean flagGuidKey=false;  //定义该标志是为了判断查询的列是否包含GuidKey
			flagGuidKey=isRowSetExistField(rset, "GuidKey");

			while(rset.next())
			{
			  ArrayList valuelist=new ArrayList();
			  String i9999=rset.getString("i9999");
			  valuelist.add(i9999);
			  for(int i=0;i<list.size();i++)
			  {
				FieldItem item=(FieldItem)list.get(i);
				String field_type=item.getItemtype();
				String field_name=item.getItemid();
				if("M".equalsIgnoreCase(field_type))
				{
					value=Sql_switcher.readMemo(rset,field_name);
				}
				else if("D".equalsIgnoreCase(field_type))
				{
					/**yyyy-MM-dd*/
					value=PubFunc.FormatDate(rset.getDate(field_name));

				}
				else if("N".equalsIgnoreCase(field_type))
				{
					int ndec=item.getDecimalwidth();//小数点位数
					value=PubFunc.DoFormatDecimal(rset.getString(field_name),ndec);
				}
				else //'A'
				{
					String codevalue=rset.getString(field_name);
					value=((codevalue==null)?"":codevalue.trim());
				}
  			    valuelist.add(value);
			  }//for i loop end.

			  //当包含附件的时候，需要根据GuidKey的值将附件的信息查询出来 liuzy 20151028
			  if(flagGuidKey){
			      String GuidKey=rset.getString("GuidKey");
				  ContentDAO dao=new ContentDAO(this.conn);
					try {
						String sql="select id,filename,path,srcfilename,topic,ext from hr_multimedia_file where childguid='"+GuidKey+"'";
						RowSet flagset=dao.search(sql);
						int m=0;
						String valuestr="";
						while(flagset.next()){
							m++;
							String id=flagset.getString("id");                   //文件唯一标识
							String filename=flagset.getString("filename");       //编码后文件名
							String path=flagset.getString("path");               //文件上传路径
							String srcfilename=flagset.getString("topic")+flagset.getString("ext"); //原始文件名
							String text=filename+"|"+path+"|"+srcfilename+"|"+id+"|"+m ;
							valuestr+=text+",";
						}
						if(valuestr.length()>0){
							valuestr=valuestr.substring(0, valuestr.length()-1);
							valuelist.add(valuestr);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}

			  }
			  valuelist.add(1, new Date().getTime());//时间戳
			  valuelist.add("false");//ishavechange
			  valuelist.add("");//record_key_id
			  reclist.add(valuelist);
			}//for while end.
		}
		catch(Exception ex)
{
    ex.printStackTrace();
		}
		return reclist;
	}

	private String getPattern(String strPattern,String formula)
	{
		int iS,iE;
		String result="";
		String sSP="<"+strPattern+">";
		iS=formula.indexOf(sSP);
		String sEP="</"+strPattern+">";
		iE=formula.indexOf(sEP);
		if(iS>=0 && iS<iE)
		{
			result=formula.substring(iS+sSP.length(), iE);
		}
		return result;
	}
	/**
	 * 解释Formula字段的内容
	 * for example
	 * ssssfsf<EXPR>1+2</EXPR><FACTOR>A0303=222,A0404=pppp</FACTOR>
	 * @return
	 */
	  private String[] getPrefixCond(String formula)
	  {
		   String[] preCond=new String[3];
		   int idx=formula.indexOf("<");
		   if(idx==-1)
		   {
			   preCond[0]=formula;
		   }
		   else
		   {
			   preCond[0]=formula.substring(0, idx);
			   preCond[2]=getPattern("FACTOR",formula)+",";
			   preCond[2]=preCond[2].replaceAll(",", "`");
			   preCond[1]=getPattern("EXPR",formula);
		   }
		   return preCond;
	  }
   /*
   private String[] getPrefixCond(String formula)
   {
	   String[] preCond=new String[3];
	   StringBuffer buf=new StringBuffer();
	   buf.append("<?xml version='1.0' encoding='GB2312'?");
	   buf.append("<formula>");
	   buf.append(formula);
	   buf.append("</formula>");
	   StringReader reader=new StringReader(buf.toString());
	   try
	   {
		   Document doc=saxbuilder.build(reader);
		   Element root=doc.getRootElement();
		   preCond[0]=root.getText();
		   List list=XPath.selectNodes(root,"//EXPR");
		   if(list!=null&&list.size()>0)
		   {
			   Element expr=(Element)list.get(0);
			   preCond[1]=expr.getText();

		   }
		   list=XPath.selectNodes(root,"//FACTOR");
		   if(list!=null&&list.size()>0)
		   {
			   Element factor=(Element)list.get(0);
			   preCond[2]=factor.getText();
		   }
	   }
	   catch(Exception ex)
	   {
		   ex.printStackTrace();
	   }
	   return preCond;
   }
   */
	/**
	 * 取得所有子集序号列表
	 * @param subtab
	 * @param a0100
	 * @return
	 */
	private ArrayList getSubSetI9999s(String subtab,String a0100)
	{
		ArrayList paralist=new ArrayList();
		paralist.add(a0100);
		StringBuffer buf=new StringBuffer();
		buf.append("select I9999 from ");
		buf.append(subtab);
		if(this.infor_type==1) {
            buf.append(" where a0100=?  ");
        } else if(this.infor_type==2) {
            buf.append(" where b0110=? ");
        } else if(this.infor_type==3) {
            buf.append(" where e01a1=? ");
        }
		buf.append(" order by I9999");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString(),paralist);
			paralist.clear();
			while(rset.next()) {
                paralist.add(rset.getString("I9999"));
            }
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return paralist;
	}

		/**
		 * 取得所有子集序号列表
		 * @param subtab
		 * @param a0100
		 * @return
		 */
		private ArrayList getSubSetI9999s(String sql)
		{
			ArrayList paralist=new ArrayList();
			try
			{
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rset=dao.search(sql);
				while(rset.next()) {
                    paralist.add(rset.getString("I9999"));
                }
				PubFunc.closeDbObj(rset);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return paralist;
		}

	/**
	 * 取得模板中的对象的文书号
	 * @param fieldname
	 * @param strDesT
	 * @return
	 */
	private String getSequenceNoInEqual(String fieldname,String strDesT,String item_str,int prefix_field_len)
	{
		StringBuffer buf=new StringBuffer();
		buf.append("select "+fieldname+","+item_str+" from "+strDesT+" where "+fieldname+"=(");
		buf.append("select max(");
		buf.append(fieldname);
		buf.append(") as seqno from ");
		buf.append(strDesT+" ) ");
		String seqno=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
			{
				seqno=rset.getString(fieldname);
			    String _str=rset.getString(item_str);
			    if(_str!=null&&_str.trim().length()>0)
			    {
						if(prefix_field_len!=0&&_str.length()>prefix_field_len)
						{
							_str=_str.substring(0, prefix_field_len);
						}
						seqno=seqno.replaceAll(_str,"");
			    }
			}
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{

		}
		return seqno;
	}



	/**
	 * 取得模板中的对象的文书号
	 * @param fieldname
	 * @param strDesT
	 * @return
	 */
	private String getSequenceNoInEqual(String fieldname,String strDesT)
	{
		StringBuffer buf=new StringBuffer();
		buf.append("select max(");
		buf.append(fieldname);
		buf.append(") as seqno from ");
		buf.append(strDesT);
		String seqno=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RowSet rset=dao.search(buf.toString());
			if(rset.next()) {
                seqno=rset.getString("seqno");
            }
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{

		}
		return seqno;
	}


	private RecordVo getFactoryIdVo(String id)
	{
		RecordVo vo=new RecordVo("id_factory");
		try
		{

			ContentDAO dao=new ContentDAO(this.conn);
			vo.setString("sequence_name", id);
			vo=dao.findByPrimaryKey(vo);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return vo;
	}


	/**
	 * 根据序号生成器规则，生成对应的序号
	 * @param seqHm
	 * @param a0100s
	 * @param dbpre
	 * @param strDesT
	 */
	private void createRuleSequenceNo(HashMap seqHm,String a0100s,String dbpre,String strDesT)throws GeneralException
	{
        Iterator seq=seqHm.entrySet().iterator();
        IDGenerator idg=new IDGenerator(2,this.conn);
        String[] a0100arr=StringUtils.split(a0100s,",");
        String a0100=null;
        StringBuffer buf=new StringBuffer();
        try
        {
        	ContentDAO dao=new ContentDAO(this.conn);
			String seq_no=null;
			String seqname=null;

			HashMap columnMap=new HashMap();
			RowSet rowSet=dao.search("select * from "+strDesT+" where 1=2");
			ResultSetMetaData mt=rowSet.getMetaData();
			for(int i=0;i<mt.getColumnCount();i++)
			{
				columnMap.put(mt.getColumnName(i+1).toLowerCase(),"1");
			}

			String end_str="_1";
			if(operationtype==0||operationtype==5) {
                end_str="_2";
            }

			while(seq.hasNext())
			{
				Entry entry=(Entry)seq.next();
				String fieldname=(String)entry.getKey();
				FieldItem fielditem=DataDictionary.getFieldItem(fieldname);
				if(fielditem==null) {
                    continue;
                }
				seqname=(String)entry.getValue();

				RecordVo factoryVo=getFactoryIdVo(seqname);
				if(fielditem.getC_rule()==1)//同单同号,同业务中的对象文书号一样
				{

					String prefix_field_value="";
					String prefix_field=factoryVo.getString("prefix_field");
					if(prefix_field!=null&&prefix_field.trim().length()>0&&columnMap.get(prefix_field.toLowerCase()+end_str)!=null)
					{
						seq_no=getSequenceNoInEqual(fieldname+"_2",strDesT,prefix_field+end_str,fielditem.getPrefix_field_len());
						if(seq_no==null|| "".equalsIgnoreCase(seq_no)) {
                            seq_no=idg.getId(seqname);
                        }
						if(seq_no.length()>fielditem.getItemlength()) {
                            throw new GeneralException(ResourceFactory.getProperty("error.seqno.length"));
                        }

						for(int i=0;i<a0100arr.length;i++)
						{
							a0100=a0100arr[i];
							if(a0100.indexOf("'")==-1) {
                                a0100="'"+a0100+"'";
                            }
							prefix_field_value="";

							String _subStr=" a0100="+a0100+" and  upper(basepre)='"+dbpre.toUpperCase()+"'";
							if(this.infor_type==2) {
                                _subStr=" b0110="+a0100+"";
                            }
							if(this.infor_type==3) {
                                _subStr=" e01a1="+a0100+"";
                            }
							rowSet=dao.search("select "+prefix_field+end_str+" from "+strDesT+" where  "+_subStr);
							if(rowSet.next())
							{
								if(rowSet.getString(1)!=null&&rowSet.getString(1).trim().length()>0)
								{
									int prefix_field_len=fielditem.getPrefix_field_len();
									prefix_field_value=rowSet.getString(1);
									if(prefix_field_len!=0&&prefix_field_value.length()>prefix_field_len)
									{
										prefix_field_value=prefix_field_value.substring(0, prefix_field_len);
									}
								}
							}

							buf.setLength(0);

					        buf.append("update ");
					        buf.append(strDesT);
					        buf.append(" set ");
					        buf.append(fieldname);
					        buf.append("_2='");
					        buf.append(prefix_field_value+seq_no);
					        buf.append("' where ");
					        buf.append(_subStr);
							dao.update(buf.toString());
						}
					}
					else
					{
						seq_no=getSequenceNoInEqual(fieldname+"_2",strDesT);
						if(seq_no==null|| "".equalsIgnoreCase(seq_no)) {
                            seq_no=idg.getId(seqname);
                        }
						if(seq_no.length()>fielditem.getItemlength()) {
                            throw new GeneralException(ResourceFactory.getProperty("error.seqno.length"));
                        }
						for(int i=0;i<a0100arr.length;i++)
						{
							buf.setLength(0);
							a0100=a0100arr[i];
							if(a0100.indexOf("'")==-1) {
                                a0100="'"+a0100+"'";
                            }
							String _subStr=" a0100="+a0100+" and  upper(basepre)='"+dbpre.toUpperCase()+"'";
							if(this.infor_type==2) {
                                _subStr=" b0110="+a0100+"";
                            }
							if(this.infor_type==3) {
                                _subStr=" e01a1="+a0100+"";
                            }

					        buf.append("update ");
					        buf.append(strDesT);
					        buf.append(" set ");
					        buf.append(fieldname);
					        buf.append("_2='");
					        buf.append(seq_no);
					        buf.append("' where  ");
					        buf.append(_subStr);
							dao.update(buf.toString());
						}
					}
				}
				else//同单异号
				{
					String prefix_field=factoryVo.getString("prefix_field");
                        for(int i=0;i<a0100arr.length;i++)
					{
						a0100=a0100arr[i];
                            if(a0100.indexOf("'")==-1) {
a0100="'"+a0100+"'";
                        }
						String _subStr=" a0100="+a0100+" and  upper(basepre)='"+dbpre.toUpperCase()+"'";
						if(this.infor_type==2) {
                            _subStr=" b0110="+a0100+"";
                        }
						if(this.infor_type==3) {
                            _subStr=" e01a1="+a0100+"";
                        }

						String prefix_field_value="";
						if(prefix_field!=null&&prefix_field.trim().length()>0&&columnMap.get(prefix_field.toLowerCase()+end_str)!=null)
						{
							rowSet=dao.search("select "+prefix_field+end_str+" from "+strDesT+" where "+_subStr);
							if(rowSet.next())
							{
								if(rowSet.getString(1)!=null&&rowSet.getString(1).trim().length()>0)
								{
									int prefix_field_len=fielditem.getPrefix_field_len();
									prefix_field_value=rowSet.getString(1);
									if(prefix_field_len!=0&&prefix_field_value.length()>prefix_field_len)
									{
										prefix_field_value=prefix_field_value.substring(0, prefix_field_len);
									}
								}
							}


						}
						//判断该记录是否已存在序号
						buf.setLength(0);
                                buf.append("select  ");
				        buf.append(fieldname);
				        buf.append("_2 from ");
                                buf.append(strDesT);
				        buf.append(" where ");
				        buf.append(_subStr);
				        rowSet=dao.search(buf.toString());
				    	if(rowSet.next())
						{
							if(rowSet.getString(1)!=null&&rowSet.getString(1).trim().length()>0)
							{
								continue;
							}
						}
						if(fielditem.isByprefix()&&prefix_field_value.length()>0) {
                            seq_no=idg.getId(seqname+"`"+prefix_field_value);
                        } else {
                            seq_no=idg.getId(seqname);
                        }

						if(seq_no.length()>fielditem.getItemlength()) {
                            throw new GeneralException(ResourceFactory.getProperty("error.seqno.length"));
                        }
						buf.setLength(0);

				        buf.append("update ");
				        buf.append(strDesT);
				        buf.append(" set ");
				        buf.append(fieldname);
				        buf.append("_2='");
				        buf.append(prefix_field_value+seq_no);
				        buf.append("' where ");
				        buf.append(_subStr);
						dao.update(buf.toString());
					}
				}
				//seq_no=seq_no.substring(0,fielditem.getItemlength());
        }


        if(rowSet!=null) {
rowSet.close();
            }
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);
        }
	}


	/**
	 * 根据部门获得单位值
	 * @param value
	 * @return
	 */
	public  String  getB0110(String value)
	{
		String b0110="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=null;
			while(true)
			{
				rowSet=dao.search("select codesetid,codeitemid from organization where  codeitemid=(select parentid from organization where codeitemid='"+value+"' )");
				if(rowSet.next())
				{
					String codesetid=rowSet.getString("codesetid");
					String codeitemid=rowSet.getString("codeitemid");
					if("UN".equalsIgnoreCase(codesetid))
					{
						b0110=codeitemid;
						break;
					}
					else {
                        value=codeitemid;
                    }
				}

			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return b0110;
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


	/**
	 * 导入数据
* @param a0100s
* @param dbpre
	 * @return
	 * @throws GeneralException
*/
public boolean impDataFromArchive(ArrayList a0100s,String dbpre)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			if(a0100s.size() == 0) {
                return false;
            }
			ArrayList dbList=DataDictionary.getDbpreList();
			for(int i=0;i<dbList.size();i++)
			{
				String pre=(String)dbList.get(i);
				if(pre.equalsIgnoreCase(dbpre)) {
                    dbpre=pre;
                }
			}
			StringBuffer stra0100 = getA0100String(a0100s);
			if(a0100s.size()==1&&isBEmploy()) //员工通过自助平台发动申请
			{

				String	strDesT="g_templet_"+this.tabid;
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rowSet=dao.search("select count(*) from "+strDesT+" where a0100="+stra0100+" and lower(basepre)='"+dbpre.toLowerCase()+"'");
				if(rowSet.next())
				{
					if(rowSet.getInt(1)>0) {
                        impDataFromArchive(stra0100.toString(),dbpre,1);
                    } else {
                        impDataFromArchive(stra0100.toString(),dbpre,0);
                    }
			}
				else {
                    impDataFromArchive(stra0100.toString(),dbpre,0);
                }
				PubFunc.closeDbObj(rowSet);
			}
			else {
                impDataFromArchive(stra0100.toString(),dbpre,0);
            }

			//自动计算临时变量
			String where="";
			Object[] dbarr=null;
			if(this.infor_type==1)
			{
				String tablename=this.bz_tablename;
				if(a0100s.size()==1&&isBEmploy()) {
                    tablename="g_templet_"+this.tabid;
                }
				ArrayList dblist=searchDBPreList(tablename);
				dbarr=dblist.toArray();
				if(a0100s.size()==1&&isBEmploy()) //员工通过自助平台发动申请
				{
					this.bz_tablename="g_templet_"+this.tabid;
					 where=" where lower(g_templet_"+this.tabid+".basepre)='"+dbpre.toLowerCase()+"'  and g_templet_"+this.tabid+".a0100="+stra0100;
				}
			}
			this.onlyComputeFieldVar=true;
			ArrayList fieldlist=getMidVariableList();
			addMidVarIntoGzTable(where,dbarr,fieldlist);
			this.onlyComputeFieldVar=false;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			String message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{
				String destTab=this.bz_tablename;
				if(a0100s.size()==1&&isBEmploy()) {
                    destTab="g_templet_"+this.tabid;
                }
				PubFunc.resolve8060(this.conn,destTab);
				throw GeneralExceptionHandler.Handle(new Exception("请重新操作!"));
			}
			else {
                throw GeneralExceptionHandler.Handle(ex);
       }
		}
		return bflag;
	}

	public StringBuffer getA0100String(ArrayList a0100s) {
		StringBuffer stra0100=new StringBuffer();
		for(int i=0;i<a0100s.size();i++)
		{
			if(i!=0) {
                stra0100.append(",");
            }
			stra0100.append("'");
			stra0100.append(((String)a0100s.get(i)).trim());
			stra0100.append("'");
		}
		return stra0100;
	}



	/**
	 * 一次性导入主集记录
	 * B0110_1,E0122_1,A0101_1,basepre,A0100,state
	 * @param a0100s
	 * @param dbpre
	 * @return
	 * @throws GeneralException
	 */
	private boolean impMainSetFromArchive(String a0100s,String dbpre)throws GeneralException
	{
		boolean bflag=true;
		StringBuffer strsql=new StringBuffer();

		StringBuffer str_dfields=new StringBuffer();
		StringBuffer str_sfields=new StringBuffer();
		if(this._static==10) //单位管理
		{
			str_dfields.append("b0110,state");
			str_sfields.append("b0110,0");
		}
		else if(this._static==11) //职位管理
		{
			str_dfields.append("e01a1,state");
			str_sfields.append("e01a1,0");
		}
		else
		{
			str_dfields.append("a0100,basepre,b0110_1,e01a1_1,e0122_1,a0101_1,a0000,state");
			str_sfields.append("a0100,'");
			str_sfields.append(dbpre);
			str_sfields.append("',b0110,e01a1,e0122,a0101,a0000,0");
		}


		str_dfields.append(",seqnum,submitflag");
		if(Sql_switcher.searchDbServer()==Constant.MSSQL)
		{

			str_sfields.append(",newid(),1");
		}
		else if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		{
			str_sfields.append(",sys_guid(),1");
		}



		String tablename=null;
		if(this.isBEmploy()) {
            tablename="g_templet_"+this.tabid;
        } else {
            tablename=this.userview.getUserName()+"templet_"+this.tabid;
        }
		if(impOthTableName!=null&&impOthTableName.trim().length()>0)  //供高级花名册调用人事异动的人员引入功能，将数据导入到临时表中
        {
            tablename=impOthTableName;
        }

		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList fieldlist=this.getAllFieldItem();
			String fieldname=null;
			boolean bphoto=false;
	//		switch(this.infor_type)
			{
	//			case 1: //人员信息处理
					for(int i=0;i<fieldlist.size();i++)
					{
						FieldItem fielditem=(FieldItem)fieldlist.get(i);
						if(fielditem.getVarible()==1) {
                            continue;
                        }
						if("0".equals(fielditem.getUseflag())&&!"photo".equals(fielditem.getItemid())) {
                            continue;
                        }
						if(this.infor_type==2||this.infor_type==3) {
                            if("codesetid".equalsIgnoreCase(fielditem.getItemid())|| "codeitemdesc".equalsIgnoreCase(fielditem.getItemid())|| "corcode".equalsIgnoreCase(fielditem.getItemid())|| "parentid".equalsIgnoreCase(fielditem.getItemid())|| "start_date".equalsIgnoreCase(fielditem.getItemid()))
							{
continue;}
                        }
						if("photo".equals(fielditem.getItemid()))
						{
							bphoto=true;
							continue;
						}
						if(fielditem.isChangeAfter()&&this.opinion_field!=null&&this.opinion_field.length()>0&&this.opinion_field.equalsIgnoreCase(fielditem.getItemid()))
						{
							continue;
						}
						boolean isOk=true;
						if(this.infor_type==1) //人员信息处理
                        {
                            isOk=fielditem.isPerson();
                        }
						if(fielditem.isMainSet()&&isOk&&fielditem.isChangeBefore())
						{
								fieldname=fielditem.getItemid()+"_1";
//								if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0){
//									fieldname=fielditem.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_1";
//									}//有主集isMainSet()条件限制
								if(str_dfields.indexOf(fieldname)!=-1) {
                                    continue;
                                }
								str_dfields.append(",");
								str_dfields.append(fieldname);
								str_sfields.append(",");
								str_sfields.append(fielditem.getItemid());
						}
					}
					strsql.append("insert into ");
					strsql.append(tablename);
					strsql.append("(");
					strsql.append(str_dfields.toString());
					strsql.append(") select ");
					strsql.append(str_sfields.toString());
					strsql.append(" from ");

					if(this.infor_type==1) //人员信息处理
					{
						strsql.append(dbpre);
						strsql.append("A01 where ");
						String temp[]=a0100s.split(",");
						if(temp!=null&&temp.length>0){

							int zheng=temp.length/999;
							int yu = temp.length%999;
							for (int j = 0; j < zheng; j++) {
								if(j!=0){
									strsql.append("or ");
								}
								strsql.append(" a0100 in (");
								for(int i=j*999;i<(j+1)*999;i++){
									if(i!=j*999){
										strsql.append(",");
									}
									strsql.append(temp[i]);
								}
								strsql.append(")");
							}
							if(zheng==0){
								if(yu>0){
									strsql.append(" a0100 in (");
									for(int i=zheng*999;i<zheng*999+yu;i++){
										if(i!=zheng*999){
											strsql.append(",");
										}
										strsql.append(temp[i]);
									}
									strsql.append(")");
								}
							}else{
								if(yu>0){
									strsql.append("or a0100 in (");
									for(int i=zheng*999;i<zheng*999+yu;i++){
										if(i!=zheng*999){
											strsql.append(",");
										}
										strsql.append(temp[i]);
									}
									strsql.append(")");
								}
							}

						}
						strsql.append(" and a0100 not in (select a0100 from ");
						strsql.append(tablename);
						strsql.append(" where upper(basepre)='");
						strsql.append(dbpre.toUpperCase());
						strsql.append("')");
					}
					else if(this.infor_type==2) //单位信息处理
					{
						strsql.append("B01 where b0110 in (");
						strsql.append(a0100s);
						strsql.append(") and b0110 not in (select b0110 from ");
						strsql.append(tablename);
						strsql.append("  )");
					}
					else if(this.infor_type==3) //职位信息处理
					{
						strsql.append("K01 where E01A1 in (");
						strsql.append(a0100s);
						strsql.append(") and E01A1 not in (select E01A1 from ");
						strsql.append(tablename);
						strsql.append("  )");
					}

//					break;
			}
			int n=dao.update(strsql.toString());
			//如果信息表中没有相应记录
			if(this.infor_type==2||this.infor_type==3)
			{
				String key_field="";
				String _str="insert into "+tablename+" ( ";
				if(this._static==10) //单位管理
				{
					_str+="b0110,state";
					key_field="b0110";
				}
				else if(this._static==11) //职位管理
				{
					_str+="e01a1,state";
					key_field="e01a1";
				}
				_str+=" ) select codeitemid,0 from organization where codeitemid in ("+a0100s+") and codeitemid not in (select "+key_field+" from "+tablename+" )";
				dao.update(_str);
			}


			if(this.infor_type==2||this.infor_type==3)
			{
				DbWizard dbw=new DbWizard(this.conn);
				String _name="b0110";
				if(this.infor_type==3) {
                    _name="e01a1";
                }

				String sql="update "+tablename+" set codeitemdesc_1=(select codeitemdesc from organization where "+tablename+"."+_name+"=organization.codeitemid ) where "+tablename+"."+_name+" in ("+a0100s+") ";
				dbw.execute(sql);
				if("1".equals(change_after_get_data))
        {
            RecordVo vo=new RecordVo(tablename.toLowerCase());
					if(vo.hasAttribute("codeitemdesc_2"))
					{
						sql="update "+tablename+" set codeitemdesc_2=(select codeitemdesc from organization where "+tablename+"."+_name+"=organization.codeitemid ) where "+tablename+"."+_name+" in ("+a0100s+") ";
						dbw.execute(sql);
					}
				}
				sql="update "+tablename+" set a0000=(select a0000 from organization where "+tablename+"."+_name+"=organization.codeitemid ) where "+tablename+"."+_name+" in ("+a0100s+") ";
				dbw.execute(sql);

				for(int i=0;i<fieldlist.size();i++)
				{

					FieldItem fielditem=(FieldItem)fieldlist.get(i);
					if(fielditem.getVarible()==1) {
                        continue;
                    }
					if("0".equals(fielditem.getUseflag())&&!"photo".equals(fielditem.getItemid())) {
                        continue;
                    }

				}
			}

			/**不导入照片信息，除人员调入操作外，全都从档案记录中取得照片*/
			if(bphoto)
			{
				/**photo ,ext*/
				strsql.setLength(0);
				DbWizard dbw=new DbWizard(this.conn);
				if(this.infor_type==1) //人员信息处理
				{
					String srctab=dbpre+"A00";
					dbw.updateRecord(tablename,srctab ,tablename+".A0100="+srctab+".A0100","photo=ole`"+tablename+".ext="+srctab+".ext",tablename+".a0100 in ("+a0100s+") and basepre='"+dbpre+"'",srctab+".flag='P'");
				}
				else if(this.infor_type==2) //单位信息处理
				{
					String srctab="B00";
					dbw.updateRecord(tablename,srctab ,tablename+".B0110="+srctab+".B0110","photo=ole`"+tablename+".ext="+srctab+".ext",tablename+".B0110 in ("+a0100s+")  ",srctab+".flag='P'");
				}
				else if(this.infor_type==3) //职位信息处理
				{
					String srctab="K00";
					dbw.updateRecord(tablename,srctab ,tablename+".E01A1="+srctab+".E01A1","photo=ole`"+tablename+".ext="+srctab+".ext",tablename+".E01A1 in ("+a0100s+")  ",srctab+".flag='P'");
				}
			}

			//写入唯一标识 seqnum
			/*
			if(Sql_switcher.searchDbServer()!=Constant.MSSQL)
			{
				String sql="select * from "+tablename ;
				if(this.infor_type==1){
					StringBuffer str = new StringBuffer();
					String temp[]=a0100s.split(",");
		         	if(temp!=null&&temp.length>0){

						int zheng=temp.length/999;
						int yu = temp.length%999;
						for (int j = 0; j < zheng; j++) {
							if(j!=0){
								str.append("or ");
							}
                                  str.append(" a0100 in (");
							for(int i=j*999;i<(j+1)*999;i++){
								if(i!=j*999){
									str.append(",");
								}
								str.append(temp[i]);
							}
							str.append(")");
						}
						if(zheng==0){
							if(yu>0){
								str.append(" a0100 in (");
								for(int i=zheng*999;i<zheng*999+yu;i++){
									if(i!=zheng*999){
										str.append(",");
									}
									str.append(temp[i]);
								}
								str.append(")");
							}
						}else{
							if(yu>0){
								str.append("or a0100 in (");
								for(int i=zheng*999;i<zheng*999+yu;i++){
									if(i!=zheng*999){
										str.append(",");
									}
									str.append(temp[i]);
                                                }
                                                str.append(")");
                                            }
                                        }

                                    }
                                    sql+=" where "+str+" and basepre='"+dbpre+"'";
                                }
                                else if(this.infor_type==2) //单位信息处理
					sql+=" where B0110 in ("+a0100s+") ";
				else if(this.infor_type==3) //职位信息处理
					sql+=" where E01A1 in ("+a0100s+") ";
				RowSet rowSet=dao.search(sql);
				while(rowSet.next())
				{
					String seqnum=CreateSequence.getUUID();
					if(this.infor_type==1)
                {
                    dao.update("update "+tablename+" set seqnum='"+seqnum+"',submitflag=1 where a0100='"+rowSet.getString("a0100")+"' and  lower(basepre)='"+dbpre.toLowerCase()+"'");
					}
					else if(this.infor_type==2)
					{
						dao.update("update "+tablename+" set seqnum='"+seqnum+"',submitflag=1 where b0110='"+rowSet.getString("b0110")+"'");
					}
					else if(this.infor_type==3)
					{
						dao.update("update "+tablename+" set seqnum='"+seqnum+"',submitflag=1 where E01A1='"+rowSet.getString("E01A1")+"'");
					}
				}

				PubFunc.closeDbObj(rowSet);

			} */
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	/**
	 * 从档案库导入照片，至临时表中去
	 * @param a0100s
	 * @param dbpre
	 */
	private void impPhotoFromArchive(String a0100s,String dbpre,String tablename)
	{
		String[] a0100arr=StringUtils.split(",");
		StringBuffer buf=new StringBuffer();
		buf.append("update ");

		for(int i=0;i<a0100arr.length;i++)
		{
			String a0100=a0100arr[i].substring(1, a0100arr[i].length()-1);


		}//for i loop end.

	}
	private void addFieldItem(Table table,int flag)throws GeneralException
	{
		ArrayList list=getAllFieldItem();
		Field temp=null;
		HashMap hm=new HashMap();
		if(this._static==10) //单位管理
		{
			temp=new Field("B0110",ResourceFactory.getProperty("column.sys.org"));
			temp.setDatatype(DataType.STRING);
			temp.setLength(30);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setCodesetid("UN");
			temp.setKeyable(true);
			table.addField(temp);
			hm.put("b0110",temp);
		}
		else if(this._static==11) //职位管理
		{
			temp=new Field("E01A1",ResourceFactory.getProperty("column.sys.pos"));
			temp.setDatatype(DataType.STRING);
			temp.setLength(30);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setCodesetid("@K");
			temp.setKeyable(true);
			table.addField(temp);
			hm.put("e01a1",temp);
		}
		else //人员
		{

			temp=new Field("A0100",ResourceFactory.getProperty("a0100.label"));
			temp.setDatatype(DataType.STRING);
			temp.setVisible(false);
    temp.setNullable(false);
    temp.setKeyable(true);
    temp.setSortable(false);
    temp.setLength(10);
    table.addField(temp);
    hm.put("a0100",temp);

			temp=new Field("BasePre",ResourceFactory.getProperty("label.dbase"));
			temp.setDatatype(DataType.STRING);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setSortable(false);
    temp.setKeyable(true);
			temp.setLength(3);
table.addField(temp);
			hm.put("basepre",temp);

		}
		/**人员顺序号*/
		temp=new Field("A0000","A0000");
		temp.setDatatype(DataType.INT);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);
		hm.put("a0000",temp);


		/**提交选中标志*/
		temp=new Field("submitflag","submitflag");
		temp.setDatatype(DataType.INT);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);
		hm.put("submitflag",temp);

		if(flag==0)
		{
			/**状态标志=0,=1来源消息(其它模板发过来的通知)*/
			temp=new Field("state","state");
			temp.setDatatype(DataType.INT);
			temp.setVisible(false);
			temp.setNullable(true);
			temp.setSortable(false);
			table.addField(temp);
			hm.put("state",temp);
		}
		else //审批表结构
		{
			temp=new Field("state","state");
			temp.setDatatype(DataType.INT);
temp.setVisible(false);
			temp.setNullable(true);
			temp.setSortable(false);
			temp.setKeyable(false);//key field
table.addField(temp);
			hm.put("state",temp);

			temp=new Field("ins_id","ins_id");
			temp.setDatatype(DataType.INT);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setSortable(false);
			temp.setKeyable(true);//key field
			table.addField(temp);
			hm.put("ins_id",temp);

			/**任务号*/
			temp=new Field("task_id","task_id");
			temp.setDatatype(DataType.INT);
			temp.setVisible(false);
			temp.setNullable(true);
			temp.setSortable(false);
			table.addField(temp);
			hm.put("task_id",temp);

			/**入库标志 wangrd 2013-11-26*/
			temp=new Field("archive_flag","archive_flag");
			temp.setDatatype(DataType.INT);
			temp.setVisible(false);
			temp.setNullable(true);
			temp.setSortable(false);
			table.addField(temp);
			hm.put("archive_flag",temp);
		}


//		CS需要的字段 start

		FieldItem tempitem0=new FieldItem();
		tempitem0.setItemtype("M");
		tempitem0.setItemid("AppProcess");
		table.addField(tempitem0);
		hm.put("appprocess",tempitem0);

		tempitem0=new FieldItem();		//打印预演报这个字段不存在
		tempitem0.setItemtype("M");
		tempitem0.setItemid("signature");
		table.addField(tempitem0);
		hm.put("signature",tempitem0);

		tempitem0=new FieldItem();
		tempitem0.setItemtype("D");
		tempitem0.setItemid("LastTime");
table.addField(tempitem0);
hm.put("lasttime",tempitem0);

		tempitem0=new FieldItem();
		tempitem0.setItemtype("A");
tempitem0.setItemid("AppUser");
tempitem0.setItemlength(200);
table.addField(tempitem0);
		hm.put("appuser",tempitem0);

		tempitem0=new FieldItem();
		tempitem0.setItemtype("A");
		tempitem0.setItemid("ChgPK32");
		tempitem0.setItemlength(50);
		table.addField(tempitem0);
		hm.put("chgpk32",tempitem0);

		tempitem0=new FieldItem();
		tempitem0.setItemtype("A");
		tempitem0.setItemid("ChgUser");
		tempitem0.setItemlength(50);
		table.addField(tempitem0);
		hm.put("chguser",tempitem0);

		tempitem0=new FieldItem();
		tempitem0.setItemtype("N");
		tempitem0.setItemid("AppState");
		tempitem0.setItemlength(12);
		tempitem0.setDecimalwidth(0);
		table.addField(tempitem0);
		hm.put("appstate",tempitem0);

tempitem0=new FieldItem();
tempitem0.setItemtype("N");
tempitem0.setItemid("key_no");
		tempitem0.setItemlength(12);
		tempitem0.setDecimalwidth(0);
table.addField(tempitem0);
hm.put("key_no",tempitem0);

tempitem0=new FieldItem();
		tempitem0.setItemtype("N");
		tempitem0.setItemid("MessageID");
		tempitem0.setItemlength(12);
		tempitem0.setDecimalwidth(0);
		table.addField(tempitem0);
		hm.put("messageid",tempitem0);

		tempitem0=new FieldItem();
		tempitem0.setItemtype("A");
		tempitem0.setItemid("seqnum");
		tempitem0.setItemlength(40);
		table.addField(tempitem0);
		hm.put("seqnum",tempitem0);


		/**对人员信息群增加三个固定字段B0110_1,E0122_1,A0101_1,单位、部门及姓名*/
		if(this._static!=10&&this._static!=11)
		{
			temp=new Field("b0110_1",ResourceFactory.getProperty("column.sys.org"));
			temp.setDatatype(DataType.STRING);
			temp.setLength(30);
			temp.setVisible(true);
			temp.setNullable(true);
			temp.setCodesetid("UN");
			temp.setKeyable(false);
			table.addField(temp);
			hm.put("b0110_1",temp);

			temp=new Field("e0122_1",ResourceFactory.getProperty("column.sys.dept"));
			temp.setDatatype(DataType.STRING);
temp.setLength(30);
temp.setVisible(true);
			temp.setNullable(true);
temp.setCodesetid("UM");
			temp.setKeyable(false);
			table.addField(temp);
			hm.put("e0122_1",temp);

			temp=new Field("e01a1_1",ResourceFactory.getProperty("column.sys.pos"));
			temp.setDatatype(DataType.STRING);
			temp.setLength(30);
			temp.setVisible(true);
			temp.setNullable(true);
			temp.setCodesetid("@K");
			temp.setKeyable(false);
			table.addField(temp);
			hm.put("e01a1_1",temp);


			temp=new Field("a0101_1",ResourceFactory.getProperty("label.title.name"));
			temp.setDatatype(DataType.STRING);
			temp.setLength(30);
			temp.setVisible(true);
			temp.setNullable(true);
			temp.setKeyable(false);
			table.addField(temp);
			hm.put("a0101_1",temp);

			if(this.operationtype==0){
			temp=new Field("a0101_2",ResourceFactory.getProperty("label.title.name"));
			temp.setDatatype(DataType.STRING);
			temp.setLength(30);
			temp.setVisible(true);
			temp.setNullable(true);
			temp.setKeyable(false);
			table.addField(temp);
			hm.put("a0101_2",temp);
			}

		}
		else if(this._static==10||this._static==11)
		{
			if(this._static==10) {
                temp=new Field("codeitemdesc_1",ResourceFactory.getProperty("general.template.orgname"));
            }
			if(this._static==11) {
                temp=new Field("codeitemdesc_1",ResourceFactory.getProperty("e01a1.label"));
            }
			temp.setDatatype(DataType.STRING);
			temp.setLength(50);
			temp.setVisible(true);
			temp.setNullable(true);
			temp.setKeyable(false);
			table.addField(temp);
			hm.put("codeitemdesc_1",temp);
			if(this.operationtype==5){
        temp=new Field("codeitemdesc_2",ResourceFactory.getProperty("general.template.orgname"));
			temp.setDatatype(DataType.STRING);
			temp.setLength(50);
			temp.setVisible(true);
    temp.setNullable(true);
			temp.setKeyable(false);
			table.addField(temp);
			hm.put("codeitemdesc_2",temp);
			}
		}
		if(this.operationtype==8||this.operationtype==9)
		{
			temp=new Field("to_id","to_id");
			temp.setDatatype(DataType.STRING);
			temp.setLength(50);
			temp.setVisible(true);
			temp.setNullable(true);
			temp.setKeyable(false);
			table.addField(temp);
			hm.put("to_id",temp);
		}
		FieldItem item=null;
		String field_name=null;
		for(int i=0;i<list.size();i++)
		{
			item=(FieldItem)list.get(i);
			FieldItem tempitem=(FieldItem)item.cloneItem();
			if("PHOTO".equalsIgnoreCase(tempitem.getItemid().toUpperCase()))//照片必填需要传入change,但再次会造成照片生成字段不对，上传照片报找不到photo，此处特殊判断一下。
			{
				field_name=item.getItemid();
			}
			else if(item.isChangeAfter()){
				field_name=item.getItemid()+"_2";
				if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0){//多变相同的变化后子集需要拼接上id
					field_name=item.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_2";
				}
			}else if(item.isChangeBefore()){
				field_name=item.getItemid()+"_1";
				if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0){
					field_name=item.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_1";
					}
			}
			else {
                field_name=item.getItemid();
            }
			//维护多子集，多子集指标，变化前类型
			if(this.field_name_map!=null&&this.field_name_map.get(field_name.toLowerCase())!=null) {
                tempitem.setItemtype("M");
            }
			if(!hm.containsKey(field_name.toLowerCase()))
			{
				hm.put(field_name.toLowerCase(),item);
				tempitem.setItemid(field_name);
				//item.setItemid(name);
				table.addField(tempitem);
			}
		}//



	}

	 /**
	  * 删除类型不一样的表字段
	  * @param tableName
	  */
	 public boolean dropNoTypeField(String tableName,ResultSetMetaData mt)
	 {
		 boolean flag=false;
		 try
		 {
			 Table table=new Table(tableName);
		//	 ContentDAO dao=new ContentDAO(this.conn);
		//	 RowSet rowSet=dao.search("select * from "+tableName+" where 1=2");
		//	 ResultSetMetaData mt=rowSet.getMetaData();
			 int n=0;
			 for(int i=0;i<mt.getColumnCount();i++)
			 {
				 String columnName=mt.getColumnName(i+1);
				 int columnType=mt.getColumnType(i+1);
			//	 if(columnName.toLowerCase().indexOf("t_")==-1)
				 if(columnName.toLowerCase().indexOf("t_")==-1||(columnName.toLowerCase().length()>2&&!"t_".equalsIgnoreCase(columnName.toLowerCase().trim().substring(0,2))))
				 {
					 if("2".equals(columnName.substring(columnName.length()-1))&&columnName.indexOf("_2")!=-1)
					 {
						 String itemid=columnName.substring(0,columnName.length()-2);
						 FieldItem item=DataDictionary.getFieldItem(itemid);
						 if(item!=null)
						 {
							 switch(columnType)
							 {
									case java.sql.Types.INTEGER:
										if(!"N".equals(item.getItemtype()))
										{
											Field field=new Field(mt.getColumnName(i+1));
											table.addField(field);
											n++;
										}
										break;
									case java.sql.Types.TIMESTAMP:
										if(!"D".equals(item.getItemtype()))
										{
											Field field=new Field(mt.getColumnName(i+1));
											table.addField(field);
											n++;
										}
										break;
									case java.sql.Types.VARCHAR:
										if(!"A".equals(item.getItemtype()))
										{
											Field field=new Field(mt.getColumnName(i+1));
											table.addField(field);
											n++;
										}
										break;
									case java.sql.Types.DOUBLE:
										if(!"N".equals(item.getItemtype()))
										{
											Field field=new Field(mt.getColumnName(i+1));
											table.addField(field);
											n++;
										}
										break;
									case java.sql.Types.NUMERIC:
										if(!"N".equals(item.getItemtype()))
										{
											Field field=new Field(mt.getColumnName(i+1));
											table.addField(field);
											n++;
										}
										break;
									case java.sql.Types.LONGVARCHAR:
										if(!"M".equals(item.getItemtype()))
										{
											Field field=new Field(mt.getColumnName(i+1));
											table.addField(field);
											n++;
										}
								}



						 }
					 }

				 }

			 }
			 if(n>0)
			 {
				 DbWizard dbwizard=new DbWizard(this.conn);
          dbwizard.dropColumns(table);
          DBMetaModel dbmodel=new DBMetaModel(this.conn);
				 dbmodel.reloadTableModel(table.getName());
          flag=true;
      }
  }
  catch(Exception e)
	 {
			 e.printStackTrace();
		 }
		 return flag;

	 }




	/***
	 * 修改临时表的结构
	 * @param table
	 * @param flag =1升级审批表结构
	 * @return
	 */
	private boolean updateTempTemplateStruct(Table table,int flag,ArrayList fieldList)
	{
		boolean bflag=true;
		try
		{

			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList list=(ArrayList)fieldList.clone();//getAllFieldItem();
			RowSet rowSet=dao.search("select * from "+table.getName()+" where 1=2");
			ResultSetMetaData mt=rowSet.getMetaData();

		//	DBMetaModel dbmodel=new DBMetaModel(this.conn);
		//	dbmodel.reloadTableModel(table.getName());
		//	boolean isDrop=dropNoTypeField(table.getName(),mt);
		//	dbmodel.reloadTableModel(table.getName());

			HashMap att_map=new HashMap();


			DbWizard dbwizard=new DbWizard(this.conn);
			StringBuffer strs=new StringBuffer();
			String field_name=null;
			boolean baddkey=false;

			/*
			if(isDrop)
			{
				rowSet=dao.search("select * from "+table.getName()+" where 1=2");
				mt=rowSet.getMetaData();
			}*/

			for(int i=0;i<mt.getColumnCount();i++)
			{
				att_map.put(mt.getColumnName(i+1).toLowerCase(),"1");
			}

			strs.append(",");
			StringBuffer str_items=new StringBuffer("");
			/**字典表中的指标*/
			for(int i=0;i<list.size();i++)
			{
				FieldItem item=(FieldItem)list.get(i);
				FieldItem tempitem=(FieldItem)item.cloneItem();
				boolean g=false;
				if("PHOTO".equalsIgnoreCase(tempitem.getItemid().toUpperCase()))//照片必填需要传入change,但再次会造成照片生成字段不对，上传照片报找不到photo，此处特殊判断一下。
				{
					field_name=item.getItemid();
				}
				else if(item.isChangeAfter())
				{
					g=true;
					field_name=item.getItemid()+"_2";
					if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0){//多个相同的变化后子集需要拼接id区分
						field_name=item.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_2";
						}
				}
				else if(item.isChangeBefore())
				{
					g=true;
					field_name=item.getItemid()+"_1";
					if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0){
						field_name=item.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_1";
						}
				}
				else {
                    field_name=item.getItemid();
                }

				if("A".equalsIgnoreCase(item.getItemtype())&&g) {
                    str_items.append(","+field_name.toLowerCase());
                }

				strs.append(field_name);
				strs.append(",");
			//	if(!vo.hasAttribute(field_name.toLowerCase()))
				if(att_map.get(field_name.toLowerCase())==null)//如果表中没有这个字段，加上
				{
					tempitem.setItemid(field_name);
					table.addField(tempitem);
				}
			}

			/** 临时变量字段 */
			ArrayList fieldlist=getMidVariableList();
			FieldItem _item=null;
			for(int i=0;i<fieldlist.size();i++)
			{
				_item=(FieldItem)fieldlist.get(i);
				if(att_map.get(_item.getItemid().toLowerCase())==null)//如果表中没有这个字段，加上
				{
					table.addField(_item);
				}
			}


			/**实例字段*/
			if(flag==1)
			{
				//if(!vo.hasAttribute("ins_id"))
				if(att_map.get("ins_id")==null)
				{
					FieldItem tempitem=new FieldItem();
					tempitem.setItemtype("N");
					tempitem.setItemlength(12);
					tempitem.setDecimalwidth(0);
					tempitem.setItemid("ins_id");
					table.addField(tempitem);
					baddkey=true;
				}
				//if(!vo.hasAttribute("task_id"))
				if(att_map.get("task_id")==null)
				{
					FieldItem tempitem=new FieldItem();
					tempitem.setItemtype("N");
					tempitem.setItemlength(10);
					tempitem.setDecimalwidth(0);
					tempitem.setItemid("task_id");
					table.addField(tempitem);
				}
	            /**入库标志 wangrd 2013-11-26*/
				if(att_map.get("archive_flag")==null)
				{
				    FieldItem tempitem=new FieldItem();
				    tempitem.setItemtype("N");
				    tempitem.setItemlength(10);
				    tempitem.setDecimalwidth(0);
				    tempitem.setItemid("archive_flag");
				    table.addField(tempitem);
				}

			}
			//if(!vo.hasAttribute("submitflag"))
			if(att_map.get("submitflag")==null)
			{
				FieldItem tempitem=new FieldItem();
				tempitem.setItemtype("N");
            tempitem.setItemlength(2);
			tempitem.setDecimalwidth(0);
        tempitem.setItemid("submitflag");
				table.addField(tempitem);
			}

			//if(!vo.hasAttribute("a0000"))
			if(att_map.get("a0000")==null)
			{
				FieldItem tempitem=new FieldItem();
				tempitem.setItemtype("N");
				tempitem.setItemlength(12);
				tempitem.setDecimalwidth(0);
				tempitem.setItemid("a0000");
				table.addField(tempitem);
			}

			if((this.infor_type==2||this.infor_type==3)&&(this.operationtype==8||this.operationtype==9))
			{
				if(att_map.get("to_id")==null)
				{
					FieldItem tempitem=new FieldItem();
					tempitem.setItemtype("A");
					tempitem.setItemlength(50);
					tempitem.setDecimalwidth(0);
					tempitem.setItemid("to_id");
					table.addField(tempitem);
				}
			}

			if(this._static!=10&&this._static!=11)
			{
				if(att_map.get("e01a1_1")==null)
				{
					FieldItem tempitem=new FieldItem();
					tempitem.setItemtype("A");
					tempitem.setItemlength(30);
					tempitem.setDecimalwidth(0);
					tempitem.setItemid("e01a1_1");
					tempitem.setCodesetid("@K");
					table.addField(tempitem);
				}
			}
			if(att_map.get("appprocess")==null)
			{
				FieldItem tempitem=new FieldItem();
				tempitem.setItemtype("M");
				tempitem.setItemid("AppProcess");
				table.addField(tempitem);
			}
			if(att_map.get("signature")==null)
			{
				FieldItem tempitem=new FieldItem();
				tempitem.setItemtype("M");
				tempitem.setItemid("signature");
				table.addField(tempitem);
			}
			if(att_map.get("lasttime")==null)
			{
				FieldItem tempitem=new FieldItem();
				tempitem.setItemtype("D");
				tempitem.setItemid("LastTime");
				table.addField(tempitem);
			}
			//if(!vo.hasAttribute("appuser"))
			if(att_map.get("appuser")==null)
			{
				FieldItem tempitem=new FieldItem();
				tempitem.setItemtype("A");
				tempitem.setItemid("AppUser");
				tempitem.setItemlength(200);
				table.addField(tempitem);
			}
			//if(!vo.hasAttribute("chgpk32"))
			if(att_map.get("chgpk32")==null)
			{
				FieldItem tempitem=new FieldItem();
				tempitem.setItemtype("A");
				tempitem.setItemid("ChgPK32");
				tempitem.setItemlength(50);
				table.addField(tempitem);
}
//if(!vo.hasAttribute("chguser"))
			if(att_map.get("chguser")==null)
			{
				FieldItem tempitem=new FieldItem();
				tempitem.setItemtype("A");
				tempitem.setItemid("ChgUser");
				tempitem.setItemlength(50);
				table.addField(tempitem);
			}
		//	if(!vo.hasAttribute("appstate"))
			if(att_map.get("appstate")==null)
			{
				FieldItem tempitem=new FieldItem();
				tempitem.setItemtype("N");
				tempitem.setItemid("AppState");
				tempitem.setItemlength(12);
				tempitem.setDecimalwidth(0);
				table.addField(tempitem);
			}
			//if(!vo.hasAttribute("key_no"))
			if(att_map.get("key_no")==null)
			{
				FieldItem tempitem=new FieldItem();
				tempitem.setItemtype("N");
				tempitem.setItemid("key_no");
				tempitem.setItemlength(12);
				tempitem.setDecimalwidth(0);
				table.addField(tempitem);
			}
			//if(!vo.hasAttribute("messageid"))
			if(att_map.get("messageid")==null)
			{
				FieldItem tempitem=new FieldItem();
				tempitem.setItemtype("N");
				tempitem.setItemid("MessageID");
				tempitem.setItemlength(12);
				tempitem.setDecimalwidth(0);
				table.addField(tempitem);
			}


			if(att_map.get("seqnum")==null)
			{
				FieldItem tempitem=new FieldItem();
				tempitem.setItemtype("A");
				tempitem.setItemid("seqnum");
				tempitem.setItemlength(40);
				table.addField(tempitem);
			}
			//   end






			if(table.size()>0) {
                dbwizard.addColumns(table);
            }
			//

			/*
			table.clear();
			String temp=strs.toString().toLowerCase();
			for(int i=0;i<mt.getColumnCount();i++)
			{
				field_name=","+mt.getColumnName(i+1)+",";

				if(this._static==10)
				{
					if(",appprocess,seqnum,lasttime,appuser,chgpk32,chguser,appstate,key_no,messageid,basepre,ins_id,state,task_id,submitflag,b0110,a0000,".indexOf(field_name.toLowerCase())!=-1)
						continue;
				}
				else if(this._static==11)
				{
					if(",appprocess,seqnum,lasttime,appuser,chgpk32,chguser,appstate,key_no,messageid,basepre,ins_id,state,task_id,submitflag,e01a1,a0000,".indexOf(field_name.toLowerCase())!=-1)
						continue;
				}
				else
				{
					if(",appprocess,seqnum,lasttime,appuser,chgpk32,chguser,appstate,key_no,messageid,basepre,ins_id,state,task_id,submitflag,a0100,b0110_1,e01a1_1,e0122_1,a0101_1,a0000,".indexOf(field_name.toLowerCase())!=-1)
						continue;
				}
				if(temp.indexOf(field_name.toLowerCase())==-1)
				{
					Field field=new Field(mt.getColumnName(i+1));
					table.addField(field);
				}
			}
			*/
		//	if(table.size()>0)
	//			dbwizard.dropColumns(table);

			//判断字符字段的长度是否跟数据字典一致
			table.clear();

			/**
			str_items.append(",");
			for(int i=0;i<mt.getColumnCount();i++)
			{
				String fieldname=mt.getColumnName(i+1);
				if(str_items.indexOf(","+fieldname.toLowerCase()+",")!=-1)
				{
					String temp_id=fieldname.substring(0,fieldname.length()-2);
					FieldItem item=DataDictionary.getFieldItem(temp_id);

					//对人员信息群增加三个固定字段B0110_1,E0122_1,A0101_1,单位、部门及姓名
					if(fieldname.equalsIgnoreCase("B0110_1")||fieldname.equalsIgnoreCase("E01A1_1")||fieldname.equalsIgnoreCase("E0122_1")||fieldname.equalsIgnoreCase("A0101_1"))
						continue;
					if(item.getItemlength()>mt.getColumnDisplaySize(i+1))
					{
						FieldItem tempitem=(FieldItem)item.cloneItem();
						tempitem.setItemid(fieldname);
						table.addField(tempitem);
					}
				}
			}
			if(table.size()>0)
			{
				dbwizard.alterColumns(table);
			}

			//判断 字段类型是否改变
			table.clear();
			for(int i=0;i<mt.getColumnCount();i++)
			{
				String fieldname=mt.getColumnName(i+1);
				String temp_id=fieldname.substring(0,fieldname.length()-2);
				FieldItem item=DataDictionary.getFieldItem(temp_id);
				//对人员信息群增加三个固定字段B0110_1,E0122_1,A0101_1,单位、部门及姓名
				if(fieldname.equalsIgnoreCase("B0110_1")||fieldname.equalsIgnoreCase("E01A1_1")||fieldname.equalsIgnoreCase("E0122_1")||fieldname.equalsIgnoreCase("A0101_1"))
						continue;
				if(item!=null)
				{
					if(item.getItemtype().equalsIgnoreCase("N"))
{
    int scale=mt.getScale(i+1);
						if(item.getDecimalwidth()==0)
						{
							if(!((mt.getColumnType(i+1)==Types.NUMERIC&&scale==0)||mt.getColumnType(i+1)==Types.BIGINT||mt.getColumnType(i+1)==Types.INTEGER||mt.getColumnType(i+1)==Types.TINYINT||mt.getColumnType(i+1)==Types.SMALLINT))
							{
								FieldItem tempitem=(FieldItem)item.cloneItem();
								tempitem.setItemid(fieldname);
                        table.addField(tempitem);
							}
						}
						else
						{
							if(!((mt.getColumnType(i+1)==Types.DECIMAL||mt.getColumnType(i+1)==Types.DOUBLE||mt.getColumnType(i+1)==Types.FLOAT||mt.getColumnType(i+1)==Types.NUMERIC||mt.getColumnType(i+1)==Types.REAL)&&scale>0))
							{
								FieldItem tempitem=(FieldItem)item.cloneItem();
								tempitem.setItemid(fieldname);
								table.addField(tempitem);
							}

						}
					}
					if(item.getItemtype().equalsIgnoreCase("A"))
					{
						if(!(mt.getColumnType(i+1)==Types.CHAR||mt.getColumnType(i+1)==Types.LONGVARCHAR||mt.getColumnType(i+1)==Types.VARCHAR))
						{
							FieldItem tempitem=(FieldItem)item.cloneItem();
                        				tempitem.setItemid(fieldname);
							table.addField(tempitem);
						}
					}
				}
			}
			if(table.size()>0)
			{
				dbwizard.dropColumns(table);
				dbwizard.addColumns(table);
			//	dbwizard.alterColumns(table);
			}
			*/


			/**增加主键*/
			if(baddkey)
			{
				table.clear();
				dbwizard.dropPrimaryKey(table.getName());
				strs.setLength(0);
				strs.append("delete from ");
				strs.append(table.getName());
				dbwizard.execute(strs.toString());

				Field field=new Field("ins_id","ins_id");
				field.setNullable(false);
				field.setKeyable(true);
				field.setDatatype(DataType.INT);
				table.addField(field);
				dbwizard.alterColumns(table);

				if(this._static==10) //单位管理
				{
					field=new Field("B0110",ResourceFactory.getProperty("column.sys.org"));
					field.setDatatype(DataType.STRING);
					field.setLength(30);
					field.setVisible(false);
					field.setNullable(false);
					field.setCodesetid("UN");
					field.setKeyable(true);
					table.addField(field);
				}
				else if(this._static==11) //职位管理
				{
					field=new Field("E01A1",ResourceFactory.getProperty("column.sys.pos"));
					field.setDatatype(DataType.STRING);
					field.setLength(30);
					field.setVisible(false);
					field.setNullable(false);
					field.setCodesetid("@K");
					field.setKeyable(true);
					table.addField(field);
				}
				else
				{
					field=new Field("A0100",ResourceFactory.getProperty("a0100.label"));
					field.setKeyable(true);
					field.setNullable(false);
					table.addField(field);

					field=new Field("BasePre",ResourceFactory.getProperty("label.dbase"));
					field.setKeyable(true);
					field.setNullable(false);
					table.addField(field);
				}

				dbwizard.addPrimaryKey(table);
			}
			PubFunc.closeDbObj(rowSet);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
		return bflag;
	}
	/**
	 * 创建标题对象
	 * @param titlelist
	 * @param div
	 */
	private void createTitleElement(ArrayList titlelist,Element div)
{
for(int i=0;i<titlelist.size();i++)
		{
			TTitle title=(TTitle)titlelist.get(i);
			title.setCon(this.conn);
	title.setIns_id(this.ins_id);
			title.createTitleView(div,this.userview);
		}
	}

	/**得到模板中所有附件区域 list(0)附件区域的总个数，list(1)附件下标与附件类型的对应关系,list(2)附件位置(pageid和gridno)与下标的对应关系*/
	public ArrayList getAttachmentInfor(){
		ArrayList list = new ArrayList();
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			int tabid=this.getTabid();
			StringBuffer attachmenttype = new StringBuffer();
			int count = 0;
			HashMap map = new HashMap();
			StringBuffer sb = new StringBuffer("");
			sb.append("select * from template_set where tabid="+tabid+" and flag='F'");
			RowSet rs = dao.search(sb.toString());
			while(rs.next()){
				String type = "0";//暂时写个固定的 待改
				String xml = Sql_switcher.readMemo(rs, "sub_domain");
				type = this.getAttachmentType(xml);
				attachmenttype.append(count+"`"+type+",");
				int pageid = rs.getInt("pageid");
				int gridno = rs.getInt("gridno");
				map.put(pageid+"`"+gridno, String.valueOf(count));
				count++;
			}
			if(attachmenttype.length()>0){
				attachmenttype.setLength(attachmenttype.length()-1);
			}
			list.add(String.valueOf(count));
			list.add(attachmenttype.toString());
			list.add(map);
			PubFunc.closeDbObj(rs);
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	/**得到附件类型*/
	public String getAttachmentType(String xml){
		String type = "0";
		if(xml==null || "".equals(xml)){
			return type;
		}
		try{
			Document doc=PubFunc.generateDom(xml);;
			Element element=null;
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String xpath="/sub_para/para";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			element =(Element) findPath.selectSingleNode(doc);
			if(element!=null){
				if(element.getAttribute("attachmentType")!=null){
					type = element.getAttributeValue("attachmentType");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return type;
	}
	/**
	 * 创建单元格输出对象
	 * @param celllist
	 * @param div
	 */
	private void createCellElement(ArrayList celllist,Element div)
	{
		ArrayList templist = new ArrayList();
		templist = this.getAttachmentInfor();
		String attachment_count = (String)templist.get(0);//模板中附件区域的总个数
		String attachmentareatotype = (String)templist.get(1);
		HashMap innermap = (HashMap)templist.get(2);
		this.setAttachmentcount(attachment_count);
		this.setAttachmentAreaToType(attachmentareatotype);
		this.setBHaveCalcItem(false);
		for(int i=0;i<celllist.size();i++)
		{
			TemplateSetBo cell=(TemplateSetBo)celllist.get(i);
			cell.setOperationtype(this.operationtype);
			cell.setSignnumber(i);

			String attachmentindex = "0";///附件区域的下标
			String fieldflag = cell.getFlag();
			if(fieldflag!=null && "F".equalsIgnoreCase(fieldflag)){//如果是附件，就查出附件区域的下标
				//找到pageid和gridid
				int pageid = cell.getPagebo().getPageid();
				int gridno = cell.getGridno();
				attachmentindex = (String)innermap.get(pageid+"`"+gridno);
			}
			//解决用户管理范围为空“”时，需要判断用户的管理范围，因此需要为每个cell设置userView
			cell.setUserview(this.userview);
			cell.createCellView(div,this.userview,attachmentindex);
	        if("C".equalsIgnoreCase(fieldflag)){//是否有计算项 前台需要刷新 wangrd 2014-01-03
	            this.setBHaveCalcItem(true);
	        }
		}
	}
	/**
	 * 创建模板的HTML表单(输入/显示)
	 * @param pageno 页号
	 * @param page_num 第几页数据  每页30条记录
	 * @return
	 */
	public String createTemplatePageView(int pageno,int page_num)
	{
		/**输出的HMTL内容*/

		StringBuffer strhtml=new StringBuffer();
		try
		{
			int paperOrientation = this.getPageParam(this.tabid,pageno);
			/**纸张背景*/
			Element div=new Element("div");
			div.setAttribute("class","pagebgk");
			int direct=this.table_vo.getInt("paperori");
			if (paperOrientation!=0){
				direct= paperOrientation;
			}
			int width=0;
			int height=0;
			if(direct==1)
			{
				width=this.table_vo.getInt("paperw");
				height=this.table_vo.getInt("paperh");
			}
			else
			{
				width=this.table_vo.getInt("paperh");
				height=this.table_vo.getInt("paperw");
			}
			int wpx=Math.round((float)(width /25.4*PixelInInch));
			int hpx=Math.round((float)(height/25.4*PixelInInch));
			StringBuffer style=new StringBuffer();
			if(this.isBEmploy()) {
                style.append("left:4px;top:5px;width:"); //定位4
            } else
			{
				if(this.infor_type==1) {
                    style.append("left:150px;top:5px;width:"); //定位4
                } else {
                    style.append("left:200px;top:5px;width:"); //定位4
                }
			}
			style.append(wpx);
style.append("px");
			style.append(";height:");
			style.append(hpx);
			style.append("px");
			style.append(";position:absolute");
			div.setAttribute("style",style.toString());

			StringBuffer divlist=new StringBuffer();
			divlist.append("<div id=\"emplist\" style=\"");

			String value="140";
			if(this.infor_type!=1) {
                value="180";
            }
			divlist.append("left:4px;top:5px;width:"+value+"px;"); //定位4
			divlist.append("height:100%");
			//divlist.append(550);
		//	divlist.append("hpx");
			//divlist.append("px");
			divlist.append(";");

			divlist.append("overflow: auto;");

			divlist.append("top: expression(this.offsetParent.scrollTop+5);");
			divlist.append("position:relative\">");
			divlist.append("</div>");

			String task_id = "0";
			if(this.getTasklist().size()>0) {
                task_id = ""+this.getTasklist().get(0);
            }

			/**标题*/
			TemplatePageBo pagebo=new TemplatePageBo(this.conn,this.tabid,pageno,task_id);
			ArrayList titlelist=pagebo.getAllTitle();
			if(titlelist.size()>0) {
                createTitleElement(titlelist,div);
            }

			/**输出单元格*/
			ArrayList celllist=pagebo.getAllCell();

			if(celllist.size()>0) {
                createCellElement(celllist,div);
            }

			/**输出超文标志*/
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setExpandEmptyElements(true);//must

			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String htmlview=outputter.outputString(div);

        //还原附件iframe
        htmlview=htmlview.replaceAll("&gt;&lt;iframe","><iframe");
        htmlview=htmlview.replaceAll("&gt;&lt;/iframe&gt;","></iframe>");
			htmlview=htmlview.replaceAll("&lt;div id='attachmentid","<div id='attachmentid");
    htmlview=htmlview.replaceAll("</iframe>&lt;/div&gt;","</iframe></div>");
			//标题照片
			htmlview=htmlview.replaceAll("tp&lt;","<");
			htmlview=htmlview.replaceAll("/&gt;tp","/>");

			htmlview=htmlview.replaceAll("empty_context","&nbsp;");
			htmlview=htmlview.replaceAll("&amp;","&nbsp;");
			htmlview=htmlview.replaceAll("`","<br />");
			//strhtml.append("<div style=\"border-collapse: collapse;height: expression(document.body.clientHeight)\">");
			strhtml.append(htmlview);
			strhtml.append("\n");
			//strhtml.append("</div>");
        /**收集数据，采用通过数据集的方式*/
        if(this.isBEmploy())//员工自助-业务申请
        {
            outputJs(strhtml, pagebo);
        }
else//
{
        if(this.ins_id==0) {
                    outputJs(strhtml, pagebo,0,page_num);
                } else {
                    outputJs(strhtml, pagebo,1,page_num);
                }
			}
			strhtml.insert(0, divlist.toString());

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return strhtml.toString();
	}
	/**
	 * 得到某一页的横纵向设置
	 * @param tabid
	 * @param pageid
	 * @return
	 */
	public int getPageParam(int tabid ,int pageid)  {
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rset = null;
		int paperOrientation = 0;
		try {
			sql.append("select * from Template_Page where tabid=");
			sql.append(String.valueOf(tabid)+" and pageid ="+String.valueOf(pageid));
			rset = dao.search(sql.toString());
			rset.next();
			DbWizard dbw = new DbWizard(this.conn);
boolean hasPaperOriFld = false;
			if(dbw.isExistField("template_Page", "paperOrientation", false)){
    hasPaperOriFld = true;
			}
            if(hasPaperOriFld){
            	paperOrientation = rset.getInt("paperOrientation");
            }
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			PubFunc.closeResource(rset);
		}
		return paperOrientation;
	}
	/**
	 * 将list中fielditem对象转换成field对象
	 * @param fieldlist
	 * @return
	 */
	private ArrayList covertItemToField(ArrayList fieldlist)
	{
		 ArrayList _list=new ArrayList();
		 Field item = null;
	     String field_name=null;
		 for(int i=0;i<fieldlist.size();i++)
		 {
	         Object obj = fieldlist.get(i);
	         if(obj instanceof FieldItem)
	         {
	             FieldItem fielditem = (FieldItem)obj;
	             item = fielditem.cloneField();
	         } else
	         {
	             item = (Field)obj;
	         }
				if(item.isChangeAfter()) {
                    field_name=item.getName()+"_2";
} else if(item.isChangeBefore()) {
                    field_name=item.getName()+"_1";
                } else {
                    field_name=item.getName();
}
	item.setName(field_name);
	        	_list.add(item);
		 }
		 return _list;
	}



	/**
	 * 分析此实例，是否为当前用户发起的申请
	 * @param ins_id
	 */
	public String isStartNode(String ins_id,String task_id,String tabid,int sp_mode)
	{
		String startflag="0";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("select actorid from t_wf_instance where ins_id=?");
			ArrayList list=new ArrayList();
			list.add(ins_id);
			RowSet rset=dao.search(buf.toString(),list);
			if(rset.next())
			{
				/**
				 * 申请人编码
				 * 自助平台用户:应用库前缀+人员编码
				 * 业务平台用户：operuser中的账号
				 */
				String applyobj=rset.getString("actorid");//   ins_vo.getString("actorid");
				String a0100=this.userview.getDbname()+this.userview.getA0100();
				String usrname=this.userview.getUserId();
				if(applyobj!=null&&(applyobj.equalsIgnoreCase(a0100)||applyobj.equalsIgnoreCase(usrname))) {
                    startflag="1";
                }
				if ("1".equals(this.def_flow_self)){
					//startflag="0";//自定义审批流程 在流程中已经不是发起人
					if (this.isDef_flow_self(Integer.parseInt(task_id))){
						String sql="select 1 from t_wf_task t,t_wf_node_manual m where t.ins_id=m.ins_id and t.node_id = m.id and t.task_id = "+task_id+"";
	                    rset=dao.search(sql);
	                    if(rset.next()) {
                            startflag="0";
                        } else {
                            startflag="1";
                        }
                    }
				}
				if("1".equals(startflag)&&task_id!=null&&!"0".equals(task_id)&&task_id.trim().length()>0)
				{
					if(sp_mode==0){//自动
						rset=dao.search("select nodetype from t_wf_node where tabid="+tabid+" and node_id=(select node_id from t_wf_task where task_id="+task_id+")");
						if(rset.next())
						{

							if(!"1".equals(rset.getString("nodetype"))&&!"9".equals(rset.getString("nodetype"))) {
                                    startflag="0";
                            }
						}
					}
					if(sp_mode==1&&(!"1".equals(this.def_flow_self)||!this.isDef_flow_self(Integer.parseInt(task_id)))){//手工非自定义或者是自定义没设置审批流程
            //判断单子是否是驳回的
            RecordVo vo=new RecordVo("t_wf_task");
vo.setString("task_id", task_id);
vo=dao.findByPrimaryKey(vo);
						if ("07".equals(vo.getString("state"))){//如果是驳回的
							//是否是驳回到起草
							int pri_task_id = 0;
							rset=dao.search("select pri_task_id from t_wf_task where state='07' and task_id="+task_id);
							if(rset.next())
							{
								if(rset.getString(1)!=null) {
                                    pri_task_id=rset.getInt(1);
                                }
							}
							if(pri_task_id!=0) {
                                startflag="0";
                            }
						}else{//报批的
							//当前任务节点是否是起草
							rset=dao.search("select nodetype from t_wf_node where tabid="+this.tabid+" and node_id=(select node_id from t_wf_task where task_id="+task_id+")");
                                    if(rset.next())
                                    {
                                        if(!"1".equals(rset.getString("nodetype"))&&!"9".equals(rset.getString("nodetype"))) {
                                    startflag="0";
                                }
							}
						}
					}
				}
			}
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return startflag;
	}

	/**判断是否是发起人
	 * @param task_id
	 * @return
	 */
	public String isStartNode(String task_id)
	{
		if (task_id.contains(",")){
	        String[] lists=StringUtils.split(task_id,",");
	        for(int i=0;i<lists.length;i++)
	        {
	        	if (!"".equals(lists[i])){
	        		task_id= lists[i];
	        		break;
	        	}
	        }
		}
		if ("".equals(task_id) || "0".equals(task_id)){
			return "1";
		}
	    int sp_mode=this.getSp_mode();
		String startflag="0";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("select actorid from t_wf_instance where ins_id=(select ins_id from t_wf_task where task_id=? )");
			ArrayList list=new ArrayList();
			list.add(new Integer(task_id));
			RowSet rset=dao.search(buf.toString(),list);
			if(rset.next())
			{
				/**
				 * 申请人编码
				 * 自助平台用户:应用库前缀+人员编码
				 * 业务平台用户：operuser中的账号
				 */
				String applyobj=rset.getString("actorid");//   ins_vo.getString("actorid");
				String a0100=this.userview.getDbname()+this.userview.getA0100();
				String usrname=this.userview.getUserId();
				if(applyobj!=null&&(applyobj.equalsIgnoreCase(a0100)||applyobj.equalsIgnoreCase(usrname))) {
                    startflag="1";
                }
				if ("1".equals(this.getDef_flow_self())){
                    startflag="0";//自定义审批流程 在流程中已经不是发起人
                }
				if("1".equals(startflag)&&task_id!=null&&!"0".equals(task_id)&&task_id.trim().length()>0&&sp_mode==0)
				{
					rset=dao.search("select nodetype from t_wf_node where tabid="+this.tabid+" and node_id=(select node_id from t_wf_task where task_id="+task_id+")");
					if(rset.next())
					{

						if(!"1".equals(rset.getString("nodetype"))&&!"9".equals(rset.getString("nodetype"))) {
                                    startflag="0";
                                }
                            }
                        }
}
}
                catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return startflag;
	}

	/**
	 * 生成数据集js代码
	 * @param strhtml
	 * @param pagebo
	 * @param flag =0和用户有关的表，=1与用户名无关的表
	 * @throws SQLException
	 */
	private void outputJs(StringBuffer strhtml, TemplatePageBo pagebo) throws GeneralException {
		try
		{
			StringBuffer sql=new StringBuffer();
			sql.append("select * from ");
        sql.append("templet_");
			sql.append(this.tabid);
			sql.append(" where a0100='");
			sql.append(this.userview.getA0100());
			sql.append("' and basepre='");
			sql.append(this.userview.getDbname());
			sql.append("'");

			String dataset="templet_"+tabid;
			AutoFormBo formbo=new AutoFormBo(dataset/*"templet_"+tabid*/,sql.toString(),this.operationtype);
			formbo.setUserview(this.userview);
			formbo.setTablebo(this);

			HashMap f_cellhm=new HashMap();
			ArrayList fieldlist=pagebo.getAllFieldItem(f_cellhm);


			Field temp=new Field("a0100",ResourceFactory.getProperty("a0100.label"));
			temp.setDatatype(DataType.STRING);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setKeyable(true);
			temp.setSortable(true);

			temp.setLength(10);
			fieldlist.add(temp);
			if(f_cellhm.get("a0101_1")==null)
			{
				temp=new Field("a0101_1",ResourceFactory.getProperty("a0100.label"));
				temp.setDatatype(DataType.STRING);
				temp.setVisible(false);
				temp.setNullable(false);
				temp.setLength(30);
				fieldlist.add(temp);
			}

			temp=new Field("basepre",ResourceFactory.getProperty("label.dbase"));
			temp.setDatatype(DataType.STRING);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setSortable(true);
			temp.setKeyable(true);
			temp.setLength(3);
			fieldlist.add(temp);

			temp=new Field("submitflag","submitflag");
			temp.setDatatype(DataType.INT);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setSortable(false);
			temp.setValue("0");
			fieldlist.add(temp);

			/**状态标志=0,=1来源消息(其它模板发过来的通知)*/
			temp=new Field("state","state");
			temp.setDatatype(DataType.INT);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setSortable(false);
			temp.setValue("0");
			fieldlist.add(temp);
			this.currentFieldlist=covertItemToField((ArrayList)fieldlist.clone());
			String strsql=sql.toString();

			strsql=strsql.replaceAll("templet_"+tabid,"g_templet_"+tabid);
			dataset="g_templet_"+tabid;

			ContentDAO dao=new ContentDAO(this.conn);
			this.hmuster_sql=strsql;
			RowSet rset=dao.search(strsql);
			HashMap codehm=new HashMap();
			formbo.setSum_domain_map(pagebo.getSub_domain_map());
			strhtml.append(formbo.createDataSetRecord(fieldlist,rset,codehm,this.conn,f_cellhm,new HashMap()));
			this.signxml = formbo.getSignxml().toString();
			strhtml.append("\n");
/**输出代码*/
//strhtml.append(DataTable.outCodeJs(codehm,1));
			strhtml.append("\n");
			int maxrows=1;
HashMap itemCodeMap = getitemCounts();
			String strjs=formbo.createDataSetJavaScript(fieldlist,1,1,maxrows,f_cellhm,itemCodeMap);
			strhtml.append(strjs);
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 生成数据集js代码
	 * @param strhtml
	 * @param pagebo
	 * @param flag =0和用户有关的表，=1与用户名无关的表
	 * @param page_num 第几页,每页30行
	 * @throws SQLException
	 */
	private void outputJs(StringBuffer strhtml, TemplatePageBo pagebo,int flag,int page_num) throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			DbWizard dbw=new DbWizard(this.conn);
			HashMap submitFlagMap=new HashMap();
			RowSet rset=null;
			StringBuffer sql=new StringBuffer();
			boolean isHasProcessTask = false;//是否是已办任务
			String tableName = "templet_" + this.tabid;//表名
			if("3".equals(business_model)&&this.ins_id!=0){
				isHasProcessTask = true;
			}
			sql.append("select templet_"+this.tabid+".* from ");
			//sql.append(this.userview.getUserName());
			sql.append(tableName);
			//sql.append(this.tabid);
			if(isHasProcessTask){
				sql.append(",t_wf_instance i");
			}
			//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
			ArrayList usernameList=PubFunc.SearchOperUserOrSelfUserName(this.userview);
			if("2".equals(business_model)&&this.ins_id!=0) //目标管理－面谈记录
			{
				sql.append(" where ins_id="+this.ins_id);
			}
			else if(isHasProcessTask) //已批任务处理的人员记录
			{
				String seqnum="";
				//rset=dao.search("select seqnum from templet_"+this.tabid+" where ins_id="+this.ins_id);
				// 2014-04-01 dengcan
				if(tasklist.size()>0&&this.ins_id!=0) {
                    rset=dao.search("select seqnum from t_wf_task_objlink where task_id="+(String)tasklist.get(0));
                } else {
                    rset=dao.search("select seqnum from t_wf_task_objlink where ins_id="+this.ins_id);
}
if(rset.next()) {
                    seqnum=rset.getString(1)!=null?rset.getString(1):"";
                }

				sql.append(" where ");
				if(seqnum.length()>0)
				{
					String _taskid=(String)tasklist.get(0);
					if(isHasProcessTask){
						sql.append(tableName + ".ins_id=i.ins_id and");
					}
					sql.append(" exists (select null from t_wf_task_objlink where "+tableName+".seqnum=t_wf_task_objlink.seqnum and "+tableName+".ins_id=t_wf_task_objlink.ins_id ");
	//				if(!"2".equals(this.task_sp_flag))
					sql.append(" and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userview.getUserName().toLowerCase()+"' ");
					if (this.userview.getA0100()!=null && this.userview.getA0100().trim().length()>0)//liuyz bug 32108 业务用户没有关联自助用户this.userView.getDbname().toLowerCase()+this.userView.getA0100()结果为空串，会查出不属于这个人的数据。
					{
						sql.append(" or lower(username)='"+this.userview.getDbname().toLowerCase()+this.userview.getA0100()+"' ");
						if(usernameList.size()>0){
							for(int i=0;i<usernameList.size();i++){
								sql.append(" or username='"+usernameList.get(i)+"' ");
							}
						}
					}
					sql.append(" )   )  ) ");
					sql.append(" and t_wf_task_objlink.tab_id="+this.tabid+" and t_wf_task_objlink.task_id="+_taskid+"    and ( "+Sql_switcher.isnull("t_wf_task_objlink.state","0")+"<>3 ");
					if(isHasProcessTask){
						sql.append(" or i.finished=6 )  )");
					}
				}
				else
				{
					if(isHasProcessTask){
						sql.append(tableName+".ins_id=i.ins_id and");
					}
					sql.append(" ins_id="+this.ins_id);
				}
			}
			else if(flag==1&&("61".equals(this.business_model)|| "62".equals(this.business_model)|| "71".equals(this.business_model)|| "72".equals(this.business_model))) //报备
			{
			//	String _taskid=(String)tasklist.get(0);
			//	sql.append(" where  exists (select null from t_wf_task_objlink where templet_"+this.tabid+".seqnum=t_wf_task_objlink.seqnum  and templet_"+this.tabid+".ins_id=t_wf_task_objlink.ins_id");
			//	sql.append("  and task_id=(select pri_task_id from t_wf_task where task_id="+_taskid+") and tab_id="+this.tabid+" and ( "+Sql_switcher.isnull("state","0")+"<>3 ) ) ");//state is null or  state=0

				StringBuffer strins=new StringBuffer();
				for(int i=0;i<tasklist.size();i++)//按任务号查询需要审批的对象20080418
				{
					if(i!=0) {
                        strins.append(",");
                    }
					strins.append((String)tasklist.get(i));
				}

				sql.append(" where  exists (select null from t_wf_task_objlink where templet_"+this.tabid+".seqnum=t_wf_task_objlink.seqnum  and templet_"+this.tabid+".ins_id=t_wf_task_objlink.ins_id");
				sql.append("  and task_id in ("+strins.toString()+") and tab_id="+this.tabid+" and ( "+Sql_switcher.isnull("state","0")+"<>3 ) ) ");//state is null or  state=0

				rset=dao.search("select seqnum,submitflag,task_id from t_wf_task_objlink where   task_id in ("+strins.toString()+") and tab_id="+this.tabid+" and  ("+Sql_switcher.isnull("state","0")+"<>3 )");//state is null or  state=0//结束的流程看不到数据
				while(rset.next()) {
                    submitFlagMap.put(rset.getString("seqnum"),(rset.getString("submitflag")!=null?rset.getString("submitflag"):"0")+","+rset.getString("task_id"));
                }


			}
			else if(flag==1) //审批表中的数据
			{
				StringBuffer strins=new StringBuffer();
				for(int i=0;i<tasklist.size();i++)//按任务号查询需要审批的对象20080418
				{
					if(i!=0) {
                        strins.append(",");
                    }
					strins.append((String)tasklist.get(i));
				}

				String seqnum="";
				if(this.ins_id!=0) //兼容以前版本产生的数据 2014-04-01 dengcan
				{
				//	rset=dao.search("select seqnum from templet_"+this.tabid+" where ins_id="+this.ins_id);
					rset=dao.search("select seqnum from t_wf_task_objlink where task_id in ("+strins.toString()+")" );
					if(rset.next()) {
                        seqnum=rset.getString(1)!=null?rset.getString(1):"";
                    }
				}


				if(this.bsp_flag&&seqnum.length()>0){
						sql.append(" where  exists (select null from t_wf_task_objlink where templet_"+this.tabid+".seqnum=t_wf_task_objlink.seqnum  and templet_"+this.tabid+".ins_id=t_wf_task_objlink.ins_id");
						if(!"2".equals(this.task_sp_flag))
						{
							sql.append(" and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userview.getUserName().toLowerCase()+"' ");
							if(this.userview.getA0100()!=null && this.userview.getA0100().trim().length()>0)//liuyz bug 32108 业务用户没有关联自助用户this.userView.getDbname().toLowerCase()+this.userView.getA0100()结果为空串，会查出不属于这个人的数据。
							{
								sql.append(" or lower(username)='"+this.userview.getDbname().toLowerCase()+this.userview.getA0100()+"' ");
								//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
								if(usernameList.size()>0){
									for(int i=0;i<usernameList.size();i++){
										sql.append(" or username='"+usernameList.get(i)+"' ");
									}
								}
							}
							sql.append(" )   )  ) ");
						}
						sql.append("  and task_id in ("+strins.toString()+") and tab_id="+this.tabid+" and ( "+Sql_switcher.isnull("state","0")+"<>3 ) ) ");//state is null or  state=0
				}else{
				//	sql.append(" where task_id in(");
				//	sql.append(strins.toString());
				//	sql.append(")");
					// 2014-04-01 dengcan
				    sql.append(" where ins_id=");
				    sql.append(this.ins_id);
				}

				if ((this.infor_type==1)&&this.objectId!=null && objectId.length()>0){
					String nbase =this.objectId.substring(0,3);
					String a0100 =this.objectId.substring(3);
					sql.append(" and upper(basepre)='"+nbase.toUpperCase()+"'");
					sql.append(" and a0100='"+a0100+"'");
				}


				rset=dao.search("select seqnum,submitflag,task_id from t_wf_task_objlink where   task_id in ("+strins.toString()+") and tab_id="+this.tabid+" and  ("+Sql_switcher.isnull("state","0")+"<>3 )");//state is null or  state=0//结束的流程看不到数据
				while(rset.next()) {
                    submitFlagMap.put(rset.getString("seqnum"),(rset.getString("submitflag")!=null?rset.getString("submitflag"):"0")+","+rset.getString("task_id"));
                }

			}



			if(filterStr!=null&&filterStr.length()>0){
					filterStr = filterStr.replaceAll(this.userview.getUserName()+"templet_"+tabid, "templet_"+tabid);
					if(sql.indexOf("where")!=-1) {
                        sql.append(" and "+filterStr);
                    } else {
                        sql.append(" where "+filterStr);
                    }
			}
			//只能看到权限范围内的人员。wangrd 2013-12-13
            if ((this.infor_type==1)&&(flag==1)&&("2".equals(this.task_sp_flag)) && (!this.userview.isSuper_admin())&&!"5".equals(this.business_model)&&!"3".equals(this.business_model))
            {//人员&&非发起流程&&2为已批从任务监控里进到卡片方式&&(不是超级用户&& 不是人事异动已批任务&&不是我的申请)
                String operOrg = this.userview.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板

                if (isJobtitleVoteModule(this.userview)){//职称评审投票系统不是使用ehr的用户
                	operOrg="UN`";
                }
                String un_1="";
                String um_1="";
                String um_2="";
                String un_2="";
                if (this.operationtype==0)
                {
                	if(dbw.isExistField("templet_"+tabid, "e0122_2", false)) {
                        um_2="e0122_2";
                    }
                	if(dbw.isExistField("templet_"+tabid, "b0110_2", false)) {
                        un_2="b0110_2";
                    }
                }
                else
                {
                	if(dbw.isExistField("templet_"+tabid, "e0122_1", false)) {
                        um_1="e0122_1";
                    }
                	if(dbw.isExistField("templet_"+tabid, "b0110_1", false)) {
                        un_1="b0110_1";
                    }
                }
                if(operOrg==null||!"UN`".equalsIgnoreCase(operOrg))
                {
                    if((operOrg!=null)&&(!"UN`".equalsIgnoreCase(operOrg)))
                    {
                        String strB0110Where="";
                        if(operOrg.length() >3)
                        {
                            String[] temp = operOrg.split("`");
                            for (int j = 0; j < temp.length; j++) {
                                 if (temp[j]!=null&&temp[j].length()>0) {
                                	 String _pre=temp[j].substring(0,2);
                                	 if (this.operationtype==0)
                                	 {
                                		 if("UN".equalsIgnoreCase(_pre))
                                		 {
                                			 if(un_2.length()>0) {
                                                 strB0110Where =strB0110Where+ " or  "+un_2+" like '" + temp[j].substring(2)+ "%'";
                                             } else if(um_2.length()>0) {
                                                 strB0110Where =strB0110Where+ " or  "+um_2+" like '" + temp[j].substring(2)+ "%'";
                                             }
                                		 }
                                		 else if("UM".equalsIgnoreCase(_pre)&&um_2.length()>0)
                                		 {
                                			 strB0110Where =strB0110Where+ " or  "+um_2+" like '" + temp[j].substring(2)+ "%'";
                                		 }
                                	 }
                                	 else
                                	 {
                                		 if("UN".equalsIgnoreCase(_pre))
                                		 {
                                			 if(un_1.length()>0) {
                                                 strB0110Where =strB0110Where+ " or  "+un_1+" like '" + temp[j].substring(2)+ "%'";
                                             } else if(um_1.length()>0) {
                                                 strB0110Where =strB0110Where+ " or  "+um_1+" like '" + temp[j].substring(2)+ "%'";
                                             }
                                		 }
                                		 else if("UM".equalsIgnoreCase(_pre)&&um_1.length()>0)
                                		 {
                                			 strB0110Where =strB0110Where+ " or  "+um_1+" like '" + temp[j].substring(2)+ "%'";
                                		 }

                                	 }
                                 }
                            }
                        }
                        else if(operOrg==null)
                        {
                        	strB0110Where=strB0110Where +" or 1=2 ";
                        }

                        strB0110Where=strB0110Where.substring(3);
                        strB0110Where = "("+strB0110Where+")";
                        if(sql.indexOf("where")!=-1) {
                            sql.append(" and "+strB0110Where);
                        } else {
                            sql.append(" where "+strB0110Where);
                        }
                  }
            }

            }
			if((this.infor_type==2||this.infor_type==3)&&(this.operationtype==8||this.operationtype==9))
{
String key="b0110";
if(this.infor_type==3) {
key="e01a1";
                }
				sql.append("  order by "+Sql_switcher.isnull("to_id","100000000")+",case when "+key+"=to_id then 100000000 else a0000 end asc ");
			}
			else {
               sql.append(" order by a0000");
            }



			String dataset="templet_"+tabid;
			//if(flag==0)
			//	dataset=this.userview.getUserName()+"templet_"+tabid;
			AutoFormBo formbo=new AutoFormBo(dataset/*"templet_"+tabid*/,sql.toString(),this.operationtype);
			formbo.setSp_flag(this.task_sp_flag);//bug 42345 7x包60锁任务监控进入表单能够编辑变化后指标

			String strsql=sql.toString();
			if(flag==0)
			{
				strsql=strsql.replaceAll("templet_"+tabid,this.userview.getUserName()+"templet_"+tabid);
				dataset=this.userview.getUserName()+"templet_"+tabid;
			}
			this.hmuster_sql=strsql;
//人员筛选后清空选中的人员
			if(filterStr!=null&&filterStr.length()>0){
				if(flag==0){
					dao.update(" update "+this.userview.getUserName()+"templet_"+tabid+" set submitflag=0  ")	;
}else{
			//		dao.update(" update templet_"+tabid+" set submitflag=0  ");
				}
			}


			int record_num=0;
			this.hmuster_sql=strsql.toString();
			int index_from=strsql.toString().indexOf("from");
			int order_index=strsql.lastIndexOf("order by ");
			rset=dao.search("select count(*) "+strsql.toString().substring(index_from,order_index));
			if(rset.next()) {
                record_num=rset.getInt(1);
            }
			if(record_num>40)
			{
				int mode=record_num%40;
int _pageCount=record_num/40;
				if(mode>0) {
                    _pageCount++;
}
if(page_num>_pageCount) {
                    page_num=_pageCount;
                }
				this.pageCount=_pageCount;

				int first=(page_num-1)*40+1;
				int last=page_num*40;


				String order_sql=strsql.substring(order_index);
				String noselect=strsql.substring(6);
				String sub_sql=strsql.substring(6,order_index);//去掉了 select 去掉了 order by
				String execut_order=strsql.substring(0, order_index);//除去order by 语句
String querysql="select  * from ("+execut_order+") a where ";
				if(flag==0){//没有ins_id    sutemplet_1 这样的表
				    querysql=querysql+" not exists(select null from (";
				    if(this.infor_type==1){
				        querysql=querysql+" select top "+(first-1)+" "+noselect+" ) b where b.A0100=a.A0100 and b.BasePre=a.BasePre) "+order_sql;
				    }
				    if(this.infor_type==2){//对单位部门操作
				        querysql=querysql+" select top "+(first-1)+" "+noselect+") b where b.b0100=a.b0100) "+order_sql;
				    }
				    if(this.infor_type==3){//对岗位的操作
				        querysql=querysql+" select top "+(first-1)+" "+noselect+") b where b.e01a1=a.e01a1) "+order_sql;
				    }

				}else{//有ins_id     templet_1这样的表
				    querysql=querysql+" not exists(select null from ( ";
				    if(this.infor_type==1){
                        querysql=querysql+" select top "+(first-1)+" "+noselect+") b where b.A0100=a.A0100 and b.BasePre=a.BasePre and a.ins_id=b.ins_id) "+order_sql;
                    }
                    if(this.infor_type==2){//对单位部门操作
                        querysql=querysql+" select top "+(first-1)+" "+noselect+") b where b.b0100=a.b0100 and a.ins_id=b.ins_id) "+order_sql;
                    }
                    if(this.infor_type==3){//对岗位的操作
                        querysql=querysql+" select top "+(first-1)+" "+noselect+") b where b.e01a1=a.e01a1 and a.ins_id=b.ins_id) "+order_sql;
                    }
				}
				strsql="select x.* from  ( select row_number() over ("+order_sql+") as cn,"+sub_sql+") x  where cn between "+first+" and "+last;
				if(Sql_switcher.searchDbServer()==1){//如果是sqlserver
				    DatabaseMetaData dbMeta = conn.getMetaData();
				    int version=dbMeta.getDatabaseMajorVersion();  //  sql2000=8    sql2005=9    sql2008=10    sql2012=11
                    if(version==8){
                       strsql=querysql;
                    }
}

	}



			formbo.setUserview(this.userview);
formbo.setTablebo(this);
			if("5".equals(this.business_model)) {
                formbo.setFromApply(true);
      }

			HashMap f_cellhm=new HashMap();
			ArrayList fieldlist=pagebo.getAllFieldItem(f_cellhm);




			Field temp=null;
			if(this.infor_type==1)
			{
				if(f_cellhm.get("a0100_1")==null){
				temp=new Field("a0100",ResourceFactory.getProperty("a0100.label"));
				temp.setDatatype(DataType.STRING);
				temp.setVisible(false);
				temp.setNullable(false);
				temp.setKeyable(true);
				temp.setSortable(true);
				temp.setLength(10);
				fieldlist.add(temp);
				}
			}
			else if(this.infor_type==2)
			{
				if(f_cellhm.get("b0110_1")==null){
				temp=new Field("B0110",ResourceFactory.getProperty("column.sys.org"));
				temp.setDatatype(DataType.STRING);
				temp.setLength(30);
				temp.setVisible(false);
				temp.setNullable(false);
				temp.setCodesetid("0");
				temp.setKeyable(true);
				fieldlist.add(temp);
				}
			}
			else if(this.infor_type==3)
			{
				if(f_cellhm.get("e01a1_1")==null){
				temp=new Field("E01A1",ResourceFactory.getProperty("column.sys.pos"));
				temp.setDatatype(DataType.STRING);
				temp.setLength(30);
				temp.setVisible(false);
				temp.setNullable(false);
				temp.setCodesetid("0");
				temp.setKeyable(true);
				fieldlist.add(temp);
				}
			}

			if(this.infor_type==1)
			{
				if(f_cellhm.get("a0101_1")==null)
				{
					temp=new Field("a0101_1",ResourceFactory.getProperty("a0100.label"));
					temp.setDatatype(DataType.STRING);
					temp.setVisible(false);
					temp.setNullable(false);
					temp.setLength(30);
					fieldlist.add(temp);
				}
				if(f_cellhm.get("basepre")==null){
        	temp=new Field("basepre",ResourceFactory.getProperty("label.dbase"));
				temp.setDatatype(DataType.STRING);
				temp.setVisible(false);
				temp.setNullable(false);
				temp.setSortable(true);
				temp.setKeyable(true);
				temp.setLength(3);
				fieldlist.add(temp);
				}
			}
			else
			{
				if(f_cellhm.get("codeitemdesc_1")==null)
				{
					if(this.infor_type==2) {
                        temp=new Field("codeitemdesc_1",ResourceFactory.getProperty("general.template.orgname"));
                    }
					if(this.infor_type==3) {
                        temp=new Field("codeitemdesc_1",ResourceFactory.getProperty("e01a1.label"));
}
temp.setDatatype(DataType.STRING);
					temp.setLength(50);
					temp.setVisible(true);
                temp.setNullable(true);
                temp.setKeyable(false);
					fieldlist.add(temp);
				}
			}



			temp=new Field("submitflag","submitflag");
			temp.setDatatype(DataType.INT);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setSortable(false);
temp.setValue("0");
			fieldlist.add(temp);

			if(flag==0)
			{
				/**状态标志=0,=1来源消息(其它模板发过来的通知)*/
				temp=new Field("state","state");
				temp.setDatatype(DataType.INT);
				temp.setVisible(false);
				temp.setNullable(false);
				temp.setSortable(false);
				temp.setValue("0");
				fieldlist.add(temp);

			}
			else //审批表结构
			{
				temp=new Field("state","state");
	temp.setDatatype(DataType.INT);
				temp.setVisible(false);
				temp.setNullable(false);
				temp.setSortable(false);
				temp.setKeyable(true);//key field
fieldlist.add(temp);
				temp=new Field("ins_id","ins_id");
				temp.setDatatype(DataType.INT);
				temp.setVisible(false);
				temp.setNullable(false);
				temp.setSortable(true);
				temp.setKeyable(true);//key field
fieldlist.add(temp);

				temp=new Field("task_id","task_id");
				temp.setDatatype(DataType.INT);
				temp.setVisible(false);
				temp.setNullable(false);
				temp.setSortable(false);
temp.setKeyable(false);//key field
				fieldlist.add(temp);

			}
			this.currentFieldlist=covertItemToField((ArrayList)fieldlist.clone());




			rset=dao.search(strsql);
HashMap codehm=new HashMap();
			formbo.setSum_domain_map(pagebo.getSub_domain_map());
			strhtml.append(formbo.createDataSetRecord(fieldlist,rset,codehm,this.conn,f_cellhm,submitFlagMap));

			if(this.infor_type==2||this.infor_type==3)
			{
				priv_html=formbo.createDataSetRecordPriv(fieldlist,rset,codehm,this.conn);
			}
			this.signxml = formbo.getSignxml().toString();
strhtml.append("\n");
		/**输出代码*/
			//strhtml.append(DataTable.outCodeJs(codehm,1));
			strhtml.append("\n");
			int maxrows=getMaxRecord(flag);
			/**额外增加姓名列，对单位信息则应去掉*/
//			FieldItem item=DataDictionary.getFieldItem("A0101");
//			if(item!=null)
//			{
//				/**可以增加模板指标与字典表指标进行校验*/
//				FieldItem tempitem=(FieldItem)item.cloneItem();
//				tempitem.setNChgstate(1);
//				fieldlist.add(tempitem);
//			}

			HashMap itemCodeMap = getitemCounts();
			String strjs=formbo.createDataSetJavaScript(fieldlist,1,1,maxrows,f_cellhm,itemCodeMap);
			strhtml.append(strjs);
			//System.out.println("strjs="+strjs);
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
   ex.printStackTrace();
   throw GeneralExceptionHandler.Handle(ex);
		}
	}


	/**
	 * 查找变化前历史记录单元格(多条或条件定位)
	 * 字段名+"_"+[1|2]s
	 * @return
	 * @throws GeneralException
	 */
	public HashMap getHisModeCell()throws GeneralException
	{
		HashMap map=new HashMap();
		StringBuffer buf=new StringBuffer();
		try
		{
			String temp=null;
			ContentDAO dao=new ContentDAO(this.conn);
			buf.append("select * from template_set where tabid=");
			buf.append(this.tabid);
			buf.append(" and (hismode=2 or hismode=3 or hismode=4) and chgstate=1 and flag in ('A','B','K')");
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				TemplateSetBo setbo=new TemplateSetBo(this.conn);
				setbo.setHz(rset.getString("hz"));
				setbo.setSetname(rset.getString("setname"));
				setbo.setCodeid(rset.getString("codeid"));
				setbo.setField_hz(rset.getString("Field_hz"));
				setbo.setField_name(rset.getString("Field_name"));
				setbo.setField_type(rset.getString("Field_type"));
				setbo.setFlag(rset.getString("Flag"));
				setbo.setFormula(Sql_switcher.readMemo(rset,"Formula"));
				setbo.setAlign(rset.getInt("Align"));
				setbo.setB(rset.getInt("B"));
				setbo.setChgstate(rset.getInt("ChgState"));
				setbo.setDisformat(rset.getInt("DisFormat"));
				setbo.setFonteffect(rset.getInt("Fonteffect"));
				setbo.setFontname(rset.getString("FontName"));
				setbo.setFontsize(rset.getInt("Fontsize"));
				setbo.setHismode(rset.getInt("HisMode"));
				setbo.setL(rset.getInt("L"));
				if(Sql_switcher.searchDbServer()==2) {
                    setbo.setMode(rset.getInt("Mode_o"));
                } else {
                    setbo.setMode(rset.getInt("Mode"));
                }
				setbo.setNsort(rset.getInt("nSort"));
				setbo.setR(rset.getInt("R"));
				setbo.setT(rset.getInt("T"));
				setbo.setRcount(rset.getInt("Rcount"));
				setbo.setRheight(rset.getInt("RHeight"));
				setbo.setRleft(rset.getInt("RLeft"));
				setbo.setRwidth(rset.getInt("RWidth"));
				setbo.setRtop(rset.getInt("RTop"));
				temp=rset.getString("subflag");
				if(temp==null|| "".equals(temp)|| "0".equals(temp)) {
                    setbo.setSubflag(false);
                } else {
                    setbo.setSubflag(true);
                }
				if(rset.getInt("yneed")==0) {
                    setbo.setYneed(false);
                } else {
                    setbo.setYneed(true);
                }
				setbo.setXml_param(Sql_switcher.readMemo(rset,"sub_domain"));
				//setbo.setUserview(this.userview);
				if(setbo.getField_name()==null) {
                    continue;
                }
				FieldItem item=DataDictionary.getFieldItem(setbo.getField_name());
				if(item!=null)
				{
					/**可以增加模板指标与字典表指标进行校验*/
					FieldItem tempitem=(FieldItem)item.cloneItem();
					tempitem.setNChgstate(setbo.getChgstate());
					map.put(item.getItemid()+"_"+setbo.getChgstate(),setbo);
				}
			}//while loop end.
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return map;
	}



	/**
	 * 查找 变化后的无写权限的单元格( 非子集 )
	 * 字段名+"_"+[1|2]s
	 * @return
	 * @throws GeneralException
	 */
	public HashMap getModeCell2()throws GeneralException
	{
		HashMap map=new HashMap();
		StringBuffer buf=new StringBuffer();
		try
		{
			String temp=null;
			ContentDAO dao=new ContentDAO(this.conn);
			buf.append("select * from template_set where ( subflag=0 or subflag is null ) and tabid="+this.tabid+" and chgstate=2 ");
			buf.append(" and flag in ('A','B','K')");
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{

				if(rset.getString("Field_name")==null) {
                    continue;
                }
				FieldItem item=DataDictionary.getFieldItem(rset.getString("Field_name"));
				if(item!=null)
				{
					if(isBEmploy())//员工通过自助平台发动申请
					{
						if(!"2".equals(this.userview.analyseFieldPriv(item.getItemid(),0))&& "0".equals(this.UnrestrictedMenuPriv_Input)) {
                            map.put(item.getItemid()+"_2","1");
                        }
					}
					else
					{
						if(!"2".equals(this.userview.analyseFieldPriv(item.getItemid()))&& "0".equals(this.UnrestrictedMenuPriv_Input))
						{
							map.put(item.getItemid()+"_2","1");
//							if(this.userview.analyseFieldPriv(item.getItemid(),0).equals("2")&&this.UnrestrictedMenuPriv_Input.equals("0"))
//								map.remove(item.getItemid()+"_2");
						}
					}
				}
			}//while loop end.
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return map;
	}


	/**
	 * 查找 变化后的的单元格( 非子集 )
	 * 字段名+"_"+[1|2]s
	 * @return
	 * @throws GeneralException
	 */
	public HashMap getModeCell3()throws GeneralException
	{
		HashMap map=new HashMap();
		StringBuffer buf=new StringBuffer();
		try
		{
			String temp=null;
			ContentDAO dao=new ContentDAO(this.conn);
			buf.append("select * from template_set where ( subflag=0 or subflag is null ) and tabid="+this.tabid+" and chgstate=2 ");
			buf.append(" and flag in ('A','B','K')");
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{

				if(rset.getString("Field_name")==null) {
                    continue;
                }
				FieldItem item=DataDictionary.getFieldItem(rset.getString("Field_name"));
				if(item!=null)
				{
						map.put(item.getItemid()+"_2","1");
				}
			}//while loop end.
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return map;
	}
	/**
	 * 查找 变化后的的单元格各属性( 非子集 )
	 * 字段名+"_"+[1|2]s
	 * @return
	 * @throws GeneralException
	 */
	public HashMap getModeCell4()throws GeneralException
	{
		HashMap map=new HashMap();
		StringBuffer buf=new StringBuffer();
		try
		{
			String temp=null;
			ContentDAO dao=new ContentDAO(this.conn);
			buf.append("select * from template_set where ( subflag=0 or subflag is null ) and tabid="+this.tabid+" and chgstate=2 ");
			buf.append(" and flag in ('A','B','K')");
			RowSet rset=dao.search(buf.toString());
			LazyDynaBean abean=null;
			while(rset.next())
			{

				if(rset.getString("Field_name")==null) {
         continue;
                }
//				System.out.println(rset.getString("Field_name"));
				String tempField_name=rset.getString("Field_name");
				FieldItem item=DataDictionary.getFieldItem(rset.getString("Field_name"));
				if("parentid".equals(tempField_name)||item!=null)
				{

						abean=new LazyDynaBean();
						abean.set("hz",rset.getString("hz")!=null?rset.getString("hz"):"");
						abean.set("field_name",rset.getString("field_name")!=null?rset.getString("field_name"):"");
						abean.set("field_type",rset.getString("field_type")!=null?rset.getString("field_type"):"");
						abean.set("field_hz",rset.getString("field_hz")!=null?rset.getString("field_hz"):"");
						abean.set("codeid",rset.getString("codeid")!=null?rset.getString("codeid"):"");
						abean.set("setname",rset.getString("setname")!=null?rset.getString("setname"):"");
						abean.set("chgstate",rset.getString("chgstate")!=null?rset.getString("chgstate"):"");
						abean.set("hismode",rset.getString("hismode")!=null?rset.getString("hismode"):"");
						abean.set("subflag",rset.getString("subflag")!=null?rset.getString("subflag"):"");
						abean.set("isvar","0");
						abean.set("pageid",""+rset.getInt("pageid"));
						abean.set("gridno",""+rset.getInt("gridno"));
						abean.set("yneed",""+rset.getInt("yneed"));
						abean.set("disformat",""+rset.getInt("disformat"));
						abean.set("formula",rset.getString("formula")!=null?rset.getString("formula"):"");
						abean.set("sub_domain",""+SafeCode.encode(Sql_switcher.readMemo(rset, "sub_domain")));
						if(item!=null){
						    if(map.get(item.getItemid()+"_2")==null) {
                                map.put(item.getItemid()+"_2",abean);
                            } else{
		                            if(Sql_switcher.readMemo(rset, "sub_domain")!=null&&!"".equals(Sql_switcher.readMemo(rset, "sub_domain"))) {
                                        map.put(item.getItemid()+"_2",abean);
                                    }
		                        }
						}else{
						    if(map.get(tempField_name+"_2")==null) {
                                map.put(tempField_name+"_2",abean);
                            } else{
                                    if(Sql_switcher.readMemo(rset, "sub_domain")!=null&&!"".equals(Sql_switcher.readMemo(rset, "sub_domain"))) {
                                        map.put(tempField_name+"_2",abean);
                                    }
                                }
						}

				}
			}//while loop end.
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return map;
	}
	/**
	 * 判断是否是黑名单数据
	 * @param blacklist_per
	 * @param blacklist_field
	 * @param value
	 * @return
	 */
	public boolean validateIsBlackList(String blacklist_per,String blacklist_field,String value)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from "+blacklist_per+"A01 where "+blacklist_field+"='"+value+"'");
			if(rowSet.next()) {
                flag=true;
            }
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}



	/**
	 * 取得子集且为变化后的区域
	 * @return
	 * @throws GeneralException
	 */
	public HashMap getHisModeSubCell()throws GeneralException
	{
		HashMap map=new HashMap();
		StringBuffer buf=new StringBuffer();
		try
		{
			String temp=null;
			ContentDAO dao=new ContentDAO(this.conn);
			buf.append("select * from template_set where tabid=");
			buf.append(this.tabid);
			buf.append(" and subflag=1 and flag in ('A','B','K')");
RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				TemplateSetBo setbo=new TemplateSetBo(this.conn);
				setbo.setHz(rset.getString("hz"));
				setbo.setSetname(rset.getString("setname"));
				setbo.setCodeid(rset.getString("codeid"));
				setbo.setField_hz(rset.getString("Field_hz"));
				setbo.setField_name(rset.getString("Field_name"));
				setbo.setField_type(rset.getString("Field_type"));
				setbo.setFlag(rset.getString("Flag"));
				setbo.setFormula(Sql_switcher.readMemo(rset,"Formula"));
				setbo.setAlign(rset.getInt("Align"));
				setbo.setB(rset.getInt("B"));
				setbo.setChgstate(rset.getInt("ChgState"));
				setbo.setDisformat(rset.getInt("DisFormat"));
				setbo.setFonteffect(rset.getInt("Fonteffect"));
				setbo.setFontname(rset.getString("FontName"));
				setbo.setFontsize(rset.getInt("Fontsize"));
				setbo.setHismode(rset.getInt("HisMode"));
				setbo.setL(rset.getInt("L"));
				if(Sql_switcher.searchDbServer()==2) {
                    setbo.setMode(rset.getInt("Mode_o"));
                } else {
                    setbo.setMode(rset.getInt("Mode"));
                }
				setbo.setNsort(rset.getInt("nSort"));
				setbo.setR(rset.getInt("R"));
				setbo.setT(rset.getInt("T"));
				setbo.setRcount(rset.getInt("Rcount"));
				setbo.setRheight(rset.getInt("RHeight"));
				setbo.setRleft(rset.getInt("RLeft"));
				setbo.setRwidth(rset.getInt("RWidth"));
				setbo.setRtop(rset.getInt("RTop"));
				temp=rset.getString("subflag");
				if(temp==null|| "".equals(temp)|| "0".equals(temp)) {
                    setbo.setSubflag(false);
                } else {
                    setbo.setSubflag(true);
                }
				if(rset.getInt("yneed")==0) {
                    setbo.setYneed(false);
                } else {
                    setbo.setYneed(true);
                }
				setbo.setXml_param(Sql_switcher.readMemo(rset,"sub_domain"));
				map.put("t_"+setbo.getSetname()+"_"+setbo.getChgstate(),setbo);
			}//while loop end.
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return map;
	}

	/**
	 * 刷新数据，
	 * @param transvo
	 * @throws GeneralException
	 */
	public void flushData(TransVo transvo)throws GeneralException
	{
		RowSet rset=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(transvo.getDatasetSql());
			/**先找到变化前历史记录*/
			HashMap map=getHisModeCell();
			HashMap hmap=getHisModeSubCell();

	        Iterator seq=hmap.entrySet().iterator();
			while(seq.hasNext())
			{
				Entry entry=(Entry)seq.next();
				String fieldname=(String)entry.getKey();
				map.put(fieldname.toLowerCase(), entry.getValue());
			}

			AutoFormBo formbo=new AutoFormBo(transvo.getDatasetid(),this.operationtype);
			formbo.setUserview(this.userview);
			formbo.setTablebo(this);
			formbo.getDataSetRecord(transvo,rset,this.conn,map);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeDbObj(rset);
		}
	}
	/**
	 * 求对应模板临时中的最大记录数
	 * @param flag =0与用户名有关的临时表 =1无关的临时表
	 * @return
	 */
	private int getMaxRecord(int flag)
	{
		StringBuffer strsql=new StringBuffer();
		String setname="templet_"+this.tabid ;
		if(flag==0) {
            setname=this.userview.getUserName()+setname;
        }
		strsql.append("select count(*) as nmax from ");
		strsql.append(setname);
		if(flag==1)
		{
			StringBuffer strins=new StringBuffer();
			for(int i=0;i<inslist.size();i++)
			{
				if(i!=0) {
                    strins.append(",");
                }
  			    strins.append((String)inslist.get(i));
			}
			strsql.append(" where ins_id in(");
			strsql.append(strins.toString());
			strsql.append(")");
		}
		int maxs=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(strsql.toString());
			if(rset.next()) {
                maxs=rset.getInt("nmax");
            }
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return maxs;
	}
	/**
	 * 取得输出花名册及以WORD、EXCEL模板
	 * @return 返回 LazyDynaBean对象列表,对象
	 * 包括三个属性
	 * id 花名册号
	 * name 名称
	 * flag 标志位 =0花名册 =1输出模板 =2登记表
	 */
	public ArrayList getMusterOrTemplate()
	{
		ArrayList list=new ArrayList();
		/**取花名册列表*/
		ContentDAO dao=null;
		RowSet rset=null;
		try
		{
			StringBuffer strsql=new StringBuffer();
			String strtabid=null;
			/**登记表*/
			dao=new ContentDAO(this.conn);
			if(!((this.card_str==null|| "".equals(this.card_str))) && this.operationtype!=0)
			{
				strtabid=this.card_str.replaceAll("`",",");
				strsql.append("select tabid,name from rname where tabid in (");
				strsql.append(strtabid);
				strsql.append(")");
				rset=dao.search(strsql.toString());
				while(rset.next())
				{
					LazyDynaBean bean=new LazyDynaBean();
					bean.set("tabid",String.valueOf(this.tabid));
					bean.set("id",rset.getString("tabid"));
					bean.set("name",rset.getString("name"));
					bean.set("flag","2");
					list.add(bean);
				}
			}
			/**高级花名册*/
			if(!(this.muster_str==null|| "".equals(this.muster_str)))
			{
				strtabid=this.muster_str.replaceAll("`",",");
				strsql.append("select tabid,cname from muster_name where tabid in (");
				strsql.append(strtabid);
				strsql.append(")");
				rset=dao.search(strsql.toString());
				while(rset.next())
				{
					LazyDynaBean bean=new LazyDynaBean();
					bean.set("tabid",String.valueOf(this.tabid));
					bean.set("id",rset.getString("tabid"));
					bean.set("name",rset.getString("cname"));
					bean.set("flag","0");
					list.add(bean);
				}
			}
			/**模板单据WORD/EXCEL模板*/
strsql.setLength(0);
strsql.append("select tp_id,name,content from t_wf_template where tabid=");
			strsql.append(this.tabid);
			rset=dao.search(strsql.toString());
			DOMParser parser = new DOMParser();
			while(rset.next())
			{
				LazyDynaBean bean=new LazyDynaBean();
				bean.set("tabid",String.valueOf(this.tabid));
				bean.set("id",rset.getString("tp_id"));
				bean.set("name",rset.getString("name"));
				bean.set("flag","1");

				InputStream in = null;
				try
				{
					in = rset.getBinaryStream("content");
					InputSource inputsource=new InputSource(in);
					parser.parse(inputsource);
					org.w3c.dom.Document doc=parser.getDocument();
			org.w3c.dom.Node node=doc.getDocumentElement().getFirstChild();
					if(node.getNamespaceURI().length()==0) {
                        continue;
                    }
				}
				catch(Exception ee)
				{
					//ee.printStackTrace();
					continue;
				} finally {
				    PubFunc.closeIoResource(in);
				}

				list.add(bean);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rset);
		}
		return list;
	}
    /**
     * 把业务员处理的业务数据提交到审批流节中去
     * @param username 用户名
     * @param ins_id 实例号
     * @return
     */
    public boolean saveSubmitTemplateData(String username,int ins_id,String whl)throws GeneralException
    {
        boolean bflag=true;
        try
        {
             bflag=saveSubmitTemplateData(username,ins_id,whl,-1);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return bflag;
    }
	/**
	 * 把业务员处理的业务数据提交到审批流节中去
	 * @param username 用户名
	 * @param ins_id 实例号
	 * @param whl 条件
	 * @param archive_flag -1 无意义，0:不入库 1: 入库。wangrd 2013-11-26
* @return
*/
public boolean saveSubmitTemplateData(String username,int ins_id,String whl,int archive_flag)throws GeneralException
	{
		boolean bflag=true;
		String srcTab=username+"templet_"+this.tabid;
		if(isBEmploy())//员工通过自助平台发动申请
        {
            srcTab="g_templet_"+tabid;
        }

String destTab="templet_"+this.tabid;
RowSet rset=null;
		try
		{
			StringBuffer strsql=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			strsql.append("select count(*) as nrec from ");
			strsql.append(srcTab);
			if(isBEmploy())//员工通过自助平台发动申请
            {
                strsql.append(" where a0100='"+this.userview.getA0100()+"' and lower(basepre)='"+this.userview.getDbname().toLowerCase()+"'");
            } else
			{
				strsql.append(" where submitflag=1"+whl);

			}
			rset=dao.search(strsql.toString());
			if(rset.next())
			{
				if(rset.getInt("nrec")==0) {
                    throw new GeneralException(ResourceFactory.getProperty("error.not.man"));
                }
			}

			String task_id="";
			rset=dao.search("select task_id from t_wf_task where ins_id="+ins_id);
			if(rset.next()) {
                task_id=rset.getString(1);
            }



			ArrayList fieldlist=getAllFieldItems();
			/**必填项校验*/
		//	checkMustFillItem(srcTab,fieldlist,/*ins_id*/0);
		//	checkLogicExpress(srcTab, 0, fieldlist);
			strsql.setLength(0);
			strsql.append("select * from ");
			strsql.append(srcTab);
			if(isBEmploy())//员工通过自助平台发动申请
            {
                strsql.append(" where a0100='"+this.userview.getA0100()+"' and lower(basepre)='"+this.userview.getDbname().toLowerCase()+"'");
            } else {
                strsql.append(" where submitflag=1"+whl);//对选中的人提交审批
            }
			rset=dao.search(strsql.toString());

			ArrayList reclist=new ArrayList();
			HashMap recBigTextFieldMap=new HashMap();
			while(rset.next())
			{
				RecordVo recvo=new RecordVo(destTab);
				recvo.setInt("state",rset.getInt("state"));
				recvo.setInt("ins_id",ins_id);
				recvo.setString("seqnum",rset.getString("seqnum"));
				if(this.infor_type==1)
				{
					recvo.setString("a0100",rset.getString("a0100"));
					recvo.setString("basepre",rset.getString("basepre"));
					recvo.setString("b0110_1",rset.getString("b0110_1"));
					recvo.setString("e0122_1",rset.getString("e0122_1"));
					recvo.setString("a0101_1",rset.getString("a0101_1"));
					recvo.setString("a0000",rset.getString("a0000"));
				}
				else
				{
					if(this.infor_type==2) {
                        recvo.setString("b0110",rset.getString("b0110"));
                    } else if(this.infor_type==3) {
                        recvo.setString("e01a1",rset.getString("e01a1"));
                    }

				}
				if(task_id.length()>0)
				{
					recvo.setInt("task_id",Integer.parseInt(task_id));
				}
				recvo.setInt("archive_flag",archive_flag);//-1 无意义，0:不入库 1: 入库。wangrd 2013-11-26
				HashMap fieldMap=new HashMap();
				for(int i=0;i<fieldlist.size();i++)
				{
					FieldItem fielditem=(FieldItem)((FieldItem)fieldlist.get(i)).cloneItem();
					String field_name=fielditem.getItemid();
					if(field_name.toLowerCase().indexOf("attachment")>-1)//过滤附件
                    {
                        continue;
                    }
					if("A00".equals(fielditem.getFieldsetid())/*&&(!(field_name.equalsIgnoreCase("photo")||field_name.equalsIgnoreCase("ext")))*/)
					{
						/**对人员调入才进行相应的操作*/
						//if(this.operationtype==0)
						//{
						if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
						{
							if("photo".equalsIgnoreCase(field_name)&&rset.getObject(field_name)!=null) {
                                recvo.setObject("photo", rset.getObject(field_name));
                            }
						}
						recvo.setString("fileid", rset.getString("fileid"));
						if("ext".equalsIgnoreCase(field_name)) {
                            recvo.setString("ext", rset.getString("ext"));
                        }
						//}
						if("signature".equalsIgnoreCase(field_name)){
							recvo.setString(field_name.toLowerCase(),Sql_switcher.readMemo(rset,field_name));
						}
						continue;
					}
					if(fielditem.isChangeAfter()&&this.opinion_field!=null&&this.opinion_field.length()>0&&this.opinion_field.equalsIgnoreCase(field_name))
					{
						String old_value=Sql_switcher.readMemo(rset,field_name.toLowerCase()+"_2");  //20150425 dengcan 通知单带过来的审批记录不能清空，得追加（汉口银行）
						//this.approve_opinion=old_value+"\r\n"+this.approve_opinion;
						String _approve_opinion=old_value+"\r\n"+this.approve_opinion;
						recvo.setString(field_name.toLowerCase()+"_2",_approve_opinion);
						continue;
					}
					if(fielditem.isChangeAfter()){
						field_name=field_name+"_2";
						//兼容新人事异动 存在多个变化后子集的情况。wangrd 20160826
						if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0){
							field_name=fielditem.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_2";
							DbWizard dbw = new DbWizard(this.conn);
							if (!dbw.isExistField(destTab, field_name)){
								field_name=fielditem.getItemid()+"_2";
							}
						}
					}
					else if(fielditem.isChangeBefore()){
						field_name=field_name+"_1";
						if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0){
							field_name=fielditem.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_1";
							}
					}

					if(this.field_name_map!=null&&this.field_name_map.get(field_name.toLowerCase())!=null) {
                        fielditem.setItemtype("M");
                    }
					if(fieldMap.containsKey(field_name.toLowerCase())){
						continue;
					}
					fieldMap.put(field_name.toLowerCase(), field_name.toLowerCase());
					if("A".equalsIgnoreCase(fielditem.getItemtype())) {
                        recvo.setString(field_name.toLowerCase(),rset.getString(field_name));
                    } else if("N".equalsIgnoreCase(fielditem.getItemtype()))
					{
						if(rset.getString(field_name)!=null)
						{
							if(fielditem.getDecimalwidth()==0) {
                                recvo.setInt(field_name.toLowerCase(),rset.getInt(field_name));
                            } else {
                                recvo.setDouble(field_name.toLowerCase(),rset.getDouble(field_name));
                            }
						}

					}
					else if("M".equalsIgnoreCase(fielditem.getItemtype())){
						//recvo.setString(field_name.toLowerCase(),Sql_switcher.readMemo(rset,field_name));
						recBigTextFieldMap.put(field_name.toLowerCase(), field_name.toLowerCase());
                }
					else
					{
						if(Sql_switcher.searchDbServer()==2)
						{
							Timestamp ta=rset.getTimestamp(field_name);
							if(ta!=null)
							{
								Date d=new Date(ta.getTime());
								recvo.setDate(field_name.toLowerCase(),d);
							}
						}
						else {
                            recvo.setDate(field_name.toLowerCase(),rset.getDate(field_name));
                        }
					}
            }
            if(this.infor_type==2||this.infor_type==3) {
                    recvo.setString("codeitemdesc_1", rset.getString("codeitemdesc_1"));
                }
				if((this.infor_type==2||this.infor_type==3)&&(this.operationtype==8||this.operationtype==9)) {
                    recvo.setString("to_id", rset.getString("to_id"));
                }

				reclist.add(recvo);
			}
			dao.addValueObject(reclist);
			RecordVo srctabvo=new RecordVo(srcTab);
			RecordVo desttabvo=new RecordVo(destTab);
			//liuyz 大文本段首空格被清除
			if(recBigTextFieldMap.size()>0){
				Iterator iterator  = recBigTextFieldMap.entrySet().iterator();
				DbWizard dbw=new DbWizard(this.conn);
				String srctab=srcTab;
				StringBuffer updateItem=new StringBuffer();
				boolean isHaveSignuare=false;
				while (iterator.hasNext()) {
					Entry entry = (Entry) iterator.next();
					if(entry.getKey().toString().toUpperCase().startsWith("S_")){//如果是签章，只需要拼接一次。
						if(!isHaveSignuare){
							isHaveSignuare=true;
							updateItem.append(destTab+".signature="+srctab+".signature`");
						}
					}else{
						updateItem.append(destTab+"."+entry.getKey()+"="+srctab+"."+entry.getKey()+"`");
					}
				}

				if(this.infor_type==1){
					dbw.updateRecord(destTab,srctab ,destTab+".A0100="+srctab+".A0100",updateItem.substring(0,updateItem.toString().length()-1),destTab+".ins_id="+ins_id,destTab+".basepre="+srctab+".basepre");
				} else if(this.infor_type==2){
    dbw.updateRecord(destTab,srctab ,destTab+".b0110="+srctab+".b0110",updateItem.substring(0,updateItem.toString().length()-1),destTab+".ins_id="+ins_id,"");
			    }else if(this.infor_type==3){
				dbw.updateRecord(destTab,srctab ,destTab+".e01a1="+srctab+".e01a1",updateItem.substring(0,updateItem.toString().length()-1),destTab+".ins_id="+ins_id,"");
				}
			}
			if(Sql_switcher.searchDbServer()==Constant.ORACEL&&srctabvo.hasAttribute("photo")&&desttabvo.hasAttribute("photo"))
			{
			/**photo ,ext*/
				strsql.setLength(0);
				DbWizard dbw=new DbWizard(this.conn);
				String srctab=srcTab;
				dbw.updateRecord(destTab,srctab ,destTab+".A0100="+srctab+".A0100",destTab+".photo="+srctab+".photo"+"`"+destTab+".ext="+srctab+".ext",destTab+".ins_id="+ins_id,destTab+".basepre="+srctab+".basepre"+whl);
			}
			/**清空以前的数据*/
			if(isBEmploy())//员工通过自助平台发动申请
            {
                dao.update("delete from "+srcTab+" where a0100='"+this.userview.getA0100()+"' and lower(basepre)='"+this.userview.getDbname().toLowerCase()+"'");
            } else {
                dao.update("delete from "+srcTab+" where submitflag=1"+whl);
            }



		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			String message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{
				PubFunc.resolve8060(this.conn,destTab);
				throw GeneralExceptionHandler.Handle(new Exception("请重新执行报批操作!"));
			}
			else {
                throw GeneralExceptionHandler.Handle(ex);
            }
		}finally{
			PubFunc.closeDbObj(rset);
		}
		return bflag;
	}

	/**
	 * 把员工自助申请的业务数据提交到审批流节中去
	 * @param username 用户名
	 * @return
	 */
	public boolean saveSubmitTemplateData(int ins_id)throws GeneralException
	{
		boolean bflag=true;
		String srcTab="g_templet_"+this.tabid;
		String destTab="templet_"+this.tabid;
		RowSet rset=null;
		try
		{
			StringBuffer strsql=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);

			ArrayList fieldlist=getAllFieldItem();
			/**必填项校验*/
			StringBuffer strWhere=new StringBuffer();
			strWhere.append(" basepre='");
			strWhere.append(this.userview.getDbname());
			strWhere.append("' and a0100='");
			strWhere.append(this.userview.getA0100());
			strWhere.append("'");

			checkMustFillItem(srcTab,fieldlist,strWhere.toString());

strsql.setLength(0);
strsql.append("select * from ");
			strsql.append(srcTab);
			strsql.append(" where ");
			strsql.append(strWhere.toString());
			rset=dao.search(strsql.toString());

			ArrayList reclist=new ArrayList();
			HashMap recBigTextFieldMap=new HashMap();

			while(rset.next())
			{
				RecordVo recvo=new RecordVo(destTab);
				recvo.setString("a0100",rset.getString("a0100"));
				recvo.setString("basepre",rset.getString("basepre"));
				recvo.setInt("state",rset.getInt("state"));
				recvo.setInt("ins_id",ins_id);
				recvo.setString("seqnum",rset.getString("seqnum"));
				recvo.setString("b0110_1",rset.getString("b0110_1"));
				recvo.setString("e0122_1",rset.getString("e0122_1"));
				recvo.setString("a0101_1",rset.getString("a0101_1"));
				recvo.setInt("archive_flag",-1);

				for(int i=0;i<fieldlist.size();i++)
				{
					FieldItem fielditem=(FieldItem)fieldlist.get(i);
					String field_name=fielditem.getItemid();
					if(field_name.toLowerCase().indexOf("attachment")>-1)//过滤附件
                    {
                        continue;
                    }
					if("A00".equals(fielditem.getFieldsetid())/*&&(!(field_name.equalsIgnoreCase("photo")||field_name.equalsIgnoreCase("ext")))*/)
					{
						/**对人员调入才进行相应的操作*/
						//if(this.operationtype==0)
						//{
						if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
						{
							if("photo".equalsIgnoreCase(field_name)&&rset.getObject(field_name)!=null) {
								recvo.setObject("photo", rset.getObject(field_name));
							}
						}

						if("ext".equalsIgnoreCase(field_name)) {
                            recvo.setString("ext", rset.getString("ext"));
                        }

						if("fileid".equalsIgnoreCase(field_name)) {
                            recvo.setString("fileid", rset.getString("fileid"));
                        }
						//}
						continue;
					}
					if(fielditem.isChangeAfter()&&this.opinion_field!=null&&this.opinion_field.length()>0&&this.opinion_field.equalsIgnoreCase(field_name))
					{
						String old_value=Sql_switcher.readMemo(rset,field_name.toLowerCase()+"_2");  //20150425 dengcan 通知单带过来的审批记录不能清空，得追加（汉口银行）
						//this.approve_opinion=old_value+"\r\n"+this.approve_opinion;
						String _approve_opinion=old_value+"\r\n"+this.approve_opinion;
						recvo.setString(field_name.toLowerCase()+"_2",_approve_opinion);
						continue;
					}
					if(fielditem.isChangeAfter()){
						field_name=field_name+"_2";
						//兼容新人事异动 存在多个变化后子集的情况。wangrd 20160826
						if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0){
							field_name=fielditem.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_2";
							DbWizard dbw = new DbWizard(this.conn);
							if (!dbw.isExistField(destTab, field_name)){
								field_name=fielditem.getItemid()+"_2";
							}
						}
					}
					else if(fielditem.isChangeBefore()){
						field_name=field_name+"_1";
						if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0){
field_name=fielditem.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_1";
							}
					}

					//维护多子集，多子集指标，变化前类型
					if(this.field_name_map!=null&&this.field_name_map.get(field_name.toLowerCase())!=null)
					{
						recvo.setString(field_name.toLowerCase(),Sql_switcher.readMemo(rset,field_name));
					}
                        else
					{
						if("A".equalsIgnoreCase(fielditem.getItemtype())) {
                            recvo.setString(field_name.toLowerCase(),rset.getString(field_name));
                        } else if("N".equalsIgnoreCase(fielditem.getItemtype()))
						{
							if(rset.getString(field_name)!=null) {
                                recvo.setDouble(field_name.toLowerCase(),rset.getDouble(field_name));
                            }
						}
						else if("M".equalsIgnoreCase(fielditem.getItemtype())){
							//recvo.setString(field_name.toLowerCase(),Sql_switcher.readMemo(rset,field_name));
							recBigTextFieldMap.put(field_name.toLowerCase(),field_name.toLowerCase());
						}
						else
						{
							if(Sql_switcher.searchDbServer()==2)
							{
								Timestamp ta=rset.getTimestamp(field_name);
								if(ta!=null)
								{
									Date d=new Date(ta.getTime());
									recvo.setDate(field_name.toLowerCase(),d);
								}
							}
							else {
                                recvo.setDate(field_name.toLowerCase(),rset.getDate(field_name));
                            }
						}
					}
				}
				recvo.setInt("submitflag", 1);
				reclist.add(recvo);
			}
			dao.addValueObject(reclist);
			//liuyz  大文本段首空格被清除
			if(recBigTextFieldMap.size()>0){
				Iterator iterator  = recBigTextFieldMap.entrySet().iterator();
				DbWizard dbw=new DbWizard(this.conn);
				String srctab=srcTab;
				StringBuffer updateItem=new StringBuffer();
				boolean isHaveSignuare=false;
				while (iterator.hasNext()) {
					Entry entry = (Entry) iterator.next();
					if(entry.getKey().toString().toUpperCase().startsWith("S_")){
						if(!isHaveSignuare){
							isHaveSignuare=true;
							updateItem.append(destTab+".signature="+srctab+".signature`");
						}
					}else{
						updateItem.append(destTab+"."+entry.getKey()+"="+srctab+"."+entry.getKey()+"`");
					}
				}
				updateItem.setLength(updateItem.length()-1);
				dbw.updateRecord(destTab,srctab ,destTab+".A0100="+srctab+".A0100",updateItem.toString(),destTab+".ins_id="+ins_id,destTab+".basepre="+srctab+".basepre");
			}
			RecordVo srctabvo=new RecordVo(srcTab);
			RecordVo desttabvo=new RecordVo(destTab);
			if(Sql_switcher.searchDbServer()==Constant.ORACEL&&srctabvo.hasAttribute("photo")&&desttabvo.hasAttribute("photo"))
			{
			/**photo ,ext*/
				strsql.setLength(0);
				DbWizard dbw=new DbWizard(this.conn);
				String srctab=srcTab;
				dbw.updateRecord(destTab,srctab ,destTab+".A0100="+srctab+".A0100",destTab+".photo="+srctab+".photo"+"`"+destTab+".ext="+srctab+".ext"+"`"+destTab+".fileid="+srctab+".fileid",destTab+".ins_id="+ins_id,destTab+".basepre="+srctab+".basepre");
			}
			/**清空以前的数据*/
			dao.update("delete from "+srcTab+" where "+strWhere.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			String message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{
				PubFunc.resolve8060(this.conn,destTab);
				throw GeneralExceptionHandler.Handle(new Exception("请重新执行报批操作!"));
			}
			else {
                throw GeneralExceptionHandler.Handle(ex);
            }
		}finally{
			PubFunc.closeDbObj(rset);
		}
		return bflag;
	}
	/**
	 * 同步修改审批表结构
	 * @return
	 */
	public boolean changeSpTableStrut()
	{
		boolean bflag=true;
		String tablename="templet_"+this.tabid; //"templet_"+this.tabid
		try
		{
			DbWizard dbwizard=new DbWizard(this.conn);
			ArrayList fieldList=getAllFieldItem();
			if(!dbwizard.isExistTable("templet_"+this.tabid,false))
			{
				Table table_wf=new Table("templet_"+this.tabid);
				addFieldItem(table_wf,1);
				dbwizard.createTable(table_wf);
				DbSecurityImpl dbS = new DbSecurityImpl();
				dbS.encryptTableName(this.conn, "templet_"+this.tabid);
			}
			else
			{
				Table table_wf=new Table("templet_"+this.tabid);
				updateTempTemplateStruct(table_wf,1,fieldList);
				syncGzField("templet_"+this.tabid);

			}
			/**重新加载数据模型*/
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tablename);
			dbmodel.reloadTableModel("templet_"+this.tabid);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
		return bflag;

	}

	/**
	 * 根据用户名以及模板结构创建临时表,包括增加及删除字段
	 * 维护两个表的结构,审批和原始表单
	 * A0100 varchar(10) ,basepre varchar(3),state int
	 * @param username
	 * @return
	 */
	public boolean createTempTemplateTable1(String username)
	{
		boolean bflag=true;
		String tablename=username+"templet_"+this.tabid; //"templet_"+this.tabid
		RowSet rowSet=null;
		try
		{
			Table table=new Table(tablename);
			DbWizard dbwizard=new DbWizard(this.conn);


			ResultSetMetaData md=null;
			ContentDAO dao=new ContentDAO(this.conn);
			if(dbwizard.isExistTable(tablename,false))
			{
				 rowSet=dao.search("select * from "+tablename+" where 1=2");
				 md=rowSet.getMetaData();
				 for(int i=1;i<=md.getColumnCount();i++)
				 {
					 String columnName=md.getColumnName(i);
					 if("a0100".equalsIgnoreCase(columnName)&&(this.infor_type==1||this.infor_type==2||this.infor_type==3))
					 {
						 dbwizard.dropTable(tablename);
						 break;
					 }
				 }
			}

			if(dbwizard.isExistTable("templet_"+this.tabid,false))
			{
				 rowSet=dao.search("select * from templet_"+this.tabid+" where 1=2");
				 md=rowSet.getMetaData();
				 for(int i=1;i<=md.getColumnCount();i++)
				 {
					 String columnName=md.getColumnName(i);
					 if("a0100".equalsIgnoreCase(columnName)&&(this.infor_type==2||this.infor_type==3))
					 {
						 dbwizard.dropTable("templet_"+this.tabid);
						 break;
					 }
				 }
			}




			DbSecurityImpl dbS = new DbSecurityImpl();
			ArrayList fieldList=getAllFieldItem();
			if(!dbwizard.isExistTable(tablename,false))
			{
				/**取得模板需要生成字段的表结构*/
				addFieldItem(table,0);
				if(Sql_switcher.searchDbServer()==2&&TemplateFuncBo.getStrLength(tablename+"_pk_1")>30){//bug 37792 orcl库的建表名、主键名最多30个字符
					bflag=false;
					throw new GeneralException("登录用户名太长，联系管理员重新创建登录用户名！");
				}
				dbwizard.createTable(table);
				dbS.encryptTableName(this.conn, tablename);
			}
			else
			{
				updateTempTemplateStruct(table,0,fieldList);
				syncGzField(tablename);
			}


			if(!dbwizard.isExistTable("templet_"+this.tabid,false))
			{
				Table table_wf=new Table("templet_"+this.tabid);
				addFieldItem(table_wf,1);
				dbwizard.createTable(table_wf);
				dbS.encryptTableName(this.conn, "templet_"+this.tabid);
			}
			else
			{
				Table table_wf=new Table("templet_"+this.tabid);
				updateTempTemplateStruct(table_wf,1,fieldList);
				syncGzField("templet_"+this.tabid);
			}
			/**重新加载数据模型*/
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tablename);
			dbmodel.reloadTableModel("templet_"+this.tabid);
			/**从消息库中导入此业务模板，可以取到的消息*/
			impDataFromMessage(tablename);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
		finally
		{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception ee)
			{

			}
		}
		return bflag;
	}



	/**
	 * 根据用户名以及模板结构创建临时表,包括增加及删除字段
	 * 维护两个表的结构,审批和原始表单
	 * A0100 varchar(10) ,basepre varchar(3),state int
	 * @param username
	 * @return
	 */
	public boolean createTempTemplateTable(String username) throws GeneralException
	{
		boolean bflag=true;
		String tablename=username+"templet_"+this.tabid; //"templet_"+this.tabid
		RowSet rowSet=null;
		try
		{
			DbSecurityImpl dbS = new DbSecurityImpl();
			Table table=new Table(tablename);
			DbWizard dbwizard=new DbWizard(this.conn);


			ResultSetMetaData md=null;
			ContentDAO dao=new ContentDAO(this.conn);
			if(dbwizard.isExistTable(tablename,false))
			{
				 rowSet=dao.search("select * from "+tablename+" where 1=2");
				 md=rowSet.getMetaData();
				 for(int i=1;i<=md.getColumnCount();i++)
				 {
					 String columnName=md.getColumnName(i);
					 if("a0100".equalsIgnoreCase(columnName)&&(this.infor_type==2||this.infor_type==3))
					 {
						 dbwizard.dropTable(tablename);
						 break;
					 }
				 }
			}

			if(dbwizard.isExistTable("templet_"+this.tabid,false))
			{
				 rowSet=dao.search("select * from templet_"+this.tabid+" where 1=2");
				 md=rowSet.getMetaData();
				 for(int i=1;i<=md.getColumnCount();i++)
				 {
					 String columnName=md.getColumnName(i);
					 if("a0100".equalsIgnoreCase(columnName)&&(this.infor_type==2||this.infor_type==3))
					 {
						 dbwizard.dropTable("templet_"+this.tabid);
						 break;
					 }
				 }
			}



			ArrayList fieldList=getAllFieldItem();

			if(!dbwizard.isExistTable(tablename,false))
			{
				/**取得模板需要生成字段的表结构*/
				addFieldItem(table,0);
				if(Sql_switcher.searchDbServer()==2&&TemplateFuncBo.getStrLength(tablename+"_pk_1")>30){//bug 37792 orcl库的建表名、主键名最多30个字符
					bflag=false;
					throw new GeneralException("登录用户名太长，联系管理员重新创建登录用户名！");
				}
				dbwizard.createTable(table);
				dbS.encryptTableName(this.conn, tablename);
			}
			else
			{
				updateTempTemplateStruct(table,0,fieldList);
				syncGzField(tablename);
			}


			if(!dbwizard.isExistTable("templet_"+this.tabid,false))
			{
				Table table_wf=new Table("templet_"+this.tabid);
				addFieldItem(table_wf,1);
				dbwizard.createTable(table_wf);
				dbS.encryptTableName(this.conn, tablename);
			}
			else
			{
				Table table_wf=new Table("templet_"+this.tabid);
				updateTempTemplateStruct(table_wf,1,fieldList);
				syncGzField("templet_"+this.tabid);
			}
			/**重新加载数据模型*/
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tablename);
			dbmodel.reloadTableModel("templet_"+this.tabid);
			/**从消息库中导入此业务模板，可以取到的消息*/
			impDataFromMessage(tablename);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
        bflag=false;
        throw GeneralExceptionHandler.Handle(ex);

		}
		finally
		{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception ee)
			{

			}
		}
		return bflag;
	}






	/**
	 *当指标长度或类型发生的变化同步 审批和原始表单
	 */
	private void  syncGzField(String tableName)
	{
		try
		{
			 ContentDAO dao=new ContentDAO(this.conn);
			 RowSet rowSet=dao.search("select * from "+tableName+" where 1=2");
			 ResultSetMetaData data=rowSet.getMetaData();
		//	 ArrayList list=getAllFieldItem();
			 /** 临时变量字段 */
			ArrayList fieldlist=getMidVariableList();
			FieldItem _item=null;
			HashMap varMap=new HashMap();
			for(int i=0;i<fieldlist.size();i++)
			{
					_item=(FieldItem)fieldlist.get(i);
					varMap.put(_item.getItemid().toLowerCase(),_item);
			}

			 ArrayList alterList=new ArrayList();
			 ArrayList resetList=new ArrayList();
			 for(int i=1;i<=data.getColumnCount();i++)
      {
					String columnName=data.getColumnName(i).toLowerCase();

					//对人员信息群增加三个固定字段B0110_1,E0122_1,A0101_1,单位、部门及姓名
					if("B0110_1".equalsIgnoreCase(columnName)|| "E01A1_1".equalsIgnoreCase(columnName)|| "E0122_1".equalsIgnoreCase(columnName)) //||columnName.equalsIgnoreCase("A0101_1"))
                    {
                        continue;
                    }
					if("codesetid_1".equalsIgnoreCase(columnName)|| "codeitemdesc_1".equalsIgnoreCase(columnName)|| "corcode_1".equalsIgnoreCase(columnName)|| "parentid_1".equalsIgnoreCase(columnName)|| "start_date_1".equalsIgnoreCase(columnName)) {
                        continue;
                    }
					if("codesetid_2".equalsIgnoreCase(columnName)|| "codeitemdesc_2".equalsIgnoreCase(columnName)|| "corcode_2".equalsIgnoreCase(columnName)|| "parentid_2".equalsIgnoreCase(columnName)|| "start_date_2".equalsIgnoreCase(columnName)) {
                        continue;
                    }
					if(columnName.indexOf("_1")!=-1||columnName.indexOf("_2")!=-1||varMap.get(columnName.toLowerCase())!=null)
					{

						FieldItem _tempItem=null;
						if(varMap.get(columnName.toLowerCase())==null)
						{//表明该字段不是临时变量
							String _columnName=columnName.substring(0,columnName.length()-2);
            							_tempItem=DataDictionary.getFieldItem(_columnName);
							if(_tempItem==null&&_columnName.split("_").length>1)//该字段在数据库那种临时表中应该是itemid_subdominid_[1||2]
                            {
                                _tempItem=DataDictionary.getFieldItem(_columnName.substring(0,columnName.indexOf("_")));
                            }

						}
						else {
                            _tempItem=(FieldItem)varMap.get(columnName.toLowerCase());
                        }
						if(_tempItem==null) {
                            continue;
                        }

						FieldItem tempItem=(FieldItem)_tempItem.cloneItem();
						tempItem.setItemid(columnName);
			 			if(this.field_name_map!=null&&this.field_name_map.get(columnName.toLowerCase())!=null) {
                            tempItem.setItemtype("M");
                        }
						int columnType=data.getColumnType(i);
						int size=data.getColumnDisplaySize(i);
						int scale=data.getScale(i);
						switch(columnType)
						{
							case java.sql.Types.BIGINT:
							case java.sql.Types.INTEGER:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale) {
                                        alterList.add(tempItem.cloneField());
                                    }
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype())) {
                                        alterList.add(tempItem.cloneField());
                                    } else {
                                        resetList.add(tempItem.cloneField());
                                    }
								}
								break;
							case java.sql.Types.TIMESTAMP:
							case java.sql.Types.DATE:
							  case java.sql.Types.TIME :
								if(!"D".equals(tempItem.getItemtype()))
								{
									resetList.add(tempItem.cloneField());
								}
								break;
							case java.sql.Types.VARCHAR:
								if("A".equals(tempItem.getItemtype()))
								{
									if(tempItem.getItemlength()>size) {
                                        alterList.add(tempItem.cloneField());
                                    }
				}
								else {
                                    resetList.add(tempItem.cloneField());
                                }
                                                break;
						case java.sql.Types.DOUBLE:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale) {
                                        alterList.add(tempItem.cloneField());
                                    }
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype())) {
                                        alterList.add(tempItem.cloneField());
                                    } else {
                                        resetList.add(tempItem.cloneField());
                                    }
								}


								break;
							case java.sql.Types.NUMERIC:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale) {
                                        alterList.add(tempItem.cloneField());
                                    }
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype())){
									    if (tempItem.getItemlength()>size){
									        alterList.add(tempItem.cloneField());
                                                            }
                                    					    else {//如果此列有数据且大于新字段类型长度，更改不成功 wangrd 2015-03-31
									        if (tempItem.getItemlength()<=(scale+1)){//如果小于等于(小数位数+小数点)，只能重建字段
									            resetList.add(tempItem.cloneField());
									        }
									        else {//截取字段最大长度为新指标的长度。
									            int newLen =tempItem.getItemlength()-scale-1;
									            String sql="update "+tableName+" set "+columnName+"=" + Sql_switcher.charToFloat(Sql_switcher.left(Sql_switcher.numberToChar(columnName), newLen))
									            +" where "+ Sql_switcher.length(Sql_switcher.numberToChar(columnName)) +" >"+String.valueOf(newLen);
									            dao.update(sql);
									            alterList.add(tempItem.cloneField());
									        }
									    }
									}
									else {
                                        resetList.add(tempItem.cloneField());
                                    }
								}
								break;
							case java.sql.Types.CLOB:
							  case java.sql.Types.LONGVARCHAR:
							  case java.sql.Types.LONGVARBINARY:
								if(!"M".equals(tempItem.getItemtype()))
								{
									resetList.add(tempItem.cloneField());
								}
								break;
						}
					}
				}
				rowSet.close();
				DbWizard dbw=new DbWizard(this.conn);
			    Table table=new Table(tableName);
			    if(Sql_switcher.searchDbServer()!=2)  //不为oracle
			    {
				    for(int i=0;i<alterList.size();i++) {
                        table.addField((Field)alterList.get(i));
                    }
					if(alterList.size()>0) {
                        dbw.alterColumns(table);
                    }
					 table.clear();
			    }
			    else {
                    syncGzOracleField(data,tableName,varMap);
                }
				 for(int i=0;i<resetList.size();i++) {
                     table.addField((Field)resetList.get(i));
                 }
				 if(resetList.size()>0)
				 {
					 dbw.dropColumns(table);
					 dbw.addColumns(table);
				 }



		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}




	private void syncGzOracleField(ResultSetMetaData data,String tableName,HashMap varMap)
	{
		try
		{
			 DbWizard dbw=new DbWizard(this.conn);
			 ContentDAO dao=new ContentDAO(this.conn);
			 for(int i=1;i<=data.getColumnCount();i++)
			 {
				String columnName=data.getColumnName(i).toLowerCase();
				//对人员信息群增加三个固定字段B0110_1,E0122_1,A0101_1,单位、部门及姓名
			//	if(columnName.equalsIgnoreCase("B0110_1")||columnName.equalsIgnoreCase("E01A1_1")||columnName.equalsIgnoreCase("E0122_1")||columnName.equalsIgnoreCase("A0101_1"))
   //		continue;
       //对人员信息群增加三个固定字段B0110_1,E0122_1,A0101_1,单位、部门及姓名
				if("B0110_1".equalsIgnoreCase(columnName)|| "E01A1_1".equalsIgnoreCase(columnName)|| "E0122_1".equalsIgnoreCase(columnName)) //||columnName.equalsIgnoreCase("A0101_1"))
                {
                    continue;
                }

				if("codesetid_1".equalsIgnoreCase(columnName)|| "codeitemdesc_1".equalsIgnoreCase(columnName)|| "corcode_1".equalsIgnoreCase(columnName)|| "parentid_1".equalsIgnoreCase(columnName)|| "start_date_1".equalsIgnoreCase(columnName)) {
                    continue;
                }
				if("codesetid_2".equalsIgnoreCase(columnName)|| "codeitemdesc_2".equalsIgnoreCase(columnName)|| "corcode_2".equalsIgnoreCase(columnName)|| "parentid_2".equalsIgnoreCase(columnName)|| "start_date_2".equalsIgnoreCase(columnName)) {
                    continue;
                }

				if(columnName.indexOf("_1")!=-1||columnName.indexOf("_2")!=-1||varMap.get(columnName.toLowerCase())!=null)
				{
				/*
					String _columnName=columnName.substring(0,columnName.length()-2);
					FieldItem _tempItem=DataDictionary.getFieldItem(_columnName);
					if(_tempItem==null)
						continue;
				*/
					FieldItem _tempItem=null;
					if(varMap.get(columnName.toLowerCase())==null)
					{
						String _columnName=columnName.substring(0,columnName.length()-2);
						_tempItem=DataDictionary.getFieldItem(_columnName);
						if(_tempItem==null&&_columnName.split("_").length>1) {
                            _tempItem=DataDictionary.getFieldItem(_columnName.substring(0,columnName.indexOf("_")));
                        }

					}
					else {
                        _tempItem=(FieldItem)varMap.get(columnName.toLowerCase());
                }
					if(_tempItem==null) {
                        continue;
                    }



					FieldItem tempItem=(FieldItem)_tempItem.cloneItem();
					if(this.field_name_map!=null&&this.field_name_map.get(columnName.toLowerCase())!=null) {
                        tempItem.setItemtype("M");
                    }
					tempItem.setItemid(columnName);
					int columnType=data.getColumnType(i);
					int size=data.getColumnDisplaySize(i);
					int scale=data.getScale(i);
					switch(columnType)
					{
						case java.sql.Types.INTEGER:
							if("N".equals(tempItem.getItemtype()))
							{
								if(tempItem.getDecimalwidth()!=scale) {
                                    alertColumn(tableName,tempItem,dbw,dao);
                                }
							}
							if(!"N".equals(tempItem.getItemtype()))
							{
								if("A".equals(tempItem.getItemtype())) {
                                    alertColumn(tableName,tempItem,dbw,dao);
                                }

							}
							break;
						case java.sql.Types.VARCHAR:
							if("A".equals(tempItem.getItemtype()))
							{
								if(tempItem.getItemlength()>size) {
                                    alertColumn(tableName,tempItem,dbw,dao);
                                }
							}
							break;
						case java.sql.Types.DOUBLE:
							if("N".equals(tempItem.getItemtype()))
							{
								if(tempItem.getDecimalwidth()!=scale) {
                                    alertColumn(tableName,tempItem,dbw,dao);
                                }
							}
							if(!"N".equals(tempItem.getItemtype()))
							{
								if("A".equals(tempItem.getItemtype())) {
                                    alertColumn(tableName,tempItem,dbw,dao);
                                }
							}


							break;
						case java.sql.Types.NUMERIC:
							if("N".equals(tempItem.getItemtype()))
							{
								if(tempItem.getDecimalwidth()!=scale) {
                                    alertColumn(tableName,tempItem,dbw,dao);
                                }
							}
							if(!"N".equals(tempItem.getItemtype()))
							{
								if("A".equals(tempItem.getItemtype())) {
                                    alertColumn(tableName,tempItem,dbw,dao);
}

                                            }
                                            break;
                                    }
}
}


}
catch(Exception e)
{
			e.printStackTrace();
		}
	}





	public void alertColumn(String tableName,FieldItem _item,DbWizard dbw,ContentDAO dao)
{
try
{
			FieldItem item=(FieldItem)_item.cloneItem();
			Table table=new Table(tableName);
     String item_id=item.getItemid();
     item.setItemid(item_id+"_x");
			 //TableModel tm=new TableModel(tableName);

			 RowSet rowSet=dao.search("select * from "+tableName+" where 1=2");
			 ResultSetMetaData data=rowSet.getMetaData();
			 HashMap columnMap=new HashMap();
			 for(int i=1;i<=data.getColumnCount();i++) {
                 columnMap.put(data.getColumnName(i).toLowerCase(),"1");
}

// if(!dbw.isExistField(tableName, item_id+"_x"))
			 if(columnMap.get(item_id.toLowerCase()+"_x")==null)
      {
          table.addField(item.cloneField());
          dbw.addColumns(table);
			 }

			 if("N".equalsIgnoreCase(item.getItemtype()))
      {
          int dicimal=item.getDecimalwidth();
          dao.update("update "+tableName+" set "+item_id+"_x=ROUND("+item_id+","+dicimal+")");
}
if("A".equalsIgnoreCase(item.getItemtype()))
			 {
				 int length=item.getItemlength();
				 dao.update("update "+tableName+" set "+item_id+"_x=substr(to_char("+item_id+"),0,"+length+")");
          }
          table.clear();

			 item.setItemid(item_id);
			 table.addField(item.cloneField());
      dbw.dropColumns(table);
      dbw.addColumns(table);

      dao.update("update "+tableName+" set "+item_id+"="+item_id+"_x");
table.clear();
item.setItemid(item_id+"_x");
table.addField(item.cloneField());
			 dbw.dropColumns(table);
			 item.setItemid(item_id);
      if(rowSet!=null) {
rowSet.close();
             }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}













/**
* 从系统邮件服务器设置中得到发送邮件的地址
* @return
	 */
	public String getFromAddr() throws GeneralException
	{
		String str = "";
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
     if(stmp_vo==null) {
            return "";
        }
        String param=stmp_vo.getString("str_value");
if(param==null|| "".equals(param)) {
            return "";
        }
        try
        {
	        Document doc = PubFunc.generateDom(param);;
	        Element root = doc.getRootElement();
	        Element stmp=root.getChild("stmp");
	        str=stmp.getAttributeValue("from_addr");
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);
        }
        return str;
	}


	/**
	 * 取得驳回对象列表
	 * @param taskid
	 * @param dao
	 * @param ins_id
	 * @return
	 */
	public ArrayList getRejectObjList(String taskid,ContentDAO dao,String ins_id,int flag)
{
ArrayList list=new ArrayList();
		try
		{
			if(taskid!=null&&!"0".equals(taskid))
			{
				String p_pri_task_id="";
				RowSet rowSet=null;
				if(flag==0)
				{
					RecordVo task_vo=new RecordVo("t_wf_task");
					task_vo.setInt("task_id",Integer.parseInt(taskid));
					task_vo=dao.findByPrimaryKey(task_vo);
                String pri_task_id=String.valueOf(task_vo.getInt("pri_task_id"));
				//	RowSet rowSet=dao.search("select role_id,seqnum,pri_task_id from t_wf_task_datalink where ins_id="+ins_id+" and task_id="+pri_task_id);
        rowSet=dao.search("select td.role_id,td.seqnum,tt.pri_task_id from t_wf_task_datalink td,t_wf_task tt where td.task_id=tt.task_id and tt.task_id="+pri_task_id+" and td.ins_id="+ins_id+" and td.task_id="+pri_task_id);


					if(rowSet.next())
					{
						p_pri_task_id=rowSet.getString("pri_task_id")!=null?rowSet.getString("pri_task_id"):"";
					}
				}
				else {
                    p_pri_task_id=taskid;
}
if(p_pri_task_id.length()>0)
				{
						rowSet=dao.search("select distinct actorid,actorname,actor_type,node_id from t_wf_task where ins_id="+ins_id+" and pri_task_id="+p_pri_task_id);
						LazyDynaBean a_bean=null;
						while(rowSet.next())
						{
							a_bean=new LazyDynaBean();
							String actorid=rowSet.getString("actorid");
							String actorname=rowSet.getString("actorname")!=null?rowSet.getString("actorname"):"";
							String actor_type=rowSet.getString("actor_type")!=null?rowSet.getString("actor_type"):"";
							String nodeid=rowSet.getString("node_id")!=null?rowSet.getString("node_id"):"";
							ArrayList objList=new ArrayList();
							if("1".equals(actor_type))
							{
								if(actorid.length()>3)
								{
									a_bean.set("a0100",actorid.substring(3));
									a_bean.set("email","");
									a_bean.set("phone","");
									a_bean.set("a0101",actorname);
									a_bean.set("dbname",actorid.substring(0,3));
									a_bean.set("actor_type",actor_type);

									LazyDynaBean _bean=new LazyDynaBean();
									_bean.set("a0100",actorid);
									_bean.set("email","");
									_bean.set("phone","");
									_bean.set("status","1");
									objList.add(_bean);
									a_bean.set("objList",objList);

									list.add(a_bean);
									this.isKhRelationData=true;
								}
							}
							if("4".equals(actor_type))
							{
								a_bean.set("actorid",actorid);

								a_bean.set("actorname",actorname);
								a_bean.set("nodeid",nodeid);
								a_bean.set("actortype",actor_type);
								RecordVo vo=new RecordVo("operuser");

								vo.setString("username",actorid);
								vo=dao.findByPrimaryKey(vo);
								LazyDynaBean _bean=new LazyDynaBean();
								_bean.set("email",vo.getString("email"));
								_bean.set("phone",vo.getString("phone"));
								_bean.set("status","0");
								_bean.set("a0100",actorid);
								objList.add(_bean);
								a_bean.set("objList",objList);

								list.add(a_bean);
								this.isKhRelationData=false;
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
		return list;
	}






	/**
	 * 插入每个任务处理的人员记录
	 * @param sql
	 * @param tabid
	 */
	public  void insertTaskRecords2(String sql,int task_id)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);

			RowSet rowSet=dao.search(sql);
			RowSet rowSet2=null;
			ArrayList recordList=new ArrayList();
			int node_id=0;
			while(rowSet.next())
			{
					 RecordVo taskvo=new RecordVo("t_wf_task_objlink");

                      if(node_id==0)
					 {
						 rowSet2=dao.search("select node_id from t_wf_task where task_id="+rowSet.getInt("task_id"));
						 if(rowSet2.next()) {
                             node_id=rowSet2.getInt("node_id");
                         }
					 }
					 taskvo.setString("seqnum",rowSet.getString("seqnum"));
					 taskvo.setInt("ins_id", rowSet.getInt("ins_id"));
					 taskvo.setInt("task_id", task_id);
					 taskvo.setInt("tab_id",this.tabid);
					 taskvo.setInt("node_id",node_id);
					 recordList.add(taskvo);
			}
			dao.addValueObject(recordList);
			if(rowSet!=null) {
                rowSet.close();
            }
			if(rowSet2!=null) {
                rowSet2.close();
            }

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}







	/**
	 * 插入每个任务处理的人员记录
	 * @param sql
	 * @param tabid
	 */
	public  void insertTaskRecords(String sql,LazyDynaBean recordBean)
	{
		try
		{
			/*
			ContentDAO dao=new ContentDAO(this.conn);
			if(recordBean==null)
			{
				RowSet rowSet=dao.search(sql);
				RowSet rowSet2=null;
				ArrayList recordList=new ArrayList();
				int node_id=0;
				while(rowSet.next())
				{
					 RecordVo taskvo=new RecordVo("t_wf_task_objlink");

					 if(node_id==0)
					 {
						 rowSet2=dao.search("select node_id from t_wf_task where task_id="+rowSet.getInt("task_id"));
						 if(rowSet2.next())
							 node_id=rowSet2.getInt("node_id");
					 }
					 taskvo.setString("seqnum",rowSet.getString("seqnum"));
					 taskvo.setInt("ins_id", rowSet.getInt("ins_id"));
					 taskvo.setInt("task_id", rowSet.getInt("task_id"));
					 taskvo.setInt("tab_id",this.tabid);
					 taskvo.setInt("node_id",node_id);
					 recordList.add(taskvo);
				}
				dao.addValueObject(recordList);
				if(rowSet!=null)
					rowSet.close();
				if(rowSet2!=null)
					rowSet2.close();
			}
			else
			{
				 RecordVo taskvo=new RecordVo("t_wf_task_objlink");
				 taskvo.setString("seqnum",(String)recordBean.get("seqnum"));
				 taskvo.setInt("node_id", Integer.parseInt((String)recordBean.get("ins_id")));
				 taskvo.setInt("ins_id", Integer.parseInt((String)recordBean.get("ins_id")));
				 taskvo.setInt("task_id", Integer.parseInt((String)recordBean.get("task_id")));
				 taskvo.setInt("tab_id",Integer.parseInt((String)recordBean.get("tab_id")));
				 dao.addValueObject(taskvo);
			}

			*/

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}



	/**
	 * 如果角色属性为汇报关系 则按单节点多个审批人的逻辑处理
	 * @specialOperate  0：按业务处理人员的考核关系 1：按单据中人员的考核关系
	 */
	private int _node_id=-1;
	private String _roleid="";
	private boolean isKhRelationData=true;  //是否是考核关系里的数据
	public boolean executeSingleNodeMutipleApprover(int src_taskid,int ins_id,int role_property,String objs_sql,String a_actorid,String aim_objs,String specialOperate)
	{
		boolean flag=true;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			LazyDynaBean a_bean=null;
			RecordVo taskvo=new RecordVo("t_wf_task");
			taskvo.setInt("task_id", src_taskid);
			taskvo=dao.findByPrimaryKey(taskvo);
			int node_id=taskvo.getInt("node_id");

			 EMailBo bo=null;
			 HashMap actorMap=new HashMap();

			 String template_sp=this.getTemplate_sp();    ////业务办理人员的邮件模板
			 String title=this.getTable_vo().getString("name");
			 String context=this.getTable_vo().getString("name")+this.getRecordBusiTopic("");
			 String template_emailAddress=ConstantParamter.getEmailField().toLowerCase();
			 String template__set="A01";
			 String fromaddr=getFromAddr();
			 SendMessageBo sendBo=new SendMessageBo(this.conn,this.userview);
			 if(isBemail())
			 {
				 try
				 {
					 bo = new EMailBo(this.conn,true,"");
				 }
				 catch(Exception e)
				 {

				 }

				 if(aim_objs==null||aim_objs.trim().length()==0) //非驳回
				 {
					if(template_sp!=null&&template_sp.trim().length()>0)
					{

						RowSet rowSet=dao.search("select * from email_name where id="+template_sp);
						String address="";
						if(rowSet.next())
						{
							context=Sql_switcher.readMemo(rowSet,"content");

							address=rowSet.getString("address")!=null?rowSet.getString("address"):"";
							if(address.trim().length()>0)
							{
								String[] temps=address.split(":");
								template_emailAddress=temps[0];
								FieldItem item=DataDictionary.getFieldItem((template_emailAddress).toLowerCase());
								if(item!=null) {
                                    template__set=item.getFieldsetid();
                                }
							}


						}
					//	bo.setEmail_field(address.split(":")[0].trim());
						PubFunc.closeDbObj(rowSet);
					}
				 }
				 sendBo.setTemplate__set(template__set);
				 sendBo.setTemplate_emailAddress(template_emailAddress);

			 }



			RowSet rowSet=dao.search(objs_sql);
			RecordVo vo=new RecordVo("templet_"+this.tabid);

		    ArrayList approverList=new ArrayList();
			if("0".equals(specialOperate)&&(aim_objs==null||aim_objs.trim().length()==0))
			{
				if(this.userview.getA0100()!=null&&this.userview.getA0100().trim().length()>0)
				{
					_node_id=node_id;
					_roleid=a_actorid;
					approverList=getObjectApprovers(this.userview.getA0100(),this.userview.getDbname(),role_property);
				}
			}
			if(aim_objs!=null&&aim_objs.trim().length()>0)  //驳回
			{
				RowSet rowSet2=dao.search("select pri_task_id from t_wf_task where task_id="+src_taskid+" and ins_id="+ins_id);
				if(rowSet2.next())
				{
					if(rowSet2.getString(1)!=null&&rowSet2.getString(1).length()>0)
					{
						_node_id=node_id;
						_roleid=a_actorid;
						approverList=getRejectObjList(rowSet2.getString(1),dao,String.valueOf(ins_id),1);
					}
				//	if(approverList.size()==1&&!((String)((LazyDynaBean)approverList.get(0)).get("actor_type")).equals("1"))
				//		return flag;
				}
				PubFunc.closeDbObj(rowSet2);
			}

			while(rowSet.next())
			{
				if("0".equals(specialOperate)&&(aim_objs==null||aim_objs.trim().length()==0)&&approverList.size()==0) {
                    break;
                }


				if("1".equals(specialOperate)&&(aim_objs==null||aim_objs.trim().length()==0))
				{
					_node_id=node_id;
					_roleid=a_actorid;
					this.isKhRelationData=true;
					approverList=getObjectApprovers(rowSet.getString("a0100"),rowSet.getString("basepre"),role_property);
				}

			    String seqnum=rowSet.getString("seqnum")!=null?rowSet.getString("seqnum"):"";
			    String a_a0101=rowSet.getString("a0101_1");
			    int operationtype=getOperationtype();
				if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
               {
       			a_a0101=rowSet.getString("a0101_2");;
                  }
			    if(seqnum.length()>0)
			    {

			    	sendBo.setTemplate__set(template__set);
			    	sendBo.setTemplate_emailAddress(template_emailAddress);


			    	String task_id_pro_context=taskvo.getString("task_id_pro");
                       if(task_id_pro_context==null||task_id_pro_context.trim().length()==0) {
                        task_id_pro_context="";
                    }
			    	StringBuffer taskids=new StringBuffer("");
				    for(int j=0;j<approverList.size();j++)
					{
				    	if(this.isKhRelationData) //如果是特殊角色
				    	{
					    	a_bean=(LazyDynaBean)approverList.get(j);
					    	String a0100=(String)a_bean.get("a0100");
					    	String dbname=(String)a_bean.get("dbname");
					    	String a0101=(String)a_bean.get("a0101");

					    	if(aim_objs!=null&&aim_objs.trim().length()>0&&!"undefined".equalsIgnoreCase(aim_objs)&&!"self".equalsIgnoreCase(aim_objs))
					    	{
					    		if(aim_objs.toLowerCase().indexOf(a0100+"@"+dbname.toLowerCase())==-1) {
                                    continue;
                                }

					    	}

					    	IDGenerator idg=new IDGenerator(2,this.conn);
					    	int task_id= Integer.parseInt(idg.getId("wf_task.task_id"));
				            taskvo.setInt("task_id",task_id);
				            taskvo.setString("actor_type", "1");
				            taskvo.setString("actorid", dbname+a0100);
				            taskvo.setString("actorname", a0101);
				            taskvo.setInt("node_id", _node_id);
				            sendBo.setIns_id(String.valueOf(ins_id));
				            sendBo.setTask_id(String.valueOf(task_id));
				            sendBo.setSp_flag("1");

				            taskids.append(","+task_id);

					    	RecordVo t_wf_task_datalink_vo=new RecordVo("t_wf_task_datalink");
				            t_wf_task_datalink_vo.setInt("ins_id", ins_id);
				            t_wf_task_datalink_vo.setInt("node_id", _node_id);
				            t_wf_task_datalink_vo.setInt("task_id",task_id);
				            t_wf_task_datalink_vo.setString("seqnum", seqnum);
				            t_wf_task_datalink_vo.setInt("state", 0);
				    		t_wf_task_datalink_vo.setString("role_id",_roleid);
				            taskvo.setString("task_topic",getTable_vo().getString("name")+"("+a_a0101+",共1人)");

				            dao.addValueObject(taskvo);
				            dao.addValueObject(t_wf_task_datalink_vo);
				            dao.update(" update templet_"+this.tabid+" set task_id="+task_id+" where ins_id="+ins_id+" and a0100='"+rowSet.getString("a0100")+"' and seqnum='"+rowSet.getString("seqnum")+"' and upper(basepre)='"+rowSet.getString("basepre").toUpperCase()+"'");

				            /**
							 * 记录任务处理的人员 20100504
							 */
				            LazyDynaBean _bean=new LazyDynaBean();
				            _bean.set("seqnum", seqnum);
				            _bean.set("node_id", String.valueOf(_node_id));
				            _bean.set("ins_id", String.valueOf(ins_id));
				            _bean.set("task_id",String.valueOf(task_id));
				            _bean.set("tab_id", String.valueOf(this.tabid));
				            insertTaskRecords("",_bean);


				         //   普天代办
             //	if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
							{
								if(a0100!=null&&a0100.trim().length()>0)
								{
									MessageToOtherSys sysBo=new MessageToOtherSys(this.conn,this.userview);
									String pre_pendingID="";
									if(this.otherParaMap.get("pre_pendingID")!=null) {
                                        pre_pendingID=(String)this.otherParaMap.get("pre_pendingID");
                                    }
									sysBo.sendDealWithInfo(pre_pendingID, 1,String.valueOf(tabid), ins_id,String.valueOf(task_id), dbname, a0100);

								}
							}


							try
					    	{
						    	if(isBemail()&&bo!=null)
						    	{


							    		String toaddr=""; //bo.getEmailAddrByA0100(dbname+a0100);
							    		if(sendBo.getTemplate_emailAddress()!=null&&sendBo.getTemplate_emailAddress().trim().length()>0)
										{
											toaddr=sendBo.getEmailAddress(dbname,a0100);
										}
										else {
                                            toaddr=bo.getEmailAddrByA0100(dbname+a0100);
                                        }

										if(toaddr!=null)
										{
											String _context=context;
                                                        String _title=title;
if(aim_objs==null||aim_objs.trim().length()==0)
											{
												LazyDynaBean abean=sendBo.getEmailBean("1",rowSet,title,context,dbname+a0100,this.userview,String.valueOf(tabid),"");
												_title=(String)abean.get("title");
												_context=(String)abean.get("context");
											}
											else
											{
												_title=title+"(驳回)";
												_context=title+"(驳回)"+this.reject_cause;
											}
											bo.sendEmail(_title,_context,"",fromaddr,toaddr);
										}



						    	}


					    	}
					    	catch(Exception e)
					    	{

					    	}
				    	}
				    	else
				    	{
				    		 sendBo.setIns_id(String.valueOf(ins_id));
				    		 processApproveObject_nokh(rowSet,sendBo,approverList,taskvo,a_a0101,bo,context,title,aim_objs,taskids);
				    	}


					  }

				      if(taskids.length()>0) {
                          dao.update("update t_wf_task set task_id_pro='"+taskids.toString()+task_id_pro_context+"' where task_id in ( "+taskids.substring(1)+" )");
                      }

			    }
			}
			if("0".equals(specialOperate)&&(aim_objs==null||aim_objs.trim().length()==0)&&approverList.size()==0)
			{

			}
			else if(approverList.size()>0) {
                dao.update("delete from t_wf_task where task_id="+src_taskid);
            }

			if(approverList.size()==0) {
                flag=false;
            }
			PubFunc.closeDbObj(rowSet);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 跳级报批 非考核关系的审批人
	 * @param rowSet
	 * @param sendBo
	 * @param approverList
	 */
	public void processApproveObject_nokh(RowSet rowSet,SendMessageBo sendBo,ArrayList approverList,RecordVo taskvo,String a_a0101,EMailBo bo,String context,String title,String aim_objs,StringBuffer taskids)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);

			LazyDynaBean a_bean=(LazyDynaBean)approverList.get(0);
	    	String actorid=(String)a_bean.get("actorid");
	    	String actortype=(String)a_bean.get("actortype");
	    	String nodeid=(String)a_bean.get("nodeid");
	    	String actorname=(String)a_bean.get("actorname");
	    	ArrayList objList=(ArrayList)a_bean.get("objList");

	    	IDGenerator idg=new IDGenerator(2,this.conn);
	    	int task_id= Integer.parseInt(idg.getId("wf_task.task_id"));
            taskvo.setInt("task_id",task_id);
            taskvo.setString("actor_type", actortype);
            taskvo.setString("actorid",actorid);
            taskvo.setString("actorname", actorname);
	    	taskvo.setInt("node_id", Integer.parseInt(nodeid));
        //    sendBo.setIns_id(String.valueOf(ins_id));
            sendBo.setTask_id(String.valueOf(task_id));
            sendBo.setSp_flag("1");

            taskids.append(","+task_id);



            String fromaddr=sendBo.getFromAddr();
            taskvo.setString("task_topic",getTable_vo().getString("name")+"("+a_a0101+",共1人)");
            dao.addValueObject(taskvo);

            //修改 templet_XXX表中 task_id值为最新
            String _a0100=rowSet.getString("a0100");
            String _basepre=rowSet.getString("basepre");
            int    _ins_id=rowSet.getInt("ins_id");
            dao.update("update templet_"+this.tabid+" set task_id="+task_id+" where a0100='"+_a0100+"' and lower(basepre)='"+_basepre.toLowerCase()+"' and ins_id="+_ins_id);

            String seqnum=rowSet.getString("seqnum")!=null?rowSet.getString("seqnum"):"";
            /**
			 * 记录任务处理的人员 20100504
			 */
            LazyDynaBean bean=new LazyDynaBean();
            bean.set("seqnum",seqnum);
bean.set("node_id", nodeid);
            bean.set("ins_id", String.valueOf(_ins_id));
            bean.set("task_id",String.valueOf(task_id));
            bean.set("tab_id", String.valueOf(this.tabid));
            insertTaskRecords("",bean);



            try
	    	{
		    	if(isBemail()&&bo!=null&&objList!=null)
		    	{
		    		LazyDynaBean _bean=null;
		    		for(int i=0;i<objList.size();i++)
		    		{
		    			_bean=(LazyDynaBean)objList.get(i);
		    			String status=(String)_bean.get("status");
		    			String a0100=(String)_bean.get("a0100");
		    			String email=(String)_bean.get("email");

		    			String toaddr="";
		    			if("0".equals(status))
		    			{
		    				toaddr=email;
		    			}
		    			else
		    			{
			    			if(sendBo.getTemplate_emailAddress()!=null&&sendBo.getTemplate_emailAddress().trim().length()>0)
							{
								toaddr=sendBo.getEmailAddress(a0100.substring(0,3),a0100.substring(3));
							}
							else {
                                toaddr=bo.getEmailAddrByA0100(a0100);
                            }
		    			}
						if(toaddr!=null)
						{
							String _context=context;
							String _title=title;
							if(aim_objs==null||aim_objs.trim().length()==0)
							{
								LazyDynaBean abean=sendBo.getEmailBean("1",rowSet,title,context,a0100,this.userview,String.valueOf(tabid),"");
								_title=(String)abean.get("title");
								_context=(String)abean.get("context");
							}
							else
							{
								_title=title+"(驳回)";
								_context=title+"(驳回)"+this.reject_cause;
							}
							bo.sendEmail(_title,_context,"",fromaddr,toaddr);
						}

		    		}

		    	}


	    	}
	    	catch(Exception e)
	    	{

	    	}


		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	//根据 ins_id  ,task_id 取得 正在处理的人员
	public LazyDynaBean getPerson2(String ins_id,String task_id,String tableName)
	{
		LazyDynaBean abean=null;
		try
		{
			String sql="select * from "+tableName+" where ins_id="+ins_id+"  and task_id="+task_id;
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("a0100", rowSet.getString("a0100"));
				abean.set("basepre", rowSet.getString("basepre"));
			}
			PubFunc.closeDbObj(rowSet);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}

	//根据 ins_id  ,task_id 取得 正在处理的人员
	public LazyDynaBean getPerson(String ins_id,String task_id,String tableName)
	{
		LazyDynaBean abean=null;
		try
		{
			String sql="select a0100,basepre   from "+tableName+" where seqnum in (select seqnum from ";
			sql+=" t_wf_task_datalink where ins_id="+ins_id+" and task_id="+task_id+" ) and ins_id="+ins_id;
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("a0100", rowSet.getString("a0100"));
				abean.set("basepre", rowSet.getString("basepre"));
			}
			PubFunc.closeDbObj(rowSet);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}


	/**
	 * 根据考核关系查找考核主体
	 * @param a0100
	 * @param dbname
	 * @param role_property
	 * @return
	 */
	public ArrayList get_Object_Approvers(String a0100,String dbname,int role_property)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="";
			RowSet rowSet=null;
			if(role_property==14)
			{
				rowSet=dao.search("select * from "+dbname+"a01 where a0100='"+a0100+"'");
				if(rowSet.next())
				{
					LazyDynaBean a_bean=new LazyDynaBean();
					a_bean.set("a0100",a0100);
					a_bean.set("email","");
					a_bean.set("phone","");
					a_bean.set("a0101",rowSet.getString("a0101"));
					a_bean.set("dbname",dbname);
					list.add(a_bean);
				}
			}
			else
			{


				StringBuffer level_str=new StringBuffer("");
				if(role_property==9) {
                    level_str.append(",1");
                } else if(role_property==10) {
                    level_str.append(",0");
                } else if(role_property==11) {
                    level_str.append(",-1");
                } else if(role_property==12) {
                    level_str.append(",-2");
                } else if(role_property==13) {
                    level_str.append(",1,0,-1,-2");
                }
				sql="select pmb.*  from per_mainbody_std pmb,per_mainbodyset pmbs "
						+" where pmb.body_id=pmbs.body_id  and object_id='"+a0100+"' and ";
				if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                    sql+=" level_o";
                } else {
                    sql+=" level ";
                }
				sql+=" in ("+level_str.substring(1)+")";
				rowSet=dao.search(sql);
				while(rowSet.next())
				{
						String mainbody_id=rowSet.getString("mainbody_id");
						LazyDynaBean a_bean=new LazyDynaBean();
						a_bean.set("a0100",mainbody_id);
						a_bean.set("email","");
						a_bean.set("phone","");
						a_bean.set("a0101",rowSet.getString("a0101"));
						a_bean.set("dbname","Usr");
						list.add(a_bean);
				}

			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}



	/**
	 * 按照考核关系取得对象的主体(可跳级)
	 * @param a0100
	 * @param dbname
	 * @param role_property
	 * @return
	 */
	public ArrayList getObjectApprovers(String a0100,String dbname,int role_property)
	{

		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RowSet rowSet=null;

			if(role_property==14) //本人
			{
				rowSet=dao.search("select * from "+dbname+"A01 where a0100='"+a0100+"'");
				if(rowSet.next())
				{
					LazyDynaBean a_bean=new LazyDynaBean();
					a_bean.set("a0100",a0100);
					a_bean.set("email","");
					a_bean.set("phone","");
					a_bean.set("a0101",rowSet.getString("a0101"));
					a_bean.set("dbname",dbname);
					list.add(a_bean);
				}
			}
			else
			{
				while(true)
				{
					String sql="";

					StringBuffer level_str=new StringBuffer("");
					if(role_property==9) {
                        level_str.append(",1");
                    } else if(role_property==10) {
                        level_str.append(",0");
                    } else if(role_property==11) {
                        level_str.append(",-1");
                    } else if(role_property==12) {
                        level_str.append(",-2");
                    } else if(role_property==13) {
                        level_str.append(",1,0,-1,-2");
                    } else if(role_property==14)
                        {
                            rowSet=dao.search("select * from "+dbname+"A01 where a0100='"+a0100+"'");
                                    if(rowSet.next())
						{
							LazyDynaBean a_bean=new LazyDynaBean();
							a_bean.set("a0100",a0100);
							a_bean.set("email","");
                                            a_bean.set("phone","");
                                                            a_bean.set("a0101",rowSet.getString("a0101"));
							a_bean.set("dbname",dbname);
                                            list.add(a_bean);
						}
						break;
					}
					sql="select pmb.*  from per_mainbody_std pmb,per_mainbodyset pmbs "
							+" where pmb.body_id=pmbs.body_id  and object_id='"+a0100+"' and ";
					if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                        sql+=" level_o";
                    } else {
                        sql+=" level ";
                    }
					sql+=" in ("+level_str.substring(1)+")";
					rowSet=dao.search(sql);
					while(rowSet.next())
					{
							String mainbody_id=rowSet.getString("mainbody_id");
							LazyDynaBean a_bean=new LazyDynaBean();
							a_bean.set("a0100",mainbody_id);
							a_bean.set("email","");
							a_bean.set("phone","");
							a_bean.set("a0101",rowSet.getString("a0101"));
							a_bean.set("dbname","Usr");
							list.add(a_bean);
					}

					if(list.size()>0) {
                        break;
                    } else
					{
						 sql="select t_wf_transition.*,t_wf_node.nodetype,t_wf_actor.actor_type,t_wf_actor.actorid,t_wf_actor.actorname  from t_wf_transition,t_wf_node,t_wf_actor ";
						 sql+=" where t_wf_transition.next_nodeid=t_wf_node.node_id and  t_wf_transition.next_nodeid=t_wf_actor.node_id  and t_wf_transition.tabid="+this.tabid+" and t_wf_transition.pre_nodeid="+this._node_id;
						 rowSet=dao.search(sql);
						 if(rowSet.next())
						 {
							 String nodetype=rowSet.getString("nodetype");
							 if("9".equals(nodetype)) {
                                 break;
                             } else
							 {
								WF_Actor actor=new  WF_Actor(rowSet.getString("actorid"),rowSet.getString("actor_type"));
								String actor_type=rowSet.getString("actor_type");
								int next_nodeid=rowSet.getInt("next_nodeid");
								int _role_property=0;
								if("2".equals(actor_type)) {
                                    _role_property=actor.decideIsKhRelation(actor.getActorid(),actor.getActortype(),this.conn);
                                }

								if("2".equals(actor_type)&&_role_property!=0)
								{
									role_property=_role_property;
									this._node_id=next_nodeid;
								}
								else
								{
									this._node_id=next_nodeid;
									isKhRelationData=false;
									String actorid=rowSet.getString("actorid");
									LazyDynaBean a_bean=new LazyDynaBean();
									a_bean.set("actorid",actorid);
									a_bean.set("actorname",rowSet.getString("actorname"));
									a_bean.set("actortype",actor_type);
									a_bean.set("nodeid",String.valueOf(next_nodeid));


									String dbase="";
									ArrayList objList=new ArrayList();
									if("1".equals(actor_type))
									{
										if(actorid.length()>3)
										{
											dbase=actorid.substring(0,3);
											a0100=actorid.substring(3);

											LazyDynaBean _bean=new LazyDynaBean();
											_bean.set("a0100",actorid);
											_bean.set("email","");
											_bean.set("phone","");
											_bean.set("status","1");
                                                                objList.add(_bean);
										}
									}
									/**operuser中的用户*/
									if("4".equals(actor.getActortype()))
									{
										RecordVo vo=new RecordVo("operuser");

										vo.setString("username",actorid);
										vo=dao.findByPrimaryKey(vo);
										dbase=vo.getString("nbase");
										a0100=vo.getString("a0100");

										LazyDynaBean _bean=new LazyDynaBean();
										a_bean.set("email","");
										a_bean.set("phone","");
										if(a0100==null|| "".equalsIgnoreCase(a0100))
										{

											_bean.set("email",vo.getString("email"));
											_bean.set("phone",vo.getString("phone"));
                                                                    _bean.set("status","0");
                                                            		_bean.set("a0100",actorid);
										}
										else
                                                                {
                                                                    _bean.set("status","1");
                                                  	_bean.set("a0100",dbase+a0100);
                                                                          }
                                                                          objList.add(_bean);
                                                                   }
                                                                                   if("2".equals(actor.getActortype()))//角色
                			{
										RowSet rowSet2=dao.search("select * from t_sys_role where role_id='"+actorid+"'");
                                                            int roleproperty=0;
                                                            if(rowSet2.next()) {
                 roleproperty=rowSet2.getInt("role_property");
                                        }
                                                            if(!(roleproperty==9||roleproperty==10||roleproperty==11||roleproperty==12||roleproperty==13))
                                                                        		{

                                                                                                        SendMessageBo sendBo=new SendMessageBo(this.conn,this.userview);
                                                                                                                                ArrayList alist=sendBo.findUserListByRoleId(actorid,"","");
                                                      LazyDynaBean abean=null;
											for(int i=0;i<alist.size();i++)
											{
                                                                        LazyDynaBean _bean=new LazyDynaBean();
                                                                        abean=(LazyDynaBean)alist.get(i);
                        												String type=(String)abean.get("type");   // 1:业务用户  2：自助用户
												_bean.set("a0100",(String)abean.get("a0100"));
												_bean.set("email",(String)abean.get("email"));
												_bean.set("phone",(String)abean.get("phone"));
												String status=(String)abean.get("status");
												if("1".equals(type)) {
                                                    _bean.set("status","0");
                                                } else {
                                                    _bean.set("status","1");
                                                }
												objList.add(_bean);
											}
										}
									}
									a_bean.set("objList",objList);
                                                list.add(a_bean);
									break;
								}
							 }
                              }
						 else
						 {

							 break;
						 }

					}
				}

			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
{
e.printStackTrace();
		}
		return list;
	}




	/**
	 * 创建业务审批表和员工申请临时表
	 * @return
	 */
	public boolean createTempTemplateTable()
	{
		boolean bflag=true;
		String tablename="g_templet_"+this.tabid; //
		try
		{
			DbSecurityImpl dbS = new DbSecurityImpl();
			Table table=new Table(tablename);
			DbWizard dbwizard=new DbWizard(this.conn);
			ArrayList fieldList=getAllFieldItem();
			if(!dbwizard.isExistTable(tablename,false))
			{
				/**取得模板需要生成字段的表结构*/
				addFieldItem(table,0);
				dbwizard.createTable(table);
				dbS.encryptTableName(this.conn, tablename);

			}
			else
			{
				updateTempTemplateStruct(table,0,fieldList);
				syncGzField(tablename);
			}
			if(!dbwizard.isExistTable("templet_"+this.tabid,false))
			{
				Table table_wf=new Table("templet_"+this.tabid);
				addFieldItem(table_wf,1);
				dbwizard.createTable(table_wf);
				dbS.encryptTableName(this.conn, tablename);
			}
			else
			{

				Table table_wf=new Table("templet_"+this.tabid);
				updateTempTemplateStruct(table_wf,1,fieldList);
				syncGzField("templet_"+this.tabid);
			}
			/**重新加载数据模型*/
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tablename);
			dbmodel.reloadTableModel("templet_"+this.tabid);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
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
						Entry entry = (Entry) it.next();
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
						Entry entry = (Entry) it.next();
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
	//获取模版上全部指标（电脑页和手机页）
	public ArrayList getAllFieldItems()throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			ArrayList pagelist=null;

			pagelist=getAllTemplatePage();
			this.sub_domain_map =null;
			this.field_name_map = null;
			for(int i=0;i<pagelist.size();i++)
			{
				TemplatePageBo pagebo=(TemplatePageBo)pagelist.get(i);
				//bug 39943 没有区分手机页还是pc页，导致必填项判断错误。
				list.addAll(pagebo.getAllFieldItem());
if(this.sub_domain_map!=null){
					int n = this.sub_domain_map.size();
    if(n>0) {
             n = n/2;
                    }
				if(pagebo.getSub_domain_map()!=null){
				Iterator it = pagebo.getSub_domain_map().entrySet().iterator();
				while (it.hasNext()) {
					Entry entry = (Entry) it.next();
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
					Entry entry = (Entry) it.next();
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
catch(Exception ex)
{
ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	/**
	 * 求模板中定义所有的指标(变化前及变化后)及变量的列表
	 * @return 列表中存放的是FieldItem对象
	 */
	public ArrayList getAllFieldItem()throws GeneralException
{
ArrayList list=new ArrayList();
		try
		{
			ArrayList pagelist=null;

pagelist=getAllTemplatePage();
			this.sub_domain_map =null;
this.field_name_map = null;
for(int i=0;i<pagelist.size();i++)
			{
				TemplatePageBo pagebo=(TemplatePageBo)pagelist.get(i);
				//bug 39943 没有区分手机页还是pc页，导致必填项判断错误。
				if(("-1".equalsIgnoreCase(this.pcOrMobile))||pagebo.getIsMobile().equalsIgnoreCase(this.pcOrMobile)){
					list.addAll(pagebo.getAllFieldItem());
				}else{
					continue;
				}
				if(this.sub_domain_map!=null){
					int n = this.sub_domain_map.size();
					if(n>0) {
                        n = n/2;
                    }
				if(pagebo.getSub_domain_map()!=null){
    Iterator it = pagebo.getSub_domain_map().entrySet().iterator();
				while (it.hasNext()) {
					Entry entry = (Entry) it.next();
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
					Entry entry = (Entry) it.next();
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
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	//获取所有页的数据包括隐藏页，否则归档时隐藏页上数据无法归档。
	public ArrayList getAllFieldItemIgnoreIsShow(String isMobile)throws GeneralException
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
					list.addAll(pagebo.getAllFieldItem());
					if(this.sub_domain_map!=null){
						int n = this.sub_domain_map.size();
						if(n>0) {
                            n = n/2;
                        }
					if(pagebo.getSub_domain_map()!=null){
					Iterator it = pagebo.getSub_domain_map().entrySet().iterator();
					while (it.hasNext()) {
						Entry entry = (Entry) it.next();
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
						Entry entry = (Entry) it.next();
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
	 * 不包括A0100,basepre,ins_id,state等字段
	 * 取得当前页中所有字段
	 * @param pageno
	 * @return
	 */
	public ArrayList getAllFieldItemByPage(int pageno)
	{
		ArrayList list=new ArrayList();
		try
		{
			ArrayList pagelist=getAllTemplatePage();
			for(int i=0;i<pagelist.size();i++)
			{
				TemplatePageBo pagebo=(TemplatePageBo)pagelist.get(i);
				if(pagebo.getPageid()!=pageno) {
                    continue;
                }
				list.addAll(pagebo.getAllFieldItem());
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
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

                TemplatePageBo pagebo = new TemplatePageBo(this.conn, this.tabid, rset.getInt("pageid"), task_id,this.userview);
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
	 * 查询当前表中页对象列表
	 * @param flag =0 =1 ,其它值为全部页
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getAllTemplatePage(int flag)throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer sql=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		try
		{
			sql.append("select * from Template_Page where tabid=");
			sql.append(this.tabid);
			switch(flag)
			{
			case 0:
				sql.append(" and isprn=0");
				break;
			case 1:
				sql.append(" and isprn<>0");
				break;
			}
			rset=dao.search(sql.toString());
			String task_id = "0";
			if(this.getTasklist().size()>0) {
                task_id = ""+this.getTasklist().get(0);
            }
			while(rset.next())
			{
				TemplatePageBo pagebo=new TemplatePageBo(this.conn,this.tabid,rset.getInt("pageid"),task_id);
				pagebo.setTitle(rset.getString("title"));
				if(rset.getInt("isprn")==0) {
                    pagebo.setIsprint(false);
                        } else {
                    pagebo.setIsprint(true);
                }
				list.add(pagebo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeDbObj(rset);
		}
		return list;
	}

	/**
	 * 根据单位性质和业务类型，取得所有的模板列表
	 * @param unittype 单位性质  国家机关、事业单位、企业、军队或其它
	 * @param busitype 业务类型 ,工资，日常管理
	 * @return 返回是动态LazyDynaBean列表
	 */
	public ArrayList getAllTemplate(int unittype,int busitype)
	{
		ArrayList list=new ArrayList();
		StringBuffer strsql=new StringBuffer();
		try
		{
			strsql.append("select * from Template_table where flag=");
			strsql.append(unittype);
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				strsql.append(" and static_o=");
			}else {
				strsql.append(" and Static=");
			}
			strsql.append(busitype);
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(strsql.toString());
			list=dao.getDynaBeanList(rset);
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}

	public static void main(String[] args)
	{
	//	String sql="select templet_3.* from dfajd";
//	System.out.println(PubFunc.convertTo64Base("余鸿林,1"));
		 String aa="sdfffa";
		 System.out.println(aa.hashCode());
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
		/*
		StringBuffer strsql=new StringBuffer();
		strsql.append("select operationtype from operation where operationcode='");
		strsql.append(operationcode);
		strsql.append("'");
		ContentDAO dao=new ContentDAO(this.conn);
		int flag=-1;
		try
		{
			RowSet rset=dao.search(strsql.toString());
			if(rset.next())
				flag=rset.getInt("operationtype");
			PubFunc.closeDbObj(rset);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}*/
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
	 * 得到下通知条件表达式
	 * @return
	 */
	private ArrayList getConexprList()
	{
		ArrayList conexprlist=new ArrayList();
		if(this.msg_flag!=null&& "1".equals(this.msg_flag))
		{
			String condid="";
			String expr="";
			for(int i=0;i<this.mag_condlist.size();i++)
			{
				CommonData da=(CommonData)this.mag_condlist.get(i);
				CommonData tmp=new CommonData();
				condid=da.getDataName();
				expr=getConexpr(condid);
				tmp.setDataName(expr);
				tmp.setDataValue(da.getDataValue());
				conexprlist.add(tmp);
			}
		}


		return conexprlist;
	}
	/**
	 * 得到下通知条件表达式
	 * @param condid
	 * @return
	 */
	private String getConexpr(String condid)
	{
		String expr="";
		String sql="select condexpr from t_wf_cond where condid="+condid;
		try
		{
			RowSet rs=null;
			ContentDAO dao=new ContentDAO(this.conn);
			rs=dao.search(sql);
			if(rs.next()) {
                expr=Sql_switcher.readMemo(rs,"condexpr");
            }
			PubFunc.closeDbObj(rs);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return expr;
	}
	public String getctrl_para(String mode,String email,String sms,String flag) throws JDOMException{
		/*
		 * flag=0修改自定义表单审批模式 =1 修改固定表单审批模式
		 */
		Document doc=this.getDoc();
		Element element=null;
		XMLOutputter outputter = new XMLOutputter();
    Format format = Format.getPrettyFormat();
format.setEncoding("UTF-8");
outputter.setFormat(format);
		if(doc==null){
			if("0".equals(flag)){
				doc =createxmlctrl_para("0");
			}else{
				doc =createxmlctrl_para();
			}
		}
		String xpath="/params";
		XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
		element =(Element) findPath.selectSingleNode(doc);
		if(element!=null){
			Element sp_flagel=element.getChild("sp_flag");
			if(sp_flagel==null){
				sp_flagel=new Element("sp_flag");
				sp_flagel.setAttribute("mode","0");
				element.addContent(sp_flagel);
			}
			sp_flagel.getAttribute("mode").setValue(mode);

			Element notes=element.getChild("notes");
			if(notes==null){
				notes=new Element("notes");
				notes.setAttribute("email","false");
				notes.setAttribute("sms","false");
				element.addContent(notes);
			}
			notes.getAttribute("email").setValue(email);
			notes.getAttribute("sms").setValue(sms);
		}

		return outputter.outputString(doc);
	}

	public String getctrl_para_codediff(String sp_flag,String email,String sms,String flag,String fields,String code_leader) throws JDOMException{
		/*
		 * flag=0修改自定义表单审批模式 =1 修改固定表单审批模式
		 */
		Document doc=this.getDoc();
		Element element=null;
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		if(doc==null){
			if("0".equals(flag)){
				doc =createxmlctrl_para("0");
			}else{
				doc =createxmlctrl_para();
			}
		}
		String xpath="/params";
		XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
		element =(Element) findPath.selectSingleNode(doc);
		if(element!=null){
			Element split_data=element.getChild("split_data");
			if(split_data==null){
					if("0".equals(code_leader)){
						split_data=new Element("split_data");
						split_data.setAttribute("mode","groupfield");
						split_data.setAttribute("fields",fields);
						element.addContent(split_data);
					}else if("1".equals(code_leader)){
						split_data=new Element("split_data");
						split_data.setAttribute("mode","superior");
						split_data.setAttribute("fields","");
						element.addContent(split_data);
					}

			}else{
					if("0".equals(code_leader)){
						element.removeContent(split_data);
						split_data=new Element("split_data");
						split_data.setAttribute("mode","groupfield");
						split_data.setAttribute("fields",fields);
						element.addContent(split_data);
					}else if("1".equals(code_leader)){
						element.removeContent(split_data);
						split_data.setAttribute("mode","superior");
						split_data.setAttribute("fields","");
						element.addContent(split_data);
					}else if("-1".equals(code_leader)){
						element.removeChild("split_data");
					}
			}
		}

		return outputter.outputString(doc);
	}
	public String getctrl_para(String inputurl,String appurl) throws JDOMException{
		Document doc=this.createxmlctrl_para();
		Element element=null;
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		String xpath="/params";
		XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
		element =(Element) findPath.selectSingleNode(doc);
		if(element!=null){
			Element edit_form=element.getChild("edit_form");
			edit_form.getAttribute("mode").setValue(inputurl);
			Element appeal_form=element.getChild("appeal_form");
			appeal_form.getAttribute("appeal_form").setValue(appurl);

		}
		return outputter.outputString(doc);
	}
	private Document createxmlctrl_para(){

		Element params=new Element("params");
		Element notes =new Element("notes");
		notes.setAttribute("email","false");
		notes.setAttribute("sms","false");
		Element sp_flag=new Element("sp_flag");
		sp_flag.setAttribute("mode","0");
		Element edit_form=new Element("edit_form");
		edit_form.setAttribute("url","");
		Element appeal_form=new Element("appeal_form");
		appeal_form.setAttribute("url","");
		params.addContent(notes);
		params.addContent(sp_flag);
		params.addContent(edit_form);
		params.addContent(appeal_form);
		Document doc =new Document(params);
		return doc;
	}
	private Document createxmlctrl_para(String flag){

		Element params=new Element("params");
		Element notes =new Element("notes");
		notes.setAttribute("email","false");
		notes.setAttribute("sms","false");
		Element sp_flag=new Element("sp_flag");
		sp_flag.setAttribute("mode","0");
		params.addContent(notes);
		params.addContent(sp_flag);
		Document doc =new Document(params);
		return doc;
	}




	/**
	 * 原始表单归档
	 * @param sql
	 * @param flag 1:sutemplet_1  2:templet_1
	 * @param srctab
	 */
	public void subDataToArchive(String sql,String srctab,String flag)
	{
		FileInputStream fis = null;
		try
		{
			ArrayList list = new ArrayList();
			 if("-1".equals(this.pcOrMobile)) {
                 list=getAllFieldItem();
             } else {
                 list=getAllFieldItemIgnoreIsShow(this.pcOrMobile);
             }
			 createArchiveTable(list);  //创建模板归档表
			 ContentDAO dao=new ContentDAO(this.conn);
			 RowSet rowSet=dao.search(sql);
			 IDGenerator idg=new IDGenerator(2,this.conn);
             if (this.outPdfBo==null) {
                 this.outPdfBo=new TemplateTableOutBo(this.conn,this.tabid,this.userview);
             }
             TemplateTableOutBo tbo= this.outPdfBo;
			 tbo.setType("1");
			 RowSet rowSet2=null;
			 /**人事异动模板的归档数据都在template_archive表中存放，如果客户需要归档的表单有很多字段也很多就会导致template_archive表中字段超出数据库的自身限制（最多1024个字段）导致归档失败**/
			 /**现在在归档之前将不在数据字典中的字段，也就是对于数据无效的字段给drop掉**/
			 dropItemNotInDataDictionary();
			 /**drop字段 结束**/
			 while(rowSet.next())
			 {
				 ArrayList bigTextFieldList=new ArrayList();
				 RecordVo recordVo=new RecordVo("template_archive");
			     int id= Integer.parseInt(idg.getId("template_archive.id"));
			     String a0100="";
			     String basepre="";
			     if(this.infor_type==1){
			      a0100=rowSet.getString("a0100");
			      recordVo.setString("a0100",a0100);
			       basepre=rowSet.getString("basepre");
			       recordVo.setString("basepre", basepre);
			     }else if(this.infor_type==2){
			      a0100=rowSet.getString("b0110");
			      recordVo.setString("b0110",a0100);
			     }else if(this.infor_type==3){
			      a0100=rowSet.getString("e01a1");
			      recordVo.setString("e01a1",a0100);
			     }

				 recordVo.setInt("id", id);
				 recordVo.setInt("tabid",this.tabid);
				 recordVo.setString("chguser", this.userview.getUserName());
				 recordVo.setDate("lasttime",new Date());

				 if(this.infor_type==1){
				 try{
					 rowSet2=dao.search("select b0110,e0122,e01a1 from "+basepre+"A01 where a0100='"+a0100+"'");
					 if(rowSet2.next())
					 {
						 recordVo.setString("b0110_1", rowSet2.getString("b0110"));
						 recordVo.setString("e0122_1", rowSet2.getString("e0122"));
						 recordVo.setString("e01a1_1", rowSet2.getString("e01a1"));
					 }

				//	 recordVo.setString("b0110_1", rowSet.getString("b0110_1"));

				 }catch(Exception e){
					 ;
				 }
				 }
				 int state=rowSet.getInt("state");
				 if(state==1)
				 {
					 LazyDynaBean bean=getFromMessageBean(a0100,basepre,this.tabid);
					 if(bean!=null)
					 {
						 recordVo.setInt("from_tabid",Integer.parseInt((String)bean.get("tabid")));
						 recordVo.setInt("from_id",Integer.parseInt((String)bean.get("id")));
					 }
				 }
				 recordVo.setString("operationcode", this.operationcode);
				 if("2".equals(flag)) {
                     recordVo.setInt("task_id",rowSet.getInt("task_id"));
                 }
			     int ins_id=0;
			     if("2".equals(flag)) {
                     ins_id=rowSet.getInt("ins_id");
                 }
			     File file=tbo.outPdfFile(a0100,basepre,1,ins_id);

			     recordVo.setString("content_ext","pdf");
	 			FieldItem item=null;
			     for(int i=0;i<list.size();i++)
			     {
			    	     item=(FieldItem)list.get(i);
						String field_name=item.getItemid();
                        FieldItem fielditem=(FieldItem)item.cloneItem();
						if("A00".equals(fielditem.getFieldsetid()))
						{
							if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
							{
								if("photo".equalsIgnoreCase(field_name)&&rowSet.getObject(field_name)!=null) {
                                        recordVo.setObject("photo", rowSet.getObject(field_name));
                                }
							}
							if("ext".equalsIgnoreCase(field_name)) {
                                recordVo.setString("ext", rowSet.getString("ext"));
                            }
							continue;
						}

						if(fielditem.isChangeAfter()){
							field_name=field_name+"_2";
							if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0&&PubFunc.isUseNewPrograme(userview)){
								field_name=item.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_2";
							}
						}else if(fielditem.isChangeBefore()){
							field_name=field_name+"_1";
							if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0){
								field_name=item.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_1";
								}
						}
						if(this.field_name_map!=null&&this.field_name_map.get(field_name.toLowerCase())!=null) {
                            fielditem.setItemtype("M");
                        }
						if("A".equalsIgnoreCase(fielditem.getItemtype())){
							if(fielditem.getItemid().startsWith("attachment"))//过滤附件
                            {
                                continue;
                            } else {
                                recordVo.setString(field_name.toLowerCase(),rowSet.getString(field_name));
                            }
						}else if("N".equalsIgnoreCase(fielditem.getItemtype()))
						{
							if(fielditem.getDecimalwidth()==0) {
                                recordVo.setInt(field_name.toLowerCase(),rowSet.getInt(field_name));
                            } else {
                                recordVo.setDouble(field_name.toLowerCase(),rowSet.getDouble(field_name));
                            }
						}
						else if("M".equalsIgnoreCase(fielditem.getItemtype())){
							//recordVo.setString(field_name.toLowerCase(),Sql_switcher.readMemo(rowSet,field_name));
							bigTextFieldList.add(field_name.toLowerCase());
						}
						else
						{
							recordVo.setDate(field_name.toLowerCase(),rowSet.getDate(field_name));
						}
			     }
			     dao.addValueObject(recordVo);
			     if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				 {
						RecordVo updatevo=new RecordVo("template_archive");
						updatevo.setInt("id",id);
					 	Blob blob = getOracleBlob(file,"template_archive",id);
					 	updatevo.setObject("content_pdf",blob);
						dao.updateValueObject(updatevo);
				 }
			     if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
			     {
			    	// recordVo.setObject("content",file);
			    	 DbSecurityImpl dbS = new DbSecurityImpl();
			    	 try{
			    	 fis = new FileInputStream(file);
			    	 sql="update template_archive set content_pdf=? where id=?";

			    	 PreparedStatement prestmt =conn.prepareStatement(sql);
		             prestmt.setBinaryStream(1,fis,(int)file.length());
		             prestmt.setInt(2,id);
		             dbS.open(this.conn, sql);
		             prestmt.executeUpdate();
		             prestmt.close();
			    	 }finally{
			 			try {
			 				dbS.close(this.conn);
			 			} catch (Exception e) {
			 				e.printStackTrace();
			 			}

			 		}
			     }
			     //liuyz 大文本段首空格被清除
			     if(bigTextFieldList.size()>0){
						DbWizard dbw=new DbWizard(this.conn);
						StringBuffer updateItem=new StringBuffer();
						for(int i=0;i<bigTextFieldList.size();i++){
							updateItem.append("template_archive."+bigTextFieldList.get(i)+"="+srctab+"."+bigTextFieldList.get(i)+"`");
						}
						updateItem.setLength(updateItem.length()-1);
						String src_where="";
						if(this.infor_type==1){
							 src_where=srctab+".a0100='"+a0100+"' and lower("+srctab+".basepre)='"+basepre.toLowerCase()+"'  and  lower(template_archive.basepre)=lower("+srctab+".basepre)";
						} else if(this.infor_type==2){
							 src_where=srctab+".b0110='"+a0100+"' ";
						}else if(this.infor_type==3){
							 src_where=srctab+".e01a1='"+a0100+"' ";
						}

						if("2".equals(flag)) {
                            src_where+=" and ins_id="+ins_id;
                        }
						if(this.infor_type==1){
							dbw.updateRecord("template_archive",srctab , "template_archive.A0100="+srctab+".A0100",updateItem.toString(), "template_archive.id="+id,src_where);
						} else if(this.infor_type==2){
							dbw.updateRecord("template_archive",srctab , "template_archive.b0110="+srctab+".b0110",updateItem.toString(), "template_archive.id="+id,src_where);
						}else if(this.infor_type==3){
							dbw.updateRecord("template_archive",srctab , "template_archive.e01a1="+srctab+".e01a1",updateItem.toString(), "template_archive.id="+id,src_where);
						}
					}
			     RecordVo srctabvo=new RecordVo(srctab);
				 RecordVo desttabvo=new RecordVo("template_archive");
				 if(Sql_switcher.searchDbServer()==Constant.ORACEL&&srctabvo.hasAttribute("photo")&&desttabvo.hasAttribute("photo"))
				 {
						/**photo ,ext*/

						DbWizard dbw=new DbWizard(this.conn);
						String src_where="";
						if(this.infor_type==1){
							 src_where=srctab+".a0100='"+a0100+"' and lower("+srctab+".basepre)='"+basepre.toLowerCase()+"'  and  lower(template_archive.basepre)=lower("+srctab+".basepre)";
						} else if(this.infor_type==2){
							 src_where=srctab+".b0110='"+a0100+"' ";
						}else if(this.infor_type==3){
							 src_where=srctab+".e01a1='"+a0100+"' ";
						}

						if("2".equals(flag)) {
                            src_where+=" and ins_id="+ins_id;
                        }
						if(this.infor_type==1){
							dbw.updateRecord("template_archive",srctab , "template_archive.A0100="+srctab+".A0100","template_archive.photo="+srctab+".photo"+"`template_archive.ext="+srctab+".ext", "template_archive.id="+id,src_where);
						} else if(this.infor_type==2){
							dbw.updateRecord("template_archive",srctab , "template_archive.b0110="+srctab+".b0110","template_archive.photo="+srctab+".photo"+"`template_archive.ext="+srctab+".ext", "template_archive.id="+id,src_where);
						}else if(this.infor_type==3){
							dbw.updateRecord("template_archive",srctab , "template_archive.e01a1="+srctab+".e01a1","template_archive.photo="+srctab+".photo"+"`template_archive.ext="+srctab+".ext", "template_archive.id="+id,src_where);
						}
				}
			 }

			 if(rowSet!=null) {
                 rowSet.close();
             }
			 if(rowSet2!=null) {
                 rowSet2.close();
             }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			String message=e.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{
				PubFunc.resolve8060(this.conn,"template_archive");
			}
		} finally {
			PubFunc.closeResource(fis);//资源释放 jingq 2014.12.29
		}
	}


	/**
	* @Title: dropItemNotInDataDictionary
	* @Description: 人事异动模板的归档数据都在template_archive表中存放，
	* @Description: 如果客户需要归档的表单有很多字段也很多就会导致template_archive表中字段超出数据库的自身限制（最多1024个字段）导致归档失败，无法查看到历史数据。
	* @param
	* @return
	* @throws
	*/
	private void dropItemNotInDataDictionary() {
		String sql="select * from template_archive where 1=2";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		ArrayList colNameList=new ArrayList();
		try{
			rs=dao.search(sql);
		    ResultSetMetaData rsmd = rs.getMetaData();
		    int count=rsmd.getColumnCount();
		    for (int i = 1; i <= count; i++) {
		        String cellName= rsmd.getColumnName(i);
		        if((cellName.endsWith("_1")||cellName.endsWith("_2"))&&!cellName.toUpperCase().startsWith("T_")){//子集字段不用drop
		        	//int endIndex=cellName.indexOf("_");
		        	int endIndex=cellName.lastIndexOf("_"); //这样可以解决START_DATE_2的情况 liuzy 20160109
		        	String colName=cellName.substring(0, endIndex);
		        	if(colName.length()==5){
			        	FieldItem item=DataDictionary.getFieldItem(colName);
			        	if(item==null){
			        		colNameList.add(cellName);
			        	}
		        	}
		        }
		   }
		   Table table= new Table("template_archive");
		   for(int i=0;i<colNameList.size();i++){
			   String colName= (String) colNameList.get(i);
			   Field field=new Field(colName);
			   table.addField(field);
     }
		   DbWizard dbw=new DbWizard(this.conn);
		   if(table.getCount()>0){
			   dbw.dropColumns(table);
		   }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}



	}

	/**
	 * @param vo
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Blob getOracleBlob(File file,String tablename,int id) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select content_pdf from ");
		strSearch.append(tablename);
		strSearch.append(" where id=");
		strSearch.append(id);
		strSearch.append("  FOR UPDATE");

		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(tablename);
		strInsert.append(" set content_pdf=EMPTY_BLOB() where id=");
		strInsert.append(id);
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.conn);
	    InputStream in = null;
	    Blob blob = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			blob = blobutils.readBlob(strSearch.toString(),strInsert.toString(),in); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		}catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			PubFunc.closeIoResource(in);
		}
		return blob;
	}

	/**
	 * 取得 消息来源数据
	 * @param a0100
* @param nbase
* @param tabid
	 * @return
	 */
	public LazyDynaBean getFromMessageBean(String a0100,String nbase,int tabid)
	{
		LazyDynaBean bean=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sqlw="";
			if(this.infor_type==1){
				sqlw="where a0100='"+a0100+"' and lower(db_type)='"+nbase.toLowerCase()+"' and noticetempid="+tabid+" and state=1";
			}else if(this.infor_type==2){
				sqlw="where b0110='"+a0100+"'  and noticetempid="+tabid+" and state=1";
			}else if(this.infor_type==3){
				sqlw="where b0110='"+a0100+"'  and noticetempid="+tabid+" and state=1";
			}
			RowSet rowSet=dao.search("select sourcetempid from tmessage "+sqlw);
			if(rowSet.next())
			{
				int sourcetempid=rowSet.getInt(1);
        sqlw="";
			if(this.infor_type==1){
					sqlw=" where lower(BasePre)='"+nbase+"' and a0100='"+a0100+"'  and tabid="+sourcetempid+" order by id desc ";
				}else if(this.infor_type==2){
					sqlw=" where  b0110='"+a0100+"'  and tabid="+sourcetempid+" order by id desc ";
				}else if(this.infor_type==3){
					sqlw=" where  b0110='"+a0100+"'  and tabid="+sourcetempid+" order by id desc ";
				}
				rowSet=dao.search("select id from template_archive "+sqlw);
				if(rowSet.next())
				{
					bean=new LazyDynaBean();
					bean.set("id",rowSet.getString("id"));
					bean.set("tabid", String.valueOf(sourcetempid));

				}
			}
			PubFunc.closeDbObj(rowSet);
		}
		catch(Exception e)
{
e.printStackTrace();
}
	return bean;
	}



	/**
	 * 创建模板归档表
	 */
	private void createArchiveTable(ArrayList list)
	{
		try
		{
	        if (this.isSynedArchiveStruct){
	                return;
}
String tablename="template_archive";
			DbWizard dbw=new DbWizard(this.conn);
			if(dbw.isExistTable(tablename, false))
			{
				addFieldItem(tablename,2,dbw,list);
			}
			else
			{
				addFieldItem(tablename,1,dbw,list);
			}

            /**人事异动模板的归档数据都在template_archive表中存放，如果客户需要归档的表单有很多字段也很多就会导致template_archive表中字段超出数据库的自身限制（最多1024个字段）导致归档失败**/
            /**现在在归档之前将不在数据字典中的字段，也就是对于数据无效的字段给drop掉**/
            dropItemNotInDataDictionary();

/**重新加载数据模型*/
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tablename);
this.isSynedArchiveStruct=true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	private Field getFieldItem(String dataType,String codesetid,String itemid,String itemname,int length,int decimal,boolean isKey)
	{
		Field _temp=new Field(itemid,itemname);
		if("A".equalsIgnoreCase(dataType))
		{
			_temp.setDatatype(DataType.STRING);
			_temp.setLength(length);
			if(codesetid!=null&&codesetid.length()>0) {
                _temp.setCodesetid(codesetid);
            }
		}
		else if("N".equalsIgnoreCase(dataType))
		{

			if(decimal==0) {
                _temp.setDatatype(DataType.INT);
            } else
			{
				_temp.setDatatype(DataType.FLOAT);
				_temp.setDecimalDigits(decimal);
			}
		}
		else if("BN".equalsIgnoreCase(dataType)) {
            _temp.setDatatype(DataType.BINARY);
        } else if("M".equalsIgnoreCase(dataType)) {
            _temp.setDatatype(DataType.CLOB);
        } else if("D".equalsIgnoreCase(dataType))
		{
			_temp.setDatatype(DataType.DATE);
		}
		if(isKey) {
            _temp.setKeyable(true);
        }
		return _temp;
	}


	/**
	 *
	 * @param tablename
	 * @param flag  1:新建  2:添加字段
	 */
	private void addFieldItem(String tablename,int flag,DbWizard dbw,ArrayList list)
	{

		Table table=new Table(tablename);

		try
		{
			String columnName_str="/id/tabid/a0100/basepre/chguser/lasttime/appprocess/from_tabid/from_id/operationcode/task_id/content_pdf/content_ext/b0110_1/e0122_1/e01a1_1/a0101_1/";
			if(flag==1)
			{
				table.addField(getFieldItem("N","","id","主键序号",0,0,true));
				table.addField(getFieldItem("N","","tabid","表格号",0,0,false));
				table.addField(getFieldItem("A","","A0100","人员编号",8,0,false));
				table.addField(getFieldItem("A","","b0110","机构编号",9,0,false));
        table.addField(getFieldItem("A","","e01a1","职位编号",9,0,false));
				table.addField(getFieldItem("A","","BasePre","库前缀",3,0,false));
				table.addField(getFieldItem("A","","chguser","最近提交的用户",50,0,false));
table.addField(getFieldItem("D","","lasttime","最近提交的时间",10,0,false));
				table.addField(getFieldItem("M","","appprocess","审批流程",0,0,false));
table.addField(getFieldItem("N","","from_tabid","通知模板号",0,0,false));
				table.addField(getFieldItem("N","","from_id","消息来源归档号",0,0,false));
				table.addField(getFieldItem("A","","operationcode","业务代码",4,0,false));
				table.addField(getFieldItem("N","","task_id","任务号",0,0,false));
				table.addField(getFieldItem("BN","","content_pdf","PDF文件",0,0,false));
				table.addField(getFieldItem("A","","content_ext","文件后缀名",8,0,false));
				HashMap hm=new HashMap();
				table.addField(getFieldItem("A","UN","b0110_1",ResourceFactory.getProperty("column.sys.org"),30,0,false));
    hm.put("b0110_1", "1");
    table.addField(getFieldItem("A","UM","e0122_1",ResourceFactory.getProperty("column.sys.dept"),30,0,false));
hm.put("e0122_1", "1");
			table.addField(getFieldItem("A","@k","e01a1_1",ResourceFactory.getProperty("column.sys.pos"),30,0,false));
				hm.put("e01a1_1", "1");
            table.addField(getFieldItem("A","","a0101_1",ResourceFactory.getProperty("label.title.name"),30,0,false));
				hm.put("a0101_1", "1");
				FieldItem item=null;
				String field_name=null;
				for(int i=0;i<list.size();i++)
				{
					item=(FieldItem)list.get(i);
					FieldItem tempitem=(FieldItem)item.cloneItem();
					if(item.isChangeAfter()){
						field_name=item.getItemid()+"_2";
						if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0&&PubFunc.isUseNewPrograme(userview)){
							field_name=item.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_2";
						}
					}else if(item.isChangeBefore()){
						field_name=item.getItemid()+"_1";
						if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0){
							field_name=item.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_1";
							}
					}
					else {
                        field_name=item.getItemid();
                    }
					if(this.field_name_map!=null&&this.field_name_map.get(field_name.toLowerCase())!=null) {
                        tempitem.setItemtype("M");
                    }
					if(!hm.containsKey(field_name.toLowerCase()))
					{
						hm.put(field_name.toLowerCase(),item);
						tempitem.setItemid(field_name);
						table.addField(tempitem);
					}
				}
				dbw.createTable(table);
			}
			else
			{
				 ContentDAO dao=new ContentDAO(this.conn);
				 HashMap columnNameMap=new HashMap();
				 RowSet rowSet=dao.search("select * from "+tablename+" where 1=2");
				 ResultSetMetaData data=rowSet.getMetaData();
				 for(int i=1;i<=data.getColumnCount();i++)
				 {
						String columnName=data.getColumnName(i).toLowerCase();
						columnNameMap.put(columnName,"1");
				 }


				if(columnNameMap.get("a0100")==null) {
                    table.addField(getFieldItem("A","","A0100","人员编号",8,0,false));
                }
				if(columnNameMap.get("b0110")==null) {
                    table.addField(getFieldItem("A","","b0110","机构编号",9,0,false));
                }
				if(columnNameMap.get("e01a1")==null) {
                    table.addField(getFieldItem("A","","e01a1","职位编号",9,0,false));
                }
				if(columnNameMap.get("basepre")==null) {
                    table.addField(getFieldItem("A","","BasePre","库前缀",3,0,false));
                }
				if(columnNameMap.get("chguser")==null) {
                    table.addField(getFieldItem("A","","chguser","最近提交的用户",50,0,false));
                }
				if(columnNameMap.get("lasttime")==null) {
                    table.addField(getFieldItem("D","","lasttime","最近提交的时间",10,0,false));
                }
				if(columnNameMap.get("appprocess")==null) {
                    table.addField(getFieldItem("M","","appprocess","审批流程",0,0,false));
           }
				if(columnNameMap.get("from_tabid")==null) {
                    table.addField(getFieldItem("N","","from_tabid","通知模板号",0,0,false));
                }
				if(columnNameMap.get("from_id")==null) {
                    table.addField(getFieldItem("N","","from_id","消息来源归档号",0,0,false));
                }
				if(columnNameMap.get("operationcode")==null) {
                    table.addField(getFieldItem("A","","operationcode","业务代码",4,0,false));
                }
				if(columnNameMap.get("task_id")==null) {
                    table.addField(getFieldItem("N","","task_id","任务号",0,0,false));
                }
				if(columnNameMap.get("content_pdf")==null) {
                    table.addField(getFieldItem("BN","","content_pdf","PDF文件",0,0,false));
                }
				if(columnNameMap.get("content_ext")==null) {
                    table.addField(getFieldItem("A","","content_ext","文件后缀名",8,0,false));
                }
				if(columnNameMap.get("b0110_1")==null) {
                    table.addField(getFieldItem("A","UN","b0110_1",ResourceFactory.getProperty("column.sys.org"),30,0,false));
                }
				if(columnNameMap.get("e0122_1")==null) {
                    table.addField(getFieldItem("A","UM","e0122_1",ResourceFactory.getProperty("column.sys.dept"),30,0,false));
                }
				if(columnNameMap.get("e01a1_1")==null) {
                    table.addField(getFieldItem("A","@k","e01a1_1",ResourceFactory.getProperty("column.sys.pos"),30,0,false));
                }
				if(columnNameMap.get("a0101_1")==null) {
                    table.addField(getFieldItem("A","","a0101_1",ResourceFactory.getProperty("label.title.name"),30,0,false));
                }

				FieldItem item=null;
				String field_name=null;
				for(int i=0;i<list.size();i++)
				{
					item=(FieldItem)list.get(i);
					FieldItem tempitem=(FieldItem)item.cloneItem();
					if(item.isChangeAfter()){
						field_name=item.getItemid()+"_2";
						if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0&&PubFunc.isUseNewPrograme(userview)){
							field_name=item.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_2";
						}
					}else if(item.isChangeBefore()){
						field_name=item.getItemid()+"_1";
						if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0){
							field_name=item.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_1";
                                        }
                                }
                        		else {
                        field_name=item.getItemid();
                    }

					if(columnName_str.indexOf("/"+field_name.toLowerCase()+"/")!=-1) {
                        continue;
                    }

					if(columnNameMap.get(field_name.toLowerCase())!=null) {
                        continue;
                    }

					tempitem.setItemid(field_name);
					table.addField(tempitem);

				}
				if(table.getCount()>0) {
                    dbw.addColumns(table);
                }
				PubFunc.closeDbObj(rowSet);
			}

			syncGzField2(tablename,list,columnName_str,dbw);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}





	/**
	 *当指标长度或类型发生的变化同步归档表
	 */
	private void  syncGzField2(String tableName,ArrayList fieldlist,String columnName_str,DbWizard dbw)
	{
		try
{
     ContentDAO dao=new ContentDAO(this.conn);
			 RowSet rowSet=dao.search("select * from "+tableName+" where 1=2");
			 ResultSetMetaData data=rowSet.getMetaData();
      HashMap metaDataMap=new HashMap();
			 LazyDynaBean _bean=new LazyDynaBean();
			 for(int i=1;i<=data.getColumnCount();i++)
			 {
					String columnName=data.getColumnName(i).toLowerCase();
					int columnType=data.getColumnType(i);
					int size=data.getColumnDisplaySize(i);
					int scale=data.getScale(i);
					 _bean=new LazyDynaBean();
					 _bean.set("columntype", String.valueOf(columnType));
					 _bean.set("size", String.valueOf(size));
					 _bean.set("scale", String.valueOf(scale));
					 _bean.set("columnName",columnName);
					metaDataMap.put(columnName,_bean);
			 }


			 ArrayList alterList=new ArrayList();
			 ArrayList resetList=new ArrayList();
			 FieldItem item=null;
			 for(int i=0;i<fieldlist.size();i++)
			 {

				    item=(FieldItem)fieldlist.get(i);
				    String item_id=item.getItemid().toLowerCase();
				    FieldItem tempItem=(FieldItem)item.cloneItem();
				    if(DataDictionary.getFieldItem(item_id)==null) {
                        continue;
                    }

				    String field_name="";
if(item.isChangeAfter()) {
field_name=item.getItemid()+"_2";
      } else if(item.isChangeBefore()){
						field_name=item.getItemid()+"_1";
                if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0){
							field_name=item.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_1";
							}
					}
					else {
                        field_name=item.getItemid();
}
if(this.field_name_map!=null&&this.field_name_map.get(field_name.toLowerCase())!=null) {
                        tempItem.setItemtype("M");
}
if(!(item.isChangeAfter()||item.isChangeBefore())) {
continue;
                    }
				    tempItem.setItemid(field_name);
				    if(columnName_str.indexOf("/"+field_name.toLowerCase()+"/")!=-1) {
                        continue;
                    }


						_bean=(LazyDynaBean)metaDataMap.get(field_name.toLowerCase());


						int columnType=Integer.parseInt((String)_bean.get("columntype"));
						int size=Integer.parseInt((String)_bean.get("size"));
						int scale=Integer.parseInt((String)_bean.get("scale"));
                                    switch(columnType)
						{
							case java.sql.Types.INTEGER:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale)
									{
										if(Sql_switcher.searchDbServer()!=2) {
                                            alterList.add(tempItem.cloneField());
                                        } else {
               alertColumn(tableName,tempItem,dbw,dao);
}
						}
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
                                                    if("A".equals(tempItem.getItemtype()))
									{
										if(Sql_switcher.searchDbServer()!=2) {
                                            alterList.add(tempItem.cloneField());
                                        } else {
                                            alertColumn(tableName,tempItem,dbw,dao);
                                        }
									}
									else {
                                        resetList.add(tempItem.cloneField());
                                    }
								}
								break;
							case java.sql.Types.DATE:
							case java.sql.Types.TIMESTAMP:
								if(!"D".equals(tempItem.getItemtype()))
								{
									resetList.add(tempItem.cloneField());
								}
								break;
							case java.sql.Types.VARCHAR:
								if("A".equals(tempItem.getItemtype()))
								{
									if(tempItem.getItemlength()>size)
									{
										if(Sql_switcher.searchDbServer()!=2) {
                                            alterList.add(tempItem.cloneField());
                                        } else {
                                            alertColumn(tableName,tempItem,dbw,dao);
                                        }
									}
								}
								else {
                                        resetList.add(tempItem.cloneField());
                                }
								break;
                                case java.sql.Types.DOUBLE:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale)
									{
										if(Sql_switcher.searchDbServer()!=2) {
                                            alterList.add(tempItem.cloneField());
                                        } else {
                                            alertColumn(tableName,tempItem,dbw,dao);
                                        }
									}
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
									{
										if(Sql_switcher.searchDbServer()!=2) {
                                            alterList.add(tempItem.cloneField());
                                        } else {
                                            alertColumn(tableName,tempItem,dbw,dao);
                                        }
									}
									else {
                                        resetList.add(tempItem.cloneField());
                                    }
								}


								break;
							case java.sql.Types.NUMERIC:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale)
									{
										if(Sql_switcher.searchDbServer()!=2) {
                                            alterList.add(tempItem.cloneField());
                                        } else {
                                            alertColumn(tableName,tempItem,dbw,dao);
                                        }
									}
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
									{
										if(Sql_switcher.searchDbServer()!=2) {
                                            alterList.add(tempItem.cloneField());
                                        } else {
                                            alertColumn(tableName,tempItem,dbw,dao);
                                        }
									}
									else {
                                        resetList.add(tempItem.cloneField());
                                    }
								}
								break;
							case java.sql.Types.CLOB:  //对于clob类型，也应该进行数据类型的判断，liuzy 20160113
							case java.sql.Types.LONGVARCHAR:
								if(!"M".equals(tempItem.getItemtype()))
								{
									resetList.add(tempItem.cloneField());
								}
								break;
						}

				}
				rowSet.close();
			    Table table=new Table(tableName);
			    if(Sql_switcher.searchDbServer()!=2)  //不为oracle
			    {
				    for(int i=0;i<alterList.size();i++) {
                        table.addField((Field)alterList.get(i));
                    }
					if(alterList.size()>0) {
                        dbw.alterColumns(table);
                    }
					 table.clear();
			    }

			     table.clear();
				 for(int i=0;i<resetList.size();i++) {
                     table.addField((Field)resetList.get(i));
                 }
				 if(resetList.size()>0)
				 {
					 dbw.dropColumns(table);
					 dbw.addColumns(table);
				 }



		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 判断是否是已结束的单据
	 * @param task_id
	 * @return
	 */
	public String isFinishedRecord(String task_id)
	{
		String isFinishedRecord="0";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=null;
            /*and   flag=1   去掉这个标记 20160805 wangrd task_state=5 就可以标识吧  bug21320*/
            rowSet=dao.search("select count(task_id) from t_wf_task where (  task_type='2' or  task_type='9') and   task_state='5' and task_id="+task_id);
            if(rowSet.next())
			{
				if(rowSet.getInt(1)>0) {
                    isFinishedRecord="1";
                }
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return isFinishedRecord;
	}


    /**
     * @Title: getApproveOpinion
     * @Description:
     * 申请人：名字  申请时间
       审批人意见：同意 审批人名字 审批时间
        批注：大大大
       节点名称意见：同意 审批人名字 审批时间
     * @param @param ins_vo
     * @param @param task_id
     * @param @param wf_actor
     * @param @param sp_j
     * @param @return
     * @return String
     * @throws
    */
    public String getApproveOpinion(RecordVo ins_vo,String task_id,WF_Actor wf_actor,String sp_j){
//获得审批信息
StringBuffer sbuffer = new StringBuffer();
        int flag=1; //审批人
        if(ins_vo!=null){
           //bug 38592通过这种方法不能准确判断是否是申请人。当申请人和一级审批人相同时无法判断准确。
/* if(this.userview.getStatus()==0&&ins_vo.getInt("actor_type")==4&&this.userview.getUserName().equalsIgnoreCase(ins_vo.getString("actorid")))
                flag=0;
if(this.userview.getStatus()==4&&ins_vo.getInt("actor_type")==1&&(this.userview.getDbname()+this.userview.getA0100()).equalsIgnoreCase(ins_vo.getString("actorid")))
flag=0;*/

          // if (flag==1){//是否是发起人 驳回后 上面判断不准
           if ("1".equals(isStartNode(ins_vo.getString("ins_id"), task_id, this.getTabid()+"", this.sp_mode))){
flag=0 ;
}else{
flag=1 ;
}
//}
}
String nodename="";
String nodeRoleName="";//节点名称
if(flag==0) {
            nodename="申请人";
        } else {
            nodename="审批人";
        }

        ContentDAO dao=new ContentDAO(this.conn);
        WF_Node wf_node= null;
if (this.sp_mode==0 && !"".equals(task_id)){//自动审批
RowSet rset = null;
try
{//显示节点名称
if(flag==1 &&!"0".equals(task_id)) {
RecordVo task_vo=new RecordVo("t_wf_task");
	                task_vo.setInt("task_id",Integer.parseInt(task_id));
	                task_vo=dao.findByPrimaryKey(task_vo);
if (task_vo!=null){
int node_id=task_vo.getInt("node_id");
wf_node=new WF_Node(node_id,this.conn);
                    String _nodename = wf_node.getNodename();
	                    if (_nodename!=null
	                            && _nodename.length()>0
	                            && !"human".equals(_nodename)
	                            && !"begin".equals(_nodename)
	                            && !"活动".equals(_nodename)
	                            ){
	                    	nodeRoleName=  _nodename;
	                    };
	                }
            	}else if(flag==0){
            		String nodename1= "";
            		rset=dao.search("select nodename from t_wf_node where tabid="+tabid+" and nodetype=1");
            		if(rset.next()) {
                        nodename1 = rset.getString("nodename");
                    }
            		if(nodename1!=null && nodename1.length()>0&& !"begin".equals(nodename1)) {
            			nodeRoleName = nodename1;
            		}
            	}
            }
            catch(Exception ex)
            {
            	ex.printStackTrace();
            }  finally {
            	PubFunc.closeDbObj(rset);
            }
        }
		/**审批意见格式变更*/
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
        String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122)) {
            display_e0122="0";
        }
		CodeItem UMitem=AdminCode.getCode("UM", this.userview.getUserDeptId(), Integer.parseInt(display_e0122));
        CodeItem UNitem=AdminCode.getCode("UN", this.userview.getUserOrgId());
        String value="";
        if(UNitem!=null){
        	value=UNitem.getCodename();
        }else{
        	value = AdminCode.getCodeName("UN",this.userview.getUserOrgId())!= null ? AdminCode.getCodeName("UN", this.userview.getUserOrgId()): "";
        }
		if(UMitem!=null)
    	{
			if(StringUtils.isNotBlank(value)){
				value+="/"+UMitem.getCodename();
			}else{
				value=UMitem.getCodename();
			}
		}
    	else
    	{
    		String value1 = AdminCode.getCodeName("UM",this.userview.getUserDeptId())!= null ? AdminCode.getCodeName("UM", this.userview.getUserDeptId()): "";
    		if(StringUtils.isNotBlank(value)){
				value+="/"+value1;
			}else{
				value=value1;
			}
    	}
		sbuffer.append("\n");
        sbuffer.append(value);
      //审批意见中增加节点名称
        if(flag==0){//申请人
        	if(StringUtils.isNotBlank(nodeRoleName)){
        		nodename=nodeRoleName;
        	}
        }
        else{//审批人
        	if(StringUtils.isNotBlank(nodeRoleName)){
        		nodename=nodeRoleName;
        	}
        }
        sbuffer.append("("+nodename+")：");
        sbuffer.append("\n");
        if(flag!=0){//申请人 不填写是否同意
            if(wf_actor!=null&&wf_actor.getSp_yj()!=null){
                if("02".equals(sp_j)){
                    sbuffer.append(""+AdminCode.getCodeName("30","02"));
                }else{
                    sbuffer.append(""+AdminCode.getCodeName("30",wf_actor.getSp_yj()));
                }
            }
        }
        if(flag!=0){
        	sbuffer.append("     ");
        }
        sbuffer.append(""+this.userview.getUserFullName());
        sbuffer.append("     ");
        sbuffer.append(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm")+"   ");
        String no_sp_opinion="false";
        if(SystemConfig.getPropertyValue("no_sp_opinion")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("no_sp_opinion").trim())) {
            no_sp_opinion=SystemConfig.getPropertyValue("no_sp_opinion");
        }

        if(this.sp_mode!=1&& "true".equalsIgnoreCase(no_sp_opinion)) //不为手工审批，同时参数设置报批时无意见填写窗口
        {
            if(wf_actor!=null&&wf_actor.getContent()!=null&&"02".equals(sp_j)){ //20140809  dengcan 驳回需要填写意见
                if(!("同意".equals(wf_actor.getContent())) && wf_actor.getContent().length()>0){
                  sbuffer.append("\n");//bug 31574
                  sbuffer.append("批注："+wf_actor.getContent().replace("\r\n", "\n").replace("<p>", "\n").replace("</p>", "").replace("&nbsp;", " "));
                }
            }
        }
        else
        {
            if(wf_actor!=null&&wf_actor.getContent()!=null){
                if(!("同意".equals(wf_actor.getContent()))&& wf_actor.getContent().length()>0){
                   sbuffer.append("\n");//bug 31574
                   sbuffer.append("批注："+wf_actor.getContent().replace("\r\n", "\n").replace("<p>", "\n").replace("</p>", "").replace("&nbsp;", " "));
                }
            }
        }
        return sbuffer.toString();
    }


	public String getApproveOpinion(RecordVo ins_vo,WF_Actor wf_actor,String sp_j){
	    if (true) {
            return getApproveOpinion(ins_vo,"0",wf_actor,sp_j);
        }
	    //获得审批信息
		StringBuffer sbuffer = new StringBuffer();
		int flag=1; //审批人
		if(ins_vo!=null)
		{
			if(this.userview.getStatus()==0&&ins_vo.getInt("actor_type")==4&&this.userview.getUserName().equalsIgnoreCase(ins_vo.getString("actorid"))) {
                flag=0;
            }
			if(this.userview.getStatus()==4&&ins_vo.getInt("actor_type")==1&&(this.userview.getDbname()+this.userview.getA0100()).equalsIgnoreCase(ins_vo.getString("actorid"))) {
                flag=0;
            }

		}
		if(flag==0) {
            sbuffer.append("申请人:");
        } else {
            sbuffer.append("审批人:");
        }
		sbuffer.append(""+this.userview.getUserFullName()+"     ");

		if(flag==0) {
            sbuffer.append("申请时间:");
        } else {
            sbuffer.append("审批时间:");
        }
		sbuffer.append(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm")+"   ");
		//当为申请人时，无需 意见：同意  liuzy 20150922
		if(flag!=0){
			sbuffer.append("意见:");
			if(wf_actor!=null&&wf_actor.getSp_yj()!=null){
				if("02".equals(sp_j)){
					sbuffer.append(""+AdminCode.getCodeName("30","02"));
				}else{
					sbuffer.append(""+AdminCode.getCodeName("30",wf_actor.getSp_yj()));
				}
			}
		}

		sbuffer.append("\r\n");
		String no_sp_opinion="false";
		if(SystemConfig.getPropertyValue("no_sp_opinion")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("no_sp_opinion").trim())) {
            no_sp_opinion=SystemConfig.getPropertyValue("no_sp_opinion");
        }

		if(this.sp_mode!=1&& "true".equalsIgnoreCase(no_sp_opinion)) //不为手工审批，同时参数设置报批时无意见填写窗口
		{
			if(wf_actor!=null&&wf_actor.getContent()!=null&&"02".equals(sp_j)){ //20140809  dengcan 驳回需要填写意见
				if(!(flag==0 && "同意".equals(wf_actor.getContent()))){
				   sbuffer.append("批注:"+wf_actor.getContent().replace("<p>", "\r\n").replace("</p>", "").replace("&nbsp;", " "));
				}
			}
		}
		else
		{
			if(wf_actor!=null&&wf_actor.getContent()!=null){
				if(!(flag==0 && "同意".equals(wf_actor.getContent()))){
				   sbuffer.append("批注:"+wf_actor.getContent().replace("<p>", "\r\n").replace("</p>", "").replace("&nbsp;", " "));
				}
			}
		}
		sbuffer.append("\r\n");
		return sbuffer.toString();
	}
	public void updateApproveOpinion(String table_name,String ins_id,String fieldstr,String approveopinion,String opinionsql){
		//更新templet_id中审批意见指标（变化后）
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rowSet=null;
		try {

			int index=opinionsql.indexOf(table_name);
			if(index!=-1)
			{
				String subsql=opinionsql.substring(index);
				opinionsql="select ";
				if(this.infor_type==1){
					opinionsql+="a0100,basepre,ins_id";
					}else if(this.infor_type==2){
						opinionsql+="b0110,ins_id";
					}else if(this.infor_type==3){
						opinionsql+="e01a1,ins_id";
				}
				opinionsql+=","+fieldstr+" from  "+subsql;
			}

			rowSet=dao.search(opinionsql);
			ArrayList list = new ArrayList();
			while(rowSet.next())
			{
				ArrayList paramlist = new ArrayList();
				String opinion_str=Sql_switcher.readMemo(rowSet, fieldstr.toLowerCase());
				paramlist.add(opinion_str+"\r\n"+approveopinion);
				if(this.infor_type==1){
					paramlist.add(rowSet.getString("a0100"));
					paramlist.add(rowSet.getString("basepre").toLowerCase());
					paramlist.add(ins_id);
				}else if(this.infor_type==2){
					paramlist.add(rowSet.getString("b0110"));
					paramlist.add(ins_id);
				}else if(this.infor_type==3){
					paramlist.add(rowSet.getString("e01a1"));
					paramlist.add(ins_id);
				}
				list.add(paramlist);
			}
			if(list.size()>0) {
				String updatesql = "update "+table_name+" set "+fieldstr+"=? where ";
				if(this.infor_type==1) {
					updatesql+=" a0100=? and ";
					updatesql+=" lower(basepre)=? and ";
					updatesql+=" ins_id=?";
				}else if(this.infor_type==2){
					updatesql+=" b0110=? and ";
					updatesql+=" ins_id=?";
				}else if(this.infor_type==3){
					updatesql+=" e01a1=? and ";
					updatesql+=" ins_id=?";
				}
				dao.batchUpdate(updatesql, list);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowSet);
		}
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

	/*是否勾选了 提交入库时不判断子集和指标的权限**/
	public boolean getIsIgnorePriv(){
		if("1".equals(this.UnrestrictedMenuPriv))//=1 不判断
        {
            return true;//=true 提交入库时不判断子集和指标权限  =false 判断
        }
		return false;
	}
	/**检查是否有归档附件的权限*/
	public boolean hasFunction(){
		boolean bool = false;
		String tablename = "";
		if(getInfor_type()==1){
			tablename="A00";
		}else if(getInfor_type()==2){
			tablename="B00";
		}else if(getInfor_type()==3){
			tablename="K00";
		}
		if("2".equals(this.userview.analyseTablePriv(tablename))/*&& (this.userview.getMediapriv().length()>0 || this.userview.isAdmin() || this.userview.getGroupId().equals("1"))*/){//bug26515 liuyz
			bool = true;
		}
		if(!bool){
			bool = getIsIgnorePriv();
		}
		return bool;
	}
	/**将多媒体分类的权限组装成sql语句的形式*/
	public String getMediaPriv(){
		/*StringBuffer sb = new StringBuffer("(");
		String priv = this.userview.getMediapriv().toString();//我在上层已经用hasFunction控制了。所以priv一定有值。没值则不会调用这个函数
		String[] array = priv.split(",");
		for(int i=0;i<array.length;i++){
			if(array[i].equals("")){
				continue;
			}
			sb.append("'"+array[i]+"',");
		}
		sb.setLength(sb.length()-1);
		sb.append(")");*/
		//bug26515 liuyz
		StringBuffer sb = new StringBuffer("(select distinct flag from  MediaSort union select 'K' from MEDIASORT)");
		return sb.toString();
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

	/**
	 * 提交单据入库时获得某子集条件更新所需得指标列表(涉及子集指标和模板变化后指标)
	 * @param setid  子集id
	 * @return
	 */
	public ArrayList getCondUpdateFieldList(String setid)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			list.addAll((ArrayList)DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET).clone());
			String sql="select * from template_set  where tabid="+this.tabid+" and field_name is not null and field_type is not null   and chgstate=2 and subflag='0'";
			RowSet rowSet=dao.search(sql);
			while(rowSet.next())
			{
				String flag=rowSet.getString("flag");
				String field_type=rowSet.getString("field_type");
				if(flag==null|| "".equals(flag)) {
                    continue;
                }
				if("A".equalsIgnoreCase(flag)|| "B".equalsIgnoreCase(flag)|| "K".equalsIgnoreCase(flag))
				{
					if(field_type!=null&&field_type.trim().length()>0)
					{
						if(!("A".equalsIgnoreCase(field_type)|| "N".equalsIgnoreCase(field_type)|| "D".equalsIgnoreCase(field_type)|| "M".equalsIgnoreCase(field_type))) {
                            continue;
                        }
					}
					String field_name=rowSet.getString("field_name");
					FieldItem item=DataDictionary.getFieldItem(field_name.toLowerCase());
					if(item!=null)
					{
						/**可以增加模板指标与字典表指标进行校验*/
						FieldItem tempitem=(FieldItem)item.cloneItem();
						tempitem.setNChgstate(2);
						tempitem.setItemid(rowSet.getString("field_name")+"_2");
						tempitem.setItemdesc("拟"+rowSet.getString("field_hz"));
						list.add(tempitem);
					}
				}

			}
			PubFunc.closeDbObj(rowSet);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**得到basepre和a1000的list 用于删除个人附件*/
	public ArrayList getPersonlist(int infor_type,String tablename){
		ArrayList list = new ArrayList();
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			StringBuffer sb = new StringBuffer("");
			if(infor_type==1){
				if(this.isBEmploy())
				{
					ArrayList templist = new ArrayList();
					String basepre =this.userview.getDbname();
					String a0100 =this.userview.getA0100();
					templist.add(basepre);
					templist.add(a0100);
					list.add(templist);
				}
				else
				{
					sb.append("select basepre,a0100 from "+tablename+" where submitflag=1");
					rs = dao.search(sb.toString());
					while(rs.next()){
						ArrayList templist = new ArrayList();
						String basepre = rs.getString("basepre");
						String a0100 = rs.getString("a0100");
						templist.add(basepre);
						templist.add(a0100);
						list.add(templist);
					}
				}
			}else if(infor_type==2){
				sb.append("select b0110 from "+tablename+" where submitflag=1");
				rs = dao.search(sb.toString());
				while(rs.next()){
					ArrayList templist = new ArrayList();
					String b0110 = rs.getString("b0110");
					templist.add(b0110);
					list.add(templist);
				}
			}else if(infor_type==3){
				sb.append("select e01a1 from "+tablename+" where submitflag=1");
				rs = dao.search(sb.toString());
				while(rs.next()){
					ArrayList templist = new ArrayList();
					String e01a1 = rs.getString("e01a1");
					templist.add(e01a1);
					list.add(templist);
				}
			}
			PubFunc.closeDbObj(rs);
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}

	public HashMap getitemCounts(){

		ContentDAO dao=new ContentDAO(this.conn);
		HashMap codehm=new HashMap();
		try {

			RowSet rset=dao.search("select codesetid,COUNT(codesetid)codelength from codeitem group by codesetid  having COUNT(codesetid)>=100  ");
			while(rset.next()){
				codehm.put(rset.getString("codesetid").toLowerCase(),rset.getString("codelength"));
			}
			rset.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return codehm;
	}


	 /**
	 * 获得 汇报关系中 直接上级指标
	 * @return
	 */
	public String getPS_SUPERIOR_value()
	{
		String fieldItem="";
		RecordVo vo=ConstantParamter.getConstantVo("PS_SUPERIOR");
        if(vo==null) {
            return fieldItem;
        }
        String param=vo.getString("str_value");
        if(param==null|| "".equals(param)|| "#".equals(param)) {
            return fieldItem;
        }
		fieldItem=param;
		return fieldItem;
	}


	/**
	 * 判断当前任务是否进行编制控制
	 * @param task_id
	 * @return
	 */
	public boolean  isHeadCountControl(int task_id)
	{
		boolean isControl= "1".equals(this.headCount_control)?true:false;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			if(isControl&&this.isBsp_flag()&&this.getSp_mode()==0)
			{
				StringBuffer strsql=new StringBuffer("select * from t_wf_node where ");
				if(task_id==0) {
                    strsql.append(" nodetype='"+NodeType.START_NODE+"'");
                } else
				{
					strsql.append("  node_id=(select node_id from t_wf_task where task_id="+task_id+") ");
				}
				strsql.append(" and tabid="+this.tabid);
				RowSet rset=dao.search(strsql.toString());
				String ext_param="";
				if(rset.next())
				{
					ext_param=Sql_switcher.readMemo(rset,"ext_param");
				}
				if(ext_param!=null&&ext_param.trim().length()>0)
				{
					Document doc=PubFunc.generateDom(ext_param);;
					String xpath="/params/headCount_control";
					XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
					Element element=(Element)findPath.selectSingleNode(doc);
					if(element!=null&&element.getValue()!=null&& "false".equalsIgnoreCase(element.getValue())) {
                        isControl=false;
                    }
				}
				if(rset!=null) {
                    rset.close();
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return isControl;
	}
	 /** 2015-07-10
	 * 判断某审批环节 是否是在此审批流程中
	 * 判断task_id 是否与ins_id 是否匹配 检查窜单的问题
	 * @return
	 */
	public boolean taskIsMatchedInstance(int ins_id, int task_id)
	{
		boolean b=true;
		try{
			String sql="select * from t_wf_task where ins_id =? and  task_id =? ";
			ArrayList list=new ArrayList();
	        list.add(Integer.valueOf(ins_id));
	        list.add(Integer.valueOf(task_id));
	        ContentDAO dao=new ContentDAO(this.conn);
	        RowSet rSet =dao.search(sql,list);
	        if (!rSet.next()){
	        	b=false;//不匹配
	        }
	        PubFunc.closeDbObj(rSet);
		}
		catch(Exception e){
			e.printStackTrace();

		}
		return b;

	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String[] getGz_stand() {
		return gz_stand;
	}

	public void setGz_stand(String[] gz_stand) {
		this.gz_stand = gz_stand;
	}

	public String[] getMsg_template() {
		return msg_template;
	}

	public void setMsg_template(String[] msg_template) {
		this.msg_template = msg_template;
	}

	public String getMuster_str() {
		return muster_str;
	}

	public void setMuster_str(String muster_str) {
		this.muster_str = muster_str;
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

	public boolean isBsp_flag() {
		return bsp_flag;
	}

	public void setBsp_flag(boolean bsp_flag) {
		this.bsp_flag = bsp_flag;
	}

	public String getDest_base() {
		return dest_base;
	}

	public void setDest_base(String dest_base) {
		this.dest_base = dest_base;
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

	public int getOperationtype() {
		return operationtype;
	}

	public void setOperationtype(int operationtype) {
		this.operationtype = operationtype;
	}

	public String getCard_str() {
		return card_str;
	}

	public void setCard_str(String card_str) {
		this.card_str = card_str;
	}

	public RecordVo getTable_vo() {
		return table_vo;
	}

	public int getIns_id() {
		return ins_id;
	}

	public void setIns_id(int ins_id) {
		this.ins_id = ins_id;
	}

	public int getSp_mode() {
		return sp_mode;
	}

	public void setSp_mode(int sp_mode) {
		this.sp_mode = sp_mode;
	}

	public ArrayList getInslist() {
		return inslist;
	}

	public void setInslist(ArrayList inslist) {
		this.inslist = inslist;
	}

	public String getFactor() {
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

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public ArrayList getTasklist() {
		return tasklist;
	}

	public void setTasklist(ArrayList tasklist) {
		this.tasklist = tasklist;
	}

	public int getTask_id() {
		return task_id;
	}

	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}

	public boolean isBEmploy() {
		return bEmploy;
	}

	public String getTask_sp_flag() {
		return task_sp_flag;
	}

	public void setTask_sp_flag(String task_sp_flag) {
		this.task_sp_flag = task_sp_flag;
	}

	public void setBEmploy(boolean employ) {
		bEmploy = employ;
		if(isBEmploy())//员工通过自助平台发动申请
        {
            this.bz_tablename="g_templet_"+this.tabid;
        } else {
            this.bz_tablename=this.userview.getUserName()+"templet_"+this.tabid;
        }
	}

	public int getTabid() {
		return tabid;
	}

	public void setTabid(int tabid) {
		this.tabid = tabid;
	}

	public String getSp_yj() {
		return sp_yj;
	}

	public void setSp_yj(String sp_yj) {
		this.sp_yj = sp_yj;
	}

	public boolean isEmail_staff() {
		return email_staff;
	}

	public void setEmail_staff(boolean email_staff) {
		this.email_staff = email_staff;
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

	public String getBusiness_model() {
		return business_model;
	}

	public void setBusiness_model(String business_model) {
		this.business_model = business_model;
	}

	public String getChange_after_get_data() {
		return change_after_get_data;
	}

	public void setChange_after_get_data(String change_after_get_data) {
		this.change_after_get_data = change_after_get_data;
	}

	public boolean isImpDataFromArchive_sub() {
		return isImpDataFromArchive_sub;
	}

	public void setImpDataFromArchive_sub(boolean isImpDataFromArchive_sub) {
		this.isImpDataFromArchive_sub = isImpDataFromArchive_sub;
	}

	public boolean isValidateM_L() {
		return isValidateM_L;
	}

	public void setValidateM_L(boolean isValidateM_L) {
		this.isValidateM_L = isValidateM_L;
	}

	public HashMap getOtherParaMap() {
		return otherParaMap;
	}

	public void setOtherParaMap(HashMap otherParaMap) {
		this.otherParaMap = otherParaMap;
	}

	public String getTemplate_sp() {
		return template_sp;
	}

	public void setTemplate_sp(String template_sp) {
		this.template_sp = template_sp;
	}

	public String getReject_cause() {
		return reject_cause;
	}

	public void setReject_cause(String reject_cause) {
		this.reject_cause = reject_cause;
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

	public int get_node_id() {
		return _node_id;
	}

	public void set_node_id(int _node_id) {
		this._node_id = _node_id;
	}

	public String getHmuster_sql() {
		return hmuster_sql;
	}

	public void setHmuster_sql(String hmuster_sql) {
		this.hmuster_sql = hmuster_sql;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getArchflag() {
		return archflag;
	}

	public void setArchflag(String archflag) {
		this.archflag = archflag;
	}

	public String getInit_base() {
		return init_base;
	}

	public void setInit_base(String init_base) {
		this.init_base = init_base;
	}

	public String getUnrestrictedMenuPriv() {
		return UnrestrictedMenuPriv;
	}

	public void setUnrestrictedMenuPriv(String unrestrictedMenuPriv) {
		UnrestrictedMenuPriv = unrestrictedMenuPriv;
	}

	public int getInfor_type() {
		return infor_type;
	}

	public void setInfor_type(int infor_type) {
		this.infor_type = infor_type;
	}

	public String getPriv_html() {
		return priv_html;
	}

	public void setPriv_html(String priv_html) {
		this.priv_html = priv_html;
	}

	public int get_static() {
		return _static;
	}

	public void set_static(int _static) {
		this._static = _static;
	}

	public String getUnrestrictedMenuPriv_Input() {
		return UnrestrictedMenuPriv_Input;
	}

	public void setUnrestrictedMenuPriv_Input(String unrestrictedMenuPriv_Input) {
		UnrestrictedMenuPriv_Input = unrestrictedMenuPriv_Input;
	}

	public String getId_gen_manual() {
		return id_gen_manual;
	}

	public void setId_gen_manual(String id_gen_manual) {
		this.id_gen_manual = id_gen_manual;
	}

	public String getExistid_gen_manual() {
		return existid_gen_manual;
	}

	public String getSignxml() {
		return signxml;
	}

	public void setSignxml(String signxml) {
		this.signxml = signxml;
	}

	public void setExistid_gen_manual(String existid_gen_manual) {
		this.existid_gen_manual = existid_gen_manual;
	}

	public HashMap getSub_domain_map() {
		return sub_domain_map;
	}

	public void setSub_domain_map(HashMap sub_domain_map) {
		this.sub_domain_map = sub_domain_map;
	}

	public HashMap getField_name_map() {
		return field_name_map;
	}

	public void setField_name_map(HashMap field_name_map) {
		this.field_name_map = field_name_map;
	}

	public String getFilter_by_factor() {
		return filter_by_factor;
	}

	public void setFilter_by_factor(String filter_by_factor) {
		this.filter_by_factor = filter_by_factor;
	}

	public String getOpinion_field() {
		return opinion_field;
	}

	public String getRelation_id() {
		return Relation_id;
	}

	public void setRelation_id(String relation_id) {
		Relation_id = relation_id;
	}

	public String getBz_tablename() {
		return bz_tablename;
	}

	public void setBz_tablename(String bz_tablename) {
		this.bz_tablename = bz_tablename;
	}
	public void setOpinion_field(String opinion_field) {
		this.opinion_field = opinion_field;
	}

	public String getApprove_opinion() {
		return approve_opinion;
	}

	public void setApprove_opinion(String approve_opinion) {
		this.approve_opinion = approve_opinion;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getReject_type() {
		return reject_type;
	}

	public void setReject_type(String reject_type) {
		this.reject_type = reject_type;
	}

	public String getMidValue() {
		return midValue;
	}

	public void setMidValue(String midValue) {
		this.midValue = midValue;
	}

	public String getSplit_data_model() {
		return split_data_model;
	}

	public void setSplit_data_model(String split_data_model) {
		this.split_data_model = split_data_model;
	}

	public String getNo_priv_ctrl() {
		return no_priv_ctrl;
	}

	public void setNo_priv_ctrl(String no_priv_ctrl) {
		this.no_priv_ctrl = no_priv_ctrl;
	}

	public ArrayList getCurrentFieldlist() {
		return currentFieldlist;
	}

	public void setCurrentFieldlist(ArrayList currentFieldlist) {
		this.currentFieldlist = currentFieldlist;
	}

	public String getAutoCaculate() {
		return autoCaculate;
	}

	public String getAttachmentcount() {
		return attachmentcount;
	}

	public void setAttachmentcount(String attachmentcount) {
		this.attachmentcount = attachmentcount;
	}

	public String getAttachmentAreaToType() {
		return attachmentAreaToType;
	}

	public void setAttachmentAreaToType(String attachmentAreaToType) {
		this.attachmentAreaToType = attachmentAreaToType;
	}

	public HashMap getDestination_a0100() {
		return destination_a0100;
	}

	public void setDestination_a0100(HashMap destination_a0100) {
		this.destination_a0100 = destination_a0100;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public String getHeadCount_control() {
		return headCount_control;
	}

	public void setHeadCount_control(String headCount_control) {
		this.headCount_control = headCount_control;
	}

	public String getPcOrMobile() {
		return pcOrMobile;
	}

	public void setPcOrMobile(String pcOrMobile) {
		this.pcOrMobile = pcOrMobile;
	}

	public String getImport_notice_data() {
		return import_notice_data;
	}

	public void setImport_notice_data(String import_notice_data) {
		this.import_notice_data = import_notice_data;
	}

	public boolean isDef_flow_self(int task_id) {
		try
		{
			if(this.tabid!=-1&& "1".equals(def_flow_self))
			{
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rset=null;
				if(task_id==0&&this.userview!=null)
				{
					rset=dao.search("select count(*) from t_wf_node_manual where  bs_flag='1' and  tabid="+this.tabid+" and create_user='"+this.userview.getUserName()+"'   and ins_id=-1");
					if(rset.next())
					{
						if(rset.getInt(1)==0) {
                            this.allow_defFlowSelf=false;
                        }
					}
				}
				else if(task_id>0)
				{
					rset=dao.search("select count(*) from t_wf_node_manual where tabid="+this.tabid+"  and ins_id=(select ins_id from t_wf_task where task_id="+task_id+")");
					if(rset.next())
					{
						if(rset.getInt(1)>0) {
                            this.allow_defFlowSelf=true;
                        } else {
                            this.allow_defFlowSelf=false;
                        }
					}

				}
				if(rset!=null) {
                    rset.close();
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return allow_defFlowSelf;
	}

    public String getDef_flow_self() {
        return def_flow_self;
    }

    public void setDef_flow_self(String def_flow_self) {
        this.def_flow_self = def_flow_self;
    }

	public boolean isAllow_defFlowSelf() {
		return allow_defFlowSelf;
	}
	public void setAllow_defFlowSelf(boolean allow_defFlowSelf) {
		this.allow_defFlowSelf = allow_defFlowSelf;
	}
    public boolean isHaveCalcItem() {
        return bHaveCalcItem;
    }

    public void setBHaveCalcItem(boolean haveCalcItem) {
        bHaveCalcItem = haveCalcItem;
    }

	public void setImpOthTableName(String impOthTableName) {
		this.impOthTableName = impOthTableName;
	}

	public void setComputeVar(boolean isComputeVar) {
		this.isComputeVar = isComputeVar;
	}
	/**
     *
     * @param tableName 要更新的表名
     * @param dao
     * @param keyvalue//更新后的主键值（E01A1、B0110）
     * @param selfId//原有的主键值（E01A1、B0110）
     * @param parent_id（如果更新K01,并且更改了岗位的上级，那么这个值是该岗位的新上级,对应的是e0122（部门值））
     * @return
     */
	public String copyRecordIntoTable(String tableName,ContentDAO dao,String keyvalue,String selfId,String parent_id){
	    StringBuffer sqlbuffer=new StringBuffer();
	    sqlbuffer.append("select * from ");
	    sqlbuffer.append(tableName+" where 1=2 ");
	    RowSet rs = null;
	    ResultSetMetaData rsmd=null;
	    StringBuffer sub = new StringBuffer();
	    String columsql="";
	    String[] columsqlArray=null;
	    try{
	        rs=dao.search(sqlbuffer.toString());
	        rsmd=rs.getMetaData();
	        int colCount = rsmd.getColumnCount();
	        for(int i=1;i<=colCount;i++){
	            String cellName= rsmd.getColumnName(i);
	            sub.append(cellName+",");
	        }
	        columsql=sub.toString();
	        if(columsql.length()>0){
	            columsql=columsql.substring(0, columsql.length()-1);
	            columsqlArray=columsql.split(",");
	        }

	        sqlbuffer.setLength(0);
	        sqlbuffer.append("insert into ");
	        sqlbuffer.append(tableName);
	        sqlbuffer.append("("+columsql+")");
	        sqlbuffer.append(" select ");
	        //sql="insert into "+tableName+"("+columsql+") values(";
	        for(int i=0;i<columsqlArray.length;i++){
	            if("ModTime".equalsIgnoreCase(columsqlArray[i])){
                    if(i==columsqlArray.length-1){
                        sqlbuffer.append(""+Sql_switcher.sqlNow()+"");
                    }else{
                        sqlbuffer.append(""+Sql_switcher.sqlNow()+",");
                    }
                    continue;
                }
	            if("ModUserName".equalsIgnoreCase(columsqlArray[i])){
                    if(i==columsqlArray.length-1){
                        sqlbuffer.append("'"+this.userview.getUserName()+"'");
                    }else{
                        sqlbuffer.append("'"+this.userview.getUserName()+"',");
                    }
                    continue;
                }

	            if(tableName.toUpperCase().startsWith("K")){
	               if("e01a1".equalsIgnoreCase(columsqlArray[i])){
	                   if(i==columsqlArray.length-1){
	                       sqlbuffer.append("'"+keyvalue+"'");
	                   }else{
	                       sqlbuffer.append("'"+keyvalue+"',");
	                   }
	                   continue;
	               }
	               if("e0122".equalsIgnoreCase(columsqlArray[i])){
	                   if(i==columsqlArray.length-1){
                           sqlbuffer.append("'"+parent_id+"'");
                       }else{
                           sqlbuffer.append("'"+parent_id+"',");
                       }
                       continue;
	               }
	            }
	            if(tableName.toUpperCase().startsWith("B")){
	                if("b0110".equalsIgnoreCase(columsqlArray[i])){
	                    if(i==columsqlArray.length-1){
	                        sqlbuffer.append("'"+keyvalue+"'");
	                    }else{
	                        sqlbuffer.append("'"+keyvalue+"',");
	                    }
	                    continue;
	                   }
	            }
	            if(i==columsqlArray.length-1){
	                sqlbuffer.append(columsqlArray[i]);
	            }else{
	                sqlbuffer.append(columsqlArray[i]+",");
	            }
	        }
	        sqlbuffer.append(" from "+tableName);
	        sqlbuffer.append(" where ");
	        if(tableName.toUpperCase().startsWith("K")){
	            sqlbuffer.append("e01a1='");
	        }else{
	            sqlbuffer.append("b0110='");
	        }
	        sqlbuffer.append(selfId+"'");
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    finally{
	        if(rs!=null){
	            try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
	        }
	    }
        return sqlbuffer.toString();
	}
	/*
	 * 判断是否是职称评审投票系统，此系统不使用ehr的用户，许多判断权限的地方需要特殊判断
	 * */
	public static boolean isJobtitleVoteModule(UserView view) {
		boolean b=false;
		if (view.getHm().get("moduleFlag")!=null){
			if ("jobtitleVote".equals((String)view.getHm().get("moduleFlag"))){
				b=true;
			}
		}
		return b;
	}
       /**
     * @Title: sendPersonToSap
     * @Description: 给sap发送审批数据
     * @param @param tab_id
     * @param @param ins_id
     * @param @throws GeneralException
     * @return void
     * @throws
    */
	public void sendDataToSAP(String tab_id,String ins_id) throws GeneralException{
        try {
            try {
            	this.log.debug("开始调用IPendingTask接口实现类com.hjsj.ludi.send.PendingTask--》pendingTask方法");
                Class aClass = Class.forName("com.hjsj.ludi.send.PendingTask");
                IPendingTask pt = (IPendingTask) aClass.newInstance();
                ContentDAO dao = new ContentDAO(this.conn);
                // 获取task_id
                String sql = "select * from t_wf_task where ins_id= " + ins_id + " and task_state=3";
                RowSet frowset = dao.search(sql);
                String task_id = "";
                if (frowset.next()) {
                    task_id = frowset.getString("task_id");
                } else {
                    return;
                }
                // 传递人员数据
                sql = " select * from templet_" + tab_id + " where ins_id =" + ins_id;
                frowset = dao.search(sql);
                while (frowset.next()) {
                    String nbase = frowset.getString("basepre");
                    String a0100 = frowset.getString("a0100");
                    String jsonStr = "{\"ins_id\":\"" + ins_id + "\",\"taskid\":\"" + task_id + "\",\"tabid\":\"" + tab_id + "\",\"object_id\":\"" + nbase + a0100 + "\"}";
                    this.log.debug("PendingTask-》pendingTask参数："+jsonStr);
                    pt.pendingTask(jsonStr);
                }

            } catch (Exception e) {
                this.log.debug("调用IPendingTask接口实现类com.hjsj.ludi.send.PendingTask出现异常"+e.getMessage());
            }
        } // try
        catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }
    }
	/**
	 * 查找模板中是否有多个相同变化后子集
	 */
	public void checkIsHaveMutilSub() throws GeneralException{
		StringBuffer sqlbuffer=new StringBuffer();
		RowSet frowset = null;
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList values = new ArrayList();
		try {
			sqlbuffer.append("select setname from Template_Set a,template_page  b where a.pageid=b.pageid and a.tabid=b.tabid  and a.setname in " );
			sqlbuffer.append("(select setname from Template_Set where tabid=? and subflag=1 and chgstate=2 group by setname having count(*) > 1) ");
			sqlbuffer.append("and a.tabid=? and a.subflag=1 and a.chgstate=2 and b.ismobile=0");
			values.add(this.tabid);
			values.add(this.tabid);
			frowset = dao.search(sqlbuffer.toString(), values);
			HashSet setnames = new HashSet();
			while (frowset.next()) {
                 String setname = frowset.getString("setname");
                 if(!setnames.contains(setname)) {
                     setnames.add(setname);
                 } else{
                	 String strInfo="模板中存在多个相同的变化后子集，请重新设计模板！";
                	 throw new GeneralException(strInfo);
                 }
            }
		}catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }
	}
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getSpAutoCaculate() {
		return spAutoCaculate;
	}
	/**
	 * 对选中任务对应的临时表的数据先迁移到起草的临时表然后进行删除templet_tabid
	 * @param username
	 * @param ins_id
	 * @param whl
	 * @param ishaverecall
	 * @return
	 * @throws GeneralException
	 */
	public boolean saveRecallTemplatedata(String username,int ins_id,String whl, String ishaverecall)throws GeneralException {
		boolean bflag=true;
		String destTab=this.getTemplateName(username, ins_id);
		String srcTab="templet_"+this.tabid;
		RowSet rset=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer strsql=new StringBuffer();
			ArrayList fieldlist=getAllFieldItem();
			strsql.append("select * from ");
			strsql.append(srcTab);
			strsql.append(" where ins_id="+ins_id);
			rset=dao.search(strsql.toString());

			ArrayList reclist=new ArrayList();
			HashMap recBigTextFieldMap=new HashMap();
			while(rset.next())
			{
				//如果存在数据先删除
				if("1".equals(ishaverecall)) {
                    this.deleteRecallData(destTab,rset);
                }
				RecordVo recvo=new RecordVo(destTab);
				recvo.setInt("state",rset.getInt("state"));
				recvo.setString("seqnum",rset.getString("seqnum"));
				if(this.infor_type==1)
				{
					recvo.setString("a0100",rset.getString("a0100"));
					recvo.setString("basepre",rset.getString("basepre"));
					recvo.setString("b0110_1",rset.getString("b0110_1"));
					recvo.setString("e0122_1",rset.getString("e0122_1"));
					recvo.setString("a0101_1",rset.getString("a0101_1"));
					recvo.setString("a0000",rset.getString("a0000"));
				}
				else
				{
					if(this.infor_type==2) {
                        recvo.setString("b0110",rset.getString("b0110"));
                    } else if(this.infor_type==3) {
                        recvo.setString("e01a1",rset.getString("e01a1"));
                    }

				}
				for(int i=0;i<fieldlist.size();i++)
				{
					FieldItem fielditem=(FieldItem)((FieldItem)fieldlist.get(i)).cloneItem();
					String field_name=fielditem.getItemid();
					if(StringUtils.isNotBlank(this.opinion_field)&&this.opinion_field.equalsIgnoreCase(field_name.trim())){//bug 38595 报批后撤回，审批意见没有清空，造成申请人审批意见显示多条
						continue;
					}
					if(field_name.toLowerCase().indexOf("attachment")>-1){//过滤附件
						String objectid = "";
						String basepre = "";
						String attachmentType = "0";
						if(this.infor_type==1){
							objectid = rset.getString("a0100");
							basepre = rset.getString("basepre");
						}else if(this.infor_type==2) {
                            objectid = rset.getString("b0110");
                        } else if(this.infor_type==3) {
                            objectid = rset.getString("e01a1");
                        }
						String [] itemarr = field_name.split("_");
						if(itemarr.length==2) {
                            attachmentType = itemarr[1];
                        }
						this.recallAttachment(ins_id,objectid,basepre,attachmentType);
						continue;
					}
					if("A00".equals(fielditem.getFieldsetid())/*&&(!(field_name.equalsIgnoreCase("photo")||field_name.equalsIgnoreCase("ext")))*/)
					{
						/**对人员调入才进行相应的操作*/
						if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
						{
							if("photo".equalsIgnoreCase(field_name)&&rset.getObject(field_name)!=null) {
                                recvo.setObject("photo", rset.getObject(field_name));
                            }
						}
						if("ext".equalsIgnoreCase(field_name)) {
                            recvo.setString("ext", rset.getString("ext"));
                        }
						if("signature".equalsIgnoreCase(field_name)){
							recvo.setString(field_name.toLowerCase(),Sql_switcher.readMemo(rset,field_name));
						}
						continue;
					}
					/*if(fielditem.isChangeAfter()&&this.opinion_field!=null&&this.opinion_field.length()>0&&this.opinion_field.equalsIgnoreCase(field_name))
					{
						String old_value=Sql_switcher.readMemo(rset,field_name.toLowerCase()+"_2");  //20150425 dengcan 通知单带过来的审批记录不能清空，得追加（汉口银行）
						//this.approve_opinion=old_value+"\r\n"+this.approve_opinion;
						String _approve_opinion=old_value+"\r\n"+this.approve_opinion;
						recvo.setString(field_name.toLowerCase()+"_2",_approve_opinion);
						continue;
					}*/
					if(fielditem.isChangeAfter()){
						field_name=field_name+"_2";
						//兼容新人事异动 存在多个变化后子集的情况。wangrd 20160826
						if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0){
							field_name=fielditem.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_2";
							DbWizard dbw = new DbWizard(this.conn);
							if (!dbw.isExistField(destTab, field_name)){
								field_name=fielditem.getItemid()+"_2";
							}
						}
					}
					else if(fielditem.isChangeBefore()){
						field_name=field_name+"_1";
						if(this.sub_domain_map!=null&&this.sub_domain_map.get(""+i)!=null&&this.sub_domain_map.get(""+i).toString().trim().length()>0){
							field_name=fielditem.getItemid()+"_"+this.sub_domain_map.get(""+i)+"_1";
							}
					}

					if(this.field_name_map!=null&&this.field_name_map.get(field_name.toLowerCase())!=null) {
                        fielditem.setItemtype("M");
                    }

					if("A".equalsIgnoreCase(fielditem.getItemtype())) {
                        recvo.setString(field_name.toLowerCase(),rset.getString(field_name));
                    } else if("N".equalsIgnoreCase(fielditem.getItemtype()))
					{
						if(rset.getString(field_name)!=null)
						{
							if(fielditem.getDecimalwidth()==0) {
                                recvo.setInt(field_name.toLowerCase(),rset.getInt(field_name));
                            } else {
                                recvo.setDouble(field_name.toLowerCase(),rset.getDouble(field_name));
                            }
						}

					}
					else if("M".equalsIgnoreCase(fielditem.getItemtype())){
						//recvo.setString(field_name.toLowerCase(),Sql_switcher.readMemo(rset,field_name));
						recBigTextFieldMap.put(field_name.toLowerCase(),field_name.toLowerCase());
					}
					else
					{
						if(Sql_switcher.searchDbServer()==2)
						{
							Timestamp ta=rset.getTimestamp(field_name);
							if(ta!=null)
							{
								Date d=new Date(ta.getTime());
								recvo.setDate(field_name.toLowerCase(),d);
							}
						}
						else {
                            recvo.setDate(field_name.toLowerCase(),rset.getDate(field_name));
                        }
					}
				}
				if(this.infor_type==2||this.infor_type==3) {
                    recvo.setString("codeitemdesc_1", rset.getString("codeitemdesc_1"));
                }
				if((this.infor_type==2||this.infor_type==3)&&(this.operationtype==8||this.operationtype==9)) {
                    recvo.setString("to_id", rset.getString("to_id"));
                }
				recvo.setInt("submitflag", 1);
				reclist.add(recvo);
			}
			dao.addValueObject(reclist);
			//liuyz 大文本段首空格被清除
			if(recBigTextFieldMap.size()>0){
				Iterator iterator  = recBigTextFieldMap.entrySet().iterator();
				DbWizard dbw=new DbWizard(this.conn);
				String srctab=srcTab;
				StringBuffer updateItem=new StringBuffer();
				boolean isHaveSignuare=false;
				while (iterator.hasNext()) {
					Entry entry = (Entry) iterator.next();
					if(entry.getKey().toString().toUpperCase().startsWith("S_")){
						if(!isHaveSignuare){
							isHaveSignuare=true;
							updateItem.append(destTab+".signature="+srctab+".signature`");
						}
					}else{
						updateItem.append(destTab+"."+entry.getKey()+"="+srctab+"."+entry.getKey()+"`");
					}
				}
				updateItem.setLength(updateItem.length()-1);
				if(this.infor_type==1){
					dbw.updateRecord(destTab,srctab ,destTab+".A0100="+srctab+".A0100",updateItem.toString(),
							destTab+".seqnum = (select seqnum from "+srctab+" where "+destTab+".A0100="+srctab+".A0100 and "+destTab+".basepre="+srctab+".basepre and "+srctab+".ins_id="+ins_id+")",srctab+".ins_id="+ins_id);
				} else if(this.infor_type==2){
					dbw.updateRecord(destTab,srctab ,destTab+".B0110="+srctab+".B0110",updateItem.toString(),destTab+".B0110=(select B0110 from "+srctab+" where "+destTab+".B0110="+srctab+".B0110 and "+srctab+".ins_id="+ins_id+")",srctab+".ins_id="+ins_id);
			    }else if(this.infor_type==3){
					dbw.updateRecord(destTab,srctab ,destTab+".e01a1="+srctab+".e01a1",updateItem.toString(),destTab+".e01a1=(select e01a1 from "+srctab+" where "+destTab+".e01a1="+srctab+".e01a1 and "+srctab+".ins_id="+ins_id+")",srctab+".ins_id="+ins_id);
				}

			}
			RecordVo srctabvo=new RecordVo(srcTab);
			RecordVo desttabvo=new RecordVo(destTab);
			if(Sql_switcher.searchDbServer()==Constant.ORACEL&&srctabvo.hasAttribute("photo")&&desttabvo.hasAttribute("photo"))
			{
			/**photo ,ext*/
				strsql.setLength(0);
				DbWizard dbw=new DbWizard(this.conn);
				String srctab=srcTab;
				dbw.updateRecord(destTab,srctab ,destTab+".A0100="+srctab+".A0100",destTab+".photo="+srctab+".photo"+"`"+destTab+".ext="+srctab+".ext",
						destTab+".seqnum = (select seqnum from "+srctab+" where "+destTab+".A0100="+srctab+".A0100 and "+destTab+".basepre="+srctab+".basepre and "+srctab+".ins_id="+ins_id+")",srctab+".ins_id="+ins_id);
			}
			/**清空以前的数据*/
			dao.update("delete from "+srcTab+" where ins_id="+ins_id);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			String message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{
				PubFunc.resolve8060(this.conn,destTab);
				throw GeneralExceptionHandler.Handle(new Exception("请重新执行报批操作!"));
			}
			else {
                throw GeneralExceptionHandler.Handle(ex);
            }
		}finally{
			PubFunc.closeDbObj(rset);
		}
		return bflag;
	}
	/**
	 * 将附件内容撤回到起草状态
	 * @param ins_id
	 * @param objectid
	 * @param basepre
	 * @param attachmentType
	 */
	private void recallAttachment(int ins_id, String objectid, String basepre, String attachmentType) throws GeneralException {
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sb = new StringBuffer("");
			ArrayList paramlist = new ArrayList();
			sb.append("update t_wf_file set ins_id=0 where ins_id=? and objectid=? ");
			paramlist.add(ins_id);
			paramlist.add(objectid);
			if(!"".equals(basepre)){
				sb.append(" and lower(basepre)=? ");
				paramlist.add(basepre.toLowerCase());
			}
			sb.append(" and attachmenttype=? and tabid=?");
			paramlist.add(attachmentType);
			paramlist.add(this.tabid);
			dao.update(sb.toString(), paramlist);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 如果存在数据先删除
	 * @param destTab
	 * @param rset
	 * @throws GeneralException
	 */
	private void deleteRecallData(String destTab, RowSet rset) throws GeneralException {
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			if(this.infor_type==1)
			{
				String a0100 = rset.getString("a0100");
				String basepre =rset.getString("basepre");
				dao.delete("delete from "+destTab+" where a0100='"+a0100+"' and lower(basepre)='"+basepre.toLowerCase()+"'", new ArrayList());
			}
			else
			{
				if(this.infor_type==2){
					String b0110=rset.getString("b0110");
					dao.delete("delete from "+destTab+" where b0110='"+b0110+"'", new ArrayList());
				}else if(this.infor_type==3){
					String e01a1=rset.getString("e01a1");
					dao.delete("delete from "+destTab+" where e01a1='"+e01a1+"'", new ArrayList());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 判断此ins_id对应的人员或者单位在起草临时表中是否存在正在起草的人
	 * @param username
	 * @param ins_id
	 * @return
	 * @throws GeneralException
	 */
	public String getRecallStartTask(String username, int ins_id) throws GeneralException {
		String recallname = "";
		String destTab=this.getTemplateName(username, ins_id);

		String srcTab="templet_"+this.tabid;
		RowSet rset=null;
		try
		{
			StringBuffer strsql=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			strsql.append("select t2.name from "+srcTab+" t,"+destTab+" t1,template_table t2 where ");
			if(this.infor_type==1) {
                strsql.append("t.A0100=t1.a0100 and t.BasePre=t1.BasePre ");
            } else{
				if(this.infor_type==2){
					strsql.append("t.B0110=t1.B0110 ");
				}else if(this.infor_type==3){
					strsql.append("t.E01A1=t1.E01A1 ");
				}
			}
			strsql.append(" and t2.tabid="+this.tabid);
			strsql.append(" and t.ins_id="+ins_id);
			rset=dao.search(strsql.toString());
			if(rset.next())
			{
				String name_ = rset.getString("name");
				recallname=name_;
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rset);
		}
		return recallname;
	}
	private String getTemplateName(String username,int ins_id) throws GeneralException {
		String destTab=username+"templet_"+this.tabid;
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo vo = new RecordVo("t_wf_instance");
			vo.setInt("ins_id", ins_id);
			vo=dao.findByPrimaryKey(vo);
			int bfile=vo.getInt("bfile");
			if(bfile==3) {
                destTab="g_templet_"+tabid;
            } else if(bfile==0||bfile==1) {//兼容以前旧数据
				int actor_type=vo.getInt("actor_type");
				if(actor_type==1)//员工通过自助平台发动申请
                {
                    destTab="g_templet_"+tabid;
                }
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return destTab;
	}
	/**
	 * 校验机构下是否有人员
	 * @param sql
	 * @param taskid 预留需求流程中校验参数 不需要传递空
	 */
	public String checkIsHavePerson(String srcsql, String taskid) throws GeneralException{
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rowSet = null;
		RowSet rowSet1 = null;
		String havePerson = "";
		try {
			String strpres_real = "";//系统设置的认证库
			RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
			if (login_vo != null) {
				strpres_real = login_vo.getString("str_value");//认证库
			}
			if(!"".equals(strpres_real)) {
				//此机构在认证库下是否还有人员
				String [] strpres_realarr = strpres_real.split(",");
				rowSet=dao.search(srcsql);
				while(rowSet.next()) {
					String code = "";
					StringBuffer searchsql = new StringBuffer("");
					String codedesc = rowSet.getString("codeitemdesc_1");
					if(this.infor_type==2) {//单位
						code = rowSet.getString("b0110");
						for(int i=0;i<strpres_realarr.length;i++){
							if(i==0) {
                                searchsql.append("select count(1) num from "+strpres_realarr[i].toLowerCase()+"a01 where b0110 like '"+code+"%' or e0122 like '"+code+"%' ");
                            } else {
                                searchsql.append(" union all select count(1) num from "+strpres_realarr[i].toLowerCase()+"a01 where b0110 like '"+code+"%' or e0122 like '"+code+"%' ");
                            }
						}
					}else if(this.infor_type==3) {//职位
						code = rowSet.getString("e01a1");
						for(int i=0;i<strpres_realarr.length;i++){
							if(i==0) {
                                searchsql.append("select count(1) num from "+strpres_realarr[i].toLowerCase()+"a01 where e01a1 like '"+code+"%' ");
                            } else {
                                searchsql.append(" union all select count(1) num from "+strpres_realarr[i].toLowerCase()+"a01 where e01a1 like '"+code+"%' ");
                            }
						}
					}
					rowSet1 = dao.search(searchsql.toString());
					while(rowSet1.next()) {
						int num = rowSet1.getInt("num");
						if(num>0) {
							havePerson+=codedesc+",";
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
			PubFunc.closeDbObj(rowSet1);
		}
		if(!"".equals(havePerson)) {
            havePerson = havePerson.substring(0,havePerson.length()-1);
        }
		return havePerson;
	}
	/**
	 *从档案库中更新照片
	 **/
	//bug 35130 员工自助业务申请上的照片更新问题
	private void impPhotoFromArchive2(String a0100s,String dbpre) throws GeneralException
	{

		boolean bflag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			boolean bphoto=false;
			StringBuffer strsql=new StringBuffer();
			String tablename=null;

			if(this.isBEmploy()) {
                tablename="g_templet_"+this.tabid;
            } else {
                tablename=this.userview.getUserName()+"templet_"+this.tabid;
            }
			if(this.task_id!=0) {
                tablename = "templet_"+this.tabid;
            }

			if(this.impOthTableName!=null&&this.impOthTableName.trim().length()>0)  //供高级花名册调用人事异动的人员引入功能，将数据导入到临时表中
            {
                tablename=this.impOthTableName;
            }

			ArrayList fieldlist=getAllFieldItem();
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem fielditem= (FieldItem) fieldlist.get(i);
				if("photo".equals(fielditem.getItemid()))
				{
					bphoto=true;
					break;
				}
			}
			/**不导入照片信息，除人员调入操作外，全都从档案记录中取得照片  || linbz 26542  刷新的时候也同步*/
			if(bphoto)
			{
				/**photo ,ext*/
				strsql.setLength(0);
				DbWizard dbw=new DbWizard(this.conn);
				int dbflag = Sql_switcher.searchDbServer();
				if(this.infor_type==1) //人员信息处理
				{
					//liuyz 人员头像更新 原来有的刷新为原来的，原来没有的不刷新。
					String srctab=dbpre+"A00";
					StringBuilder sql=new StringBuilder();
					String dbProductName;
			        dbProductName = conn.getMetaData().getDatabaseProductName();
			        if(dbProductName != null){
			            dbProductName = dbProductName.toLowerCase();
			            if(dbProductName.indexOf("oracle") != -1) {
                            dbflag = 2;
                        } else
			            if(dbProductName.indexOf("oscar") != -1) {
                            dbflag = 6;
                        } else
			            if(dbProductName.indexOf("db2") != -1) {
                            dbflag = 3;
                        } else
			            if(dbProductName.indexOf("kunlun") != -1) {
                            dbflag = 7;
                        } else {
                            dbflag = 1;
                        }
			        switch(dbflag)
			        {
				        case 2: // '\002'
			            case 3: // '\003'
			            case 5: // '\005'
			            case 6: // '\006'
			            case 7: // '\007'
			            {
							sql.append("update ");
							sql.append(tablename);
							sql.append(" set (");
							sql.append(" photo,").append(tablename).append(".ext ");
							sql.append(")=(select ");
							sql.append(" ole, ").append(srctab).append(".ext ");
							sql.append(" from ");
							sql.append(srctab);
							sql.append(" where ");
							sql.append(tablename+".A0100="+srctab+".A0100");
			                sql.append(" and ");
			                sql.append(srctab+".flag='P' ");
			                sql.append(")");
			                sql.append(" where ");
			                sql.append(tablename).append(".a0100 in ( select ").append(srctab).append(".a0100 from ").append(srctab).append(" left join ").append(tablename).append(" on ").append(tablename).append(".A0100=").append(srctab).append(".A0100 where ").append(tablename).append(".a0100 in (").append(a0100s).append(") and ").append(tablename).append(".basepre='"+dbpre+"') and basepre='").append(dbpre).append("'");
							break;
			            }
			            case 4: // '\004'
			            default:
			            {
			            	String strSet = ("photo=ole`"+tablename+".ext="+srctab+".ext").replace('`', ',');
							sql.append("update ");
							sql.append(tablename);
			                String strLeft = " left join " + srctab + " on " + tablename+".A0100="+srctab+".A0100";
			                String strUpdate = " set " + strSet;
			                String strFrom = " from " + tablename;
			                sql.append(strUpdate);
			                sql.append(strFrom);
			                sql.append(strLeft);
			                sql.append(" where ");
			                sql.append(srctab+".flag='P'");
			                sql.append(" and ");
			                sql.append(tablename).append(".a0100 in ( select ").append(srctab).append(".a0100 from ").append(srctab).append(" left join ").append(tablename).append(" on ").append(tablename).append(".A0100=").append(srctab).append(".A0100 where ").append(tablename).append(".a0100 in (").append(a0100s).append(") and ").append(tablename).append(".basepre='"+dbpre+"') and basepre='").append(dbpre).append("'");
			            }
			        }
					dao.update(sql.toString());
			        }
					//dbw.updateRecord(tablename,srctab ,tablename+".A0100="+srctab+".A0100","photo=ole`"+tablename+".ext="+srctab+".ext",tablename+".a0100 in ("+a0100s+") and basepre='"+dbpre+"'",srctab+".flag='P'");
				}
				else if(this.infor_type==2) //单位信息处理
				{
					String srctab="B00";
					dbw.updateRecord(tablename,srctab ,tablename+".B0110="+srctab+".B0110","photo=ole`"+tablename+".ext="+srctab+".ext",tablename+".B0110 in ("+a0100s+")  ",srctab+".flag='P'");
				}
				else if(this.infor_type==3) //职位信息处理
				{
					String srctab="K00";
					dbw.updateRecord(tablename,srctab ,tablename+".E01A1="+srctab+".E01A1","photo=ole`"+tablename+".ext="+srctab+".ext",tablename+".E01A1 in ("+a0100s+")  ",srctab+".flag='P'");
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 判断是否符合条件更新的条件。
	 * @param subctrl
	 * @param srctab
	 * @param key_field 单位、部门关键字 this.infor_type==2或this.infor_type==3时需要传递
	 * @param keyValue  单位、部门的值     this.infor_type==2或this.infor_type==3时需要传递
	 * @param a0100               人员编号                 this.infor_type==1时需要传递
	 * @param srcbase   人员库前缀             this.infor_type==1时需要传递
	 * @param setname
	 * @return
	 */
	private Integer addNewByCondFormula(TSubsetCtrl subctrl,String srctab,String key_field,String keyValue,String a0100,String srcbase,String setname){
		Integer num=0;
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rowSet=null;
		try{
			String cond_str="";
			String condFormula=subctrl.getCondFormula();
			if(condFormula==null||condFormula.trim().length()==0)
			{
				cond_str=" and ( 1=1 ) ";
			}
			else
			{
				YksjParser yp = new YksjParser( this.userview ,getCondUpdateFieldList(setname),
						YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");

				yp.run_where(condFormula);
				String strfilter=yp.getSQL();
				if(strfilter.length()>0) {
                    cond_str=" and ("+strfilter+") ";
                }
			}
			String sql="";
			if(this.infor_type==2||this.infor_type==3){
			if(srctab.equalsIgnoreCase("g_templet_"+this.tabid)) {
                sql="select count(1) as num from "+srctab+" where  "+key_field+"='"+keyValue+"' "+cond_str;
            } else if(srctab.equalsIgnoreCase("templet_"+this.tabid)) {
                sql="select count(1) as num from "+srctab+" where ins_id="+this.ins_id+" and "+key_field+"='"+keyValue+"' "+cond_str;
            } else {
                sql="select count(1) as num from "+srctab+" where  "+key_field+"='"+keyValue+"' "+cond_str;
            }
			}else if(this.infor_type==1){
				if(srctab.equalsIgnoreCase("g_templet_"+this.tabid)) {
                    sql="select count(1) as num from "+srctab+" left join "+srcbase+setname+" on "+srcbase+setname+".a0100="+srctab+".a0100  where  "+srctab+".a0100='"+a0100+"' and lower(basepre)='"+srcbase.toLowerCase()+"' "+cond_str;
                } else if(srctab.equalsIgnoreCase("templet_"+this.tabid)) {
                    sql="select count(1) as num from "+srctab+" left join "+srcbase+setname+" on "+srcbase+setname+".a0100="+srctab+".a0100 where ins_id="+this.ins_id+" and "+srctab+".a0100='"+a0100+"' and lower(basepre)='"+srcbase.toLowerCase()+"'  "+cond_str;
                } else {
                    sql="select count(1) as num from "+srctab+" left join "+srcbase+setname+" on "+srcbase+setname+".a0100="+srctab+".a0100 where  "+srctab+".a0100='"+a0100+"' and lower(basepre)='"+srcbase.toLowerCase()+"' "+cond_str;
                }
			}
			rowSet=dao.search(sql);
			if(rowSet.next()){
				num=rowSet.getInt("num");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			PubFunc.closeDbObj(rowSet);
		}
		return num;
	}
	 private String getRoleArr(UserView userView) {
			ArrayList rolelist= userView.getRolelist();//角色列表
		 	StringBuffer strrole=new StringBuffer();
		 	for(int i=0;i<rolelist.size();i++)
		 	{
		 		strrole.append("'");
		 		strrole.append((String)rolelist.get(i));
		 		strrole.append("'");
	 			strrole.append(",");	 		
		 	}
		 	if(rolelist.size()>0)
		 	{
		 		strrole.setLength(strrole.length()-1);
		 	}
			return strrole.toString();
		}
}
