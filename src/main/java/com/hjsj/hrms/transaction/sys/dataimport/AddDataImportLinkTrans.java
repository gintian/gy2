/**
 * 
 */
package com.hjsj.hrms.transaction.sys.dataimport;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.dataimport.DataImportBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Element;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title:AddDataImportLinkTrans
 * </p>
 * <p>
 * Description:添加或修改参数
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
public class AddDataImportLinkTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		// request中的参数
		Map request = (Map) this.getFormHM().get("requestPamaHM");
		// 操作类型
		String opt = (String) request.get("opt");
		
		// 业务类
		DataImportBo bo = new DataImportBo(this.frameconn);
		
		if ("addlink".equals(opt)) {// 新增
			this.getFormHM().put("id", "");
			this.getFormHM().put("name", "");
			this.getFormHM().put("nbase", "");
			this.getFormHM().put("dbType", "");
			this.getFormHM().put("dbUrl", "");
			this.getFormHM().put("userName", "");
			this.getFormHM().put("password", "");
			this.getFormHM().put("mapping", "");
			this.getFormHM().put("jobClass", "");
			this.getFormHM().put("enable", "0");
			this.getFormHM().put("ehrTable", "");
			this.getFormHM().put("extTable", "");
			this.getFormHM().put("hrRelation", "");
			this.getFormHM().put("extRelation", "");
			this.getFormHM().put("srcTabCond", "");
			this.getFormHM().put("tagTabCond", "");
			this.getFormHM().put("hrfield", "");
			this.getFormHM().put("extfield", "");
			
			
		} else if ("editlink".equals(opt)){ // 修改
			String id = (String) request.get("id");
			// 获取所有参数
			ConstantXml constantXml = new ConstantXml(this.frameconn, "IMPORTINFO", "params"); 		
							
			// 名称
			String name = constantXml.getTextValue("/params/param[@id='"+id+"']/name");
			this.getFormHM().put("name", name);
			
			// 人员库
			String nbase = constantXml.getTextValue("/params/param[@id='"+id+"']/nbase");
			this.getFormHM().put("nbase", nbase);
			
			// 数据库类型
			String dbType = constantXml.getTextValue("/params/param[@id='"+id+"']/dbtype");
			this.getFormHM().put("dbType", dbType);
			
			// 数据库URL
			String dbUrl = constantXml.getTextValue("/params/param[@id='"+id+"']/dburl");
			this.getFormHM().put("dbUrl", dbUrl);
			
			// 数据库用户名
			String userName = constantXml.getTextValue("/params/param[@id='"+id+"']/dbuser");
			this.getFormHM().put("userName", userName);
			
			// 数据库密码
			String password = constantXml.getTextValue("/params/param[@id='"+id+"']/dbpwd");
			this.getFormHM().put("password", password);
			
			// 作业类
			String jobClass = constantXml.getTextValue("/params/param[@id='"+id+"']/jobclass");
			this.getFormHM().put("jobClass", jobClass);
			
			// 是否启用
			String enable = constantXml.getTextValue("/params/param[@id='"+id+"']/enable");
			this.getFormHM().put("enable", enable);
		
//			<mappings>
//			<ehrtable>eHR表名</ehrtable>
//			<exttable>外部表名</exttable>
//			<relation hr=”主集指标，人员关联指标” ext=”外部系统关联指标”><relation>
//			<srctabcond>外部数据过滤条件</srctabcond>
//			<tagtabcond>eHR数据保护条件</tagtabcond>
//			<fieldref hrfeild=” eHR字段” extfield=”外部字段” ispk=”是否主键，1为是，0为否” defaultvalue=”默认值”></fieldref>
			
			// ehr表名
			String ehrTable = constantXml.getTextValue("/params/param[@id='"+id+"']/mappings/ehrtable");
			this.getFormHM().put("ehrTable", ehrTable);
			
			// 外部表名
			String extTable = constantXml.getTextValue("/params/param[@id='"+id+"']/mappings/exttable");
			this.getFormHM().put("extTable", extTable);
			
			// hr关联指标
			String hrRelation = constantXml.getNodeAttributeValue("/params/param[@id='"+id+"']/mappings/relation", "hr"); 
			this.getFormHM().put("hrRelation", hrRelation);
			
			// 外部系统关联指标
			String extRelation = constantXml.getNodeAttributeValue("/params/param[@id='"+id+"']/mappings/relation", "ext"); 
			this.getFormHM().put("extRelation", extRelation);
			
			// 外部系统过滤条件
			String srcTabCond = constantXml.getTextValue("/params/param[@id='"+id+"']/mappings/srctabcond");
			this.getFormHM().put("srcTabCond", srcTabCond);
			
			// hr数据保护条件
			String tagTabCond = constantXml.getTextValue("/params/param[@id='"+id+"']/mappings/tagtabcond");
			this.getFormHM().put("tagTabCond", tagTabCond);
			
			// 指标关联
			List list = constantXml.getAllChildren("/params/param[@id='"+id+"']/mappings");
			StringBuffer mapping = new StringBuffer();
			for (int i = 0; i < list.size(); i++)  {
				Element el = (Element) list.get(i);
				if ("fieldref".equals(el.getName())) {
					String hrfield = el.getAttributeValue("hrfield");
					String extfield = el.getAttributeValue("extfield");
					String ispk = el.getAttributeValue("ispk");
					String defaultvalue = el.getAttributeValue("defaultvalue");
					mapping.append(hrfield);
					mapping.append(":");
					mapping.append(extfield);
					mapping.append(":");
					mapping.append(defaultvalue);				
					mapping.append(":");
					mapping.append(ispk);
					mapping.append(",");
				}
			}
			
			if (mapping.length() > 0) {
				this.getFormHM().put("mapping", mapping.substring(0, mapping.length()- 1));
			} else {			
				this.getFormHM().put("mapping", "");
			}

		} else if ("del".equals(opt)) {
			String id = (String) request.get("id");
			id = PubFunc.keyWord_reback(id);
			String []ids = id.split(",");
			// 获取所有参数
			ConstantXml constantXml = new ConstantXml(this.frameconn, "IMPORTINFO", "params");
			for (int i = 0; i < ids.length; i++) {
				 
				constantXml.removeNodes("/params/param[@id='"+ids[i]+"']");
								
			}
			
			constantXml.saveStrValue();
		}
		
		
		
		this.getFormHM().put("nbaseList", bo.getNbaseList());
		this.getFormHM().put("dbTypeList", bo.getDbTypeList());
		
	} 
	
	public static void main(String[] str) {
		String n = "1,2,,3,4,";
		String []ns = n.split(",");
		for (int i = 0; i < ns.length; i++) {
			System.out.println("----" + ns[i] + "-----");
		}
	}
}
