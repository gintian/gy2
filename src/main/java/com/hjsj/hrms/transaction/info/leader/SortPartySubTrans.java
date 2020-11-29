package com.hjsj.hrms.transaction.info.leader;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SortPartySubTrans extends IBusiness {

	public void execute() throws GeneralException {
		String type=(String)this.getFormHM().get("type");
		String emp_e=(String)this.getFormHM().get("emp_e");
		String order=(String)this.getFormHM().get("order");
		String orderbyfield = (String)this.getFormHM().get("orderbyfield");
		String b0110field = (String)this.getFormHM().get("b0110field");
		String link_field = (String)this.getFormHM().get("link_field");
		String b0110 = (String)this.getFormHM().get("b0110");
		String i9999=(String)this.getFormHM().get("i9999");
		String nbase = (String)this.getFormHM().get("nbase");
		this.doSort(type, emp_e, b0110field,b0110, link_field,i9999,orderbyfield,order,nbase);
	}
	
	private synchronized void doSort(String type,String emp_e,String b0110field,String b0110,String link_field,String i9999,String orderbyfield,String order,String nbase)throws GeneralException {
		if(!(emp_e!=null&&emp_e.length()>2)){
			return;
		}
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			ArrayList dbprelist = this.userView.getPrivDbList();

			FieldItem fi = DataDictionary.getFieldItem(orderbyfield);
			String numstr = "";
			for(int i=0;i<fi.getItemlength()&&i<4;i++){
				numstr+="9";
				
			}

			if("up".equals(type)){
				int upi9999=Integer.parseInt(numstr);
				String updbpre="";
				StringBuffer sql =new StringBuffer();
				for(int i=0;i<dbprelist.size();i++){
					String dbpre = (String)dbprelist.get(i);
					sql.append(" union select '"+dbpre+"' dbpre,"+orderbyfield+" from "+dbpre+emp_e+" where "+b0110field+"='"+b0110+"' and "+link_field+"="+i9999+" and "+orderbyfield+"<"+order);
				}
				this.frecset =dao.search("select dbpre,"+orderbyfield+" from ("+sql.toString().substring(7)+") tt "+" order by "+orderbyfield+" desc");
				if(this.frecset.next()){
					upi9999=this.frecset.getInt(orderbyfield);
					updbpre = this.frecset.getString("dbpre");
				}else{
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("dtgh.party.sort.up")));
				}
				sql.setLength(0);
				sql.append("update "+updbpre+emp_e+" set "+orderbyfield+"="+(Integer.parseInt(numstr)-1)+" where "+b0110field+"='"+b0110+"' and "+link_field+"="+i9999+" and "+orderbyfield+"="+upi9999);
				if(dao.update(sql.toString())>0){
					sql.setLength(0);
					sql.append("update "+nbase+emp_e+" set "+orderbyfield+"="+upi9999+" where "+b0110field+"='"+b0110+"' and "+link_field+"="+i9999+" and "+orderbyfield+"="+order);
					if(dao.update(sql.toString())>0){
						sql.setLength(0);
						sql.append("update "+updbpre+emp_e+" set "+orderbyfield+"="+order+" where "+b0110field+"='"+b0110+"' and "+link_field+"="+i9999+" and "+orderbyfield+"="+(Integer.parseInt(numstr)-1));
						dao.update(sql.toString());
					}
				}
				
			}else if("down".equals(type)){
				int upi9999=Integer.parseInt(numstr);
				String updbpre="";
				StringBuffer sql =new StringBuffer();
				for(int i=0;i<dbprelist.size();i++){
					String dbpre = (String)dbprelist.get(i);
					sql.append(" union select '"+dbpre+"' dbpre,"+orderbyfield+" from "+dbpre+emp_e+" where "+b0110field+"='"+b0110+"' and "+link_field+"="+i9999+" and "+orderbyfield+">"+order);
				}
				this.frecset =dao.search("select dbpre,"+orderbyfield+" from ("+sql.toString().substring(7)+") tt "+" order by "+orderbyfield);
				if(this.frecset.next()){
					upi9999=this.frecset.getInt(orderbyfield);
					updbpre = this.frecset.getString("dbpre");
				}else{
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("dtgh.party.sort.down")));
				}
				sql.setLength(0);
				sql.append("update "+updbpre+emp_e+" set "+orderbyfield+"="+(Integer.parseInt(numstr)-2)+" where "+b0110field+"='"+b0110+"' and "+link_field+"="+i9999+" and "+orderbyfield+"="+upi9999);
				if(dao.update(sql.toString())>0){
					sql.setLength(0);
					sql.append("update "+nbase+emp_e+" set "+orderbyfield+"="+upi9999+" where "+b0110field+"='"+b0110+"' and "+link_field+"="+i9999+" and "+orderbyfield+"="+order);
					if(dao.update(sql.toString())>0){
						sql.setLength(0);
						sql.append("update "+updbpre+emp_e+" set "+orderbyfield+"="+order+" where "+b0110field+"='"+b0110+"' and "+link_field+"="+i9999+" and "+orderbyfield+"="+(Integer.parseInt(numstr)-2));
						dao.update(sql.toString());
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
