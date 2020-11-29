package com.hjsj.hrms.transaction.dtgh.party;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SortPartySubTrans extends IBusiness {

	public void execute() throws GeneralException {
		String type=(String)this.getFormHM().get("type");
		String fieldsetid=(String)this.getFormHM().get("fieldsetid");
		String codeitemid=(String)this.getFormHM().get("codeitemid");
		String i9999=(String)this.getFormHM().get("i9999");
		
		try{
		if(fieldsetid.indexOf("A01")==-1){
			this.doSort(type, fieldsetid, codeitemid, i9999);
		}else{
			String cond_str = "";
			if(this.userView.getHm().containsKey("staff_sql"))
			    cond_str = this.userView.getHm().get("staff_sql").toString();
			else{
				throw new Exception("Please check <userView.getHM()> key:staff_sql");
			}
			//String cond_str=(String)this.getFormHM().get("cond_str");
			//cond_str = com.hrms.frame.codec.SafeCode.decode(cond_str);
			//cond_str=PubFunc.keyWord_reback(cond_str);
			this.doSort(type, fieldsetid, codeitemid, i9999, cond_str);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private synchronized void doSort(String type,String fieldsetid,String codeitemid,String i9999)throws GeneralException {
		if(!(fieldsetid!=null&&fieldsetid.length()>2)){
			return;
		}
		String key = "";
		if(fieldsetid.toUpperCase().startsWith("B")){
			key = "B0110";
		}else if(fieldsetid.toUpperCase().startsWith("K")){
			key = "E01A1";
		}else{
			key =fieldsetid.substring(0,1).toLowerCase()+"0100";
		}
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			if("up".equals(type)){
				int upi9999=9999;
				String sql = "select * from "+fieldsetid+" where "+key+"='"+codeitemid+"' and i9999<"+i9999+" order by i9999 desc";
				this.frecset =dao.search(sql);
				if(this.frecset.next()){
					upi9999=this.frecset.getInt("i9999");
				}else{
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("dtgh.party.sort.up")));
				}
				sql = "update "+fieldsetid+" set i9999=999998 where "+key+"='"+codeitemid+"' and i9999="+upi9999;
				if(dao.update(sql)>0){
					sql = "update "+fieldsetid+" set i9999="+upi9999+" where "+key+"='"+codeitemid+"' and i9999="+i9999;
					if(dao.update(sql)>0){
						sql = "update "+fieldsetid+" set i9999="+i9999+" where "+key+"='"+codeitemid+"' and i9999=999998";
						dao.update(sql);
					}
				}
				
			}else if("down".equals(type)){
				int downi9999=9999;
				String sql = "select * from "+fieldsetid+" where "+key+"='"+codeitemid+"' and i9999>"+i9999+" order by i9999";
				this.frecset =dao.search(sql);
				if(this.frecset.next()){
					downi9999=this.frecset.getInt("i9999");
				}else{
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("dtgh.party.sort.down")));
				}
				sql = "update "+fieldsetid+" set i9999=999997 where "+key+"='"+codeitemid+"' and i9999="+downi9999;
				if(dao.update(sql)>0){
					sql = "update "+fieldsetid+" set i9999="+downi9999+" where "+key+"='"+codeitemid+"' and i9999="+i9999;
					if(dao.update(sql)>0){
						sql = "update "+fieldsetid+" set i9999="+i9999+" where "+key+"='"+codeitemid+"' and i9999=999997";
						dao.update(sql);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	private synchronized void doSort(String type,String fieldsetid,String a0100,String a0000,String cond_str)throws GeneralException {
		if(!(fieldsetid!=null&&fieldsetid.length()>2)){
			return;
		}
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			if("up".equals(type)){
				int upa0000=9999;
				String upa0100 = "";
				String sql = "select a0100,a0000 from "+fieldsetid+" where a0000<"+a0000+" and a0100 in (select "+fieldsetid+".a0100 "+cond_str+") order by a0000 desc";
				this.frecset =dao.search(sql);
				if(this.frecset.next()){
					upa0000=this.frecset.getInt("a0000");
					upa0100=this.frecset.getString("a0100");
				}else{
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("dtgh.party.sort.up")));
				}
				sql = "update "+fieldsetid+" set a0000=? where a0100=?";
				ArrayList values = new ArrayList();
				values.add(new Integer(upa0000));
				values.add(a0100);
				dao.update(sql, values);
				values.clear();
				values.add(new Integer(a0000));
				values.add(upa0100);
				dao.update(sql, values);
			}else if("down".equals(type)){
				int upa0000=9999;
				String upa0100 = "";
				String sql = "select a0100,a0000 from "+fieldsetid+" where a0000>"+a0000+" and a0100 in (select "+fieldsetid+".a0100 "+cond_str+") order by a0000";
				this.frecset =dao.search(sql);
				if(this.frecset.next()){
					upa0000=this.frecset.getInt("a0000");
					upa0100=this.frecset.getString("a0100");
				}else{
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("dtgh.party.sort.down")));
				}
				sql = "update "+fieldsetid+" set a0000=? where a0100=?";
				ArrayList values = new ArrayList();
				values.add(new Integer(upa0000));
				values.add(a0100);
				dao.update(sql, values);
				values.clear();
				values.add(new Integer(a0000));
				values.add(upa0100);
				dao.update(sql, values);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
