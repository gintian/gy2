/**
 * 
 */
package com.hjsj.hrms.businessobject.sys.sysout;

import com.hjsj.hrms.service.ladp.PareXmlUtils;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.constants.Style;
import org.apache.axis.encoding.XMLType;
import org.codehaus.xfire.client.Client;
import org.jdom.Element;
import org.w3c.dom.Node;

import javax.sql.RowSet;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * <p>
 * Title:SyncBo
 * </p>
 * <p>
 * Description:处理同步
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2012-08-08
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class SyncBo {

	public int erroCount = 0;

	private Connection conn = null;

	public SyncBo(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 读取xml内容，价格参数保存到对象中
	 * 
	 * @param file
	 */
	public void setParam(String file, SyncParamPojo pojo) {
		PareXmlUtils utils = new PareXmlUtils(file);

		// 是否同步人员
		String isSyncHr = utils.getAttributeValue("/sync/params/syncscope",
				"hr");
		pojo.setSyncHR("true".equalsIgnoreCase(isSyncHr) ? true : false);

		// 是否同步机构
		String isSyncOrg = utils.getAttributeValue("/sync/params/syncscope",
				"org");
		pojo.setSyncOrg("true".equalsIgnoreCase(isSyncOrg) ? true : false);

		// 是否同步岗位
		String isSyncPost = utils.getAttributeValue("/sync/params/syncscope",
				"post");
		pojo.setSyncPost("true".equalsIgnoreCase(isSyncPost) ? true : false);

		// 是否发送变动前后内容
		String isLog = utils.getTextValue("/sync/params/islog");
		pojo.setLog("true".equalsIgnoreCase(isLog) ? true : false);

		// 人员同步条件
		String hrCond = utils.getTextValue("/sync/params/hrcondition");
		pojo.setHrCond(hrCond);

		// 机构同步条件
		String orgCond = utils.getTextValue("/sync/params/orgcondition");
		pojo.setOrgCond(orgCond);

		// 岗位同步条件
		String postCond = utils.getTextValue("/sync/params/postcondition");
		pojo.setPostCond(postCond);

		// 人员webservice
		String hr_webservice_username = utils.getAttributeValue(
				"/sync/params/hrwebservice", "username");
		pojo.setHr_webservice_username(hr_webservice_username);
		String hr_webservice_password = utils.getAttributeValue(
				"/sync/params/hrwebservice", "password");
		pojo.setHr_webservice_password(hr_webservice_password);
		String hr_webservice_url = utils.getAttributeValue(
				"/sync/params/hrwebservice", "url");
		pojo.setHr_webservice_url(hr_webservice_url);
		String hr_webservice_function = utils.getAttributeValue(
				"/sync/params/hrwebservice", "function");
		pojo.setHr_webservice_function(hr_webservice_function);
		String hr_webservice_namespace = utils.getAttributeValue(
				"/sync/params/hrwebservice", "namespace");
		pojo.setHr_webservice_namespace(hr_webservice_namespace);
		String hr_webservice_paramname = utils.getAttributeValue(
				"/sync/params/hrwebservice", "paramname");
		pojo.setHr_webservice_paramname(hr_webservice_paramname);
		String hr_webservice_style = utils.getAttributeValue(
				"/sync/params/hrwebservice", "style");
		pojo.setHr_webservice_style(hr_webservice_style);
		
		String hr_webservice_type = utils.getAttributeValue(
				"/sync/params/hrwebservice", "type");
		pojo.setHr_webservice_type(hr_webservice_type);
		
		String hr_webservice_prefix = utils.getAttributeValue(
				"/sync/params/hrwebservice", "prefix");
		pojo.setHr_webservice_prefix(hr_webservice_prefix);
		String hr_webservice_soapstr = utils.getTextValue("/sync/params/hrwebservice");
		pojo.setHr_webservice_soapstr(hr_webservice_soapstr);

		// 机构webservice
		String org_webservice_username = utils.getAttributeValue(
				"/sync/params/orgwebservice", "username");
		pojo.setOrg_webservice_username(org_webservice_username);
		String org_webservice_password = utils.getAttributeValue(
				"/sync/params/orgwebservice", "password");
		pojo.setOrg_webservice_password(org_webservice_password);
		String org_webservice_url = utils.getAttributeValue(
				"/sync/params/orgwebservice", "url");
		pojo.setOrg_webservice_url(org_webservice_url);
		String org_webservice_function = utils.getAttributeValue(
				"/sync/params/orgwebservice", "function");
		pojo.setOrg_webservice_function(org_webservice_function);
		String org_webservice_namespace = utils.getAttributeValue(
				"/sync/params/orgwebservice", "namespace");
		pojo.setOrg_webservice_namespace(org_webservice_namespace);
		String org_webservice_paramname = utils.getAttributeValue(
				"/sync/params/orgwebservice", "paramname");
		pojo.setOrg_webservice_paramname(org_webservice_paramname);
		String org_webservice_style = utils.getAttributeValue(
				"/sync/params/orgwebservice", "style");
		pojo.setOrg_webservice_style(org_webservice_style);
		
		String org_webservice_type = utils.getAttributeValue(
				"/sync/params/orgwebservice", "type");
		pojo.setOrg_webservice_type(org_webservice_type);
		
		String org_webservice_prefix = utils.getAttributeValue(
				"/sync/params/orgwebservice", "prefix");
		pojo.setOrg_webservice_prefix(org_webservice_prefix);
		
		String org_webservice_soapstr = utils.getTextValue("/sync/params/orgwebservice");
		pojo.setOrg_webservice_soapstr(org_webservice_soapstr);

		// 岗位webservice
		String post_webservice_username = utils.getAttributeValue(
				"/sync/params/postwebservice", "username");
		pojo.setPost_webservice_username(post_webservice_username);
		String post_webservice_password = utils.getAttributeValue(
				"/sync/params/postwebservice", "password");
		pojo.setPost_webservice_password(post_webservice_password);
		String post_webservice_url = utils.getAttributeValue(
				"/sync/params/postwebservice", "url");
		pojo.setPost_webservice_url(post_webservice_url);
		String post_webservice_function = utils.getAttributeValue(
				"/sync/params/postwebservice", "function");
		pojo.setPost_webservice_function(post_webservice_function);
		String post_webservice_namespace = utils.getAttributeValue(
				"/sync/params/postwebservice", "namespace");
		pojo.setPost_webservice_namespace(post_webservice_namespace);
		String post_webservice_paramname = utils.getAttributeValue(
				"/sync/params/postwebservice", "paramname");
		pojo.setPost_webservice_paramname(post_webservice_paramname);
		String post_webservice_style = utils.getAttributeValue(
				"/sync/params/postwebservice", "style");
		pojo.setPost_webservice_style(post_webservice_style);
		
		String post_webservice_type = utils.getAttributeValue(
				"/sync/params/postwebservice", "type");
		pojo.setPost_webservice_type(post_webservice_type);
		
		String post_webservice_prefix = utils.getAttributeValue(
				"/sync/params/postwebservice", "prefix");
		pojo.setPost_webservice_prefix(post_webservice_prefix);
		
		String post_webservice_soapstr = utils.getTextValue("/sync/params/postwebservice");
		pojo.setPost_webservice_soapstr(post_webservice_soapstr);

		// 人员主键
		String hrXmlKey = utils.getAttributeValue("/sync/params/hrkey",
				"xmlkeyname");
		String hrDbKey = utils.getAttributeValue("/sync/params/hrkey",
				"dbkeyname");
		pojo.setHrXmlKey(hrXmlKey);
		pojo.setHrDbKey(hrDbKey);

		// 机构主键
		String orgXmlKey = utils.getAttributeValue("/sync/params/orgkey",
				"xmlkeyname");
		String orgDbKey = utils.getAttributeValue("/sync/params/orgkey",
				"dbkeyname");
		pojo.setOrgXmlKey(orgXmlKey);
		pojo.setOrgDbKey(orgDbKey);

		// 岗位主键
		String postXmlKey = utils.getAttributeValue("/sync/params/postkey",
				"xmlkeyname");
		String postDbKey = utils.getAttributeValue("/sync/params/postkey",
				"dbkeyname");
		pojo.setPostXmlKey(postXmlKey);
		pojo.setPostDbKey(postDbKey);

		// 人员记录标志
		String hrXmlFlag = utils.getAttributeValue("/sync/params/hrflag",
				"xmlflagname");
		String hrDbFlag = utils.getAttributeValue("/sync/params/hrflag",
				"dbflagname");
		pojo.setHrXmlFlag(hrXmlFlag);
		pojo.setHrDbFlag(hrDbFlag);

		// 机构记录标志
		String orgXmlFlag = utils.getAttributeValue("/sync/params/orgflag",
				"xmlflagname");
		String orgDbFlag = utils.getAttributeValue("/sync/params/orgflag",
				"dbflagname");
		pojo.setOrgXmlFlag(orgXmlFlag);
		pojo.setOrgDbFlag(orgDbFlag);

		// 岗位记录标志
		String postXmlFlag = utils.getAttributeValue("/sync/params/postflag",
				"xmlflagname");
		String postDbFlag = utils.getAttributeValue("/sync/params/postflag",
				"dbflagname");
		pojo.setPostXmlFlag(postXmlFlag);
		pojo.setPostDbFlag(postDbFlag);

		// xml编码
		String xmlcode = utils.getTextValue("/sync/params/xmlcode");
		pojo.setXmlcode(xmlcode);
		// 最大条数
		String maxnum = utils.getTextValue("/sync/params/maxnum");
		try {
			pojo.setMaxnum(Integer.parseInt(maxnum));
		} catch (Exception e) {
			pojo.setMaxnum(200);
		}

		// 人员根节点名称
		String hrRootName = utils.getAttributeValue("/sync/params/rootname",
				"hr");
		pojo.setHrRootName(hrRootName);

		// 机构根节点名称
		String orgRootName = utils.getAttributeValue("/sync/params/rootname",
				"org");
		pojo.setOrgRootName(orgRootName);

		// 岗位根节点名称
		String postRootName = utils.getAttributeValue("/sync/params/rootname",
				"post");
		pojo.setPostRootName(postRootName);

		// 人员节点名称
		String hrNodeName = utils.getAttributeValue("/sync/params/nodename",
				"hr");
		pojo.setHrNodeName(hrNodeName);

		// 机构节点名称
		String orgNodeName = utils.getAttributeValue("/sync/params/nodename",
				"org");
		pojo.setOrgNodeName(orgNodeName);
		// 岗位节点名称
		String postNodeName = utils.getAttributeValue("/sync/params/nodename",
				"post");
		pojo.setPostNodeName(postNodeName);

		// 人员返回值的根节点名称
		String hrReturnRootName = utils.getAttributeValue(
				"/sync/params/returnrootname", "hr");
		pojo.setHrReturnRootName(hrReturnRootName);

		// 机构返回值的根节点名称
		String orgReturnRootName = utils.getAttributeValue(
				"/sync/params/returnrootname", "org");
		pojo.setOrgReturnRootName(orgReturnRootName);

		// 岗位返回值的根节点名称
		String postReturnRootName = utils.getAttributeValue(
				"/sync/params/returnrootname", "post");
		pojo.setPostReturnRootName(postReturnRootName);

		// 人员返回值的节点名称
		String hrReturnNodeName = utils.getAttributeValue(
				"/sync/params/returnnodename", "hr");
		pojo.setHrReturnNodeName(hrReturnNodeName);

		// 机构返回值的节点名称
		String orgReturnNodeName = utils.getAttributeValue(
				"/sync/params/returnnodename", "org");
		pojo.setOrgReturnNodeName(orgReturnNodeName);

		// 岗位返回值的节点名称
		String postReturnNodeName = utils.getAttributeValue(
				"/sync/params/returnnodename", "post");
		pojo.setPostReturnNodeName(postReturnNodeName);

		// 人员返回值的id
		String hrReturnKey = utils.getAttributeValue("/sync/params/returnkey",
				"hr");
		pojo.setHrReturnKey(hrReturnKey);

		// 机构返回值的id
		String orgReturnKey = utils.getAttributeValue("/sync/params/returnkey",
				"org");
		pojo.setOrgReturnKey(orgReturnKey);

		// 岗位返回值的id
		String postReturnKey = utils.getAttributeValue(
				"/sync/params/returnkey", "post");
		pojo.setPostReturnKey(postReturnKey);

		// 人员返回值的记录标志
		String hrReturnHrFlag = utils.getAttributeValue(
				"/sync/params/returnhrflag", "hr");
		pojo.setHrReturnHrFlag(hrReturnHrFlag);

		// 机构返回值的记录标志
		String orgReturnHrFlag = utils.getAttributeValue(
				"/sync/params/returnhrflag", "org");
		pojo.setOrgReturnHrFlag(orgReturnHrFlag);

		// 岗位返回值的记录标志
		String postReturnHrFlag = utils.getAttributeValue(
				"/sync/params/returnhrflag", "post");
		pojo.setPostReturnHrFlag(postReturnHrFlag);

		// 人员返回值的是否操作成功
		String hrReturnFlag = utils.getAttributeValue(
				"/sync/params/returnflag", "hr");
		pojo.setHrReturnFlag(hrReturnFlag);

		// 机构返回值的是否操作成功
		String orgReturnFlag = utils.getAttributeValue(
				"/sync/params/returnflag", "org");
		pojo.setOrgReturnFlag(orgReturnFlag);

		// 岗位返回值的是否操作成功
		String postReturnFlag = utils.getAttributeValue(
				"/sync/params/returnflag", "post");
		pojo.setPostReturnFlag(postReturnFlag);
		
		// 人员固定内容父节点
		String hrfixParent = utils.getAttributeValue(
				"/sync/params/hrfix", "parent");
		hrfixParent = hrfixParent == null ? "" : hrfixParent;
		pojo.setHrfixParent(hrfixParent);
		
		// 人员固定内容
		String hrfix = utils.getTextValue("/sync/params/hrfix");
		hrfix = hrfix == null ? "" : hrfix;
		pojo.setHrfix(hrfix);
		
		// 机构固定内容父节点
		String orgfixParent = utils.getAttributeValue(
				"/sync/params/orgfix", "parent");
		orgfixParent = orgfixParent == null ? "" : orgfixParent;
		pojo.setOrgfixParent(orgfixParent);
		
		// 机构固定内容
		String orgfix = utils.getTextValue("/sync/params/orgfix");
		orgfix = orgfix == null ? "" : orgfix;
		pojo.setOrgfix(orgfix);
		
		// 岗位固定内容父节点
		String postfixParent = utils.getAttributeValue(
				"/sync/params/postfix", "parent");
		postfixParent = postfixParent == null ? "" : postfixParent;
		pojo.setPostfixParent(postfixParent);
		
		// 岗位固定内容
		String postfix = utils.getTextValue("/sync/params/postfix");
		postfix = postfix == null ? "" : postfix;
		pojo.setPostfix(postfix);
		
		
		
		// 人员无论指标变化与否，都将显示此列，只针对发送指标变动前后信息时有效
		String hrfixfield = utils.getTextValue("/sync/params/hrfixfield");
		hrfixfield = hrfixfield == null ? "" : hrfixfield;
		pojo.setHrfixfield(hrfixfield);
		
		// 机构无论指标变化与否，都将显示此列，只针对发送指标变动前后信息时有效
		String orgfixfield = utils.getTextValue("/sync/params/orgfixfield");
		orgfixfield = orgfixfield == null ? "" : orgfixfield;
		pojo.setOrgfixfield(orgfixfield);
		
		// 岗位无论指标变化与否，都将显示此列，只针对发送指标变动前后信息时有效
		String postfixfield = utils.getTextValue("/sync/params/postfixfield");
		postfixfield = postfixfield == null ? "" : postfixfield;
		pojo.setPostfixfield(postfixfield);
		
		
		// 人员这些指标变化后才发送消息，
		String hrfieldchange = utils.getTextValue("/sync/params/hrfieldchange");
		hrfieldchange = hrfieldchange == null ? "" : hrfieldchange;
		pojo.setHrfieldchange(hrfieldchange);
		
		// 机构这些指标变化后才发送消息，
		String orgfieldchange = utils.getTextValue("/sync/params/orgfieldchange");
		orgfieldchange = orgfieldchange == null ? "" : orgfieldchange;
		pojo.setOrgfieldchange(orgfieldchange);
		
		// 岗位这些指标变化后才发送消息，
		String postfieldchange = utils.getTextValue("/sync/params/postfieldchange");
		postfieldchange = postfieldchange == null ? "" : postfieldchange;
		pojo.setPostfieldchange(postfieldchange);
		
		
		// 返回值bom头的开始位置
		String bomStart = utils.getAttributeValue("/sync/params/returnstrbom", "start");
		if (bomStart != null && bomStart.trim().length() > 0) {
			try {
				 pojo.setBomStart(Integer.parseInt(bomStart));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// 返回值bom头的结束位置
		String bomEnd = utils.getAttributeValue("/sync/params/returnstrbom", "end");
		if (bomEnd != null && bomEnd.trim().length() > 0) {
			try {
				 pojo.setBomEnd(Integer.parseInt(bomEnd));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Map dateMap = pojo.getDateFormateField();
		
		// 人员对应指标
		List hrList = utils.getNodes("/sync/fields_ref/hrfield/field_ref");
		Map hrMap = new HashMap();
		if (hrList != null) {
			for (int i = 0; i < hrList.size(); i++) {
				Element el = (Element) hrList.get(i);
				String hrfield = el.getAttributeValue("hrfield");
				String xmlnodename = el.getAttributeValue("xmlnodename");
				hrMap.put(hrfield, xmlnodename);
				
				if ("D".equalsIgnoreCase(el.getAttributeValue("type"))) {
					String formate = el.getAttributeValue("formate");
					if (formate != null && formate.length() > 0) {
						dateMap.put(hrfield, formate);
					}
				}
			}
		}
		pojo.setHrFieldRefMap(hrMap);

		// 机构对应指标
		List orgList = utils.getNodes("/sync/fields_ref/orgfield/field_ref");
		Map orgMap = new HashMap();
		if (orgList != null) {
			for (int i = 0; i < orgList.size(); i++) {
				Element el = (Element) orgList.get(i);
				String hrfield = el.getAttributeValue("hrfield");
				String xmlnodename = el.getAttributeValue("xmlnodename");
				orgMap.put(hrfield, xmlnodename);
				
				if ("D".equalsIgnoreCase(el.getAttributeValue("type"))) {
					String formate = el.getAttributeValue("formate");
					if (formate != null && formate.length() > 0) {
						dateMap.put(hrfield, formate);
					}
				}
			}
		}
		pojo.setOrgFieldRefMap(orgMap);

		// 岗位对应指标
		List postList = utils.getNodes("/sync/fields_ref/postfield/field_ref");
		Map postMap = new HashMap();
		if (postList != null) {
			for (int i = 0; i < postList.size(); i++) {
				Element el = (Element) postList.get(i);
				String hrfield = el.getAttributeValue("hrfield");
				String xmlnodename = el.getAttributeValue("xmlnodename");
				postMap.put(hrfield, xmlnodename);
				
				if ("D".equalsIgnoreCase(el.getAttributeValue("type"))) {
					String formate = el.getAttributeValue("formate");
					if (formate != null && formate.length() > 0) {
						dateMap.put(hrfield, formate);
					}
				}
			}
		}
		pojo.setPostFieldRefMap(postMap);
		
		pojo.setDateFormateField(dateMap);

	}

	/**
	 * 返回日志错误信息，失败次数加一
	 */
	public void updateFailtime(ContentDAO dao, String sysId) {
		String sql = "update t_sys_outsync set fail_time=fail_time+1 where sys_id='"
				+ sysId + "'";
		try {
			dao.update(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param pojo
	 *            xml参数
	 * @param sysId
	 *            系统代号
	 * @param hrCond
	 *            人员条件
	 * @param orgCond
	 *            机构条件
	 * @param postCond
	 *            岗位条件
	 * @return
	 */
	public String sendSyncMessage(SyncParamPojo pojo, String sysId,
			String hrCond, String orgCond, String postCond) {

		// 返回的消息
		String mess = "";

		try {
			ContentDAO dao = new ContentDAO(this.conn);

			// xml头信息
			StringBuffer xmlTop = new StringBuffer();
			xmlTop.append("<?xml version='1.0' encoding='");
			xmlTop.append(pojo.getXmlcode());
			xmlTop.append("' ?>");

			// 机构新增更新
			if (pojo.isSyncOrg()) {
				mess += sendMessages("B", "1", pojo, sysId, orgCond, xmlTop
						.toString(), dao);
			}

			// 岗位新增更新
			if (pojo.isSyncPost()) {
				mess += sendMessages("K", "1", pojo, sysId, postCond, xmlTop
						.toString(), dao);
			}

			// 人员新增更新
			if (pojo.isSyncHR()) {
				mess += sendMessages("A", "1", pojo, sysId, hrCond, xmlTop
						.toString(), dao);
			}

			// 人员删除
			if (pojo.isSyncHR()) {
				mess += sendMessages("A", "3", pojo, sysId, hrCond, xmlTop
						.toString(), dao);
			}

			// 岗位删除
			if (pojo.isSyncPost()) {
				mess += sendMessages("K", "3", pojo, sysId, postCond, xmlTop
						.toString(), dao);
			}

			// 机构删除
			if (pojo.isSyncOrg()) {
				mess += sendMessages("B", "3", pojo, sysId, orgCond, xmlTop
						.toString(), dao);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mess;
	}

	private String sendMessages(String type, String flag, SyncParamPojo pojo,
			String sysId, String cond, String xmlTop, ContentDAO dao) {
		String mess = "";
		RowSet rs = null;
		try {

			String rootName = "";
			String nodeName = "";
			String xmlKey = "";
			String xmlFlag = "";
			String dbKey = "";
			String dbFlag = "";
			String fix = "";
			String fixParent = "";
			String fixField = "";
			Map map = null;
			String fieldChange = "";
			if ("A".equalsIgnoreCase(type)) {
				rootName = pojo.getHrRootName();
				nodeName = pojo.getHrNodeName();
				xmlKey = pojo.getHrXmlKey();
				dbKey = pojo.getHrDbKey();
				map = pojo.getHrFieldRefMap();
				xmlFlag = pojo.getHrXmlFlag();
				dbFlag = pojo.getHrDbFlag();
				fix = pojo.getHrfix();
				fixParent = pojo.getHrfixParent();
				fixField = pojo.getHrfixfield();
				fieldChange = pojo.getHrfieldchange();
			} else if ("B".equalsIgnoreCase(type)) {
				rootName = pojo.getOrgRootName();
				nodeName = pojo.getOrgNodeName();
				xmlKey = pojo.getOrgXmlKey();
				dbKey = pojo.getOrgDbKey();
				map = pojo.getOrgFieldRefMap();
				xmlFlag = pojo.getOrgXmlFlag();
				dbFlag = pojo.getOrgDbFlag();
				fix = pojo.getOrgfix();
				fixParent = pojo.getOrgfixParent();
				fixField = pojo.getOrgfixfield();
				fieldChange = pojo.getOrgfieldchange();
			} else if ("K".equalsIgnoreCase(type)) {
				rootName = pojo.getPostRootName();
				nodeName = pojo.getPostNodeName();
				xmlKey = pojo.getPostXmlKey();
				dbKey = pojo.getPostDbKey();
				map = pojo.getPostFieldRefMap();
				xmlFlag = pojo.getPostXmlFlag();
				dbFlag = pojo.getPostDbFlag();
				fix = pojo.getPostfix();
				fixParent = pojo.getPostfixParent();
				fixField = pojo.getPostfixfield();
				fieldChange = pojo.getPostfieldchange();
			}
			
			Map dateMap = pojo.getDateFormateField();
			
			// 获取查询机构更新的sql
			String sql = this.getSQL(type, flag, pojo, sysId, cond, fixField);
			fixField = ("," + fixField + ",").toLowerCase();
			boolean change = false;
			if (fieldChange != null && fieldChange.trim().length() > 0) {
				fieldChange = ("," + fieldChange + ",").toLowerCase();
				change = true;
			} 
			// 查询
			rs = dao.search(sql);
			// 计数器
			int count = 0;
			// 发送的xml
			StringBuffer xml = new StringBuffer();
			xml.append(xmlTop);
			// 根节点
			xml.append("<");
			xml.append(rootName);
			xml.append(">");
			
			if ("root".equalsIgnoreCase(fixParent)) {
				xml.append(fix);
			}

			while (rs.next()) {

				StringBuffer xml2 = new StringBuffer();
				Iterator it = map.entrySet().iterator();
				boolean isNode = false;
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry) it.next();

					if (pojo.isLog()) {
						String str[] = entry.getValue().toString().split(",");
						
						String value = rs.getString(str[0]);
						
						
						value = value == null ? "" : value;
						String value2 = rs.getString(str[1]);
						value2 = value2 == null ? "" : value2;
						
						if (! value.equals(value2)) {
							xml2.append("<");
							xml2.append(str[0]);
							xml2.append("><![CDATA[");
		
							xml2.append(value);
		
							xml2.append("]]></");
							xml2.append(str[0]);
							xml2.append(">");
							
							
							xml2.append("<");
							xml2.append(str[1]);
							xml2.append("><![CDATA[");
		
							
		
							xml2.append(value2);
		
							xml2.append("]]></");
							xml2.append(str[1]);
							xml2.append(">");
							
							if (change) {
								if (fieldChange.contains("," + entry.getKey().toString().toLowerCase() + ",")) {
									isNode = true;
								}
							} else {
								isNode = true;
							} 
						} else if (fixField.contains("," + entry.getKey().toString().toLowerCase() + ",")) {
							value = rs.getString("fix_fix_" + entry.getKey().toString());
							value = value == null ? "" : value;
							xml2.append("<");
							xml2.append(str[0]);
							xml2.append("><![CDATA[");
		
							xml2.append(value);
		
							xml2.append("]]></");
							xml2.append(str[0]);
							xml2.append(">");
							
							
							xml2.append("<");
							xml2.append(str[1]);
							xml2.append("><![CDATA[");
		
							
		
							xml2.append(value);
		
							xml2.append("]]></");
							xml2.append(str[1]);
							xml2.append(">");
							if (!change) {
								isNode = true;
							}
						}
					} else {
						xml2.append("<");
						xml2.append(entry.getValue());
						xml2.append("><![CDATA[");
	
						String value = "";
						try {
							FieldItem fielditem = DataDictionary.getFieldItem(entry.getKey().toString());
							if(fielditem!=null)
							{
								String itemtype = fielditem.getItemtype();
								if (itemtype!=null && itemtype.trim().length()>0 && "D".equalsIgnoreCase(itemtype))
								{
									SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
									if(rs.getDate(entry.getKey().toString())!=null && rs.getDate(entry.getKey().toString()).toString().trim().length()>0) {
                                        value = format.format(rs.getDate(entry.getKey().toString()));
                                    }
								}
								else
								{
									value = rs.getString(entry.getKey().toString());
								}
							}
							else
							{
								value = rs.getString(entry.getKey().toString());
							}
						} catch (Exception e) {
							try {
								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
								if(rs.getDate(entry.getKey().toString())!=null && rs.getDate(entry.getKey().toString()).toString().trim().length()>0) {
                                    value = format.format(rs.getDate(entry.getKey().toString()));
                                }
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						}
						try {							
							rs.getString(entry.getKey().toString());
						} catch (Exception e) {
							if(rs.getDate(entry.getKey().toString())!=null && rs.getDate(entry.getKey().toString()).toString().trim().length()>0) {
                                rs.getDate(entry.getKey().toString());
                            }
						}
//						rs.getString(entry.getKey().toString());
						
						
						value = value == null ? "" : value;
	
						xml2.append(value);
	
						xml2.append("]]></");
						xml2.append(entry.getValue());
						xml2.append(">");
						isNode = true;
					}

				}
				
				if (isNode || "3".equals(flag)) {
					// 机构节点
					xml.append("<");
					xml.append(nodeName);
					xml.append(">");
	
					// 主键节点
					xml.append("<");
					xml.append(xmlKey);
					xml.append("><![CDATA[");
	
					// 主键值
					
					String strKey = dbKey;
					if (pojo.isLog()) {
						strKey = "fix_fix_" + dbKey;
						
					}
					
					String orgId = rs.getString(strKey);
					orgId = orgId == null ? "" : orgId;
					xml.append(orgId);
	
					// 主键节点结束标志
					xml.append("]]></");
					xml.append(xmlKey);
					xml.append(">");
					
					xml.append(xml2.toString());
	
					// 记录标志节点
					xml.append("<");
					xml.append(xmlFlag);
					xml.append("><![CDATA[");
					
					if ("secondnode".equalsIgnoreCase(fixParent)) {
						xml.append(fix);
					}
	
					// 记录标志值
					String flagId = rs.getString(dbFlag);
					flagId = flagId == null ? "" : flagId;
					xml.append(flagId);
	
					// 记录标志节点结束标志
					xml.append("]]></");
					xml.append(xmlFlag);
					xml.append(">");
	
					// 机构节点结束标志
					xml.append("</");
					xml.append(nodeName);
					xml.append(">");
	
					count++;
				}
				
				if (pojo.getMaxnum() != -1 && count >= pojo.getMaxnum()) {
					// 根节点结束标志
					xml.append("</");
					xml.append(rootName);
					xml.append(">");
					
					mess += sendMessage(type, pojo, sysId, xml.toString(), dao);

					count = 0;

					xml.delete(0, xml.length());
					xml.append(xmlTop);

					// 根节点
					xml.append("<");
					xml.append(rootName);
					xml.append(">");
					
					if ("root".equalsIgnoreCase(fixParent)) {
						xml.append(fix);
					}
				}

			}

			// 根节点结束标志
			xml.append("</");
			xml.append(rootName);
			xml.append(">");
			
			if (count > 0) {
                mess += sendMessage(type, pojo, sysId, xml.toString(), dao);
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return mess;
	}

	/**
	 * 发送xml信息
	 * 
	 * @param type
	 *            类型，A为人员，B为机构，K为岗位
	 * @param pojo
	 *            参数
	 * @param sysId
	 *            系统代号
	 * @param xml
	 *            内容
	 */
	private String sendMessage(String type, SyncParamPojo pojo, String sysId,
			String xml, ContentDAO dao) {
		String mess = null;
//		System.out.println(xml.toString());
		try {
			Service service = new Service();
			Call call = (Call) service.createCall();
			String url = "";
			String nameSpace = "";
			String methodName = "";
			String paramName = "";
			String userName = "";
			String password = "";
			String style = "";
			// 前缀
			String prefix = "";
			// 调用类型
			String soapType = "";
			
			String soapstr = "";

			if ("A".equalsIgnoreCase(type)) {
				url = pojo.getHr_webservice_url();
				nameSpace = pojo.getHr_webservice_namespace();
				methodName = pojo.getHr_webservice_function();
				paramName = pojo.getHr_webservice_paramname();
				userName = pojo.getHr_webservice_username();
				password = pojo.getHr_webservice_password();
				style = pojo.getHr_webservice_style();
				prefix = pojo.getHr_webservice_prefix();
				soapType = pojo.getHr_webservice_type();
				soapstr = pojo.getHr_webservice_soapstr();
			} else if ("B".equalsIgnoreCase(type)) {
				url = pojo.getOrg_webservice_url();
				nameSpace = pojo.getOrg_webservice_namespace();
				methodName = pojo.getOrg_webservice_function();
				paramName = pojo.getOrg_webservice_paramname();
				userName = pojo.getOrg_webservice_username();
				password = pojo.getOrg_webservice_password();
				style = pojo.getOrg_webservice_style();
				prefix = pojo.getOrg_webservice_prefix();
				soapType = pojo.getOrg_webservice_type();
				soapstr = pojo.getOrg_webservice_soapstr();
			} else if ("K".equalsIgnoreCase(type)) {
				url = pojo.getPost_webservice_url();
				nameSpace = pojo.getPost_webservice_namespace();
				methodName = pojo.getPost_webservice_function();
				paramName = pojo.getPost_webservice_paramname();
				userName = pojo.getPost_webservice_username();
				password = pojo.getPost_webservice_password();
				style = pojo.getPost_webservice_style();
				prefix = pojo.getPost_webservice_prefix();
				soapType = pojo.getPost_webservice_type();
				soapstr = pojo.getPost_webservice_soapstr();
			}

			if (userName != null && userName.trim().length() > 0) {
				call.getMessageContext().setUsername(userName);
				call.getMessageContext().setPassword(password);
			}

			if ("soap".equalsIgnoreCase(soapType)) {
								
				Map map = new HashMap();
				map.put("xmlns:" + prefix, nameSpace);				
				
				SOAPMessage response = invokeMethod("soapenv",map,prefix + ":" + methodName,paramName, xml ,url);
				//SOAPMessage response = WebserviceClientUtils.invokeMethod("soapenv",map,"gen:OAManager","input","<?xml version='1.0' encoding='UTF-8'?><input><key>dffd512f3c274ec11af53753fc82b483</key><cmd>getOrgInfoDetailByCode</cmd><orgcode>1</orgcode><domain>0</domain></input>","http://testoa.zhaopin.com/defaultroot/xfservices/GeneralWeb");
				//response.writeTo(System.out);
				if(null != response){
			    	SOAPBody responseBody = response.getSOAPBody();
			    	Node it = responseBody.getFirstChild().getFirstChild();
			    	mess = it.getTextContent();
				}
								
			} else if ("client".equalsIgnoreCase(soapType)){
				
				Object[] obj = null;
				try {
					Client client = new Client(new URL(url));
				//	String xml = strb.toString();
					obj = client.invoke(methodName, new Object[]{xml});
					mess = (String)obj[0];
					
//					System.out.println("返回值为="+obj[0]);
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if ("soapstr".equalsIgnoreCase(soapType)) {
				SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
				SOAPConnection con = factory.createConnection();
				SOAPMessage request = MessageFactory.newInstance().createMessage();

				SOAPBody body = request.getSOAPBody();
				
				SOAPPart part = request.getSOAPPart();
				
				
//				System.out.println("--" + soapstr);
				// 转化xml字符串
//				&lt; < 小于号 
//				&gt; > 大于号 
//				&amp; & 和 
//				&apos; ' 单引号 
//				&quot; " 双引号
				xml = xml.replaceAll("&", "&amp;");
				xml = xml.replaceAll("<", "&lt;");
				xml = xml.replaceAll(">", "&gt;");
				
				xml = xml.replaceAll("'", "&apos;");
				xml = xml.replaceAll("\"", "&quot;");
				
				soapstr = soapstr.replaceAll(prefix, xml);
				
				Source source = new StreamSource(new StringReader(soapstr));
				part.setContent(source);
				
//				request.writeTo(System.out);
				SOAPMessage response = con.call(request, url);

				SOAPBody responseBody = response.getSOAPBody();
//				response.writeTo(System.out);

				mess = response.getSOAPBody().getFirstChild().getFirstChild().getTextContent();
			}else 
			{
				call.setTargetEndpointAddress(new URL(url));
				call.setReturnType(XMLType.XSD_STRING);
				call.setUseSOAPAction(true);
				call.setOperationName(new QName(nameSpace, methodName));
				call.addParameter( paramName,
						XMLType.XSD_STRING, ParameterMode.IN);
				call.setSOAPActionURI(nameSpace + methodName);
//				rpc、document、default、message、wrapped
				if ("rpc".equalsIgnoreCase(style)) {
					call.setOperationStyle(Style.RPC);
				} else if ("document".equalsIgnoreCase(style)) {
					call.setOperationStyle(Style.DOCUMENT);
				} else if ("message".equalsIgnoreCase(style)) {
					call.setOperationStyle(Style.MESSAGE);
				} else if ("wrapped".equalsIgnoreCase(style)) {
					call.setOperationStyle(Style.WRAPPED);
				}  
				
			
			// paramValue = new
			// String(paramValue.getBytes(),"ISO-8859-1");//如果没有加这段，中文参数将会乱码
				mess = (String) call.invoke(new Object[] { xml });
			}
//			mess = "<?xml version = '1.0' encoding = 'utf-8'?><userinfo><info><hr_id>0000100002</hr_id><hr_flag>2</hr_flag><flag>0</flag></info><info><hr_id>0000043222</hr_id><hr_flag>1</hr_flag><flag>0</flag></info></userinfo>";
			// mess = new
			if (pojo.getBomEnd() != pojo.getBomStart()) {
				mess = mess.substring(0, pojo.getBomStart()) +
				mess.substring(pojo.getBomEnd(), mess.length());
			}
//			System.out.println(mess);
			mess = handlerResultMessage(type, mess, sysId, pojo, dao);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mess;
	}

	/**
	 * 
	 * @param preFix soap前缀
	 * @param atrrMap 属性集合
	 * @param methodName 方法名称
	 * @param paraName 参数名称
	 * @param paraValue 参数值
	 * @param url webservice地址
	 * @return
	 */
	public SOAPMessage invokeMethod(String preFix, Map atrrMap, String methodName, String paraName, String paraValue, String url) {
		SOAPConnection con = null;
		SOAPMessage response = null;
		
    	try {
    		
    		//Properties AxisProperties = System.getProperties();
	    	//AxisProperties.setProperty("http.proxyHost","192.168.2.213");
	        //AxisProperties.setProperty("http.proxyPort","3128");

	    	SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance(); 
	    	con = factory.createConnection(); 


	    	SOAPMessage request = MessageFactory.newInstance().createMessage(); 
	    	
	    	SOAPPart soapPart = request.getSOAPPart();

			SOAPEnvelope envelope = soapPart.getEnvelope();
			envelope.setPrefix(preFix);
			Iterator it  = atrrMap.entrySet().iterator();
			while (it.hasNext()) {
				 Map.Entry entry = (Map.Entry) it.next();
				envelope.setAttribute(entry.getKey().toString(), entry.getValue().toString());
			}

	    	SOAPBody body = request.getSOAPBody(); 
	    	// 方法
	    	SOAPElement getMessage = body.addChildElement(methodName); 
	    	getMessage.setEncodingStyle(SOAPConstants.URI_NS_SOAP_ENCODING); 
	    	// 第一个参数
	    	SOAPElement in0 = getMessage.addChildElement(paraName); 
	    	in0.addTextNode(paraValue);
	    	//request.writeTo(System.out); 
	    	response = con.call(request, url); 
	    	//response.writeTo(System.out); 
//	        	
//	    	SOAPBody responseBody = response.getSOAPBody();
//	    	Node it = responseBody.getFirstChild().getFirstChild();
//	    	responseStr.append(it.getTextContent());
	    	
    	} catch (Exception e) {
    		e.printStackTrace();
    	}catch(Error d){
    		System.out.println(d.getMessage());
    		System.out.println(d);
    		d.printStackTrace();
    	}
    	finally {
    		try {
    		con.close();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
		
		return response;
	}
	/**
	 * 处理消息
	 * 
	 * @param resultMessage
	 * @param sysId
	 */
	private String handlerResultMessage(String type, String resultMessage,
			String sysId, SyncParamPojo pojo, ContentDAO dao) {
		RowSet rs = null;
		if (resultMessage == null || resultMessage.length() <= 0) {
			return "";
		}
		
		StringBuffer mess = new StringBuffer();
		try {
			PareXmlUtils utils = new PareXmlUtils(resultMessage);
			// 路径
			String path = "";
			// 根节点
			String rootName = "";
			// 节点
			String nodeName = "";
			// 返回值id
			String id = "";
			// 返回值表示记录状态
			String hr_flag = "";
			// 返回值标志操作状态
			String flag = "";
			// 表名称
			String tableName = "";
			// 
			String dbKey = "";
			
			// 返回结果查询sql
			StringBuffer resultSQL = new StringBuffer();
			
			

			if ("A".equalsIgnoreCase(type)) {
				path = "/" + pojo.getHrReturnRootName() + "/"
						+ pojo.getHrReturnNodeName();
				rootName = pojo.getHrReturnRootName();
				nodeName = pojo.getHrReturnNodeName();
				id = pojo.getHrReturnKey();
				hr_flag = pojo.getHrReturnHrFlag();
				flag = pojo.getHrReturnFlag();
				tableName = "t_hr_view";
				dbKey = pojo.getHrDbKey();
				resultSQL.append("select a0101,");
			} else if ("B".equalsIgnoreCase(type)) {
				path = "/" + pojo.getOrgReturnRootName() + "/"
						+ pojo.getOrgReturnNodeName();
				rootName = pojo.getOrgReturnRootName();
				nodeName = pojo.getOrgReturnNodeName();
				id = pojo.getOrgReturnKey();
				hr_flag = pojo.getOrgReturnHrFlag();
				flag = pojo.getOrgReturnFlag();
				tableName = "t_org_view";
				dbKey = pojo.getOrgDbKey();
				resultSQL.append("select codeitemdesc,");
			} else if ("K".equalsIgnoreCase(type)) {
				path = "/" + pojo.getPostReturnRootName() + "/"
						+ pojo.getPostReturnNodeName();
				rootName = pojo.getPostReturnRootName();
				nodeName = pojo.getPostReturnNodeName();
				id = pojo.getPostReturnKey();
				hr_flag = pojo.getPostReturnHrFlag();
				flag = pojo.getPostReturnFlag();
				tableName = "t_Post_view";
				dbKey = pojo.getPostDbKey();
				resultSQL.append("select codeitemdesc,parentdesc,");
			}
			
			resultSQL.append(dbKey);
			resultSQL.append(" from ");
			resultSQL.append(tableName);
			resultSQL.append(" where ");
			resultSQL.append(dbKey);
			resultSQL.append(" in (");
			
			List list = utils.getNodes(path);

			// 更新记录的sql
			StringBuffer update = new StringBuffer();
			update.append("update ");
			update.append(tableName);
			update.append(" set ");
			update.append(sysId);
			update.append("=0 where ");
			update.append(sysId);
			update.append("=? and ");
			update.append(dbKey);
			update.append("=?");
			ArrayList dataList = new ArrayList();
			ArrayList dataList2 = new ArrayList();
			Map map = new HashMap();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					Element el = (Element) list.get(i);
					
					if (i == 0) {
						resultSQL.append("'");
						resultSQL.append(el.getChildText(id));
						resultSQL.append("'");						
					} else {
						resultSQL.append(",");
						resultSQL.append("'");
						resultSQL.append(el.getChildText(id));
						resultSQL.append("'");
					}
					ArrayList elList = new ArrayList();
					ArrayList elList2 = new ArrayList();
					
					String opFlag = el.getChildText(flag);
					if ("0".equals(opFlag)) {
						elList.add(el.getChildText(hr_flag));
						elList.add(el.getChildText(id));
						elList2.add(sysId);
						elList2.add(el.getChildText(id));
						dataList.add(elList);
						dataList2.add(elList2);
						map.put(el.getChildText(id), "成功");
						
					} else {
						erroCount++;
						map.put(el.getChildText(id), "失败");
					}
				}
			}
			
			if (list == null || list.size()==0) {
				resultSQL.append("''");
			}
			resultSQL.append(" )");
			dao.batchUpdate(update.toString(), dataList);
			
			
			if (pojo.isLog()) {
				update.delete(0, update.length());
				update.append("update " + tableName + "_log set flag=0,oldvalue=newvalue where sysid=? and unique_id in (select unique_id from "+tableName+" where "+dbKey+"=?)");
				dao.batchUpdate(update.toString(), dataList2);
			}
			
			rs = dao.search(resultSQL.toString());
			while (rs.next()) {
				mess.append(rs.getString(dbKey));
				mess.append("----");
				if ("A".equalsIgnoreCase(type)) {
					mess.append(rs.getString("a0101"));
				} else if ("B".equalsIgnoreCase(type)) {
					mess.append(rs.getString("codeitemdesc"));
				} else if ("K".equalsIgnoreCase(type)) {
					mess.append(rs.getString("parentdesc") + "/" + rs.getString("codeitemdesc"));
				}
				
				mess.append("----");
				mess.append(map.get(rs.getString(dbKey)));
				mess.append("\r\n");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return mess.toString();
	}

	/**
	 * 获取sql
	 * 
	 * @param type
	 *            类型，A表示人员；B表示机构；K表示岗位
	 * @param flag
	 *            标志，1代表新增，2代表更新，3代表删除
	 * @param pojo
	 *            参数
	 * @param sysId
	 *            系统代号
	 * @param cond
	 *            条件
	 * @return
	 */
	private String getSQL(String type, String flag, SyncParamPojo pojo,
			String sysId, String cond,String fixField) {
		StringBuffer sql = new StringBuffer();

		// 查询机构新增更新信息
		

		if ("A".equalsIgnoreCase(type)) {
			if(Sql_switcher.searchDbServer() == 1) {
				sql.append("select t.*,(select top 1 unique_id from t_org_view where b0110_0=t.e0122_0) e0122_unique_id,(select top 1 unique_id from t_post_view where e01a1_0=t.e01a1_0) e01a1_unique_id,(select top 1 unique_id from t_org_view where b0110_0=t.b0110_0) b0110_unique_id from ");
			} else {
				sql.append("select t.*,(select unique_id from t_org_view where b0110_0=t.e0122_0 and rownum=1) e0122_unique_id,(select unique_id from t_post_view where e01a1_0=t.e01a1_0 and rownum=1) e01a1_unique_id,(select unique_id from t_org_view where b0110_0=t.b0110_0 and rownum=1) b0110_unique_id from ");
			}
			sql.append("t_hr_view");
		} else if ("B".equalsIgnoreCase(type)) {
			if(Sql_switcher.searchDbServer() == 1) {
				sql.append("select t.*,(select top 1 unique_id from t_org_view where b0110_0=t.parentid) parent_unique_id from ");
			} else {
				sql.append("select t.*,(select unique_id from t_org_view where b0110_0=t.parentid and rownum=1) parent_unique_id from ");
			}
			sql.append("t_org_view");
		} else if ("K".equalsIgnoreCase(type)) {
			
			if(Sql_switcher.searchDbServer() == 1) {
				sql.append("select t.*,(select top 1 unique_id from t_org_view where b0110_0=t.e0122_0) parent_unique_id from ");
			} else {
				sql.append("select t.*,(select unique_id from t_org_view where b0110_0=t.e0122_0 and rownum=1) parent_unique_id from ");
			}
			sql.append("t_post_view");
		}

		sql.append(" t where ");

		if ("3".equals(flag)) {
			sql.append(sysId);
			sql.append("=3 ");
		} else {
			sql.append(sysId);
			sql.append("<>0 and ");
			sql.append(sysId);
			sql.append("<>3 ");
		}

		// 添加条件
		if (cond != null && cond.trim().length() > 0) {
			sql.append(" and (");
			sql.append(cond);
			sql.append(")");

		}

		// 添加条件
		if (("B".equalsIgnoreCase(type) && pojo.getOrgCond() != null && pojo.getOrgCond().trim().length() > 0) 
				|| ("A".equalsIgnoreCase(type) && pojo.getHrCond() != null && pojo.getHrCond().trim().length() > 0) 
				|| ("K".equalsIgnoreCase(type) && pojo.getPostCond()!= null && pojo.getPostCond().trim().length() > 0)) {
			sql.append(" and (");
			if ("A".equalsIgnoreCase(type)) {
				sql.append(pojo.getHrCond());
			} else if ("B".equalsIgnoreCase(type)) {
				sql.append(pojo.getOrgCond());
//				sql.append(" order by b0110_0");
//				if ("3".equals(flag)) {
//					sql.append(" desc ");
//				}
			} else if ("K".equalsIgnoreCase(type)) {
				sql.append(pojo.getPostCond());
//				sql.append(" order by e01a1_0");
//				
//				if ("3".equals(flag)) {
//					sql.append(" desc ");
//				}
			}
			sql.append(")");
		}
		
		if (! pojo.isLog()) {
			if ("B".equalsIgnoreCase(type)) {
				sql.append(" order by b0110_0");
				if ("3".equals(flag)) {
					sql.append(" desc ");
				}
			} else if ("K".equalsIgnoreCase(type)) {
				sql.append(" order by e01a1_0");
				
				if ("3".equals(flag)) {
					sql.append(" desc ");
				}
			}
		}
		
		if (pojo.isLog()) {
			String logTab = "";					
			Map map = null;	
			String dbFlag = "";
			String dbKey = "";

			if ("A".equalsIgnoreCase(type)) {
				logTab = "t_hr_view_log";
				map = pojo.getHrFieldRefMap();
				dbFlag = pojo.getHrDbFlag();
				dbKey = pojo.getHrDbKey();
			} else if ("B".equalsIgnoreCase(type)) {
				logTab = "t_org_view_log";
				map = pojo.getOrgFieldRefMap();
				dbFlag = pojo.getOrgDbFlag();
				dbKey = pojo.getOrgDbKey();
			} else if ("K".equalsIgnoreCase(type)) {
				logTab = "t_post_view_log";
				map = pojo.getPostFieldRefMap();
				dbFlag = pojo.getPostDbFlag();
				dbKey = pojo.getPostDbKey();
			}
			
			StringBuffer logSQL = new StringBuffer();
			
			logSQL.append("select kk.*,nn." + dbFlag);
			
			
			String fields[] = fixField.split(",");
			for (int i = 0; i < fields.length; i++) {
				if (fields[i] != null && fields[i].length() > 0 && !"unique_id".equalsIgnoreCase(fields[i])) {
					logSQL.append(",");
					logSQL.append("nn.");
					logSQL.append(fields[i]);
					logSQL.append(" fix_fix_");
					logSQL.append(fields[i]);
				}
			}
			
			if (!"unique_id".equalsIgnoreCase(dbKey)) {
				logSQL.append(",");
				logSQL.append("nn.");
				logSQL.append(dbKey);
			}
			
				logSQL.append(",");
				logSQL.append("nn.");
				logSQL.append(dbKey);
				logSQL.append(" fix_fix_");
				logSQL.append(dbKey);
			
			logSQL.append(" from (");
			logSQL.append(sql.toString());
			logSQL.append(") nn left join ");
			logSQL.append("( ");
			logSQL.append("select unique_id,");
			
			Iterator it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String []str = entry.getValue().toString().split(",");
				logSQL.append("max(" + str[0] + ") " + str[0]);
				logSQL.append(",max(" + str[1] + ") " + str[1]);
				if (it.hasNext()) {
					logSQL.append(",");
				}
			}
			
			logSQL.append(" from (");
			logSQL.append("		select unique_id, ");
			
			it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String []str = entry.getValue().toString().split(",");
				logSQL.append("case when fielditemid='" +entry.getKey().toString().toUpperCase()+ "' then oldvalue end "+str[0]+",");
				logSQL.append("		 case when fielditemid='" +entry.getKey().toString().toUpperCase()+ "' then newvalue end "+str[1]+"");
				if (it.hasNext()) {
					logSQL.append(",");
				}
			}
			
			
			
			logSQL.append(" from "+ logTab );
//			logSQL.append("	where flag=1 and sysid='"+sysId.toUpperCase()+"') mm group by unique_id ");
			logSQL.append("	where sysid='"+sysId.toUpperCase()+"') mm group by unique_id ");
			logSQL.append("		 ) kk ");
			
			logSQL.append(" on kk.unique_id=nn.unique_id where nn.unique_id is not null");
			
			return logSQL.toString();
		}
		

		return sql.toString();
	}

	/**
	 * 获得环境变量目录下的配置文件
	 * 
	 * @param sysId
	 * @return
	 */
	public File getFilePath(String fileName) {
		// 类路径，Ad.xml文件放到该路径下
		String classPath = System.getProperty("java.class.path");
		// 路径分割符号
		String sep = System.getProperty("path.separator");

		String[] path = classPath.split(sep);
		File file = null;
		for (int i = 0; i < path.length; i++) {
			
			
			
			file = new File(path[i], fileName);
			if (file.exists()) {
				break;
			}
		}

		return file;
	}
	
	
	public static void main (String[] args) {
	
			String mess = null;
			String xml = "<?xml version = '1.0' encoding = 'GB2312' ?>" + 
						"<userinfo><info><hr_id>0000100002</hr_id><name>Jan1</name><orgcode>Krohn</orgcode><cardnum>银行帐号</cardnum><subject>1221000001</subject><flag>2</flag></info>" +
                "<info><hr_id>43222</hr_id><name>Jan2</name><orgcode>Krohn</orgcode><cardnum>银行帐号</cardnum><subject>1221000002</subject><flag>1</flag></info></userinfo> ";
			try {
				Service service = new Service();
				Call call = (Call) service.createCall();
				String url = "http://zypdev.eppen.com.cn:8000/sap/bc/srt/rfc/sap/zhr_create_data/200/zhr_create_data/zhr_create_data";
				String nameSpace = "urn:sap-com:document:sap:soap:functions:mc-style";
				String methodName = "syncUserMessage";
				String paramName = "xml";
				String userName = "HR_wangzj";
				String password = "123456";

				if (userName != null && userName.trim().length() > 0) {
					call.getMessageContext().setUsername(userName);
					call.getMessageContext().setPassword(password);
				}

				call.setTargetEndpointAddress(new URL(url));
				call.setReturnType(XMLType.XSD_STRING);
				call.setUseSOAPAction(true);
				call.setOperationName(new QName(nameSpace, methodName));
				call.addParameter( paramName,
						XMLType.XSD_STRING, ParameterMode.IN);
				call.setSOAPActionURI(nameSpace + methodName);
				// paramValue = new
				// String(paramValue.getBytes(),"ISO-8859-1");//如果没有加这段，中文参数将会乱码
				mess = (String) call.invoke(new Object[] { xml });
				System.out.println(new String(mess.getBytes(),"utf-8").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
