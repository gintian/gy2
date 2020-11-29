package com.hjsj.hrms.module.recruitment.recruitflow.businessobject;

import com.hjsj.hrms.module.recruitment.util.RecruitPrivBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * <p>Title:RecruitflowBo.java</p>
 * <p>Description>:招聘流程环节业务类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-1-22</p>
 * <p>@author:dengcan</p>
 * <p>@version: 7.x</p>
 */
public class RecruitFlowLink {
	private Connection conn=null;
	private String link_id="";
	private String z0301="";
	private String custom_name="";
	//招聘环节  01:人力筛选 02:部门筛选 03:人才测评 04:面试 05:背景调查 06:录用审批 07:offer  08:体检 09:入职 
	private String node_id="";  
	private int valid=1;  //1:有效
	private UserView userView ; 
	private String posId = "";
	private String flowId = "";
	private ArrayList statusList=new ArrayList();  //流程环节包含的状态
	private ArrayList functionList=new ArrayList(); //流程环节包含的操作项（ 分割符|功能按钮 ） 
	private ArrayList resumeFunctionList=new ArrayList();//简历操作集合

	public RecruitFlowLink(String link_id,String z0301,Connection conn)
	{
		this.conn=conn;
		this.link_id=link_id;
		this.z0301=z0301;
		initObject();
		
	}
	public RecruitFlowLink(UserView userView,String posId,String flowId,String link_id,String z0301,Connection conn)
    {
		 this.conn=conn;
    	 this.link_id=link_id;
    	 this.z0301=z0301;
    	 this.userView = userView; 
    	 this.posId = posId;
    	 this.flowId = flowId;
    	 initObject();
    	 
    }
	
	
	public RecruitFlowLink(String link_id,Connection conn)
    {
		 this.conn=conn;
    	 this.link_id=link_id; 
    	 try
 		 {
    	 	ContentDAO dao = new ContentDAO(this.conn);
    	 	RecordVo vo=new RecordVo("zp_flow_links");
			vo.setString("id",this.link_id);
			vo=dao.findByPrimaryKey(vo);
			this.custom_name=vo.getString("custom_name");
			this.node_id=vo.getString("node_id");
			this.valid=vo.getInt("valid"); //1：启用
 		 }
 		 catch (Exception e) {
             e.printStackTrace();
              
         }
    	 
    }
	
