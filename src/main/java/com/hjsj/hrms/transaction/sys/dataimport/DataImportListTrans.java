/**
 * 
 */
package com.hjsj.hrms.transaction.sys.dataimport;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.dataimport.DataImportBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Title:DataImportListTrans
 * </p>
 * <p>
 * Description:展现参数设置列表
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2012-06-29
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class DataImportListTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		// 获取所有参数
		ConstantXml constantXml = new ConstantXml(this.frameconn, "IMPORTINFO", "params"); 		
		List elList = constantXml.getAllChildren("/params");
		
		// 保存参数的集合
		ArrayList list = new ArrayList();
		for (int i = 0; i < elList.size(); i++) {
			HashMap map = new HashMap();
			Element el = (Element) elList.get(i);
			
			// 参数id
			String id = el.getAttributeValue("id");
			map.put("id", id);
			
			// 名称
			String name = constantXml.getTextValue("/params/param[@id='"+id+"']/name");
			map.put("name", name);
			
			// 数据库类型
			String dbType = constantXml.getTextValue("/params/param[@id='"+id+"']/dbtype");
			map.put("dbtype", dbType);
			
			// 数据库URL
			String dbUrl = constantXml.getTextValue("/params/param[@id='"+id+"']/dburl");
			map.put("dburl", dbUrl);
			
			// 作业类
			String jobClass = constantXml.getTextValue("/params/param[@id='"+id+"']/jobclass");
			map.put("jobclass", jobClass);
			
			// 作业类
			String enable = constantXml.getTextValue("/params/param[@id='"+id+"']/enable");
			map.put("enable", enable);			
			
			list.add(map);
		}		
		
		DataImportBo bo = new DataImportBo();
		bo.orderById(list);
		this.getFormHM().put("list", list);	
		this.getFormHM().put("maxOrder", bo.getMaxId(list));
	} 
	
	
	
	
}
