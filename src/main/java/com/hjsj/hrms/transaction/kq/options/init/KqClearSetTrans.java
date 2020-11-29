package com.hjsj.hrms.transaction.kq.options.init;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.KQ_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * <p>Title:KqRedDateTrans.java</p>
 * <p>Description>:KqRedDateTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:June 03, 2011 15:40:50 AM</p>
 * <p>@version: 1.0</p>
 * <p>@author: wangzhongjun
 */
public class KqClearSetTrans extends IBusiness {

	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		// 结构参数
		String struts = (String) this.getFormHM().get("struts");
		// 公休日
		String ress = (String) this.getFormHM().get("ress");
		// 节假日
		String fest = (String) this.getFormHM().get("fest");
		// 调休设置
		String daoxiu = (String) this.getFormHM().get("daoxiu");
		// 是否仅保存超级用户设置
		String isSu = (String) this.getFormHM().get("isSu");
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			if ("1".equals(struts)) {// 结构参数
				String sql = "delete from  KQ_PARAMETER";
				KQ_Parameter param = new KQ_Parameter();
				if (!"1".equals(isSu)) {
					// 删除工号、人员库、					
					param.init_XMLData(this.frameconn);
					// 删除其他设置
					dao.delete(sql, new ArrayList());
				} else {
					// 删除工号、人员库、
					String xml = param.search_KQ_PARAMETER(this.frameconn);
					Document doc=PubFunc.generateDom(xml);//读入xml
					String xpath="/kq/parameter";
					XPath reportPath = XPath.newInstance(xpath);// 取得根节点
				    List paraList=reportPath.selectNodes(doc);
				    for (int i = 0; i < paraList.size(); i++) {
				    	Element el = (Element) paraList.get(i);
				    	String b0110 = el.getAttributeValue("B0110");
				    	if (!"UN".equalsIgnoreCase(b0110)) {
				    		doc.getRootElement().removeContent(el);
				    	}
				    }
				    
				    XMLOutputter outputter = new XMLOutputter();
		  	        Format format = Format.getPrettyFormat();
		  	        format.setEncoding("UTF-8");
		  	        outputter.setFormat(format);  	           
		  	        xml = outputter.outputString(doc);
		  	        param.insert_XMLData(this.frameconn, xml);
					// 删除其他设置
		  	        sql = sql + " where upper(b0110)<>'UN'";
					dao.delete(sql, new ArrayList());
				}
				
				//重新加载静态参数
				KqParam.getInstance().reloadAllParam();
			}
			
			if ("1".equals(ress)) {// 公休日
				String sql = "delete from kq_restofweek";
				if ("1".equals(isSu)) {
					sql = sql + " where upper(b0110)<>'UN'";
				} 
				dao.delete(sql, new ArrayList());
			}
			
			if ("1".equals(fest)) {// 节假日
				if (!"1".equals(isSu)) {
					String sql = "delete from kq_feast";
					dao.delete(sql, new ArrayList());
				}
			}
			
			if ("1".equals(daoxiu)) {// 调休设置
				String sql = "delete from kq_turn_rest";
				if ("1".equals(isSu)) {
					sql = sql + " where upper(b0110)<>'UN'";
				} 
				dao.delete(sql, new ArrayList());
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

}