	/**
	 * 初始化对象
	 * @throws GeneralException
	 */
	private void initObject()  
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo vo=new RecordVo("zp_flow_links");
			vo.setString("id",this.link_id);
			vo=dao.findByPrimaryKey(vo);
			this.custom_name=vo.getString("custom_name");
			this.node_id=vo.getString("node_id");
			this.valid=vo.getInt("valid"); //1：启用
			this.statusList=getFlowStatusList(this.link_id,this.z0301,2);
			if(this.userView!=null){
				this.functionList=getFlowFunctionList(this.userView, this.posId, this.flowId,this.link_id,2);
				this.resumeFunctionList=getResumeFunctionList(this.link_id, 2);
			}
		}
		catch (Exception e) {
            e.printStackTrace();
             
        }
	}
	
	/**
	 * 获得某环节下可用的操作
     * @param flow_id 招聘流程id 
     * @param z0301   招聘职位id
     * @param flag    1:包含所有环节  2：只包含有效环节  
	 * @return
	 */
	private ArrayList getFlowFunctionList(UserView userView,String posId,String flowId,String link_id,int flag)throws GeneralException 
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		try
		{
			RecruitPrivBo privBo = new RecruitPrivBo();
			if(privBo.hasFlowLinkPriv(this.conn, this.userView, posId, flowId, link_id)){
				ContentDAO dao = new ContentDAO(this.conn);
	    		ArrayList valueList=new ArrayList();
	    		String sql="select zff.*,zfl.node_id from  zp_flow_functions zff,zp_flow_links zfl where zff.link_id=zfl.id and zff.link_id=? ";
	    		valueList.add(link_id);
	    		if(flag==2) 
	    			sql+=" and zff.valid=1";  
	    		sql+=" order by zff.seq";
	    		rowSet=dao.search(sql,valueList);
	    		String groupSql = "select seq from zp_flow_functions a" +
	    		" where seq in " +
	    		"(select  max( seq) from zp_flow_functions b where a.group_number = b.group_number" +
	    		" and link_id=? group by group_number) " +
	    		"and link_id=?";
	    		valueList.add(link_id);
	    		//查询某一组内最大seq，用于进行分组
	    		RowSet grouprs = dao.search(groupSql,valueList);
	    		ArrayList group=new ArrayList();
	    		while(grouprs.next())
	    		{
	    			group.add(grouprs.getString("seq"));
	    		}
	    		String hiddenButton = "arrangementNotice`resumeEvaluate`uploadingAffix`sendRzNotice`";
	    		while(rowSet.next())
	    		{
	    			String functionName = rowSet.getString("function_str")+"`";
	    			if(hiddenButton.indexOf(functionName)!=-1)
	    			{
	    				//暂时隐藏面试通知、面试评价、发送入职通知按钮
	    				continue;
	    			}
	    			String node_id=rowSet.getString("node_id");
	    			String function_str=rowSet.getString("function_str")!=null?rowSet.getString("function_str"):"";
	    			String valid=rowSet.getString("valid");  //1：启用 
	    			String group_number=rowSet.getString("group_number");  //1：启用 
	    			String custom_name=StringUtils.isNotEmpty(rowSet.getString("custom_name"))?rowSet.getString("custom_name"):rowSet.getString("sys_name");
	    			if(custom_name.length()==0) //如果没有自定义名称，采用系统名称
	    			{
	    				custom_name=""; 
	    			}
	    			if("0".equals(valid))//操作当前未启用
	    				continue;
	    			String seq=rowSet.getString("seq");
	    			ButtonInfo button = new ButtonInfo(custom_name,"Global.operation");
	    			button.setText(custom_name);
	    			button.setParameter("functions", function_str);
	    			button.setParameter("link_id",link_id );
	    			button.setParameter("node_id",node_id );
	    			button.setParameter("custom_name",custom_name );
	    			button.setParameter("valid",valid );
	    			button.setParameter("group_number",group_number );
	    			button.setParameter("seq",seq );
	    			//改用表格控件提供button后list中加入button实体类
	    			/*LazyDynaBean bean=new LazyDynaBean();
	    			bean.set("link_id",link_id); 
	    			bean.set("node_id",node_id);
	    			bean.set("custom_name",custom_name);
	    			bean.set("seq",seq);
	    			bean.set("valid",valid);
	    			bean.set("function_id",function_id);
	    			bean.set("function_str",function_str); */
	    		 
	    			list.add(button);
	    			//添加分组
	    			for(int i=0;i<group.size();i++)
	    			{
	    				if(seq.equals(group.get(i)))
	    				{
	    					list.add("-");    					
	    				}
	    			}
	    		}
			}
		}
		catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
		finally
    	{
    		PubFunc.closeDbObj(rowSet);
    	}
		
		return list;
	}
	
	
	
	/**
	 * 获得流程某环节下涉及的状态信息
     * @param flow_id 招聘流程id 
     * @param z0301   招聘职位id
     * @param flag 1:包含所有环节  2：只包含有效环节
	 * @return
	 */
	private ArrayList getFlowStatusList(String link_id,String z0301,int flag)throws GeneralException 
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		ArrayList valueList=new ArrayList();
    		String sql="select * from  zp_flow_status where link_id=? ";
    		valueList.add(link_id);
    		if(flag==2) 
    			sql+=" and valid=1";  
    		sql+=" order by seq";
    		rowSet=dao.search(sql,valueList);
			String str = "";
    		while(rowSet.next())
    		{
    			String status=rowSet.getString("status");
    			str = status.substring(0,2);
    			String valid=rowSet.getString("valid");
    			String custom_name=rowSet.getString("custom_name")!=null?rowSet.getString("custom_name"):"";
    			if(custom_name.length()==0) //如果没有自定义名称，采用代码名称
    				custom_name=AdminCode.getCodeName("36", status); 
    			String seq=rowSet.getString("seq");
    			LazyDynaBean bean=new LazyDynaBean();
    			bean.set("link_id",link_id);
    			bean.set("status",status);
    			bean.set("custom_name",custom_name);  //名称
    			bean.set("seq",seq);      // 排序号
    			bean.set("valid",valid);  //1：启用
    			// 如果z0301!=0 ,获得职位某环节下  新候选人数/所有候选人数
    			if(z0301!=null&&z0301.length()>0)
    			{ 
    				bean.set("all_number","("+new Integer(getCandidateNumber(z0301,link_id,status))+")"); //人数
    			}
    			list.add(bean);
    		}

    		LazyDynaBean bean1=new LazyDynaBean();
			bean1.set("link_id",link_id);
			bean1.set("custom_name","全部");  //名称
			bean1.set("seq","");      // 排序号
			bean1.set("valid","1");  //1：启用
			bean1.set("all_number",""); //人数
    		if(!"".equals(str))
			{
				bean1.set("status",str);
			}else{
				bean1.set("status","0");
			}
    		list.add(0, bean1);
    	}
    	catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    	finally
    	{
    		PubFunc.closeDbObj(rowSet);
    	}
    	return list;
	}
	
	
	
	 /**
     * 获得职位环节某状态下候选人数 
     * @param link_id:环节id
     * @param z0301:职位id
     * @param status:状态id
     * @return
     */
    public int getCandidateNumber(String z0301,String link_id,String status)throws GeneralException 
    {
    	int number=0;
    	RowSet rowSet=null;
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		String sql="select count(a0100) from zp_pos_tache where zp_pos_id=? and link_id=? and resume_flag=? and status<>0 ";
    		ArrayList valueList=new ArrayList();
    		valueList.add(z0301);
    		valueList.add(link_id);  
    	    valueList.add(status); 
    		rowSet=dao.search(sql,valueList);
    		if(rowSet.next())
    		{
    			number=rowSet.getInt(1);
    		}
    	}
    	catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    	finally
    	{
    		PubFunc.closeDbObj(rowSet);
    	}
    	return number;
    }
	
    private ArrayList getResumeFunctionList(String link_id,int flag)throws GeneralException 
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		try
		{
			RecruitPrivBo privBo = new RecruitPrivBo();
			if(privBo.hasFlowLinkPriv(this.conn, this.userView, this.posId, this.flowId, link_id)){
				ContentDAO dao = new ContentDAO(this.conn);
	    		ArrayList valueList=new ArrayList();
	    		String sql="select zff.*,zfl.node_id from  zp_flow_functions zff,zp_flow_links zfl where zff.link_id=zfl.id and zff.link_id=? ";
	    		valueList.add(link_id);
	    		if(flag==2) 
	    			sql+=" and zff.valid=1";  
	    		sql+=" order by zff.seq";
	    		rowSet=dao.search(sql,valueList);
	    		String groupSql = "select seq from zp_flow_functions a" +
	    		" where seq in " +
	    		"(select  max( seq) from zp_flow_functions b where a.group_number = b.group_number" +
	    		" and link_id=? group by group_number) " +
	    		"and link_id=?";
	    		valueList.add(link_id);
	    		//查询某一组内最大seq，用于进行分组
	    		RowSet grouprs = dao.search(groupSql,valueList);
	    		ArrayList group=new ArrayList();
	    		while(grouprs.next())
	    		{
	    			group.add(grouprs.getString("seq"));
	    		}
	    		String hiddenButton = "arrangementNotice`resumeEvaluate`sendRzNotice`";
	    		while(rowSet.next())
	    		{
	    			String functionName = rowSet.getString("function_str")+"`";
	    			if(hiddenButton.indexOf(functionName)!=-1)
	    			{
	    				//暂时隐藏面试通知、面试评价、上传面试评价记录、发送入职通知按钮
	    				continue;
	    			}
	    			String node_id=rowSet.getString("node_id");
	    			String function_str=rowSet.getString("function_str")!=null?rowSet.getString("function_str"):"";
	    			String valid=rowSet.getString("valid");  //1：启用 
	    			String group_number=rowSet.getString("group_number");  //1：启用 
	    			String custom_name=rowSet.getString("custom_name")!=null?rowSet.getString("custom_name"):rowSet.getString("sys_name");
	    			if(custom_name.length()==0) //如果没有自定义名称，采用系统名称
	    			{
	    				custom_name=""; 
	    			}
	    			String seq=rowSet.getString("seq");
	    			LazyDynaBean bean=new LazyDynaBean();
	    			bean.set("link_id",link_id); 
	    			bean.set("node_id",node_id);
	    			bean.set("custom_name",custom_name);
	    			bean.set("seq",seq);
	    			bean.set("valid",valid);
	    			bean.set("function_str",function_str); 
	    		 
	    			list.add(bean);
	    		}
			}
		}
		catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
		finally
    	{
    		PubFunc.closeDbObj(rowSet);
    	}
		
		return list;
	}
	
    private String getFunctionName(String function_id,String nodeid)
	{
		String functionName="";
		String [][] function_array=new String[9][15];
		function_array[0][0]=function_array[1][0]="简历筛选";
		function_array[0][1]=function_array[1][1]="通过";
		function_array[0][2]=function_array[1][2]="淘汰";
		function_array[0][3]=function_array[1][3]="备选";
		function_array[0][4]=function_array[1][4]="转新阶段";
		function_array[0][5]=function_array[1][5]="变更状态";
		function_array[0][6]=function_array[1][6]="转发简历邀请评价";
		function_array[0][7]=function_array[1][7]=function_array[2][7]="推荐给其他人";
		function_array[0][8]=function_array[1][8]=function_array[2][8]="推荐职位";
		function_array[0][9]=function_array[1][9]=function_array[2][9]="收藏简历";
		function_array[0][10]=function_array[1][10]=function_array[2][10]="转人才库";
		function_array[0][11]=function_array[1][11]=function_array[2][11]="转黑名单";
		
		function_array[2][0]=function_array[3][0]=function_array[4][0]="通过";
		function_array[2][1]=function_array[3][1]=function_array[4][1]="淘汰";
		function_array[2][2]=function_array[3][2]=function_array[4][2]="备选";
		function_array[2][3]=function_array[3][3]=function_array[4][3]="转新阶段";
		function_array[2][4]=function_array[3][4]=function_array[4][4]="变更状态"; 
		function_array[2][5]="邀请参加人才测评"; 
		function_array[2][6]="上传测评结果";
		function_array[3][5]="面试安排";
		function_array[3][6]="面试通知";
		function_array[3][7]="面试评价";
		function_array[3][8]="上传面试评价记录";
		function_array[3][9]="推荐给其他人";
		function_array[3][10]="推荐职位";
		function_array[3][11]="收藏简历";
		function_array[3][12]="转人才库";
		function_array[3][13]="转黑名单";
		
		function_array[4][5]="上传背景调查资料";
		function_array[4][6]="推荐给其他人";
		function_array[4][7]="推荐职位";
		function_array[4][8]="收藏简历";
		function_array[4][9]="转人才库";
		function_array[4][10]="转黑名单";
		
		return functionName;
	}

	public String getLink_id() {
		return link_id;
	}

	public String getCustom_name() {
		return custom_name;
	}

	public String getNode_id() {
		return node_id;
	}

	public int getValid() {
		return valid;
	}

	public ArrayList getStatusList() {
		return statusList;
	}

	public ArrayList getFunctionList() {
		return functionList;
	}

	public ArrayList getResumeFunctionList(){
		return resumeFunctionList;
	}
}
