package com.hjsj.hrms.module.system.portal.jobtitle.businessobject;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.module.qualifications.businessobject.QuanlificationsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 首页--职称评审
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 */
public class JobtitlePortalBo {
	
	// 基本属性
	private Connection conn = null;
	private UserView userview;
	
	/**
	 * 构造函数
	 * @param conn
	 * @param userview
	 */
	public JobtitlePortalBo(Connection conn, UserView userview){
		this.conn = conn;
		this.userview = userview;
	}
	
	
	public ArrayList<HashMap<String, String>> getInfoList() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		
		HashMap configMap = new HashMap();
		configMap = this.getConfig();
		
		ArrayList<HashMap<String, String>> num1list = this.getDetailInfoList("num1");
		if(num1list.size() > 0/*this.userview.hasTheFunction("38001")*/) {//评审条件,不需要授权，直接显示 chent 20160615
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("id", "num1");
			map.put("name", "申报条件");//haosl 20170425 update 北理工
			map.put("src", "/images/new_module/pingshentiaojian.png");
			list.add(map);
			
		}
		if(StringUtils.isNotEmpty((String)configMap.get("8")) && this.userview.isHaveResource(IResourceConstant.RSBD,(String)configMap.get("8")) && StringUtils.isNotEmpty(this.userview.getA0100())) {//预报名，A0100为空时不显示（业务用户没有关联自助用户的情况）
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("id", "num2");
			map.put("name", "预报名");
			map.put("src", "/images/new_module/baomingshenqing.png");
			list.add(map);
			
		}
		//
		if(StringUtils.isNotEmpty((String)configMap.get("5")) && this.userview.isHaveResource(IResourceConstant.RSBD,(String)configMap.get("5")) && StringUtils.isNotEmpty(this.userview.getA0100())) {//送审论文
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("id", "num3");
			map.put("name", "送审论文");
			map.put("src", "/images/new_module/songshenlunwen.png");
			list.add(map);
			
		}
		
