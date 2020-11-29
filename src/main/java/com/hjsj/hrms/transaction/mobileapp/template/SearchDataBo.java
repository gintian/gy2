/**   
 * @Title: SearchDataBo.java 
 * @Package com.hjsj.hrms.transaction.mobileapp.template 
 * @Description: 查询业务协同数据的BO类
 * @author xucs
 * @date 2014-1-3 下午03:54:33 
 * @version V1.0   
 */
package com.hjsj.hrms.transaction.mobileapp.template;

import com.hjsj.hrms.businessobject.general.template.*;
import com.hjsj.hrms.businessobject.general.template.workflow.*;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.infor.BaseInfoBo;
import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.template.templatecard.businessobject.AttachmentBo;
import com.hjsj.hrms.module.template.utils.TemplateInterceptorAdapter;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.service.SynOaService;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.*;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.Map.Entry;

/**
 * @ClassName: SearchDataBo
 * @Description: 查询数据的BO类
 * @author xucs
 * @date 2014-1-3 下午03:54:33
 * 
 */
public class SearchDataBo {
    private Connection conn = null;
    private UserView userView = null;
    private TemplateTableBo templateTableBo = null;
    private String opt="";
    private TemplateParam paramBo=null;
    private Boolean isWeiX=false;
    public Boolean getIsWx() {
		return isWeiX;
	}
	public void setIsWx(Boolean isWeiX) {
		this.isWeiX = isWeiX;
	}



	public void setOpt(String opt) {
		this.opt = opt;
	}



	/**
     * <p>
     * Title:SearchTemplateDataBo
     * </p>
     * <p>
     * Description:创建查询数据Bo类
     * </p>
     * 
     * @param conn
     *            数据库的连接
     * @param userView
     *            当前用户数据对象
     */
    public SearchDataBo(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }
    
    

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param conn2
     * @param userView2
     * @param tabid
     * @throws  
     * @throws GeneralException 
     */
    public SearchDataBo(Connection conn, UserView userView, String tabid) throws GeneralException {
        try {
            this.conn = conn;
            this.userView = userView;
            ContentDAO dao = new ContentDAO(conn);
            this.templateTableBo = new TemplateTableBo(this.conn, Integer.parseInt(tabid), this.userView);
       //     RecordVo table_vo = new RecordVo("template_table");
       //     table_vo.setInt("tabid", Integer.parseInt(tabid));
       //     table_vo=dao.findByPrimaryKey(table_vo);\
            paramBo=new TemplateParam(conn, userView, Integer.parseInt(tabid));
            RecordVo table_vo=TemplateUtilBo.readTemplate(Integer.parseInt(tabid),this.conn);
            String staticKey = "static";
            if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
            	staticKey = "static_o";
            }
            int tempstatic = table_vo.getInt(staticKey);
            String sp_flag = table_vo.getString("sp_flag");
            if (tempstatic == 10) {
                this.templateTableBo.setInfor_type(2);
            } else if (tempstatic == 11) {
                this.templateTableBo.setInfor_type(3);
            } else {
                this.templateTableBo.setInfor_type(1);
            }
            this.templateTableBo.setTask_sp_flag(sp_flag);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        

    }
    
    /**
     * @Title:getSpFlag
     * @Description:初始化审批数据
     * @param task_id 任务号
     * @author dengc
     * @param ins_id 流程号
     * @serialData 2014-1-14
     * @return    spObjectList-->审批对象名称     spObjectValueList-->审批对象ID       flag -> 1:手工报批-选人   2：手工报批-指定审批人  3：自动审批
     */
    public LazyDynaBean getInitData(String task_id,String selfapply, String ins_id) throws GeneralException 
    {
    	LazyDynaBean initData=new LazyDynaBean();
    	String flag="1";
    	ArrayList spObjectList=new ArrayList();
    	ArrayList spObjectValueList=new ArrayList(); 
    	ArrayList priorityList=new ArrayList();
    	ArrayList priorityValueList=new ArrayList();
    	String actor_type="";
    	try
    	{
    		RowSet recset=null;
    		RowSet recset2=null;
    		ContentDAO dao = new ContentDAO(this.conn);
    		 
    		if(!"0".equals(task_id))
    		{
    			dao.update("update t_wf_task_objlink set submitflag=1 where task_id="+task_id);
    		}
    		
    		if(selfapply!=null&& "0".equals(selfapply))
    		{
	    		String tabid=""+this.templateTableBo.getTabid();
	    		String sql_0="select count(*) from "+this.userView.getUserName()+"templet_"+tabid+" where  submitflag=1";
	    		if(!"0".equals(task_id))
	    		{
	    			sql_0="select count(*)   from t_wf_task_objlink where task_id="+task_id+" and tab_id=" + tabid + "  and  submitflag=1 and " + Sql_switcher.isnull("state", "0") + "<>3 ";// state 
	    		}
	    		recset=dao.search(sql_0);
	    		if(recset.next())
	    		{
	    			if(recset.getInt(1)==0)
	    				throw new GeneralException("请选择记录!");
	    		}
    		}
    		
    		if("0".equals(task_id)&& "0".equals(selfapply)&&this.templateTableBo.getOperationtype()!=0&&this.templateTableBo.getOperationtype()!=5)
    		{ 
    			String _tabname=this.userView.getUserName()+"templet_"+this.templateTableBo.getTabid();
    			if(this.templateTableBo.getInfor_type()==1) //人员
    			{ 
    				recset=dao.search("select distinct lower(basepre) from "+_tabname+" where submitflag=1");
    				while(recset.next())
    				{
    					String basepre=recset.getString(1);
    					String _str="";
    					recset2=dao.search("select a0101_1 from "+_tabname+" where submitflag=1 and  not exists (select null from "+basepre+"A01 where  "+basepre+"A01.a0100="+_tabname+".a0100  ) ");
    					while(recset2.next())
    					{
    						_str+=","+recset2.getString(1);
    					}
    					if(_str.length()>0)
    					{
    						throw new GeneralException("信息库中不存在 "+_str.substring(1)+" 记录!");
    					}
    					
    				}
    			}
    			else
    			{ 
    					//是不是该获得一下是不是新生成代码
    					String join="e01a1";
    					if(this.templateTableBo.getInfor_type()==2)
    						join="b0110";
    					String _str=""; 
    					String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
						String sql=" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date "; 
						
						StringBuffer queryExitsBuffer = new StringBuffer();//用以确认处理的数据是否在organization表中存在
						queryExitsBuffer.append("select codeitemdesc_1 ");
						if(this.templateTableBo.getOperationtype()==8){//如果是合并
							if(this.templateTableBo.getInfor_type()==2){//如果是单位
								queryExitsBuffer.append(",b0110");
							}else{
								queryExitsBuffer.append(",e01A1");
							}
							queryExitsBuffer.append(",to_id ");
						}
						queryExitsBuffer.append(" from "+_tabname);
						queryExitsBuffer.append(" where  submitflag=1 and    not exists (select null from organization where  organization.codeitemid="+_tabname+"."+join+" "+sql+")");
    					//recset2=dao.search("select codeitemdesc_1 from "+_tabname+" where  submitflag=1 and    not exists (select null from organization where  organization.codeitemid="+_tabname+"."+join+" "+sql+" ) ");
						recset2=dao.search(queryExitsBuffer.toString());
    					while(recset2.next())
    					{
    						String newItemId="";//这个代表这是在合并的时候采用新代码
    						String to_id ="";
    						if(this.templateTableBo.getOperationtype()==8){
    							newItemId = recset2.getString(2);
    							to_id = recset2.getString(3);
    						}
    						if(newItemId!=null&&newItemId.trim().length()>0&&to_id!=null&&to_id.trim().length()>0&&newItemId.equals(to_id)){
    							continue;//如果是采用新代码是在orgnization表中不存在的,并且to_id也是和新代码相同时不用提示出来的
    						}
    						_str+=","+recset2.getString(1);
    					}
    					if(_str.length()>0)
    					{
    						throw new GeneralException("信息库中不存在 "+_str.substring(1)+" 记录!");
    					}
    					 
    			}
    		}
    		
	    	if(this.templateTableBo.isBsp_flag()&&this.templateTableBo.getSp_mode()==0){//如果是需要审批并且自动流转
	    	    WorkflowBo wbo=new WorkflowBo(this.conn,this.templateTableBo.getTabid(),this.userView);
	    	    /**这里执行一下程序,是为了判断流程节点定义是否正确,并不是为了获取下一个结点是谁**/
                wbo.getNextNodeStr(Integer.parseInt(task_id),Integer.parseInt(ins_id),selfapply);
	    	    flag="3";
	    	}
	    	else //if(!task_id.equals("0"))
	    	{
	   
	    		
	    		if(this.templateTableBo.getRelation_id()!=null&&this.templateTableBo.getRelation_id().trim().length()>0&&!"1".equals(this.templateTableBo.getDef_flow_self()))
	    		{
	    			flag="2";
	    			HashMap map=new HashMap();
					HashMap map2=new HashMap();
					String b0110="";String e0122="";String e01a1="";
	    			String _self_id="";
	    			
	    			recset=dao.search("select actor_type from t_wf_relation where relation_id="+this.templateTableBo.getRelation_id());
	    			if(recset.next())
	    				actor_type=recset.getString(1);
	    			
	    			if("4".equals(actor_type)){//业务
	    				_self_id=this.userView.getUserName();
	    				recset=dao.search("select object_id,mainbody_id,a0101 from t_wf_mainbody where relation_id="+this.templateTableBo.getRelation_id()+" and  object_id='"+this.userView.getUserName()+"' order by object_id ");	
	    			}
	    			else
	    			{ 
	    				if(this.userView.getA0100()==null||this.userView.getA0100().trim().length()==0){//业务用户,没有关联自助用户，无法选取自助的审批关系
	    					recset=dao.search("select object_id,mainbody_id,a0101 from t_wf_mainbody where 1=2");
	    				}else{
	    					 _self_id=this.userView.getDbname().toLowerCase()+this.userView.getA0100();
	 						recset=dao.search("select b0110,e0122,e01a1 from "+this.userView.getDbname()+"A01 where a0100='"+this.userView.getA0100()+"'");
	 						if(recset.next())
	 						{
	 							b0110=recset.getString("b0110")!=null?"un"+recset.getString("b0110"):"";
	 							e0122=recset.getString("e0122")!=null?"um"+recset.getString("e0122"):"";
	 							e01a1=recset.getString("e01a1")!=null?"@k"+recset.getString("e01a1"):""; 
	 						}
	 						recset=dao.search("select object_id,mainbody_id,a0101 from t_wf_mainbody where relation_id="+this.templateTableBo.getRelation_id()+" and lower(object_id) in ('"+b0110.toLowerCase()+"','"+e0122.toLowerCase()+"','"+e01a1.toLowerCase()+"','"+_self_id+"') order by object_id ");
	    				}
		    			
	    			} 
					while(recset.next())
					{
						String object_id=recset.getString("object_id");//考核对象的值
						String mainbody_id=recset.getString("mainbody_id");//考核主体的值
						String a0101=recset.getString("a0101");//考核主题的姓名
						if(map.get(object_id)==null)
						{
							ArrayList list=new ArrayList();
							list.add(mainbody_id);
							ArrayList list1=new ArrayList();
							list1.add(recset.getString("a0101"));
							map.put(object_id,list);
							map2.put(object_id,list1);
						}
						else
						{
							ArrayList list=(ArrayList)map.get(object_id);
							ArrayList list1=(ArrayList)map2.get(object_id);
							list.add(mainbody_id);
							list1.add(recset.getString("a0101"));
						}
					}
					
					if(map.get(_self_id)!=null)
					{
						spObjectList=(ArrayList)map.get(_self_id);//当前人员的审批关系
						spObjectValueList=(ArrayList)map2.get(_self_id);
					}
					else if(map.get(e01a1)!=null)
					{
						spObjectList=(ArrayList)map.get(e01a1);
						spObjectValueList=(ArrayList)map2.get(e01a1);
					}
					else if(map.get(e0122)!=null)
					{
						spObjectList=(ArrayList)map.get(e0122);
						spObjectValueList=(ArrayList)map2.get(e0122);
					}
					else if(map.get(b0110)!=null)
					{
						spObjectList=(ArrayList)map.get(b0110);
						spObjectValueList=(ArrayList)map2.get(b0110);
					} 
					else//如果没有审批的审批关系所对应的人员,将审批置为手工选人
					{
						flag="1"; 
					}
	    			
	    		}
	    		else if("1".equals(this.templateTableBo.getDef_flow_self()))
	    		{
	    			if(this.templateTableBo.isDef_flow_self(Integer.parseInt(task_id)))
	    			{ 
	    			    flag="3";
	    			}
	    			else
	    			{
	    				flag="1"; 
	    			}
	    		}
	    	}
	    	ArrayList codes=AdminCode.getCodeItemList("37");
	    	for(int i=0;i<codes.size();i++)
	    	{
	    		CodeItem c=(CodeItem)codes.get(i);
	    		priorityValueList.add(c.getCodeitem());
	    		priorityList.add(c.getCodename());
	    		
	    	}
	    	if(recset!=null)
	    		recset.close();
	    }
	    catch (Exception ex) {
	            ex.printStackTrace();
	            throw GeneralExceptionHandler.Handle(ex);
	    }
	    if(!this.templateTableBo.isBsp_flag())
	        flag="3";
	    
	    if("1".equals(flag))//手工选人,添加人员和业务用户
	    {
	    	spObjectList.add(ResourceFactory.getProperty("label.query.employ"));//人员
	    	spObjectList.add(ResourceFactory.getProperty("label.role.detail.name.0"));//业务用户
	    	spObjectValueList.add("1");
	    	spObjectValueList.add("4");
	    	
	    }
	    
	    
	    initData.set("actor_type",actor_type);
	    initData.set("flag", flag);
	    initData.set("spObjectList", spObjectList); 
	    initData.set("spObjectValueList", spObjectValueList); 
	    initData.set("priorityValueList", priorityValueList); 
	    initData.set("priorityList", priorityList); 
    	return initData;
    }
    
    /**
	 * 判断任务是否是自定义流程中最后审批层级的最后一个待处理任务
	 * @param task_id
	 * @param bo
	 * @return
	 */
	public boolean isEndNode(int task_id,TemplateTableBo bo)
	{
		boolean flag=false;
		try
		{
			String sql="";
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo _task_vo=new RecordVo("t_wf_task");
			_task_vo.setInt("task_id",task_id);
			_task_vo=dao.findByPrimaryKey(_task_vo);
			if("1".equals(_task_vo.getString("params"))) //自定义流程
			{
				int _ins_id=_task_vo.getInt("ins_id");
				sql="select task_id from t_wf_task where node_id in (select id from t_wf_node_manual where sp_level=(select max(sp_level) from  t_wf_node_manual where bs_flag='1' and ins_id="+_ins_id+" and tabid="+bo.getTabid()+" ) ";
				sql+=" and ins_id="+_ins_id+" )  and ins_id="+_ins_id+" and Task_state=3";
				RowSet rowSet=dao.search(sql);
				String _task_id="";
				while(rowSet.next())
				{
					_task_id+=rowSet.getString("task_id")+",";
				}
				if(_task_id.equals(task_id+","))
					flag=true;
				if(rowSet!=null)
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
     * @Title: getSpButtonFlagList
     * @Description: 获得待办按钮权限
     * @param flag  1:手工报批-选人   2：手工报批-指定审批人  3：自动审批
     * @return ArrayList [0]：退回  0|1  [1]：报批 0|1    [2]：办理 0|1
     */
    public ArrayList getSpButtonFlagList(String flag,String task_id,String ins_id,String tabid)
    {
    	ArrayList list=new ArrayList();
    	// 超级管理员拥有所有权限
		if (this.userView.isSuper_admin()) {
			// 发起的模板没有退回
			if ("0".equals(task_id)) {
				list.add("0");
			} else {
				list.add("1");
			}
			list.add("1");
			if ("0".equals(task_id)) {
			    list.add("0");
			} else {
			    list.add("1");
			}
		} else {
			// 其他用户走权限
			if ("0".equals(task_id)) {
				list.add("0");
			} else {
				if ("1".equals(this.templateTableBo.isStartNode(ins_id, task_id, tabid, this.templateTableBo.getSp_mode()))) {
					list.add("0");
				} else
					list.add("1");
			}
			// 是否需要审批
			if (!this.templateTableBo.isBsp_flag()) {
				list.add("0");
				list.add("1");
			} else {
				// 自动流转
				if (this.templateTableBo.getSp_mode() == 0) {
					list.add("0");
					list.add("1");
				} else if ("3".equals(flag)) {
					list.add("0");
					list.add("1");
				} else {
					if ("0".equals(task_id) && this.templateTableBo.getSp_mode() == 1) {
						list.add("1");
						list.add("0");
					} else {
						list.add("1");
						if ("1".equals(this.templateTableBo.isStartNode(ins_id, task_id, tabid, this.templateTableBo.getSp_mode()))) {
							list.add("0");
						} else if (this.userView.hasTheFunction("010703") || this.userView.hasTheFunction("32101")
								|| this.userView.hasTheFunction("33001001") || this.userView.hasTheFunction("33101001")) {
							list.add("1");
						} else if (this.userView.hasTheFunction("2701501") || this.userView.hasTheFunction("0C34801")
								|| this.userView.hasTheFunction("32001") || this.userView.hasTheFunction("324010102")
								|| this.userView.hasTheFunction("325010102")|| this.userView.hasTheFunction("3800701")) {
							list.add("1");
						} else
							list.add("0");
					}
				}
			}
		}
    	return list;
    }
    
    
    /**
	 * 获得下一审批层级，0为没有(自定义审批流)
	 */
	public int getNextSpLevel(int task_id,TemplateTableBo bo)
	{
		int level=0;
		try
		{
			String sql="select node_id,Params,ins_id from t_wf_task where   task_id="+task_id;
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(sql);
			String params="";
			int node_id=0;
			int ins_id=0;
			if(rset.next())
			{
				node_id=rset.getInt("node_id");
				params=rset.getString("params");
				ins_id=rset.getInt("ins_id");
			}
			if(params!=null&& "1".equals(params.trim())) //自定义审批流程
			{
				sql="select * from t_wf_node_manual where bs_flag='1'   and tabid="+bo.getTabid()+" and ins_id="+ins_id;
				sql+=" and sp_level>(select sp_level from t_wf_node_manual where id="+node_id+" ) and actorid is not null order by sp_level ";
				rset=dao.search(sql);
				if(rset.next())
					level=rset.getInt("sp_level");
				else
				{
					rset=dao.search("select nodename from t_wf_node where   tabid="+bo.getTabid()+" and node_id="+node_id); 
					if(rset.next())
					{
						if("begin".equalsIgnoreCase(rset.getString("nodename")))
							level=1; 
					}
				}
			}
			else
				level=0;
			
			if(rset!=null)
				rset.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return level;
	}
    
    
    
    
    /**
     * 退回审批单据
     * @param task_id 任务id
     * @param ins_id  实例id
     * @param priority优先级
     * @param cause   退回原因
     * @return
     * @throws GeneralException
     */
    public boolean rejectTask(String task_id,String ins_id,String priority,String cause) throws GeneralException {
    	boolean flag=true;
    	try
    	{
    		ContentDAO dao=new ContentDAO(this.conn); 
    		dao.update("update t_wf_task_objlink set submitflag=1 where ins_id="+ins_id+" and task_id="+task_id);
    		//检查当前任务的活动状态是否是结束状态  
			RowSet frowset=dao.search("select count(*) from t_wf_task where ( task_state='5' or task_state='4' ) and task_id="+task_id);
			if(frowset.next())
			{
				if(frowset.getInt(1)>0)
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("general.template.workflowbo.info9")));
			}
			RecordVo ins_vo=new RecordVo("t_wf_instance");
			ins_vo.setInt("ins_id",Integer.parseInt(ins_id));
			TemplateInterceptorAdapter.preHandle("templet_"+this.templateTableBo.getTabid(),this.templateTableBo.getTabid(),Integer.parseInt(task_id), null, "reject", this.userView,"");
			String actorid="";
			String actor_type="";
			this.templateTableBo.setIns_id(Integer.parseInt(ins_id));
			this.templateTableBo.getTasklist().add(task_id);
			WF_Instance ins=new WF_Instance(this.templateTableBo,this.conn);//因为驳回任务（rejectTask()是在WF_Instance这个类里写的。所以要初始化ins这个对象） 
			//原来微信审批没有获取邮件属性，导致报批审批后不会发送代办和报备提醒邮件
			String isSendMessage="0";
	        if(this.templateTableBo.isBemail()&&this.templateTableBo.isBsms())
	            isSendMessage="3";
	        else if(this.templateTableBo.isBemail())
	            isSendMessage="1";
	        else if(this.templateTableBo.isBsms())
	            isSendMessage="2";
	        boolean emailSelf = templateTableBo.isEmail_staff();// 是否通知本人 
	        if (isSendMessage != null && !"0".equals(isSendMessage)) {//是否抄送本人
                ins.setIsSendMessage(isSendMessage);
                ins.setEmail_staff_value(emailSelf?"1":"0"); // 通知本人
            }
			if(this.userView.getStatus()==0)//业务用户
			{
					actorid=this.userView.getUserName();
					actor_type="4";
			}
			else
			{
					actorid=this.userView.getDbname()+this.userView.getA0100();
					actor_type="1";
			}
			String tabid=String.valueOf(this.templateTableBo.getTabid());
	    	    String srcTab="templet_"+tabid; 
			ArrayList fieldlist=this.templateTableBo.getAllFieldItem();
			String opinionstr = " select * from templet_"+tabid+" ";
			opinionstr+=" where  exists (select null from t_wf_task_objlink where "+srcTab+".seqnum=t_wf_task_objlink.seqnum ";
			opinionstr+="  and task_id="+task_id+"   and submitflag=1  and (state is null or  state=0 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )) ";
			String opinion_field = this.templateTableBo.getOpinion_field();//审批意见指标
			
			
			WF_Actor wf_actor=new WF_Actor(actorid,actor_type);//流程参与者的相关信息。对话框中的数据全部会存进去 
			wf_actor.setActorname(this.userView.getUserFullName());			
			wf_actor.setContent(cause);//.replace("\r\n", "<p>").replace(" ", "&nbsp;")); //批复内容
			wf_actor.setEmergency(priority); //优先级 
			wf_actor.setSp_yj("02");
			if(this.templateTableBo.getSp_mode()!=1)//自动流转 
				wf_actor.setBexchange(true);
			String reject_type = "1";//=1 or null：逐级驳回  =2：驳回到发起人 
			reject_type = this.templateTableBo.getReject_type();
			
			for(int j=0;j<fieldlist.size();j++)
			{
				FieldItem fielditem=(FieldItem)fieldlist.get(j);
				String field_name=fielditem.getItemid();
				if(fielditem.isChangeAfter()&&opinion_field!=null&&opinion_field.length()>0&&opinion_field.equalsIgnoreCase(field_name))
				{
					String approveopinion =""; 
					approveopinion = this.templateTableBo.getApproveOpinion(ins_vo,task_id, wf_actor,""); 
					this.templateTableBo.updateApproveOpinion( "templet_"+tabid,ins_id,opinion_field+"_2",approveopinion,opinionstr);
					break;
				}
						
			}
			
			if("2".equalsIgnoreCase(reject_type)){//驳回到发起人
					ins.rejectTaskToSponsor(ins_vo,wf_actor,Integer.parseInt(task_id),this.userView);//此方法先结束掉当前任务，再创建一个新任务(更新t_wf_task和t_wf_task_objlink这两张表)
			}else{//逐级驳回
					ins.rejectTask(ins_vo,wf_actor,Integer.parseInt(task_id),this.userView);//此方法先结束掉当前任务，再创建一个新任务(更新t_wf_task和t_wf_task_objlink这两张表)
			}
			TemplateInterceptorAdapter.afterHandle(Integer.parseInt(task_id),0,Integer.parseInt(tabid),null,"reject",this.userView);
			if(frowset!=null)
				frowset.close();
    	}
    	catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
    	}
    	
    	
    	return flag;
    }
    
    
    /**
     * 任务办理
     * @param tabid    表ID
     * @param ins_id   实例ID
     * @param task_id  任务ID
     * @param isSelf   是否是个人业务申请
     * @param priority 优先级
     * @param content  意见
     * @return
     * @throws GeneralException
     */
    public boolean  approveTask(String tabid,String ins_id,String task_id,String isSelf,String priority,String content) throws GeneralException {
    	boolean flag=true;
    	try
    	{
    		ContentDAO dao=new ContentDAO(this.conn); 
    		RowSet frowset=null;
			RecordVo ins_vo=new RecordVo("t_wf_instance");
			ins_vo.setInt("ins_id",Integer.parseInt(ins_id));
			String actorid="";
			String actor_type="";
			this.templateTableBo.setIns_id(Integer.parseInt(ins_id));
			this.templateTableBo.getTasklist().add(task_id);
			if(this.userView.getStatus()==0)//业务用户
			{
				actorid=this.userView.getUserName();
				actor_type="4";
			}
			else
			{
				actorid=this.userView.getDbname()+this.userView.getA0100();
				actor_type="1";
			}
			WF_Actor wf_actor=new WF_Actor(actorid,actor_type);//流程参与者的相关信息。对话框中的数据全部会存进去
			if(this.templateTableBo.getSp_mode()!=1)//自动流转
				wf_actor.setBexchange(false);
			wf_actor.setActorname(this.userView.getUserFullName());			
			wf_actor.setContent(content);//.replace("\r\n", "<p>").replace(" ", "&nbsp;")); //批复内容
			wf_actor.setEmergency(priority); //优先级
			wf_actor.setSp_yj("01");//审批意见
			this.templateTableBo.setSp_yj("01");
			WF_Instance ins=new WF_Instance(this.templateTableBo,this.conn);//因为驳回任务（rejectTask()是在WF_Instance这个类里写的。所以要初始化ins这个对象）  
			
			
			if(!this.templateTableBo.isBsp_flag()||(this.templateTableBo.getSp_mode()==1&& "0".equals(task_id))) //无需审批，直接提交
			{
				submitTemplet(isSelf,String.valueOf(this.templateTableBo.getTabid()),dao);
			}
			else
			{
				ArrayList fieldlist=this.templateTableBo.getAllFieldItem();
				String opinionstr = " select * from templet_"+tabid+" ";
				opinionstr+=" where  exists (select null from t_wf_task_objlink where templet_"+tabid+".seqnum=t_wf_task_objlink.seqnum ";
				opinionstr+="  and ins_id="+ins_id+"   and submitflag=1  and (state is null or  state=0 ) and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ";
				String opinion_field = this.templateTableBo.getOpinion_field();
				for(int j=0;j<fieldlist.size();j++)
				{
					FieldItem fielditem=(FieldItem)fieldlist.get(j);
					String field_name=fielditem.getItemid();
					if(fielditem.isChangeAfter()&&opinion_field!=null&&opinion_field.length()>0&&opinion_field.equalsIgnoreCase(field_name))
					{
						String approveopinion ="";
					    approveopinion = this.templateTableBo.getApproveOpinion(ins_vo, task_id,wf_actor,""); 
					    this.templateTableBo.updateApproveOpinion( "templet_"+tabid,ins_id,opinion_field+"_2",approveopinion,opinionstr);
						break;
					}
						
				}
	
				if(this.templateTableBo.getOperationtype()==0)
				{
					
					HashMap subhm=this.templateTableBo.readUpdatesSetField(fieldlist);
					if(subhm.get("A01")==null)
					{
						throw new GeneralException(ResourceFactory.getProperty("error.input.a01read"));
					}
				}
				/**必填项校验*/
				this.templateTableBo.setValidateM_L(true); 
	    		if(this.templateTableBo.getSp_mode()==1) //手工审批
	    		{
	    			 manualApproveTask(ins,ins_vo,wf_actor,task_id,ins_id,tabid,dao);
	    		}
	    		else
	    		{ 
		    		ins.createNextTask(ins_vo,wf_actor,Integer.parseInt(task_id),this.userView);//在这个函数里面执行了expDataIntoArchive()
			/////////////////提交时，将个人附件归档///////////////
					ins_vo=dao.findByPrimaryKey(ins_vo);
					if(ins_vo.getString("finished").equalsIgnoreCase(String.valueOf(NodeType.TASK_FINISHED))){
						if(this.templateTableBo.hasFunction()){
							StringBuffer sb = new StringBuffer("");
							sb.append("select * from t_wf_file where ins_id="+ins_id+" and tabid="+tabid+" and attachmenttype=1");//个人附件
							if(!this.userView.isAdmin() && !"1".equals(this.userView.getGroupId()) && !this.templateTableBo.getIsIgnorePriv()){
								sb.append(" and filetype in (select id from mediasort where flag in "+this.templateTableBo.getMediaPriv()+")");
							}
						    frowset = dao.search(sb.toString());
							SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
							String time = sdf.format(new Date());//得到当前日期
						    switch(Sql_switcher.searchDbServer())
						    {
								case Constant.ORACEL:
							    {
							    	time="to_date('"+time+"','yyyy-mm-dd')";
							    	break;
							    }
								case Constant.MSSQL:
							    {
							    	time="'"+time+"'";
							    	break;
							    }
							}
							while(frowset.next()){
								sb.setLength(0);
								int flagid = frowset.getInt("filetype");//和mediasort表的id相关联
								int file_id =frowset.getInt("file_id");
								if(this.templateTableBo.getInfor_type()==1){
									String basepre = "";//要归档到的人员库
									String a0100 = "";//最终的人员编号
									if(this.templateTableBo.getOperationtype()==1 || this.templateTableBo.getOperationtype()==2){
										basepre = this.templateTableBo.getDestBase();
										String sourcea0100 = a0100 = frowset.getString("objectid");
										a0100 = (String)this.templateTableBo.getDestination_a0100().get(sourcea0100);
									}else{
										basepre =frowset.getString("basepre");
										a0100 =  frowset.getString("objectid");
									}
									sb.append("insert into "+basepre+"A00 (id,a0100,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
									sb.append(" select null,'"+a0100+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from "+basepre+"a00 where a0100='"+a0100+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,"+"'.'"+Sql_switcher.concat()+"ext,create_user,create_user,null,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);
								}else if(this.templateTableBo.getInfor_type()==2){
									String b0110 = frowset.getString("objectid");
									sb.append("insert into B00 (b0110,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
									sb.append(" select '"+b0110+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from B00 where b0110='"+b0110+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,"+"'.'"+Sql_switcher.concat()+"ext,create_user,create_user,null,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);
								}else if(this.templateTableBo.getInfor_type()==3){
									String e01a1 = frowset.getString("objectid");
									sb.append("insert into K00 (e01a1,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
									sb.append(" select '"+e01a1+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from B00 where e01a1='"+e01a1+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,"+"'.'"+Sql_switcher.concat()+"ext,create_user,create_user,null,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);
								}
								dao.update(sb.toString());
							} //while 遍历结束	
						} //flag=1  结束
						////////////////个人附件归档结束//////////////////
					}
	    		}
			}
			if(frowset!=null)
				frowset.close();
    	}
    	catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
    	}
    	return flag;
    }
    
    /**
     * 创建流程实例
     * @param conn
     * @param tablebo
     * @param selfapply
     * @param srcTab
     * @return
     * @throws GeneralException
     */
    private String createInstance(Connection conn,TemplateTableBo tablebo,String selfapply,String srcTab)throws GeneralException
	{
		ContentDAO dao=new ContentDAO(conn);
		RecordVo vo=new RecordVo("t_wf_instance");
		try
		{
	        //取得当前用户的人员范围 wangrd 2013-12-11
	        String operOrg = this.userView.getUnitIdByBusi("8");
	        if (!"".equals(operOrg)){
	            int k = operOrg.indexOf("`");
	            if (k>=0){
	                String s=operOrg.substring(0, k);
	                if (!"UN".equals(s))
	                  s = s.substring(2,s.length());
	                operOrg=s;
	            }           
	        }
	        else {
	            operOrg="UN";   
	        }
            IDGenerator idg=new IDGenerator(2,conn);		
            vo.setInt("ins_id",Integer.parseInt(idg.getId("wf_instance.ins_id")));

            vo.setString("name",tablebo.getName()+tablebo.getRecordBusiTopic(""));
            vo.setInt("tabid",tablebo.getTable_vo().getInt("tabid"));
            vo.setDate("start_date",DateStyle.getSystemTime());
            /**过程状态
             * 1 初始化
             * 2 运行中
             * 3 暂停
             * 4 终止
             * 5 结束
             * */
            vo.setString("finished","5"); //结束
            /**模板类型
             * =1业务模板
             * =2固定网页
             * */
            vo.setInt("template_type",1);
            /**平台用户 =0
             * 还是自助用户=4
             * */
            vo.setInt("actor_type",tablebo.getUserview().getStatus());
            if(tablebo.getUserview().getStatus()==0)
            	vo.setString("actorid",tablebo.getUserview().getUserName());
            else
            	vo.setString("actorid",tablebo.getUserview().getDbname()+tablebo.getUserview().getUserId());            	
            vo.setString("actorname",tablebo.getUserview().getUserFullName());
            /**是否有附件
             * =0 无附件
             * =1 有附件
             * */
            vo.setInt("bfile",0);
            //new_ins_vo=vo;
            if (!"".equals(operOrg)){
                vo.setString("b0110", operOrg);                
            }            
            dao.addValueObject(vo);
            /**加上任务处理*/
            RecordVo taskvo=new RecordVo("t_wf_task");
	
            taskvo.setInt("task_id",Integer.parseInt(idg.getId("wf_task.task_id")));
            taskvo.setString("task_topic",vo.getString("name"));
            taskvo.setInt("node_id",-1);
            taskvo.setInt("ins_id",vo.getInt("ins_id"));
            taskvo.setDate("start_date",DateStyle.getSystemTime());
            taskvo.setString("task_type","9");//结束   //""1");

            taskvo.setString("state","06"); //审批状态
            taskvo.setString("sp_yj","");//审批意见
            taskvo.setString("content","");//审批意见描述
            taskvo.setInt("bread",0);//是否已阅读
            taskvo.setDate("end_date",DateStyle.getSystemTime());	
            taskvo.setString("task_state",String.valueOf(NodeType.TASK_FINISHED));
            /**根据任务分配算法，具体对应到准*/
			taskvo.setString("actorid",vo.getString("actorid"));//当前对象	流程定义的参与者	 
			taskvo.setString("actor_type",vo.getString("actor_type"));
            taskvo.setString("a0100",vo.getString("actorid"));//人员编号 实际处理人员编码
            taskvo.setString("a0101",vo.getString("actorname"));//人员姓名 实际处理人员姓名
        	taskvo.setString("a0100_1",vo.getString("actorid"));//发送人
        	taskvo.setString("a0101_1",vo.getString("actorname"));//发送人姓名	            
            taskvo.setString("url_addr","");//审批网址
            taskvo.setString("params","");//参数	
            taskvo.setInt("flag",1);
            dao.addValueObject(taskvo); 
            //t_wf_task_objlink里写记录
        	StringBuffer strsql = new StringBuffer();
        	strsql.append("select * from ");
			strsql.append(srcTab);
			if("1".equals(selfapply))//员工通过自助平台发动申请
				strsql.append(" where a0100='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'");
			else
				strsql.append(" where submitflag=1");//对选中的人提交审批
			RowSet rowSet=dao.search(strsql.toString());
			ArrayList recordList = new ArrayList();
            while(rowSet.next())
			{
				String seqnum=(String)rowSet.getString("seqnum");
				
				RecordVo objlinkvo=new RecordVo("t_wf_task_objlink"); 
				objlinkvo.setString("seqnum",seqnum);
				objlinkvo.setInt("ins_id", Integer.parseInt(vo.getString("ins_id")));
				objlinkvo.setInt("task_id",taskvo.getInt("task_id"));
				objlinkvo.setInt("tab_id", vo.getInt("tabid"));
				objlinkvo.setInt("node_id",-1);
				objlinkvo.setInt("count",0);
				objlinkvo.setInt("submitflag", 1);
				objlinkvo.setInt("state",1);
				recordList.add(objlinkvo);
			} 
			dao.addValueObject(recordList);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return vo.getString("ins_id"); 
	}
    
    
    
    public void submitTemplet(String selfapply,String tabid,ContentDAO dao)throws GeneralException 
    {
    	try
    	{
    		if("1".equals(selfapply))
			{	 
    			this.templateTableBo.setBEmploy(true);
			}
    		this.templateTableBo.expDataIntoArchive(0);//将数据插入到相应的库中
    		String _sql="select * from ";
			String tablename=this.userView.getUserName()+"templet_"+tabid; 
			if("1".equalsIgnoreCase(selfapply))//员工通过自助平台发动申请
			{
				_sql+=" g_templet_"+tabid+" where a0100='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'";
				tablename="g_templet_"+tabid;
			}
			else
			{
				_sql+=this.userView.getUserName()+"templet_"+tabid+" where submitflag=1"; 
			}
			ArrayList personlist = this.templateTableBo.getPersonlist(this.templateTableBo.getInfor_type(),this.userView.getUserName()+"templet_"+tabid);
			WF_Instance ins=new WF_Instance(this.templateTableBo,this.conn);
			ins.insertKqApplyTable(_sql,tabid,selfapply,"03",tablename); //往考勤申请单中写入申请记录
			TemplateInterceptorAdapter.preHandle(tablename,Integer.parseInt(tabid), 0, paramBo, "submit", this.userView,"");
            /**不用审批的单据也创建一个实例号，把数据导入templet_xxx中去*/ 
			String ins_id=createInstance(this.conn,this.templateTableBo,selfapply,tablename);
			this.templateTableBo.saveSubmitTemplateData(this.userView.getUserName(),Integer.parseInt(ins_id),"",1);
			
			//原始单据归档 && 走审批流程的单据
			if("1".equals(this.templateTableBo.getArchflag()))
				this.templateTableBo.subDataToArchive("select * from templet_"+tabid+" where ins_id="+ins_id,"templet_"+tabid,"2");
			RowSet rowSet=dao.search("select * from id_factory where sequence_name='t_wf_file.file_id'");
			if(!rowSet.next())
			{
				StringBuffer insertSQL=new StringBuffer();
				insertSQL.append("insert into id_factory  (sequence_name, sequence_desc, minvalue, maxvalue, auto_increase, increase_order, prefix, suffix, currentid, id_length, increment_O)");
				insertSQL.append(" values ('t_wf_file.file_id', '附件号', 1, 99999999, 1, 1, Null, Null, 0, 8, 1)");
				ArrayList list=new ArrayList();
				dao.insert(insertSQL.toString(),list);				
			}
			//bug 44793 业务用户上传的附件，手机上关联的自助用户看不到
			ArrayList usernameList=PubFunc.SearchOperUserOrSelfUserName(this.userView);
			StringBuffer sqlBuf=new StringBuffer();
			sqlBuf.append( " select * from t_wf_file where ins_id=0 and tabid="+tabid+" and ( create_user='"+this.userView.getUserName()+"'");
			for(int i=0;i<usernameList.size();i++){
				String username=(String) usernameList.get(i);
				sqlBuf.append(" or  create_user='").append(username).append("'");
			}
			sqlBuf.append(" ) ");
			rowSet= dao.search(sqlBuf.toString());
			String sqlstrs = "";
			while(rowSet.next()){
				String file_id = rowSet.getString("file_id");
				IDGenerator idg = new IDGenerator(2, this.conn);
	    		String file_id2 = idg.getId("t_wf_file.file_id");
				sqlstrs=" insert into t_wf_file(file_id,content,filetype,objectid,basepre,attachmenttype,ins_id,tabid,ext,name,create_user,create_time) select "+file_id2+",content,filetype,objectid,basepre,attachmenttype,"+ins_id+",tabid,ext,name,create_user,create_time from t_wf_file where file_id="+file_id+"  ";
				dao.update(sqlstrs);
			}
			
////////////////个人附件归档 开始//////////////////
			StringBuffer sb = new StringBuffer("");
			if(this.templateTableBo.hasFunction()){
				sb.append("select * from t_wf_file where ins_id="+ins_id+" and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and attachmenttype=1");//个人附件
				if(!this.userView.isAdmin() && !"1".equals(this.userView.getGroupId()) && !this.templateTableBo.getIsIgnorePriv()){
					sb.append(" and filetype in (select id from mediasort where flag in "+this.templateTableBo.getMediaPriv()+")");
				}
				rowSet= dao.search(sb.toString());
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				String time = sdf.format(new Date());//得到当前日期
			    switch(Sql_switcher.searchDbServer())
			    {
					case Constant.ORACEL:
				    {
				    	time="to_date('"+time+"','yyyy-mm-dd')";
				    	break;
				    }
					case Constant.MSSQL:
				    {
				    	time="'"+time+"'";
				    	break;
				    }
				}
				while(rowSet.next()){
					sb.setLength(0);
					int flagid = rowSet.getInt("filetype");//和mediasort表的id相关联
					int file_id = rowSet.getInt("file_id");
					if(this.templateTableBo.getInfor_type()==1){
						String basepre = "";//要归档到的人员库
						String a0100 = "";//最终的人员编号
						if(this.templateTableBo.getOperationtype()==1 || this.templateTableBo.getOperationtype()==2){
							basepre = this.templateTableBo.getDestBase();
							String sourcea0100 = a0100 = rowSet.getString("objectid");
							a0100 = (String)this.templateTableBo.getDestination_a0100().get(sourcea0100);
						}else{
							basepre = rowSet.getString("basepre");
							a0100 = rowSet.getString("objectid");
						}
						sb.append("insert into "+basepre+"A00 (id,a0100,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
						sb.append(" select null,'"+a0100+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from "+basepre+"a00 where a0100='"+a0100+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,ext,create_user,create_user,null,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);
					}else if(this.templateTableBo.getInfor_type()==2){
						String b0110 = rowSet.getString("objectid");
						sb.append("insert into B00 (b0110,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
						sb.append(" select '"+b0110+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from B00 where b0110='"+b0110+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,ext,create_user,create_user,null,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);
					}else if(this.templateTableBo.getInfor_type()==3){
						String e01a1 = rowSet.getString("objectid");
						sb.append("insert into K00 (e01a1,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
						sb.append(" select '"+e01a1+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from B00 where e01a1='"+e01a1+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,ext,create_user,create_user,null,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);
					}
					dao.update(sb.toString());
				}
			}
			
			////////////////个人附件归档结束////////////////// 
			//再清空个人附件
			if("1".equals(selfapply)){//如果是员工自助申请，那么直接删除
				dao.update(" delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype=1) and objectid='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'");
			}else{
				sb.setLength(0);
				if(this.templateTableBo.getInfor_type()==1){
					sb.append("delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype=1) and basepre=? and objectid=?");
				}else{
					sb.append("delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype=1) and objectid=?");
				}
				dao.batchUpdate(sb.toString(),personlist);
			}
			TemplateInterceptorAdapter.afterHandle(0,Integer.parseInt(ins_id),Integer.parseInt(tabid),paramBo,"submit",this.userView);
			RecordVo ins_vo = new RecordVo("t_wf_instance");
            ins_vo.setInt("ins_id", Integer.parseInt(ins_id));
            ins_vo=dao.findByPrimaryKey(ins_vo);
            SendEmailToBeginUser(this.templateTableBo,ins_vo,dao,ins,tabid);//发送邮件给发起人
			//判断临时表里是否有记录，没有记录更新临时表的结构（解决模板变动时保留的多余字段sqlserver可能造成8060的问题）
			rowSet = dao.search(" select * from "+tablename+""); 
			if(!rowSet.next()){
				//把原公共附件清空
				dao.update(" delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype is null or attachmenttype=0)");
				dao.update(" drop table "+tablename+"");
				//创建表结构
				if("1".equalsIgnoreCase(selfapply)){
					this.templateTableBo.createTempTemplateTable();
				}else{
					this.templateTableBo.createTempTemplateTable(this.userView.getUserName());
				}
			}
			if(rowSet!=null)
				rowSet.close();
    	}
    	catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
    	}
    }
    
    
    /**
     * 报批操作
     * @param task_id   任务id
     * @param ins_id    实例id
     * @param priority  优先级
     * @param content   意见
     * @param actorid   审批人ID
     * @param actorname 审批人姓名
     * @param actor_type 审批人类别
     * @param isSelf
     */
    public void createNextTask(String task_id,String ins_id,String priority,String content,String actorid,String actorname,String actor_type,String selfapply)throws GeneralException 
    {
    	try
    	{
    		//流程中单据 查询节点类型
    		if(StringUtils.isNotEmpty(ins_id)&&!"0".equals(ins_id)&&StringUtils.isNotEmpty(task_id)&&!"0".equals(task_id)) {
    			WF_Actor actor=this.getNodeWFActor(ins_id, task_id, this.templateTableBo);
    			if(actor!=null) {
    				actor_type=actor.getActortype();
    			}
    		}
    		if(this.templateTableBo.getSp_mode()!=1)//自动
    		{
    			//节点参与者类型只有为用户时按照业务用户处理
	    		if(this.userView.getStatus()==0&&"4".equals(actor_type))//业务用户
				{
					actorid=this.userView.getUserName();
					actor_type="4";
					actorname=this.userView.getUserFullName();
				}
				else
				{
					actorid=this.userView.getDbname()+this.userView.getA0100();
					actor_type="1";
					actorname=this.userView.getUserFullName();
				}
    		} 
    		ArrayList fieldlist=this.templateTableBo.getAllFieldItem();
    		WF_Actor wf_actor=new WF_Actor(actorid,actor_type);
			wf_actor.setContent(content); 
			wf_actor.setEmergency(priority); //优先级
			wf_actor.setSp_yj("01");//审批意见   01:同意
			wf_actor.setActorname(actorname); 
			
			
			WF_Instance ins=new WF_Instance(this.templateTableBo,this.conn);
			ins.setModuleId("9");//微信端报批默认为自助端报批，不传此参数 t_wf_instance 表中bfile默认为2是业务模块，应该为3自助模块
			//原来微信审批没有获取邮件属性，导致报批审批后不会发送代办和报备提醒邮件
			String isSendMessage="0";
	        if(this.templateTableBo.isBemail()&&this.templateTableBo.isBsms())
	            isSendMessage="3";
	        else if(this.templateTableBo.isBemail())
	            isSendMessage="1";
	        else if(this.templateTableBo.isBsms())
	            isSendMessage="2";
	        if(!this.userView.hasTheFunction("2701515")&&!this.userView.hasTheFunction("0C34815")&&!this.userView.hasTheFunction("400040115")
	                &&!this.userView.hasTheFunction("32015")&&!this.userView.hasTheFunction("325010115")&&!this.userView.hasTheFunction("324010115")&&!this.userView.hasTheFunction("010701")&&!this.userView.hasTheFunction("32115")&&!this.userView.hasTheFunction("3800715"))
	            isSendMessage="0";
	        boolean emailSelf = templateTableBo.isEmail_staff();// 是否通知本人 
	        if (isSendMessage != null && !"0".equals(isSendMessage)) {//是否抄送本人
                ins.setIsSendMessage(isSendMessage);
                ins.setEmail_staff_value(emailSelf?"1":"0"); // 通知本人
            } 
    		if("0".equals(task_id))
    		{
    			//单据正在处理，不允许重复申请
    			String info=this.templateTableBo.validateExistData();
    			if(info.length()>0)
    				throw new GeneralException(info);
    			if(this.templateTableBo.getSp_mode()!=1)//自动流转
    				wf_actor.setBexchange(false);
    			String srcTab=this.userView.getUserName()+"templet_"+this.templateTableBo.getTabid(); 
    			 if("1".equalsIgnoreCase(selfapply))
    			 {
    				 this.templateTableBo.setBEmploy(true);
    				 srcTab="g_templet_"+this.templateTableBo.getTabid();
    			 }
    			 ArrayList whlList=new ArrayList(); 
    			 whlList.add("");
    			 //拆单
    			 if(!"1".equalsIgnoreCase(selfapply))
    			 { 
    					 whlList=this.templateTableBo.getSplitInstanceWhl();
    					 if(whlList.size()==1&&((String)whlList.get(0)).trim().length()==0)//不拆单
    					 {
    						 
    					 }
    					 else
    						 wf_actor.setSpecialRoleUserList(new ArrayList()); 
    			 }
    			 ins.setbSelfApply(this.templateTableBo.isBEmploy());
    			 startTask(whlList,String.valueOf(this.templateTableBo.getTabid()),selfapply,wf_actor,srcTab,ins);
    			 if(!this.isWeiX) {//二维码进入时 更新t_wf_instance b0110
    				 RecordVo task_vo=ins.getTask_vo();
    				 updateWFInstance(task_vo, String.valueOf(this.templateTableBo.getTabid()));
    			 }
    		}
    		else
    		{
				//bug 40922和泓任务监控部分结束单子浏览里面没有人。微信钉钉不会判断t_wf_task_objlink表中submitflag值是否为0，如果为0，后面产生节点信息t_wf_task_objlink表中不产生记录。
    			String updateSubmitFlagSql="update t_wf_task_objlink set submitflag=1 where ins_id=? and task_id=? and submitflag=0";
    			ArrayList list=new ArrayList();
    			list.add(Integer.parseInt(ins_id));
    			list.add(Integer.parseInt(task_id));
    			ContentDAO dao=new ContentDAO(this.conn);
    			dao.update(updateSubmitFlagSql,list);
    			RecordVo ins_vo=new RecordVo("t_wf_instance");
    			ins_vo.setInt("ins_id",Integer.parseInt(ins_id)); 
    			ins.setObjs_sql(ins.getObjsSql(Integer.parseInt(ins_id),Integer.parseInt(task_id),3,String.valueOf(this.templateTableBo.getTabid()),this.userView,""));//作用是
    			ins_vo.setInt("ins_id",Integer.parseInt(ins_id));
    			if(this.templateTableBo.getSp_mode()!=1)//自动
    			{ 
        		    wf_actor.setBexchange(false);
    				autoAppeal(task_id,ins_id,wf_actor,fieldlist,ins,ins_vo);
    			}
    			else  //手工报批
    			{
    				manualAppeal(task_id,ins_id,wf_actor,fieldlist,ins,ins_vo);
    			}
    		}
    		
    	}
    	catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
    	}
    }
    
    /***
     * 查询节点参与者类型
     * @param ins_id
     * @param task_id
     * @param tablebo
     * @return
     * @throws GeneralException
     */
    private WF_Actor getNodeWFActor(String ins_id,String task_id,TemplateTableBo tablebo)throws GeneralException{
    	String sql="select node_id from t_wf_task where ins_id=? and task_id=?";
    	ContentDAO dao=new ContentDAO(this.conn);
    	RowSet rs=null;
    	WF_Node node=new WF_Node(tablebo, conn);
    	try {
    		rs=dao.search(sql, Arrays.asList(ins_id,task_id));
    		int node_id=0;
    		if(rs.next()) {
    			node_id=rs.getInt("node_id");
    		}
    		return node.getWf_Actor(node_id);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
    	return null;
    }
    
    /**
     * 二维码进入报批时自动更新t_wf_instance b0110
     * 单据中有变化后的单位/部门 部门优先，e0122_2>b0110_2
     * @return
     */
    private void updateWFInstance(RecordVo task_vo,String tabid) {
    	int task_id=task_vo.getInt("task_id");
    	int ins_id=task_vo.getInt("ins_id");
    	ContentDAO dao=new ContentDAO(this.conn);
    	RowSet rs=null;
    	try {
    		String seqnum="";
			rs=dao.search("select seqnum from t_wf_task_objlink where ins_id=? and task_id = ? and tab_id=? ",Arrays.asList(ins_id,task_id,tabid));
			if(rs.next()) {
				seqnum=rs.getString("seqnum");
				if(StringUtils.isNotEmpty(seqnum)) {
					ArrayList<LazyDynaBean> list=(ArrayList<LazyDynaBean>)ExecuteSQL.executeMyQuery(" select * from templet_"+tabid+" where  seqnum='"+seqnum+"'");
					if(list!=null&&list.size()>0) {
						LazyDynaBean bean=list.get(0);
						String b0110_2=(String)bean.get("b0110_2");
						String e0122_2=(String)bean.get("e0122_2");
						String b0110="";
						if(StringUtils.isNotEmpty(e0122_2)) {
							b0110=e0122_2;
						}else if(StringUtils.isNotEmpty(b0110_2)) {
							b0110=b0110_2;
						}
						if(StringUtils.isNotEmpty(b0110)) {
							dao.update("update t_wf_instance set b0110='"+b0110+"' where ins_id="+ins_id+"");
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
    }
    
    /**
     * @throws GeneralException  
     * @Title: getSpecialRoleUserStr 
     * @Description:  得到特殊角色指定的用户   nodeid:xxxx,nodeid:yyyy
     * @return String   
     * @throws 
    */

    /**
     * @throws GeneralException  
     * @Title: autoValidate 
     * @Description: TODO
     * @param taskId
     * @param insId
     * @param object
     * @param object2
     * @param selfapply
     * @param templateTableBo2 void   
     * @throws 
    */

    /**
     * 手工报批
     * @param taskid    任务id
     * @param ins_id    实例id
     * @param wf_actor  审批人对象
     * @param fieldlist 模板指标列表
     * @param ins       实例对象
     * @param ins_vo    
     * @author dengcan
     * @serialData 2014-1-18
     * @throws GeneralException
     */
    private void manualAppeal(String taskid,String ins_id,WF_Actor wf_actor,ArrayList fieldlist,WF_Instance ins,RecordVo ins_vo)throws GeneralException 
    {
    	try
    	{ 
    		String tabid=String.valueOf(this.templateTableBo.getTabid());
			this.templateTableBo.setIns_id(Integer.parseInt(ins_id));
			this.templateTableBo.getTasklist().add(taskid); 
			String opinionstr = " select * from templet_"+tabid+" ";
			opinionstr+=" where  exists (select null from t_wf_task_objlink where templet_"+tabid+".seqnum=t_wf_task_objlink.seqnum ";
			opinionstr+="  and ins_id="+ins_id+"   and submitflag=1  and (state is null or  state=0 ) and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ";
			String opinion_field = this.templateTableBo.getOpinion_field();
			for(int j=0;j<fieldlist.size();j++)
			{
				FieldItem fielditem=(FieldItem)fieldlist.get(j);
				String field_name=fielditem.getItemid();
				if(fielditem.isChangeAfter()&&opinion_field!=null&&opinion_field.length()>0&&opinion_field.equalsIgnoreCase(field_name))
				{
					String approveopinion =""; 
				    approveopinion = this.templateTableBo.getApproveOpinion(ins_vo,taskid, wf_actor,""); 
			        this.templateTableBo.updateApproveOpinion( "templet_"+tabid,ins_id,opinion_field+"_2",approveopinion,opinionstr);
					break;
				}
					
			}
			ins.reAssignTask(ins_vo,wf_actor,Integer.parseInt(taskid),this.userView);
				 
    	}
    	catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
    	}
    }
    
    
    /**
     * 自动报批
     * @param taskid    任务id
     * @param ins_id    实例id
     * @param wf_actor  审批人对象
     * @param fieldlist 模板指标列表
     * @param ins       实例对象
     * @param ins_vo    
     * @author dengcan
     * @serialData 2014-1-18
     */
    private void autoAppeal(String taskid,String ins_id,WF_Actor wf_actor,ArrayList fieldlist,WF_Instance ins,RecordVo ins_vo)throws GeneralException 
    {
    		
    	try
    	{
    		boolean isEnd = false;
    		RowSet rowSet=null;
	    	String tabid=String.valueOf(this.templateTableBo.getTabid());
	    	String srcTab="templet_"+tabid; 
			ContentDAO dao=new ContentDAO(this.conn); 
			String actorid="";
			String actor_type="";
			this.templateTableBo.setIns_id(Integer.parseInt(ins_id)); 
			 
				/**驳回意见*/
			ins.setIns_id(Integer.parseInt(ins_id));
			
			
			
			ins.createNextTask(ins_vo,wf_actor,Integer.parseInt(taskid),this.userView);//在这个函数里面执行了expDataIntoArchive()
	        /////////////////提交时，将个人附件归档///////////////
			ins_vo=dao.findByPrimaryKey(ins_vo);
			if(ins_vo.getString("finished").equalsIgnoreCase(String.valueOf(NodeType.TASK_FINISHED))){
				TemplateInterceptorAdapter.afterHandle(0,ins_vo.getInt("ins_id"),this.templateTableBo.getTabid(),paramBo,"submit",this.userView);
				isEnd=true;
				SendEmailToBeginUser(this.templateTableBo,ins_vo,dao,ins,tabid);//发送通知给发起人
				//微信批准没有把附件信息以二进制形式保存到数据库中，导致通过员工管理无法查看附件
				submitAttachmentFile(ins_id,this.templateTableBo,"1",tabid);
					
			}else{
				if(!isEnd){//bug 36386 生成结束节点时也会保存审批意见，造成审批意见保存两次。如果下一个节点是结束节点，本节点不再保存审批意见。
					String opinionstr = " select * from templet_"+tabid+" ";
					opinionstr+=" where  exists (select null from t_wf_task_objlink where "+srcTab+".seqnum=t_wf_task_objlink.seqnum ";
					opinionstr+="  and task_id="+taskid+"   and submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )) ";
					String opinion_field = this.templateTableBo.getOpinion_field();//审批意见指标
					for(int j=0;j<fieldlist.size();j++)
					{
						FieldItem fielditem=(FieldItem)fieldlist.get(j);
						String field_name=fielditem.getItemid();
						if(fielditem.isChangeAfter()&&opinion_field!=null&&opinion_field.length()>0&&opinion_field.equalsIgnoreCase(field_name))
						{
							String approveopinion =""; 
							approveopinion = this.templateTableBo.getApproveOpinion(ins_vo,taskid, wf_actor,""); 
							this.templateTableBo.updateApproveOpinion( "templet_"+tabid,ins_id,opinion_field+"_2",approveopinion,opinionstr);
							break;
						}
								
 
					}
				}
				autoApplyTask(ins, ins_vo, wf_actor.getContent(), wf_actor.getEmergency(), wf_actor.getSp_yj(), wf_actor.getActorname(), new HashMap(), templateTableBo, dao);
				if(ins_vo.getString("finished").equalsIgnoreCase(String.valueOf(NodeType.TASK_FINISHED))){
					TemplateInterceptorAdapter.afterHandle(0,ins_vo.getInt("ins_id"),this.templateTableBo.getTabid(),paramBo,"submit",this.userView);
					isEnd=true;
					SendEmailToBeginUser(this.templateTableBo,ins_vo,dao,ins,tabid);//发送通知给发起人
					//微信批准没有把附件信息以二进制形式保存到数据库中，导致通过员工管理无法查看附件
					submitAttachmentFile(ins_id,this.templateTableBo,"1",tabid);
				}
				else
					TemplateInterceptorAdapter.afterHandle(Integer.parseInt(taskid),0,Integer.parseInt(tabid),null,"appeal",this.userView);
			}
				if(rowSet!=null)
					rowSet.close();
    	}
    	catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
    	}
    	
    }
    
    
    /**
     * 发起流程
     * @param whlList 拆单条件
     * @param tabid   模板id
     * @param selfapply  是否来自个人业务申请
     * @param wf_actor   审批人
     * @param src_Tab    原表名
     */
    private void startTask(ArrayList whlList,String tabid,String selfapply,WF_Actor wf_actor,String src_Tab,WF_Instance ins)throws GeneralException 
    {
    	try
    	{
    		 ContentDAO dao=new ContentDAO(this.conn); 
    		 RowSet rs=null;
    		 RowSet rowSet=null;
			 //公共附件、个人附件微信提交，附件没有从t_wf_file中删除，下次用户再次打开，附件还会显示在单子中
    		 String srcTab=this.userView.getUserName()+"templet_"+tabid;
	    	 if("1".equalsIgnoreCase(selfapply)){//员工通过自助平台发动申请
	    		 srcTab="g_templet_"+tabid;
	    	 }
    		 ArrayList personList= getPersonlist(templateTableBo.getInfor_type(),srcTab,selfapply);
	    	 for(int i=0;i<whlList.size();i++)
			 {
				
				RecordVo ins_vo=new RecordVo("t_wf_instance");	 
				String whl=(String)whlList.get(i);
				if("1".equalsIgnoreCase(selfapply))
					ins.setObjs_sql(ins.getObjsSql(0,0,1,tabid,this.userView,""));
				else
					ins.setObjs_sql(ins.getObjsSql(0,0,2,tabid,this.userView,whl));  
				if(ins.createInstance(ins_vo,wf_actor,whl))//将数据插入到t_wf_instance、t_wf_task_objlink和t_wf_task中
				{
				    boolean isOriData=false; // 表单数据没到临时表
					String sql="select count(*) as nrec from ";
					if("1".equalsIgnoreCase(selfapply))//员工通过自助平台发动申请
						sql+=" g_templet_"+tabid+" where a0100='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'";
					else
					{
						sql+=this.userView.getUserName()+"templet_"+tabid+" where submitflag=1"+whl;
					
					}
					rowSet=dao.search(sql);
					if(rowSet.next())
					{
						if(rowSet.getInt(1)>0)
						{
						    isOriData=true;
						}
					}
					if(isOriData)
	                {
	                    insertKQdata(selfapply,0,tabid,whl,dao,ins);
	                    
	                    String approve_opinion = this.templateTableBo.getApproveOpinion(ins_vo, "0",wf_actor,"");
	                    this.templateTableBo.setApprove_opinion(approve_opinion);
	                    if("1".equalsIgnoreCase(selfapply))
	                        this.templateTableBo.saveSubmitTemplateData(ins_vo.getInt("ins_id"));
	                    else//将数据插入到template_tabid中
	                        this.templateTableBo.saveSubmitTemplateData(this.userView.getUserName(),ins_vo.getInt("ins_id"),whl);
 
	                    TemplateInterceptorAdapter.afterHandle(0,ins_vo.getInt("ins_id"),Integer.parseInt(tabid),paramBo,"apply",this.userView);
	                    
	                    //提交
	                    String sql_fiel_=" select * from t_wf_file where ins_id="+ins_vo.getInt("ins_id")+" and tabid="
		                        +tabid+" and create_user='"+this.userView.getUserName()+"' ";
	                    String sqlfile=" select * from t_wf_file where ins_id=0 and tabid="
	                        +tabid+" and create_user='"+this.userView.getUserName()+"' ";
	                        if(this.templateTableBo.getInfor_type()==1){//人员
	                        	String sqlStr="";
	                        	//liuyz bug27262 选人较多时，in后面超过1000,报ORA-01795
	                        	switch (Sql_switcher.searchDbServer()) {
	        					case Constant.MSSQL:
	        					    sqlStr+=" lower(basepre+objectid) " ;
	        						 break;
	        					default:
	        					    sqlStr+=" lower(concat(basepre,objectid)) " ;
	        						 break;
	                        	}
	                        	/*for(int i=0;i<personlist.size();i++){
	                        		ArrayList list = (ArrayList)personlist.get(i);
	                        		String basepre = (String)list.get(0);
	                        		String a0100 = (String)list.get(1);
	                        		if(i==0)
	                        			personarr += " in('"+basepre+a0100+"'";
	                        		else if(i%990==0&&personlist.size()%990>0)
	                        		{
	                        			personarr+=") or "+sqlStr+" in('"+basepre+a0100+"'";
	                        		}
	                        		else{
	                        			personarr += ",'"+basepre+a0100+"'";
	                        		}
	                        		if(i==personlist.size()-1)
	                        		{
	                        			personarr +=")";
	                        		}
	                        	}*/
	                        	/*if("".equals(personarr))
	                        		personarr = "in ('')";*/
	                        	sqlfile+="and ("+sqlStr+" in ('"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"'))";
	                        	sql_fiel_+="and ("+sqlStr+" in ('"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"'))";
	                        }
	                        sqlfile+= " order by file_id";
	                    rs=dao.search(sql_fiel_);
	                    boolean flag=true;
	                    if(rs.next()) {
	                    	flag=false;
	                    }
	                    if(flag) {
	                    	rs = dao.search(sqlfile);
		                    String sqlstrs = "";
		                    while(rs.next()){
		                        String file_id = rs.getString("file_id");
		                        IDGenerator idg = new IDGenerator(2, this.conn);
		                        String file_id2 = idg.getId("t_wf_file.file_id");
		                        //liuyz bug25774 兼容6.3上传的附件，须将content字段内容也保留
		                        sqlstrs=" insert into t_wf_file(file_id,filepath,filetype,objectid,basepre,attachmenttype,ins_id,tabid,ext,name,"
		                            +"create_user,create_time,fullname,content,state,i9999) "
		                            +"select "+file_id2+",filepath,filetype,objectid,basepre,attachmenttype,"+ins_vo.getInt("ins_id")
		                            +",tabid,ext,name,create_user,create_time,fullname,content,case when state is null then 0 else state end,i9999 from t_wf_file where file_id="+file_id+"  ";
		                        dao.update(sqlstrs);
		                    } 
	                    }
	                    
	                }
	                else
	                {
	                	
	                	TemplateInterceptorAdapter.afterHandle(0,ins_vo.getInt("ins_id"),Integer.parseInt(tabid),paramBo,"apply",this.userView);
 
 
	                    insertKQdata(selfapply,ins_vo.getInt("ins_id"),tabid,"",dao,ins);
	                } 
                    //提交
					String sqlfile_=" select * from t_wf_file where ins_id="+ins_vo.getInt("ins_id")+" and tabid="
	                        +tabid+" and create_user='"+this.userView.getUserName()+"' ";
                    String sqlfile=" select * from t_wf_file where ins_id=0 and tabid="
                        +tabid+" and create_user='"+this.userView.getUserName()+"' ";
                        if(this.templateTableBo.getInfor_type()==1){//人员
                        	String sqlStr="";
                        	//liuyz bug27262 选人较多时，in后面超过1000,报ORA-01795
                        	switch (Sql_switcher.searchDbServer()) {
        					case Constant.MSSQL:
        					    sqlStr+=" lower(basepre+objectid) " ;
        						 break;
        					default:
        					    sqlStr+=" lower(concat(basepre,objectid)) " ;
        						 break;
                        	}
                        	/*for(int i=0;i<personlist.size();i++){
                        		ArrayList list = (ArrayList)personlist.get(i);
                        		String basepre = (String)list.get(0);
                        		String a0100 = (String)list.get(1);
                        		if(i==0)
                        			personarr += " in('"+basepre+a0100+"'";
                        		else if(i%990==0&&personlist.size()%990>0)
                        		{
                        			personarr+=") or "+sqlStr+" in('"+basepre+a0100+"'";
                        		}
                        		else{
                        			personarr += ",'"+basepre+a0100+"'";
                        		}
                        		if(i==personlist.size()-1)
                        		{
                        			personarr +=")";
                        		}
                        	}*/
                        	/*if("".equals(personarr))
                        		personarr = "in ('')";*/
                        	sqlfile+="and ("+sqlStr+" in ('"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"'))";
                        	sqlfile_+="and ("+sqlStr+" in ('"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"'))";
                        }
                        sqlfile+= " order by file_id";
                        rs = dao.search(sqlfile_);
                        boolean flag=true;
                        if(rs.next()) {
                        	flag=false;
                        }
                        if(flag) {
                        	rs = dao.search(sqlfile);
                            String sqlstrs = "";
                            while(rs.next()){
                                String file_id = rs.getString("file_id");
                                IDGenerator idg = new IDGenerator(2, this.conn);
                                String file_id2 = idg.getId("t_wf_file.file_id");
                                //liuyz bug25774 兼容6.3上传的附件，须将content字段内容也保留
                                sqlstrs=" insert into t_wf_file(file_id,filepath,filetype,objectid,basepre,attachmenttype,ins_id,tabid,ext,name,"
                                    +"create_user,create_time,fullname,content,i9999,state) "//bug 51078
                                    +"select "+file_id2+",filepath,filetype,objectid,basepre,attachmenttype,"+ins_vo.getInt("ins_id")
                                    +",tabid,ext,name,create_user,create_time,fullname,content,i9999,state from t_wf_file where file_id="+file_id+"  ";
                                dao.update(sqlstrs);
                            } 
                        
                        }
                    
					int ins_id = ins_vo.getInt("ins_id");
					
					if(ins_vo.getString("finished").equalsIgnoreCase(String.valueOf(NodeType.TASK_FINISHED))){
					    TemplateInterceptorAdapter.afterHandle(0,ins_vo.getInt("ins_id"),Integer.parseInt(tabid),paramBo,"submit",this.userView); 
 
						SendEmailToBeginUser(this.templateTableBo,ins_vo,dao,ins,tabid);//发送通知给发起人
						//微信批准没有把附件信息以二进制形式保存到数据库中，导致通过员工管理无法查看附件
						submitAttachmentFile(String.valueOf(ins_id),this.templateTableBo,"1",tabid);
					}else {
						ins_vo=autoApplyTask(ins, ins_vo, wf_actor.getContent(), wf_actor.getEmergency(), wf_actor.getSp_yj(), wf_actor.getActorname(), new HashMap(), templateTableBo, dao);
					}
				}
				
				SynOaService sos=new SynOaService();
				String tab_ids=sos.getTabids();
				if(tab_ids.indexOf(","+tabid+",")!=-1)
				{
						if("1".equals((String)sos.getTabOptMap().get(tabid.trim())))
						{
							String _info=sos.synOaService(String.valueOf(ins.getTask_vo().getInt("task_id")),tabid,this.userView);  //创建成功返回1，否则返回详细错误信息
							if(!"1".equals(_info))
								throw GeneralExceptionHandler.Handle(new Exception(_info));	
						}
				}
				 
				
			 } //循环whlList 结束 
			 //公共附件、个人附件微信提交，删除公共和个人附件
	    	//把原公共附件清空（不能直接清空。当发起人选了4个人，只报批了三个的时候，就不能清空）
	            rowSet=dao.search("select count(*) from "+srcTab+" where submitflag=0");
	            boolean deleteFlag=true;
	            if(rowSet.next()){
	                if(rowSet.getInt(1)!=0){
	                    deleteFlag=false;
	                }
	            }
	            if(deleteFlag){
	                dao.update(" delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype is null or attachmenttype=0)");
	            }
	           //再清空个人附件
	            StringBuffer sb = new StringBuffer("");
	            if("1".equalsIgnoreCase(selfapply)){//如果是员工自助申请，那么直接删除
	                   dao.update(" delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype=1) and objectid='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'");
	            }else{
	               sb.setLength(0);
	               if(templateTableBo.getInfor_type()==1){
	                   sb.append("delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype=1) and basepre=? and objectid=?");
	               }else{
	                   sb.append("delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype=1) and objectid=?");
	               }
	               dao.batchUpdate(sb.toString(),personList);
	            }
				//判断临时表里是否有记录，没有记录更新临时表的结构（解决模板变动时保留的多余字段sqlserver可能造成8060的问题）
		    	 rowSet = dao.search(" select * from "+src_Tab+""); 
				 if(!rowSet.next()){
						dao.update(" drop table "+src_Tab+"");
						//创建表结构
						if("1".equalsIgnoreCase(selfapply)){
							this.templateTableBo.createTempTemplateTable();
						}else{
							this.templateTableBo.createTempTemplateTable(this.userView.getUserName());
						}
				 }
				 PubFunc.closeDbObj(rowSet);
    	}
    	catch (Exception ex) {
            ex.printStackTrace(); 
            throw GeneralExceptionHandler.Handle(ex);
    	}
    	
    }
    
    
    
    
    
    /**
     * @throws GeneralException  
     * @Title: insertKQdata 
     * @Description:写入考勤表数据
     * @param selfapply
     * @param int1
     * @param tabid
     * @param string
     * @param dao
     * @param ins void   
     * @throws 
    */
    private void insertKQdata(String selfapply,int ins_id,String tabid,String whl,ContentDAO dao,WF_Instance ins) throws GeneralException {

        try
        {
            String _sql="select * from ";
            String tablename=this.userView.getUserName()+"templet_"+tabid; 
            if(ins_id==0)
            { 
                if("1".equalsIgnoreCase(selfapply))//员工通过自助平台发动申请
                {
                    _sql+=" g_templet_"+tabid+" where a0100='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'";
                    tablename="g_templet_"+tabid;
                }
                else
                {
                    _sql+=this.userView.getUserName()+"templet_"+tabid+" where submitflag=1"+whl;
                
                }  
            }
            else
            {
                tablename="templet_"+tabid; 
                _sql+=" templet_"+tabid+" where ins_id="+ins_id;
            }
                
            try
            {
                ins.insertKqApplyTable(_sql,tabid,selfapply,"02",tablename); //往考勤申请单中写入报批记录
            }
            catch(Exception e)
            {
                dao.delete("delete from t_wf_instance where ins_id="+ins.getIns_id(),new ArrayList());
                dao.delete("delete from t_wf_task where ins_id="+ins.getIns_id(),new ArrayList());
                dao.delete("delete from t_wf_task_objlink where ins_id="+ins.getIns_id(),new ArrayList());
                throw GeneralExceptionHandler.Handle(e);
            }
        }
        catch(Exception ex)
        {  
                throw GeneralExceptionHandler.Handle(ex); 
        }
    
        
    }

    /**
     * 手工审批-批准入库
     * @param ins
     * @param ins_vo
     * @param wf_actor
     * @param taskid
     * @param ins_id
     * @param tabid
     * @param dao
     * @throws GeneralException
     */
    public void manualApproveTask(WF_Instance ins,RecordVo ins_vo,WF_Actor  wf_actor,String taskid,String ins_id,String tabid,ContentDAO dao)throws GeneralException 
    {
    	
    	try
    	{ 
	    	RowSet frowset=null;
	    	if(ins.finishTask(ins_vo,wf_actor,Integer.parseInt(taskid),this.userView,"5"))
			{
				if(ins.getTask_vo().getInt("task_id")!=0) ////往考勤申请单中写入记录
				{
					StringBuffer strsql=new StringBuffer("");
					strsql.append("select * from templet_"+tabid); 
					strsql.append(" where  exists (select null from t_wf_task_objlink where templet_"+tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+tabid+".ins_id=t_wf_task_objlink.ins_id ");
					strsql.append("  and task_id="+ins.getTask_vo().getInt("task_id")+" and tab_id="+tabid+" and state=1 ) ");
					
					String operState="03"; 
					ins.insertKqApplyTable(strsql.toString(),tabid,"0",operState,"templet_"+tabid); //往考勤申请单中写入报批记录
					
				}
				boolean bhave=ins.isHaveObjTheTask(Integer.parseInt(taskid));
				this.templateTableBo.setSp_yj((String)wf_actor.getSp_yj());
				this.templateTableBo.expDataIntoArchive(ins.getTask_vo().getInt("task_id"));
				
	/////////////////提交时，将个人附件归档///////////////
				if( this.templateTableBo.hasFunction()){
					StringBuffer sb = new StringBuffer("");
					sb.append("select * from t_wf_file where ins_id="+ins_id+" and tabid="+tabid+" and attachmenttype=1");//个人附件
					if(!this.userView.isAdmin() && !"1".equals(this.userView.getGroupId()) && !this.templateTableBo.getIsIgnorePriv()){
						sb.append(" and filetype in (select id from mediasort where flag in "+this.templateTableBo.getMediaPriv()+")");
					}
				    frowset = dao.search(sb.toString());
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
					String time = sdf.format(new Date());//得到当前日期
				    switch(Sql_switcher.searchDbServer())
				    {
						case Constant.ORACEL:
					    {
					    	time="to_date('"+time+"','yyyy-mm-dd')";
					    	break;
					    }
						case Constant.MSSQL:
					    {
					    	time="'"+time+"'";
					    	break;
					    }
					}
					while(frowset.next()){
						sb.setLength(0);
						int flagid = frowset.getInt("filetype");//和mediasort表的id相关联
						int file_id = frowset.getInt("file_id");
						if(this.templateTableBo.getInfor_type()==1){
							String basepre = "";//要归档到的人员库
							String a0100 = "";//最终的人员编号
							if(this.templateTableBo.getOperationtype()==1 || this.templateTableBo.getOperationtype()==2){
								basepre = this.templateTableBo.getDestBase();
								String sourcea0100 = a0100 = frowset.getString("objectid");
								a0100 = (String)this.templateTableBo.getDestination_a0100().get(sourcea0100);
							}else{
								basepre = frowset.getString("basepre");
								a0100 = frowset.getString("objectid");
							}
							sb.append("insert into "+basepre+"A00 (id,a0100,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
							sb.append(" select null,'"+a0100+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from "+basepre+"a00 where a0100='"+a0100+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,ext,create_user,create_user,null,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);
						}else if(this.templateTableBo.getInfor_type()==2){
							String b0110 =frowset.getString("objectid");
							sb.append("insert into B00 (b0110,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
							sb.append(" select '"+b0110+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from B00 where b0110='"+b0110+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,ext,create_user,create_user,null,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);
						}else if(this.templateTableBo.getInfor_type()==3){
							String e01a1 =frowset.getString("objectid");
							sb.append("insert into K00 (e01a1,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
							sb.append(" select '"+e01a1+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from B00 where e01a1='"+e01a1+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,ext,create_user,create_user,null,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);
						}
						dao.update(sb.toString());
					} //while 遍历结束	
				} //flag=1 结束
				////////////////个人附件归档结束//////////////////
				
				 
				StringBuffer buf=new StringBuffer();
					/**如果当前流程实例中存在正在运行中的任务，重新把实例置为运行状态*/
				if(ins.isHaveRuningTask(ins_vo.getInt("ins_id"))||bhave)
				{
						/**流程实例启动*/
						
						buf.append("update t_wf_instance set end_date=null,finished='2' where ins_id=");
						buf.append(ins_vo.getInt("ins_id"));
						dao.update(buf.toString());
				}
				buf.setLength(0);
				buf.append("update t_wf_task set flag=1");
				buf.append(" where task_id=");
				buf.append(ins.getTask_vo().getInt("task_id")/*taskid*/);
				dao.update(buf.toString());
				
				/*判断是否 调用中建接口
				 * sso_templetOwner=1:(2;lia;1~4;lsj;0)&51:(54:Usr00000004;1)
				 */
				SendMessageBo bo=new SendMessageBo(this.conn,this.userView);
				bo.sendMessageToOa(tabid) ;
				 
			}
	    	if(frowset!=null)
	    		frowset.close();
    	}
    	catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
    	}
    }
    
    
    
    /**
     * 报批、批准前执行编制控制校验
     * @param ins_id
     * @param task_id
     * @param isSelf 1:个人业务申请
     * @author dengcan
     * @serialData 2014-01-16
     * @return
     */
    public LazyDynaBean validateInfo(String ins_id,String task_id,String selfapply) 
    {
    	LazyDynaBean abean=new LazyDynaBean();
    	String msg="";
    	String flag="success";  //success:成功继续执行下一步   warn:警告弹出提示框msg,由用户判断是否继续    error:弹出提示框msg,结束操作
    	int tabid=this.templateTableBo.getTabid();
    	if("1".equalsIgnoreCase(selfapply))
    		this.templateTableBo.setBEmploy(true);
    	com.hjsj.hrms.module.template.utils.TemplateBo templateBo=new com.hjsj.hrms.module.template.utils.TemplateBo(conn, this.userView, tabid);
		templateBo.setModuleId("9");
		templateBo.setTaskId(task_id);
    	try
    	{
    		RowSet rowSet=null;
    		ContentDAO dao = new ContentDAO(this.conn);
    		//获取pc叶上的必填设置
    		//ArrayList fieldlist_must_pc=this.templateTableBo.getAllFieldItem("0");
    		/**
    		 * 模板手机页和pc页插入相同的变化后子集，查询指标应只查手机页签内容
    		 */
    		ArrayList fieldlist_must=this.templateTableBo.getAllFieldItem("1");
	    	
	    	//微信页和pc页有相同设置的子集，微信也会拿pc页上的，必填设置也需要按照pc页上的子集必填设置校验。这里是寻找pc页上对应子集
	    	/*for(int i=0;i<fieldlist_must.size();i++){
	    		FieldItem item=(FieldItem)fieldlist_must.get(i);
	    		if(item.getItemid().toLowerCase().substring(0,2).equalsIgnoreCase("t_")&&item.isChangeAfter()){
		    		for(int j=0;j<fieldlist_must_pc.size();j++){
		    			FieldItem itemPC=(FieldItem)fieldlist_must_pc.get(j);
		    			if(itemPC.getItemid().toLowerCase().substring(0,2).equalsIgnoreCase("t_")&&itemPC.isChangeAfter()){
		    				SubSetDomain subdomainPc=new SubSetDomain(itemPC.getFormat());
		    				SubSetDomain subdomain=new SubSetDomain(item.getFormat());
		    				if(subdomain.getSubFields().equalsIgnoreCase(subdomainPc.getSubFields())){
		    					fieldlist_must.remove(i);
		    					if(i>0){		    						
		    						fieldlist_must.set(i-1, itemPC);
		    					}else{
		    						fieldlist_must.set(i-1, itemPC);
		    					}
		    					break;
		    				}
		    			}
		    		}
	    		}
	    	}*/
	    	//自动计算 //驳回不进行自动计算、校验公式、必填校验。
			if(templateBo.getParamBo().getAutoCaculate().length()==0&&(!"3".equalsIgnoreCase(this.opt)))
			{
				if(SystemConfig.getPropertyValue("templateAutoCompute")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))&&!"1".equalsIgnoreCase(selfapply))
				{
					templateBo.batchCompute(ins_id);
				}
			}else if("1".equals(templateBo.getParamBo().getAutoCaculate())&&(!"3".equalsIgnoreCase(this.opt)))
			{
				templateBo.batchCompute(ins_id);
			}
			String srcTab="templet_"+tabid;
			if("0".equals(ins_id))
			{
				srcTab=this.userView.getUserName()+"templet_"+tabid;
				if("1".equalsIgnoreCase(selfapply))
					srcTab="g_templet_"+tabid;
			}
			
			if("0".equals(task_id)&& "0".equals(selfapply)) //非个人业务申请
			{
				rowSet=dao.search("select count(*) from  "+srcTab+" where submitflag=1");
				if(rowSet.next())
				{
					if(rowSet.getInt(1)==0)
					{	 
						msg=ResourceFactory.getProperty("general.template.workflowbo.info10");
						flag="error";;
					}
				}
			}
			
			
			if(!"error".equals(flag))
			{
				//驳回不进行自动计算、校验公式、必填校验。
				if(!"3".equalsIgnoreCase(this.opt)){
					this.templateTableBo.checkMustFillItem(srcTab,fieldlist_must,Integer.parseInt(task_id));
					ArrayList fieldlist_logic=this.templateTableBo.getAllFieldItem();
					this.templateTableBo.checkLogicExpress(srcTab, Integer.parseInt(task_id), fieldlist_logic);
				}
				//检查当前任务的活动状态是否是结束状态  
				if("0".equals(task_id))
				{ 
					rowSet=dao.search("select count(*) from t_wf_task where ( task_state='5' or task_state='4' ) and task_id="+task_id);
					if(rowSet.next())
					{
						if(rowSet.getInt(1)>0)
						{	 
							msg=ResourceFactory.getProperty("general.template.workflowbo.info9");
							flag="error";;
						}
					} 
				}
			}
            //驳回不进行自动计算、校验公式、必填校验。
			if(!"error".equals(flag)&&(this.templateTableBo.getInfor_type()==2||this.templateTableBo.getInfor_type()==3)&&(!"3".equalsIgnoreCase(this.opt)))//如果是单位部门或岗位
			{
				StringBuffer strsql=new StringBuffer(""); 
				
				if("0".equals(task_id))
				{
					strsql.append("select * from ");
					strsql.append(srcTab);
					strsql.append(" where submitflag=1");
					rowSet=dao.search(strsql.toString());
					HashMap tableColumnMap=new HashMap();
					ResultSetMetaData mt=rowSet.getMetaData();
					for(int i=1;i<=mt.getColumnCount();i++)
					{
						String columnName=mt.getColumnName(i);
						tableColumnMap.put(columnName.toLowerCase(),"1");
					}
					this.templateTableBo.validateSysItem(tableColumnMap);
					/**如果为新建组织单元业务  */
					if(this.templateTableBo.getOperationtype()==5) 
					{
						this.templateTableBo.checkNewOrgFillItem(strsql.toString(),this.templateTableBo.getOperationtype());
					}
					/**  如果为合并 和 划转业务 需判断同一组中的记录是否都被选中，同时去掉没有指定（划转|合并）目标记录的选中标记 */
					if(this.templateTableBo.getOperationtype()==8||this.templateTableBo.getOperationtype()==9)
					{
						this.templateTableBo.checkSelectedRule(strsql.toString(),srcTab,"");
					}
				}
				else
				{
					strsql.append("select * from ");
					strsql.append(srcTab);  
					strsql.append(" where  exists (select null from t_wf_task_objlink where "+srcTab+".seqnum=t_wf_task_objlink.seqnum and "+srcTab+".ins_id=t_wf_task_objlink.ins_id ");
					strsql.append("  and task_id="+task_id+"  and submitflag=1  and (state is null or  state=0 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ");	
					if(this.templateTableBo.getOperationtype()==8||this.templateTableBo.getOperationtype()==9)
					{
						this.templateTableBo.checkSelectedRule(strsql.toString(),srcTab,task_id);
					}		
				}
				
			}
			 if(!"error".equals(flag)&&this.templateTableBo.getInfor_type()==1&&this.templateTableBo.isHeadCountControl(Integer.parseInt(task_id))&&(!"3".equalsIgnoreCase(this.opt))){//报批和提交时验证超编
				 StringBuffer sbsql = new StringBuffer("");//查询的sql语句 
				 
				 if("0".equals(task_id))
				 {
					 sbsql.append("select * from "+srcTab+" where");
					 if("1".equalsIgnoreCase(selfapply))//员工通过自助平台发动申请
					    sbsql.append(" a0100='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'");
					 else
						sbsql.append(" submitflag=1");
				 }
				 else
				 {
				 
					sbsql.append("select * from "+srcTab); 
					sbsql.append(" where  exists (select null from t_wf_task_objlink where "+srcTab+".seqnum=t_wf_task_objlink.seqnum and "+srcTab+".ins_id=t_wf_task_objlink.ins_id ");
					sbsql.append("  and task_id="+task_id+"  and submitflag=1  and (state is null or  state=0 ) and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ");
				 }
				///源库与目标库
				String srcDb = "";//源库
				String destinationDb = "";//目标库
				srcDb = this.templateTableBo.getDestinationDb(sbsql.toString());
				destinationDb = this.templateTableBo.getDestBase(); //移库模板的目标库
				if("".equals(destinationDb)){//说明不是移库模板
					destinationDb = srcDb;
				}
				///对那些库进行编制控制
				PosparameXML pos = new PosparameXML(this.conn);//得到constant表中UNIT_WORKOUT对应的参数
				String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");//对哪个库进行编制控制
				dbs=dbs!=null&&dbs.trim().length()>0?dbs:"";
				if(dbs.length()>0){
					if(dbs.charAt(0)!=','){
						dbs=","+dbs;
					}
					if(dbs.charAt(dbs.length()-1)!=','){
						dbs=dbs+",";
					}
				}
				int currentOperation = this.templateTableBo.getOperationtype();//模板类型
				String addFlag = this.templateTableBo.getAddFlag(currentOperation,dbs,srcDb,destinationDb);//1:新增。0：修改 2：不控制
				
				ScanFormationBo scanFormationBo=new ScanFormationBo(this.conn,this.userView);
					
				if(("1".equals(addFlag) || "0".equals(addFlag)) && scanFormationBo.doScan()){//如果需要编制控制
					
					///得到所有变化后的指标（包含子集中的指标）
					StringBuffer allFields = new StringBuffer("");//所有的指标（如果是子集，也要把所包含的指标提取出来）
					ArrayList allFieldsList = new ArrayList();//list(0)为变化后的指标。list(1)为map.键为变化后的子集，键值为该子集中涉及到的指标
					allFieldsList = this.templateTableBo.getAllFields(String.valueOf(this.templateTableBo.getTabid()));
					if(allFieldsList.size()>0){
						ArrayList afterFields = (ArrayList)allFieldsList.get(0);//变化后的指标（仅仅是指标）
						ArrayList temp_list = new ArrayList();//所有的指标（包括子集中的）
						temp_list.addAll(afterFields);
						HashMap temp_map = (HashMap)allFieldsList.get(1);//所有子集的map(包括兼职子集)。键全为小写
						
						Set key = temp_map.keySet();
						for (Iterator it = key.iterator(); it.hasNext();) {
							String s = (String) it.next();
							ArrayList innerlist = (ArrayList)temp_map.get(s);
							temp_list.addAll(innerlist);
						}
						
						//将所有变化后的指标（包括子集中的指标）变为字符串的形式
						for(int m=0;m<temp_list.size();m++){
							String str = (String)temp_list.get(m);
							allFields.append(str+",");
						}
						if(allFields.length()>0 && allFields.charAt(allFields.length()-1)==','){
							allFields.setLength(allFields.length()-1);
						}
						String itemids = "all";
						if(currentOperation!=1 && currentOperation!=2){//不是移库型
							itemids = allFields.toString();
						}
						if(scanFormationBo.needDoScan(destinationDb,itemids)){//当前模板的指标是否能引起编制检查
							ArrayList beanlist = new ArrayList();
							//开始传list。list中存放着bean.
							String partType = "0";//兼职指标以何种方式维护。=0：以罗列指标的形式   =1：在兼职子集中
							partType = this.templateTableBo.getPartType(temp_map);
							if("0".equals(partType)){
								beanlist = this.templateTableBo.getBeanList_1(currentOperation,addFlag,sbsql.toString(),destinationDb,srcDb,afterFields,temp_map,String.valueOf(this.templateTableBo.getTabid()));
							}else{
								beanlist = this.templateTableBo.getBeanList_2(currentOperation,addFlag,sbsql.toString(),destinationDb,srcDb,afterFields,temp_map);
							}
							scanFormationBo.execDate2TmpTable(beanlist);
							String mess=scanFormationBo.isOverstaffs();
							if(!"ok".equals(mess)){
								if("warn".equals(scanFormationBo.getMode())){//提示，继续。
									msg += mess;
									flag="warn";
								}else{
									msg += mess;
									flag="error";;
								}
							}
						} //当前模板的指标能引起编制检查 结束
					} //allFieldsList.size()>0 结束
				} //需要编制控制 结束 
			}
			if(!"error".equals(flag)&&this.templateTableBo.getInfor_type()==1&& "0".equals(task_id)&&this.templateTableBo.getOperationtype()==0&&!isWeiX){
				//如果是人员调入模版，通过二维码扫描填写报批，判断是否有身份证号指标，如果有，查看身份证号在库中是否已经存在，不存在才可以提交。
				Boolean isHaveChk=false;
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
				String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name"); //身份证指标
				chk=chk!=null?chk:"";
				String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//唯一性验证是否启用
	    		uniquenessvalid=uniquenessvalid!=null?uniquenessvalid:"";
				String dbonly = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","db");//验证唯一性适用的人员库
	    		dbonly=dbonly!=null?dbonly:"";
	    		if("1".equals(uniquenessvalid)&&chk.length()>0){//勾选了检验唯一指标，且选择了唯一指标
	    			if (templateTableBo.getDestBase()!=null &&templateTableBo.getDestBase().length()>0 && 
							dbonly.toUpperCase().indexOf(templateTableBo.getDestBase().toUpperCase())!=-1){
						StringBuffer sbsql=new StringBuffer();
						sbsql.append("select * from "+srcTab+" where");
						if("1".equalsIgnoreCase(selfapply))//员工通过自助平台发动申请
							sbsql.append(" a0100='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'");
						rowSet=dao.search(sbsql.toString());
						ResultSetMetaData resultSetData=rowSet.getMetaData();
						if(StringUtils.isNotBlank(chk)){
							for(int i=1;i<=resultSetData.getColumnCount();i++){
								String columnName = resultSetData.getColumnName(i);
								if(columnName.equalsIgnoreCase(chk+"_2")){
									isHaveChk=true;
								}
							}
							String chk_value="";
							String id_typeValue="";
							if(isHaveChk){
								String errerMsg="";
								if(rowSet.next()){
									String a0101=rowSet.getString("a0101_2");
									chk_value=rowSet.getString(chk+"_2");
									String destDb=templateTableBo.getDestBase();
									if(StringUtils.isNotBlank(chk_value)&&StringUtils.isNotBlank(destDb)){
							            String searchSql="select a0101_1 from templet_"+tabid+" where "+chk+"_2='"+chk_value+"'";
							            	searchSql+=" UNION  select a0101 from "+destDb+"A01  where "+chk+"='"+chk_value+"'";
							            RowSet search = dao.search(searchSql);
		 
							            if(search.next()){
							            	errerMsg = "您的证件号码已存在，不允许提交";
							            }
									}
								}
								if(StringUtils.isNotBlank(errerMsg)){
									msg+=errerMsg; 
									flag="error";
								}
							}
						}
	    			}
	    		}
			}
			
			if(rowSet!=null)
				rowSet.close();
    	}
    	catch (Exception ex) {
    		flag="error";
    		String errorMsg=ex.toString();
    		int index_i=errorMsg.indexOf("description:");
    		msg=errorMsg.substring(index_i+12);
    		ex.printStackTrace();
         // throw GeneralExceptionHandler.Handle(ex);
        }
    	
    	abean.set("msg", msg);
    	abean.set("flag", flag);
    	return abean;
    }
    
    
    /**
     * 
     * @Title: searchInWaitingTask
     * @Description: 查询待办业务的数据
     * @param query_type
     *            查询的类型 1：按照天数查询 2：范围查询
     * @param days
     *            天数 页面上填写的数据
     * @param startDate
     *            开始时间
     * @param endDate
     *            结束时间
     * @param bsFlag
     * @return ArrayList 返回List存放查询出来的数据
     * @throws GeneralException
     */

    public ArrayList searchInWaitingTask(String query_type, String days, String startDate, String endDate, String bs_flag, String taskFormName) throws GeneralException {
        StringBuffer strsql = new StringBuffer();// 这个sql语句并没有考虑接收范围等因素。接收范围在getTaskList（）方法中处理。
        StringBuffer strsql2 = new StringBuffer(); // 模板查询sql
        ArrayList taskList = new ArrayList();
        try {
            /** 查询任务 */

            String format_str = "yyyy-MM-dd HH:mm";
            if (Sql_switcher.searchDbServer() == Constant.ORACEL)
                format_str = "yyyy-MM-dd hh24:mi";
            strsql.append("select U.tabid,a0101_1,tt.name,state states," + Sql_switcher.dateToChar("T.start_date", format_str) + " start_date,task_pri,bread,bfile,task_id ,T.ins_id,U.template_type,T.actor_type,T.actorid,T.node_id from t_wf_task T,t_wf_instance U");
            strsql.append(",template_table tt ");
            strsql.append(" where T.ins_id=U.ins_id and ( task_topic not like '%,共0人)' and task_topic not like '%,共0条记录)' )  ");

            strsql2.append("select distinct U.tabid,tt.name  from t_wf_task T,t_wf_instance U ,template_table tt ");
            strsql2.append("  where T.ins_id=U.ins_id  and tt.tabid=U.tabid  and  task_topic not like '%共0人%' and  task_topic not like '%共0条%' ");
            strsql.append(" and U.tabid=tt.tabid   ");
            if (taskFormName != null && !"".equals(taskFormName) && taskFormName.length() > 0) {
                strsql.append(" and tt.Name like '%" + taskFormName + "%'");

            }
            strsql.append("and " + Sql_switcher.isnull("T.bs_flag", "'1'") + "='" + bs_flag + "'");
            strsql2.append("and " + Sql_switcher.isnull("T.bs_flag", "'1'") + "='" + bs_flag + "'");
            /**
             * 待批任务 任务类型为人工，状态为等待
             * */
            strsql.append(" and task_type='2' and task_state='3'");
            strsql2.append(" and  task_type='2' and task_state='3'");
            if(!"2".equals(bs_flag)&&!"3".equals(bs_flag))//审批任务或空任务 与bs同步 wangrd 20160708
            {
                strsql.append(" and U.finished='2' ");// =2:运行中
                strsql2.append(" and U.finished='2' ");
            }
            strsql.append(" and (");
            strsql.append(getTaskFilterWhere());
            strsql.append(")");
            strsql2.append(" and (");
            strsql2.append(getTaskFilterWhere());
            strsql2.append(")");
            if ("1".equals(query_type))// 最近多少天
            {
                if (validateNum(days)) {
                    String strexpr = Sql_switcher.addDays(Sql_switcher.sqlNow(), "-" + days);// "GetDate()"
                    strsql.append(" and T.start_date>=");
                    strsql.append(strexpr);

                }

            } else {
                strsql.append(" and ( 1=1 ");
                if (validateDate(startDate))
                    strsql.append(PubFunc.getDateSql(">=", "T.start_date", startDate));
                if (validateDate(endDate))
                    strsql.append(PubFunc.getDateSql("<=", "T.start_date", endDate));
                strsql.append(" )");

            }

            boolean isSource = false;
            if (this.userView.isHavetemplateid(IResourceConstant.RSBD) || this.userView.isHavetemplateid(IResourceConstant.ORG_BD) || this.userView.isHavetemplateid(IResourceConstant.POS_BD) || this.userView.isHavetemplateid(IResourceConstant.GZBD) || this.userView.isHavetemplateid(IResourceConstant.INS_BD) || this.userView.isHavetemplateid(IResourceConstant.PSORGANS)
                    || this.userView.isHavetemplateid(IResourceConstant.PSORGANS_FG) || this.userView.isHavetemplateid(IResourceConstant.PSORGANS_GX) || this.userView.isHavetemplateid(IResourceConstant.PSORGANS_JCG))
                isSource = true;
            if (this.userView.isSuper_admin())
                isSource = true;
            if (!isSource) {
                strsql.append(" and 1=2 ");
                strsql2.append(" and 1=2 ");
            }
            ArrayList columnList = getColumnList();
            if (!isSource) {// 如果没有相应的模版资源权限
                return new ArrayList();
            } else {
                ArrayList tmessageList = new ArrayList();
                tmessageList = getTmessageList(columnList, taskFormName);
                taskList = getTaskList(strsql.toString() + " order by T.start_date desc,task_pri asc", tmessageList, bs_flag);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }

        return taskList;

    }

    /**
     * @Title: getColumnList
     * @Description: TODO
     * @return ArrayList
     * @throws
     */
    private ArrayList getColumnList() {

        ArrayList columnList = new ArrayList();
        columnList.add(getBean("task_pri", "任务优先级"));
        columnList.add(getBean("senduser", "发送人"));
        columnList.add(getBean("task_topic", "主题"));
        columnList.add(getBean("receive_time", "接收时间"));
        columnList.add(getBean("state","状态"));

        return columnList;
    }

    public LazyDynaBean getBean(String itemid, String itemdesc) {
        LazyDynaBean abean = new LazyDynaBean();
        abean.set("itemid", itemid);
        abean.set("itemdesc", itemdesc);
        return abean;
    }

    /**
     * @Title: getReturnList
     * @Description: 截取所需要的数据
     * @param taskList 查询出来的总数据
     * @param index    要查询的页数
     * @param size     每页的数据个数
     * @return ArrayList 截取后存放数据的list
     * @throws
     */
    public ArrayList getReturnList(ArrayList taskList, String index, String size) {
        ArrayList returnList = null;
        int tempindex = Integer.valueOf(index).intValue();
        int tempsize = Integer.valueOf(size).intValue();
        int begindex = 0;
        int endindex = 0;

        begindex = (tempindex - 1) * tempsize;
        endindex = tempindex * tempsize;
        if (begindex > taskList.size()) {
            returnList = new ArrayList();
            return returnList;
        }
        if (endindex > taskList.size()) {
            endindex = taskList.size();
        }
        List templist = taskList.subList(begindex, endindex);
        returnList = new ArrayList(templist);
        return returnList;
    }

    /**
     * @Title: getTaskList
     * @Description: 获取待办业务的数据
     * @param sql
     *            查询数据的语句
     * @param tmessageList
     *            存放通知消息数据的List
     * @param bsFlag
     *            暂时没有用到 可能为以后考虑
     * @return ArrayList 返回所有的查询的数据 用LIST 存放
     * @throws
     */
    private ArrayList getTaskList(String sql, ArrayList tmessageList, String bsFlag) {

        ArrayList list = new ArrayList();
        ArrayList returnList = new ArrayList();
        try {
            WorkflowBo wb = new WorkflowBo(this.conn, this.userView);

            returnList.addAll(tmessageList);
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rowSet = null;
            rowSet = dao.search(sql);
            HashMap map = null;

            LazyDynaBean paramBean = new LazyDynaBean();
            Set tabidSet=new HashSet();
            while (rowSet.next()) {
                map = new HashMap();
                String tabid = ""+rowSet.getInt("tabid");
               
                String task_id = rowSet.getString("task_id");
                String ins_id = rowSet.getString("ins_id");
                String actor_type = rowSet.getString("actor_type");
                String node_id = rowSet.getString("node_id");
                String state=rowSet.getString("states");
                if ("5".equals(rowSet.getString("actor_type")))// 本人
                {
                    paramBean = new LazyDynaBean();
                    paramBean.set("tabid", tabid);
                    paramBean.set("task_id", task_id);
                    paramBean.set("ins_id", ins_id);
                    paramBean.set("actor_type", actor_type);
                    paramBean.set("node_id", node_id);
                    ArrayList listrecord = wb.getRecordList(paramBean, this.userView);
                    if (listrecord.size() == 0)
                        continue;
                }

                if ("2".equals(rowSet.getString("actor_type")))// 角色
                {
                    paramBean = new LazyDynaBean();
                    paramBean.set("tabid", tabid);
                    paramBean.set("task_id", task_id);
                    paramBean.set("ins_id", ins_id);
                    paramBean.set("actor_type", actor_type);
                    paramBean.set("node_id", node_id);
                    paramBean.set("actorid", rowSet.getString("actorid"));
                    ArrayList listrecord = wb.getRecordList(paramBean, this.userView);
                    if (listrecord.size() == 0)
                        continue;
                }
              
                map.put("state", AdminCode.getCodeName("23",state));
                map.put("task_topic", rowSet.getString("name"));
                map.put("task_pri", rowSet.getString("task_pri"));
                map.put("senduser", rowSet.getString("a0101_1"));
                map.put("receive_time", rowSet.getString("start_date"));
                map.put("tabid", ""+rowSet.getInt("tabid"));
                map.put("task_id", ""+rowSet.getInt("task_id"));
                map.put("ins_id", ""+rowSet.getInt("ins_id"));
                map.put("node_id", node_id);
                tabidSet.add(""+rowSet.getInt("tabid"));
                list.add(map);
            }
            
            HashMap tabMap=new HashMap();
            for(Iterator t=tabidSet.iterator();t.hasNext();)
            {
                String tabid=(String)t.next();
                HashMap tempmap =getInfortype(tabid);
                tabMap.put(tabid,tempmap);
            } 
            for(int i=0;i<list.size();i++)
            {
                HashMap linshimap=(HashMap)list.get(i);
                String tabid=(String)linshimap.get("tabid");
                HashMap tempmap=(HashMap)tabMap.get(tabid);
                map.put("infor_type",(String)tempmap.get("infor_type"));
                
                map.put("factorFlag",(String)tempmap.get("factorFlag"));
                map.put("operationtype",(String)tempmap.get("operationtype"));
            } 
            returnList.addAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnList;

    }

    /**
     * @param taskFormName
     * @param columnList
     * @Title: getTmessageList
     * @Description: 获得通知消息的数据
     * @return ArrayList 返回通知消息的数据用List存放
     * @throws
     */
    private ArrayList getTmessageList(ArrayList columnList, String taskFormName) {

        ArrayList listAll = new ArrayList();
        try {
        	String static_="static";
        	if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
        		static_="static_o";
        	}
            StringBuffer sql = new StringBuffer();
            sql.append("select DISTINCT Noticetempid,Template_table.name as name, Template_table."+static_+" as type ");
            sql.append(" from tmessage left join Template_table on tmessage.Noticetempid=Template_table.tabid ");
            sql.append(" where (State='0' or State='1')");
            if (taskFormName != null && !"".equals(taskFormName) && taskFormName.length() > 0) {
                sql.append(" and Template_table.name like '%" + taskFormName + "%'");
            }
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rs;
            CommonData cData = null;
            ArrayList list = new ArrayList();
            ArrayList list2 = new ArrayList();
            rs = dao.search(sql.toString());
            String tabid = "";
            String type = null;
            boolean isCorrect = false;
            HashMap map = new HashMap();
            HashMap map2 = new HashMap();
            while (rs.next()) {
                tabid = rs.getString("Noticetempid");
                type = rs.getInt("type") + " ";
                if (tabid == null || tabid.length() <= 0)
                    continue;
                isCorrect = false;
                if (this.userView.isHaveResource(IResourceConstant.RSBD, tabid))// 人事移动
                    isCorrect = true;
                if (!isCorrect)
                    if (this.userView.isHaveResource(IResourceConstant.ORG_BD, tabid))// 组织变动
                        isCorrect = true;
                if (!isCorrect)
                    if (this.userView.isHaveResource(IResourceConstant.POS_BD, tabid))// 岗位变动
                        isCorrect = true;
                if (!isCorrect)
                    if (this.userView.isHaveResource(IResourceConstant.GZBD, tabid))// 工资变动
                        isCorrect = true;
                if (!isCorrect)
                    if (this.userView.isHaveResource(IResourceConstant.INS_BD, tabid))// 保险变动
                        isCorrect = true;
                if (!isCorrect)
                    if (this.userView.isHaveResource(IResourceConstant.PSORGANS, tabid))
                        isCorrect = true;
                if (!isCorrect)
                    if (this.userView.isHaveResource(IResourceConstant.PSORGANS_FG, tabid))
                        isCorrect = true;
                if (!isCorrect)
                    if (this.userView.isHaveResource(IResourceConstant.PSORGANS_GX, tabid))
                        isCorrect = true;
                if (!isCorrect)
                    if (this.userView.isHaveResource(IResourceConstant.PSORGANS_JCG, tabid))
                        isCorrect = true;
                if (isCorrect && map.get(tabid) == null) {
                    cData = new CommonData();
                    int str = getRecordBusiTopic(rs.getString("Noticetempid"), type, map2);
                    if (str != 0) {
                        cData.setDataName(rs.getString("name")+"_通知");
                        cData.setDataValue(tabid);
                        list.add(cData);
                        list2.add(rs.getString("Noticetempid"));
                        map.put(tabid, "1");
                    }
                }
            }
            CommonData dt = new CommonData();
            LazyDynaBean _bean = null;
            HashMap datamap = null;
            Set tabidset = new HashSet();
            for (int i = 0; i < list.size(); i++) {
                datamap = new HashMap();
                dt = (CommonData) list.get(i);
                String Noticetempid = (String) list2.get(i);
                for (int j = 0; j < columnList.size(); j++) {
                    _bean = (LazyDynaBean) columnList.get(j);
                    String itemid = (String) _bean.get("itemid");
                    if ("senduser".equals(itemid) || "receive_time".equals(itemid)|| "state".equals(itemid)) {
                        if (map2 != null && map2.get(Noticetempid) != null) {
                            HashMap map3 = (HashMap) map2.get(Noticetempid);
                            if (map3 != null) {
                                if (map3.get(itemid) != null) {
                                    datamap.put(itemid, map3.get(itemid));
                                } else {
                                    datamap.put(itemid, "");
                                }
                            } else {
                                datamap.put(itemid, "");
                            }
                        }
                    } else {
                        continue;
                    }
                }
                datamap.put("task_topic", dt.getDataName());
                datamap.put("tabid", dt.getDataValue());
                tabidset.add(dt.getDataValue());
                datamap.put("task_id", "0");
                datamap.put("ins_id", "0");
                datamap.put("task_pri", "0");
                datamap.put("node_id", "0");
                datamap.put("state", AdminCode.getCodeName("23","01"));//消息的将所有的状态置为起草
                listAll.add(datamap);
            }
            HashMap tabMap=new HashMap();
            for(Iterator t=tabidset.iterator();t.hasNext();)
            {
                String temptabid=(String)t.next();
                HashMap tempmap =getInfortype(temptabid);
                tabMap.put(temptabid,tempmap);
            } 
            for(int i=0;i<listAll.size();i++)
            {
                HashMap linshimap=(HashMap)listAll.get(i);
                String temptabid=(String)linshimap.get("tabid");
                HashMap tempmap=(HashMap)tabMap.get(temptabid);
                linshimap.put("infor_type",(String)tempmap.get("infor_type"));
                
                linshimap.put("factorFlag",(String)tempmap.get("factorFlag"));
                linshimap.put("operationtype",(String)tempmap.get("operationtype"));
            } 
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return listAll;
    }

    /**
     * 
     * @Title: getRecordBusiTopic
     * @Description: 具体去获得通知消息的数据（属于某个模版的消息list，有可能多个用户发通知通知到该模版）
     * @param Noticetempid
     *            通知的模版ID 就是具体要展现出来的模版id
     * @param type
     *            查询数据的类型 =1,日常管理（人事异动） =2,工资管理 =3,警衔管理 =8,保险管理 =4,关衔管理 =9,档案转递
     *            =10单位模版 =11岗位模版
     * @param name
     *            模版的名字
     * @return ArrayList 返回属于某个模版的数据List
     * @throws
     */
    public int getRecordBusiTopic(String Noticetempid, String type, HashMap map2) {

        int nmax = 0;
        RowSet rs = null;
        try {

            ContentDAO dao = new ContentDAO(this.conn);
            DbWizard dbw = new DbWizard(this.conn);
            String sql = "select distinct a0101 from tmessage   where  Noticetempid=" + Noticetempid + " and state='0' ";
            if (type != null) {// 对单位或者岗位的操作
                type=type.trim();
                if(("10".equals(type) || "11".equals(type))){
                    sql = "select distinct organization.codeitemdesc from tmessage,organization   where  tmessage.b0110=organization.codeitemid  and Noticetempid=" + Noticetempid + " and tmessage.state='0' ";
                }
            }
            String filter_by_manage_priv = "0"; // 接收通知单数据方式：0接收全部数据，1接收管理范围内数据
            String include_suborg = "1"; // 0不包括下属单位, 1包括(默认值)

            try {
                rs = dao.search("select ctrl_para from template_table where tabid=" + Noticetempid);
                if (rs.next()) {
                    String sxml = Sql_switcher.readMemo(rs, "ctrl_para"); // vo.getString("ctrl_para");
                    Document doc = null;
                    Element element = null;
                    if (sxml != null && sxml.trim().length() > 0) {
                        doc = PubFunc.generateDom(sxml);
                        String xpath = "/params/receive_notice";
                        XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                        List childlist = findPath.selectNodes(doc);
                        if (childlist != null && childlist.size() > 0) {
                            element = (Element) childlist.get(0);
                            filter_by_manage_priv = (String) element.getAttributeValue("filter_by_manage_priv");
                            if (element.getAttributeValue("include_suborg") != null)
                                include_suborg = (String) element.getAttributeValue("include_suborg");
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String sqlstr = "";// 查找消息库中的发送人、接收日期、审阅状态
            String sqlstr2 = "";// 查找临时表关联消息库中的发送人、接收日期、审阅状态
            String receive_time = "receive_time";
            if (Sql_switcher.searchDbServer() != Constant.ORACEL) {
            } else {
                receive_time = "receive_time";
            }
            sql = "select count(*) a from ( ";
            if (type != null && ("10".equals(type) || "11".equals(type))) {
                sql += "select distinct b0110 from tmessage   where  Noticetempid=" + Noticetempid + " and state='0'  ";
                sqlstr += "select  b0110,bread,send_user," + Sql_switcher.dateToChar(receive_time, "yyyy-MM-dd HH:mm") + " receive_time,state from tmessage   where Noticetempid=" + Noticetempid + "   ";
            } else {
                sql += "select distinct a0100,lower(db_type) db_type  from tmessage   where  Noticetempid=" + Noticetempid + " and state='0'  ";
                sqlstr += "select  a0100,bread,send_user," + Sql_switcher.dateToChar(receive_time, "yyyy-MM-dd HH:mm") + " receive_time,state from tmessage   where  Noticetempid=" + Noticetempid + "  ";
            }
            if (!this.userView.isSuper_admin() && "1".equals(filter_by_manage_priv)) {
                String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理
                // 6：培训管理 7：招聘管理
                // 8:业务模板
                if (operOrg == null || !"UN`".equalsIgnoreCase(operOrg)) {
                    sql += " and ( ";
                    sqlstr += " and ( ";

                    if (operOrg != null && operOrg.length() > 3) {
                        StringBuffer tempSql = new StringBuffer("");
                        String[] temp = operOrg.split("`");
                        for (int j = 0; j < temp.length; j++) {
                            if (temp[j] != null && temp[j].length() > 0) {
                                if ("0".equalsIgnoreCase(include_suborg))// 不包含下属单位
                                {
                                    if ("UN".equalsIgnoreCase(temp[j].substring(0, 2))) {
                                        tempSql.append(" or  tmessage.b0110_self ='" + temp[j].substring(2) + "%'");
                                    } else
                                        tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2) + "%'");
                                } else
                                    tempSql.append(" or  tmessage.b0110 like '" + temp[j].substring(2) + "%'");
                            }
                        }

                        if (tempSql.length() > 0) {
                            sql += tempSql.substring(3);
                            sqlstr += tempSql.substring(3);
                        } else {
                            sql += " tmessage.b0110='##'";
                            sqlstr += " tmessage.b0110='##'";
                        }
                    } else {
                        sql += " tmessage.b0110='##'";
                        sqlstr += " tmessage.b0110='##'";
                    }

                    sql += " or nullif(tmessage.b0110,'') is null )";
                    sqlstr += " or nullif(tmessage.b0110,'') is null )";
                }

            }
            sql += " and (username is null or username='' or lower(username)='" + this.userView.getUserName().toLowerCase() + "')";
            sqlstr += " and (username is null or username='' or lower(username)='" + this.userView.getUserName().toLowerCase() + "')";
            if (type != null && "10".equals(type)) {
                sql += " and object_type=2 ";
                sqlstr += " and object_type=2 ";
                sqlstr += "order by receive_time";
            } else if (type != null && "11".equals(type)) {
                sql += " and object_type=3 ";
                sqlstr += " and object_type=3 ";
                sqlstr += "order by receive_time";
            } else {
                sql += " and ( object_type is null or object_type=1 ) ";
                sqlstr += " and ( object_type is null or object_type=1 ) ";
                sqlstr += "order by receive_time";
            }
            if (dbw.isExistTable(this.userView.getUserName() + "templet_" + Noticetempid, false)) {
                if (type != null && ("10".equals(type) || "11".equals(type))) {
                    sql += " union select  distinct b0110  from " + this.userView.getUserName() + "templet_" + Noticetempid + "  where state=1  ";

                    sqlstr2 = "  select b0110 from " + this.userView.getUserName() + "templet_" + Noticetempid + "  where state=1  ";
                } else {
                    sql += " union select distinct a0100,lower(basepre) db_type from " + this.userView.getUserName() + "templet_" + Noticetempid + "  where state=1  ";

                    sqlstr2 = "  select a0100 from " + this.userView.getUserName() + "templet_" + Noticetempid + "  where state=1  ";
                }
            }
            sql += " ) aa";
            rs = dao.search(sql);
            while (rs.next())
                nmax += rs.getInt(1);
            if (nmax == 0) {
                return 0;
            } else {
                String str = "";
                if (sqlstr2.length() > 0) {
                    rs = dao.search(sqlstr2);
                    while (rs.next()) {
                        str += rs.getString(1) + ",";
                    }
                }
                rs = dao.search(sqlstr);
                HashMap map = new HashMap();
                String send_users = ",";
                String receive_times = ",";
                HashMap map4 = new HashMap();// 一个发送人对应一个接收时间
                while (rs.next()) {
                    String a0100 = rs.getString(1);
                    String state = rs.getString("state");
                    if ("0".equals(state)) {
                        map.put("bread", "0");
                        if (rs.getString("send_user") != null && send_users.indexOf(rs.getString("send_user")) == -1) {
                            send_users += rs.getString("send_user") + ",";

                        }
                        if (rs.getString("receive_time") != null) {
                            String time = rs.getString("receive_time");
                            receive_times += time + ",";
                            if (rs.getString("send_user") != null)
                                map4.put(rs.getString("send_user"), time);
                        }
                    } else {
                        if (str.length() > 0 && a0100 != null && a0100.length() > 0 && str.indexOf(a0100 + ",") != -1) {
                            if (rs.getString("send_user") != null && send_users.indexOf(rs.getString("send_user")) == -1) {
                                send_users += rs.getString("send_user") + ",";
                            }
                            if (rs.getString("receive_time") != null) {
                                String time = rs.getString("receive_time");
                                receive_times += time + ",";
                                if (rs.getString("send_user") != null)
                                    map4.put(rs.getString("send_user"), time);
                            }

                        }
                    }
                }
                send_users = send_users.replace(",,", ",");
                receive_times = receive_times.replace(",,", ",");
                while (send_users.startsWith(","))
                    send_users = send_users.substring(1, send_users.length());
                while (send_users.endsWith(","))
                    send_users = send_users.substring(0, send_users.length() - 1);

                String temp[] = send_users.split(",");
                receive_times = "";
                for (int d = 0; d < temp.length; d++) {
                    if (map4 != null && map4.get(temp[d]) != null)
                        receive_times += map4.get(temp[d]) + ",";
                }
                while (receive_times.endsWith(","))
                    receive_times = receive_times.substring(0, receive_times.length() - 1);
                map.put("receive_time", receive_times);
                map.put("senduser", send_users);
                map2.put(Noticetempid, map);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return nmax;
        }
        return nmax;
    }

    /**
     * @Title: validateDate
     * @Description: 验证是不是日期类型
     * @param startDate
     *            传过来的时间，以字符串形式传递
     * @return boolean 返回值true或者false
     * @throws
     */
    private boolean validateDate(String datestr) {
        boolean bflag = true;
        if (datestr == null || "".equals(datestr))
            return false;
        try {
            Date date = DateStyle.parseDate(datestr);
            if (date == null)
                bflag = false;
        } catch (Exception ex) {
            bflag = false;
        }
        return bflag;
    }

    /**
     * @Title: validateNum
     * @Description: 验证是不是数字
     * @param days
     *            天数
     * @return boolean 返回值true或者false
     * @throws
     */
    private boolean validateNum(String days) {
        boolean bflag = true;
        if (days == null || "".equals(days))
            return false;
        try {
            String valide = "0123456789.";
            if (days.startsWith("."))
                bflag = false;
            for (int i = 0; i < days.length(); i++) {
                if (valide.indexOf(days.charAt(i)) == -1) {
                    bflag = false;
                }
            }
        } catch (Exception ex) {
            bflag = false;
        }
        return bflag;
    }

    /**
     * @Title: getTaskFilterWhere
     * @Description: 获得查询sql数据的过滤条件
     * @return String 返回最终的查询sql
     * @throws
     */
    private String getTaskFilterWhere() {

        StringBuffer strwhere = new StringBuffer();
        /** 用户号 */
        String dbpre = this.userView.getDbname(); // 库前缀
        String userid = dbpre + this.userView.getA0100();// 人员编号
        String orgid = "UN" + this.userView.getUserOrgId();// 单位编码
        String deptid = "UM" + this.userView.getUserDeptId();// 部门编码
        String posid = "@K" + this.userView.getUserPosId();// getUserOrgId();//职位编码
        /** 组织元 */
        strwhere.append("(T.actor_type='3' and T.actorid in ('");// =3:组织单元
        strwhere.append(orgid.toUpperCase());
        strwhere.append("','");
        strwhere.append(deptid.toUpperCase());
        strwhere.append("','");
        strwhere.append(posid.toUpperCase());
        strwhere.append("'))");

        strwhere.append(" or ( T.actor_type='5'  )");

        /** 人员列表 */
        strwhere.append(" or ((T.actor_type='1' or T.actor_type='4') and lower(T.actorid) in ('");// =1:人员
        // =4:业务用户
        strwhere.append(userid.toLowerCase());
        strwhere.append("','");
        strwhere.append(this.userView.getUserName().toLowerCase());
        strwhere.append("'))");

        /** 角色ID列表 */
        ArrayList rolelist = this.userView.getRolelist();// 角色列表
        StringBuffer strrole = new StringBuffer();
        for (int i = 0; i < rolelist.size(); i++) {

            strrole.append("'");
            strrole.append((String) rolelist.get(i));
            strrole.append("'");
            strrole.append(",");
        }
        if (rolelist.size() > 0) {
            strrole.setLength(strrole.length() - 1);
            strwhere.append(" or (T.actor_type='2' and T.actorid in (");
            strwhere.append(strrole.toString());
            strwhere.append("))");
        }
        return strwhere.toString();
    }

    /**
     * 
     * @Title: searchTaskPageList
     * @Description: TODO
     * @param taskId
     * @param isInitData
     * @param tabid
     * @param insid
     * @param pageNum
     * @param a0100
     * @return
     * @throws GeneralException
     *             ArrayList
     * @throws
     */
    public ArrayList searchTaskPageList(String taskId, String isInitData, String tabid, String ins_id,String selfapply) throws GeneralException {
        ArrayList outlist = new ArrayList();
        try {
            PendingTask imip = new PendingTask();
            String pendingType = "业务模板";
            // 将旧的代办信息置为已阅状态
            imip.updatePending("T", "HRMS-" +PubFunc.encrypt(String.valueOf(taskId)), 2, pendingType, this.userView);//bug 39604 taskid没有加密
            if ("1".equals(isInitData)) {
                /** 创建或修临时表 */
                /** 发起流程时才需要创建临时表,审批环节不用创建临时表 */
                if ("0".equalsIgnoreCase(ins_id)) {
                    if (!this.templateTableBo.isCorrect(tabid))// 是否拥有该模版的权限
                        throw new GeneralException(ResourceFactory.getProperty("template.operation.noResource"));
                   
                    if("1".equals(selfapply))
                    {
                        this.templateTableBo.createTempTemplateTable();
                        ArrayList a0100list=new ArrayList();
                        a0100list.add(this.userView.getA0100());
                        this.templateTableBo.impDataFromArchive(a0100list,this.userView.getDbname());
                    }
                    else
                    {   
                        this.templateTableBo.createTempTemplateTable(this.userView.getUserName());
                        String tablename = this.userView.getUserName() + "templet_" + tabid;
                        ContentDAO dao = new ContentDAO(this.conn);
                        RowSet rowSet = dao.search("select count(*) from " + tablename);
                        int n = 0;
                        if (rowSet.next())
                            n = rowSet.getInt(1);
                        if (n == 0)// 如果新增人员和新增组织单元机构中没有人员或者机构，那么就要默认的创建一个
                                   // --（人员姓名|组织单元名称）
                        {
                            autoAddRecord(this.templateTableBo, tablename);
                        }
                    }
                    /** 档案中与模板中的数据进行数据同步 */
                    this.templateTableBo.syncDataFromArchive();
                    // 解决封板包的问题，后期因注释掉
                    if (this.templateTableBo.getOperationtype() == 0 || this.templateTableBo.getOperationtype() == 5)
                        updateSeqNum(this.templateTableBo.getInfor_type(), this.templateTableBo.getTabid());
                } else {
                    this.templateTableBo.changeSpTableStrut();
                    if (this.templateTableBo.getOperationtype() != 0 && this.templateTableBo.getOperationtype() != 1 && this.templateTableBo.getOperationtype() != 2)// 不是调入、调出、也不是离退
                    {
                        // tablebo.setImpDataFromArchive_sub(false);

                        /** 新增任务列表,20080418 */
                        String task_id = taskId;
                        ArrayList inslist = new ArrayList();
                        inslist.add(ins_id);
                        this.templateTableBo.setInslist(inslist);
                        this.templateTableBo.setIns_id(Integer.parseInt(ins_id));
                        this.templateTableBo.getTasklist().add(task_id);
                       //this.templateTableBo.syncDataFromArchive(Integer.parseInt(ins_id), "templet_" + tabid);//bug38397 移动app审批人审批时会同步档案库数据，现在pc端不同步，导致显示数据可能会有差异。
                    }
                }
            }
            // 得到模板所有页签
            ArrayList list = this.templateTableBo.getAllTemplatePage();
            // 获取适合移动app显示的页签
            Map mapPage = this.getAppTemplatePage(tabid);
            String pageId;
            for (int i = 0; i < list.size(); i++) {
                TemplatePageBo pagebo = (TemplatePageBo) list.get(i);
                HashMap map = new HashMap();
                // #模板是否显示不打印页
                if (!pagebo.isShow())
                    continue;
				pageId = String.valueOf(pagebo.getPageid());
				// 取交集，当遇到业务模板中的页签适合移动app时，只显示适合移动app的页签。否则全部显示
				if (mapPage.size() == 0 || mapPage.get(pageId) != null) {
					map.put("pagenum", pageId);
					map.put("title", pagebo.getTitle());
					outlist.add(map);
				} 
            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }
        return outlist;
    }
    
    /**
     * 
     * @Title: getAppTemplatePage   
     * @Description: 获得移动app要显示的模板页签   
     * @param tabid  模板id
     * @throws GeneralException 
     * @return Map
     */
    private Map getAppTemplatePage(String tabid) throws GeneralException {
    	Map map = new HashMap();
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			sql.append("select * from Template_Page ");
			sql.append("where tabid = '" + tabid + "'");
			rs = dao.search(sql.toString());
			int isMobile;
			String pageID;
			while (rs.next()) {
				isMobile = rs.getInt("isMobile");
				// Template_Page isMobile字段(0,1)=(否，是)，默认空或0非移动服务页签
				if (isMobile == 1) {
					pageID = String.valueOf(rs.getInt("PageID"));
					map.put(pageID, pageID);
				}
			}
		} catch (Exception ex) {
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			PubFunc.closeResource(rs);
		}
    	return map;
    }
    

    /**
     * 
     * @Title: updateSeqNum
     * @Description: 
     * @param infor_type
     * @param tabid
     * @throws GeneralException
     */
    private void updateSeqNum(int infor_type, int tabid) throws GeneralException {

        String strDesT = null;

        ContentDAO dao = new ContentDAO(this.conn);
        try {

            strDesT = this.userView.getUserName() + "templet_" + tabid;
            String sql = "select * from " + strDesT;
            RowSet rowSet = dao.search(sql);
            while (rowSet.next()) {

                String seqnum = rowSet.getString("seqnum");
                if (seqnum == null || seqnum.trim().length() == 0) {
                    seqnum = CreateSequence.getUUID();
                    if (infor_type == 1) {
                        dao.update("update " + strDesT + " set seqnum='" + seqnum + "' where a0100='" + rowSet.getString("a0100") + "' and  lower(basepre)='" + rowSet.getString("basepre").toLowerCase() + "'");
                    } else if (infor_type == 2) {
                        dao.update("update " + strDesT + " set seqnum='" + seqnum + "' where b0110='" + rowSet.getString("b0110") + "'");
                    } else if (infor_type == 3) {
                        dao.update("update " + strDesT + " set seqnum='" + seqnum + "' where E01A1='" + rowSet.getString("E01A1") + "'");
                    }
                }
            }
            rowSet.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }

    }

    /**
     * @throws GeneralException
     * @Title: autoAddRecord
     * @Description: TODO
     * @param tablebo
     * @param tablename
     *            void
     * @throws
     */
    private void autoAddRecord(TemplateTableBo tablebo, String tablename) throws GeneralException {

        ContentDAO dao = null;
        RowSet rset = null;
        try {
            if (!(tablebo.getOperationtype() == 0 || tablebo.getOperationtype() == 5))
                return;
            dao = new ContentDAO(this.conn);

            StringBuffer buf = new StringBuffer();
            buf.append("select count(*) as nrec from ");
            buf.append(tablename);
            rset = dao.search(buf.toString());
            int irow = 0;
            if (rset.next())
                irow = rset.getInt("nrec");
            if (irow != 0)
                return;
            String a0100 = null;
            RecordVo vo = new RecordVo(tablename);
            IDGenerator idg = new IDGenerator(2, this.conn);

            /**
             * 查找变化前的历史记录单元格 保存时把这部分单元格的内容 过滤掉，不作处理
             * */
            HashMap sub_map = tablebo.getHisModeSubCell();
            a0100 = idg.getId("rsbd.a0100");
            if (tablebo.getInfor_type() == 2 || tablebo.getInfor_type() == 3)
                a0100 = "B" + a0100;
            if (tablebo.getInfor_type() == 1 && (tablebo.getDest_base() == null || tablebo.getDest_base().length() == 0))
                throw new GeneralException("人员调入业务模板未定义目标库!");
            ArrayList dbList = DataDictionary.getDbpreList();
            if (tablebo.getInfor_type() == 1) {
                vo.setString("a0100", a0100);
                String dbpre = tablebo.getDest_base();
                for (int i = 0; i < dbList.size(); i++) {
                    String pre = (String) dbList.get(i);
                    if (pre.equalsIgnoreCase(tablebo.getDest_base()))
                        dbpre = pre;
                }
                vo.setString("basepre", dbpre);
                if (vo.hasAttribute("a0101_2")) {
                    vo.setString("a0101_2", "--");
                }
                if (vo.hasAttribute("a0101_1")) {
                    vo.setString("a0101_1", "--");
                }
            } else {
                if (tablebo.getInfor_type() == 2)
                    vo.setString("b0110", a0100);
                if (tablebo.getInfor_type() == 3)
                    vo.setString("e01a1", a0100);

                if (vo.hasAttribute("codeitemdesc_2")) {
                    vo.setString("codeitemdesc_2", "--");
                }
                if (vo.hasAttribute("codeitemdesc_1")) {
                    vo.setString("codeitemdesc_1", "--");
                }
            }
            Iterator iterator = sub_map.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String field_name = entry.getKey().toString();
                TemplateSetBo setbo = (TemplateSetBo) entry.getValue();
                TSubSetDomain setdomain = new TSubSetDomain(setbo.getXml_param());
                String xml = setdomain.outContentxml();
                vo.setString(field_name.toLowerCase(), xml);
            }

            rset = dao.search("select " + Sql_switcher.isnull("max(a0000)", "0") + "+1 from " + tablename);
            if (rset.next())
                vo.setInt("a0000", rset.getInt(1));

            String seqnum = CreateSequence.getUUID();
            vo.setString("seqnum", seqnum);
            dao.addValueObject(vo);
            // if(tablebo.getInfor_type()==1)
            // {
            // // this.getFormHM().put("a0100", a0100);
            // String dbpre=tablebo.getDest_base();
            // for(int i=0;i<dbList.size();i++)
            // {
            // String pre=(String)dbList.get(i);
            // if(pre.equalsIgnoreCase(tablebo.getDest_base()))
            // dbpre=pre;
            // }
            // // this.getFormHM().put("basepre",dbpre);
            // }
            // else if(tablebo.getInfor_type()==2)
            // // this.getFormHM().put("b0110", a0100);
            // else if(tablebo.getInfor_type()==3)
            // // this.getFormHM().put("e01a1", a0100);
            if ("1".equals(tablebo.getId_gen_manual())) {

            } else {
                tablebo.filloutSequence(a0100, tablebo.getDest_base(), tablename);
            }

            /** 生成序号 */

        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
    }

    /**
     * @param selfapply 
     * @param searchValue 
     * @param url 
     * @param bs_flag
     * @param business_model
     * @param tabid
     * @Title: searchTaskBusinessDataList
     * @Description: TODO
     * @param insId
     * @return ArrayList
     * @throws
     */
    public ArrayList searchTaskBusinessDataList(String task_id, String ins_id, String tabid, String business_model, String bs_flag, String url, String searchValue, String selfapply) {
      
        DbWizard dbWizard = new DbWizard(this.conn);
        ArrayList TaskBusinessDataList=new ArrayList();
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            DbWizard dbw = new DbWizard(this.conn);
            HashMap submitFlagMap = new HashMap();
            RowSet rset = null;
            StringBuffer sql = new StringBuffer();
            String tablename = this.userView.getUserName() + "templet_" + tabid;
            if (!"0".equals(ins_id))
                tablename="templet_"+tabid;
            else  if("1".equals(selfapply))//个人业务申请
                tablename="g_templet_"+tabid;
            if (this.templateTableBo.getInfor_type() == 1) {// 如果是对人员操作
                sql.append("select a0100,basepre");
                
                if (this.templateTableBo.getOperationtype() == 0) {// 人员调入型
                     
                    
                    if (dbWizard.isExistField(tablename, "ext", false)) {
                        sql.append(",photo,ext");
                    }
                    if (dbWizard.isExistField(tablename, "a0101_2", false)) {
                        sql.append(",a0101_2");
                    }
                    if (dbWizard.isExistField(tablename, "b0110_2", false)) {
                        sql.append(",b0110_2");
                    }
                    if (dbWizard.isExistField(tablename, "e0122_2", false)) {
                        sql.append(",e0122_2");
                    }
                } else {// 非人员调入型
                    sql.append(",a0101_1,b0110_1,e0122_1");
                }
            } else if (this.templateTableBo.getInfor_type() == 2 || this.templateTableBo.getInfor_type() == 3) {
                if(this.templateTableBo.getInfor_type() == 2){
                    sql.append("select b0110 ");
                }else{
                    sql.append("select e01a1 "); 
                }
                
                if (this.templateTableBo.getOperationtype() == 5) {// 如果是新建
                    sql.append(",codeitemdesc_2");
                }else{
                    sql.append(",codeitemdesc_1");
                }
                if (this.templateTableBo.getOperationtype() == 8 || this.templateTableBo.getOperationtype() == 9) {// 如果是合并划转
                    sql.append(",to_id ");
                }
            }
            
            sql.append(",submitflag ");
            sql.append(" from " + tablename);
            if("0".equals(ins_id)){
                if("1".equals(selfapply)){
                    sql.append(" where a0100='");
                    sql.append(this.userView.getA0100()+"'");
                    sql.append(" and lower(basepre)='");
                    sql.append(this.userView.getDbname().toLowerCase() + "'");
                }
            }
            if("2".equals(business_model)&&!"0".equals(ins_id)){// 已批任务处理的人员记录  xcs 不知道 原来是怎么想的 已办任务都是已经处理过的任务那ins_id肯定不是0现在改过来了
                String seqnum = "1";
                rset = dao.search("select seqnum from templet_" + tabid + " where ins_id=" + ins_id);
                if (rset.next())
                    seqnum = rset.getString(1) != null ? rset.getString(1) : "";
                if (seqnum.length() > 0) {
                    sql.append(" where exists (select null from t_wf_task_objlink where  templet_" + tabid + ".seqnum=t_wf_task_objlink.seqnum and templet_" + tabid + ".ins_id=t_wf_task_objlink.ins_id ");
                    // if(!"2".equals(this.task_sp_flag))
                    sql.append(" and ( " + Sql_switcher.isnull("special_node", "0") + "=0 or (" + Sql_switcher.isnull("special_node", "0") + "=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) ");
                    sql.append(" and t_wf_task_objlink.tab_id=" + tabid + " and t_wf_task_objlink.task_id=" + task_id + "    and ( " + Sql_switcher.isnull("t_wf_task_objlink.state", "0") + "<>3 )  )");
                } else {
                    sql.append(" where ins_id=" + ins_id);
                }

            } else if (!"0".equals(ins_id) && ("2".equals(bs_flag) || "3".equals(bs_flag) || "4".equals(bs_flag))) // 报备
            {
                StringBuffer strins = new StringBuffer();
                strins.append(task_id);
                //strins.append(",");

                sql.append(" where  exists (select null from t_wf_task_objlink where templet_" + tabid + ".seqnum=t_wf_task_objlink.seqnum  and templet_" + tabid + ".ins_id=t_wf_task_objlink.ins_id");
                sql.append("  and task_id in (" + strins.toString() + ") and tab_id=" + tabid + " and ( " + Sql_switcher.isnull("state", "0") + "<>3 ) ) ");// state
                                                                                                                                                            // is
                                                                                                                                                            // null
                                                                                                                                                            // or
                                                                                                                                                            // state=0

                rset = dao.search("select seqnum,submitflag,task_id from t_wf_task_objlink where   task_id in (" + strins.toString() + ") and tab_id=" + tabid + " and  (" + Sql_switcher.isnull("state", "0") + "<>3 )");// state
                                                                                                                                                                                                                          // is
                                                                                                                                                                                                                          // null
                                                                                                                                                                                                                          // or
                                                                                                                                                                                                                          // state=0//结束的流程看不到数据
                while (rset.next())
                    submitFlagMap.put(rset.getString("seqnum"), (rset.getString("submitflag") != null ? rset.getString("submitflag") : "0") + "," + rset.getString("task_id"));

            } else if (!"0".equals(ins_id)) // 审批表中的数据
            {
                StringBuffer strins = new StringBuffer();
                strins.append(task_id);

                String seqnum = "1";
                if (!"0".equals(ins_id)) {
                    rset = dao.search("select seqnum from templet_" + tabid + " where ins_id=" + ins_id);
                    if (rset.next())
                        seqnum = rset.getString(1) != null ? rset.getString(1) : "";
                }

                if (this.templateTableBo.isBsp_flag() && seqnum.length() > 0) {
                    sql.append(" where  exists (select null from t_wf_task_objlink where templet_" + tabid + ".seqnum=t_wf_task_objlink.seqnum  and templet_" + tabid + ".ins_id=t_wf_task_objlink.ins_id");
                    if (!"2".equals(this.templateTableBo.getTask_sp_flag()))
                        sql.append(" and ( " + Sql_switcher.isnull("special_node", "0") + "=0 or (" + Sql_switcher.isnull("special_node", "0") + "=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) ");

                    sql.append("  and task_id in (" + strins.toString() + ") and tab_id=" + tabid + " and ( " + Sql_switcher.isnull("state", "0") + "<>3 ) ) ");// state
                                                                                                                                                                // is
                                                                                                                                                                // null
                                                                                                                                                                // or
                                                                                                                                                                // state=0
                } else {
                    sql.append(" where task_id in(");
                    sql.append(strins.toString());
                    sql.append(")");
                }
                rset = dao.search("select seqnum,submitflag,task_id from t_wf_task_objlink where   task_id in (" + strins.toString() + ") and tab_id=" + tabid + " and  (" + Sql_switcher.isnull("state", "0") + "<>3 )");// state
                while (rset.next())
                    submitFlagMap.put(rset.getString("seqnum"), (rset.getString("submitflag") != null ? rset.getString("submitflag") : "0") + "," + rset.getString("task_id"));
            }
            if(searchValue!=null&&!"".equals(searchValue)){
                if(this.templateTableBo.getInfor_type() == 1){//开始处理查询条件
                    if (this.templateTableBo.getOperationtype() == 0) {
                        if (dbWizard.isExistField(tablename, "a0101_2", false)) {
                            searchValue="a0101_2 like '%"+searchValue+"%'";
                        }
                    }else{
                            searchValue="a0101_1 like '%"+searchValue+"%'";
                    }
                }else if (this.templateTableBo.getInfor_type() == 2 || this.templateTableBo.getInfor_type() == 3){
                    if (this.templateTableBo.getOperationtype() == 5) {// 如果是新建
                        searchValue="codeitemdesc_2 like '%"+searchValue+"%'";
                    }else{
                        searchValue="codeitemdesc_1 like '%"+searchValue+"%'";
                    }
                }
                if(sql.indexOf("where")!=-1)
                    sql.append(" and "+searchValue);
                else
                    sql.append(" where "+searchValue);//查询条件处理完毕
            }
            if ((this.templateTableBo.getInfor_type() == 1) && (!"0".equals(ins_id)) && ("2".equals(this.templateTableBo.getTask_sp_flag())) && (!this.userView.isSuper_admin())) {
                String operOrg = this.userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理6：培训管理7：招聘管理 8:业务模板
                String un_1 = "";
                String um_1 = "";
                String um_2 = "";
                String un_2 = "";
                if (this.templateTableBo.getOperationtype() == 0) {
                    if (dbw.isExistField("templet_" + tabid, "e0122_2", false))
                        um_2 = "e0122_2";
                    if (dbw.isExistField("templet_" + tabid, "b0110_2", false))
                        un_2 = "b0110_2";
                } else {
                    if (dbw.isExistField("templet_" + tabid, "e0122_1", false))
                        um_1 = "e0122_1";
                    if (dbw.isExistField("templet_" + tabid, "b0110_1", false))
                        un_1 = "b0110_1";
                }
                if (operOrg == null || !"UN`".equalsIgnoreCase(operOrg)) {
                    if ((operOrg != null) && (!"UN`".equalsIgnoreCase(operOrg))) {
                        String strB0110Where = "";
                        if (operOrg.length() > 3) {
                            String[] temp = operOrg.split("`");
                            for (int j = 0; j < temp.length; j++) {
                                if (temp[j] != null && temp[j].length() > 0) {
                                    String _pre = temp[j].substring(0, 2);
                                    if (this.templateTableBo.getOperationtype() == 0) {
                                        if ("UN".equalsIgnoreCase(_pre)) {
                                            if (un_2.length() > 0)
                                                strB0110Where = strB0110Where + " or  " + un_2 + " like '" + temp[j].substring(2) + "%'";
                                            else if (um_2.length() > 0)
                                                strB0110Where = strB0110Where + " or  " + um_2 + " like '" + temp[j].substring(2) + "%'";
                                        } else if ("UM".equalsIgnoreCase(_pre) && um_2.length() > 0) {
                                            strB0110Where = strB0110Where + " or  " + um_2 + " like '" + temp[j].substring(2) + "%'";
                                        }
                                    } else {
                                        if ("UN".equalsIgnoreCase(_pre)) {
                                            if (un_1.length() > 0)
                                                strB0110Where = strB0110Where + " or  " + un_1 + " like '" + temp[j].substring(2) + "%'";
                                            else if (um_1.length() > 0)
                                                strB0110Where = strB0110Where + " or  " + um_1 + " like '" + temp[j].substring(2) + "%'";
                                        } else if ("UM".equalsIgnoreCase(_pre) && um_1.length() > 0) {
                                            strB0110Where = strB0110Where + " or  " + um_1 + " like '" + temp[j].substring(2) + "%'";
                                        }

                                    }
                                }
                            }
                        } else if (operOrg == null) {
                            strB0110Where = strB0110Where + " or 1=2 ";
                        }

                        strB0110Where = strB0110Where.substring(3);
                        strB0110Where = "(" + strB0110Where + ")";
                        if (sql.indexOf("where") != -1)
                            sql.append(" and " + strB0110Where);
                        else
                            sql.append(" where " + strB0110Where);
                    }
                }

            }
            if ((this.templateTableBo.getInfor_type() == 2 || this.templateTableBo.getInfor_type() == 3) && (this.templateTableBo.getOperationtype() == 8 || this.templateTableBo.getOperationtype() == 9)) {
                String key = "b0110";
                if (this.templateTableBo.getInfor_type() == 3)
                    key = "e01a1";
                sql.append("  order by " + Sql_switcher.isnull("to_id", "100000000") + ",case when " + key + "=to_id then 100000000 else a0000 end asc ");
            } else
                sql.append(" order by a0000");
            String querySql = sql.toString();
            if (!"0".equals(ins_id)) {
                tablename="templet_"+tabid;
                querySql=querySql.replaceAll(this.userView.getUserName() + "templet_" + tabid, "templet_" + tabid);
            }
            if("0".equals(ins_id)){
                if("1".equals(selfapply)){//个人业务申请
                    tablename="g_templet_"+tabid;
                    querySql=querySql.replaceAll(this.userView.getUserName() + "templet_" + tabid, "g_templet_" + tabid);
                
                }
            }
            rset=dao.search(querySql);
            if (this.templateTableBo.getInfor_type() == 1) {
                while(rset.next()){
                    HashMap datamap = new HashMap();
                    String a0100 =rset.getString("a0100");
                    String basepre =rset.getString("basepre");
                    String ext =null;
                    String photo =null;
                    String name=null;
                    StringBuffer desc=new StringBuffer();
                    int submitflag =rset.getInt("submitflag"); 
                    if(this.templateTableBo.getOperationtype() == 0){//人员调入型
                        if (dbWizard.isExistField(tablename, "a0101_2", false)) {
                            name =rset.getString("a0101_2");
                        }
                        if (dbWizard.isExistField(tablename, "b0110_2", false)) {
                            String departmentCode=rset.getString("b0110_2");
                            String departmentDesc="";
                            if(departmentCode!=null&&!"".equals(departmentCode)){
                                departmentDesc=AdminCode.getCodeName("UN", departmentCode);
                                if("".equals(departmentDesc)){
                                    departmentDesc=AdminCode.getCodeName("UM", departmentCode);
                                }
                            }
                            desc.append(departmentDesc);
                        }
                        if (!dbWizard.isExistField(tablename, "e0122_2", false)) {
                            String postCode=rset.getString("e0122_2");
                            String postDesc="";
                            if(postCode!=null&&!"".equals(postCode)){
                                postDesc=AdminCode.getCodeName("UN", postCode);
                            }if(desc.length()==0){
                                desc.append(postDesc);
                            }else{
                                desc.append("/"+postDesc);
                            }
                            
                        }
                        if (dbWizard.isExistField(tablename, "ext", false)) {
                            ext=rset.getString("ext");
                        }
                        if(ext!=null){
                            FileOutputStream fout = null;
                            InputStream in = null;
                            try {
	                            File tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, rset.getString("ext"), new File(System.getProperty("java.io.tmpdir")));
	                            in = rset.getBinaryStream("photo");
	                            fout = new FileOutputStream(tempFile);
	                            int len;
	                            byte buf[] = new byte[1024];
	                            while ((len = in.read(buf, 0, 1024)) != -1) {
	                                fout.write(buf, 0, len);
	                            }
	                            photo = tempFile.getName();
							} finally {
								try {
									if (fout != null)
										in.close();
								} catch (Exception e) {
									e.printStackTrace();
								}
								try {
									if (fout != null)
										fout.close();
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
                        }
                    }else{
                        photo=ServletUtilities.createPhotoFile(basepre+"A00",a0100,"P",null);
                        name=rset.getString("a0101_1");
                        String departmentCode=rset.getString("b0110_1");
                        String departmentDesc="";
                        String postCode=rset.getString("e0122_1");
                        String postDesc="";
                        if(departmentCode!=null&&!"".equals(departmentCode)){
                            departmentDesc=AdminCode.getCodeName("UN", departmentCode);
                            if("".equals(departmentDesc)){
                                departmentDesc=AdminCode.getCodeName("UM", departmentCode);
                            }
                            desc.append(departmentDesc);
                        }
                        if(postCode!=null&&!"".equals(postCode)){
                            postDesc=AdminCode.getCodeName("UM", postCode);
                        }if(desc.length()==0){
                            desc.append(postDesc);
                        }else{
                            if("".equals(postDesc)){
                            }else{
                                desc.append("/"+postDesc);
                            }
                        }
                    }
                    datamap.put("name", name);
                    datamap.put("objectId", a0100+"`"+basepre);
                    datamap.put("desc",desc.toString());
                    datamap.put("infor_type",this.templateTableBo.getInfor_type()+"");
                    if(photo!=null&&!"".equals(photo)){
                        datamap.put("photo", url+"/servlet/DisplayOleContent?mobile=1&filename=" + photo);
                    }else{
                        datamap.put("photo", url+"/images/photo.jpg");
                    }
                    datamap.put("submitFlag", submitflag+"");
                    TaskBusinessDataList.add(datamap);
                }
            }else{
                int group_no=0;
                String _to_id="";
                String b0110="";
                while(rset.next()){
                    boolean isvalue=false;
                    HashMap datamap = new HashMap();
                    int submitflag =rset.getInt("submitflag"); 
                    if(this.templateTableBo.getInfor_type() == 2){
                        b0110=rset.getString("b0110"); 
                    }else{
                        b0110=rset.getString("e01a1");
                    }
                    String name="";
                    if(this.templateTableBo.getOperationtype() == 5){
                       name=rset.getString("codeitemdesc_2");
                    }else{
                        name=rset.getString("codeitemdesc_1");
                    }
                    
                    datamap.put("name", name);
                    datamap.put("objectId", b0110);
                    datamap.put("infor_type",this.templateTableBo.getInfor_type()+"");
                    datamap.put("submitFlag", submitflag+"");
                    if (this.templateTableBo.getOperationtype() == 8 || this.templateTableBo.getOperationtype() == 9) {// 如果是合并划转
                        if(rset.getString("to_id")==null){//如果获得的to_id==null,什么都不用处理
                            
                        }else{
                            if(_to_id.length()==0){//这说明是第一个有to_id的数据那么就是第一组
                                _to_id=rset.getString("to_id");
                                group_no=1;
                            }
                            if(!_to_id.equalsIgnoreCase(rset.getString("to_id"))){//如果_to_id不等于取出记录的to_id那么说明组号是另外一个分组了
                                group_no++;
                                _to_id=rset.getString("to_id");
                            }
                            datamap.put("groupId","组号："+group_no+"");
                            if(this.templateTableBo.getInfor_type()==2){
                                if(rset.getString("to_id").equalsIgnoreCase(rset.getString("b0110"))){  
                                    isvalue=true;
                                }
                             }
                            if(this.templateTableBo.getInfor_type()==3){
                                 if(rset.getString("to_id").equalsIgnoreCase(rset.getString("e01a1"))){
                                     isvalue=true;
                                 }
                             }
                            if(isvalue){
                                datamap.put("target","true");
                            }else{
                                datamap.put("target","false");
                            }
                        }
                    }
                    TaskBusinessDataList.add(datamap);
                } 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TaskBusinessDataList;
    }

    /**
     * @throws GeneralException  
     * @Title: deleteBusinessRecData 
     * @Description: TODO
     * @param taskId
     * @param tabid
     * @param inforType
     * @param objid void   
     * @throws 
    */
    public void deleteBusinessRecData(String task_id, String tab_id, String infor_type, String objid) throws GeneralException {
        RowSet rset = null;
        try{
            ContentDAO dao=new ContentDAO(this.conn);
            ArrayList list3 = new ArrayList();
            String setname="";
            String tempKey[]=objid.split("`");
            StringBuffer queryBuffer = new StringBuffer();
            int ins_id=0;
            if("0".equals(task_id)){
                setname=this.userView.getUserName()+"templet_"+tab_id;
            }else{
                setname="templet_"+tab_id;
                String tempsql= "select ins_id from t_wf_task  where task_id='"+task_id+"'";
                rset=dao.search(tempsql);
                if(rset.next()){
                    ins_id=rset.getInt("ins_id");
                }
            }
            
            RecordVo vo=new RecordVo(setname);
            if("1".equals(infor_type))
            {
                vo.setString("basepre", tempKey[1]);
                vo.setString("a0100", tempKey[0]);
            }
            else if("2".equals(infor_type))
            {
                vo.setString("b0110", tempKey[0]);
            }
            else if("3".equals(infor_type))
            {
                vo.setString("e01a1", tempKey[0]);
            }
            if(!"0".equals(task_id)){
                vo.setInt("ins_id",ins_id);
            }
            vo=dao.findByPrimaryKey(vo);
            String seqnum=vo.getString("seqnum");

            //处理组织和岗位
            if("3".equals(infor_type)|| "2".equals(infor_type)){//组织、岗位
                    if("0".equals(task_id)){
                        String key =tempKey[0];
                        queryBuffer.append("select * from ");
                        queryBuffer.append(setname);
                        if("2".equals(infor_type)){
                            queryBuffer.append("  where b0110= '");
                        }else{
                            queryBuffer.append("  where e01a1= '");
                        }
                        queryBuffer.append(key+"'");
                        rset = dao.search(queryBuffer.toString());
                    }
                    else{
                        rset = dao.search(" select * from "+setname+"  where  seqnum in (select seqnum from t_wf_task_objlink where task_id="+task_id+"  and tab_id="+tab_id+" and (state is null or state<>3) and seqnum='"+seqnum+"'and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )) ");
                    }
                    while(rset.next()){
                        if(("3".equals(infor_type)&&(8==this.templateTableBo.getOperationtype()))){
                            ArrayList list2 = new ArrayList();
                            if(rset.getString("e01a1")!=null&&rset.getString("e01a1").equals(rset.getString("to_id"))){
                                list2.add(rset.getString("e01a1"));
                                list3.add(list2);
                            }
                        }else if(("2".equals(infor_type)&&(8==this.templateTableBo.getOperationtype()||9==this.templateTableBo.getOperationtype()))){
                            if(rset.getString("b0110")!=null&&rset.getString("b0110").equals(rset.getString("to_id"))){
                                ArrayList list2 = new ArrayList();
                                list2.add(rset.getString("b0110"));
                                list3.add(list2);
                            }
                        }
                    }
            }
            if("0".equals(task_id)){//如果还没有进入审批流
                    //来自于消息,如果删除了此记录，则需要把tmessage中的消息 置为未处理状态
                    String state_flag=vo.getString("state");
                    if(state_flag==null|| "".equalsIgnoreCase(state_flag))
                        state_flag="0";
                    int from_msg=Integer.parseInt(state_flag);
                    if(from_msg==1)                 
                    {
                        setMessageState(vo,tab_id,infor_type);
    //                    else
    //                    {是否从消息表中删除，待做
    //                        if(infor_type.equals("1"))
    //                            dao.update("delete from tmessage where a0100='"+vo.getString("a0100")+"' and lower(db_type)='"+vo.getString("basepre").toLowerCase()+"' and noticetempid="+tab_id);
    //                        else if(infor_type.equals("2"))
    //                            dao.update("delete from tmessage where b0110='"+(String)arr[0]+"' and object_type=2  and noticetempid="+tab_id);
    //                        else if(infor_type.equals("3"))
    //                            dao.update("delete from tmessage where b0110='"+(String)arr[0]+"' and object_type=3  and noticetempid="+tab_id);
    //                    }
                    }
            }
            
            if(list3.size()>0){
                if(("3".equals(infor_type)&&(8==this.templateTableBo.getOperationtype()))){
                    dao.batchUpdate(" update "+setname+" set to_id=NULL where to_id=?", list3); 
                }
                else if(("2".equals(infor_type)&&(8==this.templateTableBo.getOperationtype()||9==this.templateTableBo.getOperationtype()))){
                    dao.batchUpdate(" update "+setname+" set to_id=NULL where to_id=?", list3); 
                }
            }
            ///开始撤销数据
            WF_Instance ins=new  WF_Instance(Integer.parseInt(tab_id),this.conn,this.userView);
            
            if("0".equals(task_id)){//未进入审批流
                TemplateTableBo tablebo=new TemplateTableBo(this.conn,Integer.parseInt(tab_id),this.userView);
                tablebo.updateWorkCodeState(setname,"submitflag=1");
                dao.update("delete from "+setname+"  where  seqnum='"+seqnum+"'");
            }
            else{//进入了审批流
                StringBuffer strsql=new StringBuffer("select * from templet_"+tab_id);
                strsql.append(" where  exists (select null from t_wf_task_objlink where templet_"+tab_id+".seqnum=t_wf_task_objlink.seqnum and templet_"+tab_id+".ins_id=t_wf_task_objlink.ins_id ");
                strsql.append("  and task_id="+task_id+"  and tab_id="+tab_id+" and (state is null or state=0 ) and seqnum='"+seqnum+"' and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ");
                if(ins.isStartNode(task_id))
                {
                    ins.insertKqApplyTable(strsql.toString(),tab_id,"0","10","templet_"+tab_id); //往考勤申请单中写入报批记录
                }
                dao.update("update t_wf_task_objlink set state=3  where task_id="+task_id+"  and tab_id="+tab_id+" and (state is null or state=0 ) and seqnum='"+seqnum+"' and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ");
            }
            /**结束正在处理的任务*/
            if(!"0".equals(task_id)){//在审批流中
                TemplateTableBo tablebo=new TemplateTableBo(this.conn,Integer.parseInt(tab_id),this.userView);
                    String _taskid=task_id;
                    String topic=tablebo.getRecordBusiTopic(Integer.parseInt(_taskid),0); 
                    if(topic.indexOf(",共0")!=-1)
                    { 
                        RecordVo task_vo=new RecordVo("t_wf_task");
                        task_vo.setInt("task_id",Integer.parseInt(_taskid));
                        task_vo=dao.findByPrimaryKey(task_vo);
                        if(task_vo!=null)
                        {
                            topic=tablebo.getRecordBusiTopicByState(Integer.parseInt(_taskid),3);
                            task_vo.setDate("end_date",new Date()); //DateStyle.getSystemTime());               
                            task_vo.setString("task_state",String.valueOf(NodeType.TASK_FINISHED));
                            task_vo.setString("task_topic", topic);
                    
                            String fullsender=this.userView.getUserFullName();
                            if(fullsender==null|| "".equalsIgnoreCase(fullsender))
                                fullsender=this.userView.getUserName(); 
                            String sender=null;
                            if(this.userView.getStatus()!=0)
                                sender=this.userView.getDbname()+this.userView.getA0100();
                            else
                                sender=this.userView.getUserId();
                            String appuser=task_vo.getString("appuser")+this.userView.getUserName()+",";
                            task_vo.setString("appuser", appuser);
                            task_vo.setString("a0100",sender);
                            task_vo.setString("a0101",fullsender);
                            task_vo.setString("content","撤销记录");
                            dao.updateValueObject(task_vo);
                        }
                    }
                    else
                        dao.update("update t_wf_task set task_topic='"+topic+"' where task_id="+_taskid);
            }
        }catch(Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        finally {
            if(rset!=null){
                try {
                    rset.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** 
     * @Title: setMessageState 
     * @Description: TODO
     * @param vo
     * @param tabId
     * @param inforType void   
     * @throws 
    */
    private void setMessageState(RecordVo vo, String template_id, String infor_type) throws GeneralException{
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            StringBuffer buf=new StringBuffer();
            ArrayList paralist=new ArrayList();
            if("1".equals(infor_type))
            {
                buf.append("update tmessage set state=0 where a0100=? and db_type=? and noticetempid=?"); 
                paralist.add(vo.getString("a0100"));
                paralist.add(vo.getString("basepre"));
                paralist.add(template_id);
            }
            else
            {
                buf.append("update tmessage set state=0 where b0110=? and noticetempid=?"); 
                if("2".equals(infor_type))
                    paralist.add(vo.getString("b0110"));
                else if("3".equals(infor_type))
                    paralist.add(vo.getString("e01a1"));
                paralist.add(template_id); 
            }
            dao.update(buf.toString(),paralist);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
    }

	public TemplateTableBo getTemplateTableBo() {
		return templateTableBo;
	}

	public void setTemplateTableBo(TemplateTableBo templateTableBo) {
		this.templateTableBo = templateTableBo;
	}
	 /**
	 * @param selfapply 
	 * @return 
     * @param pagenum  
     * @Title: saveBusinessData 
     * @Description: 保存页面业务单据的数据
     * @param tabid
     * @param taskId
     * @param inforType
     * @param objid
     * @param recordData
     * return void   
     * @throws 
    */
    public String saveBusinessData(String tabid, String task_id, String infor_type, String objid, String recordData, String pagenum, String selfapply) {
        StringBuffer sqlbuffer = new StringBuffer();
        RowSet rset =null;
        try{
            String tablename="";
            String tempKey[]=objid.split("`");
            String[] datavalueArray=recordData.split(",",-1);//liuyz 如果recordData最后有空串，split分割会有错误，后面提示数组越界。
            int ins_id=0;
            int skip=0;
            
            ContentDAO dao=new ContentDAO(this.conn);
            TemplateTableBo tablebo=new TemplateTableBo(this.conn,Integer.parseInt(tabid),this.userView);
            if("0".equals(task_id)){
                tablename=this.userView.getUserName()+"templet_"+tabid;
                if("1".equals(selfapply)){
                    tablename="g_templet_"+tabid;
                }
            }else{
                tablename="templet_"+tabid;
                String tempsql= "select ins_id from t_wf_task  where task_id='"+task_id+"'";
                rset=dao.search(tempsql);
                if(rset.next()){
                    ins_id=rset.getInt("ins_id");
                }
            }
            RecordVo vo = new RecordVo(tablename);
            if("1".equals(infor_type))
            {
                vo.setString("basepre", tempKey[1]);
                vo.setString("a0100", tempKey[0]);
                skip=2;
            }
            else if("2".equals(infor_type))
            {
                skip=1;
                vo.setString("b0110", tempKey[0]);
            }
            else if("3".equals(infor_type))
            {
                skip=1;
                vo.setString("e01a1", tempKey[0]);
            }
            if(!"0".equals(task_id)){
                vo.setInt("ins_id",ins_id);
            }
            MoblieTemplatePageBo pageBo = new MoblieTemplatePageBo(this.conn, Integer.parseInt(tabid),Integer.parseInt(pagenum),task_id, this.userView);
            ArrayList itemlist = (ArrayList) pageBo.getAllCell(Integer.parseInt(tabid),Integer.parseInt(pagenum), task_id).get(0);
          //  ArrayList recordList=getRecordList(itemlist);
            TemplateBo templatebo = new TemplateBo(this.conn,this.userView);
            HashMap filedPrivMap = templatebo.getFieldPriv(task_id, this.conn);//获取流程节点中的指标权限
            ArrayList recordList=templatebo.getCanEditRecordList(itemlist, tablebo, task_id, filedPrivMap);
            Document doc=null;
            Element element=null;
            for(int i=0;i<recordList.size();i++){
                MobileTemplateSetBo setBo = (MobileTemplateSetBo) recordList.get(i);
                String fieldtype = setBo.getField_type();
                String fieldname = setBo.getField_name()==null?"":setBo.getField_name().toLowerCase();
                String flag =setBo.getFlag();
                boolean sub_flag =setBo.isSubflag();
                int change_state=setBo.getChgstate();
                if("P".equals(flag)){//图片暂时还没想好怎么做
                    skip=skip+1;
                    continue;
                }
                if(change_state==1){
                    continue;
                }
                if(!sub_flag){
                	if(tablebo!=null&&tablebo.getOpinion_field()!=null&&tablebo.getOpinion_field().trim().length()>0&&fieldname.equalsIgnoreCase(tablebo.getOpinion_field()))
                	{
                		continue;
                	}
                    if("A".equals(fieldtype)){
                        String value=datavalueArray[i+skip].trim();
                        value=value.replaceAll("`g`g", ",");
                        if("V".equalsIgnoreCase(flag)){
                            vo.setString(fieldname,value);
                        }else{
                            vo.setString(fieldname+"_2",value);
                        }
                        
                    }else if("D".equals(fieldtype)){
                        String value=datavalueArray[i+skip].trim();
                        if(value!=null&&!"".equals(value)&&!"nan".equalsIgnoreCase(value)){//nan的问题处理从前台传过来的数据是NAN类型的空数据，js的处理问题
                            long datevalue = Long.parseLong(value);
                            Date date = new Date(datevalue);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            //-------------处理时间同步的问题BEGIN-----------
                            if("start_date_2".equalsIgnoreCase(fieldname+"_2")&&(tablebo.getOperationtype()==8||tablebo.getOperationtype()==9)){//如果是合并和划转岗位
                                
                                sqlbuffer.append("select to_id from ");
                                sqlbuffer.append(tablename);
                                sqlbuffer.append(" where ");
                                if("2".equals(infor_type)){
                                    sqlbuffer.append(" b0110='");
                                }else if("3".equals(infor_type)){
                                    sqlbuffer.append(" e01a1='");
                                   
                                }
                                sqlbuffer.append(tempKey[0]+"'");
                                if(ins_id!=0){
                                    sqlbuffer.append(" and ins_id=");
                                    sqlbuffer.append(ins_id);
                                }
                                rset =dao.search(sqlbuffer.toString());
                                sqlbuffer.setLength(0);
                                if(rset.next()){
                                    String to_id=rset.getString("to_id");
                                    if(to_id!=null&&to_id.equalsIgnoreCase(tempKey[0])){
                                        sqlbuffer.append("update ");
                                        sqlbuffer.append(tablename);
                                        sqlbuffer.append(" set start_date_2 =");
                                        String tempDateValue=sdf.format(date);
                                        sqlbuffer.append(Sql_switcher.dateValue(tempDateValue));
                                        sqlbuffer.append(" where to_id ='");
                                        sqlbuffer.append(tempKey[0]+"'");
                                        if(ins_id!=0){
                                            sqlbuffer.append(" and ins_id=");
                                            sqlbuffer.append(ins_id);
                                        }
                                    }
                                }
                                
                            }
                          //-------------处理时间同步的问题END-----------
                            if("V".equalsIgnoreCase(flag)){
                                vo.setDate(fieldname,date);
                            }else{
                                vo.setDate(fieldname+"_2",date);
                            }
                        }
                    } else if("N".equals(fieldtype)){
                        if("V".equalsIgnoreCase(flag)){
                            int disformat =setBo.getDisformat();
                            if(disformat==0){
                                String value=datavalueArray[i+skip].trim();
                                if(value.trim().length()>0){
                                    vo.setInt(fieldname,Integer.parseInt(value));
                                }else{
                                    vo.setInt(fieldname,0);
                                }
                                
                            }else{
                                String value=datavalueArray[i+skip].trim();
                                if(value.trim().length()>0){
                                    vo.setDouble(fieldname, Double.parseDouble(value));
                                }else{
                                    vo.setDouble(fieldname, 0);
                                }
                            }
                        }else{
                            FieldItem item = DataDictionary.getFieldItem(fieldname);
                            int desclength=item.getDecimalwidth();
                            if(desclength==0){
                                String value=datavalueArray[i+skip].trim();
                                if(value.length()>item.getItemlength()){
                                    throw new GeneralException(item.getItemdesc()+ResourceFactory.getProperty("templa.value.lengthError")+item.getItemlength()+","+ResourceFactory.getProperty("templa.value.fix"));
                                }
                                if(value.trim().length()>0){
                                    vo.setInt(fieldname+"_2",Integer.parseInt(value));
                                }else{
                                    vo.setInt(fieldname+"_2",0);
                                }
                            }else{
                                int disformat =setBo.getDisformat();
                                if(disformat==0){
                                    String value=datavalueArray[i+skip].trim();
                                    if(value.length()>item.getItemlength()){
                                        throw new GeneralException(item.getItemdesc()+ResourceFactory.getProperty("templa.value.lengthError")+item.getItemlength()+","+ResourceFactory.getProperty("templa.value.fix"));
                                    }
                                    if(value.trim().length()>0){
                                        vo.setInt(fieldname+"_2",Integer.parseInt(value));
                                    }else{
                                        vo.setInt(fieldname+"_2",0);
                                    }
                                }else{
                                    String value=datavalueArray[i+skip].trim();
                                    int index = value.indexOf(".");
                                    String tempvalue="";
                                    if(index!=-1){
                                        tempvalue = value.substring(0, index);
                                    }
                                    if(tempvalue.length()>item.getItemlength()){
                                        throw new GeneralException(item.getItemdesc()+ResourceFactory.getProperty("templa.value.lengthError")+item.getItemlength()+","+ResourceFactory.getProperty("templa.value.fix"));
                                    }
                                    if(value.trim().length()>0){
                                        vo.setDouble(fieldname+"_2", Double.parseDouble(value));
                                    }else{
                                        vo.setDouble(fieldname+"_2", 0);
                                    }
                                    
                                }
                            }
                        }
                    }else{
                        String value=datavalueArray[i+skip].trim();
                        value=value.replaceAll("`g`g", ",");
                        if("V".equalsIgnoreCase(flag)){
                            vo.setString(fieldname,value);
                        }else{
                            vo.setString(fieldname+"_2",value);
                        }
                    }
                }else{
                    String itemid="t_"+setBo.getSetname().toLowerCase()+"_2";
                    
                    String xml = removeAttributesFromXml(vo,datavalueArray[i+skip].trim(),itemid);
                    TSubSetDomain tSubSetDomain=new TSubSetDomain();
                    String item_id="codeitemdesc_1";
                    if(tablebo.getInfor_type()==1)
                        item_id="a0101_1";
                    if(xml.length()>0)
                    {
                        String _name="";
                        if(vo.hasAttribute(item_id))
                            _name=vo.getString(item_id);
                        tSubSetDomain.validateSubValue(_name,xml,doc,element);
                    }
                }
            }
            sysnA0101(vo,tablebo.getOperationtype(),itemlist); 
            boolean isrelevan=relevancePost(vo);
            if(isrelevan){
                dao.updateValueObject(vo);
                if(sqlbuffer.length()>0){
                    dao.update(sqlbuffer.toString());
                }
            }
            return "saveok";
        }catch(Exception e){
            e.printStackTrace();
            String message=e.toString();
            int index_i=message.indexOf("description:");
            message=message.substring(index_i+12);
            return message;
        }
        finally{
            if(rset!=null){
                try {
                    rset.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @Title: getRecordList 获得页面单据中单元格的相关数据
     * @Description: TODO
     * @param itemlist
     * @return ArrayList   
     * @throws 
    */
    private ArrayList getRecordList(ArrayList itemlist) {
        ArrayList list = new ArrayList();
        for (int i = 0; i < itemlist.size(); i++) {
            MobileTemplateSetBo setBo = (MobileTemplateSetBo) itemlist.get(i);
            String flag = setBo.getFlag();
            if (flag == null || "".equals(flag) || "H".equals(flag) || "F".equalsIgnoreCase(flag) || "S".equalsIgnoreCase(flag) || "C".equalsIgnoreCase(flag) || "T".equalsIgnoreCase(flag)) {
                continue;
            }
            list.add(setBo);   
        }
        return list;
    }

    /**
     * @return 
     * @param itemid  子集标识id
     * @Title: removeAttributesFromXml  删除子集记录中不需要的数据 rwPriv、fieldsPriv、fieldsWidth
     * @Description: TODO
     * @param vo 存放数据的vo
     * @param string xmlvalue 子集的xml数据   
     * @throws 
    */
    private String removeAttributesFromXml(RecordVo vo, String xmlvalue, String itemid) {
        String newxml = "";
        try{
            xmlvalue=SafeCode.decode(xmlvalue);
            xmlvalue=xmlvalue.replaceAll("＜", "<");
            xmlvalue=xmlvalue.replaceAll("＞", ">");
            xmlvalue=xmlvalue.replaceAll("＂", "\"");
            xmlvalue=xmlvalue.replaceAll("&", "");//如果连续分隔符`，被替换成,号啦
            if(itemid.indexOf("t_")==0){//t_***
                int index = itemid.lastIndexOf("_");
                if((index<itemid.length()-1) && itemid.charAt(index+1)=='2'){//t_***_2
                    //开始从xml删除属性
                    Document doc=PubFunc.generateDom(xmlvalue);
                    Element element=null;
                    XMLOutputter outputter = new XMLOutputter();
                    Format format = Format.getPrettyFormat();
                    format.setEncoding("UTF-8");
                    outputter.setFormat(format);
                    String xpath="/records";
                    XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点      
                    element =(Element) findPath.selectSingleNode(doc);
                    if(element!=null){
                        if(element.getAttribute("rwPriv")!=null){
                            element.removeAttribute("rwPriv");
                        }
                        if(element.getAttribute("fieldsPriv")!=null){
                            element.removeAttribute("fieldsPriv");
                        }
                        if(element.getAttribute("fieldsWidth")!=null){
                            element.removeAttribute("fieldsWidth");
                        }
                    }
                    newxml = outputter.outputString(doc);
                    vo.setString(itemid, newxml);
                    
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return newxml;
    }
    
    
    /**
     * 人员调入模板同步姓名至a0101_1
     * @param operationtype
     * @param itemlist 
     */
    private void sysnA0101(RecordVo vo,int operationtype, ArrayList itemlist)
    {
        boolean bflag=false;
        if(operationtype==0)
        {
            if(vo.hasAttribute("a0101_2"))
            {
                for(int i=0;i<itemlist.size();i++)
                {
                    MobileTemplateSetBo setBo = (MobileTemplateSetBo) itemlist.get(i);
                    String tempname=setBo.getField_name()+"_2";
                    /**数据集中有变化后姓名字段*/
                    if("a0101_2".equalsIgnoreCase(tempname))
                    {
                        bflag=true;
                        break;
                    }
                }
                if(bflag)
                    vo.setString("a0101_1", vo.getString("a0101_2"));
            }
        }
        if(operationtype==5)
        {
            if(vo.hasAttribute("codeitemdesc_2"))
            {
                for(int i=0;i<itemlist.size();i++)
                {
                    MobileTemplateSetBo setBo = (MobileTemplateSetBo) itemlist.get(i);
                    String tempname=setBo.getField_name()+"_2";
                    /**数据集中有变化后姓名字段*/
                    if("codeitemdesc_2".equalsIgnoreCase(tempname))
                    {
                        bflag=true;
                        break;
                    }
                }
                if(bflag)
                    vo.setString("codeitemdesc_1", vo.getString("codeitemdesc_2"));
            }
        }
    }

    /**
     * @Title: searchBusinessFlowData 
     * @Description: TODO
     * @param tabid
     * @param taskid
     * @param inforType void   
     * @return 
     * @throws GeneralException 
    */
    public ArrayList searchBusinessFlowData(String tabid, String taskid, String infor_type) throws GeneralException {
        
        String type="";//业务模版类型，对人还是对单位|岗位
        String ins_id="0";
        ArrayList inforList = new ArrayList();//存放模版名称和姓名(组织单元名称)
        HashMap inforMap = new HashMap();
        if(infor_type!=null&& "1".equals(infor_type))
            type="1";
        if(infor_type!=null&& "2".equals(infor_type))
            type="10";
        if(infor_type!=null&& "3".equals(infor_type))
            type="11";
// 暂时看不到这个怎用        String sqlwhere = " and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and lower(username)='"+this.userView.getUserName().toLowerCase()+"'   )  ) ";
        StringBuffer strsql=new StringBuffer();
        ContentDAO dao=new ContentDAO(this.conn);
        RowSet rset =null;
        try{


            String querySql ="select ins_id from t_wf_task where task_id="+taskid;
            rset=dao.search(querySql);
            if(rset.next()){
                ins_id= rset.getInt("ins_id")+"";
            }
            String task_id_pro="";
            String flag="";
            rset=dao.search("select task_id_pro,flag from t_wf_task where task_id="+taskid);
            if(rset.next())
            {
                if(rset.getString("task_id_pro")!=null&&rset.getString("task_id_pro").trim().length()>0)
                    task_id_pro=rset.getString("task_id_pro");
                if(rset.getString("flag")!=null)
                    flag=rset.getString("flag");
            }
            String tableName="";
            String names="";
             rset=dao.search("select tabid from t_wf_instance where ins_id=(select ins_id from t_wf_task where task_id="+taskid+")");
            if(rset.next())
                tabid=rset.getString("tabid");
            rset=dao.search("select template_table.name from template_table where tabid="+tabid);
            if(rset.next()){
                    tableName=rset.getString("name");
            }
            inforMap.put("tableName", tableName);//存放的模版名称
            
            String nodes ="";
            TemplateTableBo bo=new TemplateTableBo(this.conn,Integer.parseInt(tabid),this.userView);
            if(bo.get_static()==10||bo.get_static()==11)
                type=String.valueOf(bo.get_static());
            int mode = bo.getSp_mode();
            boolean def_flow_self=false; //是否是自定义审批流程
            if(mode==0){
                WorkflowBo workflowBo=new WorkflowBo(this.conn,Integer.parseInt(tabid),this.userView); 
                
                task_id_pro = workflowBo.getSubedTaskids(Integer.parseInt(taskid), Integer.parseInt(ins_id));
             
            }
            else if(mode==1&&bo.isDef_flow_self(Integer.parseInt(taskid)))
            {
                def_flow_self=true;
            }
        
                switch(Sql_switcher.searchDbServer()){
                 case Constant.MSSQL:
                  { 
                        rset=dao.search("select * from templet_"+tabid+" where ins_id="+ins_id+" and  exists (select null from t_wf_task_objlink where task_id="+taskid+" and templet_"+tabid+".seqnum=seqnum)");//+sqlwhere+" )");
                        while(rset.next()){
                            if(type!=null&&("10".equals(type)|| "11".equals(type))){
                                names+=","+rset.getString("codeitemdesc_1");
                            }else{
                                names+=","+rset.getString("a0101_1");
                            }
                        }
                        break;
                  }
                  case Constant.ORACEL:
                  case Constant.DB2:
                  {  
                        rset=dao.search("select * from templet_"+tabid+" where ins_id="+ins_id+" and  exists (select null from t_wf_task_objlink where task_id="+taskid+" and templet_"+tabid+".seqnum=seqnum)");//+sqlwhere+" )");
                        while(rset.next()){
                            if(type!=null&&("10".equals(type)|| "11".equals(type))){
                                names+=","+rset.getString("codeitemdesc_1");
                            }else{
                                names+=","+rset.getString("a0101_1");
                            }
                        }   
                        break;
                  }
                 
                  default:
                          rset=dao.search("select * from templet_"+tabid+" where ins_id="+ins_id+" and  exists (select null from t_wf_task_objlink where task_id="+taskid+" and templet_"+tabid+".seqnum=seqnum)");//+sqlwhere+" )");
                  while(rset.next()){
                        if(type!=null&&("10".equals(type)|| "11".equals(type))){
                            names+=","+rset.getString("codeitemdesc_1");
                        }else{
                            names+=","+rset.getString("a0101_1");
                        }
                    }   
                    break;
                }
            if("".equals(names)){
                rset=dao.search("select * from templet_"+tabid+" where task_id="+taskid );
                while(rset.next()){
                    if(type!=null&&("10".equals(type)|| "11".equals(type))){
                        names+=","+rset.getString("codeitemdesc_1");
                    }else{
                        names+=","+rset.getString("a0101_1");
                    }
                }
            }
            if(names.length()>0)
                names=names.substring(1);
            inforMap.put("a0101s", names);
            String format_str="yyyy-MM-dd HH:mm";
            if(Sql_switcher.searchDbServer()==Constant.ORACEL)
                format_str="yyyy-MM-dd hh24:mi";
        
            if(task_id_pro.length()==0)
            {   
                strsql.append("select task_id,a0101 ,"+Sql_switcher.dateToChar("end_date",format_str)+" end_date,start_date,sp_yj,content,state,task_state,actorname,nodename,bs_flag,task_type,t_wf_task.node_id from t_wf_task,t_wf_node where t_wf_task.node_id=t_wf_node.node_id  and   ins_id=");
                strsql.append(ins_id);
                strsql.append(" and (task_type='2' or task_type='1')  order by task_id,end_date");
            }
            else
            {
                if(def_flow_self)
                {
                    strsql.append(" select task_id from t_wf_task where ( pri_task_id in (select pri_task_id from t_wf_task where task_id in ("+task_id_pro.substring(1)+")) "
                            +" and task_id not in ("+task_id_pro.substring(1)+") ) or (ins_id="+ins_id+" and bs_flag='3') ");
                    rset=dao.search(strsql.toString());
                    while(rset.next())
                    {
                        task_id_pro=","+rset.getString("task_id")+task_id_pro;
                    }
                    strsql.setLength(0);
                    strsql.append("select task_id,a0101,"+Sql_switcher.dateToChar("end_date",format_str)+" end_date,start_date,sp_yj,content,state,task_state,actorname ,bs_flag,task_type,node_id  from t_wf_task  where  ins_id=");
                    strsql.append(ins_id);
                    strsql.append(" and (task_type='2' or task_type='1') and task_id in ("+task_id_pro.substring(1)+")  order by task_id,end_date");
                }
                else
                {
                    strsql.append("select task_id,a0101,"+Sql_switcher.dateToChar("end_date",format_str)+" end_date,start_date,sp_yj,content,state,task_state,actorname,nodename,bs_flag,task_type,t_wf_task.node_id from t_wf_task,t_wf_node where t_wf_task.node_id=t_wf_node.node_id  and ins_id=");
                    strsql.append(ins_id);
                    strsql.append(" and (task_type='2' or task_type='1') and task_id in ("+task_id_pro.substring(1)+")  order by task_id,end_date");
                }
            }
            
            rset=dao.search(strsql.toString());
            ArrayList splist=new ArrayList();
            int i=0;
            int  beginNodeid=0;
            HashMap endtimemap = new HashMap();
            HashMap firstAppuser = new HashMap(); 
            while(rset.next())
            {
                String end_date="";
                String task_type=rset.getString("task_type");
                int node_id=rset.getInt("node_id");
                if("1".equals(task_type))
                    beginNodeid=rset.getInt("node_id");
                String bs_flag=rset.getString("bs_flag")!=null?rset.getString("bs_flag"):"1"; //1：待批 2：加签 3报备
                HashMap appuser = new HashMap();
                if(i==0){
                    firstAppuser.put("bs_flag", bs_flag);
                }else{
                    appuser.put("bs_flag", bs_flag);
                }
                if(rset.getString("a0101")!=null&&rset.getString("a0101").trim().length()>0){
                    if(i==0){
                        firstAppuser.put("a0101", rset.getString("a0101"));
                    }else{
                        appuser.put("a0101", rset.getString("a0101"));
                    }
                }else{
                    if(i==0){
                        firstAppuser.put("a0101", rset.getString("actorname"));
                    }else{
                        appuser.put("a0101", rset.getString("actorname"));
                    }
                }
                
                if(def_flow_self)
                {
                    if(beginNodeid==node_id){
                        if(i==0){
                            firstAppuser.put("appuser","发起人");
                        }
                    }
                    else if("3".equals(bs_flag)){
                        appuser.put("appuser","报备人");
                    }
                    else
                        appuser.put("appuser","审批人");
                }else{
                    if(mode==0&&beginNodeid!=node_id&&rset.getString("nodename")!=null&&rset.getString("nodename").length()>0){//自动流转
                        if(i==0){
                            firstAppuser.put("appuser",""+rset.getString("nodename")+"");
                        }else{
                            appuser.put("appuser",""+rset.getString("nodename")+""); 
                        }
                    }else if(beginNodeid==node_id){
                        if(i==0){
                            firstAppuser.put("appuser","发起人");
                        }else{
                            appuser.put("appuser","发起人"); 
                        }
                    }else{
                        if(i==0){
                            firstAppuser.put("appuser","审批人");
                        }else{
                            appuser.put("appuser","审批人"); 
                        }
                    }
                }
                if(i==0){
                    firstAppuser.put("task_state",rset.getString("task_state"));
                }else{
                    appuser.put("task_state",rset.getString("task_state")); 
                }
                if(rset.getString("task_state")!=null&&"4".equals(rset.getString("task_state"))){//4代表终止
                    if("08".equals(rset.getString("state"))){//=06(结束)=07(驳回)=08(报审)
                        if(i==0){
                            firstAppuser.put("appuser","系统报批");
                        }else{
                            appuser.put("appuser","系统报批"); 
                        }
                    }else if("07".equals(rset.getString("state"))){
                        if(i==0){
                            firstAppuser.put("appuser","系统驳回");
                        }else{
                            appuser.put("appuser","系统驳回"); 
                        }
                    }else{
                        if(i==0){
                            firstAppuser.put("appuser","");
                        }else{
                            appuser.put("appuser",""); 
                        }
                    }
                }
                if(i==0){
                    String temp=rset.getString("sp_yj");
                    if(rset.getString("task_state")!=null&&"4".equals(rset.getString("task_state"))){
                        if("08".equals(rset.getString("state"))){
                            firstAppuser.put("sp_yj","系统报批");
                        }else if("07".equals(rset.getString("state"))){
                            firstAppuser.put("sp_yj","系统驳回");
                        }else
                            firstAppuser.put("sp_yj","");
                    }else{
                        firstAppuser.put("sp_yj",AdminCode.getCodeName("30", temp));
                    }
                }else{
                    String temp=rset.getString("sp_yj");
                    if(rset.getString("task_state")!=null&&"4".equals(rset.getString("task_state"))){
                        if("08".equals(rset.getString("state"))){
                            appuser.put("sp_yj","系统报批");
                        }else if("07".equals(rset.getString("state"))){
                            appuser.put("sp_yj","系统驳回");
                        }else
                            appuser.put("sp_yj","");
                    }else{
                        appuser.put("sp_yj",AdminCode.getCodeName("30", temp));
                    }
                }
                if(i==0){
                    firstAppuser.put("content",Sql_switcher.readMemo(rset,"content").replace("<br>", "\n").replace("&nbsp;", " "));
                }else{
                    appuser.put("content",Sql_switcher.readMemo(rset,"content").replace("<br>", "\n").replace("&nbsp;", " "));  
                }
                if(rset.getString("end_date")!=null){
                    if(i==0){
                        firstAppuser.put("end_date",rset.getString("end_date"));
                    }else{
                        appuser.put("end_date",rset.getString("end_date"));  
                    }
                }
                else{
                    if(i==0){
                        firstAppuser.put("end_date","");
                    }else{
                        appuser.put("end_date","");  
                    }
                }
                if(i!=0&&(rset.getString("state")==null||rset.getString("state").trim().length()==0))
                    continue;
                if(i!=0&&((rset.getString("a0101")==null||rset.getString("a0101").trim().length()==0)&&!"4".equals(rset.getString("task_state"))))
                    continue;
                if(i!=0&&(rset.getString("state")==null||rset.getString("state").trim().length()==0))
                    continue;
                if(i!=0&&((rset.getString("a0101")==null||rset.getString("a0101").trim().length()==0)&&!"4".equals(rset.getString("task_state"))))
                    continue;
                 //最后一个审批人不显示：状态不为结束，别且结束日期不为空。
                if(rset.isLast()){
                    if(!"5".equals(rset.getString("task_state"))&&((String) appuser.get("end_date")).length()<=0)
                        continue;
                }
                i++;
                if(appuser.size()>0)
                    splist.add(appuser);
            }
            inforList.add(inforMap);
            inforList.add(firstAppuser);
            inforList.add(splist);
            return inforList;
        }catch(Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * @Title: updateSubmitFlag 
     * @Description: TODO
     * @param tabid
     * @param objectIds void   
     * @throws GeneralException
    */
    public void updateSubmitFlag(String tabid, String objectIds) throws GeneralException {
        
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList sqllist = new ArrayList();
        try{
            StringBuffer  updatesql = new StringBuffer();
//            updatesql.append("update ");
//            updatesql.append(this.userView.getUserName()+"templet_"+tabid);
//            updatesql.append(" set submitflag=0");
//            dao.update(updatesql.toString());
            if(objectIds!=null&&!"".equals(objectIds)){
                String splitArray[]=objectIds.split(",");
                updatesql.setLength(0);
                this.templateTableBo = new TemplateTableBo(this.conn, Integer.parseInt(tabid), this.userView);
                int infor_type =templateTableBo.getInfor_type();
                for(int i=0;i<splitArray.length;i++){
                    updatesql.append("update ");
                    updatesql.append(this.userView.getUserName()+"templet_"+tabid);
                    updatesql.append(" set submitflag=");
                    String objectId =splitArray[i];
                    String tempArray[] =objectId.split("`");
                    if(infor_type==1){
                        String a0100=tempArray[0];
                        String basepre=tempArray[1];
                        updatesql.append(tempArray[2]);
                        updatesql.append(" where a0100='");
                        updatesql.append(a0100+"' and basepre='");
                        updatesql.append(basepre+"'");
                    }else if(infor_type==2){
                        String b0100 =tempArray[0];
                        updatesql.append(tempArray[1]);
                        updatesql.append(" where b0110='");
                        updatesql.append(b0100+"'");
                    }else if(infor_type==3){
                        String e01a1 =tempArray[0];
                        updatesql.append(tempArray[1]);
                        updatesql.append(" where e01a1='");
                        updatesql.append(e01a1+"'");
                    }
                    sqllist.add(updatesql.toString());
                    updatesql.setLength(0);
                }
                dao.batchUpdate(sqllist);
            }
            
        }catch(Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
    }

    /** 
     * @Title: searchResolveTask 
     * @Description: TODO
     * @param queryType
     * @param days
     * @param startDate
     * @param endDate
     * @param bsFlag
     * @param taskFormName
     * @return Object   
     * @throws GeneralException
    */
    public ArrayList searchResolveTask(String query_type, String days, String startDate, String endDate, String bsFlag, String taskFormName) throws GeneralException {
        StringBuffer strsql = new StringBuffer();// 这个sql语句并没有考虑接收范围等因素。接收范围在getTaskList（）方法中处理。
        ArrayList resolveTaskList = new ArrayList();
        String static_="static";
        if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
        	static_="static_o";
        }
        RowSet rs=null;
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            String format_str="yyyy-MM-dd HH:mm";
            if(Sql_switcher.searchDbServer()==Constant.ORACEL)
                format_str="yyyy-MM-dd hh24:mi";
            strsql.append("select U.ins_id,T.task_topic,U.tabid,U.actorname fullname,a0101, task_state finished ,"+Sql_switcher.dateToChar("U.start_date",format_str)+" start_date,"+Sql_switcher.dateToChar("T.end_date",format_str)+" end_date,");
            strsql.append("T.actorname,T.task_id,T.flag,U.tabid,tt."+static_+",U.finished insfinished   from t_wf_task T,t_wf_instance U,template_table tt ");
            strsql.append(" where  T.ins_id=U.ins_id  and U.tabid=tt.tabid and ((task_type='2' ) and (task_state='5'  or task_state='6' ) ) ");
            
            TemplateTableParamBo tp=new TemplateTableParamBo(this.conn); 
            String tabids=tp.getAllDefineKqTabs(0); 
            //1：审批任务 2：加签任务 3：报备任务  4：空任务
            strsql.append(" and  "+Sql_switcher.isnull("T.bs_flag","'1'")+"='"+bsFlag+"' ");
            strsql.append(" and  T.task_type!=1 ");
            strsql.append(" and (( (task_topic not like '%共0人%' and task_topic not like '%共0条%'  ) and ");
            strsql.append(" ( T.flag=1 and U.ins_id in (select ins_id from t_wf_task where "+getInsFilterWhere("")+" and (task_state='5'  or task_state='6' ) and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("content"),"' '")+"<>'重新分派' )) )");
            strsql.append(" or ( ");
            
            strsql.append(" ("+getInsFilterWhere("T.")+" and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("T.content"),"' '")+"<>'重新分派'    )"); 
            strsql.append(" and   U.ins_id not in ( ");
            strsql.append("  select ins_id from t_wf_task where  ( task_topic not like '%共0人%' and  task_topic not like '%共0条%'  ) and (task_type='2' )  and (task_state='5'  or task_state='6' )  and flag=1 ");
            strsql.append("   and   ins_id in (select ins_id from t_wf_task where  "+getInsFilterWhere("")+"   and (task_state='5'  or task_state='6' ) and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("content"),"' '")+"<>'重新分派'  ) ) ");
            strsql.append(" )");
            strsql.append(")");
            if (taskFormName != null && !"".equals(taskFormName) && taskFormName.length() > 0) {
                strsql.append(" and tt.name like '%" + taskFormName + "%'");
            }
            if("1".equals(query_type))//最近多少天
            {
                if(validateNum(days)){
                String strexpr=Sql_switcher.addDays(Sql_switcher.sqlNow(),"-"+days);
                strsql.append(" and U.start_date>=");
                strsql.append(strexpr); 
                }
            }else{
                if("2".equals(query_type)){
                strsql.append(" and ( 1=1 ");
                if(validateDate(startDate))
                    strsql.append(PubFunc.getDateSql(">=","U.start_date",startDate));
                if(validateDate(endDate))
                    strsql.append(PubFunc.getDateSql("<=","U.start_date",endDate));
                strsql.append(" )");
                }
            }
            
            boolean isSource=false;
            if(this.userView.isHavetemplateid(IResourceConstant.RSBD)||this.userView.isHavetemplateid(IResourceConstant.ORG_BD)||this.userView.isHavetemplateid(IResourceConstant.POS_BD)||this.userView.isHavetemplateid(IResourceConstant.GZBD)||this.userView.isHavetemplateid(IResourceConstant.INS_BD)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS_FG)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS_GX)||this.userView.isHavetemplateid(IResourceConstant.PSORGANS_JCG))
                isSource=true;
            if(this.userView.isSuper_admin())
                isSource=true;
            if(!isSource)
            {   
                strsql.append(" and 1=2 ");
            }
            //获得各实例下当前审批人sql
            int index=strsql.toString().indexOf("from");
            String sql="";
            if(index!=-1)
                sql=" select ins_id,actorname from t_wf_task where ins_id in (select  U.ins_id "+strsql.substring(index)+") and task_type='2' and  "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and task_state='3' order by ins_id";
            
            strsql.append(" order by T.end_date DESC");
            rs=dao.search(strsql.toString());
            //获得各实例下当前审批人
            HashMap ins_CurrentSpInfo=new HashMap(); // 
            if(sql.length()>0)
            { 
                RowSet rowSet=dao.search(sql);
                int ins_id=0;
                String actorname="";
                while(rowSet.next())
                {
                    if(ins_id==0)
                        ins_id=rowSet.getInt("ins_id");
                    if(ins_id==rowSet.getInt("ins_id"))
                        actorname+=","+rowSet.getString("actorname");
                    else
                    {
                        ins_CurrentSpInfo.put(String.valueOf(ins_id),actorname.toString().substring(1));
                        ins_id=rowSet.getInt("ins_id");
                        actorname=","+rowSet.getString("actorname");
                    } 
                }
                if(ins_id!=0&&actorname.length()>0)
                    ins_CurrentSpInfo.put(String.valueOf(ins_id),actorname.toString().substring(1));
            }

            HashMap map=null;
            HashMap operationTypeMap=new HashMap();
            HashMap tableNameMap=new HashMap();
            
            while(rs.next())
            {
                map=new HashMap();
                String tabid=rs.getString("tabid");
                String task_id=rs.getString("task_id");
                String ins_id=rs.getString("ins_id");
                String flag=rs.getString("flag")!=null?rs.getString("flag"):"";
                String task_topic=rs.getString("task_topic");
                String _static=rs.getString(static_);
                if("".equals(flag))
                {
                    String operationType="";
                    if(operationTypeMap.get(tabid)==null)
                    {
                        operationType=findOperationType(tabid);
                        operationTypeMap.put(tabid, operationType);
                    }
                    else
                        operationType=(String)operationTypeMap.get(tabid);
                    
                    
                    String tabName="";
                    if(tableNameMap.get(tabid)==null)
                    {
                        tabName=findTabName(tabid);
                        tableNameMap.put(tabid,tabName);
                    }
                    else
                    {
                        tabName=(String)tableNameMap.get(tabid);
                    }
                    String topic="";
        
                    if(userView.getStatus()!=4)
                        topic=getTopic(task_id,"templet_"+tabid,Integer.parseInt(operationType),tabid,_static);
                    else
                    {
                        if(_static!=null&&("10".equals(_static)|| "11".equals(_static)))
                        {
                            topic=getTopic(task_id,"templet_"+tabid,Integer.parseInt(operationType),tabid,_static);
                        } 
                        else
                            topic=getTopic(task_id,"templet_"+tabid,Integer.parseInt(operationType),tabid,_static);
                    }
                    
                    if(topic.indexOf(",共0")!=-1) //撤销任务主题
                    {
                        topic=getRecordBusiTopicByState(Integer.parseInt(task_id),3,"templet_"+tabid,dao,Integer.parseInt(operationType), _static);
                    }
                    
                    task_topic=tabName;
                }
                if(ins_CurrentSpInfo.get(ins_id)!=null)
                    map.put("curr_user",(String)ins_CurrentSpInfo.get(ins_id));
                else
                    map.put("curr_user","");
                map.put("title",task_topic);
                map.put("ins_id",rs.getString("ins_id"));
                map.put("tabid",rs.getString("tabid"));
                map.put("user",rs.getString("fullname"));
                map.put("start_date", rs.getString("start_date"));
                map.put("time", rs.getString("start_date")+"~"+(rs.getString("end_date")==null?" ":rs.getString("end_date")));
                map.put("task_id",rs.getString("task_id"));
                if("5".equals(rs.getString("insfinished"))){
                    map.put("task_state", "结束");
                }else{
                    map.put("task_state", "等待");
                }
                resolveTaskList.add(map);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        finally{
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return resolveTaskList;
    }
    
    private String getInsFilterWhere(String othername)
    {
        StringBuffer strwhere=new StringBuffer();
        /**用户号*/
        String dbpre=this.userView.getDbname(); //库前缀
        String userid=dbpre+this.userView.getA0100();//人员编号
        /**如果为空，设置一个不可能出现的用户，为了少写几行代码*/
        if(userid==null||userid.length()==0)
        userid="-1";
         
        strwhere.append(" upper("+othername+"actorid) in ('");
        strwhere.append(userid.toUpperCase());
        strwhere.append("','");
        strwhere.append(this.userView.getUserName().toUpperCase());
        strwhere.append("') ");
            
        if(this.userView.getRolelist().size()>0)
        {
            strwhere.append(" or  ( "+othername+"actor_type=2 and upper("+othername+"a0100) in ('"+userid.toUpperCase()+"','"+this.userView.getUserName().toUpperCase()+"') and  upper("+othername+"actorid) in ( ");
            String str="";
            for(int i=0;i<this.userView.getRolelist().size();i++)
            {
                    str+=",'"+(String)this.userView.getRolelist().get(i)+"'";
            }
            strwhere.append(str.substring(1));
            strwhere.append(" ) )");
            //本人和有范围的角色
            strwhere.append("  or(( T.actor_type=2 or  T.actor_type=5 )and exists (select null from t_wf_task_objlink where ins_id=U.ins_id and task_id=T.task_id and node_id= T.node_id and tab_id=U.tabid and (state=1 or state=2) and upper(username)='"+this.userView.getUserName().toUpperCase()+"' ))");
        }
        
    //  if(this.userView.getUserPosId()!=null&&this.userView.getUserPosId().trim().length()>0)
        {
    //      strwhere.append(" or  ( "+othername+"actor_type=3 and upper("+othername+"a0100) in ('"+userid.toUpperCase()+"','"+this.userView.getUserName().toUpperCase()+"') and upper("+othername+"actorid)='@K"+this.userView.getUserPosId().trim()+"' ) ");
        
            String a0100=this.userView.getDbname()+this.userView.getA0100();
            if(a0100==null||a0100.length()==0)
                a0100=this.userView.getUserName();
            /**组织元*/
            strwhere.append(" or (T.actor_type='3' and T.a0100='"+a0100+"')"); 
        }
        
        return " ( "+strwhere.toString()+" ) ";
    }
    
    
    public String findOperationType(String tabid)
    {
        String operationType="";
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rowSet=dao.search("select operationtype from operation where operationcode=(select operationcode from template_table where tabid="+tabid+")");
            if(rowSet.next())
                operationType=rowSet.getString("operationtype");
            if(rowSet!=null)
                rowSet.close(); 
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return operationType;
    }
    
    public String findTabName(String tabid)
    {
        String tabName="";
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rowSet=dao.search("select name from template_table where tabid="+tabid);
            if(rowSet.next())
                tabName=rowSet.getString("name");
            if(rowSet!=null)
                rowSet.close(); 
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return tabName;
    }
    public String getTopic(String task_id,String tabname,int operationtype,String tab_id,String type)
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
            
            if(type!=null&&("10".equals(type)|| "11".equals(type)))
            {
                a0101="codeitemdesc_1";
                if(operationtype==5)
                    a0101="codeitemdesc_2";
            }
            String sql="";
            String seqnum="1";
            RowSet rset=null;
            rset=dao.search("select seqnum from "+tabname+" where ins_id=(select ins_id from t_wf_task where task_id="+task_id+" )");
            if(rset.next())
                seqnum=rset.getString(1)!=null?rset.getString(1):"";
            
            if(seqnum.length()>0)
            {
                sql=" select "+a0101+" from "+tabname+",t_wf_task_objlink two where "+tabname+".seqnum=two.seqnum and "+tabname+".ins_id=two.ins_id "
                          +" and two.task_id="+task_id+" and two.tab_id="+tab_id +" and ( "+Sql_switcher.isnull("two.state","0")+"<>3 )  and ("+Sql_switcher.isnull("two.special_node","0")+"=0  or ( "+Sql_switcher.isnull("two.special_node","0")+"=1 and (lower(two.username)='"+this.userView.getUserName().toLowerCase()+"' or lower(two.username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )";
            }
            else
            {
                sql=" select "+a0101+" from "+tabname+"  where ins_id=(select ins_id from t_wf_task where task_id="+task_id+" )";
            }  
            rset=dao.search(sql);           
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
            if(i>4)
            {
                if(seqnum.length()>0)
                    sql="select count(*)  from t_wf_task_objlink   where task_id="+task_id+" and tab_id="+tab_id +"  and ( "+Sql_switcher.isnull("state","0")+"<>3 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )";
                else
                    sql=" select count(*) from "+tabname+"  where ins_id=(select ins_id from t_wf_task where task_id="+task_id+" )";  
                rset=dao.search(sql);
                    if(rset.next())
                        nmax=rset.getInt(1);
            }
            else
                nmax=i;
            stopic.append(",");
            stopic.append(ResourceFactory.getProperty("hmuster.label.total"));      
            stopic.append(nmax);
            if(type!=null&&("10".equals(type)|| "11".equals(type)))
                stopic.append("条记录)");
            else
                stopic.append("人)");
            if(rset!=null)
                rset.close();
        }
        catch(Exception ex)
        {
            return stopic.toString();
        }
        return stopic.toString();
    }
    
    
    public String getRecordBusiTopicByState(int task_id,int state,String tabname,ContentDAO dao,int operationtype,String type)
    {
        int nmax=0;
        StringBuffer stopic=new StringBuffer();
        stopic.append("(");
        try
        { 
            StringBuffer strsql=new StringBuffer(); 
            String a0101="a0101_1";
            RecordVo vo=new RecordVo(tabname);
            if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
            {
                    a0101="a0101_2";
            }
            
            if(type!=null&&("10".equals(type)|| "11".equals(type)))
            {
                a0101="codeitemdesc_1";
                if(operationtype==5)
                    a0101="codeitemdesc_2";
            }
            String strWhere=" where "; 
            String strWhere2="";
            if(state==0)
                strWhere2=" and (state is null or  state=0) ";
            else
                strWhere2=" and state="+state+" "; 
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
             
            if(type!=null&&("10".equals(type)|| "11".equals(type)))
                stopic.append("条记录 ");
            else
                stopic.append("人"); 
            if(state==3)
                stopic.append(" 被撤销");
            stopic.append(")");
        }
        catch(Exception ex)
        {
            return stopic.toString();
        }
        return stopic.toString();
    }

    /**
     * @throws GeneralException  
     * @Title: searchMyapplyTask 
     * @Description: TODO
     * @param queryType
     * @param queryMethod
     * @param days
     * @param startDate
     * @param endDate
     * @param bsFlag
     * @param taskFormName
     * @return Object   
     * @throws 
    */
    public ArrayList searchMyapplyTask(String query_type, String query_method, String days, String start_date, String end_date, String bsFlag, String taskFormName) throws GeneralException {
        // TODO Auto-generated method stub
        if(query_method==null || query_method.trim().length()==0)
            query_method="1";
        if(query_type==null || query_type.trim().length()==0)
            query_type="1";
        StringBuffer strsql=new StringBuffer();
        ArrayList datalist = new ArrayList();
        RowSet rs= null;
        try
        {
            ContentDAO dao = new ContentDAO(this.conn);
            /**查询任务实例*/
            String format_str="yyyy-MM-dd HH:mm";
            if(Sql_switcher.searchDbServer()==Constant.ORACEL)
                format_str="yyyy-MM-dd hh24:mi";
            strsql.append("select U.ins_id,T.task_topic name,U.tabid,U.actorname fullname,a0101, task_state finished ,"+Sql_switcher.dateToChar("U.start_date",format_str)+" as ins_start_date,"+Sql_switcher.dateToChar("T.end_date",format_str)+" as ins_end_date,T.actorname,T.task_id from t_wf_task T,t_wf_instance U,template_table tt ");
            strsql.append(" where T.ins_id=U.ins_id and  task_topic not like '%共0人%' and  task_topic not like '%共0条%' ");
            if(taskFormName.length()>0)
            {
                strsql.append(" and tt.name like '%"+taskFormName+"%'");
            }
            strsql.append("  and  U.tabid=tt.tabid");
            TemplateTableParamBo tp=new TemplateTableParamBo(this.conn); 
            String tabids=tp.getAllDefineKqTabs(0); 
            if(tabids.length()==0)
                tabids+=",-1000"; 
            
            if("0".equals(query_method))
            {
                strsql.append(" and task_type='2' and finished='2' and ( task_state='3' or task_state='6' ) ");
            } 
            else if("1".equals(query_method)) //&&!sp_flag.equals("1"))//结束
            {
                strsql.append(" and ( T.task_type='9' and  T.task_state='5' )");//Finished T.task_type='2' and T.flag=1//task_type='2' and task_state='5' and state<>'07'
            }
//            if(sp_flag.equals("1")) //我的申请
                strsql.append(" and (");
                strsql.append(getInsFilterWhereMyapply("1"));
                strsql.append(")"); 
            //1：审批任务 2：加签任务 3：报备任务  4：空任务
            strsql.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"='1'  ");
            //增加时间查询
            if("1".equals(query_type))//最近多少天
            {
                if(days==null|| "".equals(days)){
                }else if(validateNum(days)){
                    String strexpr=Sql_switcher.addDays(Sql_switcher.sqlNow(),"-"+days);//"GetDate()"
                    strsql.append(" and U.start_date>=");
                    strsql.append(strexpr); 
                }
                
            }else{
                if("2".equals(query_type)){
                    strsql.append(" and ( 1=1 ");
                    if(validateDate(start_date))
                        strsql.append(PubFunc.getDateSql(">=","U.start_date",start_date));
                    if(validateDate(end_date))
                        strsql.append(PubFunc.getDateSql("<=","U.start_date",end_date));
                    strsql.append(" )");
                }
            }
            int index=strsql.toString().indexOf("from");
            String sql="";
            if(index!=-1)
                sql=" select ins_id,actorname from t_wf_task where ins_id in (select  U.ins_id "+strsql.substring(index)+") and task_type='2' and  "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and task_state='3' order by ins_id";
            //获得各实例下当前审批人
            HashMap ins_CurrentSpInfo=new HashMap(); // 
            if(sql.length()>0){ 
                RowSet rowSet=dao.search(sql);
                int ins_id=0;
                String actorname="";
                while(rowSet.next())
                {
                    if(ins_id==0)
                        ins_id=rowSet.getInt("ins_id");
                    if(ins_id==rowSet.getInt("ins_id"))
                        actorname+=","+rowSet.getString("actorname");
                    else
                    {
                        ins_CurrentSpInfo.put(String.valueOf(ins_id),actorname.toString().substring(1));
                        ins_id=rowSet.getInt("ins_id");
                        actorname=","+rowSet.getString("actorname");
                    } 
            }
                if(ins_id!=0&&actorname.length()>0)
                    ins_CurrentSpInfo.put(String.valueOf(ins_id),actorname.toString().substring(1));
            }
            String order_sql="";
            if("0".equals(query_method))  //运行中
                order_sql=" order by U.start_date DESC";
            if("1".equals(query_method))//结束
                order_sql=" order by T.end_date DESC";
            strsql.append(order_sql);
            rs=dao.search(strsql.toString());
            HashMap map = null;
            HashMap tableNameMap=new HashMap();
            while(rs.next()){
                map=new HashMap();
                String tabid=rs.getString("tabid");
                String ins_id=rs.getString("ins_id");
                String tabName="";
                if(tableNameMap.get(tabid)==null){
                    tabName=findTabName(tabid);
                    tableNameMap.put(tabid,tabName);
                }else{
                    tabName=(String)tableNameMap.get(tabid);
                }
                String task_topic=tabName;
                if(ins_CurrentSpInfo.get(ins_id)!=null)
                    map.put("curr_user",(String)ins_CurrentSpInfo.get(ins_id));
                else
                    map.put("curr_user","");
                map.put("title",task_topic);
                map.put("ins_id",rs.getString("ins_id"));
                map.put("tabid",rs.getString("tabid"));
                map.put("user",rs.getString("fullname"));
                map.put("start_time", rs.getString("ins_start_date"));
                map.put("time", rs.getString("ins_start_date")+"~"+(rs.getString("ins_end_date")==null?" ":rs.getString("ins_end_date")));
                map.put("task_id",rs.getString("task_id"));
                if("5".equals(rs.getString("finished"))){
                    map.put("task_state", "结束");
                }else{
                    map.put("task_state", "等待");
                }
                datalist.add(map);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        
        finally{
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return datalist;
    }

    /** 
     * @Title: getInsFilterWhereMyapply 
     * @Description: TODO
     * @param string
     * @return Object   
     * @throws 
    */
    private Object getInsFilterWhereMyapply(String sp_flag) {
        StringBuffer strwhere=new StringBuffer();
        /**用户号*/
        String dbpre=this.userView.getDbname(); //库前缀
        String userid=dbpre+this.userView.getA0100();//人员编号
        
        /**如果为空，设置一个不可能出现的用户，为了少写几行代码*/
        if(userid==null||userid.length()==0)
            userid="-1";
        if("1".equalsIgnoreCase(sp_flag))
        {
        /**人员列表*/
            strwhere.append( " ( upper(U.actorid) in ('");
            strwhere.append(userid.toUpperCase());
            strwhere.append("','");
            strwhere.append(this.userView.getUserName().toUpperCase());
            strwhere.append("'))");
        }
        else if("3".equalsIgnoreCase(sp_flag))//已批，列出自己参与的流程实例中的任务
        {
            strwhere.append(" U.ins_id in (select ins_id from t_wf_task where  ");
            strwhere.append( " upper(actorid) in ('");
            strwhere.append(userid.toUpperCase());
            strwhere.append("','");
            strwhere.append(this.userView.getUserName().toUpperCase());
            strwhere.append("') ");
            
            if(this.userView.getRolelist().size()>0)
            {
                strwhere.append(" or  ( actor_type=2 and  upper(actorid) in ( ");
                String str="";
                for(int i=0;i<this.userView.getRolelist().size();i++)
                {
                    str+=",'"+(String)this.userView.getRolelist().get(i)+"'";
                }
                strwhere.append(str.substring(1));
                strwhere.append(" ) )");
            }
            strwhere.append(")");
        }
        return strwhere.toString();
    }

    /**
     * @param userflag 标识是自助用户还是业务用户 0：业务用户 4：自助用户
     * @throws GeneralException 
     * @Title: searchTemplateList 
     * @Description: TODO
     * @return ArrayList   
     * @throws 
    */
    public ArrayList searchTemplateList(String userflag) throws GeneralException {
        UserView RealuserView = null;
        if("4".equals(userflag)){
          //业务用户关联自助用户 按自助用户走
            if(this.userView.getS_userName()!=null&&this.userView.getS_userName().length()>0&&this.userView.getStatus()==0&&this.userView.getBosflag()!=null){
                RealuserView=new UserView(this.userView.getS_userName(), this.userView.getS_pwd(), this.conn);
                try {
                    RealuserView.canLogin();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }else{
            RealuserView=this.userView;
        }
        RowSet rs =null;
        ArrayList dataList = new ArrayList();
        TemplateTableParamBo tp=new TemplateTableParamBo(this.conn); 
        String tabids=tp.getAllDefineKqTabs(0); 
        if(tabids.length()==0)
            tabids+=",-1000"; 
        try {
            StringBuffer querysql = new StringBuffer();
            ContentDAO dao = new ContentDAO(this.conn);
            String staitic_="static";
            if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
            	staitic_="static_o";
            }
            querysql.append("select a.TabId,a.Name,a.operationcode,b.operationname,b."+staitic_+" "+staitic_+" from template_table a ,operation b where a.operationcode=b.operationcode");// order by static");
            if("4".equals(userflag)){
                querysql.append(" and b.operationtype <> 0 ");
            }
            querysql.append(" order by "+staitic_+" ");
            rs=dao.search(querysql.toString());
            HashMap map = null;
            HashMap tempMap = new HashMap();
            while(rs.next()){
                if(tempMap.get(rs.getString("operationcode"))!=null)
                    continue;
                else{
                    if(rs.getString(staitic_)!=null&& "1".equals(rs.getString(staitic_)))//人事异动模版权限
                        if (!RealuserView.isHaveResource(IResourceConstant.RSBD, rs.getString("tabid")))
                          continue;
                     if(rs.getString(staitic_)!=null&& "2".equals(rs.getString(staitic_)))//薪资管理模版权限
                            if (!RealuserView.isHaveResource(IResourceConstant.GZBD, rs.getString("tabid")))
                              continue;
                     if(rs.getString(staitic_)!=null&& "8".equals(rs.getString(staitic_)))//保险管理模版权限
                            if (!RealuserView.isHaveResource(IResourceConstant.INS_BD, rs.getString("tabid")))
                              continue;
                     if(rs.getString(staitic_)!=null&& "3".equals(rs.getString(staitic_)))//警衔管理模版权限
                            if (!RealuserView.isHaveResource(IResourceConstant.PSORGANS, rs.getString("tabid")))
                              continue;
                     if(rs.getString(staitic_)!=null&& "4".equals(rs.getString(staitic_)))//法官管理模版权限
                            if (!RealuserView.isHaveResource(IResourceConstant.PSORGANS_FG, rs.getString("tabid")))
                              continue;
                     if(rs.getString(staitic_)!=null&& "5".equals(rs.getString(staitic_)))//关衔管理模版权限
                            if (!RealuserView.isHaveResource(IResourceConstant.PSORGANS_GX, rs.getString("tabid")))
                              continue;
                     if(rs.getString(staitic_)!=null&& "6".equals(rs.getString(staitic_)))//检察官管理模版权限
                            if (!RealuserView.isHaveResource(IResourceConstant.PSORGANS_JCG, rs.getString("tabid")))
                              continue;  
                     if(rs.getString(staitic_)!=null&& "10".equals(rs.getString(staitic_)))//单位管理模版权限
                         if (!RealuserView.isHaveResource(IResourceConstant.ORG_BD, rs.getString("tabid")))
                           continue;
                     if(rs.getString(staitic_)!=null&& "11".equals(rs.getString(staitic_)))//岗位管理模版权限
                         if (!RealuserView.isHaveResource(IResourceConstant.POS_BD, rs.getString("tabid")))
                           continue;
                     if("4".equals(userflag)){
                         if(rs.getString(staitic_)!=null&&("10".equals(rs.getString(staitic_))|| "11".equals(rs.getString(staitic_)))){//是自助用户 没有权限操作单位和部门
                             continue;
                         }
                     }
                     if("4".equals(userflag)){
                         String tabid=rs.getString("tabid");
                         if((tabids+",").indexOf(","+tabid+",")!=-1) //不包含考勤业务申请信息,自助用户不包含
                         {
                             continue;
                         } 
                     }
                     
                     String name ="";
//                     if(rs.getString("static").equals("1")){
//                         name=rs.getString("operationname");///+"(人事异动)";
//                     }else if(rs.getString("static").equals("2")){
//                         name=rs.getString("operationname");//+"(薪资变动)";
//                     }else if(rs.getString("static").equals("8")){
//                         name=rs.getString("operationname")+"(保险业务)";
//                     }else if(rs.getString("static").equals("3")){
//                         name=rs.getString("operationname")+"(警衔管理)";
//                     }else if(rs.getString("static").equals("4")){
//                         name=rs.getString("operationname")+"(法官等级)";
//                     }else if(rs.getString("static").equals("5")){
//                         name=rs.getString("operationname")+"(关衔管理)";
//                     }else if(rs.getString("static").equals("6")){
//                         name=rs.getString("operationname")+"(检察官管理)";
//                     }else if(rs.getString("static").equals("10")){
//                         name=rs.getString("operationname")+"(单位业务)";
//                     }else if(rs.getString("static").equals("11")){
//                         name=rs.getString("operationname")+"(岗位业务)";
//                     }else{
                         name=rs.getString("operationname");
//                     }
                     map = new HashMap();
                     map.put("id", rs.getString("operationcode"));
                     tempMap.put(rs.getString("operationcode"), rs.getString("operationcode"));
                     map.put("categories", name);
                     dataList.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        finally{
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataList;
    }

    /**
     * @param string 
     * @throws GeneralException  
     * @Title: searchNextTemplateList 
     * @Description: TODO
     * @param operationcode
     * @return ArrayList   
     * @throws 
    */
    public ArrayList searchNextTemplateList(String operationcode, String userflag) throws GeneralException {
        UserView RealuserView = null;
        if("4".equals(userflag)){
          //业务用户关联自助用户 按自助用户走
            if(this.userView.getS_userName()!=null&&this.userView.getS_userName().length()>0&&this.userView.getStatus()==0&&this.userView.getBosflag()!=null){
                RealuserView=new UserView(this.userView.getS_userName(), this.userView.getS_pwd(), this.conn);
                try {
                    RealuserView.canLogin();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }else{
            RealuserView=this.userView;
        }
        
        RowSet rs =null;
        ArrayList dataList = new ArrayList();
        TemplateTableParamBo tp=new TemplateTableParamBo(this.conn); 
        String tabids=tp.getAllDefineKqTabs(0); 
        if(tabids.length()==0)
            tabids+=",-1000"; 
        try {
            StringBuffer querysql = new StringBuffer();
            ContentDAO dao = new ContentDAO(this.conn);
            String staitic_="static";
            if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
            	staitic_="static_o";
            }
            querysql.append("select a.TabId,a.Name,a.operationcode,b.operationname,b."+staitic_+" "+staitic_+" from template_table a ,operation b where a.operationcode=b.operationcode ");
            if(!"".equals(operationcode)){
                querysql.append(" and a.operationcode = '"+operationcode+"'");
                
            }else{
                return new ArrayList();//不会出现这种情况，防止报错
            }
            if("4".equals(userflag)){//自助用户不能看到调入型的模版
                querysql.append("and b.operationtype <> 0 ");
            }
            querysql.append(" order by "+staitic_+"");
            rs=dao.search(querysql.toString());
            HashMap map = null;
            Set tabidSet = new HashSet();
            while(rs.next()){
                    if(rs.getString(staitic_)!=null&& "1".equals(rs.getString(staitic_)))//人事异动模版权限
                        if (!RealuserView.isHaveResource(IResourceConstant.RSBD, rs.getString("tabid")))
                          continue;
                     if(rs.getString(staitic_)!=null&& "2".equals(rs.getString(staitic_)))//薪资管理模版权限
                            if (!RealuserView.isHaveResource(IResourceConstant.GZBD, rs.getString("tabid")))
                              continue;
                     if(rs.getString(staitic_)!=null&& "8".equals(rs.getString(staitic_)))//保险管理模版权限
                            if (!RealuserView.isHaveResource(IResourceConstant.INS_BD, rs.getString("tabid")))
                              continue;
                     if(rs.getString(staitic_)!=null&& "3".equals(rs.getString(staitic_)))//警衔管理模版权限
                            if (!RealuserView.isHaveResource(IResourceConstant.PSORGANS, rs.getString("tabid")))
                              continue;
                     if(rs.getString(staitic_)!=null&& "4".equals(rs.getString(staitic_)))//法官管理模版权限
                            if (!RealuserView.isHaveResource(IResourceConstant.PSORGANS_FG, rs.getString("tabid")))
                              continue;
                     if(rs.getString(staitic_)!=null&& "5".equals(rs.getString(staitic_)))//关衔管理模版权限
                            if (!RealuserView.isHaveResource(IResourceConstant.PSORGANS_GX, rs.getString("tabid")))
                              continue;
                     if(rs.getString(staitic_)!=null&& "6".equals(rs.getString(staitic_)))//检察官管理模版权限
                            if (!RealuserView.isHaveResource(IResourceConstant.PSORGANS_JCG, rs.getString("tabid")))
                              continue;  
                     if(rs.getString(staitic_)!=null&& "10".equals(rs.getString(staitic_)))//单位管理模版权限
                         if (!RealuserView.isHaveResource(IResourceConstant.ORG_BD, rs.getString("tabid")))
                           continue;
                     if(rs.getString(staitic_)!=null&& "11".equals(rs.getString(staitic_)))//岗位管理模版权限
                         if (!RealuserView.isHaveResource(IResourceConstant.POS_BD, rs.getString("tabid")))
                           continue; 
                     if("4".equals(userflag)){
                         if(rs.getString(staitic_)!=null&&("10".equals(rs.getString(staitic_))|| "11".equals(rs.getString(staitic_)))){//是自助用户 没有权限操作单位和部门
                             continue;
                         }
                     }
                     if("4".equals(userflag)){
                         String tabid=rs.getString("tabid");
                         if((tabids+",").indexOf(","+tabid+",")!=-1) //不包含考勤业务申请信息,自助用户不包含
                         {
                             continue;
                         } 
                     }
                     map = new HashMap();
                     tabidSet.add(""+rs.getInt("tabid"));
                     map.put("tabid", rs.getString("tabid"));
                     map.put("name", rs.getString("tabid")+":"+rs.getString("name"));
                     map.put("task_id", "0");
                     map.put("ins_id", "0");
                     dataList.add(map);
                }
            HashMap tabMap=new HashMap();
            for(Iterator t=tabidSet.iterator();t.hasNext();)
            {
                String temptabid=(String)t.next();
                HashMap tempmap =getInfortype(temptabid);
                tabMap.put(temptabid,tempmap);
            } 
            for(int i=0;i<dataList.size();i++)
            {
                HashMap linshimap=(HashMap)dataList.get(i);
                String temptabid=(String)linshimap.get("tabid");
                HashMap tempmap=(HashMap)tabMap.get(temptabid);
                linshimap.put("infor_type",(String)tempmap.get("infor_type"));
                
                linshimap.put("factorFlag",(String)tempmap.get("factorFlag"));
                linshimap.put("operationtype",(String)tempmap.get("operationtype"));
            } 
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        finally{
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataList;
    
    }
    /**
     * 
     * @Title: getInfortype 
     * @Description: 获得模版的操作对象 人员、单位（部门）、岗位
     * @param tabid
     * @return String
     * @throws GeneralException    
     */
    public HashMap getInfortype(String tabid) throws GeneralException{
        HashMap map = new HashMap();
        try {
            ContentDAO dao = new ContentDAO(this.conn);
        //    RecordVo table_vo = new RecordVo("template_table");
       //     table_vo.setInt("tabid", Integer.parseInt(tabid));
       //     table_vo=dao.findByPrimaryKey(table_vo);
            RecordVo table_vo=TemplateUtilBo.readTemplate(Integer.parseInt(tabid),this.conn);
            int tempstatic = table_vo.getInt("static");
            String factor=table_vo.getString("factor");
            if(SystemConfig.getPropertyValue("promptIndex_template")!=null&& "false".equalsIgnoreCase(SystemConfig.getPropertyValue("promptIndex_template").trim())){
                map.put("factorFlag", "false");
            }else{
                if(factor==null|| "".equals(factor)){
                    map.put("factorFlag", "false");
                }else{
                    map.put("factorFlag", "true");
                }
            }
            if (tempstatic == 10) {
                map.put("infor_type", "2");
            } else if (tempstatic == 11) {
                map.put("infor_type", "3");
            } else {
                map.put("infor_type", "1");
            }
            String Operationtype=findOperationType(tabid);
            map.put("operationtype", Operationtype);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return map;
        
    }

    /**
     * @throws GeneralException  
     * @Title: ImpObjToTemplet 
     * @Description: 向临时表中导入相应的人员、单位、岗位的相关数据
     * @param allObjectId void   
     * @throws 
    */
    public void ImpObjToTemplet(String allObjectId,String tabid) throws GeneralException {
        HashMap hm=new HashMap();
        ArrayList a0100list=new ArrayList();
        HashMap nameMap = new HashMap();
        String indexnames = ""; 
        HashMap indexMap = getIndexMap( this.templateTableBo, tabid,nameMap);
        String[] objectIdArray=null;
        try{
            if(allObjectId!=null&&allObjectId.trim().length()>0){
                objectIdArray=allObjectId.split(",");
            }
            int infor_type = this.templateTableBo.getInfor_type();
            if(objectIdArray!=null){
                for(int i=0;i<objectIdArray.length;i++){
                    String obj_id=objectIdArray[i];
                    if(obj_id==null|| "".equals(obj_id))
                        continue;
                    if("selectall".equalsIgnoreCase(obj_id))
                        continue;
                    if(infor_type==1){//如果是人员
                        String[] tempIdArray =obj_id.split("`");
                        String pre=tempIdArray[0];
                        /**按人员库进行分类*/
                        if(!hm.containsKey(pre)){//hm包含所有的人员库
                            a0100list=new ArrayList();
                        }
                        else{
                            a0100list=(ArrayList)hm.get(pre);
                        }
                        if(indexMap!=null){
                            if(indexMap.get(obj_id.toLowerCase())!=null){
                                
                            }else{
                                if(nameMap!=null&&nameMap.get(obj_id.toLowerCase())!=null)
                                indexnames +=nameMap.get(obj_id.toLowerCase())+",";
                                continue;
                            }
                        }
                        a0100list.add(tempIdArray[1]);
                        hm.put(pre,a0100list);
                    }
                    else{///如果不是人员
                        if(a0100list==null)
                            a0100list=new ArrayList();
                        a0100list.add(obj_id);
                    }
                } //for objlist loop end.
                
                if(infor_type==2)
                    hm.put("B",a0100list);
                if(infor_type==3)
                    hm.put("K",a0100list);
            }
    
          //开始一个人员库一个人员库地导入
            Iterator iterator=hm.entrySet().iterator();
            ArrayList tempList=null;
            while(iterator.hasNext())
            {
                Entry entry=(Entry)iterator.next();
                String pre=entry.getKey().toString();
                a0100list =(ArrayList)entry.getValue();
                if(a0100list.size()==0)
                    continue;
                
                if(a0100list.size()<=500)
                    this.templateTableBo.impDataFromArchive(a0100list,pre);
                else{
                    int size=a0100list.size();
                    int n=size/500+1;
                    for(int i=0;i<n;i++){
                        tempList=new ArrayList();
                        for(int j=i*500;j<(i+1)*500;j++){
                            if(j<a0100list.size())
                                tempList.add((String)a0100list.get(j));
                            else
                                break;
                        }
                        if(tempList.size()>0)
                            this.templateTableBo.impDataFromArchive(tempList,pre);
                    }
                }
            }
       }catch(Exception e){
           e.printStackTrace();
           throw GeneralExceptionHandler.Handle(e);
       }
    }
    /**
     * @Title: getIndexMap 
     * @Description:检索条件的处理
     * @param tablebo
     * @param tabid
     * @param nameMap
     * @return HashMap   
     * @throws
     */
    public HashMap getIndexMap(TemplateTableBo tablebo,String tabid,HashMap nameMap){
        //检索条件map
        HashMap indexMap = null;
        RowSet rs = null;
        try{
            String factor=tablebo.getFactor();
            if(factor==null||factor.trim().length()==0)
                return indexMap;
            if("1".equals(tablebo.getFilter_by_factor())){//手工选人、条件选人不按检索条件过滤, 0不过滤(默认值),1过滤
                indexMap = new HashMap();
            ArrayList dblist=new ArrayList();
            ContentDAO dao=new ContentDAO(this.conn);
            String init_base=tablebo.getInit_base();
        
            if(this.userView.isSuper_admin())
            {
                rs=dao.search("select * from dbname");
                while(rs.next())
                    dblist.add(rs.getString("pre"));
            }
            else
            {
                if(init_base!=null&&init_base.trim().length()>0)
                {
                    if(this.userView.getDbpriv().toString().toLowerCase().indexOf(","+init_base.toLowerCase()+",")==-1)
                        return indexMap;
                    dblist.add(init_base);
                }
                else
                {
                    String[] temps=this.userView.getDbpriv().toString().split(",");
                    for(int i=0;i<temps.length;i++)
                    {
                        if(temps[i].trim().length()>0)
                            dblist.add(temps[i]);
                    }
                }   
            }
            
            
            ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
            for(int e=0;e<dblist.size();e++)
            {
                String BasePre=(String)dblist.get(e);
                StringBuffer sql=new StringBuffer();
 
                sql.append("select a0100 from ");
                int infoGroup = 0; // forPerson 人员
                int varType = 8; // logic                               
                String whereIN=InfoUtils.getWhereINSql(this.userView,BasePre);
                whereIN="select a0100 "+whereIN;    
                String no_priv_ctrl=tablebo.getNo_priv_ctrl(); //手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按
                if("1".equals(no_priv_ctrl))
                    whereIN="";
                YksjParser yp = new YksjParser(this.userView ,alUsedFields,
                        YksjParser.forSearch, varType, infoGroup, "Ht", BasePre);
                YearMonthCount ymc=null;    
                yp.setSupportVar(true,"select  *  from   midvariable where nflag=0 and templetid= "+tabid);  //支持临时变量
                yp.run_Where(factor, ymc,"","", dao, whereIN,this.conn,"A", null);
                String tempTableName = yp.getTempTableName();
                sql.append(tempTableName);
                sql.append(" where " + yp.getSQL());
                
                rs=dao.search(sql.toString());
                while(rs.next()){
                    indexMap.put(BasePre.toLowerCase()+"`"+rs.getString("a0100"), BasePre.toLowerCase()+"`"+rs.getString("a0100"));
                }
                sql.setLength(0);
                sql.append(" select a0100,a0101 from "+BasePre+"a01 " );
                rs=dao.search(sql.toString());
                while(rs.next()){
                    nameMap.put(BasePre.toLowerCase()+"`"+rs.getString("a0100"), rs.getString("a0101"));
                }
            }
            }
        }catch(Exception e2){
            e2.printStackTrace();
        }
        
        return indexMap;
    }

    /**
     * @throws GeneralException  
     * @Title: ImpObjToTempletByFactor 
     * @Description:根据检索条件，增加人员和减少人员 
     * @param tabid 模版号
     * @param flag  1:清空当前人员,重新引入  2:不清空,引入符合条件的数据
     * return void   
     * @throws 
    */
    public void ImpObjToTempletByFactor(String tabid, String flag) throws GeneralException {
        RowSet rs=null;
        try{
            String no_priv_ctrl=this.templateTableBo.getNo_priv_ctrl(); //手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按
            String intbase=this.templateTableBo.getInit_base();
            String factor=this.templateTableBo.getFactor();
            String init_base=this.templateTableBo.getInit_base();
            ArrayList dblist=new ArrayList();
            ContentDAO dao=new ContentDAO(this.conn);
            if(this.userView.isSuper_admin())
            {
                rs=dao.search("select * from dbname");
                while(rs.next())
                    dblist.add(rs.getString("pre"));
            }
            else
            {
                if(init_base!=null&&init_base.trim().length()>0)
                {
                    if(this.userView.getDbpriv().toString().toLowerCase().indexOf(","+init_base.toLowerCase()+",")==-1)
                        return;
                    dblist.add(init_base);
                }
                else
                {
                    String[] temps=this.userView.getDbpriv().toString().split(",");
                    for(int i=0;i<temps.length;i++)
                    {
                        if(temps[i].trim().length()>0)
                            dblist.add(temps[i]);
                    }
                }   
            }
            
            if("1".equals(flag))
                dao.update("delete from "+this.userView.getUserName()+"templet_"+tabid);
            
            ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
            for(int e=0;e<dblist.size();e++)
            {
                String BasePre=(String)dblist.get(e);
                
                if(intbase!=null&&intbase.trim().length()>0)
                {
                    if(!intbase.equalsIgnoreCase(BasePre))
                        continue;
                }
                
                StringBuffer sql=new StringBuffer();
 
                sql.append("select a0100 from ");
                int infoGroup = 0; // forPerson 人员
                int varType = 8; // logic                               
                String whereIN=InfoUtils.getWhereINSql(this.userView,BasePre);
                whereIN="select a0100 "+whereIN;        
                if("1".equals(no_priv_ctrl))
                    whereIN="";
                YksjParser yp = new YksjParser(this.userView ,alUsedFields,
                        YksjParser.forSearch, varType, infoGroup, "Ht", BasePre);
                YearMonthCount ymc=null;    
                yp.setSupportVar(true,"select  *  from   midvariable where nflag=0 and templetid= "+tabid);  //支持临时变量
                yp.run_Where(factor, ymc,"","", dao, whereIN,this.conn,"A", null);
                String tempTableName = yp.getTempTableName();
                sql.append(tempTableName);
                sql.append(" where " + yp.getSQL());
                
                if("2".equals(flag))
                {
                    sql.append(" and a0100 not in (select a0100 from ");
                    sql.append(this.userView.getUserName()+"templet_"+tabid);
                    sql.append(" where upper(basepre)='");
                    sql.append(BasePre.toUpperCase());
                    sql.append("')");
                }
                
                ArrayList a0100list =new ArrayList();
                rs=dao.search(sql.toString());
                while(rs.next())
                    a0100list.add(rs.getString("a0100"));
                
                if(a0100list.size()==0)
                    continue;
                
                if(a0100list.size()<=500)
                    this.templateTableBo.impDataFromArchive(a0100list,BasePre);
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
                            this.templateTableBo.impDataFromArchive(tempList,BasePre);
                        
                    }
                    
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
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
    }

    /** 
     * @Title: canDoCombine 
     * @Description: TODO
     * @param tabid
     * @param inforType
     * @param operationtype void   
     * @throws 
    */
    public HashMap canDoCombine(String tabid, String infor_type, String operationtype) {
        RowSet rs =null;
        ArrayList orgcodeitemid=new ArrayList();
        HashMap returnMap = new HashMap();
        String table_name =this.userView.getUserName()+"templet_"+tabid;
        String d="",maxstartdate="",msg=""; 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            //是否排除流程中进行合并，划转，撤销？
            rs=dao.search(" select * from " +table_name+" where submitflag=1");
            while(rs.next()){
                    if(infor_type!=null&& "2".equals(infor_type))
                        orgcodeitemid.add(rs.getString("b0110"));
                    else
                        orgcodeitemid.add(rs.getString("e01a1"));
            }
            DbWizard dbwizard=new DbWizard(this.conn);
            TemplateListBo bo=new TemplateListBo(tabid,this.conn,this.userView);
            ArrayList templateSetList = bo.getAllCell();
            boolean flag1=false;
            boolean flag2=false;
            boolean flag3=false;
            for(int j=0;j<templateSetList.size();j++){
                LazyDynaBean abean = (LazyDynaBean)templateSetList.get(j);
                if(abean!=null&& "start_date_2".equalsIgnoreCase(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim()))
                    flag1=true;
                if(abean!=null&& "codeitemdesc_2".equalsIgnoreCase(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim()))
                    flag2=true;
                if(abean!=null&& "parentid_2".equalsIgnoreCase(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim()))
                    flag3=true;
            }
            if(!flag1||!dbwizard.isExistField(table_name, "start_date_2",false)){
                msg="模板中不存在变化后生效日期!\\n";
            }
            if("8".equals(operationtype)&&(!flag2||!dbwizard.isExistField(table_name, "codeitemdesc_2",false))){
                msg+="模板中不存在变化后组织单元名称!";
            }
            if("3".equals(infor_type)&& "9".equals(operationtype)&&(!flag3||!dbwizard.isExistField(table_name, "parentid_2",false))){
                msg+="模板中不存在变化后上级组织单元名称!";
            }
            if("2".equals(infor_type)||("3".equals(infor_type)&& "8".equals(operationtype))){
                ArrayList transferorglist =new ArrayList();
                rs=dao.search("select * from "+table_name+" where submitflag=1");
                String selectall=",";
                String selectcomb = "";
                while(rs.next()){ 
                        if(infor_type!=null&& "2".equals(infor_type)){
                            transferorglist.add(rs.getString("b0110"));
                            selectall+=rs.getString("b0110")+",";
                            if(rs.getString("b0110").equals(rs.getString("to_id"))){
                                selectcomb+=rs.getString("b0110")+",";
                            }
                        }
                        else{
                            transferorglist.add(rs.getString("e01a1"));
                            selectall+=rs.getString("e01a1")+",";
                            if(rs.getString("e01a1").equals(rs.getString("to_id"))){
                                selectcomb+=rs.getString("e01a1")+",";
                        } 
                    }
                }
                //已经存在被合并项
                if(selectcomb.length()>0&& "".equals(msg)&& "8".equals(operationtype)){
                    msg="选项中存在已合并后的编码的记录,不能再次进行合并操作！";
                }
                if(selectcomb.length()>0&& "".equals(msg)&& "9".equals(operationtype)){
                    msg="选项中存在已划转后的编码的记录,不能再次进行划转操作！";
                }
                //合并项之间不能存在上下级关系
                int size1 = transferorglist.size();
                transferorglist =getLazyDynaBeanToRecordVo(transferorglist,"0");
                if("2".equals(infor_type)&&size1!=transferorglist.size()&& "".equals(msg)){
                    msg="选项中存在上下级关系的记录,请不要选择有上下级关系的记录！";
                }
                //单位不能和部门合并
                if("8".equals(operationtype)&&transferorglist.size()>1){
                    
                    ArrayList trans = getGradeRecord2(transferorglist);
                    if(trans.size()>0&& "".equals(msg)){
                        msg="选项中存在单位和部门,请不要同时选择单位和部门！";
                    }
                }
                //合并 必须是同级之间的合并
                if("8".equals(operationtype)&&transferorglist.size()>1){
                    
                    transferorglist = getGradeRecord(transferorglist);
                    if(transferorglist.size()>0&& "".equals(msg)){
                        msg="选项中上一级节点必须相同,请不要选择上一级节点不同的记录！";
                    }
                }
            }
            
            if("8".equals(operationtype)&&orgcodeitemid.size()==1)
            {
                msg="单个机构不允许执行合并操作!";
            }
            
            if(orgcodeitemid.size()>0){
                for(int i=0;i<orgcodeitemid.size();i++){
                    String sql = "select start_date from organization where codeitemid='"+(String)orgcodeitemid.get(i)+"'";
                    rs = dao.search(sql);
                    if(rs.next()){
                        java.sql.Date temp = rs.getDate("start_date");
                        d = sdf.format(temp);
                    }
                        if("".equals(maxstartdate)){
                            maxstartdate=d;
                        }else{
                            Date d1=sdf.parse(d);
                            Date d2=sdf.parse(maxstartdate);
                            if(d1.compareTo(d2)>0)
                                maxstartdate=d;
                        }
                    if("".equals(msg))
                    msg="ok";
                }
            }else{
                if("".equals(msg))
                    msg="equals";
            }
            
        }catch(Exception e){
            e.printStackTrace();
        //  throw GeneralExceptionHandler.Handle(e);
        }finally{
            if(rs!=null){
                try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Calendar calendar = Calendar.getInstance();
            String date = sdf.format(calendar.getTime());
            if(date.equalsIgnoreCase(maxstartdate)){
                msg ="date";
            }
            returnMap.put("msg",msg);
            returnMap.put("maxstartdate",maxstartdate);
            returnMap.put("table_name",table_name);
        }
        return returnMap;
    }
    
    private ArrayList getLazyDynaBeanToRecordVo(ArrayList transferorglist,String tempflag) throws GeneralException{
        ArrayList list = new ArrayList();
    
        for(int i=0;i<transferorglist.size();i++)
        {
            String codeitemid=(String)transferorglist.get(i);
            StringBuffer strsql = new StringBuffer();//合并单位：存在上下级单位时，不能合并到下级单位。（解决出现断树状态）
            for(int j=0;j<transferorglist.size();j++)
            {
                if(i==j){
                    
                }else{
                 String codeitemid2=(String)transferorglist.get(j);
                 strsql.append(" and codeitemid not like '"+codeitemid2+"%' ");
                }
                
            }
            StringBuffer sql=new StringBuffer();   
            String table="organization";
            sql.append("select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from "+table+"");
            sql.append(" where codeitemid='"+codeitemid+"' "+strsql+"");
            RowSet rs=null;
            ContentDAO dao=new ContentDAO(this.conn);
            RecordVo vo = null;
            try {
                rs=dao.search(sql.toString());
                if(rs.next())
                {
                    vo=new RecordVo("organization");
                    vo.setString("codesetid",rs.getString("codesetid"));
                    vo.setString("codeitemdesc",rs.getString("codeitemdesc"));
                    vo.setString("parentid",rs.getString("parentid"));
                    vo.setString("childid",rs.getString("childid"));
                    vo.setString("codeitemid",rs.getString("codeitemid"));
                    vo.setInt("grade", rs.getInt("grade"));
                }else{
                //  throw GeneralExceptionHandler.Handle(new GeneralException("","虚拟机构不许合并，操作失败！","",""));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(vo==null)
                continue;
            if("0".equals(tempflag)){
                list.add(codeitemid);  
            }else{
                list.add(vo);
            }
            
        }
        return list;
    }
    
    private ArrayList getGradeRecord(ArrayList transferorglist) throws GeneralException{
        ArrayList list = new ArrayList();
        String str ="";
        try {
        for(int i=0;i<transferorglist.size();i++)
        {
            str+= "'"+transferorglist.get(i)+"'"+",";
        }
            str = str.substring(0,str.length()-1);
        String sql = "select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from  organization where codeitemid in ("+str+") and codeitemid=parentid";
        
            RowSet rs=null;
            ContentDAO dao=new ContentDAO(this.conn);
            RecordVo vo = null;
            
                rs=dao.search(sql.toString());
                int i=0;
                while(rs.next())
                {
                    i++;
                }
                if(transferorglist.size()!=i&&i!=0){//存在顶级结点与非顶级结点
                    return transferorglist;
                }else{
                    if(transferorglist.size()==i){
                        return list;
                    }else{
                        sql ="select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from  organization where codeitemid in ("+str+") ";
                        String parentid="";
                        rs=dao.search(sql.toString());
                        int j=0;
                        while(rs.next())
                        {
                            if(("'"+parentid+"'").indexOf("'"+rs.getString("parentid")+"'")==-1){
                                parentid+="'"+rs.getString("parentid")+"'"+",";
                                j++;
                            }
                        }
                        if(j!=1){
                            return transferorglist;
                        }
                    }
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        return list;
    }
    
    private ArrayList getGradeRecord2(ArrayList transferorglist) throws GeneralException{
        ArrayList list = new ArrayList();
        String str ="";
        try {
        for(int i=0;i<transferorglist.size();i++)
        {
            str+= "'"+transferorglist.get(i)+"'"+",";
        }
            str = str.substring(0,str.length()-1);
        String sql = "select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from  organization where codeitemid in ("+str+") ";
        
            RowSet rs=null;
            ContentDAO dao=new ContentDAO(this.conn);
            RecordVo vo = null;
            
                rs=dao.search(sql.toString());
                int i=0;
                String codesetid="";
                while(rs.next())
                {
                    if(("'"+codesetid+"'").indexOf("'"+rs.getString("codesetid")+"'")==-1){
                        codesetid+="'"+rs.getString("codesetid")+"'"+",";
                        i++;
                    }
                    if(i!=1){
                        return transferorglist;
                    }
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        return list;
    }

    /**
     * @return 
     * @throws GeneralException  
     * @Title: initCombine 
     * @Description: TODO
     * @param tableName
     * @param inforType
     * @param tabid void   
     * @throws 
    */
    public HashMap initCombine(String table_name, String infor_type, String tabid) throws GeneralException {
        RowSet rs = null;
        HashMap returnMap = new HashMap();
        String othermsg ="";
        ArrayList codeitemDescList = new ArrayList();
        ArrayList codeitemValueList= new ArrayList();
        try {
            ArrayList transferorglist=new ArrayList();
            
            ContentDAO dao = new ContentDAO(this.conn);
            rs=dao.search(" select * from " +table_name);
            while(rs.next()){
                if(rs.getString("submitflag")!=null&& "1".equals(rs.getString("submitflag").trim())){
                if(infor_type!=null&& "2".equals(infor_type)){
                    transferorglist.add(rs.getString("b0110"));
                }
                else{
                        transferorglist.add(rs.getString("e01a1"));
                    }
                }
            }
            transferorglist = getLazyDynaBeanToRecordVo(transferorglist,"1");
            String firstset = "";
            String firstparentid="";
            String firstgrade="";
            String firstitemid="";
            for (int i = 0; i < transferorglist.size(); i++) {
                RecordVo vo = (RecordVo) transferorglist.get(i);
                if (i == 0){
                    firstset = vo.getString("codesetid");
                    firstparentid=vo.getString("parentid");
                    firstgrade=vo.getString("grade");
                    firstitemid=vo.getString("codeitemid");
                }
            }
            TemplateListBo bo=new TemplateListBo(tabid,this.conn,this.userView);
            ArrayList templateSetList=bo.getAllCell();
            boolean flag1=false;
            boolean flag2=false;
            for(int j=0;j<templateSetList.size();j++){
                LazyDynaBean abean = (LazyDynaBean)templateSetList.get(j);
                if(abean!=null&& "parentid_2".equalsIgnoreCase(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim()))
                    flag1=true;
                
                if(abean!=null&& "codesetid_2".equalsIgnoreCase(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim()))
                    flag2=true;
                
            }
            DbWizard dbwizard=new DbWizard(this.conn);
            
            if(!flag1||!dbwizard.isExistField(table_name, "parentid_2",false)){
                othermsg="模板中不存在变化后上级组织单元名称!\\n";
            }
            if(infor_type!=null&& "2".equals(infor_type)&&(!flag2||!dbwizard.isExistField(table_name, "codesetid_2",false))){
                othermsg+="模板中不存在变化后组织单元类型!";
            }
            
            for(int i=0;i<transferorglist.size();i++){
                RecordVo vo=(RecordVo)transferorglist.get(i);
                codeitemDescList.add( "(" + vo.getString("codeitemid") + ")" + vo.getString("codeitemdesc"));
                codeitemValueList.add(vo.getString("codeitemid"));
            }
            String code = firstparentid;
            if(!firstitemid.equalsIgnoreCase(firstparentid)){
                String first = "1";
                StringBuffer strsql = new StringBuffer();
                strsql.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from organization where parentid='");
                strsql.append(code);
                strsql.append("' and codeitemid<>parentid ");
    
                strsql.append(" order by codeitemid desc");
                
    
                rs = dao.search(strsql.toString());
                while (rs.next()) {
                    first = "0";
                    //this.getFormHM().put("first", first);
                    String chilecode = rs.getString("codesetid");
                    String codeitemid = rs.getString("codeitemid");
                    String corcode = rs.getString("corcode");
                    int grade = rs.getInt("grade");
                    if (chilecode != null) {
                        codeitemDescList.add( ResourceFactory.getProperty("org.orginfo.neworg"));
                        codeitemValueList.add("");
                        break;
                    }
                }
            }else{
                StringBuffer strsql = new StringBuffer();
                strsql.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from organization where parentid=");
                strsql.append("codeitemid");
                strsql.append(" order by codeitemid desc");
                rs = dao.search(strsql.toString());
                while (rs.next()) {
                    String chilecode = rs.getString("codesetid");
                    String codeitemid = rs.getString("codeitemid");
                    String corcode = rs.getString("corcode");
                    int grade = rs.getInt("grade");
                    if (chilecode != null) {
                        codeitemDescList.add(ResourceFactory.getProperty("org.orginfo.neworg"));
                        codeitemValueList.add("");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        finally{
            if(rs!=null){
                try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            returnMap.put("otherMsg", othermsg);
            returnMap.put("codeitemDescList", codeitemDescList);
            returnMap.put("codeitemValueList", codeitemValueList);
        }
        return returnMap;
    }

    /**
     * @throws GeneralException 
     * @param tabid 
     * @param infor_type 
     * @param hm 
     * @param operationtype  
     * @Title: getCombinePageList 
     * @Description: TODO
     * @param backMap void   
     * @throws 
    */
    public void getCombinePageList(HashMap backMap, String operationtype, HashMap hm, String infor_type, String tabid) throws GeneralException {
        String msg = (String)backMap.get("msg");
        if("equals".equals(msg)){
            if("8".equals(operationtype)){
                hm.put("msg", "请选择需要合并的记录！");//等待向资源文件中添加这个信息
            }else if("9".equals(operationtype)){
                hm.put("msg", "请选择需要划转的记录！");//等待向资源文件中添加这个信息
            }
        }else if("date".equals(msg)){
            hm.put("msg", "你选择了有效日期为当日机构,不允许此操作!");//等待向资源文件中添加这个信息
        }else if("ok".equals(msg)){//如果能划转、合并
            hm.put("msg", "ok");
            String table_name=(String)backMap.get("table_name");
            hm.put("table_name", table_name);
            String maxstartdate=(String)backMap.get("maxstartdate");
            hm.put("maxstartdate", maxstartdate);
            if("8".equals(operationtype)){//走合并流程
                HashMap returnMap=this.initCombine(table_name,infor_type,tabid);
                hm.put("otherMsg", (String)returnMap.get("otherMsg"));
                hm.put("codeitemDescList", (ArrayList)returnMap.get("codeitemDescList"));
                hm.put("codeitemValueList", (ArrayList)returnMap.get("codeitemValueList"));
            }
            if("9".equals(operationtype)){//走划转流程
               
            }
        }else{
            if(msg.length()>5){
                hm.put("msg",msg);
                }else{
                    hm.put("msg","检查能否此操作时失败，不允许此操作！");//等待向资源文件中添加这个信息
                }
        }
    }

    /**
     * @throws GeneralException 
     * @param endDate  
     * @Title: combineOrgBussiness 
     * @Description: TODO
     * @param inforType
     * @param tabid
     * @param combinecodeitemid
     * @param endDate void   
     * @throws 
    */
    public void combineOrgBussiness(String infor_type, String table_name, String combinecodeitemid, String tarCodeitemdesc, String end_date) throws GeneralException {
       boolean version = false;
       RowSet rs=null;
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
       
        if (this.userView.getVersion() >= 50) {
            version = true;
        }

        String to_id=combinecodeitemid;
        
        //判断combinecodeitemid是否在组织机构中存在。
        ContentDAO dao = new ContentDAO(this.conn);
        // 判断新的合并后机构编码是否是新机构编码
        boolean flag = true;
        if(combinecodeitemid!=null&&combinecodeitemid.length()>0){
            flag=false;
        }else{
            flag=true;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        String date = sdf.format(calendar.getTime());
        end_date = end_date != null && end_date.length() > 9 ? end_date : date;
        
        tarCodeitemdesc = PubFunc.splitString(tarCodeitemdesc, 50);
        RecordVo vo = new RecordVo(table_name);
        if (flag) {
             IDGenerator idg=new IDGenerator(2,this.conn);
            String a0100=  idg.getId("rsbd.a0100");
            if("2".equals(infor_type)){
                vo.setString("b0110", "B"+a0100);
                vo.setString("to_id", "B"+a0100);
                to_id="B"+a0100;
            }
            if("3".equals(infor_type)){
                vo.setString("e01a1", "B"+a0100);
                vo.setString("to_id", "B"+a0100);
                to_id="B"+a0100;
            }
            vo.setDate("start_date_2",end_date);
            vo.setString("codeitemdesc_2", tarCodeitemdesc);
            vo.setString("codeitemdesc_1", tarCodeitemdesc);
            vo.setInt("submitflag",0);
            vo.setInt("state", 0);
            String seqnum=CreateSequence.getUUID();
            vo.setString("seqnum", seqnum);
            
            try {
                //获得新机构的类型
                //String hmuster_sql = (String)this.getFormHM().get("hmuster_sql");
                ArrayList list  = new ArrayList();
                String b0101="";
                rs=dao.search(" select * from "+table_name+" where submitflag=1");
                if(rs.next()){
                    if("2".equals(infor_type)){
                    b0101= rs.getString("b0110");
                    }
                    if("3".equals(infor_type)){
                    b0101= rs.getString("e01a1");
                    }
                    
                }
                if(b0101!=null&&b0101.length()>0){
                    rs = dao.search("select codesetid,parentid from organization where codeitemid='"+b0101+"' ");
                if(rs.next()){
                    if("2".equals(infor_type))
                        vo.setString("codesetid_2", rs.getString("codesetid"));
                    if(!b0101.equals(rs.getString("parentid"))){
                        vo.setString("parentid_2", rs.getString("parentid"));
                    }else{
                        vo.setString("parentid_2", "");
                    }
                
                }
                }
                dao.addValueObject(vo);

            } catch (Exception e) {
                e.printStackTrace();
                throw GeneralExceptionHandler.Handle(e);
            } finally {
                try {
                   if(rs!=null){
                       rs.close();
                   }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        
        }else{
            try{
            if(infor_type!=null&& "2".equals(infor_type)){
            dao.update(" update "+table_name+" set codeitemdesc_2='"+tarCodeitemdesc+"' where b0110='"+combinecodeitemid+"'");
            }else{
            dao.update(" update "+table_name+" set codeitemdesc_2='"+tarCodeitemdesc+"' where e01a1='"+combinecodeitemid+"'");
            }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        try {
    //      String hmuster_sql = (String)this.getFormHM().get("hmuster_sql");
            ArrayList list  = new ArrayList();
            rs=dao.search(" select * from " +table_name);
            while(rs.next()){
                
                if(rs.getString("submitflag")!=null&& "1".equals(rs.getString("submitflag").trim())){
                    ArrayList transferorglist = new ArrayList();
                    transferorglist.add(to_id);
                    transferorglist.add(java.sql.Date.valueOf(end_date));
                    if(infor_type!=null&& "2".equals(infor_type))
                    transferorglist.add(rs.getString("b0110"));
                    else
                        transferorglist.add(rs.getString("e01a1"));
                list.add(transferorglist);
                }
                
            }
            if("2".equals(infor_type)){
            dao.batchUpdate(" update "+table_name+" set to_id=?,start_date_2=? where b0110=?", list);
            }else if("3".equals(infor_type)){
            dao.batchUpdate(" update "+table_name+" set to_id=?,start_date_2=? where e01a1=?", list);   
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
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
    }

    /**
     * @return  
     * @Title: transferOrgBussiness 
     * @Description: TODO
     * @param inforType
     * @param tablename
     * @param transfercodeitemid
     * @param endDate void   
     * @throws 
    */
    public String transferOrgBussiness(String infor_type, String table_name, String transfercodeitemid, String end_date) {
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

         RowSet rs =null;
         
         String to_id=transfercodeitemid;
             
         ContentDAO dao = new ContentDAO(this.conn);
         // 判断新的合并后机构编码是否是新机构编码
         Calendar calendar = Calendar.getInstance();
         calendar.add(Calendar.DATE, -1);
         String date = sdf.format(calendar.getTime());
         end_date = end_date != null && end_date.length() > 9 ? end_date : date;
         
         
         try {
             //单位不能划转到部门下
             HashMap map = new HashMap();
             if(infor_type!=null&& "2".equals(infor_type)){
                 rs = dao.search("select * from organization ");
                 while(rs.next()){
                     map.put(rs.getString("codeitemid"), rs.getString("codesetid"));
                 }
             }
             ArrayList list  = new ArrayList();
             rs=dao.search(" select * from " +table_name);
             //组织机构划转特殊处理：判断选中的记录和指定划转单位的上下级关系
             //最后判断选择换转到的机构是否在该表中是否存在（直接查询表），不存在直接拉进来
             while(rs.next()){
                 
                 if(rs.getString("submitflag")!=null&& "1".equals(rs.getString("submitflag").trim())){
                     ArrayList transferorglist = new ArrayList();
                     if(infor_type!=null&& "2".equals(infor_type)){
                         String b0110 = rs.getString("b0110");
                         String desc = rs.getString("codeitemdesc_1");
                         //判断上下级关系
                         if(b0110.startsWith(to_id)){
                             if(b0110.length()==to_id.length()){
                                 throw new GeneralException("","选择记录中的\""+desc+"\"不能划转给本身","","");
                             }else if(b0110.length()==to_id.length()+2){
                                 throw new GeneralException("","选择记录中的\""+desc+"\"不能划转给直接上级","","");
                             }
                         }
                         if(to_id.startsWith(b0110)){
                             if(to_id.length()==b0110.length()){
                                 throw new GeneralException("","选择记录中的\""+desc+"\"不能划转给本身","","");
                             }else{
                                 throw new GeneralException("","选择记录中的\""+desc+"\"不能划转给自己的下级","","");
                             }
                         }
                         if(map!=null&&map.get(b0110)!=null&& "UN".equals(map.get(b0110))&&map.get(to_id)!=null&& "UM".equals(map.get(to_id))){
                             throw new GeneralException("","选择记录中的\""+desc+"\"是单位不能划转给部门","","");
                         }
                     }
                     
                         transferorglist.add(to_id);
                     transferorglist.add(java.sql.Date.valueOf(end_date));
                     if(infor_type!=null&& "2".equals(infor_type))
                     transferorglist.add(rs.getString("b0110"));
                     else
                         transferorglist.add(rs.getString("e01a1"));
                 list.add(transferorglist);
                 }
                 
             }
             if("2".equals(infor_type)){
             dao.batchUpdate(" update "+table_name+" set to_id=?,start_date_2=? where b0110=?", list);
             //判断选择换转到的机构是否在该表中是否存在（直接查询表），不存在直接拉进来
             rs=dao.search(" select * from "+table_name+" where b0110='"+to_id+"'");
             if(rs.next()){
             dao.update(" update "+table_name+" set to_id='"+to_id+"' where b0110='"+to_id+"' ");
             }else{
                 ArrayList a0100s= new ArrayList();
                 a0100s.add(to_id);
                 String tabid = table_name.substring(table_name.lastIndexOf("_")+1,table_name.length());
                 TemplateTableBo tablebo=new TemplateTableBo(this.conn,Integer.parseInt(tabid),this.userView);
                 tablebo.impDataFromArchive(a0100s,"B");
                 dao.update(" update "+table_name+" set to_id='"+to_id+"' where b0110='"+to_id+"' ");
             }
             dao.update(" update "+table_name+" set start_date_2="+Sql_switcher.dateValue(end_date)+" where b0110='"+to_id+"' ");
             }else if("3".equals(infor_type)){
             dao.batchUpdate(" update "+table_name+" set parentid_2=?,start_date_2=? where e01a1=?", list);  
             
             }
             
         } catch (Exception e) {
             e.printStackTrace();
             String message=e.toString();
             int index_i=message.indexOf("description:");
             message=message.substring(index_i+12);
             return message;
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
         return "saveOk";
    }

    /**
     * @throws GeneralException  
     * @Title: searchClocckBusiness 
     * @Description:  查询考勤申请的模版
     * @return HashMap   
     * @throws 
    */
    public HashMap searchClocckBusiness() throws GeneralException {
        ArrayList list=new ArrayList();
        HashMap kqMap = new HashMap();
        TemplateTableParamBo tp=new TemplateTableParamBo(this.conn);
        HashMap kqParamMap=tp.getDefineKqParamInfo(); 
        if(kqParamMap.get("1")!=null) 
             list.add(ResourceFactory.getProperty("general.template.overtimeApply"));
        if(kqParamMap.get("2")!=null)
             list.add(ResourceFactory.getProperty("general.template.leavetimeApply"));
        if(kqParamMap.get("3")!=null)
             list.add(ResourceFactory.getProperty("general.template.officetimeApply"));
        String name="";
        for(int i=0;i<list.size();i++)
        {
            name=list.get(i)!=null&&list.get(i).toString().length()>0?list.get(i).toString():"";
            ArrayList returnList=getBuisTemplatelList(name,"60");
            if(name.equalsIgnoreCase(ResourceFactory.getProperty("general.template.overtimeApply"))){//加班
                kqMap.put("0K0G",returnList);
            }
            if(name.equalsIgnoreCase(ResourceFactory.getProperty("general.template.leavetimeApply"))){//请假
                kqMap.put("0K0F",returnList);
            }
            if(name.equalsIgnoreCase(ResourceFactory.getProperty("general.template.officetimeApply"))){//公出
                kqMap.put("0K0H",returnList);
            }
        }           
        return kqMap;
    }

    /**
     * @return  
     * @Title: getBuisTemplatelList 
     * @Description: TODO
     * @param name
     * @param string void   
     * @throws 
    */
    private ArrayList getBuisTemplatelList(String operationname, String staticid) {

        ArrayList list=new ArrayList();
        ContentDAO dao=new ContentDAO(this.conn);
        SubsysOperation subsysOperation=new SubsysOperation(this.conn,this.userView);
        String codes=subsysOperation.getView_value(staticid, operationname);
        if("60".equals(staticid)) //考勤业务办理
        {
            try
            {
                TemplateTableParamBo tp=new TemplateTableParamBo(this.conn);  
                if(operationname.equalsIgnoreCase(ResourceFactory.getProperty("general.template.overtimeApply"))) //加班
                {
                    String tabids=tp.getAllDefineKqTabs(1);  
                    if(tabids.length()>0)
                        codes=tabids.substring(1);
                }
                if(operationname.equalsIgnoreCase(ResourceFactory.getProperty("general.template.leavetimeApply"))) //请假
                {
                    String tabids=tp.getAllDefineKqTabs(2);  
                    if(tabids.length()>0)
                        codes=tabids.substring(1);
                }
                if(operationname.equalsIgnoreCase(ResourceFactory.getProperty("general.template.officetimeApply"))) //公出
                {
                    String tabids=tp.getAllDefineKqTabs(3);  
                    if(tabids.length()>0)
                        codes=tabids.substring(1);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        if(codes!=null&&codes.length()>0)
        {
            String opertationcodes[]=codes.split(",");
            StringBuffer buf=new StringBuffer();
            for(int i=0;i<opertationcodes.length;i++)
            {
                buf.append("'"+opertationcodes[i]+"',");
            }
            buf.setLength(buf.length()-1);
            StringBuffer sql=new StringBuffer();
            sql.append("select tabid,name from template_table where tabid in("+buf.toString()+") order by tabid");
            try
            {
                RowSet rset=dao.search(sql.toString());
                while(rset.next())
                {
                    /**此业务模板无权限时，不加(对人事变动)*/
                    HashMap map=new HashMap();
                   
                    if("60".equals(staticid)){ //考勤业务办理
                        if(this.userView.isHaveResource(IResourceConstant.RSBD,rset.getString("tabid"))){
                            map.put("tabid",rset.getString("tabid"));              
                            map.put("task_topic",rset.getString("name"));
                            map.put("task_id", "0");
                            map.put("ins_id", "0");
                            map.put("infor_type", "1");
                            list.add(map);
                        }
                    }
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
            
        }
        return list;
    
    }

    /**
     * @param selfapply2 
     * @param ins_id 
     * @throws GeneralException  
     * @Title: autoCalculate 
     * @Description: TODO
     * @param userView2 void   
     * @throws 
    */
    public boolean autoCalculate(UserView userView, String ins_id, String selfapply) throws GeneralException {
        /**员工自助申请标识
         *＝1员工
         *＝0业务员 
         */
        if("1".equals(selfapply))
        {   
            
            this.templateTableBo.setBEmploy(true);
        }
        ArrayList formulalist = this.templateTableBo.readFormula();
        // 有计算公式时才计算
		if (formulalist.size() > 0) {
			if (this.templateTableBo.getAutoCaculate().length() == 0) {
				if (SystemConfig.getPropertyValue("templateAutoCompute") != null && 
						"true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute")) && !"1".equalsIgnoreCase(selfapply)) {
					this.templateTableBo.batchCompute(ins_id);
					return true;
				}
			} else if ("1".equals(this.templateTableBo.getAutoCaculate())) {
				this.templateTableBo.batchCompute(ins_id);
				return true;
			}
		}
        return false;
        
    }

    /**
     * @throws GeneralException  
     * @Title: addBusinessCallIn 
     * @Description: TODO void   
     * @throws 
    */
    public void addBusinessCallIn(String tabid) throws GeneralException {
        // TODO Auto-generated method stub
        String name = this.userView.getUserName()+"templet_"+tabid;
        RowSet rs =null;
        ContentDAO dao=null;
        try
        {
            String a0100=null;
            RecordVo vo=new RecordVo(name);
            IDGenerator idg=new IDGenerator(2,this.conn);
            dao=new ContentDAO(this.conn);
            /**
             * 查找变化前的历史记录单元格
             * 保存时把这部分单元格的内容
             * 过滤掉，不作处理
             * */            
            
            HashMap sub_map=this.templateTableBo.getHisModeSubCell();
            a0100=  idg.getId("rsbd.a0100");
            if(this.templateTableBo.getInfor_type()==2||this.templateTableBo.getInfor_type()==3)
                a0100="B"+a0100;
            
            if(this.templateTableBo.getInfor_type()==1&&(this.templateTableBo.getDest_base()==null||this.templateTableBo.getDest_base().length()==0))
                throw new GeneralException("人员调入业务模板未定义目标库!");
            
            
            
            if(this.templateTableBo.getInfor_type()==1)
            {
                
                ArrayList dbList=DataDictionary.getDbpreList();
                String dbpre=this.templateTableBo.getDest_base();
                for(int i=0;i<dbList.size();i++)
                {
                    String pre=(String)dbList.get(i);
                    if(pre.equalsIgnoreCase(this.templateTableBo.getDest_base()))
                        dbpre=pre;
                }
                
                vo.setString("a0100",a0100);
                vo.setString("basepre",dbpre);
                if(vo.hasAttribute("a0101_2"))
                {
                    vo.setString("a0101_2", "--");
                }
                if(vo.hasAttribute("a0101_1"))
                {
                    vo.setString("a0101_1", "--");
                }
            }
            else
            {
                if(this.templateTableBo.getInfor_type()==2)
                    vo.setString("b0110",a0100);
                if(this.templateTableBo.getInfor_type()==3)
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
                TemplateSetBo setbo =(TemplateSetBo)entry.getValue();
                TSubSetDomain setdomain=new TSubSetDomain(setbo.getXml_param());
                String xml=setdomain.outContentxml();
                vo.setString(field_name.toLowerCase(), xml);
            }
            
            rs=dao.search("select "+Sql_switcher.isnull("max(a0000)","0")+"+1 from "+name);
            if(rs.next())
                vo.setInt("a0000", rs.getInt(1));
            vo.setString("seqnum", CreateSequence.getUUID());
            dao.addValueObject(vo);
            
            if(Sql_switcher.searchDbServer()==Constant.ORACEL)
            {
                 if("1".equals(this.templateTableBo.getId_gen_manual())){
                    
                 }else{
                     this.templateTableBo.filloutSequence(a0100, this.templateTableBo.getDest_base(), name);   
                 }
                        
            }
            else    
            {
             if("1".equals(this.templateTableBo.getId_gen_manual())){
                
             }else{
                 this.templateTableBo.filloutSequence(a0100, this.templateTableBo.getDest_base(), name);         
             }
                    
            }
            /**生成序号*/

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            String message=ex.toString();
            if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
            {  
                PubFunc.resolve8060(this.conn,name);
                throw GeneralExceptionHandler.Handle(new Exception("请重新操作!"));
            }
            else
                throw GeneralExceptionHandler.Handle(ex);
        }       
    }
    public boolean relevancePost(RecordVo vo) throws GeneralException{
        //B0110为单位指标、E0122为部门指标、E01A1 岗位指标
        String e01a1 =null;
        String e0122 =null;
        String b0110 =null;
        if(vo.hasAttribute("e01a1_2")){
            e01a1=vo.getString("e01a1_2");
        }
        if(vo.hasAttribute("e0122_2")){
            e0122=vo.getString("e0122_2");
        }
        if(vo.hasAttribute("b0110_2")){
            b0110=vo.getString("b0110_2");
        }
        if(e01a1!=null&&e01a1.trim().length()>0){
            if(e0122!=null){
                if(e0122.trim().length()>0)
                {
                    if(e01a1.length()>e0122.length()&&e01a1.substring(0,e0122.length()).equals(e0122)){
                        
                    }
                    else
                        throw GeneralExceptionHandler.Handle(new Exception("岗位和部门未关联,请重新选择!")); 
                }
                else
                {
                    String tempcodeid=e01a1;
                    String tempdesc ="";
                    while (tempdesc.trim().length()==0){
                        if(tempcodeid.length()>=2){
                            tempcodeid=tempcodeid.substring(0, tempcodeid.length()-1);
                            tempdesc=AdminCode.getCodeName("UM", tempcodeid);
                        }else{
                            break;
                        }
                    }
                    if(tempdesc.trim().length()>0){
                        vo.setString("e0122_2", tempcodeid);
                    }
                }
            }
            if(b0110!=null){
                if(b0110.trim().length()>0)
                {
                    if(e01a1.length()>b0110.length()&&e01a1.substring(0,b0110.length()).equals(b0110)){
                        
                    }
                    else
                        throw GeneralExceptionHandler.Handle(new Exception("岗位和单位未关联,请重新选择!")); 
                }
                else
                {
                    String tempcodeid=e01a1;
                    String tempdesc ="";
                    while (tempdesc.trim().length()==0){
                        if(tempcodeid.length()>=2){
                            tempcodeid=tempcodeid.substring(0, tempcodeid.length()-1);
                            tempdesc=AdminCode.getCodeName("UN", tempcodeid);
                        }else{
                            break;
                        }
                    }
                    if(tempdesc.trim().length()>0){
                        vo.setString("b0110_2", tempcodeid);
                    }
                }
            }
        }else if(e0122!=null&&e0122.trim().length()>0){ 
            if(b0110!=null){
                if(b0110.trim().length()>0)
                {
                    if(e0122.length()>b0110.length()&&e0122.substring(0,b0110.length()).equals(b0110)){
                        
                    }
                    else
                        throw GeneralExceptionHandler.Handle(new Exception("部门和单位未关联,请重新选择!")); 
                }
                else
                {
                    String tempcodeid=e0122;
                    String tempdesc ="";
                    while (tempdesc.trim().length()==0){
                        if(tempcodeid.length()>=2){
                            tempcodeid=tempcodeid.substring(0, tempcodeid.length()-1);
                            tempdesc=AdminCode.getCodeName("UN", tempcodeid);
                        }else{
                            break;
                        }
                    }
                    if(tempdesc.trim().length()>0){
                        vo.setString("b0110_2", tempcodeid);
                    }
                }
            }
        }
        
        return true;
    }

    /**
     * @param midValue 
     * @return 
     * @Title: calculateBusiness 
     * @Description: TODO
     * @param insId
     * @param selfapply void   
     * @throws 
    */
    public String calculateBusiness(String ins_id, String selfapply, String midValue){
        try {
            if(selfapply!=null&& "1".equals(selfapply)){
                this.templateTableBo.setBEmploy(true);
            }
            if(midValue!=null&&midValue.trim().length()>0){
                this.templateTableBo.setMidValue(midValue);
            }
                
            this.templateTableBo.batchCompute(ins_id);
            return "calculateok";
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg=e.toString();
            int index_i=errorMsg.indexOf("description:");
            String message=errorMsg.substring(index_i+12);
            return message;
        }
       
    }

    /**
     * @return 
     * @param tabid  
     * @Title: validateMidvarBusiness 
     * @Description: TODO
     * @param objid
     * @param insId
     * @param pagenum void   
     * @throws 
    */
    public ArrayList validateMidvarBusiness() {
        ArrayList fieldlist;
        ArrayList midvarList = new ArrayList();
        try {
            fieldlist = this.templateTableBo.getMidVariableList();
            for(int i=0;i<fieldlist.size();i++){
                HashMap mesMap= new HashMap();
                FieldItem item=(FieldItem)fieldlist.get(i);
                String formula=item.getFormula();
                if(formula==null||formula.trim().length()==0)
                {
                    String itemdesc=item.getItemdesc();
                    String itemtype=item.getItemtype();
                    String codesetid=item.getCodesetid();
                    int length=item.getItemlength();
                    if("A".equalsIgnoreCase(itemtype)&&("0".equals(codesetid)||codesetid.trim().length()==0)){
                        mesMap.put("itemdesc", itemdesc);
                        mesMap.put("itemtype", itemtype);
                        mesMap.put("length", String.valueOf(length));
//                      message.append(","+itemdesc+":"+itemtype+":"+length);
                    }else{
                        mesMap.put("itemdesc", itemdesc);
                        mesMap.put("itemtype", itemtype);
                        mesMap.put("length", "0");
//                      message.append(","+itemdesc+":"+itemtype+":0");
                    }
                    midvarList.add(mesMap);       
                }
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        
        return midvarList;
    }

    /**
     * @return  
     * @Title: getBusinessOrderFlag 
     * @Description: 获得业务用户的业务分类是否启用 
     * @throws 
    */
    public boolean getBusinessOrderFlag() {
        StringBuffer sql = new StringBuffer();
        sql.append("select id,name from t_hr_subsys where id in ('34','37','38','39','40','55','56','57' ");//56:机构管理 57：岗位管理
        if(SystemConfig.getPropertyValue("unit_property")!=null)
        {
            if("psorgans_jcg".equalsIgnoreCase(SystemConfig.getPropertyValue("unit_property"))) //警衔管理、检察官管理
            {
                sql.append(",'51','52'");
            }
            else if("psorgans_fg".equalsIgnoreCase(SystemConfig.getPropertyValue("unit_property"))) //警衔管理、法官等级
            {
                sql.append(",'51','53'");
            }
            else if("psorgans_gx".equalsIgnoreCase(SystemConfig.getPropertyValue("unit_property"))) //警衔管理、关衔管理
            {
                sql.append(",'51','54'");
            }
        }
        sql.append(" ) order by id");
        ContentDAO dao = new ContentDAO(this.conn);
        SubsysOperation so = new SubsysOperation(this.conn,this.userView);
        RowSet rs = null;
        RowSet rset=null;
        boolean checkOn=false;
        try {
            rs = dao.search(sql.toString());
            while(rs.next()){
                String id = rs.getString("id");
                ArrayList list = so.getView_tag(id);//得到所有业务分类的名字
                if(list==null||list.size()<=0){//如果该业务模板下没有业务分类
                   continue;
                }else{
                    HashMap map = so.getMap();//是否启用
                    String check = (String)map.get(id);
                    if("true".equalsIgnoreCase(check)){
                        checkOn=true;  
                        break;
                    }
                }
            }
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
           if(rset!=null){
               try {
                rset.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
           }
       }
       return checkOn;
    }

    /** 
     * @Title: getAutoUserTemplateList 
     * @Description: 自助用户业务申请模版列表
     * @throws 
    */
    public ArrayList getAutoUserTemplateList() {
        
        UserView RealuserView = null;
        RowSet rs =null;
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList list = new ArrayList();
        //业务用户关联自助用户 按自助用户走
        if(this.userView.getS_userName()!=null&&this.userView.getS_userName().length()>0&&this.userView.getStatus()==0&&this.userView.getBosflag()!=null){
            RealuserView=new UserView(this.userView.getS_userName(), this.userView.getS_pwd(), this.conn);
            try {
                RealuserView.canLogin();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        if(RealuserView==null){
            RealuserView=this.userView;
        }
        try {
            
            TemplateTableParamBo tp = new TemplateTableParamBo(this.conn);
            String tabids=tp.getAllDefineKqTabs(0); 
            if(tabids.length()==0)
                tabids+=",-1000";
            
            String staitic_="static";
            if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
            	staitic_="static_o";
            }
           
            HashMap map = null;
            Set tabidSet = new HashSet();
            
            StringBuffer strsql = new StringBuffer();
            strsql.append("select a.TabId,a.Name,a.operationcode,b.operationname,b."+staitic_+" "+staitic_+" from ");
            strsql.append("template_table a ,operation b where a.operationcode=b.operationcode ");
            strsql.append("and b.operationtype <> 0 ");
            rs = dao.search(strsql.toString());
            
            while (rs.next()){// 权限控制
               if(rs.getString(staitic_)!=null&& "1".equals(rs.getString(staitic_)))
                  if (!RealuserView.isHaveResource(IResourceConstant.RSBD, rs.getString("tabid")))
                    continue;
               if(rs.getString(staitic_)!=null&& "2".equals(rs.getString(staitic_)))
                      if (!RealuserView.isHaveResource(IResourceConstant.GZBD, rs.getString("tabid")))
                        continue;
               if(rs.getString(staitic_)!=null&& "8".equals(rs.getString(staitic_)))
                      if (!RealuserView.isHaveResource(IResourceConstant.INS_BD, rs.getString("tabid")))
                        continue;
               if(rs.getString(staitic_)!=null&& "3".equals(rs.getString(staitic_)))
                      if (!RealuserView.isHaveResource(IResourceConstant.PSORGANS, rs.getString("tabid")))
                        continue;
               if(rs.getString(staitic_)!=null&& "4".equals(rs.getString(staitic_)))
                      if (!RealuserView.isHaveResource(IResourceConstant.PSORGANS_FG, rs.getString("tabid")))
                        continue;
               if(rs.getString(staitic_)!=null&& "5".equals(rs.getString(staitic_)))
                      if (!RealuserView.isHaveResource(IResourceConstant.PSORGANS_GX, rs.getString("tabid")))
                        continue;
               if(rs.getString(staitic_)!=null&& "6".equals(rs.getString(staitic_)))
                      if (!RealuserView.isHaveResource(IResourceConstant.PSORGANS_JCG, rs.getString("tabid")))
                        continue;
               if(rs.getString(staitic_)!=null&&("10".equals(rs.getString(staitic_))|| "11".equals(rs.getString(staitic_))))
                   continue;
               
               String tabid=rs.getString("tabid");
               if((tabids+",").indexOf(","+tabid+",")!=-1) //不包含考勤业务申请信息
               {
                   continue;
               }
               
               map = new HashMap();
               tabidSet.add(""+rs.getInt("tabid"));
               map.put("tabid", rs.getString("tabid"));
               map.put("name", rs.getString("tabid")+":"+rs.getString("name"));
               map.put("task_id", "0");
               map.put("ins_id", "0");
               list.add(map);
            }
            HashMap tabMap=new HashMap();
            for(Iterator t=tabidSet.iterator();t.hasNext();){
                String temptabid=(String)t.next();
                HashMap tempmap =getInfortype(temptabid);
                tabMap.put(temptabid,tempmap);
            } 
            for(int i=0;i<list.size();i++){
                HashMap linshimap=(HashMap)list.get(i);
                String temptabid=(String)linshimap.get("tabid");
                HashMap tempmap=(HashMap)tabMap.get(temptabid);
                linshimap.put("infor_type",(String)tempmap.get("infor_type"));
                
                linshimap.put("factorFlag",(String)tempmap.get("factorFlag"));
                linshimap.put("operationtype",(String)tempmap.get("operationtype"));
            } 
        
        } catch (Exception e) {
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
        return list;

    }

    /** 
     * @Title: searchCheckOnTemplateList 
     * @Description: TODO
     * @return ArrayList   
     * @throws 
    */
    public ArrayList searchCheckOnTemplateList() {
        RowSet rs = null;
        ArrayList list = new ArrayList();
        try{
        	String staitic_="static";
            if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
            	staitic_="static_o";
            }
        	
        	ContentDAO dao = new ContentDAO(this.conn);
    		HashMap privMap=new HashMap();
    		StringBuffer sql=new StringBuffer("");
    		sql.append("select a.TabId,a.Name,a.operationcode,a.factor,b.operationname,b.operationtype,b."+staitic_+" "+staitic_+" from template_table a ,operation b where a.operationcode=b.operationcode ");
    	    rs = dao.search(sql.toString());
    	    while (rs.next()) {
    	                if (rs.getString(staitic_) != null && "1".equals(rs.getString(staitic_)))// 人事异动模版权限
    	                    if (!this.userView.isHaveResource(IResourceConstant.RSBD, rs.getString("tabid")))
    	                        continue;
    	                if (rs.getString(staitic_) != null && "2".equals(rs.getString(staitic_)))// 薪资管理模版权限
    	                    if (!this.userView.isHaveResource(IResourceConstant.GZBD, rs.getString("tabid")))
    	                        continue;
    	                if (rs.getString(staitic_) != null && "8".equals(rs.getString(staitic_)))// 保险管理模版权限
    	                    if (!this.userView.isHaveResource(IResourceConstant.INS_BD, rs.getString("tabid")))
    	                        continue;
    	                if (rs.getString(staitic_) != null && "3".equals(rs.getString(staitic_)))// 警衔管理模版权限
    	                    if (!this.userView.isHaveResource(IResourceConstant.PSORGANS, rs.getString("tabid")))
    	                        continue;
    	                if (rs.getString(staitic_) != null && "4".equals(rs.getString(staitic_)))// 法官管理模版权限
    	                    if (!this.userView.isHaveResource(IResourceConstant.PSORGANS_FG, rs.getString("tabid")))
    	                        continue;
    	                if (rs.getString(staitic_) != null && "5".equals(rs.getString(staitic_)))// 关衔管理模版权限
    	                    if (!this.userView.isHaveResource(IResourceConstant.PSORGANS_GX, rs.getString("tabid")))
    	                        continue;
    	                if (rs.getString(staitic_) != null && "6".equals(rs.getString(staitic_)))// 检察官管理模版权限
    	                    if (!this.userView.isHaveResource(IResourceConstant.PSORGANS_JCG, rs.getString("tabid")))
    	                        continue;
    	                if (rs.getString(staitic_) != null && "10".equals(rs.getString(staitic_)))// 单位管理模版权限
    	                    if (!this.userView.isHaveResource(IResourceConstant.ORG_BD, rs.getString("tabid")))
    	                        continue;
    	                if (rs.getString(staitic_) != null && "11".equals(rs.getString(staitic_)))// 岗位管理模版权限
    	                    if (!this.userView.isHaveResource(IResourceConstant.POS_BD, rs.getString("tabid")))
    	                        continue;
    	                privMap.put(rs.getString("tabid"),"1");
    	    }  
        	sql.setLength(0);
            sql.append("select id,name from t_hr_subsys where id in ('34','37','38','39','40','55','56','57' ");//56:机构管理 57：岗位管理
            if(SystemConfig.getPropertyValue("unit_property")!=null)
            {
                if("psorgans_jcg".equalsIgnoreCase(SystemConfig.getPropertyValue("unit_property"))) //警衔管理、检察官管理
                {
                    sql.append(",'51','52'");
                }
                else if("psorgans_fg".equalsIgnoreCase(SystemConfig.getPropertyValue("unit_property"))) //警衔管理、法官等级
                {
                    sql.append(",'51','53'");
                }
                else if("psorgans_gx".equalsIgnoreCase(SystemConfig.getPropertyValue("unit_property"))) //警衔管理、关衔管理
                {
                    sql.append(",'51','54'");
                }
            }
            sql.append(" ) order by id"); 
            SubsysOperation so = new SubsysOperation(this.conn,this.userView);
           
            
            rs=dao.search(sql.toString());
            HashMap map =null;
            while(rs.next()){
                String id = rs.getString("id");
                ArrayList tempList = so.getView_tag_tab(id);//得到所有业务分类的名字
                if(tempList==null||tempList.size()<=0){//如果该业务模板下没有业务分类
                    continue;
                }else{
                   for(int i=0;i<tempList.size();i++){
                       
                       String sortname = (String) tempList.get(i);
                       String[] temps=sortname.split("~~");
                       //bug 32818 用户只写了业务分类名称，没有选模版，temps只有一个元素，后面会报错。
                       if(temps.length<2)
                    	   continue;
                       map=new HashMap();
                       map.put("id", id);
                       map.put("categories", temps[0]);
                       
                       String tabids=temps[1];
                       if(StringUtils.isNotBlank(tabids))
                       {
                    	   String[] _temps=tabids.split(",");
                    	   boolean hasTemplateTab=false;
                    	   for (String a : _temps) {
                    		   if(StringUtils.isNotBlank(a)&&privMap.get(a.trim())!=null)
                    			   hasTemplateTab=true;
                    	   }
                    	   if(!hasTemplateTab)
                    		   continue; 
                       }
                       else 
                    	   continue;
                       
                       list.add(map);
                   }
                }
            }
            TemplateTableParamBo tp=new TemplateTableParamBo(this.conn); 
            boolean hasKq=tp.isDefineKqParam(); 
            if(hasKq){
            	 
                String tabids=tp.getAllDefineKqTabs(0); 
                String[] _temps=tabids.split(",");
         	    boolean hasTemplateTab=false;
         	    for (String a : _temps) {
         		   if(StringUtils.isNotBlank(tabids)&&privMap.get(a.trim())!=null)
         			   hasTemplateTab=true;
         	    }
         	    if(hasTemplateTab)
         	    {
	                map=new HashMap();
	                map.put("categories", "考勤业务办理");
	                map.put("id", "30");
	                list.add(map);
         	    }
            }
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
        return list;
  }

  
    
    /**
     * @param operationname  
     * @Title: searchNextCheckOnTemplateList 
     * @Description: TODO
     * @param id
     * @return ArrayList   
     * @throws 
    */
    public ArrayList searchNextCheckOnTemplateList(String id, String operationname) {
        ArrayList dataList = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        SubsysOperation so = new SubsysOperation(this.conn, this.userView);
        StringBuffer sql = new StringBuffer();
        HashMap map = null;
        RowSet rs=null;
        String select_id="";
        try {
            if("30".equals(id)){//表示是考勤业务
                TemplateTableParamBo tp=new TemplateTableParamBo(this.conn); 
                String tabids=tp.getAllDefineKqTabs(0); 
                if(tabids.length()==0)
                    tabids+="-1000,";
                select_id=tabids;
            }else{
                ArrayList list = so.getView_tag(id);// 得到所有业务分类的名字
                if (list == null || list.size() <= 0) {// 如果该业务模板下没有业务分类
                    return dataList;
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        String sortname = list.get(i).toString();
                        if (!sortname.equalsIgnoreCase(operationname)) {
                            continue;
                        }else{
                            select_id = so.getView_value(id, sortname);
                            break;
                        }
                    }
                }
            } 
            String[] select_ids = select_id.split(",");
            String staitic_="static";
            if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
            	staitic_="static_o";
            }
            sql.append("select a.TabId,a.Name,a.operationcode,a.factor,b.operationname,b.operationtype,b."+staitic_+" "+staitic_+" from template_table a ,operation b where a.operationcode=b.operationcode ");
            if (select_ids != null && select_ids.length > 0) {
                sql.append(" and TabId in (");
                for (int j = 0; j < select_ids.length; j++) {
                    sql.append("'" + select_ids[j] + "',");
                }
                sql.setLength(sql.length() - 1);
                sql.append(")");
            }
            sql.append(" order by tabid");
            rs = dao.search(sql.toString());
            Set tabidSet = new HashSet();
            while (rs.next()) {
                if (rs.getString(staitic_) != null && "1".equals(rs.getString(staitic_)))// 人事异动模版权限
                    if (!this.userView.isHaveResource(IResourceConstant.RSBD, rs.getString("tabid")))
                        continue;
                if (rs.getString(staitic_) != null && "2".equals(rs.getString(staitic_)))// 薪资管理模版权限
                    if (!this.userView.isHaveResource(IResourceConstant.GZBD, rs.getString("tabid")))
                        continue;
                if (rs.getString(staitic_) != null && "8".equals(rs.getString(staitic_)))// 保险管理模版权限
                    if (!this.userView.isHaveResource(IResourceConstant.INS_BD, rs.getString("tabid")))
                        continue;
                if (rs.getString(staitic_) != null && "3".equals(rs.getString(staitic_)))// 警衔管理模版权限
                    if (!this.userView.isHaveResource(IResourceConstant.PSORGANS, rs.getString("tabid")))
                        continue;
                if (rs.getString(staitic_) != null && "4".equals(rs.getString(staitic_)))// 法官管理模版权限
                    if (!this.userView.isHaveResource(IResourceConstant.PSORGANS_FG, rs.getString("tabid")))
                        continue;
                if (rs.getString(staitic_) != null && "5".equals(rs.getString(staitic_)))// 关衔管理模版权限
                    if (!this.userView.isHaveResource(IResourceConstant.PSORGANS_GX, rs.getString("tabid")))
                        continue;
                if (rs.getString(staitic_) != null && "6".equals(rs.getString(staitic_)))// 检察官管理模版权限
                    if (!this.userView.isHaveResource(IResourceConstant.PSORGANS_JCG, rs.getString("tabid")))
                        continue;
                if (rs.getString(staitic_) != null && "10".equals(rs.getString(staitic_)))// 单位管理模版权限
                    if (!this.userView.isHaveResource(IResourceConstant.ORG_BD, rs.getString("tabid")))
                        continue;
                if (rs.getString(staitic_) != null && "11".equals(rs.getString(staitic_)))// 岗位管理模版权限
                    if (!this.userView.isHaveResource(IResourceConstant.POS_BD, rs.getString("tabid")))
                        continue;
                map = new HashMap();
                String tempstatic = rs.getString(staitic_);
                if ("10".equals(tempstatic)){
                    map.put("infor_type", "2");
                } else if ("11".equals(tempstatic)) {
                    map.put("infor_type", "3");
                } else {
                    map.put("infor_type", "1");
                }
                String factor=rs.getString("factor");
                if(SystemConfig.getPropertyValue("promptIndex_template")!=null&& "false".equalsIgnoreCase(SystemConfig.getPropertyValue("promptIndex_template").trim())){
                    map.put("factorFlag", "false");
                }else{
                    if(factor==null|| "".equals(factor)){
                        map.put("factorFlag", "false");
                    }else{
                        map.put("factorFlag", "true");
                    }
                }
                map.put("operationtype", rs.getString("operationtype"));
                tabidSet.add("" + rs.getInt("tabid"));
                map.put("tabid", rs.getString("tabid"));
                map.put("name", rs.getString("tabid")+":"+rs.getString("name"));
                map.put("task_id", "0");
                map.put("ins_id", "0");
                dataList.add(map);
            }
        } catch (Exception e) {
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
        return dataList;
    }
    /**得到basepre和a1000的list 用于删除个人附件*/
    public ArrayList getPersonlist(int infor_type,String tablename,String isSelfApply){
        ArrayList list = new ArrayList();
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rs = null;
            StringBuffer sb = new StringBuffer("");
            if(infor_type==1){ 
                if("1".equalsIgnoreCase(isSelfApply))
                {
                    ArrayList templist = new ArrayList();
                    String basepre =this.userView.getDbname();
                    String a0100 =this.userView.getA0100();
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
    public void submitAttachmentFile(String ins_id ,TemplateTableBo tablebo,String flag,String tabid) throws GeneralException{

    	RowSet rset = null;
        RowSet rowSet = null;
        RowSet objectRowSet = null;
        String archive_attach_to = paramBo.getArchive_attach_to();	
		Boolean attach_history=paramBo.isAttach_history();
        if("".equals(archive_attach_to)){//bug 49689 单位、岗位个人附件提交后重复
        	if (tablebo.getInfor_type() == 1) {
        		archive_attach_to = "A00";
        	}else if (tablebo.getInfor_type() == 2) {
        		archive_attach_to = "B00";
        	}else if (tablebo.getInfor_type() == 3) {
        		archive_attach_to = "K00";
        	}
        }
        HashMap delMap=new HashMap();
        ContentDAO dao=new ContentDAO(this.conn);
        
        try
        {
    		boolean isSavaAttachToMinSet = false;
    		FieldSet a01Set = DataDictionary.getFieldSetVo("A01");
    		if(paramBo.isArchiveAttachToMainSet() && "1".equals(a01Set.getMultimedia_file_flag())){
				isSavaAttachToMinSet = true;
    		}
            if (this.hasFunction(isSavaAttachToMinSet,archive_attach_to)) {
            	ArrayList paramList=new ArrayList();
	            	String task_id = "";
	        		StringBuffer sb = new StringBuffer("");
	        		StringBuffer sbfortask = new StringBuffer("");
	        		sbfortask.append("select max(task_id) task_id from t_wf_task where ins_id="+ins_id);
	        		if(tablebo.getInfor_type() == 1){
		        		sb.append("select * from t_wf_file where ins_id=? and tabid=? and attachmenttype=1 order by file_id");// 个人附件
		        		paramList=new ArrayList();
		        		paramList.add(ins_id);
		        		paramList.add(tabid);
	        		}else if(tablebo.getInfor_type() == 2){
		        		sb.append("select * from t_wf_file where ins_id=? and tabid=? and attachmenttype=1 order by file_id");// 个人附件
		        		paramList=new ArrayList();
		        		paramList.add(ins_id);
		        		paramList.add(tabid);
	        		}else if(tablebo.getInfor_type() == 3){
		        		sb.append("select * from t_wf_file where ins_id=? and tabid=? and attachmenttype=1 order by file_id");// 个人附件
		        		paramList=new ArrayList();
		        		paramList.add(ins_id);
		        		paramList.add(tabid);
	        		}
        		//个人附件归档时不需要判断 lis 20160820
        		/*if (!this.userView.isAdmin() && !this.userView.getGroupId().equals("1") && !this.getIsIgnorePriv()) {
        			sb.append(" and filetype in (select id from mediasort where flag in (" + this.getMediaPriv() + "))");
        		}*/
        		//查询对应的最后一个task_id
        		rowSet = dao.search(sbfortask.toString());
        		if(rowSet.next())
        			task_id =rowSet.getInt("task_id")+"";
        		rowSet = dao.search(sb.toString(),paramList);//bug 50505 没有传递参数
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        		String time = sdf.format(new Date());// 得到当前日期
        		switch (Sql_switcher.searchDbServer()) {
        		case Constant.ORACEL : {
        			time = "to_date('" + time + "','yyyy-mm-dd')";
        			break;
        		}
        		case Constant.MSSQL : {
        			time = "'" + time + "'";
        			break;
        		}
        		}
        		HashMap destA0100Map = new HashMap();//用于是否清除目标主集附件。 
        		HashMap map = tablebo.getDestination_a0100();
        		while (rowSet.next()) {
        			Boolean isNeedInsert=false;
        			sb.setLength(0);
        			int flagid = rowSet.getInt("filetype");// 和mediasort表的id相关联
        			int file_id = rowSet.getInt("file_id");
        			int state = rowSet.getInt("state");
        			String mediaId = rowSet.getString("i9999");
        			if(state==1&&"-1".equals(mediaId)) {//个人附件或者公共附件 新增的记录删除后不执行归档操作
                        continue;
                    }
        			int recid = 0;
        			String tableName = null;
        			RecordVo updatevo = null;
        			Blob blob = null;
        			String filepath = rowSet.getString("filepath");
        			//liuyz bug25774 兼容6.3程序上传的附件，6.3程序上传附件存入t_wf_file表中无filepath，需要将content中二进制数据转换成文件，文件路径重新保存到t_wf_file的filepath中
        			AttachmentBo attachmentBo = new AttachmentBo(userView, conn, tabid);
        			if(filepath==null)
        			{
        				 InputStream in = null;
        				 HashMap fileMap =attachmentBo.downloadFile(String.valueOf(file_id));
        				 filepath = (String)fileMap.get("filepath");
        				in = (InputStream)fileMap.get("ole");
        				String ext = attachmentBo.getExt().toLowerCase();
        				//保存到指定目录(路径)按照子集附件保存路径存储
        				attachmentBo.initParam(true);
        			    String middlepath = "";
        				if("\\".equals(File.separator)){//证明是windows
        					middlepath = "subdomain\\template_";
        				}else if("/".equals(File.separator)){//证明是linux
        					middlepath = "subdomain/template_";
        				}
        				UUID uuid = UUID.randomUUID();
        			    String fileuuidname = uuid.toString();
        			    filepath = attachmentBo.getAbsoluteDir(fileuuidname,middlepath)+File.separator+fileuuidname + ext;
        			    // 保存文件
        			    String absoluteDir = attachmentBo.getAbsoluteDir(fileuuidname,middlepath);
        			    if(!absoluteDir.startsWith(attachmentBo.getRootDir())) {
        			    	absoluteDir = attachmentBo.getRootDir()+absoluteDir;
        			    }
        				File file = new File(absoluteDir, fileuuidname + ext);
						try(OutputStream output = new FileOutputStream(file)) {
							byte[] bt = new byte[1024];
							int read = 0;
							while ((read = in.read(bt)) != -1) {
								output.write(bt, 0, read);
							}
						}
        				//将filepath存储到对应记录
        				StringBuffer sbin = new StringBuffer();
        				sbin.append("update t_wf_file set filepath='"+filepath+"',content='' where file_id ="+file_id);
        				dao.update(sbin.toString());
        			}
        			////liuyz bug25774 end
        			filepath = filepath.replace("\\", File.separator).replace("/", File.separator);//liunx和window盘符不同
        			File file = null;
    				if(StringUtils.isNotBlank(filepath)) {
        				attachmentBo.initParam(false);
        				if(!filepath.startsWith(attachmentBo.getRootDir())) {
        					filepath = attachmentBo.getRootDir()+filepath;
        				}
        				file = new File(filepath);
        			}
        			if(file.exists()||state==1){
	        			if (tablebo.getInfor_type() == 1) {
	        				String basepre = "";// 要归档到的人员库
	        				String a0100 = "";// 最终的人员编号
	        				if (tablebo.getOperationtype() == 0 || tablebo.getOperationtype() == 1 || tablebo.getOperationtype() == 2) {
	        					basepre = tablebo.getDestBase();
	        					String sourcea0100 =  rowSet.getString("objectid");
	        					a0100 = (String) tablebo.getDestination_a0100().get(sourcea0100);
	        				} else {
	        					basepre = rowSet.getString("basepre");
	        					a0100 = rowSet.getString("objectid");
	        				}
	        				if ("1".equals(this.userView.getHm().get("fillInfo")))
	        	            {
	        	              ArrayList list = new ArrayList();
	        	              String sql = "update t_wf_file set basepre=? where file_id=?";
	        	              list.add(basepre);
	        	              list.add(Integer.valueOf(file_id));
	        	              dao.update(sql, list);
	        	            }
	        				boolean isRemoveAtta = false;
	        				if(isSavaAttachToMinSet){
	        					isRemoveAtta = attach_history;
	        					this.saveMultimediaToFile(basepre, a0100, "A01",destA0100Map, flagid, rowSet, file, mediaId,isRemoveAtta,state);
	                    	}else{
	                    		if("A00".equalsIgnoreCase(archive_attach_to)){
	                    			tableName =  basepre + "A00";
	                    			if(state==1){
		                    			if(attach_history&&StringUtils.isNotBlank(mediaId)){
				                    		String sql="update "+tableName+" set state=9 where a0100=? and lower(flag)<>'p'  and i9999=? ";//先把多媒体数据的state置为9标记为待删除
				                    		ArrayList  list=new ArrayList();
				                    		list.add(a0100);
				                    		list.add(mediaId);
				                    		dao.update(sql, list);
				                    		delMap.put(basepre+"`"+a0100, tableName);
		                    			}
		                    		}
	                    			if(state==0){
	                    				String sql="select 1 from  "+tableName+" where a0100=? and lower(flag)<>'p'  and i9999=? ";//先把多媒体数据的state置为9标记为待删除
			                    		ArrayList  list=new ArrayList();
			                    		list.add(a0100);
			                    		list.add(mediaId);
			                    		rset=dao.search(sql, list);
			                    		if(!rset.next()||"-1".equalsIgnoreCase(mediaId)){
			                    			isNeedInsert=true;
				                    		recid = Integer.parseInt(new StructureExecSqlString().getUserI9999(basepre + "a00",a0100,"A0100",this.conn));
				                    		updatevo=new RecordVo(tableName);
				                    		updatevo.setString("a0100",a0100);
				                    		updatevo.setInt("i9999",recid);
				                    		sb.append("insert into " + tableName + " (id,a0100,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
				                    		sb.append(" select null,'" + a0100 + "'," + recid + " ,name,?,(select flag from mediasort where id=" + flagid + ") flag," );
				                    		if(Sql_switcher.searchDbServer()==Constant.ORACEL){
				                    			sb.append("case when instr(ext,'.')=1 then ext when instr(ext,'.')=0 then '.'" + Sql_switcher.concat() + "ext end ext");
				                    		}else{
				                    			sb.append("case when charindex('.',ext)=1 then ext when charindex('.',ext)=0 then '.'" + Sql_switcher.concat() + "ext end ext");
				                    		}
				                    		sb.append(",create_user,create_user,3,(select " + time + " from t_wf_file where file_id=" + file_id + ") createtime,(select " + time + " from t_wf_file where file_id=" + file_id + ") modtime from t_wf_file where file_id=" + file_id);//liuyz 通过人事异动审批的多媒体附件都是审批状态。 
					                   }
	                    			}
	                    		}
		                    	else{//归档到子集
		                    		int i9999 = 1;
									BaseInfoBo infobo=new BaseInfoBo(this.conn,this.userView,1);
		                    		ArrayList subUpdateList = paramBo.getSubUpdateList();
		                    		int updatetype = 0;
		                    		TSubsetCtrl tsubsetCtrl_ = null;
		                    		YksjParser yp=null;
		                    		for(int i=0;i<subUpdateList.size();i++){
		                    			TSubsetCtrl tsubsetCtrl = (TSubsetCtrl)subUpdateList.get(i);
		                    			String name = tsubsetCtrl.getSetcode();
		                    			String submenu = tsubsetCtrl.getSubMenu();
		                    			int type = tsubsetCtrl.getUpdatetype();
		                    			if(name.equalsIgnoreCase(archive_attach_to)&&"false".equalsIgnoreCase(submenu)){
		                    				updatetype = type;
		                    				tsubsetCtrl_ = tsubsetCtrl;
		                    			}
		                    		}
		                    		i9999=infobo.getMaxI9999(basepre,archive_attach_to,a0100);
		                    		if(updatetype==1||updatetype==0){//新增记录
		                    			isRemoveAtta = false;
		                    			if(i9999>1)
			                    			this.saveMultimediaToFile(basepre, a0100, archive_attach_to,destA0100Map, flagid, rowSet, file, i9999-1,isRemoveAtta,state);
		                    		}
		                    		else if(updatetype==2){//更新当前
		                    			isRemoveAtta = false;    
		                    			if(i9999>1)
			                    			this.saveMultimediaToFile(basepre, a0100, archive_attach_to,destA0100Map, flagid, rowSet, file, i9999-1,isRemoveAtta,state);
									}
		                    		else if(updatetype==3){//条件更新
		                    			isRemoveAtta = false;
		                    			//查出条件对应的i9999
		                    			String destab=basepre+archive_attach_to;
		                    			String srctab = "templet_"+tabid;
		                    			String i9999s = "";
		                    			String cond_str="";
		    							String condFormula=tsubsetCtrl_.getCondFormula();
		    							if(condFormula==null||condFormula.trim().length()==0){
		    								cond_str=" and ( 1=1 ) ";	
		    							}else{
		    								yp = new YksjParser( this.userView ,getCondUpdateFieldList(archive_attach_to,tabid),
		    										YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
		    								
		    								yp.run_where(condFormula);
		    								String strfilter=yp.getSQL();
		    								if(strfilter.length()>0)
		    									cond_str=" and ("+strfilter+") ";
		    							}
		    							StringBuffer strSubCondition=new StringBuffer(""); 
		    							strSubCondition.append(" where  exists (select null from t_wf_task_objlink where templet_"+tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+tabid+".ins_id=t_wf_task_objlink.ins_id ");
		    							strSubCondition.append("  and task_id="+task_id+" and tab_id="+tabid+" and state=1 ) ");
		    							strSubCondition.append(" and lower(basepre)='"+basepre.toLowerCase()+"'"); 
		    							String sql = "select i9999 from "+destab+","+srctab+" where "+destab+".a0100="+srctab+".a0100 and "+strSubCondition.substring(7)+" "+cond_str;
		    							rset = dao.search(sql);
		    							while(rset.next()) {
		    								String i9999_ = rset.getInt("i9999")+"";
		    								i9999s+=i9999_+",";
		    							}
		    							if(i9999s.length()>0) {
		    								i9999s = i9999s.substring(0, i9999s.length()-1);
		    								this.saveMultimediaToFile(basepre, a0100, archive_attach_to,destA0100Map, flagid, rowSet, file, i9999s,isRemoveAtta,state);
		    							}else{//如果没有符合条件的，更新到最后一条中//bug 51113
		    								isRemoveAtta = false;    
			                    			if(i9999>1)
				                    			this.saveMultimediaToFile(basepre, a0100, archive_attach_to,destA0100Map, flagid, rowSet, file, i9999-1,isRemoveAtta,state);
		    							}
									}
		                    	}
	                    	}
	        			} else if (tablebo.getInfor_type() == 2) {
	        				String b0110 = rowSet.getString("objectid");
	        				//lis add 20160719
	        				if(map!=null){
	        					String objectid = (String) map.get(b0110);
	        					if(objectid!=null&&!objectid.equals(b0110)){
	        						b0110 = objectid;
	        					}
	        				}
	        				//lis end 20160719   
	        				recid = Integer.parseInt(new StructureExecSqlString().getUserI9999("B00",b0110,"b0110",this.conn));
	        				tableName = "B00";
	        				if(state==1){
		        				if(attach_history&&StringUtils.isNotBlank(mediaId)){
		                    		String sql="update "+tableName+" set state=9  where b0110=? and i9999=? ";
		                    		ArrayList  list=new ArrayList();
		                    		list.add(b0110);
		                    		list.add(mediaId);
		                    		dao.update(sql, list);
		                    		delMap.put(b0110, tableName);
		        				}
	        				}
	        				if(state==0){
		        				String sql="select 1 from  "+tableName+" where b0110=? and i9999=? ";//先把多媒体数据的state置为9标记为待删除
	                    		ArrayList  list=new ArrayList();
	                    		list.add(b0110);
	                    		list.add(mediaId);
	                    		rset=dao.search(sql, list);
	                    		if(!rset.next()||"-1".equalsIgnoreCase(mediaId)){
	                    			isNeedInsert=true;
			        				updatevo=new RecordVo(tableName);
			        				updatevo.setString("b0110",b0110);
			        				updatevo.setInt("i9999",recid);
			        				sb.append("insert into B00 (b0110,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
			        				sb.append(" select '" + b0110 + "',(select " + Sql_switcher.isnull("max(i9999)", "0") + "+1 from B00 where b0110='" + b0110 + "') i9999,name,?,(select flag from mediasort where id=" + flagid + ") flag," );
			        				if(Sql_switcher.searchDbServer()==Constant.ORACEL){
			                			sb.append("case when instr(ext,'.')=1 then ext when instr(ext,'.')=0 then '.'" + Sql_switcher.concat() + "ext end ext");
			                		}else{
			                			sb.append("case when charindex('.',ext)=1 then ext when charindex('.',ext)=0 then '.'" + Sql_switcher.concat() + "ext end ext");
			                		} 
			        				sb.append(",create_user,create_user,3,(select " + time + " from t_wf_file where file_id=" + file_id + ") createtime,(select " + time + " from t_wf_file where file_id=" + file_id + ") modtime from t_wf_file where file_id=" + file_id);//liuyz 通过人事异动审批的多媒体附件都是审批状态。 
	                    		}
	        				}
	                   } else if (tablebo.getInfor_type() == 3) {
	        				String e01a1 = rowSet.getString("objectid");
	        				//lis add 20160719
	        				if(map!=null){
	        					String objectid = (String) map.get(e01a1);
	        					if(objectid!=null&&!objectid.equals(e01a1)){
	        						e01a1 = objectid;
	        					}
	        				}
	        				//lis end 20160719
	        				tableName = "K00";
	        				if(state==1){
		        				if(attach_history&&StringUtils.isNotBlank(mediaId)){
		                    		String sql="update "+tableName+" set state=9 where e01a1=? and i9999=? ";
		                    		ArrayList  list=new ArrayList();
		                    		list.add(e01a1);
		                    		list.add(mediaId);
		                    		dao.update(sql, list);
		                    		delMap.put(e01a1, tableName);
		        				}
	        				}
	        				if(state==0){
	        					String sql="select 1 from  "+tableName+" where e01a1=? and i9999=? ";//先把多媒体数据的state置为9标记为待删除
	                    		ArrayList  list=new ArrayList();
	                    		list.add(e01a1);
	                    		list.add(mediaId);
	                    		rset=dao.search(sql, list);
	                    		if(!rset.next()||"-1".equalsIgnoreCase(mediaId)){
	                    			isNeedInsert=true;
			        				recid = Integer.parseInt(new StructureExecSqlString().getUserI9999("K00",e01a1,"e01a1",this.conn));
			        				updatevo=new RecordVo(tableName);
			        				updatevo.setString("e01a1",e01a1);
			        				updatevo.setInt("i9999",recid);
			        				sb.append("insert into K00 (e01a1,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
			        				sb.append(" select '" + e01a1 + "',(select " + Sql_switcher.isnull("max(i9999)", "0") + "+1 from K00 where e01a1='" + e01a1 + "') i9999,name,?,(select flag from mediasort where id=" + flagid + ") flag,");//云南能投批准报e01a1不存在
			        				if(Sql_switcher.searchDbServer()==Constant.ORACEL){
			                			sb.append("case when instr(ext,'.')=1 then ext when instr(ext,'.')=0 then '.'" + Sql_switcher.concat() + "ext end ext");
			                		}else{
			                			sb.append("case when charindex('.',ext)=1 then ext when charindex('.',ext)=0 then '.'" + Sql_switcher.concat() + "ext end ext");
			                		}
			        				sb.append(",create_user,create_user,3,(select " + time + " from t_wf_file where file_id=" + file_id + ") createtime,(select " + time + " from t_wf_file where file_id=" + file_id + ") modtime from t_wf_file where file_id=" + file_id);//liuyz 通过人事异动审批的多媒体附件都是审批状态。 
	                    		}
	        				}
	                   }
	        			//AttachmentBo attachmentBo = new AttachmentBo(userView, conn, tabid);
	        			if("A00".equalsIgnoreCase(archive_attach_to)||"B00".equalsIgnoreCase(archive_attach_to)||"K00".equalsIgnoreCase(archive_attach_to)){
	        				if(state==0&&isNeedInsert){
		        				switch (Sql_switcher.searchDbServer()) {
			        				case Constant.ORACEL : {
			        					dao.update(sb.toString(),Arrays.asList(""));
			        					//保存附件
			        					blob = this.getOracleBlob(updatevo, file,tablebo.getInfor_type());
			        					updatevo.setObject("ole",blob);	
			        					dao.updateValueObject(updatevo);
			        					break;
			        				}
			        				case Constant.MSSQL : {
			        					dao.update(sb.toString(),Arrays.asList(attachmentBo.getBytes(file)));
			        					break;
			        				}
		        				}
	        				}
	        			}
	        		} // while 遍历结束
	            } // flag=1 结束
	        } // try
        }
        catch(Exception e)
        {
        	try{
            	Iterator iterator = delMap.entrySet().iterator();
        		while(iterator.hasNext()){
        			Entry next = (Entry) iterator.next();
        			String key = (String) next.getKey();
        			String value = (String) next.getValue();
        			String sql="";
        			String updatesql="";
        			ArrayList list=new ArrayList();
        			if("A00".equalsIgnoreCase(archive_attach_to)){
        				updatesql="update "+value+" set state=3 where a0100=? and state=9";
        				String a0100=key.split("`")[1];
        				list.add(a0100);
        			}else if("B00".equalsIgnoreCase(archive_attach_to)){
        				updatesql="update "+value+" set state=3 where b0110=? and state=9";//bug 
        				list.add(key);
        			}else if("K00".equalsIgnoreCase(archive_attach_to)){
        				updatesql="update "+value+" set state=3 where e01a1=? and state=9";
        				list.add(key);
        			}
        			dao.delete(sql, list);
        			dao.update(updatesql, list);
        		}
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
        	try{
	        	Iterator iterator = delMap.entrySet().iterator();
	    		while(iterator.hasNext()){
	    			Entry next = (Entry) iterator.next();
	    			String key = (String) next.getKey();
	    			String value = (String) next.getValue();
	    			String sql="";
	    			ArrayList list=new ArrayList();
	    			if("A00".equalsIgnoreCase(archive_attach_to)){
	    				sql="delete from "+value+" where a0100=? and state=9";
	    				String a0100=key.split("`")[1];
        				list.add(a0100);
	    			}else if("B00".equalsIgnoreCase(archive_attach_to)){
	    				sql="delete from "+value+" where B0110=? and state=9";
	    				list.add(key);
	    			}else if("K00".equalsIgnoreCase(archive_attach_to)){
	    				sql="delete from "+value+" where e01a1=? and state=9";
	    				list.add(key);
	    			}
	    				dao.delete(sql, list);
	    		}
			}catch(Exception ex){
				ex.printStackTrace();
			}
        	PubFunc.closeDbObj(rowSet);
        	PubFunc.closeDbObj(rset);
        }
    }
    
	public boolean hasFunction( boolean isSavaAttachToMinSet, String archive_attach_to){
		boolean bool = false;
		boolean issubatt = false;
		FieldSet fieldSet = null;
		String tablename = "";
		if(paramBo.getInfor_type()==1){
			if(isSavaAttachToMinSet){ //个人附件保存到主集
				tablename="A01";
				issubatt = true;
			}else{
				if(!"".equals(archive_attach_to)&&archive_attach_to.length()>0&&!"old".equals(archive_attach_to)){
					tablename  = archive_attach_to;
					issubatt = true;
					if(!"A00".equalsIgnoreCase(tablename)){
						fieldSet = DataDictionary.getFieldSetVo(tablename);
						issubatt = "1".equals(fieldSet.getMultimedia_file_flag());
					}
				}else{
					tablename="A00";
					issubatt = true;
				}
			}
		}else if(paramBo.getInfor_type()==2){
			if(isSavaAttachToMinSet) //个人附件保存到主集
				tablename="B01";
			else
				tablename="B00";
			issubatt = true;		
		}else if(paramBo.getInfor_type()==3){
			if(isSavaAttachToMinSet) //个人附件保存到主集
				tablename="K01";
			else
				tablename="K00";
			issubatt = true;
		}

		if(issubatt&&"2".equals(this.userView.analyseTablePriv(tablename)) && (this.getMediaPriv().length()>0 || this.userView.isAdmin() || "1".equals(this.userView.getGroupId()))){
			bool = true;
		}
		if(!bool){
			bool = getIsIgnorePriv();
		}
		return bool;
	}
	
	/*是否勾选了 提交入库时不判断子集和指标的权限**/
	public boolean getIsIgnorePriv(){
		if("1".equals(paramBo.getUnrestrictedMenuPriv()))//=1 不判断
			return true;//=true 提交入库时不判断子集和指标权限  =false 判断
		return false;
	}
	
	/**将多媒体分类的权限组装成sql语句的形式*/
	public String getMediaPriv(){
		String mediaPriv = "";
		StringBuffer sb = new StringBuffer(",'-1'");
		String priv = this.userView.getMediapriv().toString();//我在上层已经用hasFunction控制了。所以priv一定有值。没值则不会调用这个函数
		String[] array = priv.split(",");
		for(int i=0;i<array.length;i++){
			if("".equals(array[i])){
				continue;
			}
			sb.append(",'" + array[i] + "'");
		}
		mediaPriv = sb.substring(1);
		return mediaPriv;
	}
	/**
     * 主集或者子集附件归档
     * @param basepre
     * @param a0100
     * @param setid
     * @param destA0100Map
     * @param flagid
     * @param rowSet
     * @param file
     * @param i9999
     * @param isRemoveAtta
     * @throws GeneralException
     */
    private void saveMultimediaToFile(String basepre, String a0100, String setid, HashMap destA0100Map,int flagid,RowSet rowSet,File file,int i9999, boolean isRemoveAtta,int state)throws GeneralException{
    	try{
            ContentDAO dao=new ContentDAO(this.conn);
    		MultiMediaBo multimediabo = new MultiMediaBo(this.conn, this.userView, "A",basepre,setid, a0100, i9999);
			String keyValue= basepre+a0100;
			if (isRemoveAtta&&state==1&&(i9999!=0&&i9999!=-1)){
				//清除附件
				multimediabo.deleteMultimediaFileByA0100("A", setid, basepre, a0100, i9999);
				destA0100Map.put(keyValue, keyValue);
			}
			if(state==0&&(i9999==0||i9999==-1)&&"A01".equalsIgnoreCase(setid)||!"A01".equalsIgnoreCase(setid)){//i9999==0说明是优化前新增的附件，i9999=-1说明是优化后用户新增的。state=0不是要删除的
				RowSet set = dao.search("select flag from mediasort where id=?", Arrays.asList(flagid));
				HashMap destMap = new HashMap();
				destMap.put("mainguid", multimediabo.getMainGuid());//主集
		        destMap.put("childguid", multimediabo.getChildGuid());          
		        destMap.put("nbase", basepre);
		        destMap.put("a0100", a0100);
		        if(set.next())
		        	destMap.put("filetype", set.getString("flag"));//多媒体文件目录
		        destMap.put("filetitle", rowSet.getString("name"));//多媒体文件目录
				multimediabo.saveMultimediaFile(destMap, file, true);
				PubFunc.closeResource(set);
			}
    	}catch(Exception e){
        	e.printStackTrace();
        	throw GeneralExceptionHandler.Handle(e);
        }
    }
    private ArrayList getCondUpdateFieldList(String setid, String tabid)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			list.addAll((ArrayList)DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET).clone());
			String sql="select * from template_set  where tabid="+tabid+" and field_name is not null and field_type is not null  and subflag='0'";
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
			
			ArrayList fieldlist=getMidVariableList(tabid);//临时变量
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
	 * 从临时变量中取得对应指标列表
	 * @param tabid 
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public ArrayList getMidVariableList(String tabid)throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where  nflag=0 and templetId <> 0 ");  //人事异动的nflag=0 
			//计算本模板中的临时变量和模板指标中引入的共享的临时变量。 20150929 liuzy
			buf.append(" and (templetId ="+tabid+" or (cstate ='1' and cname in (select field_name from template_set where tabid="+tabid+" and nullif(field_name,'') is not null ))) ");
			
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
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return fieldlist;
	}
	 /**
     * @author lis
     * @Description: 得到oracle字段数据
     * @date 2016-5-25
     * @param vo
     * @param file
     * @param info_type
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
	private Blob getOracleBlob(RecordVo vo, File file,int info_type) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		StringBuffer strInsert=new StringBuffer();
		if(info_type == 1){
			String tableName = vo.getModelName();
			String a0100 = vo.getString("a0100");
			String recid = vo.getString("i9999");
			strSearch.append("select ole from ");
			strSearch.append(tableName);
			strSearch.append(" where a0100='");
			strSearch.append(a0100);
			strSearch.append("' and i9999=");
			strSearch.append(recid);
			strSearch.append(" FOR UPDATE");
			
			strInsert.append("update  ");
			strInsert.append(tableName);
			strInsert.append(" set ole=EMPTY_BLOB() where a0100='");
			strInsert.append(a0100);
			strInsert.append("' and i9999=");
			strInsert.append(recid);
		}else if(info_type == 2){
			String tableName = vo.getModelName();
			String b0110 = vo.getString("b0110");//云南能投批准报b0100不存在
			String recid = vo.getString("i9999");
			strSearch.append("select ole from ");
			strSearch.append(tableName);
			strSearch.append(" where b0110='");
			strSearch.append(b0110);
			strSearch.append("' and i9999=");
			strSearch.append(recid);
			strSearch.append(" FOR UPDATE");
			
			strInsert.append("update  ");
			strInsert.append(tableName);
			strInsert.append(" set ole=EMPTY_BLOB() where b0110='");
			strInsert.append(b0110);
			strInsert.append("' and i9999=");
			strInsert.append(recid);
		}else if(info_type == 3){
			String tableName = vo.getModelName();
			String e01a1 = vo.getString("e01a1");
			String recid = vo.getString("i9999");
			strSearch.append("select ole from ");
			strSearch.append(tableName);
			strSearch.append(" where e01a1='");
			strSearch.append(e01a1);
			strSearch.append("' and i9999=");
			strSearch.append(recid);
			strSearch.append(" FOR UPDATE");
			
			strInsert.append("update  ");
			strInsert.append(tableName);
			strInsert.append(" set ole=EMPTY_BLOB() where e01a1='");
			strInsert.append(e01a1);
			strInsert.append("' and i9999=");
			strInsert.append(recid);
		}
		InputStream in = new FileInputStream(file);
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.conn);
		Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),in); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		return blob;
	}
	
	 /**
     * 条件更新时的附件归档
     * @param basepre
     * @param a0100
     * @param archive_attach_to
     * @param destA0100Map
     * @param flagid
     * @param rowSet
     * @param file
     * @param i9999s
     * @param isRemoveAtta
     * @throws GeneralException
     */
    private void saveMultimediaToFile(String basepre, String a0100, String archive_attach_to, HashMap destA0100Map,
			int flagid, RowSet rowSet, File file, String i9999s, boolean isRemoveAtta,int state) throws GeneralException{
    	try {
			String i9999arr [] = i9999s.split(",");
			for(int i=0;i<i9999arr.length;i++) {
				int i9999 = Integer.parseInt(i9999arr[i]);
				this.saveMultimediaToFile(basepre, a0100, archive_attach_to,destA0100Map, flagid, rowSet, file, i9999,isRemoveAtta,state);
			}
    	}catch (Exception e) {
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
		}
	}
    public void SendEmailToBeginUser(TemplateTableBo tablebo,RecordVo ins_vo,ContentDAO dao,WF_Instance ins,String tabid) throws GeneralException{
        try{
         	if (!"true".equalsIgnoreCase(tablebo.getNotice_initiator())){//发送邮件并且在单据提交入库时要通知到流程发起人
	    		return;
	    	}
	    	if (!(tablebo.isBemail() || tablebo.isBsms())){
	    		return;
	    	}
            
            ins_vo=dao.findByPrimaryKey(ins_vo);
            if(ins_vo==null){
                throw new GeneralException(ResourceFactory.getProperty("error.wf.notins_id"));
            }

            int actorType=ins_vo.getInt("actor_type");
            String tempactorid=ins_vo.getString("actorid");
            String Titlename="";
            String context="";
            String a0100="";
            String nbase="";
            String sql="";
            //EMailBo bo=null;

            if(actorType==4){//说明是业务用户
                sql="select * from operuser where userName='"+tempactorid+"'";
                RowSet frowset=dao.search(sql);
                if(frowset.next()){
                    a0100=frowset.getString("a0100");
                    nbase=frowset.getString("nbase");
                }
            }else{//自助用户
                if(tempactorid.length()>3){
                    nbase=tempactorid.substring(0,3);
                    a0100=tempactorid.substring(3);
                }
            }
            
            ArrayList attachList = null;
            Titlename=tablebo.getTable_vo().getString("name");//得到模版的名称 也就是邮件头
            context=Titlename;
            String template_initiator="";
            SendMessageBo sendBo=null;
            template_initiator=(String)tablebo.getTemplate_initiator();
            sendBo=new SendMessageBo(this.conn,tablebo.getUserview());
            if(ins.getTask_vo().getInt("task_id")!=0)
                sendBo.setTask_id(String.valueOf(ins.getTask_vo().getInt("task_id")));
            sendBo.setIns_id(String.valueOf(ins_vo.getInt("ins_id")));
            sendBo.setSp_flag("0");
            if(template_initiator!=null&&template_initiator.trim().length()>0) //获得审批模板
            {
                LazyDynaBean mailInfo=sendBo.getTemplateMailInfo(template_initiator);
                context=(String)mailInfo.get("content");
                //zxj 20141023 邮件模板附件
                attachList = (ArrayList)mailInfo.get("attach"); 
            }
            
            String _context = context.replace("\r\n","<br>").replace("\n","<br>").replace("\r","<br>");
            LazyDynaBean _bean = null;
            //这地方好像颠倒了 先改过来 wangrd 2015-05-27 
            if(template_initiator==null||template_initiator.trim().length()==0){
                _bean = sendBo.getTileAndContent("3", Titlename, _context, tempactorid, tablebo.getUserview(), tabid, ins.getObjs_sql(), null,tablebo.getInfor_type());//单据结束通知发起人,opt传3,让信息中的提示得到的是已被批准
            }else{
                _bean = sendBo.getEmailBean("2",null,Titlename,_context,tempactorid,tablebo.getUserview(),tabid,ins.getObjs_sql());
            }
            String objectId = nbase+a0100;
            _bean.set("objectId", objectId);
            if(attachList!=null&&attachList.size()>0){
                _bean.set("attachList", attachList);
            }
            //_context = (String)_bean.get("context");
            //context=_context;
            if (tablebo.isBemail() ){ //发送邮件
                AsyncEmailBo newEmailBo = null;
                try{
                    //bo=new EMailBo(this.getFrameconn(),true,"");
                    newEmailBo = new AsyncEmailBo(this.conn, this.userView);
                }
                catch(Exception e){
                    throw new GeneralException(ResourceFactory.getProperty("邮箱服务器配置错误，通知发起人失败"));
                }
            	newEmailBo.send(_bean);
            	//发送微信信息
            	new TemplateUtilBo(this.conn,this.userView).sendWeixinMessageFromEmail(_bean);
            }
            
            if (tablebo.isBsms() ){ //发送短信
				SmsBo smsbo=new SmsBo(this.conn);
				smsbo.sendMessage(this.userView,objectId,_context);
            }
                
        }catch(Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    
    }
    public RecordVo autoApplyTask(WF_Instance ins, RecordVo ins_vo,String content,String pri,String sp_yj,String actorName,HashMap otherParaMap,TemplateTableBo tablebo,ContentDAO dao)
 	{
 		WF_Actor wf_actor=null; 
 		try
 		{
 		
            //获得下一个流程节点任务
            RecordVo nextTaskVo=ins.getTask_vo();  
            if(nextTaskVo==null)
            	return ins_vo;
            

            ArrayList nextTaskVoList=ins.getNextTaskVoList();
            if(nextTaskVoList.size()==0||nextTaskVoList.size()==1)
            {
            	nextTaskVoList=new ArrayList();
            	nextTaskVoList.add(nextTaskVo);
            }
            for(int i=0;i<nextTaskVoList.size();i++)
            {
            	nextTaskVo=(RecordVo)nextTaskVoList.get(i);
            
            
	            int whileNum=0;
	            int ins_id=ins_vo.getInt("ins_id");
	            int task_id=nextTaskVo.getInt("task_id");
	            int tabId=ins_vo.getInt("tabid");
	            //校验必填项
	            boolean isMustFill = this.checkMustFill(tablebo,task_id);
	            if(isMustFill) {
	            	break;
	            }
	            while(true)
	            {
	            	whileNum++;
	            	if(whileNum>10)//最多连续10个自动提交，防止死循环
	            		break;
	            	if(nextTaskVo==null)
	            		break;
	            	int _task_id=nextTaskVo.getInt("task_id");
	            	if(task_id==_task_id&&whileNum>1)
	            		break;
	            	String actor_type=nextTaskVo.getString("actor_type");
	            	String actorid=nextTaskVo.getString("actorid"); 
	        		ArrayList rolelist= userView.getRolelist();//角色列表
	        		boolean isSelf=false; //判断下一个节点是否是自己
	        		
	            	if("2".equals(actor_type))
	            	{
	            		                       		
	            		if(rolelist.contains(actorid))
	            		{ 
	            			 isSelf=roleIsSelf(tabId,nextTaskVo.getInt("node_id")+"",_task_id,dao,actorid,ins_id,tablebo ); 
	            		} 
	            	}
	            	else if("1".equals(actor_type)&&actorid.equalsIgnoreCase(this.userView.getDbname()+this.userView.getA0100()))
	            		isSelf=true;
	            	else if("4".equals(actor_type)&&actorid.equalsIgnoreCase(this.userView.getUserName()))
	            		isSelf=true;
	            	
	            	if(isSelf)
	    			{
	    				 wf_actor = new WF_Actor(actorid, nextTaskVo.getString("actor_type"));
	    		         wf_actor.setContent("【自动审批】 "+content);
	    		         wf_actor.setEmergency(pri);
	    		         wf_actor.setSp_yj(sp_yj);
	    		         wf_actor.setActorname(actorName);
	    		         wf_actor.setBexchange(false);
	    		        
	    		         dao.update("update t_wf_task_objlink set submitflag=1 where task_id="+_task_id); 
	    		         
	    		         // 待办信息 
	                     String _pendingCode = "HRMS-" + PubFunc.encrypt(_task_id+"");
	                     otherParaMap.put("pre_pendingID", _pendingCode);
	                     ins.setOtherParaMap(otherParaMap);
	                     ins.setObjs_sql(ins.getObjsSql(ins_id, _task_id, 3, ""+tabId, this.userView, ""));// 作用是 
	                     ins.setIns_id(ins_id);       
	    		         
	    				ins.createNextTask(ins_vo, wf_actor,nextTaskVo.getInt("task_id"), this.userView);// 在这个函数里面执行了expDataIntoArchive()
	                    ins_vo =dao.findByPrimaryKey(ins_vo);//重新获取vo lis 21060825
	                    nextTaskVo=ins.getTask_vo();
	                   
	                    if(ins_vo.getString("finished").equalsIgnoreCase(String.valueOf(NodeType.TASK_FINISHED))){//下一个流程结点是结束结点
	                    	break; 
	                    }
	                    else
	                    	 ins.updateApproveOpinion(ins_vo, wf_actor, this.userView,_task_id);
	    			}
	    			else 
	    				break; 
	            	
	            }
            }
          
 		}
 		catch(Exception e)
 		{
 			e.printStackTrace();
 		}
 		return ins_vo;
 	}
    /**
 	 * 检验必填，如果有必填项没有填写，则直接跳过不在自动审批
 	 * @param tablebo
 	 * @param task_id
 	 * @return
 	 */
	private boolean checkMustFill(TemplateTableBo tablebo, int task_id) {
		boolean isMustFill = false;
		try {          
	        String tableName="templet_"+tablebo.getTabid();
			ArrayList fieldlist = tablebo.getAllFieldItem("0");
			tablebo.checkMustFillItem_batch(tableName, fieldlist,task_id+"");
		} catch (Exception e) {
			String message=e.toString();
			if(message.indexOf("指标未填写")!=-1){
				isMustFill = true;
			}
		}
		return isMustFill;
	}
	/**
	 * 判断流程某节点下的角色是否是自己审批
	 * @param tabId 模板ID
	 * @param nodeid 节点ID
	 * @param task_id 任务ID
	 * @param dao
	 * @param actorid 审批人
	 * @param ins_id 实例ID
	 * @param tablebo
	 * @return
	 */
	 private boolean  roleIsSelf(int tabId,String nodeid,int task_id,ContentDAO dao,String actorid,int ins_id,TemplateTableBo tablebo )
	    {
	    	boolean isSelf=false;
	        String userStr="";
	        RowSet rowSet2=null;
	        try
	        { 
	        	String scope_field="";
				String containUnderOrg="0"; //包含下属机构
				ArrayList valueList=new ArrayList();
				valueList.add(new Integer(tabId));
				valueList.add(new Integer(nodeid));
				rowSet2=dao.search("select * from t_wf_node where tabid=? and node_id=?",valueList);
				String ext_param="";
				if(rowSet2.next())
						ext_param=Sql_switcher.readMemo(rowSet2,"ext_param"); 
				if(ext_param!=null&&ext_param.trim().length()>0)
				{
						Document doc=PubFunc.generateDom(ext_param); 
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
							for(int j=0;j<childlist.size();j++)
							{
								Element element=(Element)childlist.get(j);
								if(element!=null&&element.getText()!=null&&element.getText().trim().length()>0)
								{
									scope_field=element.getText().trim();
									if(element.getAttribute("flag")!=null&& "1".equals(element.getAttributeValue("flag").trim()))
										containUnderOrg="1";
								}
							}
						}
				}
				if(scope_field==null)
						scope_field=""; 
	            RowSet rowSet=dao.search("select * from t_sys_role where role_id=?",Arrays.asList(new Object[] {actorid}));
	            if(rowSet.next())
	            {
	                    int role_property=rowSet.getInt("role_property");
	                    if(role_property==9||role_property==10||role_property==11||role_property==12||role_property==13)
	                    {
	                            WorkflowBo workflowBo=new WorkflowBo(conn,tabId,this.userView);
	                         
	                            LazyDynaBean bean=workflowBo.getFromNodeid_role(ext_param,ins_id,dao,task_id,"0","");
	                            String sql=""; 
	                            HashMap a_map=workflowBo.getSuperSql(role_property,tablebo.getRelation_id(),bean);
	                            if(a_map.size()==0)
	                                        throw new GeneralException(ResourceFactory.getProperty("general.template.wf_node.nosprelation"));
	                             sql=(String)a_map.get("sql");  
	                             rowSet=dao.search(sql);
	                             if(rowSet.next()){
	                                    if("1".equals(rowSet.getString("actor_type")))  //自助
	                                    {
	                                        if((this.userView.getDbname()+this.userView.getA0100()).equalsIgnoreCase(rowSet.getString("mainbody_id")))
	                                        	isSelf=true;
	                                    }else{ //业务用户
	                                        if(this.userView.getUserName().equalsIgnoreCase(rowSet.getString("mainbody_id")))
	                                        	isSelf=true;
	                                    }
	                                    
	                              }
	                    }
	                    else //非特殊角色
	                    {
	                    	String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板
	                    	if ("1".equals(role_property)){//部门领导
	    						String e0122=this.userView.getUserDeptId();
	    						if (e0122!=null &&e0122.length()>0){
	    							//operOrg="UN"+e0122;//不知道为什么要写成UN
	    							operOrg="UM"+e0122;//改成UM，应该是对的 20170930
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
	                    	
	                    	if(scope_field.toUpperCase().indexOf("SUBMIT")!=-1||"parentid_2".equals(scope_field)|| "parentid_1".equals(scope_field)) //单独处理
	        				{
	                    		if(scope_field.toUpperCase().indexOf("SUBMIT")!=-1)
	                    			  isSelf=true;
	        				}
	                    	else
	                    	{
	                    		if("UN`".equalsIgnoreCase(operOrg)||scope_field.trim().length()==0)
	                    		{
	                    			isSelf=true;
	                    			ArrayList tempList=new ArrayList();
									Timestamp dateTime = new Timestamp((new Date()).getTime());
									tempList.add(dateTime);
									tempList.add(userView.getUserName()); 
									tempList.add(task_id);
	                    			dao.update("update t_wf_task_objlink set locked_time=?,username=? where   task_id=?",tempList );
	                    		}
	                    		else
	                    		{
	                    			String from_where_sql="";
	                    			String[] temps=scope_field.split("_");
	    							String itemid=temps[0].toLowerCase(); 
	    							FieldItem _item=DataDictionary.getFieldItem(itemid);
	    							String codesetid=_item.getCodesetid();
	    							if(operOrg!=null && operOrg.length() > 3)
	    							{
	    								StringBuffer tempSql = new StringBuffer(""); 
	    								String[] temp = operOrg.split("`");
	    								for (int i = 0; i < temp.length; i++) {
	    									if("1".equals(containUnderOrg))
	    									{
	    										tempSql.append(" or t."+scope_field+" like '" + temp[i].substring(2)+ "%'");		
	    									}
	    									else
	    									{
	    										if ("UN".equalsIgnoreCase(codesetid)&& "UN".equalsIgnoreCase(temp[i].substring(0, 2)))
	    											tempSql.append(" or t."+scope_field+"='" + temp[i].substring(2)+ "'");
	    										else if ("UM".equalsIgnoreCase(codesetid)&& "UM".equalsIgnoreCase(temp[i].substring(0, 2)))
	    											tempSql.append(" or t."+scope_field+" like '" + temp[i].substring(2)+ "%'");				
	    									}
	    								}
	    								
	    								if(tempSql.length()==0)
	    								{
	    									if(userView.getA0100()!=null&&userView.getA0100().trim().length()>0) //自助用户没定义管理范围，按所在单位、部门控制 2014-04-01 dengcan
	    									{
	    										if("UN".equalsIgnoreCase(codesetid))
	    										{
	    											if("1".equals(containUnderOrg))
	    												tempSql.append(" or t."+scope_field+" like '"+userView.getUserOrgId()+"%'");
	    											else
	    												tempSql.append(" or t."+scope_field+"='"+userView.getUserOrgId()+"'");
	    										}
	    										else if ("UM".equalsIgnoreCase(codesetid)){
	    										    if(userView.getUserDeptId()!=null&&userView.getUserDeptId().trim().length()>0){
	    										        tempSql.append(" or t."+scope_field+" like '"+userView.getUserDeptId()+"%'");
	    										    }else{
	    										        tempSql.append(" or 1=2 ");
	    										    }
	    											
	    										}
	    									}
	    								}
	    								
	    								if(tempSql.toString().trim().length()==0)
	    									tempSql.append(" or 1=2 ");
	    								
	    								from_where_sql+=" and ( " + tempSql.substring(3) + " ) ";
	    							}
	    							else
	    							{
	    								if(userView.getA0100()!=null&&userView.getA0100().trim().length()>0) // 2014-04-01 dengcan
	    								{
	    									if("UN".equalsIgnoreCase(codesetid))
	    									{
	    										if("1".equals(containUnderOrg))
	    											from_where_sql+=" and t."+scope_field+" like '"+userView.getUserOrgId()+"%'";
	    										else
	    											from_where_sql+=" and t."+scope_field+"='"+userView.getUserOrgId()+"'";
	    									}
	    									else if ("UM".equalsIgnoreCase(codesetid)){
	    									    if(userView.getUserDeptId()!=null&&userView.getUserDeptId().trim().length()>0){
	    									    	from_where_sql+=" and t."+scope_field+" like '"+userView.getUserDeptId()+"%'";
	    									    }else{
	    									    	from_where_sql+=" and 1=2 ";
	    									    }
	    									}
	    								}
	    								else
	    									from_where_sql+=" and 1=2 ";
	    							}
	    							
	    							rowSet=dao.search("select twt.* from templet_"+tabId+" t,t_wf_task_objlink twt where   twt.seqnum=t.seqnum and twt.ins_id=t.ins_id     and t.ins_id=? and twt.task_id=? and "+Sql_switcher.isnull("twt.state","0")+"=0   "+from_where_sql,Arrays.asList(new Object[] {new Integer(ins_id),new Integer(task_id)}));
	    							int n=0;
	    							HashMap dataMap=new HashMap();
	    							ArrayList updList=new ArrayList();
	    							while(rowSet.next())
	    							{
	    								n++;
	    								/*
	    								int num=rowSet.getInt(1);
	    								if(num>0)
	    									 isSelf=true;
	    									 */
	    								int node_id=rowSet.getInt("node_id");
	    								if(dataMap.get(node_id+task_id)==null)
	    								{
	    									dao.update("update t_wf_task_objlink set special_node=1 where task_id="+task_id+" and node_id="+node_id);
	    									dataMap.put(node_id+task_id,"1");
	    								}
	    								
	    								ArrayList tempList=new ArrayList();
										Timestamp dateTime = new Timestamp((new Date()).getTime());
										tempList.add(dateTime);
										tempList.add(userView.getUserName());
										tempList.add(rowSet.getString("seqnum"));
										tempList.add(new Integer(rowSet.getString("task_id")));
										updList.add(tempList);
	    							} 
	    							if(n>0)
	    							{
	    								if(updList.size()>0)
	    								{ 
	    									dao.batchUpdate("update t_wf_task_objlink set locked_time=?,username=? where seqnum=? and task_id=?",updList );
	    								}
	    								isSelf=true;
	    							}
	    							
	    							
	    							
	                    		} 
	                    	} 
	                    }
	                     
	                }
	                if(rowSet!=null)
	                    rowSet.close();
	            
	        }
	        catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	        finally
	        {
	        	PubFunc.closeDbObj(rowSet2);
	        }
	        return isSelf;
	    }
	

}


