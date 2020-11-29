package com.hjsj.hrms.businessobject.train.trainexam.exam;

import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Title:QuestionesBo
 * </p>
 * <p>
 * Description:培训考试计划业务类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-11-10
 * </p>
 * 
 * @author zxj
 * @version 1.0
 * 
 */
public class TrainExamPlanBo {

	// 数据库连接
	private Connection conn;
	
	private Boolean emailEnable;
	private Boolean smsEnable;
	private String messageTmpId;
	private String messageSueId;
	private Boolean autoCompute; //是否自动阅卷
	private Boolean autoRelease;
	private UserView userView;
	private Boolean enabled;
	private String times;
	private Boolean weixinEnable;
	private Boolean dingTalk;
	private Boolean pendingTaskEnable;
	
	public Boolean getWeixinEnable() {
		return weixinEnable;
	}

	public void setWeixinEnable(Boolean weixinEnable) {
		this.weixinEnable = weixinEnable;
	}

	public Boolean getDingTalk() {
	    return dingTalk;
	}
	
	public void setDingTalk(Boolean dingTalk) {
	    this.dingTalk = dingTalk;
	}

	public TrainExamPlanBo(){
		
	}

	public TrainExamPlanBo(Connection conn) {
		this.conn = conn;
	}
	
	public TrainExamPlanBo(Connection conn,UserView userView) {
		this.conn = conn;
		this.userView = userView;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 获得计划状态列表
	 * 
	 * @return
	 */
	public ArrayList getStatusList() {
		ArrayList list = new ArrayList();
		
		CommonData datavo = new CommonData("all", ResourceFactory.getProperty("label.all"));
		list.add(datavo);
		
		ArrayList codelist = AdminCode.getCodeItemList("23");
		for (int i = 0; i < codelist.size(); i++) {
            CodeItem codeItem = (CodeItem)codelist.get(i);
            
            String codeItemId = codeItem.getCodeitem();
            if("01".equalsIgnoreCase(codeItemId)|| "04".equalsIgnoreCase(codeItemId)|| "05".equalsIgnoreCase(codeItemId)|| "06".equalsIgnoreCase(codeItemId)|| "09".equalsIgnoreCase(codeItemId))
            {
                String codeName = codeItem.getCodename();
                CommonData data = new CommonData(codeItemId, codeName);
                list.add(data);
            }
        }
		return list;
	}

	/**
	 * 获得答卷方式列表 
	 * 
	 * @return
	 */
	public ArrayList getShowSytleList() {
		ArrayList list = new ArrayList();
        
		CommonData data0 = new CommonData("0", "全部");
		CommonData data1 = new CommonData("1", "整版");
		CommonData data2 = new CommonData("2", "单题");

		list.add(data0);
		list.add(data1);
		list.add(data2);

		return list;
	}

	
	/**
	 * 取得新的考试计划编号
	 * 
	 * @return
	 */
	public int getNewPlanId() {

		IDGenerator idg = new IDGenerator(2, this.getConn());
		try
		{
			String planId = idg.getId("R54.R5400");
			return Integer.parseInt(planId);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return 0;
		}		
		
	}

	/**
	 * 得到可选的试卷
	 * 
	 * @return
	 */
	public ArrayList getExamPapers() {
	    ArrayList examPapers = new ArrayList();
	    RowSet rs = null;
	    try {
	        ContentDAO dao = new ContentDAO(this.conn);
	        
	        // 权限过滤		
	        StringBuffer priv = new StringBuffer();
	        //非超级用户
	        if (!this.userView.isSuper_admin()) {
	            //公开试卷
	            priv.append(" AND (" + Sql_switcher.isnull("r5313", "'2'") + "='1'");
	            
	            //权限范围内试卷
	            TrainCourseBo bo = new TrainCourseBo(this.userView);
	            String unit = bo.getUnitIdByBusi();
	            
	            //不是全权
	            if(!"UN`".equals(unit))
	            {
	                //不是0权限，加权限控制；否则，只能得到公开试卷
	                if(!"".equals(unit))
	                {
	                    String []units = unit.split("`");
	                    if (units.length > 0 && unit.length() > 0) {
	                        priv.append(" OR (");
	                        for (int i = 0; i < units.length; i++) {
	                            if (i != 0) {
	                                priv.append(" OR ");
	                            } 
	                            String b0110s = units[i].substring(2);
	                            priv.append("b0110 LIKE '");
	                            priv.append(b0110s);
	                            priv.append("%'");
	                        }
	                        priv.append(" OR b0110='' OR "+Sql_switcher.isnull("b0110", "'-1'")+"='-1'");
	                        priv.append(")");
	                    }
	                }
	            } else 
	                //全权
                {
                    priv.append(" OR (1=1) ");
                }
	            
	            priv.append(")");
	        }
	        
	        StringBuffer sql = new StringBuffer("SELECT r5300,r5301 FROM R53 WHERE r5311 IN ('03','04','05') ");
	        //
	        if (priv.length() > 3) {
                sql.append(priv.toString());
            }
	        
	        sql.append(" ORDER BY nOrder");		
	        rs = dao.search(sql.toString());
	        while (rs.next()) {
	            String strValue = rs.getString("r5300");
	            String strName = rs.getString("r5301");				
	            CommonData data = new CommonData(strValue, strName);
	            examPapers.add(data);
	            
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return examPapers;
	}
	
	public ArrayList getMessageTmpList(String planId)
	{
		ArrayList list = new ArrayList();
		
		String sql = "SELECT id,name FROM EMAIL_NAME WHERE nModule=1 AND nInfoclass=1 ORDER BY id";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs;
		try
		{			
			rs = dao.search(sql);
			while(rs.next())
			{
				String tmpId = rs.getString("id");
				String tmpName = rs.getString("name");
				CommonData data = new CommonData(tmpId, tmpName);
				
				list.add(data);
			}	
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			
			
		}
		return list;
	}
	
	/*
	 * 加载消息通知参数，调用一次后，才可取各项参数
	 */
	public void loadMessageParam(String planId)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		Document doc = null;
		try
		{
			rs = dao.search("SELECT ext_param FROM R54 WHERE R5400=" + planId);
			if(rs.next())
			{
				String extParam = rs.getString("ext_param");				

				if(extParam!=null&&extParam.length()>10)
				{
					doc = PubFunc.generateDom(extParam);
					String xpath="/Params/notes";
					XPath reportPath = XPath.newInstance(xpath);// 取得根节点
					List paraList=reportPath.selectNodes(doc);
				    for (int i = 0; i < paraList.size(); i++) 
				    {
				    	Element el = (Element) paraList.get(i);
				    	String email = el.getAttributeValue("email");
				    	if ((email!=null)&&(!"".equalsIgnoreCase(email))) 
				    	{
				    		if("false".equalsIgnoreCase(email)) {
                                emailEnable = Boolean.FALSE;
                            } else {
                                emailEnable = Boolean.TRUE;
                            }
				    				
				    	}
				    	
				    	String sms = el.getAttributeValue("sms");
				    	if ((sms!=null)&&(!"".equalsIgnoreCase(sms))) 
				    	{
				    		if("false".equalsIgnoreCase(sms)) {
                                smsEnable = Boolean.FALSE;
                            } else {
                                smsEnable = Boolean.TRUE;
                            }
				    				
				    	}
				    	//培训管理-考试计划，设置已发布的考试计划时，报错   jingq upd 2015.04.29
				    	String weixin = el.getAttributeValue("weixin");
				    	if("true".equalsIgnoreCase(weixin)) {
                            weixinEnable = Boolean.TRUE;
                        } else {
                            weixinEnable = Boolean.FALSE;
                        }
				    	
				    	String dingTalkflag = el.getAttributeValue("dingTalk");
				    	if("true".equalsIgnoreCase(dingTalkflag)) {
                            dingTalk = Boolean.TRUE;
                        } else {
                            dingTalk = Boolean.FALSE;
                        }
				    	
				    	messageTmpId = el.getAttributeValue("template");
				    	if (messageTmpId==null) 
				    	{
				    		messageTmpId = "";				    				
				    	}
				    	
				    	messageSueId = el.getAttributeValue("template1");
				    	if (messageSueId==null) 
				    	{
				    		messageSueId = "";				    				
				    	}
				    	
				    	String pendingTask = el.getAttributeValue("pendingTask");
				    	if("true".equalsIgnoreCase(pendingTask)) {
                            pendingTaskEnable = Boolean.TRUE;
                        } else {
                            pendingTaskEnable = Boolean.FALSE;
                        }
				    	
				    }
				    
				    //自动阅卷参数 和自动发布成绩参数 2013-12-06 gdd
				    xpath="/Params/exam";
					reportPath = XPath.newInstance(xpath);// 取得根节点
					Element el = (Element)reportPath.selectSingleNode(doc);
					
					//默认为自动阅卷
					autoCompute = Boolean.TRUE;
					autoRelease = Boolean.FALSE;
					if(el!=null && "false".equals(el.getAttributeValue("autocompute"))) {
                        autoCompute = Boolean.FALSE;
                    }
					if(el!=null && "true".equals(el.getAttributeValue("autorelease"))) {
                        autoRelease = Boolean.TRUE;
                    }
					
					xpath="/Params/reexam";
	                reportPath = XPath.newInstance(xpath);// 取得根节点
	                Element reexam = (Element)reportPath.selectSingleNode(doc);
					enabled = Boolean.FALSE;
					times = reexam.getAttributeValue("times"); 
                    if(reexam!=null && !"false".equals(reexam.getAttributeValue("enabled"))) {
                        enabled = Boolean.TRUE;
                    }
                    
                    if(times == null) {
                        times = "0";
                    }
				}
				else
				{
					setEmailEnable(Boolean.FALSE);
					setSmsEnable(Boolean.FALSE);
					setWeixinEnable(Boolean.FALSE);
					setDingTalk(Boolean.FALSE);
					setMessageTmpId("");
					setMessageSueId("");
					setAutoCompute(Boolean.TRUE);
					setAutoRelease(Boolean.FALSE);
					setEnabled(Boolean.FALSE);
					setPendingTaskEnable(Boolean.FALSE);
					setTimes("0");
					
				}
				
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void saveMessageParam(String planId)
	{
		String extParam = "";
		ArrayList uptStr = new ArrayList();
		ArrayList extParamStr = new ArrayList();
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		Document doc = null;
		try
		{
			rs = dao.search("SELECT ext_param FROM R54 WHERE R5400=" + planId);
			if(rs.next())
			{
				extParam = rs.getString("ext_param");
				
				if((extParam!=null)&&(!"".equals(extParam)))
				{
					doc = PubFunc.generateDom(extParam);
					
					String xpath="/Params/notes";
					XPath reportPath = XPath.newInstance(xpath);// 取得根节点
					List paraList=reportPath.selectNodes(doc);
				    for (int i = 0; i < paraList.size(); i++) 
				    {
				    	Element el = (Element) paraList.get(i);
				    	if(emailEnable.booleanValue()) {
                            el.setAttribute("email", "true");
                        } else {
                            el.setAttribute("email","false");
                        }
				    	
				    	if(smsEnable.booleanValue()) {
                            el.setAttribute("sms", "true");
                        } else {
                            el.setAttribute("sms","false");
                        }
				    	
				    	if(weixinEnable.booleanValue()) {
                            el.setAttribute("weixin","true");
                        } else {
                            el.setAttribute("weixin","false");
                        }

				    	if(dingTalk.booleanValue()) {
                            el.setAttribute("dingTalk","true");
                        } else {
                            el.setAttribute("dingTalk","false");
                        }
				    	
				    	el.setAttribute("template",messageTmpId);
				    	el.setAttribute("template1",messageSueId);
				    	
				    	if(pendingTaskEnable.booleanValue()) {
                            el.setAttribute("pendingTask","true");
                        } else {
                            el.setAttribute("pendingTask","false");
                        }
				    }
				    
				    //保存考试设置参数（自动阅卷参数） 2013-12-06 gdd
				    xpath="/Params/exam";
				    reportPath = XPath.newInstance(xpath);
				    Element el = (Element)reportPath.selectSingleNode(doc);
				    if(el != null){
				    	el.setAttribute("autocompute", autoCompute.booleanValue()?"true":"false");
				    	el.setAttribute("autorelease", autoRelease.booleanValue()?"true":"false");
				    	
				    }
				    else{
				    	Element root = doc.getRootElement();
				    	Element e = new Element("exam");
				    	e.setAttribute("autocompute", autoCompute.booleanValue()?"true":"false");
				    	e.setAttribute("autorelease", autoRelease.booleanValue()?"true":"false");
				    	root.addContent(e);
				    }
				  //保存考试设置参数（自动阅卷参数） 2013-12-06 gdd
                    xpath="/Params/reexam";
                    reportPath = XPath.newInstance(xpath);
                    Element ele = (Element)reportPath.selectSingleNode(doc);
                    if(ele != null){
                        ele.setAttribute("enabled", enabled.booleanValue()?"true":"false");
                        ele.setAttribute("times", times);
                        
                    }
                    else{
                        Element root = doc.getRootElement();
                        Element reexam = new Element("reexam");
                        reexam.setAttribute("enabled", enabled.booleanValue()?"true":"false");
                        reexam.setAttribute("times", times);
                        root.addContent(reexam);
                    }
				    
				    XMLOutputter outputter = new XMLOutputter();
					Format format = Format.getPrettyFormat();
					format.setEncoding("UTF-8");
					outputter.setFormat(format);					 
					
				    String param = outputter.outputString(doc);
				    extParamStr.add(param);
				}
				else
				{
					StringBuffer xmlParam = new StringBuffer();
					xmlParam.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
					xmlParam.append("<Params>");
					xmlParam.append("<notes email=\"");
					if(emailEnable.booleanValue()) {
                        xmlParam.append("true");
                    } else {
                        xmlParam.append("false");
                    }
					xmlParam.append("\"");
					xmlParam.append(" sms=\"");
					if(smsEnable.booleanValue()) {
                        xmlParam.append("true");
                    } else {
                        xmlParam.append("false");
                    }
					xmlParam.append("\"");
					xmlParam.append(" weixin=\"");
					if(weixinEnable.booleanValue()) {
                        xmlParam.append("true");
                    } else {
                        xmlParam.append("false");
                    }
					xmlParam.append("\"");
					xmlParam.append(" dingTalk=\"");
					if(dingTalk.booleanValue()) {
                        xmlParam.append("true");
                    } else {
                        xmlParam.append("false");
                    }
					xmlParam.append("\"");
					xmlParam.append(" template=\"");
					xmlParam.append(messageTmpId);
					xmlParam.append("\"");
					xmlParam.append(" template1=\"");
					xmlParam.append(messageSueId);
					xmlParam.append("\"");
					xmlParam.append(" pendingTask=\"");
					if(pendingTaskEnable.booleanValue()) {
                        xmlParam.append("true");
                    } else {
                        xmlParam.append("false");
                    }
					xmlParam.append("\"");
					xmlParam.append("/>");
					xmlParam.append("<exam autocompute=\""+(autoCompute.booleanValue()?"true":"false")+"\" autorelease=\""+(autoRelease.booleanValue()?"true":"false")+"\"/>");
					
					xmlParam.append("<reexam enabled=\"");
					xmlParam.append(enabled.booleanValue()?"true":"false");
					xmlParam.append("\" times=\"");
					xmlParam.append(times);
					xmlParam.append("\"/>");
					
					xmlParam.append("</Params>");
					
					extParamStr.add(xmlParam.toString());
				}
				
				uptStr.add(extParamStr);
				String sql = "UPDATE R54 SET ext_param=? WHERE R5400=" + planId;
				dao.batchUpdate(sql, uptStr);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private String getPlanIdWhr(ArrayList planIds)
	{
		StringBuffer planIdWhr = new StringBuffer("R5400 IN (");		
		
    	for(int i=0; i<planIds.size(); i++)
        {	    	       
       	    String r5400 = (String)planIds.get(i);
       	    
       	    if(i<(planIds.size()-1)) {
                planIdWhr.append(r5400 + ',');
            } else {
                planIdWhr.append(r5400);
            }
        }
    	planIdWhr.append(")");
    	
    	return planIdWhr.toString();
	}
	
	/**
	 * 启动考试计划
	 * 
	 * @return
	 */
	
	public boolean startExamPlan(ArrayList planIds,String sendMsg) throws Exception
	{		
		String planWhr = getPlanIdWhr(planIds);
		
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE R54");
		sql.append(" SET R5411='05'");
		sql.append(" WHERE R5411 IN ('01','02','09')");
		sql.append(" AND " + planWhr);
    	
		ContentDAO dao = new ContentDAO(this.conn);
		boolean flag = false;
	    try {
	    	ArrayList msgPlanIds;
	    	if("0".equals(sendMsg)) {
                msgPlanIds = getSendMsgPlanIds(planWhr);
            } else {
                msgPlanIds = planIds;
            }
	    	
	    	try {
	    	    dao.update(sql.toString());
	    	    flag = true;
	    	}catch(Exception e){
	    	    flag = false;
	    	    throw GeneralExceptionHandler.Handle(e);
	    	}
	    	
	    	sendMessage(msgPlanIds,"1");//发送通知
	    		 
	    }catch(Exception e){
	        throw GeneralExceptionHandler.Handle(e);
	    }
	    
	    return flag;
	}
	
	private ArrayList getSendMsgPlanIds(String planWhr)
	{
		ArrayList planIds = new ArrayList();
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT R5400 FROM R54");
		sql.append(" WHERE R5411 IN ('01','02')");
		sql.append(" AND " + planWhr);
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try
		{
			rs = dao.search(sql.toString());
			if(rs.next())
			{
				planIds.add(rs.getString("R5400"));
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
				if(rs != null)
				{
					rs.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return planIds;
	}
	
	public boolean existStudentInExamPlan(String planId)
	{
		RowSet rs = null;
		boolean exists = false;
		
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String sql = "SELECT 1 FROM R55 WHERE R5400=" + planId;
			rs = dao.search(sql);
			exists = rs.next();			
			rs.close();			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return exists;
	}

	/**
	 * 暂停考试计划
	 * 
	 * @return
	 */
	
	public boolean pauseExamPlan(ArrayList planIds)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE R54");
		sql.append(" SET R5411='09'");
		sql.append(" WHERE R5411 IN ('05')");
		sql.append(" AND " + getPlanIdWhr(planIds));
    	
		ContentDAO dao = new ContentDAO(this.conn);
	    try
	    {
	    	dao.update(sql.toString());
	    	
	    	for(int i = 0; i < planIds.size(); i++) {
	    	    String r5400 = (String) planIds.get(i);
	    	    loadMessageParam(r5400);//加载参数
	    	    //发送待办
	    	    if(getPendingTaskEnable()) {
                    sendPendingTask(r5400, 4);
                }
	    	    
	    	}
	    	return true;
	    		 
	    }catch(Exception e)
	    {
	    	return false;
	    }
	}
	
	/**
	 * 发布考试计划成绩
	 * 
	 * @return
	 */
	
	public boolean publishExamPlan(ArrayList planIds) throws Exception
	{
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE R54");
		sql.append(" SET R5411='04'");
		sql.append(" WHERE R5411 IN ('06')");
		sql.append(" AND " + getPlanIdWhr(planIds));
    	
		ContentDAO dao = new ContentDAO(this.conn);
		
		boolean flag = false;
	    try
	    {
	    	dao.update(sql.toString());
	    	
	    	//修改阅卷状态
	    	sql.setLength(0);
	    	sql.append("update r55");
	    	sql.append(" set r5515=2");
	    	sql.append(" where r5515 in (0,1) and "+getPlanIdWhr(planIds));
	    	dao.update(sql.toString());
	    	
	    	//发送通知
	    	sendMessage(planIds,"2");
	    	flag = true;
	    		 
	    }catch(Exception e)
	    {
	    	flag = false;
	    	throw GeneralExceptionHandler.Handle(e);
	    	
	    }
	    return flag;
	}	
	
	/**
	 * 结束考试计划（收卷）
	 */
	public boolean finishExamPlan(String planId)
	{
		boolean finished = true;
		
		String sql = "UPDATE R54 SET R5411='06' WHERE R5400=" + planId;
		ContentDAO dao = new ContentDAO(this.conn);		
		try
		{			
			TrainExamStudentBo bo = new TrainExamStudentBo(this.conn);
			boolean stateOperSuccess = bo.setExamedForUnExamOrExaming(planId);
			if(stateOperSuccess)
			{
				dao.update(sql);
			
			    finished = true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		
		return finished;
	}
	
	/**
	 * 取计划的状态
	 */
	public String getPlanStatus(String planId)
	{
		String status = "";
		String sql = "SELECT R5411 FROM R54 WHERE R5400=" + planId;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			RowSet rs = dao.search(sql);
			if(rs.next())
			{
				status = rs.getString(1);
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return status;
	}
	
	/**
	 * 发送通知
	 * @param list
	 * @param flag
	 */
	public void sendMessage(ArrayList list,String flag) throws Exception{
		try{
			for(int i = 0; i < list.size(); i++) {
				String r5400 = (String)list.get(i);
				loadMessageParam(r5400);//加载参数
				if(getEmailEnable().booleanValue()) {
                    sendEMail(r5400,flag);
                }
				if(getSmsEnable().booleanValue()) {
                    sendSMS(r5400,flag);
                }
				
				String picUrl = "http://www.hjsoft.com.cn:8089/UserFiles/Image/announce.png";
				String topic = "";
				if("1".equals(flag)){
				    topic = ResourceFactory.getProperty("message.weixin.exam.start");
				}else {
                    topic = ResourceFactory.getProperty("message.weixin.exam.publish");
                }
				//【9865】培训中计划的启动微信中，没有按照设定的模板发送微信提醒 jingq add 2015.05.27
				String temp = "";
				if("1".equals(flag)){
				    temp = this.getMessageTmpId();
				} else {
				    temp = this.getMessageSueId();
				}
				
				if(getWeixinEnable() || getDingTalk()) {
                    sendExamPlanToWX(r5400,temp,topic,"","");
                }
				//发送待办
				if(getPendingTaskEnable()) {
                    sendPendingTask(r5400, 0);
                }
			}
		} catch (Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 发送考试计划消息到微信
	 * @param r5400 考试计划Id
	 * @param templateId 模板Id
	 * @param topic 标题
	 * @param picUrl 图片地址
	 * @param url 点击图文消息进入页面地址
	 * @return
	 */
	public boolean sendExamPlanToWX(String r5400,String templateId,String topic,String picUrl,String url) throws Exception{
		boolean flag = false;
		ResultSet rs = null;
		try{
			RecordVo vo = null;
			ContentDAO dao = new ContentDAO(conn);
			EmailTemplateBo bo = new EmailTemplateBo(conn);
			if(templateId!=null&&templateId.length()>0){
				String username = "";
				RecordVo login_vo = ConstantParamter
						.getConstantVo("SS_LOGIN_USER_PWD");
				if (login_vo == null) {
					username = "username";
				} else {
					String login_name = login_vo.getString("str_value").toLowerCase();
					int idx = login_name.indexOf(",");
					if (idx == -1) {
						username = "username";
					} else {
						username = login_name.substring(0, idx);
						if ("#".equals(username) || "".equals(username)) {
							username = "username";
						}
					}
				}
				ArrayList fieldList=bo.getTemplateFieldInfo(Integer.parseInt(templateId),2);
				String sql = "select nbase,a0100 from r55 where r5400="+r5400;
				rs = dao.search(sql);
				while(rs.next()){
					String tmpNbase = rs.getString("nbase");
					String tmpA0100 = rs.getString("a0100");
					vo = new RecordVo(tmpNbase+"A01");
					vo.setString("a0100", tmpA0100);
					if(dao.isExistRecordVo(vo)){
						if(vo!=null){
							String content = bo.getFactContent(bo.getEmailContent(Integer.parseInt(templateId)), tmpNbase+tmpA0100, fieldList, userView);
							/*content=content.replaceAll(" ", "");
						    content=content.replaceAll("\\r","");
						    content=content.replaceAll("\\n","");
						    content=content.replaceAll("\\r\\n","");*/
							if(getWeixinEnable() && StringUtils.isNotEmpty(ConstantParamter.getAttribute("wx", "corpid"))) {
                                flag = WeiXinBo.sendMsgToPerson(vo.getString(username), topic, content, picUrl, url);
                            }
						    
						    //推送至钉钉 chenxg 2017-06-01
			                if(getDingTalk() && StringUtils.isNotEmpty(ConstantParamter.getAttribute("DINGTALK","corpid"))){
			                    flag = DTalkBo.sendMessage(vo.getString(username), topic, content, "", "");
			                }
						}
					}
				}
			} else {
				throw GeneralExceptionHandler.Handle(new Exception("未找到通知模板，请先设定通知模板"));
			}
		} catch (Exception e){
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return flag;
	}
	
	/**
	 * 发送短信
	 * @param r5400 计划r5400
	 * @param flag 1:启动计划;2:发布成绩
	 */
	private void sendSMS(String r5400, String flag) throws Exception{
//		System.out.println("发送短信操作："+messageTmpId);
		
		ResultSet rs = null;
		try {
			String phone=ConstantParamter.getMobilePhoneField().toLowerCase();
			RecordVo vo = null;
			ContentDAO dao=new ContentDAO(conn);
			
			EmailTemplateBo bo = new EmailTemplateBo(conn);
			String templateId = "";
			if("1".equals(flag)) {
                templateId = this.getMessageTmpId();
            } else {
                templateId = this.getMessageSueId();
            }
			if(templateId!=null&&templateId.length()>0){
				ArrayList fieldList=bo.getTemplateFieldInfo(Integer.parseInt(templateId),2);
				String sql = "select nbase,a0100 from r55 where r5400="+r5400;
				rs = dao.search(sql);
				while(rs.next()){
					String tmpNbase = rs.getString("nbase");
					String tmpA0100 = rs.getString("a0100");
					
					vo = new RecordVo(tmpNbase+"A01");
					vo.setString("a0100", tmpA0100);
					if(dao.isExistRecordVo(vo)){
						if(vo!=null){
							//String _phone=vo.getString(phone);
							
							String phones = tmpNbase + tmpA0100;
							String content = bo.getFactContent(bo.getEmailContent(Integer.parseInt(templateId)), tmpNbase+tmpA0100, fieldList, userView);
							content=content.replaceAll(" ", "");
						    content=content.replaceAll("\\r","");
						    content=content.replaceAll("\\n","");
						    content=content.replaceAll("\\r\\n","");
							SmsBo sbo = new SmsBo(conn);
							sbo.sendMessage(userView, phones, content);
						}
					}
				}//end while
			} else {
				throw new Exception("未找到通知模板，请先设定通知模板");
			}
		} catch (Exception e) {
			throw e;
			//e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	/**
	 * 发送邮件
	 * @param list 计划ids
	 * @param flag 1:启动计划;2:发布成绩
	 */
	public void sendEMail(String r5400,String flag) throws Exception
	{
		ResultSet rs = null;
		int count = 0;
		int error = 0;
		StringBuffer personName = new StringBuffer();
		try {
		    ArrayList<LazyDynaBean> emailList = new ArrayList<LazyDynaBean>();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String email=ConstantParamter.getEmailField().toLowerCase();
			//System.out.println(email);
			//String loguser=ConstantParamter.getLoginUserNameField().toLowerCase();
			//String logpassword=ConstantParamter.getLoginPasswordField();
			//String hrp_logon_url=SystemConfig.getProperty("hrp_logon_url");
			RecordVo vo = null;
			ContentDAO dao=new ContentDAO(conn);
			
			StringBuffer buf=new StringBuffer();//邮件内容
			StringBuffer title=new StringBuffer();//邮件标题
			
			EmailTemplateBo bo = new EmailTemplateBo(conn);
			String templateId = "";
			if("1".equals(flag)) {
                templateId = this.getMessageTmpId();
            } else {
                templateId = this.getMessageSueId();
            }

			if(templateId!=null&&templateId.length()>0){
				HashMap hashmap = bo.getSubject(templateId);
				ArrayList fieldList=bo.getTemplateFieldInfo(Integer.parseInt(templateId),2);
				rs=dao.search("select address from email_name where id="+templateId);
				if(rs.next()){
					String address = rs.getString("address");
					email = address.substring(0, address.indexOf(":")).toLowerCase().trim();
				}
				
				String sql = "select nbase,a0100,a0101 from r55 where r5400="+r5400;
				rs = dao.search(sql);
				while(rs.next()){
				    count++;
					String tmpNbase = rs.getString("nbase");
					String tmpA0100 = rs.getString("a0100");
					
					vo = new RecordVo(tmpNbase+"A01");
					vo.setString("a0100", tmpA0100);
					if(dao.isExistRecordVo(vo)){
						if(vo!=null){
						    LazyDynaBean bean = new LazyDynaBean();
							buf.setLength(0);
							title.setLength(0);
							String email_address=vo.getString(email);
							//String a0101=vo.getString("a0101");
							//String username=vo.getString(loguser);
							//String password=vo.getString(logpassword);
							//String etoken=PubFunc.convertTo64Base(username+","+password);
							if(!bo.isMail(email_address)){
							    if(error < 5) {
                                    personName.append(rs.getString("a0101") + ",");
                                } else if(error == 5) {
                                    personName.append(rs.getString("a0101") + "...等");
                                }
							    error++;
								continue;
							}
							if("1".equals(flag)){
								title.append("培训考试通知");
							}else if("2".equals(flag)){
								title.append("培训考试成绩发布通知");
							}
							String content = bo.getFactContent(bo.getEmailContent(Integer.parseInt(templateId)), tmpNbase+tmpA0100, fieldList, userView);
							content = content.replaceAll("\r\n", "<br/>");
							content = content.replace("\r", "<br/>");
							content = content.replace("\n", "<br/>");
							bean.set("subject", title.toString());
							bean.set("bodyText", content);
							bean.set("toAddr", email_address);
							emailList.add(bean);
						}
					}
				}//end while
				
				if(emailList != null && emailList.size() > 0) {
				    AsyncEmailBo AsyncEmailBo = new AsyncEmailBo(this.conn, userView);
				    AsyncEmailBo.send(emailList);
				}
			} else {
				throw new Exception("未找到通知模板，请先设定通知模板");
			}
			
			if(count > error && error > 0){
			    String personNames = "";
			    if(error < 5) {
                    personNames = personName.substring(0, personName.length()-1);
                } else {
                    personNames = personName.toString();
                }
			    
                StringBuffer Exception = new StringBuffer("考试计划已启动，以下"+error+"人由于邮箱格式不正确或没有邮箱，邮件发送失败：<BR>&nbsp;&nbsp;&nbsp;&nbsp;");
                if("2".equals(flag)) {
                    Exception = new StringBuffer("考试成绩已发布，以下"+error+"人由于邮箱格式不正确或没有邮箱，邮件发送失败：<BR>&nbsp;&nbsp;&nbsp;&nbsp;");
                }
                
                Exception.append(personNames);
                throw new Exception(Exception.toString());
            } else if(error > 0) {
                throw new Exception("邮件发送失败，请检查邮箱或邮箱地址指标配置是否正确！");
            }
			
		} catch (Exception e) {
			throw e;
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public Boolean getEmailEnable() {
		return emailEnable;
	}

	public void setEmailEnable(Boolean emailEnable) {
		this.emailEnable = emailEnable;
	}

	public Boolean getSmsEnable() {
		return smsEnable;
	}

	public void setSmsEnable(Boolean smsEnable) {
		this.smsEnable = smsEnable;
	}

	public String getMessageTmpId() {
		return messageTmpId;
	}

	public void setMessageTmpId(String messageTmpId) {
		this.messageTmpId = messageTmpId;
	}

	public String getMessageSueId() {
		return messageSueId;
	}

	public void setMessageSueId(String messageSueId) {
		this.messageSueId = messageSueId;
	}
	
	
	public Boolean getAutoCompute() {
		return autoCompute;
	}

	public void setAutoCompute(Boolean autoCompute) {
		this.autoCompute = autoCompute;
	}

	public Boolean getAutoRelease() {
		return autoRelease;
	}

	public void setAutoRelease(Boolean autoRelease) {
		this.autoRelease = autoRelease;
	}

	public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getPlanName(String planId)
	{
		String planName = "";
		String sql = "SELECT R5401 FROM R54 WHERE R5400=" + planId;
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try
		{
			rs = dao.search(sql);
			if(rs.next()) {
                planName = rs.getString("R5401");
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
			    if(rs != null) {
                    rs.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return planName;
	}
    
    /**
     * id解密并判断id是否超过1000个，生成list
     * @param sels
     * @return
     */
    public static ArrayList getList(String sels) {
        ArrayList list = new ArrayList();
        if (sels == null || sels.length() < 1) {
            return list;
        }

        String[] sel = sels.split(",");
        String id = "";
        int a = 0;
        for (int i = 0; i < sel.length; i++) {
            String value = PubFunc.decrypt(SafeCode.decode(sel[i]));
            if (a > 0) {
                id += ",";
            }

            id += value;
            a++;

            if (a == 1000) {
                list.add(id);
                id = "";
                a = 0;
            }
        }

        if (id != null && id.length() > 0) {
            list.add(id);
        }

        return list;
    }
    /**
     * 发送待办任务
     * @param r5400 考试计划Id
     * @param pendingStatus 待办状态=0：待办；=1：已办；=3：已阅；=4：无效
     * @return
     */
    public boolean sendPendingTask(String r5400, int pendingStatus) throws Exception {
        boolean flag = false;
        ResultSet rs = null;
        try {
            String extFlag = "TRAIN_EXAM_" + r5400;
            String pendingTitle = "";
            String paperId = "";
            String answerType = "";
            String planState = "";
            ContentDAO dao = new ContentDAO(conn);
            StringBuffer sqlBuf = new StringBuffer();
            sqlBuf.append("select r5401,r5300,r5409,r5411,r5405,r5406 from r54");
            sqlBuf.append(" where r5400=" + r5400);
            
            rs = dao.search(sqlBuf.toString());
            if(rs.next()) {
                String start = "";
                String end = "";
                Date startDate = rs.getDate("r5405");
                if(startDate != null) {
                    start = DateUtils.format(startDate, "yyyy-MM-dd HH:mm:ss");
                }
                
                Date endDate = rs.getDate("r5406");
                if(endDate != null) {
                    end = DateUtils.format(endDate, "yyyy-MM-dd HH:mm:ss");
                }
                
                pendingTitle = rs.getString("r5401");
                if(StringUtils.isNotEmpty(start) || StringUtils.isNotEmpty(end)) {
                    pendingTitle += "(考试时间：" + start + "~" + end + ")";
                }
                
                paperId = rs.getString("r5300");
                answerType = rs.getString("r5409");
                planState = rs.getString("r5411");
            }
            
            String url = "/train/trainexam/paper/preview/ExamPlanIframe.jsp?src=" 
                    + "/train/trainexam/paper/preview/paperspreview.do?b_query=link`home=5`r5300=" + SafeCode.encode(PubFunc.encrypt(paperId)) 
                    + "`exam_type=2`flag=2`returnId=2`paperState=0`plan_id=" + SafeCode.encode(PubFunc.encrypt(r5400));
            if("2".equals(answerType)) {
                url = "/train/trainexam/paper/preview/ExamPlanIframe.jsp?src="
                    + "/train/trainexam/paper/preview/paperspreview.do?b_single=link`home=5`r5300=" + SafeCode.encode(PubFunc.encrypt(paperId))
                    + "`current=1`exam_type=2`flag=2`returnId=2`paperState=0`paper_id=" + SafeCode.encode(PubFunc.encrypt(r5400));
            }
                
            String username = "";
            RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
            if (login_vo == null) {
                username = "username";
            } else {
                String login_name = login_vo.getString("str_value").toLowerCase();
                int idx = login_name.indexOf(",");
                if (idx == -1) {
                    username = "username";
                } else {
                    username = login_name.substring(0, idx);
                    if ("#".equals(username) || "".equals(username)) {
                        username = "username";
                    }

                }
            }
            
            String sql = "select nbase,a0100 from r55 where r5400=" + r5400;
            rs = dao.search(sql);
            while (rs.next()) {
                String tmpNbase = rs.getString("nbase");
                String tmpA0100 = rs.getString("a0100");
                pendingTaskData(tmpNbase, tmpA0100, pendingTitle, url, extFlag, username, pendingStatus, planState);
            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return flag;
    }
    
    public boolean pendingTaskData (String nbase, String a0100, String pendingTitle, String url, String extFlag,
            String userName, int pendingStatus, String planState) {
        boolean flag = false;
        RowSet rs = null; 
        try {
            PendingTask pendingTask = new PendingTask();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String date = format.format(new Date());
            
            RecordVo vo = new RecordVo(nbase + "A01");
            vo.setString("a0100", a0100);
            ContentDAO dao = new ContentDAO(conn);
            if (dao.isExistRecordVo(vo)) {
                if (vo != null) {
                    String receiver = vo.getString(userName);
                    ArrayList<String> valueList = new ArrayList<String>();
                    valueList.add(extFlag);
                    valueList.add(receiver);
                    rs = dao.search("select pending_id from t_hr_pendingtask where ext_flag=? and receiver=?", valueList);
                    if(rs.next()) {
                        StringBuffer update = new StringBuffer();
                        update.append("update t_hr_pendingtask set");
                        update.append(" Pending_status=" + pendingStatus + ",");
                        update.append("lasttime=?");
                        update.append(" where ext_flag=?");
                        update.append(" and receiver=?");
                        //考试计划启动或暂停时，不更改已办的待办状态
                        if("09".equals(planState) || "05".equals(planState)) {
                            update.append("  and pending_status<>1");
                        }
                        
                        valueList.clear();
                        valueList.add(date);
                        valueList.add(extFlag);
                        valueList.add(receiver);
                        dao.update(update.toString(), valueList);
                        
                        String pendingId = rs.getString("pending_id");
                        flag = pendingTask.updatePending("TR", pendingId, pendingStatus, "培训考试", this.userView);
                    } else {
                        IDGenerator idg = new IDGenerator(2, conn);
                        String pendingId = idg.getId("pengdingTask.pengding_id");
                        RecordVo ptVo = new RecordVo("t_hr_pendingtask");
                        ptVo.setDate("create_time", new Date());
                        ptVo.setDate("lasttime", new Date());
                        ptVo.setString("sender", this.userView.getUserName());
                        ptVo.setString("pending_type", "20");
                        ptVo.setString("pending_title", pendingTitle);
                        ptVo.setString("pending_url", url);
                        ptVo.setString("pending_status", pendingStatus + "");
                        ptVo.setString("pending_level", "0");
                        ptVo.setInt("bread", 0);
                        ptVo.setString("ext_flag", extFlag);
                        ptVo.setString("pending_id", pendingId);
                        ptVo.setString("receiver", receiver);
                        dao.addValueObject(ptVo);
                        
                        flag = pendingTask.insertPending(pendingId, "TR", pendingTitle, this.userView.getDbname() + this.userView.getA0100(),
                                nbase + a0100, url + "&isThird=1", pendingStatus, 0, "培训考试", this.userView);
                    }
                    
                }
            }
        }catch (Exception e) {
            flag = false;
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        
        return flag;
    }
    /**
     * 删除待办任务
     * @param planIds 考试计划Id
     * @return
     */
    public boolean deletePendingTask(ArrayList<ArrayList<String>> planIds) throws Exception {
        boolean flag = false;
        RowSet rs = null;
        try {
            if(planIds.isEmpty()) {
                return flag;
            }
            
            PendingTask task = new PendingTask();
            ContentDAO dao = new ContentDAO(conn);
            StringBuffer sqlBuf = new StringBuffer();
            sqlBuf.append("select pending_id from t_hr_pendingtask");
            sqlBuf.append(" where Ext_flag in (");
            ArrayList<String> valueList = new ArrayList<String>();
            for(int i = 0; i < planIds.size(); i++) {
                ArrayList<String> ids = planIds.get(i);
                valueList.add("TRAIN_EXAM_" + ids.get(0));
                sqlBuf.append("?,");
            }
            
            if(sqlBuf.toString().endsWith(",")) {
                sqlBuf.setLength(sqlBuf.length() - 1);
            }
            
            sqlBuf.append(")");
            
            rs = dao.search(sqlBuf.toString(), valueList);
            valueList.clear();
            sqlBuf.setLength(0);
            sqlBuf.append("delete from from t_hr_pendingtask");
            sqlBuf.append(" where pending_id in (");
            while (rs.next()) {
                valueList.add(rs.getString("pending_id"));
                sqlBuf.append("?,");
            }
            
            if(valueList.isEmpty()) {
                return flag;
            }
            
            if(sqlBuf.toString().endsWith(",")) {
                sqlBuf.setLength(sqlBuf.length() - 1);
            }
            
            sqlBuf.append(")");
            dao.update(sqlBuf.toString(), valueList);
            
            for(String taskId : valueList) {
                task.deletePending("TR", taskId, 100, "培训考试");
            }
            
            flag = true;
        } catch (Exception e) {
            flag = false;
            throw GeneralExceptionHandler.Handle(e);
        }
        return flag;
    }
    /**
     * 重考更新待办任务任务
     * @param planIds 考试计划Id
     * @return
     */
    public boolean updatePendingTask(ArrayList<ArrayList<String>> students, int pendingStatus) throws Exception {
        boolean flag = false;
        RowSet rs = null;
        try {
            if(students.isEmpty()) {
                return flag;
            }
            
            ArrayList<String> student = students.get(0);
            String r5400 = student.get(0);
            loadMessageParam(r5400);
            if(!getPendingTaskEnable()) {
                return flag;
            }
            
            ContentDAO dao = new ContentDAO(conn);
            String pendingTitle = "";
            String paperId = "";
            String answerType = "";
            String planState = "";
            StringBuffer sqlBuf = new StringBuffer();
            sqlBuf.append("select r5401,r5300,R5409,r5411,R5405,R5406 from r54");
            sqlBuf.append(" where r5400=" + r5400);
            
            rs = dao.search(sqlBuf.toString());
            if(rs.next()) {
                pendingTitle = rs.getString("r5401");
                paperId = rs.getString("r5300");
                answerType = rs.getString("r5409");
                planState = rs.getString("r5411");
            }
            //考试计划暂停时重考将待办改为无效（4），考试计划启动时再将待办状态改为待办（0）
            if("09".equals(planState)) {
                pendingStatus = 4;
                planState = "";
            } else if("05".equals(planState)) {
                planState = "";
            }
            
            String url = "/train/trainexam/paper/preview/ExamPlanIframe.jsp?src=" 
                    + "/train/trainexam/paper/preview/paperspreview.do?b_query=link`home=5`r5300=" + SafeCode.encode(PubFunc.encrypt(paperId))
                    + "`exam_type=2`flag=2`returnId=2`paperState=0`plan_id=" + SafeCode.encode(PubFunc.encrypt(r5400));
            if("2".equals(answerType)) {
                url = "/train/trainexam/paper/preview/ExamPlanIframe.jsp?src="
                    + "/train/trainexam/paper/preview/paperspreview.do?b_single=link`home=5`r5300=" + SafeCode.encode(PubFunc.encrypt(paperId))
                    + "`current=1`exam_type=2`flag=2`returnId=2`paperState=0`paper_id=" + SafeCode.encode(PubFunc.encrypt(r5400));
            }
            
            String userName = "";
            RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
            if (login_vo == null) {
                userName = "username";
            } else {
                String login_name = login_vo.getString("str_value").toLowerCase();
                int idx = login_name.indexOf(",");
                if (idx == -1) {
                    userName = "username";
                } else {
                    userName = login_name.substring(0, idx);
                    if ("#".equals(userName) || "".equals(userName)) {
                        userName = "username";
                    }

                }
            }
            
            for(ArrayList<String> values : students) {
                r5400 = values.get(0);
                String nbase = values.get(1);
                String a0100 = values.get(2);
                pendingTaskData(nbase, a0100, pendingTitle, url, "TRAIN_EXAM_" + r5400, userName, pendingStatus, planState);
            }
            
            flag = true;
        } catch (Exception e) {
            flag = false;
            throw GeneralExceptionHandler.Handle(e);
        }
        return flag;
    }
    /**
     * 删除考生时删除待办任务
     * @param students 考生信息
     * @return
     */
    public boolean deleteStudentsPendingTask(ArrayList<ArrayList<String>> students) throws Exception {
        boolean flag = false;
        RowSet rs = null;
        try {
            if(students.isEmpty()) {
                return flag;
            }
            
            ArrayList<String> student = students.get(0);
            String r5400 = student.get(0);
            
            String userName = "";
            RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
            if (login_vo == null) {
                userName = "username";
            } else {
                String login_name = login_vo.getString("str_value").toLowerCase();
                int idx = login_name.indexOf(",");
                if (idx == -1) {
                    userName = "username";
                } else {
                    userName = login_name.substring(0, idx);
                    if ("#".equals(userName) || "".equals(userName)) {
                        userName = "username";
                    }

                }
            }
            
            PendingTask task = new PendingTask();
            ContentDAO dao = new ContentDAO(conn);
            StringBuffer sql = new StringBuffer();
            sql.append("select pending_id from t_hr_pendingtask");
            sql.append(" where Ext_flag =? and receiver in (");
            ArrayList<String> valueList = new ArrayList<String>();
            valueList.add("TRAIN_EXAM_" + r5400);
            for(ArrayList<String> ids : students) {
                String nbase = ids.get(1);
                String a0100 = ids.get(2);
                RecordVo vo = new RecordVo(nbase + "A01");
                vo.setString("a0100", a0100);
                if (dao.isExistRecordVo(vo)) {
                    if (vo != null) {
                        String receiver = vo.getString(userName);
                        valueList.add(receiver);
                        sql.append("?,");
                    }
                }
            }
            
            if(valueList.size() < 2) {
                return flag;
            }
            
            if(sql.toString().endsWith(",")) {
                sql.setLength(sql.length() - 1);
            }
            
            sql.append(")");
            rs = dao.search(sql.toString(), valueList);
            valueList.clear();
            sql.setLength(0);
            sql.append("delete from from t_hr_pendingtask");
            sql.append(" where pending_id in (");
            while (rs.next()) {
                valueList.add(rs.getString("pending_id"));
                sql.append("?,");
            }
            
            if(valueList.isEmpty()) {
                return flag;
            }
            
            if(sql.toString().endsWith(",")) {
                sql.setLength(sql.length() - 1);
            }
            
            sql.append(")");
            dao.update(sql.toString(), valueList);
            
            for(String taskId : valueList) {
                task.deletePending("TR", taskId, 100, "培训考试");
            }
            
            flag = true;
        } catch (Exception e) {
            flag = false;
            throw GeneralExceptionHandler.Handle(e);
        }
        return flag;
    }
    
    public Boolean getPendingTaskEnable() {
        return pendingTaskEnable;
    }

    public void setPendingTaskEnable(Boolean pendingTaskEnable) {
        this.pendingTaskEnable = pendingTaskEnable;
    }
    
    
}
