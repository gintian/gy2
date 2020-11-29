package com.hjsj.hrms.transaction.sys.outsync;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 同步接口信息配置过滤范围
 * 
 * @author LiWeichao 2011-07-20 11:22:46
 */
public class FilteRangeTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList setlist = new ArrayList();
		String type = (String) this.getFormHM().get("type");
		HashMap map = new HashMap();
		HrSyncBo hsb = new HrSyncBo(this.frameconn);
		if("1".equals(type)){
			map = hsb.getFieldsMap();
		}else if("2".equals(type)){
			map = hsb.getOrgFieldsMap();
		}else if("3".equals(type)){
			map = hsb.getPostFieldsMap();
		}
		String codefieldstr=hsb.getTextValue(HrSyncBo.CODE_FIELDS);	
		Iterator it = map.values().iterator();
		while(it.hasNext()) {
			//xus 19/4/26 数据视图人员过滤范围可以选择代码型指标
			String item = String.valueOf(it.next()).toUpperCase();
			FieldItem fielditem = (FieldItem)DataDictionary.getFieldItem(item);
			if("A".equalsIgnoreCase(fielditem.getItemtype())&&!"0".equals(fielditem.getCodesetid())&&codefieldstr.toUpperCase().indexOf(item)>-1){
				continue;
			}
//			if("A".equalsIgnoreCase(fielditem.getItemtype())&&!"0".equals(fielditem.getCodesetid())
//					&&!"b0110".equalsIgnoreCase(fielditem.getItemid())
//					&&!"e0122".equalsIgnoreCase(fielditem.getItemid())
//					&&!"e01a1".equalsIgnoreCase(fielditem.getItemid()))
//				continue;
			CommonData dataobj = new CommonData();
			dataobj = new CommonData(fielditem.getItemid() + ":"
					+ fielditem.getItemtype() + ":" + fielditem.getCodesetid()
					+ ":" + fielditem.getFieldsetid(), fielditem.getItemdesc());
			setlist.add(dataobj);
		}
		this.getFormHM().put("htmllist", loadQueryHTML(type));
		this.getFormHM().put("setlist", setlist);
	}

	/**
	 * 解析xml中的查询条件
	 * 
	 * @param type 人员=1，机构=2，岗位=3
	 * @return 组装html的调用js参数
	 */
	private ArrayList loadQueryHTML(String type) {
		ArrayList htmllist = new ArrayList();
		String like = "0";
		String querystr = "";
		String other_param = (String) this.getFormHM().get("other_param");
		other_param = SafeCode.decode(other_param);
		other_param = PubFunc.keyWord_reback(other_param);
		if (other_param.length() < 20 || type == null || type.length() < 1){
			this.getFormHM().put("like", like);
			return htmllist;
		}
		try {
			String name = "";
			if ("1".equals(type))
				name = "A";
			else if ("2".equals(type))
				name = "B";
			else if ("3".equals(type))
				name = "K";
			Document doc = DocumentHelper.parseText(other_param);
			Element root = doc.getRootElement();
			for (Iterator it = root.elementIterator(); it.hasNext();) {
				Element element = (Element) it.next();
				String tempName = element.attributeValue("name");
				tempName = tempName == null ? "" : tempName;
				if (name.equalsIgnoreCase(tempName))
					querystr = element.getText();
			}
			if (querystr != null && querystr.length() > 0) {
				String[] str = querystr.split(",");
				if (str != null && str.length == 3) {
					like = str[0];// 是否为模糊查询 like
					htmllist = getHtml(str[1], str[2]); // 解析sexpr,sfactor
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			return htmllist;
		} finally {
			this.getFormHM().put("like", like);
		}
		return htmllist;
	}

	/**
	 * 解析sexpr,sfactor
	 * 
	 * @param sexpr
	 * @param sfactor
	 * @return 组装html的调用js参数
	 * @throws Exception
	 */
	private ArrayList getHtml(String sexpr, String sfactor) throws Exception {
		ArrayList list = new ArrayList();
		StringBuffer strHtml = new StringBuffer();
		if (sexpr == null || sexpr.length() < 1 || sfactor == null || sfactor.length() < 1)
			return list;
		String[] sfactors = sfactor.split("`");
		for (int i = 0; i < sfactors.length; i++) {
			if (sfactors[i] != null && sfactors[i].length() > 4) {
				FieldItem field = DataDictionary.getFieldItem(sfactors[i].substring(0, 5));
				if (field != null) {
					String value = "";
					int temp = sfactors[i].length() - 1;
					temp = sfactors[i].lastIndexOf("=");
					temp = temp == -1 ? sfactors[i].lastIndexOf(">") : temp;
					temp = temp == -1 ? sfactors[i].lastIndexOf("<") : temp;
					if ("A".equalsIgnoreCase(field.getItemtype())
							&& !"0".equals(field.getCodesetid())) {
						value = AdminCode.getCodeName(field.getCodesetid(), PubFunc.nullToStr(sfactors[i].substring(temp + 1)));
					}
					//loadTable('查询指标','指标名称','类型(A|N|D)','codesetid',逻辑符定位位置,关系符定位位置,'查询值','代码型隐藏值');
					list.add("loadTable('"
							+ field.getItemid()
							+ "','"
							+ field.getItemdesc()
							+ "','"
							+ field.getItemtype()
							+ "','"
							+ field.getCodesetid()
							+ "',"
							+ getOper(0, i, sexpr)
							+ ","
							+ getOper(1, i, sfactors[i])
							+ ",'"
							+ PubFunc.nullToStr(sfactors[i].substring(temp + 1))
							+ "','" + value + "');");
				}
			}
		}
		//System.out.println(list);
		return list;
	}

	/**
	 * 符号所在位置
	 * @param flag 逻辑符=0，关系符=1
	 * @param i 加载的第几个条件
	 * @param strValue 要解析的字符串
	 * @return select定位位置 int
	 * @throws Exception
	 */
	private int getOper(int flag, int i, String strValue) throws Exception {
		int intValue = 0;
		String tempValue = "";
		if (flag == 0) {
			int t = strValue.indexOf((i) + "");
			int tt = i > 9 ? 2 : 1;
			if (strValue.length() > t + tt)
				tempValue = strValue.substring(t + 1, t + tt + 1);
			if ("+".equals(tempValue))
				intValue = 1;
		} else if (flag == 1) {
			if (strValue.indexOf("<>") != -1)
				intValue = 5;
			else if (strValue.indexOf("<=") != -1)
				intValue = 4;
			else if (strValue.indexOf("<") != -1)
				intValue = 3;
			else if (strValue.indexOf(">=") != -1)
				intValue = 2;
			else if (strValue.indexOf(">") != -1)
				intValue = 1;
		}
		return intValue;
	}
}
