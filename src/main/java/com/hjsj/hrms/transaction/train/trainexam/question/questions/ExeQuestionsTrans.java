package com.hjsj.hrms.transaction.train.trainexam.question.questions;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.*;

/**
 * 将读入内存的数据持久化到数据库
 */
public class ExeQuestionsTrans extends IBusiness {
	public void execute() throws GeneralException {
		int num = 0;
		try {
			Object[] maps = (Object[]) this.getFormHM().get("maps");
			String isupdate = (String) this.getFormHM().get("isupdate");
			isupdate = isupdate == null || "".equals(isupdate) ? "1" : isupdate;
			String updatestr = (String) this.getFormHM().get("updatestr");
			ArrayList dlist = (ArrayList) this.getFormHM().get("dlist"); // 得到知识点集合
			// ArrayList xlist =
			// (ArrayList)this.getFormHM().get("xlist");//得到选项内容集合
			// ArrayList tlist =
			// (ArrayList)this.getFormHM().get("tlist");//得到列名集合
			ArrayList valueMapList = (ArrayList) this.getFormHM().get("valueMapList"); //得到试题选项的集合 例如：key 选项A value 选项A内容
			updatestr = updatestr != null ? updatestr : "";
			HashMap fieldMap = (HashMap) maps[0];
			if (fieldMap == null)
				return;
			ArrayList valueList = (ArrayList) maps[1];
			if (valueList == null || valueList.size() == 0) {
				return;
			}
			ArrayList keyList = (ArrayList) maps[3];
			String primarykey = (String) keyList.get(5);
			StringBuffer a0100sb = (StringBuffer) maps[4];// 用于判断库中是否已存在
			if (keyList == null || keyList.size() == 0)
				return;
			ContentDAO dao = new ContentDAO(this.frameconn);
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			RecordVo vo = null;
			QuestionesBo bo = new QuestionesBo(this.frameconn);
			int size = valueList.size();

			String r5200 = "";
			String type = "";

			List list = new ArrayList();

			TrainCourseBo tbo = new TrainCourseBo(this.getUserView());//用户所在单位
			String s = tbo.getUnitIdByBusi();

			if ("".equals(s)) {
				s = null;
			} else {
				String[] s1 = s.split("`");
				s = s1[0].substring(2, s1[0].length());
			}
			for (int i = 0; i < size; i++) {
				HashMap valueMap = (HashMap) valueList.get(i);
				String keyvalue = (String) valueMap.get(primarykey);

				if (keyvalue == null || "".equals(keyvalue)) {
					continue;
				}
				//if (a0100sb.toString().indexOf(keyvalue) != -1)// 库中已存在
				//	continue;
				vo = new RecordVo("r52");
				String questionesClass = this.getFormHM().get("questionClass")//从前台得到试题分类
						.toString();
				for (Iterator it = valueMap.keySet().iterator(); it.hasNext();) {
					String itemid = (String) it.next();

					FieldItem item = DataDictionary.getFieldItem(itemid);

					if ("create_user".equals(itemid)) { //读取列时 先读取题型 再读取答案
						itemid = "type_id";
					}

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

						if ("type_id".equals(itemid)) { //通过题型名称找到题型ID
							value = bo.getQuestionTypeIdByName(value) + "";
							if (null != value && !"".equals(value)) {
								type = value;
							}
						}

						if ("create_user".equals(item.getItemid())) {
							itemid = "create_user"; //答案
							value = (String) valueMap.get(itemid);
							if (null != type && !"".equals(type)) {
								if (bo.getIsObjective(type)) {
									// R5208 主观题答案 Text
									
									vo.setString("r5208", value);
								} else {
									// R5209 客观题答案 Text
									if (value.length() > 1) { //如果是多选题
										String str = "";
										//去除多选题答案中的逗号  chenxg 2016-11-04
										value = value.replace(",", "");
										char[] ch = value.toCharArray();
										for (int f = 0; f < ch.length; f++) {
											if (ch[f] >= 'a' && ch[f] <= 'z') {
												ch[f] -= 32;
									 		}
											
											if (ch[f] >= 'A' && ch[f] <= 'Z') {
											    str += ch[f]+",";
                                            }
										}
										value = str;
									}
									if ("对".equals(value)) { //如果是是非题
										value = "A";
									} else if ("错".equals(value)) {
										value = "B";
									}
									if(!"".equals(value) && value != null){
										
									char c = value.charAt(0);
									if (c >= 'a' && c <= 'z') {
										c -= 32;
										value = c + "";
									}
									}
									vo.setString("r5209", value.trim());
								}
							}
						}

						if ("r5203".equals(item.getItemid())) { //通过题型难度名称找到相应VALUE
							ArrayList a = bo.getDifficultyList();
							CommonData c = null;
							for (int v = 0; v < a.size(); v++) {
								c = (CommonData) a.get(v);
								if (value.equals(c.getDataName())) {
									value = c.getDataValue();
								}
							}
						}

						if ("A".equals(item.getItemtype()) && "0".equals(item.getCodesetid())) {
							value = splitString(value, item.getItemlength());
						}

						if ("N".equals(item.getItemtype()) && "0".equals(item.getCodesetid()) && !"R5203".equals(item.getItemid())) {
							value = splitString(value, item.getItemlength());
							if (!this.isNum(value)) {
								value = "0";
							}
						}
						vo.setString("r5201", questionesClass);

						if (!"```".equals(value)) {
							vo.setString(itemid, value);
						}
					}

				}
				vo.setString("b0110", s);
				String priFldValue = idg.getId("R52.R5200");
				vo.setString("create_user", null);
				vo.setString("r5200", priFldValue);
				r5200 = priFldValue;
				list.add(r5200);
				num += dao.addValueObject(vo);
			}

			if (valueMapList.size() > 0) {
				HashMap value2Map = new HashMap();
				Set set = null;
				for (int v = 0; v < valueMapList.size(); v++) {
					value2Map = (HashMap) valueMapList.get(v);
					set = value2Map.keySet(); //得到集合所有key 即 选项A
					Object[] o = set.toArray();
					Arrays.sort(o); // 对数组进行排序 按ABCD的方式进行显示
					StringBuffer sel = new StringBuffer();
					if (o.length > 0) {
						sel.append("<?xml version=\"1.0\" encoding=\"GB2312\"?><Params>");
						for (int x = 0; x < o.length; x++) {
							sel.append("<item id=\"");
							sel.append(o[x].toString().substring(2, 3)); //截取字符串 若是选项A 则变成A
							sel.append("\"><![CDATA[");
							sel.append(value2Map.get(o[x]));
							sel.append("]]></item>");
						}
						sel.append("</Params>");
						if (list.size() > 0) {
							if (v < list.size()) {
								bo.updateR5207(sel.toString(), list.get(v).toString()); //修改表中r5207字段
							}
						}
					}
				}
			}

			String knowName = null;
			String sid = "";
			if (dlist.size() > 0) {
				for (int i = 0; i < dlist.size(); i++) {
					knowName = (String) dlist.get(i);
					String[] str = knowName.split(",");

					for (int k = 0; k < str.length; k++) {
						sid = bo.getCodeitemId(str[k].trim()); // 通过知识点名称得到ID
						if (list.size() > 0 && i < list.size()) {
							bo.saveKnowledge(sid, list.get(i).toString()); //添加当前行的知识点
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			this.getFormHM().put("num", String.valueOf(num));
		}

	}

	//判断是不是数字
	public static boolean isNum(String ss) {
		for (int i = 0; i < ss.length(); i++) {
			char a = ss.charAt(i);
			if (a > '9') {
				return false;
			}
		}
		return true;
	}

	public char Caseconversion(String value) {
		char c = value.charAt(0);
		if (c >= 'a' && c <= 'z')
			c -= 32;
		return c;
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
}
