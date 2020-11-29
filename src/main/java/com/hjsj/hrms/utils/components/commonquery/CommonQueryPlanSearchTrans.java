package com.hjsj.hrms.utils.components.commonquery;

import com.hjsj.hrms.module.recruitment.util.RecruitPrivBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommonQueryPlanSearchTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute() throws GeneralException {


		/** 测试代码 
		 *   传进来的参数：subModuleId、default
		 *   default是如果没有设置方案显示的查询指标，在后台为单级代码指标添加数据
		 *   回传参数格式：
		 *  arraylist [
			    {fieldsetid:’A01’,itemid:’a0101’,itemdesc:’姓名’,itemtype:’A’,codesetid:’0’},
			    {fieldsetid:’A01’,itemid:’a0102’,itemdesc:’出生日期’,itemtype:’D’,format:’yyyy-MM-dd’},
			    {fieldsetid:’A01’,itemid:’a0103’,itemdesc:’身高’,itemtype:’N’,format:’00.00’},//代表整数两位位小数两位
			    {fieldsetid:’A01’,itemid:’a0104’,itemdesc:’个人简介’,itemtype:’M’},
			    //代码型指标，如果是单层级代码，将代码查出来放到codeData中，如果是多层级，就不放了。
			    {fieldsetid:’A01’,itemid:’a0105’,itemdesc:’性别’,itemtype:’A’,codesetid:’XB’,codeData:[{codeitemid:'01',codeitemdesc:'男'},{codeitemid:'02',codeitemdesc:'女'}]},
			    //如果不是数据字典里的指标，不用设置setid属性
			    {itemid:’customfield’,itemdesc:’自定义指标’,itemtype:’A’},
			               ........
			            ]
		 * 
		 * 
		 * **/
		String subModuleId = "";
		String userName = "";
		String planid = "";
		HashMap map=null;
		ArrayList planItems = new ArrayList();
		ArrayList defaultList =null;
		ArrayList list =new ArrayList();
		subModuleId = (String)this.getFormHM().get("subModuleId");
		defaultList = (ArrayList)this.getFormHM().get("default");
		defaultList = null==defaultList ? new ArrayList() : defaultList;
		userName = this.userView.getUserName();
		boolean flag = true;
		String sql = "";
		ContentDAO dao = new ContentDAO(frameconn);
		String isReset = null;
		isReset = (String)this.getFormHM().get("isReset");
		RowSet rs = null;
		try {
			ArrayList al = new ArrayList();
			if(isReset==null||!"1".equals(isReset)){
				//查询是否存在私有的查询方案
				sql = "SELECT PLANID FROM t_sys_table_commonqueryplan WHERE submoduleid = ? and username = ? and is_share =0";
				al.add(subModuleId==null?"":subModuleId);
				al.add(userName);
				rs= dao.search(sql.toString(), al);
				if(rs.next()){
					planid = rs.getString("planid");
					flag = false;
				}
			}
			//如果没有私有查询方案，那么查询是否存在公共查询模板
			if(flag){
				al.clear();
				al.add(subModuleId==null?"":subModuleId);
				sql = "SELECT PLANID FROM t_sys_table_commonqueryplan WHERE submoduleid = ? and is_share =1";
				rs= dao.search(sql.toString(), al);
				if(rs.next()){
					planid = rs.getString("planid");
				}
			}
			//判断是否存在私有或者公共模板，如果存在查出来不存在则使用默认的方案
			if(!"".equals(planid)&&planid.length()>0){
				sql = "SELECT * FROM t_sys_table_commonqueryfield WHERE planid = ? ORDER BY fieldorder";
				al.clear();
				al.add(planid);
				rs= dao.search(sql.toString(), al);
				while(rs.next()){
					map = new HashMap();
					map.put("itemid", rs.getString("itemid"));
					map.put("fieldsetid", rs.getString("fieldsetid"));
					if(rs.getString("fieldsetid")!=null&&!"".equals(rs.getString("fieldsetid"))){
						FieldItem item=DataDictionary.getFieldItem(rs.getString("itemid"), rs.getString("fieldsetid"));
						if(item!=null&&!"0".equals(item.getUseflag())){
							map.put("itemtype", item.getItemtype());
							map.put("itemdesc", item.getItemdesc());
							map.put("codesetid", item.getCodesetid());
							if("N".equals(item.getItemtype())){
								map.put("formatlength", item.getDecimalwidth());
							}else if("D".equals(item.getItemtype())){
								map.put("formatlength", item.getItemlength());
							}
							list.add(map);
						}else{
							//如果不存在item，就将该条数据删除掉
							String sqlStr = "DELETE FROM t_sys_table_commonqueryfield WHERE planid = ? AND itemid = ?";
							ArrayList paramList =new ArrayList();
							paramList.add(planid);
							String itemid = rs.getString("itemid");
							paramList.add(itemid);
							dao.delete(sqlStr, paramList);
						}
					}else{
						map.put("itemtype", rs.getString("itemtype"));
						map.put("itemdesc", rs.getString("itemdesc"));
						map.put("codesetid", rs.getString("codesetid"));
						map.put("formatlength", rs.getInt("formatlength"));
						list.add(map);
					}
				}
			}else{
				list = defaultList;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//查询是不是单层级代码，如果是，有数据，如果不是，没有数据
		sql = "select codeitemid,codeitemdesc from codeitem where codesetid=? and not exists(select 1 from codeitem where codesetid=? and parentid<>codeitemid)";
		ArrayList value = new ArrayList();
		try{
			for(int i=0;i<list.size();i++){
				if(!"".equals(planid)&&planid.length()>0){
					map = (HashMap) list.get(i);
				}else{
					DynaBean b = (DynaBean)list.get(i);
					map = PubFunc.DynaBean2Map(b);
				}
				if(map.containsKey("codesetid") && !"0".equals(map.get("codesetid"))){
					value.add(map.get("codesetid"));
					value.add(map.get("codesetid"));
					this.frowset = dao.search(sql, value);
					List items = ExecuteSQL.executePreMyQuery(sql, value, frameconn);
					value.clear();
					if (items.size() > 0) {
						String hirePrivSql = "";
						RecruitPrivBo bo = new RecruitPrivBo();
						HashMap<String, Object> parame = bo.getChannelPrivMap(userView, this.frameconn);
						boolean setFlag = (Boolean) parame.get("setFlag");
						if (setFlag) {
							ArrayList<String> hirePriv = (ArrayList<String>) parame.get("hirePriv");
							 for (int j = items.size()-1; j>=0;j--) {
								 LazyDynaBean rec=new LazyDynaBean();
								 rec= (LazyDynaBean) items.get(j);
								 String codeitemid = (String) rec.get("codeitemid");
								 if(hirePriv.size()>0) {
									 if(!hirePriv.contains(codeitemid)) {
										 items.remove(j);
								     }
								 }else {
									 items.remove(j);
								 }
							 }
						}
						map.put("codeData", items);
					}
				}
				planItems.add(map);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		this.getFormHM().put("planItems", planItems);
	}
}
