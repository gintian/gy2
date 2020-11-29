package com.hjsj.hrms.module.kq.kqdata.businessobject.impl;

import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataArchiveService;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KqDataArchiveServiceImpl implements KqDataArchiveService{

	private UserView userView;
	private Connection conn;
	
	public KqDataArchiveServiceImpl(UserView userView, Connection conn){
		this.userView = userView;
		this.conn = conn;
	}
	
	@Override
    public HashMap getKqDataArchive() throws GeneralException {
		Document doc = getKqParameter();
		Element rootEl = null;
		Element archiveEl = null;
		List itemList = null;
		String fieldset = null;
		if(doc != null){
			rootEl = doc.getRootElement();
			archiveEl = rootEl.getChild("archive_scheme");
			if(archiveEl == null){
				itemList = new ArrayList();
			}else{
				fieldset = archiveEl.getAttributeValue("fieldset");
				itemList = archiveEl.getChildren("item");
			}
		}
		StringBuffer messages = new StringBuffer();
		ArrayList fieldItemList = new ArrayList();
		ArrayList mappingList = new ArrayList();
		
		ArrayList fieldList = DataDictionary.getFieldList("Q35", Constant.USED_FIELD_SET);
		String filterField=",a0000,b0110,e0122,scheme_id,e01a1,a0100,guidkey,a0101,confirm,only_field,";
		for(int i =0 ; i < fieldList.size() ; i++){
			HashMap fieldItemMap = new HashMap();
			FieldItem fieldItem = (FieldItem)fieldList.get(i);
			String itemid = fieldItem.getItemid();
			if(filterField.indexOf(","+itemid.toLowerCase()+",")!=-1)
				continue;

			for(int j = 0 ; j < itemList.size() ; j++){
				Element itemEl = (Element) itemList.get(j);
				String sourcefield = itemEl.getAttributeValue("sourcefield");
				if(sourcefield.equalsIgnoreCase(itemid)){
					HashMap mappingMap = new HashMap();
					String fielditemid = itemEl.getAttributeValue("fielditem");
					// 53832 校验指标是否符合要求
					if(!isCheckFieldItemid(fielditemid, fieldset)) {
						messages.append("," + fieldItem.getItemdesc());
						break;
					}
					mappingMap = this.getFieldMap(sourcefield, DataDictionary.getFieldItem(fielditemid).getItemdesc(), "", "");
					mappingMap.put("to_item_id",fielditemid);
					mappingList.add(mappingMap);
					break;
				}
			}
			fieldItemMap = this.getFieldMap(itemid, fieldItem.getItemdesc(), fieldItem.getItemtype(), fieldItem.getCodesetid());
			fieldItemList.add(fieldItemMap);
		}
		// 机构考勤员
		String lower_clerkDesc = ResourceFactory.getProperty("label.select.org") + ResourceFactory.getProperty("kq.data.sp.clerk");
		// 机构审核人
		String lower_reviewerDesc = ResourceFactory.getProperty("label.select.org") + ResourceFactory.getProperty("button.audit") 
					+ ResourceFactory.getProperty("sys.import.men");
		// 方案考勤员
		String upper_clerkDesc = ResourceFactory.getProperty("report.project") + ResourceFactory.getProperty("kq.data.sp.clerk");
		// 方案审核人
		String upper_reviewerDesc = ResourceFactory.getProperty("report.project") + ResourceFactory.getProperty("button.audit") 
					+ ResourceFactory.getProperty("sys.import.men");
		
		for(int j = 0 ; j < itemList.size() ; j++){
			Element itemEl = (Element) itemList.get(j);
			String sourcefield = itemEl.getAttributeValue("sourcefield");
			HashMap mappingMap = new HashMap();
			String fielditemid = itemEl.getAttributeValue("fielditem");
			// 53832 校验指标是否符合要求
			if(!isCheckFieldItemid(fielditemid, fieldset))
				continue;
			
			if("lower_clerk".equalsIgnoreCase(sourcefield)) {
				mappingMap = this.getFieldMap("lower_clerk", lower_clerkDesc, "", "");
				mappingMap.put("to_item_id", fielditemid);
				mappingList.add(mappingMap);
			}else if("lower_reviewer".equalsIgnoreCase(sourcefield)) {
				mappingMap = this.getFieldMap("lower_reviewer", lower_reviewerDesc, "", "");
				mappingMap.put("to_item_id", fielditemid);
				mappingList.add(mappingMap);
			}else if("upper_clerk".equalsIgnoreCase(sourcefield)) {
				mappingMap = this.getFieldMap("upper_clerk", upper_clerkDesc, "", "");
				mappingMap.put("to_item_id", fielditemid);
				mappingList.add(mappingMap);
			}else if("upper_reviewer".equalsIgnoreCase(sourcefield)) {
				mappingMap = this.getFieldMap("upper_reviewer", upper_reviewerDesc, "", "");
				mappingMap.put("to_item_id", fielditemid);
				mappingList.add(mappingMap);
			}
		}
		/**
		 * 增加 机构考勤员  机构审核人 人事处考勤员  人事处审核人  
		 * 以适用于 轮岗情况 归档用
		 */
		// 机构考勤员
		fieldItemList.add(this.getFieldMap("lower_clerk", lower_clerkDesc, "A", "0"));
		// 机构审核人
		fieldItemList.add(this.getFieldMap("lower_reviewer", lower_reviewerDesc, "A", "0"));
		// 人事处考勤员
		fieldItemList.add(this.getFieldMap("upper_clerk", upper_clerkDesc, "A", "0"));
		// 人事处审核人
		fieldItemList.add(this.getFieldMap("upper_reviewer", upper_reviewerDesc, "A", "0"));
				
		ArrayList fieldsetlist = this.userView.getPrivFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);//获取权限子集
		ArrayList setList = new ArrayList();
		for (int i = 0; i < fieldsetlist.size(); i++) {
			HashMap fieldsetMap = new HashMap();
			FieldSet fieldSet = (FieldSet) fieldsetlist.get(i);
			if(!"1".equalsIgnoreCase(fieldSet.getChangeflag()))
				continue;
			if("0".equalsIgnoreCase(fieldSet.getUseflag()))
				continue;
			if(!fieldSet.getFieldsetid().startsWith("A"))
				continue;

			fieldsetMap.put("fieldsetid", fieldSet.getFieldsetid());
			fieldsetMap.put("fieldsetdesc", fieldSet.getCustomdesc());
			setList.add(fieldsetMap);
		}
		/**
		 * 54240
		 * 记录设置归档失效的指标
		 */
		String mesgs = "";
		if(StringUtils.isNotBlank(messages.toString())) {
			// "以下设置的归档指标已失效，请重新设置！"
			mesgs = ResourceFactory.getProperty("kq.data.sp.msg.archiveFieldInfo");
			mesgs += "<br/>" + messages.toString().substring(1) + "。";;
		}
		HashMap return_data = new HashMap();
		return_data.put("fieldsetid",fieldset);//归档子集
		return_data.put("field_item_list",fieldItemList);//源数据集合
		return_data.put("mapping_list",mappingList);//目标数据集合
		return_data.put("set_list", setList);//子集集合
		return_data.put("messages", mesgs);// 返回信息
		return return_data;
	}
	
	private HashMap getFieldMap(String itemid, String itemdesc, String itemtype, String codesetid) {
		HashMap fieldItemMap = new HashMap();
		fieldItemMap.put("item_id", itemid);
		fieldItemMap.put("item_name", itemdesc);
		fieldItemMap.put("item_type", itemtype);
		fieldItemMap.put("codesetid", codesetid);
		return fieldItemMap;
	}
	
	@Override
    public boolean saveKqDataArchive(String fieldsetid, ArrayList mappingList) throws GeneralException{
		boolean flag = false;
		Document doc = getKqParameter();
		Element rootEl = null;
		Element archiveEl = null;
		List itemList = null;
		if(doc != null){
			rootEl = doc.getRootElement();
			archiveEl = rootEl.getChild("archive_scheme");
		}
	
		if(archiveEl == null){
			archiveEl = new Element("archive_scheme");
			archiveEl.setAttribute("fieldset", fieldsetid);
			rootEl.addContent(archiveEl);
		}else{
			archiveEl.removeChildren("item");
			//archiveEl.detach();
		}
		
		for(int i = 0 ; i < mappingList.size() ; i++){
			   HashMap mappingMap = PubFunc.DynaBean2Map((MorphDynaBean)mappingList.get(i));
			   String sourcefield = (String) mappingMap.get("to_item_id");
			   String itemid = (String) mappingMap.get("item_id");
			   Element itemEl = new Element("item");
			   if(itemid == null || itemid.trim().length() == 0 )
				   continue;
			   itemEl.setAttribute("sourcefield", sourcefield);
			   itemEl.setAttribute("fielditem", itemid);
			   archiveEl.addContent(itemEl);
		}
		StringBuffer xmls = new StringBuffer();
	    XMLOutputter outputter = new XMLOutputter();
	    Format format=Format.getPrettyFormat();
     	format.setEncoding("UTF-8");
     	outputter.setFormat(format);
        xmls.setLength(0);
        xmls.append(outputter.outputString(doc));
        flag = saveStrValue(xmls.toString());

        return flag;
	}

	/**
	 * 保存配置参数,更新缓存
	 * @param sql
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean saveStrValue(String str_value) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		RecordVo vo=new RecordVo("constant");
		vo.setString("constant","KQ_PARAMETER");
		vo.setString("str_value",str_value);
		try {
			if(dao.updateValueObject(vo) > 0 ){
				ConstantParamter.putConstantVo(vo,"KQ_PARAMETER");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//保存考勤归档配置方案出错
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("kq.archive.scheme.savemsg")));
		}
		return false;
	}
	
	/**
	 * 获取考勤相关配置
	 * @return
	 * @throws GeneralException
	 */
	private Document getKqParameter() throws GeneralException{
		RecordVo vo = ConstantParamter.getRealConstantVo("KQ_PARAMETER");
		Document doc = null;
		if (vo != null) {
			if (vo.getString("str_value").toLowerCase() != null
					&& vo.getString("str_value").toLowerCase().trim().length() > 0
					&& vo.getString("str_value").toLowerCase().indexOf("xml") != -1) {
				try {
					doc = PubFunc.generateDom(vo.getString("str_value"));
				} catch (JDOMException e) {
					e.printStackTrace();
					//解析考勤归档方案xml出错
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("kq.archive.scheme.xmlmsg")));
				} catch (Exception e) {
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("kq.archive.scheme.xmlmsg")));
				}
			}
		}
		return doc;
	}
	/**
	 * 校验指标是否符合要求
	 * @param fielditemid
	 * @param fieldset
	 * @return
	 * @throws GeneralException
	 */
	private boolean isCheckFieldItemid(String fielditemid, String fieldset) throws GeneralException{
		boolean bool = true;
		if(StringUtils.isBlank(fielditemid) || fielditemid.trim().length() == 0 || fielditemid.trim().length() == 1)
			return false;
		FieldItem fi = DataDictionary.getFieldItem(fielditemid, fieldset);
		if(null == fi)
			return false;
		// 去除没有启用的指标
		if (!"1".equals(fi.getUseflag())) 
			return false;
		// 去除隐藏的指标
		if (fi.getDisplaywidth() <= 0) 
			return false;
		
		return bool;
	}
}
