/*
 * Created on 2005-12-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.pos.posbusiness;

import com.hjsj.hrms.businessobject.duty.MoveSdutyBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SortBusinessCodeTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String a_code = (String)this.getFormHM().get("a_code");
		String codeitemid = (String)this.getFormHM().get("codeitemid");
		//【4895】库结构-代码体系，调整代码项顺序，报错  jingq upd 2014.11.10
		codeitemid = PubFunc.decrypt(SafeCode.decode(codeitemid));
		int a0000 = Integer.parseInt(PubFunc.decrypt(SafeCode.decode((String)this.getFormHM().get("a0000"))));
		String type = (String)this.getFormHM().get("type");
		
		//岗位体系代码类时itemids有值
		if(this.getFormHM().containsKey("itemids"))
			movePS_C_CODE();
		else
			corcodeMove(a_code,codeitemid,a0000,type);
		
	}
	
	 public void corcodeMove(String a_code,String codeitemid,int a0000,String type){
		try {
			StringBuffer sql = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.frameconn);
			String tcodeitemid="";
			int ta0000=1;
			String sq= "update codeitem set a0000=? where codesetid='"+a_code.substring(0,2)+"' and codeitemid=?";
			ArrayList values=new ArrayList();
			String temp=ResourceFactory.getProperty("pos.posbusiness.nosetposcode");
			if(a_code.length()==2){
				sql.append("select count(codeitemid) c from codeitem where codesetid='"+a_code+"' and parentid=codeitemid group by a0000 having  COUNT(codeitemid)>1");
				this.frowset = dao.search(sql.toString());
				if(this.frowset.next())
					a0000=this.inita0000(a_code, dao,codeitemid);
				sql.setLength(0);
				sql.append("select codeitemid from codeitem where codesetid='"+a_code+"' and (a0000 is null or a0000='') and parentid=codeitemid");
				this.frowset = dao.search(sql.toString());
				if(this.frowset.next())
					a0000=this.inita0000(a_code, dao,codeitemid);
				if("up".equals(type)){
					sql.setLength(0);
					sql.append("select codeitemid,a0000 from codeitem where codesetid='"+a_code+"' and codeitemid=parentid and a0000<"+a0000+" order by a0000 desc");
					this.frowset = dao.search(sql.toString());
					if(this.frowset.next()){
						tcodeitemid=this.frowset.getString("codeitemid");
						ta0000=this.frowset.getInt("a0000");
					}
					ArrayList v= new ArrayList();
					v.add(new Integer(ta0000));
					v.add(codeitemid);
					values.add(v);
					v=new ArrayList();
					v.add(new Integer(a0000));
					v.add(tcodeitemid);
					values.add(v);
					dao.batchUpdate(sq, values);
				}else{
					sql.setLength(0);
					sql.append("select codeitemid,a0000 from codeitem where codesetid='"+a_code+"' and codeitemid=parentid and a0000>"+a0000+" order by a0000");
					this.frowset = dao.search(sql.toString());
					if(this.frowset.next()){
						tcodeitemid=this.frowset.getString("codeitemid");
						ta0000=this.frowset.getInt("a0000");
					}
					ArrayList v= new ArrayList();
					v.add(new Integer(ta0000));
					v.add(codeitemid);
					values.add(v);
					v=new ArrayList();
					v.add(new Integer(a0000));
					v.add(tcodeitemid);
					values.add(v);
					dao.batchUpdate(sq, values);
				}
			}else{
				String codesetid=a_code.substring(0,2);
				String parentid=a_code.substring(2);
				sql.append("select count(codeitemid) c from codeitem where codesetid='"+codesetid+"' and parentid='"+parentid+"' and codeitemid<>parentid group by a0000 having  COUNT(codeitemid)>1");
				this.frowset = dao.search(sql.toString());
				if(this.frowset.next())
					a0000=this.inita0000(a_code, dao,codeitemid);
				sql.setLength(0);
				sql.append("select codeitemid from codeitem where codesetid='"+codesetid+"' and (a0000 is null or a0000='') and parentid='"+parentid+"' and codeitemid<>parentid");
				this.frowset = dao.search(sql.toString());
				if(this.frowset.next())
					a0000=this.inita0000(a_code, dao,codeitemid);
				if("up".equals(type)){
					sql.setLength(0);
					sql.append("select codeitemid,a0000 from codeitem where codesetid='"+codesetid+"' and parentid='"+parentid+"' and codeitemid<>parentid and a0000<"+a0000+" order by a0000 desc");
					this.frowset = dao.search(sql.toString());
					if(this.frowset.next()){
						tcodeitemid=this.frowset.getString("codeitemid");
						ta0000=this.frowset.getInt("a0000");
					}
					ArrayList v= new ArrayList();
					v.add(new Integer(ta0000));
					v.add(codeitemid);
					values.add(v);
					v=new ArrayList();
					v.add(new Integer(a0000));
					v.add(tcodeitemid);
					values.add(v);
					dao.batchUpdate(sq, values);
				}else{
					sql.setLength(0);
					sql.append("select codeitemid,a0000 from codeitem where codesetid='"+codesetid+"' and parentid='"+parentid+"' and codeitemid<>parentid and a0000>"+a0000+" order by a0000");
					this.frowset = dao.search(sql.toString());
					if(this.frowset.next()){
						tcodeitemid=this.frowset.getString("codeitemid");
						ta0000=this.frowset.getInt("a0000");
					}
					ArrayList v= new ArrayList();
					v.add(new Integer(ta0000));
					v.add(codeitemid);
					values.add(v);
					v=new ArrayList();
					v.add(new Integer(a0000));
					v.add(tcodeitemid);
					values.add(v);
					dao.batchUpdate(sq, values);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private int inita0000(String a_code,ContentDAO dao,String codeitemid) throws SQLException{
		StringBuffer sql = new StringBuffer();
		int a0000=1;
		String s= "update codeitem set a0000=? where codesetid='"+a_code.substring(0,2)+"' and codeitemid=?";
		if(a_code.length()==2){
			sql.append("select codeitemid from codeitem where codesetid='"+a_code+"' and parentid=codeitemid order by a0000,codeitemid");
			this.frowset = dao.search(sql.toString());
			ArrayList values = new ArrayList();
			int i=1;
			while(this.frowset.next()){
				ArrayList v= new ArrayList();
				v.add(new Integer(i));
				String t=this.frowset.getString("codeitemid");
				v.add(t);
				if(t.equals(codeitemid)){
					a0000=i;
				}
				values.add(v);
				i++;
			}
			dao.batchUpdate(s, values);
		}else{
			sql.append("select codeitemid from codeitem where codesetid='"+a_code.substring(0,2)+"' and parentid='"+a_code.substring(2)+"' order by a0000,codeitemid");
			this.frowset = dao.search(sql.toString());
			ArrayList values = new ArrayList();
			int i=1;
			while(this.frowset.next()){
				ArrayList v= new ArrayList();
				v.add(new Integer(i));
				String t=this.frowset.getString("codeitemid");
				v.add(t);
				if(t.equals(codeitemid)){
					a0000=i;
				}
				values.add(v);
				i++;
			}
			dao.batchUpdate(s, values);
		}
		return a0000;
	}
	
	public void movePS_C_CODE(){
		try{
			
			 ContentDAO dao=null;
			    String a_code = (String)this.getFormHM().get("a_code");
				String codeitemid = (String)this.getFormHM().get("codeitemid");
				codeitemid = PubFunc.decrypt(SafeCode.decode(codeitemid));
				String movetype = (String)this.getFormHM().get("type");
				String itemids = (String)this.getFormHM().get("itemids");
				String[] list = itemids.split(",");
				dao  = new ContentDAO(this.frameconn);
				MoveSdutyBo msb = new MoveSdutyBo(dao, frowset);
				String sql = " select '1' from codeitem where codesetid='"+a_code.substring(0,2)+"' group by a0000 having count(*)>1";
				this.frowset = dao.search(sql);
				if(this.frowset.next())
					msb.initA0000(a_code.substring(0,2));
				msb.updateA0000(codeitemid,a_code.substring(0,2),list,movetype);
		}catch(Exception e){
			
		}
	}
}
