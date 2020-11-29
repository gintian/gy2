package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 将读入内存的数据持久化到数据库
 */
public class ImpClassDataTrans extends IBusiness {

	private String fieldsetid = "R31";
	public void execute() throws GeneralException {

		int num = 0;
		String priFldValue="";
		try {
			fieldsetid = ((String) this.getFormHM().get("fieldsetid")).toLowerCase();
			ArrayList mapsList = (ArrayList) this.getFormHM().get("mapsList");
			for (int no = 0; no < mapsList.size(); no++) {

				Object[] maps = (Object[]) mapsList.get(no);

				HashMap fieldMap = (HashMap) maps[0];
				if (fieldMap == null)
					return;
				ArrayList valueList = (ArrayList) maps[1];
				if (valueList == null || valueList.size() == 0) {
					return;
				}
				ArrayList keyList = (ArrayList) maps[3]; // 字段列表
				fieldsetid = ((StringBuffer) maps[4]).toString();
				HashMap errorkey = (HashMap)maps[5];
				if (keyList == null || keyList.size() == 0)
					return;
				String primarykey = (String) keyList.get(0);
				ContentDAO dao = new ContentDAO(this.frameconn);

				// 先清空
				this.getFormHM().put("info", "");
				this.getFormHM().put("num", 0 + "");
				ScanFormationBo scanFormationBo = new ScanFormationBo(
						this.frameconn, userView);
				if (scanFormationBo.doScan()) {
					int infoFlag; // 信息类型， 1培训班信息表   2培训课程信息表 
					if ("R31".equalsIgnoreCase(fieldsetid)) {
						infoFlag = 1;
					} else {
						infoFlag = 2;
					}
					StringBuffer items = new StringBuffer(",");
					for (int i = 0; i < keyList.size(); i++) {
						// 第一个是唯一性标识
						if (infoFlag != 1 && i == 0)
							continue;

						items.append(keyList.get(i).toString().trim());
						items.append(",");
					}

				}

				if ("R31".equalsIgnoreCase(fieldsetid)) {
					int i9999 = valueList.size();
					 getI9999(i9999);
					boolean flag = true;
					for (int i = 0; i < valueList.size(); i++) {
						HashMap valueMap = (HashMap) valueList.get(i);
						String keyvalue = (String) valueMap.get(primarykey);
						if (keyvalue == null || "".equals(keyvalue)) {
							continue;
						}
						
						if(errorkey.containsKey(keyvalue+"a"+i))
							continue;
						RecordVo vo = null;

						vo = new RecordVo(fieldsetid);

						IDGenerator idg = new IDGenerator(2, this.getFrameconn());
						priFldValue = idg.getId(fieldsetid.toUpperCase() + ".R3101");
						
						vo.setString("r3101", priFldValue);
						vo.setString("r3127", "03");
						
						if(i9999 != 0)
							vo.setInt("i9999", i9999-i);
						for (Iterator it = valueMap.keySet().iterator(); it.hasNext();) {
							String itemid = (String) it.next();

							FieldItem item = DataDictionary.getFieldItem(itemid);
							if ("D".equals(item.getItemtype())) {
								String value = (String) valueMap.get(itemid);
								if (value == null || "".equals(value)) {
									continue;
								}
								
								if (!"```".equals(value)) {
									vo.setDate(itemid, value);
								}
							} else {
								String value = (String) valueMap.get(itemid);
								if("b0110".equalsIgnoreCase(item.getItemid())&& (value == null || "".equals(value))){
									flag = false;
									break;
								}
								
								if (value == null || "".equals(value)) {
									continue;
								}
								String codesetid = item.getCodesetid();
								if(codesetid == null || "".equalsIgnoreCase(codesetid))
								    codesetid = "0";
								
								if ("A".equals(item.getItemtype()) && "0".equals(codesetid)) {
									value = splitString(value, item.getItemlength());
								}
								
								if (!"```".equals(value)) {
									vo.setString(itemid, value);
								}
							}
						}
						if(!flag)
							break;
						
						num += dao.addValueObject(vo);
						i9999++;
					}
				} else {
					for (int i = 0; i < valueList.size(); i++) {
						HashMap valueMap = (HashMap) valueList.get(i);
						String keyvalue = (String) valueMap.get(primarykey);
						if (keyvalue == null)
							continue;
						
						if(errorkey.containsKey(keyvalue+"b"+i))
							continue;
						
						RecordVo vo = new RecordVo(fieldsetid);
						
						IDGenerator idg = new IDGenerator(2, this.getFrameconn());
						priFldValue = idg.getId(fieldsetid.toUpperCase() + ".R4101");
						
						vo.setString("r4101", priFldValue);
						String classid = getClassId(keyvalue);
						if(classid == null || classid.length()<1)
							continue;
						vo.setString("r4103", classid);
						for (Iterator it = valueMap.keySet().iterator(); it.hasNext();) {
							String itemid = (String) it.next();

							if (primarykey.equalsIgnoreCase(itemid)) {
								continue;
							}
							FieldItem item = DataDictionary.getFieldItem(itemid);
							if ("D".equals(item.getItemtype())) {
								String value = (String) valueMap.get(itemid);
								if (value == null || "".equals(value)) {
									continue;
								}
								if (!"```".equals(value)) {
									vo.setDate(itemid, value);
								}
							} else {
								String value = (String) valueMap.get(itemid);
								if (value == null || "".equals(value)) {
									continue;
								}
								
								String codesetid = item.getCodesetid();
                                if(codesetid == null || "".equalsIgnoreCase(codesetid))
                                    codesetid = "0";
                                
                                if ("A".equals(item.getItemtype()) && "0".equals(codesetid)) {
									value = splitString(value, item.getItemlength());
								}
                                
								if (!"```".equals(value)) {
									vo.setString(itemid, value);
								}
							}
						}
						num += dao.addValueObject(vo);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			this.getFormHM().put("number", String.valueOf(num));
			this.getFormHM().put("flag", "improt");
		}
	}

	private String splitString(String source, int len) {
		byte[] bytes = source.getBytes();
		int bytelen = bytes.length;
		int j = 0;
		int rlen = 0;
		if (bytelen <= len)
			return source;

		for (int i = 0; i < len; ++i) {
			if (bytes[i] < 0)
				++j;
		}
		if (j % 2 == 1)
			rlen = len - 1;
		else
			rlen = len;
		byte[] target = new byte[rlen];
		System.arraycopy(bytes, 0, target, 0, rlen);
		String dd = new String(target);
		return dd;
	}
/**
 * 获取新插入的培训班编号  如果培训班有重复的则只获取最后插入的培训班编号
 * @param classname
 * @return
 */
	private String getClassId(String classname) {
		String id = "";
		String sql = "select r3101 from r31 where r3130='" + classname + "' and r3127<>'06' and (b0110 is not null or b0110<>'') " + getPiv() + " order by r3101 desc";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql);
			if (this.frowset.next())
				id = this.frowset.getString("r3101");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}
	/**
	 * 获取r31表中最大的I9999
	 * @param classname
	 * @return
	 */
	private void getI9999(int i9999) {
		String sql = "update r31 set i9999=i9999+"+i9999+"where i9999 is not null";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			dao.update(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取当前用户的权限生成查询条件
	 * @return
	 */
	private String getPiv() {
		StringBuffer tmpstr = new StringBuffer();
		try {
		    if(this.userView.isSuper_admin())
		        return tmpstr.toString();
		    TrainCourseBo bo = new TrainCourseBo(this.userView);
		    String manpriv = bo.getUnitIdByBusi();
		    String code = "";
		    
		    if (manpriv == null || manpriv.trim().length() < 3 || manpriv.indexOf("UN`") != -1)
		        return tmpstr.toString();
		    String[] tmp = manpriv.split("`");
		    tmpstr.append("and (");
		    for (int i = 0; i < tmp.length; i++) {
		        code = tmp[i];
		        if (i > 0)
		            tmpstr.append(" or ");
		        if ("UN".equalsIgnoreCase(manpriv.substring(0, 2)))
		            tmpstr.append("b0110 like '" + code.substring(2, code.length()) + "%'");
		    }
		    tmpstr.append(")");
		} catch (Exception e) {
		    e.printStackTrace();
        }
		return tmpstr.toString();
	}
}
