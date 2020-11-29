package com.hjsj.hrms.transaction.sys.sms;


import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommaMailTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			RecordVo sms_vo=ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
	        if(sms_vo==null)
	        	throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("sys.smsparam.nodifine")));
	        String param=sms_vo.getString("str_value");
	        if(param==null|| "".equals(param))
	        	throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("sys.smsparam.nodifine")));
			SmsBo smsbo=new SmsBo(this.getFrameconn());
			smsbo.acceptMessage();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
//		Sms_Parameter sparam=new Sms_Parameter(this.getFrameconn());			
//	    LazyDynaBean lvo=sparam.searchCommPort("COM1"); 
//	    if(lvo!=null)
//	    {
//	    	String port=(String) lvo.get("name");
//	    	String pin=(String) lvo.get("pin");
//	    
//			SMSReader reader=new SMSReader(port,pin);
//			List msgList=reader.read();
//			for (int i=0; i<msgList.size(); i++) {
//				try {
//					ContentDAO dao=new ContentDAO(this.getFrameconn());
//					InboundMessage msg=(InboundMessage) msgList.get(i);
//					String sendNumber = msg.getOriginator().substring(2,msg.getOriginator().length());
//					String sender=getUserName(sendNumber,dao);
//					sender=sender==null||sender.length()<1?sendNumber:sender;
//					List msgs=getMSGArray(msg.getText());
//					StringBuffer sqlstr=new StringBuffer();
//					List sqlList=new ArrayList();
//					for (int j = 0; j < msgs.size(); j++) {
//						sqlstr.append("insert into t_sys_smsbox (sms_id,sender,mobile_no,status,msg,send_time) values(");
//						sqlstr.append("'"+CreateSequence.getUUID()+"',");
//						sqlstr.append("'"+sender+"',");
//						sqlstr.append("'"+sendNumber+"','2',");
//						sqlstr.append("'"+msgs.get(j)+"',");
//						sqlstr.append("'"+msg.getDate()+"')");
//						sqlList.add(sqlstr.toString());
//					}
//					dao.batchUpdate(sqlList);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			reader.close();
//	    }
	}
	
	private List getMSGArray(String text) {
		List list=new ArrayList();
		if(text.length()>200){
			for(int i=0;i<=text.length(); i=i+200){
				String item=text.substring(i+200);
				list.add(text.substring(i, i+200));
				if(item.length()<200){
					list.add(item);
					break;
				}
			}
		}else{
			list.add(text);
		}
		return list;
	}

	private String getUserName(String sendNumber,ContentDAO dao) throws GeneralException, SQLException {
		String name="";
		String field_name=getMobileNumber();
		if(field_name==null|| "".equals(field_name))
			throw new GeneralException(ResourceFactory.getProperty("error.sms.notdefine"));
		else{
			String strsql="select a0101 from usra01 where "+field_name+"='"+sendNumber+"'";
			this.frowset=dao.search(strsql);
			while(this.frowset.next()){
				name=this.frowset.getString("a0101");
			}
		}
		return name;
	}

	/**
	 * 求移动电话号码
	 * @return
	 */
	private String getMobileNumber()
	{
        RecordVo vo=ConstantParamter.getConstantVo("SS_MOBILE_PHONE");
        if(vo==null)
        	return "";
        String field_name=vo.getString("str_value");
        if(field_name==null|| "".equals(field_name)|| "#".equals(field_name))
        	return "";
        FieldItem item=DataDictionary.getFieldItem(field_name);
        if(item==null)
        	return "";
        /**分析是否构库*/
        if("0".equals(item.getUseflag()))
        	return "";
        return field_name; 
	}

}
