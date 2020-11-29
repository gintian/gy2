package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.businessobject.sys.dataimport.DataImportBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import javax.xml.rpc.ParameterMode;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class GzServiceBo {
	private Connection conn=null;
	
	public GzServiceBo(Connection _con)
	{
		this.conn=_con;
	}
	public GzServiceBo(){
		
	}
	public void sendMessage(String xml){
		try{
			String clientName = SystemConfig.getPropertyValue("clientName");
			if(clientName==null||clientName.trim().length()==0)
				throw GeneralExceptionHandler.Handle(new Exception("系统没有设置clientName，请在system.properties文件中设置！"));
			ResourceBundle    myResource = ResourceBundle.getBundle(clientName);
	        String NCAddress=myResource.getString("NCAddress");
	        String ncNameSpace="";
	        Service service = new Service(); 
		    Call call = (Call) service.createCall(); 
		    call.setTargetEndpointAddress(new java.net.URL(NCAddress)); 	
		    call.setReturnType(XMLType.XSD_STRING);
		    call.setUseSOAPAction(true);
		    call.setOperationName("peWageResult");	        
		    call.addParameter("appResultMsg",XMLType.XSD_STRING,ParameterMode.IN);	
		    call.setOperationStyle(org.apache.axis.constants.Style.RPC);
	        call.setOperationUse(org.apache.axis.constants.Use.LITERAL);
		    //call.setSOAPActionURI("appResultMsg");
		   // call.setOperationStyle(Style.DEFAULT);
		    //paramValue = new String(paramValue.getBytes(),"ISO-8859-1");//如果没有加这段，中文参数将会乱码
		    call.invoke( new Object[] {xml} );  
		}catch(Exception e){
			//flag=false;
		}
	}
	/**
	 * 导入信息数据
	 * @return
	 */
	public String impGzData(String gzperiod,String unitcode,String deptcode )throws GeneralException
	{
		String flag="true";
		RowSet rs = null;
		try
		{
			DbWizard dbw =new DbWizard(this.conn);
			ContentDAO dao=new ContentDAO(this.conn);
			String clientName = SystemConfig.getPropertyValue("clientName");
			if(clientName==null||clientName.trim().length()==0)
				throw GeneralExceptionHandler.Handle(new Exception("系统没有设置clientName，请在system.properties文件中设置！"));
			ResourceBundle    myResource = ResourceBundle.getBundle(clientName);
	        String salaryid=myResource.getString("S"+unitcode.toUpperCase());  //绩效薪资类别ID
	        if(salaryid==null||salaryid.trim().length()==0)
	        	throw GeneralExceptionHandler.Handle(new Exception(clientName+".properties文件中没有设置绩效薪资类别！"));
	        String jobId=myResource.getString("jobId");
	        if(jobId==null||jobId.trim().length()==0)
	        	throw GeneralExceptionHandler.Handle(new Exception(clientName+".properties文件中没有设置数据导入映射关系的作业类标识！"));
	        	
	        SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,Integer.parseInt(salaryid));
			String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			if(!(manager!=null&&manager.trim().length()>0))
				throw GeneralExceptionHandler.Handle(new Exception("薪资类别需定义为共享!"));
			DataImportBo bo=new DataImportBo(this.conn);
			
			LazyDynaBean paramBean=new LazyDynaBean();
			paramBean.set("gzperiod", gzperiod);
			paramBean.set("salaryid", salaryid);
			paramBean.set("username", manager);
			paramBean.set("unitcode", unitcode);
			paramBean.set("deptcode", deptcode);
		 
			String extWhere="  period='"+gzperiod+"'";
			
			if(deptcode!=null&&deptcode.trim().length()>0)
				extWhere+=" and deptcode='"+deptcode+"' ";
			else if(unitcode!=null&&unitcode.trim().length()>0)
				extWhere+=" and unitcode='"+unitcode+"' ";
			String tempDate=getNoSalaryPayed(manager,salaryid);
			String fromaddr=this.getFromAddr();
			LazyDynaBean emailInfo=getEmailInfo(gzperiod,unitcode,deptcode,manager);
			String email=(String)emailInfo.get("email");
			rs=dao.search("select count(*) from hr_pe_middle ");
			while(rs.next())
			{
				int count = rs.getInt(1);
				if(count==0)
					return "false";
			}
			if(tempDate.length()==0)  //已提交
			{
				createNewGzData(salaryid,gzperiod,manager,dao); //创建当月薪资  
				bo.importData(conn,jobId,"", extWhere,"gz",paramBean);
				
				if(email!=null&&email.trim().length()>0)
				{
					String title=(String)emailInfo.get("title");
					String content=(String)emailInfo.get("content");
					try{
				    	EMailBo  email_bo=new EMailBo(this.conn,true,""); 
				     	email_bo.sendEmail(title,content,"",fromaddr,email);
					}catch(Exception e)
					{
						
					}
				}
			}
			else
			{
				if(tempDate.substring(0, 6).equals(gzperiod.substring(0,6))) //如果未提交且月份一致
				{
					bo.importData(conn,jobId,"", extWhere,"gz",paramBean);
					
					
					if(email!=null&&email.trim().length()>0)
					{
						String title=(String)emailInfo.get("title");
						String content=(String)emailInfo.get("content");
						try{
					    	EMailBo  email_bo=new EMailBo(this.conn,true,""); 
					    	email_bo.sendEmail(title,content,"",fromaddr,email);
						}catch(Exception e)
						{
							
						}
					}
				}
				else
				{
					String year=gzperiod.substring(0,4);
					String month=gzperiod.substring(4,6);
					//throw GeneralExceptionHandler.Handle(new Exception("-eHR中的"+year+"年"+month+"月绩效工资业务未结束,请稍候提交!"));
					flag="false";
				}
			}
			if (dbw.isExistTable(bo.getImportTempTable(), false)) {
				dbw.dropTable(bo.getImportTempTable());
			}
			
		}
		catch(Exception e)
		{
			flag="false";
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}finally{
			try{
				if(this.conn!=null)
					this.conn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return flag;
	}
	/**
	 * 从系统邮件服务器设置中得到发送邮件的地址
	 * @return
	 */
	public String getFromAddr() throws GeneralException 
	{
		String str = "";
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
        if(stmp_vo==null)
        	return "";
        String param=stmp_vo.getString("str_value");
        if(param==null|| "".equals(param))
        	return ""; 
        try
        {
	        Document doc =PubFunc.generateDom(param);
	        Element root = doc.getRootElement();
	        Element stmp=root.getChild("stmp");	
	        str=stmp.getAttributeValue("from_addr");
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);
        }  
        return str;
	}
	
	
	/**
	 * 获得邮件发送信息
	 * @param gzperiod
	 * @param unitcode
	 * @return
	 */
	private LazyDynaBean getEmailInfo(String gzperiod, String unitcode,String deptcode,String username)
	{
		LazyDynaBean bean=new LazyDynaBean();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select fullname,email,username from operuser where username='"+username+"' ");
			if(rowSet.next())
			{
				if(rowSet.getString("fullname")!=null)
					bean.set("fullname",rowSet.getString("fullname"));
				else
					bean.set("fullname",username);
				
				if(rowSet.getString("email")!=null)
					bean.set("email",rowSet.getString("email"));
				else
					bean.set("email",""); 
			}
			
			String codeitemdesc="";
			if(unitcode!=null&&unitcode.trim().length()>0)
				codeitemdesc=AdminCode.getCodeName("UN",unitcode);
			if(deptcode!=null&&deptcode.trim().length()>0)
				codeitemdesc=AdminCode.getCodeName("UM",deptcode);
			bean.set("codeitemdesc",codeitemdesc);
			bean.set("title",codeitemdesc+"绩效工资报批");
			
			String year=gzperiod.substring(0,4);
			String month=gzperiod.substring(4,6);
			bean.set("content",codeitemdesc+" "+year+"年"+month+"月绩效工资业务已报批，请及时处理！");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return bean;
	}
	
	
	/**
	 * 创建当月薪资
	 * @param salaryid
	 * @param gzperiod
	 * @param username
	 * @param dao
	 * @throws GeneralException
	 */
	private  void createNewGzData(String salaryid,String gzperiod,String username,ContentDAO dao)throws GeneralException
	{
		try
		{
			/**归属日期及归属次数*/
			String date=gzperiod.substring(0,4)+"-"+gzperiod.substring(4, 6)+"-1";
			String count="1";
			if(dateIsExended(date,salaryid,username))
				throw GeneralExceptionHandler.Handle(new Exception("本月绩效薪资已发!")); 
			/**在薪资发放历史记录表中增加一条发放记录*/
			DbNameBo.appendExtendLog(username,Integer.parseInt(salaryid),date, count,this.conn);
			dao.update("delete from "+username+"_salary_"+salaryid);
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		} 
	}
	
	
	/**
	 * 输入的业务日期是否进行过薪资发放
	 * @param date
	 * @return
	 */
	public boolean dateIsExended(String date,String salaryid,String username)
	{
		boolean flag=false;  //是否已发过
		StringBuffer buf=new StringBuffer();
		buf.append("select * from gz_extend_log");
		buf.append(" where sp_flag='06' and salaryid=");
		buf.append(salaryid);
		buf.append(" and ");
		buf.append(" upper(username)='");
		buf.append(username.toUpperCase());
		buf.append("'");
		buf.append(" and A00Z2=");
		buf.append(Sql_switcher.dateValue(date));		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
			{
				flag=true;
			}
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return flag;
	}
	
	
	
	
	
	/**
	 * 判断薪资发放记录表中是否有没提交的工资
	 * @return
	 */
	public String getNoSalaryPayed(String userName,String salaryid)
	{
		String a00z2="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String  sql="select * from gz_extend_log where username='"+userName+"' and sp_flag<>'06' and salaryid="+salaryid; 
			RowSet rowSet=dao.search(sql);
			SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
			if(rowSet.next())
			{
				a00z2=df.format(rowSet.getDate("a00z2"));
			}
			 
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return a00z2;
	}

}
