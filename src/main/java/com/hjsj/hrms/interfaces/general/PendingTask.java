package com.hjsj.hrms.interfaces.general;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


public class PendingTask {
	
	private String hrp_logon_url="";//人力系统的链接
	private String pending_system="";//待办系统
	private String receiveruser_field="";//待办接收人账号指标
	private String task_type="";//代办任务类型 P：绩效；T：人事异动;G:工资;W：日志
	private String pending_class_name="";//接口业务类完整类别
	private String pending_default_ck="";//是否使用我们默认的验证方式
	private String serviceURL = "";//第三方系统接口地址
	Category cat = Category.getInstance("com.hjsj.hrms.interfaces.general.PendingTask");
	public PendingTask()
	{
		this.hrp_logon_url=SystemConfig.getPropertyValue("hrp_logon_url");
		this.pending_system=SystemConfig.getPropertyValue("pending_system");
		this.receiveruser_field=SystemConfig.getPropertyValue("receiveruser_field");
		this.task_type=SystemConfig.getPropertyValue("task_type");
		this.pending_class_name=SystemConfig.getPropertyValue("pending_class_name");
		this.pending_default_ck=SystemConfig.getPropertyValue("pending_default_ck");
		this.serviceURL=SystemConfig.getPropertyValue("serviceURL");
		init();
	}
	private void init(){
		Connection conn = null;
		try{
			conn = AdminDb.getConnection();
			Table table = new Table("t_hr_task_view");
			DbWizard dbWizard = new DbWizard(conn);
			if(!dbWizard.isExistTable("t_hr_task_view",false)){
				Field field = null;
				field = new Field("orderId","顺序号",DataType.INT);
				field.setKeyable(true);
				field.setSortable(true);
				field.setNullable(false);
				table.addField(field);
				
				field = new Field("pendingCode","待办编号",DataType.STRING);
				field.setLength(200);
				table.addField(field);
				
				field = new Field("reqReserved","步骤名称");
				field.setDatatype(DataType.STRING);
				field.setLength(100);
				table.addField(field);
				
				
				field = new Field("taskid","任务编号",DataType.STRING);
				field.setLength(50);
				table.addField(field);
				
				field = new Field("insid","流程编号",DataType.STRING);
				field.setLength(50);
				table.addField(field);
				
				field = new Field("receiverGuid","审批人guid",DataType.STRING);
				field.setLength(50);
				table.addField(field);
				
				field = new Field("receiverId","审批人账号",DataType.STRING);
				field.setLength(30);
				table.addField(field);
				
				field = new Field("receiverName","审批人姓名",DataType.STRING);
				field.setLength(30);
				table.addField(field);
				
				field = new Field("senderGuid","发起人guid",DataType.STRING);
				field.setLength(50);
				table.addField(field);
				
				field = new Field("senderId","发起人账号",DataType.STRING);
				field.setLength(30);
				table.addField(field);
				
				field = new Field("senderName","发起人姓名",DataType.STRING);
				field.setLength(30);
				table.addField(field);
				
				field = new Field("pendingTitle","待办标题",DataType.STRING);
				field.setLength(250);
				table.addField(field);
				
				field = new Field("pendingUrl","待办链接",DataType.CLOB);
				field.setLength(500);
				table.addField(field);
				
				field = new Field("pendingType","待办类型",DataType.STRING);
				field.setLength(30);//代办类型传递的是模块名称，1不够大。
				table.addField(field);
				
				field = new Field("pendingStatus","待办状态",DataType.STRING);
				field.setLength(5);
				table.addField(field);
				
				field = new Field("pendingLevel","待办级别",DataType.STRING);
				field.setLength(1);
				table.addField(field);
				
				field = new Field("pendingSendFlag","待办发送状态",DataType.STRING);
				field.setLength(1);
				table.addField(field);
				
				field = new Field("pendingSendTime","待办发送时间",DataType.STRING);
				field.setLength(20);
				table.addField(field);
				
				field = new Field("doSendFlag","已办发送状态",DataType.STRING);
				field.setLength(1);
				table.addField(field);
				
				field = new Field("doSendTime","已办发送时间",DataType.STRING);
				field.setLength(20);
				table.addField(field);
				
				field = new Field("returnCode","集成系统返回的任务号",DataType.STRING);
				field.setLength(200);
				table.addField(field);
				
				field = new Field("startTime","待办创建时间",DataType.STRING);
				field.setLength(20);
				table.addField(field);
				
				field = new Field("endTime","待办完成时间",DataType.STRING);
				field.setLength(20);
				table.addField(field);
				
				dbWizard.createTable(table);
			}else{ 
				boolean addColumn=false; 
				if(!dbWizard.isExistField("t_hr_task_view", "reqReserved", false)){
					Field field = new Field("reqReserved","步骤名称");
					field.setDatatype(DataType.STRING);
					field.setLength(100);
					table.addField(field);
					addColumn=true;
				}
				
				if(!dbWizard.isExistField("t_hr_task_view", "taskid", false)){
					Field field = new Field("taskid","任务编号");
					field.setDatatype(DataType.STRING);
					field.setLength(50);
					table.addField(field);
					addColumn=true;
				}
				
				if(!dbWizard.isExistField("t_hr_task_view", "insid", false)){
					Field field = new Field("insid","流程编号");
					field.setDatatype(DataType.STRING);
					field.setLength(50);
					table.addField(field);
					addColumn=true;
				}
				if(addColumn)
				{
					dbWizard.addColumns(table);
					/**重新加载数据模型*/
					DBMetaModel dbmodel=new DBMetaModel(conn);
					dbmodel.reloadTableModel("t_hr_task_view");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(conn != null){
					conn.close();
				}
			}catch(Exception e){
				
			}
		}
		
	}
	/**
     * 向待办库中加入新的待办
     * @param pendingCode 待办编号
     * @param appType 申请待办的类型
     * @param pendingTitle  待办标题
     * @param senderMessage 待办信息发送人usercode（发送人用户登录名）Usr000001     
     * @param receiverMessage 待办信息接收人usercode（接收人用户登录名）Usr000001
     * @param pendingURL  待办链接地址
     * @param pendingStatus  待办状态（0：待办， 1：已办，2：待阅，3：已阅）
     * @param pendingLevel  待办级别（0：非重要，1：重要）
     * @param pendingType  待办所在应用中的类别（不同类型的绩效考核的中文名称，如个人考核，团队考核）
     * @return
     */
	public boolean insertPending(String pendingCode,String appType,String pendingTitle,String senderMessage,String receiverMessage,
			String pendingURL,int pendingStatus,int pendingLevel,String pendingType,UserView userview)
	{
		//System.out.println("新增||"+pendingCode+",待办发送者为:"+senderMessage+",接收者为："+receiverMessage+",模块标识："+appType+"(P为绩效如果是其他的算bug)");
		
		boolean isCorrect=false;
		/*if(this.pending_system==null || "".equalsIgnoreCase(pending_system)){
			return false;
		}*/
		 
		if(this.task_type!=null && !"".equalsIgnoreCase(this.task_type) && !"all".equalsIgnoreCase(this.task_type))//P:绩效,T:人事异动,G:薪资
		{
			if(this.task_type.indexOf(appType)==-1)
			{
				cat.error("发送待办类型与设置的待办类型不符！----appType="+appType);
				return false;
			}
		}
		
		if (receiverMessage==null || "".equalsIgnoreCase(receiverMessage)){
			cat.error("找不到待办接收人！----receiverMessage="+receiverMessage);
			return false;
		}
		// int spflag=this.getTaskSP_flag(pendingURL);
		HashMap sp_map=this.getTaskSP_flag(pendingURL);
		int spflag = 0;
		String spflagstr = (String)sp_map.get("spflag");
		 if(spflagstr!=null && spflagstr.length()>0){
			 spflag = Integer.parseInt(spflagstr);
		 }
		 
		 String insid =(String)sp_map.get("insid");
		 String taskid =(String)sp_map.get("taskid");
	       
		RowSet rs = null;
        Connection conn = null;
		try{
			StringBuffer apendingUrl = new StringBuffer();
			String etoken = "";
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			DbNameBo dbNameBo = new DbNameBo(conn);
			String username_field = dbNameBo.getLogonUserNameField();
			String password_field = dbNameBo.getLogonPassWordField();
			String loginPassword = "";
			String nbase = "";
			String a0100 = "";
			
			//生成etoken
        	rs = dao.search("select username,password,nbase,a0100 from operuser where username='"+receiverMessage+"'");
        	if(rs.next()){
        		nbase = rs.getString("nbase");
        		a0100 = rs.getString("a0100");
                if(StringUtils.isBlank(nbase)||"null".equalsIgnoreCase(nbase)){
                    return false;
                }
        		etoken = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(rs.getString("username")+","+rs.getString("Password")));
        		//etoken = PubFunc.convertTo64Base(rs.getString("username").trim()+",999912310000");
        	}else{
        		nbase = receiverMessage.substring(0,3);
    			a0100 = receiverMessage.substring(3);
                if(StringUtils.isBlank(nbase)||"null".equalsIgnoreCase(nbase)){
                    return false;
                }
        		rs = dao.search("select "+username_field+" username,"+password_field+" password from "+nbase+"a01 where a0100='"+a0100+"'");
        		//System.out.println(this.getClass()+"查询登录用户sql：" + "select "+username_field+" username,"+password_field+" password from "+nbase+"a01 where a0100='"+a0100+"'");
        		if (rs.next()){
        			//System.out.println(this.getClass()+"登录密码：" + rs.getString("password"));
        			if(rs.getString("password")==null){
        				loginPassword = "";
        			}else{
        				loginPassword = rs.getString("password");
        			}
        			etoken = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(rs.getString("username")+","+loginPassword));
        			//etoken = PubFunc.convertTo64Base(rs.getString("username").trim()+",999912310000");
        		}
        	}

        	//若待办编号传过来是空，例如绩效待办，那么自动生成唯一编号
        	SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        	Date d = new Date();
        	String date = sdf.format(d);
        	if(pendingCode==null || "".equalsIgnoreCase(pendingCode)){
        		pendingCode= "HRMS-"+nbase+a0100+"-"+d.getTime()+Math.round(Math.ceil(Math.random()*10));
        	}
        	//组合待办链接
        	apendingUrl.append(this.hrp_logon_url);
        	apendingUrl.append(pendingURL);
        	apendingUrl.append("&appfwd=1&etoken=");
        	apendingUrl.append(etoken);
        	apendingUrl.append("&sp_flag=");
        	apendingUrl.append(spflag);

        	if (this.receiveruser_field==null || "".equalsIgnoreCase(this.receiveruser_field)){
        		this.receiveruser_field = username_field;
        	}
        	
        	//获取接收人信息
        	String receiverId = "";
        	String receiverName = "";
        	String receiverGuid = "";
        	LazyDynaBean bean = this.getUserAtts(receiverMessage);
        	if(bean != null){
        		receiverId = (String) bean.get("id");
        		receiverName = (String) bean.get("name");
        		receiverGuid = (String) bean.get("guid");
        	}
        	
        	//获取发送人信息
        	String senderId = "";
        	String senderName = "";
        	String senderGuid = "";
        	bean = this.getUserAtts(senderMessage);
        	if(bean != null){
        		senderId = (String) bean.get("id");
        		senderName = (String) bean.get("name");
        		senderGuid = (String) bean.get("guid");
        	}
        	
        	//获取最大排序号
        	int orderId = getMaxOrderId(conn);
        	/*int orderId = 1;
        	rs = dao.search("select max(orderId) orderId from t_hr_task_view");
        	if (rs.next()){
        		orderId = rs.getInt("orderId")+1;
        	}*/
        	
        	//将待办数据记录到中间表
			//taskid部分情况下vo中没有，导致报错，无法更新数据
        	DBMetaModel dbmodel=new DBMetaModel(conn);
			dbmodel.reloadTableModel("t_hr_task_view");
        	RecordVo vo = new RecordVo("t_hr_task_view");
        	vo.setInt("orderid", orderId);
        	vo.setString("pendingcode", pendingCode);
        	vo.setString("taskid", taskid);
        	vo.setString("insid", insid);
        	vo.setString("receiverguid", receiverGuid);
        	vo.setString("receiverid", receiverId);
        	vo.setString("receivername", receiverName);
        	vo.setString("senderguid", senderGuid);
        	vo.setString("senderid", senderId);
        	vo.setString("sendername", senderName);
        	vo.setString("pendingtitle", pendingTitle);
        	vo.setString("pendingtype", pendingType);
        	vo.setString("pendingstatus", String.valueOf(pendingStatus));
        	vo.setString("pendinglevel", String.valueOf(pendingLevel));
        	vo.setString("pendingsendflag", "0");
        	vo.setString("pendingsendtime", date);
        	vo.setString("starttime", date);
        	dao.addValueObject(vo);
        	dao.update("update t_hr_task_view set pendingurl='"+apendingUrl.toString()+"' where orderId="+orderId+"");
        	//发送待办
        	if(this.pending_class_name != null && !"".equalsIgnoreCase(this.pending_class_name)){
        		
	        	Map map = new HashMap();
	        	/* if ("P".equalsIgnoreCase(appType))
	        		 map.put("fldtype", "民主测评");
	             else if ("T".equalsIgnoreCase(appType))
	            	 map.put("fldtype", "人事变动");
	             else if ("G".equalsIgnoreCase(appType))
	        	 	 map.put("fldtype", "工资审批");
	             else if("K".equalsIgnoreCase(appType))
	            	 map.put("fldtype", "考勤管理");
	             else
	            	 map.put("fldtype", "");*/
	        	if(spflag==2)
	        		map.put("stat", "驳回");
	   	        else {
	   	            map.put("stat", "领导审批");
	   			}
	        	map.put("taskid", pendingCode+receiverId);
	        	map.put("title", pendingTitle);
	        	map.put("url", apendingUrl.toString());
	        	map.put("receiverid", receiverId);
	        	map.put("senderid", senderId);
	        	map.put("starttime", date);
	        	map.put("serviceurl", this.serviceURL);
	        	map.put("pendingType", pendingType);
				Class onwClass = Class.forName(this.pending_class_name);
				SendMatters sendmatters = (SendMatters) onwClass.newInstance();
				String returnCode = sendmatters.insert(map);
				if ("false".equalsIgnoreCase(returnCode)){
					vo.setString("pendingsendflag", "2"); //即使待办新增失败，在审批时也要发通知，谁知道是不是对面反馈信息有问题
					vo.setString("returncode", pendingCode+receiverId);
				}else if("true".equalsIgnoreCase(returnCode)){
					vo.setString("pendingsendflag", "1");
					vo.setString("returncode", pendingCode+receiverId);
					isCorrect = true;
				}else{
					vo.setString("pendingsendflag", "1");
					vo.setString("returncode", returnCode);
					isCorrect = true;
				}
				dao.updateValueObject(vo);
        	}else{
        		isCorrect = true;
        	}
        	
        	
		}catch(Exception e){
			e.printStackTrace();
			return isCorrect;
		}finally{
			try 
			{				
        		if (rs != null){
        			rs.close();
        		}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		

		return isCorrect;
	}	
	
	

	/**
	 * 
	 * @param appType
	 * @param pendingCode
	 * @param pendingStatus 0:待办； 1：已办 2：已阅  100:删除待阅、已阅文件
	 * @param pendingType
	 * @return
	 */
	public boolean updatePending(String appType,String pendingCode,int pendingStatus,String pendingType,UserView userview)
	{
		//System.out.println("修改||"+pendingCode+"状态为:"+pendingStatus+"(0:待办； 1：已办),模块标识："+appType);
        boolean isCorrect=false; 
		/*if(this.pending_system==null || "".equalsIgnoreCase(pending_system)){
			return false;
		}*/
		 
		if(this.task_type!=null && !"".equalsIgnoreCase(this.task_type) && !"all".equalsIgnoreCase(this.task_type))//P:绩效,T:人事异动,G:薪资
		{
			if(this.task_type.indexOf(appType)==-1)
			{
				cat.error("发送待办类型与设置的待办类型不符！----appType="+appType);
				return false;
			}
		}
		
		if (pendingCode==null || "".equalsIgnoreCase(pendingCode)){
			cat.error("找不到待办编号！----pendingCode="+pendingCode);
			return false;
		}
		
        RowSet rs = null;
        Connection conn = null;
		try{
			SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        	Date d = new Date();
        	String date = sdf.format(d);
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search("select orderId,returnCode,receiverId from t_hr_task_view where pendingCode='"+pendingCode+"' and pendingSendFlag<>'3' and (DOSENDFLAG<>'1' or  DOSENDFLAG is null )");
			if(rs != null){
				StringBuffer buf = new StringBuffer();
				buf.append("update t_hr_task_view set pendingStatus=");
				buf.append("'"+pendingStatus+"'");
				if (pendingStatus!=2){
					buf.append(",doSendTime=");
					buf.append("'"+date+"'");
					buf.append(",endTime=");
					buf.append("'"+date+"'");
				}
				buf.append(" where pendingCode=");
				buf.append("'"+pendingCode+"'");
				dao.update(buf.toString());
			}
			if(this.pending_class_name != null && !"".equalsIgnoreCase(this.pending_class_name)){
				while(rs.next()){
					//修改待办
					Map map = new HashMap();
					map.put("taskid", rs.getString("returnCode"));
					map.put("receiverid", rs.getString("receiverId"));
					map.put("serviceurl", this.serviceURL);
					map.put("fldDoneTime", date);
					map.put("pendingType", pendingType);
					Class onwClass = Class.forName(this.pending_class_name);
					SendMatters sendmatters = (SendMatters) onwClass.newInstance();
					String returnCode = "false";
					if (pendingStatus==1){
						returnCode = sendmatters.update(map);
					}else if (pendingStatus==100){
						returnCode = sendmatters.delete(map);
					}
					if ("false".equalsIgnoreCase(returnCode)){
						dao.update("update t_hr_task_view set doSendFlag='2' where orderId="+rs.getInt("orderId")+"");
						isCorrect = false;
					}else{
						dao.update("update t_hr_task_view set doSendFlag='3' where orderId="+rs.getInt("orderId")+"");
						isCorrect = true;
					}
					
				}
        	}else{
        		isCorrect = true;
        	}
			
		}catch(Exception e){
			e.printStackTrace();
			return isCorrect;
		}finally{
			try 
			{				
	    		if (rs != null){
	    			rs.close();
	    		}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isCorrect;		

	}	
	public boolean deletePending(String appType,String pendingCode,int pendingStatus,String pendingType)
	{
		//System.out.println("删除||"+pendingCode+"状态为:"+pendingStatus+"(0:待办； 1：已办),模块标识："+appType);
		 boolean isCorrect=false;        		
			/*if(this.pending_system==null || "".equalsIgnoreCase(pending_system)){
				return false;
			}*/
			 
			if(this.task_type!=null && !"".equalsIgnoreCase(this.task_type) && !"all".equalsIgnoreCase(this.task_type))//P:绩效,T:人事异动,G:薪资
			{
				if(this.task_type.indexOf(appType)==-1)
				{
					cat.error("发送待办类型与设置的待办类型不符！----appType="+appType);
					return false;
				}
			}
			
			if (pendingCode==null || "".equalsIgnoreCase(pendingCode)){
				cat.error("找不到待办编号！----pendingCode="+pendingCode);
				return false;
			}
			
	        RowSet rs = null;
	        Connection conn = null;
			try{
				SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
	        	Date d = new Date();
	        	String date = sdf.format(d);
				conn = AdminDb.getConnection();
				ContentDAO dao = new ContentDAO(conn);
				rs = dao.search("select orderId,returnCode,receiverId from t_hr_task_view where pendingCode='"+pendingCode+"' and pendingSendFlag<>'3'");
				if(rs != null){
					dao.update("update t_hr_task_view set pendingStatus='"+pendingStatus+"',doSendTime='"+date+"',endTime='"+date+"' where pendingCode='"+pendingCode+"'");
				}
				if(this.pending_class_name != null && !"".equalsIgnoreCase(this.pending_class_name)){
					while(rs.next()){
						//修改待办
						Map map = new HashMap();
						map.put("taskid", rs.getString("returnCode"));
						map.put("receiverid", rs.getString("receiverId"));
						map.put("serviceurl", this.serviceURL);
						map.put("pendingType", pendingType);
						Class onwClass = Class.forName(this.pending_class_name);
						SendMatters sendmatters = (SendMatters) onwClass.newInstance();
						String returnCode = sendmatters.delete(map);
						if ("false".equalsIgnoreCase(returnCode)){
							dao.update("update t_hr_task_view set doSendFlag='2' where orderId="+rs.getInt("orderId")+"");
							isCorrect = false;
						}else{
							dao.update("update t_hr_task_view set doSendFlag='3' where orderId="+rs.getInt("orderId")+"");
							isCorrect = true;
						}
						
					}
	        	}else{
	        		isCorrect = true;
	        	}
				
			}catch(Exception e){
				e.printStackTrace();
				return isCorrect;
			}finally{
				try 
				{				
		    		if (rs != null){
		    			rs.close();
		    		}
					if (conn != null) {
						conn.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return isCorrect; 
	}

	/**
	 * 通过人事移动的传递的值得到用户名密码 例value=Usr000001
	 * @param value
	 * @return username，password
	 */
	private LazyDynaBean getUserAtts(String message)
	{
		if(message==null||message.length()<=0)
		{
			return null;
		}
		LazyDynaBean bean=null;
		RowSet rs = null;
        Connection conn = null;
		try{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String nbase = "";
			String a0100 = "";
        	
        	rs = dao.search("select nbase,a0100 from operuser where username='"+message+"'");
        	if(rs.next()){
        		nbase = rs.getString("nbase");
        		a0100 = rs.getString("a0100");
        	}else{
        		nbase = message.substring(0,3);
    			a0100 = message.substring(3);
        	}
        	if(StringUtils.isBlank(nbase)||"null".equalsIgnoreCase(nbase)){
        	    return null;
        	}
        	List list = dao.searchDynaList("select "+this.receiveruser_field+" id,a0101 name,guidkey guid from "+nbase+"a01 where a0100='"+a0100+"'");
        	if (list!=null && list.size()>0){
        		bean = (LazyDynaBean) list.get(0);
        	}
        	
		}catch(Exception e){
			e.printStackTrace();
			return bean;
		}finally{
			try 
			{				
        		if (rs != null){
        			rs.close();
        		}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	   return bean;
	}
	
	//public int getTaskSP_flag(String url){
	public HashMap getTaskSP_flag(String url){
		  int flag=1;
		  Connection conn=null;
		  RowSet rowSet=null;
		  String tabid="";
		  String insidString="";
		  String taskidString="";
		  try{
			  conn = AdminDb.getConnection();
			 
			  Pattern pattern = Pattern.compile("[0-9]+");
			  
			  if(url.contains("&param=")){
				  url = url.split("&param=")[1];
				  url = SafeCode.decode(url);
				  String[] arr=url.split("&");
				  
				  for(int i=0;i<arr.length;i++){
					  String arr_i=arr[i];
					  if(arr_i.toUpperCase().indexOf("TABID")!=-1 || arr_i.toUpperCase().indexOf("TAB_ID")!=-1){
						  String[] aStrings=arr_i.split("=");
						  tabid=aStrings.length>1?aStrings[1]:"";
					  }else if(arr_i.toUpperCase().indexOf("TASKID")!=-1||arr_i.toUpperCase().indexOf("TASK_ID")!=-1){
						  String[] aStrings=arr_i.split("=");
						  taskidString=aStrings.length>1?aStrings[1]:"";
					  }else if(arr_i.toUpperCase().indexOf("INSID")!=-1||arr_i.toUpperCase().indexOf("INS_ID")!=-1){
						  String[] aStrings=arr_i.split("=");
						  insidString=aStrings.length>1?aStrings[1]:"";
					  }
				  }
				  
			  }else{
				  String[] arr=url.split("&");
				  for(int i=0;i<arr.length;i++){
					  String arr_i=arr[i];
					  if(arr_i.toUpperCase().indexOf("TABID")!=-1){
						  String[] aStrings=arr_i.split("=");
						  tabid=aStrings.length>1?aStrings[1]:"";
					  }else if(arr_i.toUpperCase().indexOf("TASKID")!=-1||arr_i.toUpperCase().indexOf("TASK_ID")!=-1){
						  String[] aStrings=arr_i.split("=");
						  taskidString=aStrings.length>1?aStrings[1]:"";
					  }else if(arr_i.toUpperCase().indexOf("INSID")!=-1||arr_i.toUpperCase().indexOf("INS_ID")!=-1){
						  String[] aStrings=arr_i.split("=");
						  insidString=aStrings.length>1?aStrings[1]:"";
					  }
				  }
				  
			  }
			  //taskidString 为空时，拼接sql 会报错
			  if(StringUtils.isNotBlank(taskidString)) {
				  if(!pattern.matcher(taskidString.trim()).matches())
					  taskidString = PubFunc.decrypt(taskidString.trim());
				  StringBuffer sql = new StringBuffer();
				  sql.append("select state from t_wf_task where task_id=");
				  sql.append(taskidString);
				  ContentDAO dao = new ContentDAO(conn);
				  rowSet=dao.search(sql.toString());
				  while(rowSet.next()){
					  String state=rowSet.getString("state");
					  if("07".equals(state)){
						  flag=2;
						  break;
					  }
				  }
			  }
			  
		  }catch(Exception e){
			  e.printStackTrace();
		  }finally{
			  try{
				  if(rowSet!=null)
					  rowSet.close();
				  if(conn!=null)
					  conn.close();
			  }catch(Exception e){
				  
			  }
		  }
		  
		  //return flag;
		  //增加了流程id,任务id和表单id
		  HashMap<String,String> retmap = new HashMap<String,String>();
		  retmap.put("insid", insidString);
		  retmap.put("taskid", taskidString);
		  retmap.put("tabid", tabid);
		  retmap.put("spflag", flag+"");
		  return retmap;
		  
		 
	  }
	
	/**
	 * 得到orderid
	 * @param conn
	 * @return
	 */
	private int getMaxOrderId(Connection conn) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from id_factory where sequence_name='t_hr_task_view.orderid'");
		RowSet rs = null;
		int orderId = 0;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search("select max(orderId) orderId from t_hr_task_view");
			if (rs.next()) {
				orderId = rs.getInt("orderId") + 1;
			}
			rs = dao.search(sql.toString());
			if (!rs.next()) {
				StringBuffer insertSQL = new StringBuffer();
				insertSQL.append(
						"insert into id_factory  (sequence_name, sequence_desc, minvalue, maxvalue, auto_increase, increase_order, prefix, suffix, currentid, id_length, increment_O)");
				insertSQL.append(" values ('t_hr_task_view.orderid', '待办表主键id', 1, 99999999, 1, 1, Null, Null, "
						+ orderId + ", 8, 1)");
				ArrayList list = new ArrayList();
				dao.insert(insertSQL.toString(), list);
			}
			IDGenerator idg = new IDGenerator(2, conn);
			String id = idg.getId("t_hr_task_view.orderid");
			orderId = Integer.parseInt(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return orderId;
	}
}
