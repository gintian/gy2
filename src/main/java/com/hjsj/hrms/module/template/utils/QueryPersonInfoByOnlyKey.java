package com.hjsj.hrms.module.template.utils;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class QueryPersonInfoByOnlyKey extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String ids = (String)this.getFormHM().get("ids");
		String tabid=(String)this.getFormHM().get("tabid");
		ContentDAO dao=new ContentDAO(this.frameconn);
		RowSet rowSet=null;
		HashMap resultMap=new HashMap();
		ArrayList returnList=new ArrayList();
		TemplateTableBo tableBo=new TemplateTableBo(this.frameconn,Integer.parseInt(tabid),this.userView);
		String dbName="";
		String a0100s="";
		String[] idsArray = ids.split("、");
		String onlyname="";
		String valid="";
		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
        onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
        valid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
        String errerMsg="";
		try{
			String sql="";
			ArrayList dbList=DataDictionary.getDbpreList();
			for (String userid:idsArray) {
				sql="";
				String[] userids=userid.split(":");
				if(userids.length!=2){
					errerMsg+=userid;
					continue;
				}
				String username=userids[0];
				String onlyKeyValue=userids[1];
				
		        if ("0".equals(valid)) {
		        	for(int num=0;num<dbList.size();num++){
		        		if(StringUtils.isNotBlank(sql)){
		        			sql+=" UNION  ";
		        		}
		        		sql+="select '"+dbList.get(num)+"' as pre,a0100 from "+dbList.get(num)+"a01 where lower(a0101)=lower('"+username+"') and a0100='"+onlyKeyValue+"'";
		        	}
		        }else{
		        	for(int num=0;num<dbList.size();num++){
		        		if(StringUtils.isNotBlank(sql)){
		        			sql+=" UNION  ";
		        		}
		        		sql+="select '"+dbList.get(num)+"' as pre,a0100 from "+dbList.get(num)+"a01 where lower(a0101)=lower('"+username+"') and "+onlyname+"='"+onlyKeyValue+"'";
		        	}
		        }
		        rowSet=dao.search(sql);
		        HashMap map=new HashMap();
		        while(rowSet.next()){
		        	map.put(rowSet.getString("pre").toLowerCase(), rowSet.getString("a0100"));
		        }
		        if(map.size()==1){
		        	Iterator iterator = map.entrySet().iterator();
		        	while(iterator.hasNext()){
		        		Map.Entry next = (Entry) iterator.next();
		        		String a0100 = (String) next.getValue();
		        		String pre = (String) next.getKey();
		        		returnList.add(PubFunc.encrypt(pre+a0100));
		        	}
		        	
		        }else if(map.size()>1&&map.containsKey(tableBo.getInit_base().toLowerCase())){
		        	returnList.add(PubFunc.encrypt(tableBo.getInit_base()+map.get(tableBo.getInit_base().toLowerCase())));
		        }else if(map.size()>1&&!map.containsKey(tableBo.getInit_base().toLowerCase())){
		        	Iterator iterator = map.entrySet().iterator();
		        	if(iterator.hasNext()){
		        		Map.Entry next = (Entry) iterator.next();
		        		String a0100 = (String) next.getValue();
		        		String pre = (String) next.getKey();
		        		returnList.add(PubFunc.encrypt(pre+a0100));
		        	}
		        }
			}
			resultMap.put("succeed", "true");
			resultMap.put("value", returnList);
			resultMap.put("MSG", errerMsg);
		}catch(Exception ex){
			ex.printStackTrace();
			resultMap.put("succeed", "false");
			resultMap.put("value", "");
			resultMap.put("MSG", "");
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		this.getFormHM().put("resultValue",resultMap);
	}

}
