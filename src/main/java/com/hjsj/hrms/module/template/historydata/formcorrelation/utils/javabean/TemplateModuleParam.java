package com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
/**
 * 签章相关属性
* @Title: TemplateModuleParam
* @Description:
* @author: hej
* @date 2019年11月19日 下午5:16:17
* @version
 */
public class TemplateModuleParam {
    private Connection conn=null;
    private ContentDAO dao; 
	/**
	 * 电子签章服务商 
	 * =0 金格
	 * =1 BJCA
	 */
    private Integer signatureType=0;
	private Boolean signature_usb = false;
	private int tabid = 0;
	private String archive_id = "";
	/**初始化参数Bo类，将所有参数加载
	 * @param conn
	 * @param userview
	 * @throws GeneralException
	 */
	public TemplateModuleParam(Connection conn, int tabid, String archive_id) {
		this.conn = conn;
		dao = new ContentDAO(this.conn);
		this.archive_id = archive_id;
		this.tabid = tabid;
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
		RowSet rowSet = null;
		try
		{
			String xml="";
			String sql = "select archived_data from t_cells_archive where tabid=? and id=?";
			ArrayList sqlList = new ArrayList();
			sqlList.add(tabid);
			sqlList.add(archive_id);
			rowSet = dao.search(sql, sqlList);
			if (rowSet.next()) {
				xml = rowSet.getString("archived_data");
			}
		    if ("".equals(xml))
		    	 return ;
			doc=PubFunc.generateDom(xml);
			String xpath="/data/template_signature/signature_type";
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
			xpath="/data/template_signature/signature_usb";
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
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
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
}
