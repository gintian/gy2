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
 * Title:DataImportSaveTrans
 * </p>
 * <p>
 * Description:保存参数
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
public class DataImportSaveTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		// request中的参数
		Map request = (Map) this.getFormHM().get("requestPamaHM");
		
		String id = (String) this.getFormHM().get("id");
		// 获取所有参数
		ConstantXml constantXml = new ConstantXml(this.frameconn, "IMPORTINFO", "params"); 	
        
		int maxid=0;
		if (id == null || id.length() <= 0) {
			List list = constantXml.getAllChildren("/params");
			
			if (list.size() > 0) {
				for(int n=0;n<list.size();n++)
				{
					Element el = (Element) list.get(n);
					String temp = el.getAttributeValue("id");
					if(Integer.parseInt(temp)>maxid)
						maxid=Integer.parseInt(temp);
				}
				id = (maxid + 1) + "";
				
				
			} else {
				id = "1";
			}
			Element paramEl = constantXml.getElement("/params/param[@id='"+id+"']");
			
			if (paramEl == null) {
				Element root = constantXml.getRootNode();
				Element param = new Element("param");
				param.setAttribute("id", id);
				root.addContent(param);
			} else {
				
				Element root = constantXml.getRootNode();
				root.removeContent(paramEl);
				Element param = new Element("param");
				param.setAttribute("id", id);
				root.addContent(param);
			}
			
			Element mapEl = constantXml.getElement("/params/param[@id='"+id+"']/mappings");
			if (mapEl == null) {
				mapEl = new Element("mappings");
				paramEl = constantXml.getElement("/params/param[@id='"+id+"']");
				paramEl.addContent(mapEl);
			}
			
		} else {
			Element paramEl = constantXml.getElement("/params/param[@id='"+id+"']");
			
			if (paramEl == null) {
				Element root = constantXml.getRootNode();
				Element param = new Element("param");
				param.setAttribute("id", id);
				root.addContent(param);
			} else {
				
				Element root = constantXml.getRootNode();
				root.removeContent(paramEl);
				Element param = new Element("param");
				param.setAttribute("id", id);
				root.addContent(param);
			}
			
			Element mapEl = constantXml.getElement("/params/param[@id='"+id+"']/mappings");
			if (mapEl == null) {
				mapEl = new Element("mappings");
				paramEl = constantXml.getElement("/params/param[@id='"+id+"']");
				paramEl.addContent(mapEl);
			}
		}		
		
		
		// 名称
		String name = (String) this.getFormHM().get("name");
		constantXml.setTextValue("/params/param[@id='"+id+"']/name" , name);
		
		// 人员库
		String nbase = (String) this.getFormHM().get("nbase");
		constantXml.setTextValue("/params/param[@id='"+id+"']/nbase", nbase);
		
		
		// 数据库类型
		String dbType = (String) this.getFormHM().get("dbType");
		dbType = PubFunc.keyWord_reback(dbType);
		constantXml.setTextValue("/params/param[@id='"+id+"']/dbtype", dbType);
		
		
		// 数据库URL
		String dbUrl = (String) this.getFormHM().get("dbUrl");
		dbUrl = PubFunc.keyWord_reback(dbUrl);
		constantXml.setTextValue("/params/param[@id='"+id+"']/dburl", dbUrl);
		
		
		// 数据库用户名
		String userName = (String) this.getFormHM().get("userName");
		userName = PubFunc.keyWord_reback(userName);
		constantXml.setTextValue("/params/param[@id='"+id+"']/dbuser", userName);
		
		
		// 数据库密码
		String password = (String) this.getFormHM().get("password");
		password = PubFunc.keyWord_reback(password);
		constantXml.setTextValue("/params/param[@id='"+id+"']/dbpwd", password);
		
		
		// 作业类
		String jobClass = (String) this.getFormHM().get("jobClass");
		constantXml.setTextValue("/params/param[@id='"+id+"']/jobclass", jobClass);
		
		
		// 是否启用
		String enable = (String) this.getFormHM().get("enable");
		constantXml.setTextValue("/params/param[@id='"+id+"']/enable", enable);
		
	
//		<mappings>
//		<ehrtable>eHR表名</ehrtable>
//		<exttable>外部表名</exttable>
//		<relation hr=”主集指标，人员关联指标” ext=”外部系统关联指标”><relation>
//		<srctabcond>外部数据过滤条件</srctabcond>
//		<tagtabcond>eHR数据保护条件</tagtabcond>
//		<fieldref hrfeild=” eHR字段” extfield=”外部字段” ispk=”是否主键，1为是，0为否” defaultvalue=”默认值”></fieldref>
		
		// ehr表名
		String ehrTable = (String) this.getFormHM().get("ehrTable");
		constantXml.setTextValue("/params/param[@id='"+id+"']/mappings/ehrtable", ehrTable);
		
		
		// 外部表名
		String extTable = (String) this.getFormHM().get("extTable");
		constantXml.setTextValue("/params/param[@id='"+id+"']/mappings/exttable", extTable);
		
		// hr关联指标
		String hrRelation = (String) this.getFormHM().get("hrRelation"); 
		constantXml.setAttributeValue("/params/param[@id='"+id+"']/mappings/relation", "hr", hrRelation); 
		
		
		// 外部系统关联指标
		String extRelation = (String) this.getFormHM().get("extRelation");
		constantXml.setAttributeValue("/params/param[@id='"+id+"']/mappings/relation", "ext", extRelation); 
		
		
		// 外部系统过滤条件
		String srcTabCond = (String) this.getFormHM().get("srcTabCond");
		srcTabCond = PubFunc.keyWord_reback(srcTabCond);
		constantXml.setTextValue("/params/param[@id='"+id+"']/mappings/srctabcond", srcTabCond);
		
		
		// hr数据保护条件
		String tagTabCond = (String) this.getFormHM().get("tagTabCond");
		tagTabCond = PubFunc.keyWord_reback(tagTabCond);
		constantXml.setTextValue("/params/param[@id='"+id+"']/mappings/tagtabcond", tagTabCond);
		
		
		// 指标关联
		constantXml.removeNodes("/params/param[@id='"+id+"']/mappings/fieldref");
		String mapping = (String) this.getFormHM().get("mapping");
		mapping = PubFunc.keyWord_reback(mapping);
		if (mapping != null && mapping.length() > 0) {
			Element mapEl = constantXml.getElement("/params/param[@id='"+id+"']/mappings");
			if (mapEl == null) {
				mapEl = new Element("mappings");
				Element paramEl = constantXml.getElement("/params/param[@id='"+id+"']");
				paramEl.addContent(mapEl);
			}
			String []strs = mapping.split(",");
			for (int i = 0; i < strs.length; i++) {
				String []att = strs[i].split(":");
				Element el = new Element("fieldref");
				el.setAttribute("hrfield", att[0]);
				el.setAttribute("extfield", att[1]);
				el.setAttribute("defaultvalue", att[2]);
				el.setAttribute("ispk", att[3]);
				mapEl.addContent(el);
			}
		}
		
	    //强制按id排序
        DataImportBo bo = new DataImportBo();
        bo.orderParamNodeById(constantXml);
        
		constantXml.saveStrValue();
		
	} 
	
	public static void main(String[] str) {
		String n = "1,2,,3,4,";
		String []ns = n.split(",");
		for (int i = 0; i < ns.length; i++) {
			System.out.println("----" + ns[i] + "-----");
		}
	}
}
