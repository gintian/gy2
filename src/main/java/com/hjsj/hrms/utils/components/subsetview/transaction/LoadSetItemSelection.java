package com.hjsj.hrms.utils.components.subsetview.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class LoadSetItemSelection extends IBusiness {

	public void execute() throws GeneralException {
		
//		String query = (String)this.formHM.get("query");
//		
//		String realQ = null;
//		try {
//			realQ = new String(query.getBytes(), "UTF-8");
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		System.out.println(realQ);
//		
//		HashMap ss = new HashMap();
//		ss.put("itemdesc", "sdfsdf");
//		ss.put("itemid", "01");
//		ArrayList ssa = new ArrayList();
//		ssa.add(ss);
//		this.formHM.put("selectionList", ssa);
//		System.out.println("go");		
//		if(ss!=null)
//			return;
//		
//		
		try
		{
			String flag="false";
			String setName=(String)this.formHM.get("setName");
			String personPickerNbase=this.formHM.get("personPickerNbase")==null?"":(String)this.formHM.get("personPickerNbase");
			String cond=this.formHM.get("cond")==null?"":(String)this.formHM.get("cond");
			if("".equals(cond)){
				this.formHM.put("selectionList", new ArrayList());
				return;
			}
//			String cond  = new String(query.getBytes(), "UTF-8");
//			System.out.println(cond);
//			String cond = this.formHM.get("cond")==null?"":(String)this.formHM.get("cond");
			ContentDAO dao = new ContentDAO(this.frameconn);
			ArrayList selectionList = getSelectionList(dao,setName,personPickerNbase,cond);
			this.formHM.put("selectionList", selectionList);
			this.formHM.put("flag", "true");
		}
		catch(Exception e)
		{
			this.formHM.put("flag", "false");
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	
	public ArrayList getSelectionList(ContentDAO dao,String setName,String nbase,String cond) throws SQLException{
		ArrayList returnList=new ArrayList();
		String sql="";
		if(setName.toUpperCase().startsWith("A")){
			String[] nbases=nbase.split(",");
			for(int i = 0;i<nbases.length;i++){
				if(!"".equals(nbases[i])){
					if(!"".equals(sql)){
						sql += " union ";
					}
					sql +=  "select guidkey,'"+nbases[i]+"' nbase,cur itemid,(case when org is null or org = '' then itemid else itemid"+Sql_switcher.concat()+"'('"+Sql_switcher.concat()+"org"+Sql_switcher.concat()+"')' end) itemdesc from ("+
							" select a01.guidkey,'"+nbases[i]+"'"+Sql_switcher.concat()+"A0100 cur,A0101 itemid,(case  "+Sql_switcher.isnull("o1.codeitemdesc", "'null'")+"   when 'null' then '' else o1.codeitemdesc end)"+
							Sql_switcher.concat()+"(case "+Sql_switcher.isnull("o2.codeitemdesc", "'null'")+"  when 'null' then ''  else '/'"+Sql_switcher.concat()+"o2.codeitemdesc end)"+
							Sql_switcher.concat()+"(case "+Sql_switcher.isnull("o3.codeitemdesc", "'null'")+"   when 'null' then ''  else '/'"+Sql_switcher.concat()+"o3.codeitemdesc end) org "+ 
							"from "+nbases[i]+"A01 a01 "+
							"left join organization o1 on a01.B0110=o1.codeitemid "+
							"left join organization o2 on a01.E0122=o2.codeitemid "+
							"left join organization o3 on a01.E01A1=o3.codeitemid ";
					if(!"".equals(cond))
						sql +=  "where A0101 like '%"+cond+"%'";
					sql +=  ") t ";
				}
			}
		}
		/**
		 * TODO 机构下拉选
		 * else if(setName.toUpperCase().startsWith("B")){
		 
			sql="select "+columns+" from "+setName+" where B0110 = ? ";
		}else if(setName.toUpperCase().startsWith("H")){
			sql="select "+columns+" from "+setName+" where H0100 = ? ";
		}
		*/
		if("".equals(sql))
			return returnList;
//		LazyDynaBean bean = null;
		HashMap map=new HashMap();
		this.frowset=dao.search(sql);
		while(this.frowset.next()){
			map = new HashMap();
			String itemid=this.frowset.getString("itemid");
			if("".equals(itemid))
				continue;
			map.put("itemid",PubFunc.encrypt(itemid));
			map.put("itemdesc",this.frowset.getString("itemdesc"));
			map.put("nbase",PubFunc.encrypt(this.frowset.getString("nbase")));
			// vfs改造 上传需要guidkey
			map.put("guidkey",this.frowset.getString("guidkey"));
			returnList.add(map);
		}
		return returnList;
	}
}
