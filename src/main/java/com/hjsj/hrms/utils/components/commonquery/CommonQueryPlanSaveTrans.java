package com.hjsj.hrms.utils.components.commonquery;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommonQueryPlanSaveTrans extends IBusiness {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public void execute() throws GeneralException {
		
		ArrayList planItems = (ArrayList)this.formHM.get("planItems");
		int is_share =(Integer)this.formHM.get("is_share");
		String userName = this.userView.getUserName();
		String subModuleId = (String)this.formHM.get("subModuleId");
		RowSet rs = null;
		ArrayList paramList = new ArrayList();
		ContentDAO dao = new ContentDAO(this.frameconn);
		String sql = "";
		try {
			if(is_share==0){
				sql = "SELECT planid FROM t_sys_table_commonqueryplan WHERE subModuleId=? AND username=?  ORDER BY is_share";
				paramList.add(subModuleId);
				paramList.add(userName);
			}else if(is_share==1){
				sql = "SELECT planid FROM t_sys_table_commonqueryplan WHERE subModuleId=? AND is_share=1  ORDER BY is_share";
				paramList.add(subModuleId);
			}
			rs = dao.search(sql, paramList);
			if(rs.next()){
				String planid = "";
				planid = rs.getString("planid");
				if(is_share==0){
					sql = "DELETE FROM t_sys_table_commonqueryplan WHERE subModuleId=? AND username=?  AND is_share=0";;
				}else if(is_share==1){
					sql = "DELETE FROM t_sys_table_commonqueryplan WHERE subModuleId=? AND is_share=1";
				}
				dao.delete(sql, paramList);
				paramList.clear();
				if(is_share==0){
					sql = "DELETE FROM t_sys_table_commonqueryfield WHERE planid=?";;
				}
				paramList.add(planid);
				dao.delete(sql, paramList);
			}
			paramList.clear();
			HashMap map = new HashMap();
			ArrayList fieldList = new ArrayList();
			HashMap hashMap = new HashMap();
			RecordVo planvo = new RecordVo("t_sys_table_commonqueryplan");
			IDGenerator id = new IDGenerator(2, this.frameconn);
			String planid = id.getId("t_sys_table_commonqueryplan.planid");
			planvo.setString("planid",planid);
			planvo.setString("submoduleid",subModuleId);
			planvo.setString("username",userView.getUserName());
			planvo.setInt("is_share",is_share);
			dao.addValueObject(planvo);
			RecordVo fieldvo = null;
			ArrayList voList = new ArrayList();
			for(int i =0;i<planItems.size();i++){
				DynaBean b = (DynaBean)planItems.get(i);
				hashMap = new HashMap();
				map = PubFunc.DynaBean2Map(b);
				fieldvo = new RecordVo("t_sys_table_commonqueryfield");
				fieldvo.setString("planid",planid );
				fieldvo.setString("itemid",(String) (map.get("itemid")) );
				fieldvo.setString("fieldsetid",(String) (map.get("fieldsetid")) );
				hashMap.put("itemid", (String) (map.get("itemid")));
				hashMap.put("fieldsetid", (String) (map.get("fieldsetid")));
				if(map.get("fieldsetid")!=null&&!"".equals(map.get("fieldsetid"))){
					FieldItem item = DataDictionary.getFieldItem((String) (map.get("itemid")), (String) (map.get("fieldsetid")));
					if(item!=null&&!"0".equals(item.getUseflag())){
						fieldvo.setString("itemtype",item.getItemtype() );
						fieldvo.setString("itemdesc",item.getItemdesc());
						fieldvo.setString("codesetid",item.getCodesetid() );
						hashMap.put("itemtype",item.getItemtype() );
						hashMap.put("itemdesc",item.getItemdesc());
						hashMap.put("codesetid",item.getCodesetid() );
						getCodeData(hashMap,dao);
						if("N".equals(item.getItemtype())){
							fieldvo.setInt("formatlength",item.getDecimalwidth() );
							hashMap.put("formatlength",item.getDecimalwidth() );
						}else if("D".equals(item.getItemtype())){
							fieldvo.setInt("formatlength",item.getItemlength() );
							hashMap.put("formatlength",item.getItemlength() );
						}
						fieldvo.setInt("fieldorder",i );
						voList.add(fieldvo);
						fieldList.add(hashMap);
					}
				}else{
					hashMap = map;
					fieldvo.setString("itemtype",(String) (map.get("itemtype")) );
					fieldvo.setString("itemdesc",(String) (map.get("itemdesc")) );
					fieldvo.setString("codesetid",(String) (map.get("codesetid")) );
					if(map.get("formatlength")!=null)
						fieldvo.setInt("formatlength",(Integer) map.get("formatlength"));
					fieldvo.setInt("fieldorder",i );
					getCodeData(hashMap,dao);
					voList.add(fieldvo);
					fieldList.add(hashMap);
				}
				
			}
			dao.addValueObject(voList);
			this.formHM.put("planItems", fieldList);
			this.formHM.put("saveResult", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			this.formHM.put("saveResult", false);
			e.printStackTrace();
		}
	}
	private void getCodeData(HashMap map,ContentDAO dao){
		//查询是不是单层级代码，如果是，有数据，如果不是，没有数据
		String sql = "select codeitemid,codeitemdesc from codeitem where codesetid=? and not exists(select * from codeitem where codesetid=? and parentid<>codeitemid)";
		ArrayList value = new ArrayList();
		try{
			if(map.containsKey("codesetid") && !"0".equals(map.get("codesetid"))){
				value.add(map.get("codesetid"));
				value.add(map.get("codesetid"));
				this.frowset = dao.search(sql, value);
				List items = ExecuteSQL.executePreMyQuery(sql, value, frameconn);
				value.clear();
				if(items.size()>0)
					map.put("codeData", items);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
