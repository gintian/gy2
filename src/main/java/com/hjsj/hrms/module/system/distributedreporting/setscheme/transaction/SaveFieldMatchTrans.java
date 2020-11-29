package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
/**
 * 指标对应——保存指标项对应关系
 * @author caoqy
 * @date 2019-3-20 15:11:44
 *
 */
public class SaveFieldMatchTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String unitCode = (String)this.getFormHM().get("unitcode");
		ArrayList<String> matchFieldList = (ArrayList<String>)this.getFormHM().get("matchfield");//对应了指标编码
		MorphDynaBean matchMap = (MorphDynaBean)this.getFormHM().get("matchmap");
		HashMap setItemMap = getSetItemMap(matchFieldList,dao);// 用于对应指标的Map 指标编码：子集指标,如 A0107:A01
		ArrayList<String> sqlList = getSqlList(unitCode,matchFieldList,matchMap,setItemMap);// 获取插入信息的sql语句list集合
		List wrapList = groupListByQuantity(sqlList,300);
		try {
			for(int i = 0;i<wrapList.size();i++) {
				ArrayList tempList = (ArrayList) wrapList.get(i);
				dao.batchUpdate(tempList);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取插入信息的sql语句list集合
	 * @param unitCode
	 * @param matchFieldList
	 * @param matchMap
	 * @param setItemMap
	 * @return
	 */
	private ArrayList<String> getSqlList(String unitCode, ArrayList<String> matchFieldList, MorphDynaBean matchMap, HashMap<String, String> setItemMap) {
		ArrayList<String> sqlList = new ArrayList<String>();
		//删除之前配置过的此类代码项
		for (Entry<String, String> entry : setItemMap.entrySet()) {
			String tempcodesetid = entry.getValue();
			sqlList.add("DELETE FROM t_sys_asyn_code WHERE unitcode='"+unitCode+"' and codesetid ='"+tempcodesetid+"'");
		}
		//插入此代码项对应数据
		for (int i = 0;i<matchFieldList.size();i++) {
			String fieldItemId = matchFieldList.get(i);
			String setid = (String) setItemMap.get(fieldItemId);
			ArrayList<MorphDynaBean> MatchList = (ArrayList) matchMap.get(fieldItemId);
			for(int k = 0;k<MatchList.size();k++) {
				MorphDynaBean MatchMessage = (MorphDynaBean) MatchList.get(k);
				String srccodeid = (String) MatchMessage.get("midcodedesc");
				String desccodeid = (String) MatchMessage.get("codesetid");
				String sql = "INSERT INTO t_sys_asyn_code (id,unitcode,codesetid,srccodeid,destcodeid)";
				sql += " SELECT "+Sql_switcher.isnull("max(id)", "0")+"+1,'"+unitCode+"','"+setid+"','"+srccodeid+"','"+desccodeid+"' FROM t_sys_asyn_code";
				sqlList.add(sql);
			}
		}
		return sqlList;
	}
	/**
	 * 
	 * @param matchFieldList
	 * @param dao
	 * @return
	 */
	private HashMap getSetItemMap(ArrayList<String> matchFieldList, ContentDAO dao) {
		HashMap<String, String> setItemMap = new HashMap<String, String>();
		RowSet rs = null;
		try {
			String sql = "SELECT itemid,codesetid FROM fielditem WHERE itemid in(";
			for(int i = 0;i<matchFieldList.size();i++) {
				if(i==0) {
					sql+="?";
				}else {
					sql+=",?";
				}
			}
			sql+=")";
			rs = dao.search(sql,matchFieldList);
			while(rs.next()) {
				String tempitemid = rs.getString("itemid");
				String codesetid = rs.getString("codesetid");
				setItemMap.put(tempitemid, codesetid);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rs);
		}
		return setItemMap;
	}
/**
 * 获取按个数分组的list
 * @param list
 * @param quantity
 * @return
 */
	public List groupListByQuantity(List list, int quantity) {
		if (list == null || list.size() == 0) {
			return list;
		}
		
		if (quantity <= 0) {
			new IllegalArgumentException("Wrong quantity.");
		}
		
		List wrapList = new ArrayList();
		int count = 0;
		while (count < list.size()) {
			wrapList.add(new ArrayList(list.subList(count, (count + quantity) > list.size() ? list.size() : count + quantity)));
			count += quantity;
		}
		return wrapList;
	}
}
