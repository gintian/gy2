package com.hjsj.hrms.module.recruitment.recruitprocess.businessobject;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.module.recruitment.util.RecruitPrivBo;
import com.hjsj.hrms.module.recruitment.util.ZpPendingtaskBo;
import com.hjsj.hrms.module.recruitment.util.transaction.SendMsgIsSuccess;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class FunctionRecruitBo {

	private Connection conn=null;
    private UserView userview;
    
    public FunctionRecruitBo(Connection conn, UserView userview)
    {
    	 this.conn=conn;
    	 this.userview=userview;
    }
    /**
     * 根据当前阶段，查出对应的操作id值
     * @param link_id 流程id
     * @param function_str 方法名
     * @param stageList 流程集合
     * @return
     */
    public ArrayList getFunctionList(String link_id,String node_id,String function_str,ArrayList stageList)
    {
    	ArrayList functionList = new ArrayList();
    	ArrayList list = this.functionList();
    	if("toStage".equalsIgnoreCase(function_str))
    		list = stageList;
    	
    	//根据传入的方法名及流程id查询当前操作改变后的编码
    	if(node_id!=null&&function_str!=null&&!"".equalsIgnoreCase(node_id)&&!"".equalsIgnoreCase(function_str))
    	{
    		if(list.size()>0)
    		{
    			LazyDynaBean bean = new LazyDynaBean();
    			for(int i=0;i<list.size();i++)
    			{
    				bean = (LazyDynaBean)list.get(i);
    				//得到当前方法需改变的值
    				String stage_id = (String)bean.get("node_id");
    				if(node_id.equals(stage_id))
    				{
    					if("toStage".equalsIgnoreCase(function_str)){
    						String returnId = (String)bean.get("status");
        					functionList.add(link_id);
        					functionList.add(returnId);
    					}else{
    						String returnId = (String)bean.get(function_str);
        					functionList.add(link_id);
        					functionList.add(stage_id+returnId);
    					}
    					return functionList;
    				}
    				
    			}
    		}
    	}
    	return functionList;
    }
    
    
    /***
     * 修改候选人状态
     * @param employeeStatusList 需要的修改数据集合 [0]link_id  [1]resume_flag [2]zp_pos_id [3]a0100 [4]nbase 
     * @param real_nodeid 招聘环节ID 01:人力筛选 02:部门筛选 03:人才测评 04:面试 05:背景调查 06:录用审批 07:offer 08:体检09:入职 
     * @param function_str 当前操作
     * @author dengc
     * @return
     */
    public int[] changeStatus(ArrayList employeeStatusList,String real_nodeid,String function_str)
    {
    	int[] num=new int[employeeStatusList.size()];
    	ContentDAO dao =new ContentDAO(conn);
    	StringBuffer sql = new StringBuffer("update zp_pos_tache set ");
    	sql.append(" link_id=?");
    	sql.append(",resume_flag=?");
    	sql.append(" where zp_pos_id=? and a0100=? and Nbase=? "); 
    	try {
			num = dao.batchUpdate(sql.toString(), employeeStatusList);
			  
		 
			String resume_flag=(String)((ArrayList)employeeStatusList.get(0)).get(1);
			if("1003".equalsIgnoreCase(resume_flag)) //已入职
			{
				RecruitProcessBo  _recruitProcessBo=new RecruitProcessBo(this.conn,this.userview);
				//已入职,将招聘库信息同步至在职人员库,并且终止已录用人员应聘的其它职位
				_recruitProcessBo.StaffEntry(employeeStatusList);
				
			}
			if("toStage".equalsIgnoreCase(function_str))
			{
				this.deleteArrangeMsg(employeeStatusList, function_str);
			}
			//记录招聘过程中的关键数据，用于招聘分析，暂只记录录用阶段（录用）、Offer阶段（Offer通知）、入职阶段（入职）
			if("07".equals(real_nodeid)|| "08".equals(real_nodeid)|| "10".equals(real_nodeid))
			{
				recordZpProcess(employeeStatusList,real_nodeid);
			}
			//进入笔试状态插入笔试对应信息
			if("03".equals(real_nodeid))
			{
				for(int i=0;i<employeeStatusList.size();i++)
				{
					ArrayList list = (ArrayList)employeeStatusList.get(i);
					if(this.isSetValue(list.get(2).toString()))
					{						
						//设置考场安排信息
						this.setZpExamAssign(list.get(2).toString(), list.get(3).toString(), list.get(4).toString());
						//设置招聘考试成绩信息
						this.setZ63(list.get(2).toString(), list.get(3).toString(), list.get(4).toString());
					}
				}
			}
			//更新职位的候选人数、已录用人数
			PositionBo positionBo=new PositionBo(this.conn,dao,this.userview);
			HashSet set=new HashSet();
			for(Iterator t=employeeStatusList.iterator();t.hasNext();)
			{
				ArrayList values=(ArrayList)t.next();
				String z0301=(String)values.get(values.size()-3);
				set.add(z0301); 
			}
			for(Iterator t=set.iterator();t.hasNext();)
			{
				String z0301=(String)t.next();
				positionBo.saveCandiatesNumber(z0301,2); //进程中的候选人数：没被录用或淘汰的候选人个数
				positionBo.saveCandiatesNumber(z0301,4); //已录用的人数
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return num;
    }
    
    
    /**
     * 根据当前招聘环节，查出对应的操作负责人发送邮件
     * @param link_id 流程id
     * @param  node_id 方法名
     * @param stageList 流程集合
     * @param z0301  招聘流程id
     * @param resumeNumber 勾选简历的数目
     */
    public void getPrincipal(String linkId, String nodeId, String z0301, int resumeNumber, ArrayList functionList) 
    {
        RowSet rs = null;
        RowSet rsMember = null ;
        try {
            ContentDAO dao = new ContentDAO(conn);
            RecruitPrivBo bo = new RecruitPrivBo();
            ZpPendingtaskBo taskBo = new ZpPendingtaskBo(conn, userview);
            ArrayList list = new ArrayList();
            StringBuffer sql = new StringBuffer("SELECT Z0351, Z0381 FROM Z03 WHERE z0301 =?");
            list.add(z0301);
            String z0351 = "";
            String z0381 = "";
            rs = dao.search(sql.toString(),list);
            if (rs.next()) {
                z0351 = rs.getString("Z0351");
                z0381 = rs.getString("Z0381");
            }

            ArrayList personList = new ArrayList();
            ArrayList<String> taskPersons = new ArrayList();
            sql.setLength(0);
            sql.append("select a0100, nbase from zp_members where z0301=?");
            sql.append(" and member_type IN (1,2,3)");
            rsMember = dao.search(sql.toString(),list);
            String name = ConstantParamter.getLoginUserNameField().toLowerCase();
            String pwd = ConstantParamter.getLoginPasswordField().toLowerCase();
            while (rsMember.next()) {
                String staffNumber = rsMember.getString("nbase") + rsMember.getString("a0100");
                String userName = "";
                String passWord = "";
                
                RecordVo vo = new RecordVo(rsMember.getString("nbase") + "A01");
                if(StringUtils.isEmpty(rsMember.getString("a0100")))
                	continue;
                
                vo.setString("a0100", rsMember.getString("a0100"));
                if (!dao.isExistRecordVo(vo))
                	continue;
                
                if (vo != null) {
                    //用户账号和密码
                    userName=vo.getString(name);
                    passWord=vo.getString(pwd);
                    UserView recipientUser = new UserView(userName, passWord, this.conn);
                    recipientUser.canLogin(false);
                    boolean permissions = bo.hasFlowLinkPriv(conn, recipientUser, z0301, z0381, linkId);
                    if (permissions) {
                    	taskPersons.add(userName);
                        personList.add(staffNumber);
                    }
                }
            }  

            for (int x = 0; x < personList.size(); x++) {
            	this.sendPendingTask(taskBo, taskPersons.get(x), z0351, resumeNumber, functionList, z0301, z0381);
                this.sendEMail((String) personList.get(x), z0351, resumeNumber, functionList, z0301, z0381,true);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeResource(rs);
            PubFunc.closeResource(rsMember);
        }
    }
    
	/**
     * 根据收件人id，发送对应的邮件
     * @param tomails 收件人id
     * @param  Z0351  招聘职位名称
     * @param resumeNumber 勾选简历数目
     * @param functionList  招聘流程id与环节id集合
     * @param z0301 招聘职位id
     * @param Z0381 招聘流程编号
     */
    public void sendEMail(String tomails, String Z0351, int resumeNumber, ArrayList functionList, String z0301, String Z0381, boolean isSelfUser)
    {
    	RowSet rs = null;
        try {
            String linkId = (String) functionList.get(0);
            String nodeId = (String) functionList.get(1);
            String linkName = (String) functionList.get(2);
            String email = ConstantParamter.getEmailField().toLowerCase();
            RecordVo vo = null;
            ContentDAO dao = new ContentDAO(this.conn);
            StringBuffer buf = new StringBuffer(); //邮件内容
            String email_address = "";
            String etoken = "";
            EmailTemplateBo bo = new EmailTemplateBo(conn);
            if(isSelfUser) {
            	String tmpNbase = tomails.substring(0, 3);
                String tmpA0100 = tomails.substring(3);
                vo = new RecordVo(tmpNbase + "A01");
                vo.setString("a0100", tmpA0100);
                if (dao.isExistRecordVo(vo)) 
                {
                    if (vo != null)
                    {
                        //邮件地址
                        email_address = vo.getString(email);
                        String name = ConstantParamter.getLoginUserNameField().toLowerCase();
                        String pwd = ConstantParamter.getLoginPasswordField().toLowerCase();
                        //用户账号和密码
                        String userName = vo.getString(name);
                        String passWord = vo.getString(pwd);
                        etoken = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(userName + "," + passWord));
                        
                    }
                }
            }else{
    			StringBuffer sql = new StringBuffer(" select email , Password  from OperUser  where username = ? ");
    			rs = dao.search(sql.toString(),Arrays.asList(tomails));
    			if(rs.next())
    			{
    				email_address = rs.getString("email");
    				String passWord = rs.getString("Password");
    				etoken = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(tomails + "," + passWord));
    			}
            }
            
            if(!bo.isMail(email_address)) 
                return;

            buf.append("<br>您好:</br>");
            buf.append("<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            buf.append(Z0351);
            buf.append("职位有");
            buf.append(resumeNumber);
            buf.append("份简历进入"+linkName+"环节， 请及时");
            buf.append("<a href=");
            buf.append(this.userview.getServerurl());
            buf.append("/recruitment/recruitprocess/recruitprocesslist.do?b_query=link&encryptParam=");
            buf.append(PubFunc.encryption("link_id=" + linkId + "&node_id=" + nodeId));
            buf.append("&z0301=" + PubFunc.encrypt(z0301) + "&z0381=" + PubFunc.encrypt(Z0381) + "&appfwd=1&etoken=");
            buf.append(etoken);
            buf.append(">登录</a>处理</td>");

            SendMsgIsSuccess sendMsgIsSuccess=new SendMsgIsSuccess();
            /** 发送邮件 实现附件发送   */
            AsyncEmailBo emailBo = new AsyncEmailBo(this.conn, this.userview, sendMsgIsSuccess);
            LazyDynaBean emailBean = new LazyDynaBean();
            emailBean.set("toAddr", email_address);
            emailBean.set("subject", "招聘业务通知");
            emailBean.set("bodyText", buf.toString());
            emailBean.set("href", "");
            emailBean.set("hrefDesc", "");
            emailBean.set("a0100", this.userview.getA0100());
            emailBean.set("username", this.userview.getUserFullName());
            emailBo.send(emailBean);
            
        } catch(Exception e) {
            e.printStackTrace();
        }finally{
			PubFunc.closeResource(rs);
		}
    }
    
    
    /***
     * 当前操作为转新阶段时，删除面试安排信息
    * @Title:deleteArrangeMsg
    * @Description：
    * @author xiexd
    * @date 2016-1-18
    * @param employeeStatusList
    * @param function_str
     */
    private void deleteArrangeMsg(ArrayList employeeStatusList,String function_str)
    {
    	if(!"toStage".equalsIgnoreCase(function_str))
    	{
    		return;
    	}
    	RowSet rs = null;
    	try {
    		ContentDAO dao = new ContentDAO(conn);
			ArrayList tempList;
			StringBuffer a0100s = new StringBuffer();
			String nbase = "";
    		for(int i=0;i<employeeStatusList.size();i++)
    		{
    			tempList=(ArrayList)employeeStatusList.get(i);
    			a0100s.append("'"+tempList.get(3)+"',");
    			nbase = (String)tempList.get(4);
    		}
    		a0100s.setLength(a0100s.length()-1);
    		StringBuffer sql = new StringBuffer("select Z0501 from Z05 where A0100 in ("+a0100s.toString()+") and nbase='"+nbase+"'");
    		ArrayList Z0501s = new ArrayList();
    		rs = dao.search(sql.toString());
    		while(rs.next())
    		{
    			Z0501s.add(rs.getString("Z0501"));
    		}
    		
    		if(Z0501s.size()>0)
    		{
    			StringBuffer Z0501 = new StringBuffer();
    			for(int i=0;i<Z0501s.size();i++)
    			{
    				Z0501.append("'"+Z0501s.get(i)+"',");
    			}
    			Z0501.setLength(Z0501.length()-1);
    			//删除面试安排表
    			sql = new StringBuffer("delete from Z05 where Z0501 in ("+Z0501+")");
    			dao.delete(sql.toString(), null);
    			//删除面试官表
    			sql = new StringBuffer("delete from zp_examiner_arrange where Z0501 in ("+Z0501+")");
    			dao.delete(sql.toString(), null);
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
    }
    
    /**
     * 记录招聘过程中的关键数据，用于招聘分析，暂只记录录用阶段（录用）、Offer阶段（Offer通知）、入职阶段（入职）
     * @param employeeStatusList 需要的修改数据集合 [0]link_id  [1]resume_flag [2]zp_pos_id [3]a0100 [4]nbase 
     * @param real_nodeid 招聘环节ID 01:人力筛选 02:部门筛选 03:人才测评 04:面试 05:背景调查 06:录用审批 07:offer 08:体检09:入职 
     * @author dengc
     */
    private void recordZpProcess(ArrayList employeeStatusList,String real_nodeid)
    {
    	try
    	{
    		ContentDAO dao =new ContentDAO(conn);
    		ArrayList tempList=null;
    		String key_operation="";  //1:录用 ２：Offer通知  ３：入职
    		String function_str="";   //功能描述
    		if("07".equals(real_nodeid))
    		{
    			key_operation="1";
    			function_str="录用";
    		}
    		else if("08".equals(real_nodeid))
    		{
    			key_operation="2";
    			function_str="Offer通知";
    		}
    		else if("10".equals(real_nodeid))
    		{
    			key_operation="3";
    			function_str="入职";
    		}
    		ArrayList voList=new ArrayList();
    		for(int i=0;i<employeeStatusList.size();i++)
    		{
    			tempList=(ArrayList)employeeStatusList.get(i);
    			RecordVo vo=new RecordVo("zp_process_history");
    			IDGenerator idg=new IDGenerator(2,this.conn);	
    			String id=idg.getId("zp_process_history.id");
    			vo.setString("id",id);
    			vo.setString("z0301", (String)tempList.get(2));
    			vo.setString("link_id", (String)tempList.get(0)); 
    			vo.setString("key_operation", key_operation);
    			vo.setString("function_str", function_str);
    			vo.setString("a0100",(String)tempList.get(3));
    			vo.setString("nbase",(String)tempList.get(4));
    			vo.setDate("create_time",Calendar.getInstance().getTime());
    			vo.setString("create_user",this.userview.getUserName());
    			vo.setString("create_fullname",this.userview.getUserFullName());
    			voList.add(vo);
    		}
    		dao.addValueObject(voList);
    		
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
    
    
    /***
     * 各个流程中所有操作及状态
     * @return
     */
    public ArrayList functionList()
    {
    	ArrayList functionList = new ArrayList();
    	//人力资源筛选
    	LazyDynaBean HRbean = new LazyDynaBean();
    	HRbean.set("node_id", "01");//流程id
    	HRbean.set("toStage", "01");//转新阶段
    	HRbean.set("changeStatus", "02");//进行时
    	HRbean.set("passChoice", "03");//通过
    	HRbean.set("reserve", "04");//备选
    	HRbean.set("obsolete", "05");//淘汰
    	functionList.add(HRbean);
    	//部门筛选
    	LazyDynaBean UMbean = new LazyDynaBean();
    	UMbean.set("node_id", "02");//流程id
    	UMbean.set("toStage", "01");//转新阶段
    	UMbean.set("changeStatus", "02");//进行时
    	UMbean.set("passChoice", "03");//通过
    	UMbean.set("reserve", "04");//备选
    	UMbean.set("obsolete", "05");//淘汰
    	functionList.add(UMbean);
    	//笔试
    	LazyDynaBean writtenbean = new LazyDynaBean();
    	writtenbean.set("node_id", "03");
    	writtenbean.set("toStage", "01");//转新阶段
    	writtenbean.set("changeStatus", "03");//进行时
    	writtenbean.set("passChoice", "04");//通过
    	writtenbean.set("reserve", "05");//备选
    	writtenbean.set("obsolete", "06");//淘汰
    	functionList.add(writtenbean);
    	//面试、复试
    	LazyDynaBean auditionbean = new LazyDynaBean();
    	auditionbean.set("node_id", "05");//流程id
    	auditionbean.set("toStage", "01");//转新阶段
    	auditionbean.set("arrangement", "02");//安排面试
    	auditionbean.set("changeStatus", "03");//进行时
    	auditionbean.set("passChoice", "04");//通过
    	auditionbean.set("reserve", "05");//备选
    	auditionbean.set("obsolete", "06");//淘汰
    	functionList.add(auditionbean);
    	//背景调查
    	LazyDynaBean backgroundbean = new LazyDynaBean();
    	backgroundbean.set("node_id", "06");//流程id
    	backgroundbean.set("toStage", "01");//转新阶段
    	backgroundbean.set("passChoice", "02");//通过
    	backgroundbean.set("obsolete", "03");//淘汰
    	functionList.add(backgroundbean);
    	//录用审批
    	LazyDynaBean employmentbean = new LazyDynaBean();
    	employmentbean.set("node_id", "07");//流程id
    	employmentbean.set("toStage", "01");//转新阶段
    	employmentbean.set("passChoice", "02");//通过
    	employmentbean.set("obsolete", "03");//淘汰
    	functionList.add(employmentbean);
    	//offer
    	LazyDynaBean offerbean = new LazyDynaBean();
    	offerbean.set("node_id", "08");//流程id
    	offerbean.set("toStage", "01");//转新阶段
    	offerbean.set("changeStatus", "02");//进行时
    	offerbean.set("sendOffer", "03");//offer通知
    	offerbean.set("acceptOffer", "04");//接受offer
    	offerbean.set("refuseOffer", "05");//拒绝offer
    	functionList.add(offerbean);
    	//入职
    	LazyDynaBean entrybean = new LazyDynaBean();
    	entrybean.set("node_id", "10");//流程id
    	entrybean.set("toStage", "01");//转新阶段
    	entrybean.set("changeStatus", "02");//进行时
    	entrybean.set("rzRegister", "03");//入职
    	entrybean.set("refuseRz", "04");//拒绝入职
    	functionList.add(entrybean);
    	return functionList;
    }
    /***
     * 根据状态获取当前状态名字
     * @param status
     * @return
     */
    public LazyDynaBean getStatus(String status,String link_id)
    {
    	LazyDynaBean statusBean = new LazyDynaBean();
    	try {
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql = new StringBuffer("select link_id,status,custom_name from zp_flow_status where status=? and link_id=?");
			ArrayList list = new ArrayList();
			list.add(status);
			list.add(link_id);
			RowSet rs = dao.search(sql.toString(), list);
			if(rs.next())
			{
				statusBean.set("link_id", rs.getString("link_id"));
				statusBean.set("status", rs.getString("status"));
				statusBean.set("custom_name", rs.getString("custom_name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBean;
    }
    /***
     * 根据流程号获取流程名
     * @param link_id
     * @return
     */
    public LazyDynaBean getCustom_name(String link_id)
    {
    	LazyDynaBean bean = new LazyDynaBean();
    	try {
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql = new StringBuffer("select custom_name from zp_flow_links where id=?");
			ArrayList list = new ArrayList();
			list.add(link_id);
			RowSet rs = dao.search(sql.toString(), list);
			if(rs.next())
			{
				bean.set("custom_name", rs.getString("custom_name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
    }
    
    /****
     * 给当前人员设置考场信息
    * @Title:setZpExamAssign
    * @Description：
    * @author xiexd
    * @param z0301
    * @param a0100
    * @param nbase
     */
    public void setZpExamAssign(String z0301,String a0100,String nbase)
    {
    	if(this.hasMsg(z0301, a0100, nbase, "zp_exam_assign"))
    	{
    		return;
    	}
    	String codeSql = "select * from codeitem where codesetid='79' and codeitemid = parentid and invalid=1";
    	RowSet codeRs = null;
 	    try {
 	    	ContentDAO dao = new ContentDAO(conn);
 	    	codeRs = dao.search(codeSql.toString());
 	    	ArrayList z03items = new ArrayList();
 	    	ArrayList allItems = new ArrayList();
 	    	StringBuffer replaces = new StringBuffer();
 	    	//需要插入值的指标集
 	    	allItems.add("nbase");
 	    	allItems.add("a0100");
 	    	allItems.add("a0101");
 	    	//Z03的指标
 	    	z03items.add("Z0301");
 	    	z03items.add("Z0321");
 	    	z03items.add("Z0325");
 	    	//已有科目
 			while (codeRs.next()) {
 				z03items.add("subject_"+codeRs.getString("codeitemid"));
 			}
 	    	allItems.addAll(z03items);
 	    	//需要插入的值
 			ArrayList allValues = new ArrayList();
 			allValues.add(nbase);
 			allValues.add(a0100);
 			allValues.add(this.getA0101(a0100, nbase));
 			allValues.addAll(this.getZ03Info(z0301, z03items));
 			
 			StringBuffer sql = new StringBuffer("insert into zp_exam_assign(");
 			for(int i=0;i<allItems.size();i++)
 			{
 				sql.append(allItems.get(i)+",");
 				replaces.append("?,");
 			}
 			sql.setLength(sql.length()-1);
 			replaces.setLength(replaces.length()-1);
 			
 			sql.append(") values (");
 			sql.append(replaces);
 			sql.append(")");
 			
 			//保存考场安排信息
 			dao.insert(sql.toString(), allValues);
 			
	 	}catch (Exception e) {
	 		   e.printStackTrace();
		}finally{
			PubFunc.closeResource(codeRs);
		}
    }
    
    /***
     * 获取Z03相关信息
    * @Title:getZ03Info
    * @Description：
    * @author xiexd
    * @param z0301
    * @param z03items Z03需要取得的字段
    * @return
     */
    public ArrayList getZ03Info(String z0301,ArrayList z03items){
    	
    	ArrayList z03values = new ArrayList();
    	RowSet rs = null;
    	try {
    		ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql = new StringBuffer(" select ");
			ArrayList value = new ArrayList();
			for(int i=0;i<z03items.size();i++)
			{
				sql.append(" "+z03items.get(i)+",");
			}
			sql.setLength(sql.length()-1);
			sql.append(" from z03 where z0301 = ?");
			value.add(z0301);
			
			rs = dao.search(sql.toString(), value);
			
			if(rs.next())
			{
				for(int i=0;i<z03items.size();i++)
				{
					if(rs.getObject((z03items.get(i)+""))==null)
					{
						z03values.add("");
					}else{						
						z03values.add(rs.getString(z03items.get(i)+""));
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
    	
    	return z03values;
    }
    
    /***
     * 查询人员姓名
    * @Title:getA0101
    * @Description：
    * @author xiexd
    * @param a0100
    * @param nbase
    * @return
     */
    public String getA0101(String a0100,String nbase)
    {
    	String a0101 = "";
    	ContentDAO dao = new ContentDAO(conn);
    	RowSet rs = null;
    	try {
			
    		String sql = "select a0101 from "+nbase+"A01 where a0100='"+a0100+"'";    	
    		rs= dao.search(sql);
    		if(rs.next())
    		{
    			a0101 = rs.getString("a0101");
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return a0101;
    }
    
    /***
     * 向招聘成绩表中插入数据
    * @Title:setZ63
    * @Description：
    * @author xiexd
    * @param z0301
    * @param a0100
    * @param nbase
     */
    public void setZ63(String z0301,String a0100,String nbase)
    {
    	RowSet search = null;
 	    try {
 	    	if(this.hasMsg(z0301, a0100, nbase, "z63"))
 	    	{
 	    		return;
 	    	}
 	    	ContentDAO dao = new ContentDAO(conn);
 	    	HashMap<String, ArrayList> fieldItem = getItem(dao);
 	    	ArrayList z03items = new ArrayList();
 	    	ArrayList allItems = new ArrayList();
 	    	StringBuffer replaces = new StringBuffer();
 	    	//需要插入值的指标集
 	    	allItems.add("nbase");
 	    	allItems.add("a0100");
 	    	allItems.add("a0101");
 	    	//Z03的指标
 	    	z03items.add("Z0301");
 	    	z03items.add("Z0321");
 	    	z03items.add("Z0325");
 	    	z03items.add("Z0351");
 	    	allItems.addAll(z03items);
 	    	//需要插入的值
 			ArrayList allValues = new ArrayList();
 			allValues.add(nbase);
 			allValues.add(a0100);
 			allValues.add(this.getA0101(a0100, nbase));
 			allValues.addAll(this.getZ03Info(z0301, z03items));
 			ArrayList<FieldItem> list = new ArrayList<FieldItem>();
 	    	StringBuffer searchSql = new StringBuffer();
 	    	for (String key : fieldItem.keySet()) {
 	    		list = fieldItem.get(key);
 	    		searchSql.setLength(0);
 	    		searchSql.append("select ");
 	    		for (FieldItem item : list) {
 	    			allItems.add(item.getItemid());
 	    			searchSql.append(item.getItemid()+",");
				}
 	    		searchSql.setLength(searchSql.length()-1);
 	    		searchSql.append(" from ");
    			searchSql.append(nbase+key);
 	    		
 	    		searchSql.append(" where a0100="+a0100);
 	    		if(!"A01".equals(key))
 	    			searchSql.append(" and i9999=(select max(i9999) from "+nbase+key+" where a0100="+a0100+") ");
 	    		search = dao.search(searchSql.toString());
 	    		if(search.next()){
 	    			String value = "";
 	    			CodeItem code = null;
 	    			for (FieldItem item : list) {
// 	    				if("A".equalsIgnoreCase(item.getItemtype())&&!"0".equals(item.getCodesetid())){
// 	    					code = AdminCode.getCode(item.getCodesetid(),search.getString(item.getItemid()));
// 	    					if(code!=null)
// 	    						value = code.getCodename();
// 	    				}
// 	    				else
 	    					value = search.getString(item.getItemid());
 	    				
 	    				allValues.add(value);
					}
 	    		}else{
 	    			String value = "";
 	    			for (int i=0;i<list.size();i++) {
 	    				allValues.add(value);
					}
 	    		}
 	    	  }
 			StringBuffer sql = new StringBuffer("insert into z63(");
 			for(int i=0;i<allItems.size();i++)
 			{
 				sql.append(allItems.get(i)+",");
 				replaces.append("?,");
 			}
 			sql.setLength(sql.length()-1);
 			replaces.setLength(replaces.length()-1);
 			
 			sql.append(") values (");
 			sql.append(replaces);
 			sql.append(")");
 			
 			//保存招聘考试成绩表
 			dao.insert(sql.toString(), allValues);
 			
	 	}catch (Exception e) {
	 		   e.printStackTrace();
		}finally{
			PubFunc.closeResource(search);
		}
    }
    
    /***
     * 校验当前表中是否存在此用户数据
    * @Title:hasMsg
    * @Description：
    * @author xiexd
    * @param z0301
    * @param a0100
    * @param nbase
    * @param tableName 表名
    * @return
     */
    public boolean hasMsg(String z0301,String a0100,String nbase,String tableName)
    {
    	boolean flg = false;
    	ContentDAO dao = new ContentDAO(conn);
    	RowSet rs = null;
    	try {
			String sql = "select * from "+tableName+" where nbase=? and a0100=? and z0301=?";
			ArrayList value = new ArrayList();
			value.add(nbase);
			value.add(a0100);
			value.add(z0301);
			rs = dao.search(sql, value);
			if(rs.next())
			{
				flg = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
    	
    	return flg;
    }
    
    /***
     * 当前人员是否可以进入考试安排人员表中（批次和科目都在必须有的情况下才允许进入安排表）
     * 2019.5.17修改为没有考试科目也可以进入考生管理
    * @Title:isSetValue
    * @Description：
    * @author xiexd
    * @param z0301
    * @return
     */
    public boolean isSetValue(String z0301){
    	boolean flg = false;
    	ContentDAO dao = new ContentDAO(conn);
    	RowSet rs = null;
    	String codeSql = "select * from codeitem where codesetid='79' and codeitemid = parentid and invalid=1";
    	RowSet codeRs = null;
    	try {
    		codeRs = dao.search(codeSql.toString());
    		ArrayList subjs = new ArrayList();
    		//去掉关于考试科目的控制，到了笔试环节后均可以进入考生管理
 			/*while (codeRs.next()) {
 				subjs.add("subject_"+codeRs.getString("codeitemid"));
 			}*/
 			
			StringBuffer sql = new StringBuffer("select * from z03 where z0301='"+z0301.trim()+"' and Z0101 is not null and (");
			if(subjs.size()>0)
			{
				for(int i=0;i<subjs.size();i++)
				{
					sql.append(subjs.get(i)+" is not null or ");
				}	
				sql.setLength(sql.length()-3);
			}else{
				sql.append(" 1=1");
			}
			sql.append(")");
			rs = dao.search(sql.toString());
			if(rs.next())
			{
				flg = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
    	return flg;
    }
    
    /**
     * 获取z63表属于指标集的字段信息
     * @param dao
     * @return
     */
    public HashMap<String, ArrayList> getItem(ContentDAO dao){
    	RowSet rs = null;
    	HashMap<String, ArrayList> map = new HashMap<String, ArrayList>();
    	try {
	    	ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("z63",Constant.USED_FIELD_SET);
	    	StringBuffer sql = new StringBuffer();
	    	sql.append("select fieldsetid,itemid,itemtype,itemdesc,codesetid from fielditem where  useflag='1' and itemid<>'A0101' ");
	    	sql.append(" and fieldsetid like 'A%' ");
	    	sql.append(" and itemid in( ");
	    	for (FieldItem item : fieldList) {
				sql.append("'"+item.getItemid().toUpperCase()+"',");
			}
	    	sql.setLength(sql.length()-1);
	    	sql.append(")");
	    	sql.append(" order by fieldsetid");
	    	rs = dao.search(sql.toString());
	    	String fieldsetid = "";
	    	ArrayList<FieldItem> list = new ArrayList<FieldItem>();
	    	FieldItem item = new FieldItem();
	    	while(rs.next()){
	    		item = new FieldItem();
	    		if(!fieldsetid.equalsIgnoreCase(rs.getString("fieldsetid"))){
	    			fieldsetid = rs.getString("fieldsetid");
	    			list = new ArrayList();
	    		}
	    		item.setItemid(rs.getString("itemid"));
	    		item.setItemtype(rs.getString("itemtype"));
	    		item.setCodesetid(rs.getString("codesetid"));
	    		item.setFieldsetid(fieldsetid);
	    		list.add(item);
	    		if(!"".equals(fieldsetid))
	    			map.put(fieldsetid, list);
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return map;
    }
    
    /**
     * 发送待办
     * 一个接收人一个职位一个环节一条待办
     * @param taskBo 发送待办类
     * @param username 环节处理人
     * @param  Z0351  招聘职位名称
     * @param resumeNumber 勾选简历数目
     * @param functionList  招聘流程id与环节id集合
     * @param z0301 招聘职位id
     * @param Z0381 招聘流程编号
     */
    private void sendPendingTask(ZpPendingtaskBo taskBo, String username, String z0351, int resumeNumber, ArrayList<String> functionList, String z0301, String z0381) {
    	RowSet rs = null;
    	try {
	    	String linkId = functionList.get(0);
            String nodeId = functionList.get(1);
            String linkName = functionList.get(2);
            String ext_flag = "ZP_"+z0301+"_"+linkId;
            ContentDAO dao =new ContentDAO(conn);
	    	StringBuffer buf = new StringBuffer();
			buf.append("/recruitment/position/position.do?b_search=link&encryptParam=");
            buf.append(PubFunc.encryption("link_id=" + linkId + "&node_id=" + nodeId +"&z0301=" + PubFunc.encryption(z0301) + "&z0381=" + PubFunc.encryption(z0381) + "&appfwd=1&from=position"));
	    	HashMap<String, String> params = new HashMap<String, String>(); 
	    	params.put("title", z0351+"职位有简历进入"+linkName+"环节， 请及时处理"); 
	    	params.put("url", buf.toString());
	    	params.put("ext_flag", ext_flag);
	    	StringBuffer sql = new StringBuffer();
	    	sql.append("select pending_id from t_hr_pendingtask");
	    	sql.append(" where pending_type='32' ");
	    	sql.append(" and receiver =?");
	    	sql.append(" and EXT_FLAG =?");
	    	ArrayList<String> value = new ArrayList<String>();
	    	value.add(username);
	    	value.add(ext_flag);
	    	rs = dao.search(sql.toString(), value);
	    	if(rs.next()) {
	    		params.put("pending_id", rs.getString("pending_id"));
	    		taskBo.updatePendingTask(username, params);
	    	}else
	    		taskBo.sendPendingTask(username, params);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		
	}
    
    /**
     * 根据环节和职位id查询默认待办接收人
     * @param linkId
     * @param z0301
     * @return
     */
    public ArrayList<HashMap> getDefPerson(String linkId, String z0301) {
    	RowSet rs = null;
        RowSet rsMember = null ;
        ArrayList<HashMap> defPerson = new ArrayList<HashMap>();
        try {
            ContentDAO dao = new ContentDAO(conn);
            RecruitPrivBo bo = new RecruitPrivBo();
            PhotoImgBo imgbo = new PhotoImgBo(conn);
            ArrayList list = new ArrayList();
            StringBuffer sql = new StringBuffer("SELECT Z0351, Z0381 FROM Z03 WHERE z0301 =?");
            list.add(z0301);
            String z0351 = "";
            String z0381 = "";
            rs = dao.search(sql.toString(),list);
            if (rs.next()) {
                z0351 = rs.getString("Z0351");
                z0381 = rs.getString("Z0381");
            }

            sql.setLength(0);
            sql.append("select a0100, nbase,a0101 from zp_members where z0301=?");
            sql.append(" and member_type IN (1,2,3)");
            rsMember = dao.search(sql.toString(),list);
            String name = ConstantParamter.getLoginUserNameField().toLowerCase();
            String pwd = ConstantParamter.getLoginPasswordField().toLowerCase();
            while (rsMember.next()) {
                String staffNumber = rsMember.getString("nbase") + rsMember.getString("a0100");
                String userName = "";
                String passWord = "";
                
                RecordVo vo = new RecordVo(rsMember.getString("nbase") + "A01");
                if(StringUtils.isEmpty(rsMember.getString("a0100")))
                	continue;
                
                vo.setString("a0100", rsMember.getString("a0100"));
                if (!dao.isExistRecordVo(vo))
                	continue;
                
                if (vo != null) {
                    //用户账号和密码
                    userName=vo.getString(name);
                    passWord=vo.getString(pwd);
                    UserView recipientUser = new UserView(userName, passWord, this.conn);
                    recipientUser.canLogin(false);
                    boolean permissions = bo.hasFlowLinkPriv(conn, recipientUser, z0301, z0381, linkId);
                    if (permissions) {
                        String photoPath = imgbo.getPhotoPath(rsMember.getString("nbase"), rsMember.getString("a0100"));
                        HashMap valueMap = new HashMap();
    					valueMap.put("id", PubFunc.encrypt(staffNumber));
    					valueMap.put("name", rsMember.getString("a0101"));
    					valueMap.put("photo", photoPath);
    					defPerson.add(valueMap);
                    }
                }
            }
        }catch (Exception e) {
        	e.printStackTrace();
		}
        return defPerson;
    }
    
    public void sendNotice(ArrayList<String> person, String linkId, String z0301, int resumeNumber, ArrayList functionList) 
    {
    	this.sendNotice(person, linkId, z0301, resumeNumber, functionList, true); 
    }
    
    /**
     * 发送待办和邮件待办
     * @param person 选中的接收人
     * @param linkId 环节id
     * @param z0301 职位id
     * @param resumeNumber 选中简历数
     * @param functionList 招聘流程id与环节id集合
     */
    public void sendNotice(ArrayList<String> person, String linkId, String z0301, int resumeNumber, ArrayList functionList , boolean isSelfUser) 
    {
        RowSet rs = null;
        try {
        	ArrayList param = (ArrayList) functionList.clone();
            ContentDAO dao = new ContentDAO(conn);
            RecruitPrivBo bo = new RecruitPrivBo();
            ZpPendingtaskBo taskBo = new ZpPendingtaskBo(conn, userview);
            ArrayList list = new ArrayList();
            list.add(linkId);
            //获取环节名称
            rs = dao.search("select CUSTOM_NAME from ZP_FLOW_LINKS where id=?",list);
            if(rs.next())
            	param.add(rs.getString("CUSTOM_NAME"));
            list.clear();
            StringBuffer sql = new StringBuffer("SELECT Z0351, Z0381 FROM Z03 WHERE z0301 =?");
            list.add(z0301);
            String z0351 = "";
            String z0381 = "";
            rs = dao.search(sql.toString(),list);
            if (rs.next()) {
                z0351 = rs.getString("Z0351");
                z0381 = rs.getString("Z0381");
            }
            
            ArrayList personList = new ArrayList();
            ArrayList<String> taskPersons = new ArrayList();
            String name = ConstantParamter.getLoginUserNameField().toLowerCase();
            String pwd = ConstantParamter.getLoginPasswordField().toLowerCase();
            //判断用户类型
            if(isSelfUser) {
            	for(int i = 0; person!=null && i<person.size(); i++) {
                    String staffNumber = PubFunc.decrypt(person.get(i));
                    String userName = "";
                    String passWord = "";
                    
                    RecordVo vo = new RecordVo(staffNumber.substring(0,3) + "A01");
                    if(StringUtils.isEmpty(staffNumber.substring(3)))
                    	continue;
                    
                    vo.setString("a0100", staffNumber.substring(3));
                    if (!dao.isExistRecordVo(vo))
                    	continue;
                    
                    if (vo != null) {
                        //用户账号和密码
                        userName=vo.getString(name);
                    	taskPersons.add(userName);
                        personList.add(staffNumber);
                    }
                }
            }else {
            	for(int i = 0; person!=null && i<person.size(); i++) {
                    String userName = PubFunc.decrypt(person.get(i));
                    //用户账号和密码
                	taskPersons.add(userName);
                    personList.add(userName);
                }
            }

            for (int x = 0; x < personList.size(); x++) {
            	this.sendPendingTask(taskBo, taskPersons.get(x), z0351, resumeNumber, param, z0301, z0381);
                this.sendEMail((String) personList.get(x), z0351, resumeNumber, param, z0301, z0381, isSelfUser);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeResource(rs);
        }
    }
}
