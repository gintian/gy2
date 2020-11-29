package com.hjsj.hrms.transaction.sys.export;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;


public class SysoutWXSyncThread extends Thread{
	private LazyDynaBean bean;
	private String hr_only_field;//人员的唯一指标
	private String outinfo="";
	boolean isSend = false;
	private String sendValue = "0";
	public SysoutWXSyncThread(LazyDynaBean bean,String hr_only_field)
	{
		this.bean=bean;
		if(hr_only_field==null||hr_only_field.length()<1)
			this.hr_only_field="unique_id";
		else
			this.hr_only_field=hr_only_field;
		String send = (String)bean.get("send");
//		isSend = send!=null&&"1".equals(send)?true:false;
		// 增加了两种同步模式，需要将发送的条件重新判断一下
		isSend = send!=null&&"0".equals(send)?false:true;
		sendValue = send;
	}
	
	public void run()
	{
		try
		{	
			String mess=sendSyncMessage(false,false,false);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	//发送消息更新同步消息
	private String  sendSyncMessage(boolean ishr,boolean isOrg,boolean isPost)
	{
		String ip_addr=(String)bean.get("sync_data_addr");
		String port=(String)bean.get("sync_data_post");
		String username=(String)bean.get("sync_user");
		String pass=(String)bean.get("sync_pass");	
		String sync_base=(String)bean.get("sync_base");	
		String sync_baseType=(String)bean.get("sync_baseType");	
		String org_table=(String)bean.get("org_table");
		String emp_table=(String)bean.get("emp_table");

		StringBuffer buf=new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		buf.append("<hr>");
		buf.append("<recs>"); 
		if(ishr)
			buf.append("<rec>emp</rec>");
		if(isOrg)
			buf.append("<rec>org</rec>");
		if(isPost)
			buf.append("<rec>post</rec>");
		buf.append("</recs>");
		buf.append("<jdbc>");
		//信息xml中添加外部系统代号，读取xml配置时根据 外部系统代号.xml 读取配置文件。以前是读取固定文件，不能实现配置多个相同类型的外部系统
		buf.append("<sysid>"+(String)bean.get("sys_id")+"</sysid>");
		buf.append("<ip_addr>"+ip_addr+"</ip_addr>");
		buf.append("<port>"+port+"</port>");
		buf.append("<username>"+username+"</username>");
		buf.append("<pass>"+pass+"</pass>");
		buf.append("<database>"+sync_base+"</database>");
		buf.append("<datatype>"+sync_baseType+"</datatype>");
		buf.append("<emp_table>"+emp_table+"</emp_table>");
		buf.append("<org_table>"+org_table+"</org_table>");
		buf.append("</jdbc>");
		buf.append("</hr>");
		String url=(String)bean.get("url");
		String mess="";
		try
		{

				String targetNamespace = (String) bean.get("targetNamespace");
				targetNamespace = targetNamespace == null ? "" : targetNamespace;
				mess = getMessage(targetNamespace,"sendSyncMsg","xmlMessage",url,buf.toString());
			
		} catch(Exception e) {
			Category.getInstance("com.hrms.frame.dao.ContentDAO").error(bean.get("sys_id")+"接口调用sendSyncMsg方法时失败");
			System.out.println(bean.get("sys_id")+"接口初始化失败");
			this.outinfo=this.outinfo!=null&&this.outinfo.length()>0?this.outinfo:"接口初始化失败="+e.toString();
			e.printStackTrace();
		}		
		return mess;
	}
	
	/**
	 * 根据空间名和参数名获得webservice接口返回的数据（解决调用.net的webservice时出错的问题）
	 * @param targetNamespace 空间名xml文件中targetNamespace的值
	 * @param paramName 方法sendSyncMsg中的参数名 （.net程序必须为xmlMessage）
	 * @param url webservice的url，不带?wsdl
	 * @param paramName 参数值
	 * @return 调用webservice返回的结果
	 */
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
			Category.getInstance("com.hrms.frame.dao.ContentDAO").error(bean.get("sys_id")+"接口调用失败，信息为"+mess);
			System.out.println(bean.get("sys_id")+"接口初始化失败");
			this.outinfo=this.outinfo!=null&&this.outinfo.length()>0?this.outinfo:bean.get("sys_id")+"接口调用失败="+e.toString();
		}
		return mess;
		
	}
}
