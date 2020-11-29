package com.hjsj.hrms.transaction.sys.export;


import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

public class SyncWXDataTrans  extends IBusiness{

	
	public void execute() throws GeneralException {	
		RowSet rs=null;
		try{
			String url=(String)this.getFormHM().get("url");
			String sys_id=(String)this.getFormHM().get("sys_id");
			String sync_data_addr=SystemConfig.getPropertyValue("hr_data_addr");
			String sync_data_post=SystemConfig.getPropertyValue("hr_data_post");
			String sync_user=SystemConfig.getPropertyValue("hr_sync_user");
			String sync_pass=SystemConfig.getPropertyValue("hr_sync_pass");
			String sync_base=SystemConfig.getPropertyValue("hr_data_base");
			String sync_baseType=SystemConfig.getPropertyValue("dbserver");
			
			HrSyncBo hsb = new HrSyncBo(this.frameconn);
			String hr_only_field=hsb.getAttributeValue(HrSyncBo.HR_ONLY_FIELD);
			
			LazyDynaBean bean =new LazyDynaBean();
			bean.set("sys_id", sys_id);
			bean.set("sync_data_addr", sync_data_addr);
			bean.set("sync_data_post", sync_data_post);
			bean.set("sync_user", sync_user);
			bean.set("sync_pass", sync_pass);
			bean.set("sync_base", sync_base);
			bean.set("sync_baseType", sync_baseType);
			bean.set("org_table", "t_org_view");
			bean.set("emp_table", "t_hr_view");
			bean.set("url",clearUrl_WSDL(url));
			String sql="select * from t_sys_outsync where state=1 and sys_id='"+sys_id+"'";
			ContentDAO dao=new ContentDAO(this.frameconn);			
			rs=dao.search(sql.toString());	
			if(rs.next())
			{
				bean.set("targetNamespace", rs.getString("targetnamespace"));
			}
			SysoutWXSyncThread syncthread=new SysoutWXSyncThread(bean,hr_only_field);//通过线程发送消息
			syncthread.run();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
	}
	private String clearUrl_WSDL(String url)
	{
		String trim_url=url.trim();
		if(trim_url!=null&&trim_url.indexOf("?wsdl")!=-1)
		{
			url=trim_url.substring(0,trim_url.indexOf("?wsdl"));
		}
		return url;
	}

	public String SendMessage(LazyDynaBean bean){
		String mess="";
		String ip_addr=(String)bean.get("sync_data_addr");
		String port=(String)bean.get("sync_data_post");
		String username=(String)bean.get("sync_user");
		String pass=(String)bean.get("sync_pass");	
		String sync_base=(String)bean.get("sync_base");	
		String sync_baseType=(String)bean.get("sync_baseType");	
		String org_table=(String)bean.get("org_table");
		String emp_table=(String)bean.get("emp_table");
		String post_table=(String)bean.get("post_table");

		StringBuffer buf=new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		buf.append("<hr>");
		buf.append("<recs>"); 
		buf.append("</recs>");
		buf.append("<jdbc>");
		//信息xml中添加外部系统代号，读取xml配置时根据 外部系统代号.xml 读取配置文件。以前是读取固定文件，不能实现配置多个相同类型的外部系统
		buf.append("<sysid>"+bean.get("sys_id")+"</sysid>");
		buf.append("<ip_addr>"+ip_addr+"</ip_addr>");
		buf.append("<port>"+port+"</port>");
		buf.append("<username>"+username+"</username>");
		buf.append("<pass>"+pass+"</pass>");
		buf.append("<database>"+sync_base+"</database>");
		buf.append("<datatype>"+sync_baseType+"</datatype>");
		buf.append("<emp_table>"+emp_table+"</emp_table>");
		buf.append("<org_table>"+org_table+"</org_table>");
		buf.append("<post_table>"+post_table+"</post_table>");
		buf.append("</jdbc>");
		buf.append("</hr>");
		String url=(String)bean.get("url");
		try
		{
				String targetNamespace = (String) bean.get("targetNamespace");
				targetNamespace = targetNamespace == null ? "" : targetNamespace;
				mess = getMessage(targetNamespace,"sendSyncMsg","xmlMessage",url,buf.toString());
			
		} catch(Exception e) {
			Category.getInstance("com.hrms.frame.dao.ContentDAO").error(bean.get("sys_id")+"接口调用sendSyncMsg方法时失败");
			System.out.println(bean.get("sys_id")+"接口初始化失败");
			e.printStackTrace();
		}		
		return mess;
	};
	
	private String getMessage(String targetNamespace, String methodName, String paramName, String url, String paramValue) {
		String mess = null;
		
		try {
			Service service = new Service(); 
			Call call = (Call) service.createCall();
			call.setTimeout(new Integer(30*60*1000));
		    call.setTargetEndpointAddress(new java.net.URL(url)); 	
		    call.setReturnType(XMLType.XSD_STRING);
		    call.setUseSOAPAction(true);
		    call.setOperationName(new QName(targetNamespace,methodName));	        
		    call.addParameter(new QName(targetNamespace,paramName),XMLType.XSD_STRING,ParameterMode.IN);	
		    call.setSOAPActionURI(targetNamespace + methodName);
		    mess = (String) call.invoke( new Object[] {paramValue} );  
		} catch(Exception e) {
			e.printStackTrace();
		}
		return mess;
		
	}
}
