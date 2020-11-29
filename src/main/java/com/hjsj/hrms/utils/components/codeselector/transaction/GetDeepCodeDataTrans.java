package com.hjsj.hrms.utils.components.codeselector.transaction;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.List;

public class GetDeepCodeDataTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String codesetid = (String)this.getFormHM().get("codesetid");
		String level     = (String)this.getFormHM().get("level");
		
		ArrayList dataList = null;
		if("1".equals(level)){
			dataList = getTopCode(codesetid);
		}else{
		    String codeid = (String)this.getFormHM().get("codeid");
		    String sql = "select codeitemid id,codeitemdesc text from codeitem where codesetid='"+codesetid+"' and parentid='"+codeid+"' and parentid<>codeitemid and invalid<>0 order by a0000";
		    dataList = new ArrayList();
			List childList = ExecuteSQL.executeMyQuery(sql);
			for(int i=0;i<childList.size();i++){
				LazyDynaBean ldb = (LazyDynaBean)childList.get(i);
				dataList.add(ldb.getMap());
			}
			
		}
		this.formHM.put("data", dataList);
	}

	private ArrayList getTopCode(String codesetid){
		String data = "";
		ArrayList dataList = new ArrayList();
		try{
			StringBuilder  sql = new StringBuilder();
			sql.append("select codeitemid id,codeitemdesc text from codeitem where codesetid='");
			sql.append(codesetid);
			sql.append("' and parentid=codeitemid and invalid<>0 order by a0000");
			List topList = ExecuteSQL.executeMyQuery(sql.toString());
			for(int i=0;i<topList.size();i++){
				LazyDynaBean ldb = (LazyDynaBean)topList.get(i);
				String codeid = ldb.get("id").toString();
				sql.setLength(0);
				sql.append(" select codeitemid id,codeitemdesc text,");
				sql.append(" (select count(1) from codeitem where codesetid=C.codesetid and parentid=C.codeitemid) childcount ");
				sql.append(" from codeitem C where C.codesetid='");
				sql.append(codesetid);
				sql.append("' and C.parentid='");
				sql.append(codeid);
				sql.append("' and C.parentid<>C.codeitemid and C.invalid<>0 order by C.a0000");
				List twoList = ExecuteSQL.executeMyQuery(sql.toString());
				for(int k=0;k<twoList.size();k++){
					LazyDynaBean t = (LazyDynaBean)twoList.get(k);
					twoList.set(k, t.getMap());
				}
				ldb.set("child", twoList);
				
				dataList.add(ldb.getMap());
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return dataList;
	}
	
}
