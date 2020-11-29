/**
 * 
 */
package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-12:17:16:26</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SendRandomPwdTrans extends IBusiness {

	/**
	 * 保存口令
	 * @param a0100
	 * @param password
	 * @param dbpre
	 * @throws GeneralException
	 */
	private void savePassword(String a0100,String password,String dbpre) throws GeneralException
	{
		String field_name=getPwd_Field();
		RecordVo vo=new RecordVo(dbpre+"A01");
		vo.setString(field_name,password);
		vo.setString("a0100",a0100);
		try
		{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		dao.updateValueObject(vo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 求得口令对应的字段
	 * @return
	 */
	private String getPwd_Field()
	{
			String username,password;
	        /**登录参数表,登录用户指定不是username or userpassword*/
	        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
	        if(login_vo==null)
	        {
                username="username";	        	
	            password="userpassword";
	        }
	        else
	        {
	            String login_name = login_vo.getString("str_value").toLowerCase();
	            int idx=login_name.indexOf(",");
	            if(idx==-1)
	            {
	                username="username";
	                password="userpassword";
	            }
	            else
	            {
	                username=login_name.substring(0,idx);
	                if("#".equals(username)|| "".equals(username))
	                	username="username";
	                password=login_name.substring(idx+1);  
	                if("#".equals(password)|| "".equals(password))
	                	password="userpassword";
	                else
	                {
	                	FieldItem item=DataDictionary.getFieldItem(password);
	                	password=item.getItemid();
	                }
	            }
	        }
	        return password;
	}
	
	public void execute() throws GeneralException {
		
		ArrayList selectedlist=(ArrayList)this.getFormHM().get("accountlist");
		String dbpre=(String)this.getFormHM().get("dbpre");
		if(selectedlist==null)
			return;
		EMailBo mailbo=null;
		StringBuffer content=new StringBuffer();
		String a0100=this.userView.getDbname()+this.userView.getA0100();
		try
		{		
			//if(a0100==null||a0100.length()<8)
			//	throw new GeneralException(ResourceFactory.getProperty("error.notlink.a0100"));
//test
//			FactorList factorlist=new FactorList("1+2*3","A0405=02`A0405=03`A0107=1`");
//			cat.debug("--->strwhere+"+ factorlist.getSingleTableSqlExpression("aa"));
			mailbo=new EMailBo(this.getFrameconn(),false,dbpre);
			StringBuffer sbmsg = new StringBuffer(ResourceFactory.getProperty("button.account.createpwd.errermsg")+"<br/>[");
			int n = 0;
			for(int i=0;i<selectedlist.size();i++)
			{
				String tmpa0101 ="";
				try{
					DynaBean bean=(DynaBean)selectedlist.get(i);
					tmpa0101 = (String)bean.get("a0101");
					content.append(tmpa0101);
					content.append("<br>");	
					content.append(ResourceFactory.getProperty("welcome.smtp.loguser"));
					String toAddr=(String)bean.get("a0100");
					content.append(ResourceFactory.getProperty("label.mail.username"));
					content.append(":");
					/* 56449 为了账号安全，用户名部分内容用*代替 guodd 2020-01-03 */
					String username = (String)bean.get("username");
					String secretStr = "*****************************************************";
					if(username.length()<3){
						username = username.substring(0,1)+secretStr.substring(0,username.length()-1);
					}else if(username.length()<6){
						username = username.substring(0,1)+secretStr.substring(0,username.length()-2)+username.substring(username.length()-1);
					}else{
						username = username.substring(0,2)+secretStr.substring(0,username.length()-4)+username.substring(username.length()-2);
					}
					content.append(username);
					content.append("<br>");
					content.append(ResourceFactory.getProperty("label.mail.password"));
					content.append(":");
					content.append(bean.get("password"));
					content.append("<br>");	
					mailbo.sendEmail(ResourceFactory.getProperty("label.selfservice.topic"),content.toString(),null,toAddr);
//					content.setLength(0);
					//savePassword((String)bean.get("a0100"),(String)bean.get("password"),dbpre);				
				}catch(Exception e){
					n++;
					sbmsg.append(tmpa0101+",");
					if(n%20==0)
						sbmsg.append("<br/>");
				}finally{
					content.setLength(0); //不管发送成功 内容都清空  wangb 20171110
				}
			}
			if(n>0)
				throw GeneralExceptionHandler.Handle(new Exception(sbmsg.toString().endsWith(",<br/>")?sbmsg.substring(0, sbmsg.toString().length()-6):sbmsg.substring(0, sbmsg.toString().length()-1)+"]"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			if(mailbo!=null)
				mailbo.close();
		}
	}

}