		boolean flg = false;
		String config = (String)configMap.get("6");// 配置的模板号，可能是多个
		if(StringUtils.isNotEmpty(config)){//材料审查
			if(config.indexOf(",") > -1){
				String[] array = config.split(",");
				for(int i=0; i<array.length; i++){
					String value = array[i];//模板号
					if(this.userview.isHaveResource(IResourceConstant.RSBD,value)){
						flg = true;
					}
				}
			}else {
				if(this.userview.isHaveResource(IResourceConstant.RSBD, config)){
					flg = true;
				}
			}
		}
		if(StringUtils.isNotEmpty((String)configMap.get("6")) && StringUtils.isNotEmpty(this.userview.getA0100()) && flg) {//专业技术职务申报
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("id", "num4");
			//map.put("name", "专业技术职务申报");
			map.put("name", "职称申报入口");//haosl 20170425 update 北理工
			map.put("src", "/images/new_module/shenbaocailiao.png");
			list.add(map);
		}
		return list;
	}
	public ArrayList<HashMap<String, String>> getDetailInfoList(String id){
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			//!this.userView.isSuper_admin()&&!this.userView.isHaveResource(IResourceConstant, tabID)
			
			// 【预报名】【送审论文】【申报材料】时，需要获取配置信息
			HashMap configMap = new HashMap();
			if("num2".equalsIgnoreCase(id) || "num3".equalsIgnoreCase(id) || "num4".equalsIgnoreCase(id)){
				try{
					ConstantXml constantXml = new ConstantXml(this.conn, "JOBTITLE_CONFIG","params");
					List listEl = constantXml.getAllChildren("//templates");
					Element template = null;
					for (int j = 0; j < listEl.size(); j++) {   
						template = (Element) listEl.get(j); //循环依次得到子节点
						configMap.put(template.getAttributeValue("type"), template.getAttributeValue("template_id"));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			String index = "";
			if("num1".equalsIgnoreCase(id)){// 评审条件
				list = this.getCondition();
				
			} else if("num2".equalsIgnoreCase(id)) {// 预报名
				index = "8";
				
			} else if("num3".equalsIgnoreCase(id)) {// 送审论文
				index = "5";
				
			} else if("num4".equalsIgnoreCase(id)) {// 申报材料,可能多个
				index = "6";
			}
			String config = (String)configMap.get(index);// 配置的模板号，可能是多个
			if(StringUtils.isNotEmpty(config)){//材料审查
				if(config.indexOf(",") > -1){
					String[] array = config.split(",");
					for(int i=0; i<array.length; i++){
						String value = array[i];//模板号
						if(this.userview.isHaveResource(IResourceConstant.RSBD,value)){//只加有权限的
							ReviewFileBo reviewFileBo = new ReviewFileBo(this.conn, this.userview);
							String name = reviewFileBo.getTabNameByTabId(value);// 模板名
							
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("id", value);
							map.put("name", name);
							map.put("src", "/images/new_module/tubiao.png");
							list.add(map);
						}
					}
				} else {
					HashMap<String, String> map = new HashMap<String, String>();
					ReviewFileBo reviewFileBo = new ReviewFileBo(this.conn, this.userview);
					String name = reviewFileBo.getTabNameByTabId(config);// 模板名
					map.put("id", config);
					map.put("name", name);
					map.put("src", "/images/new_module/tubiao.png");
					list.add(map);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return list;
	}
	public ArrayList<HashMap<String, String>> getCondition(){
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			// 获取评审条件
			DbWizard dbWizard = new DbWizard(this.conn);
	     	if (dbWizard.isExistTable("zc_condition", false)){
	     		QuanlificationsBo quanlificationsBo = new QuanlificationsBo(this.conn,this.userview);
	     		String wheresql = quanlificationsBo.getWhereSql("9");
	     		StringBuffer sql = new StringBuffer("select * from zc_condition ");
	    		sql.append(wheresql);
	    		sql.append(" and "+ Sql_switcher.isnull("module_type", "1") +"=1 ");
	    		sql.append(" order by condition_id");
	     		rs=dao.search(sql.toString());
	     		while(rs.next()){
	     			HashMap<String, String> map = new HashMap<String, String>();
	     			String conid = PubFunc.encrypt(rs.getString("condition_id"));
	     			map.put("id", conid);
	     			map.put("name", rs.getString("zc_series"));
	     			map.put("src", "/images/new_module/tubiao.png");
	     			map.put("url", "/module/jobtitle/qualifications/Qualifications.html?b_query=link&id="+conid);
	     			list.add(map);
	     		}
	     	}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return list;
	}
	/**
	 * 通过归档子集、找到通过状态和申报职称
	 * @param fieldset：归档子集
	 * @param committee_result：通过状态
	 * @param Apply_post：申报材料
	 * @return
	 */
	public HashMap<String, String> getResultsArchivingInfo(String fieldset, String resultFieldItem, String applyFieldItem, String startDateFieldItem, String meetingNameFieldItem){
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			
			String a0100 = this.userview.getA0100();
			String nbase = this.userview.getDbname();
			String tableName = nbase+fieldset;
			
			// 判断没有子集表、子集表中有没有相应字段，直接算没取到
			DbWizard dbWizard = new DbWizard(this.conn);
			Table table = new Table(tableName);
			if (!dbWizard.isExistTable(tableName, false) 
					|| !dbWizard.isExistField(tableName, resultFieldItem, false) 
					|| !dbWizard.isExistField(tableName, applyFieldItem, false) 
					|| !dbWizard.isExistField(tableName, startDateFieldItem, false) 
					|| !dbWizard.isExistField(tableName, meetingNameFieldItem, false)
					|| StringUtils.isEmpty(tableName)
					|| StringUtils.isEmpty(resultFieldItem)
					|| StringUtils.isEmpty(applyFieldItem)
					|| StringUtils.isEmpty(startDateFieldItem)
					|| StringUtils.isEmpty(meetingNameFieldItem)) {
				return map;
			}
	    		
	     	// 获取登录人归档的数据
			StringBuilder sql = new StringBuilder();
			sql.append("select I9999,"+resultFieldItem+" as result, "+applyFieldItem+" as apply,"+startDateFieldItem+" as starttime,"+meetingNameFieldItem+" as meetingname");
			sql.append(" from "+tableName);
			sql.append(" where A0100='"+a0100+"' order by I9999 desc");
			// 获取评审条件
			rs=dao.search(sql.toString());
			
			int i9999Bak = -1;
			String apply_code_bak = "-1";
			int apply_num = 1;//申请次数
			String _meetingname = "";
			while(rs.next()){
				int I9999 = rs.getInt("I9999"); 
				String result_code = rs.getString("result"); 
				String apply_code = rs.getString("apply"); 
				
				Date date = rs.getDate("starttime");
				int year = 0; 
				if(date != null){
					year = date.getYear()+1900;
				}
				String meetingname = rs.getString("meetingname"); 

				String result_name = "";//状态名
				if(DataDictionary.getFieldItem(resultFieldItem, fieldset) != null){
					String codeSetid = DataDictionary.getFieldItem(resultFieldItem, fieldset).getCodesetid();
					result_name = AdminCode.getCodeName(codeSetid, result_code);
				}
				
				String apply_name = "";//申报的岗位名
				if(DataDictionary.getFieldItem(applyFieldItem, fieldset) != null){
					String codeSetid = DataDictionary.getFieldItem(applyFieldItem, fieldset).getCodesetid();
					apply_name = AdminCode.getCodeName(codeSetid, apply_code);
				}

				if(i9999Bak == -1){
					String yearText = "";
					if(year != 0){
						yearText = "于"+year+"年";
					}
					map.put("text", apply_name+"职称申报"+yearText+result_name+",共申报{num}次");
					map.put("num", String.valueOf(apply_num));
					_meetingname = meetingname;
					
				}else{//不是第一条时，继续判断 
					if(I9999 == (i9999Bak-1) && !StringUtils.isEmpty(apply_code_bak) && apply_code_bak.equalsIgnoreCase(apply_code)){//计算申请数量：如果是连续数据、并且申请的职位相同
						apply_num += 1;
						map.put("num", String.valueOf(apply_num));
						_meetingname = meetingname;
					}
				}
				apply_code_bak = apply_code;
				i9999Bak = I9999;
				
				
			}
			//获取送审材料的链接
			sql.setLength(0);
			sql.append("select w0537 from w05 ");
			sql.append("where w0301 in( ");
			sql.append("select w0301 from w03 where w0303='"+_meetingname+"' ");
			sql.append(") and w0503='"+nbase+"' and w0505= '"+a0100+"'");
			sql.append(" order by w0501 desc");
			rs=dao.search(sql.toString());
			if(rs.next()){
				String w0537 = rs.getString("w0537");//送审论文材料访问地址
				map.put("url", w0537);
				map.put("nbasea0100", PubFunc.encrypt(nbase+a0100));
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return map;
	}
	/**
	 * 获取归档配置
	 * @return
	 */
	private HashMap getConfig(){
		// 【预报名】【送审论文】【申报材料】时，需要获取配置信息
		HashMap configMap = new HashMap();
		try{
			ConstantXml constantXml = new ConstantXml(this.conn, "JOBTITLE_CONFIG","params");
			List listEl = constantXml.getAllChildren("//templates");
			Element template = null;
			for (int j = 0; j < listEl.size(); j++) {   
				template = (Element) listEl.get(j); //循环依次得到子节点
				configMap.put(template.getAttributeValue("type"), template.getAttributeValue("template_id"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return configMap;
	}
}
