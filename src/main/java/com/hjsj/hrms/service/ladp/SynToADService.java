package com.hjsj.hrms.service.ladp;

import com.hjsj.hrms.service.SynUtils;
import com.hrms.struts.constant.SystemConfig;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;

import javax.naming.directory.DirContext;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

/**
 * <p>Title:SynToADService</p>
 * <p>Description:同步人员、组织机构信息到AD服务器</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-06-25</p> 
 * @author wangzhongjun
 * @version 1.0 
 */

public class SynToADService 
{

	private Category log = Category.getInstance(SynToADService.class.getName());

	/**
	 * service必须实现的方法，同步时，ehr就是调用该方法进行同步
	 * 
	 * @param xmlMessage
	 *            String ehr系统传入数据库信息,格式如下 <?xml version="1.0"
	 *            encoding="GB2312"?>
	 *            <hr>
	 *            <recs> <rec>emp</rec> <rec>org</rec> <rec>post</rec></recs> 
	 *            <jdbc> 
	 *            <sysid>AD</sysid><ip_addr>192.192.100.170</ip_addr><port>1433</port>
	 *            <username>yksoft</username> <pass>yksoft1919</pass>
	 *            <database>test</database> <datatype>mssql|oracle|db2</datatype>
	 *            <emp_table>t_hr_view</emp_table> <org_table>t_org_view</org_table>
	 *            <post_table>t_post_view</post_table>"); <emp_where><![CDATA[]]></emp_where>
	 *				<org_where><![CDATA[]]></org_where>
	 *				<post_where><![CDATA[]]></post_where>
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
	 */
	
	
	public String sendSyncMsg(String xmlMessage) 
	{
		// ehr数据库连接
		Connection ehrConn = null;
		// AD服务器连接
		DirContext adConn = null;
		// 需要返回的xml
		StringBuffer returnStr = new StringBuffer();
				
		try 
		{
			SynUtils synUtils = new SynUtils();
			// 解析xml，获得数据库及同步信息
			LazyDynaBean bean = synUtils.parseXml(xmlMessage);
			// 获得ehr数据库连接
			ehrConn = synUtils.createConnByBean(bean);
			
			
			/**判断如果设置可以同步多个AD域，按代号读取配置文件 guodd 2017-03-24*/
			String synMany = SystemConfig.getPropertyValue("sysnc_to_manyAD");
			String sysid = null;
			if("true".equals(synMany) && bean.getMap().containsKey("sysid"))
				sysid = bean.get("sysid").toString();
			LdapUtils ldapUtils = new LdapUtils(sysid);
			
			// 获得Ad服务器连接
			adConn = ldapUtils.getADConn(null);
			LdapParam ldapParam = ldapUtils.getLdapParam();
			// 系统代号
			String ldapCode = ldapParam.getLdapCode();

			if (ldapCode.length() <= 0) 
			{
				String erro = "系统没有设置erp代号，请在AD.xml文件中设置！";
				log.error(erro);
				return "<?xml version=\"1.0\" encoding = \"GB2312\" ?><msg><info>" + erro + "</info></msg>";
			}
			
			// 同步类型
			String syn = (String) bean.get("rec");
			syn = "," + syn + ",";

			Map orgMap = null;
			Map postMap = null;
			Map hrMap = null;
			Map delOrgMap = null;
			Map delPostMap = null;
			Map delHrMap = null;

			// 需要同步组织机构,更新和新增
			if (syn.toLowerCase().contains(",org,") && "true".equalsIgnoreCase(ldapParam.getSyncOrg())) 
			{				
				// 机构表名称
				String view = (String) bean.get("org_table");
				ldapUtils.setOrg_table(view);
				
				// 数据库类型
				String dbType = (String) bean.get("datatype");
				// 查询条件
				String cond = (String) bean.get("org_where");
				String orgcondition = ldapParam.getOrgcondition();
				if (!"".equalsIgnoreCase(orgcondition) && orgcondition.length() > 0) {
					if (cond != null && cond.length() > 0) {
						cond = cond + " and (" + orgcondition + ")";
					} else {
						cond = orgcondition;
					}
				}				
				
				//定义不同步机构条件，但其子机构如不是控制条件机构还同步，同步后挂到上上级机构下（条件只能为为organization字段和b01中指标） 如：b01d3=1 or codeitemdesc like '%合并%'
                String orgNosycnode=ldapParam.getOrgNosycnode();
				if(orgNosycnode!=null && orgNosycnode.length()>1)
				{
					view = ldapUtils.initOrgNosycnode(orgNosycnode, ehrConn, view, cond);
            		cond = "";
                }

				orgMap = ldapUtils.syncOrg(ehrConn, adConn, view, ldapCode, dbType, cond, 1);
			}

			// 需要同步岗位,更新和新增
			if (syn.toLowerCase().contains(",post,") && "true".equalsIgnoreCase(ldapParam.getSyncPost())) 
			{
				// 机构表名称
				String view = (String) bean.get("org_table");
				ldapUtils.setOrg_table(view);
				// 岗位表名称
				view = (String) bean.get("post_table");
				ldapUtils.setPost_table(view);
				// 数据库类型
				String dbType = (String) bean.get("datatype");
				// 查询条件
				String cond = (String) bean.get("post_where");
				String postcondition = ldapParam.getPostcondition();
				if (!"".equalsIgnoreCase(postcondition) && postcondition.length() > 0) 
				{
					if (cond != null && cond.length() > 0) {
						cond = cond + " and (" + postcondition + ")";
					} else {
						cond = postcondition;
					}
				}

				postMap = ldapUtils.syncPost(ehrConn, adConn, view, ldapCode, dbType, cond, 1);
			}

			// 需要同步人员,更新和新增
			if (syn.toLowerCase().contains(",emp,") && "true".equalsIgnoreCase(ldapParam.getSyncHr())) 
			{
				// 机构表名称
				String view = (String) bean.get("org_table");
				ldapUtils.setOrg_table(view);
				// 岗位表名称
				view = (String) bean.get("post_table");
				ldapUtils.setPost_table(view);
				// 人员表名称
				view = (String) bean.get("emp_table");
				ldapUtils.setHr_table(view);
				// 数据库类型
				String dbType = (String) bean.get("datatype");
				// 查询条件
				String cond = (String) bean.get("emp_where");
				String hrcondition = ldapParam.getHrcondition();
				if (!"".equalsIgnoreCase(hrcondition) && hrcondition.length() > 0) 
				{
					if (cond != null && cond.length() > 0) {
						cond = cond + " and (" + hrcondition + ")";
					} else {
						cond = hrcondition;
					}
				}
				
				hrMap = ldapUtils.syncEmp(ehrConn, adConn, view, ldapCode, dbType, cond, 1);
			}

			// 需要同步人员,删除
			if (syn.toLowerCase().contains(",emp,") && "true".equalsIgnoreCase(ldapParam.getSyncHr())) 
			{
				// 人员表名称
				String view = (String) bean.get("emp_table");
				// 数据库类型
				String dbType = (String) bean.get("datatype");
				// 查询条件
				String cond = (String) bean.get("emp_where");
				String hrcondition = ldapParam.getHrcondition();
				if (!"".equalsIgnoreCase(hrcondition) && hrcondition.length() > 0) 
				{
					if (cond != null && cond.length() > 0) {
						cond = cond + " and (" + hrcondition + ")";
					} else {
						cond = hrcondition;
					}
				}

				delHrMap = ldapUtils.syncEmp(ehrConn, adConn, view, ldapCode, dbType, cond, 3);
			}

			// 需要同步岗位,删除
			if (syn.toLowerCase().contains(",post,") && "true".equalsIgnoreCase(ldapParam.getSyncPost())) 
			{
				// 岗位表名称
				String view = (String) bean.get("post_table");
				// 数据库类型
				String dbType = (String) bean.get("datatype");
				// 查询条件
				String cond = (String) bean.get("post_where");
				String postcondition = ldapParam.getPostcondition();
				if (!"".equalsIgnoreCase(postcondition) && postcondition.length() > 0) 
				{
					if (cond != null && cond.length() > 0) {
						cond = cond + " and (" + postcondition + ")";
					} else {
						cond = postcondition;
					}
				}
				
				delPostMap = ldapUtils.syncPost(ehrConn, adConn, view, ldapCode, dbType, cond, 3);
				
			}

			// 需要同步组织机构,删除
			if (syn.toLowerCase().contains(",org,") && "true".equalsIgnoreCase(ldapParam.getSyncOrg())) 
			{
				// 机构表名称
				String view = (String) bean.get("org_table");
				// 数据库类型
				String dbType = (String) bean.get("datatype");
				// 查询条件
				String cond = (String) bean.get("org_where");
				String orgcondition = ldapParam.getOrgcondition();
				if (!"".equalsIgnoreCase(orgcondition) && orgcondition.length() > 0) 
				{
					if (cond != null && cond.length() > 0) {
						cond = cond + " and (" + orgcondition + ")";
					} else {
						cond = orgcondition;
					}
				}
				//定义不同步机构条件，但其子机构如不是控制条件机构还同步，同步后挂到上上级机构下（条件只能为为organization字段和b01中指标） 如：b01d3=1 or codeitemdesc like '%合并%'
                String orgNosycnode = ldapParam.getOrgNosycnode();
				if(orgNosycnode!=null && orgNosycnode.length()>1){
					view = ldapUtils.getTempTable();
            		cond = "";
                }
				delOrgMap = ldapUtils.syncOrg(ehrConn, adConn, view, ldapCode, dbType, cond, 3);
			}

			returnStr.append("<?xml version='1.0' encoding='GB2312'?>");
			returnStr.append("<msg>");
			returnStr.append("<info>");
			int cunt = getCount(orgMap, "errosList") + getCount(postMap, "errosList") + getCount(hrMap, "errosList") + getCount(delOrgMap, "errosList") + getCount(delPostMap, "errosList") + getCount(delHrMap, "errosList");
			if (cunt > 0) 
			{
				returnStr.append("<![CDATA[id为");
				returnStr.append(this.getIds(orgMap, "errosList"));
				returnStr.append(this.getIds(postMap, "errosList"));
				returnStr.append(this.getIds(hrMap, "errosList"));
				returnStr.append(this.getIds(delOrgMap, "errosList"));
				returnStr.append(this.getIds(delPostMap, "errosList"));
				returnStr.append(this.getIds(delHrMap, "errosList"));
				returnStr.append("的记录更新失败]]>");
			}
			returnStr.append("</info>");

			// 机构操作
			int org = getCount(orgMap, "insertToupdate")
					+ getCount(orgMap, "insert") + getCount(orgMap, "update")
					+ getCount(orgMap, "updateToinsert")
					+ getCount(delOrgMap, "delete")
					+ getCount(delOrgMap, "deleteToinsert");
			if (org > 0) 
			{
				returnStr.append("<recs type='org' count='" + org + "'>");

				// 新增
				int add = getCount(orgMap, "insertToupdate")
						+ getCount(orgMap, "insert");
				if (add > 0) {
					returnStr.append("<rec flag='1'>");
					returnStr.append(this.getIds(orgMap, "insertToupdate"));
					returnStr.append(this.getIds(orgMap, "insert"));
					returnStr.append("</rec>");
				}

				// 更新
				int update = getCount(orgMap, "update")
						+ getCount(orgMap, "updateToinsert");
				if (update > 0) {
					returnStr.append("<rec flag='2'>");
					returnStr.append(this.getIds(orgMap, "update"));
					returnStr.append(this.getIds(orgMap, "updateToinsert"));
					returnStr.append("</rec>");
				}

				// 删除
				int del = getCount(delOrgMap, "delete")
						+ getCount(delOrgMap, "deleteToinsert");
				if (del > 0) {
					returnStr.append("<rec flag='3'>");
					returnStr.append(this.getIds(delOrgMap, "delete"));
					returnStr.append(this.getIds(delOrgMap, "deleteToinsert"));
					returnStr.append("</rec>");
				}

				returnStr.append("</recs>");
			}

			// 岗位操作
			int post = getCount(postMap, "insertToupdate")
					+ getCount(postMap, "insert") + getCount(postMap, "update")
					+ getCount(postMap, "updateToinsert")
					+ getCount(delPostMap, "delete")
					+ getCount(delPostMap, "deleteToinsert");
			if (post > 0) 
			{
				returnStr.append("<recs type='post' count='" + post + "'>");

				// 新增
				int add = getCount(postMap, "insertToupdate")
						+ getCount(postMap, "insert");
				if (add > 0) {
					returnStr.append("<rec flag='1'>");
					returnStr.append(this.getIds(postMap, "insertToupdate"));
					returnStr.append(this.getIds(postMap, "insert"));
					returnStr.append("</rec>");
				}

				// 更新
				int update = getCount(postMap, "update")
						+ getCount(postMap, "updateToinsert");
				if (update > 0) {
					returnStr.append("<rec flag='2'>");
					returnStr.append(this.getIds(postMap, "update"));
					returnStr.append(this.getIds(postMap, "updateToinsert"));
					returnStr.append("</rec>");
				}

				// 删除
				int del = getCount(delPostMap, "delete")
						+ getCount(delPostMap, "deleteToinsert");
				if (del > 0) {
					returnStr.append("<rec flag='3'>");
					returnStr.append(this.getIds(delPostMap, "delete"));
					returnStr.append(this.getIds(delPostMap, "deleteToinsert"));
					returnStr.append("</rec>");
				}

				returnStr.append("</recs>");
			}

			// 人员操作
			int emp = getCount(hrMap, "insertToupdate")
					+ getCount(hrMap, "insert") + getCount(hrMap, "update")
					+ getCount(hrMap, "updateToinsert")
					+ getCount(delHrMap, "delete")
					+ getCount(delHrMap, "deleteToinsert");
			if (emp > 0) 
			{
				returnStr.append("<recs type='emp' count='" + emp + "'>");

				// 新增
				int add = getCount(hrMap, "insertToupdate")
						+ getCount(hrMap, "insert");
				if (add > 0) {
					returnStr.append("<rec flag='1'>");
					returnStr.append(this.getIds(hrMap, "insertToupdate"));
					returnStr.append(this.getIds(hrMap, "insert"));
					returnStr.append("</rec>");
				}

				// 更新
				int update = getCount(hrMap, "update")
						+ getCount(hrMap, "updateToinsert");
				if (update > 0) {
					returnStr.append("<rec flag='2'>");
					returnStr.append(this.getIds(hrMap, "update"));
					returnStr.append(this.getIds(hrMap, "updateToinsert"));
					returnStr.append("</rec>");
				}

				// 删除
				int del = getCount(delHrMap, "delete")
						+ getCount(delHrMap, "deleteToinsert");
				if (del > 0) {
					returnStr.append("<rec flag='3'>");
					returnStr.append(this.getIds(delHrMap, "delete"));
					returnStr.append(this.getIds(delHrMap, "deleteToinsert"));
					returnStr.append("</rec>");
				}

				returnStr.append("</recs>");
			}

			returnStr.append("<oths>");
			int orgOth = getCount(orgMap, "insertToupdate")
					+ getCount(orgMap, "updateToinsert")
					+ getCount(orgMap, "errosList")
					+ getCount(delOrgMap, "errosList");
			if (orgOth > 0) 
			{
				returnStr.append("<elems type='org'>");
				returnStr.append(getStr("1", "1", orgMap, "insertToupdate",
						"1", "2", "B"));
				returnStr.append(getStr("2", "1", orgMap, "updateToinsert",
						"1", "1", "B"));
				returnStr.append(getStr("2", "0", orgMap, "errosList", "0",
						"3", "B"));
				returnStr.append(getStr("3", "0", delOrgMap, "errosList", "0",
						"3", "B"));
				returnStr.append("</elems>");
			}

			int postOth = getCount(postMap, "insertToupdate")
					+ getCount(postMap, "updateToinsert")
					+ getCount(postMap, "errosList")
					+ getCount(delPostMap, "errosList");
			if (postOth > 0) 
			{
				returnStr.append("<elems type='org'>");
				returnStr.append(getStr("1", "1", postMap, "insertToupdate",
						"1", "2", "K"));
				returnStr.append(getStr("2", "1", postMap, "updateToinsert",
						"1", "1", "K"));
				returnStr.append(getStr("2", "0", postMap, "errosList", "0",
						"3", "K"));
				returnStr.append(getStr("3", "0", delPostMap, "errosList", "0",
						"3", "K"));
				returnStr.append("</elems>");
			}

			int empOth = getCount(hrMap, "insertToupdate")
					+ getCount(hrMap, "updateToinsert")
					+ getCount(delHrMap, "deleteToinsert")
					+ getCount(hrMap, "errosList")
					+ getCount(delHrMap, "errosList");
			if (empOth > 0) 
			{
				returnStr.append("<elems type='emp'>");
				returnStr.append(getStr("1", "1", hrMap, "insertToupdate", "1",
						"2", "A"));
				returnStr.append(getStr("2", "1", hrMap, "updateToinsert", "1",
						"1", "A"));
				returnStr.append(getStr("3", "1", delHrMap, "deleteToinsert",
						"0", "3", "A"));
				returnStr.append(getStr("2", "0", hrMap, "errosList", "0", "3",
						"A"));
				returnStr.append(getStr("3", "0", delHrMap, "errosList", "0",
						"3", "A"));
				returnStr.append("</elems>");
			}
			returnStr.append("</oths>");
			returnStr.append("</msg>");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				// 关闭数据库连接
				if (ehrConn != null) {
					ehrConn.close();
				}

				// 关闭ldap连接
				if (adConn != null) {
					LdapOperUtils.closeEnvLdapDirContext(adConn);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return returnStr.toString();
	}

	/**
	 * 
	 * @param flag,1为新增，2为更新，3为删除
	 * @param pass，1为成功，0为失败
	 * @param map
	 * @param name
	 * @param isExist，记录是否存在，1为存在，0为不存在
	 * @param opt
	 *            1为新增，2为更新，3为删除
	 * @param type
	 *            A为人员，B为机构，K为岗位
	 * @return
	 */
	private String getStr(String flag, String pass, Map map, String name, String isExist, String opt, String type) 
	{
		StringBuffer str = new StringBuffer();
		String typeStr = "";
		if ("A".equals(type.toUpperCase())) {
			typeStr = "人员";
		} else if ("B".equals(type.toUpperCase())) {
			typeStr = "机构";
		} else if ("K".equals(type.toUpperCase())) {
			typeStr = "岗位";
		}

		if (map != null) 
		{
			ArrayList list = (ArrayList) map.get(name);

			if (list != null && list.size() > 0) 
			{
				for (int i = 0; i < list.size(); i++) 
				{
					str.append("<elem id='" + list.get(i) + "' flag='" + flag
							+ "' pass='" + pass + "'>");
					if ("1".equals(pass)) {
						if ("1".equals(isExist)) {
							str.append("该" + typeStr + "记录在AD中已存在，现执行的是");
							if ("1".equals(opt)) {
								str.append("新增操作");
							} else if ("2".equals(opt)) {
								str.append("修改操作");
							} else if ("3".equals(opt)) {
								str.append("删除操作");
							}
						} else {
							str.append("该" + typeStr + "记录在AD中不存在，现执行的是");
							if ("1".equals(opt)) {
								str.append("新增操作");
							} else if ("2".equals(opt)) {
								str.append("修改操作");
							} else if ("3".equals(opt)) {
								str.append("删除操作");
							}
						}
					} else {
						if ("1".equals(flag)) {
							str.append("该" + typeStr + "记录新增失败");
						} else if ("2".equals(flag)) {
							str.append("该" + typeStr + "记录修改失败");
						} else if ("3".equals(flag)) {
							str.append("该" + typeStr + "记录删除失败");
						}
					}

					str.append("</elem>");
				}
			}
		}
		if (str.length() > 0) {
			return str.toString();
		} else {
			return "";
		}
	}

	private String getIds(Map map, String name) 
	{
		StringBuffer ids = new StringBuffer();
		if (map != null) 
		{
			ArrayList list = (ArrayList) map.get(name);
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					ids.append(list.get(i));
					ids.append(",");
				}
			}
		}
		if (ids.length() > 0) {
			return ids.toString();
		} else {
			return "";
		}
	}

	private int getCount(Map map, String name) 
	{
		if (map != null) 
		{
			ArrayList list = (ArrayList) map.get(name);
			if (list != null && list.size() > 0) {
				return list.size();
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

}