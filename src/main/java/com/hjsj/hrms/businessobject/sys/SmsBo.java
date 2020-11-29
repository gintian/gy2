/**
 * 
 */
package com.hjsj.hrms.businessobject.sys;

import com.blogsky.smsif.*;
import com.blogsky.smsif.transport.HttpTool;
import com.blogsky.smsif.transport.SimpleHttpEnSmsSender;
import com.cmpp2.CmppSession;
import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.sys.cmpp.MsgConfig;
import com.hjsj.hrms.businessobject.sys.cmpp.SubmitSms;
import com.hjsj.hrms.interfaces.sys.SmsProxy;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CreateSequence;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.smslib.InboundMessage;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jul 28, 20064:14:34 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SmsBo {

	private Connection conn=null;
	private UserView userview=null;
	/**串口*/
	private String comm_port="COM1";
	/**sim保护密码*/
	private String pin="0000";
	
	/**短信猫=0|短信网关=1*/
	private String flag="0";
	/**短信网关参数*/
	private String username;
	private String password;
	private String up_url;
	private String down_url;
	private String channelid;
	private String service;
	private String spname;
	private ArrayList modemPortList = new ArrayList();
	
	/**短信地址的提供方式
	 * =false按人员编码,
	 * =true按实际邮件地址
	 */
	private boolean bflag=false;
	
	public SmsBo(Connection conn) {
		this.conn=conn;
		initparamter();
	}
	public SmsBo(Connection conn,UserView userview) {
		this.conn=conn;
		this.userview=userview;
		initparamter();
	}
	
	private class SmsSendTread implements Runnable{

		SmsBo bo;
		UserView userView;
		String receiver;
		String msg;
		public SmsSendTread(SmsBo bo){
			this.bo = bo;
		}
		@Override
        public void run() {
			Connection con = null;
			try {
				con = AdminDb.getConnection();
				this.bo.conn = con;
				this.bo.sendMessage(userView, receiver, msg, true);
			} catch (GeneralException e) {
				e.printStackTrace();
			}finally{
				PubFunc.closeResource(con);
				PubFunc.closeResource(conn);
			}
		}
		
		public void setMessageConfig(UserView userView,String receiver,String msg){
			  this.userView = userView;
			  this.receiver = receiver;
			  this.msg = msg;
		}
	}
	
	
	/**
	 * 如果参数未定义，则按默认值处理
	 */
	private void initparamter()
	{
        RecordVo sms_vo=ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
        if(sms_vo==null) {
			return ;
		}
        String param=sms_vo.getString("str_value");
        if(param==null|| "".equals(param)) {
			return;
		}
        Element ele=null;
        try
        {
	        Document doc = PubFunc.generateDom(param);
	        Element root=doc.getRootElement();
	        String flagvalue=root.getAttributeValue("flag");
	        if(flagvalue==null||flagvalue.length()==0) {
				flagvalue="0";
			}
	        this.flag=flagvalue;
	        String xpath = "//port[@valid=\"true\"]";	        
            XPath reportPath = XPath.newInstance(xpath);
            List childlist = reportPath.selectNodes(doc);
            if(childlist.size()!=0)
            {
            	ele=(Element)childlist.get(0);
            	this.comm_port=ele.getAttributeValue("name");
            	this.pin=ele.getAttributeValue("pin");
            	
            	for (int i = 0; i < childlist.size(); i++) {
            		ele=(Element)childlist.get(i);
            		String company = ele.getAttributeValue("company");
            		company = company == null ? "" : company;
            		String modeltype = ele.getAttributeValue("modeltype");
            		modeltype = modeltype == null ? "" :modeltype;
            		String bit = ele.getAttributeValue("bit");
            		bit = bit == null ? "" : bit;
            		String comPort = ele.getAttributeValue("name");
            		comPort = comPort == null ? "" : comPort;
            		String pin = ele.getAttributeValue("pin");
            		pin = pin == null ? "" : pin;
            		LazyDynaBean bean =new LazyDynaBean();
            		bean.set("company", company);
            		bean.set("modeltype", modeltype);
            		bean.set("bit", bit);
            		bean.set("com", comPort);
            		bean.set("pin", pin);
            		modemPortList.add(bean);
            	}
            }
            /**短信网关参数*/
    		xpath="/ports/gateway";
    		reportPath = XPath.newInstance(xpath);
    		childlist = reportPath.selectNodes(doc);
            if(childlist.size()!=0)
            {
	            ele=(Element)childlist.get(0);
	            this.username=ele.getAttributeValue("username");
	            this.password=ele.getAttributeValue("password");
	            this.channelid=ele.getAttributeValue("channelid");
	            this.service=ele.getAttributeValue("service");
	            this.spname = ele.getAttributeValue("spname");
	            childlist=ele.getChildren("up_url");
	            if(childlist.size()!=0)
	            {
	            	Element c_ele=(Element)childlist.get(0);
	            	this.up_url=c_ele.getText();
	            }
	            childlist=ele.getChildren("down_url");
	            if(childlist.size()!=0)
	            {	
	            	Element c_ele=(Element)childlist.get(0);
	            	this.down_url=c_ele.getText();            
	            }
            }
        }
            
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	//throw GeneralExceptionHandler.Handle(ex);
        }     		
	}
	/**
	 * 如果短信的内容太长，自动切分成每条200字符
	 * @param msg
	 * @return
	 */
	public ArrayList getMsgList(String msg)
	{
		ArrayList list=new ArrayList();
//		int len=0;
//		if(msg!=null)
//		{
//			//msg=msg.replaceAll(" ", "");
//			//msg=msg.replaceAll("　", "");
//		}
//		len=msg.getBytes().length;
//		if(/*msg.length()*/len<=140)
//		{
//			list.add(msg);
//			return list;
//		}
//
//		while(true)
//		{
//			String temp=PubFunc.splitString(msg,140);
//			list.add(temp);				
//			byte[] bytes=msg.getBytes();
//			len=bytes.length;
//			
//			/**实际的长度*/
//			int rlen=0;
//			int j=0;
//			for(int i=0;i<140;i++)
//			{
//				if(bytes[i]<0)
//				{
//					j++;
//				}
//			}
//			if((j%2)==1)
//				rlen=140-1;		
//			else
//				rlen=140;
//			
//			
//			
//			byte[] sub=new byte[len-rlen];
//			System.arraycopy(bytes,rlen,sub,0,len-rlen);
//			msg=new String(sub);
//			if(len-140<=140)
//			{
//				list.add(msg);	
//				break;
//			}
//		}
		
		list.add(msg);
		return list;
	}
	/**
	 * 
	 * @param sender
	 * @param receiver
	 * @param phone_num
	 * @param msg
	 * @param flag 短信信箱标识 =-1 待发信箱 =0 失败 =1成功
	 * @return
	 * @throws GeneralException
	 */
	private boolean addMessageItem(String sender,String receiver,String phone_num,String msg,int flag)throws GeneralException
	{
		boolean bflag=false;
		if(msg==null|| "".equals(msg)) {
			return false;
		}
		if(sender==null|| "".equals(sender)||receiver==null|| "".equals(receiver)) {
			return false;
		}
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList list=getMsgList(msg);
			for(int i=0;i<list.size();i++)
			{
				String zj=CreateSequence.getUUID();
				RecordVo  vo=new RecordVo("t_sys_smsbox");
				vo.setString("sms_id",zj);
				vo.setString("sender",sender);
				vo.setString("receiver",receiver);
				vo.setString("mobile_no",phone_num);
				vo.setString("msg",(String)list.get(i));
				vo.setInt("send_order",i+1);
				if(flag==1)
				{
					vo.setInt("sended_count",1);
					
					vo.setDate("send_time", new Date()/*PubFunc.DoFormatSystemDate(true)*/);
				}
				else
				{
					vo.setInt("sended_count",0);
				}
				vo.setInt("status",flag);
				dao.addValueObject(vo);
			}
			bflag=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	
	/**
	 * 
	 * @param sender
	 * @param receiver
	 * @param phone_num
	 * @param msg
	 * @param flag 短信信箱标识 =-1 待发信箱 =0 失败 =1成功
	 * @return
	 * @throws GeneralException
	 */
	public boolean addAcceptMessage(String sender,String receiver,String msg,int flag,String date)throws GeneralException
	{
		boolean bflag=false;
		if(msg==null|| "".equals(msg)) {
			return false;
		}
		if(sender==null|| "".equals(sender)) {
			return false;
		}
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList list=getMsgList(msg);
			
			String field_name = getMobileNumber();
			if(field_name==null|| "".equals(field_name)) {
				throw new GeneralException(ResourceFactory.getProperty("error.sms.notdefine"));
			}
			LazyDynaBean bean = getUserNameByMobile(sender,field_name);
			for(int i=0;i<list.size();i++)
			{
				String zj=CreateSequence.getUUID();
				RecordVo  vo=new RecordVo("t_sys_smsbox");
				vo.setString("sms_id",zj);
				vo.setString("sender",sender);
				vo.setString("mobile_no",sender);
				vo.setString("msg",(String)list.get(i));
				if (bean != null) {
					vo.setString("a0100", (String) bean.get("a0100"));
					vo.setString("nbase", (String) bean.get("nbase"));
					vo.setString("sender",(String) bean.get("a0101"));
				}
				vo.setDate("send_time", DateUtils.getDate(date, "yyyy-MM-dd HH:mm:ss"));
				vo.setInt("status",flag);
				dao.addValueObject(vo);
			}
			bflag=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	
	/**
	 * 根据电话号码获得用户名及nbase及a0100
	 * @param mobile
	 * @return
	 */
	public LazyDynaBean getUserNameByMobile(String mobile,String field) {
		LazyDynaBean bean = null;
		List dbList = getAllNbase();
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			for (int i = 0; i < dbList.size(); i++) {
				String pre = (String) dbList.get(i);
				StringBuffer buff = new StringBuffer();
				buff.append("select a0100,a0101 from ");
				buff.append(pre);
				buff.append("a01 where ");
				buff.append(field);
				buff.append("='");
				buff.append(mobile);
				buff.append("'");
				
				rs = dao.search(buff.toString());
				if (rs.next()) {
					bean = new LazyDynaBean();
					bean.set("a0100", rs.getString("a0100"));
					String a0101 = rs.getString("a0101");
					a0101 = a0101 == null ? "" : a0101;
					bean.set("a0101", a0101);
					bean.set("nbase", pre);
					break;
				}
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
		return bean;
		
	}
	
	/**
	 * 获得所有人员库
	 * @return
	 */
	private ArrayList getAllNbase() {
		ArrayList list = new ArrayList();
		String sql = "select pre from dbname";
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while (rs.next()) {
				String pre = rs.getString("pre");
				if (pre != null && pre.length() > 0) {
					list.add(pre);
				}
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
		return list;
	}
	/**
	 * 
	 * @param sender
	 * @param receiver
	 * @param phone_num
	 * @param msg
	 * @param username 用户登陆名
	 * @param wid  预警id
	 * @param flag 短信信箱标识 =-1 待发信箱 =0 失败 =1成功
	 * @return
	 * @throws GeneralException
	 */
	private boolean addMessageItem(String sender,String receiver,String phone_num,String msg,String username,String wid,int flag)throws GeneralException
	{
		boolean bflag=false;
		if(msg==null|| "".equals(msg)) {
			return false;
		}
		if(sender==null|| "".equals(sender)||receiver==null|| "".equals(receiver)) {
			return false;
		}
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList list=getMsgList(msg);
			for(int i=0;i<list.size();i++)
			{
				String zj=CreateSequence.getUUID();
				RecordVo  vo=new RecordVo("t_sys_smsbox");
				String msss=list.get(i)!=null&&list.get(i).toString().length()>0?list.get(i).toString():"";
				vo.setString("sms_id",zj);
				vo.setString("sender",sender);
				vo.setString("receiver",receiver);
				vo.setString("mobile_no",phone_num);
				vo.setString("msg",(String)list.get(i));
				vo.setInt("send_order",i+1);
				vo.setString("username", username);
				vo.setString("wid", wid);
				if(flag==1)
				{
					vo.setInt("sended_count",1);
					
					vo.setDate("send_time", new Date()/*PubFunc.DoFormatSystemDate(true)*/);
				}
				else
				{
					vo.setInt("sended_count",0);
				}
				vo.setInt("status",flag);
				/**自助帐号的用户或关联的帐号的业务用户（业务用户：getDbname()=null） 填写库前缀和人员编号字段 liwc*/
				if(this.userview!=null&&this.userview.getDbname()!=null&&this.userview.getDbname().length()>0){
					vo.setString("nbase", this.userview.getDbname());
					vo.setString("a0100", this.userview.getA0100());
				}
				dao.addValueObject(vo);
			}
			bflag=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	/**
	 * 增加短信条目,把需要发的短信插入待发信箱中，由后台程序自动发送
	 * @param sender 直接为姓名
	 * @param receiver 直接为姓名
	 * @param phone_num 手机号码
	 * @param msg 短信内容
	 * @return
	 */
	public boolean addMessageItem(String sender,String receiver,String phone_num,String msg)throws GeneralException
	{
		return addMessageItem(sender,receiver,phone_num,msg,-1);
	}
	/**
	 * 求移动电话号码
	 * @return
	 */
	private String getMobileNumber()
	{
        RecordVo vo=ConstantParamter.getConstantVo("SS_MOBILE_PHONE");
        if(vo==null) {
			return "";
		}
        String field_name=vo.getString("str_value");
        if(field_name==null|| "".equals(field_name)) {
			return "";
		}
        FieldItem item=DataDictionary.getFieldItem(field_name);
        if(item==null) {
			return "";
		}
        /**分析是否构库*/
        if("0".equals(item.getUseflag())) {
			return "";
		}
        return field_name; 
	}
	/**
	 * 取得姓名
	 * @param name
	 * @param phone_num
	 * @return
	 */
	private String getA0101String(String name,StringBuffer phone_num)throws GeneralException
	{
		if(name==null|| "".equals(name)) {
			return "";
		}
		String a0101="";
		String dbpre=name.substring(0,3);
		String a0100=name.substring(3);
		RecordVo vo=new RecordVo(dbpre+"a01");
		try
		{
			String field_name=getMobileNumber();
			if(field_name==null|| "".equals(field_name)) {
				throw new GeneralException(ResourceFactory.getProperty("error.sms.notdefine"));
			}
			ContentDAO dao=new ContentDAO(this.conn);
			vo.setString("a0100",a0100);
			vo=dao.findByPrimaryKey(vo);
			if(vo==null) {
				return "";
			}
			a0101=vo.getString("a0101");
			phone_num.append(vo.getString(field_name.toLowerCase()));
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}
		return a0101;
	}
	/**
	 * 增加短信条目
	 * @param sender for examples Usr00000001  应库前缀+人员编号
	 * @param receiver Usr00000002 
	 * @param msg
	 * @return
	 */
	public boolean addMessageItem(String sender,String receiver,String msg)throws GeneralException
	{
		boolean bflag=false;
		if(msg==null|| "".equals(msg)) {
			return false;
		}
		StringBuffer phone_num=new StringBuffer();
		try
		{
			/**不关心发送者的移动电话号码*/
			sender= getA0101String(sender,phone_num);
			phone_num.setLength(0);
			receiver=getA0101String(receiver,phone_num);
			if(sender==null|| "".equals(sender)||receiver==null|| "".equals(receiver)) {
				return false;
			}
			bflag=addMessageItem(sender,receiver,phone_num.toString(),msg,-1);
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	/**
	 * 批量发送短信通过modem
	 * @param list
	 * @throws GeneralException
	 */
	private void batchSendMessageByModem(ArrayList list)throws GeneralException
	{
//		SMSSender sender=SMSSender.getInstance(this.comm_port,this.pin);
		SMSSender sender=SMSSender.getInstance(this.modemPortList);
		if(!sender.isAvailable()){// 状态检测，如果不可用 xuj add 2015-7-24如果抛异常则代表发送短信不成功，可也业务层判断，同短信接口方式抛异常
			throw GeneralExceptionHandler.Handle(new GeneralException("短信猫状态异常，跳过短信处理！"));		
		}
		//SMSSender sender=null;
		for(int i=0;i<list.size();i++) {
			outPutMessage(sender,list.get(i));
		}
		//sendMessage("aaa", "bbb", "13801297310", "sss陈猛清你好");
		/*
		  CService srv = new CService(this.comm_port, 9600, "Wavecom", "");		
		  try
		  {
			srv.setSimPin(this.pin);
			srv.setSmscNumber("");
			srv.connect();	
			for(int i=0;i<list.size();i++)
				outPutMessage(srv,list.get(i));
		  }
		  catch(NoResponseException se)
		  {
			  se.printStackTrace();
			  throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.sms.notresponse")));				  
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
			  throw GeneralExceptionHandler.Handle(ex);		  
		  }
		  finally
		  {
				//if(srv.isAlive())
					srv.disconnect();			  
		  }		
		  */
	}
	/**
	 * 通过短信网关发送短信
	 * @param list
	 * @throws GeneralException
	 */
	private void batchSendMessageByGateWay(ArrayList list)throws GeneralException
	{
		  try
		  {
			  if("BKKJ".equals(this.service))
			  {
					SimpleHttpEnSmsSender sender = new SimpleHttpEnSmsSender(this.down_url);
					for(int i=0;i<list.size();i++) {
						outPutMessageByGateWay(list.get(i));
					}
			  }
			  /**集成短信接*/
			  if("HJSJ".equals(this.service))
			  {
					for(int i=0;i<list.size();i++) {
						outPutMessageByGateWayProxy(list.get(i));
					}
			  }
			  if("CMPP2".equals(this.service))
			  {
					for(int i=0;i<list.size();i++) {
						outPutMessageByCMPP2(list.get(i));
					}
			  }
			  if("GSTX".equals(this.service))
			  {
					for(int i=0;i<list.size();i++) {
						outPutMessageByGSTXGateWay(list.get(i));
					}
			  }
			  if("JGJK".equals(this.service))
			  {
					for(int i=0;i<list.size();i++) {
						outPutMessageByJGJKGateWay(list.get(i));
					}
			  }
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
			  throw GeneralExceptionHandler.Handle(ex);		  
		  }
		  finally
		  {
  
		  }		
	}
	/**
	 * 中国移动cmpp2协议网关
	 * @param vo
	 * @return
	 * @throws GeneralException
	 */
	private boolean outPutMessageByCMPP2(Object vo)throws GeneralException
	{
		boolean bflag=false;
		String sender=null;
		String receiver=null;
		String phone_num=null;
		String msg=null;
		String sms_id=null;
		LazyDynaBean dyvo=null;
		String username=null;
		String wid=null;
		CmppSession session=null;
		String templateId="";
		String prea0100="";
		try
		{
			if (vo instanceof LazyDynaBean) {
				dyvo= (LazyDynaBean) vo;
			}
			else {
				return false;
			}
			sender=(String)dyvo.get("sender");
			receiver=(String)dyvo.get("receiver");
			phone_num=(String)dyvo.get("phone_num");
			templateId=(String)dyvo.get("templateId");
			prea0100=(String)dyvo.get("pera0100");
			msg=(String)dyvo.get("msg");	
			if(templateId!=null&&templateId.length()>0&&this.userview!=null)
			{
				EmailTemplateBo bo = new EmailTemplateBo(conn);  
				ArrayList templateFieldList=bo.getTemplateFieldInfo(Integer.parseInt(templateId),2);
		        String content=bo.getEmailContent(Integer.parseInt(templateId));
		        String cont=bo.getFactContent(content,prea0100,templateFieldList,this.userview);
		        if(cont!=null&&cont.trim().length()>0) {
					msg=cont.trim();
				}
			}	
			if(msg==null|| "".equals(msg)) {
				return false;
			}
			if(sender==null|| "".equals(sender)||receiver==null|| "".equals(receiver)) {
				return false;
			}
			username=(String)dyvo.get("username");
			wid=(String)dyvo.get("wid");
			if(username==null||username.length()<=0) {
				username="";
			}
			wid=wid!=null&&wid.length()>0?wid:"";
			/**根据sms_id是否为空，分析是重发还是新发的短信*/
			sms_id=(String)dyvo.get("sms_id");
			ArrayList list=getMsgList(msg);
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList paralist=new ArrayList();
			StringBuffer strsql=new StringBuffer();//send_time=?
			strsql.append("update t_sys_smsbox set sended_count=sended_count+1,status=1 where sms_id=?");

//			session = new CmppSession(null);
//			session.setDebug(true);
//			session.connect(this.up_url, Integer.parseInt(this.down_url), this.username, this.password); 
			
			for(int i=0;i<list.size();i++)
			{
//				ShortMessage sms = new SmsShortMessage("66", this.channelid, phone_num, msg,this.username);
//				long msgId = session.submit(sms);
				
				MsgConfig.setConnectCount(3);
				MsgConfig.setIsmgIp(this.up_url);
				MsgConfig.setIsmgPort(Integer.parseInt(this.down_url));
				MsgConfig.setSpCode(this.channelid);
				MsgConfig.setSpId(this.username);
				MsgConfig.setSpSharedSecret(this.password);
				MsgConfig.setServiceId(this.spname);
				MsgConfig.setTimeOut(10000);
				
				List msgList = getMsgList(msg, this.spname);
				SubmitSms sms = new SubmitSms(msgList, phone_num, this.spname);
				sms.run();
				
//				MsgContainer.sendMsg(msg + this.spname, phone_num);
				
//				Timer timer = new Timer();
//				timer.schedule(new MsgActivityTimer(), 10000);
//				MsgContainer2.sendMsg(msg + this.spname, phone_num);
				
//				timer.cancel();
				/**记录到发送成功的信箱中去*/
				paralist.clear();
				if(sms_id==null|| "".equals(sms_id))
					//addMessageItem(sender,receiver,phone_num.toString(),msg,1);
				{
					addMessageItem(sender,receiver,phone_num.toString(),msg,username,wid,1);
				} else
				{
					paralist.add(sms_id);
					dao.update(strsql.toString(),paralist);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception("错误！请检查参数设置！"));					
		}
		finally
		{
			if(session!=null) {
				session.close();
			}
		}
		return bflag;
		
	}
	private boolean outPutMessageByGateWay(Object vo)throws GeneralException
	{
		boolean bflag=false;
		String sender=null;
		String receiver=null;
		String phone_num=null;
		String msg=null;
		String sms_id=null;
		LazyDynaBean dyvo=null;
		String username=null;
		String wid=null;
		String templateId="";
		String prea0100="";
		try
		{
			if (vo instanceof LazyDynaBean) {
				dyvo= (LazyDynaBean) vo;
			}
			else {
				return false;
			}
			sender=(String)dyvo.get("sender");
			receiver=(String)dyvo.get("receiver");
			phone_num=(String)dyvo.get("phone_num");				
			templateId=(String)dyvo.get("templateId");
			prea0100=(String)dyvo.get("pera0100");
			msg=(String)dyvo.get("msg");	
			if(templateId!=null&&templateId.length()>0&&this.userview!=null)
			{
				EmailTemplateBo bo = new EmailTemplateBo(conn);  
				ArrayList templateFieldList=bo.getTemplateFieldInfo(Integer.parseInt(templateId),2);
		        String content=bo.getEmailContent(Integer.parseInt(templateId));
		        String cont=bo.getFactContent(content,prea0100,templateFieldList,this.userview);
		        if(cont!=null&&cont.trim().length()>0) {
					msg=cont.trim();
				}
			}	
			if(msg==null|| "".equals(msg)) {
				return false;
			}
			if(sender==null|| "".equals(sender)||receiver==null|| "".equals(receiver)) {
				return false;
			}
			username=(String)dyvo.get("username");
			wid=(String)dyvo.get("wid");
			if(username==null||username.length()<=0) {
				username="";
			}
			wid=wid!=null&&wid.length()>0?wid:"";
			/**根据sms_id是否为空，分析是重发还是新发的短信*/
			sms_id=(String)dyvo.get("sms_id");
			ArrayList list=getMsgList(msg);
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList paralist=new ArrayList();
			StringBuffer strsql=new StringBuffer();//send_time=?
			strsql.append("update t_sys_smsbox set sended_count=sended_count+1,status=1 where sms_id=?");
			for(int i=0;i<list.size();i++)
			{
				MTEnSms mtsms=new MTEnSms(i,phone_num,msg,"",this.channelid); //参数分别为：序号，目标号码，短信内容，源号码后缀，channelid
				EnSmsMTMessage mtmessage=new EnSmsMTMessage();
				mtmessage.setUsername("shoukai_res");//平台分配的用户名。
				mtmessage.setPassword("shoukai_res");//平台分配的密码。
				mtmessage.append(mtsms);		
				byte[] data=EnSmsMessageTools.toByte(mtmessage);
				data=HttpTool.send(EnSmsMessageTools.toByte(mtmessage),this.down_url);
				EnSmsRespMessage resp=(EnSmsRespMessage)EnSmsMessageTools.toMessage(data);
				Status[] statues=resp.getStatuses();
				if(!(statues.length==1&&statues[0].getData()==0)){
					throw new GeneralException("send sms error!");
				}				
				/**记录到发送成功的信箱中去*/
				paralist.clear();
				if(sms_id==null|| "".equals(sms_id))
					//addMessageItem(sender,receiver,phone_num.toString(),msg,1);
				{
					addMessageItem(sender,receiver,phone_num.toString(),msg,username,wid,1);
				} else
				{
					paralist.add(sms_id);
					dao.update(strsql.toString(),paralist);
				}
			}			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception("错误！请检查参数设置！"));					
		}
		return bflag;
	}
	
	private boolean outPutMessageByGSTXGateWay(Object vo)throws GeneralException {
		boolean bflag=false;
		String sender=null;
		String receiver=null;
		String phone_num=null;
		String msg=null;
		String sms_id=null;
		LazyDynaBean dyvo=null;
		String username=null;
		String wid=null;
		String templateId="";
		String prea0100="";
		try
		{
			if (vo instanceof LazyDynaBean) {
				dyvo= (LazyDynaBean) vo;
			}
			else {
				return false;
			}
			sender=(String)dyvo.get("sender");
			receiver=(String)dyvo.get("receiver");
			phone_num=(String)dyvo.get("phone_num");				
			templateId=(String)dyvo.get("templateId");
			prea0100=(String)dyvo.get("pera0100");
			msg=(String)dyvo.get("msg");	
			if(templateId!=null&&templateId.length()>0&&this.userview!=null)
			{
				EmailTemplateBo bo = new EmailTemplateBo(conn);  
				ArrayList templateFieldList=bo.getTemplateFieldInfo(Integer.parseInt(templateId),2);
		        String content=bo.getEmailContent(Integer.parseInt(templateId));
		        String cont=bo.getFactContent(content,prea0100,templateFieldList,this.userview);
		        if(cont!=null&&cont.trim().length()>0) {
					msg=cont.trim();
				}
			}	
			if(msg==null|| "".equals(msg)) {
				return false;
			}
			if(sender==null|| "".equals(sender)||receiver==null|| "".equals(receiver)) {
				return false;
			}
			username=(String)dyvo.get("username");
			wid=(String)dyvo.get("wid");
			if(username==null||username.length()<=0) {
				username="";
			}
			wid=wid!=null&&wid.length()>0?wid:"";
			/**根据sms_id是否为空，分析是重发还是新发的短信*/
			sms_id=(String)dyvo.get("sms_id");
			ArrayList list=getMsgList(msg);
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList paralist=new ArrayList();
			StringBuffer strsql=new StringBuffer();//send_time=?
			strsql.append("update t_sys_smsbox set sended_count=sended_count+1,status=? where sms_id=?");
			
			// 创建与服务器的连接
			HttpClient client = new HttpClient();
			PostMethod post = new PostMethod(this.down_url);
			String xml = "";
			try {
				for(int i=0;i<list.size();i++) {
					int flag = 0;
					String message = (String) list.get(i);
					xml = HttpSendSmsMessage(phone_num, message);
					post.setRequestHeader("Content-Type","text/html;charset=utf-8");
					post.setRequestBody(xml);
					client.executeMethod(post);
					String res = post.getResponseBodyAsString();
					if (res.contains("<Data>0</Data>")) {
						flag = 1;
					}
					/**记录到发送成功的信箱中去*/
					paralist.clear();
					if(sms_id==null|| "".equals(sms_id)) {
						if (flag == 1) {
							addMessageItem(sender,receiver,phone_num.toString(),message,username,wid,1);
						} else {
							addMessageItem(sender,receiver,phone_num.toString(),message,username,wid,0);
						}
					} else {
						paralist.add(sms_id);
						paralist.add(Integer.valueOf(flag));
						dao.update(strsql.toString(),paralist);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				post.releaseConnection();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception("错误！请检查参数设置！"));					
		}
		return bflag;
	}
	
	private boolean outPutMessageByJGJKGateWay(Object vo)throws GeneralException {
		boolean bflag=false;
		String sender=null;
		String receiver=null;
		String phone_num=null;
		String msg=null;
		String sms_id=null;
		LazyDynaBean dyvo=null;
		String username=null;
		String wid=null;
		String templateId="";
		String prea0100="";
		try
		{
			if (vo instanceof LazyDynaBean) {
				dyvo= (LazyDynaBean) vo;
			}
			else {
				return false;
			}
			sender=(String)dyvo.get("sender");
			receiver=(String)dyvo.get("receiver");
			phone_num=(String)dyvo.get("phone_num");				
			templateId=(String)dyvo.get("templateId");
			prea0100=(String)dyvo.get("pera0100");
			msg=(String)dyvo.get("msg");	
			if(templateId!=null&&templateId.length()>0&&this.userview!=null)
			{
				EmailTemplateBo bo = new EmailTemplateBo(conn);  
				ArrayList templateFieldList=bo.getTemplateFieldInfo(Integer.parseInt(templateId),2);
		        String content=bo.getEmailContent(Integer.parseInt(templateId));
		        String cont=bo.getFactContent(content,prea0100,templateFieldList,this.userview);
		        if(cont!=null&&cont.trim().length()>0) {
					msg=cont.trim();
				}
			}	
			if(msg==null|| "".equals(msg)) {
				return false;
			}
			if(sender==null|| "".equals(sender)||receiver==null|| "".equals(receiver)) {
				return false;
			}
			username=(String)dyvo.get("username");
			wid=(String)dyvo.get("wid");
			if(username==null||username.length()<=0) {
				username="";
			}
			wid=wid!=null&&wid.length()>0?wid:"";
			/**根据sms_id是否为空，分析是重发还是新发的短信*/
			sms_id=(String)dyvo.get("sms_id");
			ArrayList list=getMsgList(msg);
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList paralist=new ArrayList();
			StringBuffer strsql=new StringBuffer();//send_time=?
			strsql.append("update t_sys_smsbox set sended_count=sended_count+1,status=? where sms_id=?");
			
			// 创建与服务器的连接
			iSMSClient2000 client = new iSMSClient2000();

			boolean openFlag = false;
			try {
				if (openFlag = client.OpenSMS(this.username, Integer.parseInt(this.password))){
					
				for(int i=0;i<list.size();i++) {
					int flag = 0;
					String message = (String) list.get(i);
					if (client.SendSMS("", phone_num, message)) {
						flag = 1;
					}

					/**记录到发送成功的信箱中去*/
					paralist.clear();
					if(sms_id==null|| "".equals(sms_id)) {
						if (flag == 1) {
							addMessageItem(sender,receiver,phone_num.toString(),message,username,wid,1);
						} else {
							addMessageItem(sender,receiver,phone_num.toString(),message,username,wid,0);
						}
					} else {
						paralist.add(sms_id);
						paralist.add(Integer.valueOf(flag));
						dao.update(strsql.toString(),paralist);
					}
				}
			}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (openFlag) {
					client.CloseSMS();
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception("错误！请检查参数设置！"));					
		}
		return bflag;
	}
	
	private String HttpSendSmsMessage(String phone, String msg) {
		StringBuffer xml = new StringBuffer();
		String date = DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<ZWTSMSMessage type=\"mt\">");
		xml.append("<Username>");
		xml.append(this.username);
		xml.append("</Username>");
		xml.append("<Password>");
		xml.append(this.password);
		xml.append("</Password>");
		xml.append("<MTSms>");
		xml.append("<SmsID>1</SmsID>");
		xml.append("<Target>");
		xml.append(phone);
		xml.append("</Target>");
		xml.append("<Message> <![CDATA[");
		xml.append(msg);
		xml.append("]]></Message>");
		xml.append("<Source>");
		xml.append(this.channelid);
		xml.append("</Source>");
		xml.append("<Channel>");		
		xml.append("</Channel>");
		xml.append("<MtDate>");
		xml.append(date);
		xml.append("</MtDate>");
		xml.append("</MTSms>");
		xml.append("</ZWTSMSMessage>");

		return xml.toString();
	}
	/**
	 * 通过短信网关集成接口发送短信
	 * @param vo
	 * @return
	 * @throws GeneralException
	 */
	private boolean outPutMessageByGateWayProxy(Object vo)throws GeneralException
	{
		boolean bflag=false;
		String sender=null;
		String receiver=null;
		String phone_num=null;
		String msg=null;
		String sms_id=null;
		LazyDynaBean dyvo=null;
		String username=null;
		String wid=null;
		String templateId="";
		String prea0100="";
		try
		{
			if (vo instanceof LazyDynaBean) {
				dyvo= (LazyDynaBean) vo;
			}
			else {
				return false;
			}
			sender=(String)dyvo.get("sender");
			receiver=(String)dyvo.get("receiver");
			phone_num=(String)dyvo.get("phone_num");
			templateId=(String)dyvo.get("templateId");
			prea0100=(String)dyvo.get("pera0100");
			msg=(String)dyvo.get("msg");	
			if(templateId!=null&&templateId.length()>0&&this.userview!=null)
			{
				EmailTemplateBo bo = new EmailTemplateBo(conn);  
				ArrayList templateFieldList=bo.getTemplateFieldInfo(Integer.parseInt(templateId),2);
		        String content=bo.getEmailContent(Integer.parseInt(templateId));
		        String cont=bo.getFactContent(content,prea0100,templateFieldList,this.userview);
		        if(cont!=null&&cont.trim().length()>0) {
					msg=cont.trim();
				}
			}	
			if(msg==null|| "".equals(msg)) {
				return false;
			}
			if(sender==null|| "".equals(sender)||receiver==null|| "".equals(receiver)) {
				return false;
			}
			username=(String)dyvo.get("username");
			wid=(String)dyvo.get("wid");
			if(username==null||username.length()<=0) {
				username="";
			}
			wid=wid!=null&&wid.length()>0?wid:"";
			/**根据sms_id是否为空，分析是重发还是新发的短信*/
			sms_id=(String)dyvo.get("sms_id");
			ArrayList list=getMsgList(msg);
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList paralist=new ArrayList();
			StringBuffer strsql=new StringBuffer();//send_time=?
			strsql.append("update t_sys_smsbox set sended_count=sended_count+1,status=1 where sms_id=?");
			SmsProxy smsproxy=(SmsProxy)Class.forName(this.down_url).newInstance();
			for(int i=0;i<list.size();i++)
			{
				String str = (String) list.get(i);
				/**发送短信*/
				
				smsproxy.sendMessage(phone_num, str);
				/**记录到发送成功的信箱中去*/
				paralist.clear();
				if(sms_id==null|| "".equals(sms_id)) {
					addMessageItem(sender,receiver,phone_num.toString(),str,username,wid,1);
				} else
				{
					paralist.add(sms_id);
					dao.update(strsql.toString(),paralist);
				}
			}			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception("错误！请检查参数设置！"));					
		}
		return bflag;
	}	
	/**
	 * 批量发送手机短信
	 * @param list 存放的是发送人姓名，接收人姓名，手机号，短信内容,其用LazyDynaBean保存，
	 * 包括sender,receiver,phone_num,msg四个属性,可选sms_id属性可选，如果不为空并且在
	 * 信箱里有此短信，则为重发，要改变状态
	 */
	public synchronized void batchSendMessage(ArrayList list)throws GeneralException
	{
	  if(list==null||list.size()==0) {
		  return;
	  }
	  if("0".equalsIgnoreCase(flag)) {
		  batchSendMessageByModem(list);
	  }
	  if("1".equalsIgnoreCase(flag)) {
		  batchSendMessageByGateWay(list);
	  }
	}
	
	private boolean outPutMessage(SMSSender smsbo,Object vo)throws GeneralException
	{
		boolean bflag=false;
		String sender=null;
		String receiver=null;
		String phone_num=null;
		String msg=null;
		String sms_id=null;
		LazyDynaBean dyvo=null;
		String username=null;
		String wid=null;
		String templateId="";
		String prea0100="";
		EmailTemplateBo bo = new EmailTemplateBo(conn);  
		try
		{
			if (vo instanceof LazyDynaBean) {
				dyvo= (LazyDynaBean) vo;
			}
			else {
				return false;
			}
			sender=(String)dyvo.get("sender");
			receiver=(String)dyvo.get("receiver");
			phone_num=(String)dyvo.get("phone_num");
			templateId=(String)dyvo.get("templateId");
			prea0100=(String)dyvo.get("pera0100");
			msg=(String)dyvo.get("msg");	
			if(templateId!=null&&templateId.length()>0&&this.userview!=null)
			{
				ArrayList templateFieldList=bo.getTemplateFieldInfo(Integer.parseInt(templateId),2);
		        String content=bo.getEmailContent(Integer.parseInt(templateId));
		        String cont=bo.getFactContent(content,prea0100,templateFieldList,this.userview);
		        if(cont!=null&&cont.trim().length()>0) {
					msg=cont.trim();
				}
			}	
			if(msg==null|| "".equals(msg)) {
				return false;
			}
			username=(String)dyvo.get("username");
			wid=(String)dyvo.get("wid");
			if(username==null||username.length()<=0) {
				username="";
			}
			wid=wid!=null&&wid.length()>0?wid:"";			
			if(sender==null|| "".equals(sender)||receiver==null|| "".equals(receiver)) {
				return false;
			}
			sms_id=(String)dyvo.get("sms_id");
			ArrayList list=getMsgList(msg);
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList paralist=new ArrayList();
			StringBuffer strsql=new StringBuffer();//send_time=?
			strsql.append("update t_sys_smsbox set sended_count=sended_count+1,status=1 where sms_id=?");
			for(int i=0;i<list.size();i++)
			{
				/*
				IOutgoingMessage outmsg = new COutgoingMessage(phone_num, (String)list.get(i));
				outmsg.setMessageEncoding(IMessage.MESSAGE_ENCODING_UNICODE);
				outmsg.setStatusReport(true);
				outmsg.setValidityPeriod(8);
				srv.sendMessage(outmsg);
				*/
				smsbo.send(phone_num, (String)list.get(i));
				paralist.clear();
				if(sms_id==null|| "".equals(sms_id))
					//addMessageItem(sender,receiver,phone_num.toString(),msg,1);
				{
					addMessageItem(sender,receiver,phone_num.toString(),msg,username,wid,1);
				} else
				{
					//paralist.add(new Date());
					paralist.add(sms_id);
					dao.update(strsql.toString(),paralist);
				}
			}			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);					
		}
		return bflag;
		
	}
	/**
	 * 输出短信
	 * @param srv
	 * @param vo
	 * @return
	 */
	/*
	private boolean outPutMessage(CService srv,Object vo)throws GeneralException
	{
		boolean bflag=false;
		String sender=null;
		String receiver=null;
		String phone_num=null;
		String msg=null;
		String sms_id=null;
		LazyDynaBean dyvo=null;
		String username=null;
		String wid=null;
		try
		{
			if (vo instanceof LazyDynaBean) {
				dyvo= (LazyDynaBean) vo;
			}
			else
				return false;
			sender=(String)dyvo.get("sender");
			receiver=(String)dyvo.get("receiver");
			phone_num=(String)dyvo.get("phone_num");
			msg=(String)dyvo.get("msg");	
			username=(String)dyvo.get("username");
			wid=(String)dyvo.get("wid");
			if(username==null||username.length()<=0)
				username="";
			wid=wid!=null&&wid.length()>0?wid:"";
			if(msg==null||msg.equals(""))
				return false;
			if(sender==null||sender.equals("")||receiver==null||receiver.equals(""))
				return false;			
			sms_id=(String)dyvo.get("sms_id");
			ArrayList list=getMsgList(msg);
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList paralist=new ArrayList();
			StringBuffer strsql=new StringBuffer();//send_time=?
			strsql.append("update t_sys_smsbox set sended_count=sended_count+1,status=1 where sms_id=?");
			for(int i=0;i<list.size();i++)
			{
				IOutgoingMessage outmsg = new COutgoingMessage(phone_num, (String)list.get(i));
				outmsg.setMessageEncoding(IMessage.MESSAGE_ENCODING_UNICODE);
				outmsg.setStatusReport(true);
				outmsg.setValidityPeriod(8);
				srv.sendMessage(outmsg);
				paralist.clear();
				if(sms_id==null||sms_id.equals(""))
					//addMessageItem(sender,receiver,phone_num.toString(),msg,1);
					addMessageItem(sender,receiver,phone_num.toString(),msg,username,wid,1);
				else
				{
					//paralist.add(new Date());
					paralist.add(sms_id);
					dao.update(strsql.toString(),paralist);
				}
			}			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);					
		}
		return bflag;
	}
	*/
	
	
	/**
	 * 直接发短信
	 * @param sender 直接为姓名
	 * @param receiver 直接为姓名
	 * @param phone_num 手机号码
	 * @param msg
	 * @return
	 */
	public synchronized boolean sendMessage(String sender,String receiver,String phone_num,String msg)throws GeneralException
	{
		boolean bflag=false;
		if(msg==null|| "".equals(msg)) {
			return false;
		}
		if(sender==null|| "".equals(sender)||receiver==null|| "".equals(receiver)) {
			return false;
		}
//		SMSSender smsbo=SMSSender.getInstance(this.comm_port,this.pin);
		SMSSender smsbo=SMSSender.getInstance(this.modemPortList);
		bflag=smsbo.isAvailable();
		ArrayList list=getMsgList(msg);
		for(int i=0;i<list.size();i++)
		{
			smsbo.send(phone_num, msg);	
			addMessageItem(sender,receiver,phone_num.toString(),msg,1);
		}	

		return bflag;		
	}
	
	private boolean sendMessageByGateWay(String sender,String receiver,String phone_num,String msg)throws GeneralException
	{
		boolean bflag=false;
		if(msg==null|| "".equals(msg)) {
			return false;
		}
		if(sender==null|| "".equals(sender)||receiver==null|| "".equals(receiver)) {
			return false;
		}
		try
		{
			  ArrayList list=getMsgList(msg);			
			  for(int i=0;i<list.size();i++)
			  {
					MTEnSms mtsms=new MTEnSms(i,phone_num, (String)list.get(i),"",this.channelid); //参数分别为：序号，目标号码，短信内容，源号码后缀，channelid
					EnSmsMTMessage mtmessage=new EnSmsMTMessage();
					mtmessage.setUsername("shoukai_res");//平台分配的用户名。
					mtmessage.setPassword("shoukai_res");//平台分配的密码。
					mtmessage.append(mtsms);		
					byte[] data=EnSmsMessageTools.toByte(mtmessage);
					data=HttpTool.send(EnSmsMessageTools.toByte(mtmessage),this.down_url);
					EnSmsRespMessage resp=(EnSmsRespMessage)EnSmsMessageTools.toMessage(data);
					Status[] statues=resp.getStatuses();
					if(!(statues.length==1&&statues[0].getData()==0)){
						throw new GeneralException("send sms error!");
					}				
				  
					/**记录到发送成功的信箱中去*/
					addMessageItem(sender,receiver,phone_num.toString(), (String)list.get(i),1);
			  }
			  bflag=true;
		}
		catch (Exception e)
		{
			//发送不成功，记错误信箱中去
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);					
		}
		return bflag;		
	}
	
	
	
	/**
	 * 直接发短信
	 * @param sender for examples Usr00000001  应库前缀+人员编号
	 * @param receiver for examples Ret00000002  应库前缀+人员编号
	 * @param msg
	 * @return
	 */
	public boolean sendMessage(UserView userView,String receiver,String msg)throws GeneralException
	{
		SmsSendTread sendTrd = new SmsSendTread(this);
		sendTrd.setMessageConfig(userView, receiver, msg);
		new Thread(sendTrd).start();
		return true;
	}	
	
	
	private boolean sendMessage(UserView userView,String receiver,String msg,boolean flag) throws GeneralException{
		boolean bflag=false;
		if(msg==null|| "".equals(msg)) {
			return false;
		}
		if(userView==null||receiver==null|| "".equals(receiver)) {
			return false;
		}
		StringBuffer phone_num=new StringBuffer();
		try
		{
			/**不关心发送者的移动电话号码*/
		//	if(!this.bflag)
			String	sender=userView.getUserName();
			phone_num.setLength(0);
			if(!this.bflag)
			{
				receiver=getA0101String(receiver,phone_num);
			}
			else {
				phone_num.append(receiver);
			}
			if("0".equals(this.flag)) {
				bflag=sendMessage(sender,receiver,phone_num.toString(),msg);
			}
			if("1".equals(this.flag))
			{
				if("BKKJ".equals(this.service)) {
					bflag=sendMessageByGateWay(sender,receiver,phone_num.toString(),msg);
				}
				if("HJSJ".equals(this.service))
				{
					LazyDynaBean dyna=new LazyDynaBean();
					dyna.set("sender", sender);
					dyna.set("receiver", receiver);
					dyna.set("msg", msg);
					dyna.set("phone_num", phone_num.toString());
					bflag=outPutMessageByGateWayProxy(dyna);
				}
				if("CMPP2".equals(this.service)){//中国移动短信平台
				    this.userview=userView;
				    LazyDynaBean dyvo=new LazyDynaBean();
                    dyvo.set("sender",sender);
                    dyvo.set("receiver",receiver);
                    dyvo.set("phone_num",phone_num.toString());
                    dyvo.set("msg",msg);
                    bflag=outPutMessageByCMPP2(dyvo);//试用中国移动短信平台发送短信
				}
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;		
	}	
	
	
	
	
	/**
	 * 直接发短信
	 * @param sender for examples Usr00000001  应库前缀+人员编号
	 * @param receiver for examples Ret00000002  应库前缀+人员编号
	 * @param msg
	 * @return
	 */
	public boolean sendMessage(String sender,String receiver,String msg)throws GeneralException
	{
		boolean bflag=false;
		if(msg==null|| "".equals(msg)) {
			return false;
		}
		if(sender==null|| "".equals(sender)||receiver==null|| "".equals(receiver)) {
			return false;
		}
		StringBuffer phone_num=new StringBuffer();
		try
		{
			/**不关心发送者的移动电话号码*/
			if(!this.bflag) {
				sender= getA0101String(sender,phone_num);
			}
			phone_num.setLength(0);
			if(!this.bflag)
			{
				receiver=getA0101String(receiver,phone_num);
			}
			else {
				phone_num.append(receiver);
			}
			if("0".equals(this.flag)) {
				sendMessage(sender,receiver,phone_num.toString(),msg);
			}
			if("1".equals(this.flag))
			{
				if("BKKJ".equals(this.service)) {
					sendMessageByGateWay(sender,receiver,phone_num.toString(),msg);
				}
				if("HJSJ".equals(this.service))
				{
					LazyDynaBean dyna=new LazyDynaBean();
					dyna.set("sender", sender);
					dyna.set("receiver", receiver);
					dyna.set("msg", msg);
					dyna.set("phone_num", phone_num);
					outPutMessageByGateWayProxy(dyna);
				}
				if("CMPP2".equals(this.service)){//中国移动短信平台
                    LazyDynaBean dyvo=new LazyDynaBean();
                    dyvo.set("sender",sender);
                    dyvo.set("receiver",receiver);
                    dyvo.set("phone_num",phone_num.toString());
                    dyvo.set("msg",msg);
                    outPutMessageByCMPP2(dyvo);//试用中国移动短信平台发送短信
                }
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;		
	}	
	
	/**
	 * 直接发短信
	 * @param sender for examples 123456
	 * @param receiver for examples 123456
	 * @param msg
	 * @return
	 */
	public boolean sendMessage2(String sender,String receiver,String receiverPhone,String msg)throws GeneralException
	{
		boolean bflag=false;
		if(msg==null|| "".equals(msg)) {
			return false;
		}
		if(sender==null|| "".equals(sender)||receiver==null|| "".equals(receiver)) {
			return false;
		}
		try
		{
			
			
			if("0".equals(this.flag)) {
				sendMessage(sender,receiver,receiverPhone.toString(),msg);
			}
			if("1".equals(this.flag))
			{
				if("BKKJ".equals(this.service)) {
					sendMessageByGateWay(sender,receiver,receiverPhone.toString(),msg);
				}
				if("HJSJ".equals(this.service))
				{
					LazyDynaBean dyna=new LazyDynaBean();
					dyna.set("sender", sender);
					dyna.set("receiver", receiver);
					dyna.set("msg", msg);
					dyna.set("phone_num", receiverPhone);
					outPutMessageByGateWayProxy(dyna);
				}
				if("CMPP2".equals(this.service))
				{
					LazyDynaBean dyna=new LazyDynaBean();
					dyna.set("sender", sender);
					dyna.set("receiver", receiver);
					dyna.set("msg", msg);
					dyna.set("phone_num", receiverPhone);
					outPutMessageByCMPP2(dyna);
				}
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;		
	}

	/**
	 * 收短信
	 * @return
	 */
	public boolean acceptMessage()
	{
		boolean bflag=true;
		/**短信猫*/
		if("0".equalsIgnoreCase(flag)) {
			acceptMessageByModem();
		}
		/**短信网关*/
		if("1".equalsIgnoreCase(flag)) {
			acceptMessageByGateWay();
		}
		
		return bflag;
	}
	
	/**
	 * 接受来自于短信猫
	 */
	private void acceptMessageByModem() {
//		SMSSender smsbo=SMSSender.getInstance(this.comm_port,this.pin);
		SMSSender smsbo=SMSSender.getInstance(this.modemPortList);
		ArrayList msglist=smsbo.read();
		for (int i=0; i<msglist.size(); i++) {
			try {
				ContentDAO dao=new ContentDAO(this.conn);
				InboundMessage msg=(InboundMessage) msglist.get(i);
				String sendNumber = msg.getOriginator().substring(2,msg.getOriginator().length());
				String sender=getA0101ByPhoneNume(sendNumber);//getA0101String(sender, new StringBuffer(sendNumber));
				sender=sender==null||sender.length()<1?sendNumber:sender;
				String msgtxt=msg.getText();
				int count = msgtxt.length()/100 + 1;
				StringBuffer sqlstr=new StringBuffer();
				List sqlList=new ArrayList();
				
				for (int j = 0; j < count; j++) {
					sqlstr.append("insert into t_sys_smsbox (sms_id,sender,mobile_no,status,msg,send_time) values(");
					sqlstr.append("'"+CreateSequence.getUUID()+"',");
					sqlstr.append("'"+sender+"',");
					sqlstr.append("'"+sendNumber+"','2',");
					if (j == count - 1) {
						sqlstr.append("'"+msgtxt.substring(j*100, msgtxt.length())+"',");
					} else {
						sqlstr.append("'"+msgtxt.substring(j*100, (j+1)*100)+"',");
					}
					sqlstr.append(""+Sql_switcher.dateValue(DateUtils.format(msg.getDate(), "yyyy-MM-dd HH:mm:ss")) +")");
					sqlList.add(sqlstr.toString());
				}
				
				this.invokYWInterface(sendNumber, "", msgtxt, msg.getDate());
				dao.batchUpdate(sqlList);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 根据号码获得人名
	 * @param phoneNum
	 * @return
	 */
	private String getA0101ByPhoneNume(String phoneNum) {
		String file = getMobileNumber();
		String name = "";
		StringBuffer sql = new StringBuffer();
		sql.append("select pre from dbname");
		ContentDAO dao = new ContentDAO(this.conn);
		
		RowSet rs = null;
		RowSet rs2 = null;
		if (file != null && file.length() > 0) {
			try {
				rs = dao.search(sql.toString());
				sql.delete(0, sql.length());
				while (rs.next()) {
					sql.append("select a0101");
					sql.append(" from ");
					sql.append(rs.getString("pre"));
					sql.append("a01 where ");
					sql.append(getMobileNumber());
					sql.append("='");
					sql.append(phoneNum);
					sql.append("' union all ");
				}
				
				rs2 = dao.search(sql.substring(0,sql.length() - 10));
				if (rs2.next()) {
					name = rs2.getString("a0101");
					if (name == null) {
						name = "";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (rs2 != null) {
						rs2.close();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return name;
	}
	/**
	 * 通过网关接收短信
	 */
	private void acceptMessageByGateWay()
	{
		if ("JGJK".equalsIgnoreCase(this.service)) {
			acceptMessageByJGJKGateWay();
		}
	}
	
	/**
	 * 金格接口接收短信
	 */
	private void acceptMessageByJGJKGateWay() {
		iSMSClient2000 Objsms = new iSMSClient2000();
	   	if (Objsms.OpenSMS(this.username,Integer.parseInt(this.password))){
	   		while (!("0".equalsIgnoreCase(Objsms.ReadSMS()))){
	   			String mobile = "";
	   			String content = "";
	   			String dateTime = "";
	   			try {
		   			mobile = new String(Objsms.Mobile.getBytes("8859_1"));
		   			content = new String(Objsms.Content.getBytes("8859_1"));
		   			dateTime = new String(Objsms.DateTime.getBytes("8859_1"));
		   			addAcceptMessage(mobile, "", content, 2, dateTime);
	   			} catch (Exception e) {
	   				e.printStackTrace();
	   			}
	   			
	   			try {
	   				invokYWInterface(mobile, "", content, dateTime);
	   			} catch (Exception e) {
	   				e.printStackTrace();
	   			}
	          	

	   		}
	   		
	   		Objsms .CloseSMS();
	   	}

	}
	/**
	 * 获得接收到的信息的发送人号码 和 发送内容 只接受指定长度的短信息
	 * 以 LazyDynaBean存储 num 发送人手机号，content发送内容
	 * 返回list
	 * dml 2011-05-20 
	 * 招聘管理中发送面试通知后 接收面试者回复信息用
	 * */
	public ArrayList acceptMessageNumAndCon(int length){
		ArrayList messagelist=new ArrayList();
		SMSSender smsbo=SMSSender.getInstance(this.modemPortList);
		ArrayList msglist=smsbo.read();
		for (int i=0; i<msglist.size(); i++) {
			try {
				InboundMessage msg=(InboundMessage) msglist.get(i);
				String sendNumber = msg.getOriginator().substring(2,msg.getOriginator().length());
				String sender=sendNumber;//getA0101String(sender, new StringBuffer(sendNumber));
				sender=sender==null||sender.length()<1?sendNumber:sender;
				String msgtxt=msg.getText();
				if(msgtxt.length()>length){
					continue;
				}else{
					LazyDynaBean bean=new LazyDynaBean();
					bean.set("num", sendNumber);
					bean.set("content", msgtxt);
					messagelist.add(bean);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return messagelist;
	}
	
	/**
	 * 调用短信业务接口
	 * @param sendNumber
	 * @param acceptor
	 * @param msgtxt
	 * @param date
	 * @return
	 * @throws GeneralException
	 */
	public boolean invokYWInterface(String sendNumber, String acceptor, String msgtxt, Date date) throws GeneralException {
		boolean isSave = true; 
		String classes = "";
		String desc = "";
		try {
			SmsYWInterfaceBo ywBo = new SmsYWInterfaceBo(this.conn);
			ArrayList ywList = ywBo.getList();
			LazyDynaBean bean = new LazyDynaBean();
			for (int i = 0; i< ywList.size(); i++) {
				LazyDynaBean ywBean = (LazyDynaBean) ywList.get(i);
				String code = (String) ywBean.get("code");
				classes = (String) ywBean.get("classes");
				desc = (String) ywBean.get("desc");
				String status = (String) ywBean.get("status");
				
				if (msgtxt.startsWith(code + "﹟") || msgtxt.startsWith(code + "#") || msgtxt.startsWith(code + "#")) {
					bean.set("sender", sendNumber);
					bean.set("acceptor", acceptor);
					bean.set("text", msgtxt);
					bean.set("datetime", DateUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
					// 该条短信不再保存到信箱中
					isSave = false;
					if ("1".equals(status)) {
						// 调用业务接口
						IAcceptSMS accept = (IAcceptSMS) Class.forName(classes).newInstance();
						accept.acceptSMS(bean);
					}
					
				}
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(new GeneralException(
			"短信业务接口"+desc+"("+classes+")调用失败"));
		}
		
		return isSave;
	}
	
	public ArrayList getMsgList(String msg, String head)
	{
		ArrayList list=new ArrayList();
		int len=0;
		if(msg!=null)
		{
			//msg=msg.replaceAll(" ", "");
			//msg=msg.replaceAll("　", "");
		}
		len=msg.getBytes().length;
		if(/*msg.length()*/len<=(140 - head.getBytes().length))
		{
			list.add(msg);
			return list;
		}
		
		// 最大将短信分成99条，
		int tt = 140 - head.getBytes().length - ("(99/99)".getBytes().length);
		
		
		while(true)
		{
			String temp=PubFunc.splitString(msg,tt);
			list.add(temp);				
			byte[] bytes=msg.getBytes();
			len=bytes.length;
			
			/**实际的长度*/
			int rlen=0;
			int j=0;
			for(int i=0;i<tt;i++)
			{
				if(bytes[i]<0)
				{
					j++;
				}
			}
			if((j%2)==1) {
				rlen=tt-1;
			} else {
				rlen=tt;
			}
			
			
			
			byte[] sub=new byte[len-rlen];
			System.arraycopy(bytes,rlen,sub,0,len-rlen);
			msg=new String(sub);
			if(len-tt<=tt)
			{
				list.add(msg);	
				break;
			}
		}
		return list;
	}
	
	/**
	 * 调用短信业务接口
	 * @param sendNumber
	 * @param acceptor
	 * @param msgtxt
	 * @param date
	 * @return
	 * @throws GeneralException
	 */
	public boolean invokYWInterface(String sendNumber, String acceptor, String msgtxt, String date) throws GeneralException {
		Date dat = DateUtils.getDate(date, "yyyy-MM-dd HH:mm:dd");
		return invokYWInterface(sendNumber, acceptor, msgtxt, dat);
	}
	
	public boolean isBflag() {
		return bflag;
	}


	public void setBflag(boolean bflag) {
		this.bflag = bflag;
	}	
	
}
