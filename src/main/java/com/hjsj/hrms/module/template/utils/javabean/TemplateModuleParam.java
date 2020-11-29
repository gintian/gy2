package com.hjsj.hrms.module.template.utils.javabean;

import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.sql.Connection;
import java.util.List;

public class TemplateModuleParam {
    private Connection conn=null;
    private UserView userView;
    private ContentDAO dao; 
	/**
	 * 电子签章服务商 
	 * =0 金格
	 * =1 BJCA
	 */
    private Integer signatureType=0;
	private Boolean signature_usb = false;
	private String server_url = "";
	private String key_item = "";
	/**初始化参数Bo类，将所有参数加载
	 * @param conn
	 * @param userview
	 * @throws GeneralException
	 */
	public TemplateModuleParam(Connection conn, UserView userview) 
	{
		this.conn = conn;
		this.userView=userview;	
		dao = new ContentDAO(this.conn);
		parseXMlParam();
	}
	
	

	/**
	 * 解释业务模板定义的参数
	 * @param sxml
	 * @return
	 */
	private void parseXMlParam()
	{
		Document doc=null;
		Element element=null;
		try
		{
			String xml="";
			 /*
			String sql="select * from constant where constant ='MB_PARAM'";
             RowSet rowSet=dao.search(sql);
		     if (rowSet.next()){
		    	 xml= rowSet.getString("str_value")==null?"":rowSet.getString("str_value");
		     }
		     */
		     
		     if(TemplateStaticDataBo.getConstantStr("MB_PARAM", this.conn)!=null) //20171113 邓灿，采用缓存解决并发下压力过大问题
		    	 xml=TemplateStaticDataBo.getConstantStr("MB_PARAM", conn);
		     
		     
		     if ("".equals(xml))
		    	 return ;
			doc=PubFunc.generateDom(xml);
			String xpath="/params/signature_type";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			List childlist=findPath.selectNodes(doc);	
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				String value = element.getTextTrim();
				if (value!=null && value.length()>0){
					signatureType= Integer.parseInt(value);
				}
			}
			xpath="/params/signature_usb";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);	
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				String value = element.getTextTrim();
				if (value!=null && value.length()>0){
					signature_usb= Boolean.parseBoolean(value);
				}
			}
			
			xpath="/params/server_url";
			findPath = XPath.newInstance(xpath);// 签章服务地址
			childlist=findPath.selectNodes(doc);	
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				String value = element.getTextTrim();
				if (value!=null && value.length()>0){
					server_url= value;
				}
			}
			xpath="/params/key_item";
			findPath = XPath.newInstance(xpath);// 签章密钥key
			childlist=findPath.selectNodes(doc);	
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				String value = element.getTextTrim();
				if (value!=null && value.length()>0){
					key_item= value;
				}
			}
			

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
	}



	public Integer getSignatureType() {
		return signatureType;
	}



	public void setSignatureType(Integer signatureType) {
		this.signatureType = signatureType;
	}



	public Boolean getSignature_usb() {
		return signature_usb;
	}



	public void setSignature_usb(Boolean signature_usb) {
		this.signature_usb = signature_usb;
	}
	
	public String getServer_url() {
		return server_url;
	}



	public void setServer_url(String server_url) {
		this.server_url = server_url;
	}



	public String getKey_item() {
		return key_item;
	}



	public void setKey_item(String key_item) {
		this.key_item = key_item;
	}
		

}
