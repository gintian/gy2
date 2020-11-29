/**
 * 
 */
package com.hjsj.hrms.businessobject.sys;

import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Logger;
import org.smslib.*;
import org.smslib.InboundMessage.MessageClasses;
import org.smslib.Message.MessageEncodings;
import org.smslib.Message.MessageTypes;
import org.smslib.modem.SerialModemGateway;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;




/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 9, 2009:1:32:53 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SMSSender {

	    private Service service;   
	    private String port, manufacturer, model;   
	    private int baudrate;   
	    private Logger logger = Logger.getLogger(SMSSender.class.getName());   
	    private Timer timer;   
	    /**手机卡密码*/
	    private String pin;
	    private static SMSSender instance;   
	    private static boolean available; 
	  
	    private SMSSender(String com,String pin) {   
	        port = com; //获取参数配置，端口（COM1）   
	        manufacturer ="Wavecom";// Parameters.getSmsModemManufacturer(); //   
	        model ="G900"; //Parameters.getSmsModemModel(); //
	        this.pin=pin;
	        try {   
	            baudrate = 9600;// 比特率(9600)   
	        } catch (Exception e) {   
	            baudrate = 9600;   
	        }   
	        timer = new Timer();   
	        available = open();   
	    }
	    
	    private SMSSender(ArrayList list) {   
//	        port = com; //获取参数配置，端口（COM1）   
	        manufacturer = "Wavecom";// Parameters.getSmsModemManufacturer(); //   
	        model = "G900"; //Parameters.getSmsModemModel(); //
	        //pin=pin;
	        try {   
	            baudrate = 9600;// 比特率(9600)   
	        } catch (Exception e) {   
	            baudrate = 9600;   
	        }   
	        timer = new Timer();   
	        available = open(list);   
	    }
	  
	    public static SMSSender getInstance(String com,String pin) {   
	        if (instance == null) {   
	            instance = new SMSSender(com,pin);   
	        } 
	        return instance;   
	    } 
	  
	    public static SMSSender getInstance(ArrayList list) {   
	        if (instance == null) {   
	            instance = new SMSSender(list);   
	        } 
	        return instance;   
	    }
	    /**  
	     * 判断短信猫是否可用  
	     *  
	     * @return  
	     */  
	    public boolean isAvailable() {   
	        return available;   
	    }   
	  
	    /**  
	     * 初始化，启动短信猫  
	     *  
	     * @return  
	     */  
	    private boolean open() {   
	        try {  	    	
	        service = new Service();  

	        SerialModemGateway gateway = new SerialModemGateway("SMS", port, baudrate, manufacturer, model);
	        for (int i = 4; i <= 4; i++) {
	        	SerialModemGateway gateway2 = new SerialModemGateway("SMS", "COM"+i, baudrate, manufacturer, model);
	        	gateway2.setInbound(true);   
		        gateway2.setOutbound(true);  
				gateway2.setSimPin(this.pin);
				service.addGateway(gateway2);
	        }
	        gateway.setInbound(true);   
	        gateway.setOutbound(true);  
			gateway.setSimPin(this.pin);
	        service.addGateway(gateway);   
	        service.setOutboundNotification(new IOutboundMessageNotification() {   
	            @Override
                public void process(String gatewayId, OutboundMessage msg) {
	                logger.debug("Outbound handler called from Gateway:" + gatewayId);   
	                logger.debug(msg);   
	            }   
	        }); 
	        
	        service.setInboundNotification(new IInboundMessageNotification() {
	        	@Override
                public void process(String gatewayId, MessageTypes msgType,
                                    InboundMessage msg){
	        		Connection conn = null;
	    			try {
	    				conn = AdminDb.getConnection();
	    				// 保存短信
	    				save(msg, conn);
	    				// 删除短信
	    				service.findGateway(gatewayId).deleteMessage(msg); 

	    			} catch (Exception e) {
	    				e.printStackTrace();
	    			} finally {
	    				if (conn != null) {
	    					try {
								conn.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
	    				}
	    			}
	        	}
	        });
	            service.startService();   
	        } catch (Exception e) {   
	            logger.error("短信猫启动失败！" + e.getMessage());   
	            close();   
	            return false;   
	        }   
	        return true;   
	    } 
	    
	    /**  
	     * 初始化，启动短信猫  
	     *  
	     * @return  
	     */  
	    private boolean open(ArrayList list) {   
	        try {  	    	
	        service = new Service();  

	        for (int i = 0; i < list.size(); i++) {
	        	LazyDynaBean bean = (LazyDynaBean) list.get(i);
	        	
	        	String company = (String)bean.get("company");
	        	if(company == null || company.length() == 0) {
					company = manufacturer;
				}
	        	
	        	String modeltype = (String)bean.get("modeltype");
	        	if(modeltype == null || modeltype.length() == 0) {
					modeltype = model;
				}
	        	
	        	String bit = (String)bean.get("bit");
	        	int iBit;
	        	if(bit == null || bit.length() == 0) {
					iBit = baudrate;
				} else {
					iBit = Integer.valueOf(bit).intValue();
				}
	        	
        		String com = (String) bean.get("com");
        		String pin = (String) bean.get("pin");
	        	SerialModemGateway gateway = new SerialModemGateway("SMS"+i, com, iBit, company, modeltype);
	        	gateway.setInbound(true);   
		        gateway.setOutbound(true);  
				gateway.setSimPin(pin);
				service.addGateway(gateway);
	        }
  
	        service.setOutboundNotification(new IOutboundMessageNotification() {   
	            @Override
                public void process(String gatewayId, OutboundMessage msg) {
	                logger.debug("Outbound handler called from Gateway:" + gatewayId);   
	                logger.debug(msg);   
	            }   
	        }); 
	        
	        service.setInboundNotification(new IInboundMessageNotification() {
	        	@Override
                public void process(String gatewayId, MessageTypes msgType,
                                    InboundMessage msg){
	        		Connection conn = null;
	    			try {
	    				conn = AdminDb.getConnection();
	    				// 保存短信
	    				save(msg, conn);
	    				// 删除短信
	    				service.findGateway(gatewayId).deleteMessage(msg); 

	    			} catch (Exception e) {
	    				e.printStackTrace();
	    			} finally {
	    				if (conn != null) {
	    					try {
								conn.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
	    				}
	    			}
	        	}
	        });
	            service.startService();   
	        } catch (Exception e) {   
	            logger.error("短信猫启动失败！" + e.getMessage());   
	            close();   
	            return false;   
	        }   
	        return true;   
	    }
	    
	    /**
	     * 保存短信
	     * @param msg InboundMessage 短信
	     * @param conn Connection 数据库连接
	     * @return Boolean 是否保存成功
	     */
	    public boolean save(InboundMessage msg, Connection conn) {  
	    	boolean flag = true;
///	    	ContentDAO dao = new ContentDAO(conn);
			try {
				String sendNumber = msg.getOriginator().substring(2,msg.getOriginator().length());
				String sender=sendNumber;//getA0101String(sender, new StringBuffer(sendNumber));
				sender=sender==null||sender.length()<1?sendNumber:sender;
				String msgtxt=msg.getText();
//				ArrayList list=getMsgList(msgtxt);
//				StringBuffer sqlstr=new StringBuffer();
//				List sqlList=new ArrayList();
//				for (int i = 0; i < list.size(); i++) {
//					String ms = (String) list.get(i);
//					sqlstr.append("insert into t_sys_smsbox (sms_id,sender,mobile_no,status,msg,send_time,a0100,nbase) values(");
//					sqlstr.append("'"+CreateSequence.getUUID()+"',");
//					sqlstr.append("'"+sender+"',");
//					sqlstr.append("'"+sendNumber+"','2',");
//					sqlstr.append("'"+ms+"',");
//
//					sqlstr.append(""+Sql_switcher.dateValue(DateUtils.format(msg.getDate(), "yyyy-MM-dd HH:mm:ss")) +")");
//					sqlList.add(sqlstr.toString());
//				}
//				dao.batchUpdate(sqlList);
//				String classes = "";
//				String desc = "";
				SmsBo bo = new SmsBo(conn);
				bo.addAcceptMessage(sendNumber,"",msgtxt,2,DateUtils.format(msg.getDate(), "yyyy-MM-dd HH:mm:ss"));
				bo.invokYWInterface(sendNumber, "", msgtxt, msg.getDate());
								
				
				
			} catch (Exception e) {
				flag = false;
				e.printStackTrace();
			} 		
	        return flag;
	    }
	    
//	    private ArrayList getMsgList(String msg)
//		{
//			ArrayList list=new ArrayList();
//			int len=0;
//			if(msg!=null)
//			{
//				msg=msg.replaceAll(" ", "");
//				msg=msg.replaceAll("　", "");
//			}
//			len=msg.getBytes().length;
//			if(/*msg.length()*/len<=140)
//			{
//				list.add(msg);
//				return list;
//			}
//
//			while(true)
//			{
//				String temp=PubFunc.splitString(msg,140);
//				list.add(temp);				
//				byte[] bytes=msg.getBytes();
//				len=bytes.length;
//				byte[] sub=new byte[len-140];
//				System.arraycopy(bytes,140,sub,0,len-140);
//				msg=new String(sub);
//				if(len-140<=140)
//				{
//					list.add(msg);	
//					break;
//				}
//			}
//			return list;
//		}
	    /**  
	     * 发送短信  
	     *  
	     * @param phone  
	     * @param message  
	     * @return  
	     */  
	    public void send(String phone, String message) {   

	        logger.debug("请求发送短信，手机：" + phone + "，内容：" + message);   
	  
	        if (!available) { // 状态检测，如果不可用，直接跳过   
	            logger.debug("短信猫状态异常，跳过短信处理！");   
	            return;   
	        }   
	  
	        timer.schedule(new SendTask(phone, message), 0);   

	    }   
	  
		/**
		 * 读短信
		 * @return
		 */
	    public ArrayList read() {   
	    	if (!available) { // 状态检测，如果不可用，直接跳过   
	            logger.debug("短信猫状态异常，跳过短信处理！");   
	            return new ArrayList();   
	        }     
	        ArrayList msgList = new ArrayList();
			try {
				this.service.readMessages(msgList, MessageClasses.UNREAD);
				for (int i = 0; i < msgList.size(); i++) {
					InboundMessage msg = (InboundMessage) msgList.get(i);				
		            this.service.deleteMessage(msg); //删除SIM卡中的信息
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
	        return msgList;
	    } 
	    
	    /**  
	     * 关闭短信猫  
	     */  
	    public void close() {   
	        try {   
	            timer.cancel();   
	            service.stopService();   
	            logger.debug("短信猫已关闭！");   
	        } catch (Exception e) {   
	            logger.error("关闭短信猫失败！", e);   
	        }   
	    }   
	  
	    private class SendTask extends TimerTask {   
	  
	        private String phone, message;   
	  
	        public SendTask(String phone, String message) {   
	            this.phone = phone;   
	            this.message = message;   
	        }   
	  
	        @Override
            public void run() {
	            // 初始化短信   
	            OutboundMessage outMsg = new OutboundMessage(phone, message);   
	            outMsg.setEncoding(MessageEncodings.ENCUCS2);   
	            try {   
	                service.sendMessage(outMsg);   
	                logger.debug("发送短信：\n" + outMsg);   
	            } catch (Exception e) {   
	                logger.error("短信发送失败，", e);   
	            }   
	        }   
	    }   


}
