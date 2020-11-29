package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SaveTrainees extends IBusiness {

	public void execute() throws GeneralException {
		HashMap maps = (HashMap) this.getFormHM().get("maps"); // 得到培训班培训学员信息
		String classid = (String) maps.get("classid");
		HashMap hm = getStudents(classid);
		int num = hm.size();
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			for (Iterator iter = maps.entrySet().iterator(); iter.hasNext();) {
				Map.Entry entry = (Map.Entry) iter.next(); // map.entry 同时取出键值
				String key = (String) entry.getKey();
				if("classid".equalsIgnoreCase(key))
					continue;
				Object val = entry.getValue();
				HashMap values = (HashMap) val;

				String studentName="";
				
				RecordVo vo = new RecordVo("r40");

				for (Iterator i = values.entrySet().iterator(); i.hasNext();) {
					Map.Entry entrys = (Map.Entry) i.next();
					String key1 = (String) entrys.getKey();
					String value = (String) entrys.getValue();
					FieldItem fi = DataDictionary.getFieldItem(key1, "r40");
					if("r4002".equalsIgnoreCase(key1))
						studentName = value;
					if ("N".equalsIgnoreCase(fi.getItemtype())) {
						int m = fi.getDecimalwidth();
						String va = PubFunc.round(value, m);
						vo.setNumber(key1, va);
					} else if ("D".equalsIgnoreCase(fi.getItemtype())) {
						vo.setDate(key1, value);
					} else if ("A".equalsIgnoreCase(fi.getItemtype()) && "0".equalsIgnoreCase(fi.getCodesetid())){
						int m =fi.getItemlength();
						value = splitString(value, m);
						vo.setString(key1, value);
					} else
						vo.setString(key1, value);

				}
				String[] keys = key.split("[|]");
				String[] persons = keys[1].split(",");
				vo.setString("nbase", persons[0]);
				vo.setString("r4001", persons[1]);
				vo.setString("r4005", classid);
				vo.setString("r4013", "03");
				if (hm.containsKey(persons[0] + persons[1])) 
					num += dao.updateValueObject(vo);
				else {
					num += dao.addValueObject(vo);
					hm.put(persons[1], studentName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			this.formHM.put("num", num+"");
		}
	}
	/**
	 * 字符串截断
	 * @param source 字符串
	 * @param len 字符串的最大长度
	 * @return
	 */
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
	 * 
	 * @param source
	 * @param len
	 * @return
	 */
	private HashMap getStudents(String classid) {
		HashMap hm = new HashMap();
		String sql = "select nbase,r4001,r4002 from r40 where r4005='" + classid + "' and r4013='03'";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset= dao.search(sql);
			while(this.frowset.next()){
				hm.put(this.frowset.getString("nbase")+this.frowset.getString("r4001"), this.frowset.getString("r4002"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return hm;
	}
}
