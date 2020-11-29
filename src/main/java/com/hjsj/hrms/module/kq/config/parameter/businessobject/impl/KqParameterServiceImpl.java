package com.hjsj.hrms.module.kq.config.parameter.businessobject.impl;

import com.hjsj.hrms.module.kq.config.parameter.businessobject.KqParameterService;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**  
 * <p>Title: KqParameterServiceImpl</p>  
 * <p>Description: 考勤参数</p>  
 * <p>Company: hjsj</p>
 * @date 2018年11月16日 下午3:08:06
 * @author linbz  
 * @version 7.5
 */  
public class KqParameterServiceImpl implements KqParameterService {
    private UserView userView;
    private Connection conn;

    public KqParameterServiceImpl(UserView userView, Connection connection) {
        this.userView = userView;
        this.conn = connection;
    }
    /**
     * 获取库集合
     * listNbase
     * @return
     * @throws GeneralException
     * @date 2018年11月16日 下午3:07:56
     * @author linbz
     */
    @Override
    public ArrayList<HashMap<String, String>> listNbase() throws GeneralException {
    	
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        RowSet rs = null;
        try {
        	HashMap<String, String> nbaseMap = new HashMap<String, String>();
        	ContentDAO dao = new ContentDAO(this.conn);
            String sql = "SELECT * FROM  DBNAME ORDER BY dbid";
            rs = dao.search(sql);

            while (rs.next()) {
            	nbaseMap = new HashMap<String, String>();
                nbaseMap.put(rs.getString("DBNAME"), rs.getString("PRE"));
                list.add(nbaseMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }

        return list;
    }
    /**
     * 获取主集字符串集合
     * listA01Str
     * @param flag	=0工号指标备选；=1考勤部门备选
     * @return
     * @throws GeneralException
     * @date 2018年11月19日 下午3:16:37
     * @author linbz
     */
    @Override
    public ArrayList<HashMap<String, String>> listA01Str(String flag) throws GeneralException {
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		HashMap<String, String> map = new HashMap<String, String>();
    		map.put("dataValue", "");
			map.put("dataName", ResourceFactory.getProperty("label.select.dot"));
			list.add(map);
    		ArrayList<FieldItem> fielditemlist = DataDictionary.getFieldList("A01", Constant.USED_FIELD_SET);
    		boolean bool = false;
    		for(int i=0;i<fielditemlist.size();i++) {
    			bool = false;
    			FieldItem fi = fielditemlist.get(i);
    			String itemType = fi.getItemtype();
    			if("0".equals(flag)) {
    				if("A".equals(itemType) && "0".equals(fi.getCodesetid())) {
    					bool = true;
    				}
    			}else if("1".equals(flag)) {
    				if("A".equals(itemType)
    						&& "UM".equalsIgnoreCase(fi.getCodesetid()) && !"E0122".equalsIgnoreCase(fi.getItemid())) {
    					bool = true;
    				}
    			}else if("2".equals(flag)) {
    				if("D".equals(itemType)) {
    					bool = true;
    				}
    			}
    			
    			if(bool) {
    				map = new HashMap<String, String>();
    				String fielditemdesc = (String) fi.getItemid().toUpperCase() + ":" + (String) fi.getItemdesc();
    				map.put("dataValue", fi.getItemid());
    				map.put("dataName", fielditemdesc);
    				list.add(map);
    			}
    		}
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } 
    	return list;
    }
    /**
     * 保存参数
     * saveKqParameter
     * @param jsonObj	{"nbase":"Usr,Trs","g_no":"e0127","type":"save"}
     * @return
     * @throws GeneralException
     * @date 2018年11月19日 下午3:16:46
     * @author linbz
     */
    @Override
    public String saveKqParameter(JSONObject jsonObj) throws GeneralException {
	
		JSONObject obj = new JSONObject();
		String return_code = "success";
		String return_msg = "";
		try {
			String nbase = jsonObj.getString("nbase");
			// 工号
			String g_no = jsonObj.getString("g_no");
			// 考勤部门
			String kq_dept = jsonObj.getString("kq_dept");
			// 变动子集
			String setid = jsonObj.getString("setid");
			// 变动部门
			String dept_field = jsonObj.getString("dept_field");
			// 变动开始日期
			String start_field = jsonObj.getString("start_field");
			// 变动结束日期
			String end_field = jsonObj.getString("end_field");
			// 考勤开始日期
			String kq_start_date = jsonObj.getString("kq_start_date");
			// 考勤结束日期
			String kq_end_date = jsonObj.getString("kq_end_date");
        	// 卡号
    		String card_no = jsonObj.getString("card_no");
    		// 请假子集
    		JSONObject leave_subset = jsonObj.getJSONObject("leave_subset");
    		// 公出子集
    		JSONObject officeleave_subset = jsonObj.getJSONObject("officeleave_subset");
    		// 加班子集
    		JSONObject overtime_subset = jsonObj.getJSONObject("overtime_subset");
    		// 数据上报
    		JSONObject report_daily_apply = jsonObj.getJSONObject("report_daily_apply");
			
			//原有参数，取缓存数据
			RecordVo paramsVo=ConstantParamter.getConstantVo("KAOQIN_PARAM");
			String param = "";
			// 有缓存则取缓存数据，没有则取默认参数
			if(null != paramsVo)
				param = paramsVo.getString("str_value");
			//构建Document对象
			Document doc = PubFunc.generateDom(param);
			//获得root节点
			Element root = doc.getRootElement();
			// 考勤人员库
			Element nbaseNode = root.getChild("nbase");
			// 考勤卡号指标
			Element card_noNode = root.getChild("card_no");
			// 考勤工号指标
			Element g_noNode = root.getChild("g_no");
			// 考勤部门指标
			Element kq_deptNode = root.getChild("kq_dept");
			// 考勤部门指标
			Element kq_dept_changeNode = root.getChild("kq_dept_change");
			// 考勤开始日期
			Element kq_start_dateNode = root.getChild("kq_start_date");
			// 考勤结束日期
			Element kq_end_dateNode = root.getChild("kq_end_date");
			// 请假子集
			Element leave_subsetNode = root.getChild("leave_subset");
			// 公出子集
			Element officeleave_subsetNode = root.getChild("officeleave_subset");
			// 加班子集
			Element overtime_subsetNode = root.getChild("overtime_subset");
			// 数据上报
			Element report_daily_applyNode = root.getChild("report_daily_apply");
			
			// 填报人员库
			if(nbaseNode!=null){
				nbaseNode.setText(nbase);
			}else{
				Element nbaseNodeE = new Element("nbase");
				nbaseNodeE.setText(nbase);
				root.addContent(nbaseNodeE);
			}
			// 工号
			if(g_noNode!=null){
				g_noNode.setText(g_no);
			}else{
				Element g_noNodeE = new Element("g_no");
				g_noNodeE.setText(g_no);
				root.addContent(g_noNodeE);
			}
			// 考勤部门
			if(kq_deptNode!=null){
				kq_deptNode.setText(kq_dept);
			}else{
				Element kq_deptNodeE = new Element("kq_dept");
				kq_deptNodeE.setText(kq_dept);
				root.addContent(kq_deptNodeE);
			}
			// 考勤部门变动（轮岗）信息
			if(kq_dept_changeNode!=null){
				kq_dept_changeNode.setAttribute("setid", setid);
				kq_dept_changeNode.setAttribute("dept_field", dept_field);
				kq_dept_changeNode.setAttribute("start_field", start_field);
				kq_dept_changeNode.setAttribute("end_field", end_field);
			}else{
				Element kq_dept_changeNodeE = new Element("kq_dept_change");
				kq_dept_changeNodeE.setAttribute("setid", setid);
				kq_dept_changeNodeE.setAttribute("dept_field", dept_field);
				kq_dept_changeNodeE.setAttribute("start_field", start_field);
				kq_dept_changeNodeE.setAttribute("end_field", end_field);
				root.addContent(kq_dept_changeNodeE);
			}
			// 考勤开始日期
			if(kq_start_dateNode!=null){
				kq_start_dateNode.setText(kq_start_date);
			}else{
				Element kq_start_dateNodeE = new Element("kq_start_date");
				kq_start_dateNodeE.setText(kq_start_date);
				root.addContent(kq_start_dateNodeE);
			}
			// 考勤结束日期
			if(kq_end_dateNode!=null){
				kq_end_dateNode.setText(kq_end_date);
			}else{
				Element kq_end_dateNodeE = new Element("kq_end_date");
				kq_end_dateNodeE.setText(kq_end_date);
				root.addContent(kq_end_dateNodeE);
			}
			// 卡号
			if(card_noNode!=null){
				card_noNode.setText(card_no);
			}else{
				Element card_noNodeE = new Element("card_no");
				card_noNodeE.setText(card_no);
				root.addContent(card_noNodeE);
			}
			// 请假子集
			String leave_setid = leave_subset.getString("setid");
			String leave_type = leave_subset.getString("type");
			String leave_start = leave_subset.getString("start");
			String leave_end = leave_subset.getString("end");
			String leave_reason = leave_subset.getString("reason");
			if(leave_subsetNode!=null){
				leave_subsetNode.setAttribute("setid", leave_setid);
				leave_subsetNode.setAttribute("type", leave_type);
				leave_subsetNode.setAttribute("start", leave_start);
				leave_subsetNode.setAttribute("end", leave_end);
				leave_subsetNode.setAttribute("reason", leave_reason);
			}else{
				Element leave_subsetNodeE = new Element("leave_subset");
				leave_subsetNodeE.setAttribute("setid", leave_setid);
				leave_subsetNodeE.setAttribute("type", leave_type);
				leave_subsetNodeE.setAttribute("start", leave_start);
				leave_subsetNodeE.setAttribute("end", leave_end);
				leave_subsetNodeE.setAttribute("reason", leave_reason);
				root.addContent(leave_subsetNodeE);
			}
			// 公出子集
			String officeleave_setid = officeleave_subset.getString("setid");
			String officeleave_type = officeleave_subset.getString("type");
			String officeleave_start = officeleave_subset.getString("start");
			String officeleave_end = officeleave_subset.getString("end");
			String officeleave_reason = officeleave_subset.getString("reason");
			if(officeleave_subsetNode!=null){
				officeleave_subsetNode.setAttribute("setid", officeleave_setid);
				officeleave_subsetNode.setAttribute("type", officeleave_type);
				officeleave_subsetNode.setAttribute("start", officeleave_start);
				officeleave_subsetNode.setAttribute("end", officeleave_end);
				officeleave_subsetNode.setAttribute("reason", officeleave_reason);
			}else{
				Element officeleave_subsetNodeE = new Element("officeleave_subset");
				officeleave_subsetNodeE.setAttribute("setid", officeleave_setid);
				officeleave_subsetNodeE.setAttribute("type", officeleave_type);
				officeleave_subsetNodeE.setAttribute("start", officeleave_start);
				officeleave_subsetNodeE.setAttribute("end", officeleave_end);
				officeleave_subsetNodeE.setAttribute("reason", officeleave_reason);
				root.addContent(officeleave_subsetNodeE);
			}
			// 加班子集
			String overtime_setid = overtime_subset.getString("setid");
			String overtime_type = overtime_subset.getString("type");
			String overtime_start = overtime_subset.getString("start");
			String overtime_end = overtime_subset.getString("end");
			String overtime_reason = overtime_subset.getString("reason");
			if(overtime_subsetNode!=null){
				overtime_subsetNode.setAttribute("setid", overtime_setid);
				overtime_subsetNode.setAttribute("type", overtime_type);
				overtime_subsetNode.setAttribute("start", overtime_start);
				overtime_subsetNode.setAttribute("end", overtime_end);
				overtime_subsetNode.setAttribute("reason", overtime_reason);
			}else{
				Element overtime_subsetNodeE = new Element("overtime_subset");
				overtime_subsetNodeE.setAttribute("setid", overtime_setid);
				overtime_subsetNodeE.setAttribute("type", overtime_type);
				overtime_subsetNodeE.setAttribute("start", overtime_start);
				overtime_subsetNodeE.setAttribute("end", overtime_end);
				overtime_subsetNodeE.setAttribute("reason", overtime_reason);
				root.addContent(overtime_subsetNodeE);
			}
			// 数据上报
			String enable_modify = report_daily_apply.getString("enable_modify");
			String approval_message = report_daily_apply.getString("approval_message");
			if(report_daily_applyNode!=null){
				report_daily_applyNode.setAttribute("enable_modify", enable_modify);
				report_daily_applyNode.setAttribute("approval_message", approval_message);
			}else{
				Element report_daily_applyNodeE = new Element("report_daily_apply");
				report_daily_applyNodeE.setAttribute("enable_modify", enable_modify);
				report_daily_applyNodeE.setAttribute("approval_message", approval_message);
				root.addContent(report_daily_applyNodeE);
			}
			
			// 设置xml字体编码，然后输出为字符串
			Format format = Format.getRawFormat();
			format.setEncoding("UTF-8");
			XMLOutputter output = new XMLOutputter(format);
			String xml = output.outputString(doc);
			// 更新数据库
			this.updateRecord(xml);
			
		} catch (Exception e) {
			return_code = "fail";
			return_msg = e.getMessage();
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			obj.put("return_code", return_code);
			obj.put("return_msg", return_msg);
		}
		
		return obj.toString();
    }
    /**
     * 获取考勤参数
     * getKqParameter
     * @return
     * @throws GeneralException
     * @date 2018年11月17日 下午6:04:48
     * @author linbz
     */
    @Override
    public HashMap getKqParameter() throws GeneralException{
    	
    	HashMap map = new HashMap();
    	try {
			map = KqPrivForHospitalUtil.getKqParameter(conn);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    	return map;
    }
    /**
     * 更新数据库考勤参数
     * updateRecord
     * @param xml
     * @throws GeneralException
     * @date 2018年11月17日 下午6:06:33
     * @author linbz
     */
    private void updateRecord(String xml) throws GeneralException {
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "update constant set Str_Value=? where Constant=?";
			ArrayList values = new ArrayList();
			values.add(xml);
			values.add("KAOQIN_PARAM");
			int result = dao.update(sql, values);
			if(result !=1 ) {
				sql = "insert into constant (Str_Value, Constant) values (?,?)";
				dao.insert(sql.toString(), values);
			}
			//更新成功，同步到内存
			this.updateConstantCache(xml);
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
    /**
     * 更新考勤参数缓存
     * updateConstantCache
     * @param xml
     * @throws GeneralException
     * @date 2018年11月17日 下午6:07:00
     * @author linbz
     */
    private void updateConstantCache(String xml) throws GeneralException {
		try{
			RecordVo vo = new RecordVo("Constant");
			vo.setString("constant", "KAOQIN_PARAM");
			vo.setString("describe", "考勤参数");
			vo.setString("str_value", xml);
			vo.setString("type", "");
			ConstantParamter.putConstantVo(vo, "KAOQIN_PARAM");
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
    /**
     * 获取人员子集 集合
     * listFieldSet
     * @param flag
     * @return
     * @throws GeneralException
     * @date 2019年2月20日 上午11:57:39
     * @author linbz
     */
    @Override
    public ArrayList<HashMap<String, String>> listFieldSet(String flag) throws GeneralException {
        ArrayList<HashMap<String, String>> fieldSubsetlist = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("dataValue", "");
        // "请选择..."
        map.put("dataName", ResourceFactory.getProperty("label.select.dot"));
        ArrayList<FieldSet> fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
	    fieldSubsetlist.add(0, map);
	    for (int i = 0; i < fieldsetlist.size(); i++) {
	        FieldSet fieldset = fieldsetlist.get(i);
	        // 未构库指标不能出现在页面
	        if (!"1".equalsIgnoreCase(fieldset.getUseflag())) 
	            continue;
	        String fieldsetid = (String) fieldset.getFieldsetid();
	        // 系统项 多媒体子集/主集  过滤掉
	        if ("A00".equalsIgnoreCase(fieldsetid) || "A01".equalsIgnoreCase(fieldsetid)) 
	        	continue;
	        String fieldsetdesc = fieldsetid.toUpperCase() +":" + (String) fieldset.getCustomdesc();
	        map = new HashMap<String, String>();
	        map.put("dataValue", fieldsetid);
	        map.put("dataName", fieldsetdesc);
	        fieldSubsetlist.add(map);
	    }
		return fieldSubsetlist;
    }
    /**
     * 获取子集指标集合
     * listChangeFieldItemid
     * @param setid 子集
     * @param flag	=0子集部门指标；=1日期型指标；=2字符性指标；=3代码型指标；=4字符与备注型指标
     * @return
     * @throws GeneralException
     * @date 2019年2月20日 上午11:41:37
     * @author linbz
     */
    @Override
    public ArrayList<HashMap<String, String>> listFieldItemid(String setid, String flag) throws GeneralException {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("dataValue", "");
        // "请选择..."
        map.put("dataName", ResourceFactory.getProperty("label.select.dot"));
        list.add(0, map);
        if(StringUtils.isBlank(setid))
        	return list;
        
        ArrayList<FieldItem> fielditemlist = DataDictionary.getFieldList(setid, Constant.USED_FIELD_SET);
		boolean bool = false;
		for(int i=0;i<fielditemlist.size();i++) {
			bool = false;
			FieldItem fi = fielditemlist.get(i);
			if("0".equals(flag)) {
				if("A".equals(fi.getItemtype()) && "1".equals(fi.getUseflag())
						&& "UM".equalsIgnoreCase(fi.getCodesetid()) && !"E0122".equalsIgnoreCase(fi.getItemid())) {
					bool = true;
				}
			}else if("1".equals(flag)) {
				if("D".equals(fi.getItemtype()) && "1".equals(fi.getUseflag())) {
					bool = true;
				}
			}else if("2".equals(flag)) {
				if("A".equals(fi.getItemtype()) && "0".equals(fi.getCodesetid())) {
					bool = true;
				}
			}else if("3".equals(flag)) {
				if("A".equals(fi.getItemtype()) && !"0".equals(fi.getCodesetid())) {
					bool = true;
				}
			}else if("4".equals(flag)) {
				if("M".equals(fi.getItemtype()) || ("A".equals(fi.getItemtype()) && "0".equals(fi.getCodesetid()))) {
					bool = true;
				}
			}
			
			if(bool) {
				String itemId = (String) fi.getItemid();
				map = new HashMap<String, String>();
				String fielditemdesc = itemId.toUpperCase() + ":" + (String) fi.getItemdesc();
				map.put("dataValue", itemId);
				map.put("dataName", fielditemdesc);
				list.add(map);
			}
		}
        return list;
    }
    /**
     * 获取考勤开始结束 日期型指标下拉数据
     * listDateFieldItemid
     * @param flag
     * @return
     * @throws GeneralException
     * @date 2019年6月24日 下午6:46:24
     * @author linbz
     */
    @Override
    public ArrayList<HashMap<String, String>> listDateFieldItemid(String flag) throws GeneralException {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        RowSet rs = null;
        try {
        	HashMap<String, String> map = new HashMap<String, String>();
        	map.put("dataValue", "");
        	// "请选择..."
        	map.put("dataName", ResourceFactory.getProperty("label.select.dot"));
        	list.add(0, map);
        	
        	StringBuffer sql = new StringBuffer();
        	sql.append("SELECT A.fieldsetid,displayorder,displayid,itemid,itemdesc,itemtype");
        	sql.append(" FROM fielditem A left join fieldset B");
        	sql.append(" ON A.fieldsetid=B.fieldsetid");
        	sql.append(" WHERE A.itemtype='D'");
        	sql.append(" AND A.useflag='1'");
        	sql.append(" AND B.fieldsetid like 'A%'");
        	sql.append(" AND (changeFlag=0 OR (A.itemid NOT LIKE '%Z0' AND changeFlag<>0))");
        	sql.append(" ORDER BY displayorder,displayid");
        	
        	ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            while (rs.next()) {
            	String itemId = rs.getString("itemid").toUpperCase();
                map = new HashMap<String, String>();
				String fielditemdesc = rs.getString("fieldsetid").toUpperCase()  + ":" +itemId+ ":" +rs.getString("itemdesc");
				map.put("dataValue", itemId);
				map.put("dataName", fielditemdesc);
				list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return list;
    }
}
