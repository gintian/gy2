package com.hjsj.hrms.transaction.mobileapp.contacts;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * <p> Title: ContactsTrans </p>
 * <p> Description: 通讯录交易类 </p>
 * <p> Company: hjsj </p>
 * <p> create time: 2013-12-17 下午4:49:33 </p>
 * 
 * @author yangj
 * @version 1.0
 */
public class ContactsTrans extends IBusiness {

	private static final long serialVersionUID = 1L;

	/** 显示通讯录 */
	private final String CREATE_CONTACTS = "1";
	/** 保存通讯录 */
	private final String SAVED_CONTACTS = "2";
	/** 批量转发联系人 */
	private final String BATCH_FORWARD_CONTACTS = "3";
	/** 查询单人联系人详情 */
	private final String SEARCH_CONTACT_INFO = "4";
	/**获取便捷查询hint*/
	private final String HINTINFO = "5";
	
	public void execute() throws GeneralException {
		String message = "";
		String succeed = "false";
		HashMap hm = this.getFormHM();
		try {
			String transType = (String) hm.get("transType");
			hm.remove("transType");
			hm.remove("message");
			hm.remove("succeed");
			UserView userView = this.getUserView();
			Connection conn = this.getFrameconn();
			ContactsBo contactsBo = new ContactsBo(conn, userView);
			// 不同业务流程分支点
			if (transType != null) {
				if (CREATE_CONTACTS.equals(transType)) {// 主界面通讯录展示
					this.createContacts(hm, contactsBo);
					succeed = "true";
				} else if (SAVED_CONTACTS.equals(transType)) {// 收集保存人员信息
					ArrayList list = (ArrayList) hm.get("nbaseA0100List");
					ArrayList contactsOrderList = new ArrayList();// 用于记录后台配置的指标顺序
					List savedContactsList = contactsBo.getContactsList(list, contactsOrderList, null);
					hm.put("transType", transType);
					hm.put("savedContactsList", savedContactsList);
					hm.put("contactsOrderList", contactsOrderList);
					succeed = "true";
				} else if (BATCH_FORWARD_CONTACTS.equals(transType)) {// 批量转发联系人
					ArrayList list = (ArrayList) hm.get("nbaseA0100List");
					ArrayList contactsOrderList = new ArrayList();// 用于记录后台配置的指标顺序
					List batchForwardContactsList = contactsBo.getContactsList(list, contactsOrderList, null);
					hm.put("transType", transType);
					hm.put("batchForwardContactsList", batchForwardContactsList);
					hm.put("contactsOrderList", contactsOrderList);
					succeed = "true";
				} else if (SEARCH_CONTACT_INFO.equals(transType)) {// 查询单人联系人详情
					ArrayList list = new ArrayList();
					String nbase = (String) hm.get("nbase");
					String a0100 = (String) hm.get("a0100");
					list.add(nbase + "`" + a0100);
					String photoUrl = (String) hm.get("photoUrl");
					HashMap personInfoMap = new HashMap();// 存放联系人姓名单位，岗位部门，照片信息
					ArrayList contactInfoList = new ArrayList();// 存放联系方式信息
					contactsBo.getOneContantInfo(list, personInfoMap, contactInfoList, photoUrl);
					hm.put("transType", transType);
					hm.put("contactInfoList", contactInfoList);
					hm.put("personInfoMap", personInfoMap);
					succeed = "true";
				}else if (HINTINFO.equals(transType)){
					//获取便捷查询hint					
					this.getGeneralMessage(hm,conn);					
					succeed = "true";
				} else {
					message = ResourceFactory.getProperty("mobileapp.contacts.error.transTypeError");
					hm.put("message", message);
				}
			} else {
				message = ResourceFactory.getProperty("mobileapp.contacts.error.transTypeError");
				hm.put("message", message);
			}
		} catch (Exception e) {
			succeed = "false";
			message = ResourceFactory.getProperty("mobileapp.contacts.error.transTypeError");
			hm.put("message", message);
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			hm.put("succeed", succeed);
		}
	}

	/**
	 *  
	 * @Title: getGeneralMessage   
	 * @Description: 获取便捷查询hint   
	 * @param  hm 
	 * @param conn
	 * @return void    
	 * @throws
	 */
	private void getGeneralMessage(HashMap hm, Connection conn) {
		String generalmessage = ResourceFactory.getProperty("label.title.name");
		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
		FieldItem item = DataDictionary.getFieldItem(onlyname);
		if (item != null && !"a0101".equalsIgnoreCase(onlyname) && !"0".equals(userView.analyseFieldPriv(item.getItemid()))) {
			generalmessage += "\\" + item.getItemdesc();
		}
		String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
		item = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
		if (!(pinyin_field == null || "".equals(pinyin_field) || "#".equals(pinyin_field) || item == null
				|| "0".equals(item.getUseflag())) && !"a0101".equalsIgnoreCase(pinyin_field)
				&& !"0".equals(userView.analyseFieldPriv(item.getItemid())))
			generalmessage += "\\" + item.getItemdesc();
		hm.put("hintinfo", generalmessage);
		hm.put("transType", HINTINFO);
	}

	/**
	 * 
	 * @Title: createContacts
	 * @Description: 通讯录展示
	 * @param hm
	 * @param contactsBo
	 * @return void
	 * @throws GeneralException
	 */
	private void createContacts(HashMap hm, ContactsBo contactsBo) throws GeneralException {
		// 准备工作
		Map temporaryMap = contactsBo.prepare();
		// 判断是否设置电话字段参数
		if (temporaryMap != null && temporaryMap.size() > 0) {

			String mobile = (String) temporaryMap.get("mobile");
			String email = (String) temporaryMap.get("email");
			String url = (String) hm.get("url");

			// 第几页
			String pageIndex = (String) hm.get("pageIndex");
			pageIndex = pageIndex == null ? "1" : pageIndex;

			// 每页条数
			String pageSize = (String) hm.get("pageSize");
			pageSize = pageSize == null ? "10" : pageSize;

			// 模糊查询
			String keywords = (String) hm.get("keywords");
			keywords = keywords == null ? "" : keywords;

			// 根据组织机构ID查询
			String unitID = (String) hm.get("mUnitID");
			unitID = unitID == null || unitID.length() == 0 ? "" : unitID;

			List mContactList = contactsBo.searchInfoList(unitID, keywords, url, pageIndex, pageSize, mobile, email);
			hm.put("mContactList", mContactList);
			hm.put("transType", CREATE_CONTACTS);
		} else {
			hm.put("transType", "0");
		}
	}

}
