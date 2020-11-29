/**
 * 
 */
package com.hjsj.hrms.module.template.utils;

import com.hjsj.hrms.businessobject.general.template.*;
import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardBo;
import com.hjsj.hrms.businessobject.infor.BaseInfoBo;
import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.template.templatecard.businessobject.AttachmentBo;
import com.hjsj.hrms.module.template.templatecard.businessobject.TempletChgLogBo;
import com.hjsj.hrms.module.template.utils.javabean.*;
import com.hjsj.hrms.utils.H2JdbcUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.CreateSequence;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.VfsFileEntity;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * <p>TemplateBo</p>
 * <p>Description>: 代替templatetablebo中的流程相关的方法</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-11-20 上午10:37:39</p>
 * <p>@version: 7.0</p>
 */
public class TemplateBo {
	private Connection conn;
	private UserView userView;	
    /**当前使用的模板号*/
    private int tabId;
    private TemplateParam paramBo = null;
    private ContentDAO dao; 
    private TemplateUtilBo utilBo= null;
    private TemplateDataBo dataBo= null;
    private DbWizard dbw=null;
    
    private boolean isThrow = true; //是否抛出异常
    private String moduleId="1"; //模块号 参考 templateFrontProperty.java
    private String taskId="0"; //任务号 参考 templateFrontProperty.java
    private String impOthTableName="";  //将人员导入到其它临时表中，高级花名册调用
    private boolean onlyComputeFieldVar=false;  //只计算模板指标中引入的临时变量
    private String bz_tablename; //与用户名有关的变动临时表的名称,当然对审批也可计算
    private boolean isComputeVar=false; //是否已经计算过临时变量 
    private ArrayList midVarFieldlist= null;  //缓存临时变量列表 导入人员使用
    private HashMap bigMemoFieldMap =null;//存储人为改变的大文本字段 只做缓存使用。
    private ArrayList h2Table = new ArrayList();//存储子集计算创建的临时表
    private String insid="0"; //流程号
    
    private String insid_pro="";  //批量计算存储过程函数时，将此内容替换 流程号
    private boolean isRepreateSubmit=false;//结束单据重复提交
    public boolean isRepreateSubmit() {
		return isRepreateSubmit;
	}
	public void setRepreateSubmit(boolean isRepreateSubmit) {
		this.isRepreateSubmit = isRepreateSubmit;
	}
	/**
	 * 初始化构造函数 tabid
	 */
    public TemplateBo (Connection conn,UserView userview,int tabid){ 
        this.tabId=tabid;	                         
        this.paramBo = new TemplateParam(conn, userview, tabid);
        init(conn,userview);
    }
    /**
	 * 初始化构造函数 传递TemplateParam类，不用新创建了
	 */
    public TemplateBo (Connection conn,UserView userview,TemplateParam param){                       
    	this.paramBo = param;
    	this.tabId=param.getTabId();
    	init(conn,userview);
    }
    /**
	 * 初始化本来，创建一些公共类
	 */ 
    private void init(Connection conn,UserView userview){
    	this.conn = conn;
    	this.userView = userview;
    	dao = new ContentDAO(this.conn);                        
    	utilBo= new TemplateUtilBo(this.conn,this.userView);
    	dataBo= new TemplateDataBo(this.conn,this.userView,this.paramBo);
    	dbw=new DbWizard(this.conn);
    }
    
    /**
     * 返回模板中所有的指标项(变量、子集区域)列表
     * @return 列表中存放的是TemplateItem对象
     */
    public ArrayList getAllTemplateItem() throws GeneralException
    {
        return dataBo.getAllTemplateItem(false);
    }    
    
    /**
     * 返回模板中所有的指标项(变量、子集区域)列表
     * @return 列表中存放的是FieldItem对象
     */
    public ArrayList getAllFieldItem()throws GeneralException
    {
        return dataBo.getAllFieldItem();
    }
    
	
    /**
	 * 判断模板中包含了关联序号的变化后指标
	 */
	public boolean hasSequenceFieldItem()throws GeneralException
	{
		boolean b=false;
		ArrayList fieldlist=getAllTemplateItem();
		try
		{
			for(int i=0;i<fieldlist.size();i++)
			{
				TemplateItem item=(TemplateItem)fieldlist.get(i);
				if(item.getFieldItem().isChangeAfter())
				{
					if(item.getFieldItem().isSequenceable()){
						b=true;
						break;
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return b;
	}
	
	public boolean isDef_flow_self(int task_id) {
		boolean allow_defFlowSelf=this.paramBo.isAllow_defFlowSelf();
		try
		{
			if(this.tabId!=-1&& "1".equals(this.paramBo.getDef_flow_self()))
			{
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rset=null;
				if(task_id==0&&this.userView!=null)
				{ 
					rset=dao.search("select count(*) from t_wf_node_manual where  bs_flag='1' and  tabid="+this.tabId+" and create_user='"+this.userView.getUserName()+"'   and ins_id=-1");
					if(rset.next())
					{
						if(rset.getInt(1)==0)
							allow_defFlowSelf=false;
					} 
				} 
				else if(task_id>0)
				{
					rset=dao.search("select count(*) from t_wf_node_manual where tabid="+this.tabId+"  and ins_id=(select ins_id from t_wf_task where task_id="+task_id+")");
					if(rset.next())
					{
						if(rset.getInt(1)>0)
							allow_defFlowSelf=true;
						else
							allow_defFlowSelf=false;
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
	
	/**
	 * 分析此实例，是否为当前用户发起的申请
	 * @param ins_id
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
	    int sp_mode=paramBo.getSp_mode();
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
				String a0100=this.userView.getDbname()+this.userView.getA0100();
				String usrname=this.userView.getUserId();
				if(applyobj!=null&&(applyobj.equalsIgnoreCase(a0100)||applyobj.equalsIgnoreCase(usrname)))
					startflag="1";
				if ("1".equals(this.paramBo.getDef_flow_self())){
                    //startflag="0";//自定义审批流程 在流程中已经不是发起人
					if (this.paramBo.isDef_flow_self(Integer.parseInt(task_id))){
						String sql="select 1 from t_wf_task t,t_wf_node_manual m where t.ins_id=m.ins_id and t.node_id = m.id and t.task_id = "+task_id+"";
	                    rset=dao.search(sql);
	                    if(rset.next())
	                    	startflag="0";
	                    else
	                    	startflag="1";
                    }
                }
				if("1".equals(startflag)&&task_id!=null&&!"0".equals(task_id)&&task_id.trim().length()>0)
				{
					if(sp_mode==0){//自动
						rset=dao.search("select nodetype from t_wf_node where tabid="+this.tabId+" and node_id=(select node_id from t_wf_task where task_id="+task_id+")");
						if(rset.next())
						{
							 
							if(!"1".equals(rset.getString("nodetype"))&&!"9".equals(rset.getString("nodetype")))
								startflag="0";
						}
					}
					if(sp_mode==1&&(!"1".equals(this.paramBo.getDef_flow_self())||!this.paramBo.isDef_flow_self(Integer.parseInt(task_id)))){//手工非自定义或者是自定义没设置审批流程
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
								if(rset.getString(1)!=null)
									pri_task_id=rset.getInt(1);
							}
							if(pri_task_id!=0)
								startflag="0";
						}else{//报批的
							//当前任务节点是否是起草
							rset=dao.search("select nodetype from t_wf_node where tabid="+this.tabId+" and node_id=(select node_id from t_wf_task where task_id="+task_id+")");
							if(rset.next())
							{
								if(!"1".equals(rset.getString("nodetype"))&&!"9".equals(rset.getString("nodetype")))
									startflag="0";
							}
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
	    * @Title: getTaskState 
	    * @Description:获取任务状态 
	    * @param @param task_id
	    * @param @return
	    * @return String
	    */ 
	    public String getTaskState(String task_id)
	    {
	        String task_state="-1";
	        RowSet rowSet=null;
	        try
	        {
	            ContentDAO dao=new ContentDAO(this.conn);
	            rowSet=dao.search("select task_state from t_wf_task where task_id="+task_id);
	            if(rowSet.next())
	                task_state=rowSet.getString(1);
	        }
	        catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	        finally{
	            PubFunc.closeResource(rowSet);
	        }
	        return task_state;
	    }
	   
    /**
     * 判断是否是已结束的单据
     * @param task_id
     * @return
     */
    public boolean isFinishedTask(String task_id)
    {
        boolean bFinished=false;
        try
        {
        	String[] taskids = task_id.split(",");
        	for(int i = 0;i<taskids.length;i++){
        		String taskState=getTaskState(taskids[i]);
        		if ("4".equals(taskState)||"5".equals(taskState)||"6".equals(taskState)){//结束
        			bFinished=true;
        		}
        		break;
        	}
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally{
        }
        
        return bFinished;
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
			buf.append(this.tabId);
			buf.append(" and subflag=1 and flag in ('A','B','K')");
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				TemplateSet setbo=new TemplateSet();
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
				if(Sql_switcher.searchDbServer()==2)
					setbo.setMode(rset.getInt("Mode_o"));
				else
					setbo.setMode(rset.getInt("Mode"));
				setbo.setNsort(rset.getInt("nSort"));
				setbo.setR(rset.getInt("R"));
				setbo.setT(rset.getInt("T"));
				setbo.setRcount(rset.getInt("Rcount"));
				setbo.setRheight(rset.getInt("RHeight"));
				setbo.setRleft(rset.getInt("RLeft"));
				setbo.setRwidth(rset.getInt("RWidth"));
				setbo.setRtop(rset.getInt("RTop"));
				temp=rset.getString("subflag");
				if(temp==null|| "".equals(temp)|| "0".equals(temp))
					setbo.setSubflag(false);
				else
					setbo.setSubflag(true);
				if(rset.getInt("yneed")==0)
					setbo.setYneed(false);
				else
					setbo.setYneed(true);
				setbo.setXml_param(Sql_switcher.readMemo(rset,"sub_domain"));
				map.put("t_"+setbo.getSetname()+"_"+setbo.getChgstate(),setbo);					
			}//while loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return map;
	}
	/**
	 * 对于调入模板中自动生成序号
	 */
	public void filloutSequence(String a0100s,String dbpre,String tabname,String ins_id)throws GeneralException
	{
		int i=0;
		ArrayList fieldList=getAllTemplateItem();
		HashMap seqHm=new HashMap();
		try
		{
			for(i=0;i<fieldList.size();i++)
			{
				TemplateItem temItem=(TemplateItem)fieldList.get(i);
				FieldItem item=temItem.getFieldItem();
				if(item.isChangeAfter())
				{
					if(item.isSequenceable())
							seqHm.put(item.getItemid().toString(),item.getSequencename());					
				}
			}
			/**生成序号*/
			createRuleSequenceNo(seqHm,a0100s,dbpre,tabname,ins_id);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
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
            
            if((","+tab_id+",").indexOf(","+this.tabId+",")==-1){ return; }
       //   if (Integer.parseInt(tab_id)!=this.tabId){return;}
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
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	/**
	 * 求实际的业务数,本次模板做了多少人的业务
	 * @return
	 * @throws GeneralException 
	 */
	public String getRecordBusiTopic(int task_id,int ins_id) throws GeneralException
	{
		int nmax=0;
		StringBuffer stopic=new StringBuffer();
		RecordVo tbvo=readTemplate();
		stopic.append(tbvo.getString("name")+"");
		stopic.append("(");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer strsql=new StringBuffer();
			String tabname="templet_"+this.tabId; 
			String a0101="a0101_1";
			RecordVo vo=new RecordVo(tabname);
			if(this.paramBo.getOperationType()==0&&vo.hasAttribute("a0101_2"))//调入
			{
					a0101="a0101_2";
			}
			
			if(this.paramBo.getInfor_type()==2||this.paramBo.getInfor_type()==3)
			{
				a0101="codeitemdesc_1";
				if(this.paramBo.getOperationType()==5)
					a0101="codeitemdesc_2";
			}
			String strWhere=" where ";
			if(ins_id!=0)
				strWhere+=" ins_id="+ins_id+"  and ";
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
				if(i>4)
					break;
				if(i!=0)
					stopic.append(",");
				stopic.append(rset.getString(a0101)==null?"":rset.getString(a0101));
				i++;
			}
			strsql.setLength(0);

			strsql.append("select count(*) as nmax from ");
			strsql.append(tabname);
			strsql.append(strWhere.toString());
		
			rset=dao.search(strsql.toString());
			if(rset.next())
				nmax=rset.getInt("nmax");
			//if(nmax!=i)
			stopic.append(",");
			stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
			//stopic.append(ResourceFactory.getProperty("hmuster.label.total"));			
			stopic.append(nmax);
			if(this.paramBo.getInfor_type()==1)
				stopic.append("人)");
			else
				stopic.append("条记录)");
		}
		catch(Exception ex)
		{
			return stopic.toString();
		}
		return stopic.toString();
	}
	/**
	 * 取得业务模板的内容
	 * @param tabid
	 * @return
	 * @throws GeneralException
	 */
	private RecordVo readTemplate()throws GeneralException
	{
		 
		return TemplateUtilBo.getTableVo(this.tabId, conn);
	}
	/** todo delete
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
			buf.append(this.tabId);
			buf.append(" and (hismode=2 or hismode=3 or hismode=4) and chgstate=1 and flag in ('A','B','K')"); 
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				TemplateSet setbo=new TemplateSet();
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
				if(Sql_switcher.searchDbServer()==2)
					setbo.setMode(rset.getInt("Mode_o"));
				else
					setbo.setMode(rset.getInt("Mode"));
				setbo.setNsort(rset.getInt("nSort"));
				setbo.setR(rset.getInt("R"));
				setbo.setT(rset.getInt("T"));
				setbo.setRcount(rset.getInt("Rcount"));
				setbo.setRheight(rset.getInt("RHeight"));
				setbo.setRleft(rset.getInt("RLeft"));
				setbo.setRwidth(rset.getInt("RWidth"));
				setbo.setRtop(rset.getInt("RTop"));
				temp=rset.getString("subflag");
				if(temp==null|| "".equals(temp)|| "0".equals(temp))
					setbo.setSubflag(false);
				else
					setbo.setSubflag(true);
				if(rset.getInt("yneed")==0)
					setbo.setYneed(false);
				else
					setbo.setYneed(true);
				setbo.setXml_param(Sql_switcher.readMemo(rset,"sub_domain"));
				//setbo.setUserview(this.userView);
				if(setbo.getField_name()==null)
					continue;
				FieldItem item=DataDictionary.getFieldItem(setbo.getField_name());
				if(item!=null)
				{
					/**可以增加模板指标与字典表指标进行校验*/
					FieldItem tempitem=(FieldItem)item.cloneItem();
					tempitem.setNChgstate(setbo.getChgstate());
					map.put(item.getItemid()+"_"+setbo.getChgstate(),setbo);					
				}				
			}//while loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return map;
	}
	/**todo delete
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
			buf.append("select * from template_set where ( subflag=0 or subflag is null ) and tabid="+this.tabId+" and chgstate=2 ");
			buf.append(" and flag in ('A','B','K')");
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				
				if(rset.getString("Field_name")==null)
					continue;
				FieldItem item=DataDictionary.getFieldItem(rset.getString("Field_name"));
				if(item!=null)
				{
						map.put(item.getItemid()+"_2","1");
				}				
			}//while loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return map;
	}
	/**todo delete
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
			buf.append("select * from template_set where ( subflag=0 or subflag is null ) and tabid="+this.tabId+" and chgstate=2 ");
			buf.append(" and flag in ('A','B','K')");
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				
				if(rset.getString("Field_name")==null)
					continue;
				FieldItem item=DataDictionary.getFieldItem(rset.getString("Field_name"));
				if(item!=null)
				{
					if(isSelfApply())//员工通过自助平台发动申请
					{
						if(!"2".equals(this.userView.analyseFieldPriv(item.getItemid(),0))&& "0".equals(this.paramBo.getUnrestrictedMenuPriv_Input()))
							map.put(item.getItemid()+"_2","1");
					}
					else
					{
						if(!"2".equals(this.userView.analyseFieldPriv(item.getItemid()))&& "0".equals(this.paramBo.getUnrestrictedMenuPriv_Input()))
						{
							map.put(item.getItemid()+"_2","1");
//							if(this.userView.analyseFieldPriv(item.getItemid(),0).equals("2")&&this.UnrestrictedMenuPriv_Input.equals("0"))
//								map.remove(item.getItemid()+"_2");
						}
					}			
				}				
			}//while loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return map;
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
			buf.append(this.tabId);
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
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
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
				if(isSelfApply())
				{
					setBz_tablename("g_templet_"+this.tabId);
					strwhere+=" where  "+getBz_tablename()+".a0100='"+this.userView.getA0100()+"' and lower("+getBz_tablename()+".basepre)='"+this.userView.getDbname().toLowerCase()+"'" ;
					 
				}
				else
					setBz_tablename(this.userView.getUserName()+"templet_"+this.tabId);
			}
			else
			{
				setBz_tablename("templet_"+this.tabId);
				strwhere=getWhereSQL(ins_id);
			}
			 
			/**计算临时变量,把临时变量加到变动处理表中去*/
			/**应用库前缀*/
			
			Object[] dbarr=null;
			if(this.paramBo.getInfor_type()==1)
			{
				ArrayList dblist=searchDBPreList(getBz_tablename());
				dbarr=dblist.toArray();			
			}
			if(!isComputeVar())
			{
				if(this.paramBo.getInfor_type()==1)
				{
					ArrayList fieldlist=getMidVariableListByFunc(1);//getMidVariableList();
					addMidVarIntoGzTable(strwhere,dbarr,fieldlist);
				}
				if(this.paramBo.getInfor_type()==2)//基于组织的计算也加临时变量的处理  zhaoxg add 2014-1-13
				{
					ArrayList fieldlist=getMidVariableListByFunc(1);//getMidVariableList();
					addMidVarIntoGzTable(strwhere,dbarr,fieldlist);
				}
				if(this.paramBo.getInfor_type()==3)//基于职位的计算也加临时变量的处理  zhaoxg add 2014-1-13
				{
					ArrayList fieldlist=getMidVariableListByFunc(1);//getMidVariableList();
					addMidVarIntoGzTable(strwhere,dbarr,fieldlist);
				}
			}
			
			/**处理标准表涉及的指标*/
			if(this.paramBo.getInfor_type()==1)
			{
				addStdFieldIntoGzTable(strwhere.replaceAll("where"," "),dbarr);
				/**执行薪资标准*/
				batchCalcGzStandard(strwhere);
			}
			/**执行子集计算*/
			calSubFormula(strwhere);
			/**执行计算公式*/
			calcGzFormula(strwhere);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			
			String message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{
				PubFunc.resolve8060(this.conn,getBz_tablename());
				throw GeneralExceptionHandler.Handle(new Exception("请重新执行计算操作!"));
			}
			else
				throw GeneralExceptionHandler.Handle(ex);
			 
		}
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
			if(rowSet.next())
				flag=true;
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
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
			ArrayList templateItemList=getAllTemplateItem();
			boolean isE0122_2=false;
			boolean isE01A1_2=false;
			
			//计算政策依据  
			boolean executeZCYJ=false; //执行政策依据
			//得到记录变动日志相关设置参数
			ArrayList cellList = new ArrayList();
			Boolean isAotuLog = paramBo.getIsAotuLog();
			Boolean isRejectAotuLog = paramBo.getIsRejectAotuLog();
			if(isRejectAotuLog==true&&!"0".equalsIgnoreCase(this.insid)){
				Boolean haveReject= utilBo.isHaveRejectTaskByInsId(this.insid);
				if(haveReject){
					isAotuLog=true;
				}
			}
			if (isAotuLog && !("0".equalsIgnoreCase(this.insid)
					&& (paramBo.getOperationType() == 0|| paramBo.getOperationType() == 5))) {
				cellList= utilBo.getAllCell(this.tabId);
			}
			ArrayList zcyj_columns=new ArrayList();
			HashMap itemMap_before=new HashMap();
			HashMap itemMap_after=new HashMap();
			if(this.paramBo.getTemplateStatic()==2) //薪资管理
			{
				DbWizard dbw=new DbWizard(this.conn); 
				if(dbw.isExistTable("template_"+this.tabId,false))
				{
					rowSet=dao.search("select * from template_"+this.tabId+" where 1=2");
					ResultSetMetaData rsmd=rowSet.getMetaData();
					int columnCount  =  rsmd.getColumnCount();//得到列数  
					for(int i=0;i<columnCount;i++)
					{
						if(!"a0000".equalsIgnoreCase(rsmd.getColumnName(i+1).trim().toLowerCase()))
							zcyj_columns.add(rsmd.getColumnName(i+1).trim().toLowerCase()); 
					}
					executeZCYJ=true;
				}
			}
			ArrayList fldItemList= new ArrayList();
			for(int i=0;i<templateItemList.size();i++)
			{
				TemplateItem templateItem=(TemplateItem)templateItemList.get(i);
				if (templateItem.isbSubSetItem()){
				    continue;
				}
				
				FieldItem fieldItem= (FieldItem)templateItem.getFieldItem().cloneItem();
				if(templateItem.isCommonFieldItem())//如果是普通指标
				{
				    fieldItem.setItemid(templateItem.getFieldName());
					if(fieldItem.isChangeAfter()){
						if("e0122".equalsIgnoreCase(fieldItem.getItemid()))
							isE0122_2=true;
						if("e01a1".equalsIgnoreCase(fieldItem.getItemid()))
							isE01A1_2=true;
						String desc = (String)fieldItem.getItemdesc();												
						if (!(fieldItem.getItemdesc().startsWith("拟")))
    		 			{
						    fieldItem.setItemdesc("拟"+fieldItem.getItemdesc());						
    		 			}
						itemMap_after.put(fieldItem.getItemid().toLowerCase(),"1");
					}
					if(fieldItem.isChangeBefore()){
						itemMap_before.put(fieldItem.getItemid().toLowerCase(),"1");
					}
					fldItemList.add(fieldItem);
				}
			}
            ArrayList varlist=getMidVariableList();
            fldItemList.addAll(varlist);  //
			
			boolean isCal_b0110=false; //是否计算单位值
			boolean isCal_e0122=false; //是否计算部门值
			
			/**先对计算公式的条件进行分析*/
			ArrayList formulalist=readFormula(); 
			for(int i=0;i<formulalist.size();i++)
			{
				int infoGroupFlag=YksjParser.forPerson;
				if(this.paramBo.getInfor_type()==2)
					infoGroupFlag=YksjParser.forUnit;
				if(this.paramBo.getInfor_type()==3)
					infoGroupFlag=YksjParser.forPosition;
				FormulaGroupBo formulabo=(FormulaGroupBo)formulalist.get(i);
				/**先对计算条件进行处理*/
				String cond=formulabo.getStrWhere();
				strfilter="";
				if((cond!=null  && cond.length()>0)){
					yp = new YksjParser( this.userView ,fldItemList,
							YksjParser.forNormal, YksjParser.LOGIC,infoGroupFlag, "Ht", "");
					yp.run_where(cond);
					strfilter=yp.getSQL();
				}			
				StringBuffer strcond=new StringBuffer();
				if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
					strcond.append(strWhere);
				if(!("".equalsIgnoreCase(strfilter))){
					if(strcond.length()>0)
						strcond.append(" and ");
					else
						strcond.append(" where ");
					strcond.append(strfilter);
				}				
				ArrayList list=formulabo.getFormulalist();
				for(int j=0;j<list.size();j++)
				{
					LazyDynaBean dynabean=(LazyDynaBean)list.get(j);
					String fieldname=(String)dynabean.get("lexpr");
					String formula=(String)dynabean.get("rexpr");
					if ("".equals(formula)){
					    continue;
					}
					if(formula.indexOf("统计表单子集")!=-1) {
						continue;
					}
					/**进行公式计算*/
					FieldItem item=DataDictionary.getFieldItem(fieldname.substring(0, 5));
					if(formula==null||formula.length()==0){//zhaoxg add 2016-5-27 bug19120
						throw new GeneralException("【"+item.getItemdesc()+"】公式没有定义内容!");
					}
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
							}else
								throw new GeneralException("左表达式指标在指标体系中未定义!");
						}else
							throw new GeneralException("左表达式指标在指标体系中未定义!");
					}

					if(!isExistLexpr(fieldname,fldItemList))
						throw new GeneralException("公式中定义的指标【"+item.getItemdesc()+"】,模板中已经不存在,需修改!");
					if("b0110_2".equalsIgnoreCase(fieldname))
						isCal_b0110=true;
					if("e0122_2".equalsIgnoreCase(fieldname))
						isCal_e0122=true;
					
                    /*从工号池取工号 wangrd 2013-11-26  */  
                    if ((this.paramBo.getInfor_type()==1)&&!"1".equals(isSelfApply()) &&(formula.indexOf("执行存储过程")!=-1)&&(formula.indexOf("getJobNumber")!=-1||formula.indexOf("getStaffId")!=-1))
                    {
                        formula=formula.replace("当前表", getBz_tablename());  
                        formula=formula.replace("STR_WHERE", strWhere); 
                        formula=formula.replace("TEMPLET_ITEMID", fieldname);  
                    }
                    
                    
                    if(formula!=null&&formula.indexOf("执行存储过程")!=-1)
                    {
                    	formula=formula.replace("流程号",this.insid_pro);  
                    }
                    	
					yp=new YksjParser( this.userView ,fldItemList,
							YksjParser.forNormal, getDataType(item.getItemtype()),infoGroupFlag, "Ht", "");
					yp.setVarList(varlist);
					yp.setSupportVar(true);
					
					yp.setStdTmpTable_where(strcond.toString().replaceAll("where"," "));
					yp.run(formula,this.conn,strcond.toString().replaceAll("where"," "),getBz_tablename()); 
					if (formula.indexOf("执行存储过程")!=-1)
					         continue;
					
					/**单表计算*/
					String strexpr=yp.getSQL();
					//由于大文本在这里特殊处理，导致一个单子中人多的话在循环中更新值会很慢，考虑到大文本的计算产生的sql与普通指标无异，因此注释掉这里按普通指标处理  update 20180622
					/*if(item.getItemtype().equalsIgnoreCase("M"))
					{
						StringBuffer strsql=new StringBuffer("");
						String key_str=",a0100,basepre";
						if(this.paramBo.getInfor_type()==2)
							key_str=",b0110";
						else if(this.paramBo.getInfor_type()==3)
							key_str=",e01a1";
						
						if(getBz_tablename().equalsIgnoreCase("templet_"+this.tabId))
							strsql.append("select "+strexpr+key_str+",ins_id  from "+getBz_tablename());
						else
							strsql.append("select "+strexpr+key_str+"  from "+getBz_tablename());
						if(strcond.length()>0)
						{
							strsql.append(strcond.toString());
						}
						rowSet=dao.search(strsql.toString());
						RecordVo vo=new RecordVo(getBz_tablename());
						while(rowSet.next())
						{
							 
							 String value=rowSet.getString(1);
							
							 if(this.paramBo.getInfor_type()==1)
							 {
								 String a0100=rowSet.getString(2);
								 String basepre=rowSet.getString(3);
								 vo.setString("a0100",a0100);
								 vo.setString("basepre",basepre);
							 }
							 else if(this.paramBo.getInfor_type()==2)
							 {
								 vo.setString("b0110",rowSet.getString("b0110"));
							 }
							 else if(this.paramBo.getInfor_type()==3)
							 {
								 vo.setString("e01a1",rowSet.getString("e01a1"));
							 }
							 
							 int ins_id=0;
							 if(getBz_tablename().equalsIgnoreCase("templet_"+this.tabId))
							 {
								 ins_id=rowSet.getInt("ins_id");
								 vo.setInt("ins_id",ins_id);
							 }
							 vo=dao.findByPrimaryKey(vo);
							 vo.setString(fieldname.toLowerCase(), value);
							 vo.removeValue("photo");//bug30810  自动计算有照片oracle库更新会报错。因为照片不会参与计算，所以直接移除照片字段。
							 dao.updateValueObject(vo);
						}
					}
					else*/
					{
						StringBuffer strsql=new StringBuffer();
						strsql.append("update ");
						strsql.append(getBz_tablename());
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
							//添加变动日志
							if (isAotuLog && !("0".equalsIgnoreCase(this.insid)
									&& (paramBo.getOperationType() == 0|| paramBo.getOperationType() == 5))) {
								String updAutoLotObjectid = "";
								ArrayList updFieldList=new ArrayList();//要修改的字段
					            ArrayList updDataList=new ArrayList(); //要修改字段对应的数据
					            ArrayList updAutoLogSetBoList=new ArrayList();
								updFieldList.add(fieldname);
								for(int k=0;k<cellList.size();k++){
			            			TemplateSet setBo = (TemplateSet) cellList.get(k);
			            			String tableFieldName = setBo.getTableFieldName();
			            			if(fieldname.equalsIgnoreCase(tableFieldName)){
			            				updAutoLogSetBoList.add(setBo);
			            				break;
			            			}
			            		}
								StringBuffer strsql_=new StringBuffer("");
								String key_str=",a0100,basepre";
								if(this.paramBo.getInfor_type()==2)
									key_str=",b0110";
								else if(this.paramBo.getInfor_type()==3)
									key_str=",e01a1";
								
								if(getBz_tablename().equalsIgnoreCase("templet_"+this.tabId))
									strsql_.append("select "+strexpr+key_str+",ins_id  from "+getBz_tablename());
								else
									strsql_.append("select "+strexpr+key_str+"  from "+getBz_tablename());
								if(strcond.length()>0)
								{
									strsql_.append(strcond.toString());
								}
								rowSet=dao.search(strsql_.toString());
								while(rowSet.next())
								{
									 String value="";
									 if(this.paramBo.getInfor_type()==1)
									 {
										 String a0100=rowSet.getString(2);
										 String basepre=rowSet.getString(3);
										 updAutoLotObjectid = basepre+"`"+a0100;
									 }
									 else if(this.paramBo.getInfor_type()==2)
									 {
										 String b0110 = rowSet.getString("b0110");
										 updAutoLotObjectid = b0110;
									 }
									 else if(this.paramBo.getInfor_type()==3)
									 {
										 String e01a1 = rowSet.getString("e01a1");
										 updAutoLotObjectid = e01a1;
									 }
									 if("D".equalsIgnoreCase(item.getItemtype())) {
										 SimpleDateFormat format=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
										 Timestamp dateValue = rowSet.getTimestamp(1);
										 if(dateValue!=null) {
											 value = format.format(dateValue);
										 }
										 java.sql.Date date = null;
										 if(StringUtils.isNotBlank(value)) {
											 if(value.indexOf("-")<0)
												 date = DateUtils.getSqlDate(value,"yyyy.MM.dd");
											 else 
												 date = DateUtils.getSqlDate(value,"yyyy-MM-dd");
											 updDataList.add(date); 
										 }else
											 updDataList.add(null); 
									 }else {
										 value=rowSet.getString(1);
										 updDataList.add(value);
									 }
									 TempletChgLogBo chgLogBo=new TempletChgLogBo(this.conn,this.userView,paramBo);
				                		chgLogBo.createTemplateChgLogTable("templet_chg_log");
				                		String realTask_id= this.taskId;
				                		if(StringUtils.isBlank(realTask_id)){
				                			realTask_id="0";
				                		}
				                		if(taskId.indexOf(",")==-1&&StringUtils.isNotBlank(taskId)){
				                			realTask_id=taskId;
				                		}
									  chgLogBo.insertOrUpdateAllLogger(updFieldList, updAutoLogSetBoList, updDataList,
											this.insid, realTask_id, updAutoLotObjectid, getBz_tablename(),
											paramBo.getInfor_type());
								}
		                	}
							dao.update(strsql.toString());
						}
						catch(Exception cex)
						{
							cex.printStackTrace();
							throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.compute.checkFielditem").replace("{0}", item.getItemid().toUpperCase() + ":" + item.getItemdesc())));
						}
					
					}
				}//for j loop end.
			}//for i loop end.
			
			String join="+";
			String where=" where 1=1 ";
			if(Sql_switcher.searchDbServer()==2)
				join="||";
			if(strWhere.length()!=0)
				where=strWhere;
			if(isCal_b0110) //计算单位时，部门、岗位信息要联动
			{
				if(isE0122_2) 
					dao.update("update "+getBz_tablename()+" set e0122_2=null "+where+" and ( e0122_2 not like b0110_2"+join+"'%' or b0110_2 is null ) ");
				if(isE01A1_2) 
					dao.update("update "+getBz_tablename()+" set e01a1_2=null "+where+" and ( e01a1_2 not like b0110_2"+join+"'%' or b0110_2 is null ) ");
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
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			if(this.isThrow)
				throw GeneralExceptionHandler.Handle(ex);
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
			if(fielditem.getVarible()==1)
				continue;
			field_name=fielditem.getItemid();
			if(field_name.equalsIgnoreCase(lexpr))
			{
				bflag=true;
				break;
			}
		}
		return bflag;
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
						whl+=" and isnull("+getBz_tablename()+"."+column_name+",'-10aa0')="+"isnull(template_"+this.tabId+"."+column_name+",'-10aa0')";
				}
			} 
			if(whl.length()>0)
			{
				for(int i=0;i<zcyj_columns.size();i++)
				{
					String column_name=(String)zcyj_columns.get(i);
					if(itemMap_after.get(column_name)!=null)
					{
						sql="update "+getBz_tablename()+" set "+column_name+"=(select "+column_name+" from template_"+this.tabId+"  where 1=1 "+whl+" ) ";
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
	 * 执行所有薪资标准
	 * @param strWhere
	 * @throws GeneralException
	 */
	/**执行的工资标准,标准号数组*/
	private void batchCalcGzStandard(String strWhere)throws GeneralException
	{
		try
		{
			for(int i=0;i<this.paramBo.getGz_stand().length;i++)
			{
				int standid=Integer.parseInt(this.paramBo.getGz_stand()[i]);
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
		if(!stdbo.isExist())
			return;
		
		/**重新计算相关日期型或数值型区间范围的值*/
		StringBuffer buf=new StringBuffer();
		if(!stdbo.checkHVField(buf))
			throw new GeneralException(buf.toString());
		/**把标准横纵坐标为日期型或数值型指标，加至薪资表中*/
		ArrayList list=stdbo.addStdItemIntoTable(getBz_tablename());
		stdbo.updateStdItem(list, getBz_tablename());
		/**关联更新串,0*/
		String joinon=stdbo.getStandardJoinOn(getBz_tablename(),0);
		/**结果指标*/
		String fieldname=stdbo.getItem();
		FieldItem item=DataDictionary.getFieldItem(fieldname);
		/**变化后的指标,模板中是否定义了变化后指标*/
		fieldname=fieldname+"_2";
		RecordVo vo=new RecordVo(getBz_tablename());
		if(!vo.hasAttribute(fieldname.toLowerCase()))
			return;
		DbWizard dbw=new DbWizard(this.conn);		
		switch(Sql_switcher.searchDbServer())
		{
		case 1: //MSSQL
			dbw.updateRecord(getBz_tablename(), "gz_item",joinon,getBz_tablename()+"."+fieldname+"=gz_item.standard", strWhere, "");
			break;
		case 2://oracle
			if("N".equalsIgnoreCase(item.getItemtype()))
				dbw.updateRecord(getBz_tablename(), "gz_item",joinon,getBz_tablename()+"."+fieldname+"=to_number(gz_item.standard)", strWhere, "");
			else
				dbw.updateRecord(getBz_tablename(), "gz_item",joinon,getBz_tablename()+"."+fieldname+"=gz_item.standard", strWhere, "");
			break;
		case 3://db2
			if("N".equalsIgnoreCase(item.getItemtype()))
				dbw.updateRecord(getBz_tablename(), "gz_item",joinon,getBz_tablename()+"."+fieldname+"=double(gz_item.standard)", strWhere, "");
			else
				dbw.updateRecord(getBz_tablename(), "gz_item",joinon,getBz_tablename()+"."+fieldname+"=gz_item.standard", strWhere, "");
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
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where  nflag=0 and templetId <> 0 and (templetId ="+this.tabId+"  or cstate ='1') "); 
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
			
			 ArrayList expList=new ArrayList();//用于存放计算条件，以及计算公式的表达式
			 if(func_id==1||func_id==3) // 获得计算公式    
			 {
				    
				    ArrayList formulalist=readFormula(); 
					for(int i=0;i<formulalist.size();i++)
					{
						FormulaGroupBo formulabo=(FormulaGroupBo)formulalist.get(i);
						/**先对计算条件进行处理*/
						String cond=formulabo.getStrWhere(); 
						if(cond!=null&&cond.trim().length()>0)
							expList.add(cond);
						ArrayList list=formulabo.getFormulalist();
						for(int j=0;j<list.size();j++)
						{
							LazyDynaBean dynabean=(LazyDynaBean)list.get(j);
							String fieldname=(String)dynabean.get("lexpr");
							String formula=(String)dynabean.get("rexpr");
							if(formula!=null&&formula.length()>0)
								expList.add(formula);
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
			buf.append("(select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from template_set ts ,midvariable m where ts.flag='V' and ts.tabid="+this.tabId);
			buf.append(" and (m.TempletID="+this.tabId+" or m.cstate='1') and TempletID<>0 and m.nFlag=0 and m.cName=ts.Field_name )mts");
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
						if(usedMap.get(cname)!=null)//意味着在计算公式或者计算条件中该临时变量被使用了，或者是该模版中插入了该临时变量
							newList.add(item);
				}
				fieldlist=newList;
			}
			else if(expList.size()==0&&templarVarList.size()==0)
				fieldlist=new ArrayList();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return fieldlist;
	}
	/**
	 * 取得当前模板的校验公式
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList getLogicExpressList()throws GeneralException
	{
		StringBuffer strsql=new StringBuffer();
		strsql.append("select * from hrpchkformula where flag=0 and tabid=");
		strsql.append(this.tabId+"");
		//新人事异动判断是否启用标记
		strsql.append(" and validflag=1");		
		strsql.append(" order by seq");
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RowSet rset=dao.search(strsql.toString());
			String formula=null;
			while(rset.next())
			{
				formula=rset.getString("formula");
				if(formula==null|| "".equals(formula))
					continue;
				RecordVo vo=new RecordVo("hrpchkformula");
				vo.setString("name", rset.getString("name"));
				vo.setString("information", rset.getString("information"));
				vo.setString("formula", rset.getString("formula"));		
				list.add(vo);
			}// loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	
	
	/**
	 * 审核逻辑表达式(批量审批)
	 */
	public void checkLogicExpress_batch(String task_ids)
			throws GeneralException {
		
		try {
			ArrayList list = getLogicExpressList();
			if (list.size() < 1) {
				return;
			}
			StringBuffer buf = new StringBuffer();
			StringBuffer strsql = new StringBuffer();
			StringBuffer strInfo = new StringBuffer();
			StringBuffer strInfoA = new StringBuffer();
			ArrayList conefieldlist = new ArrayList();
			ArrayList templateItemList = getAllTemplateItem();
			for (int i = 0; i < templateItemList.size(); i++) {
				TemplateItem templateItem = (TemplateItem) templateItemList
						.get(i);
				if (templateItem.isbSubSetItem()) {
					continue;
				}

				FieldItem fieldItem = (FieldItem) templateItem.getFieldItem()
						.cloneItem();
				if (templateItem.isCommonFieldItem())// 如果是普通指标
				{
					fieldItem.setItemid(templateItem.getFieldName());
					conefieldlist.add(fieldItem);
				}
			}
			ArrayList midFieldlist = getMidVariableListByFunc(2);// getMidVariableList();
			conefieldlist.addAll(midFieldlist);

			/** 计算临时变量,把临时变量加到变动处理表中去 */
			/** 应用库前缀 */

			String tableName= this.getBz_tablename();
			Object[] dbarr = null;
			if (this.paramBo.getInfor_type() == 1) {
				ArrayList dblist = searchDBPreList(tableName);
				dbarr = dblist.toArray();
			}

			// 公式解析类需要的where条件，不然计算整个表，计算可休天数的时候会慢很多。wangrd 20151103
			String whereText = "";
			StringBuffer strwhere = new StringBuffer(""); 
			strwhere.append(" where  exists (select null from t_wf_task_objlink where templet_"
								+ this.tabId
								+ ".seqnum=t_wf_task_objlink.seqnum and templet_"
								+ this.tabId
								+ ".ins_id=t_wf_task_objlink.ins_id ");
			strwhere.append("  and task_id in ("
								+ task_ids
								+ " ) and tab_id="
								+ this.tabId
								+ "  and submitflag=1  and (state is null or  state=0 ) ) ");  
			if (!isComputeVar)
				addMidVarIntoGzTable(strwhere.toString(), dbarr, midFieldlist);
			whereText = strwhere.toString().substring(6);
			boolean bUseTmpTable = false;
			DbWizard dbw = new DbWizard(this.conn);
			String tmpTableName = "t#" + this.userView.getUserName()
					+ "_templet";
			;
			{// 审批表及自助申请表存在并发问题 ，需建临时表
				String strWhere = whereText;
				if (strWhere.toUpperCase().startsWith("WHERE"))
					strWhere = strWhere.substring(6);
				else if (strWhere.toUpperCase().startsWith(" WHERE"))
					strWhere = strWhere.substring(7);
				// 生成临时表
				if (dbw.isExistTable(tmpTableName, false))
					dbw.dropTable(tmpTableName);
				dbw.createTempTable(tableName, tmpTableName, "*", strWhere, "");
				if (dbw.isExistTable(tmpTableName, false))
					bUseTmpTable = true;
				else
					tmpTableName = tableName;
			}

			// strInfo.append("下列人员信息审核有误!\n\r");
			for (int i = 0; i < list.size(); i++) {
				RecordVo vo = (RecordVo) list.get(i);
				String formula = vo.getString("formula");
				String name = vo.getString("name");
				String information = vo.getString("information");

				int infoGroupFlag = YksjParser.forPerson;
				if (this.paramBo.getInfor_type() == 2)
					infoGroupFlag = YksjParser.forUnit;
				if (this.paramBo.getInfor_type() == 3)
					infoGroupFlag = YksjParser.forPosition;
				YksjParser yp = new YksjParser(this.userView, conefieldlist,
						YksjParser.forNormal, YksjParser.LOGIC, infoGroupFlag,
						"Ht", "");
				yp.setStdTmpTable(tmpTableName);
				yp.setTempTableName(tmpTableName);
				yp.setCon(conn);
				if (!bUseTmpTable) {// 使用临时表时 就不用塞条件了
					if (whereText.length() > 0) {
						yp.setWhereText(whereText);
					}
				}

				if (formula.indexOf("执行存储过程") != -1
						&& formula.indexOf("KqCheckResult") != -1) // 汉口银行
				{
					formula = formula.replaceAll("param", strwhere.toString()
							.replaceAll("'", "\""));

				}

				yp.run(formula.trim());
				if (formula.indexOf("执行存储过程") != -1)
					continue;
				String strWhere = yp.getSQL();// 公式的结果
				buf.setLength(0);
				
				buf.append(" where  exists (select null from t_wf_task_objlink where "
									+ tmpTableName
									+ ".seqnum=t_wf_task_objlink.seqnum and "
									+ tmpTableName
									+ ".ins_id=t_wf_task_objlink.ins_id ");
				buf.append("  and task_id in ("
									+ task_ids
									+ " ) and tab_id="
									+ this.tabId
									+ "  and submitflag=1  and (state is null or  state=0 ) ) ");

				buf.append(" and  (");
				buf.append(strWhere + " )");
				
				strsql.setLength(0);
				String key = "a0101_1";
				if (this.paramBo.getInfor_type() == 2
						|| this.paramBo.getInfor_type() == 3)
					key = "codeitemdesc_1";
				strsql.append("select " + key + " from ");
				strsql.append(tmpTableName);
				strsql.append(buf.toString());
				RowSet rset = dao.search(strsql.toString());
				strInfo.setLength(0);
				strInfo.append(name + "(" + information + ")\n\r");
				int idx = 0;
				while (rset.next()) {
					if (idx != 0)
						strInfo.append(",");
					if (this.paramBo.getInfor_type() == 1)
						strInfo.append(rset.getString("a0101_1"));
					else if (this.paramBo.getInfor_type() == 2
							|| this.paramBo.getInfor_type() == 3) {
						strInfo.append(rset.getString("codeitemdesc_1"));
					}
					idx++;
				}// loop end.
				if (idx > 0) {
					if (strInfoA.length() > 0)
						strInfoA.append("\n\r");
					strInfoA.append(strInfo.toString());
				}
				PubFunc.closeDbObj(rset);
			}// for i loop end.
			if (bUseTmpTable)
				dbw.dropTable(tmpTableName);
			if (strInfoA.length() > 0)
				throw new GeneralException(strInfoA.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			String message = ex.toString();
			if (message.indexOf("最大") != -1 && message.indexOf("8060") != -1
					&& Sql_switcher.searchDbServer() == 1) {
				PubFunc.resolve8060(this.conn, this.bz_tablename);
				throw GeneralExceptionHandler.Handle(new Exception("请重新操作!"));
			} else
				throw GeneralExceptionHandler.Handle(ex);

		}
	}
	
	
	
	
	/**
	 * 审核逻辑表达式
	 */
	public void checkLogicExpress(int task_id)
			throws GeneralException {
		try {
			ArrayList list = getLogicExpressList();
			if (list.size() < 1) {
				return;
			}
			StringBuffer buf = new StringBuffer();
			StringBuffer strsql = new StringBuffer();
			StringBuffer strInfo = new StringBuffer();
			StringBuffer strInfoA = new StringBuffer();
			ArrayList conefieldlist = new ArrayList();
			ArrayList templateItemList = getAllTemplateItem();
			for (int i = 0; i < templateItemList.size(); i++) {
				TemplateItem templateItem = (TemplateItem) templateItemList
						.get(i);
				if (templateItem.isbSubSetItem()) {
					continue;
				}

				FieldItem fieldItem = (FieldItem) templateItem.getFieldItem()
						.cloneItem();
				if (templateItem.isCommonFieldItem())// 如果是普通指标
				{
					fieldItem.setItemid(templateItem.getFieldName());
					conefieldlist.add(fieldItem);
				}
			}
			ArrayList midFieldlist = getMidVariableListByFunc(2);// getMidVariableList();
			conefieldlist.addAll(midFieldlist);

			/** 计算临时变量,把临时变量加到变动处理表中去 */
			/** 应用库前缀 */

			String tableName= this.getBz_tablename();
			Object[] dbarr = null;
			if (this.paramBo.getInfor_type() == 1) {
				ArrayList dblist = searchDBPreList(tableName);
				dbarr = dblist.toArray();
			}

			// 公式解析类需要的where条件，不然计算整个表，计算可休天数的时候会慢很多。wangrd 20151103
			String whereText = "";
			StringBuffer strwhere = new StringBuffer("");
			if (task_id != 0) {
				strwhere
						.append(" where  exists (select null from t_wf_task_objlink where templet_"
								+ this.tabId
								+ ".seqnum=t_wf_task_objlink.seqnum and templet_"
								+ this.tabId
								+ ".ins_id=t_wf_task_objlink.ins_id ");
				strwhere
						.append("  and task_id="
								+ task_id
								+ " and tab_id="
								+ this.tabId
								+ "  and submitflag=1  and (state is null or  state=0 ) ) ");
			} else {
				strwhere.append(" where ");
				if (this.isSelfApply())// 员工通过自助平台发动申请
				{
					strwhere.append(" g_templet_" + this.tabId + ".a0100='"
							+ this.userView.getA0100()
							+ "' and lower(g_templet_" + this.tabId
							+ ".basepre)='"
							+ this.userView.getDbname().toLowerCase() + "'");
				} else {
					strwhere.append(" submitflag=1 ");
				}
			}
			if (!isComputeVar)
				addMidVarIntoGzTable(strwhere.toString(), dbarr, midFieldlist);
			whereText = strwhere.toString().substring(6);
			boolean bUseTmpTable = false;
			DbWizard dbw = new DbWizard(this.conn);
			String tmpTableName = "t#" + this.userView.getUserName()
					+ "_templet";
			;
			if (task_id != 0 || isSelfApply()) {// 审批表及自助申请表存在并发问题 ，需建临时表
				String strWhere = whereText;
				if (strWhere.toUpperCase().startsWith("WHERE"))
					strWhere = strWhere.substring(6);
				else if (strWhere.toUpperCase().startsWith(" WHERE"))
					strWhere = strWhere.substring(7);
				// 生成临时表
				if (dbw.isExistTable(tmpTableName, false))
					dbw.dropTable(tmpTableName);
				dbw.createTempTable(tableName, tmpTableName, "*", strWhere, "");
				if (dbw.isExistTable(tmpTableName, false))
					bUseTmpTable = true;
				else
					tmpTableName = tableName;
			} else {
				tmpTableName = tableName;
			}

			// strInfo.append("下列人员信息审核有误!\n\r");
			for (int i = 0; i < list.size(); i++) {
				RecordVo vo = (RecordVo) list.get(i);
				String formula = vo.getString("formula");
				String name = vo.getString("name");
				String information = vo.getString("information");

				int infoGroupFlag = YksjParser.forPerson;
				if (this.paramBo.getInfor_type() == 2)
					infoGroupFlag = YksjParser.forUnit;
				if (this.paramBo.getInfor_type() == 3)
					infoGroupFlag = YksjParser.forPosition;
				YksjParser yp = new YksjParser(this.userView, conefieldlist,
						YksjParser.forNormal, YksjParser.LOGIC, infoGroupFlag,
						"Ht", "");
				yp.setStdTmpTable(tmpTableName);
				yp.setTempTableName(tmpTableName);
				yp.setCon(conn);
				if (!bUseTmpTable) {// 使用临时表时 就不用塞条件了
					if (whereText.length() > 0) {
						yp.setWhereText(whereText);
					}
				}

				if (formula.indexOf("执行存储过程") != -1
						&& formula.indexOf("KqCheckResult") != -1) // 汉口银行
				{
					formula = formula.replaceAll("param", strwhere.toString()
							.replaceAll("'", "\""));

				}

				yp.run(formula.trim());
				if (formula.indexOf("执行存储过程") != -1)
					continue;
				String strWhere = yp.getSQL();// 公式的结果
				buf.setLength(0);
				if (task_id != 0) {
					buf
							.append(" where  exists (select null from t_wf_task_objlink where "
									+ tmpTableName
									+ ".seqnum=t_wf_task_objlink.seqnum and "
									+ tmpTableName
									+ ".ins_id=t_wf_task_objlink.ins_id ");
					buf
							.append("  and task_id="
									+ task_id
									+ " and tab_id="
									+ this.tabId
									+ "  and submitflag=1  and (state is null or  state=0 ) ) ");

					buf.append(" and  (");
					buf.append(strWhere + " )");
				} else {
					buf.append(" where ");
					if (this.isSelfApply())// 员工通过自助平台发动申请
					{
						buf
								.append(tmpTableName
										+ ".a0100='"
										+ this.userView.getA0100()
										+ "' and lower("
										+ tmpTableName
										+ ".basepre)='"
										+ this.userView.getDbname()
												.toLowerCase() + "'");
					} else {
						buf.append(" submitflag=1 ");
					}
					buf.append(" and (");
					buf.append(strWhere + " )");
				}
				strsql.setLength(0);
				String key = "a0101_1";
				if (this.paramBo.getInfor_type() == 2
						|| this.paramBo.getInfor_type() == 3)
					key = "codeitemdesc_1";
				strsql.append("select " + key + " from ");
				strsql.append(tmpTableName);
				strsql.append(buf.toString());
				RowSet rset = dao.search(strsql.toString());
				strInfo.setLength(0);
				strInfo.append(name + "(" + information + ")\n\r");
				int idx = 0;
				while (rset.next()) {
					if (idx != 0)
						strInfo.append(",");
					if (this.paramBo.getInfor_type() == 1)
						strInfo.append(rset.getString("a0101_1"));
					else if (this.paramBo.getInfor_type() == 2
							|| this.paramBo.getInfor_type() == 3) {
						strInfo.append(rset.getString("codeitemdesc_1"));
					}
					idx++;
				}// loop end.
				if (idx > 0) {
					if (strInfoA.length() > 0)
						strInfoA.append("\n\r");
					strInfoA.append(strInfo.toString());
				}
				PubFunc.closeDbObj(rset);
			}// for i loop end.
			if (bUseTmpTable)
				dbw.dropTable(tmpTableName);
			if (strInfoA.length() > 0)
				throw new GeneralException(strInfoA.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			String message = ex.toString();
			if (message.indexOf("最大") != -1 && message.indexOf("8060") != -1
					&& Sql_switcher.searchDbServer() == 1) {
				PubFunc.resolve8060(this.conn, this.bz_tablename);
				throw GeneralExceptionHandler.Handle(new Exception("请重新操作!"));
			} else
				throw GeneralExceptionHandler.Handle(ex);

		}
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
			
			if(fieldlist.size()==0)
				return;
			List setlist=getSetListByStd(fieldlist);			
			RecordVo vo=new RecordVo(getBz_tablename());
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(getBz_tablename());
			String midtable="t#"+this.userView.getUserName()+"_gz"; //this.userView.getUserName()+"midtable";			
			
			ArrayList fieldlist_2=new ArrayList();
			/**
			 * 把标准中涉及到的指标加入至薪资表结构中
			 */
			boolean bflag=false;
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
				
				if(vo.hasAttribute(fieldname.toLowerCase()+"_2"))
					fieldlist_2.add(item);
				
			}//for i loop end.
			if(bflag)
			{
				dbw.addColumns(table);
				DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel(getBz_tablename());						
			}

			/**从档案表中导入有关标准表涉及到的数据*/
			for(int i=0;i<dbarr.length;i++)
			{
				String dbpre=(String)dbarr[i];
				for(int j=0;j<setlist.size();j++)
				{
					String setid=(String)setlist.get(j);
					if("A00".equalsIgnoreCase(setid))//多媒体子集
						continue;
					char cc=setid.charAt(0);
					switch(cc)
					{
					case 'A': //人员信息
							String strS=dbpre+setid;		
							if("A01".equalsIgnoreCase(setid)) //主集
							{
								String strupdate=getStdUpdateSQL(fieldlist, strS, setid);
								if(strupdate.length()>0)
									dbw.updateRecord(getBz_tablename(),strS,getBz_tablename()+".A0100="+strS+".A0100", strupdate, getBz_tablename()+".basepre='"+dbpre+"'", "");
							}
							else//子集
							{
								String strupdate=getStdUpdateSQL(fieldlist, midtable, setid);
								if(strupdate.length()==0)
									continue;
								String strfields=getStdFieldNameList(fieldlist, setid);
								/**子集当前子录生成临时表*/
								String tempt="t#"+this.userView.getUserName()+"_gz_1"; //this.userView.getUserName()+"midtable1";
								if(dbw.isExistTable(tempt, false))
									dbw.dropTable(tempt);
								dbw.createTempTable(strS, tempt,"A0100 as A0000,Max(I9999) as midid", "","A0100");
								if(dbw.isExistTable(midtable, false))
									dbw.dropTable(midtable);
								dbw.createTempTable(strS+" Left join "+tempt+" On "+strS+".A0100="+tempt+".A0000",midtable, "A0100,"+strfields,strS+".I9999="+tempt+".midid","");
								dbw.updateRecord(getBz_tablename(),midtable,getBz_tablename()+".A0100="+midtable+".A0100",strupdate, getBz_tablename()+".basepre='"+dbpre+"'", strWhere);
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
				if(strWhere != null && !"".equals(strWhere))
					dbw.execute("update "+getBz_tablename()+" set "+item.getItemid()+"="+item.getItemid()+"_2 where "+strWhere);
				else
					dbw.execute("update "+getBz_tablename()+" set "+item.getItemid()+"="+item.getItemid()+"_2 ");
				
			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
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
				if(fields.indexOf(item.getItemid())!=-1)
					continue;
				fields.append(item.getItemid());
				fields.append(",");
				
				String fieldname=item.getItemid();
				buf.append(getBz_tablename());
				buf.append(".");
				buf.append(fieldname);
				buf.append("=");
				buf.append(strS);
				buf.append(".");
				buf.append(fieldname);
			
				if(Sql_switcher.searchDbServer()==2)
					buf.append("`");
				else
					buf.append(",");
			}
		}//for i loop end.
		if(buf.length()>0)
			buf.setLength(buf.length()-1);
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
		if(buf.length()>0)
			buf.setLength(buf.length()-1);
		return buf.toString();
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
			if(this.paramBo.getGz_stand().length==0)
				return fieldlist;
			StringBuffer stdbuf=new StringBuffer();
			for(int i=0;i<this.paramBo.getGz_stand().length;i++)
			{
				stdbuf.append(this.paramBo.getGz_stand()[i]);
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
		for(int i=0;i<formulaList.size();i++)
		{
				temp=(String)formulaList.get(i);
				if(cname!=null&&cname.trim().length()>0&&temp.toLowerCase().indexOf(cname.toLowerCase())!=-1)
				{
					usedMap.put(cname,"1");
					for(int j=0;j<varList.size();j++) //解决临时变量调用临时变量问题
					{
						FieldItem _item=(FieldItem)varList.get(j);
						String _cname=_item.getItemid();
						String _chz=_item.getItemdesc();
						if(_cname!=null&&_cname.trim().length()>0&&formula.toLowerCase().indexOf(_cname.toLowerCase())!=-1) 
							usedMap.put(_cname,"1"); 
						else if(_chz!=null&&_chz.trim().length()>0&&formula.toLowerCase().indexOf(_chz.toLowerCase())!=-1)
							usedMap.put(_cname,"1");
					}
					break;
				}
				else if(chz!=null&&chz.trim().length()>0&&temp.toLowerCase().indexOf(chz.toLowerCase())!=-1)
				{
					usedMap.put(cname,"1");
					for(int j=0;j<varList.size();j++)
					{
						FieldItem _item=(FieldItem)varList.get(j);
						String _cname=_item.getItemid();
						String _chz=_item.getItemdesc();
						if(_cname!=null&&_cname.trim().length()>0&&formula.toLowerCase().indexOf(_cname.toLowerCase())!=-1) 
							usedMap.put(_cname,"1"); 
						else if(_chz!=null&&_chz.trim().length()>0&&formula.toLowerCase().indexOf(_chz.toLowerCase())!=-1)
							usedMap.put(_cname,"1");
					}
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
		        for(int j=0;j<varList.size();j++) //解决临时变量调用临时变量问题
                {
                    FieldItem _item=(FieldItem)varList.get(j);
                    String _cname=_item.getItemid();
                    String _chz=_item.getItemdesc();
                    if(_cname!=null&&_cname.trim().length()>0&&tempFormula.toLowerCase().indexOf(_cname.toLowerCase())!=-1) 
                        usedMap.put(_cname,"1"); 
                    else if(_chz!=null&&_chz.trim().length()>0&&tempFormula.toLowerCase().indexOf(_chz.toLowerCase())!=-1)
                        usedMap.put(_cname,"1");
                }
		        break;
		    }
		}
		
		
		for(int e=0;e<varList.size();e++) //解决临时变量调用临时变量 三层嵌套问题
		{
			FieldItem _item0=(FieldItem)varList.get(e);
			String _cname0=_item0.getItemid();
			String _chz0=_item0.getItemdesc();
			String _tempFormula0=_item0.getFormula(); 
			if(usedMap.get(_cname0)!=null) 
			{
				for(int j=0;j<varList.size();j++)
				{
					FieldItem _item=(FieldItem)varList.get(j);
					String _cname=_item.getItemid();
					String _chz=_item.getItemdesc();
					if(_cname!=null&&_cname.trim().length()>0&&_tempFormula0.toLowerCase().indexOf(_cname.toLowerCase())!=-1) 
						usedMap.put(_cname,"1"); 
					else if(_chz!=null&&_chz.trim().length()>0&&_tempFormula0.toLowerCase().indexOf(_chz.toLowerCase())!=-1)
						usedMap.put(_cname,"1");
				} 
			} 
		}
		
		
		
	}
	
	
	
	
	
	
	
	////////////////////表结构同步和数据同步 liuzy 20151221/////////////////////////////
	
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
		String tablename=username+"templet_"+this.tabId; //"templet_"+this.tabId
		if ("".equals(username)){ //来自业务申请
		    tablename="g_templet_"+this.tabId;
		}
		RowSet rowSet=null;
		try
		{
			DbSecurityImpl dbS = new DbSecurityImpl();
			Table table=new Table(tablename);
			DbWizard dbwizard=new DbWizard(this.conn);
			ResultSetMetaData md=null;
			ContentDAO dao=new ContentDAO(this.conn);
			//无论是 sutemplet_1，还是templet_1表，若不是人员表但存在 a0100字段，就将其注销掉
			if(dbwizard.isExistTable(tablename,false)){
				this.checkIsOrgOrUsr(tablename,dbwizard,md,rowSet);
			}
			
			if(dbwizard.isExistTable("templet_"+this.tabId,false)){
				this.checkIsOrgOrUsr("templet_"+this.tabId,dbwizard,md,rowSet);
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
			
			
			if(!dbwizard.isExistTable("templet_"+this.tabId,false))
			{
				Table table_wf=new Table("templet_"+this.tabId);
				addFieldItem(table_wf,1);		
				dbwizard.createTable(table_wf);
				dbS.encryptTableName(this.conn, tablename);
			}
			else
			{
				Table table_wf=new Table("templet_"+this.tabId);
				updateTempTemplateStruct(table_wf,1,fieldList);	
				syncGzField("templet_"+this.tabId);
			}
			/**重新加载数据模型*/
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tablename);
			dbmodel.reloadTableModel("templet_"+this.tabId);
			/**从消息库中导入此业务模板，可以取到的消息*/
			if (username.length()>0){ //来自业务申请
				impDataFromMessage(tablename);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);
			
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
		return bflag;
	}
	/**
	 * 检查表结构是否与模板对应类型相符
	 * @param tablename
	 * @param dbwizard
	 * @param md
	 * @param rowSet
	 * @throws GeneralException
	 */
	private void checkIsOrgOrUsr(String tablename, DbWizard dbwizard, ResultSetMetaData md, RowSet rowSet) throws GeneralException {
		try
		{
			 String a0100 = null;
			 rowSet=dao.search("select * from "+tablename+" where 1=2");
			 md=rowSet.getMetaData();
			 for(int i=1;i<=md.getColumnCount();i++)
			 {
				 String columnName=md.getColumnName(i);
				 if("a0100".equalsIgnoreCase(columnName))
				 {
					 if((this.paramBo.getInfor_type()==2||this.paramBo.getInfor_type()==3)){
						 dbwizard.dropTable(tablename);
						 break;
					 }else
						 a0100 = columnName;
				 }
			 }
			 
			 if(this.paramBo.getInfor_type()==1 && StringUtils.isBlank(a0100)){//人员表中a0100不存在则删除整张临时表 lis 20160811
				 dbwizard.dropTable(tablename);
			 }
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	private void addFieldItem(Table table,int flag)throws GeneralException
	{
		Field temp=null;
		HashMap hm=new HashMap();
		if(this.paramBo.getTemplateStatic()==10) //单位管理
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
		else if(this.paramBo.getTemplateStatic()==11) //职位管理
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
		temp.setLength(38);//a0000oracle数据库不给长度，会默认建成长度10
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
		
		if("1".equals(this.userView.getHm().get("fillInfo"))){
			tempitem0=new FieldItem();
			tempitem0.setItemtype("D");
			tempitem0.setItemid("create_time");
			table.addField(tempitem0);
			hm.put("create_time",tempitem0);
		}
		
		/**对人员信息群增加三个固定字段B0110_1,E0122_1,A0101_1,单位、部门及姓名*/
		if(this.paramBo.getTemplateStatic()!=10&&this.paramBo.getTemplateStatic()!=11)
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
			
			if(this.paramBo.getOperationType()==0){
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
		else if(this.paramBo.getTemplateStatic()==10||this.paramBo.getTemplateStatic()==11)
		{
			if(this.paramBo.getTemplateStatic()==10)
				temp=new Field("codeitemdesc_1",ResourceFactory.getProperty("general.template.orgname"));
			if(this.paramBo.getTemplateStatic()==11)
				temp=new Field("codeitemdesc_1",ResourceFactory.getProperty("e01a1.label"));
			temp.setDatatype(DataType.STRING);
			temp.setLength(200);
			temp.setVisible(true);
			temp.setNullable(true);
			temp.setKeyable(false);
			table.addField(temp);
			hm.put("codeitemdesc_1",temp);	
			if(this.paramBo.getOperationType()==5){
			temp=new Field("codeitemdesc_2",ResourceFactory.getProperty("general.template.orgname"));
			temp.setDatatype(DataType.STRING);
			temp.setLength(200);
			temp.setVisible(true);
			temp.setNullable(true);
			temp.setKeyable(false);
			table.addField(temp);
			hm.put("codeitemdesc_2",temp);
			}
		}
		if(this.paramBo.getOperationType()==8||this.paramBo.getOperationType()==9)
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
		
		//将模板所有指标加入table
	    ArrayList FldItemlist=getAllFieldItem();
		for(int i=0;i<FldItemlist.size();i++)
		{
		    FieldItem fldItem=(FieldItem)FldItemlist.get(i);
			String field_name= fldItem.getItemid();
			if(!hm.containsKey(field_name.toLowerCase()))
			{
				if(field_name.toLowerCase().indexOf("attachment")>-1)//过滤附件
					continue;
				hm.put(field_name.toLowerCase(),fldItem);
				table.addField(fldItem);
			}
		}
	}
	
	/***
	 * 修改临时表的结构
	 * @param table
	 * @param flag =1升级审批表结构
	 * @return
	 */
	private boolean updateTempTemplateStruct(Table table,int flag,ArrayList fieldItemList)
	{
		boolean bflag=true;
		try
		{
		
			ContentDAO dao=new ContentDAO(this.conn);
			//ArrayList list=(ArrayList)fieldList.clone();//getAllFieldItem();
			RowSet rowSet=dao.search("select * from "+table.getName()+" where 1=2");
			ResultSetMetaData mt=rowSet.getMetaData();
			HashMap att_map=new HashMap();
			
			
			DbWizard dbwizard=new DbWizard(this.conn);		
			StringBuffer strs=new StringBuffer();
			String field_name=null;
			boolean baddkey=false;
	
			for(int i=0;i<mt.getColumnCount();i++)
			{
				att_map.put(mt.getColumnName(i+1).toLowerCase(),"1");
			}
			
			strs.append(",");
			/**字典表中的指标*/
			for(int i=0;i<fieldItemList.size();i++)
			{
			    FieldItem temItem=(FieldItem)fieldItemList.get(i);
				boolean g=false;
				field_name=temItem.getItemid();
				if(field_name.toLowerCase().indexOf("attachment")>-1)//过滤附件
					continue;
				strs.append(field_name);
				strs.append(",");
			//	if(!vo.hasAttribute(field_name.toLowerCase()))
				if(att_map.get(field_name.toLowerCase())==null)//如果表中没有这个字段，加上
				{
					table.addField(temItem);				
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
			
			if((this.paramBo.getInfor_type()==2||this.paramBo.getInfor_type()==3)&&(this.paramBo.getOperationType()==8||this.paramBo.getOperationType()==9))
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
			
			if(this.paramBo.getTemplateStatic()!=10&&this.paramBo.getTemplateStatic()!=11) 
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
			
			if(att_map.get("create_time")==null&&"1".equals(this.userView.getHm().get("fillInfo")))
			{
				FieldItem tempitem=new FieldItem();
				tempitem.setItemtype("D");
				tempitem.setItemid("create_time");
				table.addField(tempitem);
			}

			if(table.size()>0)
				dbwizard.addColumns(table);
			
			//判断字符字段的长度是否跟数据字典一致
			table.clear();

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
				
				if(this.paramBo.getTemplateStatic()==10) //单位管理
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
				else if(this.paramBo.getTemplateStatic()==11) //职位管理
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
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
		return bflag;
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
			if(isOnlyComputeFieldVar()){  //计算本模板中的临时变量和模板指标中引入的共享的临时变量。 20150929 liuzy
				buf.append(" and (templetId ="+this.tabId+" or (cstate ='1' and cname in (select field_name from template_set where tabid="+this.tabId+" and nullif(field_name,'') is not null ))) ");
			}else{
				buf.append(" and (templetId ="+this.tabId+"  or cstate ='1') "); 
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
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return fieldlist;
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
			 HashMap field_name_map=getBigMemoMap();
			 for(int i=1;i<=data.getColumnCount();i++)
			 {
					String columnName=data.getColumnName(i).toLowerCase();
				
					//对人员信息群增加三个固定字段B0110_1,E0122_1,A0101_1,单位、部门及姓名
					if("B0110_1".equalsIgnoreCase(columnName)|| "E01A1_1".equalsIgnoreCase(columnName)|| "E0122_1".equalsIgnoreCase(columnName)) //||columnName.equalsIgnoreCase("A0101_1"))
						continue;
					if("codesetid_1".equalsIgnoreCase(columnName)|| "codeitemdesc_1".equalsIgnoreCase(columnName)|| "corcode_1".equalsIgnoreCase(columnName)|| "parentid_1".equalsIgnoreCase(columnName)|| "start_date_1".equalsIgnoreCase(columnName))
						continue;
					if("codesetid_2".equalsIgnoreCase(columnName)|| "codeitemdesc_2".equalsIgnoreCase(columnName)|| "corcode_2".equalsIgnoreCase(columnName)|| "parentid_2".equalsIgnoreCase(columnName)|| "start_date_2".equalsIgnoreCase(columnName))
						continue;
					if(columnName.indexOf("_1")!=-1||columnName.indexOf("_2")!=-1||varMap.get(columnName.toLowerCase())!=null)
					{
						 
						FieldItem _tempItem=null;
						if(varMap.get(columnName.toLowerCase())==null)
						{//表明该字段不是临时变量
							String _columnName=columnName.substring(0,columnName.length()-2);
							_tempItem=DataDictionary.getFieldItem(_columnName);
							if(_tempItem==null&&_columnName.split("_").length>1)//该字段在数据库那种临时表中应该是itemid_subdominid_[1||2]
								_tempItem=DataDictionary.getFieldItem(_columnName.substring(0,columnName.indexOf("_")));
							
						}
						else
							_tempItem=(FieldItem)varMap.get(columnName.toLowerCase());
						if(_tempItem==null)
							continue;
						
						FieldItem tempItem=(FieldItem)_tempItem.cloneItem();
						tempItem.setItemid(columnName);
			 			if(field_name_map!=null&&field_name_map.get(columnName.toLowerCase())!=null)
			 				tempItem.setItemtype("M");
						int columnType=data.getColumnType(i);	
						int size=data.getColumnDisplaySize(i);
						int scale=data.getScale(i);
						switch(columnType)
						{
							case java.sql.Types.BIGINT:
							case java.sql.Types.INTEGER:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale)
										alterList.add(tempItem.cloneField());
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
										alterList.add(tempItem.cloneField());
									else		
										resetList.add(tempItem.cloneField());
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
									if(tempItem.getItemlength()>size)
										alterList.add(tempItem.cloneField());
								}
								else 
									resetList.add(tempItem.cloneField());
								break;
							case java.sql.Types.DOUBLE:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale)
										alterList.add(tempItem.cloneField());
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
										alterList.add(tempItem.cloneField());
									else		
										resetList.add(tempItem.cloneField());
								}
								
								
								break;
							case java.sql.Types.NUMERIC:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale)
										alterList.add(tempItem.cloneField());
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
									else		
										resetList.add(tempItem.cloneField());
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
				    for(int i=0;i<alterList.size();i++)
							table.addField((Field)alterList.get(i));
					if(alterList.size()>0)
							dbw.alterColumns(table);
					 table.clear();
			    }
			    else
			    	syncGzOracleField(data,tableName,varMap);
				 for(int i=0;i<resetList.size();i++)
						table.addField((Field)resetList.get(i));
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
			 HashMap field_name_map=getBigMemoMap();
			 for(int i=1;i<=data.getColumnCount();i++)
			 {
				String columnName=data.getColumnName(i).toLowerCase();
				//对人员信息群增加三个固定字段B0110_1,E0122_1,A0101_1,单位、部门及姓名
			//	if(columnName.equalsIgnoreCase("B0110_1")||columnName.equalsIgnoreCase("E01A1_1")||columnName.equalsIgnoreCase("E0122_1")||columnName.equalsIgnoreCase("A0101_1"))
			//		continue;
				//对人员信息群增加三个固定字段B0110_1,E0122_1,A0101_1,单位、部门及姓名
				if("B0110_1".equalsIgnoreCase(columnName)|| "E01A1_1".equalsIgnoreCase(columnName)|| "E0122_1".equalsIgnoreCase(columnName)) //||columnName.equalsIgnoreCase("A0101_1"))
					continue;
				
				if("codesetid_1".equalsIgnoreCase(columnName)|| "codeitemdesc_1".equalsIgnoreCase(columnName)|| "corcode_1".equalsIgnoreCase(columnName)|| "parentid_1".equalsIgnoreCase(columnName)|| "start_date_1".equalsIgnoreCase(columnName))
					continue;
				if("codesetid_2".equalsIgnoreCase(columnName)|| "codeitemdesc_2".equalsIgnoreCase(columnName)|| "corcode_2".equalsIgnoreCase(columnName)|| "parentid_2".equalsIgnoreCase(columnName)|| "start_date_2".equalsIgnoreCase(columnName))
					continue;
				
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
						if(_tempItem==null&&_columnName.split("_").length>1)
							_tempItem=DataDictionary.getFieldItem(_columnName.substring(0,columnName.indexOf("_")));
						
					}
					else
						_tempItem=(FieldItem)varMap.get(columnName.toLowerCase());
					if(_tempItem==null)
						continue;
					
					
					
					FieldItem tempItem=(FieldItem)_tempItem.cloneItem();
					if(field_name_map!=null&&field_name_map.get(columnName.toLowerCase())!=null)
		 				tempItem.setItemtype("M");
					tempItem.setItemid(columnName);
					int columnType=data.getColumnType(i);	
					int size=data.getColumnDisplaySize(i);
					int scale=data.getScale(i);
					switch(columnType)
					{
						case java.sql.Types.INTEGER:
							if("N".equals(tempItem.getItemtype()))
							{
								if(tempItem.getDecimalwidth()!=scale)
									 alertColumn(tableName,tempItem,dbw,dao);
							}
							if(!"N".equals(tempItem.getItemtype()))
							{
								if("A".equals(tempItem.getItemtype()))
									 alertColumn(tableName,tempItem,dbw,dao);
								
							}
							break;
						case java.sql.Types.VARCHAR:
							if("A".equals(tempItem.getItemtype()))
							{
								if(tempItem.getItemlength()>size)
									 alertColumn(tableName,tempItem,dbw,dao);
							}
							break;
						case java.sql.Types.DOUBLE:
							if("N".equals(tempItem.getItemtype()))
							{
								if(tempItem.getDecimalwidth()!=scale)
									 alertColumn(tableName,tempItem,dbw,dao);
							}
							if(!"N".equals(tempItem.getItemtype()))
							{
								if("A".equals(tempItem.getItemtype()))
									 alertColumn(tableName,tempItem,dbw,dao);
							}
							
							
							break;
						case java.sql.Types.NUMERIC:
							if("N".equals(tempItem.getItemtype()))
							{
								if(tempItem.getDecimalwidth()!=scale)
									 alertColumn(tableName,tempItem,dbw,dao);
							}
							if(!"N".equals(tempItem.getItemtype()))
							{
								if("A".equals(tempItem.getItemtype()))
									 alertColumn(tableName,tempItem,dbw,dao);
								
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
			 for(int i=1;i<=data.getColumnCount();i++)
				 columnMap.put(data.getColumnName(i).toLowerCase(),"1");
			 
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
			 if(rowSet!=null)
				 rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
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
			if(this.paramBo.getTemplateStatic()==10||this.paramBo.getTemplateStatic()==11) //单位管理
			{
				buf.setLength(0);
				buf.append("select b0110 from tmessage where state=0 and noticetempid=");
			}
			buf.append(this.tabId);
			if(!this.userView.isSuper_admin()&& "1".equals(this.paramBo.getFilter_by_manage_priv()))
			{
				/*
				buf.append(" and (tmessage.b0110 like '");
				if((this.userView.getManagePrivCodeValue()==null||this.userView.getManagePrivCodeValue().trim().length()==0)&&this.userView.getManagePrivCode().length()==0)
					buf.append("##");
				else
					buf.append(this.userView.getManagePrivCodeValue());
				buf.append("%' or tmessage.b0110 is null or tmessage.b0110='')");*/
				String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板  
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
								 if("0".equalsIgnoreCase(this.paramBo.getInclude_suborg()))//不包含下属单位
								 {
									 if("UN".equalsIgnoreCase(temp[j].substring(0,2)))
									 {
										 tempSql.append(" or  tmessage.b0110_self ='" + temp[j].substring(2)+ "'");
									 }
									 else
										 tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2)+ "%'");
								 }
								 else
									 tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2)+ "%'");		
								  			
							 }
						}
						if(tempSql.length()>0)
						{
							buf.append(tempSql.substring(3));
						}
						else
							buf.append(" tmessage.b0110='##'");
					}
					else
						buf.append(" tmessage.b0110='##'");
					
					buf.append(" or nullif(tmessage.b0110,'') is null)");
				}
			}
			if(dbw.isExistField("tmessage", "receivetype", false)){
				buf.append(" and (nullif(username,'') is null or (lower(username)='"+userView.getUserName().toLowerCase()+"' and (receivetype='4' or nullif(receivetype,'') is null)) ");
				if(this.getRoleArr(userView).length()>0)
					buf.append(" or (username in("+this.getRoleArr(userView)+") and receivetype='2'))");
				else
					buf.append(" )");
			}else
				buf.append(" and ( nullif(username,'') is null  or lower(username)='"+userView.getUserName().toLowerCase()+"')");

			if(this.paramBo.getTemplateStatic()==10) //单位管理
			{
				buf.append(" and object_type=2 ");
			}
			else if(this.paramBo.getTemplateStatic()==11) //职位管理
			{
				buf.append(" and object_type=3 ");
			}
			else
			{
				buf.append(" and ( object_type is null or object_type=1 ) ");
			}
			//Static=10单位管理（单位业务)、Static=11职位管理（岗位业务）
			if(this.paramBo.getTemplateStatic()==10||this.paramBo.getTemplateStatic()==11)
			{
				ArrayList b0110list=new ArrayList();
				rset=dao.search(buf.toString());
				while(rset.next())
				{
					if(rset.getString("b0110")==null||rset.getString("b0110").trim().length()==0)
						continue;
					b0110list.add(rset.getString("b0110"));
					 			
				} 
				if(this.paramBo.getTemplateStatic()==10)
					this.impDataFromArchive(b0110list,"B"); //直接从档案库中把数据导入进来，
				if(this.paramBo.getTemplateStatic()==11)
					this.impDataFromArchive(b0110list,"K"); //直接从档案库中把数据导入进来，
			}
			else
			{
			
				rset=dao.search(buf.toString());
				while(rset.next())
				{
					base_pre=rset.getString("db_type");
					if(base_pre==null|| "".equalsIgnoreCase(base_pre))
						continue;
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
					if(list.size()>0)
						this.impDataFromArchive(list,pre); //直接从档案库中把数据导入进来，
					//	this.impDataFromArchive(a0100list,pre); //直接从档案库中把数据导入进来，
				
				}			
			}
//			buf.append("insert into ");
//			buf.append(tabname);
//			buf.append(" (a0100,a0101_1,basepre,state) select ");
//			buf.append(" distinct a0100,a0101,db_type,1 from tmessage where noticetempid=");
//			buf.append(this.tabId);
//			buf.append(" and a0100 not in (select a0100 from ");
//			buf.append(tabname);
//			buf.append(")");
//			dao.update(buf.toString());
			/**把变化后数据更新到临时表中去*/
			buf.setLength(0);
			buf.append("select id,a0100,db_type,changelast,changepre from tmessage where state=0 and noticetempid=");
			if(this.paramBo.getTemplateStatic()==10||this.paramBo.getTemplateStatic()==11) //单位管理
			{
				buf.setLength(0);
				buf.append("select id,b0110,changelast,changepre from tmessage where state=0 and noticetempid=");
			}
			buf.append(this.tabId);
			if(!this.userView.isSuper_admin()&& "1".equals(this.paramBo.getFilter_by_manage_priv()))
			{
				/*
				buf.append(" and (tmessage.b0110 like '");
				if((this.userView.getManagePrivCodeValue()==null||this.userView.getManagePrivCodeValue().trim().length()==0)&&this.userView.getManagePrivCode().length()==0)
					buf.append("##");
				else
					buf.append(this.userView.getManagePrivCodeValue());
				buf.append("%' or tmessage.b0110 is null or tmessage.b0110='')");
				*/
				String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板  
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
								 if("0".equalsIgnoreCase(this.paramBo.getInclude_suborg()))//不包含下属单位
								 {
									 if("UN".equalsIgnoreCase(temp[j].substring(0,2)))
									 {
										 tempSql.append(" or  tmessage.b0110_self ='" + temp[j].substring(2)+ "'");
									 }
									 else
										 tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2)+ "%'");
								 }
								 else
									 tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2)+ "%'");	 			
							 }
						}
						if(tempSql.length()>0)
						{
							buf.append(tempSql.substring(3));
						}
						else
							buf.append(" tmessage.b0110='##'");
					}
					else
						buf.append(" tmessage.b0110='##'");
					
					buf.append(" or nullif(tmessage.b0110,'') is null)");
				}
				
			}
			if(dbw.isExistField("tmessage", "receivetype", false)){
				buf.append(" and (nullif(username,'') is null or (lower(username)='"+userView.getUserName().toLowerCase()+"' and (receivetype='4' or nullif(receivetype,'') is null)) ");
				if(this.getRoleArr(userView).length()>0)
					buf.append(" or (username in("+this.getRoleArr(userView)+") and receivetype='2'))");
				else
					buf.append(" )");
			}else
				buf.append(" and ( nullif(username,'') is null  or lower(username)='"+userView.getUserName().toLowerCase()+"')");
			if(this.paramBo.getTemplateStatic()==10) //单位管理
				buf.append(" and object_type=2 ");
			else if(this.paramBo.getTemplateStatic()==11) //职位管理
				buf.append(" and object_type=3 ");
			else
				buf.append(" and ( object_type is null or object_type=1 ) ");
			rset=dao.search(buf.toString());

			String chgpre=null;
			String chglast=null;
			ArrayList paralist=new ArrayList();
			while(rset.next())
			{
				if(this.paramBo.getTemplateStatic()==10||this.paramBo.getTemplateStatic()==11)
				{
					b0110=rset.getString("b0110");
				}
				else
				{
					a0100=rset.getString("a0100");
					base_pre=rset.getString("db_type");
				}
				if(!(this.paramBo.getTemplateStatic()==10||this.paramBo.getTemplateStatic()==11)){
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
			
				if(this.paramBo.getTemplateStatic()==10)//单位管理
				{
					buf.append(" where b0110='"+b0110+"'");
					paralist.add(b0110);
				}
				else if(this.paramBo.getTemplateStatic()==11)//职位管理
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
				buf.append(this.userView.getUserName());
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
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
			  throw GeneralExceptionHandler.Handle(ex);
		  }
		}
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
			buf.append("select count(*) as nmax from tmessage where state=0 and noticetempid=");
			buf.append(this.tabId);
			if(!this.userView.isSuper_admin()&& "1".equals(this.paramBo.getFilter_by_manage_priv()))
			{
				/*
				buf.append(" and (tmessage.b0110 like '");
				if((this.userView.getManagePrivCodeValue()==null||this.userView.getManagePrivCodeValue().trim().length()==0)&&this.userView.getManagePrivCode().length()==0)
					buf.append("##");
				else
					buf.append(this.userView.getManagePrivCodeValue());
				buf.append("%' or tmessage.b0110 is null or tmessage.b0110='')");*/
				
				String operOrg = this.userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板  
				if(operOrg==null||!"UN`".equalsIgnoreCase(operOrg))
				{
					buf.append(" and ( ");
					if(operOrg!=null && operOrg.length() >3)
					{
						StringBuffer tempSql = new StringBuffer(""); 
						String[] temp = operOrg.split("`");
						for (int j = 0; j < temp.length; j++) { 
							 if (temp[j]!=null&&temp[j].length()>0)
								tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2)+ "%'");				
						}
						if(tempSql.length()>0)
						{
							buf.append(tempSql.substring(3));
						}
						else
							buf.append(" tmessage.b0110='##'");
					}
					else
						buf.append(" tmessage.b0110='##'");
					
					buf.append(" or nullif(tmessage.b0110,'') is null)");
				}
			}
			if(dbw.isExistField("tmessage", "receivetype", false)){
				buf.append(" and (nullif(username,'') is null or (lower(username)='"+userView.getUserName().toLowerCase()+"' and (receivetype='4' or nullif(receivetype,'') is null)) ");
				if(this.getRoleArr(userView).length()>0)
					buf.append(" or (username in("+this.getRoleArr(userView)+") and receivetype='2'))");
				else
					buf.append(" )");
			}else
				buf.append(" and ( nullif(username,'') is null  or lower(username)='"+userView.getUserName().toLowerCase()+"')");
			//Object_type	 对象类型	Int	   1:人员  2:单位  3：职位
			if(this.paramBo.getTemplateStatic()==10) //单位管理
			{
				buf.append(" and object_type=2 ");
			}
			else if(this.paramBo.getTemplateStatic()==11) //职位管理
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
			if(rset.next())
				nrec=rset.getInt("nmax");
			if(nrec!=0)
				bflag=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bflag;
	}
   
   private String getInsIdsWhere(ArrayList insIdsList)
   {
       String insIds="";
       for (int i=0;i<insIdsList.size();i++){
           String ins_id= (String)insIdsList.get(i);
           if (i==0)
               insIds= ins_id;
           else 
               insIds= insIds+","+ins_id;      
       }
       return  insIds; 
   }
	/**
	 * 从档案中导入数据
	 * @param a0100s  for examples '0100000','20202020' or 是一个SQL条件
	 * @param dbpre
	 * @param sync =0导入　　=1更新
	 * @return
	 * @throws GeneralException
	 */
	public boolean impDataFromArchive(String a0100s,String dbpre,int sync,ArrayList insIdsList)throws GeneralException
	{
		boolean bflag=true;
		int nmode=0,nhismode=0,ncount=0,nchgstate=0;
        String ErrorSetName="";//用于标志哪个子集的字段在更新的时出错（主要是数据类型出错）
        boolean ErrorSetFlag=false;//是否是由于更新子集信息字段时出的错
		try
		{
			//导入主集中有的数据 
			impMainSetFromArchive(a0100s,dbpre,sync);
			ArrayList setlist=searchUsedSetList();
			String setname=null;
			String cname=null;
			String field_name=null;
			StringBuffer strsql=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=null;
			RowSet rset2=null;
			int db_type=Sql_switcher.searchDbServer();//数据库类型
			String strDesT= this.getBz_tablename();
			String insIdWhere="";
			if (insIdsList.size()>0){
			    insIdWhere=" and ins_id in ("+getInsIdsWhere(insIdsList)+")";
			}
			
			
			StringBuffer strUpdate=new StringBuffer();
			HashMap seqHm=new HashMap();//序号
			/**更新非插入子集区域的值*/
			Document doc=null;
			Element element=null;
			String xpath="/sub_para/para";
			//获得要更新的数据
			String paramname = "a0100";
			if(this.paramBo.getInfor_type()==2)
				paramname="b0110";
			else if(this.paramBo.getInfor_type()==3)
				paramname="e01a1";

			ArrayList a0110list = new ArrayList();
			String[] arr0100s =a0100s.split(",");
			for(int a =0; a<arr0100s.length;a++){
				if(arr0100s[a].trim().length()>0){
					a0110list.add(arr0100s[a].trim().replace("'", ""));
				}
			}

			ArrayList<TemplateItem> fieldList=this.getAllTemplateItem();
			for(TemplateItem item : fieldList){//判断是否有个人附件,引入时候才同步个人附件，更新不在引入
				if(sync == 0&&item.getFieldName()!=null&&
						item.getFieldName().toLowerCase().equalsIgnoreCase("attachment_1fld_"+item.getCellBo().getPageId()+"_"+item.getCellBo().getGridno())//附件名称为了能在列表状态显示多个，被修改为attachment_1fld_+PageId+"_"+Gridno来区分
						&& "1".equals(item.getCellBo().getAttachmentType())){
					boolean isSavaAttachToMinSet = false;
					String archive_attach_to = paramBo.getArchive_attach_to();	
					Boolean attach_history=paramBo.isAttach_history();
		    		FieldSet a01Set = DataDictionary.getFieldSetVo("A01");
		    		if(paramBo.isArchiveAttachToMainSet() && "1".equals(a01Set.getMultimedia_file_flag())){
						isSavaAttachToMinSet = true;
		    		}
		    		this.sysPersonAttachmentToTemplate(arr0100s, dbpre, archive_attach_to.toUpperCase(),insIdsList,isSavaAttachToMinSet,attach_history);
					break;
				}
			}
			
			for(int i=0;i<setlist.size();i++)//遍历所有的数据表  setlist如：[A01, A19, A55]
			{
				setname=(String)setlist.get(i);
				ErrorSetName=setname;
				strsql.setLength(0);
//				if(setname.equalsIgnoreCase("A01")||setname.equalsIgnoreCase("B01")||setname.equalsIgnoreCase("K01"))
//					continue;
				if(db_type==2)//oracle
				{	//strsql.append("select T.field_name,T.ChgState,T.hismode,T.rcount,T.mode_o,T.subflag,T.sub_domain from template_set T ,fielditem M where ");
					strsql.append("select T.Flag, T.disformat,T.field_name,T.ChgState,T.hismode,T.rcount,T.mode_o,T.subflag,T.formula,T.Hz,T.sub_domain from template_set T  where ");		
					//strsql.append("select distinct T.field_name,T.ChgState,T.hismode,T.rcount,T.mode_o,T.subflag from template_set T  where ");
				}
				else
				{
					//	strsql.append("select T.field_name,T.ChgState,T.hismode,T.rcount,T.mode,T.subflag,T.sub_domain from template_set T ,fielditem M where ");
					strsql.append("select T.Flag, T.disformat,T.field_name,T.ChgState,T.hismode,T.rcount,T.mode,T.subflag,T.formula,T.Hz,T.sub_domain from template_set T where ");	
					//strsql.append("select distinct T.field_name,T.ChgState,T.hismode,T.rcount,T.mode,T.subflag  from template_set T where ");
				}
				strsql.append(" T.tabid=");
				strsql.append(this.tabId);
//				strsql.append(" and T.subflag=0 and ((T.field_name=M.itemid and M.useflag<>'0') or (T.field_name='B0110' or T.field_name='E01A1'))");
				strsql.append(" and T.subflag=0 ");
				strsql.append(" and T.flag<>'H' ");
				if(sync==1)
					strsql.append(" and T.chgstate=1 ");
				else 
				{
					if("0".equals(this.paramBo.getChange_after_get_data()))
						strsql.append(" and T.chgstate=1 ");
					else
						strsql.append(" and (T.chgstate=1 or T.chgstate=2) ");
				}
				if(this.paramBo.getInfor_type()==2&& "B01".equalsIgnoreCase(setname))
				{	
					strsql.append(" and ( T.setname='"+setname+"' or T.field_name='codesetid'  or T.field_name='codeitemdesc'  or T.field_name='corcode'  or T.field_name='parentid'  or T.field_name='start_date'  ) ");
				}
				else if(this.paramBo.getInfor_type()==3&& "K01".equalsIgnoreCase(setname))
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
						nmode=rset.getInt("mode_o");
					else
						nmode=rset.getInt("mode");
					ncount=rset.getInt("rcount");
					nhismode=rset.getInt("hismode");
					if(nchgstate==1&&nhismode==0){  //变化前等于0的情况不存在,但是存在的话就有可能是脏数据,这里处理一下，防止报错
					   nhismode=1;
					}
					cname=rset.getString("field_name");//指标名称（没有变化前变化后标志）
					String flags = rset.getString("Flag");// 数据源的标识（文本描述、照片......）
					
					String formula=Sql_switcher.readMemo(rset,"formula");
					if(cname==null)
						continue;
					String sub_domain = Sql_switcher.readMemo(rset,"sub_domain");
					//获得sub_domain_id
					String sub_domain_id="";
					//获得第x到y中的x值
					String his_start2="";
					int his_start =0;
					if(sub_domain!=null&&sub_domain.trim().length()>0&&"1".equals(""+rset.getInt("ChgState"))){
						try{
								doc=PubFunc.generateDom(sub_domain);
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
					if(his_start2.length()>0)
						his_start = Integer.parseInt(his_start2);
					field_name=cname+sub_domain_id+"_"+nchgstate;//指标名称（后面带变化前变化后标志）
					String strSrcT=setname;
					FieldItem fielditem=null;//得到指标的详细信息
					if("codesetid".equalsIgnoreCase(cname)|| "codeitemdesc".equalsIgnoreCase(cname)|| "corcode".equalsIgnoreCase(cname)|| "parentid".equalsIgnoreCase(cname)|| "start_date".equalsIgnoreCase(cname))
					{//如果该指标是特殊指标
						fielditem=new FieldItem();
						fielditem.setItemid(cname);
						fielditem.setItemdesc(rset.getString("hz"));
						if(this.paramBo.getInfor_type()==2)
							fielditem.setFieldsetid("B01");
						else if(this.paramBo.getInfor_type()==3)
							fielditem.setFieldsetid("K01");
						if("start_date".equalsIgnoreCase(cname))
							fielditem.setItemtype("D");
						else 
							fielditem.setItemtype("A");
						if("parentid".equalsIgnoreCase(cname))
							fielditem.setCodesetid("UM");
						else if("codesetid".equalsIgnoreCase(cname))
							fielditem.setCodesetid("orgType");
						else
							fielditem.setCodesetid("0");
						if(!"start_date".equalsIgnoreCase(cname))
							fielditem.setItemlength(50);
						fielditem.setUseflag("1");
					}
					else{
						fielditem=DataDictionary.getFieldItem(cname,setname);//得到指标的详细信息  20150708 dengcan
						if(fielditem!=null)
							fielditem = (FieldItem)fielditem.cloneItem();//将内存中fielditem克隆出来 操作克隆的 20161024 hej
					}
					/**未构库或指标体系不存在时则退出*/
					if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag()))
						continue;
					//日期格式 wangrd 2016-05-05
					fielditem.setDisplayid(rset.getInt("disformat"));
					//添加数值型小数位长度
					if("N".equalsIgnoreCase(fielditem.getItemtype()))
						fielditem.setDecimalwidth(rset.getInt("disformat"));
					/**自动生序规则序号，如备案号，只对变化后指标等*/
					if(nchgstate==2)
					{
						if(fielditem.isSequenceable())
							seqHm.put(fielditem.getItemid().toString(),fielditem.getSequencename());
					}
					if(nchgstate==2&&this.paramBo.getOpinion_field()!=null&&this.paramBo.getOpinion_field().length()>0&&this.paramBo.getOpinion_field().equalsIgnoreCase(fielditem.getItemid()))
					{
						continue;
					}
			//		if(nchgstate!=1)
			//			continue;
					if(nchgstate==2)
						nhismode=1;
					if(fieldstr.indexOf(field_name)!=-1)
						continue;
					
					if(fieldstr2_current.indexOf(field_name)!=-1)
						continue;
					
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
								else
									fieldstr+=",T."+_temp[0]+"=U."+_temp[1]; 
							}
						}
						if(fieldstr.length()>0)
						{
							if(fieldstr.charAt(0)==',')
								fieldstr=fieldstr.substring(1);
							fieldstr2_current=fieldstr;
						}
						if(fieldstr1.length()>0){
							if(fieldstr1.charAt(0)==',')
								fieldstr1=fieldstr1.substring(1);
						}
					}
					else if(nhismode==1)//K B 主集或子集的单条记录
					{
						if(db_type==2||db_type==3)
						{
							fieldstr="T."+field_name;
							fieldstr1="U."+cname;
						}
						else
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
								strUpdate.append(") where ");
								//strUpdate.append(a0100s);
								String[] temp =a0100s.split(",");
								if(temp!=null&&temp.length>0){
									strUpdate.append("(");
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
									strUpdate.append(")");
								}
								strUpdate.append(" and basepre='");
								strUpdate.append(dbpre);
								strUpdate.append("'");
								strUpdate.append(insIdWhere);
								strUpdate.append(" and  exists (select A0100 from "+dbpre+"A01 where "+dbpre+"A01.a0100=T.A0100 ) ");
							}
							else
							{
								strUpdate.append(" where ");
								//strUpdate.append(a0100s);
								String[] temp =a0100s.split(",");
								if(temp!=null&&temp.length>0){
									strUpdate.append("(");
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
									strUpdate.append(")");
								}
								strUpdate.append(" and basepre='");
								strUpdate.append(dbpre);
								strUpdate.append("'");
								strUpdate.append(insIdWhere);
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
									String[] temp =a0100s.split(",");
									if(temp!=null&&temp.length>0){
										strUpdate.append("(");
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
										strUpdate.append(")");
									}
									strUpdate.append(" and basepre='");
									strUpdate.append(dbpre);
									strUpdate.append("'");	
									strUpdate.append(insIdWhere);
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
									String[] temp =a0100s.split(",");
									if(temp!=null&&temp.length>0){
										strUpdate.append("(");
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
										strUpdate.append(")");
									}
									strUpdate.append(" and basepre='");
									strUpdate.append(dbpre);
									strUpdate.append("'");	
									strUpdate.append(insIdWhere);
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

								//写入临时表里
								StringBuffer strUpdate2 = new StringBuffer();
								if(a0110list.size()>0){
									ArrayList valuelist = new ArrayList();
									StringBuffer buf = new StringBuffer();
									buf.append("select rn,");
									buf.append(cname);
									buf.append(",i9999,");
									buf.append(paramname);
									buf.append(" from ");
									buf.append("(select row_number() over(PARTITION by "+paramname+" order by i9999  ) rn,i9999,"+paramname+","+cname+" from  ");
									buf.append(strSrcT+" F  where 1=1");
									//拼接导入的人员
									buf.append(" and "+this.getSqlIn(arr0100s,paramname));
									if(strw.trim().length()>0)
										buf.append(" and ("+strw+") ) cc");
									else
										buf.append(" ) cc");
									//更新记录
									ArrayList reclist=getRecordValue(fieldlist,dao.search(buf.toString()),false);
									buf.setLength(0);
									String a0100_dis = "";
									for(int n=0;n<reclist.size();n++)
									{
										String value = ((ArrayList)reclist.get(n)).get(1).toString().replaceAll("`", "^^");
										String a0100 = ((ArrayList)reclist.get(n)).get(2).toString();
										if(n==0)
											a0100_dis = a0100;
										if(a0100_dis.equals(a0100)) {
											if(value!=null&&!"".equals(value))
												buf.append(value+"`");
										}else {
											String object = a0100_dis;
											if(this.paramBo.getInfor_type()==1&&dbpre.length()>0)
												object = dbpre+object;
											if(buf.length()>0) {
												buf.setLength(buf.length()-1);
												ArrayList paramlist = new ArrayList();
												paramlist.add(buf.toString());
												paramlist.add(object);
												valuelist.add(paramlist);
											}
											buf.setLength(0);
											if(value!=null&&!"".equals(value))
												buf.append(value+"`");
											a0100_dis = a0100;
										}
										if(n==reclist.size()-1){
											String object = a0100;
											if(this.paramBo.getInfor_type()==1&&dbpre.length()>0)
												object = dbpre+object;
											if(buf.length()>0) {
												buf.setLength(buf.length()-1);
												ArrayList paramlist = new ArrayList();
												paramlist.add(buf.toString());
												paramlist.add(object);
												valuelist.add(paramlist);
											}
										}
									}
									//批量更新
									if(valuelist.size()>0) {
										strUpdate2.setLength(0);
										strUpdate2.append("Update ");
										strUpdate2.append(strDesT);
										strUpdate2.append(" set ");
										strUpdate2.append(field_name);
										strUpdate2.append(" =?");
										strUpdate2.append(" where ");
										if(this.paramBo.getInfor_type()==1)
											strUpdate2.append("basepre"+Sql_switcher.concat()+paramname);
										else
											strUpdate2.append(paramname);
										strUpdate2.append(" =? ");
										strUpdate2.append(insIdWhere);
										dao.batchUpdate(strUpdate2.toString(), valuelist);
									}
									flag =true;
								}
							}
							else if(nhismode==4||(nhismode==2&&(nmode==0 || nmode==1 || nmode==2 || nmode==3))) //条件序号或者多条记录
							{
								//<EXPR>1</EXPR><FACTOR>A0420=01</FACTOR> 条件的格式
								String strw = "";
								if(nhismode==4) {
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
									strw=factorlist.getSingleTableSqlExpression("F");
								}
								//写入临时表里
								StringBuffer strUpdate2 = new StringBuffer();
								if(a0110list.size()>0){
									ArrayList valuelist = new ArrayList();
									StringBuffer buf = new StringBuffer();		
									buf.append("select rn,i9999,");
									buf.append(cname+",");
									buf.append(paramname);
									buf.append(" from (select ROW_NUMBER() over(PARTITION by "+paramname+" order by i9999 ");
									if(nmode==0||nmode==1)
										buf.append(" desc");
									else
										buf.append(" asc");
									buf.append(" ) rn,i9999,"+paramname+","+cname);
									buf.append(" from "+strSrcT+" F where 1=1 ");
									//拼接导入的人员
									buf.append(" and "+this.getSqlIn(arr0100s,paramname));
									if(nhismode==4&&strw.trim().length()>0)
										buf.append(" and ("+strw+")) a where ");
									else
										buf.append("  ) a where ");
									switch(nmode)
									{
									case 0://倒数第...条（最近第）
									case 2://正数第...条(最初第)
										buf.append(" a.rn="+ncount);													
										break;
									case 1://倒数...条（最近）
									case 3://正数...条（最初）
										if(his_start==0){
											buf.append(" a.rn<="+ncount);
										}else{
											buf.append(" a.rn<="+(his_start+ncount-1)+" and a.rn>="+his_start);
										}
										break;
									}
									//更新记录（处理附件，将其他类型值放到list中）
									ArrayList reclist=getRecordValue(fieldlist,dao.search(buf.toString()),nmode,nhismode,false);
									String a0100_dis = "";
									buf.setLength(0);
									for(int n=0;n<reclist.size();n++)
									{
										String value = ((ArrayList)reclist.get(n)).get(1).toString().replaceAll("`", "^^");
										String a0100 = ((ArrayList)reclist.get(n)).get(2).toString();
										if(n==0)
											a0100_dis = a0100;
										if(a0100_dis.equals(a0100)) {
											if(value!=null&&!"".equals(value))
												buf.append(value+"`");
										}else {
											String object = a0100_dis;
											if(this.paramBo.getInfor_type()==1&&dbpre.length()>0)
												object = dbpre+object;
											if(buf.length()>0) {
												buf.setLength(buf.length()-1);
												ArrayList paramlist = new ArrayList();
												if("D".equalsIgnoreCase(fielditem.getItemtype())) {
													if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
														paramlist.add(buf.toString().replace(".", "-"));
													}else {
														paramlist.add(buf.toString());
													}
												}else {
													paramlist.add(buf.toString());
												}
												paramlist.add(object);
												valuelist.add(paramlist);
											}
											buf.setLength(0);
											if(value!=null&&!"".equals(value))
												buf.append(value+"`");
											a0100_dis = a0100;
										}
										if(n==reclist.size()-1){
											String object = a0100;
											if(this.paramBo.getInfor_type()==1&&dbpre.length()>0)
												object = dbpre+object;
											if(buf.length()>0) {
												buf.setLength(buf.length()-1);
												ArrayList paramlist = new ArrayList();
												if("D".equalsIgnoreCase(fielditem.getItemtype())) {
													if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
														paramlist.add(buf.toString().replace(".", "-"));
													}else {
														paramlist.add(buf.toString());
													}
												}else {
													paramlist.add(buf.toString());
												}
												paramlist.add(object);
												valuelist.add(paramlist);
											}
										}
									}
									
									//批量更新
									if(valuelist.size()>0) {
										strUpdate2.setLength(0);
										strUpdate2.append("Update ");
										strUpdate2.append(strDesT);
										strUpdate2.append(" set ");
										strUpdate2.append(field_name);
										if("D".equalsIgnoreCase(fielditem.getItemtype())) {
											if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
												strUpdate2.append(" =to_date(?,'yyyy-mm-dd hh24:mi:ss')");
											}else {
												strUpdate2.append(" =?");
											}
										}else {
											strUpdate2.append(" =?");
										}
										strUpdate2.append(" where ");
										if(this.paramBo.getInfor_type()==1)
											strUpdate2.append("basepre"+Sql_switcher.concat()+paramname);
										else
											strUpdate2.append(paramname);
										strUpdate2.append(" =? ");
										strUpdate2.append(insIdWhere);
										dao.batchUpdate(strUpdate2.toString(), valuelist);
									}
									flag =true;
								}
						
							}
						}
						break;
					case 'B'://单位信息
					case 'b':
						if(this.paramBo.getInfor_type()==1)
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
	          					   strUpdate.append(" and (");//lis 20160901 下面已有A0100 in信息 此处去掉 k01也做相应更改
								 else
	          					   strUpdate.append(" where ");
								//  strUpdate.append(" and T.A0100 in (");
									//strUpdate.append(a0100s);
								 String[] temp =a0100s.split(",");
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
									strUpdate.append(insIdWhere);
								//	strUpdate.append("')"); //cmq added ")";
									if(db_type==2||db_type==3) //oracle,db2
										strUpdate.append(" )) "); 
							}
							else
							{
								if(nhismode==1) { //当前记录
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
									if(db_type==2||db_type==3)
										strUpdate.append(" ) where ");
									else
										strUpdate.append(" and ");
									//strUpdate.append(" T.A0100 in (");
									//strUpdate.append(a0100s);
									String[] temp =a0100s.split(",");
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
									strUpdate.append(insIdWhere);
								}else {
									this.updateDataToField(formula,a0110list,cname,paramname,strSrcT,dbpre,nmode,ncount,strDesT,fieldlist,field_name,nhismode,setname,his_start);
									strUpdate.setLength(0);
									flag=true;
								}
							}
						}
						else if(this.paramBo.getInfor_type()==2)
						{
						 String  str =	impDataFromBKarchive(strSrcT,db_type,strDesT,fieldstr,fieldstr1,setname,a0100s,ncount,nmode,nhismode,formula,dao,a0110list,cname,fieldlist,field_name,paramname,dbpre,his_start);
						if(str.length()>0)	
						 strUpdate.append(str);
						else
							flag =true;
						}
						
						break;
					case 'K'://职位信息
					case 'k':
						if(this.paramBo.getInfor_type()==1)
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
	            					   strUpdate.append(" and (");
	            				   else
	            					   strUpdate.append(" where ");
									//strUpdate.append(a0100s);
	            				   String[] temp =a0100s.split(",");
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
									strUpdate.append(insIdWhere);
									if(db_type==2||db_type==3) //oracle,db2
										strUpdate.append(" )) "); 
							}
							else
							{
								if(nhismode==1) { //当前记录
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
									if(db_type==2||db_type==3)
										strUpdate.append(" ) where ");
									else
										strUpdate.append(" and ");
									String[] temp =a0100s.split(",");
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
									strUpdate.append(insIdWhere);
								}else {
									this.updateDataToField(formula,a0110list,cname,paramname,strSrcT,dbpre,nmode,ncount,strDesT,fieldlist,field_name,nhismode,setname,his_start);
									strUpdate.setLength(0);
									flag=true;
								}
							}
						}
						else if(this.paramBo.getInfor_type()==3)
						{
							String  str =	impDataFromBKarchive(strSrcT,db_type,strDesT,fieldstr,fieldstr1,setname,a0100s,ncount,nmode,nhismode,formula,dao,a0110list,cname,fieldlist,field_name,paramname,dbpre,his_start);
							if(str.length()>0)	
							 strUpdate.append(str);
							else
								flag =true;
						}
						break;
					}
					if(flag)
						continue;
			 		//System.out.println("="+strUpdate.toString());
					dao.update(strUpdate.toString());
					
				}//while rset loop end.
			}//for i loop end.
			ErrorSetFlag=true;
			//不被  change_after_get_data="0";  //1：变化后指标取当前值  0：不取  控制
			if(sync==0)
			{
				strsql.setLength(0);
				if(db_type==2)
					strsql.append("select distinct T.field_name,T.ChgState,T.hismode,T.rcount,T.mode_o,T.subflag from template_set T ");
				else
					strsql.append("select distinct T.field_name,T.ChgState,T.hismode,T.rcount,T.mode,T.subflag from template_set T ");
				strsql.append(" where  T.tabid="+this.tabId+" and T.subflag=0 ");
				rset=dao.search(strsql.toString());
				while(rset.next())
				{
					nchgstate=rset.getInt("chgstate");
					cname=rset.getString("field_name");
					if(cname==null)
						continue;
					FieldItem fielditem=DataDictionary.getFieldItem(cname);
					/**未构库或指标体系不存在时则退出*/
					if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag()))
						continue;
					/**自动生序规则序号，如备案号，只对变化后指标等*/
					if(nchgstate==2)
					{
						if(fielditem.isSequenceable())
							seqHm.put(fielditem.getItemid().toString(),fielditem.getSequencename());
					}
				}
			}
			/**导入插入子集区域的数据*/
			strsql.setLength(0);
			if(db_type==2)//oracle
				strsql.append("select T.setname,T.ChgState,T.formula,T.hismode,T.rcount,T.mode_o,T.subflag,T.sub_domain from template_set T  where ");
			else
				strsql.append("select T.setname,T.ChgState,T.formula,T.hismode,T.rcount,T.mode,T.subflag,T.sub_domain from template_set T  where ");
			strsql.append(" T.tabid=");
			strsql.append(this.tabId);
			strsql.append(" and ");
			strsql.append(" subflag=1");
			rset=dao.search(strsql.toString());
			while(rset.next())
			{
				strUpdate.setLength(0);
				nchgstate=rset.getInt("chgstate");
				if(db_type==2)//oracle
					nmode=rset.getInt("mode_o");
				else
					nmode=rset.getInt("mode");
				ncount=rset.getInt("rcount");
				nhismode=rset.getInt("hismode");
				cname=rset.getString("setname");
				field_name="t_"+cname+"_"+nchgstate;
				/**导入插入子集区域的数据*/
				String subxml=Sql_switcher.readMemo(rset, "sub_domain");
				String formula=Sql_switcher.readMemo(rset, "formula");
				/**对插入子集区域数据同步规则，变化前或第一次导入时
				 * 勾选了autosync_beforechg_item自动同步变化前指标数据的时候不同步变化前子集，没有勾选的话手工点击刷新则同步变化前子集
				 * */
				if ((sync == 0 || (nchgstate == 1&&"0".equals(this.paramBo.getAutosync_beforechg_item()))) && cname != null
						&& cname.trim().length() > 0) {
					// 获得sub_domain_id
					String sub_domain_id = "";
					// 获得第x到y中的x值
					String his_start2 = "";
					int his_start = 0;
					if (subxml != null && subxml.trim().length() > 0) {
						try {
							doc = PubFunc.generateDom(subxml);
							XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							List childlist = findPath.selectNodes(doc);
							if (childlist != null && childlist.size() > 0) {
								element = (Element) childlist.get(0);
								if (element.getAttributeValue("id") != null/* &&"1".equals(""+nchgstate) */) {
									sub_domain_id = (String) element.getAttributeValue("id");
									if (sub_domain_id != null&& sub_domain_id.trim().length() > 0) {
									} else {
										sub_domain_id = "";
									}
								}
								if (element.getAttributeValue("his_start") != null) {
									his_start2 = (String) element.getAttributeValue("his_start");
									if (his_start2 != null&& his_start2.trim().length() > 0) {
									} else {
										his_start2 = "";
									}
								}
							}
						} catch (Exception e) {

						}
					}
					

					if(his_start2.length()>0)
						his_start = Integer.parseInt(his_start2);
					if(sub_domain_id!=null&&sub_domain_id.length()>0)
						field_name="t_"+cname+"_"+sub_domain_id+"_"+nchgstate;
					impSubDomainData(a0100s,dbpre,subxml,cname,field_name.toLowerCase(),nhismode,nmode,ncount,formula,his_start,insIdsList,"0");
				}
			}//while rset loop end.
			/**导入插入子集区域的数据区域结束*/
			/**生成序号*/
			/**首次导入时,才需要进行序号生成*/
			if(sync==0&&!"1".equals(this.paramBo.getId_gen_manual()))
				createRuleSequenceNo(seqHm,a0100s,dbpre,strDesT,"0");	
			
			//写入唯一标识 seqnum
			if ("0".equals(this.taskId))
			{
				String tablename=strDesT;
			
				String sql="select * from "+tablename ;
				IDGenerator idg=new IDGenerator(2,this.conn);	
				String kq_id_str="";
				String kq_seqnum_id=""; 
				String mb_seqnum_id=""; //模板对应的考勤单据号指标
				if(this.paramBo.getInfor_type()==1) 
				{
					StringBuffer str = new StringBuffer();
					String[] temp =a0100s.split(",");
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
					//考勤申请模板
					TemplateTableParamBo tp=new TemplateTableParamBo(tabId,this.conn);
					String mapping=getKqParam(1,tp); 
					String kqTab=getKqParam(2,tp);
					kq_seqnum_id=kqTab+"01"; 
				    kq_id_str=getKqParam(4,tp); 
				    mb_seqnum_id=getKqParam(5,tp); 
				}
				else if(this.paramBo.getInfor_type()==2) //单位信息处理
					sql+=" where B0110 in ("+a0100s+") ";
				else if(this.paramBo.getInfor_type()==3) //职位信息处理 
					sql+=" where E01A1 in ("+a0100s+") "; 
				RowSet rowSet=dao.search(sql);
				while(rowSet.next())
				{
					String	seqnum = rowSet.getString("seqnum");
					if(seqnum==null||seqnum.trim().length()==0){
					    seqnum=CreateSequence.getUUID();
						if(this.paramBo.getInfor_type()==1) 
						{
							dao.update("update "+tablename+" set seqnum='"+seqnum+"',submitflag=1 where a0100='"+rowSet.getString("a0100")+"' and  lower(basepre)='"+dbpre.toLowerCase()+"'");
						}
						else if(this.paramBo.getInfor_type()==2)
						{
							dao.update("update "+tablename+" set seqnum='"+seqnum+"',submitflag=1 where b0110='"+rowSet.getString("b0110")+"'");
						}
						else if(this.paramBo.getInfor_type()==3)
						{
							dao.update("update "+tablename+" set seqnum='"+seqnum+"',submitflag=1 where E01A1='"+rowSet.getString("E01A1")+"'");
						}
					}
					//自动生成考勤单据号
					try{
					    if(kq_id_str.length()>0&&mb_seqnum_id.length()>0&&this.paramBo.getInfor_type()==1)
	                    {
	                        String kq_id=idg.getId(kq_id_str);
	                        dao.update("update "+tablename+" set "+mb_seqnum_id+"='"+kq_id+"' where a0100='"+rowSet.getString("a0100")+"' and  lower(basepre)='"+dbpre.toLowerCase()+"'");
	                    }
					}catch(Exception e){
					    throw new GeneralException("模板中考勤指标对应出现问题,请重新对应考勤指标!");
					}
				}
				if(sync==0){
					ArrayList list=new ArrayList();
					for(int i=0;i<fieldList.size();i++){
						String fieldStr="";
						String whereSql="";
						TemplateItem item=fieldList.get(i);
						TemplateSet setBo = item.getCellBo();
						if(!setBo.isSubflag()&&StringUtils.isNotBlank(setBo.getDefaultValue())){
							ArrayList sqlList=new ArrayList();
							String defaultValue="'"+setBo.getDefaultValue()+"'";
							String id = setBo.getSub_domain_id();
							if("D".equalsIgnoreCase(setBo.getOld_fieldType())&&"SYSTIME".equalsIgnoreCase(setBo.getDefaultValue())){
								defaultValue=Sql_switcher.sqlNow();
							}
							if(id!=null&&id.trim().length()>0){
								id = "_"+id;
							}else{
								id="";
							}
							field_name=setBo.getField_name()+id+"_"+setBo.getChgstate();
							if("D".equalsIgnoreCase(setBo.getField_type())&&!"SYSTIME".equalsIgnoreCase(setBo.getDefaultValue())){	//SYSTIME不需要在格式化日期。
								if(Sql_switcher.searchDbServer()==Constant.ORACEL)
									fieldStr=field_name+"=to_date("+defaultValue+",'YYYY-MM-DD HH24:MI:SS')";
								else{
									fieldStr=field_name+"=convert(varchar,"+defaultValue+",120)";
								}
							}else{
								fieldStr=field_name+"="+defaultValue;
							}
							if(Sql_switcher.searchDbServer()==Constant.ORACEL)
								whereSql=" ( "+field_name+" is null or length(cast("+field_name+" as varchar(100)))=0 ) ";
							else{
								whereSql=" ( "+field_name+" is null or len(cast("+field_name+" as varchar(max)))=0 ) ";//bug 48048 sqlserver 数据库通过isnull 不能处理空值的字段。通过len函数判断
							}
							sqlList.add(fieldStr);
							sqlList.add(whereSql);
							list.add(sqlList);
						}
					}
					if(list.size()>0){
						ArrayList updateSqlList=new ArrayList();
						if(1==this.paramBo.getInfor_type()){
							String whereSql="";
							String[] temp =a0100s.split(",");
							if(temp!=null&&temp.length>0){
								
								int zheng=temp.length/999;
								int yu = temp.length%999;
								for (int j = 0; j < zheng; j++) {
									if(j!=0){
										whereSql+=" or ";
									}
									whereSql+=" A0100 in (";
									for(int a=j*999;a<(j+1)*999;a++){
										if(a!=j*999){
											whereSql+=",";
										}
										whereSql+=temp[a];
									}
									whereSql+=")";
								}
								if(zheng==0){
									if(yu>0){
										whereSql+=" A0100 in (";
										for(int a=zheng*999;a<zheng*999+yu;a++){
											if(a!=zheng*999){
												whereSql+=",";
											}
											whereSql+=temp[a];
										}
										whereSql+=")";
									}
								}else{
									if(yu>0){
										whereSql+=" or A0100 in (";
										for(int a=zheng*999;a<zheng*999+yu;a++){
											if(a!=zheng*999){
												whereSql+=",";
											}
											whereSql+=temp[a];
										}
										whereSql+=")";
									}
								}
								
							}
							whereSql+=" and basepre='";
							whereSql+=dbpre;
							whereSql+="'";
							whereSql+=insIdWhere;
							//if(db_type==2||db_type==3) //oracle,db2
							//	whereSql+=" )) "; 
							for(int i=0;i<list.size();i++){
								ArrayList sqlList=(ArrayList) list.get(i);
								String updateSql="update "+strDesT+" set "+sqlList.get(0);
								if(StringUtils.isNotBlank(whereSql)){
									updateSql+=" where "+whereSql +" and "+sqlList.get(1);
								}else{
									updateSql+=" where "+sqlList.get(1);
								}
								updateSqlList.add(updateSql);
							}
						}else{
							String key_str="B0110";
							if(this.paramBo.getInfor_type()==3)
								key_str="E01A1";
							
							for(int i=0;i<list.size();i++){
								ArrayList sqlList=(ArrayList) list.get(i);
								String updateSql="update "+strDesT+" set "+sqlList.get(0);
								if(StringUtils.isNotBlank(a0100s)){
									updateSql+="  where "+key_str+" in("+a0100s+") and" +sqlList.get(1);;
								}else{
									updateSql+=" where "+sqlList.get(1);
								}
								updateSqlList.add(updateSql);
							}
						}
						
						if(updateSqlList.size()>0){
							dao.batchUpdate(updateSqlList);
						}
					}
					
				}
			}
			if(rset!=null)
				rset.close();
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
	 * 人员模板插入单位或者岗位子集指标，设置条件定位条件序号，多条记录时，更新数据
	 * @param formula
	 * @param a0110list
	 * @param cname
	 * @param paramname
	 * @param strSrcT
	 * @param dbpre
	 * @param nmode
	 * @param ncount
	 * @param strDesT
	 * @param fieldlist
	 * @param field_name
	 * @param nhismode
	 * @param setname
	 * @param his_start
	 * @throws GeneralException
	 */
	public void updateDataToField(String formula, ArrayList a0110list, String cname, String paramname, String strSrcT, String dbpre, int nmode, int ncount, String strDesT, ArrayList fieldlist, String field_name, int nhismode, String setname, int his_start) throws GeneralException{
		String expr="";
		String factor="";
		String field = "";
		String field_1 = "";
		String strw = "";
		try {
			if(setname.toUpperCase().startsWith("B")) {
				field_1 = "B0110_1";
				field = "B0110";
			}else if(setname.toUpperCase().startsWith("K")) {
				field_1 = "E01A1_1";
				field = "E01A1";
			}
			if(nhismode==3||nhismode==4) {//条件定位，条件序号
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
				strw=factorlist.getSingleTableSqlExpression("F");
			}
			//写入临时表里
			StringBuffer strUpdate2 = new StringBuffer();
			if(a0110list.size()>0){
				for(int m =0;m<a0110list.size();m++){
					StringBuffer buf = new StringBuffer();
					buf.append("select ");
					buf.append("F."+cname);
					buf.append(",F.i9999,T."+paramname);
					buf.append(" from "+strDesT);
					buf.append(" T left join ");
					buf.append(strSrcT+" F ON T."+field_1+"=F."+field+" ");
					buf.append(" where ");
					buf.append("T.a0100='"+a0110list.get(m)+"'");
					buf.append(" and T.basepre ='"+dbpre+"'");
					if(strw.trim().length()>0)
						buf.append(" and ("+strw+") ");
					ArrayList i9999list=getSubSetI9999s(buf.toString()+" order by F.i9999");
					ArrayList paralist = new ArrayList();
					if(nhismode==2||nhismode==4) {
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
										if(size==ncount)
											curri9999=(String)i9999list.get(0);
										else
										{
											if(ncount!=0)
												curri9999=(String)i9999list.get(size-ncount);
											else
												curri9999=(String)i9999list.get(size-ncount-1);
										}
									}
									buf.append(" and F.I9999=?");							
								paralist.add(curri9999);							
								break;
							case 1://倒数...条（最近）
								if(his_start==0){
									if(size>=ncount)
									{
										if(size==ncount)
											curri9999=(String)i9999list.get(0);
										else
											curri9999=(String)i9999list.get(size-ncount);
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
											if(size<his_start+ncount)
												curri9999=(String)i9999list.get(0);
											else
												curri9999=(String)i9999list.get(size-ncount-(his_start-1));
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
								if(size>=ncount)
									curri9999=(String)i9999list.get(ncount-1);
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
					}
					//更新记录
					ArrayList reclist = new ArrayList();
					if(paralist.size()>0)
						reclist=getRecordValue(fieldlist,dao.search(buf.toString(),paralist),true);
					else
						reclist=getRecordValue(fieldlist,dao.search(buf.toString()),true);
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
					if(this.paramBo.getInfor_type()==1&&dbpre.length()>0)	
						strUpdate2.append(" and basepre ='"+dbpre+"'");
					dao.update(strUpdate2.toString());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 将人员串转成sql where条件
	 * @param a0100arr
	 * @param paramname 
	 * @return
	 */
	private StringBuffer getSqlIn(String[] a0100arr, String paramname) {	
		StringBuffer strUpdate = new StringBuffer();
		int zheng=a0100arr.length/999;
		int yu = a0100arr.length%999;
		for (int j = 0; j < zheng; j++) {
			if(j!=0){
				strUpdate.append("or ");
			}
			strUpdate.append(" "+paramname+" in (");
			for(int a=j*999;a<(j+1)*999;a++){
				if(a!=j*999){
					strUpdate.append(",");
				}
				strUpdate.append(a0100arr[a]);
			}
			strUpdate.append(")");
		}
		if(zheng==0){
			if(yu>0){
				strUpdate.append(" "+paramname+" in (");
				for(int a=zheng*999;a<zheng*999+yu;a++){
					if(a!=zheng*999){
						strUpdate.append(",");
					}
					strUpdate.append(a0100arr[a]);
				}
				strUpdate.append(")");
			}
		}else{
			if(yu>0){
				strUpdate.append(" or "+paramname+" in (");
				for(int a=zheng*999;a<zheng*999+yu;a++){
					if(a!=zheng*999){
						strUpdate.append(",");
					}
					strUpdate.append(a0100arr[a]);
				}
				strUpdate.append(")");
			}
		}
		return strUpdate;
	}
	private ArrayList getRecordValue(ArrayList list, RowSet rset,int nmode, int nhismode,boolean isFormat) {
		ArrayList reclist=new ArrayList();
		String value="";
		try
		{
			String paramname = "a0100";
			if(this.paramBo.getInfor_type()==2)
				  paramname="b0110";
			else if(this.paramBo.getInfor_type()==3)
				  paramname="e01a1";
			ConstantXml constantXml = new ConstantXml(this.conn,"FILEPATH_PARAM");
			String rootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
            rootDir=rootDir.replace("\\",File.separator);
            if (!rootDir.endsWith(File.separator)) rootDir =rootDir+File.separator;
            rootDir += "multimedia"+File.separator;
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
				int disformat =item.getDisplayid();
				if("M".equalsIgnoreCase(field_type))
				{
					value=Sql_switcher.readMemo(rset,field_name);
				}
				else if("D".equalsIgnoreCase(field_type))
				{
					/**yyyy-MM-dd*/
					value=PubFunc.FormatDate(rset.getTimestamp(field_name),"yyyy-MM-dd HH:mm:ss");
					if(isFormat) {
						value=utilBo.getFormatDate(value,disformat);//需要格式化数据
					}
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
						String sql="select id,filename,path,class,srcfilename,topic,ext from hr_multimedia_file where childguid='"+GuidKey+"'";
						RowSet flagset=dao.search(sql); 
						int m=0;
						String valuestr="";
						while(flagset.next()){
							m++;
							String id=flagset.getString("id");                   //文件唯一标识 
							String filename=flagset.getString("filename");       //编码后文件名
							String path= rootDir + flagset.getString("path").replace("\\", File.separator);               //文件上传路径
							String srcfilename=flagset.getString("topic")+flagset.getString("ext"); //原始文件名 
							String filetype = flagset.getString("class");//文件类型
					        File f= new File(path,filename);  
					        long s=0;
							if (f.exists()) {
								FileInputStream fis = null;
								try {
									fis = new FileInputStream(f);
									s = fis.available();
								} finally {
									PubFunc.closeResource(fis);
								}
							} else {
					            //f.createNewFile();
					        }
					        DecimalFormat df = new DecimalFormat("#0.00");
					        String fileSizeString = "";
					        //if (s < 1024) {
					        //    fileSizeString = df.format((double) s) + "B";
					        //} else
					        if (s < 1048576) {
					            fileSizeString = df.format((double) s / 1024) + "K";
					        } else if (s < 1073741824) {
					            fileSizeString = df.format((double) s / 1048576) + "M";
					        } 
					           // else {
					           // fileSizeString = df.format((double) s / 1073741824) +"G";
					       // }
							String text=filename+"|"+path+"|"+srcfilename+"|"+fileSizeString+"|"+id+"|"+m+"|"+"type:"+filetype ;
							valuestr+=text+",";
						}
						if(valuestr.length()>0){
							valuestr=valuestr.substring(0, valuestr.length()-1);
							valuestr = this.saveSubAttachmentToTemplate(valuestr);//将子集附件保存到模板临时目录 lis 20160912 add
							valuelist.add(valuestr);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				  
			  }
			  String a0100 = rset.getString(paramname);
			  valuelist.add(a0100);
			  if((nhismode==2||nhismode==4)&&(nmode==1||nmode==0))
				  reclist.add(0,valuelist);
			  else
				  reclist.add(valuelist);
			}	  
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return reclist;
	}
	/**
	 * 获得模板设置的考勤参数
	 * @param flag 1:模板与申请单指标对应关系  2：考勤申请单表名  3：考勤申请单序号 4：申请单序号生成串  5：模板对应考勤申请单序号指标
	 * @return
	 */
	private String getKqParam(int flag,TemplateTableParamBo tp)
	{
		String param_str="";
		if(flag==1)
			param_str=tp.getKq_field_mapping();
		else if(flag==2)
		{
			param_str=tp.getKq_setid();
		}
		else if(flag==3)
		{
			String kqTab=tp.getKq_setid();
			if(kqTab.length()>0)
				param_str=kqTab+"01";
		}
		else if(flag==4)
		{
			String kqTab=tp.getKq_setid();
			String kq_id_str="";
			if("Q11".equalsIgnoreCase(kqTab)) //加班
				kq_id_str="Q11.Q1101";
			else if("Q13".equalsIgnoreCase(kqTab)) //公出
				kq_id_str="Q13.Q1301";
			else if("Q15".equalsIgnoreCase(kqTab)) //请假
				kq_id_str="Q15.Q1501";
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
	 * 根据序号生成器规则，生成对应的序号
	 * @param seqHm
	 * @param a0100s
	 * @param dbpre
	 * @param strDesT
	 */
	private void createRuleSequenceNo(HashMap seqHm,String a0100s,String dbpre,String strDesT,String ins_id)throws GeneralException
	{
        Iterator seq=seqHm.entrySet().iterator();
        IDGenerator idg=new IDGenerator(2,this.conn);     
        String[] a0100arr=StringUtils.split(a0100s,",");
        String a0100=null;
        StringBuffer buf=new StringBuffer();
        String normname = "";
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
			if(this.paramBo.getOperationType()==0||this.paramBo.getOperationType()==5)
				end_str="_2";
			
			while(seq.hasNext())
			{
				Entry entry=(Entry)seq.next();
				String fieldname=(String)entry.getKey();
				FieldItem fielditem=DataDictionary.getFieldItem(fieldname);
				normname = fielditem.getItemdesc();
				if(fielditem==null)
					continue;
				seqname=(String)entry.getValue();
				
				RecordVo factoryVo=getFactoryIdVo(seqname);
				if(fielditem.getC_rule()==1)//同单同号,同业务中的对象文书号一样
				{
					
					String prefix_field_value="";
					String prefix_field=factoryVo.getString("prefix_field");
					if(this.paramBo.getOperationType()!=0&&this.paramBo.getOperationType()!=5&&columnMap.get(prefix_field.toLowerCase()+end_str)==null){
						end_str="_2";
					}
					if(prefix_field!=null&&prefix_field.trim().length()>0&&columnMap.get(prefix_field.toLowerCase()+end_str)!=null)
					{
						seq_no=getSequenceNoInEqual(fieldname+"_2",strDesT,prefix_field+end_str,fielditem.getPrefix_field_len());
						if(seq_no==null|| "".equalsIgnoreCase(seq_no))
							seq_no=idg.getId(seqname);
						if(seq_no.length()>fielditem.getItemlength())
							throw new GeneralException(ResourceFactory.getProperty("error.seqno.length"));
						
						for(int i=0;i<a0100arr.length;i++)
						{
							a0100=a0100arr[i];
							if(a0100.indexOf("'")==-1)
								a0100="'"+a0100+"'";
							prefix_field_value="";
							
							String _subStr=" a0100="+a0100+" and  upper(basepre)='"+dbpre.toUpperCase()+"'";
							if(this.paramBo.getInfor_type()==2)
								_subStr=" b0110="+a0100+"";
							if(this.paramBo.getInfor_type()==3)
								_subStr=" e01a1="+a0100+"";
							if(ins_id!=null&&!"0".equals(ins_id))  //20160612 邓灿
								_subStr+=" and ins_id="+ins_id;
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
						if(seq_no==null|| "".equalsIgnoreCase(seq_no))
							seq_no=idg.getId(seqname);
						if(seq_no.length()>fielditem.getItemlength())
							throw new GeneralException(ResourceFactory.getProperty("error.seqno.length"));
						for(int i=0;i<a0100arr.length;i++)
						{
							buf.setLength(0);
							a0100=a0100arr[i];
							if(a0100.indexOf("'")==-1)
								a0100="'"+a0100+"'";
							String _subStr=" a0100="+a0100+" and  upper(basepre)='"+dbpre.toUpperCase()+"'";
							if(this.paramBo.getInfor_type()==2)
								_subStr=" b0110="+a0100+"";
							if(this.paramBo.getInfor_type()==3)
								_subStr=" e01a1="+a0100+"";
							if(ins_id!=null&&!"0".equals(ins_id))  //20160612 邓灿
								_subStr+=" and ins_id="+ins_id;
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
					if(this.paramBo.getOperationType()!=0&&this.paramBo.getOperationType()!=5&&columnMap.get(prefix_field.toLowerCase()+end_str)==null){
						end_str="_2";
					}
					for(int i=0;i<a0100arr.length;i++)
					{
						a0100=a0100arr[i];
						if(a0100.indexOf("'")==-1)
							a0100="'"+a0100+"'";
						String _subStr=" a0100="+a0100+" and  upper(basepre)='"+dbpre.toUpperCase()+"'";
						if(this.paramBo.getInfor_type()==2)
							_subStr=" b0110="+a0100+"";
						if(this.paramBo.getInfor_type()==3)
							_subStr=" e01a1="+a0100+"";
						if(ins_id!=null&&!"0".equals(ins_id))  //20160612 邓灿
							_subStr+=" and ins_id="+ins_id;
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
				    	//判断有没有子序列 hej 20170715
				    	String backfix="";
			 	        if(prefix_field_value!=null&&prefix_field_value.length()>0)
			 	            backfix="_"+prefix_field_value;
			 	        RecordVo idFactory=new RecordVo("id_factory");
			 	        idFactory.setString("sequence_name", fielditem.getFieldsetid().toUpperCase()+"."+fielditem.getItemid().toUpperCase()+backfix);
			 	        
						if(dao.isExistRecordVo(idFactory)&&prefix_field!=null&&prefix_field.trim().length()>0&&prefix_field_value.length()>0)//prefix_field_value e0122
							seq_no=idg.getId(seqname+"`"+prefix_field_value);
						else
							seq_no=idg.getId(seqname);
						
						if(seq_no.length()>fielditem.getItemlength())
							throw new GeneralException(ResourceFactory.getProperty("error.seqno.length"));
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
			
			
			if(rowSet!=null)
				rowSet.close();
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
			String message=ex.toString();
        	if(message.indexOf("err.utility.noCorrentOfID_FACTORY")!=-1){
			    ex=new GeneralException("指标"+normname+"的序号生成出错！请查证序号生成规则！");
			}
        	throw GeneralExceptionHandler.Handle(ex);
        }
	}
	
	/**
	 * @author lis
	 * @Description: 同步主集附件、或者多媒体附件到t_wf_file
	 * @date Sep 13, 2016
	 * @param arr0100s
	 * @param nbase
	 * @param setid 主集id
	 * @param isSavaAttachToMinSet 
	 */
	public void sysPersonAttachmentToTemplate(String[] arr0100s,String nbase,String setid,ArrayList insIdsList, boolean isSavaAttachToMinSet,Boolean attach_history){
		RowSet rowSet = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			/*StringBuffer sqlin = this.getSqlIn(arr0100s,"objectid");
			StringBuffer sb = new StringBuffer();
			sb.append("select t_wf_file.objectid from t_wf_file where ");
			sb.append(" tabid=");
			sb.append(this.tabId);
			sb.append(" and attachmenttype=1");
			if(this.paramBo.getInfor_type()==1){
				sb.append(" and ");
				sb.append(sqlin);
				sb.append(" and basepre='");
				sb.append(nbase);
				sb.append("'");
			}
			else if(this.paramBo.getInfor_type()==2||this.paramBo.getInfor_type()==3){
				sb.append(" and ");
				sb.append(sqlin);
			}
			if(insIdsList.size()>0){ 
				sb.append(" and ins_id in (");
				for (int k=0;k<insIdsList.size();k++){
					String ins_id = (String)insIdsList.get(k);
					if(k==0)
						sb.append(ins_id);
					else
						sb.append(","+ins_id);
				}
				sb.append(")");
			}else{
				sb.append(" and create_user ='"+this.userView.getUserName()+"'");
				sb.append(" and ins_id=0");
			}
			rowSet = dao.search(sb.toString());*/
			HashSet objectidset = new HashSet();//所有存在记录的
			/*while(rowSet.next()) {
				String objectid = rowSet.getString("objectid");
				objectidset.add(objectid);
			}*/
			for(int i=0;i<arr0100s.length;i++){
				String a0100 = arr0100s[i].trim().replace("'", "");
				if(!objectidset.contains(a0100)) {
					this.saveToTemplate(a0100, nbase, setid, insIdsList, isSavaAttachToMinSet,attach_history);
					objectidset.add(a0100);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @author lis
	 * @Description: 保存附件到临时表
	 * @date Sep 13, 2016
	 * @param A0100
	 * @param nbase
	 * @param setid
	 * @param insIdsList 
	 * @param isSavaAttachToMinSet 
	 */
	private void saveToTemplate(String A0100,String nbase,String setid, ArrayList insIdsList, boolean isSavaAttachToMinSet,boolean attach_history){
		try {
			StringBuffer fileValues = new StringBuffer();
			FieldSet fieldSet = null;
			if(isSavaAttachToMinSet){
				//如果没有个人附件则从主集中导入附件
				setid = "A01";
				if(attach_history)
					this.savesubToAttactment(nbase,setid,A0100,0,"0",this.paramBo.getInfor_type(),"1",insIdsList);
			}
			//暂时去掉针对多媒体与子集的个人附件同步，想想这两个同步并没有太大的实际意义。
			else{
				if(!"".equals(setid)&&setid.length()>0&&!"old".equalsIgnoreCase(setid)){
					if(!"A00".equalsIgnoreCase(setid)){//子集
						/*	fieldSet = DataDictionary.getFieldSetVo(setid);
						if("1".equals(fieldSet.getMultimedia_file_flag())){
							//从子集导入附件
							int i9999 = 1;
							BaseInfoBo infobo=new BaseInfoBo(this.conn,this.userView,1);
							ArrayList subUpdateList = this.paramBo.getSubUpdateList();
                    		int updatetype = 0;
                    		TSubsetCtrl tsubsetCtrl = null;
                    		for(int i=0;i<subUpdateList.size();i++){
                    			TSubsetCtrl tsubsetCtrl1 = (TSubsetCtrl)subUpdateList.get(i);
                    			String name = tsubsetCtrl1.getSetcode();
                    			String submenu = tsubsetCtrl1.getSubMenu();
                    			int type = tsubsetCtrl1.getUpdatetype();
                    			if(name.equalsIgnoreCase(setid)&&"false".equalsIgnoreCase(submenu)){//子集指标的更新方式
                    				updatetype = type;
                    				tsubsetCtrl = tsubsetCtrl1;
                    			}
                    		}
                    		i9999=infobo.getMaxI9999(nbase,setid,A0100);
                    		int _i9999=i9999;
                    		if(updatetype==1||updatetype==0){//新增记录或者不更新
                    			//不同步
                    		}
                    		else if(updatetype==2){//更新当前
								this.savesubToAttactment(nbase,setid,A0100,i9999-1,"0",this.paramBo.getInfor_type(),"1");                   			
							}
                    		else if(updatetype==3){//条件更新
                    			boolean bDatasync = true;
                    			String destab = nbase+setid;
                    			String srctab = this.getBz_tablename();
                    			YksjParser yp=null;
                    			String cond_str="";
                    			String condFormula=tsubsetCtrl.getCondFormula();
    							if(condFormula==null||condFormula.trim().length()==0)
    							{
    								cond_str=" and ( 1=1 ) ";	
    							}
    							else
    							{
    								yp = new YksjParser( this.userView ,getCondUpdateFieldList(setid),
    										YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
    								
    								yp.run_where(condFormula);
    								String strfilter=yp.getSQL();
    								if(strfilter.length()>0)
    									cond_str=" and ("+strfilter+") ";
    								
    							}
    							String sql="select count("+destab+".a0100) from  (select a.* from "+destab+" a where a.i9999=(select max(b.i9999) from "+destab+" b where a.a0100=b.a0100 group by b.a0100 ) " +
    									" ) "+destab+","+srctab+" where "+destab+".a0100="+srctab+".a0100 and  "+srctab+".a0100='"+A0100+"'"+cond_str;
    							if (insIdsList.size()>0)
    								sql+=" and ins_id in ("+getInsIdsWhere(insIdsList)+")";
    							if(this.paramBo.getOperationType()!=0)
    								sql+=" and lower("+srctab+".basepre)='"+nbase.toLowerCase()+"' ";
    							
    							RowSet rowSet=dao.search(sql);
    							if(rowSet.next())
    							{
    								if(rowSet.getInt(1)==0){
    									//不同步
    								}else
    									this.savesubToAttactment(nbase,setid,A0100,i9999-1,"0",this.paramBo.getInfor_type(),"1");
    							}
    							PubFunc.closeDbObj(rowSet);
    							
							}
						};*/
					}else{
						//从多媒体附件中导入附件
						if(attach_history)
							this.getMultimedia(A0100,nbase,"0",this.paramBo.getInfor_type(),"1",insIdsList);
					}
				}else{
					//从多媒体附件中导入附件
					if(attach_history)
						this.getMultimedia(A0100,nbase,"0",this.paramBo.getInfor_type(),"1",insIdsList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 
	/**
	 * 同步主集或子集附件
	 * @param nbase
	 * @param setid
	 * @param a0100
	 * @param ins_id
	 * @param infor_type
	 * @param attachmenttype
	 */
	private void savesubToAttactment(String nbase, String setid, String a0100, int maxi9999,
			String ins_id, int infor_type, String attachmenttype,ArrayList insIdsList) {
		try {
			StringBuffer fileValues = new StringBuffer();
			MultiMediaBo multiMediaBo=new MultiMediaBo(this.conn, this.userView, "A",nbase,setid, a0100, maxi9999);
			//得到附件数据
			ArrayList<LazyDynaBean> multimedialist = multiMediaBo.getMultimediaListByKey("");
			for(LazyDynaBean bean : multimedialist){
				String filename = (String)bean.get("filename");
				String path = (String)bean.get("path");
				String srcfilename = (String)bean.get("srcfilename");
				String dbflag = (String)bean.get("dbflag");
				String hc_id = (String)bean.get("hc_id");
				String id = (String)bean.get("mediaid");
				StringBuffer fileStr = new StringBuffer();
				fileStr.append(filename);
				fileStr.append("|");
				fileStr.append(path);
				fileStr.append("|");
				fileStr.append(srcfilename);
				fileStr.append("|");
				fileStr.append(hc_id);
				fileStr.append("|");
				fileStr.append(id);
				
				fileValues.append(",");
				fileValues.append(fileStr);
				//178d2857-51da-466f-a2ee-bbc833ff408d.xls|D:/Development tools/tomcat6-6.0.1/temp|应聘人员登记表_su|0.39MB
			}
			if(StringUtils.isNotBlank(fileValues.toString())){
				AttachmentBo attachmentBo = new AttachmentBo(userView, this.conn,this.tabId+"");
				//保存附件到t_wf_file
				attachmentBo.saveAttachment("0", fileValues.substring(1), nbase+"`"+a0100, "1", "1",false,insIdsList);
			}else{
				AttachmentBo attachmentBo = new AttachmentBo(userView, this.conn,this.tabId+"");
				//保存附件到t_wf_file
				attachmentBo.saveAttachment("0", fileValues.toString(), nbase+"`"+a0100, "1", "1",false,insIdsList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param a0100
	 * @param nbase
	 * @param ins_id
	 * @param infor_type "1" 人员 “2” 单位B “3”岗位K
	 * @param attachmenttype
	 * @return
	 */
	private void getMultimedia(String a0100, String nbase, String ins_id, int infor_type, String attachmenttype,ArrayList insIdList) throws IOException {
		RowSet rowSet = null;
		RowSet rowSet1 = null;
		InputStream in = null;
		OutputStream output = null;
		String tablename = "";
		String filepath = "";
		if(infor_type==1){
			tablename=nbase + "A00";
		}
		else if(infor_type==2){
			tablename="B00";
		}
		else if(infor_type==3){
			tablename="K00";
		}
	    AttachmentBo attachmentBo = new AttachmentBo(userView, this.conn,this.tabId+"");
	    StringBuffer cond=new StringBuffer();
		cond.append("select flag,sortname from mediasort");
		ContentDAO dao=new ContentDAO(this.conn);
		String mediasort="''";
		int n=0;
		try{
			rowSet=dao.search(cond.toString());
	          while(rowSet.next())
	          {
	              String flagsort=rowSet.getString("flag");
	              /**多媒体类型权限分析*/
	              if(userView.isSuper_admin())
	              {
              		mediasort+=",";
	              	mediasort+="'" + flagsort + "'";
	              	n++;
	              }
	              else
	              {
		              if(userView.hasTheMediaSet(flagsort))
		              {
		           		mediasort+=",";
		              	mediasort+="'" + flagsort + "'";
		              	n++;
		              }	    
	              }        
	          }
		}catch(Exception e){
			
		}
	    StringBuffer strsql=new StringBuffer();
 		strsql.delete(0,strsql.length());
		//保存sql的字符串
	    ArrayList list=new ArrayList();                             //封装子集的数据
		//if("A0100".equals(A0100) || "su".equalsIgnoreCase(A0100))
			//A0100=userView.getUserId();                             //如果A0100的值为A0100表示员工资助取其ID
		strsql.append("select b.id,");
		strsql.append("a.title,");
		strsql.append("a.I9999,");
		strsql.append("a.ole,");
		strsql.append("a.fileid,");
		strsql.append("a.ext,");
		strsql.append("a.STATE,");
		if(infor_type==1)
			strsql.append("a.a0100 ");
		else if(infor_type==2)
			strsql.append("a.b0110 ");
		else if(infor_type==3)
			strsql.append("a.e01a1 ");
		strsql.append(" from mediasort b," + tablename+" a ");
		strsql.append(" where ");
		if(infor_type==1)
			strsql.append("b.dbflag=1 and a.a0100='");
		else if(infor_type==2)
			strsql.append("b.dbflag=2 and a.b0110='");
		else if(infor_type==3)
			strsql.append("b.dbflag=3 and a.e01a1='");
		strsql.append(a0100);
		strsql.append("' and ");
		strsql.append("a.flag<>'p' and b.flag=");
		strsql.append("a.flag and a.state=3 ");
		if(mediasort!=null&&mediasort.length()>0)
		  strsql.append(" and b.flag in("+mediasort+")");
        try
		{	
        	String insIdWhere="";
        	HashSet ins_idList=new HashSet();
			if(insIdList.size()>0){
				 for (int i=0;i<insIdList.size();i++){
			           String ins_idtemp= (String)insIdList.get(i);
			           ins_idList.add(ins_idtemp);
			           if (i==0)
			        	   insIdWhere=" and ins_id in("+ ins_idtemp;
			           else 
			        	   insIdWhere= insIdWhere+","+ins_idtemp;  
			           if(i==insIdList.size()-1){
			        	   insIdWhere+=" ) ";
			           }
			       }
			}
        	HashMap fileMap=new HashMap();
        	if(StringUtils.isBlank(insIdWhere)){
        		ArrayList paramList=new ArrayList();
        		String searchAttachInwfFile="";
        		if(infor_type==1){
	        		searchAttachInwfFile="select file_id,name,i9999 from t_wf_file where ins_id='0' and lower(objectid)=? and lower(basepre)=? and create_user=? and tabid=?";
	        		paramList.add(a0100);
	        		paramList.add(nbase.toLowerCase());
	        		paramList.add(userView.getUserName());
	        		paramList.add(this.tabId);
        		}else{
        			searchAttachInwfFile="select file_id,name,i9999 from t_wf_file where ins_id='0' and lower(objectid)=? and create_user=? and tabid=?";
	        		paramList.add(a0100);
	        		paramList.add(userView.getUserName());
	        		paramList.add(this.tabId);
        		}
        		rowSet1 = dao.search(searchAttachInwfFile.toString(),paramList); 
        		while(rowSet1.next()){
        			fileMap.put(rowSet1.getString("file_id"),rowSet1.getString("name")+":"+rowSet1.getString("i9999")+":0");
        		}
        		ins_idList.add("0");
        	}else{
        		ArrayList paramList=new ArrayList();
        		String searchAttachInwfFile="";
        		if(infor_type==1){
	        		searchAttachInwfFile="select file_id,name,i9999,ins_id from t_wf_file where lower(objectid)=? and lower(basepre)=?  and tabid=? "+insIdWhere;
	        		paramList.add(a0100);
	        		paramList.add(nbase.toLowerCase());
	        		paramList.add(this.tabId);
        		}else{
        			searchAttachInwfFile="select file_id,name,i9999,ins_id from t_wf_file where  lower(objectid)=? and tabid=?"+insIdWhere;
	        		paramList.add(a0100);
	        		paramList.add(this.tabId);
        		}
        		rowSet1 = dao.search(searchAttachInwfFile.toString(),paramList); 
        		while(rowSet1.next()){
        			fileMap.put(rowSet1.getString("file_id"),rowSet1.getString("name")+":"+rowSet1.getString("i9999")+":"+rowSet1.getString("ins_id"));
        		}
        	}
          rowSet1 = dao.search(strsql.toString());//获取子集的纪录数据
          String file_ids="";
		  while(rowSet1.next())
		  {
			  HashMap map = new HashMap();
			  String fileuuidname = "";
			  String title = rowSet1.getString("TITLE");
			  if(title==null){
				  title="";
			  }
			  if(infor_type==1)
				  a0100=rowSet1.getString("A0100");
			  else if(infor_type==2)
				  a0100=rowSet1.getString("b0110");
			  else if(infor_type==3)
				  a0100=rowSet1.getString("e01a1");
		     String i9999=Integer.toString(rowSet1.getInt("I9999"));
		     title= "".equalsIgnoreCase(title)?"未知文件名":title;
		     String sortid=rowSet1.getString("id");
		     in = rowSet1.getBinaryStream("ole");
		     String fileid=rowSet1.getString("fileid");
		     String ext=rowSet1.getString("ext");
		     String state=rowSet1.getString("STATE");
		     state=(state==null|| "".equals(state))?"3":state;
		     if("3".equalsIgnoreCase(state)){
		    	 Iterator insiditerator=ins_idList.iterator();
			     while(insiditerator.hasNext()){
			    	 String ins_idtemp = (String) insiditerator.next();
				     map.put("objectid", a0100);
				     map.put("nbase", nbase);
				     map.put("title", title);
				     map.put("sortid", sortid);
				     map.put("ext", ext);
				     map.put("ins_id", ins_idtemp);
				     map.put("infor_type", infor_type+"");
				     map.put("attachmenttype", attachmenttype);
				     map.put("I9999", i9999);
				     Iterator iterator = fileMap.entrySet().iterator();
				     Boolean isNeedInsert=true;
				     while(iterator.hasNext()){
				    	 Entry next = (Entry) iterator.next();
				    	 String value = (String) next.getValue();
				    	 String idTem = (String) next.getKey();
				    	 String[] values = value.split(":",3);
				    	 String nameTem=values[0];
				    	 String i9999Tem=values[1];
				    	 String insidTemp=values[2];
				    	 if(title.equalsIgnoreCase(nameTem)&&StringUtils.isBlank(i9999Tem)&&ins_idtemp.equalsIgnoreCase(insidTemp)){
				    		 String updateSql="update t_wf_file set i9999=?,state=0 where file_id=? ";
				    		 ArrayList paramList=new ArrayList();
				    		 paramList.add(i9999);
				    		 paramList.add(idTem);
				    		 dao.update(updateSql, paramList);
				    		 next.setValue(nameTem+":"+i9999+":"+insidTemp);
				    		 isNeedInsert=false;
				    		 iterator.remove();
				    		 break;
				    	 }else if(title.equalsIgnoreCase(nameTem)&&i9999Tem.equalsIgnoreCase(i9999)&&ins_idtemp.equalsIgnoreCase(insidTemp)){
				    		 isNeedInsert=false;
				    		 String updateSql="update t_wf_file set state=0 where file_id=? ";
				    		 ArrayList paramList=new ArrayList();
				    		 paramList.add(idTem);
				    		 dao.update(updateSql, paramList);
				    		 iterator.remove();
				    		 break;
				    	 }
				     }
				     if(!isNeedInsert){
				    	 continue;
				     }
				     if(StringUtils.isNotEmpty(fileid)){
						 //保存到指定目录(路径)按照子集附件保存路径存储 由于调用vfs接口 多媒体子集存储的为fileid 文件不需要
					     /*attachmentBo.initParam(true);
					     String middlepath = "";
						 if("\\".equals(File.separator)){//证明是windows
							middlepath = "subdomain\\template_";
						 }else if("/".equals(File.separator)){//证明是linux
							middlepath = "subdomain/template_";
						 }
						 UUID uuid = UUID.randomUUID();
					     fileuuidname = uuid.toString();
					     filepath = attachmentBo.getAbsoluteDir(fileuuidname,middlepath)+File.separator+fileuuidname + ext;
					     String rootDir = attachmentBo.getRootDir();
					     map.put("filepath", filepath.replace("\\", File.separator).replace("/", File.separator));*/
				    	 map.put("filepath", fileid);
					     // 保存文件
					    /* String fileAbsolute="";
					     fileAbsolute=attachmentBo.getAbsoluteDir(fileuuidname,middlepath);
					     if(!fileAbsolute.startsWith(rootDir)){
					    	 if(rootDir.endsWith(File.separator)){
					    		 fileAbsolute=rootDir+fileAbsolute;
					    	 }else{
					    		 fileAbsolute=rootDir+ File.separator+fileAbsolute;
					    	 }
					     }
						 File file = new File(fileAbsolute.replace("\\", File.separator).replace("/", File.separator), fileuuidname + ext);//bug 48163 以前传的路径是相对路径，创建文件报错。
						 output = new FileOutputStream(file);
						 byte[] bt = new byte[1024];
						 int read = 0;
						 while ((read = in.read(bt)) != -1) {
							output.write(bt, 0, read);
						 }*/
						String id= attachmentBo.saveMediaToTwffile(map);
				     }
			    }
		     }
		  }
		  Iterator iterator = fileMap.entrySet().iterator();
	     Boolean isNeedInsert=true;
	     while(iterator.hasNext()){
	    	 Entry next = (Entry) iterator.next();
	    	 String value = (String) next.getValue();
	    	 String idTem = (String) next.getKey();
	    	 String[] values = value.split(":",3);
	    	 String nameTem=values[0];
	    	 String i9999Tem=values[1];
	    	 String insidTemp=values[2];
	    	 if(!(StringUtils.isBlank(i9999Tem)||"-1".equalsIgnoreCase(i9999Tem))){
	    		 file_ids+="'"+idTem+"',";
	    	 }
	     }
		  if(StringUtils.isNotBlank(file_ids)){
				if(StringUtils.isBlank(insIdWhere)){
					ins_id="0";
					ArrayList paramList=new ArrayList();
					String sql="";
					if(infor_type==1){
						sql="delete from t_wf_file where ins_id='0' and lower(objectid)=? and lower(basepre)=? and create_user=? and tabid=? and file_id  in ("+file_ids.substring(0,file_ids.length()-1)+")";
		        		paramList.add(a0100);
		        		paramList.add(nbase.toLowerCase());
		        		paramList.add(userView.getUserName());
		        		paramList.add(this.tabId);
	        		}else{
	        			sql="delete from t_wf_file where ins_id='0' and lower(objectid)=? and create_user=? and tabid=? and file_id  in ("+file_ids.substring(0,file_ids.length()-1)+")";
		        		paramList.add(a0100);
		        		paramList.add(userView.getUserName());
		        		paramList.add(this.tabId);
	        		}
					dao.delete(sql, paramList);
				}else{
					ArrayList paramList=new ArrayList();
					String sql="";
					if(infor_type==1){
						sql="delete from t_wf_file where lower(objectid)=? and lower(basepre)=?  and tabid=? "+insIdWhere +" and file_id  in ("+file_ids.substring(0,file_ids.length()-1)+")";
		        		paramList.add(a0100);
		        		paramList.add(nbase.toLowerCase());
		        		paramList.add(this.tabId);
	        		}else{
	        			sql="delete from t_wf_file where  lower(objectid)=? and tabid=?"+insIdWhere+" and file_id  in ("+file_ids.substring(0,file_ids.length()-1)+")";
		        		paramList.add(a0100);
		        		paramList.add(this.tabId);
	        		}
					dao.delete(sql, paramList);
				}
			}
		 }catch(SQLException sqle){
		   sqle.printStackTrace();
		   //throw GeneralExceptionHandler.Handle(sqle);
		 }catch(Exception e){
			 e.printStackTrace();
	     }finally {
				if (output != null) {
					output.close();
				}
				
				if (in != null) {
					in.close();
				}
			}
	}
	/**
	 * 取得模板中的对象的文书号
	 * @param fieldname
	 * @param strDesT
	 * @return
	 */
	public String getSequenceNoInEqual(String fieldname,String strDesT,String item_str,int prefix_field_len)
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
	public String getSequenceNoInEqual(String fieldname,String strDesT)
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
			if(rset.next())
				seqno=rset.getString("seqno");
		}
		catch(Exception ex)
		{
			
		}
		return seqno;
	}
	
	public RecordVo getFactoryIdVo(String id)
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
	
    private FieldItem getMIdVarItemByDesc(ArrayList midVarList,String desc)
    {
        FieldItem returnItem=null;
        try
        {
            for(int i=0;i<midVarList.size();i++)
            {
                FieldItem item=(FieldItem)midVarList.get(i);
                if (desc.equals(item.getItemdesc())){
                    returnItem=item;
                    break;
                }
            }
        }
        catch(Exception e)
        {           
            e.printStackTrace();
        }
        return returnItem;
    }   
	
	
    /**   
     * @Title: reCombineFactor   
     * @Description: 重新组合条件定位条件 将临时变量替换成计算的值   
     * @param @param rSet //当前人的记录
     * @param @param sql  //当前人sql 
     * @param @param strFactor 因子表达式
     * @param @return 
     * @return String 
     * @throws   
    */
    private String reCombineFactor(RowSet rSet,String sql,String strFactor)
    {
        String rFactor=strFactor;
        try
        {
            if (midVarFieldlist==null){
                midVarFieldlist= getMidVariableList();
            }
            String [] arrFactor =strFactor.split("`");
            for (int midI=0;midI<arrFactor.length;midI++){
                String factor= arrFactor[midI];
                factor=factor.replace("=", ",");
                factor=factor.replace("<", ",");
                factor=factor.replace(">", ",");
                String [] arrTmp =factor.split(",");
                if (arrTmp.length>=2){
                  String factorValue=arrTmp[arrTmp.length-1];
                  FieldItem item =getMIdVarItemByDesc(midVarFieldlist,factorValue);
                  if (item!=null){
                      //计算临时变量，因为效率问题，条件需要用到临时变量的时候再计算。
                      computeMidVar();
                      if (rSet==null){
                          rSet= this.dao.search(sql);
                          rSet.next();
                      }
                      String value="";
                      if("D".equalsIgnoreCase(item.getItemtype())){
                          value=PubFunc.FormatDate(rSet.getDate(item.getItemid()));
                          value=value.replace("-", ".");
                      }
                      else if("N".equalsIgnoreCase(item.getItemtype())){
                          int ndec=item.getDecimalwidth();//小数点位数
                          value=PubFunc.DoFormatDecimal(rSet.getString(item.getItemid()),ndec);
                      }
                      else {
                          value=rSet.getString(item.getItemid());
                          value=((value==null)?"":value.trim());
                      }   
                      rFactor=rFactor.replace(factorValue, value);
                  }
                }
                
            }
    
        }
        catch(Exception e)
        {           
            e.printStackTrace();
        }
        return rFactor;
    }   
    /**   
     * @Title: reCombineFactor   
     * @Description: 重新组合条件定位条件 将临时变量替换成计算的值   
     * @param @param rSet //当前人的记录
     * @param @param sql  //当前人sql 
     * @param @param strFactor 因子表达式
     * @param @return 
     * @return String 
     * @throws   
    */
    private String reCombineFactor(RowSet rSet,String sql,String strFactor,HashMap list)
    {
    	String rFactor="";
        try
        {
            if (midVarFieldlist==null){
                midVarFieldlist= getMidVariableList();
            }
            String [] arrFactor =strFactor.split("`");
            HashSet factorset = new HashSet();
            for (int midI=0;midI<arrFactor.length;midI++){
                String factor= arrFactor[midI];
                factor=factor.replace("=", ",");
                factor=factor.replace("<", ",");
                factor=factor.replace(">", ",");
                String [] arrTmp =factor.split(",");
                if (arrTmp.length>=2){
                  String factorValue=arrTmp[arrTmp.length-1];
                  FieldItem item =getMIdVarItemByDesc(midVarFieldlist,factorValue);
                  if (item!=null){
                      //计算临时变量，因为效率问题，条件需要用到临时变量的时候再计算。
                      computeMidVar();
                      String value="";
                      String key = "";
                      value=sql.replace("*", item.getItemid());
                      if("D".equalsIgnoreCase(item.getItemtype())|| "N".equalsIgnoreCase(item.getItemtype())){
                    	  if(factorset.contains(factorValue.toUpperCase())) {
                    		  key = "$YK_"+factorValue.toUpperCase()+"_"+midI;
                    	  }else {
                    		  key = "$YK_"+factorValue.toUpperCase();
                    		  factorset.add(factorValue.toUpperCase());
                    	  }
                    	  list.put(key,"( "+value+" )");
                      }
                      else {
                    	  key = item.getItemid().toUpperCase();
                    	  list.put("'"+key+"'","( "+value+" )");
                      }
                      rFactor+=arrFactor[midI].replace(factorValue, key)+"`";
                  }else {
                	  rFactor+=arrFactor[midI]+"`";
                  }
               }else {
            	   rFactor += arrFactor[midI]+"`";
               }
            }
        }
        catch(Exception e)
        {           
            e.printStackTrace();
        }
        return rFactor;
    } 
	/**
	 * 导入子集区域对应的数据
	 * @param a0100s      人员编号列表
	 * @param dbpre		  应用库前缀
	 * @param xmlfmt      xml格式
	 * @param setname     子集
	 * @param field_name  业务模板表中的字段
	 * @param flag  来自哪里  刷新按钮0   子集同步按钮1
	 * @throws GeneralException
	 */
	private ArrayList impSubDomainData(String a0100s,String dbpre,String xmlfmt,String setname,String field_name,int nhismode,int mode,int count,String formula,int his_start,ArrayList insIdsList,String flag)throws GeneralException
	{
		ArrayList list=new ArrayList();
		if(xmlfmt==null|| "".equalsIgnoreCase(xmlfmt))
			return list;
		TSubSetDomain setdomain=new TSubSetDomain(xmlfmt);
		 setdomain.setUserview(this.userView);
		String fields=setdomain.getFields();
		String[]  fieldarr=StringUtils.split(fields,"`");
		StringBuffer buf=new StringBuffer();
		StringBuffer bufbetween=new StringBuffer();
		StringBuffer sql=new StringBuffer();
		StringBuffer strsql=new StringBuffer();	
		ArrayList paralist=new ArrayList();
		String insIdWhere = "";
		String tablename=dbpre+setname;
		boolean attachFlag=false;  //是否支持附件标识 true为支持附件 false为不支持附件 liuzy 20151028
		if(this.paramBo.getInfor_type()==2||this.paramBo.getInfor_type()==3)
			tablename=setname;
		ArrayList fieldlist=new ArrayList();
		/**人员*/
        try
		{
        	String _tablename=this.getBz_tablename();
			if(getImpOthTableName()!=null&&getImpOthTableName().trim().length()>0)  //供高级花名册调用人事异动的人员引入功能，将数据导入到临时表中
				_tablename=getImpOthTableName();
			String paramname = "a0100";
			if(this.paramBo.getInfor_type()==2)
				paramname="b0110";
			else if(this.paramBo.getInfor_type()==3)
				paramname="e01a1";
			strsql.append("select rn,i9999,");
			strsql.append(paramname);
			for(int i=0;i<fieldarr.length;i++)
			{
				String name=fieldarr[i];
				//当子集中包含attach时，将attach设置为true liuzy 20151028
				if("attach".equals(name)){ 
					attachFlag=true;
				}
				
				FieldItem item=DataDictionary.getFieldItem(name);
				if(item==null|| "0".equals(item.getUseflag()))
					continue;
				//if(this.userView.analyseFieldPriv(name).equals("0")&&this.paramBo.getUnrestrictedMenuPriv_Input().equals("0"))
				//	continue;
				fieldlist.add(item);
				buf.append(",");
				buf.append(name);
			}
			if(buf.length()==0)
				return list;
			
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
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			strsql.append(buf.toString());
			bufbetween.append(buf);
			//如果支持附件，查询子集数据时需将GuidKey也查询出来，方便去hr_multimedia_file查找附件 liuzy 20151028
			if(attachFlag){
				if(dbw.isExistField(tablename, "GuidKey",false)) {
					strsql.append(",GuidKey");
					bufbetween.append(",GuidKey");
				}
			}
			strsql.append(" from (select ROW_NUMBER() over(PARTITION by "+paramname+" order by i9999 ");
			if(nhismode==2||nhismode==4) {
				if(mode==0||mode==1)
					strsql.append(" desc");
				else
					strsql.append(" asc");
			}else if(nhismode==1)
				strsql.append(" desc");
			
			strsql.append(" ) rn,i9999"+bufbetween+",");
			strsql.append(paramname);
			strsql.append(" from ");
			strsql.append(tablename);
			strsql.append(" where 1=1 ");
			
			String[] a0100arr=StringUtils.split(a0100s,",");
			//拼接导入的人员
			strsql.append(" and "+this.getSqlIn(a0100arr,paramname));
			
			StringBuffer task_str=new StringBuffer("");
            String templatetablename=this.getBz_tablename();
            if(!"0".equals(this.taskId))
            { 
                for(int j=0;j<getTasklist().size();j++)
                {
                    if(getTasklist().get(j)!=null&&((String)getTasklist().get(j)).trim().length()>0)
                        task_str.append(","+(String)getTasklist().get(j));
                }
            }
            ArrayList valuelist = new ArrayList();
			sql.setLength(0);
			sql.append(strsql.toString());

			// 取临时变量
			String midVarSql = "";
			if (this.paramBo.getInfor_type() == 1)
				midVarSql = " select * from " + templatetablename
						+ " where a0100="+tablename+".a0100 and lower(basepre)='" + dbpre.toLowerCase()
						+ "'";
			else if (this.paramBo.getInfor_type() == 2)
				midVarSql = "select * from " + templatetablename
						+ " where b0110='" +tablename+".b0110" ;
			else if (this.paramBo.getInfor_type() == 3)
				midVarSql = "select * from " + templatetablename
						+ " where e01a1='"  +tablename+".e01a1";
			if (!"0".equals(this.taskId)) {//等待整改
				midVarSql = "select a.* from "+templatetablename+" a,t_wf_task_objlink b where a.seqnum = b.seqnum and a.ins_id=b.ins_id and b.task_id in("+this.taskId+") and b.tab_id="+this.tabId+"";
				if (this.paramBo.getInfor_type() == 1) 
					midVarSql+=" and a.a0100="+tablename+".a0100 and lower(a.basepre)='" + dbpre.toLowerCase()+ "'";
				else if(this.paramBo.getInfor_type() == 2)
					midVarSql+=" and a.b0110="+tablename+".b0110 ";
				else if (this.paramBo.getInfor_type() == 3)
					midVarSql+=" and a.e01a1="+tablename+".e01a1 ";
			}
			RowSet rMidVarSet = null;// 位置不能变
			if (nhismode == 1) // 当前记录
			{
				sql.append(" ) a where ");
				sql.append(" a.rn<2");

			} else if (nhismode == 3) // 条件定位
			{
				
				String[] preCond = getPrefixCond(formula);
				String cond = preCond[1] != null ? preCond[1] : "";

				if (cond.length() > 0) {
					String strFactor = preCond[2] != null ? preCond[2]: "";
					// 判断因子表达式是否有临时变量 替换
					HashMap listFactor=new HashMap();
					strFactor = reCombineFactor(rMidVarSet, midVarSql,strFactor,listFactor);
					String strw = strFactor;
					FactorList factorlist = new FactorList(cond,strFactor, "");
					strw=factorlist.getSingleTableSqlExpression(tablename);
					if(listFactor.size()>0) {//有临时变量需要替换
						for(Object obj:listFactor.keySet()) {
							strw=strw.replace(obj.toString(), listFactor.get(obj).toString());
						}
					}
					sql.append(" and ( ");
					sql.append(strw);
					sql.append(" ) ");
					sql.append(" ) a ");
				} else {
					sql.append(" ) a ");
				}

			} else if (nhismode == 4||nhismode == 2) // 条件序号
			{
				String[] preCond = getPrefixCond(formula);
				String cond = preCond[1] != null ? preCond[1] : "";
				if (cond.length() > 0) {
					String strFactor = preCond[2] != null ? preCond[2]: "";
					// 判断因子表达式是否有临时变量 替换
					HashMap listFactor=new HashMap();
					strFactor = reCombineFactor(rMidVarSet, midVarSql,strFactor,listFactor);
					String strw = strFactor;
					FactorList factorlist = new FactorList(cond,strFactor, "");
					strw=factorlist.getSingleTableSqlExpression(tablename);
					if(listFactor.size()>0) {//有临时变量需要替换
						for(Object obj:listFactor.keySet()) {
							strw=strw.replace(obj.toString(), listFactor.get(obj).toString());
						}
					}
					sql.append(" and ( ");
					sql.append(strw);
					sql.append(" ) ");
					sql.append(" ) a where ");
				} else {
					sql.append(" ) a where ");
				}
				switch(mode)
				{
				case 0://倒数第...条（最近第）
				case 2://正数第...条(最初第)
					sql.append(" a.rn="+count);													
					break;
				case 1://倒数...条（最近）
				case 3://正数...条（最初）
					if(his_start==0){
						sql.append(" a.rn<="+count);
					}else{
						sql.append(" a.rn<="+(his_start+count-1)+" and a.rn>="+his_start);
					}
					break;
				}
			}

			RowSet rset=dao.search(sql.toString());
			ArrayList reclist=getRecordValue(fieldlist,rset,setdomain,mode,nhismode);
			
			//得到对应的临时表中的子集的xml,并解析成list
			list = getLinValue(setname,field_name,setdomain,a0100s,dbpre,reclist,fieldlist,attachFlag,valuelist,paramname,flag);
			if(valuelist.size()>0){
				StringBuffer strUpdate2=new StringBuffer();
				strUpdate2.append("Update ");
				strUpdate2.append(_tablename);
				strUpdate2.append(" set ");
				strUpdate2.append(field_name);
				strUpdate2.append(" =?");
				strUpdate2.append(" where ");
				if(this.paramBo.getInfor_type()==1)
					strUpdate2.append("basepre"+Sql_switcher.concat()+paramname);
				else
					strUpdate2.append(paramname);
				strUpdate2.append(" =? ");
				strUpdate2.append(insIdWhere);
				dao.batchUpdate(strUpdate2.toString(), valuelist);
			}
		}		
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return list;
	}
	
	private ArrayList getLinValue(String setname, String field_name, TSubSetDomain setdomain,String a0100s, String dbpre, ArrayList reclist, 
			ArrayList fieldlist, boolean attachFlag, ArrayList valuelist, String paramname, String flag) {
		String templatetablename=this.getBz_tablename();
		ArrayList list=new ArrayList();
		RowSet rset = null;
		try {
			String[] a0100arr=StringUtils.split(a0100s,",");
			//拼接导入的人员
			StringBuffer sqlin = this.getSqlIn(a0100arr,paramname);
			String sql = "select "+field_name+","+paramname+" from "+templatetablename ;
					
			if (this.paramBo.getInfor_type() == 1)
				sql += " where lower(basepre)='"+dbpre.toLowerCase()+"' and "+sqlin.toString();
			else if (this.paramBo.getInfor_type() == 2||this.paramBo.getInfor_type() == 3)
				sql += " where "+sqlin.toString();
			if(!"0".equals(this.taskId)) {
				ArrayList ins_idlist = this.dataBo.getUtilBo().getTaskIdtoInsId(this.taskId);
				String sql_ins = "";
				for(int i=0;i<ins_idlist.size();i++) {
					HashMap insmap = (HashMap) ins_idlist.get(i);
					String ins_id = (String) insmap.get("ins_id");
					if(i==0)
						sql_ins += ins_id;
					else
						sql_ins += ","+ins_id;
				}
				sql+=" and ins_id in ("+sql_ins+")";
			}
			rset = dao.search(sql);
			String his_edit = setdomain.getHis_edit();
			String fields = setdomain.getFields();
			String[] fieldarr=StringUtils.split(fields, "`");
			while(rset.next()) {
				HashMap i999_record_key_id=new HashMap();
				LinkedHashMap dropmap = new LinkedHashMap();
				String a0100 = rset.getString(paramname);
				ArrayList reclist_  = new ArrayList();
				for(int j=0;j<reclist.size();j++) {
					HashMap reclist_map = (HashMap) reclist.get(j);
					if(reclist_map.containsKey(a0100)) {
						reclist_ = (ArrayList) reclist_map.get(a0100);
						break;
					}
				}
				String subxml = rset.getString(field_name);
				ArrayList paramlist=new ArrayList();
				ArrayList subsetlist = new ArrayList();
				subsetlist = setdomain.getOrderRecList(fieldlist,subxml,reclist_,field_name);
				//格式化子集数据 目前只针对数值型
				subsetlist = setdomain.formatRecordList(fieldlist,subsetlist);
				String xmlcontent=setdomain.outContentxml(fieldlist, subsetlist, attachFlag);
				//将排序完的转成前台用的数据
				if("1".equals(flag)) {
					list = setdomain.getRecords(fieldlist, subsetlist, attachFlag);
				}
				String object = a0100;
				if(this.paramBo.getInfor_type()==1&&dbpre.length()>0)
					object = dbpre+a0100;
				if(xmlcontent.length()>0) {
					paramlist.add(xmlcontent);
					paramlist.add(object);
					valuelist.add(paramlist);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rset);
		}
		return list;
	}
	
	/**解析原记录顺序
	 * @param xml
	 * @return
	 */
	private ArrayList getOldSubRecI999List(String xml)
	{
		ArrayList list=new ArrayList();
		if(xml==null||xml.length()==0)
			return list;
		Document doc=null;
		Element element=null;
		try
		{
			doc=PubFunc.generateDom(xml);
			Element root=doc.getRootElement();
			String fields=root.getAttributeValue("columns");
			String[] fieldarr=StringUtils.split(fields, "`");
			
			String xpath="/records/record";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			List childlist=findPath.selectNodes(doc);	
			String value=null;
			ContentDAO dao=null;
			if(childlist!=null&&childlist.size()>0)
			{
				for(int i=0;i<childlist.size();i++)
				{				
					element=(Element)childlist.get(i);
					String values=element.getText();
					Object[] valuearr=PubFunc.split(values, "`");    
					
					String i9999=element.getAttributeValue("I9999");
					list.add(i9999);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
		return list;
	}
	
	/**按已设定的顺序，对子集记录重新排序
	 * @param oldXml 目前排序的xml
	 * @param curRecList  归档库的记录 按i9999排序
	 * @return
	 */
	private ArrayList getOrderRecList(String oldXml,ArrayList curRecList )
	{
		ArrayList newRecList = new ArrayList();
		try{
		
			ArrayList oldRecList=getOldSubRecI999List(oldXml);
			//对已存在的记录排序
			for (int i=0;i<oldRecList.size();i++){
				String i9999= (String)oldRecList.get(i);
				for (int j=0;j<curRecList.size();j++){
					ArrayList valueList = (ArrayList)curRecList.get(j);
					String _i9999= (String)valueList.get(0);
					if (i9999.equals(_i9999)){
						newRecList.add(valueList);
						curRecList.remove(j);
						break;
					}
				}
			}
			//将不存在的记录（新增记录）全部放在后面
			for (int j=0;j<curRecList.size();j++){
				ArrayList valueList = (ArrayList)curRecList.get(j);
				newRecList.add(valueList);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
		return newRecList;
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
			if(this.paramBo.getInfor_type()==3)
				key_str="E01A1";
			
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
					if(this.paramBo.getInfor_type()==1)
						buf.append(" where F.a0100='"+a0110list.get(m)+"'");
					else if(this.paramBo.getInfor_type()==2)
						buf.append(" where F.b0110='"+a0110list.get(m)+"'");
					else if(this.paramBo.getInfor_type()==3)
						buf.append(" where F.e01a1='"+a0110list.get(m)+"'");
					if(strw.trim().length()>0)
					buf.append(" and ("+strw+") ");
					//更新记录
					ArrayList reclist=getRecordValue(fieldlist,dao.search(buf.toString()),false);
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
					if(this.paramBo.getInfor_type()==1&&dbpre.length()>0)	
						strUpdate2.append(" and basepre ='"+dbpre+"'");
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
					if(this.paramBo.getInfor_type()==1)
						buf.append(" where F.a0100='"+a0110list.get(m)+"'");
					else if(this.paramBo.getInfor_type()==2)
						buf.append(" where F.b0110='"+a0110list.get(m)+"'");
					else if(this.paramBo.getInfor_type()==3)
						buf.append(" where F.e01a1='"+a0110list.get(m)+"'");
					if(strw.trim().length()>0)
					buf.append(" and ("+strw+") ");
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
								if(size==ncount)
									curri9999=(String)i9999list.get(0);
								else
								{
									if(ncount!=0)
										curri9999=(String)i9999list.get(size-ncount);
									else
										curri9999=(String)i9999list.get(size-ncount-1);
								}
							}
						
							buf.append(" and F.I9999=?");							
						paralist.add(curri9999);							
						break;
					case 1://倒数...条（最近）
						if(his_start==0){
							if(size>=ncount)
							{
								if(size==ncount)
									curri9999=(String)i9999list.get(0);
								else
									curri9999=(String)i9999list.get(size-ncount);
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
									if(size<his_start+ncount)
										curri9999=(String)i9999list.get(0);
									else
										curri9999=(String)i9999list.get(size-ncount-(his_start-1));
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
						if(size>=ncount)
							curri9999=(String)i9999list.get(ncount-1);
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
					ArrayList reclist=getRecordValue(fieldlist,dao.search(buf.toString(),paralist),false);
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
					if(this.paramBo.getInfor_type()==1&&dbpre.length()>0)	
						strUpdate2.append(" and basepre ='"+dbpre+"'");
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
								if(this.paramBo.getInfor_type()==1)
									buf.append(" where a0100=?");
								else if(this.paramBo.getInfor_type()==2)
									buf.append(" where b0110=?");
								else if(this.paramBo.getInfor_type()==3)
									buf.append(" where e01a1=?");
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
												if(size==ncount)
													curri9999=(String)i9999list.get(0);
												else
												{
													if(ncount!=0)
														curri9999=(String)i9999list.get(size-ncount);
													else
														curri9999=(String)i9999list.get(size-ncount-1);
												}
											}
										
											buf.append(" and I9999=?");							
										paralist.add(curri9999);							
										break;
									case 1://倒数...条（最近）
										if(his_start==0){
											if(size>=ncount)
											{
												if(size==ncount)
													curri9999=(String)i9999list.get(0);
												else
													curri9999=(String)i9999list.get(size-ncount);
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
													if(size<his_start+ncount)
														curri9999=(String)i9999list.get(0);
													else
														curri9999=(String)i9999list.get(size-ncount-(his_start-1));
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
										if(size>=ncount)
											curri9999=(String)i9999list.get(ncount-1);
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
								ArrayList reclist=getRecordValue(fieldlist,dao.search(buf.toString(),paralist),false);
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
								if(this.paramBo.getInfor_type()==1&&dbpre.length()>0)	
									strUpdate2.append(" and basepre ='"+dbpre+"'");
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
		if(this.paramBo.getInfor_type()==1)
			buf.append(" where a0100=?  ");
		else if(this.paramBo.getInfor_type()==2)
			buf.append(" where b0110=? ");
		else if(this.paramBo.getInfor_type()==3)
			buf.append(" where e01a1=? ");
		buf.append(" order by I9999");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString(),paralist);
			paralist.clear();
			while(rset.next())
				paralist.add(rset.getString("I9999"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return paralist;
	}	
	/**
	 * 取得记录串
	 * @param list
	 * @param rset
	 * @return
	 */
	private ArrayList getRecordValue(ArrayList list ,RowSet rset,boolean isFormat)
	{
		ArrayList reclist=new ArrayList();
		String value="";
		try
		{
			ConstantXml constantXml = new ConstantXml(this.conn,"FILEPATH_PARAM");
			String rootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
            rootDir=rootDir.replace("\\",File.separator);
            if (!rootDir.endsWith(File.separator)) rootDir =rootDir+File.separator;
            rootDir += "multimedia"+File.separator;
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
				int disformat =item.getDisplayid();
				if("M".equalsIgnoreCase(field_type))
				{
					value=Sql_switcher.readMemo(rset,field_name);
				}
				else if("D".equalsIgnoreCase(field_type))
				{
					/**yyyy-MM-dd*/
					value=PubFunc.FormatDate(rset.getDate(field_name));
					if(isFormat)
						value=utilBo.getFormatDate(value,disformat);//去掉格式化日期

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
						String sql="select id,filename,path,class,srcfilename,topic,ext from hr_multimedia_file where childguid='"+GuidKey+"'";
						RowSet flagset=dao.search(sql); 
						int m=0;
						String valuestr="";
						while(flagset.next()){
							m++;
							String id=flagset.getString("id");                   //文件唯一标识 
							String filename=flagset.getString("filename");       //编码后文件名
							String path= rootDir + flagset.getString("path").replace("\\", File.separator);               //文件上传路径
							String srcfilename=flagset.getString("topic")+flagset.getString("ext"); //原始文件名 
							String filetype = flagset.getString("class");//文件类型
					        File f= new File(path,filename);  
					        long s=0;
					        if (f.exists()) {
					        	FileInputStream fis = null; 
					            try {
						            fis = new FileInputStream(f);
						            s= fis.available();
								} finally {
									PubFunc.closeResource(fis);
								}
					        } else {
					            //f.createNewFile();
					        }
					        DecimalFormat df = new DecimalFormat("#0.00");
					        String fileSizeString = "";
					        //if (s < 1024) {
					        //    fileSizeString = df.format((double) s) + "B";
					        //} else
					        if (s < 1048576) {
					            fileSizeString = df.format((double) s / 1024) + "K";
					        } else if (s < 1073741824) {
					            fileSizeString = df.format((double) s / 1048576) + "M";
					        } 
					           // else {
					           // fileSizeString = df.format((double) s / 1073741824) +"G";
					       // }
							String text=filename+"|"+path+"|"+srcfilename+"|"+fileSizeString+"|"+id+"|"+m+"|"+"type:"+filetype ;
							valuestr+=text+",";
						}
						if(valuestr.length()>0){
							valuestr=valuestr.substring(0, valuestr.length()-1);
							valuestr = this.saveSubAttachmentToTemplate(valuestr);//将子集附件保存到模板临时目录 lis 20160912 add
							valuelist.add(valuestr);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				  
			  }
			  String paramname = "a0100";
			  if(this.paramBo.getInfor_type()==2)
				  paramname="b0110";
			  else if(this.paramBo.getInfor_type()==3)
				  paramname="e01a1";
			  String a0100 = rset.getString(paramname);
			  valuelist.add(a0100);
			  reclist.add(valuelist);
			}	  
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return reclist;
	}
	/**
	 * 取得记录串(依据指定行数)
	 * @param list
	 * @param rset
	 * @param setdomain  从中获取指定记录数
	 * @param mode 
	 * @param nhismode 
	 * @return
	 */
	private ArrayList getRecordValue(ArrayList list ,RowSet rset, TSubSetDomain setdomain, int mode, int nhismode)
	{
		ArrayList reclist=new ArrayList();
		String value="";
		String paramname = "a0100";
		  if(this.paramBo.getInfor_type()==2)
			  paramname="b0110";
		  else if(this.paramBo.getInfor_type()==3)
			  paramname="e01a1";
		try
		{
			int datarowcount = setdomain.getDatarowcount();
			ConstantXml constantXml = new ConstantXml(this.conn,"FILEPATH_PARAM");
			String rootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
            rootDir=rootDir.replace("\\",File.separator);
            if (!rootDir.endsWith(File.separator)) rootDir =rootDir+File.separator;
            rootDir += "multimedia"+File.separator;
			boolean flagGuidKey=false;  //定义该标志是为了判断查询的列是否包含GuidKey
			flagGuidKey=isRowSetExistField(rset, "GuidKey");
			String a0100_dis = "";
			HashMap a0100Map = new HashMap();
			ArrayList a0100List = new ArrayList();
			while(rset.next())
			{
			  ArrayList valuelist=new ArrayList();
			  String a0100_ = rset.getString(paramname);
			  if(a0100_dis.equals(a0100_)) {//同一个人同一个梦想
				  a0100List = (ArrayList)a0100Map.get(a0100_dis);
				  valuelist = this.getValueList(rset,list,flagGuidKey,valuelist,rootDir,a0100_dis);
				  if((nhismode==2||nhismode==4)&&(mode==1||mode==0))
					  a0100List.add(0,valuelist);  
				  else
					  a0100List.add(valuelist);
			  }else {
				  if(!a0100Map.isEmpty()){
					  a0100List = (ArrayList)a0100Map.get(a0100_dis);
					  //处理上一个人的结果集
					  a0100List = this.reGetValueList(a0100List,datarowcount);
					  a0100Map.put(a0100_dis,a0100List);
					  reclist.add(a0100Map);
				  }
				  a0100_dis = a0100_;
				  a0100Map = new HashMap();
				  a0100List = new ArrayList();
				  valuelist = this.getValueList(rset,list,flagGuidKey,valuelist,rootDir,a0100_);
				  if((nhismode==2||nhismode==4)&&(mode==1||mode==0))
					  a0100List.add(0,valuelist);  
				  else
					  a0100List.add(valuelist);
				  a0100Map.put(a0100_, a0100List);
			  }
			}
			if(a0100List.size()>0) {
				a0100List = this.reGetValueList(a0100List,datarowcount);
				a0100Map.put(a0100_dis,a0100List);
				reclist.add(a0100Map);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return reclist;
	}
	
	private ArrayList reGetValueList(ArrayList a0100List, int datarowcount) {
		String midi9999 = "";
		ArrayList reclist_ = new ArrayList();
		if(datarowcount>0){//设定了指定行数
			int size = a0100List.size();
			if(size>datarowcount){//存在的大于指定的
				ArrayList valuelist = (ArrayList)a0100List.get(size-datarowcount);
				midi9999 = (String)valuelist.get(0);
			}else{
				
			}
		}
		if(!"".equals(midi9999)){
			for(int i=0;i<a0100List.size();i++){
				ArrayList valuelist = (ArrayList)a0100List.get(i);
				String i9999 = (String)valuelist.get(0);
				if(Integer.parseInt(i9999)>=Integer.parseInt(midi9999))
					reclist_.add(valuelist);
			}
		}else
			reclist_ = a0100List;
		return reclist_;
	}
	private ArrayList getValueList(RowSet rset, ArrayList list, boolean flagGuidKey, ArrayList valuelist, String rootDir, String a0100_dis) {
		  String value="";
		  try {
			  String i9999=rset.getString("i9999");
			  valuelist.add(i9999);
			  //valuelist.add(a0100_dis);
			  for(int i=0;i<list.size();i++)
			  {
				FieldItem item=(FieldItem)list.get(i);
				String field_type=item.getItemtype();
				String field_name=item.getItemid();
				int disformat =item.getDisplayid();
				if("M".equalsIgnoreCase(field_type))
				{
					value=Sql_switcher.readMemo(rset,field_name);
				}
				else if("D".equalsIgnoreCase(field_type))
				{
					/**yyyy-MM-dd*/
					value=PubFunc.FormatDate(rset.getDate(field_name));
				    value=utilBo.getFormatDate(value,disformat);
	
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
			  }
			  
			  //当包含附件的时候，需要根据GuidKey的值将附件的信息查询出来 liuzy 20151028
			  if(flagGuidKey){
			      String GuidKey=rset.getString("GuidKey");
				  ContentDAO dao=new ContentDAO(this.conn);
					try {
						String sql="select id,filename,path,class,srcfilename,topic,ext from hr_multimedia_file where childguid='"+GuidKey+"'";
						RowSet flagset=dao.search(sql); 
						int m=0;
						String valuestr="";
						while(flagset.next()){
							m++;
							String id=flagset.getString("id");                   //文件唯一标识 
							String filename=flagset.getString("filename");       //编码后文件名
							String path= flagset.getString("path");               //文件上传路径
							//String path= rootDir + flagset.getString("path").replace("\\", File.separator);               //文件上传路径
							String srcfilename=flagset.getString("topic")+flagset.getString("ext"); //原始文件名 
							String filetype = flagset.getString("class");//文件类型
							VfsFileEntity fileEnty=VfsService.getFileEntity(path);
							int s=fileEnty.getFilesize();
					        /*File f= new File(path,filename);  
					        long s=0;
					        if (f.exists()) {
					            FileInputStream fis = null;
					            fis = new FileInputStream(f);
					            s= fis.available();
					        } else {
					        }*/
					        DecimalFormat df = new DecimalFormat("#0.00");
					        String fileSizeString = "";
					        if (s < 1048576) {
					            fileSizeString = df.format((double) s / 1024) + "K";
					        } else if (s < 1073741824) {
					            fileSizeString = df.format((double) s / 1048576) + "M";
					        } 
							String text=""+"|"+path+"|"+srcfilename+"|"+fileSizeString+"|"+id+"|"+m+"|"+"type:"+filetype ;
							valuestr+=text+",";
						}
						if(valuestr.length()>0){
							valuestr=valuestr.substring(0, valuestr.length()-1);
							//调用vfs附件无需保存到临时目录
							//valuestr = this.saveSubAttachmentToTemplate(valuestr);//将子集附件保存到模板临时目录 lis 20160912 add
						}
						valuelist.add(valuestr);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				  
			  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  return valuelist;
	}
	/**
	 * @author lis
	 * @Description: 保存子集附件到模板临时目录
	 * @date Jul 18, 2016
	 * @param attachmentValues
	 * @return
	 * @throws GeneralException
	 */
	public String saveSubAttachmentToTemplate(String attachmentValues) throws GeneralException{
		StringBuffer attachment = new StringBuffer();;
		try {
			 AttachmentBo attachmentBo = new AttachmentBo(this.userView, conn,this.tabId +"");
			 if(StringUtils.isNotBlank(attachmentValues)){
				String[] attachmentValueArry = attachmentValues.split(",");
				for(String attachmentValue : attachmentValueArry){
					if(StringUtils.isBlank(attachmentValue))
						continue;
					String[] subDataArry = attachmentValue.split("\\|");
					String filePath = subDataArry[1] +File.separator + subDataArry[0];
					filePath = filePath.replace("\\", File.separator).replace("/", File.separator);
					String middlepath = "";
					if("\\".equals(File.separator)){//证明是windows
						middlepath = "subdomain\\template_";
					}else if("/".equals(File.separator)){//证明是linux
						middlepath = "subdomain/template_";
					}
					
					File file = new File(filePath);
					if(file.exists()){
						StringBuffer tempValue = new StringBuffer();
						HashMap valueMap = new HashMap();
						//保存文件到指定目录
						attachmentBo.setRealFileName(subDataArry[2]);
						valueMap = attachmentBo.SaveFileToDisk(file, middlepath);
						for(int i = 0; i < subDataArry.length; i++){
							if(i == 1)
								tempValue.append("|" + attachmentBo.getAbsoluteDir());
							else
								tempValue.append("|" + subDataArry[i]);
						}
						if(tempValue.length() > 0)
							attachmentValue = tempValue.substring(1);
					}
					attachment.append("," + attachmentValue);
				}
             }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		if(attachment.length() > 0)
			attachmentValues = attachment.substring(1);
		return attachmentValues;
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
			   if(field.equalsIgnoreCase(rsmd.getColumnName(i))){
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
			while(rset.next())
				paralist.add(rset.getString("I9999"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return paralist;
	}
	
	/**
	 * 获取大文本字段
	 * @return
	 */
	private HashMap getBigMemoMap()
	{
		if (this.bigMemoFieldMap ==null ){
		    HashMap map=new HashMap();
		    try {
		        ArrayList fieldlist=this.getAllTemplateItem();
		        for(int i=0;i<fieldlist.size();i++)
		        {
		            TemplateItem temItem=(TemplateItem)fieldlist.get(i);
		            FieldItem fielditem=temItem.getFieldItem();
		            if(!temItem.getFieldType().equals(fielditem.getItemtype())){
		                if ("M".equals(temItem.getFieldType())) {
		                    map.put(temItem.getFieldName(), temItem.getFieldName());
		                }
		            }
		        }
		    } catch (GeneralException e) {
		        e.printStackTrace();
		    }
		    this.bigMemoFieldMap=  map; 
		}
		return this.bigMemoFieldMap;
	}
	
	/**
	 * 一次性导入主集记录
	 * B0110_1,E0122_1,A0101_1,basepre,A0100,state
	 * @param a0100s
	 * @param dbpre
	 * @param sync =0导入　　=1更新
	 * @return
	 * @throws GeneralException
	 */
	private boolean impMainSetFromArchive(String a0100s,String dbpre, int sync)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			boolean bphoto=false;
			StringBuffer strsql=new StringBuffer();
			String tablename=null;
			
			if(isSelfApply())
				 tablename="g_templet_"+this.tabId;
			else
				tablename=this.userView.getUserName()+"templet_"+this.tabId;
			if(!"0".equals(this.taskId))
				tablename = "templet_"+this.tabId;
			
			if(getImpOthTableName()!=null&&getImpOthTableName().trim().length()>0)  //供高级花名册调用人事异动的人员引入功能，将数据导入到临时表中
				tablename=getImpOthTableName();
			
			ArrayList fieldlist=this.getAllTemplateItem();
			StringBuffer sqlin_tablename = new StringBuffer("");
			if(this.paramBo.getInfor_type()==1) {
				String paramname = "a0100";
				String[] a0100arr =a0100s.split(",");
				sqlin_tablename = this.getSqlIn(a0100arr, tablename+"."+paramname);
			}
			
			if(sync==0){
				StringBuffer str_dfields=new StringBuffer();
				StringBuffer str_sfields=new StringBuffer();
				if(this.paramBo.getTemplateStatic()==10) //单位管理
				{
					str_dfields.append("b0110,state");
					str_sfields.append("b0110,0");
				}
				else if(this.paramBo.getTemplateStatic()==11) //职位管理
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
				
				ContentDAO dao=new ContentDAO(this.conn);
				String fieldname=null;
					
				for(int i=0;i<fieldlist.size();i++)
				{
				    TemplateItem templateItem=(TemplateItem)fieldlist.get(i);
				    FieldItem fielditem=(FieldItem)templateItem.getFieldItem();
				    
					if(fielditem.getVarible()==1)
						continue;
					if("0".equals(fielditem.getUseflag())&&!"photo".equals(fielditem.getItemid()))
						continue;
					if(this.paramBo.getInfor_type()==2||this.paramBo.getInfor_type()==3)
					if("codesetid".equalsIgnoreCase(fielditem.getItemid())|| "codeitemdesc".equalsIgnoreCase(fielditem.getItemid())|| "corcode".equalsIgnoreCase(fielditem.getItemid())|| "parentid".equalsIgnoreCase(fielditem.getItemid())|| "start_date".equalsIgnoreCase(fielditem.getItemid()))
						continue;
					if("photo".equals(fielditem.getItemid()))
					{
						bphoto=true;
						continue;
					}
					if(fielditem.isChangeAfter()&&this.paramBo.getOpinion_field()!=null&&this.paramBo.getOpinion_field().length()>0&&this.paramBo.getOpinion_field().equalsIgnoreCase(fielditem.getItemid()))
					{
						continue;
					}
					boolean isOk=true;
					if(this.paramBo.getInfor_type()==1) //人员信息处理
						isOk=fielditem.isPerson();
					if(fielditem.isMainSet()&&isOk&&fielditem.isChangeBefore())
					{
							fieldname=templateItem.getFieldName();
							if(str_dfields.indexOf(fieldname)!=-1)
								continue;
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
				
				if(this.paramBo.getInfor_type()==1) //人员信息处理
				{
					strsql.append(dbpre);
					strsql.append("A01 where ");
					String[] temp =a0100s.split(",");
					if(temp!=null&&temp.length>0){
						
						int zheng=temp.length/500;
						int yu = temp.length%500;
						if(yu!=0)
							zheng++;
						
						for (int j = 0; j < zheng; j++) {
							StringBuffer tmp=new StringBuffer("");
							tmp.append(" a0100 in (");
							for(int i=j*500;i<(j+1)*500&&i<temp.length;i++){
								if(i!=j*500){
									tmp.append(",");
								}
								tmp.append(temp[i]);
							}
							tmp.append(")");
							tmp.append(" and a0100 not in (select a0100 from ");
							tmp.append(tablename);
							tmp.append(" where upper(basepre)='");
							tmp.append(dbpre.toUpperCase());
							tmp.append("')");
							int n=dao.update(strsql.toString()+tmp.toString());
						}
						
						
					}
					
				}
				else if(this.paramBo.getInfor_type()==2) //单位信息处理
				{ 
					strsql.append("B01 where b0110 in (");
					strsql.append(a0100s);
					strsql.append(") and b0110 not in (select b0110 from ");
					strsql.append(tablename);
					strsql.append("  )");
				}
				else if(this.paramBo.getInfor_type()==3) //职位信息处理
				{
					strsql.append("K01 where E01A1 in (");
					strsql.append(a0100s);
					strsql.append(") and E01A1 not in (select E01A1 from ");
					strsql.append(tablename);
					strsql.append("  )");
				}
					
				if(this.paramBo.getInfor_type()!=1)
					 dao.update(strsql.toString());
				//如果信息表中没有相应记录
				if(this.paramBo.getInfor_type()==2||this.paramBo.getInfor_type()==3)
				{
					String key_field="";
					String _str="insert into "+tablename+" ( ";
					if(this.paramBo.getTemplateStatic()==10) //单位管理 
					{
						_str+="b0110,state";
						key_field="b0110";
					}
					else if(this.paramBo.getTemplateStatic()==11) //职位管理 
					{
						_str+="e01a1,state";
						key_field="e01a1";
					}
					_str+=" ) select codeitemid,0 from organization where codeitemid in ("+a0100s+") and codeitemid not in (select "+key_field+" from "+tablename+" )";
					dao.update(_str);
				}
				
				
				if(this.paramBo.getInfor_type()==2||this.paramBo.getInfor_type()==3)
				{
					DbWizard dbw=new DbWizard(this.conn);
					String _name="b0110";
					if(this.paramBo.getInfor_type()==3)
						_name="e01a1";
					
					String sql="update "+tablename+" set codeitemdesc_1=(select codeitemdesc from organization where "+tablename+"."+_name+"=organization.codeitemid ) where "+tablename+"."+_name+" in ("+a0100s+") ";
					dbw.execute(sql);
					if("1".equals(this.paramBo.getChange_after_get_data()))
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
					
				}
			}
			else
			{
				for(int i=0;i<fieldlist.size();i++)
				{
				    TemplateItem templateItem=(TemplateItem)fieldlist.get(i);
				    FieldItem fielditem=(FieldItem)templateItem.getFieldItem();

					if("photo".equals(fielditem.getItemid())&&"0".equalsIgnoreCase(this.taskId))//只有起草状态且勾选了自动同步变化前指标才会每次更新照片
					{
						bphoto=true;
						break;
					}
				}
			}
			/**不导入照片信息，除人员调入操作外，全都从档案记录中取得照片  || linbz 26542  刷新的时候也同步*/
			if(bphoto)
			{
				/**photo ,ext*/
				strsql.setLength(0);
				DbWizard dbw=new DbWizard(this.conn);
				int dbflag = Sql_switcher.searchDbServer();
				if(this.paramBo.getInfor_type()==1) //人员信息处理
				{
					//liuyz 人员头像更新 原来有的刷新为原来的，原来没有的不刷新。
					String srctab=dbpre+"A00";
					StringBuilder sql=new StringBuilder();
					String dbProductName;
			        dbProductName = conn.getMetaData().getDatabaseProductName();
			        if(dbProductName != null){
			            dbProductName = dbProductName.toLowerCase();
			            if(dbProductName.indexOf("oracle") != -1)
			                dbflag = 2;
			            else
			            if(dbProductName.indexOf("oscar") != -1)
			                dbflag = 6;
			            else
			            if(dbProductName.indexOf("db2") != -1)
			                dbflag = 3;
			            else
			            if(dbProductName.indexOf("kunlun") != -1)
			                dbflag = 7;
			            else
			                dbflag = 1;
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
							sql.append(" photo,").append(tablename).append(".ext,fileid ");
							sql.append(")=(select ");
							sql.append(" ole, ").append(srctab).append(".ext,fileid ");
							sql.append(" from ");
							sql.append(srctab);
							sql.append(" where ");
							sql.append(tablename+".A0100="+srctab+".A0100");
			                sql.append(" and ");
			                sql.append(srctab+".flag='P' ");
			                sql.append(")");
			                sql.append(" where ");
			                sql.append(tablename).append(".a0100 in ( select ").append(srctab).append(".a0100 from ").append(srctab).append(" left join ").append(tablename).append(" on ").append(tablename).append(".A0100=").append(srctab).append(".A0100 where ").append(sqlin_tablename).append(" and ").append(tablename).append(".basepre='"+dbpre+"') and basepre='").append(dbpre).append("'");
							break;
			            }
			            case 4: // '\004'
			            default:
			            {
			            	String strSet = ("photo=ole`"+tablename+".ext="+srctab+".ext,fileid="+srctab+".fileid").replace('`', ',');
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
			                sql.append(tablename).append(".a0100 in ( select ").append(srctab).append(".a0100 from ").append(srctab).append(" left join ").append(tablename).append(" on ").append(tablename).append(".A0100=").append(srctab).append(".A0100 where ").append(sqlin_tablename).append(" and ").append(tablename).append(".basepre='"+dbpre+"') and basepre='").append(dbpre).append("'");
			            }
			        }
					dao.update(sql.toString());
			        }
					//dbw.updateRecord(tablename,srctab ,tablename+".A0100="+srctab+".A0100","photo=ole`"+tablename+".ext="+srctab+".ext",tablename+".a0100 in ("+a0100s+") and basepre='"+dbpre+"'",srctab+".flag='P'");
				}
				else if(this.paramBo.getInfor_type()==2) //单位信息处理
				{
					String srctab="B00";
					dbw.updateRecord(tablename,srctab ,tablename+".B0110="+srctab+".B0110","photo=ole`"+tablename+".ext="+srctab+".ext",tablename+".B0110 in ("+a0100s+")  ",srctab+".flag='P'");
				}
				else if(this.paramBo.getInfor_type()==3) //职位信息处理
				{
					String srctab="K00";
					dbw.updateRecord(tablename,srctab ,tablename+".E01A1="+srctab+".E01A1","photo=ole`"+tablename+".ext="+srctab+".ext",tablename+".E01A1 in ("+a0100s+")  ",srctab+".flag='P'");
				}
			}
				
			//写入唯一标识 seqnum
			/*
			{
				String sql="select * from "+tablename ;
				if(this.paramBo.getInfor_type()==1){
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
				else if(this.paramBo.getInfor_type()==2) //单位信息处理
					sql+=" where B0110 in ("+a0100s+") ";
				else if(this.paramBo.getInfor_type()==3) //职位信息处理 
					sql+=" where E01A1 in ("+a0100s+") "; 
				RowSet rowSet=dao.search(sql);
				while(rowSet.next())
				{
					String seqnum=CreateSequence.getUUID();
					if(this.paramBo.getInfor_type()==1) 
					{
						dao.update("update "+tablename+" set seqnum='"+seqnum+"',submitflag=1 where a0100='"+rowSet.getString("a0100")+"' and  lower(basepre)='"+dbpre.toLowerCase()+"'");
					}
					else if(this.paramBo.getInfor_type()==2)
					{
						dao.update("update "+tablename+" set seqnum='"+seqnum+"',submitflag=1 where b0110='"+rowSet.getString("b0110")+"'");
					}
					else if(this.paramBo.getInfor_type()==3)
					{
						dao.update("update "+tablename+" set seqnum='"+seqnum+"',submitflag=1 where E01A1='"+rowSet.getString("E01A1")+"'");
					}
				}
				
				
				
			}*/
				
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	
	
	/**   
	 * @Title: computeMidVar   
	 * @Description: 自动计算临时变量   
	 * @param @return
	 * @param @throws GeneralException 
	 * @return boolean 
	 * @throws   
	*/
	public boolean computeMidVar()throws GeneralException
    {
        boolean bflag = true;
        try {
            // 自动计算临时变量
            if (!isComputeVar()) {// 判断是否计算过临时变量
                String where = "";
                Object[] dbarr = null;
                if (this.paramBo.getInfor_type() == 1) {
                    String tablename = getBz_tablename();
                    ArrayList dblist = searchDBPreList(tablename);
                    dbarr = dblist.toArray();
                    if (isSelfApply()) // 员工通过自助平台发动申请
                    {
                        where = " where lower(" + tablename + ".basepre)='" + this.userView.getDbname().toLowerCase() + "'  and " + tablename + ".a0100='" + this.userView.getA0100() + "'";
                    }
                    else if(!"0".equals(this.taskId)){    
                    	where = " where seqnum in  (select seqnum from  t_wf_task_objlink  where task_id in("+this.taskId+") and tab_id="+this.tabId+" ) ";
    		             
    	            }
                }
                setOnlyComputeFieldVar(true);
                ArrayList fieldlist = getMidVariableList();
                addMidVarIntoGzTable(where, dbarr, fieldlist);
                setOnlyComputeFieldVar(false);
                setComputeVar(true);
            }

        } catch (Exception ex) {
            throw GeneralExceptionHandler.Handle(ex);
        }
        return bflag;
    }
	   
	/**   
	 * @Title: computeMidVar   
	 * @Description: 自动计算临时变量   
	 * @param @return
	 * @param @throws GeneralException 
	 * @return boolean 
	 * @throws   
	*/
	public boolean computeMidVar(String where_a0100,String dbpre)throws GeneralException
    {

        boolean bflag = true;
        try {
            // 自动计算临时变量
            if (!isComputeVar()) {// 判断是否计算过临时变量
                String where = "";
                Object[] dbarr = null;
                if (this.paramBo.getInfor_type() == 1) {
                    String tablename = getBz_tablename();
                    ArrayList dblist = searchDBPreList(tablename);
                    dbarr = dblist.toArray();
                    if (isSelfApply()) // 员工通过自助平台发动申请
                    {
                        where = " where lower(" + tablename + ".basepre)='" + this.userView.getDbname().toLowerCase() + "'  and " + tablename + ".a0100='" + this.userView.getA0100() + "'";
                    }
                    else if(!"0".equals(this.taskId)){    
                    	where = " where seqnum in  (select seqnum from  t_wf_task_objlink  where task_id in("+this.taskId+") and tab_id="+this.tabId+" ) ";
    		             
    	            }else if("0".equals(this.taskId)&&StringUtils.isNotEmpty(where_a0100)) {//发起节点时
    	            	where = " where lower(" + tablename + ".basepre)='" + dbpre.toLowerCase() + "' and "+tablename+".a0100 in ("+where_a0100+")";
    	            }
                }
               
                setOnlyComputeFieldVar(true);
                ArrayList fieldlist = getMidVariableList();
                addMidVarIntoGzTable(where, dbarr, fieldlist);
                setOnlyComputeFieldVar(false);
                setComputeVar(true);
            }

        } catch (Exception ex) {
            throw GeneralExceptionHandler.Handle(ex);
        }
        return bflag;
    
	}
	
	/**   
	 * @Title: impDataFromArchive   
	 * @Description:  导入人员数据：手工选择、通用查询、按条件检索、自助业务申请-自动导入当前人数据时调用   
	 * @param @param a0100s //人员编号
	 * @param @param dbpre 所在库
	 * @param @return
	 * @param @throws GeneralException 
	 * @return boolean 
	 * @throws   
	*/
	public boolean impDataFromArchive(ArrayList a0100s,String dbpre)throws GeneralException
	{
		if (a0100s.size()<1 || (dbpre!=null && "".equals(dbpre.trim()))){
		    return true;
		}
		boolean bflag=true;
		RowSet rowSet=null;
		try
		{
			ArrayList dbList=DataDictionary.getDbpreList();
			for(int i=0;i<dbList.size();i++)
			{
				String pre=(String)dbList.get(i);
				if(pre.equalsIgnoreCase(dbpre))
					dbpre=pre;
			}
			StringBuffer stra0100 = getA0100String(a0100s);
			boolean bImp=true;// true:导入  false:更新
			if(a0100s.size()==1&&isSelfApply()) //员工通过自助平台发动申请
			{
				
				String	strDesT="g_templet_"+this.tabId;
				ContentDAO dao=new ContentDAO(this.conn);
				StringBuffer sql = new StringBuffer("");
				sql.append("select count(*) from ").append(strDesT);
				sql.append(" where a0100=").append(stra0100);
				sql.append(" and lower(basepre)='").append(dbpre.toLowerCase()).append("'");
//				("select count(*) from "+strDesT+" where a0100="+stra0100+" and lower(basepre)='"+dbpre.toLowerCase()+"'");
				rowSet=dao.search(sql.toString());
				if(rowSet.next()){
					if(rowSet.getInt(1)>0)
					    bImp=false;
				}
			}
			//else
			if (bImp){
			    impDataFromArchive(stra0100.toString(),dbpre,0,new ArrayList()); 
			    //新增人时需要计算临时变量
			    computeMidVar(stra0100.toString(),dbpre);
			}
			else {
				if("1".equals(this.paramBo.getAutosync_beforechg_item()))
					impDataFromArchive(stra0100.toString(),dbpre,1,new ArrayList());   
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			String message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{ 
				String destTab=getBz_tablename();
				if(a0100s.size()==1&&isSelfApply())
					destTab="g_templet_"+this.tabId;
				PubFunc.resolve8060(this.conn,destTab);
				throw GeneralExceptionHandler.Handle(new Exception("请重新操作!"));
			}
			else
				throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeDbObj(rowSet);
		}
		return bflag;		
	}
	/**
	 * 同步刷新子集数据
	 * @param a0100s
	 * @param dbpre
	 * @param columnName
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList refDataFromArchive(ArrayList a0100s,String dbpre,String columnName)throws GeneralException
	{
		ArrayList reclist = new ArrayList();
		if (a0100s.size()<1 || (dbpre!=null && "".equals(dbpre.trim()))){
		    return reclist;
		}
	    boolean bflag=true;
		try
		{
			ArrayList dbList=DataDictionary.getDbpreList();
			for(int i=0;i<dbList.size();i++)
			{
				String pre=(String)dbList.get(i);
				if(pre.equalsIgnoreCase(dbpre))
					dbpre=pre;
			}
			StringBuffer stra0100 = getA0100String(a0100s);
			reclist=refDataFromArchive(stra0100.toString(),dbpre,columnName);		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			/*String message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{ 
				String destTab=getBz_tablename();
				if(a0100s.size()==1&&isSelfApply())
					destTab="g_templet_"+this.tabId;
				PubFunc.resolve8060(this.conn,destTab);
				throw GeneralExceptionHandler.Handle(new Exception("请重新操作!"));
			}
			else*/
			throw GeneralExceptionHandler.Handle(ex);
		}
		return reclist;		
	}
	/**
	 * 同步刷新子集数据
	 * @param a0100s
	 * @param dbpre
	 * @param columnName
	 * @return
	 */
	private ArrayList refDataFromArchive(String a0100s, String dbpre,String columnName) throws GeneralException{
		ArrayList reclist = new ArrayList();
		int nmode=0,nhismode=0,ncount=0,nchgstate=0;
		String setname=null;
		String cname=null;
		String field_name=null;
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		StringBuffer strsql = new StringBuffer();
		int db_type=Sql_switcher.searchDbServer();//数据库类型
		Document doc=null;
		Element element=null;
		int columnindex = columnName.lastIndexOf("_");
		String chgstate = columnName.substring(columnindex+1,columnName.length());
		String xpath="/sub_para/para";
		/**子集区域的数据*/
		if(db_type==2)//oracle
			strsql.append("select T.setname,T.ChgState,T.formula,T.hismode,T.rcount,T.mode_o,T.subflag,T.sub_domain from template_set T  where ");
		else
			strsql.append("select T.setname,T.ChgState,T.formula,T.hismode,T.rcount,T.mode,T.subflag,T.sub_domain from template_set T  where ");
		strsql.append(" T.tabid=");
		strsql.append(this.tabId);
		strsql.append(" and ");
		strsql.append(" subflag=1");
		strsql.append(" and T.setname='");
		strsql.append(columnName.split("_")[1].toUpperCase());
		strsql.append("'");
		strsql.append(" and T.ChgState='");
		strsql.append(chgstate);
		strsql.append("'");
		
		try {
			rset=dao.search(strsql.toString());
			while(rset.next())
			{
				nchgstate=rset.getInt("chgstate");
				if(db_type==2)//oracle
					nmode=rset.getInt("mode_o");
				else
					nmode=rset.getInt("mode");
				ncount=rset.getInt("rcount");
				nhismode=rset.getInt("hismode");
				cname=rset.getString("setname");
				field_name="t_"+cname+"_"+nchgstate;
				String subxml=Sql_switcher.readMemo(rset, "sub_domain");
				String formula=Sql_switcher.readMemo(rset, "formula");
				/**对子集区域数据同步规则*/
				if(/*(nchgstate==2)&&*/cname!=null&&cname.trim().length()>0){
					//获得sub_domain_id
					String sub_domain_id="";
					//获得第x到y中的x值
					String his_start2="";
					int his_start =0;
					if(subxml!=null&&subxml.trim().length()>0){
						try{
								doc=PubFunc.generateDom(subxml);
								XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
								List childlist=findPath.selectNodes(doc);	
								if(childlist!=null&&childlist.size()>0)
								{
									element=(Element)childlist.get(0);
									if(element.getAttributeValue("id")!=null){
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
					if(his_start2.length()>0)
						his_start = Integer.parseInt(his_start2);
					if(sub_domain_id!=null&&sub_domain_id.length()>0)
						field_name="t_"+cname+"_"+sub_domain_id+"_"+nchgstate;
					if (columnName.equalsIgnoreCase(field_name)){
						//reclist = refSubDomainData(a0100s,dbpre,subxml,cname,field_name.toLowerCase(),nhismode,nmode,ncount,formula,his_start);
						reclist = impSubDomainData(a0100s,dbpre,subxml,cname,field_name.toLowerCase(),nhismode,nmode,ncount,formula,his_start,new ArrayList(),"1");
					}
				}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}finally{
				PubFunc.closeDbObj(rset);
			}
			return reclist;
	}
	/**
	 * 同步刷新子集数据
	 * @param a0100s
	 * @param dbpre
	 * @param xmlfmt
	 * @param setname
	 * @param field_name
	 * @param nhismode
	 * @param mode
	 * @param count
	 * @param formula
	 * @param his_start
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList refSubDomainData(String a0100s,String dbpre,String xmlfmt,String setname,String field_name,int nhismode,int mode,int count,String formula,int his_start)throws GeneralException{
		ArrayList reclist = new ArrayList();
		if(xmlfmt==null|| "".equalsIgnoreCase(xmlfmt))
			return reclist;
		TSubSetDomain setdomain=new TSubSetDomain(xmlfmt);
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
		if(this.paramBo.getInfor_type()==2||this.paramBo.getInfor_type()==3)
			tablename=setname;
		ArrayList fieldlist=new ArrayList();
		/**人员*/
        try
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
				if(item==null|| "0".equals(item.getUseflag()))
					continue;
				
	/*			if(isSelfApply())//员工通过自助平台发动申请
				{
					if(this.userView.analyseFieldPriv(name,0).equals("0"))
						continue;
				}
				else  */
				{
					if("0".equals(this.userView.analyseFieldPriv(name))&& "0".equals(this.paramBo.getUnrestrictedMenuPriv_Input()))
						continue;
				}
				fieldlist.add(item);
				buf.append(",");
				buf.append(name);
			}//for i loop end.
			
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
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			strsql.append(buf.toString());
			
			//如果支持附件，查询子集数据时需将GuidKey也查询出来，方便去hr_multimedia_file查找附件 liuzy 20151028
			if(attachFlag){
				if(dbw.isExistField(tablename, "GuidKey",false))
					strsql.append(",GuidKey");
			}
			if(buf.length()==0&&!attachFlag)//liuyz 如果只引入附件，buf.length()==0,原来没判断attachFlag，只引入附件会同步不了附件信息。
				return reclist;
			strsql.append(" from ");
			strsql.append(tablename);
			strsql2.append(buf.toString());
			strsql2.append(" from ");
			strsql2.append(tablename);
			if(this.paramBo.getInfor_type()==1){
				strsql.append(" where a0100=? ");
				strsql2.append(" where a0100=' ");
			}
			else if(this.paramBo.getInfor_type()==2){
				strsql.append(" where b0110=? ");
				strsql2.append(" where b0110=' ");
			}
			else if(this.paramBo.getInfor_type()==3){
				strsql.append(" where e01a1=? ");
				strsql2.append(" where e01a1=' ");
			}
			String[] a0100arr=StringUtils.split(a0100s,",");
           
			StringBuffer task_str=new StringBuffer("");
            String templatetablename=this.getBz_tablename();
            if(!"0".equals(this.taskId))
            { 
                for(int j=0;j<getTasklist().size();j++)
                {
                    if(getTasklist().get(j)!=null&&((String)getTasklist().get(j)).trim().length()>0)
                        task_str.append(","+(String)getTasklist().get(j));
                }
            }
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
				
				//取临时变量
			    String midVarSql= "";
	            if(this.paramBo.getInfor_type()==1)
	                midVarSql= "select * from "+templatetablename+" where a0100='" +a0100+"' and lower(basepre)='"+dbpre.toLowerCase()+"'";
	            else if(this.paramBo.getInfor_type()==2)
	                midVarSql="select * from "+templatetablename+" where b0110='"+a0100+"' ";
	            else if(this.paramBo.getInfor_type()==3)
	                midVarSql="select * from "+templatetablename+" where e01a1='"+a0100+"'";
	            if(!"0".equals(this.taskId)){   
	                //midVarSql =midVarSql+" and task_id in ("+task_str.substring(1)+")"; 
	                if("1".equals(this.isStartNode(this.taskId))){
		            	midVarSql = "select a.* from "+templatetablename+" a,t_wf_task_objlink b where a.seqnum = b.seqnum and a.ins_id=b.ins_id and b.task_id="+this.taskId+" and b.tab_id="+this.tabId+"";
		            }
	            }
	            
	            RowSet rMidVarSet=null;//位置不能变
				
					/**求子集序号列表*/
					ArrayList i9999list=getSubSetI9999s(tablename,a0100);
					int size=i9999list.size();
					if(size>0)
					{
						/**初值为-1*/
						String curri9999="-1";
						if(nhismode==1) //当前记录
						{
								if(size>=1)
									curri9999=(String)i9999list.get(size-1);
							sql.append(" and I9999=?");							
							paralist.add(curri9999);							
							
						}
						else if(nhismode==3) //条件定位
						{

							String[] preCond=getPrefixCond(formula);
							String cond=preCond[1]!=null?preCond[1]:"";
							
							if(cond.length()>0)
							{
							    String strFactor= preCond[2]!=null?preCond[2]:"";
							    //判断因子表达式是否有临时变量 替换
							    strFactor= reCombineFactor(rMidVarSet,midVarSql,strFactor);							   
								FactorList factorlist=new FactorList(cond,strFactor,"");
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
							    String strFactor= preCond[2]!=null?preCond[2]:"";
                                //判断因子表达式是否有临时变量 替换
                                strFactor= reCombineFactor(rMidVarSet,midVarSql,strFactor);                            
                                FactorList factorlist=new FactorList(cond,strFactor,"");
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
							 i9999list=getSubSetI9999s(sql2.toString()+" order by i9999");
							 size=i9999list.size();
							if(size>0)
							{
								switch(mode)
								{  
							case 0:
									if(size>=count)//子集记录大于要取的的记录数
									{
										if(size==count)
											curri9999=(String)i9999list.get(0);
										else
										{
											if(count!=0)
												curri9999=(String)i9999list.get(size-count);
											else
												curri9999=(String)i9999list.get(size-count-1);
										}
									}
								sql.append(" and I9999=?");							
								paralist.add(curri9999);							
								break;
							case 1://倒数...条（最近）
								if(his_start==0){
									if(size>=count)
									{
										if(size==count)
											curri9999=(String)i9999list.get(0);
										else
											curri9999=(String)i9999list.get(size-count);
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
											if(size<his_start+count)
												curri9999=(String)i9999list.get(0);
											else
												curri9999=(String)i9999list.get(size-count-(his_start-1));
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
								if(size>=count)
									curri9999=(String)i9999list.get(count-1);
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
									if(size==count)
										curri9999=(String)i9999list.get(0);
									else
									{
										if(count!=0)
											curri9999=(String)i9999list.get(size-count);
										else
											curri9999=(String)i9999list.get(size-count-1);
									}
								}
							sql.append(" and I9999=?");							
							paralist.add(curri9999);							
							break;
						case 1://倒数...条（最近）
							if(his_start==0){
								if(size>=count)
								{
									if(size==count)
										curri9999=(String)i9999list.get(0);
									else
										curri9999=(String)i9999list.get(size-count);
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
										if(size<his_start+count)
											curri9999=(String)i9999list.get(0);
										else
											curri9999=(String)i9999list.get(size-count-(his_start-1));
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
							if(size>=count)
								curri9999=(String)i9999list.get(count-1);
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
					RowSet rset=dao.search(sql.toString(),paralist);
					reclist=getrefRecordValue(fieldlist,rset,setdomain);
			}
		}		
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return reclist;	
	
	}
	/**
	 * 取得记录串(子集记录刷新同步)
	 * @param list
	 * @param rset
	 * @param setdomain 
	 * @return
	 */
	private ArrayList getrefRecordValue(ArrayList list ,RowSet rset, TSubSetDomain setdomain)
	{
		ArrayList reclist=new ArrayList();
		ArrayList reclist_=new ArrayList();
		ArrayList i9999list=new ArrayList();
		String value="";
		try
		{
			ArrayList fieldfmtlist  = setdomain.getFieldfmtlist();
			int datarowcount = setdomain.getDatarowcount();
			ConstantXml constantXml = new ConstantXml(this.conn,"FILEPATH_PARAM");
			String rootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
            rootDir=rootDir.replace("\\",File.separator);
            if (!rootDir.endsWith(File.separator)) rootDir =rootDir+File.separator;
            rootDir += "multimedia"+File.separator;
			boolean flagGuidKey=false;  //定义该标志是为了判断查询的列是否包含GuidKey
			flagGuidKey=isRowSetExistField(rset, "GuidKey");
			
			while(rset.next())
			{
			  HashMap  maps = new HashMap();
			  ArrayList valuelist=new ArrayList();
			  String i9999=rset.getString("i9999");
			  i9999list.add(i9999);
			  maps.put("columname", "i9999");
			  maps.put("columnvalue", i9999);
			  if(!"-1".equals(i9999)){
				  HashMap  maps1 = new HashMap();
				  maps1.put("columname", "hisEdit");
				  maps1.put("columnvalue", setdomain.getHis_edit());
				  valuelist.add(maps1);
			  } 
			  valuelist.add(maps);
			  for(int i=0;i<list.size();i++)
			  {
				HashMap  map = new HashMap();
				FieldItem item=(FieldItem)list.get(i);
				String field_type=item.getItemtype();
				String field_name=item.getItemid();
				int disformat =item.getDisplayid();
				TFieldFormat fieldformat=(TFieldFormat)fieldfmtlist.get(i);
				if("M".equalsIgnoreCase(field_type))
				{
					value=Sql_switcher.readMemo(rset,field_name);
				}
				else if("D".equalsIgnoreCase(field_type))
				{
					/**yyyy-MM-dd*/
					value=PubFunc.FormatDate(rset.getDate(field_name));
				    //value=utilBo.getFormatDate(value,Integer.parseInt(fieldformat.getSlop())+6);

				}
				else if("N".equalsIgnoreCase(field_type))
				{
					int ndec=item.getDecimalwidth();//小数点位数
					value=PubFunc.DoFormatDecimal(rset.getString(field_name),Integer.parseInt(fieldformat.getSlop()));
				}
				else //'A'
				{
					String codevalue=rset.getString(field_name);
					value=((codevalue==null)?"":codevalue.trim());
					String codesetid = item.getCodesetid();
					if(!"".equals(value)){
						try {
							String sql = "select codeitemdesc from codeitem where codesetid='"+codesetid+"' and codeitemid='"+value+"'";
							RowSet itemset=dao.search(sql); 
							while(itemset.next()){
								String codeitemdesc=itemset.getString("codeitemdesc");
								value = value +"`"+codeitemdesc;
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
				map.put("columname", field_name);
				map.put("columnvalue", value);
  			    valuelist.add(map);				
			  }//for i loop end.
			  
			  //当包含附件的时候，需要根据GuidKey的值将附件的信息查询出来 liuzy 20151028
			  if(flagGuidKey){
			      String GuidKey=rset.getString("GuidKey");
				  ContentDAO dao=new ContentDAO(this.conn);
					try {
						String sql="select id,filename,path,class,srcfilename,topic,ext from hr_multimedia_file where childguid='"+GuidKey+"'";
						RowSet flagset=dao.search(sql); 
						int m=0;
						String valuestr="";
						String valuestr_en = "";
						while(flagset.next()){
							m++;
							String id=flagset.getString("id");                   //文件唯一标识 
							String filename=flagset.getString("filename");       //编码后文件名
							String path= rootDir + flagset.getString("path").replace("\\", File.separator);               //文件上传路径
							String srcfilename=flagset.getString("topic")+flagset.getString("ext"); //原始文件名 
							String filetype = flagset.getString("class");//文件类型
					        File f= new File(path,filename);  
					        long s=0;
					        if (f.exists()) {
					        	FileInputStream fis = null;
					            try {
						            fis = new FileInputStream(f);
						            s= fis.available();
								} finally {
									PubFunc.closeResource(fis);
								}
					        } else {
					            //f.createNewFile();
					        }
					        DecimalFormat df = new DecimalFormat("#0.00");
					        String fileSizeString = "";
					        //if (s < 1024) {
					        //    fileSizeString = df.format((double) s) + "B";
					        //} else 
					        if (s < 1048576) {
					            fileSizeString = df.format((double) s / 1024) + "K";
					        } else if (s < 1073741824) {
					            fileSizeString = df.format((double) s / 1048576) + "M";
					        } 
					        //else {
					           // fileSizeString = df.format((double) s / 1073741824) +"G";
					       // }
							String text=filename+"|"+path+"|"+srcfilename+"|"+fileSizeString+"|"+id+"|"+m +"|type:"+filetype;
							String text_en = PubFunc.encrypt(filename)+"|"+PubFunc.encrypt(path)+"|"+srcfilename+"|"+fileSizeString+"|"+id+"|"+m +"|type:"+filetype;
							valuestr += text+",";
							valuestr_en += text_en+",";
						}
						if(valuestr.length()>0){
							valuestr=valuestr.substring(0, valuestr.length()-1);
							valuestr = this.saveSubAttachmentToTemplate(valuestr);//将子集附件保存到模板临时目录 lis 20160912 add
						}
						if(valuestr_en.length()>0)
							valuestr_en=valuestr_en.substring(0, valuestr_en.length()-1);
						maps = new HashMap();
						maps.put("columname", "attach");
						maps.put("columnvalue", valuestr_en);
						valuelist.add(maps);
					} catch (SQLException e) {
						e.printStackTrace();
					}
			  }
			  reclist.add(valuelist);
			}
			String midi9999 = "";
			if(datarowcount>0){//设定了指定行数
				int size = i9999list.size();
				if(size>datarowcount){//存在的大于指定的
					midi9999 = (String)i9999list.get(size-datarowcount);
				}else{
					
				}
			}
			if(!"".equals(midi9999)){
				for(int i=0;i<reclist.size();i++){
					ArrayList valuelist = (ArrayList)reclist.get(i);
					HashMap i9999map = new HashMap();
					String i9999 = "";
					for(int j=0;j<valuelist.size();j++){
						i9999map= (HashMap)valuelist.get(j);
						String columname = (String)i9999map.get("columname");
						if("i9999".equalsIgnoreCase(columname)){
							i9999 = (String)i9999map.get("columnvalue");
							break;
						}
					}
					if(Integer.parseInt(i9999)>=Integer.parseInt(midi9999))
						reclist_.add(valuelist);
				}
			}else
				reclist_ = reclist;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return reclist_;
	}
	public StringBuffer getA0100String(ArrayList a0100s) {
		StringBuffer stra0100=new StringBuffer();
		for(int i=0;i<a0100s.size();i++)
		{
			if(i!=0)
				stra0100.append(",");
			stra0100.append("'");
			stra0100.append(((String)a0100s.get(i)).trim());
			stra0100.append("'");
		}
		return stra0100;
	}
	
	/**
	 * 把临时变量增加到薪资表中去。
	 */
	public void addMidVarIntoGzTable(String strWhere,Object[] dbarr,ArrayList fieldlist)throws GeneralException
	{
		try
		{
			RecordVo vo=new RecordVo(getBz_tablename());
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(getBz_tablename());
			String tablename="t#"+this.userView.getUserName()+"_gz"; //this.userView.getUserName()+"midtable";
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			boolean bflag=false;
			if(fieldlist.size()==0)
				return;
			
			HashMap columnMap=new HashMap();
			RowSet rowSet=dao.search("select * from "+getBz_tablename()+" where 1=2");
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
			if(rowSet!=null)
				rowSet.close();
			
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
				if(bflag)
					dbw.dropColumns(table);
				if(alterList.size()>0)
				{
					table=new Table(getBz_tablename());
					if(Sql_switcher.searchDbServer()!=2)  //不为oracle
					{
						    for(int i=0;i<alterList.size();i++)
									table.addField(((FieldItem)alterList.get(i)).cloneField());
							if(alterList.size()>0)
									dbw.alterColumns(table);
							 table.clear();
					 }
					 else
					 {
						 for(int i=0;i<alterList.size();i++)
						 {
							 FieldItem _item=(FieldItem)alterList.get(i);
							 alertColumn(getBz_tablename(),_item,dbw,dao);
						 }
					 }
				}
				DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel(getBz_tablename());					
			}
			table=new Table(getBz_tablename());
			vo=new RecordVo(getBz_tablename());  
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
				dbmodel.reloadTableModel(getBz_tablename());					
			}
			
			
			
			
			/**导入计算后的临时变量的值*/
			StringBuffer strFilter=new StringBuffer();
			String currym=ConstantParamter.getAppdate(this.userView.getUserName());
			String stry=currym.substring(0, 4);
			String strm=currym.substring(5, 7);
			String strc="1";
			YearMonthCount ymc=new YearMonthCount(Integer.parseInt(stry),Integer.parseInt(strm),Integer.parseInt(strc));
			
			if(this.paramBo.getInfor_type()==1)
			{
				/**按人员分库进行批量计算*/
				for(int i=0;i<dbarr.length;i++)
				{
					String dbpre=(String)dbarr[i];
					/**调入人员业务，不用计算变量*/
					if(dbpre==null||dbpre.length()==0)
						continue;
					for(int j=0;j<fieldlist.size();j++)
					{
						FieldItem item=(FieldItem)fieldlist.get(j);
						String fldtype=item.getItemtype();
						String fldname=item.getItemid();
						String formula= item.getFormula();
						String itemdesc=item.getItemdesc(); 
						if(formula==null||formula.trim().length()==0)
						{
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
						YksjParser yp = new YksjParser(this.userView, allUsedFields,
								YksjParser.forSearch, getDataType(fldtype), YksjParser.forPerson, "Ht", dbpre);
						
						yp.setStdTmpTable(getBz_tablename());
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
						String tmptable="t#"+this.userView.getUserName()+"_gz"; //this.userView.getUserName()+"midtable";
						if(createMidTable(usedlist,tmptable,"A0100"))
						{
							/**导入人员主集数据A0100,A0000,B0110,E0122,A0101*/
							buf.setLength(0);
							buf.append("insert into ");
							buf.append(tablename);
							buf.append("(A0000,A0100,B0110,E0122,A0101) select A0000,A0100,B0110,E0122,A0101 FROM ");
							buf.append(dbpre+"A01");
							buf.append(" where A0100 in (select A0100 from ");
							buf.append(getBz_tablename());
							if(strWhere.length()==0)
							{
								buf.append(" where basepre='");
								buf.append(dbpre);
								buf.append("'");
								/**计算临时变量的导入人员范围条件*/
								strFilter.append(" (select a0100 from ");
								strFilter.append(getBz_tablename());
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
								strFilter.append(getBz_tablename());
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
		                        dbw.updateRecord(tablename,this.paramBo.getBz_tablename(), tablename+".a0100"+"="+this.paramBo.getBz_tablename()+".a0100", 
		                                tablename+"."+item_m.getItemid()+"="+this.paramBo.getBz_tablename()+"."+item_m.getItemid(), 
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
								set_str.append(getBz_tablename()+"."+temp+"="+tablename+"."+temp);
								if(Sql_switcher.searchDbServer()==2)
									set_str.append("`");
								else
									set_str.append(",");
							}
							if(set_str.length()>0)
								set_str.setLength(set_str.length()-1);
							else
								continue;
							
							dao.update("update "+getBz_tablename()+" set "+set_st2.substring(1)+"   "+buf.toString());
							dbw.updateRecord(getBz_tablename(),tablename,getBz_tablename()+".A0100="+tablename+".A0100", set_str.toString(), strcond, strcond);
						}
						else
							dbw.updateRecord(getBz_tablename(),tablename,getBz_tablename()+".A0100="+tablename+".A0100", getBz_tablename()+"."+fldname+"="+tablename+".AAAAA", strcond, strcond);
					}//for j loop end.
				}//for i loop end.
			
			}
			else if(this.paramBo.getInfor_type()==2||this.paramBo.getInfor_type()==3)
			{
				
				for(int j=0;j<fieldlist.size();j++)
				{
					FieldItem item=(FieldItem)fieldlist.get(j);
					String fldtype=item.getItemtype();
					String fldname=item.getItemid();
					String formula= item.getFormula();
					if(formula==null||formula.trim().length()==0)
						continue;
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
					if(this.paramBo.getInfor_type()==3)
					{
						infoGroup=YksjParser.forPosition;
						keyName="E01A1";
						mainset="K01";
					}
					
					
					
					
					YksjParser yp = new YksjParser(this.userView, allUsedFields,
							YksjParser.forSearch, getDataType(fldtype),infoGroup, "Ht", "");
					yp.setStdTmpTable(getBz_tablename());
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
					String tmptable="t#"+this.userView.getUserName()+"_gz"; //this.userView.getUserName()+"midtable";
					if(createMidTable(usedlist,tmptable,keyName))
		 			{ 
						/**导入 主集数据 B0110 */
						buf.setLength(0);
						buf.append("insert into ");
						buf.append(tablename);
						buf.append("( "+keyName+") select "+keyName+" FROM ");
						buf.append(mainset);
						buf.append(" where "+keyName+" in (select "+keyName+" from ");
						buf.append(getBz_tablename());
						if(strWhere.length()==0)
						{
							/**计算临时变量的导入人员范围条件*/
							strFilter.append(" (select "+keyName+" from ");
							strFilter.append(getBz_tablename());
							strFilter.append(" )");							
						}
						else
						{
							buf.append(strWhere);
							/**计算临时变量的导入人员范围条件*/
							strFilter.append(" (select "+keyName+" from ");
							strFilter.append(getBz_tablename());
							strFilter.append(" ");
							strFilter.append(strWhere);
						    strFilter.append(" )");									
							
						}
						buf.append(")");
						dao.update(buf.toString());
				/*		for(int m=0;m<fieldlist.size();m++)//传递临时变量的值 wangrd 2014-04-09
						{
						    FieldItem item_m=(FieldItem)fieldlist.get(m);
						    dbw.updateRecord(tablename,this.paramBo.getBz_tablename(), tablename+"."+keyName+""+"="+this.paramBo.getBz_tablename()+"."+keyName, 
						            tablename+"."+item_m.getItemid()+"="+this.paramBo.getBz_tablename()+"."+item_m.getItemid(), 
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
					if(buf.toString().toUpperCase().startsWith("WHERE"))
						strcond=buf.substring(6);
					
					
					if(yp.isStatMultipleVar())
					{
						StringBuffer set_str=new StringBuffer("");
						StringBuffer set_st2=new StringBuffer("");
						for(int e=0;e<yp.getStatVarList().size();e++)
						{
							String temp=(String)yp.getStatVarList().get(e);
							set_st2.append(","+temp+"=null");
							set_str.append(getBz_tablename()+"."+temp+"="+tablename+"."+temp);
							if(Sql_switcher.searchDbServer()==2)
								set_str.append("`");
							else
								set_str.append(",");
						}
						if(set_str.length()>0)
							set_str.setLength(set_str.length()-1);
						else
							continue;
						 
						dao.update("update "+getBz_tablename()+" set "+set_st2.substring(1)+"   "+buf.toString());
						dbw.updateRecord(getBz_tablename(),tablename,getBz_tablename()+"."+keyName+"="+tablename+"."+keyName+"", set_str.toString(), strcond, strcond);
					}
					else
						//dbw.updateRecord(this.paramBo.getBz_tablename(),tablename,this.paramBo.getBz_tablename()+"."+keyName+"="+tablename+"."+keyName+"", this.paramBo.getBz_tablename()+"."+fldname+"="+tablename+".AAAAA", strcond, strcond);
					    dbw.updateRecord(getBz_tablename(),tablename,getBz_tablename()+"."+keyName+"="+tablename+"."+keyName+"",  getBz_tablename()+"."+fldname+"="+tablename+".AAAAA", strcond, strcond);
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
			
			if(dbw.isExistTable(tablename, false))
				dbw.dropTable(tablename);
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
			if(!bflag)
				dlist.add(fielditem);			
		}//for i loop end.
	}
	
	/**
	 * 初始设置使用字段列表
	 * @return
	 */
	private ArrayList initUsedFields()
	{
		ArrayList fieldlist=new ArrayList();
		
		if(this.paramBo.getInfor_type()==1)
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
			fielditem.setItemlength(50);
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
		else if(this.paramBo.getInfor_type()==2)
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
		else if(this.paramBo.getInfor_type()==3)
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
		if(changelast==null)
			changelast="";
		if(changepre==null)
			changepre="";
		String[] chglastarr=StringUtils.split(changelast,",");
		String[] chgprearr=StringUtils.split(changepre,",");
		int idx_l=0;
		int idx_p=0;
		buf.append("state=1");
		HashMap existFieldMap=new HashMap();
		for(int i=0;i<chglastarr.length;i++)
		{
			String expr=chglastarr[i];
			expr = expr.replace("，", ",");
			idx_l=expr.indexOf("=");
			if(idx_l==-1)
				continue;
			
			String fieldname=expr.substring(0,idx_l);
			String value=expr.substring(idx_l+1);
			FieldItem fielditem=DataDictionary.getFieldItem(fieldname);
			if(fielditem==null)
				continue;
			if("0".equals(this.paramBo.getImport_notice_data())) {
				fieldname=fieldname+"_1";
			}else {
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
						if(value.length()==0)
							buf.append("null");
						else
							buf.append(value);					
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
						buf.append(value.replace("'", "＇"));
						buf.append("'");
					}
				}
			}
		}
		for(int i=0;i<chgprearr.length;i++)
		{
			String expr=chgprearr[i];
			expr = expr.replace("，", ",");
			idx_p=expr.indexOf("=");
			if(idx_p==-1)
				continue;
			
			String fieldname=expr.substring(0,idx_p);
			String value=expr.substring(idx_p+1);
			FieldItem fielditem=DataDictionary.getFieldItem(fieldname);
			if(fielditem==null)
				continue;
			if("0".equals(this.paramBo.getImport_notice_data()))
				continue;
			fieldname=fieldname+"_1";
			if(vo.hasAttribute(fieldname.toLowerCase()))
			{
				if(existFieldMap.get(fieldname.toLowerCase())!=null)
				{
					continue;
				}
				existFieldMap.put(fieldname.toLowerCase(),"1");
				
				buf.append(",");
				buf.append(fieldname);
				buf.append("=");
				if(existFieldMap!=null&&existFieldMap.get(fieldname.toLowerCase())!=null){
					if(fielditem.isFloat()||fielditem.isInt())
					{
						if(value.length()==0)
							buf.append("null");
						else
							buf.append("'"+value+"'");					
					}
					else if(fielditem.isDate())
					{
						value=value.replaceAll("/","-");
						String str="^\\s*[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\s*(20|21|22|23|[0-1]\\d)?(:[0-5]\\d)?(:[0-5]\\d)?\\s*$";
						if(value.matches(str))
						{
							value=Sql_switcher.dateValue(value); 
							buf.append(value);
						}
						else
						{
							buf.append("'"+value+"'");
						}
				//		value=Sql_switcher.dateValue(value);  //2013-11-20 dengc 当日期型字段为按条件取值，字段类型则为M 
						//buf.append("'"+value+"'");
					}
					
					else//if(fielditem.isChar())
					{
						buf.append("'");
						buf.append(value.replace("'", "＇"));
						buf.append("'");
					}
				}else{
				if(fielditem.isFloat()||fielditem.isInt())
				{
					if(value.length()==0)
						buf.append("null");
					else{
						if(value.getBytes().length>fielditem.getItemlength()){
							String[] str =  value.split("`");
							if(str[0].length()!=0&&str[0].getBytes().length<=fielditem.getItemlength()){
								buf.append(str[0]);
							}else
							buf.append("null");
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
						String[] str =  _value.split("`");
						if(str[0].length()!=0&&str[0].replace("'", "").length()<=fielditem.getItemlength()){
							_value=Sql_switcher.dateValue(str[0]);
							buf.append(_value);
						}else
							buf.append("null");
					}else{
						buf.append(value);
					}
				}
				else//if(fielditem.isChar())
				{
					buf.append("'");
					if(value.getBytes().length>fielditem.getItemlength()){
						String[] str =  value.split("`");
						if(str[0].length()!=0&&str[0].getBytes().length<=fielditem.getItemlength()){
							buf.append(str[0]);
						}else
						buf.append("");
					}else{
					buf.append(value.replace("'", "＇"));
					}
					buf.append("'");
				}
				}
			}
		}
		return buf.toString();
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
				if(cname==null)
					continue;
				String sub_domain = Sql_switcher.readMemo(rset,"sub_domain");
				//获得sub_domain_id
				String sub_domain_id=""; 
				Document doc=null;
				if(sub_domain!=null&&sub_domain.trim().length()>0&&"1".equals(""+rset.getInt("ChgState"))){
					try{
						    doc=PubFunc.generateDom(sub_domain);
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
					if(this.paramBo.getInfor_type()==2)
						fielditem.setFieldsetid("B01");
					else if(this.paramBo.getInfor_type()==3)
						fielditem.setFieldsetid("K01");
					if("start_date".equalsIgnoreCase(cname))
						fielditem.setItemtype("D");
					else 
						fielditem.setItemtype("A");
					if("parentid".equalsIgnoreCase(cname))
						fielditem.setCodesetid("UM");
					else if("codesetid".equalsIgnoreCase(cname))
						fielditem.setCodesetid("orgType");
					else
						fielditem.setCodesetid("0");
					if(!"start_date".equalsIgnoreCase(cname))
						fielditem.setItemlength(50);
					fielditem.setUseflag("1");
				}
				else
				{	
					fielditem=DataDictionary.getFieldItem(cname, setname);//.getFieldItem(cname); 20150708 dengcan
						
				}
				/**未构库或指标体系不存在时则退出*/
				if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag()))
					continue; 
				if(nchgstate==2&&this.paramBo.getOpinion_field()!=null&&this.paramBo.getOpinion_field().length()>0&&this.paramBo.getOpinion_field().equalsIgnoreCase(fielditem.getItemid()))
				{
					continue;
				} 
				if(nchgstate==2)
					nhismode=1;
				 
				if("A".equalsIgnoreCase(setname.substring(0,1))&&("A01".equalsIgnoreCase(setname)||nhismode==1))
				{//如果是（人员信息集&&一条记录）||人员主集
					if(itemids.indexOf(field_name+"=")!=-1)
						continue;
					
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
			while(rset.next())
				dblist.add(rset.getString("basepre"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return dblist;
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
			if(this.paramBo.getInfor_type()==3)
				key="e01a1";
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
			strsql.append(this.tabId);
			strsql.append(" and T.setname=N.fieldsetid and N.useflag<>'0'");
			strsql.append(" and (T.chgstate=1 or T.chgstate=2) and (T.setname is not null) ");
			strsql.append(" order by T.setname");
			rset=dao.search(strsql.toString());
			while(rset.next())
			{
				setlist.add(rset.getString("setname"));
				if("K01".equalsIgnoreCase(rset.getString("setname")))
					isK01=true;
				if("B01".equalsIgnoreCase(rset.getString("setname")))
					isB01=true;
			}
			
			if(this.paramBo.getInfor_type()==2&&!isB01)
				setlist.add("B01");
			else if(this.paramBo.getInfor_type()==3&&!isK01)
				setlist.add("K01");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return setlist;
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
		if(this.paramBo.getOperationType()==0||this.paramBo.getOperationType()==5)
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
			strDesT = this.getBz_tablename();
			
			if(this.paramBo.getInfor_type()==2)
			{
				buf.setLength(0);
				buf.append("select b0110 from ");
				buf.append(strDesT);
			}
			else if(this.paramBo.getInfor_type()==3)
			{
				buf.setLength(0);
				buf.append("select e01a1 from ");
				buf.append(strDesT);
			}
			else {
			    buf.append("select basepre,a0100 from ");
	            buf.append(strDesT); 
			    
			}
			String whereInsId="";
			ArrayList insIdsList = new ArrayList();
            if (!"0".equals(this.taskId)){
                ArrayList taskList =getTasklist();
                String insIds="";
                for (int i=0;i<taskList.size();i++){
                    String task_id= (String)taskList.get(i);
                    String ins_id = this.utilBo.getInsId(task_id);
                    //判断ins_id是否已经结束
                    boolean isFinished = this.utilBo.isFinishedTask(ins_id);
                    if(!isFinished) {
                    	insIdsList.add(ins_id);
                        if (i==0)
                            insIds= ins_id;
                        else 
                            insIds= insIds+","+ins_id;
                    }
                }
                if(StringUtils.isBlank(insIds)) {
                	whereInsId = " 1=2";
                }else
                	whereInsId= " ins_id in ("+insIds+")";
                buf.append(" where "+ whereInsId);
            }
            else if("0".equals(this.taskId)&& "9".equals(this.moduleId)) //业务申请
            {
            	buf.append(" where a0100="+this.userView.getA0100()+" and lower(basepre)=lower('"+this.userView.getDbname()+"')");
            }
            if(this.paramBo.getInfor_type()==1){
                buf.append(" order by basepre");
            }
            
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				if(this.paramBo.getInfor_type()==1)
				{
					a0100=rset.getString("a0100");
					/**按人员库进行分类*/
					pre=rset.getString("basepre");
					/**人员库分空时，则按在职人员数据同步*/
					if(pre==null||pre.length()==0)
						pre="Usr";
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
				else if(this.paramBo.getInfor_type()==2||this.paramBo.getInfor_type()==3)
				{
					if(a0100list==null)
						a0100list=new ArrayList();
					if(rset.getString(1).charAt(0)=='B'||rset.getString(1).charAt(0)=='K')
						continue;
					a0100list.add(rset.getString(1));
				}		
			}
			if(this.paramBo.getInfor_type()==2||this.paramBo.getInfor_type()==3)
				hm.put("BK",a0100list);	
			
			Iterator iterator=hm.entrySet().iterator();
			while(iterator.hasNext())
			{
				Entry entry=(Entry)iterator.next();
				pre=entry.getKey().toString();
				a0100list =(ArrayList)entry.getValue();
				
				if(a0100list==null)
					continue;
				if(a0100list.size()<=500)
				{
					StringBuffer stra0100 = getA0100String(a0100list);				
					impDataFromArchive(stra0100.toString(),pre,1,insIdsList);
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
							if(j<a0100list.size())
								tempList.add((String)a0100list.get(j));
							else
								break;
						}
						if(tempList.size()>0)
						{
							StringBuffer stra0100 = getA0100String(tempList);				
							impDataFromArchive(stra0100.toString(),pre,1,insIdsList);
						}
						
					}
				}
				
			}	
			if(strDesT.equals(this.userView.getUserName()+"templet_"+this.tabId))
				impPreDataFromMessage(strDesT,"");
			//自动计算临时变量
			String where = "";
            Object[] dbarr = null;
            if (this.paramBo.getInfor_type() == 1) {
                String tablename = getBz_tablename();
                ArrayList dblist = searchDBPreList(tablename);
                dbarr = dblist.toArray();
                if (isSelfApply()) // 员工通过自助平台发动申请
                {
                    where = " where lower(" + tablename + ".basepre)='" + this.userView.getDbname().toLowerCase() + "'  and " + tablename + ".a0100='" + this.userView.getA0100() + "'";
                }
                else if(!"0".equals(this.taskId)){    
                	where = " where seqnum in  (select seqnum from  t_wf_task_objlink  where task_id in("+this.taskId+")  and tab_id="+this.tabId+" ) ";
		             
	            }
	            setOnlyComputeFieldVar(true);
	            ArrayList fieldlist = getMidVariableList();
	            addMidVarIntoGzTable(where, dbarr, fieldlist);
	            setOnlyComputeFieldVar(false);
	            setComputeVar(true);
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
			if(this.paramBo.getInfor_type()==2||this.paramBo.getInfor_type()==3)
				set_str=" set codeitemdesc_1=codeitemdesc_2";
			//liuyz bug27909
			if("0".equalsIgnoreCase(taskId))
			{
				if(isSelfApply())//员工通过自助平台发动申请
					strDesT="g_templet_"+this.tabId;
				else
					strDesT=this.userView.getUserName()+"templet_"+this.tabId;
			}
			else
			{
				strDesT="templet_"+this.tabId;
			}
			/*if(this.paramBo.isSp_syncArchiveData())//审批过程中同步档案库数据
				strDesT="templet_"+this.tabId;*/
			
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
				if(this.paramBo.getInfor_type()==1) 
				{
					dao.update("update "+strDesT+" set seqnum='"+seqnum+"' where a0100='"+rowSet.getString("a0100")+"' and  lower(basepre)='"+rowSet.getString("basepre").toLowerCase()+"'");
				}
				else if(this.paramBo.getInfor_type()==2)
				{
					dao.update("update "+strDesT+" set seqnum='"+seqnum+"' where b0110='"+rowSet.getString("b0110")+"'");
				}
				else if(this.paramBo.getInfor_type()==3)
				{
					dao.update("update "+strDesT+" set seqnum='"+seqnum+"' where E01A1='"+rowSet.getString("E01A1")+"'");
				}
			}
			}
			rowSet.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
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
						if(this.paramBo.getInfor_type()==1)
							sql.append(" lower(M.db_type)=lower("+tabname+".basepre) and "+tabname+".a0100=M.a0100 ");
						else if(this.paramBo.getInfor_type()==2)
							sql.append("   "+tabname+".B0110=M.B0110 ");
						else if(this.paramBo.getInfor_type()==3)
							sql.append("   "+tabname+".E01A1=M.B0110 ");
						sql.append(" and M.state=1 and M.noticetempid="+this.tabId);
						if(!this.userView.isSuper_admin()&& "1".equals(this.paramBo.getFilter_by_manage_priv()))
						{ 
								String operOrg = this.userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板  
								if(operOrg==null||!"UN`".equalsIgnoreCase(operOrg))
								{
									sql.append(" and ( ");
								 
									if(operOrg!=null && operOrg.length() >3)
									{
										StringBuffer tempSql = new StringBuffer(""); 
										String[] temp = operOrg.split("`");
										for (int j = 0; j < temp.length; j++) { 
											 if (temp[j]!=null&&temp[j].length()>0)
												tempSql.append(" or  M.b0110 like '" + temp[j].substring(2)+ "%'");				
										}
										if(tempSql.length()>0)
										{
											sql.append(tempSql.substring(3));
										}
										else
											sql.append(" M.b0110='##'");
									}
									else
										sql.append(" M.b0110='##'");
									
									sql.append(" or nullif(M.b0110,'') is null)");
								}
								
						}
						if(dbw.isExistField("tmessage", "receivetype", false)){
							sql.append(" and (nullif(M.username,'') is null or (lower(M.username)='"+userView.getUserName().toLowerCase()+"' and (M.receivetype='4' or nullif(M.receivetype,'') is null)) ");
							if(this.getRoleArr(userView).length()>0)
								sql.append(" or (M.username in("+this.getRoleArr(userView)+") and M.receivetype='2'))");
							else
								sql.append(" )");
						}else
							sql.append(" and (M.username is null or M.username='' or lower(M.username)='"+this.userView.getUserName().toLowerCase()+"')");

						if(this.paramBo.getTemplateStatic()==10) //单位管理
							sql.append(" and object_type=2 ");
						else if(this.paramBo.getTemplateStatic()==11) //职位管理
							sql.append(" and object_type=3 ");
						else
							sql.append(" and ( object_type is null or object_type=1 ) ");				
						sql.append(" and "+tabname+".state=1 ");//来源于消息
						if(where_str.length()>0)
							sql.append(" and "+where_str);
						RowSet rowSet=dao.search(sql.toString());
						ArrayList paralist=new ArrayList();
						while(rowSet.next())
						{
							String basepre=""; 
							String a0100=""; 
							String b0110=""; 
							if(this.paramBo.getInfor_type()==1)
							{
								basepre=rowSet.getString("basepre");
								a0100=rowSet.getString("a0100");
							}
							else
							{ 
								if(this.paramBo.getInfor_type()==2)
									b0110=rowSet.getString("b0110");
								else if(this.paramBo.getInfor_type()==3)
									b0110=rowSet.getString("e01a1");
							}
							String changepre=Sql_switcher.readMemo(rowSet, "changepre");
							
							sql.setLength(0);
							paralist.clear();
							sql.append("update ");
							sql.append(tabname);
							sql.append(" set ");
							
							String up_str=getChgUpdateSQL(changepre,tabname);
							if(up_str.length()==0)
								continue;
							sql.append(up_str.substring(1));
							if(this.paramBo.getInfor_type()==1)
								sql.append(" where a0100=?  and lower(basepre)=?  ");
							else if(this.paramBo.getInfor_type()==2)
								sql.append(" where b0110=? ");
							else if(this.paramBo.getInfor_type()==3)
								sql.append(" where e01a1=? ");
							sql.append(" and state=1 ");
							if(where_str.length()>0)
								sql.append(" and "+where_str);
							if(this.paramBo.getInfor_type()==1)
							{
								paralist.add(a0100);
								paralist.add(basepre.toLowerCase());
							}
							else if(this.paramBo.getInfor_type()==2||this.paramBo.getInfor_type()==3)
							{
								paralist.add(b0110);
							}
							dao.update(sql.toString(),paralist);
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
				buf.append("select count(*) as nmax from tmessage where state=1 and noticetempid="+this.tabId); 
				//Object_type	 对象类型	Int	   1:人员  2:单位  3：职位
				if(this.paramBo.getTemplateStatic()==10) //单位管理 
					buf.append(" and object_type=2 "); 
				else if(this.paramBo.getTemplateStatic()==11) //职位管理 
					buf.append(" and object_type=3 "); 
				else 
					buf.append(" and ( object_type is null or object_type=1 ) "); 
				RowSet rset=dao.search(buf.toString());
				int nrec=0;
				if(rset.next())
					nrec=rset.getInt("nmax");
				if(nrec>0)
				{ 
					StringBuffer sql=new StringBuffer("select "+tabname+".*,M.changepre from "+tabname+" ,tmessage M where ");
					if(this.paramBo.getInfor_type()==1)
						sql.append(" lower(M.db_type)=lower("+tabname+".basepre) and "+tabname+".a0100=M.a0100 ");
					else if(this.paramBo.getInfor_type()==2)
						sql.append("   "+tabname+".B0110=M.B0110 ");
					else if(this.paramBo.getInfor_type()==3)
						sql.append("   "+tabname+".E01A1=M.B0110 ");
					sql.append(" and M.state=1 and M.noticetempid="+this.tabId);  
					if(this.paramBo.getTemplateStatic()==10) //单位管理
						sql.append(" and object_type=2 ");
					else if(this.paramBo.getTemplateStatic()==11) //职位管理
						sql.append(" and object_type=3 ");
					else
						sql.append(" and ( object_type is null or object_type=1 ) ");				
					sql.append(" and "+tabname+".state=1 ");//来源于消息
					if(where_str.length()>0)
						sql.append(" and "+where_str);
					RowSet rowSet=dao.search(sql.toString());
					ArrayList paralist=new ArrayList();
					while(rowSet.next())
					{
						String basepre=""; 
						String a0100=""; 
						String b0110=""; 
						if(this.paramBo.getInfor_type()==1)
						{
							basepre=rowSet.getString("basepre");
							a0100=rowSet.getString("a0100");
						}
						else
						{ 
							if(this.paramBo.getInfor_type()==2)
								b0110=rowSet.getString("b0110");
							else if(this.paramBo.getInfor_type()==3)
								b0110=rowSet.getString("e01a1");
						}
						String changepre=Sql_switcher.readMemo(rowSet, "changepre");
						
						sql.setLength(0);
						paralist.clear();
						sql.append("update ");
						sql.append(tabname);
						sql.append(" set ");
						
						String up_str=getChgUpdateSQL(changepre,tabname);
						if(up_str.length()==0)
							continue;
						sql.append(up_str.substring(1));
						if(this.paramBo.getInfor_type()==1)
							sql.append(" where a0100=?  and lower(basepre)=?  ");
						else if(this.paramBo.getInfor_type()==2)
							sql.append(" where b0110=? ");
						else if(this.paramBo.getInfor_type()==3)
							sql.append(" where e01a1=? ");
						sql.append(" and state=1 ");
						if(where_str.length()>0)
							sql.append(" and "+where_str);
						if(this.paramBo.getInfor_type()==1)
						{
							paralist.add(a0100);
							paralist.add(basepre.toLowerCase());
						}
						else if(this.paramBo.getInfor_type()==2||this.paramBo.getInfor_type()==3)
						{
							paralist.add(b0110);
						}
						dao.update(sql.toString(),paralist);
					} 
					
				}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
		HashMap field_name_map=getBigMemoMap();
		for(int i=0;i<chglastarr.length;i++)
		{
			String expr=chglastarr[i];
			expr = expr.replace("，", ",");
			idx=expr.indexOf("=");
			if(idx==-1)
				continue;
			
			String fieldname=expr.substring(0,idx);
			String value=expr.substring(idx+1);
			FieldItem fielditem=DataDictionary.getFieldItem(fieldname);
			if(fielditem==null)
				continue;
			if("0".equals(this.paramBo.getImport_notice_data()))
				continue;
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
				if(field_name_map!=null&&field_name_map.get(fieldname.toLowerCase())!=null){
					if(fielditem.isFloat()||fielditem.isInt())
					{
						if(value.length()==0)
							buf.append("null");
						else
							buf.append("'"+value+"'");					
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
					if(value.length()==0)
						buf.append("null");
					else{
						if(value.getBytes().length>fielditem.getItemlength()){
							String[] str =  value.split("`");
							if(str[0].length()!=0&&str[0].getBytes().length<=fielditem.getItemlength()){
								buf.append(str[0]);
							}else
							buf.append("null");
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
						String[] str =  _value.split("`");
						if(str[0].length()!=0&&str[0].replace("'", "").length()<=fielditem.getItemlength()){
							_value=Sql_switcher.dateValue(str[0]);
							buf.append(_value);
						}else
							buf.append("null");
					}else{
						buf.append(value);
					}
				}
				else//if(fielditem.isChar())
				{
					//
					buf.append("'");
					if(value.getBytes().length>fielditem.getItemlength()){
						String[] str =  value.split("`");
						if(str[0].length()!=0&&str[0].getBytes().length<=fielditem.getItemlength()){
							buf.append(str[0]);
						}else
						buf.append("");
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
			buf.append("select count(*) as nmax from tmessage where state="+state+" and noticetempid=");
			buf.append(this.tabId);
			if(!this.userView.isSuper_admin()&& "1".equals(this.paramBo.getFilter_by_manage_priv()))
			{ 
				String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板  
				if(operOrg==null||!"UN`".equalsIgnoreCase(operOrg))
				{
					buf.append(" and ( "); 
					if(operOrg!=null && operOrg.length() >3)
					{
						StringBuffer tempSql = new StringBuffer(""); 
						String[] temp = operOrg.split("`");
						for (int j = 0; j < temp.length; j++) { 
							 if (temp[j]!=null&&temp[j].length()>0)
								tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2)+ "%'");				
						}
						if(tempSql.length()>0)
						{
							buf.append(tempSql.substring(3));
						}
						else
							buf.append(" tmessage.b0110='##'");
					}
					else
						buf.append(" tmessage.b0110='##'");
					
					buf.append(" or nullif(tmessage.b0110,'') is null)");
				}
			}
			if(dbw.isExistField("tmessage", "receivetype", false)){
				buf.append(" and (nullif(username,'') is null or (lower(username)='"+userView.getUserName().toLowerCase()+"' and (receivetype='4' or nullif(receivetype,'') is null)) ");
				if(this.getRoleArr(userView).length()>0)
					buf.append(" or (username in("+this.getRoleArr(userView)+") and receivetype='2'))");
				else
					buf.append(" )");
			}else
				buf.append(" and ( nullif(username,'') is null  or lower(username)='"+userView.getUserName().toLowerCase()+"')");
			//Object_type	 对象类型	Int	   1:人员  2:单位  3：职位
			if(this.paramBo.getTemplateStatic()==10) //单位管理
			{
				buf.append(" and object_type=2 ");
			}
			else if(this.paramBo.getTemplateStatic()==11) //职位管理
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
			if(rset.next())
				nrec=rset.getInt("nmax");
			if(nrec!=0)
				bflag=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
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
		String tablename="templet_"+this.tabId; //"templet_"+this.tabId
		RowSet rowSet=null;
		try
		{
			DbWizard dbwizard=new DbWizard(this.conn);
			if(!dbwizard.isExistTable("templet_"+this.tabId,false))
			{
				Table table_wf=new Table("templet_"+this.tabId);
				addFieldItem(table_wf,1);		
				dbwizard.createTable(table_wf);
				DbSecurityImpl dbS = new DbSecurityImpl();
				dbS.encryptTableName(this.conn, "templet_"+this.tabId);
			}
			else
			{
			    ArrayList fieldList=this.getAllFieldItem();
				Table table_wf=new Table("templet_"+this.tabId); 
				updateTempTemplateStruct(table_wf,1,fieldList); 
				syncGzField("templet_"+this.tabId);
				 
			}
			
			String index_name=tablename+"_idx1";
			ArrayList valueList=new ArrayList();
			valueList.add(index_name.toUpperCase());
			String validateSql1=""; 
			switch (Sql_switcher.searchDbServer()) {
			case 1: // MSSQL 
				validateSql1="select count(*) from sys.indexes where upper(name)=?"; 
				break;
			case 2:// oracle
				validateSql1="  SELECT  count(*)  FROM user_indexes WHERE  upper(INDEX_NAME)=? and  upper(TABLE_NAME)=? ";
				valueList.add(tablename.toUpperCase());
				break;
			}
			int nn=0;
			rowSet=dao.search(validateSql1,valueList);
			if(rowSet.next())
				nn=rowSet.getInt(1);
			if(nn==0)
			{
				dao.update("create index "+index_name.toUpperCase()+" on "+tablename+" (SEQNUM,INS_ID) ");
			} 
			/**重新加载数据模型*/
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tablename);
			dbmodel.reloadTableModel("templet_"+this.tabId);
			 
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
		return bflag;
		
	}
	

	/**
	 * 人员调入模板|新增组织单元模板
	 * @param tablename 
	 */
	public void autoAddRecord(String tablename)throws GeneralException
	{
		ContentDAO dao=null;
		RowSet rset = null;
		RowSet rowSet = null;
		try
		{
			if(!(this.paramBo.getOperationType()==0||this.paramBo.getOperationType()==5))
				return;
            dao=new ContentDAO(this.conn);
            
			StringBuffer buf=new StringBuffer();
			buf.append("select count(*) as nrec from "); 
			buf.append(tablename);
			rset=dao.search(buf.toString());
			int irow=0;
			if(rset.next())
				irow=rset.getInt("nrec");
			if(irow!=0&&!"1".equals(this.userView.getHm().get("fillInfo")))
				return;
			String a0100=null;
			RecordVo vo=new RecordVo(tablename);
            IDGenerator idg=new IDGenerator(2,this.conn);

			/**
			 * 查找变化前的历史记录单元格
			 * 保存时把这部分单元格的内容
			 * 过滤掉，不作处理
			 * */            
            HashMap sub_map=getHisModeSubCell();
            if("1".equals(this.userView.getHm().get("fillInfo")))
            	a0100 = this.userView.getA0100();
            else
            	a0100=  idg.getId("rsbd.a0100");
			if(this.paramBo.getInfor_type()==2||this.paramBo.getInfor_type()==3)
				a0100="B"+a0100;
			
		/*	if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				if(tablebo.getDest_base()==null||tablebo.getDest_base().length()==0)
					throw new GeneralException("人员调入业务模板未定义目标库!");
				vo.setString("basepre",tablebo.getDest_base());
			}
			else	
				vo.setString("basepre","");*/
			if(this.paramBo.getInfor_type()==1&&(this.paramBo.getDest_base()==null||this.paramBo.getDest_base().length()==0))
				throw new GeneralException("人员调入业务模板未定义目标库!");
			ArrayList dbList=DataDictionary.getDbpreList();
			if(this.paramBo.getInfor_type()==1)
			{
				vo.setString("a0100",a0100); 
				String dbpre=this.paramBo.getDest_base();
				for(int i=0;i<dbList.size();i++)
				{
					String pre=(String)dbList.get(i);
					if(pre.equalsIgnoreCase(this.paramBo.getDest_base()))
						dbpre=pre;
				}
				if("1".equals(this.userView.getHm().get("fillInfo")))
					dbpre = this.userView.getDbname();
				vo.setString("basepre",dbpre);
				if(vo.hasAttribute("a0101_2"))
				{
					if("1".equals(this.userView.getHm().get("fillInfo")))
						vo.setString("a0101_2", "");
					else
						vo.setString("a0101_2", "--");
				}
				if(vo.hasAttribute("a0101_1"))
				{
					if("1".equals(this.userView.getHm().get("fillInfo")))
						vo.setString("a0101_1", "");
					else
						vo.setString("a0101_1", "--");
				}
				if(vo.hasAttribute("create_time")&&"1".equals(this.userView.getHm().get("fillInfo")))
				{
					vo.setDate("create_time", new Date());
				}
			}
			else
			{
				if(this.paramBo.getInfor_type()==2)
					vo.setString("b0110",a0100);
				if(this.paramBo.getInfor_type()==3)
					vo.setString("e01a1",a0100);
				
				
				if(vo.hasAttribute("codeitemdesc_2"))
				{
					vo.setString("codeitemdesc_2", "--");
				}
				if(vo.hasAttribute("codeitemdesc_1"))
				{
					vo.setString("codeitemdesc_1", "--");
				}
			}
			Iterator iterator=sub_map.entrySet().iterator();
			while(iterator.hasNext())
			{
				Entry entry=(Entry)iterator.next();
				String field_name=entry.getKey().toString();
				TemplateSet setbo =(TemplateSet)entry.getValue();
				
				//根据field_name的值，判断对应的子集是否存在，若不存在抛出异常 20150824 liuzy
				String text_name=setbo.getHz();
				text_name=text_name.substring(text_name.indexOf("{")+1, text_name.lastIndexOf("}"));
				String name=field_name.substring(field_name.indexOf("_")+1, field_name.lastIndexOf("_"));   
				if(!"".equals(name)&&name!=null)
				{
				    FieldSet set = DataDictionary.getFieldSetVo(name.toLowerCase());
				    if(set==null){
				    	throw GeneralExceptionHandler.Handle(new Exception(text_name+ ResourceFactory.getProperty("system.param.sysinfosort.subset.undiscovered")));
				    }
				}
				
				TSubSetDomain setdomain=new TSubSetDomain(setbo.getXml_param());
				String xml=setdomain.outContentxml();
				vo.setString(field_name.toLowerCase(), xml);
			}
			 ArrayList allTemplateItem = this.getAllTemplateItem();
	            for(int i=0;i<allTemplateItem.size();i++){
	            	TemplateItem item = (TemplateItem) allTemplateItem.get(i);
	            	TemplateSet setBo = item.getCellBo();
	            	if(StringUtils.isNotBlank(setBo.getDefaultValue())&&!setBo.isSubflag()&&setBo.getChgstate()==2){
	            		String id = setBo.getSub_domain_id();
						if(id!=null&&id.trim().length()>0){
							id = "_"+id;
						}else{
							id="";
						}
						String defaultValue= setBo.getDefaultValue();
						if("D".equalsIgnoreCase(setBo.getOld_fieldType())){
							Date date=null;
							if(StringUtils.isNotBlank(defaultValue)){
								if("SYSTIME".equalsIgnoreCase(setBo.getDefaultValue())){
									 date=new Date();
								}else{
									defaultValue=defaultValue.replace("\\", "-").replace("/", "-").replace(".", "-").replace("年", "-").replace("月", "-").replace("日", " ").replace("时", ":").replace("分", ":").replace("秒", "");
									String format="yyyy-MM-dd";
									if(defaultValue!=null&&defaultValue.indexOf(":")!=-1){
										format="yyyy-MM-dd HH:mm:ss";
									}
									date=DateUtils.getSqlDate(defaultValue,format);
								}
								vo.setDate(setBo.getField_name().toLowerCase()+id+"_"+setBo.getChgstate(),date);
							}
	            		}else if("N".equalsIgnoreCase(setBo.getOld_fieldType())){
	            			int ndec=item.getFieldItem().getDecimalwidth();//小数点位数  
	    					String value=PubFunc.DoFormatDecimal(defaultValue,ndec);
	            			vo.setString(setBo.getField_name().toLowerCase()+id+"_"+setBo.getChgstate(),  value);
	            		}else{
	            			if(this.paramBo.getInfor_type()!=1&&this.paramBo.getOperationType()==5
	            					&&"defMngOrg".equalsIgnoreCase(defaultValue)) {//默认值是管理机构，需要得到权限下的机构，按顺序取最大范围那个
	            				String orgCode = this.userView.getUnitIdByBusi("4");
	            				String orgid = "";
	            				if("UN`".equalsIgnoreCase(orgCode)){//UN`=全部 需要查到最顶层的UN节点
	            					String sql = "select codeitemid from organization where grade=1 and codesetid='UN'";
	            					rset = dao.search(sql);
	            					while(rset.next()) {
	            						String codeitemid = rset.getString("codeitemid");
	            						orgid += "UN"+codeitemid+"`";
	            					}
	            					orgCode = orgid;
	            				}
	            				String[] orgArr = StringUtils.split(orgCode,"`");
	            				String value = "";
	            				for(int j=0;j<orgArr.length;j++) {
	            					String value_ = orgArr[j].substring(2);
	            					if(j==0) {
	            						value = value_;
	            					}
	            					if(value.length()>value_.length()) {
	            						value = value_;
	            					}
	            				}
	            				vo.setString(setBo.getField_name().toLowerCase()+id+"_"+setBo.getChgstate(),  value);
	            			}else {
	            				vo.setString(setBo.getField_name().toLowerCase()+id+"_"+setBo.getChgstate(),  defaultValue);
	            			}
	            		}
					}
	            }
			rowSet=dao.search("select "+Sql_switcher.isnull("max(a0000)","0")+"+1 from "+tablename);
			if(rowSet.next())
				vo.setInt("a0000", rowSet.getInt(1));
			
			String seqnum=CreateSequence.getUUID();
			vo.setString("seqnum", seqnum);
            dao.addValueObject(vo);
  
      
			if("1".equals(this.paramBo.getId_gen_manual())){
			            	
            }else{
            	filloutSequence(a0100, "1".equals(this.userView.getHm().get("fillInfo"))?this.userView.getDbname():this.paramBo.getDest_base(), tablename,"0");     
            }
  
            /**生成序号*/

	    }
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally {
			PubFunc.closeDbObj(rset);
			PubFunc.closeDbObj(rowSet);
		}		
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
            if("0".equals(this.paramBo.getUnique_check()))
                return info;
            ContentDAO dao=new ContentDAO(this.conn);
            String sql="";
            if(isSelfApply())//员工通过自助平台发动申请
            {
                sql="select count(task_id) from t_wf_task where task_id in ( select task_id from t_wf_task_objlink where seqnum in (select seqnum from templet_"+this.tabId+" where  a0100='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"' ) ) and (  (task_state<>'5' and task_state<>'4') or task_state is null) and "+Sql_switcher.isnull("bs_flag", "1")+"=1 ";
            }
            else 
            {
                if(this.paramBo.getInfor_type()==1)
                {
                    sql="select count(task_id) from t_wf_task where task_id in ( select task_id from t_wf_task_objlink where state<>3 and seqnum in (select seqnum from templet_"+this.tabId+" where ";
                    sql+=" exists (select null from "+this.userView.getUserName()+"templet_"+this.tabId+" where  submitflag=1 and   lower("+this.userView.getUserName()+"templet_"+this.tabId+".basepre)=lower(templet_"+this.tabId+".basepre) and "+this.userView.getUserName()+"templet_"+this.tabId+".a0100=templet_"+this.tabId+".a0100  ) ";               
                    sql+=") ) and ( (task_state<>'5' and task_state<>'4') or task_state is null)  and "+Sql_switcher.isnull("bs_flag", "1")+"=1 ";
                    sql+="and ins_id not in (select ins_id from T_WF_INSTANCE  where T_WF_INSTANCE.ins_id=t_wf_task.INS_ID and tabid="+this.tabId+" and FINISHED='5' )";  //添加过滤条件，将可能存在的脏数据排除掉
                }
                else
                {
                    String key="b0110";
                    if(this.paramBo.getInfor_type()==3)
                        key="e01a1";
                    
                    sql="select count(task_id) from t_wf_task where task_id in ( select task_id from t_wf_task_objlink where state<>3 and  seqnum in ( select seqnum from templet_"+this.tabId+" where ";
                    sql+=" exists (select null from "+this.userView.getUserName()+"templet_"+this.tabId+" where submitflag=1 and   "+this.userView.getUserName()+"templet_"+this.tabId+"."+key+"=templet_"+this.tabId+"."+key+"  ) ";               
                    sql+=" ) ) and ( (task_state<>'5' and task_state<>'4')  or task_state is null) and "+Sql_switcher.isnull("bs_flag", "1")+"=1 ";
                    sql+="and ins_id not in (select ins_id from T_WF_INSTANCE  where T_WF_INSTANCE.ins_id=t_wf_task.INS_ID and tabid="+this.tabId+" and FINISHED='5' )";  //添加过滤条件，将可能存在的脏数据排除掉
                }
            }
            RowSet rowSet=dao.search(sql);
            if(rowSet.next())
            {
                if(rowSet.getInt(1)>0)
                {
                    if(isSelfApply())//员工通过自助平台发动申请
                        info="您已提交，不允许重复提交!";//农大修改提示
                    else
                    {
                        String tabName="templet_"+this.tabId;
                        String tabName2=this.userView.getUserName()+"templet_"+this.tabId;
                        
                        sql="";
                        
                        if(this.paramBo.getInfor_type()==1)
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
                            if(this.paramBo.getInfor_type()==3)
                                key="e01a1";
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
                        info=info_str.substring(1)+" 的单据您已提交，不允许重复提交!";//农大修改提示语句
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
    
	/**校验单据是否被处理过及被其他人锁定
	 * @param ins_id
	 * @param task_id
	 * @param self
	 * @return
	 */
	public String checkDealTaskInformation(String task_ids,String self){
		String errorinfo="";
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			PendingTask imip=new PendingTask();
			String timeoutUration = SystemConfig.getPropertyValue("timeoutUration");
			if(StringUtils.isBlank(timeoutUration))
				timeoutUration = "30";
			if(StringUtils.isNotBlank(timeoutUration)&&Integer.parseInt(timeoutUration)<10)
				timeoutUration = "10";
			String taskids="";
			String[] lists=StringUtils.split(task_ids,",");
			for(int i=0;i<lists.length;i++)
			{
				taskids+=","+lists[i];
			}
			String sql ="";
			if("2".equals(self)){   // 当自定义审批流程时，应查询t_wf_node_manual表中的数据，liuzy 20150813
				sql="select t.ins_id,t.task_id,t.task_topic,t.node_id,t.actorid,t.actor_type,t.actorname,t.start_date,m.tabid from t_wf_task t,t_wf_node_manual m where t.ins_id=m.ins_id and t.node_id = m.id and t.task_state='3' and t.task_id in ("+taskids.substring(1)+" ) ";
			}else{
				sql ="select ins_id,task_id,task_topic,t_wf_task.node_id,actorid,actor_type,actorname,start_date,t_wf_node.tabid from t_wf_task,t_wf_node where t_wf_task.node_id=t_wf_node.node_id and task_state='3'  and t_wf_task.task_id in ("+taskids.substring(1)+")";
			}
			RowSet  frowset=dao.search(sql);
			RowSet  frowset2=null;
			String actor_type ="";
			String node_id="";
			String tabid ="";
			String task_id="";
			int j=0;
			//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
			ArrayList usernameList=PubFunc.SearchOperUserOrSelfUserName(this.userView);
			while(frowset.next()){
				j++;
				actor_type = frowset.getString("actor_type");
				node_id = frowset.getString("node_id");
				tabid= frowset.getString("tabid");
				task_id=frowset.getString("task_id");
				String actor_id =frowset.getString("actorid");
				/*
				if(j==0){
					sql ="select t_wf_task.actor_type,t_wf_instance.tabid from t_wf_task,t_wf_instance where t_wf_task.ins_id = t_wf_instance.ins_id and  t_wf_task.task_state='3'  and t_wf_task.task_id="+task_id;
					frowset=dao.search(sql);
					if(frowset.next()){//流程修改过
						actor_type = frowset.getString("actor_type");
						node_id = "0";
						tabid= frowset.getString("tabid");
					}
					else{
						return "该单据已处理！";
					}
				}
				else */
				{
					LazyDynaBean abean=null;
					HashMap dataMap=new HashMap();
					if("5".equals(actor_type))//本人
					{
						
						String sql0="select twt.* from  t_wf_task_objlink twt,templet_"+tabid+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
						sql0+=" and twt.task_id="+task_id+" and "+Sql_switcher.isnull("twt.state","0")+"=0 and tt.a0100='"+userView.getA0100()+"' and lower(tt.basepre)='"+userView.getDbname().toLowerCase()+"'";
						frowset2=dao.search(sql0);
						
						while(frowset2.next())
						{
							
							if(dataMap.get(node_id+task_id)==null)
							{
								dao.update("update t_wf_task_objlink set special_node=1 where task_id="+task_id+" and node_id="+node_id);
								dataMap.put(node_id+task_id,"1");
							}
							
							String username=frowset2.getString("username");
							if(username==null||username.trim().length()==0)
								dao.update("update t_wf_task_objlink set username='"+userView.getDbname().toUpperCase()+userView.getA0100()+"' where seqnum='"+frowset2.getString("seqnum")+"' and task_id="+frowset2.getString("task_id"));
							
						}
						 
					}
					else if("2".equals(actor_type)){//角色

						String scope_field="";
						String containUnderOrg="0";
						frowset2=dao.search("select * from t_wf_node where tabid="+tabid+" and node_id="+node_id);
						String ext_param="";
						Document doc=null;
						Element element=null;
						if(frowset2.next())
							ext_param=Sql_switcher.readMemo(frowset2,"ext_param"); 
						if(ext_param!=null&&ext_param.trim().length()>0)
						{
							doc=PubFunc.generateDom(ext_param); 
							String xpath="/params/scope_field";
							XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							List childlist=findPath.selectNodes(doc);
							if(childlist.size()==0){
								xpath="/param/scope_field";
								 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
								 childlist=findPath.selectNodes(doc);
							}
							if(childlist!=null&&childlist.size()>0)
							{
								for(int i=0;i<childlist.size();i++)
								{
									element=(Element)childlist.get(i);
									if(element!=null&&element.getText()!=null&&element.getText().trim().length()>0)
									{
										scope_field=element.getText().trim();
										if(element.getAttribute("flag")!=null&& "1".equals(element.getAttributeValue("flag").trim()))
											containUnderOrg="1";
									}
								}
							}
						}
						//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
						
						if(scope_field.length()>0)
						{
							String sql0 = "select twt.* from  t_wf_task_objlink twt,templet_"+tabid+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
							sql0 += " and twt.task_id="+task_id+"  and "+Sql_switcher.isnull("twt.state","0")+"=0  and (( "+Sql_switcher.isnull("twt.username","' '")+"=' ' or twt.username='"+userView.getUserName()+"'  ";
							//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
							if(usernameList.size()>0){
								for(int i=0;i<usernameList.size();i++){
									sql0+=" or username='"+usernameList.get(i)+"' ";
								}
							}	
							sql0 += " ) ";
							sql0 += " or (("+Sql_switcher.diffMinute(Sql_switcher.sqlNow() ,"twt.locked_time")+">="+Integer.parseInt(timeoutUration)+" or twt.locked_time is null) and ";
							sql0 += " ( "+Sql_switcher.isnull("twt.username","' '")+"=' ' or twt.username<>'"+userView.getUserName()+"'   ))) ";
							//sql0+=" and twt.task_id="+task_id+" and "+Sql_switcher.isnull("twt.state","0")+"=0  and ( "+Sql_switcher.isnull("username","' '")+"=' ' or username='"+userView.getUserName()+"' ) ";
							{
								String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板
								//如果角色特征为单位领导或部门领导，则根据直接根据角色特征过滤一下 不走业务范围 1：部门领导 6：单位领导 
								if (actor_id!=null && actor_id.length()>0){
									String role_property="";//角色特征
									frowset2= dao.search("select role_property from t_sys_role where role_id= '"+actor_id+"'");
									if (frowset2.next()){    	
										role_property= frowset2.getString("role_property");
									}
									
									String filterField="";
									if ("1".equals(role_property)){//部门领导
										String e0122=this.userView.getUserDeptId();
										if (e0122!=null &&e0122.length()>0){
											operOrg="UM"+e0122;
										}
										else {
											operOrg="";
										}
									}
									else if ("6".equals(role_property)){//单位领导
										String b0110=this.userView.getUserOrgId();
										if (b0110!=null &&b0110.length()>0){
											operOrg="UN"+b0110;
										}
										else {
											operOrg="";
										}
									}
									
								}
								
								
								
								String codesetid="";
								boolean noSql=true;
								if(scope_field.toUpperCase().indexOf("SUBMIT")!=-1)
								{ 
									if(scope_field.toUpperCase().indexOf("E0122")!=-1)
									{
										codesetid="UM";
										String value=getSubmitTaskInfo(task_id,"UM");
										if(value.length()>0)
										{ 
											scope_field="'"+value+"'"; 
										}
										else
										{
											noSql=false;
											 
										}
									}
									else if(scope_field.toUpperCase().indexOf("B0110")!=-1)
									{
										codesetid="UN";  
										String value=getSubmitTaskInfo(task_id,"UN");
										if(value.length()>0)
										{
											scope_field="'"+value+"'"; 
										}
										else
										{
											noSql=false;
											 
										}
									}
								}
								else
								{
									String[] temps=scope_field.split("_");
									String itemid=temps[0].toLowerCase(); 
									
									FieldItem _item=DataDictionary.getFieldItem(itemid);
									if(_item!=null)
										codesetid=_item.getCodesetid();
								}
								if("UN`".equalsIgnoreCase(operOrg))
								{
									
								}
								else if(noSql)
								{
									if(operOrg!=null && operOrg.length() > 3)
									{
										StringBuffer tempSql = new StringBuffer(""); 
										String[] temp = operOrg.split("`");
										for (int i = 0; i < temp.length; i++) {
											if("1".equals(containUnderOrg))
											{
												tempSql.append(" or "+scope_field+" like '" + temp[i].substring(2)+ "%'");				
											}
											else
											{
												if ("UN".equalsIgnoreCase(codesetid)&& "UN".equalsIgnoreCase(temp[i].substring(0, 2)))
													tempSql.append(" or "+scope_field+"='" + temp[i].substring(2)+ "'");
												else if ("UM".equalsIgnoreCase(codesetid)&& "UM".equalsIgnoreCase(temp[i].substring(0, 2)))
													tempSql.append(" or "+scope_field+" like '" + temp[i].substring(2)+ "%'");				
											}
										}
										
										if(tempSql.length()==0)
										{
											if("UN".equalsIgnoreCase(codesetid))
											{
												if("1".equals(containUnderOrg))
													tempSql.append(" or "+scope_field+" like '"+userView.getUserDeptId()+"%'");
												else
													tempSql.append(" or "+scope_field+"='"+userView.getUserOrgId()+"'");
											}
											else if ("UM".equalsIgnoreCase(codesetid))
											{
												tempSql.append(" or "+scope_field+" like '"+userView.getUserDeptId()+"%'");
											}
										}
										
										if(tempSql.toString().trim().length()==0)
											tempSql.append(" or 1=2 ");
										
										sql0+=" and ( " + tempSql.substring(3) + " ) ";
									}
									else
									{
										if(userView.getA0100()!=null&&userView.getA0100().trim().length()>0) // 2014-04-01 dengcan
										{
											if("UN".equalsIgnoreCase(codesetid))
											{
												if("1".equals(containUnderOrg))
													sql0+=" and"+scope_field+" like '"+userView.getUserDeptId()+"%'";
												else
													sql0+=" and "+scope_field+"='"+userView.getUserOrgId()+"'";
											}
											else if ("UM".equalsIgnoreCase(codesetid))
											{
												sql0+=" and"+scope_field+" like '"+userView.getUserDeptId()+"%'";
											}
										}
										else
											sql0+=" and 1=2 ";
									}
								}
								else
								{
									sql0+=" and 1=2 ";
								}
							}
						
							frowset2=dao.search(sql0);
							ArrayList updList=new ArrayList();
							while(frowset2.next())
							{
								String username=frowset2.getString("username");
								if(dataMap.get(node_id+task_id)==null)
								{
									dao.update("update t_wf_task_objlink set special_node=1 where task_id="+task_id+" and node_id="+node_id);
									dataMap.put(node_id+task_id,"1");
								}
								
								//if(username==null||username.trim().length()==0)
								//{
									ArrayList tempList=new ArrayList();
									Timestamp dateTime = new Timestamp((new Date()).getTime());
									tempList.add(dateTime);
									tempList.add(userView.getUserName());
									tempList.add(frowset2.getString("seqnum"));
									tempList.add(new Integer(frowset2.getString("task_id")));
									updList.add(tempList);
								//	dao.update("update t_wf_task_objlink set username='"+userView.getUserName()+"' where seqnum='"+frowset2.getString("seqnum")+"' and task_id="+frowset2.getString("task_id"));
								//}
							}
							
							if(updList.size()>0)
							{ 
								dao.batchUpdate("update t_wf_task_objlink set locked_time=?,username=? where seqnum=? and task_id=?",updList );
							}
							
							 
							sql0="select * from  t_wf_task_objlink  where task_id="+task_id+"   and username='"+userView.getUserName()+"'   ";	
							frowset2=dao.search(sql0);
							if(frowset2.next())
							{
								
							}else 
							{
								String encrypt_task_id=PubFunc.encrypt(task_id);
								imip.updatePending("T","HRMS-"+encrypt_task_id,100,"业务模板",this.userView);
								errorinfo="该单据已被他人锁定处理了！";
							}
						}
						else
						{
							//普通角色也抢单 2013-7-19 dengc
							StringBuffer sqlForRole = new StringBuffer("");
							sqlForRole.append("select username from t_wf_task_objlink twt where ");
							sqlForRole.append(" twt.task_id="+task_id+"  and  (( "+Sql_switcher.isnull("twt.username","' '")+"=' ' or twt.username='"+userView.getUserName()+"'   ");
							//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。单
							if(usernameList.size()>0){
								for(int i=0;i<usernameList.size();i++){
									sqlForRole.append(" or username='"+usernameList.get(i)+"' ");
								}
							}
							sqlForRole.append(" ) ");
							sqlForRole.append(" or (("+Sql_switcher.diffMinute(Sql_switcher.sqlNow() ,"twt.locked_time")+">="+Integer.parseInt(timeoutUration)+" or twt.locked_time is null) and ");
							sqlForRole.append(" ( "+Sql_switcher.isnull("twt.username","' '")+"=' ' or twt.username<>'"+userView.getUserName()+"'   ))) ");
							//String	sql0="select * from  t_wf_task_objlink  where task_id="+task_id+"   and ( "+Sql_switcher.isnull("username","' '")+"=' ' or username='"+userView.getUserName()+"' )   ";	
							frowset2=dao.search(sqlForRole.toString());
							if(frowset2.next())
							{
								//String username=frowset2.getString("username");
								//if(username==null||username.trim().length()==0)
								dao.update("update t_wf_task_objlink set locked_time="+Sql_switcher.sqlNow()+",username='"+userView.getUserName()+"'  where task_id="+task_id+" and node_id="+node_id);
								 
							}else{
									String encrypt_task_id=PubFunc.encrypt(task_id);
									imip.updatePending("T","HRMS-"+encrypt_task_id,100,"业务模板",this.userView);
									errorinfo="该单据已被他人锁定处理了！";
							}
						}
					
					
					}
				}
				
				
				
			}
			if(frowset2!=null)
				frowset2.close();
			if(frowset!=null)
				frowset.close();
			
			if(j==0){
				return "该单据已处理！";
			}
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return errorinfo;
	}
    
	
	/**
	 * 获得报批人所在的单位  或 部门
	 * @param task_id
	 * @param orgFlag UN:单位  UM：部门
	 * @return
	 */
	private String getSubmitTaskInfo(String task_id,String orgFlag)
	{
		String info="";
		ContentDAO dao=new ContentDAO(this.conn);
		String fielditem="e0122";
		if("UN".equalsIgnoreCase(orgFlag))
			fielditem="b0110";
		RowSet rset=null;
		try
		{
			String a0100="";
			rset=dao.search("select a0100_1 from t_wf_task where task_id="+task_id);
			if(rset.next())
				a0100=rset.getString(1);
			if(a0100!=null&&a0100.trim().length()>0)
			{
				if(a0100.length()>3)
				{
					String dbpre=a0100.substring(0,3);
					boolean flag=false;
					ArrayList dblist=DataDictionary.getDbpreList();
					for(int i=0;i<dblist.size();i++)
					{
						if(((String)dblist.get(i)).equalsIgnoreCase(dbpre))
							flag=true;
					}
					if(flag)
					{
						rset=dao.search("select "+fielditem+" from "+dbpre+"a01 where a0100='"+a0100.substring(3)+"' ");
						if(rset.next())
						{
							info=rset.getString(1);
						}
					} 
				}
				
				if(info.length()==0)
				{
					rset=dao.search("select a0100,nbase from operuser where username='"+a0100+"'");
					if(rset.next())
					{
						String _a0100=rset.getString("a0100");
						String _nbase=rset.getString("nbase");
						if(_a0100!=null&&_a0100.length()>0&&_nbase!=null&&_nbase.length()>0)
						{
							a0100 = _nbase+_a0100;
							rset=dao.search("select "+fielditem+" from "+_nbase+"a01 where a0100='"+_a0100+"' ");
							if(rset.next())
							{
								info=rset.getString(1);
							}
						}
						
					}
					
				}
				if(info.length()==0&&"UM".equalsIgnoreCase(orgFlag)) {
					String dbpre=a0100.substring(0,3);
					fielditem="b0110";
					rset=dao.search("select "+fielditem+" from "+dbpre+"a01 where a0100='"+a0100.substring(3)+"' ");
					if(rset.next())
					{
						info=rset.getString(1);
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
				if(rset!=null)
					rset.close();
			}
			catch(Exception e)
			{
				
			}
		}
		return info;
	}
	
	

	
	/**
	 * @param  strbase 源应用库,如果目标库未定义的话，则按源应用库进行数据操作
	 * @param  a0100   数据处理对象编号
	 * @param  srctab  源表
	 * @throws GeneralException
	 */
	private void subSetChangeSubmit(String strbase,String srctab ,HashMap submap,int ins_id,String strCondition)throws GeneralException
	{
        Object[] key =submap.keySet().toArray();
    	RowSet _rowSet=null;
    	RowSet rowSet=null;
    	try
    	{
    		
    		ContentDAO dao=new ContentDAO(this.conn);
			boolean bSumitedSubArea=false;//提交子集区域标志		
			ArrayList subUpdateList=this.paramBo.getSubUpdateList();
			StringBuffer strsql=new StringBuffer("select a0100,");
			for(int i=0;i<key.length;i++)
	        {
	        	String setid=(String)key[i];
	        	FieldItem item=(FieldItem)submap.get(setid);	
	        	String domain_id="";
	        	if(setid.contains("_")){///子集存在多个变化后的情况 wangrd 20160826
	        		String tmp =setid;
	        		int k = setid.indexOf("_");
	        		setid=tmp.substring(0,k);
	        		domain_id= tmp.substring(k+1,tmp.length());
	        	}
	        	
	        	String temp="";
	        	int updatetype=1; 
	        	for(int j=0;j<subUpdateList.size();j++)
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
	        	if(updatetype==0)
	        		continue;
	        	
	        	String state=this.userView.analyseTablePriv(setid.toUpperCase());
	        	if((state==null|| "0".equalsIgnoreCase(state))&& "0".equals(this.paramBo.getUnrestrictedMenuPriv())) //判断子集是否有写权限
	        		continue;
	        	        	
	        	String field_name=item.getItemid();
	    		if (domain_id.length()>0){
	    			if(field_name.indexOf("_2")==-1)
	    				field_name=field_name+"_"+domain_id+"_2";
	    			else 
	    				field_name=field_name+"_"+domain_id;
	    		}
	    		else {
	    			if(field_name.indexOf("_2")==-1)
	    				field_name=field_name+"_2";
	    		}
	    		strsql.append(field_name+",");
	        }
			strsql.setLength(strsql.length()-1);
    		strsql.append(" from  "+srctab+strCondition);   
    		_rowSet=dao.search(strsql.toString());
    		ArrayList list=new ArrayList(); 
    		StringBuffer buf=new StringBuffer();
    	
    		for(int i=0;i<key.length;i++)
	        {
	        	String setid=(String)key[i];
	        	FieldItem item=(FieldItem)submap.get(setid);	
	        	String domain_id="";
	        	if(setid.contains("_")){///子集存在多个变化后的情况 wangrd 20160826
	        		String tmp =setid;
	        		int k = setid.indexOf("_");
	        		setid=tmp.substring(0,k);
	        		domain_id= tmp.substring(k+1,tmp.length());
	        	}
	        	
	        	list=new ArrayList();
	        	String temp="";
	        	int updatetype=1; 
	        	for(int j=0;j<subUpdateList.size();j++)
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
	        	if(updatetype==0)
	        		continue;
	        	
	        	
	        	String state=this.userView.analyseTablePriv(setid.toUpperCase());
	        	if((state==null|| "0".equalsIgnoreCase(state))&& "0".equals(this.paramBo.getUnrestrictedMenuPriv())) //判断子集是否有写权限
	        		continue;
	        	
	        	        	
	        	String field_name=item.getItemid();
	    		TSubSetDomain setdomain=new TSubSetDomain(item.getFormula());
	    		setdomain.setCon(this.conn);
	    		setdomain.setUnrestrictedMenuPriv(this.paramBo.getUnrestrictedMenuPriv());
	    		setdomain.setUnrestrictedMenuPriv_Input(this.paramBo.getUnrestrictedMenuPriv_Input());
	    		setdomain.setId_gen_manual(this.paramBo.getId_gen_manual());
	    		setdomain.setUserview(this.userView);  //控制指标权限
	    		setdomain.setInfor_type(1);
	    		if (domain_id.length()>0){
	    			if(field_name.indexOf("_2")==-1)
	    				field_name=field_name+"_"+domain_id+"_2";
	    			else 
	    				field_name=field_name+"_"+domain_id;
	    		}
	    		else {
	    			if(field_name.indexOf("_2")==-1)
	    				field_name=field_name+"_2";
	    		}
	    		if(!_rowSet.isBeforeFirst())
	    			_rowSet.beforeFirst();
	    		while(_rowSet.next())
	        	{
	        		String a0100=_rowSet.getString("a0100");
	        		String xml=Sql_switcher.readMemo(_rowSet,field_name.toLowerCase());
	        		if(xml==null||xml.length()==0)
	        			continue;
	        		ArrayList reclist=setdomain.getChangeRecList(xml, strbase+setid, a0100);
	        		if(this.isRepreateSubmit) {
	        			reclist=this.clearRepreateSublist(reclist, strbase+setid, a0100);
	        		}
	        		HashMap filemap=setdomain.getFilemap();
	        		/**先更新*/
	        	//	list.clear();
	        		for(int j=0;j<reclist.size();j++)
	        		{
	        			RecordVo recvo=(RecordVo)reclist.get(j);
	        			int i9999=recvo.getInt("i9999");
	        			if(i9999==-1)
	        				continue; 
	        			recvo.setString("modusername",this.userView.getUserName());
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
	        			if(i9999!=-1)
	        				continue;
	        			if(recvo.getValues().size()<=2)
	        				continue;
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
	        				buf.append(" where a0100='"); 
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
	        				stri9999=DbNameBo.insertSubSetA0100(strbase+setid, a0100, conn,this.userView.getUserName()); 
	        				i9999s=Integer.parseInt(stri9999);
	        				recvo.setInt("i9999", Integer.parseInt(stri9999));
		        			dao.updateValueObject(recvo);
	        			
	        			}
	        			
	        			//处理i9999=-1的情况，将附件上传，提交入库 liuzy 20151031
	        			if(!"".equals(value)){
	        				MultiMediaBo multimediabo=new MultiMediaBo(conn, userView, "A",strbase,setid, a0100, i9999s);
	        				multimediabo.initParam(false);
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
    	finally
    	{
    		PubFunc.closeDbObj(_rowSet);
    		PubFunc.closeDbObj(rowSet);
    	}
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
			if(tmp==-1)
				continue;
			else
			{
				if(oldMini9999==0){
					oldMini9999=tmp;
				}
				else {
					if (tmp<oldMini9999)
						oldMini9999=tmp;
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
				if(i9999arr[j]==-1)
					continue;
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
	public void batchChangeSubmit(HashMap subhm,String srcbase,String srctab,int ins_id,int task_id,boolean isBEmploy,HashMap submap)throws GeneralException
	{ 
		RecordVo tabVo=new RecordVo(srctab.toLowerCase());
		if(!tabVo.hasAttribute("currenti9999")) //记录子集入库时每个人最新的I9999值
		{
			 DBMetaModel dbmodel=new DBMetaModel(this.conn);
			 Table table=new Table(srctab); 
             Field field=new Field("currentI9999","currentI9999");
             field.setDatatype(DataType.INT); 
             table.addField(field); 
             
             field=new Field("updatetype","updatetype");
             field.setDatatype(DataType.INT); 
             table.addField(field);  
             DbWizard dbw=new DbWizard(this.conn);
             dbw.addColumns(table);
             dbmodel.reloadTableModel(srctab);
            
		}
		if(!tabVo.hasAttribute("nullvalue"))  
		{
			 DBMetaModel dbmodel=new DBMetaModel(this.conn);
			 Table table=new Table(srctab); 
			 Field field=new Field("nullvalue","nullvalue");
             field.setDatatype(DataType.INT); 
             table.addField(field);  
             DbWizard dbw=new DbWizard(this.conn);
             dbw.addColumns(table);
             dbmodel.reloadTableModel(srctab);
		}
		
		StringBuffer strSubCondition=new StringBuffer(""); 
		if(task_id!=0)
		{ 
			//暂时不确定是否要加角色范围控制 
			strSubCondition.append(" where  exists (select null from t_wf_task_objlink where templet_"+this.tabId+".seqnum=t_wf_task_objlink.seqnum and templet_"+this.tabId+".ins_id=t_wf_task_objlink.ins_id ");
			strSubCondition.append("  and task_id="+task_id+" and tab_id="+this.tabId+" and state=1 ) ");
		}
		else
		{
			if(isBEmploy)//员工通过自助平台发动申请
			{
				strSubCondition.append(" where  "+srctab+".basepre='");
				strSubCondition.append(this.userView.getDbname());
				strSubCondition.append("' and "+srctab+".a0100='");
				strSubCondition.append(this.userView.getA0100());
				strSubCondition.append("'");
			}
			else
				strSubCondition.append(" where  submitflag=1");
		}
		strSubCondition.append(" and lower(basepre)='"+srcbase.toLowerCase()+"'"); 
		StringBuffer strCondition=new StringBuffer("select a0100 from "+srctab+strSubCondition.toString()); 
		StringBuffer updateCurrentI9999_0=new StringBuffer("update "+srctab+" set currentI9999=0,nullvalue=0,updatetype=0 "+strSubCondition.toString()); 
		StringBuffer updateCurrentI9999_1=new StringBuffer(" update "+srctab+" set currentI9999=(select max(i9999) from setname where "+srctab+".a0100=setname.a0100 ");
		updateCurrentI9999_1.append(" ) "+strSubCondition.toString()+" and  exists ( select null from setname where "+srctab+".a0100=setname.a0100 )");
		if(this.paramBo.getOperationType()==4||this.paramBo.getOperationType()==0) //系统内部调动 || 人员调入
		{
			return;
		} 
		
        Object[]   key   =     subhm.keySet().toArray();   
        Arrays.sort(key); 
        
		/**数据库类型*/	
		int db_type=Sql_switcher.searchDbServer();	
		/**数据同步版*/
		boolean bDatasync=true; //false;
		String strvalue=null;
		String destab=null;
		StringBuffer strsql=new StringBuffer();
		BaseInfoBo infobo=new BaseInfoBo(this.conn,this.userView,1);
		/**分析数据处理方式*/
		int noper=0;
	 
		/**记录操作方式*/
		int updatetype=SubSetUpdateType.NOCHANGE;
			/**如果目标库和源库不一致的话，则先更新数据，然后进行移库操作*/
		switch(this.paramBo.getOperationType()){
			case 1://人员调出
			case 2://人员离退
				if(!(this.paramBo.getDest_base()==null|| "".equals(this.paramBo.getDest_base()))&&(!this.paramBo.getDest_base().equalsIgnoreCase(srcbase)))
					noper=2;
				else/**如果目标库未定义的话，则按源应用库进行数据更新*/
				{ 
					noper=1;
				}
				break;
			default://其它业务，只进行更新
				noper=1;
				break;
		} 
		try
		{
			YksjParser yp=null;
			ContentDAO dao=new ContentDAO(this.conn);
			boolean bSumitedSubArea=false;//提交子集区域标志
			 
			for(int k=0;k<key.length;k++)
			{ 
				/**清空*/
				strsql.setLength(0);
				String setname=(String)key[k];
				TSubsetCtrl subctrl=(TSubsetCtrl)subhm.get(setname);
				/**目标表*/
				destab=srcbase+setname;
				switch(setname.charAt(0))
				{
				case 'A'://人员信息
				case 'a':
					strvalue=getChangeUpdateSQL(srctab,destab,subctrl.getFieldlist(),bDatasync);
					/**主集信息更新*/
					if("a01".equalsIgnoreCase(setname))
					{
						if(subctrl.getUpdatetype()==SubSetUpdateType.NOCHANGE)
						{
							subSetChangeSubmit(srcbase,srctab ,submap,ins_id,strSubCondition.toString());
						    bSumitedSubArea=true;
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
							strsql.append(".a0100=");
							strsql.append(srctab);
							strsql.append(".a0100 ");
							if(ins_id!=0)
							{
								strsql.append(" and ins_id=");
								strsql.append(ins_id);
							}
							
							strsql.append(" and "+strSubCondition.substring(7)+"  ");
							strsql.append(") where ");
							strsql.append(destab);
							strsql.append(".a0100 in ( "+strCondition+") "); 
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
							strsql.append(" where 1=1 "); 
							if(ins_id!=0)
							{
								strsql.append(" and "+srctab+".ins_id="+ins_id);
							}
							strsql.append(" and "+strSubCondition.substring(7));
							
							break;
						}
						dao.update(strsql.toString());
						subSetChangeSubmit(srcbase,srctab ,submap,ins_id,strSubCondition.toString());
					    bSumitedSubArea=true;
					
					}
					else//子集
					{
						
						updatetype=subctrl.getUpdatetype();
						dao.update(updateCurrentI9999_0.toString().replaceAll(",updatetype=0", ",updatetype="+updatetype)); //将currentI9999置0
						dao.update(updateCurrentI9999_1.toString().replaceAll("setname", destab)); //将currentI9999置最新的值
						 
						
						if(subctrl.getUpdatetype()==3) //子集条件更新
						{ 
							
							String cond_str="";
							String condFormula=subctrl.getCondFormula();
							if(condFormula==null||condFormula.trim().length()==0)
							{
								cond_str=" and ( 1=1 ) ";	
							}
							else
							{
								yp = new YksjParser( this.userView ,getCondUpdateFieldList(setname),
										YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
								
								yp.run_where(condFormula);
								String strfilter=yp.getSQL();
								if(strfilter.length()>0)
									cond_str=" and ("+strfilter+") ";
								
							}
							 
							String sql="select distinct  "+srctab+".a0100  from  "+destab+","+srctab+" where "+destab+".a0100="+srctab+".a0100 and  "+strSubCondition.substring(7)+" "+cond_str;
							RowSet rowSet=dao.search(sql);
							sql="update "+srctab+" set updatetype=4 "+strSubCondition.toString()+" and a0100=?";
							ArrayList valuesList=new ArrayList();
							while(rowSet.next())
							{
								ArrayList valueList=new ArrayList();
								valueList.add(rowSet.getString(1));
								valuesList.add(valueList);
							}
							if(valuesList.size()>0)
								dao.batchUpdate(sql,valuesList);
							
							strsql.setLength(0);
							//不符合条件 updatetype=3
							//仅判断 按条件更新没填值，也不增 （新加一种不增记录的情况）
							getUpdateTypeBySubctrl(subctrl,destab,srctab,strSubCondition.toString(),srcbase,1);
							HashMap mapUsedFieldItems = yp.getMapUsedFieldItems();
							boolean isHaveSubsetFiled = judgeIsHaveSubsetFiled(setname,mapUsedFieldItems);
							AutoBatchCreateNextRecord(setname,srcbase,subctrl.getRefPreRec(),srctab,strSubCondition.toString()," and updatetype=3 ",isHaveSubsetFiled);
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
								strsql.append(".a0100=");
								strsql.append(srctab);
								strsql.append(".a0100 and ");
								
								strsql.append(destab);
								strsql.append(".i9999=");
								strsql.append(srctab);
								strsql.append(".currentI9999 and ");
								
								strsql.append(strSubCondition.substring(7)+" and  updatetype=3   ");
							
								strsql.append(") where EXISTS ( select a0100 from "+srctab);
								strsql.append(" where ");
								strsql.append(destab);
								strsql.append(".a0100=");
								strsql.append(srctab);
								strsql.append(".a0100 and ");
								
								strsql.append(destab);
								strsql.append(".i9999=");
								strsql.append(srctab);
								strsql.append(".currentI9999 and ");
								
								strsql.append(strSubCondition.substring(7)+" and  updatetype=3  ) ");
							 
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
								strsql.append(".a0100 and ");
								strsql.append(destab);
								strsql.append(".i9999=");
								strsql.append(srctab);
								strsql.append(".currentI9999 "); 
								strsql.append(" where ");
								strsql.append(strSubCondition.substring(7));
								strsql.append("  and "+srctab+".updatetype=3"); 
								break;
							}  
							dao.update(strsql.toString());
							
							
							
							//符合条件的 updatetype=4
							
							strsql.setLength(0); 
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
								strsql.append(".a0100=");
								strsql.append(srctab);
								strsql.append(".a0100   "+cond_str);
								

								/*strsql.append(" and ");
								strsql.append(destab);
								strsql.append(".i9999=");
								strsql.append(srctab);
								strsql.append(".currentI9999 ");*/
								
								strsql.append(" and "+strSubCondition.substring(7)+" and  updatetype=4   ");
							
								strsql.append(") where EXISTS ( select a0100 from "+srctab+" where ");
								strsql.append(destab);
								strsql.append(".a0100=");
								strsql.append(srctab);
								strsql.append(".a0100   "+cond_str); 
								strsql.append(" and "+strSubCondition.substring(7)+" and  updatetype=4   ) ");  
								 
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
								strsql.append(".a0100  "+cond_str);
								 
								strsql.append(" and ");
								strsql.append(destab);
								strsql.append(".i9999=");
								strsql.append(srctab);
								strsql.append(".currentI9999 ");
								
								strsql.append(" where ");
								strsql.append(strSubCondition.substring(7));
								strsql.append(" and "+srctab+".updatetype=4"); 
								break;
							}  
							dao.update(strsql.toString());
							
							  
							
						}
						else if(updatetype==4){//条件新增
							String cond_str="";
							String condFormula=subctrl.getCondFormula();
							if(condFormula==null||condFormula.trim().length()==0)
							{
								cond_str=" and ( 1=1 ) ";	
							}
							else
							{
								yp = new YksjParser( this.userView ,getCondUpdateFieldList(setname),
										YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
								
								yp.run_where(condFormula);
								String strfilter=yp.getSQL();
								if(strfilter.length()>0)
									cond_str=" and ("+strfilter+") ";
							}
							//条件新增
							if(this.isRepreateSubmit) {
								boolean flag=checkRepreateData(destab,srctab,db_type,strSubCondition.toString(),cond_str,subctrl.getFieldlist());
								if(flag) {
									continue;
								}
							}
							//判断计算公式是否包含子集指标
							HashMap mapUsedFieldItems = yp.getMapUsedFieldItems();
							boolean isHaveSubsetFiled = judgeIsHaveSubsetFiled(setname,mapUsedFieldItems);
							AutoBatchCreateNextRecord(setname,srcbase,subctrl.getRefPreRec(),srctab,strSubCondition.toString()+cond_str," and updatetype=4 ",isHaveSubsetFiled);
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
								strsql.append(".a0100=");
								strsql.append(srctab);
								strsql.append(".a0100 and ");
								
								strsql.append(destab);
								strsql.append(".i9999=");
								strsql.append(srctab);
								strsql.append(".currentI9999 and ");
								
								strsql.append(strSubCondition.substring(7)+" and  updatetype=4   "+cond_str);
							
								strsql.append(") where EXISTS ( select a0100 from "+srctab);
								strsql.append(" where ");
								strsql.append(destab);
								strsql.append(".a0100=");
								strsql.append(srctab);
								strsql.append(".a0100 and ");
								
								strsql.append(destab);
								strsql.append(".i9999=");
								strsql.append(srctab);
								strsql.append(".currentI9999 and ");
								
								strsql.append(strSubCondition.substring(7)+" and  updatetype=4  "+cond_str+") ");
							 
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
								strsql.append(".a0100 and ");
								strsql.append(destab);
								strsql.append(".i9999=");
								strsql.append(srctab);
								strsql.append(".currentI9999 "); 
								strsql.append(" where ");
								strsql.append(strSubCondition.substring(7));
								strsql.append("  and "+srctab+".updatetype=4"+cond_str); 
								break;
							}  
							dao.update(strsql.toString());
						}
						else
						{
							boolean bflag=false;
							 // 如果来自消息库且对应子集的更新字段，如果有更新指标， 则子集记录进 行更新操作
							RowSet rowSet=dao.search("select a0100,changelast,sourcetempid from tmessage where exists (select null from "+srctab+" where "+srctab+".state=1  and  lower(tmessage.db_type)=lower("+srctab+".basepre)  and  lower(tmessage.a0100)=lower("+srctab+".a0100) and noticetempid="+this.tabId+" and  "+strSubCondition.substring(6)+" ) ");
							ArrayList fieldlist=subctrl.getFieldlist();
							String chglast="";
							int tm_tabid=0;
							if(rowSet.next()) {
								tm_tabid=rowSet.getInt("sourcetempid");
								chglast=Sql_switcher.readMemo(rowSet,"changelast");
								
							}
							/**
							 * 找到当前通知单上一个下发的模板 (只找上一个模板，多级下发通知单不考虑)
							 * 判断模板子集指标更新方式是新增（包括条件）还是更新（包括条件），
							 * 如果是新增现有逻辑不变,当前模板记录只允许更新操作
							 * 如果是更新 则按照当前模板子集更新方式 更新或者新增
							 */
							boolean flag=true;
							if(tm_tabid!=0) {
								TemplateTableBo temTable=new TemplateTableBo(conn, tm_tabid, this.userView);
								ArrayList subUpdateList=temTable.getSubUpdateList();
								for(int i=0;i<subUpdateList.size();i++) {
									TSubsetCtrl subctrl0=(TSubsetCtrl)subUpdateList.get(i);
									if(setname.equalsIgnoreCase(subctrl0.getSetcode())) {
										if(SubSetUpdateType.NOCHANGE==0||subctrl0.getUpdatetype()==SubSetUpdateType.UPDATE||
										 subctrl0.getUpdatetype()==SubSetUpdateType.COND_UPDATE) {
											flag=false;
										}
									}
								}
							}
							chglast=chglast.toUpperCase();
							if(chglast.length()>0&&flag)
							{
								for(int i=0;i<fieldlist.size();i++)
								{
									String fieldname=((String)fieldlist.get(i)).toUpperCase();
									if(chglast.indexOf(fieldname)!=-1)
									{
										bflag=true;
										break;
									}
								}
							}
							if(bflag) {
								dao.update("update "+srctab+" set updatetype=2  "+strSubCondition.toString()+" and state=1 and updatetype=1 ");
							}
							if(this.isRepreateSubmit&&subctrl.getUpdatetype()==2) {
								boolean flag_type=checkRepreateData(destab,srctab,db_type,strSubCondition.toString(),"",subctrl.getFieldlist());
								if(flag_type) {
									continue;
								}
							}
							//判断子集指标变化后的值是否和库中最后一条记录的值相同，如果相同，则不操作
							getUpdateTypeBySubctrl(subctrl,destab,srctab,strSubCondition.toString(),srcbase,0);
							AutoBatchCreateNextRecord(setname,srcbase,subctrl.getRefPreRec(),srctab,strSubCondition.toString()," and updatetype=1 ",false);
							
							
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
								strsql.append(".a0100=");
								strsql.append(srctab);
								strsql.append(".a0100 and ");
								
								strsql.append(destab);
								strsql.append(".i9999=");
								strsql.append(srctab);
								strsql.append(".currentI9999 and ");
								
								strsql.append(strSubCondition.substring(7)+"  and ( "+srctab+".updatetype=1 or "+srctab+".updatetype=2 )  ");
							
								strsql.append(") where EXISTS ( select a0100 from "+srctab);
								strsql.append(" where ");
								strsql.append(destab);
								strsql.append(".a0100=");
								strsql.append(srctab);
								strsql.append(".a0100 and ");
								
								strsql.append(destab);
								strsql.append(".i9999=");
								strsql.append(srctab);
								strsql.append(".currentI9999 and "); 
								strsql.append(strSubCondition.substring(7)+"  and ( "+srctab+".updatetype=1 or "+srctab+".updatetype=2 )  ) ");
							 
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
								strsql.append(".a0100 and ");
								strsql.append(destab);
								strsql.append(".i9999=");
								strsql.append(srctab);
								strsql.append(".currentI9999 "); 
								strsql.append(" where ");
								strsql.append(strSubCondition.substring(7));
								strsql.append(" and ( "+srctab+".updatetype=1 or "+srctab+".updatetype=2 )    "); 
								break;
							} 
					
							dao.update(strsql.toString());
							
							
						}
						linkUpdatePreRec(setname,srcbase+setname,strSubCondition.toString(),srctab);
					}//while loop end.
				}
			}
			if(!bSumitedSubArea){ 
				 subSetChangeSubmit(srcbase,srctab ,submap,ins_id,strSubCondition.toString());
		    }
	         
			 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 模板插入整个子集且子集记录时新增的情况下
	 * 校验模板子集记录是否存在-1的记录 如果有这样的记录则校验为-1的记录在对应子集中是否有相同记录，有则去除为-1的记录
	 * @param reclist
	 * @param subSet
	 * @param a0100
	 * @return
	 */
	private ArrayList clearRepreateSublist(ArrayList<RecordVo> reclist,String subSet,String a0100) {
		ArrayList<RecordVo> list=new ArrayList<RecordVo>();
		ContentDAO dao=new ContentDAO(conn);
		RowSet rs=null;
		try {
			for(RecordVo vo:reclist) {
				if(vo.getInt("i9999")==-1) {
					list.add(vo);
				}
			}
			if(list!=null&&list.size()>0) {
				ArrayList keyList=list.get(0).getKeylist();
				StringBuffer sbf=new StringBuffer();
				HashMap<String,Object> map=null;
				ArrayList value_list=null;
				for(RecordVo vo:list) {
					value_list=new ArrayList();
					sbf.setLength(0);
					map=vo.getValues();
					if(map!=null) {
						sbf.append("select i9999 from "+subSet+" where ");
						String flag_type="";
						for(String key:map.keySet()) {
							if("modusername".equalsIgnoreCase(key)|| "modtime".equalsIgnoreCase(key)|| "createtime".equalsIgnoreCase(key)|| "i9999".equalsIgnoreCase(key)) {
								continue;
							}
							FieldItem item=DataDictionary.getFieldItem(key);
							if(item!=null&& "D".equalsIgnoreCase(item.getItemtype())) {
								if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
									sbf.append(" "+Sql_switcher.isnull(Sql_switcher.dateToChar(key, "yyyy.MM.dd HH24:mi:ss"), "' '")+"=? and");
								}else {
									sbf.append(" "+Sql_switcher.dateToChar(key, "yyyy.MM.dd HH:mm:ss")+"=? and");
								}
							}else if(item!=null&& "A".equalsIgnoreCase(item.getItemtype())) {
								sbf.append(" "+Sql_switcher.isnull(key, "' '")+"=? and");
							}else if(item!=null&& "M".equalsIgnoreCase(item.getItemtype())){
								sbf.append(" "+Sql_switcher.isnull(Sql_switcher.sqlToChar(key), "' '")+"=? and");
							}else {
								sbf.append(" "+key+"=? and");
							}
							
							if(map.get(key)!=null) {
								if(item!=null&&item.isDate()) {
									Date date=vo.getDate(key);
									SimpleDateFormat sfor=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
									value_list.add(sfor.format(date));
								}else {
									if(StringUtils.isEmpty(map.get(key).toString())) {
										value_list.add(" ");
									}else {
										value_list.add(map.get(key));
									}
								}
								if(!"a0100".equalsIgnoreCase(key)) {
									flag_type+=map.get(key).toString();
								}
							}else {
								value_list.add(" ");
							}
							
							
						}
						if(value_list!=null&&value_list.size()>0) {
							rs=dao.search(sbf.toString().substring(0, sbf.length()-3), value_list);
							if(rs.next()||StringUtils.isEmpty(flag_type)) {
								//重复提交时 模板子集记录为-1时，并且为-1的记录不在最后一条
								if(vo.getInt("i9999")==-1) {
									if(reclist.get(reclist.size()-1).equals(vo)||StringUtils.isEmpty(flag_type)) {
										reclist.remove(vo);
									}else {
										//模板子集记录中有相同记录，且i9999是-1的情况 可认为子集记录与模板子集记录相同，不做任何操作 reclist置空
										reclist=new ArrayList<RecordVo>();
										break;
									}
								}
							}else {
								/*	//如果没有相同记录，模板子集记录的i9999 应该作为 新增记录
							vo.setInt("i9999", -1);*/
							}
						}
						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		
		return reclist;
	}
	
	/**
	 * 检查子集新增是否存在重复数据
	 * @param sql
	 * @return
	 */
	private boolean checkRepreateData(String destab,String srctab,int db_type,String strSubCondition,String cond_str,ArrayList fieldList) {
		boolean flag=false;
		RowSet rs=null;
		try {
			ContentDAO dao=new ContentDAO(conn);
			StringBuffer strsql=new StringBuffer();
			strsql.append("select 1 ");
			strsql.append(" from ");
			strsql.append(destab);
			strsql.append(" left join ");
			strsql.append(srctab);
			strsql.append(" on ");
			strsql.append(srctab);
			strsql.append(".a0100=");
			strsql.append(destab);
			strsql.append(".a0100  ");
			strsql.append(" where ");
			strsql.append(strSubCondition.substring(7));
			strsql.append("  and "+srctab+".updatetype=4"+cond_str);
			for(int i=0;i<fieldList.size();i++) {
				strsql.append(" and "+destab+"."+fieldList.get(i)+" = "+srctab+"."+fieldList.get(i)+"_2 ");
				/*if(i<fieldList.size()-1) {
					strsql.append(" and ");
				}*/
			}
			rs=dao.search(strsql.toString());
			if(rs.next()) {
				flag=true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 判断计算公式是否包含子集指标		
	 * @param setname
	 * @param mapUsedFieldItems
	 * @return
	 */
	private boolean judgeIsHaveSubsetFiled(String setname, HashMap mapUsedFieldItems) {
		boolean isHaveSubsetFiled = false;
		try {
			ArrayList fieldList = (ArrayList)DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET).clone();
			for(Object fielditemid:mapUsedFieldItems.keySet()) {
				for(int i=0;i<fieldList.size();i++){
					FieldItem fielditem = (FieldItem) fieldList.get(i);
					String itemid = fielditem.getItemid();
					if(itemid.equalsIgnoreCase(fielditemid.toString())) {
						isHaveSubsetFiled = true;
						break;
					}
				}
				if(isHaveSubsetFiled) {
					break;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return isHaveSubsetFiled;
	}
	private ArrayList getCondUpdateFieldList(String setid)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			list.addAll((ArrayList)DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET).clone());
			String sql="select * from template_set  where tabid="+this.tabId+" and field_name is not null and field_type is not null  and subflag='0'";
			RowSet rowSet=dao.search(sql);
			while(rowSet.next())
			{
				String flag=rowSet.getString("flag");
				String field_type=rowSet.getString("field_type");
				int chgstate=rowSet.getInt("chgstate");
				if(flag==null|| "".equals(flag))
					continue;
				if("A".equalsIgnoreCase(flag)|| "B".equalsIgnoreCase(flag)|| "K".equalsIgnoreCase(flag))
				{
					if(field_type!=null&&field_type.trim().length()>0)
					{
						if(!("A".equalsIgnoreCase(field_type)|| "N".equalsIgnoreCase(field_type)|| "D".equalsIgnoreCase(field_type)|| "M".equalsIgnoreCase(field_type)))
							continue;
					}
					String field_name=rowSet.getString("field_name"); 
					FieldItem item=DataDictionary.getFieldItem(field_name.toLowerCase());
					if(item!=null)
					{
						/**可以增加模板指标与字典表指标进行校验*/
						FieldItem tempitem=(FieldItem)item.cloneItem();
						if(chgstate==2)
						{
							tempitem.setNChgstate(2); 
							tempitem.setItemid(rowSet.getString("field_name")+"_2");
							tempitem.setItemdesc("拟"+rowSet.getString("field_hz"));
						}
						else
						{
							tempitem.setNChgstate(1); 
							tempitem.setItemid(rowSet.getString("field_name")+"_1");
							tempitem.setItemdesc("现"+rowSet.getString("field_hz"));
						}
						list.add(tempitem);
					}
				}
				
			}
			
			ArrayList fieldlist=getMidVariableList();//临时变量
			list.addAll(fieldlist);
			PubFunc.closeDbObj(rowSet);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	
			
	/**
	 * 关联更新上条记录
	 * @param fields
	 * @param srcbase
	 * @param a0100
	 * @param curri999
	*/
	private void linkUpdatePreRec(String setname,String tablename,String strSubCondition,String srcTable)
	{
				Iterator iterator=this.paramBo.getLinkmap().entrySet().iterator();
				HashMap linkdateflagmap=this.paramBo.getLinkdateflagmap();
				ContentDAO dao=new ContentDAO(this.conn);
				try
				{
					Calendar calendar=Calendar.getInstance();   
					while(iterator.hasNext())
					{
						Entry entry=(Entry)iterator.next();
						String dest_field=((String)entry.getKey()).toLowerCase();
						String src_field=((String)entry.getValue()).toLowerCase();
						FieldItem item=DataDictionary.getFieldItem(dest_field,setname);
						ArrayList valueList=new ArrayList();
						if(item!=null)
						{
						//	System.out.println("select "+src_field+",a0100,i9999  from "+tablename+" where exitsts (select null from "+srcTable+" where "+srcTable+".a0100="+tablename+".a0100 and "+srcTable+".currentI9999="+tablename+".i9999 and "+strSubCondition.substring(7)+" and currentI9999>1 and updatetype in (1,2,3) )");
							RowSet rowSet=dao.search("select "+src_field+",a0100,i9999  from "+tablename+" where EXISTS (select null from "+srcTable+" where "+srcTable+".a0100="+tablename+".a0100 and "+srcTable+".currentI9999="+tablename+".i9999 and "+strSubCondition.substring(7)+" and currentI9999>1 and updatetype in (1,2,3,4) )");
							while(rowSet.next())
							{
								Date d=rowSet.getDate(src_field.toLowerCase());
								calendar.setTime(d);
								if(linkdateflagmap!=null&&linkdateflagmap.get(dest_field)!=null&&"1".equals(linkdateflagmap.get(dest_field))){
									calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH));//日期相同
								}else{
									calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)-1);//让日期减1
								}
								ArrayList values=new ArrayList();
								values.add(new java.sql.Date(calendar.getTime().getTime()));
								values.add(rowSet.getInt("i9999")-1);
								values.add(rowSet.getString("a0100"));
								valueList.add(values);
								
								
							}
							dao.batchUpdate("update "+tablename+" set "+dest_field.toLowerCase()+"=? where i9999=? and a0100=?",valueList);
							 
						}
					}
				}
				catch(Exception ex)
				{
					//ex.printStackTrace();
				}
	}
    
    
			
			
	/**
	 * 判断子集指标变化后的值是否和库中最后一条记录的值相同，如果相同，则不操作
	 * @param subctrl
	 * @param destab
	 * @param strctab
	 * @param src_updateType
	 * @param  1:仅判断 按条件更新没填值，也不增 （新加一种不增记录的情况）
	 * @return
	 */
	private void getUpdateTypeBySubctrl(TSubsetCtrl subctrl,String destab,String strctab,String strSubCondition,String nbase,int opt)
	{
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList fieldlist=subctrl.getFieldlist();
			if(fieldlist.size()==0)
				return;
			StringBuffer sql=new StringBuffer("select aa.a0100  ");
			StringBuffer sqlnull=new StringBuffer("select a.a0100  ");
			
			StringBuffer sql1=new StringBuffer(",a0100");
			if(strctab.equalsIgnoreCase("templet_"+this.tabId))
			{
				sql.append(",ins_id");
				sql1.append(",ins_id");
				sqlnull.append(",a.ins_id");
			}
			else
			{
				sql.append(",0 as ins_id");
				sqlnull.append(",0 as ins_id");
			}
			sql.append(" from ");
			sqlnull.append(" from ");
			
			StringBuffer sql2=new StringBuffer(",a.a0100");
			StringBuffer sql3=new StringBuffer("");
			StringBuffer sql4=new StringBuffer("");
			boolean flag=false;
			FieldItem item=null;
			for(int i=0;i<fieldlist.size();i++)
			{
				String field_name=(String)fieldlist.get(i);
			//	if(field_name.indexOf("t_")!=-1)
				if(field_name.length()>2&& "t_".equalsIgnoreCase(field_name.trim().substring(0,2)))
					continue;
				item=DataDictionary.getFieldItem(field_name.toLowerCase());
				if(item==null/*&&item.getItemtype().equalsIgnoreCase("M")*/)
					continue;
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
				if(strctab.equalsIgnoreCase("g_templet_"+this.tabId))
					sql.append("(select "+sql1.substring(1)+" from "+strctab+strSubCondition.toString()+"  and updatetype<>3  ) aa,");
				else if(strctab.equalsIgnoreCase("templet_"+this.tabId))
					sql.append("(select "+sql1.substring(1)+" from "+strctab+strSubCondition.toString()+"  and updatetype<>3   ) aa,");
				else
					sql.append("(select "+sql1.substring(1)+" from "+strctab+strSubCondition.toString()+"  and updatetype<>3  ) aa,");
				sql.append("(select "+sql2.substring(1)+" from "+destab+" a where a.i9999=(select max(b.i9999) from "+destab+" b where ");
				sql.append(" b.a0100=a.a0100 and b.a0100 in (select a0100 from "+strctab+strSubCondition+") ) and a0100  in (select a0100 from  "+strctab+strSubCondition+ ")  ) bb ");
				sql.append(" where aa.a0100=bb.a0100 and "+sql3.substring(4));
				
				ArrayList valuesList=null;
				String update_str=" update  "+strctab+ " set updatetype=0 where a0100=? and lower(basepre)=? ";
				if(strctab.equalsIgnoreCase("templet_"+this.tabId))
					update_str+=" and ins_id=?";
				if(opt!=1)
				{
					
					rowSet=dao.search(sql.toString());
					valuesList=new ArrayList();
					while(rowSet.next())
					{
						ArrayList valueList=new ArrayList();
						String a0100=rowSet.getString("a0100");
						String ins_id=rowSet.getString("ins_id");
						valueList.add(a0100);
						valueList.add(nbase.toLowerCase());
						if(strctab.equalsIgnoreCase("templet_"+this.tabId))
							valueList.add(ins_id);	
						valuesList.add(valueList);
					}
					if(valuesList.size()>0)
						dao.batchUpdate(update_str.toString(),valuesList);
				}
				//没填值，也不增 （新加一种不增记录的情况）
					
				sqlnull.append(" ( "); 
				sqlnull.append("select "+sql1.substring(1)+" from "+strctab+strSubCondition);  
				sqlnull.append(""+sql4);
				if(opt==1) //仅判断 按条件更新没填值，也不增 （新加一种不增记录的情况）
					sqlnull.append(" and updatetype=3 ");
				else
					sqlnull.append(" and ( updatetype=1 or updatetype=2 ) ");
				sqlnull.append(" ) a ");
				rowSet=dao.search(sqlnull.toString());
				valuesList=new ArrayList();
				while(rowSet.next())
				{
					ArrayList valueList=new ArrayList();
					String a0100=rowSet.getString("a0100");
					String ins_id=rowSet.getString("ins_id");
					valueList.add(a0100);
					valueList.add(nbase.toLowerCase());
					if(strctab.equalsIgnoreCase("templet_"+this.tabId))
						valueList.add(ins_id);	
					valuesList.add(valueList);
				}
				update_str=" update  "+strctab+ " set updatetype=0,nullvalue=1 where a0100=? and lower(basepre)=? ";
				if(strctab.equalsIgnoreCase("templet_"+this.tabId))
					update_str+=" and ins_id=?";
				if(valuesList.size()>0)
					dao.batchUpdate(update_str.toString(),valuesList); 
				
				
			}
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
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
	 * @param updatetype_str  and updatetype=1
	 * @param isHaveSubsetFiled 公式中是否包含子集指标
	 */
	private void AutoBatchCreateNextRecord(String setname,String basepre,int refPreRec,String srctabname,String strSubCondition,String updatetype_str, boolean isHaveSubsetFiled)throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			String srctab=basepre+setname;
			String alias=setname;
			String strfields=getFieldString(setname);
			StringBuffer strsql=new StringBuffer();
			ArrayList paralist=new ArrayList();
			String key_field="a0100"; 
			
			//此人无子集记录
			/***
			 * bug 54553 changxy
			 * 原因：子集插入记录  按条件更新时  子集记录人为空的情况 新增一条空记录 
			 *  此方法下方代码中有针对按条件更新时，子集有记录但是不满足更新条件的情况 新增一条记录
			 * 两个插入针对子集是否有记录区分
			 */
			//if(!isHaveSubsetFiled) {  
				strsql.append("insert into ");
				strsql.append(srctab);
				strsql.append(" ("+key_field+",i9999 ,createtime,createusername ) ");
				strsql.append("select a0100,1,"+Sql_switcher.sqlNow()+",'"+this.userView.getUserName()+"'");
				strsql.append(" from "+srctabname+strSubCondition+" and currentI9999=0 and updatetype<>0 "); 
				
				if(updatetype_str.indexOf("updatetype=1")!=-1) //不是按条件更新  治面貌子集在档案库信息无记录，此子集指标设置的是更新当前记录，在表单处不维护值，提交入库之后会新增一条空记录
				{
					strsql.append(" and nullvalue=0 ");
				}
				
				dao.update(strsql.toString());
			//}
			strsql.setLength(0);
		
			
			strsql.append("insert into ");
			strsql.append(srctab);
			strsql.append(" ("+key_field+",i9999"); 
			if(refPreRec==1&&strfields.length()!=0)
			{
					strsql.append(",");
					strsql.append(strfields);
			}  
			strsql.append(",createtime,createusername ) "); 
			strsql.append("select a0100,i9999+1");
			if(refPreRec==1&&strfields.length()!=0)
			{
					strsql.append(",");
					strsql.append(strfields);
			}  
			strsql.append(","+Sql_switcher.sqlNow()+",'"+this.userView.getUserName()+"' from "+srctab+" where exists (select null "); 
			strsql.append(" from "+srctabname+strSubCondition+" and "+srctab+".a0100="+srctabname+".a0100  and "+srctab+".i9999="+srctabname+".currentI9999   and currentI9999<>0 "+updatetype_str+" )"); 
			dao.update(strsql.toString());
			strsql.setLength(0);
			if(!isHaveSubsetFiled) {
				dao.update("update "+srctabname+" set currentI9999=currentI9999+1  "+strSubCondition+" and currentI9999<>0 "+updatetype_str+" ");
				dao.update("update "+srctabname+" set currentI9999=1  "+strSubCondition+" and currentI9999=0 and updatetype<>0   ");
			}else {
				dao.update("update "+srctabname+" set currentI9999=currentI9999+1 where exists(select null from "+srctab+strSubCondition+" and currentI9999<>0 "+updatetype_str+" )");
				dao.update("update "+srctabname+" set currentI9999=1  where exists(select null from "+srctab+strSubCondition+" and currentI9999=0 and updatetype<>0 )");
			}
		 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
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
				if(i!=0)
					fields.append(",");
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
		boolean isDameng = Sql_switcher.searchDbServerFlag()==Constant.DAMENG;
		/***/
		int n=0;
		for(int i=0;i<fieldlist.size();i++)
		{
			
			String field_name=(String)fieldlist.get(i);
			  
			if(field_name.length()>2&& "t_".equalsIgnoreCase(field_name.trim().substring(0,2)))
				continue;
			switch(db_type)
			{
			case 2://ORACLE
			case 3://DB2
				if(n!=0)
				{
					strupdate_s.append(",");
					strupdate_d.append(",");
				}
				
				FieldItem item=DataDictionary.getFieldItem(field_name);
				if(isDameng && item!=null && "M".equalsIgnoreCase(item.getItemtype())){
					String char_field_name = Sql_switcher.sqlToChar(srcTab+"."+field_name+"_2");
					strupdate_s.append(char_field_name);
					strupdate_s.append(" ");
					strupdate_s.append(fieldlist.get(i));
					strupdate_s.append("_2");
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
				if(n!=0)
					strupdate.append(",");
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
				if(strupdate_d.length()>0)
					strupdate.append(",");
				strupdate.append("modtime,modusername)=(select ");
				strupdate.append(strupdate_s.toString());
				if(db_type==2)
				{
					if(strupdate_d.length()>0)
						strupdate.append(",");
					strupdate.append(Sql_switcher.sqlNow()+",'");
				//	strupdate.append(",getdate(),'");
				}
				else
				{
					if(strupdate_d.length()>0)
						strupdate.append(",");
					strupdate.append("Current Timestamp,'");
				}
				strupdate.append(this.userView.getUserName());
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
			if(strupdate.length()>0)
				strupdate.append(",");
			strupdate.append("modtime="+Sql_switcher.sqlNow()+",modusername='"+this.userView.getUserName()+"'");
		}
		
		return strupdate.toString();
	}

    
    
    
    
    
    
	
	
	
	
	
	
	
	
	
	
	
	
    
    public String getTableName(String moduleId,int tabId,String taskIds) {
        return this.utilBo.getTableName(moduleId, tabId, taskIds);
    }
	
	
	
	public TemplateUtilBo getUtilBo() {
		return utilBo;
	}
	public void setUtilBo(TemplateUtilBo utilBo) {
		this.utilBo = utilBo;
	}
	public TemplateParam getParamBo() {
		return paramBo;
	}
	
	
	public boolean isOnlyComputeFieldVar() {
        return onlyComputeFieldVar;
    }

    public void setOnlyComputeFieldVar(boolean onlyComputeFieldVar) {
        this.onlyComputeFieldVar = onlyComputeFieldVar;
    }

    public String getBz_tablename() {
        if (bz_tablename==null || bz_tablename.length()<1){
            this.bz_tablename = this.utilBo.getTableName(this.moduleId, this.tabId, this.taskId);
           // setBz_tablename(this.userView.getUserName()+"templet_"+this.tabId);
        }
        return bz_tablename;
    }

    public void setBz_tablename(String bz_tablename) {
        this.bz_tablename = bz_tablename;
    }


    public String getImpOthTableName() {
        return impOthTableName;
    }

    public void setImpOthTableName(String impOthTableName) {
        this.impOthTableName = impOthTableName;
    }

    public ArrayList getTasklist() {
        ArrayList tasklist = new ArrayList();
        if (!"0".equals(this.taskId)){
            String [] str = this.taskId.split(",");
            tasklist =new ArrayList(Arrays.asList(str));
        }
        return tasklist;
    }


    public void setComputeVar(boolean isComputeVar) {
        this.isComputeVar = isComputeVar;
    }

    public boolean isComputeVar() {
        return isComputeVar;
    }
    
    /**
     * 是否是自助业务申请
     * */
    public boolean isSelfApply() {
        return "9".equals(this.moduleId)&& "0".equals(this.taskId);
    }
    
    
    public String getModuleId() {
        return moduleId;
    }
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
	public boolean isThrow() {
		return isThrow;
	}
	public void setThrow(boolean isThrow) {
		this.isThrow = isThrow;
	}
	
	
	public String getInsid() {
		return insid;
	}
	public void setInsid(String insid) {
		this.insid = insid;
	}
	/**
	 * 更改初始模板数据
	 * @param a0100list 人员编号
	 * @param tableName	模板表名
	 * @param map		要修改的参数数据
	 * @return
	 * @throws GeneralException
	 */
	public boolean updateInitValue(ArrayList a0100list,String tableName, HashMap map)throws GeneralException
	{
		if (null==map || map.isEmpty()){
		    return false;
		}
		boolean bflag=true;
		try { 
    		ArrayList itemlist = getAllFieldItem();
    		ArrayList valuelist = new ArrayList();
    		StringBuffer sql = new StringBuffer("");
    		sql.append(" update ").append(tableName);
    		sql.append(" set ");
    		boolean sqlbool = false;
//    		DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    		for(int i=0;i<itemlist.size();i++) {
    			FieldItem item = (FieldItem)itemlist.get(i);
    			String itemid = item.getItemid();
    			if(null != map.get(itemid.toUpperCase())) {
    				String type = item.getItemtype();
    				String value = (String)map.get(itemid.toUpperCase());
    				sql.append(itemid).append("=");
    				
    				if("D".equalsIgnoreCase(type)) {
    					sql.append(Sql_switcher.dateValue(value)).append(",");
    				}else if("N".equalsIgnoreCase(type)){
    					sql.append(value).append(",");
    				}else {
    					sql.append("'").append(value).append("',");
    				}
    				sqlbool = true;
    			}
    		}
    		// 若传的参数对应不上则不更改初始模板参数
    		if(sqlbool) {
    			sql = new StringBuffer(sql.substring(0, sql.toString().length()-1));
    			sql.append(" where a0100=").append(getA0100String(a0100list).toString());
    			sql.append(" and lower(basepre)=? ");
    			valuelist.add(this.userView.getDbname().toLowerCase());
    			dao.update(sql.toString(), valuelist);
    		}
		}
		catch(Exception e)
		{
			bflag=false;
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return bflag;		
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
	
	
	//----------------------------------   支持子集计算---------------------------------------------
	
	/**
	 * 子集计算
	 * @param strWhere
	 * @throws GeneralException
	 */
	private void calSubFormula(String strWhere)throws GeneralException
	{
		H2JdbcUtil h2 = new H2JdbcUtil();
		try { 
			//查询表单中是否有数据
			boolean isHaveData = checkIsHaveData(strWhere);
			if(!isHaveData) {
				return;
			}
			//读取公式列表
			ArrayList formulalist = readFormula(); 
			//读取临时变量
			ArrayList fieldlist_mid = getMidVariableList();
			//获得模板需参与计算的子集信息
			ArrayList subSetInfoList = getSubSetByFormula(formulalist);
			//将流程涉及计算的子集数据同步至H2中
			ArrayList subsetdatatablelist = insertSubDateToH2(subSetInfoList,strWhere,fieldlist_mid,h2);
			//调用算法分析器解析公式，生成sql，先计算子集列计算公式，再计算子集统计公式
			calyksjParser(h2,fieldlist_mid,subSetInfoList);
			//将计算完的值写回到子集xml中
			wirteBackDateToXML(h2,subsetdatatablelist,subSetInfoList,strWhere);
			//将计算完的统计公式的值写入到临时表中
			wirteBackDateToLin(h2,subSetInfoList,strWhere);
			//计算完成清除计算临时表
			dropTable_h2(h2);
		}
		catch(Exception e)
		{  
			e.printStackTrace();
			dropTable_h2(h2);
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 查询表单中是否有数据
	 * @param strWhere
	 * @return
	 * @throws GeneralException 
	 */
	private boolean checkIsHaveData(String strWhere) throws GeneralException {
		RowSet rset = null;
		boolean isHaveData = false;
	    try {
			ContentDAO dao=new ContentDAO(conn);
			String sql="select 1 from "+this.bz_tablename+" "+strWhere;
			rset = dao.search(sql);
			if(rset.next()) {
				isHaveData = true;
			}
	    }catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rset);
		}
		return isHaveData;
	}
	/**
	 * 计算完成清除计算临时表
	 * @param h2 
	 * @throws GeneralException 
	 */
	private void dropTable_h2(H2JdbcUtil h2) throws GeneralException {
		try {
			for(int i=0;i<h2Table.size();i++) {
				String tablename = (String) h2Table.get(i);
				if(H2JdbcUtil.isExistTable(tablename,false))
		    	    H2JdbcUtil.dropTable(tablename);
			}
			h2Table.clear();
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 将计算完的统计公式的值写入到临时表中
	 * @param h2
	 * @param subSetInfoList
	 * @param strWhere
	 * @throws GeneralException 
	 */
	private void wirteBackDateToLin(H2JdbcUtil h2, ArrayList subSetInfoList, String strWhere) throws GeneralException {
		RowSet rset = null;
		RowSet rset1 = null;
		try {
			//得到记录变动日志相关设置参数
			ArrayList cellList = new ArrayList();
			Boolean isAotuLog = paramBo.getIsAotuLog();
			Boolean isRejectAotuLog = paramBo.getIsRejectAotuLog();
			if(isRejectAotuLog==true&&!"0".equalsIgnoreCase(this.insid)){
				Boolean haveReject= utilBo.isHaveRejectTaskByInsId(this.insid);
				if(haveReject){
					isAotuLog=true;
				}
			}
			if (isAotuLog && !("0".equalsIgnoreCase(this.insid)
					&& (paramBo.getOperationType() == 0|| paramBo.getOperationType() == 5))) {
				cellList= utilBo.getAllCell(this.tabId);
			}
			String tablename_cal = this.userView.getUserName()+"_template_cal";//计算用临时表
			StringBuffer select_id=new StringBuffer(""); 
			if(this.paramBo.getInfor_type()==2)
				select_id.append("b0110");
			else if(this.paramBo.getInfor_type()==3)
				select_id.append("e01a1");
			else if(this.paramBo.getInfor_type()==1)
				select_id.append("a0100,basepre");
			String sql="select "+select_id.toString()+" from "+this.bz_tablename+" "+strWhere;
			rset = dao.search(sql);
			while(rset.next()) {
				String objectid = "";
				if(this.paramBo.getInfor_type()==1) {
					String basepre = rset.getString("basepre");
					String a0100 = rset.getString("a0100");
					objectid = basepre+a0100;
				}else if(this.paramBo.getInfor_type()==2) {
					objectid = rset.getString("b0110");
				}else if(this.paramBo.getInfor_type()==3) {
					objectid = rset.getString("e01a1");
				}
				for(Iterator t=subSetInfoList.iterator();t.hasNext();)
				{
					ArrayList fieldFields = new ArrayList();
					LazyDynaBean abean=(LazyDynaBean)t.next(); 
					String subdomainid=(String)abean.get("subdomainid");
					int chgstate=(Integer)abean.get("chgstate");
					String setname=(String)abean.get("setname"); 
					String type = (String) abean.get("type");
					if("1".equals(type)) { // 1:统计子集指标计算公式     2：子集四则运算公式
						LazyDynaBean sub_info = (LazyDynaBean) abean.get("sub_info");
						String fieldname=(String)sub_info.get("lexpr");
						String sql_ = "select "+fieldname+" from "+tablename_cal+" where objectid='"+objectid+"'";
						rset1 = H2JdbcUtil.search(sql_, new ArrayList());
						if(rset1.next()) {
							String updAutoLotObjectid = "";
							ArrayList updFieldList=new ArrayList();//要修改的字段
				            ArrayList updDataList=new ArrayList(); //要修改字段对应的数据
				            ArrayList updAutoLogSetBoList=new ArrayList();
							FieldItem fielditem=DataDictionary.getFieldItem(fieldname.substring(0, 5));
							String itemtype = fielditem.getItemtype();//类型
							ArrayList params = new ArrayList();
							String updateSql = "update "+this.bz_tablename+" set "+fieldname+"=? where ";
							if(this.paramBo.getInfor_type()==1) {
								String basepre = rset.getString("basepre");
								String a0100 = rset.getString("a0100");
								updateSql+=" basepre='"+basepre+"' and a0100='"+a0100+"'";
								updAutoLotObjectid = basepre+"`"+a0100;
							}else if(this.paramBo.getInfor_type()==2) {
								String b0110 = rset.getString("b0110");
								updateSql+=" b0110='"+b0110+"'";
								updAutoLotObjectid = b0110;
							}else if(this.paramBo.getInfor_type()==3) {
								String e01a1 = rset.getString("e01a1");
								updateSql+=" e01a1='"+e01a1+"'";
								updAutoLotObjectid = e01a1;
							}
							if("A".equalsIgnoreCase(itemtype)) {//字符型
								params.add(rset1.getString(fieldname));
								updDataList.add(rset1.getString(fieldname));
							}
							else if("N".equalsIgnoreCase(itemtype)) {//数值型
								params.add(rset1.getDouble(fieldname));
								String disValue = rset1.getDouble(fieldname)+"";
								String decimal=disValue.substring(disValue.indexOf(".")+1);
		                    	if(Integer.parseInt(decimal, 10)==0&&disValue.indexOf(".")!=-1)
		                    	{
		                    		disValue=disValue.substring(0,disValue.indexOf("."));
		                    		updDataList.add(disValue);
		                    	}else
		                    		updDataList.add(rset1.getDouble(fieldname));
							}
							else if("D".equalsIgnoreCase(itemtype)) {//时间
								params.add(rset1.getDate(fieldname));
								updDataList.add(rset1.getDate(fieldname));
							}
							else if("M".equalsIgnoreCase(itemtype)) {//大文本
								params.add(rset1.getString(fieldname));
								updDataList.add(rset1.getString(fieldname));
							}
							//添加变动日志
							if(isAotuLog&&!("0".equalsIgnoreCase(this.insid)&&(paramBo.getOperationType()==0||paramBo.getOperationType()==5))){
								updFieldList.add(fieldname);
								for(int k=0;k<cellList.size();k++){
			            			TemplateSet setBo = (TemplateSet) cellList.get(k);
			            			String tableFieldName = setBo.getTableFieldName();
			            			if(fieldname.equalsIgnoreCase(tableFieldName)){
			            				updAutoLogSetBoList.add(setBo);
			            				break;
			            			}
			            		}
								TempletChgLogBo chgLogBo=new TempletChgLogBo(this.conn,this.userView,paramBo);
			                		chgLogBo.createTemplateChgLogTable("templet_chg_log");
			                		String realTask_id= this.taskId;
			                		if(StringUtils.isBlank(realTask_id)){
			                			realTask_id="0";
			                		}
			                		if(taskId.indexOf(",")==-1&&StringUtils.isNotBlank(taskId)){
			                			realTask_id=taskId;
			                		}
								 chgLogBo.insertOrUpdateAllLogger(updFieldList, updAutoLogSetBoList, updDataList,
										this.insid, realTask_id, updAutoLotObjectid, getBz_tablename(),
										paramBo.getInfor_type());
		                	}
							dao.update(updateSql,params);
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rset);
			PubFunc.closeDbObj(rset1);
		}
	}
	/**
	 * 将计算完的值写回到子集xml中
	 * @param h2
	 * @param subsetdatatablelist
	 * @param subSetInfoList 
	 * @param strWhere 
	 * @throws GeneralException 
	 */
	private void wirteBackDateToXML(H2JdbcUtil h2, ArrayList subsetdatatablelist, ArrayList subSetInfoList, String strWhere) throws GeneralException {
		RowSet rset = null;
		RowSet rset1 = null;
		try {
			ContentDAO dao=new ContentDAO(this.conn);
			//得到记录变动日志相关设置参数
			ArrayList cellList = new ArrayList();
			Boolean isAotuLog = paramBo.getIsAotuLog();
			Boolean isRejectAotuLog = paramBo.getIsRejectAotuLog();
			if(isRejectAotuLog==true&&!"0".equalsIgnoreCase(this.insid)){
				Boolean haveReject= utilBo.isHaveRejectTaskByInsId(this.insid);
				if(haveReject){
					isAotuLog=true;
				}
			}
			if (isAotuLog && !("0".equalsIgnoreCase(this.insid)
					&& (paramBo.getOperationType() == 0|| paramBo.getOperationType() == 5))) {
				cellList= utilBo.getAllCell(this.tabId);
			}
			for(int i=0;i<subsetdatatablelist.size();i++) {
				String subsettablename = (String) subsetdatatablelist.get(i);
				StringBuffer select_id=new StringBuffer(""); 
				if(this.paramBo.getInfor_type()==2)
					select_id.append("b0110");
				else if(this.paramBo.getInfor_type()==3)
					select_id.append("e01a1");
				else if(this.paramBo.getInfor_type()==1)
					select_id.append("a0100,basepre");
				
				for(int j=0;j<subSetInfoList.size();j++) {
					LazyDynaBean abean=(LazyDynaBean)subSetInfoList.get(j); 
					String subdomainid=(String)abean.get("subdomainid");
					int chgstate=(Integer)abean.get("chgstate");
					String setname=(String)abean.get("setname"); 
					String type=(String)abean.get("type"); 
					String key="t_"+setname;
					String key1 = setname;
					if(subdomainid.length()>0) {
						key+="_"+subdomainid;
						key1+="_"+subdomainid;
					}
					key+="_"+chgstate;
					key1+="_"+chgstate;
					if("2".equals(type)) {
						ArrayList readonlyfields = new ArrayList();
						SubSetDomain subDomain=(SubSetDomain)abean.get("sub_info");
						ArrayList sublist=subDomain.getSubFieldList();
						//得到各列对应是否历史记录只读
						for(int nn=0;nn<sublist.size();nn++) {
							SubField subtable=(SubField)sublist.get(nn);
							String fieldname = subtable.getFieldname();
							String his_readonly = subtable.getHis_readonly();
							if("true".equalsIgnoreCase(his_readonly)) {
								readonlyfields.add(fieldname.toLowerCase());
							}
						}
						String tablename = this.userView.getUserName()+"_"+tabId+"_"+key1;
						if(subsettablename.equalsIgnoreCase(tablename)) { 
							select_id.append(","+key);
							String sql="select "+select_id.toString()+" from "+this.bz_tablename+" "+strWhere;
							rset = dao.search(sql);
							while(rset.next()) {
								String objectid = "";
								if(this.paramBo.getInfor_type()==1) {
									String basepre = rset.getString("basepre");
									String a0100 = rset.getString("a0100");
									objectid = basepre+a0100;
								}else if(this.paramBo.getInfor_type()==2) {
									objectid = rset.getString("b0110");
								}else if(this.paramBo.getInfor_type()==3) {
									objectid = rset.getString("e01a1");
								}
								String dataxml = rset.getString(key);
								
								//读取h2表中数据
								String sql_ = "select * from "+tablename+" where objectid='"+objectid+"'";
								rset1 = H2JdbcUtil.search(sql_, new ArrayList());
								ArrayList recordList = new ArrayList();
								while(rset1.next()) {
									int i9999 = rset1.getInt("i9999");
									String timestamp = rset1.getString("timestamp");
									HashMap map = new HashMap();
									map.put("i9999", i9999+"");
									map.put("timestamp", timestamp);
									for(Iterator k=sublist.iterator();k.hasNext();)
									{
										SubField subtable=(SubField)k.next();
										String field_name = subtable.getFieldname();
										FieldItem item=DataDictionary.getFieldItem(field_name);
										String value = "";
										if(item!=null) {
											String itemtype = item.getItemtype();//类型
											if("A".equalsIgnoreCase(itemtype)) {//字符型
												value = rset1.getString(field_name);
											}
											else if("N".equalsIgnoreCase(itemtype)) {//数值型
												value = rset1.getDouble(field_name)+"";
											}
											else if("D".equalsIgnoreCase(itemtype)) {//时间
												value = PubFunc.FormatDate(rset1.getDate(field_name));
											}
											else if("M".equalsIgnoreCase(itemtype)) {//大文本
												value = rset1.getString(field_name);
											}
										}else if("attach".equalsIgnoreCase(field_name)) {
											value = rset1.getString(field_name);
										}
										map.put(field_name.toLowerCase(), value);
									}
									recordList.add(map);
								}
								if(recordList.size()>0) {
									//解析子集xml
									String updAutoLotObjectid = "";
									dataxml=dataxml.replace("&", "＆");
									Document doc=PubFunc.generateDom(dataxml);
									XMLOutputter outputter = new XMLOutputter();
									String xpath="/records";
									XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
									Element eleRoot =(Element) findPath.selectSingleNode(doc);
									if(eleRoot!=null){
										String columns=eleRoot.getAttributeValue("columns").toUpperCase(); //得到xml中columns中对应的值
										List recordlist=eleRoot.getChildren("record");
										if(recordlist!=null&&recordlist.size()>0)
										{
											for(int n=0;n<recordlist.size();n++)
											{
												Element element=(Element)recordlist.get(n);
												String I9999=element.getAttributeValue("I9999");
												String timestamp = element.getAttributeValue("timestamp")==null?"":element.getAttributeValue("timestamp");
												String contentValue=element.getValue();
												HashMap recordMap = new HashMap();
												for(int m=0;m<recordList.size();m++) {
													HashMap map = (HashMap) recordList.get(m);
													if(I9999.equals(map.get("i9999"))) {
														if("-1".equals(I9999)&&timestamp.equals(map.get("timestamp"))) {
															recordMap = map;
															break;
														}else if(!"-1".equals(I9999)) {
															recordMap = map;
															break;
														}
													}
												}
												String contentvalue_new = "";
												if(contentValue!=null&&contentValue.length()>0)
												{
													String[] valueArr=contentValue.split("`",-1);
													String[] columnArr=columns.split("`");
													for(int u=0;u<columnArr.length;u++){
														String column = columnArr[u].toLowerCase();
														String value_new = (String) recordMap.get(column);
														String value_old = valueArr[u];
														if(!"-1".equals(I9999)&&readonlyfields.contains(column)) {//证明是原始记录行设置了列历史记录只读
															contentvalue_new+=value_old+"`";
														}else {
															if(StringUtils.isNotBlank(value_new)&&!value_new.equals(value_old)) {
																value_new = formatDecimal(value_new,sublist,column);
																contentvalue_new+=value_new+"`";
															}else {
																value_old = formatDecimal(value_old,sublist,column);
																contentvalue_new+=value_old+"`";
															}
														}
													}
												}
												if(StringUtils.isNotBlank(contentvalue_new)) {
													contentvalue_new = contentvalue_new.substring(0,contentvalue_new.length()-1);
												}
												element.setText(contentvalue_new);
												element.setAttribute("isHaveChange", "true");
											}
										}
									}
									dataxml = outputter.outputString(doc);
									//更新子集xml数据
									ArrayList paramlist = new ArrayList();
									String updateSql = "update "+this.bz_tablename+" set "+key+"=? where ";
									if(this.paramBo.getInfor_type()==1) {
										String basepre = rset.getString("basepre");
										String a0100 = rset.getString("a0100");
										updateSql+=" basepre='"+basepre+"' and a0100='"+a0100+"'";
										updAutoLotObjectid = basepre+"`"+a0100;
									}else if(this.paramBo.getInfor_type()==2) {
										String b0110 = rset.getString("b0110");
										updateSql+=" b0110='"+b0110+"'";
										updAutoLotObjectid = b0110;
									}else if(this.paramBo.getInfor_type()==3) {
										String e01a1 = rset.getString("e01a1");
										updateSql+=" e01a1='"+e01a1+"'";
										updAutoLotObjectid = e01a1;
									}
									paramlist.add(dataxml);
									//添加变动日志
									if(isAotuLog&&!("0".equalsIgnoreCase(this.insid)&&(paramBo.getOperationType()==0||paramBo.getOperationType()==5))){
										ArrayList updFieldList=new ArrayList();//要修改的字段
							            ArrayList updDataList=new ArrayList(); //要修改字段对应的数据
							            ArrayList updAutoLogSetBoList=new ArrayList();
										updFieldList.add(key);
										for(int k=0;k<cellList.size();k++){
					            			TemplateSet setBo = (TemplateSet) cellList.get(k);
					            			String tableFieldName = setBo.getTableFieldName();
					            			if(key.equalsIgnoreCase(tableFieldName)){
					            				updAutoLogSetBoList.add(setBo);
					            				break;
					            			}
					            		}
										updDataList.add(dataxml);
										TempletChgLogBo chgLogBo=new TempletChgLogBo(this.conn,this.userView,paramBo);
					                		chgLogBo.createTemplateChgLogTable("templet_chg_log");
					                		String realTask_id= this.taskId;
					                		if(StringUtils.isBlank(realTask_id)){
					                			realTask_id="0";
					                		}
					                		if(taskId.indexOf(",")==-1&&StringUtils.isNotBlank(taskId)){
					                			realTask_id=taskId;
					                		}
										 chgLogBo.insertOrUpdateAllLogger(updFieldList, updAutoLogSetBoList, updDataList,
												this.insid, realTask_id, updAutoLotObjectid, getBz_tablename(),
												paramBo.getInfor_type());
				                	}
									dao.update(updateSql,paramlist);
								}
							}
							break;
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rset);
			PubFunc.closeDbObj(rset1);
		}
	}
	/**
	 * 按照设置格式化值
	 * @param value
	 * @param sublist
	 * @param column
	 * @return
	 */
	private String formatDecimal(String value, ArrayList sublist, String column) {
		FieldItem item=DataDictionary.getFieldItem(column);
		if(item!=null) {
			if("N".equalsIgnoreCase(item.getItemtype())) {
				String slop = "";
				int ndec=item.getDecimalwidth();//小数点位数
				for(int nn=0;nn<sublist.size();nn++) {
					SubField subtable=(SubField)sublist.get(nn);
					String field_name = subtable.getFieldname();
					if(field_name.equalsIgnoreCase(column)) {
						slop = subtable.getSlop();
						break;
					}
				}
				if(StringUtils.isNotBlank(slop)) {
					if(Integer.parseInt(slop)<ndec) {
						ndec = Integer.parseInt(slop);
					}
				}
				value=PubFunc.DoFormatDecimal(value,ndec);
			}
		}
		return value;
	}
	/**
	 * 调用算法分析器解析公式，生成sql，先计算子集列计算公式，再计算子集统计公式
	 * @param h2 
	 * @param fieldlist_mid 
	 * @param subSetInfoList 
	 * @throws GeneralException 
	 */
	private void calyksjParser(H2JdbcUtil h2, ArrayList fieldlist_mid, ArrayList subSetInfoList) throws GeneralException {
		RowSet rset=null;
		RowSet rset1=null;
		HashMap subsetFieldMap = new HashMap();
		try {
			ArrayList paramlist = new ArrayList();
			paramlist.add(tabId);
			String sql = "select * from TEMPLATE_SUBSET_DETAIL where tabid=?";
			rset = H2JdbcUtil.search(sql, paramlist);
			while(rset.next()) {
				String subsetname = rset.getString("subsetname");
				String subsetdesc = rset.getString("subsetdesc");
				String field_id= rset.getString("field_id");
				String field_type = rset.getString("field_type");
				String formula = rset.getString("formula");
				String cond = rset.getString("cond");
				String target = rset.getString("target");
				String field_desc = "";
				//得到指标涉及的子集下的所有指标
				ArrayList fieldFields = new ArrayList();
				if(!subsetFieldMap.containsKey(subsetname)) {
					ArrayList paramlist_ = new ArrayList();
					String sql_ = "select field_id from TEMPLATE_SUBSET_DETAIL where tabid=? and subsetname=?";
					paramlist_.add(tabId);
					paramlist_.add(subsetname);
					rset1 = H2JdbcUtil.search(sql_, paramlist_);
					while(rset1.next()) {
						String fieldid = rset1.getString("field_id");
						if(StringUtils.isNotBlank(fieldid)) {
							FieldItem item=DataDictionary.getFieldItem(fieldid);
							fieldFields.add(item);
						}
					}
					subsetFieldMap.put(subsetname, fieldFields);
				}else {
					fieldFields = (ArrayList) subsetFieldMap.get(subsetname);
				}
				if(StringUtils.isNotBlank(field_id)&&StringUtils.isNotBlank(formula)) {//子集列有计算公式
					//计算条件
					YksjParser yp = null;
					String strfilter="";
					if((cond!=null  && cond.length()>0)){
						yp = new YksjParser( this.userView ,fieldFields,
								YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson, "Ht", "");
						yp.run_where(cond);
						strfilter=yp.getSQL();
					}			
					StringBuffer strcond=new StringBuffer();
					if(!("".equalsIgnoreCase(strfilter))){
						if(strcond.length()>0)
							strcond.append(" and ");
						else
							strcond.append(" where ");
						strcond.append(strfilter);
					}
					//计算公式
					yp = new YksjParser(this.userView, fieldFields,
							YksjParser.forNormal, getDataType(field_type), YksjParser.forPerson, "Ht", "");
					String tablename = this.userView.getUserName()+"_"+tabId+"_"+subsetname.substring(subsetname.indexOf("t_")+2, subsetname.length());
					yp.setDataBaseType("h2");
					yp.run(formula);
					String sql_filter = yp.getSQL();
					//更新每个人对应的列值
					if(H2JdbcUtil.isExistTable(tablename,false)) {
						String updatesql = "update "+tablename+" set "+field_id+" = "+ sql_filter +strcond;
						try{
							H2JdbcUtil.execute(updatesql);
						}
						catch(Exception cex){
							FieldItem item=DataDictionary.getFieldItem(field_id);
							if(item!=null)
								field_desc = item.getItemdesc();
							String message=cex.toString();
							if(message.indexOf("Value too long for column")!=-1)
								throw GeneralExceptionHandler.Handle(new Exception("子集【"+subsetdesc+"】的列【"+field_desc+"】公式计算值长度大于目标指标的长度，请修改列公式再进行计算！"));
							else
								throw GeneralExceptionHandler.Handle(cex);
						}
					}
				}
			}
			//计算子集统计函数
			for(Iterator t=subSetInfoList.iterator();t.hasNext();)
			{
				ArrayList fieldFields = new ArrayList();
				LazyDynaBean abean=(LazyDynaBean)t.next(); 
				String subdomainid=(String)abean.get("subdomainid");
				int chgstate=(Integer)abean.get("chgstate");
				String setname=(String)abean.get("setname"); 
				String type = (String) abean.get("type");
				String key=setname;
				if(subdomainid.length()>0)
					key+="_"+subdomainid;
				key+="_"+chgstate;
				if("1".equals(type)) { // 1:统计子集指标计算公式     2：子集四则运算公式
					LazyDynaBean sub_info = (LazyDynaBean) abean.get("sub_info");
					String fieldname=(String)sub_info.get("lexpr");
					String formula=(String)sub_info.get("rexpr");
					String cond="";
					SubSetDomain subDomain=(SubSetDomain)sub_info.get("subdomain");
					ArrayList sublist=subDomain.getSubFieldList();
					for(Iterator i=sublist.iterator();i.hasNext();)
					{
						SubField subtable=(SubField)i.next();
						String field_name = subtable.getFieldname();
						FieldItem item=DataDictionary.getFieldItem(field_name);
						if(item!=null){
							FieldItem item_=(FieldItem) item.cloneItem();
							item_.setItemdesc(subtable.getTitle());
							fieldFields.add(item_);
						}
					}
					ArrayList itemlist = this.getAllFieldItem();
					HashMap<String, String> map_fieldSet = utilBo.getFieldSetMap(tabId);
					for(int i=0;i<itemlist.size();i++){
						FieldItem field = (FieldItem)itemlist.get(i);
						String final_name = "";
						if(field==null)
							continue;
						
						String itemdesc = field.getItemdesc();
						//对于子集名称在cs端修改过的，需要用修改的
						if(field.getVarible()==2) {
							String hz = map_fieldSet.get(field.getItemid().toLowerCase());
							if(!hz.equalsIgnoreCase(itemdesc)) {
								final_name = hz;
							}
						}
						
						if(StringUtils.isBlank(final_name) && field.isChangeAfter()){
							final_name = ResourceFactory.getProperty("inform.muster.to.be")+field.getItemdesc();
						}
						else if(StringUtils.isBlank(final_name) && field.isChangeBefore()){
							final_name = ResourceFactory.getProperty("inform.muster.now")+field.getItemdesc();
						}
						
						if(StringUtils.isNotBlank(final_name)) {
							field.setItemdesc(final_name);
						}
						fieldFields.add(field);
					}
					fieldFields.addAll(fieldlist_mid);
					YksjParser yp = null;
					FieldItem fielditem=DataDictionary.getFieldItem(fieldname.substring(0, 5));
					
					yp = new YksjParser(this.userView, fieldFields,
							YksjParser.forNormal, getDataType(fielditem.getItemtype()), YksjParser.forPerson, "Ht", "");
					String tablename_cal = this.userView.getUserName()+"_template_cal";
					String tablename = this.userView.getUserName()+"_"+tabId+"_"+key;
					yp.setStdTmpTable(tablename);
					yp.setDataBaseType("h2");
					//调用统计函数。。。
					yp.run(formula, null, fieldname,tablename_cal, dao, cond,conn, fielditem.getItemtype(), fielditem.getItemlength(),4, "");
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rset);
			PubFunc.closeDbObj(rset1);
		}
	}
	
	/**
	 * 将流程涉及计算的子集数据同步至H2中
	 * @param subSetInfoList
	 * @param strWhere
	 * @param fieldlist_mid
	 * @param h2
	 * @return
	 * @throws GeneralException 
	 */
	private ArrayList insertSubDateToH2(ArrayList subSetInfoList,String strWhere,ArrayList fieldlist_mid, H2JdbcUtil h2) throws GeneralException
	{
		ContentDAO dao=new ContentDAO(conn);
		ArrayList  list=new ArrayList();
		RowSet rset = null;
		RowSet rset1 = null;
		RowSet rset2 = null;
		try {
			LazyDynaBean abean=null; 
			String sql="select * from "+this.bz_tablename+" "+strWhere; 
			//解析子集对应列及对应的公式 创建表subsetdetail
			//是否需要插入数据
			boolean isneedinsert = false;
			/**有表的话先删除*/
	        if(H2JdbcUtil.isExistTable("TEMPLATE_SUBSET_DETAIL",false)) {
	        	//先判断与当前模板相关的数据有没有
	        	rset1 = H2JdbcUtil.search("select 1 from TEMPLATE_SUBSET_DETAIL where tabid="+tabId, new ArrayList());
	        	if(rset1.next()) {//有
	        		//再判断数据创建时间是否超过1分钟
		        	rset2 = H2JdbcUtil.search("select 1 from TEMPLATE_SUBSET_DETAIL where DATEDIFF(Minute,CREATE_TIME,CURRENT_TIMESTAMP)>1 and tabid="+tabId, new ArrayList());
					if(rset2.next()) {
						//h2.dropTable("TEMPLATE_SUBSET_DETAIL");
						H2JdbcUtil.execute("delete from TEMPLATE_SUBSET_DETAIL where tabid="+tabId);
						/*h2.execute("CREATE TABLE TEMPLATE_SUBSET_DETAIL(ID INT PRIMARY KEY auto_increment,TABID INT,SUBSETNAME VARCHAR(50),SUBSETDESC VARCHAR(150),FIELD_ID VARCHAR(50),FIELD_TYPE VARCHAR(5),"
								+ "FORMULA VARCHAR(3000),COND VARCHAR(3000),TARGET VARCHAR(50),CREATE_TIME TIMESTAMP);");*/
						isneedinsert = true;
					}else {
						//isneedinsert = true;
					}
	        	}else {//没有
	        		isneedinsert = true;
	        	}
	        }else {
	        	H2JdbcUtil.execute("CREATE TABLE TEMPLATE_SUBSET_DETAIL(ID INT PRIMARY KEY auto_increment,TABID INT,SUBSETNAME VARCHAR(50),SUBSETDESC VARCHAR(150),FIELD_ID VARCHAR(50),FIELD_TYPE VARCHAR(5),"
						+ "FORMULA VARCHAR(3000),COND VARCHAR(3000),TARGET VARCHAR(50),CREATE_TIME TIMESTAMP);");
	        	isneedinsert = true;
	        }
			String insertSqlForSubset = "insert into TEMPLATE_SUBSET_DETAIL (TABID,SUBSETNAME,SUBSETDESC,FIELD_ID,FIELD_TYPE,FORMULA,COND,TARGET,CREATE_TIME) values(?,?,?,?,?,?,?,?,?)";
			
			//创建计算用临时表
			String tablename_cal = this.userView.getUserName()+"_template_cal";//计算用临时表 每个操作用户点计算都重新建一次
			String createSql_cal = "CREATE TABLE "+tablename_cal+" ";
			createSql_cal+="(ID INT PRIMARY KEY auto_increment,OBJECTID VARCHAR(50),CREATE_TIME TIMESTAMP,CREATEUSER VARCHAR(50)";
			String insertSql_cal = "insert into "+tablename_cal+"(objectid,create_time,createuser";
			String insertSql_cal_end = "(?,?,?";
			//解析统计公式中条件涉及的指标和临时变量
			ArrayList fieldList = new ArrayList();
            ArrayList itemlist = this.getAllFieldItem();
            HashMap<String, String> map_fieldSet = utilBo.getFieldSetMap(tabId);
			for(int i=0;i<itemlist.size();i++){
				FieldItem field = (FieldItem)itemlist.get(i);
				String final_name = "";
				if(field==null)
					continue;
				
				String itemdesc = field.getItemdesc();
				//对于子集名称在cs端修改过的，需要用修改的
				if(field.getVarible()==2) {
					String hz = map_fieldSet.get(field.getItemid().toLowerCase());
					if(!hz.equalsIgnoreCase(itemdesc)) {
						final_name = hz;
					}
				}
				
				if(StringUtils.isBlank(final_name) && field.isChangeAfter()){
					final_name = ResourceFactory.getProperty("inform.muster.to.be")+field.getItemdesc();
				}
				else if(StringUtils.isBlank(final_name) && field.isChangeBefore()){
					final_name = ResourceFactory.getProperty("inform.muster.now")+field.getItemdesc();
				}
				
				if(StringUtils.isNotBlank(final_name)) {
					field.setItemdesc(final_name);
				}
				fieldList.add(field);
			}
            fieldList.addAll(fieldlist_mid);
            Set usedFieldid = new HashSet();
            ArrayList usedFieldList = new ArrayList();
			List<Object[]> listForSubset = new ArrayList<Object[]>();
			for(Iterator t=subSetInfoList.iterator();t.hasNext();)
			{
				abean=(LazyDynaBean)t.next(); 
				String subdomainid=(String)abean.get("subdomainid");
				int chgstate=(Integer)abean.get("chgstate");
				String setname=(String)abean.get("setname"); 
				String setdesc = (String)abean.get("setdesc");
				String type = (String) abean.get("type");
				String key="t_"+setname;
				if(subdomainid.length()>0)
					key+="_"+subdomainid;
				key+="_"+chgstate;
				if("1".equals(type)) { // 1:统计子集指标计算公式     2：子集四则运算公式
					LazyDynaBean sub_info = (LazyDynaBean) abean.get("sub_info");
					String fieldname=(String)sub_info.get("lexpr");
					String formula=(String)sub_info.get("rexpr");
					if(formula!=null&&formula.trim().length()>0)
					{
						Object [] object = new Object[9];
						object[0]=tabId;
						object[1]=key;
						object[2]=setdesc;
						object[3]="";
						object[4]="";
						object[5]=formula;
						object[6]="";
						object[7]=fieldname;
						object[8]=new Date();
						listForSubset.add(object);
						FieldItem item=DataDictionary.getFieldItem(fieldname.substring(0, 5));
						//调用计算类，获得条件包含的临时变量与指标
						SubSetDomain subDomain=(SubSetDomain)sub_info.get("subdomain");
						if(subDomain!=null) {
							ArrayList sublist=subDomain.getSubFieldList();
							for(Iterator i=sublist.iterator();i.hasNext();)
							{
								SubField subtable=(SubField)i.next();
								String field_name = subtable.getFieldname();
								FieldItem item1=DataDictionary.getFieldItem(field_name);
								if(item1!=null){
									FieldItem item1_=(FieldItem) item1.cloneItem();
									item1_.setItemdesc(subtable.getTitle());
									fieldList.add(0,item1_);
								}
							}
						}
						YksjParser yp = new YksjParser(this.userView, fieldList,
								YksjParser.forNormal, getDataType(item.getItemtype()), YksjParser.forPerson, "Ht", "");
						yp.setDataBaseType("h2");
						boolean b = yp.Verify_where(formula);
						if (!b){ //验证没有通过
							String errorMessage = yp.getStrError();
							throw GeneralExceptionHandler.Handle(new Exception(errorMessage));  
						}
						ArrayList statVarList = yp.getSubSetConditionFieldItemList();
						//将临时变量字段和其他指标加入到临时表中
						for(int ii=0;ii<statVarList.size();ii++)
						{
							FieldItem fielditem=(FieldItem) statVarList.get(ii);
							String itemid=fielditem.getItemid();
							String itemdesc = fielditem.getItemdesc();
							if(!usedFieldid.contains(itemid.toUpperCase())&&(itemdesc.startsWith("现")||itemdesc.startsWith("拟")||itemid.toLowerCase().startsWith("yk"))) {
								String itemtype = fielditem.getItemtype();
								int itemlength = fielditem.getItemlength();//长度
								if("A".equalsIgnoreCase(itemtype)) {//字符型
									createSql_cal+=","+itemid+" VARCHAR("+itemlength+")";
								}
								else if("N".equalsIgnoreCase(itemtype)) {//数值型
									createSql_cal+=","+itemid+" DECIMAL(18,8)";
								}
								else if("D".equalsIgnoreCase(itemtype)) {//时间
									createSql_cal+=","+itemid+" TIMESTAMP";
								}
								else if("M".equalsIgnoreCase(itemtype)) {//大文本
									createSql_cal+=","+itemid+" VARCHAR(3000)";
								}
								insertSql_cal+=","+itemid;
								insertSql_cal_end+=",?";
								usedFieldid.add(itemid.toUpperCase());
								usedFieldList.add(fielditem);
							}
						}
						
						if(item!=null&&!usedFieldid.contains(fieldname.toUpperCase())) {
							String itemtype = item.getItemtype();
							int itemlength = item.getItemlength();//长度
							if("A".equalsIgnoreCase(itemtype)) {//字符型
								createSql_cal+=","+fieldname+" VARCHAR("+itemlength+")";
							}
							else if("N".equalsIgnoreCase(itemtype)) {//数值型
								createSql_cal+=","+fieldname+" DECIMAL(18,8)";
							}
							else if("D".equalsIgnoreCase(itemtype)) {//时间
								createSql_cal+=","+fieldname+" TIMESTAMP";
							}
							else if("M".equalsIgnoreCase(itemtype)) {//大文本
								createSql_cal+=","+fieldname+" VARCHAR(3000)";
							}
							usedFieldid.add(fieldname.toUpperCase());
							//usedFieldList.add(item);
						}
					}
				}else if("2".equals(type)) {
					SubField subtable=null;
					SubSetDomain subDomain=(SubSetDomain)abean.get("sub_info");
					ArrayList sublist=subDomain.getSubFieldList();
					for(Iterator i=sublist.iterator();i.hasNext();)
					{
						subtable=(SubField)i.next();
						String field_name = subtable.getFieldname();
						String formula=subtable.getFormula();
						String cond=subtable.getCond();
						FieldItem item=DataDictionary.getFieldItem(field_name);
						if(item!=null){
							Object [] object = new Object[9];
							object[0]=tabId;
							object[1]=key;
							object[2]=setdesc;
							object[3]=field_name;
							object[4]=item.getItemtype();
							object[5]=formula;
							object[6]=cond;
							object[7]="";
							object[8]=new Date();
							listForSubset.add(object);
						}
					}
				}
			}
			insertSql_cal+=") values ";
			insertSql_cal_end+=")";
			insertSql_cal+=insertSql_cal_end;
			createSql_cal+=")";
			if(isneedinsert) {
				H2JdbcUtil.batchUpdate(insertSqlForSubset, listForSubset);
			}
			if(H2JdbcUtil.isExistTable(tablename_cal,false))
	    	    H2JdbcUtil.dropTable(tablename_cal);
			H2JdbcUtil.execute(createSql_cal);//创建成功
			if(!h2Table.contains(tablename_cal)) {
				h2Table.add(tablename_cal);
			}
			//创建子集数据表
			//查询需要计算的人员的子集数据
			rset = dao.search(sql);
			List<Object[]> paramList_cal = new ArrayList<Object[]>();
			while(rset.next()) {
				ArrayList keyList = new ArrayList();
				Object [] object_cal = new Object[usedFieldList.size()+3];
				String objectid = "";
				if(this.paramBo.getInfor_type()==2)
					objectid=rset.getString("b0110");
				else if(this.paramBo.getInfor_type()==3)
					objectid=rset.getString("e01a1");
				else if(this.paramBo.getInfor_type()==1)
					objectid=rset.getString("basepre")+rset.getString("a0100");
				for(Iterator t=subSetInfoList.iterator();t.hasNext();)
				{
					abean=(LazyDynaBean)t.next(); 
					String subdomainid=(String)abean.get("subdomainid");
					int chgstate=(Integer)abean.get("chgstate");
					String setname=(String)abean.get("setname"); 
					String type=(String)abean.get("type"); 
					String key="t_"+setname;
					String key1 = setname;
					if(subdomainid.length()>0) {
						key+="_"+subdomainid;
						key1+="_"+subdomainid;
					}
					key+="_"+chgstate;
					key1+="_"+chgstate;
					if(!keyList.contains(key)) {
						keyList.add(key);
						String value = rset.getString(key);
						//创建子集数据表
						SubField subtable=null;
						SubSetDomain subDomain = null;
						//解析子集xml
						if("2".equals(type)) {
							subDomain=(SubSetDomain)abean.get("sub_info");
						}
						else if("1".equals(type)) {
							LazyDynaBean sub_info = (LazyDynaBean) abean.get("sub_info");
							subDomain=(SubSetDomain)sub_info.get("subdomain");
						}
						ArrayList sublist=subDomain.getSubFieldList();
						String tablename = this.userView.getUserName()+"_"+tabId+"_"+key1;
						String createSql = "CREATE TABLE "+tablename;
						createSql+="(ID INT PRIMARY KEY auto_increment,OBJECTID VARCHAR(50),I9999 INT,SEQNUM INT,TIMESTAMP VARCHAR(200),CREATE_TIME TIMESTAMP,CREATEUSER VARCHAR(50)";
						String insertSql = "insert into "+tablename+"(OBJECTID,I9999,SEQNUM,TIMESTAMP,CREATE_TIME,CREATEUSER";
						String insertSql_end = "";
						int num = 0;
						for(Iterator i=sublist.iterator();i.hasNext();)
						{
							subtable=(SubField)i.next();
							String field_name = subtable.getFieldname();
							FieldItem item=DataDictionary.getFieldItem(field_name);
							if(item!=null){
								String itemtype = item.getItemtype();//类型
								int itemlength = item.getItemlength();//长度
								if("A".equalsIgnoreCase(itemtype)) {//字符型
									createSql+=","+field_name+" VARCHAR("+itemlength+")";
								}
								else if("N".equalsIgnoreCase(itemtype)) {//数值型
									createSql+=","+field_name+" DECIMAL(18,8)";
								}
								else if("D".equalsIgnoreCase(itemtype)) {//时间
									createSql+=","+field_name+" TIMESTAMP";
								}
								else if("M".equalsIgnoreCase(itemtype)) {//大文本
									createSql+=","+field_name+" VARCHAR(3000)";
								}
								insertSql+=","+field_name;
								insertSql_end+=",?";
								num++;
							}else if("attach".equalsIgnoreCase(field_name)) {
								createSql+=","+field_name+" VARCHAR(3000)";
								insertSql+=","+field_name;
								insertSql_end+=",?";
								num++;
							}
						}
						createSql+=")";
						insertSql+=") values (?,?,?,?,?,?";
						insertSql += insertSql_end+")";
						if(!H2JdbcUtil.isExistTable(tablename,false)) {
							H2JdbcUtil.execute(createSql);//创建成功
							if(!h2Table.contains(tablename)) {
								h2Table.add(tablename);
							}
						}
						//将表明加入到list中
						if(!list.contains(tablename)) {
							list.add(tablename);
						}
						ArrayList subsetdataList = analysisSubSetXml(value,subDomain);//解析xml数据
						//将数据插入到paramList中
						List<Object[]> paramList = new ArrayList<Object[]>();
						for(int n=0;n<subsetdataList.size();n++) {
							HashMap datamap = (HashMap) subsetdataList.get(n);
							for(Object i9999:datamap.keySet()) {
								Object [] object = new Object[num+6];
								HashMap map = (HashMap) datamap.get(i9999);
								object[0]=objectid;
								object[1]=i9999;
								object[2]=n;
								object[3]=map.get("timestamp");
								object[4]=new Timestamp(new Date().getTime());
								object[5]=this.userView.getUserName();
								for(int m = 0;m<sublist.size();m++) {
									SubField subtable_=(SubField)sublist.get(m);
									String field_name = subtable_.getFieldname().toLowerCase();
									FieldItem item=DataDictionary.getFieldItem(field_name);
									if(item!=null){
										if(map.containsKey(field_name)) {
											Object fieldvalue = null;
											if("D".equalsIgnoreCase(item.getItemtype())) {
												fieldvalue = map.get(field_name)==null?null:(Date)map.get(field_name);
											}else if("N".equalsIgnoreCase(item.getItemtype())) {
												fieldvalue = map.get(field_name)==null?null:Double.parseDouble((String)map.get(field_name));
											}else
												fieldvalue = (String) map.get(field_name);
											object[m+6] = fieldvalue;
										}
									}else if("attach".equalsIgnoreCase(field_name)) {
										object[m+6] = map.get(field_name)==null?null:(String)map.get(field_name);
									}
								}
								paramList.add(object);
							}
						}
						//将数据插入到h2数据库表中
						H2JdbcUtil.batchUpdate(insertSql, paramList);
					}
				}
				object_cal[0]=objectid;
				object_cal[1]=new Timestamp(new Date().getTime());
				object_cal[2]=this.userView.getUserName();
				//将临时表对应的临时变量的值插入h2临时表
				for(int i=0;i<usedFieldList.size();i++)
				{
					FieldItem item=(FieldItem) usedFieldList.get(i);
					String value = "";
					String itemtype = item.getItemtype();//类型
					String itemid=item.getItemid();
					if("A".equalsIgnoreCase(itemtype)) {//字符型
						value = rset.getString(itemid);
					}
					else if("N".equalsIgnoreCase(itemtype)) {//数值型
						value = rset.getDouble(itemid)+"";
					}
					else if("D".equalsIgnoreCase(itemtype)) {//时间
						value = PubFunc.FormatDate(rset.getDate(itemid));
					}
					else if("M".equalsIgnoreCase(itemtype)) {//大文本
						value = rset.getString(itemid);
					}
					object_cal[i+3]=value;
				}
				paramList_cal.add(object_cal);
			}
			//执行插入计算临时表
			H2JdbcUtil.batchUpdate(insertSql_cal, paramList_cal);
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rset);
			PubFunc.closeDbObj(rset1);
			PubFunc.closeDbObj(rset2);
		}
		return list;
	}
	
	/**
	 * 获得模板需参与计算的子集信息
	 * @param formulalist
	 * @return
	 */
	private ArrayList getSubSetByFormula(ArrayList formulalist)
	{
		ArrayList setList=new ArrayList();
		ArrayList cellList=  utilBo.getPageCell(tabId,-1); 
		SubField subtable=null;
		HashMap map=new HashMap();
		
		for (int i = 0; i < cellList.size(); i++) { 
			TemplateSet setBo = (TemplateSet) cellList.get(i);
			boolean isSubflag = setBo.isSubflag();
			if(isSubflag)
			{ 
				SubSetDomain subDomain = new SubSetDomain(setBo.getXml_param());
				ArrayList sublist=subDomain.getSubFieldList();
				String subdomainid=subDomain.getSubDomainId();
				boolean hasCal=false;
				for(Iterator t=sublist.iterator();t.hasNext();)
				{
					subtable=(SubField)t.next();
					String formula=subtable.getFormula();
					String cond=subtable.getCond();
					if(formula!=null&&formula.trim().length()>0)
					{
						hasCal=true;
						break;
					}
				}
				
				String key=setBo.getSetname().toUpperCase()+"_"+subdomainid+"_"+setBo.getChgstate();
				if(hasCal)
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("type","2"); // 1:统计子集指标计算公式     2：子集四则运算公式
					abean.set("sub_info",subDomain);
					abean.set("subdomainid", subdomainid);
					abean.set("chgstate", setBo.getChgstate());
					abean.set("setname",setBo.getSetname());
					abean.set("setdesc",setBo.getField_hz());
					setList.add(abean);
				} 
			} 
		}
		
		YksjParser yp=null;
		for(int i=0;i<formulalist.size();i++)
		{ 
			FormulaGroupBo formulabo=(FormulaGroupBo)formulalist.get(i); 
			ArrayList list=formulabo.getFormulalist();
			for(int j=0;j<list.size();j++)
			{
				LazyDynaBean dynabean=(LazyDynaBean)list.get(j);
				String fieldname=(String)dynabean.get("lexpr");
				String formula=(String)dynabean.get("rexpr");
				if(formula.indexOf("统计表单子集")!=-1)
				{
					int chgstate=0;
					String subdomainid="";
					String setname="";
					String key=setname.toUpperCase()+"_"+subdomainid+"_"+chgstate;
					String setdesc = "";
					//需分析统计子集指标公式涉及模板哪个子集的数据 。。。。。。。 子集统计公式格式   统计表单子集 工作经历的 指标+指标 。。。。
					//将公式拆分拿到涉及的子集指标
					String[] formulaarrs = formula.split(" ");
					String field = formulaarrs[1];
					if(field.startsWith("[")&&field.endsWith("]")) {
						field = field.substring(1,field.length()-1);
					}
					if(field.endsWith("的")) {
						field = field.substring(0,field.length()-1);
					}
					for (int k = 0; k < cellList.size(); k++) { 
						TemplateSet setBo = (TemplateSet) cellList.get(k);
						String field_name = setBo.getField_name();//指标id
						String field_hz = setBo.getField_hz();//指标的名字  没改过的有现拟
						String hz = setBo.getHz();//改过的名字
						int chg = setBo.getChgstate();
						//判断名字是否改动
						boolean isSubflag = setBo.isSubflag();
						if(isSubflag) {
							SubSetDomain subDomain = new SubSetDomain(setBo.getXml_param());
							ArrayList sublist=subDomain.getSubFieldList();
							String id=subDomain.getSubDomainId();
							hz = hz.replaceAll("[\\{\\}\\`]", "");
							if(!hz.equals(field_hz)) {//改了名字
								if(field.equals(hz)) {
									chgstate = chg;
									subdomainid = id;
									setname = setBo.getSetname();
									setdesc = setBo.getField_hz();
									dynabean.set("subdomain", subDomain);
								}
							}else {
								if(chg==1) {
									field_hz = "现"+field_hz;
								}else if(chg==2) {
									field_hz = "拟"+field_hz;
								}
								if(field.equals(field_hz)) {
									chgstate = chg;
									subdomainid = id;
									setname = setBo.getSetname();
									setdesc = setBo.getField_hz();
									dynabean.set("subdomain", subDomain);
								}
							}
						}
					}
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("type","1"); // 1:统计子集指标计算公式     2：子集四则运算公式
					abean.set("sub_info",dynabean);
					abean.set("subdomainid", subdomainid);
					abean.set("chgstate",chgstate);
					abean.set("setname",setname);
					abean.set("setdesc",setdesc);
					setList.add(abean);
				} 
			} 
		} 
		return setList;
	}
	
	/**
	 * 解析xml，将数据插入到临时表中
	 * @param sub_dataXml
	 * @param subDomain 
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList analysisSubSetXml(String sub_dataXml, SubSetDomain subDomain) throws GeneralException {
		Document doc=null;
		Element eleRoot=null;
		Element element = null; 
		ArrayList dataList = new ArrayList();
		try
		{
			ArrayList sublist=subDomain.getSubFieldList();
			if(sub_dataXml!=null&&sub_dataXml.length()>0)
			{
				sub_dataXml=sub_dataXml.replace("&", "＆");
				StringReader reader=new StringReader(sub_dataXml);
				doc=PubFunc.generateDom(sub_dataXml);
				XMLOutputter outputter = new XMLOutputter();
				String xpath="/records";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				eleRoot =(Element) findPath.selectSingleNode(doc);
				if(eleRoot!=null){
					String columns=eleRoot.getAttributeValue("columns").toUpperCase(); //得到xml中columns中对应的值
					List recordList=eleRoot.getChildren("record");
					if(recordList!=null&&recordList.size()>0)
					{
						for(int i=0;i<recordList.size();i++)
						{
							HashMap recordmap = new HashMap();
							element=(Element)recordList.get(i);
							String I9999=element.getAttributeValue("I9999");
							String timestamp = element.getAttributeValue("timestamp")==null?"":element.getAttributeValue("timestamp");
							String contentValue=element.getValue();
							if(contentValue!=null&&contentValue.length()>0)
							{
								String[] valueArr=contentValue.split("`",-1);
								String[] columnArr=columns.split("`");
								String theEndValue = "";
								HashMap map = new HashMap();
								for(int j=0;j<columnArr.length;j++){
									FieldItem fielditem = DataDictionary.getFieldItem(columnArr[j]);
									if(fielditem!=null) {
										String type =  fielditem.getItemtype();
										if("D".equalsIgnoreCase(type)) {
											for(Iterator t=sublist.iterator();t.hasNext();)
											{
												SubField subtable=(SubField)t.next();
												String itemid = subtable.getFieldname();
												String slop = subtable.getSlop();
												String pre = subtable.getPre();
												if(itemid.equalsIgnoreCase(columnArr[j])) {
													String value=valueArr[j].replace(".", "-");
													if(StringUtils.isNotBlank(value)&&slop!=null&&!"".equals(slop)){
														value = this.formatDateFiledsetValue(value, Integer.parseInt(slop));
													}
													SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //加上时间
													if(StringUtils.isNotBlank(value)) {
														Date date=sDateFormat.parse(value.replace(".", "-"));
												        map.put(columnArr[j].toLowerCase(), new Timestamp(date.getTime()));
													}else
														map.put(columnArr[j].toLowerCase(), null);
											        break;
												}
											}
										}else if("N".equalsIgnoreCase(type)) {
											if(StringUtils.isBlank(valueArr[j])) {
												map.put(columnArr[j].toLowerCase(), null);
											}else
												map.put(columnArr[j].toLowerCase(), valueArr[j]);
										}else
											map.put(columnArr[j].toLowerCase(), valueArr[j]);
									}else
										map.put(columnArr[j].toLowerCase(), valueArr[j]);
								}
								map.put("timestamp",timestamp);
								recordmap.put(I9999, map);
							}
							dataList.add(recordmap);
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return dataList;
	}
	public String formatDateFiledsetValue(String value,int disformat)
	{
		StringBuffer buf=new StringBuffer();
		value=value.replace(".", "-");
		Date date=DateUtils.getDate(value,"yyyy-MM-dd");
		if(date!=null){
			int year=DateUtils.getYear(date);
			int month=DateUtils.getMonth(date);
			int day=DateUtils.getDay(date);
			value=value.replaceAll("-",".");
			String hours = "00";
			String minutes = "00";
			String seconds = "00";
			switch(disformat)
			{
			case 0: //1991.12.3
				buf.append(year);
				buf.append(".");
				buf.append(month);
				buf.append(".");
				buf.append(day);
				buf.append(" ");
				buf.append(hours);
				buf.append(":");
				buf.append(minutes);
				buf.append(":");
				buf.append(seconds);
				break;
			case 1: //91.12.3
				if(year>=2000)
					buf.append(year);
				else
				{
					String temp=String.valueOf(year);
					buf.append(temp.substring(2));
				}
				buf.append(".");
				buf.append(month);
				buf.append(".");
				buf.append(day);
				buf.append(" ");
				buf.append(hours);
				buf.append(":");
				buf.append(minutes);
				buf.append(":");
				buf.append(seconds);
				break;
			case 2://1991.2
				buf.append(year);
				buf.append(".");
				buf.append(month);	
				buf.append(".");
				buf.append("01");
				buf.append(" ");
				buf.append(hours);
				buf.append(":");
				buf.append(minutes);
				buf.append(":");
				buf.append(seconds);
				break;
			case 3://1992.02
	            buf.append(year);
	            buf.append(".");            
	            if (month>9){                
	                buf.append(month);  
	            }
	            else {
	                buf.append("0"+month);    
	            }
	            buf.append(".");
				buf.append("01");
				buf.append(" ");
				buf.append(hours);
				buf.append(":");
				buf.append(minutes);
				buf.append(":");
				buf.append(seconds);
				break;
			case 4://92.2
				if(year>=2000)
					buf.append(year);
				else
				{
					String temp=String.valueOf(year);
					buf.append(temp.substring(2));
				}
				buf.append(".");
				buf.append(month);
				buf.append(".");
				buf.append("01");
				buf.append(" ");
				buf.append(hours);
				buf.append(":");
				buf.append(minutes);
				buf.append(":");
				buf.append(seconds);
				break;
			case 5://98.02
				if(year>=2000)
					buf.append(year);
				else
				{
					String temp=String.valueOf(year);
					buf.append(temp.substring(2));
				}
				buf.append(".");
				if(month>=10)
					buf.append(month);
				else
				{
					buf.append("0");
					buf.append(month);
				}
				buf.append(".");
				buf.append("01");
				buf.append(" ");
				buf.append(hours);
				buf.append(":");
				buf.append(minutes);
				buf.append(":");
				buf.append(seconds);
				break;
			case 18://1992.02.01
				buf.append(year);
				buf.append(".");
				if(month>=10)
					buf.append(month);
				else
				{
					buf.append("0");
					buf.append(month);
				}
				buf.append(".");
				if(day>=10)
					buf.append(day);
				else
				{
					buf.append("0");
					buf.append(day);
				}	
				buf.append(" ");
				buf.append(hours);
				buf.append(":");
				buf.append(minutes);
				buf.append(":");
				buf.append(seconds);
				break;
			default:
				buf.append(year);
				buf.append(".");
				buf.append(month);
				buf.append(".");
				buf.append(day);
				buf.append(" ");
				buf.append(hours);
				buf.append(":");
				buf.append(minutes);
				buf.append(":");
				buf.append(seconds);
				break;
			}
		}
		return buf.toString();
	}
	/**
	 * 读取涉及到的子集列计算公式
	 * @return
	 */
	public ArrayList readSubsetFormula() {
		ArrayList subsetformula = new ArrayList();
		ArrayList cellList=  utilBo.getPageCell(tabId,-1); 
		for (int i = 0; i < cellList.size(); i++) { 
			TemplateSet setBo = (TemplateSet) cellList.get(i);
			boolean isSubflag = setBo.isSubflag();
			if(isSubflag)
			{ 
				SubSetDomain subDomain = new SubSetDomain(setBo.getXml_param());
				ArrayList sublist=subDomain.getSubFieldList();
				for(Iterator t=sublist.iterator();t.hasNext();)
				{
					SubField subtable=(SubField)t.next();
					String formula=subtable.getFormula();
					String cond=subtable.getCond();
					if(formula!=null&&formula.trim().length()>0)
					{
						LazyDynaBean abean=new LazyDynaBean();
						abean.set("formula",formula);
						abean.set("cond",cond);
						subsetformula.add(abean);
					}
				}
			} 
		}
		return subsetformula;
	}
	/**
	 * 得到条件检索对应的sql 语句
	 * @param factor
	 * @return 
	 * @throws GeneralException 
	 */
	public String getFactor2Sql() throws GeneralException {
		StringBuffer sqlwhere = new StringBuffer();
		try {
	        String fieldcode = "b0110";
	        String key = "B01";
	        String BasePre = "B";
	        int infoGroup=YksjParser.forUnit;
	        ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			if(this.paramBo.getInfor_type()==3)
			{
				fieldcode = "e01a1";
				infoGroup=YksjParser.forPosition;
				BasePre = "K";
				key = "K01";
			}
			sqlwhere.append("select "+fieldcode+" from ");
	        int varType = 8;
	        String whereIN = "";
	        //获得管理范围sql
			whereIN = "select "+fieldcode+" from "+key+" "+this.getPrivSQL(fieldcode);
	        if ("1".equals(this.paramBo.getNo_priv_ctrl()))
	            whereIN = "";
	        YksjParser yp = new YksjParser(this.userView, alUsedFields, YksjParser.forSearch, varType, infoGroup, "Ht", "");
	        YearMonthCount ymc = null;
	        yp.setSupportVar(true, "select  *  from   midvariable where nflag=0 and templetid= " + tabId); // 支持临时变量
	        yp.run_Where(this.paramBo.getFactor(), ymc, "", "", dao, whereIN, this.conn, "A", null);
	        sqlwhere.append(key);
	        sqlwhere.append(" where " +yp.getSQL()+" ");
		} catch (GeneralException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return sqlwhere.toString();
	}
	public String getPrivSQL(String fieldcode) throws GeneralException {
		StringBuffer sql = new StringBuffer();
		try {
			if (!userView.isSuper_admin()) {
				String unit_id = userView.getUnit_id();
				String privcode = userView.getManagePrivCode();
				String privcodeValue = userView.getManagePrivCodeValue();
				if("UN`".equalsIgnoreCase(unit_id)){
					sql.append(" ");
				} else if (StringUtils.isNotEmpty(unit_id) && unit_id.length()>2 || StringUtils.isNotEmpty(privcode)) {
					sql.append("where 1=1 ");
					if(StringUtils.isNotEmpty(unit_id) && unit_id.length() > 2) {
						String[] unitArr = unit_id.split("`");
						String unitStr = "";
						for(int j=0;j<unitArr.length;j++) {
							String unit = unitArr[j].substring(2);
							unitStr+="'"+unit+"',";
						}
						sql.append(" AND "+fieldcode+" in (" + unitStr.substring(0, unitStr.length()-1) + ")");
					}else {
						if (privcodeValue == null || "".equals(privcodeValue.trim())) {
							
						} else {
							sql.append(" AND "+fieldcode+" in ('" + privcodeValue + "')");
						}
					}
					
				}else {
					sql.append(" AND 1=2");
				}
			} else {
				sql.append("");
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
		return sql.toString();
	}
}
