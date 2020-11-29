package com.hjsj.hrms.module.serviceclient.serviceHome.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * 自助终端保存服务须知填写信息业务类
 * 
 * @Titile: SaveClientInputDataBo
 * @Description:
 * @Company:hjsj
 * @Create time: 2018年8月3
 * @author: xuchangshun
 * @version 1.0
 *
 */
public class SaveClientInputDataBo {

	/** userView对象存储用户的相关信息 **/
	private UserView userView;

	/** conn,数据库链接资源 **/
	private Connection conn;
	
	private ContentDAO dao;
	public SaveClientInputDataBo(UserView userView, Connection conn) {
		this.userView = userView;
		this.conn = conn;
		this.dao = new ContentDAO(conn);
	}

	/**
	 * 根据传递过来数据对
	 * @param fieldData
	 */
	public void saveClientInputData(MorphDynaBean fieldData) {
		Map fieldInfoMap = PubFunc.DynaBean2Map(fieldData);
		Iterator<Entry> iterator = fieldInfoMap.entrySet().iterator();
		Map<String, List<Map<String, String>>> setNameFieldListMap = new HashMap<String, List<Map<String, String>>>();
		while (iterator.hasNext()) {
			Entry entry = iterator.next();
			String setAndItemIdOrder = (String) entry.getKey();
			String[] keyArry = setAndItemIdOrder.split("`");
			String setId = keyArry[0];
			String inputValue = (String) entry.getValue();
			List<Map<String, String>> fieldItemIdList = null;
			if (setNameFieldListMap.get(setId) == null) {
				fieldItemIdList = new ArrayList<Map<String, String>>();
			} else {
				fieldItemIdList = setNameFieldListMap.get(setId);
			}
			Map<String, String> keyAndValueMap = new HashMap<String, String>();
			keyAndValueMap.put(setAndItemIdOrder, inputValue);
			fieldItemIdList.add(keyAndValueMap);
			setNameFieldListMap.put(setId, fieldItemIdList);
		}
		constructorAndUpdateVo(setNameFieldListMap);
	}

	/**
	 * 根据传递过来的信息构建vo对象
	 * @param setNameFieldListMap
	 */
	private List<RecordVo> constructorAndUpdateVo(Map<String, List<Map<String, String>>> setNameFieldListMap) {
		Iterator<Entry<String, List<Map<String, String>>>> iterator = setNameFieldListMap.entrySet().iterator();
		String dbName = this.userView.getDbname();
		List<RecordVo> voList = new ArrayList<RecordVo>();
		while(iterator.hasNext()) {
			Entry<String, List<Map<String, String>>> entry = iterator.next();
			String setName = entry.getKey();
			String tableName = dbName+setName;
			RecordVo recordVo = new RecordVo(tableName.toLowerCase());
			recordVo.setString("a0100", this.userView.getA0100());
			List<Map<String,String>> keyAndValueList = entry.getValue();
			fillVoDataAndUpdate(recordVo,keyAndValueList,tableName);
		}
		return voList;
	}
	/**
	 * 填充VO中的数据
	 * @param recordVo
	 * @param keyAndValueList
	 * @param tableName 
	 */
	private void fillVoDataAndUpdate(RecordVo recordVo, List<Map<String, String>> keyAndValueList, String tableName) {
		String i9999 = null;
		for(Map<String,String> keyAndValueMap:keyAndValueList) {
			Iterator<Entry<String,String>> valueIte = keyAndValueMap.entrySet().iterator();
			while(valueIte.hasNext()){
				Entry<String,String> valueEntry = valueIte.next();
				String keys = valueEntry.getKey();//"主集:A01`A0107; 子集(新增):A04`A0405`-1;子集(更新):A04`A0405`1"
				String value = valueEntry.getValue();
				String[] keyArry = keys.split("`");
				String setId = keyArry[0];
				String itemId = keyArry[1];
				if(!"A01".equalsIgnoreCase(setId)) {
					i9999 = keyArry[2];
				}
				FieldItem fieldItem = DataDictionary.getFieldItem(itemId);
				String itemType = fieldItem.getItemtype();
				String codeSetId = fieldItem.getCodesetid();
				itemId = itemId.toLowerCase();
				if("A".equals(itemType)) {
					if("0".equals(codeSetId)) {//非代码类
						recordVo.setString(itemId, value);
					}else {//代码型
						String[] values = value.split("`");
						String codeValue = values[0];
						recordVo.setString(itemId, codeValue);
					}
				}else if("D".equals(itemType)) {
					recordVo.setDate(itemId, value);
				}else if("M".equals(itemType)) {
					recordVo.setString(itemId, value);
				}else if("N".equals(itemType)) {
					int decimalWidth = fieldItem.getDecimalwidth();
					if(decimalWidth==0) {
						recordVo.setInt(itemId, Integer.parseInt(value));
					}else {
						recordVo.setDouble(itemId, Double.parseDouble(value));
					}
				}
			}
		}
		boolean isInsert = false;
		if(StringUtils.isNotBlank(i9999)) {
			int newI9999 = Integer.parseInt(i9999);
			if(newI9999 == -1) {
				isInsert = true;
				newI9999 = getMaxI9999(tableName);
				recordVo.setString("createusername", this.userView.getUserName());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				recordVo.setDate("createtime", sdf.format(new Date()));
			}
			recordVo.setInt("i9999",newI9999);
		}
		try {
			if(isInsert) {
				this.dao.addValueObject(recordVo);
			}else {
				this.dao.updateValueObject(recordVo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	/**
	 * 求子集记录的最大主键
	 * @param tableName
	 * @return
	 */
	public int getMaxI9999(String tableName) {
		int maxi9999 = 1;
		RowSet rset = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList paralist = new ArrayList();
			StringBuffer strsql = new StringBuffer();
			strsql.append("select max(i9999) as maxi9999 from ");
			strsql.append(tableName);
			strsql.append(" where A0100");
			strsql.append("=?");
			paralist.add(this.userView.getA0100());
			rset = dao.search(strsql.toString(), paralist);
			if (rset.next()) {
				maxi9999 = rset.getInt("maxi9999") + 1;
		    }
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rset);
		}
		return maxi9999;
	}
}
