/**
 * 
 */
package com.hjsj.hrms.transaction.sys.sms;

import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jul 29, 20064:02:41 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SendSmsTrans extends IBusiness {

	private String receiverq;
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
        if(field_name==null|| "".equals(field_name))
        	return "";
        FieldItem item=DataDictionary.getFieldItem(field_name);
        if(item==null)
        	return "";
        /**分析是否构库*/
        if("0".equals(item.getUseflag()))
        	return "";
        return field_name; 
	}
	/**
	 * 批量发短信，按选择的单位，部门或人员
	 * @param strsql
	 * @throws GeneralException
	 */
	private void batch_sendsms(String strsql,String field_name,String msg)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(strsql);
			SmsBo smsbo=new SmsBo(this.getFrameconn(),this.getUserView());
			while(this.frowset.next())
			{
				String receiver=this.getFrowset().getString("a0101");
				String phone=this.getFrowset().getString(field_name);
				if(phone==null|| "".equals(phone))
					continue;
				smsbo.sendMessage(userView.getUserFullName(),receiver,phone,msg);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
	}
	
	private void get_BatchList(String strsql,String field_name,String msg,ArrayList destlist)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(strsql);
			while(this.frowset.next())
			{
				String receiver=this.getFrowset().getString("a0101");
				if (receiver == null) {
					receiver = "";
				}
				String phone=this.getFrowset().getString(field_name);
				if(phone==null|| "".equals(phone))
					continue;
				LazyDynaBean dyvo=new LazyDynaBean();
				dyvo.set("sender",userView.getUserFullName());
				dyvo.set("receiver",receiver);
				dyvo.set("phone_num",phone);
				dyvo.set("msg",msg);
				destlist.add(dyvo);				
				//smsbo.sendMessage(userView.getUserFullName(),receiver,phone,msg);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
	}
	
	private ArrayList get_BatchList(String msg,ArrayList list)throws GeneralException
	{
		try
		{
				ArrayList destlist=new ArrayList();
				for(int i=0;i<list.size();i++)
				{
					String phone=(String)list.get(i);
					LazyDynaBean dyvo=new LazyDynaBean();
					dyvo.set("sender",userView.getUserFullName());
					dyvo.set("receiver",phone);
					dyvo.set("phone_num",phone);
					dyvo.set("msg",msg);
					destlist.add(dyvo);
				}
				return destlist;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
	}	
	/**
	 * 取得目标对象地址及内容
	 * @param list
	 * @param msg
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList getReceiverSmsList(ArrayList list,String msg)throws GeneralException
	{
		String codevalue=null;
		ArrayList destlist=new ArrayList();
		StringBuffer strsql=new StringBuffer();
		String field_name=getMobileNumber();
		if(field_name==null|| "".equals(field_name))
			throw new GeneralException(ResourceFactory.getProperty("error.sms.notdefine"));
		ArrayList dblist=userView.getPrivDbList();
		if(dblist.size()==0)
			throw new GeneralException(ResourceFactory.getProperty("errors.static.notdbname"));
		String strWhere=null;
		String dbpre=null;
		try
		{
			ArrayList fieldlist=new ArrayList();
			for(int i=0;i<list.size();i++)
			{
				String obj=(String)list.get(i);
				String pre=obj.substring(0,2);
				if("UN".equalsIgnoreCase(pre))
				{
					codevalue=obj.substring(2);
					for(int j=0;j<dblist.size();j++)
					{
						dbpre=(String)dblist.get(j);
						strsql.append("select a0101 ,");
						strsql.append(field_name);
						strsql.append(" ");
						strWhere=userView.getPrivSQLExpression("1"+"|"+"B0110="+codevalue+"*`",dbpre,false,false,fieldlist);
						//strsql.append(strWhere);
						strsql.append(" from ");
						strsql.append(dbpre);
						strsql.append("A01 where b0110 like '");
						strsql.append(codevalue);
						strsql.append("%'");
						
						strsql.append(" union ");
					}
					strsql.setLength(strsql.length()-7);
					//batch_sendsms(strsql.toString(),field_name,msg);
					get_BatchList(strsql.toString(),field_name,msg,destlist);
				}
				else if("UM".equalsIgnoreCase(pre))
				{
					codevalue=obj.substring(2);					
					for(int j=0;j<dblist.size();j++)
					{
						dbpre=(String)dblist.get(j);
						strsql.append("select a0101 ,");
						strsql.append(field_name);
						strsql.append(" ");
						strWhere=userView.getPrivSQLExpression("1"+"|"+"E0122="+codevalue+"*`",dbpre,false,false,fieldlist);
						//strsql.append(strWhere);
						strsql.append(" from ");
						strsql.append(dbpre);
						strsql.append("A01 where E0122 like '");
						strsql.append(codevalue);
						strsql.append("%'");
						
						strsql.append(" union ");
					}
					strsql.setLength(strsql.length()-7);		
					//batch_sendsms(strsql.toString(),field_name,msg);
					get_BatchList(strsql.toString(),field_name,msg,destlist);
					
				}
				else if("@K".equalsIgnoreCase(pre))
				{
					codevalue=obj.substring(2);					
					for(int j=0;j<dblist.size();j++)
					{
						dbpre=(String)dblist.get(j);
						strsql.append("select a0101 ,");
						strsql.append(field_name);
						strsql.append(" ");
						strWhere=userView.getPrivSQLExpression("1"+"|"+"E01A1="+codevalue+"*`",dbpre,false,false,fieldlist);
						//strsql.append(strWhere);
						strsql.append(" from ");
						strsql.append(dbpre);
						strsql.append("A01 where E01A1 like '");
						strsql.append(codevalue);
						strsql.append("%'");
						
						strsql.append(" union ");
					}
					strsql.setLength(strsql.length()-7);	
					//batch_sendsms(strsql.toString(),field_name,msg);
					get_BatchList(strsql.toString(),field_name,msg,destlist);					
				}else if ("HQ".equalsIgnoreCase(pre)){
					if (this.receiverq != null && this.receiverq.length() > 0) {
						String q[] = this.receiverq.split(";");
						String expr = q[0];
						String factor = q[1];
						String chwhere = "";
						boolean likeflag = false;
						if("1".equals(q[2])){
							likeflag = true;
						}
						if ("all".equalsIgnoreCase(q[3])) {
							for(int j = 0;j < dblist.size(); j++) {
								String userbase = (String) dblist.get(j);							
				        		FactorList factorslist=new FactorList(expr,PubFunc.getStr(factor),userbase,false,likeflag,true,1,userView.getUserId());
				        		fieldlist=factorslist.getFieldList();
				        		chwhere=factorslist.getSqlExpression();
				        		strsql.append("select "+userbase+"a01.a0101,");
				        		strsql.append(userbase+"a01.");
								strsql.append(field_name);
								strsql.append(" ");
								strsql.append(chwhere);
								
								strsql.append(" union ");
							}
							strsql.setLength(strsql.length()-7);
						} else {							
			        		FactorList factorslist=new FactorList(expr,PubFunc.getStr(factor),q[3],false,likeflag,true,1,userView.getUserId());
			        		fieldlist=factorslist.getFieldList();
			        		chwhere=factorslist.getSqlExpression();
			        		strsql.append("select "+q[3]+"a01.a0101,");
			        		strsql.append(q[3]+"a01.");
							strsql.append(field_name);
							strsql.append(" ");
							strsql.append(chwhere);
						}
						get_BatchList(strsql.toString(),field_name,msg,destlist);
					}
				}
				else
				{
					dbpre=obj.substring(0,3);
					codevalue=obj.substring(3);
					RecordVo vo=new RecordVo(dbpre+"a01");
					ContentDAO dao=new ContentDAO(this.getFrameconn());
					vo.setString("a0100",codevalue);
					vo=dao.findByPrimaryKey(vo);
					LazyDynaBean dyvo=new LazyDynaBean();
					dyvo.set("sender",userView.getUserFullName());
					dyvo.set("receiver",vo.getString("a0101"));
					dyvo.set("phone_num",vo.getString(field_name.toLowerCase()));
					dyvo.set("msg",msg);
					destlist.add(dyvo);
					//SmsBo smsbo=new SmsBo(this.getFrameconn());
					//smsbo.sendMessage(userView.getUserFullName(),vo.getString("a0101"),vo.getString(field_name.toLowerCase()),msg);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return destlist;
	}
	/**
	 * 发送短信
	 * @param list
	 * @param msg
	 */
	private void send_sms(ArrayList list,String msg)throws GeneralException
	{
		String codevalue=null;
		StringBuffer strsql=new StringBuffer();
		String field_name=getMobileNumber();
		if(field_name==null|| "".equals(field_name))
			throw new GeneralException(ResourceFactory.getProperty("error.sms.notdefine"));
		ArrayList dblist=userView.getPrivDbList();
		if(dblist.size()==0)
			throw new GeneralException(ResourceFactory.getProperty("errors.static.notdbname"));
		String strWhere=null;
		String dbpre=null;
		try
		{
			ArrayList fieldlist=new ArrayList();
			for(int i=0;i<list.size();i++)
			{
				String obj=(String)list.get(i);
				String pre=obj.substring(0,2);
				if("UN".equalsIgnoreCase(pre))
				{
					codevalue=obj.substring(2);
					for(int j=0;j<dblist.size();j++)
					{
						dbpre=(String)dblist.get(j);
						strsql.append("select a0101 ,");
						strsql.append(field_name);
						strsql.append(" ");
						strWhere=userView.getPrivSQLExpression("1"+"|"+"B0110="+codevalue+"*`",dbpre,false,false,fieldlist);
						//strsql.append(strWhere);						
						strsql.append(" from ");
						strsql.append(dbpre);
						strsql.append("A01 where B0110 like '");
						strsql.append(codevalue);
						strsql.append("%'");
						
						strsql.append(" union ");
					}
					strsql.setLength(strsql.length()-7);
					batch_sendsms(strsql.toString(),field_name,msg);
				}
				else if("UM".equalsIgnoreCase(pre))
				{
					codevalue=obj.substring(2);					
					for(int j=0;j<dblist.size();j++)
					{
						dbpre=(String)dblist.get(j);
						strsql.append("select a0101 ,");
						strsql.append(field_name);
						strsql.append(" ");
						strWhere=userView.getPrivSQLExpression("1"+"|"+"E0122="+codevalue+"*`",dbpre,false,false,fieldlist);
						//strsql.append(strWhere);
						strsql.append(" from ");
						strsql.append(dbpre);
						strsql.append("A01 where E0122 like '");
						strsql.append(codevalue);
						strsql.append("%'");
						
						strsql.append(" union ");
					}
					strsql.setLength(strsql.length()-7);		
					batch_sendsms(strsql.toString(),field_name,msg);
				}
				else if("@K".equalsIgnoreCase(pre))
				{
					codevalue=obj.substring(2);					
					for(int j=0;j<dblist.size();j++)
					{
						dbpre=(String)dblist.get(j);
						strsql.append("select a0101 ,");
						strsql.append(field_name);
						strsql.append(" ");
						strWhere=userView.getPrivSQLExpression("1"+"|"+"E01A1="+codevalue+"*`",dbpre,false,false,fieldlist);
						//strsql.append(strWhere);
						strsql.append(" from ");
						strsql.append(dbpre);
						strsql.append("A01 where E01A1 like '");
						strsql.append(codevalue);
						strsql.append("%'");
						
						strsql.append(" union ");
					}
					strsql.setLength(strsql.length()-7);	
					batch_sendsms(strsql.toString(),field_name,msg);
				}
				else
				{
					dbpre=obj.substring(0,3);
					codevalue=obj.substring(3);
					RecordVo vo=new RecordVo(dbpre+"a01");
					ContentDAO dao=new ContentDAO(this.getFrameconn());
					vo.setString("a0100",codevalue);
					vo=dao.findByPrimaryKey(vo);
					SmsBo smsbo=new SmsBo(this.getFrameconn(),this.getUserView());
					smsbo.sendMessage(userView.getUserFullName(),vo.getString("a0101"),vo.getString(field_name.toLowerCase()),msg);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	
	public void execute() throws GeneralException {
		String receiver=(String)this.getFormHM().get("receiver");
		this.receiverq = (String)this.getFormHM().get("receiverq");
		String out_receiver=(String)this.getFormHM().get("out_receiver");
		String msg=(String)this.getFormHM().get("msg");
		if(msg==null|| "".equals(msg))
			return;
		try
		{
			RecordVo sms_vo=ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
	        if(sms_vo==null)
	        	throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("sys.smsparam.nodifine")));
	        String param=sms_vo.getString("str_value");
	        if(param==null|| "".equals(param))
	        	throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("sys.smsparam.nodifine")));
			StringTokenizer token=new StringTokenizer(receiver,",");
			ArrayList list=new ArrayList();
			while(token.hasMoreTokens())
				list.add(token.nextElement());
			//send_sms(list,msg);
			ArrayList destlist=getReceiverSmsList(list,msg);
			SmsBo smsbo=new SmsBo(this.getFrameconn(),this.getUserView());
			if(!(destlist==null||destlist.size()==0))
				smsbo.batchSendMessage(destlist);
			
			list.clear();
			token=new StringTokenizer(out_receiver,",");
			while(token.hasMoreTokens())
				list.add(token.nextElement());
			destlist=get_BatchList(msg,list);
			smsbo.batchSendMessage(destlist);
			
			this.getFormHM().clear();
			this.getFormHM().put("message",ResourceFactory.getProperty("label.sms.success"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
