package com.hjsj.hrms.service;

import com.hrms.struts.constant.SystemConfig;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;

import java.sql.Connection;
import java.util.Map;

/**
 * <p>
 * Title:SynEmpOrgToERPService
 * </p>
 * <p>
 * Description:同步人员、组织机构信息到erp的webservice接口
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-3-6
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class SynEmpOrgToERPService {
	
	
	
	/**
	 * service必须实现的方法，同步时，ehr就是调用该方法进行同步
	 * @param xmlMessage String ehr系统传入数据库信息,格式如下
	 *	<?xml version="1.0" encoding="GB2312"?>
	 *		<hr>
	 *			<recs> 
	 *				<rec>emp</rec>
	 *				<rec>org</rec>
	 *			</recs>
	 *			<jdbc>
	 *				<ip_addr>192.192.100.170</ip_addr>
	 *				<port>1433</port>");
	 *				<username>yksoft</username>
	 *				<pass>yksoft1919</pass>");
	 *				<database>test</database>
	 *				<datatype>mssql|oracle|db2</datatype>
	 *				<emp_table>t_hr_view</emp_table>
	 *				<org_table>t_org_view</org_table>
	 *			</jdbc>
	 *		</hr>
	 * @return String xml格式字符窜，格式如下：
	 *		<?xml version="1.0" encoding = "GB2312" ?>
	 *		<msg>
	 *			<info>抛出一些异常信息</info>   
	 *				<!--所有操作成功的编号都要写到<recs> </recs>范围内-->
	 *			<recs type=”信息类型” count=”同步成功记录数”>emp：人员信息；org：机构信息
	 *    			<rec flag=”操作类型”><!--1：新增；2：更新；3：删除
	 *						已同步过的员工编号或组织机构编号,如果多条记录用逗号隔开-->
	 *				</rec>
	 *   			<rec flag=”操作类型”>。。。</rec>
	 *   				…
	 *			</recs>
	 *			<oths>  记录一些特殊处理的记录和一些同步失败记录的失败描述信息。
	 *				如hr系统为新增，但是，外部系统已经存在该人员，则描述为“该id的人员**系统已有记录，
	 *				现执行的是修改操作”。特殊处理但是同步成功的数据id，要在上面的recs中有对应的id编号，
	 *				下面的只做日志处理而不向同步表中对应的记录做同步标识。
	 *  			<elems type=”emp”>
	 *        			按单条记录为一个节点
	 *        			flag: 1：新增；2：更新；3：删除(对应hr同步表的外部系统同步标识)；pass：0：同步失败;1:同步成功
	 *       			<elem id=”员工编号” flag=”操作类型” pass=”1”>
	 *						该人员本系统已存在，现执行的是修改操作
	 *					</elem >
	 *         			<elem id=”员工编号” flag=”操作类型” pass=”0”>
	 *						失败描述信息
	 *					</elem >
	 *    			</elems >
	 *    			<elems type=”org”>
	 *   			</elems>
	 *  		</oths>
	 *		</msg>
	 *		
	 *		例如
	 *		<?xml version="1.0" encoding = "GB2312" ?>
	 *		<msg>
	 *			<info>有数据同步失败信息</info>
	 *			<recs type=”emp” count=”6”> 
	 *				<rec flag=”1”>新增操作
	 *		   			001,002,003
	 *				</rec>
	 *		       	<rec flag=”2”>更新操作
	 *					004,005
	 *				</rec>
	 *		      	<rec flag=”3”>删除操作
	 *					007
	 *				</rec>
	 *		   	</recs>
	 *		   	
	 *			<oths>
	 *				<elems type=”emp”> 
	 *					<elem id=”002” flag=”1” pass=”1”>
	 *						该新增人员记录本系统已存在，现执行的是修改操作
	 *					</elem >
	 *					<elem id=”003” flag=”1” pass=”1”>
	 *						该新增人员记录本系统已存在，现执行的是修改操作
	 *					</elem >
	 *					<elem id=”005” flag=”2” pass=”1”>
	 *						该修改人员记录本系统不存在，现执行的是新增操作
	 *					</elem >
	 *		          	<elem id=”006” flag=”2” pass=”0”>
	 *						该人员记录修改失败，（失败可能原因）
	 *					</elem >
	 *		     	</elems >
	 *			</oths>
	 *		</msg>
	 *
	 *
	 *
	 *
	 */
	public String sendSyncMsg (String xmlMessage) {
		// ehr数据库连接
		Connection ehrConn = null;
		// erp数据库连接
		Connection erpConn = null;
		// 需要返回的xml
		StringBuffer returnStr = new StringBuffer();
		try {		
			SynUtils utils = new SynUtils();
			//解析xml，获得数据库及同步信息
			LazyDynaBean bean = utils.parseXml(xmlMessage);
			// 获得ehr数据库连接
			ehrConn = utils.createConnByBean(bean);
			// 获得erp数据库连接
			erpConn = utils.createConnByBean(utils.getDBBean());
			
			// 系统代号
			String erp = SystemConfig.getPropertyValue("erpcode");
			if (erp.length() <= 0) {
				String erro = "系统没有设置erp代号，请在system.properties文件中设置！";
				Category.getInstance("com.hrms.frame.dao.ContentDAO").error(erro);
				System.out.println(erro);
				return "<?xml version=\"1.0\" encoding = \"GB2312\" ?><msg><info>" + erro + "</info></msg>";
			}
			
			returnStr.append("<?xml version=\"1.0\" encoding=\"GB2312\"?><msg><info><![CDATA[");
			int erroPost = returnStr.length();
			returnStr.append("]]></info>");
			String syn = (String) bean.get("rec");
			syn = "," + syn + ",";
			
			/******同步组织机构表*****/
			StringBuffer org = new StringBuffer();
			StringBuffer orgoth = new StringBuffer();
			int count = 0;
			if (syn.toLowerCase().contains(",org,")) { // 需要同步组织机构
				
				String view = (String) bean.get("org_table");
				Map map = utils.syncOrg(ehrConn, erpConn, view, erp, (String) bean.get("datatype"),erp + "<>0");
				String erro = (String) map.get("erro");
				returnStr.insert(erroPost, erro);
				erroPost = erroPost + erro.length();
				org.append(map.get("org"));
				orgoth.append(map.get("oth"));
				count = count + ((Integer) map.get("count")).intValue();
				
				if (count > 0) {
					returnStr.append("<recs type='org' count='");
					returnStr.append(count);
					returnStr.append("'>");
					returnStr.append(org);
					returnStr.append("</recs>");
				}
				
			} 
			
			
			
			/******同步人员*****/
			count = 0;
			org.setLength(0);
			StringBuffer empoth = new StringBuffer();
			if (syn.toLowerCase().contains(",emp,")) { // 需要同步人员信息
				String view = (String) bean.get("emp_table");
				
				Map map = utils.syncEmp(ehrConn, erpConn, view, erp, (String) bean.get("datatype"),erp + "<>0");
				
				String erro = (String) map.get("erro");
				returnStr.insert(erroPost, erro);
				erroPost = erroPost + erro.length();
				org.append(map.get("org"));
				empoth.append(map.get("oth"));
				count = count + ((Integer) map.get("count")).intValue();
				
				
				if (count > 0) {
					returnStr.append("<recs type='emp' count='");
					returnStr.append(count);
					returnStr.append("'>");
					returnStr.append(org);
					returnStr.append("</recs>");
				}
			}
			
			
			
			returnStr.append("<oths>");
			if (orgoth.length() > 0) {
				returnStr.append("<elems type='org'>");
				returnStr.append(orgoth);
				returnStr.append("</elems>");
			}
			
			if (empoth.length() > 0) {
				returnStr.append("<elems type='emp'>");
				returnStr.append(empoth);
				returnStr.append("</elems>");
			}
			returnStr.append("</oths>");
			returnStr.append("</msg>");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭数据库连接
			try {				
				if (ehrConn != null) {
					ehrConn.close();
				}
				
				if (erpConn != null) {
					erpConn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return returnStr.toString();
	}
	
}